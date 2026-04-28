package sg.com.jp.generalcargo.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.ContainerCommonFunctionRepo;
import sg.com.jp.generalcargo.dao.ProcessGBLogRepository;
import sg.com.jp.generalcargo.dao.StuffingRepository;
import sg.com.jp.generalcargo.domain.CcStuffDetailValueObject;
import sg.com.jp.generalcargo.domain.CcStuffingVo;
import sg.com.jp.generalcargo.domain.ContainerDetailObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.SchemeAccountObject;
import sg.com.jp.generalcargo.domain.StuffingDetailObject;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselTxnEventLogValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("stuffingRepo")
public class StuffingJdbcRepository  implements StuffingRepository {

	private static final Log log = LogFactory.getLog(StuffingJdbcRepository.class);
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private ContainerCommonFunctionRepo cntrCommonFuncRepo;
	@Autowired
	private ProcessGBLogRepository processGBLogRepo;
	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->getContainerDetails()
	/**
	 * Returns the Container details as list of ContainerDetailObject for the var
	 * number and container number passed
	 * 
	 * @param vvcode      Var Number of Vessel.
	 * @param containerno Container Number and ESN ASN Number for the container
	 *                    together with "::" as delimeter
	 * @return ArrayList ArrayList containing ContainerDetailObject.
	 * @throws BusinessException
	 * 
	 */
	
	@Override
	public TableResult getContainerDetails(String vvcode, String containerno, Criteria criteria) throws BusinessException {
		
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringTokenizer cntrno = new StringTokenizer(containerno, "::");
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		String sql = "";
		try {
			log.info("START: getContainerDetails  DAO  Start vvcode:" + CommonUtility.deNull(vvcode) + "containerno:" + CommonUtility.deNull(containerno)
			+ "criteria:"+ criteria.toString());

			// qry="select '"+ cntrno.nextToken() +"' cntrno,cntr.cntr_seq_nbr cntrseqnbr"+
//			     ",cntr.size_ft cntrsize,nvl(to_char(stf.stuffing_dttm,'dd-mm-yyyy hh24:mi'),'') stuffdate"+
//			     ",decode(stf.stuff_closed,'N','Not Closed','Y','Closed',null,'Not Closed','Not Closed') stuffsts"+
//			     ",nvl(to_char(stuff_seq_nbr),'--') seqno from cntr,cc_stuffing stf where "+
//			     "stf.active_status(+)='A' and stf.var_nbr(+)=cntr.load_vv_cd and cntr.cntr_seq_nbr='"+
//			     cntrno.nextToken() +"' and cntr.load_vv_cd='"+ vvcode +"'";

			sb.append(" select '" + cntrno.nextToken().toString()
					+ "' cntrno,cntr.cntr_seq_nbr cntrseqnbr,cntr.size_ft cntrsize ");
			sb.append(" ,nvl(to_char(stf.stuffing_dttm,'dd-mm-yyyy hh24:mi'),'') stuffdate ");
			sb.append(" ,bill_party_acct_nbr acctno,decode(stf.stuff_closed,'N','Not Closed', ");
			sb.append(" 'Y','Closed',null,'Not Closed','Not Closed') stuffsts ");
			sb.append(" ,nvl(to_char(stuff_seq_nbr),'--') seqno,decode(waive_charge,'Y', ");
			sb.append(" 'Waived','Not Waived') charge  from cntr,cc_stuffing stf where ");
			sb.append(" stf.active_status(+)='A' and stf.cntr_seq_nbr(+)=cntr.cntr_seq_nbr and ");
			sb.append(" stf.var_nbr(+)=:vvcode and cntr.cntr_seq_nbr='" + cntrno.nextToken().toString() + "' ");
			sb.append(" and (cntr.load_vv_cd=:vvcode or cntr.load_vv_cd is null) ");
			log.info(" ***getContainerDetails SQL *****" + sb.toString());
			sql = sb.toString();
			paramMap.put("vvcode", vvcode);
			// paramMap.put("cntrno", cntrno.nextToken());
			
			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
			}

			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());

			}
			log.info(" ***getContainerDetails paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				ContainerDetailObject cdo = new ContainerDetailObject();
				cdo.setContainerNo(CommonUtility.deNull(rs.getString("cntrno")));
				cdo.setContainerSeqNo(CommonUtility.deNull(rs.getString("cntrseqnbr")));
				cdo.setContainerSize(CommonUtility.deNull(rs.getString("cntrsize")));
				cdo.setStuffingDttm(CommonUtility.deNull(rs.getString("stuffdate")));
				cdo.setBillAccountNumber(CommonUtility.deNull(rs.getString("acctno")));
				cdo.setStuffingClosed(CommonUtility.deNull(rs.getString("stuffsts")));
				cdo.setVarNo(vvcode);
				cdo.setSeqNo(CommonUtility.deNull(rs.getString("seqno")));
				cdo.setWaiveCharge(CommonUtility.deNull(rs.getString("charge")));
				topsModel.put(cdo);
			}
			
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
			log.info("END: *** getContainerDetails Result *****" + topsModel.getSize());
		} catch (NullPointerException e) {
			log.info("Exception getContainerDetails : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getContainerDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getContainerDetails  DAO  END");
		}
		return tableResult;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->getVesselVoyage()
	/**
	 * Returns the list of vessel voyage details encapsulated in
	 * VesselVoyValueObject
	 * 
	 * @param companycode Company Code of the user logged in.
	 * @return ArrayList Return arraylist of VesselVoyValueObject.
	 * @throws BusinessException
	 */
	@Override
	public List<VesselVoyValueObject> getVesselVoyage(String companycode) throws BusinessException {

		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<VesselVoyValueObject> voyList = new ArrayList<VesselVoyValueObject>();

		try {
			log.info("START: getVesselVoyage  DAO  Start companycode:" + CommonUtility.deNull(companycode));
		
//		    if (companycode.equals("JP")) {
//		      qry = "SELECT OUT_VOY_NBR,VSL_NM,VV_CD FROM VESSEL_CALL WHERE VV_STATUS_IND NOT IN ('CL','CX','UB') AND GB_CLOSE_BJ_IND <> 'Y' ORDER BY VSL_NM,IN_VOY_NBR";
//		    }
//		    else {
//		      qry = "SELECT OUT_VOY_NBR,VSL_NM,VV_CD FROM VESSEL_CALL WHERE VV_STATUS_IND NOT IN ('CL','CX','UB') AND GB_CLOSE_BJ_IND <> 'Y' AND nvl(DECLARANT_CUST_CD,CREATE_CUST_CD)='" +
//		            companycode + "' ORDER BY VSL_NM,IN_VOY_NBR";
//		    }

			if (companycode.equals("JP")) {
				sb.append(" SELECT out_VOY_NBR, VSL_NM, VV_CD FROM ");
//		      changed by Irene Tan on 18 Feb 2004 : to allow JP user to view vessel/voyage that has already unberthed 
//		      "VESSEL_CALL WHERE VV_STATUS_IND IN('PR','AP','AL','BR') AND " +
				sb.append(" VESSEL_CALL WHERE VV_STATUS_IND IN('PR','AP','AL','BR','UB') AND ");
//		      end changed by Irene Tan on 18 Feb 2004
				sb.append(" (GB_CLOSE_SHP_IND < > 'Y'or GB_CLOSE_SHP_IND IS NULL) ORDER BY ");
				sb.append(" VSL_NM, out_VOY_NBR ");

			}
			// ++ 19.10.2009 Changed by vietnd02 for GB CR
			else {
				/*
				 * qry = "SELECT out_VOY_NBR, VSL_NM, VV_CD FROM " +
				 * "VESSEL_CALL WHERE VV_STATUS_IND IN('PR','AP','AL','BR')"
				 * +" AND nvl(DECLARANT_CUST_CD,CREATE_CUST_CD)='" + companycode + "'" +
				 * "(GB_CLOSE_SHP_IND < > 'Y' or GB_CLOSE_SHP_IND IS NULL) ORDER BY VSL_NM, out_VOY_NBR"
				 * ;
				 */
				sb.append(" SELECT DISTINCT out_VOY_NBR,VSL_NM,VC.VV_CD ");
				sb.append(" FROM VESSEL_CALL VC LEFT OUTER JOIN VESSEL_DECLARANT VD ON ");
				sb.append(" (VD.VV_CD = VC.VV_CD AND VD.STATUS = 'A') ");
				sb.append(" WHERE VV_STATUS_IND IN ('PR','AP','AL','BR') AND ");
				sb.append(" (GB_CLOSE_SHP_IND < > 'Y' or GB_CLOSE_SHP_IND IS NULL) ");
				sb.append(" AND (VD.CUST_CD =:companycode OR VC.CREATE_CUST_CD =:companycode ) ");
				sb.append(" ORDER BY VSL_NM,out_VOY_NBR ");
			}
			// -- 19.10.2009 Changed by vietnd02 for GB CR

			log.info(" ***getVesselVoyage SQL *****" + sb.toString());

			if (!companycode.equals("JP")) {
				paramMap.put("companycode", companycode);
			}
			log.info(" *** getVesselVoyage params *****" + paramMap.toString());
			rs =  namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				VesselVoyValueObject vvvObj = new VesselVoyValueObject();
				vvvObj.setVoyNo(CommonUtility.deNull(rs.getString("OUT_VOY_NBR")));
				vvvObj.setVslName(CommonUtility.deNull(rs.getString("VSL_NM")));
				vvvObj.setVarNbr(CommonUtility.deNull(rs.getString("VV_CD")));
				voyList.add(vvvObj);
			}
			log.info("END: *** getVesselVoyage Result *****" + voyList.size());
		} catch (NullPointerException e) {
			log.info("Exception getVesselVoyage : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVesselVoyage : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselVoyage  DAO  END");
		}
		return voyList;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->getContainerNos()
	/**
	 * Returns the list of containers for the var number passed.
	 * 
	 * @param vvcode Var Number of the Vessel.
	 * @return ArrayList Returns an ArrayList of containetnumber and container
	 *         sequence number as cntr_nbr::cntr_seq_nbr.
	 * @throws BusinessException
	 */
	@Override
	public List<String> getContainerNos(String vvcode) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<String> cntrnos = new ArrayList<String>();
//		    qry = "select distinct cntr_nbr||'::'||cntr.esn_asn_nbr as cntr_nbr from esn_cntr cntr,esn esn,bk_details bk where " +
//		          "cntr.cntr_nbr <> 'null' and cntr.esn_asn_nbr=esn.esn_asn_nbr and esn_status ='A' and esn.bk_ref_nbr=bk.bk_ref_nbr " +
//		          "and out_voy_var_nbr=bk.var_nbr and bk.cntr_type in ('E','L') and bk_status='A' and bk.var_nbr = '" +
//		          vvcode + "'";

		sb.append("select distinct cntr_nbr||'::'||cntr_seq_nbr as cntr_nbr ");
		sb.append("from cntr where ((load_vv_cd = :vvcode and ");
		sb.append("purp_cd in ('EX','RS', 'TS', 'RE') ) ");
		sb.append(" or ");
		sb.append("(disc_vv_cd is null ");
		sb.append(" and ");
		sb.append("load_vv_cd is null");
		sb.append(" and ");
		sb.append("purp_cd = 'ST')) ");
		sb.append(" and ");
		sb.append("status in ('L', 'E') ");
		sb.append(" and ");
		sb.append("txn_status = 'A'");
		sb.append(" union ");
		sb.append("select distinct cntr_nbr||'::'||cntr_seq_nbr as cntr_nbr ");
		sb.append(" from ");
		sb.append("cc_stuffing ");
		sb.append(" where ");
		sb.append("active_status='A' ");
		sb.append(" and var_nbr=:vvcode ");

		log.info("sql is .. " + sb);
		try {
			log.info("START: getContainerNos  DAO  Start vvcode:" + CommonUtility.deNull(vvcode));
			log.info(" ***getContainerNos SQL *****" + sb.toString());

			paramMap.put("vvcode", vvcode);

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				cntrnos.add(CommonUtility.deNull(rs.getString("cntr_nbr")));
			}
			log.info("END: *** getContainerNos Result *****" + cntrnos.size());
		} catch (NullPointerException e) {
			log.info("Exception getContainerNos : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getContainerNos : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getContainerNos  DAO  END");
		}
		return cntrnos;

	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->checkEdoNoPkgs()
	/**
	 * Method to check the validity of the EDO number and available number of
	 * pakages against entered number of packages.
	 * 
	 * @param edonos  List of EDO Numbers
	 * @param edopkgs List of number of packages for the corresponding EDO in edonos
	 *                list
	 * @param vvcode  Var Number of the Vessel
	 * @param seqno   Stuffing Master Sequence Number
	 * @param insert  'true' if called for insert and false if called amend
	 * @return ArrayList containing appropriate error messages or ArrayList of size
	 *         0 if all entries are valid.
	 * @throws BusinessException
	 * 
	 */

	@Override
	public List<String> checkEdoNoPkgs(List<String> edonos, List<String> edopkgs, String vvcode, String seqno, boolean insert)
			throws BusinessException {
		List<String> returnmsgs = new ArrayList<String>();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: checkEdoNoPkgs  DAO  Start edonos:" + edonos.size() + "edopkgs:" + edopkgs.size() + "vvcode:" + CommonUtility.deNull(vvcode)
					+ "seqno:" + CommonUtility.deNull(seqno) + "insert:" + insert);

			for (int lp = 0; lp < edonos.size(); lp++) {
				sb.setLength(0);

				sb.append(" select nvl(edo.nbr_pkgs,0)-nvl(edo.dn_nbr_pkgs,0)-nvl(trans_dn_nbr_pkgs,0) ");
				sb.append(" -(select nvl(sum(nvl(dtls.nbr_pkgs,0)),0) from ");
				sb.append(" cc_stuffing stf,cc_stuffing_details dtls where stf.active_status='A' ");
				sb.append(" and stf.stuff_seq_nbr=dtls.stuff_seq_nbr and ");

				if (!insert) {
					sb.append(" stf.stuff_seq_nbr !=:seqno and ");
				}
				
				sb.append(" dtls.rec_status='A' and dtls.edo_esn_ind='EDO' and ");
				sb.append(" dtls.edo_esn_nbr =:edonos ) avlpkgs from ");
				sb.append(" gb_edo edo where edo.edo_status='A' and edo.edo_asn_nbr=:edonos ");

				log.info(" ***checkEdoNoPkgs SQL *****" + sb.toString());
				if (!insert) {
					paramMap.put("seqno", seqno);
				}

				paramMap.put("edonos", edonos.get(lp).toString().trim());
				log.info(" *** checkEdoNoPkgs params *****" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				if (rs.next()) {
					if (rs.getInt("avlpkgs") < Integer.valueOf(edopkgs.get(lp).toString()).intValue()) {
						returnmsgs.add("Entered Packages ' " + edopkgs.get(lp).toString() + " ' for EDO ' "
								+ edonos.get(lp).toString().trim() + " ' is more than Available ' "
								+ rs.getInt("avlpkgs") + " ' Packages.");
					}
				} else {
					returnmsgs.add("EDO Number ' " + edonos.get(lp).toString().trim()
							+ " ' Does Not Exist for Selected Vessel Voyage.");
				}

			}
			
			log.info("END: *** checkEdoNoPkgs Result *****" + returnmsgs.size());
		} catch (NullPointerException e) {
			log.info("Exception checkEdoNoPkgs : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkEdoNoPkgs : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkEdoNoPkgs  DAO  END");
		}
		return returnmsgs;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->chkESNStuffInd()
	// vinayak added on 2 jan 2004
	
	@Override
	public List<ContainerDetailObject> chkESNStuffInd(List<String> esnNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<ContainerDetailObject> al = new ArrayList<ContainerDetailObject>();

		try {
			log.info("START: chkESNStuffInd  DAO  Start esnNbr:" + esnNbr.size());
			
			for (int lp = 0; lp < esnNbr.size(); lp++) {

				String strStuffInd = "";
				sb.setLength(0);
				sb.append(" SELECT e.STUFF_IND FROM esn e,VESSEL_CALL vc WHERE vc.VV_CD= ");
				sb.append(" e.OUT_VOY_VAR_NBR AND e.ESN_STATUS='A' AND e.STUFF_IND='Y' ");
				sb.append(" AND e.ESN_ASN_NBR=:esnNbr ");

			
				log.info(" ***chkESNStuffInd SQL *****" + sb.toString());
				paramMap.put("esnNbr", esnNbr.get(lp).toString().trim());
				log.info(" *** chkESNStuffInd params *****" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				if (rs.next()) {
					strStuffInd = CommonUtility.deNull(rs.getString("STUFF_IND"));
				}
				ContainerDetailObject cdo = new ContainerDetailObject();
				if (strStuffInd.equalsIgnoreCase("Y")) {
					cdo.setDummyEsn("Stuff Indicator");
					cdo.setEsnNbr(esnNbr.get(lp).toString().trim());
					log.info("esn no :" + esnNbr.get(lp).toString().trim());
				} else {
					cdo.setEsnNbr(esnNbr.get(lp).toString().trim());
					log.info("esn no :" + esnNbr.get(lp).toString().trim());
				}
				al.add(cdo);
			}
			log.info("END: *** chkESNStuffInd Result *****" + al.size());
		} catch (NullPointerException e) {
			log.info("Exception chkESNStuffInd : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkESNStuffInd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkESNStuffInd  DAO  END");
		}
		return al;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->checkEsnNoPkgs()
	/**
	 * Method to check the validity of the ESN number and available number of
	 * pakages against entered number of packages.
	 * 
	 * @param esnnos  List of ESN Numbers
	 * @param esnpkgs List of number of packages for the corresponding ESN in esnnos
	 *                list
	 * @param vvcode  Var Number of the Vessel
	 * @param seqno   Stuffing Master Sequence Number
	 * @param insert  'true' if called for insert and false if called amend
	 * @return ArrayList containing appropriate error messages or ArrayList of size
	 *         0 if all entries are valid.
	 * @throws BusinessException
	 * 
	 */
	
	@Override
	public List<String> checkEsnNoPkgs(List<String> esnnos, List<String> esnpkgs, String vvcode, String seqno, boolean insert)
			throws BusinessException {
		List<String> returnmsgs = new ArrayList<String>();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: checkEsnNoPkgs  DAO  Start esnnos:" + esnnos.size() + "esnpkgs:" + esnpkgs.size() + "vvcode:" + CommonUtility.deNull(vvcode)
					+ "seqno:" + CommonUtility.deNull(seqno) + "insert:" + insert);
			
			for (int lp = 0; lp < esnnos.size(); lp++) {
				sb.setLength(0);
				sb.append(" select nvl(dtls.ua_nbr_pkgs,nvl(jpjp.ua_nbr_pkgs,nvl(jppsa.ua_nbr_pkgs,0)))- ");
				sb.append(" (select nvl(sum(nvl(dtls1.nbr_pkgs,0)),0) ");
				sb.append(" from cc_stuffing stf,cc_stuffing_details dtls1 ");
				sb.append(" where ");
				sb.append(" stf.active_status='A' ");
				sb.append(" and stf.stuff_seq_nbr=dtls1.stuff_seq_nbr ");
				sb.append(" and  dtls1.rec_status='A' ");

				if (!insert)
					sb.append(" and stf.stuff_seq_nbr <> :seqno ");

				sb.append(" and dtls1.edo_esn_ind='ESN' ");
				sb.append(" and dtls1.edo_esn_nbr =:esnnos ) avlpkgs from ");
				sb.append(" tesn_psa_jp jppsa,tesn_jp_jp jpjp,esn_details dtls,esn ");
				sb.append(" where jppsa.esn_asn_nbr(+)=esn.esn_asn_nbr ");
				sb.append(" and jpjp.esn_asn_nbr(+)=esn.esn_asn_nbr ");
				sb.append(" and dtls.esn_asn_nbr(+)=esn.esn_asn_nbr ");
				sb.append(" and esn.esn_status='A' ");
				sb.append(" and esn.out_voy_var_nbr=:vvcode ");
				sb.append(" and esn.esn_asn_nbr=:esnnos ");

				/*
				 * qry.append(
				 * "select nvl(nbr_pkgs,0)-(select nvl(sum(nvl(dtls.nbr_pkgs,0)),0) from" +
				 * " cc_stuffing stf,cc_stuffing_details dtls where stf.active_status='A'" +
				 * " and stf.stuff_seq_nbr=dtls.stuff_seq_nbr and "); if (!insert)
				 * qry.append(" stf.stuff_seq_nbr !='" + seqno + "' and ");
				 * qry.append(" dtls.rec_status='A'" +
				 * " and dtls.edo_esn_ind='ESN' and dtls.edo_esn_nbr ='" +
				 * esnnos.get(lp).toString().trim() + "')" +
				 * " avlpkgs from esn,esn_details dtls " +
				 * " where dtls.esn_asn_nbr=esn.esn_asn_nbr and esn.esn_status='A' and" +
				 * " esn.out_voy_var_nbr='" + vvcode + "' and esn.esn_asn_nbr='" +
				 * esnnos.get(lp).toString().trim() + "'");
				 */

			
				log.info(" ***checkEsnNoPkgs SQL *****" + sb.toString());

				if (!insert) {
					paramMap.put("seqno", seqno);
				}
				paramMap.put("vvcode", vvcode);
				paramMap.put("esnnos", esnnos.get(lp).toString().trim());
				log.info(" *** checkEsnNoPkgs params *****" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				if (rs.next()) {
					if (rs.getInt("avlpkgs") < Integer.valueOf(esnpkgs.get(lp).toString()).intValue()) {
						returnmsgs.add("Entered Packages ' " + esnpkgs.get(lp).toString() + " ' for ESN ' "
								+ esnnos.get(lp).toString().trim() + " ' is more than Available ' "
								+ rs.getInt("avlpkgs") + " ' Packages.");
					}
				} else {
					returnmsgs.add("ESN Number ' " + esnnos.get(lp).toString().trim()
							+ " ' Does Not Exist for Selected Vessel Voyage.");
				}
			}
			log.info("END: *** checkEsnNoPkgs Result *****" + returnmsgs.size());
		} catch (NullPointerException e) {
			log.info("Exception checkEsnNoPkgs : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkEsnNoPkgs : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkEsnNoPkgs  DAO  END");
		}
		return returnmsgs;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->isClosed()
	/**
	 * Method to check the related stuffing document os closed or not.
	 * 
	 * @param vvcode    Var Number of the Vessel.
	 * @param contno    Container Number to be Stuffed.
	 * @param cntrseqno Container Sequence Number.
	 * @param seqno     Master Stuffing Sequence Number if Exist or "--"
	 * @return 'true' if closed else 'false'.
	 * @throws BusinessException
	 * 
	 */
	@Override
	public boolean isClosed(String vvcode, String contno, String cntrseqno, String seqno) throws BusinessException {
		boolean closed = false;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: isClosed  DAO  Start vvcode:" + CommonUtility.deNull(vvcode) + "contno:" + CommonUtility.deNull(contno) + 
					"cntrseqno:" + CommonUtility.deNull(cntrseqno) + "seqno:" + CommonUtility.deNull(seqno));
			

			sb.append(" select stuff_closed from cc_stuffing where active_status='A' ");
			sb.append(" and cntr_seq_nbr=:cntrseqno and cntr_nbr=:contno and var_nbr=:vvcode ");


			paramMap.put("cntrseqno", cntrseqno);
			paramMap.put("contno", contno);
			paramMap.put("vvcode", vvcode);
			log.info(" ***isClosed SQL *****" + sb.toString());
			log.info(" *** isClosed params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				if (rs.getString(1).equals("N"))
					closed = false;
				else
					closed = true;
			}
			log.info("END: *** isClosed Result *****" + closed);

		} catch (NullPointerException e) {
			log.info("Exception isClosed : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isClosed : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isClosed  DAO  END");
		}
		return closed;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->insertStuffing()
		
		@Override
		public String insertStuffing(List<String> edonos, List<String> edopkgs, List<String> esnnos, List<String> esnpkgs, String seqno,
				String vvcode, String cntrno, String cntrseqno, String userid) throws BusinessException {
			List<CcStuffingVo> qrys = new ArrayList<CcStuffingVo>();
			SqlRowSet rs = null;
			Map<String, Object> paramMap = new HashMap<String, Object>();
			StringBuilder sb = new StringBuilder();
			String seqNoFlag = seqno;

			try {
				log.info("START: insertStuffing  DAO  Start edonos:" + edonos.size() + "edopkgs:" + edopkgs.size() + "esnnos:" + esnnos.size()
						+ "esnpkgs:" + esnpkgs + "seqno:" + CommonUtility.deNull(seqno) + "vvcode:" + CommonUtility.deNull(vvcode) + "cntrno:" + CommonUtility.deNull(cntrno) 
						+ "cntrseqno:" + CommonUtility.deNull(cntrseqno) + "userid:" + CommonUtility.deNull(userid));
				

				if (seqno.equals("--")) {
					sb.append(" Select stuffing_seq_nbr.nextval seqno from dual ");

					try {
						log.info(" ***insertStuffing SQL *****" + sb.toString());

						rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
						while (rs.next()) {
							seqno = CommonUtility.deNull(rs.getString("seqno"));
						}

					} catch (Exception e) {
						seqno = "stuffing_seq_nbr.currval";
						log.info("Exception isClosed : ", e);
						//throw new BusinessException("M4201");
						
					}
					sb.setLength(0);
					log.info("seq no in EJB insertStuffing()" + seqno + " //qry :" + sb.toString());

					sb.append(" insert into cc_stuffing (stuff_seq_nbr,var_nbr,cntr_nbr ");
					sb.append(",cntr_seq_nbr,create_user_id,create_dttm) values( :seqNo, :vvCode, :cntrNo, ");
					sb.append(":cntrSeqNo, :userId, sysdate ) ");
					
					CcStuffingVo params = new CcStuffingVo();
					params.setSeqNo(seqno);
					params.setVvCode(vvcode);
					params.setCntrNo(cntrno);
					params.setCntrSeqNo(cntrseqno);
					params.setUserId(userid);
					qrys.add(params);
					
					SqlParameterSource[] insertInput = SqlParameterSourceUtils.createBatch(qrys.toArray());
					log.info("SQL insertStuffing" + sb.toString() + "input:" + insertInput.toString());
					int[] result = namedParameterJdbcTemplate.batchUpdate(sb.toString(), insertInput);
					log.info("Insert cc_stuffing result" + result.length);
//					pstmt.addBatch();
//					log.info(" ***insertStuffing SQL *****" + sb.toString());
//					pstmt.executeBatch();
					
					sb.setLength(0);
					
				}

				
				String detseq = "";
				String currentSeq = "";
				String currentSeqNum = null;

				CcStuffDetailValueObject ccStuffDetail = null;
				List<CcStuffDetailValueObject> ccStuffDetailList = new ArrayList<CcStuffDetailValueObject>();
				for (int lp = 0; lp < edonos.size(); lp++) {
					ccStuffDetail = new CcStuffDetailValueObject();
					if (seqNoFlag.equals("--")) {
						currentSeq = "Select stuffing_seq_nbr.currval seqno from dual";
						log.info(" *** insertStuffing SQL *****" + currentSeq.toString());
						log.info(" *** insertStuffing params *****" + paramMap.toString());

						rs = namedParameterJdbcTemplate.queryForRowSet(currentSeq.toString(), paramMap);

						while (rs.next()) {
							currentSeqNum = rs.getString(1);
						}
					}
			

					if (seqno.equalsIgnoreCase(currentSeqNum)) {
						detseq = "" + (lp + 1);
						ccStuffDetail.setDetseq(Integer.parseInt(detseq));
					} else {
						detseq = "(select nvl(max(stuff_det_seq_nbr),0)+1 as detseq from cc_stuffing_details where stuff_seq_nbr="
								+ seqno + ")";
						log.info(" *** insertStuffing SQL *****" + detseq.toString());
						log.info(" *** insertStuffing params *****" + paramMap.toString());
						rs =  namedParameterJdbcTemplate.queryForRowSet(detseq.toString(), paramMap);
						while (rs.next()) {
							ccStuffDetail.setDetseq(Integer.parseInt(rs.getString(1)) + lp);
						}
					}
					log.info("detseg insertStuffing() :" + detseq);
					ccStuffDetail.setSeqno(Integer.parseInt(seqno));
					ccStuffDetail.setEdonos(edonos.get(lp).toString());
					ccStuffDetail.setEdopkgs(Integer.parseInt(edopkgs.get(lp).toString()));
					ccStuffDetail.setUserid(userid);
					ccStuffDetailList.add(ccStuffDetail);
				}
				sb.append(" insert into cc_stuffing_details (stuff_det_seq_nbr,stuff_seq_nbr ");
				sb.append(" ,edo_esn_nbr,nbr_pkgs,edo_esn_ind,last_modify_user_id ");
				sb.append(" ,last_modify_dttm ) values (:detseq,:seqno,:edonos,:edopkgs, 'EDO', :userid, sysdate)");

				for (int s = 0; s < ccStuffDetailList.size(); s++) {
					paramMap.put("detseq", ccStuffDetailList.get(s).getDetseq());
					paramMap.put("seqno", ccStuffDetailList.get(s).getSeqno());
					paramMap.put("edonos", ccStuffDetailList.get(s).getEdonos());
					paramMap.put("edopkgs", ccStuffDetailList.get(s).getEdopkgs());
					paramMap.put("userid", ccStuffDetailList.get(s).getUserid());
					
					log.info(" *** insertStuffing SQL *****" + sb.toString());
					log.info(" *** insertStuffing params *****" + paramMap.toString());
					
					namedParameterJdbcTemplate.update(sb.toString(), paramMap);
				}

				sb.setLength(0);
				ccStuffDetailList = new ArrayList<CcStuffDetailValueObject>();
				for (int lp = 0; lp < esnnos.size(); lp++) {
					ccStuffDetail = new CcStuffDetailValueObject();
					if (seqNoFlag.equals("--")) {
						currentSeq = "Select stuffing_seq_nbr.currval seqno from dual";
						log.info(" *** insertStuffing SQL *****" + currentSeq.toString());
						log.info(" *** insertStuffing params *****" + paramMap.toString());
						rs = namedParameterJdbcTemplate.queryForRowSet(currentSeq.toString(), paramMap);

						while (rs.next()) {
							currentSeqNum = rs.getString(1);
						}
					}

					if (seqno.equalsIgnoreCase(currentSeqNum)) {
						detseq = "" + (edonos.size() + lp + 1);
						ccStuffDetail.setDetseq(Integer.parseInt(detseq));
					} else {
						detseq = "(select nvl(max(stuff_det_seq_nbr),0)+1 as detseq from cc_stuffing_details where stuff_seq_nbr="
								+ seqno + ")";
						log.info(" *** insertStuffing SQL *****" + detseq.toString());
						log.info(" *** insertStuffing params *****" + paramMap.toString());
						rs = namedParameterJdbcTemplate.queryForRowSet(detseq.toString(), paramMap);
						while (rs.next()) {
							ccStuffDetail.setDetseq(Integer.parseInt(rs.getString(1)) + lp);
						}
					}
					log.info("detseg insertStuffing() :" + detseq);

					ccStuffDetail.setSeqno(Integer.parseInt(seqno));
					ccStuffDetail.setEdonos(esnnos.get(lp).toString());
					ccStuffDetail.setEdopkgs(Integer.parseInt(esnpkgs.get(lp).toString()));
					ccStuffDetail.setUserid(userid);
					ccStuffDetailList.add(ccStuffDetail);
				}

			
				sb.append(" insert into cc_stuffing_details (stuff_det_seq_nbr,stuff_seq_nbr ");
				sb.append(" ,edo_esn_nbr,nbr_pkgs,edo_esn_ind,last_modify_user_id ");
				sb.append(" ,last_modify_dttm ) values (:detseq,:seqno,:esnnos,:esnpkgs, 'ESN', :userid, sysdate)");
				for (int s = 0; s < ccStuffDetailList.size(); s++) {
					paramMap.put("detseq", ccStuffDetailList.get(s).getDetseq());
					paramMap.put("seqno", ccStuffDetailList.get(s).getSeqno());
					paramMap.put("esnnos", ccStuffDetailList.get(s).getEdonos());
					paramMap.put("esnpkgs", ccStuffDetailList.get(s).getEdopkgs());
					paramMap.put("userid", ccStuffDetailList.get(s).getUserid());
					
					log.info(" *** insertStuffing SQL *****" + sb.toString());
					log.info(" *** insertStuffing params *****" + paramMap.toString());

					namedParameterJdbcTemplate.update(sb.toString(), paramMap);
				}
				log.info("END: *** insertStuffing Result *****" + CommonUtility.deNull(seqno));
			} catch (NullPointerException e) {
				log.info("Exception insertStuffing : ", e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception insertStuffing : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: insertStuffing  DAO  END");
			}
			return seqno;
		}
		
		
	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->getStuffingDetails()
	/**
	 * Method to get the stuffing details as List of StuffingDetailObject class
	 * 
	 * @param vvcode    Var Number of the Vessel
	 * @param contno    Container Number
	 * @param cntrseqno Container Sequence Number related to container number.
	 * @param seqno     Master Stuffing Sequence Number.
	 * @return ArrayList of StuffingDetailObject.
	 * @throws BusinessException
	 * 
	 */
	
	@Override
	public List<StuffingDetailObject> getStuffingDetails(String vvcode, String contno, String cntrseqno, String seqno)
			throws BusinessException {
		List<StuffingDetailObject> stuffingdetaillist = new ArrayList<StuffingDetailObject>();

		if (seqno.equals("--"))
			return stuffingdetaillist;

		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getStuffingDetails  DAO  Start vvcode:" + CommonUtility.deNull(vvcode) + "contno:" + CommonUtility.deNull(contno) + "cntrseqno:"
					+ CommonUtility.deNull(cntrseqno) + "seqno:" + CommonUtility.deNull(seqno));
			

			sb.append(" select dtls.stuff_seq_nbr,dtls.stuff_det_seq_nbr,dtls.edo_esn_ind, ");
			sb.append(" dtls.edo_esn_nbr,dtls.nbr_pkgs,dtls.rec_status,decode(stf.stuff_closed, ");
			sb.append(" 'N','NotClosed','Closed') from ");
			sb.append(" cc_stuffing_details dtls,cc_stuffing stf where stf.active_status='A' ");
			sb.append(" and dtls.rec_status = 'A' and stf.stuff_seq_nbr= dtls.stuff_seq_nbr ");
			sb.append(" and dtls.stuff_seq_nbr=:seqno ");

			log.info(" ***getStuffingDetails SQL *****" + sb.toString());
			paramMap.put("seqno", seqno);
			log.info(" *** getStuffingDetails params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				StuffingDetailObject sdo = new StuffingDetailObject();
				sdo.setStuffingSeqNo(CommonUtility.deNull(rs.getString(1)));
				sdo.setStuffingDetSeqNo(CommonUtility.deNull(rs.getString(2)));
				sdo.setEdoEsnInd(CommonUtility.deNull(rs.getString(3)));
				sdo.setEdoEsnNo(CommonUtility.deNull(rs.getString(4)));
				sdo.setPkgs(CommonUtility.deNull(rs.getString(5)));
				sdo.setActiveStatus(CommonUtility.deNull(rs.getString(6)));
				sdo.setClosedStatus(CommonUtility.deNull(rs.getString(7)));

				stuffingdetaillist.add(sdo);
			}
			log.info("END: *** getStuffingDetails Result *****" + stuffingdetaillist.size());

		} catch (NullPointerException e) {
			log.info("Exception getStuffingDetails : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getStuffingDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getStuffingDetails  DAO  END");
		}
		return stuffingdetaillist;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->updateStuffing()
		/**
		 * Update the master and detail information of stuffing into Database.
		 * 
		 * @param edonos    List of EDO Numbers
		 * @param edopkgs   List of EDO number od pakages
		 * @param esnnos    List of ESN Numbers
		 * @param esnpkgs   List of ESN Number of Pakages
		 * @param seqno     Master Stuffing Sequence Number if Exist or "--"
		 * @param vvcode    Var Number of the Vessel
		 * @param cntrno    Container Number to be Stuffed.
		 * @param cntrseqno Conatiner Sequence Number related to the Container Number
		 * @param userid    userid of the user logged in.
		 * @return Master Stuffing sequence number to which entries are made.
		 * @throws BusinessException
		 */
		@Override
		public String updateStuffing(List<String> edonos, List<String> edopkgs, List<String> esnnos, List<String> esnpkgs, String seqno,
				String vvcode, String cntrno, String cntrseqno, String userid) throws BusinessException {
			
			SqlRowSet rs = null;
			Map<String, Object> paramMap = new HashMap<String, Object>();
			StringBuilder sb = new StringBuilder();

			try {
				log.info("START: updateStuffing  DAO  Start edonos:" + edonos.size() + "edopkgs:" + edopkgs.size() + "esnnos:" + esnnos.size()
						+ "esnpkgs:" + esnpkgs.size() + "vvcode:" + CommonUtility.deNull(vvcode) + "cntrno:" + CommonUtility.deNull(cntrno) 
						+ "cntrseqno:" + CommonUtility.deNull(cntrseqno) + "userid:" + CommonUtility.deNull(userid));
				
				
				sb = new StringBuilder();
				sb.append("delete from cc_stuffing_details where stuff_seq_nbr= :seqno");

				List<CcStuffDetailValueObject> deleteStuffing = new ArrayList<CcStuffDetailValueObject>();
				CcStuffDetailValueObject param = new CcStuffDetailValueObject();
				param.setSeqno(Integer.parseInt(seqno));
				deleteStuffing.add(param);
				//pstmt.addBatch();
				SqlParameterSource[] input = SqlParameterSourceUtils.createBatch(deleteStuffing.toArray());
				
				if (!isClosed(vvcode, cntrno, cntrseqno, seqno)) {
					namedParameterJdbcTemplate.batchUpdate(sb.toString(), input);
				}else {
					throw new BusinessException("ErrorMsg_Stuff_NotAmmend");
				}

				sb.setLength(0);
				log.info(" ***updateStuffing SQL *****" + sb.toString());

				sb.append("update cc_stuffing set last_modify_user_id = :userid ,last_modify_dttm=sysdate where active_status='A' and stuff_seq_nbr= :seqno");

				List<CcStuffDetailValueObject> updateStuffing = new ArrayList<CcStuffDetailValueObject>();
				param = new CcStuffDetailValueObject();
				param.setSeqno(Integer.parseInt(seqno));
				param.setUserid(userid);
				updateStuffing.add(param);
				SqlParameterSource[] inputUpdate = SqlParameterSourceUtils.createBatch(updateStuffing.toArray());
				//pstmt.addBatch();
				if (!isClosed(vvcode, cntrno, cntrseqno, seqno)) {
					//pstmt.executeBatch();
					namedParameterJdbcTemplate.batchUpdate(sb.toString(), inputUpdate);
				}else {
					throw new BusinessException("ErrorMsg_Stuff_NotAmmend");
				}
				
				log.info(" ***updateStuffing SQL *****" + sb.toString());
				sb.setLength(0);
				String detseq = "";
				for (int lp = 0; lp < edonos.size(); lp++) {
					List<CcStuffDetailValueObject> insertStuffing = new ArrayList<CcStuffDetailValueObject>();
					param = new CcStuffDetailValueObject();
					
					detseq = "(select nvl(max(stuff_det_seq_nbr),0)+1 from cc_stuffing_details where stuff_seq_nbr=" + seqno
							+ ")";
					log.info("updateStuffing() detseq :" + detseq);
					rs = namedParameterJdbcTemplate.queryForRowSet(detseq.toString(), paramMap);
					while (rs.next()) {
						param.setDetseq(Integer.parseInt(rs.getString(1)) + lp);
					}

					sb = new StringBuilder();
					sb.append(" insert into cc_stuffing_details (stuff_det_seq_nbr,stuff_seq_nbr ");
					sb.append("	,edo_esn_nbr,nbr_pkgs,edo_esn_ind,last_modify_user_id" + ",last_modify_dttm ) values ( ");
					sb.append("	:detseq, :seqno, :edonos, :edopkgs, 'EDO', :userid, sysdate)");
					
					param.setSeqno(Integer.parseInt(seqno));
					param.setEdonos(edonos.get(lp).toString());
					param.setEdopkgs(Integer.parseInt(edopkgs.get(lp).toString()));
					param.setUserid(userid);
					insertStuffing.add(param);
					SqlParameterSource[] inputInsert = SqlParameterSourceUtils.createBatch(insertStuffing.toArray());
					log.info("input:" + inputInsert.toString());
					//pstmt.addBatch();
					if (!isClosed(vvcode, cntrno, cntrseqno, seqno)) {
						//pstmt.executeBatch();
						namedParameterJdbcTemplate.batchUpdate(sb.toString(), inputInsert);
					}else {
						throw new BusinessException("ErrorMsg_Stuff_NotAmmend");
					}
					log.info(" ***updateStuffing SQL *****" + sb.toString());
				}
				
				sb.setLength(0);
				for (int lp = 0; lp < esnnos.size(); lp++) {
					List<CcStuffDetailValueObject> insertStuffing2 = new ArrayList<CcStuffDetailValueObject>();
					param = new CcStuffDetailValueObject();
					
					if (seqno.equalsIgnoreCase("stuffing_seq_nbr.currval")) {
						detseq = "" + (edonos.size() + lp + 1);
						param.setDetseq(Integer.parseInt(detseq));
					} else {
						detseq = "(select nvl(max(stuff_det_seq_nbr),0)+1 from cc_stuffing_details where stuff_seq_nbr="
								+ seqno + ")";
						rs = namedParameterJdbcTemplate.queryForRowSet(detseq.toString(), paramMap);
						while (rs.next()) {
							param.setDetseq(Integer.parseInt(rs.getString(1)) + lp);
						}
					}

					sb = new StringBuilder();
					sb.append(" insert into cc_stuffing_details (stuff_det_seq_nbr,stuff_seq_nbr ");
					sb.append("	,edo_esn_nbr,nbr_pkgs,edo_esn_ind,last_modify_user_id, last_modify_dttm ) values ( ");
					sb.append("	:detseq, :seqno, :esnnos, :esnpkgs, 'ESN', :userid, sysdate)");
					
					
					param.setSeqno(Integer.parseInt(seqno));
					param.setEsnnos(esnnos.get(lp).toString());
					param.setEsnpkgs(Integer.parseInt(esnpkgs.get(lp).toString()));
					param.setUserid(userid);
					insertStuffing2.add(param);
					SqlParameterSource[] inputInsert2 = SqlParameterSourceUtils.createBatch(insertStuffing2.toArray());;
					//pstmt.addBatch();
					if (!isClosed(vvcode, cntrno, cntrseqno, seqno)) {
						//pstmt.executeBatch();
						namedParameterJdbcTemplate.batchUpdate(sb.toString(), inputInsert2);
					}else {
						throw new BusinessException("ErrorMsg_Stuff_NotAmmend");
					}
					log.info(" ***updateStuffing SQL *****" + sb.toString());
				}
				
				log.info("END: *** updateStuffing Result *****" + CommonUtility.deNull(seqno));
			} catch (NullPointerException e) {
				log.info("Exception updateStuffing : ", e);
				throw new BusinessException("M4201");
			} catch (BusinessException e) {
				log.info("Exception updateStuffing : ", e);
				throw new BusinessException(e.getMessage());
			} catch (Exception e) {
				log.info("Exception updateStuffing : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: updateStuffing  DAO  END");
			}
			return seqno;
		}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->getStuffingDetailsToAmend()
	/**
	 * Method to get the stuffing details as List of StuffingDetailObject class only
	 * if the record is not closed
	 * 
	 * @param vvcode    Var Number of the Vessel
	 * @param contno    Container Number
	 * @param cntrseqno Container Sequence Number related to container number.
	 * @param seqno     Master Stuffing Sequence Number.
	 * @return ArrayList of StuffingDetailObject.
	 * @throws BusinessException
	 */
	@Override
	public List<StuffingDetailObject> getStuffingDetailsToAmend(String vvcode, String contno, String cntrseqno, String seqno)
			throws BusinessException {
		List<StuffingDetailObject> stuffingdetaillist = new ArrayList<StuffingDetailObject>();

		if (seqno.equals("--"))
			return stuffingdetaillist;

		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getStuffingDetailsToAmend  DAO  Start vvcode:" + CommonUtility.deNull(vvcode) + "contno:" + CommonUtility.deNull(contno) + "cntrseqno:"
					+ CommonUtility.deNull(cntrseqno) + "seqno:" + CommonUtility.deNull(seqno));
			

			sb.append(" select dtls.stuff_seq_nbr,dtls.stuff_det_seq_nbr,dtls.edo_esn_ind, ");
			sb.append(" dtls.edo_esn_nbr,dtls.nbr_pkgs,dtls.rec_status,decode(stf.stuff_closed, ");
			sb.append(" 'N','NotClosed','Closed') from ");
			sb.append(" cc_stuffing_details dtls,cc_stuffing stf where stf.stuff_closed='N' ");
			sb.append(" and stf.active_status='A' ");
			sb.append(" and dtls.rec_status = 'A' and stf.stuff_seq_nbr= dtls.stuff_seq_nbr ");
			sb.append(" and dtls.stuff_seq_nbr=:seqno ");

			log.info(" ***getStuffingDetailsToAmend SQL *****" + sb.toString());

			paramMap.put("seqno", seqno);

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			while (rs.next()) {
				StuffingDetailObject sdo = new StuffingDetailObject();
				sdo.setStuffingSeqNo(CommonUtility.deNull(rs.getString(1)));
				sdo.setStuffingDetSeqNo(CommonUtility.deNull(rs.getString(2)));
				sdo.setEdoEsnInd(CommonUtility.deNull(rs.getString(3)));
				sdo.setEdoEsnNo(CommonUtility.deNull(rs.getString(4)));
				sdo.setPkgs(CommonUtility.deNull(rs.getString(5)));
				sdo.setActiveStatus(CommonUtility.deNull(rs.getString(6)));
				sdo.setClosedStatus(CommonUtility.deNull(rs.getString(7)));

				stuffingdetaillist.add(sdo);
			}
			log.info("END: *** getStuffingDetailsToAmend Result *****" + stuffingdetaillist.size());
		} catch (NullPointerException e) {
			log.info("Exception getStuffingDetailsToAmend : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getStuffingDetailsToAmend : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getStuffingDetailsToAmend  DAO  END");
		}
		return stuffingdetaillist;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->getBillAccountNos()
	/**
	 * Gets the Bill Account Number for the given vvcode.
	 * 
	 * @param vvcode varnbr of the vessel.
	 * @return ArrayList of Bill account number.
	 * @throws BusinessException
	 */
	@Override
	public List<String> getBillAccountNos(String vvcode) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<String> billacctnos = new ArrayList<String>();

		try {
			log.info("START: getBillAccountNos  DAO  Start vvcode:" + CommonUtility.deNull(vvcode));
			

			sb.append(" SELECT BILL_ACCT_NBR FROM VESSEL_CALL WHERE VV_CD =:vvcode ");
			log.info(" ***getBillAccountNos SQL *****" + sb.toString());

			paramMap.put("vvcode", vvcode);

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				billacctnos.add(rs.getString(1));
			}
			log.info("END: *** getBillAccountNos Result *****" + billacctnos.size());
		} catch (NullPointerException e) {
			log.info("Exception getBillAccountNos : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBillAccountNos : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBillAccountNos  DAO  END");
		}
		return billacctnos;

	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->getSchemeAccountNos()
	/**
	 * Method to get list of scheme account numbers.
	 * 
	 * @return ArrayList of SchemeAccountObject.
	 * @throws BusinessException
	 */
	@Override
	public List<SchemeAccountObject> getSchemeAccountNos() throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<SchemeAccountObject> schemeaccountlist = new ArrayList<SchemeAccountObject>();

		try {
			log.info("START: getSchemeAccountNos  DAO  Start ");
			

			sb.append(" select scheme_cd,scheme_desc,acct_nbr from ");
			sb.append(" vessel_scheme where ab_cd is not null ");
			log.info(" ***getSchemeAccountNos SQL *****" + sb.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				SchemeAccountObject sao = new SchemeAccountObject();
				sao.setSchemeCode(CommonUtility.deNull(rs.getString(1)));
				sao.setSchemeName(CommonUtility.deNull(rs.getString(2)));
				sao.setAccountNumber(CommonUtility.deNull(rs.getString(3)));
				schemeaccountlist.add(sao);
			}
			
			log.info("END: *** getSchemeAccountNos Result *****" + schemeaccountlist.size());
		} catch (NullPointerException e) {
			log.info("Exception getSchemeAccountNos : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSchemeAccountNos : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSchemeAccountNos  DAO  END");
		}
		return schemeaccountlist;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->assignBillableParty()
	/**
	 * Method to assign a billable party for the stuffing record.
	 * 
	 * @param vvcode    var nbr of vessel.
	 * @param contno    Container Number.
	 * @param cntrseqno Container Sequence Number.
	 * @param seqno     Master Stuffing Sequence Number.
	 * @param acctno    Account Number of billable party.
	 * @param userid    Id of user logged in.
	 * @throws BusinessException
	 */
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void assignBillableParty(String vvcode, String contno, String cntrseqno, String seqno, String acctno,
			String userid) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: assignBillableParty  DAO  Start vvcode:" + CommonUtility.deNull(vvcode) + "contno:" + CommonUtility.deNull(contno) + "cntrseqno:"
					+ CommonUtility.deNull(cntrseqno) + "seqno:" + CommonUtility.deNull(seqno) + "acctno:" + CommonUtility.deNull(acctno) + "userid:" + CommonUtility.deNull(userid));
			

			sb.append(" update cc_stuffing set bill_party_acct_nbr=:acctno ");
			sb.append(" ,last_modify_user_id=:userid,last_modify_dttm=sysdate ");
			sb.append("  where active_status='A' and var_nbr=:vvcode ");
			sb.append(" and stuff_seq_nbr=:seqno ");

			log.info(" ***assignBillableParty SQL *****" + sb.toString());

			paramMap.put("acctno", acctno);
			paramMap.put("userid", userid);
			paramMap.put("vvcode", vvcode);
			paramMap.put("seqno", seqno);

			if (checkAccountNumber(acctno)) {
				if (!isClosed(vvcode, contno, cntrseqno, seqno))
					namedParameterJdbcTemplate.update(sb.toString(), paramMap);
				else {
					log.info("Writing from StuffingEJB.assignBillableParty");
					log.info("Cannot Assign Billable Party.Record Not Available or Closed By Another User.");

					throw new BusinessException("ErrorMsg_CannotAssign_BillableParty");
				}

			} else {
				log.info("Writing from StuffingEJB.assignBillableParty");
				log.info("Invalid Account Nbr" + acctno);
				throw new BusinessException("M41208");
			}
		} catch (NullPointerException e) {
			log.info("Exception assignBillableParty : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception assignBillableParty : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception assignBillableParty : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: assignBillableParty  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->checkAccountNumber()
	/**
	 * Checks the validity of the user entered account number in the Assign Billing
	 * Party screen.
	 * 
	 * @param con    GBMS Connection object.
	 * @param acctno account number which has to be checked.
	 * @return boolean 'true' if exist else 'false'.
	 * @throws BusinessException
	 */
	private boolean checkAccountNumber(String acctno) throws BusinessException {

		String straccnbrcount = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: checkAccountNumber  DAO  Start acctno:" + CommonUtility.deNull(acctno));
			

			sb.append(" SELECT COUNT(*) FROM CUST_ACCT A, COMPANY_CODE B, ");
			sb.append(" CUSTOMER C  WHERE A.BUSINESS_TYPE LIKE '%G%' AND ");
			sb.append(" A.ACCT_NBR IS NOT NULL AND B.CO_CD=C.CUST_CD AND ");
			sb.append(" A.ACCT_STATUS_CD='A' AND ");
			sb.append(" UPPER(A.ACCT_NBR)=UPPER(:acctno ) ");

			log.info(" ***checkAccountNumber SQL *****" + sb.toString());

			paramMap.put("acctno", acctno);

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				straccnbrcount = CommonUtility.deNull(rs.getString(1));
			}
			if (((straccnbrcount).trim().equalsIgnoreCase("")) || straccnbrcount == null) {
				straccnbrcount = "0";
			}
			int intaccnbrcount = Integer.parseInt(straccnbrcount);

			if (intaccnbrcount > 0) {
				return true;
			} else {
				return false;
			}

		} catch (NullPointerException e) {
			log.info("Exception checkAccountNumber : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkAccountNumber : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkAccountNumber  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->isChkDNCreated()
	// added by vinayak on 11/02/2004
	@Override
	public boolean isChkDNCreated(String seqno) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		boolean upd = false;

		try {
			log.info("START: isChkDNCreated  DAO  Start seqno:" + CommonUtility.deNull(seqno));
			

			sb.append(" SELECT g.edo_asn_nbr, d.DN_STATUS,cd.NBR_PKGS FROM gb_edo g, ");
			sb.append(" dn_details d, cc_stuffing c, cc_stuffing_details cd WHERE ");
			sb.append(" g.edo_asn_nbr=d.edo_asn_nbr AND d.dn_status='A' AND ");
			sb.append(" g.edo_status='A' AND d.tesn_asn_nbr=c.stuff_seq_nbr AND ");
			sb.append(" g.crg_status IN ('T','R') AND c.stuff_seq_nbr=cd.stuff_seq_nbr ");
			sb.append(" AND cd.edo_esn_nbr=g.edo_asn_nbr AND cd.edo_esn_ind='EDO' AND ");
			sb.append(" c.active_status='A' AND c.stuff_seq_nbr=:seqno ");

			log.info(" ***isChkDNCreated SQL *****" + sb.toString());

			paramMap.put("seqno", seqno);

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				upd = true;
			}
			log.info("END: *** isChkDNCreated Result *****" + upd);
		} catch (NullPointerException e) {
			log.info("Exception isChkDNCreated : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isChkDNCreated : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isChkDNCreated  DAO  END");
		}
		return upd;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->cancelGbEdoUpd()
	// added by vinayak on 11/02/2004
	@Override
	public boolean cancelGbEdoUpd(String seqno) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		boolean upd = false;
		String strGbEdoUpd = "";

		try {
			log.info("START: cancelGbEdoUpd  DAO  Start seqno:" + CommonUtility.deNull(seqno));
			

			sb.append(" SELECT e.edo_asn_nbr,cd.NBR_PKGS FROM cc_stuffing c, ");
			sb.append(" cc_stuffing_details cd, gb_edo e WHERE c.stuff_seq_nbr=:seqno ");
			sb.append(" AND c.stuff_seq_nbr=cd.stuff_seq_nbr AND cd.edo_esn_ind='EDO' ");
			sb.append(" AND cd.edo_esn_nbr=e.edo_asn_nbr AND e.crg_status IN ('T','R') ");

			log.info(" ***cancelGbEdoUpd SQL *****" + sb.toString());

			paramMap.put("seqno", seqno);

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				sb = new StringBuilder();

				sb.append(" UPDATE gb_edo SET TRANS_NBR_PKGS=TRANS_NBR_PKGS- :nbrPkgs ");
				sb.append(" WHERE EDO_ASN_NBR=:edoAsbNbr ");

				log.info("cancelGbEdoUpd() strGbEdoUpd :" + strGbEdoUpd);
				log.info(" ***cancelGbEdoUpd SQL *****" + sb.toString());

				paramMap.put("nbrPkgs", rs.getInt("NBR_PKGS"));
				paramMap.put("edoAsbNbr", rs.getInt("edo_asn_nbr"));

				int intUpGbEdo = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
				log.info("intUpGbEdo :" + intUpGbEdo);
				if (intUpGbEdo == 0) {
					upd = true;
					throw new BusinessException("ErrorMsg_Update_Fail");
				}

			}
			log.info("END: *** cancelGbEdoUpd Result *****" + upd);
		} catch (NullPointerException e) {
			log.info("Exception cancelGbEdoUpd : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception cancelGbEdoUpd : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception cancelGbEdoUpd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: cancelGbEdoUpd  DAO  END");
		}
		return upd;

	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->cancelStuffing()
		/**
		 * Method Cancels the stuffing record related to the container number and
		 * vvcode.
		 * 
		 * @param vvcode    Var Number of the Vessel
		 * @param cntrno    Container Number
		 * @param cntrseqno Container Sequence Number related to container number.
		 * @param seqno     Master Stuffing Sequence Number.
		 * @param userid    Id of User Logged in.
		 * @throws BusinessException
		 */
		@Override
		public void cancelStuffing(String vvcode, String cntrno, String cntrseqno, String seqno, String userid)
				throws BusinessException {
			
			StringBuilder sb = new StringBuilder();

			try {
				log.info("START: cancelStuffing  DAO  Start vvcode:" + CommonUtility.deNull(vvcode) + "cntrno:" + CommonUtility.deNull(cntrno) + "cntrseqno:"
						+ CommonUtility.deNull(cntrseqno) + "seqno:" + CommonUtility.deNull(seqno) + "userid:" + CommonUtility.deNull(userid));
				

				sb = new StringBuilder();
				sb.append(" update cc_stuffing set active_status='N',last_modify_user_id= :userId ,");
				sb.append(" last_modify_dttm=sysdate where active_status='A' ");
				sb.append(" and stuff_seq_nbr= :seqNo ");
				
				List<CcStuffingVo> updateStuffing = new ArrayList<CcStuffingVo>();
				CcStuffingVo params = new CcStuffingVo();
				params.setSeqNo(seqno);
				params.setUserId(userid);
				updateStuffing.add(params);
				SqlParameterSource[] inputUpdateS = SqlParameterSourceUtils.createBatch(updateStuffing.toArray());

				log.info(" ***cancelStuffing SQL *****" + sb.toString());
				if (!isClosed(vvcode, cntrno, cntrseqno, seqno)) {
					namedParameterJdbcTemplate.batchUpdate(sb.toString(), inputUpdateS);
				}else {
					throw new BusinessException("ErrorMsg_Stuff_Not_Cancel");
				}
				
				sb.setLength(0);
				sb.append(" update cc_stuffing_details set rec_status='N',last_modify_user_id = :userid ");
				sb.append(" ,last_modify_dttm=sysdate where rec_status='A' ");
				sb.append(" and stuff_seq_nbr= :seqno ");
				
				List<CcStuffDetailValueObject> updateStuffingDetails = new ArrayList<CcStuffDetailValueObject>();
				CcStuffDetailValueObject param = new CcStuffDetailValueObject();
				param.setSeqno(Integer.parseInt(seqno));
				param.setUserid(userid);
				updateStuffingDetails.add(param);
				SqlParameterSource[] inputUpdate = SqlParameterSourceUtils.createBatch(updateStuffingDetails.toArray());
				log.info(" ***cancelStuffing SQL *****" + sb.toString());

				if (!isClosed(vvcode, cntrno, cntrseqno, seqno)) {
					// incorporate a check to see whether stuffing has already been closed, then
					// need to update gb_edo for
					// those edo_esn_ind='EDO' and crg_status='T'/'R' need to substrat
					// trans_nbr_pkgs-cc_stuffing_details.nbr_pkgs

					log.info(" ***cancelStuffing SQL *****" + sb.toString());
					namedParameterJdbcTemplate.batchUpdate(sb.toString(), inputUpdate);
				} else {
					throw new BusinessException("ErrorMsg_Stuff_Not_Cancel");
				}

			} catch (NullPointerException e) {
				log.info("Exception cancelStuffing : ", e);
				throw new BusinessException("M4201");
			} catch (BusinessException e) {
				log.info("Exception cancelStuffing : ", e);
				throw new BusinessException(e.getMessage());
			} catch (Exception e) {
				log.info("Exception cancelStuffing : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: cancelStuffing  DAO  END");
			}
		}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->isStuffingDttmLesser()
	/**
	 * Method to check the entered date and time is less than current date and time.
	 * 
	 * @param stuffdttm Date and time in 'ddmmyyyy hh24mi' format.
	 * @return 'true' if less than current time otherwise 'false'.
	 * @throws BusinessException
	 */
	@Override
	public boolean isStuffingDttmLesser(String stuffdttm) throws BusinessException {
		boolean lesser = false;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: isStuffingDttmLesser  DAO  Start stuffdttm:" + CommonUtility.deNull(stuffdttm));
			

			sb.append(" select 'true' from dual where sysdate >= ");
			sb.append(" to_date(:stuffdttm,'ddmmyyyy hh24mi') ");

			log.info(" ***isStuffingDttmLesser SQL *****" + sb.toString());

			paramMap.put("stuffdttm", stuffdttm);

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				if (rs.getString(1).equals("true"))
					lesser = true;
				else
					lesser = false;
			}
			log.info("END: *** isStuffingDttmLesser Result *****" + lesser);
		} catch (NullPointerException e) {
			log.info("Exception isStuffingDttmLesser : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isStuffingDttmLesser : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isStuffingDttmLesser  DAO  END");
		}
		return lesser;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->isGbEdoUpd()
	// added by vinayak on 11/02/2004
	@Override
	public boolean isGbEdoUpd(String seqno) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		boolean upd = false;
		String strGbEdoUpd = "";

		try {
			log.info("START: isGbEdoUpd  DAO  Start seqno:" + CommonUtility.deNull(seqno));
			

			sb.append(" SELECT e.edo_asn_nbr,cd.NBR_PKGS FROM cc_stuffing c, ");
			sb.append(" cc_stuffing_details cd, gb_edo e WHERE c.stuff_seq_nbr=:seqno ");
			sb.append(" AND c.stuff_seq_nbr=cd.stuff_seq_nbr AND cd.edo_esn_ind='EDO' ");
			sb.append(" AND cd.edo_esn_nbr=e.edo_asn_nbr AND e.crg_status IN ('T','R') ");

			log.info(" ***isGbEdoUpd SQL *****" + sb.toString());

			paramMap.put("seqno", seqno);
			log.info(" *** isGbEdoUpd params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				sb = new StringBuilder();
				log.info("isGbEdoUpd() strGbEdoUpd :" + strGbEdoUpd);

				sb.append(" UPDATE gb_edo SET TRANS_NBR_PKGS=TRANS_NBR_PKGS- :nbrPkgs ");
				sb.append(" WHERE EDO_ASN_NBR=:edoAsbNbr ");
				sb.append(" AND NBR_PKGS>=DN_NBR_PKGS+TRANS_NBR_PKGS+(select nbr_pkgs ");
				sb.append(" from cc_stuffing_details where edo_esn_nbr=:edoAsbNbr ");
				sb.append(" and stuff_seq_nbr=:seqno ) ");

				log.info(" ***isGbEdoUpd SQL *****" + sb.toString());

				paramMap.put("nbrPkgs", rs.getInt("NBR_PKGS"));
				paramMap.put("edoAsbNbr", rs.getInt("edo_asn_nbr"));
				paramMap.put("seqno", seqno);
				log.info(" *** isGbEdoUpd params *****" + paramMap.toString());
				int intUpGbEdo = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
				log.info("intUpGbEdo :" + intUpGbEdo);
				if (intUpGbEdo == 0) {
					upd = true;
					break;
				}

			}
			log.info("END: *** isGbEdoUpd Result *****" + upd);
		} catch (NullPointerException e) {
			log.info("Exception isGbEdoUpd : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isGbEdoUpd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isGbEdoUpd  DAO  END");
		}
		return upd;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->closeStuffing()
		/**
		 * Method for closing of stuffing for a container.Stores the stuffing date and
		 * time,also logs the charges event log and vessel transaction log if charges
		 * are not waived.
		 * 
		 * @param vvcode      var nbr of the vessel.
		 * @param contno      Container Number for which record should be closed.
		 * @param cntrseqno   Container Sequence Number.
		 * @param seqno       Master Stuffing Sequence Number.
		 * @param stuffdttm   stuffing date and time.
		 * @param waivecharge waive charge indicator."Y" if waived else "N".
		 * @param userid      Id of the user logged in.
		 * @throws Exception
		 */
		@Override
		public void closeStuffing(String vvcode, String contno, String cntrseqno, String seqno, String stuffdttm,
				String waivecharge, String userid) throws Exception {
			if (seqno == null || seqno.trim().equals("") || seqno.trim().equals("--"))
				throw new BusinessException("M41207");
			log.info("after business exception");
		
			List<GeneralEventLogValueObject> gbloglist = new ArrayList<GeneralEventLogValueObject>();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			StringBuilder sb = new StringBuilder();

			try {
				log.info("START: closeStuffing  DAO  Start vvcode :" + CommonUtility.deNull(vvcode) + "contno :" + CommonUtility.deNull(contno) + "cntrseqno :"
						+ CommonUtility.deNull(cntrseqno) + "seqno :" + CommonUtility.deNull(seqno) + "stuffdttm :" + CommonUtility.deNull(stuffdttm) + 
						"waivecharge :" + CommonUtility.deNull(waivecharge) + "userid :" + CommonUtility.deNull(userid));

				

				sb = new StringBuilder();
				sb.append(" update cc_stuffing set stuff_closed='Y',stuffing_dttm=to_date ( :stuffdttm ,'ddmmyyyy hh24mi'), " );
				sb.append(" waive_charge=:waivecharge ,last_modify_user_id= :userId ,last_modify_dttm = sysdate ");
				sb.append(" where active_status='A' and cntr_seq_nbr= :cntrSeqNo and cntr_nbr= :cntrNo and var_nbr= :vvCode ");

				List<CcStuffingVo> updateStuff = new ArrayList<CcStuffingVo>();
				CcStuffingVo params = new CcStuffingVo();
				params.setStuffdttm(stuffdttm);
				params.setWaivecharge(waivecharge);
				params.setUserId(userid);
				params.setCntrSeqNo(cntrseqno);
				params.setCntrNo(contno);
				params.setVvCode(vvcode);
				updateStuff.add(params);
				SqlParameterSource[] updateInput = SqlParameterSourceUtils.createBatch(updateStuff.toArray());
				//pstmt.addBatch();

				log.info(" ***closeStuffing SQL *****" + sb.toString());
				namedParameterJdbcTemplate.batchUpdate(sb.toString(), updateInput);
				//pstmt.executeBatch();

				log.info("qry.toString() :" + sb.toString());
				/*
				 * comment if edo.stuff_ind is removed start vinay on 5 jan 2004 try {
				 * qry.delete(0, qry.length()); } catch (Exception e) { }
				 * 
				 * qry.append("update gb_edo set stuff_ind='Y' ");
				 * qry.append(" where edo_status='A' and "); qry.append(" edo_asn_nbr in ");
				 * qry.append(" (select distinct edo_esn_nbr ");
				 * qry.append(" from cc_stuffing_details where ");
				 * qry.append(" nbr_pkgs > 0 and "); qry.append(" edo_esn_ind='EDO' and ");
				 * qry.append(" rec_status='A' and "); qry.append(" stuff_seq_nbr='" + seqno +
				 * "')");
				 * 
				 * 
				 * qrys.add(qry.toString()); sqlstmt.addBatch(qry.toString());
				 * 
				 * try { qry.delete(0, qry.length()); } catch (Exception e) { } //comment if
				 * edo.stuff_ind is removed end qry.append("update esn set stuff_ind='Y' ");
				 * qry.append(" where esn_status='A' and "); qry.append(" esn_asn_nbr in ");
				 * qry.append(" (select distinct edo_esn_nbr ");
				 * qry.append(" from cc_stuffing_details where ");
				 * qry.append(" nbr_pkgs > 0 and "); qry.append(" edo_esn_ind='ESN' and ");
				 * qry.append(" rec_status='A' and "); qry.append(" stuff_seq_nbr='" + seqno +
				 * "')");
				 * 
				 * qrys.add(qry.toString()); sqlstmt.addBatch(qry.toString());
				 */
				sb = new StringBuilder();
				sb.append("select ");
				sb.append("iso_size_type_cd ");
				sb.append(",imdg_cl_cd ");
				sb.append(",oog_oh ");
				sb.append(",oog_ol_front ");
				sb.append(",oog_ol_back ");
				sb.append(",oog_ow_right ");
				sb.append(",oog_ow_left ");
				sb.append(",refr_ind ");
				sb.append(",uc_ind ");
				sb.append(",over_sz_ind ");
				sb.append(",special_details ");
//			    following 2 lines added by karthi on 14/04/04    To capture previouscontainer status and cat code before stuffing/unstuffing        
				sb.append(",cat_cd ");
				sb.append(",status ");
				// up to this... karthi.
				sb.append(" from ");
				sb.append(" cntr ");
				sb.append(" where ");
				sb.append("cntr_seq_nbr='" + cntrseqno + "'");
				Map<String, Object> value = getContainerDetails(sb.toString());
				// following lines added by karthi on 14/04/04 To capture previouscontainer
				// status and cat code before stuffing/unstuffing

				String cat_CdVar = "";
				String statusVar = "";
				cat_CdVar = value.get("catcode").toString();
				statusVar = value.get("status").toString();
				// up to this... karthi.
//			      String strQry="select iso_size_type_cd,imdg_cl_cd,oog_oh,oog_ol_front,oog_ol_back,oog_ow_right,oog_ow_left,refr_ind,uc_ind,over_sz_ind,special_details from cntr where cntr_seq_nbr='" +  cntrseqno + "'";
				// Hashtable value = getContainerDetails(con, strQry);

				String cntrcatcd = cntrCommonFuncRepo.getCntrCatCd(value.get("isosize").toString(), value.get("imdgclcd").toString(),
						getInt(value.get("oogoh")), getInt(value.get("oogolfront")), getInt(value.get("oogolback")),
						getInt(value.get("oogowright")), getInt(value.get("oogowleft")), value.get("refrind").toString(),
						value.get("ucind").toString(), value.get("overszind").toString(),
						value.get("specialdtls").toString(), "F");

				log.info("container cat code is >>>>>>>>>>>>::::::::::::" + cntrcatcd);

				sb = new StringBuilder();
				sb.append("update cntr SET status='F',cat_cd= :cntrcatcd ");
				sb.append(" WHERE  txn_status = 'A' and cntr_seq_nbr= :cntrseqno ");
				paramMap.put("cntrcatcd", cntrcatcd);
				paramMap.put("cntrseqno", cntrseqno);
				//pstmt.addBatch();

				log.info(" ***closeStuffing SQL *****" + sb.toString());
				namedParameterJdbcTemplate.update(sb.toString(), paramMap);
				//pstmt.executeBatch();
				// 09 Dec 2003 add query
				// 14/04/04 karthi added to vars cat_cd,status to insert into cntr_txn To
				// capture previouscontainer status and cat code before stuffing/unstuffing
				StringBuilder sb1 = new StringBuilder();
				sb1.append(" insert into cntr_txn(CNTR_SEQ_NBR,CNTR_NBR,TXN_DTTM,TXN_CD,LAST_MODIFY_USER_ID,");
				sb1.append(" LAST_MODIFY_DTTM,LOAD_VV_CD,CAT_CD,STATUS) values(:cntrseqno , :contno ,");
				sb1.append(" to_date(:stuffdttm , 'DDMMYYYY HH24MI'), 'STUC', :userid ,");
				sb1.append(" to_date(:stuffdttm , 'DDMMYYYY HH24MI'), :vvcode , :cat_CdVar , :statusVar )" );

				log.info(" ***closeStuffing SQL *****" + sb1.toString());
				paramMap.put("cntrseqno", cntrseqno);
				paramMap.put("contno", contno);
				paramMap.put("stuffdttm", stuffdttm);
				paramMap.put("userid", userid);
				paramMap.put("vvcode", vvcode);
				paramMap.put("cat_CdVar", cat_CdVar);
				paramMap.put("statusVar", statusVar);
				int countCntr = namedParameterJdbcTemplate.update(sb1.toString(), paramMap);

				// add query to update gb_edo table for those TS cargo stuff into cntr,
				// edo_esn_ind='EDO'

				if (countCntr == 0) {
					// sessionContext.setRollbackOnly();
					log.info("Record Cannot be inserted into Database");
					throw new BusinessException("M4201");
				}
				Timestamp txndttm = getSystemDate();

				StringBuilder detqry = new StringBuilder();
				detqry.append("select ");
				detqry.append("decode(vc.scheme,'JNL','JNL','JBT','JBT','JLR') scheme");
				detqry.append(",decode(nvl(ct.size_ft,20),20,2,40,4) cntrsize");
				detqry.append(",nvl(stf.bill_party_acct_nbr,vc.bill_acct_nbr)");
				detqry.append(" from ");
				detqry.append("cc_stuffing stf");
				detqry.append(",cntr ct");
				detqry.append(",vessel_call vc");
				detqry.append(" where ");
				detqry.append("stf.cntr_nbr=ct.cntr_nbr");
				detqry.append(" and ");
				detqry.append("stf.active_status='A'");
				detqry.append(" and ");
				detqry.append("stf.stuff_seq_nbr='" + seqno + "'");
				detqry.append(" and ");
				detqry.append("ct.cntr_seq_nbr='" + cntrseqno + "'");
				detqry.append(" and ");
				detqry.append("ct.cntr_nbr='" + contno + "'");
				detqry.append(" and ");
				detqry.append("vc.vv_cd='" + vvcode + "'");

				Map<String, Object> details = getBillingDetails(detqry.toString());

				VesselTxnEventLogValueObject vsllogvalueobject = new VesselTxnEventLogValueObject();
				vsllogvalueobject.setVvCd(vvcode);
				vsllogvalueobject.setTxnDttm(txndttm);
				vsllogvalueobject.setLastModifyDttm(txndttm);
				vsllogvalueobject.setLastModifyUserId(userid);

				// commented by Vinayak on 09 Feb 2004 : to remove cargo charges logging
				/*
				 * if (details.get("scheme").toString().equalsIgnoreCase("JNL") ||
				 * details.get("scheme").toString().equalsIgnoreCase("JBT")) { ArrayList
				 * chargelist = getChargeList(con, vvcode, seqno, "Service",
				 * details.get("scheme").toString(), details.get("billacctnbr"). toString(),
				 * stuffdttm, userid, txndttm);
				 * 
				 * if (chargelist.size() > 0) { vsllogvalueobject.setBillSvcChargeInd("Y");
				 * gbloglist.addAll(chargelist); } } else if
				 * (details.get("scheme").toString().equalsIgnoreCase("JLR")) { ArrayList
				 * chargelist = getChargeList(con, vvcode, seqno, "Wharf",
				 * details.get("scheme").toString(), details.get("billacctnbr"). toString(),
				 * stuffdttm, userid, txndttm); if (chargelist.size() > 0) {
				 * vsllogvalueobject.setBillWharfInd("Y"); gbloglist.addAll(chargelist); } }
				 * 
				 * log.info(" **************  gbloglist size 1 " + gbloglist.size() +
				 * " **********************  ");
				 * 
				 * ArrayList chargelist = getChargeList(con, vvcode, seqno, "Store",
				 * details.get("scheme").toString(), details.get("billacctnbr").toString(),
				 * stuffdttm, userid, txndttm); if (chargelist.size() > 0) {
				 * vsllogvalueobject.setBillStoreInd("Y"); gbloglist.addAll(chargelist); }
				 * 
				 * log.info(" **************  gbloglist size 2 " + gbloglist.size() +
				 * " **********************  ");
				 */

				if (waivecharge.equalsIgnoreCase("N")) {
					vsllogvalueobject.setBillStuffInd("Y");

					GeneralEventLogValueObject generallogvalueobject = new GeneralEventLogValueObject();
//			        generallogvalueobject.setDiscVvCd(vvcode);
					generallogvalueobject.setLoadVvCd(vvcode);
					generallogvalueobject.setVvInd("L");
					generallogvalueobject.setBusinessType("G");
					generallogvalueobject.setSchemeCd(CommonUtility.deNull(details.get("scheme").toString()));
					generallogvalueobject.setTariffMainCatCd("CC");
					generallogvalueobject.setTariffSubCatCd("SU");
					generallogvalueobject.setMvmt("00");
					generallogvalueobject.setType("00");
					generallogvalueobject.setCntrCat("0");
					generallogvalueobject.setCntrSize(CommonUtility.deNull(details.get("cntrsize").toString()));
					generallogvalueobject.setLocalLeg("EX");
					generallogvalueobject.setRefInd("SU");
					generallogvalueobject.setCntrNbr(contno); //
					generallogvalueobject.setCntrSeqNbr(Integer.valueOf(cntrseqno).intValue());
					generallogvalueobject.setBillAcctNbr(CommonUtility.deNull(details.get("billacctnbr").toString()));
					generallogvalueobject.setLastModifyUserId(userid);
					generallogvalueobject.setLastModifyDttm(txndttm);

					gbloglist.add(generallogvalueobject);
				} else {
					sb = new StringBuilder();
					sb.append("update cc_stuffing set ");
					sb.append("bill_stuff_triggered_ind='X' where active_status='A' ");
					sb.append("and stuff_seq_nbr= :seqNo ");

					List<CcStuffingVo> updateStuff2 = new ArrayList<CcStuffingVo>();
					CcStuffingVo param = new CcStuffingVo();
					param.setSeqNo(seqno);
					updateStuff2.add(param);
					SqlParameterSource[] updateInput2 = SqlParameterSourceUtils.createBatch(updateStuff2.toArray());
					//pstmt.addBatch();
					log.info(" ***closeStuffing SQL *****" + sb.toString());
					namedParameterJdbcTemplate.batchUpdate(sb.toString(), updateInput2);

				}

				if (gbloglist.size() > 0) {
					log.info(" **************  gbloglist size 3 " + gbloglist.size() + " **********************  ");
					processGBLogRepo.executeGBCharges(vsllogvalueobject, gbloglist, "SU");
				}
			} catch (NullPointerException e) {
				log.info("Exception closeStuffing : ", e);
				throw new BusinessException("M4201");
			} catch (BusinessException e) {
				log.info("Exception closeStuffing : ", e);
				throw new BusinessException(e.getMessage());
			} catch (Exception e) {
				log.info("Exception closeStuffing : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: closeStuffing  DAO  END");
			}
		}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->getSystemDate()
	/**
	 * Method to get the timestamp.
	 * 
	 * @param con GBMS Connection Object.
	 * @return Timestamp.
	 * @throws BusinessException
	 */
	public Timestamp getSystemDate() throws BusinessException {
		StringBuilder sql = new StringBuilder();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Timestamp sdate = null;
		try {
			log.info("START: getSystemDate  DAO  Start Obj ");
			
			sql.append(" SELECT SYSDATE FROM DUAL ");
			log.info(" *** getSystemDate SQL *****" + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				sdate = (Timestamp) rs.getObject("SYSDATE");
			}
			
			log.info("END: *** getSystemDate Result *****" + sdate);
		} catch (NullPointerException e) {
			log.info("Exception getSystemDate : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSystemDate : ", e);
			throw new BusinessException("M4201");
		} finally {
			
			log.info("END: getSystemDate  DAO  END");
		}
		return sdate;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->getBillingDetails()
	/**
	 * Gives the container size,bill account number and vessel scheme for GB Charge
	 * event log
	 * 
	 * @param con GMS Connection
	 * @param qry query string
	 * @return values as hashtable
	 * @throws BusinessException
	 */
	private Map<String, Object> getBillingDetails(String qry) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> value = new HashMap<String, Object>();
		
		try {
			log.info("START: getBillingDetails  DAO  Start qry:" + CommonUtility.deNull(qry));
			rs = namedParameterJdbcTemplate.queryForRowSet(qry, paramMap);
			if (rs.next()) {
				value.put("scheme", CommonUtility.deNull(rs.getString(1)));
				value.put("cntrsize", CommonUtility.deNull(rs.getString(2)));
				value.put("billacctnbr", CommonUtility.deNull(rs.getString(3)));
			}
		} catch (NullPointerException e) {
			log.info("Exception getBillingDetails : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBillingDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBillingDetails  DAO  END" + (value != null ? value.toString() : ""));
		}
		return value;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->getContainerDetails()
	/**
	 * Gives the iso,imdgclscd,oh,olfront,olback,owright,owleft,refrind,ucind,
	 * oogind,specdetail, for getCntrCatcd while closing.
	 * 
	 * @param con GMS Connection
	 * @param qry query string
	 * @return values as hashtable
	 * @throws BusinessException
	 */
	private Map<String, Object> getContainerDetails(String qry) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> value = new HashMap<String, Object>();
		try {
			log.info("START: getContainerDetails  DAO  Start qry:" + CommonUtility.deNull(qry));

			rs = namedParameterJdbcTemplate.queryForRowSet(qry, paramMap);
			if (rs.next()) {
				value.put("isosize", CommonUtility.deNull(rs.getString(1)));
				value.put("imdgclcd", CommonUtility.deNull(rs.getString(2)));
				value.put("oogoh", CommonUtility.deNull(rs.getString(3)));
				value.put("oogolfront", CommonUtility.deNull(rs.getString(4)));
				value.put("oogolback", CommonUtility.deNull(rs.getString(5)));
				value.put("oogowright", CommonUtility.deNull(rs.getString(6)));
				value.put("oogowleft", CommonUtility.deNull(rs.getString(7)));
				value.put("refrind", CommonUtility.deNull(rs.getString(8)));
				value.put("ucind", CommonUtility.deNull(rs.getString(9)));
				value.put("overszind", CommonUtility.deNull(rs.getString(10)));
				value.put("specialdtls", CommonUtility.deNull(rs.getString(11)));
//		    following 2 lines added by karthi on 14/04/04    To capture previouscontainer status and cat code before stuffing/unstuffing        
				value.put("catcode", CommonUtility.deNull(rs.getString(12)));
				value.put("status", CommonUtility.deNull(rs.getString(13)));
				// up to this... karthi.

			}
			
			log.info("END: *** getContainerDetails Result *****" + value);
		} catch (NullPointerException e) {
			log.info("Exception getContainerDetails : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getContainerDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getContainerDetails  DAO  END");
		}
		return value;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->getInt()
	/**
	 * Takes a object and converts to int.If any exception occurs returns 0.
	 * 
	 * @param intobj as Object.
	 * @return int
	 */
	private int getInt(Object intobj) {
		try {
			log.info("START: getInt  DAO  Start Obj "+" intobj:"+intobj );
			log.info("END: *** getInt Result *****" + Integer.valueOf(intobj.toString()).intValue());
			return Integer.valueOf(intobj.toString()).intValue();
		} catch (Exception e) {
			return 0;
		}
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->updateWaiverStatus()
	/**
	 * Method to update waive charge status for stuffing.Cancel the charge log if
	 * the waive status is yes if bill is not generated.If waive status 'N' no auto
	 * chage logging will happen.Please see updateWaiveStatus_AutoBill for auto
	 * charges logging while update of waive status.
	 * 
	 * @param vvcode      Var No of the vessel.
	 * @param contno      container number
	 * @param cntrseqno   container sequence number
	 * @param seqno       stuffing sequence number
	 * @param waivecharge waive charge status
	 * @param userid      userid of the user currently logged in
	 * @throws BusinessException
	 */
	@Override
	public void updateWaiverStatus(String vvcode, String contno, String cntrseqno, String seqno, String waivecharge,
			String userid) throws BusinessException {
		

		
		String dbwaivests = new String();
	

		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: updateWaiverStatus  DAO  Start vvcode:" + CommonUtility.deNull(vvcode) + "contno:" + CommonUtility.deNull(contno) + "cntrseqno:"
					+ CommonUtility.deNull(cntrseqno) + "seqno:" + CommonUtility.deNull(seqno) + "waivecharge:" + CommonUtility.deNull(waivecharge));			

			sb = new StringBuilder();
			sb.append("select ");
			sb.append("waive_charge ");
			sb.append(",to_char(stuffing_dttm,'ddmmyyyy hh24mi') stuffdttm");
			sb.append(",bill_stuff_triggered_ind billind");
			sb.append(" from ");
			sb.append(" cc_stuffing ");
			sb.append(" where ");
			sb.append(" active_status='A' ");
			sb.append(" and ");
			sb.append(" cntr_nbr= :contno ");
			sb.append(" and cntr_seq_nbr = :cntrseqno ");
			sb.append(" and stuff_seq_nbr=:seqno ");

			log.info(" ***updateWaiverStatus SQL *****" + sb.toString());
			paramMap.put("contno", contno);
			paramMap.put("cntrseqno", cntrseqno);
			paramMap.put("seqno", seqno);
			log.info(" *** updateWaiverStatus params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				dbwaivests = CommonUtility.deNull(rs.getString("waive_charge"));
				
			}

			log.info("dbwaivests : " + dbwaivests + " waive charge : " + waivecharge);

			if (dbwaivests.equalsIgnoreCase("Y") && waivecharge.equalsIgnoreCase("N")) {
				log.info("stage 1");
				sb = new StringBuilder();
				sb.append(" update cc_stuffing set waive_charge= :waivecharge ");
				sb.append(" ,last_modify_user_id= :userid ");
				sb.append(" ,last_modify_dttm=sysdate where ");
				sb.append(" active_status='A' and cntr_seq_nbr= :cntrseqno");
				sb.append(" and cntr_nbr= :contno  and var_nbr= :vvcode ");

				// qrys.add(sb.toString());
				// pstmt.addBatch();
				paramMap.put("waivecharge", waivecharge);
				paramMap.put("userid", userid);
				paramMap.put("cntrseqno", cntrseqno);
				paramMap.put("contno", contno);
				paramMap.put("vvcode", vvcode);

				log.info(" ***updateWaiverStatus SQL *****" + sb.toString());
				// pstmt.executeBatch();
				log.info(" *** updateWaiverStatus params *****" + paramMap.toString());
				namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			} else if (dbwaivests.equalsIgnoreCase("N") && waivecharge.equalsIgnoreCase("Y")) {

				boolean billcancelled = processGBLogRepo.cancelStuffCharges(Integer.valueOf(cntrseqno).intValue(), "SU", vvcode, "L");
				if (!billcancelled) {
					throw new BusinessException("ErrorMsg_Bill_Already_Generate");
				} else {
					sb = new StringBuilder();
					sb.append(" update cc_stuffing set waive_charge= :waivecharge ");
					sb.append(" ,last_modify_user_id= :userid ");
					sb.append(" ,last_modify_dttm=sysdate where ");
					sb.append(" active_status='A' and cntr_seq_nbr= :cntrseqno");
					sb.append(" and cntr_nbr= :contno  and var_nbr= :vvcode ");

					// qrys.add(sb.toString());
					// pstmt.addBatch();
					paramMap.put("waivecharge", waivecharge);
					paramMap.put("userid", userid);
					paramMap.put("cntrseqno", cntrseqno);
					paramMap.put("contno", contno);
					paramMap.put("vvcode", vvcode);

					log.info(" ***updateWaiverStatus SQL *****" + sb.toString());
					// pstmt.executeBatch();
					log.info(" *** updateWaiverStatus params *****" + paramMap.toString());
					namedParameterJdbcTemplate.update(sb.toString(), paramMap);
				}
			}
		} catch (NullPointerException e) {
			log.info("Exception updateWaiverStatus : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception updateWaiverStatus : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception updateWaiverStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateWaiverStatus  DAO  END");
		}
	}
	
	
	// jp.src.ejb.sessionBeans.gbms.containerised.stuffing-->StuffingEJB-->isTesnNbr()
	@Override
	public boolean isTesnNbr(String esnno) throws BusinessException {
        boolean result = false;
        try {
        	log.info("START: isTesnNbr esnno : " + CommonUtility.deNull(esnno));
        	Map<String, String> paramMap = new HashMap<String, String>();
    		SqlRowSet rs = null;
    		
    		String sql = "select trans_type from esn where esn_asn_nbr = :esnno";
			paramMap.put("esnno", esnno);
			
			log.info("isTesnNbr sql : " + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

            if(rs.next()) {
              log.info("isTesnNbr result have data");
              String transType = rs.getString("trans_type");
              log.info("isTesnNbr transType: " + transType);
              if(transType.equalsIgnoreCase("C")) {
            	  result = true;
              }
            }
            log.info("isTesnNbr RESULT : " + result);
        } catch (NullPointerException e) {
			log.info("exception: isTesnNbr " + e.toString());
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: isTesnNbr " + e.toString());
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isTesnNbr  DAO  END");
		}
            return result;
    }

}
