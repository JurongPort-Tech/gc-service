package sg.com.jp.generalcargo.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class RequestUtils {
	private static final IntParser INT_PARSER = new IntParser();
	private static final LongParser LONG_PARSER = new LongParser();
	private static final FloatParser FLOAT_PARSER = new FloatParser();
	private static final DoubleParser DOUBLE_PARSER = new DoubleParser();
	private static final BooleanParser BOOLEAN_PARSER = new BooleanParser();
	private static final StringParser STRING_PARSER = new StringParser();

	public static Integer getIntParameter(ServletRequest request, String name) {
		if (request.getParameter(name) == null) {
			return null;
		}
		return new Integer(getRequiredIntParameter(request, name));
	}

	public static int getIntParameter(ServletRequest request, String name, int defaultVal) {
		try {
			return getRequiredIntParameter(request, name);
		} catch (Exception ex) {
		}
		return defaultVal;
	}

	public static int[] getIntParameters(ServletRequest request, String name) {
		try {
			return getRequiredIntParameters(request, name);
		} catch (Exception ex) {
		}
		return new int[0];
	}

	public static int getRequiredIntParameter(ServletRequest request, String name) {
		return INT_PARSER.parseInt(name, request.getParameter(name));
	}

	public static int[] getRequiredIntParameters(ServletRequest request, String name) {
		return INT_PARSER.parseInts(name, request.getParameterValues(name));
	}

	public static Long getLongParameter(ServletRequest request, String name) {
		if (request.getParameter(name) == null) {
			return null;
		}
		return getRequiredLongParameter(request, name);
	}

	public static Long getLongParameter(ServletRequest request, String name, Long defaultVal) {
		try {
			return getRequiredLongParameter(request, name);
		} catch (Exception ex) {
		}
		return defaultVal;
	}

	public static Long[] getLongParameters(ServletRequest request, String name) {
		try {
			return getRequiredLongParameters(request, name);
		} catch (Exception ex) {
		}
		return new Long[0];
	}

	public static Long getRequiredLongParameter(ServletRequest request, String name) {
		return LONG_PARSER.parseLong(name, request.getParameter(name));
	}

	public static Long[] getRequiredLongParameters(ServletRequest request, String name) {
		return LONG_PARSER.parseLongs(name, request.getParameterValues(name));
	}

	public static Float getFloatParameter(ServletRequest request, String name) {
		if (request.getParameter(name) == null) {
			return null;
		}
		return new Float(getRequiredFloatParameter(request, name));
	}

	public static float getFloatParameter(ServletRequest request, String name, float defaultVal) {
		try {
			return getRequiredFloatParameter(request, name);
		} catch (Exception ex) {
		}
		return defaultVal;
	}

	public static float[] getFloatParameters(ServletRequest request, String name) {
		try {
			return getRequiredFloatParameters(request, name);
		} catch (Exception ex) {
		}
		return new float[0];
	}

	public static float getRequiredFloatParameter(ServletRequest request, String name) {
		return FLOAT_PARSER.parseFloat(name, request.getParameter(name));
	}

	public static float[] getRequiredFloatParameters(ServletRequest request, String name) {
		return FLOAT_PARSER.parseFloats(name, request.getParameterValues(name));
	}

	public static Double getDoubleParameter(ServletRequest request, String name) {
		if (request.getParameter(name) == null) {
			return null;
		}
		return new Double(getRequiredDoubleParameter(request, name));
	}

	public static double getDoubleParameter(ServletRequest request, String name, double defaultVal) {
		try {
			return getRequiredDoubleParameter(request, name);
		} catch (Exception ex) {
		}
		return defaultVal;
	}

	public static double[] getDoubleParameters(ServletRequest request, String name) {
		try {
			return getRequiredDoubleParameters(request, name);
		} catch (Exception ex) {
		}
		return new double[0];
	}

	public static double getRequiredDoubleParameter(ServletRequest request, String name) {
		return DOUBLE_PARSER.parseDouble(name, request.getParameter(name));
	}

	public static double[] getRequiredDoubleParameters(ServletRequest request, String name) {
		return DOUBLE_PARSER.parseDoubles(name, request.getParameterValues(name));
	}

	public static Boolean getBooleanParameter(ServletRequest request, String name) {
		if (request.getParameter(name) == null) {
			return null;
		}
		return getRequiredBooleanParameter(request, name) ? Boolean.TRUE : Boolean.FALSE;
	}

	public static boolean getBooleanParameter(ServletRequest request, String name, boolean defaultVal) {
		try {
			return getRequiredBooleanParameter(request, name);
		} catch (Exception ex) {
		}
		return defaultVal;
	}

	public static boolean[] getBooleanParameters(ServletRequest request, String name) {
		try {
			return getRequiredBooleanParameters(request, name);
		} catch (Exception ex) {
		}
		return new boolean[0];
	}

	public static boolean getRequiredBooleanParameter(ServletRequest request, String name) {
		return BOOLEAN_PARSER.parseBoolean(name, request.getParameter(name));
	}

	public static boolean[] getRequiredBooleanParameters(ServletRequest request, String name) {
		return BOOLEAN_PARSER.parseBooleans(name, request.getParameterValues(name));
	}

	public static String getStringParameter(ServletRequest request, String name) {
		if (request.getParameter(name) == null) {
			return "";
		}
		return getRequiredStringParameter(request, name);
	}

	public static String getStringParameter(ServletRequest request, String name, String defaultVal) {
		String result = getRequiredStringParameter(request, name);
		if (StringUtils.isEmpty(result)) {
			return defaultVal;
		}
		return result;
	}

	public static String[] getStringParameters(ServletRequest request, String name) {
		try {
			return getRequiredStringParameters(request, name);
		} catch (Exception ex) {
		}
		return new String[0];
	}

	public static String getRequiredStringParameter(ServletRequest request, String name) {
		return STRING_PARSER.validateRequiredString(name, request.getParameter(name));
	}

	public static String[] getRequiredStringParameters(ServletRequest request, String name) {
		return STRING_PARSER.validateRequiredStrings(name, request.getParameterValues(name));
	}

	public static boolean isMultipart(HttpServletRequest request) {
		if (!"POST".equalsIgnoreCase(request.getMethod())) {
			return false;
		}

		String contentType = request.getContentType();

		return (contentType != null) && (contentType.startsWith("multipart/form-data"));
	}

	public static String getProtocolName(HttpServletRequest request) {
		String s = request.getProtocol().toLowerCase();
		return s.substring(0, s.indexOf("/")).toLowerCase();
	}

	public static String concatURL(String path1, String path2) {
		StringBuffer sb = new StringBuffer();
		sb.append(path1);
		if (!path1.endsWith("/")) {
			sb.append("/");
		}
		if (path2.startsWith("/")) {
			sb.append(path2.substring(1));
		}
		return sb.toString();
	}

	public static String mergeURL(HttpServletRequest request, String url) {
		String host = getProtocolName(request) + "://" + request.getServerName() + ":" + request.getServerPort();
		String context = concatURL(host, request.getContextPath());
		return concatURL(context, url);
	}

	public static String appendParameters(String url, Map params) {
		StringBuffer remoteURL = new StringBuffer();
		remoteURL.append(url);
		if (params.isEmpty()) {
			return remoteURL.toString();
		}

		if (url.indexOf("?") < 0) {
			remoteURL.append("?");
		}
		if (url.indexOf("?") > 0) {
			remoteURL.append("&");
		}
		boolean first = true;
		for (Iterator iterator = params.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			Object value = params.get(key);
			if (value != null) {
				if (!first)
					remoteURL.append("&");
				else {
					first = false;
				}
				if (value.getClass().isArray())
					remoteURL.append(key).append("=").append(StringUtils.join((String[]) (String[]) value, ","));
				else {
					remoteURL.append(key).append("=").append(value.toString());
				}
			}
		}

		return remoteURL.toString();
	}

	public static String getOriginalURL(HttpServletRequest req) {
		String scheme = getProtocolName(req);
		String serverName = req.getServerName();
		int serverPort = req.getServerPort();
		String contextPath = req.getContextPath();
		String servletPath = req.getServletPath();
		String pathInfo = req.getPathInfo();

		if (StringUtils.isEmpty(contextPath)) {
			contextPath = "";
		}
		if (StringUtils.isEmpty(servletPath)) {
			servletPath = "";
		}
		if (StringUtils.isEmpty(pathInfo)) {
			pathInfo = "";
		}

		String url = scheme + "://" + serverName + ":" + serverPort + contextPath + servletPath;
		if (pathInfo != null) {
			url = url + pathInfo;
		}

		Map params = new HashMap();
		params.putAll(req.getParameterMap());
		params.remove("_TOKEN_");

		return appendParameters(url, params);
	}

	public static String getOriginalServerURL(HttpServletRequest request) {
		String originalURL = getOriginalURL(request);
		int index = originalURL.indexOf("/", "http://".length() + 1);
		String originalServer = originalURL.substring(0, index);
		return originalServer;
	}

	public static Object getBean(HttpServletRequest request, Object obj) {
		return obj;
//need to implementing
//		try {
//			BeanUtils.populate(obj, request.getParameterMap());
//			return obj;
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
//		throw new RuntimeException("Can not get bean from Http Request.");
	}

//	static {
//		MyDateConverter d = new MyDateConverter();
//		String[] datePattern = { "yyyy-MM-dd", "yyyy/MM/dd", "dd/MM/yyyy" };
//		d.setPatterns(datePattern);
//		ConvertUtils.register(d, Date.class);
//	}

	private static class StringParser extends RequestUtils.ParameterParser {
		private StringParser() {
			super();
		}

		protected String getType() {
			return "string";
		}

		protected Object doParse(String parameter) throws NumberFormatException {
			return parameter;
		}

		public String validateRequiredString(String name, String value) {
			validateRequiredParameter(name, value);
			return value;
		}

		public String[] validateRequiredStrings(String name, String[] values) {
			validateRequiredParameter(name, values);
			for (int i = 0; i < values.length; i++) {
				validateRequiredParameter(name, values[i]);
			}
			return values;
		}
	}

	private static class BooleanParser extends RequestUtils.ParameterParser {
		private BooleanParser() {
			super();
		}

		protected String getType() {
			return "boolean";
		}

		protected Object doParse(String parameter) throws NumberFormatException {
			return (parameter.equalsIgnoreCase("true")) || (parameter.equalsIgnoreCase("on"))
					|| (parameter.equalsIgnoreCase("yes")) || (parameter.equals("1")) ? Boolean.TRUE : Boolean.FALSE;
		}

		public boolean parseBoolean(String name, String parameter) {
			return ((Boolean) parse(name, parameter)).booleanValue();
		}

		public boolean[] parseBooleans(String name, String[] values) {
			validateRequiredParameter(name, values);
			boolean[] parameters = new boolean[values.length];
			for (int i = 0; i < values.length; i++) {
				parameters[i] = parseBoolean(name, values[i]);
			}
			return parameters;
		}
	}

	private static class DoubleParser extends RequestUtils.ParameterParser {
		private DoubleParser() {
			super();
		}

		protected String getType() {
			return "double";
		}

		protected Object doParse(String parameter) throws NumberFormatException {
			return Double.valueOf(parameter);
		}

		public double parseDouble(String name, String parameter) {
			return ((Number) parse(name, parameter)).doubleValue();
		}

		public double[] parseDoubles(String name, String[] values) {
			validateRequiredParameter(name, values);
			double[] parameters = new double[values.length];
			for (int i = 0; i < values.length; i++) {
				parameters[i] = parseDouble(name, values[i]);
			}
			return parameters;
		}
	}

	private static class FloatParser extends RequestUtils.ParameterParser {
		private FloatParser() {
			super();
		}

		protected String getType() {
			return "float";
		}

		protected Object doParse(String parameter) throws NumberFormatException {
			return Float.valueOf(parameter);
		}

		public float parseFloat(String name, String parameter) {
			return ((Number) parse(name, parameter)).floatValue();
		}

		public float[] parseFloats(String name, String[] values) {
			validateRequiredParameter(name, values);
			float[] parameters = new float[values.length];
			for (int i = 0; i < values.length; i++) {
				parameters[i] = parseFloat(name, values[i]);
			}
			return parameters;
		}
	}

	private static class LongParser extends RequestUtils.ParameterParser {
		private LongParser() {
			super();
		}

		protected String getType() {
			return "long";
		}

		protected Object doParse(String parameter) throws NumberFormatException {
			return Long.valueOf(parameter);
		}

		public Long parseLong(String name, String parameter) {
			return new Long(((Number) parse(name, parameter)).longValue());
		}

		public Long[] parseLongs(String name, String[] values) {
			validateRequiredParameter(name, values);
			Long[] parameters = new Long[values.length];
			for (int i = 0; i < values.length; i++) {
				parameters[i] = parseLong(name, values[i]);
			}
			return parameters;
		}
	}

	private static class IntParser extends RequestUtils.ParameterParser {
		private IntParser() {
			super();
		}

		protected String getType() {
			return "int";
		}

		protected Object doParse(String s) throws NumberFormatException {
			return Integer.valueOf(s);
		}

		public int parseInt(String name, String parameter) {
			return ((Number) parse(name, parameter)).intValue();
		}

		public int[] parseInts(String name, String[] values) {
			validateRequiredParameter(name, values);
			int[] parameters = new int[values.length];
			for (int i = 0; i < values.length; i++) {
				parameters[i] = parseInt(name, values[i]);
			}
			return parameters;
		}
	}

	private static abstract class ParameterParser {
		protected final Object parse(String name, String parameter) {
			validateRequiredParameter(name, parameter);
			try {
				return doParse(parameter);
			} catch (NumberFormatException ex) {
			}
			throw new IllegalArgumentException("Required " + getType() + " parameter '" + name + "' with value of '"
					+ parameter + "' is not a valid number");
		}

		protected final void validateRequiredParameter(String name, Object parameter) {
		}

		protected abstract String getType();

		protected abstract Object doParse(String paramString) throws NumberFormatException;
	}
}
