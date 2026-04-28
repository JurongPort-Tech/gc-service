
package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/*
 * Revision History
 * ------------------------------------------------------------------------------------------------------
 * Author			Description												Version			Date
 * ------------------------------------------------------------------------------------------------------
 * MC Consulting	First Version											1.0				27-Mar-2019
 */

@JsonInclude(Include.NON_NULL)
public class BaseModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String coCode;

	private String userName;

	private String userId;

	public String getCoCode() {
		return coCode;
	}

	public void setCoCode(String coCode) {
		this.coCode = coCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
