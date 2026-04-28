package sg.com.jp.generalcargo.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.SystemCodeRepo;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("systemCodeRepo")
public class SystemCodeJdbcRepository implements SystemCodeRepo {
	private static final Log log = LogFactory.getLog(SystemCodeJdbcRepository.class);
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public String getValue(String paraCd) throws BusinessException {
		String value = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String queryStr = "select VALUE from SYSTEM_PARA where PARA_CD = :paraCd ";
		try {
			log.info("START: getValue  DAO  Start paraCd " + CommonUtility.deNull(paraCd));
			paramMap.put("paraCd", paraCd);

			log.info(" getValue  DAO  SQL " + queryStr);
			rs = namedParameterJdbcTemplate.queryForRowSet(queryStr, paramMap);

			if (rs.next()) {
				value = rs.getString("VALUE");
			}
			log.info("END: *** getValue Result *****" + CommonUtility.deNull(value));
		} catch (NullPointerException e) {
			log.info("Exception getValue : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getValue : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getValue  DAO  END");
		}
		return value;
	}

}
