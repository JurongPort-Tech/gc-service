package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.TesnjpjpRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TesnEsnListValueObject;
import sg.com.jp.generalcargo.domain.TesnJpJpValueObject;
import sg.com.jp.generalcargo.domain.TesnVesselVoyValueObject;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.DpeCommonUtil;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("tesnjpjpRepository")
public class TesnjpjpJdbcRepository implements TesnjpjpRepository {

	private static final Log log = LogFactory.getLog(TesnjpjpJdbcRepository.class);
	public String logStatusGlobal = "Y";

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	// ejb.sessionBeans.gbms.cargo.tesn.tesnjpjp -->TesnjpjpEJB

	@Override
	public String tesnjpjpAddForDPE(String edo_asn_nbr, String bk_ref_nbr, String ld_ind, String nbr_pkgs,
			String pay_mode, String acc_num, String val_pkgs, String out_voy_var_nbr, String in_voy_var_nbr,
			String coCd, String UserID, String edo_nbr_pkgs, String stuffind, String category, String nomWt,
			String nomVol) throws BusinessException {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		SqlRowSet rss = null;
		SqlRowSet rs3 = null;
		SqlRowSet rs4 = null;
		SqlRowSet rs3_1 = null;
		SqlRowSet rsNGen1 = null;
		SqlRowSet rsasn = null;
		String Mssg1 = null;
		float bk_nbr_pkgs = 0;
		float bk_wt = 0;
		float bk_vol = 0;
		double variance_pkgs = 0;
		float variance_wt = 0;
		float variance_vol = 0;
		String esn_asn_nbr = "";
		String declarant_cd = "";
		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String sql4 = "";
		StringBuffer sb1 = new StringBuffer();

		try {
			log.info("START: tesnjpjpAddForDPE  DAO  Start Obj " + " edo_asn_nbr:" + CommonUtility.deNull(edo_asn_nbr) + " bk_ref_nbr:"
					+ CommonUtility.deNull(bk_ref_nbr) + " ld_ind:" + CommonUtility.deNull(ld_ind) + " nbr_pkgs:" + CommonUtility.deNull(nbr_pkgs)
					+ " pay_mode:" + CommonUtility.deNull(pay_mode) + " acc_num:" + CommonUtility.deNull(acc_num) + " val_pkgs:"
					+ CommonUtility.deNull(val_pkgs) + " out_voy_var_nbr:" + CommonUtility.deNull(out_voy_var_nbr) + " in_voy_var_nbr:"
					+ CommonUtility.deNull(in_voy_var_nbr) + " coCd:" + CommonUtility.deNull(coCd) + " UserID:" + CommonUtility.deNull(UserID) 
					+ " edo_nbr_pkgs:" + CommonUtility.deNull(edo_nbr_pkgs) + " stuffind:" + CommonUtility.deNull(stuffind)
					+ " category:" + CommonUtility.deNull(category) + " nomWt:" + CommonUtility.deNull(nomWt) + " nomVol:" + CommonUtility.deNull(nomVol));

			log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE :: step 1 ");

			String sql = "select nom_wt,nom_vol from gb_edo where edo_asn_nbr=:edo_asn_nbr";
			String sql5 = "SELECT declarant_cd FROM bk_details WHERE bk_ref_nbr=:bk_ref_nbr ";
			String sql6 = "select bk_nbr_pkgs,bk_wt,bk_vol,variance_pkgs,variance_vol,variance_wt from bk_details bk where bk_ref_nbr=:bk_ref_nbr ";

			log.info(" *** tesnjpjpAddForDPE SQL *****" + sql);
			paramMap.put("edo_asn_nbr", edo_asn_nbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			paramMap.put("bk_ref_nbr", bk_ref_nbr);
			log.info(" *** tesnjpjpAddForDPE SQL *****" + sql5);
			log.info(" paramMap: " + paramMap);
			rs3 = namedParameterJdbcTemplate.queryForRowSet(sql5, paramMap);

			paramMap.put("bk_ref_nbr", bk_ref_nbr);
			log.info(" *** tesnjpjpAddForDPE SQL *****" + sql6);
			log.info(" paramMap: " + paramMap);
			rs4 = namedParameterJdbcTemplate.queryForRowSet(sql6, paramMap);

			if (rs3.next()) {
				try {
					declarant_cd = rs3.getString("declarant_cd");
				} catch (Exception exp) {
					log.info(exp);
				}
			}

			log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE :: step 2 ");

			float nom_wt = 0, nom_vol = 0;
			float nomwt = 0, nomvol = 0;
			if (rs.next()) {
				try {
					nomwt = Float.parseFloat(rs.getString("nom_wt"));
					nomvol = Float.parseFloat(rs.getString("nom_vol"));
				} catch (Exception exp) {
					log.info(exp);
					nomwt = 0;
					nomvol = 0;
				}
			}
			log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE :: step 3 ");
			// log.info("wt1"+nomwt);
			// log.info("vol1"+nomvol);
			try {
				nom_wt = (Float.parseFloat(nbr_pkgs) / Float.parseFloat(edo_nbr_pkgs)) * nomwt;
				nom_vol = (Float.parseFloat(nbr_pkgs) / Float.parseFloat(edo_nbr_pkgs)) * nomvol;
			} catch (Exception exp) {
				log.info(exp);
				nom_wt = 0;
				nom_vol = 0;
			}
			// log.info("wt"+nom_wt); log.info("pk"+nbr_pkgs);
			// log.info("vol"+nom_vol); log.info("edo"+edo_nbr_pkgs);

			log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE :: step 4 ");
			if (rs4.next()) {
				try {
					bk_nbr_pkgs = Float.parseFloat(rs4.getString("bk_nbr_pkgs"));
					bk_wt = Float.parseFloat(rs4.getString("bk_wt"));
					bk_vol = Float.parseFloat(rs4.getString("bk_vol"));
					variance_pkgs = Double.parseDouble(rs4.getString("variance_pkgs"));
					variance_wt = Float.parseFloat(rs4.getString("variance_wt"));
					variance_vol = Float.parseFloat(rs4.getString("variance_vol"));
				} catch (Exception exp) {
					log.info(exp);
					bk_nbr_pkgs = 0;
					bk_wt = 0;
					bk_vol = 0;
					variance_pkgs = 0;
					variance_wt = 0;
					variance_vol = 0;
				}
			}

			log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE :: step 5 ");
			float bk_nbr_chk = 0;
			float bk_wt_chk = 0;
			float bk_vol_chk = 0;
			log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE :: step 5.1 ");
			try {
				bk_nbr_chk = bk_nbr_pkgs * (1 + (float) (Math.floor(variance_pkgs)) / 100);
				bk_wt_chk = bk_wt * (1 + variance_wt / 100);
				bk_vol_chk = bk_vol * (1 + variance_vol / 100);
				log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE :: step 5.1 - bk_nbr_chk : " + bk_nbr_chk);
				log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE :: step 5.1 - bk_nbr_chk : " + bk_wt_chk);
				log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE :: step 5.1 - bk_nbr_chk : " + bk_vol_chk);
			} catch (Exception exp) {
				log.info("Exception in  step 5.1 - bk_nbr_chk : " , exp);
				bk_nbr_chk = 0;
				bk_wt_chk = 0;
				bk_vol_chk = 0;
			}

			// Add by VietNguyen 08/01/2014: START
			log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE :: nomWt: " + nomWt);
			log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE ::nomVol " + nomVol);
			try {
				nom_wt = Float.parseFloat(nomWt);
				nom_vol = Float.parseFloat(nomVol);
			} catch (Exception exp) {
				log.info("Exception log: " , exp);
				nom_wt = 0;
				nom_vol = 0;
			}
			// Add by VietNguyen 08/01/2014: END

			log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE :: step 6 ");

			log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE :: nom_wt: " + nom_wt);
			log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE ::nom_vol " + nom_vol);

			// Commented by VietNguyen 05/05/2014: START
			if (Float.parseFloat(nbr_pkgs) > bk_nbr_pkgs) {
				Mssg1 = "M21407";
			}
			if (nom_wt > bk_wt_chk) {
				Mssg1 = "M21405";
			}
			if (nom_vol > bk_vol_chk) {
				Mssg1 = "M21406";
			}
			// Commented by VietNguyen 05/05/2014: END

			if (Mssg1 == null) {
				/*
				 * rs2 =
				 * stmt.executeQuery("select to_char(sysdate,'yy/mm/dd') as sysdte from dual");
				 * // log.info("select to_char(sysdate,'yy/mm/dd') as sysdte from dual");
				 * if(rs2.next()) sysd = rs2.getString("sysdte"); sysd =
				 * sysd.substring(0,2)+sysd.substring(3,5); rs2 = stmt.executeQuery("select '"
				 * +sysd+"'||trim(nvl(trim(to_char(max(to_number(substr(esn_asn_nbr,4)))+2,'0000')),'1002')) as esn_asn_nbr from esn"
				 * ); // log.info("select '"
				 * +sysd+"'||trim(nvl(trim(to_char(max(to_number(substr(esn_asn_nbr,4)))+2,'0000')),'1002')) as esn_asn_nbr from esn"
				 * ); if (rs2.next()) esn_asn_nbr = rs2.getString("esn_asn_nbr"); rs2.close();
				 */

				// Begin of generating esn number
				String sqlNGen1 = "";
				String sql3_1 = "";

				// sqlNGen1 = "SELECT MAX(ESN_ASN_NBR) FROM ESN ";
				sql3_1 = "SELECT TO_CHAR(SYSDATE,'Y/MM/DD') AS STRDATE FROM DUAL";
				/*
				 * log.info(sqlNGen1); log.info(sql3_1);
				 */

				String stresnasnnbr = "";
				// String esn_asn_nbr= "";
				String strsqldate = "";

				log.info(" *** tesnjpjpAddForDPE SQL *****" + sql3_1);
				log.info(" paramMap: " + paramMap);
				rs3_1 = namedParameterJdbcTemplate.queryForRowSet(sql3_1, paramMap);
				log.info("rs3_1: " + rs3_1.toString());
				while (rs3_1.next()) {
					strsqldate = CommonUtility.deNull(rs3_1.getString(1));
				}
				
				String strsqlyy = strsqldate.substring(0, 1);
				String strsqlmm = strsqldate.substring(2, 4);

				if ((strsqlyy + strsqlmm.substring(0, 1)).equals("00")// Bhuvana 15/09/2010
						|| (strsqlyy + strsqlmm.substring(0, 1)).equals("01")) { // For year ends with 0. ie. 2010,
																					// 2020, etc.
					sqlNGen1 = "SELECT MAX(ESN_ASN_NBR) FROM ESN WHERE ESN_ASN_NBR < 1300000";
				} else {
					// sqlNGen1 = "SELECT MAX(ESN_ASN_NBR) FROM ESN ";
					// eg. For 2011: Retrieve the max ESN No between ESN No 10000000 and 19999999.
					sqlNGen1 = "SELECT MAX(ESN_ASN_NBR) FROM ESN WHERE ESN_ASN_NBR BETWEEN (SELECT TO_NUMBER(TO_CHAR(SYSDATE,'Y')) * 10000000 FROM DUAL) ";
					sqlNGen1 = sqlNGen1 + " AND (SELECT ((TO_NUMBER(TO_CHAR(SYSDATE,'Y'))+1) * 10000000)-1 FROM DUAL)";
				}

				log.info(" *** tesnjpjpAddForDPE SQL *****" + sqlNGen1);
				log.info(" paramMap: " + paramMap);
				rsNGen1 = namedParameterJdbcTemplate.queryForRowSet(sqlNGen1, paramMap);
				log.info("rsNGen1: " + rsNGen1);
				while (rsNGen1.next()) {
					stresnasnnbr = CommonUtility.deNull(rsNGen1.getString(1));
				}

				// generating next number
				// ResultSet rs3_1 = stmt.executeQuery(sql3_1);
				// while (rs3_1.next()) {
				// strsqldate = CommonUtility.deNull(rs3_1.getString(1));
				// }
				// rs3_1.close();
				if (stresnasnnbr.equalsIgnoreCase("")) {
					// stresnasnnbr = "00000002";
					stresnasnnbr = "00100000";
				}
				if (stresnasnnbr.length() == 7) {
					stresnasnnbr = "0".concat(stresnasnnbr);
				}
				if (stresnasnnbr.length() == 6) {
					stresnasnnbr = "00".concat(stresnasnnbr);
				}
				if (stresnasnnbr.length() == 5) {
					stresnasnnbr = "000".concat(stresnasnnbr);
				}

				int intesnasnnbr = Integer.parseInt(stresnasnnbr.substring(3, 8));
				String stresnasnnbryy = stresnasnnbr.substring(0, 1);
				String stresnasnnbrmm = stresnasnnbr.substring(1, 3);

				if ((stresnasnnbryy.equalsIgnoreCase(strsqlyy)) && (stresnasnnbrmm.equalsIgnoreCase(strsqlmm))) {
					stresnasnnbr = (stresnasnnbryy).concat(stresnasnnbrmm);
					intesnasnnbr = intesnasnnbr + 2;
					String strtempnbr = Integer.toString(intesnasnnbr);
					log.info("strtempnbr = " + strtempnbr.toString());
					// Added by Babatunde on Jan., 2014 : START
					boolean isValid = false;
					String randomAsnNbr = null;
					String dbAsnNbr = null;
					String sqlasn;

					ArrayList<String> asnNbrs;

					while (!isValid) {
						asnNbrs = new ArrayList<String>();

						for (int i = 0; i <= 19; i++) {
							randomAsnNbr = stresnasnnbr.concat(DpeCommonUtil.generateRandomNumber(5, true));
							asnNbrs.add(randomAsnNbr);
						}
						
						sb1.append("select ESN_ASN_NBR from ESN where ESN_ASN_NBR in (:asnStr)");
						sqlasn = sb1.toString();
						List<String> existAsnNbrs = new ArrayList<String>();

						log.info(" *** tesnjpjpAddForDPE SQL *****" + sqlasn);
						
						MapSqlParameterSource parameters = new MapSqlParameterSource();
						parameters.addValue("asnStr", asnNbrs);
						log.info(" parameters: " + parameters.getValues());
						rsasn = namedParameterJdbcTemplate.queryForRowSet(sqlasn, parameters);
						while (rsasn.next()) {
							dbAsnNbr = CommonUtility.deNull(rsasn.getString(1));
							existAsnNbrs.add(dbAsnNbr);
							log.info("Resultset = " + dbAsnNbr);
						}
						asnNbrs.removeAll(existAsnNbrs);

						if (asnNbrs.size() > 0) {
							stresnasnnbr = asnNbrs.get(0);
							isValid = true;
							log.info("New ASN Nbr = " + stresnasnnbr);
						}
					}
					log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE ::New ASN Nbr " + stresnasnnbr);
					// Added by Babatunde on Jan., 2014 : END

					// Commented by babatunde on Jan., 2014 : START
					// if (strtempnbr.length() == 1) {
					// stresnasnnbr = stresnasnnbr.concat("0000");
					// stresnasnnbr = stresnasnnbr.concat(strtempnbr);
					// }
					// if (strtempnbr.length() == 2) {
					// stresnasnnbr = stresnasnnbr.concat("000");
					// stresnasnnbr = stresnasnnbr.concat(strtempnbr);
					// }
					// if (strtempnbr.length() == 3) {
					// stresnasnnbr = stresnasnnbr.concat("00");
					// stresnasnnbr = stresnasnnbr.concat(strtempnbr);
					// }
					// if (strtempnbr.length() == 4) {
					// stresnasnnbr = stresnasnnbr.concat("0");
					// stresnasnnbr = stresnasnnbr.concat(strtempnbr);
					// }
					// if (strtempnbr.length() == 5) {
					// stresnasnnbr = stresnasnnbr.concat(strtempnbr);
					// }
					// Commented by babatunde on Jan., 2014 : START
				} else {
					stresnasnnbr = (strsqlyy).concat(strsqlmm);
					stresnasnnbr = stresnasnnbr.concat("00002");
					log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE ::New ASN Nbr22 " + stresnasnnbr);
				}
				// new number generated
				esn_asn_nbr = stresnasnnbr;
				// log.info("esn_an_nbr"+esn_asn_nbr);
				// end of esn number generation

				// TVS added stuff ind in query 25-09-2003
				StringBuffer sb = new StringBuffer();
				sql1 = "";
				sb.append(
						"insert into esn(esn_asn_nbr,declarant_cr_no,bk_ref_nbr,trans_type,in_voy_var_nbr,out_voy_var_nbr,esn_status,esn_create_cd,stuff_ind,last_modify_user_id,last_modify_dttm,CARGO_CATEGORY_CD)");
				sb.append(
						" values (:esn_asn_nbr,'0',:bk_ref_nbr,'A',:in_voy_var_nbr,:out_voy_var_nbr,'A',:declarant_cd,:stuffind,:UserID,sysdate,:category) ");

				sql1 = sb.toString();

				paramMap.put("esn_asn_nbr", esn_asn_nbr);
				paramMap.put("bk_ref_nbr", bk_ref_nbr);
				paramMap.put("in_voy_var_nbr", in_voy_var_nbr);
				paramMap.put("out_voy_var_nbr", out_voy_var_nbr);
				paramMap.put("declarant_cd", declarant_cd);
				paramMap.put("stuffind", stuffind);
				paramMap.put("UserID", UserID);
				paramMap.put("category", category);
				log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE ::TVS sql1" + sql1);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(sql1, paramMap);

				sb = new StringBuffer();
				sql2 = "";

				sb.append(
						"insert into tesn_jp_jp (esn_asn_nbr,edo_asn_nbr,ld_ind,nbr_pkgs,nom_wt,nom_vol,payment_mode,acct_nbr)");
				sb.append("values (:esn_asn_nbr,:edo_asn_nbr,:ld_ind,:nbr_pkgs,:nom_wt,:nom_vol,:pay_mode,:acc_num) ");

				sql2 = sb.toString();

				paramMap.put("esn_asn_nbr", esn_asn_nbr);
				paramMap.put("edo_asn_nbr", edo_asn_nbr);
				paramMap.put("ld_ind", ld_ind);
				paramMap.put("nbr_pkgs", nbr_pkgs);
				paramMap.put("nom_wt", nom_wt);
				paramMap.put("nom_vol", nom_vol);
				paramMap.put("pay_mode", pay_mode);
				paramMap.put("acc_num", acc_num);
				log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE ::TVS sql2" + sql2);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(sql2, paramMap);

				sql3 = "";
				sql3 = "update gb_edo set trans_nbr_pkgs = trans_nbr_pkgs + :nbr_pkgs,last_modify_dttm=sysdate,last_modify_user_id=:UserID where edo_asn_nbr=:edo_asn_nbr ";
				// log.info("sql1"+sql3);

				paramMap.put("nbr_pkgs", nbr_pkgs);
				paramMap.put("UserID", UserID);
				paramMap.put("edo_asn_nbr", edo_asn_nbr);
				log.info(" *** tesnjpjpAddForDPE SQL *****" + sql3);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(sql3, paramMap);

				sql4 = "";
				sql4 = "update bk_details set esn_declared='Y',last_modify_dttm=sysdate,last_modify_user_id=:UserID where bk_ref_nbr=:bk_ref_nbr";
				// log.info("sql1"+sql4);

				paramMap.put("UserID", UserID);
				paramMap.put("bk_ref_nbr", bk_ref_nbr);
				log.info(" *** tesnjpjpAddForDPE SQL *****" + sql4);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(sql4, paramMap);

				log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE ::TVS sql4" + sql4);
				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					String strtransnbr = "";
					// Add by Revathi
					String strESNTransNbr = "";
					String strBKTransNbr = "";
					String strGbEdoTransNbr = "";
					String strTransNbrPkgs = "";
					// end of Add by Revathi

					String sqllog = "SELECT MAX(TRANS_NBR) FROM TESN_JP_JP_trans WHERE ESN_ASN_NBR=:esn_asn_nbr ";

					paramMap.put("esn_asn_nbr", esn_asn_nbr);
					log.info(" *** tesnjpjpAddForDPE SQL *****" + sqllog);
					log.info(" paramMap: " + paramMap);
					rss = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);

					while (rss.next()) {
						strtransnbr = CommonUtility.deNull(rss.getString(1));
					}
					log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE ::TESN_JP_JP_trans strtransnbr" + strtransnbr);

					if (strtransnbr.equalsIgnoreCase("")) {
						strtransnbr = "0";
					} else {
						strtransnbr = String.valueOf(Integer.parseInt(strtransnbr) + 1);
					}

					// Added by Revathi
					// for inserting esn_trans
					sqllog = "";
					sqllog = "SELECT MAX(TRANS_NBR) FROM esn_trans WHERE ESN_ASN_NBR=:esn_asn_nbr";

					rs = null;
					paramMap.put("esn_asn_nbr", esn_asn_nbr);
					log.info(" *** tesnjpjpAddForDPE SQL *****" + sqllog);
					log.info(" paramMap: " + paramMap);
					rs = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);

					while (rs.next()) {
						strESNTransNbr = CommonUtility.deNull(rs.getString(1));
					}

					log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE ::esn_trans strESNTransNbr" + strtransnbr);

					if (strESNTransNbr.equalsIgnoreCase("")) {
						strESNTransNbr = "0";
					} else {
						strESNTransNbr = String.valueOf(Integer.parseInt(strESNTransNbr) + 1);
					}

					// for inserting gb_edo_trans
					sqllog = "";
					sqllog = "SELECT MAX(TRANS_NBR) FROM gb_edo_trans WHERE EDO_ASN_NBR=:edo_asn_nbr ";

					rs = null;

					paramMap.put("edo_asn_nbr", edo_asn_nbr);
					log.info(" *** tesnjpjpAddForDPE SQL *****" + sqllog);
					log.info(" paramMap: " + paramMap);
					rs = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);

					while (rs.next()) {
						strGbEdoTransNbr = CommonUtility.deNull(rs.getString(1));
					}
					log.info(
							"Writing from TesnJpJpEJB.tesnjpjpAddForDPE ::gb_edo_trans strGbEdoTransNbr" + strtransnbr);

					if (strGbEdoTransNbr.equalsIgnoreCase("")) {
						strGbEdoTransNbr = "0";
					} else {
						strGbEdoTransNbr = String.valueOf(Integer.parseInt(strGbEdoTransNbr) + 1);
					}
					// for selecting trans_nbr_packages

					sqllog = "";
					sqllog = "SELECT trans_nbr_pkgs FROM gb_edo WHERE EDO_ASN_NBR=:edo_asn_nbr ";

					rs = null;

					paramMap.put("edo_asn_nbr", edo_asn_nbr);
					log.info(" *** tesnjpjpAddForDPE SQL *****" + sqllog);
					log.info(" paramMap: " + paramMap);
					rs = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);

					while (rs.next()) {
						strTransNbrPkgs = CommonUtility.deNull(rs.getString(1));
					}

					if (strTransNbrPkgs.equalsIgnoreCase("")) {
						strTransNbrPkgs = "0";
					}

					// for inserting BK_DETAILS_trans

					sqllog = "";
					sqllog = "SELECT MAX(TRANS_NBR) FROM BK_DETAILS_TRANS WHERE BK_REF_NBR=:bk_ref_nbr ";

					rs = null;

					paramMap.put("bk_ref_nbr", bk_ref_nbr);
					log.info(" *** tesnjpjpAddForDPE SQL *****" + sqllog);
					log.info(" paramMap: " + paramMap);
					rs = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);

					while (rs.next()) {
						strBKTransNbr = CommonUtility.deNull(rs.getString(1));
					}

					log.info("Writing from TesnJpJpEJB.tesnjpjpAddForDPE ::gb_edo_trans strBKTransNbr" + strBKTransNbr);
					if (strBKTransNbr.equalsIgnoreCase("")) {
						strBKTransNbr = "0";
					} else {
						strBKTransNbr = String.valueOf(Integer.parseInt(strBKTransNbr) + 1);
					}
					sb = new StringBuffer();
					sb.append(
							"insert into tesn_jp_jp_trans(esn_asn_nbr,edo_asn_nbr,trans_nbr,ld_ind,nbr_pkgs,nom_wt,nom_vol,acct_nbr,last_modify_user_id,last_modify_dttm)");
					sb.append(
							" values (:esn_asn_nbr,:edo_asn_nbr,:strtransnbr,:ld_ind,:nbr_pkgs,:nom_wt,:nom_vol,:acc_num,:UserID,sysdate)");
					String strUpdatetrans = sb.toString();

					sb = new StringBuffer();
					sb.append(
							"insert into esn_trans(trans_nbr,esn_asn_nbr,declarant_cr_no,bk_ref_nbr,trans_type,in_voy_var_nbr,out_voy_var_nbr,esn_status,esn_create_cd,stuff_ind,last_modify_user_id,last_modify_dttm)");
					sb.append(
							"values (:strESNTransNbr,:esn_asn_nbr,'0',:bk_ref_nbr,'A',:in_voy_var_nbr,:out_voy_var_nbr,'A',:declarant_cd,:stuffind,:UserID,sysdate)");
					String strESNTrans = sb.toString();

					sb = new StringBuffer();
					sb.append(
							"insert into gb_edo_trans(trans_nbr,edo_asn_nbr,trans_nbr_pkgs,last_modify_user_id,last_modify_dttm)");
					sb.append("values (:strGbEdoTransNbr, :edo_asn_nbr,:strTransNbrPkgs,:UserID,sysdate)");
					String strGbEdoTrans = sb.toString();

					sb = new StringBuffer();
					sb.append(
							"INSERT INTO BK_DETAILS_TRANS(TRANS_NBR,BK_REF_NBR,ESN_DECLARED,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)");
					sb.append("VALUES(:strBKTransNbr,:bk_ref_nbr,'Y',:UserID,sysdate)");
					String strBKDetailsTrans = sb.toString();

					paramMap.put("strESNTransNbr", strESNTransNbr);
					paramMap.put("esn_asn_nbr", esn_asn_nbr);
					paramMap.put("bk_ref_nbr", bk_ref_nbr);
					paramMap.put("in_voy_var_nbr", in_voy_var_nbr);
					paramMap.put("out_voy_var_nbr", out_voy_var_nbr);
					paramMap.put("declarant_cd", declarant_cd);
					paramMap.put("stuffind", stuffind);
					paramMap.put("UserID", UserID);
					log.info(" *** tesnjpjpAddForDPE SQL *****" + strESNTrans);
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(strESNTrans, paramMap);

					paramMap.put("strGbEdoTransNbr", strGbEdoTransNbr);
					paramMap.put("strTransNbrPkgs", strTransNbrPkgs);
					paramMap.put("UserID", UserID);
					log.info(" *** tesnjpjpAddForDPE SQL *****" + strGbEdoTrans);
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(strGbEdoTrans, paramMap);

					paramMap.put("strBKTransNbr", strBKTransNbr);
					paramMap.put("bk_ref_nbr", bk_ref_nbr);
					paramMap.put("UserID", UserID);
					log.info(" *** tesnjpjpAddForDPE SQL *****" + strBKDetailsTrans);
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(strBKDetailsTrans, paramMap);
					// end of Add by Revathi

					paramMap.put("esn_asn_nbr", esn_asn_nbr);
					paramMap.put("edo_asn_nbr", edo_asn_nbr);
					paramMap.put("strtransnbr", strtransnbr);
					paramMap.put("ld_ind", ld_ind);
					paramMap.put("nbr_pkgs", nbr_pkgs);
					paramMap.put("nom_wt", nom_wt);
					paramMap.put("nom_vol", nom_vol);
					paramMap.put("acc_num", acc_num);
					paramMap.put("UserID", UserID);
					log.info(" *** tesnjpjpAddForDPE SQL *****" + strUpdatetrans);
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(strUpdatetrans, paramMap);
				}
			}  else {// if msg
				throw new BusinessException(Mssg1);
			}

			log.info("END: *** tesnjpjpAddForDPE Result *****" + esn_asn_nbr.toString());

		} catch (NullPointerException e) {
			log.info("Exception tesnjpjpAddForDPE : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception tesnjpjpAddForDPE : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception tesnjpjpAddForDPE : " , e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: tesnjpjpAddForDPE  DAO  END");
		}

		return esn_asn_nbr;
	}

	@Override
	public TesnJpJpValueObject tesnjpjpAddView(String edo_asn_nbr, String bk_ref_nbr) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		SqlRowSet rs1 = null;
		TesnJpJpValueObject tesnjpjpobj = new TesnJpJpValueObject();
		String vsl_nm_nbr = "";
		String out_vsl_nm_nbr = "";
		String cont_nbr = "";
		String vsl_voy_nbr = "";
		String out_vsl_voy_nbr = "";
		String vsl_nm = "";
		String out_vsl_nm = "";

		try {
			log.info("START: tesnjpjpAddView  DAO  Start Obj " + " edo_asn_nbr:" + CommonUtility.deNull(edo_asn_nbr) + " bk_ref_nbr:"
					+ CommonUtility.deNull(bk_ref_nbr));

//			sb.append(
//					"select (ed.nom_wt - NVL((select SUM(tesn.NOM_WT) from TESN_JP_JP tesn,esn esn  where tesn.edo_asn_nbr=:edo_asn_nbr and tesn.ESN_ASN_NBR = esn.esn_asn_nbr and esn_status='A' group by tesn.edo_asn_nbr),0) "
//					+ "- NVL((select SUM(nom_wt) from dn_details where edo_asn_nbr='"
//							+ edo_asn_nbr
//							+ "' and dn_status='A' and TESN_ASN_NBR is null group by edo_asn_nbr),0) - NVL((select SUM(psa.NOM_WT) from TESN_JP_PSA psa,esn esn  where psa.edo_asn_nbr=:edo_asn_nbr and psa.ESN_ASN_NBR = esn.esn_asn_nbr and esn_status='A' group by psa.edo_asn_nbr),0)) edo_nom_wt, ");
//			sb.append(
//					"(ed.nom_vol - NVL((select SUM(tesn.NOM_VOL) from TESN_JP_JP tesn, esn esn  where tesn.edo_asn_nbr=:edo_asn_nbr and tesn.ESN_ASN_NBR = esn.esn_asn_nbr and esn.esn_status='A' group by tesn.edo_asn_nbr),0) - NVL((select SUM(nom_vol) from dn_details where edo_asn_nbr='"
//							+ edo_asn_nbr
//							+ "' and dn_status='A' and TESN_ASN_NBR is null group by edo_asn_nbr),0) "
//							+ "- NVL((select SUM(psa.NOM_VOL) from TESN_JP_PSA psa, esn esn  where psa.edo_asn_nbr=:edo_asn_nbr and psa.ESN_ASN_NBR = esn.esn_asn_nbr and esn.esn_status='A' group by psa.edo_asn_nbr),0)) edo_nom_vol, ");
//			sb.append(
//					"ed.mft_seq_nbr,mf.crg_type,ct.crg_type_nm,mf.pkg_type,pt.pkg_desc,hs_code,mf.crg_des,decode(mf.dg_ind,'Y','Yes','N','No',null,'') as dg_ind,mf.cntr_type,mf.cntr_size,mk.mft_markings,");
//			sb.append(
//					"mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS,0) as mf_nbr_pkgs,ed.trans_nbr_pkgs,ed.nbr_pkgs-nvl(ed.CUT_OFF_NBR_PKGS,0) as edo_nbr_pkgs,ed.var_nbr,ed.dn_nbr_pkgs,nvl(mf.NBR_PKGS_IN_PORT,0) as shortnbr, ed.ca_cust_cd ");
//			sb.append(
//					"from gb_edo ed,manifest_details mf,crg_type ct,pkg_types pt ,mft_markings mk where ed.mft_seq_nbr = mf.mft_seq_nbr and mf.crg_type=ct.crg_type_cd and mf.pkg_type = pt.pkg_type_cd and mf.mft_seq_nbr = mk.mft_sq_nbr  and ed.edo_asn_nbr=:edo_asn_nbr");
			// Start #31377, update SQL to retrieve balance of weight and volume, NS June 2023
			sb.append("select (ed.nom_wt - NVL((select SUM(tesn.NOM_WT) from TESN_JP_JP tesn,esn esn  where tesn.edo_asn_nbr=");
			sb.append(":edo_asn_nbr ");
			sb.append("and tesn.ESN_ASN_NBR = esn.esn_asn_nbr and esn_status='A' group by tesn.edo_asn_nbr),0) ");
			sb.append("- NVL((select SUM(nom_wt) from dn_details where edo_asn_nbr=");
			sb.append(":edo_asn_nbr ");
			sb.append("and dn_status='A' and TESN_ASN_NBR is null group by edo_asn_nbr),0) ");
			sb.append("- NVL((select SUM(psa.NOM_WT) from TESN_JP_PSA psa,esn esn  where psa.edo_asn_nbr=");
			sb.append(":edo_asn_nbr ");
			sb.append("and psa.ESN_ASN_NBR = esn.esn_asn_nbr and esn_status='A' group by psa.edo_asn_nbr),0)) edo_nom_wt, ");
			sb.append("(ed.nom_vol - NVL((select SUM(tesn.NOM_VOL) from TESN_JP_JP tesn, esn esn  where tesn.edo_asn_nbr=");
			sb.append(":edo_asn_nbr ");
			sb.append("and tesn.ESN_ASN_NBR = esn.esn_asn_nbr and esn.esn_status='A' group by tesn.edo_asn_nbr),0) ");
			sb.append("- NVL((select SUM(nom_vol) from dn_details where edo_asn_nbr=");
			sb.append(":edo_asn_nbr ");
			sb.append("and dn_status='A' and TESN_ASN_NBR is null group by edo_asn_nbr),0) ");
			sb.append("- NVL((select SUM(psa.NOM_VOL) from TESN_JP_PSA psa, esn esn  where psa.edo_asn_nbr=");
			sb.append(":edo_asn_nbr ");
			sb.append("and psa.ESN_ASN_NBR = esn.esn_asn_nbr and esn.esn_status='A' group by psa.edo_asn_nbr),0)) edo_nom_vol, ");
			sb.append("ed.mft_seq_nbr,mf.crg_type,ct.crg_type_nm,mf.pkg_type,pt.pkg_desc,hs_code,mf.crg_des,decode(mf.dg_ind,'Y','Yes','N','No',null,'') as dg_ind,mf.cntr_type,mf.cntr_size,mk.mft_markings,");
			sb.append("mf.nbr_pkgs - nvl(mf.CUT_OFF_NBR_PKGS,0) as mf_nbr_pkgs,ed.trans_nbr_pkgs,ed.nbr_pkgs-nvl(ed.CUT_OFF_NBR_PKGS,0) as edo_nbr_pkgs,");
			sb.append("ed.var_nbr,ed.dn_nbr_pkgs,nvl(mf.NBR_PKGS_IN_PORT,0) as shortnbr, ed.ca_cust_cd ");
			sb.append("from gb_edo ed,manifest_details mf,crg_type ct,pkg_types pt ,mft_markings mk where ed.mft_seq_nbr = mf.mft_seq_nbr and mf.crg_type=ct.crg_type_cd ");
			sb.append("and mf.pkg_type = pt.pkg_type_cd and mf.mft_seq_nbr = mk.mft_sq_nbr  and ed.edo_asn_nbr=");
			sb.append(":edo_asn_nbr");
			// End #31377, update SQL to retrieve balance of weight and volume, NS June 2023
			String sql = sb.toString();

			// log.info("view sql"+sql);
			String sql1 = "select bk.port_dis,po.port_nm,bk.shipper_nm,var_nbr,bk_nbr_pkgs,bk_wt,bk_vol,variance_pkgs,variance_vol,variance_wt from bk_details bk,un_port_code po where bk.port_dis = po.port_cd and bk_ref_nbr=:bk_ref_nbr";
			// log.info("view sql"+sql1);

			log.info(" *** tesnjpjpAddView SQL *****" + sql);

			paramMap.put("edo_asn_nbr", edo_asn_nbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {

				String mft_seq_nbr = CommonUtility.deNull(rs.getString("mft_seq_nbr"));
				String blnbrpkgs = rs.getString("mf_nbr_pkgs");
				String balance = getBLNnbr(mft_seq_nbr);

				int bal1 = Integer.parseInt(blnbrpkgs) - Integer.parseInt(balance);

				tesnjpjpobj.setEdo_asn_nbr(edo_asn_nbr);
				// tesnjpjpobj.setNum_pkgs(CommonUtility.deNull(rs.getString("mf_nbr_pkgs")));
				tesnjpjpobj.setNum_pkgs("" + bal1);
				tesnjpjpobj.setCargo_type(CommonUtility.deNull(rs.getString("crg_type")));
				tesnjpjpobj.setCargo_type_nm(CommonUtility.deNull(rs.getString("crg_type_nm")));
				tesnjpjpobj.setPkg_type(CommonUtility.deNull(rs.getString("pkg_type")));
				tesnjpjpobj.setPkg_type_nm(CommonUtility.deNull(rs.getString("pkg_desc")));
				// START CR FTZ - NS JUNE 2024
				String hsCodeDisp = this.getHsCodeDisplay(edo_asn_nbr, mft_seq_nbr);
				tesnjpjpobj.setHs_code(CommonUtil.deNull(hsCodeDisp).isEmpty() ? rs.getString("hs_code") : hsCodeDisp);
				// START CR FTZ - NS JUNE 2024
				tesnjpjpobj.setDg_ind(CommonUtility.deNull(rs.getString("dg_ind")));
				tesnjpjpobj.setCrg_desc(CommonUtility.deNull(rs.getString("crg_des")));
				tesnjpjpobj.setBk_ref_nbr(bk_ref_nbr);
				tesnjpjpobj.setCntr_type(CommonUtility.deNull(rs.getString("cntr_type")));
				tesnjpjpobj.setCntr_size(CommonUtility.deNull(rs.getString("cntr_size")));
				tesnjpjpobj.setOld_mark(CommonUtility.deNull(rs.getString("mft_markings")));
				tesnjpjpobj.setMft_seq_nbr(CommonUtility.deNull(rs.getString("shortnbr")));
				tesnjpjpobj.setTrns_nbr_pkgs(CommonUtility.deNull(rs.getString("trans_nbr_pkgs")));
				tesnjpjpobj.setEdo_nbr_pkgs(CommonUtility.deNull(rs.getString("edo_nbr_pkgs")));
				tesnjpjpobj.setIn_voy_var_nbr(CommonUtility.deNull(rs.getString("var_nbr")));
				tesnjpjpobj.setDn_nbr_pkgs(CommonUtility.deNull(rs.getString("dn_nbr_pkgs")));
				vsl_nm_nbr = in_voy_nbr_nm(CommonUtility.deNull(rs.getString("var_nbr")));
				// Added by VietNguyen 16/04/2014 DPE
				if ((Integer.parseInt(tesnjpjpobj.getEdo_nbr_pkgs()) - Integer.parseInt(tesnjpjpobj.getDn_nbr_pkgs())
						- Integer.parseInt(tesnjpjpobj.getTrns_nbr_pkgs())) < 1) {
					log.info("Balance pkgs for ASN is not enough for the TESN");
					throw new BusinessException("Balance pkgs for ASN is not enough for the TESN");
				}
				tesnjpjpobj.setEdo_nom_wt(CommonUtility.deNull(rs.getString("edo_nom_wt")));
				tesnjpjpobj.setEdo_nom_vol(CommonUtility.deNull(rs.getString("edo_nom_vol")));
				tesnjpjpobj.setCaCustCd(CommonUtility.deNull(rs.getString("ca_cust_cd")));
				// Added by VietNguyen 16/04/2014 DPE
				StringTokenizer strToken = new StringTokenizer(vsl_nm_nbr, ",", false);
				while (strToken.hasMoreElements()) {
					vsl_voy_nbr = strToken.nextToken();
					vsl_nm = strToken.nextToken();
				}
				tesnjpjpobj.setIn_voy_nbr(vsl_voy_nbr);
				tesnjpjpobj.setIn_vsl_nm(vsl_nm);
				cont_nbr = cont_nbr(CommonUtility.deNull(rs.getString("mft_seq_nbr")));
				strToken = new StringTokenizer(cont_nbr, ",", false);
				if (strToken.hasMoreElements()) {
					tesnjpjpobj.setCont1(strToken.nextToken());
					tesnjpjpobj.setCont2(strToken.nextToken());
					tesnjpjpobj.setCont3(strToken.nextToken());
					tesnjpjpobj.setCont4(strToken.nextToken());
				} else {
					tesnjpjpobj.setCont1(" ");
					tesnjpjpobj.setCont2(" ");
					tesnjpjpobj.setCont3(" ");
					tesnjpjpobj.setCont4(" ");
				}
			}

			log.info(" *** tesnjpjpAddView SQL *****" + sql1);
			paramMap.put("bk_ref_nbr", bk_ref_nbr);
			log.info(" paramMap: " + paramMap);
			rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
			if (rs1.next()) {
				tesnjpjpobj.setPort_dis_cd(CommonUtility.deNull(rs1.getString("port_dis")));
				tesnjpjpobj.setPort_dis_nm(CommonUtility.deNull(rs1.getString("port_nm")));
				tesnjpjpobj.setShipper_nm(CommonUtility.deNull(rs1.getString("shipper_nm")));
				tesnjpjpobj.setOut_voy_var_nbr(CommonUtility.deNull(rs1.getString("var_nbr")));

				tesnjpjpobj.setBk_nbr_pkgs(CommonUtility.deNull(rs1.getString("bk_nbr_pkgs")));
				tesnjpjpobj.setBk_wt(CommonUtility.deNull(rs1.getString("bk_wt")));
				tesnjpjpobj.setBk_vol(CommonUtility.deNull(rs1.getString("bk_vol")));
				tesnjpjpobj.setVariance_pkgs(CommonUtility.deNull(rs1.getString("variance_pkgs")));
				tesnjpjpobj.setVariance_wt(CommonUtility.deNull(rs1.getString("variance_wt")));
				tesnjpjpobj.setVariance_vol(CommonUtility.deNull(rs1.getString("variance_vol")));

				out_vsl_nm_nbr = out_voy_nbr_nm(CommonUtility.deNull(rs1.getString("var_nbr")));
				StringTokenizer strToken = new StringTokenizer(out_vsl_nm_nbr, ",", false);
				while (strToken.hasMoreElements()) {
					out_vsl_voy_nbr = strToken.nextToken();
					out_vsl_nm = strToken.nextToken();
				}
				tesnjpjpobj.setOut_voy_nbr(out_vsl_voy_nbr);
				tesnjpjpobj.setOut_vsl_nm(out_vsl_nm);
			}
			log.info("END: *** tesnjpjpAddView Result *****" + tesnjpjpobj.toString());

		} catch (NullPointerException e) {
			log.info("Exception tesnjpjpAddView : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception tesnjpjpAddView : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception tesnjpjpAddView : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: tesnjpjpAddView  DAO  END");
		}
		return tesnjpjpobj;
	}

	private String getBLNnbr(String mftsqnbr) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String sql = "";
		String total = "";
		sql = "select sum(nvl(TRANS_NBR_PKGS,0)+nvl(CUT_OFF_NBR_PKGS,0)) from gb_edo edo where MFT_SEQ_NBR =:mftsqnbr ";
		try {
			log.info("START: getBLNnbr  DAO  Start Obj " + " mftsqnbr:" + CommonUtility.deNull(mftsqnbr));

			log.info(" *** getBLNnbr SQL *****" + sql);

			paramMap.put("mftsqnbr", mftsqnbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				total = rs.getString(1);
			}

			log.info("END: *** getBLNnbr Result *****" + total.toString());

		} catch (NullPointerException e) {
			log.info("Exception getBLNnbr : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBLNnbr : " , e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getBLNnbr  DAO  END");
		}
		return total;
	}

	@Override
	public void tesnVerify_edo_bk(String edo_asn_nbr, String bk_ref_nbr, String comp_code) throws BusinessException {
		log.info("START: tesnVerify_edo_bk  DAO  Start Obj " + " edo_asn_nbr:" + CommonUtility.deNull(edo_asn_nbr) + " bk_ref_nbr:" + CommonUtility.deNull(bk_ref_nbr) 
		+ " bk_ref_nbr:" + CommonUtility.deNull(bk_ref_nbr));
		edo_asn_nbr = edo_asn_nbr.trim();
		bk_ref_nbr = bk_ref_nbr.trim();
		// boolean chkEdoNo = chkEdoNoForDPE(edo_asn_nbr, comp_code);
		// log.info("chkEDO NO in TESNVERIFYEDOBK***"+ chkEdoNo);
		// boolean chkBkref = chkBkref(bk_ref_nbr, comp_code);
		// log.info(" chkBkRef in TESNVERIFYEDOBK:"+ chkBkref +
		// "bk_ref_nbr:"+bk_ref_nbr+":len:"+bk_ref_nbr.length());

		// boolean isLocalCargo = chkEdoNoForDPE(edo_asn_nbr, comp_code);
		// if (!isLocalCargo) {
		// log.info("Writing from TesnJpJpEjb.verify");
		// log.info("Not allow local EDO to create TESN JP JP " + edo_asn_nbr);
		// throw new BusinessException("Not allow local EDO to create TESN JP JP");
		// }

		boolean chkBkrefDPE = chkBkrefDPE(bk_ref_nbr);
		if (!chkBkrefDPE) {
			log.info("Writing from TesnJpJpEjb.verify");
			log.info("This BK areadly create TESN JPJP before");
			throw new BusinessException("The Booking Number Cannot be Used.");
		}
		
		// START - ITSM #40370 Added to prevent inactive edo status tesn created - NS APRIL 2023
		boolean chkEDOstatus = chkEdoStatus(edo_asn_nbr);
		if (!chkEDOstatus) {
			log.info("Inactive EDO");
			throw new BusinessException("The Edo is inactive.");
		}
		// END - ITSM #40370 Added to prevent inactive edo status tesn created - NS APRIL 2023

		boolean validateEdoBkRef = chkEdoBKWhenAdding(edo_asn_nbr, comp_code, bk_ref_nbr);
		if (!validateEdoBkRef) {
			log.info("Writing from TesnJpJpEjb.verify");
			log.info("This can be used only by Cargo EDO ADP or ESN Declarant " + edo_asn_nbr);
			throw new BusinessException("The Edo or Booking Reference entered cannot be Used.");
		}
		boolean validateLocalEDO = chkLocalEDOWhenAdding(edo_asn_nbr);
		if (!validateLocalEDO) {
			log.info("Writing from TesnJpJpEjb.verify");
			log.info("Unable to add Local EDO " + edo_asn_nbr);
			throw new BusinessException("Local EDO cannot be Used.");
		}
		/*
		 * if (!chkBkref) { log.info("Writing from TesnJpJpEJB.Verify");
		 * log.info("Invalid Booking Reference No " + bk_ref_nbr); throw new
		 * BusinessException("M21401"); }
		 */
		if (!"JP".equalsIgnoreCase(comp_code)) {
			boolean chkAtuDttmBref = chkDttmOfSecondCarrierVsl(bk_ref_nbr);
			if (!chkAtuDttmBref) {
				log.info("Writing from TesnJpJpEJB.Verify");
				log.info("Invalid Booking Reference No " + bk_ref_nbr);
				throw new BusinessException("Unable to create TESN after ATU of second vessel");
			}
		}
	}

	// START - ITSM #40370 Added to prevent inactive edo status tesn created - NS APRIL 2023
	private boolean chkEdoStatus(String edo_asn_nbr) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		boolean edoActive = false;
		SqlRowSet rs = null;
		String sql = "";
		try {
			log.info("START: chkEdoStatus  DAO  Start edo_asn_nbr: " + CommonUtility.deNull(edo_asn_nbr));
			sql = "SELECT * FROM gb_edo where edo_asn_nbr=:edo_asn_nbr AND EDO_STATUS ='A'";
			paramMap.put("edo_asn_nbr", edo_asn_nbr);
			log.info("sql:" + sql + " ,paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				edoActive = true;
			}
		} catch (Exception e) {
			log.info("Exception chkEdoStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkEdoStatus  DAO  END Result:" + edoActive);
		}
		return edoActive;
	}
	// END - ITSM #40370 Added to prevent inactive edo status tesn created - NS APRIL 2023

	private boolean chkLocalEDOWhenAdding(String edo_asn_nbr) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String sql = "";
		boolean localEDO = true;
		sql = "select * from gb_edo where edo_asn_nbr=:edo_asn_nbr AND EDO_STATUS ='A' and crg_status  = 'L' ";

		try {

			log.info("START: chkLocalEDOWhenAdding  DAO  Start Obj " + " edo_asn_nbr:" + CommonUtility.deNull(edo_asn_nbr));

			log.info(" *** chkLocalEDOWhenAdding SQL *****" + sql);

			paramMap.put("edo_asn_nbr", edo_asn_nbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				localEDO = false;
			} else {
				localEDO = true;
			}
			log.info("END: *** chkLocalEDOWhenAdding Result *****" + localEDO);

		} catch (NullPointerException e) {
			log.info("Exception chkLocalEDOWhenAdding : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkLocalEDOWhenAdding : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkLocalEDOWhenAdding  DAO  END");
		}

		return localEDO;
	}

	private boolean chkEdoBKWhenAdding(String edo_asn_nbr, String comp_code, String bk_ref_nbr)
			throws BusinessException {

		StringBuffer sb = new StringBuffer();
		String sql = "";
		boolean edo_asn_nbr_fl = false;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		try {
			log.info("START: chkEdoBKWhenAdding  DAO  Start Obj " + " edo_asn_nbr:" + CommonUtility.deNull(edo_asn_nbr) + " comp_code:"
					+ CommonUtility.deNull(comp_code) + " bk_ref_nbr:" + CommonUtility.deNull(bk_ref_nbr));

			edo_asn_nbr_fl = false;
			// Add by VietNguyen 10/01/2014 : START

			if (comp_code.length() != 0 && comp_code.equals("JP")) {
				sql = "SELECT bk_ref_nbr FROM bk_details,vessel_call WHERE bk_ref_nbr=:bk_ref_nbr and bk_status='A' and esn_declared='N'  and bk_details.VAR_NBR= vessel_call.vv_cd and  vessel_call.vv_status_ind not in ('CX','CL')";
			} else {
				sql = "SELECT bk_ref_nbr FROM bk_details,vessel_call WHERE bk_ref_nbr=:bk_ref_nbr and bk_status='A' and esn_declared='N' and declarant_cd=:comp_code and bk_details.VAR_NBR= vessel_call.vv_cd and vessel_call.gb_close_shp_ind !='Y' and vessel_call.vv_status_ind not in ('CX','CL','UB')";
			}

			log.info(" *** chkEdoBKWhenAdding SQL *****" + sql);

			if (comp_code.length() != 0 && comp_code.equals("JP")) {
				paramMap.put("bk_ref_nbr", bk_ref_nbr);
			} else {
				paramMap.put("bk_ref_nbr", bk_ref_nbr);
				paramMap.put("comp_code", comp_code);
			}
			
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				String sql1 = "";

				if (!"JP".equalsIgnoreCase(comp_code)) {

					sb.append("SELECT edo_asn_nbr FROM gb_edo,vessel_call ");
					sb.append(" WHERE edo_status='A' ");
					sb.append(" and gb_edo.crg_status in ('T','R')  ");
					sb.append(" and  gb_edo.VAR_NBR = vessel_call.vv_cd  ");
					sb.append("and (gb_edo.CA_CUST_CD = :comp_code ");
					sb.append("or gb_edo.ADP_CUST_CD = :comp_code )");
					sb.append(" and vessel_call.vv_status_ind not in ('CX') and edo_asn_nbr = :edo_asn_nbr ");
					sql1 = sb.toString();
					log.info("chkEdoBKWhenAdding( ***:" + sql1 + ":" + comp_code + ":" + edo_asn_nbr);

					log.info(" *** chkEdoBKWhenAdding SQL *****" + sql1);

					paramMap.put("comp_code", comp_code);
					paramMap.put("edo_asn_nbr", edo_asn_nbr);
					log.info(" paramMap: " + paramMap);
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
					if (rs1.next()) {
						edo_asn_nbr_fl = true;
					}

					if (!edo_asn_nbr_fl) {
						String sql2 = "select * from bk_details where bk_ref_nbr=:bk_ref_nbr and bk_create_cd= :comp_code ";

						log.info(" *** chkEdoBKWhenAdding SQL *****" + sql2);

						paramMap.put("bk_ref_nbr", bk_ref_nbr);
						paramMap.put("comp_code", comp_code);
						log.info(" paramMap: " + paramMap);
						rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);
						if (rs2.next()) {
							edo_asn_nbr_fl = true;
						}
					}
				} else {
					edo_asn_nbr_fl = true;
				}
			}

			log.info("END: *** chkEdoBKWhenAdding Result *****" + edo_asn_nbr_fl);

		} catch (NullPointerException e) {
			log.info("Exception chkEdoBKWhenAdding : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkEdoBKWhenAdding : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkEdoBKWhenAdding  DAO  END");
		}
		return edo_asn_nbr_fl;
	}

	private boolean chkBkrefDPE(String bk_ref_nbr) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String sql = "";
		boolean bk_ref_nbr_fl = true;
		sql = "select * from esn esn where esn.bk_ref_nbr=:bk_ref_nbr AND ESN.ESN_STATUS ='A'";

		try {

			log.info("START: chkBkrefDPE  DAO  Start Obj " + " bk_ref_nbr:" + CommonUtility.deNull(bk_ref_nbr));

			log.info(" *** chkBkrefDPE SQL *****" + sql);
			paramMap.put("bk_ref_nbr", bk_ref_nbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				bk_ref_nbr_fl = false;
			} else {
				bk_ref_nbr_fl = true;
			}

			log.info("END: *** chkBkrefDPE Result *****" + bk_ref_nbr_fl);

		} catch (NullPointerException e) {
			log.info("Exception chkBkrefDPE : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkBkrefDPE : " , e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: chkBkrefDPE  DAO  END");
		}

		return bk_ref_nbr_fl;
	}

	@Override
	public void tesnjpjpDelete(String vesselvoyageno, String tesn_nbr, String nbr_pkgs, String edo_asn_nbr,
			String bk_ref_nbr, String UserID) throws BusinessException {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		try {
			log.info("START: tesnjpjpDelete  DAO  Start Obj " + " vesselvoyageno:" + CommonUtility.deNull(vesselvoyageno) + " tesn_nbr:"
					+ CommonUtility.deNull(tesn_nbr) + " nbr_pkgs:" + CommonUtility.deNull(nbr_pkgs) + " edo_asn_nbr:"
					+ CommonUtility.deNull(edo_asn_nbr) + " bk_ref_nbr:" + CommonUtility.deNull(bk_ref_nbr)
					+ " UserID:" + CommonUtility.deNull(UserID));

			String sql1 = "update esn set esn_status='X',last_modify_user_id=:UserID,last_modify_dttm=sysdate where esn_asn_nbr=:tesn_nbr";
			// log.info("sql1"+sql1);
			String sql2 = "update bk_details set esn_declared='N',last_modify_dttm=sysdate,last_modify_user_id=:UserID where bk_ref_nbr=:bk_ref_nbr ";
			// log.info("sql2"+sql2);
			String sql3 = "update gb_edo set trans_nbr_pkgs = trans_nbr_pkgs - :nbr_pkgs,last_modify_dttm=sysdate,last_modify_user_id=:UserID where edo_asn_nbr=:edo_asn_nbr";
			// log.info("sql3"+sql3);

			log.info(" *** tesnjpjpDelete SQL *****" + sql1);

			paramMap.put("UserID", UserID);
			paramMap.put("tesn_nbr", tesn_nbr);
			log.info(" paramMap: " + paramMap);
			namedParameterJdbcTemplate.update(sql1, paramMap);

			log.info(" *** tesnjpjpDelete SQL *****" + sql2);

			paramMap.put("UserID", UserID);
			paramMap.put("bk_ref_nbr", bk_ref_nbr);
			log.info(" paramMap: " + paramMap);
			namedParameterJdbcTemplate.update(sql2, paramMap);

			log.info(" *** tesnjpjpDelete SQL *****" + sql3);

			paramMap.put("nbr_pkgs", nbr_pkgs);
			paramMap.put("UserID", UserID);
			paramMap.put("edo_asn_nbr", edo_asn_nbr);
			log.info(" paramMap: " + paramMap);
			namedParameterJdbcTemplate.update(sql3, paramMap);

			// added by Revathi
			if (logStatusGlobal.equalsIgnoreCase("Y")) {

				String sqllog = "";
				String strESNTransNbr = "";
				String strBKTransNbr = "";
				String strGbEdoTransNbr = "";
				String strTransNbrPkgs = "";

				// for inserting esn_trans

				sqllog = "SELECT MAX(TRANS_NBR) FROM esn_trans WHERE ESN_ASN_NBR=:tesn_nbr";

				log.info(" *** tesnjpjpDelete SQL *****" + sqllog);

				paramMap.put("tesn_nbr", tesn_nbr);
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
				while (rs.next()) {
					strESNTransNbr = CommonUtility.deNull(rs.getString(1));
				}

				if (strESNTransNbr.equalsIgnoreCase("")) {
					strESNTransNbr = "0";
				} else {
					strESNTransNbr = String.valueOf(Integer.parseInt(strESNTransNbr) + 1);
				}

				// for inserting gb_edo_trans
				sqllog = null;
				sqllog = "SELECT MAX(TRANS_NBR) FROM gb_edo_trans WHERE EDO_ASN_NBR=:edo_asn_nbr ";

				log.info(" *** tesnjpjpDelete SQL *****" + sqllog);

				paramMap.put("edo_asn_nbr", edo_asn_nbr);
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
				while (rs.next()) {
					strGbEdoTransNbr = CommonUtility.deNull(rs.getString(1));
				}

				if (strGbEdoTransNbr.equalsIgnoreCase("")) {
					strGbEdoTransNbr = "0";
				} else {
					strGbEdoTransNbr = String.valueOf(Integer.parseInt(strGbEdoTransNbr) + 1);
				}
				// for selecting trans_nbr_packages

				sqllog = null;
				sqllog = "SELECT trans_nbr_pkgs FROM gb_edo WHERE EDO_ASN_NBR=:edo_asn_nbr ";

				log.info(" *** tesnjpjpDelete SQL *****" + sqllog);

				paramMap.put("edo_asn_nbr", edo_asn_nbr);
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
				while (rs.next()) {
					strTransNbrPkgs = CommonUtility.deNull(rs.getString(1));
				}

				if (strTransNbrPkgs.equalsIgnoreCase("")) {
					strTransNbrPkgs = "0";
				}

				// for inserting BK_DETAILS_trans

				sqllog = null;
				sqllog = "SELECT MAX(TRANS_NBR) FROM BK_DETAILS_TRANS WHERE BK_REF_NBR=:bk_ref_nbr ";

				log.info(" *** tesnjpjpDelete SQL *****" + sqllog);

				paramMap.put("bk_ref_nbr", bk_ref_nbr);
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
				while (rs.next()) {
					strBKTransNbr = CommonUtility.deNull(rs.getString(1));
				}

				if (strBKTransNbr.equalsIgnoreCase("")) {
					strBKTransNbr = "0";
				} else {
					strBKTransNbr = String.valueOf(Integer.parseInt(strBKTransNbr) + 1);
				}

				String strESNTrans = "insert into esn_trans(trans_nbr,esn_asn_nbr,esn_status,last_modify_user_id,last_modify_dttm) values (:strESNTransNbr,:tesn_nbr,'X',:UserID,sysdate)";

				String strBKDetailsTrans = "INSERT INTO BK_DETAILS_TRANS(TRANS_NBR,BK_REF_NBR,ESN_DECLARED,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) VALUES(:strBKTransNbr,:bk_ref_nbr,'N',:UserID,sysdate)";

				log.info(" *** tesnjpjpDelete SQL *****" + strESNTrans);

				paramMap.put("strESNTransNbr", strESNTransNbr);
				paramMap.put("tesn_nbr", tesn_nbr);
				paramMap.put("UserID", UserID);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strESNTrans, paramMap);

				String strGbEdoTrans = "insert into gb_edo_trans(trans_nbr,edo_asn_nbr,trans_nbr_pkgs,last_modify_user_id,last_modify_dttm) values (:strGbEdoTransNbr,:edo_asn_nbr,:strTransNbrPkgs,:UserID,sysdate)";

				log.info(" *** tesnjpjpDelete SQL *****" + strGbEdoTrans);

				paramMap.put("strGbEdoTransNbr", strGbEdoTransNbr);
				paramMap.put("edo_asn_nbr", edo_asn_nbr);
				paramMap.put("strTransNbrPkgs", strTransNbrPkgs);
				paramMap.put("UserID", UserID);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strGbEdoTrans, paramMap);

				log.info(" *** tesnjpjpDelete SQL *****" + strBKDetailsTrans);

				paramMap.put("strBKTransNbr", strBKTransNbr);
				paramMap.put("bk_ref_nbr", bk_ref_nbr);
				paramMap.put("UserID", UserID);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strBKDetailsTrans, paramMap);

			}

			log.info("END: *** tesnjpjpDelete Result *****");

		} catch (NullPointerException e) {
			log.info("Exception tesnjpjpDelete : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception tesnjpjpDelete : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: tesnjpjpDelete  DAO  END");
		}

	}

	@Override
	public void tesnVerify_delete_DN(String tesn_nbr, String out_voy_var_nbr) throws BusinessException {
		log.info("START: tesnVerify_delete_DN "+" tesn_nbr:"+CommonUtility.deNull(tesn_nbr) 
		+" out_voy_var_nbr:"+ CommonUtility.deNull(out_voy_var_nbr) );
		int chkDelete = tesnjpjpValidCheck_DN(tesn_nbr, out_voy_var_nbr);
		if (chkDelete == 0) {
			log.info("Writing from TesnJpJpEjb.valid delete");
			log.info("Invalid delete " + tesn_nbr);
			throw new BusinessException("Record Cannot be Deleted because UA printed.");
		}
	}

	@Override
	public void tesnVerify_delete_status(String tesn_nbr, String out_voy_var_nbr) throws BusinessException {
		log.info("START: tesnVerify_delete_status "+" tesn_nbr:"+CommonUtility.deNull(tesn_nbr) 
		+" out_voy_var_nbr:"+ CommonUtility.deNull(out_voy_var_nbr) );
		int chkDelete = tesnjpjpValidCheck_status(tesn_nbr, out_voy_var_nbr);
		if (chkDelete == 0) {
			log.info("Writing from TesnJpJpEjb.valid delete");
			log.info("Invalid delete " + tesn_nbr);
			throw new BusinessException("Record Cannot be Deleted because UA printed.");
		}
	}

	@Override
	public void tesnVerify_delete(String tesn_nbr, String out_voy_var_nbr) throws BusinessException {
		log.info("START: tesnVerify_delete "+" tesn_nbr:"+CommonUtility.deNull(tesn_nbr) 
		+" out_voy_var_nbr:"+ CommonUtility.deNull(out_voy_var_nbr) );
		int chkDelete = tesnjpjpValidCheck(tesn_nbr, out_voy_var_nbr);
		if (chkDelete == 0) {
			log.info("Writing from TesnJpJpEjb.valid delete");
			log.info("Invalid delete " + tesn_nbr);
			throw new BusinessException("Record Cannot be Deleted because UA printed.");
		}
	}

	@Override
	public List<String> tesnjpjpGetAccNo(String comp_code, String vsl_cd, String bk_ref_nbr) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		String temp = "";
		List<String> GetAccNo = new ArrayList<String>();
		String sql = "";

		try {
			log.info("START: tesnjpjpGetAccNo  DAO  Start Obj " + " comp_code:" + CommonUtility.deNull(comp_code) + " vsl_cd:" + CommonUtility.deNull(vsl_cd)
					+ " bk_ref_nbr:" + CommonUtility.deNull(bk_ref_nbr));

			if (comp_code != null && comp_code.equals("JP"))
				sql = "select co_nm,acct_nbr from company_code,cust_acct,bk_details bk where cust_cd = co_cd and acct_status_cd='A' and business_type like '%G%' and trial_ind='N' and acct_nbr is not null and  co_cd=bk.declarant_cd and bk_ref_nbr=:bk_ref_nbr ";
			else
				sql = "select co_nm,acct_nbr from company_code,cust_acct where cust_cd = co_cd and acct_status_cd='A' and business_type like '%G%' and trial_ind='N' and acct_nbr is not null and  co_cd=:comp_code ";
			// log.info("GetAccNo sql"+sql);
			String sql1 = "select co_nm,acct_nbr from company_code,cust_acct,vessel_call where cust_cd = co_cd and acct_status_cd='A' and business_type like '%G%' and trial_ind='N' and acct_nbr is not null  and co_cd = create_cust_cd and vv_cd=:vsl_cd";
			// log.info("GetAccNo sql"+sql);

			log.info(" *** tesnjpjpGetAccNo SQL *****" + sql);

			if (comp_code != null && comp_code.equals("JP")) {
				paramMap.put("bk_ref_nbr", bk_ref_nbr);
			} else {
				paramMap.put("comp_code", comp_code);
			}
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				temp = "";
				temp = "TESN Declarant" + "-" + (CommonUtility.deNull(rs.getString("acct_nbr")));
				GetAccNo.add(temp);
			}

			log.info(" *** tesnjpjpGetAccNo SQL *****" + sql1);

			paramMap.put("vsl_cd", vsl_cd);
			log.info(" paramMap: " + paramMap);
			rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
			while (rs1.next()) {
				temp = "";
				temp = "Shipping Agent" + "-" + (CommonUtility.deNull(rs1.getString("acct_nbr")));
				GetAccNo.add(temp);
			}
			log.info("END: *** tesnjpjpGetAccNo Result *****" + GetAccNo.toString());

		} catch (NullPointerException e) {
			log.info("Exception tesnjpjpGetAccNo : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception tesnjpjpGetAccNo : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: tesnjpjpGetAccNo  DAO  END");
		}

		return GetAccNo;

	}

	@Override
	public String getEdoAcct(String txt_edo_asn_nbr, String bk_ref_nbr, String comp_code) throws BusinessException {
		String scheme = getScheme(bk_ref_nbr, comp_code);
		if (StringUtils.isNotBlank(scheme) && ProcessChargeConst.LCT_SCHEME.equals(scheme)) {
			StringBuffer sql = new StringBuffer("select acct_nbr from gb_edo where edo_asn_nbr = :txt_edo_asn_nbr");

			Map<String, Object> paramMap = new HashMap<String, Object>();
			SqlRowSet rs = null;
			String result = "";

			try {
				log.info("START: getEdoAcct  DAO  Start Obj " + " txt_edo_asn_nbr:" + CommonUtility.deNull(txt_edo_asn_nbr) + " bk_ref_nbr:"
						+ CommonUtility.deNull(bk_ref_nbr) + " comp_code:" + CommonUtility.deNull(comp_code));

				log.info(" *** getEdoAcct SQL *****" + sql.toString());

				paramMap.put("txt_edo_asn_nbr", txt_edo_asn_nbr);
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
				while (rs.next()) {
					result = CommonUtility.deNull(rs.getString("acct_nbr"));
				}

				log.info("END: *** getEdoAcct Result *****" + result);

			} catch (NullPointerException e) {
				log.info("Exception getEdoAcct : " , e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception getEdoAcct : " , e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: getEdoAcct  DAO  END");
			}
			return result;
		}
		return null;
	}

	private String getScheme(String bk_ref_nbr, String comp_code) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String sql = "";

		try {
			log.info("START: getScheme  DAO  Start Obj " + " bk_ref_nbr:" + CommonUtility.deNull(bk_ref_nbr) + " comp_code:" + CommonUtility.deNull(comp_code));

			if (comp_code.length() != 0 && comp_code.equals("JP"))
				sql = "SELECT scheme FROM bk_details,vessel_call WHERE bk_ref_nbr=:bk_ref_nbr and bk_status='A' and esn_declared='N'  and bk_details.VAR_NBR= vessel_call.vv_cd and vessel_call.gb_close_shp_ind !='Y' and vessel_call.vv_status_ind not in ('CX','CL')";
			else
				sql = "SELECT scheme FROM bk_details,vessel_call WHERE bk_ref_nbr=:bk_ref_nbr and bk_status='A' and esn_declared='N' and declarant_cd=:comp_code and bk_details.VAR_NBR= vessel_call.vv_cd and vessel_call.gb_close_shp_ind !='Y' and vessel_call.vv_status_ind not in ('CX','CL')";

			log.info(" *** getScheme SQL *****" + sql);

			if (comp_code.length() != 0 && comp_code.equals("JP")) {
				paramMap.put("bk_ref_nbr", bk_ref_nbr);
			} else {
				paramMap.put("bk_ref_nbr", bk_ref_nbr);
				paramMap.put("comp_code", comp_code);
			}

			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			String scheme = null;
			if (rs.next()) {
				scheme = rs.getString(1);
			}

			log.info("END: *** getScheme Result ***** " + scheme);
			return scheme;

		} catch (NullPointerException e) {
			log.info("Exception getScheme : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getScheme : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getScheme  DAO  END");
		}

	}

	@Override
	public boolean chkDttmOfSecondCarrierVsl(String bk_ref_nbr) throws BusinessException {

		String sql;
		int count = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		boolean result = false;

		try {
			log.info("START: chkDttmOfSecondCarrierVsl  DAO  Start Obj " + " bk_ref_nbr:" + CommonUtility.deNull(bk_ref_nbr));

			sb.append("SELECT count(bk_ref_nbr) FROM bk_details bk join vessel_call vsl on bk.VAR_NBR = vsl.vv_cd ");
			sb.append(" WHERE bk_ref_nbr = :bk_ref_nbr and bk_status='A' ");
			sb.append(" AND (vsl.vv_status_ind not in ('UB', 'CX', 'CL') and  nvl(vsl.GB_CLOSE_SHP_IND,'N') != 'Y')");

			sql = sb.toString();
			// + "join berthing be on vsl.vv_cd = be.vv_cd and shift_ind = 1 "

			// "and vsl.gb_close_shp_ind !='Y' and vsl.vv_status_ind not in ('CX','CL') "
			// + " AND ATU_DTTM IS NULL AND vsl.GB_CLOSE_SHP_IND != 'Y' ";

			log.info(" *** chkDttmOfSecondCarrierVsl SQL *****" + sql);

			paramMap.put("bk_ref_nbr", bk_ref_nbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				count = rs.getInt(1);
				if (count > 0)
					result = true;
				else
					result = false;
			}
			log.info("END: *** chkDttmOfSecondCarrierVsl Result *****" + result);

		} catch (NullPointerException e) {
			log.info("Exception chkDttmOfSecondCarrierVsl : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkDttmOfSecondCarrierVsl : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkDttmOfSecondCarrierVsl  DAO  END");
		}

		return result;
	}

	@Override
	public void tesnVerify_amend_status(String tesn_nbr, String out_voy_var_nbr) throws BusinessException {
		log.info("START: tesnVerify_amend_status "+" tesn_nbr:"+CommonUtility.deNull(tesn_nbr) +" out_voy_var_nbr:"+CommonUtility.deNull(out_voy_var_nbr) );

		int chkAmend = tesnjpjpValidCheck_status(tesn_nbr, out_voy_var_nbr);
		if (chkAmend == 0) {
			log.info("Writing from TesnJpJpEjb.valid amend");
			log.info("Invalid Amend " + tesn_nbr);
			throw new BusinessException("Record Cannot be Modified.");
		}
	}

	@Override
	public void tesnVerify_amend(String tesn_nbr, String out_voy_var_nbr) throws BusinessException {
		log.info("START: tesnVerify_amend "+" tesn_nbr:"+CommonUtility.deNull(tesn_nbr) 
		+" out_voy_var_nbr:"+ CommonUtility.deNull(out_voy_var_nbr) );
		int chkAmend = tesnjpjpValidCheck(tesn_nbr, out_voy_var_nbr);
		if (chkAmend == 0) {
			log.info("Writing from TesnJpJpEjb.valid Amend");
			log.info("Invalid Amend " + tesn_nbr);
			throw new BusinessException("Record Cannot be Modified.");
		}
	}

	@Override
	public void tesnjpjpAmendForDPE(String vesselvoyageno, String tesn_nbr, String ld_ind, String nbr_pkgs,
			String pay_mode, String acc_num, String val_pkgs, String edo_asn_nbr, String userid, String edo_nbr_pkgs,
			String prev_nbr_pkgs, String stuffind, String category, String nomWt, String nomVol)
			throws BusinessException {
		String sql = "";
		String sql1 = "";
		String Mssg1 = null;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		SqlRowSet rs4 = null;

		StringBuffer sb = new StringBuffer();

		float bk_nbr_pkgs = 0;
		float bk_wt = 0;
		float bk_vol = 0;
		double variance_pkgs = 0;
		float variance_wt = 0;
		float variance_vol = 0;

		try {
			log.info("START: tesnjpjpAmendForDPE  DAO  Start Obj " + " vesselvoyageno" + CommonUtility.deNull(vesselvoyageno) + " tesn_nbr"
					+ CommonUtility.deNull(tesn_nbr) + " ld_ind" + CommonUtility.deNull(ld_ind) + " nbr_pkgs" + CommonUtility.deNull(nbr_pkgs)
					+ " pay_mode" + CommonUtility.deNull(pay_mode) + " acc_num" + CommonUtility.deNull(acc_num) + " val_pkgs" + CommonUtility.deNull(val_pkgs)
					+ " edo_asn_nbr" + CommonUtility.deNull(edo_asn_nbr) + " userid" + CommonUtility.deNull(userid) + " edo_nbr_pkgs" + CommonUtility.deNull(edo_nbr_pkgs)
					+ " prev_nbr_pkgs" + CommonUtility.deNull(prev_nbr_pkgs) + " stuffind" + CommonUtility.deNull(stuffind) + " category" + CommonUtility.deNull(category)
					+ " nomWt" + CommonUtility.deNull(nomWt) + " nomVol" + CommonUtility.deNull(nomVol));

			String sql_sel = "select nom_wt,nom_vol from gb_edo where edo_asn_nbr=:edo_asn_nbr ";
			String sql6 = "select bk.bk_nbr_pkgs,bk.bk_wt,bk.bk_vol,bk.variance_pkgs,bk.variance_vol,bk.variance_wt from bk_details bk,esn e  where bk.bk_ref_nbr = e.bk_ref_nbr  and e.esn_asn_nbr=:tesn_nbr";

			log.info(" *** tesnjpjpAmendForDPE SQL *****" + sql_sel);
			
			paramMap.put("edo_asn_nbr", edo_asn_nbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql_sel, paramMap);
			
			log.info(" *** tesnjpjpAmendForDPE SQL *****" + sql6);
			paramMap.put("tesn_nbr", tesn_nbr);
			
			log.info(" paramMap: " + paramMap);
			rs4 = namedParameterJdbcTemplate.queryForRowSet(sql6, paramMap);

			float nom_wt = 0, nom_vol = 0;
			float nomwt = 0, nomvol = 0;
			if (rs.next()) {
				try {
					nomwt = Float.parseFloat(rs.getString("nom_wt"));
					nomvol = Float.parseFloat(rs.getString("nom_vol"));
				} catch (Exception exp) {
					log.info(exp);
					nomwt = 0;
					nomvol = 0;
				}
			}
			try {
				nom_wt = (Float.parseFloat(nbr_pkgs) / Float.parseFloat(edo_nbr_pkgs)) * nomwt;
				nom_vol = (Float.parseFloat(nbr_pkgs) / Float.parseFloat(edo_nbr_pkgs)) * nomvol;
			} catch (Exception exp) {
				nom_wt = 0;
				nom_vol = 0;
			}

			if (rs4.next()) {
				try {
					bk_nbr_pkgs = Float.parseFloat(rs4.getString("bk_nbr_pkgs"));
					bk_wt = Float.parseFloat(rs4.getString("bk_wt"));
					bk_vol = Float.parseFloat(rs4.getString("bk_vol"));
					variance_pkgs = Double.parseDouble(rs4.getString("variance_pkgs"));
					variance_wt = Float.parseFloat(rs4.getString("variance_wt"));
					variance_vol = Float.parseFloat(rs4.getString("variance_vol"));
				} catch (Exception exp) {
					log.info(exp);
					bk_nbr_pkgs = 0;
					bk_wt = 0;
					bk_vol = 0;
					variance_pkgs = 0;
					variance_wt = 0;
					variance_vol = 0;
				}
			}
			float bk_nbr_chk = 0;
			float bk_wt_chk = 0;
			float bk_vol_chk = 0;
			try {
				bk_nbr_chk = bk_nbr_pkgs * (1 + (float) (Math.floor(variance_pkgs)) / 100);
				bk_wt_chk = bk_wt * (1 + variance_wt / 100);
				bk_vol_chk = bk_vol * (1 + variance_vol / 100);
			} catch (Exception exp) {
				log.info(exp);
				bk_nbr_chk = 0;
				bk_wt_chk = 0;
				bk_vol_chk = 0;
			}
			log.info("bk_nbr_chk = " + bk_nbr_chk);
			// Add by VietNguyen 08/01/2014: START
			nom_wt = Float.parseFloat(nomWt);
			nom_vol = Float.parseFloat(nomVol);
			// Add by VietNguyen 08/01/2014: END

			if (Float.parseFloat(nbr_pkgs) > bk_nbr_pkgs) {
				Mssg1 = "Local EDO cannot be Used.";
			}
			if (nom_wt > bk_wt_chk) {
				Mssg1 = "Nom. Wt. cannot be greater than the Nom. Wt. in Booking reference";
			}
			if (nom_vol > bk_vol_chk) {
				Mssg1 = "Nom. Vol. cannot be greater than the Nom. Vol. in Booking reference";
			}

			if (Mssg1 == null) {
				sql = "UPDATE tesn_jp_jp SET NBR_PKGS=:nbr_pkgs,PAYMENT_MODE=:pay_mode,LD_IND=:ld_ind,ACCT_NBR=:acc_num,nom_wt=:nom_wt,nom_vol=:nom_vol WHERE ESN_ASN_NBR=:tesn_nbr";
				// log.info("update"+sql);
				log.info(" *** tesnjpjpAmendForDPE SQL *****" + sql);

				paramMap.put("nbr_pkgs", nbr_pkgs);
				paramMap.put("pay_mode", pay_mode);
				paramMap.put("ld_ind", ld_ind);
				paramMap.put("acc_num", acc_num);
				paramMap.put("nom_wt", nom_wt);
				paramMap.put("nom_vol", nom_vol);
				paramMap.put("tesn_nbr", tesn_nbr);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(sql, paramMap);

				// TVS added stuff ind in query 25-09-2003
				sql1 = "UPDATE esn SET stuff_ind=:stuffind,last_modify_user_id=:userid,last_modify_dttm=sysdate, CARGO_CATEGORY_CD=:category WHERE ESN_ASN_NBR=:tesn_nbr";
				// log.info("update"+sql1);
				String sql2 = "update gb_edo set trans_nbr_pkgs=" + val_pkgs + " - " + prev_nbr_pkgs + " + " + nbr_pkgs
						+ ",last_modify_dttm=sysdate, last_modify_user_id=:userid where edo_asn_nbr = :edo_asn_nbr ";

				log.info(" *** tesnjpjpAmendForDPE SQL *****" + sql1);

				paramMap.put("stuffind", stuffind);
				paramMap.put("userid", userid);
				paramMap.put("category", category);
				paramMap.put("tesn_nbr", tesn_nbr);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(sql1, paramMap);

				log.info(" *** tesnjpjpAmendForDPE SQL *****" + sql2);

				paramMap.put("userid", userid);
				paramMap.put("edo_asn_nbr", edo_asn_nbr);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(sql2, paramMap);

				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					String strtransnbr = "";

					// Added by Revathi
					String strESNTransNbr = "";
					String strGbEdoTransNbr = "";
					String strTransNbrPkgs = "";
					// end of Add by Revathi

					String sqllog = "SELECT MAX(TRANS_NBR) FROM TESN_JP_JP_trans WHERE ESN_ASN_NBR=:tesn_nbr ";

					log.info(" *** tesnjpjpAmendForDPE SQL *****" + sqllog);

					paramMap.put("tesn_nbr", tesn_nbr);
					log.info(" paramMap: " + paramMap);
					rs = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
					while (rs.next()) {
						strtransnbr = CommonUtility.deNull(rs.getString(1));
					}

					if (strtransnbr.equalsIgnoreCase("")) {
						strtransnbr = "0";
					} else {
						strtransnbr = String.valueOf(Integer.parseInt(strtransnbr) + 1);
					}

					// Added by Revathi
					// for inserting esn_trans

					sqllog = null;
					sqllog = "SELECT MAX(TRANS_NBR) FROM esn_trans WHERE ESN_ASN_NBR=:tesn_nbr ";

					log.info(" *** tesnjpjpAmendForDPE SQL *****" + sqllog);

					paramMap.put("tesn_nbr", tesn_nbr);
					log.info(" paramMap: " + paramMap);
					rs = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
					while (rs.next()) {
						strESNTransNbr = CommonUtility.deNull(rs.getString(1));
					}

					if (strESNTransNbr.equalsIgnoreCase("")) {
						strESNTransNbr = "0";
					} else {
						strESNTransNbr = String.valueOf(Integer.parseInt(strESNTransNbr) + 1);
					}

					sqllog = null;
					// for inserting gb_edo_trans
					sqllog = "SELECT MAX(TRANS_NBR) FROM gb_edo_trans WHERE EDO_ASN_NBR=:edo_asn_nbr ";

					log.info(" *** tesnjpjpAmendForDPE SQL *****" + sqllog);

					paramMap.put("edo_asn_nbr", edo_asn_nbr);
					log.info(" paramMap: " + paramMap);
					rs = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
					while (rs.next()) {
						strGbEdoTransNbr = CommonUtility.deNull(rs.getString(1));
					}

					if (strGbEdoTransNbr.equalsIgnoreCase("")) {
						strGbEdoTransNbr = "0";
					} else {
						strGbEdoTransNbr = String.valueOf(Integer.parseInt(strGbEdoTransNbr) + 1);
					}
					// for selecting trans_nbr_packages

					sqllog = null;
					sqllog = "SELECT trans_nbr_pkgs FROM gb_edo WHERE EDO_ASN_NBR=:edo_asn_nbr";

					log.info(" *** tesnjpjpAmendForDPE SQL *****" + sqllog);
					paramMap.put("edo_asn_nbr", edo_asn_nbr);
					log.info(" paramMap: " + paramMap);
					rs = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
					while (rs.next()) {
						strTransNbrPkgs = CommonUtility.deNull(rs.getString(1));
					}

					if (strTransNbrPkgs.equalsIgnoreCase("")) {
						strTransNbrPkgs = "0";
					}

					String strESNTrans = "insert into esn_trans(trans_nbr,esn_asn_nbr,stuff_ind,last_modify_user_id,last_modify_dttm,esn_status) values (:strESNTransNbr,:tesn_nbr,:stuffind,:userid,sysdate,'A')";

					String strGbEdoTrans = "insert into gb_edo_trans(trans_nbr,edo_asn_nbr,trans_nbr_pkgs,last_modify_user_id,last_modify_dttm) values (:strGbEdoTransNbr,:edo_asn_nbr,:strTransNbrPkgs,:userid,sysdate)";

					log.info(" *** tesnjpjpAmendForDPE SQL *****" + strESNTrans);

					paramMap.put("strESNTransNbr", strESNTransNbr);
					paramMap.put("tesn_nbr", tesn_nbr);
					paramMap.put("stuffind", stuffind);
					paramMap.put("userid", userid);
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(strESNTrans, paramMap);

					log.info(" *** tesnjpjpAmendForDPE SQL *****" + strGbEdoTrans);

					paramMap.put("strGbEdoTransNbr", strGbEdoTransNbr);
					paramMap.put("edo_asn_nbr", edo_asn_nbr);
					paramMap.put("strTransNbrPkgs", strTransNbrPkgs);

					paramMap.put("userid", userid);
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(strGbEdoTrans, paramMap);

					sb = new StringBuffer();
					sb.append(
							"insert into tesn_jp_jp_trans(esn_asn_nbr,edo_asn_nbr,trans_nbr,ld_ind,nbr_pkgs,nom_wt,nom_vol,acct_nbr,last_modify_user_id,last_modify_dttm)");
					sb.append(
							"values (:tesn_nbr,:edo_asn_nbr,:strtransnbr,:ld_ind,:nbr_pkgs,:nom_wt,:nom_vol,:acc_num,:userid,sysdate)");

					String strUpdatetrans = sb.toString();

					// end of Add by Revathi
					log.info(" *** tesnjpjpAmendForDPE SQL *****" + strUpdatetrans);

					paramMap.put("tesn_nbr", tesn_nbr);
					paramMap.put("edo_asn_nbr", edo_asn_nbr);
					paramMap.put("strtransnbr", strtransnbr);
					paramMap.put("ld_ind", ld_ind);
					paramMap.put("nbr_pkgs", nbr_pkgs);
					paramMap.put("nom_wt", nom_wt);
					paramMap.put("nom_vol", nom_vol);
					paramMap.put("acc_num", acc_num);
					paramMap.put("userid", userid);
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(strUpdatetrans, paramMap);
				}
				// log.info("Yes");
				// con.commit();
			} else {
				throw new BusinessException(Mssg1);
			}
			log.info("END: *** tesnjpjpAmendForDPE Result *****");

		} catch (BusinessException e) {
			log.info("Exception tesnjpjpAmendForDPE : " , e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception tesnjpjpAmendForDPE : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception tesnjpjpAmendForDPE : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: tesnjpjpAmendForDPE  DAO  END");
		}

	}

	@Override
	public void tesnVerify_accno(String acc_num) throws BusinessException {
		log.info("START: tesnVerify_accno "+" acc_num:"+CommonUtility.deNull(acc_num));
		int chkAccNo = tesnjpjpValidAccNo(acc_num);
		if (chkAccNo == 0) {
			log.info("Writing from TesnJpJpEjb.valid acc num");
			log.info("Invalid Account No " + acc_num);
			throw new BusinessException("Account Number Cannot be Used.");
		}
	}

	private int tesnjpjpValidAccNo(String acc_num) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		int flg = 0;
		String sql = "select acct_nbr from cust_acct where  acct_status_cd='A' and business_type like '%G%' and trial_ind='N' and acct_nbr is not null and upper(acct_nbr)=upper(:acc_num)";
		// log.info("validAccNo sql"+sql);
		try {
			log.info("START: tesnjpjpValidAccNo  DAO  Start Obj " + " acc_num:" + CommonUtility.deNull(acc_num));

			log.info(" *** tesnjpjpValidAccNo SQL *****" + sql);

			paramMap.put("acc_num", acc_num);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next())
				flg = 1;
			else
				flg = 0;

			log.info("END: *** tesnjpjpValidAccNo Result *****" + flg);

		} catch (NullPointerException e) {
			log.info("Exception tesnjpjpValidAccNo : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception tesnjpjpValidAccNo : " , e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: tesnjpjpValidAccNo  DAO  END");
		}
		return flg;
	}

	@Override
	public List<TesnEsnListValueObject> getAssignCargo() throws BusinessException {
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		String cc_cd = "";
		String cicos_cd = "";
		String cc_name = "";
		List<TesnEsnListValueObject> maniveclist = new ArrayList<TesnEsnListValueObject>();
		TesnEsnListValueObject esnListValueObject = null;

		// Changed added by Linus on 8 Oct 2003
		sql = "SELECT cc_cd, cicos_cd,cc_name FROM cargo_category_code where cc_status='A'";
		// Before
		// sql = "SELECT cc_cd, cicos_cd,cc_name FROM cargo_category_code";
		// End Change

		try {
			log.info("START: getAssignCargo  DAO  Start Obj ");

			log.info(" *** getAssignCargo SQL *****" + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				esnListValueObject = new TesnEsnListValueObject();
				cc_cd = rs.getString(1);
				cicos_cd = rs.getString(2);
				if (cc_cd.equals("00")) {
					cicos_cd = "G";
				}
				cc_name = rs.getString(3);
				esnListValueObject.setCc_cd(cc_cd);
				esnListValueObject.setCc_name(cc_name);
				esnListValueObject.setCicos_cd(cicos_cd);
				maniveclist.add(esnListValueObject);
			}

			log.info("END: *** getAssignCargo Result *****" + maniveclist.toString());
			return maniveclist;

		} catch (NullPointerException e) {
			log.info("Exception getAssignCargo : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getAssignCargo : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAssignCargo  DAO  END");
		}
	}

	@Override
	public String AssignCrgvalCheck(String esnnbr) throws BusinessException {
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String crgCd = "";

		sql = "SELECT CARGO_CATEGORY_CD from esn WHERE ESN_ASN_NBR =:esnnbr ";

		try {
			log.info("START: AssignCrgvalCheck  DAO  Start Obj " + " esnnbr:" + CommonUtility.deNull(esnnbr));

			log.info(" *** AssignCrgvalCheck SQL *****" + sql);

			paramMap.put("esnnbr", esnnbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				crgCd = rs.getString("CARGO_CATEGORY_CD");
			}

			log.info("END: *** AssignCrgvalCheck Result *****" + crgCd.toString());
			return crgCd;

		} catch (NullPointerException e) {
			log.info("Exception AssignCrgvalCheck : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception AssignCrgvalCheck : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: AssignCrgvalCheck  DAO  END");
		}

	}

	@Override
	public void AssignCrgvalUpdate(String crgval, String esnnbr, String userId) throws BusinessException {
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();

		String sqltlog = "";
		String strInsert_trans = "";
		int stransno = 0;
		int count_trans = 0;

		sql = "UPDATE esn SET CARGO_CATEGORY_CD =:crgval,LAST_MODIFY_USER_ID=:userId,LAST_MODIFY_DTTM = sysdate WHERE ESN_ASN_NBR =:esnnbr ";

		try {
			log.info("START: AssignCrgvalUpdate  DAO  Start Obj " + " crgval:" + CommonUtility.deNull(crgval) + " esnnbr:" + CommonUtility.deNull(esnnbr)
					+ " userId:" + CommonUtility.deNull(userId));

			log.info(" *** AssignCrgvalUpdate SQL *****" + sql);

			paramMap.put("crgval", crgval);
			paramMap.put("userId", userId);
			paramMap.put("esnnbr", esnnbr);
			log.info(" paramMap: " + paramMap);
			int count = namedParameterJdbcTemplate.update(sql, paramMap);

			sqltlog = "SELECT MAX(TRANS_NBR) FROM ESN_TRANS WHERE ESN_ASN_NBR=:esnnbr ";

			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003

				log.info(" *** AssignCrgvalUpdate SQL *****" + sqltlog);

				paramMap.put("esnnbr", esnnbr);
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);
				if (rs.next()) {
					stransno = (rs.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
			}

			sb.append("INSERT INTO ESN_TRANS(TRANS_NBR,ESN_ASN_NBR,");
			sb.append("CARGO_CATEGORY_CD,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
			sb.append("VALUES(:stransno,:esnnbr,:crgval,:userId,sysdate)");
			strInsert_trans = sb.toString();

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				log.info(" *** AssignCrgvalUpdate SQL *****" + strInsert_trans);

				paramMap.put("stransno", stransno);
				paramMap.put("esnnbr", esnnbr);
				paramMap.put("crgval", crgval);
				paramMap.put("userId", userId);
				log.info(" paramMap: " + paramMap);
				count_trans = namedParameterJdbcTemplate.update(strInsert_trans, paramMap);
			}

			if (count == 0) {
				log.info("Writing from TesnJpJp.AssignCrgvalUpdate");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 21/01/2003
				if (count_trans == 0) {
					log.info("Writing from TesnJpJp.AssignCrgvalUpdate");
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}
			}
			log.info("END: *** AssignCrgvalUpdate Result *****" + count);

		} catch (NullPointerException e) {
			log.info("Exception submitBilling : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception submitBilling : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception submitBilling : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: AssignCrgvalUpdate  DAO  END");
		}
	}

	@Override
	public void EsnAssignVslUpdate(String vv_cd, String status, String userId) throws BusinessException {
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {
			log.info("START: EsnAssignVslUpdate  DAO  Start Obj " + " vv_cd:" + CommonUtility.deNull(vv_cd) + " status:" + CommonUtility.deNull(status)
					+ " userId:" + CommonUtility.deNull(userId));

			sql = "UPDATE vessel_call SET mixed_scheme_ind=:status,LAST_MODIFY_DTTM=sysdate ,LAST_MODIFY_USER_ID=:userId WHERE vv_cd =:vv_cd ";

			log.info(" *** EsnAssignVslUpdate SQL *****" + sql);

			paramMap.put("status", status);
			paramMap.put("userId", userId);
			paramMap.put("vv_cd", vv_cd);
			log.info(" paramMap: " + paramMap);
			int count = namedParameterJdbcTemplate.update(sql, paramMap);

			// log.info("sql count "+count);

			if (count == 0) {
				log.info("Writing from TesnJpJp.EsnAssignVslUpdate");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			log.info("END: *** EsnAssignVslUpdate Result *****" + count);

		} catch (NullPointerException e) {
			log.info("Exception submitBilling : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception submitBilling : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception submitBilling : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: EsnAssignVslUpdate  DAO  END");
		}
	}

	@Override
	public void EsnAssignBillUpdate(String acctnbr, String esno, String userid) throws BusinessException {
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String sqltlog = "";
		String strInsert_trans = "";
		int stransno = 0;
		int count_trans = 0;

		boolean bactnbr = checkAccountNbr(acctnbr);
		if (!bactnbr) {
			log.info("Writing from EsnEJB.EsnAssignBillUpdate");
			log.info("Invalid Account Nbr" + acctnbr);
			throw new BusinessException("Invalid account number ");
		}

		sql = "UPDATE tesn_jp_jp SET MIXED_SCHEME_ACCT_NBR=:acctnbr WHERE  ESN_ASN_NBR =:esno ";

		try {
			log.info("START: EsnAssignBillUpdate  DAO  Start Obj " + " acctnbr: " + CommonUtility.deNull(acctnbr) +
					" esno: " + CommonUtility.deNull(esno) + " userid: " + CommonUtility.deNull(userid));

			log.info(" *** EsnAssignBillUpdate SQL *****" + sql);

			paramMap.put("acctnbr", acctnbr);
			paramMap.put("esno", esno);
			log.info(" paramMap: " + paramMap);
			int count = namedParameterJdbcTemplate.update(sql, paramMap);

			sqltlog = "SELECT MAX(TRANS_NBR) FROM TESN_JP_JP_TRANS WHERE ESN_ASN_NBR=:esno ";

			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003
				log.info(" *** EsnAssignBillUpdate SQL *****" + sqltlog);

				paramMap.put("esno", esno);
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);
				if (rs.next()) {
					stransno = (rs.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
			}

			strInsert_trans = strInsert_trans + "INSERT INTO TESN_JP_JP_TRANS(TRANS_NBR,ESN_ASN_NBR,";
			strInsert_trans = strInsert_trans + "MIXED_SCHEME_ACCT_NBR,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ";
			strInsert_trans = strInsert_trans + "VALUES(:stransno,:esno,:acctnbr,:userid,sysdate)";

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				log.info(" *** EsnAssignBillUpdate SQL *****" + strInsert_trans);

				paramMap.put("stransno", stransno);
				paramMap.put("esno", esno);
				paramMap.put("acctnbr", acctnbr);
				paramMap.put("userid", userid);
				log.info(" paramMap: " + paramMap);
				count_trans = namedParameterJdbcTemplate.update(strInsert_trans, paramMap);
				log.info("count_trans: " + count_trans);
			}

			if (count == 0) {
				log.info("Writing from TesnJpJp.EsnAssignBillUpdate");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003
				if (count_trans == 0) {
					log.info("Writing from TesnJpJp.EsnAssignBillUpdate");
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}
			}
			log.info("END: *** EsnAssignBillUpdate Result *****" + count);

		} catch (NullPointerException e) {
			log.info("Exception submitBilling : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception submitBilling : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception submitBilling : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: EsnAssignBillUpdate  DAO  END");
		}

	}

	private boolean checkAccountNbr(String accnbr) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		String sql = "";
		String straccnbrcount = "";
		try {
			log.info("START: checkAccountNbr  DAO  Start Obj " + " accnbr:" + CommonUtility.deNull(accnbr));

			sb.append("SELECT COUNT(*) FROM CUST_ACCT A, COMPANY_CODE B, ");
			sb.append("CUSTOMER C  WHERE A.BUSINESS_TYPE LIKE '%G%' AND ");
			sb.append("A.ACCT_NBR IS NOT NULL AND B.CO_CD=C.CUST_CD  AND A.ACCT_STATUS_CD='A' AND ");
			sb.append("UPPER(A.ACCT_NBR)=UPPER(:accnbr)");
			sql = sb.toString();

			log.info(" *** checkAccountNbr SQL *****" + sql);

			paramMap.put("accnbr", accnbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				straccnbrcount = CommonUtility.deNull(rs.getString(1));
			}
			if (((straccnbrcount).trim().equalsIgnoreCase("")) || straccnbrcount == null) {
				straccnbrcount = "0";
			}
			int intaccnbrcount = Integer.parseInt(straccnbrcount);

			log.info("END: *** checkAccountNbr Result *****" + intaccnbrcount);
			if (intaccnbrcount > 0) {
				return true;
			} else {
				return false;
			}

		} catch (NullPointerException e) {
			log.info("Exception checkAccountNbr : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkAccountNbr : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkAccountNbr  DAO  END");
		}

	}

	@Override
	public String getClsShipInd_bkr(String bkrNbr) throws BusinessException {
		String clsShpInd = "";
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		sql = "select gb_close_shp_ind from bk_details where bk_ref_nbr = :bkrNbr ";

		try {
			log.info("START: getClsShipInd_bkr  DAO  Start Obj " + " bkrNbr:" + CommonUtility.deNull(bkrNbr));

			log.info(" *** getClsShipInd_bkr SQL *****" + sql);

			paramMap.put("bkrNbr", bkrNbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				clsShpInd = CommonUtility.deNull(rs.getString("gb_close_shp_ind"));
			}
			log.info("END: *** getClsShipInd_bkr Result *****" + clsShpInd.toString());

		} catch (NullPointerException e) {
			log.info("Exception getClsShipInd_bkr : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getClsShipInd_bkr : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getClsShipInd_bkr  DAO  END");
		}
		return clsShpInd;

	}

	@Override
	public String getClsShipInd(String varNo) throws BusinessException {
		String varNbr = varNo;
		String clsShpInd = "";
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		sql = "select gb_close_shp_ind from vessel_Call where vv_cd = :varNbr ";

		try {
			log.info("START: getClsShipInd  DAO  Start Obj " + " varNo:" + CommonUtility.deNull(varNo));
			log.info(" *** getClsShipInd SQL *****" + sql);

			paramMap.put("varNbr", varNbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				clsShpInd = CommonUtility.deNull(rs.getString("gb_close_shp_ind"));
			}
			log.info("END: *** getClsShipInd Result *****" + clsShpInd.toString());

		} catch (NullPointerException e) {
			log.info("Exception getClsShipInd : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getClsShipInd : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getClsShipInd  DAO  END");
		}

		return clsShpInd;
	}

	@Override
	public String getSchemeInd(String out_voyno) throws BusinessException {
		String sql = "";
		String msch = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		sql = "SELECT MIXED_SCHEME_IND FROM VESSEL_CALL WHERE VV_CD=:out_voyno ";

		try {
			log.info("START: getSchemeInd  DAO  Start Obj " + " out_voyno:" + CommonUtility.deNull(out_voyno));

			log.info(" *** getSchemeInd	 SQL *****" + sql);

			paramMap.put("out_voyno", out_voyno);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				msch = rs.getString(1);
			}
			// log.info("sql sch ind "+sql);
			// log.info("msch "+msch);

			log.info("END: *** getSchemeInd Result *****" + msch.toString());
			return msch;

		} catch (NullPointerException e) {
			log.info("Exception getSchemeInd : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSchemeInd : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSchemeInd  DAO  END");
		}
	}

	@Override
	public String getBPacctnbr(String esno, String voy_nbr) throws BusinessException {
		String sql = "";
		String acctnbr = "";
		String scheme = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		sql = "SELECT MIXED_SCHEME_ACCT_NBR FROM tesn_jp_jp WHERE ESN_ASN_NBR=:esno ";

		try {
			log.info("START: getBPacctnbr  DAO  Start Obj " + " esno:" + CommonUtility.deNull(esno) + " voy_nbr:" + CommonUtility.deNull(voy_nbr));

			log.info(" *** getBPacctnbr SQL *****" + sql);

			paramMap.put("esno", esno);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				acctnbr = rs.getString(1);
			}
			if (acctnbr != null && !acctnbr.equals("") && !acctnbr.equals("null")) {
				log.info("acctnbr = " + acctnbr.toString());
			} else {
				scheme = getSchemeName(voy_nbr);
				if (scheme.equals("JLR")) {
					acctnbr = getVCactnbr(voy_nbr);
				}
				// add new scheme for LCT, 20.feb.11 by hpeng
				else if (!scheme.equals("JLR") && !scheme.equals("JNL") && !scheme.equals(ProcessChargeConst.LCT_SCHEME)
						&& !scheme.equals("JBT")) {
					acctnbr = getABactnbr(voy_nbr);
				}
			}

			log.info("END: *** getBPacctnbr Result *****" + acctnbr);
			return acctnbr;

		} catch (NullPointerException e) {
			log.info("Exception getBPacctnbr : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception getBPacctnbr : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getBPacctnbr : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBPacctnbr  DAO  END");
		}
	}

	@Override
	public String getABactnbr(String voy_nbr) throws BusinessException {
		String sql = "";
		String bactnbr = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		sql = "SELECT VS.ACCT_NBR FROM VESSEL_CALL VC,VESSEL_SCHEME VS WHERE VC.SCHEME=VS.SCHEME_CD AND VC.VV_CD =:voy_nbr ";

		try {
			log.info("START: getABactnbr  DAO  Start Obj " + " voy_nbr:" + CommonUtility.deNull(voy_nbr));

			log.info(" *** getABactnbr SQL *****" + sql);

			paramMap.put("voy_nbr", voy_nbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				bactnbr = rs.getString(1);
			}

			log.info("END: *** getABactnbr Result ***** " + CommonUtility.deNull(bactnbr));
			return bactnbr;

		} catch (NullPointerException e) {
			log.info("Exception getABactnbr : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getABactnbr : " , e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getABactnbr  DAO  END");
		}

	}

	@Override
	public String getVCactnbr(String voy_nbr) throws BusinessException {
		String sql = "";
		String bactnbr = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		sql = "SELECT BILL_ACCT_NBR FROM VESSEL_CALL WHERE VV_CD=:voy_nbr ";

		try {
			log.info("START: getVCactnbr  DAO  Start Obj " + " voy_nbr:" + CommonUtility.deNull(voy_nbr));

			log.info(" *** getVCactnbr SQL *****" + sql);

			paramMap.put("voy_nbr", voy_nbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				bactnbr = rs.getString(1);
			}

			log.info("END: *** getVCactnbr Result *****" + CommonUtility.deNull(bactnbr.toString()));
			return bactnbr;

		} catch (NullPointerException e) {
			log.info("Exception getVCactnbr : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVCactnbr : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVCactnbr  DAO  END");
		}

	}

	@Override
	public String getSchemeName(String voy_nbr) throws BusinessException {
		String sql = "";
		String sch = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		sql = "SELECT SCHEME FROM VESSEL_CALL WHERE VV_CD=:voy_nbr ";

		try {
			log.info("START: getSchemeName  DAO  Start Obj " + " voy_nbr: " + CommonUtility.deNull(voy_nbr));

			log.info(" *** getSchemeName SQL *****" + sql);

			paramMap.put("voy_nbr", voy_nbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				sch = rs.getString(1);
			}

			log.info("END: *** getSchemeName Result *****" + CommonUtility.deNull(sch.toString()));
			return sch;

		} catch (NullPointerException e) {
			log.info("Exception getSchemeName : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSchemeName : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSchemeName  DAO  END");
		}

	}

	@Override
	public List<TesnEsnListValueObject> getABacctnoForSA(String out_voyno) throws BusinessException {
		List<TesnEsnListValueObject> vacctno = new ArrayList<TesnEsnListValueObject>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuffer tempSql = new StringBuffer();

		try {
			log.info("START: getABacctnoForSA  DAO  Start Obj " + " out_voyno:" + CommonUtility.deNull(out_voyno));

			tempSql.append("SELECT VESSEL_SCHEME.SCHEME_CD, VESSEL_SCHEME.ACCT_NBR ");
			tempSql.append("FROM VESSEL_SCHEME , NOMINATED_SCHEME ");
			// tempSql.append("WHERE VESSEL_SCHEME.AB_CD IS NOT NULL ");
			// tempSql.append("AND VESSEL_SCHEME.scheme_cd = NOMINATED_SCHEME.scheme_cd ");
			tempSql.append("WHERE VESSEL_SCHEME.scheme_cd = NOMINATED_SCHEME.scheme_cd ");
			tempSql.append("AND NOMINATED_SCHEME.nominate_status = 'APP' ");
			tempSql.append("AND VESSEL_SCHEME.AB_CD IS NOT NULL ");
			tempSql.append("AND NOMINATED_SCHEME.vv_cd = :out_voyno ");
			String sql = tempSql.toString();

			log.info(" *** getABacctnoForSA SQL *****" + sql);

			paramMap.put("out_voyno", out_voyno);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			
			TesnEsnListValueObject esnListValueObject = null;
			while (rs.next()) {
				esnListValueObject = new TesnEsnListValueObject();
				esnListValueObject.setCc_cd("" + rs.getString(1));
				esnListValueObject.setCc_name("" + rs.getString(2));
				vacctno.add(esnListValueObject);
			}

			log.info("END: *** getABacctnoForSA Result *****" + vacctno.toString());
			return vacctno;

		} catch (NullPointerException e) {
			log.info("Exception getABacctnoForSA : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getABacctnoForSA : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getABacctnoForSA  DAO  END");
		}

	}

	@Override
	public List<TesnEsnListValueObject> getABacctno(String out_voyno) throws BusinessException {
		String sql = "";
		List<TesnEsnListValueObject> vacctno = new ArrayList<TesnEsnListValueObject>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		/*
		 * sql= "SELECT A.ACCT_NBR FROM CUST_ACCT A, VESSEL_CALL V ,VESSEL_SCHEME VS"
		 * +" WHERE VS.SCHEME_CD=V.SCHEME AND VS.AB_CD = A.CUST_CD AND A.BUSINESS_TYPE LIKE '%G%' AND "
		 * +" A.ACCT_NBR IS NOT NULL AND A.ACCT_STATUS_CD='A' AND A.TRIAL_IND='N' AND"
		 * +" V.OUT_VOY_NBR = '"+out_voyno+"' ORDER BY A.ACCT_NBR";
		 */
		sql = "SELECT SCHEME_CD,ACCT_NBR FROM VESSEL_SCHEME WHERE AB_CD IS NOT NULL";

		try {
			log.info("START: getABacctno  DAO  Start Obj " + " out_voyno:" + CommonUtility.deNull(out_voyno));

			log.info(" *** getABacctno SQL *****" + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			
			TesnEsnListValueObject esnListValueObject = null;
			while (rs.next()) {

				esnListValueObject = new TesnEsnListValueObject();
				esnListValueObject.setCc_cd("" + rs.getString(1));
				esnListValueObject.setCc_name("" + rs.getString(2));
				vacctno.add(esnListValueObject);
			}

			log.info("END: *** getABacctno Result *****" + vacctno.toString());
			return vacctno;

		} catch (NullPointerException e) {
			log.info("Exception getABacctno : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getABacctno : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getABacctno  DAO  END");
		}
	}

	@Override
	public List<String> getSAacctno(String vv_cd) throws BusinessException {
		String sql = "";
		List<String> vacctno = new ArrayList<String>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		/*
		 * sql= "SELECT A.ACCT_NBR FROM CUST_ACCT A, VESSEL_CALL V "
		 * +" WHERE A.BUSINESS_TYPE LIKE '%G%' AND"
		 * +" A.ACCT_NBR IS NOT NULL AND A.ACCT_STATUS_CD='A' AND A.TRIAL_IND='N' AND"
		 * +" V.CREATE_CUST_CD = A.CUST_CD AND V.OUT_VOY_NBR = '"+out_voyno+"'"
		 * +" ORDER BY A.ACCT_NBR";
		 */
		sql = "SELECT BILL_ACCT_NBR FROM VESSEL_CALL WHERE VV_CD =:vv_cd ";

		try {
			log.info("START: getSAacctno  DAO  Start Obj " + " vv_cd:" + CommonUtility.deNull(vv_cd));

			log.info(" *** getSAacctno SQL *****" + sql);

			paramMap.put("vv_cd", vv_cd);
			log.info(" paramMap: " + paramMap);
			rs =namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				vacctno.add(rs.getString(1));
			}

			log.info("END: *** getPargetSAacctnoaCodeInfo Result *****" + vacctno.toString());
			return vacctno;

		} catch (NullPointerException e) {
			log.info("Exception getSAacctno : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSAacctno : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSAacctno  DAO  END");
		}

	}

	@Override
	public TesnJpJpValueObject tesnjpjpView(String vesselvoyageno, String tesn_nbr, String edoAsn) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();

		TesnJpJpValueObject tesnjpjpobj = new TesnJpJpValueObject();

		// TVS added stuff ind in query 25-09-2003

		// log.info("view sql"+sql);
		try {
			log.info("START: tesnjpjpView  DAO  Start Obj " + " vesselvoyageno:" + CommonUtility.deNull(vesselvoyageno) + " tesn_nbr:"
					+ CommonUtility.deNull(tesn_nbr) + " edoAsn:" + CommonUtility.deNull(edoAsn));

			sb.append("select bk.bk_ref_nbr,te.esn_asn_nbr,ed.edo_asn_nbr,ed.mft_seq_nbr,");
			sb.append("mf.crg_type,ct.crg_type_nm,mf.pkg_type,pt.pkg_desc,hs_code,mf.crg_des,");
			sb.append("bk.port_dis,po.port_nm,bk.shipper_nm,decode(mf.dg_ind,'Y','Yes','N','No',null,'') as dg_ind,");
			sb.append("mf.cntr_type,mf.cntr_size,decode(te.ld_ind,'N','Normal','O','OverSide',null,'') as ld_ind,");
			sb.append(
					"e.out_voy_var_nbr,e.in_voy_var_nbr,mk.mft_markings,te.nbr_pkgs as te_nbr_pkgs,ed.trans_nbr_pkgs,");
			
			// Start #36864 : Update calculation for balance weight and volume - NS NOV 2023
			if(!edoAsn.isEmpty()) {
				sb.append("(ed.nom_wt - NVL((SELECT SUM(tesn.NOM_WT) FROM TESN_JP_JP tesn, esn esn WHERE tesn.edo_asn_nbr = ");
				sb.append(":edoAsn AND tesn.ESN_ASN_NBR = esn.esn_asn_nbr AND esn_status = 'A' GROUP BY tesn.edo_asn_nbr), 0) ");
				sb.append("- NVL((SELECT SUM(nom_wt) FROM dn_details WHERE edo_asn_nbr = :edoAsn AND dn_status = 'A' AND TESN_ASN_NBR IS NULL GROUP BY edo_asn_nbr), 0) ");
				sb.append("- NVL((SELECT SUM(psa.NOM_WT) FROM TESN_JP_PSA psa, esn esn WHERE psa.edo_asn_nbr = :edoAsn AND psa.ESN_ASN_NBR = esn.esn_asn_nbr AND esn_status = 'A' ");
				sb.append("GROUP BY psa.edo_asn_nbr), 0)) edo_nom_wt, ");
				sb.append("(ed.nom_vol - NVL((SELECT SUM(tesn.NOM_VOL) FROM TESN_JP_JP tesn, esn esn WHERE tesn.edo_asn_nbr = :edoAsn AND tesn.ESN_ASN_NBR = esn.esn_asn_nbr ");
				sb.append("AND esn.esn_status = 'A' GROUP BY tesn.edo_asn_nbr), 0) - NVL((SELECT SUM(nom_vol) FROM dn_details WHERE edo_asn_nbr = :edoAsn AND dn_status = 'A' ");
				sb.append("AND TESN_ASN_NBR IS NULL GROUP BY edo_asn_nbr), 0) - NVL((SELECT SUM(psa.NOM_VOL) FROM TESN_JP_PSA psa, esn esn WHERE psa.edo_asn_nbr = :edoAsn ");
				sb.append("AND psa.ESN_ASN_NBR = esn.esn_asn_nbr AND esn.esn_status = 'A' GROUP BY psa.edo_asn_nbr), 0)) edo_nom_vol, ");
			}
			sb.append("ed.nom_wt edo_nom_wt, ed.nom_vol edo_nom_vol, ");
			// End #36864 : Update calculation for balance weight and volume - NS NOV 2023
			sb.append(
					"te.payment_mode,te.acct_nbr,co_nm,ed.nbr_pkgs as edo_nbr_pkgs,te.nom_wt,te.nom_vol,bk_nbr_pkgs,bk_wt,bk_vol,variance_pkgs,variance_vol,variance_wt,ed.dn_nbr_pkgs as dn_nbr_pkgs,e.stuff_ind as stuffind, code.CC_NAME,code.CC_CD, ed.ca_cust_cd from tesn_jp_jp te,");
			sb.append("esn e ,bk_details bk, gb_edo ed,manifest_details mf,crg_type ct,pkg_types pt ,");
			sb.append("un_port_code po,mft_markings mk,company_code co,cust_acct cu ,CARGO_CATEGORY_CODE code ");
			sb.append("where te.esn_asn_nbr = e.esn_asn_nbr and te.edo_asn_nbr=ed.edo_asn_nbr ");
			sb.append("and e.bk_ref_nbr = bk.bk_ref_nbr and ed.mft_seq_nbr = mf.mft_seq_nbr ");
			sb.append("and e.CARGO_CATEGORY_CD = code.CC_CD ");
			sb.append("and mf.crg_type=ct.crg_type_cd and mf.pkg_type = pt.pkg_type_cd and ");
			sb.append("bk.port_dis = po.port_cd and mf.mft_seq_nbr = mk.mft_sq_nbr and ");
			sb.append(
					"lower(te.acct_nbr) = lower(cu.acct_nbr(+)) and cu.cust_cd  = co.co_cd(+) and te.esn_asn_nbr=:tesn_nbr ");
			String sql = sb.toString();

			log.info(" *** tesnjpjpView SQL *****" + sql);

			paramMap.put("tesn_nbr", tesn_nbr);
			paramMap.put("edoAsn", edoAsn);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			String vsl_nm_nbr = "";
			String out_vsl_nm_nbr = "";
			String cont_nbr = "";
			if (rs.next()) {
				tesnjpjpobj.setEsn_asn_nbr(CommonUtility.deNull(rs.getString("esn_asn_nbr")));
				tesnjpjpobj.setEdo_asn_nbr(CommonUtility.deNull(rs.getString("edo_asn_nbr")));
				tesnjpjpobj.setLd_ind(CommonUtility.deNull(rs.getString("ld_ind")));
				tesnjpjpobj.setNum_pkgs(CommonUtility.deNull(rs.getString("te_nbr_pkgs")));
				tesnjpjpobj.setPort_dis_cd(CommonUtility.deNull(rs.getString("port_dis")));
				tesnjpjpobj.setPort_dis_nm(CommonUtility.deNull(rs.getString("port_nm")));
				tesnjpjpobj.setCargo_type(CommonUtility.deNull(rs.getString("crg_type")));
				tesnjpjpobj.setCargo_type_nm(CommonUtility.deNull(rs.getString("crg_type_nm")));
				tesnjpjpobj.setPkg_type(CommonUtility.deNull(rs.getString("pkg_type")));
				tesnjpjpobj.setPkg_type_nm(CommonUtility.deNull(rs.getString("pkg_desc")));
				tesnjpjpobj.setDg_ind(CommonUtility.deNull(rs.getString("dg_ind")));
				tesnjpjpobj.setCrg_desc(CommonUtility.deNull(rs.getString("crg_des")));
				tesnjpjpobj.setBk_ref_nbr(CommonUtility.deNull(rs.getString("bk_ref_nbr")));
				tesnjpjpobj.setShipper_nm(CommonUtility.deNull(rs.getString("shipper_nm")));
				tesnjpjpobj.setCntr_type(CommonUtility.deNull(rs.getString("cntr_type")));
				tesnjpjpobj.setCntr_size(CommonUtility.deNull(rs.getString("cntr_size")));
				tesnjpjpobj.setOld_mark(CommonUtility.deNull(rs.getString("mft_markings")));
				tesnjpjpobj.setMft_seq_nbr(CommonUtility.deNull(rs.getString("mft_seq_nbr")));
				// START CR FTZ - NS JUNE 2024
				String hsCodeDisp = this.getHsCodeDisplay(tesnjpjpobj.getEdo_asn_nbr(), tesnjpjpobj.getMft_seq_nbr());
				tesnjpjpobj.setHs_code(CommonUtil.deNull(hsCodeDisp).isEmpty() ? rs.getString("hs_code") : hsCodeDisp);
				// START CR FTZ - NS JUNE 2024
				tesnjpjpobj.setTrns_nbr_pkgs(CommonUtility.deNull(rs.getString("trans_nbr_pkgs")));
				tesnjpjpobj.setEdo_nbr_pkgs(CommonUtility.deNull(rs.getString("edo_nbr_pkgs")));
				tesnjpjpobj.setPay_mode(CommonUtility.deNull(rs.getString("payment_mode")));
				tesnjpjpobj.setAcc_num(CommonUtility.deNull(rs.getString("acct_nbr")));
				tesnjpjpobj.setBill_party(CommonUtility.deNull(rs.getString("co_nm")));
				tesnjpjpobj.setOut_voy_var_nbr(CommonUtility.deNull(rs.getString("out_voy_var_nbr")));
				tesnjpjpobj.setIn_voy_var_nbr(CommonUtility.deNull(rs.getString("in_voy_var_nbr")));
				tesnjpjpobj.setNom_wt(CommonUtility.deNull(rs.getString("nom_wt")));
				tesnjpjpobj.setNom_vol(CommonUtility.deNull(rs.getString("nom_vol")));
				tesnjpjpobj.setBk_nbr_pkgs(CommonUtility.deNull(rs.getString("bk_nbr_pkgs")));
				tesnjpjpobj.setBk_wt(CommonUtility.deNull(rs.getString("bk_wt")));
				tesnjpjpobj.setBk_vol(CommonUtility.deNull(rs.getString("bk_vol")));
				tesnjpjpobj.setVariance_pkgs(CommonUtility.deNull(rs.getString("variance_pkgs")));
				tesnjpjpobj.setVariance_wt(CommonUtility.deNull(rs.getString("variance_wt")));
				tesnjpjpobj.setVariance_vol(CommonUtility.deNull(rs.getString("variance_vol")));
				tesnjpjpobj.setDn_nbr_pkgs(CommonUtility.deNull(rs.getString("dn_nbr_pkgs")));
				tesnjpjpobj.setStuffInd(CommonUtility.deNull(rs.getString("stuffind")));
				// add by Zhenguo Deng on 14/02/2011 for Cargo Category
				tesnjpjpobj.setCategory(CommonUtility.deNull(rs.getString("CC_CD")));
				tesnjpjpobj.setCategoryView(CommonUtility.deNull(rs.getString("CC_NAME")));
				// end add

				// Added by VietNguyen 16/04/2014 DPE
				tesnjpjpobj.setEdo_nom_wt(CommonUtility.deNull(rs.getString("edo_nom_wt")));
				tesnjpjpobj.setEdo_nom_vol(CommonUtility.deNull(rs.getString("edo_nom_vol")));
				tesnjpjpobj.setCaCustCd(CommonUtility.deNull(rs.getString("ca_cust_cd")));
				// Added by VietNguyen 16/04/2014 DPE

				vsl_nm_nbr = in_voy_nbr_nm(CommonUtility.deNull(rs.getString("in_voy_var_nbr")));
				StringTokenizer strToken = new StringTokenizer(vsl_nm_nbr, ",", false);
				String vsl_voy_nbr = "";
				String out_vsl_voy_nbr = "";
				String vsl_nm = "";
				String out_vsl_nm = "";
				while (strToken.hasMoreElements()) {
					vsl_voy_nbr = strToken.nextToken();
					vsl_nm = strToken.nextToken();
				}
				tesnjpjpobj.setIn_voy_nbr(vsl_voy_nbr);
				tesnjpjpobj.setIn_vsl_nm(vsl_nm);
				out_vsl_nm_nbr = out_voy_nbr_nm(CommonUtility.deNull(rs.getString("out_voy_var_nbr")));
				strToken = new StringTokenizer(out_vsl_nm_nbr, ",", false);
				while (strToken.hasMoreElements()) {
					out_vsl_voy_nbr = strToken.nextToken();
					out_vsl_nm = strToken.nextToken();
				}
				tesnjpjpobj.setOut_voy_nbr(out_vsl_voy_nbr);
				tesnjpjpobj.setOut_vsl_nm(out_vsl_nm);
				cont_nbr = cont_nbr(CommonUtility.deNull(rs.getString("mft_seq_nbr")));
				strToken = new StringTokenizer(cont_nbr, ",", false);
				if (strToken.hasMoreElements()) {
					tesnjpjpobj.setCont1(strToken.nextToken());
					tesnjpjpobj.setCont2(strToken.nextToken());
					tesnjpjpobj.setCont3(strToken.nextToken());
					tesnjpjpobj.setCont4(strToken.nextToken());
				} else {
					tesnjpjpobj.setCont1(" ");
					tesnjpjpobj.setCont2(" ");
					tesnjpjpobj.setCont3(" ");
					tesnjpjpobj.setCont4(" ");
				}

			}
			log.info("END: *** tesnjpjpView Result *****" + tesnjpjpobj.toString());

		} catch (NullPointerException e) {
			log.info("Exception tesnjpjpView : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception tesnjpjpView : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: tesnjpjpView  DAO  END");
		}
		return tesnjpjpobj;
	}

	private String in_voy_nbr_nm(String varno) throws BusinessException {
		String vsl_nm_nbr = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String sql = "";
		sql = "SELECT in_voy_nbr,vsl_nm FROM VESSEL_CALL WHERE VV_CD=:varno ";

		try {
			log.info("START: in_voy_nbr_nm  DAO  Start Obj " + " varno:" + CommonUtility.deNull(varno));

			log.info(" *** in_voy_nbr_nm SQL *****" + sql);

			paramMap.put("varno", varno);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				vsl_nm_nbr = CommonUtility.deNull(rs.getString("in_voy_nbr")) + ","
						+ CommonUtility.deNull(rs.getString("vsl_nm"));
			} else {
				vsl_nm_nbr = "";
			}
			log.info("END: *** in_voy_nbr_nm Result *****" + vsl_nm_nbr.toString());

		} catch (NullPointerException e) {
			log.info("Exception in_voy_nbr_nm : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception in_voy_nbr_nm : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: in_voy_nbr_nm  DAO  END");
		}

		return vsl_nm_nbr;
	}

	private String out_voy_nbr_nm(String varno) throws BusinessException {
		String out_vsl_nm_nbr = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String sql = "";
		sql = "SELECT out_voy_nbr,vsl_nm FROM VESSEL_CALL WHERE VV_CD=:varno ";

		try {
			log.info("START: out_voy_nbr_nm  DAO  Start Obj " + " varno:" + varno);

			log.info(" *** out_voy_nbr_nm SQL *****" + sql);

			paramMap.put("varno", varno);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				out_vsl_nm_nbr = CommonUtility.deNull(rs.getString("out_voy_nbr")) + ","
						+ CommonUtility.deNull(rs.getString("vsl_nm"));
			} else {
				out_vsl_nm_nbr = "";
			}
			log.info("END: *** out_voy_nbr_nm Result *****" + out_vsl_nm_nbr.toString());

		} catch (NullPointerException e) {
			log.info("Exception out_voy_nbr_nm : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception out_voy_nbr_nm : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: out_voy_nbr_nm  DAO  END");
		}
		return out_vsl_nm_nbr;
	} // end of method

	/**
	 * This method retrieves Container Numbers.
	 *
	 * @param String represents Manifest Sequence Number.
	 * @return String represents Container Number.
	 * @exception BusinessException if failed.
	 **/

	private String cont_nbr(String mft_seq_num) throws BusinessException {
		String cont_nbr = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String sql = "";
		String cnt1 = " ", cnt2 = " ", cnt3 = " ", cnt4 = " ";
		sql = "SELECT cntr_bl_seq,cntr_nbr FROM bl_cntr_details WHERE mft_seq_nbr=:mft_seq_num order by cntr_bl_seq asc ";

		try {
			log.info("START: cont_nbr  DAO  Start Obj " + " mft_seq_num:" + CommonUtility.deNull(mft_seq_num));

			log.info(" *** cont_nbr SQL *****" + sql);

			paramMap.put("mft_seq_num", mft_seq_num);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				if (CommonUtility.deNull(rs.getString("cntr_bl_seq")).equals("1"))
					cnt1 = CommonUtility.deNull(rs.getString("cntr_nbr"));
				if (CommonUtility.deNull(rs.getString("cntr_bl_seq")).equals("2"))
					cnt2 = CommonUtility.deNull(rs.getString("cntr_nbr"));
				if (CommonUtility.deNull(rs.getString("cntr_bl_seq")).equals("3"))
					cnt3 = CommonUtility.deNull(rs.getString("cntr_nbr"));
				if (CommonUtility.deNull(rs.getString("cntr_bl_seq")).equals("4"))
					cnt4 = CommonUtility.deNull(rs.getString("cntr_nbr"));
			}
			cont_nbr = cnt1 + "," + cnt2 + "," + cnt3 + "," + cnt4;

			log.info("END: *** cont_nbr Result *****" + cont_nbr.toString());

		} catch (NullPointerException e) {
			log.info("Exception cont_nbr : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception cont_nbr : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: cont_nbr  DAO  END");
		}

		return cont_nbr;
	}

	@Override
	public int tesnjpjpValidCheck_DN(String tesn_nbr, String out_voy_var_nbr) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		int flg = 0;
		String trans_dn_nbr_pkgs = "";
		// String sql = "select gb_edo.trans_dn_nbr_pkgs from tesn_jp_jp,gb_edo where
		// tesn_jp_jp.edo_asn_nbr = gb_edo.edo_asn_nbr and esn_asn_nbr ='"+tesn_nbr+"'";
		// changes done by thiru on 21/05/2003
		String sql = "select DN_NBR_PKGS from tesn_jp_jp where ESN_ASN_NBR =:tesn_nbr ";
		try {
			log.info("START: tesnjpjpValidCheck_DN  DAO  Start Obj " + " tesn_nbr:" + CommonUtility.deNull(tesn_nbr) + " out_voy_var_nbr:"
					+ CommonUtility.deNull(out_voy_var_nbr));

			log.info(" *** tesnjpjpValidCheck_DN SQL *****" + sql);

			paramMap.put("tesn_nbr", tesn_nbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			
			if (rs.next())
				trans_dn_nbr_pkgs = CommonUtility.deNull(rs.getString("DN_NBR_PKGS"));
			if (trans_dn_nbr_pkgs.equals("") || trans_dn_nbr_pkgs.equals("0"))
				flg = 1;
			else
				flg = 0;

			log.info("END: *** tesnjpjpValidCheck_DN Result *****" + flg);

		} catch (NullPointerException e) {
			log.info("Exception tesnjpjpValidCheck_DN : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception tesnjpjpValidCheck_DN : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: tesnjpjpValidCheck_DN  DAO  END");
		}

		return flg;

	}

	@Override
	public int tesnjpjpValidCheck_status(String tesn_nbr, String out_voy_var_nbr) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		int flg = 0;
		String gb_close_shp_ind = "";
		String sql = "select gb_close_shp_ind from vessel_call where vv_cd=:out_voy_var_nbr ";
		try {
			log.info("START: tesnjpjpValidCheck_status  DAO  Start Obj " + " tesn_nbr:" + CommonUtility.deNull(tesn_nbr) + " out_voy_var_nbr:"
					+ CommonUtility.deNull(out_voy_var_nbr));

			log.info(" *** tesnjpjpValidCheck_status SQL *****" + sql);

			paramMap.put("out_voy_var_nbr", out_voy_var_nbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			
			if (rs.next())
				gb_close_shp_ind = CommonUtility.deNull(rs.getString("gb_close_shp_ind"));
			if (gb_close_shp_ind.equals("N"))
				flg = 1;
			else
				flg = 0;
			log.info("END: *** tesnjpjpValidCheck_status Result *****" + flg);

		} catch (NullPointerException e) {
			log.info("Exception tesnjpjpValidCheck_status : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception tesnjpjpValidCheck_status : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: tesnjpjpValidCheck_status  DAO  END");
		}

		return flg;

	}

	@Override
	public int tesnjpjpValidCheck(String tesn_nbr, String out_voy_var_nbr) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		int flg = 0;
		String ua_nbr_pkgs = "";
		String sql = "select ua_nbr_pkgs from tesn_jp_jp where esn_asn_nbr=:tesn_nbr ";
		try {
			log.info("START: tesnjpjpValidCheck  DAO  Start Obj " + " tesn_nbr:" + CommonUtility.deNull(tesn_nbr) + " out_voy_var_nbr:"
					+ CommonUtility.deNull(out_voy_var_nbr));

			log.info(" *** tesnjpjpValidCheck SQL *****" + sql);

			paramMap.put("tesn_nbr", tesn_nbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			
			if (rs.next())
				ua_nbr_pkgs = CommonUtility.deNull(rs.getString("ua_nbr_pkgs"));
			if (ua_nbr_pkgs.equals("0"))
				flg = 1;
			else
				flg = 0;

			log.info("END: *** tesnjpjpValidCheck Result *****" + flg);

		} catch (NullPointerException e) {
			log.info("Exception tesnjpjpValidCheck : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception tesnjpjpValidCheck : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: tesnjpjpValidCheck  DAO  END");
		}

		return flg;

	}

	@Override
	public TableResult getTesnJpJpList(String vvcode, Criteria criteria) throws BusinessException {
		SqlRowSet rs = null;
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		TableResult tableResult = new TableResult();
		Map<String, String> paramMap = new HashMap<>();

		StringBuffer sb = new StringBuffer();
		String sql = "";
		String esn_asn_nbr = "";
		String in_vsl_nm = "";
		String crg_desc = "";
		String num_pkgs = "";
		String in_voy_nbr = "";

		// Amended by VietNguyen 20/03/2014: get more scheme field

		try {
			log.info("START: getTesnJpJpList  DAO  Start Obj " + " vvcode:" + CommonUtility.deNull(vvcode));

			sb.append(
					"select edo.edo_asn_nbr, ves.terminal, ves.COMBI_GC_OPS_IND,nvl(vsh.scheme_cd, ves.COMBI_GC_SCHEME) subScheme, nvl(vsh.scheme_cd, ves.scheme) scheme_cd,e.esn_asn_nbr,ves.vsl_nm,ves.out_voy_nbr,te.nbr_pkgs,mft.crg_des,e.stuff_ind as stuffind, decode (edo.CRG_STATUS, 'T', 'Transhipment','L','Local','R','Re-export') crg_status, ");
			sb.append("vess.terminal inTerminal, ");
			sb.append(
					"vess.COMBI_GC_OPS_IND in_COMBI_GC_OPS_IND,nvl(vsh.scheme_cd, vess.COMBI_GC_SCHEME) inSubScheme, nvl(vsh.scheme_cd, vess.scheme) inScheme, ");
			sb.append(
					"vess.vsl_nm vsl_nm1,vess.in_voy_nbr, to_char(e.last_modify_dttm, 'DD/MM/YYYY HH:SS') last_modify_dttm,  ad.user_name  last_modify_user_id  ");
			// START - Added nom weight and vol to display in listing table - NS MAY 2023
			sb.append(", te.nom_wt, te.nom_vol ");
			// END - Added nom weight and vol to display in listing table - NS MAY 2023
			sb.append("from esn e,tesn_jp_jp te,manifest_details mft,gb_edo edo,");
			sb.append("vessel_call ves,vessel_scheme vsh, vessel_call vess, ");
			sb.append(
					" adm_user ad, (select esn_asn_nbr, min(last_modify_user_id) last_modify_user_id,trans_nbr from TESN_JP_JP_TRANS group by esn_asn_nbr,trans_nbr having trans_nbr=0 )  tmp ");
			sb.append(
					"where e.esn_asn_nbr = te.esn_asn_nbr and e.esn_Status = 'A' and e.trans_type='A' and Edo.edo_asn_nbr=te.edo_asn_nbr and Edo.mft_seq_nbr = mft.mft_seq_nbr ");
			sb.append(
					"and e.out_voy_var_nbr = ves.vv_cd and   ad.user_acct(+) = tmp.last_modify_user_id  and tmp.esn_asn_nbr (+) = e.esn_asn_nbr and");
			sb.append(" e.in_voy_var_nbr = vess.vv_cd and ");
			sb.append("te.mixed_scheme_acct_nbr = vsh.acct_nbr(+) and vsh.rec_status(+) = 'A' ");
			sb.append("and e.out_voy_var_nbr=:vvcode order by ves.terminal desc,ves.vsl_nm,ves.out_voy_nbr ");

			sql = sb.toString();
			paramMap.put("vvcode", vvcode);

			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						paramMap, Integer.class));
			}

			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());

			}
			
			log.info(" *** getTesnJpJpList SQL *****" + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			
			while (rs.next()) {
				esn_asn_nbr = CommonUtility.deNull(rs.getString("esn_asn_nbr"));
				in_vsl_nm = CommonUtility.deNull(rs.getString("vsl_nm"));
				crg_desc = CommonUtility.deNull(rs.getString("crg_des"));
				num_pkgs = CommonUtility.deNull(rs.getString("nbr_pkgs"));
				in_voy_nbr = CommonUtility.deNull(rs.getString("out_voy_nbr"));
				TesnJpJpValueObject tesnObj = new TesnJpJpValueObject();
				tesnObj.setEsn_asn_nbr(esn_asn_nbr);
				tesnObj.setCrg_desc(crg_desc);
				tesnObj.setNum_pkgs(num_pkgs);
				tesnObj.setIn_vsl_nm(in_vsl_nm);
				tesnObj.setIn_voy_nbr(in_voy_nbr);
				tesnObj.setStuffInd(CommonUtility.deNull(rs.getString("stuffind")));
				tesnObj.setScheme(CommonUtility.deNull(rs.getString("scheme_cd")));
				tesnObj.setTerminal(CommonUtility.deNull(rs.getString("terminal")));
				tesnObj.setSubScheme(CommonUtility.deNull(rs.getString("subScheme")));
				tesnObj.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				tesnObj.setInScheme(CommonUtility.deNull(rs.getString("inScheme")));
				tesnObj.setInTerminal(CommonUtility.deNull(rs.getString("inTerminal")));
				tesnObj.setInSubScheme(CommonUtility.deNull(rs.getString("inSubScheme")));
				tesnObj.setIngcOperations(CommonUtility.deNull(rs.getString("in_COMBI_GC_OPS_IND")));

				tesnObj.setEdo_asn_nbr(CommonUtility.deNull(rs.getString("edo_asn_nbr")));
				tesnObj.setCrg_status(CommonUtility.deNull(rs.getString("crg_status")));
				tesnObj.setOut_vsl_nm(CommonUtility.deNull(rs.getString("vsl_nm1")));
				tesnObj.setOut_voy_nbr(CommonUtility.deNull(rs.getString("in_voy_nbr")));
				tesnObj.setCreate_user(CommonUtility.deNull(rs.getString("last_modify_user_id")));
				tesnObj.setModify_dttm(CommonUtility.deNull(rs.getString("last_modify_dttm")));

				// START - Added nom weight and vol to display in listing table - NS MAY 2023
				tesnObj.setNom_wt(CommonUtility.deNull(rs.getString("nom_wt")));
				tesnObj.setNom_vol(CommonUtility.deNull(rs.getString("nom_vol")));
				// END - Added nom weight and vol to display in listing table - NS MAY 2023
				topsModel.put(tesnObj);
			}

			log.info("END: *** getTesnJpJpList Result *****" + topsModel.toString());

		} catch (NullPointerException e) {
			log.info("Exception getTesnJpJpList : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTesnJpJpList : " , e);
			throw new BusinessException("M4201");
		} finally {
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			log.info("END: getTesnJpJpList  DAO  END");
		}

		return tableResult;
	}

	@Override
	public TesnVesselVoyValueObject getVessel(String vesselName, String outvoyNbr, String coCd)
			throws BusinessException {

		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();

		TesnVesselVoyValueObject vvvObj = null;

		try {
			log.info("START: getVessel  DAO  Start Obj " + " vesselName:" + CommonUtility.deNull(vesselName) + " outvoyNbr:" + CommonUtility.deNull(outvoyNbr)
					+ " coCd:" + CommonUtility.deNull(coCd));

			if (coCd.length() != 0 && coCd.equals("JP")) {
				sb.append("select distinct ves.vsl_nm, ves.out_voy_nbr, e.out_voy_var_nbr,ves.terminal ");
				sb.append("from esn e, vessel_call ves where");
				sb.append(" ves.vsl_nm = :vesselName");
				sb.append(" AND ves.out_voy_nbr = :outvoyNbr ");
				sb.append(" AND e.trans_type='A'  and e.out_voy_var_nbr = ves.vv_cd ");
				sb.append("and (ves.vv_status_ind not in ('CX') or (ves.vv_status_ind='CL' AND ves.TERMINAL = 'CT'))");
				sb.append(
						" and ((ves.TERMINAL IN 'CT' AND ves.COMBI_GC_OPS_IND IN('Y',null)) OR ves.TERMINAL NOT IN 'CT') ");
				sb.append(" order by ves.terminal desc,ves.vsl_nm,ves.out_voy_nbr");
				sql = sb.toString();
			} else {
				sb.append("select distinct ves.vsl_nm, ves.out_voy_nbr, e.out_voy_var_nbr,ves.terminal ");
				sb.append("from esn e, vessel_call ves where");
				sb.append(" ves.vsl_nm = :vesselName");
				sb.append(" AND ves.out_voy_nbr = :outvoyNbr ");
				sb.append(" AND e.trans_type='A'  and e.out_voy_var_nbr = ves.vv_cd ");
				sb.append(" and ( ESN_CREATE_CD=:coCd OR ves.CREATE_CUST_CD = :coCd)");
				sb.append(" and ves.vv_status_ind not in ('CX') order by ves.terminal desc,ves.vsl_nm,ves.out_voy_nbr");

				sql = sb.toString();
			}

			log.info(" *** getVessel SQL *****" + sql);

			if ((coCd.length() != 0 && coCd.equals("JP"))) {
				paramMap.put("vesselName", vesselName);
				paramMap.put("outvoyNbr", outvoyNbr);
			} else {
				paramMap.put("vesselName", vesselName);
				paramMap.put("outvoyNbr", outvoyNbr);
				paramMap.put("coCd", coCd);
			}
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			String voy_nbr = "";
			String vsl_nm = "";
			String vv_voy = "";
			String terminal = "";

			if (rs.next()) {
				voy_nbr = CommonUtility.deNull(rs.getString("out_VOY_var_NBR"));
				vsl_nm = CommonUtility.deNull(rs.getString("VSL_NM"));
				vv_voy = CommonUtility.deNull(rs.getString("out_VOY_NBR"));
				terminal = CommonUtility.deNull(rs.getString("TERMINAL"));

				vvvObj = new TesnVesselVoyValueObject();
				vvvObj.setVoyNo(voy_nbr);
				vvvObj.setVslName(vsl_nm);
				vvvObj.setVvVoy(vv_voy);
				vvvObj.setTerminal(terminal);
			}
			log.info("END: *** getVessel Result *****" + vvvObj.toString());

		} catch (NullPointerException e) {
			log.info("Exception getVessel : " , e);
		} catch (Exception e) {
			log.info("Exception getVessel : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVessel  DAO  END");
		}

		return vvvObj;
	}

	@Override
	public List<TesnVesselVoyValueObject> getVslList(String coCd) throws BusinessException {

		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		List<TesnVesselVoyValueObject> vslList = new ArrayList<TesnVesselVoyValueObject>();

		try {
			log.info("START: getVslList  DAO  Start Obj " + " coCd:" + CommonUtility.deNull(coCd));

			if (coCd.length() != 0 && coCd.equals("JP"))
				sql = "select distinct ves.vsl_nm,ves.out_voy_nbr,e.out_voy_var_nbr,ves.terminal from esn e, vessel_call ves where e.trans_type='A' and e.esn_status = 'A' and e.out_voy_var_nbr = ves.vv_cd and ves.gb_close_shp_ind = 'N' and (ves.vv_status_ind not in ('CX','CL') or (ves.vv_status_ind='CL' AND ves.TERMINAL = 'CT')) AND ((ves.TERMINAL IN 'CT' AND ves.COMBI_GC_OPS_IND IN('Y',null)) OR ves.TERMINAL NOT IN 'CT') order by ves.terminal desc,ves.vsl_nm,ves.out_voy_nbr";
			else {
				sb.append("select distinct ves.vsl_nm,ves.out_voy_nbr,e.out_voy_var_nbr,ves.terminal ");
				sb.append("from esn e, vessel_call ves where e.trans_type='A' and e.esn_status = 'A' ");
				sb.append("and e.out_voy_var_nbr = ves.vv_cd and ves.gb_close_shp_ind = 'N' ");
				sb.append(" and (ESN_CREATE_CD=:coCd OR ves.CREATE_CUST_CD = :coCd) ");
				sb.append(
						"and ves.vv_status_ind not in ('CX','CL','UB') order by ves.terminal desc,ves.vsl_nm,ves.out_voy_nbr");
				sql = sb.toString();
			}

			log.info(" *** getVslList SQL *****" + sql);

			if (!(coCd.length() != 0 && coCd.equals("JP"))) {
				paramMap.put("coCd", coCd);
			}
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			String voy_nbr = "";
			String vsl_nm = "";
			String vv_voy = "";
			String terminal = "";

			while (rs.next()) {
				voy_nbr = CommonUtility.deNull(rs.getString("out_VOY_var_NBR"));
				vsl_nm = CommonUtility.deNull(rs.getString("VSL_NM"));
				vv_voy = CommonUtility.deNull(rs.getString("out_VOY_NBR"));
				terminal = CommonUtility.deNull(rs.getString("TERMINAL"));

				TesnVesselVoyValueObject vvvObj = new TesnVesselVoyValueObject();
				vvvObj.setVoyNo(voy_nbr);
				vvvObj.setVslName(vsl_nm);
				vvvObj.setVvVoy(vv_voy);
				vvvObj.setTerminal(terminal);
				vslList.add(vvvObj);
			}
			log.info("END: *** getVslList Result *****" + vslList.toString());

		} catch (NullPointerException e) {
			log.info("Exception getVslList : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVslList : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVslList  DAO  END");
		}

		return vslList;

	}

	// Added to check edo package for tesn
	@Override
	public void checkEdoPackage(String edo_asn_nbr) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			log.info("START: checkEdoPackage  DAO  Start Obj " + " edo_asn_nbr:" + CommonUtility.deNull(edo_asn_nbr));

			sb.append("SELECT ed.trans_nbr_pkgs,");
			sb.append("ed.nbr_pkgs-nvl(ed.CUT_OFF_NBR_PKGS, 0) AS edo_nbr_pkgs,");
			sb.append("ed.dn_nbr_pkgs ");
			sb.append("FROM gb_edo ed WHERE ");
			sb.append("ed.edo_asn_nbr =:edo_asn_nbr");

			String sql = sb.toString();

			log.info(" *** tesnjpjpAddView SQL *****" + sql);

			paramMap.put("edo_asn_nbr", edo_asn_nbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				String trans_nbr_pkgs = CommonUtility.deNull(rs.getString("trans_nbr_pkgs"));
				String edo_nbr_pkgs = CommonUtility.deNull(rs.getString("edo_nbr_pkgs"));
				String dn_nbr_pkgs = CommonUtility.deNull(rs.getString("dn_nbr_pkgs"));
				
				// follow method tesnjpjpAddView to check balance
				if ((Integer.parseInt(edo_nbr_pkgs) - Integer.parseInt(dn_nbr_pkgs)
						- Integer.parseInt(trans_nbr_pkgs)) < 1) {
					log.info("Balance pkgs for ASN is not enough for the TESN = " + (Integer.parseInt(edo_nbr_pkgs) - Integer.parseInt(dn_nbr_pkgs)
							- Integer.parseInt(trans_nbr_pkgs)));
					throw new BusinessException("Balance pkgs for ASN is not enough for the TESN");
				}

			}

		} catch (NullPointerException e) {
			log.info("Exception checkEdoPackage : ", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception checkEdoPackage : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception checkEdoPackage : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkEdoPackage  DAO  END");
		}
	}
	
	// START CR FTZ - NS JUNE 2024
	private String getHsCodeDisplay(String asnNo, String mftSeqNbr) throws BusinessException {
		String hsDisp = "";
		SqlRowSet rs4 = null;
		StringBuffer sb1 = new StringBuffer();
		List<String> slist = new ArrayList<>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getHsCodeDisplay  DAO  Start asnNo" + CommonUtility.deNull(asnNo));

			sb1.append("SELECT HS_CODE, HS_SUB_CODE_FR, HS_SUB_CODE_TO, CUSTOM_HS_CODE ");
			sb1.append("FROM GBMS.GB_EDO_HSCODE_DETAILS ");
			sb1.append("WHERE edo_asn_nbr =:asnNo AND MFT_SEQ_NBR = :mftSeqNbr");
			String sql4 = sb1.toString();
			paramMap.put("asnNo", asnNo);
			paramMap.put("mftSeqNbr", mftSeqNbr);

			log.info(" getCargoDetails  DAO  SQL " + sql4);
			log.info("param:" + paramMap);
			rs4 = namedParameterJdbcTemplate.queryForRowSet(sql4, paramMap);
			String hsCode = "", hsSubFr = "", hsSubTo = "", customHs = "";
			while (rs4.next()) {
				hsCode = rs4.getString("HS_CODE");
				hsSubFr = rs4.getString("HS_SUB_CODE_FR");
				hsSubTo = rs4.getString("HS_SUB_CODE_TO");
				customHs = CommonUtil.deNull(rs4.getString("CUSTOM_HS_CODE"));
				slist.add(hsCode + " (" + hsSubFr + (hsSubFr.equalsIgnoreCase(hsSubTo) ? "" : "-" + hsSubTo) + ") " + (customHs.isEmpty() ? "" : "~" + customHs ));
			}
			if(slist.size() == 0) {
				sb1.setLength(0);
				sb1.append("SELECT HS_CODE, HS_SUB_CODE_FR, HS_SUB_CODE_TO, CUSTOM_HS_CODE FROM GB_EDO EDO,MANIFEST_DETAILS MFT WHERE EDO_ASN_NBR = :asnNo AND EDO.MFT_SEQ_NBR = MFT.MFT_SEQ_NBR ");
				paramMap.put("asnNo", asnNo);

				log.info(" getHSEdoDetails  DAO  SQL " + sb1.toString());
				log.info("param:" + paramMap);
				rs4 = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
				while (rs4.next()) {
					hsCode = rs4.getString("HS_CODE");
					hsSubFr = rs4.getString("HS_SUB_CODE_FR");
					hsSubTo = rs4.getString("HS_SUB_CODE_TO");
					customHs = CommonUtil.deNull(rs4.getString("CUSTOM_HS_CODE"));
					slist.add(hsCode + " (" + hsSubFr + (hsSubFr.equalsIgnoreCase(hsSubTo) ? "" : "-" + hsSubTo) +  ") " + (customHs.isEmpty() ? "" : "~" + customHs ));
				}
			}
			hsDisp = String.join(",", slist);
		} catch (Exception e) {
			log.info("Exception getHsCodeDisplay : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getHsCodeDisplay  DAO  END hsDisp:" + hsDisp);
		}
		return hsDisp;
	}
	// END CR FTZ - NS JUNE 2024
}
