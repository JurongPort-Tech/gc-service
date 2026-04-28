package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FTZGroup14VO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private FTZGroup14GIDVO gid = new FTZGroup14GIDVO();
	private List<FTZGroup14HANVO> han = new ArrayList<>(); // optional, max 9
	private List<FTZGroup14FTXVO> ftx = new ArrayList<>(); // max 99
	private List<FTZGroup14MEAVO> mea = new ArrayList<>(); // max 99
	private List<FTZGroup14SGPVO> sgp = new ArrayList<>(); // max 9999
	private List<FTZGroup14DGSVO> dgs = new ArrayList<>(); // conditional, max 9
	private List<FTZGroup14PCIVO> pci = new ArrayList<>(); // conditional, max 9
	private FTZGroup14CSTVO cst = new FTZGroup14CSTVO(); // optional
	

	public FTZGroup14GIDVO getGid() {
		return gid;
	}

	public void setGid(FTZGroup14GIDVO gid) {
		this.gid = gid;
	}

	public List<FTZGroup14HANVO> getHan() {
		return han;
	}

	public void setHan(List<FTZGroup14HANVO> han) {
		this.han = han;
	}

	public List<FTZGroup14FTXVO> getFtx() {
		return ftx;
	}

	public void setFtx(List<FTZGroup14FTXVO> ftx) {
		this.ftx = ftx;
	}

	public List<FTZGroup14MEAVO> getMea() {
		return mea;
	}

	public void setMea(List<FTZGroup14MEAVO> mea) {
		this.mea = mea;
	}

	public List<FTZGroup14SGPVO> getSgp() {
		return sgp;
	}

	public void setSgp(List<FTZGroup14SGPVO> sgp) {
		this.sgp = sgp;
	}

	public List<FTZGroup14DGSVO> getDgs() {
		return dgs;
	}

	public void setDgs(List<FTZGroup14DGSVO> dgs) {
		this.dgs = dgs;
	}

	public List<FTZGroup14PCIVO> getPci() {
		return pci;
	}

	public void setPci(List<FTZGroup14PCIVO> pci) {
		this.pci = pci;
	}

	public FTZGroup14CSTVO getCst() {
		return cst;
	}

	public void setCst(FTZGroup14CSTVO cst) {
		this.cst = cst;
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
