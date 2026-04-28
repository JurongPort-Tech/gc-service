package sg.com.jp.generalcargo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sg.com.jp.generalcargo.util.LogWebRequestInterceptor;

@Configuration
@ComponentScan(basePackages = { "sg.com.jp.generalcargo.controller" })
public class InterceptorAppConfig implements WebMvcConfigurer {

	@Autowired
	LogWebRequestInterceptor logWebRequestInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(logWebRequestInterceptor);
		WebMvcConfigurer.super.addInterceptors(registry);
	}
}