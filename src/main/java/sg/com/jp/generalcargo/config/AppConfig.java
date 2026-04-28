/*
 * Copyright (C) Jurong Port Pte Ltd. 2018. All rights reserved. Prohibited without permission
 */
package sg.com.jp.generalcargo.config;

import java.util.Base64;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import oracle.ucp.jdbc.JDBCConnectionPoolStatistics;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;


@Configuration
public class AppConfig {
	
	private static final Log log = LogFactory.getLog(AppConfig.class);

	@Value("${spring.datasource.driver-class-name}")
	private String dbDriverClassName;

	@Value("${spring.datasource.url}")
	private String dbUrl;

	@Value("${spring.datasource.username}")
	private String dbUsername;

	@Value("${spring.datasource.password}")
	private String dbPassword;

	@Value("${oracle.ucp.minPoolSize}")
	private String minPoolSize;

	@Value("${oracle.ucp.initialPoolSize}")
	private String initialPoolSize;

	@Value("${oracle.ucp.maxPoolSize}")
	private String maxPoolSize;

	@Value("${oracle.ucp.maxConnReuseCount}")
	private String maxConnReuseCount;

	@Value("${oracle.ucp.connWaitTimeout}")
	private String connWaitTimeout;

	@Value("${oracle.ucp.abandonConnTimeout}")
	private String abandonConnTimeout;

	@Value("${oracle.ucp.inactiveConnTimeout}")
	private String inactiveConnTimeout;

	@Value("${oracle.ucp.queryTimeout}")
	private String queryTimeout;

	@Value("${oracle.ucp.timeoutCheckInterval}")
	private String timeoutCheckInterval;

	
	private void init() {

	}

	@Bean(name = "OracleUniversalConnectionPool")
	public DataSource dataSource() {
		PoolDataSource pds = null;
		int minSize = 2;
		int initSize = 5;
		int maxSize = 50;
		int maxConnRuCnt = 100;
		int connWaitTO = 30;
		int abandonConnTO = 5;
		int inactConnTO = 30;
		int toCheckIntv = 30;
		int queryTO = 30;

		try {
			log.info("minPoolSize: " + minPoolSize);
			minSize = Integer.valueOf(minPoolSize);

			log.info("initialPoolSize: " + initialPoolSize);
			initSize = Integer.valueOf(initialPoolSize);

			log.info("maxPoolSize: " + maxPoolSize);
			maxSize = Integer.valueOf(maxPoolSize);

			log.info("maxConnReuseCount: " + maxConnReuseCount);
			maxConnRuCnt = Integer.valueOf(maxConnReuseCount);

			log.info("connWaitTimeout: " + connWaitTimeout);
			connWaitTO = Integer.valueOf(connWaitTimeout);

			log.info("abandonConnTimeout: " + abandonConnTimeout);
			abandonConnTO = Integer.valueOf(abandonConnTimeout);

			log.info("inactiveConnTimeout: " + inactiveConnTimeout);
			inactConnTO = Integer.valueOf(inactiveConnTimeout);

			log.info("queryTimeout: " + queryTimeout);
			queryTO = Integer.valueOf(queryTimeout);

			log.info("timeoutCheckInterval: " + timeoutCheckInterval);
			toCheckIntv = Integer.valueOf(timeoutCheckInterval);
		} catch (Exception e) {
			log.error(e);
		}
		try {
			pds = PoolDataSourceFactory.getPoolDataSource();
			pds.setURL(dbUrl);
			pds.setConnectionFactoryClassName(dbDriverClassName);
			pds.setUser(dbUsername);
			byte[] b = Base64.getDecoder().decode(dbPassword);
			String password = new String(b);
			pds.setPassword(password);
			

			pds.setMinPoolSize(minSize);
			pds.setInitialPoolSize(initSize);
			pds.setMaxPoolSize(maxSize);

			pds.setMaxConnectionReuseCount(maxConnRuCnt);
			pds.setConnectionWaitTimeout(connWaitTO);
			pds.setAbandonedConnectionTimeout(abandonConnTO);
			pds.setInactiveConnectionTimeout(inactConnTO);
			pds.setTimeoutCheckInterval(toCheckIntv);
			pds.setQueryTimeout(queryTO);
			log.info("pool conn setting all set.");

		} catch (Exception e) {
			log.error(e);
		}

		try {
			JDBCConnectionPoolStatistics stats = pds.getStatistics();
			log.info("pds stats: " + stats);
			if (stats != null) {
				int totalCnt = stats.getTotalConnectionsCount();
				int availCnt = stats.getAvailableConnectionsCount();
				int peakCnt = stats.getPeakConnectionsCount();
				int pendReqCnt = stats.getPendingRequestsCount();
				int remainCnt = stats.getRemainingPoolCapacityCount();
				int createCnt = stats.getConnectionsCreatedCount();
				int closeCnt = stats.getConnectionsClosedCount();
				long averWaitTime = stats.getAverageConnectionWaitTime();

				log.info("Total Conn Count     : " + totalCnt);
				log.info("Available Conn Count : " + availCnt);
				log.info("Peak Conn Count      : " + peakCnt);
				log.info("Pending Request Count: " + pendReqCnt);
				log.info("Remain Conn Count    : " + remainCnt);
				log.info("Conn Create Count    : " + createCnt);
				log.info("Conn Close Count     : " + closeCnt);
				log.info("Aver Wait Time       : " + averWaitTime);
			}
		} catch (Exception e) {
		}
		return pds;
	}
}