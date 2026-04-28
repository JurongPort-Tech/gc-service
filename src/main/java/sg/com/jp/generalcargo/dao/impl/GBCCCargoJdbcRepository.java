package sg.com.jp.generalcargo.dao.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.GBCCCargoRepository;
import sg.com.jp.generalcargo.domain.Berthing;
import sg.com.jp.generalcargo.domain.CargoOprSummInfoValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.GbccCargoOpenBalDet;
import sg.com.jp.generalcargo.domain.GbccCargoOpenBalDetId;
import sg.com.jp.generalcargo.domain.GbccCargoOpenBalVO;
import sg.com.jp.generalcargo.domain.GbccCargoOpr;
import sg.com.jp.generalcargo.domain.GbccCargoOprDet;
import sg.com.jp.generalcargo.domain.GbccCargoOprDetId;
import sg.com.jp.generalcargo.domain.GbccCargoOprId;
import sg.com.jp.generalcargo.domain.GbccCargoOprPlanDet;
import sg.com.jp.generalcargo.domain.GbccCargoOprPlanDetId;
import sg.com.jp.generalcargo.domain.GbccCargoOprPlanVO;
import sg.com.jp.generalcargo.domain.GbccCargoOprVO;
import sg.com.jp.generalcargo.domain.GbccCargoTallysheetDet;
import sg.com.jp.generalcargo.domain.GbccCargoTallysheetDetId;
import sg.com.jp.generalcargo.domain.GbccCargoTallysheetId;
import sg.com.jp.generalcargo.domain.GbccCargoTallysheetVO;
import sg.com.jp.generalcargo.domain.GbccCargoTimesheetAct;
import sg.com.jp.generalcargo.domain.GbccCargoTimesheetActId;
import sg.com.jp.generalcargo.domain.GbccCargoTimesheetEqrental;
import sg.com.jp.generalcargo.domain.GbccCargoTimesheetEqrentalId;
import sg.com.jp.generalcargo.domain.GbccCargoTimesheetId;
import sg.com.jp.generalcargo.domain.GbccCargoTimesheetVO;
import sg.com.jp.generalcargo.domain.GbccPara;
import sg.com.jp.generalcargo.domain.GbccRulePara;
import sg.com.jp.generalcargo.domain.GbccViewVvStevedore;
import sg.com.jp.generalcargo.domain.GbccViewVvStevedoreId;
import sg.com.jp.generalcargo.domain.GbccVslProd;
import sg.com.jp.generalcargo.domain.GbccVslProdId;
import sg.com.jp.generalcargo.domain.MiscTypeCode;
import sg.com.jp.generalcargo.domain.MiscTypeCodeId;
import sg.com.jp.generalcargo.domain.StevedoreCompany;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselCall;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.ConstantUtil;

@Repository("GBCCCargoRepository")
public class GBCCCargoJdbcRepository implements GBCCCargoRepository {
	private static final Log log = LogFactory.getLog(GBCCCargoJdbcRepository.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// sg.com.ntc.gbcc.hibernate.dao.impl-->GBCCCargoHibernateDaoImpl
	@Override
	public GbccCargoOprPlanVO getCargoOprPlanById(String vvCd, String stevCd, Date crDttm) throws BusinessException {
		GbccCargoOprPlanVO copr = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: getCargoOprPlanById  DAO: vvCd: " + vvCd + " stevCd: " + stevCd + " crDttm: " + crDttm);

			sb.append(
					"SELECT co.VV_CD vvCd, co.create_dttm createDttm, co.stev_co_cd stevCoCd,co.checker_nm checkerNm,");
			sb.append(" co.CHECKER_HP_NBR checkerHpNbr,co.TOT_DISC_TON totDiscTon, co.TOT_LOAD_TON totLoadTon,");
			sb.append(" co.CRANE_ONBOARD_NBR craneOnboardNbr, co.CRANE_ONBOARD_TON craneOnboardTon, ");
			sb.append(
					" co.MOBILE_CRANE_NBR mobileCraneNbr, co.MOBILE_CRANE_TON mobileCraneTon, co.FLOATING_CRANE_NBR floatingCraneNbr, co.FLOATING_CRANE_TON floatingCraneTon, co.GANGS_NBR gangsNbr, co.MAX_WRK_HATCH_NBR maxWrkHatchNbr, co.CTRL_HATCH_NBR ctrlHatchNbr, ");
			sb.append(
					" co.REMARKS remarks, co.HEAVY_LIFT_NBR heavyLiftNbr, co.HEAVY_LIFT_TON heavyLiftTon, co.CRANE_ONBOARD_TON_STR,");
			sb.append(
					" co.CRANE_ONBOARD_TON_REMARK, co.MOBILE_CRANE_TON_STR,  co.MOBILE_CRANE_TON_REMARK, co.FLOATING_CRANE_TON_STR, co.FLOATING_CRANE_TON_REMARK, co.HEAVY_LIFT_TON_STR,");
			sb.append(" co.HEAVY_LIFT_TON_REMARK, co.UNIT, ");
			sb.append(
					" co.LAST_MODIFY_USER_ID lastModifyUserId, co.LAST_MODIFY_DTTM lastModifyDttm, co.TANDEM_LIFT_IND tandemLiftInd, ");

			sb.append(
					"	vc.VV_CD vv_Cd, vc.VSL_NM, vc.IN_VOY_NBR, vc.OUT_VOY_NBR, vc.VV_STATUS_IND, vc.SHPG_SVC_CD, vc.SHPG_ROUTE_NBR,  ");
			sb.append(
					"	vc.ROUTE_NM, vc.VSL_OPR_CD, vc.BERTH_APPL_DTTM, vc.VSL_BERTH_DTTM, vc.VSL_ETD_DTTM, vc.VV_CLOSE_DTTM, ");
			sb.append(
					"	vc.PORT_FR, vc.PORT_TO, vc.ARRIVAL_DRAFT, vc.DEPARTURE_DRAFT, vc.BERTH_SIDE_IND, vc.CONTACT_NM, ");
			sb.append(
					"	vc.CONTACT_HOME_TEL, vc.CONTACT_OFF_TEL, vc.CONTACT_PGR, vc.CONTACT_FAX, vc.CNTR_DISC, vc.CNTR_LOAD, vc.UC_LOAD, vc.UC_DISC, vc.COB_DTTM, vc.BERTH_APPL_REM, ");
			sb.append(
					"	vc.BILL_MARINE_IND, vc.BILL_STEV_IND, vc.BILL_OTHER_IND, vc.BILL_ADMIN_IND, vc.CREATE_USER_ID, vc.CREATE_ACCT_NBR, vc.LAST_MODIFY_USER_ID vcLastModifyUserId, vc.LAST_MODIFY_DTTM vcLastModifyDttm, ");
			sb.append(
					"	vc.SCHEME, vc.TERMINAL, vc.VSL_LOA, vc.LOC_FR, vc.LOC_TO, vc.ALONGSIDE_DRAFT, vc.VSL_UNDER_TOW_IND, vc.CONTACT2_NM, ");
			sb.append(
					"	vc.CONTACT2_HOME_TEL, vc.CONTACT2_OFF_TEL, vc.CONTACT2_PGR, vc.CONTACT2_FAX, vc.TOTAL_CARGO_ONBOARD, vc.LOAD_DISPLACEMENT, vc.DG_CARGO_IND, vc.HLIFT, ");
			sb.append(
					"	vc.HLIFT_OVERWHARF, vc.HLIFT_OVERSIDE, vc.MOBILE_CR_DWT, vc.MOBILE_CR_SWL, vc.CNTR_VSL_IND, vc.GB_CLOSE_VSL_IND, vc.GB_CLOSE_BJ_IND, vc.GB_CLOSE_SHP_IND, ");
			sb.append(
					"	vc.CARGO_MODE, vc.BERTH_ALLOC_REM, vc.STORAGE_SPACE_IND, vc.BILL_ACCT_NBR, vc.GB_ARRIVAL_WAIVER_CD, vc.GB_ARRIVAL_WAIVER_REASON, vc.GB_DEPARTURE_WAIVER_CD, vc.GB_DEPARTURE_WAIVER_REASON, ");
			sb.append(
					"	vc.GB_BERT_BILL_IND, vc.BRIDGE_DIST_FROM_BOW, vc.VACATE_BERTH_IND, vc.MIXED_SCHEME_IND, vc.DISC_CM_CD, vc.LOAD_CM_CD, vc.DISC_BER_REM, vc.LOAD_BER_REM, ");
			sb.append(
					"	vc.CRG_DET_PROC, vc.DECLARANT_CUST_CD, vc.UC_DISC_CM_CD, vc.UC_LOAD_CM_CD, vc.UC_DISC_BER_REM, vc.UC_LOAD_BER_REM, vc.EST_LONG_CR_MOVE_NBR, vc.EST_THROUGHPUT_NBR, ");
			sb.append(
					"	vc.SENT_TO_PSA_IND, vc.ADVICE_DTTM, vc.ABBR_IN_VOY_NBR, vc.ABBR_OUT_VOY_NBR, vc.INCENTIVE_CLASS, vc.ISPS_LEVEL, vc.BILL_OPEN_TS_IND, vc.SMS_ALERT_REP1_IND, ");
			sb.append(
					"	vc.SMS_ALERT_REP2_IND, vc.TANDEM_LIFT_IND vsl_tandemLiftInd, vc.BILL_PROD_SURCHRG_IND, vc.ALLOC_PROD_PRD, vc.REEFER_PARTY, vc.PROTRUSION_IND, vc.FLOAT_CRANE_IND, vc.FIRST_SCHEME, ");
			sb.append(
					"	vc.GB_CLOSE_LCT_IND, vc.GB_CLOSE_LCT_DTTM, vc.GB_CLOSE_LCT_USER_ID, vc.STORAGE_DET_PROC, vc.GB_ARRIVAL_WAIVER_IND, vc.GB_ARRIVAL_WAIVER_AMOUNT, vc.GB_AUTO_CLOSE_BJ_IND, vc.GB_AUTO_CLOSE_SHP_IND, ");
			sb.append(
					"	vc.LAST_ATU_DTTM, vc.CARGO_WT, vc.TOTAL_CARGO_WT_MOBILE_CR_DWT, vc.LCT_MIN_CHRG_IND, vc.CEMENT_VSL_IND, vc.BTR_AFT_CLOSING1_DTTM, vc.NOM_IND, vc.NOM_PROCESS_IND, ");
			sb.append("	vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND, vc.PRINCIPAL_CARRIER_CD, vc.USE_SHP_CRN_IND, ");

			sb.append(
					"	vs.VV_CD stev_VvCd, vs.STEV_CO_CD, vs.STEV_CONTACT, vs.STEV_REMARKS, vs.STEV_REP, vs.LINENO, vs.LAST_MODIFY_USER_ID gbccView_lastModifyUserId, ");
			sb.append("	sc.stev_co_nm stevedoreCompanyName, ");
			sb.append("	cc.co_nm agentName ");
			sb.append(" FROM ");
			sb.append("	TOPS.vessel_call vc ");
			sb.append("INNER JOIN Company_Code cc ON ");
			sb.append("	(vc.vsl_opr_cd = cc.co_cd)");
			// " INNER JOIN berthing br ON (vc.vv_cd = br.vv_cd AND br.shift_ind=1) "
			// +
			// " INNER JOIN GBCC_V_VV_stevedore vs ON (vc.vv_cd = vs.vv_cd) "
			// +
			sb.append("INNER JOIN ( ");
			sb.append("SELECT ");
			sb.append("	vv_cd, ");
			sb.append("	v.stev_co_cd1 stev_co_cd, ");
			sb.append("	v.stev_contact1 stev_contact, ");
			sb.append("	v.stev_remarks1 stev_remarks, ");
			sb.append("	v.stev_rep1 stev_rep, ");
			sb.append("	1 AS lineno, ");
			sb.append("	last_modify_user_id ");
			sb.append("FROM ");
			sb.append("	TOPS.vv_stevedore V ");
			sb.append("WHERE ");
			sb.append("	stev_co_cd1 IS NOT NULL ");
			sb.append("UNION ");
			sb.append("SELECT ");
			sb.append("	vv_cd, ");
			sb.append("	v.stev_co_cd2 stev_co_cd, ");
			sb.append("	v.stev_contact2 stev_contact, ");
			sb.append("	v.stev_remarks2 stev_remarks, ");
			sb.append("	v.stev_rep2 stev_rep, ");
			sb.append("	2 AS lineno, ");
			sb.append("	last_modify_user_id ");
			sb.append("FROM ");
			sb.append("	TOPS.vv_stevedore V ");
			sb.append("WHERE ");
			sb.append("	stev_co_cd2 IS NOT NULL ");
			sb.append("UNION ");
			sb.append("SELECT ");
			sb.append("	vv_cd, ");
			sb.append("	v.stev_co_cd3 stev_co_cd, ");
			sb.append("	v.stev_contact3 stev_contact, ");
			sb.append("	v.stev_remarks3 stev_remarks, ");
			sb.append("	v.stev_rep3 stev_rep, ");
			sb.append("	3 AS lineno, ");
			sb.append("	last_modify_user_id ");
			sb.append("FROM ");
			sb.append("	TOPS.vv_stevedore V ");
			sb.append("WHERE ");
			sb.append("	stev_co_cd3 IS NOT NULL ) vs ON ");
			sb.append("(vc.vv_cd = vs.vv_cd) ");
			sb.append("INNER JOIN stevedore_company sc ON ");
			sb.append("(vs.stev_co_cd = sc.stev_co_cd) ");
			sb.append("LEFT JOIN GBCC_Cargo_Opr_Plan co ON ");
			sb.append("(vc.vv_cd = co.vv_cd ");
			sb.append("AND co.stev_co_cd = vs.stev_co_cd) ");
			sb.append("WHERE ");
			sb.append("vc.vv_cd = :pVvCd ");
			sb.append("AND vs.stev_co_cd = :pStevCoCd");

			if (crDttm != null)
				sb.append(" AND co.create_dttm = :pCreateDttm ");

			log.info("SQL::" + sb.toString());
			paramMap.put("pVvCd", vvCd.trim());
			paramMap.put("pStevCoCd", stevCd.trim());

			// SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			// String b = f.format(crDttm);
			if (crDttm != null) {
				Timestamp crTs = new Timestamp(crDttm.getTime());
				paramMap.put("pCreateDttm", crTs);
			}
			log.info("getCargoOprPlanById SQL::" + sb.toString() + " paramMap " + paramMap.toString());
			List<GbccCargoOprPlanVO> lst = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<GbccCargoOprPlanVO>(GbccCargoOprPlanVO.class));
			log.info("getCargoOprPlanById Result" + lst.toString());
			// Object[] obj = (Object[]) query.uniqueResult();
			copr = new GbccCargoOprPlanVO();
			if (!lst.isEmpty()) {
				for (GbccCargoOprPlanVO gbccCargoOprPlanVO : lst) {
					copr = gbccCargoOprPlanVO;

					if (vvCd != null && copr.getCreateDttm() != null) {
						copr.setCargoOprPlanDetVO(getCargoOprPlanDet(vvCd, copr.getCreateDttm()));
					}
				}
			} else {
				copr = new GbccCargoOprPlanVO();
			}

			Berthing firstBerthingVO = getFirstBerthing(vvCd);
			copr.setFirstBerthingVO(firstBerthingVO);

			Berthing lastBerthingVO = getLastBerthing(vvCd);
			copr.setLastBerthingVO(lastBerthingVO);

		} catch (Exception e) {
			log.info("Exception getCargoOprPlanById : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoOprPlanById DAO Result " + copr.toString());
		}
		return copr;
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	// Get Cargo Operation Plan Detail - Hatch No
	private List<GbccCargoOprPlanDet> getCargoOprPlanDet(String vvCd, Date crDttm) {
		List<GbccCargoOprPlanDet> returnList = new ArrayList<GbccCargoOprPlanDet>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START getCargoOprPlanDet: vvCd: " + vvCd + " crDttm: " + crDttm.toString());
			StringBuffer queryBuf = new StringBuffer();
			queryBuf.append("SELECT co.* from GBCC.GBCC_Cargo_Opr_Plan_Det co ");
			queryBuf.append(" WHERE co.VV_CD = :pVvCd and co.CREATE_DTTM = :pCreateDttm ");
			queryBuf.append(" ORDER BY co.HATCH_NBR");

			// SQLQuery query = this.getSession().createSQLQuery(queryBuf.toString());
			paramMap.put("pVvCd", vvCd.trim());

			Timestamp crTs = new Timestamp(crDttm.getTime());
			paramMap.put("pCreateDttm", crTs);
			log.info("getCargoOprPlanDet SQL::" + queryBuf.toString() + " paramMap " + paramMap.toString());
			// query.addEntity("co", GbccCargoOprPlanDet.class);

			List<GbccCargoOprPlanDet> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<GbccCargoOprPlanDet>(GbccCargoOprPlanDet.class));
			log.info("getCargoOprPlanDet Result" + lst.toString());
			GbccCargoOprPlanDet copr = null;

			if (lst != null) {
				GbccCargoOprPlanDetId id = null;
				for (GbccCargoOprPlanDet gbccCargoOprPlanDet : lst) {
					copr = new GbccCargoOprPlanDet();
					id = new GbccCargoOprPlanDetId();
					copr = gbccCargoOprPlanDet;
					id.setCreateDttm(gbccCargoOprPlanDet.getCreateDttm());
					id.setVvCd(gbccCargoOprPlanDet.getVvCd());
					id.setHatchNbr(gbccCargoOprPlanDet.getHatchNbr());
					copr.setId(id);

					returnList.add(copr);
				}

			}
		} catch (Exception e) {
			log.info("Exception getCargoOprPlanDet : ", e);
		} finally {
			log.info("END: getCargoOprPlanDet DAO Result " + returnList.size());
		}
		return returnList;
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	@Override
	public Berthing getFirstBerthing(String vvCd) throws BusinessException {
		return getFirstLastBerthing(vvCd, true, "etb");
	}

	@Override
	public Berthing getLastBerthing(String vvCd) throws BusinessException {
		return getFirstLastBerthing(vvCd, false, "etb");
	}

	private Berthing getFirstLastBerthing(String vvCd, boolean isFirst, String byWhichfield) throws BusinessException {
		Berthing br = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer queryBuf = new StringBuffer();
		// query.addEntity("br" , Berthing.class);
		try {
			log.info("START getFirstLastBerthing:  vvCd: " + vvCd + " isFirst: " + isFirst + " byWhichfield: "
					+ byWhichfield);

			queryBuf.append(" SELECT br.*");
			queryBuf.append(" FROM (");
			queryBuf.append("  SELECT * FROM TOPS.Berthing");
			queryBuf.append("  WHERE vv_cd = :pVvCd ");
			String field = "etb_dttm";
			if (byWhichfield.equalsIgnoreCase("etu"))
				field = "etu_dttm";

			if (isFirst)
				queryBuf.append("  ORDER BY " + field + " ASC ");
			else
				queryBuf.append("  ORDER BY " + field + " DESC ");

			queryBuf.append(" ) br WHERE rownum = 1 ");

			// SQLQuery query = this.getSession().createSQLQuery(queryBuf.toString());
			// working
			// SQLQuery query =
			// this.getSessionFactory().getCurrentSession().createSQLQuery(queryBuf.toString());
			paramMap.put("pVvCd", vvCd.trim());

			log.info("getFirstLastBerthing SQL:: " + queryBuf.toString() + " paramMap " + paramMap.toString());
			List<Berthing> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<Berthing>(Berthing.class));
			log.info("getFirstLastBerthing Result" + lst.toString());
			for (Berthing berthing : lst) {
				br = berthing;
			}
		} catch (Exception e) {
			log.info("Exception getFirstLastBerthing : ", e);
		} finally {
			log.info("END: getFirstLastBerthing DAO Result " + br.toString());
		}
		return br;
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl-->GBCCCargoHibernateDaoImpl
	@Override
	public boolean saveCargoOprPlan(GbccCargoOprPlanVO transientObject) throws BusinessException {
		boolean result;
		StringBuffer sb = new StringBuffer();
		Date dttm = new Date();
		try {
			log.info("START: saveCargoOprPlan  DAO GbccCargoOprPlanVO" + transientObject.toString());

			if (transientObject.getCreateDttm() == null) {
				transientObject.setCreateDttm(dttm);

				sb.append("INSERT ");
				sb.append("	INTO ");
				sb.append("	GBCC.GBCC_CARGO_OPR_PLAN (VV_CD, ");
				sb.append("	CREATE_DTTM, ");
				sb.append("	STEV_CO_CD, ");
				sb.append("	CHECKER_NM, ");
				sb.append("	CHECKER_HP_NBR, ");
				sb.append("	TOT_DISC_TON, ");
				sb.append("	TOT_LOAD_TON, ");
				sb.append("	CRANE_ONBOARD_NBR, ");
				sb.append("	CRANE_ONBOARD_TON, ");
				sb.append("	MOBILE_CRANE_NBR, ");
				sb.append("	MOBILE_CRANE_TON, ");
				sb.append("	FLOATING_CRANE_NBR, ");
				sb.append("	FLOATING_CRANE_TON, ");
				sb.append("	TANDEM_LIFT_IND, ");
				sb.append("	GANGS_NBR, ");
				sb.append("	MAX_WRK_HATCH_NBR, ");
				sb.append("	CTRL_HATCH_NBR, ");
				sb.append("	LAST_MODIFY_USER_ID, ");
				sb.append("	LAST_MODIFY_DTTM, ");
				sb.append("	REMARKS, ");
				sb.append("	HEAVY_LIFT_NBR, ");
				sb.append("	HEAVY_LIFT_TON, ");
				sb.append("	CRANE_ONBOARD_TON_STR, ");
				sb.append("	CRANE_ONBOARD_TON_REMARK, ");
				sb.append("	MOBILE_CRANE_TON_STR, ");
				sb.append("	MOBILE_CRANE_TON_REMARK, ");
				sb.append("	FLOATING_CRANE_TON_STR, ");
				sb.append("	FLOATING_CRANE_TON_REMARK, ");
				sb.append("	HEAVY_LIFT_TON_STR, ");
				sb.append("	HEAVY_LIFT_TON_REMARK, ");
				sb.append("	UNIT) ");
				sb.append("VALUES(:vv_Cd, ");
				sb.append(":createDttm, ");
				sb.append(":stevCoCd, ");
				sb.append(":checkerNm, ");
				sb.append(":checkerHpNbr, ");
				sb.append(":totDiscTon, ");
				sb.append(":totLoadTon, ");
				sb.append(":craneOnboardNbr, ");
				sb.append(":craneOnboardTon, ");
				sb.append(":mobileCraneNbr, ");
				sb.append(":mobileCraneTon, ");
				sb.append(":floatingCraneNbr, ");
				sb.append(":floatingCraneTon, ");
				sb.append(":tandemLiftInd, ");
				sb.append(":gangsNbr, ");
				sb.append(":maxWrkHatchNbr, ");
				sb.append(":ctrlHatchNbr, ");
				sb.append(":lastModifyUserId, ");
				sb.append("SYSDATE, ");
				sb.append(":remarks, ");
				sb.append(":heavyLiftNbr, ");
				sb.append(":heavyLiftTon, ");
				sb.append(":craneOnboardTonStr, ");
				sb.append(":craneOnboardTonRemark, ");
				sb.append(":mobileCraneTonStr, ");
				sb.append(":mobileCraneTonRemark, ");
				sb.append(":floatingCraneTonStr, ");
				sb.append(":floatingCraneTonRemark, ");
				sb.append(":heavyLiftTonStr, ");
				sb.append(":heavyLiftTonRemark, ");
				sb.append(":unit)");

			} else {
				sb = new StringBuffer();

				sb.append("UPDATE ");
				sb.append("	GBCC.GBCC_CARGO_OPR_PLAN ");
				sb.append("SET ");
				sb.append("	STEV_CO_CD = :stevCoCd, ");
				sb.append("	CHECKER_NM = :checkerNm, ");
				sb.append("	CHECKER_HP_NBR = :checkerHpNbr, ");
				sb.append("	TOT_DISC_TON = :totDiscTon, ");
				sb.append("	TOT_LOAD_TON = :totLoadTon, ");
				sb.append("	CRANE_ONBOARD_NBR = :craneOnboardNbr, ");
				sb.append("	CRANE_ONBOARD_TON = :craneOnboardTon, ");
				sb.append("	MOBILE_CRANE_NBR = :mobileCraneNbr, ");
				sb.append("	MOBILE_CRANE_TON = :mobileCraneTon, ");
				sb.append("	FLOATING_CRANE_NBR = :floatingCraneNbr, ");
				sb.append("	FLOATING_CRANE_TON = :floatingCraneTon, ");
				sb.append("	TANDEM_LIFT_IND = :tandemLiftInd, ");
				sb.append("	GANGS_NBR = :gangsNbr, ");
				sb.append("	MAX_WRK_HATCH_NBR = :maxWrkHatchNbr, ");
				sb.append("	CTRL_HATCH_NBR = :ctrlHatchNbr, ");
				sb.append("	LAST_MODIFY_USER_ID = :lastModifyUserId, ");
				sb.append("	LAST_MODIFY_DTTM = SYSDATE, ");
				sb.append("	REMARKS = :remarks, ");
				sb.append("	HEAVY_LIFT_NBR = :heavyLiftNbr, ");
				sb.append("	HEAVY_LIFT_TON = :heavyLiftTon, ");
				sb.append("	CRANE_ONBOARD_TON_STR = :craneOnboardTonStr, ");
				sb.append("	CRANE_ONBOARD_TON_REMARK = :craneOnboardTonRemark, ");
				sb.append("	MOBILE_CRANE_TON_STR = :mobileCraneTonStr, ");
				sb.append("	MOBILE_CRANE_TON_REMARK = :mobileCraneTonRemark, ");
				sb.append("	FLOATING_CRANE_TON_STR = :floatingCraneTonStr, ");
				sb.append("	FLOATING_CRANE_TON_REMARK = :floatingCraneTonRemark, ");
				sb.append("	HEAVY_LIFT_TON_STR = :heavyLiftTonStr, ");
				sb.append("	HEAVY_LIFT_TON_REMARK = :heavyLiftTonRemark, ");
				sb.append("	UNIT = :unit ");
				sb.append("WHERE ");
				sb.append("	VV_CD = :vv_Cd ");
				sb.append("	AND CREATE_DTTM = :createDttm");

			}

			transientObject.setLastModifyDttm(dttm);

			transientObject.setAuditUserId(transientObject.getLastModifyUserId());
			transientObject.setFnType(ConstantUtil.AUDIT_FNTYPE_CARGOOPS);
			transientObject.setFnsubType(ConstantUtil.AUDIT_FNSUBTYPE_CARGOOPRPLAN_UPDATE);
			transientObject.setTableNm(ConstantUtil.TABLE_GBCCCARGOOPRPLAN);
			transientObject.setKeyType1(ConstantUtil.AUDIT_KEYID_VVCD);
			transientObject.setKeyVal1(transientObject.getId().getVvCd());

			String fieldvalue = ConstantUtil.AUDIT_KEYID_VVCD + "=" + transientObject.getId().getVvCd();
			transientObject.setFieldNewValue(fieldvalue);
			transientObject.setFieldOldValue(fieldvalue);

			log.info("saveCargoOprPlan SQL  " + sb.toString() + " GbccCargoOprPlanVO" + transientObject.toString());
			int count = namedParameterJdbcTemplate.update(sb.toString(),
					new BeanPropertySqlParameterSource(transientObject));
			if (count == 0) {
				log.info("not amended");
			}

			result = true;
			log.info("saveCargoOprPlan Result: "+result);
		} catch (Exception e) {
			result = false;
			log.info("Exception saveCargoOprPlan : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: saveCargoOprPlan DAO ");
		}
		return result;
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	@Override
	public void addCargoOprPlanDet(GbccCargoOprPlanDet transientObject) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String vvCd = "";
		StringBuffer queryBuf = new StringBuffer();
		try {
			log.info("START: addCargoOprPlanDet  DAO GbccCargoOprPlanDet: " + transientObject.toString());
			Date dttm = new Date();
			transientObject.setLastModifyDttm(dttm);
			transientObject.setVvCd(transientObject.getId().getVvCd());
			transientObject.setCreateDttm(transientObject.getId().getCreateDttm());
			transientObject.setHatchNbr(transientObject.getId().getHatchNbr());
			String modifyUserId = transientObject.getLastModifyUserId();
			transientObject.setAuditUserId(modifyUserId);
			transientObject.setFnType(ConstantUtil.AUDIT_FNTYPE_CARGOOPS);
			transientObject.setFnsubType(ConstantUtil.AUDIT_FNSUBTYPE_CARGOOPRPLANDET_ADD);
			transientObject.setTableNm(ConstantUtil.TABLE_GBCCCARGOOPRPLANDET);
			transientObject.setKeyType1(ConstantUtil.AUDIT_KEYID_VVCD);
			transientObject.setKeyVal1(transientObject.getId().getVvCd());

			String fieldvalue = ConstantUtil.AUDIT_KEYID_VVCD + "=" + transientObject.getId().getVvCd();
			transientObject.setFieldNewValue(fieldvalue);
			transientObject.setFieldOldValue(fieldvalue);

			queryBuf.append("SELECT co.VV_CD from GBCC.GBCC_Cargo_Opr_Plan_Det co ");
			queryBuf.append(" WHERE co.VV_CD = :pVvCd and co.CREATE_DTTM = :pCreateDttm ");
			queryBuf.append(" ORDER BY co.HATCH_NBR");

			paramMap.put("pVvCd", transientObject.getVvCd());
			paramMap.put("pCreateDttm", transientObject.getCreateDttm());

			log.info("SQL::" + queryBuf.toString() + " paramMap " + paramMap.toString());

			List<String> lst = namedParameterJdbcTemplate.queryForList(queryBuf.toString(), paramMap, String.class);
			log.info("addCargoOprPlanDet Result" + lst.toString());
			for (int i = 0; i < lst.size(); i++) {
				vvCd = (String) lst.get(i);
			}

			StringBuffer sb = new StringBuffer();

			if (vvCd.equalsIgnoreCase("")) {

				sb.append("INSERT ");
				sb.append("	INTO ");
				sb.append("	GBCC.GBCC_CARGO_OPR_PLAN_DET (VV_CD, ");
				sb.append("	CREATE_DTTM, ");
				sb.append("	HATCH_NBR, ");
				sb.append("	DISC_TON, ");
				sb.append("	LOAD_TON, ");
				sb.append("	LAST_MODIFY_USER_ID, ");
				sb.append("	LAST_MODIFY_DTTM, ");
				sb.append("	REMARK) ");
				sb.append("VALUES(:vvCd, ");
				sb.append(":createDttm, ");
				sb.append(":hatchNbr, ");
				sb.append(":discTon, ");
				sb.append(":loadTon, ");
				sb.append(":lastModifyUserId, ");
				sb.append("SYSDATE, ");
				sb.append(":remark)");

			} else {
				sb = new StringBuffer();

				sb.append("UPDATE ");
				sb.append("	GBCC.GBCC_CARGO_OPR_PLAN_DET ");
				sb.append("SET ");
				sb.append("	DISC_TON = :discTon, ");
				sb.append("	LOAD_TON = :loadTon, ");
				sb.append("	LAST_MODIFY_USER_ID = :lastModifyUserId, ");
				sb.append("	LAST_MODIFY_DTTM = SYSDATE, ");
				sb.append("	REMARK = :remark ");
				sb.append("WHERE ");
				sb.append("	VV_CD = :vvCd ");
				sb.append("	AND CREATE_DTTM = :createDttm ");
				sb.append("	AND HATCH_NBR = :hatchNbr");
			}

			log.info("addCargoOprPlanDet SQL  " + sb.toString() + " GbccCargoOprPlanDet" + transientObject.toString());
			int count = namedParameterJdbcTemplate.update(sb.toString(),
					new BeanPropertySqlParameterSource(transientObject));
			if (count == 0) {
				log.info("not inserted");
			}
			log.info("addCargoOprPlanDet Result: " + count);
		} catch (Exception e) {
			log.info("Exception addCargoOprPlanDet : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: addCargoOprPlanDet DAO ");
		}
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	@Override
	public TableResult getCargoOprPlan(String CustCode, String sortBy, Criteria criteria, Boolean needAllData)
			throws BusinessException {
		return getCargoOprPlan("", CustCode, sortBy, criteria, needAllData);
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	private TableResult getCargoOprPlan(String vvCd, String CustCode, String sortBy, Criteria criteria,
			Boolean needAllData) throws BusinessException {
		return getCargoOprPlan(vvCd, CustCode, sortBy, criteria, needAllData, "", "", "");
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	private TableResult getCargoOprPlan(String vvCd, String CustCode, String sortBy, Criteria criteria,
			Boolean needAllData, String ETBFrom, String ETBTo, String listAllChk) throws BusinessException {
		String vvStatus_Ind_Query = "";
		StringBuffer sb = new StringBuffer();
		String sql = "";
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getCargoOprPlan  DAOP: vvCd:" + vvCd + " " + "sortBy  " + sortBy + " ETBFrom " + ETBFrom
					+ " ETBTo " + ETBTo + " listAllChk" + listAllChk);
			if ("".equalsIgnoreCase(listAllChk)) {
				vvStatus_Ind_Query = "('AL','BR')";
			} else {
				vvStatus_Ind_Query = "('AL','BR', 'UB')";
			}
			// sbStart.append(" SELECT COUNT(*) as count FROM ( ");
			// sbEnd.append(" )");

			if (!"JP".equalsIgnoreCase(CustCode.trim()) && (!"".equalsIgnoreCase(CustCode.trim()))) {
				// For JP Staff only
				sb = new StringBuffer();
				sb.append("SELECT ");
				sb.append(" row_number() OVER (ORDER BY br.ETB_DTTM ),  ");
				sb.append(
						" co.VV_CD vvCd, co.create_dttm createDttm, co.stev_co_cd stevCoCd, co.checker_nm checkerNm, co.CHECKER_HP_NBR checkerHpNbr, co.TOT_DISC_TON totDiscTon, co.TOT_LOAD_TON totLoadTon, co.CRANE_ONBOARD_NBR craneOnboardNbr, co.CRANE_ONBOARD_TON craneOnboardTon, ");
				sb.append(
						" co.MOBILE_CRANE_NBR mobileCraneNbr, co.MOBILE_CRANE_TON mobileCraneTon, co.FLOATING_CRANE_NBR floatingCraneNbr, co.FLOATING_CRANE_TON floatingCraneTon, co.GANGS_NBR gangsNbr, co.MAX_WRK_HATCH_NBR maxWrkHatchNbr, co.CTRL_HATCH_NBR ctrlHatchNbr, ");
				sb.append(
						" co.REMARKS remarks, co.HEAVY_LIFT_NBR heavyLiftNbr, co.HEAVY_LIFT_TON heavyLiftTon, co.CRANE_ONBOARD_TON_STR,");
				sb.append(
						" co.CRANE_ONBOARD_TON_REMARK, co.MOBILE_CRANE_TON_STR,  co.MOBILE_CRANE_TON_REMARK, co.FLOATING_CRANE_TON_STR, co.FLOATING_CRANE_TON_REMARK, co.HEAVY_LIFT_TON_STR,");
				sb.append(" co.HEAVY_LIFT_TON_REMARK, co.UNIT, ");
				sb.append(
						" co.LAST_MODIFY_USER_ID lastModifyUserId, co.LAST_MODIFY_DTTM lastModifyDttm, co.TANDEM_LIFT_IND tandemLiftInd, ");

				sb.append(
						"	vc.VV_CD vv_Cd, vc.VSL_NM, vc.IN_VOY_NBR, vc.OUT_VOY_NBR, vc.VV_STATUS_IND, vc.SHPG_SVC_CD, vc.SHPG_ROUTE_NBR,  ");
				sb.append(
						"	vc.ROUTE_NM, vc.VSL_OPR_CD, vc.BERTH_APPL_DTTM, vc.VSL_BERTH_DTTM, vc.VSL_ETD_DTTM, vc.VV_CLOSE_DTTM, ");
				sb.append(
						"	vc.PORT_FR, vc.PORT_TO, vc.ARRIVAL_DRAFT, vc.DEPARTURE_DRAFT, vc.BERTH_SIDE_IND, vc.CONTACT_NM, ");
				sb.append(
						"	vc.CONTACT_HOME_TEL, vc.CONTACT_OFF_TEL, vc.CONTACT_PGR, vc.CONTACT_FAX, vc.CNTR_DISC, vc.CNTR_LOAD, vc.UC_LOAD, vc.UC_DISC, vc.COB_DTTM, vc.BERTH_APPL_REM, ");
				sb.append(
						"	vc.BILL_MARINE_IND, vc.BILL_STEV_IND, vc.BILL_OTHER_IND, vc.BILL_ADMIN_IND, vc.CREATE_USER_ID, vc.CREATE_ACCT_NBR, vc.LAST_MODIFY_USER_ID vcLastModifyUserId, vc.LAST_MODIFY_DTTM vcLastModifyDttm, ");
				sb.append(
						"	vc.SCHEME, vc.TERMINAL, vc.VSL_LOA, vc.LOC_FR, vc.LOC_TO, vc.ALONGSIDE_DRAFT, vc.VSL_UNDER_TOW_IND, vc.CONTACT2_NM, ");
				sb.append(
						"	vc.CONTACT2_HOME_TEL, vc.CONTACT2_OFF_TEL, vc.CONTACT2_PGR, vc.CONTACT2_FAX, vc.TOTAL_CARGO_ONBOARD, vc.LOAD_DISPLACEMENT, vc.DG_CARGO_IND, vc.HLIFT, ");
				sb.append(
						"	vc.HLIFT_OVERWHARF, vc.HLIFT_OVERSIDE, vc.MOBILE_CR_DWT, vc.MOBILE_CR_SWL, vc.CNTR_VSL_IND, vc.GB_CLOSE_VSL_IND, vc.GB_CLOSE_BJ_IND, vc.GB_CLOSE_SHP_IND, ");
				sb.append(
						"	vc.CARGO_MODE, vc.BERTH_ALLOC_REM, vc.STORAGE_SPACE_IND, vc.BILL_ACCT_NBR, vc.GB_ARRIVAL_WAIVER_CD, vc.GB_ARRIVAL_WAIVER_REASON, vc.GB_DEPARTURE_WAIVER_CD, vc.GB_DEPARTURE_WAIVER_REASON, ");
				sb.append(
						"	vc.GB_BERT_BILL_IND, vc.BRIDGE_DIST_FROM_BOW, vc.VACATE_BERTH_IND, vc.MIXED_SCHEME_IND, vc.DISC_CM_CD, vc.LOAD_CM_CD, vc.DISC_BER_REM, vc.LOAD_BER_REM, ");
				sb.append(
						"	vc.CRG_DET_PROC, vc.DECLARANT_CUST_CD, vc.UC_DISC_CM_CD, vc.UC_LOAD_CM_CD, vc.UC_DISC_BER_REM, vc.UC_LOAD_BER_REM, vc.EST_LONG_CR_MOVE_NBR, vc.EST_THROUGHPUT_NBR, ");
				sb.append(
						"	vc.SENT_TO_PSA_IND, vc.ADVICE_DTTM, vc.ABBR_IN_VOY_NBR, vc.ABBR_OUT_VOY_NBR, vc.INCENTIVE_CLASS, vc.ISPS_LEVEL, vc.BILL_OPEN_TS_IND, vc.SMS_ALERT_REP1_IND, ");
				sb.append(
						"	vc.SMS_ALERT_REP2_IND, vc.TANDEM_LIFT_IND vsl_tandemLiftInd, vc.BILL_PROD_SURCHRG_IND, vc.ALLOC_PROD_PRD, vc.REEFER_PARTY, vc.PROTRUSION_IND, vc.FLOAT_CRANE_IND, vc.FIRST_SCHEME, ");
				sb.append(
						"	vc.GB_CLOSE_LCT_IND, vc.GB_CLOSE_LCT_DTTM, vc.GB_CLOSE_LCT_USER_ID, vc.STORAGE_DET_PROC, vc.GB_ARRIVAL_WAIVER_IND, vc.GB_ARRIVAL_WAIVER_AMOUNT, vc.GB_AUTO_CLOSE_BJ_IND, vc.GB_AUTO_CLOSE_SHP_IND, ");
				sb.append(
						"	vc.LAST_ATU_DTTM, vc.CARGO_WT, vc.TOTAL_CARGO_WT_MOBILE_CR_DWT, vc.LCT_MIN_CHRG_IND, vc.CEMENT_VSL_IND, vc.BTR_AFT_CLOSING1_DTTM, vc.NOM_IND, vc.NOM_PROCESS_IND, ");
				sb.append("	vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND, vc.PRINCIPAL_CARRIER_CD, vc.USE_SHP_CRN_IND, ");

				sb.append(
						"	br.VV_CD berth_VvCd, br.SHIFT_IND, br.ETB_DTTM, br.ETU_DTTM, br.ATB_DTTM, br.ATU_DTTM, br.FIRST_DISC_DTTM, br.FIRST_LOAD_DTTM, br.COD_DTTM, br.COL_DTTM,  ");
				sb.append(
						"	br.BERTH_NBR, br.WHARF_MARK_FR, br.WHARF_MARK_TO, br.WHARF_SIDE_IND, br.LAST_MODIFY_USER_ID berth_lastModifyUserId, br.LAST_MODIFY_DTTM berth_lastModifyDttm, br.GB_COD_DTTM, br.GB_COL_DTTM, br.GB_FIRST_ACT_DTTM, br.GB_LAST_ACT_DTTM,  ");
				sb.append(
						"	br.GB_BCOD_DTTM, br.GB_BCOL_DTTM, br.HAUL_DIST, br.HAUL_DIRN, br.GANG_NBR, br.HATCH_NBR, br.DELAY_RSN_CD, br.REMARKS berthRemarks, br.TOT_GEN_CARGO_ACT, br.GB_FIRST_CARGO_ACT_DTTM, br.DEP_DRAFT_FORTH,  ");
				sb.append(
						"	br.GB_FIRST_DISC_DTTM, br.GB_FIRST_LOAD_DTTM, br.ACT_DRAFT_FORTH, br.ACT_DRAFT_AFT, br.ACT_WHARF_SIDE_IND, br.ACT_WHARF_MARK_FR, br.ACT_WHARF_MARK_TO, br.BERTH_REMARKS, br.COL_MODIFY_DTTM, br.DEP_DRAFT_AFT,  ");

				sb.append(
						"	vs.VV_CD stev_VvCd, vs.STEV_CO_CD, vs.STEV_CONTACT, vs.STEV_REMARKS, vs.STEV_REP, vs.LINENO, vs.LAST_MODIFY_USER_ID gbccView_lastModifyUserId, ");
				sb.append("	sc.stev_co_nm stevedoreCompanyName, ");
				sb.append("	cc.co_nm ");
				sb.append("FROM ");
				sb.append("	TOPS.vessel_call vc ");
				sb.append("INNER JOIN TOPS.Vessel_Pre_Ops vpo ON ");
				sb.append("	(vc.vv_cd = vpo.vv_cd) ");
				sb.append("INNER JOIN cargo_client_code ccc ON ");
				sb.append("	(vpo.cc_cd = ccc.cc_cd) ");
				sb.append("INNER JOIN TOPS.Company_Code cc ON ");
				sb.append("	(vc.vsl_opr_cd = cc.co_cd) ");
				sb.append("INNER JOIN ( ");
				sb.append("	SELECT ");
				sb.append("		b1.* ");
				sb.append("	FROM ");
				sb.append("		TOPS.berthing b1 ");
				sb.append("	INNER JOIN ( ");
				sb.append("		SELECT ");
				sb.append("			vv_cd, ");
				sb.append("			min(etb_dttm) min_etb ");
				sb.append("		FROM ");
				sb.append("			TOPS.berthing ");
				sb.append("		GROUP BY ");
				sb.append("			vv_cd ) b2 ON ");
				sb.append("		(b1.vv_cd = b2.vv_cd ");
				sb.append("		AND b1.etb_dttm = b2.min_etb) ) br ON ");
				sb.append("	(vc.vv_cd = br.vv_cd ) ");
				sb.append("INNER JOIN ( ");
				sb.append("	SELECT ");
				sb.append("		vv_cd, ");
				sb.append("		v.stev_co_cd1 stev_co_cd, ");
				sb.append("		v.stev_contact1 stev_contact, ");
				sb.append("		v.stev_remarks1 stev_remarks, ");
				sb.append("		v.stev_rep1 stev_rep, ");
				sb.append("		1 AS lineno, ");
				sb.append("		last_modify_user_id ");
				sb.append("	FROM ");
				sb.append("		TOPS.vv_stevedore V ");
				sb.append("	WHERE ");
				sb.append("		stev_co_cd1 IS NOT NULL ");
				sb.append("UNION ");
				sb.append("	SELECT ");
				sb.append("		vv_cd, ");
				sb.append("		v.stev_co_cd2 stev_co_cd, ");
				sb.append("		v.stev_contact2 stev_contact, ");
				sb.append("		v.stev_remarks2 stev_remarks, ");
				sb.append("		v.stev_rep2 stev_rep, ");
				sb.append("		2 AS lineno, ");
				sb.append("		last_modify_user_id ");
				sb.append("	FROM ");
				sb.append("		TOPS.vv_stevedore V ");
				sb.append("	WHERE ");
				sb.append("		stev_co_cd2 IS NOT NULL ");
				sb.append("UNION ");
				sb.append("	SELECT ");
				sb.append("		vv_cd, ");
				sb.append("		v.stev_co_cd3 stev_co_cd, ");
				sb.append("		v.stev_contact3 stev_contact, ");
				sb.append("		v.stev_remarks3 stev_remarks, ");
				sb.append("		v.stev_rep3 stev_rep, ");
				sb.append("		3 AS lineno, ");
				sb.append("		last_modify_user_id ");
				sb.append("	FROM ");
				sb.append("		TOPS.vv_stevedore V ");
				sb.append("	WHERE ");
				sb.append("		stev_co_cd3 IS NOT NULL ) vs ON ");
				sb.append("	(vc.vv_cd = vs.vv_cd) ");
				sb.append("INNER JOIN stevedore_company sc ON ");
				sb.append("	(vs.stev_co_cd = sc.stev_co_cd) ");
				sb.append("LEFT JOIN GBCC_Cargo_Opr_Plan co ON ");
				sb.append("	(vc.vv_cd = co.vv_cd ");
				sb.append("	AND co.stev_co_cd = vs.stev_co_cd) ");
				sb.append("LEFT JOIN ( ");
				sb.append("	SELECT ");
				sb.append("		var_nbr vv_cd, ");
				sb.append("		(sum(nvl(md.Gross_WT, 0))/ 1000) total_ton ");
				sb.append("	FROM ");
				sb.append("		GBMS.manifest_details md ");
				sb.append("	WHERE ");
				sb.append("		md.bl_status = 'A' ");
				sb.append("	GROUP BY ");
				sb.append("		var_nbr ) disc ON ");
				sb.append("	(vc.vv_cd = disc.vv_cd) ");
				sb.append("LEFT JOIN ( ");
				sb.append("	SELECT ");
				sb.append("		vv_cd, ");
				sb.append("		sum(total_ton1) AS total_ton ");
				sb.append("	FROM ");
				sb.append("		( ");
				sb.append("		SELECT ");
				sb.append("			out_voy_var_nbr vv_cd, ");
				sb.append("			(sum(nvl(d.ESN_WT, 0))/ 1000) total_ton1 ");
				sb.append("		FROM ");
				sb.append("			esn e ");
				sb.append("		INNER JOIN bk_details bk ON ");
				sb.append("			(e.bk_ref_nbr = bk.bk_ref_nbr) ");
				sb.append("		INNER JOIN esn_details d ON ");
				sb.append("			(e.esn_asn_nbr = d.esn_asn_nbr) ");
				sb.append("		WHERE ");
				sb.append("			e.esn_status != 'X' ");
				sb.append("			AND bk.bk_status != 'X' ");
				sb.append("			AND trans_type = 'E' ");
				sb.append("		GROUP BY ");
				sb.append("			out_voy_var_nbr ");
				sb.append("	UNION ");
				sb.append("		SELECT ");
				sb.append("			out_voy_var_nbr vv_cd, ");
				sb.append("			(sum(nvl(tesn.NOM_WT, 0))/ 1000) total_ton1 ");
				sb.append("		FROM ");
				sb.append("			GBMS.bk_details bk1 ");
				sb.append("		INNER JOIN esn e1 ON ");
				sb.append("			(bk1.bk_ref_nbr = e1.bk_ref_nbr) ");
				sb.append("		INNER JOIN tesn_jp_jp tesn ON ");
				sb.append("			(e1.esn_asn_nbr = tesn.esn_asn_nbr) ");
				sb.append("		WHERE ");
				sb.append("			bk1.bk_status != 'X' ");
				sb.append("			AND e1.esn_status != 'X' ");
				sb.append("			AND trans_type = 'A' ");
				sb.append("		GROUP BY ");
				sb.append("			out_voy_var_nbr ");
				sb.append("	UNION ");
				sb.append("		SELECT ");
				sb.append("			out_voy_var_nbr vv_cd, ");
				sb.append("			(sum(nvl(psa.GROSS_WT, 0))/ 1000) total_ton1 ");
				sb.append("		FROM ");
				sb.append("			GBMS.bk_details bk2 ");
				sb.append("		INNER JOIN esn e2 ON ");
				sb.append("			(bk2.bk_ref_nbr = e2.bk_ref_nbr) ");
				sb.append("		INNER JOIN tesn_psa_jp psa ON ");
				sb.append("			(e2.esn_asn_nbr = psa.esn_asn_nbr) ");
				sb.append("		WHERE ");
				sb.append("			bk2.bk_status != 'X' ");
				sb.append("			AND e2.esn_status != 'X' ");
				sb.append("			AND trans_type = 'C' ");
				sb.append("		GROUP BY ");
				sb.append("			out_voy_var_nbr) ");
				sb.append("	GROUP BY ");
				sb.append("		vv_cd ) load ON ");
				sb.append("	(vc.vv_cd = load.vv_cd) ");
				sb.append("LEFT JOIN ( ");
				sb.append("	SELECT ");
				sb.append("		disc_vv_cd vv_cd, ");
				sb.append("		(sum(nvl(cn1.Declr_Wt, 0))/ 1000) total_ton ");
				sb.append("	FROM ");
				sb.append("		TOPS.Cntr cn1 ");
				sb.append("	WHERE ");
				sb.append("		cn1.TXN_STATUS <> 'D' ");
				sb.append("	GROUP BY ");
				sb.append("		disc_vv_cd ) cntr_disc ON ");
				sb.append("	(vc.vv_cd = cntr_disc.vv_cd) ");
				sb.append("LEFT JOIN ( ");
				sb.append("	SELECT ");
				sb.append("		load_vv_cd vv_cd, ");
				sb.append("		(sum(nvl(cn2.Declr_Wt, 0))/ 1000) total_ton ");
				sb.append("	FROM ");
				sb.append("		TOPS.Cntr cn2 ");
				sb.append("	WHERE ");
				sb.append("		cn2.TXN_STATUS <> 'D' ");
				sb.append("	GROUP BY ");
				sb.append("		load_vv_cd ) cntr_load ON ");
				sb.append("	(vc.vv_cd = cntr_load.vv_cd) ");
				sb.append("WHERE ");
				sb.append("	vc.vv_status_ind IN " + vvStatus_Ind_Query);
				sb.append("	 AND vc.scheme NOT IN ('JBT', 'JWP') ");
				sb.append("	AND vpo.cc_cd NOT IN ('CV') ");
				sb.append("	AND br.BERTH_NBR NOT LIKE 'R%' ");
				sb.append("	AND vc.terminal = 'GB' ");
				sb.append(
						"	AND (nvl(disc.total_ton, 0) + nvl(cntr_disc.total_ton, 0) + nvl(load.total_ton, 0) + nvl(cntr_load.total_ton, 0)) > :pThreshold");

			} else {
				sb = new StringBuffer();
				sb.append("SELECT ");
				sb.append(" row_number() OVER (ORDER BY br.ETB_DTTM ),  ");
				sb.append(
						" co.VV_CD vvCd, co.create_dttm createDttm, co.stev_co_cd stevCoCd, co.checker_nm checkerNm, co.CHECKER_HP_NBR checkerHpNbr, co.TOT_DISC_TON totDiscTon, co.TOT_LOAD_TON totLoadTon, co.CRANE_ONBOARD_NBR craneOnboardNbr, co.CRANE_ONBOARD_TON craneOnboardTon, ");
				sb.append(
						" co.MOBILE_CRANE_NBR mobileCraneNbr, co.MOBILE_CRANE_TON mobileCraneTon, co.FLOATING_CRANE_NBR floatingCraneNbr, co.FLOATING_CRANE_TON floatingCraneTon, co.GANGS_NBR gangsNbr, co.MAX_WRK_HATCH_NBR maxWrkHatchNbr, co.CTRL_HATCH_NBR ctrlHatchNbr, ");
				sb.append(
						" co.REMARKS remarks, co.HEAVY_LIFT_NBR heavyLiftNbr, co.HEAVY_LIFT_TON heavyLiftTon, co.CRANE_ONBOARD_TON_STR,");
				sb.append(
						" co.CRANE_ONBOARD_TON_REMARK, co.MOBILE_CRANE_TON_STR,  co.MOBILE_CRANE_TON_REMARK, co.FLOATING_CRANE_TON_STR, co.FLOATING_CRANE_TON_REMARK, co.HEAVY_LIFT_TON_STR,");
				sb.append(" co.HEAVY_LIFT_TON_REMARK, co.UNIT, ");
				sb.append(
						" co.LAST_MODIFY_USER_ID lastModifyUserId, co.LAST_MODIFY_DTTM lastModifyDttm, co.TANDEM_LIFT_IND tandemLiftInd, ");

				sb.append(
						"	vc.VV_CD vv_Cd, vc.VSL_NM, vc.IN_VOY_NBR, vc.OUT_VOY_NBR, vc.VV_STATUS_IND, vc.SHPG_SVC_CD, vc.SHPG_ROUTE_NBR,  ");
				sb.append(
						"	vc.ROUTE_NM, vc.VSL_OPR_CD, vc.BERTH_APPL_DTTM, vc.VSL_BERTH_DTTM, vc.VSL_ETD_DTTM, vc.VV_CLOSE_DTTM, ");
				sb.append(
						"	vc.PORT_FR, vc.PORT_TO, vc.ARRIVAL_DRAFT, vc.DEPARTURE_DRAFT, vc.BERTH_SIDE_IND, vc.CONTACT_NM, ");
				sb.append(
						"	vc.CONTACT_HOME_TEL, vc.CONTACT_OFF_TEL, vc.CONTACT_PGR, vc.CONTACT_FAX, vc.CNTR_DISC, vc.CNTR_LOAD, vc.UC_LOAD, vc.UC_DISC, vc.COB_DTTM, vc.BERTH_APPL_REM, ");
				sb.append(
						"	vc.BILL_MARINE_IND, vc.BILL_STEV_IND, vc.BILL_OTHER_IND, vc.BILL_ADMIN_IND, vc.CREATE_USER_ID, vc.CREATE_ACCT_NBR, vc.LAST_MODIFY_USER_ID vcLastModifyUserId, vc.LAST_MODIFY_DTTM vcLastModifyDttm, ");
				sb.append(
						"	vc.SCHEME, vc.TERMINAL, vc.VSL_LOA, vc.LOC_FR, vc.LOC_TO, vc.ALONGSIDE_DRAFT, vc.VSL_UNDER_TOW_IND, vc.CONTACT2_NM, ");
				sb.append(
						"	vc.CONTACT2_HOME_TEL, vc.CONTACT2_OFF_TEL, vc.CONTACT2_PGR, vc.CONTACT2_FAX, vc.TOTAL_CARGO_ONBOARD, vc.LOAD_DISPLACEMENT, vc.DG_CARGO_IND, vc.HLIFT, ");
				sb.append(
						"	vc.HLIFT_OVERWHARF, vc.HLIFT_OVERSIDE, vc.MOBILE_CR_DWT, vc.MOBILE_CR_SWL, vc.CNTR_VSL_IND, vc.GB_CLOSE_VSL_IND, vc.GB_CLOSE_BJ_IND, vc.GB_CLOSE_SHP_IND, ");
				sb.append(
						"	vc.CARGO_MODE, vc.BERTH_ALLOC_REM, vc.STORAGE_SPACE_IND, vc.BILL_ACCT_NBR, vc.GB_ARRIVAL_WAIVER_CD, vc.GB_ARRIVAL_WAIVER_REASON, vc.GB_DEPARTURE_WAIVER_CD, vc.GB_DEPARTURE_WAIVER_REASON, ");
				sb.append(
						"	vc.GB_BERT_BILL_IND, vc.BRIDGE_DIST_FROM_BOW, vc.VACATE_BERTH_IND, vc.MIXED_SCHEME_IND, vc.DISC_CM_CD, vc.LOAD_CM_CD, vc.DISC_BER_REM, vc.LOAD_BER_REM, ");
				sb.append(
						"	vc.CRG_DET_PROC, vc.DECLARANT_CUST_CD, vc.UC_DISC_CM_CD, vc.UC_LOAD_CM_CD, vc.UC_DISC_BER_REM, vc.UC_LOAD_BER_REM, vc.EST_LONG_CR_MOVE_NBR, vc.EST_THROUGHPUT_NBR, ");
				sb.append(
						"	vc.SENT_TO_PSA_IND, vc.ADVICE_DTTM, vc.ABBR_IN_VOY_NBR, vc.ABBR_OUT_VOY_NBR, vc.INCENTIVE_CLASS, vc.ISPS_LEVEL, vc.BILL_OPEN_TS_IND, vc.SMS_ALERT_REP1_IND, ");
				sb.append(
						"	vc.SMS_ALERT_REP2_IND, vc.TANDEM_LIFT_IND vsl_tandemLiftInd, vc.BILL_PROD_SURCHRG_IND, vc.ALLOC_PROD_PRD, vc.REEFER_PARTY, vc.PROTRUSION_IND, vc.FLOAT_CRANE_IND, vc.FIRST_SCHEME, ");
				sb.append(
						"	vc.GB_CLOSE_LCT_IND, vc.GB_CLOSE_LCT_DTTM, vc.GB_CLOSE_LCT_USER_ID, vc.STORAGE_DET_PROC, vc.GB_ARRIVAL_WAIVER_IND, vc.GB_ARRIVAL_WAIVER_AMOUNT, vc.GB_AUTO_CLOSE_BJ_IND, vc.GB_AUTO_CLOSE_SHP_IND, ");
				sb.append(
						"	vc.LAST_ATU_DTTM, vc.CARGO_WT, vc.TOTAL_CARGO_WT_MOBILE_CR_DWT, vc.LCT_MIN_CHRG_IND, vc.CEMENT_VSL_IND, vc.BTR_AFT_CLOSING1_DTTM, vc.NOM_IND, vc.NOM_PROCESS_IND, ");
				sb.append("	vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND, vc.PRINCIPAL_CARRIER_CD, vc.USE_SHP_CRN_IND, ");

				sb.append(
						"	br.VV_CD berth_VvCd, br.SHIFT_IND, br.ETB_DTTM, br.ETU_DTTM, br.ATB_DTTM, br.ATU_DTTM, br.FIRST_DISC_DTTM, br.FIRST_LOAD_DTTM, br.COD_DTTM, br.COL_DTTM,  ");
				sb.append(
						"	br.BERTH_NBR, br.WHARF_MARK_FR, br.WHARF_MARK_TO, br.WHARF_SIDE_IND, br.LAST_MODIFY_USER_ID berth_lastModifyUserId, br.LAST_MODIFY_DTTM berth_lastModifyDttm, br.GB_COD_DTTM, br.GB_COL_DTTM, br.GB_FIRST_ACT_DTTM, br.GB_LAST_ACT_DTTM,  ");
				sb.append(
						"	br.GB_BCOD_DTTM, br.GB_BCOL_DTTM, br.HAUL_DIST, br.HAUL_DIRN, br.GANG_NBR, br.HATCH_NBR, br.DELAY_RSN_CD, br.REMARKS berthRemarks, br.TOT_GEN_CARGO_ACT, br.GB_FIRST_CARGO_ACT_DTTM, br.DEP_DRAFT_FORTH,  ");
				sb.append(
						"	br.GB_FIRST_DISC_DTTM, br.GB_FIRST_LOAD_DTTM, br.ACT_DRAFT_FORTH, br.ACT_DRAFT_AFT, br.ACT_WHARF_SIDE_IND, br.ACT_WHARF_MARK_FR, br.ACT_WHARF_MARK_TO, br.BERTH_REMARKS, br.COL_MODIFY_DTTM, br.DEP_DRAFT_AFT,  ");

				sb.append(
						"	vs.VV_CD stev_VvCd, vs.STEV_CO_CD, vs.STEV_CONTACT, vs.STEV_REMARKS, vs.STEV_REP, vs.LINENO, vs.LAST_MODIFY_USER_ID gbccView_lastModifyUserId, ");
				sb.append("	sc.stev_co_nm stevedoreCompanyName, ");
				sb.append("	cc.co_nm ");
				sb.append("FROM ");
				sb.append("	TOPS.vessel_call vc ");
				sb.append("INNER JOIN Vessel_Pre_Ops vpo ON ");
				sb.append("	(vc.vv_cd = vpo.vv_cd) ");
				sb.append("INNER JOIN TOPS.cargo_client_code ccc ON ");
				sb.append("	(vpo.cc_cd = ccc.cc_cd) ");
				sb.append("INNER JOIN TOPS.Company_Code cc ON ");
				sb.append("	(vc.vsl_opr_cd = cc.co_cd) ");
				sb.append("INNER JOIN ( ");
				sb.append("	SELECT ");
				sb.append("		b1.* ");
				sb.append("	FROM ");
				sb.append("		TOPS.berthing b1 ");
				sb.append("	INNER JOIN ( ");
				sb.append("		SELECT ");
				sb.append("			vv_cd, ");
				sb.append("			min(etb_dttm) min_etb ");
				sb.append("		FROM ");
				sb.append("			TOPS.berthing ");
				sb.append("		GROUP BY ");
				sb.append("			vv_cd ) b2 ON ");
				sb.append("		(b1.vv_cd = b2.vv_cd ");
				sb.append("		AND b1.etb_dttm = b2.min_etb) ) br ON ");
				sb.append("	(vc.vv_cd = br.vv_cd )");
				// " INNER JOIN Berthing br ON (vc.vv_cd = br.vv_cd AND br.shift_ind=1) "
				// +

				sb.append(" INNER JOIN ( ");
				sb.append("SELECT ");
				sb.append("	vv_cd, ");
				sb.append("	v.stev_co_cd1 stev_co_cd, ");
				sb.append("	v.stev_contact1 stev_contact, ");
				sb.append("	v.stev_remarks1 stev_remarks, ");
				sb.append("	v.stev_rep1 stev_rep, ");
				sb.append("	1 AS lineno, ");
				sb.append("	last_modify_user_id ");
				sb.append("FROM ");
				sb.append("	TOPS.vv_stevedore V ");
				sb.append("WHERE ");
				sb.append("	stev_co_cd1 IS NOT NULL ");
				sb.append("UNION ");
				sb.append("SELECT ");
				sb.append("	vv_cd, ");
				sb.append("	v.stev_co_cd2 stev_co_cd, ");
				sb.append("	v.stev_contact2 stev_contact, ");
				sb.append("	v.stev_remarks2 stev_remarks, ");
				sb.append("	v.stev_rep2 stev_rep, ");
				sb.append("	2 AS lineno, ");
				sb.append("	last_modify_user_id ");
				sb.append("FROM ");
				sb.append("	TOPS.vv_stevedore V ");
				sb.append("WHERE ");
				sb.append("	stev_co_cd2 IS NOT NULL ");
				sb.append("UNION ");
				sb.append("SELECT ");
				sb.append("	vv_cd, ");
				sb.append("	v.stev_co_cd3 stev_co_cd, ");
				sb.append("	v.stev_contact3 stev_contact, ");
				sb.append("	v.stev_remarks3 stev_remarks, ");
				sb.append("	v.stev_rep3 stev_rep, ");
				sb.append("	3 AS lineno, ");
				sb.append("	last_modify_user_id ");
				sb.append("FROM ");
				sb.append("	TOPS.vv_stevedore V ");
				sb.append("WHERE ");
				sb.append("	stev_co_cd3 IS NOT NULL ) vs ON ");
				sb.append("(vc.vv_cd = vs.vv_cd) ");
				sb.append("INNER JOIN stevedore_company sc ON ");
				sb.append("(vs.stev_co_cd = sc.stev_co_cd) ");
				sb.append("LEFT JOIN GBCC_Cargo_Opr_Plan co ON ");
				sb.append("(vc.vv_cd = co.vv_cd ");
				sb.append("AND co.stev_co_cd = vs.stev_co_cd) ");

				// " LEFT JOIN" +
				// " (" +
				// " SELECT " +
				// " vc.vv_cd, (sum(nvl(md.Gross_WT,0))/1000) + (sum(nvl(bd.ESN_WT,0))/1000) +
				// (sum(nvl(cn1.Declr_Wt,0))/1000) + (sum(nvl(cn2.Declr_Wt,0))/1000) TOTALTON "
				// +
				// " from vessel_call vc " +
				// " left join Manifest_Details md on (vc.vv_cd=md.var_nbr AND md.bl_status
				// ='A') "
				// +
				// " left join ESN be on (vc.vv_cd = be.out_voy_var_nbr and be.esn_status = 'A')
				// "
				// +
				// " left join ESN_Details bd on (be.esn_asn_nbr = bd.esn_asn_nbr) "
				// +
				// " left join Cntr cn1 on (vc.vv_cd = cn1.disc_vv_cd and cn1.TXN_STATUS <> 'D')
				// "
				// +
				// " left join Cntr cn2 on (vc.vv_cd = cn2.load_vv_cd and cn2.disc_vv_cd <>
				// vc.vv_cd AND cn2.TXN_STATUS <> 'D') "
				// +
				// " group by vc.vv_cd" +
				// " ) ton" +
				// " ON (vc.vv_cd = ton.vv_cd ) " +
				// TOTAL TONNaGE
				sb.append("LEFT JOIN ( ");
				sb.append("SELECT ");
				sb.append("	var_nbr vv_cd, ");
				sb.append("	(sum(nvl(md.Gross_WT, 0))/ 1000) total_ton ");
				sb.append("FROM ");
				sb.append("	GBMS.manifest_details md ");
				sb.append("WHERE ");
				sb.append("	md.bl_status = 'A' ");
				sb.append("GROUP BY ");
				sb.append("	var_nbr ) disc ON ");
				sb.append("(vc.vv_cd = disc.vv_cd) ");
				sb.append("LEFT JOIN (");
				// start Modified on 14/04/2010
				// " select out_voy_var_nbr vv_cd, (sum(nvl(d.ESN_WT,0))/1000) total_ton "
				// +
				// " from esn e " +
				// " inner join esn_details d on (e.esn_asn_nbr = d.esn_asn_nbr) "
				// +
				// " where e.esn_status ='A' group by out_voy_var_nbr "
				// +
				sb.append(" SELECT ");
				sb.append("	vv_cd, ");
				sb.append("	sum(total_ton1) AS total_ton ");
				sb.append("FROM ");
				sb.append("	( ");
				sb.append("	SELECT ");
				sb.append("		out_voy_var_nbr vv_cd, ");
				sb.append("		(sum(nvl(d.ESN_WT, 0))/ 1000) total_ton1 ");
				sb.append("	FROM ");
				sb.append("		GBMS.esn e ");
				sb.append("	INNER JOIN bk_details bk ON ");
				sb.append("		(e.bk_ref_nbr = bk.bk_ref_nbr) ");
				sb.append("	INNER JOIN esn_details d ON ");
				sb.append("		(e.esn_asn_nbr = d.esn_asn_nbr) ");
				sb.append("	WHERE ");
				sb.append("		e.esn_status != 'X' ");
				sb.append("		AND bk.bk_status != 'X' ");
				sb.append("		AND trans_type = 'E' ");
				sb.append("	GROUP BY ");
				sb.append("		out_voy_var_nbr ");
				sb.append("UNION ");
				sb.append("	SELECT ");
				sb.append("		out_voy_var_nbr vv_cd, ");
				sb.append("		(sum(nvl(tesn.NOM_WT, 0))/ 1000) total_ton1 ");
				sb.append("	FROM ");
				sb.append("		GBMS.bk_details bk1 ");
				sb.append("	INNER JOIN esn e1 ON ");
				sb.append("		(bk1.bk_ref_nbr = e1.bk_ref_nbr) ");
				sb.append("	INNER JOIN tesn_jp_jp tesn ON ");
				sb.append("		(e1.esn_asn_nbr = tesn.esn_asn_nbr) ");
				sb.append("	WHERE ");
				sb.append("		bk1.bk_status != 'X' ");
				sb.append("		AND e1.esn_status != 'X' ");
				sb.append("		AND trans_type = 'A' ");
				sb.append("	GROUP BY ");
				sb.append("		out_voy_var_nbr ");
				sb.append("UNION ");
				sb.append("	SELECT ");
				sb.append("		out_voy_var_nbr vv_cd, ");
				sb.append("		(sum(nvl(psa.GROSS_WT, 0))/ 1000) total_ton1 ");
				sb.append("	FROM ");
				sb.append("		GBMS.bk_details bk2 ");
				sb.append("	INNER JOIN esn e2 ON ");
				sb.append("		(bk2.bk_ref_nbr = e2.bk_ref_nbr) ");
				sb.append("	INNER JOIN tesn_psa_jp psa ON ");
				sb.append("		(e2.esn_asn_nbr = psa.esn_asn_nbr) ");
				sb.append("	WHERE ");
				sb.append("		bk2.bk_status != 'X' ");
				sb.append("		AND e2.esn_status != 'X' ");
				sb.append("		AND trans_type = 'C' ");
				sb.append("	GROUP BY ");
				sb.append("		out_voy_var_nbr) ");
				sb.append("GROUP BY ");
				sb.append("	vv_cd ) load ON ");
				sb.append("	(vc.vv_cd = load.vv_cd) ");
				sb.append("LEFT JOIN ( ");
				sb.append("	SELECT ");
				sb.append("		disc_vv_cd vv_cd, ");
				sb.append("		(sum(nvl(cn1.Declr_Wt, 0))/ 1000) total_ton ");
				sb.append("	FROM ");
				sb.append("		TOPS.Cntr cn1 ");
				sb.append("	WHERE ");
				sb.append("		cn1.TXN_STATUS <> 'D' ");
				sb.append("	GROUP BY ");
				sb.append("		disc_vv_cd ) cntr_disc ON ");
				sb.append("	(vc.vv_cd = cntr_disc.vv_cd) ");
				sb.append("LEFT JOIN ( ");
				sb.append("	SELECT ");
				sb.append("		load_vv_cd vv_cd, ");
				sb.append("		(sum(nvl(cn2.Declr_Wt, 0))/ 1000) total_ton ");
				sb.append("	FROM ");
				sb.append("		TOPS.Cntr cn2 ");
				sb.append("	WHERE ");
				sb.append("		cn2.TXN_STATUS <> 'D' ");
				sb.append("	GROUP BY ");
				sb.append("		load_vv_cd ) cntr_load ON ");
				sb.append("	(vc.vv_cd = cntr_load.vv_cd)");

				sb.append(" WHERE vc.vv_status_ind IN " + vvStatus_Ind_Query);
				sb.append(" AND vc.scheme NOT IN ('JBT', 'JWP') ");
				sb.append(" AND vpo.cc_cd NOT IN ('CV')");
				// " AND nvl(ccc.bulk_vsl_ind,'N') <> 'Y' " + Allow Bulk Vessel
				sb.append(" AND br.BERTH_NBR NOT LIKE 'R%' ");
				sb.append(" AND vc.terminal =  'GB' ");
				// " AND gbcc_fn_totaltonnage(vc.vv_cd) >= 0 " +
				// " AND ton.totalton > :pThreshold " +

				sb.append(
						"AND (nvl(disc.total_ton, 0) + nvl(cntr_disc.total_ton, 0) + nvl(load.total_ton, 0) + nvl(cntr_load.total_ton, 0)) > :pThreshold");
			}

			if (!"".equalsIgnoreCase(vvCd.trim())) {
				sb.append(" and vc.vv_cd = :pVvCd ");
			}
			if (!"JP".equalsIgnoreCase(CustCode.trim()) && (!"".equalsIgnoreCase(CustCode.trim()))) {
				// queryBuf.append(" and vc.vsl_opr_cd = :pCustCd "); Bhuvana
				// 12/01/2011
				sb.append(" and sc.co_cd = :pCustCd ");
			} else {
				if (!"".equalsIgnoreCase(ETBFrom) && !"".equalsIgnoreCase(ETBTo)) {
					sb.append(" and br.ETB_DTTM >=  :pETBFrom ");
					sb.append(" and br.ETB_DTTM <= :pETBTo ");
				}
			}

			if (!"".equalsIgnoreCase(sortBy.trim())) {
				sb.append(" ORDER BY ");
				sb.append(sortBy.trim());
			}
			log.info("SQL->" + sb.toString());
			long threshold = getGbccParaNbr(ConstantUtil.PARA_CD_GC_TON_THRESHOLD);
			paramMap.put("pThreshold", (int) threshold);

			if (!"".equalsIgnoreCase(vvCd.trim())) {
				paramMap.put("pVvCd", vvCd.trim());
			}

			if (!"JP".equalsIgnoreCase(CustCode.trim()) && (!"".equalsIgnoreCase(CustCode.trim()))) {
				paramMap.put("pCustCd", CustCode.trim());
			} else {

				if (!"".equalsIgnoreCase(ETBFrom) && !"".equalsIgnoreCase(ETBTo)) {

					SimpleDateFormat f = new SimpleDateFormat("ddMMyyyy hhmm");
					// String b = f.format(crDttm);
					Date etbFromDttm = null;
					Date etbToDttm = null;

					try {
						etbFromDttm = f.parse(ETBFrom + " 0000");
						etbToDttm = f.parse(ETBTo + " 2359");
					} catch (ParseException e) {
						log.info("getCargoOprPlan Exception", e);
					}

					paramMap.put("pETBFrom", new Timestamp(etbFromDttm.getTime()));
					paramMap.put("pETBTo", new Timestamp(etbToDttm.getTime()));
				}
			}

			// StringBuffer sb = new
			// StringBuffer("SELECT {co.*} FROM GBCC_Cargo_Opr co");
			// SQLQuery query =
			// this.getSession().createSQLQuery(sb.toString());
			// query.addEntity("co", GbccCargoOprPlan.class);
			// query.addEntity("vc", VesselCall.class);
			// query.addEntity("br", Berthing.class);
			// query.addEntity("vs", GbccViewVvStevedore.class);
			// query.addScalar("stev_co_nm", Hibernate.STRING);
			// query.addScalar("co_nm", Hibernate.STRING);

			// query.addJoin("vc" , "GbccCargoOpr.vesselCallVO");
			// query.addJoin("vc", "GbccCargoOpr.vesselCallVO");
			// query.addJoin("br", "GbccCargoOpr.firstBerthingVO");
			sql = sb.toString();

			if (!needAllData) {
				if (criteria.isPaginated()) {
					sql = CommonUtil.getPaginatedSql(sb.toString(), criteria.getStart(), criteria.getLimit());
				} else {
					sql = sb.toString();
				}

			} else {
				sql = sb.toString();
			}

			log.info("getCargoOprPlan SQL " + sb.toString() + " paramMap " + paramMap.toString());

			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate
						.queryForObject("SELECT COUNT(*) FROM (" + sb.toString() + ")", paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
			}
			List<GbccCargoOprPlanVO> lst = namedParameterJdbcTemplate.query(sql, paramMap,
					new BeanPropertyRowMapper<GbccCargoOprPlanVO>(GbccCargoOprPlanVO.class));

			log.info("getCargoOprPlan Result" + lst.toString());
			log.info("getCargoOprPlan list " + lst.size());
			GbccCargoOprPlanVO copr = null;

			if (lst != null) {
				// for (GbccCargoOprPlanVO gbccCargoOprPlanVO : lst) {
				// copr = new GbccCargoOprPlanVO();
				// copr = gbccCargoOprPlanVO;
				// returnList.add(copr);
				// }
				for (GbccCargoOprPlanVO gbccCargoOprPlanVO : lst) {
					copr = new GbccCargoOprPlanVO();
					copr = gbccCargoOprPlanVO;
					// returnList.add(copr);
					topsModel.put(copr);
				}
			} 

			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
		} catch (Exception e) {
			log.info("Exception getCargoOprPlan : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoOprPlan DAO ");
		}
		return tableResult;
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	public long getGbccParaNbr(String paraCd) {
		long paraVal = 0;
		try {
			log.info("getGbccParaNbr START: paraCd: " + paraCd);
			GbccPara oVO = getGbccParaById(paraCd);

			if (oVO != null) {
				paraVal = Long.parseLong(oVO.getValue());
			}

			log.info("getGbccParaNbr  " + paraVal);
		} catch (Exception e) {
			log.info("getGbccParaNbr Exception::", e);
		}
		return paraVal;
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	public GbccPara getGbccParaById(String paraCd) {
		GbccPara oVO = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("getGbccParaById START paraCd: " + paraCd);
			StringBuffer queryBuf = new StringBuffer();
			queryBuf.append(" SELECT pr.* ");
			queryBuf.append(" FROM GBCC.Gbcc_Para pr WHERE para_cd = :pParaCd");

			paramMap.put("pParaCd", paraCd.trim());
			log.info("getGbccParaById SQL::" + queryBuf.toString() + " paramMap " + paramMap.toString());
			List<GbccPara> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<GbccPara>(GbccPara.class));
			log.info("getGbccParaById Result" + lst.toString());
			for (GbccPara gbccPara : lst) {
				oVO = gbccPara;
			}

			log.info("getGbccParaById Result" + oVO.toString());
			// SQLQuery query = this.getSession().createSQLQuery(queryBuf.toString());
			// oVO = (GbccPara) query.uniqueResult();
		} catch (Exception e) {
			log.info("getGbccParaById Exception::", e);
		} finally {
			log.info("END: getGbccParaById DAO ");
		}
		return oVO;
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	@Override
	public TableResult getCargoOprPlan(String CustCode, String sortBy, Criteria criteria, Boolean needAllData,
			String ETBFrom, String ETBTo, String listAllChk) throws BusinessException {
		return getCargoOprPlan("", CustCode, sortBy, criteria, needAllData, ETBFrom, ETBTo, listAllChk);
	}

	// added by syazwani on 2/06/2021
	@Override
	public String getCompanyName(String coCd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		String companyName = "";
		try {
			log.info("START: getCompanyName coCd:" + coCd);
			sb.append(" SELECT CO_NM AS coNm FROM TOPS.COMPANY_CODE WHERE CO_CD LIKE :coCd ");
			paramMap.put("coCd", coCd);
			companyName = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, String.class);
			log.info("getCompanyName SQL: " + sb.toString() + " paramMap: " + paramMap.toString());
			log.info(" getCompanyName Result: "+companyName);
		} catch (Exception e) {
			log.info("Exception getCompanyName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getCompanyName");
		}
		return companyName;
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	@Override
	public List<MiscTypeCode> getMiscTypeCode(String catCd) throws BusinessException {
		StringBuffer queryBuf = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		List<MiscTypeCode> returnList = new ArrayList<MiscTypeCode>();
		try {
			log.info("START: getMiscTypeCode  DAO catCd: " + catCd);
			queryBuf.append(" SELECT mtc.*");
			queryBuf.append(" FROM ");
			queryBuf.append("  TOPS.Misc_Type_Code mtc");
			queryBuf.append("  WHERE rec_status = 'A'  AND cat_cd = :pCatCd ");
			queryBuf.append(" ORDER BY misc_type_cd ASC ");

			paramMap.put("pCatCd", catCd.trim());

			// StringBuffer queryBuf = new StringBuffer("SELECT {co.*} FROM GBCC_Cargo_Opr
			// co");
			// SQLQuery query = this.getSession().createSQLQuery(queryBuf.toString());
			log.info("getMiscTypeCode SQL  " + queryBuf.toString() + " paramMap" + paramMap.toString());
			List<MiscTypeCode> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<MiscTypeCode>(MiscTypeCode.class));

			MiscTypeCode oVO = null;
			MiscTypeCodeId id = null;
			for (MiscTypeCode miscTypeCode : lst) {
				id = new MiscTypeCodeId();
				oVO = new MiscTypeCode();
				oVO = miscTypeCode;
				id.setCatCd(oVO.getCatCd());
				id.setMiscTypeCd(oVO.getMiscTypeCd());
				oVO.setId(id);
				returnList.add(oVO);
			}
			log.info("getMiscTypeCode Result: " + returnList.toString());
		} catch (Exception e) {
			log.info("Exception getMiscTypeCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getMiscTypeCode");
		}
		return returnList;
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	@Override
	public GbccCargoOpenBalVO getCargoOpenBalById(String vvCd, String stevCd) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		GbccCargoOpenBalVO oVO = null;
		try {
			log.info("START: getCargoOpenBal  DAO vvCd: " + vvCd + " stevCd: " + stevCd);
			StringBuffer queryBuf = new StringBuffer();
			queryBuf.append("SELECT ");
			queryBuf.append(
					" co.vv_cd vvCd, co.stev_co_cd stevCoCd, co.WRK_HATCH_NBR, co.CTRL_HATCH_NBR, co.CHECKER_NM, co.CHECKER_OFFICE_NBR, co.CHECKER_HP_NBR, co.GANGS_NBR, co.WRK_START_DTTM, co.DELAY_RSN1_CD, co.DELAY_RSN2_CD, ");
			queryBuf.append(
					" co.MOBILE_CRANE_IND, co.HEAVY_LIFT_IND, co.TOT_DISC_TON, co.TOT_LOAD_TON, co.DELAY_REMARKS, co.CREATE_DTTM, ");
			queryBuf.append(" co.LAST_MODIFY_USER_ID lastModifyUserId, co.LAST_MODIFY_DTTM lastModifyDttm,");

			queryBuf.append(
					"	vc.VV_CD vv_Cd, vc.VSL_NM, vc.IN_VOY_NBR, vc.OUT_VOY_NBR, vc.VV_STATUS_IND, vc.SHPG_SVC_CD, vc.SHPG_ROUTE_NBR,  ");
			queryBuf.append(
					"	vc.ROUTE_NM, vc.VSL_OPR_CD, vc.BERTH_APPL_DTTM, vc.VSL_BERTH_DTTM, vc.VSL_ETD_DTTM, vc.VV_CLOSE_DTTM, ");
			queryBuf.append(
					"	vc.PORT_FR, vc.PORT_TO, vc.ARRIVAL_DRAFT, vc.DEPARTURE_DRAFT, vc.BERTH_SIDE_IND, vc.CONTACT_NM, ");
			queryBuf.append(
					"	vc.CONTACT_HOME_TEL, vc.CONTACT_OFF_TEL, vc.CONTACT_PGR, vc.CONTACT_FAX, vc.CNTR_DISC, vc.CNTR_LOAD, vc.UC_LOAD, vc.UC_DISC, vc.COB_DTTM, vc.BERTH_APPL_REM, ");
			queryBuf.append(
					"	vc.BILL_MARINE_IND, vc.BILL_STEV_IND, vc.BILL_OTHER_IND, vc.BILL_ADMIN_IND, vc.CREATE_USER_ID, vc.CREATE_ACCT_NBR, vc.LAST_MODIFY_USER_ID vsl_lastModifyUserId, vc.LAST_MODIFY_DTTM vsl_lastModifyDttm, ");
			queryBuf.append(
					"	vc.SCHEME, vc.TERMINAL, vc.VSL_LOA, vc.LOC_FR, vc.LOC_TO, vc.ALONGSIDE_DRAFT, vc.VSL_UNDER_TOW_IND, vc.CONTACT2_NM, ");
			queryBuf.append(
					"	vc.CONTACT2_HOME_TEL, vc.CONTACT2_OFF_TEL, vc.CONTACT2_PGR, vc.CONTACT2_FAX, vc.TOTAL_CARGO_ONBOARD, vc.LOAD_DISPLACEMENT, vc.DG_CARGO_IND, vc.HLIFT, ");
			queryBuf.append(
					"	vc.HLIFT_OVERWHARF, vc.HLIFT_OVERSIDE, vc.MOBILE_CR_DWT, vc.MOBILE_CR_SWL, vc.CNTR_VSL_IND, vc.GB_CLOSE_VSL_IND, vc.GB_CLOSE_BJ_IND, vc.GB_CLOSE_SHP_IND, ");
			queryBuf.append(
					"	vc.CARGO_MODE, vc.BERTH_ALLOC_REM, vc.STORAGE_SPACE_IND, vc.BILL_ACCT_NBR, vc.GB_ARRIVAL_WAIVER_CD, vc.GB_ARRIVAL_WAIVER_REASON, vc.GB_DEPARTURE_WAIVER_CD, vc.GB_DEPARTURE_WAIVER_REASON, ");
			queryBuf.append(
					"	vc.GB_BERT_BILL_IND, vc.BRIDGE_DIST_FROM_BOW, vc.VACATE_BERTH_IND, vc.MIXED_SCHEME_IND, vc.DISC_CM_CD, vc.LOAD_CM_CD, vc.DISC_BER_REM, vc.LOAD_BER_REM, ");
			queryBuf.append(
					"	vc.CRG_DET_PROC, vc.DECLARANT_CUST_CD, vc.UC_DISC_CM_CD, vc.UC_LOAD_CM_CD, vc.UC_DISC_BER_REM, vc.UC_LOAD_BER_REM, vc.EST_LONG_CR_MOVE_NBR, vc.EST_THROUGHPUT_NBR, ");
			queryBuf.append(
					"	vc.SENT_TO_PSA_IND, vc.ADVICE_DTTM, vc.ABBR_IN_VOY_NBR, vc.ABBR_OUT_VOY_NBR, vc.INCENTIVE_CLASS, vc.ISPS_LEVEL, vc.BILL_OPEN_TS_IND, vc.SMS_ALERT_REP1_IND, ");
			queryBuf.append(
					"	vc.SMS_ALERT_REP2_IND, vc.TANDEM_LIFT_IND tandemLiftInd, vc.BILL_PROD_SURCHRG_IND, vc.ALLOC_PROD_PRD, vc.REEFER_PARTY, vc.PROTRUSION_IND, vc.FLOAT_CRANE_IND, vc.FIRST_SCHEME, ");
			queryBuf.append(
					"	vc.GB_CLOSE_LCT_IND, vc.GB_CLOSE_LCT_DTTM, vc.GB_CLOSE_LCT_USER_ID, vc.STORAGE_DET_PROC, vc.GB_ARRIVAL_WAIVER_IND, vc.GB_ARRIVAL_WAIVER_AMOUNT, vc.GB_AUTO_CLOSE_BJ_IND, vc.GB_AUTO_CLOSE_SHP_IND, ");
			queryBuf.append(
					"	vc.LAST_ATU_DTTM, vc.CARGO_WT, vc.TOTAL_CARGO_WT_MOBILE_CR_DWT, vc.LCT_MIN_CHRG_IND, vc.CEMENT_VSL_IND, vc.BTR_AFT_CLOSING1_DTTM, vc.NOM_IND, vc.NOM_PROCESS_IND, ");
			queryBuf.append(
					"	vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND, vc.PRINCIPAL_CARRIER_CD, vc.USE_SHP_CRN_IND, ");

			queryBuf.append(
					"	vs.VV_CD stev_VvCd, vs.STEV_CO_CD stev_coCd, vs.STEV_CONTACT, vs.STEV_REMARKS, vs.STEV_REP, vs.LINENO, vs.LAST_MODIFY_USER_ID gbccView_lastModifyUserId, ");

			queryBuf.append("	sc.stev_co_nm, ");
			queryBuf.append("	cc.co_nm , ");
			queryBuf.append("	mtc1.misc_type_nm DELAY_RSN1_name , ");
			queryBuf.append("	mtc2.misc_type_nm DELAY_RSN2_name ");
			queryBuf.append("FROM ");
			queryBuf.append("	TOPS.vessel_call vc ");
			queryBuf.append("INNER JOIN TOPS.Company_Code cc ON ");
			queryBuf.append("	(vc.vsl_opr_cd = cc.co_cd) INNER JOIN ");
			// " INNER JOIN berthing br ON (vc.vv_cd = br.vv_cd AND br.shift_ind=1) "
			// +
			// " INNER JOIN GBCC_V_VV_stevedore vs ON (vc.vv_cd = vs.vv_cd) "
			// +
			queryBuf.append(" ( ");
			queryBuf.append("SELECT ");
			queryBuf.append("	vv_cd, ");
			queryBuf.append("	v.stev_co_cd1 stev_co_cd, ");
			queryBuf.append("	v.stev_contact1 stev_contact, ");
			queryBuf.append("	v.stev_remarks1 stev_remarks, ");
			queryBuf.append("	v.stev_rep1 stev_rep, ");
			queryBuf.append("	1 AS lineno, ");
			queryBuf.append("	last_modify_user_id ");
			queryBuf.append("FROM ");
			queryBuf.append("	TOPS.vv_stevedore V ");
			queryBuf.append("WHERE ");
			queryBuf.append("	stev_co_cd1 IS NOT NULL ");
			queryBuf.append("UNION ");
			queryBuf.append("SELECT ");
			queryBuf.append("	vv_cd, ");
			queryBuf.append("	v.stev_co_cd2 stev_co_cd, ");
			queryBuf.append("	v.stev_contact2 stev_contact, ");
			queryBuf.append("	v.stev_remarks2 stev_remarks, ");
			queryBuf.append("	v.stev_rep2 stev_rep, ");
			queryBuf.append("	2 AS lineno, ");
			queryBuf.append("	last_modify_user_id ");
			queryBuf.append("FROM ");
			queryBuf.append("	TOPS.vv_stevedore V ");
			queryBuf.append("WHERE ");
			queryBuf.append("	stev_co_cd2 IS NOT NULL ");
			queryBuf.append("UNION ");
			queryBuf.append("SELECT ");
			queryBuf.append("	vv_cd, ");
			queryBuf.append("	v.stev_co_cd3 stev_co_cd, ");
			queryBuf.append("	v.stev_contact3 stev_contact, ");
			queryBuf.append("	v.stev_remarks3 stev_remarks, ");
			queryBuf.append("	v.stev_rep3 stev_rep, ");
			queryBuf.append("	3 AS lineno, ");
			queryBuf.append("	last_modify_user_id ");
			queryBuf.append("FROM ");
			queryBuf.append("	TOPS.vv_stevedore V ");
			queryBuf.append("WHERE ");
			queryBuf.append("	stev_co_cd3 IS NOT NULL ) vs ON ");
			queryBuf.append("(vc.vv_cd = vs.vv_cd) ");
			queryBuf.append("INNER JOIN stevedore_company sc ON ");
			queryBuf.append("(vs.stev_co_cd = sc.stev_co_cd) ");
			queryBuf.append("LEFT JOIN GBCC_Cargo_Open_Bal co ON ");
			queryBuf.append("(vc.vv_cd = co.vv_cd ");
			queryBuf.append("AND co.stev_co_cd = vs.stev_co_cd) ");
			queryBuf.append("LEFT JOIN Misc_Type_Code mtc1 ON ");
			queryBuf.append("(co.DELAY_RSN1_CD = mtc1.misc_type_cd ");
			queryBuf.append("AND mtc1.cat_cd = :pCatCd1) ");
			queryBuf.append("LEFT JOIN Misc_Type_Code mtc2 ON ");
			queryBuf.append("(co.DELAY_RSN2_CD = mtc2.misc_type_cd ");
			queryBuf.append("AND mtc2.cat_cd = :pCatCd2)");
			queryBuf.append(" WHERE vc.vv_cd = :pVvCd AND vs.stev_co_cd = :pStevCoCd ");

			paramMap.put("pCatCd1", ConstantUtil.MISCTYPECD_DELAY_REASON);
			paramMap.put("pCatCd2", ConstantUtil.MISCTYPECD_DELAY_REASON);

			paramMap.put("pVvCd", vvCd.trim());
			paramMap.put("pStevCoCd", stevCd.trim());

			// query.addEntity("co", GbccCargoOpenBal.class);
			// query..addEntity("vc", VesselCall.class);

			// query..addEntity("vs", GbccViewVvStevedore.class);
			// query..addScalar("stev_co_nm", Hibernate.STRING);
			// query..addScalar("co_nm", Hibernate.STRING);
			// query..addScalar("DELAY_RSN1_name", Hibernate.STRING);
			// query..addScalar("DELAY_RSN2_name", Hibernate.STRING);

			log.info("getCargoOpenBalById SQL  " + queryBuf.toString() + " paramMap" + paramMap.toString());
			List<GbccCargoOpenBalVO> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<GbccCargoOpenBalVO>(GbccCargoOpenBalVO.class));

			log.info("getCargoOpenBalById Result" + lst.toString());

			for (GbccCargoOpenBalVO gbccCargoOpenBalVO : lst) {
				oVO = new GbccCargoOpenBalVO();
				oVO = gbccCargoOpenBalVO;
				oVO.setCargoOpenBalDetVO(getCargoOpenBalDet(vvCd, stevCd));

				oVO.setStevedoreCompanyName(oVO.getStev_co_nm());
				oVO.setAgentName(oVO.getCo_nm());
				oVO.setDelayRsn1Name(oVO.getDelay_rsn1_name());
				oVO.setDelayRsn2Name(oVO.getDelay_rsn2_name());
				Berthing firstBerthingVO = getFirstBerthing(vvCd);
				oVO.setFirstBerthingVO(firstBerthingVO);

				Berthing lastBerthingVO = getLastBerthing(vvCd);
				oVO.setLastBerthingVO(lastBerthingVO);
			}

			// opr.setFirstBerthingVO(this.getFirstBerthingVO(noso.getVvCd()));
			// copr.setFirstBerthingVO((Berthing) obj[2]);
			// copr.setAgentCompanyVO((CompanyCode) obj[3]);

		} catch (Exception e) {
			log.info("Exception getCargoOpenBalById : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getCargoOpenBalById");
		}
		return oVO;
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	// Get Cargo Opening Balance Details
	private List<GbccCargoOpenBalDet> getCargoOpenBalDet(String vvCd, String stevCd) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		List<GbccCargoOpenBalDet> returnList = new ArrayList<GbccCargoOpenBalDet>();
		try {
			log.info("START: getCargoOpenBalDet  DAO vvCd: " + vvCd + " stevCd: " + stevCd);

			StringBuffer queryBuf = new StringBuffer("SELECT det.* from GBCC.GBCC_Cargo_Open_Bal_Det det ");
			queryBuf.append(" WHERE det.VV_CD = :pVvCd and det.STEV_CO_CD = :pStevCd ");
			queryBuf.append(" ORDER BY det.HATCH_NBR");

			paramMap.put("pVvCd", vvCd.trim());
			paramMap.put("pStevCd", stevCd.trim());

			GbccCargoOpenBalDet oVO = null;
			GbccCargoOpenBalDetId id = null;
			log.info("getCargoOpenBalDet SQL  " + queryBuf.toString() + " paramMap" + paramMap.toString());
			List<GbccCargoOpenBalDet> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<GbccCargoOpenBalDet>(GbccCargoOpenBalDet.class));
			log.info("getCargoOpenBalDet Result" + lst.toString());
			for (GbccCargoOpenBalDet gbccCargoOpenBalDet : lst) {
				oVO = new GbccCargoOpenBalDet();
				id = new GbccCargoOpenBalDetId();
				oVO = gbccCargoOpenBalDet;
				id.setHatchNbr(oVO.getHatchNbr());
				id.setStevCoCd(oVO.getStevCoCd());
				id.setVvCd(oVO.getVvCd());
				oVO.setId(id);
				returnList.add(oVO);
			}
		} catch (Exception e) {
			log.info("Exception getCargoOpenBalDet : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getCargoOpenBalDet");
		}
		return returnList;
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	@Override
	public boolean saveCargoOpenBal(GbccCargoOpenBalVO transientObject) throws BusinessException {
		boolean result;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		StringBuffer queryBuf = new StringBuffer();
		String vv_Cd = "";
		try {
			log.info("START: saveCargoOpenBal  DAO GbccCargoOpenBalVO: " + transientObject.toString());

			queryBuf.append("SELECT co.VV_CD from GBCC.GBCC_CARGO_OPEN_BAL co ");
			queryBuf.append(" WHERE co.VV_CD = :pVvCd");
			queryBuf.append(" ORDER BY co.WRK_HATCH_NBR ");

			paramMap.put("pVvCd", transientObject.getVvCd());

			log.info("SQL::" + queryBuf.toString() + " paramMap " + paramMap.toString());
			// query.addEntity("co", GbccCargoOprPlanDet.class);

			List<String> lst = namedParameterJdbcTemplate.queryForList(queryBuf.toString(), paramMap, String.class);

			for (int i = 0; i < lst.size(); i++) {
				vv_Cd = (String) lst.get(i);
			}

			if (vv_Cd.equalsIgnoreCase("")) {
				sb.append("INSERT ");
				sb.append("	INTO ");
				sb.append("	GBCC.GBCC_CARGO_OPEN_BAL (VV_CD, ");
				sb.append("	STEV_CO_CD, ");
				sb.append("	WRK_HATCH_NBR, ");
				sb.append("	CTRL_HATCH_NBR, ");
				sb.append("	CHECKER_NM, ");
				sb.append("	CHECKER_OFFICE_NBR, ");
				sb.append("	CHECKER_HP_NBR, ");
				sb.append("	GANGS_NBR, ");
				sb.append("	WRK_START_DTTM, ");
				sb.append("	DELAY_RSN1_CD, ");
				sb.append("	DELAY_RSN2_CD, ");
				sb.append("	MOBILE_CRANE_IND, ");
				sb.append("	HEAVY_LIFT_IND, ");
				sb.append("	TOT_DISC_TON, ");
				sb.append("	TOT_LOAD_TON, ");
				sb.append("	LAST_MODIFY_USER_ID, ");
				sb.append("	LAST_MODIFY_DTTM, ");
				sb.append("	DELAY_REMARKS, ");
				sb.append("	CREATE_DTTM) ");
				sb.append("VALUES(:vv_Cd, ");
				sb.append(":stev_coCd, ");
				sb.append(":wrkHatchNbr, ");
				sb.append(":ctrlHatchNbr, ");
				sb.append(":checkerNm, ");
				sb.append(":checkerOfficeNbr, ");
				sb.append(":checkerHpNbr, ");
				sb.append(":gangsNbr, ");
				sb.append(":wrkStartDttm, ");
				sb.append(":delayRsn1Cd, ");
				sb.append(":delayRsn2Cd, ");
				sb.append(":mobileCraneInd, ");
				sb.append(":heavyLiftInd, ");
				sb.append(":totDiscTon, ");
				sb.append(":totLoadTon, ");
				sb.append(":lastModifyUserId, ");
				sb.append(":lastModifyDttm, ");
				sb.append(":delayRemarks, ");
				sb.append(":createDttm)");
			} else {
				sb = new StringBuffer();
				sb.append("UPDATE ");
				sb.append("	GBCC.GBCC_CARGO_OPEN_BAL ");
				sb.append("SET ");
				sb.append("	WRK_HATCH_NBR = :wrkHatchNbr, ");
				sb.append("	CTRL_HATCH_NBR = :ctrlHatchNbr, ");
				sb.append("	CHECKER_NM = :checkerNm, ");
				sb.append("	CHECKER_OFFICE_NBR = :checkerOfficeNbr, ");
				sb.append("	CHECKER_HP_NBR = :checkerHpNbr, ");
				sb.append("	GANGS_NBR = :gangsNbr, ");
				sb.append("	WRK_START_DTTM = :wrkStartDttm, ");
				sb.append("	DELAY_RSN1_CD = :delayRsn1Cd, ");
				sb.append("	DELAY_RSN2_CD = :delayRsn2Cd, ");
				sb.append("	MOBILE_CRANE_IND = :mobileCraneInd, ");
				sb.append("	HEAVY_LIFT_IND = :heavyLiftInd, ");
				sb.append("	TOT_DISC_TON = :totDiscTon, ");
				sb.append("	TOT_LOAD_TON = :totLoadTon, ");
				sb.append("	LAST_MODIFY_USER_ID = :lastModifyUserId, ");
				sb.append("	LAST_MODIFY_DTTM = :lastModifyDttm, ");
				sb.append("	DELAY_REMARKS = :delayRemarks, ");
				sb.append("	CREATE_DTTM = :createDttm ");
				sb.append("WHERE ");
				sb.append("	VV_CD = :vv_Cd ");
				sb.append("	AND STEV_CO_CD = :stev_coCd");
			}

			Date dttm = new Date();
			transientObject.setLastModifyDttm(dttm);

			String modifyUserId = transientObject.getLastModifyUserId();
			String vvCd = transientObject.getId().getVvCd();
			transientObject.setAuditUserId(modifyUserId);
			transientObject.setFnType(ConstantUtil.AUDIT_FNTYPE_CARGOOPS);
			transientObject.setFnsubType(ConstantUtil.AUDIT_FNSUBTYPE_CARGOOPENINGBALANCE_UPDATE);
			transientObject.setTableNm(ConstantUtil.TABLE_GBCCCARGOOPENBAL);
			transientObject.setKeyType1(ConstantUtil.AUDIT_KEYID_VVCD);
			transientObject.setKeyVal1(vvCd);
			transientObject.setKeyType2(ConstantUtil.AUDIT_KEYID_STEVCOCD);
			transientObject.setKeyVal2(transientObject.getId().getStevCoCd());

			String fieldvalue = ConstantUtil.AUDIT_KEYID_VVCD + "=" + vvCd;
			fieldvalue = fieldvalue + ConstantUtil.SEPARATOR + ConstantUtil.AUDIT_KEYID_STEVCOCD + "="
					+ transientObject.getId().getStevCoCd();

			transientObject.setFieldNewValue(fieldvalue);
			transientObject.setFieldOldValue(fieldvalue);

			log.info("saveCargoOpenBal SQL  " + sb.toString() + " GbccCargoOpenBalVO" + transientObject.toString());

			int count = namedParameterJdbcTemplate.update(sb.toString(),
					new BeanPropertySqlParameterSource(transientObject));
			if (count == 0) {
				log.info("not amended");
			}

			// cSession.saveOrUpdate(transientObject);

			List<GbccCargoOpenBalDet> detlst = transientObject.getCargoOpenBalDetVO();

			if (detlst != null) {
				queryBuf = new StringBuffer();

				queryBuf.append("SELECT co.VV_CD from GBCC.GBCC_Cargo_Open_Bal_DET co ");
				queryBuf.append(" WHERE co.VV_CD = :pVvCd ");
				queryBuf.append(" ORDER BY co.HATCH_NBR");

				// SQLQuery query = this.getSession().createSQLQuery(queryBuf.toString());
				paramMap.put("pVvCd", transientObject.getId().getVvCd());

				log.info("SQL::" + queryBuf.toString() + " paramMap " + paramMap.toString());
				// query.addEntity("co", GbccCargoOprPlanDet.class);

				List<String> lstDet = namedParameterJdbcTemplate.queryForList(queryBuf.toString(), paramMap,
						String.class);

				for (int i = 0; i < lstDet.size(); i++) {
					vv_Cd = (String) lstDet.get(i);
				}

				int hatchSize = detlst.size();

				for (int i = 0; i < hatchSize; i++) {
					GbccCargoOpenBalDet detVO = (GbccCargoOpenBalDet) detlst.get(i);

					detVO.setAuditUserId(modifyUserId);

					detVO.setFnType(ConstantUtil.AUDIT_FNTYPE_CARGOOPS);
					detVO.setFnsubType(ConstantUtil.AUDIT_FNSUBTYPE_CARGOOPENINGBALANCE_UPDATE);
					detVO.setTableNm(ConstantUtil.TABLE_GBCCCARGOOPENBALDET);
					detVO.setKeyType1(ConstantUtil.AUDIT_KEYID_VVCD);
					detVO.setKeyVal1(vvCd);

					detVO.setFieldNewValue(fieldvalue);
					detVO.setFieldOldValue(fieldvalue);

					detVO.setLastModifyDttm(dttm);

					detVO.setVvCd(detVO.getId().getVvCd());
					detVO.setStevCoCd(detVO.getId().getStevCoCd());
					detVO.setHatchNbr(detVO.getId().getHatchNbr());

					sb = new StringBuffer();
					if (vv_Cd.equalsIgnoreCase("")) {
						sb.append("INSERT ");
						sb.append("	INTO ");
						sb.append("	GBCC.GBCC_CARGO_OPEN_BAL_DET (VV_CD, ");
						sb.append("	STEV_CO_CD, ");
						sb.append("	HATCH_NBR, ");
						sb.append("	DISC_OPEN_BAL_TON, ");
						sb.append("	DISC_OVERSIDE_IND, ");
						sb.append("	LOAD_OPEN_BAL_TON, ");
						sb.append("	LOAD_OVERSIDE_IND, ");
						sb.append("	LAST_MODIFY_USER_ID, ");
						sb.append("	LAST_MODIFY_DTTM, ");
						sb.append("	DISC_REMARKS, ");
						sb.append("	LOAD_REMARKS) ");
						sb.append("VALUES(:vvCd, ");
						sb.append(":stevCoCd, ");
						sb.append(":hatchNbr, ");
						sb.append(":discOpenBalTon, ");
						sb.append(":discOversideInd, ");
						sb.append(":loadOpenBalTon, ");
						sb.append(":loadOversideInd, ");
						sb.append(":lastModifyUserId, ");
						sb.append("SYSDATE, ");
						sb.append(":discRemarks, ");
						sb.append(":loadRemarks)");
					} else {
						sb = new StringBuffer();
						sb.append("UPDATE ");
						sb.append("	GBCC.GBCC_CARGO_OPEN_BAL_DET ");
						sb.append("SET ");
						sb.append("	DISC_OPEN_BAL_TON = :discOpenBalTon, ");
						sb.append("	DISC_OVERSIDE_IND = :discOversideInd, ");
						sb.append("	LOAD_OPEN_BAL_TON = :loadOpenBalTon, ");
						sb.append("	LOAD_OVERSIDE_IND = :loadOversideInd, ");
						sb.append("	LAST_MODIFY_USER_ID = :lastModifyUserId, ");
						sb.append("	LAST_MODIFY_DTTM =  :lastModifyDttm, ");
						sb.append("	DISC_REMARKS = :discRemarks, ");
						sb.append("	LOAD_REMARKS = :loadRemarks ");
						sb.append("WHERE ");
						sb.append("	VV_CD = :vvCd ");
						sb.append("	AND STEV_CO_CD = :stevCoCd ");
						sb.append("	AND HATCH_NBR = :hatchNbr");
					}
					log.info("saveCargoOpenBal SQL  " + sb.toString() + " GbccCargoOpenBalDet" + detVO.toString());

					int detcount = namedParameterJdbcTemplate.update(sb.toString(),
							new BeanPropertySqlParameterSource(detVO));
					if (detcount == 0) {
						log.info("not amended");
					}
					// cSession.saveOrUpdate(detVO);
					// this.getHibernateTemplate().saveOrUpdate(transientObject);
					// addCargoOpenBalDet(detVO);
				}
			}
			result = true;
			log.info("getCargoOpenBalDet Result: " + result);
		} catch (Exception e) {
			result = false;
			log.info("Exception getCargoOpenBalDet : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getCargoOpenBalDet");
		}
		return result;
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	@Override
	public List<GbccCargoOpenBalVO> getCargoOpenBal(String vvCd, String CustCode, String sortBy, Criteria criteria,
			Boolean needAllData, String ATBFrom, String ATBTo, String listAllChk) throws BusinessException {
		List<GbccCargoOpenBalVO> returnList = new ArrayList<GbccCargoOpenBalVO>();
		StringBuffer queryBuf = new StringBuffer("");
		String vvStatus_Ind_Query = "";
		Map<String, Object> paramMap = new HashMap<>();
		String sql = "";
		TableData tableData = new TableData();
		try {

			log.info("START: getCargoOpenBal  DAO vvCd: " + vvCd + " CustCode: " + CustCode + " sortBy: " + sortBy
					+ " ATBFrom: " + ATBFrom + " ATBTo: " + ATBTo + " listAllChk: " + listAllChk);
			if ("".equalsIgnoreCase(listAllChk)) {
				vvStatus_Ind_Query = "('BR')";
			} else {
				vvStatus_Ind_Query = "('BR', 'UB')";
			}

			if (!"JP".equalsIgnoreCase(CustCode.trim()) && (!"".equalsIgnoreCase(CustCode.trim()))) {
				// For JP Staff only
				queryBuf = new StringBuffer();

				queryBuf.append(" SELECT  ");
				queryBuf.append(
						" co.vv_cd vvCd, co.stev_co_cd stevCoCd, co.WRK_HATCH_NBR, co.CTRL_HATCH_NBR, co.CHECKER_NM, co.CHECKER_OFFICE_NBR, co.CHECKER_HP_NBR, co.GANGS_NBR, co.WRK_START_DTTM, co.DELAY_RSN1_CD, co.DELAY_RSN2_CD, ");
				queryBuf.append(
						" co.MOBILE_CRANE_IND, co.HEAVY_LIFT_IND, co.TOT_DISC_TON, co.TOT_LOAD_TON, co.DELAY_REMARKS, co.CREATE_DTTM, ");
				queryBuf.append(" co.LAST_MODIFY_USER_ID lastModifyUserId, co.LAST_MODIFY_DTTM lastModifyDttm,");

				queryBuf.append(
						"	vc.VV_CD vv_Cd, vc.VSL_NM, vc.IN_VOY_NBR, vc.OUT_VOY_NBR, vc.VV_STATUS_IND, vc.SHPG_SVC_CD, vc.SHPG_ROUTE_NBR,  ");
				queryBuf.append(
						"	vc.ROUTE_NM, vc.VSL_OPR_CD, vc.BERTH_APPL_DTTM, vc.VSL_BERTH_DTTM, vc.VSL_ETD_DTTM, vc.VV_CLOSE_DTTM, ");
				queryBuf.append(
						"	vc.PORT_FR, vc.PORT_TO, vc.ARRIVAL_DRAFT, vc.DEPARTURE_DRAFT, vc.BERTH_SIDE_IND, vc.CONTACT_NM, ");
				queryBuf.append(
						"	vc.CONTACT_HOME_TEL, vc.CONTACT_OFF_TEL, vc.CONTACT_PGR, vc.CONTACT_FAX, vc.CNTR_DISC, vc.CNTR_LOAD, vc.UC_LOAD, vc.UC_DISC, vc.COB_DTTM, vc.BERTH_APPL_REM, ");
				queryBuf.append(
						"	vc.BILL_MARINE_IND, vc.BILL_STEV_IND, vc.BILL_OTHER_IND, vc.BILL_ADMIN_IND, vc.CREATE_USER_ID, vc.CREATE_ACCT_NBR, vc.LAST_MODIFY_USER_ID vsl_lastModifyUserId, vc.LAST_MODIFY_DTTM vsl_lastModifyDttm, ");
				queryBuf.append(
						"	vc.SCHEME, vc.TERMINAL, vc.VSL_LOA, vc.LOC_FR, vc.LOC_TO, vc.ALONGSIDE_DRAFT, vc.VSL_UNDER_TOW_IND, vc.CONTACT2_NM, ");
				queryBuf.append(
						"	vc.CONTACT2_HOME_TEL, vc.CONTACT2_OFF_TEL, vc.CONTACT2_PGR, vc.CONTACT2_FAX, vc.TOTAL_CARGO_ONBOARD, vc.LOAD_DISPLACEMENT, vc.DG_CARGO_IND, vc.HLIFT, ");
				queryBuf.append(
						"	vc.HLIFT_OVERWHARF, vc.HLIFT_OVERSIDE, vc.MOBILE_CR_DWT, vc.MOBILE_CR_SWL, vc.CNTR_VSL_IND, vc.GB_CLOSE_VSL_IND, vc.GB_CLOSE_BJ_IND, vc.GB_CLOSE_SHP_IND, ");
				queryBuf.append(
						"	vc.CARGO_MODE, vc.BERTH_ALLOC_REM, vc.STORAGE_SPACE_IND, vc.BILL_ACCT_NBR, vc.GB_ARRIVAL_WAIVER_CD, vc.GB_ARRIVAL_WAIVER_REASON, vc.GB_DEPARTURE_WAIVER_CD, vc.GB_DEPARTURE_WAIVER_REASON, ");
				queryBuf.append(
						"	vc.GB_BERT_BILL_IND, vc.BRIDGE_DIST_FROM_BOW, vc.VACATE_BERTH_IND, vc.MIXED_SCHEME_IND, vc.DISC_CM_CD, vc.LOAD_CM_CD, vc.DISC_BER_REM, vc.LOAD_BER_REM, ");
				queryBuf.append(
						"	vc.CRG_DET_PROC, vc.DECLARANT_CUST_CD, vc.UC_DISC_CM_CD, vc.UC_LOAD_CM_CD, vc.UC_DISC_BER_REM, vc.UC_LOAD_BER_REM, vc.EST_LONG_CR_MOVE_NBR, vc.EST_THROUGHPUT_NBR, ");
				queryBuf.append(
						"	vc.SENT_TO_PSA_IND, vc.ADVICE_DTTM, vc.ABBR_IN_VOY_NBR, vc.ABBR_OUT_VOY_NBR, vc.INCENTIVE_CLASS, vc.ISPS_LEVEL, vc.BILL_OPEN_TS_IND, vc.SMS_ALERT_REP1_IND, ");
				queryBuf.append(
						"	vc.SMS_ALERT_REP2_IND, vc.TANDEM_LIFT_IND tandemLiftInd, vc.BILL_PROD_SURCHRG_IND, vc.ALLOC_PROD_PRD, vc.REEFER_PARTY, vc.PROTRUSION_IND, vc.FLOAT_CRANE_IND, vc.FIRST_SCHEME, ");
				queryBuf.append(
						"	vc.GB_CLOSE_LCT_IND, vc.GB_CLOSE_LCT_DTTM, vc.GB_CLOSE_LCT_USER_ID, vc.STORAGE_DET_PROC, vc.GB_ARRIVAL_WAIVER_IND, vc.GB_ARRIVAL_WAIVER_AMOUNT, vc.GB_AUTO_CLOSE_BJ_IND, vc.GB_AUTO_CLOSE_SHP_IND, ");
				queryBuf.append(
						"	vc.LAST_ATU_DTTM, vc.CARGO_WT, vc.TOTAL_CARGO_WT_MOBILE_CR_DWT, vc.LCT_MIN_CHRG_IND, vc.CEMENT_VSL_IND, vc.BTR_AFT_CLOSING1_DTTM, vc.NOM_IND, vc.NOM_PROCESS_IND, ");
				queryBuf.append(
						"	vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND, vc.PRINCIPAL_CARRIER_CD, vc.USE_SHP_CRN_IND, ");

				queryBuf.append(
						"	br.VV_CD berth_VvCd, br.SHIFT_IND, br.ETB_DTTM, br.ETU_DTTM, br.ATB_DTTM, br.ATU_DTTM, br.FIRST_DISC_DTTM, br.FIRST_LOAD_DTTM, br.COD_DTTM, br.COL_DTTM,  ");
				queryBuf.append(
						"	br.BERTH_NBR, br.WHARF_MARK_FR, br.WHARF_MARK_TO, br.WHARF_SIDE_IND, br.LAST_MODIFY_USER_ID berth_lastModifyUserId, br.LAST_MODIFY_DTTM berth_lastModifyDttm, br.GB_COD_DTTM, br.GB_COL_DTTM, br.GB_FIRST_ACT_DTTM, br.GB_LAST_ACT_DTTM,  ");
				queryBuf.append(
						"	br.GB_BCOD_DTTM, br.GB_BCOL_DTTM, br.HAUL_DIST, br.HAUL_DIRN, br.GANG_NBR, br.HATCH_NBR, br.DELAY_RSN_CD, br.REMARKS berthRemarks, br.TOT_GEN_CARGO_ACT, br.GB_FIRST_CARGO_ACT_DTTM, br.DEP_DRAFT_FORTH,  ");
				queryBuf.append(
						"	br.GB_FIRST_DISC_DTTM, br.GB_FIRST_LOAD_DTTM, br.ACT_DRAFT_FORTH, br.ACT_DRAFT_AFT, br.ACT_WHARF_SIDE_IND, br.ACT_WHARF_MARK_FR, br.ACT_WHARF_MARK_TO, br.BERTH_REMARKS, br.COL_MODIFY_DTTM, br.DEP_DRAFT_AFT,  ");

				queryBuf.append(
						"	vs.VV_CD stev_VvCd, vs.STEV_CO_CD stev_coCd, vs.STEV_CONTACT, vs.STEV_REMARKS, vs.STEV_REP, vs.LINENO, vs.LAST_MODIFY_USER_ID gbccView_lastModifyUserId, ");

				queryBuf.append(" sc.stev_co_nm, ");
				queryBuf.append(" cc.co_nm ");
				queryBuf.append("FROM ");
				queryBuf.append("	TOPS.vessel_call vc ");
				queryBuf.append("INNER JOIN Vessel_Pre_Ops vpo ON ");
				queryBuf.append("	(vc.vv_cd = vpo.vv_cd) ");
				queryBuf.append("INNER JOIN cargo_client_code ccc ON ");
				queryBuf.append("	(vpo.cc_cd = ccc.cc_cd) ");
				queryBuf.append("INNER JOIN Company_Code cc ON ");
				queryBuf.append("	(vc.vsl_opr_cd = cc.co_cd) ");
				queryBuf.append("INNER JOIN ( ");
				queryBuf.append("	SELECT ");
				queryBuf.append("		b1.* ");
				queryBuf.append("	FROM ");
				queryBuf.append("		TOPS.berthing b1 ");
				queryBuf.append("	INNER JOIN ( ");
				queryBuf.append("		SELECT ");
				queryBuf.append("			vv_cd, ");
				queryBuf.append("			min(etb_dttm) min_etb ");
				queryBuf.append("		FROM ");
				queryBuf.append("			TOPS.berthing ");
				queryBuf.append("		GROUP BY ");
				queryBuf.append("			vv_cd ) b2 ON ");
				queryBuf.append("		(b1.vv_cd = b2.vv_cd ");
				queryBuf.append("		AND b1.etb_dttm = b2.min_etb) ) br ON ");
				queryBuf.append("	(vc.vv_cd = br.vv_cd ) ");
				// " INNER JOIN Berthing br ON (vc.vv_cd = br.vv_cd AND br.shift_ind=1) "
				// );
				queryBuf.append("INNER JOIN ( ");
				queryBuf.append("SELECT ");
				queryBuf.append("	vv_cd, ");
				queryBuf.append("	v.stev_co_cd1 stev_co_cd, ");
				queryBuf.append("	v.stev_contact1 stev_contact, ");
				queryBuf.append("	v.stev_remarks1 stev_remarks, ");
				queryBuf.append("	v.stev_rep1 stev_rep, ");
				queryBuf.append("	1 AS lineno, ");
				queryBuf.append("	last_modify_user_id ");
				queryBuf.append("FROM ");
				queryBuf.append("	TOPS.vv_stevedore V ");
				queryBuf.append("WHERE ");
				queryBuf.append("	stev_co_cd1 IS NOT NULL ");
				queryBuf.append("UNION ");
				queryBuf.append("SELECT ");
				queryBuf.append("	vv_cd, ");
				queryBuf.append("	v.stev_co_cd2 stev_co_cd, ");
				queryBuf.append("	v.stev_contact2 stev_contact, ");
				queryBuf.append("	v.stev_remarks2 stev_remarks, ");
				queryBuf.append("	v.stev_rep2 stev_rep, ");
				queryBuf.append("	2 AS lineno, ");
				queryBuf.append("	last_modify_user_id ");
				queryBuf.append("FROM ");
				queryBuf.append("	TOPS.vv_stevedore V ");
				queryBuf.append("WHERE ");
				queryBuf.append("	stev_co_cd2 IS NOT NULL ");
				queryBuf.append("UNION ");
				queryBuf.append("SELECT ");
				queryBuf.append("	vv_cd, ");
				queryBuf.append("	v.stev_co_cd3 stev_co_cd, ");
				queryBuf.append("	v.stev_contact3 stev_contact, ");
				queryBuf.append("	v.stev_remarks3 stev_remarks, ");
				queryBuf.append("	v.stev_rep3 stev_rep, ");
				queryBuf.append("	3 AS lineno, ");
				queryBuf.append("	last_modify_user_id ");
				queryBuf.append("FROM ");
				queryBuf.append("	TOPS.vv_stevedore V ");
				queryBuf.append("WHERE ");
				queryBuf.append("	stev_co_cd3 IS NOT NULL ) vs ON ");
				queryBuf.append("(vc.vv_cd = vs.vv_cd) ");
				queryBuf.append("INNER JOIN stevedore_company sc ON ");
				queryBuf.append("(vs.stev_co_cd = sc.stev_co_cd) ");
				queryBuf.append("LEFT JOIN GBCC_Cargo_Open_Bal co ON ");
				queryBuf.append("(vc.vv_cd = co.vv_cd ");
				queryBuf.append("AND co.stev_co_cd = vs.stev_co_cd) ");
				// " LEFT JOIN" +
				// " (" +
				// " SELECT " +
				// " vc.vv_cd, (sum(nvl(md.Gross_WT,0))/1000) + (sum(nvl(bd.ESN_WT,0))/1000) +
				// (sum(nvl(cn1.Declr_Wt,0))/1000) + (sum(nvl(cn2.Declr_Wt,0))/1000) TOTALTON "
				// +
				// " from vessel_call vc " +
				// " left join Manifest_Details md on (vc.vv_cd=md.var_nbr AND md.bl_status
				// ='A') "
				// +
				// " left join ESN be on (vc.vv_cd = be.out_voy_var_nbr and be.esn_status = 'A')
				// "
				// +
				// " left join ESN_Details bd on (be.esn_asn_nbr = bd.esn_asn_nbr) "
				// +
				// " left join Cntr cn1 on (vc.vv_cd = cn1.disc_vv_cd and cn1.TXN_STATUS <> 'D')
				// "
				// +
				// " left join Cntr cn2 on (vc.vv_cd = cn2.load_vv_cd and cn2.disc_vv_cd <>
				// vc.vv_cd AND cn2.TXN_STATUS <> 'D') "
				// +
				// " group by vc.vv_cd" +
				// " ) ton" +
				// " ON (vc.vv_cd = ton.vv_cd ) " +
				// TOTAL TONNaGE
				queryBuf.append("LEFT JOIN ( ");
				queryBuf.append("SELECT ");
				queryBuf.append("	var_nbr vv_cd, ");
				queryBuf.append("	(sum(nvl(md.Gross_WT, 0))/ 1000) total_ton ");
				queryBuf.append("FROM ");
				queryBuf.append("	GBMS.manifest_details md ");
				queryBuf.append("WHERE ");
				queryBuf.append("	md.bl_status = 'A' ");
				queryBuf.append("GROUP BY ");
				queryBuf.append("	var_nbr ) disc ON ");
				queryBuf.append("(vc.vv_cd = disc.vv_cd) ");
				queryBuf.append("LEFT JOIN (");
				// start Modified on 14/04/2010
				// " select out_voy_var_nbr vv_cd, (sum(nvl(d.ESN_WT,0))/1000) total_ton "
				// +
				// " from esn e " +
				// " inner join esn_details d on (e.esn_asn_nbr = d.esn_asn_nbr) "
				// +
				// " where e.esn_status ='A' group by out_voy_var_nbr "
				// +
				queryBuf.append("SELECT ");
				queryBuf.append("	vv_cd, ");
				queryBuf.append("	sum(total_ton1) AS total_ton ");
				queryBuf.append("FROM ");
				queryBuf.append("	( ");
				queryBuf.append("	SELECT ");
				queryBuf.append("		out_voy_var_nbr vv_cd, ");
				queryBuf.append("		(sum(nvl(d.ESN_WT, 0))/ 1000) total_ton1 ");
				queryBuf.append("	FROM ");
				queryBuf.append("		GBMS.esn e ");
				queryBuf.append("	INNER JOIN bk_details bk ON ");
				queryBuf.append("		(e.bk_ref_nbr = bk.bk_ref_nbr) ");
				queryBuf.append("	INNER JOIN esn_details d ON ");
				queryBuf.append("		(e.esn_asn_nbr = d.esn_asn_nbr) ");
				queryBuf.append("	WHERE ");
				queryBuf.append("		e.esn_status != 'X' ");
				queryBuf.append("		AND bk.bk_status != 'X' ");
				queryBuf.append("		AND trans_type = 'E' ");
				queryBuf.append("	GROUP BY ");
				queryBuf.append("		out_voy_var_nbr ");
				queryBuf.append("UNION ");
				queryBuf.append("	SELECT ");
				queryBuf.append("		out_voy_var_nbr vv_cd, ");
				queryBuf.append("		(sum(nvl(tesn.NOM_WT, 0))/ 1000) total_ton1 ");
				queryBuf.append("	FROM ");
				queryBuf.append("		GBMS.bk_details bk1 ");
				queryBuf.append("	INNER JOIN esn e1 ON ");
				queryBuf.append("		(bk1.bk_ref_nbr = e1.bk_ref_nbr) ");
				queryBuf.append("	INNER JOIN tesn_jp_jp tesn ON ");
				queryBuf.append("		(e1.esn_asn_nbr = tesn.esn_asn_nbr) ");
				queryBuf.append("	WHERE ");
				queryBuf.append("		bk1.bk_status != 'X' ");
				queryBuf.append("		AND e1.esn_status != 'X' ");
				queryBuf.append("		AND trans_type = 'A' ");
				queryBuf.append("	GROUP BY ");
				queryBuf.append("		out_voy_var_nbr ");
				queryBuf.append("UNION ");
				queryBuf.append("	SELECT ");
				queryBuf.append("		out_voy_var_nbr vv_cd, ");
				queryBuf.append("		(sum(nvl(psa.GROSS_WT, 0))/ 1000) total_ton1 ");
				queryBuf.append("	FROM ");
				queryBuf.append("		GBMS.bk_details bk2 ");
				queryBuf.append("	INNER JOIN esn e2 ON ");
				queryBuf.append("		(bk2.bk_ref_nbr = e2.bk_ref_nbr) ");
				queryBuf.append("	INNER JOIN tesn_psa_jp psa ON ");
				queryBuf.append("		(e2.esn_asn_nbr = psa.esn_asn_nbr) ");
				queryBuf.append("	WHERE ");
				queryBuf.append("		bk2.bk_status != 'X' ");
				queryBuf.append("		AND e2.esn_status != 'X' ");
				queryBuf.append("		AND trans_type = 'C' ");
				queryBuf.append("	GROUP BY ");
				queryBuf.append("		out_voy_var_nbr) ");
				queryBuf.append("GROUP BY ");
				queryBuf.append("	vv_cd ) load ON ");
				queryBuf.append("	(vc.vv_cd = load.vv_cd) ");
				queryBuf.append("LEFT JOIN ( ");
				queryBuf.append("	SELECT ");
				queryBuf.append("		disc_vv_cd vv_cd, ");
				queryBuf.append("		(sum(nvl(cn1.Declr_Wt, 0))/ 1000) total_ton ");
				queryBuf.append("	FROM ");
				queryBuf.append("		TOPS.Cntr cn1 ");
				queryBuf.append("	WHERE ");
				queryBuf.append("		cn1.TXN_STATUS <> 'D' ");
				queryBuf.append("	GROUP BY ");
				queryBuf.append("		disc_vv_cd ) cntr_disc ON ");
				queryBuf.append("	(vc.vv_cd = cntr_disc.vv_cd) ");
				queryBuf.append("LEFT JOIN ( ");
				queryBuf.append("	SELECT ");
				queryBuf.append("		load_vv_cd vv_cd, ");
				queryBuf.append("		(sum(nvl(cn2.Declr_Wt, 0))/ 1000) total_ton ");
				queryBuf.append("	FROM ");
				queryBuf.append("		TOPS.Cntr cn2 ");
				queryBuf.append("	WHERE ");
				queryBuf.append("		cn2.TXN_STATUS <> 'D' ");
				queryBuf.append("	GROUP BY ");
				queryBuf.append("		load_vv_cd ) cntr_load ON ");
				queryBuf.append("	(vc.vv_cd = cntr_load.vv_cd) ");
				queryBuf.append("WHERE ");
				queryBuf.append("	vc.vv_status_ind IN " + vvStatus_Ind_Query);
				queryBuf.append("	AND vc.scheme NOT IN ('JBT', 'JWP') ");
				queryBuf.append("	AND vpo.cc_cd NOT IN ('CV') ");
				// " AND nvl(ccc.bulk_vsl_ind,'N') <> 'Y' " + Allow Bulk vessel
				// " AND gbcc_fn_totaltonnage(vc.vv_cd) >= 0 " +
				// " AND ton.totalton > :pThreshold " + // >
				// 1000
				queryBuf.append(
						" AND (nvl(disc.total_ton,0) + nvl(cntr_disc.total_ton,0) + nvl(load.total_ton,0) + nvl(cntr_load.total_ton,0)) > :pThreshold");
			} else {
				queryBuf = new StringBuffer();
				queryBuf.append(" SELECT ");
				queryBuf.append(
						" co.vv_cd vvCd, co.stev_co_cd stevCoCd, co.WRK_HATCH_NBR, co.CTRL_HATCH_NBR, co.CHECKER_NM, co.CHECKER_OFFICE_NBR, co.CHECKER_HP_NBR, co.GANGS_NBR, co.WRK_START_DTTM, co.DELAY_RSN1_CD, co.DELAY_RSN2_CD, ");
				queryBuf.append(
						" co.MOBILE_CRANE_IND, co.HEAVY_LIFT_IND, co.TOT_DISC_TON, co.TOT_LOAD_TON, co.DELAY_REMARKS, co.CREATE_DTTM, ");
				queryBuf.append(" co.LAST_MODIFY_USER_ID lastModifyUserId, co.LAST_MODIFY_DTTM lastModifyDttm,");

				queryBuf.append(
						"	vc.VV_CD vv_Cd, vc.VSL_NM, vc.IN_VOY_NBR, vc.OUT_VOY_NBR, vc.VV_STATUS_IND, vc.SHPG_SVC_CD, vc.SHPG_ROUTE_NBR,  ");
				queryBuf.append(
						"	vc.ROUTE_NM, vc.VSL_OPR_CD, vc.BERTH_APPL_DTTM, vc.VSL_BERTH_DTTM, vc.VSL_ETD_DTTM, vc.VV_CLOSE_DTTM, ");
				queryBuf.append(
						"	vc.PORT_FR, vc.PORT_TO, vc.ARRIVAL_DRAFT, vc.DEPARTURE_DRAFT, vc.BERTH_SIDE_IND, vc.CONTACT_NM, ");
				queryBuf.append(
						"	vc.CONTACT_HOME_TEL, vc.CONTACT_OFF_TEL, vc.CONTACT_PGR, vc.CONTACT_FAX, vc.CNTR_DISC, vc.CNTR_LOAD, vc.UC_LOAD, vc.UC_DISC, vc.COB_DTTM, vc.BERTH_APPL_REM, ");
				queryBuf.append(
						"	vc.BILL_MARINE_IND, vc.BILL_STEV_IND, vc.BILL_OTHER_IND, vc.BILL_ADMIN_IND, vc.CREATE_USER_ID, vc.CREATE_ACCT_NBR, vc.LAST_MODIFY_USER_ID vsl_lastModifyUserId, vc.LAST_MODIFY_DTTM vsl_lastModifyDttm, ");
				queryBuf.append(
						"	vc.SCHEME, vc.TERMINAL, vc.VSL_LOA, vc.LOC_FR, vc.LOC_TO, vc.ALONGSIDE_DRAFT, vc.VSL_UNDER_TOW_IND, vc.CONTACT2_NM, ");
				queryBuf.append(
						"	vc.CONTACT2_HOME_TEL, vc.CONTACT2_OFF_TEL, vc.CONTACT2_PGR, vc.CONTACT2_FAX, vc.TOTAL_CARGO_ONBOARD, vc.LOAD_DISPLACEMENT, vc.DG_CARGO_IND, vc.HLIFT, ");
				queryBuf.append(
						"	vc.HLIFT_OVERWHARF, vc.HLIFT_OVERSIDE, vc.MOBILE_CR_DWT, vc.MOBILE_CR_SWL, vc.CNTR_VSL_IND, vc.GB_CLOSE_VSL_IND, vc.GB_CLOSE_BJ_IND, vc.GB_CLOSE_SHP_IND, ");
				queryBuf.append(
						"	vc.CARGO_MODE, vc.BERTH_ALLOC_REM, vc.STORAGE_SPACE_IND, vc.BILL_ACCT_NBR, vc.GB_ARRIVAL_WAIVER_CD, vc.GB_ARRIVAL_WAIVER_REASON, vc.GB_DEPARTURE_WAIVER_CD, vc.GB_DEPARTURE_WAIVER_REASON, ");
				queryBuf.append(
						"	vc.GB_BERT_BILL_IND, vc.BRIDGE_DIST_FROM_BOW, vc.VACATE_BERTH_IND, vc.MIXED_SCHEME_IND, vc.DISC_CM_CD, vc.LOAD_CM_CD, vc.DISC_BER_REM, vc.LOAD_BER_REM, ");
				queryBuf.append(
						"	vc.CRG_DET_PROC, vc.DECLARANT_CUST_CD, vc.UC_DISC_CM_CD, vc.UC_LOAD_CM_CD, vc.UC_DISC_BER_REM, vc.UC_LOAD_BER_REM, vc.EST_LONG_CR_MOVE_NBR, vc.EST_THROUGHPUT_NBR, ");
				queryBuf.append(
						"	vc.SENT_TO_PSA_IND, vc.ADVICE_DTTM, vc.ABBR_IN_VOY_NBR, vc.ABBR_OUT_VOY_NBR, vc.INCENTIVE_CLASS, vc.ISPS_LEVEL, vc.BILL_OPEN_TS_IND, vc.SMS_ALERT_REP1_IND, ");
				queryBuf.append(
						"	vc.SMS_ALERT_REP2_IND, vc.TANDEM_LIFT_IND tandemLiftInd, vc.BILL_PROD_SURCHRG_IND, vc.ALLOC_PROD_PRD, vc.REEFER_PARTY, vc.PROTRUSION_IND, vc.FLOAT_CRANE_IND, vc.FIRST_SCHEME, ");
				queryBuf.append(
						"	vc.GB_CLOSE_LCT_IND, vc.GB_CLOSE_LCT_DTTM, vc.GB_CLOSE_LCT_USER_ID, vc.STORAGE_DET_PROC, vc.GB_ARRIVAL_WAIVER_IND, vc.GB_ARRIVAL_WAIVER_AMOUNT, vc.GB_AUTO_CLOSE_BJ_IND, vc.GB_AUTO_CLOSE_SHP_IND, ");
				queryBuf.append(
						"	vc.LAST_ATU_DTTM, vc.CARGO_WT, vc.TOTAL_CARGO_WT_MOBILE_CR_DWT, vc.LCT_MIN_CHRG_IND, vc.CEMENT_VSL_IND, vc.BTR_AFT_CLOSING1_DTTM, vc.NOM_IND, vc.NOM_PROCESS_IND, ");
				queryBuf.append(
						"	vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND, vc.PRINCIPAL_CARRIER_CD, vc.USE_SHP_CRN_IND, ");

				queryBuf.append(
						"	br.VV_CD berth_VvCd, br.SHIFT_IND, br.ETB_DTTM, br.ETU_DTTM, br.ATB_DTTM, br.ATU_DTTM, br.FIRST_DISC_DTTM, br.FIRST_LOAD_DTTM, br.COD_DTTM, br.COL_DTTM,  ");
				queryBuf.append(
						"	br.BERTH_NBR, br.WHARF_MARK_FR, br.WHARF_MARK_TO, br.WHARF_SIDE_IND, br.LAST_MODIFY_USER_ID berth_lastModifyUserId, br.LAST_MODIFY_DTTM berth_lastModifyDttm, br.GB_COD_DTTM, br.GB_COL_DTTM, br.GB_FIRST_ACT_DTTM, br.GB_LAST_ACT_DTTM,  ");
				queryBuf.append(
						"	br.GB_BCOD_DTTM, br.GB_BCOL_DTTM, br.HAUL_DIST, br.HAUL_DIRN, br.GANG_NBR, br.HATCH_NBR, br.DELAY_RSN_CD, br.REMARKS berthRemarks, br.TOT_GEN_CARGO_ACT, br.GB_FIRST_CARGO_ACT_DTTM, br.DEP_DRAFT_FORTH,  ");
				queryBuf.append(
						"	br.GB_FIRST_DISC_DTTM, br.GB_FIRST_LOAD_DTTM, br.ACT_DRAFT_FORTH, br.ACT_DRAFT_AFT, br.ACT_WHARF_SIDE_IND, br.ACT_WHARF_MARK_FR, br.ACT_WHARF_MARK_TO, br.BERTH_REMARKS, br.COL_MODIFY_DTTM, br.DEP_DRAFT_AFT,  ");

				queryBuf.append(
						"	vs.VV_CD stev_VvCd, vs.STEV_CO_CD stev_coCd, vs.STEV_CONTACT, vs.STEV_REMARKS, vs.STEV_REP, vs.LINENO, vs.LAST_MODIFY_USER_ID gbccView_lastModifyUserId, ");

				queryBuf.append(" sc.stev_co_nm, ");
				queryBuf.append(" cc.co_nm ");
				queryBuf.append("FROM ");
				queryBuf.append("	TOPS.vessel_call vc ");
				queryBuf.append("INNER JOIN Vessel_Pre_Ops vpo ON ");
				queryBuf.append("	(vc.vv_cd = vpo.vv_cd) ");
				queryBuf.append("INNER JOIN cargo_client_code ccc ON ");
				queryBuf.append("	(vpo.cc_cd = ccc.cc_cd) ");
				queryBuf.append("INNER JOIN Company_Code cc ON ");
				queryBuf.append("	(vc.vsl_opr_cd = cc.co_cd) ");
				queryBuf.append("INNER JOIN ( ");
				queryBuf.append("	SELECT ");
				queryBuf.append("		b1.* ");
				queryBuf.append("	FROM ");
				queryBuf.append("		TOPS.berthing b1 ");
				queryBuf.append("	INNER JOIN ( ");
				queryBuf.append("		SELECT ");
				queryBuf.append("			vv_cd, ");
				queryBuf.append("			min(etb_dttm) min_etb ");
				queryBuf.append("		FROM ");
				queryBuf.append("			TOPS.berthing ");
				queryBuf.append("		GROUP BY ");
				queryBuf.append("			vv_cd ) b2 ON ");
				queryBuf.append("		(b1.vv_cd = b2.vv_cd ");
				queryBuf.append("		AND b1.etb_dttm = b2.min_etb) ) br ON ");
				queryBuf.append("	(vc.vv_cd = br.vv_cd )");
				// " INNER JOIN Berthing br ON (vc.vv_cd = br.vv_cd AND br.shift_ind=1) "
				// +
				queryBuf.append("INNER JOIN ( ");
				queryBuf.append("SELECT ");
				queryBuf.append("	vv_cd, ");
				queryBuf.append("	v.stev_co_cd1 stev_co_cd, ");
				queryBuf.append("	v.stev_contact1 stev_contact, ");
				queryBuf.append("	v.stev_remarks1 stev_remarks, ");
				queryBuf.append("	v.stev_rep1 stev_rep, ");
				queryBuf.append("	1 AS lineno, ");
				queryBuf.append("	last_modify_user_id ");
				queryBuf.append("FROM ");
				queryBuf.append("	TOPS.vv_stevedore V ");
				queryBuf.append("WHERE ");
				queryBuf.append("	stev_co_cd1 IS NOT NULL ");
				queryBuf.append("UNION ");
				queryBuf.append("SELECT ");
				queryBuf.append("	vv_cd, ");
				queryBuf.append("	v.stev_co_cd2 stev_co_cd, ");
				queryBuf.append("	v.stev_contact2 stev_contact, ");
				queryBuf.append("	v.stev_remarks2 stev_remarks, ");
				queryBuf.append("	v.stev_rep2 stev_rep, ");
				queryBuf.append("	2 AS lineno, ");
				queryBuf.append("	last_modify_user_id ");
				queryBuf.append("FROM ");
				queryBuf.append("	TOPS.vv_stevedore V ");
				queryBuf.append("WHERE ");
				queryBuf.append("	stev_co_cd2 IS NOT NULL ");
				queryBuf.append("UNION ");
				queryBuf.append("SELECT ");
				queryBuf.append("	vv_cd, ");
				queryBuf.append("	v.stev_co_cd3 stev_co_cd, ");
				queryBuf.append("	v.stev_contact3 stev_contact, ");
				queryBuf.append("	v.stev_remarks3 stev_remarks, ");
				queryBuf.append("	v.stev_rep3 stev_rep, ");
				queryBuf.append("	3 AS lineno, ");
				queryBuf.append("	last_modify_user_id ");
				queryBuf.append("FROM ");
				queryBuf.append("	TOPS.vv_stevedore V ");
				queryBuf.append(" WHERE ");
				queryBuf.append("	stev_co_cd3 IS NOT NULL ) vs ON ");
				queryBuf.append("(vc.vv_cd = vs.vv_cd) ");
				queryBuf.append("INNER JOIN stevedore_company sc ON ");
				queryBuf.append("(vs.stev_co_cd = sc.stev_co_cd) ");
				queryBuf.append("LEFT JOIN GBCC_Cargo_Open_Bal co ON ");
				queryBuf.append("(vc.vv_cd = co.vv_cd ");
				queryBuf.append("AND co.stev_co_cd = vs.stev_co_cd)");
				// " LEFT JOIN" +
				// " (" +
				// " SELECT " +
				// " vc.vv_cd, (sum(nvl(md.Gross_WT,0))/1000) + (sum(nvl(bd.ESN_WT,0))/1000) +
				// (sum(nvl(cn1.Declr_Wt,0))/1000) + (sum(nvl(cn2.Declr_Wt,0))/1000) TOTALTON "
				// +
				// " from vessel_call vc " +
				// " left join Manifest_Details md on (vc.vv_cd=md.var_nbr AND md.bl_status
				// ='A') "
				// +
				// " left join ESN be on (vc.vv_cd = be.out_voy_var_nbr and be.esn_status = 'A')
				// "
				// +
				// " left join ESN_Details bd on (be.esn_asn_nbr = bd.esn_asn_nbr) "
				// +
				// " left join Cntr cn1 on (vc.vv_cd = cn1.disc_vv_cd and cn1.TXN_STATUS <> 'D')
				// "
				// +
				// " left join Cntr cn2 on (vc.vv_cd = cn2.load_vv_cd and cn2.disc_vv_cd <>
				// vc.vv_cd AND cn2.TXN_STATUS <> 'D') "
				// +
				// " group by vc.vv_cd" +
				// " ) ton" +
				// " ON (vc.vv_cd = ton.vv_cd ) " +
				// TOTAL TONNaGE
				queryBuf.append(" LEFT JOIN ( ");
				queryBuf.append("SELECT ");
				queryBuf.append("	var_nbr vv_cd, ");
				queryBuf.append("	(sum(nvl(md.Gross_WT, 0))/ 1000) total_ton ");
				queryBuf.append("FROM ");
				queryBuf.append("	GBMS.manifest_details md ");
				queryBuf.append("WHERE ");
				queryBuf.append("	md.bl_status = 'A' ");
				queryBuf.append("GROUP BY ");
				queryBuf.append("	var_nbr ) disc ON ");
				queryBuf.append("(vc.vv_cd = disc.vv_cd) ");
				queryBuf.append("LEFT JOIN (");
				// start Modified on 14/04/2010
				// " select out_voy_var_nbr vv_cd, (sum(nvl(d.ESN_WT,0))/1000) total_ton "
				// +
				// " from esn e " +
				// " inner join esn_details d on (e.esn_asn_nbr = d.esn_asn_nbr) "
				// +
				// " where e.esn_status ='A' group by out_voy_var_nbr "
				// +
				queryBuf.append("SELECT ");
				queryBuf.append("	vv_cd, ");
				queryBuf.append("	sum(total_ton1) AS total_ton ");
				queryBuf.append("FROM ");
				queryBuf.append("	( ");
				queryBuf.append("	SELECT ");
				queryBuf.append("		out_voy_var_nbr vv_cd, ");
				queryBuf.append("		(sum(nvl(d.ESN_WT, 0))/ 1000) total_ton1 ");
				queryBuf.append("	FROM ");
				queryBuf.append("		GBMS.esn e ");
				queryBuf.append("	INNER JOIN bk_details bk ON ");
				queryBuf.append("		(e.bk_ref_nbr = bk.bk_ref_nbr) ");
				queryBuf.append("	INNER JOIN esn_details d ON ");
				queryBuf.append("		(e.esn_asn_nbr = d.esn_asn_nbr) ");
				queryBuf.append("	WHERE ");
				queryBuf.append("		e.esn_status != 'X' ");
				queryBuf.append("		AND bk.bk_status != 'X' ");
				queryBuf.append("		AND trans_type = 'E' ");
				queryBuf.append("	GROUP BY ");
				queryBuf.append("		out_voy_var_nbr ");
				queryBuf.append("UNION ");
				queryBuf.append("	SELECT ");
				queryBuf.append("		out_voy_var_nbr vv_cd, ");
				queryBuf.append("		(sum(nvl(tesn.NOM_WT, 0))/ 1000) total_ton1 ");
				queryBuf.append("	FROM ");
				queryBuf.append("		GBMS.bk_details bk1 ");
				queryBuf.append("	INNER JOIN esn e1 ON ");
				queryBuf.append("		(bk1.bk_ref_nbr = e1.bk_ref_nbr) ");
				queryBuf.append("	INNER JOIN tesn_jp_jp tesn ON ");
				queryBuf.append("		(e1.esn_asn_nbr = tesn.esn_asn_nbr) ");
				queryBuf.append("	WHERE ");
				queryBuf.append("		bk1.bk_status != 'X' ");
				queryBuf.append("		AND e1.esn_status != 'X' ");
				queryBuf.append("		AND trans_type = 'A' ");
				queryBuf.append("	GROUP BY ");
				queryBuf.append("		out_voy_var_nbr ");
				queryBuf.append("UNION ");
				queryBuf.append("	SELECT ");
				queryBuf.append("		out_voy_var_nbr vv_cd, ");
				queryBuf.append("		(sum(nvl(psa.GROSS_WT, 0))/ 1000) total_ton1 ");
				queryBuf.append("	FROM ");
				queryBuf.append("		GBMS.bk_details bk2 ");
				queryBuf.append("	INNER JOIN esn e2 ON ");
				queryBuf.append("		(bk2.bk_ref_nbr = e2.bk_ref_nbr) ");
				queryBuf.append("	INNER JOIN tesn_psa_jp psa ON ");
				queryBuf.append("		(e2.esn_asn_nbr = psa.esn_asn_nbr) ");
				queryBuf.append("	WHERE ");
				queryBuf.append("		bk2.bk_status != 'X' ");
				queryBuf.append("		AND e2.esn_status != 'X' ");
				queryBuf.append("		AND trans_type = 'C' ");
				queryBuf.append("	GROUP BY ");
				queryBuf.append("		out_voy_var_nbr) ");
				queryBuf.append("GROUP BY ");
				queryBuf.append("	vv_cd ) load ON ");
				queryBuf.append("(vc.vv_cd = load.vv_cd) ");
				queryBuf.append("LEFT JOIN ( ");
				queryBuf.append("SELECT ");
				queryBuf.append("	disc_vv_cd vv_cd, ");
				queryBuf.append("	(sum(nvl(cn1.Declr_Wt, 0))/ 1000) total_ton ");
				queryBuf.append("FROM ");
				queryBuf.append("	TOPS.Cntr cn1 ");
				queryBuf.append("WHERE ");
				queryBuf.append("	cn1.TXN_STATUS <> 'D' ");
				queryBuf.append("GROUP BY ");
				queryBuf.append("	disc_vv_cd ) cntr_disc ON ");
				queryBuf.append("(vc.vv_cd = cntr_disc.vv_cd) ");
				queryBuf.append("LEFT JOIN ( ");
				queryBuf.append("SELECT ");
				queryBuf.append("	load_vv_cd vv_cd, ");
				queryBuf.append("	(sum(nvl(cn2.Declr_Wt, 0))/ 1000) total_ton ");
				queryBuf.append("FROM ");
				queryBuf.append("	TOPS.Cntr cn2 ");
				queryBuf.append("WHERE ");
				queryBuf.append("	cn2.TXN_STATUS <> 'D' ");
				queryBuf.append("GROUP BY ");
				queryBuf.append("	load_vv_cd ) cntr_load ON ");
				queryBuf.append("(vc.vv_cd = cntr_load.vv_cd) ");
				queryBuf.append("WHERE ");
				queryBuf.append("vc.vv_status_ind IN " + vvStatus_Ind_Query);
				queryBuf.append(" AND vc.scheme NOT IN ('JBT', 'JWP') ");
				queryBuf.append("AND vpo.cc_cd NOT IN ('CV') ");
				// " AND nvl(ccc.bulk_vsl_ind,'N') <> 'Y' " + Allow Bulk Vessel
				// " AND gbcc_fn_totaltonnage(vc.vv_cd) >= 0 " +
				// " AND ton.totalton > :pThreshold " + // >
				// 1000
				queryBuf.append(
						" AND (nvl(disc.total_ton,0) + nvl(cntr_disc.total_ton,0) + nvl(load.total_ton,0) + nvl(cntr_load.total_ton,0)) > :pThreshold ");
			}

			if (!"JP".equalsIgnoreCase(CustCode.trim()) && (!"".equalsIgnoreCase(CustCode.trim()))) {
				queryBuf.append(" and sc.co_cd = :pCustCd ");
			} else {
				if (!"".equalsIgnoreCase(ATBFrom) && !"".equalsIgnoreCase(ATBTo)) {
					queryBuf.append(" and br.ATB_DTTM >=  :pATBFrom ");
					queryBuf.append(" and br.ATB_DTTM <= :pATBTo ");
				}
			}

			if (!"".equalsIgnoreCase(vvCd.trim()))
				queryBuf.append(" and vc.vv_cd = :pVvCd ");
			// queryBuf.append(" and vc.vsl_opr_cd = :pCustCd ");

			if (!"".equalsIgnoreCase(sortBy.trim())) {
				queryBuf.append(" ORDER BY ");
				if (sortBy.equalsIgnoreCase("vc.vsl_nm, vc.out_voy_nbr")) {
					queryBuf.append("vc.vsl_nm, vc.out_voy_nbr ");
				} else if (sortBy.equalsIgnoreCase("br.ATB_DTTM")) {
					queryBuf.append("br.ATB_DTTM " );
				}
 			}

			log.info("SQL->" + queryBuf.toString());

			long threshold = getGbccParaNbr(ConstantUtil.PARA_CD_GC_TON_THRESHOLD);
			paramMap.put("pThreshold", (int) threshold);

			if (!"JP".equalsIgnoreCase(CustCode.trim()) && (!"".equalsIgnoreCase(CustCode.trim()))) {
				paramMap.put("pCustCd", CustCode.trim());
			} else {

				if (!"".equalsIgnoreCase(ATBFrom) && !"".equalsIgnoreCase(ATBTo)) {

					SimpleDateFormat f = new SimpleDateFormat("ddMMyyyy hhmm");
					// String b = f.format(crDttm);
					Date atbFromDttm = null;
					Date atbToDttm = null;

					try {
						atbFromDttm = f.parse(ATBFrom + " 0000");
						atbToDttm = f.parse(ATBTo + " 2359");
					} catch (ParseException e) {
						log.info("getCargoOpenBal Exception", e);
					}

					paramMap.put("pATBFrom", new Timestamp(atbFromDttm.getTime()));
					paramMap.put("pATBTo", new Timestamp(atbToDttm.getTime()));
				}
			}

			if (!"".equalsIgnoreCase(vvCd.trim()))
				paramMap.put("pVvCd", vvCd.trim());

			// StringBuffer queryBuf = new
			// StringBuffer("SELECT {co.*} FROM GBCC_Cargo_Opr co");
			// SQLQuery query =
			// this.getSession().createSQLQuery(queryBuf.toString());

			// query.addJoin("vc" , "GbccCargoOpr.vesselCallVO");
			// query.addJoin("vc", "GbccCargoOpr.vesselCallVO");
			// query.addJoin("br", "GbccCargoOpr.firstBerthingVO");

			sql = queryBuf.toString();

			if (!needAllData) {

				if (criteria.isPaginated()) {
					sql = CommonUtil.getPaginatedSql(queryBuf.toString(), criteria.getStart(), criteria.getLimit());
				} else {
					sql = queryBuf.toString();
				}

			}

			log.info("getCargoOpenBal SQL  " + sql + " paramMap" + paramMap.toString());

			List<GbccCargoOpenBalVO> lst = namedParameterJdbcTemplate.query(sql, paramMap,
					new BeanPropertyRowMapper<GbccCargoOpenBalVO>(GbccCargoOpenBalVO.class));
			GbccCargoOpenBalVO copr = null;
			log.info("getCargoOpenBal Result" + lst.toString());
			for (GbccCargoOpenBalVO gbccCargoOpenBalVO : lst) {
				copr = new GbccCargoOpenBalVO();
				copr = gbccCargoOpenBalVO;
				copr.setDelayRsn1Name(copr.getDelay_rsn1_name());
				copr.setDelayRsn2Name(copr.getDelay_rsn2_name());
				copr.setStevedoreCompanyName(copr.getStev_co_nm());
				copr.setAgentName(copr.getCo_nm());
				returnList.add(copr);
			}

			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate
						.queryForObject("SELECT COUNT(*) FROM (" + queryBuf.toString() + ")", paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());

				copr = new GbccCargoOpenBalVO();
				copr.setTotal(tableData.getTotal());
				returnList.add(copr);
			}
			// tableData.setListData(topsModel);
			// tableResult.setData(tableData);
			// tableResult.setSuccess(true);
			// returnList.add(tableData.getTotal());
		} catch (Exception e) {
			log.info("getCargoOpenBal Exception", e);
		} finally {
			log.info("END: DAO getCargoOpenBal");
		}

		return returnList;
	}

	@Override
	public List<VesselCall> getCargoOprVesselCall(String custCd) throws BusinessException {
		return getCargoOprVesselCall(custCd, "", "", true);
	}

	private List<VesselCall> getCargoOprVesselCall(String custCd, String stevCoCd, String vvCd, boolean chkOpenBal)
			throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer queryBuf = new StringBuffer();
		List<VesselCall> returnList = new ArrayList<VesselCall>();
		try {
			log.info("START: getCargoOprVesselCall  DAO custCd: " + custCd + " stevCoCd: " + stevCoCd + " vvCd: " + vvCd
					+ " chkOpenBal: " + chkOpenBal);

			queryBuf.append(" SELECT vc.*  FROM TOPS.vessel_call vc  WHERE 1=1 ");

			if (chkOpenBal) {
				queryBuf.append(
						" AND vc.vv_cd IN (SELECT co.vv_cd FROM GBCC.Gbcc_Cargo_Open_Bal co  INNER JOIN stevedore_company sc ON (co.stev_co_cd = sc.stev_co_cd )   WHERE 1=1 ");

				if (!"JP".equalsIgnoreCase(custCd.trim()) && !"".equalsIgnoreCase(custCd.trim())) {
					queryBuf.append(" AND sc.co_cd = :pCoCd ");
				}

				if (!"".equalsIgnoreCase(stevCoCd.trim())) {
					queryBuf.append(" AND co.stev_co_cd = :pStevCoCd ");
				}
				queryBuf.append(" )");
			}
			if (!"JP".equalsIgnoreCase(custCd.trim()) && !"".equalsIgnoreCase(custCd.trim())) {
				queryBuf.append(" AND vc.vv_status_ind in (:statusBr, :statusUb) ");
				paramMap.put("statusBr", ConstantUtil.VV_STATUS_BR);
				paramMap.put("statusUb", ConstantUtil.VV_STATUS_UB);
				// queryBuf.append(" AND sc.co_cd = :pCoCd ");
			} else {
				queryBuf.append(" AND vc.vv_status_ind in (:statusBr, :statusUb) ");
				paramMap.put("statusBr", ConstantUtil.VV_STATUS_BR);
				paramMap.put("statusUb", ConstantUtil.VV_STATUS_UB);
			}

			// if (!"".equalsIgnoreCase(stevCoCd.trim())) {
			// queryBuf.append(" AND co.stev_co_cd = :pStevCoCd ");
			// }

			if (!"".equalsIgnoreCase(vvCd.trim())) {
				queryBuf.append(" AND vc.vv_cd = :pVvCd ");
			}

			queryBuf.append(" ORDER BY vc.vsl_nm ");

			log.info("SQL::" + queryBuf.toString());

			if (chkOpenBal) {
				if (!"JP".equalsIgnoreCase(custCd.trim()) && !"".equalsIgnoreCase(custCd.trim())) {
					paramMap.put("pCoCd", custCd.trim());
				}

				if (!"".equalsIgnoreCase(stevCoCd.trim())) {
					paramMap.put("pStevCoCd", stevCoCd.trim());
				}
			}
			if (!"".equalsIgnoreCase(vvCd.trim())) {
				paramMap.put("pVvCd", vvCd.trim());
			}

			log.info("START: getCargoOprVesselCall  SQL " + queryBuf.toString() + " paramMap" + paramMap.toString());

			List<VesselCall> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<VesselCall>(VesselCall.class));
			log.info("getCargoOprVesselCall Result: " + lst.size());
			if (lst != null) {
				for (Iterator<VesselCall> it = lst.iterator(); it.hasNext();) {
					VesselCall oVO = (VesselCall) it.next();
					returnList.add(oVO);
				}
			}
		} catch (Exception e) {
			log.info("Exception getCargoOprVesselCall : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoOprVesselCall DAO");
		}

		return returnList;
	}

	@Override
	public List<StevedoreCompany> getCargoOprStevedore() throws BusinessException {
		return getCargoOprStevedore("", true);
	}

	private List<StevedoreCompany> getCargoOprStevedore(String vvCd, boolean chkOpenBal) throws BusinessException {
		List<StevedoreCompany> returnList = new ArrayList<StevedoreCompany>();
		try {

			log.info("START: getCargoOprStevedore  DAO vvCd: " + vvCd + " chkOpenBal: " + chkOpenBal);
			StringBuffer queryBuf = new StringBuffer();
			queryBuf.append("SELECT ");
			queryBuf.append("	sc.* ");
			queryBuf.append("FROM ");
			queryBuf.append("	TOPS.Stevedore_Company sc ");
			queryBuf.append("WHERE ");
			queryBuf.append("	stev_co_cd IN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vs.stev_co_cd ");
			queryBuf.append("	FROM ");
			queryBuf.append("		Vessel_Call vc ");
			queryBuf.append("	INNER JOIN ( ");
			queryBuf.append("		SELECT ");
			queryBuf.append("			vv_cd, ");
			queryBuf.append("			v.stev_co_cd1 stev_co_cd, ");
			queryBuf.append("			v.stev_contact1 stev_contact, ");
			queryBuf.append("			v.stev_remarks1 stev_remarks, ");
			queryBuf.append("			v.stev_rep1 stev_rep, ");
			queryBuf.append("			1 AS lineno, ");
			queryBuf.append("			last_modify_user_id ");
			queryBuf.append("		FROM ");
			queryBuf.append("			TOPS.vv_stevedore V ");
			queryBuf.append("		WHERE ");
			queryBuf.append("			stev_co_cd1 IS NOT NULL ");
			queryBuf.append("	UNION ");
			queryBuf.append("		SELECT ");
			queryBuf.append("			vv_cd, ");
			queryBuf.append("			v.stev_co_cd2 stev_co_cd, ");
			queryBuf.append("			v.stev_contact2 stev_contact, ");
			queryBuf.append("			v.stev_remarks2 stev_remarks, ");
			queryBuf.append("			v.stev_rep2 stev_rep, ");
			queryBuf.append("			2 AS lineno, ");
			queryBuf.append("			last_modify_user_id ");
			queryBuf.append("		FROM ");
			queryBuf.append("			TOPS.vv_stevedore V ");
			queryBuf.append("		WHERE ");
			queryBuf.append("			stev_co_cd2 IS NOT NULL ");
			queryBuf.append("	UNION ");
			queryBuf.append("		SELECT ");
			queryBuf.append("			vv_cd, ");
			queryBuf.append("			v.stev_co_cd3 stev_co_cd, ");
			queryBuf.append("			v.stev_contact3 stev_contact, ");
			queryBuf.append("			v.stev_remarks3 stev_remarks, ");
			queryBuf.append("			v.stev_rep3 stev_rep, ");
			queryBuf.append("			3 AS lineno, ");
			queryBuf.append("			last_modify_user_id ");
			queryBuf.append("		FROM ");
			queryBuf.append("			TOPS.vv_stevedore V ");
			queryBuf.append("		WHERE ");
			queryBuf.append("			stev_co_cd3 IS NOT NULL ) vs ON ");
			queryBuf.append("		(vc.vv_cd = vs.vv_cd) ");

			if (chkOpenBal) {
				queryBuf.append(
						" INNER JOIN Gbcc_Cargo_Open_Bal co ON (vc.vv_cd = co.vv_cd AND vs.stev_co_cd = co.stev_co_cd ) ");
			}
			queryBuf.append(" WHERE 1=1 ");
			/*
			 * if (!"JP".equalsIgnoreCase(custCd.trim()) &&
			 * !"".equalsIgnoreCase(custCd.trim())) {
			 * queryBuf.append("  AND vc.vv_status_ind in (" + "'" +
			 * ConstantUtil.VV_STATUS_BR + "'," + "'" + ConstantUtil.VV_STATUS_UB + "')" +
			 * ""); queryBuf.append("  AND sc.co_cd = :pCoCd "); } else {
			 */

			queryBuf.append(" AND  vc.vv_status_ind in ('BR','UB') ");

			// "'" + ConstantUtil.VV_STATUS_CL + "')" +

			// }
			queryBuf.append(")  ");

			queryBuf.append(" ORDER BY STEV_CO_NM");

			log.info("SQL::" + queryBuf.toString());

			// if (!"JP".equalsIgnoreCase(custCd.trim()) &&
			// !"".equalsIgnoreCase(custCd.trim())) {
			// paramMap.put("pCoCd", custCd.trim());
			// }

			// query.addScalar("stev_co_nm" , Hibernate.STRING);

			log.info("START: getCargoOprStevedore  SQL " + queryBuf.toString());

			List<StevedoreCompany> lst = namedParameterJdbcTemplate.query(queryBuf.toString(),
					new BeanPropertyRowMapper<StevedoreCompany>(StevedoreCompany.class));

			log.info("getCargoOprStevedore Result: " + lst.toString());

			if (lst != null) {
				for (Iterator<StevedoreCompany> it = lst.iterator(); it.hasNext();) {
					StevedoreCompany oVO = (StevedoreCompany) it.next();

					returnList.add(oVO);
				}
			}
		} catch (Exception e) {
			log.info("Exception getCargoOprStevedore : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoOprStevedore DAO");
		}
		return returnList;
	}

	@Override
	public List<String> getCargoOprBerth(String custCd) throws BusinessException {
		log.info("START: getCargoOprBerth  DAO  Start Obj "+" custCd:"+custCd );
		return getCargoOprBerth(custCd, true);
	}

	private List<String> getCargoOprBerth(String custCd, boolean chkOpenBal) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer queryBuf = new StringBuffer();
		List<String> returnList = new ArrayList<String>();
		try {
			log.info("START: getCargoOprBerth  DAO custCd: " + custCd + " chkOpenBal: " + chkOpenBal);
			queryBuf.append(" SELECT distinct br.berth_nbr ");
			queryBuf.append(" FROM TOPS.Berthing br ");
			queryBuf.append(" INNER JOIN Vessel_Call vc ON (br.vv_cd = vc.vv_cd) ");
			queryBuf.append(" WHERE br.shift_ind = 1 ");
			queryBuf.append(" AND berth_nbr is not null ");

			if (chkOpenBal) {
				queryBuf.append(" AND vc.vv_cd IN (");
				queryBuf.append("  SELECT co.vv_cd FROM ");
				queryBuf.append("  GBCC.Gbcc_Cargo_Open_Bal co ");
				queryBuf.append("  INNER JOIN stevedore_company sc ON (co.stev_co_cd = sc.stev_co_cd) ");
				queryBuf.append("  WHERE 1=1 ");

				if (!"JP".equalsIgnoreCase(custCd.trim()) && !"".equalsIgnoreCase(custCd.trim())) {
					queryBuf.append(" AND sc.co_cd = :pCoCd ");

				}
				queryBuf.append(")");
			}

			if (!"JP".equalsIgnoreCase(custCd.trim()) && !"".equalsIgnoreCase(custCd.trim())) {
				queryBuf.append(" AND vc.vv_status_ind in (:statusBr, :statusUb) ");
				paramMap.put("statusBr", ConstantUtil.VV_STATUS_BR);
				paramMap.put("statusUb", ConstantUtil.VV_STATUS_UB);
				// queryBuf.append("" +
				// " AND sc.co_cd = :pCoCd " +
				// "");
			} else {
				queryBuf.append(" AND vc.vv_status_ind in (:statusBr, :statusUb, :statusCl) ");
				paramMap.put("statusBr", ConstantUtil.VV_STATUS_BR);
				paramMap.put("statusUb", ConstantUtil.VV_STATUS_UB);
				paramMap.put("statusCl", ConstantUtil.VV_STATUS_CL);
			}
			queryBuf.append(" GROUP BY br.berth_nbr ");
			queryBuf.append(" ORDER BY br.berth_nbr ");

			log.info("SQL::" + queryBuf.toString());
			if (chkOpenBal) {
				if (!"JP".equalsIgnoreCase(custCd.trim()) && !"".equalsIgnoreCase(custCd.trim())) {
					paramMap.put("pCoCd", custCd.trim());
				}
			}
			log.info("START: getCargoOprBerth  SQL " + queryBuf.toString() + " paramMap: "+paramMap.toString());

			List<String> lst = namedParameterJdbcTemplate.queryForList(queryBuf.toString(), paramMap, String.class);
			log.info("getCargoOprBerth Result" + lst.toString());

			if (lst != null) {
				for (Iterator<String> it = lst.iterator(); it.hasNext();) {
					String berthNo = (String) it.next();
					returnList.add(berthNo);
				}
			}
		} catch (Exception e) {
			log.info("Exception getCargoOprBerth : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoOprBerth DAO");
		}
		return returnList;
	}

	@Override
	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	public List<GbccViewVvStevedore> getViewVvStevedoreByVvCd(String vvCd, String coCd) throws BusinessException {
		List<GbccViewVvStevedore> returnList = new ArrayList<GbccViewVvStevedore>();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getViewVvStevedoreByVvCd  DAO vvCd: " + vvCd + " coCd: " + coCd);
			StringBuffer queryBuf = new StringBuffer();
			queryBuf.append("SELECT ");
			queryBuf.append("	vs.*, ");
			queryBuf.append("	sc.stev_co_nm STEVEDORECOMPANYNAME ");
			queryBuf.append("FROM ");
			queryBuf.append("	( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd1 stev_co_cd, ");
			queryBuf.append("		v.stev_contact1 stev_contact, ");
			queryBuf.append("		v.stev_remarks1 stev_remarks, ");
			queryBuf.append("		v.stev_rep1 stev_rep, ");
			queryBuf.append("		1 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd1 IS NOT NULL ");
			queryBuf.append("UNION ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd2 stev_co_cd, ");
			queryBuf.append("		v.stev_contact2 stev_contact, ");
			queryBuf.append("		v.stev_remarks2 stev_remarks, ");
			queryBuf.append("		v.stev_rep2 stev_rep, ");
			queryBuf.append("		2 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd2 IS NOT NULL ");
			queryBuf.append("UNION ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd3 stev_co_cd, ");
			queryBuf.append("		v.stev_contact3 stev_contact, ");
			queryBuf.append("		v.stev_remarks3 stev_remarks, ");
			queryBuf.append("		v.stev_rep3 stev_rep, ");
			queryBuf.append("		3 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd3 IS NOT NULL ) vs ");
			queryBuf.append("INNER JOIN stevedore_company sc ON ");
			queryBuf.append("	(vs.stev_co_cd = sc.stev_co_cd) ");
			queryBuf.append("LEFT JOIN company_code cc ON ");
			queryBuf.append("	(sc.co_cd = cc.co_cd) ");
			queryBuf.append("WHERE ");
			queryBuf.append("	vs.vv_cd = :pVvCd ");

			if (!"JP".equalsIgnoreCase(coCd.trim()) && (!"".equalsIgnoreCase(coCd.trim())))
				queryBuf.append(" and cc.co_cd = :pCoCd ");

			paramMap.put("pVvCd", vvCd.trim());

			if (!"JP".equalsIgnoreCase(coCd.trim()) && (!"".equalsIgnoreCase(coCd.trim())))
				paramMap.put("pCoCd", coCd.trim());

			log.info(
					"START: getViewVvStevedoreByVvCd  SQl " + queryBuf.toString() + " paramMap " + paramMap.toString());
			List<GbccViewVvStevedore> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<GbccViewVvStevedore>(GbccViewVvStevedore.class));
			log.info("getViewVvStevedoreByVvCd Result: " + lst.toString());
			GbccViewVvStevedore oVo = null;
			if (lst != null) {
				for (GbccViewVvStevedore gbccViewVvStevedore : lst) {
					oVo = new GbccViewVvStevedore();
					GbccViewVvStevedoreId id = new GbccViewVvStevedoreId();
					oVo = gbccViewVvStevedore;
					id.setVvCd(oVo.getVvCd());
					id.setLineno(oVo.getLineno());
					id.setStevCoCd(oVo.getStevCoCd());
					oVo.setId(id);

					oVo.setStevedoreCompanyName(oVo.getStev_co_nm());
					returnList.add(oVo);
				}
			}
		} catch (Exception e) {
			log.info("Exception getViewVvStevedoreByVvCd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getViewVvStevedoreByVvCd DAO");
		}
		return returnList;
	}

	@Override
	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	// get Cargo Operation Plan By VvCd, StevCoCd and Create DateTime
	public GbccCargoOprVO getCargoOprById(String vvCd, String stevCd, Date crDttm, String shiftCd, Date shiftDttm,
			String custCd) throws BusinessException {
		GbccCargoOprVO oVO = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getCargoOprById  DAO vvCd: " + vvCd + " stevCd: " + stevCd + " crDttm: " + crDttm
					+ " shiftCd:" + shiftCd + " shiftDttm: " + shiftDttm + " custCd: " + custCd);
			vvCd = (vvCd == null ? "" : vvCd);
			stevCd = (stevCd == null ? "" : stevCd);
			shiftCd = (shiftCd == null ? "" : shiftCd);
			custCd = (custCd == null ? "" : custCd);

			StringBuffer queryBuf = new StringBuffer();
			queryBuf.append("SELECT ");
			queryBuf.append("	co.VV_CD vvCd, co.STEV_CO_CD stev_CoCd, co.CREATE_DTTM, co.SHIFT_CD, co.SHIFT_DTTM , ");
			queryBuf.append(
					"	co.CHECKER_NM, co.CHECKER_OFFICE_NBR, co.CHECKER_HP_NBR, co.GANGS_NBR, co.DISC_START_DTTM , ");
			queryBuf.append(
					"	co.DISC_END_DTTM, co.LOAD_START_DTTM, co.LOAD_END_DTTM, co.LAST_MODIFY_USER_ID, co.LAST_MODIFY_DTTM , ");
			queryBuf.append(
					"	co.UNDER_PERF_REASON_CD, co.UNDER_PERF_REMARKS, co.SHIFT_END_DTTM, co.SHIFT_START_DTTM, co.DISC_DELAY_REASON_CD1 , ");
			queryBuf.append(
					"	co.DISC_DELAY_REASON_CD2, co.DISC_DELAY_REASON_REMARKS, co.LOAD_DELAY_REASON_CD1, co.LOAD_DELAY_REASON_CD2, co.LOAD_DELAY_REASON_REMARKS , ");

			queryBuf.append(
					"	vc.VV_CD vv_Cd, vc.VSL_NM, vc.IN_VOY_NBR, vc.OUT_VOY_NBR, vc.VV_STATUS_IND, vc.SHPG_SVC_CD, vc.SHPG_ROUTE_NBR,  ");
			queryBuf.append(
					"	vc.ROUTE_NM, vc.VSL_OPR_CD, vc.BERTH_APPL_DTTM, vc.VSL_BERTH_DTTM, vc.VSL_ETD_DTTM, vc.VV_CLOSE_DTTM, ");
			queryBuf.append(
					"	vc.PORT_FR, vc.PORT_TO, vc.ARRIVAL_DRAFT, vc.DEPARTURE_DRAFT, vc.BERTH_SIDE_IND, vc.CONTACT_NM, ");
			queryBuf.append(
					"	vc.CONTACT_HOME_TEL, vc.CONTACT_OFF_TEL, vc.CONTACT_PGR, vc.CONTACT_FAX, vc.CNTR_DISC, vc.CNTR_LOAD, vc.UC_LOAD, vc.UC_DISC, vc.COB_DTTM, vc.BERTH_APPL_REM, ");
			queryBuf.append(
					"	vc.BILL_MARINE_IND, vc.BILL_STEV_IND, vc.BILL_OTHER_IND, vc.BILL_ADMIN_IND, vc.CREATE_USER_ID, vc.CREATE_ACCT_NBR, vc.LAST_MODIFY_USER_ID vsl_lastModifyUserId, vc.LAST_MODIFY_DTTM vsl_lastModifyDttm, ");
			queryBuf.append(
					"	vc.SCHEME, vc.TERMINAL, vc.VSL_LOA, vc.LOC_FR, vc.LOC_TO, vc.ALONGSIDE_DRAFT, vc.VSL_UNDER_TOW_IND, vc.CONTACT2_NM, ");
			queryBuf.append(
					"	vc.CONTACT2_HOME_TEL, vc.CONTACT2_OFF_TEL, vc.CONTACT2_PGR, vc.CONTACT2_FAX, vc.TOTAL_CARGO_ONBOARD, vc.LOAD_DISPLACEMENT, vc.DG_CARGO_IND, vc.HLIFT, ");
			queryBuf.append(
					"	vc.HLIFT_OVERWHARF, vc.HLIFT_OVERSIDE, vc.MOBILE_CR_DWT, vc.MOBILE_CR_SWL, vc.CNTR_VSL_IND, vc.GB_CLOSE_VSL_IND, vc.GB_CLOSE_BJ_IND, vc.GB_CLOSE_SHP_IND, ");
			queryBuf.append(
					"	vc.CARGO_MODE, vc.BERTH_ALLOC_REM, vc.STORAGE_SPACE_IND, vc.BILL_ACCT_NBR, vc.GB_ARRIVAL_WAIVER_CD, vc.GB_ARRIVAL_WAIVER_REASON, vc.GB_DEPARTURE_WAIVER_CD, vc.GB_DEPARTURE_WAIVER_REASON, ");
			queryBuf.append(
					"	vc.GB_BERT_BILL_IND, vc.BRIDGE_DIST_FROM_BOW, vc.VACATE_BERTH_IND, vc.MIXED_SCHEME_IND, vc.DISC_CM_CD, vc.LOAD_CM_CD, vc.DISC_BER_REM, vc.LOAD_BER_REM, ");
			queryBuf.append(
					"	vc.CRG_DET_PROC, vc.DECLARANT_CUST_CD, vc.UC_DISC_CM_CD, vc.UC_LOAD_CM_CD, vc.UC_DISC_BER_REM, vc.UC_LOAD_BER_REM, vc.EST_LONG_CR_MOVE_NBR, vc.EST_THROUGHPUT_NBR, ");
			queryBuf.append(
					"	vc.SENT_TO_PSA_IND, vc.ADVICE_DTTM, vc.ABBR_IN_VOY_NBR, vc.ABBR_OUT_VOY_NBR, vc.INCENTIVE_CLASS, vc.ISPS_LEVEL, vc.BILL_OPEN_TS_IND, vc.SMS_ALERT_REP1_IND, ");
			queryBuf.append(
					"	vc.SMS_ALERT_REP2_IND, vc.TANDEM_LIFT_IND tandemLiftInd, vc.BILL_PROD_SURCHRG_IND, vc.ALLOC_PROD_PRD, vc.REEFER_PARTY, vc.PROTRUSION_IND, vc.FLOAT_CRANE_IND, vc.FIRST_SCHEME, ");
			queryBuf.append(
					"	vc.GB_CLOSE_LCT_IND, vc.GB_CLOSE_LCT_DTTM, vc.GB_CLOSE_LCT_USER_ID, vc.STORAGE_DET_PROC, vc.GB_ARRIVAL_WAIVER_IND, vc.GB_ARRIVAL_WAIVER_AMOUNT, vc.GB_AUTO_CLOSE_BJ_IND, vc.GB_AUTO_CLOSE_SHP_IND, ");
			queryBuf.append(
					"	vc.LAST_ATU_DTTM, vc.CARGO_WT, vc.TOTAL_CARGO_WT_MOBILE_CR_DWT, vc.LCT_MIN_CHRG_IND, vc.CEMENT_VSL_IND, vc.BTR_AFT_CLOSING1_DTTM, vc.NOM_IND, vc.NOM_PROCESS_IND, ");
			queryBuf.append(
					"	vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND, vc.PRINCIPAL_CARRIER_CD, vc.USE_SHP_CRN_IND, ");

			queryBuf.append(
					"	vs.VV_CD stev_VvCd, vs.STEV_CO_CD stevCoCd, vs.STEV_CONTACT, vs.STEV_REMARKS, vs.STEV_REP, vs.LINENO, vs.LAST_MODIFY_USER_ID gbccView_lastModifyUserId, ");

			queryBuf.append("	sc.stev_co_nm, ");
			queryBuf.append("	cc.co_nm , ");
			queryBuf.append("	bal.tot_disc_ton tot_disc_openbal , ");
			queryBuf.append("	bal.tot_load_ton tot_load_openbal , ");
			queryBuf.append("	nvl(opr.total_completed_disc, 0) total_completed_disc , ");
			queryBuf.append("	nvl(opr.total_completed_load, 0) total_completed_load ");
			queryBuf.append("FROM ");
			queryBuf.append("	TOPS.vessel_call vc ");
			queryBuf.append("INNER JOIN Company_Code cc ON ");
			queryBuf.append("	(vc.vsl_opr_cd = cc.co_cd) ");
			queryBuf.append("INNER JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd1 stev_co_cd, ");
			queryBuf.append("		v.stev_contact1 stev_contact, ");
			queryBuf.append("		v.stev_remarks1 stev_remarks, ");
			queryBuf.append("		v.stev_rep1 stev_rep, ");
			queryBuf.append("		1 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd1 IS NOT NULL ");
			queryBuf.append("UNION ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd2 stev_co_cd, ");
			queryBuf.append("		v.stev_contact2 stev_contact, ");
			queryBuf.append("		v.stev_remarks2 stev_remarks, ");
			queryBuf.append("		v.stev_rep2 stev_rep, ");
			queryBuf.append("		2 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd2 IS NOT NULL ");
			queryBuf.append("UNION ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd3 stev_co_cd, ");
			queryBuf.append("		v.stev_contact3 stev_contact, ");
			queryBuf.append("		v.stev_remarks3 stev_remarks, ");
			queryBuf.append("		v.stev_rep3 stev_rep, ");
			queryBuf.append("		3 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd3 IS NOT NULL ) vs ON ");
			queryBuf.append("	(vc.vv_cd = vs.vv_cd) ");
			queryBuf.append("INNER JOIN stevedore_company sc ON ");
			queryBuf.append("	(vs.stev_co_cd = sc.stev_co_cd) ");
			queryBuf.append("INNER JOIN GBCC_Cargo_Open_Bal bal ON ");
			queryBuf.append("	(vc.vv_cd = bal.vv_cd ");
			queryBuf.append("	AND vs.stev_co_cd = bal.stev_co_cd) ");
			queryBuf.append("LEFT JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		opr.vv_cd, ");
			queryBuf.append("		opr.stev_co_cd, ");
			queryBuf.append("		sum(det.disc_completed_ton) total_completed_disc, ");
			queryBuf.append("		sum(det.load_completed_ton) total_completed_load ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBCC.gbcc_cargo_opr opr ");
			queryBuf.append("	INNER JOIN gbcc_cargo_opr_det det ON ");
			queryBuf.append("		(opr.vv_cd = det.vv_cd ");
			queryBuf.append("		AND opr.stev_co_cd = det.stev_co_cd ");
			queryBuf.append("		AND opr.create_dttm = det.create_dttm) ");
			queryBuf.append("	GROUP BY ");
			queryBuf.append("		opr.vv_cd, ");
			queryBuf.append("		opr.stev_co_cd ) opr ON ");
			queryBuf.append("	(vc.vv_cd = opr.vv_cd ");
			queryBuf.append("	AND vs.stev_co_cd = opr.stev_co_cd) ");
			queryBuf.append("LEFT JOIN GBCC_Cargo_Opr co ON ");
			queryBuf.append("	(vc.vv_cd = co.vv_cd ");
			queryBuf.append("	AND co.stev_co_cd = vs.stev_co_cd ");

			if (crDttm == null) {
				if (shiftDttm != null) {
					queryBuf.append(" AND TO_CHAR(co.shift_dttm,'dd/MM/yyyy') = :pShiftDttm ");
				} else
					queryBuf.append(" AND co.shift_cd IS NULL ");
			}
			if (!shiftCd.equalsIgnoreCase(""))
				queryBuf.append(" AND co.shift_cd = :pShiftCd ");

			queryBuf.append(") ");
			queryBuf.append(
					" LEFT JOIN Misc_Type_Code mtc1 ON (co.shift_cd = mtc1.misc_type_cd AND mtc1.cat_cd = :pCatCd1) ");
			queryBuf.append(" WHERE vc.vv_cd = :pVvCd  ");

			if (!stevCd.equalsIgnoreCase(""))
				queryBuf.append(" AND vs.stev_co_cd = :pStevCoCd ");
			if (!custCd.equalsIgnoreCase("") && !custCd.equalsIgnoreCase("JP"))
				queryBuf.append(" AND sc.co_cd = :pCoCd ");

			if (crDttm != null)
				queryBuf.append(" AND co.create_dttm = :pCreateDttm ");

			log.info("SQL::" + queryBuf.toString());

			if (crDttm == null) {
				if (shiftDttm != null)
					paramMap.put("pShiftDttm", CommonUtil.formatDateToStr(shiftDttm, "dd/MM/yyyy"));
			}
			if (!shiftCd.equalsIgnoreCase(""))
				paramMap.put("pShiftCd", shiftCd.trim());

			paramMap.put("pCatCd1", ConstantUtil.MISCTYPECD_SHIFT_STEV);

			paramMap.put("pVvCd", vvCd.trim());

			if (!stevCd.equalsIgnoreCase(""))
				paramMap.put("pStevCoCd", stevCd.trim());

			if (!custCd.equalsIgnoreCase("") && !custCd.equalsIgnoreCase("JP"))
				paramMap.put("pCoCd", custCd.trim());

			if (crDttm != null) {
				Timestamp crTs = new Timestamp(crDttm.getTime());
				paramMap.put("pCreateDttm", crTs);
			}

			log.info(": getCargoOprById  SQL " + queryBuf.toString() + " paramMap " + paramMap.toString());
			List<GbccCargoOprVO> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<GbccCargoOprVO>(GbccCargoOprVO.class));

		if(lst.size()>1) {
			oVO=null;
			return oVO;
		}
			if (lst.isEmpty())
				return null;
			// GbccCargoOprId id = null;

			for (GbccCargoOprVO gbccCargoOprVO : lst) {
				// id = new GbccCargoOprId();
				oVO = new GbccCargoOprVO();
				oVO = gbccCargoOprVO;

				GbccViewVvStevedore stevVO = new GbccViewVvStevedore();
				stevVO.setId(oVO.getGbcc_id());
				stevVO.setLastModifyUserId(oVO.getLastModifyUserId());
				stevVO.setLineno(oVO.getLineno());
				stevVO.setStev_co_nm(oVO.getStev_co_nm());
				stevVO.setStevCoCd(oVO.getStevCoCd());
				stevVO.setStevContact(oVO.getStevContact());
				stevVO.setStevedoreCompanyName(oVO.getStevedoreCompanyName());
				stevVO.setStevRemarks(oVO.getStevRemarks());
				stevVO.setStevRep(oVO.getStevRep());
				stevVO.setVvCd(oVO.getVvCd());
				// stevCd = stevVO.getId().getStevCoCd();

				if (oVO.getVvCd() != null && oVO.getStevCoCd() != null && oVO.getCreateDttm() != null) {
					GbccCargoOprId id = new GbccCargoOprId();
					id.setVvCd(oVO.getVvCd());
					id.setStevCoCd(oVO.getStevCoCd());
					id.setCreateDttm(oVO.getCreateDttm());
					oVO.setId(id);

					oVO.setCargoOprDetVO(getCargoOprDet(vvCd, stevCd, oVO.getCreateDttm()));
				} else {

					oVO.setCargoOprDetVO(getCargoOprDet(vvCd, stevCd, null));
				}

				VesselCall vSc = new VesselCall();

				vSc.setAbbrInVoyNbr(oVO.getAbbrInVoyNbr());
				vSc.setAdviceDttm(oVO.getAdviceDttm());
				vSc.setAllocProdPrd(oVO.getAllocProdPrd());
				vSc.setAlongsideDraft(oVO.getAlongsideDraft());
				vSc.setBerthAllocRem(oVO.getBerthAllocRem());
				vSc.setBerthApplDttm(oVO.getBerthApplDttm());
				vSc.setBerthApplRem(oVO.getBerthApplRem());
				vSc.setBerthSideInd(oVO.getBerthSideInd());
				vSc.setBillAcctNbr(oVO.getBillAcctNbr());
				vSc.setBillAdminInd(oVO.getBillAdminInd());
				vSc.setBillMarineInd(oVO.getBillMarineInd());
				vSc.setBillOpenTsInd(oVO.getBillOpenTsInd());
				vSc.setBillOtherInd(oVO.getBillOtherInd());
				vSc.setBillProdSurchrgInd(oVO.getBillProdSurchrgInd());
				vSc.setBillStevInd(oVO.getBillStevInd());
				vSc.setBridgeDistFromBow(oVO.getBridgeDistFromBow());
				vSc.setCargoMode(oVO.getCargoMode());
				vSc.setCntrDisc(oVO.getCntrDisc());
				vSc.setCntrLoad(oVO.getCntrLoad());
				vSc.setCntrVslInd(oVO.getCntrVslInd());
				vSc.setCobDttm(oVO.getCobDttm());
				vSc.setContact2Fax(oVO.getContact2Fax());
				vSc.setContact2HomeTel(oVO.getContact2HomeTel());
				vSc.setContact2Nm(oVO.getContact2Nm());
				vSc.setContact2OffTel(oVO.getContact2OffTel());
				vSc.setContact2Pgr(oVO.getContact2Pgr());
				vSc.setContactFax(oVO.getContactFax());
				vSc.setContactHomeTel(oVO.getContactHomeTel());
				vSc.setContactNm(oVO.getContactNm());
				vSc.setContactOffTel(oVO.getContactOffTel());
				vSc.setContactPgr(oVO.getContactPgr());
				vSc.setCreateAcctNbr(oVO.getCreateAcctNbr());
				vSc.setCreateCustCd(oVO.getCreateCustCd());
				vSc.setCreateUserId(oVO.getCreateUserId());
				vSc.setCrgDetProc(oVO.getCrgDetProc());
				vSc.setDeclarantCustCd(oVO.getDeclarantCustCd());
				vSc.setDepartureDraft(oVO.getDepartureDraft());
				vSc.setDgCargoInd(oVO.getDgCargoInd());
				vSc.setDiscBerRem(oVO.getDiscBerRem());
				vSc.setDiscCmCd(oVO.getDiscCmCd());
				vSc.setEstLongCrMoveNbr(oVO.getEstLongCrMoveNbr());
				vSc.setEstThroughputNbr(oVO.getEstThroughputNbr());
				vSc.setFloatCraneInd(oVO.getFloatCraneInd());
				vSc.setGbArrivalWaiverCd(oVO.getGbArrivalWaiverCd());
				vSc.setGbArrivalWaiverReason(oVO.getGbArrivalWaiverReason());
				vSc.setGbBertBillInd(oVO.getGbBertBillInd());
				vSc.setGbCloseBjInd(oVO.getGbCloseBjInd());
				vSc.setGbCloseProdInd(oVO.getGbCloseProdInd());
				vSc.setGbCloseShpInd(oVO.getGbCloseShpInd());
				vSc.setGbCloseVslInd(oVO.getGbCloseVslInd());
				vSc.setGbDepartureWaiverCd(oVO.getGbDepartureWaiverCd());
				vSc.setGbDepartureWaiverReason(oVO.getGbDepartureWaiverReason());
				vSc.setHlift(oVO.getHlift());
				vSc.setHliftOverside(oVO.getHliftOverside());
				vSc.setHliftOverwharf(oVO.getHliftOverwharf());
				vSc.setIncentiveClass(oVO.getIncentiveClass());
				vSc.setInVoyNbr(oVO.getInVoyNbr());
				vSc.setIspsLevel(oVO.getIspsLevel());
				vSc.setLastAtuDttm(oVO.getLastAtuDttm());
				vSc.setLastModifyDttm(oVO.getLastModifyDttm());
				vSc.setLastModifyUserId(oVO.getLastModifyUserId());
				vSc.setLoadBerRem(oVO.getLoadBerRem());
				vSc.setLoadCmCd(oVO.getLoadCmCd());
				vSc.setLoadDisplacement(oVO.getLoadDisplacement());
				vSc.setLocFr(oVO.getLocFr());
				vSc.setLocTo(oVO.getLocTo());
				vSc.setMixedSchemeInd(oVO.getMixedSchemeInd());
				vSc.setMobileCrDwt(oVO.getMobileCrDwt());
				vSc.setMobileCrSwl(oVO.getMobileCrSwl());
				vSc.setOutVoyNbr(oVO.getOutVoyNbr());
				vSc.setPortFr(oVO.getPortFr());
				vSc.setPortTo(oVO.getPortTo());
				vSc.setProtrusionInd(oVO.getProtrusionInd());
				vSc.setReeferParty(oVO.getReeferParty());
				vSc.setRouteNm(oVO.getRouteNm());
				vSc.setSentToPsaInd(oVO.getSentToPsaInd());
				vSc.setScheme(oVO.getScheme());
				vSc.setShpgRouteNbr(oVO.getShpgRouteNbr());
				vSc.setShpgSvcCd(oVO.getShpgSvcCd());
				vSc.setSmsAlertRep1Ind(oVO.getSmsAlertRep1Ind());
				vSc.setSmsAlertRep2Ind(oVO.getSmsAlertRep2Ind());
				vSc.setStorageSpaceInd(oVO.getStorageSpaceInd());
				vSc.setTandemLiftInd(oVO.getTandemLiftInd());
				vSc.setTerminal(oVO.getTerminal());
				vSc.setTotalCargoOnboard(oVO.getTotalCargoOnboard());
				vSc.setUcDisc(oVO.getUcDisc());
				vSc.setUcDiscBerRem(oVO.getUcDiscBerRem());
				vSc.setUcDiscCmCd(oVO.getUcDiscCmCd());
				vSc.setUcLoad(oVO.getUcLoad());
				vSc.setUcLoadBerRem(oVO.getUcLoadBerRem());
				vSc.setUcLoadCmCd(oVO.getUcLoadCmCd());
				vSc.setVacateBerthInd(oVO.getVacateBerthInd());
				vSc.setVslBerthDttm(oVO.getVslBerthDttm());
				vSc.setVslEtdDttm(oVO.getVslEtdDttm());
				vSc.setVslLoa(oVO.getVslLoa());
				vSc.setVslNm(oVO.getVslNm());
				vSc.setVslOprCd(oVO.getVslOprCd());
				vSc.setVslUnderTowInd(oVO.getVslUnderTowInd());
				vSc.setVvCd(oVO.getVvCd());
				vSc.setVvCloseDttm(oVO.getVvCloseDttm());
				vSc.setVvStatusInd(oVO.getVvStatusInd());
				vSc.setVvStatusIndText(oVO.getVvStatusIndText());

				oVO.setVesselCallVO(vSc);
				oVO.setVesselStevedoreVO(stevVO);
				oVO.setStevedoreCompanyName(oVO.getStev_co_nm());
				oVO.setAgentName(oVO.getCo_nm());

				oVO.setTotDiscOpenBal((Integer) oVO.getTot_disc_openbal());
				oVO.setTotLoadOpenBal((Integer) oVO.getTot_load_openbal());

				oVO.setTotDiscTotalCompleted((Integer) oVO.getTotal_completed_disc());
				oVO.setTotLoadTotalCompleted((Integer) oVO.getTotal_completed_load());

				Berthing firstBerthingVO = getFirstBerthing(vvCd);
				oVO.setFirstBerthingVO(firstBerthingVO);

				Berthing lastBerthingVO = getLastBerthing(vvCd);
				oVO.setLastBerthingVO(lastBerthingVO);
				stevCd = oVO.getStevCoCd();
				// id.setCreateDttm(oVO.getCreateDttm());
				// id.setStevCoCd(stevCd);
				// id.setVvCd(oVO.getVvCd());
				// oVO.setId(id);
				oVO.recalculteTotal();
			}

			log.info("getCargoOprById Result" + oVO.toString());

		} catch (Exception e) {
			log.info("Exception getCargoOprById : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoOprById DAO");
		}
		return oVO;
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	private List<GbccCargoOprDet> getCargoOprDet(String vvCd, String stevCoCd, Date crDttm) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer queryBuf = new StringBuffer();
		List<GbccCargoOprDet> returnList = new ArrayList<GbccCargoOprDet>();
		try {
			log.info("START: getCargoOprDet  DAO vvCd: " + vvCd + " stevCoCd: " + stevCoCd + " crDttm: " + crDttm);
			queryBuf.append("SELECT ");
			queryBuf.append("	det.* , ");
			queryBuf.append("	bal.disc_open_bal_ton , ");
			queryBuf.append("	bal.load_open_bal_ton , ");
			queryBuf.append("	nvl(total_disc_completed, 0) total_disc_completed , ");
			queryBuf.append("	nvl(total_load_completed, 0) total_load_completed , ");
			queryBuf.append("	(bal.disc_open_bal_ton - nvl(total_disc_completed, 0)) bal_disc_ton , ");
			queryBuf.append("	(bal.load_open_bal_ton - nvl(total_load_completed, 0)) bal_load_ton , ");
			queryBuf.append("	bal.hatch_nbr bal_hatch_nbr ");
			queryBuf.append("FROM ");
			queryBuf.append("	GBCC.GBCC_cargo_open_bal_det bal ");
			queryBuf.append("LEFT JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		stev_co_cd, ");
			queryBuf.append("		hatch_nbr , ");
			queryBuf.append("		sum(disc_completed_ton) total_disc_completed, ");
			queryBuf.append("		sum(load_completed_ton) total_load_completed ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBCC.gbcc_cargo_opr_det ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		vv_cd = :pVvCd0 ");
			queryBuf.append("		AND stev_co_cd = :pStevCd0 ");

			if (crDttm != null) {
				queryBuf.append("" + "  AND create_dttm <> :pCrDttm0 " + "");
			}
			queryBuf.append(" GROUP BY vv_cd, ");
			queryBuf.append("stev_co_cd, ");
			queryBuf.append("hatch_nbr ) opr ON ");
			queryBuf.append("(opr.vv_cd = opr.vv_cd ");
			queryBuf.append("AND opr.stev_co_cd = opr.stev_co_cd ");
			queryBuf.append("AND opr.hatch_nbr = bal.hatch_nbr) ");
			queryBuf.append("LEFT JOIN gbcc_cargo_opr_det det ON ");
			queryBuf.append("(det.vv_cd = bal.vv_cd ");
			queryBuf.append("AND det.stev_co_cd = bal.stev_co_cd ");
			queryBuf.append("AND det.hatch_nbr = bal.hatch_nbr ");

			if (crDttm != null) {
				queryBuf.append(" and det.create_dttm = :pCrDttm1 ");
			} else {
				queryBuf.append("" + " and det.create_dttm is null " + "");
			}
			queryBuf.append(
					" ) " + " WHERE bal.vv_cd = :pVvCd  AND bal.stev_co_cd = :pStevCd  order by bal.hatch_nbr  ");

			log.info("SQL::" + queryBuf.toString());

			Timestamp crTs = null;
			if (crDttm != null)
				crTs = new Timestamp(crDttm.getTime());

			paramMap.put("pVvCd0", vvCd.trim());
			paramMap.put("pStevCd0", stevCoCd.trim());

			if (crDttm != null)
				paramMap.put("pCrDttm0", crTs);

			if (crDttm != null)
				paramMap.put("pCrDttm1", crTs);

			paramMap.put("pVvCd", vvCd.trim());
			paramMap.put("pStevCd", stevCoCd.trim());

			log.info(": getCargoOprDet  SQL " + queryBuf.toString() + " paramMap " + paramMap.toString());
			
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(queryBuf.toString(), paramMap);

			while (rs.next()) {
					GbccCargoOprDet oVO = new GbccCargoOprDet();
				GbccCargoOpr oPR = new GbccCargoOpr();
				oPR.setVv_Cd(rs.getString("VV_CD"));
				oPR.setStev_co_cd(rs.getString("STEV_CO_CD"));
				oPR.setCreate_dttm(rs.getDate("CREATE_DTTM"));
				oPR.setHatch_nbr(rs.getInt("HATCH_NBR"));
				oPR.setDisc_completed_ton(rs.getInt("DISC_COMPLETED_TON"));
				oPR.setLoad_completed_ton(rs.getInt("LOAD_COMPLETED_TON"));
				oPR.setLast_modify_user_id(rs.getString("LAST_MODIFY_USER_ID"));
				oPR.setLast_modify_dttm(rs.getDate("LAST_MODIFY_DTTM"));
				oPR.setDisc_remarks(rs.getString("DISC_REMARKS"));
				oPR.setLoad_remarks(rs.getString("LOAD_REMARKS"));
				oPR.setDisc_weather_cd(rs.getString("DISC_WEATHER_CD"));
				oPR.setDisc_activity_cd(rs.getString("DISC_ACTIVITY_CD"));
				oPR.setLoad_weather_cd(rs.getString("LOAD_WEATHER_CD"));
				oPR.setLoad_activity_cd(rs.getString("LOAD_ACTIVITY_CD"));
					
				oVO.setGbcc(oPR);
				if (oVO.getGbcc() != null) {
					oVO.setVvCd(rs.getString("VV_CD"));
					oVO.setStevCoCd(rs.getString("STEV_CO_CD"));
					oVO.setCreateDttm(rs.getDate("CREATE_DTTM"));
					oVO.setHatchNbr(rs.getInt("HATCH_NBR"));
					oVO.setDiscCompletedTon(rs.getInt("DISC_COMPLETED_TON"));
					oVO.setLoadCompletedTon(rs.getInt("LOAD_COMPLETED_TON"));
					oVO.setLastModifyUserId(rs.getString("LAST_MODIFY_USER_ID"));
					oVO.setLastModifyDttm(rs.getDate("LAST_MODIFY_DTTM"));
					oVO.setDiscRemarks(rs.getString("DISC_REMARKS"));
					oVO.setLoadRemarks(rs.getString("LOAD_REMARKS"));
					oVO.setDiscWeatherCd(rs.getString("DISC_WEATHER_CD"));
					oVO.setDiscActivityCd(rs.getString("DISC_ACTIVITY_CD"));
					oVO.setLoadActivityCd(rs.getString("LOAD_WEATHER_CD"));
					oVO.setLoadActivityCd(rs.getString("LOAD_ACTIVITY_CD"));
				} else {
						oVO = new GbccCargoOprDet();
						GbccCargoOprDetId oId = new GbccCargoOprDetId();
						oId.setHatchNbr(rs.getInt("BAL_HATCH_NBR"));
						oId.setVvCd(vvCd);
						oId.setStevCoCd(stevCoCd);
						oVO.setId(oId);
					}

				oVO.setDiscOpenBal(rs.getInt("DISC_OPEN_BAL_TON"));
					oVO.setLoadOpenBal(rs.getInt("LOAD_OPEN_BAL_TON"));
					oVO.setDiscTotalCompleted(rs.getInt("TOTAL_DISC_COMPLETED"));
					oVO.setLoadTotalCompleted(rs.getInt("TOTAL_LOAD_COMPLETED"));
					oVO.setDiscBal(rs.getInt("BAL_DISC_TON"));
					oVO.setLoadBal(rs.getInt("BAL_LOAD_TON"));
					returnList.add(oVO);
				
			}

			log.info("getCargoOprDet Result: " + returnList.toString());
		} catch (Exception e) {
			log.info("Exception getCargoOprDet : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoOprDet DAO");
		}
		return returnList;
	}

	@Override
	public List<GbccCargoOprVO> getCargoOprByShiftDate(String vvCd, String stevCoCd, Date crDttm)
			throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		List<GbccCargoOprVO> returnList = new ArrayList<GbccCargoOprVO>();
		try {

			log.info("START: getCargoOprByShiftDate  DAO vvCd: " + vvCd + " stevCoCd: " + stevCoCd + " crDttm: "
					+ crDttm);
			StringBuffer queryBuf = new StringBuffer();
			queryBuf.append("SELECT ");
			queryBuf.append("	opr.*, ");
			queryBuf.append("	mtc.misc_type_cd ");
			queryBuf.append("FROM ");
			queryBuf.append("	TOPS.misc_type_code mtc ");
			queryBuf.append("LEFT JOIN gbcc_cargo_opr opr ON ");
			queryBuf.append("	(mtc.misc_type_cd = opr.shift_cd ");
			queryBuf.append("	AND vv_cd = :pVvCd ");
			queryBuf.append("	AND stev_co_cd = :pStevCd ");
			queryBuf.append("	AND to_char(shift_dttm, 'dd/MM/yyyy') = :pCrDttm ) ");
			queryBuf.append("WHERE ");
			queryBuf.append("	rec_status = 'A' ");
			queryBuf.append("	AND mtc.cat_cd = :pCatCd ");
			queryBuf.append("ORDER BY ");
			queryBuf.append("	misc_type_cd");

			log.info("SQL::" + queryBuf.toString());

			paramMap.put("pVvCd", vvCd.trim());
			paramMap.put("pStevCd", stevCoCd.trim());
			paramMap.put("pCrDttm", CommonUtil.formatDateToStr(crDttm, "dd/MM/yyyy"));
			paramMap.put("pCatCd", ConstantUtil.MISCTYPECD_SHIFT_STEV);

			log.info("CrDttm: " + CommonUtil.formatDateToStr(crDttm, "dd/MM/yyyy"));
			log.info(": getCargoOprByShiftDate  SQL " + queryBuf.toString() + " paramMap " + paramMap.toString());
			List<GbccCargoOprVO> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<GbccCargoOprVO>(GbccCargoOprVO.class));

			log.info("getCargoOprByShiftDate Result: " + lst.toString());

			if (lst.isEmpty())
				return returnList;
			GbccCargoOprVO oVO = null;
			GbccCargoOprId id = null;
			for (GbccCargoOprVO gbccCargoOprVO : lst) {
				id = new GbccCargoOprId();
				oVO = new GbccCargoOprVO();
				oVO = gbccCargoOprVO;
				id.setCreateDttm(oVO.getCreateDttm());
				id.setStevCoCd(oVO.getStevCoCd());
				id.setVvCd(oVO.getVvCd());
				oVO.setId(id);
				oVO.setShiftCd((String) oVO.getMisc_type_cd());
				returnList.add(oVO);

			}
		} catch (Exception e) {
			log.info("Exception getCargoOprByShiftDate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoOprByShiftDate DAO");
		}
		return returnList;
	}

	@Override
	public List<GbccCargoOprVO> getCargoOpr(String vvCd, String berthNo, String CustCode, String shiftCd,
			Date shiftDttm, String sortBy, Criteria criteria, Boolean needAllData) throws BusinessException {
		List<GbccCargoOprVO> returnList = new ArrayList<GbccCargoOprVO>();
		Map<String, Object> paramMap = new HashMap<>();
		String sql = "";
		TableData tableData = new TableData();
		try {
			StringBuffer queryBuf = new StringBuffer();
			log.info("START: getCargoOpr  DAO vvCd: " + vvCd + " berthNo: " + berthNo + " CustCode: " + CustCode
					+ " shiftCd: " + shiftCd + " shiftDttm: " + shiftDttm + " sortBy: " + sortBy);

			queryBuf.append("SELECT ");
			queryBuf.append("	co.VV_CD vvCd, co.STEV_CO_CD stevCoCd, co.CREATE_DTTM, co.SHIFT_CD, co.SHIFT_DTTM , ");
			queryBuf.append(
					"	co.CHECKER_NM, co.CHECKER_OFFICE_NBR, co.CHECKER_HP_NBR, co.GANGS_NBR, co.DISC_START_DTTM , ");
			queryBuf.append(
					"	co.DISC_END_DTTM, co.LOAD_START_DTTM, co.LOAD_END_DTTM, co.LAST_MODIFY_USER_ID, co.LAST_MODIFY_DTTM , ");
			queryBuf.append(
					"	co.UNDER_PERF_REASON_CD, co.UNDER_PERF_REMARKS, co.SHIFT_END_DTTM, co.SHIFT_START_DTTM, co.DISC_DELAY_REASON_CD1 , ");
			queryBuf.append(
					"	co.DISC_DELAY_REASON_CD2, co.DISC_DELAY_REASON_REMARKS, co.LOAD_DELAY_REASON_CD1, co.LOAD_DELAY_REASON_CD2, co.LOAD_DELAY_REASON_REMARKS , ");

			queryBuf.append(
					"	vc.VV_CD vv_Cd, vc.VSL_NM, vc.IN_VOY_NBR, vc.OUT_VOY_NBR, vc.VV_STATUS_IND, vc.SHPG_SVC_CD, vc.SHPG_ROUTE_NBR,  ");
			queryBuf.append(
					"	vc.ROUTE_NM, vc.VSL_OPR_CD, vc.BERTH_APPL_DTTM, vc.VSL_BERTH_DTTM, vc.VSL_ETD_DTTM, vc.VV_CLOSE_DTTM, ");
			queryBuf.append(
					"	vc.PORT_FR, vc.PORT_TO, vc.ARRIVAL_DRAFT, vc.DEPARTURE_DRAFT, vc.BERTH_SIDE_IND, vc.CONTACT_NM, ");
			queryBuf.append(
					"	vc.CONTACT_HOME_TEL, vc.CONTACT_OFF_TEL, vc.CONTACT_PGR, vc.CONTACT_FAX, vc.CNTR_DISC, vc.CNTR_LOAD, vc.UC_LOAD, vc.UC_DISC, vc.COB_DTTM, vc.BERTH_APPL_REM, ");
			queryBuf.append(
					"	vc.BILL_MARINE_IND, vc.BILL_STEV_IND, vc.BILL_OTHER_IND, vc.BILL_ADMIN_IND, vc.CREATE_USER_ID, vc.CREATE_ACCT_NBR, vc.LAST_MODIFY_USER_ID vsl_lastModifyUserId, vc.LAST_MODIFY_DTTM vsl_lastModifyDttm, ");
			queryBuf.append(
					"	vc.SCHEME, vc.TERMINAL, vc.VSL_LOA, vc.LOC_FR, vc.LOC_TO, vc.ALONGSIDE_DRAFT, vc.VSL_UNDER_TOW_IND, vc.CONTACT2_NM, ");
			queryBuf.append(
					"	vc.CONTACT2_HOME_TEL, vc.CONTACT2_OFF_TEL, vc.CONTACT2_PGR, vc.CONTACT2_FAX, vc.TOTAL_CARGO_ONBOARD, vc.LOAD_DISPLACEMENT, vc.DG_CARGO_IND, vc.HLIFT, ");
			queryBuf.append(
					"	vc.HLIFT_OVERWHARF, vc.HLIFT_OVERSIDE, vc.MOBILE_CR_DWT, vc.MOBILE_CR_SWL, vc.CNTR_VSL_IND, vc.GB_CLOSE_VSL_IND, vc.GB_CLOSE_BJ_IND, vc.GB_CLOSE_SHP_IND, ");
			queryBuf.append(
					"	vc.CARGO_MODE, vc.BERTH_ALLOC_REM, vc.STORAGE_SPACE_IND, vc.BILL_ACCT_NBR, vc.GB_ARRIVAL_WAIVER_CD, vc.GB_ARRIVAL_WAIVER_REASON, vc.GB_DEPARTURE_WAIVER_CD, vc.GB_DEPARTURE_WAIVER_REASON, ");
			queryBuf.append(
					"	vc.GB_BERT_BILL_IND, vc.BRIDGE_DIST_FROM_BOW, vc.VACATE_BERTH_IND, vc.MIXED_SCHEME_IND, vc.DISC_CM_CD, vc.LOAD_CM_CD, vc.DISC_BER_REM, vc.LOAD_BER_REM, ");
			queryBuf.append(
					"	vc.CRG_DET_PROC, vc.DECLARANT_CUST_CD, vc.UC_DISC_CM_CD, vc.UC_LOAD_CM_CD, vc.UC_DISC_BER_REM, vc.UC_LOAD_BER_REM, vc.EST_LONG_CR_MOVE_NBR, vc.EST_THROUGHPUT_NBR, ");
			queryBuf.append(
					"	vc.SENT_TO_PSA_IND, vc.ADVICE_DTTM, vc.ABBR_IN_VOY_NBR, vc.ABBR_OUT_VOY_NBR, vc.INCENTIVE_CLASS, vc.ISPS_LEVEL, vc.BILL_OPEN_TS_IND, vc.SMS_ALERT_REP1_IND, ");
			queryBuf.append(
					"	vc.SMS_ALERT_REP2_IND, vc.TANDEM_LIFT_IND tandemLiftInd, vc.BILL_PROD_SURCHRG_IND, vc.ALLOC_PROD_PRD, vc.REEFER_PARTY, vc.PROTRUSION_IND, vc.FLOAT_CRANE_IND, vc.FIRST_SCHEME, ");
			queryBuf.append(
					"	vc.GB_CLOSE_LCT_IND, vc.GB_CLOSE_LCT_DTTM, vc.GB_CLOSE_LCT_USER_ID, vc.STORAGE_DET_PROC, vc.GB_ARRIVAL_WAIVER_IND, vc.GB_ARRIVAL_WAIVER_AMOUNT, vc.GB_AUTO_CLOSE_BJ_IND, vc.GB_AUTO_CLOSE_SHP_IND, ");
			queryBuf.append(
					"	vc.LAST_ATU_DTTM, vc.CARGO_WT, vc.TOTAL_CARGO_WT_MOBILE_CR_DWT, vc.LCT_MIN_CHRG_IND, vc.CEMENT_VSL_IND, vc.BTR_AFT_CLOSING1_DTTM, vc.NOM_IND, vc.NOM_PROCESS_IND, ");
			queryBuf.append(
					"	vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND, vc.PRINCIPAL_CARRIER_CD, vc.USE_SHP_CRN_IND, ");

			queryBuf.append(
					"	vs.VV_CD stev_VvCd, vs.STEV_CO_CD stev_coCd, vs.STEV_CONTACT, vs.STEV_REMARKS, vs.STEV_REP, vs.LINENO, vs.LAST_MODIFY_USER_ID gbccView_lastModifyUserId, ");

			queryBuf.append(
					"	br.VV_CD berth_VvCd, br.SHIFT_IND, br.ETB_DTTM, br.ETU_DTTM, br.ATB_DTTM, br.ATU_DTTM, br.FIRST_DISC_DTTM, br.FIRST_LOAD_DTTM, br.COD_DTTM, br.COL_DTTM,  ");
			queryBuf.append(
					"	br.BERTH_NBR, br.WHARF_MARK_FR, br.WHARF_MARK_TO, br.WHARF_SIDE_IND, br.LAST_MODIFY_USER_ID berth_lastModifyUserId, br.LAST_MODIFY_DTTM berth_lastModifyDttm, br.GB_COD_DTTM, br.GB_COL_DTTM, br.GB_FIRST_ACT_DTTM, br.GB_LAST_ACT_DTTM,  ");
			queryBuf.append(
					"	br.GB_BCOD_DTTM, br.GB_BCOL_DTTM, br.HAUL_DIST, br.HAUL_DIRN, br.GANG_NBR, br.HATCH_NBR, br.DELAY_RSN_CD, br.REMARKS berthRemarks, br.TOT_GEN_CARGO_ACT, br.GB_FIRST_CARGO_ACT_DTTM, br.DEP_DRAFT_FORTH,  ");
			queryBuf.append(
					"	br.GB_FIRST_DISC_DTTM, br.GB_FIRST_LOAD_DTTM, br.ACT_DRAFT_FORTH, br.ACT_DRAFT_AFT, br.ACT_WHARF_SIDE_IND, br.ACT_WHARF_MARK_FR, br.ACT_WHARF_MARK_TO, br.BERTH_REMARKS, br.COL_MODIFY_DTTM, br.DEP_DRAFT_AFT,  ");

			queryBuf.append("	sc.stev_co_nm, ");
			queryBuf.append("	cc.co_nm, ");
			queryBuf.append("	detsum.total_disc , ");
			queryBuf.append("	detsum.total_load , ");
			queryBuf.append("	bal.OPEN_BAL_DISC tot_disc_openbal , ");
			queryBuf.append("	bal.OPEN_BAL_LOAD tot_load_openbal , ");
			queryBuf.append("	nvl(opr.total_completed_disc, 0) total_completed_disc , ");
			queryBuf.append("	nvl(opr.total_completed_load, 0) total_completed_load ");
			queryBuf.append("FROM ");
			queryBuf.append("	GBCC.gbcc_cargo_opr co ");
			queryBuf.append("INNER JOIN Vessel_Call vc ON ");
			queryBuf.append("	(co.vv_cd = vc.vv_cd) ");
			queryBuf.append("INNER JOIN Company_Code cc ON ");
			queryBuf.append("	(vc.vsl_opr_cd = cc.co_cd) ");
			queryBuf.append("INNER JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd1 stev_co_cd, ");
			queryBuf.append("		v.stev_contact1 stev_contact, ");
			queryBuf.append("		v.stev_remarks1 stev_remarks, ");
			queryBuf.append("		v.stev_rep1 stev_rep, ");
			queryBuf.append("		1 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd1 IS NOT NULL ");
			queryBuf.append("UNION ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd2 stev_co_cd, ");
			queryBuf.append("		v.stev_contact2 stev_contact, ");
			queryBuf.append("		v.stev_remarks2 stev_remarks, ");
			queryBuf.append("		v.stev_rep2 stev_rep, ");
			queryBuf.append("		2 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd2 IS NOT NULL ");
			queryBuf.append("UNION ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd3 stev_co_cd, ");
			queryBuf.append("		v.stev_contact3 stev_contact, ");
			queryBuf.append("		v.stev_remarks3 stev_remarks, ");
			queryBuf.append("		v.stev_rep3 stev_rep, ");
			queryBuf.append("		3 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd3 IS NOT NULL ) vs ON ");
			queryBuf.append("	(co.vv_cd = vs.vv_cd ");
			queryBuf.append("	AND co.stev_co_cd = vs.stev_co_cd) ");
			queryBuf.append("INNER JOIN stevedore_company sc ON ");
			queryBuf.append("	(vs.stev_co_cd = sc.stev_co_cd) ");
			queryBuf.append("INNER JOIN Berthing br ON ");
			queryBuf.append("	(co.vv_cd = br.vv_cd ");
			queryBuf.append("	AND br.shift_ind = 1) ");
			queryBuf.append("INNER JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		stev_co_cd, ");
			queryBuf.append("		create_dttm, ");
			queryBuf.append("		sum(disc_completed_ton) total_disc, ");
			queryBuf.append("		sum(load_completed_ton) total_load ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBCC.gbcc_cargo_opr_det det ");
			queryBuf.append("	GROUP BY ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		stev_co_cd, ");
			queryBuf.append("		create_dttm ) detsum ON ");
			queryBuf.append("	(co.vv_cd = detsum.vv_cd ");
			queryBuf.append("	AND co.stev_co_cd = detsum.stev_co_cd ");
			queryBuf.append("	AND co.create_dttm = detsum.create_dttm) ");
			queryBuf.append("INNER JOIN GBCC_VSL_PROD bal ON ");
			queryBuf.append("	(co.vv_cd = bal.vv_cd ");
			queryBuf.append("	AND co.stev_co_cd = bal.stev_co_cd ");
			queryBuf.append("	AND co.SHIFT_CD = bal.SHIFT_CD ");
			queryBuf.append("	AND co.SHIFT_DTTM = bal.SHIFT_DTTM ) ");
			queryBuf.append("LEFT JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		opr.vv_cd, ");
			queryBuf.append("		opr.stev_co_cd, ");
			queryBuf.append("		sum(det.disc_completed_ton) total_completed_disc, ");
			queryBuf.append("		sum(det.load_completed_ton) total_completed_load ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBCC.gbcc_cargo_opr opr ");
			queryBuf.append("	INNER JOIN gbcc_cargo_opr_det det ON ");
			queryBuf.append("		(opr.vv_cd = det.vv_cd ");
			queryBuf.append("		AND opr.stev_co_cd = det.stev_co_cd ");
			queryBuf.append("		AND opr.create_dttm = det.create_dttm) ");
			queryBuf.append("	GROUP BY ");
			queryBuf.append("		opr.vv_cd, ");
			queryBuf.append("		opr.stev_co_cd ) opr ON ");
			queryBuf.append("	(co.vv_cd = opr.vv_cd ");
			queryBuf.append("	AND co.stev_co_cd = opr.stev_co_cd) ");
			queryBuf.append("WHERE ");
			queryBuf.append("	1 = 1 ");

			if (!"".equalsIgnoreCase(vvCd.trim())) {
				queryBuf.append(" and co.vv_cd = :pVvCd ");
			}
			if (!"".equalsIgnoreCase(berthNo.trim())) {
				queryBuf.append(" and br.berth_nbr = :pBerthNo");
			}
			if (!"".equalsIgnoreCase(shiftCd.trim())) {
				queryBuf.append(" and co.shift_cd = :pShiftCd");
			}
			if (shiftDttm != null)
				queryBuf.append(" and to_char(co.shift_dttm,'dd/MM/yyyy') = :pShiftDttm");

			if (!"JP".equalsIgnoreCase(CustCode.trim()) && (!"".equalsIgnoreCase(CustCode.trim())))
				queryBuf.append(" and sc.co_cd = :pCustCd ");

			if (!"".equalsIgnoreCase(sortBy.trim())) {
				queryBuf.append(" ORDER BY ");
				queryBuf.append(sortBy.trim());
			}

			log.info("SQL::" + queryBuf.toString());

			if (!"".equalsIgnoreCase(vvCd.trim())) {
				paramMap.put("pVvCd", vvCd.trim());
			}
			if (!"".equalsIgnoreCase(berthNo.trim())) {
				paramMap.put("pBerthNo", berthNo.trim());
			}
			if (!"".equalsIgnoreCase(shiftCd.trim())) {
				paramMap.put("pShiftCd", shiftCd.trim());
			}
			if (shiftDttm != null)
				paramMap.put("pShiftDttm", CommonUtil.formatDateToStr(shiftDttm, "dd/MM/yyyy"));

			if (!"JP".equalsIgnoreCase(CustCode.trim()) && (!"".equalsIgnoreCase(CustCode.trim())))
				paramMap.put("pCustCd", CustCode.trim());

			log.info(": getCargoOpr  SQL " + queryBuf.toString() + " paramMap " + paramMap.toString());

			sql = queryBuf.toString();

			if (!needAllData) {

				if (criteria.isPaginated()) {
					sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());
				} else {
					sql = queryBuf.toString();
				}

			}

			List<GbccCargoOprVO> lst = namedParameterJdbcTemplate.query(sql, paramMap,
					new BeanPropertyRowMapper<GbccCargoOprVO>(GbccCargoOprVO.class));
			log.info("getCargoOpr Result: " + lst.size());

			if (lst.isEmpty())
				return returnList;
			GbccCargoOprVO oVO = null;
			GbccCargoOprId id = null;
			for (GbccCargoOprVO gbccCargoOprVO : lst) {
				id = new GbccCargoOprId();
				oVO = new GbccCargoOprVO();
				oVO = gbccCargoOprVO;
				id.setCreateDttm(oVO.getCreateDttm());
				id.setStevCoCd(oVO.getStevCoCd());
				id.setVvCd(oVO.getVvCd());
				oVO.setId(id);
				oVO.setStevedoreCompanyName(oVO.getStev_co_nm());
				oVO.setAgentName(oVO.getCo_nm());

				oVO.setTotDiscCompleted((Integer) oVO.getTotal_disc());
				oVO.setTotLoadCompleted((Integer) oVO.getTotal_load());

				oVO.setTotDiscOpenBal((Integer) oVO.getTot_disc_openbal());
				oVO.setTotLoadOpenBal((Integer) oVO.getTot_load_openbal());

				oVO.setTotDiscTotalCompleted((Integer) oVO.getTotal_completed_disc());
				oVO.setTotLoadTotalCompleted((Integer) oVO.getTotal_completed_load());

				Integer balDisc = new Integer(
						oVO.getTotDiscOpenBal().intValue() - oVO.getTotDiscTotalCompleted().intValue());
				Integer balLoad = new Integer(
						oVO.getTotLoadOpenBal().intValue() - oVO.getTotLoadTotalCompleted().intValue());
				oVO.setTotDiscBal(balDisc);
				oVO.setTotLoadBal(balLoad);

				returnList.add(oVO);
			}

			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate
						.queryForObject("SELECT COUNT(*) FROM (" + queryBuf.toString() + ")", paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());

				oVO = new GbccCargoOprVO();
				oVO.setTotal(tableData.getTotal());
				returnList.add(oVO);
			}

		} catch (Exception e) {
			log.info("Exception getCargoOpr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoOpr DAO");
		}
		return returnList;
	}

	@Override
	public GbccRulePara getGbccRuleParaById(String paraCd) throws BusinessException {
		GbccRulePara oVO = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer queryBuf = new StringBuffer();
		try {
			log.info("START: getGbccRuleParaById  DAO paraCd: " + paraCd);
			queryBuf.append(" SELECT pr.* FROM GBCC.Gbcc_Rule_Para pr WHERE rule_para_cd = :pParaCd");
			paramMap.put("pParaCd", paraCd.trim());
			log.info(": getGbccRuleParaById  SQL " + queryBuf.toString() + " paramMap " + paramMap.toString());
			List<GbccRulePara> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<GbccRulePara>(GbccRulePara.class));
			log.info("getGbccRuleParaById Result: " + lst.toString());
			for (GbccRulePara gbccRulePara : lst) {
				oVO = new GbccRulePara();
				oVO = gbccRulePara;
			}
		} catch (Exception e) {
			log.info("Exception getGbccRuleParaById : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getGbccRuleParaById DAO");
		}
		return oVO;
	}

	@Override
	public GbccCargoOprVO getCargoOprById(String vvCd, String stevCd, Date crDttm) throws BusinessException {
		return getCargoOprById(vvCd, stevCd, crDttm, "", null, "");
	}

	@Override
	public GbccCargoOprVO getCargoOprByVvCdCustCd(String vvCd, String custCd) throws BusinessException {
		return getCargoOprById(vvCd, "", null, "", null, "");
	}

	@Override
	public GbccVslProd getVslProdById(String vvCd, String stevCd, Date crDttm) throws BusinessException {
		return getVslProdById(vvCd, stevCd, crDttm, "", null);
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	public GbccVslProd getVslProdById(String vvCd, String stevCd, Date crDttm, String shiftCd, Date shiftDttm)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		GbccVslProd copr = null;
		try {
			log.info("START: getVslProdById  DAO vvCd: " + vvCd + " stevCd: " + stevCd + " crDttm: " + crDttm
					+ " shiftCd: " + shiftCd + " shiftDttm: " + shiftDttm);
			StringBuffer queryBuf = new StringBuffer();
			queryBuf.append("SELECT ");
			queryBuf.append("	co.*, ");
			queryBuf.append("	mtc1.MISC_TYPE_NM ");
			queryBuf.append("FROM ");
			queryBuf.append("	GBCC.Gbcc_Vsl_Prod co ");
			queryBuf.append("LEFT JOIN Misc_Type_Code mtc1 ON ");
			queryBuf.append("	(co.UNDER_PERF_REASON_CD = mtc1.misc_type_cd ");
			queryBuf.append("	AND mtc1.cat_cd = :pCatCd1) ");
			queryBuf.append("WHERE ");
			queryBuf.append("	co.vv_cd = :pVvCd ");
			queryBuf.append("	AND co.stev_co_cd = :pStevCoCd");

			if (shiftDttm != null)
				queryBuf.append(" AND TO_CHAR(co.shift_dttm,'dd/MM/yyyy') = :pShiftDttm ");

			if (crDttm != null)
				queryBuf.append(" AND co.create_dttm = :pCreateDttm ");

			if (!shiftCd.equalsIgnoreCase(""))
				queryBuf.append(" AND co.shift_cd = :pShiftCd ");

			log.info("SQL::" + queryBuf.toString());

			paramMap.put("pCatCd1", ConstantUtil.MISCTYPECD_SHIFT_STEV);

			paramMap.put("pVvCd", vvCd.trim());
			paramMap.put("pStevCoCd", stevCd.trim());

			if (shiftDttm != null)
				paramMap.put("pShiftDttm", CommonUtil.formatDateToStr(shiftDttm, "dd/MM/yyyy"));

			if (crDttm != null) {
				Timestamp crTs = new Timestamp(crDttm.getTime());
				paramMap.put("pCreateDttm", crTs);
			}
			if (!shiftCd.equalsIgnoreCase(""))
				paramMap.put("pShiftCd", shiftCd.trim());

			// query.addEntity("co", GbccVslProd.class);
			// query.addScalar("MISC_TYPE_NM", Hibernate.STRING);

			log.info(": getVslProdById  SQL " + queryBuf.toString() + " paramMap " + paramMap.toString());

			List<GbccVslProd> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<GbccVslProd>(GbccVslProd.class));
			log.info("getVslProdById Result: " + lst.toString());
			if (lst != null) {
				GbccVslProdId id = null;

				for (GbccVslProd gbccVslProd : lst) {
					id = new GbccVslProdId();
					copr = new GbccVslProd();
					copr = gbccVslProd;
					id.setVvCd(copr.getVvCd());
					id.setStevCoCd(copr.getStevCoCd());
					id.setCreateDttm(copr.getCreateDttm());
					copr.setId(id);
					copr.setUnderPerfReasonName((String) copr.getMisc_type_nm());
				}

			}
		} catch (Exception e) {
			log.info("Exception getVslProdById : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVslProdById DAO");
		}
		return copr;
	}

	@Override
	public boolean saveCargoOpr(GbccCargoOprVO transientObject) throws BusinessException {
		boolean result;
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: saveCargoOpr  DAO GbccCargoOprVO: " + transientObject.toString());
			boolean isNew = false;
			Date dttm = new Date();

			transientObject.setLastModifyDttm(dttm);
			if (transientObject.getCreateDttm() == null) {
				transientObject.setCreateDttm(dttm);
				isNew = true;

				sb.append("INSERT ");
				sb.append("	INTO ");
				sb.append("	GBCC.GBCC_CARGO_OPR (VV_CD, ");
				sb.append("	STEV_CO_CD, ");
				sb.append("	CREATE_DTTM, ");
				sb.append("	SHIFT_CD, ");
				sb.append("	SHIFT_DTTM, ");
				sb.append("	CHECKER_NM, ");
				sb.append("	CHECKER_OFFICE_NBR, ");
				sb.append("	CHECKER_HP_NBR, ");
				sb.append("	GANGS_NBR, ");
				sb.append("	DISC_START_DTTM, ");
				sb.append("	DISC_END_DTTM, ");
				sb.append("	LOAD_START_DTTM, ");
				sb.append("	LOAD_END_DTTM, ");
				sb.append("	LAST_MODIFY_USER_ID, ");
				sb.append("	LAST_MODIFY_DTTM, ");
				sb.append("	UNDER_PERF_REASON_CD, ");
				sb.append("	UNDER_PERF_REMARKS, ");
				sb.append("	SHIFT_END_DTTM, ");
				sb.append("	SHIFT_START_DTTM, ");
				sb.append("	DISC_DELAY_REASON_CD1, ");
				sb.append("	DISC_DELAY_REASON_CD2, ");
				sb.append("	DISC_DELAY_REASON_REMARKS, ");
				sb.append("	LOAD_DELAY_REASON_CD1, ");
				sb.append("	LOAD_DELAY_REASON_CD2, ");
				sb.append("	LOAD_DELAY_REASON_REMARKS) ");
				sb.append("VALUES(:vvCd, ");
				sb.append(":stevCoCd, ");
				sb.append(":createDttm, ");
				sb.append(":shiftCd, ");
				sb.append(":shiftDttm, ");
				sb.append(":checkerNm, ");
				sb.append(":checkerOfficeNbr, ");
				sb.append(":checkerHpNbr, ");
				sb.append(":gangsNbr, ");
				sb.append(":discStartDttm, ");
				sb.append(":discEndDttm, ");
				sb.append(":loadStartDttm, ");
				sb.append(":loadEndDttm, ");
				sb.append(":lastModifyUserId, ");
				sb.append(":lastModifyDttm, ");
				sb.append(":underPerfReasonCd, ");
				sb.append(":underPerfRemarks, ");
				sb.append(":shiftEndDttm, ");
				sb.append(":shiftStartDttm, ");
				sb.append(":discDelayReasonCd1, ");
				sb.append(":discDelayReasonCd2, ");
				sb.append(":discDelayReasonRemarks, ");
				sb.append(":loadDelayReasonCd1, ");
				sb.append(":loadDelayReasonCd2, ");
				sb.append(":loadDelayReasonRemarks)");
			} else {
				sb.append("UPDATE ");
				sb.append("	GBCC.GBCC_CARGO_OPR ");
				sb.append("SET ");
				sb.append("	SHIFT_CD = :shiftCd, ");
				sb.append("	SHIFT_DTTM = :shiftDttm, ");
				sb.append("	CHECKER_NM = :checkerNm, ");
				sb.append("	CHECKER_OFFICE_NBR = :checkerOfficeNbr, ");
				sb.append("	CHECKER_HP_NBR = :checkerHpNbr, ");
				sb.append("	GANGS_NBR = :gangsNbr, ");
				sb.append("	DISC_START_DTTM = :discStartDttm, ");
				sb.append("	DISC_END_DTTM = :discEndDttm, ");
				sb.append("	LOAD_START_DTTM = :loadStartDttm, ");
				sb.append("	LOAD_END_DTTM = :loadEndDttm, ");
				sb.append("	LAST_MODIFY_USER_ID = :lastModifyUserId, ");
				sb.append("	LAST_MODIFY_DTTM = :lastModifyDttm, ");
				sb.append("	UNDER_PERF_REASON_CD = :underPerfReasonCd, ");
				sb.append("	UNDER_PERF_REMARKS = :underPerfRemarks, ");
				sb.append("	SHIFT_END_DTTM = :shiftEndDttm, ");
				sb.append("	SHIFT_START_DTTM = :shiftStartDttm, ");
				sb.append("	DISC_DELAY_REASON_CD1 = :discDelayReasonCd1, ");
				sb.append("	DISC_DELAY_REASON_CD2 = :discDelayReasonCd2, ");
				sb.append("	DISC_DELAY_REASON_REMARKS = :discDelayReasonRemarks, ");
				sb.append("	LOAD_DELAY_REASON_CD1 = :loadDelayReasonCd1, ");
				sb.append("	LOAD_DELAY_REASON_CD2 = :loadDelayReasonCd2, ");
				sb.append("	LOAD_DELAY_REASON_REMARKS = :loadDelayReasonRemarks ");
				sb.append("WHERE ");
				sb.append("	VV_CD = :vvCd ");
				sb.append("	AND STEV_CO_CD = :stevCoCd ");
				sb.append("	AND CREATE_DTTM = :createDttm");
			}

			String modifyUserId = transientObject.getLastModifyUserId();
			String vvCd = transientObject.getId().getVvCd();
			transientObject.setVvCd(transientObject.getId().getVvCd());
			transientObject.setAuditUserId(modifyUserId);
			transientObject.setFnType(ConstantUtil.AUDIT_FNTYPE_CARGOOPS);
			transientObject.setFnsubType(ConstantUtil.AUDIT_FNSUBTYPE_CARGOOPR_UPDATE);
			transientObject.setTableNm(ConstantUtil.TABLE_GBCCCARGOOPR);
			transientObject.setKeyType1(ConstantUtil.AUDIT_KEYID_VVCD);
			transientObject.setKeyVal1(vvCd);
			transientObject.setKeyType1(ConstantUtil.AUDIT_KEYID_STEVCOCD);
			transientObject.setKeyVal1(transientObject.getStevCoCd());

			String fieldvalue = ConstantUtil.AUDIT_KEYID_VVCD + "=" + vvCd;
			fieldvalue = fieldvalue + ConstantUtil.SEPARATOR + ConstantUtil.AUDIT_KEYID_STEVCOCD + "="
					+ transientObject.getStevCoCd();

			transientObject.setFieldNewValue(fieldvalue);
			transientObject.setFieldOldValue(fieldvalue);

			log.info("saveCargoOpr SQL  " + sb.toString() + " GbccCargoOprVO" + transientObject.toString());

			int count = namedParameterJdbcTemplate.update(sb.toString(),
					new BeanPropertySqlParameterSource(transientObject));
			if (count == 0) {
				log.info("not amended");
			}

			List<GbccCargoOprDet> detlst = transientObject.getCargoOprDetVO();

			if (detlst != null) {
				int hatchSize = detlst.size();

				for (int i = 0; i < hatchSize; i++) {
					GbccCargoOprDet detVO = (GbccCargoOprDet) detlst.get(i);
					if (isNew) {
						detVO.setCreateDttm(dttm);
						sb = new StringBuffer();
						sb.append("INSERT ");
						sb.append("	INTO ");
						sb.append("	GBCC.GBCC_CARGO_OPR_DET (VV_CD, ");
						sb.append("	STEV_CO_CD, ");
						sb.append("	CREATE_DTTM, ");
						sb.append("	HATCH_NBR, ");
						sb.append("	DISC_COMPLETED_TON, ");
						sb.append("	LOAD_COMPLETED_TON, ");
						sb.append("	LAST_MODIFY_USER_ID, ");
						sb.append("	LAST_MODIFY_DTTM, ");
						sb.append("	DISC_REMARKS, ");
						sb.append("	LOAD_REMARKS, ");
						sb.append("	DISC_WEATHER_CD, ");
						sb.append("	DISC_ACTIVITY_CD, ");
						sb.append("	LOAD_WEATHER_CD, ");
						sb.append("	LOAD_ACTIVITY_CD) ");
						sb.append("VALUES(:vvCd, ");
						sb.append(":stevCoCd, ");
						sb.append(":createDttm, ");
						sb.append(":hatchNbr, ");
						sb.append(":discCompletedTon, ");
						sb.append(":loadCompletedTon, ");
						sb.append(":lastModifyUserId, ");
						sb.append(":lastModifyDttm, ");
						sb.append(":discRemarks, ");
						sb.append(":loadRemarks, ");
						sb.append(":discWeatherCd, ");
						sb.append(":discActivityCd, ");
						sb.append(":loadWeatherCd, ");
						sb.append(":loadActivityCd)");
					} else {
						sb = new StringBuffer();
						sb.append("UPDATE ");
						sb.append("	GBCC.GBCC_CARGO_OPR_DET ");
						sb.append("SET ");
						sb.append("	DISC_COMPLETED_TON = :discCompletedTon, ");
						sb.append("	LOAD_COMPLETED_TON = :loadCompletedTon, ");
						sb.append("	LAST_MODIFY_USER_ID = :lastModifyUserId, ");
						sb.append("	LAST_MODIFY_DTTM = SYSDATE, ");
						sb.append("	DISC_REMARKS = :discRemarks, ");
						sb.append("	LOAD_REMARKS = :loadRemarks, ");
						sb.append("	DISC_WEATHER_CD = :discWeatherCd, ");
						sb.append("	DISC_ACTIVITY_CD = :discActivityCd, ");
						sb.append("	LOAD_WEATHER_CD = :loadWeatherCd, ");
						sb.append("	LOAD_ACTIVITY_CD = :loadActivityCd ");
						sb.append("WHERE ");
						sb.append("	VV_CD = :vvCd ");
						sb.append("	AND STEV_CO_CD = :stevCoCd ");
						sb.append("	AND CREATE_DTTM = :createDttm ");
						sb.append("	AND HATCH_NBR = :hatchNbr");
					}

					// saveCargoOprDet(detVO);
					// Date dttm = new Date();y
					detVO.setLastModifyUserId(modifyUserId);
					detVO.setStevCoCd(transientObject.getStevCoCd());
					detVO.setAuditUserId(modifyUserId);
					detVO.setFnType(ConstantUtil.AUDIT_FNTYPE_CARGOOPS);
					detVO.setFnsubType(ConstantUtil.AUDIT_FNSUBTYPE_CARGODISCLOADINFO_UPDATE);
					detVO.setTableNm(ConstantUtil.TABLE_GBCCCARGOOPRDET);
					detVO.setKeyType1(ConstantUtil.AUDIT_KEYID_VVCD);
					detVO.setKeyVal1(vvCd);

					detVO.setFieldNewValue(fieldvalue);
					detVO.setFieldOldValue(fieldvalue);

					detVO.setLastModifyDttm(dttm);
					log.info("saveCargoOpr SQL  " + sb.toString() + " GbccCargoOprDet" + detVO.toString());

					int detcount = namedParameterJdbcTemplate.update(sb.toString(),
							new BeanPropertySqlParameterSource(detVO));
					if (detcount == 0) {
						log.info("not amended");
					}
				}
			}

			result = true;
log.info(" saveCargoOpr Result: "+result);
		} catch (Exception e) {
			result = false;
			log.info("Exception saveCargoOpr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: saveCargoOpr DAO");
		}
		return result;
	}

	@Override
	public CargoOprSummInfoValueObject getCargoOprSumm(String vvCd) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer queryBuf = new StringBuffer();
		CargoOprSummInfoValueObject oVO = null;
		try {
			log.info("START: getCargoOprSumm  DAO vvCd: " + vvCd);

			queryBuf.append("SELECT ");
			queryBuf.append("	max_shift_dttm, ");
			queryBuf.append("	max_col, ");
			queryBuf.append("	max_cod , ");
			queryBuf.append("	nvl(total_disc_completed, 0) total_disc_completed , ");
			queryBuf.append("	nvl(total_load_completed, 0) total_load_completed , ");
			queryBuf.append("	nvl(total_disc_openbal, 0) total_disc_openbal , ");
			queryBuf.append("	nvl(total_load_openbal, 0) total_load_openbal , ");
			queryBuf.append("	nvl(total_disc_completed, 0) + nvl(total_load_completed, 0) total_completed , ");
			queryBuf.append("	nvl(total_disc_openbal, 0) + nvl(total_load_openbal, 0) total_openbal , ");
			queryBuf.append("	workstartdttm ");
			queryBuf.append("FROM ");
			queryBuf.append("	( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		max(shift_end_dttm) max_shift_dttm , ");
			queryBuf.append("		max(load_end_dttm) max_col , ");
			queryBuf.append("		max(disc_end_dttm) max_cod ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBCC.gbcc_cargo_opr opr ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		vv_cd = :pVvCd1 ), ");
			queryBuf.append("	( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		sum(disc_completed_ton) total_disc_completed, ");
			queryBuf.append("		sum(load_completed_ton) total_load_completed ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBCC.gbcc_cargo_opr_det ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		vv_cd = :pVvCd2) a , ");
			queryBuf.append("	( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		min(wrk_start_dttm) workstartdttm, ");
			queryBuf.append("		sum(disc_open_bal_ton) total_disc_openbal, ");
			queryBuf.append("		sum(load_open_bal_ton) total_load_openbal ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBCC.gbcc_cargo_open_bal_det det ");
			queryBuf.append("	INNER JOIN gbcc_cargo_open_bal bal ON ");
			queryBuf.append("		(bal.vv_cd = det.vv_cd ");
			queryBuf.append("		AND det.stev_co_cd = bal.stev_co_cd) ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		det.vv_cd = :pVvCd3) b");

			log.info("SQL::" + queryBuf.toString());

			paramMap.put("pVvCd1", vvCd.trim());
			paramMap.put("pVvCd2", vvCd.trim());
			paramMap.put("pVvCd3", vvCd.trim());

			log.info(": getCargoOprSumm  SQL " + queryBuf.toString() + " paramMap " + paramMap.toString());

			List<CargoOprSummInfoValueObject> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<CargoOprSummInfoValueObject>(CargoOprSummInfoValueObject.class));
			log.info("getCargoOprSumm Result" + lst.toString());

			for (CargoOprSummInfoValueObject cargoVo : lst) {
				oVO = new CargoOprSummInfoValueObject();
				oVO = cargoVo;

				Date maxShiftDttm = (Date) oVO.getMax_shift_dttm();
				Date maxCol = (Date) oVO.getMax_col();
				Date maxCod = (Date) oVO.getMax_cod();
				Integer totalCompleted = (Integer) oVO.getTotal_completed();
				Integer totalOpenBal = (Integer) oVO.getTotal_openbal();

				oVO.setVvCd(vvCd);
				oVO.setCodDttm(maxCod);
				oVO.setColDttm(maxCol);
				oVO.setTotalComp(totalCompleted);
				oVO.setTotalOpen(totalOpenBal);
				oVO.setTotalCompDisc((Integer) oVO.getTotal_disc_completed());
				oVO.setTotalCompLoad((Integer) oVO.getTotal_load_completed());
				oVO.setTotalOpenDisc((Integer) oVO.getTotal_disc_openbal());
				oVO.setTotalOpenLoad((Integer) oVO.getTotal_load_openbal());

				oVO.setCommencementDttm((Date) oVO.getWorkstartdttm());
				Date completedDttm = null;
				completedDttm = maxShiftDttm;
				oVO.setCompletedDttm(completedDttm);
				Berthing firstBerthing = getFirstBerthing(vvCd);
				oVO.setFirstBerthing(firstBerthing);
				oVO.setLastBerthing(getLastBerthing(vvCd));
			}

			// if (totalCompleted.longValue() >= totalOpenBal.longValue()) {
			// if (maxCol == null) {
			// completedDttm = maxCod;
			// } else {
			// if (maxCol.before(maxCod))
			// completedDttm = maxCod;
			// else
			// completedDttm = maxCol;
			// }
			// if (completedDttm == null)
			// completedDttm = maxShiftDttm;
			// }
			// else
			// completedDttm = maxShiftDttm;

		} catch (Exception e) {
			log.info("Exception getCargoOprSumm : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoOprSumm DAO");
		}
		return oVO;
	}

	@Override
	public CargoOprSummInfoValueObject getCargoOprPrevCompleted(String vvCd, String stevCd, String shiftCd,
			Date shiftDttm) throws BusinessException {
		CargoOprSummInfoValueObject oVO = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getCargoOprPrevCompleted  DAO vvCd: " + vvCd + " stevCd: " + stevCd + " shiftCd: "
					+ shiftCd + " shiftDttm: " + shiftDttm);
			String shiftDate = CommonUtil.formatDateToStr(shiftDttm, ConstantUtil.DATEFORMAT_INPUT_SHORT);

			StringBuffer queryBuf = new StringBuffer();
			queryBuf.append("SELECT ");
			queryBuf.append("	nvl(sum(disc_completed_ton), 0) total_disc_completed, ");
			queryBuf.append("	nvl(sum(load_completed_ton), 0) total_load_completed ");
			queryBuf.append("FROM ");
			queryBuf.append("	GBCC.GBCC_CARGO_OPR_DET det ");
			queryBuf.append("LEFT JOIN GBCC_CARGO_OPR opr ON ");
			queryBuf.append("	det.VV_CD = opr.VV_CD ");
			queryBuf.append("	AND det.STEV_CO_CD = opr.STEV_CO_CD ");
			queryBuf.append("	AND det.CREATE_DTTM = opr.CREATE_DTTM ");
			queryBuf.append("WHERE ");
			queryBuf.append("	opr.VV_CD = :pVvCd ");
			queryBuf.append("	AND opr.STEV_CO_CD = :pStevCd ");
			queryBuf.append("	AND ((UPPER(opr.SHIFT_CD) < UPPER(:pShiftCd) ");
			queryBuf.append("	AND TO_CHAR(opr.SHIFT_DTTM, 'ddMMyyyy') = :pShiftDttm1) ");
			queryBuf.append("	OR (opr.SHIFT_DTTM < TO_DATE(:pShiftDttm2, 'ddMMyyyy')))");

			log.info("SQL::" + queryBuf.toString());

			paramMap.put("pVvCd", vvCd.trim());
			paramMap.put("pStevCd", stevCd.trim());
			paramMap.put("pShiftCd", shiftCd.trim());
			paramMap.put("pShiftDttm1", shiftDate);
			paramMap.put("pShiftDttm2", shiftDate);

			log.info(": getCargoOprPrevCompleted  SQL " + queryBuf.toString() + " paramMap " + paramMap.toString());

			List<CargoOprSummInfoValueObject> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<CargoOprSummInfoValueObject>(CargoOprSummInfoValueObject.class));
			log.info("getCargoOprPrevCompleted Result: " + lst.toString());

			for (CargoOprSummInfoValueObject cargoOprVO : lst) {
				oVO = new CargoOprSummInfoValueObject();
				oVO = cargoOprVO;
				oVO.setVvCd(vvCd);
				oVO.setTotalCompDisc((Integer) oVO.getTotal_disc_completed());
				oVO.setTotalCompLoad((Integer) oVO.getTotal_load_completed());

			}

		} catch (Exception e) {
			log.info("Exception getCargoOprPrevCompleted : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoOprPrevCompleted DAO");
		}
		return oVO;
	}

	@Override
	public boolean saveVslProd(GbccVslProd transientObject) throws BusinessException {
		boolean result = false;
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: saveVslProd  DAO GbccVslProd: " + transientObject.toString());
			String vv_Cd = "";
			StringBuffer queryBuf = new StringBuffer();

			queryBuf.append("SELECT co.VV_CD from GBCC.GBCC_VSL_PROD co ");
			queryBuf.append(" WHERE co.VV_CD = :pVvCd ");

			paramMap.put("pVvCd", transientObject.getVvCd());

			log.info("SQL::" + queryBuf.toString() + " paramMap " + paramMap.toString());
			// query.addEntity("co", GbccCargoOprPlanDet.class);

			List<String> lstDet = namedParameterJdbcTemplate.queryForList(queryBuf.toString(), paramMap, String.class);

			for (int i = 0; i < lstDet.size(); i++) {
				vv_Cd = (String) lstDet.get(i);
			}

			if (vv_Cd.equalsIgnoreCase("")) {

				sb.append("INSERT ");
				sb.append("	INTO ");
				sb.append("	GBCC.GBCC_VSL_PROD (CREATE_DTTM, ");
				sb.append("	VV_CD, ");
				sb.append("	STEV_CO_CD, ");
				sb.append("	SHIFT_CD, ");
				sb.append("	SHIFT_DTTM, ");
				sb.append("	SHIFT_END_DTTM, ");
				sb.append("	VSL_TYPE, ");
				sb.append("	OPEN_BAL, ");
				sb.append("	OPEN_BAL_DISC, ");
				sb.append("	OPEN_BAL_LOAD, ");
				sb.append("	WORK_START_DTTM, ");
				sb.append("	TOT_WRK_HR, ");
				sb.append("	TOT_PORT_HR, ");
				sb.append("	TOT_DISC_HR, ");
				sb.append("	TOT_LOAD_HR, ");
				sb.append("	TOT_COMPLETED, ");
				sb.append("	TOT_DISC_COMPLETED, ");
				sb.append("	TOT_LOAD_COMPLETED, ");
				sb.append("	BAL_TOTAL, ");
				sb.append("	BAL_DISC, ");
				sb.append("	BAL_LOAD, ");
				sb.append("	PROD_RATE_NETT, ");
				sb.append("	PROD_RATE_GROSS, ");
				sb.append("	PROD_RATE_DISC, ");
				sb.append("	PROD_RATE_LOAD, ");
				sb.append("	PROJECTED_ETU, ");
				sb.append("	ETU_DTTM, ");
				sb.append("	ETU_VARIATION_HR, ");
				sb.append("	COL_DTTM, ");
				sb.append("	COD_DTTM, ");
				sb.append("	AFFECT_PLANNED_ETU, ");
				sb.append("	UNDER_PERF_REASON_CD, ");
				sb.append("	UNDER_PERF_REMARKS, ");
				sb.append("	LAST_MODIFY_USER_ID, ");
				sb.append("	LAST_MODIFY_DTTM, ");
				sb.append("	SHIFT_START_DTTM) ");
				sb.append("VALUES(:createDttm, ");
				sb.append(":vvCd, ");
				sb.append(":stevCoCd, ");
				sb.append(":shiftCd, ");
				sb.append(":shiftDttm, ");
				sb.append(":shiftEndDttm, ");
				sb.append(":vslType, ");
				sb.append(":openBal, ");
				sb.append(":openBalDisc, ");
				sb.append(":openBalLoad, ");
				sb.append(":workStartDttm, ");
				sb.append(":totWrkHr, ");
				sb.append(":totPortHr, ");
				sb.append(":totDiscHr, ");
				sb.append(":totLoadHr, ");
				sb.append(":totCompleted, ");
				sb.append(":totDiscCompleted, ");
				sb.append(":totLoadCompleted, ");
				sb.append(":balTotal, ");
				sb.append(":balDisc, ");
				sb.append(":balLoad, ");
				sb.append(":prodRateNett, ");
				sb.append(":prodRateGross, ");
				sb.append(":prodRateDisc, ");
				sb.append(":prodRateLoad, ");
				sb.append(":projectedEtu, ");
				sb.append(":etuDttm, ");
				sb.append(":etuVariationHr, ");
				sb.append(":colDttm, ");
				sb.append(":codDttm, ");
				sb.append(":affectPlannedEtu, ");
				sb.append(":underPerfReasonCd, ");
				sb.append(":underPerfRemarks, ");
				sb.append(":lastModifyUserId, ");
				sb.append(":lastModifyDttm, ");
				sb.append(":shiftStartDttm)");
			} else {

				sb = new StringBuffer();

				sb.append("UPDATE ");
				sb.append("	GBCC.GBCC_VSL_PROD ");
				sb.append("SET ");
				sb.append("	SHIFT_CD = :shiftCd, ");
				sb.append("	SHIFT_DTTM = :shiftDttm, ");
				sb.append("	SHIFT_END_DTTM = :shiftEndDttm, ");
				sb.append("	VSL_TYPE = :vslType, ");
				sb.append("	OPEN_BAL = :openBal, ");
				sb.append("	OPEN_BAL_DISC = :openBalDisc, ");
				sb.append("	OPEN_BAL_LOAD = :openBalLoad, ");
				sb.append("	WORK_START_DTTM = :workStartDttm, ");
				sb.append("	TOT_WRK_HR = :totWrkHr, ");
				sb.append("	TOT_PORT_HR = :totPortHr, ");
				sb.append("	TOT_DISC_HR = :totDiscHr, ");
				sb.append("	TOT_LOAD_HR = :totLoadHr, ");
				sb.append("	TOT_COMPLETED = :totCompleted, ");
				sb.append("	TOT_DISC_COMPLETED = :totDiscCompleted, ");
				sb.append("	TOT_LOAD_COMPLETED = :totLoadCompleted, ");
				sb.append("	BAL_TOTAL = :balTotal, ");
				sb.append("	BAL_DISC = :balDisc, ");
				sb.append("	BAL_LOAD = :balLoad, ");
				sb.append("	PROD_RATE_NETT = :prodRateNett, ");
				sb.append("	PROD_RATE_GROSS = :prodRateGross, ");
				sb.append("	PROD_RATE_DISC = :prodRateDisc, ");
				sb.append("	PROD_RATE_LOAD = :prodRateLoad, ");
				sb.append("	PROJECTED_ETU = :projectedEtu, ");
				sb.append("	ETU_DTTM = :etuDttm, ");
				sb.append("	ETU_VARIATION_HR = :etuVariationHr, ");
				sb.append("	COL_DTTM = :colDttm, ");
				sb.append("	COD_DTTM = :codDttm, ");
				sb.append("	AFFECT_PLANNED_ETU = :affectPlannedEtu, ");
				sb.append("	UNDER_PERF_REASON_CD = :underPerfReasonCd, ");
				sb.append("	UNDER_PERF_REMARKS = :underPerfRemarks, ");
				sb.append("	LAST_MODIFY_USER_ID = :lastModifyUserId, ");
				sb.append("	LAST_MODIFY_DTTM = :lastModifyDttm, ");
				sb.append("	SHIFT_START_DTTM = :shiftStartDttm ");
				sb.append("WHERE ");
				sb.append("	CREATE_DTTM = :createDttm ");
				sb.append("	AND VV_CD = :vvCd ");
				sb.append("	AND STEV_CO_CD = :stevCoCd");
			}

			Date dttm = new Date();
			transientObject.setLastModifyDttm(dttm);
			// this.getHibernateTemplate().saveOrUpdate(transientObject);

			transientObject.setAuditUserId(transientObject.getLastModifyUserId());
			transientObject.setFnType(ConstantUtil.AUDIT_FNTYPE_CARGOOPS);
			transientObject.setFnsubType(ConstantUtil.AUDIT_FNSUBTYPE_CARGOVSLPROD_ADD);
			transientObject.setTableNm(ConstantUtil.TABLE_GBCCVSLPROD);
			transientObject.setKeyType1(ConstantUtil.AUDIT_KEYID_VVCD);
			transientObject.setKeyVal1(transientObject.getVvCd());

			String fieldvalue = ConstantUtil.AUDIT_KEYID_VVCD + "=" + transientObject.getVvCd();
			transientObject.setFieldNewValue(fieldvalue);
			transientObject.setFieldOldValue(fieldvalue);

			log.info("saveVslProd SQL  " + sb.toString() + " GbccVslProd" + transientObject.toString());

			int count = namedParameterJdbcTemplate.update(sb.toString(),
					new BeanPropertySqlParameterSource(transientObject));
			if (count == 0) {
				log.info("not amended");
				result = false;
				return result;
			}

			result = true;

			log.info("saveVslProd Result: "+result);
		} catch (Exception e) {
			result = false;
			log.info("Exception saveVslProd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: saveVslProd DAO");
		}
		return result;
	}

	@Override
	public GbccVslProd getVslProdById(String vvCd, String stevCd, String shiftCd, Date shiftDttm)
			throws BusinessException {
		return getVslProdById(vvCd, stevCd, null, shiftCd, shiftDttm);
	}

	@Override
	public GbccCargoTallysheetVO getCargoTallySheetById(String custCd, String vvCd, Date crDttm, String oprType,
			Integer hatchNo, String stevCd) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		GbccCargoTallysheetVO oVO = null;
		try {
			log.info("START: getCargoTallySheetById  DAO CustCode: " + custCd + " vvCd: " + vvCd + " crDttm: " + crDttm
					+ " oprType: " + oprType + " hatchNo: " + hatchNo + " stevCd: " + stevCd);

			StringBuffer queryBuf = new StringBuffer();

			queryBuf.append("SELECT ");
			queryBuf.append(
					"	co.VV_CD vvCd, co.CREATE_DTTM, co.OPR_TYPE, co.HATCH_NBR, co.STEV_CO_CD, co.CHECKER_NM, co.CHECKER_HP, ");
			queryBuf.append("   co.WORK_COMMENCE_DTTM, co.WORK_COMPLETE_DTTM, co.FROM_DTTM, co.TO_DTTM, ");
			queryBuf.append(
					"   co.SUBMITTED_DTTM, co.LAST_MODIFY_USER_ID lastModifyUserId, co.LAST_MODIFY_DTTM lastModifyDttm, co.TALLY_CHECKER_NM, co.TALLY_CHECKER_HP, ");

			queryBuf.append(
					"	vc.VV_CD vv_Cd, vc.VSL_NM, vc.IN_VOY_NBR, vc.OUT_VOY_NBR, vc.VV_STATUS_IND, vc.SHPG_SVC_CD, vc.SHPG_ROUTE_NBR,  ");
			queryBuf.append(
					"	vc.ROUTE_NM, vc.VSL_OPR_CD, vc.BERTH_APPL_DTTM, vc.VSL_BERTH_DTTM, vc.VSL_ETD_DTTM, vc.VV_CLOSE_DTTM, ");
			queryBuf.append(
					"	vc.PORT_FR, vc.PORT_TO, vc.ARRIVAL_DRAFT, vc.DEPARTURE_DRAFT, vc.BERTH_SIDE_IND, vc.CONTACT_NM, ");
			queryBuf.append(
					"	vc.CONTACT_HOME_TEL, vc.CONTACT_OFF_TEL, vc.CONTACT_PGR, vc.CONTACT_FAX, vc.CNTR_DISC, vc.CNTR_LOAD, vc.UC_LOAD, vc.UC_DISC, vc.COB_DTTM, vc.BERTH_APPL_REM, ");
			queryBuf.append(
					"	vc.BILL_MARINE_IND, vc.BILL_STEV_IND, vc.BILL_OTHER_IND, vc.BILL_ADMIN_IND, vc.CREATE_USER_ID, vc.CREATE_ACCT_NBR, vc.LAST_MODIFY_USER_ID vsl_lastModifyUserId, vc.LAST_MODIFY_DTTM vsl_lastModifyDttm, ");
			queryBuf.append(
					"	vc.SCHEME, vc.TERMINAL, vc.VSL_LOA, vc.LOC_FR, vc.LOC_TO, vc.ALONGSIDE_DRAFT, vc.VSL_UNDER_TOW_IND, vc.CONTACT2_NM, ");
			queryBuf.append(
					"	vc.CONTACT2_HOME_TEL, vc.CONTACT2_OFF_TEL, vc.CONTACT2_PGR, vc.CONTACT2_FAX, vc.TOTAL_CARGO_ONBOARD, vc.LOAD_DISPLACEMENT, vc.DG_CARGO_IND, vc.HLIFT, ");
			queryBuf.append(
					"	vc.HLIFT_OVERWHARF, vc.HLIFT_OVERSIDE, vc.MOBILE_CR_DWT, vc.MOBILE_CR_SWL, vc.CNTR_VSL_IND, vc.GB_CLOSE_VSL_IND, vc.GB_CLOSE_BJ_IND, vc.GB_CLOSE_SHP_IND, ");
			queryBuf.append(
					"	vc.CARGO_MODE, vc.BERTH_ALLOC_REM, vc.STORAGE_SPACE_IND, vc.BILL_ACCT_NBR, vc.GB_ARRIVAL_WAIVER_CD, vc.GB_ARRIVAL_WAIVER_REASON, vc.GB_DEPARTURE_WAIVER_CD, vc.GB_DEPARTURE_WAIVER_REASON, ");
			queryBuf.append(
					"	vc.GB_BERT_BILL_IND, vc.BRIDGE_DIST_FROM_BOW, vc.VACATE_BERTH_IND, vc.MIXED_SCHEME_IND, vc.DISC_CM_CD, vc.LOAD_CM_CD, vc.DISC_BER_REM, vc.LOAD_BER_REM, ");
			queryBuf.append(
					"	vc.CRG_DET_PROC, vc.DECLARANT_CUST_CD, vc.UC_DISC_CM_CD, vc.UC_LOAD_CM_CD, vc.UC_DISC_BER_REM, vc.UC_LOAD_BER_REM, vc.EST_LONG_CR_MOVE_NBR, vc.EST_THROUGHPUT_NBR, ");
			queryBuf.append(
					"	vc.SENT_TO_PSA_IND, vc.ADVICE_DTTM, vc.ABBR_IN_VOY_NBR, vc.ABBR_OUT_VOY_NBR, vc.INCENTIVE_CLASS, vc.ISPS_LEVEL, vc.BILL_OPEN_TS_IND, vc.SMS_ALERT_REP1_IND, ");
			queryBuf.append(
					"	vc.SMS_ALERT_REP2_IND, vc.TANDEM_LIFT_IND tandemLiftInd, vc.BILL_PROD_SURCHRG_IND, vc.ALLOC_PROD_PRD, vc.REEFER_PARTY, vc.PROTRUSION_IND, vc.FLOAT_CRANE_IND, vc.FIRST_SCHEME, ");
			queryBuf.append(
					"	vc.GB_CLOSE_LCT_IND, vc.GB_CLOSE_LCT_DTTM, vc.GB_CLOSE_LCT_USER_ID, vc.STORAGE_DET_PROC, vc.GB_ARRIVAL_WAIVER_IND, vc.GB_ARRIVAL_WAIVER_AMOUNT, vc.GB_AUTO_CLOSE_BJ_IND, vc.GB_AUTO_CLOSE_SHP_IND, ");
			queryBuf.append(
					"	vc.LAST_ATU_DTTM, vc.CARGO_WT, vc.TOTAL_CARGO_WT_MOBILE_CR_DWT, vc.LCT_MIN_CHRG_IND, vc.CEMENT_VSL_IND, vc.BTR_AFT_CLOSING1_DTTM, vc.NOM_IND, vc.NOM_PROCESS_IND, ");
			queryBuf.append(
					"	vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND, vc.PRINCIPAL_CARRIER_CD, vc.USE_SHP_CRN_IND, ");

			queryBuf.append(
					"	vs.VV_CD stev_VvCd, vs.STEV_CO_CD stev_coCd, vs.STEV_CONTACT, vs.STEV_REMARKS, vs.STEV_REP, vs.LINENO, vs.LAST_MODIFY_USER_ID gbccView_lastModifyUserId, ");

			queryBuf.append(
					"	br.VV_CD berth_VvCd, br.SHIFT_IND, br.ETB_DTTM, br.ETU_DTTM, br.ATB_DTTM, br.ATU_DTTM, br.FIRST_DISC_DTTM, br.FIRST_LOAD_DTTM, br.COD_DTTM, br.COL_DTTM,  ");
			queryBuf.append(
					"	br.BERTH_NBR, br.WHARF_MARK_FR, br.WHARF_MARK_TO, br.WHARF_SIDE_IND, br.LAST_MODIFY_USER_ID berth_lastModifyUserId, br.LAST_MODIFY_DTTM berth_lastModifyDttm, br.GB_COD_DTTM, br.GB_COL_DTTM, br.GB_FIRST_ACT_DTTM, br.GB_LAST_ACT_DTTM,  ");
			queryBuf.append(
					"	br.GB_BCOD_DTTM, br.GB_BCOL_DTTM, br.HAUL_DIST, br.HAUL_DIRN, br.GANG_NBR, br.HATCH_NBR berth_hatchNbr, br.DELAY_RSN_CD, br.REMARKS berthRemarks, br.TOT_GEN_CARGO_ACT, br.GB_FIRST_CARGO_ACT_DTTM, br.DEP_DRAFT_FORTH,  ");
			queryBuf.append(
					"	br.GB_FIRST_DISC_DTTM, br.GB_FIRST_LOAD_DTTM, br.ACT_DRAFT_FORTH, br.ACT_DRAFT_AFT, br.ACT_WHARF_SIDE_IND, br.ACT_WHARF_MARK_FR, br.ACT_WHARF_MARK_TO, br.BERTH_REMARKS, br.COL_MODIFY_DTTM, br.DEP_DRAFT_AFT,  ");

			queryBuf.append("	sc.stev_co_nm, ");
			queryBuf.append("	cc.co_nm , ");
			queryBuf.append("	detsum.total_qty, ");
			queryBuf.append("	detsum.total_ton , ");
			queryBuf.append("	nvl(disc.total_pkgs, 0) tot_pkgs_disc_openbal, ");
			queryBuf.append("	nvl(disc.total_ton, 0) tot_disc_openbal , ");
			queryBuf.append("	nvl(load.total_pkgs, 0) tot_pkgs_load_openbal, ");
			queryBuf.append("	nvl(load.total_ton, 0) tot_load_openbal , ");
			queryBuf.append("	nvl(tallydisc.total_qty, 0) tot_completed_qty_disc, ");
			queryBuf.append("	nvl(tallydisc.total_ton, 0) tot_completed_disc , ");
			queryBuf.append("	nvl(tallyload.total_qty, 0) tot_completed_qty_load, ");
			queryBuf.append("	nvl(tallyload.total_ton, 0) tot_completed_load , ");
			queryBuf.append("	nvl(mtc.misc_type_nm, '') oprTypeName ");
			queryBuf.append("FROM ");
			queryBuf.append("	TOPS.Vessel_Call vc ");
			queryBuf.append("INNER JOIN Company_Code cc ON ");
			queryBuf.append("	(vc.vsl_opr_cd = cc.co_cd) ");
			queryBuf.append("INNER JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd1 stev_co_cd, ");
			queryBuf.append("		v.stev_contact1 stev_contact, ");
			queryBuf.append("		v.stev_remarks1 stev_remarks, ");
			queryBuf.append("		v.stev_rep1 stev_rep, ");
			queryBuf.append("		1 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd1 IS NOT NULL ");
			queryBuf.append("UNION ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd2 stev_co_cd, ");
			queryBuf.append("		v.stev_contact2 stev_contact, ");
			queryBuf.append("		v.stev_remarks2 stev_remarks, ");
			queryBuf.append("		v.stev_rep2 stev_rep, ");
			queryBuf.append("		2 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd2 IS NOT NULL ");
			queryBuf.append("UNION ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd3 stev_co_cd, ");
			queryBuf.append("		v.stev_contact3 stev_contact, ");
			queryBuf.append("		v.stev_remarks3 stev_remarks, ");
			queryBuf.append("		v.stev_rep3 stev_rep, ");
			queryBuf.append("		3 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd3 IS NOT NULL ) vs ON ");
			queryBuf.append("	(vc.vv_cd = vs.vv_cd) ");
			queryBuf.append("INNER JOIN stevedore_company sc ON ");
			queryBuf.append("	(vs.stev_co_cd = sc.stev_co_cd) ");
			queryBuf.append("INNER JOIN Berthing br ON ");
			queryBuf.append("	(vc.vv_cd = br.vv_cd ");
			queryBuf.append("	AND br.shift_ind = 1) ");
			queryBuf.append("LEFT JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		var_nbr vv_cd, ");
			queryBuf.append("		nvl((sum(nvl(md.Gross_WT, 0))/ 1000), 0) total_ton, ");
			queryBuf.append("		sum(nbr_pkgs) total_pkgs ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBMS.Manifest_Details md ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		bl_status = 'A' ");
			queryBuf.append("	GROUP BY ");
			queryBuf.append("		var_nbr ) disc ON ");
			queryBuf.append("	(vc.vv_cd = disc.vv_cd) ");
			queryBuf.append("LEFT JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		e.out_voy_var_nbr vv_cd, ");
			queryBuf.append("		nvl((sum(nvl(ed.ESN_WT, 0))/ 1000), 0) total_ton, ");
			queryBuf.append("		sum(nbr_pkgs) total_pkgs ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBMS.ESN e ");
			queryBuf.append("	INNER JOIN ESN_Details ed ON ");
			queryBuf.append("		(e.esn_asn_nbr = ed.esn_asn_nbr) ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		e.esn_status = 'A' ");
			queryBuf.append("	GROUP BY ");
			queryBuf.append("		e.out_voy_var_nbr ) load ON ");
			queryBuf.append("	(vc.vv_cd = load.vv_cd) ");
			queryBuf.append("LEFT JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		t.vv_cd, ");
			queryBuf.append("		t.stev_co_cd, ");
			queryBuf.append("		sum(trans_qty) total_qty, ");
			queryBuf.append("		sum(det.trans_ton) total_ton ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBCC.gbcc_cargo_tallysheet_det det ");
			queryBuf.append("	INNER JOIN gbcc_cargo_tallysheet t ON ");
			queryBuf.append("		(det.vv_cd = t.vv_cd ");
			queryBuf.append("		AND det.create_dttm = t.create_dttm ");
			queryBuf.append("		AND det.opr_type = t.opr_type ");
			queryBuf.append("		AND det.hatch_nbr = t.hatch_nbr) ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		t.opr_type = :oprTypeDischarge");
			queryBuf.append("	GROUP BY ");
			queryBuf.append("		t.vv_cd, ");
			queryBuf.append("		t.stev_co_cd ) tallydisc ON ");
			queryBuf.append("	(vc.vv_cd = tallydisc.vv_cd ");
			queryBuf.append("	AND vs.stev_co_cd = tallydisc.stev_co_cd ) ");
			queryBuf.append("LEFT JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		t.vv_cd, ");
			queryBuf.append("		t.stev_co_cd, ");
			queryBuf.append("		sum(trans_qty) total_qty, ");
			queryBuf.append("		sum(det.trans_ton) total_ton ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBCC.gbcc_cargo_tallysheet_det det ");
			queryBuf.append("	INNER JOIN gbcc_cargo_tallysheet t ON ");
			queryBuf.append("		(det.vv_cd = t.vv_cd ");
			queryBuf.append("		AND det.create_dttm = t.create_dttm ");
			queryBuf.append("		AND det.opr_type = t.opr_type ");
			queryBuf.append("		AND det.hatch_nbr = t.hatch_nbr) ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		t.opr_type = :oprTypeLoad");
			queryBuf.append("	GROUP BY ");
			queryBuf.append("		t.vv_cd, ");
			queryBuf.append("		t.stev_co_cd ) tallyload ON ");
			queryBuf.append("	(vc.vv_cd = tallyload.vv_cd ");
			queryBuf.append("	AND vs.stev_co_cd = tallyload.stev_co_cd ) ");
			queryBuf.append("LEFT JOIN Gbcc_Cargo_TallySheet co ON ");
			queryBuf.append("	(vc.vv_cd = co.vv_cd ");
			queryBuf.append("	AND vs.stev_co_cd = co.stev_co_cd ");
			queryBuf.append("	AND co.opr_type = :pOprType ");
			queryBuf.append("	AND co.hatch_nbr = :pHatchNo ) ");
			queryBuf.append("LEFT JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		create_dttm, ");
			queryBuf.append("		opr_type, ");
			queryBuf.append("		hatch_nbr, ");
			queryBuf.append("		sum(trans_qty) total_qty, ");
			queryBuf.append("		sum(trans_ton) total_ton ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBCC.gbcc_cargo_tallysheet_det det ");
			queryBuf.append("	GROUP BY ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		create_dttm, ");
			queryBuf.append("		opr_type, ");
			queryBuf.append("		hatch_nbr ) detsum ON ");
			queryBuf.append("	(co.vv_cd = detsum.vv_cd ");
			queryBuf.append("	AND co.create_dttm = detsum.create_dttm ");
			queryBuf.append("	AND co.opr_type = detsum.opr_type ");
			queryBuf.append("	AND co.hatch_nbr = detsum.hatch_nbr) ");
			queryBuf.append("LEFT JOIN Misc_Type_Code mtc ON ");
			queryBuf.append("	(co.opr_type = mtc.misc_type_cd ");
			queryBuf.append("	AND mtc.cat_cd = :pCatCd) ");
			queryBuf.append("WHERE ");
			queryBuf.append("	vc.vv_cd = :pVvCd");

			if (!"JP".equalsIgnoreCase(custCd.trim()) && (!"".equalsIgnoreCase(custCd.trim())))
				queryBuf.append(" and sc.co_cd = :pCustCd ");
			else
				queryBuf.append(" and sc.stev_co_cd = :pStevCd ");

			if (crDttm != null)
				queryBuf.append(" and co.create_dttm = :pCreateDttm ");

			log.info("SQL::" + queryBuf.toString());

			paramMap.put("pOprType", oprType.trim());
			paramMap.put("pHatchNo", hatchNo.intValue());
			paramMap.put("pCatCd", ConstantUtil.MISCTYPECD_OPERATION_TYPE);
			paramMap.put("oprTypeDischarge", ConstantUtil.TALLYSHEET_OPRTYPE_Discharge);
			paramMap.put("oprTypeLoad", ConstantUtil.TALLYSHEET_OPRTYPE_Load);

			paramMap.put("pVvCd", vvCd.trim());
			if (!"JP".equalsIgnoreCase(custCd.trim()) && (!"".equalsIgnoreCase(custCd.trim())))
				paramMap.put("pCustCd", custCd.trim());
			else
				paramMap.put("pStevCd", stevCd.trim());

			if (crDttm != null) {
				Timestamp ts = new Timestamp(crDttm.getTime());
				paramMap.put("pCreateDttm", ts);
			}

			log.info(": getCargoTallySheet  SQL " + queryBuf.toString() + " paramMap " + paramMap.toString());

			List<GbccCargoTallysheetVO> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<GbccCargoTallysheetVO>(GbccCargoTallysheetVO.class));

			if (lst.isEmpty())
				return null;

			GbccCargoTallysheetId id = null;
			for (GbccCargoTallysheetVO gbccCargVO : lst) {
				id = new GbccCargoTallysheetId();
				oVO = new GbccCargoTallysheetVO();
				oVO = gbccCargVO;

				if (oVO.getCreateDttm() == null) {
					oVO.setCargoTallysheetDetVO(getCargoTallySheetDetForStevedoreNet(vvCd, null, oprType, hatchNo));
				} else {
					oVO.setCargoTallysheetDetVO(getCargoTallySheetDetForStevedoreNet(vvCd, crDttm, oprType, hatchNo));
				}
				id.setVvCd(oVO.getVvCd());
				id.setOprType(oVO.getOprType());
				id.setCreateDttm(oVO.getCreateDttm());
				id.setHatchNbr(oVO.getHatchNbr());
				if (oVO.getHatchNbr() == null) {
					id.setHatchNbr(hatchNo);
					oVO.setHatchNbr(hatchNo);
				}
				oVO.setId(id);
				oVO.setStevedoreCompanyName((String) oVO.getStev_co_nm());
				oVO.setAgentName((String) oVO.getCo_nm());

				if (oprType.equalsIgnoreCase(ConstantUtil.TALLYSHEET_OPRTYPE_Discharge)) {
					oVO.setTotQtyDiscCompleted((Integer) oVO.getTotal_qty());
					oVO.setTotDiscCompleted((BigDecimal) oVO.getTotal_ton());

					oVO.setTotQtyLoadCompleted(new Integer(0));
					oVO.setTotLoadCompleted(new BigDecimal(0));
				} else {
					oVO.setTotQtyLoadCompleted((Integer) oVO.getTotal_qty());
					oVO.setTotLoadCompleted((BigDecimal) oVO.getTotal_ton());

					oVO.setTotQtyDiscCompleted(new Integer(0));
					oVO.setTotDiscCompleted(new BigDecimal(0));
				}

				oVO.setTotPkgsDiscOpenBal((Integer) oVO.getTot_pkgs_disc_openbal());
				oVO.setTotDiscOpenBal((BigDecimal) oVO.getTot_disc_openbal());

				oVO.setTotPkgsLoadOpenBal((Integer) oVO.getTot_pkgs_load_openbal());
				oVO.setTotLoadOpenBal((BigDecimal) oVO.getTot_load_openbal());

				oVO.setTotQtyDiscTotalCompleted((Integer) oVO.getTot_completed_qty_disc());
				oVO.setTotDiscTotalCompleted((BigDecimal) oVO.getTot_completed_disc());
				oVO.setTotQtyLoadTotalCompleted((Integer) oVO.getTot_completed_qty_load());
				oVO.setTotLoadTotalCompleted((BigDecimal) oVO.getTot_completed_load());
				oVO.setLastBerthingVO(getLastBerthing(vvCd));

			}

			log.info("getCargoTallySheet Result: " + oVO.toString());

		} catch (Exception e) {
			log.info("getCargoTallySheetById Exception: ", e);
		} finally {
			log.info("END: DAO getCargoTallySheetById");
		}

		return oVO;
	}

	public List<GbccCargoTallysheetDet> getCargoTallySheetDetForStevedoreNet(String vvCd, Date crDttm, String oprType,
			Integer hatchNo) {
		List<GbccCargoTallysheetDet> returnList = new ArrayList<GbccCargoTallysheetDet>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getCargoTallySheetDetForStevedoreNet  DAO  vvCd: " + vvCd + " crDttm: " + crDttm
					+ " oprType: " + oprType + " hatchNo: " + hatchNo);

			StringBuffer queryBuf = new StringBuffer();
			queryBuf.append("SELECT ");
			queryBuf.append("	det.* , ");
			queryBuf.append("	m.vv_cd, ");
			queryBuf.append("	m.bl_bk_nbr, ");
			queryBuf.append("	m.NBR_PKGS, ");
			queryBuf.append("	m.TOTAL_TON , ");
			queryBuf.append("	nvl(total_comp_qty, 0) total_comp_qty , ");
			queryBuf.append("	nvl(total_comp_ton, 0) total_comp_ton , ");
			queryBuf.append(
					"	(m.nbr_pkgs - nvl(total_comp_qty, 0) - nvl(sl_total.sl_total_qty, 0) - nvl(det.TRANS_QTY, 0) - nvl(sl.TRANS_QTY, 0)) bal_comp_qty , ");
			queryBuf.append(
					"	(m.total_ton - nvl(total_comp_ton, 0) - nvl(sl_total.sl_total_ton, 0) - nvl(det.TRANS_TON, 0) - nvl(sl.TRANS_TON, 0)) bal_comp_ton , ");
			queryBuf.append("	status, ");
			queryBuf.append("	ld_port, ");
			queryBuf.append("	detail1 , ");
			queryBuf.append("	detail2, " + getMarkNbr(oprType));
			queryBuf.append(", m.crg_des crg_des  FROM ");

			if (oprType.equalsIgnoreCase(ConstantUtil.TALLYSHEET_OPRTYPE_Discharge)) {

				queryBuf.append("( ");
				queryBuf.append("SELECT ");
				queryBuf.append("	var_nbr vv_cd, ");
				queryBuf.append("	bl_nbr bl_bk_nbr, ");
				queryBuf.append("	sum(nbr_pkgs) nbr_pkgs , ");
				queryBuf.append(
						"	sum(DECODE(SIGN(GROSS_WT / 1000-GROSS_VOL), -1, GROSS_VOL, 0, GROSS_VOL, 1, GROSS_WT / 1000)) total_ton, ");
				queryBuf.append("	crg_status status, ");
				queryBuf.append("	ld_port ld_port, ");
				queryBuf.append("	dis_type detail1, ");
				queryBuf.append("	dg_ind detail2, ");
				queryBuf.append("	MFT_SEQ_NBR, ");
				queryBuf.append("	crg_des ");
				queryBuf.append("FROM ");
				queryBuf.append("	GBMS.MANIFEST_DETAILS ");
				queryBuf.append("WHERE ");
				queryBuf.append("	bl_status = 'A' ");
				queryBuf.append("GROUP BY ");
				queryBuf.append("	var_nbr, ");
				queryBuf.append("	bl_nbr, ");
				queryBuf.append("	crg_status, ");
				queryBuf.append("	ld_port, ");
				queryBuf.append("	dis_type, ");
				queryBuf.append("	dg_ind, ");
				queryBuf.append("	MFT_SEQ_NBR, ");
				queryBuf.append("	crg_des ) m ");
			} else {

				queryBuf.append("( ");
				queryBuf.append("SELECT ");
				queryBuf.append("	out_voy_var_nbr vv_cd, ");
				queryBuf.append("	bk_ref_nbr bl_bk_nbr, ");
				queryBuf.append("	sum(nbr_pkgs) nbr_pkgs , ");
				queryBuf.append(
						"	sum(DECODE(SIGN(ESN_WT / 1000-ESN_VOL), -1, ESN_VOL, 0, ESN_VOL, 1, ESN_WT / 1000)) total_ton, ");
				queryBuf.append("	'' status, ");
				queryBuf.append("	esn_port_dis ld_port, ");
				queryBuf.append("	Esn_ops_ind detail1 , ");
				queryBuf.append("	Esn_dg_ind detail2, ");
				queryBuf.append("	d.crg_des, ");
				queryBuf.append("	e.ESN_ASN_NBR ");
				queryBuf.append("FROM ");
				queryBuf.append("	GBMS.ESN e ");
				queryBuf.append("INNER JOIN ESN_DETAILS d ON ");
				queryBuf.append("	(e.esn_asn_nbr = d.esn_asn_nbr) ");
				queryBuf.append("WHERE ");
				queryBuf.append("	esn_status != 'X' ");
				queryBuf.append("	AND trans_type = 'E' ");
				queryBuf.append("GROUP BY ");
				queryBuf.append("	out_voy_var_nbr, ");
				queryBuf.append("	bk_ref_nbr, ");
				queryBuf.append("	esn_port_dis, ");
				queryBuf.append("	Esn_ops_ind, ");
				queryBuf.append("	Esn_dg_ind, ");
				queryBuf.append("	d.crg_des, ");
				queryBuf.append("	e.ESN_ASN_NBR ");
				queryBuf.append("UNION ");
				queryBuf.append("SELECT ");
				queryBuf.append("	out_voy_var_nbr vv_cd, ");
				queryBuf.append("	bk_ref_nbr bl_bk_nbr, ");
				queryBuf.append("	sum(nbr_pkgs) nbr_pkgs , ");
				queryBuf.append(
						"	sum(DECODE(SIGN(NOM_WT / 1000-NOM_VOL), -1, NOM_VOL, 0, NOM_VOL, 1, NOM_WT / 1000)) total_ton, ");
				queryBuf.append("	'' status, ");
				queryBuf.append("	'' ld_port, ");
				queryBuf.append("	'' detail1 , ");
				queryBuf.append("	'' detail2, ");
				queryBuf.append("	'' crg_des, ");
				queryBuf.append("	e.ESN_ASN_NBR ");
				queryBuf.append("FROM ");
				queryBuf.append("	GBMS.ESN e ");
				queryBuf.append("INNER JOIN tesn_jp_jp d ON ");
				queryBuf.append("	(e.esn_asn_nbr = d.esn_asn_nbr) ");
				queryBuf.append("WHERE ");
				queryBuf.append("	esn_status != 'X' ");
				queryBuf.append("	AND trans_type = 'A' ");
				queryBuf.append("GROUP BY ");
				queryBuf.append("	out_voy_var_nbr, ");
				queryBuf.append("	bk_ref_nbr, ");
				queryBuf.append("	e.ESN_ASN_NBR ");
				queryBuf.append("UNION ");
				queryBuf.append("SELECT ");
				queryBuf.append("	out_voy_var_nbr vv_cd, ");
				queryBuf.append("	bk_ref_nbr bl_bk_nbr, ");
				queryBuf.append("	sum(nbr_pkgs) nbr_pkgs , ");
				queryBuf.append(
						"	SUM(DECODE(SIGN(GROSS_WT / 1000-GROSS_VOL), -1, GROSS_VOL, 0, GROSS_VOL, 1, GROSS_WT / 1000)) total_ton, ");
				queryBuf.append("	'' status, ");
				queryBuf.append("	'' ld_port, ");
				queryBuf.append("	'' detail1 , ");
				queryBuf.append("	'' detail2, ");
				queryBuf.append("	'' crg_des, ");
				queryBuf.append("	e.ESN_ASN_NBR ");
				queryBuf.append("FROM ");
				queryBuf.append("	GBMS.ESN e ");
				queryBuf.append("INNER JOIN tesn_psa_jp d ON ");
				queryBuf.append("	(e.esn_asn_nbr = d.esn_asn_nbr) ");
				queryBuf.append("WHERE ");
				queryBuf.append("	esn_status != 'X' ");
				queryBuf.append("	AND trans_type = 'C' ");
				queryBuf.append("GROUP BY ");
				queryBuf.append("	out_voy_var_nbr, ");
				queryBuf.append("	bk_ref_nbr, ");
				queryBuf.append("	e.ESN_ASN_NBR ) m ");

			}
			queryBuf.append(
					" LEFT JOIN GBCC_CARGO_TALLYSHEET_Det det ON (m.vv_cd = det.vv_cd AND m.bl_bk_nbr = det.bl_bk_nbr ");

			if (crDttm == null) {
				queryBuf.append("  AND create_dttm is null) ");
			} else {
				queryBuf.append(" AND create_dttm = :pCrDttm  AND opr_type = :pOprType AND hatch_nbr = :pHatchNo ) ");
			}

			queryBuf.append(" LEFT JOIN gbcc_cargo_tallysheet_ol_sl sl  ON (sl.bl_bk_nbr  = det.bl_bk_nbr ");
			if (crDttm == null) {
				queryBuf.append(" AND sl.create_dttm is null) ");
			} else {
				queryBuf.append(
						" AND sl.create_dttm = :pCrDttm  AND sl.type = :pOprType  AND sl.opr_type = 'SH'  AND sl.hatch_nbr = :pHatchNo ) ");
			}

			if (oprType.equalsIgnoreCase(ConstantUtil.TALLYSHEET_OPRTYPE_Discharge)) {
				queryBuf.append("LEFT JOIN MFT_MARKINGS MS ON m.MFT_SEQ_NBR = MS.MFT_SQ_NBR");
			} else {
				queryBuf.append("LEFT JOIN ESN_MARKINGS ES ON m.ESN_ASN_NBR = ES.ESN_ASN_NBR");
			}

			queryBuf.append(
					" LEFT JOIN  ( SELECT bl_bk_nbr, Sum(trans_qty) total_comp_qty, Sum(trans_ton) total_comp_ton  FROM GBCC.Gbcc_Cargo_TallySheet_Det det  WHERE vv_cd = :pVvCd0   AND opr_type = :pOprType0 ");

			if (crDttm != null) {
				queryBuf.append("" + " AND (create_dttm <> :pCrDttm0 " + " AND hatch_nbr <> :pHatchNo0 )" + " ");
			}
			queryBuf.append("" + " GROUP BY bl_bk_nbr " + ") total " + " ON (m.bl_bk_nbr = total.bl_bk_nbr) " + " ");

			queryBuf.append("" + " LEFT JOIN "
					+ " ( SELECT bl_bk_nbr, Sum(trans_qty) sl_total_qty, Sum(trans_ton) sl_total_ton "
					+ " FROM GBCC.Gbcc_Cargo_TallySheet_ol_sl " + " WHERE opr_type = 'SH' AND type = :pOprType0 " + "");
			if (crDttm != null) {
				queryBuf.append("" + " AND (create_dttm <> :pCrDttm0  AND hatch_nbr <> :pHatchNo0 ) ");
			}
			queryBuf.append(" GROUP BY bl_bk_nbr ) sl_total  ON (m.bl_bk_nbr = sl_total.bl_bk_nbr)  ");

			queryBuf.append("  WHERE  m.vv_cd = :pVvCd  ORDER BY m.bl_bk_nbr ");

			log.info("SQL::" + queryBuf.toString());

			if (crDttm != null) {
				Timestamp crTs = null;
				crTs = new Timestamp(crDttm.getTime());

				paramMap.put("pCrDttm", crTs);
				paramMap.put("pOprType", oprType.trim());
				paramMap.put("pHatchNo", hatchNo.intValue());
			}
			paramMap.put("pVvCd0", vvCd.trim());
			paramMap.put("pOprType0", oprType.trim());
			if (crDttm != null) {
				Timestamp crTs = null;
				crTs = new Timestamp(crDttm.getTime());
				paramMap.put("pCrDttm0", crTs);

				paramMap.put("pHatchNo0", hatchNo.intValue());
			}

			paramMap.put("pVvCd", vvCd.trim());

			log.info(": getCargoTallySheetDetForStevedoreNet  SQL " + queryBuf.toString() + " paramMap "
					+ paramMap.toString());

			List<GbccCargoTallysheetDet> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<GbccCargoTallysheetDet>(GbccCargoTallysheetDet.class));

			String details1 = "";
			String details2 = "";
			String status = "";

			if (lst != null) {
				GbccCargoTallysheetDetId id = null;
				GbccCargoTallysheetDet oVO = null;
				for (GbccCargoTallysheetDet gbccCargoDet : lst) {
					id = new GbccCargoTallysheetDetId();
					oVO = new GbccCargoTallysheetDet();
					oVO = gbccCargoDet;
					if (oVO.getOprType() != null && oVO.getHatchNbr() != null) {
						oVO = gbccCargoDet;
					} else {
						GbccCargoTallysheetDetId oId = new GbccCargoTallysheetDetId();
						oId.setOprType(oprType);
						oId.setHatchNbr(hatchNo);
						oId.setVvCd((String) oVO.getVvCd());
						oId.setBlBkNbr((String) oVO.getBl_bk_nbr());
						oVO.setId(oId);
					}

					id.setOprType(oVO.getOprType());
					id.setHatchNbr(oVO.getHatchNbr());
					id.setCreateDttm(oVO.getCreateDttm());
					id.setVvCd((String) oVO.getVvCd());
					id.setBlBkNbr((String) oVO.getBl_bk_nbr());
					oVO.setId(id);
					oVO.setBlBkNbr((String) oVO.getBl_bk_nbr());

					oVO.setNbrPkgs((Integer) oVO.getNbr_pkgs());
					oVO.setNbrTon((BigDecimal) oVO.getTotal_ton());

					oVO.setCompletedQty((Integer) oVO.getTotal_comp_qty());
					oVO.setCompletedTon((BigDecimal) oVO.getTotal_comp_ton());
					oVO.setBalQty((Integer) oVO.getBal_comp_qty());
					oVO.setBalTon((BigDecimal) oVO.getBal_comp_ton());
					status = (String) oVO.getStatus();
					oVO.setLdPort((String) oVO.getLd_port());
					details1 = (String) oVO.getDetail1();
					details2 = (String) oVO.getDetail2();
					oVO.setMarkNbr((String) oVO.getMark_nbr());
					oVO.setCargoDes((String) oVO.getCrg_des());

					if ("L".equalsIgnoreCase(status)) {
						status = "Local Import";
					} else if ("T".equalsIgnoreCase(status)) {
						status = "Transshipment";
					} else if ("R".equalsIgnoreCase(status)) {
						status = "Re-export";
					}

					// to set status
					oVO.setStatus(status);

					if (ConstantUtil.STRING_OVERSIDE_CD.equalsIgnoreCase(details1)) {
						details1 = ConstantUtil.STRING_OVERSIDE;
					} else if (ConstantUtil.STRING_DIRECT_CD.equalsIgnoreCase(details1)) {
						details1 = ConstantUtil.STRING_DIRECT;
					} else if (ConstantUtil.STRING_NORMAL_CD.equalsIgnoreCase(details1)) {
						details1 = ConstantUtil.STRING_NORMAL;
					} else if (ConstantUtil.STRING_LAND_RESHIP_CD.equalsIgnoreCase(details1)) {
						details1 = ConstantUtil.STRING_LAND_RESHIP;
					} else {
						details1 = "";
					}

					if (ConstantUtil.STRING_VALUE_YES_CODE.equalsIgnoreCase(details2)) {
						details2 = ConstantUtil.STRING_VALUE_YES;
					} else if (ConstantUtil.STRING_VALUE_NO_CODE.equalsIgnoreCase(details2)) {
						details2 = ConstantUtil.STRING_VALUE_NO;
					} else {
						details2 = "";
					}
					// to set detail
					oVO.setDetail(details1.equalsIgnoreCase("") ? details2
							: details1 + (details2.equalsIgnoreCase("") ? "" : "," + details2));

					returnList.add(oVO);

				}
				log.info("getCargoTallySheetDet Result" + returnList.toString());
			}
			return returnList;
		} catch (Exception e) {
			log.info("getCargoTallySheetDet Exception: ", e);
		}
		return null;
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	private String getMarkNbr(String oprType) {
		return oprType.equalsIgnoreCase(ConstantUtil.TALLYSHEET_OPRTYPE_Discharge) ? "MS.MFT_MARKINGS mark_nbr"
				: "ES.MARKINGS mark_nbr";
	}

	@Override
	public boolean saveCargoTallySheet(GbccCargoTallysheetVO transientObject) throws BusinessException {
		boolean result;
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: saveCargoTallySheet  DAO GbccCargoTallysheetVO: " + transientObject.toString());
			boolean isNew = false;
			Date dttm = new Date();
			transientObject.setLastModifyDttm(dttm);
			if (transientObject.getId().getCreateDttm() == null) {
				transientObject.setCreateDttm(dttm);
				transientObject.setSubmittedDttm(dttm);
				isNew = true;

				sb.append("INSERT ");
				sb.append("	INTO ");
				sb.append("	GBCC.GBCC_CARGO_TALLYSHEET (VV_CD, ");
				sb.append("	CREATE_DTTM, ");
				sb.append("	OPR_TYPE, ");
				sb.append("	HATCH_NBR, ");
				sb.append("	STEV_CO_CD, ");
				sb.append("	CHECKER_NM, ");
				sb.append("	CHECKER_HP, ");
				sb.append("	WORK_COMMENCE_DTTM, ");
				sb.append("	WORK_COMPLETE_DTTM, ");
				sb.append("	FROM_DTTM, ");
				sb.append("	TO_DTTM, ");
				sb.append("	SUBMITTED_DTTM, ");
				sb.append("	LAST_MODIFY_USER_ID, ");
				sb.append("	LAST_MODIFY_DTTM, ");
				sb.append("	TALLY_CHECKER_NM, ");
				sb.append("	TALLY_CHECKER_HP) ");
				sb.append("VALUES(:vvCd, ");
				sb.append(":createDttm, ");
				sb.append(":oprType, ");
				sb.append(":hatchNbr, ");
				sb.append(":stevCoCd, ");
				sb.append(":checkerNm, ");
				sb.append(":checkerHp, ");
				sb.append(":workCommenceDttm, ");
				sb.append(":workCompleteDttm, ");
				sb.append(":fromDttm, ");
				sb.append(":toDttm, ");
				sb.append(":submittedDttm, ");
				sb.append(":lastModifyUserId, ");
				sb.append(":lastModifyDttm, ");
				sb.append(":tallyCheckerNm, ");
				sb.append(":tallyCheckerHp)");

			} else {
				sb = new StringBuffer();
				sb.append("UPDATE ");
				sb.append("	GBCC.GBCC_CARGO_TALLYSHEET ");
				sb.append("SET ");
				sb.append("	STEV_CO_CD =:stevCoCd, ");
				sb.append("	CHECKER_NM =:checkerNm, ");
				sb.append("	CHECKER_HP =:checkerHp, ");
				sb.append("	WORK_COMMENCE_DTTM =:workCommenceDttm, ");
				sb.append("	WORK_COMPLETE_DTTM =:workCompleteDttm, ");
				sb.append("	FROM_DTTM =:fromDttm, ");
				sb.append("	TO_DTTM =:toDttm, ");
				sb.append("	SUBMITTED_DTTM =:submittedDttm, ");
				sb.append("	LAST_MODIFY_USER_ID =:lastModifyUserId, ");
				sb.append("	LAST_MODIFY_DTTM =:lastModifyDttm, ");
				sb.append("	TALLY_CHECKER_NM =:tallyCheckerNm, ");
				sb.append("	TALLY_CHECKER_HP = :tallyCheckerHp ");
				sb.append("WHERE ");
				sb.append("	VV_CD = :vvCd ");
				sb.append("	AND CREATE_DTTM = :createDttm ");
				sb.append("	AND OPR_TYPE = :oprType ");
				sb.append("	AND HATCH_NBR = :hatchNbr");

			}

			transientObject.setAuditUserId(transientObject.getLastModifyUserId());
			transientObject.setFnType(ConstantUtil.AUDIT_FNTYPE_CARGOOPS);
			transientObject.setFnsubType(ConstantUtil.AUDIT_FNSUBTYPE_CARGOTALLYSHEET_UPDATE);
			transientObject.setTableNm(ConstantUtil.TABLE_GBCCCARGOTALLYSHEET);
			transientObject.setKeyType1(ConstantUtil.AUDIT_KEYID_VVCD);
			transientObject.setKeyVal1(transientObject.getId().getVvCd());

			String fieldvalue = ConstantUtil.AUDIT_KEYID_VVCD + "=" + transientObject.getId().getVvCd();
			transientObject.setFieldNewValue(fieldvalue);
			transientObject.setFieldOldValue(fieldvalue);
			transientObject.setOprType(transientObject.getId().getOprType());
			transientObject.setHatchNbr(transientObject.getId().getHatchNbr());

			log.info("saveCargoTallySheet SQL  " + sb.toString() + " GbccCargoTallysheetVO"
					+ transientObject.toString());

			int count = namedParameterJdbcTemplate.update(sb.toString(),
					new BeanPropertySqlParameterSource(transientObject));
			if (count == 0) {
				log.info("not amended");
			}
			// cSession.saveOrUpdate(transientObject);

			List<GbccCargoTallysheetDet> detlst = transientObject.getCargoTallysheetDetVO();

			if (detlst != null) {
				int hatchSize = detlst.size();

				for (int i = 0; i < hatchSize; i++) {
					GbccCargoTallysheetDet detVO = (GbccCargoTallysheetDet) detlst.get(i);

					if (isNew) {
						sb = new StringBuffer();

						detVO.setCreateDttm(dttm);
						sb.append("INSERT ");
						sb.append("	INTO ");
						sb.append("	GBCC.GBCC_CARGO_TALLYSHEET_DET (VV_CD, ");
						sb.append("	CREATE_DTTM, ");
						sb.append("	OPR_TYPE, ");
						sb.append("	HATCH_NBR, ");
						sb.append("	BL_BK_NBR, ");
						sb.append("	TRANS_QTY, ");
						sb.append("	TRANS_TON, ");
						sb.append("	RECV_IND, ");
						sb.append("	LAST_MODIFY_USER_ID, ");
						sb.append("	LAST_MODIFY_DTTM, ");
						sb.append("	REMARKS) ");
						sb.append("VALUES(:vvCd, ");
						sb.append(":createDttm, ");
						sb.append(":oprType, ");
						sb.append(":hatchNbr, ");
						sb.append(":blBkNbr, ");
						sb.append(":transQty, ");
						sb.append(":transTon, ");
						sb.append(":recvInd, ");
						sb.append(":lastModifyUserId, ");
						sb.append(":lastModifyDttm, ");
						sb.append(":remarks)");
						saveCargoTallySheetDet(sb.toString(), detVO);

					} else {
						sb = new StringBuffer();
						sb.append("UPDATE ");
						sb.append("	GBCC.GBCC_CARGO_TALLYSHEET_DET ");
						sb.append("SET ");
						sb.append("	TRANS_QTY =:transQty, ");
						sb.append("	TRANS_TON =:transTon, ");
						sb.append("	RECV_IND =:recvInd, ");
						sb.append("	LAST_MODIFY_USER_ID =:lastModifyUserId, ");
						sb.append("	LAST_MODIFY_DTTM =:lastModifyDttm, ");
						sb.append("	REMARKS =:remarks ");
						sb.append("WHERE ");
						sb.append("	VV_CD =:vvCd ");
						sb.append("	AND CREATE_DTTM =:createDttm ");
						sb.append("	AND OPR_TYPE =:oprType ");
						sb.append("	AND HATCH_NBR =:hatchNbr ");
						sb.append("	AND BL_BK_NBR =:blBkNbr");
						saveCargoTallySheetDet(sb.toString(), detVO);
					}

				}
			}

			result = true;
			log.info("saveCargoTallySheet Result: " + result);
		} catch (BusinessException ex) {
			log.info("Exception saveCargoTallySheet : ", ex);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			result = false;
			log.info("Exception saveCargoTallySheet : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO saveCargoTallySheet");
		}
		return result;
	}

	private void saveCargoTallySheetDet(String query, GbccCargoTallysheetDet transientObject) throws BusinessException {
		try {
			log.info("START: saveCargoTallySheetDet  DAO GbccCargoTallysheetDet: " + transientObject.toString());
			if (transientObject != null) {

				Date dttm = new Date();
				transientObject.setLastModifyDttm(dttm);
				transientObject.setAuditUserId(transientObject.getLastModifyUserId());
				transientObject.setFnType(ConstantUtil.AUDIT_FNTYPE_CARGOOPS);
				transientObject.setFnsubType(ConstantUtil.AUDIT_FNSUBTYPE_CARGOTALLYSHEETDET_UPDATE);
				transientObject.setTableNm(ConstantUtil.TABLE_GBCCCARGOTALLYSHEETDET);
				transientObject.setKeyType1(ConstantUtil.AUDIT_KEYID_VVCD);
				transientObject.setKeyVal1(transientObject.getId().getVvCd());

				String fieldvalue = ConstantUtil.AUDIT_KEYID_VVCD + "=" + transientObject.getId().getVvCd();
				transientObject.setFieldNewValue(fieldvalue);
				transientObject.setFieldOldValue(fieldvalue);

				log.info("saveCargoTallySheetDet SQL  " + query + " GbccCargoTallysheetDet"
						+ transientObject.toString());

				int count = namedParameterJdbcTemplate.update(query,
						new BeanPropertySqlParameterSource(transientObject));
				if (count == 0) {
					log.info("not amended");
				}
				log.info("saveCargoTallySheetDet Result: " + count);
			}

		} catch (Exception ex) {
			log.info("Exception saveCargoTallySheetDet : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO saveCargoTallySheetDet");
		}
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	@Override
	public List<GbccCargoTallysheetVO> getCargoTallySheet(String CustCode, String vvCd, String berthNo, String oprType,
			Integer hatchNo, String sortBy, Criteria criteria, Boolean needAllData) throws BusinessException {
		List<GbccCargoTallysheetVO> returnList = new ArrayList<GbccCargoTallysheetVO>();
		Map<String, Object> paramMap = new HashMap<>();
		String sql = "";
		TableData tableData = new TableData();
		try {
			log.info("START: getCargoTallySheet  DAO CustCode: " + CustCode + " vvCd: " + vvCd + " berthNo: " + berthNo
					+ " oprType: " + oprType + " hatchNo: " + hatchNo + " sortBy: " + sortBy);
			StringBuffer queryBuf = new StringBuffer();

			queryBuf.append("SELECT ");
			queryBuf.append(
					"	co.VV_CD vvCd, co.CREATE_DTTM, co.OPR_TYPE, co.HATCH_NBR, co.STEV_CO_CD, co.CHECKER_NM, co.CHECKER_HP, ");
			queryBuf.append("   co.WORK_COMMENCE_DTTM, co.WORK_COMPLETE_DTTM, co.FROM_DTTM, co.TO_DTTM, ");
			queryBuf.append(
					"   co.SUBMITTED_DTTM, co.LAST_MODIFY_USER_ID lastModifyUserId, co.LAST_MODIFY_DTTM lastModifyDttm, co.TALLY_CHECKER_NM, co.TALLY_CHECKER_HP, ");

			queryBuf.append(
					"	vc.VV_CD vv_Cd, vc.VSL_NM, vc.IN_VOY_NBR, vc.OUT_VOY_NBR, vc.VV_STATUS_IND, vc.SHPG_SVC_CD, vc.SHPG_ROUTE_NBR,  ");
			queryBuf.append(
					"	vc.ROUTE_NM, vc.VSL_OPR_CD, vc.BERTH_APPL_DTTM, vc.VSL_BERTH_DTTM, vc.VSL_ETD_DTTM, vc.VV_CLOSE_DTTM, ");
			queryBuf.append(
					"	vc.PORT_FR, vc.PORT_TO, vc.ARRIVAL_DRAFT, vc.DEPARTURE_DRAFT, vc.BERTH_SIDE_IND, vc.CONTACT_NM, ");
			queryBuf.append(
					"	vc.CONTACT_HOME_TEL, vc.CONTACT_OFF_TEL, vc.CONTACT_PGR, vc.CONTACT_FAX, vc.CNTR_DISC, vc.CNTR_LOAD, vc.UC_LOAD, vc.UC_DISC, vc.COB_DTTM, vc.BERTH_APPL_REM, ");
			queryBuf.append(
					"	vc.BILL_MARINE_IND, vc.BILL_STEV_IND, vc.BILL_OTHER_IND, vc.BILL_ADMIN_IND, vc.CREATE_USER_ID, vc.CREATE_ACCT_NBR, vc.LAST_MODIFY_USER_ID vsl_lastModifyUserId, vc.LAST_MODIFY_DTTM vsl_lastModifyDttm, ");
			queryBuf.append(
					"	vc.SCHEME, vc.TERMINAL, vc.VSL_LOA, vc.LOC_FR, vc.LOC_TO, vc.ALONGSIDE_DRAFT, vc.VSL_UNDER_TOW_IND, vc.CONTACT2_NM, ");
			queryBuf.append(
					"	vc.CONTACT2_HOME_TEL, vc.CONTACT2_OFF_TEL, vc.CONTACT2_PGR, vc.CONTACT2_FAX, vc.TOTAL_CARGO_ONBOARD, vc.LOAD_DISPLACEMENT, vc.DG_CARGO_IND, vc.HLIFT, ");
			queryBuf.append(
					"	vc.HLIFT_OVERWHARF, vc.HLIFT_OVERSIDE, vc.MOBILE_CR_DWT, vc.MOBILE_CR_SWL, vc.CNTR_VSL_IND, vc.GB_CLOSE_VSL_IND, vc.GB_CLOSE_BJ_IND, vc.GB_CLOSE_SHP_IND, ");
			queryBuf.append(
					"	vc.CARGO_MODE, vc.BERTH_ALLOC_REM, vc.STORAGE_SPACE_IND, vc.BILL_ACCT_NBR, vc.GB_ARRIVAL_WAIVER_CD, vc.GB_ARRIVAL_WAIVER_REASON, vc.GB_DEPARTURE_WAIVER_CD, vc.GB_DEPARTURE_WAIVER_REASON, ");
			queryBuf.append(
					"	vc.GB_BERT_BILL_IND, vc.BRIDGE_DIST_FROM_BOW, vc.VACATE_BERTH_IND, vc.MIXED_SCHEME_IND, vc.DISC_CM_CD, vc.LOAD_CM_CD, vc.DISC_BER_REM, vc.LOAD_BER_REM, ");
			queryBuf.append(
					"	vc.CRG_DET_PROC, vc.DECLARANT_CUST_CD, vc.UC_DISC_CM_CD, vc.UC_LOAD_CM_CD, vc.UC_DISC_BER_REM, vc.UC_LOAD_BER_REM, vc.EST_LONG_CR_MOVE_NBR, vc.EST_THROUGHPUT_NBR, ");
			queryBuf.append(
					"	vc.SENT_TO_PSA_IND, vc.ADVICE_DTTM, vc.ABBR_IN_VOY_NBR, vc.ABBR_OUT_VOY_NBR, vc.INCENTIVE_CLASS, vc.ISPS_LEVEL, vc.BILL_OPEN_TS_IND, vc.SMS_ALERT_REP1_IND, ");
			queryBuf.append(
					"	vc.SMS_ALERT_REP2_IND, vc.TANDEM_LIFT_IND tandemLiftInd, vc.BILL_PROD_SURCHRG_IND, vc.ALLOC_PROD_PRD, vc.REEFER_PARTY, vc.PROTRUSION_IND, vc.FLOAT_CRANE_IND, vc.FIRST_SCHEME, ");
			queryBuf.append(
					"	vc.GB_CLOSE_LCT_IND, vc.GB_CLOSE_LCT_DTTM, vc.GB_CLOSE_LCT_USER_ID, vc.STORAGE_DET_PROC, vc.GB_ARRIVAL_WAIVER_IND, vc.GB_ARRIVAL_WAIVER_AMOUNT, vc.GB_AUTO_CLOSE_BJ_IND, vc.GB_AUTO_CLOSE_SHP_IND, ");
			queryBuf.append(
					"	vc.LAST_ATU_DTTM, vc.CARGO_WT, vc.TOTAL_CARGO_WT_MOBILE_CR_DWT, vc.LCT_MIN_CHRG_IND, vc.CEMENT_VSL_IND, vc.BTR_AFT_CLOSING1_DTTM, vc.NOM_IND, vc.NOM_PROCESS_IND, ");
			queryBuf.append(
					"	vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND, vc.PRINCIPAL_CARRIER_CD, vc.USE_SHP_CRN_IND, ");

			queryBuf.append(
					"	vs.VV_CD stev_VvCd, vs.STEV_CO_CD stev_coCd, vs.STEV_CONTACT, vs.STEV_REMARKS, vs.STEV_REP, vs.LINENO, vs.LAST_MODIFY_USER_ID gbccView_lastModifyUserId, ");

			queryBuf.append(
					"	br.VV_CD berth_VvCd, br.SHIFT_IND, br.ETB_DTTM, br.ETU_DTTM, br.ATB_DTTM, br.ATU_DTTM, br.FIRST_DISC_DTTM, br.FIRST_LOAD_DTTM, br.COD_DTTM, br.COL_DTTM,  ");
			queryBuf.append(
					"	br.BERTH_NBR, br.WHARF_MARK_FR, br.WHARF_MARK_TO, br.WHARF_SIDE_IND, br.LAST_MODIFY_USER_ID berth_lastModifyUserId, br.LAST_MODIFY_DTTM berth_lastModifyDttm, br.GB_COD_DTTM, br.GB_COL_DTTM, br.GB_FIRST_ACT_DTTM, br.GB_LAST_ACT_DTTM,  ");
			queryBuf.append(
					"	br.GB_BCOD_DTTM, br.GB_BCOL_DTTM, br.HAUL_DIST, br.HAUL_DIRN, br.GANG_NBR, br.HATCH_NBR berth_hatchNbr, br.DELAY_RSN_CD, br.REMARKS berthRemarks, br.TOT_GEN_CARGO_ACT, br.GB_FIRST_CARGO_ACT_DTTM, br.DEP_DRAFT_FORTH,  ");
			queryBuf.append(
					"	br.GB_FIRST_DISC_DTTM, br.GB_FIRST_LOAD_DTTM, br.ACT_DRAFT_FORTH, br.ACT_DRAFT_AFT, br.ACT_WHARF_SIDE_IND, br.ACT_WHARF_MARK_FR, br.ACT_WHARF_MARK_TO, br.BERTH_REMARKS, br.COL_MODIFY_DTTM, br.DEP_DRAFT_AFT,  ");

			queryBuf.append("	sc.stev_co_nm, ");
			queryBuf.append("	cc.co_nm , ");
			queryBuf.append("	detsum.total_qty, ");
			queryBuf.append("	detsum.total_ton , ");
			queryBuf.append("	nvl(disc.total_pkgs, 0) tot_pkgs_disc_openbal, ");
			queryBuf.append("	nvl(disc.total_ton, 0) tot_disc_openbal , ");
			queryBuf.append("	nvl(load.total_pkgs, 0) tot_pkgs_load_openbal, ");
			queryBuf.append("	nvl(load.total_ton, 0) tot_load_openbal , ");
			queryBuf.append("	nvl(tallydisc.total_qty, 0) tot_completed_qty_disc, ");
			queryBuf.append("	nvl(tallydisc.total_ton, 0) tot_completed_disc , ");
			queryBuf.append("	nvl(tallyload.total_qty, 0) tot_completed_qty_load, ");
			queryBuf.append("	nvl(tallyload.total_ton, 0) tot_completed_load , ");
			queryBuf.append("	nvl(mtc.misc_type_nm, '') oprTypeName ");
			queryBuf.append("FROM ");
			queryBuf.append("	GBCC.Gbcc_Cargo_TallySheet co ");
			queryBuf.append("INNER JOIN Vessel_Call vc ON ");
			queryBuf.append("	(co.vv_cd = vc.vv_cd) ");
			queryBuf.append("INNER JOIN Company_Code cc ON ");
			queryBuf.append("	(vc.vsl_opr_cd = cc.co_cd) ");
			queryBuf.append("INNER JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd1 stev_co_cd, ");
			queryBuf.append("		v.stev_contact1 stev_contact, ");
			queryBuf.append("		v.stev_remarks1 stev_remarks, ");
			queryBuf.append("		v.stev_rep1 stev_rep, ");
			queryBuf.append("		1 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd1 IS NOT NULL ");
			queryBuf.append("UNION ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd2 stev_co_cd, ");
			queryBuf.append("		v.stev_contact2 stev_contact, ");
			queryBuf.append("		v.stev_remarks2 stev_remarks, ");
			queryBuf.append("		v.stev_rep2 stev_rep, ");
			queryBuf.append("		2 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd2 IS NOT NULL ");
			queryBuf.append("UNION ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd3 stev_co_cd, ");
			queryBuf.append("		v.stev_contact3 stev_contact, ");
			queryBuf.append("		v.stev_remarks3 stev_remarks, ");
			queryBuf.append("		v.stev_rep3 stev_rep, ");
			queryBuf.append("		3 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd3 IS NOT NULL ) vs ON ");
			queryBuf.append("	(vc.vv_cd = vs.vv_cd ");
			queryBuf.append("	AND co.STEV_CO_CD = vs.stev_co_cd) ");
			queryBuf.append("INNER JOIN stevedore_company sc ON ");
			queryBuf.append("	(vs.stev_co_cd = sc.stev_co_cd) ");
			queryBuf.append("INNER JOIN Berthing br ON ");
			queryBuf.append("	(vc.vv_cd = br.vv_cd ");
			queryBuf.append("	AND br.shift_ind = 1) ");
			queryBuf.append("INNER JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		create_dttm, ");
			queryBuf.append("		opr_type, ");
			queryBuf.append("		hatch_nbr, ");
			queryBuf.append("		sum(trans_qty) total_qty, ");
			queryBuf.append("		sum(trans_ton) total_ton ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBCC.gbcc_cargo_tallysheet_det det ");
			queryBuf.append("	GROUP BY ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		create_dttm, ");
			queryBuf.append("		opr_type, ");
			queryBuf.append("		hatch_nbr ) detsum ON ");
			queryBuf.append("	(co.vv_cd = detsum.vv_cd ");
			queryBuf.append("	AND co.create_dttm = detsum.create_dttm ");
			queryBuf.append("	AND co.opr_type = detsum.opr_type ");
			queryBuf.append("	AND co.hatch_nbr = detsum.hatch_nbr) ");
			queryBuf.append("LEFT JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		var_nbr vv_cd, ");
			queryBuf.append("		nvl((sum(nvl(md.Gross_WT, 0))/ 1000), 0) total_ton, ");
			queryBuf.append("		sum(nbr_pkgs) total_pkgs ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBMS.Manifest_Details md ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		bl_status = 'A' ");
			queryBuf.append("	GROUP BY ");
			queryBuf.append("		var_nbr ) disc ON ");
			queryBuf.append("	(vc.vv_cd = disc.vv_cd) ");
			queryBuf.append("LEFT JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		e.out_voy_var_nbr vv_cd, ");
			queryBuf.append("		nvl((sum(nvl(ed.ESN_WT, 0))/ 1000), 0) total_ton, ");
			queryBuf.append("		sum(nbr_pkgs) total_pkgs ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBMS.ESN e ");
			queryBuf.append("	INNER JOIN ESN_Details ed ON ");
			queryBuf.append("		(e.esn_asn_nbr = ed.esn_asn_nbr) ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		e.esn_status = 'A' ");
			queryBuf.append("	GROUP BY ");
			queryBuf.append("		e.out_voy_var_nbr ) load ON ");
			queryBuf.append("	(vc.vv_cd = load.vv_cd) ");
			queryBuf.append("LEFT JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		t.vv_cd, ");
			queryBuf.append("		t.stev_co_cd, ");
			queryBuf.append("		sum(trans_qty) total_qty, ");
			queryBuf.append("		sum(det.trans_ton) total_ton ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBCC.gbcc_cargo_tallysheet_det det ");
			queryBuf.append("	INNER JOIN gbcc_cargo_tallysheet t ON ");
			queryBuf.append("		(det.vv_cd = t.vv_cd ");
			queryBuf.append("		AND det.create_dttm = t.create_dttm ");
			queryBuf.append("		AND det.opr_type = t.opr_type ");
			queryBuf.append("		AND det.hatch_nbr = t.hatch_nbr) ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		t.opr_type = :oprTypeDischarge");
			queryBuf.append("	GROUP BY ");
			queryBuf.append("		t.vv_cd, ");
			queryBuf.append("		t.stev_co_cd ) tallydisc ON ");
			queryBuf.append("	(co.vv_cd = tallydisc.vv_cd ");
			queryBuf.append("	AND co.stev_co_cd = tallydisc.stev_co_cd ) ");
			queryBuf.append("LEFT JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		t.vv_cd, ");
			queryBuf.append("		t.stev_co_cd, ");
			queryBuf.append("		sum(trans_qty) total_qty, ");
			queryBuf.append("		sum(det.trans_ton) total_ton ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBCC.gbcc_cargo_tallysheet_det det ");
			queryBuf.append("	INNER JOIN gbcc_cargo_tallysheet t ON ");
			queryBuf.append("		(det.vv_cd = t.vv_cd ");
			queryBuf.append("		AND det.create_dttm = t.create_dttm ");
			queryBuf.append("		AND det.opr_type = t.opr_type ");
			queryBuf.append("		AND det.hatch_nbr = t.hatch_nbr) ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		t.opr_type = :oprTypeLoad");
			queryBuf.append("	GROUP BY ");
			queryBuf.append("		t.vv_cd, ");
			queryBuf.append("		t.stev_co_cd ) tallyload ON ");
			queryBuf.append("	(co.vv_cd = tallyload.vv_cd ");
			queryBuf.append("	AND co.stev_co_cd = tallyload.stev_co_cd ) ");
			queryBuf.append("LEFT JOIN Misc_Type_Code mtc ON ");
			queryBuf.append("	(co.opr_type = mtc.misc_type_cd ");
			queryBuf.append("	AND mtc.cat_cd = :pCatCd) ");
			queryBuf.append("WHERE ");
			queryBuf.append("	1 = 1");

			if (!"JP".equalsIgnoreCase(CustCode.trim()) && (!"".equalsIgnoreCase(CustCode.trim())))
				queryBuf.append(" and sc.co_cd = :pCustCd ");

			if (!"".equalsIgnoreCase(vvCd.trim()))
				queryBuf.append(" and vc.vv_cd = :pVvCd ");

			if (!"".equalsIgnoreCase(berthNo))
				queryBuf.append(" and br.berth_nbr = :pBerthNo ");

			if (!"".equalsIgnoreCase(oprType))
				queryBuf.append(" and co.opr_type = :pOprType ");

			if (hatchNo != null)
				queryBuf.append(" and co.hatch_nbr = :pHatchNo ");

			if (!"".equalsIgnoreCase(sortBy.trim())) {
				queryBuf.append(" ORDER BY ");
				queryBuf.append(sortBy.trim());
			}

			log.info("SQL::" + queryBuf.toString());

			paramMap.put("pCatCd", ConstantUtil.MISCTYPECD_OPERATION_TYPE);
			paramMap.put("oprTypeDischarge", ConstantUtil.TALLYSHEET_OPRTYPE_Discharge);
			paramMap.put("oprTypeLoad", ConstantUtil.TALLYSHEET_OPRTYPE_Load);

			if (!"JP".equalsIgnoreCase(CustCode.trim()) && (!"".equalsIgnoreCase(CustCode.trim())))
				paramMap.put("pCustCd", CustCode.trim());
			if (!"".equalsIgnoreCase(vvCd.trim())) {
				paramMap.put("pVvCd", vvCd.trim());
			}

			if (!"".equalsIgnoreCase(berthNo))
				paramMap.put("pBerthNo", berthNo.trim());

			if (!"".equalsIgnoreCase(oprType))
				paramMap.put("pOprType", oprType.trim());

			if (hatchNo != null)
				paramMap.put("pHatchNo", hatchNo.intValue());

			log.info(": getCargoTallySheet  SQL " + queryBuf.toString() + " paramMap " + paramMap.toString());

			sql = queryBuf.toString();

			if (!needAllData) {

				if (criteria.isPaginated()) {
					sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());
				} else {
					sql = queryBuf.toString();
				}

			}

			List<GbccCargoTallysheetVO> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<GbccCargoTallysheetVO>(GbccCargoTallysheetVO.class));

			if (lst.isEmpty())
				return returnList;
			GbccCargoTallysheetVO oVO = null;
			GbccCargoTallysheetId id = null;
			for (GbccCargoTallysheetVO gbccCargVO : lst) {
				id = new GbccCargoTallysheetId();
				oVO = new GbccCargoTallysheetVO();
				oVO = gbccCargVO;
				id.setVvCd(oVO.getVvCd());
				id.setOprType(oVO.getOprType());
				id.setCreateDttm(oVO.getCreateDttm());
				id.setHatchNbr(oVO.getHatchNbr());
				oVO.setId(id);
				oVO.setStevedoreCompanyName((String) oVO.getStev_co_nm());
				oVO.setAgentName((String) oVO.getCo_nm());

				if (oVO.getId().getOprType().equalsIgnoreCase(ConstantUtil.TALLYSHEET_OPRTYPE_Discharge)) {
					oVO.setTotQtyDiscCompleted((Integer) oVO.getTotal_qty());
					oVO.setTotDiscCompleted((BigDecimal) oVO.getTotal_ton());

					oVO.setTotQtyLoadCompleted(new Integer(0));
					oVO.setTotLoadCompleted(new BigDecimal(0));
				} else {
					oVO.setTotQtyLoadCompleted((Integer) oVO.getTotal_qty());
					oVO.setTotLoadCompleted((BigDecimal) oVO.getTotal_ton());

					oVO.setTotQtyDiscCompleted(new Integer(0));
					oVO.setTotDiscCompleted(new BigDecimal(0));
				}

				oVO.setTotPkgsDiscOpenBal((Integer) oVO.getTot_pkgs_disc_openbal());
				oVO.setTotDiscOpenBal((BigDecimal) oVO.getTot_disc_openbal());

				oVO.setTotPkgsLoadOpenBal((Integer) oVO.getTot_pkgs_load_openbal());
				oVO.setTotLoadOpenBal((BigDecimal) oVO.getTot_load_openbal());

				oVO.setTotQtyDiscTotalCompleted((Integer) oVO.getTot_completed_qty_disc());
				oVO.setTotDiscTotalCompleted((BigDecimal) oVO.getTot_completed_disc());
				oVO.setTotQtyLoadTotalCompleted((Integer) oVO.getTot_completed_qty_load());
				oVO.setTotLoadTotalCompleted((BigDecimal) oVO.getTot_completed_load());

				returnList.add(oVO);
			}

			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate
						.queryForObject("SELECT COUNT(*) FROM (" + queryBuf.toString() + ")", paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());

				oVO = new GbccCargoTallysheetVO();
				oVO.setTotal(tableData.getTotal());
				returnList.add(oVO);
			}

			log.info("getCargoTallySheet Result" + returnList.toString());

		} catch (Exception e) {
			log.info("Exception getCargoTallySheet : ", e);
		} finally {
			log.info("END: DAO getCargoTallySheet");
		}
		return returnList;
	}

	@Override
	public GbccCargoTimesheetVO getCargoTimeSheetById(String vvCd, String stevCoCd, Date crDttm)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		GbccCargoTimesheetVO oVO = null;
		GbccCargoTimesheetId id = null;
		try {
			log.info("START: getCargoTimesheetById  DAO  vvCd: " + vvCd + " stevCoCd: " + stevCoCd + " crDttm: "
					+ crDttm);
			StringBuffer queryBuf = new StringBuffer();
			queryBuf.append("SELECT ");
			queryBuf.append(
					" co.vv_cd vvCd, co.stev_co_cd stev_CoCd, co.CHECKER_NM, co.CHECKER_HP, co.WORK_COMMENCE_DTTM workCommenceDttm, co.WORK_COMPLETE_DTTM workCompleteDttm, ");
			queryBuf.append(
					" co.SUBMITTED_DTTM submittedDttm, co.LAST_MODIFY_USER_ID lastModifyUserId, co.LAST_MODIFY_DTTM lastModifyDttm, co.CREATE_DTTM createDttm, ");

			queryBuf.append(
					"	vc.VV_CD vv_Cd, vc.VSL_NM, vc.IN_VOY_NBR, vc.OUT_VOY_NBR, vc.VV_STATUS_IND, vc.SHPG_SVC_CD, vc.SHPG_ROUTE_NBR,  ");
			queryBuf.append(
					"	vc.ROUTE_NM, vc.VSL_OPR_CD, vc.BERTH_APPL_DTTM, vc.VSL_BERTH_DTTM, vc.VSL_ETD_DTTM, vc.VV_CLOSE_DTTM, ");
			queryBuf.append(
					"	vc.PORT_FR, vc.PORT_TO, vc.ARRIVAL_DRAFT, vc.DEPARTURE_DRAFT, vc.BERTH_SIDE_IND, vc.CONTACT_NM, ");
			queryBuf.append(
					"	vc.CONTACT_HOME_TEL, vc.CONTACT_OFF_TEL, vc.CONTACT_PGR, vc.CONTACT_FAX, vc.CNTR_DISC, vc.CNTR_LOAD, vc.UC_LOAD, vc.UC_DISC, vc.COB_DTTM, vc.BERTH_APPL_REM, ");
			queryBuf.append(
					"	vc.BILL_MARINE_IND, vc.BILL_STEV_IND, vc.BILL_OTHER_IND, vc.BILL_ADMIN_IND, vc.CREATE_USER_ID, vc.CREATE_ACCT_NBR, vc.LAST_MODIFY_USER_ID vsl_lastModifyUserId, vc.LAST_MODIFY_DTTM vsl_lastModifyDttm, ");
			queryBuf.append(
					"	vc.SCHEME, vc.TERMINAL, vc.VSL_LOA, vc.LOC_FR, vc.LOC_TO, vc.ALONGSIDE_DRAFT, vc.VSL_UNDER_TOW_IND, vc.CONTACT2_NM, ");
			queryBuf.append(
					"	vc.CONTACT2_HOME_TEL, vc.CONTACT2_OFF_TEL, vc.CONTACT2_PGR, vc.CONTACT2_FAX, vc.TOTAL_CARGO_ONBOARD, vc.LOAD_DISPLACEMENT, vc.DG_CARGO_IND, vc.HLIFT, ");
			queryBuf.append(
					"	vc.HLIFT_OVERWHARF, vc.HLIFT_OVERSIDE, vc.MOBILE_CR_DWT, vc.MOBILE_CR_SWL, vc.CNTR_VSL_IND, vc.GB_CLOSE_VSL_IND, vc.GB_CLOSE_BJ_IND, vc.GB_CLOSE_SHP_IND, ");
			queryBuf.append(
					"	vc.CARGO_MODE, vc.BERTH_ALLOC_REM, vc.STORAGE_SPACE_IND, vc.BILL_ACCT_NBR, vc.GB_ARRIVAL_WAIVER_CD, vc.GB_ARRIVAL_WAIVER_REASON, vc.GB_DEPARTURE_WAIVER_CD, vc.GB_DEPARTURE_WAIVER_REASON, ");
			queryBuf.append(
					"	vc.GB_BERT_BILL_IND, vc.BRIDGE_DIST_FROM_BOW, vc.VACATE_BERTH_IND, vc.MIXED_SCHEME_IND, vc.DISC_CM_CD, vc.LOAD_CM_CD, vc.DISC_BER_REM, vc.LOAD_BER_REM, ");
			queryBuf.append(
					"	vc.CRG_DET_PROC, vc.DECLARANT_CUST_CD, vc.UC_DISC_CM_CD, vc.UC_LOAD_CM_CD, vc.UC_DISC_BER_REM, vc.UC_LOAD_BER_REM, vc.EST_LONG_CR_MOVE_NBR, vc.EST_THROUGHPUT_NBR, ");
			queryBuf.append(
					"	vc.SENT_TO_PSA_IND, vc.ADVICE_DTTM, vc.ABBR_IN_VOY_NBR, vc.ABBR_OUT_VOY_NBR, vc.INCENTIVE_CLASS, vc.ISPS_LEVEL, vc.BILL_OPEN_TS_IND, vc.SMS_ALERT_REP1_IND, ");
			queryBuf.append(
					"	vc.SMS_ALERT_REP2_IND, vc.TANDEM_LIFT_IND tandemLiftInd, vc.BILL_PROD_SURCHRG_IND, vc.ALLOC_PROD_PRD, vc.REEFER_PARTY, vc.PROTRUSION_IND, vc.FLOAT_CRANE_IND, vc.FIRST_SCHEME, ");
			queryBuf.append(
					"	vc.GB_CLOSE_LCT_IND, vc.GB_CLOSE_LCT_DTTM, vc.GB_CLOSE_LCT_USER_ID, vc.STORAGE_DET_PROC, vc.GB_ARRIVAL_WAIVER_IND, vc.GB_ARRIVAL_WAIVER_AMOUNT, vc.GB_AUTO_CLOSE_BJ_IND, vc.GB_AUTO_CLOSE_SHP_IND, ");
			queryBuf.append(
					"	vc.LAST_ATU_DTTM, vc.CARGO_WT, vc.TOTAL_CARGO_WT_MOBILE_CR_DWT, vc.LCT_MIN_CHRG_IND, vc.CEMENT_VSL_IND, vc.BTR_AFT_CLOSING1_DTTM, vc.NOM_IND, vc.NOM_PROCESS_IND, ");
			queryBuf.append(
					"	vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND, vc.PRINCIPAL_CARRIER_CD, vc.USE_SHP_CRN_IND, ");

			queryBuf.append(
					"	vs.VV_CD stev_VvCd, vs.STEV_CO_CD stevCoCd, vs.STEV_CONTACT, vs.STEV_REMARKS, vs.STEV_REP, vs.LINENO, vs.LAST_MODIFY_USER_ID gbccView_lastModifyUserId, ");

			queryBuf.append(
					"	br.VV_CD berth_VvCd, br.SHIFT_IND, br.ETB_DTTM, br.ETU_DTTM, br.ATB_DTTM, br.ATU_DTTM, br.FIRST_DISC_DTTM, br.FIRST_LOAD_DTTM, br.COD_DTTM, br.COL_DTTM,  ");
			queryBuf.append(
					"	br.BERTH_NBR, br.WHARF_MARK_FR, br.WHARF_MARK_TO, br.WHARF_SIDE_IND, br.LAST_MODIFY_USER_ID berth_lastModifyUserId, br.LAST_MODIFY_DTTM berth_lastModifyDttm, br.GB_COD_DTTM, br.GB_COL_DTTM, br.GB_FIRST_ACT_DTTM, br.GB_LAST_ACT_DTTM,  ");
			queryBuf.append(
					"	br.GB_BCOD_DTTM, br.GB_BCOL_DTTM, br.HAUL_DIST, br.HAUL_DIRN, br.GANG_NBR, br.HATCH_NBR, br.DELAY_RSN_CD, br.REMARKS berthRemarks, br.TOT_GEN_CARGO_ACT, br.GB_FIRST_CARGO_ACT_DTTM, br.DEP_DRAFT_FORTH,  ");
			queryBuf.append(
					"	br.GB_FIRST_DISC_DTTM, br.GB_FIRST_LOAD_DTTM, br.ACT_DRAFT_FORTH, br.ACT_DRAFT_AFT, br.ACT_WHARF_SIDE_IND, br.ACT_WHARF_MARK_FR, br.ACT_WHARF_MARK_TO, br.BERTH_REMARKS, br.COL_MODIFY_DTTM, br.DEP_DRAFT_AFT,  ");

			queryBuf.append("	sc.stev_co_nm stev_co_nm, ");
			queryBuf.append("	cc.co_nm , ");
			queryBuf.append("	nvl(bal.tot_disc_ton, 0) tot_disc_openbal , ");
			queryBuf.append("	nvl(bal.tot_load_ton, 0) tot_load_openbal , ");
			queryBuf.append("	nvl(opr.total_completed_disc, 0) tot_completed_disc , ");
			queryBuf.append("	nvl(opr.total_completed_load, 0) tot_completed_load ");
			queryBuf.append("FROM ");
			queryBuf.append("	TOPS.Vessel_Call vc ");
			queryBuf.append("INNER JOIN Company_Code cc ON ");
			queryBuf.append("	(vc.vsl_opr_cd = cc.co_cd) ");
			queryBuf.append("INNER JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd1 stev_co_cd, ");
			queryBuf.append("		v.stev_contact1 stev_contact, ");
			queryBuf.append("		v.stev_remarks1 stev_remarks, ");
			queryBuf.append("		v.stev_rep1 stev_rep, ");
			queryBuf.append("		1 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd1 IS NOT NULL ");
			queryBuf.append("UNION ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd2 stev_co_cd, ");
			queryBuf.append("		v.stev_contact2 stev_contact, ");
			queryBuf.append("		v.stev_remarks2 stev_remarks, ");
			queryBuf.append("		v.stev_rep2 stev_rep, ");
			queryBuf.append("		2 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd2 IS NOT NULL ");
			queryBuf.append("UNION ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd3 stev_co_cd, ");
			queryBuf.append("		v.stev_contact3 stev_contact, ");
			queryBuf.append("		v.stev_remarks3 stev_remarks, ");
			queryBuf.append("		v.stev_rep3 stev_rep, ");
			queryBuf.append("		3 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd3 IS NOT NULL ) vs ON ");
			queryBuf.append("	(vc.vv_cd = vs.vv_cd) ");
			queryBuf.append("INNER JOIN stevedore_company sc ON ");
			queryBuf.append("	(vs.stev_co_cd = sc.stev_co_cd) ");
			queryBuf.append("INNER JOIN Berthing br ON ");
			queryBuf.append("	(vc.vv_cd = br.vv_cd ");
			queryBuf.append("	AND br.shift_ind = 1) ");
			queryBuf.append("LEFT JOIN gbcc_cargo_open_bal bal ON ");
			queryBuf.append("	(vc.vv_cd = bal.vv_cd ");
			queryBuf.append("	AND vs.stev_co_cd = bal.stev_co_cd) ");
			queryBuf.append("LEFT JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		opr.vv_cd, ");
			queryBuf.append("		opr.stev_co_cd, ");
			queryBuf.append("		sum(det.disc_completed_ton) total_completed_disc, ");
			queryBuf.append("		sum(det.load_completed_ton) total_completed_load ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBCC.gbcc_cargo_opr opr ");
			queryBuf.append("	INNER JOIN gbcc_cargo_opr_det det ON ");
			queryBuf.append("		(opr.vv_cd = det.vv_cd ");
			queryBuf.append("		AND opr.stev_co_cd = det.stev_co_cd ");
			queryBuf.append("		AND opr.create_dttm = det.create_dttm) ");
			queryBuf.append("	GROUP BY ");
			queryBuf.append("		opr.vv_cd, ");
			queryBuf.append("		opr.stev_co_cd ) opr ON ");
			queryBuf.append("	(vc.vv_cd = opr.vv_cd ");
			queryBuf.append("	AND vs.stev_co_cd = opr.stev_co_cd) ");
			queryBuf.append("LEFT JOIN Gbcc_Cargo_Timesheet co ON ");
			queryBuf.append("	(co.vv_cd = vc.vv_cd ");
			queryBuf.append("	AND co.stev_co_cd = vs.stev_co_cd ) ");
			queryBuf.append("WHERE ");
			queryBuf.append("	vc.vv_cd = :pVvCd ");
			queryBuf.append("	AND vs.stev_co_cd = :pStevCoCd");

			if (crDttm != null)
				queryBuf.append(" AND co.create_dttm = :pCrDttm");

			log.info("SQL::" + queryBuf.toString());

			paramMap.put("pVvCd", vvCd.trim());
			paramMap.put("pStevCoCd", stevCoCd.trim());

			if (crDttm != null) {
				Timestamp ts = new Timestamp(crDttm.getTime());
				paramMap.put("pCrDttm", ts);
			}

			log.info(": getCargoTimesheetById  SQL " + queryBuf.toString() + " paramMap " + paramMap.toString());

			List<GbccCargoTimesheetVO> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<GbccCargoTimesheetVO>(GbccCargoTimesheetVO.class));

			if (lst != null) {

				for (GbccCargoTimesheetVO gbccCargoVO : lst) {
					oVO = new GbccCargoTimesheetVO();
					id = new GbccCargoTimesheetId();
					oVO = gbccCargoVO;
					id.setVvCd(oVO.getVvCd());
					id.setCreateDttm(oVO.getCreateDttm());
					oVO.setId(id);
					if (oVO.getCreateDttm() != null) {
						oVO.setCargoTimesheetActVO(getCargoTimeSheetAct(vvCd, oVO.getId().getCreateDttm()));
						oVO.setCargoTimesheetEqRentalVO(getCargoTimeSheetEqRental(vvCd, oVO.getId().getCreateDttm()));
					}
					oVO.setStevedoreCompanyName((String) oVO.getStev_co_nm());
					oVO.setAgentName((String) oVO.getCo_nm());
					oVO.setTotDiscOpenBal((Integer) oVO.getTot_disc_openbal());
					oVO.setTotLoadOpenBal((Integer) oVO.getTot_load_openbal());
					oVO.setTotDiscTotalCompleted((Integer) oVO.getTot_completed_disc());
					oVO.setTotLoadTotalCompleted((Integer) oVO.getTot_completed_load());
					oVO.setLastBerthingVO(getLastBerthing(vvCd));
				}

				log.info("getCargoTimesheetById Result" + oVO.toString());
			}

		} catch (Exception e) {
			log.info("getCargoTimesheetById Exception: ", e);
		} finally {
			log.info("END: DAO getCargoTimesheetById");
		}
		return oVO;
	}

	// sg.com.ntc.gbcc.hibernate.dao.impl--->GBCCCargoHibernateDaoImpl
	private List<GbccCargoTimesheetAct> getCargoTimeSheetAct(String vvCd, Date crDttm) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<GbccCargoTimesheetAct> returnList = new ArrayList<GbccCargoTimesheetAct>();
		try {
			log.info("START: getCargoTimeSheetAct  DAO  vvCd: " + vvCd + " crDttm: " + crDttm);
			StringBuffer queryBuf = new StringBuffer();

			queryBuf.append("SELECT ");
			queryBuf.append("	co.* , ");
			queryBuf.append("	mtc1.misc_type_nm weatherName , ");
			queryBuf.append("	mtc2.misc_type_nm activityName ");
			queryBuf.append("FROM ");
			queryBuf.append("	GBCC.GBCC_Cargo_Timesheet_Act co ");
			queryBuf.append("LEFT JOIN Misc_Type_Code mtc1 ON ");
			queryBuf.append("	(co.weather_cd = mtc1.misc_type_cd ");
			queryBuf.append("	AND mtc1.cat_cd = :pCatCd1 ) ");
			queryBuf.append("LEFT JOIN Misc_Type_Code mtc2 ON ");
			queryBuf.append("	(co.activity_cd = mtc2.misc_type_cd ");
			queryBuf.append("	AND mtc2.cat_cd = :pCatCd2 ) ");
			queryBuf.append("WHERE ");
			queryBuf.append("	co.vv_cd = :pVvCd ");
			queryBuf.append("	AND co.create_dttm = :pCrDttm ");
			queryBuf.append("ORDER BY ");
			queryBuf.append("	co.rec_seq_nbr");

			paramMap.put("pCatCd1", ConstantUtil.MISCTYPECD_WEATHER);
			paramMap.put("pCatCd2", ConstantUtil.MISCTYPECD_ACTIVITY);

			paramMap.put("pVvCd", vvCd.trim());

			Timestamp crTs = new Timestamp(crDttm.getTime());
			paramMap.put("pCrDttm", crTs);

			log.info(": getCargoTimeSheetAct  SQL " + queryBuf.toString() + " paramMap " + paramMap.toString());

			List<GbccCargoTimesheetAct> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<GbccCargoTimesheetAct>(GbccCargoTimesheetAct.class));
			GbccCargoTimesheetAct oVO = null;
			GbccCargoTimesheetActId id = null;

			if (lst != null) {
				for (GbccCargoTimesheetAct gbccCargoAct : lst) {
					id = new GbccCargoTimesheetActId();
					oVO = new GbccCargoTimesheetAct();
					oVO = gbccCargoAct;
					id.setVvCd(oVO.getVvCd());
					id.setCreateDttm(oVO.getCreateDttm());
					id.setRecSeqNbr(oVO.getRecSeqNbr());
					oVO.setId(id);

					returnList.add(oVO);
				}
			}
			log.info("getCargoTimeSheetAct Result" + returnList.toString());
		} catch (Exception e) {
			log.info("getCargoTimesheetAct Exception: ", e);
		} finally {
			log.info("END: DAO getCargoTimeSheetAct");
		}
		return returnList;
	}

	private List<GbccCargoTimesheetEqrental> getCargoTimeSheetEqRental(String vvCd, Date crDttm) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<GbccCargoTimesheetEqrental> returnList = new ArrayList<GbccCargoTimesheetEqrental>();
		try {
			log.info("START: getCargoTimeSheetEqRental  DAO  vvCd: " + vvCd + " crDttm: " + crDttm);

			StringBuffer queryBuf = new StringBuffer();
			queryBuf.append("SELECT ");
			queryBuf.append("	co.* , ");
			queryBuf.append("	mtc1.misc_type_nm eqTypeName ");
			queryBuf.append("FROM ");
			queryBuf.append("	GBCC.GBCC_Cargo_Timesheet_EqRental co ");
			queryBuf.append("LEFT JOIN Misc_Type_Code mtc1 ON ");
			queryBuf.append("	(co.eq_type_cd = mtc1.misc_type_cd ");
			queryBuf.append("	AND mtc1.cat_cd = :pCatCd1 ) ");
			queryBuf.append("WHERE ");
			queryBuf.append("	co.vv_cd = :pVvCd ");
			queryBuf.append("	AND co.create_dttm = :pCrDttm ");
			queryBuf.append("ORDER BY ");
			queryBuf.append("	co.rec_seq_nbr");

			log.info("SQL::" + queryBuf.toString());

			paramMap.put("pCatCd1", ConstantUtil.MISCTYPECD_EQTYPE);

			paramMap.put("pVvCd", vvCd.trim());

			Timestamp crTs = new Timestamp(crDttm.getTime());
			paramMap.put("pCrDttm", crTs);

			log.info(": getCargoTimeSheetEqRental  SQL " + queryBuf.toString() + " paramMap " + paramMap.toString());

			List<GbccCargoTimesheetEqrental> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<GbccCargoTimesheetEqrental>(GbccCargoTimesheetEqrental.class));

			if (lst != null) {
				GbccCargoTimesheetEqrental oVO = null;
				GbccCargoTimesheetEqrentalId id = null;

				for (GbccCargoTimesheetEqrental gbccCargoEqrental : lst) {
					id = new GbccCargoTimesheetEqrentalId();
					oVO = new GbccCargoTimesheetEqrental();
					oVO = gbccCargoEqrental;
					id.setVvCd(oVO.getVvCd());
					id.setRecSeqNbr(oVO.getRecSeqNbr());
					id.setCreateDttm(oVO.getCreateDttm());
					oVO.setId(id);

					returnList.add(oVO);
				}

				log.info("getCargoTimeSheetEqRental Result" + returnList.toString());
			}

		} catch (Exception e) {
			log.info("Exception getCargoTimeSheetEqRental : ", e);
		} finally {
			log.info("END: DAO getCargoTimeSheetEqRental");
		}
		return returnList;
	}

	@Override
	public boolean saveCargoTimeSheet(GbccCargoTimesheetVO transientObject) throws BusinessException {
		boolean result;
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: saveCargoTimeSheet  DAO GbccCargoTimesheetVO: " + transientObject.toString());
			boolean isNew = false;
			Date dttm = new Date();
			transientObject.setLastModifyDttm(dttm);
			if (transientObject.getId().getCreateDttm() == null) {
				transientObject.setCreateDttm(dttm);
				transientObject.setSubmittedDttm(dttm);
				isNew = true;
				sb.append("INSERT ");
				sb.append("	INTO ");
				sb.append("	GBCC.GBCC_CARGO_TIMESHEET (VV_CD, ");
				sb.append("	CREATE_DTTM, ");
				sb.append("	STEV_CO_CD, ");
				sb.append("	CHECKER_NM, ");
				sb.append("	CHECKER_HP, ");
				sb.append("	WORK_COMMENCE_DTTM, ");
				sb.append("	WORK_COMPLETE_DTTM, ");
				sb.append("	SUBMITTED_DTTM, ");
				sb.append("	LAST_MODIFY_USER_ID, ");
				sb.append("	LAST_MODIFY_DTTM) ");
				sb.append("VALUES(:vvCd, ");
				sb.append(":createDttm, ");
				sb.append(":stevCoCd, ");
				sb.append(":checkerNm, ");
				sb.append(":checkerHp, ");
				sb.append(":workCommenceDttm, ");
				sb.append(":workCompleteDttm, ");
				sb.append(":submittedDttm, ");
				sb.append(":lastModifyUserId, ");
				sb.append("SYSDATE)");

			} else {
				dttm = transientObject.getId().getCreateDttm();
				sb = new StringBuffer();
				sb.append("UPDATE ");
				sb.append("	GBCC.GBCC_CARGO_TIMESHEET ");
				sb.append("SET ");
				sb.append("	STEV_CO_CD =:stevCoCd, ");
				sb.append("	CHECKER_NM =:checkerNm, ");
				sb.append("	CHECKER_HP =:checkerHp, ");
				sb.append("	WORK_COMMENCE_DTTM =:workCommenceDttm, ");
				sb.append("	WORK_COMPLETE_DTTM =:workCompleteDttm, ");
				sb.append("	SUBMITTED_DTTM =:submittedDttm, ");
				sb.append("	LAST_MODIFY_USER_ID =:lastModifyUserId, ");
				sb.append("	LAST_MODIFY_DTTM = SYSDATE ");
				sb.append("WHERE ");
				sb.append("	VV_CD =:vvCd ");
				sb.append("	AND CREATE_DTTM =:createDttm");
			}

			transientObject.setAuditUserId(transientObject.getLastModifyUserId());
			transientObject.setFnType(ConstantUtil.AUDIT_FNTYPE_CARGOOPS);
			transientObject.setFnsubType(ConstantUtil.AUDIT_FNSUBTYPE_CARGOTIMESHEET_UPDATE);
			transientObject.setTableNm(ConstantUtil.TABLE_GBCCCARGOTIMESHEET);
			transientObject.setKeyType1(ConstantUtil.AUDIT_KEYID_VVCD);
			transientObject.setKeyVal1(transientObject.getId().getVvCd());

			String fieldvalue = ConstantUtil.AUDIT_KEYID_VVCD + "=" + transientObject.getId().getVvCd();
			transientObject.setFieldNewValue(fieldvalue);
			transientObject.setFieldOldValue(fieldvalue);
			log.info("saveCargoTimeSheet SQL  " + sb.toString() + " GbccCargoTimesheetVO" + transientObject.toString());

			int count = namedParameterJdbcTemplate.update(sb.toString(),
					new BeanPropertySqlParameterSource(transientObject));
			if (count == 0) {
				log.info("not amended");
			}

			List<GbccCargoTimesheetAct> detlst = transientObject.getCargoTimesheetActVO();

			if (detlst != null) {
				int actSize = detlst.size();

				for (int i = 0; i < actSize; i++) {
					GbccCargoTimesheetAct detVO = (GbccCargoTimesheetAct) detlst.get(i);
					if (detVO.getObjUpdateMode().equalsIgnoreCase(ConstantUtil.OBJ_UPDATEMODE_DELETE)) {
						if (!isNew) {
							sb = new StringBuffer();
							sb.append("DELETE ");
							sb.append("FROM ");
							sb.append("	GBCC.GBCC_CARGO_TIMESHEET_ACT ");
							sb.append("WHERE ");
							sb.append("	VV_CD =:vvCd ");
							sb.append("	AND CREATE_DTTM =:createDttm ");
							sb.append("	AND REC_SEQ_NBR =:recSeqNbr");
							deleteCargoTimeSheetAct(sb.toString(), detVO.getId());
						}
					} else {

						if (detVO.getId().getCreateDttm() == null) {
							Long nextSeq = getNextSeqNbr(ConstantUtil.SEQ_TALLYSHEET_ACT);
							detVO.setRecSeqNbr(new Integer(nextSeq.intValue()));
							detVO.setCreateDttm(dttm);
							sb = new StringBuffer();
							sb.append("INSERT ");
							sb.append("	INTO ");
							sb.append("	GBCC.GBCC_CARGO_TIMESHEET_ACT (VV_CD, ");
							sb.append("	CREATE_DTTM, ");
							sb.append("	REC_SEQ_NBR, ");
							sb.append("	FROM_DTTM, ");
							sb.append("	TO_DTTM, ");
							sb.append("	HATCH_NBR, ");
							sb.append("	WEATHER_CD, ");
							sb.append("	ACTIVITY_CD, ");
							sb.append("	REMARKS, ");
							sb.append("	LAST_MODIFY_USER_ID, ");
							sb.append("	LAST_MODIFY_DTTM, ");
							sb.append("	OPR_TYPE, ");
							sb.append("	CARGO_DTTM) ");
							sb.append("VALUES(:vvCd, ");
							sb.append(":createDttm, ");
							sb.append(":recSeqNbr, ");
							sb.append(":fromDttm, ");
							sb.append(":toDttm, ");
							sb.append(":hatchNbr, ");
							sb.append(":weatherCd, ");
							sb.append(":activityCd, ");
							sb.append(":remarks, ");
							sb.append(":lastModifyUserId, ");
							sb.append("SYSDATE, ");
							sb.append(":oprType, ");
							sb.append(":cargoDttm)");
						} else {
							sb = new StringBuffer();
							sb.append("UPDATE ");
							sb.append("	GBCC.GBCC_CARGO_TIMESHEET_ACT ");
							sb.append("SET ");
							sb.append("	FROM_DTTM =:fromDttm, ");
							sb.append("	TO_DTTM =:toDttm, ");
							sb.append("	HATCH_NBR =:hatchNbr, ");
							sb.append("	WEATHER_CD =:weatherCd, ");
							sb.append("	ACTIVITY_CD =:activityCd, ");
							sb.append("	REMARKS =:remarks, ");
							sb.append("	LAST_MODIFY_USER_ID =:remarks, ");
							sb.append("	LAST_MODIFY_DTTM =SYSDATE, ");
							sb.append("	OPR_TYPE =:oprType, ");
							sb.append("	CARGO_DTTM =:cargoDttm ");
							sb.append("WHERE ");
							sb.append("	VV_CD =:vvCd ");
							sb.append("	AND CREATE_DTTM =:createDttm ");
							sb.append("	AND REC_SEQ_NBR =:recSeqNbr");
						}
						detVO.setVvCd(detVO.getId().getVvCd());
						saveCargoTimeSheetAct(sb.toString(), detVO);
					}
				}
			}

			List<GbccCargoTimesheetEqrental> detlstEq = transientObject.getCargoTimesheetEqRentalVO();

			if (detlstEq != null) {
				int eqSize = detlstEq.size();

				for (int i = 0; i < eqSize; i++) {
					GbccCargoTimesheetEqrental detVO = (GbccCargoTimesheetEqrental) detlstEq.get(i);
					if (detVO.getObjUpdateMode().equalsIgnoreCase(ConstantUtil.OBJ_UPDATEMODE_DELETE)) {
						if (!isNew) {
							sb = new StringBuffer();
							sb.append("DELETE ");
							sb.append("FROM ");
							sb.append("	GBCC.GBCC_CARGO_TIMESHEET_EQRENTAL ");
							sb.append("WHERE ");
							sb.append("	VV_CD =:vvCd ");
							sb.append("	AND CREATE_DTTM =:createDttm ");
							sb.append("	AND REC_SEQ_NBR =:recSeqNbr");
							deleteCargoTimeSheetEqRental(sb.toString(), detVO.getId());
						}
					} else {
						sb = new StringBuffer();

						if (detVO.getId().getCreateDttm() == null) {
							Long nextSeq = getNextSeqNbr(ConstantUtil.SEQ_TALLYSHEET_EQRENTAL);
							detVO.setRecSeqNbr(new Integer(nextSeq.intValue()));
							detVO.setCreateDttm(dttm);

							sb.append("INSERT ");
							sb.append("	INTO ");
							sb.append("	GBCC.GBCC_CARGO_TIMESHEET_EQRENTAL (VV_CD, ");
							sb.append("	CREATE_DTTM, ");
							sb.append("	REC_SEQ_NBR, ");
							sb.append("	FROM_DTTM, ");
							sb.append("	TO_DTTM, ");
							sb.append("	EQ_TYPE_CD, ");
							sb.append("	EQ_TYPE_DESC, ");
							sb.append("	EQ_TON, ");
							sb.append("	EQ_UNIT, ");
							sb.append("	LAST_MODIFY_USER_ID, ");
							sb.append("	LAST_MODIFY_DTTM) ");
							sb.append("VALUES(:vvCd, ");
							sb.append(":createDttm, ");
							sb.append(":recSeqNbr, ");
							sb.append(":fromDttm, ");
							sb.append(":toDttm, ");
							sb.append(":eqTypeCd, ");
							sb.append(":eqTypeDesc, ");
							sb.append(":eqTon, ");
							sb.append(":eqUnit, ");
							sb.append(":lastModifyUserId, ");
							sb.append("SYSDATE)");
						} else {
							sb = new StringBuffer();
							sb.append("UPDATE ");
							sb.append("	GBCC.GBCC_CARGO_TIMESHEET_EQRENTAL ");
							sb.append("SET ");
							sb.append("	FROM_DTTM =:fromDttm, ");
							sb.append("	TO_DTTM =:toDttm, ");
							sb.append("	EQ_TYPE_CD =:eqTypeCd, ");
							sb.append("	EQ_TYPE_DESC =:eqTypeDesc, ");
							sb.append("	EQ_TON =:eqTon, ");
							sb.append("	EQ_UNIT =:eqUnit, ");
							sb.append("	LAST_MODIFY_USER_ID =:lastModifyUserId, ");
							sb.append("	LAST_MODIFY_DTTM = SYSDATE ");
							sb.append("WHERE ");
							sb.append("	VV_CD =:vvCd ");
							sb.append("	AND CREATE_DTTM =:createDttm ");
							sb.append("	AND REC_SEQ_NBR =:recSeqNbr");
						}
						detVO.setVvCd(detVO.getId().getVvCd());
						saveCargoTimeSheetEqRental(sb.toString(), detVO);
					}
				}
			}

			result = true;
			log.info("saveCargoTimeSheet Result: " + result);
		} catch (BusinessException e) {
			result = false;
			log.info("Exception saveCargoTimeSheet : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			result = false;
			log.info("Exception saveCargoTimeSheet : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO saveCargoTimeSheet");
		}
		return result;
	}

	public void deleteCargoTimeSheetAct(String query, GbccCargoTimesheetActId id) throws BusinessException {
		try {
			log.info("START: deleteCargoTimeSheetAct  DAO GbccCargoTimesheetActId" + id.toString());
			GbccCargoTimesheetAct c = getCargoTimeSheetActById(id);
			if (c != null) {
				c.setAuditUserId(c.getLastModifyUserId());
				c.setFnType(ConstantUtil.AUDIT_FNTYPE_CARGOOPS);
				c.setFnsubType(ConstantUtil.AUDIT_FNSUBTYPE_CARGOTIMESHEETACT_DELETE);
				c.setTableNm(ConstantUtil.TABLE_GBCCCARGOTIMESHEETACT);
				c.setKeyType1(ConstantUtil.AUDIT_KEYID_VVCD);
				c.setKeyVal1(c.getId().getVvCd());

				String fieldvalue = ConstantUtil.AUDIT_KEYID_VVCD + "=" + c.getId().getVvCd();
				c.setFieldNewValue(fieldvalue);
				c.setFieldOldValue(fieldvalue);
				log.info("deleteCargoTimeSheetAct SQL  " + query + " GbccCargoTimesheetActId" + c.toString());

				int count = namedParameterJdbcTemplate.update(query, new BeanPropertySqlParameterSource(c));
				if (count == 0) {
					log.info("not deleted");
				}
				log.info("deleteCargoTimeSheetAct Result: " + count);
			}
		} catch (Exception e) {
			log.info("Exception deleteCargoTimeSheetAct : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO deleteCargoTimeSheetAct");
		}
	}

	private GbccCargoTimesheetAct getCargoTimeSheetActById(GbccCargoTimesheetActId acctId) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		GbccCargoTimesheetAct oVO = null;
		try {
			log.info("START: getCargoTimeSheetActById  DAO  GbccCargoTimesheetActId: " + acctId.toString());
			StringBuffer queryBuf = new StringBuffer();

			queryBuf.append("SELECT ");
			queryBuf.append("	co.* , ");
			queryBuf.append("	mtc1.misc_type_nm weatherName , ");
			queryBuf.append("	mtc2.misc_type_nm activityName ");
			queryBuf.append("FROM ");
			queryBuf.append("	GBCC.GBCC_Cargo_Timesheet_Act co ");
			queryBuf.append("LEFT JOIN Misc_Type_Code mtc1 ON ");
			queryBuf.append("	(co.weather_cd = mtc1.misc_type_cd ");
			queryBuf.append("	AND mtc1.cat_cd = :pCatCd1 ) ");
			queryBuf.append("LEFT JOIN Misc_Type_Code mtc2 ON ");
			queryBuf.append("	(co.activity_cd = mtc2.misc_type_cd ");
			queryBuf.append("	AND mtc2.cat_cd = :pCatCd2 ) ");
			queryBuf.append("WHERE ");
			queryBuf.append("	co.vv_cd = :pVvCd ");
			queryBuf.append("	AND co.create_dttm = :pCrDttm AND REC_SEQ_NBR = :reseq ");
			queryBuf.append("ORDER BY ");
			queryBuf.append("	co.rec_seq_nbr");

			paramMap.put("pCatCd1", ConstantUtil.MISCTYPECD_WEATHER);
			paramMap.put("pCatCd2", ConstantUtil.MISCTYPECD_ACTIVITY);
			paramMap.put("reseq", acctId.getRecSeqNbr());
			paramMap.put("pVvCd", acctId.getVvCd().trim());
			Timestamp crTs = new Timestamp(acctId.getCreateDttm().getTime());
			paramMap.put("pCrDttm", crTs);

			log.info(": getCargoTimeSheetActById  SQL " + queryBuf.toString() + " paramMap " + paramMap.toString());

			List<GbccCargoTimesheetAct> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<GbccCargoTimesheetAct>(GbccCargoTimesheetAct.class));

			GbccCargoTimesheetActId id = null;

			if (lst != null) {
				for (GbccCargoTimesheetAct gbccCargoAct : lst) {
					id = new GbccCargoTimesheetActId();
					oVO = new GbccCargoTimesheetAct();
					oVO = gbccCargoAct;
					id.setVvCd(oVO.getVvCd());
					id.setCreateDttm(oVO.getCreateDttm());
					id.setRecSeqNbr(oVO.getRecSeqNbr());
					oVO.setId(id);
				}
			}
			log.info("getCargoTimeSheetActById Result" + oVO.toString());
		} catch (Exception e) {
			log.info("Exception getCargoTimeSheetActById : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getCargoTimeSheetActById");
		}
		return oVO;
	}

	public Long getNextSeqNbr(String seqName) throws BusinessException {
		Long nextId = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer queryBuf = new StringBuffer();
		try {
			log.info("getNextSeqNbr Sequence Name: " + seqName);
			queryBuf.append(" SELECT " + seqName + ".NEXTVAL FROM DUAL ");
			log.info("getNextSeqNbr Sql: " + queryBuf.toString());
			nextId = namedParameterJdbcTemplate.queryForObject(queryBuf.toString(), paramMap, Long.class);
			log.info("Sequence NO" + nextId);
			log.info("getNextSeqNbr Result: " + nextId);
		} catch (Exception e) {
			log.info("Exception getNextSeqNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getNextSeqNbr");
		}
		return nextId;
	}

	public void saveCargoTimeSheetAct(String query, GbccCargoTimesheetAct transientObject) throws BusinessException {
		try {
			log.info("START: saveCargoTimeSheet  DAO GbccCargoTimesheetAct: " + transientObject.toString());

			Date dttm = new Date();
			transientObject.setLastModifyDttm(dttm);

			transientObject.setAuditUserId(transientObject.getLastModifyUserId());
			transientObject.setFnType(ConstantUtil.AUDIT_FNTYPE_CARGOOPS);
			transientObject.setFnsubType(ConstantUtil.AUDIT_FNSUBTYPE_CARGOTIMESHEETACT_UPDATE);
			transientObject.setTableNm(ConstantUtil.TABLE_GBCCCARGOTIMESHEETACT);
			transientObject.setKeyType1(ConstantUtil.AUDIT_KEYID_VVCD);
			transientObject.setKeyVal1(transientObject.getId().getVvCd());

			String fieldvalue = ConstantUtil.AUDIT_KEYID_VVCD + "=" + transientObject.getId().getVvCd();
			transientObject.setFieldNewValue(fieldvalue);
			transientObject.setFieldOldValue(fieldvalue);
			log.info("saveCargoTimeSheetAct SQL  " + query + " GbccCargoTimesheetAct" + transientObject.toString());

			int count = namedParameterJdbcTemplate.update(query, new BeanPropertySqlParameterSource(transientObject));
			if (count == 0) {
				log.info("not amended");
			}
			log.info("saveCargoTimeSheetAct Result: " + count);
		} catch (Exception e) {
			log.info("Exception saveCargoTimeSheetAct : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO saveCargoTimeSheetAct");
		}

	}

	public void deleteCargoTimeSheetEqRental(String query, GbccCargoTimesheetEqrentalId id) throws BusinessException {
		try {
			log.info("START: deleteCargoTimeSheetEqRental  DAO GbccCargoTimesheetEqrentalId: " + id.toString());
			GbccCargoTimesheetEqrental c = getCargoTimeSheetEqRentalById(id);
			if (c != null) {

				c.setAuditUserId(c.getLastModifyUserId());
				c.setFnType(ConstantUtil.AUDIT_FNTYPE_CARGOOPS);
				c.setFnsubType(ConstantUtil.AUDIT_FNSUBTYPE_CARGOTIMESHEETEQRENTAL_DELETE);
				c.setTableNm(ConstantUtil.TABLE_GBCCCARGOTALLYSHEETEQRENTAL);
				c.setKeyType1(ConstantUtil.AUDIT_KEYID_VVCD);
				c.setKeyVal1(c.getId().getVvCd());

				String fieldvalue = ConstantUtil.AUDIT_KEYID_VVCD + "=" + c.getId().getVvCd();
				c.setFieldNewValue(fieldvalue);
				c.setFieldOldValue(fieldvalue);
				log.info("deleteCargoTimeSheetEqRental SQL  " + query + " GbccCargoTimesheetActId" + c.toString());

				int count = namedParameterJdbcTemplate.update(query, new BeanPropertySqlParameterSource(c));
				if (count == 0) {
					log.info("not deleted");
				}
				log.info("deleteCargoTimeSheetEqRental Result: " + count);
			}
		} catch (Exception ex) {
			log.info("Exception deleteCargoTimeSheetEqRental : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO deleteCargoTimeSheetEqRental");
		}

	}

	private GbccCargoTimesheetEqrental getCargoTimeSheetEqRentalById(GbccCargoTimesheetEqrentalId cID)
			throws BusinessException {
		GbccCargoTimesheetEqrental oVO = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getCargoTimeSheetEqRentalById  DAO  GbccCargoTimesheetEqrentalId: " + cID.toString());

			StringBuffer queryBuf = new StringBuffer();
			queryBuf.append("SELECT ");
			queryBuf.append("	co.* , ");
			queryBuf.append("	mtc1.misc_type_nm eqTypeName ");
			queryBuf.append("FROM ");
			queryBuf.append("	GBCC.GBCC_Cargo_Timesheet_EqRental co ");
			queryBuf.append("LEFT JOIN Misc_Type_Code mtc1 ON ");
			queryBuf.append("	(co.eq_type_cd = mtc1.misc_type_cd ");
			queryBuf.append("	AND mtc1.cat_cd = :pCatCd1 ) ");
			queryBuf.append("WHERE ");
			queryBuf.append("	co.vv_cd = :pVvCd ");
			queryBuf.append("	AND co.create_dttm = :pCrDttm ");
			queryBuf.append("AND REC_SEQ_NBR = :reseq ");
			queryBuf.append("ORDER BY ");
			queryBuf.append("	co.rec_seq_nbr");

			log.info("SQL::" + queryBuf.toString());

			paramMap.put("pCatCd1", ConstantUtil.MISCTYPECD_EQTYPE);
			paramMap.put("reseq", cID.getRecSeqNbr());
			paramMap.put("pVvCd", cID.getVvCd().trim());

			Timestamp crTs = new Timestamp(cID.getCreateDttm().getTime());
			paramMap.put("pCrDttm", crTs);

			log.info(
					": getCargoTimeSheetEqRentalById  SQL " + queryBuf.toString() + " paramMap " + paramMap.toString());

			List<GbccCargoTimesheetEqrental> lst = namedParameterJdbcTemplate.query(queryBuf.toString(), paramMap,
					new BeanPropertyRowMapper<GbccCargoTimesheetEqrental>(GbccCargoTimesheetEqrental.class));

			if (lst != null) {

				GbccCargoTimesheetEqrentalId id = null;

				for (GbccCargoTimesheetEqrental gbccCargoEqrental : lst) {
					id = new GbccCargoTimesheetEqrentalId();
					oVO = new GbccCargoTimesheetEqrental();
					oVO = gbccCargoEqrental;
					id.setVvCd(oVO.getVvCd());
					id.setRecSeqNbr(oVO.getRecSeqNbr());
					id.setCreateDttm(oVO.getCreateDttm());
					oVO.setId(id);

				}
				log.info("getCargoTimeSheetEqRentalById Result: " + oVO.toString());
			}

		} catch (Exception e) {
			log.info("Exception getCargoTimeSheetEqRentalById : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getCargoTimeSheetEqRentalById");
		}
		return oVO;
	}

	public void saveCargoTimeSheetEqRental(String query, GbccCargoTimesheetEqrental transientObject)
			throws BusinessException {
		try {
			log.info(
					"START: saveCargoTimeSheetEqRental  DAO GbccCargoTimesheetEqrental: " + transientObject.toString());
			Date dttm = new Date();
			transientObject.setLastModifyDttm(dttm);

			transientObject.setAuditUserId(transientObject.getLastModifyUserId());
			transientObject.setFnType(ConstantUtil.AUDIT_FNTYPE_CARGOOPS);
			transientObject.setFnsubType(ConstantUtil.AUDIT_FNSUBTYPE_CARGOTIMESHEETEQRENTAL_UPDATE);
			transientObject.setTableNm(ConstantUtil.TABLE_GBCCCARGOTALLYSHEETEQRENTAL);
			transientObject.setKeyType1(ConstantUtil.AUDIT_KEYID_VVCD);
			transientObject.setKeyVal1(transientObject.getId().getVvCd());

			String fieldvalue = ConstantUtil.AUDIT_KEYID_VVCD + "=" + transientObject.getId().getVvCd();
			transientObject.setFieldNewValue(fieldvalue);
			transientObject.setFieldOldValue(fieldvalue);

			log.info("saveCargoTimeSheetEqRental SQL  " + query + " GbccCargoTimesheetEqrental"
					+ transientObject.toString());

			int count = namedParameterJdbcTemplate.update(query, new BeanPropertySqlParameterSource(transientObject));
			if (count == 0) {
				log.info("not amended");
			}
			log.info("saveCargoTimeSheetEqRental Result: " + count);
		} catch (Exception e) {
			log.info("Exception saveCargoTimeSheetEqRental : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO saveCargoTimeSheetEqRental");
		}
	}

	@Override
	public List<VesselCall> getCargoVesselCall(String custCd) throws BusinessException {
		return getCargoOprVesselCall(custCd, "", "", false);
	}

	@Override
	public List<StevedoreCompany> getCargoStevedore() throws BusinessException {
		return getCargoOprStevedore("", false);
	}

	@Override
	public List<String> getCargoBerth(String custCd) throws BusinessException {
		return getCargoOprBerth(custCd, false);
	}

	@Override
	public List<GbccCargoTimesheetVO> getCargoTimeSheet(String CustCode, String vvCd, String stevCoCd, String berthNo,
			String sortBy, Criteria criteria, Boolean needAllData) throws BusinessException {
		List<GbccCargoTimesheetVO> returnList = new ArrayList<GbccCargoTimesheetVO>();
		Map<String, Object> paramMap = new HashMap<>();
		String sql = "";
		TableData tableData = new TableData();
		try {

			log.info("START: getCargoTimeSheet  DAO CustCode: " + CustCode + " vvCd: " + vvCd + " stevCoCd: " + stevCoCd
					+ " berthNo: " + berthNo + " sortBy: " + sortBy);
			StringBuffer queryBuf = new StringBuffer();
			queryBuf.append("SELECT ");
			queryBuf.append(
					" co.vv_cd vvCd, co.stev_co_cd stev_CoCd, co.CHECKER_NM, co.CHECKER_HP, co.WORK_COMMENCE_DTTM workCommenceDttm, co.WORK_COMPLETE_DTTM workCompleteDttm, ");
			queryBuf.append(
					" co.SUBMITTED_DTTM submittedDttm, co.LAST_MODIFY_USER_ID lastModifyUserId, co.LAST_MODIFY_DTTM lastModifyDttm, co.CREATE_DTTM createDttm, ");

			queryBuf.append(
					"	vc.VV_CD vv_Cd, vc.VSL_NM, vc.IN_VOY_NBR, vc.OUT_VOY_NBR, vc.VV_STATUS_IND, vc.SHPG_SVC_CD, vc.SHPG_ROUTE_NBR,  ");
			queryBuf.append(
					"	vc.ROUTE_NM, vc.VSL_OPR_CD, vc.BERTH_APPL_DTTM, vc.VSL_BERTH_DTTM, vc.VSL_ETD_DTTM, vc.VV_CLOSE_DTTM, ");
			queryBuf.append(
					"	vc.PORT_FR, vc.PORT_TO, vc.ARRIVAL_DRAFT, vc.DEPARTURE_DRAFT, vc.BERTH_SIDE_IND, vc.CONTACT_NM, ");
			queryBuf.append(
					"	vc.CONTACT_HOME_TEL, vc.CONTACT_OFF_TEL, vc.CONTACT_PGR, vc.CONTACT_FAX, vc.CNTR_DISC, vc.CNTR_LOAD, vc.UC_LOAD, vc.UC_DISC, vc.COB_DTTM, vc.BERTH_APPL_REM, ");
			queryBuf.append(
					"	vc.BILL_MARINE_IND, vc.BILL_STEV_IND, vc.BILL_OTHER_IND, vc.BILL_ADMIN_IND, vc.CREATE_USER_ID, vc.CREATE_ACCT_NBR, vc.LAST_MODIFY_USER_ID vsl_lastModifyUserId, vc.LAST_MODIFY_DTTM vsl_lastModifyDttm, ");
			queryBuf.append(
					"	vc.SCHEME, vc.TERMINAL, vc.VSL_LOA, vc.LOC_FR, vc.LOC_TO, vc.ALONGSIDE_DRAFT, vc.VSL_UNDER_TOW_IND, vc.CONTACT2_NM, ");
			queryBuf.append(
					"	vc.CONTACT2_HOME_TEL, vc.CONTACT2_OFF_TEL, vc.CONTACT2_PGR, vc.CONTACT2_FAX, vc.TOTAL_CARGO_ONBOARD, vc.LOAD_DISPLACEMENT, vc.DG_CARGO_IND, vc.HLIFT, ");
			queryBuf.append(
					"	vc.HLIFT_OVERWHARF, vc.HLIFT_OVERSIDE, vc.MOBILE_CR_DWT, vc.MOBILE_CR_SWL, vc.CNTR_VSL_IND, vc.GB_CLOSE_VSL_IND, vc.GB_CLOSE_BJ_IND, vc.GB_CLOSE_SHP_IND, ");
			queryBuf.append(
					"	vc.CARGO_MODE, vc.BERTH_ALLOC_REM, vc.STORAGE_SPACE_IND, vc.BILL_ACCT_NBR, vc.GB_ARRIVAL_WAIVER_CD, vc.GB_ARRIVAL_WAIVER_REASON, vc.GB_DEPARTURE_WAIVER_CD, vc.GB_DEPARTURE_WAIVER_REASON, ");
			queryBuf.append(
					"	vc.GB_BERT_BILL_IND, vc.BRIDGE_DIST_FROM_BOW, vc.VACATE_BERTH_IND, vc.MIXED_SCHEME_IND, vc.DISC_CM_CD, vc.LOAD_CM_CD, vc.DISC_BER_REM, vc.LOAD_BER_REM, ");
			queryBuf.append(
					"	vc.CRG_DET_PROC, vc.DECLARANT_CUST_CD, vc.UC_DISC_CM_CD, vc.UC_LOAD_CM_CD, vc.UC_DISC_BER_REM, vc.UC_LOAD_BER_REM, vc.EST_LONG_CR_MOVE_NBR, vc.EST_THROUGHPUT_NBR, ");
			queryBuf.append(
					"	vc.SENT_TO_PSA_IND, vc.ADVICE_DTTM, vc.ABBR_IN_VOY_NBR, vc.ABBR_OUT_VOY_NBR, vc.INCENTIVE_CLASS, vc.ISPS_LEVEL, vc.BILL_OPEN_TS_IND, vc.SMS_ALERT_REP1_IND, ");
			queryBuf.append(
					"	vc.SMS_ALERT_REP2_IND, vc.TANDEM_LIFT_IND tandemLiftInd, vc.BILL_PROD_SURCHRG_IND, vc.ALLOC_PROD_PRD, vc.REEFER_PARTY, vc.PROTRUSION_IND, vc.FLOAT_CRANE_IND, vc.FIRST_SCHEME, ");
			queryBuf.append(
					"	vc.GB_CLOSE_LCT_IND, vc.GB_CLOSE_LCT_DTTM, vc.GB_CLOSE_LCT_USER_ID, vc.STORAGE_DET_PROC, vc.GB_ARRIVAL_WAIVER_IND, vc.GB_ARRIVAL_WAIVER_AMOUNT, vc.GB_AUTO_CLOSE_BJ_IND, vc.GB_AUTO_CLOSE_SHP_IND, ");
			queryBuf.append(
					"	vc.LAST_ATU_DTTM, vc.CARGO_WT, vc.TOTAL_CARGO_WT_MOBILE_CR_DWT, vc.LCT_MIN_CHRG_IND, vc.CEMENT_VSL_IND, vc.BTR_AFT_CLOSING1_DTTM, vc.NOM_IND, vc.NOM_PROCESS_IND, ");
			queryBuf.append(
					"	vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND, vc.PRINCIPAL_CARRIER_CD, vc.USE_SHP_CRN_IND, ");

			queryBuf.append(
					"	vs.VV_CD stev_VvCd, vs.STEV_CO_CD stevCoCd, vs.STEV_CONTACT, vs.STEV_REMARKS, vs.STEV_REP, vs.LINENO, vs.LAST_MODIFY_USER_ID gbccView_lastModifyUserId, ");

			queryBuf.append(
					"	br.VV_CD berth_VvCd, br.SHIFT_IND, br.ETB_DTTM, br.ETU_DTTM, br.ATB_DTTM, br.ATU_DTTM, br.FIRST_DISC_DTTM, br.FIRST_LOAD_DTTM, br.COD_DTTM, br.COL_DTTM,  ");
			queryBuf.append(
					"	br.BERTH_NBR, br.WHARF_MARK_FR, br.WHARF_MARK_TO, br.WHARF_SIDE_IND, br.LAST_MODIFY_USER_ID berth_lastModifyUserId, br.LAST_MODIFY_DTTM berth_lastModifyDttm, br.GB_COD_DTTM, br.GB_COL_DTTM, br.GB_FIRST_ACT_DTTM, br.GB_LAST_ACT_DTTM,  ");
			queryBuf.append(
					"	br.GB_BCOD_DTTM, br.GB_BCOL_DTTM, br.HAUL_DIST, br.HAUL_DIRN, br.GANG_NBR, br.HATCH_NBR, br.DELAY_RSN_CD, br.REMARKS berthRemarks, br.TOT_GEN_CARGO_ACT, br.GB_FIRST_CARGO_ACT_DTTM, br.DEP_DRAFT_FORTH,  ");
			queryBuf.append(
					"	br.GB_FIRST_DISC_DTTM, br.GB_FIRST_LOAD_DTTM, br.ACT_DRAFT_FORTH, br.ACT_DRAFT_AFT, br.ACT_WHARF_SIDE_IND, br.ACT_WHARF_MARK_FR, br.ACT_WHARF_MARK_TO, br.BERTH_REMARKS, br.COL_MODIFY_DTTM, br.DEP_DRAFT_AFT,  ");

			queryBuf.append("	sc.stev_co_nm stev_co_nm, ");
			queryBuf.append("	cc.co_nm , ");
			queryBuf.append("	nvl(bal.tot_disc_ton, 0) tot_disc_openbal , ");
			queryBuf.append("	nvl(bal.tot_load_ton, 0) tot_load_openbal , ");
			queryBuf.append("	nvl(opr.total_completed_disc, 0) tot_completed_disc , ");
			queryBuf.append("	nvl(opr.total_completed_load, 0) tot_completed_load ");
			queryBuf.append("FROM ");
			queryBuf.append("	TOPS.Vessel_Call vc ");
			queryBuf.append("INNER JOIN Company_Code cc ON ");
			queryBuf.append("	(vc.vsl_opr_cd = cc.co_cd) ");
			queryBuf.append("INNER JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd1 stev_co_cd, ");
			queryBuf.append("		v.stev_contact1 stev_contact, ");
			queryBuf.append("		v.stev_remarks1 stev_remarks, ");
			queryBuf.append("		v.stev_rep1 stev_rep, ");
			queryBuf.append("		1 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd1 IS NOT NULL ");
			queryBuf.append("UNION ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd2 stev_co_cd, ");
			queryBuf.append("		v.stev_contact2 stev_contact, ");
			queryBuf.append("		v.stev_remarks2 stev_remarks, ");
			queryBuf.append("		v.stev_rep2 stev_rep, ");
			queryBuf.append("		2 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd2 IS NOT NULL ");
			queryBuf.append("UNION ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		vv_cd, ");
			queryBuf.append("		v.stev_co_cd3 stev_co_cd, ");
			queryBuf.append("		v.stev_contact3 stev_contact, ");
			queryBuf.append("		v.stev_remarks3 stev_remarks, ");
			queryBuf.append("		v.stev_rep3 stev_rep, ");
			queryBuf.append("		3 AS lineno, ");
			queryBuf.append("		last_modify_user_id ");
			queryBuf.append("	FROM ");
			queryBuf.append("		TOPS.vv_stevedore V ");
			queryBuf.append("	WHERE ");
			queryBuf.append("		stev_co_cd3 IS NOT NULL ) vs ON ");
			queryBuf.append("	(vc.vv_cd = vs.vv_cd) ");
			queryBuf.append("INNER JOIN stevedore_company sc ON ");
			queryBuf.append("	(vs.stev_co_cd = sc.stev_co_cd) ");
			queryBuf.append("INNER JOIN Berthing br ON ");
			queryBuf.append("	(vc.vv_cd = br.vv_cd ");
			queryBuf.append("	AND br.shift_ind = 1) ");
			queryBuf.append("LEFT JOIN gbcc_cargo_open_bal bal ON ");
			queryBuf.append("	(vc.vv_cd = bal.vv_cd ");
			queryBuf.append("	AND vs.stev_co_cd = bal.stev_co_cd) ");
			queryBuf.append("LEFT JOIN ( ");
			queryBuf.append("	SELECT ");
			queryBuf.append("		opr.vv_cd, ");
			queryBuf.append("		opr.stev_co_cd, ");
			queryBuf.append("		sum(det.disc_completed_ton) total_completed_disc, ");
			queryBuf.append("		sum(det.load_completed_ton) total_completed_load ");
			queryBuf.append("	FROM ");
			queryBuf.append("		GBCC.gbcc_cargo_opr opr ");
			queryBuf.append("	INNER JOIN gbcc_cargo_opr_det det ON ");
			queryBuf.append("		(opr.vv_cd = det.vv_cd ");
			queryBuf.append("		AND opr.stev_co_cd = det.stev_co_cd ");
			queryBuf.append("		AND opr.create_dttm = det.create_dttm) ");
			queryBuf.append("	GROUP BY ");
			queryBuf.append("		opr.vv_cd, ");
			queryBuf.append("		opr.stev_co_cd ) opr ON ");
			queryBuf.append("	(vc.vv_cd = opr.vv_cd ");
			queryBuf.append("	AND vs.stev_co_cd = opr.stev_co_cd) ");
			queryBuf.append("LEFT JOIN Gbcc_Cargo_Timesheet co ON ");
			queryBuf.append("	(co.vv_cd = vc.vv_cd ");
			queryBuf.append("	AND co.stev_co_cd = vs.stev_co_cd ) ");
			queryBuf.append("WHERE ");
			queryBuf.append("	1 = 1");

			if (!"JP".equalsIgnoreCase(CustCode.trim()) && (!"".equalsIgnoreCase(CustCode.trim()))) {
				queryBuf.append(" AND vc.vv_status_ind in (:statusBr, :statusUb) ");
				paramMap.put("statusBr", ConstantUtil.VV_STATUS_BR);
				paramMap.put("statusUb", ConstantUtil.VV_STATUS_UB);
				
				queryBuf.append(" and sc.co_cd = :pCustCd ");
			} else {
				queryBuf.append(" AND vc.vv_status_ind in (:statusBr, :statusUb) ");
				paramMap.put("statusBr", ConstantUtil.VV_STATUS_BR);
				paramMap.put("statusUb", ConstantUtil.VV_STATUS_UB);// "'" + ConstantUtil.VV_STATUS_CL + "')" +);
			}

			if (!"".equalsIgnoreCase(vvCd.trim()))
				queryBuf.append(" and vc.vv_cd = :pVvCd ");

			if (!"".equalsIgnoreCase(stevCoCd.trim()))
				queryBuf.append(" and vs.stev_co_cd = :pStevCoCd ");

			if (!"".equalsIgnoreCase(berthNo))
				queryBuf.append(" and br.berth_nbr = :pBerthNo ");

			if (!"".equalsIgnoreCase(sortBy.trim())) {
				queryBuf.append(" ORDER BY ");
				queryBuf.append(sortBy.trim());
			}

			log.info("SQL::" + queryBuf.toString());

			if (!"JP".equalsIgnoreCase(CustCode.trim()) && (!"".equalsIgnoreCase(CustCode.trim())))
				paramMap.put("pCustCd", CustCode.trim());

			if (!"".equalsIgnoreCase(vvCd.trim())) {
				paramMap.put("pVvCd", vvCd.trim());
			}

			if (!"".equalsIgnoreCase(stevCoCd.trim()))
				paramMap.put("pStevCoCd", stevCoCd.trim());

			if (!"".equalsIgnoreCase(berthNo))
				paramMap.put("pBerthNo", berthNo.trim());

			sql = queryBuf.toString();

			if (!needAllData) {

				if (criteria.isPaginated()) {
					sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());
				} else {
					sql = queryBuf.toString();
				}

			}
			log.info(": getCargoTimesheet  SQL " + queryBuf.toString() + " paramMap " + paramMap.toString());

			List<GbccCargoTimesheetVO> lst = namedParameterJdbcTemplate.query(sql, paramMap,
					new BeanPropertyRowMapper<GbccCargoTimesheetVO>(GbccCargoTimesheetVO.class));

			if (lst.isEmpty())
				return returnList;
			GbccCargoTimesheetVO oVO = null;
			GbccCargoTimesheetId id = null;
			for (GbccCargoTimesheetVO gbccCargoVO : lst) {
				id = new GbccCargoTimesheetId();
				oVO = new GbccCargoTimesheetVO();
				oVO = gbccCargoVO;
				id.setVvCd(oVO.getVvCd());
				id.setCreateDttm(oVO.getCreateDttm());

				oVO.setId(id);
				oVO.setStevedoreCompanyName((String) oVO.getStev_co_nm());
				oVO.setAgentName((String) oVO.getCo_nm());
				oVO.setTotDiscOpenBal((Integer) oVO.getTot_disc_openbal());
				oVO.setTotLoadOpenBal((Integer) oVO.getTot_load_openbal());
				oVO.setTotDiscTotalCompleted((Integer) oVO.getTot_completed_disc());
				oVO.setTotLoadTotalCompleted((Integer) oVO.getTot_completed_load());

				returnList.add(oVO);

			}

			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate
						.queryForObject("SELECT COUNT(*) FROM (" + queryBuf.toString() + ")", paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());

				oVO = new GbccCargoTimesheetVO();
				oVO.setTotal(tableData.getTotal());
				returnList.add(oVO);
			}
			log.info("getCargoTimesheet Result: " + returnList.size());
			return returnList;
		} catch (Exception e) {
			log.info("getCargoTimesheet Exception: ", e);
		}
		return null;
	}

}
