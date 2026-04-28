package sg.com.jp.generalcargo.dao.impl;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.ContainerCommonFunctionRepo;
import sg.com.jp.generalcargo.dao.ContainerDataRepo;
import sg.com.jp.generalcargo.dao.UAOpsRepository;
import sg.com.jp.generalcargo.domain.ContainerValueObject;
import sg.com.jp.generalcargo.domain.GcOpsUaReport;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.domain.UaListObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("UAOpsRepository")
public class UAOpsJdbcRepository implements UAOpsRepository {

	// jp.src.ejb.sessionBeans.gbms.ops.dnua.ua-->UAEJB
	private static final Log log = LogFactory.getLog(UAOpsJdbcRepository.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	private ContainerDataRepo containerData;
	@Autowired
	private ContainerCommonFunctionRepo containerCommonFunction;

	@Override
	public List<UaEsnDetValueObject> getUAViewPrint(String uANbr, String esnasnnbr, String transtype)
			throws BusinessException {

		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String sql = "";
		String uasql = "";
		// String uavehsql = "";
		SqlRowSet rs = null;
		String sql4 = "";
		List<UaEsnDetValueObject> esnList = new ArrayList<UaEsnDetValueObject>();
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();

		// Added by SONLT
		// Check whether container associate with esn
		try {
			log.info("START: getUAViewPrint dao  uANbr: " + CommonUtility.deNull(uANbr) + " esnasnnbr: "
					+ CommonUtility.deNull(esnasnnbr) + " transtype: " + CommonUtility.deNull(transtype));
			if (checkESNCntr(esnasnnbr)) {
				sb.setLength(0);
				sb.append(" SELECT to_char(atb_dttm,'dd/mm/yyyy hh24:mi') as atb,  vsl_nm, vessel_call.out_voy_nbr, ");
				sb.append(" esn_details.esn_wt as dec_wt,esn_details.esn_vol as dec_vol, ");
				sb.append(" esn_details.nbr_pkgs as dec_qty, bk_details.CNTR_TYPE as cntr_type, ");
				sb.append(" bk_details.CNTR_SIZE as cntr_size, markings , ");
				sb.append(" esn_details.crg_des ,ua_details.cntr_nbr as cont_no, ");
				sb.append(
						" esn.esn_asn_nbr  as esn_asn ,esn.bk_ref_nbr,esn_details.nbr_pkgs-esn_details.ua_nbr_pkgs, ");
				sb.append(
						" to_char(GB_COD_DTTM,'dd/mm/yyyy hh24:mi') as cod,esn_details.acct_nbr,vessel_call.vv_cd,to_char(ETB_DTTM,'dd/mm/yyyy hh24:mi') as etb,to_char(VSL_BERTH_DTTM,'dd/mm/yyyy hh24:mi') as btr ");
				sb.append(
						" , vessel_call.terminal, vessel_call.scheme, vessel_call.combi_gc_scheme,  vessel_call.combi_gc_ops_ind ");
				sb.append(
						" from  esn , vessel_call,berthing , esn_details , bk_details , esn_markings , esn_cntr, ua_details ");
				sb.append(" where esn.esn_asn_nbr = esn_details.esn_asn_nbr ");
				sb.append(" and esn.esn_asn_nbr = esn_details.esn_asn_nbr ");
				sb.append(" and esn.out_voy_var_nbr = vessel_call.vv_cd ");
				sb.append(" and esn.esn_asn_nbr = esn_markings.esn_asn_nbr ");
				sb.append(" and esn.esn_asn_nbr = esn_cntr.esn_asn_nbr(+) ");
				sb.append(" and vessel_call.vv_cd = berthing.vv_cd and  ");
				sb.append(" esn.bk_ref_nbr = bk_details.bk_ref_nbr and shift_ind=1 and esn.TRANS_CRG<>'Y' AND ");
				sb.append(" esn.esn_asn_nbr = :esnasnnbr ");
				sb.append(" and ua_details.ua_nbr =:UANbr");

				sql1 = sb.toString();

				sb.setLength(0);
				sb.append(" SELECT to_char(atb_dttm,'dd/mm/yyyy hh24:mi') as atb, vsl_nm, vessel_call.out_voy_nbr, ");
				sb.append(" tesn_psa_jp.gross_wt as dec_wt,tesn_psa_jp.gross_vol as dec_vol, ");
				sb.append(" tesn_psa_jp.nbr_pkgs as dec_qty, bk_details.CNTR_TYPE as cntr_type, ");
				sb.append(" bk_details.CNTR_SIZE as cntr_size, markings , ");
				sb.append(" tesn_psa_jp.crg_des ,  ua_details.cntr_nbr as cont_no , ");
				sb.append(
						" esn.esn_asn_nbr  as esn_asn, esn.bk_ref_nbr as bk_ref,tesn_psa_jp.nbr_pkgs-tesn_psa_jp.ua_nbr_pkgs,  ");
				sb.append(
						" to_char(GB_COD_DTTM,'dd/mm/yyyy hh24:mi') as cod,tesn_psa_jp.acct_nbr ,vessel_call.vv_cd,to_char(ETB_DTTM,'dd/mm/yyyy hh24:mi') as etb,to_char(VSL_BERTH_DTTM,'dd/mm/yyyy hh24:mi') as btr ");
				sb.append(
						" , vessel_call.terminal, vessel_call.scheme, vessel_call.combi_gc_scheme,  vessel_call.combi_gc_ops_ind ");
				sb.append(" from  esn , vessel_call,berthing , tesn_psa_jp , bk_details , ");
				sb.append(" esn_markings , esn_cntr, ua_details where  esn.esn_asn_nbr = tesn_psa_jp.esn_asn_nbr ");
				sb.append(" and esn.out_voy_var_nbr = vessel_call.vv_cd ");
				sb.append(" and esn.esn_asn_nbr = esn_markings.esn_asn_nbr ");
				sb.append(" and esn.esn_asn_nbr = esn_cntr.esn_asn_nbr(+) ");
				sb.append(" and vessel_call.vv_cd = berthing.vv_cd and  ");
				sb.append(" esn.bk_ref_nbr = bk_details.bk_ref_nbr ");
				sb.append(" and esn.trans_type='C' and shift_ind=1 and esn.TRANS_CRG<>'Y' AND ");
				sb.append(" esn.esn_asn_nbr =:esnasnnbr ");
				sb.append(" and ua_details.ua_nbr =:UANbr");

				sql2 = sb.toString();

				sb.setLength(0);
				sb.append(" select to_char(atb_dttm,'dd/mm/yyyy hh24:mi') as atb , vsl_nm, vessel_call.out_voy_nbr, ");
				sb.append(" tesn_jp_jp.nom_wt as dec_wt,tesn_jp_jp.nom_vol as dec_vol, ");
				sb.append(" tesn_jp_jp.nbr_pkgs as dec_qty, manifest_details.CNTR_TYPE as cntr_type, ");
				sb.append(" manifest_details.CNTR_SIZE as cntr_size, mft_markings , ");
				sb.append(" manifest_details.crg_des , ua_details.cntr_nbr as cont_no , ");
				sb.append(
						" esn.esn_asn_nbr  as esn_asn, esn.bk_ref_nbr as bk_ref,tesn_jp_jp.nbr_pkgs-tesn_jp_jp.ua_nbr_pkgs, ");
				sb.append(
						" to_char(GB_COD_DTTM,'dd/mm/yyyy hh24:mi') as cod,tesn_jp_jp.acct_nbr ,vessel_call.vv_cd,to_char(ETB_DTTM,'dd/mm/yyyy hh24:mi') as etb,to_char(VSL_BERTH_DTTM,'dd/mm/yyyy hh24:mi') as btr ");
				sb.append(
						" , vessel_call.terminal, vessel_call.scheme, vessel_call.combi_gc_scheme,  vessel_call.combi_gc_ops_ind ");
				sb.append(" from  esn , vessel_call,berthing , tesn_jp_jp , gb_edo , ");
				sb.append(" manifest_details ,mft_markings ,bl_cntr_details, ua_details ");
				sb.append(" where  esn.esn_asn_nbr = tesn_jp_jp.esn_asn_nbr ");
				sb.append(" and esn.out_voy_var_nbr = vessel_call.vv_cd ");
				sb.append(" and tesn_jp_jp.edo_asn_nbr = gb_edo.edo_asn_nbr ");
				sb.append(" and gb_edo.mft_seq_nbr = manifest_details.mft_seq_nbr ");
				sb.append(" and manifest_details.MFT_SEQ_NBR = mft_markings.MFT_SQ_NBR ");
				sb.append(" and manifest_details.MFT_SEQ_NBR = bl_cntr_details.MFT_SEQ_NBR(+) ");
				sb.append(
						" and vessel_call.vv_cd = berthing.vv_cd and esn.trans_type='A' and shift_ind=1 and esn.TRANS_CRG<>'Y' AND ");
				sb.append(" esn.esn_asn_nbr =:esnasnnbr  ");
				sb.append(" and ua_details.ua_nbr =:UANbr");
				sql3 = sb.toString();

				sb.setLength(0);
				sb.append(" SELECT to_char(atb_dttm,'dd/mm/yyyy hh24:mi') as atb, vsl_nm, vessel_call.out_voy_nbr, ");
				sb.append(" SS.SS_WT as dec_wt,SS.SS_VOL as dec_vol, ");
				sb.append(" SS.nbr_pkgs as dec_qty, '','', ");
				sb.append(" markings , ");
				sb.append(" SS.CRG_DES ,  ua_details.cntr_nbr as cont_no, ");
				sb.append(" esn.esn_asn_nbr  as esn_asn,SS.ss_ref_nbr,SS.nbr_pkgs-SS.ua_nbr_pkgs,  ");
				sb.append(
						" to_char(GB_COD_DTTM,'dd/mm/yyyy hh24:mi') as cod,SS.acct_nbr ,vessel_call.vv_cd,to_char(ETB_DTTM,'dd/mm/yyyy hh24:mi') as etb,to_char(VSL_BERTH_DTTM,'dd/mm/yyyy hh24:mi') as btr ");
				sb.append(
						" , vessel_call.terminal, vessel_call.scheme, vessel_call.combi_gc_scheme,  vessel_call.combi_gc_ops_ind ");
				sb.append(" from  esn , vessel_call,berthing , ss_details SS, ");
				sb.append(" esn_markings, ua_details  where  esn.esn_asn_nbr = SS.esn_asn_nbr ");
				sb.append(" and esn.out_voy_var_nbr = vessel_call.vv_cd ");
				sb.append(" and esn.esn_asn_nbr = esn_markings.esn_asn_nbr ");
				sb.append(" and vessel_call.vv_cd = berthing.vv_cd ");
				sb.append(" and esn.trans_type='S' and shift_ind=1 and esn.TRANS_CRG<>'Y' AND ");
				sb.append(" esn.esn_asn_nbr =:esnasnnbr  ");
				sb.append(" and ua_details.ua_nbr =:UANbr");
				sql4 = sb.toString();
				// End-------------
			} else {
				sb.setLength(0);
				sb.append(" SELECT to_char(atb_dttm,'dd/mm/yyyy hh24:mi') as atb,  vsl_nm, vessel_call.out_voy_nbr, ");
				sb.append(" esn_details.esn_wt as dec_wt,esn_details.esn_vol as dec_vol, ");
				sb.append(" esn_details.nbr_pkgs as dec_qty, bk_details.CNTR_TYPE as cntr_type, ");
				sb.append(" bk_details.CNTR_SIZE as cntr_size, markings , ");
				sb.append(" esn_details.crg_des ,cntr_nbr as cont_no, ");
				sb.append(
						" esn.esn_asn_nbr  as esn_asn ,esn.bk_ref_nbr,esn_details.nbr_pkgs-esn_details.ua_nbr_pkgs, ");
				sb.append(
						" to_char(GB_COD_DTTM,'dd/mm/yyyy hh24:mi') as cod,esn_details.acct_nbr,vessel_call.vv_cd,to_char(ETB_DTTM,'dd/mm/yyyy hh24:mi') as etb,to_char(VSL_BERTH_DTTM,'dd/mm/yyyy hh24:mi') as btr ");
				sb.append(
						" , vessel_call.terminal, vessel_call.scheme, vessel_call.combi_gc_scheme,  vessel_call.combi_gc_ops_ind ");
				sb.append(" from  esn , vessel_call,berthing , esn_details , bk_details , esn_markings , esn_cntr ");
				sb.append(" where esn.esn_asn_nbr = esn_details.esn_asn_nbr ");
				sb.append(" and esn.esn_asn_nbr = esn_details.esn_asn_nbr ");
				sb.append(" and esn.out_voy_var_nbr = vessel_call.vv_cd ");
				sb.append(" and esn.esn_asn_nbr = esn_markings.esn_asn_nbr ");
				sb.append(" and esn.esn_asn_nbr = esn_cntr.esn_asn_nbr(+) ");
				sb.append(" and vessel_call.vv_cd = berthing.vv_cd and  ");
				sb.append(" esn.bk_ref_nbr = bk_details.bk_ref_nbr and shift_ind=1 and esn.TRANS_CRG<>'Y' AND ");
				sb.append(" esn.esn_asn_nbr =:esnasnnbr");
				sql1 = sb.toString();

				sb.setLength(0);
				sb.append(" SELECT to_char(atb_dttm,'dd/mm/yyyy hh24:mi') as atb, vsl_nm, vessel_call.out_voy_nbr, ");
				sb.append(" tesn_psa_jp.gross_wt as dec_wt,tesn_psa_jp.gross_vol as dec_vol, ");
				sb.append(" tesn_psa_jp.nbr_pkgs as dec_qty, bk_details.CNTR_TYPE as cntr_type, ");
				sb.append(" bk_details.CNTR_SIZE as cntr_size, markings , ");
				sb.append(" tesn_psa_jp.crg_des ,  cntr_nbr as cont_no , ");
				sb.append(
						" esn.esn_asn_nbr  as esn_asn, esn.bk_ref_nbr as bk_ref,tesn_psa_jp.nbr_pkgs-tesn_psa_jp.ua_nbr_pkgs,  ");
				sb.append(
						" to_char(GB_COD_DTTM,'dd/mm/yyyy hh24:mi') as cod,tesn_psa_jp.acct_nbr ,vessel_call.vv_cd,to_char(ETB_DTTM,'dd/mm/yyyy hh24:mi') as etb,to_char(VSL_BERTH_DTTM,'dd/mm/yyyy hh24:mi') as btr ");
				sb.append(
						" , vessel_call.terminal, vessel_call.scheme, vessel_call.combi_gc_scheme,  vessel_call.combi_gc_ops_ind ");
				sb.append(" from  esn , vessel_call,berthing , tesn_psa_jp , bk_details , ");
				sb.append(" esn_markings , esn_cntr where  esn.esn_asn_nbr = tesn_psa_jp.esn_asn_nbr ");
				sb.append(" and esn.out_voy_var_nbr = vessel_call.vv_cd ");
				sb.append(" and esn.esn_asn_nbr = esn_markings.esn_asn_nbr ");
				sb.append(" and esn.esn_asn_nbr = esn_cntr.esn_asn_nbr(+) ");
				sb.append(" and vessel_call.vv_cd = berthing.vv_cd and  ");
				sb.append(" esn.bk_ref_nbr = bk_details.bk_ref_nbr ");
				sb.append(" and esn.trans_type='C' and shift_ind=1 and esn.TRANS_CRG<>'Y' AND ");
				sb.append(" esn.esn_asn_nbr =:esnasnnbr");
				sql2 = sb.toString();

				sb.setLength(0);
				sb.append(" select to_char(atb_dttm,'dd/mm/yyyy hh24:mi') as atb , vsl_nm, vessel_call.out_voy_nbr, ");
				sb.append(" tesn_jp_jp.nom_wt as dec_wt,tesn_jp_jp.nom_vol as dec_vol, ");
				sb.append(" tesn_jp_jp.nbr_pkgs as dec_qty, manifest_details.CNTR_TYPE as cntr_type, ");
				sb.append(" manifest_details.CNTR_SIZE as cntr_size, mft_markings , ");
				sb.append(" manifest_details.crg_des , bl_cntr_details.cntr_nbr as cont_no , ");
				sb.append(
						" esn.esn_asn_nbr  as esn_asn, esn.bk_ref_nbr as bk_ref,tesn_jp_jp.nbr_pkgs-tesn_jp_jp.ua_nbr_pkgs, ");
				sb.append(
						" to_char(GB_COD_DTTM,'dd/mm/yyyy hh24:mi') as cod,tesn_jp_jp.acct_nbr ,vessel_call.vv_cd,to_char(ETB_DTTM,'dd/mm/yyyy hh24:mi') as etb,to_char(VSL_BERTH_DTTM,'dd/mm/yyyy hh24:mi') as btr ");
				sb.append(
						" , vessel_call.terminal, vessel_call.scheme, vessel_call.combi_gc_scheme,  vessel_call.combi_gc_ops_ind ");
				sb.append(" from  esn , vessel_call,berthing , tesn_jp_jp , gb_edo , ");
				sb.append(" manifest_details ,mft_markings ,bl_cntr_details ");
				sb.append(" where  esn.esn_asn_nbr = tesn_jp_jp.esn_asn_nbr ");
				sb.append(" and esn.out_voy_var_nbr = vessel_call.vv_cd ");
				sb.append(" and tesn_jp_jp.edo_asn_nbr = gb_edo.edo_asn_nbr ");
				sb.append(" and gb_edo.mft_seq_nbr = manifest_details.mft_seq_nbr ");
				sb.append(" and manifest_details.MFT_SEQ_NBR = mft_markings.MFT_SQ_NBR ");
				sb.append(" and manifest_details.MFT_SEQ_NBR = bl_cntr_details.MFT_SEQ_NBR(+) ");
				sb.append(
						" and vessel_call.vv_cd = berthing.vv_cd and esn.trans_type='A' and shift_ind=1 and esn.TRANS_CRG<>'Y' AND ");
				sb.append(" esn.esn_asn_nbr =:esnasnnbr");
				sql3 = sb.toString();

				sb.setLength(0);
				sb.append(" SELECT to_char(atb_dttm,'dd/mm/yyyy hh24:mi') as atb, vsl_nm, vessel_call.out_voy_nbr, ");
				sb.append(" SS.SS_WT as dec_wt,SS.SS_VOL as dec_vol, ");
				sb.append(" SS.nbr_pkgs as dec_qty, '','', ");
				sb.append(" markings , ");
				sb.append(" SS.CRG_DES ,  '', ");
				sb.append(" esn.esn_asn_nbr  as esn_asn,SS.ss_ref_nbr,SS.nbr_pkgs-SS.ua_nbr_pkgs,  ");
				sb.append(
						" to_char(GB_COD_DTTM,'dd/mm/yyyy hh24:mi') as cod,SS.acct_nbr ,vessel_call.vv_cd,to_char(ETB_DTTM,'dd/mm/yyyy hh24:mi') as etb,to_char(VSL_BERTH_DTTM,'dd/mm/yyyy hh24:mi') as btr ");
				sb.append(
						" , vessel_call.terminal, vessel_call.scheme, vessel_call.combi_gc_scheme,  vessel_call.combi_gc_ops_ind ");
				sb.append(" from  esn , vessel_call,berthing , ss_details SS, ");
				sb.append(" esn_markings  where  esn.esn_asn_nbr = SS.esn_asn_nbr ");
				sb.append(" and esn.out_voy_var_nbr = vessel_call.vv_cd ");
				sb.append(" and esn.esn_asn_nbr = esn_markings.esn_asn_nbr ");
				sb.append(" and vessel_call.vv_cd = berthing.vv_cd ");
				sb.append(" and esn.trans_type='S' and shift_ind=1 and esn.TRANS_CRG<>'Y' AND ");
				sb.append(" esn.esn_asn_nbr =:esnasnnbr");
				sql4 = sb.toString();
			}

			// Get the truck number from UA_DETAILS. 26/5/2010.
//	    uasql = "SELECT TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')AS T_DTTM,NBR_PKGS,DP_IC_NBR,DP_IC_TYPE,DP_NM,BILLABLE_TON FROM UA_DETAILS WHERE UA_NBR='" + UANbr + "'";
			uasql = "SELECT TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')AS T_DTTM,NBR_PKGS,DP_IC_NBR,DP_IC_TYPE,DP_NM,BILLABLE_TON, TRUCK_NBR,TO_CHAR(UA_CREATE_DTTM,'DD/MM/YYYY HH24:MI')AS UA_DTTM FROM UA_DETAILS WHERE UA_NBR= :UANbr ";
			// uavehsql = "SELECT * FROM UA_VEH WHERE UA_NBR='" + UANbr + "'";

			paramMap.put("esnasnnbr", esnasnnbr);
			paramMap.put("UANbr", uANbr);

			if (transtype != null && !transtype.equals("") && transtype.equals("E")) {
				sql = sql1;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("C")) {
				sql = sql2;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("A")) {
				sql = sql3;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("S")) {
				sql = sql4;
			}
			
			log.info(" *** getUAViewPrint SQL *****" + sql);
			log.info(" *** getUAViewPrint paramMap *****" + paramMap);
			
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			UaEsnDetValueObject esnObj = new UaEsnDetValueObject();

			while (rs.next()) {
				esnObj.setAtb(CommonUtility.deNull(rs.getString(1)));
				esnObj.setVessel_name(CommonUtility.deNull(rs.getString(2)));
				esnObj.setOut_voy_nbr(CommonUtility.deNull(rs.getString(3)));
				esnObj.setWeight(CommonUtility.deNull(rs.getString(4)));
				esnObj.setVolume(CommonUtility.deNull(rs.getString(5)));
				esnObj.setDecl_pkg(CommonUtility.deNull(rs.getString(6)));
				esnObj.setConttype(CommonUtility.deNull(rs.getString(7)));
				esnObj.setContsize(CommonUtility.deNull(rs.getString(8)));
				esnObj.setCargo_markings(CommonUtility.deNull(rs.getString(9)));
				esnObj.setCargo_desc(CommonUtility.deNull(rs.getString(10)));
				esnObj.setContno(CommonUtility.deNull(rs.getString(11)));
				esnObj.setEsn_asn_nbr(CommonUtility.deNull(rs.getString(12)));
				esnObj.setBk_ref_nbr(CommonUtility.deNull(rs.getString(13)));
				esnObj.setBal_pkg(CommonUtility.deNull(rs.getString(14)));
				esnObj.setCod(CommonUtility.deNull(rs.getString(15)));
				esnObj.setAct_no(CommonUtility.deNull(rs.getString(16)));
				esnObj.setVvCode(CommonUtility.deNull(rs.getString(17)));
				esnObj.setTrans_type(transtype);
				esnObj.setEtb(CommonUtility.deNull(rs.getString("etb")));
				esnObj.setBtr(CommonUtility.deNull(rs.getString("btr")));
				esnObj.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				esnObj.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				esnObj.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				esnObj.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
			}
			
			log.info(" *** getUAViewPrint SQL *****" + uasql);
			log.info(" *** getUAViewPrint paramMap *****" + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(uasql, paramMap);
			while (rs.next()) {
				esnObj.setDate_time(CommonUtility.deNull(rs.getString(1)));
				esnObj.setUanbrpkgs(CommonUtility.deNull(rs.getString(2)));
				esnObj.setNric_no(CommonUtility.deNull(rs.getString(3)));
				esnObj.setIctype(CommonUtility.deNull(rs.getString(4)));
				esnObj.setDpname(CommonUtility.deNull(rs.getString(5)));
				esnObj.setBilltons(CommonUtility.deNull(rs.getString(6)));
				// Get the truck number from UA_DETAILS. 26/5/2010.
				esnObj.setVeh1(CommonUtility.deNull(rs.getString(7)));
				esnObj.setUadate_time(CommonUtility.deNull(rs.getString(8)));
			}

			esnList.add(esnObj);

		} catch (NullPointerException e) {
			log.info("Exception: getUAViewPrint ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: getUAViewPrint ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getUAViewPrint  END *****");
		}
		return esnList;
	}

	private boolean checkESNCntr(String esnasn) throws BusinessException {

		String sql = "";
		StringBuffer sb = new StringBuffer();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: checkESNCntr dao  esnasn: " + CommonUtility.deNull(esnasn));
			sb.append("select a.cntr_seq_nbr, b.cntr_nbr from esn_mot_cntr a, cntr b  where a.esn_asn_nbr = :esnasn ");
			sb.append(" and a.cntr_seq_nbr = b.cntr_seq_nbr ");
			sb.append(" and b.txn_status <> 'D' and b.txn_status <> 'I'");

			sql = sb.toString();
			log.info("START: checkESNCntr  DAO  Start Obj ");
			log.info(" *** checkESNCntr SQL *****" + sql);
			paramMap.put("esnasn", esnasn);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {

				return true;
			}
			log.info("END: *** checkESNCntr Result *****");
			return false;

		} catch (NullPointerException ne) {
			log.info("Exception checkESNCntr : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkESNCntr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkESNCntr  DAO  END");
		}

	}

	@Override
	/**
	 * This method is to check if the trans_type is 'A' (JP to JP)
	 * 
	 * @param esnasnnbr
	 * @return
	 * @throws BusinessException Created by Babatunde on Dec., 2013
	 */
	public boolean isTESN_JP_JP(String esnNbr) throws BusinessException {
		boolean isJP = false;
		String sql = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: isTESN_JP_JP  DAO  Start esnasnnbr:" + CommonUtility.deNull(esnNbr));

			sql = "select * from TESN_JP_JP where ESN_ASN_NBR= :esnNbr";
			paramMap.put("esnNbr", esnNbr);

			log.info(" *** isTESN_JP_JP SQL *****" + sql + " paramMap " + paramMap.toString());
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				isJP = true;
			} else {
				isJP = false;
			}
			
			log.info("END: *** isTESN_JP_JP  isJP*****" + isJP);
		} catch (NullPointerException e) {
			log.info("Exception: isTESN_JP_JP ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: isTESN_JP_JP ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** isTESN_JP_JP  END *****");
		}

		return isJP;
	}

	@Override
	/**
	 * This method is to check if shipment is closed for a bkRef
	 * 
	 * @param bkRef
	 * @return
	 * @throws BusinessException
	 * @author Babatunde Created on Jan 23, 2014
	 */
	public boolean isClosedShipment(String bkRef) throws BusinessException {
		boolean isClosed = false;
		String gb_close_shp_ind = "";
		String sql = "";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		sql = "select gb_close_shp_ind from bk_details where bk_ref_nbr = :bkRef ";

		try {
			log.info("START: isClosedShipment  DAO  Start bkRef:" + CommonUtility.deNull(bkRef));
			paramMap.put("bkRef", bkRef);

			log.info(" *** isClosedShipment SQL *****" + sql + " paramMap " + paramMap.toString());
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				gb_close_shp_ind = rs.getString("gb_close_shp_ind");
				if ("Y".equalsIgnoreCase(gb_close_shp_ind)) {
					isClosed = true;
				} else if ("N".equalsIgnoreCase(gb_close_shp_ind)) {
					isClosed = false;
				}
			}
			log.info("END: *** isClosedShipment  isClosed *****" + isClosed);
		} catch (NullPointerException e) {
			log.info("Exception: isClosedShipment ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: isClosedShipment ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** isClosedShipment  END *****");
		}
		return isClosed;
	}

	@Override
	/**
	 * This method to check if Booking Reference no is created after shipment reopen
	 * date and time If yes, UA can be created provided that other conditions are
	 * met.
	 * 
	 * @param esnasnnbr
	 * @return
	 * @throws BusinessException Created by Dongsheng on 5/6/2012
	 */
	public boolean checkBKCreatedAfterSHPReopen(String esnasnnbr) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = null;
		String sql2 = null;
		String shp_reopen_dttm = "";
		String bk_date = "";
		long shpReopenInt;
		long bkDateInt;
		boolean createUAAllowed = true;

		try {

			log.info("START: checkBKCreatedAfterSHPReopen  DAO  Start esnasnnbr:" + CommonUtility.deNull(esnasnnbr));

			// To get the latest shipment reopen date to compare against Booking Reference
			// no creation date and time
			StringBuffer sb = new StringBuffer();

			sb.append("select to_char(max(action_dttm) ,'YYYYMMDDHH24MISS') as shpReopenDate from ACTION_LOG ");
			sb.append(
					" where action_cd = 'SHO' and ref_nbr in (select out_voy_var_nbr from esn where esn_asn_nbr = :esnasnnbr)");

			sql = sb.toString();
			paramMap.put("esnasnnbr", esnasnnbr);
			log.info(" *** checkBKCreatedAfterSHPReopen SQL *****" + sql + " paramMap " + paramMap.toString());
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				shp_reopen_dttm = rs.getString("shpReopenDate");
			}

			sql2 = "select to_char(last_modify_dttm,'YYYYMMDDHH24MISS') as bkDate from bk_details where bk_ref_nbr in (select bk_ref_nbr from esn where esn_asn_nbr = :esnasnnbr)";
			log.info("-- UAEJB.checkBKCreatedAfterSHPReopen sql2 is: " + sql2);
			paramMap.put("esnasnnbr", esnasnnbr);
			log.info(" *** checkBKCreatedAfterSHPReopen SQL *****" + sql2 + " paramMap " + paramMap.toString());
			SqlRowSet rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);
			while (rs2.next()) {
				bk_date = rs2.getString("bkDate");
			}

			if (shp_reopen_dttm != null && !shp_reopen_dttm.equals("")) {
				// log.info("-- UAEJB.checkBKCreatedAfterSHPReopen to convert string to
				// integer.");
				shpReopenInt = Long.parseLong(shp_reopen_dttm);
				log.info("-- UAEJB.checkBKCreatedAfterSHPReopen shpReopenInt is: " + shpReopenInt + "");
				bkDateInt = Long.parseLong(bk_date);
				log.info("-- UAEJB.checkBKCreatedAfterSHPReopen bkDateInt is: " + bkDateInt + "");
				int cmp = (new Long(bkDateInt)).compareTo(new Long(shpReopenInt));
				if (cmp > 0) { // bkDateInt>shpReopenInt
					createUAAllowed = true;
				} else {
					// BK created before shipment was re-opened
					log.info("-- UAEJB.checkBKCreatedAfterSHPReopen. old Booking Reference No!");
					createUAAllowed = false;
				}
			} else {
				// Prior to checkBKCreatedAfterSHPReopen, system has checked if
				// GB_CLOSE_SHP_IND='Y' (method chkVslStat).
				// Only if GB_CLOSE_SHP_IND='N', system will come to
				// checkBKCreatedAfterSHPReopen
				// So if no SHO action found, it simply means shipment was never closed before.
				// UA creation is allowed.
				log.info("-- UAEJB.checkBKCreatedAfterSHPReopen no SHO found!");
				createUAAllowed = true;
			}
			
			log.info("END: *** checkBKCreatedAfterSHPReopen  createUAAllowed*****" + createUAAllowed);

		} catch (NullPointerException e) {
			log.info("Exception: checkBKCreatedAfterSHPReopen ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: checkBKCreatedAfterSHPReopen ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** checkBKCreatedAfterSHPReopen  END *****");
		}

		return createUAAllowed;

	}

	@Override
	public List<String[]> getCntrNbr(String esnasn) throws BusinessException {
		String sql = "";
		List<String[]> cntrNbr = new ArrayList<String[]>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		sql = " select a.cntr_seq_nbr, b.cntr_nbr from esn_mot_cntr a, cntr b  where a.esn_asn_nbr = :esnasn  and a.cntr_seq_nbr = b.cntr_seq_nbr  and b.txn_status <> 'D' and b.txn_status <> 'I'";
		try {
			log.info("START: getCntrNbr  DAO  Start esnasn:" + CommonUtility.deNull(esnasn));

			log.info(" *** getCntrNbr SQL *****" + sql + " paramMap " + paramMap.toString());

			paramMap.put("esnasn", esnasn);
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				String[] arr = new String[2];
				arr[0] = CommonUtility.deNull(rs.getString("cntr_seq_nbr"));
				arr[1] = CommonUtility.deNull(rs.getString("cntr_nbr"));
				cntrNbr.add(arr);
			}

			return cntrNbr;
		} catch (NullPointerException e) {
			log.info("Exception: getCntrNbr ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: getCntrNbr ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getCntrNbr  END *****");
		}
	}

	@Override
	/**
	 * Method is used to check the status of the esn.
	 * 
	 * @param esnasn ESN_ASN_NBR
	 * @return true if Stuff indicator is 'Y' else false.
	 * @throws RemoteException
	 * @throws BusinessException
	 */
	public boolean checkEsnStuffIndicator(String esnasn) throws BusinessException {
		boolean returnvalue = false;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: checkEsnStuffIndicator  DAO  Start esnasn:" + CommonUtility.deNull(esnasn));

			sb.append("select ");
			sb.append("trim(decode(stuff_ind,'Y','true','N','false')) stuffind ");
			sb.append(" from ");
			sb.append("GBMS.esn ");
			sb.append(" where ");
			sb.append("esn_status='A' ");
			sb.append(" and ");
			sb.append(" esn_asn_nbr=:esnasn ");

			paramMap.put("esnasn", esnasn);
			log.info(" ***checkEsnStuffIndicator SQL *****" + sb.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				String temp = rs.getString("stuffind");
				if (temp.equalsIgnoreCase("true"))
					returnvalue = true;
				else
					returnvalue = false;
			}

		} catch (NullPointerException e) {
			log.info("Exception checkEsnStuffIndicator : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkEsnStuffIndicator : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkEsnStuffIndicator  DAO  END");
		}
		return returnvalue;
	}

	@Override
	/**
	 * This method is to check valid IC Number
	 * 
	 * @param NRIC_NBR
	 * @return
	 * @throws BusinessException
	 * @throws RemoteException
	 * @author Tungnm3 Created on Jan 23, 2014
	 */
	public String getCustCdByIcNbr(String type, String nric_nbr) throws BusinessException {
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String cust_cd = "";

		if (!StringUtils.equalsIgnoreCase("F", type)) {
			sql = "SELECT DISTINCT NVL(CUST_CD, SUBSTR(CUST_NAME, 1, 9)) cust_cd  FROM JC_CARDDTL  WHERE (ID_NO = :nric_nbr   OR CARD_SERIALNO = :nric_nbr OR PASSPORT_NO = :nric_nbr) and expiry_dt >= sysdate";
			// if (!StringUtils.equalsIgnoreCase("P", type)) {
			// sql= sql +" AND STATUS_CD IN ('USE','SUS') ";
			// }

		} else {
			sql = "SELECT DISTINCT NVL(CUST_CD, SUBSTR(CUST_NAME, 1, 9)) cust_cd FROM JC_CARDDTL  WHERE STATUS_CD IN ('USE','SUS') and (Id_no = :nric_nbr or fin_no= :nric_nbr ) and expiry_dt >= sysdate";
		}

		try {
			log.info("START: getCustCdByIcNbr  DAO  Start type:" + CommonUtility.deNull(type) + " nric_nbr"
					+ CommonUtility.deNull(nric_nbr));

			paramMap.put("nric_nbr", nric_nbr);
			log.info(" *** getCustCdByIcNbr SQL *****" + sql + " paramMap " + paramMap.toString());
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				cust_cd = rs.getString("cust_cd");
			}
		} catch (NullPointerException e) {
			log.info("Exception: getCustCdByIcNbr ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: getCustCdByIcNbr ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getCustCdByIcNbr  END *****");
		}
		return cust_cd;
	}

	@Override
	public void updateUA(String uanbr, String cntrNo) throws BusinessException {
		String sql = "update ua_details set CNTR_NBR = :cntrNo, MOT_CREATE_DTTM = :date where ua_nbr = :uanbr";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			// conn = DbConnectionFactory.getInstance().getConnection();

			log.info("START: updateUA  DAO  Start uanbr:" + CommonUtility.deNull(uanbr) + " cntrNo"
					+ CommonUtility.deNull(cntrNo));

			paramMap.put("cntrNo", cntrNo);
			paramMap.put("date", new Timestamp(new java.util.Date().getTime()));
			paramMap.put("uanbr", uanbr);
			log.info(" *** updateUA SQL *****" + sql + " paramMap " + paramMap.toString());
			namedParameterJdbcTemplate.update(sql, paramMap);
		} catch (NullPointerException e) {
			log.info("Exception: updateUA ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: updateUA ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** updateUA  END *****");
		}
	}

	@Override
	public int checkFirstUA(String asnNbr, String cntrNo) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		int resultUla = 0;
		int resultUula = 0;
		try {
			log.info("START: checkFirstUA  DAO  Start asnNbr:" + CommonUtility.deNull(asnNbr) + " cntrNo"
					+ CommonUtility.deNull(cntrNo));

			sql = "select count(*) from cntr_txn,cntr  where txn_cd = 'ULA'  and cntr_txn.cntr_seq_nbr = cntr.cntr_seq_nbr  and cntr_txn.cntr_nbr = cntr.cntr_nbr  and cntr.cntr_nbr = :cntrNo  and cntr.txn_status = 'A'  and cntr.purp_cd = 'ST'  and cntr.misc_app_nbr is not null ";

			paramMap.put("cntrNo", cntrNo);
			log.info(" *** checkFirstUA SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				resultUla = rs.getInt(1);
			}

			sql = "select count(*) from cntr_txn,cntr  where txn_cd = 'UULA'  and cntr_txn.cntr_seq_nbr = cntr.cntr_seq_nbr  and cntr_txn.cntr_nbr = cntr.cntr_nbr  and cntr.cntr_nbr = :cntrNo  and cntr.txn_status = 'A'  and cntr.purp_cd = 'ST'  and cntr.misc_app_nbr is not null ";

			paramMap.put("cntrNo", cntrNo);
			log.info(" *** checkFirstUA SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				resultUula = rs.getInt(1);
			}

			return (resultUla - resultUula);
		} catch (NullPointerException e) {
			log.info("Exception: checkFirstUA ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: checkFirstUA ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** checkFirstUA  END *****");
		}
	}

	@Override
	public String getNewCatCd(String cntrSeq) throws BusinessException {
		String newCatCode = "";

		try {

			log.info("START: getNewCatCd  DAO  Start cntrSeq:" + CommonUtility.deNull(cntrSeq));

			// First: Get the container vo object to get required values
			ContainerValueObject cntrVo = containerData.getContainerByPrimaryKey(Long.parseLong(cntrSeq));

			// Second: Get the new cntr cat code
			String newStatus = "F".equals(cntrVo.getStatus()) ? "E" : "F";
			newCatCode = containerCommonFunction.getCntrCatCd(cntrVo.getIsoCode(), null, cntrVo.getOogOH(),
					cntrVo.getOogOlFront(), cntrVo.getOogOlBack(), cntrVo.getOogOwRight(), cntrVo.getOogOwLeft(),
					cntrVo.getReeferInd(), cntrVo.getUCInd(), cntrVo.getOverSizeInd(), cntrVo.getSpecialDetails(),
					newStatus);
			log.info("END: *** getNewCatCd  result : newCatCode *****" + newCatCode);
			return newCatCode;
		} catch (BusinessException e) {
			log.info("Exception: getNewCatCd", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception: getNewCatCd ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: getNewCatCd ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getNewCatCd  END *****");
		}

	}

	@Override
	public void updateStdWeigth(String cntrSeq, String cntrNbr, String user, String newCatCode)
			throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer();
		try {

			log.info("START: updateStdWeigth  DAO  Start cntrSeq:" + CommonUtility.deNull(cntrSeq) + " cntrNbr"
					+ CommonUtility.deNull(cntrNbr) + " user" + CommonUtility.deNull(user) + " newCatCode"
					+ CommonUtility.deNull(newCatCode));
			sql.setLength(0);
			sql.append("select SIZE_FT, DECLR_WT, STATUS from cntr where cntr_seq_nbr = :cntrSeq ");

			paramMap.put("cntrSeq", cntrSeq);
			log.info(" *** updateStdWeigth SQL *****" + sql.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			int curWeigth = 0;
			String descCntr = "";
			if (rs.next()) {
				int sizeFt = rs.getInt("SIZE_FT");
				int declrWeight = rs.getInt("DECLR_WT");
				String status = rs.getString("STATUS");
				// insert cntr_event_log for 1st weight
				// sql.setLength(0);
				// sql.append("insert into cntr_event_log(CNTR_SEQ_NBR, TXN_DTTM, TXN_CD, " +
				// "CNTR_NBR, STATUS, DECLR_WT, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
				// sql.append("values(?, ?, 'ULA', ?, ?, ?, ?, sysdate)");
				// ps = con.prepareStatement(sql.toString());
				// ps.setString(1, cntrSeq);
				// ps.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
				// ps.setString(3, cntrNbr);
				// ps.setString(4, status);
				// ps.setInt(5, declrWeight);
				// ps.setString(6, user);
				// int count = ps.executeUpdate();
				// if (count <= 0) {
				// ps.close();
				// con.close();
				// sessionContext.setRollbackOnly();
				// throw new BusinessException("M4201");
				// }
				descCntr = status + declrWeight;

				if (sizeFt == 20) {
					curWeigth = 2000;
				} else if (sizeFt == 40) {
					curWeigth = 4000;
				} else {
					curWeigth = declrWeight;
				}
				// update the status of cntr to E
				// log.info("--------weigth and catCD for UA 1st---:" + curWeigth + "," +
				// newCatCode + "," + descCntr);
				sql.setLength(0);
				sql.append(
						"update cntr set CAT_CD = :newCatCode, DECLR_WT = :curWeigth, status = 'E', LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = :user where cntr_seq_nbr = :cntrSeq ");
				paramMap.put("newCatCode", newCatCode);
				paramMap.put("curWeigth", curWeigth);
				paramMap.put("user", user);
				paramMap.put("cntrSeq", cntrSeq);

				log.info(" *** updateStdWeigth SQL *****" + sql.toString() + " paramMap " + paramMap.toString());
				int recordOfNumber = namedParameterJdbcTemplate.update(sql.toString(), paramMap);
				if (recordOfNumber == 0) {
					throw new BusinessException("M4201");
				}

				String sqlb = "INSERT INTO CNTR_TXN(CNTR_SEQ_NBR, CNTR_NBR, TXN_CD, TXN_DTTM, LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID, ERROR_MSG) VALUES(:cntrSeq, :cntrNbr, 'ULA', sysdate, sysdate, :user, :descCntr)";

				paramMap.put("descCntr", descCntr);
				paramMap.put("user", user);
				paramMap.put("cntrSeq", cntrSeq);
				paramMap.put("cntrNbr", cntrNbr);

				log.info(" *** updateStdWeigth SQL *****" + sqlb + " paramMap " + paramMap.toString());
				namedParameterJdbcTemplate.update(sqlb, paramMap);

			}
		} catch (BusinessException e) {
			log.info("Exception: updateStdWeigth", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception: updateStdWeigth", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: updateStdWeigth", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** updateStdWeigth  END *****");
		}
	}

	@Override
	/**
	 * Update UA No
	 * 
	 * @param uaNo
	 * @param vehicleNo
	 * @throws BusinessException
	 */
	public void updateVehicleNo(String uaNo, String vehicleNo) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = null;
		try {

			log.info("START: updateVehicleNo  DAO  Start uaNo:" + CommonUtility.deNull(uaNo) + " vehicleNo"
					+ CommonUtility.deNull(vehicleNo));
			sql = "UPDATE UA_DETAILS SET TRUCK_NBR= :vehicleNo WHERE UA_NBR = :uaNo ";

			paramMap.put("vehicleNo", vehicleNo);
			paramMap.put("uaNo", uaNo);

			log.info(" *** updateVehicleNo SQL *****" + sql + " paramMap " + paramMap.toString());
			namedParameterJdbcTemplate.update(sql, paramMap);
		} catch (NullPointerException e) {
			log.info("Exception: updateVehicleNo ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: updateVehicleNo ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** updateVehicleNo  END *****");
		}
	}

	@Override
	/**
	 * This method to checking Vehicle exist or not
	 * 
	 * @param uanbr
	 * @return
	 * @throws BusinessException
	 */
	public boolean checkVehicleExist(String uanbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = null;
		Date gate_out_dttm = null;
		try {

			log.info("START: checkVehicleExist  DAO  Start uanbr:" + CommonUtility.deNull(uanbr));
			sql = "SELECT GATE_OUT_DTTM FROM UA_DETAILS WHERE UA_NBR = :uanbr ";
			paramMap.put("uanbr", uanbr);
			log.info(" *** checkVehicleExist SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				gate_out_dttm = rs.getDate("GATE_OUT_DTTM");
			}

		} catch (NullPointerException e) {
			log.info("Exception: checkVehicleExist ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: checkVehicleExist ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** checkVehicleExist  END *****");
		}
		if (gate_out_dttm != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String updateCntrStatus(String cntrSeq, String userID) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();

		int count = 0;
		StringBuffer sql = new StringBuffer();

		sql.setLength(0);
		sql.append(" UPDATE CNTR SET STATUS = 'F', LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID = :userID ");
		sql.append(" WHERE CNTR_SEQ_NBR = :cntrSeq ");

		try {
			log.info("START: updateCntrStatus  DAO  Start cntrSeq:" + CommonUtility.deNull(cntrSeq) + " userID: "
					+ CommonUtility.deNull(userID));
			paramMap.put("cntrSeq", cntrSeq);
			paramMap.put("userID", userID);
			
			log.info("Before update" + sql.toString());
			count = namedParameterJdbcTemplate.update(sql.toString(), paramMap);

			 log.info("after update====" + count);

			if (count == 0) {
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
		} catch (NullPointerException e) {
			log.info("Exception: updateCntrStatus ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: updateCntrStatus ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** updateCntrStatus  END *****");
		}
		return "" + count;

	}

	@Override
	public boolean countUABalance(String cntrNbr) throws BusinessException {
		boolean bal = false;
		String sql = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();

		sql = "select * from ua_details where cntr_nbr= :cntrNbr and UA_STATUS='A' ";

		try {
			log.info("START: countUABalance cntrNbr:" + CommonUtility.deNull(cntrNbr));
			paramMap.put("cntrNbr", cntrNbr);
			log.info(" *** countUABalance SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				bal = true;
			}
		} catch (NullPointerException e) {
			log.info("Exception: countUABalance ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: countUABalance ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** countUABalance  END *****");
		}
		return bal;
	}

	@Override
	public void cancel1stUa(String cntrSeq, String cntrNbr, String userID) throws BusinessException {
		StringBuffer sql = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {

			log.info("START: cancel1stUa cntrSeq:" + CommonUtility.deNull(cntrSeq) + "cntrNbr:"
					+ CommonUtility.deNull(cntrNbr) + " userID" + CommonUtility.deNull(userID));

			sql.append(
					"INSERT INTO CNTR_TXN(CNTR_SEQ_NBR, CNTR_NBR, TXN_CD, TXN_DTTM, LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID) ");
			sql.append("VALUES(:cntrSeq, :cntrNbr, 'UULA', :date, sysdate, :userID)");

			paramMap.put("cntrSeq", cntrSeq);
			paramMap.put("cntrNbr", cntrNbr);
			paramMap.put("userID", userID);
			paramMap.put("date", new Timestamp(new java.util.Date().getTime()));
			log.info(" *** cancel1stUa SQL *****" + sql.toString() + " paramMap " + paramMap.toString());
			namedParameterJdbcTemplate.update(sql.toString(), paramMap);

		} catch (NullPointerException e) {
			log.info("Exception: cancel1stUa ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: cancel1stUa ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** cancel1stUa  END *****");
		}
	}

	@Override
	public String getUaCntrFirst(String cntrSeq, String cntrNbr) throws BusinessException {
		String sql = "";
		String dnFirst = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		try {
			log.info("START: getUaCntrFirst cntrSeq:" + CommonUtility.deNull(cntrSeq) + "cntrNbr:"
					+ CommonUtility.deNull(cntrNbr));

			int resultUla = 0;
			int resultUula = 0;
			sql = "select count(*) from cntr_txn  where txn_cd = 'ULA'  and cntr_txn.cntr_seq_nbr = :cntrSeq ";
			paramMap.put("cntrSeq", cntrSeq);

			log.info(" *** getUaCntrFirst SQL *****" + sql.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				resultUla = rs.getInt(1);
			}
			sql = "select count(*) from cntr_txn  where txn_cd = 'UULA'  and cntr_txn.cntr_seq_nbr = :cntrSeq ";

			paramMap.put("cntrSeq", cntrSeq);
			log.info(" *** getUaCntrFirst SQL *****" + sql.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				resultUula = rs.getInt(1);
			}
			if (resultUla - resultUula <= 0) {
				return "";
			}

			sql = "select ua_nbr from ua_details  where ESN_ASN_NBR in (select ESN_ASN_NBR from ESN_MOT_CNTR where cntr_seq_nbr = :cntrSeq)  and ua_status = 'A' and cntr_nbr = :cntrNbr  order by mot_create_dttm ASC ";
			paramMap.put("cntrSeq", cntrSeq);
			paramMap.put("cntrNbr", cntrNbr);
			log.info(" *** getUaCntrFirst SQL *****" + sql.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				dnFirst = rs.getString("ua_nbr");
			}
			log.info("------------dnFirst--------------" + dnFirst);

			return dnFirst;
		} catch (NullPointerException e) {
			log.info("Exception: getUaCntrFirst ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: getUaCntrFirst ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getUaCntrFirst  END *****");
		}
	}

	@Override
	public boolean isUABefCloseShp(String vvcode, String uaCreateDttm) throws BusinessException {

		String closeShpDttm = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		boolean canDelete = false;
		// String varno = "";
		DateFormat df = null;
		Date d2 = new Date();
		SqlRowSet rs = null;
		String sqlvoy = " select vv_cd,min(to_char(last_modify_dttm,'DD/MM/YYYY HH24:MI'))  from audit_trail_vessel_call where  gb_close_shp_ind = 'Y'  and vv_Cd = :vvcode group by vv_cd";

		try {
			log.info("START: isUABefCloseShp vvcode:" + CommonUtility.deNull(vvcode) + "uaCreateDttm:"
					+ CommonUtility.deNull(uaCreateDttm));

			paramMap.put("vvcode", vvcode);
			log.info(" *** isUABefCloseShp SQL *****" + sqlvoy + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlvoy, paramMap);

			df = new SimpleDateFormat("dd/mm/yyyy HH:mm");
			Date d1 = df.parse(uaCreateDttm);

			if (rs.next()) {
				closeShpDttm = rs.getString(2);
				d2 = df.parse(closeShpDttm);
				if (d1.before(d2)) {
					canDelete = true;
				}
			}

			log.info("END: *** isUABefCloseShp  canDelete*****" + canDelete);
		} catch (NullPointerException e) {
			log.info("Exception: isUABefCloseShp ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: isUABefCloseShp ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** isUABefCloseShp  END *****");
		}
		return canDelete;
	}

	@Override
	public void changeStatusCntr(String cntrSeq, String user, String newCatCode) throws BusinessException {
		StringBuffer sql = new StringBuffer();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: changeStatusCntr cntrSeq:" + CommonUtility.deNull(cntrSeq) + "user:"
					+ CommonUtility.deNull(user) + "newCatCode:" + CommonUtility.deNull(newCatCode));
			String statusCntr = "";
			int declrWeight = 0;
			sql.setLength(0);

			sql.append(
					"select ERROR_MSG from CNTR_TXN where cntr_seq_nbr =:cntrSeq and TXN_CD = 'ULA' ORDER by txn_dttm ASC");

			paramMap.put("cntrSeq", cntrSeq);
			paramMap.put("user", user);
			paramMap.put("newCatCode", newCatCode);
			log.info("SQL" + sql.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				statusCntr = rs.getString("ERROR_MSG");
			}
			if (statusCntr != null && statusCntr.length() > 0) {
				statusCntr = statusCntr.substring(1);
				declrWeight = Integer.parseInt(statusCntr);
			}
			if ("E".equals(statusCntr)) {
				// update the status of cntr to E
				sql.setLength(0);
				sql.append("UPDATE CNTR SET CAT_CD =:newCatCode, STATUS = 'F', LAST_MODIFY_DTTM = sysdate,"
						+ " DECLR_WT = :declrWeight  LAST_MODIFY_USER_ID =:user " + "WHERE CNTR_SEQ_NBR=:cntrSeq ");
				// log.info("SQL DN:" + sql.toString() + "," + newCatCode +
				// "," + user + "," + cntrSeq);
				paramMap.put("declrWeight", declrWeight);
				paramMap.put("newCatCode", newCatCode);
				paramMap.put("user", user);
				paramMap.put("cntrSeq", cntrSeq);
				log.info("SQL" + sql.toString() + "pstmt:");
				namedParameterJdbcTemplate.update(sql.toString(), paramMap);
			}
		} catch (NullPointerException e) {
			log.info("Exception changeStatusCntr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception changeStatusCntr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO changeStatusCntr");

		}
	}

	@Override
	public String getCntrSeq(String cntrNo) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		String cntrSeq = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getCntrSeq cntrNo:" + CommonUtility.deNull(cntrNo));
			sb.append(" SELECT CNTR_SEQ_NBR FROM CNTR WHERE CNTR_NBR =:cntrNo AND PURP_CD='ST' ");
			sb.append(" AND TXN_STATUS = 'A' AND MISC_APP_NBR IS NOT NULL");
			paramMap.put("cntrNo", cntrNo);
			log.info("SQL" + sb.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				cntrSeq = Integer.toString(rs.getInt(1));
			}
			log.info("END: DAO getCntrSeq cntrSeq" + cntrSeq);
		} catch (Exception e) {
			log.info("Exception getCntrSeq : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getCntrSeq");
		}
		return cntrSeq;
	}

	@Override
	public List<GcOpsUaReport> getUAPrintJasper(String uaNbr) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		List<GcOpsUaReport> listUaDetails = new ArrayList<GcOpsUaReport>();
		try {
			log.info("getUAPrintJasper DAO START uaNbr:" + CommonUtility.deNull(uaNbr));
			sb.append(
					" SELECT TO_CHAR(WEBDNUATEMP.DATETIME, 'DD/MM/YYYY HH24:MI') DATETIME, WEBDNUATEMP.TRANSREFNO AS UA_DETAILS_UA_NBR,WEBDNUATEMP.ATB,");
			sb.append(" WEBDNUATEMP.VSLNM,WEBDNUATEMP.VOYNO,WEBDNUATEMP.CONTNO,");
			sb.append(" WEBDNUATEMP.CONTSIZE,WEBDNUATEMP.CONTTYPE,WEBDNUATEMP.ASNNO,WEBDNUATEMP.CRGREF,");
			sb.append(" WEBDNUATEMP.WT,WEBDNUATEMP.VOL,WEBDNUATEMP.DECLQTY,");
			sb.append(" WEBDNUATEMP.TRANSQTY,WEBDNUATEMP.BALQTY,WEBDNUATEMP.NRICPASSPORTNO,");
			sb.append(" WEBDNUATEMP.TRANSTYPE,WEBDNUATEMP.VEH1,WEBDNUATEMP.MARKING,");
			sb.append(" WEBDNUATEMP.CRG_DESC,SST_BILL.ACCT_NBR_SER_CHRG,SST_BILL.EDO_ACCT_NBR,");
			sb.append(" SST_BILL.TOTAL_AMT_SER_CHRG,SST_BILL.TOTAL_AMT_WHARF_CHRG,");
			sb.append(" SST_BILL.TOTAL_AMT_STORE_CHRG,SST_BILL.TOTAL_AMT_SR_CHRG,");
			sb.append(" SST_BILL.TOTAL_AMT_SER_WHARF_CHRG,SST_BILL.TARRIF_CD_SER_CHRG,");
			sb.append(" SST_BILL.TARRIF_DESC_SER_CHRG,SST_BILL.BILLABLE_TON_SER_CHRG,");
			sb.append(" SST_BILL.UNIT_RATE_SER_CHRG,SST_BILL.TARRIF_CD_WHARF_CHRG,");
			sb.append(" SST_BILL.TARRIF_DESC_WHARF_CHRG,SST_BILL.BILLABLE_TON_WHARF_CHRG,");
			sb.append(" SST_BILL.UNIT_RATE_WHARF_CHRG,SST_BILL.TARRIF_CD_STORE_CHRG,");
			sb.append(" SST_BILL.TARRIF_DESC_STORE_CHRG,SST_BILL.BILLABLE_TON_STORE_CHRG,");
			sb.append(" SST_BILL.UNIT_RATE_STORE_CHRG,SST_BILL.TARRIF_CD_SR_CHRG,");
			sb.append(" SST_BILL.TARRIF_DESC_SR_CHRG,SST_BILL.BILLABLE_TON_SR_CHRG,SST_BILL.UNIT_RATE_SR_CHRG,");
			sb.append(" SST_BILL.UNIT_RATE_SER_WHARF_CHRG,");
			sb.append(" SST_BILL.BILLABLE_TON_SER_WHARF_CHRG,SST_BILL.TARRIF_DESC_SER_WHARF_CHRG,");
			sb.append(" SST_BILL.TARRIF_CD_SER_WHARF_CHRG,SST_BILL.TIME_UNIT_SER,SST_BILL.TIME_UNIT_WHF,");
			sb.append(" SST_BILL.TIME_UNIT_SR,SST_BILL.TIME_UNIT_SER_WHF,");
			sb.append(" SST_BILL.TIME_UNIT_STORE");
			sb.append(" FROM WEBDNUATEMP WEBDNUATEMP INNER JOIN SST_BILL SST_BILL ON ");
			sb.append(" WEBDNUATEMP.TRANSREFNO=SST_BILL.DN_UA_NBR");
			sb.append(" WHERE (WEBDNUATEMP.TRANSREFNO = :uaNbr)");
			sb.append(" ORDER BY WEBDNUATEMP.TRANSREFNO ");

			paramMap.put("uaNbr", uaNbr);
			log.info("SQL" + sb.toString() + "pstmt:");
			listUaDetails = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<GcOpsUaReport>(GcOpsUaReport.class));
			return listUaDetails;
		} catch (NullPointerException e) {
			log.info("Exception getUAPrintJasper :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getUAPrintJasper :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getUAPrintJasper DAO end");

		}
	}

	@Override
	public List<UaListObject> getUAList(String esnNo) throws BusinessException {

		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		List<UaListObject> uaList = new ArrayList<UaListObject>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getUAList esnNo: " + CommonUtility.deNull(esnNo));
			sb.append(
					" SELECT UA_NBR,NBR_PKGS,BILLABLE_TON,BILL_STATUS,UA_STATUS,TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')||' @ '||DECODE(UA_CREATE_LOGIN , 'AUTOUA','MG', DECODE(PRINT_LOCATION,'','GCO','S',SST_MACHINE_NBR,PRINT_LOCATION))||DECODE(GATE_OUT_DTTM,'','',' (VehExit: '||TO_CHAR(GATE_OUT_DTTM,'DD/MM HH24:MI')||')') TRANS_DTTM, CNTR_NBR, GOE.LANE_ID LANE_NBR, GOE.GATE_STAFF_ID LAST_MODIFY_USER_ID,  UA.TRUCK_NBR ");
			sb.append("FROM UA_DETAILS UA");
			sb.append(" LEFT JOIN ");
			sb.append(" (");
			sb.append("   SELECT MAX(GATE_OUT_NBR) GATE_OUT_NBR, TRANS_REF");
			sb.append("   FROM GATE_OUT_EVENT_DETAILS ");
			sb.append("   GROUP BY TRANS_REF ");
			sb.append(" ) GOD ");
			sb.append(" ON UA.UA_NBR = GOD.TRANS_REF ");
			sb.append(" LEFT JOIN GATE_OUT_EVENT GOE");
			sb.append(" ON GOE.GATE_OUT_NBR = GOD.GATE_OUT_NBR ");
			sb.append(" WHERE ESN_ASN_NBR= :esnasnnbr ORDER BY UA_NBR");

			sql = sb.toString();
			log.info("getUAList SQL: " + sql);
			paramMap.put("esnasnnbr", esnNo);
			log.info("paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				UaListObject uaObj = new UaListObject();

				uaObj.setUa_nbr(CommonUtility.deNull(rs.getString(1)));
				uaObj.setUa_nbr_pkgs(CommonUtility.deNull(rs.getString(2)));
				uaObj.setBill_tonn(CommonUtility.deNull(rs.getString(3)));
				uaObj.setBill_status(CommonUtility.deNull(rs.getString(4)));
				uaObj.setUa_status(CommonUtility.deNull(rs.getString(5)));
				uaObj.setTrans_time(CommonUtility.deNull(rs.getString(6)));
				// Added by SONLT
				uaObj.setUa_cntr(CommonUtility.deNull(rs.getString(7)));
				// Added by HoaBT2: get GATE_OUT_EVENT.LANE_ID, GOE.GATE_STAFF_ID
				uaObj.setLane_nbr(CommonUtility.deNull(rs.getString(8)));
				uaObj.setLast_modify_user_id(CommonUtility.deNull(rs.getString(9)));
				uaObj.setTruck_nbr(CommonUtility.deNull(rs.getString(10)));
				uaList.add(uaObj);
			}
		} catch (NullPointerException e) {
			log.info("Exception getUAList :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getUAList :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getUAList DAO end   uaList: " + uaList.size());

		}
		return uaList;
	}

	@Override
	public String getUAtransDttm(String esnasnnbr) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		String sql = "";
		SqlRowSet rs = null;
		String trans_dttm = null;
		Map<String, String> paramMap = new HashMap<>();
		try {
			log.info("START getUAtransDttm DAO :: esnasnnbr: " + CommonUtility.deNull(esnasnnbr));
			sb.append(" SELECT 	TO_CHAR(MIN(TRANS_DTTM), 'DD/MM/YYYY HH24:MI')  AS TRANS_DTTM  FROM UA_DETAILS ");
			sb.append("WHERE UA_NBR LIKE :esnasnnbr AND UA_STATUS = 'A'  ");
			sql = sb.toString();
			log.info("getUAtransDttm SQL: " + sql);
			paramMap.put("esnasnnbr", "U" + esnasnnbr + "%");
			log.info("paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				trans_dttm = rs.getString("TRANS_DTTM");
			}
		} catch (NullPointerException e) {
			log.info("Exception getUAtransDttm :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getUAtransDttm :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getUAtransDttm DAO end   uaList: " + trans_dttm);

		}
		return trans_dttm;
	}

	// ejb.sessionBeans.gbms.ops.dnua.ua->UAEJB->updFtrans
	@Override
	public void updFtrans(String esnasnnbr, String transtype, String ftransdate, String userId)
			throws BusinessException {
		String sql = "";
		int count = 0;
		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String sql4 = "";
		Map<String, String> paramMap = new HashMap<>();

		try {
			log.info("START updFtrans Dao :: esnasnnbr:" + CommonUtility.deNull(esnasnnbr) + "transtype:" + CommonUtility.deNull(transtype) + "ftransdate:"
					+ CommonUtility.deNull(ftransdate) + "userId:" + CommonUtility.deNull(userId));
			sql1 = " update ESN_details set FIRST_TRANS_DTTM =to_date(:ftransdate,'dd/mm/yyyy hh24:mi') WHERE ESN_ASN_NBR=:esnasnnbr ";
			sql2 = " update tesn_psa_jp set FIRST_TRANS_DTTM =to_date(:ftransdate,'dd/mm/yyyy hh24:mi') WHERE ESN_ASN_NBR=:esnasnnbr ";
			sql3 = " update tesn_jp_jp set FIRST_TRANS_DTTM =to_date(:ftransdate,'dd/mm/yyyy hh24:mi') WHERE ESN_ASN_NBR=:esnasnnbr ";
			sql4 = " update ss_details set FIRST_TRANS_DTTM =to_date(:ftransdate,'dd/mm/yyyy hh24:mi'),LAST_MODIFY_USER_ID=:userId,LAST_MODIFY_DTTM=SYSDATE WHERE ESN_ASN_NBR=:esnasnnbr";

			if (transtype != null && !transtype.equals("") && transtype.equals("E")) {
				sql = sql1;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("C")) {
				sql = sql2;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("A")) {
				sql = sql3;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("S")) {
				sql = sql4;
			}
			count = namedParameterJdbcTemplate.update(sql, paramMap);
			log.info("count:" + count);
			if (count == 0) {
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			} else {
				updateTransactionTables(esnasnnbr, transtype, ftransdate, userId);
			}
		} catch (NullPointerException e) {
			log.info("Exception updFtrans :", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception updFtrans :", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception updFtrans :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("updFtrans DAO end");

		}
	}

	// ejb.sessionBeans.gbms.ops.dnua.ua->UAEJB->updateTransactionTables
	private void updateTransactionTables(String esnasnnbr, String transtype, String ftransdate, String userId)
			throws BusinessException {
		StringBuilder sb = new StringBuilder();
		String remarks = "UPDATES EARLIEST UA TRANS";
		int trans_nbr = 0;
		int count = 0;
		String transSqlTable = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<>();
		try {
			log.info("START updateTransactionTables esnasnnbr:" + CommonUtility.deNull(esnasnnbr) + "transtype:" + CommonUtility.deNull(transtype) + "ftransdate:"
					+ CommonUtility.deNull(ftransdate) + "userId:" + CommonUtility.deNull(userId));
			if (transtype != null && !transtype.equals("") && transtype.equals("E")) {
				transSqlTable = "SELECT MAX(TRANS_NBR) FROM ESN_DETAILS_TRANS WHERE ESN_ASN_NBR=:esnasnnbr";
			} else if (transtype != null && !transtype.equals("") && transtype.equals("C")) {
				transSqlTable = "SELECT MAX(TRANS_NBR) FROM TESN_PSA_JP_TRANS WHERE ESN_ASN_NBR=:esnasnnbr";
			} else if (transtype != null && !transtype.equals("") && transtype.equals("A")) {
				transSqlTable = "SELECT MAX(TRANS_NBR) FROM TESN_JP_JP_TRANS WHERE ESN_ASN_NBR=:esnasnnbr";
			} else if (transtype != null && !transtype.equals("") && transtype.equals("S")) {
				transSqlTable = "SELECT MAX(TRANS_NBR) FROM SS_DETAILS_TRANS WHERE ESN_ASN_NBR=:esnasnnbr";
			}
			log.info("updateTransactionTables SQL 1: " + transSqlTable);
			paramMap.put("esnasnnbr", esnasnnbr);
			log.info("paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(transSqlTable, paramMap);
			if (rs.next()) {
				trans_nbr = (rs.getInt(1)) + 1;
			} else {
				trans_nbr = 0;
			}
			if (transtype != null && !transtype.equals("") && transtype.equals("A")) {
				sb.append("INSERT ");
				sb.append("	INTO ");
				sb.append("		GBMS.TESN_JP_JP_TRANS (ESN_ASN_NBR, ");
				sb.append("		EDO_ASN_NBR, ");
				sb.append("		LD_IND, ");
				sb.append("		NBR_PKGS, ");
				sb.append("		NOM_WT, ");
				sb.append("		NOM_VOL, ");
				sb.append("		PAYMENT_MODE, ");
				sb.append("		ACCT_NBR, ");
				sb.append("		UA_NBR_PKGS, ");
				sb.append("		FIRST_TRANS_DTTM, ");
				sb.append("		MIXED_SCHEME_ACCT_NBR, ");
				sb.append("		DN_NBR_PKGS, ");
				sb.append("		REMARKS, ");
				sb.append("		LAST_MODIFY_DTTM, ");
				sb.append("		LAST_MODIFY_USER_ID, ");
				sb.append("		TRANS_NBR) SELECT ");
				sb.append("			ESN_ASN_NBR, ");
				sb.append("			EDO_ASN_NBR, ");
				sb.append("			LD_IND, ");
				sb.append("			NBR_PKGS, ");
				sb.append("			NOM_WT, ");
				sb.append("			NOM_VOL, ");
				sb.append("			PAYMENT_MODE, ");
				sb.append("			ACCT_NBR, ");
				sb.append("			UA_NBR_PKGS, ");
				sb.append("			FIRST_TRANS_DTTM, ");
				sb.append("			MIXED_SCHEME_ACCT_NBR, ");
				sb.append("			DN_NBR_PKGS, ");
				sb.append("			:remarks, ");
				sb.append("			SYSDATE, ");
				sb.append("			:userId, ");
				sb.append("			:trans_nbr ");
				sb.append("		FROM ");
				sb.append("			GBMS.tesn_jp_jp ");
				sb.append("		WHERE ");
				sb.append("			ESN_ASN_NBR = :esnasnnbr ");
				paramMap.put("remarks", remarks);
				paramMap.put("userId", userId);
				paramMap.put("trans_nbr", trans_nbr);
				paramMap.put("esnasnnbr", esnasnnbr);
			} else if (transtype != null && !transtype.equals("") && transtype.equals("E")) {
				sb.append(" INSERT ");
				sb.append("	INTO ");
				sb.append("		GBMS.ESN_DETAILS_TRANS (ESN_ASN_NBR, ");
				sb.append("		HA_CUST_CD, ");
				sb.append("		ESN_PORT_DIS, ");
				sb.append("		ESN_OPS_IND, ");
				sb.append("		ESN_LOAD_FROM, ");
				sb.append("		ESN_DG_IND, ");
				sb.append("		ESN_HS_CODE, ");
				sb.append("		ESN_DUTY_GOOD_IND, ");
				sb.append("		TRUCKER_NM, ");
				sb.append("		TRUCKER_IC, ");
				sb.append("		STG_DAYS, ");
				sb.append("		STG_IND, ");
				sb.append("		PKG_TYPE, ");
				sb.append("		NBR_PKGS, ");
				sb.append("		ESN_WT, ");
				sb.append("		ESN_VOL, ");
				sb.append("		ACCT_NBR, ");
				sb.append("		CRG_DES, ");
				sb.append("		TRUCKER_PHONE_NBR, ");
				sb.append("		UA_NBR_PKGS, ");
				sb.append("		FIRST_TRANS_DTTM, ");
				sb.append("		MIXED_SCHEME_ACCT_NBR, ");
				sb.append("		PAYMENT_MODE, ");
				sb.append("		TRUCKER_NBR_PKGS, ");
				sb.append("		LAST_MODIFY_DTTM, ");
				sb.append("		LAST_MODIFY_USER_ID, ");
				sb.append("		REMARKS, ");
				sb.append("		TRANS_NBR) ");
				sb.append("SELECT ");
				sb.append("	ESN_ASN_NBR, ");
				sb.append("	HA_CUST_CD, ");
				sb.append("	ESN_PORT_DIS, ");
				sb.append("	ESN_OPS_IND, ");
				sb.append("	ESN_LOAD_FROM, ");
				sb.append("	ESN_DG_IND, ");
				sb.append("	ESN_HS_CODE, ");
				sb.append("	ESN_DUTY_GOOD_IND, ");
				sb.append("	TRUCKER_NM, ");
				sb.append("	TRUCKER_IC, ");
				sb.append("	STG_DAYS, ");
				sb.append("	STG_IND, ");
				sb.append("	PKG_TYPE, ");
				sb.append("	NBR_PKGS, ");
				sb.append("	ESN_WT, ");
				sb.append("	ESN_VOL, ");
				sb.append("	ACCT_NBR, ");
				sb.append("	CRG_DES, ");
				sb.append("	TRUCKER_PHONE_NBR, ");
				sb.append("	UA_NBR_PKGS, ");
				sb.append("	FIRST_TRANS_DTTM, ");
				sb.append("	MIXED_SCHEME_ACCT_NBR, ");
				sb.append("	PAYMENT_MODE, ");
				sb.append("		TRUCKER_NBR_PKGS, ");
				sb.append("		SYSDATE, ");
				sb.append("		:userId, ");
				sb.append("		:remarks, ");
				sb.append("		:trans_nbr ");
				sb.append(" FROM ");
				sb.append("	GBMS.ESN_DETAILS WHERE ESN_ASN_NBR = :esnasnnbr ");
				paramMap.put("userId", userId);
				paramMap.put("trans_nbr", trans_nbr);
				paramMap.put("esnasnnbr", esnasnnbr);
			} else if (transtype != null && !transtype.equals("") && transtype.equals("C")) {
				sb.append("INSERT ");
				sb.append("	INTO ");
				sb.append("		GBMS.TESN_PSA_JP_TRANS (ESN_ASN_NBR, ");
				sb.append("		DIS_PORT, ");
				sb.append("		FIRST_CAR_VOY_NBR, ");
				sb.append("		FIRST_CAR_VES_NM, ");
				sb.append("		HS_CD, ");
				sb.append("		PKG_TYPE, ");
				sb.append("		CRG_DES, ");
				sb.append("		NBR_PKGS, ");
				sb.append("		GROSS_WT, ");
				sb.append("		GROSS_VOL, ");
				sb.append("		DG_IND, ");
				sb.append("		STORAGE_IND, ");
				sb.append("		STORAGE_DAYS, ");
				sb.append("		SHIPPER_NM, ");
				sb.append("		TRUCKER_NM, ");
				sb.append("		TRUCKER_IC, ");
				sb.append("		OPS_IND, ");
				sb.append("		ACCT_NBR, ");
				sb.append("		FIRST_TRANS_DTTM, ");
				sb.append("		MIXED_SCHEME_ACCT_NBR, ");
				sb.append("		PAYMENT_MODE, ");
				sb.append("		UA_NBR_PKGS, ");
				sb.append("		TRUCKER_NBR_PKGS, ");
				sb.append("		TRUCKER_CONTACT_NBR, ");
				sb.append("		TRUCKER_CO_CD, ");
				sb.append("		REMARKS, ");
				sb.append("		LAST_MODIFY_USER_ID, ");
				sb.append("		LAST_MODIFY_DTTM, ");
				sb.append("		TRANS_NBR) ");
				sb.append("	SELECT ");
				sb.append("		ESN_ASN_NBR, ");
				sb.append("		DIS_PORT, ");
				sb.append("		FIRST_CAR_VOY_NBR, ");
				sb.append("		FIRST_CAR_VES_NM, ");
				sb.append("		HS_CD, ");
				sb.append("		PKG_TYPE, ");
				sb.append("		CRG_DES, ");
				sb.append("		NBR_PKGS, ");
				sb.append("		GROSS_WT, ");
				sb.append("		GROSS_VOL, ");
				sb.append("		DG_IND, ");
				sb.append("		STORAGE_IND, ");
				sb.append("		STORAGE_DAYS, ");
				sb.append("		SHIPPER_NM, ");
				sb.append("		TRUCKER_NM, ");
				sb.append("		TRUCKER_IC, ");
				sb.append("		OPS_IND, ");
				sb.append("		ACCT_NBR, ");
				sb.append("		FIRST_TRANS_DTTM, ");
				sb.append("		MIXED_SCHEME_ACCT_NBR, ");
				sb.append("		PAYMENT_MODE, ");
				sb.append("		UA_NBR_PKGS, ");
				sb.append("		TRUCKER_NBR_PKGS, ");
				sb.append("		TRUCKER_CONTACT_NBR, ");
				sb.append("		TRUCKER_CO_CD, ");
				sb.append("		:remarks, ");
				sb.append("		:userId, ");
				sb.append("		SYSDATE, ");
				sb.append("		:trans_nbr ");
				sb.append("	FROM ");
				sb.append("		GBMS.TESN_PSA_JP ");
				sb.append("	WHERE ");
				sb.append("		ESN_ASN_NBR = :esnasnnbr ");
				paramMap.put("remarks", remarks);
				paramMap.put("userId", userId);
				paramMap.put("trans_nbr", trans_nbr);
				paramMap.put("esnasnnbr", esnasnnbr);
			}

			else if (transtype != null && !transtype.equals("") && transtype.equals("S")) {

				sb.append("INSERT ");
				sb.append("	INTO ");
				sb.append("		GBMS.SS_DETAILS_TRANS (ESN_ASN_NBR, ");
				sb.append("		SS_REF_NBR, ");
				sb.append("		NBR_PKGS, ");
				sb.append("		SS_WT, ");
				sb.append("		SS_VOL, ");
				sb.append("		CRG_DES, ");
				sb.append("		PKG_TYPE, ");
				sb.append("		SS_HS_CODE, ");
				sb.append("		CARGO_TYPE, ");
				sb.append("		SHIPPER_CD, ");
				sb.append("		SHIPPER_CR_NBR, ");
				sb.append("		SHIPPER_CONTACT, ");
				sb.append("		SHIPPER_ADDR, ");
				sb.append("		SHIPPER_NM, ");
				sb.append("		ACCT_NBR, ");
				sb.append("		SS_DG_IND, ");
				sb.append("		SS_DUTY_GOOD_IND, ");
				sb.append("		UA_NBR_PKGS, ");
				sb.append("		PAYMENT_MODE, ");
				sb.append("		BILL_STATUS, ");
				sb.append("		LAST_MODIFY_USER_ID, ");
				sb.append("		LAST_MODIFY_DTTM, ");
				sb.append("		FIRST_TRANS_DTTM, ");
				sb.append("		REMARKS, ");
				sb.append("		TRANS_NBR ) ");

				sb.append("	SELECT ");
				sb.append("		ESN_ASN_NBR, ");
				sb.append("		SS_REF_NBR, ");
				sb.append("		NBR_PKGS, ");
				sb.append("		SS_WT, ");
				sb.append("		SS_VOL, ");
				sb.append("		CRG_DES, ");
				sb.append("		PKG_TYPE, ");
				sb.append("		SS_HS_CODE, ");
				sb.append("		CARGO_TYPE, ");
				sb.append("		SHIPPER_CD, ");
				sb.append("		SHIPPER_CR_NBR, ");
				sb.append("		SHIPPER_CONTACT, ");
				sb.append("		SHIPPER_ADDR, ");
				sb.append("		SHIPPER_NM, ");
				sb.append("		ACCT_NBR, ");
				sb.append("		SS_DG_IND, ");
				sb.append("		SS_DUTY_GOOD_IND, ");
				sb.append("		UA_NBR_PKGS, ");
				sb.append("		PAYMENT_MODE, ");
				sb.append("		BILL_STATUS, ");
				sb.append("		LAST_MODIFY_USER_ID, ");
				sb.append("		LAST_MODIFY_DTTM, ");
				sb.append(" 	FIRST_TRANS_DTTM, ");
				sb.append("		:remarks, ");
				sb.append("		:trans_nbr ");
				sb.append("	FROM ");
				sb.append("		GBMS.SS_DETAILS WHERE ESN_ASN_NBR = :esnasnnbr ");
				paramMap.put("remarks", remarks);
				paramMap.put("trans_nbr", trans_nbr);
				paramMap.put("esnasnnbr", esnasnnbr);
			}
			log.info("updateTransactionTables SQL 2:" + sb.toString());
			log.info("paramMap: " + paramMap);
			count = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			log.info("count:" + count);
		} catch (NullPointerException e) {
			log.info("Exception updateTransactionTables :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception updateTransactionTables :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("updateTransactionTables end");

		}
	}

	// Start #36252  - to check whether UA is already deleted or not. - NS OCT 2023
	@Override
	public boolean checkCancelUA(String uanbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<>();
		boolean flag = false;
		String ua_status = "";
		StringBuffer buffer = (new StringBuffer("select UA_STATUS from UA_DETAILS where UA_NBR = :uanbr"));
		String query = buffer.toString();
		
		try {
			log.info("START checkCancelUA esnasnnbr:" + CommonUtility.deNull(uanbr));
			paramMap.put("uanbr", uanbr);
			log.info("checkCancelUA sql:" + query);
			log.info(" checkCancelUA paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(query, paramMap);
			
			 if (rs.next()) {
				 ua_status = rs.getString(1);
		     }
			 
			 if (ua_status.equalsIgnoreCase("X")){
				 flag = true;
			 } else {
				 flag = false;
			 }
			 log.info("checkCancelUA DAO end   flag: " + flag);

		} catch (NullPointerException e) {
			log.info("Exception checkCancelUA :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkCancelUA :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("checkCancelUA end");

		}

		return flag;
	}
	// End #36252  - to check whether UA is already deleted or not. - NS OCT 2023

	// Start ITSM 34719 - to check whether Vessel is Sailed. - NS SEPT 2023
	@Override
	public boolean hasVesselSailed(String vvCd) throws BusinessException {

		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<>();
		boolean flag = false;
		StringBuffer buffer = (new StringBuffer(
				"select * from vessel_call where vv_status_ind in ( 'UB', 'CL') AND  VESSEL_CALL.vv_cd='"))
				.append(vvCd)
				.append("'");

		String query = buffer.toString();
		log.info("hasVesselSailed: " + query);
		try {
			log.info("START hasVesselSailed vvCd:" + CommonUtility.deNull(vvCd));
			log.info(" hasVesselSailed paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(query, paramMap);
			if (rs.next())
				flag = true;
			else
				flag = false;

		} catch (NullPointerException e) {
			log.info("Exception hasVesselSailed :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception hasVesselSailed :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("hasVesselSailed end");

		}

		return flag;
	}
	// End ITSM 34719 - to check whether Vessel is Sailed. - NS SEPT 2023

}
