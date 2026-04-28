package sg.com.jp.generalcargo.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.LighterTerminalRepository;
import sg.com.jp.generalcargo.domain.CargoDeclarationVO;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

@Repository("LighterTerminalJdbcRepository")
public class LighterTerminalJdbcRepository implements LighterTerminalRepository {

	private static final Log log = LogFactory.getLog(LighterTerminalJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	protected JdbcTemplate jdbcTemplate;

	// StartRegion LighterTerminalJdbcRepository
	// jp.src.ejb.sessionBeans.lwms--->LighterTerminalEJB----->getCargoDeclarationByDsaNbr()
	// jp.src.sg.com.jp.lighterterminal.dao----->LighterTerminalJdbcDao------>getCargoDeclarationByDsaNbr()
	public CargoDeclarationVO getCargoDeclarationByDsaNbr(String dsa_nbr) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		Map<String, String> params = new HashMap<>();
		params.put("dsa_nbr", dsa_nbr.trim());
		CargoDeclarationVO retval = null;
		List<CargoDeclarationVO> tmp = null;
		try {
			log.info("START getCargoDeclarationByDsaNbr DAO");
			sb.append("SELECT distinct c.DSA_NBR, c.DSA_ID, c.CUST_CD, cc2.CO_NM AS CUST_NAME, ");
			sb.append("  c.LIGHTER_OPR_CD, cc1.CO_NM AS LIGHTER_OPR_NM, c.TERMINAL_CD, ");
			sb.append("  misc2.MISC_TYPE_NM AS TERMINAL_NAME, c.DSA_STATUS, misc1.MISC_TYPE_NM AS DSA_STATUS_NM, ");
			sb.append("  c.BILLING_STATUS, c.CALL_SIGN, c.LIGHTER_NM, c.LIGHTER_ETA, c.DCL_TRUCK_NBR, ");
			sb.append("  c.ACT_TRUCK_NBR, c.TRUCK_STATUS, c.DRIVER1_PASS_ID, c.DRIVER2_PASS_ID, c.PAYM_MODE, ");
			sb.append("  c.ACCT_NBR, c.BILL_NBR, c.BILL_DTTM, c.SHIP_NM, c.DG_CARGO_IND, c.CARGO_DEST_TYPE, ");
			sb.append("  c.CARGO_DEST_CD, c.GATE_IN_DTTM, c.GATE_OUT_DTTM, c.GATE_STATUS, c.CRANE_NBR, ");
			sb.append("  c.CRANE_OPR_CD, c.CRANE_START_DTTM, c.CRANE_END_DTTM,  c.CHECK_IN_DTTM, c.PAN_DTTM, ");
			sb.append("  c.VSL_MVMT_NBR, c.CASHSALES_REF, c.CREATE_USER_ID, c.CREATE_DTTM, c.SUBMITTED_USER_ID, ");
			sb.append("  c.SUBMITTED_DTTM, c.LAST_MODIFY_USER_ID, c.LAST_MODIFY_DTTM, c.OUTWARD_DTTM, ");
			sb.append(
					"  d.ATB_DTTM DOCKAGEATB, lacct1.USER_NM AS CREATE_USER_NM, lacct2.USER_NM AS SUBMITTED_USER_NM, ");
			sb.append(
					"  lacct3.USER_NM AS LAST_MODIFY_USER_NM, NVL(misc3.MISC_TYPE_NM, 'Not Ready For Billing') AS BILLING_STATUS_NM, ");
			sb.append("  misc4.MISC_TYPE_NM as CARGO_TYPE, ldd.CARGO_TYPE_CD, c.AMEND_REM, c.LAST_AMEND_DTTM, ");
			sb.append("  c.LIFT_CHARGE, c.ADDITIONAL_CHARGE, (c.LIFT_CHARGE + c.ADDITIONAL_CHARGE) AS TOTAL_CHARGE, ");
			sb.append(
					"  c.SUGGESTION_CRANE, c.QUOTE_REM, c.INTERNAL_REM, c.CUST_REM, c.APPROVE_REM, c.APPROVE_STATUS, ");
			sb.append("  c.VERIFY_STATUS, c.QUOTATION_USER_ID, c.APPROVED_USER_ID, c.VERIFIED_USER_ID ");
			sb.append("FROM LWMS_DSA c ");
			sb.append("LEFT JOIN COMPANY_CODE cc1 ON (cc1.CO_CD = c.LIGHTER_OPR_CD) ");
			sb.append("LEFT JOIN COMPANY_CODE cc2 ON (cc2.CO_CD = c.CUST_CD) ");
			sb.append("LEFT JOIN LWMS_DOCKAGE d ON (c.VSL_MVMT_NBR = d.VSL_MVMT_NBR) ");
			sb.append(
					"LEFT JOIN MISC_TYPE_CODE misc1 ON (misc1.CAT_CD = 'LW_DSA_ST' AND misc1.MISC_TYPE_CD = c.DSA_STATUS) ");
			sb.append(
					"LEFT JOIN MISC_TYPE_CODE misc2 ON (misc2.CAT_CD = 'TERM_CD' AND misc2.MISC_TYPE_CD = c.TERMINAL_CD) ");
			sb.append(
					"LEFT JOIN MISC_TYPE_CODE misc3 ON (misc3.CAT_CD = 'LW_BILL_ST' AND misc3.MISC_TYPE_CD = c.BILLING_STATUS) ");
			sb.append("LEFT JOIN LWMS_DSA_DETAILS ldd ON ldd.DSA_NBR = c.DSA_NBR ");
			sb.append(
					"LEFT JOIN MISC_TYPE_CODE misc4 ON (misc4.CAT_CD = 'LW_CARGO_T' AND misc4.MISC_TYPE_CD = ldd.CARGO_TYPE_CD) ");
			sb.append("LEFT JOIN LOGON_ACCT lacct1 ON (lacct1.LOGIN_ID = c.CREATE_USER_ID) ");
			sb.append("LEFT JOIN LOGON_ACCT lacct2 ON (lacct2.LOGIN_ID = c.SUBMITTED_USER_ID) ");
			sb.append("LEFT JOIN LOGON_ACCT lacct3 ON (lacct3.LOGIN_ID = c.LAST_MODIFY_USER_ID) ");
			sb.append("WHERE c.DSA_NBR = :dsa_nbr;");
			tmp = namedParameterJdbcTemplate.query(sb.toString(), params,
					new BeanPropertyRowMapper<CargoDeclarationVO>(CargoDeclarationVO.class));
			if (tmp != null) {
				if (tmp.size() > 0) {
					retval =  tmp.get(0);
					int totalLifts = this.getTotalLiftByDsaNbr(dsa_nbr);
					retval.setTotalLifts(totalLifts);
				}
			}
		} catch (Exception e) {
			log.error("Exception getCargoDeclarationByDsaNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getCargoDeclarationByDsaNbr DAO");
		}
		return retval;
	}

	// jp.src.sg.com.jp.lighterterminal.dao----->LighterTerminalJdbcDao------>getCargoDeclarationByDsaNbr()
	public int getTotalLiftByDsaNbr(String dsa_nbr) throws BusinessException {
		String sql = "";
		Map<String, String> params = new HashMap<>();
		try {
			log.info("START getTotalLiftByDsaNbr DAO");
			sql = "SELECT SUM(NORM_LIFT_NBR + WHARF_LIFT_NBR) FROM LWMS_DSA_CRANAGE c WHERE DSA_NBR = :dsaNbr";
			if (dsa_nbr == null) {
				return 0;
			}
			params.put("dsaNbr", dsa_nbr.trim());
		} catch (Exception e) {
			log.error("Exception getCargoDeclarationByDsaNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getTotalLiftByDsaNbr DAO");
		}
		return (int) namedParameterJdbcTemplate.queryForObject(sql, params,
				new BeanPropertyRowMapper(GeneralEventLogValueObject.class));
	}
	// EndRegion LighterTerminalJdbcRepository

}
