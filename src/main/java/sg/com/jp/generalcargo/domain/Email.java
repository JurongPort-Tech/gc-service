package sg.com.jp.generalcargo.domain;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.springframework.stereotype.Component;

@Component
public class Email implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Long id;
	private String from;
	private String fromName;
	private String charSet;
	private String subject;
	private String content;
	private String contentType;

	private List<String> toList = new ArrayList<String>();
	private List<String> ccList = new ArrayList<String>();
	private List<String> bccList = new ArrayList<String>();
	private List<String> attachmentContents = new ArrayList<String>();
	private List<String> attachmentFiles = new ArrayList<String>();
	
	private String createUser;
	private Timestamp createTime = new Timestamp(System.currentTimeMillis());
	private String lastModUser;
	private Timestamp lastModTime = new Timestamp(System.currentTimeMillis());

	private String emailSvcUrl;
	
	
	public String getEmailSvcUrl() {
		return emailSvcUrl;
	}

	public void setEmailSvcUrl(String emailSvcUrl) {
		this.emailSvcUrl = emailSvcUrl;
	}
	
	public Email() {
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

	public String getFromName() {
		return fromName;
	}
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getCharSet() {
		return charSet;
	}
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public InternetAddress getFromAddress() {
		InternetAddress retVal = null;
		try {
			if (this.fromName != null && this.from != null && this.charSet != null) {
				retVal = new InternetAddress(this.from, this.fromName, this.charSet);
			} else if (this.from != null && this.charSet != null) {
				retVal = new InternetAddress(this.from, this.charSet);
			} else if (this.from != null) {
				retVal = new InternetAddress(this.from);
			}
		} catch (Exception e) {
		}
		return retVal;
	}

	
	public List<String> getToList() {
		return toList;
	}
	public void setToList(List<String> toList) {
		this.toList = toList;
	}

	public List<String> getCcList() {
		return ccList;
	}
	public void setCcList(List<String> ccList) {
		this.ccList = ccList;
	}

	public List<String> getBccList() {
		return bccList;
	}
	public void setBccList(List<String> bccList) {
		this.bccList = bccList;
	}

	public List<String> getAttachmentContents() {
		return attachmentContents;
	}
	public void setAttachmentContents(List<String> attachmentContents) {
		this.attachmentContents = attachmentContents;
	}

	public List<String> getAttachmentFiles() {
		return attachmentFiles;
	}
	public void setAttachmentFiles(List<String> attachmentFiles) {
		this.attachmentFiles = attachmentFiles;
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
		sb.append("id:\"").append(this.getId()).append("\",");
		sb.append("from:\"").append(this.getFrom()).append("\",");
		sb.append("fromName:\"").append(this.getFromName()).append("\",");
		sb.append("charSet:\"").append(this.getCharSet()).append("\",");
		sb.append("subject:\"").append(this.getSubject()).append("\",");
		sb.append("content:\"").append(this.getContent()).append("\",");
		sb.append("contentType:\"").append(this.getContentType()).append("\",");
		sb.append("toList:[");
		for (String s : this.toList) {
			sb.append("\"").append(s).append("\",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("]");

		sb.append("ccList:[");
		for (String s : this.ccList) {
			sb.append("\"").append(s).append("\",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("]");

		sb.append("bccList:[");
		for (String s : this.bccList) {
			sb.append("\"").append(s).append("\",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("]");
		
		sb.append("attachmentFiles:[");
		for (String s : this.attachmentFiles) {
			sb.append("\"").append(s).append("\",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("]");

		
		sb.append("createUser:\"").append(this.getCreateUser()).append(",");
		sb.append("createTimeStr:\"").append(this.getCreateTimeStr()).append("\",");
		sb.append("lastModUser:").append(this.getLastModUser()).append(",");
		sb.append("lastModTimeStr:\"").append(this.getLastModTimeStr()).append("\",");
		sb.append("}");
		return sb.toString();
	}
}
