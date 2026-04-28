package sg.com.jp.generalcargo.dao.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.CntrEventLogRepo;
import sg.com.jp.generalcargo.domain.CntrEventLogKey;
import sg.com.jp.generalcargo.util.BusinessException;

@Repository("cntrEventLogRepo")
public class CntrEventLogJdbcRepo implements CntrEventLogRepo {

	private static final Log log = LogFactory.getLog(CntrEventLogJdbcRepo.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// ejb.entityBeans.cab.CntrEventLog-->CntrEventLogEJB-->ejbCreate()
	@Override
	public CntrEventLogKey create(Integer cntrSeqNbr, String cntrNbr, String status, String prevPurpCd,
			Timestamp txnDttm, String txnCd, String userId, String craneNm, String craneOprId, String wtaId,
			String pmType, String pmNm, String pmOprId, String isoSizeTypeCd, String sizeFt, String catCd,
			Integer declrWt, Integer measureWt, String purpCd, String cntrOprCd, String haulCd, String loloPartyInd,
			String discSlotOprCd, String loadSlotOprCd, String renomSlotOprCd, String discVvCd, String ldVvCd,
			String renomVvCd, String psrc, String pload, String pdisc, String pdest, String dgInd, String imdgClCd,
			String refrInd, String ucInd, String overSzInd, String intergatewayInd, Timestamp discDttm,
			Timestamp loadDttm, Timestamp offloadDttm, Timestamp mountDttm, Timestamp arrDttm, Timestamp exitDttm,
			Timestamp changePurpDttm, String dirHdlgInd, String chasProvInd, String gearUsed, String pluginTemp,
			Timestamp pluginDttm, Timestamp unplugDttm, Integer ucHandlingDur, String athwartshipInd, String billVslInd,
			String billYdInd, String procQcIncentive, String procYcIncentive, String procPmIncentive,
			String lastModifyUserId, Timestamp lastModifyDttm) throws BusinessException {

		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		CntrEventLogKey cntrEventLogKey = null;
		int insertResult = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			log.info("START: create  DAO  Start cntrSeqNbr:" + cntrSeqNbr + " cntrNbr:" + cntrNbr + " status:" + status
					+ " prevPurpCd:" + prevPurpCd + " txnDttm:" + txnDttm + " txnCd:" + txnCd + " userId:" + userId
					+ " craneNm:" + craneNm + " craneOprId:" + craneOprId + " wtaId:" + wtaId + " pmType:" + pmType
					+ " pmNm:" + pmNm + " pmOprId:" + pmOprId + " isoSizeTypeCd:" + isoSizeTypeCd + " sizeFt:" + sizeFt
					+ " catCd:" + catCd + " declrWt:" + declrWt + " measureWt:" + " purpCd:" + purpCd + " cntrOprCd:"
					+ cntrOprCd + " haulCd:" + haulCd + " loloPartyInd:" + loloPartyInd + " discSlotOprCd:"
					+ discSlotOprCd + " loadSlotOprCd:" + loadSlotOprCd + " renomSlotOprCd:" + renomSlotOprCd
					+ " discVvCd" + discVvCd + " ldVvCd:" + ldVvCd + " renomVvCd:" + renomVvCd + " psrc:" + psrc
					+ " pload:" + pload + " pdisc:" + pdisc + " pdest:" + pdest + " dgInd:" + dgInd + " imdgClCd:"
					+ imdgClCd + " refrInd:" + refrInd + " ucInd:" + ucInd + " overSzInd:" + overSzInd
					+ " intergatewayInd:" + intergatewayInd + " discDttm:" + discDttm + " loadDttm:" + loadDttm
					+ " offloadDttm:" + offloadDttm + " mountDttm:" + mountDttm + " arrDttm:" + arrDttm + " exitDttm:"
					+ exitDttm + " changePurpDttm:" + changePurpDttm + " dirHdlgInd:" + dirHdlgInd + " chasProvInd:"
					+ chasProvInd + " gearUsed:" + gearUsed + " pluginTemp:" + pluginTemp + " pluginDttm:" + pluginDttm
					+ " unplugDttm:" + unplugDttm + " ucHandlingDur:" + ucHandlingDur + " athwartshipInd:"
					+ athwartshipInd + " billVslInd:" + billVslInd + " billYdInd:" + billYdInd + " procQcIncentive:"
					+ procQcIncentive + " procYcIncentive:" + procYcIncentive + " procPmIncentive:" + procPmIncentive
					+ " lastModifyUserId:" + lastModifyUserId + " lastModifyDttm:" + lastModifyDttm);

			// Insert into the database
			sb.append(" INSERT INTO cntr_event_log (cntr_seq_nbr,cntr_nbr, ");
			sb.append(" status,prev_purp_cd,txn_dttm,txn_cd,user_id,crane_nm, ");
			sb.append(" crane_opr_id,WTA_id,PM_type,PM_nm,PM_opr_id, ");
			sb.append(" iso_size_type_cd,size_ft,cat_cd,declr_wt,measure_wt, ");
			sb.append(" purp_cd,cntr_opr_cd,haul_cd,lolo_party_ind,disc_slot_opr_cd, ");
			sb.append(" load_slot_opr_cd,renom_slot_opr_cd,disc_vv_cd,ld_vv_cd, ");
			sb.append(" renom_vv_cd,psrc,pload,pdisc,pdest,dg_ind,imdg_cl_cd,refr_ind, ");
			sb.append(" uc_ind,over_sz_ind,intergateway_ind,disc_dttm,load_dttm, ");
			sb.append(" offload_dttm,mount_dttm,arr_dttm,exit_dttm,change_purp_dttm, ");
			sb.append(" dir_hdlg_ind,chas_prov_ind,gear_used,plugin_temp, ");
			sb.append(" plugin_dttm,unplug_dttm,uc_handling_dur,athwartship_ind, ");
			sb.append(" bill_vsl_ind,bill_yd_ind,proc_qc_incentive,proc_yc_incentive, ");
			sb.append(" proc_pm_incentive,last_modify_user_id,last_modify_dttm) ");
			sb.append(" VALUES (:cntrSeqNbr,:cntrNbr,:status,:prevPurpCd, ");
			// 51240  Close LCT - Start
			//sb.append("  TO_DATE(:txnDttm,'YYYY-MM-DD HH24:MI:SS'),:txnCd,:userId,:craneNm, ");
			sb.append(" :txnDttm,:txnCd,:userId,:craneNm, ");
			// 51240  Close LCT - End
			sb.append(" :craneOprId,:wtaId,:pmType,:pmNm, ");
			sb.append(" :pmOprId,:isoSizeTypeCd,:sizeFt,:catCd, ");
			sb.append(" :declrWt,:measureWt,:purpCd,:cntrOprCd, ");
			sb.append(" :haulCd,:loloPartyInd,:discSlotOprCd,:loadSlotOprCd, ");
			sb.append(" :renomSlotOprCd,:discVvCd,:ldVvCd,:renomVvCd, ");
			sb.append(" :psrc,:pload,:pdisc,:pdest, ");
			sb.append(" :dgInd,:imdgClCd,:refrInd,:ucInd, ");
			sb.append(" :overSzInd,:intergatewayInd,:discDttm,:loadDttm, ");
			sb.append(" :offloadDttm,:mountDttm,:arrDttm,:exitDttm, ");
			sb.append(" :changePurpDttm,:dirHdlgInd,:chasProvInd,:gearUsed, ");
			sb.append(" :pluginTemp,:pluginDttm,:unplugDttm,:ucHandlingDur, ");
			sb.append(" :athwartshipInd,:billVslInd,:billYdInd,:procQcIncentive, ");
			sb.append(" :procYcIncentive,:procPmIncentive,:lastModifyUserId,sysdate )");

			if (cntrSeqNbr != null) {
				paramMap.put("cntrSeqNbr", cntrSeqNbr.intValue());
			} else {
				paramMap.put("cntrSeqNbr", 0);
			}

			if (cntrNbr != null) {
				paramMap.put("cntrNbr", cntrNbr);
			} else {
				paramMap.put("cntrNbr", null);
			}

			if (status != null) {
				paramMap.put("status", status);
			} else {
				paramMap.put("status", null);
			}

			if (prevPurpCd != null) {
				paramMap.put("prevPurpCd", prevPurpCd);
			} else {
				paramMap.put("prevPurpCd", null);
			}
			
//			String StrDate = txnDttm.toString();
//			String StrDate1 = StrDate.substring(0, 19);
//			log.info("arrOfStrDate" + StrDate1);
//
//			if (txnDttm != null) {
//				paramMap.put("txnDttm", StrDate1);
//			}
			
			if (txnDttm != null) {
				log.info("txnDttm without conversion: " + txnDttm);
				log.info("txnDttm with String conversion: " + sdf.format(txnDttm));
				// 51240  Close LCT - Start
				//paramMap.put("txnDttm", sdf.format(txnDttm));
				paramMap.put("txnDttm", txnDttm);
				// 51240  Close LCT - End
			} else {
				paramMap.put("txnDttm", null);
			}

			if (txnCd != null) {
				paramMap.put("txnCd", txnCd);
			} else {
				paramMap.put("txnCd", null);
			}

			if (userId != null) {
				paramMap.put("userId", userId);
			} else {
				paramMap.put("userId", null);
			}

			if (craneNm != null) {
				paramMap.put("craneNm", craneNm);
			} else {
				paramMap.put("craneNm", null);
			}

			if (craneOprId != null) {
				paramMap.put("craneOprId", craneOprId);
			} else {
				paramMap.put("craneOprId", null);
			}

			if (wtaId != null) {
				paramMap.put("wtaId", wtaId);
			} else {
				paramMap.put("wtaId", null);
			}

			if (pmType != null) {
				paramMap.put("pmType", pmType);
			} else {
				paramMap.put("pmType", null);
			}

			if (pmNm != null) {
				paramMap.put("pmNm", pmNm);
			} else {
				paramMap.put("pmNm", null);
			}

			if (pmOprId != null) {
				paramMap.put("pmOprId", pmOprId);
			} else {
				paramMap.put("pmOprId", null);
			}

			if (isoSizeTypeCd != null) {
				paramMap.put("isoSizeTypeCd", isoSizeTypeCd);
			} else {
				paramMap.put("isoSizeTypeCd", null);
			}

			if (sizeFt != null) {
				paramMap.put("sizeFt", sizeFt);
			} else {
				paramMap.put("sizeFt", null);
			}

			if (catCd != null) {
				paramMap.put("catCd", catCd);
			} else {
				paramMap.put("catCd", null);
			}

			if (declrWt != null) {
				paramMap.put("declrWt", declrWt.intValue());
			} else {
				paramMap.put("declrWt", 0);
			}

			if (measureWt != null) {
				paramMap.put("measureWt", measureWt.intValue());
			} else {
				paramMap.put("measureWt", 0);
			}

			if (purpCd != null) {
				paramMap.put("purpCd", purpCd);
			} else {
				paramMap.put("purpCd", null);
			}

			if (cntrOprCd != null) {
				paramMap.put("cntrOprCd", cntrOprCd);
			} else {
				paramMap.put("cntrOprCd", null);
			}

			if (haulCd != null) {
				paramMap.put("haulCd", haulCd);
			} else {
				paramMap.put("haulCd", null);
			}

			if (loloPartyInd != null) {
				paramMap.put("loloPartyInd", loloPartyInd);
			} else {
				paramMap.put("loloPartyInd", null);
			}

			if (discSlotOprCd != null) {
				paramMap.put("discSlotOprCd", discSlotOprCd);
			} else {
				paramMap.put("discSlotOprCd", null);
			}

			if (loadSlotOprCd != null) {
				paramMap.put("loadSlotOprCd", loadSlotOprCd);
			} else {
				paramMap.put("loadSlotOprCd", null);
			}

			if (renomSlotOprCd != null) {
				paramMap.put("renomSlotOprCd", renomSlotOprCd);
			} else {
				paramMap.put("renomSlotOprCd", null);
			}

			if (discVvCd != null) {
				paramMap.put("discVvCd", discVvCd);
			} else {
				paramMap.put("discVvCd", null);
			}

			if (ldVvCd != null) {
				paramMap.put("ldVvCd", ldVvCd);
			} else {
				paramMap.put("ldVvCd", null);
			}

			if (renomVvCd != null) {
				paramMap.put("renomVvCd", renomVvCd);
			} else {
				paramMap.put("renomVvCd", null);
			}

			if (psrc != null) {
				paramMap.put("psrc", psrc);
			} else {
				paramMap.put("psrc", null);
			}

			if (pload != null) {
				paramMap.put("pload", pload);
			} else {
				paramMap.put("pload", null);
			}

			if (pdisc != null) {
				paramMap.put("pdisc", pdisc);
			} else {
				paramMap.put("pdisc", null);
			}

			if (pdest != null) {
				paramMap.put("pdest", pdest);
			} else {
				paramMap.put("pdest", null);
			}

			if (dgInd != null) {
				paramMap.put("dgInd", dgInd);
			} else {
				paramMap.put("dgInd", null);
			}

			if (imdgClCd != null) {
				paramMap.put("imdgClCd", imdgClCd);
			} else {
				paramMap.put("imdgClCd", null);
			}

			if (refrInd != null) {
				paramMap.put("refrInd", refrInd);
			} else {
				paramMap.put("refrInd", null);
			}

			if (ucInd != null) {
				paramMap.put("ucInd", ucInd);
			} else {
				paramMap.put("ucInd", null);
			}

			if (overSzInd != null) {
				paramMap.put("overSzInd", overSzInd);
			} else {
				paramMap.put("overSzInd", null);
			}

			if (intergatewayInd != null) {
				paramMap.put("intergatewayInd", intergatewayInd);
			} else {
				paramMap.put("intergatewayInd", null);
			}

			if (discDttm != null) {
				// 51240  Close LCT - Start
				paramMap.put("discDttm", discDttm);
				//paramMap.put("discDttm", sdf.format(discDttm));
				// 51240  Close LCT - End
			} else {
				paramMap.put("discDttm", null);
			}

			if (loadDttm != null) {
				// 51240  Close LCT - Start
				paramMap.put("loadDttm", loadDttm);
				//paramMap.put("loadDttm", sdf.format(loadDttm));
				// 51240  Close LCT - End
			} else {
				paramMap.put("loadDttm", null);
			}

			if (offloadDttm != null) {
				// 51240  Close LCT - Start
				paramMap.put("offloadDttm", offloadDttm);
				//paramMap.put("offloadDttm", sdf.format(offloadDttm));
				// 51240  Close LCT - End
			} else {
				paramMap.put("offloadDttm", null);
			}

			if (mountDttm != null) {
				// 51240  Close LCT - Start
				paramMap.put("mountDttm", mountDttm);
				//paramMap.put("mountDttm", sdf.format(mountDttm));
				// 51240  Close LCT - End
			} else {
				paramMap.put("mountDttm", null);
			}

			if (arrDttm != null) {
				// 51240  Close LCT - Start
				paramMap.put("arrDttm", arrDttm);
				//paramMap.put("arrDttm", sdf.format(arrDttm));
				// 51240  Close LCT - End
			} else {
				paramMap.put("arrDttm", null);
			}

			if (exitDttm != null) {
				// 51240  Close LCT - Start
				paramMap.put("exitDttm", exitDttm);
				//paramMap.put("exitDttm", sdf.format(exitDttm));
				// 51240  Close LCT - End
			} else {
				paramMap.put("exitDttm", null);
			}

			if (changePurpDttm != null) {
				// 51240  Close LCT - Start
				paramMap.put("changePurpDttm", changePurpDttm);
				//paramMap.put("changePurpDttm", sdf.format(changePurpDttm));
				// 51240  Close LCT - End
			} else {
				paramMap.put("changePurpDttm", null);
			}

			if (dirHdlgInd != null) {
				paramMap.put("dirHdlgInd", dirHdlgInd);
			} else {
				paramMap.put("dirHdlgInd", null);
			}

			if (chasProvInd != null) {
				paramMap.put("chasProvInd", chasProvInd);
			} else {
				paramMap.put("chasProvInd", null);
			}

			if (gearUsed != null) {
				paramMap.put("gearUsed", gearUsed);
			} else {
				paramMap.put("gearUsed", null);
			}

			if (pluginTemp != null) {
				paramMap.put("pluginTemp", pluginTemp);
			} else {
				paramMap.put("pluginTemp", null);
			}

			if (pluginDttm != null) {
				// 51240  Close LCT - Start
				paramMap.put("pluginDttm", pluginDttm);
				//paramMap.put("pluginDttm", sdf.format(pluginDttm));
				// 51240  Close LCT - End
			} else {
				paramMap.put("pluginDttm", null);
			}

			if (unplugDttm != null) {
				// 51240  Close LCT - Start
				paramMap.put("unplugDttm", unplugDttm);
				//paramMap.put("unplugDttm", sdf.format(unplugDttm));
				// 51240  Close LCT - End
			} else {
				paramMap.put("unplugDttm", null);
			}

			if (ucHandlingDur != null) {
				paramMap.put("ucHandlingDur", ucHandlingDur.intValue());
			} else {
				paramMap.put("ucHandlingDur", 0);
			}

			if (athwartshipInd != null) {
				paramMap.put("athwartshipInd", athwartshipInd);
			} else {
				paramMap.put("athwartshipInd", null);
			}

			if (billVslInd != null) {
				paramMap.put("billVslInd", billVslInd);
			} else {
				paramMap.put("billVslInd", null);
			}

			if (billYdInd != null) {
				paramMap.put("billYdInd", billYdInd);
			} else {
				paramMap.put("billYdInd", null);
			}

			if (procQcIncentive != null) {
				paramMap.put("procQcIncentive", procQcIncentive);
			} else {
				paramMap.put("procQcIncentive", null);
			}

			if (procYcIncentive != null) {
				paramMap.put("procYcIncentive", procYcIncentive);
			} else {
				paramMap.put("procYcIncentive", null);
			}

			if (procPmIncentive != null) {
				paramMap.put("procPmIncentive", procPmIncentive);
			} else {
				paramMap.put("procPmIncentive", null);
			}

			if (lastModifyUserId != null) {
				paramMap.put("lastModifyUserId", lastModifyUserId);
			} else {
				paramMap.put("lastModifyUserId", null);
			}

			// Execute prepared statement
			log.info(" create  DAO  SQL  " + sb.toString() + " ,paramMap:" + paramMap.toString());
			insertResult = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			if (insertResult != 1) {
				throw new BusinessException("Create failed");
			}

			cntrEventLogKey = new CntrEventLogKey();
			cntrEventLogKey.cntrSeqNbr = cntrSeqNbr;
			cntrEventLogKey.txnDttm = txnDttm;
			cntrEventLogKey.txnCd = txnCd;
		} catch (NullPointerException e) {
			log.error("Exception create :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception create :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: create  DAO  Result:" + (cntrEventLogKey != null ? cntrEventLogKey.toString() : null));
		}
		return cntrEventLogKey;
	}

}
