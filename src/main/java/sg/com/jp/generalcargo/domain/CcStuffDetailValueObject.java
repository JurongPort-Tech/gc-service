package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CcStuffDetailValueObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 259538110012933846L;
	private int detseq;
	private String detseqString;
	private int seqno;
	private String edonos;
	private int edopkgs;
	private String userid;
	private String esnnos;
	private int esnpkgs;
	
	

	public String getDetseqString() {
		return detseqString;
	}

	public void setDetseqString(String detseqString) {
		this.detseqString = detseqString;
	}

	public int getDetseq() {
		return detseq;
	}

	public void setDetseq(int detseq) {
		this.detseq = detseq;
	}

	public int getSeqno() {
		return seqno;
	}

	public void setSeqno(int seqno) {
		this.seqno = seqno;
	}

	public String getEdonos() {
		return edonos;
	}

	public void setEdonos(String edonos) {
		this.edonos = edonos;
	}

	public int getEdopkgs() {
		return edopkgs;
	}

	public void setEdopkgs(int edopkgs) {
		this.edopkgs = edopkgs;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getEsnnos() {
		return esnnos;
	}

	public void setEsnnos(String esnnos) {
		this.esnnos = esnnos;
	}

	public int getEsnpkgs() {
		return esnpkgs;
	}

	public void setEsnpkgs(int esnpkgs) {
		this.esnpkgs = esnpkgs;
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
