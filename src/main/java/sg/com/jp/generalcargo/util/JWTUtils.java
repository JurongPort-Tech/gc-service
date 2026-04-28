package sg.com.jp.generalcargo.util;

import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;


public class JWTUtils {

	private static final Log log = LogFactory.getLog(JWTUtils.class);

	public static JSONObject getUserInfo(String jwt) {
		String[] split_string = jwt.split("\\.");
		if (split_string.length > 1) {
			String base64EncodedBody = split_string[1];
			Base64 base64Url = new Base64(true);
			String body = new String(base64Url.decode(base64EncodedBody), Charset.defaultCharset());
			try {
				JSONObject jsonObject = new JSONObject(body);
				return jsonObject;
			} catch (Exception e) {
				log.error("Exception", e);
			}
		}
		return null;
	}
}
