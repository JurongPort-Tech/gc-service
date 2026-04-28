package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.ContainerDataRepo;
import sg.com.jp.generalcargo.domain.ContainerValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("containerDataRepository")
public class ContainerDataJdbcRepo implements ContainerDataRepo {

	private static final Log log = LogFactory.getLog(ContainerDataJdbcRepo.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// package : ejb.sessionBeans.cim.Container-->ContainerDataEJB
	// StartRegion ContainerDataEJB
	// method: parseString()
	private String parseString(String inputStr) {

		inputStr = CommonUtility.deNull(inputStr);
		inputStr = inputStr.trim().toUpperCase();
		return inputStr;
	}

	// method: getContainerVO()
	// Added by MC Consulting for VGM
	// private ArrayList getContainerVO(ResultSet rs) {
	private List<ContainerValueObject> getContainerVO(SqlRowSet rs) throws BusinessException {
		// End of addition by MC Consulting for VGM
		List<ContainerValueObject> containerList = new ArrayList<ContainerValueObject>();
		ContainerValueObject containerVO = null;

		if (rs == null)
			return null;
		try {
			while (rs.next()) {
				containerVO = new ContainerValueObject();
				// (cntr_seq_nbr,txn_status,cntr_nbr,Status,iso_size_type_cd,size_ft,ht_ft,"
				// +
				containerVO.setContainerSeqNo(rs.getLong("cntr_seq_nbr"));
				containerVO.setTxnStatus(rs.getString("txn_status"));
				containerVO.setContainerNo(rs.getString("cntr_nbr"));
				containerVO.setStatus(rs.getString("Status"));
				containerVO.setIsoCode(rs.getString("iso_size_type_cd"));
				containerVO.setSizeFt(rs.getString("size_ft"));
				containerVO.setHeightFt(rs.getString("ht_ft"));

				// "width_ft,old_new_ind,type_cd,cat_cd,declr_wt,measure_wt,wt_class,Psrc,Pload,"
				// +
				containerVO.setWidthFt(rs.getString("width_ft"));
				containerVO.setOldNewInd(rs.getString("old_new_ind"));
				containerVO.setTypeCode(rs.getString("type_cd"));
				containerVO.setCategoryCode(rs.getString("cat_cd"));

				containerVO.setWeight(rs.getLong("declr_wt"));
				containerVO.setMeasureWeight(rs.getLong("measure_wt"));
				containerVO.setWeightClass(rs.getString("wt_class"));
				containerVO.setPsrc(rs.getString("Psrc"));
				containerVO.setPload(rs.getString("Pload"));

				// "pdisc1,pdisc2,pdisc3,Pdest,oog_unit,oog_oh,oog_ol_front,oog_ol_back,oog_ow_right,"
				// +
				containerVO.setPdisc1(rs.getString("pdisc1"));
				containerVO.setPdisc2(rs.getString("pdisc2"));
				containerVO.setPdisc3(rs.getString("pdisc3"));
				containerVO.setPdest(rs.getString("Pdest"));
				containerVO.setOogUnit(rs.getString("oog_unit"));
				containerVO.setOogOH(rs.getInt("oog_oh"));
				containerVO.setOogOlFront(rs.getInt("oog_ol_front"));
				containerVO.setOogOlBack(rs.getInt("oog_ol_back"));
				containerVO.setOogOwRight(rs.getInt("oog_ow_right"));

				// "oog_ow_left,refr_temp,refr_volt,imdg_cl_cd,cntr_opr_cd,tli_batch_nbr,imp_haul_cd,"
				// +
				containerVO.setOogOwLeft(rs.getInt("oog_ow_left"));
				containerVO.setReeferTemp(rs.getString("refr_temp"));
				containerVO.setReeferVolt(rs.getString("refr_volt"));
				containerVO.setImdgClassCode(rs.getString("imdg_cl_cd"));
				containerVO.setContainerOperator(rs.getString("cntr_opr_cd"));
				containerVO.setTLIBatchNo(rs.getString("tli_batch_nbr"));
				containerVO.setImportHaulier(rs.getString("imp_haul_cd"));

				// "exp_haul_cd,lolo_party_ind,disc_slot_opr_cd,load_slot_opr_cd,disc_vv_cd,nom_disc_vv_cd,"
				// +
				containerVO.setExportHaulier(rs.getString("exp_haul_cd"));
				containerVO.setLoLoPartyInd(rs.getString("lolo_party_ind"));
				containerVO.setDiscSlotOperator(rs.getString("disc_slot_opr_cd"));
				containerVO.setLoadSlotOperator(rs.getString("load_slot_opr_cd"));
				containerVO.setDiscVesselVoyage(rs.getString("disc_vv_cd"));
				containerVO.setNomDiscVesselVoyage(rs.getString("nom_disc_vv_cd"));

				// "nom_load_vv_cd,load_vv_cd,doc_status,chas_prov_ind,purp_cd,prev_purp_cd,trpt_mode_cd,"
				// +
				containerVO.setNomLoadVesselVoyage(rs.getString("nom_load_vv_cd"));
				containerVO.setLoadVesselVoyage(rs.getString("load_vv_cd"));
				containerVO.setDocStatus(rs.getString("doc_status"));
				containerVO.setChasProvInd(rs.getString("chas_prov_ind"));
				containerVO.setPurposeCode(rs.getString("purp_cd"));
				containerVO.setPreviousPurposeCode(rs.getString("prev_purp_cd"));
				containerVO.setTrptModeCode(rs.getString("trpt_mode_cd"));

				// "svc_type_cd,commodity_cd,seal_nbr,bill_lading_nbr,disc_os_ind,load_os_ind,dg_ind,"
				// +
				containerVO.setSvcTypeCode(rs.getString("svc_type_cd"));
				containerVO.setCommodityCode(rs.getString("commodity_cd"));
				containerVO.setSealNo(rs.getString("seal_nbr"));
				containerVO.setBillLadingNo(rs.getString("bill_lading_nbr"));
				containerVO.setDiscOSInd(rs.getString("disc_os_ind"));
				containerVO.setLoadOSInd(rs.getString("load_os_ind"));
				containerVO.setDGInd(rs.getString("dg_ind"));

				// "refr_ind,uc_ind,over_sz_ind,intergateway_ind,disc_gateway,load_gateway,shipment_status,"
				// +
				containerVO.setReeferInd(rs.getString("refr_ind"));
				containerVO.setUCInd(rs.getString("uc_ind"));
				containerVO.setOverSizeInd(rs.getString("over_sz_ind"));
				containerVO.setIntergatewayInd(rs.getString("intergateway_ind"));
				containerVO.setDiscGateway(rs.getString("disc_gateway"));
				containerVO.setLoadGateway(rs.getString("load_gateway"));
				containerVO.setShipmentStatus(rs.getString("shipment_status"));

				// "dir_hdlg_ind,uc_unit,uc_len,uc_width,uc_ht,ucr_nbr,ucr_bundle_nbr,imp_bay_nbr,"
				// +
				containerVO.setDirHandlingInd(rs.getString("dir_hdlg_ind"));
				containerVO.setUCUnit(rs.getString("uc_unit"));
				containerVO.setUCLength(rs.getInt("uc_len"));
				containerVO.setUCWidth(rs.getInt("uc_width"));
				containerVO.setUCHeight(rs.getInt("uc_ht"));
				containerVO.setUCRNo(rs.getString("ucr_nbr"));
				containerVO.setUCRBundleNo(rs.getLong("ucr_bundle_nbr"));
				containerVO.setImportBayNo(rs.getString("imp_bay_nbr"));

				// "imp_row_nbr,imp_tier_nbr,imp_deck_nbr,imp_bay_pos_cd,exp_bay_nbr,exp_row_nbr,"
				// +
				containerVO.setImportRowNo(rs.getString("imp_row_nbr"));
				containerVO.setImportTierNo(rs.getString("imp_tier_nbr"));
				containerVO.setImportDeckNo(rs.getString("imp_deck_nbr"));
				containerVO.setImportBayPosCode(rs.getString("imp_bay_pos_cd"));
				containerVO.setExportBayNo(rs.getString("exp_bay_nbr"));
				containerVO.setExportRowNo(rs.getString("exp_row_nbr"));

				// "exp_tier_nbr,exp_deck_nbr,exp_bay_pos_cd,create_user_id,create_org_cd,create_dttm,"
				// +
				containerVO.setExportTierNo(rs.getString("exp_tier_nbr"));
				containerVO.setExportDeckNo(rs.getString("exp_deck_nbr"));
				containerVO.setExportBayPosCode(rs.getString("exp_bay_pos_cd"));
				containerVO.setCreateUserId(rs.getString("create_user_id"));
				containerVO.setCreateOrgCode(rs.getString("create_org_cd"));
				containerVO.setCreateDttm(rs.getTimestamp("create_dttm"));

				// "auth_slip_nbr,last_modify_dttm,special_details,cargo_desc,pscw_id_nbr,last_modify_user_id)
				// " +
				containerVO.setAuthSlipNo(rs.getString("auth_slip_nbr"));
				containerVO.setLastModifyDttm(rs.getTimestamp("last_modify_dttm"));
				containerVO.setSpecialDetails(rs.getString("special_details"));
				containerVO.setCargoDesc(rs.getString("cargo_desc"));
				containerVO.setPscwIdNo(rs.getLong("pscw_id_nbr"));
				containerVO.setLastModifyUserId(rs.getString("last_modify_user_id"));
				// START: FPT-ManhT Blocking Of Container 2-July-2010
				containerVO.setIsBlock(CommonUtility.deNull(rs.getString("is_blocked")));
				// END: FPT-ManhT Blocking Of Container 2-July-2010
				// Added by MC Consulting for VGM
				setCntrVgm(containerVO);
				// End of addition by MC Consulting for VGM
				containerList.add(containerVO);
			}
			return containerList;
		} catch (NullPointerException e) {
			log.info("Exception getContainerVO :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getContainerVO :", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getContainerVO  DAO  END");
		}
	}

	/***************************************************************************
	 * Business Method
	 * 
	 * @throws Exception
	 **************************************************************************/

	@Override
	public ContainerValueObject getContainerByPrimaryKey(long containerSequenceNo) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getContainerByPrimaryKey  DAO  Start containerSequenceNo:" + containerSequenceNo);

			sb.append(" Select cntr_seq_nbr,txn_status,cntr_nbr,Status, ");
			sb.append(" iso_size_type_cd,size_ft,ht_ft, ");
			sb.append(" width_ft,old_new_ind,type_cd,cat_cd,declr_wt,measure_wt, ");
			sb.append(" wt_class,Psrc,Pload, ");
			sb.append(" pdisc1,pdisc2,pdisc3,Pdest,oog_unit,oog_oh,oog_ol_front, ");
			sb.append(" oog_ol_back,oog_ow_right, ");
			sb.append(" oog_ow_left,refr_temp,refr_volt,imdg_cl_cd,cntr_opr_cd, ");
			sb.append(" tli_batch_nbr,imp_haul_cd, ");
			sb.append(" exp_haul_cd,lolo_party_ind,disc_slot_opr_cd,load_slot_opr_cd, ");
			sb.append(" disc_vv_cd,nom_disc_vv_cd, ");
			sb.append(" nom_load_vv_cd,load_vv_cd,doc_status,chas_prov_ind,purp_cd, ");
			sb.append(" prev_purp_cd,trpt_mode_cd, ");
			sb.append(" svc_type_cd,commodity_cd,seal_nbr,bill_lading_nbr,disc_os_ind, ");
			sb.append(" load_os_ind,dg_ind, ");
			sb.append(" refr_ind,uc_ind,over_sz_ind,intergateway_ind,disc_gateway, ");
			sb.append(" load_gateway,shipment_status, ");
			sb.append(" dir_hdlg_ind,uc_unit,uc_len,uc_width,uc_ht,ucr_nbr, ");
			sb.append(" ucr_bundle_nbr,imp_bay_nbr, ");
			sb.append(" imp_row_nbr,imp_tier_nbr,imp_deck_nbr,imp_bay_pos_cd,exp_bay_nbr, ");
			sb.append(" exp_row_nbr, ");
			sb.append(" exp_tier_nbr,exp_deck_nbr,exp_bay_pos_cd,create_user_id, ");
			sb.append(" create_org_cd,create_dttm, ");
			sb.append(" auth_slip_nbr,last_modify_dttm,special_details, ");
			sb.append(" cargo_desc,pscw_id_nbr,last_modify_user_id, is_blocked ");
			sb.append(" from cntr ");
			sb.append(" where cntr_seq_nbr=:containerSequenceNo ");

			log.info(" ***getContainerByPrimaryKey SQL *****" + sb.toString());

			paramMap.put("containerSequenceNo", containerSequenceNo);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			// Added by MC Consulting for VGM
			// ArrayList containerList = getContainerVO(rsResult);
			List<ContainerValueObject> containerList = getContainerVO(rs);
			// End of addition by MC Consulting for VGM

			if (containerList == null || containerList.size() != 1)
				return null;
			Iterator<ContainerValueObject> iterator = containerList.iterator();
			ContainerValueObject containerVO = null;

			if (iterator.hasNext())
				containerVO = (ContainerValueObject) iterator.next();

			log.info("END: *** getContainerByPrimaryKey Result *****" + containerVO.toString());
			return containerVO;
		} catch (NullPointerException e) {
			log.info("Exception getContainerByPrimaryKey :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getContainerByPrimaryKey :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getContainerByPrimaryKey  DAO  END");
		}

	}

	// method: getContainerByTLIBatchRef()
	@Override
	public List<ContainerValueObject> getContainerByTLIBatchRef(String batchNo) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getContainerByTLIBatchRef  DAO  Start batchNo:" + batchNo);

			sb.append(" Select cntr_seq_nbr,txn_status,cntr_nbr,Status, ");
			sb.append(" iso_size_type_cd,size_ft,ht_ft, ");
			sb.append(" width_ft,old_new_ind,type_cd,cat_cd,declr_wt,measure_wt, ");
			sb.append(" wt_class,Psrc,Pload, ");
			sb.append(" pdisc1,pdisc2,pdisc3,Pdest,oog_unit,oog_oh,oog_ol_front, ");
			sb.append(" oog_ol_back,oog_ow_right, ");
			sb.append(" oog_ow_left,refr_temp,refr_volt,imdg_cl_cd,cntr_opr_cd, ");
			sb.append(" tli_batch_nbr,imp_haul_cd, ");
			sb.append(" exp_haul_cd,lolo_party_ind,disc_slot_opr_cd,load_slot_opr_cd, ");
			sb.append(" disc_vv_cd,nom_disc_vv_cd, ");
			sb.append(" nom_load_vv_cd,load_vv_cd,doc_status,chas_prov_ind,purp_cd, ");
			sb.append(" prev_purp_cd,trpt_mode_cd, ");
			sb.append(" svc_type_cd,commodity_cd,seal_nbr,bill_lading_nbr,disc_os_ind, ");
			sb.append(" load_os_ind,dg_ind, ");
			sb.append(" refr_ind,uc_ind,over_sz_ind,intergateway_ind,disc_gateway, ");
			sb.append(" load_gateway,shipment_status, ");
			sb.append(" dir_hdlg_ind,uc_unit,uc_len,uc_width,uc_ht,ucr_nbr, ");
			sb.append(" ucr_bundle_nbr,imp_bay_nbr, ");
			sb.append(" imp_row_nbr,imp_tier_nbr,imp_deck_nbr,imp_bay_pos_cd,exp_bay_nbr, ");
			sb.append(" exp_row_nbr, ");
			sb.append(" exp_tier_nbr,exp_deck_nbr,exp_bay_pos_cd,create_user_id, ");
			sb.append(" create_org_cd,create_dttm, ");
			sb.append(" auth_slip_nbr,last_modify_dttm,special_details, ");
			sb.append(" cargo_desc,pscw_id_nbr,last_modify_user_id, is_blocked ");
			sb.append(" from cntr ");
			sb.append(" where TLI_batch_nbr=:batchNo ");

			log.info(" ***getContainerByTLIBatchRef SQL *****" + sb.toString());

			paramMap.put("batchNo", parseString(batchNo));
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			// Added by MC Consulting for VGM
			// ArrayList containerList = getContainerVO(rsResult);
			List<ContainerValueObject> containerList = getContainerVO(rs);
			// End of addition by MC Consulting for VGM

			log.info("END: *** getContainerByTLIBatchRef Result *****" + containerList.toString());
			return containerList;
		} catch (NullPointerException e) {
			log.info("Exception getContainerByTLIBatchRef :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getContainerByTLIBatchRef :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getContainerByTLIBatchRef  DAO  END");
		}
	}

	// method: getActiveContainerByCntrNo()
	@Override
	public ContainerValueObject getActiveContainerByCntrNo(String cntrNo) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getActiveContainerByCntrNo  DAO  Start cntrNo:" + cntrNo);

			sb.append(" Select cntr_seq_nbr,txn_status,cntr_nbr,Status,iso_size_type_cd,size_ft,ht_ft, ");
			sb.append(" width_ft,old_new_ind,type_cd,cat_cd,declr_wt,measure_wt,wt_class,Psrc,Pload, ");
			sb.append(" pdisc1,pdisc2,pdisc3,Pdest,oog_unit,oog_oh,oog_ol_front,oog_ol_back,oog_ow_right, ");
			sb.append(" oog_ow_left,refr_temp,refr_volt,imdg_cl_cd,cntr_opr_cd,tli_batch_nbr,imp_haul_cd, ");
			sb.append(" exp_haul_cd,lolo_party_ind,disc_slot_opr_cd,load_slot_opr_cd,disc_vv_cd,nom_disc_vv_cd, ");
			sb.append(" nom_load_vv_cd,load_vv_cd,doc_status,chas_prov_ind,purp_cd,prev_purp_cd,trpt_mode_cd, ");
			sb.append(" svc_type_cd,commodity_cd,seal_nbr,bill_lading_nbr,disc_os_ind,load_os_ind,dg_ind, ");
			sb.append(" refr_ind,uc_ind,over_sz_ind,intergateway_ind,disc_gateway,load_gateway,shipment_status, ");
			sb.append(" dir_hdlg_ind,uc_unit,uc_len,uc_width,uc_ht,ucr_nbr,ucr_bundle_nbr,imp_bay_nbr, ");
			sb.append(" imp_row_nbr,imp_tier_nbr,imp_deck_nbr,imp_bay_pos_cd,exp_bay_nbr,exp_row_nbr, ");
			sb.append(" exp_tier_nbr,exp_deck_nbr,exp_bay_pos_cd,create_user_id,create_org_cd,create_dttm, ");
			sb.append(" auth_slip_nbr,last_modify_dttm,special_details,cargo_desc,pscw_id_nbr,last_modify_user_id ");
			sb.append(" , is_blocked from cntr ");
			sb.append(" where txn_status='A' and cntr_nbr=:cntrNo ");

			log.info(" ***getActiveContainerByCntrNo SQL *****" + sb.toString());
			paramMap.put("cntrNo", cntrNo);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			// Added by MC Consulting for VGM
			// ArrayList containerList = getContainerVO(rsResult);
			List<ContainerValueObject> containerList = getContainerVO(rs);
			// End of addition by MC Consulting for VGM
			if (containerList == null || containerList.size() != 1)
				return null;
			Iterator<ContainerValueObject> iterator = containerList.iterator();
			ContainerValueObject containerVO = null;
			if (iterator.hasNext())
				containerVO = (ContainerValueObject) iterator.next();

			log.info("END: *** getActiveContainerByCntrNo Result *****" + containerVO.toString());
			return containerVO;
		} catch (NullPointerException e) {
			log.info("Exception getActiveContainerByCntrNo :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getActiveContainerByCntrNo :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getActiveContainerByCntrNo  DAO  END");
		}
	}

	// method: getContainerVO()
	// Added by MC Consulting for VGM
	private void setCntrVgm(ContainerValueObject containerVO) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: setCntrVgm  DAO  Start containerVO:" + containerVO.toString());

			sb.append(" SELECT VGM_IND, WEIGH_METHOD_CD, VGM_SIGN_PERSON, VGM_REF_NBR, ");
			sb.append(" TO_CHAR(VGM_ACQ_DTTM, 'yyyymmddhh24mi') VGM_ACQ_DTTM FROM CNTR_VGM WHERE ");
			sb.append(" CNTR_SEQ_NBR =:containerSeqNo ");

			log.info(" ***setCntrVgm SQL *****" + sb.toString());
			paramMap.put("containerSeqNo", containerVO.getContainerSeqNo());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				containerVO.setVgmInd(CommonUtility.deNull(rs.getString("VGM_IND")));
				containerVO.setWeighMethodCd(CommonUtility.deNull(rs.getString("WEIGH_METHOD_CD")));
				containerVO.setVgmSignPerson(CommonUtility.deNull(rs.getString("VGM_SIGN_PERSON")));
				containerVO.setVgmRefNbr(CommonUtility.deNull(rs.getString("VGM_REF_NBR")));
				containerVO.setVgmAcqDttm(CommonUtility.deNull(rs.getString("VGM_ACQ_DTTM")));
			}
			log.info("END: *** setCntrVgm Result ");
		} catch (NullPointerException e) {
			log.info("Exception setCntrVgm :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception setCntrVgm :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: setCntrVgm  DAO  END");
		}
	}

	// EndRegion ContainerDataEJB

	// psaLink.util-->PsaLinkCommonUtility
	// method: getPsaFormattedCntrNbr()
	/**
	 * This method returns PSA formatted container number. The format is 4A+1b+8N.
	 *
	 * @param cntrNbr container number
	 * @return String PSA formatted container number
	 */
	public String getPsaFormattedCntrNbr(String cntrNbr) {
		cntrNbr = cntrNbr.trim();

		String numeric = "";
		String alpha = "";

		if (cntrNbr == null || cntrNbr.length() == 0)
			return cntrNbr;
		if (cntrNbr.length() == 13)
			return cntrNbr;

		if (containsOnlyNumbers(cntrNbr)) {
			if (cntrNbr.length() <= 8) {
				// 5b + cntrNbr
				return "     " + cntrNbr;
			} else {
				return "     " + cntrNbr.substring(0, 8);
			}
		} else {
			alpha = getFirstAlphaPart(cntrNbr).trim();
			numeric = cntrNbr.substring(alpha.length(), cntrNbr.length());
			if (!containsOnlyNumbers(numeric)) {
				return cntrNbr;
			}
			if (alpha.length() <= 4) {
				alpha = CommonUtility.rPad(alpha, 5, " ");
				// return alpha + numeric;
			} else {
				alpha = alpha.substring(0, 4) + " ";
				// return alpha + numeric;
			}
			if (numeric.length() <= 8) {
				return alpha + numeric;
			} else {
				return alpha + numeric.substring(0, 8);
			}
		}
	}

	// psaLink.util-->PsaLinkCommonUtility
	// method: containsOnlyNumbers()
	private static boolean containsOnlyNumbers(String toCheck) {
		for (int n = 0; n < toCheck.length(); n++) {
			if (!Character.isDigit(toCheck.charAt(n))) {
				return false;
			}
		}
		return true;
	}

	// psaLink.util-->PsaLinkCommonUtility
	// method: getFirstAlphaPart()
	private static String getFirstAlphaPart(String alphaNumericStr) {
		String alpha = "";
		for (int n = 0; n < alphaNumericStr.length(); n++) {
			if (!Character.isDigit(alphaNumericStr.charAt(n))) {
				alpha += alphaNumericStr.charAt(n);
			} else {
				break;
			}
		}
		return alpha;
	}

}
