package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.BalanceCargoRepository;
import sg.com.jp.generalcargo.domain.BalanceCargoVo;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("BalanceCargoRepo")
public class BalanceCargoJdbcRepository implements BalanceCargoRepository {
	
	private static final Log log = LogFactory.getLog(BalanceCargoJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// Region BalanceCargo
	//BalanceCargoEJB
	@Override
	public List<BalanceCargoVo> getVesselListForDPE(Criteria criteria) throws BusinessException {
		List<BalanceCargoVo> vesselList = new ArrayList<>();
		String custCd = CommonUtil.deNull(criteria.getPredicates().get("companyCode"));
		StringBuffer query = new StringBuffer();
		SqlRowSet rs = null;
		BalanceCargoVo vo = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: getVesselListForDPE  DAO  Start criteria:" + criteria.toString());
			query.setLength(0);
			query.append(" select distinct mf.var_nbr, vc.vsl_nm, vc.in_voy_nbr,vc.terminal ");
			query.append(" from manifest_details mf, ");
			query.append(
					" (select mft_seq_nbr,(sum(DN_NBR_PKGS) + sum(TRANS_DN_NBR_PKGS) + sum(nvl(CUT_OFF_NBR_PKGS,0))) total_delivered_pkgs from gb_edo ");
			query.append(" WHERE edo_status = 'A' group by mft_seq_nbr) edo, ");
			query.append(" vessel_call vc, berthing b ");
			query.append(" where mf.bl_status='A' ");
			query.append(" and mf.mft_seq_nbr =edo.mft_seq_nbr ");
			query.append(" and mf.edo_nbr_pkgs > edo.total_delivered_pkgs ");
			query.append(" and mf.var_nbr = vc.vv_cd ");
			query.append(" and vc.vv_cd = b.vv_cd ");
			query.append(
					" AND ((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
			// query.append(" and vc.terminal='GB' ");
			query.append(" and b.shift_ind=1 ");
			query.append(" and mf.special_action_pkgs < (mf.edo_nbr_pkgs - edo.total_delivered_pkgs) ");
			query.append(" and b.atu_dttm <= (sysdate - 10) ");
			query.append(
					" and  (mf.edo_nbr_pkgs- edo.total_delivered_pkgs -mf.SPECIAL_ACTION_PKGS -  nvl(mf.NBR_PKGS_IN_PORT,0)) >0 ");
			if (!"JP".equalsIgnoreCase(custCd)) {
				query.append(" and vc.VSL_OPR_CD = :custCd");
			}
			query.append(" union ");
			query.append(" select distinct mf.var_nbr,vc.vsl_nm,vc.in_voy_nbr,vc.terminal ");
			query.append(" from manifest_details mf, vessel_call vc, berthing b ");
			query.append(" where mf.bl_status='A' ");
			query.append(" and mf.var_nbr = vc.vv_cd ");
			query.append(" and vc.vv_cd = b.vv_cd ");
			query.append(
					" AND ((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
			// query.append(" and vc.terminal='GB' ");
			query.append(" and b.shift_ind=1 ");
			query.append(" and b.atu_dttm <= (sysdate - 10) ");
			query.append(" and MF.MFT_SEQ_NBR in (  ");
			query.append(" select MFT_SEQ_NBR from manifest_details where bl_status='A' ");
			query.append(" minus  ");
			query.append(" select MFT_SEQ_NBR from gb_edo where edo_status='A' ) ");
			if (!"JP".equalsIgnoreCase(custCd)) {
				// query.append(" and vc.VSL_OPR_CD = '").append(custCd).append("'");
				query.append(" and vc.VSL_OPR_CD = :custCd");
			}

			query.append(" order by terminal desc,vsl_nm, in_voy_nbr ");
			log.info("getVesselList() Query: " + query.toString());

			if (!"JP".equalsIgnoreCase(custCd)) {
				paramMap.put("custCd", custCd);
			}
			log.info(" ***paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(query.toString(), paramMap);
			while (rs.next()) {
				vo = new BalanceCargoVo();
				vo.setVesselVvCd(rs.getString("var_nbr"));
				vo.setVesselName(rs.getString("vsl_nm"));
				vo.setInVoyNbr(rs.getString("in_voy_nbr"));
				vo.setTerminal(CommonUtility.deNull(rs.getString("terminal")));
				vesselList.add(vo);
			}
			log.info("END: *** getVesselListForDPE Result *****" + vesselList.size());
		} catch (Exception e) {
			log.info("Exception getVesselListForDPE : ", e);
			throw new BusinessException("M4201");
		}
		finally {
			log.info("END: getVesselListForDPE Dao Impl END");
		}
		return vesselList;
	}

	@Override
	public List<BalanceCargoVo> getCompanyList() throws BusinessException {
		List<BalanceCargoVo> companyList = new ArrayList<>();
		StringBuffer query = new StringBuffer();
		SqlRowSet rs = null;
		BalanceCargoVo vo = null;
		log.info("START: getCompanyList Dao Start");
		query.setLength(0);
		query.append(" select CC.CO_CD, CC.CO_NM from COMPANY_CODE CC where CC.REC_STATUS='A' ");
		query.append(" order by CC.CO_NM ");
		log.info("getCompanyList() Query: "+query.toString());
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			
			log.info(" ***paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(query.toString(), paramMap);
			while(rs.next()){
				vo = new BalanceCargoVo();
				vo.setCoCd(rs.getString("CO_CD"));
				vo.setCoNm(rs.getString("CO_NM"));
				companyList.add(vo);
			}
			log.info("END: *** getCompanyList Result *****" + companyList.size());
		} catch(Exception e){
			log.info("Exception getCompanyList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCompanyList Dao Impl END");
		}
		return companyList;
    }
	
	
	public TableData getOutStandingCargoList(Criteria criteria) throws BusinessException{
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		TableData tableData = new TableData();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		try {
			log.info("START: getOutStandingCargoList  DAO  Start criteria:" + criteria.toString());
			String vslName="";
			String voyNbr="";
			
			String companyCd = CommonUtil.deNull(criteria.getPredicates().get("companyCode"));
			String dateFrom = CommonUtil.deNull(criteria.getPredicates().get("dateFrom"));
			String dateTo = CommonUtil.deNull(criteria.getPredicates().get("dateTo"));
			String vslDepartureDays = CommonUtil.deNull(criteria.getPredicates().get("vslDepartureDays"));
			
			Integer vlsDeparDays;
			try {
				vlsDeparDays = Integer.parseInt(vslDepartureDays);
			}catch(Exception ex) {
				// START Update vlsDeparDays value from JRPA NS JUNE 2023
				vlsDeparDays = 3;
				// END Update vlsDeparDays value from JRPA NS JUNE 2023
			}
			
			String blNbr = CommonUtil.deNull(criteria.getPredicates().get("blNbr"));
			String vslVoy = CommonUtil.deNull(criteria.getPredicates().get("vesselVoy"));
			if(vslVoy != null && !"".equals(vslVoy)){
				vslName = vslVoy.substring(0,vslVoy.indexOf('/') );
				//commented by Dongsheng on 14/1/2013. For CR-CIM-20121224-003
				//outVoyNbr = vslVoy.substring(vslVoy.indexOf('/')+1);
				voyNbr = vslVoy.substring(vslVoy.indexOf('/')+1);
			}
			String terminal = CommonUtil.deNull(criteria.getPredicates().get("terminal"));
			String varCode = CommonUtil.deNull(criteria.getPredicates().get("varCode"));
			String vslScheme = CommonUtil.deNull(criteria.getPredicates().get("vslScheme"));
			String agent = CommonUtil.deNull(criteria.getPredicates().get("agent"));
			String adp = CommonUtil.deNull(criteria.getPredicates().get("adp"));
			String edoAsnNbr = CommonUtil.deNull(criteria.getPredicates().get("edoAsnNbr"));
			/*query = query.append("select distinct edo.var_nbr, vc.vsl_nm, vc.out_voy_nbr, to_char(b.atu_dttm,'DDMMYYYY HH24MI') as atu_dttm, edo.bl_nbr, edo.edo_asn_nbr, edo.nbr_pkgs as total_pkgs, ");
			query = query.append( " (edo.nbr_pkgs - dn.total_delivered_pkgs - edo.SPECIAL_ACTION_PKGS) as balance_pkgs, dn.total_delivered_pkgs as total_pkgs_by_SST, ");

			query = query.append( " round(((edo.nbr_pkgs - dn.total_delivered_pkgs - edo.SPECIAL_ACTION_PKGS)/edo.nbr_pkgs)*edo.nom_wt,2) as balance_weight," );
			query = query.append( " round(((edo.nbr_pkgs - dn.total_delivered_pkgs - edo.SPECIAL_ACTION_PKGS)/edo.nbr_pkgs)*edo.nom_vol,2) as balance_volume, ");
			query = query.append( " edo.SPECIAL_ACTION_PKGS,edo.SPECIAL_ACTION_REMARKS ");

			query = query.append( " from gb_edo edo, (select edo_asn_nbr, sum(nbr_pkgs) total_delivered_pkgs from dn_details where dn_status='A' group by edo_asn_nbr ) dn, ");
			query = query.append( " vessel_call vc, berthing b ");

			query = query.append( " where edo.edo_status='A' ");
			query = query.append( " and edo.edo_asn_nbr =dn.edo_asn_nbr ");
			query = query.append( " and edo.nbr_pkgs > dn.total_delivered_pkgs ");
			query = query.append( " and edo.var_nbr = vc.vv_cd ");
			query = query.append( " and vc.vv_cd = b.vv_cd ");
			query = query.append( " and vc.terminal='GB' ");
			query = query.append( " and b.shift_ind=1 ");

			query = query.append( " and edo.special_action_pkgs < (edo.nbr_pkgs - dn.total_delivered_pkgs) ");
			query = query.append( " and b.atu_dttm <= (sysdate - ?)");
			*/
			/* Commented out by Dongsheng on 14/1/2012. For CR-CIM-20121224-003
			query = query.append(" select distinct mf.var_nbr,vc.vsl_nm, vc.out_voy_nbr, to_char(b.atu_dttm,'DDMMYYYY HH24MI') as atu_dttm,mf.bl_nbr,");
			   query = query.append(" (mf.nbr_pkgs - nvl(CUT_OFF_NBR_PKGS ,0)) as total_pkgs,");
			query = query.append(" (mf.edo_nbr_pkgs- edo.total_delivered_pkgs -mf.SPECIAL_ACTION_PKGS -   nvl(mf.NBR_PKGS_IN_PORT,0)) as balance_pkgs,");
			query = query.append(" edo.total_delivered_pkgs as total_pkgs_by_SST,");
			query = query.append(" round(((mf.edo_nbr_pkgs - edo.total_delivered_pkgs - mf.SPECIAL_ACTION_PKGS - nvl(mf.NBR_PKGS_IN_PORT,0))/mf.edo_nbr_pkgs)*mf. GROSS_wt,2) as balance_weight,");
			query = query.append(" round(((mf.edo_nbr_pkgs - edo.total_delivered_pkgs - mf.SPECIAL_ACTION_PKGS -  nvl(mf.NBR_PKGS_IN_PORT,0))/mf.edo_nbr_pkgs)*mf. GROSS_VOL,2) as balance_volume,");
			query = query.append(" mf.SPECIAL_ACTION_PKGS,mf.SPECIAL_ACTION_REMARKS,mf.NBR_PKGS_IN_PORT");
			query = query.append(" from manifest_details mf,");
			query = query.append(" (select mft_seq_nbr,(sum(DN_NBR_PKGS) + sum(TRANS_DN_NBR_PKGS) + sum(nvl(CUT_OFF_NBR_PKGS,0))) total_delivered_pkgs from gb_edo");
			query = query.append(" WHERE edo_status = 'A' group by mft_seq_nbr) edo,");
			query = query.append(" vessel_call vc, berthing b");
			query = query.append(" where mf.bl_status='A'");
			query = query.append(" and mf.mft_seq_nbr =edo.mft_seq_nbr");
			query = query.append(" and mf.edo_nbr_pkgs > edo.total_delivered_pkgs ");
			query = query.append(" and mf.var_nbr = vc.vv_cd ");
			query = query.append(" and vc.vv_cd = b.vv_cd ");
			query = query.append(" and vc.terminal='GB'");
			query = query.append(" and b.shift_ind=1 ");
			query = query.append(" and mf.special_action_pkgs < (mf.edo_nbr_pkgs - edo.total_delivered_pkgs)");
			query = query.append(" and b.atu_dttm <= (sysdate - ?)");
			query = query.append(" and  (mf.edo_nbr_pkgs- edo.total_delivered_pkgs -mf.SPECIAL_ACTION_PKGS -  nvl(mf.NBR_PKGS_IN_PORT,0)) >0" );
			*/
			log.info("START: getOutStandingCargoList Dao Start criteria:" + criteria.toString());
		
			StringBuffer query = new StringBuffer();
			
			query = query.append(" select distinct mf.var_nbr,vc.vsl_nm,vc.in_voy_nbr,vc.out_voy_nbr, to_char(b.atu_dttm,'DDMMYYYY HH24MI') as atu_dttm,");
			query = query.append(" to_char(b.atb_dttm,'DDMMYYYY HH24MI') as atb_dttm, mf.bl_nbr, a.edo_asn_nbr,");
		    query = query.append(" (a.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0)) as total_pkgs,");
			query = query.append(" (a.nbr_pkgs- (A.DN_NBR_PKGS+ A.TRANS_DN_NBR_PKGS + nvl(mf.CUT_OFF_NBR_PKGS,0))-mf.SPECIAL_ACTION_PKGS -   nvl(mf.NBR_PKGS_IN_PORT,0)) as balance_pkgs,");
			query = query.append(" A.DN_NBR_PKGS+ A.TRANS_DN_NBR_PKGS + nvl(mf.CUT_OFF_NBR_PKGS,0)  as total_pkgs_by_SST,");
			query = query.append(" round(((a.nbr_pkgs - (A.DN_NBR_PKGS+ A.TRANS_DN_NBR_PKGS + nvl(mf.CUT_OFF_NBR_PKGS,0)) - mf.SPECIAL_ACTION_PKGS - nvl(mf.NBR_PKGS_IN_PORT,0))/a.nbr_pkgs)*a. nom_wt,2) as balance_weight,");
			query = query.append(" round(((a.nbr_pkgs - (A.DN_NBR_PKGS+ A.TRANS_DN_NBR_PKGS + nvl(mf.CUT_OFF_NBR_PKGS,0)) - mf.SPECIAL_ACTION_PKGS - nvl(mf.NBR_PKGS_IN_PORT,0))/a.nbr_pkgs)*a. nom_VOL,2) as balance_volume,");
			query = query.append(" mf.SPECIAL_ACTION_PKGS,mf.SPECIAL_ACTION_REMARKS, nvl(mf.NBR_PKGS_IN_PORT,0) as shortlanded, VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND,A.CRG_STATUS,");
			query = query.append(" To_Char(B.GB_COD_DTTM,'DDMMYYYY HH24MI') as cod, ceil(sysdate- B.GB_COD_DTTM) as dwell_days,");
			query = query.append(" A.ADP_NM ADP, CC.CO_NM as AGENT, CASE WHEN wh.exp_date is not null THEN 'Warehouse Exp Dt: '|| to_char(wh.exp_date,'DD/MM/YYYY') ELSE null END as WH_EXP_DATE, ");
			query = query.append(" B.GB_COD_DTTM, A.ACCT_NBR");
			query = query.append(" ,mf.CRG_DES, wh.WA_REF_NBR, decode(A.WH_IND,'Y', 'Yes', 'No') WH_IND, mf.mixed_scheme_acct_nbr ,VC.VSL_OPR_CD,  A.ADP_CUST_CD, A.ADP_IC_TDBCR_nbr ");
			query = query.append(" from manifest_details mf,");
			query = query.append(" (select mft_seq_nbr,(sum(DN_NBR_PKGS) + sum(TRANS_DN_NBR_PKGS) + sum(nvl(CUT_OFF_NBR_PKGS,0))) total_delivered_pkgs from gb_edo");
			query = query.append(" WHERE edo_status = 'A' group by mft_seq_nbr) edo,");
			query = query.append(" vessel_call vc, berthing b, GB_EDO A, company_code cc, ");
			query = query.append(" (select edo_esn_nbr, max(wa_running_nbr) as wa_ref_nbr, max(end_date) as exp_date from wa_appln_details  where rec_status in ('A','R','C') and  edo_esn_nbr is not null ");
			//query = query.append("  having  max(wa_running_nbr) = (select max(wa_running_nbr) from wa_appln_details )");
			query = query.append(" group by edo_esn_nbr) wh")   ;
			query = query.append(" where mf.bl_status='A'");
			query = query.append(" and mf.mft_seq_nbr =edo.mft_seq_nbr");
			//Changed condition from > to >= to include records which are yet to create EDO Sripriya 8 April 2016
			query = query.append(" and mf.edo_nbr_pkgs > edo.total_delivered_pkgs ");
			// End Sripriya 8 April 2016
			query = query.append(" and mf.var_nbr = vc.vv_cd ");
			query = query.append(" and vc.vv_cd = b.vv_cd ");
			query = query.append(" AND ((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
			//query = query.append(" and vc.terminal='GB'");
			query = query.append(" and A.MFT_SEQ_NBR(+) = mf.MFT_SEQ_NBR ");
			query = query.append(" and a.edo_asn_nbr = wh.edo_esn_nbr(+) ");
			query = query.append(" AND A.EDO_STATUS = 'A' ");
			query = query.append(" AND A.nbr_pkgs > A.dn_nbr_pkgs ");
			query = query.append(" and b.shift_ind=1 ");
			query = query.append(" and VC.CREATE_CUST_CD = cc.co_cd");
			query = query.append(" and mf.special_action_pkgs < (mf.edo_nbr_pkgs - edo.total_delivered_pkgs)");
			query = query.append(" and b.atu_dttm <= (sysdate - :vlsDeparDays)");
			query = query.append(" and mf.var_nbr in (select vv_cd FROM berthing where shift_ind=1 and atu_dttm > sysdate - 2700)"); //Added by TOS for ITSM 56698
			query = query.append(" and  (mf.edo_nbr_pkgs- edo.total_delivered_pkgs -mf.SPECIAL_ACTION_PKGS -  nvl(mf.NBR_PKGS_IN_PORT,0)) >0" );
			
			if (companyCd != null && !companyCd.isEmpty() && !"JP".equalsIgnoreCase(companyCd)) {
				query = query.append(" and  (A.ADP_CUST_CD = :companyCd OR VC.VSL_OPR_CD =:companyCd OR " +
									" a.edo_asn_nbr IN (SELECT distinct (SUB.ESN_ASN_NBR) FROM SUB_ADP SUB WHERE SUB.TRUCKER_CO_CD = :companyCd AND STATUS_CD = 'A' AND EDO_ESN_IND = '1'))" );
			}
			
			log.info("The SQL is of the first part before some ANDs1: " + sb.toString());

			if (dateFrom != null && !"".equals(dateFrom)) {
				query = query.append(" and  b.atb_dttm >= to_date(:dateFrom,'DDMMYYYY')");
			}
			if (dateTo != null && !"".equals(dateTo)) {
				query = query.append(" and  b.atb_dttm <= to_date(:dateTo,'DDMMYYYY')");
			}
			/*Amended by Punitha on 16/02/2009 to add the BL Nbr
			if (esnEdoNbr != null && !"".equals(esnEdoNbr)){
				query = query.append(" and  edo.edo_asn_nbr = ? ");
			}*/

			if (blNbr != null && !"".equals(blNbr)){
				//to change exact matching to wildcard searching. For CR-CIM-20121224-003. Dongsheng on 2/2/2013
				//query = query.append(" and  mf.bl_nbr = ? ");
				query = query.append(" and  upper(mf.bl_nbr) like :blNbr ");
			}

			if (vslName != null && !"".equals(vslName)) {
				query = query.append(" and  upper(vc.vsl_nm) like :vslName ");
			}
			
			if (terminal != null && !"".equals(terminal)) {
				query = query.append(" and  upper(vc.terminal) like :terminal ");
			}
			/* Amended by Dongsheng on 14/1/2013 for CR-CIM-20121224-003
			if (outVoyNbr != null && !"".equals(outVoyNbr)) {
				query = query.append(" and vc.out_voy_nbr=?  ");
			}
			*/
			if (voyNbr != null && !"".equals(voyNbr)) {
				query = query.append(" and vc.in_voy_nbr=:voyNbr  ");
			}

			if (varCode != null && !"".equals(varCode)) {
				query = query.append(" and vc.vv_cd =:varCode  ");
			}
			if (vslScheme != null && !"".equals(vslScheme)) {
				query = query.append(" and vc.SCHEME =:vslScheme  ");
			}
			if (adp != null && !"".equals(adp)) {
				query = query.append(" and A.adp_cust_cd = :adp  ");
			}
			if (edoAsnNbr != null && !"".equals(edoAsnNbr)) {
				edoAsnNbr = edoAsnNbr.trim();
				edoAsnNbr = StringUtils.replace(edoAsnNbr, " ", "");
				edoAsnNbr = StringUtils.replace(edoAsnNbr, ",", "','");
			//	edoAsnNbr = "'" + edoAsnNbr + "'";
				log.info(" edoAsnNbr *********** :"+ edoAsnNbr);
				query = query.append(" and  a.edo_asn_nbr in ( :edoAsnNbr )");
			}
			if (agent != null && !"".equals(agent)) {
				query = query.append(" and VC.CREATE_CUST_CD =:agent  ");
			}
			query = query.append(" union ");
			query = query.append(" select distinct mf.var_nbr,vc.vsl_nm,vc.in_voy_nbr,vc.out_voy_nbr, to_char(b.atu_dttm,'DDMMYYYY HH24MI') as atu_dttm, ");
			query = query.append(" to_char(b.atb_dttm,'DDMMYYYY HH24MI') as atb_dttm, mf.bl_nbr, null as EDO_ASN_NBR,");
			query = query.append(" (mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0)) as total_pkgs,  ");
			query = query.append("  (mf.nbr_pkgs- mf.edo_nbr_pkgs-mf.SPECIAL_ACTION_PKGS -   nvl(mf.NBR_PKGS_IN_PORT,0)) as balance_pkgs,");
			query = query.append(" (gb_edo.dn_nbr_pkgs+gb_edo.trans_dn_nbr_pkgs) as total_pkgs_by_SST,  ");
			query = query.append(" round(((mf.nbr_pkgs -MF.EDO_NBR_PKGS- nvl(mf.CUT_OFF_NBR_PKGS ,0) - mf.SPECIAL_ACTION_PKGS - nvl(mf.NBR_PKGS_IN_PORT,0))/(mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0)))*mf. GROSS_wt,2) as balance_weight,  ");
			query = query.append("  round(((mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0) - mf.SPECIAL_ACTION_PKGS - nvl(mf.NBR_PKGS_IN_PORT,0))/(mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0)))*mf. GROSS_VOL,2) as balance_volume,  mf.SPECIAL_ACTION_PKGS,mf.SPECIAL_ACTION_REMARKS, nvl(mf.NBR_PKGS_IN_PORT,0) as shortlanded, ");
			query = query.append("  VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME,VC.COMBI_GC_OPS_IND, MF.CRG_STATUS,  To_Char(B.GB_COD_DTTM,'DDMMYYYY HH24MI') as cod, ceil(sysdate- B.GB_COD_DTTM) as dwell_days, null as ADP, CC.CO_NM as AGENT, null as WH_EXP_DATE,  B.GB_COD_DTTM, null as ACCT_NBR  ,mf.CRG_DES, null as WA_REF_NBR, null as WH_IND, mf.mixed_scheme_acct_nbr,VC.VSL_OPR_CD , ");
			query = query.append(" gb_edo.adp_nm as ADP_CUST_CD, gb_edo.ADP_IC_TDBCR_nbr as ADP_IC_TDBCR_nbr from manifest_details mf, GB_EDO ,vessel_call vc, berthing b, company_code cc ");
			query = query.append("  where mf.bl_status='A' and mf.var_nbr = vc.vv_cd and vc.vv_cd = b.vv_cd  ");
			query = query.append(" AND ((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
			//		"and vc.terminal='GB'" +
			query = query.append("  and b.shift_ind=1  and VC.CREATE_CUST_CD = cc.co_cd ");
			query = query.append(" and b.atu_dttm <= (sysdate - :vlsDeparDays)");
			//Removed by TOS for ITSM 56698 - Starts
			//query = query.append(" and MF.MFT_SEQ_NBR in (   select MFT_SEQ_NBR from manifest_details where bl_status='A' minus  ");
			//Removed by TOS for ITSM 56698 - Ends
			query = query.append(" and MF.MFT_SEQ_NBR in (   select MFT_SEQ_NBR from manifest_details where bl_status='A' and var_nbr in (select vv_cd from berthing where shift_ind=1 and atu_dttm > sysdate - 2700) minus  "); //Added by TOS for ITSM 56698
			query = query.append(" select GB_EDO.MFT_SEQ_NBR from gb_edo , MANIFEST_DETAILS where manifest_details.edo_nbr_pkgs = gb_EDO.DN_NBR_PKGS+GB_EDO.TRANS_DN_NBR_PKGS ");
			query = query.append("   AND GB_EDO.edo_status='A' AND MAnifest_dETAILS.BL_STATUS = 'A' AND GB_EDO.MFT_SEQ_NBR = MANIFEST_DETAILS.MFT_SEQ_NBR) ");
			query = query.append(" and ((mf.nbr_pkgs-gb_edo.nbr_pkgs-gb_edo.dn_nbr_pkgs-gb_edo.trans_dn_nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0) - mf.SPECIAL_ACTION_PKGS -  nvl(mf.NBR_PKGS_IN_PORT,0)) >0) ");
			query = query.append(" and  GB_EDO.MFT_SEQ_NBR = mf.MFT_SEQ_NBR AND ");
			query = query.append("  gb_edo.nbr_pkgs <> (GB_EDO.DN_NBR_PKGS + gb_edo.trans_dn_nbr_pkgs) and ");
			query = query.append(" gb_edo.edo_status = 'A'");
			query = query.append(" and nvl(mf.edo_nbr_pkgs,0) < nvl(mf.nbr_pkgs,0) ");
			if (companyCd != null && !"JP".equalsIgnoreCase(companyCd)) {
				query = query.append(" and  (VC.VSL_OPR_CD = :companyCd)" );
			}
			if (dateFrom != null && !"".equals(dateFrom)) {
				query = query.append(" and  b.atb_dttm >= to_date(:dateFrom,'DDMMYYYY')");
			}
			if (dateTo != null && !"".equals(dateTo)) {
				query = query.append(" and  b.atb_dttm <= to_date(:dateTo,'DDMMYYYY')");
			}

			if (blNbr != null && !"".equals(blNbr)){
				//to change exact matching to wildcard searching. For CR-CIM-20121224-003. Dongsheng on 2/2/2013
				//query = query.append(" and  mf.bl_nbr = ? ");
				query = query.append(" and  upper(mf.bl_nbr) like :blNbr ");
			}

			if (vslName != null && !"".equals(vslName)) {
				query = query.append(" and  upper(vc.vsl_nm) like :vslName ");
			}
			
			if (terminal != null && !"".equals(terminal)) {
				query = query.append(" and  upper(vc.terminal) like :terminal ");
			}
			/* Amended by Dongsheng on 14/1/2013 for CR-CIM-20121224-003
			if (outVoyNbr != null && !"".equals(outVoyNbr)) {
				query = query.append(" and vc.out_voy_nbr=?  ");
			}
			*/
			if (voyNbr != null && !"".equals(voyNbr)) {
				query = query.append(" and vc.in_voy_nbr= :voyNbr  ");
			}

			if (varCode != null && !"".equals(varCode)) {
				query = query.append(" and vc.vv_cd = :varCode  ");
			}
			if (vslScheme != null && !"".equals(vslScheme)) {
				query = query.append(" and vc.SCHEME = :vslScheme  ");
			}
			if (agent != null && !"".equals(agent)) {
				query = query.append(" and VC.CREATE_CUST_CD = :agent  ");
			}
			if (adp != null && !"".equals(adp)) {
				query = query.append(" and 0 >1  ");
			}
			if (edoAsnNbr != null && !"".equals(edoAsnNbr)) {
				query = query.append(" and  0 >1 ");
			}
			
			//to add the portion of manifest without EDO. For CR-CIM-20121224-003. Dongsheng on 14/1/2013.
			query = query.append(" union ");
			query = query.append(" select distinct mf.var_nbr,vc.vsl_nm,vc.in_voy_nbr,vc.out_voy_nbr, to_char(b.atu_dttm,'DDMMYYYY HH24MI') as atu_dttm, ");
			query = query.append(" to_char(b.atb_dttm,'DDMMYYYY HH24MI') as atb_dttm, mf.bl_nbr, null as EDO_ASN_NBR, ");
			query = query.append(" (mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0)) as total_pkgs, ");
			query = query.append(" (mf.nbr_pkgs- mf.SPECIAL_ACTION_PKGS - nvl(mf.NBR_PKGS_IN_PORT,0)) as balance_pkgs, ");
			query = query.append(" 0 as total_pkgs_by_SST,");
			query = query.append(" round(((mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0) - mf.SPECIAL_ACTION_PKGS - nvl(mf.NBR_PKGS_IN_PORT,0))/(mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0)))*mf. GROSS_wt,2) as balance_weight, ");
			query = query.append(" round(((mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0) - mf.SPECIAL_ACTION_PKGS - nvl(mf.NBR_PKGS_IN_PORT,0))/(mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0)))*mf. GROSS_VOL,2) as balance_volume, ");
			query = query.append(" mf.SPECIAL_ACTION_PKGS,mf.SPECIAL_ACTION_REMARKS, nvl(mf.NBR_PKGS_IN_PORT,0) as shortlanded, VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND, MF.CRG_STATUS, ");
			query = query.append(" To_Char(B.GB_COD_DTTM,'DDMMYYYY HH24MI') as cod, ceil(sysdate- B.GB_COD_DTTM) as dwell_days,");
			query = query.append(" null as ADP,");
			query = query.append(" CC.CO_NM as AGENT, null as WH_EXP_DATE, ");
			query = query.append(" B.GB_COD_DTTM, null as ACCT_NBR ");
			query = query.append(" ,mf.CRG_DES, null as WA_REF_NBR, null as WH_IND, mf.mixed_scheme_acct_nbr,VC.VSL_OPR_CD , null as ADP_CUST_CD, null as ADP_IC_TDBCR_nbr");
			query = query.append(" from manifest_details mf, vessel_call vc, berthing b, company_code cc");
			query = query.append(" where mf.bl_status='A'");
			query = query.append(" and mf.var_nbr = vc.vv_cd");
			query = query.append(" and vc.vv_cd = b.vv_cd ");
			query = query.append(" AND ((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
			//query = query.append(" and vc.terminal='GB'");
			query = query.append(" and b.shift_ind=1 ");
			query = query.append(" and VC.CREATE_CUST_CD = cc.co_cd ");
			query = query.append(" and b.atu_dttm <= (sysdate - :vlsDeparDays)");
			query = query.append(" and MF.MFT_SEQ_NBR in ( ");
			query = query.append("  select MFT_SEQ_NBR from manifest_details where bl_status='A'");
			query = query.append("  and var_nbr in (select vv_cd from berthing where shift_ind=1 and atu_dttm > sysdate - 2700)"); //Added by TOS for ITSM 56698
			query = query.append(" minus ");
			query = query.append(" select MFT_SEQ_NBR from gb_edo where edo_status='A' ) ");
			query = query.append(" and (mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0) - mf.SPECIAL_ACTION_PKGS -  nvl(mf.NBR_PKGS_IN_PORT,0)) >0 ");

			log.info("Third part query ends" + sb.toString());
			
			if (companyCd != null && !"JP".equalsIgnoreCase(companyCd)) {
				query = query.append(" and  (VC.VSL_OPR_CD = :companyCd)" );
			}
			if (dateFrom != null && !"".equals(dateFrom)) {
				query = query.append(" and  b.atb_dttm >= to_date(:dateFrom,'DDMMYYYY')");
			}
			if (dateTo != null && !"".equals(dateTo)) {
				query = query.append(" and  b.atb_dttm <= to_date(:dateTo,'DDMMYYYY')");
			}

			if (blNbr != null && !"".equals(blNbr)){
				//to change exact matching to wildcard searching. For CR-CIM-20121224-003. Dongsheng on 2/2/2013
				//query = query.append(" and  mf.bl_nbr = ? ");
				query = query.append(" and  upper(mf.bl_nbr) like :blNbr ");
			}

			if (vslName != null && !"".equals(vslName)) {
				query = query.append(" and  upper(vc.vsl_nm) like :vslName ");
			}
			
			if (terminal != null && !"".equals(terminal)) {
				query = query.append(" and  upper(vc.terminal) like :terminal ");
			}
			/* Amended by Dongsheng on 14/1/2013 for CR-CIM-20121224-003
			if (outVoyNbr != null && !"".equals(outVoyNbr)) {
				query = query.append(" and vc.out_voy_nbr=?  ");
			}
			*/
			if (voyNbr != null && !"".equals(voyNbr)) {
				query = query.append(" and vc.in_voy_nbr= :voyNbr  ");
			}

			if (varCode != null && !"".equals(varCode)) {
				query = query.append(" and vc.vv_cd = :varCode  ");
			}
			if (vslScheme != null && !"".equals(vslScheme)) {
				query = query.append(" and vc.SCHEME = :vslScheme  ");
			}
			if (agent != null && !"".equals(agent)) {
				query = query.append(" and VC.CREATE_CUST_CD =:agent  ");
			}
			if (adp != null && !"".equals(adp)) {
				query = query.append(" and gb_edo.adp_cust_cd = :adp  ");
			}
			if (edoAsnNbr != null && !"".equals(edoAsnNbr)) {
				query = query.append(" and  0 >1 ");
			}
			// end of addition for CR-CIM-20121224-003.

			/*Amended by Punitha on 16/02/2009 to add the BL Nbr
			query.append(" order by vc.vsl_nm, vc.out_voy_nbr, edo.edo_asn_nbr");*/
			/*Amended by Dongsheng on 14/1/2013 for CR-CIM-20121224-003. To use In Voy No */
			//query.append(" order by vc.vsl_nm, vc.out_voy_nbr, mf.bl_nbr");
			query.append(" order by GB_COD_DTTM, vsl_nm, in_voy_nbr, bl_nbr");

			log.info("getOutStandingCargoList Query: "+query.toString());
			
			/*TableData tableData = new TableData();
			tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
					parameters, Integer.class));*/
			
			// get the total count
			String totalCountSql = "SELECT COUNT(*) FROM (" + query.toString() +" ) ";
			paramMap.put("vlsDeparDays", vlsDeparDays);
			
			if (companyCd != null && !"JP".equalsIgnoreCase(companyCd)) {
				paramMap.put("companyCd", companyCd);
			}
			if (dateFrom != null && !"".equals(dateFrom)) {
				paramMap.put("dateFrom", dateFrom);
			}
			if (dateTo != null && !"".equals(dateTo)) {
				paramMap.put("dateTo", dateTo);
			}

			if (blNbr != null && !"".equals(blNbr)) {
				paramMap.put("blNbr", "%" + blNbr.toUpperCase() + "%");
			}
			if (vslName != null && !"".equals(vslName)) {
				paramMap.put("vslName", "%" + vslName.toUpperCase() + "%");
			}

			if (terminal != null && !"".equals(terminal)) {
				paramMap.put("terminal", "%" + terminal.toUpperCase() + "%");
			}

			if (voyNbr != null && !"".equals(voyNbr)) {
				paramMap.put("voyNbr", voyNbr);
			}
			if (varCode != null && !"".equals(varCode)) {
				paramMap.put("varCode", varCode);
			}
			if (vslScheme != null && !"".equals(vslScheme)) {
				paramMap.put("vslScheme", vslScheme);
			}
			if (agent != null && !"".equals(agent)) {
				paramMap.put("agent", agent);
			}
			if (adp != null && !"".equals(adp)) {
				paramMap.put("adp", adp);
			}
			if (edoAsnNbr != null && !"".equals(edoAsnNbr)) {
				paramMap.put("edoAsnNbr", edoAsnNbr);
			}
		 	log.info("SQL - " + totalCountSql);
		 	log.info(" ***paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(totalCountSql, paramMap);
			//to get the count
			
			while (rs.next())
			{
				tableData.setTotal(Integer.parseInt(CommonUtility.deNull(rs.getString(1))));
			}
			
			String baListsql= query.toString();
			if(!criteria.getPredicates().containsKey("excel")||criteria.getPredicates().get("excel").equalsIgnoreCase("false")) {
				if (criteria.isPaginated()) {
				baListsql = CommonUtil.getPaginatedSql(query.toString(), criteria.getStart(), criteria.getLimit());

				}
			}
			
			// Added For seqNo pagination also handling here
			// String baListsql = "SELECT * FROM (SELECT ROW_.*, ROWNUM seqNo FROM (" + query.toString()
			//					+ ") ROW_ WHERE ROWNUM <= " + (start + limit) + ") WHERE seqNo > " + start;
			
			log.info("getOutStandingCargoList Query: **********  "+baListsql.toString());
			paramMap.put("vlsDeparDays", vlsDeparDays);
			
			if (companyCd != null && !"JP".equalsIgnoreCase(companyCd)) {
				paramMap.put("companyCd", companyCd);
			}
			if (dateFrom != null && !"".equals(dateFrom)) {
				paramMap.put("dateFrom", dateFrom);
			}
			if (dateTo != null && !"".equals(dateTo)) {
				paramMap.put("dateTo", dateTo);
			}

			if (blNbr != null && !"".equals(blNbr)) {
				paramMap.put("blNbr", "%" + blNbr.toUpperCase() + "%");
			}
			if (vslName != null && !"".equals(vslName)) {
				paramMap.put("vslName", "%" + vslName.toUpperCase() + "%");
			}

			if (terminal != null && !"".equals(terminal)) {
				paramMap.put("terminal", "%" + terminal.toUpperCase() + "%");
			}

			if (voyNbr != null && !"".equals(voyNbr)) {
				paramMap.put("voyNbr", voyNbr);
			}
			if (varCode != null && !"".equals(varCode)) {
				paramMap.put("varCode", varCode);
			}
			if (vslScheme != null && !"".equals(vslScheme)) {
				paramMap.put("vslScheme", vslScheme);
			}
			if (agent != null && !"".equals(agent)) {
				paramMap.put("agent", agent);
			}
			if (adp != null && !"".equals(adp)) {
				paramMap.put("adp", adp);
			}
			if (edoAsnNbr != null && !"".equals(edoAsnNbr)) {
				paramMap.put("edoAsnNbr", edoAsnNbr);
			}
			log.info(" ***paramMap *****" + paramMap.toString());
			
			rs = namedParameterJdbcTemplate.queryForRowSet(baListsql.toString(), paramMap);
			TopsModel topsModel = new TopsModel();
			String acctNbr = getAccountNbr(companyCd);
			Map<String,String> ret = getAllSubAdp();
			while (rs.next())
			{
				BalanceCargoVo vo = new BalanceCargoVo();
				vo= populateVO(rs, "Outstanding", companyCd, acctNbr, ret);
				if (vo != null && vo.getBlNbr() != null) {
					//balanceCargoVoList.add(vo);
					topsModel.put(vo);
				}
			}
			tableData.setListData(topsModel);
			
			log.info("END: *** getOutStandingCargoList Result *****" + tableData);
		} catch (Exception e) {
			log.info("Exception getOutStandingCargoList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getOutStandingCargoList Dao Impl  END *****");
		}
		return tableData;
	}
	
	@Override
	public TableData getCompletedDeliveryCargoList(Criteria criteria) throws BusinessException {
		SqlRowSet rs = null;
		TableData tableData = new TableData();
		StringBuffer query = new StringBuffer();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		try {
			log.info("START: getCompletedDeliveryCargoList Dao Start criteria:" + criteria.toString());
			String vslName="";
			String voyNbr="";
			String companyCd = CommonUtil.deNull(criteria.getPredicates().get("companyCode"));
			String dateFrom = CommonUtil.deNull(criteria.getPredicates().get("dateFrom"));
			String dateTo = CommonUtil.deNull(criteria.getPredicates().get("dateTo"));
			String vslDepartureDays = CommonUtil.deNull(criteria.getPredicates().get("vslDepartureDays"));
			
			Integer vlsDeparDays;
			try {
				vlsDeparDays = Integer.parseInt(vslDepartureDays);
			}catch(Exception ex) {
				vlsDeparDays = 10;
			}
			
			String blNbr = CommonUtil.deNull(criteria.getPredicates().get("blNbr"));
			String vslVoy = CommonUtil.deNull(criteria.getPredicates().get("vesselVoy"));
			if(vslVoy != null && !"".equals(vslVoy)){
				vslName = vslVoy.substring(0,vslVoy.indexOf('/') );
				//commented by Dongsheng on 14/1/2013. For CR-CIM-20121224-003
				//outVoyNbr = vslVoy.substring(vslVoy.indexOf('/')+1);
				voyNbr = vslVoy.substring(vslVoy.indexOf('/')+1);
			}
			String terminal = CommonUtil.deNull(criteria.getPredicates().get("terminal"));
			String varCode = CommonUtil.deNull(criteria.getPredicates().get("varCode"));
			String vslScheme = CommonUtil.deNull(criteria.getPredicates().get("vslScheme"));
			String agent = CommonUtil.deNull(criteria.getPredicates().get("agent"));
			String adp = CommonUtil.deNull(criteria.getPredicates().get("adp"));
			String edoAsnNbr = CommonUtil.deNull(criteria.getPredicates().get("edoAsnNbr"));
			/*Amended by Punitha on 16/02/2009 to change the existing retrieval from gb_edo to manifest_details table
				query = query.append("select distinct edo.var_nbr, vc.vsl_nm, vc.out_voy_nbr, to_char(b.atu_dttm,'DDMMYYYY HH24MI') as atu_dttm, edo.bl_nbr, edo.edo_asn_nbr, edo.nbr_pkgs as total_pkgs,  ");
				query = query.append( " (edo.nbr_pkgs - dn.total_delivered_pkgs - edo.SPECIAL_ACTION_PKGS) as balance_pkgs, dn.total_delivered_pkgs as total_pkgs_by_SST,");
				query = query.append( " round(((edo.nbr_pkgs - dn.total_delivered_pkgs - edo.SPECIAL_ACTION_PKGS)/edo.nbr_pkgs)*edo.nom_wt,2) as balance_weight," );
				query = query.append( " round(((edo.nbr_pkgs - dn.total_delivered_pkgs - edo.SPECIAL_ACTION_PKGS)/edo.nbr_pkgs)*edo.nom_vol,2) as balance_volume, ");
				query = query.append( " edo.SPECIAL_ACTION_PKGS,edo.SPECIAL_ACTION_REMARKS ");

				query = query.append( " from gb_edo edo, (select edo_asn_nbr, sum(nbr_pkgs) total_delivered_pkgs from dn_details where dn_status='A' group by edo_asn_nbr ) dn, ");
				query = query.append( " vessel_call vc, berthing b ");

				query = query.append( " where edo.edo_status='A'");
				query = query.append( " and edo.edo_asn_nbr =dn.edo_asn_nbr ");
				query = query.append( " and edo.nbr_pkgs > dn.total_delivered_pkgs ");
				query = query.append( " and edo.var_nbr = vc.vv_cd ");
				query = query.append( " and vc.vv_cd = b.vv_cd ");
				query = query.append( " and vc.terminal='GB' ");
				query = query.append( " and b.shift_ind=1 ");
				query = query.append( " and b.atu_dttm <= (sysdate - ?) ");
				query = query.append( " and edo.special_action_pkgs >= (edo.nbr_pkgs - dn.total_delivered_pkgs)and edo.special_action_pkgs >0 ");
				*/

			/* Amended by Dongsheng on 14/1/2013. For CR-CIM-20121224-003.
			query = query.append(" select distinct mf.var_nbr,vc.vsl_nm, vc.out_voy_nbr, to_char(b.atu_dttm,'DDMMYYYY HH24MI') as atu_dttm,mf.bl_nbr,");
			query = query.append(" (mf.nbr_pkgs - nvl(CUT_OFF_NBR_PKGS ,0)) as total_pkgs,");
			query = query.append(" (mf.edo_nbr_pkgs- edo.total_delivered_pkgs -mf.SPECIAL_ACTION_PKGS -  mf.NBR_PKGS_IN_PORT) as balance_pkgs,");
			query = query.append(" edo.total_delivered_pkgs as total_pkgs_by_SST,");
			query = query.append(" round(((mf.edo_nbr_pkgs - edo.total_delivered_pkgs - mf.SPECIAL_ACTION_PKGS - mf.NBR_PKGS_IN_PORT)/mf.edo_nbr_pkgs)* mf. GROSS_wt,2) as balance_weight, ");
			query = query.append(" round(((mf.edo_nbr_pkgs - edo.total_delivered_pkgs - mf.SPECIAL_ACTION_PKGS -  mf.NBR_PKGS_IN_PORT)/mf.edo_nbr_pkgs)* mf. GROSS_VOL,2) as balance_volume,");
			query = query.append(" mf.SPECIAL_ACTION_PKGS,mf.SPECIAL_ACTION_REMARKS,mf.NBR_PKGS_IN_PORT ");
			query = query.append(" from manifest_details mf,");
			query = query.append(" (select mft_seq_nbr,(sum(DN_NBR_PKGS) + sum(TRANS_DN_NBR_PKGS) + sum(nvl(CUT_OFF_NBR_PKGS,0))) total_delivered_pkgs from gb_edo ");
			query = query.append(" WHERE edo_status = 'A' group by mft_seq_nbr) edo,");
			query = query.append(" vessel_call vc, berthing b");
			query = query.append(" where mf.bl_status='A'");
			query = query.append(" and mf.mft_seq_nbr =edo.mft_seq_nbr");
			query = query.append(" and mf.edo_nbr_pkgs > edo.total_delivered_pkgs ");
			query = query.append(" and mf.var_nbr = vc.vv_cd ");
			query = query.append(" and vc.vv_cd = b.vv_cd ");
			query = query.append(" and vc.terminal='GB'");
			query = query.append(" and b.shift_ind=1 ");
			query = query.append(" and mf.special_action_pkgs >= (mf.nbr_pkgs - edo.total_delivered_pkgs)and mf.special_action_pkgs >0");
			query = query.append(" and b.atu_dttm <= (sysdate - ?) ");
			 */

			query.append(" select distinct mf.var_nbr,vc.vsl_nm, vc.in_voy_nbr, vc.out_voy_nbr, to_char(b.atu_dttm,'DDMMYYYY HH24MI') as atu_dttm,");
			query.append(" to_char(b.atb_dttm,'DDMMYYYY HH24MI') as atb_dttm,mf.bl_nbr, a.edo_asn_nbr,");
			query.append(" (mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0)) as total_pkgs,");
			query.append(" (mf.edo_nbr_pkgs- edo.total_delivered_pkgs - mf.SPECIAL_ACTION_PKGS -  nvl(mf.NBR_PKGS_IN_PORT,0)) as balance_pkgs,");
			query.append(" edo.total_delivered_pkgs as total_pkgs_by_SST,");
			query.append(" round(((mf.edo_nbr_pkgs - edo.total_delivered_pkgs - mf.SPECIAL_ACTION_PKGS - nvl(mf.NBR_PKGS_IN_PORT,0))/mf.edo_nbr_pkgs)* mf. GROSS_wt,2) as balance_weight, ");
			query.append(" round(((mf.edo_nbr_pkgs - edo.total_delivered_pkgs - mf.SPECIAL_ACTION_PKGS - nvl(mf.NBR_PKGS_IN_PORT,0))/mf.edo_nbr_pkgs)* mf. GROSS_VOL,2) as balance_volume,");
			query.append(" mf.SPECIAL_ACTION_PKGS, mf.SPECIAL_ACTION_REMARKS, nvl(mf.NBR_PKGS_IN_PORT,0) as NBR_PKGS_IN_PORT, VC.TERMINAL, VC.SCHEME,VC.COMBI_GC_SCHEME,VC.COMBI_GC_OPS_IND, MF.CRG_STATUS,  To_Char(B.GB_COD_DTTM,'DDMMYYYY HH24MI') as cod, ");
			query.append(" A.ADP_NM ADP, CC.CO_NM as AGENT, B.GB_COD_DTTM, A.ACCT_NBR ");
			query.append(" ,mf.CRG_DES, wh.WA_REF_NBR, A.WH_IND, mf.mixed_scheme_acct_nbr,VC.VSL_OPR_CD, A.ADP_CUST_CD, A.ADP_IC_TDBCR_nbr");
			query.append(" from manifest_details mf,");
			query.append(" (select mft_seq_nbr,(sum(DN_NBR_PKGS) + sum(TRANS_DN_NBR_PKGS) + sum(nvl(CUT_OFF_NBR_PKGS,0))) total_delivered_pkgs from gb_edo ");
			query.append(" WHERE edo_status = 'A' group by mft_seq_nbr) edo,");
			query.append(" vessel_call vc, berthing b, GB_EDO A, company_code cc, ");
			//query.append(" (select edo_esn_nbr, max(WA_REF_NBR) WA_REF_NBR from WA_APPLN_DETAILS where rec_status in ('A','C') and  edo_esn_nbr is not null  group by edo_esn_nbr) wh");
			query.append(" (select edo_esn_nbr, max(wa_running_nbr) as wa_ref_nbr, max(end_date) as exp_date from wa_appln_details  where rec_status in ('A','R','C') and  edo_esn_nbr is not null");
			//query.append("  having  max(wa_running_nbr) = (select max(wa_running_nbr) from wa_appln_details)");
			query.append(" group by edo_esn_nbr) wh")   ;
			query.append(" where mf.bl_status='A'");
			query.append(" and mf.mft_seq_nbr =edo.mft_seq_nbr");
			query.append(" and mf.edo_nbr_pkgs > edo.total_delivered_pkgs ");
			query.append(" and mf.var_nbr = vc.vv_cd ");
			query.append(" and A.MFT_SEQ_NBR(+) = mf.MFT_SEQ_NBR ");
			query.append(" and vc.vv_cd = b.vv_cd ");
			query.append(" AND ((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
			//query.append(" and vc.terminal='GB'");
			query.append(" AND A.EDO_STATUS = 'A' ");
			//query.append(" AND A.nbr_pkgs > A.dn_nbr_pkgs ");
			query.append(" and vc.CREATE_CUST_CD = cc.co_cd ");
			query.append(" and a.edo_asn_nbr = wh.edo_esn_nbr(+) ");
			query.append(" and b.shift_ind=1 ");
			query.append(" and mf.special_action_pkgs >= (mf.nbr_pkgs - edo.total_delivered_pkgs) and mf.special_action_pkgs >0");
			query.append(" and b.atu_dttm <= (sysdate - :vlsDeparDays) ");
			
			if (companyCd != null && !"JP".equalsIgnoreCase(companyCd)) {
				query.append(" and  (A.ADP_CUST_CD = :companyCd OR VC.VSL_OPR_CD =:companyCd OR " +
									" a.edo_asn_nbr IN (SELECT distinct (SUB.ESN_ASN_NBR) FROM SUB_ADP SUB WHERE SUB.TRUCKER_CO_CD = :companyCd AND STATUS_CD = 'A' AND EDO_ESN_IND = '1'))" );
				paramMap.put("companyCd", companyCd);
			}
			if (dateFrom != null && !"".equals(dateFrom)) {
				query.append(" and  b.atb_dttm >= to_date(:dateFrom,'DDMMYYYY')");
			}
			if (dateTo != null && !"".equals(dateTo)) {
				query.append(" and  b.atb_dttm <= to_date(:dateTo,'DDMMYYYY')");
			}
			/*Amended by Punitha on 16/02/2009 to add the BL Nbr
			if (esnEdoNbr != null && !"".equals(esnEdoNbr)){
				query = query.append(" and  edo.edo_asn_nbr = ? ");
			}*/

			if (blNbr != null && !"".equals(blNbr)){
				//to change exact matching to wildcard searching. For CR-CIM-20121224-003. Dongsheng on 2/2/2013
				//query = query.append(" and  mf.bl_nbr = ? ");
				query.append(" and  upper(mf.bl_nbr) like :blNbr ");
			}
			if (vslName != null && !"".equals(vslName)) {
				query.append(" and  upper(vc.vsl_nm) like :vslName ");
			}

			if (terminal != null && !"".equals(terminal)) {
				query.append(" and  upper(vc.terminal) like :terminal ");
			}
			
			/*
			if (outVoyNbr != null && !"".equals(outVoyNbr)) {
				query.append(" and vc.out_voy_nbr=?  ");
			}
			*/
			if (voyNbr != null && !"".equals(voyNbr)) {
				query.append(" and vc.in_voy_nbr= :voyNbr  ");
			}

			if (varCode != null && !"".equals(varCode)) {
				query.append(" and vc.vv_cd = :varCode  ");
			}

			if (vslScheme != null && !"".equals(vslScheme)) {
				query.append(" and vc.SCHEME = :vslScheme  ");
			}
			if (agent != null && !"".equals(agent)) {
				query.append(" and VC.CREATE_CUST_CD = :agent  ");
			}
			if (edoAsnNbr != null && !"".equals(edoAsnNbr)) {
				query.append(" and  a.edo_asn_nbr = :edoAsnNbr ");
			}
			if (adp != null && !"".equals(adp)) {
				query.append(" and A.adp_cust_cd = :adp  ");
			}
			//Added to query to retrieve cases without EDO being created. By Dongsheng on 7/2/2013
			query.append(" union ");
			query.append(" select distinct mf.var_nbr,vc.vsl_nm, vc.in_voy_nbr, vc.out_voy_nbr, to_char(b.atu_dttm,'DDMMYYYY HH24MI') as atu_dttm, ");
			query.append(" to_char(b.atb_dttm,'DDMMYYYY HH24MI') as atb_dttm,mf.bl_nbr, null as edo_asn_nbr, ");
			query.append(" (mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0)) as total_pkgs, ");
			query.append(" (mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0) - mf.SPECIAL_ACTION_PKGS -  nvl(mf.NBR_PKGS_IN_PORT,0)) as balance_pkgs, ");
			query.append(" 0 as total_pkgs_by_SST, ");
			query.append(" round(((mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0) - mf.SPECIAL_ACTION_PKGS - nvl(mf.NBR_PKGS_IN_PORT,0))/(mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0)))* mf. GROSS_wt,2) as balance_weight, ");
			query.append(" round(((mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0) - mf.SPECIAL_ACTION_PKGS - nvl(mf.NBR_PKGS_IN_PORT,0))/(mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS ,0)))* mf. GROSS_VOL,2) as balance_volume, ");
			query.append(" mf.SPECIAL_ACTION_PKGS, mf.SPECIAL_ACTION_REMARKS, nvl(mf.NBR_PKGS_IN_PORT,0) as NBR_PKGS_IN_PORT,  VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND, MF.CRG_STATUS,  To_Char(B.GB_COD_DTTM,'DDMMYYYY HH24MI') as cod, ");
			query.append(" null as ADP, CC.CO_NM as AGENT, B.GB_COD_DTTM, null as ACCT_NBR ");
			query.append(" ,mf.CRG_DES, null as WA_REF_NBR, null as WH_IND, mf.mixed_scheme_acct_nbr,VC.VSL_OPR_CD, null as ADP_CUST_CD, null as ADP_IC_TDBCR_nbr ");
			query.append(" from manifest_details mf, ");
			query.append(" vessel_call vc, berthing b, company_code cc ");
			query.append(" where mf.bl_status='A' ");
			query.append(" and mf.var_nbr = vc.vv_cd ");
			query.append(" and vc.vv_cd = b.vv_cd ");
			query.append(" AND ((VC.TERMINAL IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR VC.TERMINAL NOT IN 'CT') ");
			//query.append(" and vc.terminal='GB'");
			query.append(" and vc.CREATE_CUST_CD = cc.co_cd ");
			query.append(" and b.shift_ind=1 ");
			query.append(" and (mf.nbr_pkgs- nvl(mf.CUT_OFF_NBR_PKGS ,0) - mf.SPECIAL_ACTION_PKGS -  nvl(mf.NBR_PKGS_IN_PORT,0)) =0 ");
			query.append(" and mf.special_action_pkgs >0 ");
			query.append(" and b.atu_dttm <= (sysdate - :vlsDeparDays) ");
			query.append(" and MF.MFT_SEQ_NBR in ( ");
			query.append("  select MFT_SEQ_NBR from manifest_details where bl_status='A'");
			query.append(" minus ");
			query.append(" select MFT_SEQ_NBR from gb_edo where edo_status='A' ) ");
			
			if (companyCd != null && !"JP".equalsIgnoreCase(companyCd)) {
				query.append(" and  (VC.VSL_OPR_CD =:companyCd)" );
			}

			if (dateFrom != null && !"".equals(dateFrom)) {
				query.append(" and  b.atb_dttm >= to_date(:dateFrom,'DDMMYYYY')");
			}
			if (dateTo != null && !"".equals(dateTo)) {
				query.append(" and  b.atb_dttm <= to_date(:dateTo,'DDMMYYYY')");
			}

			if (blNbr != null && !"".equals(blNbr)){
				query.append(" and  upper(mf.bl_nbr) like :blNbr ");
			}
			if (vslName != null && !"".equals(vslName)) {
				query.append(" and  upper(vc.vsl_nm) like :vslName ");
			}

			if (terminal != null && !"".equals(terminal)) {
				query.append(" and  upper(vc.terminal) like :terminal ");
			}
			
			if (voyNbr != null && !"".equals(voyNbr)) {
				query.append(" and vc.in_voy_nbr=:voyNbr  ");
			}

			if (varCode != null && !"".equals(varCode)) {
				query.append(" and vc.vv_cd =:varCode  ");
			}
			if (vslScheme != null && !"".equals(vslScheme)) {
				query.append(" and vc.SCHEME =:vslScheme  ");
			}
			if (adp != null && !"".equals(adp)) {
				query.append(" and  0 > 1 ");
			}
			if (edoAsnNbr != null && !"".equals(edoAsnNbr)) {
				query.append(" and  0 > 1 ");
			}
			if (agent != null && !"".equals(agent)) {
				query.append(" and VC.CREATE_CUST_CD =:agent  ");
			}
			// end of addtion. By Dongsheng on 7/2/2013.

			//Amended by Punitha on 16/02/2009
			//query.append(" order by vc.vsl_nm, vc.out_voy_nbr, edo.edo_asn_nbr");
			//Change to use in voy nbr. By Dongsheng on 14/1/2013. For CR-CIM-20121224-003.
			//query.append(" order by vc.vsl_nm, vc.out_voy_nbr, mf.bl_nbr");
			query.append(" order by GB_COD_DTTM, vsl_nm, in_voy_nbr, bl_nbr");

			log.info("getCompletedDeliveryCargoList Query: "+query.toString());
			
			// get the total count
			String totalCountSql = "SELECT COUNT(*) FROM (" + query.toString() +" ) ";
			paramMap.put("vlsDeparDays", vlsDeparDays);
			
			if (companyCd != null && !"JP".equalsIgnoreCase(companyCd)) {
				paramMap.put("companyCd", companyCd);
			}
			if (dateFrom != null && !"".equals(dateFrom)) {
				paramMap.put("dateFrom", dateFrom);
			}
			if (dateTo != null && !"".equals(dateTo)) {
				paramMap.put("dateTo", dateTo);
			}

			if (blNbr != null && !"".equals(blNbr)) {
				paramMap.put("blNbr", "%" + blNbr.toUpperCase() + "%");
			}
			if (vslName != null && !"".equals(vslName)) {
				paramMap.put("vslName", "%" + vslName.toUpperCase() + "%");
			}

			if (terminal != null && !"".equals(terminal)) {
				paramMap.put("terminal", "%" + terminal.toUpperCase() + "%");
			}

			if (voyNbr != null && !"".equals(voyNbr)) {
				paramMap.put("voyNbr", voyNbr);
			}
			if (varCode != null && !"".equals(varCode)) {
				paramMap.put("varCode", varCode);
			}
			if (vslScheme != null && !"".equals(vslScheme)) {
				paramMap.put("vslScheme", vslScheme);
			}
			if (agent != null && !"".equals(agent)) {
				paramMap.put("agent", agent);
			}
			if (adp != null && !"".equals(adp)) {
				paramMap.put("adp", adp);
			}
			if (edoAsnNbr != null && !"".equals(edoAsnNbr)) {
				paramMap.put("edoAsnNbr", edoAsnNbr);
			}
		 	log.info("SQL" + totalCountSql);
		 	log.info(" *** getCompletedDeliveryCargoList params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(totalCountSql, paramMap);
			//to get the count
			while (rs.next())
			{
				tableData.setTotal(Integer.parseInt(CommonUtility.deNull(rs.getString(1))));
			}
			
			// Added For seqNo pagination also handling here
			String finalSql = query.toString();
			if(!criteria.getPredicates().containsKey("excel")||criteria.getPredicates().get("excel").equalsIgnoreCase("false")) {
				if (criteria.isPaginated()) {
					finalSql = CommonUtil.getPaginatedSql(query.toString(), criteria.getStart(), criteria.getLimit());

				}
			}
			
			log.info("getCompletedDeliveryCargoList Query: **********  "+finalSql.toString());			
			paramMap.put("vlsDeparDays", vlsDeparDays);
			if (companyCd != null && !"JP".equalsIgnoreCase(companyCd)) {
				paramMap.put("companyCd", companyCd);
			}
			if (dateFrom != null && !"".equals(dateFrom)) {
				paramMap.put("dateFrom", dateFrom);
			}
			if (dateTo != null && !"".equals(dateTo)) {
				paramMap.put("dateTo", dateTo);
			}

			if (blNbr != null && !"".equals(blNbr)) {
				paramMap.put("blNbr", "%" + blNbr.toUpperCase() + "%");
			}

			if (vslName != null && !"".equals(vslName)) {
				paramMap.put("vslName", "%" + vslName.toUpperCase() + "%");
			}

			if (terminal != null && !"".equals(terminal)) {
				paramMap.put("terminal", "%" + terminal.toUpperCase() + "%");
			}

			if (voyNbr != null && !"".equals(voyNbr)) {
				paramMap.put("voyNbr", voyNbr);
			}

			if (varCode != null && !"".equals(varCode)) {
				paramMap.put("varCode", varCode);
			}
			if (vslScheme != null && !"".equals(vslScheme)) {
				paramMap.put("vslScheme", vslScheme);
			}
			if (agent != null && !"".equals(agent)) {
				paramMap.put("agent", agent);
			}
			if (adp != null && !"".equals(adp)) {
				paramMap.put("adp", adp);
			}
			if (edoAsnNbr != null && !"".equals(edoAsnNbr)) {
				paramMap.put("edoAsnNum", edoAsnNbr);
			}
		 
			log.info("SQL" + finalSql.toString());
			log.info(" ***paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(finalSql.toString(), paramMap);
			TopsModel topsModel= new TopsModel();
			String acctNbr = getAccountNbr(companyCd);
			Map<String,String> ret = getAllSubAdp();
			while (rs.next())
			{
				BalanceCargoVo vo = new BalanceCargoVo();
				vo = populateVO(rs, "CompletedDelivery", companyCd, acctNbr, ret);
				if (vo != null && vo.getBlNbr() != null) {
					//balanceCargoVoList.add(vo);
					topsModel.put(vo);
				}
			}
			tableData.setListData(topsModel);
			log.info(" *** getCompletedDeliveryCargoList result *****" + tableData);

		} catch (Exception e) {
			log.info("Exception getCompletedDeliveryCargoList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCompletedDeliveryCargoList Dao Impl  END");
		}
		return tableData;
	}

	private String getAccountNbr(String companyCd) throws BusinessException{
		String ret = "";
		String query = "";
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		query= " select acct_nbr from cust_acct where cust_cd = :companyCd AND ACCT_STATUS_CD='A' and business_type='G' ";
		log.info("getVesselList() Query: "+query.toString());
		try{
			log.info("START: getAccountNbr Dao Start companyCd:" + companyCd);
			paramMap.put("companyCd", companyCd);
			log.info(" ***SQL *****" + query.toString());
			log.info(" ***paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(query, paramMap);
			while(rs.next()){
				ret = CommonUtility.deNull(rs.getString("acct_nbr"));
			}
			log.info("END: *** getAccountNbr Result *****" + CommonUtility.deNull(ret));

		} catch(Exception e){
			log.info("Exception getAccountNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAccountNbr Dao Impl END ");
		}
		return ret;
	}
	
	private Map<String, String> getAllSubAdp() throws BusinessException {
		SqlRowSet rs = null;
		String sql = "select (ESN_ASN_NBR || ',' || TRUCKER_CO_CD) as key from sub_adp where edo_esn_ind=1 AND STATUS_CD = 'A' ";
		Map<String, String> ret = new HashMap<String, String>();
		Map<String, String> paramMap = new HashMap<String, String>();
		try{
			log.info("START: getAllSubAdp Dao Start ");
			log.info(" ***SQL *****" + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()){
				ret.put(CommonUtility.deNull(rs.getString("key")), CommonUtility.deNull(rs.getString("key")));
			}
			
			log.info("END: *** getAllSubAdp Result *****" + ret);
		} catch(Exception e){
			log.info("Exception getAllSubAdp : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAllSubAdp Dao Impl END ");
		}
		return ret;
	}

	private BalanceCargoVo populateVO(SqlRowSet rs, String recordType, String companyCd, String acctNbr, Map<String,String> ret) throws BusinessException {
		BalanceCargoVo vo = new BalanceCargoVo();
		Map<String,String> paramMap = new HashMap<String,String>();
		try{
			log.info("START: populateVO Dao Start recordType: " +recordType + ",companyCd:"+companyCd+",acctNbr:"+acctNbr+",ret:"+ret);
			String adpCd = CommonUtility.deNull(rs.getString("ADP_CUST_CD"));
			String vslOprCd = CommonUtility.deNull(rs.getString("VSL_OPR_CD"));
			String mixedSchemeAcctNbr = CommonUtility.deNull(rs.getString("mixed_scheme_acct_nbr"));
			String edoAsnNbr = CommonUtility.deNull(rs.getString("edo_asn_nbr"));

			boolean isAdd = true;
			if (companyCd != null && !"JP".equalsIgnoreCase(companyCd)) {
				if (!(adpCd.equalsIgnoreCase(companyCd) || vslOprCd.equalsIgnoreCase(companyCd)
						|| (!"".equalsIgnoreCase(mixedSchemeAcctNbr) && mixedSchemeAcctNbr.equalsIgnoreCase(acctNbr))
						|| ret.containsKey(edoAsnNbr + "," + companyCd))) {
					isAdd = false;
				}
			}
			String role = "";
			if (adpCd.equalsIgnoreCase(companyCd) || ret.containsKey(edoAsnNbr + "," + companyCd)) {
				role = "ADP";
			}
			if (vslOprCd.equalsIgnoreCase(companyCd)) {
				role = ("".equalsIgnoreCase(role)) ? "Agent" : role + ", Agent";
			}
			if (!"".equalsIgnoreCase(mixedSchemeAcctNbr) && mixedSchemeAcctNbr.equalsIgnoreCase(acctNbr)) {
				role = ("".equalsIgnoreCase(role)) ? "AB OPR" : role + ", AB OPR";
			}
			vo.setRole(role);
			if ("Outstanding".equalsIgnoreCase(recordType) && isAdd) {
				vo.setVesselName(CommonUtility.deNull(rs.getString("vsl_nm")));

				// Added by Dongsheng on 14/1/2013 for CR-CIM-20121224-003
				vo.setInVoyNbr(CommonUtility.deNull(rs.getString("in_voy_nbr")));

				vo.setOutVoyNbr(CommonUtility.deNull(rs.getString("out_voy_nbr")));
				vo.setAtuDttm(CommonUtility.deNull(rs.getString("atu_dttm")));
				// Added by Dongsheng for CR-CIM-20121224-003 on 5/2/2013
				vo.setAtbDttm(CommonUtility.deNull(rs.getString("atb_dttm")));
				vo.setAcctNo(CommonUtility.deNull(rs.getString("ACCT_NBR")));

				vo.setBlNbr(CommonUtility.deNull(rs.getString("bl_nbr")));
				// put edo_asn_nbr back by Dongsheng for CR-CIM-20121224-003 on 14/1/2013
				edoAsnNbr = CommonUtility.deNull(rs.getString("edo_asn_nbr"));
				vo.setEdoAsnNbr(edoAsnNbr);

				vo.setVesselVvCd(CommonUtility.deNull(rs.getString("var_nbr")));

				/*
				 * Amended by Punitha on 16/02/2009
				 * vo.setEdoAsnNbr(CommonUtility.deNull(rs.getString("edo_asn_nbr")));
				 */
				vo.setTotalPkgs(CommonUtility.deNull(rs.getString("total_pkgs")));
				vo.setBalancePkgs(CommonUtility.deNull(rs.getString("balance_pkgs")));
				vo.setBalanceWeight(String.valueOf(rs.getFloat("balance_weight")));
				vo.setBalanceVolume(String.valueOf(rs.getFloat("balance_volume")));
				if (vo.getEdoAsnNbr() != null && (!(vo.getEdoAsnNbr().trim().equalsIgnoreCase("")))) {
					vo.setTotalDeliveredPkgs(CommonUtility.deNull(rs.getString("total_pkgs_by_SST")));
				} else {
					vo.setTotalDeliveredPkgs("");
				}
				// Added by Punitha on 16/02/2009 to retrieve the shortland packages
				/*
				 * Commented by Dongsheng on 14/1/2013 for CR-CIM-20121224-003
				 * vo.setShortlandPkgs(CommonUtility.deNull(rs.getString("NBR_PKGS_IN_PORT")));
				 */
				vo.setShortlandPkgs(CommonUtility.deNull(rs.getString("shortlanded")));

				vo.setSpecialActionRemark(CommonUtility.deNull(rs.getString("SPECIAL_ACTION_REMARKS")));
				vo.setSpecialActionPkgs(CommonUtility.deNull(rs.getString("SPECIAL_ACTION_PKGS")));

				// Added by Dongsheng on 14/1/2013 for CR-CIM-20121224-003
				vo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				vo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				vo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				vo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				vo.setCrgStatus(CommonUtility.deNull(rs.getString("CRG_STATUS")));
				vo.setCod(CommonUtility.deNull(rs.getString("COD")));
				vo.setDwellDays(CommonUtility.deNull(rs.getString("dwell_days")));
				vo.setAdpName(CommonUtility.deNull(rs.getString("ADP")));
				vo.setAgentName(CommonUtility.deNull(rs.getString("AGENT")));
				vo.setWarehouseExpDt(CommonUtility.deNull(rs.getString("WH_EXP_DATE")));

				vo.setCrgDes(CommonUtility.deNull(rs.getString("CRG_DES")));
				String warunningnbr = CommonUtility.deNull(rs.getString("WA_REF_NBR"));
				String sql = "Select WA_REF_NBR , REC_STATUS from wa_appln_details where edo_esn_nbr =:edoAsnNbr and wa_running_nbr =:warunningnbr";
				paramMap.put("edoAsnNbr", edoAsnNbr);
				paramMap.put("warunningnbr", warunningnbr);
				
				log.info(" *** populateVO SQL *****" + sql);
				log.info(" *** populateVO params *****" + paramMap.toString());
				
				SqlRowSet rs1 = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs1 != null && rs1.next()) {
					String recStatus = CommonUtility.deNull(rs1.getString("REC_STATUS"));
					if (recStatus.equalsIgnoreCase("R")) {
						vo.setWarehouseRefNbr(CommonUtility.deNull(rs1.getString("WA_REF_NBR")) + "\n" + "(Rej)");
					} else {
						vo.setWarehouseRefNbr(CommonUtility.deNull(rs1.getString("WA_REF_NBR")));
					}
				} else {
					vo.setWarehouseRefNbr("");
				}
				vo.setWarehouseInd(CommonUtility.deNull(rs.getString("WH_IND")));
			} else if ("CompletedDelivery".equalsIgnoreCase(recordType) && isAdd) {
				vo.setVesselName(CommonUtility.deNull(rs.getString("vsl_nm")));
				// Added by Dongsheng on 14/1/2013 for CR-CIM-20121224-003
				vo.setInVoyNbr(CommonUtility.deNull(rs.getString("in_voy_nbr")));
				vo.setOutVoyNbr(CommonUtility.deNull(rs.getString("out_voy_nbr")));
				vo.setAtuDttm(CommonUtility.deNull(rs.getString("atu_dttm")));
				// Added by Dongsheng for CR-CIM-20121224-003 on 5/2/2013
				vo.setAtbDttm(CommonUtility.deNull(rs.getString("atb_dttm")));
				vo.setAcctNo(CommonUtility.deNull(rs.getString("ACCT_NBR")));

				vo.setBlNbr(CommonUtility.deNull(rs.getString("bl_nbr")));
				// Amended by Punitha on 10/06/2009
				// vo.setEdoAsnNbr(CommonUtility.deNull(rs.getString("edo_asn_nbr")));
				// put edo_asn_nbr back by Dongsheng for CR-CIM-20121224-003 on 14/1/2013
				// vo.setEdoAsnNbr(CommonUtility.deNull( rs.getString("edo_asn_nbr")));
				edoAsnNbr = CommonUtility.deNull(rs.getString("edo_asn_nbr"));
				vo.setEdoAsnNbr(edoAsnNbr);
				vo.setTotalPkgs(CommonUtility.deNull(rs.getString("total_pkgs")));
				vo.setBalancePkgs(CommonUtility.deNull(rs.getString("balance_pkgs")));
				//	Added by Punitha on 16/02/2009 to retrieve the shortland packages
				vo.setShortlandPkgs(CommonUtility.deNull(rs.getString("NBR_PKGS_IN_PORT")));
				vo.setTotalDeliveredPkgs(CommonUtility.deNull(rs.getString("total_pkgs_by_SST")));

				vo.setSpecialActionRemark(CommonUtility.deNull(rs.getString("SPECIAL_ACTION_REMARKS")));
				vo.setSpecialActionPkgs(CommonUtility.deNull(rs.getString("SPECIAL_ACTION_PKGS")));

				// Added by Dongsheng on 14/1/2013 for CR-CIM-20121224-003
				vo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				vo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				vo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				vo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				vo.setCrgStatus(CommonUtility.deNull(rs.getString("CRG_STATUS")));
				vo.setCod(CommonUtility.deNull(rs.getString("COD")));
				vo.setAdpName(CommonUtility.deNull(rs.getString("ADP")));
				vo.setAgentName(CommonUtility.deNull(rs.getString("AGENT")));
				// START - Missing VVCD to update balance status - NS JUNE 2024
				vo.setVesselVvCd(CommonUtility.deNull(rs.getString("var_nbr")));
				// END - Missing VVCD to update balance status - NS JUNE 2024
				vo.setCrgDes(CommonUtility.deNull(rs.getString("CRG_DES")));
				String warunningnbr = CommonUtility.deNull(rs.getString("WA_REF_NBR"));
				String sql = "Select WA_REF_NBR from wa_appln_details where edo_esn_nbr =:edoAsnNbr and wa_running_nbr =:warunningnbr ";
				paramMap.put("edoAsnNbr", edoAsnNbr);
				paramMap.put("warunningnbr", warunningnbr);
				
				log.info(" ***SQL *****" + sql.toString());
				log.info(" ***paramMap *****" + paramMap.toString());
				
				SqlRowSet rs1 = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs1 != null && rs1.next()) {
					vo.setWarehouseRefNbr(CommonUtility.deNull(rs1.getString("WA_REF_NBR")));
				} else {
					vo.setWarehouseRefNbr("");
				}
				log.info("vo.getWarehouseRefNbr ***" + vo.getWarehouseRefNbr());
				vo.setWarehouseInd(CommonUtility.deNull(rs.getString("WH_IND")));
			}
			
			log.info("END: *** populateVO Result *****" + vo.toString());

		} catch(Exception e){
			log.info("Exception populateVO : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: populateVO Dao Impl END ");
		}
		return vo;
	}
	
	//SSL No SL-CIM-20170927-01
	//20171207 koktsing override from the existing updateCargoBalanceStatus
	//add additional parameter varCode as unique key has to be combination of blNBR and varCode
	@Override
	public int updateCargoBalanceStatus(String blNbr, String vesselVvCd, long balancePackages, String actionRemarks,String userId,String updateDttm) throws BusinessException {
		int result = 0;
		StringBuffer query = new StringBuffer();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		try {
			log.info("START: updateCargoBalanceStatus Dao Start blNbr: " +blNbr + ",vesselVvCd:"+vesselVvCd+",balancePackages:"+balancePackages+",actionRemarks:"+actionRemarks
					+",userId:"+userId+",updateDttm:"+updateDttm);
			query.setLength(0);
			query.append(
					"update manifest_details set SPECIAL_ACTION_PKGS =:balancePackages, SPECIAL_ACTION_REMARKS = :actionRemarks, SPECIAL_ACTION_USER_ID = :userId, ");
			query.append(" SPECIAL_ACTION_DTTM = to_date(:updateDttm, 'DDMMYYYY HH24MI'),last_modify_dttm = sysdate ");
			query.append(" where BL_NBR = :blNbr and VAR_NBR = :vesselVvCd");
			paramMap.put("balancePackages", balancePackages);
			paramMap.put("actionRemarks", actionRemarks);
			paramMap.put("userId", userId);
			paramMap.put("updateDttm", updateDttm);
			paramMap.put("blNbr", blNbr);
			paramMap.put("vesselVvCd", vesselVvCd);
			
			log.info(" ***SQL *****" + query.toString());
			log.info(" ***paramMap *****" + paramMap.toString());
			result = namedParameterJdbcTemplate.update(query.toString(), paramMap);
			
			log.info("END: *** updateCargoBalanceStatus Result *****" + result);

		} catch(Exception e){
			log.info("Exception updateCargoBalanceStatus : ", e);
			throw new BusinessException("M4201");
		} finally{
			log.info("END: updateCargoBalanceStatus Dao Impl END ");
		}

		return result;
	}

	/*Amended by Punitha on 17/02/2009*/
	@Override
	public int updateCargoBalanceStatus(long esnAsnNbr, long balancePackages, String actionRemarks,String userId,String updateDttm) throws BusinessException {
		int result = 0;
		StringBuffer query = new StringBuffer();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		try {
			log.info("START: updateCargoBalanceStatus Dao Start esnAsnNbr: " +esnAsnNbr + ",balancePackages:"+balancePackages+",actionRemarks:"+actionRemarks
					+",userId:"+userId+",updateDttm:"+updateDttm);

			query.setLength(0);
			/* Amended by Punitha on 17/02/2009.Need to update the manifest_details table */
			query.append(" update GB_EDO set SPECIAL_ACTION_PKGS = :balancePackages, SPECIAL_ACTION_REMARKS = :actionRemarks, SPECIAL_ACTION_USER_ID = :userId, ");
			query.append(" SPECIAL_ACTION_DTTM = to_date(:updateDttm, 'DDMMYYYY HH24MI') , last_modify_dttm = sysdate");
			query.append(" where EDO_ASN_NBR = :esnAsnNbr ");
			
			paramMap.put("balancePackages", balancePackages);
			paramMap.put("actionRemarks", actionRemarks);
			paramMap.put("userId", userId);
			paramMap.put("updateDttm", updateDttm);
			/* Amended by Punitha on 17/02/2009 */
			paramMap.put("esnAsnNbr", esnAsnNbr);
			// stmt.setString(5, blNbr);
			
			log.info(" *** updateCargoBalanceStatus SQL *****" + query.toString());
			log.info(" *** updateCargoBalanceStatus params *****" + paramMap.toString());

			result = namedParameterJdbcTemplate.update(query.toString(), paramMap);
			
			log.info("END: *** updateCargoBalanceStatus Result *****" + result);
		} catch(Exception e){
			log.info("Exception updateCargoBalanceStatus : ", e);
			throw new BusinessException("M4201");
		} finally{
			log.info("END: updateCargoBalanceStatus Dao Impl END ");
		}
		return result;
	}


}
