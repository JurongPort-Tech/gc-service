package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.GCARepository;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository
public class GCAJdbcRepository implements GCARepository {

	private static final Log log = LogFactory.getLog(GCAJdbcRepository.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	// ejb.sessionBeans.gbms.cargo.generalcargo-->GCAEJB
	@Override
	public List<String> getEdoDetails(String edoNo) throws BusinessException {
		List<String> edoList = new ArrayList<String>();
		StringBuffer sqlStr = null;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getEdoDetails  DAO  Start edoNo " + edoNo);
			sqlStr = new StringBuffer();
			sqlStr.append(
					" select  gb_edo.edo_asn_nbr edo_asn_nbr,manifest_details.hs_code hs_code,manifest_details.hs_sub_code_fr hs_sub_code_fr,");
			sqlStr.append(
					" manifest_details.hs_sub_code_to hs_sub_code_to,gb_edo.bl_nbr bl_nbr,manifest_details.gross_wt gross_wt,manifest_details.crg_status crg_status,load.port_nm ld_port,");
			sqlStr.append(
					" dis.port_nm disc_port,manifest_details.cons_nm cons_nm,manifest_details.crg_des crg_des,manifest_details.crg_type crg_type,");
			sqlStr.append(
					" manifest_details.dg_ind dg_ind,bca_crg_type.crg_type_cd crg_type_cd,vessel_call.vsl_nm vesselName,vessel_call.in_voy_nbr InVoyNo,hs_sub_code.hs_sub_desc hssubDesc,");
			sqlStr.append(
					" manifest_details.mft_seq_nbr,manifest_details.stg_type,vessel_call.terminal,vessel_call.scheme,vessel_call.combi_gc_scheme,vessel_call.combi_gc_ops_ind ");
			sqlStr.append(
					" From gb_edo,manifest_details left join hs_sub_code on manifest_details.hs_code = hs_sub_code.hs_code");
			sqlStr.append(
					" and manifest_details.hs_sub_code_fr = hs_sub_code.hs_sub_code_fr and manifest_details.hs_sub_code_to = hs_sub_code.hs_sub_code_to");
			sqlStr.append(",bca_crg_type,vessel_call,un_port_code load, un_port_code dis  where edo_asn_nbr = :edoNo ");
			sqlStr.append(" and  manifest_details.mft_seq_nbr = gb_edo.mft_seq_nbr ");
			sqlStr.append(" and manifest_details.bl_status = 'A' and  gb_edo.edo_status = 'A' ");
			sqlStr.append(" and gb_edo.var_nbr = vessel_call.vv_cd");
			// sqlStr.append(" and manifest_details.hs_code = hs_sub_code.hs_code"); used in
			// left join instead
			sqlStr.append(" and manifest_details.ld_port = load.port_cd  and manifest_details.dis_port = dis.port_cd");
			sqlStr.append(" and load.rec_status = 'A' and dis.rec_status = 'A'");
			// sqlStr.append(" and manifest_details.hs_sub_code_fr =
			// hs_sub_code.hs_sub_code_fr and manifest_details.hs_sub_code_to =
			// hs_sub_code.hs_sub_code_to");
			sqlStr.append(" and vessel_call.vv_status_ind <> 'CX'");
			paramMap.put("edoNo", edoNo);
			log.info(" getEdoDetails  DAO  SQL " + sqlStr.toString() + " paramMap " + paramMap);

			rs = namedParameterJdbcTemplate.queryForRowSet(sqlStr.toString(), paramMap);
			if (rs.next()) {
				edoList.add(CommonUtility.deNull(rs.getString("edo_asn_nbr")));
				edoList.add(CommonUtility.deNull(rs.getString("hs_code")));
				edoList.add(CommonUtility.deNull(rs.getString("hs_sub_code_fr")));
				edoList.add(CommonUtility.deNull(rs.getString("hs_sub_code_to")));
				edoList.add(CommonUtility.deNull(rs.getString("hssubDesc")));
				edoList.add(CommonUtility.deNull(rs.getString("bl_nbr")));
				edoList.add(CommonUtility.deNull(rs.getString("gross_wt")));
				edoList.add(CommonUtility.deNull(rs.getString("crg_status")));
				edoList.add(CommonUtility.deNull(rs.getString("ld_port")));
				edoList.add(CommonUtility.deNull(rs.getString("disc_port")));
				edoList.add(CommonUtility.deNull(rs.getString("cons_nm")));
				edoList.add(CommonUtility.deNull(rs.getString("crg_des")));
				edoList.add(CommonUtility.deNull(rs.getString("crg_type")));
				edoList.add(CommonUtility.deNull(rs.getString("dg_ind")));
				edoList.add(CommonUtility.deNull(rs.getString("crg_type_cd")));
				edoList.add(CommonUtility.deNull(rs.getString("crg_type_cd")));
				edoList.add(CommonUtility.deNull(rs.getString("vesselName")));
				edoList.add(CommonUtility.deNull(rs.getString("InVoyNo")));
				edoList.add(CommonUtility.deNull(rs.getString("mft_seq_nbr")));
				edoList.add(CommonUtility.deNull(rs.getString("stg_type")));
				edoList.add(CommonUtility.deNull(rs.getString("terminal")));
				edoList.add(CommonUtility.deNull(rs.getString("scheme")));
				edoList.add(CommonUtility.deNull(rs.getString("combi_gc_scheme")));
				edoList.add(CommonUtility.deNull(rs.getString("combi_gc_ops_ind")));
			}
			log.info(" getEdoDetails  DAO  Result" + edoList.toString());

		} catch (NullPointerException ne) {
			log.info("Exception getEdoDetails : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEdoDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEdoDetails  DAO  END");
		}

		return edoList;
	}

	// ejb.sessionBeans.gbms.cargo.generalcargo-->GCAEJB
	@Override
	public List<String> getEsnDetails(String esnNo) throws BusinessException {
		List<String> esnList = new ArrayList<String>();
		StringBuffer sqlStr = null;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getEsnDetails  DAO  Start esnNo " + esnNo);
			sqlStr = new StringBuffer();
			sqlStr.append(
					" select  esn_details.esn_asn_nbr esn_asn_nbr,esn_details.esn_hs_code hs_code,esn_details.hs_sub_code_fr hs_sub_code_fr, ");
			sqlStr.append(" esn_details.hs_sub_code_to hs_sub_code_to,esn_details.trucker_nm trucker_nm, ");
			sqlStr.append(" esn_details.trucker_ic trucker_ic,esn_details.trucker_phone_nbr trucker_phone_nbr, ");
			sqlStr.append(
					" esn_details.esn_dg_ind esn_dg_ind,esn_details.acct_nbr  acct_nbr,esn_details.crg_des crg_des, ");
			sqlStr.append(
					" esn_details.esn_wt esn_wt,esn_details.esn_duty_good_ind esn_duty_good_ind,vessel_call.vsl_nm vsl_nm,vessel_call.out_voy_nbr out_voy_nbr,hs_sub_code.hs_sub_desc hssubDesc, ");
			sqlStr.append(
					" vessel_call.terminal,vessel_call.scheme,vessel_call.combi_gc_scheme,vessel_call.combi_gc_ops_ind, ");
			sqlStr.append(
					" esn.bk_ref_nbr bk_ref_nbr,bk_details.shipper_nm shipper_nm,esn_details.esn_load_from esn_load_from,esn_details.esn_port_dis esn_port_dis ");
			sqlStr.append(" From esn,esn_details,bca_crg_type,vessel_call,hs_sub_code,bk_details  where  esn_details.esn_asn_nbr =:esnNo ");
			sqlStr.append(" and esn.esn_asn_nbr = esn_details.esn_asn_nbr  ");
			sqlStr.append(" and esn.out_voy_var_nbr = vessel_call.vv_cd ");
			sqlStr.append(" and esn.bk_ref_nbr = bk_details.bk_ref_nbr ");
			sqlStr.append(" and esn_details.esn_hs_code = hs_sub_code.hs_code ");
			sqlStr.append(
					" and esn_details.hs_sub_code_fr = hs_sub_code.hs_sub_code_fr and esn_details.hs_sub_code_to = hs_sub_code.hs_sub_code_to ");
			sqlStr.append(" and vessel_call.vv_status_ind <> 'CX' ");
			sqlStr.append(" union ");
			sqlStr.append(" select  tesn_psa_jp.esn_asn_nbr esn_asn_nbr,tesn_psa_jp.hs_cd hs_code, ");
			sqlStr.append(" tesn_psa_jp.hs_sub_code_fr hs_sub_code_fr,  tesn_psa_jp.hs_sub_code_to hs_sub_code_to, ");
			sqlStr.append(
					" tesn_psa_jp.trucker_nm trucker_nm,  tesn_psa_jp.trucker_ic trucker_ic,tesn_psa_jp.trucker_contact_nbr  ");
			sqlStr.append(" trucker_phone_nbr,  null esn_dg_ind,tesn_psa_jp.acct_nbr  acct_nbr, ");
			sqlStr.append(" tesn_psa_jp.crg_des crg_des,  tesn_psa_jp.gross_wt esn_wt,null esn_duty_good_ind, ");
			sqlStr.append(
					" vessel_call.vsl_nm vsl_nm,vessel_call.out_voy_nbr out_voy_nbr,hs_sub_code.hs_sub_desc hssubDesc, ");
			sqlStr.append(
					" vessel_call.terminal,vessel_call.scheme,vessel_call.combi_gc_scheme,vessel_call.combi_gc_ops_ind, ");
			sqlStr.append(
					" esn.bk_ref_nbr bk_ref_nbr,  bk_details.shipper_nm shipper_nm,vessel_call.port_to esn_load_from, ");
			sqlStr.append(
					" tesn_psa_jp.dis_port esn_port_dis From esn,tesn_psa_jp,bca_crg_type,vessel_call,hs_sub_code,bk_details   ");
			sqlStr.append(" where  tesn_psa_jp.esn_asn_nbr = :esnNo and esn.esn_asn_nbr = tesn_psa_jp.esn_asn_nbr ");
			sqlStr.append(
					" and esn.out_voy_var_nbr = vessel_call.vv_cd  and esn.bk_ref_nbr = bk_details.bk_ref_nbr   ");
			// 20180911 koktsing solve the issue Amend HS Code not able to return record
			// when HS sub code is null
			// amend to use (+) in the join
			// sqlStr.append(" and tesn_psa_jp.hs_Cd = hs_sub_code.hs_code and
			// tesn_psa_jp.hs_sub_code_fr = hs_sub_code.hs_sub_code_fr ");
			// sqlStr.append(" and tesn_psa_jp.hs_sub_code_to = hs_sub_code.hs_sub_code_to
			// and vessel_call.vv_status_ind <> 'CX' ");
			sqlStr.append(
					" and tesn_psa_jp.hs_Cd = hs_sub_code.hs_code(+)  and tesn_psa_jp.hs_sub_code_fr = hs_sub_code.hs_sub_code_fr(+)  ");
			sqlStr.append(
					" and tesn_psa_jp.hs_sub_code_to = hs_sub_code.hs_sub_code_to(+)  and vessel_call.vv_status_ind <> 'CX'   ");
			paramMap.put("esnNo", esnNo);
			log.info(" getEsnDetails  DAO  SQL " + sqlStr.toString() + " paramMap " + paramMap);

			rs = namedParameterJdbcTemplate.queryForRowSet(sqlStr.toString(), paramMap);

			if (rs.next()) {
				esnList.add(CommonUtility.deNull(rs.getString("esn_asn_nbr")));
				esnList.add(CommonUtility.deNull(rs.getString("hs_code")));
				esnList.add(CommonUtility.deNull(rs.getString("hs_sub_code_fr")));
				esnList.add(CommonUtility.deNull(rs.getString("hs_sub_code_to")));
				esnList.add(CommonUtility.deNull(rs.getString("hssubDesc")));
				esnList.add(CommonUtility.deNull(rs.getString("trucker_nm")));
				esnList.add(CommonUtility.deNull(rs.getString("trucker_ic")));
				esnList.add(CommonUtility.deNull(rs.getString("trucker_phone_nbr")));
				esnList.add(CommonUtility.deNull(rs.getString("esn_dg_ind")));
				esnList.add(CommonUtility.deNull(rs.getString("acct_nbr")));
				esnList.add(CommonUtility.deNull(rs.getString("crg_des")));
				esnList.add(CommonUtility.deNull(rs.getString("esn_wt")));
				esnList.add(CommonUtility.deNull(rs.getString("esn_duty_good_ind")));
				esnList.add(CommonUtility.deNull(rs.getString("vsl_nm")));
				esnList.add(CommonUtility.deNull(rs.getString("out_voy_nbr")));
				esnList.add(CommonUtility.deNull(rs.getString("bk_ref_nbr")));
				esnList.add(CommonUtility.deNull(rs.getString("shipper_nm")));
				esnList.add(CommonUtility.deNull(rs.getString("esn_load_from")));
				esnList.add(CommonUtility.deNull(rs.getString("esn_port_dis")));
				esnList.add(CommonUtility.deNull(rs.getString("terminal")));
				esnList.add(CommonUtility.deNull(rs.getString("scheme")));
				esnList.add(CommonUtility.deNull(rs.getString("combi_gc_scheme")));
				esnList.add(CommonUtility.deNull(rs.getString("combi_gc_ops_ind")));
			}

			log.info(" getEsnDetails  DAO  Result" + esnList.toString());

		} catch (NullPointerException ne) {
			log.info("Exception getEsnDetails : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEsnDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEsnDetails  DAO  END");
		}

		return esnList;
	}

	// ejb.sessionBeans.gbms.cargo.generalcargo-->GCAEJB
	@Override
	public List<String> getEdoDetails(String edoNo, String hsCode, String hsCodeFrom, String hsCodeTo,
			String hsSubCodeDesc) throws BusinessException {
		List<String> edoList = new ArrayList<String>();
		StringBuffer sqlStr = null;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getEdoDetails  DAO  Start edoNo " + edoNo + " hsCode" + hsCode + " hsCodeFrom" + hsCodeFrom
					+ " hsCodeTo" + hsCodeTo + " hsSubCodeDesc" + hsSubCodeDesc);
			sqlStr = new StringBuffer();
			sqlStr.append(
					" select  gb_edo.edo_asn_nbr edo_asn_nbr,manifest_details.hs_code hs_code,manifest_details.hs_sub_code_fr hs_sub_code_fr,");
			sqlStr.append(
					" manifest_details.hs_sub_code_to hs_sub_code_to,gb_edo.bl_nbr bl_nbr,manifest_details.gross_wt gross_wt,manifest_details.crg_status crg_status,load.port_nm ld_port,");
			sqlStr.append(
					" dis.port_nm disc_port,manifest_details.cons_nm cons_nm,manifest_details.crg_des crg_des,manifest_details.crg_type crg_type,");
			sqlStr.append(
					" manifest_details.dg_ind dg_ind,bca_crg_type.crg_type_cd crg_type_cd,vessel_call.vsl_nm vesselName,vessel_call.in_voy_nbr InVoyNo,hs_sub_code.hs_sub_desc hssubDesc,");
			sqlStr.append(
					" vessel_call.terminal,vessel_call.scheme,vessel_call.combi_gc_scheme,vessel_call.combi_gc_ops_ind, ");
			sqlStr.append(" manifest_details.mft_seq_nbr,manifest_details.stg_type ");
			sqlStr.append(
					" From gb_edo,manifest_details,bca_crg_type,vessel_call,hs_sub_code,un_port_code load, un_port_code dis  where edo_asn_nbr = :edoNo ");
			sqlStr.append(" and  manifest_details.mft_seq_nbr = gb_edo.mft_seq_nbr ");
			sqlStr.append(" and manifest_details.bl_status = 'A' and  gb_edo.edo_status = 'A' ");
			sqlStr.append(" and gb_edo.var_nbr = vessel_call.vv_cd");
			sqlStr.append(" and manifest_details.hs_code = hs_sub_code.hs_code");
			sqlStr.append(" and manifest_details.ld_port = load.port_cd  and manifest_details.dis_port = dis.port_cd");
			sqlStr.append(" and load.rec_status = 'A' and dis.rec_status = 'A'");
			sqlStr.append(
					" and manifest_details.hs_sub_code_fr = hs_sub_code.hs_sub_code_fr and manifest_details.hs_sub_code_to = hs_sub_code.hs_sub_code_to");
			sqlStr.append(" and vessel_call.vv_status_ind <> 'CX'");
			paramMap.put("edoNo", edoNo);
			log.info(" getEdoDetails  DAO  SQL " + sqlStr.toString() + " paramMap " + paramMap);

			rs = namedParameterJdbcTemplate.queryForRowSet(sqlStr.toString(), paramMap);
			if (rs.next()) {
				edoList.add(CommonUtility.deNull(rs.getString("edo_asn_nbr")));
				edoList.add(hsCode);
				edoList.add(hsCodeFrom);
				edoList.add(hsCodeTo);
				edoList.add(hsSubCodeDesc);
				edoList.add(CommonUtility.deNull(rs.getString("bl_nbr")));
				edoList.add(CommonUtility.deNull(rs.getString("gross_wt")));
				edoList.add(CommonUtility.deNull(rs.getString("crg_status")));
				edoList.add(CommonUtility.deNull(rs.getString("ld_port")));
				edoList.add(CommonUtility.deNull(rs.getString("disc_port")));
				edoList.add(CommonUtility.deNull(rs.getString("cons_nm")));
				edoList.add(CommonUtility.deNull(rs.getString("crg_des")));
				edoList.add(CommonUtility.deNull(rs.getString("crg_type")));
				edoList.add(CommonUtility.deNull(rs.getString("dg_ind")));
				edoList.add(CommonUtility.deNull(rs.getString("crg_type_cd")));
				edoList.add(CommonUtility.deNull(rs.getString("crg_type_cd")));
				edoList.add(CommonUtility.deNull(rs.getString("vesselName")));
				edoList.add(CommonUtility.deNull(rs.getString("InVoyNo")));
				edoList.add(CommonUtility.deNull(rs.getString("mft_seq_nbr")));
				edoList.add(CommonUtility.deNull(rs.getString("stg_type")));
				edoList.add(CommonUtility.deNull(rs.getString("terminal")));
				edoList.add(CommonUtility.deNull(rs.getString("scheme")));
				edoList.add(CommonUtility.deNull(rs.getString("combi_gc_scheme")));
				edoList.add(CommonUtility.deNull(rs.getString("combi_gc_ops_ind")));
			}
			log.info(" getEdoDetails  DAO  Result" + edoList.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getEdoDetails : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEdoDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEdoDetails  DAO  END");
		}
		return edoList;
	}

	// ejb.sessionBeans.gbms.cargo.generalcargo-->GCAEJB
	@Override
	public List<String> getEsnDetails(String esnNo, String hsCode, String hsCodeFrom, String hsCodeTo,
			String hsSubCodeDesc) throws BusinessException {
		List<String> esnList = new ArrayList<String>();
		StringBuffer sqlStr = null;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getEsnDetails  DAO  Start esnNo " + esnNo + " hsCode" + hsCode + " hsCodeFrom" + hsCodeFrom
					+ " hsCodeTo" + hsCodeTo + " hsSubCodeDesc" + hsSubCodeDesc);
			sqlStr = new StringBuffer();
			sqlStr.append(
					" select  esn_details.esn_asn_nbr esn_asn_nbr,esn_details.esn_hs_code hs_code,esn_details.hs_sub_code_fr hs_sub_code_fr,");
			sqlStr.append(" esn_details.hs_sub_code_to hs_sub_code_to,esn_details.trucker_nm trucker_nm,");
			sqlStr.append(" esn_details.trucker_ic trucker_ic,esn_details.trucker_phone_nbr trucker_phone_nbr,");
			sqlStr.append(
					" esn_details.esn_dg_ind esn_dg_ind,esn_details.acct_nbr  acct_nbr,esn_details.crg_des crg_des,");
			sqlStr.append(
					" esn_details.esn_wt esn_wt,esn_details.esn_duty_good_ind esn_duty_good_ind,vessel_call.vsl_nm vsl_nm,vessel_call.out_voy_nbr out_voy_nbr,hs_sub_code.hs_sub_desc hssubDesc,");
			sqlStr.append(
					" vessel_call.terminal,vessel_call.scheme,vessel_call.combi_gc_scheme,vessel_call.combi_gc_ops_ind, ");
			sqlStr.append(
					" esn.bk_ref_nbr bk_ref_nbr, bk_details.shipper_nm shipper_nm,esn_details.esn_load_from esn_load_from,esn_details.esn_port_dis esn_port_dis From esn,esn_details left join hs_sub_code on ");
			sqlStr.append(" esn_details.esn_hs_code = hs_sub_code.hs_code");
			sqlStr.append(
					" and esn_details.hs_sub_code_fr = hs_sub_code.hs_sub_code_fr and esn_details.hs_sub_code_to = hs_sub_code.hs_sub_code_to");
			sqlStr.append(",bca_crg_type,vessel_call,bk_details  where esn_details.esn_asn_nbr = :esnNo ");
			sqlStr.append(" and esn.esn_asn_nbr = esn_details.esn_asn_nbr ");
			sqlStr.append(" and esn.esn_status = 'A' ");
			sqlStr.append(" and esn.out_voy_var_nbr = vessel_call.vv_cd");
			sqlStr.append(" and esn.bk_ref_nbr = bk_details.bk_ref_nbr");
			// sqlStr.append(" and esn_details.esn_hs_code = hs_sub_code.hs_code"); //added
			// as left join
			// sqlStr.append(" and esn_details.hs_sub_code_fr = hs_sub_code.hs_sub_code_fr
			// and esn_details.hs_sub_code_to = hs_sub_code.hs_sub_code_to");
			sqlStr.append(" and vessel_call.vv_status_ind <> 'CX'");
			sqlStr.append(" union ");
			sqlStr.append(" select  tesn_psa_jp.esn_asn_nbr esn_asn_nbr,tesn_psa_jp.hs_cd hs_code, ");
			sqlStr.append(" tesn_psa_jp.hs_sub_code_fr hs_sub_code_fr,  tesn_psa_jp.hs_sub_code_to hs_sub_code_to, ");
			sqlStr.append(
					" tesn_psa_jp.trucker_nm trucker_nm,  tesn_psa_jp.trucker_ic trucker_ic,tesn_psa_jp.trucker_contact_nbr  ");
			sqlStr.append(" trucker_phone_nbr,  null esn_dg_ind,tesn_psa_jp.acct_nbr  acct_nbr, ");
			sqlStr.append(" tesn_psa_jp.crg_des crg_des,  tesn_psa_jp.gross_wt esn_wt,null esn_duty_good_ind, ");
			sqlStr.append(
					" vessel_call.vsl_nm vsl_nm,vessel_call.out_voy_nbr out_voy_nbr,hs_sub_code.hs_sub_desc hssubDesc, ");
			sqlStr.append(
					" vessel_call.terminal,vessel_call.scheme,vessel_call.combi_gc_scheme,vessel_call.combi_gc_ops_ind, ");
			sqlStr.append(
					" esn.bk_ref_nbr bk_ref_nbr,  bk_details.shipper_nm shipper_nm,vessel_call.port_to esn_load_from, ");
			sqlStr.append(" tesn_psa_jp.dis_port esn_port_dis From esn,tesn_psa_jp left join hs_sub_code");
			sqlStr.append(
					" on tesn_psa_jp.hs_Cd = hs_sub_code.hs_code  and tesn_psa_jp.hs_sub_code_fr = hs_sub_code.hs_sub_code_fr");
			sqlStr.append(" and tesn_psa_jp.hs_sub_code_to = hs_sub_code.hs_sub_code_to ");
			sqlStr.append(",bca_crg_type,vessel_call,bk_details");
			sqlStr.append(" where  tesn_psa_jp.esn_asn_nbr = :esnNo and esn.esn_asn_nbr = tesn_psa_jp.esn_asn_nbr    ");
			sqlStr.append(
					" and esn.out_voy_var_nbr = vessel_call.vv_cd  and esn.bk_ref_nbr = bk_details.bk_ref_nbr   ");
			// sqlStr.append(" and tesn_psa_jp.hs_Cd = hs_sub_code.hs_code and
			// tesn_psa_jp.hs_sub_code_fr = hs_sub_code.hs_sub_code_fr ");
			// sqlStr.append(" and tesn_psa_jp.hs_sub_code_to = hs_sub_code.hs_sub_code_to
			// //added as left join
			sqlStr.append(" and vessel_call.vv_status_ind <> 'CX'   ");
			paramMap.put("esnNo", esnNo);
			log.info("getEsnDetails SQL *********** " + sqlStr.toString() + " paramMap " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlStr.toString(), paramMap);
			if (rs.next()) {
				esnList.add(CommonUtility.deNull(rs.getString("esn_asn_nbr")));
				esnList.add(hsCode);
				esnList.add(hsCodeFrom);
				esnList.add(hsCodeTo);
				esnList.add(hsSubCodeDesc);
				esnList.add(CommonUtility.deNull(rs.getString("trucker_nm")));
				esnList.add(CommonUtility.deNull(rs.getString("trucker_ic")));
				esnList.add(CommonUtility.deNull(rs.getString("trucker_phone_nbr")));
				esnList.add(CommonUtility.deNull(rs.getString("esn_dg_ind")));
				esnList.add(CommonUtility.deNull(rs.getString("acct_nbr")));
				esnList.add(CommonUtility.deNull(rs.getString("crg_des")));
				esnList.add(CommonUtility.deNull(rs.getString("esn_wt")));
				esnList.add(CommonUtility.deNull(rs.getString("esn_duty_good_ind")));
				esnList.add(CommonUtility.deNull(rs.getString("vsl_nm")));
				esnList.add(CommonUtility.deNull(rs.getString("out_voy_nbr")));
				esnList.add(CommonUtility.deNull(rs.getString("bk_ref_nbr")));
				esnList.add(CommonUtility.deNull(rs.getString("shipper_nm")));
				esnList.add(CommonUtility.deNull(rs.getString("esn_load_from")));
				esnList.add(CommonUtility.deNull(rs.getString("esn_port_dis")));
				esnList.add(CommonUtility.deNull(rs.getString("terminal")));
				esnList.add(CommonUtility.deNull(rs.getString("scheme")));
				esnList.add(CommonUtility.deNull(rs.getString("combi_gc_scheme")));
				esnList.add(CommonUtility.deNull(rs.getString("combi_gc_ops_ind")));
			}
			log.info("getEsnDetails RESULT: " + Arrays.toString(esnList.toArray()));
		} catch (NullPointerException ne) {
			log.info("Exception getEsnDetails : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEsnDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEsnDetails  DAO  END");
		}
		return esnList;
	}

	// ejb.sessionBeans.gbms.cargo.generalcargo-->GCAEJB
	@Override
	public boolean updateManifestGCAHsCode(String mftSeqNbr, String hsCode, String hsCodeFrom, String hsCodeTo)
			throws BusinessException {
		boolean flag = false;
		
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: updateManifestGCAHsCode  DAO  Start mftSeqNbr " + mftSeqNbr + " hsCode" + hsCode + " hsCodeFrom" + hsCodeFrom + " hsCodeTo" + hsCodeTo);
			paramMap.put("hsCode", hsCode);
			paramMap.put("hsCodeFrom", hsCodeFrom);
			paramMap.put("hsCodeTo", hsCodeTo);
			paramMap.put("mftSeqNbr", mftSeqNbr);
			
			int count=0;
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT COUNT(MFT_HSCODE_SEQ_NBR)as CNT FROM GBMS.MANIFEST_HSCODE_DETAILS WHERE MFT_SEQ_NBR=:mftSeqNbr ");
				
			log.info(" Executing SQL " + sql.toString() + " paramMap :: " + paramMap);
			SqlRowSet countCheck = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
				
			if (countCheck.next()) {
				count = countCheck.getInt("CNT");
			}
				
			if (count > 1) {
				throw new BusinessException("Amend HS Code cannot use for ASN No with multiple HS Codes");
			} else if (count == 1) {
				//---------- UPDATE MANIFEST_DETAILS ----------
			StringBuffer sql1 = new StringBuffer();
			sql1.append("update MANIFEST_DETAILS set hs_code = :hsCode,hs_sub_code_fr = :hsCodeFrom,hs_sub_code_to=:hsCodeTo, last_modify_dttm=sysdate where mft_seq_nbr = :mftSeqNbr");
			
			log.info(" Executing SQL1 " + sql1.toString() + " paramMap :: " + paramMap);
			int result1 = namedParameterJdbcTemplate.update(sql1.toString(), paramMap);
			
			//---------- UPDATE MANIFEST_HSCODE_DETAILS ----------
			StringBuffer sql2 = new StringBuffer();
			sql2.append(" UPDATE GBMS.MANIFEST_HSCODE_DETAILS SET HS_CODE=:hsCode, HS_SUB_CODE_FR=:hsCodeFrom, HS_SUB_CODE_TO=:hsCodeTo, last_modify_dttm=sysdate where mft_seq_nbr = :mftSeqNbr");
			
			log.info(" Executing SQL2 " + sql2.toString() + " paramMap :: " + paramMap);
			int result2 = namedParameterJdbcTemplate.update(sql2.toString(), paramMap);
			
			//---------- UPDATE GB_EDO_HSCODE_DETAILS ----------
			StringBuffer sql3 = new StringBuffer();
			sql3.append("update GB_EDO_HSCODE_DETAILS set hs_code = :hsCode,hs_sub_code_fr = :hsCodeFrom,hs_sub_code_to=:hsCodeTo where mft_seq_nbr = :mftSeqNbr");
			
			log.info(" Executing SQL3 " + sql3.toString() + " paramMap :: " + paramMap);
			int result3 = namedParameterJdbcTemplate.update(sql3.toString(), paramMap);
			
			//---------- VALIDATE ----------
			if (result1 == 1 && result2 == 1 && result3 >= 1) {
				flag = true;
				log.info("Both updates successful. MANIFEST_DETAILS= " + result1 +" , MANIFEST_HSCODE_DETAILS= " + result2 +" , GB_EDO_HSCODE_DETAILS= " + result3);
			} else {
				log.info("Update failed. MANIFEST_DETAILS= " + result1 +" , MANIFEST_HSCODE_DETAILS= " + result2 +" , GB_EDO_HSCODE_DETAILS= " + result3);
				throw new BusinessException("Manifest Not Updated");
			}	
			} else {
				if (count == 0){
					log.info("No HS Code found in Mainfest" + mftSeqNbr);
				}
			}	
		} catch (BusinessException be) {
			log.info("Exception updateManifestGCAHsCode : ", be);
			throw new BusinessException(be.getMessage());
		} catch (NullPointerException ne) {
			log.info("Exception updateManifestGCAHsCode : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception updateManifestGCAHsCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateManifestGCAHsCode  DAO  END. Result: " + flag);
		}
		return flag;
	}

	// ejb.sessionBeans.gbms.cargo.generalcargo-->GCAEJB
	@Override
	public boolean updateEsnGCAHsCode(String esnNbr, String hsCode, String hsCodeFrom, String hsCodeTo)
			throws BusinessException {
		StringBuffer sqlStr = null;
		boolean flag = false;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: updateEsnGCAHsCode  DAO  Start esnNbr " + esnNbr + " hsCode" + hsCode + " hsCodeFrom"
					+ hsCodeFrom + " hsCodeTo" + hsCodeTo);

			sqlStr = new StringBuffer();
			sqlStr.append(
					"update ESN_DETAILS set esn_hs_code = :hsCode ,hs_sub_code_fr = :hsCodeFrom ,hs_sub_code_to= :hsCodeTo where esn_asn_nbr = :esnNbr");

			paramMap.put("hsCode", hsCode);
			paramMap.put("hsCodeFrom", hsCodeFrom);
			paramMap.put("hsCodeTo", hsCodeTo);
			paramMap.put("esnNbr", esnNbr);
			log.info(" updateEsnGCAHsCode  DAO  SQL " + sqlStr.toString() + " paramMap " + paramMap);
			int result = namedParameterJdbcTemplate.update(sqlStr.toString(), paramMap);

			StringBuffer sqlStrTesn = new StringBuffer();
			sqlStrTesn.append(
					"update TESN_PSA_JP set hs_cd = :hsCode ,hs_sub_code_fr = :hsCodeFrom ,hs_sub_code_to= :hsCodeTo where esn_asn_nbr = :esnNbr ");

			paramMap.put("hsCode", hsCode);
			paramMap.put("hsCodeFrom", hsCodeFrom);
			paramMap.put("hsCodeTo", hsCodeTo);
			paramMap.put("esnNbr", esnNbr);
			log.info(" updateEsnGCAHsCode  DAO  SQL " + sqlStrTesn.toString() + "paramMap " + paramMap);
			int resultTESN = namedParameterJdbcTemplate.update(sqlStrTesn.toString(), paramMap);

			sqlStr.setLength(0);
			sqlStr.append("update ESN set last_modify_dttm=sysdate where esn_asn_nbr = :esnNbr ");
			paramMap.put("esnNbr", esnNbr);
			log.info(" updateEsnGCAHsCode  DAO  SQL " + sqlStr.toString() + " paramMap " + paramMap);
			int result2 = namedParameterJdbcTemplate.update(sqlStr.toString(), paramMap);

			if ((result == 1 && result2 == 1) || (resultTESN == 1 && result2 == 1)) {
				flag = true;
				log.info("flag " + flag);
			} else {
				log.info("not updated ");
				throw new BusinessException("Manifest Not Updated");
			}
		} catch (BusinessException be) {
			log.info("Exception updateEsnGCAHsCode : ", be);
			throw new BusinessException(be.getMessage());
		} catch (NullPointerException ne) {
			log.info("Exception updateEsnGCAHsCode : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception updateEsnGCAHsCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateEsnGCAHsCode  DAO  END. Result: " + flag);
		}
		return flag;
	}

}
