package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReExportValueObject  implements TopsIObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private java.lang.String terminal;
	private java.lang.String scheme;
	private java.lang.String subScheme;
	private java.lang.String gcOperations;
	private java.lang.String vslName;
	private java.lang.String voyNo;
	private java.lang.String varNbr;

	private java.lang.String blNo;
	private java.lang.String crgDesc;
	private java.lang.String noofPkgs;
	private java.lang.String grWt;
	private java.lang.String grMsmt;
	private java.lang.String crgStatus;
	private java.lang.String edostat;
	private java.lang.String seqNo;
	private java.lang.String edoNbrPkgs;

	private java.lang.String reExpNbr;
	private java.lang.String portL;
	private java.lang.String portLn;

	public ReExportValueObject()
	{
	}
	
	public java.lang.String getTerminal() {
		return terminal;
	}

	public void setTerminal(java.lang.String terminal) {
		this.terminal = terminal;
	}

	public java.lang.String getScheme() {
		return scheme;
	}

	public void setScheme(java.lang.String scheme) {
		this.scheme = scheme;
	}

	public java.lang.String getSubScheme() {
		return subScheme;
	}

	public void setSubScheme(java.lang.String subScheme) {
		this.subScheme = subScheme;
	}

	public java.lang.String getGcOperations() {
		return gcOperations;
	}

	public void setGcOperations(java.lang.String gcOperations) {
		this.gcOperations = gcOperations;
	}

	public void setVslName(String s)
	{
		vslName = s;
	}

	public String getVslName()
	{
		return vslName;
	}
	public void setVoyNo(String s)
	{
		voyNo = s;
	}

	public String getVoyNo()
	{
		return voyNo;
	}
	public void setVarNbr(String s)
	{
		varNbr = s;
	}

	public String getVarNbr()
	{
		return varNbr;
	}

	public void setBlNo(String s)
	{
		blNo = s;
	}

	public String getBlNo()
	{
		return blNo;
	}
	public void setCrgDesc(String s)
	{
		crgDesc = s;
	}
	public String getCrgDesc()
	{
		return crgDesc;
	}
	public void setNoofPkgs(String s)
	{
		noofPkgs = s;
	}

	public String getNoofPkgs()
	{
		return noofPkgs;
	}
	public void setGrWt(String s)
	{
		grWt = s;
	}

	public String getGrWt()
	{
		return grWt;
	}
	public void setGrMsmt(String s)
	{
		grMsmt = s;
	}

	public String getGrMsmt()
	{
		return grMsmt;
	}
	public void setCrgStatus(String s)
	{
		crgStatus = s;
	}

	public String getCrgStatus()
	{
		return crgStatus;
	}
	public void setEdostat(String s)
	{
		edostat = s;
	}

	public String getEdostat()
	{
		return edostat;
	}
	
	public void setSeqNo(String s)
	{
		seqNo = s;
	}

	public String getSeqNo()
	{
		return seqNo;
	}

	public void setReExpNbr(String s)
	{
		reExpNbr = s;
	}

	public String getReExpNbr()
	{
		return reExpNbr;
	}

	public void setEdoNbrPkgs(String s)
	{
		edoNbrPkgs = s;
	}

	public String getEdoNbrPkgs()
	{
		return edoNbrPkgs;
	}
	public void setPortL(String s)
	{
		portL = s;
	}

	public String getPortL()
	{
		return portL;
	}
	public void setPortLn(String s)
	{
		portLn = s;
	}

	public String getPortLn()
	{
		return portLn;
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
