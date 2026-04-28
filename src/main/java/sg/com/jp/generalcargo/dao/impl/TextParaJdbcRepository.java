package sg.com.jp.generalcargo.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.TextParaRepository;
import sg.com.jp.generalcargo.domain.TextParaVO;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("TextParaRepository")
public class TextParaJdbcRepository implements TextParaRepository {

	private static final Log log = LogFactory.getLog(TextParaJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// ejb.sessionBeans.codes.textPara--TextParaEJB
	@Override
	public TextParaVO getParaCodeInfo(TextParaVO tpvo) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer();

		TextParaVO tpo = new TextParaVO();
		try {

			log.info("START: getParaCodeInfo Dao Start TextParaVO:" + tpvo.toString());

			sql.append("SELECT * FROM TEXT_PARA ");
			sql.append("WHERE PARA_CD = :para ");

			paramMap.put("para", tpvo.getParaCode());
			log.info("getParaCodeInfo SQL Query:" + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				tpo.setParaCode(rs.getString("PARA_CD"));
				tpo.setValue(rs.getString("VALUE"));
				tpo.setParaDesc(CommonUtility.deNull(rs.getString("PARA_DESC")));
				tpo.setUser(rs.getString("LAST_MODIFY_USER_ID"));
				tpo.setTimestamp(rs.getTimestamp("LAST_MODIFY_DTTM"));
			}

		
		} catch (NullPointerException e) {
			log.info("Exception getParaCodeInfo :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getParaCodeInfo :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getParaCodeInfo  DAO  END");
		}
		
		log.info("END: getParaCodeInfo  DAO  END **** tpo: " + tpo.toString());
		return tpo;
	}
}
