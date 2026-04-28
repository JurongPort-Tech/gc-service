package sg.com.jp.generalcargo.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.TesnSearchRepository;
import sg.com.jp.generalcargo.domain.TesnSearchValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("tesnSearchRepository")
public class TesnSearchJdbcRepository  implements TesnSearchRepository {

	// ejb.sessionBeans.gbms.cargo.tesn-->TesnSearchEJB
	private static final Log log = LogFactory.getLog(TextParaJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	public String tesnSearch(String tesnNo) throws BusinessException {
		String sql1 = "";
		String sql2 = "";
		String transType = "";
		boolean checkTesn = true;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		sql1 = "SELECT * FROM ESN E WHERE ESN_ASN_NBR = :tesnNo AND ESN_STATUS = 'A'";
		try {
			log.info("START: tesnSearch  DAO  Start tesnNo" + CommonUtility.deNull(tesnNo));
			paramMap.put("tesnNo", tesnNo);
			log.info(" tesnSearch  DAO  SQL " + sql1);
			for (rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
					rs1.next(); 
					log.info("In while : ".concat(String.valueOf(String.valueOf(checkTesn))))) {
				transType = CommonUtility.deNull(rs1.getString("TRANS_TYPE"));
				//inVoyVarNbr = CommonUtility.deNull(rs1.getString("IN_VOY_VAR_NBR"));
				checkTesn = false;
			}

			if (checkTesn) {
				log.info("In if : ".concat(String.valueOf(String.valueOf(checkTesn))));
				log.info("Writing from TesnSearchEJB.tesnSearch");
				log.info("The Tesn record cannot be found. Please try again.");
				log.info("Before throwing exception ");
				throw new BusinessException("The Tesn record cannot be found. Please try again.");
			}
			if (transType.equalsIgnoreCase("A") || transType == "A") {
				String outVoyNbr = "";
				sql2 = "SELECT OUT_VOY_VAR_NBR FROM ESN WHERE ESN_ASN_NBR = :tesnNo";
				TesnSearchValueObject tesnSearchValueObject;

				paramMap.put("tesnNo", tesnNo);

				log.info(" tesnSearch  DAO  SQL " + sql2);
				for (rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap); rs2.next(); tesnSearchValueObject.setOutVoyNo(outVoyNbr)) {
					tesnSearchValueObject = new TesnSearchValueObject();
					outVoyNbr = CommonUtility.deNull(rs2.getString("OUT_VOY_VAR_NBR"));
				}

			}
			log.info(" tesnSearch  DAO  Result" + CommonUtility.deNull(transType.toString()));
		} catch (BusinessException e) {
			log.info("Exception tesnSearch : ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: tesnSearch " + e.toString());
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: tesnSearch " + e.toString());
			throw new BusinessException("M4201");
		} finally {
			log.info("END: tesnSearch  DAO  END");
		}

		return transType;
	}

}
