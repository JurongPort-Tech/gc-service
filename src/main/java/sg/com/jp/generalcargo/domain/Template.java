package sg.com.jp.generalcargo.domain;

public class Template {
	private String refId;
	private String refType;
	private String fileName;
	private boolean isSplitBL;

	public String getRefType() {
		return refType;
	}

	public void setRefType(String refType) {
		this.refType = refType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public boolean getIsSplitBL() {
		return isSplitBL;
	}

	public void setIsSplitBL(boolean b) {
		this.isSplitBL = b;
	}
	
}
