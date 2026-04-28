package sg.com.jp.generalcargo.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;
//import org.springframework.util.StringUtils;
import org.apache.commons.lang.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Safelist;
import org.owasp.esapi.ESAPI;

/**
 * 
 * 【Description】 GenericFilter<br>
 * 【Name of class】 GenericFilter.java <br>
 * 【Creation date, Created】  Mar 12, 2019 FPT <br>
 * 【Update date, Updater, Update Summary】 YYYY/MM/DD FPT No.xxx：NNNNNN
 */

@Component
public class GenericFilter implements Filter {

	private static final Log log = LogFactory.getLog(GenericFilter.class);
	
	public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String CONTENT_TYPE_XML = "application/xml";
	
	@Value ("${genericFilter.showLog:true}")
	private boolean showLog;
	@Value ("${genericFilter.cleanHeaders:true}")
	private boolean cleanHeaders;
	@Value ("${genericFilter.cleanParams:true}")
	private boolean cleanParams;
	@Value ("${genericFilter.cleanAttributes:true}")
	private boolean cleanAttributes;
	@Value ("${genericFilter.cleanBody:true}")
	private boolean cleanBody;
	@Value ("${genericFilter.showIp:false}")
	private boolean showIp;
	@Value ("${genericFilter.copyValue:true}")
	private boolean copyValue;

	@Value ("${genericFilter.header.cnt:0}")
	private int headerCnt;
	@Autowired
	private Environment env;

	private Map<String, String> headers = new HashMap<>();
	
	@Autowired
	private RequestUtil reqUtil;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		CustomRequest request = new CustomRequest((HttpServletRequest) req);
		
		String contentType = request.getHeader("Content-Type");
		if (this.cleanHeaders) {
			this.cleanHeaders(request);
		}
		if (CONTENT_TYPE_FORM.equalsIgnoreCase(contentType)) {
			log.info("form param");
			if (this.cleanParams) {
				this.cleanParams(request);
			}
			if (this.cleanAttributes) {
				this.cleanAttributes(request);
			}
		} else if (CONTENT_TYPE_JSON.equalsIgnoreCase(contentType)) {
			log.info("json param");
			if (this.cleanBody) {
				this.cleanJsonBody(request);
			}
		} else if (CONTENT_TYPE_XML.equalsIgnoreCase(contentType)) {
			log.info("xml param");
			if (this.cleanBody) {
				this.cleanXmlBody(request);
			}
		} else {
			log.info("unknown param");
			if (this.cleanBody) {
				this.cleanJsonBody(request);
			}
			if (this.cleanParams) {
				this.cleanParams(request);
			}
			if (this.cleanAttributes) {
				this.cleanAttributes(request);
			}
		}
		if (this.showIp) {
			this.getIpAddr(request);
		}
		Set<String> keys = this.headers.keySet();
		log.info("setting response header ...");
		for (String key : keys) {
			String value = this.headers.get(key);
			if (key != null && value != null) {
				response.addHeader(key, value);
			}
		}
		
		String auth = request.getHeader("Authorization");
		if (StringUtils.isEmpty(auth)) {
			chain.doFilter(request, response);
			if (showLog) {
				log.info("no authorization header, returning...");
			}
			return;
		}
		if (showLog) {
			log.info("setting param...");
		}
		if (copyValue) {
			this.copyValueFromRequest(request);
		}
		chain.doFilter(request, response);
		
	}

	@Override
	public void destroy() {
		this.reqUtil.clear();
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.reqUtil.init();
		if (showLog) {
			List<RequestCompositeKey> l = this.reqUtil.getKeys();
			for (RequestCompositeKey key : l) {
				log.info(key);
			}
		}
		for (int i=1; i<=this.headerCnt; i++) {
			String key = "genericFilter.header." + i;
			String value = env.getProperty(key);
			if (showLog) {
				log.info(key + "-->" + value);
			}
			if (value != null && !"".equals(value)) {
				int index = value.indexOf(":");
				if (index > -1) {
					String ss[] = value.split(":");
					if (ss != null && ss.length == 2 && ss[0] != null) {
						this.headers.put(ss[0], ss[1]);
					}
				}
			}
		}
		if (showLog) {
			log.info(this.headers);
		}
	}

	public String getIpAddr(HttpServletRequest request) {
		String url = request.getRequestURI();
		if (showLog) {
			log.info("URL: " + url);
		}
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || "".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (showLog) {
			log.info("x-forwarded-for   : " + ip);
		}
		String ip4 = request.getRemoteAddr();
		if (showLog) {
			log.info("remoteAddr        : " + ip4);
		}
		String ip3 = request.getHeader("WL-Proxy-Client-IP");
		if (showLog) {
			log.info("WL-Proxy-Client-IP: " + ip3);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			if (showLog) {
				log.info("Unknown IP");
			}
			ip = ip4;
		}
		request.setAttribute("clientIp", ip);
		return ip;
	}
	
	public void copyValueFromRequest(CustomRequest request) {
		if (!this.reqUtil.isInitialised()) {
			this.reqUtil.init();
		}
		if (this.reqUtil.isInitialised()) {
			this.reqUtil.process(request);
		}
/*
		String auth = request.getHeader("Authorization");
		JSONObject obj = JWTUtils.getUserInfo(auth);
		try {
			if (obj != null) {
				if (obj.has("user_account")) {
					String userAcct = obj.getString("user_account");
					if (userAcct != null) {
						request.putParameter("userAcct", userAcct);
						request.setAttribute("userAcct", userAcct);
					}
				} else if (obj.has("sub")) {
					String userAcct = obj.getString("sub");
					if (userAcct != null) {
						request.putParameter("userAcct", userAcct);
						request.setAttribute("userAcct", userAcct);
					}
				}
				if (obj.has("company_id")) {
					String coyCode = obj.getString("company_id");
					if (coyCode != null) {
						request.putParameter("coyCode", coyCode);
						request.setAttribute("coyCode", coyCode);
					}
				}
				if (obj.has("user_id")) {
					String cardNbr = obj.getString("user_id");
					if (cardNbr != null) {
						request.putParameter("cardNbr", cardNbr);
						request.setAttribute("cardNbr", cardNbr);
					}
				}
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Exception", e);
			}
		}
		String domain = request.getHeader("Domain");
		if (domain != null) {
			request.putParameter("userDomain", domain);
		}
		//*/
	}
	
	/**
	 * Replace the existing headers with ones stripped of XSS vulnerabilities
	 * @param headers
	 */
	private void cleanHeaders(CustomRequest request) {
		Enumeration<String> ee = request.getHeaderNames();
		if (ee.hasMoreElements()) {
			while (ee.hasMoreElements()) {
				String key = ee.nextElement();
				String value = request.getHeader(key);
				//String newValue = this.cleanXssHeader(value);
				String newValue = XssUtil.cleanXss(value);
				if (showLog) {
					log.info(key + ":" + value + "-->" + newValue);
				}
				request.putHeader(key, newValue);
			}
		} else {
			log.info("no header available to cleanse");
		}
	}
	/**
	 * Replace the existing headers with ones stripped of XSS vulnerabilities
	 * @param headers
	 */
	private void cleanParams(CustomRequest request) {
		Enumeration<String> ee = request.getParameterNames();
		if (ee.hasMoreElements()) {
			while (ee.hasMoreElements()) {
				String key = ee.nextElement();
				String value = request.getParameter(key);
				//String newValue = this.cleanXssParam(value);
				String newValue = XssUtil.cleanXss(value);
				if (showLog) {
					log.info(key + ":" + value + "-->" + newValue);
				}
				request.putParameter(key, newValue);
			}
		} else {
			log.info("not a param payload");
		}
	}
	
	/**
	 * Replace the existing headers with ones stripped of XSS vulnerabilities
	 * @param headers
	 */
	private void cleanAttributes(CustomRequest request) {
		Enumeration<String> ee = request.getAttributeNames();
		if (ee.hasMoreElements()) {
			while (ee.hasMoreElements()) {
				String key = ee.nextElement();
				Object value = request.getAttribute(key);
				if (value instanceof String) {
					//String newValue = this.cleanXssParam(value.toString());
					String newValue = XssUtil.cleanXss(value.toString());
					if (showLog) {
						log.info(key + ":" + value + "-->" + newValue);
					}
					request.setAttribute(key, value);
				} else {
					if (showLog) {
						log.info(key + ":" + value + "---ok---");
					}
				}
			}
		} else {
			log.info("not a attribute payload");
		}
	}
	
	public void cleanJsonBody(CustomRequest request) {
		String body = request.getBody();
		if (body.startsWith("{") && body.endsWith("}")) {
			try {
				JSONObject json = new JSONObject(body);
				String[] names = JSONObject.getNames(json);
				for (String key : names) {
					Object o = json.get(key);
					if (o instanceof String) {
						String value = (String)o;
						String newValue = XssUtil.cleanXss(value);
						json.put(key, newValue);
						if (showLog) {
							log.info(key + ":" + value + "--->" + newValue);
						}
					}
				}
				String newBody = json.toString();
				request.setBody(newBody);
				if (showLog) {
					log.info("newBody: " + newBody);
				}
			} catch (Exception e) {
				log.error(e);
			}
		} else {
			log.info("not a JSON payload");
		}
	}
	
	public void cleanXmlBody(CustomRequest request) {
		if (showLog) {
			log.info("NOP");
		}
	}
	
	public static void main(String[] args) {
		String s = "test:haha";
		String[] ss  = s.split(":");
		System.out.println(ss.length);
		System.out.println(ss[0]);
		System.out.println(ss[1]);
	}
}