package sg.com.jp.generalcargo.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Safelist;
import org.owasp.esapi.ESAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XssUtil {
	
	private static final Log log = LogFactory.getLog(XssUtil.class);
	
	public static String cleanXss(String s){
		if (s == null) {
			return null;
		}
		String retVal = s;
		// Use the ESAPI library to avoid encoded attacks.
		retVal = ESAPI.encoder().canonicalize(s);

		// Avoid null characters
		retVal = retVal.replaceAll("\0", "");

		// Clean out HTML
		Document.OutputSettings outputSettings = new Document.OutputSettings();
		outputSettings.escapeMode(EscapeMode.xhtml);
		outputSettings.prettyPrint(false);
		retVal = Jsoup.clean(retVal, "", Safelist.none(), outputSettings);
		
		log.info(s + " ---> " + retVal);
		return retVal;
	}
	
	public static boolean isBodyOk(String s){
		if (s == null) {
			return false;
		}
		boolean b = false;
		try {
			ESAPI.validator().getValidInput("HTTPParameterValue", s, "HTTPParameterValue", 16*1024*1024, false);
			b = true;
		} catch (Exception e) {
			log.error(e);
			b = false;
		}
		return b;
	}
	public boolean isParamOk(String s){
		if (s == null) {
			return false;
		}
		boolean b = false;
		try {
			ESAPI.validator().getValidInput("HTTPParameterValue", s, "HTTPParameterValue", 1024, false);
			b = true;
		} catch (Exception e) {
			log.error(e);
			b = false;
		}
		return b;
	}
	public boolean isHeaderOk(String s){
		if (s == null) {
			return false;
		}
		boolean b = false;
		try {
			ESAPI.validator().getValidInput("HTTPHeaderValue", s, "HTTPHeaderValue", 1024, false);
			b = true;
		} catch (Exception e) {
			log.error(e);
			b = false;
		}
		return b;
	}//*/

}
