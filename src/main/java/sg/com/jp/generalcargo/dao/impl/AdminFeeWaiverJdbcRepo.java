package sg.com.jp.generalcargo.dao.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.AdminFeeWaiverRepo;
import sg.com.jp.generalcargo.domain.AdminFeeWaiverValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("adminFeeWaiverRepo")
public class AdminFeeWaiverJdbcRepo implements AdminFeeWaiverRepo {
	private static final Log log = LogFactory.getLog(AdminFeeWaiverJdbcRepo.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	// ejb.sessionBeans.gbms.cargo.adminfeewaiver-->AdminFeeWaiverEJB
	public String logStatusGlobal = "Y";
	private final String dateFormat2 = "dd/MM/yyyy";
	private final String dateFormat = "dd/MM/yyyy HH:mm";

	public AdminFeeWaiverValueObject updateWaiverAdvice(AdminFeeWaiverValueObject adminFeeWaiverVO, String userID)
			throws BusinessException {
		SqlRowSet rs = null;
		String errSql = "";
		String apprSql = "";
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START:updateWaiverAdvice DAO adminFeeWaiverVO:" + adminFeeWaiverVO.toString() + "userID:"
					+ userID);
			errSql = "UPDATE WAIVER_ADVICE SET STATUS =:status WHERE ADVICE_ID=:adviceId ";
			apprSql = "  UPDATE WAIVER_ADVICE SET STATUS =:status, REMARKS =:remarks, APPROVED_BY=:approvedBy, APPROVED_AT=SYSDATE WHERE ADVICE_NBR=:adviceNbr";

			if (adminFeeWaiverVO.getWaiverStatus().equals("E") || adminFeeWaiverVO.getWaiverStatus().equals("P")) {
				log.info("Logging Error for Oscar call: " + adminFeeWaiverVO.getAdviceId());
				// Update status as E when there is error in calling webservice
				paramMap.put("status", adminFeeWaiverVO.getWaiverStatus());
				paramMap.put("adviceId", adminFeeWaiverVO.getAdviceId());
				log.info("SQL" + errSql);
				log.info(" ***paramMap *****" + paramMap.toString());
				namedParameterJdbcTemplate.update(errSql, paramMap);
			} else {
				paramMap.put("status", adminFeeWaiverVO.getWaiverStatus());
				paramMap.put("remarks", adminFeeWaiverVO.getApprovalRemarks());
				paramMap.put("approvedBy", adminFeeWaiverVO.getApprovedBy());
				paramMap.put("adviceNbr", adminFeeWaiverVO.getWanAdviceNbr());
				log.info("SQL" + apprSql);
				log.info(" ***paramMap *****" + paramMap.toString());
				namedParameterJdbcTemplate.update(apprSql, paramMap);
			}

			String sqlSS = "SELECT REF_NBR, REF_TYPE, REQUESTED_BY, VV_CD FROM WAIVER_ADVICE WHERE ADVICE_NBR=:adviceNbr"; // Fixed.
			// To fetch vv_cd too
			paramMap.put("adviceNbr", adminFeeWaiverVO.getWanAdviceNbr());
			log.info("SQL" + sqlSS);
			log.info(" ***paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlSS, paramMap);
			while (rs.next()) {
				adminFeeWaiverVO.setRefNumber(CommonUtility.deNull(rs.getString("REF_NBR")));
				adminFeeWaiverVO.setWaiverType(CommonUtility.deNull(rs.getString("REF_TYPE")));
				adminFeeWaiverVO.setRequestedBy((rs.getString("REQUESTED_BY")));
				String vvCd = rs.getString("VV_CD");
				vvCd = (vvCd == null) ? "" : vvCd.trim();
				adminFeeWaiverVO.setVvCd(vvCd);
			}
		} catch (Exception e) {
			log.info("Exception updateWaiverAdvice : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateWaiverAdvice DAO END");
		}
		return adminFeeWaiverVO;
	}

	private AdminFeeWaiverValueObject oscarWebserviceCall(int adviceId, String waiverRefNo, String waiverRefType,
			String userID) throws BusinessException {
		AdminFeeWaiverValueObject adminFeeWaiverVO = null;
		try {
			log.info("START: oscarWebserviceCall:adviceId" + adviceId + "waiverRefNo:" + waiverRefNo + "waiverRefType:"
					+ waiverRefType);
			// prepare advice data to raise oscar request for shipstore
			if (waiverRefType != null && waiverRefType.equals(ProcessChargeConst.SS_SSAD)) {
				// prepare waiver request
				adminFeeWaiverVO = captureSSAdminWaiverRequest(adviceId, waiverRefNo, userID, waiverRefType);
			} else if (waiverRefType != null && (waiverRefType.equals(ProcessChargeConst.MF_MADD)
					|| waiverRefType.equals(ProcessChargeConst.MF_MADM))) {
				// prepare waiver request
				adminFeeWaiverVO = captureMFAdminWaiverRequest(adviceId, waiverRefNo, userID, waiverRefType);
			}
			log.info(" adminFeeWaiverVO " + adminFeeWaiverVO.toString());
		} catch (Exception e) {
			log.info("Exception oscarWebserviceCall : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: oscarWebserviceCall Dao");
		}
		return adminFeeWaiverVO;
	}

	public AdminFeeWaiverValueObject captureMFAdminWaiverRequest(int adviceId, String mfBlNbr, String userID,
			String waiverRefType) throws BusinessException {
		SqlRowSet rs = null;
		StringBuilder sb = null;
		AdminFeeWaiverValueObject adminFeeVO = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: DAO captureMFAdminWaiverRequest adviceId:" + adviceId + "mfBlNbr:" + mfBlNbr + "userID:"
					+ userID + "waiverRefType:" + waiverRefType);
			sb = new StringBuilder();
			sb.append(
					" SELECT WA.ADVICE_ID, WA.ADVICE_NBR, WA.REF_NBR, WA.REF_TYPE, WA.REQUESTED_BY, WA.REQUESTED_AT, MF.BL_NBR, MF.WAIVE_REASON, ");
			sb.append(
					" MF.AMEND_CHARGED_TO, MF.MANIFEST_CREATE_CD, CO.CO_NM, CA.ADD_L1 || ',' || CA.ADD_L2 CUST_ADDR,  ");
			sb.append(
					" MF.VAR_NBR, VC.VSL_NM, VC.IN_VOY_NBR, VC.OUT_VOY_NBR, B.ATB_DTTM, B.ETB_DTTM, VC.VSL_BERTH_DTTM BTR_DTTM ");
			sb.append(
					" FROM WAIVER_ADVICE WA, MANIFEST_DETAILS MF, VESSEL_CALL VC, BERTHING B, CUST_ACCT CU, COMPANY_CODE CO, CUST_ADDRESS CA ");
			sb.append(
					" WHERE WA.REF_NBR=MF.BL_NBR AND MF.AMEND_CHARGED_TO=CU.ACCT_NBR AND CO.CO_CD=CU.CUST_CD AND CO.CO_CD=CA.CUST_CD  ");
			sb.append(
					" AND MF.VAR_NBR = VC.VV_CD AND VC.VV_CD=B.VV_CD AND B.SHIFT_IND=(SELECT MAX(SHIFT_IND) FROM BERTHING WHERE VV_CD=VC.VV_CD) ");
			sb.append("  AND MF.VAR_NBR = WA.VV_CD AND WA.ADVICE_ID=:adviceId ");

			adminFeeVO = new AdminFeeWaiverValueObject();
			Date requestedAt = null;
			Timestamp etb = null;
			Timestamp atb = null;
			Timestamp btr = null;
			Timestamp varDttm = null;

			paramMap.put("adviceId", String.valueOf(adviceId));
			log.info("SQL" + sb.toString());
			log.info(" ***paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				adminFeeVO.setAdviceId(CommonUtility.deNull(rs.getString("ADVICE_ID")));
				adminFeeVO.setWanAdviceNbr(CommonUtility.deNull(rs.getString("ADVICE_NBR")));
				adminFeeVO.setAsnNumber(CommonUtility.deNull(rs.getString("REF_NBR")));
				adminFeeVO.setRequestedBy(CommonUtility.deNull(rs.getString("REQUESTED_BY")));
				requestedAt = rs.getDate("REQUESTED_AT");
				adminFeeVO.setRequestedAt(CommonUtility.formatDateToStr(requestedAt, this.dateFormat));
				adminFeeVO.setRefNumber(CommonUtility.deNull(rs.getString("BL_NBR")));
				adminFeeVO.setWaiverCompany(CommonUtility.deNull(rs.getString("CO_NM")));
				adminFeeVO.setCompanyAddress(CommonUtility.deNull(rs.getString("CUST_ADDR")));
				adminFeeVO.setCompanyAccount(CommonUtility.deNull(rs.getString("AMEND_CHARGED_TO")));
				adminFeeVO.setWaiverReasons(CommonUtility.deNull(rs.getString("WAIVE_REASON")));
				adminFeeVO.setVarCode(CommonUtility.deNull(rs.getString("VAR_NBR")));
				adminFeeVO.setVesselVoy(CommonUtility.deNull(rs.getString("VSL_NM")) + " / "
						+ CommonUtility.deNull(rs.getString("IN_VOY_NBR")) + " / "
						+ CommonUtility.deNull(rs.getString("OUT_VOY_NBR")));
				atb = rs.getTimestamp("ATB_DTTM");
				etb = rs.getTimestamp("ETB_DTTM");
				btr = rs.getTimestamp("BTR_DTTM");
			}

			if (atb != null) {
				varDttm = atb;
			} else if (etb != null) {
				varDttm = etb;
			} else if (btr != null) {
				varDttm = btr;
			} else {
				varDttm = new Timestamp(System.currentTimeMillis());
			}

			adminFeeVO.setAtbEtbBtr(CommonUtility.formatDateToStr(varDttm, this.dateFormat));

			adminFeeVO = prepareTariffInformation(adminFeeVO, ProcessChargeConst.TARIFF_MAIN_ADMIN,
					ProcessChargeConst.TARIFF_MAIN_MANIFEST_AMENDMENT, varDttm);
			adminFeeVO.setCreateUserId(userID);

		} catch (Exception e) {
			log.info("Exception captureMFAdminWaiverRequest : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: captureMFAdminWaiverRequest Dao");
		}
		return adminFeeVO;
	}

	private AdminFeeWaiverValueObject prepareTariffInformation(AdminFeeWaiverValueObject adminFeeVO,
			String tariffMainCat, String tariffSubCat, Timestamp atb) throws BusinessException {
		SqlRowSet rs = null;
		StringBuilder sb = null;
		Map<String,Object> paramMap = new HashMap<String,Object>();
		try {
			log.info("START:prepareTariffInformation  adminFeeVO:" + adminFeeVO.toString() + "tariffMainCat:"
					+ tariffMainCat + "tariffSubCat:" + tariffSubCat + "atb" + atb);
			DecimalFormat decFormat = new DecimalFormat("0.00");
			sb = new StringBuilder();
			sb.append(
					" SELECT TARIFF_DESC, AMT_CHARGE, GST_CD FROM TARIFF_MAIN TM, TARIFF_TIER TT WHERE TM.TARIFF_CD = TT.TARIFF_CD AND TM.VERSION_NBR=TT.VERSION_NBR AND ");
			sb.append(
					" TM.CUST_CD IS NULL AND TT.CUST_CD IS NULL AND TM.TARIFF_MAIN_CAT_CD =:tariffMainCat AND TM.TARIFF_SUB_CAT_CD=:tariffSubCat AND TM.VERSION_NBR=(SELECT MAX(VERSION_NBR) FROM TARIFF_VERSION WHERE :atb >= EFF_START_DTTM) ");

			paramMap.put("tariffMainCat", tariffMainCat);
			paramMap.put("tariffSubCat", tariffSubCat);
			paramMap.put("atb", atb);
			log.info("SQL" + sb.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			String tariffDesc = null;
			Double unitRate = 0.00;
			String gstCd = null;
			while (rs.next()) {
				tariffDesc = CommonUtility.deNull(rs.getString("TARIFF_DESC"));
				unitRate = rs.getDouble("AMT_CHARGE");
				gstCd = CommonUtility.deNull(rs.getString("GST_CD"));
			}
			if (tariffSubCat.equals(ProcessChargeConst.TARIFF_MAIN_MANIFEST_AMENDMENT)) {
				adminFeeVO.setTariffDesc(tariffDesc + "(BL Nbr : " + adminFeeVO.getRefNumber() + ")");
			} else if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_SS_ADMIN_CHARGE)) {
				adminFeeVO.setTariffDesc(tariffDesc + "( Ship Store Ref Nbr : " + adminFeeVO.getRefNumber()
						+ "; ESN Nbr : " + adminFeeVO.getAsnNumber() + ")");
			}
			adminFeeVO.setUnitNbr("1");
			adminFeeVO.setUnitRate(decFormat.format(unitRate));
			sb.setLength(0);
			sb.append(" SELECT GST_CHARGE ");
			sb.append(
					" FROM GST_PARA, MISC_TYPE_CODE WHERE GST_PARA.REC_STATUS='A' AND MISC_TYPE_CODE.REC_STATUS='A' AND CAT_CD='FMAS_GCODE' AND FMAS_GST_CD=MISC_TYPE_CD ");
			sb.append(
					" AND TARIFF_GST_CD=:gstCd AND EFF_START_DTTM<=:effStart AND (EFF_END_DTTM>=:effEnd OR EFF_END_DTTM IS NULL) ");

			paramMap.put("gstCd", gstCd);
			paramMap.put("effStart", atb);
			paramMap.put("effEnd", atb);
			log.info("SQL" + sb.toString());
			log.info(" ***paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			Double gstCharge = 0.00;
			while (rs.next()) {
				gstCharge = rs.getDouble("GST_CHARGE");
			}

			Double totalAmount = 0.00;
			totalAmount = unitRate + unitRate * gstCharge / 100;
			adminFeeVO.setGst(decFormat.format(gstCharge));
			adminFeeVO.setTotalAmount(decFormat.format(totalAmount));

		} catch (Exception e) {
			log.info("Exception prepareTariffInformation : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: prepareTariffInformation Dao");
		}
		return adminFeeVO;
	}

	public AdminFeeWaiverValueObject captureSSAdminWaiverRequest(int adviceId, String ssEsnNbr, String userID,
			String waiverRefType) throws BusinessException {
		StringBuilder sqlSSRef = null;
		SqlRowSet rs = null;
		AdminFeeWaiverValueObject adminFeeVO = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: captureSSAdminWaiverRequest DAO adviceId:" + adviceId + "ssEsnNbr:" + ssEsnNbr + "userID:"
					+ userID + "waiverRefType" + waiverRefType);
			sqlSSRef = new StringBuilder();
			sqlSSRef.append(
					" SELECT WA.ADVICE_ID, WA.ADVICE_NBR, WA.REF_NBR, WA.REF_TYPE, WA.REQUESTED_BY, WA.REQUESTED_AT, SS.SS_REF_NBR, SS.SHIPPER_CD, SS.SHIPPER_NM, SS.SHIPPER_ADDR, ");
			sqlSSRef.append(
					" SS.ACCT_NBR, SS.ADMIN_FEE_WAIVER_REASON, ES.OUT_VOY_VAR_NBR, VC.VSL_NM, VC.IN_VOY_NBR, VC.OUT_VOY_NBR, B.ATB_DTTM, B.ETB_DTTM, VC.VSL_BERTH_DTTM BTR_DTTM ");
			sqlSSRef.append(
					"FROM WAIVER_ADVICE WA, SS_DETAILS SS, ESN ES, VESSEL_CALL VC, BERTHING B WHERE WA.REF_NBR=TO_CHAR(SS.ESN_ASN_NBR) AND SS.ESN_ASN_NBR = ES.ESN_ASN_NBR  ");
			sqlSSRef.append(
					" AND ES.OUT_VOY_VAR_NBR = VC.VV_CD AND VC.VV_CD=B.VV_CD AND B.SHIFT_IND=(SELECT MAX(SHIFT_IND) FROM BERTHING WHERE VV_CD=ES.OUT_VOY_VAR_NBR) AND WA.ADVICE_ID=:ADVICE_ID AND WA.REF_TYPE=:REF_TYPE  ");

			paramMap.put("ADVICE_ID", String.valueOf(adviceId));
			paramMap.put("REF_TYPE", waiverRefType);
			log.info("SQL" + sqlSSRef.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlSSRef.toString(), paramMap);
			adminFeeVO = new AdminFeeWaiverValueObject();
			Date requestedAt = null;
			Timestamp etb = null;
			Timestamp atb = null;
			Timestamp btr = null;
			Timestamp varDttm = null;

			if (rs.next()) {
				adminFeeVO.setAdviceId(CommonUtility.deNull(rs.getString("ADVICE_ID")));
				adminFeeVO.setWanAdviceNbr(CommonUtility.deNull(rs.getString("ADVICE_NBR")));
				adminFeeVO.setAsnNumber(CommonUtility.deNull(rs.getString("REF_NBR")));
				adminFeeVO.setRequestedBy(CommonUtility.deNull(rs.getString("REQUESTED_BY")));
				requestedAt = rs.getDate("REQUESTED_AT");
				adminFeeVO.setRequestedAt(CommonUtility.formatDateToStr(requestedAt, this.dateFormat2));
				adminFeeVO.setRefNumber(CommonUtility.deNull(rs.getString("SS_REF_NBR")));
				adminFeeVO.setWaiverCompany(CommonUtility.deNull(rs.getString("SHIPPER_NM")));
				adminFeeVO.setCompanyAddress(CommonUtility.deNull(rs.getString("SHIPPER_ADDR")));
				adminFeeVO.setCompanyAccount(CommonUtility.deNull(rs.getString("ACCT_NBR")));
				adminFeeVO.setWaiverReasons(CommonUtility.deNull(rs.getString("ADMIN_FEE_WAIVER_REASON")));
				adminFeeVO.setVarCode(CommonUtility.deNull(rs.getString("OUT_VOY_VAR_NBR")));
				adminFeeVO.setVesselVoy(CommonUtility.deNull(rs.getString("VSL_NM")) + " / "
						+ CommonUtility.deNull(rs.getString("IN_VOY_NBR")) + " / "
						+ CommonUtility.deNull(rs.getString("OUT_VOY_NBR")));
				atb = rs.getTimestamp("ATB_DTTM");
				etb = rs.getTimestamp("ETB_DTTM");
				btr = rs.getTimestamp("BTR_DTTM");
			}

			if (atb != null) {
				varDttm = atb;
			} else if (etb != null) {
				varDttm = etb;
			} else if (btr != null) {
				varDttm = btr;
			} else {
				varDttm = new Timestamp(System.currentTimeMillis());
			}
			adminFeeVO.setAtbEtbBtr(CommonUtility.formatDateToStr(varDttm, this.dateFormat));
			adminFeeVO = prepareTariffInformation(adminFeeVO, ProcessChargeConst.TARIFF_MAIN_ADMIN,
					ProcessChargeConst.TARIFF_SUB_SS_ADMIN_CHARGE, varDttm);
			adminFeeVO.setCreateUserId(userID);

		} catch (Exception e) {
			log.info("Exception captureSSAdminWaiverRequest : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: captureSSAdminWaiverRequest Dao");
		}
		return adminFeeVO;
	}

	@Override
	public AdminFeeWaiverValueObject invokeOscarWaiverRequest(int adviceId, String waiverRefNo, String userID,
			String waiverRefType) throws BusinessException {
		try {
			log.info("START: invokeOscarWaiverRequest DAO adviceId:" + adviceId + "waiverRefNo:" + waiverRefNo
					+ "userID:" + userID + "waiverRefType:" + waiverRefType);
			if (adviceId != 0) {
				return oscarWebserviceCall(adviceId, waiverRefNo, waiverRefType, userID);
			}
		} catch (Exception e) {
			log.info("Exception invokeOscarWaiverRequest : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: invokeOscarWaiverRequest Dao");
		}
		return null;
	}

	@Override
	public int captureWaiverAdviceRequest(String waiverRefNo, String userID, String waiverRefType, boolean resendReq,
			String adviceIdStr, String vvCd) throws BusinessException {
		int adviceId = 0;
		try {
			log.info("START:captureWaiverAdviceRequest DAO waiverRefNo:" + waiverRefNo + "userID:" + userID
					+ "waiverRefType:" + waiverRefType + "resendReq:" + resendReq + "adviceIdStr:" + adviceIdStr
					+ "vvCd" + vvCd);
			if (resendReq) {
				adviceId = Integer.valueOf(adviceIdStr);
			} else {
				adviceId = insertWaiverAdvice(waiverRefNo, userID, waiverRefType, vvCd); // Fixed. to pass vv_cd
			}
		} catch (BusinessException e) {
			log.info("Exception captureWaiverAdviceRequest : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception captureWaiverAdviceRequest : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: captureWaiverAdviceRequest Dao");
		}
		return adviceId;

	}

	private int getWaiverAdviseNumber() throws BusinessException {
		int waiverSeqNo = 0;
		String sql = "";
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: getWaiverAdviseNumber");
			sql = " SELECT WAIVER_ADVICE_SEQ.NEXTVAL COUNT FROM DUAL ";
			log.info("SQL" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				waiverSeqNo = rs.getInt("COUNT");
			}
		} catch (Exception e) {
			log.info("Exception getWaiverAdviseNumber : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getWaiverAdviseNumber waiverSeqNo:" + waiverSeqNo);
		}
		return waiverSeqNo;
	}

	private int insertWaiverAdvice(String refNbr, String userId, String waiverRefType, String vvCd)
			throws BusinessException {
		int adviceId = 0;
		StringBuilder sqlWaiIns = new StringBuilder();
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START:insertWaiverAdvice refNbr:" + refNbr + "userId:" + userId + "waiverRefType:" + waiverRefType
					+ "vvCd:" + vvCd);
			adviceId = getWaiverAdviseNumber();
			SimpleDateFormat formatter = new SimpleDateFormat("yyMM");
			String today = formatter.format(new java.util.Date());
			String adviceNbr = "WAN-" + today + "-" + adviceId;
			sqlWaiIns.append(
					"INSERT INTO WAIVER_ADVICE (ADVICE_ID, ADVICE_NBR, REF_NBR, REF_TYPE, STATUS,REQUESTED_BY,REQUESTED_AT,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, VV_CD) ");
			sqlWaiIns.append(
					" VALUES ( :adviceId,:adviceNbr,:refNbr,:waiverRefType,'P',:userId,SYSDATE,:userId,SYSDATE,:varCode )");
			String varCode = (vvCd == null) ? "" : vvCd.trim();
			log.info("SQL" + sqlWaiIns.toString());
			log.info(" ***paramMap *****" + paramMap.toString());
			
			paramMap.put("adviceId", String.valueOf(adviceId));
			paramMap.put("adviceNbr", adviceNbr);
			paramMap.put("refNbr", refNbr);
			paramMap.put("waiverRefType", waiverRefType);
			paramMap.put("userId", userId);
			paramMap.put("userId", userId);
			paramMap.put("varCode", varCode);
			log.info(" START:insertWaiverAdvice  DAO  SQL " + sqlWaiIns.toString());
			namedParameterJdbcTemplate.update(sqlWaiIns.toString(), paramMap);
			
			log.info("adviceId = " + adviceId);
		} catch (NullPointerException e) {
			log.info("Exception insertWaiverAdvice : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception insertWaiverAdvice : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertWaiverAdvice adviceId:" + adviceId);
		}
		return adviceId;

	}

}
