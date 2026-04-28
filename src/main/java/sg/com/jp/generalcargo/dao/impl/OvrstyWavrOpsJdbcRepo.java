package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.OvrstyWavrOpsRepo;
import sg.com.jp.generalcargo.domain.BillingCodesVO;
import sg.com.jp.generalcargo.domain.OverStayDockageValueObject;
import sg.com.jp.generalcargo.domain.WaiverCodesVO;
import sg.com.jp.generalcargo.util.BusinessException;

@Repository("ovrstyWavrOpsRepo")
public class OvrstyWavrOpsJdbcRepo implements OvrstyWavrOpsRepo {
	private static final Log log = LogFactory.getLog(OvrstyWavrOpsJdbcRepo.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// ejb.sessionBeans.ops.OvrstyWavr -->OvrstyWavrOpsEjb
	@Override
	public List<BillingCodesVO> getBillingReasons(BillingCodesVO valueObject) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<BillingCodesVO> arrListBillingReasons = new ArrayList<BillingCodesVO>();
		try {
			log.info("START: getBillingReasons DAO valueObject:" + valueObject.toString());

			String sql = "SELECT BILLING_CD, BILLING_NM FROM BILLING_CODES WHERE VESSEL_TYPE= :vslType and status='A' ORDER BY BILLING_CD ASC";

			paramMap.put("vslType", valueObject.getVesselTypeCode());
			log.info(" *** getBillingReasons SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {

				// valueObject.setBillingCode(rs.getString("BILLING_CD"));
				// alBillingCodes.add(rs.getString("BILLING_CD"));
				// billingReasonsVector.add(valueObject);
				/*
				 * hashMapBillingReasons.put("billingCD", rs.getString("BILLING_CD"));
				 * hashMapBillingReasons.put("billingNM", rs.getString("BILLING_NM"));
				 */
				BillingCodesVO billingCodesVO = new BillingCodesVO();
				billingCodesVO.setBillingCode(rs.getString("BILLING_CD"));
				billingCodesVO.setBillingNumber(rs.getString("BILLING_NM"));
				arrListBillingReasons.add(billingCodesVO);
			}

			log.info("END: *** getBillingReasons Result *****" + arrListBillingReasons.toString());
		} catch (NullPointerException e) {
			log.error("Exception: getBillingReasons ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getBillingReasons ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBillingReasons  DAO  END");
		}
		return arrListBillingReasons;
	}

	@Override
	public List<WaiverCodesVO> getWaiverCodes(WaiverCodesVO valueObject) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<WaiverCodesVO> arrListWaiverCodes = new ArrayList<WaiverCodesVO>();
		try {
			log.info("START: getWaiverCodes  DAO valueObject:" + valueObject.toString());

			String sql = "SELECT  WAIVER_CD, WAIVER_NM  FROM DEPARTURE_WAIVER_CODES  WHERE VESSEL_TYPE= :vslType and status='A' ORDER BY WAIVER_CD ASC";

			paramMap.put("vslType", valueObject.getVesselTypeCode());
			log.info(" *** getWaiverCodes SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				/*
				 * // valueObject.setWaiverCode("WAIVER_CD"); //
				 * valueObject.setWaiverNumber("WAIVER_NM");
				 * 
				 * Code commented on 01-sep-2007 alWaiverCodes.add(rs.getString("WAIVER_CD"));
				 * hashMapWaiverCodes.put("WaiverCode", rs.getString("WAIVER_CD"));
				 * hashMapWaiverCodes.put("WaiverName", rs.getString("WAIVER_NM"));
				 */

				WaiverCodesVO waiverCodesVO = new WaiverCodesVO();
				waiverCodesVO.setWaiverCode(rs.getString("WAIVER_CD"));
				waiverCodesVO.setWaiverNumber(rs.getString("WAIVER_NM"));
				arrListWaiverCodes.add(waiverCodesVO);
			}
			log.info("END: *** getWaiverCodes Result *****" + arrListWaiverCodes.toString());
		} catch (NullPointerException e) {
			log.error("Exception: getWaiverCodes ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getWaiverCodes ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getWaiverCodes  DAO  END");
		}
		return arrListWaiverCodes;
	}

	@Override
	public boolean hasAccesstoOSD(String userId) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		boolean hasAccess = false;
		try {
			log.info("START: hasAccesstoOSD  DAO userId:" + userId);

			sb.append(" select user_acct From adm_user where user_id in (select user_id  From adm_user_acc_grp,  ");
			sb.append("adm_group where grp_name = 'OverstayWavrReqHandler' ");
			sb.append("and adm_user_acc_grp.grp_id = adm_group.grp_id) and user_acct = :userId");

			String sql = sb.toString();

			paramMap.put("userId", userId);
			log.info(" *** hasAccesstoOSD SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				hasAccess = true;
			}
			log.info("END: *** hasAccesstoOSD Result *****" + hasAccess);
		} catch (NullPointerException e) {
			log.error("Exception: hasAccesstoOSD ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: hasAccesstoOSD ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: hasAccesstoOSD  DAO  END");
		}
		return hasAccess;
	}

	@Override
	public OverStayDockageValueObject getWaiverStatus(String vvcd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		OverStayDockageValueObject overStayDockageValueObject = null;
		try {
			log.info("START: getWaiverStatus  DAO vvcd:" + vvcd);

			sb.append("select BC.Billing_nm,(DWC.WAIVER_NM || ");
			sb.append(
					"(select '<br>' || d2.waiver_nm from DEPARTURE_WAIVER_CODES d2 where d2.waiver_cd = ODW.waiver_cd_2 )|| ");
			sb.append(
					"(select '<br>' || d3.waiver_nm from DEPARTURE_WAIVER_CODES d3 where d3.waiver_cd = ODW.waiver_cd_3 )|| ");
			sb.append(
					"(select '<br>' || d4.waiver_nm from DEPARTURE_WAIVER_CODES d4 where d4.waiver_cd = ODW.waiver_cd_4 )|| ");
			sb.append(
					"(select '<br>' || d4.waiver_nm from DEPARTURE_WAIVER_CODES d4 where d4.waiver_cd = ODW.waiver_cd_5 ) || ");
			sb.append(
					"(select '<br>' || d4.waiver_nm from DEPARTURE_WAIVER_CODES d4 where d4.waiver_cd = ODW.waiver_cd_6 )) waiver_nm, ");
			sb.append("isrejected  ");
			sb.append("from overstay_dockage_waiver ODW, Departure_waiver_codes DWC, Billing_codes BC  ");
			sb.append("where nvl(ODW.billing_Cd,'1234') = BC.billing_CD(+)  ");
			sb.append("and nvl(ODW.Waiver_cd,'1223') = DWC.waiver_CD(+)  ");
			sb.append("and ODW.vv_cd = :vvcd ");

			String sql = sb.toString();

			paramMap.put("vvcd", vvcd);

			log.info(" *** getWaiverStatus SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				overStayDockageValueObject = new OverStayDockageValueObject();
				overStayDockageValueObject.setWaiverName(rs.getString("WAIVER_NM"));
				overStayDockageValueObject.setBillingName(rs.getString("BILLING_NM"));
				overStayDockageValueObject.setISRejected(rs.getString("ISREJECTED"));
			}
		} catch (NullPointerException e) {
			log.error("Exception: getWaiverStatus ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getWaiverStatus ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getWaiverStatus  DAO  overStayDockageValueObject:"
					+ (overStayDockageValueObject != null ? overStayDockageValueObject.toString() : ""));
		}
		return overStayDockageValueObject;
	}
}
