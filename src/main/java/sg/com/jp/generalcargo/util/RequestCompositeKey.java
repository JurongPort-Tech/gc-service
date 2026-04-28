package sg.com.jp.generalcargo.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Data
//@AllArgsConstructor
@NoArgsConstructor
//@Builder(toBuilder=true)
@Component
public class RequestCompositeKey {
	
	@Value ("${requestCompositeKey.showLog:true}")
	private boolean showLog;

	private final List<RequestKey>in = new ArrayList<>();
	private final List<RequestKey>out = new ArrayList<>();
	private static final Log log = LogFactory.getLog(RequestCompositeKey.class);
	
	public void clear() {
		this.in.clear();
		this.out.clear();
	}
	
	public boolean isValid() {
		return this.in.size() > 0 && this.out.size() > 0;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{[");
		int index = 0;
		for (RequestKey rk : in) {
			if (index++ > 0) {
				sb.append(",");
			}
			sb.append(rk.toString());
		}
		sb.append("],[");
		index = 0;
		for (RequestKey rk : out) {
			if (index++ > 0) {
				sb.append(",");
			}
			sb.append(rk.toString());
		}
		sb.append("]}");
		
		return sb.toString();
	}
	
	public static RequestCompositeKey fromString(String s) {
		if (!(s.startsWith("{") && s.endsWith("}"))) {
			log.error("Unrecognised format 1 ");
			return null;
		}
		String ss = s.substring(1, s.length()-1);
		if (!(ss.startsWith("[") && ss.endsWith("]"))) {
			log.error("Unrecognised format 2");
			return null;
		}
		String[] sss = ss.split("\\],\\[");
		if (sss == null || sss.length != 2) {
			log.error("Unrecognised format 3");
			return null;
		}
		RequestCompositeKey rck = new RequestCompositeKey();
		// in and out list base strings
		sss[0] = sss[0].substring(1, sss[0].length());
		sss[1] = sss[1].substring(0, sss[1].length()-1);

		// in key
		String[] ssss = sss[0].split("\\},\\{");
		//log.info(ssss.length);
		if (ssss.length == 1) {
			String s2 = ssss[0];//.substring(1, ssss[0].length() - 1);
			//log.info(s2);
			RequestKey key = RequestKey.fromString(s2);
			if (key != null) {
				rck.getIn().add(key);
			}
		} else if (ssss.length > 1) {
			int i = 0;
			for (i=0; i<ssss.length; i++) {
				String s2 = ssss[i];
				if (i == 0 ) {
					s2 = s2 + "}";
				} else if (i == ssss.length - 1) {
					s2 = "{" + s2;
				} else {
					s2 = "{" + s2 + "}";
				}
				//log.info(s2);
				RequestKey key = RequestKey.fromString(s2);
				if (key != null) {
					rck.getIn().add(key);
				}
			}
		}
		// out keys
		ssss = sss[1].split("\\},\\{");
		//log.info(ssss.length);
		if (ssss.length == 1) {
			String s2 = ssss[0];//.substring(1, ssss[0].length() - 1);
			//log.info(s2);
			RequestKey key = RequestKey.fromString(s2);
			if (key != null) {
				rck.getOut().add(key);
			}
		} else if (ssss.length > 1) {
			int i = 0;
			for (i=0; i<ssss.length; i++) {
				String s2 = ssss[i];
				if (i == 0 ) {
					s2 = s2 + "}";
				} else if (i == ssss.length - 1) {
					s2 = "{" + s2;
				} else {
					s2 = "{" + s2 + "}";
				}
				//log.info(s2);
				RequestKey key = RequestKey.fromString(s2);
				if (key != null) {
					rck.getOut().add(key);
				}
			}
		}
		// out keys
		
		
		return rck;
	}

	public String process(CustomRequest request) {
		String retVal = null;
		for (RequestKey rk : in) {
			retVal = rk.getValue(request);
			if (retVal != null) {
				break;
			}
		}
		if (retVal != null) {
			for (RequestKey rk: out) {
				rk.setValue(request, retVal);
			}
		}		
		return retVal;
	}
	/*
	public static void main(String[] args) {
		RequestCompositeKey rck = new RequestCompositeKey();
		rck.getIn().add(RequestKey.fromString("{user_account,authJwt}"));
		rck.getIn().add(RequestKey.fromString("{sub,authJwt}"));
		rck.getIn().add(RequestKey.fromString("{acct,authJwt}"));
		rck.getIn().add(RequestKey.fromString("{uas,authJwt}"));
		rck.getOut().add(RequestKey.fromString("{userAcct,reqParam}"));
		rck.getOut().add(RequestKey.fromString("{userAcct,reqAttr}"));
		rck.getOut().add(RequestKey.fromString("{userAcct,reqHdr}"));
		//rck.getOut().add(RequestKey.fromString("{userAcct,authJwt}"));
		System.out.println(RequestCompositeKey.fromString(rck.toString()));
	}//*/
}

