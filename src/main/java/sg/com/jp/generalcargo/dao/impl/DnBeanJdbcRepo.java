package sg.com.jp.generalcargo.dao.impl;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.ContainerCommonFunctionRepo;
import sg.com.jp.generalcargo.dao.ContainerDataRepo;
import sg.com.jp.generalcargo.dao.DnRepo;
import sg.com.jp.generalcargo.domain.ContainerValueObject;
import sg.com.jp.generalcargo.domain.EdoValueObjectContainerised;
import sg.com.jp.generalcargo.domain.EdoValueObjectOps;
import sg.com.jp.generalcargo.domain.ShutoutEdoDnReport;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("DnRepository")
public class DnBeanJdbcRepo implements DnRepo {

	private static final Log log = LogFactory.getLog(DnBeanJdbcRepo.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public String logStatusGlobal = "Y";

	@Autowired
	private ContainerCommonFunctionRepo containerCommonFunctionRepo;

	@Autowired
	private ContainerDataRepo containerDataRepo;

	// ejb.sessionBeans.gbms.ops.dnua.dn--dnBean
	// StartRegion DnJdbcRepository //

	@Override
	public boolean chkraiseCharge(String edonbr) throws BusinessException {

		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		String sql = "";
		boolean countua = false;
		try {
			log.info("START: chkraiseCharge  DAO  Start Obj " + " edonbr:" + edonbr);

			sb.append(
					"select to_char(b.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb from gb_edo a,berthing b,vessel_call vc where a.var_nbr =  vc.vv_cd and vc.vv_cd = b.vv_cd and a.edo_asn_nbr=");
			sb.append(
					":edonbr  and to_date(to_char(b.ATB_DTTM,'ddmmyyyy'),'ddmmyyyy') >= to_date('06112002','ddmmyyyy')");

			sql = sb.toString();

			log.info(" *** chkraiseCharge SQL *****" + sql);
			log.info(" *** chkraiseCharge params *****" + paramMap.toString());

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
		// LogManager.instance.logInfo("isCntrCrgDn :"+isCntrCrgDn);
		return countua;
	} // END OF chkCntrCrgDN() method -- 17th Sept,03 Vani.

	@Override
	public boolean chkCntrCrgDn(String strDnNbr) throws BusinessException {
		boolean isCntrCrgDn = false;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();

		try {
			log.info("START: chkCntrCrgDn  DAO  Start Obj " + " strDnNbr:" + strDnNbr);

			sb.append("SELECT count(*) FROM manifest_details WHERE  bl_status = 'A' AND bl_nbr=");
			sb.append("(SELECT bl_nbr FROM gb_edo WHERE edo_asn_nbr = (SELECT edo_asn_nbr FROM dn_details ");
			sb.append("WHERE dn_nbr=:strDnNbr)) AND unstuff_seq_nbr<>0 AND var_nbr = (SELECT var_nbr ");
			sb.append("FROM gb_edo WHERE edo_asn_nbr = (SELECT edo_asn_nbr FROM dn_details WHERE dn_nbr=");
			sb.append(":strDnNbr))");

			String sql = sb.toString();

			log.info(" *** chkCntrCrgDn SQL *****" + sql);

			paramMap.put("strDnNbr", strDnNbr);
			log.info(" *** chkCntrCrgDn params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				// LogManager.instance.logInfo("rs.getString(1)-" + rs.getInt(1));
				if (rs.getInt(1) == 1)
					isCntrCrgDn = true;
				else {
					isCntrCrgDn = false;
					// LogManager.instance.logInfo("chkCntrCrgDn-false");
				}
			} else {
				log.info("chkCntrCrgDn-No record");
			}

			log.info("END: *** chkCntrCrgDn Result *****" + isCntrCrgDn);
		} catch (NullPointerException e) {
			log.info("Exception chkCntrCrgDn :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkCntrCrgDn :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkCntrCrgDn  DAO  END");
		}
		// LogManager.instance.logInfo("isCntrCrgDn :"+isCntrCrgDn);
		return isCntrCrgDn;
	}

	@Override
	public void updateCntr(String cntrseq, String cntrNo, String user, String newCatCode) throws BusinessException {

		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String status = "";
		int weight = 0;
		String descCntr = "";
		try {
			log.info("START: updateCntr  DAO  Start Obj " + " cntrseq:" + cntrseq + " cntrNo:" + cntrNo + " user:"
					+ user + " newCatCode:" + newCatCode);

			StringBuffer sql = new StringBuffer();
			sql.setLength(0);
			sql.append("select STATUS, DECLR_WT from CNTR where CNTR_SEQ_NBR = :cntrseq");

			log.info(" *** updateCntr SQL *****" + sql);

			paramMap.put("cntrseq", cntrseq);
			log.info(" *** updateCntr params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				status = rs.getString("STATUS");
				weight = rs.getInt("DECLR_WT");
			}
			// log.info("--------insert cntr_event_log for DN 1st:" + status + "," +
			// weight);
			// sql.setLength(0);
			// sql.append("insert into cntr_event_log(CNTR_SEQ_NBR, TXN_DTTM, TXN_CD, " +
			// "CNTR_NBR, STATUS, DECLR_WT, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
			// sql.append("values(?, ?, 'STF', ?, ?, ?, ?, sysdate)");
			// prst = conn.prepareStatement(sql.toString());
			// prst.setString(1, cntrseq);
			// prst.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
			// prst.setString(3, cntrNo);
			// prst.setString(4, status);
			// prst.setInt(5, weight);
			// prst.setString(6, user);
			// int count = prst.executeUpdate();
			// if (count <= 0) {
			// prst.close();
			// conn.close();
			// sessionContext.setRollbackOnly();
			// throw new BusinessException("M4201");
			// }
			descCntr = status + weight;

			if ("E".equals(status)) {
				// update the status of cntr to F
				String sqla = "UPDATE CNTR SET CAT_CD = :newCatCode, STATUS = 'F', LAST_MODIFY_DTTM = sysdate,LAST_MODIFY_USER_ID =:user WHERE CNTR_SEQ_NBR=:cntrseq ";
				// log.info("SQL DN:" + sqla + "," + newCatCode + "," + user + "," + cntrseq);

				paramMap.put("newCatCode", newCatCode);
				paramMap.put("user", user);
				paramMap.put("cntrseq", cntrseq);
				
				log.info(" *** updateCntr SQL *****" + sqla.toString());
				log.info(" *** updateCntr params *****" + paramMap.toString());

				namedParameterJdbcTemplate.update(sqla.toString(), paramMap);
			}
			// capture dttm
			StringBuffer sb = new StringBuffer();

			sb.append("INSERT INTO CNTR_TXN(CNTR_SEQ_NBR, CNTR_NBR, TXN_CD, TXN_DTTM,");
			sb.append(" LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID, ERROR_MSG) ");
			sb.append("VALUES(:cntrseq, :cntrNo, 'STF', :time, sysdate, :user, :descCntr)");

			String sqlb = sb.toString();

			paramMap.put("time", new Timestamp(new java.util.Date().getTime()));
			paramMap.put("cntrseq", cntrseq);
			paramMap.put("cntrNo", cntrNo);
			paramMap.put("user", user);
			paramMap.put("descCntr", descCntr);
			
			log.info(" *** updateCntr SQL *****" + sqlb.toString());
			log.info(" *** updateCntr params *****" + paramMap.toString());
			namedParameterJdbcTemplate.update(sqlb.toString(), paramMap);

			log.info("END: *** updateCntr Result *****");
		} catch (NullPointerException e) {
			log.info("Exception updateCntr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception updateCntr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateCntr  DAO  END");
		}

	}

	@Override
	public void updateDN(String cntrNo, String dnNo) throws BusinessException {

		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {
			log.info("START: updateDN  DAO  Start Obj " + " cntrNo:" + cntrNo + " dnNo:" + dnNo);

			String sql = "update dn_details set cntr_nbr = :cntrNo, MOT_CREATE_DTTM = :time where dn_nbr = :dnNo";

			log.info(" *** updateDN SQL *****" + sql);

			paramMap.put("cntrNo", cntrNo);
			paramMap.put("time", new Timestamp(new java.util.Date().getTime()));
			paramMap.put("dnNo", dnNo);

			log.info(" *** updateDN SQL *****" + sql.toString());
			log.info(" *** updateDN params *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sql.toString(), paramMap);
			log.info("END: *** updateDN Result *****");
		} catch (NullPointerException e) {
			log.info("Exception updateDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception updateDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateDN  DAO  END");
		}

	}

	@Override
	public boolean isTESN_JP_JP(String edoNbr, String esnNbr) throws BusinessException {
		boolean isJP = false;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = null;
		try {
			log.info("START: isTESN_JP_JP  DAO  Start Obj " + " esnNbr:" + esnNbr + " edoNbr:" + edoNbr);

			sql = "select * from TESN_JP_JP where EDO_ASN_NBR=:edoNbr and ESN_ASN_NBR=:esnNbr";

			log.info(" *** isTESN_JP_JP SQL *****" + sql);

			paramMap.put("esnNbr", esnNbr);
			paramMap.put("edoNbr", edoNbr);

			log.info(" *** isTESN_JP_JP params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				isJP = true;
			} else {
				isJP = false;
			}
			log.info("END: *** isTESN_JP_JP Result *****" + isJP);
		} catch (NullPointerException e) {
			log.info("Exception isTESN_JP_JP :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isTESN_JP_JP :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isTESN_JP_JP  DAO  END");
		}

		return isJP;
	}

	@Override
	public String createDN(String edoNbr, String transtype, String edo_Nbr_Pkgs, String NomWt, String NomVol,
			String date_time, String transQty, String nric_no, String dpname, String veh1, String userid, String icType,
			String searchcrg, String tesn_nbr, String cargoDes) throws BusinessException {
		// log.info("transQty :"+transQty+" //edo_Nbr_Pkgs :"+edo_Nbr_Pkgs+" //searchcrg
		// :"+searchcrg+" //edoNbr :"+edoNbr +" //NomWt :"+NomWt+" //NomVol :"+NomVol+"
		// //date_time :"+date_time+" //nric_no :"+nric_no+" //dpname :"+dpname+" //veh1
		// :"+veh1+ " //userid :"+userid +" //icType :"+icType +" //tesn_nbr :"+tesn_nbr
		// + " //cargoDes : " + cargoDes);
		// ++ VietND02 - Set value of cargoDes to add
		String cargoDest = "";
		if (cargoDes.equalsIgnoreCase("Vessel"))
			cargoDest = "V";
		else if (cargoDes.equalsIgnoreCase("Leased Area"))
			cargoDest = "L";
		else
			cargoDest = "O"; // cargoDes = Out of JP
		// -- VietND02
		String sql = "";
		String DNNbr = "";
		String ftrans = "";
		String edosql = "";
		String sqlJp = "";
		String sqlupdJp = "";
		String edoupdsql = "";
		String dnnbrtrans = "";
		String sqlveh1 = "";

		// lak added for audit trial 23/01/2003 start
		String sqlupdJp_trans = "";
		String sqlveh1_trans = "";
		/*
		 * String sqlveh2_trans = ""; String sqlveh3_trans = ""; String sqlveh4_trans =
		 * ""; String sqlveh5_trans = "";
		 */
		String sql_trans = "";

		String sqltlog1 = "";
		String sqltlog = "";
		int stransno = 0;
		int stransno1 = 0;
		int count_trans1 = 0;
		int cnt_trans = 0;
		// lak added for audit trial 23/01/2003 end

		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();

		java.util.StringTokenizer dntrans = null;

		int countua = 0;
		int count = 0;
		int count1 = 0;
		double Bill_ton = 0.0;
		double dn_nom_wt = 0;
		double dn_nom_vol = 0;

		dnnbrtrans = getDNNbr(edoNbr);
		dntrans = new java.util.StringTokenizer(dnnbrtrans, "-");
		DNNbr = (dntrans.nextToken()).trim();
		ftrans = (dntrans.nextToken()).trim();

		log.info("inside createDN() ftrans >>>>>>>>> " + ftrans);

		dn_nom_wt = (Double.parseDouble(transQty) / Double.parseDouble(edo_Nbr_Pkgs)) * Double.parseDouble(NomWt);
		dn_nom_vol = (Double.parseDouble(transQty) / Double.parseDouble(edo_Nbr_Pkgs)) * Double.parseDouble(NomVol);

		// if(ftrans.equals("True"))
		// {
		if ((dn_nom_wt / 1000) > dn_nom_vol) {
			Bill_ton = dn_nom_wt / 1000;
		} else {
			Bill_ton = dn_nom_vol;
		} // end if nomwt
			// }else
			// {
			// Bill_ton = 0;
			// }// end if ftrans
			// log.info("dn_nom_wt :" + dn_nom_wt+" //dn_nom_vol :"+dn_nom_vol);

		// log.info("edosql :"+edosql);
		String chktesnJpJp_nbr = chktesnJpJp_nbr(tesn_nbr);
		// log.info("22222chktesnJpJp_nbr == " + chktesnJpJp_nbr);
		String chktesnJpPsa_nbr = chktesnJpPsa_nbr(tesn_nbr);
		boolean checkEdoStuff = chkEDOStuffing(edoNbr); // vinayak added 09 jan 2004
		// log.info("3333chktesnJpPsa_nbr == " + chktesnJpPsa_nbr+" //checkEdoStuff
		// :"+checkEdoStuff);
		// boolean checkjpjp = chktesnJpJp(edoNbr);
		// boolean checkjppsa = chktesnJpPsa(edoNbr);

		int newuanbrpkgs = 0;
		int newuanbrpkgs_tesn = 0;
		SqlRowSet rsStuff = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs_tesn = null;
		SqlRowSet rs2 = null;
		try {
			log.info("START: createDN  DAO  Start Obj " + " edoNbr:" + edoNbr + " transtype:" + transtype
					+ " edo_Nbr_Pkgs:" + edo_Nbr_Pkgs + " NomWt:" + NomWt + " NomVol:" + NomVol + " date_time:"
					+ date_time + " transQty:" + transQty + " nric_no:" + nric_no + " dpname:" + dpname + " veh1:"
					+ veh1 + " icType:" + icType + " searchcrg:" + searchcrg + " tesn_nbr:" + tesn_nbr + " cargoDes:"
					+ cargoDes);

			edosql = "select * from gb_edo where edo_asn_nbr = :edoNbr";

			log.info(" *** createDN SQL *****" + sql);
			paramMap.put("edoNbr", edoNbr);
			// Statement stmt_tesn = conn.createStatement();
			log.info(" *** createDN edosql *****" + edosql);
			log.info(" *** createDN paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(edosql.toString(), paramMap);

			// log.info("11111edosql == " + edosql);
			if (rs.next()) {
				if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT"))
					newuanbrpkgs = rs.getInt("dn_nbr_pkgs"); // ("dn_wt");
				else
					newuanbrpkgs = rs.getInt("TRANS_DN_NBR_PKGS");

				// log.info("before adding newuanbrpkgs :"+newuanbrpkgs);
				newuanbrpkgs = newuanbrpkgs + Integer.parseInt(transQty);
			}
			// log.info("Test here //newuanbrpkgs :"+newuanbrpkgs);

			if ((checkEdoStuff) && (chktesnJpJp_nbr.equals("N")) && (chktesnJpPsa_nbr.equals("N"))
					&& (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("T"))) // vinayak added 19 jan
																								// 2004 modified 06 feb
																								// 2004
			{

				sb.append(
						"SELECT nvl(csd.DN_NBR_PKGS,0) as DN_NBR_PKGS FROM cc_stuffing cs,cc_stuffing_details csd WHERE ");
				sb.append("cs.STUFF_CLOSED='Y' AND cs.ACTIVE_STATUS='A' AND cs.STUFF_SEQ_NBR=csd.STUFF_SEQ_NBR AND ");
				sb.append("csd.REC_STATUS='A' AND csd.STUFF_SEQ_NBR=:tesn_nbr AND csd.EDO_ESN_NBR=:edoNbr");

				String strStuffSql = sb.toString();

				String strStuffSqlupd = "UPDATE cc_stuffing_details SET DN_NBR_PKGS=DN_NBR_PKGS+:transQty WHERE NBR_PKGS>=(SELECT NVL(SUM(NBR_PKGS),0)+ :transQty FROM dn_details WHERE DN_STATUS='A' AND EDO_ASN_NBR=:edoNbr and tesn_asn_nbr=:tesn_nbr) AND STUFF_SEQ_NBR=:tesn_nbr AND EDO_ESN_NBR=:edoNbr";

				paramMap.put("tesn_nbr", tesn_nbr);
				paramMap.put("edoNbr", edoNbr);
				paramMap.put("transQty", transQty);

				log.info(" *** createDN SQL *****" + strStuffSql);
				log.info(" *** createDN paramMap *****" + paramMap.toString());
				rsStuff = namedParameterJdbcTemplate.queryForRowSet(strStuffSql.toString(), paramMap);
				// log.info("***strStuffSql createDN() :"+strStuffSql+" //strStuffSqlupd
				// :"+strStuffSqlupd);
				if (rsStuff.next()) {
					log.info(" *** createDN SQL *****" + strStuffSqlupd);
					log.info(" *** createDN paramMap *****" + paramMap.toString());
					count1 = namedParameterJdbcTemplate.update(strStuffSqlupd.toString(), paramMap);
					// log.info("countStuff :"+count1);
				}
			}

			if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
				edoupdsql = "update gb_edo set last_modify_dttm = sysdate, dn_nbr_pkgs ="; // "update bulk_gb_edo set
																							// dn_wt =";VANI-16th JUly
				/*
				 * sql =
				 * "insert into dn_details(DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, "
				 * +
				 * "TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, "
				 * + "LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE) VALUES('" + DNNbr + "','" +
				 * edoNbr + "','A','";
				 */
				// ++ VietND Change SQL
				sb = new StringBuffer();

				sb.append("insert into dn_details(TRUCK_NBR,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, ");
				sb.append(
						"TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, ");
				sb.append("LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE, CRG_DEST, GATE_OUT_DTTM) VALUES(");
				sb.append(":veh1,:DNNbr,:edoNbr,'A',");
				sql = sb.toString();
				// -- VietND
				// laks added for audit trial
				/*
				 * sql_trans =
				 * "insert into dn_details_trans(trans_nbr,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, "
				 * +
				 * "TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, "
				 * + "LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE) VALUES('0','" + DNNbr + "','"
				 * + edoNbr + "','A','";
				 */
				// ++ VietND Change SQL
				sb = new StringBuffer();

				sb.append(
						"insert into dn_details_trans(TRUCK_NBR,trans_nbr,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, ");
				sb.append(
						"TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, ");
				sb.append("LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE, CRG_DEST, GATE_OUT_DTTM) VALUES(");
				sb.append(":veh1,'0',:DNNbr,:edoNbr,'A',");
				sql_trans = sb.toString();
				// -- VietND
				// edoupdsql_trans = "insert into gb_edo_trans
				// (trans_nbr,edo_asn_nbr,last_modify_dttm,last_modify_user_id,dn_nbr_pkgs,remarks)
				// values(";
				// log.info("sql :" +sql+" //sql_trans :"+sql_trans);
			} else {

				edoupdsql = "update gb_edo set last_modify_dttm = sysdate, trans_dn_nbr_pkgs =";
				/*
				 * sql =
				 * "insert into dn_details(DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, "
				 * +
				 * "TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, "
				 * + "LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TESN_ASN_NBR) VALUES('" + DNNbr
				 * + "','" + edoNbr + "','A','";
				 */
				// ++ VietND Change SQL
				sb = new StringBuffer();

				sb.append("insert into dn_details(TRUCK_NBR,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, ");
				sb.append(
						"TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, ");
				sb.append("LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TESN_ASN_NBR, CRG_DEST, GATE_OUT_DTTM) VALUES(");
				sb.append(":veh1,:DNNbr,:edoNbr,'A',");
				sql = sb.toString();
				// -- VietND

				// laks added for audit trial
				/*
				 * sql_trans =
				 * "insert into dn_details_trans(trans_nbr,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, "
				 * +
				 * "TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, "
				 * + "LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TESN_ASN_NBR) VALUES('0','" +
				 * DNNbr + "','" + edoNbr + "','A','";
				 */
				// ++ VietND Change SQL
				sb = new StringBuffer();

				sb.append(
						"insert into dn_details_trans(TRUCK_NBR,trans_nbr,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, ");
				sb.append(
						"TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, ");
				sb.append("LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TESN_ASN_NBR, CRG_DEST, GATE_OUT_DTTM) VALUES(");
				sb.append(":veh1,'0',:DNNbr,:edoNbr,'A',");
				sql_trans = sb.toString();
				// -- VietND
				// log.info("edoupdsql :"+edoupdsql+" //sql :" +sql+" //sql_trans :"+sql_trans);
				// edoupdsql_trans = "insert into gb_edo_trans
				// (trans_nbr,edo_asn_nbr,last_modify_dttm,last_modify_user_id,trans_dn_nbr_pkgs,remarks)
				// values(";
			}
			sb = new StringBuffer();

			sb.append(edoupdsql);
			sb.append(":newuanbrpkgs where nbr_pkgs >= (select nvl(sum(nbr_pkgs),0)+ :transQty");
			sb.append(" from dn_details where dn_status='A' and edo_asn_nbr=:edoNbr) and edo_asn_nbr =:edoNbr");

			edoupdsql = sb.toString();

			paramMap.put("tesn_nbr", tesn_nbr);
			paramMap.put("edoNbr", edoNbr);
			paramMap.put("transQty", transQty);
			paramMap.put("newuanbrpkgs", newuanbrpkgs);

			log.info(" *** createDN SQL *****" + edoupdsql);
			log.info(" *** createDN paramMap *****" + paramMap.toString());
			count = namedParameterJdbcTemplate.update(edoupdsql.toString(), paramMap);
			// log.info("count :"+count+" //555555edoupdsql == " + edoupdsql);

			// lak added for Audit Trail start
			try {
				sqltlog = "SELECT MAX(TRANS_NBR) FROM gb_edo_TRANS WHERE Edo_ASN_NBR=:edoNbr";

				paramMap.put("edoNbr", edoNbr);

				if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003
					// log.info("666666sqltlog == " + sqltlog);
					log.info(" *** createDN SQL *****" + sqltlog);
					log.info(" *** createDN paramMap *****" + paramMap.toString());
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sqltlog.toString(), paramMap);

					if (rs1.next()) {
						stransno = (rs1.getInt(1)) + 1;
					} else {
						stransno = 0;
					}
					log.info("stransno == " + stransno);
				}
			} catch (Exception s2) {
				log.info("Exception createDN : ", s2);
			}
			// edoupdsql_trans = edoupdsql_trans
			// +stransno+","+edoNbr+",sysdate,'"+userid+"',"+newuanbrpkgs+",'DN Add')";

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				// count_trans = stmt.executeUpdate(edoupdsql_trans);
			}

			// lak added for Audit Trail end

			if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {
				if (chktesnJpJp_nbr.equals("Y")) {
					sqlJp = "select * from tesn_jp_jp where esn_asn_nbr = :tesn_nbr";

					sqlupdJp = "update tesn_jp_jp set dn_nbr_pkgs =";

					// lak added for Audit Trial
					sqlupdJp_trans = "insert into tesn_jp_jp_trans (trans_nbr,edo_asn_nbr,esn_asn_nbr,dn_nbr_pkgs,last_modify_dttm,last_modify_user_id,remarks) values(";
					sqltlog1 = "SELECT MAX(TRANS_NBR) FROM tesn_jp_jp_TRANS WHERE esn_asn_NBR=:tesn_nbr";
					// log.info("sqlJp :" + sqlJp+" //sqlupdJp :"+sqlupdJp+" //sqlupdJp_trans
					// :"+sqlupdJp_trans+" //sqltlog1 :"+sqltlog1);
				} else if (chktesnJpPsa_nbr.equals("Y")) {
					sqlJp = "select * from tesn_jp_psa where esn_asn_nbr = :tesn_nbr";

					sqlupdJp = "update tesn_jp_psa set dn_nbr_pkgs =";
					// lak added for Audit Trial
					sqlupdJp_trans = "insert into tesn_jp_psa_trans (trans_nbr,edo_asn_nbr,esn_asn_nbr,dn_nbr_pkgs,last_modify_dttm,last_modify_user_id,remarks) values(";
					sqltlog1 = "SELECT MAX(TRANS_NBR) FROM tesn_jp_psa_TRANS WHERE esn_asn_NBR=:tesn_nbr";
					// log.info("sqlJp :" + sqlJp+" //sqlupdJp :"+sqlupdJp+" //sqlupdJp_trans
					// :"+sqlupdJp_trans+" //sqltlog1 :"+sqltlog1);
				}

			}

			paramMap.put("tesn_nbr", tesn_nbr);
			if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {
				if (chktesnJpJp_nbr.equals("Y") || chktesnJpPsa_nbr.equals("Y")) { // added by vinay 06 feb 2004

					log.info(" *** createDN SQL *****" + sqlJp);
					log.info(" *** createDN paramMap *****" + paramMap.toString());
					rs_tesn = namedParameterJdbcTemplate.queryForRowSet(sqlJp.toString(), paramMap);
					// log.info("new 666666sqlJp == :" + sqlJp);
					if (rs_tesn.next()) {
						newuanbrpkgs_tesn = rs_tesn.getInt("DN_NBR_PKGS");
						newuanbrpkgs_tesn = newuanbrpkgs_tesn + Integer.parseInt(transQty);
					}
					// log.info("777777newuanbrpkgs_tesn == " + newuanbrpkgs_tesn);
					// sqlupdJp = sqlupdJp + newuanbrpkgs_tesn + "where esn_asn_nbr ='" +
					// tesn_nbr + "'";

					sb = new StringBuffer();

					sb.append(sqlupdJp);
					sb.append(":newuanbrpkgs_tesn  where nbr_pkgs >= (select nvl(sum(nbr_pkgs),0)+ :transQty ");
					sb.append(" from dn_details where dn_status='A' and tesn_asn_nbr=:tesn_nbr) and ");
					sb.append("esn_asn_nbr =:tesn_nbr");

					sqlupdJp = sb.toString();

					paramMap.put("newuanbrpkgs_tesn", newuanbrpkgs_tesn);
					paramMap.put("transQty", transQty);
					paramMap.put("tesn_nbr", tesn_nbr);

					// log.info("new sqlupdJp createDN() :" + sqlupdJp);
					log.info(" *** createDN SQL *****" + sqlupdJp);
					log.info(" *** createDN paramMap *****" + paramMap.toString());
					count1 = namedParameterJdbcTemplate.update(sqlupdJp.toString(), paramMap);
					// lak added for audit Trail

					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003
						// log.info("sqltlog1 :" +sqltlog1);

						log.info(" *** createDN SQL *****" + sqltlog1.toString());
						log.info(" *** createDN paramMap *****" + paramMap.toString());
						rs2 = namedParameterJdbcTemplate.queryForRowSet(sqltlog1.toString(), paramMap);
						if (rs2.next()) {
							stransno1 = (rs2.getInt(1)) + 1;
						} else {
							stransno1 = 0;
						}
						// log.info("stransno1 :" + stransno1);

					}

					sb = new StringBuffer();

					sb.append(sqlupdJp_trans);
					sb.append(":stransno1,:edoNbr,:tesn_nbr,:newuanbrpkgs_tesn,sysdate,:userid,'DN Add')");

					sqlupdJp_trans = sb.toString();

					paramMap.put("stransno1", stransno1);
					paramMap.put("newuanbrpkgs_tesn", newuanbrpkgs_tesn);
					paramMap.put("userid", userid);
					if (logStatusGlobal.equalsIgnoreCase("Y")) {
						// log.info("9999999sqlupdJp_trans == " + sqlupdJp_trans);
						log.info(" *** createDN SQL *****" + sqlupdJp_trans);
						log.info(" *** createDN paramMap *****" + paramMap.toString());
						count_trans1 = namedParameterJdbcTemplate.update(sqlupdJp_trans.toString(), paramMap);
						log.info("count_trans1 >>>>>>>>> " + count_trans1);
					}
					// lak added for audit Trail
					// log.info("edoupsql count >>>>>>>>> " + count);
				} // end of check
			}
			// log.info("\n\n ************ \n count ->"+ count + "<-count 1->"+ count1 +"\n
			// ************ ");

			// log.info("inside count 88888 \\\\---than 0000000");
			if (count == 0 || (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT") && count1 == 0)) {

				// log.info("Writing from DNEJB.createDN esnupdsql");
				log.info("Record Cannot be added to Database");
				// log.info("\n\n ************ \n B4 raising error \n ************ ");
				DNNbr = "";
				throw new BusinessException("M4201");
			} else if (count > 0) {
				// log.info("inside count >>>>>>>>>than 0000000");
				if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {

					sb = new StringBuffer();

					sb.append(sql);
					sb.append(
							":nric_no,:dpname,:icType,to_date(:date_time,'DD/MM/YYYY HH24:MI'),:transQty,:dn_nom_wt,");
					sb.append(":dn_nom_vol,:Bill_ton,:userid,sysdate,:userid,sysdate,'C',:transtype,:cargoDest,");
					sql = sb.toString();

					// For back-dated DN of more than x hours for cargo destination = Out of JP,
					// default the truck's gate-out time as the back-dated time. 26/5/2010.
					if (cargoDest.equals("O")) // Cargo Destination = Out of JP
						sql = sql
								+ "CASE WHEN TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') > SYSDATE-(SELECT VALUE/24 FROM SYSTEM_PARA WHERE PARA_CD='CL_DN') THEN NULL ELSE TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') END)";
					else
						sql = sql + "null)";

					// lak added for Audit trail
					sb = new StringBuffer();

					sb.append(sql_trans);
					sb.append(
							":nric_no,:dpname,:icType,to_date(:date_time,'DD/MM/YYYY HH24:MI'),:transQty,:dn_nom_wt,");
					sb.append(":dn_nom_vol,:Bill_ton,:userid,sysdate,:userid,sysdate,'C',:transtype,:cargoDest,");
					sql_trans = sb.toString();

					// For back-dated DN of more than x hours for cargo destination = Out of JP,
					// default the truck's gate-out time as the back-dated time. 26/5/2010.
					if (cargoDest.equals("O")) // Cargo Destination = Out of JP
						sql_trans = sql_trans
								+ "CASE WHEN TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') > SYSDATE-(SELECT VALUE/24 FROM SYSTEM_PARA WHERE PARA_CD='CL_DN') THEN NULL ELSE TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') END)";
					else
						sql_trans = sql_trans + "null)";

					// log.info("sql =="+sql+" //sql_trans :"+sql_trans);
				} else {
					sb = new StringBuffer();

					sb.append(sql);
					sb.append(
							":nric_no,:dpname,:icType,to_date(:date_time,'DD/MM/YYYY HH24:MI'),:transQty,:dn_nom_wt,");
					sb.append(
							":dn_nom_vol,:Bill_ton,:userid,sysdate,:userid,sysdate,'C',:transtype,:tesn_nbr,:cargoDest,");
					sql = sb.toString();

					// For back-dated DN of more than x hours for cargo destination = Out of JP,
					// default the truck's gate-out time as the back-dated time. 26/5/2010.
					if (cargoDest.equals("O")) // Cargo Destination = Out of JP
						sql = sql
								+ "CASE WHEN TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') > SYSDATE-(SELECT VALUE/24 FROM SYSTEM_PARA WHERE PARA_CD='CL_DN') THEN NULL ELSE TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') END)";
					else
						sql = sql + "null)";

					// lak added for Audit trail
					sb = new StringBuffer();

					sb.append(sql_trans);
					sb.append(
							":nric_no,:dpname,:icType,to_date(:date_time,'DD/MM/YYYY HH24:MI'),:transQty,:dn_nom_wt,");
					sb.append(
							":dn_nom_vol,:Bill_ton,:userid,sysdate,:userid,sysdate,'C',:transtype,:tesn_nbr,:cargoDest,");
					sql_trans = sb.toString();

					// For back-dated DN of more than x hours for cargo destination = Out of JP,
					// default the truck's gate-out time as the back-dated time. 26/5/2010.
					if (cargoDest.equals("O")) // Cargo Destination = Out of JP
						sql_trans = sql_trans
								+ "CASE WHEN TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') > SYSDATE-(SELECT VALUE/24 FROM SYSTEM_PARA WHERE PARA_CD='CL_DN') THEN NULL ELSE TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') END)";
					else
						sql_trans = sql_trans + "null)";

					// log.info("sql =="+sql+" //sql_trans :"+sql_trans);
				}

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
				paramMap.put("tesn_nbr", tesn_nbr);
				paramMap.put("cargoDest", cargoDest);

				log.info(" *** createDN SQL *****" + sql);
				log.info(" *** createDN paramMap *****" + paramMap.toString());
				countua = namedParameterJdbcTemplate.update(sql.toString(), paramMap);

				// log.info("insertion count >>>>>>>>> " + countua);

				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					log.info(" *** createDN SQL *****" + sql_trans);
					log.info(" *** createDN paramMap *****" + paramMap.toString());
					cnt_trans = namedParameterJdbcTemplate.update(sql_trans.toString(), paramMap);
					log.info("cnt_trans :" + cnt_trans);
				}

				sqlveh1 = "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values(:DNNbr,1,:veh1)";
				/*
				 * sqlveh2 = "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values('" + DNNbr +
				 * "',2,'" + veh2 + "')"; sqlveh3 =
				 * "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values('" + DNNbr + "',3,'" +
				 * veh3 + "')"; sqlveh4 =
				 * "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values('" + DNNbr + "',4,'" +
				 * veh4 + "')"; sqlveh5 =
				 * "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values('" + DNNbr + "',5,'" +
				 * veh5 + "')";
				 */
				sqlveh1_trans = "insert into dn_veh_trans(trans_nbr,DN_NBR,DN_VEH_SEQ,VEH_NO) values('0',:DNNbr,1,:veh1)";
				/*
				 * sqlveh2_trans =
				 * "insert into dn_veh_trans(trans_nbr,DN_NBR,DN_VEH_SEQ,VEH_NO) values('0','" +
				 * DNNbr + "',2,'" + veh2 + "')"; sqlveh3_trans =
				 * "insert into dn_veh_trans(trans_nbr,DN_NBR,DN_VEH_SEQ,VEH_NO) values('0','" +
				 * DNNbr + "',3,'" + veh3 + "')"; sqlveh4_trans =
				 * "insert into dn_veh_trans(trans_nbr,DN_NBR,DN_VEH_SEQ,VEH_NO) values('0','" +
				 * DNNbr + "',4,'" + veh4 + "')"; sqlveh5_trans =
				 * "insert into dn_veh_trans(trans_nbr,DN_NBR,DN_VEH_SEQ,VEH_NO) values('0','" +
				 * DNNbr + "',5,'" + veh5 + "')";
				 */
				paramMap.put("DNNbr", DNNbr);
				paramMap.put("veh1", veh1);

				if (veh1 != null && !veh1.equals("")) {
					log.info(" *** createDN SQL *****" + sqlveh1);
					log.info(" *** createDN paramMap *****" + paramMap.toString());
					namedParameterJdbcTemplate.update(sqlveh1.toString(), paramMap);
					// log.info("12121212121212 111sqlveh1 == " + sqlveh1+" //cntveh1 :"+cntveh1);
					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 24/01/2003
						log.info(" *** createDN SQL *****" + sqlveh1_trans);
						log.info(" *** createDN paramMap *****" + paramMap.toString());
						namedParameterJdbcTemplate.update(sqlveh1_trans.toString(), paramMap);
						/*
						 * log.info("13131313131313 sqlveh1_trans == " +
						 * sqlveh1_trans+" //cntveh1_trans :"+cntveh1_trans);
						 */
					}
				}
				/*
				 * if (veh2 != null && !veh2.equals("")) { int cntveh2 =
				 * stmt.executeUpdate(sqlveh2); log.info("14141414141414 sqlveh2 == " +
				 * sqlveh2+" //cntveh2 :"+cntveh2); if (logStatusGlobal.equalsIgnoreCase("Y")) {
				 * // Transaction Log Table Insertion 24/01/2003 int cntveh2_trans =
				 * stmt.executeUpdate(sqlveh2_trans);
				 * log.info("15151515151515 sqlveh2_trans == " + sqlveh2_trans +
				 * "\tcntveh2_trans == " + cntveh2_trans); } } if (veh3 != null &&
				 * !veh3.equals("")) { int cntveh3 = stmt.executeUpdate(sqlveh3);
				 * log.info("16161616161616 sqlveh3 " + sqlveh3+" //cntveh3 :"+cntveh3); if
				 * (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion
				 * 24/01/2003 int cntveh3_trans = stmt.executeUpdate(sqlveh3_trans);
				 * log.info("16161616161616 sqlveh3_trans " +
				 * sqlveh3_trans+" //cntveh3_trans :"+cntveh3_trans); } } if (veh4 != null &&
				 * !veh4.equals("")) { int cntveh4 = stmt.executeUpdate(sqlveh4);
				 * log.info("17171717171717 sqlveh4 " + sqlveh4+" //cntveh4 :"+cntveh4); if
				 * (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion
				 * 24/01/2003 int cntveh4_trans = stmt.executeUpdate(sqlveh4_trans);
				 * log.info("18181818181818 sqlveh4_trans " +
				 * sqlveh4_trans+" //cntveh4_trans :"+cntveh4_trans); } } if (veh5 != null &&
				 * !veh5.equals("")) { int cntveh5 = stmt.executeUpdate(sqlveh5);
				 * log.info("19191919191919 sqlveh5 " + sqlveh5+" //cntveh5 :"+cntveh5); if
				 * (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion
				 * 24/01/2003 int cntveh5_trans = stmt.executeUpdate(sqlveh5_trans);
				 * log.info("20202020202020 sqlveh5_trans " +
				 * sqlveh5_trans+" //cntveh5_trans :"+cntveh5_trans); } }
				 */
			} // end if count
			if (countua == 0) {

				// log.info("Writing from DNEJB.createDN");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}

			log.info("END: *** createDN Result *****" + DNNbr.toString());
		} catch (NullPointerException e) {
			log.info("Exception createDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception createDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: createDN  DAO  END");
		}
		return DNNbr;
	} // end of createUA

	@Override
	public String createShutoutDN(String edoNbr, String transtype, String edo_Nbr_Pkgs, String NomWt, String NomVol,
			String date_time, String transQty, String nric_no, String dpname, String veh1, String userid, String icType,
			String searchcrg, String tesn_nbr, String cargoDes) throws BusinessException {
		String cargoDest = "";
		if (cargoDes.equalsIgnoreCase("Vessel"))
			cargoDest = "V";
		else if (cargoDes.equalsIgnoreCase("Leased Area"))
			cargoDest = "L";
		else
			cargoDest = "O";
		String sql = "";
		String DNNbr = "";
		String edosql = "";
		String edoupdsql = "";
		String dnnbrtrans = "";
		String sqlveh1 = "";
		String sqlveh1_trans = "";
		String sql_trans = "";
		String sqltlog = "";
		java.util.StringTokenizer dntrans = null;
		int countua = 0;
		int count = 0;
		double Bill_ton = 0.0;
		double dn_nom_wt = 0;
		double dn_nom_vol = 0;
		dnnbrtrans = getDNNbr(edoNbr);
		dntrans = new java.util.StringTokenizer(dnnbrtrans, "-");
		DNNbr = (dntrans.nextToken()).trim();
		dn_nom_wt = (Double.parseDouble(transQty) / Double.parseDouble(edo_Nbr_Pkgs)) * Double.parseDouble(NomWt);
		dn_nom_vol = (Double.parseDouble(transQty) / Double.parseDouble(edo_Nbr_Pkgs)) * Double.parseDouble(NomVol);
		if ((dn_nom_wt / 1000) > dn_nom_vol) {
			Bill_ton = dn_nom_wt / 1000;
		} else {
			Bill_ton = dn_nom_vol;
		}
		StringBuffer sb = new StringBuffer();

		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		int newuanbrpkgs = 0;

		try {
			log.info("START: createShutoutDN  DAO  Start Obj " + " edoNbr:" + edoNbr + " transtype:" + transtype
					+ " edo_Nbr_Pkgs:" + edo_Nbr_Pkgs + " NomWt:" + NomWt + " NomVol:" + NomVol + " date_time:"
					+ date_time + " transQty:" + transQty + " nric_no:" + nric_no + " dpname:" + dpname + " veh1:"
					+ veh1 + " userid:" + userid + " icType:" + icType + " searchcrg:" + searchcrg + " tesn_nbr:"
					+ tesn_nbr + " cargoDes:" + cargoDes);

			edosql = "select * from gb_edo where edo_asn_nbr = :edoNbr";
			chkEDOStuffing(edoNbr);

			sb = new StringBuffer();

			sb.append("insert into dn_details(TRUCK_NBR,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, ");
			sb.append(
					"TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, ");
			sb.append("LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TESN_ASN_NBR, CRG_DEST, GATE_OUT_DTTM) VALUES(");
			// sb.append(":veh1,:DNNbr,:edoNbr,'A',:nric_no,:dpname,:icType,to_date(:date_time,'DD/MM/YYYY
			// HH24:MI'),");
			// sb.append(":transQty,:dn_nom_wt,:dn_nom_vol,:Bill_ton,:userid,sysdate,:userid,sysdate,'C',:transtype,null,:cargoDest,");
			sb.append(" :veh1 ,:DNNbr,:edoNbr,'A',");
			sql = sb.toString();
			
			StringBuffer sb1 = new StringBuffer();
			sb1.append(
					"insert into dn_details_trans(TRUCK_NBR,trans_nbr,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, ");
			sb1.append(
					"TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, ");
			sb1.append("LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TESN_ASN_NBR, CRG_DEST, GATE_OUT_DTTM) VALUES(");
			sb1.append(" :veh1 ,'0',:DNNbr,:edoNbr,'A',");
			sql_trans = sb1.toString();
			
			edoupdsql = "update gb_edo set dn_nbr_pkgs =";

			log.info(" *** createShutoutDN SQL *****" + edosql);
			paramMap.put("edoNbr", edoNbr);
			log.info(" *** createShutoutDN paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(edosql.toString(), paramMap);
			if (rs.next()) {
				newuanbrpkgs = rs.getInt("dn_nbr_pkgs");
				newuanbrpkgs = newuanbrpkgs + Integer.parseInt(transQty);
			}

			StringBuffer sbedoupd = new StringBuffer();
			sbedoupd.append(edoupdsql + newuanbrpkgs);
			sbedoupd.append(" where nbr_pkgs >= (select nvl(sum(nbr_pkgs),0)+" + transQty);
			sbedoupd.append(" from dn_details where dn_status='A' and edo_asn_nbr=:edoNbr) and edo_asn_nbr =:edoNbr ");
			edoupdsql = sbedoupd.toString();
			paramMap.put("edoNbr", edoNbr);
			log.info(" *** createShutoutDN SQL *****" + edoupdsql);
			log.info(" *** createShutoutDN paramMap *****" + paramMap.toString());
			count = namedParameterJdbcTemplate.update(edoupdsql.toString(), paramMap);
			try {
				sqltlog = "SELECT MAX(TRANS_NBR) FROM gb_edo_TRANS WHERE Edo_ASN_NBR=:edoNbr ";
				SqlRowSet rs1 = null;
				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					log.info(" *** createShutoutDN SQL *****" + sqltlog);
					log.info(" *** createShutoutDN paramMap *****" + paramMap.toString());
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sqltlog.toString(), paramMap);

					if (rs1.next()) {
					} else {
					}
				}
			} catch (Exception s2) {
				log.info("Exception createShutoutDN : ", s2);
			}
			if (count == 0) {

				log.info("Record Cannot be added to Database");
				DNNbr = "";
				throw new BusinessException("M4201");
			} else if (count > 0) {

				sql = sql + ":nric_no ,:dpname ,:icType ,to_date(:date_time,'DD/MM/YYYY HH24:MI'),:transQty ,:dn_nom_wt ,:dn_nom_vol, :Bill_ton, :userid ,sysdate, :userid,sysdate,'C',:transtype ,null , :cargoDest,";

				
				if (cargoDest.equals("O")) {
					// sb.append(sql);
					// sb.append("CASE WHEN TO_DATE('"+date_time+"','DD/MM/YYYY HH24:MI') >
					// SYSDATE-(SELECT VALUE/24 FROM SYSTEM_PARA");
					// sb.append("WHERE PARA_CD='CL_DN') THEN NULL ELSE
					// TO_DATE('"+date_time+"','DD/MM/YYYY HH24:MI') END)");
					//
					// sql = sb.toString();
					sql = sql + "CASE WHEN TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') > SYSDATE-(SELECT VALUE/24 FROM SYSTEM_PARA WHERE PARA_CD='CL_DN') THEN NULL ELSE TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') END)";
				} else {
					// sb.append(" null)");
					// sql = sb.toString();
					sql = sql + "null)";
				}

				// sb1.append("insert into
				// dn_details_trans(TRUCK_NBR,trans_nbr,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE,
				// ");
				// sb1.append("TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID,
				// ");
				// sb1.append("LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TESN_ASN_NBR,
				// CRG_DEST, GATE_OUT_DTTM) VALUES(");
				// sb1.append(":veh1,'0',:DNNbr,:edoNbr,'A',:nric_no,:dpname,:icType,to_date(:date_time,'DD/MM/YYYY
				// HH24:MI'),");
				// sb1.append(":transQty,:dn_nom_wt,:dn_nom_vol,:Bill_ton,:userid,sysdate,:userid,sysdate,'C',:transtype,null,:cargoDest,");

				sql_trans = sql_trans + " :nric_no, :dpname ,:icType ,to_date(:date_time,'DD/MM/YYYY HH24:MI'),:transQty ,:dn_nom_wt ,:dn_nom_vol ,:Bill_ton , :userid ,sysdate,:userid,sysdate,'C',:transtype,null, :cargoDest ,";
				if (cargoDest.equals("O"))
					sql_trans = sql_trans + "CASE WHEN TO_DATE( :date_time,'DD/MM/YYYY HH24:MI') > SYSDATE-(SELECT VALUE/24 FROM SYSTEM_PARA WHERE PARA_CD='CL_DN') THEN NULL ELSE TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') END)";
				else
					sql_trans = sql_trans + "null)";

				paramMap.put("veh1", veh1);
				paramMap.put("DNNbr", DNNbr);
				paramMap.put("edoNbr", edoNbr);
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
				paramMap.put("cargoDest", cargoDest);
				

				log.info(" *** createShutoutDN SQL *****" + sql);
				log.info(" *** createShutoutDN paramMap *****" + paramMap.toString());
				countua = namedParameterJdbcTemplate.update(sql.toString(), paramMap);
				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					log.info(" *** createShutoutDN SQL *****" + sql_trans);
				}

				sqlveh1 = "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values(:DNNbr,1,:veh1)";
				sqlveh1_trans = "insert into dn_veh_trans(trans_nbr,DN_NBR,DN_VEH_SEQ,VEH_NO) values('0',:DNNbr,1,:veh1)";

				paramMap.put("veh1", veh1);
				paramMap.put("DNNbr", DNNbr);
				if (veh1 != null && !veh1.equals("")) {
					log.info(" *** createShutoutDN SQL *****" + sqlveh1.toString());
					log.info(" *** createShutoutDN paramMap *****" + paramMap.toString());
					namedParameterJdbcTemplate.update(sqlveh1.toString(), paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y")) {
						log.info(" *** createShutoutDN SQL *****" + sqlveh1_trans.toString());
						log.info(" *** createShutoutDN paramMap *****" + paramMap.toString());
						namedParameterJdbcTemplate.update(sqlveh1_trans.toString(), paramMap);
					}
				}
			}
			if (countua == 0) {

				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}

			// Commented on 3/6/2016 Sripriya to nnot allow to update shutoutdelivery pkgs
			// in Booking as it is done @ Shutout Out Cargo SCreen
			// update booking_details's shutout delivery packages
			// String bookingRefNbr = getBookingRefNbr(edoNbr);
			// if (bookingRefNbr!=null && bookingRefNbr.length()>0){
			// updateBookingShutoutDeliveryPkgs(bookingRefNbr,Integer.parseInt(transQty));
			// }

			log.info("END: *** createShutoutDN Result *****" + DNNbr.toString());
		} catch (NullPointerException e) {
			log.info("Exception createShutoutDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception createShutoutDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: createShutoutDN  DAO  END");
		}
		return DNNbr;
	}

	@Override
	public int getTotalCustCdByIcNumber(String nric, String type) throws BusinessException {
		int total_cust_cd = 0;
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: getTotalCustCdByIcNumber  DAO  Start Obj " + " nric:" + nric + " type:" + type);

			if ("P".equalsIgnoreCase(type)) {
				sql = "SELECT COUNT(*) AS TOTAL_CUST_CD  FROM JC_CARDDTL WHERE (ID_NO     = :nric   OR CARD_SERIALNO = :nric OR PASSPORT_NO = :nric) AND EXPIRY_DT >= SYSDATE";
			} else {
				sql = "SELECT COUNT(*) AS TOTAL_CUST_CD FROM JC_CARDDTL WHERE (1=1) AND STATUS_CD  IN ('USE','SUS') AND (ID_NO = :nric OR FIN_NO= :nric) AND EXPIRY_DT >= SYSDATE";
			}

			log.info(" *** getTotalCustCdByIcNumber SQL *****" + sql);

			paramMap.put("nric", nric);
			log.info(" *** getTotalCustCdByIcNumber params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				total_cust_cd = rs.getInt("TOTAL_CUST_CD");
			}
			log.info("END: *** getTotalCustCdByIcNumber Result *****" + total_cust_cd);
		} catch (NullPointerException e) {
			log.info("Exception getTotalCustCdByIcNumber :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTotalCustCdByIcNumber :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTotalCustCdByIcNumber  DAO  END");

		}
		return total_cust_cd;
	}

	@Override
	public int getSpencialPackage(String edoNbr) throws BusinessException {
		int pkgs = 0;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getSpencialPackage edoNbr:" + edoNbr);
			sb.append("  select b.SPECIAL_ACTION_PKGS from gb_edo a, manifest_details b where a.mft_seq_nbr= ");
			sb.append(" b.mft_seq_nbr and a.EDO_ASN_NBR=:edoNbr and a.CRG_STATUS='L' ");

			paramMap.put("edoNbr", edoNbr);
			log.info("SQL" + sb.toString() + "pstmt:");
			log.info(" *** getSpencialPackage params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				pkgs = rs.getInt("SPECIAL_ACTION_PKGS");
			}
			log.info("END: *** getSpencialPackage Result *****" + pkgs);
		} catch (NullPointerException e) {
			log.info("Exception getSpencialPackage :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSpencialPackage :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getSpencialPackage");

		}
		return pkgs;
	}

	@Override
	public List<EdoValueObjectOps> fetchSubAdpDetails(String edoNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		List<EdoValueObjectOps> subAdpVect = new ArrayList<EdoValueObjectOps>();
		try {
			log.info("START: fetchSubAdpDetails edoNbr:" + edoNbr);
			sb.append(" select TRUCKER_IC, TRUCKER_NM from SUB_ADP where ESN_ASN_NBR =:edoNbr ");
			sb.append(" and EDO_ESN_IND = 1 and STATUS_CD ='A' order by SUB_ADP_NBR ");

			paramMap.put("edoNbr", edoNbr);
			log.info("fetchSubAdpDetails SQL" + sb.toString());
			log.info(" *** fetchSubAdpDetails params *****" + paramMap.toString());
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
			log.info("END: *** fetchSubAdpDetails Result *****" + subAdpVect.toString());
		} catch (NullPointerException e) {
			log.info("Exception fetchSubAdpDetails :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception fetchSubAdpDetails :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO fetchSubAdpDetails");

		}
		return subAdpVect;
	}

	@Override
	public List<EdoValueObjectOps> fetchDNList(String edoNbr, String searchcrg, String tesn_nbr)
			throws BusinessException {

		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		List<EdoValueObjectOps> BJDetailsVect = new ArrayList<EdoValueObjectOps>();

		boolean checkEdoStuff = chkEDOStuffing(edoNbr);
		if ("ALL".equalsIgnoreCase(searchcrg)) {

			sb.append(
					"SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')||' @ '||DECODE(PRINT_LOCATION,'C','GCO','S',SST_MACHINE_NBR,PRINT_LOCATION)||DECODE(GATE_OUT_DTTM,'','',' (VehExit: '||TO_CHAR(GATE_OUT_DTTM,'DD/MM HH24:MI')||')') as trnsDate, CNTR_NBR, GOE.LANE_ID LANE_NBR, GOE.GATE_STAFF_ID LAST_MODIFY_USER_ID, DN.TRUCK_NBR   ");
			sb.append(" FROM DN_DETAILS DN" + " LEFT JOIN " + " (");
			sb.append("   SELECT MAX(GATE_OUT_NBR) GATE_OUT_NBR, TRANS_REF    FROM GATE_OUT_EVENT_DETAILS ");
			sb.append("   GROUP BY TRANS_REF ) GOD  ON DN.DN_NBR = GOD.TRANS_REF ");
			sb.append(" LEFT JOIN GATE_OUT_EVENT GOE ON GOE.GATE_OUT_NBR = GOD.GATE_OUT_NBR");
			sb.append(" WHERE EDO_ASN_NBR=:edoNbr order by dn_nbr");

			sql = sb.toString();

		} else if ((searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT"))) {
			sb.append(
					"SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')||' @ '||DECODE(PRINT_LOCATION,'C','GCO','S',SST_MACHINE_NBR,PRINT_LOCATION)||DECODE(GATE_OUT_DTTM,'','',' (VehExit: '||TO_CHAR(GATE_OUT_DTTM,'DD/MM HH24:MI')||')') as trnsDate, CNTR_NBR, GOE.LANE_ID LANE_NBR, GOE.GATE_STAFF_ID LAST_MODIFY_USER_ID,DN.TRUCK_NBR   ");
			sb.append(" FROM DN_DETAILS DN LEFT JOIN  (");
			sb.append("   SELECT MAX(GATE_OUT_NBR) GATE_OUT_NBR, TRANS_REF   FROM GATE_OUT_EVENT_DETAILS ");
			sb.append("   GROUP BY TRANS_REF  ) GOD  ON DN.DN_NBR = GOD.TRANS_REF ");
			sb.append(" LEFT JOIN GATE_OUT_EVENT GOE ON GOE.GATE_OUT_NBR = GOD.GATE_OUT_NBR");
			sb.append(" WHERE EDO_ASN_NBR=:edoNbr and tesn_asn_nbr is null order by dn_nbr");

			sql = sb.toString();

		}

		else {
			if (checkEdoStuff) {
				sb.append(
						"SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')||' @ '||DECODE(PRINT_LOCATION,'C','GCO','S',SST_MACHINE_NBR,PRINT_LOCATION)||DECODE(GATE_OUT_DTTM,'','',' (VehExit: '||TO_CHAR(GATE_OUT_DTTM,'DD/MM HH24:MI')||')') as trnsDate, CNTR_NBR, GOE.LANE_ID LANE_NBR, GOE.GATE_STAFF_ID LAST_MODIFY_USER_ID,DN.TRUCK_NBR   ");
				sb.append(" FROM DN_DETAILS DN LEFT JOIN  (");
				sb.append("   SELECT MAX(GATE_OUT_NBR) GATE_OUT_NBR, TRANS_REF  FROM GATE_OUT_EVENT_DETAILS ");
				sb.append("   GROUP BY TRANS_REF  ) GOD ON DN.DN_NBR = GOD.TRANS_REF ");
				sb.append(" LEFT JOIN GATE_OUT_EVENT GOE ON GOE.GATE_OUT_NBR = GOD.GATE_OUT_NBR");
				sb.append(" WHERE EDO_ASN_NBR=:edoNbr and tesn_asn_nbr=:tesnNbr order by dn_nbr");

				sql = sb.toString();

			} else {
				sb.append(
						"SELECT DN_NBR,NBR_PKGS,BILLABLE_TON,DN_STATUS,BILL_STATUS,TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')||' @ '||DECODE(PRINT_LOCATION,'C','GCO','S',SST_MACHINE_NBR,PRINT_LOCATION)||DECODE(GATE_OUT_DTTM,'','',' (VehExit: '||TO_CHAR(GATE_OUT_DTTM,'DD/MM HH24:MI')||')') as trnsDate, CNTR_NBR, GOE.LANE_ID LANE_NBR, GOE.GATE_STAFF_ID LAST_MODIFY_USER_ID, DN.TRUCK_NBR  ");
				sb.append(" FROM DN_DETAILS DN LEFT JOIN  (");
				sb.append("   SELECT MAX(GATE_OUT_NBR) GATE_OUT_NBR, TRANS_REF " + "   FROM GATE_OUT_EVENT_DETAILS ");
				sb.append("   GROUP BY TRANS_REF ) GOD ON DN.DN_NBR = GOD.TRANS_REF ");
				sb.append(" LEFT JOIN GATE_OUT_EVENT GOE ON GOE.GATE_OUT_NBR = GOD.GATE_OUT_NBR");
				sb.append("  WHERE EDO_ASN_NBR=:edoNbr and tesn_asn_nbr=:tesnNbr order by dn_nbr");

				sql = sb.toString();
			}
		}
		// End Changed by Irene Tan on 20 Oct 2004 : SL-GBMS-20041020-1
		// log.info("sql fetchDNList() :"+sql);

		try {
			log.info("START: fetchDNList edoNbr:" + edoNbr + "searchcrg:" + searchcrg + "tesn_nbr:" + tesn_nbr);

			paramMap.put("edoNbr", edoNbr);
			if ("ALL".equalsIgnoreCase(searchcrg)) {
				paramMap.put("edoNbr", edoNbr);

			} else if ((searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT"))) {
				paramMap.put("edoNbr", edoNbr);
			} else {
				paramMap.put("edoNbr", edoNbr);
				paramMap.put("tesnNbr", tesn_nbr);

			}
			log.info("SQL" + sql.toString());
			log.info(" *** fetchDNList params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
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
			log.info("END: *** fetchDNList Result *****" + BJDetailsVect.toString());
		} catch (NullPointerException e) {
			log.info("Exception fetchDNList :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception fetchDNList :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO fetchDNList");

		}
		return BJDetailsVect;
	}

	@Override
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
			log.info(" *** updateWeight params *****" + paramMap.toString());
			namedParameterJdbcTemplate.update(sql.toString(), paramMap);
			log.info("END: *** updateWeight Result *****");
		} catch (NullPointerException e) {
			log.info("Exception updateWeight :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception updateWeight :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO updateWeight");

		}

	}

	public long getWeight(String cntrSeq) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		long weight = 0;
		StringBuffer sql = new StringBuffer();
		try {
			log.info("START: updateWeight cntrSeq:" + cntrSeq);
			sql.append(" select DECLR_WT from CNTR where CNTR_SEQ_NBR =:cntrSeq ");

			paramMap.put("cntrSeq", cntrSeq);
			log.info("SQL" + sql.toString() + "pstmt:");
			log.info(" *** getWeight params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				weight = rs.getLong("DECLR_WT");
			}
			log.info("END: *** getWeight Result *****" + weight);
		} catch (NullPointerException e) {
			log.info("Exception getWeight :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getWeight :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getWeight");

		}
		return weight;
	}

	@Override
	public void cancel1stDn(String cntrSeq, String cntrNbr, String user) throws BusinessException {
		StringBuffer sql = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Timestamp dttmFirst = new Timestamp(new java.util.Date().getTime());
		try {
			sql.setLength(0);
			log.info("START: cancel1stDn cntrSeq:" + cntrSeq + "cntrNbr:" + cntrNbr + "user" + user);
			sql.append(
					"INSERT INTO CNTR_TXN(CNTR_SEQ_NBR, CNTR_NBR, TXN_CD, TXN_DTTM, LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID) ");
			sql.append("VALUES( :cntrSeq, :cntrNbr, 'USTF', :dttmFirst, sysdate, :user')");

			paramMap.put("cntrSeq", cntrSeq);
			paramMap.put("cntrNbr", cntrNbr);
			paramMap.put("user", user);
			paramMap.put("dttmFirst", dttmFirst);
			log.info("SQL" + sql.toString() + "pstmt:");
			log.info(" *** cancel1stDn params *****" + paramMap.toString());
			// pstmt.setTimestamp(1, new Timestamp(new java.util.Date().getTime()));
			namedParameterJdbcTemplate.update(sql.toString(), paramMap);

		} catch (NullPointerException e) {
			log.info("Exception cancel1stDn :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception cancel1stDn :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO cancel1stDn");

		}
	}

	@Override
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
			log.info("SQL" + sql.toString() + "pstmt:");
			log.info(" *** changeStatusCntr params *****" + paramMap.toString());
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
				sql.append("UPDATE CNTR SET CAT_CD =:newCatCode, STATUS = 'E', LAST_MODIFY_DTTM = sysdate,"
						+ " LAST_MODIFY_USER_ID =:user " + "WHERE CNTR_SEQ_NBR=:cntrSeq ");
				// log.info("SQL DN:" + sql.toString() + "," + newCatCode +
				// "," + user + "," + cntrSeq);

				paramMap.put("newCatCode", newCatCode);
				paramMap.put("user", user);
				paramMap.put("cntrSeq", cntrSeq);
				log.info("SQL" + sql.toString() + "pstmt:");
				log.info(" *** changeStatusCntr params *****" + paramMap.toString());
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
	public String getNewCatCd(String cntrSeq) throws BusinessException {
		String newCatCode = "";
		try {
			log.info("START: getNewCatCd  DAO  Start Obj "+" cntrSeq:"+cntrSeq );
			// First: Get the container vo object to get required values
			ContainerValueObject cntrVo = containerDataRepo.getContainerByPrimaryKey(Long.parseLong(cntrSeq));

			// Second: Get the new cntr cat code
			String newStatus = "F".equals(cntrVo.getStatus()) ? "E" : "F";
			newCatCode = containerCommonFunctionRepo.getCntrCatCd(cntrVo.getIsoCode(), null, cntrVo.getOogOH(),
					cntrVo.getOogOlFront(), cntrVo.getOogOlBack(), cntrVo.getOogOwRight(), cntrVo.getOogOwLeft(),
					cntrVo.getReeferInd(), cntrVo.getUCInd(), cntrVo.getOverSizeInd(), cntrVo.getSpecialDetails(),
					newStatus);

			log.info("END: *** getNewCatCd Result *****" + newCatCode.toString());
			return newCatCode;
		} catch (Exception e) {
			log.info("Exception getNewCatCd :", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getNewCatCd  DAO  END");
		}

	}

	@Override
	public int checkFirstDN(String edoNbr, String cntrNo) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		int resultStf = 0;
		int resultUstf = 0;
		try {
			log.info("START: checkFirstDN edoNbr:" + edoNbr + "cntrNo:" + cntrNo);
			sb.append(" select count(*) from cntr_txn,cntr where txn_cd = 'STF'");
			sb.append(" and cntr_txn.cntr_seq_nbr = cntr.cntr_seq_nbr and cntr_txn.cntr_nbr = cntr.cntr_nbr ");
			sb.append(" and cntr.cntr_nbr =:cntrNbr and cntr.txn_status = 'A' and cntr.purp_cd = 'ST' ");
			sb.append(" and cntr.misc_app_nbr is not null ");

			paramMap.put("cntrNbr", cntrNo);
			log.info("SQL" + sb.toString() + "pstmt:");
			log.info(" *** checkFirstDN params *****" + paramMap.toString());
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
			log.info(" *** checkFirstDN params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb2.toString(), paramMap);
			while (rs.next()) {
				resultUstf = rs.getInt(1);
			}
			log.info("END: *** checkFirstDN Result *****" + resultUstf);
		} catch (NullPointerException e) {
			log.info("Exception checkFirstDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkFirstDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO checkFirstDN");

		}
		return resultStf - resultUstf;
	}

	@Override
	public String updateCntrStatus(String cntrSeq, String userID) throws BusinessException {

		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();

		int count = 0;

		try {
			log.info("START: updateCntrStatus cntrSeq:" + cntrSeq + "userID:" + userID);
			sb.append(" UPDATE CNTR SET STATUS = 'E', LAST_MODIFY_DTTM = sysdate, LAST_MODIFY_USER_ID =:userID ");
			sb.append(" WHERE CNTR_SEQ_NBR =:cntrSeq ");

			paramMap.put("userID", userID);
			paramMap.put("cntrSeq", cntrSeq);
			log.info("SQL" + sb.toString() + "pstmt:");
			log.info(" *** updateCntrStatus params *****" + paramMap.toString());
			// log.info("Before update" + edoupdsql);
			count = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			// log.info("after update====" + count);

			if (count == 0) {

				// log.info("Writing from DNEJB.cancelDN");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			log.info("END: *** updateCntrStatus Result *****" + count);
		} catch (NullPointerException e) {
			log.info("Exception updateCntrStatus :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception updateCntrStatus :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO updateCntrStatus");

		}
		return "" + count;
	}

	@Override
	public boolean countDNBalance(String cntrNbr) throws BusinessException {
		boolean bal = false;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: countDNBalance cntrNbr:" + cntrNbr);
			sb.append(" select * from dn_details where cntr_nbr=:cntrNbr and DN_STATUS='A' ");

			paramMap.put("cntrNbr", cntrNbr);
			log.info("SQL" + sb.toString() + "pstmt:");
			log.info(" *** countDNBalance params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				bal = true;
			}
			log.info("END: *** countDNBalance Result *****" + bal);
		} catch (NullPointerException e) {
			log.info("Exception countDNBalance :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception countDNBalance :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO countDNBalance");

		}
		return bal;
	}

	@Override
	public String getUaNbr(String esnNbr, int nbrPkgs, String transDttm, String dpNm, String dpIcNbr)
			throws BusinessException {
		String uaNbr = null;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

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
			log.info(" *** getUaNbr params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				uaNbr = rs.getString("ua_nbr");
			}
			log.info("END: *** getUaNbr Result *****" + uaNbr.toString());
		} catch (NullPointerException e) {
			log.info("Exception getUaNbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getUaNbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getUaNbr");

		}
		return uaNbr;
	}

	@Override
	public String cancelShutoutDN(String edoNbr, String dnNbr, String userid) throws BusinessException {
		String sql = "";
		String dnsql = "";
		String edoupdsql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String dn_sql_trans = "";
		String sqltlog = "";
		String sqltlog2 = "";
		int stransno2 = 0;
		int countua = 0;
		int count = 0;
		SqlRowSet rs1 = null;
		try {
			log.info("START: cancelShutoutDN edoNbr:" + edoNbr + "dnNbr" + dnNbr + "userid" + userid);
			sqltlog2 = "SELECT MAX(TRANS_NBR) FROM DN_DETAILS_TRANS WHERE dn_NBR=:dnNbr ";
			dnsql = "select * from dn_details where DN_NBR =:dnNbr ";

			int newuanbrpkgs = 0;

			paramMap.put("dnNbr", dnNbr);
			log.info("SQL" + dnsql.toString() + "pstmt:");
			log.info(" *** cancelShutoutDN SQL *****" + dnsql.toString());
			log.info(" *** cancelShutoutDN params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(dnsql.toString(), paramMap);

			if (rs.next()) {
				newuanbrpkgs = rs.getInt("NBR_PKGS");
			}

			edoupdsql = "update gb_edo set dn_nbr_pkgs = dn_nbr_pkgs -" + newuanbrpkgs + " where edo_asn_nbr =:edoNbr ";

			sqltlog = "SELECT MAX(TRANS_NBR) FROM gb_edo_TRANS WHERE Edo_ASN_NBR=:edoNbr ";

			paramMap.put("edoNbr", edoNbr);
			log.info("SQL" + sqltlog.toString() + "pstmt:");

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				log.info(" *** cancelShutoutDN SQL *****" + sqltlog.toString());
				log.info(" *** cancelShutoutDN params *****" + paramMap.toString());

				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqltlog.toString(), paramMap);
				if (rs1.next()) {
				} else {
				}
			}

			SqlRowSet rs3 = null;
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				log.info(" *** cancelShutoutDN SQL *****" + sqltlog2.toString());
				log.info(" *** cancelShutoutDN params *****" + paramMap.toString());

				rs3 = namedParameterJdbcTemplate.queryForRowSet(sqltlog2.toString(), paramMap);
				if (rs3.next()) {
					stransno2 = (rs3.getInt(1)) + 1;
				} else {
					stransno2 = 0;
				}
			}
			dn_sql_trans = "INSERT INTO DN_DETAILS_TRANS(TRANS_NBR,DN_NBR,DN_STATUS,LAST_MODIFY_DTTM,LAST_MODIFY_USER_ID) VALUES(";
			dn_sql_trans = dn_sql_trans + stransno2 + ",:dnNbr,'X',SYSDATE,:userid)";

			paramMap.put("userid", userid);
			paramMap.put("dnNbr", dnNbr);
			log.info("SQL" + sqltlog.toString() + "pstmt:");
			log.info(" *** cancelShutoutDN SQL *****" + edoupdsql.toString());
			log.info(" *** cancelShutoutDN params *****" + paramMap.toString());
			count = namedParameterJdbcTemplate.update(edoupdsql.toString(), paramMap);

			sql = "UPDATE DN_DETAILS SET DN_STATUS='X',LAST_MODIFY_USER_ID=:userid ,LAST_MODIFY_DTTM=SYSDATE WHERE DN_NBR =:dnNbr ";

			paramMap.put("userid", userid);
			paramMap.put("dnNbr", dnNbr);
			log.info("SQL" + sqltlog.toString() + "pstmt:");
			log.info(" *** cancelShutoutDN SQL *****" + sql.toString());
			log.info(" *** cancelShutoutDN params *****" + paramMap.toString());
			countua = namedParameterJdbcTemplate.update(sql.toString(), paramMap);

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
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
			log.info("END: *** cancelShutoutDN Result *****" + dnNbr);
		} catch (NullPointerException e) {
			log.info("Exception cancelShutoutDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception cancelShutoutDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO cancelShutoutDN");

		}
		return dnNbr;
	}

	@Override
	public String getDnCntrFirst(String cntrSeq, String cntrNbr) throws BusinessException {
		String dnFirst = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getDnCntrFirst cntrSeq:" + cntrSeq + "cntrNbr" + cntrNbr);
			int resultStf = 0;
			int resultUstf = 0;

			sb.append(" select count(*) from cntr_txn where txn_cd = 'STF' and cntr_txn.cntr_seq_nbr =:cntrSeqNbr ");

			paramMap.put("cntrSeqNbr", cntrSeq);
			log.info("SQL" + sb.toString() + "pstmt:");
			log.info(" *** getDnCntrFirst params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				resultStf = rs.getInt(1);
			}

			sb.append(" select count(*) from cntr_txn where txn_cd = 'USTF' and cntr_txn.cntr_seq_nbr =:cntrSeqNbr ");

			paramMap.put("cntrSeqNbr", cntrSeq);
			log.info("SQL" + sb.toString() + "pstmt:");
			log.info(" *** getDnCntrFirst params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				resultUstf = rs.getInt(1);
			}
			if (resultStf - resultUstf <= 0) {
				return "";
			}

			sb.append(" select dn_nbr from dn_details where edo_asn_nbr in ");
			sb.append(" (select edo_asn_nbr from edo_cntr where cntr_seq_nbr =:cntrSeqNbr) ");
			sb.append(" and dn_status = 'A' and cntr_nbr =:cntrNbr order by mot_create_dttm ASC ");

			paramMap.put("cntrSeqNbr", cntrSeq);
			paramMap.put("cntrNbr", cntrNbr);
			log.info("SQL" + sb.toString() + "pstmt:");
			log.info(" *** getDnCntrFirst params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				dnFirst = rs.getString("dn_nbr");
			}

			log.info("END: *** getDnCntrFirst Result *****" + dnFirst.toString());
		} catch (NullPointerException e) {
			log.info("Exception getDnCntrFirst :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getDnCntrFirst :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getDnCntrFirst");

		}

		return dnFirst;
	}

	@Override
	public boolean checkCancelDN(String dnNbr) throws BusinessException {
		boolean canCancel = false;
		Date dn_dttm = null;
		Date dnNextDayStart = null;
		Date today = new Date();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: checkCancelDN dnNbr:" + dnNbr);
			sb.append(" SELECT DN_CREATE_DTTM FROM DN_DETAILS WHERE DN_NBR=:dnNbr ");

			paramMap.put("dnNbr", dnNbr);
			log.info("SQL" + sb.toString() + "pstmt:");
			log.info(" *** checkCancelDN params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

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
		} catch (NullPointerException e) {
			log.info("Exception checkCancelDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkCancelDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO checkCancelDN");

		}
		return canCancel;
	}

	@Override
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
			log.info(" *** getCntrSeq SQL *****" + sb.toString());
			log.info(" *** getCntrSeq params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				cntrSeq = Integer.toString(rs.getInt(1));
			}
			log.info("END: *** getCntrSeq Result *****" + cntrSeq);
		} catch (NullPointerException e) {
			log.info("Exception getCntrSeq :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCntrSeq :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getCntrSeq");

		}
		return cntrSeq;
	}

	
	public String getDummyNumber() throws BusinessException {
		String dummyNumber = "";
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: getDummyNumber");

			sb.append("SELECT value FROM text_para WHERE para_cd = 'DUM_VEHNBR'");

			log.info("SQL" + sb.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				dummyNumber = rs.getString("VALUE");
			}

			log.info("END: *** getDummyNumber Result *****" + dummyNumber);
		} catch (NullPointerException e) {
			log.info("Exception getDummyNumber :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getDummyNumber :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getDummyNumber");

		}
		return dummyNumber;
	}

	@Override
	public boolean isValidVehicleNumber(String vehicleNumber, String companyCode) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		boolean isValid = true;
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: isValidVehicleNumber vehicleNumber:" + vehicleNumber + "companyCode:" + companyCode);

			sb.append("SELECT COUNT(*) FROM gss_veh_info WHERE veh_nbr =:vehicleNumber and status_cd ='A'");
			String dummyNumber = getDummyNumber();
			if (!(dummyNumber.equals(vehicleNumber) && "JP".equals(companyCode))) {

				paramMap.put("vehicleNumber", vehicleNumber);
				log.info("SQL" + sb.toString());
				log.info(" *** isValidVehicleNumber params *****" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

				rs.next();
				int res = rs.getInt(1);
				if (res == 0) {
					isValid = false;
				}
			}
			log.info("END: *** isValidVehicleNumber Result *****" + isValid);
		} catch (NullPointerException e) {
			log.info("Exception isValidVehicleNumber :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isValidVehicleNumber :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO isValidVehicleNumber");

		}
		return isValid;
	}

	@Override
	public void updateVehicleNo(String dnNo, String vehicleNo) throws BusinessException {

		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: updateVehicleNo dnNo:" + dnNo + "vehicleNo:" + vehicleNo);

			sb.append("UPDATE DN_DETAILS SET TRUCK_NBR=:vehicleNo WHERE DN_NBR =:dnNo");

			log.info(" *** updateVehicleNo SQL *****" + sb.toString());

			paramMap.put("vehicleNo", vehicleNo);
			paramMap.put("dnNo", dnNo);
			log.info("SQL" + sb.toString());
			log.info(" *** updateVehicleNo params *****" + paramMap.toString());
			int count = namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			log.info("END: *** updateVehicleNo Result *****" + count);
		} catch (NullPointerException e) {
			log.info("Exception updateVehicleNo :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception updateVehicleNo :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO updateVehicleNo");

		}
	}

	@Override
	public List<String[]> getCntrNbr(String edoasn) throws BusinessException {
		String sql = "";

		List<String[]> cntrNbr = new ArrayList<String[]>();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		sql = " select a.cntr_seq_nbr, b.cntr_nbr from edo_cntr a, cntr b  where a.edo_asn_nbr = :edoasn and a.cntr_seq_nbr = b.cntr_seq_nbr  and b.txn_status <> 'D'";
		try {

			log.info("START: getCntrNbr  DAO  Start edoasn " + edoasn);

			paramMap.put("edoasn", edoasn);
			log.info("getCntrNbr SQL" + sql.toString());
			log.info(" *** getCntrNbr params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next()) {
				String[] arr = new String[2];
				arr[0] = CommonUtility.deNull(rs.getString("cntr_seq_nbr"));
				arr[1] = CommonUtility.deNull(rs.getString("cntr_nbr"));
				cntrNbr.add(arr);
			}

			log.info("END: *** getCntrNbr Result *****" + cntrNbr.toString());
		} catch (NullPointerException e) {
			log.info("Exception getCntrNbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCntrNbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCntrNbr  DAO  END");

		}
		return cntrNbr;
	}

	@Override
	public boolean checkVehicleExit(String dnnbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = null;
		Date gate_out_dttm = null;
		try {
			log.info("START: checkVehicleExit  DAO  Start dnnbr " + dnnbr);

			sql = "SELECT GATE_OUT_DTTM FROM DN_DETAILS WHERE DN_NBR = :dnnbr ";

			paramMap.put("dnnbr", dnnbr);
			log.info("checkVehicleExit SQL" + sql.toString());
			log.info(" *** checkVehicleExit params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next())
				gate_out_dttm = rs.getDate("GATE_OUT_DTTM");

			log.info("END: *** checkVehicleExit Result *****" + gate_out_dttm);
		} catch (NullPointerException e) {
			log.info("Exception checkVehicleExit :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkVehicleExit :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkVehicleExit  DAO  END");

		}
		if (gate_out_dttm != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String insertTempBill(String uanbr, String tarcdser, String tardescser, double billtonsser, double urateser,
			double totchrgamtser, String actnbrser, String tarcdwf, String tardescwf, double billtonswf, double uratewf,
			double totchrgamtwf, String actnbrwf, String tarcdsr, String tardescsr, double billtonssr, double uratesr,
			double totchrgamtsr, String actnbrsr, String UserID, String esnactnbr, String tarcdsr1, String tardescsr1,
			double billtonssr1, double uratesr1, double totchrgamtsr1, String actnbrsr1, String tarcdsr2,
			String tardescsr2, double billtonssr2, double uratesr2, double totchrgamtsr2, String actnbrsr2,
			double tunitser, double tunitwhf, double tunitsr, double tunitstore, double tunitserwhf)
			throws BusinessException {

		String sql = "";
		int countua = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();

		try {
			log.info("START: insertTempBill  DAO  Start Obj " + " uanbr:" + uanbr + " tarcdser:" + tarcdser
					+ " tardescser:" + tardescser + " billtonsser:" + billtonsser + " urateser:" + urateser
					+ " totchrgamtser:" + totchrgamtser + " actnbrser:" + actnbrser + " tardescwf:" + tardescwf
					+ " billtonswf:" + billtonswf + " uratewf:" + uratewf + " totchrgamtwf:" + totchrgamtwf
					+ " actnbrwf:" + actnbrwf + " tarcdsr:" + tarcdsr + " tardescsr:" + tardescsr + " billtonssr:"
					+ billtonssr + " uratesr:" + uratesr + " totchrgamtsr:" + totchrgamtsr + " actnbrsr:" + actnbrsr
					+ " UserID:" + UserID + " esnactnbr:" + esnactnbr + " tarcdsr1:" + tarcdsr1 + " tunitser:"
					+ tunitser + " tunitwhf:" + tunitwhf + " tunitsr:" + tunitsr + " tunitstore:" + tunitstore
					+ " tunitserwhf:" + tunitserwhf);

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
			sb.append("values(:uanbr,:tarcdser,:tardescser,:billtonsser,");
			sb.append(":urateser,:totchrgamtser,:tarcdwf,:tardescwf,");
			sb.append(":billtonswf,:uratewf,:totchrgamtwf,:tarcdsr,");
			sb.append(":tardescsr,:billtonssr,:uratesr,:totchrgamtsr,");
			sb.append(":actnbrser,:actnbrwf,:actnbrsr,'WEB',:UserID,sysdate,:esnactnbr,:tarcdsr1,");
			sb.append(":tardescsr1,:billtonssr1,:uratesr1,:totchrgamtsr1,:actnbrsr1,:tarcdsr2,");
			sb.append(":tardescsr2,:billtonssr2,:uratesr2,:totchrgamtsr2,:actnbrsr2,");
			sb.append(":tunitser,:tunitwhf,:tunitsr,:tunitstore,:tunitserwhf)");

			sql = sb.toString();

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
			paramMap.put("totchrgamtsr", totchrgamtsr);
			paramMap.put("actnbrser", actnbrser);
			paramMap.put("actnbrwf", actnbrwf);
			paramMap.put("actnbrsr", actnbrsr);
			paramMap.put("UserID", UserID);
			paramMap.put("esnactnbr", esnactnbr);
			paramMap.put("tarcdsr1", tarcdsr1);
			paramMap.put("tardescsr1", tardescsr1);
			paramMap.put("billtonssr1", billtonssr1);
			paramMap.put("uratesr1", uratesr1);
			paramMap.put("totchrgamtsr1", totchrgamtsr1);
			paramMap.put("uanbr", uanbr);
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

			log.info("insertTempBill SQL" + sql.toString());
			log.info(" *** insertTempBill params *****" + paramMap.toString());
			countua = namedParameterJdbcTemplate.update(sql.toString(), paramMap);

			log.info("insertTempBill ---------------->" + countua);
			log.info("esnactnbr---->>>>" + esnactnbr);
			if (countua == 0) {
				log.info("Writing from UAEJB.insertTempBill");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}

			log.info("END: *** insertTempBill Result *****" + countua);
		} catch (NullPointerException e) {
			log.info("Exception insertTempBill :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception insertTempBill :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertTempBill  DAO  END");
		}

		return "" + countua;
	}

	@Override
	public String insertTempDNPrintOut(String strEdoNo, String DNNbr, String transtype, String searchcrg,
			String esnasnnbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = "";
		int countua = 0;
		List<EdoValueObjectOps> temptablevect = new ArrayList<EdoValueObjectOps>();
		List<EdoValueObjectOps> temptablevect_vech = new ArrayList<EdoValueObjectOps>();

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
		String date_time = "";
		String transQty = "";
		String nric_no = "";
		String ictype = "";
		String veh1 = "";
		String veh2 = "";
		String veh3 = "";
		String veh4 = "";
		String veh5 = "";
		String ttype = "";
		String dateval = "";
		String vv_cd = "";

		try {
			log.info("START: insertTempDNPrintOut  DAO  Start strEdoNo " + strEdoNo + " DNNbr" + DNNbr + " transtype"
					+ transtype + " searchcrg " + searchcrg + " esnasnnbr" + esnasnnbr);

			temptablevect = fetchShutoutDNDetail(strEdoNo, DNNbr);
			if (temptablevect == null || temptablevect.size() == 0) {
				temptablevect = fetchDNDetail(strEdoNo, DNNbr, transtype, searchcrg, esnasnnbr);
			}
			// amended end
			temptablevect_vech = getVechDetails(DNNbr);
			for (int j = 0; j < temptablevect_vech.size(); j++) {
				EdoValueObjectOps edoVo = new EdoValueObjectOps();
				edoVo = (EdoValueObjectOps) temptablevect_vech.get(j);
				veh1 = edoVo.getVech1();
				veh2 = edoVo.getVech2();
				veh3 = edoVo.getVech3();
				veh4 = edoVo.getVech4();
				veh5 = edoVo.getVech5();
			}

			for (int i = 0; i < temptablevect.size(); i++) {
				EdoValueObjectOps edoVo = new EdoValueObjectOps();
				edoVo = (EdoValueObjectOps) temptablevect.get(i);
				esnno = edoVo.getEdoAsnNbr();
				vslnm = edoVo.getVslName();
				outvoy = edoVo.getInVoyNbr();
				contno = edoVo.getCntrNo();
				// Added by SONLT
				// Check ESN whether it link with CNTR or not
				if (checkESNCntr(strEdoNo)) {
					// Get cntrNbr of this DN
					contno = getCntrNo(DNNbr);
				}
				contno = CommonUtility.deNull(contno);
				// END SONLT
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
				date_time = edoVo.getTransDate();
				transQty = edoVo.getDeliveredPkgs();
				nric_no = edoVo.getAAIcNbr();
				ictype = edoVo.getAACustCD();
				ttype = edoVo.getTransType();
				atb = edoVo.getATB();
				dateval = atb;
				// add by hujun on 26/12/2011
				if (dateval == null) {
					dateval = getVesselATBDate(edoVo.getEdoAsnNbr());
				}
				// add end
				if (ttype.equals("L"))
					ttype = "L";
				else
					ttype = "T";
				if (cod != null && !cod.equals("") && !cod.equals("null"))
					log.info("cod : " + cod);
				else
					cod = "";
				String sql_vvcd = "select v.scheme, g.var_nbr, g.ta_cust_cd, g.ta_name from vessel_call v, gb_edo g where g.var_nbr = v.vv_cd and g.edo_asn_nbr = :esnno ";

				paramMap.put("esnno", esnno);

				log.info(" insertTempDNPrintOut  DAO  SQL " + sql_vvcd.toString());
				log.info(" *** insertTempDNPrintOut params *****" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql_vvcd.toString(), paramMap);

				String taCCode = "";
				String taNmByJP = "";
				String scheme = "";
				if (rs.next()) {
					vv_cd = rs.getString("var_nbr");
					taCCode = rs.getString("ta_cust_cd");
					taNmByJP = rs.getString("ta_name");
					scheme = rs.getString("scheme");
				}

				String ab_cd = "Delivery Note must be endorsed by ";
				if (StringUtils.isBlank(taNmByJP) && StringUtils.isBlank(taCCode)) {
					if (StringUtils.isBlank(scheme)) {
						ab_cd = "";
					} else {
						ab_cd += scheme;
					}
				} else if (StringUtils.isNotBlank(taNmByJP)) {
					ab_cd += taNmByJP;
				} else {
					ab_cd += taCCode;
				}
				// End, THANHPT6, JCMS, 06/01/2016
				// added by vani start -- 27th Oct,03
				// for ab_cd
				// String sql_abcd = "SELECT DECODE(vessel_call.mixed_scheme_ind,'Y'," +
				// "(SELECT DECODE(ab_cd,NULL,''," +
				// "'Delivery Note must be endorsed BY AB Operator : '
				// ||vessel_scheme.scheme_cd) " +
				// "FROM vessel_scheme WHERE acct_nbr = " +
				// "(SELECT mixed_scheme_acct_nbr FROM manifest_details " +
				// // Added by satish on 18 Mar 2004 :Retrive Active Bl for Printing of
				// DN.:SL-GBMS-20040306-1
				// //"WHERE var_nbr='" + vv_cd + "' AND bl_nbr=" +
				// "WHERE var_nbr='" + vv_cd + "' and bl_status='A' AND bl_nbr=" +
				// "(SELECT bl_nbr FROM gb_edo WHERE edo_asn_nbr='" + esnno + "' AND " +
				// "var_nbr='" + vv_cd + "')))," +
				// "(SELECT AB_CD FROM VESSELSCHEME WHERE VV_CD='" + vv_cd +
				// "')) ab_cd " +
				// "FROM vessel_call WHERE vv_cd='" + vv_cd + "'";
				// Commended by THANHPT6 FOR JCMS 06/01/2016
				// rs1 = stmt.executeQuery(sql_abcd);
				// String ab_cd = "";
				// if (rs1.next())
				// ab_cd = CommonUtility.deNull(rs1.getString("ab_cd"));
				// rs1.close();

				// log.info("13131313 abcd: " + ab_cd);
				// added by vani end -- 27th Oct,03
				StringBuffer sb = new StringBuffer();

				sb.append(
						"Insert into webdnuatemp(DateTime,TransRefno,ATB,COD,Vslnm,voyno,contno,transtype,contsize,conttype,asnno,");
				sb.append(
						"crgref,wt,vol,declqty,transqty,balqty,nricpassportno,marking,crg_desc,veh1,veh2,veh3,veh4,veh5,vv_cd,AB_CD) ");
				sb.append(
						"values(TO_DATE(:date_time,'DD/MM/YYYY HH24:MI'),:DNNbr,TO_DATE(:dateval,'DD/MM/YYYY HH24:MI'),");
				sb.append(
						"TO_DATE(:cod,'DD/MM/YYYY HH24:MI'),:vslnm,:outvoy,:contno,:ttype,:contsize,:conttype,:esnno,:bkref");
				sb.append(",:wt,:vol,:dcpkgs,:transQty,:balpkgs, :icpassno,:markings,:crgdesc,:veh1,:veh2");
				sb.append(",:veh3,:veh4,:veh5,:vv_cd,:ab_cd)");
				sql = sb.toString();

				paramMap.put("date_time", date_time);
				paramMap.put("DNNbr", DNNbr);
				paramMap.put("dateval", dateval);
				paramMap.put("cod", cod);
				paramMap.put("vslnm", CommonUtility.addApostr(vslnm));
				paramMap.put("outvoy", CommonUtility.addApostr(outvoy));
				paramMap.put("contno", CommonUtility.addApostr(contno));
				paramMap.put("ttype", ttype);
				paramMap.put("contsize", contsize);
				paramMap.put("conttype", conttype);
				paramMap.put("esnno", esnno);
				paramMap.put("bkref", CommonUtility.addApostr(bkref));
				paramMap.put("wt", wt);
				paramMap.put("vol", vol);
				paramMap.put("dcpkgs", dcpkgs);
				paramMap.put("transQty", transQty);
				paramMap.put("balpkgs", balpkgs);
				paramMap.put("icpassno", ictype + nric_no);
				paramMap.put("ictype", ictype);
				paramMap.put("nric_no", nric_no);
				paramMap.put("markings", CommonUtility.addApostr(markings));
				paramMap.put("crgdesc", CommonUtility.addApostr(crgdesc));
				paramMap.put("veh1", CommonUtility.addApostr(veh1));
				paramMap.put("veh2", CommonUtility.addApostr(veh2));
				paramMap.put("veh3", CommonUtility.addApostr(veh3));
				paramMap.put("veh4", CommonUtility.addApostr(veh4));
				paramMap.put("veh5", CommonUtility.addApostr(veh5));
				paramMap.put("vv_cd", vv_cd);
				paramMap.put("ab_cd", ab_cd);

				log.info(" insertTempDNPrintOut  DAO  SQL " + sql.toString());
				log.info(" *** insertTempDNPrintOut params *****" + paramMap.toString());
				countua = countua + namedParameterJdbcTemplate.update(sql.toString(), paramMap);
				;
			}

			if (countua == 0) {
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}

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

	@Override
	public void purgetemptableDN(String dnnbr) throws BusinessException {
		String sql = "";
		String sql1 = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: purgetemptableDN  DAO  Start Obj " + " dnnbr:" + dnnbr);

			sql = "delete from webdnuatemp where TransRefno =:dnnbr";
			sql1 = "delete from sst_bill where print_ind = 'WEB' and dn_ua_nbr =:dnnbr";

			paramMap.put("dnnbr", dnnbr);
			log.info(" *** purgetemptableDN SQL *****" + sql);
			log.info(" *** purgetemptableDN params *****" + paramMap.toString());
			namedParameterJdbcTemplate.update(sql.toString(), paramMap);

			log.info(" *** purgetemptableDN SQL *****" + sql1);
			log.info(" *** purgetemptableDN params *****" + paramMap.toString());
			namedParameterJdbcTemplate.update(sql1.toString(), paramMap);

			log.info("END: *** purgetemptableDN Result *****");
		} catch (NullPointerException e) {
			log.info("Exception purgetemptableDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception purgetemptableDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: purgetemptableDN  DAO  END");
		}
	}

	@Override
	public List<EdoValueObjectOps> fetchDNDetail(String strEdoNo, String edoNbr, String status, String searchcrg,
			String tesn_nbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		List<EdoValueObjectOps> BJDetailsVect = new ArrayList<EdoValueObjectOps>();
		try {
			log.info("START: fetchDNDetail  DAO  Start Obj strEdoNo:" + strEdoNo + "edoNbr:" + edoNbr + "status:"
					+ status + "searchcrg:" + searchcrg + "tesn_nbr:" + tesn_nbr);

			// edoNbr stands for dnnbr
			// log.info("Writing from DnBeanBean.fetchDNDetail");
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
			// log.info("checkEdoStuff fetchDNDetail() :"+checkEdoStuff+"
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

				// log.info("881 --if (searchcrg != null &&
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
					// log.info("907 --if (chktesnJpJp_nbr.equals(Y)) in
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

			log.info(" *** fetchDNDetail SQL *****" + sb.toString());
			paramMap.put("edoNbr", edoNbr);
			paramMap.put("tesn_nbr", tesn_nbr);
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

			log.info("END: *** fetchDNDetail Result *****" + BJDetailsVect.toString());
		} catch (NullPointerException e) {
			log.info("Exception fetchDNDetail :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception fetchDNDetail :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchDNDetail  DAO  END");
		}

		return BJDetailsVect;
	}

	@Override
	public List<EdoValueObjectOps> fetchDNCreateDetail(String edoNbr, String transType, String searchcrg,
			String tesn_nbr) throws BusinessException {

		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		List<EdoValueObjectOps> BJDetailsVect = new ArrayList<EdoValueObjectOps>();

		try {
			log.info("START: fetchDNCreateDetail  DAO  Start Obj " +" edoNbr:"+edoNbr+" transType:"+transType+" searchcrg:"+searchcrg+" tesn_nbr:"+tesn_nbr );

			boolean chkEdostatus = chkEdoStatus(edoNbr);
			// log.info("chkEdostatus :"+chkEdostatus+" //edoNbr
			// :"+edoNbr+" //transType :"+transType+" //searchcrg :"+searchcrg+" //tesn_nbr
			// :"+tesn_nbr);
			if (!chkEdostatus) {
				log.info("Writing from DnBeanBean.fetchDNCreateDetail");
				log.info("EDO has been cancelled");
				throw new BusinessException("EDO has been cancelled.");
			}

			// boolean chkGbj = chkGBJ(edoNbr);
			/*
			 * if (chkGbj) { log.info("Writing from DnBeanBean.fetchDNCreateDetail" );
			 * log.info("BJ has been Closed.  No DN can be created" ); throw new
			 * BusinessException("M80002"); }
			 */

			boolean chkVvstatus = chkVVStatus(edoNbr);
			if (!chkVvstatus) {
				log.info("Writing from DnBeanBean.fetchDNCreateDetail");
				log.info("Vessel has not Berthed yet.  DN cannot be printed");
				throw new BusinessException("Vessel has not Berthed yet.  DN cannot be printed.");
			}

			/*
			 * boolean chkCargorls = chkCargoRls(edoNbr); if (!chkCargorls) { LogManager
			 * .instance.logInfo("Writing from DnBeanBean.fetchDNCreateDetail"); log.info(
			 * "Cargo needs to be release before delivery can take place"); throw new
			 * BusinessException("M80004"); }
			 */

			/*
			 * boolean chkCargostatus = chkCargoStatus(edoNbr); if (!chkCargostatus) {
			 * log.info("Writing from DnBeanBean.fetchDNCreateDetail" );
			 * log.info("No more Authorized Cargo for Delivery"); throw new
			 * BusinessException("M80005"); }
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

			log.info(" *** fetchDNCreateDetail SQL *****" + sb.toString());
			paramMap.put("edoNbr", edoNbr);
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
				cntrNo = rs.getString("CNTR_NBR"); //
				cntType = rs.getString("CNTR_TYPE"); //
				cntrSize = rs.getString("CNTR_SIZE"); //
				edoAsnNbr = rs.getString("EDO_ASN_NBR"); //
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
			log.info("END: *** fetchDNCreateDetail Result *****" + BJDetailsVect.toString());
		} catch (NullPointerException e) {
			log.info("Exception fetchDNCreateDetail :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception fetchDNCreateDetail :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchDNCreateDetail  DAO  END");

		}
		return BJDetailsVect;
	}

	public boolean chkVVStatus(String esnNbrR) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		boolean bblno = false;

		try {
			log.info("START: chkVVStatus  DAO  Start Obj " + " esnNbrR:" + esnNbrR);

			sb.append(
					"SELECT VC.VV_STATUS_IND FROM VESSEL_CALL VC,GB_EDO GB WHERE VC.VV_STATUS_IND IN('UB','BR','CL') ");
			sb.append("AND VC.VV_CD = GB.VAR_NBR AND GB.EDO_ASN_NBR =:esnNbrR ");

			log.info(" *** chkVVStatus SQL *****" + sb.toString());
			paramMap.put("esnNbrR", esnNbrR);
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

	@Override
	public List<EdoValueObjectOps> fetchShutoutDNCreateDetail(String edoNbr, String transType, String searchcrg,
			String tesn_nbr) throws BusinessException {

		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();

		List<EdoValueObjectOps> BJDetailsVect = new ArrayList<>();

		try {
			log.info("START: fetchShutoutDNCreateDetail  DAO  Start Obj " + "edoNbr:" + edoNbr + "transType:"
					+ transType + "searchcrg:" + searchcrg + "tesn_nbr:" + tesn_nbr);

			boolean chkEdostatus = chkEdoStatus(edoNbr);
			if (!chkEdostatus) {
				log.info("Writing from DnBeanBean.fetchShutoutDNCreateDetail");
				log.info("EDO has been cancelled");
				throw new BusinessException("EDO has been cancelled.");
			}

			String atbDate = getVesselATBDate(edoNbr);
			if (atbDate == null || atbDate.length() == 0) {
				log.info("Writing from DnBeanBean.fetchShutoutDNCreateDetail");
				log.info("Vessel has not Berthed yet.  DN cannot be printed");
				throw new BusinessException("Vessel has not Berthed yet.  DN cannot be printed.");
			}

			if (isEsn(null, edoNbr)) {
				sb.append(" SELECT ");
				sb.append(" TO_CHAR (SYSDATE, 'DD/MM/YYYY HH24:MI') AS TRANS_DTTM, ");
				sb.append(" VC.VSL_NM, ");
				sb.append(" VC.OUT_VOY_NBR, ");
				sb.append(
						" TO_CHAR((SELECT MIN(UA.TRANS_DTTM) FROM UA_DETAILS UA,GB_EDO EDO WHERE EDO.ESN_ASN_NBR = UA.ESN_ASN_NBR AND EDO.EDO_ASN_NBR =:edoNbr ");
				sb.append("), 'DD/MM/YYYY HH24:MI') AS FIRST_UA,  ");
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
				sb.append("), 'DD/MM/YYYY HH24:MI') AS FIRST_UA,  ");
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

			log.info(" *** fetchShutoutDNCreateDetail SQL *****" + sb.toString());
			paramMap.put("edoNbr", edoNbr);
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
			log.info("END: *** fetchShutoutDNCreateDetail Result *****" + BJDetailsVect.toString());
		} catch (NullPointerException e) {
			log.info("Exception fetchShutoutDNCreateDetail :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception fetchShutoutDNCreateDetail :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchShutoutDNCreateDetail  DAO  END");

		}
		return BJDetailsVect;
	}

	@Override
	public boolean chkEdoStatus(String esnNbrR) throws BusinessException {

		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = "";
		boolean bblno = false;

		try {

			log.info("START: chkEdoStatus  DAO  Start Obj " + " esnNbrR:" + esnNbrR);

			sql = "SELECT EDO_STATUS FROM gb_edo WHERE EDO_STATUS='A' AND EDO_ASN_NBR = :esnNbrR";

			log.info(" *** chkEdoStatus SQL *****" + sql);
			paramMap.put("esnNbrR", esnNbrR);
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
	public String getVesselATBDate(String esnNbrR) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();

		try {
			log.info("START: getVesselATBDate  DAO  Start Obj " +" esnNbrR:"+esnNbrR);

			sb.append(
					"SELECT TO_CHAR(B.ATB_DTTM,'DD/MM/YYYY HH24:MI') AS ATB_DTTM ,TO_CHAR(B.ETB_DTTM,'DD/MM/YYYY HH24:MI') AS ETB_DTTM,TO_CHAR(VC.VSL_BERTH_DTTM, ");
			sb.append("'DD/MM/YYYY HH24:MI') AS BTR_DTTM FROM VESSEL_CALL VC,GB_EDO GB,BERTHING B ");
			sb.append("WHERE  VC.VV_CD = GB.VAR_NBR AND B.VV_CD=VC.VV_CD AND GB.EDO_ASN_NBR =:esnNbrR ");

			log.info(" *** getVesselATBDate SQL *****" + sb.toString());
			paramMap.put("esnNbrR", esnNbrR);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				String atb = rs.getString("ATB_DTTM");
				String etb = rs.getString("ETB_DTTM");
				String btr = rs.getString("BTR_DTTM");
				if (atb != null) {
					log.info("END: *** getVesselATBDate Result atb*****" + atb);
					return atb;
				}
				if (etb != null) {
					log.info("END: *** getVesselATBDate Result ***** etb" + etb);
					return etb;
				}

				if (btr != null) {
					log.info("END: *** getVesselATBDate Result btr*****" + btr);
					return btr;
				}
			}

			log.info("END: *** getVesselATBDate Result *****");
		} catch (NullPointerException e) {
			log.info("Exception getVesselATBDate :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVesselATBDate :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselATBDate  DAO  END");

		}
		return null;
	}

	@Override
	public String getCntrNo(String dnNbr) throws BusinessException {

		String sql = "";
		String cntrNbr = "";
		// log.info("----------Writing from
		// DN.getCntrNo---------------");
		sql = "select cntr_nbr from dn_details where dn_nbr =:dnNbr";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getCntrNo dnNbr:" + dnNbr);

			log.info(" *** getCntrNo SQL *****" + sql);
			paramMap.put("dnNbr", dnNbr);
			log.info("SQL" + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				cntrNbr = rs.getString(1);
			}
			log.info("END: *** getCntrNo Result *****" + cntrNbr);
		} catch (NullPointerException e) {
			log.info("Exception getCntrNo :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCntrNo :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCntrNo cntrNbr:" + cntrNbr);

		}
		return cntrNbr;
	}

	@Override
	public List<EdoValueObjectOps> getVechDetails(String dnNbr) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		List<EdoValueObjectOps> vechDetails = new ArrayList<>();
		EdoValueObjectOps edoVo = new EdoValueObjectOps();
		try {
			log.info("START:  *** getVechDetails Dao Start : *** " + dnNbr);
			sql = "SELECT TRUCK_NBR FROM DN_DETAILS WHERE DN_NBR = :dnNbr";

			log.info(" *** getVechDetails SQL *****" + sql);
			paramMap.put("dnNbr", dnNbr);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			while (rs.next()) {
				edoVo.setVech1(CommonUtility.deNull(rs.getString("TRUCK_NBR")));
				/*
				 * vechSqNbr = rs.getInt("DN_VEH_SEQ"); if (vechSqNbr == 1)
				 * edoVo.setVech1(CommonUtility.deNull(rs.getString("VEH_NO"))); if (vechSqNbr
				 * == 2) edoVo.setVech2(CommonUtility.deNull(rs.getString("VEH_NO"))); if
				 * (vechSqNbr == 3)
				 * edoVo.setVech3(CommonUtility.deNull(rs.getString("VEH_NO"))); if (vechSqNbr
				 * == 4) edoVo.setVech4(CommonUtility.deNull(rs.getString("VEH_NO"))); if
				 * (vechSqNbr == 5)
				 * edoVo.setVech5(CommonUtility.deNull(rs.getString("VEH_NO")));
				 * LogManager.instance.logInfo("sql :"+sql+" //vechSqNbr :"+vechSqNbr);
				 */
				vechDetails.add(edoVo);
			} // while

			log.info("END: *** getVechDetails Result *****" + vechDetails.toString());
		} catch (NullPointerException e) {
			log.info("Exception getVechDetails :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVechDetails :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getVechDetails  END *****");
		}
		return vechDetails;
	}

	@Override
	public List<EdoValueObjectContainerised> fetchDNDetail(String edoNbr, String status, String searchcrg,
			String tesn_nbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		List<EdoValueObjectContainerised> fetchDNDetail = new ArrayList<>();

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

			log.info(" *** fetchDNDetail SQL *****" + sb.toString());
			paramMap.put("edoNbr", edoNbr);
			paramMap.put("tesnNbr", tesn_nbr);
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
	public List<EdoValueObjectOps> fetchShutoutDNDetail(String strEdoNo, String dnNo) throws BusinessException {

		List<EdoValueObjectOps> BJDetailsVect = new ArrayList<EdoValueObjectOps>();

		StringBuffer sb = new StringBuffer();
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		Map<String, String> paramMap = new HashMap<String, String>();
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

			log.info(" *** fetchShutoutDNDetail SQL *****" + sb.toString());

			paramMap.put("strEdoNo", strEdoNo);
			paramMap.put("dnNo", dnNo);
			paramMap.put("strEdoNo", strEdoNo);
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

					sb.append("SELECT CC.CO_NM, (SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) ");
					sb.append("from customer where cust_cd =:adpcustcd) TDB_CR_NBR ");
					sb.append("FROM CUSTOMER, COMPANY_CODE CC WHERE CUST_CD=CC.CO_CD and cust_cd = :adpcustcd");

					// sb.append("SELECT ");
					// sb.append(" CC.CO_NM, ");
					// sb.append(" ( ");
					// sb.append(" SELECT ");
					// sb.append(
					// " DECODE(TDB_CR_NBR, NULL, UEN_NBR, TDB_CR_NBR FROM customer WHERE cust_cd
					// =:adpcustcd) TDB_CR_NBR ");
					// sb.append(" FROM ");
					// sb.append(" CUSTOMER, ");
					// sb.append(" COMPANY_CODE CC ");
					// sb.append(" WHERE ");
					// sb.append(" CUST_CD = CC.CO_CD ");
					// sb.append(" AND cust_cd =:adpcustcd) TDB_CR_NBR ");
					// sb.append("FROM ");
					// sb.append(" CUSTOMER, ");
					// sb.append(" COMPANY_CODE CC ");
					// sb.append("WHERE ");
					// sb.append(" CUST_CD = CC.CO_CD ");
					// sb.append(" AND cust_cd =:adpcustcd");

					log.info(" *** fetchShutoutDNDetail SQL *****" + sb.toString());
					paramMap.put("adpcustcd", adpcustcd);
					log.info(" *** fetchShutoutDNDetail params *****" + paramMap.toString());
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

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

					log.info(" *** END:fetchShutoutDNDetail SQL *****" + sb.toString());
					log.info(" *** fetchShutoutDNDetail params *****" + paramMap.toString());
					rs2 = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
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
			log.info("END: *** END:fetchShutoutDNDetail Result *****" + BJDetailsVect.toString());
		} catch (NullPointerException e) {
			log.info("Exception END:fetchShutoutDNDetail :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception END:fetchShutoutDNDetail :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:fetchShutoutDNDetail");

		}
		return BJDetailsVect;
	}

	private boolean isEsn(String esnAsnNO, String edoAsnNbr) throws BusinessException {

		StringBuffer sb = new StringBuffer();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		if (esnAsnNO != null) {
			sb.append("SELECT TRANS_TYPE FROM ESN WHERE ESN_ASN_NBR=:esnAsnNO");
		} else {
			sb.append(
					"SELECT ESN.TRANS_TYPE FROM ESN ESN,GB_EDO EDO WHERE ESN.ESN_ASN_NBR=EDO.ESN_ASN_NBR AND EDO.EDO_ASN_NBR=:edoAsnNbr");
		}
		boolean isEsn = false;
		String type = "C";
		try {
			log.info("START:isEsn DAO esnAsnNO:" + esnAsnNO + "edoAsnNbr:" + edoAsnNbr);

			log.info(" *** isEsn SQL *****" + sb.toString());
			paramMap.put("esnAsnNO", esnAsnNO);
			paramMap.put("edoAsnNbr", edoAsnNbr);
			log.info("SQL" + sb.toString());
			log.info(" *** isEsn params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				type = CommonUtility.deNull(rs.getString("TRANS_TYPE"));
			}

			if (type.equals("E"))
				isEsn = true;

			log.info("END: *** isEsn Result *****" + isEsn);
		} catch (NullPointerException e) {
			log.info("Exception isEsn :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isEsn :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:isEsn ");

		}
		return isEsn;
	}

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
					|| schemeName.equals("JWP") || schemeName.equals(ProcessChargeConst.LCT_SCHEME))) {
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
	} // end of method getSchemeName

	public static Date getNextDayStart(Date date) {
		log.info("START: getNextDayStart  DAO  Start Obj "+" date:"+date );
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

	public String createDNDPE(String edoNbr, String transtype, String edo_Nbr_Pkgs, String NomWt, String NomVol,
			String date_time, String transQty, String nric_no, String dpname, String veh1, String veh2, String veh3,
			String veh4, String veh5, String userid, String icType, String searchcrg, String tesn_nbr,
			String strCntrNum, String strStuffDt, String vehNo, String tesnEsnNbr)
			throws BusinessException, RemoteException {
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
		int stransno = 0;
		int stransno1 = 0;
		int cnt_trans = 0;
		// lak added for audit trial 23/01/2003 end

		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		java.util.StringTokenizer dntrans = null;

		int countua = 0;
		int count = 0;
		double Bill_ton = 0.0;
		double dn_nom_wt = 0;
		double dn_nom_vol = 0;

		dnnbrtrans = getDNNbr(edoNbr);
		dntrans = new java.util.StringTokenizer(dnnbrtrans, "-");
		DNNbr = (dntrans.nextToken()).trim();

		log.info("DNNbr >>>>>>>>> " + DNNbr);

		dn_nom_wt = (Double.parseDouble(transQty) / Double.parseDouble(edo_Nbr_Pkgs)) * Double.parseDouble(NomWt);
		dn_nom_vol = (Double.parseDouble(transQty) / Double.parseDouble(edo_Nbr_Pkgs)) * Double.parseDouble(NomVol);

		if ((dn_nom_wt / 1000) > dn_nom_vol) {
			Bill_ton = dn_nom_wt / 1000;
		} else {
			Bill_ton = dn_nom_vol;
		}

		edosql = "select * from gb_edo where edo_asn_nbr = :edoNbr";

		String chktesnJpJp_nbr = chktesnJpJp_nbr(tesn_nbr);
		String chktesnJpPsa_nbr = chktesnJpPsa_nbr(tesn_nbr);

		if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {
			if (chktesnJpJp_nbr.equals("Y")) {
				sqlJp = "select * from tesn_jp_jp where esn_asn_nbr = :tesn_nbr";

				sqlupdJp = "update tesn_jp_jp set dn_nbr_pkgs =";

				// lak added for Audit Trial
				sqlupdJp_trans = "insert into tesn_jp_jp_trans (trans_nbr,edo_asn_nbr,esn_asn_nbr,dn_nbr_pkgs,last_modify_dttm,last_modify_user_id,remarks) values(";
				sqltlog1 = "SELECT MAX(TRANS_NBR) FROM tesn_jp_jp_TRANS WHERE esn_asn_NBR=: tesn_nbr";
			} else if (chktesnJpPsa_nbr.equals("Y")) {
				sqlJp = "select * from tesn_jp_psa where esn_asn_nbr = :tesn_nbr";
				sqlupdJp = "update tesn_jp_psa set dn_nbr_pkgs =";

				// lak added for Audit Trial
				sqlupdJp_trans = "insert into tesn_jp_psa_trans (trans_nbr,edo_asn_nbr,esn_asn_nbr,dn_nbr_pkgs,last_modify_dttm,last_modify_user_id,remarks) values(";
				sqltlog1 = "SELECT MAX(TRANS_NBR) FROM tesn_jp_psa_TRANS WHERE esn_asn_NBR=  :tesn_nbr";
			}
		}

		if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
			edoupdsql = "update gb_edo set dn_nbr_pkgs =";
			StringBuffer sb = new StringBuffer();
			sb.append("insert into dn_details(DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, ");
			sb.append(
					"TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, ");
			sb.append(
					"LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,CNTR_NBR,STUFF_DTTM,TRUCK_NBR) VALUES(:DNNbr,:edoNbr,'A',");

			sql = sb.toString();

			// laks added for audit trial
			StringBuffer sb2 = new StringBuffer();
			sb2.append(
					"insert into dn_details_trans(trans_nbr,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, ");
			sb2.append(
					"TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, ");
			sb2.append("LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TRUCK_NBR) VALUES('0',:DNNbr,:edoNbr,'A',");

			sql_trans = sb2.toString();
		} else {
			if (chktesnJpJp_nbr.equals("Y") || chktesnJpPsa_nbr.equals("Y")) {
				edoupdsql = "update gb_edo set trans_dn_nbr_pkgs =";
			} else {
				edoupdsql = "update gb_edo set dn_nbr_pkgs =";
			}
			StringBuffer sb = new StringBuffer();
			sb.append("insert into dn_details(DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, ");
			sb.append(
					"TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, ");
			sb.append(
					"LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TESN_ASN_NBR,CNTR_NBR,STUFF_DTTM,TRUCK_NBR) VALUES(:DNNbr,:edoNbr,'A',");

			sql = sb.toString();

			// laks added for audit trial
			StringBuffer sb2 = new StringBuffer();
			sb2.append(
					"insert into dn_details_trans(trans_nbr,DN_NBR,EDO_ASN_NBR,DN_STATUS,DP_IC_NBR,DP_NM,DP_IC_TYPE, ");
			sb2.append(
					"TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,DN_CREATE_LOGIN,DN_CREATE_DTTM,LAST_MODIFY_USER_ID, ");
			sb2.append(
					"LAST_MODIFY_DTTM,PRINT_LOCATION,TRANS_TYPE,TESN_ASN_NBR,TRUCK_NBR) VALUES('0',:DNNbr,:edoNbr,'A',");

			sql_trans = sb2.toString();
		}
		sqlveh1 = "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values(:DNNbr,1,:veh1)";
		sqlveh2 = "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values(:DNNbr,2,:veh2)";
		sqlveh3 = "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values(:DNNbr,3,:veh3)";
		sqlveh4 = "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values(:DNNbr ,4,:veh4)";
		sqlveh5 = "insert into dn_veh(DN_NBR,DN_VEH_SEQ,VEH_NO) values(;DNNbr,5,:veh5)";

		sqlveh1_trans = "insert into dn_veh_trans(trans_nbr,DN_NBR,DN_VEH_SEQ,VEH_NO) values('0',:DNNbr,1,:veh1)";
		sqlveh2_trans = "insert into dn_veh_trans(trans_nbr,DN_NBR,DN_VEH_SEQ,VEH_NO) values('0',:DNNbr,2,:veh2)";
		sqlveh3_trans = "insert into dn_veh_trans(trans_nbr,DN_NBR,DN_VEH_SEQ,VEH_NO) values('0',:DNNbr,4,:veh4)";
		sqlveh5_trans = "insert into dn_veh_trans(trans_nbr,DN_NBR,DN_VEH_SEQ,VEH_NO) values('0',:DNNbr,5,:veh5)";

		int newuanbrpkgs = 0;
		int newuanbrpkgs_tesn = 0;

		try {
			log.info("START: createDNDPE  DAO Obj " + "edoNbr" + edoNbr + "transtype" + transtype + "edo_Nbr_Pkgs"
					+ edo_Nbr_Pkgs + "NomWt" + NomWt + "NomVol" + NomVol + "date_time" + date_time + "transQty"
					+ transQty + "nric_no" + nric_no + "dpname" + dpname + "veh1" + veh1 + "veh2" + veh2 + "veh3" + veh3
					+ "veh4" + veh4 + "veh5" + veh5 + "userid" + userid + "icType" + icType + "searchcrg" + searchcrg
					+ "tesn_nbr" + tesn_nbr + "strCntrNum" + strCntrNum + "strStuffDt" + strStuffDt + "vehNo" + vehNo
					+ "tesnEsnNbr" + tesnEsnNbr);

			paramMap.put("edoNbr", edoNbr);
			log.info(" createDNDPE  DAO  SQL " + edosql);
			log.info(" *** createDNDPE params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(edosql.toString(), paramMap);

			if (rs.next()) {
				if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT"))
					newuanbrpkgs = rs.getInt("dn_nbr_pkgs");
				else if (chktesnJpJp_nbr.equals("Y") || chktesnJpPsa_nbr.equals("Y")) {
					newuanbrpkgs = rs.getInt("TRANS_DN_NBR_PKGS");
				} else {
					newuanbrpkgs = rs.getInt("dn_nbr_pkgs");
				}

				newuanbrpkgs = newuanbrpkgs + Integer.parseInt(transQty);
			}
			log.info("Test here");

			edoupdsql = edoupdsql + " :newuanbrpkgs where edo_asn_nbr = :edoNbr";

			log.info("@@@@@@@ edoupdsql... " + edoupdsql);

			paramMap.put("newuanbrpkgs", newuanbrpkgs);
			paramMap.put("edoNbr", edoNbr);
			log.info(" createDNDPE  DAO  SQL " + edoupdsql);
			log.info(" *** createDNDPE params *****" + paramMap.toString());
			count = namedParameterJdbcTemplate.update(edoupdsql.toString(), paramMap);

			// lak added for Audit Trail start
			sqltlog = "SELECT MAX(TRANS_NBR) FROM gb_edo_TRANS WHERE Edo_ASN_NBR= :edoNbr";

			if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table
			// Insertion 22/01/2003
			{

				paramMap.put("edoNbr", edoNbr);
				log.info(" createDNDPE  DAO  SQL " + sqltlog);
				log.info(" *** createDNDPE params *****" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog.toString(), paramMap);
				if (rs.next()) {
					stransno = (rs.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
				log.info("stransno" + stransno);
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				// count_trans = stmt.executeUpdate(edoupdsql_trans);
			}

			// lak added for Audit Trail end
			if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {

				paramMap.put("tesn_nbr", tesn_nbr);
				log.info(" createDNDPE  DAO  SQL " + sqlJp);
				log.info(" *** createDNDPE params *****" + paramMap.toString());
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqlJp.toString(), paramMap);
				if (rs1.next()) {
					newuanbrpkgs_tesn = rs1.getInt("DN_NBR_PKGS");
					newuanbrpkgs_tesn = newuanbrpkgs_tesn + Integer.parseInt(transQty);
				}
				if (tesnEsnNbr != null && !tesnEsnNbr.equals("")) {
					sqlupdJp = sqlupdJp
							+ ":newuanbrpkgs_tesn where esn_asn_nbr = :tesn_nbr and esn_asn_nbr =:tesnEsnNbr";
				} else {
					sqlupdJp = sqlupdJp + ":newuanbrpkgs_tesn where esn_asn_nbr = :tesn_nbr";
				}

				paramMap.put("newuanbrpkgs_tesn", newuanbrpkgs_tesn);
				paramMap.put("tesn_nbr", tesn_nbr);
				log.info(" createDNDPE  DAO  SQL " + sqlupdJp);
				log.info(" *** createDNDPE params *****" + paramMap.toString());
				int count1 = namedParameterJdbcTemplate.update(sqlupdJp.toString(), paramMap);
				log.info("count" + count1);
				// lak added for audit Trail
				rs2 = null;
				if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log
				// Table Insertion
				// 22/01/2003
				{

					paramMap.put("tesn_nbr", tesn_nbr);
					log.info(" createDNDPE  DAO  SQL " + sqltlog1);
					log.info(" *** createDNDPE params *****" + paramMap.toString());
					rs2 = namedParameterJdbcTemplate.queryForRowSet(sqltlog1.toString(), paramMap);
					if (rs2.next()) {
						stransno1 = (rs2.getInt(1)) + 1;
					} else {
						stransno1 = 0;
					}
				}
				sqlupdJp_trans = sqlupdJp_trans
						+ ":stransno1,:edoNbr,:tesn_nbr,:newuanbrpkgs_tesn,sysdate,:userid,'DN Add')";
				if (logStatusGlobal.equalsIgnoreCase("Y")) {

					paramMap.put("stransno1", stransno1);
					paramMap.put("edoNbr", edoNbr);
					paramMap.put("tesn_nbr", tesn_nbr);
					paramMap.put("newuanbrpkgs_tesn", newuanbrpkgs_tesn);
					paramMap.put("userid", userid);
					log.info(" createDNDPE  DAO  SQL " + sqlupdJp_trans);
					log.info(" *** createDNDPE params *****" + paramMap.toString());
					namedParameterJdbcTemplate.update(sqlupdJp_trans.toString(), paramMap);
				}

				// lak added for audit Trail
				log.info("edoupsql count >>>>>>>>> " + count);
			} else {
				if (chktesnJpPsa_nbr.equals("Y")) {
					if (tesnEsnNbr != null && !tesnEsnNbr.equals("")) {
						sqlupdJp = "update tesn_jp_psa set dn_nbr_pkgs = :transQty + (select nvl(dn_nbr_pkgs,0) from tesn_jp_psa where edo_asn_nbr = :edoNbr and esn_asn_nbr =:tesnEsnNbr) where edo_asn_nbr = :edoNbr and esn_asn_nbr = :tesnEsnNbr";
					} else {
						// update the dn_nbr_pkgs = dn_nbr_pkgs + Integer.parseInt(transQty)
						sqlupdJp = "update tesn_jp_psa set dn_nbr_pkgs =  :transQty + (select nvl(dn_nbr_pkgs,0) from tesn_jp_psa where edo_asn_nbr =:edoNbr) where edo_asn_nbr =:edoNbr";
					}

					if (tesnEsnNbr != null && !tesnEsnNbr.equals("")) {
						paramMap.put("tesnEsnNbr", tesnEsnNbr);
						paramMap.put("edoNbr", edoNbr);
						paramMap.put("transQty", Integer.parseInt(transQty));
					} else {
						paramMap.put("transQty", Integer.parseInt(transQty));
						paramMap.put("edoNbr", edoNbr);
					}

					log.info(" createDNDPE  DAO  SQL " + sqlupdJp_trans);
					log.info(" *** createDNDPE params *****" + paramMap.toString());
					namedParameterJdbcTemplate.update(sqlupdJp.toString(), paramMap);
				}
			}
			log.info("inside count 88888 \\\\---than 0000000");
			if (count == 0) {

				log.info("Writing from DNEJB.createDN edoupdsql");
				log.info(ConstantUtil.errMsg_DnBean_err01);
				throw new BusinessException(ConstantUtil.errMsg_DnBean_err01);
			} else if (count > 0) {
				log.info("inside count >>>>>>>>>than 0000000");
				if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
					sql = sql
							+ ":nric_no,:dpname,:icType,to_date(:date_time,'DD/MM/YYYY HH24:MI'),:transQty,:dn_nom_wt,:dn_nom_vol,:Bill_ton,:userid,sysdate,:userid,sysdate,'C',:transtype,:strCntrNum,to_date(:strStuffDt,'DD/MM/YYYY HH24:MI'),:vehNo)";

					// lak added for Audit trail
					StringBuffer sb = new StringBuffer();
					sb.append(":nric_no,:dpname,:icType,to_date(:date_time,'DD/MM/YYYY HH24:MI'),:transQty,");
					sb.append(
							":dn_nom_wt,:dn_nom_vol,:Bill_ton,:userid,sysdate,:userid,sysdate,'C',:transtype, :vehNo)");
					sql_trans = sql_trans + sb.toString();
					// '"+strCntrNum+"',to_date('"+strStuffDt+"','DD/MM/YYYY
					// HH24:MI'))";
					log.info("sql >>>>>>>>> " + sql);
				} else {

					StringBuffer sb = new StringBuffer();
					sb.append(
							":nric_no ,:dpname,:icType,TO_DATE(:date_time,'DD/MM/YYYY HH24:MI'),:transQty,:dn_nom_wt,:dn_nom_vol,:Bill_ton");
					sb.append(
							", :userid,sysdate,:userid,SYSDATE,'C', :transtype, :tesn_nbr, :strCntrNum, TO_DATE(:strStuffDt,'DD/MM/YYYY HH24:MI'), :vehNo)");

					sql = sql + sb.toString();

					StringBuffer sb1 = new StringBuffer();
					// lak added for Audit trail
					sb1.append(
							":nric_no ,:dpname,:icType,to_date(:date_time,'DD/MM/YYYY HH24:MI'),:transQty,:dn_nom_wt,:dn_nom_vol,:Bill_ton");
					sb1.append(", :userid,sysdate,:userid,sysdate,'C',:transtype,:tesn_nbr,:vehNo)");
					sql_trans = sql_trans + sb1.toString();
					log.info("sql >>>>>>>>> " + sql);
				}

				paramMap.put("edoNbr", edoNbr);
				paramMap.put("DNNbr", DNNbr);

				if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
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
					paramMap.put("vehNo", vehNo);
				} else {
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
					paramMap.put("tesn_nbr", tesn_nbr);
					paramMap.put("strCntrNum", strCntrNum);
					paramMap.put("strStuffDt", strStuffDt);
					paramMap.put("vehNo", vehNo);
				}
				log.info(" createDNDPE  DAO  SQL " + sql.toString());
				log.info(" *** createDNDPE params *****" + paramMap.toString());
				countua = namedParameterJdbcTemplate.update(sql.toString(), paramMap);
				log.info("insertion count >>>>>>>>> " + countua);

				if (logStatusGlobal.equalsIgnoreCase("Y")) {

					paramMap.put("edoNbr", edoNbr);
					paramMap.put("DNNbr", DNNbr);
					if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
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
						paramMap.put("vehNo", vehNo);
					} else {
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
						paramMap.put("tesn_nbr", tesn_nbr);
						paramMap.put("vehNo", vehNo);
					}
					log.info(" createDNDPE  DAO  SQL " + sql_trans);
					log.info(" *** createDNDPE params *****" + paramMap.toString());
					cnt_trans = namedParameterJdbcTemplate.update(sql_trans.toString(), paramMap);
					log.info("cnt_trans" + cnt_trans);
				}
				if (veh1 != null && !veh1.equals("")) {

					paramMap.put("veh1", veh1);
					paramMap.put("DNNbr", DNNbr);
					log.info(" createDNDPE  DAO  SQL " + sqlveh1);
					int cntveh1 = namedParameterJdbcTemplate.update(sqlveh1.toString(), paramMap);
					log.info("count" + cntveh1);
					if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log
					// Table Insertion
					// 24/01/2003
					{

						paramMap.put("veh1", veh1);
						paramMap.put("DNNbr", DNNbr);
						log.info(" createDNDPE  DAO  SQL " + sqlveh1_trans);
						log.info(" *** createDNDPE params *****" + paramMap.toString());
						namedParameterJdbcTemplate.update(sqlveh1_trans.toString(), paramMap);
					}
				}
				if (veh2 != null && !veh2.equals("")) {

					paramMap.put("veh2", veh2);
					paramMap.put("DNNbr", DNNbr);
					log.info(" createDNDPE  DAO  SQL " + sqlveh2);
					log.info(" *** createDNDPE params *****" + paramMap.toString());
					int cntveh2 = namedParameterJdbcTemplate.update(sqlveh2.toString(), paramMap);
					log.info("count" + cntveh2);
					if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log
					// Table Insertion
					// 24/01/2003
					{

						paramMap.put("veh2", veh2);
						paramMap.put("DNNbr", DNNbr);
						log.info(" createDNDPE  DAO  SQL " + sqlveh2_trans);
						log.info(" *** createDNDPE params *****" + paramMap.toString());
						int cntveh2_trans = namedParameterJdbcTemplate.update(sqlveh2_trans.toString(), paramMap);
						log.info("count" + cntveh2_trans);

					}
				}
				if (veh3 != null && !veh3.equals("")) {

					paramMap.put("veh3", veh3);
					paramMap.put("DNNbr", DNNbr);
					log.info(" createDNDPE  DAO  SQL " + sqlveh3);
					log.info(" *** createDNDPE params *****" + paramMap.toString());
					int cntveh3 = namedParameterJdbcTemplate.update(sqlveh3.toString(), paramMap);
					log.info("count" + cntveh3);

					if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log
					// Table Insertion
					// 24/01/2003
					{

						paramMap.put("veh3", veh3);
						paramMap.put("DNNbr", DNNbr);
						log.info(" createDNDPE  DAO  SQL " + sqlveh3_trans);
						log.info(" *** createDNDPE params *****" + paramMap.toString());
						int cntveh3_trans = namedParameterJdbcTemplate.update(sqlveh3_trans.toString(), paramMap);
						log.info("count" + cntveh3_trans);
					}
				}
				if (veh4 != null && !veh4.equals("")) {

					paramMap.put("veh4", veh4);
					paramMap.put("DNNbr", DNNbr);
					log.info(" createDNDPE  DAO  SQL " + sqlveh4);
					log.info(" *** createDNDPE params *****" + paramMap.toString());
					int cntveh4 = namedParameterJdbcTemplate.update(sqlveh4.toString(), paramMap);
					log.info("count" + cntveh4);
					if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log
					// Table Insertion
					// 24/01/2003
					{

						paramMap.put("veh4", veh4);
						paramMap.put("DNNbr", DNNbr);
						log.info(" createDNDPE  DAO  SQL " + sqlveh4);
						log.info(" *** createDNDPE params *****" + paramMap.toString());
						int cntveh4_trans = namedParameterJdbcTemplate.update(sqlveh4_trans.toString(), paramMap);
						log.info("count" + cntveh4_trans);
					}
				}
				if (veh5 != null && !veh5.equals("")) {

					paramMap.put("veh5", veh5);
					paramMap.put("DNNbr", DNNbr);
					log.info(" createDNDPE  DAO  SQL " + sqlveh5);
					log.info(" *** createDNDPE params *****" + paramMap.toString());
					int cntveh5 = namedParameterJdbcTemplate.update(sqlveh5.toString(), paramMap);
					log.info("count" + cntveh5);
					if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log

					// Table Insertion
					// 24/01/2003
					{

						paramMap.put("veh5", veh5);
						paramMap.put("DNNbr", DNNbr);
						log.info(" createDNDPE  DAO  SQL " + sqlveh5_trans);
						log.info(" *** createDNDPE params *****" + paramMap.toString());
						int cntveh5_trans = namedParameterJdbcTemplate.update(sqlveh5_trans.toString(), paramMap);
						log.info("count" + cntveh5_trans);
					}
				}
			}
			if (countua == 0) {

				log.info("Writing from DNEJB.createDN");
				log.info(ConstantUtil.errMsg_DnBean_err01);
				throw new BusinessException(ConstantUtil.errMsg_DnBean_err01);
			}

		} catch (BusinessException e) {
			log.info("Exception createDNDPE :", e);
			throw new BusinessException("M4201");
		} catch (NullPointerException e) {
			log.info("Exception createDNDPE :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception createDNDPE :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: createDNDPE  DAO  END");

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
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: getDNNbr  DAO  Start edoNo " + edoNo);

			if (edoNo.length() == 7) {
				edoNo = "0" + edoNo;
			}

			sql = "select max(DN_NBR) as maxDnNbr from dn_details where dn_nbr like :edoNo";

			paramMap.put("edoNo", "D" + edoNo + "%");

			log.info("SQL getDnNbr--" + sql);
			log.info(" *** getDNNbr params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			log.info("after-----excu---inside getDnNbr--" + sql);
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
				}
				DnNbr = "D" + edoNo + tempval;
				ftrans = "False";
			}

			retval = DnNbr + "-" + ftrans;
			log.info("END: *** getDNNbr Result *****" + CommonUtility.deNull(retval));
			return retval;
		} catch (NullPointerException e) {
			log.info("Exception getDNNbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getDNNbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getDNNbr  DAO  END");

		}
	}

	public boolean chkVslStat(String varno) throws BusinessException {
		String sql = "";
		boolean bvslind = false;
		sql = "SELECT GB_CLOSE_BJ_IND FROM TOPS.VESSEL_CALL WHERE GB_CLOSE_BJ_IND='Y' AND VV_CD=:varno ";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: DAO chkVslStat:" + varno);

			paramMap.put("varno", varno);
			log.info("SQL" + sql + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				bvslind = true;
			} else {
				bvslind = false;
			}

		} catch (NullPointerException e) {
			log.info("Exception chkVslStat :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkVslStat :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkVslStat DAO bvslind:" + bvslind);

		}
		return bvslind;
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String cancelDN(String edoNbr, String dnNbr, String userid, String transtype, String searchcrg,
			String tesn_nbr) throws BusinessException {
		String sql = "";

		String dnsql = "";
		String edoupdsql = "";
		String tesnupdsql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String chktesnJpJp_nbr = chktesnJpJp_nbr(tesn_nbr);
		String chktesnJpPsa_nbr = chktesnJpPsa_nbr(tesn_nbr);

		String sqlupdJp_trans = "";
		String dn_sql_trans = "";
		String sqltlog1 = "";

		String sqltlog = "";

		int stransno = 0;

		String sqltlog2 = "";

		int stransno2 = 0;
		int count_trans2 = 0;
		int stransno1 = 0;
		int count_trans1 = 0;

		sqltlog2 = "SELECT MAX(TRANS_NBR) FROM DN_DETAILS_TRANS WHERE dn_NBR=:dnNbr";

		// boolean checkjpjp = chktesnJpJp(edoNbr);
		// boolean checkjppsa = chktesnJpPsa(edoNbr);
		boolean checkEdoStuff = chkEDOStuffing(edoNbr); // vinayak added 09 jan 2004
		// log.info("checkEdoStuff cancelDN() :"+checkEdoStuff);
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

		int countua = 0;
		int count = 0;
		int count1 = 0;
		int count2 = 0;
		dnsql = "select * from dn_details where DN_NBR =:dnNbr";

		if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
			edoupdsql = "update gb_edo set last_modify_dttm = sysdate, dn_nbr_pkgs = dn_nbr_pkgs -";
			// lak added for audit trail
			// edoupdsql_trans = "insert into gb_edo_trans
			// (trans_nbr,edo_asn_nbr,last_modify_dttm,last_modify_user_id,dn_nbr_pkgs,remarks)
			// values(";
		} else {
			edoupdsql = "update gb_edo set last_modify_dttm = sysdate, trans_dn_nbr_pkgs = trans_dn_nbr_pkgs -";
			// lak added for audit trail
			// edoupdsql_trans = "insert into gb_edo_trans
			// (trans_nbr,edo_asn_nbr,last_modify_dttm,last_modify_user_id,trans_dn_nbr_pkgs,remarks)
			// values(";
		}

		int newuanbrpkgs = 0;

		try {
			log.info("START: cancelDN  DAO  Start Obj "+" edoNbr:"+edoNbr +" dnNbr:"+dnNbr +" userid:"+userid +" transtype:"+transtype
					+" searchcrg:"+searchcrg +" tesn_nbr:"+tesn_nbr );
			;
			paramMap.put("dnNbr", dnNbr);
			log.info(" *** cancelDN SQL *****" + dnsql.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(dnsql.toString(), paramMap);

			if (rs.next()) {
				newuanbrpkgs = rs.getInt("NBR_PKGS");
				// log.info("dnsql :"+dnsql+"*******vinayak newuanbrpkgs
				// :"+newuanbrpkgs);
			}

			if (checkEdoStuff) // vinayak added 09 jan 2004
			{
				String ccstuffsql = "UPDATE cc_stuffing_details SET DN_NBR_PKGS=DN_NBR_PKGS-" + newuanbrpkgs
						+ " WHERE STUFF_SEQ_NBR=:tesnNbr AND EDO_ESN_NBR=:edoNbr";
				String edostuffsql = "update gb_edo set trans_dn_nbr_pkgs = trans_dn_nbr_pkgs-" + newuanbrpkgs
						+ " where edo_asn_nbr =:edoNbr";
				String dnstuffsql = "update dn_details set dn_status='X' where dn_nbr=:dnNbr and edo_asn_nbr=:edoNbr";

				paramMap.put("tesnNbr", tesn_nbr);
				paramMap.put("edoNbr", edoNbr);
				log.info("SQL" + dnsql.toString() + "pstmt:");
				log.info(" *** cancelDN params *****" + paramMap.toString());
				count2 = namedParameterJdbcTemplate.update(ccstuffsql.toString(), paramMap);
				log.info("count" + count2);
				paramMap.put("tesnNbr", tesn_nbr);
				paramMap.put("edoNbr", edoNbr);
				log.info("SQL" + edostuffsql.toString() + "pstmt:");
				log.info(" *** cancelDN params *****" + paramMap.toString());
				int count3 = namedParameterJdbcTemplate.update(edostuffsql.toString(), paramMap);
				log.info("count" + count3);
				paramMap.put("dnNbr", dnNbr);
				paramMap.put("edoNbr", edoNbr);
				log.info("SQL" + dnstuffsql.toString() + "pstmt:");
				log.info(" *** cancelDN params *****" + paramMap.toString());
				int count4 = namedParameterJdbcTemplate.update(dnstuffsql.toString(), paramMap);
				log.info("count" + count4);
				/*
				 * (log.info("ccstuffsql cancelDN():" + ccstuffsql + " //count2 :" + count2 +
				 * " //edostuffsql :" + edostuffsql + " //count3 :" + count3 + " //dnstuffsql :"
				 * + dnstuffsql + " //count4 :" + count4);
				 */
				log.info("**************inside if of checkEDO STuff :" + dnstuffsql);
				if (chktesnJpPsa_nbr.equals("Y")) {
					// To update for TESN JP PSA
					tesnupdsql = "update tesn_jp_psa set dn_nbr_pkgs = dn_nbr_pkgs -";
					// lak added for Audit Trial
					// sqlupdJp_trans = "insert into tesn_jp_psa_trans
					// (trans_nbr,edo_asn_nbr,esn_asn_nbr,dn_nbr_pkgs,last_modify_dttm,last_modify_user_id,remarks)
					// values(";
					// sqltlog1 =
					// "SELECT MAX(TRANS_NBR) FROM tesn_jp_psa_TRANS WHERE esn_asn_NBR='" +
					// tesn_nbr + "'";

					tesnupdsql = tesnupdsql + newuanbrpkgs + " where esn_asn_nbr =:tesnNbr";
					log.info("for JP PSA1 ***:" + tesnupdsql + ":" + newuanbrpkgs + ":" + tesn_nbr);

					paramMap.put("tesnNbr", tesn_nbr);
					log.info("SQL" + tesnupdsql.toString() + "pstmt:");
					log.info(" *** cancelDN params *****" + paramMap.toString());
					int count5 = namedParameterJdbcTemplate.update(tesnupdsql.toString(), paramMap);
					log.info("count" + count5);
					tesnupdsql = "";
				}
			} else {
				edoupdsql = edoupdsql + newuanbrpkgs + " where edo_asn_nbr =:edoNbr";

				// lak added for Audit Trail start
				sqltlog = "SELECT MAX(TRANS_NBR) FROM gb_edo_TRANS WHERE Edo_ASN_NBR=:edoNbr";

				SqlRowSet rs1 = null;
				if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003

					paramMap.put("edoNbr", edoNbr);
					log.info("SQL" + dnsql.toString() + "pstmt:");
					log.info(" *** cancelDN params *****" + paramMap.toString());
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sqltlog.toString(), paramMap);
					if (rs1.next()) {
						stransno = (rs1.getInt(1)) + 1;
					} else {
						stransno = 0;
					}
				}
				log.info("stransno" + stransno);
				// edoupdsql_trans = edoupdsql_trans
				// +stransno+","+edoNbr+",sysdate,'"+userid+"',"+newuanbrpkgs+",'DN Del')";

				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					// count_trans = stmt.executeUpdate(edoupdsql_trans);
				}

				SqlRowSet rs3 = null;
				if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003

					paramMap.put("dnNbr", dnNbr);
					log.info("SQL" + dnsql.toString() + "pstmt:");
					log.info(" *** cancelDN params *****" + paramMap.toString());
					rs3 = namedParameterJdbcTemplate.queryForRowSet(sqltlog2.toString(), paramMap);
					if (rs3.next()) {
						stransno2 = (rs3.getInt(1)) + 1;
					} else {
						stransno2 = 0;
					}
				}
				dn_sql_trans = "INSERT INTO DN_DETAILS_TRANS(TRANS_NBR,DN_NBR,DN_STATUS,LAST_MODIFY_DTTM,LAST_MODIFY_USER_ID) VALUES(";
				dn_sql_trans = dn_sql_trans + stransno2 + ",:dnNbr,'X',SYSDATE,:userid)";
				// lak added for Audit Trail end

				if (searchcrg != null && !searchcrg.equals("") && !searchcrg.equals("LT")) {

					tesnupdsql = tesnupdsql + newuanbrpkgs + " where esn_asn_nbr =:esnAsnNbr";

					paramMap.put("esnAsnNbr", tesn_nbr);
					log.info("SQL" + dnsql.toString() + "pstmt:");
					log.info(" *** cancelDN params *****" + paramMap.toString());
					count1 = namedParameterJdbcTemplate.update(tesnupdsql.toString(), paramMap);
					log.info("count" + count1);
					SqlRowSet rs2 = null;
					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003

						paramMap.put("esnAsnNbr", tesn_nbr);
						log.info("SQL" + dnsql.toString() + "pstmt:");
						log.info(" *** cancelDN params *****" + paramMap.toString());
						rs2 = namedParameterJdbcTemplate.queryForRowSet(sqltlog1.toString(), paramMap);
						if (rs2.next()) {
							stransno1 = (rs2.getInt(1)) + 1;
						} else {
							stransno1 = 0;
						}
					}

					sqlupdJp_trans = sqlupdJp_trans + stransno1 + ",:edoNbr,:tesnNbr," + newuanbrpkgs
							+ ",sysdate,:userid,'DN Del')";
					if (logStatusGlobal.equalsIgnoreCase("Y")) {

						paramMap.put("edoNbr", edoNbr);
						paramMap.put("tesnNbr", tesn_nbr);
						paramMap.put("userid", userid);
						log.info("SQL" + dnsql.toString() + "pstmt:");
						log.info(" *** cancelDN params *****" + paramMap.toString());
						count_trans1 = namedParameterJdbcTemplate.update(sqlupdJp_trans.toString(), paramMap);
						log.info("count" + count_trans1);
					}

				}

				paramMap.put("edoNbr", edoNbr);
				log.info("SQL" + edoupdsql.toString() + "pstmt:");
				log.info(" *** cancelDN params *****" + paramMap.toString());
				count = namedParameterJdbcTemplate.update(edoupdsql.toString(), paramMap);

				// count :" + count + " //count_trans1 :" + count_trans1);
				sql = "UPDATE DN_DETAILS SET DN_STATUS='X',LAST_MODIFY_USER_ID=:userid,LAST_MODIFY_DTTM=SYSDATE WHERE DN_NBR =:dnNbr";

				paramMap.put("dnNbr", dnNbr);
				paramMap.put("userid", userid);
				log.info("SQL" + sql.toString() + "pstmt:");
				log.info(" *** cancelDN params *****" + paramMap.toString());
				countua = namedParameterJdbcTemplate.update(sql.toString(), paramMap);

				if (logStatusGlobal.equalsIgnoreCase("Y")) {

					paramMap.put("dnNbr", dnNbr);
					paramMap.put("userid", userid);
					log.info("SQL" + dnsql.toString() + "pstmt:");
					log.info(" *** cancelDN params *****" + paramMap.toString());
					count_trans2 = namedParameterJdbcTemplate.update(dn_sql_trans.toString(), paramMap);
					log.info("count" + count_trans2);
				}
				if (countua == 0 || count == 0) {

					log.info(ConstantUtil.errMsg_DnBean_err01);
					throw new BusinessException(ConstantUtil.errMsg_DnBean_err01);
				}

			}
			
			log.info("END: *** cancelDN Result *****" + count);
		} catch (NullPointerException e) {
			log.info("Exception cancelDN :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception cancelDN :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO cancelDN");

		}
		return "" + count;
	}

	/**
	 * This method retrieves the list of EDO.
	 * 
	 * @param edoNbr    EDO ASN No
	 * @param searchCrg Search Indicator (LT - Local/TS Delivered Locally, T - TS)
	 * @exception RemoteException, BusinessException
	 */
	private List<String> fetchEdoList(String edoNbr, String searchcrg) throws RemoteException, BusinessException {

		String sql = "";
		List<String> edoVect = new ArrayList<String>();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		if (edoNbr != null && !edoNbr.equals("")) {
			if (edoNbr.substring(0, 2).equals("00")) {
				if (edoNbr.length() > 0)
					edoNbr = edoNbr.substring(2);
			} else if (edoNbr.substring(0, 1).equals("0")) {
				if (edoNbr.length() > 0)
					edoNbr = edoNbr.substring(1);
			}
		}
		// Amend by HuJianPing on 20 June 2011 - begin
		if (searchcrg != null && !"".endsWith(searchcrg) && searchcrg.endsWith("SL")) {

			sb.append("SELECT ESN_ASN_NBR AS esn,edo_asn_nbr, nbr_pkgs AS nbr_pkgs, crg_status, ");
			sb.append("NVL (dn_nbr_pkgs, 0) + NVL (trans_dn_nbr_pkgs, 0) AS dnpkgs FROM gb_edo ");
			sb.append(
					" WHERE edo_asn_nbr like :edoNbr AND edo_status = 'A' and ESN_ASN_NBR is not null and SHUTOUT_IND='Y'");
			sql = sb.toString();

		} else {

			sql = "select  a.edo_asn_nbr from gb_edo a, manifest_details b, vessel_call c where EDO_ASN_NBR like :edoNbr and a.mft_seq_nbr = b.mft_seq_nbr and edo_status = 'A' and a.var_nbr = c.vv_cd  and vv_status_ind in ('BR','UB','CL') and a.dis_type in ('D','N') order by a.edo_asn_nbr desc";
		}
		// Amend By HuJianPing on 20 June 2011 - end
		try {
			log.info("START: fetchEdoList  DAO  Start Obj " + " edoNbr:" + edoNbr + " searchcrg:" + searchcrg);

			if (searchcrg != null && !"".endsWith(searchcrg) && searchcrg.endsWith("SL")) {
				paramMap.put("edoNbr", edoNbr + "%");
			} else {
				paramMap.put("edoNbr", edoNbr.trim() + "%");

			}
			log.info(" fetchEdoList  DAO  SQL " + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
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
			log.info(" fetchEdoList  DAO  Result" + edoVect.toString());
		} catch (NullPointerException e) {
			log.info("Exception fetchEdoList :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception fetchEdoList :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchEdoList  DAO  END");

		}
		return edoVect;

	}

	/**
	 * This method retrieves the list of EDOs for DN creation.
	 *
	 * @param edoNbr    EDO ASN No
	 * @param compCode  Completion Code
	 * @param searchCrg Search Indicator (LT - Local/TS Delivered Locally, T - TS)
	 * @exception RemoteException, BusinessException
	 */
	// ejb.sessionBeans.gbms.containerised.dn
	public List<EdoValueObjectContainerised> fetchEdo(String edoNbr, String compCode, String searchcrg)
			throws RemoteException, BusinessException {

		String sql = "";
		List<EdoValueObjectContainerised> edoVect = new ArrayList<EdoValueObjectContainerised>();
		// Vector edoRowVect = new Vector();
		// Vector edoTempVect = new Vector();
		List<String> edoAsnNbr = new ArrayList<String>();
		edoAsnNbr = fetchEdoList(edoNbr, searchcrg);
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: fetchEdo  DAO  Start Obj " + " edoNbr:" + edoNbr + " compCode:" + compCode + " searchcrg:"
					+ searchcrg);

			for (int i = 0; i < edoAsnNbr.size(); i++) {
				String edoasnNbr = (String) (edoAsnNbr).get(i);
				boolean checkEdoStuff = chkEDOStuffing(edoasnNbr); // vinayak added 07 jan 2004
				boolean checkjpjp = chktesnJpJp(edoasnNbr);
				boolean checkjppsa = chktesnJpPsa(edoasnNbr);
				if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
					// if(!checkjpjp && !checkjppsa){
					// Begin ThanhPT6 SGS 25 Sep 2015: add d.MFT_SEQ_NBR, d.VAR_NBR to sql
					// sql = "select a.edo_asn_nbr,a.nbr_pkgs as nbr_pkgs,a.bl_nbr
					// ,a.crg_status,nvl(d.NBR_PKGS_IN_PORT,0) as nbrPkgsPort,a.nbr_pkgs -
					// a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0) -
					// nvl(a.dn_nbr_pkgs,0)-nvl(a.CUT_OFF_NBR_PKGS,0) as
					// balance,nvl(a.dn_nbr_pkgs,0)+nvl(trans_dn_nbr_pkgs,0) as
					// dnPkgs,nvl(a.CUT_OFF_NBR_PKGS,0) as cutoffPkgs, nom_wt, NOM_VOL from gb_edo
					// a,manifest_details d where EDO_ASN_NBR ='" + edoasnNbr + "' and a.mft_seq_nbr
					// = d.mft_seq_nbr and a.edo_status='A'";
					// sql = "select a.edo_asn_nbr,a.nbr_pkgs as nbr_pkgs,a.bl_nbr
					// ,a.crg_status,nvl(d.NBR_PKGS_IN_PORT,0) as nbrPkgsPort,a.nbr_pkgs -
					// a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0) -
					// nvl(a.dn_nbr_pkgs,0)-nvl(a.CUT_OFF_NBR_PKGS,0) as
					// balance,nvl(a.dn_nbr_pkgs,0)+nvl(trans_dn_nbr_pkgs,0) as
					// dnPkgs,nvl(a.CUT_OFF_NBR_PKGS,0) as cutoffPkgs, nom_wt, NOM_VOL,
					// d.MFT_SEQ_NBR, d.VAR_NBR, vc.terminal, vc.scheme, vc.combi_gc_scheme,
					// vc.combi_gc_ops_ind from vessel_call vc, gb_edo a,manifest_details d where
					// EDO_ASN_NBR = :edoasnNbr and a.var_nbr = vc.vv_cd and a.mft_seq_nbr =
					// d.mft_seq_nbr and a.edo_status='A'";
					// End ThanhPT6 SGS 25 Sep 2015

					sql = "select  a.edo_asn_nbr,a.nbr_pkgs as nbr_pkgs,a.bl_nbr ,a.crg_status,nvl(d.NBR_PKGS_IN_PORT,0) as nbrPkgsPort,a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0)-nvl(a.CUT_OFF_NBR_PKGS,0) as balance,nvl(a.dn_nbr_pkgs,0)+nvl(trans_dn_nbr_pkgs,0) as dnPkgs,nvl(a.CUT_OFF_NBR_PKGS,0) as cutoffPkgs from gb_edo a,manifest_details d where EDO_ASN_NBR = :edoasnNbr and a.mft_seq_nbr = d.mft_seq_nbr and a.edo_status='A'";

					paramMap.put("edoasnNbr", edoasnNbr);
					log.info(" fetchEdo  DAO  SQL " + sql);
					log.info(" fetchEdo  DAO  paramMap " + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
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
						/*
						 * edoVo.setNomWt(CommonUtility.deNull(rs.getString("nom_wt")));
						 * edoVo.setNomVol(CommonUtility.deNull(rs.getString("NOM_VOL"))); // Begin
						 * ThanhPT6 SGS 25 Sep 2015 get more field: d.MFT_SEQ_NBR, d.VAR_NBR
						 * edoVo.setMft_seq_nbr(CommonUtility.deNull(rs.getString("MFT_SEQ_NBR")));
						 * edoVo.setVar_nbr(CommonUtility.deNull(rs.getString("VAR_NBR"))); // End
						 * ThanhPT6 SGS 25 Sep 2015
						 * edoVo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
						 * edoVo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
						 * edoVo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
						 * edoVo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")))
						 * ; edoVect.add(edoVo);
						 */
					}
					// }
				} else {
					if (checkjpjp) {
						// Begin FPT modify to includes Local EDO (crg_status = 'L'), just need to have
						// record in tesn_jp_jp
						// sql = "select
						// vsl_nm,v.out_voy_nbr,c.esn_asn_nbr,a.edo_asn_nbr,c.nbr_pkgs,a.bl_nbr
						// ,a.crg_status,nvl(c.DN_NBR_PKGS,0) as dnPkgs,c.nbr_pkgs -
						// nvl(c.DN_NBR_PKGS,0) as balance from gb_edo a,vessel_call v,tesn_jp_jp c,esn
						// e where " +
						// "v.vv_cd = e.out_voy_var_nbr and c.EDO_ASN_NBR = a.EDO_ASN_NBR and
						// c.EDO_ASN_NBR ='" +
						// edoasnNbr + "' and a.crg_status in('T','R') and a.edo_status='A' and
						// e.esn_asn_nbr =c.esn_asn_nbr and e.ESN_STATUS ='A'";

						/*
						 * sb.append("SELECT "); sb.append("	vsl_nm, ");
						 * sb.append("	v.out_voy_nbr, "); sb.append("	c.esn_asn_nbr, ");
						 * sb.append("	a.edo_asn_nbr, "); sb.append("	c.nbr_pkgs, ");
						 * sb.append("	a.bl_nbr , "); sb.append("	a.crg_status, ");
						 * sb.append("	nvl(c.DN_NBR_PKGS, 0) AS dnPkgs, ");
						 * sb.append("	c.nbr_pkgs - nvl(c.DN_NBR_PKGS, 0) AS balance, ");
						 * sb.append("	c.nom_wt, "); sb.append("	c.NOM_VOL, ");
						 * sb.append("	bk.trans_crg, "); sb.append("	v.terminal, ");
						 * sb.append("	v.scheme, "); sb.append("	v.combi_gc_scheme, ");
						 * sb.append("	v.combi_gc_ops_ind "); sb.append("FROM ");
						 * sb.append("	gb_edo a, "); sb.append("	vessel_call v, ");
						 * sb.append("	tesn_jp_jp c, "); sb.append("	esn e, ");
						 * sb.append("	bk_details bk "); sb.append("WHERE ");
						 * sb.append("	v.vv_cd = e.out_voy_var_nbr ");
						 * sb.append("	AND c.EDO_ASN_NBR = a.EDO_ASN_NBR ");
						 * sb.append("	AND c.EDO_ASN_NBR = :edoasnNbr ");
						 * sb.append("	AND a.crg_status IN('L', "); sb.append("	'T', ");
						 * sb.append("	'R') "); sb.append("	AND a.edo_status = 'A' ");
						 * sb.append("	AND e.esn_asn_nbr = c.esn_asn_nbr ");
						 * sb.append("	AND e.ESN_STATUS = 'A' ");
						 * sb.append("	AND e.bk_ref_nbr = bk.bk_ref_nbr"); sql = sb.toString();
						 */
						// End FPT modify to includes Local EDO (crg_status = 'L'), just need to have
						// record in tesn_jp_jp

						sql = "select vsl_nm,v.out_voy_nbr,c.esn_asn_nbr,a.edo_asn_nbr,c.nbr_pkgs,a.bl_nbr ,a.crg_status,nvl(c.DN_NBR_PKGS,0) as dnPkgs,c.nbr_pkgs - nvl(c.DN_NBR_PKGS,0) as balance from gb_edo a,vessel_call v,tesn_jp_jp c,esn e where "
								+ "v.vv_cd = e.out_voy_var_nbr and c.EDO_ASN_NBR = a.EDO_ASN_NBR and c.EDO_ASN_NBR = :edoasnNbr and a.crg_status in('T','R') and a.edo_status='A' and e.esn_asn_nbr =c.esn_asn_nbr and e.ESN_STATUS ='A'";

						paramMap.put("edoasnNbr", edoasnNbr);
						log.info(" fetchEdo  DAO  SQL " + sql);
						log.info(" fetchEdo  DAO  paramMap " + paramMap.toString());
						rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
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
							/*
							 * edoVo.setNomWt(CommonUtility.deNull(rs.getString("nom_wt")));
							 * edoVo.setNomVol(CommonUtility.deNull(rs.getString("NOM_VOL")));
							 * edoVo.setTransCrg(CommonUtility.deNull(rs.getString("trans_crg")));
							 * edoVo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
							 * edoVo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
							 * edoVo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
							 * edoVo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")))
							 * ;
							 */
							edoVect.add(edoVo);
						}
					}
					if (checkjppsa) {
						/*
						 * // Condition removed to check bk_details fix Sripriya 8 Dec 2014 StringBuffer
						 * sb1 = new StringBuffer(); sb1.append("SELECT ");
						 * sb1.append("	SECOND_CAR_VES_NM, "); sb1.append("	SECOND_CAR_VOY_NBR, ");
						 * sb1.append("	c.esn_asn_nbr, "); sb1.append("	a.edo_asn_nbr, ");
						 * sb1.append("	c.nbr_pkgs, "); sb1.append("	a.bl_nbr , ");
						 * sb1.append("	a.crg_status, ");
						 * sb1.append("	nvl(c.DN_NBR_PKGS, 0) AS dnPkgs, ");
						 * sb1.append("	c.nbr_pkgs - nvl(c.DN_NBR_PKGS, 0) AS balance, ");
						 * sb1.append("	a.nom_wt, "); sb1.append("	a.NOM_VOL, ");
						 * sb1.append("	vc.terminal, "); sb1.append("	vc.scheme, ");
						 * sb1.append("	vc.combi_gc_scheme, "); sb1.append("	vc.combi_gc_ops_ind ");
						 * sb1.append("FROM "); sb1.append("	gb_edo a, ");
						 * sb1.append("	vessel_call vc, "); sb1.append("	tesn_jp_psa c, ");
						 * sb1.append("	esn e "); sb1.append("WHERE ");
						 * sb1.append("	c.EDO_ASN_NBR = a.EDO_ASN_NBR ");
						 * sb1.append("	AND c.EDO_ASN_NBR = :edoasnNbr ");
						 * sb1.append("	AND a.var_nbr = vc.vv_cd ");
						 * sb1.append("	AND a.crg_status IN('T', "); sb1.append("	'R') ");
						 * sb1.append("	AND a.edo_status = 'A' ");
						 * sb1.append("	AND e.esn_asn_nbr = c.esn_asn_nbr ");
						 * sb1.append("	AND e.ESN_STATUS = 'A'"); sql = sb1.toString();
						 */

						sql = "select SECOND_CAR_VES_NM,SECOND_CAR_VOY_NBR,c.esn_asn_nbr,a.edo_asn_nbr,c.nbr_pkgs,a.bl_nbr ,a.crg_status,nvl(c.DN_NBR_PKGS,0) as dnPkgs,c.nbr_pkgs - nvl(c.DN_NBR_PKGS,0) as balance from gb_edo a,tesn_jp_psa c,esn e where "
								+ "c.EDO_ASN_NBR = a.EDO_ASN_NBR and c.EDO_ASN_NBR =:edoasnNbr and a.crg_status in('T','R') and a.edo_status='A' and e.esn_asn_nbr =c.esn_asn_nbr and e.ESN_STATUS ='A'";

						paramMap.put("edoasnNbr", edoasnNbr);
						log.info(" fetchEdo  DAO  SQL " + sql);
						log.info(" fetchEdo  DAO  paramMap " + paramMap.toString());
						rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
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
							/*
							 * edoVo.setNomWt(CommonUtility.deNull(rs.getString("nom_wt")));
							 * edoVo.setNomVol(CommonUtility.deNull(rs.getString("NOM_VOL")));
							 * edoVo.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
							 * edoVo.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
							 * edoVo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
							 * edoVo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")))
							 * ;
							 */
							// edoVo.setTrans_crg(CommonUtility.deNull(rs.getString("trans_crg")));
							edoVect.add(edoVo);
						}
					}

				}

			}
			log.info(" fetchEdo  DAO  Result" + edoVect.toString());
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

	/**
	 * This method used to check tesnJp or PSA
	 * 
	 * @param String edoNbr
	 * @return boolean tesnjppsa
	 * @exception RemoteException
	 * @exception BusinessException
	 */

	public boolean chktesnJpPsa(String edoNbr) throws BusinessException, RemoteException {

		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		boolean tesnjppsa = false;
		sql = "select * from tesn_jp_psa jppsa,esn e where e.esn_asn_nbr = jppsa.esn_asn_nbr and e.esn_status ='A' and EDO_ASN_NBR = :edoNbr";
		try {
			log.info("START: chktesnJpPsa  DAO  Start Obj " + edoNbr);

			paramMap.put("edoNbr", edoNbr);
			log.info(" chktesnJpPsa  SQL " + sql);
			log.info(" chktesnJpPsa  DAO  paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

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
	}

	@Override
	public boolean checkESNCntr(String edoasn) throws BusinessException {
		boolean esnCntr = false;
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		// int count = 0;
		SqlRowSet rs = null;
		try {
			log.info("START: checkESNCntr edoasn:" + edoasn);
			sb.append(" select a.cntr_seq_nbr, b.cntr_nbr from edo_cntr a, cntr b ");
			sb.append(" where a.edo_asn_nbr =:edoAsnNbr ");
			sb.append(" and a.cntr_seq_nbr = b.cntr_seq_nbr ");
			sb.append(" and b.txn_status <> 'D' and b.txn_status <> 'I' ");
			paramMap.put("edoAsnNbr", edoasn);
			log.info("SQL: checkESNCntr:" + sb.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				esnCntr = true;
			} else {
				esnCntr = false;
			}
			
			log.info("END: *** checkESNCntr Result *****" + esnCntr);
		} catch (NullPointerException e) {
			log.info("Exception: checkESNCntr ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: checkESNCntr ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:  checkESNCntr");
		}
		return esnCntr;
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
			log.info("SQL" + sql.toString() + "pstmt:");
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
			log.info("END: *** chktesnJpPsa_nbr Result *****" + CommonUtility.deNull(tesnjpjp));
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
	public boolean chkEDOStuffing(String edoNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String strQuery = "";
		boolean edoExst = false;

		try {
			log.info("START: chkEDOStuffing edoNbr:" + edoNbr);
			strQuery = "SELECT distinct EDO_ESN_NBR FROM cc_stuffing_details WHERE EDO_ESN_IND='EDO' AND EDO_ESN_NBR=:edoNbr AND REC_STATUS='A'";

			paramMap.put("edoNbr", edoNbr);
			log.info("SQL" + strQuery.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(strQuery.toString(), paramMap);

			if (rs.next()) {
				edoExst = true;
			}
			log.info("END: *** chkEDOStuffing Result *****" + edoExst);
		} catch (NullPointerException e) {
			log.info("Exception chkEDOStuffing :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkEDOStuffing :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO chkEDOStuffing");

		}
		// log.info("edoExst :"+edoExst);
		return edoExst;
	}

	/**
	 * This method retrieves the EDO details for display.
	 *
	 * @param edoNbr    EDO ASN No
	 * @param searchCrg Search Indicator (LT - Local/TS Delivered Locally, T - TS)
	 * @param tesnnbr   TESN ASN No
	 * @exception RemoteException, BusinessException
	 */
	public List<EdoValueObjectOps> fetchEdoDetails(String edoNbr, String searchcrg, String tesnnbr)
			throws BusinessException {

		String sql = "";
		String sql1 = "";
		List<EdoValueObjectOps> BJDetailsVect = new ArrayList<EdoValueObjectOps>();

		StringBuffer sb = new StringBuffer();

		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
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
			 * ; sb.
			 * append(" d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
			 * ; sb.
			 * append(" b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, to_char(c.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
			 * ; sb.
			 * append(" a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0)+nvl(a.trans_dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e where "
			 * ; sb.
			 * append(" a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and a.EDO_ASN_NBR ="
			 * + edoNbr.trim();
			 */

			/*
			 * sql =
			 * "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,"
			 * ; sb.
			 * append(" d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
			 * ; sb.
			 * append(" b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,f.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
			 * ; sb.
			 * append(" a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0)+nvl(a.trans_dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,cc_unstuff_manifest f where "
			 * ; sb.
			 * append(" a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr AND d.unstuff_seq_nbr=f.UNSTUFF_SEQ_NBR(+) and a.EDO_ASN_NBR ="
			 * + edoNbr.trim();
			 */

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
				/*
				 * sql =
				 * "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,"
				 * ; sb.
				 * append(" d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
				 * ; sb.
				 * append(" b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, to_char(c.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
				 * ; sb.
				 * append(" a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,"
				 * ; sb.
				 * append(" f.NBR_PKGS as jpjpnpkg,f.DN_NBR_PKGS as jpjpdn_npkg,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,tesn_jp_jp f where"
				 * ; sb.
				 * append(" a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr = '"
				 * + tesnnbr + "' and a.EDO_ASN_NBR =" + edoNbr.trim();
				 */

				/*
				 * sql =
				 * "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,"
				 * ; sb.
				 * append(" d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
				 * ; sb.
				 * append(" b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,g.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
				 * ; sb.
				 * append(" a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,"
				 * ; sb.
				 * append(" f.NBR_PKGS as jpjpnpkg,f.DN_NBR_PKGS as jpjpdn_npkg,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,tesn_jp_jp f, cc_unstuff_manifest g where"
				 * ; sb.
				 * append(" a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and d.unstuff_seq_nbr=g.UNSTUFF_SEQ_NBR(+) AND f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr = '"
				 * + tesnnbr + "' and a.EDO_ASN_NBR =" + edoNbr.trim();
				 */

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

				// log.info("sql :"+sql);
			} else if (chktesnJpPsa_nbr.equals("Y")) {
				/*
				 * sql =
				 * "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,"
				 * ; sb.
				 * append(" d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
				 * ; sb.
				 * append(" b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, to_char(c.gb_COD_DTTM , 'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
				 * ; sb.
				 * append(" a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,"
				 * ; sb.
				 * append(" f.NBR_PKGS as jppsanpkg,f.DN_NBR_PKGS as jppsadn_npkg,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,tesn_jp_psa f where"
				 * ; sb.
				 * append(" a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr = '"
				 * + tesnnbr + "' and a.EDO_ASN_NBR =" + edoNbr.trim();
				 */
				/*
				 * sql =
				 * "select a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,"
				 * ; sb.
				 * append(" d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,"
				 * ; sb.
				 * append(" b.vsl_nm , b.in_voy_nbr ,to_char(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') as atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,g.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') as cod,nvl(d.CRG_DES,' '), nvl(e.mft_markings,'') ,a.nbr_pkgs,"
				 * ; sb.
				 * append(" a.nbr_pkgs - a.trans_nbr_pkgs - nvl(a.release_nbr_pkgs,0)  - nvl(a.dn_nbr_pkgs,0) ,nvl(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,nvl(a.nbr_pkgs,0) - nvl(a.release_nbr_pkgs,0) - nvl(a.trans_dn_nbr_pkgs,0) - nvl(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,nvl(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,a.trans_nbr_pkgs as transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS as mftNbr,"
				 * ; sb.
				 * append(" f.NBR_PKGS as jppsanpkg,f.DN_NBR_PKGS as jppsadn_npkg,a.mft_seq_nbr as mftsqnbr from gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,tesn_jp_psa f,cc_unstuff_manifest g where"
				 * ; sb.
				 * append(" a.var_nbr =  b.vv_cd and b.vv_cd = c.vv_cd  and d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) and SHIFT_IND = 1 and a.edo_status='A' and a.mft_seq_nbr = d.mft_seq_nbr and d.unstuff_seq_nbr=g.UNSTUFF_SEQ_NBR(+) AND f.EDO_ASN_NBR = a.EDO_ASN_NBR and f.esn_asn_nbr = '"
				 * + tesnnbr + "' and a.EDO_ASN_NBR =" + edoNbr.trim();
				 */
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
			// vinayak added on 8 jan 2004
			else if (checkEdoStuff) {
				sb.append(
						"SELECT a.bl_nbr,a.edo_asn_nbr,a.acct_nbr,a.payment_mode,a.nom_wt,a.nom_vol,d.crg_des,ADP_CUST_CD,ADP_IC_TDBCR_NBR,ADP_NM,CA_CUST_CD,CA_IC_TDBCR_NBR,CA_NM,AA_CUST_CD,AA_IC_TDBCR_NBR,");
				sb.append(
						"b.vsl_nm , b.in_voy_nbr ,TO_CHAR(c.ATB_DTTM,'dd/mm/yyyy hh24:mi') AS atb, TO_CHAR(DECODE(d.UNSTUFF_SEQ_NBR,0,c.GB_COD_DTTM,g.DTTM_UNSTUFF),'dd/mm/yyyy hh24:mi') AS cod,NVL(d.CRG_DES,' '),");
				sb.append(
						"NVL(e.mft_markings,'') ,a.nbr_pkgs,a.nbr_pkgs - a.trans_nbr_pkgs - NVL(a.release_nbr_pkgs,0)  - NVL(a.dn_nbr_pkgs,0) ,NVL(a.dn_nbr_pkgs,0),a.CRG_STATUS,a.aa_nm,NVL(a.nbr_pkgs,0) - NVL(a.release_nbr_pkgs,0) - NVL(a.trans_dn_nbr_pkgs,0) - NVL(a.dn_nbr_pkgs,0),a.trans_dn_nbr_pkgs,a.trans_nbr_pkgs - a.TRANS_DN_NBR_PKGS,NVL(a.release_nbr_pkgs,0),b.out_voy_nbr,CONS_NM,");
				// "a.trans_nbr_pkgs AS
				// transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS AS
				// mftNbr,csd.NBR_PKGS AS stuffnpkg,csd.DN_NBR_PKGS AS stuffdnnpkg,a.mft_seq_nbr
				// AS mftsqnbr,csd.STUFF_SEQ_NBR stuffseq FROM gb_edo a , vessel_call b
				// ,berthing c,manifest_details d , mft_markings e,cc_stuffing_details
				// csd,cc_stuffing cs,cc_unstuff_manifest g WHERE a.var_nbr = b.vv_cd AND "+
				sb.append(
						"a.trans_nbr_pkgs AS transNbr,a.CUT_OFF_NBR_PKGS,d.NBR_PKGS_IN_PORT,d.NBR_PKGS AS mftNbr,csd.NBR_PKGS AS stuffnpkg,csd.DN_NBR_PKGS AS stuffdnnpkg,a.mft_seq_nbr AS mftsqnbr,csd.STUFF_SEQ_NBR stuffseq, a.wh_ind, a.wh_aggr_nbr, a.wh_remarks, b.terminal, b.scheme,b.combi_gc_scheme, b.combi_gc_ops_ind  FROM gb_edo a , vessel_call b ,berthing c,manifest_details d ,  mft_markings e,cc_stuffing_details csd,cc_stuffing cs,cc_unstuff_manifest g WHERE a.var_nbr =  b.vv_cd AND ");
				// changed by Irene Tan on 17 Feb 2004 : to allow extraction for EDO from other
				// vessel stuff into container of different vessel
				// "b.vv_cd = c.vv_cd AND c.vv_cd=cs.VAR_NBR AND d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+)
				// AND SHIFT_IND = 1 AND a.edo_status='A' AND a.mft_seq_nbr = d.mft_seq_nbr AND
				// d.unstuff_seq_nbr=g.UNSTUFF_SEQ_NBR(+) AND cs.STUFF_CLOSED='Y' AND
				// csd.REC_STATUS='A' AND cs.ACTIVE_STATUS='A' AND csd.EDO_ESN_NBR =
				// a.EDO_ASN_NBR AND csd.EDO_ESN_IND='EDO' AND
				// cs.STUFF_SEQ_NBR=csd.STUFF_SEQ_NBR AND csd.STUFF_SEQ_NBR = '"+tesnnbr+"' AND
				// a.EDO_ASN_NBR ="+edoNbr.trim();
				sb.append(
						"b.vv_cd = c.vv_cd  AND d.MFT_SEQ_NBR = e.MFT_SQ_NBR(+) AND SHIFT_IND = 1 AND a.edo_status='A' AND a.mft_seq_nbr = d.mft_seq_nbr AND d.unstuff_seq_nbr=g.UNSTUFF_SEQ_NBR(+) AND cs.STUFF_CLOSED='Y' AND csd.REC_STATUS='A' AND cs.ACTIVE_STATUS='A' AND csd.EDO_ESN_NBR = a.EDO_ASN_NBR AND csd.EDO_ESN_IND='EDO' AND cs.STUFF_SEQ_NBR=csd.STUFF_SEQ_NBR AND csd.STUFF_SEQ_NBR = :tesnnbr AND a.EDO_ASN_NBR = :edoNbr ");
				// end changed by Irene Tan on 17 Feb 2004

			}
		}
		try {
			log.info("START: fetchEdoDetails  DAO  Start edoNbr" + edoNbr + " ,tesnnbr" + tesnnbr + " ,searchcrg"
					+ searchcrg);

			sql = sb.toString();

			if (searchcrg != null && !searchcrg.equals("") && searchcrg.equals("LT")) {
				paramMap.put("edoNbr", edoNbr.trim());
			} else {
				if (chktesnJpJp_nbr.equals("Y")) {
					paramMap.put("tesnnbr", tesnnbr);
					paramMap.put("edoNbr", edoNbr.trim());
				} else if (chktesnJpPsa_nbr.equals("Y")) {
					paramMap.put("tesnnbr", tesnnbr);
					paramMap.put("edoNbr", edoNbr.trim());
				} else if (checkEdoStuff) {
					paramMap.put("tesnnbr", tesnnbr);
					paramMap.put("edoNbr", edoNbr.trim());
				}
			}

			log.info(" fetchEdoDetails  DAO  SQL " + sql);
			log.info(" *** fetchEdoDetails params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

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

					sql1 = "SELECT C.CO_NM FROM CUST_ACCT CA,COMPANY_CODE C WHERE CA.CUST_CD=C.CO_CD AND CA.ACCT_NBR=  :accNbr";

					paramMap.put("accNbr", accNbr);
					log.info(" fetchEdoDetails  DAO  SQL " + sql1);
					log.info(" *** fetchEdoDetails params *****" + paramMap.toString());
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);
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
					sql1 = "SELECT CC.CO_NM, (SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) from customer where cust_cd = :adpcustcd) TDB_CR_NBR FROM CUSTOMER, COMPANY_CODE CC WHERE CUST_CD=CC.CO_CD and cust_cd = :adpcustcd ";

					paramMap.put("adpcustcd", adpcustcd);
					log.info(" fetchEdoDetails  DAO  SQL " + sql1);
					log.info(" *** fetchEdoDetails params *****" + paramMap.toString());
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);
					if (rs1.next()) {
						adpNm = rs1.getString("co_nm");
						adpIcNbr = rs1.getString("TDB_CR_NBR");
					}

				}

				if (caCustcd != null) {
					// Punitha - UEN Enhancement
					// ResultSet rs1 = pstmt1.executeQuery("SELECT CC.CO_NM,CUSTOMER.TDB_CR_NBR FROM
					// CUSTOMER, COMPANY_CODE CC WHERE CUSTOMER.CUST_CD=CC.CO_CD AND
					// CUSTOMER.CUST_CD='" + caCustcd + "'");
					sql1 = "SELECT CC.CO_NM, (SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) from customer where cust_cd = :caCustcd) TDB_CR_NBR FROM CUSTOMER, COMPANY_CODE CC WHERE CUST_CD=CC.CO_CD and cust_cd = :caCustcd";

					paramMap.put("caCustcd", caCustcd);
					log.info(" fetchEdoDetails  DAO  SQL " + sql1);
					log.info(" *** fetchEdoDetails params *****" + paramMap.toString());
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);
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
					sql1 = "SELECT CC.CO_NM, (SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) from customer where cust_cd = :aaCustCd) TDB_CR_NBR FROM CUSTOMER, COMPANY_CODE CC WHERE CUST_CD=CC.CO_CD and cust_cd = :aaCustCd ";

					paramMap.put("aaCustCd", aaCustCd);
					log.info(" fetchEdoDetails  DAO  SQL " + sql1);
					log.info(" *** fetchEdoDetails params *****" + paramMap.toString());
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);
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
				edoVo.setWhInd(whInd);
				edoVo.setWhAggrNbr(whAggrNbr);
				edoVo.setWhRemarks(whRemarks);
				BJDetailsVect.add(edoVo);
			}

			log.info(" fetchEdoDetails  DAO  Result" + BJDetailsVect.toString());
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
			log.info("END:chktesnJpJp DAO end tesnjpjp:" + tesnjpjp);

		}
		return tesnjpjp;
	}

	// shutoutDnReport.jrxml
	@Override
	public List<ShutoutEdoDnReport> getdnReportDetails(String dnNbr) throws BusinessException {

		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		List<ShutoutEdoDnReport> listDnDetails = new ArrayList<ShutoutEdoDnReport>();
		try {
			log.info("getdnReportDetails DAO START dnNbr:" + dnNbr);

			sb.append(
					" SELECT TO_CHAR(WEBDNUATEMP.DATETIME, 'DD/MM/YYYY HH24:MI') datetime,WEBDNUATEMP.TRANSREFNO AS DNVIEWRPT_DN_NBR, ");
			sb.append(
					" TO_CHAR(WEBDNUATEMP.ATB, 'DD/MM/YYYY HH24:MI') ATB, WEBDNUATEMP.VSLNM,WEBDNUATEMP.VOYNO,WEBDNUATEMP.CONTNO,");
			sb.append(
					" WEBDNUATEMP.TRANSTYPE,WEBDNUATEMP.CONTSIZE,WEBDNUATEMP.CONTTYPE, WEBDNUATEMP.ASNNO,WEBDNUATEMP.CRGREF,WEBDNUATEMP.WT,WEBDNUATEMP.VOL, ");
			sb.append(
					" WEBDNUATEMP.DECLQTY,WEBDNUATEMP.TRANSQTY,WEBDNUATEMP.BALQTY, WEBDNUATEMP.NRICPASSPORTNO,WEBDNUATEMP.VEH1,WEBDNUATEMP.MARKING, ");
			sb.append(
					" WEBDNUATEMP.CRG_DESC,TO_CHAR(WEBDNUATEMP.COD, 'DD/MM/YYYY HH24:MI') COD,SST_BILL.TARRIF_CD_SER_CHRG, SST_BILL.TARRIF_DESC_SER_CHRG,SST_BILL.BILLABLE_TON_SER_CHRG, ");
			sb.append(
					" SST_BILL.UNIT_RATE_SER_CHRG,SST_BILL.TOTAL_AMT_SER_CHRG, SST_BILL.TOTAL_AMT_WHARF_CHRG,SST_BILL.TOTAL_AMT_STORE_CHRG,");
			sb.append(
					" SST_BILL.TOTAL_AMT_SER_WHARF_CHRG,SST_BILL.TOTAL_AMT_SR_CHRG, SST_BILL.ACCT_NBR_SER_CHRG,SST_BILL.EDO_ACCT_NBR,SST_BILL.TARRIF_CD_WHARF_CHRG,");
			sb.append(
					" SST_BILL.TARRIF_DESC_WHARF_CHRG,SST_BILL.BILLABLE_TON_WHARF_CHRG, SST_BILL.UNIT_RATE_WHARF_CHRG,SST_BILL.TARRIF_CD_STORE_CHRG, ");
			sb.append(
					" SST_BILL.TARRIF_DESC_STORE_CHRG,SST_BILL.BILLABLE_TON_STORE_CHRG, SST_BILL.UNIT_RATE_STORE_CHRG,SST_BILL.TARRIF_CD_SR_CHRG, ");
			sb.append(
					" SST_BILL.TARRIF_DESC_SR_CHRG,SST_BILL.BILLABLE_TON_SR_CHRG, SST_BILL.UNIT_RATE_SR_CHRG,SST_BILL.UNIT_RATE_SER_WHARF_CHRG, ");
			sb.append(
					" SST_BILL.BILLABLE_TON_SER_WHARF_CHRG,SST_BILL.TARRIF_DESC_SER_WHARF_CHRG, SST_BILL.TARRIF_CD_SER_WHARF_CHRG,SST_BILL.TIME_UNIT_SER, ");
			sb.append(
					" SST_BILL.TIME_UNIT_WHF,SST_BILL.TIME_UNIT_SR,SST_BILL.TIME_UNIT_SER_WHF, SST_BILL.TIME_UNIT_STORE,DN_DETAILS.\"CRG_DEST\" AS DN_DETAILS_CRG_DEST, ");
			sb.append(
					" WEBDNUATEMP.AB_CD AS DNVIEWRPT_SCHEME FROM  (((DN_DETAILS INNER JOIN WEBDNUATEMP WEBDNUATEMP ON WEBDNUATEMP.TRANSREFNO = DN_DETAILS.DN_NBR) ");
			sb.append(
					" INNER JOIN VESSELSCHEME VESSELSCHEME ON WEBDNUATEMP.VV_CD=VESSELSCHEME.VV_CD ) INNER JOIN SST_BILL SST_BILL ON WEBDNUATEMP.TRANSREFNO=SST_BILL.DN_UA_NBR) ");
			sb.append(
					" INNER JOIN DNVIEWRPT ON DN_DETAILS.DN_NBR = DNVIEWRPT.DN_NBR WHERE (WEBDNUATEMP.TRANSREFNO = :dnNbr ) ");
			sb.append(" ORDER BY WEBDNUATEMP.TRANSREFNO ");

			paramMap.put("dnNbr", dnNbr);
			log.info("SQL" + sb.toString() + "pstmt:");
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				ShutoutEdoDnReport dnVo = new ShutoutEdoDnReport();
				dnVo.setDATETIME(rs.getString("DATETIME"));
				dnVo.setDNVIEWRPT_DN_NBR(rs.getString("DNVIEWRPT_DN_NBR"));
				dnVo.setATB(rs.getString("ATB"));
				dnVo.setVSLNM(rs.getString("VSLNM"));
				dnVo.setVOYNO(rs.getString("VOYNO"));
				dnVo.setCONTNO(rs.getString("CONTNO"));
				dnVo.setTRANSTYPE(rs.getString("TRANSTYPE"));
				dnVo.setCONTSIZE(rs.getString("CONTSIZE"));
				dnVo.setCONTTYPE(rs.getString("CONTTYPE"));
				dnVo.setASNNO(new BigDecimal(rs.getString("ASNNO")));
				dnVo.setCRGREF(rs.getString("CRGREF"));
				dnVo.setWT(new BigDecimal(rs.getString("WT")));
				dnVo.setVOL(new BigDecimal(rs.getString("VOL")));
				dnVo.setDECLQTY(new BigDecimal(rs.getString("DECLQTY")));
				dnVo.setTRANSQTY(new BigDecimal(rs.getString("TRANSQTY")));
				dnVo.setBALQTY(new BigDecimal(rs.getString("BALQTY")));
				dnVo.setNRICPASSPORTNO(rs.getString("NRICPASSPORTNO"));
				dnVo.setVEH1(rs.getString("VEH1"));
				dnVo.setMARKING(rs.getString("MARKING"));
				dnVo.setCRG_DESC(rs.getString("CRG_DESC"));
				dnVo.setCOD(rs.getString("COD"));
				dnVo.setTARRIF_CD_SER_CHRG(rs.getString("TARRIF_CD_SER_CHRG"));
				dnVo.setTARRIF_DESC_SER_CHRG(rs.getString("TARRIF_DESC_SER_CHRG"));
				dnVo.setBILLABLE_TON_SER_CHRG(new BigDecimal(rs.getString("BILLABLE_TON_SER_CHRG")));
				dnVo.setUNIT_RATE_SER_CHRG(new BigDecimal(rs.getString("UNIT_RATE_SER_CHRG")));
				dnVo.setTOTAL_AMT_SER_CHRG(new BigDecimal(rs.getString("TOTAL_AMT_SER_CHRG")));
				dnVo.setTOTAL_AMT_WHARF_CHRG(new BigDecimal(rs.getString("TOTAL_AMT_WHARF_CHRG")));
				dnVo.setTOTAL_AMT_STORE_CHRG(new BigDecimal(rs.getString("TOTAL_AMT_STORE_CHRG")));
				dnVo.setTOTAL_AMT_SER_WHARF_CHRG(new BigDecimal(rs.getString("TOTAL_AMT_SER_WHARF_CHRG")));
				dnVo.setTOTAL_AMT_SR_CHRG(new BigDecimal(rs.getString("TOTAL_AMT_SR_CHRG")));
				dnVo.setACCT_NBR_SER_CHRG(rs.getString("ACCT_NBR_SER_CHRG"));
				dnVo.setEDO_ACCT_NBR(rs.getString("EDO_ACCT_NBR"));
				dnVo.setTARRIF_CD_WHARF_CHRG(rs.getString("TARRIF_CD_WHARF_CHRG"));
				dnVo.setTARRIF_DESC_WHARF_CHRG(rs.getString("TARRIF_DESC_WHARF_CHRG"));
				dnVo.setBILLABLE_TON_WHARF_CHRG(new BigDecimal(rs.getString("BILLABLE_TON_WHARF_CHRG")));
				dnVo.setUNIT_RATE_WHARF_CHRG(new BigDecimal(rs.getString("UNIT_RATE_WHARF_CHRG")));
				dnVo.setTARRIF_CD_STORE_CHRG(rs.getString("TARRIF_CD_STORE_CHRG"));
				dnVo.setTARRIF_DESC_STORE_CHRG(rs.getString("TARRIF_DESC_STORE_CHRG"));
				dnVo.setBILLABLE_TON_STORE_CHRG(new BigDecimal(rs.getString("BILLABLE_TON_STORE_CHRG")));
				dnVo.setUNIT_RATE_STORE_CHRG(new BigDecimal(rs.getString("UNIT_RATE_STORE_CHRG")));
				dnVo.setTARRIF_CD_SR_CHRG(rs.getString("TARRIF_CD_SR_CHRG"));
				dnVo.setTARRIF_DESC_SR_CHRG(rs.getString("TARRIF_DESC_SR_CHRG"));
				dnVo.setBILLABLE_TON_SR_CHRG(new BigDecimal(rs.getString("BILLABLE_TON_SR_CHRG")));
				dnVo.setUNIT_RATE_SR_CHRG(new BigDecimal(rs.getString("UNIT_RATE_SR_CHRG")));
				dnVo.setUNIT_RATE_SER_WHARF_CHRG(new BigDecimal(rs.getString("UNIT_RATE_SER_WHARF_CHRG")));
				dnVo.setBILLABLE_TON_SER_WHARF_CHRG(new BigDecimal(rs.getString("BILLABLE_TON_SER_WHARF_CHRG")));
				dnVo.setTARRIF_DESC_SER_WHARF_CHRG(rs.getString("TARRIF_DESC_SER_WHARF_CHRG"));
				dnVo.setTARRIF_CD_SER_WHARF_CHRG(rs.getString("TARRIF_CD_SER_WHARF_CHRG"));
				dnVo.setTIME_UNIT_SER(new BigDecimal(rs.getString("TIME_UNIT_SER")));
				dnVo.setTIME_UNIT_WHF(new BigDecimal(rs.getString("TIME_UNIT_WHF")));
				dnVo.setTIME_UNIT_SR(new BigDecimal(rs.getString("TIME_UNIT_SR")));
				dnVo.setTIME_UNIT_SER_WHF(new BigDecimal(rs.getString("TIME_UNIT_SER_WHF")));
				dnVo.setTIME_UNIT_STORE(new BigDecimal(rs.getString("TIME_UNIT_STORE")));
				dnVo.setDN_DETAILS_CRG_DEST(rs.getString("DN_DETAILS_CRG_DEST"));
				dnVo.setDNVIEWRPT_SCHEME(rs.getString("DNVIEWRPT_SCHEME"));
				listDnDetails.add(dnVo);
			}

			return listDnDetails;
		} catch (NullPointerException e) {
			log.info("Exception END:getdnReportDetails :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception END:getdnReportDetails :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:getdnReportDetails DAO end");

		}
	}

	// EndRegion DnJdbcRepository

}
