package sg.com.jp.generalcargo.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.DnEdoDetailRepository;
import sg.com.jp.generalcargo.domain.EdoValueObjectContainerised;
import sg.com.jp.generalcargo.domain.EdoValueObjectOps;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("DnEdoDetailRepository")
public class DnEdoDetailJdbcRepository implements DnEdoDetailRepository {

	private static final Log log = LogFactory.getLog(DnEdoDetailJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public java.lang.String logStatusGlobal = "Y";

	public static Date getNextDayStart(Date date) {
		log.info("START: getNextDayStart date: "+ date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DATE, 1);
		log.info("END: *** getNextDayStart Result *****" + cal.getTime());

		return cal.getTime();
	}

	// StartRegion DnEdoDetailJdbcRepository

	public boolean checkESNCntr(String edoasn) throws BusinessException {
		boolean esnCntr = false;
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: checkESNCntr edoasn:" + edoasn);
			sb.append(" select a.cntr_seq_nbr, b.cntr_nbr from edo_cntr a, cntr b ");
			sb.append(" where a.edo_asn_nbr =:edoAsnNbr ");
			sb.append(" and a.cntr_seq_nbr = b.cntr_seq_nbr ");
			sb.append(" and b.txn_status <> 'D' and b.txn_status <> 'I' ");
			paramMap.put("edoAsnNbr", edoasn);
			log.info("SQL: checkESNCntr:" + sb.toString());
			log.info(" *** checkESNCntr params *****" + paramMap.toString());
			try {
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				if (rs.next()) {
					esnCntr = true;
				} else {
					esnCntr = false;
				}
			} catch (EmptyResultDataAccessException e) {
				log.info("Exception checkESNCntr : ", e);
			}
		} catch (Exception e) {
			log.info("Exception checkESNCntr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO checkESNCntr esnCntr:" + esnCntr);
		}
		return esnCntr;
	}

	public String getCntrSeq(String cntrNo) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		String cntrSeq = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getCntrSeq cntrNo:" + cntrNo);
			sb.append(" SELECT CNTR_SEQ_NBR FROM CNTR WHERE CNTR_NBR =:cntrNo AND PURP_CD='ST' ");
			sb.append(" AND TXN_STATUS = 'A' AND MISC_APP_NBR IS NOT NULL");
			paramMap.put("cntrNo", cntrNo);
			log.info("getCntrSeq SQL" + sb.toString() + "pstmt:");
			log.info(" *** getCntrSeq params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				cntrSeq = Integer.toString(rs.getInt(1));
			}
			log.info("END: *** getCntrSeq Result *****" + CommonUtility.deNull(cntrSeq));
		} catch (Exception e) {
			log.info("Exception getCntrSeq : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getCntrSeq");
		}
		return cntrSeq;
	}

	public boolean checkCancelDN(String dnNbr) throws BusinessException {
		boolean canCancel = false;
		Date dn_dttm = null;
		Date dnNextDayStart = null;
		Date today = new Date();
		SqlRowSet rs = null;
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: checkCancelDN dnNbr:" + dnNbr);
			sql = " SELECT DN_CREATE_DTTM FROM DN_DETAILS WHERE DN_NBR=:dnNbr ";
			paramMap.put("dnNbr", dnNbr);
			log.info("SQL" + sql + "paramMap:" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs != null) {
				while (rs.next()) {
					dn_dttm = rs.getDate(1);
				}
			}
			dnNextDayStart = getNextDayStart(dn_dttm);
			log.info("** dn_dttm -->" + dn_dttm);
			log.info("** dnNextDayStart --> " + dnNextDayStart);
			// compare dates and allow
			if (today.before(dnNextDayStart)) {
				log.info("**Can Cancel DN as the date threshold is not crossed*** ");
				canCancel = true;
			}
			log.info("END: *** checkCancelDN Result *****" + canCancel);
		} catch (Exception e) {
			log.info("Exception checkCancelDN : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO checkCancelDN");
		}
		return canCancel;
	}

	public String getDnCntrFirst(String cntrSeq, String cntrNbr) throws BusinessException {
		String dnFirst = "";
		SqlRowSet rs = null;
		int resultStf = 0;
		int resultUstf = 0;
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getDnCntrFirst cntrSeq:" + cntrSeq + "cntrNbr" + cntrNbr);
			sb.append(" select count(*) from cntr_txn where txn_cd = 'STF' and cntr_txn.cntr_seq_nbr =:cntrSeqNbr ");
			paramMap.put("cntrSeqNbr", cntrSeq);
			log.info("SQL" + sb.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				resultStf = rs.getInt(1);
			}

			sb.setLength(0);
			sb.append(" select count(*) from cntr_txn where txn_cd = 'USTF' and cntr_txn.cntr_seq_nbr =:cntrSeqNbr ");
			log.info("SQL" + sb.toString() + "paramMap:" + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				resultUstf = rs.getInt(1);
			}

			if (resultStf - resultUstf <= 0) {
				return "";
			}

			sb.setLength(0);
			sb.append(" select dn_nbr from dn_details where edo_asn_nbr in ");
			sb.append(" (select edo_asn_nbr from edo_cntr where cntr_seq_nbr =:cntrSeqNbr) ");
			sb.append(" and dn_status = 'A' and cntr_nbr =:cntrNbr order by mot_create_dttm ASC ");
			paramMap.put("cntrNbr", cntrNbr);
			log.info("SQL" + sb.toString() + "pstmt:");
			log.info(" *** getDnCntrFirst params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				dnFirst = rs.getString("dn_nbr");
			}

			log.info("END: *** getDnCntrFirst Result *****" + dnFirst);
		} catch (Exception e) {
			log.info("Exception getDnCntrFirst : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getDnCntrFirst");
		}

		return dnFirst;
	}

	public String cancelShutoutDN(String edoNbr, String dnNbr, String userid) throws BusinessException {
		String sql = "";
		String dnsql = "";
		String edoupdsql = "";
		SqlRowSet rs = null;
		String dn_sql_trans = "";
		String sqltlog = "";
		int stransno = 0;
		String sqltlog2 = "";
		int stransno2 = 0;
		int countua = 0;
		int count = 0;
		int newuanbrpkgs = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: cancelShutoutDN edoNbr:" + edoNbr + "dnNbr" + dnNbr + "userid" + userid);

			dnsql = "select * from dn_details where DN_NBR =:dnNbr ";
			paramMap.put("dnNbr", dnNbr);
			log.info("SQL" + dnsql.toString() + "pstmt:");
			log.info(" *** cancelShutoutDN params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(dnsql, paramMap);
			if (rs.next()) {
				newuanbrpkgs = rs.getInt("NBR_PKGS");
			}

			sqltlog = "SELECT MAX(TRANS_NBR) FROM gb_edo_TRANS WHERE Edo_ASN_NBR=:edoNbr ";
			paramMap.put("edoNbr", edoNbr);
			log.info("SQL" + sqltlog.toString() + "pstmt:");
			log.info(" *** cancelShutoutDN params *****" + paramMap.toString());
			SqlRowSet rs1 = null;
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);
				if (rs1.next()) {
					stransno = (rs1.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
			}

			SqlRowSet rs3 = null;
			sqltlog2 = "SELECT MAX(TRANS_NBR) FROM DN_DETAILS_TRANS WHERE dn_NBR=:dnNbr ";
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				rs3 = namedParameterJdbcTemplate.queryForRowSet(sqltlog2, paramMap);
				if (rs3.next()) {
					stransno2 = (rs3.getInt(1)) + 1;
				} else {
					stransno2 = 0;
				}
			}

			edoupdsql = "update gb_edo set dn_nbr_pkgs = dn_nbr_pkgs - :newuanbrpkgs where edo_asn_nbr =:edoNbr ";
			paramMap.put("newuanbrpkgs", newuanbrpkgs);
			paramMap.put("edoNbr", edoNbr);
			log.info("SQL" + sqltlog.toString());
			log.info(" *** cancelShutoutDN params *****" + paramMap.toString());
			count = namedParameterJdbcTemplate.update(edoupdsql, paramMap);

			sql = "UPDATE DN_DETAILS SET DN_STATUS='X',LAST_MODIFY_USER_ID=:userid ,LAST_MODIFY_DTTM=SYSDATE WHERE DN_NBR =:dnNbr ";
			paramMap.put("userid", userid);
			paramMap.put("dnNbr", dnNbr);
			log.info("SQL" + sqltlog.toString());
			log.info(" *** cancelShutoutDN params *****" + paramMap.toString());
			countua = namedParameterJdbcTemplate.update(sql, paramMap);

			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO DN_DETAILS_TRANS(TRANS_NBR,DN_NBR,DN_STATUS,LAST_MODIFY_DTTM,LAST_MODIFY_USER_ID)");
			sb.append(" VALUES(:stransno2,:dnNbr,'X',SYSDATE,:userid)");
			paramMap.put("stransno2", stransno2);
			dn_sql_trans = sb.toString();

			log.info("SQL" + dn_sql_trans.toString());
			log.info(" *** cancelShutoutDN params *****" + paramMap.toString());
			
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				namedParameterJdbcTemplate.update(dn_sql_trans, paramMap);
			}
			if (countua == 0 || count == 0) {
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			// Commented on 3/6/2016 Sripriya to nnot allow to update shutoutdelivery pkgs
			// in Booking as it is done @ Shutout Out Cargo SCreen
			// update booking_details's shutout delivery packages
			// String bookingRefNbr = getBookingRefNbr(edoNbr);
			// if (bookingRefNbr!=null && bookingRefNbr.length()>0){
			// updateBookingShutoutDeliveryPkgs(bookingRefNbr,-newuanbrpkgs);
			// }
			log.info("END: *** cancelShutoutDN Result *****" + CommonUtility.deNull(dnNbr));
		} catch (BusinessException e) {
			log.info("Exception cancelShutoutDN : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception cancelShutoutDN : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO cancelShutoutDN");
		}
		return dnNbr;
	}

	public String cancelDN(String edoNbr, String dnNbr, String userid, String transtype, String searchcrg,
			String tesn_nbr) throws BusinessException {
		String sql = "";
		String dnsql = "";
		String edoupdsql = "";
		String tesnupdsql = "";
		SqlRowSet rs = null;
		StringBuffer stringEdo = new StringBuffer();
		StringBuffer sb = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String chktesnJpJp_nbr = chktesnJpJp_nbr(tesn_nbr);
		String chktesnJpPsa_nbr = chktesnJpPsa_nbr(tesn_nbr);

		String sqlupdJp_trans = "";
		String dn_sql_trans = "";
		String sqltlog1 = "";
		String sqltlog = "";
		int stransno = 0;
		String sqltlog2 = "";
		int stransno2 = 0;
		int stransno1 = 0;
		int countua = 0;
		int count = 0;

		// boolean checkjpjp = chktesnJpJp(edoNbr);
		// boolean checkjppsa = chktesnJpPsa(edoNbr);
		boolean checkEdoStuff = chkEDOStuffing(edoNbr); // vinayak added 09 jan 2004
		if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {
			if (chktesnJpJp_nbr.equals("Y")) {
				tesnupdsql = "update tesn_jp_jp set dn_nbr_pkgs = dn_nbr_pkgs -";
				// lak added for Audit Trial
				sqlupdJp_trans = "insert into tesn_jp_jp_trans (trans_nbr,edo_asn_nbr,esn_asn_nbr,dn_nbr_pkgs,last_modify_dttm,last_modify_user_id,remarks) values(";
				sqltlog1 = "SELECT MAX(TRANS_NBR) FROM tesn_jp_jp_TRANS WHERE esn_asn_NBR=:esnAsnNbr";
			} else if (chktesnJpPsa_nbr.equals("Y")) {
				tesnupdsql = "update tesn_jp_psa set dn_nbr_pkgs = dn_nbr_pkgs -";
				// lak added for Audit Trial
				sqlupdJp_trans = "insert into tesn_jp_psa_trans (trans_nbr,edo_asn_nbr,esn_asn_nbr,dn_nbr_pkgs,last_modify_dttm,last_modify_user_id,remarks) values(";
				sqltlog1 = "SELECT MAX(TRANS_NBR) FROM tesn_jp_psa_TRANS WHERE esn_asn_NBR=:esnAsnNbr";
			}
		}

		if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
			stringEdo.append("update gb_edo set last_modify_dttm = sysdate, dn_nbr_pkgs = dn_nbr_pkgs -");
			// lak added for audit trail
			// edoupdsql_trans = "insert into gb_edo_trans
			// (trans_nbr,edo_asn_nbr,last_modify_dttm,last_modify_user_id,dn_nbr_pkgs,remarks)
			// values(";
		} else {
			stringEdo.append("update gb_edo set last_modify_dttm = sysdate, trans_dn_nbr_pkgs = trans_dn_nbr_pkgs -");
			// lak added for audit trail
			// edoupdsql_trans = "insert into gb_edo_trans
			// (trans_nbr,edo_asn_nbr,last_modify_dttm,last_modify_user_id,trans_dn_nbr_pkgs,remarks)
			// values(";
		}

		int newuanbrpkgs = 0;

		try {
			log.info("START: cancelDN edoNbr:" + edoNbr + " dnNbr" + dnNbr + " userid" + userid + " transtype"
					+ transtype + " searchcrg" + searchcrg + " tesn_nbr" + tesn_nbr);
			dnsql = "select * from dn_details where DN_NBR =:dnNbr";
			paramMap.put("dnNbr", dnNbr);
			log.info("SQL" + dnsql.toString() + "pstmt:");
			log.info(" *** cancelDN params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(dnsql, paramMap);
			if (rs.next()) {
				newuanbrpkgs = rs.getInt("NBR_PKGS");
			}

			if (checkEdoStuff) // vinayak added 09 jan 2004
			{
				sb.append("UPDATE cc_stuffing_details SET DN_NBR_PKGS=DN_NBR_PKGS- :newuanbrpkgs");
				sb.append(" WHERE STUFF_SEQ_NBR=:tesnNbr AND EDO_ESN_NBR=:edoNbr");
				String ccstuffsql = sb.toString();

				sb.setLength(0);
				sb.append("update gb_edo set trans_dn_nbr_pkgs = trans_dn_nbr_pkgs- :newuanbrpkgs");
				sb.append(" where edo_asn_nbr =:edoNbr");
				String edostuffsql = sb.toString();

				String dnstuffsql = "update dn_details set dn_status='X' where dn_nbr=:dnNbr and edo_asn_nbr=:edoNbr";

				paramMap.put("edoNbr", edoNbr);
				paramMap.put("newuanbrpkgs", newuanbrpkgs);
				paramMap.put("tesnNbr", tesn_nbr);
				log.info("SQL" + ccstuffsql + "paramMap:" + paramMap);
				namedParameterJdbcTemplate.update(ccstuffsql, paramMap);

				log.info("SQL" + edostuffsql + "paramMap:" + paramMap);
				namedParameterJdbcTemplate.update(edostuffsql, paramMap);

				paramMap.put("dnNbr", dnNbr);
				log.info("SQL" + dnstuffsql + "paramMap:" + paramMap);
				namedParameterJdbcTemplate.update(dnstuffsql, paramMap);

				log.info("**************inside if of checkEDO STuff :" + dnstuffsql);
				if (chktesnJpPsa_nbr.equals("Y")) {
					// To update for TESN JP PSA
					// lak added for Audit Trial
					// sqlupdJp_trans = "insert into tesn_jp_psa_trans
					// (trans_nbr,edo_asn_nbr,esn_asn_nbr,dn_nbr_pkgs,last_modify_dttm,last_modify_user_id,remarks)
					// values(";
					// sqltlog1 =
					// "SELECT MAX(TRANS_NBR) FROM tesn_jp_psa_TRANS WHERE esn_asn_NBR='" +
					// tesn_nbr + "'";

					tesnupdsql = "update tesn_jp_psa set dn_nbr_pkgs = dn_nbr_pkgs - :newuanbrpkgs where esn_asn_nbr =:tesnNbr";
					log.info("for JP PSA1 ***:" + tesnupdsql + ":" + newuanbrpkgs + ":" + tesn_nbr);
					log.info("SQL" + tesnupdsql + "paramMap:" + paramMap);
					namedParameterJdbcTemplate.update(tesnupdsql, paramMap);
					tesnupdsql = "";
				}
			} else {
				// lak added for Audit Trail start
				sqltlog = "SELECT MAX(TRANS_NBR) FROM gb_edo_TRANS WHERE Edo_ASN_NBR=:edoNbr";

				SqlRowSet rs1 = null;
				if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003
					paramMap.put("edoNbr", edoNbr);
					log.info("SQL" + sqltlog + "paramMap:" + paramMap);
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);
					if (rs1.next()) {
						stransno = (rs1.getInt(1)) + 1;
					} else {
						stransno = 0;
					}
				}

				// edoupdsql_trans = edoupdsql_trans
				// +stransno+","+edoNbr+",sysdate,'"+userid+"',"+newuanbrpkgs+",'DN Del')";

				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					// count_trans = stmt.executeUpdate(edoupdsql_trans);
				}

				SqlRowSet rs3 = null;
				sqltlog2 = "SELECT MAX(TRANS_NBR) FROM DN_DETAILS_TRANS WHERE dn_NBR=:dnNbr";
				if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003
					paramMap.put("dnNbr", dnNbr);
					log.info("SQL" + sqltlog2 + "paramMap:" + paramMap);
					rs3 = namedParameterJdbcTemplate.queryForRowSet(sqltlog2, paramMap);
					if (rs3.next()) {
						stransno2 = (rs3.getInt(1)) + 1;
					} else {
						stransno2 = 0;
					}
				}

				if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {
					tesnupdsql = tesnupdsql + ":newuanbrpkgs where esn_asn_nbr =:esnAsnNbr";
					paramMap.put("newuanbrpkgs", newuanbrpkgs);
					paramMap.put("esnAsnNbr", tesn_nbr);
					log.info("SQL" + tesnupdsql + "paramMap:" + paramMap);
					namedParameterJdbcTemplate.update(tesnupdsql, paramMap);
					// lak added for audit trail
					SqlRowSet rs2 = null;
					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003
						paramMap.put("esnAsnNbr", tesn_nbr);
						log.info("SQL" + sqltlog1 + "paramMap:" + paramMap);
						rs2 = namedParameterJdbcTemplate.queryForRowSet(sqltlog1, paramMap);
						if (rs2.next()) {
							stransno1 = (rs2.getInt(1)) + 1;
						} else {
							stransno1 = 0;
						}
					}

					sqlupdJp_trans = sqlupdJp_trans
							+ ":stransno1,:edoNbr,:tesnNbr,:newuanbrpkgs,sysdate,:userid,'DN Del')";
					if (logStatusGlobal.equalsIgnoreCase("Y")) {
						paramMap.put("stransno1", stransno1);
						paramMap.put("edoNbr", edoNbr);
						paramMap.put("tesnNbr", tesn_nbr);
						paramMap.put("newuanbrpkgs", newuanbrpkgs);
						paramMap.put("userid", userid);
						log.info("SQL" + sqlupdJp_trans + "paramMap:" + paramMap);
						namedParameterJdbcTemplate.update(sqlupdJp_trans, paramMap);

					}

				}

				stringEdo.append(":newuanbrpkgs where edo_asn_nbr =:edoNbr");
				edoupdsql = stringEdo.toString();
				paramMap.put("edoNbr", edoNbr);
				log.info("SQL" + edoupdsql + "paramMap:" + paramMap);
				count = namedParameterJdbcTemplate.update(edoupdsql, paramMap);

				sql = "UPDATE DN_DETAILS SET DN_STATUS='X',LAST_MODIFY_USER_ID=:userid,LAST_MODIFY_DTTM=SYSDATE WHERE DN_NBR =:dnNbr";
				paramMap.put("dnNbr", dnNbr);
				paramMap.put("userid", userid);
				log.info("SQL" + dnsql.toString() + "paramMap:" + paramMap);
				countua = namedParameterJdbcTemplate.update(sql, paramMap);

				sb.setLength(0);
				sb.append(
						"INSERT INTO DN_DETAILS_TRANS(TRANS_NBR,DN_NBR,DN_STATUS,LAST_MODIFY_DTTM,LAST_MODIFY_USER_ID) VALUES(");
				sb.append(":stransno2,:dnNbr,'X',SYSDATE,:userid)");
				dn_sql_trans = sb.toString();
				// lak added for Audit Trail end

				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					paramMap.put("stransno2", stransno2);
					paramMap.put("dnNbr", dnNbr);
					paramMap.put("userid", userid);
					log.info("SQL" + dnsql.toString() + "paramMap:" + paramMap);
					namedParameterJdbcTemplate.update(dn_sql_trans, paramMap);
				}

				if (countua == 0 || count == 0) {
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}

			}
			
			log.info("END: *** cancelDN Result count*****" + count);

		} catch (Exception e) {
			log.info("Exception cancelDN : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO cancelDN");
		}
		return "" + count;
	}

	public String chktesnJpPsa_nbr(String esn_asnNbr) throws BusinessException {
		SqlRowSet rs = null;
		String sql = "";
		String tesnjppsa = "N";
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: chktesnJpPsa_nbr esn_asnNbr:" + esn_asnNbr);
			sql = "select * from tesn_jp_psa jppsa,esn e where e.esn_asn_nbr = jppsa.esn_asn_nbr and e.esn_status ='A' and jppsa.ESN_ASN_NBR =:esnAsnNbr";
			paramMap.put("esnAsnNbr", esn_asnNbr);
			log.info("SQL" + sql.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				tesnjppsa = "Y";
			}
			log.info("END: *** chktesnJpPsa_nbr Result *****" + CommonUtility.deNull(tesnjppsa));
		} catch (NullPointerException e) {
			log.info("Exception chktesnJpPsa_nbr : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chktesnJpPsa_nbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO chktesnJpPsa_nbr");
		}
		return tesnjppsa;
	}

	public String chktesnJpJp_nbr(String esn_asnNbr) throws BusinessException {
		SqlRowSet rs = null;
		String sql = "";
		String tesnjpjp = "N";
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: chktesnJpJp_nbr esn_asnNbr:" + esn_asnNbr);
			sql = "select * from tesn_jp_jp jpjp,esn e where e.esn_asn_nbr = jpjp.esn_asn_nbr and e.esn_status ='A' and jpjp.ESN_ASN_NBR =:esn_asnNbr and e.trans_crg != 'Y'";
			paramMap.put("esn_asnNbr", CommonUtility.deNull(esn_asnNbr));
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				tesnjpjp = "Y";
			}
			log.info("END: *** chktesnJpJp_nbr Result *****" + CommonUtility.deNull(tesnjpjp));
		} catch (NullPointerException e) {
			log.info("Exception chktesnJpJp_nbr : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chktesnJpJp_nbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO chktesnJpJp_nbr");
		}
		return tesnjpjp;
	}

	public boolean chkEDOStuffing(String edoNbr) throws BusinessException {
		SqlRowSet rs = null;
		String strQuery = "";
		boolean edoExst = false;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: chkEDOStuffing edoNbr:" + edoNbr);
			strQuery = "SELECT distinct EDO_ESN_NBR FROM cc_stuffing_details WHERE EDO_ESN_IND='EDO' AND EDO_ESN_NBR=:edoNbr AND REC_STATUS='A'";
			paramMap.put("edoNbr", edoNbr);
			log.info("SQL" + strQuery.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(strQuery, paramMap);
			if (rs.next()) {
				edoExst = true;
			}
			log.info("END: *** chkEDOStuffing Result *****" + edoExst);

		} catch (NullPointerException e) {
			log.info("Exception chkEDOStuffing : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkEDOStuffing : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO chkEDOStuffing");
		}
		return edoExst;
	}

	public String getUaNbr(String esnNbr, int nbrPkgs, String transDttm, String dpNm, String dpIcNbr)
			throws BusinessException {
		String uaNbr = null;
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getUaNbr esnNbr:" + esnNbr + "nbrPkgs:" + nbrPkgs + "transDttm:" + transDttm + "dpNm:"
					+ dpNm + "dpIcNbr:" + dpIcNbr);
			sb.append(" select ua_nbr from ua_details where esn_asn_nbr=:esnNbr and nbr_pkgs=:nbrPkgs and ");
			sb.append(
					" to_char(trans_dttm, 'DD/MM/YYYY HH24:MI') =:transDttm and dp_ic_nbr=:dpIcNbr and ua_status='A' ");

			if (dpNm != null && !"".equalsIgnoreCase(dpNm)) {
				sb.append(" and dp_nm=:dpNm ");
			}
			paramMap.put("esnNbr", esnNbr);
			paramMap.put("nbrPkgs", nbrPkgs);
			paramMap.put("transDttm", transDttm);
			paramMap.put("dpNm", dpNm);
			paramMap.put("dpIcNbr", dpIcNbr);
			log.info("SQL" + sb.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				uaNbr = rs.getString("ua_nbr");
			}
			log.info("END: *** getUaNbr Result *****" + CommonUtility.deNull(uaNbr));

		} catch (NullPointerException e) {
			log.info("Exception getUaNbr : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getUaNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getUaNbr");
		}
		return uaNbr;
	}

	public boolean countDNBalance(String cntrNbr) throws BusinessException {
		boolean bal = false;
		SqlRowSet rs = null;
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: countDNBalance cntrNbr:" + cntrNbr);
			sql = " select * from dn_details where cntr_nbr=:cntrNbr and DN_STATUS='A' ";
			paramMap.put("cntrNbr", cntrNbr);
			log.info("SQL" + sql + "paramMap:" + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				bal = true;
			}
			
			log.info("END: *** countDNBalance Result *****" + bal);
		} catch (Exception e) {
			log.info("Exception countDNBalance : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO countDNBalance");
		}
		return bal;
	}

	public String updateCntrStatus(String cntrSeq, String userID) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: updateCntrStatus cntrSeq:" + cntrSeq + "userID:" + userID);
			sb.append(" UPDATE CNTR SET STATUS = 'E', LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID =:userID ");
			sb.append(" WHERE CNTR_SEQ_NBR =:cntrSeq ");
			paramMap.put("userID", userID);
			paramMap.put("cntrSeq", cntrSeq);
			log.info("SQL" + sb.toString() + "pstmt:");
			count = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			if (count == 0) {
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			
			log.info("END: *** updateCntrStatus Result count*****" + count);

		} catch (BusinessException e) {
			log.info("Exception updateCntrStatus : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception updateCntrStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO updateCntrStatus");
		}
		return "" + count;
	}

	public int checkFirstDN(String edoNbr, String cntrNo) throws BusinessException {
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		int resultStf = 0;
		int resultUstf = 0;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: checkFirstDN edoNbr:" + edoNbr + "cntrNo:" + cntrNo);
			sb.append(" select count(*) from cntr_txn,cntr where txn_cd = 'STF'");
			sb.append(" and cntr_txn.cntr_seq_nbr = cntr.cntr_seq_nbr and cntr_txn.cntr_nbr = cntr.cntr_nbr ");
			sb.append(" and cntr.cntr_nbr =:cntrNbr and cntr.txn_status = 'A' and cntr.purp_cd = 'ST' ");
			sb.append(" and cntr.misc_app_nbr is not null ");
			paramMap.put("cntrNbr", cntrNo);
			log.info("SQL" + sb.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				resultStf = rs.getInt(1);
			}

			sb2.append(" select count(*) from cntr_txn,cntr where txn_cd = 'USTF' ");
			sb2.append(" and cntr_txn.cntr_seq_nbr = cntr.cntr_seq_nbr and cntr_txn.cntr_nbr = cntr.cntr_nbr ");
			sb2.append(" and cntr.cntr_nbr =:cntrNbr and cntr.txn_status = 'A' and cntr.purp_cd = 'ST' ");
			sb2.append(" and cntr.misc_app_nbr is not null ");
			paramMap.put("cntrNbr", cntrNo);
			log.info("SQL" + sb2.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb2.toString(), paramMap);
			while (rs.next()) {
				resultUstf = rs.getInt(1);
			}
			log.info("END: *** checkFirstDN Result resultStf*****" + resultStf);
			log.info("END: *** checkFirstDN Result resultUstf*****" + resultUstf);
			log.info("END: *** checkFirstDN Result resultStf-resultUstf*****" + (resultStf - resultUstf));
		} catch (Exception e) {
			log.info("Exception checkFirstDN : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO checkFirstDN");
		}
		return resultStf - resultUstf;
	}

	public void changeStatusCntr(String cntrSeq, String user, String newCatCode) throws BusinessException {
		StringBuffer sql = new StringBuffer();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: changeStatusCntr cntrSeq:" + cntrSeq + "user:" + user + "newCatCode:" + newCatCode);
			String cntrStatus = "";
			sql.setLength(0);
			// sql.append("select STATUS from cntr_event_log where cntr_seq_nbr = ? and
			// TXN_CD = 'STF' ORDER by txn_dttm ASC");
			sql.append(
					"select ERROR_MSG from CNTR_TXN where cntr_seq_nbr =:cntrSeq and TXN_CD = 'STF' ORDER by txn_dttm ASC");
			paramMap.put("cntrSeq", cntrSeq);
			paramMap.put("user", user);
			paramMap.put("newCatCode", newCatCode);
			log.info("SQL" + sql.toString() + "paramMap:" + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				cntrStatus = rs.getString("ERROR_MSG");
			}
			if (cntrStatus != null && cntrStatus.length() > 0) {
				cntrStatus = cntrStatus.substring(0, 1);
			}
			if ("E".equals(cntrStatus)) {
				// update the status of cntr to E
				sql.setLength(0);
				sql.append("UPDATE CNTR SET CAT_CD =:newCatCode, STATUS = 'E', LAST_MODIFY_DTTM = sysdate,");
				sql.append(" LAST_MODIFY_USER_ID =:user " + "WHERE CNTR_SEQ_NBR=:cntrSeq ");
				paramMap.put("newCatCode", newCatCode);
				paramMap.put("user", user);
				paramMap.put("cntrSeq", cntrSeq);
				log.info("SQL" + sql.toString() + "pstmt:");
				namedParameterJdbcTemplate.update(sql.toString(), paramMap);
			}
		} catch (Exception e) {
			log.info("Exception changeStatusCntr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO changeStatusCntr");
		}
	}

	public void cancel1stDn(String cntrSeq, String cntrNbr, String user) throws BusinessException {
		StringBuffer sql = new StringBuffer();
		Timestamp dttmFirst = new Timestamp(new Date().getTime());
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			sql.setLength(0);
			log.info("START: cancel1stDn cntrSeq:" + cntrSeq + "cntrNbr:" + cntrNbr + "user" + user);
			sql.append(
					"INSERT INTO CNTR_TXN(CNTR_SEQ_NBR, CNTR_NBR, TXN_CD, TXN_DTTM, LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID) ");
			sql.append("VALUES( :cntrSeq, :cntrNbr, 'USTF', :dttmFirst, sysdate, :user')");
			paramMap.put("cntrSeq", cntrSeq);
			paramMap.put("cntrNbr", cntrNbr);
			paramMap.put("user", user);
			paramMap.put("dttmFirst", dttmFirst.toString());
			log.info("SQL" + sql.toString() + "paramMap:" + paramMap);
			namedParameterJdbcTemplate.update(sql.toString(), paramMap);

			// sql.setLength(0);
			// sql.append("insert into cntr_event_log(CNTR_SEQ_NBR, TXN_DTTM, TXN_CD, " +
			// "CNTR_NBR, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
			// sql.append("values(?, ?, 'USTF', ?, ?, sysdate)");
			// PreparedStatement prst = conn.prepareStatement(sql.toString());
			// prst.setString(1, cntrSeq);
			// prst.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
			// prst.setString(3, cntrNbr);
			// prst.setString(4, user);
			// int count = prst.executeUpdate();
			// if (count <= 0) {
			// prst.close();
			// conn.close();
			// sessionContext.setRollbackOnly();
			// throw new BusinessException("M4201");
			// }
			// prst.close();
		} catch (Exception e) {
			log.info("Exception cancel1stDn : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO cancel1stDn");
		}
	}

	public void updateWeight(String cntrSeq, long weight, String user, String times) throws BusinessException {
		StringBuffer sql = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		long wght = 0;
		try {
			log.info("START: updateWeight cntrSeq:" + cntrSeq + "weight:" + weight + "user:" + user + "times:" + times);
			if ("ADD".equals(times)) {
				wght = getWeight(cntrSeq) + weight;
				sql.append(
						" UPDATE CNTR SET LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID =:user, DECLR_WT =:weight ");
				sql.append(" WHERE CNTR_SEQ_NBR=:cntrSeq ");
			} else if ("SUB".equals(times)) {
				wght = getWeight(cntrSeq) - weight;
				sql.append(
						" UPDATE CNTR SET LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID =:user, DECLR_WT =:weight ");
				sql.append(" WHERE CNTR_SEQ_NBR=:cntrSeq ");
			} else {
				throw new BusinessException("M4201");
			}
			paramMap.put("cntrSeq", cntrSeq);
			paramMap.put("weight", wght);
			paramMap.put("user", user);
			log.info("SQL" + sql.toString() + "pstmt:");
			namedParameterJdbcTemplate.update(sql.toString(), paramMap);
		} catch (NullPointerException e) {
			log.info("Exception updateWeight : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception updateWeight : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO updateWeight");
		}

	}

	public long getWeight(String cntrSeq) throws BusinessException {
		SqlRowSet rs = null;
		long weight = 0;
		StringBuffer sql = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getWeight cntrSeq:" + cntrSeq);
			sql.append(" select DECLR_WT from CNTR where CNTR_SEQ_NBR =:cntrSeq ");
			paramMap.put("cntrSeq", cntrSeq);
			log.info("SQL" + sql.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				weight = rs.getLong("DECLR_WT");
			}
			
			log.info("END: *** getWeight Result *****" + weight);
		} catch (Exception e) {
			log.info("Exception getWeight : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getWeight");
		}
		return weight;
	}

	public List<EdoValueObjectOps> fetchShutoutDNList(String edoNbr) throws BusinessException {
		SqlRowSet rs = null;
		StringBuffer sql = new StringBuffer();
		List<EdoValueObjectOps> BJDetailsVect = new ArrayList<EdoValueObjectOps>();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: fetchShutoutDNList edoNbr:" + edoNbr);
			sql.append(" SELECT DN_NBR, NBR_PKGS, BILLABLE_TON, DN_STATUS, BILL_STATUS,");
			sql.append(
					" TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')||' @ '||DECODE(PRINT_LOCATION,'C','GCO','S',SST_MACHINE_NBR,PRINT_LOCATION)||DECODE(GATE_OUT_DTTM,'','',' (VehExit: '||TO_CHAR(GATE_OUT_DTTM,'DD/MM HH24:MI')||')') as trnsDate, ");
			sql.append(" CNTR_NBR, TRUCK_NBR FROM DN_DETAILS WHERE EDO_ASN_NBR=:edoNbr ");
			sql.append(" and tesn_asn_nbr is null order by dn_nbr ");
			paramMap.put("edoNbr", edoNbr);
			log.info("SQL" + sql.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			String dnNbr;
			String noPkgs;
			String billTon;
			String dnStatus;
			String trans = "";
			String billStatus;
			String cntrNo = "";
			String truckNbr = "";
			while (rs.next()) {
				dnNbr = rs.getString("DN_NBR");
				noPkgs = rs.getString("NBR_PKGS");
				billTon = rs.getString("BILLABLE_TON");
				dnStatus = rs.getString("DN_STATUS");
				trans = rs.getString("trnsDate");
				billStatus = rs.getString("BILL_STATUS");
				cntrNo = rs.getString("CNTR_NBR");
				truckNbr = CommonUtility.deNull(rs.getString("TRUCK_NBR"));
				EdoValueObjectOps edoVo = new EdoValueObjectOps();
				edoVo.setDnNbr(dnNbr);
				edoVo.setNoPkgs(noPkgs);
				edoVo.setBillTon(billTon);
				edoVo.setDnStatus(dnStatus);
				edoVo.setTransDate(trans);
				edoVo.setBillStatus(billStatus);
				edoVo.setCntrNo(cntrNo);
				edoVo.setVech1(truckNbr);
				BJDetailsVect.add(edoVo);
			}
			
			log.info("END: *** fetchShutoutDNList Result *****" + BJDetailsVect.size());
		} catch (Exception e) {
			log.info("Exception fetchShutoutDNList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO fetchShutoutDNList");
		}
		return BJDetailsVect;
	}

	public List<EdoValueObjectOps> fetchEdoDetails(String edoNbr, String searchcrg, String tesnnbr)
			throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		List<EdoValueObjectOps> BJDetailsVect = new ArrayList<EdoValueObjectOps>();
		Map<String, String> paramMap = new HashMap<String, String>();
		// sql = select * from gb_edo edo, manifest_details mft where edo.mft_seq_nbr =
		// mft.mft_seq_nbr and edo_asn_nbr='" + edoNbr + "'"
		// boolean checkjpjp = chktesnJpJp(edoNbr);
		// boolean checkjppsa = chktesnJpPsa(edoNbr);
		boolean checkEdoStuff = chkEDOStuffing(edoNbr); // vinayak added 07 jan 2004
		String chktesnJpJp_nbr = chktesnJpJp_nbr(tesnnbr);
		String chktesnJpPsa_nbr = chktesnJpPsa_nbr(tesnnbr);
		if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
			/*
			 * sql =
			 * "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,"
			 * ; sql = sql +
			 * " d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
			 * ; sql = sql +
			 * " b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, to_char(c.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
			 * ; sql = sql +
			 * " a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0)+nvl(a.trans_dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e where "
			 * ; sql = sql +
			 * " a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and a.EDO_ASN_NBR ="
			 * + edoNbr.trim();
			 */

			/*
			 * sql =
			 * "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,"
			 * ; sql = sql +
			 * " d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
			 * ; sql = sql +
			 * " b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,f.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
			 * ; sql = sql +
			 * " a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0)+nvl(a.trans_dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,cc_unstuff_manifest f where "
			 * ; sql = sql +
			 * " a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr AND d.unstuff_seq_nbr=f.UNSTUFF_SEQ_NBR(+) and a.EDO_ASN_NBR ="
			 * + edoNbr.trim();
			 */

			sb.append("select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,");
			sb.append(
					" d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,b.vsl_nm , ");
			sb.append(
					" b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,f.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,");
			sb.append(" nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,");
			sb.append(" a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,");
			sb.append(
					" nvl(a.dn_nbr_pkgs,0)+nvl(a.trans_dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0)");
			sb.append(" - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,");
			sb.append(
					" a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,");
			sb.append(
					" a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,a.mft_seq_nbr as mftsqnbr, a.wh_ind,");
			sb.append(
					" a.wh_aggr_nbr, a.wh_remarks, b.terminal, b.scheme,b.combi_gc_scheme, b.combi_gc_ops_ind from gb_edo a ,");
			sb.append(" vessel_call b ,berthing c,manifest_details d ,  mft_markings e,cc_unstuff_manifest f where ");
			sb.append(
					" a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 ");
			sb.append(
					" and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr AND d.unstuff_seq_nbr=f.UNSTUFF_SEQ_NBR(+)");
			sb.append(" and a.EDO_ASN_NBR =:edoNbr");
			sql = sb.toString();

			// LogManager.instance.logInfo("sql :"+sql);
		} else {
			// if(checkjpjp && checkjppsa){
			if (chktesnJpJp_nbr.equals("Y")) {
				/*
				 * sql =
				 * "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,"
				 * ; sql = sql +
				 * " d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
				 * ; sql = sql +
				 * " b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, to_char(c.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
				 * ; sql = sql +
				 * " a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,"
				 * ; sql = sql +
				 * " f.NBR_PKGS as jpjpnpkg,f.DN_NBR_PKGS as jpjpdn_npkg,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,tesn_jp_jp f where"
				 * ; sql = sql +
				 * " a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr = '"
				 * + tesnnbr + "' and a.EDO_ASN_NBR =" + edoNbr.trim();
				 */

				/*
				 * sql =
				 * "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,"
				 * ; sql = sql +
				 * " d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
				 * ; sql = sql +
				 * " b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,g.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
				 * ; sql = sql +
				 * " a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,"
				 * ; sql = sql +
				 * " f.NBR_PKGS as jpjpnpkg,f.DN_NBR_PKGS as jpjpdn_npkg,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,tesn_jp_jp f, cc_unstuff_manifest g where"
				 * ; sql = sql +
				 * " a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and d.unstuff_seq_nbr=g.UNSTUFF_SEQ_NBR(+) AND f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr = '"
				 * + tesnnbr + "' and a.EDO_ASN_NBR =" + edoNbr.trim();
				 */
				sb.setLength(0);
				sb.append("select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,");
				sb.append(
						" d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,");
				sb.append(
						" b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,");
				sb.append(
						" c.GB_COD_DTTM,g.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,");
				sb.append(
						" a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,");
				sb.append(
						" a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),");
				sb.append(
						" a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,");
				sb.append(
						" CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,");
				sb.append(
						" f.NBR_PKGS as jpjpnpkg,f.DN_NBR_PKGS as jpjpdn_npkg,a.mft_seq_nbr as mftsqnbr , a.wh_ind, a.wh_aggr_nbr, a.wh_remarks,");
				sb.append(
						" b.terminal, b.scheme,b.combi_gc_scheme, b.combi_gc_ops_ind  from gb_edo a , vessel_call b ,berthing c,manifest_details d ,");
				sb.append(" mft_markings e,tesn_jp_jp f, cc_unstuff_manifest g where");
				sb.append(
						" a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and");
				sb.append(
						" a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and d.unstuff_seq_nbr=g.UNSTUFF_SEQ_NBR(+) AND");
				sb.append(" f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr =:tesnNbr and a.EDO_ASN_NBR =:edoNbr");
				sql = sb.toString();

				// LogManager.instance.logInfo("sql :"+sql);
			} else if (chktesnJpPsa_nbr.equals("Y")) {
				/*
				 * sql =
				 * "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,"
				 * ; sql = sql +
				 * " d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
				 * ; sql = sql +
				 * " b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, to_char(c.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
				 * ; sql = sql +
				 * " a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,"
				 * ; sql = sql +
				 * " f.NBR_PKGS as jppsanpkg,f.DN_NBR_PKGS as jppsadn_npkg,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,tesn_jp_psa f where"
				 * ; sql = sql +
				 * " a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr = '"
				 * + tesnnbr + "' and a.EDO_ASN_NBR =" + edoNbr.trim();
				 */
				/*
				 * sql =
				 * "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,"
				 * ; sql = sql +
				 * " d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
				 * ; sql = sql +
				 * " b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,g.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
				 * ; sql = sql +
				 * " a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,"
				 * ; sql = sql +
				 * " f.NBR_PKGS as jppsanpkg,f.DN_NBR_PKGS as jppsadn_npkg,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,tesn_jp_psa f,cc_unstuff_manifest g where"
				 * ; sql = sql +
				 * " a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and d.unstuff_seq_nbr=g.UNSTUFF_SEQ_NBR(+) AND f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr = '"
				 * + tesnnbr + "' and a.EDO_ASN_NBR =" + edoNbr.trim();
				 */
				sb.setLength(0);
				sb.append("select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,");
				sb.append(
						" d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,");
				sb.append(
						" b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0");
				sb.append(
						" ,c.GB_COD_DTTM,g.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,");
				sb.append(
						" a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,");
				sb.append(
						" a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,");
				sb.append(
						" nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,");
				sb.append(
						" f.NBR_PKGS as jppsanpkg,f.DN_NBR_PKGS as jppsadn_npkg,a.mft_seq_nbr as mftsqnbr, a.wh_ind, a.wh_aggr_nbr, a.wh_remarks, b.terminal, b.scheme,b.combi_gc_scheme, ");
				sb.append(
						" b.combi_gc_ops_ind  from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,tesn_jp_psa f,cc_unstuff_manifest g where");
				sb.append(
						" a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr ");
				sb.append(
						" and d.unstuff_seq_nbr=g.UNSTUFF_SEQ_NBR(+) AND f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr =:tesnNbr and a.EDO_ASN_NBR =:edoNbr ");
				sql = sb.toString();
			}
			// vinayak added on 8 jan 2004
			else if (checkEdoStuff) {
				sb.setLength(0);
				sb.append(
						"SELECT a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD");
				sb.append(
						",CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,b.vsl_nm , b.in_voy_nbr ,TO_CHAR(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') AS atb,");
				sb.append(
						" TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,g.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') AS cod,NVL(d.CRG_DES,' '),");
				sb.append(
						" NVL(e.mft_markings,'') ,a.nbr_pkgs,a.nbr_pkgs - a.trans_nbr_pkgs - NVL(a.release_nbr_pkgs,0)- NVL(a.dn_nbr_pkgs,0) ,");
				sb.append(" NVL(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,NVL(a.nbr_pkgs,0) - NVL(a.release_nbr_pkgs,0)");
				sb.append(
						" - NVL(a.trans_dn_nbr_pkgs,0) - NVL(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,");
				sb.append(" NVL(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,");
				sb.append(" a.trans_nbr_pkgs AS transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS AS mftNbr,");
				sb.append(
						" csd.NBR_PKGS AS stuffnpkg,csd.DN_NBR_PKGS AS stuffdnnpkg,a.mft_seq_nbr AS mftsqnbr,csd.STUFF_SEQ_NBR stuffseq, ");
				sb.append(
						" a.wh_ind, a.wh_aggr_nbr, a.wh_remarks, b.terminal, b.scheme,b.combi_gc_scheme, b.combi_gc_ops_ind  FROM gb_edo a ,");
				sb.append(
						" vessel_call b ,berthing c,manifest_details d ,  mft_markings e,cc_stuffing_details csd,cc_stuffing cs,cc_unstuff_manifest g WHERE a.var_nbr =  b.vv_cd AND ");
				sb.append(
						" b.vv_cd = c.vv_cd  AND d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) AND SHIFT_IND = 1 AND a.edo_status='A' AND ");
				sb.append(" a.mft_seq_nbr = d.mft_seq_nbr AND d.unstuff_seq_nbr=g.UNSTUFF_SEQ_NBR(+) AND");
				sb.append(
						" cs.STUFF_CLOSED='Y' AND csd.REC_STATUS='A' AND cs.ACTIVE_STATUS='A' AND csd.EDO_ESN_NBR = a.EDO_ASN_NBR");
				sb.append(
						" AND csd.EDO_ESN_IND='EDO' AND cs.STUFF_SEQ_NBR=csd.STUFF_SEQ_NBR AND csd.STUFF_SEQ_NBR =:tesnNbr");
				sb.append(" AND a.EDO_ASN_NBR =:edoNbr");
				sql = sb.toString();
				// "a.trans_nbr_pkgs AS
				// transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS AS
				// mftNbr,csd.NBR_PKGS AS stuffnpkg,csd.DN_NBR_PKGS AS stuffdnnpkg,a.mft_seq_nbr
				// AS mftsqnbr,csd.STUFF_SEQ_NBR stuffseq FROM gb_edo a , vessel_call b
				// ,berthing c,manifest_details d , mft_markings e,cc_stuffing_details
				// csd,cc_stuffing cs,cc_unstuff_manifest g WHERE a.var_nbr = b.vv_cd AND "+
				// changed by Irene Tan on 17 Feb 2004 : to allow extraction for EDO from other
				// vessel stuff into container of different vessel
				// "b.vv_cd = c.vv_cd AND c.vv_cd=cs.VAR_NBR AND d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+)
				// AND SHIFT_IND = 1 AND a.edo_status='A' AND a.mft_seq_nbr = d.mft_seq_nbr AND
				// d.unstuff_seq_nbr=g.UNSTUFF_SEQ_NBR(+) AND cs.STUFF_CLOSED='Y' AND
				// csd.REC_STATUS='A' AND cs.ACTIVE_STATUS='A' AND csd.EDO_ESN_NBR =
				// a.EDO_ASN_NBR AND csd.EDO_ESN_IND='EDO' AND
				// cs.STUFF_SEQ_NBR=csd.STUFF_SEQ_NBR AND csd.STUFF_SEQ_NBR = '"+tesnnbr+"' AND
				// a.EDO_ASN_NBR ="+edoNbr.trim();
				// end changed by Irene Tan on 17 Feb 2004
			}
		}
		try {
			log.info("START: fetchEdoDetails edoNbr:" + edoNbr + "searchcrg:" + searchcrg + "tesnnbr:" + tesnnbr);
			paramMap.put("edoNbr", edoNbr.trim());
			if (chktesnJpJp_nbr.equals("Y") || checkEdoStuff) {
				paramMap.put("tesnNbr", tesnnbr);
			}
			log.info("SQL" + sql.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			String blNbr;
			String accNbr;
			String payNbr;
			String nomWt;
			String nomVol;
			String adpcustcd;
			String adpIcNbr;
			String adpNm;
			String caCustcd;
			String caIcNbr;
			String caNm;
			String aaNm;
			String aaCustCd;
			String aaIcNbr;
			String vslName;
			String atb;
			String cod;
			String crgDes;
			String declPkgs;
			String balance;
			String dnPkgs;
			String crgStatus;
			String mftmarkings;
			String invoyNbr;
			String outvoyNbr;
			String consname;
			String billparty = "";
			String maxQty = "";
			String transNbr = "";
			String transLocalCheck = "";
			String mftNbr = "";
			String mftSqNbr = "";
			String sumCtDNnbr = "";
			String sumEdoCtDNnbr = "";
			// Warehouse details

			String whInd = "";
			String whAggrNbr = "";
			String whRemarks = "";
			while (rs.next()) {
				blNbr = rs.getString("BL_NBR");
				edoNbr = rs.getString("edo_asn_nbr");
				accNbr = rs.getString("ACCT_NBR");
				payNbr = rs.getString("PAYMENT_MODE");
				nomWt = rs.getString("NOM_WT");
				nomVol = rs.getString("NOM_VOL");
				crgDes = rs.getString("crg_des");
				adpcustcd = rs.getString("ADP_CUST_CD");
				adpIcNbr = rs.getString("ADP_IC_TDBCR_NBR");
				adpNm = rs.getString("ADP_NM");
				caCustcd = rs.getString("CA_CUST_CD");
				caIcNbr = rs.getString("CA_IC_TDBCR_NBR");
				caNm = rs.getString("CA_NM");
				aaNm = rs.getString("aa_nm");
				aaCustCd = rs.getString("AA_CUST_CD");
				aaIcNbr = rs.getString("AA_IC_TDBCR_NBR");
				vslName = rs.getString("vsl_nm");
				atb = rs.getString(18);
				cod = rs.getString(19);
				crgDes = rs.getString(20);
				mftmarkings = rs.getString(21);
				declPkgs = rs.getString("nbr_pkgs");
				balance = rs.getString(23);
				dnPkgs = rs.getString(24);
				crgStatus = rs.getString("CRG_STATUS");
				invoyNbr = rs.getString("in_voy_nbr");
				outvoyNbr = rs.getString("out_voy_nbr");
				consname = rs.getString("CONS_NM");
				maxQty = rs.getString(30);
				transNbr = rs.getString("transNbr");
				transLocalCheck = rs.getString(28);
				mftNbr = rs.getString("mftNbr");
				mftSqNbr = rs.getString("mftsqnbr");
				sumCtDNnbr = getCTDNnbr(mftSqNbr);
				sumEdoCtDNnbr = getEDOCTDNnbr(mftSqNbr);
				// Warehosue details
				whInd = CommonUtility.deNull(rs.getString("WH_IND"));
				whAggrNbr = CommonUtility.deNull(rs.getString("WH_AGGR_NBR"));
				whRemarks = CommonUtility.deNull(rs.getString("WH_REMARKS"));

				if (payNbr.equals("A")) {
					StringBuffer sql1 = new StringBuffer();
					sql1.append(
							"SELECT C.CO_NM FROM CUST_ACCT CA,COMPANY_CODE C WHERE CA.CUST_CD=C.CO_CD AND CA.ACCT_NBR=:accNbr");
					paramMap.put("accNbr", accNbr);
					log.info("SQL" + sql1.toString() + "pstmt:");
					SqlRowSet rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);

					if (rs1.next()) {
						billparty = rs1.getString(1);
						payNbr = accNbr;
					}
				}
				if (adpcustcd != null) {
					// Punitha - UEN Enhancement
					// ResultSet rs1 = sqlstmt1.executeQuery("SELECT CC.CO_NM,CUSTOMER.TDB_CR_NBR
					// FROM CUSTOMER, COMPANY_CODE CC WHERE CUSTOMER.CUST_CD=CC.CO_CD AND
					// CUSTOMER.CUST_CD='" + adpcustcd + "'");
					StringBuffer sql1 = new StringBuffer();
					sql1.append(" SELECT CC.CO_NM, (SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) from customer ");
					sql1.append(
							" where cust_cd=:adpcustcd) TDB_CR_NBR FROM CUSTOMER, COMPANY_CODE CC WHERE CUST_CD=CC.CO_CD and cust_cd =:adpcustcd ");

					paramMap.put("adpcustcd", adpcustcd);
					log.info("SQL" + sql1.toString() + "pstmt:");
					SqlRowSet rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);

					if (rs1.next()) {
						adpNm = rs1.getString("co_nm");
						adpIcNbr = rs1.getString("TDB_CR_NBR");
					}
				}

				if (caCustcd != null) {
					// Punitha - UEN Enhancement
					// ResultSet rs1 = sqlstmt1.executeQuery("SELECT CC.CO_NM,CUSTOMER.TDB_CR_NBR
					// FROM CUSTOMER, COMPANY_CODE CC WHERE CUSTOMER.CUST_CD=CC.CO_CD AND
					// CUSTOMER.CUST_CD='" + caCustcd + "'");
					StringBuffer sql1 = new StringBuffer();
					sql1.append(" SELECT CC.CO_NM, (SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR)  ");
					sql1.append(
							" from customer where cust_cd =:caCustcd) TDB_CR_NBR FROM CUSTOMER, COMPANY_CODE CC WHERE CUST_CD=CC.CO_CD and cust_cd =:caCustcd ");
					paramMap.put("caCustcd", caCustcd);
					log.info("SQL" + sql1.toString() + "pstmt:");
					SqlRowSet rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);

					if (rs1.next()) {
						caNm = rs1.getString("co_nm");
						caIcNbr = rs1.getString("TDB_CR_NBR");
					}
				}

				if (aaCustCd != null) {
					// Punitha - UEN Enhancement
					// ResultSet rs1 = sqlstmt1.executeQuery("SELECT CC.CO_NM,CUSTOMER.TDB_CR_NBR
					// FROM CUSTOMER, COMPANY_CODE CC WHERE CUSTOMER.CUST_CD=CC.CO_CD AND
					// CUSTOMER.CUST_CD='" + aaCustCd + "'");
					StringBuffer sql1 = new StringBuffer();
					sql1.append(
							" SELECT CC.CO_NM, (SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) from customer where cust_cd =:aaCustCd ");
					sql1.append(
							" ) TDB_CR_NBR FROM CUSTOMER, COMPANY_CODE CC WHERE CUST_CD=CC.CO_CD and cust_cd =:aaCustCd ");
					paramMap.put("aaCustCd", aaCustCd);
					log.info("SQL" + sql1.toString() + "pstmt:");
					SqlRowSet rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);

					if (rs1.next()) {
						aaNm = rs1.getString("co_nm");
						aaIcNbr = rs1.getString("TDB_CR_NBR");
					}
				}
				if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {
					String bal_cutoff = rs.getString("CUT_OFF_NBR_PKGS");
					String bal_short_land = rs.getString("NBR_PKGS_IN_PORT");
					int con3 = 0;
					int con5 = 0;
					if (bal_cutoff != null && !bal_cutoff.equals(""))
						con3 = Integer.parseInt(bal_cutoff);
					if (bal_short_land != null && !bal_short_land.equals(""))
						con5 = Integer.parseInt(bal_short_land);
					int relPkgsNbr = Integer.parseInt(declPkgs) - Integer.parseInt(maxQty)
							- Integer.parseInt(transLocalCheck) - Integer.parseInt(dnPkgs);
					int edoNbrPkgs = Integer.parseInt(declPkgs) - Integer.parseInt(maxQty)
							- Integer.parseInt(transLocalCheck) - Integer.parseInt(dnPkgs) - con3;
					int blPkgs = 0;
					blPkgs = Integer.parseInt(mftNbr) - (con5 + Integer.parseInt(sumEdoCtDNnbr));
					// int blPkgs = 0;
					// blPkgs = Integer.parseInt(declPkgs) - Integer.parseInt(sumEdoCtDnNbr);
					// int bal4 = con4 - con3;
					// if(blPkgs >= bal4)
					// bal4 = bal4;
					// else
					// bal4 = blPkgs;
					// balance = String.valueOf(bal4) ;

					if (chktesnJpJp_nbr.equals("Y")) {
						String bal1 = rs.getString("jpjpnpkg");
						String bal2 = rs.getString("jpjpdn_npkg");
						dnPkgs = bal2;
						declPkgs = bal1;
						int con1 = 0;
						int con2 = 0;
						if (bal1 != null && !bal1.equals(""))
							con1 = Integer.parseInt(bal1);
						if (bal2 != null && !bal2.equals(""))
							con2 = Integer.parseInt(bal2);

						int bal3 = con1 - con2;

						if (blPkgs >= bal3) {
							if (edoNbrPkgs >= bal3) {
								if (relPkgsNbr < bal3)
									bal3 = relPkgsNbr - con3;
							} else
								bal3 = edoNbrPkgs;
						} else
							bal3 = blPkgs;
						balance = String.valueOf(bal3);
					} else if (chktesnJpPsa_nbr.equals("Y")) {
						String bal1 = rs.getString("jppsanpkg");
						String bal2 = rs.getString("jppsadn_npkg");
						dnPkgs = bal2;
						declPkgs = bal1;
						int con1 = 0;
						int con2 = 0;
						if (bal1 != null && !bal1.equals(""))
							con1 = Integer.parseInt(bal1);
						if (bal2 != null && !bal2.equals(""))
							con2 = Integer.parseInt(bal2);
						int bal3 = con1 - con2;
						if (blPkgs >= bal3) {
							if (edoNbrPkgs >= bal3) {
								if (relPkgsNbr < bal3)
									bal3 = relPkgsNbr - con3;
							} else
								bal3 = edoNbrPkgs;
						} else
							bal3 = blPkgs;

						balance = String.valueOf(bal3);
					}
					// added by vinayak on 8 jan 2004
					else if (checkEdoStuff) {
						String bal1 = rs.getString("stuffnpkg");
						String bal2 = rs.getString("stuffdnnpkg");
						dnPkgs = bal2; // dnPkgs=gb_edo.dn_nbr_pkgs
						declPkgs = bal1; // declPkgs=gb_edo.nbr_pkgs
						int con1 = 0;
						int con2 = 0;
						if (bal1 != null && !bal1.equals(""))
							con1 = Integer.parseInt(bal1);
						if (bal2 != null && !bal2.equals(""))
							con2 = Integer.parseInt(bal2);
						int bal3 = con1 - con2;
						/*
						 * if (blPkgs >= bal3) { //blPkgs=manifest_details.NBR_PKGS -
						 * (manifest_details.NBR_PKGS_IN_PORT + select
						 * sum(nvl(trans_dn_nbr_pkgs,0)+nvl(CUT_OFF_NBR_PKGS,0)+nvl(DN_NBR_PKGS,0)) from
						 * gb_edo where MFT_SEQ_NBR = '") if (edoNbrPkgs >= bal3)
						 * //edoNbrPkgs=gb_edo.nbr_pkgs - gb_edo.release_nbr_pkgs -
						 * gb_edo.trans_dn_nbr_pkgs - gb_edo.dn_nbr_pkgs - gb_edo.CUT_OFF_NBR_PKGS { if
						 * (relPkgsNbr >= bal3) //relPkgsNbr=gb_edo.nbr_pkgs - gb_edo.release_nbr_pkgs -
						 * gb_edo.trans_dn_nbr_pkgs - gb_edo.dn_nbr_pkgs bal3 = bal3; else bal3 =
						 * relPkgsNbr - con3; } else bal3 = edoNbrPkgs; } else bal3 = blPkgs;
						 */
						balance = String.valueOf(bal3);
						// LogManager.instance.logInfo("*****balance fetchEdoDetails() :"+balance);
					}
				} else {
					String bal_cutoff = rs.getString("CUT_OFF_NBR_PKGS");
					String bal_short_land = rs.getString("NBR_PKGS_IN_PORT");
					int con3 = 0;
					int con4 = 0;
					int con5 = 0;

					if (bal_cutoff != null && !bal_cutoff.equals(""))
						con3 = Integer.parseInt(bal_cutoff);
					if (balance != null && !balance.equals(""))
						con4 = Integer.parseInt(balance);
					if (bal_short_land != null && !bal_short_land.equals(""))
						con5 = Integer.parseInt(bal_short_land);
					int blPkgs = 0;
					blPkgs = Integer.parseInt(mftNbr) - (con5 + Integer.parseInt(sumCtDNnbr));

					int relPkgsNbr = Integer.parseInt(declPkgs) - Integer.parseInt(maxQty)
							- Integer.parseInt(transLocalCheck) - Integer.parseInt(dnPkgs);

					int bal4 = con4 - con3;

					if (aaNm != null && !aaNm.equals("")) {
						bal4 = bal4 + Integer.parseInt(maxQty);
						if (relPkgsNbr <= bal4)
							bal4 = relPkgsNbr;
					}

					if (blPkgs < bal4)
						bal4 = blPkgs;

					balance = String.valueOf(bal4);
				}
				/*
				 * if (crgStatus.equals("T") || crgStatus.equals("R")) { String bal1 =
				 * rs.getString(27); String bal2 = rs.getString(28); dnPkgs = rs.getString(29);
				 * int con1 = Integer.parseInt(bal1); int con2 = Integer.parseInt(bal2);
				 * if(!checkjpjp && !checkjppsa){ balance = String.valueOf(con1) ; }else{ if
				 * (con1 < con2) { balance = String.valueOf(con1) ; } else { balance =
				 * String.valueOf(con2) ; } } }
				 */

				if (dnPkgs != null && !dnPkgs.equals(""))
					log.info("dnPkgs : " + dnPkgs);
				else
					dnPkgs = "0";
				EdoValueObjectOps edoVo = new EdoValueObjectOps();
				edoVo.setAACustCD(aaCustCd);
				// Added by Punitha on 30/07/2009
				edoVo.setAAIcNbr(aaIcNbr);
				edoVo.setBlNbr(blNbr);
				edoVo.setAcctNo(accNbr);
				edoVo.setPayMode(payNbr);
				edoVo.setNomWt(nomWt);
				edoVo.setNomVol(nomVol);
				edoVo.setAdpcustcd(adpcustcd);
				edoVo.setAdpIcNbr(adpIcNbr);
				edoVo.setAdpNm(adpNm);
				edoVo.setCaCustcd(caCustcd);
				edoVo.setCaIcNbr(caIcNbr);
				edoVo.setCaName(caNm);
				edoVo.setAAName(aaNm);
				edoVo.setATB(atb);
				edoVo.setCOD(cod);
				edoVo.setCrgDes(crgDes);
				edoVo.setDeclPkgs(declPkgs);
				edoVo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				edoVo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				edoVo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				edoVo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				int temp_balance = Integer.parseInt(balance);
				if (temp_balance < 0)
					balance = "0";
				edoVo.setBalance(balance);
				edoVo.setDeliveredPkgs(dnPkgs);
				edoVo.setCrgStatus(crgStatus);
				edoVo.setMarkings(mftmarkings);
				edoVo.setInVoyNbr(invoyNbr);
				edoVo.setOutVoyNbr(outvoyNbr);
				edoVo.setAACustCD(aaCustCd);
				edoVo.setVslName(vslName);
				edoVo.setConsName(consname);
				edoVo.setBillParty(billparty);
				edoVo.setBillTon(maxQty);
				edoVo.setDnNbr(transNbr);
				edoVo.setNoPkgs(transLocalCheck);
				// Warehouse
				edoVo.setWhInd(whInd);
				edoVo.setWhAggrNbr(whAggrNbr);
				edoVo.setWhRemarks(whRemarks);
				BJDetailsVect.add(edoVo);
			}
			log.info("END: *** fetchEdoDetails Result *****" + BJDetailsVect.size());
		} catch (Exception e) {
			log.info("Exception fetchEdoDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO fetchEdoDetails");
		}
		return BJDetailsVect;
	}

	private String getCTDNnbr(String mftsqnbr) throws BusinessException {
		String total = "";
		StringBuffer sql = new StringBuffer();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getCTDNnbr mftsqnbr:" + mftsqnbr);
			sql.append(
					" select sum(nvl(DN_NBR_PKGS,0)+nvl(CUT_OFF_NBR_PKGS,0)+nvl(trans_dn_nbr_pkgs,0)) from gb_edo where MFT_SEQ_NBR =:mftSeqNbr ");
			paramMap.put("mftSeqNbr", mftsqnbr);
			log.info("SQL" + sql.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			while (rs.next()) {
				total = rs.getString(1);
			}
			log.info("END: *** getCTDNnbr Result *****" + CommonUtility.deNull(total));

		} catch (Exception e) {
			log.info("Exception getCTDNnbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getCTDNnbr");
		}
		return total;
	}

	private String getEDOCTDNnbr(String mftsqnbr) throws BusinessException {
		String total = "";
		StringBuffer sql = new StringBuffer();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getEDOCTDNnbr mftsqnbr:" + mftsqnbr);
			sql.append(
					" select sum(nvl(trans_dn_nbr_pkgs,0)+nvl(CUT_OFF_NBR_PKGS,0)+nvl(DN_NBR_PKGS,0)) from gb_edo where MFT_SEQ_NBR =:mftSeqNbr ");
			paramMap.put("mftSeqNbr", mftsqnbr);
			log.info("SQL" + sql.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			while (rs.next()) {
				total = rs.getString(1);
			}
			log.info("END: *** getEDOCTDNnbr Result *****" + CommonUtility.deNull(total));
		} catch (Exception e) {
			log.info("Exception getEDOCTDNnbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getEDOCTDNnbr");
		}
		return total;
	}

	public List<EdoValueObjectOps> fetchDNList(String edoNbr, String searchcrg, String tesn_nbr)
			throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		List<EdoValueObjectOps> BJDetailsVect = new ArrayList<EdoValueObjectOps>();
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		chktesnJpJp_nbr(tesn_nbr);
		chktesnJpPsa_nbr(tesn_nbr);
		boolean checkEdoStuff = chkEDOStuffing(edoNbr); // vinayak added 07 jan 2004
		// Changed by Irene Tan on 20 Oct 2004 : SL-GBMS-20041020-1
		if ("ALL".equalsIgnoreCase(searchcrg)) {
			// NS amend sql to retrieve user name instead of user id (Jan 2023)
			sb.append(
					"SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')||'");
			sb.append(
					" @ '||DECODE(PRINT_LOCATION,'C','GCO','S',SST_MACHINE_NBR,PRINT_LOCATION)||DECODE(GATE_OUT_DTTM,'','',' ");
			sb.append(
					" (VehExit: '||TO_CHAR(GATE_OUT_DTTM,'DD/MM HH24:MI')||')') as trnsDate, CNTR_NBR, GOE.LANE_ID LANE_NBR, ");
			sb.append(" ( SELECT U.USER_NAME FROM ADM_USER U WHERE U.USER_ACCT = GOE.GATE_STAFF_ID) LAST_MODIFY_USER_ID, ");
			sb.append("  DN.TRUCK_NBR ");
			sb.append(
					" FROM DN_DETAILS DN LEFT JOIN (SELECT MAX(GATE_OUT_NBR) GATE_OUT_NBR, TRANS_REF FROM GATE_OUT_EVENT_DETAILS");
			sb.append(" GROUP BY TRANS_REF ) GOD ON DN.DN_NBR = GOD.TRANS_REF ");
			sb.append(" LEFT JOIN GATE_OUT_EVENT GOE ON GOE.GATE_OUT_NBR = GOD.GATE_OUT_NBR");
			sb.append(" WHERE EDO_ASN_NBR=:edoNbr order by dn_nbr");
			sql = sb.toString();
		} else if ((searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT"))) {
			// sql = "SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,to_char
			// (TRANS_DTTM,'DD/MM/YYYY HH24:MI') as trnsDate FROM DN_DETAILS WHERE
			// EDO_ASN_NBR='" + edoNbr + "' and tesn_asn_nbr is null";
			// sql = "SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,to_char
			// (TRANS_DTTM,'DD/MM/YYYY HH24:MI') as trnsDate, CNTR_NBR FROM DN_DETAILS WHERE
			// EDO_ASN_NBR='" + edoNbr + "' and tesn_asn_nbr is null order by dn_nbr";
			// To show gate-out time of truck. 26/5/2010.
			// NS amend sql to retrieve user name instead of user id (Jan 2023)
			sb.setLength(0);
			sb.append(
					"SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')||' @ '||");
			sb.append(
					" DECODE(PRINT_LOCATION,'C','GCO','S',SST_MACHINE_NBR,PRINT_LOCATION)||DECODE(GATE_OUT_DTTM,'','','");
			sb.append(
					" (VehExit: '||TO_CHAR(GATE_OUT_DTTM,'DD/MM HH24:MI')||')') as trnsDate, CNTR_NBR, GOE.LANE_ID LANE_NBR,");
			sb.append(" ( SELECT U.USER_NAME FROM ADM_USER U WHERE U.USER_ACCT = GOE.GATE_STAFF_ID) LAST_MODIFY_USER_ID, ");
			sb.append(" DN.TRUCK_NBR ");
			sb.append(" FROM DN_DETAILS DN LEFT JOIN ( ");
			sb.append(" SELECT MAX(GATE_OUT_NBR) GATE_OUT_NBR, TRANS_REF FROM GATE_OUT_EVENT_DETAILS ");
			sb.append(" GROUP BY TRANS_REF ) GOD ON DN.DN_NBR = GOD.TRANS_REF ");
			sb.append(" LEFT JOIN GATE_OUT_EVENT GOE ON GOE.GATE_OUT_NBR = GOD.GATE_OUT_NBR");
			sb.append(" WHERE EDO_ASN_NBR=:edoNbr and tesn_asn_nbr is null order by dn_nbr");
			sql = sb.toString();
		}

		else {
			if (checkEdoStuff) {
				// sql = "SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,to_char
				// (TRANS_DTTM,'DD/MM/YYYY HH24:MI') as trnsDate FROM DN_DETAILS WHERE
				// EDO_ASN_NBR='" + edoNbr + "' and tesn_asn_nbr='" + tesn_nbr + "'";
				// sql = "SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,to_char
				// (TRANS_DTTM,'DD/MM/YYYY HH24:MI') as trnsDate, CNTR_NBR FROM DN_DETAILS WHERE
				// EDO_ASN_NBR='" + edoNbr + "' and tesn_asn_nbr='" + tesn_nbr + "' order by
				// dn_nbr";
				// To show gate-out time of truck. 26/5/2010.
				// NS amend sql to retrieve user name instead of user id (Jan 2023)
				sb.setLength(0);
				sb.append(
						"SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')||' ");
				sb.append(" @ '||DECODE(PRINT_LOCATION,'C','GCO','S',SST_MACHINE_NBR,PRINT_LOCATION)||");
				sb.append(
						" DECODE(GATE_OUT_DTTM,'','',' (VehExit: '||TO_CHAR(GATE_OUT_DTTM,'DD/MM HH24:MI')||')') as trnsDate,");
				sb.append(" CNTR_NBR, GOE.LANE_ID LANE_NBR, ");
				sb.append(" ( SELECT U.USER_NAME FROM ADM_USER U WHERE U.USER_ACCT = GOE.GATE_STAFF_ID) LAST_MODIFY_USER_ID, DN.TRUCK_NBR ");
				sb.append(" FROM DN_DETAILS DN LEFT JOIN (");
				sb.append(" SELECT MAX(GATE_OUT_NBR) GATE_OUT_NBR, TRANS_REF FROM GATE_OUT_EVENT_DETAILS ");
				sb.append(" GROUP BY TRANS_REF) GOD ON DN.DN_NBR = GOD.TRANS_REF ");
				sb.append(" LEFT JOIN GATE_OUT_EVENT GOE ON GOE.GATE_OUT_NBR = GOD.GATE_OUT_NBR");
				sb.append(" WHERE EDO_ASN_NBR=:edoNbr and tesn_asn_nbr=:tesnNbr order by dn_nbr");
				sb.append("");
				sql = sb.toString();
			} else {
				// sql = "SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,to_char
				// (TRANS_DTTM,'DD/MM/YYYY HH24:MI') as trnsDate FROM DN_DETAILS WHERE
				// EDO_ASN_NBR='" + edoNbr + "' and tesn_asn_nbr='" + tesn_nbr + "'";
				// sql = "SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,to_char
				// (TRANS_DTTM,'DD/MM/YYYY HH24:MI') as trnsDate, CNTR_NBR FROM DN_DETAILS WHERE
				// EDO_ASN_NBR='" + edoNbr + "' and tesn_asn_nbr='" + tesn_nbr + "' order by
				// dn_nbr";
				// To show gate-out time of truck. 26/5/2010.
				// NS amend sql to retrieve user name instead of user id (Jan 2023)
				sb.setLength(0);
				sb.append(
						"SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')||' @");
				sb.append(
						" '||DECODE(PRINT_LOCATION,'C','GCO','S',SST_MACHINE_NBR,PRINT_LOCATION)||DECODE(GATE_OUT_DTTM,'','','");
				sb.append(" (VehExit: '||TO_CHAR(GATE_OUT_DTTM,'DD/MM HH24:MI')||')') as trnsDate,");
				sb.append(" CNTR_NBR, GOE.LANE_ID LANE_NBR, ");
				sb.append(" ( SELECT U.USER_NAME FROM ADM_USER U WHERE U.USER_ACCT = GOE.GATE_STAFF_ID) LAST_MODIFY_USER_ID, DN.TRUCK_NBR ");
				sb.append(" FROM DN_DETAILS DN LEFT JOIN ( ");
				sb.append(" SELECT MAX(GATE_OUT_NBR) GATE_OUT_NBR, TRANS_REF FROM GATE_OUT_EVENT_DETAILS");
				sb.append(" GROUP BY TRANS_REF) GOD ON DN.DN_NBR = GOD.TRANS_REF ");
				sb.append(" LEFT JOIN GATE_OUT_EVENT GOE ON GOE.GATE_OUT_NBR = GOD.GATE_OUT_NBR");
				sb.append("  WHERE EDO_ASN_NBR=:edoNbr and tesn_asn_nbr=:tesnNbr order by dn_nbr");
				sql = sb.toString();
			}
		}
		// End Changed by Irene Tan on 20 Oct 2004 : SL-GBMS-20041020-1
		// LogManager.instance.logInfo("sql fetchDNList() :"+sql);

		try {
			log.info("START: fetchDNList edoNbr:" + edoNbr + "searchcrg:" + searchcrg + "tesn_nbr:" + tesn_nbr);
			paramMap.put("edoNbr", edoNbr);
			if (!"ALL".equalsIgnoreCase(searchcrg) && checkEdoStuff
					&& (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT"))) {
				paramMap.put("tesnNbr", tesn_nbr);
			}
			log.info("SQL" + sql.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			String dnNbr;
			String noPkgs;
			String billTon;
			String dnStatus;
			String trans = "";
			String billStatus;
			String cntrNo = "";
			while (rs.next()) {
				dnNbr = rs.getString("DN_NBR");
				noPkgs = rs.getString("NBR_PKGS");
				billTon = rs.getString("BILLABLE_TON");
				dnStatus = rs.getString("DN_STATUS");
				trans = rs.getString("trnsDate");
				billStatus = rs.getString("BILL_STATUS");
				cntrNo = rs.getString("CNTR_NBR");

				EdoValueObjectOps edoVo = new EdoValueObjectOps();
				edoVo.setDnNbr(dnNbr);
				edoVo.setNoPkgs(noPkgs);
				edoVo.setBillTon(billTon);
				edoVo.setDnStatus(dnStatus);
				edoVo.setTransDate(trans);
				edoVo.setBillStatus(billStatus);
				edoVo.setCntrNo(cntrNo);
				edoVo.setLane_nbr(CommonUtility.deNull(rs.getString("LANE_NBR")));
				edoVo.setLast_modify_user_id(CommonUtility.deNull(rs.getString("LAST_MODIFY_USER_ID")));
				edoVo.setVech1(CommonUtility.deNull(rs.getString("TRUCK_NBR")));
				BJDetailsVect.add(edoVo);
			}
			
			log.info("END: *** fetchDNList Result *****" + BJDetailsVect.size());
		} catch (Exception e) {
			log.info("Exception fetchDNList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO fetchDNList");
		}
		return BJDetailsVect;
	}

	public boolean chktesnJpJp(String edoNbr) throws BusinessException {
		boolean tesnjpjp = false;
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START:chktesnJpJp DAO START edoNbr:" + edoNbr);
			sb.append(
					" SELECT * FROM TESN_JP_JP JPJP,ESN E WHERE E.ESN_ASN_NBR = JPJP.ESN_ASN_NBR AND E.ESN_STATUS ='A' AND EDO_ASN_NBR =:edoNbr AND E.TRANS_CRG != 'Y'  ");
			paramMap.put("edoNbr", edoNbr);
			log.info("SQL" + sb.toString() + "paramMap" + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				tesnjpjp = true;
			}
			
			log.info("END: *** chktesnJpJp Result *****" + tesnjpjp);
		} catch (NullPointerException e) {
			log.info("Exception chktesnJpJp : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chktesnJpJp : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:chktesnJpJp DAO end tesnjpjp:" + tesnjpjp);
		}
		return tesnjpjp;
	}

	public List<EdoValueObjectOps> fetchSubAdpDetails(String edoNbr) throws BusinessException {
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		List<EdoValueObjectOps> subAdpVect = new ArrayList<EdoValueObjectOps>();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: fetchSubAdpDetails edoNbr:" + edoNbr);
			sb.append(" select TRUCKER_IC, TRUCKER_NM from SUB_ADP where ESN_ASN_NBR =:edoNbr ");
			sb.append(" and EDO_ESN_IND = 1 and STATUS_CD ='A' order by SUB_ADP_NBR ");
			paramMap.put("edoNbr", edoNbr);
			log.info("SQL" + sb.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			String truckerIc;
			String trcukerNm;
			int count = 0;
			while (rs.next()) {

				truckerIc = rs.getString("TRUCKER_IC");
				trcukerNm = rs.getString("TRUCKER_NM");

				EdoValueObjectOps edoVo = new EdoValueObjectOps();
				edoVo.setVech1(count++ + "");
				edoVo.setVech2(truckerIc);
				edoVo.setVech3(trcukerNm);
				subAdpVect.add(edoVo);
			}
			
			log.info("END: *** fetchSubAdpDetails Result *****" + subAdpVect.size());

		} catch (NullPointerException e) {
			log.info("Exception fetchSubAdpDetails : ", e);
		} catch (Exception e) {
			log.info("Exception fetchSubAdpDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO fetchSubAdpDetails");
		}
		return subAdpVect;
	}

	public int getSpencialPackage(String edoNbr) throws BusinessException {
		int pkgs = 0;
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getSpencialPackage edoNbr:" + edoNbr);
			sb.append("  select b.SPECIAL_ACTION_PKGS from gb_edo a, manifest_details b where a.mft_seq_nbr= ");
			sb.append(" b.mft_seq_nbr and a.EDO_ASN_NBR=:edoNbr and a.CRG_STATUS='L' ");
			paramMap.put("edoNbr", edoNbr);
			log.info("SQL" + sb.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				pkgs = rs.getInt("SPECIAL_ACTION_PKGS");
			}
			log.info("END: *** getSpencialPackage Result *****" + pkgs);
		} catch (NullPointerException e) {
			log.info("Exception getSpencialPackage : ", e);
		} catch (Exception e) {
			log.info("Exception getSpencialPackage : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getSpencialPackage");
		}
		return pkgs;
	}

	public List<EdoValueObjectOps> fetchShutoutDNDetail(String strEdoNo, String dnNo) throws BusinessException {
		List<EdoValueObjectOps> BJDetailsVect = new ArrayList<EdoValueObjectOps>();
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("fetchShutoutDNDetail DAO START strEdoNo:" + strEdoNo + "dnNo:" + dnNo);
			if (isEsn(null, strEdoNo)) {

				sb.append("SELECT ");
				sb.append("	gb_edo.edo_asn_nbr, ");
				sb.append("	TO_CHAR (dn_details.TRANS_DTTM, ");
				sb.append("	'dd/mm/yyyy hh24:mi') AS trans_dttm, ");
				sb.append("	vessel_call.vsl_nm, ");
				sb.append("	vessel_call.in_voy_nbr, ");
				sb.append("	vessel_call.out_voy_nbr, ");
				sb.append(
						"	TO_CHAR((SELECT MIN(UA.TRANS_DTTM) FROM UA_DETAILS UA, GB_EDO EDO WHERE EDO.ESN_ASN_NBR = UA.ESN_ASN_NBR AND EDO.EDO_ASN_NBR =:strEdoNo ");
				sb.append("), 'DD/MM/YYYY HH24:MI') AS FIRST_UA, ");
				sb.append("	gb_edo.nbr_pkgs AS edoNbr, ");
				sb.append("	dn_details.dn_nbr, ");
				sb.append("	dn_details.NBR_PKGS, ");
				sb.append("	dn_details.DP_IC_NBR, ");
				sb.append("	dn_details.DP_IC_TYPE, ");
				sb.append("	dn_details.DP_NM, ");
				sb.append(
						"	gb_edo.NBR_PKGS - NVL(gb_edo.DN_NBR_PKGS, 0) - NVL(gb_edo.TRANS_DN_NBR_PKGS, 0) AS BALANCE, ");
				sb.append("	gb_edo.dn_nbr_pkgs, ");
				sb.append("	gb_edo.nom_wt, ");
				sb.append("	gb_edo.NOM_VOL, ");
				sb.append("	gb_edo.acct_nbr, ");
				sb.append("	gb_edo.EDO_ASN_NBR, ");
				sb.append("	gb_edo.aa_nm, ");
				sb.append("	gb_edo.WH_IND, ");
				sb.append("	gb_edo.WH_AGGR_NBR, ");
				sb.append("	gb_edo.WH_REMARKS, ");
				sb.append("	gb_edo.ADP_CUST_CD, ");
				sb.append("	gb_edo.ADP_NM, ");
				sb.append("	gb_edo.ADP_IC_TDBCR_NBR, ");
				sb.append("	gb_edo.PAYMENT_MODE, ");
				sb.append("	gb_edo.ACCT_NBR, ");
				sb.append("	gb_edo.NBR_PKGS, ");
				sb.append("	gb_edo.CRG_STATUS, ");
				sb.append("	esn_details.CRG_DES, ");
				sb.append("	ESN_MARKINGS.MARKINGS, ");
				sb.append("	NVL (gb_edo.release_nbr_pkgs, ");
				sb.append("	0) AS releaseOty, ");
				sb.append("	dn_details.billable_ton, ");
				sb.append("	gb_edo.CRG_STATUS AS TRANS_TYPE, ");
				sb.append("	gb_edo.bl_nbr, ");
				sb.append("	gb_edo.var_nbr, ");
				sb.append("	gb_edo.CUT_OFF_NBR_PKGS, ");
				sb.append("	NVL (gb_edo.release_nbr_pkgs, ");
				sb.append("	0) - NVL (gb_edo.trans_dn_nbr_pkgs, ");
				sb.append("	0) AS trnsPkgs, ");
				sb.append("	dn_details.CRG_DEST, ");
				sb.append("	esn_details.nbr_pkgs AS ESNPKGS, ");
				sb.append("	gb_edo.ESN_ASN_NBR, ");
				sb.append("	TO_CHAR(berthing.ATB_DTTM, 'DD/MM/YYYY HH24:MI') AS atb, ");
				sb.append("	vessel_call.terminal, ");
				sb.append("	vessel_call.scheme, ");
				sb.append("	vessel_call.combi_gc_scheme, ");
				sb.append("	vessel_call.combi_gc_ops_ind ");
				sb.append("FROM ");
				sb.append("	gb_edo, ");
				sb.append("	dn_details, ");
				sb.append("	vessel_call, ");
				sb.append("	berthing , ");
				sb.append("	esn_details, ");
				sb.append("	ESN_MARKINGS ");
				sb.append("WHERE ");
				sb.append("	gb_edo.var_nbr = vessel_call.vv_cd ");
				sb.append("	AND vessel_call.vv_cd = berthing.vv_cd ");
				sb.append("	AND dn_details.edo_asn_nbr = gb_edo.edo_asn_nbr ");
				sb.append("	AND SHIFT_IND = 1 ");
				sb.append("	AND gb_edo.edo_status = 'A' ");
				sb.append("	AND ESN_DETAILS.ESN_ASN_NBR = GB_EDO.ESN_ASN_NBR ");
				sb.append("	AND ESN_MARKINGS.ESN_ASN_NBR = GB_EDO.ESN_ASN_NBR ");
				sb.append("	AND dn_details.DN_NBR =:dnNo ");
				sb.append("	AND GB_EDO.EDO_ASN_NBR =:strEdoNo ");
			} else {

				sb.append("SELECT gb_edo.edo_asn_nbr, ");
				sb.append(" TO_CHAR (dn_details.TRANS_DTTM, 'dd/mm/yyyy hh24:mi') AS trans_dttm, ");
				sb.append(" vessel_call.vsl_nm, ");
				sb.append(" vessel_call.in_voy_nbr, ");
				sb.append(" vessel_call.out_voy_nbr, ");
				sb.append(
						" TO_CHAR((SELECT MIN(UA.TRANS_DTTM) FROM UA_DETAILS UA,GB_EDO EDO WHERE EDO.ESN_ASN_NBR = UA.ESN_ASN_NBR AND EDO.EDO_ASN_NBR = :strEdoNo ");
				sb.append("), 'DD/MM/YYYY HH24:MI') AS FIRST_UA, ");
				sb.append(" gb_edo.nbr_pkgs AS edoNbr, ");
				sb.append(" dn_details.dn_nbr, ");
				sb.append(" dn_details.NBR_PKGS, ");
				sb.append(" dn_details.DP_IC_NBR, ");
				sb.append(" dn_details.DP_IC_TYPE, ");
				sb.append(" dn_details.DP_NM, ");
				sb.append(
						" gb_edo.NBR_PKGS - nvl(gb_edo.DN_NBR_PKGS,0) - nvl(gb_edo.TRANS_DN_NBR_PKGS,0) AS BALANCE, ");
				sb.append(" gb_edo.dn_nbr_pkgs, ");
				sb.append(" gb_edo.nom_wt, ");
				sb.append(" gb_edo.NOM_VOL, ");
				sb.append(" gb_edo.acct_nbr, ");
				sb.append(" gb_edo.EDO_ASN_NBR, ");
				sb.append(" gb_edo.aa_nm, ");
				sb.append(" gb_edo.WH_IND, ");
				sb.append(" gb_edo.WH_AGGR_NBR, ");
				sb.append(" gb_edo.WH_REMARKS, ");
				sb.append(" gb_edo.ADP_CUST_CD, ");
				sb.append(" gb_edo.ADP_NM, ");
				sb.append(" gb_edo.ADP_IC_TDBCR_NBR, ");
				sb.append(" gb_edo.PAYMENT_MODE, ");
				sb.append(" gb_edo.ACCT_NBR, ");
				sb.append(" gb_edo.NBR_PKGS, ");
				sb.append(" gb_edo.CRG_STATUS, ");
				sb.append(" TESN_PSA_JP.CRG_DES, ");
				sb.append(" ESN_MARKINGS.MARKINGS, ");
				sb.append(" NVL (gb_edo.release_nbr_pkgs, 0) AS releaseOty, ");
				sb.append(" dn_details.billable_ton, gb_edo.CRG_STATUS AS TRANS_TYPE, ");
				sb.append(" gb_edo.bl_nbr, gb_edo.var_nbr, gb_edo.CUT_OFF_NBR_PKGS, ");
				sb.append(" NVL (gb_edo.release_nbr_pkgs, 0) - NVL (gb_edo.trans_dn_nbr_pkgs, 0) AS trnsPkgs, ");
				sb.append(" dn_details.CRG_DEST, ");
				sb.append(" gb_edo.ESN_ASN_NBR, ");
				sb.append(" TESN_PSA_JP.nbr_pkgs AS ESNPKGS, ");
				sb.append(" TO_CHAR(berthing.ATB_DTTM,'DD/MM/YYYY HH24:MI') as atb, ");
				sb.append(
						" vessel_call.terminal, vessel_call.scheme, vessel_call.combi_gc_scheme,  vessel_call.combi_gc_ops_ind ");
				sb.append(" FROM ");
				sb.append(" gb_edo, dn_details, vessel_call, berthing , TESN_PSA_JP, ESN_MARKINGS ");
				sb.append(" WHERE ");
				sb.append(" gb_edo.var_nbr = vessel_call.vv_cd ");
				sb.append(" AND vessel_call.vv_cd = berthing.vv_cd ");
				sb.append(" AND dn_details.edo_asn_nbr = gb_edo.edo_asn_nbr ");
				sb.append(" AND SHIFT_IND = 1 AND gb_edo.edo_status = 'A' ");
				sb.append(" AND TESN_PSA_JP.ESN_ASN_NBR = GB_EDO.ESN_ASN_NBR ");
				sb.append(" AND ESN_MARKINGS.ESN_ASN_NBR = GB_EDO.ESN_ASN_NBR ");
				sb.append(" AND dn_details.DN_NBR =:dnNo ");
				sb.append(" AND GB_EDO.EDO_ASN_NBR =:strEdoNo ");
			}
			paramMap.put("strEdoNo", strEdoNo);
			paramMap.put("dnNo", dnNo);
			paramMap.put("strEdoNo", strEdoNo);
			log.info("SQL" + sb.toString());
			log.info(" *** fetchShutoutDNDetail params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				String cuurnetdate = CommonUtility.deNull(rs.getString("trans_dttm"));
				String edoasnnbr = CommonUtility.deNull(rs.getString("edo_asn_nbr"));
				String vName = CommonUtility.deNull(rs.getString("vsl_nm"));
				String outVoyNbr = CommonUtility.deNull(rs.getString("out_voy_nbr"));
				String transType = CommonUtility.deNull(rs.getString("TRANS_TYPE"));
				edoasnnbr = CommonUtility.deNull(rs.getString("EDO_ASN_NBR"));
				String esnAsnNbr = CommonUtility.deNull(rs.getString("ESN_ASN_NBR"));
				String declaredWt = CommonUtility.deNull(rs.getString("nom_wt"));
				String declaredVol = CommonUtility.deNull(rs.getString("NOM_VOL"));
				String declaredQty = CommonUtility.deNull(rs.getString("edoNbr"));
				String transQty = CommonUtility.deNull(rs.getString("NBR_PKGS"));
				String nricNo = CommonUtility.deNull(rs.getString("DP_IC_NBR"));
				String icType = CommonUtility.deNull(rs.getString("DP_IC_TYPE"));
				String name = CommonUtility.deNull(rs.getString("DP_NM"));
				String accNo = CommonUtility.deNull(rs.getString("acct_nbr"));
				String vvcd = CommonUtility.deNull(rs.getString("var_nbr"));
				String SchemeName = getSchemeName(accNo, vvcd);
				String crgDes = CommonUtility.deNull(rs.getString("CRG_DES"));
				String firstUa = CommonUtility.deNull(rs.getString("FIRST_UA"));
				String balance = CommonUtility.deNull(rs.getString("BALANCE"));
				String whInd = CommonUtility.deNull(rs.getString("WH_IND"));
				String adpNm = CommonUtility.deNull(rs.getString("ADP_NM"));
				String adpIcNbr = CommonUtility.deNull(rs.getString("ADP_IC_TDBCR_NBR"));
				String adpcustcd = CommonUtility.deNull(rs.getString("ADP_CUST_CD"));
				if (adpcustcd != null) {

					sb.setLength(0);

					sb.append("SELECT ");
					sb.append("	CC.CO_NM, ");
					sb.append("	( ");
					sb.append("	SELECT ");
					sb.append(
							"		DECODE(TDB_CR_NBR, NULL, UEN_NBR, TDB_CR_NBR FROM customer WHERE cust_cd =:adpcustcd) TDB_CR_NBR ");
					sb.append("	FROM ");
					sb.append("		CUSTOMER, ");
					sb.append("		COMPANY_CODE CC ");
					sb.append("	WHERE ");
					sb.append("		CUST_CD = CC.CO_CD ");
					sb.append("		AND cust_cd =:adpcustcd)TDB_CR_NBR ");
					sb.append("FROM ");
					sb.append("	CUSTOMER, ");
					sb.append("	COMPANY_CODE CC ");
					sb.append("WHERE ");
					sb.append("	CUST_CD = CC.CO_CD ");
					sb.append("	AND cust_cd =:adpcustcd");
					paramMap.put("adpcustcd", adpcustcd);
					log.info("SQL" + sb.toString());
					log.info(" *** fetchShutoutDNDetail params *****" + paramMap.toString());
					SqlRowSet rs1 = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
					if (rs1.next()) {
						adpNm = CommonUtility.deNull(rs1.getString("co_nm"));
						adpIcNbr = CommonUtility.deNull(rs1.getString("TDB_CR_NBR"));
					}
				}
				String payNbr = rs.getString("PAYMENT_MODE");
				String billparty = "";
				String accNbr = rs.getString("ACCT_NBR");
				if (payNbr.equals("A")) {
					sb.setLength(0);
					sb.append(
							" SELECT C.CO_NM FROM CUST_ACCT CA,COMPANY_CODE C WHERE CA.CUST_CD=C.CO_CD AND CA.ACCT_NBR=:accNbr  ");
					paramMap.put("accNbr", accNbr);
					log.info("SQL" + sb.toString());
					log.info(" *** fetchShutoutDNDetail params *****" + paramMap.toString());
					SqlRowSet rs2 = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
					if (rs2.next()) {
						billparty = CommonUtility.deNull(rs2.getString(1));
						payNbr = accNbr;
					}
				}
				String declPkgs = rs.getString("NBR_PKGS");
				String whAggrNbr = CommonUtility.deNull(rs.getString("WH_AGGR_NBR"));
				String whRemarks = CommonUtility.deNull(rs.getString("WH_REMARKS"));
				String crgDesc = "";
				if ("O".equalsIgnoreCase(crgDes))
					crgDesc = "Out Of JP";
				else if ("V".equalsIgnoreCase(crgDes))
					crgDesc = "Vessel";
				else if ("L".equalsIgnoreCase(crgDes))
					crgDesc = "Leased Area";

				double nom_wt = 0;
				nom_wt = Double.parseDouble(declaredWt);
				double nom_vol = 0;
				double Bill_ton = 0;
				nom_vol = Double.parseDouble(declaredVol);

				if ((nom_wt / 1000) > nom_vol) {
					Bill_ton = nom_wt / 1000;
				} else {
					Bill_ton = nom_vol;
				}
				String billableUnit = "" + Bill_ton;
				String crgStatus = CommonUtility.deNull(rs.getString("CRG_STATUS"));
				String markings = CommonUtility.deNull(rs.getString("MARKINGS"));
				String atb = rs.getString("atb");

				EdoValueObjectOps edoVo = new EdoValueObjectOps();
				edoVo.setTransDate(cuurnetdate);
				edoVo.setVslName(vName);
				edoVo.setInVoyNbr(outVoyNbr);
				edoVo.setOutVoyNbr(outVoyNbr);
				edoVo.setNomWt(declaredWt);
				edoVo.setNomVol(declaredVol);
				edoVo.setNoPkgs(declaredQty);
				edoVo.setDeliveredPkgs(transQty);
				edoVo.setAAIcNbr(nricNo);
				edoVo.setAAName(name);
				edoVo.setAACustCD(icType);
				edoVo.setAcctNo(accNo);
				edoVo.setBillTon(billableUnit);
				edoVo.setEdoAsnNbr(edoasnnbr);
				edoVo.setTransType(transType);
				edoVo.setCrgDestination(crgDesc);
				edoVo.setFirstUa(firstUa);
				edoVo.setCOD(firstUa);
				edoVo.setWhInd(whInd);
				int temp_balance = Integer.parseInt(balance);
				if (temp_balance < 0)
					balance = "0";
				edoVo.setBalance(balance);
				edoVo.setDnStatus(SchemeName);

				edoVo.setBlNbr(esnAsnNbr);
				edoVo.setConsName("");
				edoVo.setAdpNm(adpNm);
				edoVo.setAdpIcNbr(adpIcNbr);
				edoVo.setCaName("");
				edoVo.setPayMode(payNbr);
				edoVo.setBillParty(billparty);
				edoVo.setDeclPkgs(declPkgs);
				edoVo.setWhAggrNbr(whAggrNbr);
				edoVo.setWhRemarks(whRemarks);
				edoVo.setCrgStatus(crgStatus);
				edoVo.setCrgDes(crgDes);
				edoVo.setMarkings(markings);
				edoVo.setATB(atb);
				edoVo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				edoVo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				edoVo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				edoVo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				BJDetailsVect.add(edoVo);

			}
			log.info("END: *** fetchShutoutDNDetail Result *****" + BJDetailsVect.size());

		} catch (Exception e) {
			log.info("Exception END:fetchShutoutDNDetail : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:fetchShutoutDNDetail");
		}
		return BJDetailsVect;
	}

	private boolean isEsn(String esnAsnNO, String edoAsnNbr) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		if (esnAsnNO != null) {
			sb.append("SELECT TRANS_TYPE FROM ESN WHERE ESN_ASN_NBR=:esnAsnNO");
		} else {
			sb.append(
					"SELECT ESN.TRANS_TYPE FROM ESN ESN,GB_EDO EDO WHERE ESN.ESN_ASN_NBR=EDO.ESN_ASN_NBR AND EDO.EDO_ASN_NBR=;edoAsnNbr");
		}
		boolean isEsn = false;
		String type = "C";
		try {
			log.info("START:isEsn DAO esnAsnNO:" + esnAsnNO + "edoAsnNbr:" + edoAsnNbr);
			paramMap.put("esnAsnNO", esnAsnNO);
			log.info("SQL" + sb.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				type = CommonUtility.deNull(rs.getString("TRANS_TYPE"));
			}
			if (type.equals("E"))
				isEsn = true;
			
			log.info("END: *** isEsn Result *****" + isEsn);
		} catch (NullPointerException e) {
			log.info("Exception isEsn : ", e);
		} catch (Exception se) {
			log.info("Exception isEsn : ", se);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isEsn ");
		}
		return isEsn;
	}

	// added on 12/09/02
	private String getSchemeName(String accNo, String vvcd) throws BusinessException {
		String sql = "";
		String sch = "";
		String schemeName = "";
		SqlRowSet rs = null;
		String sql1 = "SELECT SCHEME FROM VESSEL_CALL WHERE VV_CD =:vvcd ";
		sql = "SELECT SCHEME_CD FROM VESSEL_SCHEME WHERE ACCT_NBR=:accNo";
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getSchemeName accNo:" + accNo + "vvcd:" + vvcd);
			paramMap.put("vvcd", vvcd);
			log.info("SQL" + sql1);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
			if (rs.next()) {
				schemeName = rs.getString("SCHEME");
			}

			// <cfg: add new scheme for Wooden Craft: JWP, 27.may.08>
			// if (! (schemeName.equals("JLR") || schemeName.equals("JNL") ||
			// schemeName.equals("JBT"))) {
			// add new scheme for LCT, 13.feb.11 by hpeng
			if (!(schemeName.equals("JLR") || schemeName.equals("JNL") || schemeName.equals("JBT")
					|| schemeName.equals("JWP") || schemeName.equals(ProcessChargeConst.LCT_SCHEME))) {
				// <cfg: add new scheme for Wooden Craft: JWP, 27.may.08/>
				paramMap.put("accNo", accNo);
				log.info("SQL" + sql);
				SqlRowSet rs1 = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs1.next()) {
					sch = rs1.getString("SCHEME_CD");
				}
			}
			log.info("END: *** getSchemeName Result *****" + CommonUtility.deNull(sch));
		} catch (NullPointerException ne) {
			log.info("Exception getSchemeName : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSchemeName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:getSchemeName");
		}
		return sch;
	} // end of method getSchemeName

	public List<EdoValueObjectOps> getVechDetails(String dnNbr) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		List<EdoValueObjectOps> vechDetails = new ArrayList<>();
		EdoValueObjectOps edoVo = new EdoValueObjectOps();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START:  *** getVechDetails Dao Start : *** " + dnNbr);
			sql = "SELECT VEH_NO,DN_VEH_SEQ FROM DN_VEH WHERE DN_NBR = :dnNbr ORDER BY DN_VEH_SEQ";
			paramMap.put("dnNbr", dnNbr);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			int vechSqNbr = 0;
			while (rs.next()) {
				vechSqNbr = rs.getInt("DN_VEH_SEQ");
				if (vechSqNbr == 1)
					edoVo.setVech1(CommonUtility.deNull(rs.getString("VEH_NO")));
				if (vechSqNbr == 2)
					edoVo.setVech2(CommonUtility.deNull(rs.getString("VEH_NO")));
				if (vechSqNbr == 3)
					edoVo.setVech3(CommonUtility.deNull(rs.getString("VEH_NO")));
				if (vechSqNbr == 4)
					edoVo.setVech4(CommonUtility.deNull(rs.getString("VEH_NO")));
				if (vechSqNbr == 5)
					edoVo.setVech5(CommonUtility.deNull(rs.getString("VEH_NO")));

				vechDetails.add(edoVo);
			} // while
			log.info("END: *** getVechDetails SQL *****" + sql);
		} catch (NullPointerException ne) {
			log.info("Exception getVechDetails : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVechDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getVechDetails  END *****");
		}
		return vechDetails;
	}

	public List<EdoValueObjectContainerised> fetchDNDetail(String edoNbr, String status, String searchcrg,
			String tesn_nbr) throws BusinessException {
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		List<EdoValueObjectContainerised> fetchDNDetail = new ArrayList<EdoValueObjectContainerised>();
		Map<String, String> paramMap = new HashMap<String, String>();

		log.info("Writing from DnBeanBean.fetchDNDetail");

		String chktesnJpJp_nbr = chktesnJpJp_nbr(tesn_nbr);
		String chktesnJpPsa_nbr = chktesnJpPsa_nbr(tesn_nbr);
		if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
			sb.append(
					"select gb_edo.edo_asn_nbr,to_char(dn_details.TRANS_DTTM,'dd/mm/yyyy hh24:mi') as transDate,vessel_call.vsl_nm , vessel_call.in_voy_nbr,to_char(berthing.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb,");
			sb.append(
					"to_char(berthing.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,manifest_details.CRG_DES,manifest_details.CNTR_SIZE , manifest_details.CNTR_TYPE ,");
			sb.append(
					"mft_markings.mft_markings,gb_edo.nbr_pkgs as edoNbr,dn_details.dn_nbr ,dn_details.NBR_PKGS,dn_details.DP_IC_NBR ,dn_details.DP_IC_TYPE ,dn_details.DP_NM,");
			sb.append("dn_details.CNTR_NBR,to_char(dn_details.STUFF_DTTM,'dd/mm/yyyy hh24:mi') as stuff_dt,"); // VANI
			sb.append(
					"gb_edo.nbr_pkgs - gb_edo.trans_nbr_pkgs - gb_edo.release_nbr_pkgs - gb_edo.dn_nbr_pkgs as balance ,nvl(gb_edo.nbr_pkgs,0) - nvl(gb_edo.release_nbr_pkgs,0) - nvl(gb_edo.trans_dn_nbr_pkgs,0)- nvl(gb_edo.dn_nbr_pkgs,0)  as bal2,");
			sb.append(
					"gb_edo.dn_nbr_pkgs ,gb_edo.nom_wt ,gb_edo.NOM_VOL ,gb_edo.acct_nbr , gb_edo.EDO_ASN_NBR , bl_cntr_details.CNTR_NBR,gb_edo.aa_nm,nvl(gb_edo.release_nbr_pkgs,0) as releaseOty,");
			sb.append(
					"dn_details.billable_ton,gb_edo.CRG_STATUS as TRANS_TYPE,gb_edo.bl_nbr,gb_edo.var_nbr,gb_edo.CUT_OFF_NBR_PKGS,manifest_details.NBR_PKGS_IN_PORT,manifest_details.NBR_PKGS as mftNbr,nvl(gb_edo.release_nbr_pkgs,0)-nvl(gb_edo.trans_dn_nbr_pkgs,0) as trnsPkgs,gb_edo.mft_seq_nbr as mftsqnbr from gb_edo , dn_details,vessel_call,berthing ,bl_cntr_details ,manifest_details ,  mft_markings ");
			sb.append(
					"where gb_edo.var_nbr =  vessel_call.vv_cd and vessel_call.vv_cd = berthing.vv_cd  and manifest_details.MFT_SEQ_NBR = mft_markings.MFT_SQ_NBR and ");
			sb.append(
					"gb_edo.mft_seq_nbr = manifest_details.mft_seq_nbr AND dn_details.edo_asn_nbr = gb_edo.edo_asn_nbr and SHIFT_IND = 1 and gb_edo.edo_status='A' and ");
			sb.append("manifest_details.mft_seq_nbr = bl_cntr_details.mft_seq_nbr(+) and dn_details.dn_nbr =:edoNbr");
		} else {
			if (chktesnJpJp_nbr.equals("Y")) {
				sb.append(
						"select gb_edo.edo_asn_nbr,to_char(dn_details.TRANS_DTTM,'dd/mm/yyyy hh24:mi') as trans_dttm,vessel_call.vsl_nm , vessel_call.in_voy_nbr,to_char(berthing.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb,");
				sb.append(
						"to_char(berthing.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,manifest_details.CRG_DES,manifest_details.CNTR_SIZE , manifest_details.CNTR_TYPE ,");
				sb.append(
						"mft_markings.mft_markings,gb_edo.nbr_pkgs as edoNbr,dn_details.dn_nbr ,dn_details.NBR_PKGS,dn_details.DP_IC_NBR ,dn_details.DP_IC_TYPE ,dn_details.DP_NM,");
				sb.append("dn_details.CNTR_NBR,to_char(dn_details.STUFF_DTTM,'dd/mm/yyyy hh24:mi') as stuff_dt,"); // VANI
				sb.append(
						"gb_edo.trans_nbr_pkgs - gb_edo.release_nbr_pkgs - gb_edo.trans_dn_nbr_pkgs as balance ,nvl(gb_edo.nbr_pkgs,0) - nvl(gb_edo.release_nbr_pkgs,0) - nvl(gb_edo.trans_dn_nbr_pkgs,0) - nvl(gb_edo.dn_nbr_pkgs,0) as bal2,");
				sb.append(
						"gb_edo.dn_nbr_pkgs ,gb_edo.nom_wt ,gb_edo.NOM_VOL ,gb_edo.acct_nbr , gb_edo.EDO_ASN_NBR , bl_cntr_details.CNTR_NBR,gb_edo.aa_nm,nvl(gb_edo.release_nbr_pkgs,0) as releaseOty,");
				sb.append(
						"dn_details.billable_ton,gb_edo.CRG_STATUS as  TRANS_TYPE,gb_edo.bl_nbr,gb_edo.var_nbr,f.NBR_PKGS as jpjpnpkg,f.DN_NBR_PKGS as jpjpdn_npkg,gb_edo.CUT_OFF_NBR_PKGS,manifest_details.NBR_PKGS_IN_PORT,manifest_details.NBR_PKGS as mftNbr,nvl(gb_edo.release_nbr_pkgs,0)-nvl(gb_edo.trans_dn_nbr_pkgs,0)-nvl(gb_edo.dn_nbr_pkgs,0) as trnsPkgs,gb_edo.mft_seq_nbr as mftsqnbr from gb_edo , dn_details,vessel_call,berthing ,bl_cntr_details ,manifest_details ,  mft_markings,tesn_jp_jp f ");
				sb.append(
						"where gb_edo.var_nbr =  vessel_call.vv_cd and vessel_call.vv_cd = berthing.vv_cd  and manifest_details.MFT_SEQ_NBR = mft_markings.MFT_SQ_NBR and SHIFT_IND = 1 and ");
				sb.append(
						"gb_edo.mft_seq_nbr = manifest_details.mft_seq_nbr AND dn_details.edo_asn_nbr = gb_edo.edo_asn_nbr and ");
				sb.append(
						"manifest_details.mft_seq_nbr = bl_cntr_details.mft_seq_nbr(+) and f.edo_asn_nbr = gb_edo.edo_asn_nbr and dn_details.dn_nbr =:edoNbr and f.esn_asn_nbr=:tesnNbr and gb_edo.edo_status='A'");
			} else if (chktesnJpPsa_nbr.equals("Y")) {
				sb.append(
						"select gb_edo.edo_asn_nbr,to_char(dn_details.TRANS_DTTM,'dd/mm/yyyy hh24:mi') as trans_dttm,vessel_call.vsl_nm , vessel_call.in_voy_nbr,to_char(berthing.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb,");
				sb.append(
						"to_char(berthing.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,manifest_details.CRG_DES,manifest_details.CNTR_SIZE , manifest_details.CNTR_TYPE ,");
				sb.append(
						"mft_markings.mft_markings,gb_edo.nbr_pkgs as edoNbr,dn_details.dn_nbr ,dn_details.NBR_PKGS,dn_details.DP_IC_NBR ,dn_details.DP_IC_TYPE ,dn_details.DP_NM,");
				sb.append("dn_details.CNTR_NBR,to_char(dn_details.STUFF_DTTM,'dd/mm/yyyy hh24:mi') as stuff_dt,"); // VANI
				sb.append(
						"gb_edo.trans_nbr_pkgs - gb_edo.release_nbr_pkgs - gb_edo.trans_dn_nbr_pkgs as balance ,nvl(gb_edo.nbr_pkgs,0) - nvl(gb_edo.release_nbr_pkgs,0) - nvl(gb_edo.trans_dn_nbr_pkgs,0)- nvl(gb_edo.dn_nbr_pkgs,0)  as bal2,");
				sb.append(
						"gb_edo.dn_nbr_pkgs ,gb_edo.nom_wt ,gb_edo.NOM_VOL ,gb_edo.acct_nbr , gb_edo.EDO_ASN_NBR , bl_cntr_details.CNTR_NBR,gb_edo.aa_nm,nvl(gb_edo.release_nbr_pkgs,0) as releaseOty,");
				sb.append(
						"dn_details.billable_ton,gb_edo.CRG_STATUS as  TRANS_TYPE,gb_edo.bl_nbr,gb_edo.var_nbr,f.NBR_PKGS as jppsanpkg,f.DN_NBR_PKGS as jppsadn_npkg,gb_edo.CUT_OFF_NBR_PKGS,manifest_details.NBR_PKGS_IN_PORT,manifest_details.NBR_PKGS as mftNbr,nvl(gb_edo.release_nbr_pkgs,0)-nvl(gb_edo.trans_dn_nbr_pkgs,0)- nvl(gb_edo.dn_nbr_pkgs,0) as trnsPkgs,gb_edo.mft_seq_nbr as mftsqnbr from gb_edo , dn_details,vessel_call,berthing ,bl_cntr_details ,manifest_details ,  mft_markings,tesn_jp_psa f ");
				sb.append(
						"where gb_edo.var_nbr =  vessel_call.vv_cd and vessel_call.vv_cd = berthing.vv_cd  and manifest_details.MFT_SEQ_NBR = mft_markings.MFT_SQ_NBR and SHIFT_IND = 1 and ");
				sb.append(
						"gb_edo.mft_seq_nbr = manifest_details.mft_seq_nbr AND dn_details.edo_asn_nbr = gb_edo.edo_asn_nbr and ");
				sb.append(
						"manifest_details.mft_seq_nbr = bl_cntr_details.mft_seq_nbr(+) and f.edo_asn_nbr = gb_edo.edo_asn_nbr and dn_details.dn_nbr = :edoNbr and f.esn_asn_nbr=:tesnNbr and gb_edo.edo_status='A'");
			}
		}
		try {
			log.info("START: fetchDNDetail edoNbr:" + edoNbr + "status:" + status + "searchcrg:" + searchcrg
					+ "tesn_nbr:" + tesn_nbr);
			paramMap.put("edoNbr", edoNbr);
			paramMap.put("tesnNbr", tesn_nbr);
			log.info("SQL" + sb.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			String cuurnetdate = "";
			String atb = "";
			String cod = "";
			String vName = "";
			String voyNbr = "";
			String cntrNo = "";
			String cntType = "";
			String cntrSize = "";
			String transType = "";
			String edoAsnNbr = "";
			String blNbr = "";
			String declaredWt = "";
			String declaredVol = "";
			String declaredQty = "";
			String transQty = "";
			String nricNo = "";
			String marking = "";
			String crgDesc = "";
			String accNo = "";
			String billableUnit = "";
			String balance = "";
			String icType = "";
			String name = "";

			String strCntNum = "";
			String strStuffDt = "";

			String SchemeName = "";
			String vvcd = "";
			String transNbrPkgs = "";
			String mftNbr = "";
			String sumCtDNnbr = "";
			String sumEdoCtDNnbr = "";
			String mftSqNbr = "";
			String aa_name = "";
			while (rs.next()) {
				cuurnetdate = rs.getString("trans_dttm");
				atb = CommonUtility.deNull(rs.getString("atb"));
				cod = rs.getString("cod");
				vName = rs.getString("vsl_nm");
				voyNbr = rs.getString("in_voy_nbr");
				cntrNo = CommonUtility.deNull(rs.getString("CNTR_NBR"));//
				cntType = CommonUtility.deNull(rs.getString("CNTR_TYPE"));//
				cntrSize = CommonUtility.deNull(rs.getString("CNTR_SIZE"));//
				transType = rs.getString("TRANS_TYPE");//
				edoAsnNbr = rs.getString("EDO_ASN_NBR");//
				blNbr = rs.getString("bl_nbr");
				declaredWt = rs.getString("nom_wt");
				declaredVol = rs.getString("NOM_VOL");
				declaredQty = rs.getString("edoNbr");
				transQty = rs.getString("NBR_PKGS");
				nricNo = rs.getString("DP_IC_NBR");
				icType = rs.getString("DP_IC_TYPE");
				name = rs.getString("DP_NM");
				strCntNum = rs.getString("CNTR_NBR");
				strStuffDt = rs.getString("stuff_dt");
				marking = rs.getString("mft_markings");
				crgDesc = rs.getString("CRG_DES");
				accNo = rs.getString("acct_nbr");
				aa_name = rs.getString("aa_nm");
				// billableUnit =rs.getString("billable_ton");
				balance = rs.getString("balance");
				vvcd = rs.getString("var_nbr");
				SchemeName = getSchemeName(accNo, vvcd);
				mftNbr = rs.getString("mftNbr");
				mftSqNbr = rs.getString("mftsqnbr");
				sumCtDNnbr = getCTDNnbr(mftSqNbr);
				sumEdoCtDNnbr = getEDOCTDNnbr(mftSqNbr);
				transNbrPkgs = rs.getString("trnsPkgs");

				double nom_wt = 0;
				nom_wt = Double.parseDouble(declaredWt);
				double nom_vol = 0;
				double Bill_ton = 0;
				nom_vol = Double.parseDouble(declaredVol);

				// if(ftrans.equals("True"))
				// {
				if ((nom_wt / 1000) > nom_vol) {
					Bill_ton = nom_wt / 1000;
				} else {
					Bill_ton = nom_vol;
				} // end if nomwt

				billableUnit = "" + Bill_ton;

				if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
					String bal_cutoff = rs.getString("CUT_OFF_NBR_PKGS");
					String bal_short_land = rs.getString("NBR_PKGS_IN_PORT");
					int con3 = 0;
					int con4 = 0;
					int con5 = 0;
					if (bal_cutoff != null && !bal_cutoff.equals(""))
						con3 = Integer.parseInt(bal_cutoff);
					if (balance != null && !balance.equals(""))
						con4 = Integer.parseInt(balance);
					if (bal_short_land != null && !bal_short_land.equals(""))
						con5 = Integer.parseInt(bal_short_land);
					int blPkgs = 0;
					blPkgs = Integer.parseInt(mftNbr) - (con5 + Integer.parseInt(sumCtDNnbr));
					String relPkgsNbr_str = rs.getString("bal2");
					int relPkgsNbr = Integer.parseInt(relPkgsNbr_str);
					String maxQty = rs.getString("releaseOty");
					int bal4 = con4 - con3;

					if (aa_name != null && !aa_name.equals("")) {
						bal4 = bal4 + Integer.parseInt(maxQty);
						if (relPkgsNbr <= bal4)
							bal4 = relPkgsNbr;
					}

					if (blPkgs < bal4)
						bal4 = blPkgs;
					// int bal4 = con4 - (con3+con5);
					balance = String.valueOf(bal4);
				} else {
					String bal_cutoff = rs.getString("CUT_OFF_NBR_PKGS");
					String bal_short_land = rs.getString("NBR_PKGS_IN_PORT");
					int con3 = 0;
					int con4 = 0;
					int con5 = 0;
					if (bal_cutoff != null && !bal_cutoff.equals(""))
						con3 = Integer.parseInt(bal_cutoff);
					// if(balance != null && !balance.equals(""))
					// con4 = Integer.parseInt(balance);
					if (bal_short_land != null && !bal_short_land.equals(""))
						con5 = Integer.parseInt(bal_short_land);

					String relPkgsNbr_str = rs.getString("bal2");
					int relPkgsNbr = Integer.parseInt(relPkgsNbr_str);
					int edoNbrPkgs = Integer.parseInt(declaredQty) - Integer.parseInt(transNbrPkgs) - con3;
					int blPkgs = 0;
					blPkgs = Integer.parseInt(mftNbr) - (con5 + Integer.parseInt(sumEdoCtDNnbr));

					if (chktesnJpJp_nbr.equals("Y")) {
						String bal1 = rs.getString("jpjpnpkg");
						String bal2 = rs.getString("jpjpdn_npkg");
						// transQty = bal2;
						declaredQty = bal1;
						int con1 = 0;
						int con2 = 0;
						if (bal1 != null && !bal1.equals(""))
							con1 = Integer.parseInt(bal1);
						if (bal2 != null && !bal2.equals(""))
							con2 = Integer.parseInt(bal2);
						int bal3 = con1 - con2;
						if (blPkgs >= bal3) {
							if (edoNbrPkgs >= bal3) {
								if (relPkgsNbr >= bal3)
									bal3 = relPkgsNbr - con3;
							} else
								bal3 = edoNbrPkgs;
						} else
							bal3 = blPkgs;
						balance = String.valueOf(bal3);
					} else if (chktesnJpPsa_nbr.equals("Y")) {
						String bal1 = rs.getString("jppsanpkg");
						String bal2 = rs.getString("jppsadn_npkg");
						// transQty = bal2;
						declaredQty = bal1;
						int con1 = 0;
						int con2 = 0;
						if (bal1 != null && !bal1.equals(""))
							con1 = Integer.parseInt(bal1);
						if (bal2 != null && !bal2.equals(""))
							con2 = Integer.parseInt(bal2);
						int bal3 = con1 - con2;
						if (blPkgs >= bal3) {
							if (edoNbrPkgs >= bal3) {
								if (relPkgsNbr < bal3)
									bal3 = relPkgsNbr - con3;
							} else
								bal3 = edoNbrPkgs;
						} else
							bal3 = blPkgs;

						balance = String.valueOf(bal3);
					}
				}
				EdoValueObjectContainerised edoVo = new EdoValueObjectContainerised();
				edoVo.setTransDate(cuurnetdate);
				edoVo.setATB(atb);
				edoVo.setCOD(cod);
				edoVo.setVslName(vName);
				edoVo.setInVoyNbr(voyNbr);
				edoVo.setBlNbr(blNbr);
				edoVo.setNomWt(declaredWt);
				edoVo.setNomVol(declaredVol);
				edoVo.setNoPkgs(declaredQty);
				edoVo.setDeliveredPkgs(transQty);
				edoVo.setAAIcNbr(nricNo);
				edoVo.setAAName(name);

				edoVo.setCntrNbr(strCntNum);
				edoVo.setStuffDate(strStuffDt);

				edoVo.setAACustCD(icType);
				edoVo.setMarkings(marking);
				edoVo.setCrgDes(crgDesc);
				edoVo.setAcctNo(accNo);
				edoVo.setBillTon(billableUnit);
				edoVo.setCntrNo(cntrNo);
				edoVo.setCntrSize(cntrSize);
				edoVo.setCntrType(cntType);
				edoVo.setEdoAsnNbr(edoAsnNbr);
				edoVo.setTransType(transType);
				int temp_balance = Integer.parseInt(balance);
				if (temp_balance < 0)
					balance = "0";
				edoVo.setBalance(balance);
				edoVo.setDnStatus(SchemeName);
				fetchDNDetail.add(edoVo);

			}
			log.info("END: *** fetchDNDetail Result *****" + fetchDNDetail.size());

		} catch (Exception e) {
			log.info("Exception fetchDNDetail : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO fetchDNDetail");
		}
		return fetchDNDetail;
	}

	public String getCntrNo(String dnNbr) throws BusinessException {
		String sql = "";
		String cntrNbr = "";
		sql = "select cntr_nbr from dn_details where dn_nbr =:dnNbr";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getCntrNo dnNbr:" + dnNbr);
			paramMap.put("dnNbr", dnNbr);
			log.info("SQL" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				cntrNbr = rs.getString(1);
			}
			log.info("END: *** getCntrNo Result *****" + CommonUtility.deNull(cntrNbr));

		} catch (NullPointerException ne) {
			log.info("Exception getCntrNo : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCntrNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCntrNo cntrNbr:" + cntrNbr);

		}
		return cntrNbr;
	}

	public List<EdoValueObjectOps> fetchDNDetail(String strEdoNo, String edoNbr, String status, String searchcrg,
			String tesn_nbr) throws BusinessException {
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		List<EdoValueObjectOps> BJDetailsVect = new ArrayList<EdoValueObjectOps>();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: fetchDNDetail  DAO  Start Obj strEdoNo:" + strEdoNo + "edoNbr:" + edoNbr + "status:"
					+ status + "searchcrg:" + searchcrg + "tesn_nbr:" + tesn_nbr);
			// edoNbr stands for dnnbr
			// LogManager.instance.logInfo("Writing from DnBeanBean.fetchDNDetail");
			/*
			 * if(status.equals("L")){ sql =
			 * "select gb_edo.edo_asn_nbr,to_char(dn_details.TRANS_DTTM,'dd/mm/yyyy hh24:mi') as trans_dttm,vessel_call.vsl_nm , vessel_call.in_voy_nbr,to_char(berthing.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb,"
			 * +
			 * "to_char(berthing.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,manifest_details.CRG_DES,manifest_details.CNTR_SIZE , manifest_details.CNTR_TYPE ,"
			 * +
			 * "mft_markings.mft_markings,gb_edo.nbr_pkgs as edoNbr,dn_details.dn_nbr ,dn_details.NBR_PKGS,dn_details.DP_IC_NBR ,dn_details.DP_IC_TYPE ,dn_details.DP_NM,"
			 * +
			 * "gb_edo.nbr_pkgs - gb_edo.trans_nbr_pkgs - gb_edo.release_nbr_pkgs - gb_edo.dn_nbr_pkgs as balance ,"
			 * +
			 * "gb_edo.dn_nbr_pkgs ,gb_edo.nom_wt ,gb_edo.NOM_VOL ,gb_edo.acct_nbr , gb_edo.EDO_ASN_NBR , bl_cntr_details.CNTR_NBR,"
			 * +
			 * "dn_details.billable_ton,gb_edo.CRG_STATUS as TRANS_TYPE,gb_edo.bl_nbr,gb_edo.var_nbr from gb_edo , dn_details,vessel_call,berthing ,bl_cntr_details ,manifest_details ,  mft_markings "
			 * +
			 * "where gb_edo.var_nbr =  vessel_call.vv_cd and vessel_call.vv_cd = berthing.vv_cd  and manifest_details.MFT_SEQ_NBR = mft_markings.MFT_SQ_NBR and "
			 * +
			 * "gb_edo.mft_seq_nbr = manifest_details.mft_seq_nbr AND dn_details.edo_asn_nbr = gb_edo.edo_asn_nbr and SHIFT_IND = 1 and "
			 * +
			 * "manifest_details.mft_seq_nbr = bl_cntr_details.mft_seq_nbr(+) and dn_details.dn_nbr = '"
			 * +edoNbr+"'"; }else{ sql =
			 * "select gb_edo.edo_asn_nbr,to_char(dn_details.TRANS_DTTM,'dd/mm/yyyy hh24:mi') as trans_dttm,vessel_call.vsl_nm , vessel_call.in_voy_nbr,to_char(berthing.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb,"
			 * +
			 * "to_char(berthing.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,manifest_details.CRG_DES,manifest_details.CNTR_SIZE , manifest_details.CNTR_TYPE ,"
			 * +
			 * "mft_markings.mft_markings,gb_edo.nbr_pkgs as edoNbr,dn_details.dn_nbr ,dn_details.NBR_PKGS,dn_details.DP_IC_NBR ,dn_details.DP_IC_TYPE ,dn_details.DP_NM,"
			 * +
			 * "gb_edo.trans_nbr_pkgs - gb_edo.release_nbr_pkgs - gb_edo.trans_dn_nbr_pkgs as balance ,nvl(gb_edo.nbr_pkgs,0) - nvl(gb_edo.release_nbr_pkgs,0) - nvl(gb_edo.trans_dn_nbr_pkgs,0) - nvl(gb_edo.dn_nbr_pkgs,0) as bal2,"
			 * +
			 * "gb_edo.dn_nbr_pkgs ,gb_edo.nom_wt ,gb_edo.NOM_VOL ,gb_edo.acct_nbr , gb_edo.EDO_ASN_NBR , bl_cntr_details.CNTR_NBR,"
			 * +
			 * "dn_details.billable_ton,gb_edo.CRG_STATUS as  TRANS_TYPE,gb_edo.bl_nbr,gb_edo.var_nbr from gb_edo , dn_details,vessel_call,berthing ,bl_cntr_details ,manifest_details ,  mft_markings "
			 * +
			 * "where gb_edo.var_nbr =  vessel_call.vv_cd and vessel_call.vv_cd = berthing.vv_cd  and manifest_details.MFT_SEQ_NBR = mft_markings.MFT_SQ_NBR and SHIFT_IND = 1 and "
			 * +
			 * "gb_edo.mft_seq_nbr = manifest_details.mft_seq_nbr AND dn_details.edo_asn_nbr = gb_edo.edo_asn_nbr and "
			 * +
			 * "manifest_details.mft_seq_nbr = bl_cntr_details.mft_seq_nbr(+) and dn_details.dn_nbr = '"
			 * +edoNbr+"'"; }
			 */
			// boolean checkjpjp = chktesnJpJp(edoNbr);
			// boolean checkjppsa = chktesnJpPsa(edoNbr);
			String chktesnJpJp_nbr = chktesnJpJp_nbr(tesn_nbr);
			String chktesnJpPsa_nbr = chktesnJpPsa_nbr(tesn_nbr);
			boolean checkEdoStuff = chkEDOStuffing(strEdoNo); // add vinayak added on 8 jan 2004
			// LogManager.instance.logInfo("checkEdoStuff fetchDNDetail() :"+checkEdoStuff+"
			// //strEdoNo :"+strEdoNo);
			if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
				/*
				 * 08/12/2003 sql =
				 * "select gb_edo.edo_asn_nbr,to_char(dn_details.TRANS_DTTM,'dd/mm/yyyy hh24:mi') as trans_dttm,vessel_call.vsl_nm , vessel_call.in_voy_nbr,to_char(berthing.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb,"
				 * +
				 * "to_char(berthing.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,manifest_details.CRG_DES,manifest_details.CNTR_SIZE , manifest_details.CNTR_TYPE ,"
				 * +
				 * "mft_markings.mft_markings,gb_edo.nbr_pkgs as edoNbr,dn_details.dn_nbr ,dn_details.NBR_PKGS,dn_details.DP_IC_NBR ,dn_details.DP_IC_TYPE ,dn_details.DP_NM,"
				 * +
				 * "gb_edo.nbr_pkgs - gb_edo.trans_nbr_pkgs - gb_edo.release_nbr_pkgs - gb_edo.dn_nbr_pkgs as balance ,nvl(gb_edo.nbr_pkgs,0) - nvl(gb_edo.release_nbr_pkgs,0) - nvl(gb_edo.trans_dn_nbr_pkgs,0)- nvl(gb_edo.dn_nbr_pkgs,0)  as bal2,"
				 * +
				 * "gb_edo.dn_nbr_pkgs ,gb_edo.nom_wt ,gb_edo.NOM_VOL ,gb_edo.acct_nbr , gb_edo.EDO_ASN_NBR , bl_cntr_details.CNTR_NBR,gb_edo.aa_nm,nvl(gb_edo.release_nbr_pkgs,0) as releaseOty,"
				 * +
				 * "dn_details.billable_ton,gb_edo.CRG_STATUS as TRANS_TYPE,gb_edo.bl_nbr,gb_edo.var_nbr,gb_edo.CUT_OFF_NBR_PKGS,manifest_details.NBR_PKGS_IN_PORT,manifest_details.NBR_PKGS as mftNbr,nvl(gb_edo.release_nbr_pkgs,0)-nvl(gb_edo.trans_dn_nbr_pkgs,0) as trnsPkgs,gb_edo.mft_seq_nbr as mftsqnbr from gb_edo , dn_details,vessel_call,berthing ,bl_cntr_details ,manifest_details ,  mft_markings "
				 * +
				 * "where gb_edo.var_nbr =  vessel_call.vv_cd and vessel_call.vv_cd = berthing.vv_cd  and manifest_details.MFT_SEQ_NBR = mft_markings.MFT_SQ_NBR and "
				 * +
				 * "gb_edo.mft_seq_nbr = manifest_details.mft_seq_nbr AND dn_details.edo_asn_nbr = gb_edo.edo_asn_nbr and SHIFT_IND = 1 and gb_edo.edo_status='A' and "
				 * +
				 * "manifest_details.mft_seq_nbr = bl_cntr_details.mft_seq_nbr(+) and dn_details.dn_nbr = '"
				 * + edoNbr + "'";
				 */
				sb.append(
						"SELECT GB_EDO.EDO_ASN_NBR,TO_CHAR(DN_DETAILS.TRANS_DTTM,'DD/MM/YYYY HH24:MI') AS TRANS_DTTM,VESSEL_CALL.VSL_NM , VESSEL_CALL.IN_VOY_NBR,TO_CHAR(BERTHING.ATB_DTTM,'DD/MM/YYYY HH24:MI') AS ATB, ");
				sb.append(
						"TO_CHAR(DECODE(MANIFEST_DETAILS.UNSTUFF_SEQ_NBR,0,BERTHING.GB_COD_DTTM,CC_UNSTUFF_MANIFEST.DTTM_UNSTUFF),'DD/MM/YYYY HH24:MI') AS COD,MANIFEST_DETAILS.CRG_DES,MANIFEST_DETAILS.CNTR_SIZE , MANIFEST_DETAILS.CNTR_TYPE , ");
				sb.append(
						"MFT_MARKINGS.MFT_MARKINGS,GB_EDO.NBR_PKGS AS EDONBR,DN_DETAILS.DN_NBR ,DN_DETAILS.NBR_PKGS,DN_DETAILS.DP_IC_NBR ,DN_DETAILS.DP_IC_TYPE ,DN_DETAILS.DP_NM,");
				sb.append(
						"GB_EDO.NBR_PKGS - GB_EDO.TRANS_NBR_PKGS - GB_EDO.RELEASE_NBR_PKGS - GB_EDO.DN_NBR_PKGS AS BALANCE ,NVL(GB_EDO.NBR_PKGS,0) - NVL(GB_EDO.RELEASE_NBR_PKGS,0) - NVL(GB_EDO.TRANS_DN_NBR_PKGS,0)- NVL(GB_EDO.DN_NBR_PKGS,0)  AS BAL2,");
				sb.append(
						"GB_EDO.DN_NBR_PKGS ,GB_EDO.NOM_WT ,GB_EDO.NOM_VOL ,GB_EDO.ACCT_NBR , GB_EDO.EDO_ASN_NBR , BL_CNTR_DETAILS.CNTR_NBR,GB_EDO.AA_NM,NVL(GB_EDO.RELEASE_NBR_PKGS,0) AS RELEASEOTY, ");
				sb.append(
						"DN_DETAILS.BILLABLE_TON,GB_EDO.CRG_STATUS AS TRANS_TYPE,GB_EDO.BL_NBR,GB_EDO.VAR_NBR,GB_EDO.CUT_OFF_NBR_PKGS,MANIFEST_DETAILS.NBR_PKGS_IN_PORT,MANIFEST_DETAILS.NBR_PKGS AS MFTNBR,NVL(GB_EDO.RELEASE_NBR_PKGS,0)-NVL(GB_EDO.TRANS_DN_NBR_PKGS,0) AS TRNSPKGS,GB_EDO.MFT_SEQ_NBR AS MFTSQNBR, ");
				sb.append("DN_DETAILS.CRG_DEST, ");
				sb.append(
						" VESSEL_CALL.TERMINAL, VESSEL_CALL.SCHEME, VESSEL_CALL.COMBI_GC_SCHEME,  VESSEL_CALL.COMBI_GC_OPS_IND ");
				sb.append(
						"FROM GB_EDO , DN_DETAILS,VESSEL_CALL,BERTHING ,BL_CNTR_DETAILS ,MANIFEST_DETAILS ,  MFT_MARKINGS,CC_UNSTUFF_MANIFEST ");
				sb.append(
						"WHERE GB_EDO.VAR_NBR =  VESSEL_CALL.VV_CD AND VESSEL_CALL.VV_CD = BERTHING.VV_CD  AND MANIFEST_DETAILS.MFT_SEQ_NBR = MFT_MARKINGS.MFT_SQ_NBR AND  ");
				sb.append(
						"GB_EDO.MFT_SEQ_NBR = MANIFEST_DETAILS.MFT_SEQ_NBR AND MANIFEST_DETAILS.UNSTUFF_SEQ_NBR=CC_UNSTUFF_MANIFEST.UNSTUFF_SEQ_NBR(+) AND DN_DETAILS.EDO_ASN_NBR = GB_EDO.EDO_ASN_NBR AND SHIFT_IND = 1 AND GB_EDO.EDO_STATUS='A' AND  ");
				sb.append(
						"MANIFEST_DETAILS.MFT_SEQ_NBR = BL_CNTR_DETAILS.MFT_SEQ_NBR(+) AND DN_DETAILS.DN_NBR =:edoNbr  ");

				// LogManager.instance.logInfo("881 --if (searchcrg != null &&
				// !searchcrg.equals() && searchcrg.equals(LT)) :"+sql);

			} else {
				if (chktesnJpJp_nbr.equals("Y")) {
					/*
					 * sql =
					 * "select gb_edo.edo_asn_nbr,to_char(dn_details.TRANS_DTTM,'dd/mm/yyyy hh24:mi') as trans_dttm,vessel_call.vsl_nm , vessel_call.in_voy_nbr,to_char(berthing.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb,"
					 * +
					 * "to_char(berthing.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,manifest_details.CRG_DES,manifest_details.CNTR_SIZE , manifest_details.CNTR_TYPE ,"
					 * +
					 * "mft_markings.mft_markings,gb_edo.nbr_pkgs as edoNbr,dn_details.dn_nbr ,dn_details.NBR_PKGS,dn_details.DP_IC_NBR ,dn_details.DP_IC_TYPE ,dn_details.DP_NM,"
					 * +
					 * "gb_edo.trans_nbr_pkgs - gb_edo.release_nbr_pkgs - gb_edo.trans_dn_nbr_pkgs as balance ,nvl(gb_edo.nbr_pkgs,0) - nvl(gb_edo.release_nbr_pkgs,0) - nvl(gb_edo.trans_dn_nbr_pkgs,0) - nvl(gb_edo.dn_nbr_pkgs,0) as bal2,"
					 * +
					 * "gb_edo.dn_nbr_pkgs ,gb_edo.nom_wt ,gb_edo.NOM_VOL ,gb_edo.acct_nbr , gb_edo.EDO_ASN_NBR , bl_cntr_details.CNTR_NBR,gb_edo.aa_nm,nvl(gb_edo.release_nbr_pkgs,0) as releaseOty,"
					 * +
					 * "dn_details.billable_ton,gb_edo.CRG_STATUS as  TRANS_TYPE,gb_edo.bl_nbr,gb_edo.var_nbr,f.NBR_PKGS as jpjpnpkg,f.DN_NBR_PKGS as jpjpdn_npkg,gb_edo.CUT_OFF_NBR_PKGS,manifest_details.NBR_PKGS_IN_PORT,manifest_details.NBR_PKGS as mftNbr,nvl(gb_edo.release_nbr_pkgs,0)-nvl(gb_edo.trans_dn_nbr_pkgs,0)-nvl(gb_edo.dn_nbr_pkgs,0) as trnsPkgs,gb_edo.mft_seq_nbr as mftsqnbr from gb_edo , dn_details,vessel_call,berthing ,bl_cntr_details ,manifest_details ,  mft_markings,tesn_jp_jp f "
					 * +
					 * "where gb_edo.var_nbr =  vessel_call.vv_cd and vessel_call.vv_cd = berthing.vv_cd  and manifest_details.MFT_SEQ_NBR = mft_markings.MFT_SQ_NBR and SHIFT_IND = 1 and "
					 * +
					 * "gb_edo.mft_seq_nbr = manifest_details.mft_seq_nbr AND dn_details.edo_asn_nbr = gb_edo.edo_asn_nbr and "
					 * +
					 * "manifest_details.mft_seq_nbr = bl_cntr_details.mft_seq_nbr(+) and f.edo_asn_nbr = gb_edo.edo_asn_nbr and dn_details.dn_nbr = '"
					 * + edoNbr + "' and f.esn_asn_nbr='" + tesn_nbr +
					 * "' and gb_edo.edo_status='A'";
					 */

					sb.append(
							"SELECT GB_EDO.EDO_ASN_NBR,TO_CHAR(DN_DETAILS.TRANS_DTTM,'DD/MM/YYYY HH24:MI') AS TRANS_DTTM,VESSEL_CALL.VSL_NM , VESSEL_CALL.IN_VOY_NBR,TO_CHAR(BERTHING.ATB_DTTM,'DD/MM/YYYY HH24:MI') AS ATB, ");
					sb.append(
							"TO_CHAR(DECODE(MANIFEST_DETAILS.UNSTUFF_SEQ_NBR,0,BERTHING.GB_COD_DTTM,CC_UNSTUFF_MANIFEST.DTTM_UNSTUFF),'DD/MM/YYYY HH24:MI') AS COD,MANIFEST_DETAILS.CRG_DES,MANIFEST_DETAILS.CNTR_SIZE , MANIFEST_DETAILS.CNTR_TYPE , ");
					sb.append(
							"MFT_MARKINGS.MFT_MARKINGS,GB_EDO.NBR_PKGS AS EDONBR,DN_DETAILS.DN_NBR ,DN_DETAILS.NBR_PKGS,DN_DETAILS.DP_IC_NBR ,DN_DETAILS.DP_IC_TYPE ,DN_DETAILS.DP_NM, ");
					sb.append(
							"GB_EDO.TRANS_NBR_PKGS - GB_EDO.RELEASE_NBR_PKGS - GB_EDO.TRANS_DN_NBR_PKGS AS BALANCE ,NVL(GB_EDO.NBR_PKGS,0) - NVL(GB_EDO.RELEASE_NBR_PKGS,0) - NVL(GB_EDO.TRANS_DN_NBR_PKGS,0) - NVL(GB_EDO.DN_NBR_PKGS,0) AS BAL2, ");
					sb.append(
							"GB_EDO.DN_NBR_PKGS ,GB_EDO.NOM_WT ,GB_EDO.NOM_VOL ,GB_EDO.ACCT_NBR , GB_EDO.EDO_ASN_NBR , BL_CNTR_DETAILS.CNTR_NBR,GB_EDO.AA_NM,NVL(GB_EDO.RELEASE_NBR_PKGS,0) AS RELEASEOTY, ");
					sb.append(
							"DN_DETAILS.BILLABLE_TON,GB_EDO.CRG_STATUS AS  TRANS_TYPE,GB_EDO.BL_NBR,GB_EDO.VAR_NBR,F.NBR_PKGS AS JPJPNPKG,F.DN_NBR_PKGS AS JPJPDN_NPKG,GB_EDO.CUT_OFF_NBR_PKGS,MANIFEST_DETAILS.NBR_PKGS_IN_PORT,MANIFEST_DETAILS.NBR_PKGS AS MFTNBR,NVL(GB_EDO.RELEASE_NBR_PKGS,0)-NVL(GB_EDO.TRANS_DN_NBR_PKGS,0)-NVL(GB_EDO.DN_NBR_PKGS,0) AS TRNSPKGS,GB_EDO.MFT_SEQ_NBR AS MFTSQNBR, ");
					sb.append("DN_DETAILS.CRG_DEST, ");
					sb.append(
							" VESSEL_CALL.TERMINAL, VESSEL_CALL.SCHEME, VESSEL_CALL.COMBI_GC_SCHEME,  VESSEL_CALL.COMBI_GC_OPS_IND ");
					sb.append(
							" FROM GB_EDO , DN_DETAILS,VESSEL_CALL,BERTHING ,BL_CNTR_DETAILS ,MANIFEST_DETAILS ,  MFT_MARKINGS,TESN_JP_JP F,CC_UNSTUFF_MANIFEST   ");
					sb.append(
							"WHERE GB_EDO.VAR_NBR =  VESSEL_CALL.VV_CD AND VESSEL_CALL.VV_CD = BERTHING.VV_CD  AND MANIFEST_DETAILS.MFT_SEQ_NBR = MFT_MARKINGS.MFT_SQ_NBR AND SHIFT_IND = 1 AND  ");
					sb.append(
							"GB_EDO.MFT_SEQ_NBR = MANIFEST_DETAILS.MFT_SEQ_NBR AND  MANIFEST_DETAILS.UNSTUFF_SEQ_NBR=CC_UNSTUFF_MANIFEST.UNSTUFF_SEQ_NBR(+) AND DN_DETAILS.EDO_ASN_NBR = GB_EDO.EDO_ASN_NBR AND ");
					sb.append(
							"MANIFEST_DETAILS.MFT_SEQ_NBR = BL_CNTR_DETAILS.MFT_SEQ_NBR(+) AND F.EDO_ASN_NBR = GB_EDO.EDO_ASN_NBR AND DN_DETAILS.DN_NBR = :edoNbr ");
					sb.append(" AND F.ESN_ASN_NBR=:tesn_nbr ");
					sb.append("' AND GB_EDO.EDO_STATUS='A'");
					// LogManager.instance.logInfo("907 --if (chktesnJpJp_nbr.equals(Y)) in
					// fetchDNDEtail():"+sql);

				} else if (chktesnJpPsa_nbr.equals("Y")) {
					/*
					 * sql =
					 * "select gb_edo.edo_asn_nbr,to_char(dn_details.TRANS_DTTM,'dd/mm/yyyy hh24:mi') as trans_dttm,vessel_call.vsl_nm , vessel_call.in_voy_nbr,to_char(berthing.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb,"
					 * +
					 * "to_char(berthing.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,manifest_details.CRG_DES,manifest_details.CNTR_SIZE , manifest_details.CNTR_TYPE ,"
					 * +
					 * "mft_markings.mft_markings,gb_edo.nbr_pkgs as edoNbr,dn_details.dn_nbr ,dn_details.NBR_PKGS,dn_details.DP_IC_NBR ,dn_details.DP_IC_TYPE ,dn_details.DP_NM,"
					 * +
					 * "gb_edo.trans_nbr_pkgs - gb_edo.release_nbr_pkgs - gb_edo.trans_dn_nbr_pkgs as balance ,nvl(gb_edo.nbr_pkgs,0) - nvl(gb_edo.release_nbr_pkgs,0) - nvl(gb_edo.trans_dn_nbr_pkgs,0)- nvl(gb_edo.dn_nbr_pkgs,0)  as bal2,"
					 * +
					 * "gb_edo.dn_nbr_pkgs ,gb_edo.nom_wt ,gb_edo.NOM_VOL ,gb_edo.acct_nbr , gb_edo.EDO_ASN_NBR , bl_cntr_details.CNTR_NBR,gb_edo.aa_nm,nvl(gb_edo.release_nbr_pkgs,0) as releaseOty,"
					 * +
					 * "dn_details.billable_ton,gb_edo.CRG_STATUS as  TRANS_TYPE,gb_edo.bl_nbr,gb_edo.var_nbr,f.NBR_PKGS as jppsanpkg,f.DN_NBR_PKGS as jppsadn_npkg,gb_edo.CUT_OFF_NBR_PKGS,manifest_details.NBR_PKGS_IN_PORT,manifest_details.NBR_PKGS as mftNbr,nvl(gb_edo.release_nbr_pkgs,0)-nvl(gb_edo.trans_dn_nbr_pkgs,0)- nvl(gb_edo.dn_nbr_pkgs,0) as trnsPkgs,gb_edo.mft_seq_nbr as mftsqnbr from gb_edo , dn_details,vessel_call,berthing ,bl_cntr_details ,manifest_details ,  mft_markings,tesn_jp_psa f "
					 * +
					 * "where gb_edo.var_nbr =  vessel_call.vv_cd and vessel_call.vv_cd = berthing.vv_cd  and manifest_details.MFT_SEQ_NBR = mft_markings.MFT_SQ_NBR and SHIFT_IND = 1 and "
					 * +
					 * "gb_edo.mft_seq_nbr = manifest_details.mft_seq_nbr AND dn_details.edo_asn_nbr = gb_edo.edo_asn_nbr and "
					 * +
					 * "manifest_details.mft_seq_nbr = bl_cntr_details.mft_seq_nbr(+) and f.edo_asn_nbr = gb_edo.edo_asn_nbr and dn_details.dn_nbr = '"
					 * + edoNbr + "' and f.esn_asn_nbr='" + tesn_nbr +
					 * "' and gb_edo.edo_status='A'";
					 */

					sb.append(
							"SELECT GB_EDO.EDO_ASN_NBR,TO_CHAR(DN_DETAILS.TRANS_DTTM,'DD/MM/YYYY HH24:MI') AS TRANS_DTTM,VESSEL_CALL.VSL_NM , VESSEL_CALL.IN_VOY_NBR,TO_CHAR(BERTHING.ATB_DTTM,'DD/MM/YYYY HH24:MI') AS ATB,");
					sb.append(
							"TO_CHAR(DECODE(MANIFEST_DETAILS.UNSTUFF_SEQ_NBR,0,BERTHING.GB_COD_DTTM,CC_UNSTUFF_MANIFEST.DTTM_UNSTUFF),'DD/MM/YYYY HH24:MI') AS COD,MANIFEST_DETAILS.CRG_DES,MANIFEST_DETAILS.CNTR_SIZE , MANIFEST_DETAILS.CNTR_TYPE , ");
					sb.append(
							"MFT_MARKINGS.MFT_MARKINGS,GB_EDO.NBR_PKGS AS EDONBR,DN_DETAILS.DN_NBR ,DN_DETAILS.NBR_PKGS,DN_DETAILS.DP_IC_NBR ,DN_DETAILS.DP_IC_TYPE ,DN_DETAILS.DP_NM, ");
					sb.append(
							"GB_EDO.TRANS_NBR_PKGS - GB_EDO.RELEASE_NBR_PKGS - GB_EDO.TRANS_DN_NBR_PKGS AS BALANCE ,NVL(GB_EDO.NBR_PKGS,0) - NVL(GB_EDO.RELEASE_NBR_PKGS,0) - NVL(GB_EDO.TRANS_DN_NBR_PKGS,0)- NVL(GB_EDO.DN_NBR_PKGS,0)  AS BAL2, ");
					sb.append(
							"GB_EDO.DN_NBR_PKGS ,GB_EDO.NOM_WT ,GB_EDO.NOM_VOL ,GB_EDO.ACCT_NBR , GB_EDO.EDO_ASN_NBR , BL_CNTR_DETAILS.CNTR_NBR,GB_EDO.AA_NM,NVL(GB_EDO.RELEASE_NBR_PKGS,0) AS RELEASEOTY, ");
					sb.append(
							"DN_DETAILS.BILLABLE_TON,GB_EDO.CRG_STATUS AS  TRANS_TYPE,GB_EDO.BL_NBR,GB_EDO.VAR_NBR,F.NBR_PKGS AS JPPSANPKG,F.DN_NBR_PKGS AS JPPSADN_NPKG,GB_EDO.CUT_OFF_NBR_PKGS,MANIFEST_DETAILS.NBR_PKGS_IN_PORT,MANIFEST_DETAILS.NBR_PKGS AS MFTNBR,NVL(GB_EDO.RELEASE_NBR_PKGS,0)-NVL(GB_EDO.TRANS_DN_NBR_PKGS,0)- NVL(GB_EDO.DN_NBR_PKGS,0) AS TRNSPKGS,GB_EDO.MFT_SEQ_NBR AS MFTSQNBR, ");
					sb.append("DN_DETAILS.CRG_DEST, ");
					sb.append(
							" VESSEL_CALL.TERMINAL, VESSEL_CALL.SCHEME, VESSEL_CALL.COMBI_GC_SCHEME,  VESSEL_CALL.COMBI_GC_OPS_IND  ");
					sb.append(
							" FROM GB_EDO , DN_DETAILS,VESSEL_CALL,BERTHING ,BL_CNTR_DETAILS ,MANIFEST_DETAILS ,  MFT_MARKINGS,TESN_JP_PSA F,CC_UNSTUFF_MANIFEST  ");
					sb.append(
							"WHERE GB_EDO.VAR_NBR =  VESSEL_CALL.VV_CD AND VESSEL_CALL.VV_CD = BERTHING.VV_CD  AND MANIFEST_DETAILS.MFT_SEQ_NBR = MFT_MARKINGS.MFT_SQ_NBR AND SHIFT_IND = 1 AND  ");
					sb.append(
							"GB_EDO.MFT_SEQ_NBR = MANIFEST_DETAILS.MFT_SEQ_NBR AND DN_DETAILS.EDO_ASN_NBR = GB_EDO.EDO_ASN_NBR AND ");
					sb.append(
							"MANIFEST_DETAILS.MFT_SEQ_NBR = BL_CNTR_DETAILS.MFT_SEQ_NBR(+) AND MANIFEST_DETAILS.UNSTUFF_SEQ_NBR=CC_UNSTUFF_MANIFEST.UNSTUFF_SEQ_NBR(+) AND F.EDO_ASN_NBR = GB_EDO.EDO_ASN_NBR AND DN_DETAILS.DN_NBR =:edoNbr  ");
					sb.append("AND F.ESN_ASN_NBR=:tesn_nbr ");
					sb.append("' AND GB_EDO.EDO_STATUS='A'");

				} else if (checkEdoStuff) {

					sb.append(
							"SELECT GB_EDO.EDO_ASN_NBR,TO_CHAR(DN_DETAILS.TRANS_DTTM,'DD/MM/YYYY HH24:MI') AS TRANS_DTTM,VESSEL_CALL.VSL_NM ,VESSEL_CALL.IN_VOY_NBR,TO_CHAR(BERTHING.ATB_DTTM,'DD/MM/YYYY HH24:MI') AS ATB,TO_CHAR(DECODE(MANIFEST_DETAILS.UNSTUFF_SEQ_NBR,0,BERTHING.GB_COD_DTTM,CC_UNSTUFF_MANIFEST.DTTM_UNSTUFF),'DD/MM/YYYY HH24:MI') AS COD,MANIFEST_DETAILS.CRG_DES, ");
					sb.append(
							"MANIFEST_DETAILS.CNTR_SIZE , MANIFEST_DETAILS.CNTR_TYPE ,MFT_MARKINGS.MFT_MARKINGS,GB_EDO.NBR_PKGS AS EDONBR,DN_DETAILS.DN_NBR ,DN_DETAILS.NBR_PKGS,DN_DETAILS.DP_IC_NBR, DN_DETAILS.DP_IC_TYPE ,DN_DETAILS.DP_NM,GB_EDO.TRANS_NBR_PKGS - GB_EDO.RELEASE_NBR_PKGS - GB_EDO.TRANS_DN_NBR_PKGS AS BALANCE ,NVL(GB_EDO.NBR_PKGS,0) - NVL(GB_EDO.RELEASE_NBR_PKGS,0) - NVL(GB_EDO.TRANS_DN_NBR_PKGS,0)- NVL(GB_EDO.DN_NBR_PKGS,0) AS BAL2,GB_EDO.DN_NBR_PKGS , ");
					sb.append(
							"GB_EDO.NOM_WT ,GB_EDO.NOM_VOL ,GB_EDO.ACCT_NBR , BL_CNTR_DETAILS.CNTR_NBR,GB_EDO.AA_NM,NVL(GB_EDO.RELEASE_NBR_PKGS,0) AS RELEASEOTY,DN_DETAILS.BILLABLE_TON,GB_EDO.CRG_STATUS AS  TRANS_TYPE,GB_EDO.BL_NBR,GB_EDO.VAR_NBR,  ");
					sb.append(
							"CC_STUFFING_DETAILS.NBR_PKGS AS STUFFNPKG,CC_STUFFING_DETAILS.DN_NBR_PKGS AS STUFFDNNPKG,GB_EDO.CUT_OFF_NBR_PKGS,MANIFEST_DETAILS.NBR_PKGS_IN_PORT,MANIFEST_DETAILS.NBR_PKGS AS MFTNBR,NVL(GB_EDO.RELEASE_NBR_PKGS,0)-NVL(GB_EDO.TRANS_DN_NBR_PKGS,0)- NVL(GB_EDO.DN_NBR_PKGS,0) AS TRNSPKGS,GB_EDO.MFT_SEQ_NBR AS MFTSQNBR,  ");
					sb.append(
							" VESSEL_CALL.TERMINAL, VESSEL_CALL.SCHEME, VESSEL_CALL.COMBI_GC_SCHEME,  VESSEL_CALL.COMBI_GC_OPS_IND  ");
					sb.append(
							" FROM GB_EDO,DN_DETAILS,VESSEL_CALL,BERTHING ,BL_CNTR_DETAILS ,MANIFEST_DETAILS , MFT_MARKINGS,CC_STUFFING_DETAILS,CC_STUFFING,CC_UNSTUFF_MANIFEST");
					sb.append(
							" WHERE GB_EDO.VAR_NBR =  VESSEL_CALL.VV_CD AND VESSEL_CALL.VV_CD = BERTHING.VV_CD  AND MANIFEST_DETAILS.MFT_SEQ_NBR = MFT_MARKINGS.MFT_SQ_NBR AND BERTHING.SHIFT_IND = 1 AND GB_EDO.MFT_SEQ_NBR = MANIFEST_DETAILS.MFT_SEQ_NBR AND DN_DETAILS.EDO_ASN_NBR = GB_EDO.EDO_ASN_NBR AND MANIFEST_DETAILS.MFT_SEQ_NBR = BL_CNTR_DETAILS.MFT_SEQ_NBR(+) ");
					sb.append(
							"AND MANIFEST_DETAILS.UNSTUFF_SEQ_NBR=CC_UNSTUFF_MANIFEST.UNSTUFF_SEQ_NBR(+) AND CC_STUFFING.STUFF_CLOSED='Y' AND CC_STUFFING_DETAILS.REC_STATUS='A' AND CC_STUFFING.ACTIVE_STATUS='A' AND GB_EDO.EDO_ASN_NBR=DN_DETAILS.EDO_ASN_NBR AND DN_DETAILS.EDO_ASN_NBR=CC_STUFFING_DETAILS.EDO_ESN_NBR AND CC_STUFFING.STUFF_SEQ_NBR=CC_STUFFING_DETAILS.STUFF_SEQ_NBR AND DN_DETAILS.DN_NBR = :edoNbr AND CC_STUFFING_DETAILS.STUFF_SEQ_NBR=:tesn_nbr AND GB_EDO.EDO_STATUS='A'  ");
				}
			}

			paramMap.put("edoNbr", edoNbr);
			paramMap.put("tesn_nbr", tesn_nbr);
			
			log.info(" *** fetchDNDetail SQL *****" + sb.toString());
			log.info(" *** fetchDNDetail params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			String cuurnetdate = "";
			String atb = "";
			String cod = "";
			String vName = "";
			String voyNbr = "";
			String cntrNo = "";
			String cntType = "";
			String cntrSize = "";
			String transType = "";
			String edoAsnNbr = "";
			String blNbr = "";
			String declaredWt = "";
			String declaredVol = "";
			String declaredQty = "";
			String transQty = "";
			String nricNo = "";
			String marking = "";
			String crgDesc = "";
			String accNo = "";
			String billableUnit = "";
			String balance = "";
			String icType = "";
			String name = "";
			String SchemeName = "";
			String vvcd = "";
			String transNbrPkgs = "";
			String mftNbr = "";
			String sumCtDNnbr = "";
			String sumEdoCtDNnbr = "";
			String mftSqNbr = "";
			String aa_name = "";
			// ++ vietnd02
			String crgDes = "";
			// --
			while (rs.next()) {
				cuurnetdate = rs.getString("trans_dttm");
				atb = CommonUtility.deNull(rs.getString("atb"));
				cod = rs.getString("cod");
				vName = rs.getString("vsl_nm");
				voyNbr = rs.getString("in_voy_nbr");
				cntrNo = CommonUtility.deNull(rs.getString("CNTR_NBR"));
				cntType = CommonUtility.deNull(rs.getString("CNTR_TYPE"));
				cntrSize = CommonUtility.deNull(rs.getString("CNTR_SIZE"));
				transType = rs.getString("TRANS_TYPE");
				edoAsnNbr = rs.getString("EDO_ASN_NBR");
				blNbr = rs.getString("bl_nbr");
				declaredWt = rs.getString("nom_wt");
				declaredVol = rs.getString("NOM_VOL");
				declaredQty = rs.getString("edoNbr");
				transQty = rs.getString("NBR_PKGS");
				nricNo = rs.getString("DP_IC_NBR");
				icType = rs.getString("DP_IC_TYPE");
				name = rs.getString("DP_NM");
				marking = rs.getString("mft_markings");
				crgDesc = rs.getString("CRG_DES");
				accNo = rs.getString("acct_nbr");
				aa_name = rs.getString("aa_nm");
				// billableUnit =rs.getString("billable_ton");
				balance = rs.getString("balance");
				vvcd = rs.getString("var_nbr");
				SchemeName = getSchemeName(accNo, vvcd);
				mftNbr = rs.getString("mftNbr");
				mftSqNbr = rs.getString("mftsqnbr");
				sumCtDNnbr = getCTDNnbr(mftSqNbr);
				sumEdoCtDNnbr = getEDOCTDNnbr(mftSqNbr);
				transNbrPkgs = rs.getString("trnsPkgs");

				crgDes = rs.getString("CRG_DEST");
				if ("O".equalsIgnoreCase(crgDes))
					crgDes = "Out Of JP";
				else if ("V".equalsIgnoreCase(crgDes))
					crgDes = "Vessel";
				else if ("L".equalsIgnoreCase(crgDes))
					crgDes = "Leased Area";

				double nom_wt = 0;
				nom_wt = Double.parseDouble(declaredWt);
				double nom_vol = 0;
				double Bill_ton = 0;
				nom_vol = Double.parseDouble(declaredVol);

				// if(ftrans.equals("True"))
				// {
				if ((nom_wt / 1000) > nom_vol) {
					Bill_ton = nom_wt / 1000;
				} else {
					Bill_ton = nom_vol;
				} // end if nomwt

				billableUnit = "" + Bill_ton;

				if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
					String bal_cutoff = rs.getString("CUT_OFF_NBR_PKGS");
					String bal_short_land = rs.getString("NBR_PKGS_IN_PORT");
					int con3 = 0;
					int con4 = 0;
					int con5 = 0;
					if (bal_cutoff != null && !bal_cutoff.equals(""))
						con3 = Integer.parseInt(bal_cutoff);
					if (balance != null && !balance.equals(""))
						con4 = Integer.parseInt(balance);
					if (bal_short_land != null && !bal_short_land.equals(""))
						con5 = Integer.parseInt(bal_short_land);
					int blPkgs = 0;
					blPkgs = Integer.parseInt(mftNbr) - (con5 + Integer.parseInt(sumCtDNnbr));
					String relPkgsNbr_str = rs.getString("bal2");
					int relPkgsNbr = Integer.parseInt(relPkgsNbr_str);
					String maxQty = rs.getString("releaseOty");
					int bal4 = con4 - con3;

					if (aa_name != null && !aa_name.equals("")) {
						bal4 = bal4 + Integer.parseInt(maxQty);
						if (relPkgsNbr <= bal4)
							bal4 = relPkgsNbr;
					}

					if (blPkgs < bal4)
						bal4 = blPkgs;
					// int bal4 = con4 - (con3+con5);
					balance = String.valueOf(bal4);
				} else {
					String bal_cutoff = rs.getString("CUT_OFF_NBR_PKGS");
					String bal_short_land = rs.getString("NBR_PKGS_IN_PORT");
					int con3 = 0;
					int con5 = 0;
					if (bal_cutoff != null && !bal_cutoff.equals(""))
						con3 = Integer.parseInt(bal_cutoff);
					// if(balance != null && !balance.equals(""))
					// con4 = Integer.parseInt(balance);
					if (bal_short_land != null && !bal_short_land.equals(""))
						con5 = Integer.parseInt(bal_short_land);

					String relPkgsNbr_str = rs.getString("bal2");
					int relPkgsNbr = Integer.parseInt(relPkgsNbr_str);
					int edoNbrPkgs = Integer.parseInt(declaredQty) - Integer.parseInt(transNbrPkgs) - con3;
					int blPkgs = 0;
					blPkgs = Integer.parseInt(mftNbr) - (con5 + Integer.parseInt(sumEdoCtDNnbr));

					if (chktesnJpJp_nbr.equals("Y")) {
						String bal1 = rs.getString("jpjpnpkg");
						String bal2 = rs.getString("jpjpdn_npkg");
						// transQty = bal2;
						declaredQty = bal1;
						int con1 = 0;
						int con2 = 0;
						if (bal1 != null && !bal1.equals(""))
							con1 = Integer.parseInt(bal1);
						if (bal2 != null && !bal2.equals(""))
							con2 = Integer.parseInt(bal2);
						int bal3 = con1 - con2;
						if (blPkgs >= bal3) {
							if (edoNbrPkgs >= bal3) {
								if (relPkgsNbr < bal3)
									bal3 = relPkgsNbr - con3;
							} else
								bal3 = edoNbrPkgs;
						} else
							bal3 = blPkgs;
						balance = String.valueOf(bal3);
					} else if (chktesnJpPsa_nbr.equals("Y")) {
						String bal1 = rs.getString("jppsanpkg");
						String bal2 = rs.getString("jppsadn_npkg");
						// transQty = bal2;
						declaredQty = bal1;
						int con1 = 0;
						int con2 = 0;
						if (bal1 != null && !bal1.equals(""))
							con1 = Integer.parseInt(bal1);
						if (bal2 != null && !bal2.equals(""))
							con2 = Integer.parseInt(bal2);
						int bal3 = con1 - con2;
						if (blPkgs >= bal3) {
							if (edoNbrPkgs >= bal3) {
								if (relPkgsNbr < bal3)
									bal3 = relPkgsNbr - con3;
							} else
								bal3 = edoNbrPkgs;
						} else
							bal3 = blPkgs;

						balance = String.valueOf(bal3);
					} else if (checkEdoStuff) {
						String bal1 = rs.getString("stuffnpkg");
						String bal2 = rs.getString("stuffdnnpkg");
						// transQty = bal2;
						declaredQty = bal1; // declaredQty=gb_edo.nbr_pkgs
						int con1 = 0;
						int con2 = 0;
						if (bal1 != null && !bal1.equals(""))
							con1 = Integer.parseInt(bal1);
						if (bal2 != null && !bal2.equals(""))
							con2 = Integer.parseInt(bal2);
						int bal3 = con1 - con2;
						if (blPkgs >= bal3) // blPkgs=manifest_details.NBR_PKGS - (manifest_details.NBR_PKGS_IN_PORT +
											// (select
											// sum(nvl(DN_NBR_PKGS,0)+nvl(CUT_OFF_NBR_PKGS,0)+nvl(trans_dn_nbr_pkgs,0))
											// from gb_edo where MFT_SEQ_NBR =''))
						{
							if (edoNbrPkgs >= bal3) // edoNbrPkgs=gb_edo.nbr_pkgs - gb_edo.dn_nbr_pkgs -
													// gb_edo.CUT_OFF_NBR_PKGS
							{
								if (relPkgsNbr < bal3) 
									bal3 = relPkgsNbr - con3; // con3=gb_edo.CUT_OFF_NBR_PKGS
							} else
								bal3 = edoNbrPkgs;
						} else
							bal3 = blPkgs;

						balance = String.valueOf(bal3);
					}
				}
				EdoValueObjectOps edoVo = new EdoValueObjectOps();
				edoVo.setTransDate(cuurnetdate);
				edoVo.setATB(atb);
				edoVo.setCOD(cod);
				edoVo.setVslName(vName);
				edoVo.setInVoyNbr(voyNbr);
				edoVo.setBlNbr(blNbr);
				edoVo.setNomWt(declaredWt);
				edoVo.setNomVol(declaredVol);
				edoVo.setNoPkgs(declaredQty);
				edoVo.setDeliveredPkgs(transQty);
				edoVo.setAAIcNbr(nricNo);
				edoVo.setAAName(name);
				edoVo.setAACustCD(icType);
				edoVo.setMarkings(marking);
				edoVo.setCrgDes(crgDesc);
				edoVo.setAcctNo(accNo);
				edoVo.setBillTon(billableUnit);
				edoVo.setCntrNo(cntrNo);
				edoVo.setCntrSize(cntrSize);
				edoVo.setCntrType(cntType);
				edoVo.setEdoAsnNbr(edoAsnNbr);
				edoVo.setTransType(transType);
				// ++
				edoVo.setCrgDestination(crgDes);
				// --
				int temp_balance = Integer.parseInt(balance);
				if (temp_balance < 0)
					balance = "0";
				edoVo.setBalance(balance);
				edoVo.setDnStatus(SchemeName);
				edoVo.setVvCd(vvcd);
				edoVo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				edoVo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				edoVo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				edoVo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				BJDetailsVect.add(edoVo);

			}
			log.info("END: *** fetchDNDetail Result *****" + BJDetailsVect.size());
		} catch (NullPointerException e) {
			log.info("Exception fetchDNDetail : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception fetchDNDetail : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchDNDetail  DAO  END");
		}
		return BJDetailsVect;
	}

	public List<EdoValueObjectOps> fetchShutoutDNCreateDetail(String edoNbr, String transType, String searchcrg,
			String tesn_nbr) throws BusinessException {
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		List<EdoValueObjectOps> BJDetailsVect = new ArrayList<EdoValueObjectOps>();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: fetchShutoutDNCreateDetail  DAO  Start Obj "+" edoNbr:"+edoNbr+" transType:"+transType+" searchcrg:"+searchcrg+" tesn_nbr:"+tesn_nbr);

			boolean chkEdostatus = chkEdoStatus(edoNbr);
			if (!chkEdostatus) {
				log.info("Writing from DnBeanBean.fetchShutoutDNCreateDetail");
				log.info("EDO has been cancelled");
				throw new BusinessException("M80001");
			}

			String atbDate = getVesselATBDate(edoNbr);
			if (atbDate == null || atbDate.length() == 0) {
				log.info("Writing from DnBeanBean.fetchShutoutDNCreateDetail");
				log.info("Vessel has not Berthed yet.  DN cannot be printed");
				throw new BusinessException("M80003");
			}

			if (isEsn(null, edoNbr)) {
				sb.append(" SELECT ");
				sb.append(" TO_CHAR (SYSDATE, 'DD/MM/YYYY HH24:MI') AS TRANS_DTTM, ");
				sb.append(" VC.VSL_NM, ");
				sb.append(" VC.OUT_VOY_NBR, ");
				sb.append(
						" TO_CHAR((SELECT MIN(UA.TRANS_DTTM) FROM UA_DETAILS UA,GB_EDO EDO WHERE EDO.ESN_ASN_NBR = UA.ESN_ASN_NBR AND EDO.EDO_ASN_NBR =:edoNbr ");
				sb.append("'), 'DD/MM/YYYY HH24:MI') AS FIRST_UA,  ");
				sb.append(" EDO.NBR_PKGS AS EDONBR, ");
				sb.append(" EDO.NBR_PKGS - EDO.DN_NBR_PKGS - EDO.TRANS_DN_NBR_PKGS AS BALANCE, ");
				sb.append(" EDO.DN_NBR_PKGS, ");
				sb.append(" EDO.NOM_WT, ");
				sb.append(" EDO.NOM_VOL, ");
				sb.append(" EDO.ACCT_NBR, ");
				sb.append(" EDO.EDO_ASN_NBR, ");
				sb.append(" EDO.BL_NBR, ");
				sb.append(" EDO.CRG_STATUS AS TRANS_TYPE, ");
				sb.append(" ESNM.MARKINGS, ");
				sb.append(" ESN.CRG_DES, ");
				sb.append(" vc.terminal, ");
				sb.append(" vc.scheme, ");
				sb.append(" vc.combi_gc_scheme, ");
				sb.append(" vc.combi_gc_ops_ind  ");
				sb.append(" FROM GB_EDO EDO, VESSEL_CALL VC, BERTHING B, ESN_DETAILS ESN, ESN_MARKINGS ESNM  ");
				sb.append(" WHERE ");
				sb.append(" EDO.VAR_NBR = VC.VV_CD ");
				sb.append(" AND ESN.ESN_ASN_NBR = EDO.ESN_ASN_NBR ");
				sb.append(" AND ESNM.ESN_ASN_NBR = EDO.ESN_ASN_NBR ");
				sb.append(" AND VC.VV_CD = B.VV_CD ");
				sb.append(" AND SHIFT_IND = 1 ");
				sb.append(" AND EDO.EDO_ASN_NBR = :edoNbr ");
				sb.append(" AND EDO.EDO_STATUS = 'A'");
			} else {
				sb.append(" SELECT ");
				sb.append(" TO_CHAR (SYSDATE, 'DD/MM/YYYY HH24:MI') AS TRANS_DTTM, ");
				sb.append(" VC.VSL_NM, ");
				sb.append(" VC.OUT_VOY_NBR, ");
				sb.append(
						" TO_CHAR((SELECT MIN(UA.TRANS_DTTM) FROM UA_DETAILS UA,GB_EDO EDO WHERE EDO.ESN_ASN_NBR = UA.ESN_ASN_NBR AND EDO.EDO_ASN_NBR =:edoNbr ");
				sb.append("'), 'DD/MM/YYYY HH24:MI') AS FIRST_UA,  ");
				sb.append(" EDO.NBR_PKGS AS EDONBR, ");
				sb.append(" EDO.NBR_PKGS - EDO.DN_NBR_PKGS - EDO.TRANS_DN_NBR_PKGS AS BALANCE, ");
				sb.append(" EDO.DN_NBR_PKGS, ");
				sb.append(" EDO.NOM_WT, ");
				sb.append(" EDO.NOM_VOL, ");
				sb.append(" EDO.ACCT_NBR, ");
				sb.append(" EDO.EDO_ASN_NBR, ");
				sb.append(" EDO.BL_NBR, ");
				sb.append(" EDO.CRG_STATUS AS TRANS_TYPE, ");
				sb.append(" ESNM.MARKINGS, ");
				sb.append(" ESN.CRG_DES,  ");
				sb.append(" vc.terminal,  ");
				sb.append(" vc.scheme,  ");
				sb.append(" vc.combi_gc_scheme,  ");
				sb.append(" vc.combi_gc_ops_ind  ");
				sb.append(" FROM GB_EDO EDO, VESSEL_CALL VC, BERTHING B, TESN_PSA_JP ESN, ESN_MARKINGS ESNM  ");
				sb.append(" WHERE  ");
				sb.append(" EDO.VAR_NBR = VC.VV_CD ");
				sb.append(" AND ESN.ESN_ASN_NBR = EDO.ESN_ASN_NBR ");
				sb.append(" AND ESNM.ESN_ASN_NBR = EDO.ESN_ASN_NBR ");
				sb.append(" AND VC.VV_CD = B.VV_CD ");
				sb.append(" AND SHIFT_IND = 1 ");
				sb.append(" AND EDO.EDO_ASN_NBR =:edoNbr ");
				sb.append(" AND EDO.EDO_STATUS = 'A'");
			}

			paramMap.put("edoNbr", edoNbr);
			log.info(" *** fetchShutoutDNCreateDetail SQL *****" + sb.toString());
			log.info(" *** fetchShutoutDNCreateDetail params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			while (rs.next()) {
				String cuurnetdate = rs.getString("trans_dttm");
				String vName = rs.getString("VSL_NM");
				String voyNbr = rs.getString("OUT_VOY_NBR");
				String firstUa = rs.getString("FIRST_UA");
				String declaredQty = rs.getString("EDONBR");
				String balance = rs.getString("BALANCE");
				String declaredVol = rs.getString("NOM_VOL");
				String declaredWt = rs.getString("NOM_WT");
				String accNo = rs.getString("ACCT_NBR");
				String blNbr = rs.getString("BL_NBR");
				String edoAsnNbr = rs.getString("EDO_ASN_NBR");
				String crgDes = rs.getString("CRG_DES");
				String markings = rs.getString("MARKINGS");

				EdoValueObjectOps edoVo = new EdoValueObjectOps();

				edoVo.setTransDate(cuurnetdate);
				edoVo.setVslName(vName);
				edoVo.setFirstUa(firstUa);
				edoVo.setInVoyNbr(voyNbr);
				edoVo.setBlNbr(blNbr);
				edoVo.setNomWt(declaredWt);
				edoVo.setNomVol(declaredVol);
				edoVo.setNoPkgs(declaredQty);
				edoVo.setAcctNo(accNo);
				edoVo.setEdoAsnNbr(edoAsnNbr);
				edoVo.setBalance(balance);
				edoVo.setTransType(transType);
				edoVo.setCrgDes(crgDes);
				edoVo.setMarkings(markings);
				edoVo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				edoVo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				edoVo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				edoVo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				BJDetailsVect.add(edoVo);

			}
			
			log.info("END: *** fetchShutoutDNCreateDetail Result *****" + BJDetailsVect.size());
		} catch (NullPointerException e) {
			log.info("Exception fetchShutoutDNCreateDetail : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception fetchShutoutDNCreateDetail : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchShutoutDNCreateDetail  DAO  END");
		}
		return BJDetailsVect;
	}

	public boolean chkEdoStatus(String esnNbrR) throws BusinessException {
		SqlRowSet rs = null;
		String sql = "";
		boolean bblno = false;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: chkEdoStatus  DAO  Start Obj "+ CommonUtility.deNull(esnNbrR));
			sql = "SELECT EDO_STATUS FROM gb_edo WHERE EDO_STATUS='A' AND EDO_ASN_NBR = :esnNbrR";
			paramMap.put("esnNbrR", esnNbrR);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				bblno = true;
			} else {
				bblno = false;
			}
			log.info("END: *** chkEdoStatus Result *****" + bblno);

		} catch (NullPointerException e) {
			log.info("Exception chkEdoStatus : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkEdoStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkEdoStatus  DAO  END");
		}
		return bblno;
	}

	public String getVesselATBDate(String esnNbrR) throws BusinessException {
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getVesselATBDate  DAO  Start Obj "+ CommonUtility.deNull(esnNbrR));
			sb.append(
					"SELECT TO_CHAR(B.ATB_DTTM,'DD/MM/YYYY HH24:MI') AS ATB_DTTM ,TO_CHAR(B.ETB_DTTM,'DD/MM/YYYY HH24:MI') AS ETB_DTTM,TO_CHAR(VC.VSL_BERTH_DTTM, ");
			sb.append("'DD/MM/YYYY HH24:MI') AS BTR_DTTM FROM VESSEL_CALL VC,GB_EDO GB,BERTHING B ");
			sb.append("WHERE  VC.VV_CD = GB.VAR_NBR AND B.VV_CD=VC.VV_CD AND GB.EDO_ASN_NBR =:esnNbrR ");

			paramMap.put("esnNbrR", esnNbrR);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				String atb = rs.getString("ATB_DTTM");
				String etb = rs.getString("ETB_DTTM");
				String btr = rs.getString("BTR_DTTM");
				if (atb != null) {

					log.info("END: *** getVesselATBDate Result *****" + CommonUtility.deNull(atb));
					return atb;
				}
				if (etb != null) {
					log.info("END: *** getVesselATBDate Result *****" + CommonUtility.deNull(etb));
					return etb;
				}

				if (btr != null) {
					log.info("END: *** getVesselATBDate Result *****" + CommonUtility.deNull(btr));
					return btr;
				}
			}

		} catch (NullPointerException e) {
			log.info("Exception getVesselATBDate : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVesselATBDate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselATBDate  DAO  END");
		}
		return null;
	}

	public List<EdoValueObjectOps> fetchDNCreateDetail(String edoNbr, String transType, String searchcrg,
			String tesn_nbr) throws BusinessException {
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		List<EdoValueObjectOps> BJDetailsVect = new ArrayList<EdoValueObjectOps>();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: fetchDNCreateDetail  DAO  Start Obj "+" edoNbr:"+edoNbr+" transType:"+transType+" searchcrg:"+searchcrg+" tesn_nbr:"+tesn_nbr);

			boolean chkEdostatus = chkEdoStatus(edoNbr);
			if (!chkEdostatus) {
				log.info("Writing from DnBeanBean.fetchDNCreateDetail");
				log.info("EDO has been cancelled");
				throw new BusinessException("M80001");
			}

			// boolean chkGbj = chkGBJ(edoNbr);
			/*
			 * if (chkGbj) {
			 * LogManager.instance.logInfo("Writing from DnBeanBean.fetchDNCreateDetail" );
			 * LogManager.instance.logInfo("BJ has been Closed.  No DN can be created" );
			 * throw new BusinessException("M80002"); }
			 */

			boolean chkVvstatus = chkVVStatus(edoNbr);
			if (!chkVvstatus) {
				log.info("Writing from DnBeanBean.fetchDNCreateDetail");
				log.info("Vessel has not Berthed yet.  DN cannot be printed");
				throw new BusinessException("M80003");
			}

			/*
			 * boolean chkCargorls = chkCargoRls(edoNbr); if (!chkCargorls) { LogManager
			 * .instance.logInfo("Writing from DnBeanBean.fetchDNCreateDetail");
			 * LogManager.instance.logInfo(
			 * "Cargo needs to be release before delivery can take place"); throw new
			 * BusinessException("M80004"); }
			 */

			/*
			 * boolean chkCargostatus = chkCargoStatus(edoNbr); if (!chkCargostatus) {
			 * LogManager.instance.logInfo("Writing from DnBeanBean.fetchDNCreateDetail" );
			 * LogManager.instance.logInfo("No more Authorized Cargo for Delivery"); throw
			 * new BusinessException("M80005"); }
			 */
			if (transType.equals("L")) {
				/*
				 * sql =
				 * "select to_char(sysdate,'dd/mm/yyyy hh24:mi') as trans_dttm,vessel_call.vsl_nm , vessel_call.in_voy_nbr,to_char(berthing.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb,"
				 * +
				 * "to_char(berthing.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,manifest_details.CRG_DES,manifest_details.CNTR_SIZE , manifest_details.CNTR_TYPE ,"
				 * +
				 * "mft_markings.mft_markings,gb_edo.nbr_pkgs as edoNbr,gb_edo.nbr_pkgs - gb_edo.trans_nbr_pkgs - gb_edo.release_nbr_pkgs - gb_edo.dn_nbr_pkgs as balance ,"
				 * +
				 * "gb_edo.dn_nbr_pkgs ,gb_edo.nom_wt ,gb_edo.NOM_VOL ,gb_edo.acct_nbr , gb_edo.EDO_ASN_NBR , bl_cntr_details.CNTR_NBR,"
				 * +
				 * "gb_edo.bl_nbr,gb_edo.CRG_STATUS as TRANS_TYPE from gb_edo , vessel_call,berthing ,bl_cntr_details ,manifest_details ,  mft_markings "
				 * +
				 * "where gb_edo.var_nbr =  vessel_call.vv_cd and vessel_call.vv_cd = berthing.vv_cd  and manifest_details.MFT_SEQ_NBR = mft_markings.MFT_SQ_NBR and SHIFT_IND = 1 and "
				 * +
				 * "gb_edo.mft_seq_nbr = manifest_details.mft_seq_nbr AND manifest_details.mft_seq_nbr = bl_cntr_details.mft_seq_nbr(+) and  gb_edo.EDO_ASN_NBR = '"
				 * + edoNbr + "' and gb_edo.edo_status='A'";
				 */
				sb.append(
						"SELECT TO_CHAR(SYSDATE,'DD/MM/YYYY HH24:MI') AS TRANS_DTTM,VESSEL_CALL.VSL_NM , VESSEL_CALL.IN_VOY_NBR,TO_CHAR(BERTHING.ATB_DTTM,'DD/MM/YYYY HH24:MI') AS ATB,");
				sb.append(
						"TO_CHAR(DECODE(MANIFEST_DETAILS.UNSTUFF_SEQ_NBR,0,BERTHING.GB_COD_DTTM,CC_UNSTUFF_MANIFEST.DTTM_UNSTUFF),'DD/MM/YYYY HH24:MI') AS COD,MANIFEST_DETAILS.CRG_DES,MANIFEST_DETAILS.CNTR_SIZE , MANIFEST_DETAILS.CNTR_TYPE ,");
				sb.append(
						"MFT_MARKINGS.MFT_MARKINGS,GB_EDO.NBR_PKGS AS EDONBR,GB_EDO.NBR_PKGS - GB_EDO.TRANS_NBR_PKGS - GB_EDO.RELEASE_NBR_PKGS - GB_EDO.DN_NBR_PKGS AS BALANCE ,");
				sb.append(
						"GB_EDO.DN_NBR_PKGS ,GB_EDO.NOM_WT ,GB_EDO.NOM_VOL ,GB_EDO.ACCT_NBR , GB_EDO.EDO_ASN_NBR , BL_CNTR_DETAILS.CNTR_NBR,");
				sb.append("GB_EDO.BL_NBR,GB_EDO.CRG_STATUS AS TRANS_TYPE, ");
				sb.append(
						" VESSEL_CALL.TERMINAL, VESSEL_CALL.SCHEME, VESSEL_CALL.COMBI_GC_SCHEME,  VESSEL_CALL.COMBI_GC_OPS_IND ");
				sb.append(
						" FROM GB_EDO , VESSEL_CALL,BERTHING ,BL_CNTR_DETAILS ,MANIFEST_DETAILS ,  MFT_MARKINGS,CC_UNSTUFF_MANIFEST ");
				sb.append(
						"WHERE GB_EDO.VAR_NBR =  VESSEL_CALL.VV_CD AND VESSEL_CALL.VV_CD = BERTHING.VV_CD  AND MANIFEST_DETAILS.MFT_SEQ_NBR = MFT_MARKINGS.MFT_SQ_NBR AND SHIFT_IND = 1 AND ");
				sb.append(
						"GB_EDO.MFT_SEQ_NBR = MANIFEST_DETAILS.MFT_SEQ_NBR AND MANIFEST_DETAILS.MFT_SEQ_NBR = BL_CNTR_DETAILS.MFT_SEQ_NBR(+) AND  MANIFEST_DETAILS.UNSTUFF_SEQ_NBR=CC_UNSTUFF_MANIFEST.UNSTUFF_SEQ_NBR(+) AND GB_EDO.EDO_ASN_NBR =:edoNbr ");
				sb.append(" AND GB_EDO.EDO_STATUS='A'");

			} else {
				/*
				 * sql =
				 * "select to_char(sysdate,'dd/mm/yyyy hh24:mi') as trans_dttm,vessel_call.vsl_nm , vessel_call.in_voy_nbr,to_char(berthing.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb,"
				 * +
				 * "to_char(berthing.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,manifest_details.CRG_DES,manifest_details.CNTR_SIZE , manifest_details.CNTR_TYPE ,"
				 * +
				 * "mft_markings.mft_markings,gb_edo.nbr_pkgs as edoNbr,gb_edo.trans_nbr_pkgs - gb_edo.release_nbr_pkgs - gb_edo.trans_dn_nbr_pkgs as balance ,"
				 * +
				 * "gb_edo.dn_nbr_pkgs ,gb_edo.nom_wt ,gb_edo.NOM_VOL ,gb_edo.acct_nbr , gb_edo.EDO_ASN_NBR , bl_cntr_details.CNTR_NBR,"
				 * +
				 * "gb_edo.bl_nbr,gb_edo.CRG_STATUS as TRANS_TYPE from gb_edo , vessel_call,berthing ,bl_cntr_details ,manifest_details ,  mft_markings "
				 * +
				 * "where gb_edo.var_nbr =  vessel_call.vv_cd and vessel_call.vv_cd = berthing.vv_cd  and manifest_details.MFT_SEQ_NBR = mft_markings.MFT_SQ_NBR and SHIFT_IND = 1 and "
				 * +
				 * "gb_edo.mft_seq_nbr = manifest_details.mft_seq_nbr AND manifest_details.mft_seq_nbr = bl_cntr_details.mft_seq_nbr(+) and  gb_edo.EDO_ASN_NBR = '"
				 * + edoNbr + "' and gb_edo.edo_status='A'";
				 */
				sb.append(
						"SELECT TO_CHAR(SYSDATE,'DD/MM/YYYY HH24:MI') AS TRANS_DTTM,VESSEL_CALL.VSL_NM , VESSEL_CALL.IN_VOY_NBR,TO_CHAR(BERTHING.ATB_DTTM,'DD/MM/YYYY HH24:MI') AS ATB,");
				sb.append(
						"TO_CHAR(DECODE(MANIFEST_DETAILS.UNSTUFF_SEQ_NBR,0,BERTHING.GB_COD_DTTM,CC_UNSTUFF_MANIFEST.DTTM_UNSTUFF),'DD/MM/YYYY HH24:MI') AS COD,MANIFEST_DETAILS.CRG_DES,MANIFEST_DETAILS.CNTR_SIZE , MANIFEST_DETAILS.CNTR_TYPE ,");
				sb.append(
						"MFT_MARKINGS.MFT_MARKINGS,GB_EDO.NBR_PKGS AS EDONBR,GB_EDO.TRANS_NBR_PKGS - GB_EDO.RELEASE_NBR_PKGS - GB_EDO.TRANS_DN_NBR_PKGS AS BALANCE ,");
				sb.append(
						"GB_EDO.DN_NBR_PKGS ,GB_EDO.NOM_WT ,GB_EDO.NOM_VOL ,GB_EDO.ACCT_NBR , GB_EDO.EDO_ASN_NBR , BL_CNTR_DETAILS.CNTR_NBR,");
				sb.append("GB_EDO.BL_NBR,GB_EDO.CRG_STATUS AS TRANS_TYPE, ");
				sb.append(
						" VESSEL_CALL.TERMINAL, VESSEL_CALL.SCHEME, VESSEL_CALL.COMBI_GC_SCHEME,  VESSEL_CALL.COMBI_GC_OPS_IND ");
				sb.append(
						" FROM GB_EDO , VESSEL_CALL,BERTHING ,BL_CNTR_DETAILS ,MANIFEST_DETAILS ,  MFT_MARKINGS,CC_UNSTUFF_MANIFEST ");
				sb.append(
						"WHERE GB_EDO.VAR_NBR =  VESSEL_CALL.VV_CD AND VESSEL_CALL.VV_CD = BERTHING.VV_CD  AND MANIFEST_DETAILS.MFT_SEQ_NBR = MFT_MARKINGS.MFT_SQ_NBR AND SHIFT_IND = 1 AND ");
				sb.append(
						"GB_EDO.MFT_SEQ_NBR = MANIFEST_DETAILS.MFT_SEQ_NBR AND MANIFEST_DETAILS.MFT_SEQ_NBR = BL_CNTR_DETAILS.MFT_SEQ_NBR(+) AND MANIFEST_DETAILS.UNSTUFF_SEQ_NBR=CC_UNSTUFF_MANIFEST.UNSTUFF_SEQ_NBR(+) AND GB_EDO.EDO_ASN_NBR =:edoNbr ");
				sb.append(" AND GB_EDO.EDO_STATUS='A'");

			}

			paramMap.put("edoNbr", edoNbr);
			log.info(" *** fetchDNCreateDetail SQL *****" + sb.toString());
			log.info(" *** fetchDNCreateDetail params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			String atb = "";
			String cod = "";
			String vName = "";
			String voyNbr = "";
			String cntrNo = "";
			String cntType = "";
			String cntrSize = "";
			String edoAsnNbr = "";
			String blNbr = "";
			String declaredWt = "";
			String declaredVol = "";
			String declaredQty = "";
			String marking = "";
			String crgDesc = "";
			String accNo = "";
			String balance = "";
			String cuurnetdate = "";
			while (rs.next()) {

				cuurnetdate = rs.getString("trans_dttm");
				atb = rs.getString("atb");
				cod = rs.getString("cod");
				vName = rs.getString("vsl_nm");
				voyNbr = rs.getString("in_voy_nbr");
				cntrNo = rs.getString("CNTR_NBR");
				cntType = rs.getString("CNTR_TYPE");
				cntrSize = rs.getString("CNTR_SIZE");
				edoAsnNbr = rs.getString("EDO_ASN_NBR");
				blNbr = rs.getString("bl_nbr");
				declaredWt = rs.getString("nom_wt");
				declaredVol = rs.getString("NOM_VOL");
				declaredQty = rs.getString("edoNbr");
				marking = rs.getString("mft_markings");
				crgDesc = rs.getString("CRG_DES");
				accNo = rs.getString("acct_nbr");
				balance = rs.getString("balance");

				EdoValueObjectOps edoVo = new EdoValueObjectOps();

				edoVo.setTransDate(cuurnetdate);
				edoVo.setATB(atb);
				edoVo.setCOD(cod);
				edoVo.setVslName(vName);
				edoVo.setInVoyNbr(voyNbr);
				edoVo.setBlNbr(blNbr);
				edoVo.setNomWt(declaredWt);
				edoVo.setNomVol(declaredVol);
				edoVo.setNoPkgs(declaredQty);
				edoVo.setMarkings(marking);
				edoVo.setCrgDes(crgDesc);
				edoVo.setAcctNo(accNo);
				edoVo.setCntrNo(cntrNo);
				edoVo.setCntrSize(cntrSize);
				edoVo.setCntrType(cntType);
				edoVo.setEdoAsnNbr(edoAsnNbr);
				edoVo.setBalance(balance);
				edoVo.setTransType(transType);
				edoVo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				edoVo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				edoVo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				edoVo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				BJDetailsVect.add(edoVo);

			}
			log.info("END: *** fetchDNCreateDetail Result *****" + BJDetailsVect.size());

		} catch (NullPointerException e) {
			log.info("Exception fetchDNCreateDetail : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception fetchDNCreateDetail : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchDNCreateDetail  DAO  END");
		}
		return BJDetailsVect;
	}

	public boolean chkVVStatus(String esnNbrR) throws BusinessException {
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		boolean bblno = false;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: chkVVStatus  DAO  Start Obj "+ CommonUtility.deNull(esnNbrR));
			sb.append(
					"SELECT VC.VV_STATUS_IND FROM VESSEL_CALL VC,GB_EDO GB WHERE VC.VV_STATUS_IND IN('UB','BR','CL') ");
			sb.append("AND VC.VV_CD = GB.VAR_NBR AND GB.EDO_ASN_NBR =:esnNbrR ");
			paramMap.put("esnNbrR", esnNbrR);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				bblno = true;
			} else {
				bblno = false;
			}
			log.info("END: *** chkVVStatus Result *****" + bblno);
		} catch (NullPointerException e) {
			log.info("Exception chkVVStatus : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkVVStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkVVStatus  DAO  END");
		}
		return bblno;
	}

	// EndRegion DnEdoDetailJdbcRepository

}
