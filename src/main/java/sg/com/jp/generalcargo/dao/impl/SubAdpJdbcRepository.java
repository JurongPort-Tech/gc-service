package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.SubAdpRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.SubAdpValueObject;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.GbmsCommonUtility;

@Repository("subAdpRepository")
public class SubAdpJdbcRepository implements SubAdpRepository {

	private static final Log log = LogFactory.getLog(SubAdpJdbcRepository.class);
	// ejb.sessionBeans.gbms.cargo.subAdp-->SubAdpEJB

	private static final String param = " paramMap = ";
	private static final String logStatusGlobal = "Y";
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	/**
	 * Method to get get SubADP list
	 * 
	 * @param esnasnnbr
	 * @return Vector
	 * @throws BusinessException
	 */
	public List<SubAdpValueObject> getSubADP(String esnasnnbr, Criteria criteria) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<SubAdpValueObject> subAdpVector = new ArrayList<SubAdpValueObject>();

		sql = "SELECT SUB_ADP_NBR, TRUCKER_IC,TRUCKER_NM,TRUCKER_CONTACT_NBR,STATUS_CD, TRUCKER_CO_CD, TRUCKER_NBR_PKGS  FROM SUB_ADP WHERE esn_asn_nbr = :esnasnnbr Order by SUB_ADP_NBR";

		try {

			log.info("START: getSubADP  DAO  Start esnasnnbr " + CommonUtility.deNull(esnasnnbr) + "criteria" +criteria.toString());

			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql.toString(), criteria.getStart(), criteria.getLimit());
			}

			paramMap.put("esnasnnbr", esnasnnbr);

			log.info(" getSubADP  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				SubAdpValueObject subAdpVO = new SubAdpValueObject();

				subAdpVO.setCo_Cd(CommonUtility.deNull(rs.getString("TRUCKER_CO_CD")));
				subAdpVO.setSubAdp_nbr(CommonUtility.deNull(rs.getString("SUB_ADP_NBR")));
				subAdpVO.setTruck_ic(CommonUtility.deNull(rs.getString("TRUCKER_IC")));
				subAdpVO.setTruck_nm(CommonUtility.deNull(rs.getString("TRUCKER_NM")));
				subAdpVO.setContact_no(CommonUtility.deNull(rs.getString("TRUCKER_CONTACT_NBR")));
				subAdpVO.setStatus(CommonUtility.deNull(rs.getString("STATUS_CD")));
				// HaiTTH1 added on 27/2/2014
				subAdpVO.setTruck_pkgs(CommonUtility.deNull(rs.getString("TRUCKER_NBR_PKGS")));

				subAdpVector.add(subAdpVO);
			}

			log.info(" getSubADP  DAO  Result" + subAdpVector.toString());
	
		} catch (NullPointerException e) {
			log.info("Exception getSubADP : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSubADP : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSubADP  DAO  END");

		}
		return subAdpVector;

	}

	public boolean checkEsnExist(String esnasnnbr) throws BusinessException {

		boolean exist = false;
		String sql = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		sql = "select * from ESN where ESN.ESN_STATUS = 'A'  AND esn_asn_nbr = :esnasnnbr ";

		try {
			log.info("START: checkEsnExist  DAO  Start esnasnnbr " + CommonUtility.deNull(esnasnnbr));

			paramMap.put("esnasnnbr", esnasnnbr);

			log.info(" checkEsnExist  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				exist = true;
			}

			if (exist == false)
				throw new BusinessException(ConstantUtil.ErrorMsg_Invalid_ESNNbr); // ESN number is not correct.

			log.info(" checkEsnExist  DAO  Result" + exist);
	
		} catch (NullPointerException e) {
			log.info("Exception checkEsnExist : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception checkEsnExist : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception checkEsnExist : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkEsnExist  DAO  END");
		}
		return exist;
	}

	/**
	 * Method to get Delete SubADP
	 * 
	 * @param subAdpNbr_Vector
	 * @param userId
	 * @param status_Cd_Vector
	 * @param trucker_CoCd_Vector
	 * @param trucker_Nm_Vector
	 * @param trucker_Ic_Vector
	 * @param trucker_Contact_Nbr_Vector
	 * @throws BusinessException
	 */
	public void delADPForDPE(List<String> subAdpNbr_Vector, String userId, List<String> status_Cd_Vector,
			List<String> trucker_CoCd_Vector, List<String> trucker_Nm_Vector, List<String> trucker_Ic_Vector,
			List<String> trucker_Contact_Nbr_Vector, List<String> trucker_nbr_pkg_Vector) throws BusinessException {

		int count_adp = subAdpNbr_Vector.size();
		log.info("Num of subAdpNbrVector: " + count_adp);

		// -- Check for Delete list of DocSubAuthor then Init Set of custcd

		Map<String, Object> paramMap = new HashMap<String, Object>();
		for (int i = 0; i < count_adp; i++) {
			int subAdpNbr = Integer.parseInt((String) subAdpNbr_Vector.get(i));
			String status_Cd = (String) status_Cd_Vector.get(i);
			String trucker_CoCd = (String) trucker_CoCd_Vector.get(i);
			String trucker_Nm = (String) trucker_Nm_Vector.get(i);
			String trucker_Ic = (String) trucker_Ic_Vector.get(i);
			String trucker_Contact_Nbr = (String) trucker_Contact_Nbr_Vector.get(i);
			// haiTTh1 added on 27/2/2014
			String trucker_nbr_pkg = (String) trucker_nbr_pkg_Vector.get(i);
			// haiTTh1 ended on 27/2/2014

			log.info("subAdpNbr: " + subAdpNbr + "// status_Cd: " + status_Cd + "// trucker_CoCd: " + trucker_CoCd
					+ "// trucker_Nm: " + trucker_Nm + "// trucker_Ic: " + trucker_Ic + "// trucker_Contact_Nbr: "
					+ trucker_Contact_Nbr);

			try {
				log.info("START: delADPForDPE  DAO  Start subAdpNbr_Vector " + subAdpNbr_Vector + " userId" + userId
						+ " status_Cd_Vector" + status_Cd_Vector + " trucker_CoCd_Vector" + trucker_CoCd_Vector
						+ " trucker_Nm_Vector" + trucker_Nm_Vector + " trucker_Ic_Vector" + trucker_Ic_Vector
						+ " trucker_Contact_Nbr_Vector" + trucker_Contact_Nbr_Vector + " trucker_nbr_pkg_Vector"
						+ trucker_nbr_pkg_Vector);
                // START - Added on 18012023: Update code to follow class code - NS
				StringBuilder sqlDel = new StringBuilder();
				sqlDel.append("UPDATE sub_adp SET status_cd='X', last_modify_user_id=:userId, last_modify_dttm=SYSDATE WHERE sub_adp_nbr=:subAdpNbr ");
				// END - Added on 18012023: Update code to follow class code - NS
				paramMap.put("subAdpNbr", subAdpNbr);
				paramMap.put("userId", userId);
				log.info("Sql --> delADPForDPE " + sqlDel + param + paramMap);
				int count = namedParameterJdbcTemplate.update(sqlDel.toString(), paramMap);
				log.info("AFTER DEL ADP: " + count);

				if (count == 0) {
					throw new BusinessException(ConstantUtil.ErrorMsg_Delete_Failed);
					
				} else { // Del susses, insert SUB_ADP_TXN
					log.info("before updateSubAdpTxn: " + subAdpNbr + "// status_Cd: " + status_Cd + "// trucker_CoCd: "
							+ trucker_CoCd + "// trucker_Nm: " + trucker_Nm + "// trucker_Ic: " + trucker_Ic
							+ "// trucker_Contact_Nbr: " + trucker_Contact_Nbr + "// trucker_nbr_pkg: "
							+ trucker_nbr_pkg);
					// START - Added on 18012023: Update status to 'X' after delete transaction success - NS
					updateSubAdpTxnForDPE(subAdpNbr, userId, "X", trucker_CoCd, trucker_Nm, trucker_Ic,
							trucker_Contact_Nbr, trucker_nbr_pkg); // HaiTTH1 modified on 26/2/2014
					// END - Added on 18012023: Update status to 'X' after delete transaction success - NS
				}
		
			} catch (NullPointerException e) {
				log.info("Exception delADPForDPE : " , e);
				throw new BusinessException("M4201");
			} catch (BusinessException e) {
				log.info("Exception delADPForDPE : " , e);
				throw new BusinessException(e.getMessage());
			} catch (Exception e) {
				log.info("Exception delADPForDPE : " , e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: delADPForDPE  DAO  END");

			}
		}
	}

	/**
	 * Method to get number of SubADP
	 * 
	 * @param con
	 * @return int
	 * @throws BusinessException
	 */
	private int getAdpNo() throws BusinessException {
		int adpNo = 0;
		int max = 0;
		String sql = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getAdpNo  DAO  Start  ");
			sql = "SELECT COUNT(SUB_ADP_NBR) FROM SUB_ADP";

			log.info(" getAdpNo  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				max = rs.getInt("COUNT(SUB_ADP_NBR)");
			}

			if (max == 0) {
				adpNo = 1;
			} else {
				sql = "SELECT MAX(TO_NUMBER(SUB_ADP_NBR)) FROM SUB_ADP";
				log.info(" getAdpNo  DAO  SQL " + sql);
				log.info(param + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

				if (rs.next()) {
					adpNo = rs.getInt("MAX(TO_NUMBER(SUB_ADP_NBR))");
					// START - Added on 18012023: Update code to follow class code - NS
					adpNo++;
					// END - Added on 18012023: Update code to follow class code - NS
				}
			}

			log.info(" getAdpNo  DAO  Result" + adpNo);
			return adpNo;

	
		} catch (NullPointerException e) {
			log.info("Exception getAdpNo : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getAdpNo : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAdpNo  DAO  END");

		}
	}

	/**
	 * Method to get Update SubADP
	 * 
	 * @param subAdpNbr
	 * @param creat_userID
	 * @param status_Cd
	 * @param trucker_CoCd
	 * @param trucker_Nm
	 * @param trucker_Ic
	 * @param trucker_Contact_Nbr
	 * @throws BusinessException
	 */
	public void updateSubAdpTxnForDPE(int subAdpNbr, String creat_userID, String status_Cd, String trucker_CoCd,
			String trucker_Nm, String trucker_Ic, String trucker_Contact_Nbr, String trucker_Nbr_Pkgs)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();

		// START - Added on 18012023: Update code to follow class code - NS
		String SQL_INSERT_SUBADP_TXN = "INSERT INTO sub_adp_txn (sub_adp_nbr, txn_dttm, txn_user_id, status_cd, trucker_co_cd, trucker_nm, trucker_ic, trucker_contact_nbr, edo_esn_ind, trucker_nbr_pkgs) VALUES "
				+ "(:subAdpNbr,SYSDATE,:creat_userID,:status_Cd,:trucker_CoCd,:trucker_Nm,:trucker_Ic,:trucker_Contact_Nbr,:edo_esn_ind,:trucker_Nbr_Pkgs)";
		// END - Added on 18012023: Update code to follow class code - NS
		try {
			log.info("START: updateSubAdpTxnForDPE  DAO  Start subAdpNbr " + subAdpNbr + " creat_userID " + CommonUtility.deNull(creat_userID)
					+ " status_Cd " + CommonUtility.deNull(status_Cd) + " trucker_CoCd" + CommonUtility.deNull(trucker_CoCd) + " trucker_Nm " + CommonUtility.deNull(trucker_Nm)
					+ " trucker_Ic " + CommonUtility.deNull(trucker_Ic) + " trucker_Contact_Nbr" + CommonUtility.deNull(trucker_Contact_Nbr) + " trucker_Nbr_Pkgs "
					+ CommonUtility.deNull(trucker_Nbr_Pkgs));

			// int misNo = getMisNo(con);
			
			paramMap.put("subAdpNbr", subAdpNbr);
			paramMap.put("creat_userID", creat_userID);
			paramMap.put("status_Cd", status_Cd);
			paramMap.put("trucker_CoCd", trucker_CoCd);
			paramMap.put("trucker_Nm", trucker_Nm);
			paramMap.put("trucker_Ic", trucker_Ic);
			paramMap.put("trucker_Contact_Nbr", trucker_Contact_Nbr);
			paramMap.put("edo_esn_ind", "0");
			paramMap.put("trucker_Nbr_Pkgs", Integer.parseInt(trucker_Nbr_Pkgs));
			log.info(" updateSubAdpTxnForDPE  DAO  SQL " + SQL_INSERT_SUBADP_TXN);
			log.info(param + paramMap);
			log.info("SQL: " + SQL_INSERT_SUBADP_TXN + " [" + subAdpNbr + "|" + creat_userID + "|" + status_Cd + "|" + trucker_CoCd + "|" + trucker_Nm + "|" + trucker_Ic + "|" + trucker_Contact_Nbr + "|0|" + trucker_Nbr_Pkgs);

			int count = namedParameterJdbcTemplate.update(SQL_INSERT_SUBADP_TXN, paramMap);

			log.info("Result: " + count);
			if (count == 0) {
				log.info("Writing from SubAdpEJB.updateSubAdpTxn");
				log.info("Record Cannot be added to Database");
				throw new BusinessException(ConstantUtil.ErrorMsg_NotAdded_DB);
			}
			log.info("END: *** updateSubAdpTxnForDPE Result *****" + count);
		} catch (NullPointerException e) {
			log.info("Exception updateSubAdpTxnForDPE : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception updateSubAdpTxnForDPE : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception updateSubAdpTxnForDPE : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateSubAdpTxnForDPE  DAO  END");
		}
	}

	// START: Added on 06012023 : SubAdp.jar --> ejb.sessionBeans.gbms.cargo.subAdp; --> SubAdpEJB --> creatMultiTruckers() - NS
	public void creatMultiTruckers(String esnNbr, List<TruckerValueObject> truckerList, String creat_userID,
			String status_Cd, String totPkg_s) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		String transNumEsnStr = "";
		int transNumEsnInt = 0;
		String SQL_CREATE_SUBADP = "INSERT INTO sub_adp (sub_adp_nbr, esn_asn_nbr, status_cd, trucker_co_cd, trucker_nm, trucker_ic, trucker_contact_nbr, create_user_id, create_dttm, last_modify_user_id, "
				+ "last_modify_dttm, trucker_nbr_pkgs, edo_esn_ind) "
				+ "VALUES(:SUB_ADP_NBR,:ESN_ASN_NBR,:STATUS_CD,:TRUCKER_CO_CD,:TRUCKER_NM,:TRUCKER_IC,:TRUCKER_CONTACT_NBR,:CREATE_USER_ID,sysdate,:CREATE_USER_ID,sysdate,:TRUCKER_NBR_PKGS,:EDO_ESN_IND)";
		try {
			log.info("START: creatMultiTruckers  DAO  Start esnNbr " + CommonUtility.deNull(esnNbr) + " truckerList" + truckerList.toString()
					+ " creat_userID " + CommonUtility.deNull(creat_userID) + " status_Cd " + CommonUtility.deNull(status_Cd) + " totPkg_s " + CommonUtility.deNull(totPkg_s));

			if (truckerList.size() == 0) {
		        log.info("trucker size 0 : no multi-trucker creation required");
		        log.info("trucker size 0 : no multi-trucker creation required");
		        return;
		     } 

            String sqlTrans = "SELECT MAX(TRANS_NBR) FROM ESN_DETAILS_TRANS WHERE ESN_ASN_NBR =:esnNo ";
            if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y") {
            	paramMap.put("esnNo", esnNbr);
            	log.info("creatMultiTruckers sqlTrans SQL 1: " + sqlTrans);
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqlTrans, paramMap);
				while (rs.next()) {
					transNumEsnStr = rs.getString(1);
				}

				if (transNumEsnStr == null || transNumEsnStr == "") {
					transNumEsnInt = 0;
				} else {
					transNumEsnInt = Integer.parseInt(transNumEsnStr);
					transNumEsnInt++;
				}
            }
            
            String adpnbr = null;
			String truckerIc = "";
			String truckerNm = "";
			String truckerPkgs = null;
			String truckerContact = "";

            if (truckerList != null && truckerList.size() > 0) {
            	TruckerValueObject trkObj = truckerList.get(0);
				if (trkObj != null) {
					adpnbr = trkObj.getTruckerCd();
					truckerIc = trkObj.getTruckerIc();
					truckerNm = trkObj.getTruckerNm();
					truckerPkgs = trkObj.getTruckerPkgs();
					truckerContact = trkObj.getTruckerContact();
				}
			}
            
        	sb.setLength(0);
        	sb.append(
					"INSERT INTO ESN_DETAILS_Trans(ESN_ASN_NBR,TRANS_NBR,");
			sb.append("TRUCKER_NM,TRUCKER_IC, ");
			sb.append("NBR_PKGS,TRUCKER_PHONE_NBR,HA_CUST_CD, ");
			sb.append("LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
			sb.append("VALUES(:esnNo,:transNumEsnInt, ");
			sb.append(":truckerName,:truckerNo,");
			sb.append(":noOfPkgs, ");
			sb.append(":truckerCNo,:truckerCd,:strUserID,sysdate) ");
			String strEsnDetailsTrans = sb.toString();
			
			if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y") {
				paramMap.put("esnNo", esnNbr);
				paramMap.put("transNumEsnInt", transNumEsnInt);
				paramMap.put("truckerName", truckerNm.trim());
				paramMap.put("truckerNo", truckerIc.trim());
				paramMap.put("noOfPkgs", totPkg_s);
				paramMap.put("truckerCNo", truckerContact);
				paramMap.put("truckerCd", adpnbr);
				paramMap.put("strUserID", creat_userID);
				log.info("esnUpdateForDPE strEsnDetailsTrans SQL: " + strEsnDetailsTrans);
				log.info("strEsnDetailsTrans paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strEsnDetailsTrans, paramMap);
			}
			
			sb.setLength(0);
			
			sb.append("UPDATE esn_details SET NBR_PKGS=:noOfPkgs, ");
			sb.append("TRUCKER_NM=:trckrNm, ");
			sb.append("TRUCKER_NBR_PKGS=:truckerNbrPkgs, TRUCKER_IC=:trckIc, ");
			sb.append("HA_CUST_CD=:truckerCd,TRUCKER_PHONE_NBR=:truckerCNo ");
			sb.append("WHERE ");
			sb.append("ESN_ASN_NBR=:esnNo ");
			String strUpdate = sb.toString();
			
			paramMap.clear();
			paramMap.put("esnNo", esnNbr);
			paramMap.put("trckrNm", truckerNm.trim());
			paramMap.put("trckIc", truckerIc.trim());
			paramMap.put("truckerNbrPkgs", truckerPkgs);
			paramMap.put("noOfPkgs", totPkg_s);
			paramMap.put("truckerCNo", truckerContact);
			paramMap.put("truckerCd", adpnbr);
			log.info("creatMultiTruckers strUpdate SQL: " + strUpdate);
			log.info(" strUpdate paramMap: " + paramMap);
			int count = namedParameterJdbcTemplate.update(strUpdate, paramMap);
			log.info("creatMultiTruckers strUpdate count: " + count);
			
			String strDeleteAdpTxn = "DELETE FROM sub_adp_txn WHERE sub_adp_nbr IN (SELECT sub_adp_nbr FROM sub_adp WHERE esn_asn_nbr=" + esnNbr + " AND (edo_esn_ind=0 OR edo_esn_ind IS NULL) AND status_cd='A') ";
			log.info("SQL: " + strDeleteAdpTxn);
			log.info("SQL: " + strDeleteAdpTxn);
			int delTxnCnt = namedParameterJdbcTemplate.update(strDeleteAdpTxn, paramMap);
			log.info("Result: " + delTxnCnt);
			log.info("Result: " + delTxnCnt);
			String strDeleteADP = "DELETE FROM sub_adp WHERE esn_asn_nbr=" + esnNbr + " AND (edo_esn_ind=0 OR edo_esn_ind IS NULL) AND status_cd='A' ";
			log.info("SQL: " + strDeleteADP);
			log.info("SQL: " + strDeleteADP);
			int count1 = namedParameterJdbcTemplate.update(strDeleteADP, paramMap);
			log.info("Result: " + count1);
		    log.info("Result: " + count1);

			for (int i = 0; i < truckerList.size(); i++) {
				TruckerValueObject trkObj = new TruckerValueObject();
				trkObj = truckerList.get(i);
				int adpNo = getAdpNo();
				  
				log.info(SQL_CREATE_SUBADP);
				paramMap.put("SUB_ADP_NBR", adpNo);
				paramMap.put("ESN_ASN_NBR", esnNbr);
				paramMap.put("STATUS_CD", status_Cd);
				paramMap.put("TRUCKER_CO_CD", trkObj.getTruckerCd());
				paramMap.put("TRUCKER_NM", trkObj.getTruckerNm());
				paramMap.put("TRUCKER_IC", trkObj.getTruckerIc());
				paramMap.put("TRUCKER_CONTACT_NBR", trkObj.getTruckerContact());
				paramMap.put("CREATE_USER_ID", creat_userID);
				paramMap.put("CREATE_USER_ID", creat_userID);
				paramMap.put("TRUCKER_NBR_PKGS", Integer.parseInt(trkObj.getTruckerPkgs()));
				paramMap.put("EDO_ESN_IND", "0");
				log.info("SQL: " + SQL_CREATE_SUBADP);
				log.info("SQL: " + SQL_CREATE_SUBADP);
				int count3 = namedParameterJdbcTemplate.update(SQL_CREATE_SUBADP, paramMap);
				log.info("result: " + count3);
				log.info("result: " + count3);
				if (count3 == 0) {
					log.info("Writing from SubAdpEJB.MftCancel");
					log.info("Record Cannot be added to Database");
				} else {
					updateSubAdpTxnForDPE(adpNo, creat_userID, status_Cd, trkObj.getTruckerCd(), trkObj.getTruckerNm(), trkObj.getTruckerIc(), trkObj.getTruckerContact(), trkObj.getTruckerPkgs());
				} 
			}  
		    // END: Added on 06012023 : SubAdp.jar --> ejb.sessionBeans.gbms.cargo.subAdp; --> SubAdpEJB --> creatMultiTruckers() - NS
		} catch (NullPointerException e) {
			log.info("Exception creatMultiTruckers : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception creatMultiTruckers : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception creatMultiTruckers : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: creatMultiTruckers  DAO  END");

		}
	}
	
	public String getTruckerCdByTruckerIcNo(String trcIcNo) throws BusinessException {
		String truckerIcNo = trcIcNo;
		String sql;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String truckerCd = "";

		sql = "select cust.cust_cd from company_code cocode,customer cust where cust.cust_cd= cocode.co_cd and (upper(tdb_cr_nbr)= :truckerIcNo OR upper(uen_nbr) = :truckerIcNo) order by cocode.rec_status desc, (case when cocode.lob_cd = 'HAU' then 2 else 1 end)";

		try {
			log.info("START: getTruckerCdByTruckerIcNo  DAO  Start trcIcNo " + CommonUtility.deNull(trcIcNo));

			paramMap.put("truckerIcNo", GbmsCommonUtility.addApostr(truckerIcNo));

			log.info(" getTruckerCdByTruckerIcNo  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			truckerCd = "";
			if (rs.next()) {
				truckerCd = rs.getString("cust_cd");
				log.info("*********Writing from EsnEJB.getTruckerCd --truckerCd: " + CommonUtility.deNull(truckerCd));
			} else {
				truckerCd = "NA";

			}
			log.info(" getTruckerCdByTruckerIcNo  DAO  Result" + CommonUtility.deNull(truckerCd));
	
		} catch (NullPointerException e) {
			log.info("Exception getTruckerCdByTruckerIcNo : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTruckerCdByTruckerIcNo : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTruckerCdByTruckerIcNo  DAO  END");
			
		}
		return truckerCd;
	}

	public List<TruckerValueObject> getTruckerList(String esnNbr) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();

		sql = "select * from SUB_ADP where esn_asn_nbr = :esnNbr and (edo_esn_ind = 0 or edo_esn_ind is null) and status_cd = 'A' ";

		List<TruckerValueObject> truckerList = new ArrayList<TruckerValueObject>();

		try {
			log.info("START: getTruckerList  DAO  Start esnNbr " + CommonUtility.deNull(esnNbr));

			paramMap.put("esnNbr", esnNbr);

			log.info(" getTruckerList  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				TruckerValueObject truckerValueObject = new TruckerValueObject();
				truckerValueObject.setTruckerIc(CommonUtility.deNull(rs.getString("TRUCKER_IC")));
				truckerValueObject.setTruckerNm(CommonUtility.deNull(rs.getString("TRUCKER_NM")));
				truckerValueObject.setTruckerContact(CommonUtility.deNull(rs.getString("TRUCKER_CONTACT_NBR")));
				truckerValueObject.setTruckerPkgs(CommonUtility.deNull(rs.getString("TRUCKER_NBR_PKGS")));
				truckerValueObject.setTruckerCd(CommonUtility.deNull(rs.getString("TRUCKER_CO_CD")));
				truckerList.add(truckerValueObject);
			}
			log.info(" getTruckerList  DAO  Result" + truckerList.size());
	
		} catch (NullPointerException e) {
			log.info("Exception getTruckerList : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTruckerList : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTruckerList  DAO  END");
		}
		return truckerList;

	}

	public TruckerValueObject getTruckerDetails(String truckerIc) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append("	CO_NM, ");
		sb.append("	NVL(PHONE1_NBR, PHONE2_NBR) PHONE1_NBR ");
		sb.append("FROM ");
		sb.append("	customer cust ");
		sb.append("LEFT JOIN company_code cc ON ");
		sb.append("	cust.cust_cd = cc.co_cd ");
		sb.append("LEFT JOIN cust_contact ct ON ");
		sb.append("	cust.cust_cd = ct.CUST_CD ");
		sb.append("WHERE ");
		sb.append("	TDB_CR_NBR = :truckerIc ");
		sb.append("	OR UEN_NBR = :truckerIc");

		sql = sb.toString();

		StringBuffer sb1 = new StringBuffer();
		sb1.append("SELECT ");
		sb1.append("	DISTINCT(JC.CUST_NAME) CO_NM, ");
		sb1.append("	JC.CUST_CD CO_CD, ");
		sb1.append("	NVL(CT.PHONE1_NBR, CT.PHONE2_NBR) PHONE1_NBR, ");
		sb1.append("	CA.ADD_L1 ");
		sb1.append("FROM ");
		sb1.append("	JC_CARDDTL JC ");
		sb1.append("LEFT JOIN CUST_CONTACT CT ON ");
		sb1.append("	JC.CUST_CD = CT.CUST_CD ");
		sb1.append("LEFT JOIN CUST_ADDRESS CA ON ");
		sb1.append("	JC.CUST_CD = CA.CUST_CD ");
		sb1.append("WHERE ");
		sb1.append("	UPPER(jc.passport_no) = :truckerIc ");
		sb1.append("	OR UPPER(jc.FIN_NO)= :truckerIc ");
		sb1.append("	OR UPPER(jc.NRIC_NO)= :truckerIc");

		String sql2 = sb1.toString();

		TruckerValueObject truckerValueObject = null;
		try {
			log.info("START: getTruckerDetails  DAO  Start truckerIc " + CommonUtility.deNull(truckerIc));

			paramMap.put("truckerIc", truckerIc);

			log.info(" getTruckerDetails  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			truckerValueObject = new TruckerValueObject();
			truckerValueObject.setTruckerIc(truckerIc);
			if (rs.next()) {
				truckerValueObject.setTruckerNm(CommonUtility.deNull(rs.getString("CO_NM")));
				truckerValueObject.setTruckerContact(CommonUtility.deNull(rs.getString("PHONE1_NBR")));
			} else {
				paramMap.put("truckerIc", truckerIc);

				log.info(" getTruckerDetails  DAO  SQL " + sql2);
				log.info(param + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);
				if (rs.next()) {
					truckerValueObject.setTruckerNm(CommonUtility.deNull(rs.getString("CO_NM")));
					truckerValueObject.setTruckerContact(CommonUtility.deNull(rs.getString("PHONE1_NBR")));
				}
			}
			log.info(" getTruckerDetails  DAO  Result" + truckerValueObject.toString());
		
		} catch (NullPointerException e) {
			log.info("Exception getTruckerDetails : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTruckerDetails : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTruckerDetails  DAO  END");
			
		}
		return truckerValueObject;
	}

	/**
	 * Method to get Esn Detail
	 * 
	 * @param esnasnnbr
	 * @param esnTransType
	 * @param coCode
	 * @param userId
	 * @return UaEsnDetValueObject
	 * @throws BusinessException
	 */
	public UaEsnDetValueObject getEsnDetail(String esnasnnbr, String esnTransType, String coCode, String userId)
			throws BusinessException {

		String sqlE = "";
		String sqlS = "";
		String sqlE_JP = "";
		String sqlS_JP = "";
		String sql = "";
		boolean isSqlE_JP = false;
		boolean issqlE = false;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// Vector esnList = new Vector(0, 1);
		UaEsnDetValueObject esnObj = new UaEsnDetValueObject();
		int count = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append("	DISTINCT vsl_nm, ");
		sb.append("	vessel_call.out_voy_nbr, ");
		sb.append("	to_char(ETB_DTTM, 'ddmmyyyy hh24mi') AS etb, ");
		sb.append("	to_char(atb_dttm, 'ddmmyyyy hh24mi') AS atb, ");
		sb.append("	to_char(atu_dttm, 'ddmmyyyy hh24mi') AS atu, ");
		sb.append("	esn.esn_asn_nbr, ");
		sb.append("	esn_details.acct_nbr, ");
		sb.append("	esn.bk_ref_nbr, ");
		sb.append("	esn_details.crg_des, ");
		sb.append("	markings , ");
		sb.append("	esn_details.nbr_pkgs AS TotalPkg, ");
		sb.append("	esn_details.nbr_pkgs-esn_details.ua_nbr_pkgs AS BalancePkg, ");
		sb.append("	esn_details.TRUCKER_PHONE_NBR, esn_details.TRUCKER_IC, ");
		sb.append("	esn_details.TRUCKER_NM, esn_details.TRUCKER_NBR_PKGS, ");
		sb.append("	vessel_call.terminal, ");
		sb.append("	vessel_Call.scheme, ");
		sb.append("	vessel_call.COMBI_GC_SCHEME , ");
		sb.append("	vessel_call.COMBI_GC_OPS_IND ");
		sb.append("FROM ");
		sb.append("	esn , ");
		sb.append("	vessel_call, ");
		sb.append("	berthing , ");
		sb.append("	bk_details , ");
		sb.append("	esn_markings , ");
		sb.append("	esn_details ");
		sb.append("WHERE ");
		sb.append("	esn.esn_asn_nbr = esn_details.esn_asn_nbr ");
		sb.append("	AND esn.out_voy_var_nbr = vessel_call.vv_cd ");
		sb.append("	AND esn.esn_asn_nbr = esn_markings.esn_asn_nbr ");
		sb.append("	AND vessel_call.vv_cd = berthing.vv_cd ");
		sb.append("	AND esn.bk_ref_nbr = bk_details.bk_ref_nbr ");
		sb.append("	AND shift_ind = 1 ");
		sb.append("	AND esn.TRANS_CRG <> 'Y' ");
		sb.append("	AND esn.esn_asn_nbr = :esnasnnbr");

		sqlE_JP = sb.toString();

		StringBuffer sb1 = new StringBuffer();
		sb1.append("SELECT ");
		sb1.append("	VSL_NM, ");
		sb1.append("	V.OUT_VOY_NBR, ");
		sb1.append("	to_char(ETB_DTTM, 'ddmmyyyy hh24mi') AS etb, ");
		sb1.append("	to_char(atb_dttm, 'ddmmyyyy hh24mi') AS atb, ");
		sb1.append("	to_char(atu_dttm, 'ddmmyyyy hh24mi') AS atu, ");
		sb1.append("	E.ESN_ASN_NBR, ");
		sb1.append("	ED.ACCT_NBR, ");
		sb1.append("	E.bk_ref_nbr, ");
		sb1.append("	ED.CRG_DES AS CRG_DES, ");
		sb1.append("	EM.MARKINGS, ");
		sb1.append("	ED.NBR_PKGS AS TotalPkg, ");
		sb1.append("	ED.NBR_PKGS-ED.UA_NBR_PKGS AS BalancePkg, ");
		sb1.append("	V.TERMINAL, ");
		sb1.append("	V.SCHEME, ");
		sb1.append("	V.COMBI_GC_SCHEME , ");
		sb1.append("	V.COMBI_GC_OPS_IND ");
		sb1.append("FROM ");
		sb1.append("	ESN E, ");
		sb1.append("	ESN_MARKINGS EM, ");
		sb1.append("	vessel_call V, ");
		sb1.append("	berthing, ");
		sb1.append("	SS_DETAILS ED ");
		sb1.append("WHERE ");
		sb1.append("	TRANS_TYPE = 'S' ");
		sb1.append("	AND E.ESN_ASN_NBR = ED.ESN_ASN_NBR ");
		sb1.append("	AND E.OUT_VOY_VAR_NBR = V.vv_cd ");
		sb1.append("	AND V.vv_cd = berthing.vv_cd ");
		sb1.append("	AND E.TRANS_CRG <> 'Y' ");
		sb1.append("	AND SHIFT_IND = 1 ");
		sb1.append("	AND EM.ESN_ASN_NBR = E.ESN_ASN_NBR ");
		sb1.append("	AND E.esn_asn_nbr = :esnasnnbr");

		sqlS_JP = sb1.toString();
		StringBuffer sb2 = new StringBuffer();
		sb2.append("SELECT ");
		sb2.append("	DISTINCT vsl_nm, ");
		sb2.append("	vessel_call.out_voy_nbr, ");
		sb2.append("	to_char(ETB_DTTM, 'ddmmyyyy hh24mi') AS etb, ");
		sb2.append("	to_char(atb_dttm, 'ddmmyyyy hh24mi') AS atb, ");
		sb2.append("	to_char(atu_dttm, 'ddmmyyyy hh24mi') AS atu, ");
		sb2.append("	esn.esn_asn_nbr, ");
		sb2.append("	esn_details.acct_nbr, ");
		sb2.append("	esn.bk_ref_nbr, ");
		sb2.append("	esn_details.crg_des, ");
		sb2.append("	markings , ");
		sb2.append("	esn_details.nbr_pkgs AS TotalPkg, ");
		sb2.append("	esn_details.nbr_pkgs-esn_details.ua_nbr_pkgs AS BalancePkg, ");
		sb2.append("	esn_details.TRUCKER_PHONE_NBR, esn_details.TRUCKER_IC, ");
		sb2.append("	esn_details.TRUCKER_NM, esn_details.TRUCKER_NBR_PKGS, ");
		sb2.append("	vessel_call.terminal, ");
		sb2.append("	vessel_Call.scheme, ");
		sb2.append("	vessel_call.COMBI_GC_SCHEME , ");
		sb2.append("	vessel_call.COMBI_GC_OPS_IND ");
		sb2.append("FROM ");
		sb2.append("	esn , ");
		sb2.append("	vessel_call, ");
		sb2.append("	berthing , ");
		sb2.append("	bk_details , ");
		sb2.append("	esn_markings , ");
		sb2.append("	esn_details ");
		sb2.append("JOIN LOGON_ACCT ON ");
		sb2.append("	ESN_DETAILS.HA_CUST_CD = LOGON_ACCT.CUST_CD ");
		sb2.append("	AND LOGON_ACCT.LOGIN_ID = :userId ");
		sb2.append("WHERE ");
		sb2.append("	esn.esn_asn_nbr = esn_details.esn_asn_nbr ");
		sb2.append("	AND esn.out_voy_var_nbr = vessel_call.vv_cd ");
		sb2.append("	AND esn.esn_asn_nbr = esn_markings.esn_asn_nbr ");
		sb2.append("	AND vessel_call.vv_cd = berthing.vv_cd ");
		sb2.append("	AND esn.bk_ref_nbr = bk_details.bk_ref_nbr ");
		sb2.append("	AND shift_ind = 1 ");
		sb2.append("	AND esn.TRANS_CRG <> 'Y' ");
		sb2.append("	AND esn.esn_asn_nbr = :esnasnnbr");

		sqlE = sb2.toString();
		StringBuffer sb3 = new StringBuffer();
		sb3.append("SELECT ");
		sb3.append("	VSL_NM, ");
		sb3.append("	V.OUT_VOY_NBR, ");
		sb3.append("	to_char(ETB_DTTM, 'ddmmyyyy hh24mi') AS etb, ");
		sb3.append("	to_char(atb_dttm, 'ddmmyyyy hh24mi') AS atb, ");
		sb3.append("	to_char(atu_dttm, 'ddmmyyyy hh24mi') AS atu, ");
		sb3.append("	E.ESN_ASN_NBR, ");
		sb3.append("	ED.ACCT_NBR, ");
		sb3.append("	E.bk_ref_nbr, ");
		sb3.append("	ED.CRG_DES AS CRG_DES, ");
		sb3.append("	EM.MARKINGS, ");
		sb3.append("	ED.NBR_PKGS AS TotalPkg, ");
		sb3.append("	ED.NBR_PKGS-ED.UA_NBR_PKGS AS BalancePkg, ");
		sb3.append("	V.TERMINAL, ");
		sb3.append("	V.SCHEME, ");
		sb3.append("	V.COMBI_GC_SCHEME , ");
		sb3.append("	V.COMBI_GC_OPS_IND ");
		sb3.append("FROM ");
		sb3.append("	ESN E, ");
		sb3.append("	ESN_MARKINGS EM, ");
		sb3.append("	vessel_call V, ");
		sb3.append("	berthing, ");
		sb3.append("	SS_DETAILS ED ");
		sb3.append("JOIN LOGON_ACCT ON ");
		sb3.append("	ED.SHIPPER_CD = LOGON_ACCT.CUST_CD ");
		sb3.append("	AND LOGON_ACCT.LOGIN_ID = :userId ");
		sb3.append("WHERE ");
		sb3.append("	TRANS_TYPE = 'S' ");
		sb3.append("	AND E.ESN_ASN_NBR = ED.ESN_ASN_NBR ");
		sb3.append("	AND E.OUT_VOY_VAR_NBR = V.vv_cd ");
		sb3.append("	AND V.vv_cd = berthing.vv_cd ");
		sb3.append("	AND E.TRANS_CRG <> 'Y' ");
		sb3.append("	AND SHIFT_IND = 1 ");
		sb3.append("	AND EM.ESN_ASN_NBR = E.ESN_ASN_NBR ");
		sb3.append("	AND E.esn_asn_nbr = :esnasnnbr");

		sqlS = sb3.toString();

		try {
			log.info(
					"=======Start getEsnDetail: " + CommonUtility.deNull(esnasnnbr) + "/" + CommonUtility.deNull(esnTransType) + "/" + CommonUtility.deNull(coCode) +
							"/" + CommonUtility.deNull(userId) + "/");

			if (coCode.equalsIgnoreCase("JP")) {
				if (esnTransType != null && !esnTransType.equals("") && esnTransType.equals("E")) {
					sql = sqlE_JP;
					isSqlE_JP = true;
				}else if (esnTransType != null && !esnTransType.equals("") && esnTransType.equals("S")) {
					sql = sqlS_JP;
				}
			}

			else if (esnTransType != null && !esnTransType.equals("") && esnTransType.equals("E")) {
				sql = sqlE;
				issqlE = true;
			} else if (esnTransType != null && !esnTransType.equals("") && esnTransType.equals("S")) {
				sql = sqlS;
			}
	

			if (coCode.equalsIgnoreCase("JP")) {
				if (esnTransType != null && !esnTransType.equals("") && esnTransType.equals("E"))
					paramMap.put("esnasnnbr", esnasnnbr);

				else if (esnTransType != null && !esnTransType.equals("") && esnTransType.equals("S")) {
					paramMap.put("esnasnnbr", esnasnnbr);
				}
			} else if (esnTransType != null && !esnTransType.equals("") && esnTransType.equals("E")) {
				paramMap.put("esnasnnbr", esnasnnbr);
				paramMap.put("userId", userId);
			} else if (esnTransType != null && !esnTransType.equals("") && esnTransType.equals("S")) {
				paramMap.put("esnasnnbr", esnasnnbr);
				paramMap.put("userId", userId);
			}

			log.info(" getEsnDetail  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				// UaEsnDetValueObject esnObj = new UaEsnDetValueObject();
				count = 1;
				esnObj.setVessel_name(CommonUtility.deNull(rs.getString("VSL_NM")));
				esnObj.setOut_voy_nbr(CommonUtility.deNull(rs.getString("OUT_VOY_NBR")));
				esnObj.setEtb(CommonUtility.deNull(rs.getString("etb")));
				esnObj.setAtb(CommonUtility.deNull(rs.getString("atb")));
				esnObj.setAtu(CommonUtility.deNull(rs.getString("atu")));
				esnObj.setEsn_asn_nbr(CommonUtility.deNull(rs.getString("ESN_ASN_NBR")));
				esnObj.setAct_no(CommonUtility.deNull(rs.getString("ACCT_NBR")));
				esnObj.setBk_ref_nbr(CommonUtility.deNull(rs.getString("bk_ref_nbr")));
				esnObj.setCargo_desc(CommonUtility.deNull(rs.getString("CRG_DES")));
				esnObj.setCargo_markings(CommonUtility.deNull(rs.getString("markings")));
				esnObj.setDecl_pkg(CommonUtility.deNull(rs.getString("TotalPkg")));
				esnObj.setBal_pkg(CommonUtility.deNull(rs.getString("BalancePkg")));
				esnObj.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				esnObj.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				esnObj.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				esnObj.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				if(isSqlE_JP || issqlE) {
				esnObj.setTrucker_cont_no(CommonUtility.deNull(rs.getString("TRUCKER_PHONE_NBR")));
				esnObj.setTrucker_ic(CommonUtility.deNull(rs.getString("TRUCKER_IC")));
				esnObj.setTrucker_name(CommonUtility.deNull(rs.getString("TRUCKER_NM")));
				esnObj.setTrucker_nbr_pkg(CommonUtility.deNull(rs.getString("TRUCKER_NBR_PKGS")));
				}
				// esnList.add(esnObj);
			}

			log.info("=====v1: " + count);
			if ((!coCode.equalsIgnoreCase("JP") && count == 0)) {
				throw new BusinessException("You have no right to view the Sub ADP list."); // You have no right to view
																							// the Sub ADP list.
			}

			log.info("esnObj.getVessel_name() + coCode : Count : SQL ESN Detail: "
					+ CommonUtility.deNull(esnObj.getVessel_name()) + " : " + coCode + " : " + count + " : " + sql);
		
		} catch (NullPointerException e) {
			log.info("Exception getEsnDetail : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception getEsnDetail : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getEsnDetail : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEsnDetail  DAO  END");
			
		}
		return esnObj;
	} // end of View Esn

	/**
	 * Method to get type of ESN
	 * 
	 * @param esnasnnbr
	 * @return String
	 * @throws BusinessException
	 */
	public String getEsnTranType(String esnasnnbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String type = "";
		String sql = "";

		sql = "select TRANS_TYPE from ESN where ESN_ASN_NBR = :esnasnnbr ";

		try {
			log.info("START: getEsnTranType  DAO  Start esnasnnbr " + CommonUtility.deNull(esnasnnbr));

			paramMap.put("esnasnnbr", esnasnnbr);

			log.info(" getEsnTranType  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				type = CommonUtility.deNull(rs.getString("TRANS_TYPE"));
			}

			log.info(" getEsnTranType Result: " + type);
			if (!"E".equalsIgnoreCase(type) && !"S".equalsIgnoreCase(type))
				throw new BusinessException(ConstantUtil.ErrorMsg_Invalid_ESN_Trans); // ESN transaction type is incorrect.
	
		} catch (NullPointerException e) {
			log.info("Exception getEsnTranType : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception getEsnTranType : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getEsnTranType : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEsnTranType  DAO  END");
		
		}
		return type;
	}

	public int getSubADPTotal(String esnasnnbr) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		int total = 0;
		sql = "SELECT COUNT(*)  FROM SUB_ADP WHERE esn_asn_nbr = :esnasnnbr ";

		try {

			log.info("START: getSubADPTotal  DAO  Start esnasnnbr " + CommonUtility.deNull(esnasnnbr));

			paramMap.put("esnasnnbr", esnasnnbr);

			log.info(" getSubADP  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				total = rs.getInt(1);
			}
			log.info("END: *** getSubADPTotal Result *****" + total);
		} catch (NullPointerException e) {
			log.info("Exception getSubADPTotal : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSubADPTotal : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSubADPTotal  DAO  END");

		}
		return total;

	}

}
