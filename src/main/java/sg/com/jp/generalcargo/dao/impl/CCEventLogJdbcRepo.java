package sg.com.jp.generalcargo.dao.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.CCEventLogRepo;
import sg.com.jp.generalcargo.dao.CntrEventLogRepo;
import sg.com.jp.generalcargo.dao.GBEventLogRepository;
import sg.com.jp.generalcargo.domain.CloseLctValueObject;
import sg.com.jp.generalcargo.domain.VesselTxnEventLogValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("ccEventLogRepo")
public class CCEventLogJdbcRepo implements CCEventLogRepo {

	private static final Log log = LogFactory.getLog(CCEventLogJdbcRepo.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private CntrEventLogRepo cntrEventLogRepo;

	@Autowired
	private GBEventLogRepository gbEventLogRepo;

	// ejb.sessionBeans.ops.CCEventLog-->CCEventLogEJB-->getCatCodeNStatus()
	@Override
	public String[] getCatCodeNStatus(int cntrSeqNbr) throws BusinessException {
		String[] retArr = null;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getCatCodeNStatus  DAO  Start cntrSeqNbr:" + cntrSeqNbr);

			sb.append(" select status, cat_cd from cntr_txn where cntr_seq_nbr=:cntrSeqNbr ");
			sb.append(" and txn_cd='UNSC' order by txn_dttm ");

			log.info(" ***getCatCodeNStatus SQL *****" + sb.toString());

			paramMap.put("cntrSeqNbr", cntrSeqNbr);

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				retArr = new String[2];
				retArr[0] = CommonUtility.deNull(rs.getString("status"));
				retArr[1] = CommonUtility.deNull(rs.getString("cat_cd"));
			}
		} catch (NullPointerException e) {
			log.error("Exception getCatCodeNStatus :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getCatCodeNStatus :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCatCodeNStatus  DAO  Result:" + (retArr != null ? retArr.toString() : ""));
		}
		return retArr;
	}

	// ejb.sessionBeans.ops.CCEventLog-->CCEventLogEJB-->insertOpsCCEventLogForLct()
	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void insertOpsCCEventLogForLct(CloseLctValueObject vo, String UserID) throws BusinessException {
		try {
			log.info("START: insertOpsCCEventLogForLct  DAO  Start vo:" + vo.toString() + " UserID:" + UserID);

			String vvCd = vo.getVv_cd();
			if ("Y".equals(vo.getClose_bj())) {
				insertOpsCCEventLog(vvCd, "B", UserID);
			}
			if ("Y".equals(vo.getClose_shipment())) {
				insertOpsCCEventLog(vvCd, "C", UserID);
			}
		} catch (BusinessException e) {
			log.error("Exception: insertOpsCCEventLogForLct ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.error("Exception insertOpsCCEventLogForLct :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertOpsCCEventLogForLct DAO");
		}
	}

	// ejb.sessionBeans.ops.CCEventLog-->CCEventLogEJB-->insertOpsCCEventLog()
	@Override
	public boolean insertOpsCCEventLog(String vv_Cd, String triggerType, String UserID) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		Integer cntrSeqNbr = null;
		String txnCd = null;
		String haulCd = null;
		String userId = null;
		String craneNm = null;
		String wtaId = null;
		String craneOprId = null;
		String pmType = null;
		String pmNm = null;
		String pmOprId = null;
		String db_cntrNbr = null;
		String db_status = null;
		String db_prevPurpCd = null;
		Timestamp db_txnDttm = null;
		String db_isoSizeTypeCd = null;
		String db_sizeFt = null;
		String db_catCd = null;
		Integer db_declrWt = null;
		Integer db_measureWt = null;
		String db_purpCd = null;
		String db_cntrOprCd = null;
		String db_discSlotOprCd = null;
		String db_loadSlotOprCd = null;
		String db_discVvCd = null;
		String db_ldVvCd = null;
		String db_psrc = null;
		String db_pload = null;
		String db_pdisc = null;
		String db_pdest = null;
		String db_dgInd = null;
		String db_imdgClCd = null;
		String db_refrInd = null;
		String db_ucInd = null;
		String db_overSzInd = null;
		String db_intergatewayInd = null;
		Timestamp db_discDttm = null;
		Timestamp db_loadDttm = null;
		Timestamp db_offloadDttm = null;
		Timestamp db_mountDttm = null;
		Timestamp db_arrDttm = null;
		Timestamp db_exitDttm = null;
		Timestamp db_changePurpDttm = null;
		String db_dirHdlgInd = null;
		String db_chasProvInd = null;
		String db_gearUsed = null;
		String db_pluginTemp = null;
		Timestamp db_pluginDttm = null;
		Timestamp db_unplugDttm = null;
		Integer db_ucHandlingDur = null;
		String db_athwartshipInd = null;
		String db_loloPartyInd = null;
		// These are not in cntr or cntr_operation tables
		String db_renomSlotOprCd = null;
		String db_renomVvCd = null;
		String lastModifyUserId = UserID;
		Timestamp lastModifyDttm = null;
		String billVslInd = "N";
		String billYdInd = "N";
		String procPmIncentive = "N";
		String procQcIncentive = "N";
		String procYcIncentive = "N";
		try {
			log.info("START: insertOpsCCEventLog  DAO  Start vv_Cd:" + vv_Cd + " triggerType:" + triggerType
					+ " UserID:" + UserID);

			String triggerCode = null;

			// add LCT Scheme, 9.mar.11 by hpeng
			if (ProcessChargeConst.LCT_SCHEME.equalsIgnoreCase(getSchemeCd(vv_Cd))) {
				if (triggerType.equals("1") || triggerType.equals("2")) {
					return false;
				}
				if (triggerType.equals("B")) {
					triggerType = "1";
				}
				if (triggerType.equals("C")) {
					triggerType = "2";
				}
			}

			if (triggerType.equals("1")) {
				// Load from the data base for Close BJ
				// Added by Mc consulting, Removed JNL to trigger DCSC upon close-bj
				triggerCode = "CloseBJ";
				sb.append(" select * from cntr c, cntr_operation o, vessel_call v ");
				sb.append(" where c.cntr_seq_nbr = o.cntr_seq_nbr and ");
				sb.append(" c.disc_vv_cd = v.vv_cd and ((c.purp_cd in ('IM') and ");
				sb.append(" c.ct_planned_disc = 'N' and (v.scheme not in ('JBT') or o.exception_mvmt = 'V')) or ");
				sb.append(" (c.purp_cd in ('TS', 'RE', 'LN') and c.ct_planned_disc = 'N'  )) and ");
				sb.append(" o.disc_key_ind = 'Y' and ");
				sb.append(" c.shipment_status != 'SH'  and c.disc_vv_cd = :vvCd and c.cntr_seq_nbr ");
				sb.append(" not in ( select cntr_seq_nbr from cntr_event_log where ");
				sb.append(" txn_cd = 'DCSC' and disc_vv_cd = :vvCd ) ");
			} else if (triggerType.equals("2")) {
				triggerCode = "CloseShipment";
				// Load from the data base for Close Shipment
				sb.append(" select * from cntr c, cntr_operation o ");
				sb.append(" where c.cntr_seq_nbr = o.cntr_seq_nbr and  ");
				sb.append(" c.purp_cd in ('EX', 'RS', 'TS', 'RE')  ");
				sb.append(" and c.ct_planned_load = 'N'  ");
				sb.append(" and o.load_key_ind = 'Y'  and c.load_vv_cd = :vvCd ");
				sb.append(" and c.cntr_seq_nbr not in ( select cntr_seq_nbr from cntr_event_log ");
				sb.append(" where txn_cd = 'LDSC' and ld_vv_cd = :vvCd ) ");

			}

			log.info(" ***insertOpsCCEventLog SQL *****" + sb.toString());

			paramMap.put("vvCd", vv_Cd);

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				// triggerCode ="CloseBJ";
				if (triggerType.equals("1") && triggerCode.equals("CloseBJ")) {
					// System.out.println("######### Testing karthi--cntr_seq_nbr ####");
					cntrSeqNbr = new Integer(rs.getInt("cntr_seq_nbr"));// CNTR_SEQ_NBR
					// System.out.println("######### Testing karthi--cntr_nbr ####");
					db_cntrNbr = rs.getString("cntr_nbr");

					int cntrsqNbrInt = cntrSeqNbr.intValue();
					/*
					 * method modified by karthi and maded as public inorder OpsEventLog also to use
					 * the same method String[]
					 * statusNCatCd=getCatCodeNStatus(cntrsqNbrInt,connection);
					 */
					String[] statusNCatCd = getCatCodeNStatus(cntrsqNbrInt);
					if (statusNCatCd != null) {
						db_status = statusNCatCd[0];
						db_catCd = statusNCatCd[1];
					} else {
						db_status = rs.getString("status");
						db_catCd = rs.getString("cat_cd");
					}
					db_txnDttm = rs.getTimestamp("disc_dttm");
					txnCd = "DCSC";
					db_isoSizeTypeCd = rs.getString("iso_size_type_cd");
					db_sizeFt = rs.getString("size_ft");
					db_declrWt = new Integer(rs.getInt("declr_wt"));
					db_purpCd = rs.getString("purp_cd");
					db_cntrOprCd = rs.getString("cntr_opr_cd");
					db_discSlotOprCd = rs.getString("disc_slot_opr_cd");
					db_discVvCd = rs.getString("disc_vv_cd");
					db_ldVvCd = rs.getString("load_vv_cd");
					db_psrc = rs.getString("psrc");
					db_pload = rs.getString("pload");
					db_pdisc = rs.getString("pdisc1");
					db_pdest = rs.getString("pdest");
					db_dgInd = rs.getString("dg_ind");
					db_imdgClCd = rs.getString("imdg_cl_cd");
					db_refrInd = rs.getString("refr_ind");
					db_ucInd = rs.getString("uc_ind");
					db_overSzInd = rs.getString("over_sz_ind");
					db_discDttm = rs.getTimestamp("disc_dttm");
					db_dirHdlgInd = rs.getString("dir_hdlg_ind");
					db_intergatewayInd = rs.getString("intergateway_ind");

				}
				// triggerCode ="CloseBJ";
				else if (triggerType.equals("2") && triggerCode.equals("CloseShipment")) {

					cntrSeqNbr = new Integer(rs.getInt("cntr_seq_nbr"));
					db_cntrNbr = rs.getString("cntr_nbr");
					db_status = rs.getString("status");
					db_prevPurpCd = rs.getString("prev_purp_cd");
					db_txnDttm = rs.getTimestamp("load_dttm");
					txnCd = "LDSC";
					// db_txnDttm = new Timestamp(System.currentTimeMillis());
					db_txnDttm = rs.getTimestamp("load_dttm");
					db_isoSizeTypeCd = rs.getString("iso_size_type_cd");
					db_sizeFt = rs.getString("size_ft");
					db_catCd = rs.getString("cat_cd");
					db_declrWt = new Integer(rs.getInt("declr_wt"));
					db_measureWt = new Integer(rs.getInt("measure_wt"));
					db_purpCd = rs.getString("purp_cd");
					db_cntrOprCd = rs.getString("cntr_opr_cd");
					haulCd = rs.getString("exp_haul_cd");
					db_discSlotOprCd = rs.getString("disc_slot_opr_cd");
					db_loadSlotOprCd = rs.getString("load_slot_opr_cd");
					db_discVvCd = rs.getString("disc_vv_cd");
					db_ldVvCd = rs.getString("load_vv_cd");
					db_psrc = rs.getString("psrc");
					db_pload = rs.getString("pload");
					db_pdisc = rs.getString("pdisc1");
					db_pdest = rs.getString("pdest");
					db_dgInd = rs.getString("dg_ind");
					db_imdgClCd = rs.getString("imdg_cl_cd");
					db_refrInd = rs.getString("refr_ind");
					db_ucInd = rs.getString("uc_ind");
					db_overSzInd = rs.getString("over_sz_ind");
					db_intergatewayInd = rs.getString("intergateway_ind");
					db_discDttm = rs.getTimestamp("disc_dttm");
					db_loadDttm = rs.getTimestamp("load_dttm");
					db_arrDttm = rs.getTimestamp("arr_dttm");
					db_changePurpDttm = rs.getTimestamp("change_purp_dttm");
					db_dirHdlgInd = rs.getString("dir_hdlg_ind");
					db_chasProvInd = rs.getString("chas_prov_ind");
					db_gearUsed = rs.getString("gear_used");

					// billYdInd modified by karthi on 25-03-04

					String exceprtionMvmt = CommonUtility.deNull(rs.getString("exception_mvmt"));// exception_mvmt
					if (exceprtionMvmt.equalsIgnoreCase("V")) {
						billYdInd = "X";
					} else {
						billYdInd = "N";
					}

				}
				// Set the last_modify_user id and last_modify_dttm before insertion
				lastModifyUserId = UserID;
				lastModifyDttm = new Timestamp(System.currentTimeMillis());

				// need to fix
				// if(cntrEventLogHome!=null){ cntrEventLog=
				cntrEventLogRepo.create(cntrSeqNbr, db_cntrNbr, db_status, db_prevPurpCd, db_txnDttm, txnCd, userId,
						craneNm, craneOprId, wtaId, pmType, pmNm, pmOprId, db_isoSizeTypeCd, db_sizeFt, db_catCd,
						db_declrWt, db_measureWt, db_purpCd, db_cntrOprCd, haulCd, db_loloPartyInd, db_discSlotOprCd,
						db_loadSlotOprCd, db_renomSlotOprCd, db_discVvCd, db_ldVvCd, db_renomVvCd, db_psrc, db_pload,
						db_pdisc, db_pdest, db_dgInd, db_imdgClCd, db_refrInd, db_ucInd, db_overSzInd,
						db_intergatewayInd, db_discDttm, db_loadDttm, db_offloadDttm, db_mountDttm, db_arrDttm,
						db_exitDttm, db_changePurpDttm, db_dirHdlgInd, db_chasProvInd, db_gearUsed, db_pluginTemp,
						db_pluginDttm, db_unplugDttm, db_ucHandlingDur, db_athwartshipInd, billVslInd, billYdInd,
						procQcIncentive, procYcIncentive, procPmIncentive, lastModifyUserId, lastModifyDttm);
				// }

			}
			// need to fix
			// if(cntrEventLog !=null){
			VesselTxnEventLogValueObject vesselTxnEventLogValueObject = new VesselTxnEventLogValueObject();
			vesselTxnEventLogValueObject.setVvCd(vv_Cd);
			// = new Timestamp(System.currentTimeMillis());
			vesselTxnEventLogValueObject.setTxnDttm(new Timestamp(System.currentTimeMillis()));
			// vesselTxnEventLogValueObject.setBillSvcChargeInd("Y");
			vesselTxnEventLogValueObject.setBillCntrSvcInd("Y");
			if (triggerType.equals("2") && triggerCode.equals("CloseShipment"))
				vesselTxnEventLogValueObject.setBillCntrStoreInd("Y");
			vesselTxnEventLogValueObject.setLastModifyUserId(lastModifyUserId);
			vesselTxnEventLogValueObject.setLastModifyDttm(lastModifyDttm);

			gbEventLogRepo.logVesselTxnEvent(vesselTxnEventLogValueObject);

			log.info("END: ** insertOpsCCEventLog Result ****" + vesselTxnEventLogValueObject.toString());
		} catch (NullPointerException e) {
			log.error("Exception insertOpsCCEventLog :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception insertOpsCCEventLog :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertOpsCCEventLog  DAO  END");
		}
		return true;
	}

	// ejb.sessionBeans.ops.CCEventLog-->CCEventLogEJB-->getSchemeCd()
	private String getSchemeCd(String vvCd) throws BusinessException {
		String schemeCd = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getSchemeCd  DAO  Start vvCd:" + vvCd);

			sb.append(" SELECT VESCALL.SCHEME FROM VESSEL_CALL VESCALL, ");
			sb.append(" VESSEL_SCHEME VS WHERE VESCALL.SCHEME=VS.SCHEME_CD AND ");
			sb.append(" VESCALL.VV_CD= :vvCd ");

			log.info(" ***getSchemeCd SQL *****" + sb.toString());

			paramMap.put("vvCd", vvCd);

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				schemeCd = CommonUtility.deNull(rs.getString(1));
			}

			log.info("END: ** getSchemeCd Result ****" + schemeCd.toString());
		} catch (NullPointerException e) {
			log.error("Exception getSchemeCd :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getSchemeCd :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSchemeCd  DAO  END");
		}
		return schemeCd;
	}

}
