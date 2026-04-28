package sg.com.jp.generalcargo.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import sg.com.jp.generalcargo.config.LoggingConfig;

@Component
public class LogWebRequestInterceptor implements HandlerInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogWebRequestInterceptor.class);
	
	@Autowired
	private LoggingConfig logCfg;
	
	public LogWebRequestInterceptor(LoggingConfig logCfg) {
		MDC.put("service", logCfg.getService());
		MDC.put("version", logCfg.getVersion());
		LOGGER.info("Initialise LogWebRequestInterceptor");

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		MDC.put("service", logCfg.getService());
		MDC.put("version", logCfg.getVersion());
		MDC.put("session-id", request.getHeader("sessionID"));
		MDC.put("activity-id", request.getHeader("activityID"));
		MDC.put("user-account", request.getHeader("user-account"));
		MDC.put("x-forwarded-for", request.getHeader("x-forwarded-for"));
		if(handler instanceof HandlerMethod) {
			HandlerMethod method = (HandlerMethod) handler;
			MDC.put("controller", method.getBeanType().getSimpleName());
			MDC.put("method", method.getMethod().getName());
		}
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// Nothing
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// MDC.clear();
	}
}
