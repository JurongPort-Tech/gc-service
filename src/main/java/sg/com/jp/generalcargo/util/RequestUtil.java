package sg.com.jp.generalcargo.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Data
//@AllArgsConstructor
@NoArgsConstructor
//@Builder(toBuilder=true)
@Component
public final class RequestUtil {
	private final List<RequestCompositeKey> keys = new ArrayList<>();
	private static final Log log = LogFactory.getLog(RequestUtil.class);
	
	@Value("${requestUtil.params.count}")
	private int keyCount;
	
	@Value ("${requestUtil.showLog:true}")
	private boolean showLog;

	@Autowired
	private Environment env;
	
	public void init() {
		for (int i=1; i<=keyCount; i++) {
			String key = "requestUtil.param." + i;
			String s = env.getProperty(key);
			RequestCompositeKey rck = RequestCompositeKey.fromString(s);
			if (rck != null && rck.isValid()) {
				this.keys.add(rck);
			}
		}
	}
	
	public void clear() {
		this.keys.clear();
	}
	
	public boolean isInitialised() {
		return this.keys.size() > 0;
	}
	
	public void addKey(RequestCompositeKey key) {
		if (key != null) {
			this.keys.add(key);
		}
	}
	
	public void process(CustomRequest request) {
		if (request == null) {
			return;
		}
		for (RequestCompositeKey rck : this.keys) {
			String value = rck.process(request);
			if (showLog) {
				log.info(rck + ":" + value);
			}
		}
	}
	
	public static JSONObject getAuthJwtToken(CustomRequest request) {
		JSONObject retVal = null;
		try {
			String auth = request.getHeader("Authorization");
			retVal = JWTUtils.getUserInfo(auth);
		} catch (Exception e) {
		}
		return retVal;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			ObjectMapper om = new ObjectMapper();
			String dataAsStr = om.writeValueAsString(this);
			sb.append(dataAsStr);
		} catch (Exception e) {
		}		
		return sb.toString();
	}
}
