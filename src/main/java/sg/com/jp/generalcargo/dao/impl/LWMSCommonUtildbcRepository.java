package sg.com.jp.generalcargo.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.LWMSCommonUtilRepository;
import sg.com.jp.generalcargo.domain.AccountValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.Constant;

@Repository("LWMSCommonUtildbcRepository")
public class LWMSCommonUtildbcRepository implements LWMSCommonUtilRepository {

	private static final Log log = LogFactory.getLog(LWMSCommonUtildbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	// StartRegion LWMSCommonUtildbcRepository

	// jp.src.ejb.sessionBeans.cab.lwms--->LWMSCommonUtilEJB-->isBusTypeLighterTerminal()
		public boolean isBusTypeLighterTerminal(String busType) throws BusinessException {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			SqlRowSet rs = null;
			boolean isLT = false;
			try {
				log.info("START isBusTypeLighterTerminal DAO :: busType: " + CommonUtility.deNull(busType));
				
				String sql = "SELECT misc_type_cd from misc_type_code where cat_cd =:catCd AND rec_status='A' and  misc_type_cd = :busType ";
				paramMap.put("catCd", Constant.MISCTYPECD_TERMINAL_CD);
				paramMap.put("busType", busType);
				log.info("sql: " + sql + " paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

				if (rs.next()) {
					isLT = true;
				} else {
					isLT = false;
				}
				// return isLT;
			} catch (Exception e) {
				log.info("Exception isBusTypeLighterTerminal : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END isBusTypeLighterTerminal DAO  isLT: " + isLT);
			}
			return isLT;
		}
		
		// jp.src.ejb.sessionBeans.cab.lwms-->LWMSCommonUtil--->getCsTypeByTerminal
		public String getCsTypeByTerminal(String terminalCd) throws BusinessException {

			Map<String, Object> paramMap = new HashMap<String, Object>();
			SqlRowSet rs = null;
			String paraCd = "";
			String cstype = "";

			try {
				log.info("START getCsTypeByTerminal DAO" + CommonUtility.deNull(terminalCd));
				String sql = "SELECT para_cd from text_para where para_cd LIKE '"
						+ Constant.TEXTPARACD_CS_TO_TERMINAL_CODE_PREFIX + "%'";
				sql += " AND value = :terminalCd";

				paramMap.put("terminalCd", terminalCd);
				log.info("sql: " + sql + " paramMap: " + paramMap);
				rs =namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
				if (rs.next()) {
					paraCd = CommonUtility.deNull(rs.getString("para_cd"));
				}

				if (!paraCd.equals("")) {
					cstype = paraCd.substring(Constant.TEXTPARACD_CS_TO_TERMINAL_CODE_PREFIX.length());
				}
				
				log.info("cstype: " + cstype);
				return cstype;
			} catch (Exception e) {
				log.info("Exception getCsTypeByTerminal : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END getCsTypeByTerminal DAO");
			
			}

		}
		
		// jp.src.ejb.sessionBeans.cab.lwms-->LWMSCommonUtil--->retrieveCustAcct
		public AccountValueObject retrieveCustAcct(String salesType) throws BusinessException {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			SqlRowSet prs = null, prs1 = null;
			AccountValueObject accountValueObject = new AccountValueObject();
			String custCodeQuery = new StringBuffer().append("select value from text_para where para_cd=:para_cd_acct")
					.append(salesType).append("'").toString();
			String custAcctQuery = new StringBuffer().append("select value from text_para where para_cd=:para_cd")
					.append(salesType).append("'").toString();

			try {
				log.info("START retrieveCustAcct DAO"+ CommonUtility.deNull(salesType));
				
				paramMap.put("para_cd_acct", Constant.TEXTPARACD_CASHSALES_CUST_ACCT_PREFIX);
				paramMap.put("para_cd", Constant.TEXTPARACD_CASHSALES_CUST_CODE_PREFIX);
				
				log.info("sql: " + custCodeQuery + " paramMap: " + paramMap);
				prs = namedParameterJdbcTemplate.queryForRowSet(custCodeQuery.toString(), paramMap);
				if (prs.next()) {
					accountValueObject.setCustomerCode(CommonUtility.deNull(prs.getString(1)));
					log.info("sql: " + custAcctQuery + " paramMap: " + paramMap);
					prs1 = namedParameterJdbcTemplate.queryForRowSet(custAcctQuery.toString(), paramMap);
					if (prs1.next()) {
						accountValueObject.setAccountNumber(CommonUtility.deNull(prs1.getString(1)));
					}
				}
			
			} catch (Exception ex) {
				log.info("Exception retrieveCustAcct : ", ex);
				throw new BusinessException("M4201");
			} finally {
				log.info("END retrieveCustAcct DAO  accountValueObject: " + accountValueObject.toString());
				
			}
			return accountValueObject;
		}

		// jp.src.ejb.sessionBeans.cab.lwms-->LWMSCommonUtil--->getMiscType
		public Map<String,Object> getMiscType(String catCd) throws BusinessException {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			SqlRowSet rs = null;
			Map<String,Object> map = new HashMap<String,Object>();

			try {
				log.info("START getMiscType DAO"+ CommonUtility.deNull(catCd));
				
				String sql = "SELECT misc_type_cd, misc_type_nm from misc_type_code where cat_cd =:catCd and rec_status = 'A' order by misc_type_cd asc";
				paramMap.put("catCd", catCd);
				log.info("sql: " + sql + " paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

				while (rs.next()) {
					String mtCd = CommonUtility.deNull(rs.getString("misc_type_cd"));
					String mtName = CommonUtility.deNull(rs.getString("misc_type_nm"));
					map.put(mtCd, mtName);
				}
				log.info("map: " + map);
				return map;
			} catch (Exception e) {
				log.info("Exception getMiscType : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END getMiscType DAO");
			}

		}
	// EndRegion LWMSCommonUtildbcRepository

}
