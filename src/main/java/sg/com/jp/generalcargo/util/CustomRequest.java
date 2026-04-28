/*
 * Copyright (C) Jurong Port Pte Ltd. 2018. All rights reserved. Prohibited without permission
 */
package sg.com.jp.generalcargo.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * 
 * 【Description】 Custom Request<br>
 * 【Name of class】 CustomRequest.java <br>
 * 【Creation date, Creater】 Mar 22, 2019 FPT <br>
 * 【Update date, Updater, Update Summary】 YYYY/MM/DD FPT No.xxx：NNNNNN
 */
public class CustomRequest extends HttpServletRequestWrapper {

	private static final Log log = LogFactory.getLog(CustomRequest.class);

	private HttpServletRequest wrapped;
	private Map<String, String[]> paramMap = new HashMap<>();
	private Map<String, String> headersMap = new HashMap<>();
	private String body = null;
	
	@Value ("${customRequest.showLog:true}")
	private boolean showLog;

	/**
	 * 
	 * 【Function name】<b>Constructor</b><br>
	 * 【Description】 <br>
	 * 【Creation date, Creator】<br>
	 * Mar 22, 2019 FPT <br>
	 * 【Update date, Updater, Update summary】<br>
	 * YYYY/MM/DD FPT No.xxx：NNNNNN<br>
	 * 
	 * @param wrapped
	 */
	public CustomRequest(HttpServletRequest wrapped) {
		super(wrapped);
		this.wrapped = wrapped;

		try {
			this.paramMap.putAll(wrapped.getParameterMap());

			Enumeration<String> ee = wrapped.getHeaderNames();
			while (ee.hasMoreElements()) {
				String key = ee.nextElement();
				String value = wrapped.getHeader(key);
				if (showLog) { 
					log.info(key + ":" + value); 
				}
				this.headersMap.put(key, value);
			}

			this.readBody();
		} catch (Exception e) {
		}
	}

	private void readBody() {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			InputStream is = this.wrapped.getInputStream();
			if (is != null) {
				br = new BufferedReader(new InputStreamReader(is));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = br.read(charBuffer)) > 0) {
					sb.append(charBuffer, 0, bytesRead);
				}
			} else {
				sb.append("");
			}
		} catch (IOException ex) {
			if (showLog) {
				log.error(ex);
			}
		} finally {
			try {
				br.close();
			} catch (IOException ex) {
			}
		}
		this.body = sb.toString();
	}

	/**
	 * 
	 * 【Function name】<b>addParameter</b><br>
	 * 【Description】 Add parameter <br>
	 * 【Creation date, Creator】<br>
	 * Mar 22, 2019 FPT <br>
	 * 【Update date, Updater, Update summary】<br>
	 * YYYY/MM/DD FPT No.xxx：NNNNNN<br>
	 * 
	 * @param name
	 * @param value
	 */
	public void addParameter(String name, String value) {
		if (paramMap == null) {
			paramMap = new HashMap<String, String[]>();
			paramMap.putAll(wrapped.getParameterMap());
		}
		String[] values = paramMap.get(name);
		if (values == null) {
			values = new String[0];
		}
		List<String> list = new ArrayList<String>(values.length + 1);
		list.addAll(Arrays.asList(values));
		list.add(value);
		if (showLog) {
			log.info("putParam: " + name + ":" + list);
		}
		paramMap.put(name, list.toArray(new String[0]));
	}

	public void putParameter(String name, String value) {
		if (paramMap == null) {
			paramMap = new HashMap<String, String[]>();
			paramMap.putAll(wrapped.getParameterMap());
		}
		String[] values = paramMap.get(name);
		if (values == null) {
			values = new String[0];
		}
		List<String> list = new ArrayList<String>();
		list.add(value);
		if (showLog) {
			log.info("putParam: " + name + ":" + list);
		}
		paramMap.put(name, list.toArray(new String[0]));
	}

	@Override
	public String getParameter(String name) {
		if (paramMap == null) {
			return wrapped.getParameter(name);
		}
		String retVal = null;
		String[] ss = paramMap.get(name);
		if (ss != null) {
			retVal = ss[0];
			if (showLog) {
				log.info("getParam: " + name + ":" + retVal);
			}
		}
		if (retVal == null) {
			retVal = wrapped.getParameter(name);
		}
		return retVal;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		if (paramMap == null) {
			return wrapped.getParameterMap();
		}
		return Collections.unmodifiableMap(paramMap);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		if (paramMap == null) {
			return wrapped.getParameterNames();
		}
		return Collections.enumeration(paramMap.keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		if (paramMap == null) {
			return wrapped.getParameterValues(name);
		}
		return paramMap.get(name);
	}

	public void putHeader(String name, String value) {
		if (name != null && value != null) {
			if (showLog) { 
				log.info("putHeader: " + name + ":" + value);
			}
			this.headersMap.remove(name);
			this.headersMap.put(name, value);
		}
	}

	@Override
	public String getHeader(String name) {
		// check the custom headers first
		String retVal = null;
		if (name == null) {
			if (showLog) { 
				log.info("invalid key");
			}
			return null;
		}
		name = name.toLowerCase();
		
		if (this.headersMap.containsKey(name)) {
			retVal = headersMap.get(name);
			if (showLog) { 
				log.info("found: " + name + ":" + retVal);
			}
			return retVal;
		}
		if (showLog) {
			log.info("not found in this header: " + name);
		}
		// else return from into the original wrapped object
		return wrapped.getHeader(name);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		// create a set of the custom header names
		Set<String> set = new HashSet<String>(headersMap.keySet());

		// now add the headers from the wrapped request object
		Enumeration<String> e = wrapped.getHeaderNames();
		while (e.hasMoreElements()) {
			// add the names of the request headers into the list
			String n = e.nextElement();
			set.add(n);
		}

		// create an enumeration from the set and return
		return Collections.enumeration(set);
	}

	public Map<String, String> getHeadersMap() {
		return Collections.unmodifiableMap(this.headersMap);
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
		ServletInputStream sis = new ServletInputStream() {
			@Override
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setReadListener(ReadListener listener) {
			}
		};
		return sis;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(this.getInputStream()));
	}

	// Use this method to read the request body N times
	public String getBody() {
		return this.body;
	}
	public void setBody(String s) {
		this.body = s;
	}
}