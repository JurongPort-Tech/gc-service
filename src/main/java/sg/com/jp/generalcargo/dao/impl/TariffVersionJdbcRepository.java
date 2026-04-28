package sg.com.jp.generalcargo.dao.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.TariffVersionRepository;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("TariffVersionJdbcRepository")
public class TariffVersionJdbcRepository implements TariffVersionRepository {

	private static final Log log = LogFactory.getLog(TariffVersionJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	protected JdbcTemplate jdbcTemplate;

	// StartRegion TariffVersionJdbcRepository
	// jp.src.ejb.sessionBeans.cab.tariff--->TariffVersionEJB--->getCurrentVersion()
	public int getCurrentVersion(Timestamp ts) throws BusinessException {
		log.info("---Get Current Version---");
		
		int version = -1;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info(" START getCurrentVersion DAO :: ts: " + CommonUtility.deNull(ts.toString()));
			StringBuffer sql = new StringBuffer();
			sql.setLength(0);
			sql.append("SELECT version_nbr FROM tariff_version WHERE eff_start_dttm < :ts AND eff_end_dttm>=:ts");
			paramMap.put("ts", ts);
			log.info("SQL: " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			log.info("Reading results");
			if (rs != null && rs.next()) {
				version = rs.getInt(1);
			} else {
				rs = null;
				sql.setLength(0);
				sql = sql.append(
						"SELECT version_nbr FROM tariff_version WHERE eff_start_dttm < :ts AND eff_end_dttm IS NULL");
				
				paramMap.put("ts", ts);
				log.info("SQL: " + sql + " paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
				if (rs != null && rs.next()) {
					version = rs.getInt(1);
				} else {
					version = -1;
				}
			}
			log.info("Current Version : " + version);
			log.info("---Done---");
		} catch (Exception e) {
			log.info("Exception updateCargoTypeCargoCategory : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info(" END getCurrentVersion DAO  version: " + version);
		}
		return version;
	}
	// EndRegion TariffVersionJdbcRepository

}
