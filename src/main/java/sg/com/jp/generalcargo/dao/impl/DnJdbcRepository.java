package sg.com.jp.generalcargo.dao.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.DnRepository;
import sg.com.jp.generalcargo.domain.DNCntrJasperReport;
import sg.com.jp.generalcargo.domain.EdoValueObjectContainerised;
import sg.com.jp.generalcargo.domain.ReportValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;

@Repository("dnRepository")
public class DnJdbcRepository implements DnRepository {
	private static final Log log = LogFactory.getLog(DnJdbcRepository.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public String logStatusGlobal = "Y";
	private boolean DEBUG = false;
	private boolean INFO = false;

	@Value("${ReportPrintingBean.directory.pdf}")
	private String printingBeanPdf;

	@Value("${ReportPrintingBean.directory.report}")
	private String directoryReport;

	/*
	 * @Value("${spring.datasource.username}") private String db_user;
	 * 
	 * 
	 * 
	 * 
	 * @Value("${spring.datasource.password}") private String db_pwd;
	 * 
	 * @Value("${spring.datasource.url}") private String db_url; private static
	 * final String db_driver = "oracle.jdbc.driver.OracleDriver";
	 */

	private static final String param = " paramMap: ";

	// /ejb.sessionBeans.gbms.containerised.dn -->dnBean
	@Override
	public boolean chkraiseCharge(String edonbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		String sql = "";
		boolean countua = false;
		try {
			log.info("START: chkraiseCharge  DAO  Start Obj " + " edonbr:" + edonbr);

			sb.append("select to_char(b.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb from gb_edo a,berthing b,");
			sb.append("vessel_call vc where a.var_nbr =  vc.vv_cd and vc.vv_cd = b.vv_cd and a.edo_asn_nbr=:edonbr ");
			sb.append(" and to_date(to_char(b.ATB_DTTM,'ddmmyyyy'),'ddmmyyyy') >= to_date('06112002','ddmmyyyy')");

			sql = sb.toString();

			log.info(" *** chkCntrCrgDn SQL *****" + sql + param + paramMap);

			paramMap.put("edonbr", edonbr);

			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next())
				countua = true;

			log.info("END: *** chkraiseCharge Result *****" + countua);
		} catch (NullPointerException e) {
			log.info("Exception chkraiseCharge :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkraiseCharge :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkraiseCharge  DAO  END");
		}
		return countua;
	}

	@Override
	public String createDN(String edoNbr, String transtype, String edo_Nbr_Pkgs, String NomWt, String NomVol,
			String date_time, String transQty, String nric_no, String dpname, String veh1, String veh2, String veh3,
			String veh4, String veh5, String userid, String icType, String searchcrg, String tesn_nbr,
			String strCntrNum, String strStuffDt) throws BusinessException {
		String sql = "";
		String DNNbr = "";
		String edosql = "";
		String sqlJp = "";
		String sqlupdJp = "";
		String edoupdsql = "";
		String dnnbrtrans = "";
		String sqlveh1 = "";
		String sqlveh2 = "";
		String sqlveh3 = "";
		String sqlveh4 = "";
		String sqlveh5 = "";

		// lak added for audit trial 23/01/2003 start
		String sqlupdJp_trans = "";
		String sqlveh1_trans = "";
		String sqlveh2_trans = "";
		String sqlveh3_trans = "";
		String sqlveh4_trans = "";
		String sqlveh5_trans = "";
		String sql_trans = "";
		String sqltlog1 = "";
		String sqltlog = "";

		// int stransno = 0;
		int stransno1 = 0;
		int countua = 0;
		int count = 0;
		int newuanbrpkgs = 0;
		int newuanbrpkgs_tesn = 0;

		double Bill_ton = 0.0;
		double dn_nom_wt = 0;
		double dn_nom_vol = 0;
		// lak added for audit trial 23/01/2003 end

		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		SqlRowSet rs_tesn = null;
		StringTokenizer dntrans = null;

		StringBuffer sb = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: createDN  DAO  Start Obj " + " edoNbr: " +edoNbr + " transtype: " + transtype+ " edo_Nbr_Pkgs: " + edo_Nbr_Pkgs+ " NomWt: " +NomWt
					+ " NomVol: " +NomVol+ " date_time: " +date_time + " transQty: " +transQty + " nric_no: " + nric_no+ " dpname: " + dpname+ " veh1: " +veh1 + " veh2: " +veh2
					 + " veh3: " + veh3+ " veh4: " + veh4+ " veh5: " +veh5 + " userid: " +userid + " icType: " +icType + " searchcrg: " + searchcrg+
					 " tesn_nbr: " +tesn_nbr + " strCntrNum: " + strCntrNum+ " strStuffDt: " + strStuffDt);
			dnnbrtrans = getDNNbr(edoNbr);
			dntrans = new java.util.StringTokenizer(dnnbrtrans, "-");
			DNNbr = (dntrans.nextToken()).trim();
			(dntrans.nextToken()).trim();
			// ftrans = (dntrans.nextToken()).trim();

			log.info("DNNbr >>>>>>>>> " + DNNbr);

			dn_nom_wt = (Double.parseDouble(transQty) / Double.parseDouble(edo_Nbr_Pkgs)) * Double.parseDouble(NomWt);
			dn_nom_vol = (Double.parseDouble(transQty) / Double.parseDouble(edo_Nbr_Pkgs)) * Double.parseDouble(NomVol);

			if ((dn_nom_wt / 1000) > dn_nom_vol) {
				Bill_ton = dn_nom_wt / 1000;
			} else {
				Bill_ton = dn_nom_vol;
			} // end if nomwt

			edosql = "select * from gb_edo where edo_asn_nbr = :edoNbr";

			String chktesnJpJp_nbr = chktesnJpJp_nbr(tesn_nbr);
			String chktesnJpPsa_nbr = chktesnJpPsa_nbr(tesn_nbr);

			if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {
				if (chktesnJpJp_nbr.equals("Y")) {
					sqlJp = "select * from tesn_jp_jp where esn_asn_nbr =:tesn_nbr ";

					sqlupdJp = "update tesn_jp_jp set dn_nbr_pkgs =:newuanbrpkgs_tesn ";

					// lak added for Audit Trial
					sqlupdJp_trans = "insert into tesn_jp_jp_trans (trans_nbr,edo_asn_nbr,esn_asn_nbr,dn_nbr_pkgs,last_modify_dttm,last_modify_user_id,remarks) values(";
					sqltlog1 = "SELECT MAX(TRANS_NBR) maxTransNbr FROM tesn_jp_jp_TRANS WHERE esn_asn_NBR=:tesn_nbr ";
				} else if (chktesnJpPsa_nbr.equals("Y")) {
					sqlJp = "select * from tesn_jp_psa where esn_asn_nbr =:tesn_nbr ";
					sqlupdJp = "update tesn_jp_psa set dn_nbr_pkgs =:newuanbrpkgs_tesn ";

					// lak added for Audit Trial
					sqlupdJp_trans = "insert into tesn_jp_psa_trans (trans_nbr,edo_asn_nbr,esn_asn_nbr,dn_nbr_pkgs,last_modify_dttm,last_modify_user_id,remarks) values(";
					sqltlog1 = "SELECT MAX(TRANS_NBR) maxTransNbr FROM tesn_jp_psa_TRANS WHERE esn_asn_NBR=:tesn_nbr ";
				}
			}

			if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
				edoupdsql = "update gb_edo set dn_nbr_pkgs =";
				sb.append("insert into dn_details(DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, ");
				sb.append("TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM");
				sb.append(",LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,CNTR_NBR,STUFF_DTTM)");
				sb.append(" VALUES(:DNNbr,:edoNbr ,'A', ");
				sql = sb.toString();

				// laks added for audit trial
				sb.setLength(0);
				sb.append("insert into dn_details_trans(trans_nbr,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,");
				sb.append("DP_IC_TYPE,TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM");
				sb.append(",LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE) VALUES");
				sb.append("('0',:DNNbr,:edoNbr,'A', ");
				sql_trans = sb.toString();
			} else {
				edoupdsql = "update gb_edo set trans_dn_nbr_pkgs = ";

				sb.setLength(0);
				sb.append("insert into dn_details(DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, ");
				sb.append("TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,");
				sb.append("LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TESN_ASN_NBR,");
				sb.append("CNTR_NBR,STUFF_DTTM) VALUES(:DNNbr,:edoNbr,'A',");
				sql = sb.toString();

				// laks added for audit trial
				sb.setLength(0);
				sb.append("insert into dn_details_trans(trans_nbr,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,");
				sb.append("DP_NM,DP_IC_TYPE,TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,");
				sb.append(
						"DN_CREATE_DTTM,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TESN_ASN_NBR)");
				sb.append(" VALUES('0',:DNNbr,:edoNbr,'A',");
				sql_trans = sb.toString();
			}
			sqlveh1 = "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values(:DNNbr,1,:veh1)";
			sqlveh2 = "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values(:DNNbr,2,:veh2)";
			sqlveh3 = "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values(:DNNbr,3,:veh3)";
			sqlveh4 = "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values(:DNNbr,4,:veh4)";
			sqlveh5 = "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values(:DNNbr,5,:veh5)";

			sqlveh1_trans = "insert into dn_veh_trans(trans_nbr,DN_NBR,DN_VEH_SEQ,VEH_NO) values('0',:DNNbr,1,:veh1)";
			sqlveh2_trans = "insert into dn_veh_trans(trans_nbr,DN_NBR,DN_VEH_SEQ,VEH_NO) values('0',:DNNbr,2,:veh2)";
			sqlveh3_trans = "insert into dn_veh_trans(trans_nbr,DN_NBR,DN_VEH_SEQ,VEH_NO) values('0',:DNNbr,3,:veh3)";
			sqlveh4_trans = "insert into dn_veh_trans(trans_nbr,DN_NBR,DN_VEH_SEQ,VEH_NO) values('0',:DNNbr,4,:veh4)";
			sqlveh5_trans = "insert into dn_veh_trans(trans_nbr,DN_NBR,DN_VEH_SEQ,VEH_NO) values('0',:DNNbr,5,:veh5)";

			paramMap.put("edoNbr", edoNbr);
			paramMap.put("DNNbr", DNNbr);
			paramMap.put("tesn_nbr", tesn_nbr);
			paramMap.put("veh1", veh1);
			paramMap.put("veh2", veh2);
			paramMap.put("veh3", veh3);
			paramMap.put("veh4", veh4);
			paramMap.put("veh5", veh5);

			log.info(" *** createDN SQL *****" + edosql);
			log.info(" *** createDN params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(edosql, paramMap);

			if (rs.next()) {
				if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT"))
					newuanbrpkgs = rs.getInt("dn_nbr_pkgs");
				else
					newuanbrpkgs = rs.getInt("TRANS_DN_NBR_PKGS");

				newuanbrpkgs = newuanbrpkgs + Integer.parseInt(transQty);
			}
			paramMap.put("newuanbrpkgs", newuanbrpkgs);
			edoupdsql = edoupdsql + newuanbrpkgs+ " where edo_asn_nbr =:edoNbr";

			log.info(" *** createDN SQL *****" + edoupdsql);
			log.info(" *** createDN params *****" + paramMap.toString());
			count = namedParameterJdbcTemplate.update(edoupdsql, paramMap);

			// lak added for Audit Trail start
			sqltlog = "SELECT MAX(TRANS_NBR) maxTransNbr FROM gb_edo_TRANS WHERE Edo_ASN_NBR=:edoNbr ";

			if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 22/01/2003
			{
				log.info(" *** createDN SQL *****" + sqltlog);
				log.info(" *** createDN params *****" + paramMap.toString());
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);
				if (rs1.next()) {
					// stransno = (rs1.getInt("maxTransNbr")) + 1;
				} else {
					// stransno = 0;
				}
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				// count_trans = stmt.executeUpdate(edoupdsql_trans);
			}

			// lak added for Audit Trail end
			if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {
				log.info(" *** createDN SQL *****" + sqlJp);
				log.info(" *** createDN params *****" + paramMap.toString());
				rs_tesn = namedParameterJdbcTemplate.queryForRowSet(sqlJp, paramMap);
				if (rs_tesn.next()) {
					newuanbrpkgs_tesn = rs_tesn.getInt("DN_NBR_PKGS");
					newuanbrpkgs_tesn = newuanbrpkgs_tesn + Integer.parseInt(transQty);
				}
				sqlupdJp = sqlupdJp + "where esn_asn_nbr =:tesn_nbr ";
				paramMap.put("newuanbrpkgs_tesn", newuanbrpkgs_tesn);
				log.info(" *** createDN SQL *****" + sqlupdJp);
				log.info(" *** createDN params *****" + paramMap.toString());
				namedParameterJdbcTemplate.update(sqlupdJp, paramMap);

				// lak added for audit Trail

				if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 22/01/2003
				{
					log.info(" *** createDN SQL *****" + sqltlog1);
					log.info(" *** createDN params *****" + paramMap.toString());
					rs2 = namedParameterJdbcTemplate.queryForRowSet(sqltlog1, paramMap);
					if (rs2.next()) {
						stransno1 = (rs2.getInt("maxTransNbr")) + 1;
					} else {
						stransno1 = 0;
					}
				}

				sqlupdJp_trans = sqlupdJp_trans
						+ ":stransno1,:edoNbr,:tesn_nbr,:newuanbrpkgs_tesn,sysdate,:userid,'DN Add')";
				paramMap.put("stransno1", stransno1);
				paramMap.put("userid", userid);
				paramMap.put("newuanbrpkgs_tesn", newuanbrpkgs_tesn);
				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					log.info(" *** createDN SQL *****" + sqlupdJp_trans);
					log.info(" *** createDN params *****" + paramMap.toString());
					namedParameterJdbcTemplate.update(sqlupdJp_trans, paramMap);
				}

				// lak added for audit Trail
				log.info("edoupsql count >>>>>>>>> " + count);
			}
			log.info("inside count 88888 \\\\---than 0000000");
			if (count == 0) {
				throw new BusinessException("M4201");
			} else if (count > 0) {
				log.info("inside count >>>>>>>>>than 0000000");
				paramMap.put("nric_no", nric_no);
				paramMap.put("dpname", dpname);
				paramMap.put("icType", icType);
				paramMap.put("date_time", date_time);
				paramMap.put("transQty", transQty);
				paramMap.put("dn_nom_wt", dn_nom_wt);
				paramMap.put("dn_nom_vol", dn_nom_vol);
				paramMap.put("Bill_ton", Bill_ton);
				paramMap.put("userid", userid);
				paramMap.put("transtype", transtype);
				paramMap.put("strCntrNum", strCntrNum);
				paramMap.put("strStuffDt", strStuffDt);
				if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
					sb.setLength(0);
					sb.append(":nric_no,:dpname,:icType,to_date(:date_time,'DD/MM/YYYY HH24:MI'),");
					sb.append(":transQty,:dn_nom_wt,:dn_nom_vol,:Bill_ton,:userid,sysdate,:userid,");
					sb.append("sysdate,'C',:transtype,:strCntrNum,to_date(:strStuffDt,'DD/MM/YYYY HH24:MI'))");
					sql = sql + sb.toString();

					// lak added for Audit trail
					sb.setLength(0);
					sb.append(":nric_no,:dpname,:icType,to_date(:date_time,'DD/MM/YYYY HH24:MI'),");
					sb.append(":transQty,:dn_nom_wt,:dn_nom_vol,:Bill_ton,:userid,sysdate,:userid");
					sb.append(",sysdate,'C',:transtype)");
					sql_trans = sql_trans + sb.toString();
					// '"+strCntrNum+"',to_date('"+strStuffDt+"','DD/MM/YYYY HH24:MI'))";
					log.info("sql >>>>>>>>> " + sql + param + paramMap);
				} else {
					sb.setLength(0);
					sb.append(":nric_no,:dpname,:icType,to_date(:date_time,'DD/MM/YYYY HH24:MI'),");
					sb.append(":transQty,:dn_nom_wt,:dn_nom_vol,:Bill_ton,:userid,sysdate,:userid,");
					sb.append(
							"sysdate,'C',:transtype,:tesn_nbr,:strCntrNum,to_date(:strStuffDt,'DD/MM/YYYY HH24:MI'))");
					sql = sql + sb.toString();

					// lak added for Audit trail
					sb.setLength(0);
					sb.append(":nric_no,:dpname,:icType,to_date(:date_time,'DD/MM/YYYY HH24:MI'),");
					sb.append(":transQty,:dn_nom_wt,:dn_nom_vol,:Bill_ton,:userid,sysdate,:userid,");
					sb.append("sysdate,'C',:transtype,:tesn_nbr)");
					sql_trans = sql_trans + sb.toString();

					log.info("sql >>>>>>>>> " + sql + param + paramMap);
				}
				log.info(" *** createDN SQL *****" + sql + param + paramMap);
				countua = namedParameterJdbcTemplate.update(sql, paramMap);
				log.info("insertion count >>>>>>>>> " + countua);

				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					log.info(" *** createDN SQL *****" + sql_trans);
					log.info(" *** createDN params *****" + paramMap.toString());
					namedParameterJdbcTemplate.update(sql_trans, paramMap);
				}
				if (veh1 != null && !veh1.equals("")) {
					log.info(" *** createDN SQL *****" + sqlveh1);
					log.info(" *** createDN params *****" + paramMap.toString());
					namedParameterJdbcTemplate.update(sqlveh1, paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 24/01/2003
					{
						log.info(" *** createDN SQL *****" + sqlveh1_trans);
						log.info(" *** createDN params *****" + paramMap.toString());
						namedParameterJdbcTemplate.update(sqlveh1_trans, paramMap);
					}
				}
				if (veh2 != null && !veh2.equals("")) {
					log.info(" *** createDN SQL *****" + sqlveh2);
					log.info(" *** createDN params *****" + paramMap.toString());
					namedParameterJdbcTemplate.update(sqlveh2, paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 24/01/2003
					{
						log.info(" *** createDN SQL *****" + sqlveh2_trans);
						log.info(" *** createDN params *****" + paramMap.toString());
						namedParameterJdbcTemplate.update(sqlveh2_trans, paramMap);
					}
				}
				if (veh3 != null && !veh3.equals("")) {
					log.info(" *** createDN SQL *****" + sqlveh3);
					log.info(" *** createDN params *****" + paramMap.toString());
					namedParameterJdbcTemplate.update(sqlveh3, paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 24/01/2003
					{
						log.info(" *** createDN SQL *****" + sqlveh3_trans);
						log.info(" *** createDN params *****" + paramMap.toString());
						namedParameterJdbcTemplate.update(sqlveh3_trans, paramMap);
					}
				}
				if (veh4 != null && !veh4.equals("")) {
					log.info(" *** createDN SQL *****" + sqlveh4);
					log.info(" *** createDN params *****" + paramMap.toString());
					namedParameterJdbcTemplate.update(sqlveh4, paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 24/01/2003
					{
						log.info(" *** createDN SQL *****" + sqlveh4_trans);
						namedParameterJdbcTemplate.update(sqlveh4_trans, paramMap);
					}
				}
				if (veh5 != null && !veh5.equals("")) {
					log.info(" *** createDN SQL *****" + sqlveh5);
					log.info(" *** createDN params *****" + paramMap.toString());
					namedParameterJdbcTemplate.update(sqlveh5, paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 24/01/2003
					{
						log.info(" *** createDN SQL *****" + sqlveh5_trans);
						log.info(" *** createDN params *****" + paramMap.toString());
						namedParameterJdbcTemplate.update(sqlveh5_trans, paramMap);
					}
				}
			} // end if count
			if (countua == 0) {
				throw new BusinessException("M4201");
			}
			log.info("END: *** createDN Result *****" + DNNbr.toString());
		} catch (BusinessException e) {
			log.info("Exception createDN : ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception createDN : ", e);
			throw new BusinessException("M4201");
		} catch (Exception ex) {
			log.info("Exception createDN : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: createDN  DAO  END");
		}
		return DNNbr;
	}

	private String getDNNbr(String edoNo) throws BusinessException {
		String DnNbr = "";
		String ftrans = "";
		String retval = "";
		int count = 0;
		int tempInt = 0;
		String tempval = "";
		String tempua = "";
		String sql = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getDNNbr  DAO  Start Obj edoNo:" + edoNo);
			String tempEdoNo = edoNo;
			if (edoNo.length() == 7) {
				edoNo = "0" + edoNo;
			} else {
				edoNo = tempEdoNo;
			}
			sql = "select max(DN_NBR) as maxDnNbr from dn_details where dn_nbr like :edoNo";
			paramMap.put("edoNo", "D" + edoNo + "%");

			log.info(" *** getDNNbr SQL *****" + sql + param + paramMap);

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			log.info("after-----excu---inside getDnNbr--" + sql + param + paramMap);
			if (rs.next()) {
				if (rs.getString("maxDnNbr") != null && rs.getString("maxDnNbr").length() != 0) {
					count = 1;
					tempua = rs.getString("maxDnNbr");
				} else
					count = 0;
			}
			log.info("Count--" + count);
			if (count == 0) {
				DnNbr = "D" + edoNo + "0000";
				ftrans = "True";
			} else {
				tempInt = Integer.parseInt((tempua).substring(9, 13));
				tempInt = tempInt + 1;

				if (tempInt >= 0 && tempInt <= 9) {
					tempval = "000" + tempInt;
				} else {
					if (tempInt >= 10 && tempInt <= 99) {
						tempval = "00" + tempInt;
					} else if (tempInt >= 100 && tempInt <= 999) {
						tempval = "0" + tempInt;
					} else {
						tempval = "" + tempInt;
					}
				} // inside if close
				DnNbr = "D" + edoNo + tempval;
				ftrans = "False";
			} // outside if close

			retval = DnNbr + "-" + ftrans;
			log.info("END: *** getDNNbr Result *****" + retval.toString());
		} catch (NullPointerException e) {
			log.info("Exception getDNNbr : ", e);
			throw new BusinessException("M4201");
		} catch (Exception ex) {
			log.info("Exception getDNNbr : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getDNNbr  DAO  END");
		}
		return retval;

	}


	// ejb.sessionBeans.reports-->ReportPrintingBeanEJB -->printReport 
	//remove printing method, only get filename.
	@Override
	public String getPdfFileName(ReportValueObject rvo, String dnNbr) throws BusinessException {

		String pdfDir = printingBeanPdf + '/';
		String printer = ConstantUtil.ReportPrintingBean_printer;

		if (DEBUG)
			log.info(rvo.toString());

		try {
			log.info("START: getPdfFileName  DAO  Start Obj rvo:" + rvo + " dnNbr: "+dnNbr);
			// ---------------------------------------------------------
			// Get the settings properties
			// ---------------------------------------------------------
			if (DEBUG)
				log.info("Get the properties");
			boolean isWindows = ConstantUtil.ReportPrintingBean_isWindows;
			
			if (rvo.getOutputDirectory() != null) {
				if (DEBUG)
					log.info("Append subdirectory by rvo for ps files");
				pdfDir += rvo.getOutputDirectory() + '/';

			}

			if (DEBUG)
				log.info("isWindows         : " + isWindows);
			if (DEBUG)
				log.info("pdfDirectory      : " + pdfDir);
			if (DEBUG)
				log.info("printer           : " + printer);

			// ---------------------------------------------------------
			// Creating the output directory if needed
			// Format the file names
			// Output the pdf files
			// ---------------------------------------------------------

			if (DEBUG)
				log.info("Creating directory...");


			String exportFormat = ConstantUtil.Report_JO_Export_Format;

			if (exportFormat.equals("pdf")) {
				this.createDirectory(pdfDir);
			} 

			// TuanTA10 start at 20/08/2007
			// this.createDirectory(htmlDir); removed by TuanTA10
			// this.createDirectory(xlsDir); removed by TuanTA10
			// TuanTA10 end at 20/08/2007
			// TuanTA10 end at 24/09/2007
			if (DEBUG)
				log.info("Formatting file name");
			String s = this.generateFileName(rvo);
			if (DEBUG)
				log.info("S : " + s);
			String s1 = CommonUtility.replaceString(s, "${" + ReportValueObject.PARAM_EXTENSION + "}", "pdf", true);
			// Added by MC Consulting for E-Invoice enhancements
			s1 = getPdfName(s1, rvo);
			// End of addition by MC Consulting for E-Invoice enhancements
			String s2 = CommonUtility.replaceString(s, "${" + ReportValueObject.PARAM_EXTENSION + "}", "ps", true);

			// TuanTA10 start at 20/08/2007
			String s3 = CommonUtility.replaceString(s, "${" + ReportValueObject.PARAM_EXTENSION + "}", "html", true);
			String s4 = CommonUtility.replaceString(s, "${" + ReportValueObject.PARAM_EXTENSION + "}", "xls", true);
			// TuanTA10 end at 20/08/2007

			if (DEBUG)
				log.info("S1: " + s1);
			if (DEBUG)
				log.info("S2: " + s2);
			if (DEBUG)
				log.info("S3: " + s3);
			if (DEBUG)
				log.info("S4: " + s4);

			String pdfFile = pdfDir + s1;
			//String psFile = psDir + s2;

			// TuanTA10 start at 20/08/2007
			boolean isPDF = false;
			//String htmlFile = htmlDir + s3;
			//String xlsFile = xlsDir + s4;

			if (rvo != null && rvo.getReportFileName().indexOf(".jrxml") != -1) {
				// String exportFormat = prop.getProperty("Report.JO.Export.Format");
				if ((exportFormat != null) && (exportFormat.equals("pdf"))) {
					isPDF = true;
				}

			}
			// ---------------------------------------------------------
			// Spooling to printer
			// ---------------------------------------------------------

			// TuanTA10 add at 20/08/2007
			if (isPDF) {
				savePdfName(s1, rvo);
				return pdfFile;
			}
			// TuanTA10 end at 20/08/2007

		} catch (NullPointerException e) {
			log.info("Exception getPdfFileName : ", e);

		} catch (Exception ex) {
			log.info("Exception getPdfFileName : ", ex);

		} finally {
			log.info("END: getPdfFileName  DAO  END");
		}
		return rvo.getReportFileName();

	}

	

	public List<DNCntrJasperReport> getDnCntrJasperContent(String dnNbr) {
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getDnCntrJasperContent  DAO dnNbr:" + dnNbr);

			sb.append(" SELECT TO_CHAR(WEBDNUATEMP.DATETIME, 'DD/MM/YYYY HH24:MI') DATETIME, ");
			sb.append(" TO_CHAR(WEBDNUATEMP.ATB, 'DD/MM/YYYY HH24:MI') ATB, ");
			sb.append(" TO_CHAR(WEBDNUATEMP.COD, 'DD/MM/YYYY HH24:MI') COD, WEBDNUATEMP.TRANSREFNO,");
			sb.append(" TO_CHAR(WEBDNUATEMP.STUFF_DTTM, 'DD/MM/YYYY HH24:MI') STUFF_DTTM, ");
			sb.append(" WEBDNUATEMP.VSLNM, WEBDNUATEMP.VOYNO, WEBDNUATEMP.TRANSTYPE, WEBDNUATEMP.CONTSIZE,");
			sb.append(" WEBDNUATEMP.CONTTYPE,WEBDNUATEMP.ASNNO,WEBDNUATEMP.CRGREF,WEBDNUATEMP.WT,WEBDNUATEMP.VOL,");
			sb.append(" WEBDNUATEMP.DECLQTY,WEBDNUATEMP.TRANSQTY,WEBDNUATEMP.BALQTY,WEBDNUATEMP.NRICPASSPORTNO,");
			sb.append(" WEBDNUATEMP.VEH1,WEBDNUATEMP.VEH2,WEBDNUATEMP.VEH3,WEBDNUATEMP.VEH4,WEBDNUATEMP.VEH5,");
			sb.append(" WEBDNUATEMP.MARKING,WEBDNUATEMP.CRG_DESC,VESSELSCHEME.AB_CD,SST_BILL.TARRIF_CD_SER_CHRG,");
			sb.append(" SST_BILL.TARRIF_DESC_SER_CHRG,SST_BILL.BILLABLE_TON_SER_CHRG,SST_BILL.UNIT_RATE_SER_CHRG,");
			sb.append(" SST_BILL.TOTAL_AMT_SER_CHRG,SST_BILL.TOTAL_AMT_WHARF_CHRG,SST_BILL.TOTAL_AMT_STORE_CHRG,");
			sb.append(" SST_BILL.TOTAL_AMT_SER_WHARF_CHRG,SST_BILL.TOTAL_AMT_SR_CHRG,SST_BILL.TARRIF_CD_WHARF_CHRG,");
			sb.append(
					" SST_BILL.TARRIF_DESC_WHARF_CHRG,SST_BILL.BILLABLE_TON_WHARF_CHRG,SST_BILL.UNIT_RATE_WHARF_CHRG,");
			sb.append(
					" SST_BILL.TARRIF_CD_STORE_CHRG,SST_BILL.TARRIF_DESC_STORE_CHRG,SST_BILL.BILLABLE_TON_STORE_CHRG,");
			sb.append(
					" SST_BILL.UNIT_RATE_STORE_CHRG,SST_BILL.TARRIF_CD_SR_CHRG, SST_BILL.TARRIF_DESC_SR_CHRG,SST_BILL.BILLABLE_TON_SR_CHRG,");
			sb.append(
					" SST_BILL.UNIT_RATE_SR_CHRG, SST_BILL.UNIT_RATE_SER_WHARF_CHRG,SST_BILL.BILLABLE_TON_SER_WHARF_CHRG,");
			sb.append(" SST_BILL.TARRIF_DESC_SER_WHARF_CHRG,SST_BILL.TARRIF_CD_SER_WHARF_CHRG,SST_BILL.TIME_UNIT_SER,");
			sb.append(
					" SST_BILL.TIME_UNIT_WHF,SST_BILL.TIME_UNIT_SR,SST_BILL.TIME_UNIT_SER_WHF,SST_BILL.TIME_UNIT_STORE,");
			sb.append(" WEBDNUATEMP.CNTR_NBR,SST_BILL.ACCT_NBR_SER_CHRG,SST_BILL.EDO_ACCT_NBR");
			sb.append(" FROM GBMS.WEBDNUATEMP WEBDNUATEMP, GBMS.VESSELSCHEME VESSELSCHEME, GBMS.SST_BILL SST_BILL");
			sb.append(
					" WHERE (WEBDNUATEMP.VV_CD = VESSELSCHEME.VV_CD) AND (WEBDNUATEMP.TRANSREFNO = SST_BILL.DN_UA_NBR)");
			sb.append(" AND WEBDNUATEMP.TRANSREFNO = :dnNbr ORDER BY WEBDNUATEMP.TRANSREFNO");

			paramMap.put("dnNbr", dnNbr);

			log.info(" ***listRecords SQL *****" + sb.toString() + " paramMap " + paramMap);
			return (List<DNCntrJasperReport>) namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<DNCntrJasperReport>(DNCntrJasperReport.class));
		} catch (Exception e) {
			log.info("Exception getDnCntrJasperContent : ", e);
			return null;
		} finally {
			log.info("END: getDnCntrJasperContent  DAO ");
		}

	}

	

	private String generateFileName(ReportValueObject rvo) {
		String retVal = null;
		try {
			log.info("START: generateFileName  DAO  Start Obj rvo:" + rvo);
			Random rnd = new Random();
			String fileFormat = ConstantUtil.ReportPrintingBean_fileFormat;
			String dateFormat = ConstantUtil.ReportPrintingBean_dateFormat;
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

			Date date = new Date(System.currentTimeMillis());
			String sDate = sdf.format(date);
			String sRandom = "" + rnd.nextInt(100000);
			String sReport = rvo.getReportFileName().replace(".jrxml", "");
			String sPrinterName = rvo.getPrinterName();
			String sPageSize = rvo.getReportPageSize();
			String sOther = rvo.getFilenameAppend();
			String sOrientation = null;
			if (rvo.getPrintOrientation() != null
					&& rvo.getPrintOrientation().equals(ReportValueObject.ORIENTATION_LANDSCAPE)) {
				sOrientation = "L";
			} else {
				sOrientation = "P";
			}
			String pattern[] = { "${" + ReportValueObject.PARAM_DATE + "}", "${" + ReportValueObject.PARAM_RANDOM + "}",
					"${" + ReportValueObject.PARAM_REPORT + "}", "${" + ReportValueObject.PARAM_PRINTER + "}",
					"${" + ReportValueObject.PARAM_PAGE_SIZE + "}", "${" + ReportValueObject.PARAM_OTHER + "}",
					"${" + ReportValueObject.PARAM_PAGE_ORIENTATION + "}" };
			String value[] = { sDate, sRandom, sReport, sPrinterName, sPageSize, sOther, sOrientation };
			retVal = CommonUtility.replaceString(fileFormat, pattern, value, true);
			
			log.info("END: *** generateFileName Result *****" + CommonUtility.deNull(retVal));
		} catch (Exception e) {
			log.info("Exception generateFileName : ", e);
		} finally {
			log.info("END: generateFileName  DAO  END");
		}
		return retVal;
	}

	private void createDirectory(String dir) {
		try {
			log.info("START: createDirectory  DAO  Start Obj dir:" + dir);
			if (DEBUG)
				log.info("Dir: " + dir);
			if (dir != null) {
				// check if directory exist
				File f = new File(dir);
				boolean dirOK = f.isDirectory();
				// if (!dirOK && !f.mkdir()){ TuanTA10 replace by new code line - at 17/08/2007
				if (!dirOK && !f.mkdirs()) {
					if (INFO)
						log.info("Create directory failed.");
				}
			}
		} catch (Exception e) {
			log.info("Exception createDirectory : ", e);
		} finally {
			log.info("END: createDirectory  DAO  END");
		}
	}


	private String getPdfName(String generatedReportFileName, ReportValueObject rvo) {
		String pdfName = generatedReportFileName;
		try {
			log.info("START: getPdfName  DAO  Start Obj generatedReportFileName:" + generatedReportFileName + ",rvo:"
					+ rvo);
			if (rvo.getOutputDirectory() == null) {
				return pdfName;
			}
			String billNbr = rvo.getFilenameAppend();
			if (billNbr.length() < 1 || generatedReportFileName.length() < 1) {
				return pdfName;
			}
			boolean isTaxInvoice = rvo.isTaxInvoice(), isSupportDocument = rvo.isSupportDocument(),
					isDnCn = rvo.isDnCn();
			if (isTaxInvoice || isDnCn) {
				pdfName = billNbr.split("-")[0] + ".pdf";
			} else if (isSupportDocument) {
				pdfName = billNbr.split("-")[0] + "-SupportDoc.pdf";
			}
			log.info("END: *** getPdfName Result *****" + CommonUtility.deNull(pdfName));
		} catch (Exception e) {
			log.info("Exception getPdfName : ", e);
		} finally {
			log.info("END: getPdfName  DAO  END");
		}
		return pdfName;
	}

	private void savePdfName(String generatedReportFileName, ReportValueObject rvo) {
		if (rvo.getOutputDirectory() == null) {
			return;
		}
		String billNbr = rvo.getFilenameAppend();
		if (billNbr.length() < 1 || generatedReportFileName.length() < 1) {
			return;
		}
		boolean isTaxInvoice = rvo.isTaxInvoice(), isSupportDocument = rvo.isSupportDocument(), isDnCn = rvo.isDnCn();
		if (!isTaxInvoice && !isSupportDocument && !isDnCn) {
			return;
		}
		try {
			log.info("START: savePdfName  DAO  Start Obj generatedReportFileName:" + generatedReportFileName + ",rvo:"
					+ rvo);

			savePdfName(billNbr.split("-")[0], isTaxInvoice, isSupportDocument, isDnCn, generatedReportFileName,
					rvo.getParam("INVOICE_FORMAT"), rvo.getParam("INVOICE_EMAIL"));

		} catch (Exception e) {
			log.info("Exception savePdfName : ", e);
		} finally {
			log.info("END: savePdfName  DAO  END");
		}
	}

	// /ejb.sessionBeans.cab.billing -->BillMainEJB -->savePdfName
	public void savePdfName(String billNbr, boolean isTaxInvoice, boolean isSupportDocument, boolean isDnCn,
			String generatedReportFileName, String invoiceFormat, String invoiceEmail) throws BusinessException {
		if (DEBUG) {
			log.info("---Save Pdf Name---");
			log.info("billNbr                 : " + billNbr);
			log.info("isTaxInvoice            : " + isTaxInvoice);
			log.info("isSupportDocument       : " + isSupportDocument);
			log.info("isDnCn                  : " + isDnCn);
			log.info("generatedReportFileName : " + generatedReportFileName);
			log.info("invoiceFormat           : " + invoiceFormat);
			log.info("invoiceEmail            : " + invoiceEmail);
		}
		if (billNbr == null || generatedReportFileName == null) {
			return;
		}
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: savePdfName  DAO  Start Obj ");

			String sql = "";
			if (isTaxInvoice) {
				sql = "UPDATE bill SET print_dttm = SYSDATE, invoice_pdf =:generatedReportFileName, invoice_format =:invoiceFormat, invoice_email =:invoiceEmail WHERE bill_nbr =:billNbr ";
			}
			if (isSupportDocument) {
				sql = "UPDATE bill SET support_doc_pdf =:generatedReportFileName WHERE bill_nbr =:billNbr ";
			}
			if (isDnCn) {
				sql = "UPDATE dn_cn SET print_dttm = SYSDATE, invoice_pdf =:generatedReportFileName, invoice_format =:invoiceFormat, invoice_email =:invoiceEmail WHERE dn_cn_nbr =:billNbr ";
			}
			paramMap.put("generatedReportFileName", generatedReportFileName);
			log.info(" *** savePdfName SQL *****" + sql + param + paramMap);
			
			if (isSupportDocument) {
				paramMap.put("billNbr", billNbr);
			} else {
				paramMap.put("invoiceFormat", invoiceFormat);
				paramMap.put("invoiceEmail", invoiceEmail);
				paramMap.put("billNbr", billNbr);
			}
			namedParameterJdbcTemplate.update(sql, paramMap);
			log.info("END: *** savePdfName Result *****");
		} catch (NullPointerException e) {
			log.info("Exception savePdfName : ", e);
			throw new BusinessException("M4201");
		} catch (Exception ex) {
			log.info("Exception savePdfName : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: savePdfName  DAO  END");
		}
	}

	@Override
	public String insertTempBill(String dnnbr, String tarcdser, String tardescser, double billtonsser, double urateser,
			double totchrgamtser, String actnbrser, String tarcdwf, String tardescwf, double billtonswf, double uratewf,
			double totchrgamtwf, String actnbrwf, String tarcdsr, String tardescsr, double billtonssr, double uratesr,
			double totchrgamtsr, String actnbrsr, String UserID, String edo_act_nbr, String tarcdsr1, String tardescsr1,
			double billtonssr1, double uratesr1, double totchrgamtsr1, String actnbrsr1, String tarcdsr2,
			String tardescsr2, double billtonssr2, double uratesr2, double totchrgamtsr2, String actnbrsr2,
			double tunitser, double tunitwhf, double tunitsr, double tunitstore, double tunitserwhf)
			throws BusinessException {
		String sql = "";
		int countua = 0;
		StringBuffer sb = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: insertTempBill  DAO  Start Obj ");

			sb.append("insert into sst_bill(DN_UA_NBR,TARRIF_CD_SER_CHRG,TARRIF_DESC_SER_CHRG,BILLABLE_TON_SER_CHRG,");
			sb.append("UNIT_RATE_SER_CHRG,TOTAL_AMT_SER_CHRG,TARRIF_CD_WHARF_CHRG,TARRIF_DESC_WHARF_CHRG,");
			sb.append("BILLABLE_TON_WHARF_CHRG,UNIT_RATE_WHARF_CHRG,TOTAL_AMT_WHARF_CHRG,TARRIF_CD_STORE_CHRG,");
			sb.append("TARRIF_DESC_STORE_CHRG,BILLABLE_TON_STORE_CHRG,UNIT_RATE_STORE_CHRG,TOTAL_AMT_STORE_CHRG,");
			sb.append("ACCT_NBR_SER_CHRG,ACCT_NBR_WHARF_CHRG,ACCT_NBR_STORE_CHRG,PRINT_IND,LAST_MODIFY_USER_ID,");
			sb.append("LAST_MODIFY_DTTM,EDO_ACCT_NBR,TARRIF_CD_SR_CHRG,TARRIF_DESC_SR_CHRG,BILLABLE_TON_SR_CHRG,");
			sb.append(
					"UNIT_RATE_SR_CHRG,TOTAL_AMT_SR_CHRG,ACCT_NBR_SR_WHARF_CHRG,TARRIF_CD_SER_WHARF_CHRG,TARRIF_DESC_SER_WHARF_CHRG,");
			sb.append(
					"BILLABLE_TON_SER_WHARF_CHRG,UNIT_RATE_SER_WHARF_CHRG,TOTAL_AMT_SER_WHARF_CHRG,ACCT_NBR_SER_WHARF_CHRG,");
			sb.append("TIME_UNIT_SER,TIME_UNIT_WHF,TIME_UNIT_SR,TIME_UNIT_SER_WHF,TIME_UNIT_STORE) ");
			sb.append("values(:dnnbr, :tarcdser, :tardescser,:billtonsser,");
			sb.append(":urateser, :totchrgamtser, :tarcdwf, :tardescwf,");
			sb.append(":billtonswf,:uratewf,:totchrgamtwf,:tarcdsr,");
			sb.append(":tardescsr ,:billtonssr,:uratesr,:totchrgamtsr,");
			sb.append(":actnbrser,:actnbrwf,:actnbrsr,'WEB',:UserID,sysdate,:edo_act_nbr,:tarcdsr1,");
			sb.append(":tardescsr1,:billtonssr1 ,:uratesr1,:totchrgamtsr1,:actnbrsr1,:tarcdsr2,");
			sb.append(":tardescsr2 ,:billtonssr2,:uratesr2 ,:totchrgamtsr2,:actnbrsr2,");
			sb.append(":tunitser,:tunitwhf ,:tunitsr,:tunitstore,:tunitserwhf)");

			sql = sb.toString();

			paramMap.put("dnnbr", dnnbr);
			paramMap.put("tarcdser", tarcdser);
			paramMap.put("tardescser", tardescser);
			paramMap.put("billtonsser", billtonsser);
			paramMap.put("urateser", urateser);
			paramMap.put("totchrgamtser", totchrgamtser);
			paramMap.put("tarcdwf", tarcdwf);
			paramMap.put("tardescwf", tardescwf);
			paramMap.put("billtonswf", billtonswf);
			paramMap.put("uratewf", uratewf);
			paramMap.put("totchrgamtwf", totchrgamtwf);
			paramMap.put("tarcdsr", tarcdsr);
			paramMap.put("tardescsr", tardescsr);
			paramMap.put("billtonssr", billtonssr);
			paramMap.put("uratesr", uratesr);
			paramMap.put("totchrgamtsr", totchrgamtsr);
			paramMap.put("actnbrser", actnbrser);
			paramMap.put("actnbrwf", actnbrwf);
			paramMap.put("actnbrsr", actnbrsr);
			paramMap.put("UserID", UserID);
			paramMap.put("edo_act_nbr", edo_act_nbr);
			paramMap.put("tarcdsr1", tarcdsr1);
			paramMap.put("tardescsr1", tardescsr1);
			paramMap.put("billtonssr1", billtonssr1);
			paramMap.put("uratesr1", uratesr1);
			paramMap.put("totchrgamtsr1", totchrgamtsr1);
			paramMap.put("actnbrsr1", actnbrsr1);
			paramMap.put("tarcdsr2", tarcdsr2);
			paramMap.put("tardescsr2", tardescsr2);
			paramMap.put("billtonssr2", billtonssr2);
			paramMap.put("uratesr2", uratesr2);
			paramMap.put("totchrgamtsr2", totchrgamtsr2);
			paramMap.put("actnbrsr2", actnbrsr2);
			paramMap.put("tunitser", tunitser);
			paramMap.put("tunitwhf", tunitwhf);
			paramMap.put("tunitsr", tunitsr);
			paramMap.put("tunitstore", tunitstore);
			paramMap.put("tunitserwhf", tunitserwhf);

			log.info(" *** insertTempBill SQL *****" + sql + param + paramMap);
			countua = namedParameterJdbcTemplate.update(sql, paramMap);

			log.info("countsstbillrecs ---------------->" + countua);

			if (countua == 0) {
				throw new BusinessException("M4201");
			}
			log.info("END: *** insertTempBill Result *****" + countua);
		} catch (NullPointerException e) {
			log.info("Exception insertTempBill : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception insertTempBill : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertTempBill  DAO  END");
		}
		return "" + countua;
	}

	@Override
	public String insertTempDNPrintOut(String DNNbr, String transtype, String searchcrg, String esnasnnbr)
			throws BusinessException {
		SqlRowSet rs = null;
		String sql = "";
		int countua = 0;
		List<EdoValueObjectContainerised> temptablevect = new ArrayList<EdoValueObjectContainerised>();
		List<EdoValueObjectContainerised> temptablevect_vech = new ArrayList<EdoValueObjectContainerised>();
		Map<String, String> paramMap = new HashMap<String, String>();
		String esnno = "";
		String vslnm = "";
		String atb = "";
		String outvoy = "";
		String bkref = "";
		String dcpkgs = "";
		String wt = "";
		String vol = "";
		String conttype = "";
		String contno = "";
		String crgdesc = "";
		String markings = "";
		String contsize = "";
		String cod = "";
		String balpkgs = "";
		// String act_nbr = "";
		String date_time = "";
		String transQty = "";
		// String nric_no = "";
		String ictype = "";
		// String dpname = "";
		String veh1 = "";
		String veh2 = "";
		String veh3 = "";
		String veh4 = "";
		String veh5 = "";
		String ttype = "";
		String dateval = "";
		// String etb = "";
		// String btr = "";
		String vv_cd = "";
		String strCntrNum = "";
		String strStuffDt = "";
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: insertTempDNPrintOut  DAO  Start Obj " + " DNNbr:" + DNNbr + " transtype:" + transtype
					+ " searchcrg:" + searchcrg + " esnasnnbr:" + esnasnnbr);

			temptablevect = fetchDNDetail(DNNbr, transtype, searchcrg, esnasnnbr);
			temptablevect_vech = getVechDetails(DNNbr);
			for (int j = 0; j < temptablevect_vech.size(); j++) {
				EdoValueObjectContainerised edoVo = new EdoValueObjectContainerised();
				edoVo = (EdoValueObjectContainerised) temptablevect_vech.get(j);
				veh1 = edoVo.getVech1();
				veh2 = edoVo.getVech2();
				veh3 = edoVo.getVech3();
				veh4 = edoVo.getVech4();
				veh5 = edoVo.getVech5();
			}

			for (int i = 0; i < temptablevect.size(); i++) {
				EdoValueObjectContainerised edoVo = new EdoValueObjectContainerised();
				edoVo = (EdoValueObjectContainerised) temptablevect.get(i);
				esnno = edoVo.getEdoAsnNbr();
				vslnm = edoVo.getVslName();
				outvoy = edoVo.getInVoyNbr();
				contno = edoVo.getCntrNo();
				conttype = edoVo.getCntrType();
				contsize = edoVo.getCntrSize();
				bkref = edoVo.getBlNbr();
				dcpkgs = edoVo.getNoPkgs();
				wt = edoVo.getNomWt();
				vol = edoVo.getNomVol();
				crgdesc = edoVo.getCrgDes();
				markings = edoVo.getMarkings();
				balpkgs = edoVo.getBalance();
				cod = edoVo.getCOD();
				// act_nbr = edoVo.getAcctNo();
				date_time = edoVo.getTransDate();
				transQty = edoVo.getDeliveredPkgs();
				// nric_no = edoVo.getAAIcNbr();
				ictype = edoVo.getAACustCD();
				// dpname = edoVo.getAAName();
				ttype = edoVo.getTransType();
				atb = edoVo.getATB();
				strCntrNum = edoVo.getCntrNbr();
				strStuffDt = edoVo.getStuffDate();
				dateval = atb;
				if (ttype.equals("L"))
					ttype = "L";
				else
					ttype = "T";

				String tempCod = cod;
				if (cod != null && !cod.equals("") && !cod.equals("null"))
					cod = tempCod;
				else
					cod = "";

				// for vv_cd
				String sql_vvcd = "select var_nbr from gb_edo where edo_asn_nbr =:esnno";
				paramMap.put("esnno", esnno);
				log.info(" *** insertTempDNPrintOut SQL *****" + sql_vvcd);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql_vvcd, paramMap);
				if (rs.next())
					vv_cd = rs.getString("var_nbr");

				sb.append("Insert into webdnuatemp(DateTime,TransRefno,ATB,COD,Vslnm,voyno,contno,transtype,contsize,");
				sb.append("conttype,asnno,crgref,wt,vol,declqty,transqty,balqty,nricpassportno,marking,crg_desc,");
				sb.append("veh1,veh2,veh3,veh4,veh5,vv_cd,cntr_nbr,stuff_dttm) values");
				sb.append("(to_date(:date_time,'DD/MM/YYYY HH24:MI'),:DNNbr,to_date(:dateval,'DD/MM/YYYY HH24:MI'),");
				sb.append("to_date(:cod,'DD/MM/YYYY HH24:MI'),:vslnm,:outvoy,:contno,:ttype,:contsize,");
				sb.append(":conttype,:esnno,:bkref,:wt,:vol,:dcpkgs,:transQty,:balpkgs,:ictype,:markings,:crgdesc,");
				sb.append(
						":veh1,:veh2,:veh3,:veh4,:veh5,:vv_cd,:strCntrNum,to_date(:strStuffDt,'DD/MM/YYYY HH24:MI'))");
				sql = sb.toString();
				
				
				paramMap.put("date_time", date_time);
				paramMap.put("DNNbr", DNNbr);
				paramMap.put("dateval", dateval);
				paramMap.put("cod", cod);
				paramMap.put("vslnm", addApostr(vslnm));
				paramMap.put("outvoy", addApostr(outvoy));
				paramMap.put("contno", addApostr(contno));
				paramMap.put("ttype", ttype);
				paramMap.put("contsize", contsize);
				paramMap.put("conttype", conttype);
				paramMap.put("esnno", esnno);
				paramMap.put("bkref", addApostr(bkref));
				paramMap.put("wt", wt);
				paramMap.put("vol", vol);
				paramMap.put("dcpkgs", dcpkgs);
				paramMap.put("transQty", transQty);
				paramMap.put("balpkgs", balpkgs);
				paramMap.put("ictype", ictype);
				paramMap.put("markings", addApostr(markings));
				paramMap.put("crgdesc", addApostr(crgdesc));
				paramMap.put("veh1", addApostr(veh1));
				paramMap.put("veh2", addApostr(veh2));
				paramMap.put("veh3", addApostr(veh3));
				paramMap.put("veh4", addApostr(veh4));
				paramMap.put("veh5", addApostr(veh5));
				paramMap.put("vv_cd", vv_cd);
				paramMap.put("strCntrNum", strCntrNum);
				paramMap.put("strStuffDt", strStuffDt);
				
				log.info(" *** insertTempDNPrintOut SQL *****" + sql + param + paramMap);

				countua = countua + namedParameterJdbcTemplate.update(sql, paramMap);
			} // end for
			log.info("countuarecs ---------------->" + countua);

			if (countua == 0) {
				throw new BusinessException("M4201");
			}
			log.info("END: *** insertTempDNPrintOut Result *****" + countua);
		} catch (BusinessException e) {
			log.info("Exception insertTempDNPrintOut :", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception insertTempDNPrintOut :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception insertTempDNPrintOut :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertTempDNPrintOut  DAO  END");
		}
		return "" + countua;

	}

	public static String addApostr(String s) {
		if (s != null && !s.equals("")) {
			String s1 = new String(s);
			StringBuffer stringbuffer;
			for (int i = 0; i < s1.length(); s1 = stringbuffer.toString()) {
				stringbuffer = new StringBuffer(s1);
				if (s1.charAt(i) == '\'') {
					stringbuffer.insert(i, "'");
					i += 2;
				} else {
					i++;
				}
			}
			return s1;
		} else {
			return "";
		}
	}

	@Override
	public void purgetemptableDN(String dnnbr) throws BusinessException {
		String sql = "";
		String sql1 = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: purgetemptableDN  DAO  Start Obj " + " dnnbr:" + dnnbr);

			sql = "delete from webdnuatemp where TransRefno =:dnnbr ";
			sql1 = "delete from sst_bill where print_ind = 'WEB' and dn_ua_nbr =:dnnbr ";

			paramMap.put("dnnbr", dnnbr);

			log.info(" *** purgetemptableDN SQL *****" + sql + param + paramMap);

			namedParameterJdbcTemplate.update(sql, paramMap);

			log.info(" *** purgetemptableDN SQL *****" + sql1);
			namedParameterJdbcTemplate.update(sql1, paramMap);

			log.info("END: *** purgetemptableDN Result *****");
		} catch (NullPointerException e) {
			log.info("Exception purgetemptableDN : ", e);
			throw new BusinessException("M4201");
		} catch (Exception ex) {
			log.info("Exception purgetemptableDN : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: purgetemptableDN  DAO  END");
		}
	}

	@Override
	public List<EdoValueObjectContainerised> fetchDNCreateDetail(String edoNbr, String transType, String searchcrg,
			String tesn_nbr) throws BusinessException {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		List<EdoValueObjectContainerised> BJDetailsVect = new ArrayList<EdoValueObjectContainerised>();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		log.info("Writing from DnBeanBean.fetchDNCreateDetail");
		try {
			log.info("START: fetchDNCreateDetail  DAO  Start Obj " + " edoNbr:" + edoNbr + " transType:" + transType
					+ " searchcrg:" + searchcrg + " tesn_nbr:" + tesn_nbr);

			boolean chkEdostatus = chkEdoStatus(edoNbr);
			if (!chkEdostatus) {
				throw new BusinessException("M80001");
			}

			// boolean chkGbj = chkGBJ(edoNbr);
			/*
			 * if (chkGbj) { log.info("Writing from DnBeanBean.fetchDNCreateDetail");
			 * log.info("BJ has been Closed.  No DN can be created"); throw new
			 * BusinessException("M80002"); }
			 */

			boolean chkVvstatus = chkVVStatus(edoNbr);
			if (!chkVvstatus) {
				log.info("Vessel has not Berthed yet.  DN cannot be printed");
				throw new BusinessException("M80003");
			}

			/*
			 * boolean chkCargorls = chkCargoRls(edoNbr); if (!chkCargorls) {
			 * log.info("Writing from DnBeanBean.fetchDNCreateDetail");
			 * log.info("Cargo needs to be release before delivery can take place"); throw
			 * new BusinessException("M80004"); }
			 */

			/*
			 * boolean chkCargostatus = chkCargoStatus(edoNbr); if (!chkCargostatus) {
			 * log.info("Writing from DnBeanBean.fetchDNCreateDetail");
			 * log.info("No more Authorized Cargo for Delivery"); throw new
			 * BusinessException("M80005"); }
			 */
			if (transType.equals("L")) {
				sb.append("select to_char(sysdate,'dd/mm/yyyy hh24:mi') as trans_dttm,vessel_call.vsl_nm ,");
				sb.append("vessel_call.in_voy_nbr,to_char(berthing.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb,");
				sb.append("to_char(berthing.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,manifest_details.CRG_DES,");
				sb.append("manifest_details.CNTR_SIZE , manifest_details.CNTR_TYPE ,");
				sb.append("mft_markings.mft_markings,gb_edo.nbr_pkgs as edoNbr,");
				sb.append(
						"gb_edo.nbr_pkgs - gb_edo.trans_nbr_pkgs - gb_edo.release_nbr_pkgs - gb_edo.dn_nbr_pkgs as balance,");
				sb.append("gb_edo.dn_nbr_pkgs,gb_edo.nom_wt,gb_edo.NOM_VOL,gb_edo.acct_nbr, gb_edo.EDO_ASN_NBR,");
				sb.append("bl_cntr_details.CNTR_NBR,gb_edo.bl_nbr,gb_edo.CRG_STATUS as TRANS_TYPE from gb_edo, ");
				sb.append("vessel_call,berthing, bl_cntr_details, manifest_details, mft_markings ");
				sb.append("where gb_edo.var_nbr = vessel_call.vv_cd and vessel_call.vv_cd = berthing.vv_cd ");
				sb.append("and manifest_details.MFT_SEQ_NBR = mft_markings.MFT_SQ_NBR and SHIFT_IND = 1 and ");
				sb.append("gb_edo.mft_seq_nbr = manifest_details.mft_seq_nbr ");
				sb.append(
						"AND manifest_details.mft_seq_nbr = bl_cntr_details.mft_seq_nbr(+) and  gb_edo.EDO_ASN_NBR = :edoNbr ");
				sb.append(" and gb_edo.edo_status='A'");
				sql = sb.toString();
			} else {
				sb.append("select to_char(sysdate,'dd/mm/yyyy hh24:mi') as trans_dttm,vessel_call.vsl_nm , ");
				sb.append("vessel_call.in_voy_nbr,to_char(berthing.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb,");
				sb.append("to_char(berthing.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,manifest_details.CRG_DES,");
				sb.append(
						"manifest_details.CNTR_SIZE,manifest_details.CNTR_TYPE,mft_markings.mft_markings,gb_edo.nbr_pkgs as edoNbr,");
				sb.append("gb_edo.trans_nbr_pkgs - gb_edo.release_nbr_pkgs - gb_edo.trans_dn_nbr_pkgs as balance,");
				sb.append("gb_edo.dn_nbr_pkgs ,gb_edo.nom_wt ,gb_edo.NOM_VOL ,gb_edo.acct_nbr , gb_edo.EDO_ASN_NBR, ");
				sb.append("bl_cntr_details.CNTR_NBR,gb_edo.bl_nbr,gb_edo.CRG_STATUS as TRANS_TYPE from gb_edo, ");
				sb.append("vessel_call,berthing,bl_cntr_details ,manifest_details ,  mft_markings where ");
				sb.append("gb_edo.var_nbr =  vessel_call.vv_cd and vessel_call.vv_cd = berthing.vv_cd  and ");
				sb.append("manifest_details.MFT_SEQ_NBR = mft_markings.MFT_SQ_NBR and SHIFT_IND = 1 and ");
				sb.append("gb_edo.mft_seq_nbr = manifest_details.mft_seq_nbr AND ");
				sb.append(" manifest_details.mft_seq_nbr = bl_cntr_details.mft_seq_nbr(+) and ");
				sb.append(" gb_edo.EDO_ASN_NBR =:edoNbr and gb_edo.edo_status='A'");
				sql = sb.toString();
			}

			paramMap.put("edoNbr", edoNbr);
			log.info(" *** fetchDNCreateDetail SQL *****" + sql + param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
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
			// String TransType = "";
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
				// TransType = rs.getString("TRANS_TYPE");

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
				edoVo.setMarkings(marking);
				edoVo.setCrgDes(crgDesc);
				edoVo.setAcctNo(accNo);
				edoVo.setCntrNo(cntrNo);
				edoVo.setCntrSize(cntrSize);
				edoVo.setCntrType(cntType);
				edoVo.setEdoAsnNbr(edoAsnNbr);
				edoVo.setBalance(balance);
				edoVo.setTransType(transType);
				BJDetailsVect.add(edoVo);
			}
			log.info("END: *** fetchDNCreateDetail Result *****" + BJDetailsVect.toString());
		} catch (BusinessException e) {
			log.info("Exception fetchDNCreateDetail : ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception fetchDNCreateDetail : ", e);
			throw new BusinessException("M4201");
		} catch (Exception ex) {
			log.info("Exception fetchDNCreateDetail : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchDNCreateDetail  DAO  END");
		}
		return BJDetailsVect;

	}

	private boolean chkVVStatus(String esnNbrR) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		boolean bblno = false;

		try {
			log.info("START: chkVVStatus  DAO  Start Obj " + " esnNbrR:" + esnNbrR);

			sb.append(
					"SELECT VC.VV_STATUS_IND FROM VESSEL_CALL VC,GB_EDO GB WHERE VC.VV_STATUS_IND IN('UB','BR','CL') ");
			sb.append("AND VC.VV_CD = GB.VAR_NBR AND GB.EDO_ASN_NBR =:esnNbrR ");

			paramMap.put("esnNbrR", esnNbrR);
			log.info(" *** chkVVStatus SQL *****" + sb.toString() + param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				bblno = true;
			} else {
				bblno = false;
			}
			log.info("END: *** chkVVStatus Result *****" + bblno);
		} catch (NullPointerException e) {
			log.info("Exception chkVVStatus :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkVVStatus :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkVVStatus  DAO  END");

		}
		return bblno;
	}

	private boolean chkEdoStatus(String esnNbrR) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = "";
		boolean bblno = false;
		try {

			log.info("START: chkEdoStatus  DAO  Start Obj " + " esnNbrR:" + esnNbrR);

			sql = "SELECT EDO_STATUS FROM gb_edo WHERE EDO_STATUS='A' AND EDO_ASN_NBR = :esnNbrR";

			paramMap.put("esnNbrR", esnNbrR);
			log.info(" *** chkEdoStatus SQL *****" + sql + param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				bblno = true;
			} else {
				bblno = false;
			}

			log.info("END: *** chkEdoStatus Result *****" + bblno);
		} catch (NullPointerException e) {
			log.info("Exception chkEdoStatus :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkEdoStatus :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkEdoStatus  DAO  END");
		}
		return bblno;
	}

	@Override
	public List<EdoValueObjectContainerised> getVechDetails(String dnNbr) throws BusinessException {
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		List<EdoValueObjectContainerised> vechDetails = new ArrayList<EdoValueObjectContainerised>();
		EdoValueObjectContainerised edoVo = new EdoValueObjectContainerised();
		try {
			log.info("START: getVechDetails  DAO  Start Obj " + " dnNbr:" + dnNbr);
			sql = "SELECT VEH_NO,DN_VEH_SEQ FROM DN_VEH WHERE DN_NBR =:dnNbr  ORDER BY DN_VEH_SEQ";

			paramMap.put("dnNbr", dnNbr);
			log.info(" *** getVechDetails SQL *****" + sql + param + paramMap);
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
			}
			log.info("END: *** getVechDetails Result *****" + vechDetails.toString());
		} catch (NullPointerException e) {
			log.info("Exception getVechDetails :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVechDetails :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVechDetails  DAO  END");
		}
		return vechDetails;
	}

	@Override
	public List<EdoValueObjectContainerised> fetchDNDetail(String edoNbr, String status, String searchcrg, String tesn_nbr)
			throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		List<EdoValueObjectContainerised> fetchDNDetail = new ArrayList<EdoValueObjectContainerised>();

		log.info("Writing from DnBeanBean.fetchDNDetail");

		String chktesnJpJp_nbr = chktesnJpJp_nbr(tesn_nbr);
		String chktesnJpPsa_nbr = chktesnJpPsa_nbr(tesn_nbr);
		if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
			sb.append(
					"select gb_edo.edo_asn_nbr,to_char(dn_details.TRANS_DTTM,'dd/mm/yyyy hh24:mi') as trans_dttm,vessel_call.vsl_nm , vessel_call.in_voy_nbr,to_char(berthing.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb,");
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
			log.info(" *** fetchDNDetail SQL *****" + sb.toString() + param + paramMap);
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
					int tempBal4 = 0;
					if (aa_name != null && !aa_name.equals("")) {
						bal4 = bal4 + Integer.parseInt(maxQty);
						tempBal4 = bal4;
						if (relPkgsNbr <= bal4)
							bal4 = relPkgsNbr;
						else
							bal4 = tempBal4;
					}
					tempBal4 = bal4;
					if (blPkgs >= bal4)
						bal4 = tempBal4;
					else
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
						int tempBal3 = 0;
						tempBal3 = bal3;
						if (blPkgs >= bal3) {
							if (edoNbrPkgs >= bal3) {
								if (relPkgsNbr >= bal3)
									bal3 = tempBal3;
								else
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
						int tempBal3 = 0;
						tempBal3 = bal3;
						if (blPkgs >= bal3) {
							if (edoNbrPkgs >= bal3) {
								if (relPkgsNbr >= bal3)
									bal3 = tempBal3;
								else
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
			log.info("END: *** fetchDNDetail Result *****" + fetchDNDetail.toString());
		} catch (NullPointerException e) {
			log.info("Exception fetchDNDetail :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception fetchDNDetail :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO fetchDNDetail");

		}
		return fetchDNDetail;
	}

	@Override
	public List<EdoValueObjectContainerised> fetchDNList(String edoNbr, String searchcrg, String tesn_nbr)
			throws BusinessException {
		String sql = "";
		List<EdoValueObjectContainerised> BJDetailsVect = new ArrayList<EdoValueObjectContainerised>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: fetchDNList  DAO  Start Obj " + " edoNbr" + edoNbr + " searchcrg" + searchcrg + " tesn_nbr"
					+ tesn_nbr);

			// String chktesnJpJp_nbr = chktesnJpJp_nbr(tesn_nbr);
			// String chktesnJpPsa_nbr = chktesnJpPsa_nbr(tesn_nbr);
			if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
				sb.append("SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,");
				sb.append("to_char (TRANS_DTTM,'DD/MM/YYYY HH24:MI') as trnsDate");
				sb.append(" FROM DN_DETAILS WHERE EDO_ASN_NBR=:edoNbr ");
				sb.append("and tesn_asn_nbr is null");
				sql = sb.toString();
			} else {
				sb.append("SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,");
				sb.append("to_char (TRANS_DTTM,'DD/MM/YYYY HH24:MI') as trnsDate");
				sb.append(" FROM DN_DETAILS  WHERE EDO_ASN_NBR=:edoNbr ");
				sb.append(" and tesn_asn_nbr=:tesn_nbr ");
				sql = sb.toString();
			}

			paramMap.put("edoNbr", edoNbr);
			paramMap.put("tesn_nbr", tesn_nbr);
			log.info(" *** fetchDNList SQL *****" + sql + param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			String dnNbr;
			String noPkgs;
			String billTon;
			String dnStatus;
			String trans = "";
			String billStatus;
			while (rs.next()) {
				dnNbr = rs.getString("DN_NBR");
				noPkgs = rs.getString("NBR_PKGS");
				billTon = rs.getString("BILLABLE_TON");
				dnStatus = rs.getString("DN_STATUS");
				trans = rs.getString("trnsDate");
				billStatus = rs.getString("BILL_STATUS");

				EdoValueObjectContainerised edoVo = new EdoValueObjectContainerised();
				edoVo.setDnNbr(dnNbr);
				edoVo.setNoPkgs(noPkgs);
				edoVo.setBillTon(billTon);
				edoVo.setDnStatus(dnStatus);
				edoVo.setTransDate(trans);
				edoVo.setBillStatus(billStatus);
				BJDetailsVect.add(edoVo);
			}

			log.info("END: *** fetchDNList Result *****" + BJDetailsVect.toString());
		} catch (NullPointerException e) {
			log.info("Exception fetchDNList :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception fetchDNList :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchDNList  DAO  END");
		}
		return BJDetailsVect;

	}

	@Override
	public List<EdoValueObjectContainerised> fetchEdoDetails(String edoNbr, String searchcrg, String tesnnbr)
			throws BusinessException {
		String sql = "";
		String sql1 = "";
		List<EdoValueObjectContainerised> BJDetailsVect = new ArrayList<EdoValueObjectContainerised>();
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: fetchEdoDetails  DAO  Start Obj " + " edoNbr:" + edoNbr + " searchcrg:" + searchcrg
					+ " tesnnbr:" + tesnnbr);

			// sql = select * from gb_edo edo, manifest_details mft where edo.mft_seq_nbr =
			// mft.mft_seq_nbr and edo_asn_nbr='" + edoNbr + "'"
			// boolean checkjpjp = chktesnJpJp(edoNbr);
			// boolean checkjppsa = chktesnJpPsa(edoNbr);
			String chktesnJpJp_nbr = chktesnJpJp_nbr(tesnnbr);
			String chktesnJpPsa_nbr = chktesnJpPsa_nbr(tesnnbr);
			if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
				sb.append("select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,");
				sb.append(
						" d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,");
				sb.append(
						" b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,f.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,");
				sb.append(
						" a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0)+nvl(a.trans_dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,a.mft_seq_nbr as mftsqnbr, a.wh_ind, a.wh_aggr_nbr, a.wh_remarks, b.terminal, b.scheme,b.combi_gc_scheme, b.combi_gc_ops_ind from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,cc_unstuff_manifest f where ");
				sb.append(
						" a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr AND d.unstuff_seq_nbr=f.UNSTUFF_SEQ_NBR(+) and a.EDO_ASN_NBR = :edoNbr");

			} else {
				// if(checkjpjp && checkjppsa){
				if (chktesnJpJp_nbr.equals("Y")) {
					sb.append("select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,");
					sb.append(
							" d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,");
					sb.append(
							" b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,g.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,");
					sb.append(
							" a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,");
					sb.append(
							" f.NBR_PKGS as jpjpnpkg,f.DN_NBR_PKGS as jpjpdn_npkg,a.mft_seq_nbr as mftsqnbr , a.wh_ind, a.wh_aggr_nbr, a.wh_remarks, b.terminal, b.scheme,b.combi_gc_scheme, b.combi_gc_ops_ind  from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,tesn_jp_jp f, cc_unstuff_manifest g where");
					sb.append(
							" a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and d.unstuff_seq_nbr=g.UNSTUFF_SEQ_NBR(+) AND f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr = :tesnnbr and a.EDO_ASN_NBR =  :edoNbr");

				} else if (chktesnJpPsa_nbr.equals("Y")) {
					sb.append("select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,");
					sb.append(
							" d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,");
					sb.append(
							" b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,g.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,");
					sb.append(
							" a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,");
					sb.append(
							" f.NBR_PKGS as jppsanpkg,f.DN_NBR_PKGS as jppsadn_npkg,a.mft_seq_nbr as mftsqnbr, a.wh_ind, a.wh_aggr_nbr, a.wh_remarks, b.terminal, b.scheme,b.combi_gc_scheme, b.combi_gc_ops_ind  from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,tesn_jp_psa f,cc_unstuff_manifest g where");
					sb.append(
							" a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and d.unstuff_seq_nbr=g.UNSTUFF_SEQ_NBR(+) AND f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr = :tesnnbr  and a.EDO_ASN_NBR =  :edoNbr");
				}
			}
			sql = sb.toString();

			paramMap.put("edoNbr", edoNbr);
			paramMap.put("tesnnbr", tesnnbr);
			log.info(" *** fetchEdoDetails SQL *****" + sql + param + paramMap);
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

				if (payNbr.equals("A")) {
					sql1 = "SELECT C.CO_NM FROM CUST_ACCT CA,COMPANY_CODE C WHERE CA.CUST_CD=C.CO_CD AND CA.ACCT_NBR=:accNbr";
					paramMap.put("accNbr", accNbr);
					log.info(" *** fetchEdoDetails SQL *****" + sql1);
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
					if (rs1.next()) {
						billparty = rs1.getString(1);
						payNbr = accNbr;
					}

				}
				if (adpcustcd != null) {
					// Punitha - UEN Enhancement - 01/07/2009
					// ResultSet rs1 = sqlstmt1.executeQuery("SELECT CC.CO_NM,CUSTOMER.TDB_CR_NBR
					// FROM CUSTOMER, COMPANY_CODE CC WHERE CUSTOMER.CUST_CD=CC.CO_CD AND
					// CUSTOMER.CUST_CD='" + adpcustcd + "'");
					sb.setLength(0);
					sb.append("SELECT CC.CO_NM, (SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) ");
					sb.append("from customer where cust_cd =:adpcustcd) TDB_CR_NBR ");
					sb.append("FROM CUSTOMER, COMPANY_CODE CC WHERE CUST_CD=CC.CO_CD and cust_cd = :adpcustcd ");
					sql1 = sb.toString();
					paramMap.put("adpcustcd", adpcustcd);
					log.info(" *** fetchEdoDetails SQL *****" + sql1);
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
					if (rs1.next()) {
						adpNm = rs1.getString("co_nm");
						adpIcNbr = rs1.getString("TDB_CR_NBR");
					}

				}

				if (caCustcd != null) {
					// Punitha - UEN Enhancement - 01/07/2009
					// ResultSet rs1 = sqlstmt1.executeQuery("SELECT CC.CO_NM,CUSTOMER.TDB_CR_NBR
					// FROM CUSTOMER, COMPANY_CODE CC WHERE CUSTOMER.CUST_CD=CC.CO_CD AND
					// CUSTOMER.CUST_CD='" + caCustcd + "'");
					sb.setLength(0);
					sb.append("SELECT CC.CO_NM, (SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) ");
					sb.append("from customer where cust_cd =:caCustcd) TDB_CR_NBR ");
					sb.append("FROM CUSTOMER, COMPANY_CODE CC WHERE CUST_CD=CC.CO_CD and cust_cd =:caCustcd");
					sql1 = sb.toString();
					paramMap.put("caCustcd", caCustcd);
					log.info(" *** fetchEdoDetails SQL *****" + sql1);
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
					if (rs1.next()) {
						caNm = rs1.getString("co_nm");
						caIcNbr = rs1.getString("TDB_CR_NBR");
					}

				}

				if (aaCustCd != null) {
					// Punitha - UEN Enhancement - 01/07/2009
					// ResultSet rs1 = sqlstmt1.executeQuery("SELECT CC.CO_NM,CUSTOMER.TDB_CR_NBR
					// FROM CUSTOMER, COMPANY_CODE CC WHERE CUSTOMER.CUST_CD=CC.CO_CD AND
					// CUSTOMER.CUST_CD='" + aaCustCd + "'");
					sb.setLength(0);
					sb.append("SELECT CC.CO_NM, (SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) ");
					sb.append("from customer where cust_cd =:aaCustCd) TDB_CR_NBR ");
					sb.append("FROM CUSTOMER, COMPANY_CODE CC WHERE CUST_CD=CC.CO_CD and cust_cd =:aaCustCd ");
					sql1 = sb.toString();
					paramMap.put("aaCustCd", aaCustCd);
					log.info(" *** fetchEdoDetails SQL *****" + sql1);
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
					if (rs1.next()) {
						aaNm = rs1.getString("co_nm");
						aaIcNbr = rs1.getString("TDB_CR_NBR");
					}

				}
				if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {
					String bal_cutoff = rs.getString("CUT_OFF_NBR_PKGS");
					String bal_short_land = rs.getString("NBR_PKGS_IN_PORT");
					int con3 = 0;
					// int con4 = 0;
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
						int tempBal3 = bal3;
						if (blPkgs >= bal3) {
							if (edoNbrPkgs >= bal3) {
								if (relPkgsNbr >= bal3)
									bal3 = tempBal3;
								else
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
						int tempBal3 = bal3;
						if (blPkgs >= bal3) {
							if (edoNbrPkgs >= bal3) {
								if (relPkgsNbr >= bal3)
									bal3 = tempBal3;
								else
									bal3 = relPkgsNbr - con3;
							} else
								bal3 = edoNbrPkgs;
						} else
							bal3 = blPkgs;

						balance = String.valueOf(bal3);
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
					int tempBal4 = 0;
					if (aaNm != null && !aaNm.equals("")) {
						bal4 = bal4 + Integer.parseInt(maxQty);
						tempBal4 = bal4;
						if (relPkgsNbr <= bal4)
							bal4 = relPkgsNbr;
						else
							bal4 = tempBal4;
					}

					tempBal4 = bal4;
					if (blPkgs >= bal4)
						bal4 = tempBal4;
					else
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
				String tempDnPkgs = dnPkgs;
				if (dnPkgs != null && !dnPkgs.equals(""))
					dnPkgs = tempDnPkgs;
				else
					dnPkgs = "0";
				EdoValueObjectContainerised edoVo = new EdoValueObjectContainerised();
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
				BJDetailsVect.add(edoVo);
			}

			log.info("END: *** fetchEdoDetails Result *****" + BJDetailsVect.toString());
		} catch (NullPointerException e) {
			log.info("Exception fetchEdoDetails :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception fetchEdoDetails :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchEdoDetails  DAO  END");
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
			log.info("END: *** getCTDNnbr Result *****" + total);
		} catch (NullPointerException e) {
			log.info("Exception getCTDNnbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCTDNnbr :", e);
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
			log.info("END: *** getEDOCTDNnbr Result *****" + total);
		} catch (NullPointerException e) {
			log.info("Exception getEDOCTDNnbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEDOCTDNnbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getEDOCTDNnbr");

		}
		return total;
	}

	@Override
	public String cancelDN(String edoNbr, String dnNbr, String userid, String transtype, String searchcrg,
			String tesn_nbr) throws BusinessException {
		String sql = "";
		String dnsql = "";
		String edoupdsql = "";
		String tesnupdsql = "";
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		SqlRowSet rs3 = null;
		String chktesnJpJp_nbr = chktesnJpJp_nbr(tesn_nbr);
		String chktesnJpPsa_nbr = chktesnJpPsa_nbr(tesn_nbr);

		String sqlupdJp_trans = "";
		String dn_sql_trans = "";
		String sqltlog1 = "";
		String sqltlog = "";
		// int stransno = 0;
		String sqltlog2 = "";
		int stransno2 = 0;
		int stransno1 = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: cancelDN  DAO  Start Obj " + " edoNbr: " +edoNbr + " dnNbr: " +dnNbr + " userid: " + userid
					+ " transtype: " +transtype + " searchcrg: " + searchcrg+ " tesn_nbr: " +tesn_nbr);

			sqltlog2 = "SELECT MAX(TRANS_NBR) maxTransNbr FROM DN_DETAILS_TRANS WHERE dn_NBR=:dnNbr ";

			// boolean checkjpjp = chktesnJpJp(edoNbr);
			// boolean checkjppsa = chktesnJpPsa(edoNbr);
			if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {
				if (chktesnJpJp_nbr.equals("Y")) {
					tesnupdsql = "update tesn_jp_jp set dn_nbr_pkgs = dn_nbr_pkgs -";
					// lak added for Audit Trial
					sqlupdJp_trans = "insert into tesn_jp_jp_trans (trans_nbr,edo_asn_nbr,esn_asn_nbr,dn_nbr_pkgs,last_modify_dttm,last_modify_user_id,remarks) values(";
					sqltlog1 = "SELECT MAX(TRANS_NBR) maxTransNbr FROM tesn_jp_jp_TRANS WHERE esn_asn_NBR=:tesn_nbr ";
				} else if (chktesnJpPsa_nbr.equals("Y")) {
					tesnupdsql = "update tesn_jp_psa set dn_nbr_pkgs = dn_nbr_pkgs -";
					// lak added for Audit Trial
					sqlupdJp_trans = "insert into tesn_jp_psa_trans (trans_nbr,edo_asn_nbr,esn_asn_nbr,dn_nbr_pkgs,last_modify_dttm,last_modify_user_id,remarks) values(";
					sqltlog1 = "SELECT MAX(TRANS_NBR) maxTransNbr FROM tesn_jp_psa_TRANS WHERE esn_asn_NBR=:tesn_nbr ";
				}
			}

			int countua = 0;
			int count = 0;

			dnsql = "select * from dn_details where DN_NBR =:dnNbr ";

			if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
				edoupdsql = "update gb_edo set dn_nbr_pkgs = dn_nbr_pkgs -";
				// lak added for audit trail
				// edoupdsql_trans = "insert into gb_edo_trans
				// (trans_nbr,edo_asn_nbr,last_modify_dttm,last_modify_user_id,dn_nbr_pkgs,remarks)
				// values(";
			} else {
				edoupdsql = "update gb_edo set trans_dn_nbr_pkgs = trans_dn_nbr_pkgs -";
				// lak added for audit trail
				// edoupdsql_trans = "insert into gb_edo_trans
				// (trans_nbr,edo_asn_nbr,last_modify_dttm,last_modify_user_id,trans_dn_nbr_pkgs,remarks)
				// values(";
			}

			int newuanbrpkgs = 0;


			paramMap.put("dnNbr", dnNbr);
			paramMap.put("tesn_nbr", tesn_nbr);
			log.info(" *** cancelDN SQL *****" + dnsql + param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(dnsql, paramMap);

			if (rs.next()) {
				newuanbrpkgs = rs.getInt("NBR_PKGS");
			}
			paramMap.put("newuanbrpkgs", newuanbrpkgs);
			paramMap.put("edoNbr", edoNbr);
			edoupdsql = edoupdsql + ":newuanbrpkgs where edo_asn_nbr =:edoNbr ";

			// lak added for Audit Trail start
			sqltlog = "SELECT MAX(TRANS_NBR) maxTransNbr FROM gb_edo_TRANS WHERE Edo_ASN_NBR=:edoNbr ";

			if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 22/01/2003
			{
				log.info(" *** cancelDN SQL *****" + sqltlog + param + paramMap);
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);
				if (rs1.next()) {
					// stransno = (rs1.getInt("maxTransNbr")) + 1;
				} else {
					// stransno = 0;
				}
			}

			// edoupdsql_trans = edoupdsql_trans
			// +stransno+","+edoNbr+",sysdate,'"+userid+"',"+newuanbrpkgs+",'DN Del')";

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				// count_trans = stmt.executeUpdate(edoupdsql_trans);
			}

			if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 22/01/2003
			{
				log.info(" *** cancelDN SQL *****" + sqltlog2 + param + paramMap);
				rs3 = namedParameterJdbcTemplate.queryForRowSet(sqltlog2, paramMap);
				if (rs3.next()) {
					stransno2 = (rs3.getInt("maxTransNbr")) + 1;
				} else {
					stransno2 = 0;
				}
			}
			sb.append(
					"INSERT INTO DN_DETAILS_TRANS(TRANS_NBR,DN_NBR,DN_STATUS,LAST_MODIFY_DTTM,LAST_MODIFY_USER_ID) VALUES(");
			sb.append(" :stransno2,:dnNbr,'X',SYSDATE,:userid)");
			dn_sql_trans = sb.toString();
			// lak added for Audit Trail end

			if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {
				tesnupdsql = tesnupdsql + ":newuanbrpkgs where esn_asn_nbr =:tesn_nbr ";
				paramMap.put("newuanbrpkgs", newuanbrpkgs);
				log.info(" *** cancelDN SQL *****" + tesnupdsql + param + paramMap);
				namedParameterJdbcTemplate.update(tesnupdsql, paramMap);

				// lak added for audit trail

				if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 22/01/2003
				{
					rs2 = namedParameterJdbcTemplate.queryForRowSet(sqltlog1, paramMap);
					if (rs2.next()) {
						stransno1 = (rs2.getInt("maxTransNbr")) + 1;
					} else {
						stransno1 = 0;
					}
				}

				sqlupdJp_trans = sqlupdJp_trans
						+ ":stransno1,:tesn_nbr,:edoNbr,:newuanbrpkgs,sysdate,:userid,'DN Del')";

				paramMap.put("stransno1", stransno1);
				paramMap.put("tesn_nbr", tesn_nbr);
				paramMap.put("edoNbr", edoNbr);
				paramMap.put("newuanbrpkgs", newuanbrpkgs);
				paramMap.put("userid", userid);
				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					log.info(" *** cancelDN SQL *****" + sqlupdJp_trans + param + paramMap);
					namedParameterJdbcTemplate.update(sqlupdJp_trans, paramMap);
				}

			}
			log.info(" *** fetchEdoDetails SQL *****" + edoupdsql + param + paramMap);
			count = namedParameterJdbcTemplate.update(edoupdsql, paramMap);

			log.info("after update====" + count);
			sb.setLength(0);
			sb.append("UPDATE DN_DETAILS SET DN_STATUS='X',LAST_MODIFY_USER_ID=:userid");
			sb.append(",LAST_MODIFY_DTTM=SYSDATE WHERE DN_NBR =:dnNbr ");
			sql = sb.toString();

			paramMap.put("userid", userid);
			paramMap.put("dnNbr", dnNbr);
			log.info("insertion count >>>>>>>>> " + sql + param + paramMap);
			log.info(" *** cancelDN SQL *****" + sql + param + paramMap);
			countua = namedParameterJdbcTemplate.update(sql, paramMap);

			log.info("insertion count >>>>>>>>> " + countua);
			log.info("insertion count >>>>>>>>> " + count);

			paramMap.put("dn_sql_trans", dn_sql_trans);
			paramMap.put("stransno2", stransno2);
			paramMap.put("dnNbr", dnNbr);
			paramMap.put("userid", userid);
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				log.info(" *** cancelDN SQL *****" + dn_sql_trans + param + paramMap);
				namedParameterJdbcTemplate.update(dn_sql_trans, paramMap);
			}

			if (countua == 0 || count == 0) {
				throw new BusinessException("M4201");
			}
			log.info("END: *** cancelDN Result *****" + dnNbr.toString());
		} catch (BusinessException e) {
			log.info("Exception cancelDN :", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception cancelDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: cancelDN  DAO  END");
		}
		return dnNbr;

	}

	@Override
	public String chktesnJpPsa_nbr(String esn_asnNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = "";
		String tesnjppsa = "N";
		try {
			log.info("START: chktesnJpPsa_nbr esn_asnNbr:" + esn_asnNbr);
			sql = "select * from tesn_jp_psa jppsa,esn e where e.esn_asn_nbr = jppsa.esn_asn_nbr and e.esn_status ='A' and jppsa.ESN_ASN_NBR =:esnAsnNbr";

			paramMap.put("esnAsnNbr", esn_asnNbr);
			log.info("SQL" + sql.toString() + "pstmt:" + param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				tesnjppsa = "Y";
			}
			log.info("END: *** chktesnJpPsa_nbr Result *****" + CommonUtility.deNull(tesnjppsa));
		} catch (NullPointerException e) {
			log.info("Exception chktesnJpPsa_nbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chktesnJpPsa_nbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO chktesnJpPsa_nbr");

		}
		return tesnjppsa;
	}

	@Override
	public String chktesnJpJp_nbr(String esn_asnNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = "";
		String tesnjpjp = "N";

		try {
			log.info("START: chktesnJpJp_nbr esn_asnNbr:" + esn_asnNbr);
			sql = "select * from tesn_jp_jp jpjp,esn e where e.esn_asn_nbr = jpjp.esn_asn_nbr and e.esn_status ='A' and jpjp.ESN_ASN_NBR =:esn_asnNbr and e.trans_crg != 'Y'";

			paramMap.put("esn_asnNbr", CommonUtility.deNull(esn_asnNbr));
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				tesnjpjp = "Y";
			}
			log.info("END: *** chktesnJpJp_nbr Result *****" + CommonUtility.deNull(tesnjpjp));
		} catch (NullPointerException e) {
			log.info("Exception chktesnJpJp_nbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chktesnJpJp_nbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO chktesnJpJp_nbr");

		}
		return tesnjpjp;
	}

	@Override
	public String chkEdoNbr(String edoNbr) throws BusinessException {
		String sql = "";
		String edo_Nbr = "NO";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: chkEdoNbr  DAO  Start Obj " + " edoNbr:" + edoNbr);
			sql = "select edo_asn_nbr from gb_edo where edo_asn_nbr =:edoNbr ";
			paramMap.put("edoNbr", edoNbr);
			log.info(" *** chkEdoNbr SQL *****" + sql + param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				edo_Nbr = "YES";
			}

			log.info("END: *** chkEdoNbr Result *****" + edo_Nbr.toString());
		} catch (NullPointerException e) {
			log.info("Exception chkEdoNbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkEdoNbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO chkEdoNbr");
		}
		return edo_Nbr;
	}

	@Override
	public String chktesnEdo(String edoNbr) throws BusinessException {
		String sql = "";
		String edoNbrChk = "NO";
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: chktesnEdo  DAO  Start Obj " + " edoNbr:" + edoNbr);
			sb.append("select b.ATB_DTTM from gb_edo a,berthing b where b.SHIFT_IND = 1 ");
			sb.append(" and a.var_nbr = b.vv_cd and a.edo_asn_nbr =:edoNbr and b.ATB_DTTM is null");
			sql = sb.toString();
			paramMap.put("edoNbr", edoNbr);
			log.info(" *** chktesnEdo SQL *****" + sql + param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				edoNbrChk = "YES";
			}
			log.info("END: *** chktesnEdo Result *****" + edoNbrChk.toString());
		} catch (NullPointerException e) {
			log.info("Exception chktesnEdo :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chktesnEdo :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chktesnEdo  DAO  END");
		}
		return edoNbrChk;
	}

	@Override
	public List<EdoValueObjectContainerised> fetchEdo(String edoNbr, String compCode, String searchcrg) throws BusinessException {
		String sql = "";
		List<EdoValueObjectContainerised> edoVect = new ArrayList<EdoValueObjectContainerised>();
		// Vector edoRowVect = new Vector();
		// Vector edoTempVect = new Vector();
		List<String> edoAsnNbr = new ArrayList<String>();
		edoAsnNbr = fetchEdoList(edoNbr, searchcrg);
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		SqlRowSet rs = null;
		try {
			log.info("START: fetchEdo  DAO  Start Obj " + " edoNbr:" + edoNbr + " compCode:" + compCode + " searchcrg:"
					+ searchcrg);

			for (int i = 0; i < edoAsnNbr.size(); i++) {
				String edoasnNbr = (String) edoAsnNbr.get(i);
				boolean checkjpjp = chktesnJpJp(edoasnNbr);
				boolean checkjppsa = chktesnJpPsa(edoasnNbr);
				if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
					// if(!checkjpjp && !checkjppsa){
					sb.append("select  a.edo_asn_nbr,a.nbr_pkgs as nbr_pkgs,a.bl_nbr ,a.crg_status,");
					sb.append("nvl(d.NBR_PKGS_IN_PORT,0) as nbrPkgsPort,");
					sb.append(
							"a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0)-nvl(a.CUT_OFF_NBR_PKGS,0) as balance,");
					sb.append("nvl(a.dn_nbr_pkgs,0)+nvl(trans_dn_nbr_pkgs,0) as dnPkgs,");
					sb.append("nvl(a.CUT_OFF_NBR_PKGS,0) as cutoffPkgs from gb_edo a,");
					sb.append("manifest_details d where EDO_ASN_NBR =:edoasnNbr ");
					sb.append(" and a.mft_seq_nbr = d.mft_seq_nbr and a.edo_status='A'");
					sql = sb.toString();
					paramMap.put("edoasnNbr", edoasnNbr);
					log.info(" *** fetchEdo SQL *****" + sql + param + paramMap);
					rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
					while (rs.next()) {
						EdoValueObjectContainerised edoVo = new EdoValueObjectContainerised();
						edoVo.setEdoAsnNbr(CommonUtility.deNull(rs.getString("edo_asn_nbr")));
						edoVo.setNoPkgs(CommonUtility.deNull(rs.getString("nbr_pkgs")));
						edoVo.setBlNbr(CommonUtility.deNull(rs.getString("bl_nbr")));
						edoVo.setCrgStatus(CommonUtility.deNull(rs.getString("crg_status")));
						edoVo.setBalance(CommonUtility.deNull(rs.getString("balance")));
						edoVo.setDeliveredPkgs(CommonUtility.deNull(rs.getString("dnPkgs")));
						edoVo.setDeclPkgs(CommonUtility.deNull(rs.getString("nbrPkgsPort")));
						edoVo.setBillTon(CommonUtility.deNull(rs.getString("cutoffPkgs")));
						edoVect.add(edoVo);
					}
					// }
				} else {
					if (checkjpjp) {
						sql = new String();
						sb = new StringBuffer();
						sb.append("select vsl_nm,v.out_voy_nbr,c.esn_asn_nbr,a.edo_asn_nbr,c.nbr_pkgs,");
						sb.append("a.bl_nbr ,a.crg_status,nvl(c.DN_NBR_PKGS,0) as dnPkgs,");
						sb.append("c.nbr_pkgs - nvl(c.DN_NBR_PKGS,0) as balance from gb_edo a,vessel_call v,");
						sb.append(
								"tesn_jp_jp c,esn e where v.vv_cd = e.out_voy_var_nbr and c.EDO_ASN_NBR = a.EDO_ASN_NBR");
						sb.append(" and c.EDO_ASN_NBR =:edoasnNbr and a.crg_status in('T','R') and a.edo_status='A'");
						sb.append("  and e.esn_asn_nbr =c.esn_asn_nbr and e.ESN_STATUS ='A'");
						sql = sb.toString();

						paramMap.put("edoasnNbr", edoasnNbr);
						log.info(" *** fetchEdo SQL *****" + sql + param + paramMap);
						rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
						while (rs.next()) {
							EdoValueObjectContainerised edoVo = new EdoValueObjectContainerised();
							edoVo.setEdoAsnNbr(CommonUtility.deNull(rs.getString("edo_asn_nbr")));
							edoVo.setNoPkgs(CommonUtility.deNull(rs.getString("nbr_pkgs")));
							edoVo.setBlNbr(CommonUtility.deNull(rs.getString("bl_nbr")));
							edoVo.setCrgStatus(CommonUtility.deNull(rs.getString("crg_status")));
							edoVo.setVslName(CommonUtility.deNull(rs.getString("vsl_nm")));
							edoVo.setCaIcNbr(CommonUtility.deNull(rs.getString("esn_asn_nbr")));
							edoVo.setBalance(CommonUtility.deNull(rs.getString("balance")));
							edoVo.setDeliveredPkgs(CommonUtility.deNull(rs.getString("dnPkgs")));
							edoVo.setOutVoyNbr(CommonUtility.deNull(rs.getString("out_voy_nbr")));
							edoVect.add(edoVo);
						}
					}
					if (checkjppsa) {
						sql = new String();
						sb = new StringBuffer();
						sb.append("select SECOND_CAR_VES_NM,SECOND_CAR_VOY_NBR,c.esn_asn_nbr,a.edo_asn_nbr,");
						sb.append("c.nbr_pkgs,a.bl_nbr ,a.crg_status,nvl(c.DN_NBR_PKGS,0) as dnPkgs,");
						sb.append(
								"c.nbr_pkgs - nvl(c.DN_NBR_PKGS,0) as balance from gb_edo a,tesn_jp_psa c,esn e where ");
						sb.append("c.EDO_ASN_NBR = a.EDO_ASN_NBR and c.EDO_ASN_NBR =:edoasnNbr");
						sb.append(" and a.crg_status in('T','R') and a.edo_status='A' and");
						sb.append(" e.esn_asn_nbr =c.esn_asn_nbr and e.ESN_STATUS ='A'");
						sql = sb.toString();

						paramMap.put("edoasnNbr", edoasnNbr);
						log.info(" *** fetchEdo SQL *****" + sql + param + paramMap);
						rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
						while (rs.next()) {
							EdoValueObjectContainerised edoVo = new EdoValueObjectContainerised();
							edoVo.setEdoAsnNbr(CommonUtility.deNull(rs.getString("edo_asn_nbr")));
							edoVo.setNoPkgs(CommonUtility.deNull(rs.getString("nbr_pkgs")));
							edoVo.setBlNbr(CommonUtility.deNull(rs.getString("bl_nbr")));
							edoVo.setCrgStatus(CommonUtility.deNull(rs.getString("crg_status")));
							edoVo.setVslName(CommonUtility.deNull(rs.getString("SECOND_CAR_VES_NM")));
							edoVo.setCaIcNbr(CommonUtility.deNull(rs.getString("esn_asn_nbr")));
							edoVo.setBalance(CommonUtility.deNull(rs.getString("balance")));
							edoVo.setDeliveredPkgs(CommonUtility.deNull(rs.getString("dnPkgs")));
							edoVo.setOutVoyNbr(CommonUtility.deNull(rs.getString("SECOND_CAR_VOY_NBR")));
							edoVect.add(edoVo);
						}
					}
				}

			} // End of for loop
			log.info("END: *** fetchEdo Result *****" + edoVect.toString());
		} catch (NullPointerException e) {
			log.info("Exception fetchEdo :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception fetchEdo :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchEdo  DAO  END");
		}
		return edoVect;

	}

	@Override
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
			log.info("SQL" + sb.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				tesnjpjp = true;
			}

			log.info("END: *** chktesnJpJp Result *****" + tesnjpjp);
		} catch (NullPointerException e) {
			log.info("Exception END:chktesnJpJp :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception END:chktesnJpJp :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:chktesnJpJp DAO end");

		}
		return tesnjpjp;
	}// end of chktesnJpJp

	@Override
	public boolean chktesnJpPsa(String edoNbr) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		boolean tesnjppsa = false;
		sql = "select * from tesn_jp_psa jppsa,esn e where e.esn_asn_nbr = jppsa.esn_asn_nbr and e.esn_status ='A' and EDO_ASN_NBR = :edoNbr";
		try {
			log.info("START: chktesnJpPsa  DAO  Start Obj " + edoNbr);

			paramMap.put("edoNbr", edoNbr);
			log.info(" chktesnJpPsa  SQL " + sql + param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				tesnjppsa = true;
			}

			log.info(" chktesnJpPsa  DAO  Result" + tesnjppsa);
		} catch (NullPointerException e) {
			log.info("Exception chktesnJpPsa :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chktesnJpPsa :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chktesnJpPsa  DAO  END");

		}
		return tesnjppsa;
	}// end of chktesnJpPsa
// added on 12/09/02

	private String getSchemeName(String accNo, String vvcd) throws BusinessException {

		String sql = "";
		String sch = "";
		String schemeName = "";
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;

		Map<String, String> paramMap = new HashMap<String, String>();

		String sql1 = "SELECT SCHEME FROM VESSEL_CALL WHERE VV_CD =:vvcd ";
		sql = "SELECT SCHEME_CD FROM VESSEL_SCHEME WHERE ACCT_NBR=:accNo";
		try {
			log.info("START: getSchemeName accNo:" + accNo + "vvcd:" + vvcd);

			log.info(" *** END:getSchemeName SQL *****" + sql1);
			paramMap.put("vvcd", vvcd);
			log.info("SQL" + sql1.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);
			if (rs.next()) {
				schemeName = rs.getString("SCHEME");
			}

			// <cfg: add new scheme for Wooden Craft: JWP, 27.may.08>
			// if (! (schemeName.equals("JLR") || schemeName.equals("JNL") ||
			// schemeName.equals("JBT"))) {
			// add new scheme for LCT, 13.feb.11 by hpeng
			if (!(schemeName.equals("JLR") || schemeName.equals("JNL") || schemeName.equals("JBT")
					|| schemeName.equals("JWP") || schemeName.equals(ConstantUtil.LCT_SCHEME))) {
				// <cfg: add new scheme for Wooden Craft: JWP, 27.may.08/>

				paramMap.put("accNo", accNo);
				log.info("SQL" + sql.toString());
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
				if (rs1.next()) {
					sch = rs1.getString("SCHEME_CD");
				}
			}

			log.info("END: *** END:getSchemeName Result *****" + sch.toString());
		} catch (NullPointerException e) {
			log.info("Exception END:getSchemeName :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception END:getSchemeName :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:getSchemeName");
		}
		return sch;
	}

	private List<String> fetchEdoList(String edoNbr, String searchcrg) throws BusinessException {
		String sql = "";
		List<String> edoVect = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		try {
			log.info("START: fetchEdoList  DAO  Start Obj " + " edoNbr:" + edoNbr + " searchcrg:" + searchcrg);

			if (edoNbr != null && !edoNbr.equals("")) {
				if (edoNbr.substring(0, 1).equals("0")) {
					if (edoNbr.length() > 0)
						edoNbr = edoNbr.substring(1);
				}
			}

			sb.append(
					"select  a.edo_asn_nbr from gb_edo a, manifest_details b, vessel_call c where EDO_ASN_NBR like :edoNbr");
			sb.append(" and a.mft_seq_nbr = b.mft_seq_nbr and edo_status = 'A' and a.var_nbr = c.vv_cd  and");
			sb.append(" vv_status_ind in ('BR','UB','CL') and a.dis_type in ('D','N') order by a.edo_asn_nbr desc");
			sql = sb.toString();

			paramMap.put("edoNbr", "%" + edoNbr + "%");
			log.info(" *** fetchEdoList SQL *****" + sql + param + paramMap);

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			// boolean checkjpjp = chktesnJpJp(edoNbr);
			// boolean checkjppsa = chktesnJpPsa(edoNbr);
			while (rs.next()) {
				String edoasnNbr = CommonUtility.deNull(rs.getString("edo_asn_nbr"));
				// boolean checkjpjp = chktesnJpJp(edoasnNbr);
				// boolean checkjppsa = chktesnJpPsa(edoasnNbr);
				// if(searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")){
				// if(!checkjpjp && !checkjppsa)
				edoVect.add(edoasnNbr);
				// }else{
				// if(checkjpjp)
				// edoVect.addElement(edoasnNbr);
				// if(checkjppsa)
				// edoVect.addElement(edoasnNbr);
				// }
			}
			log.info("END: *** fetchEdoList Result *****" + edoVect.toString());
		} catch (NullPointerException e) {
			log.info("Exception END: fetchEdoList :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception END: fetchEdoList :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchEdoList");
		}
		return edoVect;
	}

}
