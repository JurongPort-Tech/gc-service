package sg.com.jp.generalcargo.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.GBMSTriggerIndRepository;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("GBMSTriggerIndRepository")
public class GBMSTriggerIndJdbcRepository implements GBMSTriggerIndRepository {

	private static final Log log = LogFactory.getLog(GBMSTriggerIndJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	// jp.src.ejb.sessionBeans.cab.gbEventLog-->GBMSTriggerIndEJB-->updateStuffUnstuffInd()
	// Added by Irene Tan on 5 Jun 2003 : for updating of stuffing/unstuffing
	// indicators in GBMS
	/**
	 * This method updates the stuffing/unstuffing billing indicator in GBMS
	 * cc_unstuff_manifest tables to indicate the charges have been triggered.
	 * 
	 * @param vvCd       vessel call id
	 * @param refNo      reference no
	 * @param refInd     reference indicator
	 * @param userId     user id
	 * @param stuffInd   stuffing/unstuffing charge indicator
	 * @param localLeg   indicate whether the container is an import or export
	 *                   container
	 * @param otherRefNo bl no (GBMS) / container sequence no (CTMS)
	 * @throws BusinessException
	 */
	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void updateStuffUnstuffInd(String vvCd, String refNo, String refInd, String userId, String stuffInd,
			String localLeg, String otherRefNo) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: updateStuffUnstuffInd  DAO  Start Obj vvCd:" + CommonUtility.deNull(vvCd) + "refNo:" + CommonUtility.deNull(refNo)
					+ "refInd:" + CommonUtility.deNull(refInd) + "userId:" + CommonUtility.deNull(userId) + "stuffInd:" + CommonUtility.deNull(stuffInd)
					+ "localLeg:" + CommonUtility.deNull(localLeg) + "otherRefNo:" + CommonUtility.deNull(otherRefNo));

			if (localLeg.trim().equals(ProcessChargeConst.PURP_CD_IMPORT)) {
				// updates the cc_unstuff_manifest table
				sb.append(" update cc_unstuff_manifest set bill_unstuff_triggered_ind=:stuffInd, ");
				sb.append(" last_modify_dttm=sysdate, last_modify_user_id=:userId ");
				sb.append(" where unstuff_closed='Y' and active_status='A' and var_nbr=:vvCd ");
				sb.append(" and cntr_nbr=:refNo and cntr_seq_bl_no=:otherRefNo ");
				
				log.info(" *** updateStuffUnstuffInd SQL *****" + sb.toString());
			} else {
				// added by Irene Tan on 17 September 2003 (JPPL/IT/001/2001 - Phase 2) : to
				// update the stuffing related tables
				// updates the cc_stuffing table
				sb.append(" update cc_stuffing set bill_stuff_triggered_ind=:stuffInd, ");
				sb.append(" last_modify_dttm=sysdate, last_modify_user_id=:userId ");
				sb.append(" where stuff_closed='Y' and active_status='A' and var_nbr=:vvCd ");
				sb.append(" and cntr_nbr=:refNo and cntr_seq_nbr=:otherRefNo ");
				log.info(" *** updateStuffUnstuffInd SQL *****" + sb.toString());
				// end added by Irene Tan on 17 September 2003 (JPPL/IT/001/2001 - Phase 2)
			}
			paramMap.put("stuffInd", stuffInd);
			paramMap.put("userId", userId);
			paramMap.put("vvCd", vvCd);
			paramMap.put("refNo", refNo);
			paramMap.put("otherRefNo", otherRefNo);
			
			log.info("paramMap: " + paramMap);
			int ctr = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			log.info("ctr: " + ctr);
			if (ctr <= 0) {
				throw new BusinessException("M4201");
			}
			
		} catch (NullPointerException e) {
			log.info("Exception updateStuffUnstuffInd : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception updateStuffUnstuffInd : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception updateStuffUnstuffInd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateStuffUnstuffInd  DAO  END");
		}
	}
	
	

	// ejb.sessionBeans.cab.gbEventLog-->GBMSTriggerIndEJB
	// method: updateWarehouseEDOInd()
	/**
	 * This method updates the wharfage & service charge billing indicator in GB_EDO
	 * table to indicate the charges triggered.
	 * 
	 * @param edoAsnNbr  edo_asn number
	 * @param userId     user id
	 * @param wharfInd   wharfage charge indicator
	 * @param serviceInd service charge indicator
	 * 
	 * @author CFG
	 * @throws BusinessException
	 */
	@Override
	public void updateWarehouseEDOInd(String edoAsnNbr, String userId, String wharfInd, String serviceInd)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sql = new StringBuilder();

		try {
			log.info("START: updateWarehouseEDOInd  DAO  Start Obj edoAsnNbr:" + CommonUtility.deNull(edoAsnNbr) + "userId:" + CommonUtility.deNull(userId)
					+ "wharfInd:" + CommonUtility.deNull(wharfInd) + "serviceInd:" + CommonUtility.deNull(serviceInd));
			
			// private final String UPDATE_WAREHOUSE_EDO_IND =
			// "update gb_edo set bill_wharf_triggered_ind=?, bill_service_triggered_ind=?,
			// last_modify_dttm=sysdate, last_modify_user_id=? where edo_asn_nbr=? and
			// edo_status='A'";
			sql.append(" update gb_edo set bill_wharf_triggered_ind=:wharfInd, ");
			sql.append(" bill_service_triggered_ind=:serviceInd, last_modify_dttm=sysdate, ");
			sql.append(" last_modify_user_id=:userId where edo_asn_nbr=:edoAsnNbr and edo_status='A' ");

			paramMap.put("wharfInd", wharfInd);
			paramMap.put("serviceInd", serviceInd);
			paramMap.put("userId", userId);
			paramMap.put("edoAsnNbr", edoAsnNbr);
			log.info(" *** updateWarehouseEDOInd SQL *****" + sql.toString() + " paramMap: " + paramMap);
			int ctr = namedParameterJdbcTemplate.update(sql.toString(), paramMap);
			log.info("WarehouseCharges updateWarehouseEDOInd result: " + ctr);
			
		
		} catch (NullPointerException e) {
			log.info("Exception updateWarehouseEDOInd :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception updateWarehouseEDOInd :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateWarehouseEDOInd  DAO  END");
		}
	}

	// package: ejb.sessionBeans.cab.gbEventLog-->GBMSTriggerIndEJB
		// method: updateGBMSInd()
		/**
		 * This method updates the wharfage, service charge & store rent billing
		 * indicator in GBMS ESN, DN_DETAILS, UA_DETAILS, MANIFEST_DETAILS tables to
		 * indicate the charges have been triggered.
		 * 
		 * @param vvCd       vessel call id
		 * @param refNo      reference no
		 * @param refInd     reference indicator
		 * @param userId     user id
		 * @param wharfInd   wharfage charge indicator
		 * @param serviceInd service charge indicator
		 * @param storeInd   store rent indicator
		 * @exception SQLException
		 * @exception NamingException
		 * @exception Exception
		 */
		// public void updateGBMSInd(String vvCd, String refNo, String refInd, String
		// userId, String wharfInd, String serviceInd) throws SQLException,
		// NamingException, Exception {
		public void updateGBMSInd(String vvCd, String refNo, String refInd, String userId, String wharfInd,
				String serviceInd, String storeInd) throws Exception {
			SqlRowSet rs = null;
			Map<String, String> paramMap = new HashMap<String, String>();
			// Added by Irene Tan on 10 Jan 2003 : To handle the checks on the trigger
			// indicators
			String billWharfInd = "N";
			String billServiceInd = "N";
			// End Added by Irene Tan on 10 Jan 2003
			// Added by Irene Tan on 6 Feb 2003 : to crater the updating of the
			// bill_store_triggered_ind
			String billStoreInd = "N";
			// End Added by Irene Tan on 6 Feb 2003

			StringBuilder updateManifest = new StringBuilder();
			StringBuilder updateUa = new StringBuilder();
			StringBuilder updateDn = new StringBuilder();
			StringBuilder updateEsn = new StringBuilder();
			StringBuilder updateBr = new StringBuilder();
			StringBuilder updateEsnIm = new StringBuilder();
			StringBuilder getEsn = new StringBuilder();
			StringBuilder getEsnIm = new StringBuilder();

			try {
				log.info("START: updateGBMSInd  DAO  Start Obj vvCd:" + CommonUtility.deNull(vvCd) + "refNo:" + CommonUtility.deNull(refNo)
						+ "refInd:" + CommonUtility.deNull(refInd) + "userId:" + CommonUtility.deNull(userId) + "wharfInd:" + CommonUtility.deNull(wharfInd)
						+ "serviceInd:" + CommonUtility.deNull(serviceInd) + "storeInd:" + CommonUtility.deNull(storeInd));

				updateManifest.append(" update manifest_details set bill_wharf_triggered_ind=:wharfInd, ");
				updateManifest.append(" bill_service_triggered_ind=:serviceInd, last_modify_dttm=sysdate, ");
				updateManifest.append(" last_modify_user_id=:userId where bl_nbr=:refNo and var_nbr=:vvCd ");
				updateManifest.append(" and bl_status='A' ");

				updateUa.append(" update ua_details set bill_wharf_triggered_ind=:wharfInd, ");
				updateUa.append(" bill_service_triggered_ind=:serviceInd, last_modify_dttm=sysdate, ");
				updateUa.append(" last_modify_user_id=:userId, bill_store_triggered_ind=:storeInd where ");
				updateUa.append(" ua_nbr=:refNo and ua_status='A' ");

				updateDn.append(" update dn_details set bill_wharf_triggered_ind=:wharfInd, ");
				updateDn.append(" bill_service_triggered_ind=:serviceInd, last_modify_dttm=sysdate, ");
				updateDn.append(" last_modify_user_id=:userId, bill_store_triggered_ind=:storeInd where ");
				updateDn.append(" dn_nbr=:refNo and dn_status='A' ");

				updateEsn.append(" update esn set bill_wharf_triggered_ind=:wharfInd, ");
				updateEsn.append(" bill_service_triggered_ind=:serviceInd, last_modify_dttm=sysdate, ");
				updateEsn.append(" last_modify_user_id=:userId, bill_store_triggered_ind=:storeInd ");
				updateEsn.append(" where esn_asn_nbr=:refNo and esn_status='A' ");

				updateBr.append(" update esn set bill_wharf_triggered_ind=:wharfInd, ");
				updateBr.append(" bill_service_triggered_ind=:serviceInd, last_modify_dttm=sysdate, ");
				updateBr.append(" last_modify_user_id=:userId where bk_ref_nbr=:refNo and esn_status='A' ");

				updateEsnIm.append(" update esn set bill_im_wharf_triggered_ind=:wharfInd, ");
				updateEsnIm.append(" bill_im_service_triggered_ind=:serviceInd, last_modify_dttm=sysdate, ");
				updateEsnIm.append(" last_modify_user_id=:userId, bill_im_store_triggered_ind=:storeInd ");
				updateEsnIm.append(" where esn_asn_nbr=:refNo and esn_status='A' ");

				getEsn.append(" select bill_service_triggered_ind, bill_wharf_triggered_ind, ");
				getEsn.append(" bill_store_triggered_ind from esn where esn_status='A' and esn_asn_nbr=:refNo ");

				getEsnIm.append(" select bill_im_service_triggered_ind, bill_im_wharf_triggered_ind, ");
				getEsnIm.append(" bill_im_store_triggered_ind from esn where esn_status='A' and esn_asn_nbr=:refNo ");

				if (refInd.trim().equals(ProcessChargeConst.REF_IND_BL)) {

					log.info(" *** updateGBMSInd SQL *****" + updateManifest.toString());
					paramMap.put("vvCd", vvCd);
				} else if (refInd.trim().equals(ProcessChargeConst.REF_IND_UA)) {

					log.info(" *** updateGBMSInd SQL *****" + updateUa.toString());
				} else if (refInd.trim().equals(ProcessChargeConst.REF_IND_DN)) {

					log.info(" *** updateGBMSInd SQL *****" + updateDn.toString());
				} else if (refInd.trim().equals(ProcessChargeConst.REF_IND_ESN)) {

					log.info(" *** updateGBMSInd SQL *****" + updateEsn.toString());
					// Added by Irene Tan on 10 Jan 2003 : To handle the checkings on the trigger
					// indicators

					log.info(" *** updateGBMSInd SQL *****" + getEsn.toString());
					paramMap.put("refNo", refNo);
					log.info(" paramMap: " + paramMap);
					rs = namedParameterJdbcTemplate.queryForRowSet(getEsn.toString(), paramMap);
					if (rs.next()) {
						billServiceInd = CommonUtility.deNull(rs.getString("bill_service_triggered_ind"));
						billWharfInd = CommonUtility.deNull(rs.getString("bill_wharf_triggered_ind"));
						// Added by Irene Tan on 6 Feb 2003 : to crater the updating of the
						// bill_store_triggered_ind
						billStoreInd = CommonUtility.deNull(rs.getString("bill_store_triggered_ind"));
						// End Added by Irene Tan on 6 Feb 2003
					}
					// End Added by Irene Tan on 10 Jan 2003
				} else if (refInd.trim().equals(ProcessChargeConst.REF_IND_BR)) {

					log.info(" *** updateGBMSInd SQL *****" + updateBr.toString());
				}
				// Added by Irene Tan on 10 Jan 2003 : To handle the checkings on the trigger
				// indicators & to update the gb_edo trigger indicators
				else if (refInd.trim().equals(ProcessChargeConst.REF_IND_EDO)) {
					// Changed by Irene Tan on 11 Apr 2003 : to crater for multiple TESN under the
					// same EDO
					/*
					 * pstmt = con.prepareStatement(UPDATE_EDO_IND); pstmt1 =
					 * con.prepareStatement(GET_EDO_TRIGGER_IND); pstmt1.setString(1, refNo); prs =
					 * pstmt1.executeQuery(); if (prs.next()) { billServiceInd =
					 * CommonUtility.deNull(prs.getString("bill_service_triggered_ind"));
					 * billWharfInd =
					 * CommonUtility.deNull(prs.getString("bill_wharf_triggered_ind")); // Added by
					 * Irene Tan on 6 Feb 2003 : to crater the updating of the
					 * bill_store_triggered_ind billStoreInd =
					 * CommonUtility.deNull(prs.getString("bill_store_triggered_ind")); // End Added
					 * by Irene Tan on 6 Feb 2003 }
					 */

					log.info(" *** updateGBMSInd SQL *****" + updateEsnIm.toString());

					log.info(" *** updateGBMSInd SQL *****" + getEsnIm.toString());
					paramMap.put("refNo", refNo);
					log.info(" paramMap: " + paramMap);
					rs = namedParameterJdbcTemplate.queryForRowSet(getEsnIm.toString(), paramMap);
					if (rs.next()) {
						billServiceInd = CommonUtility.deNull(rs.getString("bill_im_service_triggered_ind"));
						billWharfInd = CommonUtility.deNull(rs.getString("bill_im_wharf_triggered_ind"));
						billStoreInd = CommonUtility.deNull(rs.getString("bill_im_store_triggered_ind"));
					}
					// end changed by Irene Tan on 11 Apr 2003
					// End Added by Irene Tan on 10 Jan 2003
					
					
				} else {
					String[] tmp = {refInd , refNo};
					throw new Exception(CommonUtil.getErrorMessage(ConstantUtil.errMsg_GBMSTriggerInd_err01, tmp));
				}

				// Added by Irene Tan on 10 Jan 2003 : To handle the checkings on the trigger
				// indicators
				if (billWharfInd.trim().equals("Y")) {
					wharfInd = billWharfInd;
				}

				if (billServiceInd.trim().equals("Y")) {
					serviceInd = billServiceInd;
				}
				// End Added by Irene Tan on 10 Jan 2003

				// Added by Irene Tan on 6 Feb 2003 : to crater the updating of the
				// bill_store_triggered_ind
				if (billStoreInd.trim().equals("Y")) {
					storeInd = billStoreInd;
				}

				paramMap.put("wharfInd", wharfInd);
				paramMap.put("serviceInd", serviceInd);
				paramMap.put("userId", userId);
				// pstmt.setString(4, refNo);
				if (refInd.trim().equals(ProcessChargeConst.REF_IND_BL)) {
					paramMap.put("refNo", refNo);
				} else {
					paramMap.put("storeInd", storeInd);
					paramMap.put("refNo", refNo);
				}
				// End Added by Irene Tan on 6 Feb 2003

				if (refInd.trim().equals(ProcessChargeConst.REF_IND_BL)) {

					paramMap.put("vvCd", vvCd);
					log.info("sql: " + updateManifest.toString());
					log.info(" paramMap: " + paramMap);
					int ctr =namedParameterJdbcTemplate.update(updateManifest.toString(), paramMap);
					log.info("ctr"+ctr);
				} else if (refInd.trim().equals(ProcessChargeConst.REF_IND_UA)) {
					log.info("sql: " + updateUa.toString());
					log.info(" paramMap: " + paramMap);
					int ctr =namedParameterJdbcTemplate.update(updateUa.toString(), paramMap);
					log.info("ctr"+ctr);
				} else if (refInd.trim().equals(ProcessChargeConst.REF_IND_DN)) {
					log.info("sql: " + updateDn.toString());
					log.info(" paramMap: " + paramMap);
					int ctr =namedParameterJdbcTemplate.update(updateDn.toString(), paramMap);
					log.info("ctr"+ctr);
				} else if (refInd.trim().equals(ProcessChargeConst.REF_IND_ESN)) {
					log.info("sql: " + updateEsn.toString());
					log.info(" paramMap: " + paramMap);
					int ctr =namedParameterJdbcTemplate.update(updateEsn.toString(), paramMap);
					log.info("ctr"+ctr);
					// End Added by Irene Tan on 10 Jan 2003
				} else if (refInd.trim().equals(ProcessChargeConst.REF_IND_BR)) {
					log.info("sql: " + updateBr.toString());
					log.info(" paramMap: " + paramMap);
					int ctr =namedParameterJdbcTemplate.update(updateBr.toString(), paramMap);
					log.info("ctr"+ctr);
				}
				// Added by Irene Tan on 10 Jan 2003 : To handle the checkings on the trigger
				// indicators & to update the gb_edo trigger indicators
				else if (refInd.trim().equals(ProcessChargeConst.REF_IND_EDO)) {
					// Changed by Irene Tan on 11 Apr 2003 : to crater for multiple TESN under the
					// same EDO
					/*
					 * pstmt = con.prepareStatement(UPDATE_EDO_IND); pstmt1 =
					 * con.prepareStatement(GET_EDO_TRIGGER_IND); pstmt1.setString(1, refNo); prs =
					 * pstmt1.executeQuery(); if (prs.next()) { billServiceInd =
					 * CommonUtility.deNull(prs.getString("bill_service_triggered_ind"));
					 * billWharfInd =
					 * CommonUtility.deNull(prs.getString("bill_wharf_triggered_ind")); // Added by
					 * Irene Tan on 6 Feb 2003 : to crater the updating of the
					 * bill_store_triggered_ind billStoreInd =
					 * CommonUtility.deNull(prs.getString("bill_store_triggered_ind")); // End Added
					 * by Irene Tan on 6 Feb 2003 }
					 */

					int ctr =namedParameterJdbcTemplate.update(updateEsnIm.toString(), paramMap);
					log.info("ctr"+ctr);
				}

			} catch (NullPointerException e) {
				log.info("Exception updateGBMSInd :" , e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception updateGBMSInd :" , e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: updateGBMSInd  DAO  END");
			}
		}
}
