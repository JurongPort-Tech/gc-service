/*
 * Copyright (C) Jurong Port Pte Ltd. 2018. All rights reserved. Prohibited without permission
 */
package sg.com.jp.generalcargo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 【Description】 configuration for swagger<br>
 * 【Name of class】 SwaggerConfig.java <br>
 * 【Creation date, Creater】 Nov 8, 2018 FPT <br>
 * 【Update date, Updater, Update Summary】<br>
 *  YYYY/MM/DD FPT No.xxx：NNNNNN
 */
@Profile("!test")
@Configuration
@EnableSwagger2
public class SwaggerConfig {
	private static final Log log = LogFactory.getLog(SwaggerConfig.class);
	
	@Bean
	public Docket api() {
		log.info("generating swagger config");
		return new Docket(DocumentationType.SWAGGER_2)
			.select()
			.apis(RequestHandlerSelectors.basePackage("sg.com.jp.generalcargo.controller"))
			.paths(PathSelectors.any())
			.build();
	}
}