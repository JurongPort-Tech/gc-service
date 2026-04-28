package sg.com.jp.generalcargo.util;

public class BusinessException extends Exception {
	private String[] msg;

	/**
	 * Creates new <code>BusinessException</code> without detail message.
	 */
	public BusinessException() {
		super();
	}

	public BusinessException(String msg) {
		super(msg);
		this.msg = new String[] { msg };
	}

	public BusinessException(String[] msg) {
		this.msg = msg;
	}

	public String[] getMessages() {
		return msg;
	}

	public String getMessage() {
		if (msg != null) {
			return msg[0];
		} else {
			return null;
		}
	}
}
