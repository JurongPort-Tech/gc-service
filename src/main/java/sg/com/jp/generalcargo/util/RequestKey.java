package sg.com.jp.generalcargo.util;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder=true)
@Component
public class RequestKey {
	
	@Value ("${requestKey.showLog:true}")
	private boolean showLog;
	public static final String TYPE_REQUEST_HEADER = "reqHdr";
	public static final String TYPE_REQUEST_PARAMETER = "reqParam";
	public static final String TYPE_REQUEST_ATTRIBUTE = "reqAttr";
	public static final String TYPE_AUTH_JWT_HEADER = "authJwt";
	public static final String DIR_IN = "in";
	public static final String DIR_OUT = "out";
	
	private String key;
	private String type;
	private String diretion;
	private static final Log log = LogFactory.getLog(RequestKey.class);
	
	public boolean isAuthJwtHeader() {
		if (TYPE_AUTH_JWT_HEADER.equalsIgnoreCase(this.type)) {
			return true;
		}
		return false;
	}
	public boolean isRequestHeader() {
		if (TYPE_REQUEST_HEADER.equalsIgnoreCase(this.type)) {
			return true;
		}
		return false;
	}
	public boolean isRequestParameter() {
		if (TYPE_REQUEST_PARAMETER.equalsIgnoreCase(this.type)) {
			return true;
		}
		return false;
	}
	public boolean isRequestAttribute() {
		if (TYPE_REQUEST_ATTRIBUTE.equalsIgnoreCase(this.type)) {
			return true;
		}
		return false;
	}
	
	public String getValue(CustomRequest request) {
		String retVal = null;
		try {
			if (this.isAuthJwtHeader()) {
				JSONObject jwt = RequestUtil.getAuthJwtToken(request);
				if (jwt != null) {
					retVal = jwt.getString(this.getKey());
				}
			} else if (this.isRequestHeader()) {
				retVal = request.getHeader(this.key);
			} else if (this.isRequestParameter()) {
				retVal = request.getParameter(this.key);
			} else if (this.isRequestAttribute()) {
				retVal = "" + request.getAttribute(this.key);
			}
		} catch (Exception e) {
			if (showLog) {
				log.error(e);
			}
		}
		return retVal;
	}
	public void setValue(CustomRequest request, String value) {
		try {
			if (this.isAuthJwtHeader()) {
				JSONObject jwt = RequestUtil.getAuthJwtToken(request);
				if (jwt != null) {
					if (showLog) {
						log.info(this.key + "--(jwt)-->" + value);
					}
					jwt.put(this.key, value);
				}
			} else if (this.isRequestHeader()) {
				if (showLog) {
					log.info(this.key + "--(hdr)-->" + value);
				}
				request.putHeader(this.key, value);
			} else if (this.isRequestParameter()) {
				if (showLog) {
					log.info(this.key + "--(param)-->" + value);
				}
				request.putParameter(this.key, value);
			} else if (this.isRequestAttribute()) {
				if (showLog) {
					log.info(this.key + "--(attr)-->" + value);
				}
				request.setAttribute(this.key, value);
			}
		} catch (Exception e) {
		}
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("{");
			sb.append(this.key);
			sb.append(",");
			sb.append(this.type);
			sb.append("}");
		} catch (Exception e) {
		}		
		return sb.toString();
	}
	
	public static RequestKey fromString(String s) {
		if (s == null) {
			return null;
		}
		if (!(s.startsWith("{") && s.endsWith("}"))) {
			log.error("Unrecognised format");
			return null;
		}
		RequestKey retVal = null;
		retVal = new RequestKey();
		String ss = s.substring(1, s.length()-1);
		String[] sss = ss.split(",");
		if (sss != null && sss.length == 2) {
			retVal.setKey(sss[0]);
			retVal.setType(sss[1]);
		}
		return retVal;
	}
	
	public static void main(String[] args) {
		RequestKey key = RequestKey.fromString("{haha,hoho}");
		System.out.println(key);
		
		key = RequestKey.fromString("{haha,hoho");
		System.out.println(key);
	}
}

