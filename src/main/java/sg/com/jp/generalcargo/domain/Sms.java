package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Sms implements Serializable, IMessageValueObject {

	private final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Long id;
	private String from;
	private List<String> toList = new ArrayList<String>();
	private String message;
	private boolean pager = false;
	
	private String createUser;
	private Timestamp createTime = new Timestamp(System.currentTimeMillis());
	private String lastModUser;
	private Timestamp lastModTime = new Timestamp(System.currentTimeMillis());

	public Sms() {
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getToList() {
		return toList;
	}
	public void setToList(List<String> toList) {
		this.toList = toList;
	}
	
	public boolean isPager() {
		return pager;
	}
	public void setPager(boolean b) {
		this.pager = b;
	}
	public String getPagerInd() {
		String retVal = "N";
		if (this.pager) {
			retVal = "Y";
		}
		return retVal;
	}
	public void setPagerInd(String s) {
		if ("Y".equalsIgnoreCase(s)) {
			this.pager = true;
		} else {
			this.pager = false;
		}
	}
	

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}
	public String getCreateTimeStr(){
		String retVal = "";
		try {
			retVal = DF.format(this.createTime);
		} catch (Exception e) {
		}
		return retVal;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public void setCreateTimeStr(String s) {
		try {
			this.createTime = new Timestamp (DF.parse(s).getTime());
		} catch (Exception e) {
		}
	}

	public String getLastModUser() {
		return lastModUser;
	}

	public void setLastModUser(String lastModUser) {
		this.lastModUser = lastModUser;
	}

	public Timestamp getLastModTime() {
		return lastModTime;
	}
	public String getLastModTimeStr(){
		String retVal = "";
		try {
			retVal = DF.format(this.lastModTime);
		} catch (Exception e) {
		}
		return retVal;
	}

	public void setLastModTime(Timestamp lastModTime) {
		this.lastModTime = lastModTime;
	}
	public void setLastModTimeStr(String s) {
		try {
			this.lastModTime = new Timestamp (DF.parse(s).getTime());
		} catch (Exception e) {
		}
	}


	public String toString() {
		// it is good practice to log the values of the domain object
		// JSON format is chosen
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("id:").append(this.getId()).append(",");
		sb.append("from:\"").append(this.getFrom()).append("\",");
		sb.append("message:\"").append(this.getMessage()).append("\",");
		sb.append("pagerInd:\"").append(this.getPagerInd()).append("\",");
		sb.append("toList:[");
		for (String s : this.toList) {
			sb.append("\"").append(s).append("\",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("]");

		sb.append("createUser:").append(this.getCreateUser()).append(",");
		sb.append("createTimeStr:\"").append(this.getCreateTimeStr()).append("\",");
		sb.append("lastModUser:").append(this.getLastModUser()).append(",");
		sb.append("lastModTimeStr:\"").append(this.getLastModTimeStr()).append("\",");
		sb.append("}");
		return sb.toString();
	}
}
