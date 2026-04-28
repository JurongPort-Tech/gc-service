package sg.com.jp.generalcargo.domain;

/*
 * MessageValueObject.java
 * Created on March 25, 2002, 3:37 PM
 * Date | Remarks | By 
 * 20060516 | Add To CC and Bcc list | GCL
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rudy Sutjiato
 * @version 0.1
 */
public class EmailValueObject implements IMessageValueObject {

	public static final long serialVersionUID = 20080516095211L;

	private String senderAddress;
	private String[] recipientAddress;
	private String[] ccAddress;
	private String[] bccAddress;
	private String message;
	private String subject;
	private Map attachmentList;
	private List toList = new ArrayList();
	private List ccList = new ArrayList();;
	private List bccList = new ArrayList();;

	/** Creates new MessageValueObject */
	public EmailValueObject() {
		attachmentList = new HashMap();
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setRecipientAddress(String[] recipientAddress) {
		this.recipientAddress = recipientAddress;
	}

	public String[] getRecipientAddress() {
		return recipientAddress;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public void addAttachment(String fileName, String filePath) {
		attachmentList.put(fileName, filePath + "/" + fileName);
	}

	public Map getAttachment() {
		return attachmentList;
	}

	public void addToRecipient(String s) {
		if (s != null) {
			this.toList.add(s);
		}
	}

	public void addCcRecipient(String s) {
		if (s != null) {
			this.ccList.add(s);
		}
	}

	public void addBccRecipient(String s) {
		if (s != null) {
			this.bccList.add(s);
		}
	}

	public List getToList() {
		return this.toList;
	}

	public List getCcList() {
		return this.ccList;
	}

	public List getBccList() {
		return this.bccList;
	}

	/**
	 * @return the bccAddress
	 */
	public String[] getBccAddress() {
		return bccAddress;
	}

	/**
	 * @param bccAddress the bccAddress to set
	 */
	public void setBccAddress(String[] bccAddress) {
		this.bccAddress = bccAddress;
	}

	/**
	 * @return the ccAddress
	 */
	public String[] getCcAddress() {
		return ccAddress;
	}

	/**
	 * @param ccAddress the ccAddress to set
	 */
	public void setCcAddress(String[] ccAddress) {
		this.ccAddress = ccAddress;
	}
}
