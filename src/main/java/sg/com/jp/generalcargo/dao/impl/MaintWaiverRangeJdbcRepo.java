package sg.com.jp.generalcargo.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.MaintWaiverRangeRepo;
import sg.com.jp.generalcargo.domain.GroupVO;
import sg.com.jp.generalcargo.util.BusinessException;

@Repository("maintWaiverRangeRepo")
public class MaintWaiverRangeJdbcRepo implements MaintWaiverRangeRepo {
	private static final Log log = LogFactory.getLog(MaintWaiverRangeJdbcRepo.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// ejb.sessionBeans.ops.MaintWaiverRange -->MaintWaiverRangeEjb
	@Override
	public GroupVO getLevelId(String amount) throws BusinessException {
		SqlRowSet rs = null;
		GroupVO groupVO = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getLevelId  DAO amount:" + amount);

			String sql = "SELECT GROUP_ID, GROUP_NM FROM OSD_APPV_GRP WHERE :amount BETWEEN MIN_AMOUNT AND MAX_AMOUNT AND STATUS='A'";

			paramMap.put("amount", amount);

			log.info(" *** getLevelId SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs != null) {
				while (rs.next()) {
					groupVO = new GroupVO();
					groupVO.setGroupId(rs.getString("GROUP_ID"));
					groupVO.setGroupName(rs.getString("GROUP_NM"));
				}
			}
			log.info("END: *** getLevelId Result *****" + (groupVO == null ? "null" : groupVO.toString()));
		} catch (NullPointerException e) {
			log.error("Exception: getLevelId ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getLevelId ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getLevelId  DAO  END");
		}
		return groupVO;
	}
}
