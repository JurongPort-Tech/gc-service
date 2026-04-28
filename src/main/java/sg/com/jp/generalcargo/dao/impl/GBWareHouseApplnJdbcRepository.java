package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.EdoRepository;
import sg.com.jp.generalcargo.dao.EsnRepository;
import sg.com.jp.generalcargo.dao.GBWareHouseAplnRepository;
import sg.com.jp.generalcargo.domain.Email;
import sg.com.jp.generalcargo.domain.EmailValueObject;
import sg.com.jp.generalcargo.domain.GBWareHouseAplnVO;
import sg.com.jp.generalcargo.domain.IMessageValueObject;
import sg.com.jp.generalcargo.domain.Sms;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("GBWareHouseApplnRepository")
public class GBWareHouseApplnJdbcRepository implements GBWareHouseAplnRepository {

	private static final Log log = LogFactory.getLog(GBWareHouseApplnJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	@Autowired
	EsnRepository esnRepo;
	@Autowired
	EdoRepository edoRepo;

	@Value("${jp.common.notificationProperties.emailEndpoint}")
	String commonServiceUrl;

	// package: ejb.sessionBeans.gbms.warehouse.application-->GBWareHouseAplnEJB

	@Override
	public Integer voidWarehouseApplicationWithASNNubmer(String edoNbr, String userId) throws BusinessException {
		Integer result = 0;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		try {
			log.info("START: voidWarehouseApplicationWithASNNubmer Dao Start edoNbr:" + edoNbr + "userId:" + userId);

			sql.append(" update WA_APPLN_DETAILS set rec_status='X' ");
			sql.append(" , last_modify_user_id=:userId, last_modify_dttm=SYSDATE ");
			sql.append(" where edo_esn_nbr=:edoNbr ");
			sql.append(" and  rec_status in ('S') and wa_ref_nbr not like '%E%'");
			// sql.append(" and rec_status = 'S' ");

			paramMap.put("edoNbr", edoNbr);
			paramMap.put("userId", userId);
			log.info("SQL Query: " + sql.toString());
			result = namedParameterJdbcTemplate.update(sql.toString(), paramMap);

			log.info("END: *** voidWarehouseApplicationWithASNNubmer Result *****" + result);
		} catch (NullPointerException e) {
			log.error("Exception voidWarehouseApplicationWithASNNubmer :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception voidWarehouseApplicationWithASNNubmer :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: voidWarehouseApplicationWithASNNubmer DAO");
		}
		return result;
	}

	@Override
	public List<GBWareHouseAplnVO> getWarehouseApplicationListByASNNubmer(String edoNbr) throws BusinessException {
		log.info("Inside EJB- getWarehouseApplicationListByASNNubmer() method..........");
		GBWareHouseAplnVO vo;
		// GBWareHouseAplnProcessAccountVO pvo;
		List<GBWareHouseAplnVO> whVoArray = new ArrayList<GBWareHouseAplnVO>();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		try {
			log.info("START: getWarehouseApplicationListByASNNubmer Dao Start edoNbr:" + edoNbr);

			int rowNum = 1;
			sql.append(
					" select rownum sno, w.WA_REF_NBR, decode(w.rec_status, 'N','Draft','S','Submitted','A','Approved','R','Rejected','C','Closed','B','Billed','V','Verified','X','Cancelled') rec_status, ");
			sql.append(" w.EDO_ESN_NBR, DECODE(w.vsl_cargo_ind,'Y', 'Vessel', 'N', 'Non-Vessel') type, w.cargo_desc, ");
			sql.append(
					" to_char(nvl(wp.commence_date, w.commence_date), 'DD-MM-YYYY') start_date, to_char(nvl(wp.end_date,w.end_date), 'DD-MM-YYYY') end_date, ");
			sql.append(
					" nvl(wp.cargo_billable_ton, w.cargo_billable_ton) cargo_billable_ton, w.storage_type, w.vv_cd, (w.end_date - w.commence_date+1) storage_days, decode(w.purp_cd, 'IM', 'Import', 'EX', 'Export', ' ') purp_cd, ");
			sql.append(" nvl(wp.billing_criteria, ' ') billing_criteria, nvl(wp.nbr_pkgs, w.nbr_pkgs) nbr_pkgs, ");
			sql.append(" (select vsl_nm ||' / '|| out_voy_nbr from vessel_call where vv_cd=w.vv_cd) vsl_name, ");
			sql.append(" w.cust_email, w.email_alert_ind, cust_tel, sms_alert_ind ");
			sql.append(" from wa_appln_details w,  wa_appln_processg_details wp ");
			sql.append(" where w.wa_ref_nbr = wp.wa_ref_nbr(+) ");
			// sql.append(" and rec_status in ('N','S','V','A','R','C','X') ");
			sql.append(" and  rec_status in ('S','N') ");
			// check for edo_nbr
			sql.append(" and edo_esn_nbr =:edoNbr ");
			sql.append(" order by w.WA_REF_NBR desc ");

			paramMap.put("edoNbr", edoNbr);
			log.info("SQL Query: " + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				vo = new GBWareHouseAplnVO();
				vo.setSNo(String.valueOf(rowNum++));
				vo.setAplnRefNo(CommonUtility.deNull(rs.getString("WA_REF_NBR")));
				vo.setAplnStatus(CommonUtility.deNull(rs.getString("rec_status")));
				vo.setEdoEsnNumber(CommonUtility.deNull(rs.getString("EDO_ESN_NBR")));
				vo.setVesselCargoInd(CommonUtility.deNull(rs.getString("type")));
				vo.setCargoDescription(CommonUtility.deNull(rs.getString("cargo_desc")));
				vo.setCargoTonnage(CommonUtility.deNull(rs.getString("cargo_billable_ton")));
				vo.setCargoStorageType(CommonUtility.deNull(rs.getString("storage_type")));
				String sDate = CommonUtility.deNull(rs.getString("start_date"));
				String eDate = CommonUtility.deNull(rs.getString("end_date"));
				String vvCd = CommonUtility.deNull(rs.getString("vv_cd"));
				vo.setVesselName(CommonUtility.deNull(rs.getString("vsl_name")));
				vo.setBillingCriteria(CommonUtility.deNull(rs.getString("billing_criteria")));
				vo.setPackages(CommonUtility.deNull(rs.getString("nbr_pkgs")));
				vo.setCommenceDate(sDate);
				vo.setEndDate(eDate);
				vo.setVesselVoyageCode(vvCd);
				vo.setPurpCode(CommonUtility.deNull(rs.getString("purp_cd")));
				vo.setCustomerEmail(CommonUtility.deNull(rs.getString("cust_email")));
				vo.setCustomerTelephone(CommonUtility.deNull(rs.getString("cust_tel")));
				vo.setEmailAlertIndicator(CommonUtility.deNull(rs.getString("email_alert_ind")));
				vo.setSmsAlertIndicator(CommonUtility.deNull(rs.getString("sms_alert_ind")));

				whVoArray.add(vo);
			}
			log.info("END: *** getWarehouseApplicationListByASNNubmer Result *****" + whVoArray.toString());
		} catch (NullPointerException e) {
			log.error("Exception getWarehouseApplicationListByASNNubmer :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getWarehouseApplicationListByASNNubmer :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getWarehouseApplicationListByASNNubmer DAO");
		}
		return whVoArray;
	}

	@Override
	public Boolean isExistWarehouseApplicationWithASNNubmer(String edoNbr) throws BusinessException {
		Boolean result = false;

		log.info("Inside EJB- isExistWarehouseApplicationWithASNNubmer() method..........");
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sql = new StringBuilder();

		try {
			log.info("START: isExistWarehouseApplicationWithASNNubmer Dao Start edoNbr:" + edoNbr);

			sql.append(" select * from wa_appln_details where edo_esn_nbr =:edoNbr");
			sql.append(" and  rec_status in ('S') and wa_ref_nbr not like '%E%'");
			// condition added to not check extended WA
			// sql.append(" and rec_status = 'S'" ); // condition removed 20/9 to allow even
			// WA extension

			paramMap.put("edoNbr", edoNbr);
			log.info("SQL Query: " + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				result = true;
				break;
			}
			log.info("END: *** isExistWarehouseApplicationWithASNNubmer Result *****" + result);
		} catch (NullPointerException e) {
			log.error("Exception isExistWarehouseApplicationWithASNNubmer :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception isExistWarehouseApplicationWithASNNubmer :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isExistWarehouseApplicationWithASNNubmer DAO");
		}
		log.info("isExistWarehouseApplicationWithASNNubmer(" + edoNbr + ")== : " + result);
		return result;
	}

	// package: ejb.sessionBeans.messenger-->MessengerEJB
	// method: sendMessage()
	@Override
	public boolean sendMessage(IMessageValueObject mVO) {
		// Is Email
		try {
			if (mVO instanceof EmailValueObject) {
				EmailValueObject emailVO = (EmailValueObject) mVO;
				Email emailObj = new Email();
				emailObj.setFrom(emailVO.getSenderAddress());
				emailObj.setFromName(emailVO.getSenderAddress());
				emailObj.setToList(Arrays.asList(emailVO.getRecipientAddress()));
				emailObj.setSubject(emailVO.getSubject());
				emailObj.setContent(emailVO.getMessage());
				emailObj.setEmailSvcUrl(commonServiceUrl);
				emailObj.setContentType("text/html");
				log.info("***emailObj*******" + emailObj.toString());
				CommonUtil.sendEmail(emailObj, "");
			} else if (mVO instanceof Sms) {
				Sms smsVO = (Sms) mVO;
				CommonUtil.sendSMS(smsVO);
			}
		} catch (Exception e) {
			log.error("Exception sendMessage : ", e);
		}

		return true;
	}

}
