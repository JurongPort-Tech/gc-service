/*
 * Copyright (C) Jurong Port Pte Ltd. 2018. All rights reserved. Prohibited without permission
 */
package sg.com.jp.generalcargo.config;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

/**
 * 【Description】 configuration for healthcheck<br>
 * 【Name of class】 HealthcheckConfig.java <br>
 * 【Creation date, Creater】 Nov 8, 2018 FPT <br>
 * 【Update date, Updater, Update Summary】<br>
 *  YYYY/MM/DD FPT No.xxx：NNNNNN
 */
@Component
public class HealthcheckConfig extends AbstractHealthIndicator {
	@Override
	protected void doHealthCheck(Health.Builder bldr) throws Exception {
			bldr.up();
	}
}
