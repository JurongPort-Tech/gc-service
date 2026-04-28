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
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.ContainerCommonFunctionRepo;
import sg.com.jp.generalcargo.dao.ProcessGBLogRepository;
import sg.com.jp.generalcargo.dao.UnStuffingRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.Dept;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.HSCode;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.UnStuffingCargoValueObject;
import sg.com.jp.generalcargo.domain.UnStuffingValueObject;
import sg.com.jp.generalcargo.domain.VesselSearchResponse;
import sg.com.jp.generalcargo.domain.VesselTxnEventLogValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.GbmsCommonUtility;

@Repository("unStuffingRepo")
public class UnStuffingJdbcRepository  implements UnStuffingRepository {

	private static final Log log = LogFactory.getLog(UnStuffingJdbcRepository.class);
	
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public String logStatusGlobal = "Y";

	@Autowired
	private ProcessGBLogRepository processGBLogRepos;
	@Autowired
	private ContainerCommonFunctionRepo cntrCmnFcRepo;

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getVesselVoy()
	/**
	 * Returns the list of vessel voyage details encapsulated in
	 * VesselVoyValueObject
	 * 
	 * @param companycode Company Code of the user logged in.
	 * @return ArrayList Return arraylist of VesselVoyValueObject.
	 * @throws BusinessException
	 * 
	 */
	@Override
	public List<VesselVoyValueObject> getVesselVoy(String cocode) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<VesselVoyValueObject> voyList = new ArrayList<VesselVoyValueObject>();
		String voynbr = "";
		String vslName = "";
		String VV_CD = "";

		try {
			log.info("START: getVesselVoy  DAO  Start cocode:" + cocode);

			if (cocode.equals("JP")) {
				/*
				 * /sql = "SELECT IN_VOY_NBR,VSL_NM,VV_CD FROM VESSEL_CALL WHERE (VV_STATUS_IND
				 * IN "+ "('PR','AP','AL','BR','UB') OR (VV_STATUS_IND='CL' AND TERMINAL='CT'))
				 * AND "+ "GB_CLOSE_BJ_IND <> 'Y' ORDER BY VSL_NM,IN_VOY_NBR";
				 */

				/*
				 * sql = "SELECT IN_VOY_NBR,VSL_NM,VV_CD FROM VESSEL_CALL WHERE VV_STATUS_IND IN
				 * "+ "('BR','UB', 'CL') AND (GB_CLOSE_BJ_IND <> 'Y' or GB_CLOSE_BJ_IND IS NULL)
				 * "+ "ORDER BY VSL_NM,IN_VOY_NBR";
				 */
				sb.append(" SELECT IN_VOY_NBR,VSL_NM,VV_CD FROM VESSEL_CALL WHERE (VV_STATUS_IND IN ");
				sb.append(" ('AP','AL','BR','UB') OR (TERMINAL='CT' AND VV_STATUS_IND = 'CL')) AND ");
				sb.append(" (GB_CLOSE_BJ_IND <> 'Y' or GB_CLOSE_BJ_IND IS NULL) ORDER BY VSL_NM,IN_VOY_NBR ");

			} else {
				/*
				 * sql = "SELECT IN_VOY_NBR,VSL_NM,VV_CD FROM VESSEL_CALL WHERE VV_STATUS_IND IN
				 * "+ "('PR','AP','AL','BR') AND GB_CLOSE_BJ_IND <> 'Y' AND
				 * nvl(DECLARANT_CUST_CD,CREATE_CUST_CD)= "+ "'"+cocode + "' ORDER BY
				 * VSL_NM,IN_VOY_NBR"; sql = "SELECT IN_VOY_NBR,VSL_NM,VV_CD FROM VESSEL_CALL
				 * WHERE VV_STATUS_IND IN "+ "('BR','UB', 'CL') AND (GB_CLOSE_BJ_IND <> 'Y' or
				 * GB_CLOSE_BJ_IND IS NULL) "+ "AND nvl(DECLARANT_CUST_CD,CREATE_CUST_CD)=
				 * '"+cocode + "' "+ "ORDER BY VSL_NM,IN_VOY_NBR";
				 */

				// ++ 19.10.2009 Changed by vietnd02 for GB CR
				/*
				 * sql = "SELECT IN_VOY_NBR,VSL_NM,VV_CD FROM VESSEL_CALL WHERE (VV_STATUS_IND
				 * IN " + "('AP','AL','BR','UB') OR (TERMINAL='CT' AND VV_STATUS_IND = 'CL'))
				 * AND " + "(GB_CLOSE_BJ_IND <> 'Y' or GB_CLOSE_BJ_IND IS NULL) " + "AND
				 * nvl(DECLARANT_CUST_CD,CREATE_CUST_CD)= '" + cocode + "' " +
				 * "ORDER BY VSL_NM,IN_VOY_NBR";
				 */

				/*
				 * sql = " SELECT DISTINCT IN_VOY_NBR,VSL_NM,VC.VV_CD" +" FROM VESSEL_CALL VC
				 * LEFT OUTER JOIN VESSEL_DECLARANT VD ON (VD.VV_CD = VC.VV_CD AND VD.STATUS =
				 * 'A')" +" WHERE VV_STATUS_IND IN ('BR','UB', 'CL') AND (GB_CLOSE_BJ_IND <> 'Y'
				 * or GB_CLOSE_BJ_IND IS NULL)" +" AND (VD.CUST_CD = '" +cocode + "' OR
				 * VC.CREATE_CUST_CD = '" +cocode + "')" +" ORDER BY VSL_NM,IN_VOY_NBR";
				 */
				// -- 19.10.2009 Changed by vietnd02 for GB CR
				// FPT 25/3/2010 - Chnage VV_STATUS_IND
				sb.append(" SELECT DISTINCT IN_VOY_NBR,VSL_NM,VC.VV_CD ");
				sb.append(" FROM VESSEL_CALL VC LEFT OUTER JOIN VESSEL_DECLARANT VD ON ");
				sb.append(" (VD.VV_CD = VC.VV_CD AND VD.STATUS = 'A') ");
				sb.append(" WHERE VV_STATUS_IND IN ('AP','AL','BR','UB') AND ");
				sb.append(" (GB_CLOSE_BJ_IND <> 'Y' or GB_CLOSE_BJ_IND IS NULL) ");
				sb.append(" AND (VD.CUST_CD =:cocode OR VC.CREATE_CUST_CD =:cocode) ");
				sb.append(" ORDER BY VSL_NM,IN_VOY_NBR ");
			}

			log.info(" ***getVesselVoy SQL *****" + sb.toString());

			if (!cocode.equals("JP"))
				paramMap.put("cocode", cocode);
			
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			while (rs.next()) {
				voynbr = CommonUtility.deNull(rs.getString("IN_VOY_NBR"));
				vslName = CommonUtility.deNull(rs.getString("VSL_NM"));
				VV_CD = CommonUtility.deNull(rs.getString("VV_CD"));

				VesselVoyValueObject vvvObj = new VesselVoyValueObject();
				vvvObj.setVoyNo(voynbr);
				vvvObj.setVslName(vslName);
				vvvObj.setVarNbr(VV_CD);
				voyList.add(vvvObj);
			}
			log.info("voyList = "+ voyList.size());
		} catch (NullPointerException e) {
			log.info("Exception getVesselVoy : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVesselVoy : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselVoy  DAO  END");
		}
		return voyList;
	} // end of get vsl list

	@Override
	public List<Dept> listCompany(String keyword, Integer start, Integer limit, String type) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		List<Dept> listCompany = new ArrayList<Dept>();
		try {
			log.info("START: listCompany keyword:" + keyword + "start:" + start + "limit:" + limit);
			sb.append(" SELECT co_cd, co_nm || ' (' || co_cd || ')' as coNm ");
			if (type.equals("amend")) {
				sb.append(" FROM tops.company_code WHERE co_cd LIKE :company");
			} else {
				sb.append(" FROM tops.company_code WHERE co_nm LIKE :company");
			}
			sb.append(" AND rec_status='A' AND allow_jponline='Y' ");
			paramMap.put("company", "%" + keyword.toUpperCase() + "%");
			
			log.info("SQL: " + sb.toString());
			log.info("paramMap: " + paramMap.toString());
			listCompany = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<Dept>(Dept.class));
		
			log.info("END: *** listCompany Result *****" + listCompany.size());
		} catch (Exception e) {
			log.info("Exception listCompany : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO listCompany");
		}
		return listCompany;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getContainerNos()
	/**
	 * Returns the list of container numbers for the var number passed.
	 * 
	 * @param vvcode Var Number of the Vessel.
	 * @return ArrayList Returns an ArrayList of containetnumber and container
	 *         sequence number as cntr_nbr::cntr_seq_nbr.
	 * @throws BusinessException
	 * 
	 */
	@Override
	public List<String> getContainerNos(String vvcode) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<String> cntrnos = new ArrayList<String>();
		

		try {
			log.info("START: getContainerNos  DAO  Start vvcode:" + vvcode);

			// sql="select distinct cntr_nbr||'::'||bl_nbr cntr_nbr from
			// bl_cntr_details cntr,manifest_details mft where cntr_nbr <> 'null'
			// and cntr.mft_seq_nbr=mft.mft_seq_nbr and var_nbr='"+ vvcode
			// +"'";//Vani,30th June, 03
			/*
			 * sql = "select distinct bl_cntr_details.cntr_nbr||'::'||bl_nbr cntr_nbr from
			 * manifest_details, bl_cntr_details, cntr where bl_cntr_details.cntr_nbr <>
			 * 'null' and "+ "manifest_details.mft_seq_nbr = bl_cntr_details.mft_seq_nbr and
			 * manifest_details.cntr_type = 'L' and cntr.status = 'L' and
			 * var_nbr='"+vvcode+"' ";
			 */
			// 3rd Sep,03 HKM qry -- start
			sb.append(" select cntr_nbr||'::'||cntr_seq_nbr cntr_nbr from cntr ");
			sb.append(" where ((disc_vv_cd =:vvcode and ");
			sb.append(" purp_cd in ('IM', 'TS','RE')) or (disc_vv_cd is null ");
			sb.append(" and load_vv_cd is null and ");
			sb.append(" purp_cd = 'ST')) and status = 'L' and txn_status = 'A' ");
			// sql += " union select distinct cntr_nbr||'::'||cntr_seq_bl_no as
			// cntr_nbr from cc_unstuff_manifest where active_status='A' and
			// var_nbr='" +
			// vvcode + "'";
			sb.append(" union select distinct mft.cntr_nbr||'::'||cntr_seq_bl_no ");
			sb.append(" as cntr_nbr from cc_unstuff_manifest mft,cntr cntr where ");
			sb.append(" txn_status <> 'D' and cntr_seq_nbr=cntr_seq_bl_no and ");
			sb.append(" active_status='A' and var_nbr=:vvcode ");

			// 3rd Sep,03 HKM qry -- end

			log.info(" ***getContainerNos SQL *****" + sb.toString());

			paramMap.put("vvcode", vvcode);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				String contno = CommonUtility.deNull(rs.getString("cntr_nbr"));
				log.info("cont no in bean " + contno);
				cntrnos.add(contno);
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

	}// end of get vsl list

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getWaiveStatus()
	/**
	 * This method Returns status of unstuff Waive.
	 * 
	 * @param varnbr     represents Var Number of the Vessel.
	 * @param cntrnbr    represents Container Number.
	 * @param cntrseqnbr represents Container Sequence Number.
	 * @return String represents Waive status.
	 * @throws BusinessException
	 * 
	 */
	@Override
	public String getWaiveStatus(String varnbr, String cntrnbr, String cntrseqnbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String closedttm = "";

		try {
			log.info("START: getWaiveStatus  DAO  Start varnbr:" + varnbr + "cntrnbr:" + cntrnbr + "cntrseqnbr:"
					+ cntrseqnbr);

			sb.append(" select waive_unstuff waivests from cc_unstuff_manifest ");
			sb.append(" where active_status='A' and cntr_seq_bl_no=:cntrseqnbr ");
			sb.append(" and cntr_nbr=:cntrnbr and var_nbr=:varnbr ");

			log.info(" ***getWaiveStatus SQL *****" + sb.toString());

			paramMap.put("cntrseqnbr", cntrseqnbr);
			paramMap.put("cntrnbr", cntrnbr);
			paramMap.put("varnbr", varnbr);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				closedttm = CommonUtility.deNull(rs.getString("waivests"));
			}
			log.info("END: *** getWaiveStatus Result *****" + CommonUtility.deNull(closedttm));
		} catch (NullPointerException e) {
			log.info("Exception getWaiveStatus : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getWaiveStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getWaiveStatus  DAO  END");
		}
		return closedttm;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->checkUnStuffClosed()
	/**
	 * This method checks whether Unstuffed is closed or not and returns a String
	 * value.
	 * 
	 * @param cntrno    represents Container Number.
	 * @param cntrseqno represents Container Sequence Number.
	 * @param varno     represents Var Number of the Vessel.
	 * @return String value either "Closed" or "Open".
	 * @throws BusinessException
	 * 
	 */
	@Override
	public String checkUnStuffClosed(String cntrno, String cntrseqno, String varno) throws BusinessException {
		boolean closed = false;
		try {
			log.info("START: checkUnStuffClosed  DAO  Start cntrno:" + cntrno + "cntrseqno:" + cntrseqno + "varno:"
					+ varno);

			closed = chkUnStfClosed(cntrno, cntrseqno, varno);
			log.info("END: *** checkUnStuffClosed Result *****" + closed);
		} catch (NullPointerException e) {
			log.info("Exception checkUnStuffClosed : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception checkUnStuffClosed : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception checkUnStuffClosed : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkUnStuffClosed  DAO  END");
		}
		if (closed)
			return "Closed";
		else
			return "Open";
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->chkUnStfClosed()
	/**
	 * This method checks whether Unstuffed is closed or not and returns a boolean
	 * value.
	 * 
	 * @param cntrno    represents Container Number.
	 * @param cntrseqno represents Container Sequence Number.
	 * @param varno     represents Var Number of the Vessel.
	 * @return boolena value true or false.
	 * @throws BusinessException
	 * 
	 */
	public boolean chkUnStfClosed(String cntrNo, String cntSeqNo, String varNo)
			throws BusinessException {
		boolean bUnSftCls = false;

		String strUnStfClsInd = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: chkUnStfClosed  DAO  Start cntrNo:" + cntrNo + "cntSeqNo:" + cntSeqNo + "varNo:" + varNo);

			sb.append(" select unstuff_closed from cc_unstuff_manifest where ");
			sb.append(" var_nbr =:varNo and cntr_nbr=:cntrNo and ");
			sb.append(" cntr_seq_bl_no =:cntSeqNo ");
			// log.info("000 chkUnStfClosed sQl: " + sql);

			log.info(" ***chkUnStfClosed SQL *****" + sb.toString());

			paramMap.put("varNo", varNo);
			paramMap.put("cntrNo", cntrNo);
			paramMap.put("cntSeqNo", cntSeqNo);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				strUnStfClsInd = (String) rs.getString("unstuff_closed");
				if (strUnStfClsInd.equals("Y")) {
					bUnSftCls = true;
				} else if (strUnStfClsInd.equals("N")) {
					bUnSftCls = false;
				}
			} else {
				bUnSftCls = false;
			}
			log.info("bUnStfCls = " + bUnSftCls);
		} catch (NullPointerException e) {
			log.info("Exception chkUnStfClosed : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkUnStfClosed : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkUnStfClosed  DAO  END");
		}
		return bUnSftCls;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getUnStuffDttm()
	/**
	 * This method Returns Date of unstuff
	 * 
	 * @param varnbr     represents Var Number of the Vessel.
	 * @param cntrnbr    represents Container Number.
	 * @param cntrseqnbr represents Container Sequence Number.
	 * @return String represents unstuff close data time.
	 * @throws BusinessException
	 * 
	 */
	@Override
	public String getUnStuffDttm(String varnbr, String cntrnbr, String cntrseqnbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String closedttm = "";

		try {
			log.info("START: getUnStuffDttm  DAO  Start varnbr:" + varnbr + "cntrnbr:" + cntrnbr + "cntrseqnbr:"
					+ cntrseqnbr);

			sb.append(" select to_char(dttm_unstuff,'ddmmyyyy hh24mi') closedttm ");
			sb.append(" from cc_unstuff_manifest where active_status='A' and ");
			sb.append(" cntr_seq_bl_no=:cntrseqnbr and cntr_nbr=:cntrnbr ");
			sb.append(" and var_nbr=:varnbr ");

			log.info(" ***getUnStuffDttm SQL *****" + sb.toString());

			paramMap.put("cntrseqnbr", cntrseqnbr);
			paramMap.put("cntrnbr", cntrnbr);
			paramMap.put("varnbr", varnbr);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				closedttm = CommonUtility.deNull(rs.getString("closedttm"));
			}
			log.info("closedttm = "+ CommonUtility.deNull(closedttm));
		} catch (NullPointerException e) {
			log.info("Exception getUnStuffDttm : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getUnStuffDttm : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getUnStuffDttm  DAO  END");
		}
		return closedttm;

	}

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getManifestList()
	/**
	 * Returns the list of Manifest Details.
	 * 
	 * @param vvcode Var Number of the Vessel.
	 * @param cntrno Container Number.
	 * @return ArrayList a list of UnStuffingValueObject objects.
	 * @throws BusinessException
	 * 
	 */
	@Override
	public TableResult getManifestList(String vvcode, String cntrno, Criteria criteria) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		StringTokenizer token = new StringTokenizer(cntrno, "::");
		String cntrNbr = token.nextToken();
		String seqBlNbr = token.nextToken();

		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		String sql = "";

		try {
			log.info("START: getManifestList  DAO  Start vvcode:" + vvcode + "cntrno:" + cntrno + "criteria: "+ criteria.toString());

			sb.append(" SELECT MFT_SEQ_NBR,CRG_DES,BL_NBR,GROSS_WT,GROSS_VOL,EDO_NBR_PKGS, ");
			sb.append(" HS_CODE, HS_SUB_CODE_FR, HS_SUB_CODE_TO FROM MANIFEST_DETAILS WHERE ");
			sb.append(" VAR_NBR=:vvcode AND BL_STATUS='A' AND UNSTUFF_SEQ_NBR = (SELECT ");
			sb.append(" UNSTUFF_SEQ_NBR FROM CC_UNSTUFF_MANIFEST WHERE ACTIVE_STATUS='A' ");
			sb.append(" AND CNTR_NBR=:cntrNbr AND VAR_NBR=:vvcode ");
			sb.append(" and CNTR_SEQ_BL_NO=:seqBlNbr ) ORDER BY BL_NBR ");
			sql = sb.toString();
			/*
			 * sql = "SELECT MFT_SEQ_NBR,CRG_DES,BL_NBR,GROSS_VOL,EDO_NBR_PKGS FROM "+
			 * "CC_MANIFEST_DETAILS WHERE BL_STATUS='A' AND CNTR_NBR='"+cntrNum+"' AND
			 * VAR_NBR='"+vvcode+"' "+ "ORDER BY BL_NBR";
			 */
			// log.info("\nFROM EJB unstuffing list qry.... " + sql);
			paramMap.put("vvcode", vvcode);
			paramMap.put("cntrNbr", cntrNbr);
			paramMap.put("seqBlNbr", seqBlNbr);
			
			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
			}

			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());

			}

			log.info(" ***getManifestList SQL *****" + sb.toString());
			log.info("paramMap: " + paramMap.toString());
			

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				// log.info("88888 INSIDE WHILE");
				UnStuffingValueObject vo = new UnStuffingValueObject();
				vo.setCntrNbr(cntrNbr);
				vo.setSeqNo(CommonUtility.deNull(rs.getString("MFT_SEQ_NBR")));
				vo.setCrgDesc(CommonUtility.deNull(rs.getString("CRG_DES")));
				vo.setBlNo(CommonUtility.deNull(rs.getString("BL_NBR")));
				// mvObj.setCntrSize("20");
				vo.setGrossVol(CommonUtility.deNull(rs.getString("GROSS_VOL")));
				vo.setGrWt(CommonUtility.deNull(rs.getString("GROSS_WT")));
				vo.setHsCode(CommonUtility.deNull(rs.getString("HS_CODE")));
				vo.setHsCodeFr(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				vo.setHsCodeTo(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));
				if (rs.getInt("EDO_NBR_PKGS") == 0) {
					vo.setEdostat("N");
				} else {
					vo.setEdostat("Y");
				}
				// mvObj.setCntrSize("20");--4thSep,03 Vani
				/*
				 * sql = "select cntr_size from manifest_details where "+
				 * "var_nbr='"+vvcode+"' and bl_nbr = '"+seq_bl_no+"'"+ "and bl_status = 'A' and
				 * cntr_type = 'L'"; ResultSet rs1 = sqlstmt.executeQuery(sql);
				 * while(rs1.next()) {
				 * mvObj.setCntrSize(CommonUtility.deNull(rs1.getString("cntr_size")));; }
				 * rs1.close();
				 */

				topsModel.put(vo);
			}
			
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
			log.info("END: *** getManifestList Result *****" + topsModel.getSize());
		} catch (NullPointerException e) {
			log.info("Exception getManifestList : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getManifestList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getManifestList  DAO  END");
		}
		return tableResult;
	} // end of getManifestList method

	// test autocomplete by nasir
	@Override
	public List<VesselSearchResponse> getVesselsNameBySearch(String vesselName, String cocode) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		List<VesselSearchResponse> vesselList = new ArrayList<>();

		try {
			log.info("*** START: getVesselsNameBySearch dao start  params ***" + vesselName);
			if (cocode.equals("JP")) {
				sb.append(
						" SELECT IN_VOY_NBR AS vesselVoy,VSL_NM AS vesselName,VV_CD AS vesselCode,(VSL_NM||' - '||IN_VOY_NBR) AS displayName FROM VESSEL_CALL WHERE (VV_STATUS_IND IN ");
				sb.append(" ('AP','AL','BR','UB') OR (TERMINAL='CT' AND VV_STATUS_IND = 'CL')) AND ");
				sb.append(
						" (GB_CLOSE_BJ_IND <> 'Y' or GB_CLOSE_BJ_IND IS NULL) AND VSL_NM LIKE:vesselName ORDER BY VSL_NM,IN_VOY_NBR ");
			} else {
				sb.append(" SELECT DISTINCT IN_VOY_NBR AS vesselVoy, VSL_NM AS vesselName, VC.VV_CD AS vesselCode, ");
				sb.append(" (VSL_NM || ' - ' || IN_VOY_NBR) AS displayName ");
				sb.append(" FROM VESSEL_CALL VC LEFT OUTER JOIN VESSEL_DECLARANT VD ON ");
				sb.append(" (VD.VV_CD = VC.VV_CD AND VD.STATUS = 'A') ");
				sb.append(" WHERE VV_STATUS_IND IN ('AP','AL','BR','UB') AND ");
				sb.append(" (GB_CLOSE_BJ_IND <> 'Y' or GB_CLOSE_BJ_IND IS NULL) ");
				sb.append(" AND (VD.CUST_CD =:cocode OR VC.CREATE_CUST_CD =:cocode) ");
				sb.append(" AND VSL_NM LIKE :vesselName ORDER BY VSL_NM,IN_VOY_NBR ");
			}
			if (!cocode.equals("JP")) {
				paramMap.put("cocode", cocode);
			}
			paramMap.put("vesselName", "%" + vesselName + "%");
			
			log.info("*** getVesselsNameBySearch SQL   ***" + sb.toString());
			log.info("paramMap: " + paramMap.toString());
			vesselList = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<VesselSearchResponse>(VesselSearchResponse.class));
			log.info("*** getVesselsNameBySearch dao result   ***" + vesselList.toString());

		
		} catch (Exception e) {
			log.info("Exception getVesselsNameBySearch : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("***END: getVesselsNameBySearch dao end   ***");
		}

		return vesselList;
	} // end of get vsl list

	// Added by Nasir on 29/04/2021
	@Override
	public List<HSCode> getHSSubCodeList(String hsCd) throws BusinessException {

		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<HSCode> hsCodeLs = new ArrayList<HSCode>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * ");
		sql.append(" FROM HS_SUB_CODE ");
		sql.append(" WHERE REC_STATUS = '1' AND HS_CODE =:hsCd");
		sql.append(" ORDER BY HS_SUB_CODE_FR ");
		try {
			log.info("START DAO getHSSubCodeList: hsCd:" + hsCd);
			paramMap.put("hsCd", hsCd);
			log.info("SQL: " + sql.toString());
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				HSCode hs = new HSCode();
				hs.setHsCode(CommonUtility.deNull(rs.getString("HS_CODE")));
				hs.setHsSubCodeFr(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				hs.setHsSubCodeTo(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));
				hs.setHsSubDesc(CommonUtility.deNull(rs.getString("HS_SUB_DESC")));
				hsCodeLs.add(hs);
			}

			log.info("END: *** getHSSubCodeList Result *****" + hsCodeLs.size());
		} catch (Exception e) {
			log.info("Exception getHSSubCodeList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getHSSubCodeList ");
		}
		return hsCodeLs;
	}

	@Override
	public List<ManifestValueObject> getPkgList() throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<ManifestValueObject> pkgList = new ArrayList<ManifestValueObject>();
		String sql = "";
		sql = "SELECT PKG_TYPE_CD,PKG_DESC FROM PKG_TYPES WHERE REC_STATUS='A' ORDER BY PKG_DESC"; // addded
		try {
			log.info("START : getPkgList DAO START");

			log.info("SQL: " + sql.toString());
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				ManifestValueObject mvObj = new ManifestValueObject();
				mvObj.setPkgType(CommonUtility.deNull(rs.getString("PKG_TYPE_CD")));
				mvObj.setPkgn(CommonUtility.deNull(rs.getString("PKG_DESC")));
				pkgList.add(mvObj);
			}
			log.info("END: *** getPkgList Result *****" + pkgList.size());
		} catch (Exception e) {
			log.info("Exception getPkgList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:getPkgList DAO");
		}
		return pkgList;
	}

	public List<EsnListValueObject> getPkgList(String text) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<EsnListValueObject> pkgsList = new ArrayList<EsnListValueObject>();
		String sql = "";
		String pkgsText = text;
		if (pkgsText.equals("ALL")) {

			// Changed by Linus on 8 Oct 2003
			sql = "select * from PKG_TYPES WHERE REC_STATUS='A' ORDER BY PKG_TYPE_CD ";
		}else {
			sql = "select * from PKG_TYPES WHERE PKG_TYPE_CD LIKE :pkgsText  AND REC_STATUS='A' ORDER BY PKG_TYPE_CD";
		}
		// Before
		/*
		 * sql = "select * from PKG_TYPES ORDER BY PKG_TYPE_CD"; else sql =
		 * "select * from PKG_TYPES WHERE PKG_TYPE_CD LIKE'"
		 * +pkgsText+"%' ORDER BY PKG_TYPE_CD";
		 */
		// End Change
		EsnListValueObject esnListValueObject = null;
		try {
			log.info("START : getPkgList DAO START");

			paramMap.put("pkgsText", pkgsText + "%");
			log.info("SQL: " + sql.toString());
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				esnListValueObject = new EsnListValueObject();
				esnListValueObject.setPkgDesc(CommonUtility.deNull(rs.getString("PKG_DESC")));
				esnListValueObject.setPkgType(CommonUtility.deNull(rs.getString("PKG_TYPE_CD")));
				pkgsList.add(esnListValueObject);
			}
			log.info("END: *** getPkgList Result *****" + pkgsList.size());
		} catch (Exception e) {
			log.info("Exception getPkgList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:getPkgList DAO");
		}
		return pkgsList;
	}

	@Override
	public TableResult getPortList(Criteria criteria) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		String sql = "";
		sql = "SELECT PORT_CD, PORT_NM FROM UN_PORT_CODE ORDER BY PORT_NM";
		try {
			log.info("START : getPortList DAO START");
			
			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
			}

			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());

			}
			log.info("SQL: " + sql.toString());
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				ManifestValueObject mvObj = new ManifestValueObject();
				mvObj.setPortL(CommonUtility.deNull(rs.getString("PORT_CD")));
				mvObj.setPortLn(CommonUtility.deNull(rs.getString("PORT_NM")));
				topsModel.put(mvObj);
			}
			
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
			
			log.info("END: *** getPortList Result *****" + topsModel.getSize());
		} catch (Exception e) {
			log.info("Exception getPortList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:getPortList DAO");
		}
		return tableResult;
	}

	@Override
	public TableResult getPortList(String portCd, String portName, Criteria criteria) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		String sql = "";
		sql = "SELECT PORT_CD, PORT_NM FROM UN_PORT_CODE WHERE PORT_CD LIKE :portCd  AND PORT_NM LIKE:portName  ORDER BY PORT_NM";
		try {
			log.info("START : getPortList DAO START");
			
			paramMap.put("portCd", portCd + "%");
			paramMap.put("portName", portName + "%");
			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
			}

			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());

			}
			log.info("SQL: " + sql.toString());
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				ManifestValueObject mvObj = new ManifestValueObject();
				mvObj.setPortL(CommonUtility.deNull(rs.getString("PORT_CD")));
				mvObj.setPortLn(CommonUtility.deNull(rs.getString("PORT_NM")));
				topsModel.put(mvObj);
			}
			
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);

	
		} catch (Exception e) {
			log.info("Exception getPortList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:getPortList DAO");
		}
		return tableResult;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->MftInsertion()
	/**
	 * This method creats bl record and returns a String of value mftseqNo-blNo.
	 * 
	 * @param addval       represents Added Value
	 * @param coCd         represents Company Code
	 * @param varno        represents Var Number of the Vessel
	 * @param blno         represents Bills-of-Lading No.
	 * @param cntrno_seqno Container Sequence Number related to container number.
	 * @param dttm         represents Date and Time Of UnStuffing.
	 * @param waivechrg    represents Waive UnStuffing Charge.
	 * @param crgtype      represents Cargo Type.
	 * @param hscd         represents HS Code.
	 * @param crgdesc      represents Cargo Description.
	 * @param mark         represents Cargo Marking.
	 * @param nbrpkgs      represents Number of packages.
	 * @param gwt          represents Gross Weight.
	 * @param gvol         represents Gross Measurement.
	 * @param crgstatus    represents Cargo Status.
	 * @param dgind        represents DG Indicator.
	 * @param billparty    represents Billable Party.
	 * @param consNm       represents Consignee Name.
	 * @param stgind       represents Storage Indicator.
	 * @param pol          represents Port of Loading.
	 * @param pod          represents Port of Discharge.
	 * @param pkgtype      represents Packaging Type.
	 * @param pofd         represents Port of Final Destination.
	 * @return String a value of Manifest Sequence Number.
	 * @throws BusinessException
	 */
	@Override
	public String MftInsertion(String addval, String coCd, String varno, String blno, String cntrno_sqno, String dttm,
			String waivechrg, String crgtype, String hscd, String hscdFr, String hscdTo, String crgdesc, String mark,
			String nbrpkgs, String gwt, String gvol, String crgstatus, String dgind, String billparty, String consNM,
			String consCoCd, String stgind, String pol, String pod, // Added by Satish on 20th Feb, 2004
			String pkgtype, String pofd) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();

		StringTokenizer cntrtkn = new StringTokenizer(cntrno_sqno, "::");
		String cntrno = cntrtkn.nextToken();
		String seq_bl_no = cntrtkn.nextToken();

		String masterseqno = "";

		String streturn = "";

		

		try {
			log.info("START: MftInsertion  DAO  Start addval:" + addval + "coCd:" + coCd + "varno:" + varno + "blno:"
					+ blno + "cntrno_sqno:" + cntrno_sqno + "dttm:" + dttm + "waivechrg:" + waivechrg + "crgtype:"
					+ crgtype + "hscd:" + hscd + "hscdFr:" + hscdFr + "hscdTo:" + hscdTo + "crgdesc:" + crgdesc
					+ "mark:" + mark + "nbrpkgs:" + nbrpkgs + "gwt:" + gwt + "gvol:" + gvol + "crgstatus:" + crgstatus
					+ "dgind:" + dgind + "billparty:" + billparty + "consNM:" + consNM + "consCoCd:" + consCoCd
					+ "stgind:" + stgind + "pol:" + pol + "pod:" + pod + "pkgtype:" + pkgtype + "pofd:" + pofd);
			

			boolean unstfCls = chkUnStfClosed(cntrno, seq_bl_no, varno);
			// log.info("\nFROM EJB unstfCls == " + unstfCls);
			if (unstfCls) {
				log.info("Writing from UnstuffingEJB.MftInsertion");
				log.info("UnStuffing Closed");
				throw new BusinessException("M41170");
			}

			boolean chkBlNo = chkBlNo(blno, varno);
			// log.info("\nchkBlNo == " + chkBlNo);
			if (chkBlNo) {
				log.info("Writing from UnStuffingEJB.MftInsertion");
				log.info("Invalid Bl No " + blno);
				throw new BusinessException("M20201");
			}

			if (!chkPkgtype(pkgtype)) {
				log.info("Writing from UnStuffingEJB.MftInsertion");
				log.info("Invalid Packaging type " + pkgtype);
				throw new BusinessException("M21604");
			}

			boolean portcdl = chkPortCode(pol);
			if (!portcdl) {
				log.info("Writing from UnStuffingEJB.MftInsertion");
				log.info("Invalid Port Code " + pol);
				throw new BusinessException("M21601");
			}

			boolean portcdd = chkPortCode(pod);
			if (!portcdd) {
				log.info("Writing from UnStuffingEJB.MftInsertion");
				log.info("Invalid Port Code " + pod);
				throw new BusinessException("M21602");
			}

			if (pofd != null && !pofd.equals("")) {
				if (!chkPortCode(pofd)) {
					log.info("Writing from UnStuffingEJB.MftInsertion");
					log.info("Invalid Final Destination Port Code " + pofd);
					throw new BusinessException("M21603");
				} // end of if !port..
			} // end of if poFD

			// log.info("111111 from EJB b4 chkMasterData");
			masterseqno = chkMasterData(varno, cntrno, seq_bl_no); // cntrno);
			// log.info("111111 from EJB a4 chkMasterData" + masterseqno);

			String unstfSeqNo = masterseqno.substring(1);
			// log.info("11112222 from EJB unstfSeqNo" + unstfSeqNo);
			if (masterseqno.substring(0, 1).equals("N")) {
				sb.append(" insert into cc_unstuff_manifest (unstuff_seq_nbr,var_nbr, ");
				sb.append(" cntr_nbr,dttm_unstuff,waive_unstuff,cntr_seq_bl_no, ");
				sb.append(" CREATE_USER_ID,CREATE_DTTM,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
				sb.append(" values (:unstfSeqNo,:varno,:cntrno ");
				if (dttm == null || dttm.equals("null") || dttm.equals("")) {
					sb.append(" ,:dttm,:waivechrg ");
				} else {
					sb.append(" ,to_date(:dttm,'DDMMYYYY HH24MI'),:waivechrg ");
				}
				sb.append(" , :seqBlNo, :coCd,sysdate, :coCd,sysdate) ");
			}

			// log.info("22222 insert cc_unstuff_manifest qry......." + sb);
			String mftSeqNbr = generateMftSeqNum();
			sb1.append(" insert into manifest_details (mft_seq_nbr,var_nbr,bl_nbr ");
			sb1.append(" ,BL_STATUS,crg_type,crg_des,crg_status,pkg_type,nbr_pkgs, ");
			sb1.append(" EDO_NBR_PKGS,gross_wt,gross_vol,dg_ind,hs_code,hs_sub_code_fr, ");
			sb1.append(" hs_sub_code_to,des_port,cons_nm,cons_co_cd,LAST_MODIFY_USER_ID, ");
			sb1.append(" LAST_MODIFY_DTTM,UNSTUFF_SEQ_NBR,STG_TYPE,LD_PORT,DIS_PORT) ");
			sb1.append(" values(:mftSeqNbr,:varno,:blno,'A',:crgtype,:crgdesc, ");
			sb1.append(" :crgstatus,:pkgtype,:nbrpkgs,0,:gwt, ");
			sb1.append(" :gvol,:dgind,:hscd,:hscdFr,:hscdTo,:pofd, ");
			sb1.append(" :consNM,:consCoCd,:coCd,sysdate,:unstfSeqNo, ");
			sb1.append(" :stgind,:pol,:pod ) ");

			// log.info("333333 insert manifest_details qry......." + sb1);

			// Transaction Log Table Insertion 23/5/2002
			sb2.append(" INSERT INTO MFT_MARKINGS(MFT_SQ_NBR,MFT_MARKINGS, ");
			sb2.append(" LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) VALUES(:mftSeqNbr, ");
			sb2.append(" :mark, :coCd,sysdate) ");
			// log.info("44444444 insert MFT_MARKINGS qry......." + sb2);

			int cnt_unstf_mft = 0;
			if (masterseqno.substring(0, 1).equals("N")) {
				log.info(" ***MftInsertion SQL *****" + sb.toString());
				paramMap.put("unstfSeqNo", unstfSeqNo);
				paramMap.put("varno", varno);
				paramMap.put("cntrno", cntrno);
				if (dttm == null || dttm.equals("null") || dttm.equals("")) {
					paramMap.put("dttm", null);
					paramMap.put("waivechrg", waivechrg);
				} else {
					paramMap.put("dttm", dttm);
					paramMap.put("waivechrg", waivechrg);
				}

				paramMap.put("seqBlNo", seq_bl_no);
				paramMap.put("coCd", coCd);
				cnt_unstf_mft = namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			}

			log.info(" ***MftInsertion SQL *****" + sb1.toString());

			paramMap.put("mftSeqNbr", mftSeqNbr);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			paramMap.put("crgtype", crgtype);
			paramMap.put("crgdesc", crgdesc);
			paramMap.put("crgstatus", crgstatus);
			paramMap.put("pkgtype", pkgtype);
			paramMap.put("nbrpkgs", nbrpkgs);
			paramMap.put("gwt", gwt);
			paramMap.put("gvol", gvol);
			paramMap.put("dgind", dgind);
			paramMap.put("hscd", hscd);
			paramMap.put("hscdFr", hscdFr);
			paramMap.put("hscdTo", hscdTo);
			paramMap.put("pofd", CommonUtility.deNull(pofd));
			paramMap.put("consNM", consNM);
			paramMap.put("consCoCd", consCoCd);
			paramMap.put("coCd", coCd);
			paramMap.put("unstfSeqNo", unstfSeqNo);
			paramMap.put("stgind", stgind);
			paramMap.put("pol", pol);
			paramMap.put("pod", pod);

			int count = namedParameterJdbcTemplate.update(sb1.toString(), paramMap);

			log.info(" ***MftInsertion SQL *****" + sb2.toString());
			log.info("paramMap: " + paramMap.toString());
			paramMap.put("mftSeqNbr", mftSeqNbr);
			paramMap.put("mark", GbmsCommonUtility.addApostr(mark));
			paramMap.put("coCd", coCd);

			int cntmark = namedParameterJdbcTemplate.update(sb2.toString(), paramMap);


			// Transaction Log Table Insertion 23/5/2002
			/*
			 * if (logStatusGlobal.equalsIgnoreCase("Y")) { count_trans =
			 * sqlstmt.executeUpdate(strInsert_trans); cnt_mark_trans =
			 * sqlstmt.executeUpdate(strMark_trans); }
			 */

			if (masterseqno.substring(0, 1).equals("N")) {
				if (count == 0 || cntmark == 0 || cnt_unstf_mft == 0) {
					// //sessionContext.setRollbackOnly();
					log.info("Writing from UnStuffingEJB.MftInsertion");
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}
			} else if (count == 0 || cntmark == 0) {
				// //sessionContext.setRollbackOnly();
				log.info("Writing from UnStuffingEJB.MftInsertion");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}

			streturn = mftSeqNbr + "-" + blno;
			log.info("streturn = "+ CommonUtility.deNull(streturn));
			/*
			 * if (logStatusGlobal.equalsIgnoreCase("Y")) { //Transaction Log Table
			 * Insertion 23/5/2002 if (count_trans == 0 || cnt_mark_trans == 0) {
			 * //sessionContext.setRollbackOnly(); log.info("Writing from
			 * UnStuffingEJB.MftInsertion"); log.info("Record Cannot be added to Database");
			 * throw new BusinessException("M4201"); } }
			 */
		} catch (NullPointerException e) {
			log.info("Exception MftInsertion : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception MftInsertion : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception MftInsertion : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: MftInsertion  DAO  END");
		}
		return streturn;
	} // end of get MftInsertion

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->generateMftSeqNum()
	/**
	 * This method generates manifest sequence number
	 * 
	 * @return String represents manifest sequence number
	 * @throws BusinessException
	 */
	public String generateMftSeqNum() throws BusinessException {
		String mftSeqNbr = null;
		int count = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: generateMftSeqNum  DAO  Start ");

			sb.append(" select count(mft_seq_nbr) cnt from manifest_details ");

			log.info(" ***generateMftSeqNum SQL *****" + sb.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				count = rs.getInt("cnt");
			}
			if (count == 0) {
				mftSeqNbr = "1";
			} else {

				sb = new StringBuilder();
				sb.append(" select max(to_number(mft_seq_nbr))+1 mft_seq_nbr ");
				sb.append(" from manifest_details ");

				log.info(" ***generateMftSeqNum SQL *****" + sb.toString());
				log.info("paramMap: " + paramMap.toString());
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

				if (rs1.next()) {
					mftSeqNbr = rs1.getString("mft_seq_nbr");
				}
			}
			log.info("mftSeqNbr = " + CommonUtility.deNull(mftSeqNbr));
		} catch (NullPointerException e) {
			log.info("Exception generateMftSeqNum : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception generateMftSeqNum : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: generateMftSeqNum  DAO  END");
		}
		return mftSeqNbr;
	} // end of generateMftSeqNum method

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->chkMasterData()
	/**
	 * This method generates unstuff sequence number
	 * 
	 * @param varno      represents Var Number of the Vessel.
	 * @param cntrno     represents Container Number.
	 * @param seq_bl_nbr Sequence BL Number.
	 * @return String represents unstuff sequence number
	 * @throws BusinessException
	 */
	public String chkMasterData(String varno, String cntrno, String seq_bl_nbr) throws BusinessException {
		String seq_nbr = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: chkMasterData  DAO  Start varno:" + varno + "cntrno:" + cntrno + "seq_bl_nbr:"
					+ seq_bl_nbr);

			sb.append(" select unstuff_seq_nbr seq_nbr from cc_unstuff_manifest ");
			sb.append(" where active_status='A' and cntr_seq_bl_no=:seqBlNbr ");
			sb.append(" and cntr_nbr=:cntrno and var_nbr=:varno ");

			log.info(" ***chkMasterData SQL *****" + sb.toString());

			paramMap.put("varno", varno);
			paramMap.put("cntrno", cntrno);
			paramMap.put("seqBlNbr", seq_bl_nbr);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {

				seq_nbr = "Y" + rs.getString("seq_nbr");
			} else {
				int count = 0;
				sb = new StringBuilder();
				sb.append(" select count(unstuff_seq_nbr) cnt from cc_unstuff_manifest ");

				log.info(" ***chkMasterData SQL *****" + sb.toString());
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				if (rs1.next()) {
					count = rs1.getInt("cnt");
				}
				if (count == 0) {
					seq_nbr = "N" + "1";
				} else {
					sb = new StringBuilder();
					sb.append(" select max(unstuff_seq_nbr)+1 seq_nbr from cc_unstuff_manifest ");

					log.info(" ***chkMasterData SQL *****" + sb.toString());
					rs2 = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

					if (rs2.next()) {
						seq_nbr = "N" + rs2.getString("seq_nbr");
					}
				}

			}
			log.info("seq_nbr ="+ CommonUtility.deNull(seq_nbr));
		} catch (NullPointerException e) {
			log.info("Exception chkMasterData : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkMasterData : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkMasterData  DAO  END");
		}
		return seq_nbr;
	} // end of chkDNnbrPkgs method

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->chkBlNo()
	/**
	 * This method checks blnumber is already existing or not.
	 * 
	 * @param con   represents Connection object.
	 * @param blno  represents BL Number.
	 * @param varno represents Vessel Var Number.
	 * @return boolena value true or false.
	 * @throws BusinessException
	 */
	private boolean chkBlNo(String blno, String varno) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		boolean bblno = false;

		try {
			log.info("START: chkBlNo  DAO  Start blno:" + blno + "varno:" + varno);

			sb.append(" SELECT BL_NBR FROM MANIFEST_DETAILS WHERE BL_STATUS='A' ");
			sb.append(" AND BL_NBR=:blno AND VAR_NBR=:varno ");

			log.info(" ***chkBlNo SQL *****" + sb.toString());

			paramMap.put("blno", blno);
			paramMap.put("varno", varno);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				bblno = true;
			} else {
				bblno = false;
			}

		} catch (NullPointerException e) {
			log.info("Exception chkBlNo : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkBlNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkBlNo  DAO  END : " + bblno);
		}
		return bblno;
	} // end of chkBlNo method

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->chkPkgtype()
	/**
	 * This method checks pakage code.
	 * 
	 * @param con   represents Connection object.
	 * @param pkgcd represents Package Code.
	 * @return boolena value true or false.
	 * @throws BusinessException
	 */
	private boolean chkPkgtype(String pkgcd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		boolean bpkgcd = false;

		try {
			log.info("START: chkPkgtype  DAO  Start pkgcd:" + pkgcd);

			sb.append(" SELECT PKG_TYPE_CD FROM PKG_TYPES WHERE PKG_TYPE_CD=:pkgcd ");

			log.info(" ***chkPkgtype SQL *****" + sb.toString());

			paramMap.put("pkgcd", pkgcd);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				bpkgcd = true;
			} else {
				bpkgcd = false;
			}

		} catch (NullPointerException e) {
			log.info("Exception chkPkgtype : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkPkgtype : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkPkgtype  DAO  END : " + bpkgcd);
		}
		return bpkgcd;
	} // end of chkPkgtype method

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->chkPortCode()
	/**
	 * This method checks port code.
	 * 
	 * @param con    represents Connection object.
	 * @param portcd represents Port Code.
	 * @return boolena value true or false.
	 * @throws BusinessException
	 */

	private boolean chkPortCode(String portcd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		boolean bpcd = false;

		try {
			log.info("START: chkPortCode  DAO  Start portcd:" + portcd);

			sb.append(" SELECT PORT_CD FROM UN_PORT_CODE WHERE PORT_CD=:portcd ");

			log.info(" ***chkPortCode SQL *****" + sb.toString());

			paramMap.put("portcd", portcd);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				bpcd = true;
			} else {
				bpcd = false;
			}

		} catch (NullPointerException e) {
			log.info("Exception chkPortCode : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkPortCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkPortCode  DAO  END : " + bpcd);
		}
		return bpcd;
	} // end of CHK PORTCODE method

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getPortName()
	/**
	 * This method returns port name.
	 * 
	 * @param portcd String represents Port Code.
	 * @return String represents Port Name.
	 * @throws BusinessException
	 */
	@Override
	public String getPortName(String portcd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String portnm = "";

		try {
			log.info("START: getPortName  DAO  Start portcd:" + portcd);

			sb.append(" SELECT PORT_NM FROM UN_PORT_CODE WHERE PORT_CD=:portcd ");
			log.info(" ***getPortName SQL *****" + sb.toString());

			paramMap.put("portcd", portcd);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				portnm = rs.getString("PORT_NM");
			}
			log.info("portnm = "+ CommonUtility.deNull(portnm));
		} catch (NullPointerException e) {
			log.info("Exception cancelGbEdoUpd : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception cancelGbEdoUpd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPortName  DAO  END");
		}
		return portnm;
	} // end of getPortName method

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getPkgName()
	/**
	 * This method returns pakage name.
	 * 
	 * @param pkgtype String represents Package Type.
	 * @return String represents Package Name.
	 * @throws BusinessException
	 */
	@Override
	public String getPkgName(String pkgtype) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String pkgname = "";

		try {
			log.info("START: getPkgName  DAO  Start pkgtype:" + pkgtype);

			sb.append(" SELECT PKG_DESC FROM PKG_TYPES WHERE PKG_TYPE_CD=:pkgtype ");
			log.info(" ***getPkgName SQL *****" + sb.toString());

			paramMap.put("pkgtype", pkgtype);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				pkgname = rs.getString("PKG_DESC");
			}
			log.info("pkgname = "+ CommonUtility.deNull(pkgname));
		} catch (NullPointerException e) {
			log.info("Exception getPkgName : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getPkgName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPkgName  DAO  END");
		}
		return pkgname;
	} // end of getPkgName method

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->mftRetrieve()
	/**
	 * This method retieves unstuff manifest value object.
	 * 
	 * @param blno  represents Bills-of-Lading No.
	 * @param varno represents Var Number of the Vessel.
	 * @param seqno represents Master Unstuffing Sequence Number.
	 * @return UnStuffingValueObject
	 * @throws BusinessException
	 */
	@Override
	public UnStuffingValueObject mftRetrieve(String blno, String varno, String seqno) throws BusinessException {
	
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		UnStuffingValueObject vo = new UnStuffingValueObject();

		try {
			log.info("START: mftRetrieve  DAO  Start blno:" + blno + "varno:" + varno + "seqno:" + seqno);

			/*
			 * sql = "select
			 * mft_seq_nbr,bl_nbr,crg_type,crg_des,crg_status,pkg_type,nbr_pkgs," +
			 * "gross_wt,gross_vol,dg_ind,hs_code,des_port,cons_nm " + "from
			 * manifest_details where bl_status='A' and bl_nbr='" + blno + "' and
			 * var_nbr='" + varno + "'" + "and mft_seq_nbr='" + seqno + "'";
			 */
			/*
			 * sql = "select
			 * mft.mft_seq_nbr,mft.bl_nbr,mft.crg_type,mft.crg_des,mft.crg_status,mft.
			 * pkg_type,mft.nbr_pkgs,mft.gross_wt,mft.gross_vol,mft.dg_ind,mft.hs_code,mft.
			 * des_port,mft.cons_nm,nvl(unstf.unstuff_closed,'N') unstfsts, from
			 * manifest_details mft,cc_unstuff_manifest unstf where
			 * unstf.active_status(+)='A' and unstf.unstuff_seq_nbr(+)=mft.unstuff_seq_nbr
			 * and mft.bl_status='A' and mft.bl_nbr='" + blno + "' and
			 * mft.var_nbr='" + varno + "'and
			 * mft.mft_seq_nbr='" + seqno + "'"; log.info("111111111qry from
			 * EJB.mftRetrieve().... " + sql);
			 */
			// Satish
			sb.append(" select mft.mft_seq_nbr,mft.bl_nbr,mft.crg_type,mft.crg_des,mft.crg_status, ");
			sb.append(" mft.pkg_type,mft.nbr_pkgs,mft.gross_wt,mft.gross_vol,mft.dg_ind,mft.hs_code ");
			sb.append(" as hs_code,mft.hs_sub_code_Fr as hs_sub_code_Fr , mft.hs_sub_code_to as ");
			sb.append(" hs_sub_code_to, hsc.hs_sub_desc as hs_sub_desc,mft.des_port, mft.cons_nm, ");
			sb.append(" mft.cons_co_cd, nvl(unstf.unstuff_closed,'N') unstfsts, mft.stg_type, ");
			sb.append(" mft.ld_port, mft.dis_port from manifest_details mft,hs_sub_code hsc, ");
			sb.append(" cc_unstuff_manifest unstf where mft.HS_CODE = hsc.hs_code and ");
			sb.append(" mft.HS_SUB_CODE_FR = hsc.hs_sub_code_fr and mft.HS_SUB_CODE_TO = ");
			sb.append(" hsc.hs_sub_code_to and unstf.active_status(+)='A' and ");
			sb.append(" unstf.unstuff_seq_nbr(+)=mft.unstuff_seq_nbr and mft.bl_status='A' ");
			sb.append(" and mft.bl_nbr=:blno and mft.var_nbr=:varno and mft.mft_seq_nbr= :seqno ");
			// log.info("111111111qry from EJB.mftRetrieve().... " + sql);

			log.info(" ***mftRetrieve SQL *****" + sb.toString());

			paramMap.put("blno", blno);
			paramMap.put("varno", varno);
			paramMap.put("seqno", seqno);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				vo.setCrgn(getCrgNm(rs.getString("CRG_TYPE")));
				vo.setCrgType(CommonUtility.deNull(rs.getString("CRG_TYPE")));

				vo.setCrgDesc(CommonUtility.deNull(rs.getString("CRG_DES")));
				vo.setNoofPkgs(CommonUtility.deNull(rs.getString("NBR_PKGS")));
				vo.setGrWt(CommonUtility.deNull(rs.getString("GROSS_WT")));
				vo.setGrMsmt(CommonUtility.deNull(rs.getString("GROSS_VOL")));
				vo.setCrgStatus(CommonUtility.deNull(rs.getString("CRG_STATUS")));
				vo.setDgInd(CommonUtility.deNull(rs.getString("DG_IND")));
				// mftdispobj.setBillAccNo(CommonUtility.deNull(rs.getString("BILL_PARTY_ACCT_NBR")));
				vo.setPkgn(getPkgName(rs.getString("PKG_TYPE")));
				vo.setPkgType(CommonUtility.deNull(rs.getString("PKG_TYPE")));
				vo.setPortFD(CommonUtility.deNull(rs.getString("DES_PORT")));
				vo.setPortFDn(getPortName(CommonUtility.deNull(rs.getString("DES_PORT"))));
				vo.setSeqNo(CommonUtility.deNull(rs.getString("MFT_SEQ_NBR")));
				vo.setConsigneeNM(CommonUtility.deNull(rs.getString("cons_nm")));

				vo.setConsigneeCoyCode(rs.getString("cons_co_cd"));

				vo.setBillNbr(CommonUtility.deNull(rs.getString("bl_nbr")));
				vo.setUnStuffCloseStatus(CommonUtility.deNull(rs.getString("unstfsts")));
				vo.setCrgn(getCrgNm(rs.getString("CRG_TYPE")));
				vo.setCrgType(CommonUtility.deNull(rs.getString("CRG_TYPE")));
				vo.setHsCode(CommonUtility.deNull(rs.getString("HS_CODE")));
				vo.setHsCodeFr(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				vo.setHsCodeTo(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));
				vo.setHsSubCodeDesc(CommonUtility.deNull(rs.getString("HS_SUB_DESC")));
				// Satish
				vo.setStgInd(CommonUtility.deNull(rs.getString("STG_TYPE")));
				vo.setPortL(CommonUtility.deNull(rs.getString("LD_PORT")));
				vo.setPortD(CommonUtility.deNull(rs.getString("DIS_PORT")));

				// mftdispobj.setBillableParty(CommonUtility.deNull(rs.getString("bill_party_acct_nbr")));
			}

			sb1.append(" select mft_markings from MFT_MARKINGS where MFT_SQ_NBR=:seqno ");

			log.info(" ***mftRetrieve SQL *****" + sb1.toString());

			paramMap.put("seqno", seqno);
			log.info("paramMap: " + paramMap.toString());
			rs1 = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
			if (rs1.next()) {
				vo.setCrgMarking(CommonUtility.deNull(rs1.getString("MFT_MARKINGS")));
			}

		} catch (NullPointerException e) {
			log.info("Exception mftRetrieve : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception mftRetrieve : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception mftRetrieve : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: mftRetrieve  DAO  END");
		}
		return vo;
	} // end of

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getCrgNm()
	/**
	 * This method returns Cargo name.
	 * 
	 * @param pkgtype String represents Cargo Type.
	 * @return String represents Cargo Name.
	 * @throws BusinessException
	 */
	public String getCrgNm(String crgtyp) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String crgnm = "";

		try {
			log.info("START: getCrgNm  DAO  Start crgtyp:" + crgtyp);

			sb.append(" SELECT CRG_TYPE_NM FROM CRG_TYPE WHERE CRG_TYPE_CD=:crgtyp ");
			log.info(" ***getCrgNm SQL *****" + sb.toString());

			paramMap.put("crgtyp", crgtyp);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				crgnm = rs.getString("CRG_TYPE_NM");
			}
			log.info("crgnm = " + CommonUtility.deNull(crgnm));
		} catch (NullPointerException e) {
			log.info("Exception getCrgNm : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCrgNm : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCrgNm  DAO  END");
		}
		return crgnm;
	} // end of getCrgNm method

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getAddcrgList()
	/**
	 * Method to get the cargo details as List of UnStuffingValueObject
	 * 
	 * @return ArrayList of UnStuffingValueObject.
	 * @throws BusinessException
	 */
	@Override
	public List<UnStuffingValueObject> getAddcrgList() throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<UnStuffingValueObject> addcrglist = new ArrayList<UnStuffingValueObject>();

		try {
			log.info("START: getAddcrgList  DAO  Start ");

			sb.append(" SELECT DISTINCT CRG_TYPE_CD,CRG_TYPE_NM FROM CRG_TYPE WHERE ");
			sb.append(" CRG_TYPE_CD NOT IN ('00','01','02','03') "); // VANI 8th Sept,03

			log.info(" ***getAddcrgList SQL *****" + sb.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			String crgCd = "";
			String crgName = "";

			while (rs.next()) {
				crgCd = CommonUtility.deNull(rs.getString("CRG_TYPE_CD"));
				crgName = CommonUtility.deNull(rs.getString("CRG_TYPE_NM"));

				UnStuffingValueObject admftvobj = new UnStuffingValueObject();
				admftvobj.setCrgType(crgCd);
				admftvobj.setCrgDesc(crgName);
				addcrglist.add(admftvobj);
			}

		} catch (NullPointerException e) {
			log.info("Exception getAddcrgList : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getAddcrgList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAddcrgList  DAO  END");
		}
		return addcrglist;
	} // end of get manifestadd lists

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getPorts()
	/**
	 * This method returns ArrayList of Port of Loading and Port of Discharge.
	 * 
	 * @param cntrSeqNbr represents Container Sequence Number related to container
	 *                   number.
	 * @return ArrayList a list of Port of Loading and Port of Discharge.
	 * @throws BusinessException
	 */
	@Override
	public List<String> getPorts(String cntrSeqNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<String> portList = new ArrayList<String>();
		// log.info(" @ UnStuffingEJB::getPorts() cntrseqno =" + cntr_seq_no);
		// log.info(" @ UnStuffingEJB::getPorts() sql =" + sql);

		try {
			log.info("START: getPorts  DAO  Start cntrSeqNbr:" + cntrSeqNbr);

			sb.append(" Select pload, pdisc1 from cntr where cntr_seq_nbr =:cntrSeqNbr ");

			log.info(" ***getPorts SQL *****" + sb.toString());

			paramMap.put("cntrSeqNbr", Integer.parseInt(cntrSeqNbr.trim()));
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs != null && rs.next()) {
				String pload = CommonUtility.deNull(rs.getString("pload"));
				portList.add(pload);
				String pdisc = CommonUtility.deNull(rs.getString("pdisc1"));
				portList.add(pdisc);
			}

		} catch (NullPointerException e) {
			log.info("Exception getPorts : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getPorts : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPorts  DAO  END");
		}
		return portList;
	} // end of getPorts

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->mftCancel()
	/**
	 * This method cancels bl record.
	 * 
	 * @param coCd         represents Company Code.
	 * @param seqno        represents Master Unstuffing Sequence Number.
	 * @param varno        represents Var Number of the Vessel.
	 * @param blno         represents Bills-of-Lading No.
	 * @param cntrno_seqno Container Sequence Number related to container number.
	 * @throws BusinessException
	 */
	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void mftCancel(String coCd, String seqno, String varno, String blno, String cntrno_sqno)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		StringTokenizer cntrtkn = new StringTokenizer(cntrno_sqno, "::");
		String cntrNo = cntrtkn.nextToken();
		String cntrSeqNo = cntrtkn.nextToken();

		try {
			log.info("START: mftCancel  DAO  Start coCd:" + coCd + "seqno:" + seqno + "varno:" + varno + "blno:" + blno
					+ "cntrno_sqno:" + cntrno_sqno);

			boolean unstfCls = chkUnStfClosed(cntrNo, cntrSeqNo, varno);
			log.info("FROM UnStuffingEJB.mftCancel unstfCls == " + unstfCls);
			if (unstfCls) {
				log.info("Writing from UnstuffingEJB.mftCancel");
				log.info("Can not delete,UnStuffing Closed");
				throw new BusinessException("ErrorMsg_Cannot_Delete");
			}

			sb.append(" UPDATE MANIFEST_DETAILS SET BL_STATUS='X',LAST_MODIFY_USER_ID=:coCd ");
			sb.append(" ,LAST_MODIFY_DTTM=sysdate WHERE MFT_SEQ_NBR=:seqno AND ");
			sb.append(" VAR_NBR=:varno AND BL_NBR=:blno ");
			// log.info("CANCEL BL STATUS sql == " + sql);


			log.info(" ***mftCancel SQL *****" + sb.toString());

			paramMap.put("coCd", coCd);
			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("paramMap: " + paramMap.toString());
			int count = namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			if (count == 0) {
				// //sessionContext.setRollbackOnly();
				log.info("Writing from UnStuffingEJB.mftCancel");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("ErrorMsg_Record_Not_Added");
			}
		} catch (NullPointerException e) {
			log.info("Exception mftCancel : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception mftCancel : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception mftCancel : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: mftCancel  DAO  END");
		}
	} // end of mftCancel

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->MftUpdation()
	// Satish
	/**
	 * This method amends bl record and returns a String of value mftseqNo.
	 * 
	 * @param usrid        represents User Id.
	 * @param coCd         represents Company Code.
	 * @param seqno        represents Master Unstuffing Sequence Number.
	 * @param varno        represents Var Number of the Vessel.
	 * @param blno         represents Bills-of-Lading No.
	 * @param crgtyp       represents Cargo Type.
	 * @param hscd         represents HS Code.
	 * @param crgdesc      represents Cargo Description.
	 * @param mark         represents Cargo Marking.
	 * @param nopkgs       represents Number of packages.
	 * @param gwt          represents Gross Weight.
	 * @param gvol         represents Gross Measurement.
	 * @param crgstat      represents Cargo Status.
	 * @param dgind        represents DG Indicator.
	 * @param blParty      represents Billable Party.
	 * @param stgind       represents Storage Indicator.
	 * @param dop          represents DOP.
	 * @param pkgtyp       represents Packaging Type.
	 * @param coname       represents Consignee Name.
	 * @param poL          represents Port of Loading.
	 * @param poD          represents Port of Discharge.
	 * @param poFD         represents Port of Final Destination.
	 * @param cntrtype     represents Container Type.
	 * @param cntrsize     represents Container Size.
	 * @param cntr1        represents Container 1.
	 * @param cntr2        represents Container 2.
	 * @param cntr3        represents Container 3.
	 * @param cntrno_seqno Container Sequence Number related to container number.
	 * @return String a value of Manifest Sequence Number.
	 * @throws BusinessException
	 */
	@Override
	public String MftUpdation(String usrid, String coCd, String seqno, String varno, String blno, String crgtyp,
			String hscd, String hscdFr, String hscdTo, String crgdesc, String mark, String nopkgs, String gwt,
			String gvol, String crgstat, String dgind, String blParty, String stgind, String dop, String pkgtyp,
			String coname, String consCoCd, String poL, String poD, String poFD, String cntrtype, String cntrsize,
			String cntr1, String cntr2, String cntr3, String cntrno_sqno) throws BusinessException {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();

		StringTokenizer cntrtkn = new StringTokenizer(cntrno_sqno, "::");
		String cntrNo = cntrtkn.nextToken();
		String cntrSeqNo = cntrtkn.nextToken();
		//
//			String strMark = new String();
//			String strUpdate = new String();

		try {
			log.info("START: MftUpdation  DAO  Start usrid:" + usrid + "coCd:" + coCd + "seqno:" + seqno + "varno:"
					+ varno + "blno:" + blno + "crgtyp:" + crgtyp + "hscd:" + hscd + "hscdFr:" + hscdFr + "hscdTo:"
					+ hscdTo + "crgdesc:" + crgdesc + "mark:" + mark + "nopkgs:" + nopkgs + "gwt:" + gwt + "gvol:"
					+ gvol + "crgstat:" + crgstat + "dgind:" + dgind + "blParty:" + blParty + "stgind:" + stgind
					+ "dop:" + dop + "pkgtyp:" + pkgtyp + "coname:" + coname + "consCoCd:" + consCoCd + "poL:" + poL
					+ "poD:" + poD + "poFD:" + poFD + "cntrtype:" + cntrtype + "cntrsize:" + cntrsize + "cntr1:" + cntr1
					+ "cntr2:" + cntr2 + "cntr3:" + cntr3 + "cntrno_sqno:" + cntrno_sqno);


			boolean unstfCls = chkUnStfClosed( cntrNo, cntrSeqNo, varno);
			log.info("\nFROM UnStuffingEJB.mftUpdation unstfCls == " + unstfCls);
			if (unstfCls) {
				log.info("Writing from UnstuffingEJB.MftUpdation");
				log.info("Can not update,UnStuffing Closed");
				throw new BusinessException("M41171");
			}

			boolean blstat = chkBlStatus( seqno, varno, blno);
			if (blstat) {
				log.info("Writing from UnStuffingEJB.MftUpdation");
				log.info("BL canceled cannot Amend" + blno);
				throw new BusinessException("M20203");
			}

			boolean Pkgtyp = chkPkgtype(pkgtyp);
			if (!Pkgtyp) {
				log.info("Writing from UnStuffingEJB.MftUpdation");
				log.info("Invalid Packaging type " + pkgtyp);
				throw new BusinessException("M21604");
			}

			boolean portcdl = chkPortCode(poL);
			if (!portcdl) {
				log.info("Writing from UnStuffingEJB.MftUpdation");
				log.info("Invalid Port Code " + poL);
				throw new BusinessException("M21601");
			}

			boolean portcdd = chkPortCode(poD);
			if (!portcdd) {
				log.info("Writing from UnStuffingEJB.MftUpdation");
				log.info("Invalid Port Code " + poD);
				throw new BusinessException("M21602");
			}

			boolean portcfd = chkPortCode(poFD);
			if (poFD != null && !poFD.equals("")) {
				if (!portcfd) {
					log.info("Writing from UnStuffingEJB.MftUpdation");
					log.info("Invalid Final Destination Port Code " + poFD);
					throw new BusinessException("M21603");
				} // end of if !port..
			} // end of if poFD
			/*
			 * strUpdate = "UPDATE MANIFEST_DETAILS SET CRG_TYPE='" + crgtyp + "'," +
			 * "CRG_STATUS='" + crgstat + "',PKG_TYPE='" + pkgtyp + "',NBR_PKGS=" + nopkgs +
			 * "," + "CRG_DES='" + crgdesc + "',GROSS_WT=" + gwt + "," + "GROSS_VOL=" + gvol
			 * + ",DG_IND='" + CommonUtility.deNull(dgind) + "'," + "HS_CODE='" + hscd +
			 * "',DES_PORT='" + CommonUtility.deNull(poFD) + "'," + "LAST_MODIFY_USER_ID='"
			 * + usrid + "'," + "LAST_MODIFY_DTTM=SYSDATE,CONS_NM='" + coname + "' " +
			 * "WHERE VAR_NBR='" + varno + "' AND BL_NBR='" + blno + "' AND
			 * MFT_SEQ_NBR='" + seqno + "'"; log.info("6666666AMEND qry .....\n" +
			 * strUpdate);
			 */

			sb.append(" UPDATE MANIFEST_DETAILS SET CRG_TYPE=:crgtyp, CRG_STATUS=:crgstat, ");
			sb.append(" PKG_TYPE=:pkgtyp,NBR_PKGS=:nopkgs,CRG_DES=:crgdesc , ");
			sb.append(" GROSS_WT=:gwt,GROSS_VOL=:gvol,DG_IND=:dgind ,HS_CODE=:hscd, ");
			sb.append(" HS_SUB_CODE_FR =:hscdFr,HS_SUB_CODE_TO =:hscdTo,DES_PORT=:poFD, ");
			sb.append(" STG_TYPE=:stgind,LD_PORT=:poL,DIS_PORT=:poD,LAST_MODIFY_USER_ID= ");
			sb.append(" :usrid,LAST_MODIFY_DTTM=SYSDATE,CONS_NM=:coname,CONS_CO_CD= ");
			sb.append(" :consCoCd WHERE VAR_NBR=:varno AND BL_NBR=:blno AND MFT_SEQ_NBR=:seqno ");

			/*
			 * edosql1 = "SELECT EDO_ASN_NBR,NBR_PKGS FROM GB_EDO WHERE VAR_NBR
			 * ='"+varno + "' "+ "AND MFT_SEQ_NBR='" + seqno + "'"; Statement stmtedo =
			 * con.createStatement(); Statement stmtedosql = con.createStatement();
			 * ResultSet rs1 = null; String edonbr = ""; int edonbrpkgs = 0; double nom_wt =
			 * 0.0; double nom_vol = 0.0; rs1 = stmtedo.executeQuery(edosql1); while
			 * (rs1.next()) { edonbr = rs1.getString(1); edonbrpkgs = rs1.getInt(2); nom_wt
			 * = (edonbrpkgs / Integer.parseInt(nopkgs)) * Double.parseDouble(gwt); nom_vol
			 * = (edonbrpkgs / Integer.parseInt(nopkgs)) * Double.parseDouble(gvol);
			 * java.math.BigDecimal bdWt = new java.math.BigDecimal(nom_wt).setScale(2,
			 * java.math.BigDecimal.ROUND_HALF_UP); java.math.BigDecimal bdVol = new
			 * java.math.BigDecimal(nom_vol).setScale( 2,
			 * java.math.BigDecimal.ROUND_HALF_UP); edoupd = "UPDATE GB_EDO SET
			 * CRG_STATUS='" + crgstat + "', NOM_WT=" + bdWt + ",NOM_VOL=" + bdVol +
			 * " WHERE EDO_ASN_NBR =" + edonbr + " AND MFT_SEQ_NBR =" + seqno +
			 * " AND VAR_NBR=" + varno; int edocount = stmtedosql.executeUpdate(edoupd); }
			 * stmtedo.close(); stmtedosql.close();
			 */

			sb1.append(" UPDATE MFT_MARKINGS SET MFT_MARKINGS=:mark, ");
			sb1.append(" LAST_MODIFY_USER_ID=:usrid,LAST_MODIFY_DTTM= ");
			sb1.append(" SYSDATE WHERE MFT_SQ_NBR=:seqno ");

			log.info("amend MFT_MARKINGS qry .....\n" + sb1.toString());

			log.info(" ***MftUpdation SQL *****" + sb.toString());

			paramMap.put("crgtyp", crgtyp);
			paramMap.put("crgstat", crgstat);
			paramMap.put("pkgtyp", pkgtyp);
			paramMap.put("nopkgs", nopkgs);
			paramMap.put("crgdesc", crgdesc);
			paramMap.put("gwt", gwt);
			paramMap.put("gvol", gvol);
			paramMap.put("dgind", CommonUtility.deNull(dgind));
			paramMap.put("hscd", hscd);
			paramMap.put("hscdFr", hscdFr);
			paramMap.put("hscdTo", hscdTo);
			paramMap.put("poFD", CommonUtility.deNull(poFD));
			paramMap.put("stgind", CommonUtility.deNull(stgind));
			paramMap.put("poL", poL);
			paramMap.put("poD", poD);
			paramMap.put("usrid", usrid);
			paramMap.put("coname", coname);
			paramMap.put("consCoCd", consCoCd);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			paramMap.put("seqno", seqno);
			log.info("paramMap: " + paramMap.toString());
			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			log.info(" ***MftUpdation SQL *****" + sb1.toString());

			paramMap.put("mark", GbmsCommonUtility.addApostr(mark));
			paramMap.put("usrid", usrid);
			paramMap.put("seqno", seqno);
			log.info("paramMap: " + paramMap.toString());
			namedParameterJdbcTemplate.update(sb1.toString(), paramMap);

			log.info("seqno = " + CommonUtility.deNull(seqno));
		} catch (NullPointerException e) {
			log.info("Exception MftUpdation : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception MftUpdation : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception MftUpdation : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: MftUpdation  DAO  END");
		}
		return seqno;
	} // end of get MftUpdation

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->chkBlStatus()
	/**
	 * This method checks status of bl.
	 * 
	 * @param con   represents Connection object.
	 * @param seqno represents Container Sequence Number.
	 * @param varno represents Vessel Var Number.
	 * @param blno  BL Number.
	 * @return boolena value true or false.
	 * @throws BusinessException
	 */
	private boolean chkBlStatus(String seqno, String varno, String blno) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		boolean blstat = false;

		try {
			log.info("START: chkBlStatus  DAO  Start seqno:" + seqno + "varno:" + varno + "blno:" + blno);

			sb.append(" SELECT BL_STATUS FROM MANIFEST_DETAILS WHERE ");
			sb.append(" BL_STATUS='X' AND MFT_SEQ_NBR=:seqno AND ");
			sb.append(" VAR_NBR=:varno AND BL_NBR=:blno ");

			log.info(" ***chkBlStatus SQL *****" + sb.toString());

			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				blstat = true;
			} else {
				blstat = false;
			}

		} catch (NullPointerException e) {
			log.info("Exception chkBlStatus : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkBlStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkBlStatus  DAO  END = " + blstat);
		}
		return blstat;
	} // end of chkBlStatus method

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getScheme()
	/**
	 * This method returns vessel scheme.
	 * 
	 * @param voy_nbr represents Vessel Voyage Number.
	 * @return String represents Vessel Seheme.
	 * @throws BusinessException
	 */
	@Override
	public String getScheme(String voy_nbr) throws BusinessException {
		String msch = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getScheme  DAO  Start voy_nbr:" + voy_nbr);

			sb.append(" SELECT AB_CD FROM VESSEL_CALL VC, VESSEL_SCHEME ");
			sb.append(" VS WHERE VC.SCHEME=VS.SCHEME_CD AND VC.VV_CD=:voyNbr ");

			log.info(" ***getScheme SQL *****" + sb.toString());

			paramMap.put("voyNbr", voy_nbr);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				msch = rs.getString(1);
			}
			log.info("mch = " + CommonUtility.deNull(msch));
			return msch;
		} catch (NullPointerException e) {
			log.info("Exception getScheme : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getScheme : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getScheme  DAO  END");
		}
	} // end of getScheme

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getSchemeInd()
	/**
	 * This method returns vessel mixed scheme indicator.
	 * 
	 * @param voy_nbr represents Vessel Voyage Number.
	 * @return String represents Vessel Mixed Seheme Indicator.
	 * @throws BusinessException
	 */
	@Override
	public String getSchemeInd(String voy_nbr) throws BusinessException {
		String msch = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getSchemeInd  DAO  Start voy_nbr:" + voy_nbr);

			sb.append(" SELECT MIXED_SCHEME_IND FROM VESSEL_CALL WHERE VV_CD = :voyNbr ");
			log.info(" ***getSchemeInd SQL *****" + sb.toString());

			paramMap.put("voyNbr", voy_nbr);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				msch = rs.getString(1);
			}
			log.info("mch = " + CommonUtility.deNull(msch));
			return msch;
		} catch (NullPointerException e) {
			log.info("Exception getSchemeInd : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSchemeInd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSchemeInd  DAO  END");
		}
	}// end of getSchemeInd

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getClBjInd()
	/**
	 * This method retrieves close BG Indicatore.
	 * 
	 * @param seqnbr represents Sequence Number.
	 * @return String represents close BJ Indicator.
	 * @throws BusinessException
	 */
	@Override
	public String getClBjInd(String seqnbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String clbjind = "";

		try {
			log.info("START: getClBjInd  DAO  Start seqnbr:" + seqnbr);

			sb.append(" SELECT GB_CLOSE_BJ_IND FROM MANIFEST_DETAILS ");
			sb.append(" WHERE MFT_SEQ_NBR=:seqnbr ");

			log.info(" ***getClBjInd SQL *****" + sb.toString());

			paramMap.put("seqnbr", seqnbr);

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				clbjind = rs.getString("GB_CLOSE_BJ_IND");
			}
			log.info("clbjind = " + CommonUtility.deNull(clbjind));
		} catch (NullPointerException e) {
			log.info("Exception getClBjInd : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getClBjInd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getClBjInd  DAO  END");
		}
		return clbjind;
	} // end of getClBjInd method

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getSAacctno()
	/**
	 * This method returns Account Number.
	 * 
	 * @param voy_nbr represents Vessel Voyage Number.
	 * @return Vector a list of Account Numbers.
	 * @throws BusinessException
	 */
	@Override
	public List<String> getSAacctno(String voy_nbr) throws BusinessException {
		List<String> vacctno = new ArrayList<String>();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		/*
		 * sql= "SELECT A.ACCT_NBR FROM CUST_ACCT A, VESSEL_CALL V " +" WHERE
		 * A.BUSINESS_TYPE LIKE '%G%' AND" +" A.ACCT_NBR IS NOT NULL AND
		 * A.ACCT_STATUS_CD='A' AND A.TRIAL_IND='N' AND" +" V.CREATE_CUST_CD = A.CUST_CD
		 * AND V.VV_CD = '"+voy_nbr+"'" +" ORDER BY A.ACCT_NBR";
		 */

		try {
			log.info("START: getSAacctno  DAO  Start voy_nbr:" + voy_nbr);

			sb.append(" SELECT BILL_ACCT_NBR FROM VESSEL_CALL WHERE VV_CD =:voyNbr ");

			log.info(" ***getSAacctno SQL *****" + sb.toString());

			paramMap.put("voyNbr", voy_nbr);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				vacctno.add("" + rs.getString(1));
			}
			log.info("END: *** getSAacctno Result *****" + vacctno.size());
			return vacctno;
		} catch (NullPointerException e) {
			log.info("Exception getSAacctno : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSAacctno : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSAacctno  DAO  END");
		}
	} // end of getSAacctno

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getABacctno()
	/**
	 * This method returns AB Account Number.
	 * 
	 * @param voy_nbr represents Vessel Voyage Number.
	 * @return Vector a list of AB Account Numbers.
	 * @throws BusinessException
	 */
	@Override
	public List<UnStuffingValueObject> getABacctno(String voy_nbr) throws BusinessException {
		List<UnStuffingValueObject> vacctno = new ArrayList<UnStuffingValueObject>();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		/*
		 * sql= "SELECT A.ACCT_NBR FROM CUST_ACCT A, VESSEL_CALL V ,VESSEL_SCHEME VS" +"
		 * WHERE VS.SCHEME_CD=V.SCHEME AND VS.AB_CD = A.CUST_CD AND A.BUSINESS_TYPE LIKE
		 * '%G%' AND " +" A.ACCT_NBR IS NOT NULL AND A.ACCT_STATUS_CD='A' AND
		 * A.TRIAL_IND='N' AND" +" V.VV_CD = '"+voy_nbr+"' ORDER BY A.ACCT_NBR";
		 */

		try {
			log.info("START: getABacctno  DAO  Start voy_nbr:" + voy_nbr);

			sb.append(" SELECT SCHEME_CD,ACCT_NBR FROM VESSEL_SCHEME WHERE AB_CD IS NOT NULL ");
			log.info(" ***getABacctno SQL *****" + sb.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				UnStuffingValueObject mftvobj = new UnStuffingValueObject();
				mftvobj.setCrgType("" + rs.getString(1));
				mftvobj.setCrgDesc("" + rs.getString(2));
				vacctno.add(mftvobj);
			}
			log.info("END: *** getABacctno Result *****" + vacctno.size());
			return vacctno;
		} catch (NullPointerException e) {
			log.info("Exception getABacctno : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getABacctno : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getABacctno  DAO  END");
		}
	} // end of getABacctno

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getSchemeName()
	// lak added for checking of acct nbr entered in the text box of assign bill
	// party screen end 12092002
	/**
	 * This method retrieves the Scheme Name.
	 * 
	 * @param voy_nbr represents Vessel Voyage Number.
	 * @return String represents Scehme Name.
	 * @throws BusinessException
	 */
	@Override
	public String getSchemeName(String voy_nbr) throws BusinessException {
		String sch = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getSchemeName  DAO  Start voy_nbr:" + voy_nbr);

			sb.append(" SELECT SCHEME FROM VESSEL_CALL WHERE VV_CD=:voyNbr ");
			log.info(" ***getSchemeName SQL *****" + sb.toString());

			paramMap.put("voyNbr", voy_nbr);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				sch = rs.getString(1);
			}
			log.info("sch = " + CommonUtility.deNull(sch));
			return sch;
		} catch (NullPointerException e) {
			log.info("Exception getSchemeName : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSchemeName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSchemeName  DAO  END");
		}
	} // end of method getSchemeName

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getBPacctnbr()
	/**
	 * This method returns BP(Bill Party) Account Number.
	 * 
	 * @param voy_nbr represents Vessel Voyage Number.
	 * @param seqno   represents Sequence Number.
	 * @return String represents Bill Party Account Number.
	 * @throws BusinessException
	 */
	@Override
	public String getBPacctnbr(String voy_nbr, String seqno) throws BusinessException {
		String acctnbr = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String scheme = "";

		try {
			log.info("START: getBPacctnbr  DAO  Start voy_nbr:" + voy_nbr + "seqno:" + seqno);

			sb.append(" SELECT MIXED_SCHEME_ACCT_NBR FROM MANIFEST_DETAILS ");
			sb.append(" WHERE VAR_NBR=:voyNbr AND MFT_SEQ_NBR=:seqno ");

			log.info(" ***getBPacctnbr SQL *****" + sb.toString());

			paramMap.put("voyNbr", voy_nbr);
			paramMap.put("seqno", seqno);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				acctnbr = rs.getString(1);
			}
			if (acctnbr != null && !acctnbr.equals("") && !acctnbr.equals("null")) {
			
			} else {
				scheme = getSchemeName(voy_nbr);
				if (scheme.equals("JLR")) {
					acctnbr = getVCactnbr(voy_nbr);
				} else if (!scheme.equals("JLR") && !scheme.equals("JNL") && !scheme.equals("JBT")) {
					acctnbr = getABactnbr(voy_nbr);
				}
				log.info("getBPAcctnbr else  -- " + acctnbr);
			}
			return acctnbr;
		} catch (NullPointerException e) {
			log.info("Exception getBPacctnbr : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception getBPacctnbr : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getBPacctnbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBPacctnbr  DAO  END");
		}
	} // end of getBPacctnbr

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getVCactnbr()
	@Override
	public String getVCactnbr(String voy_nbr) throws BusinessException {
		String bactnbr = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		/*
		 * sql= "SELECT A.ACCT_NBR FROM CUST_ACCT A, VESSEL_CALL V " +" WHERE
		 * A.BUSINESS_TYPE LIKE '%G%' AND" +" A.ACCT_NBR IS NOT NULL AND
		 * A.ACCT_STATUS_CD='A' AND A.TRIAL_IND='N' AND" +" V.CREATE_CUST_CD = A.CUST_CD
		 * AND V.VV_CD = '"+voy_nbr+"'" +" ORDER BY A.ACCT_NBR";
		 */

		try {
			log.info("START: getVCactnbr  DAO  Start voy_nbr:" + voy_nbr);

			sb.append(" SELECT BILL_ACCT_NBR FROM VESSEL_CALL WHERE VV_CD=:voyNbr ");
			log.info(" ***getVCactnbr SQL *****" + sb.toString());

			paramMap.put("voyNbr", voy_nbr);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				bactnbr = rs.getString(1);
			}
			log.info("bactnbr: " + CommonUtility.deNull(bactnbr));
			return bactnbr;
		} catch (NullPointerException e) {
			log.info("Exception getVCactnbr : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVCactnbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVCactnbr  DAO  END");
		}
	} // end of method vessel call act nbr

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getABactnbr()
	/**
	 * This method retrieves the AB Account Number from vessel call table.
	 * 
	 * @param voy_nbr represents Vessel Voyage Number.
	 * @return String represents AB Account Number.
	 * @throws BusinessException
	 */
	@Override
	public String getABactnbr(String voy_nbr) throws BusinessException {
		String bactnbr = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getABactnbr  DAO  Start voy_nbr:" + voy_nbr);

			sb.append(" SELECT VS.ACCT_NBR FROM VESSEL_CALL VC,VESSEL_SCHEME VS ");
			sb.append(" WHERE VC.SCHEME=VS.SCHEME_CD AND VC.VV_CD =:voyNbr ");

			log.info(" ***getABactnbr SQL *****" + sb.toString());

			paramMap.put("voyNbr", voy_nbr);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				bactnbr = rs.getString(1);
			}
			log.info("bactnbr: " + CommonUtility.deNull(bactnbr));
			return bactnbr;
		} catch (NullPointerException e) {
			log.info("Exception getABactnbr : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getABactnbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getABactnbr  DAO  END");
		}
	} // end of method ab act nbr

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->MftAssignBillUpdate()
	/**
	 * This method Assign manifest Bill Update
	 * 
	 * @param voy_nbr represents Vessel Voyage Number.
	 * @param acctnbr represents Account Number.
	 * @param seqno   represents Sequence Number.
	 * @param userid  represents User Id.
	 * @throws BusinessException
	 */
	@Override
	public void MftAssignBillUpdate(String voy_nbr, String acctnbr, String seqno, String userid)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();

		int stransno = 0;
		int count_trans = 0;

		try {
			log.info("START: MftAssignBillUpdate  DAO  Start voy_nbr:" + voy_nbr + "acctnbr:" + acctnbr + "seqno:"
					+ seqno + "userid:" + userid);

			sb.append(" UPDATE manifest_details SET MIXED_SCHEME_ACCT_NBR=:acctnbr, ");
			sb.append(" LAST_MODIFY_DTTM =SYSDATE ,LAST_MODIFY_USER_ID=:userid ");
			sb.append(" WHERE VAR_NBR=:voyNbr and mft_seq_nbr =:seqno ");

			boolean bactnbr = checkAccountNbr(acctnbr);
			if (!bactnbr) {
				log.info("Writing from UnStuffingEJB.MftAssignBillUpdate");
				log.info("Invalid Account Nbr" + acctnbr);
				throw new BusinessException("M20801");
			}

			if (log.isInfoEnabled()) {
				log.info("SQL: " + sb.toString());
			}
			log.info(" ***MftAssignBillUpdate SQL *****" + sb.toString());

			paramMap.put("acctnbr", acctnbr);
			paramMap.put("userid", userid);
			paramMap.put("voyNbr", voy_nbr);
			paramMap.put("seqno", seqno);
			log.info("paramMap: " + paramMap.toString());
			int count = namedParameterJdbcTemplate.update(sb.toString(), paramMap);


			sb1.append(" SELECT MAX(TRANS_NBR) FROM MANIFEST_DETAILS_TRANS WHERE MFT_SEQ_NBR=:seqno ");

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				// Transaction Log Table Insertion 23/5/2002
				log.info(" ***MftAssignBillUpdate SQL *****" + sb1.toString());

				paramMap.put("seqno", seqno);

				rs = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
				if (rs.next()) {
					stransno = (rs.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
			}

			sb2.append(" INSERT INTO MANIFEST_DETAILS_TRANS(TRANS_NBR,MFT_SEQ_NBR,VAR_NBR, ");
			sb2.append(" MIXED_SCHEME_ACCT_NBR,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
			sb2.append(" VALUES(:stransno,:seqno,:voyNbr,:acctnbr,:userid,sysdate) ");

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				if (log.isInfoEnabled()) {
					log.info("SQL: " + sb2.toString());
				}
				log.info(" ***MftAssignBillUpdate SQL *****" + sb2.toString());

				paramMap.put("stransno", stransno);
				paramMap.put("seqno", seqno);
				paramMap.put("voyNbr", voy_nbr);
				paramMap.put("acctnbr", acctnbr);
				paramMap.put("userid", userid);
				log.info("paramMap: " + paramMap.toString());
				count_trans = namedParameterJdbcTemplate.update(sb2.toString(), paramMap);
			}

			if (count == 0) {
				// //sessionContext.setRollbackOnly();
				log.info("Writing from UnStuffingEJB.MftAssign Bill Party");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 21/01/2003
				if (count_trans == 0) {
					// //sessionContext.setRollbackOnly();
					log.info("Writing from UnStuffingEJB.MftAssign Bill Party");
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}
			}
		} catch (NullPointerException e) {
			log.info("Exception MftAssignBillUpdate : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception MftAssignBillUpdate : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception MftAssignBillUpdate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: MftAssignBillUpdate  DAO  END");
		}
	} // end of get MftAssignBillupdate

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->checkAccountNbr()
	// lak added for checking of acct nbr entered in the text box of assign bill
	// party screen start 12092002
	/**
	 * Checks valid Account Number
	 * 
	 * @param con    represents connection object.
	 * @param accnbr represents Account Number.
	 * @return boolean true or false.
	 * @throws BusinessException
	 */
	private boolean checkAccountNbr(String accnbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		String straccnbrcount = "";
		try {
			log.info("START: checkAccountNbr  DAO  Start accnbr:" + accnbr);

			sb.append(" SELECT COUNT(*) FROM CUST_ACCT A, COMPANY_CODE B, ");
			sb.append(" CUSTOMER C  WHERE A.BUSINESS_TYPE LIKE '%G%' AND ");
			sb.append(" A.ACCT_NBR IS NOT NULL AND B.CO_CD=C.CUST_CD AND ");
			sb.append(" A.ACCT_STATUS_CD='A' AND UPPER(A.ACCT_NBR)=UPPER(:accnbr) ");

			log.info(" ***checkAccountNbr SQL *****" + sb.toString());

			paramMap.put("accnbr", accnbr);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				straccnbrcount = CommonUtility.deNull(rs.getString(1));
			}
			if (((straccnbrcount).trim().equalsIgnoreCase("")) || straccnbrcount == null) {
				straccnbrcount = "0";
			}
			int intaccnbrcount = Integer.parseInt(straccnbrcount);
			if (intaccnbrcount > 0) {
				log.info("result is true = " + intaccnbrcount);
				return true;
			} else {
				log.info("rersult is false");
				return false;
			}

		} catch (NullPointerException e) {
			log.info("Exception checkAccountNbr : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkAccountNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkAccountNbr  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->MftAssignVslUpdate()
	/**
	 * This method Assign manifest Vsl Update
	 * 
	 * @param voy_nbr represents Vessel Voyage Number.
	 * @param status  represents Mixed Scheme Indicator.
	 * @param userid  represents User Id.
	 * @throws BusinessException
	 */
	@Override
	public void MftAssignVslUpdate(String voy_nbr, String status, String userid) throws BusinessException {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: MftAssignVslUpdate  DAO  Start voy_nbr:" + voy_nbr + "status:" + status + "userid:"
					+ userid);

			sb.append(" UPDATE vessel_call SET mixed_scheme_ind=:status, ");
			sb.append(" LAST_MODIFY_DTTM=sysdate ,LAST_MODIFY_USER_ID= ");
			sb.append(" :userid WHERE VV_CD=:voyNbr ");

			log.info(" ***MftAssignVslUpdate SQL *****" + sb.toString());

			paramMap.put("status", status);
			paramMap.put("userid", userid);
			paramMap.put("voyNbr", voy_nbr);

			//rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (log.isInfoEnabled()) {
				log.info("SQL: " + sb.toString());
			}
			log.info("paramMap: " + paramMap.toString());
			int count = namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			if (count == 0) {
				// //sessionContext.setRollbackOnly();
				log.info("Writing from UnStuffingEJB.MftAssignVesselUpdate");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
		} catch (NullPointerException e) {
			log.info("Exception MftAssignVslUpdate : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception MftAssignVslUpdate : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception MftAssignVslUpdate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: MftAssignVslUpdate  DAO  END");
		}
	} // end of get MftAssignVslUpdate

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->chkNbrEdopkgs()
	// added 23/01
	// added 29/04/2002 start
	/**
	 * This method checks edo nbr of packages.
	 * 
	 * @param seqno represents Container Sequence Number.
	 * @param varno represents Vessel Var Number.
	 * @param blno  BL Number.
	 * @return boolena value true or false.
	 * @throws BusinessException
	 */
	@Override
	public boolean chkNbrEdopkgs(String seqno, String varno, String blno) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		boolean bnbrpkgs = false;

		try {
			log.info("START: chkNbrEdopkgs  DAO  Start seqno:" + seqno + "varno:" + varno + "blno:" + blno);

			sb.append(" SELECT NBR_PKGS,EDO_NBR_PKGS FROM MANIFEST_DETAILS ");
			sb.append(" WHERE MFT_SEQ_NBR=:seqno AND VAR_NBR=:varno AND BL_NBR=:blno ");

			log.info(" ***chkNbrEdopkgs SQL *****" + sb.toString());

			paramMap.put("seqno", seqno);
			paramMap.put("varno", varno);
			paramMap.put("blno", blno);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (log.isInfoEnabled()) {
				log.info("SQL: " + sb.toString());
			}

			if (rs.next()) {
				if ((rs.getInt("NBR_PKGS")) > (rs.getInt("EDO_NBR_PKGS")))
					bnbrpkgs = true;
				else
					bnbrpkgs = false;
			}

		} catch (NullPointerException e) {
			log.info("Exception chkNbrEdopkgs : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkNbrEdopkgs : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkNbrEdopkgs  DAO  END = " + bnbrpkgs);
		}
		return bnbrpkgs;
	} // end of chkNbrEdopkgs method

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getMftAssignCargo()
	// ----------added by baskar 25jul---------------
	/**
	 * This method returns Mft Assign Cargo
	 * 
	 * @return Vector a list of UnStuffingCargoValueObject objects.
	 * @throws BusinessException
	 */
	@Override
	public List<UnStuffingCargoValueObject> getMftAssignCargo() throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String cc_cd = null;
		String cicos_cd = null;
		String cc_name = null;
		List<UnStuffingCargoValueObject> maniveclist = new ArrayList<UnStuffingCargoValueObject>();

		try {
			log.info("START: getMftAssignCargo  DAO  Start ");

			sb.append(" SELECT cc_cd, cicos_cd,cc_name FROM cargo_category_code ");

			log.info(" ***getMftAssignCargo SQL *****" + sb.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {

				UnStuffingCargoValueObject mftcrg = new UnStuffingCargoValueObject();
				cc_cd = rs.getString(1);
				cicos_cd = rs.getString(2);
				if (cc_cd.equals("00")) {
					cicos_cd = "G";
				}
				cc_name = rs.getString(3);

				mftcrg.setCc_cd(cc_cd);
				mftcrg.setCc_name(cc_name);
				mftcrg.setCicos_cd(cicos_cd);

				maniveclist.add(mftcrg);
			}

			return maniveclist;
		} catch (NullPointerException e) {
			log.info("Exception getMftAssignCargo : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getMftAssignCargo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getMftAssignCargo  DAO  END");
		}
	} // end of getMftAssignCargo

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->MftAssignCrgvalCheck()
	/**
	 * This method returns Mft Assign Cargo Val
	 * 
	 * @param voy_nbr represents Vessel Voyage Number.
	 * @param seqno   represents Sequence Number.
	 * @return String represents Cargo Category Code.
	 * @throws BusinessException
	 */
	@Override
	public String MftAssignCrgvalCheck(String voy_nbr, String seqno) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String crgCd = "";

		try {
			log.info("START: MftAssignCrgvalCheck  DAO  Start voy_nbr:" + voy_nbr + "seqno:" + seqno);

			sb.append(" SELECT CARGO_CATEGORY_CD from manifest_details ");
			sb.append(" WHERE VAR_NBR=:voyNbr and mft_seq_nbr =:seqno ");

			log.info(" ***MftAssignCrgvalCheck SQL *****" + sb.toString());

			paramMap.put("voyNbr", voy_nbr);
			paramMap.put("seqno", seqno);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				crgCd = rs.getString("CARGO_CATEGORY_CD");
			}
			log.info("crgCd = " + CommonUtility.deNull(crgCd));
			return crgCd;
		} catch (NullPointerException e) {
			log.info("Exception MftAssignCrgvalCheck : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception MftAssignCrgvalCheck : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: MftAssignCrgvalCheck  DAO  END");
		}
	} // --------end of baskar 25 july-------------

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->MftAssignCrgvalUpdate()
	/**
	 * This method returns Mft Assign Cargo Val Update
	 * 
	 * @param voy_nbr represents Vessel Voyage Number.
	 * @param crgval  represents Cargo Value.
	 * @param seqno   represents Sequence Number.
	 * @param userid  represents User Id.
	 * @throws BusinessException
	 */
	@Override
	public void MftAssignCrgvalUpdate(String voy_nbr, String crgval, String seqno, String userid)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();

		int stransno = 0;
		int count_trans = 0;

		try {
			log.info("START: MftAssignCrgvalUpdate  DAO  Start voy_nbr:" + voy_nbr + "crgval:" + crgval + "seqno:"
					+ seqno + "userid:" + userid);

			sb.append(" UPDATE manifest_details SET CARGO_CATEGORY_CD=:crgval, ");
			sb.append(" LAST_MODIFY_DTTM =SYSDATE ,LAST_MODIFY_USER_ID=:userid ");
			sb.append(" WHERE VAR_NBR=:voyNbr and mft_seq_nbr =:seqno ");

			if (log.isInfoEnabled()) {
				log.info("SQL: " + sb.toString());
			}
			log.info(" ***MftAssignCrgvalUpdate SQL *****" + sb.toString());

			paramMap.put("crgval", crgval);
			paramMap.put("userid", userid);
			paramMap.put("voyNbr", voy_nbr);
			paramMap.put("seqno", seqno);
			log.info("paramMap: " + paramMap.toString());
			int count = namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			sb1.append(" SELECT MAX(TRANS_NBR) FROM MANIFEST_DETAILS_TRANS WHERE MFT_SEQ_NBR=:seqno ");

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				// Transaction Log Table Insertion 23/5/2002

				log.info(" ***MftAssignCrgvalUpdate SQL *****" + sb1.toString());

				paramMap.put("seqno", seqno);

				rs = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
				if (rs.next()) {
					stransno = (rs.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
			}

			sb2.append(" INSERT INTO MANIFEST_DETAILS_TRANS(TRANS_NBR,MFT_SEQ_NBR,VAR_NBR, ");
			sb2.append(" CARGO_CATEGORY_CD,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
			sb2.append(" VALUES(:stransno,:seqno,:voyNbr,:acctnbr,:userid,sysdate) ");

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				if (log.isInfoEnabled()) {
					log.info("SQL: " + sb2.toString());
				}
				log.info(" ***MftAssignCrgvalUpdate SQL *****" + sb2.toString());

				paramMap.put("stransno", stransno);
				paramMap.put("seqno", seqno);
				paramMap.put("voyNbr", voy_nbr);
				paramMap.put("crgval", crgval);
				paramMap.put("userid", userid);
				log.info("paramMap: " + paramMap.toString());
				count_trans = namedParameterJdbcTemplate.update(sb2.toString(), paramMap);
			}

			if (count == 0) {
				// //sessionContext.setRollbackOnly();
				log.info("Writing from UnStuffingEJB.MftAssign Cargo Value");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log
															// Table Insertion
															// 21/01/2003
				if (count_trans == 0) {
					// //sessionContext.setRollbackOnly();
					log.info("Writing from UnStuffingEJB.MftAssign Cargo Value");
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}
			}

		} catch (NullPointerException e) {
			log.info("Exception MftAssignCrgvalUpdate : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception MftAssignCrgvalUpdate : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception MftAssignCrgvalUpdate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: MftAssignCrgvalUpdate  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->updateWaiverStatus()
	@Override
	public void updateWaiverStatus(String varno, String vslInVoynbr, String cntrno, String cntrseqno, String waiversts,
			String usrid) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String dbwaivests = "";
		try {
			log.info("START: updateWaiverStatus  DAO  Start varno:" + varno + " vslInVoynbr:" + vslInVoynbr + " cntrno:"
					+ cntrno + " cntrseqno:" + cntrseqno + " waiversts:" + waiversts + " usrid:" + usrid);

			sb.append(" select waive_unstuff,bill_unstuff_triggered_ind bill_ind ");
			sb.append(" from cc_unstuff_manifest where cntr_seq_bl_no=:cntrseqno ");
			sb.append(" and cntr_nbr=:cntrno and var_nbr=:varno ");


			paramMap.put("cntrseqno", cntrseqno);
			paramMap.put("cntrno", cntrno);
			paramMap.put("varno", varno);
			log.info(" ***updateWaiverStatus SQL *****" +sb.toString());
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				dbwaivests = CommonUtility.deNull(rs.getString("waive_unstuff"));
			}

			sb = new StringBuilder();
			sb.append(" update cc_unstuff_manifest set waive_unstuff=:waiversts, ");
			sb.append(" unstuff_closed='Y',last_modify_user_id=:usrid, ");
			sb.append(" last_modify_dttm=sysdate where var_nbr=:varno ");
			sb.append(" and cntr_nbr=:cntrno and cntr_seq_bl_no=:cntrseqno ");

			// log.info("DBwaivests : " + dbwaivests + " waivests : " +
			// waiversts);
			if (log.isInfoEnabled()) {
				log.info("DBwaivests : " + dbwaivests + " waivests : " + waiversts);
			}

			if (dbwaivests.equalsIgnoreCase("Y") && waiversts.equals("N")) {
				
				log.info(" ***updateWaiverStatus SQL *****" + sb.toString());

				paramMap.put("waiversts", waiversts);
				paramMap.put("usrid", usrid);
				paramMap.put("varno", varno);
				paramMap.put("cntrno", cntrno);
				paramMap.put("cntrseqno", cntrseqno);
				log.info("paramMap: " + paramMap.toString());
				namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			} else if (dbwaivests.equalsIgnoreCase("N") && waiversts.equalsIgnoreCase("Y")) {
				if (log.isInfoEnabled()) {
					log.info("Executing processGBLog::cancelStuffCharges");
				}
				boolean billcancelled = processGBLogRepos.cancelStuffCharges(Integer.valueOf(cntrseqno).intValue(),
						"SU", varno, "D");
				if (log.isInfoEnabled()) {
					log.info("Res: " + billcancelled);
				}
				if (!billcancelled) {
					throw new BusinessException("Bill already generated.Cannot waive charge.");
				} else {
					log.info(" ***updateWaiverStatus SQL *****" + sb.toString());

					paramMap.put("waiversts", waiversts);
					paramMap.put("usrid", usrid);
					paramMap.put("varno", varno);
					paramMap.put("cntrno", cntrno);
					paramMap.put("cntrseqno", cntrseqno);
					
					log.info(" *** updateWaiverStatus params *****" + paramMap.toString());
					namedParameterJdbcTemplate.update(sb.toString(), paramMap);
				}
			}
			log.info("END: ** updateWaiverStatus Result ****");
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

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->closeUnStuffing()
	@Override
	public boolean closeUnStuffing(String usrid, String vslInVoynbr, String varno, String containerno, String unStfDttm,
			String waiveUnStfChrg) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		SqlRowSet rs3 = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		StringBuilder sb3 = new StringBuilder();
		StringBuilder sb4 = new StringBuilder();
		StringBuilder sb5 = new StringBuilder();
		StringBuilder sb6 = new StringBuilder();

		String gbVslTransEventLogQry = "";

		StringTokenizer token = new StringTokenizer(containerno, "::");
		String cntrNbr = token.nextToken();
		String seqBlNbr = token.nextToken();

		try {
			log.info("START: closeUnStuffing  DAO  Start usrid:" + usrid + " vslInVoynbr:" + vslInVoynbr + " varno:"
					+ varno + " containerno:" + containerno + " unStfDttm:" + unStfDttm + " waiveUnStfChrg:"
					+ waiveUnStfChrg);

			// log.info("B44444 isUnStuffingDttmLesser ");
			boolean latter = this.isUnStuffingDttmLesser(unStfDttm);
			if (log.isInfoEnabled()) {
				log.info("isUnStuffingDttmLesser: " + latter);
			}
			// log.info("latter == " + latter);
			if (!latter) {
				log.info("Writing from UnStuffingEJB.closeUnStuffing");
				log.info("UnStf close dttm can not be latter than current date");
				throw new BusinessException("ErrorMsg_Unstuff_Date");
			}
			// changed by Irene Tan on 05 Apr 2004 : to investigate UAT problem
			// in closing
			latter = false;
			latter = this.isUnStuffingDttmGreater(varno, unStfDttm);
			if (log.isInfoEnabled()) {
				log.info("isUnStuffingDttmLesser: " + latter);
			}
			// log.info("unstuff check: " + latter);
			if (!latter) {
				// if (!isUnStuffingDttmGreater(con, varno, unStfDttm)) {
				// end changed by Irene Tan on 05 Apr 2004
				log.info("Writing from UnStuffingEJB.closeUnStuffing");
				log.info("UnStf close dttm can not be less than ATB DTTM");
				throw new BusinessException("M41177");
			}

			// commented by tvs.to include additional where condition to chect
			// //count of active manifest
			// sql = "update cc_unstuff_manifest set dttm_unstuff = to_date('" +
			// unStfDttm + "','DDMMYYYY HH24MI'),waive_unstuff='" +
			// waiveUnStfChrg +
			// "'," +
			// "unstuff_closed='Y',last_modify_user_id='" + usrid +
			// "',last_modify_dttm=sysdate where active_status='A' and
			// var_nbr='" + varno + "' and " +
			// "cntr_nbr='" + cntrno + "' and cntr_seq_bl_no='" + seq_bl_no +
			// "'";

			sb.append(" update cc_unstuff_manifest set dttm_unstuff = to_date( ");
			sb.append(" :unStfDttm,'DDMMYYYY HH24MI'),waive_unstuff= ");
			sb.append(" :waiveUnStfChrg,unstuff_closed='Y',last_modify_user_id=:usrid ");
			sb.append(" ,last_modify_dttm=sysdate where ");
			sb.append(" (select count(MFT_SEQ_NBR) from manifest_details where ");
			sb.append(" bl_status <> 'X' and unstuff_seq_nbr=(select unstuff_seq_nbr ");
			sb.append(" from cc_unstuff_manifest where active_status='A' and var_nbr= ");
			sb.append(" :varno and cntr_nbr=:cntrNbr and cntr_seq_bl_no=:seqBlNbr)) > 0 and ");
			sb.append(" active_status='A' and var_nbr=:varno and cntr_nbr=:cntrNbr ");
			sb.append(" and cntr_seq_bl_no=:seqBlNbr ");

			log.info(" ***closeUnStuffing SQL *****" + sb.toString());

			paramMap.put("unStfDttm", unStfDttm);
			paramMap.put("waiveUnStfChrg", waiveUnStfChrg);
			paramMap.put("usrid", usrid);
			paramMap.put("varno", varno);
			paramMap.put("cntrNbr", cntrNbr);
			paramMap.put("seqBlNbr", seqBlNbr);

			// log.info("33333333update cc_unstuff_manifest...inside
			// EJB.closeUnStuffing... " + sql);
			log.info("paramMap: " + paramMap.toString());
			sb1.append(" select iso_size_type_cd,imdg_cl_cd,oog_oh,oog_ol_front, ");
			sb1.append(" oog_ol_back,oog_ow_right,oog_ow_left,refr_ind,uc_ind, ");
			sb1.append(" over_sz_ind,special_details,cat_cd,status from cntr ");
			sb1.append(" where cntr_seq_nbr='").append(seqBlNbr).append("'");

			Map<String, Object> value = this.getContainerDetails(sb1.toString());

			// following lines added by karthi on 14/04/04 To capture
			// previouscontainer status and cat code before stuffing/unstuffing

			String cat_CdVar = "";
			String statusVar = "";
			cat_CdVar = value.get("catcode").toString();
			statusVar = value.get("status").toString();
			// up to this karthi..

			String cntrctcd = cntrCmnFcRepo.getCntrCatCd(value.get("isosize").toString(),
					value.get("imdgclcd").toString(), getInt(value.get("oogoh")), getInt(value.get("oogolfront")),
					getInt(value.get("oogolback")), getInt(value.get("oogowright")), getInt(value.get("oogowleft")),
					value.get("refrind").toString(), value.get("ucind").toString(), value.get("overszind").toString(),
					value.get("specialdtls").toString(), "E");

			// log.info("container cat code is >>>>>>>>>>>>::::::::::::" +
			// cntrctcd);
			sb2.append(" update cntr set status='E',cat_cd=:cntrctcd ");
			sb2.append(" where disc_vv_cd=:varno and ");
			sb2.append(" cntr_nbr=:cntrNbr and cntr_seq_nbr=:seqBlNbr ");
			// log.info("4444444444update manifest_details...inside
			// EJB.closeUnStuffing... " + sqlMft);

			log.info(" ***closeUnStuffing SQL *****" + sb2.toString());

			paramMap.put("cntrctcd", cntrctcd);
			paramMap.put("varno", varno);
			paramMap.put("cntrNbr", cntrNbr);
			paramMap.put("seqBlNbr", seqBlNbr);
			log.info("paramMap: " + paramMap.toString());
			int count = namedParameterJdbcTemplate.update(sb.toString(), paramMap);


			int countMft = namedParameterJdbcTemplate.update(sb2.toString(), paramMap);

			// 09 Dec 2003 add execute query
			// 14/04/04 karthi added to vars cat_cd,status to insert into
			// cntr_txn To capture previouscontainer status and cat code before
			// stuffing/unstuffing
			sb3.append(" insert into cntr_txn(CNTR_SEQ_NBR,CNTR_NBR,TXN_DTTM,TXN_CD,");
			sb3.append(" LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, DISC_VV_CD ,CAT_CD, STATUS) values(");
			sb3.append(" :seqBlNbr,:cntrNbr,to_date(:unStfDttm,'DDMMYYYY HH24MI'),'UNSC',:usrid");
			sb3.append(",to_date(:unStfDttm,'DDMMYYYY HH24MI'),:varno,:catCdVar,:statusVar ) ");

			paramMap.put("seqBlNbr", seqBlNbr);
			paramMap.put("cntrNbr", cntrNbr);
			paramMap.put("usrid", usrid);
			paramMap.put("unStfDttm", unStfDttm);
			paramMap.put("varno", varno);
			paramMap.put("catCdVar", cat_CdVar);
			paramMap.put("statusVar", statusVar);
			log.info(" ***closeUnStuffing SQL *****" + sb3.toString());
			log.info("paramMap: " + paramMap.toString());
			int countCntr = namedParameterJdbcTemplate.update(sb3.toString(), paramMap);


			if (countCntr == 0) {
				// sessionContext.setRollbackOnly();
				log.info("Writing from UnStuffingEJB.closeUnStuffing");
				log.info("Record Cannot be inserted into Database");
				throw new BusinessException("M41175");
			}

			if (count == 0) {
				// sessionContext.setRollbackOnly();
				log.info("Writing from UnStuffingEJB.closeUnStuffing");
				log.info("Record Cannot be updated to Database");
				log.info(gbVslTransEventLogQry);
				throw new BusinessException("M41175");
			}

			if (count == 0 || countMft == 0) {
				// sessionContext.setRollbackOnly();
				log.info("Writing from UnStuffingEJB.closeUnStuffing");
				log.info("Record Cannot be updated to Database");
				log.info(gbVslTransEventLogQry);
				throw new BusinessException("M4201");
			}
			if (count != 0 && countMft != 0) {
				if (waiveUnStfChrg.equals("N")) {
					// log.info("@@@@@@@ INSIDE EVENT LOG");
					VesselTxnEventLogValueObject vvo = null;
					GeneralEventLogValueObject gvo = null;
					List<GeneralEventLogValueObject> chargeEventLogList = new ArrayList<GeneralEventLogValueObject>();
					String strCntrSz = "";
					String strScheme = "";
					String strBlPartyAcNo = "";
					String strBlAcctNo = "";
					Timestamp timeStamp = null;

					sb4.append(" select SCHEME,BILL_ACCT_NBR from vessel_call where ");
					sb4.append(" vv_cd=:varno and in_voy_nbr=:vslInVoynbr ");

					sb5.append(" select size_ft from cntr where cntr_nbr=:cntrNbr and ");
					sb5.append(" cntr_seq_nbr=:seqBlNbr ");

					sb6.append(" select bill_party_acct_nbr,sysdate from cc_unstuff_manifest where ");
					sb6.append(" var_nbr=:varno and cntr_nbr=:cntrNbr and cntr_seq_bl_no=:seqBlNbr ");

					try {

						log.info(" ***closeUnStuffing SQL *****" + sb4.toString());

						paramMap.put("varno", varno);
						paramMap.put("vslInVoynbr", vslInVoynbr);

						rs1 = namedParameterJdbcTemplate.queryForRowSet(sb4.toString(), paramMap);

						log.info(" ***closeUnStuffing SQL *****" + sb5.toString());

						paramMap.put("cntrNbr", cntrNbr);
						paramMap.put("seqBlNbr", seqBlNbr);

						rs2 = namedParameterJdbcTemplate.queryForRowSet(sb5.toString(), paramMap);

						log.info(" ***closeUnStuffing SQL *****" + sb6.toString());

						paramMap.put("varno", varno);
						paramMap.put("cntrNbr", cntrNbr);
						paramMap.put("seqBlNbr", seqBlNbr);

						rs3 = namedParameterJdbcTemplate.queryForRowSet(sb6.toString(), paramMap);

						if (rs1.next()) {
							strScheme = CommonUtility.deNull(rs1.getString("SCHEME"));
							strBlAcctNo = CommonUtility.deNull(rs1.getString("BILL_ACCT_NBR"));
						}

						if (rs2.next()) {
							strCntrSz = CommonUtility.deNull(rs2.getString("size_ft"));
						}

						if (rs3.next()) {
							strBlPartyAcNo = CommonUtility.deNull(rs3.getString("bill_party_acct_nbr"));
							timeStamp = (java.sql.Timestamp) rs3.getObject("sysdate");
						}

					} catch (Exception ex) {
					}

					if (!strScheme.equals("JLR") && !strScheme.equals("JNL") && !strScheme.equals("JBT")) {
						strScheme = "JLR";
					}

					if (strCntrSz.equals("20") || strCntrSz == "20") {
						strCntrSz = "2";
					} else if (strCntrSz.equals("40") || strCntrSz == "40") {
						strCntrSz = "4";
					}
					log.info("\n7777777777777 timeStamp == " + timeStamp);

					if (strBlPartyAcNo.equals("") || strBlPartyAcNo == null) {
						strBlPartyAcNo = strBlAcctNo;
					}

					// SET 'vesselTxnEventLogValueObject' VALUE OBJECT
					vvo = new VesselTxnEventLogValueObject();

					vvo.setVvCd(varno);
					vvo.setTxnDttm(timeStamp);
					vvo.setBillStuffInd("Y");
					vvo.setLastModifyUserId(usrid);
					vvo.setLastModifyDttm(timeStamp);

					// SET 'generalEventLogValueObject' VALUE OBJECT
					gvo = new GeneralEventLogValueObject();

					gvo.setDiscVvCd(varno);
					gvo.setVvInd("D");
					gvo.setBusinessType("G");
					gvo.setSchemeCd(strScheme);
					gvo.setTariffMainCatCd("CC");
					gvo.setTariffSubCatCd("SU");
					gvo.setMvmt("00");
					gvo.setType("00");
					gvo.setCntrCat("0");
					gvo.setCntrSize(strCntrSz);
					gvo.setLocalLeg("IM");
					gvo.setRefInd("SU");
					gvo.setCntrNbr(cntrNbr);
					gvo.setCntrSeqNbr(Integer.parseInt(seqBlNbr));
					gvo.setBillAcctNbr(strBlPartyAcNo);
					gvo.setLastModifyUserId(usrid);
					gvo.setLastModifyDttm(timeStamp);

					chargeEventLogList.add(gvo);

					// log.info("@@@@@@@ B4 executeGBCharges");
					try {
						if (log.isInfoEnabled()) {
							log.info("processgblog.executeGBCharges");
						}
						processGBLogRepos.executeGBCharges(vvo, chargeEventLogList, "SU");
					} catch (Exception e) {
						// sessionContext.setRollbackOnly();
						log.info("exception UnStuffingEJB.mftBCancel: " ,e);
						throw new BusinessException("M4201");
					}
					// log.info("@@@@@@@ A4 executeGBCharges");
				} else {
					sb = new StringBuilder();
					sb.append(" update cc_unstuff_manifest set bill_unstuff_triggered_ind='X' ");
					sb.append(" where active_status='A' and var_nbr=:varno and ");
					sb.append(" cntr_nbr=:cntrNbr and cntr_seq_bl_no=:seqBlNbr ");

					log.info(" ***closeUnStuffing SQL *****" + sb.toString());

					paramMap.put("varno", varno);
					paramMap.put("cntrNbr", cntrNbr);
					paramMap.put("seqBlNbr", seqBlNbr);

					namedParameterJdbcTemplate.update(sb.toString(), paramMap);

				}
			}
			log.info("END: ** closeUnStuffing Result ****");
		} catch (NullPointerException e) {
			log.info("Exception closeUnStuffing : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception closeUnStuffing : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception closeUnStuffing : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: closeUnStuffing  DAO  END");
		}
		return true;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->isUnStuffingDttmLesser()
	public boolean isUnStuffingDttmLesser(String strUnStuffdttm) throws BusinessException {

		boolean lesser = false;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: isUnStuffingDttmLesser  DAO  Start strUnStuffdttm:" + strUnStuffdttm);

			sb.append(" select 'true' from dual where sysdate >= ");
			sb.append(" to_date(:strUnStuffdttm,'DDMMYYYY HH24MI') ");

			log.info(" ***isUnStuffingDttmLesser SQL *****" + sb.toString());

			paramMap.put("strUnStuffdttm", strUnStuffdttm);

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				if (rs.getString(1).equals("true"))
					lesser = true;
				else
					lesser = false;
			}
			log.info("END: ** isUnStuffingDttmLesser Result ****" + lesser);
		} catch (NullPointerException e) {
			log.info("Exception isUnStuffingDttmLesser : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isUnStuffingDttmLesser : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isUnStuffingDttmLesser  DAO  END = " + lesser);
		}
		return lesser;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->isUnStuffingDttmGreater()
	public boolean isUnStuffingDttmGreater(String vvcode, String strUnStuffdttm)
			throws BusinessException {

		boolean greater = false;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: isUnStuffingDttmGreater  DAO  Start vvcode:" + vvcode + " strUnStuffdttm:"
					+ strUnStuffdttm);

			sb.append(" select 'true' from berthing where shift_ind='1' ");
			sb.append(" and atb_dttm <= to_date(:strUnStuffdttm, ");
			sb.append(" 'DDMMYYYY HH24MI') and vv_cd=:vvcode ");

			log.info(" ***isUnStuffingDttmGreater SQL *****" + sb.toString());

			paramMap.put("strUnStuffdttm", strUnStuffdttm);
			paramMap.put("vvcode", vvcode);

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				String res = rs.getString(1);
				if (res.equalsIgnoreCase("true")) {
					greater = true;
				} else {
					greater = false;
				}
			}
			log.info("END: ** isUnStuffingDttmGreater Result ****" + greater);
		} catch (NullPointerException e) {
			log.info("Exception isUnStuffingDttmGreater : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isUnStuffingDttmGreater : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isUnStuffingDttmGreater  DAO  END");
		}
		return greater;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getInt()
	/**
	 * Takes a object and converts to int.If any exception occurs returns 0.
	 * 
	 * @param intobj as Object.
	 * @return int
	 */
	private int getInt(Object intobj) {
		try {
			log.info("START: getInt  DAO  Start :" + intobj.toString());
			return Integer.valueOf(intobj.toString()).intValue();
		} catch (Exception e) {
			return 0;
		}
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getContainerDetails()
	private Map<String, Object> getContainerDetails(String sql) throws BusinessException {
		Map<String, Object> retVal = new HashMap<String, Object>();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getContainerDetails  DAO  Start sql:" + sql);

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				retVal.put("isosize", CommonUtility.deNull(rs.getString(1)));
				retVal.put("imdgclcd", CommonUtility.deNull(rs.getString(2)));
				retVal.put("oogoh", CommonUtility.deNull(rs.getString(3)));
				retVal.put("oogolfront", CommonUtility.deNull(rs.getString(4)));
				retVal.put("oogolback", CommonUtility.deNull(rs.getString(5)));
				retVal.put("oogowright", CommonUtility.deNull(rs.getString(6)));
				retVal.put("oogowleft", CommonUtility.deNull(rs.getString(7)));
				retVal.put("refrind", CommonUtility.deNull(rs.getString(8)));
				retVal.put("ucind", CommonUtility.deNull(rs.getString(9)));
				retVal.put("overszind", CommonUtility.deNull(rs.getString(10)));
				retVal.put("specialdtls", CommonUtility.deNull(rs.getString(11)));
				// following 2 lines added by karthi on 14/04/04 To capture
				// previouscontainer status and cat code before
				// stuffing/unstuffing
				retVal.put("catcode", CommonUtility.deNull(rs.getString(12)));
				retVal.put("status", CommonUtility.deNull(rs.getString(13)));
				// up to this... karthi.
			}
			log.info("END: ** getContainerDetails Result ****" + retVal.toString());
		} catch (NullPointerException e) {
			log.info("Exception getContainerDetails : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getContainerDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getContainerDetails  DAO  END");
		}
		return retVal;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->MftAssignBlParty()
	/**
	 * Method to get the Unstuffing details as a UnStuffingValueObject.
	 * 
	 * @param varno         Var Number of the Vessel
	 * @param cntrno        Container Number
	 * @param blPartyAcctNO Bill Party Account Number
	 * @return UnStuffingValueObject
	 * @throws BusinessException
	 */
	@Override
	public UnStuffingValueObject MftAssignBlParty(String varno, String cntrno, String blPartyAcctNo)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		int mftseqno = 0;

		StringTokenizer cntrtkn = new StringTokenizer(cntrno, "::");
		String cntrNo = cntrtkn.nextToken();
		String seq_bl_no = cntrtkn.nextToken();

		UnStuffingValueObject unStfValueObj = new UnStuffingValueObject();

		try {
			log.info("START: MftAssignBlParty  DAO  Start varno:" + varno + "cntrno:" + cntrno + "blPartyAcctNo:"
					+ blPartyAcctNo);

			sb.append(" update cc_unstuff_manifest set BILL_PARTY_ACCT_NBR = ");
			sb.append(" :blPartyAcctNo where var_nbr=:varno and cntr_nbr= ");
			sb.append(" :cntrNo and CNTR_SEQ_BL_NO =:seqBlNo ");

			log.info(" ***MftAssignBlParty SQL *****" + sb.toString());

			paramMap.put("blPartyAcctNo", blPartyAcctNo);
			paramMap.put("varno", varno);
			paramMap.put("cntrNo", cntrNo);
			paramMap.put("seqBlNo", seq_bl_no);
			log.info("paramMap: " + paramMap.toString());
			int count = namedParameterJdbcTemplate.update(sb.toString(), paramMap);


			if (checkAccountNumber(blPartyAcctNo)) {
				if (count == 0) {
					// //sessionContext.setRollbackOnly();
					log.info("Writing from UnStuffingEJB.MftAssignBlParty");
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}

				/*
				 * sql = "select
				 * mft_seq_nbr,bl_nbr,crg_type,crg_des,crg_status,pkg_type,nbr_pkgs,"+
				 * "gross_wt,gross_vol,dg_ind,hs_code,des_port,bill_party_acct_nbr,cons_nm "+
				 * "from cc_manifest_details where bl_status='A' and var_nbr='"+varno+"' and
				 * "+ "cntr_nbr = '"+cntrNo+"' and mft_seq_nbr = (select
				 * min(to_number(mft_seq_nbr)) from "+ "cc_manifest_details where cntr_nbr =
				 * '"+cntrNo+"' and var_nbr='"+varno+"')";
				 */
				sb1.append(" select mft_seq_nbr,bl_nbr,crg_type,crg_des,crg_status, ");
				sb1.append(" pkg_type,nbr_pkgs,gross_wt,gross_vol,dg_ind,hs_code, ");
				sb1.append(" des_port,cons_nm,unstuff_closed unstfsts, ld_port, dis_port ");
				sb1.append(" from manifest_details mft,cc_unstuff_manifest unstf where ");
				sb1.append(" unstf.active_status='A' and unstf.unstuff_seq_nbr= ");
				sb1.append(" mft.unstuff_seq_nbr and bl_status='A' and mft.var_nbr=:varno ");
				sb1.append(" and mft_seq_nbr = (select min(to_number(mft_seq_nbr)) from ");
				sb1.append(" manifest_details where var_nbr=:varno ");
				// SL-GBMS-20050214-1 : Change by Chor Keng on 15/2/05 : Add in
				// bl_status checks
				// "' and unstuff_seq_nbr = (select unstuff_seq_nbr " +
				sb1.append(" and bl_status='A' and unstuff_seq_nbr = (select unstuff_seq_nbr ");
				// SL-GBMS-20050214-1 : End of Change
				sb1.append(" from cc_unstuff_manifest where var_nbr=:varno ");
				sb1.append(" and ACTIVE_STATUS='A' and cntr_nbr =:cntrNo");
				sb1.append(" and CNTR_SEQ_BL_NO =:seqBlNo)) ");

				log.info("TO select fisrt bllbr for this container sql == " + sb1.toString());

				log.info(" ***MftAssignBlParty SQL *****" + sb1.toString());

				paramMap.put("varno", varno);
				paramMap.put("cntrNo", cntrNo);
				paramMap.put("seqBlNo", seq_bl_no);
				log.info("paramMap: " + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);

				log.info("###### INSIDE THE  MftAssignBlParty  sql == " + sb1.toString());
				if (rs.next()) {
					mftseqno = rs.getInt("MFT_SEQ_NBR");

					unStfValueObj.setCrgn(getCrgNm(rs.getString("CRG_TYPE")));
					unStfValueObj.setCrgType(CommonUtility.deNull(rs.getString("CRG_TYPE")));
					unStfValueObj.setHsCode(CommonUtility.deNull(rs.getString("HS_CODE")));
					unStfValueObj.setCrgDesc(CommonUtility.deNull(rs.getString("CRG_DES")));
					unStfValueObj.setNoofPkgs(CommonUtility.deNull(rs.getString("NBR_PKGS")));
					unStfValueObj.setGrWt(CommonUtility.deNull(rs.getString("GROSS_WT")));
					unStfValueObj.setGrMsmt(CommonUtility.deNull(rs.getString("GROSS_VOL")));
					unStfValueObj.setCrgStatus(CommonUtility.deNull(rs.getString("CRG_STATUS")));
					unStfValueObj.setDgInd(CommonUtility.deNull(rs.getString("DG_IND")));
					// unStfValueObj.setBillAccNo(CommonUtility.deNull(rs.getString("BILL_PARTY_ACCT_NBR")));
					unStfValueObj.setPkgn(getPkgName(rs.getString("PKG_TYPE")));
					unStfValueObj.setPkgType(CommonUtility.deNull(rs.getString("PKG_TYPE")));
					unStfValueObj.setPortFD(CommonUtility.deNull(rs.getString("DES_PORT")));
					unStfValueObj.setPortFDn(getPortName(CommonUtility.deNull(rs.getString("DES_PORT"))));
					unStfValueObj.setSeqNo(CommonUtility.deNull(rs.getString("MFT_SEQ_NBR")));
					unStfValueObj.setConsigneeNM(CommonUtility.deNull(rs.getString("cons_nm")));
					unStfValueObj.setBillNbr(CommonUtility.deNull(rs.getString("bl_nbr")));
					unStfValueObj.setUnStuffCloseStatus(CommonUtility.deNull(rs.getString("unstfsts")));
					// unStfValueObj.setBillableParty(CommonUtility.deNull(rs.getString("bill_party_acct_nbr")));
				}

				sb2.append(" select mft_markings from MFT_MARKINGS where MFT_SQ_NBR=:mftseqno ");
				log.info("qry from mft_markings EJB.mftRetrieve().... " + sb2.toString());

				log.info(" ***MftAssignBlParty SQL *****" + sb2.toString());

				paramMap.put("mftseqno", mftseqno);

				rs1 = namedParameterJdbcTemplate.queryForRowSet(sb2.toString(), paramMap);

				if (rs1.next()) {
					unStfValueObj.setCrgMarking(CommonUtility.deNull(rs1.getString("MFT_MARKINGS")));
				}
			} else {
				log.info("Writing from StuffingEJB.assignBillableParty");
				log.info("Invalid Account Nbr" + blPartyAcctNo);
				throw new BusinessException("ErrorMsg_Invalid_AcctNbr");

			}
			log.info("END: *** MftAssignBlParty Result *****" + unStfValueObj.toString());
		} catch (NullPointerException e) {
			log.info("Exception MftAssignBlParty : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception MftAssignBlParty : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception MftAssignBlParty : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: MftAssignBlParty  DAO  END");
		}
		return unStfValueObj;

	} // end of MftAssignBlParty()

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->getBlPartyList()
	/**
	 * This method returns a ArrayList of Bill Party List.
	 * 
	 * @param varnno      Var Number of the Vessel
	 * @param vslInVoynbr Vessel In Voyage Number
	 * @param cntrno      Container Number
	 * @return ArrayList of Bill Party Account Numbers.
	 * @throws BusinessException
	 */
	@Override
	public List<String> getBlPartyList(String varno, String vslInVoynbr, String cntrno) throws BusinessException {
		List<String> blPartyList = new ArrayList<String>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();

		log.info("FROM EJB.getBlPartyList() varno == " + varno + "\tvslInVoynbr == " + vslInVoynbr + "\tcntrno == "
				+ cntrno);

		StringTokenizer cntrtkn = new StringTokenizer(cntrno, "::");
		String cntrNo = cntrtkn.nextToken();
		String seq_bl_no = cntrtkn.nextToken();

		try {
			log.info("START: getBlPartyList  DAO  Start varno:" + varno + "vslInVoynbr:" + vslInVoynbr + "cntrno:"
					+ cntrno);

			sb.append(" select bill_acct_nbr from vessel_call where vv_cd =:varno ");
			sb.append(" and IN_VOY_NBR =:vslInVoynbr ");

			log.info("BILL ACCT NUM sql ... == " + sb.toString());

			log.info(" ***getBlPartyList SQL *****" + sb.toString());

			paramMap.put("varno", varno);
			paramMap.put("vslInVoynbr", vslInVoynbr);
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			String blPartyAcctNo = "";
			String otherBlPartyAcctNo = "";
			int NumOfBls = 0;

			while (rs.next()) {
				blPartyAcctNo = CommonUtility.deNull(rs.getString("bill_acct_nbr"));
			}

			sb1.append(" SELECT BILL_PARTY_ACCT_NBR FROM cc_unstuff_manifest ");
			sb1.append(" WHERE var_nbr=:varno AND active_status='A' AND ");
			sb1.append(" cntr_nbr =:cntrNo AND CNTR_SEQ_BL_NO=:seqBlNo ");

			log.info(" ***getBlPartyList SQL *****" + sb1.toString());

			paramMap.put("varno", varno);
			paramMap.put("cntrNo", cntrNo);
			paramMap.put("seqBlNo", seq_bl_no);

			rs1 = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
			if (rs1.next()) {
				otherBlPartyAcctNo = CommonUtility.deNull(rs1.getString("BILL_PARTY_ACCT_NBR"));
			}

			sb2.append(" select count(*) blcnt from manifest_details where ");
			sb2.append(" var_nbr=:varno  and bl_status = 'A' and unstuff_seq_nbr ");
			sb2.append(" = (select unstuff_seq_nbr from cc_unstuff_manifest where ");
			sb2.append(" var_nbr=:varno and active_status='A' and cntr_nbr = ");
			sb2.append(" :cntrNo and CNTR_SEQ_BL_NO=:seqBlNo) ");

			log.info(" ***getBlPartyList SQL *****" + sb2.toString());

			paramMap.put("varno", varno);
			paramMap.put("cntrNo", cntrNo);
			paramMap.put("seqBlNo", seq_bl_no);
			log.info("paramMap: " + paramMap.toString());
			rs2 = namedParameterJdbcTemplate.queryForRowSet(sb2.toString(), paramMap);
			if (rs2.next()) {
				NumOfBls = rs2.getInt("blcnt");
			}

			blPartyList.add(blPartyAcctNo);
			blPartyList.add("" + NumOfBls);
			blPartyList.add(otherBlPartyAcctNo);
			
			log.info("END: *** getBlPartyList Result *****" + blPartyList.size());
		} catch (NullPointerException e) {
			log.info("Exception getBlPartyList : ", e);
			throw new BusinessException("M4201");		
		} catch (Exception e) {
			log.info("Exception getBlPartyList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBlPartyList  DAO  END");
		}
		return blPartyList;
	}

	// jp.src.ejb.sessionBeans.gbms.containerised.unstuffing-->UnStuffingEJB-->checkAccountNbr()
	/**
	 * Checks the validity of the user entered account number in the Assign Billing
	 * Party screen.
	 * 
	 * @param con     GBMS Connection object.
	 * @param acctNbr account number which has to be checked.
	 * @return boolean 'true' if exist else 'false'.
	 * @throws BusinessException
	 */
	private boolean checkAccountNumber(String acctNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		int acctNbrCount = 0;
		boolean retVal = false;

		try {
			log.info("START: checkAccountNumber  DAO  Start acctNbr:" + acctNbr);

			sb.append(" SELECT COUNT(*) FROM CUST_ACCT A, COMPANY_CODE B, ");
			sb.append(" CUSTOMER C WHERE A.BUSINESS_TYPE LIKE '%G%' AND ");
			sb.append(" A.ACCT_NBR IS NOT NULL AND B.CO_CD=C.CUST_CD AND ");
			sb.append(" A.ACCT_STATUS_CD='A' AND UPPER(A.ACCT_NBR)=UPPER(:acctNbr) ");

			log.info(" ***checkAccountNumber SQL *****" + sb.toString());

			paramMap.put("acctNbr", acctNbr);
			log.info(" ***paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				acctNbrCount = rs.getInt(1);
			}

			if (acctNbrCount > 0) {
				retVal = true;
			} else {
				retVal = false;
			}
			if (log.isInfoEnabled()) {
				log.info("Res: " + retVal + " Count: " + acctNbrCount);
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
		return retVal;
	}

	@Override
	public List<Dept> listCompanyStart(String keyword, Integer start, Integer limit) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		List<Dept> listCompany = new ArrayList<Dept>();
		try {
			log.info("START: listCompanyStart keyword:" + keyword + "start:" + start + "limit:" + limit);
			sb.append(" SELECT co_cd, co_nm || ' (' || co_cd || ')' as coNm ");
			sb.append(" FROM tops.company_code");
			sb.append(" WHERE (co_nm LIKE :company OR co_cd LIKE :company)");
			sb.append(" AND rec_status='A' ");
			sb.append(" ORDER BY co_nm");
			paramMap.put("company", keyword.toUpperCase() + "%");
			listCompany = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<Dept>(Dept.class));
			log.info("SQL" + sb.toString());
			log.info(" *** listCompanyStart params *****" + paramMap.toString());
			log.info("END: *** listCompanyStart Result *****" + listCompany.size());

		} catch (Exception e) {
			log.info("Exception listCompanyStart : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO listCompanyStart");
		}
		return listCompany;
	}
}
