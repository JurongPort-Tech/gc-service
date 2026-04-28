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

import sg.com.jp.generalcargo.dao.UARepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ReportValueObject;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.UACntrJasperReport;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.domain.UaEsnListValueObject;
import sg.com.jp.generalcargo.domain.UaListObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.GbmsCommonUtility;

@Repository("UARepository")
public class UAJdbcRepository implements UARepository {

	private static final Log log = LogFactory.getLog(UAJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	String logStatusGlobal = "Y";
	private boolean DEBUG = false;
	private boolean INFO = false;

	@Value("${ReportPrintingBean.directory.pdf}")
	private String printingBeanPdf;


	@Override
	public boolean chkVslStat(String esnasnnbr) throws BusinessException {
		String sqlvoy = "";
		SqlRowSet rs = null;
		boolean bvslind = false;
		String varno = "";
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: chkVslStat  DAO  Start Obj " + " esnasnnbr:" + CommonUtility.deNull(esnasnnbr));
			sqlvoy = "SELECT OUT_VOY_VAR_NBR FROM ESN WHERE ESN_ASN_NBR =:esnasnnbr";
			log.info(" *** chkVslStat SQL 1 *****" + sqlvoy);
			paramMap.put("esnasnnbr", esnasnnbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlvoy, paramMap);

			if (rs.next()) {
				varno = rs.getString("OUT_VOY_VAR_NBR");
			}
			log.info("*** chkVslStat Result *****" + CommonUtility.deNull(varno));
			// this sql query is not used in the jpol -- need to verify  

//			sql = "SELECT GB_CLOSE_SHP_IND FROM VESSEL_CALL WHERE GB_CLOSE_SHP_IND='Y' AND VV_CD=:varno ";
//			paramMap.put("varno", varno);
//			log.info(" *** chkVslStat SQL 2 *****" + sql);
//			log.info(" paramMap: " + paramMap);
//			
//			 rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				bvslind = true;
			} else {
				bvslind = false;
			}

			log.info("END: *** chkVslStat Result *****" + bvslind);
		} catch (NullPointerException e) {
			log.info("Exception chkVslStat : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkVslStat : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkVslStat  DAO  END");
		}
		return bvslind;
	}

	@Override
	public void cancelUA(String uanbr, String esnasnnbr, String transtype, String userid, String UA_Nbr_Pkgs)
			throws BusinessException {

		String sql = "";
		String esnupdsql = "";
		int count = 0;
		int countua = 0;
		SqlRowSet rs = null;
		String sql1_trans = "";
		String sql2_trans = "";
		String sql3_trans = "";
		String sql4_trans = "";

		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String sql4 = "";

		String sqltlog = "";
		String strInsert_trans = "";
		String sqlua = "";
		String sqltlog1 = "";
		String strInsert_trans1 = "";
		String sqlft = "";
		int stransno = 0;
		int count_trans = 0;
		int cntua = 0;
		int stransno1 = 0;
		int count_trans1 = 0;

		StringBuffer sb = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {
			log.info("START: cancelUA  DAO  Start Obj " + " uanbr:" + CommonUtility.deNull(uanbr) + " esnasnnbr:" + CommonUtility.deNull(esnasnnbr) + " transtype:"
					+ CommonUtility.deNull(transtype) + " userid:" + CommonUtility.deNull(userid) + " UA_Nbr_Pkgs:" + CommonUtility.deNull(UA_Nbr_Pkgs));

			sql = "update ua_details set ua_status='X',LAST_MODIFY_USER_ID= :userid, LAST_MODIFY_DTTM=sysdate where ua_nbr=:uanbr ";

			sql1_trans = "SELECT MAX(TRANS_NBR) maxTransNbr FROM ESN_DETAILS_TRANS WHERE ESN_ASN_NBR=:esnasnnbr ";
			sql2_trans = "SELECT MAX(TRANS_NBR) maxTransNbr FROM TESN_JP_JP_TRANS WHERE ESN_ASN_NBR=:esnasnnbr ";
			sql3_trans = "SELECT MAX(TRANS_NBR) maxTransNbr FROM TESN_PSA_JP_TRANS WHERE ESN_ASN_NBR=:esnasnnbr ";
			sql4_trans = "SELECT MAX(TRANS_NBR) maxTransNbr FROM SS_DETAILS_TRANS WHERE ESN_ASN_NBR=:esnasnnbr ";

			sqlua = "select count(*) cnt from ua_details where ua_status = 'A' and esn_asn_nbr =:esnasnnbr ";

			sql1 = "update esn_details set FIRST_TRANS_DTTM = '' where esn_asn_nbr=:esnasnnbr ";
			sql2 = "update tesn_jp_jp set FIRST_TRANS_DTTM = '' where esn_asn_nbr=:esnasnnbr ";
			sql3 = "update tesn_psa_jp set FIRST_TRANS_DTTM = '' where esn_asn_nbr=:esnasnnbr ";
			sql4 = "update ss_details set FIRST_TRANS_DTTM = '' where esn_asn_nbr=:esnasnnbr ";

			if (transtype != null && !transtype.equals("") && transtype.equals("E")) {
				esnupdsql = "update esn_details set ua_nbr_pkgs =ua_nbr_pkgs- :UA_Nbr_Pkgs where esn_asn_nbr =:esnasnnbr ";
				sqltlog1 = sql1_trans;
				sqlft = sql1;
				strInsert_trans1 = "INSERT INTO ESN_DETAILS_TRANS ";
			} else if (transtype != null && !transtype.equals("") && transtype.equals("A")) {
				esnupdsql = "update tesn_jp_jp set ua_nbr_pkgs=ua_nbr_pkgs- :UA_Nbr_Pkgs where esn_asn_nbr =:esnasnnbr ";
				sqltlog1 = sql2_trans;
				sqlft = sql2;
				strInsert_trans1 = "INSERT INTO TESN_JP_JP_TRANS ";
			} else if (transtype != null && !transtype.equals("") && transtype.equals("C")) {
				esnupdsql = "update tesn_psa_jp set ua_nbr_pkgs=ua_nbr_pkgs- :UA_Nbr_Pkgs where esn_asn_nbr =:esnasnnbr ";
				sqltlog1 = sql3_trans;
				sqlft = sql3;
				strInsert_trans1 = "INSERT INTO TESN_PSA_JP_TRANS ";
			} else if (transtype != null && !transtype.equals("") && transtype.equals("S")) {
				esnupdsql = "update ss_details set ua_nbr_pkgs=ua_nbr_pkgs- :UA_Nbr_Pkgs where esn_asn_nbr =:esnasnnbr ";
				sqltlog1 = sql4_trans;
				sqlft = sql4;
				strInsert_trans1 = "INSERT INTO SS_DETAILS_TRANS ";
			}

			log.info(" *** cancelUA SQL 1 *****" + sql);

			paramMap.put("uanbr", uanbr);
			paramMap.put("userid", userid);
			log.info(" paramMap: " + paramMap);
			countua = namedParameterJdbcTemplate.update(sql, paramMap);
			log.info("cancelUA countua: " + countua);
			
			paramMap.put("esnasnnbr", esnasnnbr);
			paramMap.put("UA_Nbr_Pkgs", Integer.parseInt(UA_Nbr_Pkgs));
			log.info(" *** cancelUA SQL 2 *****" + esnupdsql);
			log.info(" paramMap: " + paramMap);
			count = namedParameterJdbcTemplate.update(esnupdsql, paramMap);
			log.info("cancelUA count: " + count);

			log.info(" *** cancelUA SQL 3 *****" + sqlua);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlua, paramMap);

			if (rs.next()) {
				cntua = rs.getInt("cnt");
			}

			if (cntua == 0) {
				log.info(" *** cancelUA SQL 4 *****" + sqlft);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(sqlft, paramMap);
			}

			sqltlog = "SELECT MAX(TRANS_NBR) maxTransNbr FROM UA_DETAILS_TRANS WHERE UA_NBR=:uanbr ";

			if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 23/5/2002
			{
				log.info(" *** cancelUA SQL 5 *****" + sqltlog);
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);
				if (rs.next()) {
					stransno = (rs.getInt("maxTransNbr")) + 1;
				} else {
					stransno = 0;
				}
			}

			sb.append("INSERT INTO UA_DETAILS_TRANS(TRANS_NBR,UA_NBR,");
			sb.append("UA_STATUS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
			sb.append("VALUES(:stransno,:uanbr,'X',:userid,sysdate)");
			strInsert_trans = sb.toString();
			paramMap.put("stransno", stransno);

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				log.info(" *** cancelUA SQL 6 *****" + strInsert_trans);
				log.info(" paramMap: " + paramMap);
				count_trans = namedParameterJdbcTemplate.update(strInsert_trans, paramMap);
				log.info("cancelUA count_trans: " + count_trans);
			}

			if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 22/01/2003
			{
				log.info(" *** cancelUA SQL 7 *****" + sqltlog1);
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog1, paramMap);
				if (rs.next()) {
					stransno1 = (rs.getInt("maxTransNbr")) + 1;
				} else {
					stransno1 = 0;
				}
			}

			sb = new StringBuffer();
			sb.append(" (TRANS_NBR,ESN_ASN_NBR,");
			sb.append("UA_NBR_PKGS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,REMARKS) ");
			sb.append("VALUES(:stransno1,:esnasnnbr,:UA_Nbr_Pkgs,:userid,sysdate,'UA DEL')");
			strInsert_trans1 = strInsert_trans1 + sb.toString();

			paramMap.put("stransno1", stransno1);
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				log.info(" *** cancelUA SQL 8 *****" + strInsert_trans1);
				log.info(" paramMap: " + paramMap);
				count_trans1 = namedParameterJdbcTemplate.update(strInsert_trans1, paramMap);
				log.info("cancelUA count_trans1: " + count_trans1);
			}

			if (countua == 0) {
				log.info("Writing from UAEJB.cancelUA uaupdate");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}

			if (count == 0) {
				log.info("Writing from UAEJB.cancelUA esnupdsql");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 21/01/2003
			{
				if (count_trans == 0 || count_trans1 == 0) {
					log.info("Writing from UAEJB.cancelUA");
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}
			}

			log.info("END: *** cancelUA Result *****");
		} catch (BusinessException e) {
			log.info("Exception cancelUA : ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception cancelUA : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception cancelUA : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: cancelUA  DAO  END");
		}

	}

	@Override
	public List<UaEsnDetValueObject> getEsnView(String esnasnnbr, String transtype) throws BusinessException {
		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String sql = "";
		String BPName = "";
		String sql4 = "";
		List<UaEsnDetValueObject> esnList = new ArrayList<UaEsnDetValueObject>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getEsnView  DAO  Start Obj " + " esnasnnbr:" + CommonUtility.deNull(esnasnnbr) + " transtype:" + CommonUtility.deNull(transtype));

			sb.append("select esn.esn_asn_nbr,vsl_nm,in_voy_nbr,out_voy_nbr,bk_ref_nbr,PAYMENT_MODE, ");
			sb.append("ACCT_NBR,nbr_pkgs,nbr_pkgs - ua_nbr_pkgs,ua_nbr_pkgs,esn_wt,esn_vol,TRUCKER_NM, ");
			sb.append("TRUCKER_PHONE_NBR,TRUCKER_IC,TO_CHAR(FIRST_TRANS_DTTM,'DD/MM/YYYY HH24:MI'),");
			sb.append("crg_des,markings,esn.TRANS_TYPE,esn_details.acct_nbr,esn.wh_ind,esn.wh_aggr_nbr,esn.wh_remarks, ");
			sb.append("vessel_call.terminal, vessel_call.scheme, vessel_call.combi_gc_scheme,vessel_call.combi_gc_ops_ind from esn,esn_details, ");
			sb.append("vessel_call,berthing,esn_markings where esn.esn_asn_nbr = esn_details.esn_asn_nbr ");
			sb.append("and esn.OUT_VOY_VAR_NBR = vessel_call.vv_cd(+) and vessel_call.vv_cd = berthing.vv_cd(+) and ");
			sb.append("esn.esn_asn_nbr = esn_details.ESN_ASN_NBR and esn.esn_Asn_nbr = esn_markings.esn_asn_nbr ");
			sb.append(" and esn.TRANS_CRG<>'Y' AND ");
			sb.append("esn.esn_asn_nbr =:esnasnnbr ");
			sql1 = sb.toString();

			sb = new StringBuffer();
			sb.append("select esn.esn_asn_nbr,vsl_nm,in_voy_nbr,out_voy_nbr,bk_ref_nbr,PAYMENT_MODE, ");
			sb.append("acct_nbr,nbr_pkgs,nbr_pkgs - ua_nbr_pkgs,ua_nbr_pkgs,gross_wt,gross_vol,TRUCKER_NM, ");
			sb.append("'',TRUCKER_IC,TO_CHAR(FIRST_TRANS_DTTM,'DD/MM/YYYY HH24:MI'),crg_des,markings,");
			sb.append("esn.TRANS_TYPE,tesn_psa_jp.acct_nbr,esn.wh_ind,esn.wh_aggr_nbr,esn.wh_remarks, vessel_call.terminal, ");
			sb.append("vessel_call.scheme, vessel_call.combi_gc_scheme,  vessel_call.combi_gc_ops_ind ");
			sb.append("from esn,tesn_psa_jp,vessel_call,berthing,esn_markings where ");
			sb.append("esn.esn_asn_nbr = tesn_psa_jp.esn_asn_nbr and esn.OUT_VOY_VAR_NBR = vessel_call.vv_cd ");
			sb.append("and vessel_call.vv_cd = berthing.vv_cd  and esn.esn_Asn_nbr = esn_markings.esn_asn_nbr ");
			sb.append("and esn.TRANS_CRG<>'Y' AND ");
			sb.append("shift_ind = 1 and esn.esn_asn_nbr = :esnasnnbr");
			sql2 = sb.toString();

			sb = new StringBuffer();
			sb.append("select esn.esn_asn_nbr,vsl_nm,vessel_call.in_voy_nbr,vessel_call.out_voy_nbr,esn.bk_ref_nbr, ");
			sb.append("tesn_jp_jp.PAYMENT_MODE,tesn_jp_jp.acct_nbr,tesn_jp_jp.nbr_pkgs,");
			sb.append("tesn_jp_jp.nbr_pkgs - UA_Nbr_Pkgs,UA_Nbr_Pkgs,tesn_jp_jp.nom_wt, tesn_jp_jp.nom_vol,");
			sb.append("gb_edo.ADP_NM,'',gb_edo.ADP_IC_TDBCR_NBR,");
			sb.append("TO_CHAR(tesn_jp_jp.FIRST_TRANS_DTTM,'DD/MM/YYYY HH24:MI'),");
			sb.append("manifest_details.crg_des ,mft_markings.mft_markings,esn.TRANS_TYPE,");
			sb.append("tesn_jp_jp.acct_nbr ,esn.wh_ind,esn.wh_aggr_nbr,esn.wh_remarks, vessel_call.terminal, ");
			sb.append("vessel_call.scheme, vessel_call.combi_gc_scheme, vessel_call.combi_gc_ops_ind ");
			sb.append("from esn,tesn_jp_jp,vessel_call,berthing,gb_edo, ");
			sb.append("manifest_details,mft_markings where esn.esn_asn_nbr = tesn_jp_jp.esn_asn_nbr ");
			sb.append("and esn.OUT_VOY_VAR_NBR = vessel_call.vv_cd And vessel_call.vv_cd = berthing.vv_cd ");
			sb.append("and tesn_jp_jp.edo_asn_nbr = gb_edo.edo_asn_nbr and");
			sb.append(" gb_edo.mft_seq_nbr = manifest_details.mft_seq_nbr and esn.TRANS_CRG<>'Y' AND ");
			sb.append("manifest_details.mft_seq_nbr = mft_markings.mft_sq_nbr and shift_ind = 1 ");
			sb.append("and esn.esn_asn_nbr =:esnasnnbr");
			sql3 = sb.toString();

			sb = new StringBuffer();
			sb.append("SELECT E.ESN_ASN_NBR,VSL_NM,V.IN_VOY_NBR,V.OUT_VOY_NBR,ED.ss_ref_nbr,ED.PAYMENT_MODE,");
			sb.append("ED.ACCT_NBR,ED.NBR_PKGS AS NBR_PKGS,ED.NBR_PKGS-ED.UA_NBR_PKGS,ED.UA_NBR_PKGS,");
			sb.append("ED.SS_WT AS WT,ED.SS_VOL AS VOL,ED.SHIPPER_NM,ED.SHIPPER_CONTACT,'',");
			sb.append("TO_CHAR(ED.FIRST_TRANS_DTTM,'DD/MM/YYYY HH24:MI'),ED.CRG_DES AS CRG_DES,");
			sb.append("EM.MARKINGS AS CRG_MARKS,E.TRANS_TYPE,ED.acct_nbr, ");
			sb.append("e.wh_ind,e.wh_aggr_nbr,e.wh_remarks,V.terminal,V.scheme,V.combi_gc_scheme,V.combi_gc_ops_ind ");
			sb.append("FROM ESN E,SS_DETAILS ED,ESN_MARKINGS EM,vessel_call V,berthing ");
			sb.append("WHERE TRANS_TYPE = 'S' AND E.ESN_ASN_NBR=ED.ESN_ASN_NBR ");
			sb.append("AND E.OUT_VOY_VAR_NBR = V.vv_cd(+) and V.vv_cd = berthing.vv_cd(+) ");
			sb.append("AND E.TRANS_CRG<>'Y' AND SHIFT_IND=1 AND EM.ESN_ASN_NBR=E.ESN_ASN_NBR");
			sb.append(" AND E.esn_asn_nbr =:esnasnnbr");
			sql4 = sb.toString();

			if (transtype != null && !transtype.equals("") && transtype.equals("E")) {
				sql = sql1;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("C")) {
				sql = sql2;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("A")) {
				sql = sql3;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("S")) {
				sql = sql4;
			}

			log.info(" *** getEsnView SQL *****" + sql);

			paramMap.put("esnasnnbr", esnasnnbr);
			log.info("paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				UaEsnDetValueObject esnObj = new UaEsnDetValueObject();
				esnObj.setEsn_asn_nbr(CommonUtility.deNull(rs.getString(1)));
				esnObj.setVessel_name(CommonUtility.deNull(rs.getString(2)));
				esnObj.setIn_voy_nbr(CommonUtility.deNull(rs.getString(3)));
				esnObj.setOut_voy_nbr(CommonUtility.deNull(rs.getString(4)));
				esnObj.setBk_ref_nbr(CommonUtility.deNull(rs.getString(5)));
				esnObj.setPay_mode(CommonUtility.deNull(rs.getString(6)));
				BPName = getBillablePartyName(CommonUtility.deNull(rs.getString(7)));
				esnObj.setBill_party(BPName);
				esnObj.setDecl_pkg(CommonUtility.deNull(rs.getString(8)));
				esnObj.setBal_pkg(CommonUtility.deNull(rs.getString(9)));
				esnObj.setPkg_stored(CommonUtility.deNull(rs.getString(10)));
				esnObj.setWeight(CommonUtility.deNull(rs.getString(11)));
				esnObj.setVolume(CommonUtility.deNull(rs.getString(12)));
				esnObj.setTrucker_name(CommonUtility.deNull(rs.getString(13)));
				esnObj.setTrucker_cont_no(CommonUtility.deNull(rs.getString(14)));
				esnObj.setTrucker_ic(CommonUtility.deNull(rs.getString(15)));
				esnObj.setFirst_trans(CommonUtility.deNull(rs.getString(16)));
				esnObj.setCargo_desc(CommonUtility.deNull(rs.getString(17)));
				esnObj.setCargo_markings(CommonUtility.deNull(rs.getString(18)));
				esnObj.setTrans_type(CommonUtility.deNull(rs.getString(19)));
				esnObj.setAct_no(CommonUtility.deNull(rs.getString("acct_nbr")));
				esnObj.setWhInd(CommonUtility.deNull(rs.getString("wh_ind")));
		        esnObj.setWhAggrNbr(CommonUtility.deNull(rs.getString("wh_aggr_nbr")));
		        esnObj.setWhRemarks(CommonUtility.deNull(rs.getString("wh_remarks")));
		        esnObj.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
		        esnObj.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
		        esnObj.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
		        esnObj.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				esnList.add(esnObj);
			}
			log.info("END: *** getEsnView Result *****" + esnList.toString());
		} catch (NullPointerException e) {
			log.info("Exception getEsnView : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEsnView : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEsnView  DAO  END");
		}
		return esnList;
	}

	private String getBillablePartyName(String accNbr) throws BusinessException {
		String accNo = accNbr;
		String sql = "";
		String billablePartyName = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getBillablePartyName  DAO  Start Obj: " + "accNbr:" + CommonUtility.deNull(accNbr));

			sql = "select co.co_nm from cust_acct ca, company_code co where co.co_cd = ca.cust_cd and ca.ACCT_STATUS_CD='A' and ca.ACCT_NBR =:accNo";

			paramMap.put("accNo", GbmsCommonUtility.addApostr(accNo));
			log.info(" *** getBillablePartyName SQL *****" + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				billablePartyName = CommonUtility.deNull(rs.getString("co_nm"));
			}
			log.info("END: *** getBillablePartyName Result *****" + billablePartyName.toString());
		} catch (NullPointerException e) {
			log.info("Exception getBillablePartyName : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBillablePartyName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBillablePartyName  DAO  END");
		}
		return billablePartyName;
	}

	@Override
	public List<UaListObject> getUAList(String esnasnnbr) throws BusinessException {
		String sql = "";
		List<UaListObject> uaList = new ArrayList<UaListObject>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getUAList  DAO  Start Obj " + " esnasnnbr:" + CommonUtility.deNull(esnasnnbr));

			if (esnasnnbr.length() == 7)
				esnasnnbr = "0" + esnasnnbr;

			sb.append(" SELECT UA_NBR,NBR_PKGS,BILLABLE_TON,BILL_STATUS,UA_STATUS,");
			sb.append("TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI') TRANSDTTM FROM UA_DETAILS ");
			sb.append("WHERE UA_NBR LIKE :esnasnnbr");
			sql = sb.toString();

			paramMap.put("esnasnnbr", "U" + esnasnnbr + "%");
			log.info(" *** getUAList SQL *****" + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				UaListObject uaObj = new UaListObject();
				uaObj.setUa_nbr(CommonUtility.deNull(rs.getString("UA_NBR")));
				uaObj.setUa_nbr_pkgs(CommonUtility.deNull(rs.getString("NBR_PKGS")));
				uaObj.setBill_tonn(CommonUtility.deNull(rs.getString("BILLABLE_TON")));
				uaObj.setBill_status(CommonUtility.deNull(rs.getString("BILL_STATUS")));
				uaObj.setUa_status(CommonUtility.deNull(rs.getString("UA_STATUS")));
				uaObj.setTrans_time(CommonUtility.deNull(rs.getString("TRANSDTTM")));
				uaList.add(uaObj);
			}
			log.info("END: *** getUAList Result *****" + uaList.toString());
		} catch (NullPointerException e) {
			log.info("Exception getUAList : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getUAList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getUAList  DAO  END");
		}
		return uaList;

	}

	@Override
	public boolean chkESNStatus(String esnasnnbr) throws BusinessException {
		String sql = "";
		boolean esnstat = false;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: chkESNStatus  DAO  Start Obj " + " esnasnnbr:" + CommonUtility.deNull(esnasnnbr));

			sql = "SELECT ESN_STATUS FROM ESN WHERE ESN_STATUS='X' AND ESN_ASN_NBR=:esnasnnbr ";

			log.info(" *** chkESNStatus SQL *****" + sql);
			paramMap.put("esnasnnbr", esnasnnbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				esnstat = true;
			} else {
				esnstat = false;
			}

			log.info("END: *** chkESNStatus Result *****" + esnstat);
		} catch (NullPointerException e) {
			log.info("Exception chkESNStatus :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkESNStatus :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkESNStatus  DAO  END");
		}
		return esnstat;
	}

	@Override
	public boolean chkESNPkgs(String esnasnnbr, String transtype) throws BusinessException {
		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String sql4 = "";
		String sql = "";
		boolean esnstat = false;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: chkESNPkgs  DAO  Start Obj " + " esnasnnbr:" + CommonUtility.deNull(esnasnnbr) + " transtype:" + CommonUtility.deNull(transtype));

			sql1 = "SELECT nbr_pkgs-ua_nbr_pkgs FROM ESN_details WHERE ESN_ASN_NBR=:esnasnnbr ";
			sql2 = "SELECT nbr_pkgs-ua_nbr_pkgs FROM tesn_psa_jp WHERE ESN_ASN_NBR=:esnasnnbr ";
			sql3 = "SELECT nbr_pkgs-ua_nbr_pkgs FROM tesn_jp_jp WHERE ESN_ASN_NBR=:esnasnnbr ";
			sql4 = "SELECT nbr_pkgs-ua_nbr_pkgs FROM ss_details WHERE ESN_ASN_NBR=:esnasnnbr ";

			if (transtype != null && !transtype.equals("") && transtype.equals("E")) {
				sql = sql1;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("C")) {
				sql = sql2;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("A")) {
				sql = sql3;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("S")) {
				sql = sql4;
			}

			log.info(" *** chkESNPkgs SQL *****" + sql);
			paramMap.put("esnasnnbr", esnasnnbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			int nbrpkgs = 0;
			if (rs.next()) {
				nbrpkgs = rs.getInt(1);
			}
			if (nbrpkgs == 0) {
				esnstat = true;
			} else {
				esnstat = false;
			}

			log.info("END: *** chkESNPkgs Result *****" + esnstat);
		} catch (NullPointerException e) {
			log.info("Exception chkESNPkgs :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkESNPkgs :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkESNPkgs  DAO  END");
		}
		return esnstat;
	}

	@Override
	public List<UaEsnDetValueObject> getCreateUADisp(String esnasnnbr, String transtype) throws BusinessException {
		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String sql = "";
		String sql4 = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		List<UaEsnDetValueObject> esnList = new ArrayList<UaEsnDetValueObject>();
		SqlRowSet rs = null;
		try {
			log.info("START: getCreateUADisp  DAO  Start Obj " + " esnasnnbr:" + CommonUtility.deNull(esnasnnbr) + " transtype:" + CommonUtility.deNull(transtype));

			sb.append("SELECT to_char(atb_dttm,'dd/mm/yyyy hh24:mi') as atb,  vsl_nm, vessel_call.out_voy_nbr,");
			sb.append("esn_details.esn_wt as dec_wt,esn_details.esn_vol as dec_vol,");
			sb.append("esn_details.nbr_pkgs as dec_qty, bk_details.CNTR_TYPE as cntr_type,");
			sb.append("bk_details.CNTR_SIZE as cntr_size, markings , esn_details.crg_des ,cntr_nbr as cont_no,");
			sb.append("esn.esn_asn_nbr  as esn_asn ,esn.bk_ref_nbr,esn_details.nbr_pkgs-esn_details.ua_nbr_pkgs,");
			sb.append("to_char(GB_COD_DTTM,'dd/mm/yyyy hh24:mi') as cod,esn_details.acct_nbr,");
			sb.append(
					"to_char(ETB_DTTM,'dd/mm/yyyy hh24:mi') as etb,to_char(VSL_BERTH_DTTM,'dd/mm/yyyy hh24:mi') as btr ");
			sb.append("from  esn , vessel_call,berthing , esn_details , bk_details , esn_markings , esn_cntr ");
			sb.append("where esn.esn_asn_nbr = esn_details.esn_asn_nbr ");
			sb.append("and esn.esn_asn_nbr = esn_details.esn_asn_nbr ");
			sb.append("and esn.out_voy_var_nbr = vessel_call.vv_cd ");
			sb.append("and esn.esn_asn_nbr = esn_markings.esn_asn_nbr ");
			sb.append("and esn.esn_asn_nbr = esn_cntr.esn_asn_nbr(+) ");
			sb.append("and vessel_call.vv_cd = berthing.vv_cd and  ");
			sb.append("esn.bk_ref_nbr = bk_details.bk_ref_nbr and shift_ind=1 and esn.TRANS_CRG<>'Y' AND ");
			sb.append("esn.esn_asn_nbr =:esnasnnbr ");
			sql1 = sb.toString();

			sb = new StringBuffer();
			sb.append("SELECT to_char(atb_dttm,'dd/mm/yyyy hh24:mi') as atb, vsl_nm, vessel_call.out_voy_nbr, ");
			sb.append("tesn_psa_jp.gross_wt as dec_wt,tesn_psa_jp.gross_vol as dec_vol, ");
			sb.append("tesn_psa_jp.nbr_pkgs as dec_qty, bk_details.CNTR_TYPE as cntr_type, ");
			sb.append("bk_details.CNTR_SIZE as cntr_size, markings,tesn_psa_jp.crg_des,cntr_nbr as cont_no, ");
			sb.append(
					"esn.esn_asn_nbr as esn_asn, esn.bk_ref_nbr as bk_ref,tesn_psa_jp.nbr_pkgs-tesn_psa_jp.ua_nbr_pkgs,");
			sb.append("to_char(GB_COD_DTTM,'dd/mm/yyyy hh24:mi') as cod,tesn_psa_jp.acct_nbr, ");
			sb.append(
					"to_char(ETB_DTTM,'dd/mm/yyyy hh24:mi') as etb,to_char(VSL_BERTH_DTTM,'dd/mm/yyyy hh24:mi') as btr ");
			sb.append("from  esn , vessel_call,berthing , tesn_psa_jp , bk_details , ");
			sb.append("esn_markings , esn_cntr where  esn.esn_asn_nbr = tesn_psa_jp.esn_asn_nbr ");
			sb.append("and esn.out_voy_var_nbr = vessel_call.vv_cd and esn.esn_asn_nbr = esn_markings.esn_asn_nbr ");
			sb.append("and esn.esn_asn_nbr = esn_cntr.esn_asn_nbr(+) and vessel_call.vv_cd = berthing.vv_cd and  ");
			sb.append("esn.bk_ref_nbr = bk_details.bk_ref_nbr ");
			sb.append("and esn.trans_type='C' and shift_ind=1 and esn.TRANS_CRG<>'Y' AND ");
			sb.append("esn.esn_asn_nbr =:esnasnnbr");
			sql2 = sb.toString();

			sb = new StringBuffer();
			sb.append("select to_char(atb_dttm,'dd/mm/yyyy hh24:mi') as atb , vsl_nm, vessel_call.out_voy_nbr, ");
			sb.append("tesn_jp_jp.nom_wt as dec_wt,tesn_jp_jp.nom_vol as dec_vol, ");
			sb.append("tesn_jp_jp.nbr_pkgs as dec_qty, manifest_details.CNTR_TYPE as cntr_type, ");
			sb.append("manifest_details.CNTR_SIZE as cntr_size, mft_markings , ");
			sb.append("manifest_details.crg_des , bl_cntr_details.cntr_nbr as cont_no , ");
			sb.append(
					"esn.esn_asn_nbr  as esn_asn, esn.bk_ref_nbr as bk_ref,tesn_jp_jp.nbr_pkgs-tesn_jp_jp.ua_nbr_pkgs, ");
			sb.append("to_char(GB_COD_DTTM,'dd/mm/yyyy hh24:mi') as cod,tesn_jp_jp.acct_nbr ,");
			sb.append(
					"to_char(ETB_DTTM,'dd/mm/yyyy hh24:mi') as etb,to_char(VSL_BERTH_DTTM,'dd/mm/yyyy hh24:mi') as btr ");
			sb.append("from  esn , vessel_call,berthing , tesn_jp_jp , gb_edo , ");
			sb.append("manifest_details ,mft_markings ,bl_cntr_details ");
			sb.append("where  esn.esn_asn_nbr = tesn_jp_jp.esn_asn_nbr ");
			sb.append("and esn.out_voy_var_nbr = vessel_call.vv_cd ");
			sb.append("and tesn_jp_jp.edo_asn_nbr = gb_edo.edo_asn_nbr ");
			sb.append("and gb_edo.mft_seq_nbr = manifest_details.mft_seq_nbr ");
			sb.append("and manifest_details.MFT_SEQ_NBR = mft_markings.MFT_SQ_NBR ");
			sb.append("and manifest_details.MFT_SEQ_NBR = bl_cntr_details.MFT_SEQ_NBR(+) ");
			sb.append("and vessel_call.vv_cd = berthing.vv_cd and esn.trans_type='A' and shift_ind=1 ");
			sb.append("and esn.TRANS_CRG<>'Y' AND esn.esn_asn_nbr =:esnasnnbr ");
			sql3 = sb.toString();

			sb = new StringBuffer();
			sb.append("SELECT to_char(atb_dttm,'dd/mm/yyyy hh24:mi') as atb, vsl_nm, vessel_call.out_voy_nbr, ");
			sb.append("SS.SS_WT as dec_wt,SS.SS_VOL as dec_vol,SS.nbr_pkgs as dec_qty, '','', ");
			sb.append("markings , SS.CRG_DES ,  '', " + "esn.esn_asn_nbr  as esn_asn,");
			sb.append("SS.ss_ref_nbr,SS.nbr_pkgs-SS.ua_nbr_pkgs,to_char(GB_COD_DTTM,'dd/mm/yyyy hh24:mi') as cod,");
			sb.append(
					"SS.acct_nbr ,to_char(ETB_DTTM,'dd/mm/yyyy hh24:mi') as etb,to_char(VSL_BERTH_DTTM,'dd/mm/yyyy hh24:mi') as btr ");
			sb.append("from  esn , vessel_call,berthing , ss_details SS, ");
			sb.append("esn_markings  where  esn.esn_asn_nbr = SS.esn_asn_nbr ");
			sb.append("and esn.out_voy_var_nbr = vessel_call.vv_cd and esn.esn_asn_nbr = esn_markings.esn_asn_nbr ");
			sb.append("and vessel_call.vv_cd = berthing.vv_cd ");
			sb.append("and esn.trans_type='S' and shift_ind=1 and esn.TRANS_CRG<>'Y' AND ");
			sb.append("esn.esn_asn_nbr =:esnasnnbr");
			sql4 = sb.toString();

			if (transtype != null && !transtype.equals("") && transtype.equals("E")) {
				sql = sql1;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("C")) {
				sql = sql2;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("A")) {
				sql = sql3;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("S")) {
				sql = sql4;
			}
			log.info(" *** getCreateUADisp SQL *****" + sql);

			paramMap.put("esnasnnbr", esnasnnbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				UaEsnDetValueObject esnObj = new UaEsnDetValueObject();
				esnObj.setAtb(CommonUtility.deNull(rs.getString(1)));
				esnObj.setVessel_name(CommonUtility.deNull(rs.getString(2)));
				esnObj.setOut_voy_nbr(CommonUtility.deNull(rs.getString(3)));
				esnObj.setWeight(CommonUtility.deNull(rs.getString(4)));
				esnObj.setVolume(CommonUtility.deNull(rs.getString(5)));
				esnObj.setDecl_pkg(CommonUtility.deNull(rs.getString(6)));
				esnObj.setConttype(CommonUtility.deNull(rs.getString(7)));
				esnObj.setContsize(CommonUtility.deNull(rs.getString(8)));
				esnObj.setCargo_markings(CommonUtility.deNull(rs.getString(9)));
				esnObj.setCargo_desc(CommonUtility.deNull(rs.getString(10)));
				esnObj.setContno(CommonUtility.deNull(rs.getString(11)));
				esnObj.setEsn_asn_nbr(CommonUtility.deNull(rs.getString(12)));
				esnObj.setBk_ref_nbr(CommonUtility.deNull(rs.getString(13)));
				esnObj.setBal_pkg(CommonUtility.deNull(rs.getString(14)));
				esnObj.setCod(CommonUtility.deNull(rs.getString(15)));
				esnObj.setAct_no(CommonUtility.deNull(rs.getString(16)));
				esnObj.setTrans_type(transtype);
				esnObj.setEtb(CommonUtility.deNull(rs.getString("etb")));
				esnObj.setBtr(CommonUtility.deNull(rs.getString("btr")));
				esnList.add(esnObj);
			}
			log.info("END: *** getCreateUADisp Result *****" + esnList.toString());
		} catch (NullPointerException e) {
			log.info("Exception getCreateUADisp :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCreateUADisp :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCreateUADisp  DAO  END");
		}
		return esnList;
	}

	@Override
	public String getUANbr(String esnNo) throws BusinessException {
		String UaNbr = "";
		String ftrans = "";
		String retval = "";
		int count = 0;
		int tempInt = 0;
		String tempval = "";
		String tempua = "";
		String ftransql = "";
		String sql = "";
		SqlRowSet rs = null;
		SqlRowSet rsftrans = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getUANbr  DAO  Start Obj " + " esnNo:" + CommonUtility.deNull(esnNo));
			if (esnNo.length() == 7) {
				esnNo = "0" + esnNo;
			}

			sql = "select max(ua_nbr) as maxUaNbr from UA_details where UA_nbr like :esnNo";

			ftransql = "select ua_nbr from ua_details where ua_status ='A' and UA_nbr like :esnNo";

			log.info(" *** getUANbr SQL 1 *****" + sql);
			paramMap.put("esnNo", "U" + esnNo + "%");
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			
			log.info(" *** getUANbr SQL 2 *****" + ftransql);
			log.info(" paramMap: " + paramMap);
			rsftrans = namedParameterJdbcTemplate.queryForRowSet(ftransql, paramMap);

			if (rsftrans.next())
				ftrans = "False";
			else
				ftrans = "True";

			if (rs.next()) {
				tempua = rs.getString("maxUaNbr");
			}
			if (tempua != null && tempua.length() != 0) {
				count = 1;
			}
			if (count == 0) {
				UaNbr = "U" + esnNo + "0000";
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
				UaNbr = "U" + esnNo + tempval;
			} // outside if close

			retval = UaNbr + "-" + ftrans;

			log.info("END: *** getUANbr Result *****" + retval.toString());
		} catch (NullPointerException e) {
			log.info("Exception getUANbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getUANbr :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getUANbr  DAO  END");
		}
		return retval;
	}

	@Override
	public String createUA(String esnasnnbr, String transtype, String Esn_Nbr_Pkgs, String NomWt, String NomVol,
			String date_time, String UA_Nbr_Pkgs, String nric_no, String ictype, String dpname, String veh1,
			String veh2, String veh3, String veh4, String veh5, String userid, String strCntrNum, String strUnStuffDt)
			throws BusinessException {
		String sql = "";
		String UANbr = "";
		String ftrans = "";
		String sqlua = "";
		String esnupdsql = "";
		String uanbrtrans = "";
		String sqlveh1 = "";
		String sqlveh2 = "";
		String sqlveh3 = "";
		String sqlveh4 = "";
		String sqlveh5 = "";
		String sqlft = "";
		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String sql4 = "";
		String sql1_trans = "";
		String sql2_trans = "";
		String sql3_trans = "";
		String sql4_trans = "";
		String sqlveh1_trans = "";
		String sqlveh2_trans = "";
		String sqlveh3_trans = "";
		String sqlveh4_trans = "";
		String sqlveh5_trans = "";
		String sql_trans = "";
		String sqltlog = "";
		String strInsert_trans = "";
		SqlRowSet rs = null;
		StringTokenizer uatrans = null;
		StringBuffer sb = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		int countua = 0;
		int count = 0;
		double Bill_ton = 0.0;
		double ua_nom_wt = 0.0;
		double ua_nom_vol = 0.0;
		try {
			log.info("START: createUA  DAO  Start Obj " + " esnasnnbr:" + CommonUtility.deNull(esnasnnbr) + " transtype:" + CommonUtility.deNull(transtype)
					+ " Esn_Nbr_Pkgs:" + CommonUtility.deNull(Esn_Nbr_Pkgs) + " NomWt:" + CommonUtility.deNull(NomWt) + " NomVol:" + CommonUtility.deNull(NomVol)
					+ " date_time:" + CommonUtility.deNull(date_time) + " UA_Nbr_Pkgs::UA_Nbr_Pkgs nric_no:" + CommonUtility.deNull(nric_no)
					+ " ictype:" + CommonUtility.deNull(ictype) + " dpname:" + CommonUtility.deNull(dpname) + " veh1:" + CommonUtility.deNull(veh1)
					+ " veh2:" + CommonUtility.deNull(veh2) + " veh3:" + CommonUtility.deNull(veh3) + " veh4:" + CommonUtility.deNull(veh4)
					+ " veh5:" + CommonUtility.deNull(veh5) + " userid:" + CommonUtility.deNull(userid) + " strCntrNum:" + CommonUtility.deNull(strCntrNum)
					+ " strUnStuffDt:" + CommonUtility.deNull(strUnStuffDt));

			boolean vslstat = chkVslStat(esnasnnbr);
			if (vslstat) {
				log.info("Writing from UAEJB.createUA");
				log.info("Vessel Status is closed cannot Create UA");
				throw new BusinessException("Vessel is closed. Creation of UA is not allowed. ");
			}
			boolean esnstat = chkESNStatus(esnasnnbr);
			if (esnstat) {
				log.info("Writing from UAEJB.CreateUA");
				log.info("ESN Cancelled cannot create UA");
				throw new BusinessException("ESN has been cancelled. Creation of UA is not allowed.");
			}
			boolean balpkgs = chkBalpkgs(esnasnnbr, UA_Nbr_Pkgs, transtype);
			if (balpkgs) {
				log.info("Writing from UAEJB.CreateUA");
				log.info("UANbrPkgs greater than EsnPkgs cannot create UA");
				throw new BusinessException("Trans Qty should be less than Available Qty");
			}

			uanbrtrans = getUANbr(esnasnnbr);
			uatrans = new StringTokenizer(uanbrtrans, "-");
			UANbr = (uatrans.nextToken()).trim();
			ftrans = (uatrans.nextToken()).trim();

			ua_nom_wt = (Integer.parseInt(UA_Nbr_Pkgs) / Integer.parseInt(Esn_Nbr_Pkgs)) * Double.parseDouble(NomWt);
			ua_nom_vol = (Integer.parseInt(UA_Nbr_Pkgs) / Integer.parseInt(Esn_Nbr_Pkgs)) * Double.parseDouble(NomVol);

			if (ftrans.equals("True")) {
				if ((Double.parseDouble(NomWt) / 1000) > Double.parseDouble(NomVol)) {
					Bill_ton = Double.parseDouble(NomWt) / 1000;
				} else {
					Bill_ton = Double.parseDouble(NomVol);
				} // end if nomwt
			} else {
				Bill_ton = 0;
			} // end if ftrans

			sb.append("insert into ua_details(UA_NBR,ESN_ASN_NBR,UA_STATUS,DP_IC_TYPE,DP_NM,DP_IC_NBR, ");
			sb.append("TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON, ");
			sb.append("UA_CREATE_LOGIN,UA_CREATE_DTTM,LAST_MODIFY_USER_ID,");
			sb.append("LAST_MODIFY_DTTM,CNTR_NBR,UNSTUFF_DTTM) VALUES(");
			sb.append(":UANbr,:esnasnnbr,'A',");
			sql = sb.toString();
			// "LAST_MODIFY_DTTM) VALUES('"+UANbr+"','"+esnasnnbr+"','A','";//Vani --
			// 13thAug,03

			sqlveh1 = "insert into ua_veh(UA_NBR,UA_VEH_SEQ,VEH_NO) values(:UANbr,1,:veh1)";
			sqlveh2 = "insert into ua_veh(UA_NBR,UA_VEH_SEQ,VEH_NO) values(:UANbr,2,:veh2)";
			sqlveh3 = "insert into ua_veh(UA_NBR,UA_VEH_SEQ,VEH_NO) values(:UANbr,3,:veh3)";
			sqlveh4 = "insert into ua_veh(UA_NBR,UA_VEH_SEQ,VEH_NO) values(:UANbr,4,:veh4)";
			sqlveh5 = "insert into ua_veh(UA_NBR,UA_VEH_SEQ,VEH_NO) values(:UANbr,5,:veh5)";

			sqlveh1_trans = "insert into ua_veh_trans(trans_nbr,UA_NBR,UA_VEH_SEQ,VEH_NO) values('0',:UANbr,1,:veh1)";
			sqlveh2_trans = "insert into ua_veh_trans(trans_nbr,UA_NBR,UA_VEH_SEQ,VEH_NO) values('0',:UANbr,2,:veh2)";
			sqlveh3_trans = "insert into ua_veh_trans(trans_nbr,UA_NBR,UA_VEH_SEQ,VEH_NO) values('0',:UANbr,3,:veh3)";
			sqlveh4_trans = "insert into ua_veh_trans(trans_nbr,UA_NBR,UA_VEH_SEQ,VEH_NO) values('0',:UANbr,4,:veh4)";
			sqlveh5_trans = "insert into ua_veh_trans(trans_nbr,UA_NBR,UA_VEH_SEQ,VEH_NO) values('0',:UANbr,5,:veh5)";

			sqlua = "select count(*) from ua_details where ua_status = 'A' and esn_asn_nbr = :esnasnnbr";

			int cntua = 0;
			int cnt_trans = 0;
			int stransno = 0;
			int count_trans = 0;

			log.info(" *** createUA SQL 1 *****" + sqlua);
			paramMap.put("esnasnnbr", esnasnnbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlua, paramMap);

			if (rs.next()) {
				cntua = rs.getInt(1);
			}
			if (cntua == 0) {
				sql1 = "update esn_details set FIRST_TRANS_DTTM = to_date(:date_time,'dd/mm/yyyy hh24:mi') where esn_asn_nbr=:esnasnnbr";
				sql2 = "update tesn_psa_jp set FIRST_TRANS_DTTM = to_date(:date_time,'dd/mm/yyyy hh24:mi') where esn_asn_nbr=:esnasnnbr";
				sql3 = "update tesn_jp_jp set FIRST_TRANS_DTTM = to_date(:date_time,'dd/mm/yyyy hh24:mi') where esn_asn_nbr=:esnasnnbr";
				sql4 = "update ss_details set FIRST_TRANS_DTTM = to_date(:date_time,'dd/mm/yyyy hh24:mi') where esn_asn_nbr=:esnasnnbr";
			}

			sql1_trans = "SELECT MAX(TRANS_NBR) FROM ESN_DETAILS_TRANS WHERE ESN_ASN_NBR=:esnasnnbr";
			sql2_trans = "SELECT MAX(TRANS_NBR) FROM TESN_JP_JP_TRANS WHERE ESN_ASN_NBR=:esnasnnbr";
			sql3_trans = "SELECT MAX(TRANS_NBR) FROM TESN_PSA_JP_TRANS WHERE ESN_ASN_NBR=:esnasnnbr";
			sql4_trans = "SELECT MAX(TRANS_NBR) FROM SS_DETAILS_TRANS WHERE ESN_ASN_NBR=:esnasnnbr";

			if (transtype != null && !transtype.equals("") && transtype.equals("E")) {
				esnupdsql = "update esn_details set ua_nbr_pkgs =ua_nbr_pkgs+:UA_Nbr_Pkgs where esn_asn_nbr =:esnasnnbr";
				sqlft = sql1;
				sqltlog = sql1_trans;
				strInsert_trans = "INSERT INTO ESN_DETAILS_TRANS ";
			}
			if (transtype != null && !transtype.equals("") && transtype.equals("A")) {
				esnupdsql = "update tesn_jp_jp set ua_nbr_pkgs=ua_nbr_pkgs+:UA_Nbr_Pkgs where esn_asn_nbr =:esnasnnbr";
				sqlft = sql3;
				sqltlog = sql2_trans;
				strInsert_trans = "INSERT INTO TESN_JP_JP_TRANS ";
			}
			if (transtype != null && !transtype.equals("") && transtype.equals("C")) {
				esnupdsql = "update tesn_psa_jp set ua_nbr_pkgs=ua_nbr_pkgs+:UA_Nbr_Pkgs where esn_asn_nbr =:esnasnnbr";
				sqlft = sql2;
				sqltlog = sql3_trans;
				strInsert_trans = "INSERT INTO TESN_PSA_JP_TRANS ";
			}
			if (transtype != null && !transtype.equals("") && transtype.equals("S")) {
				esnupdsql = "update ss_details set ua_nbr_pkgs=ua_nbr_pkgs+:UA_Nbr_Pkgs where esn_asn_nbr =:esnasnnbr";
				sqlft = sql4;
				sqltlog = sql4_trans;
				strInsert_trans = "INSERT INTO SS_DETAILS_TRANS ";
			}

			if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 22/01/2003
			{
				log.info(" *** createUA SQL 2 *****" + sqltlog);
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);
				if (rs.next()) {
					stransno = (rs.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
			}

			sb = new StringBuffer();
			sb.append(" (TRANS_NBR,ESN_ASN_NBR,");
			sb.append("UA_NBR_PKGS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,REMARKS) ");
			sb.append("VALUES(:stransno,:esnasnnbr,:UA_Nbr_Pkgs,:userid");
			sb.append(",sysdate,'UA ADD')");
			strInsert_trans = strInsert_trans + sb.toString();

			paramMap.put("stransno", stransno);
			paramMap.put("UA_Nbr_Pkgs", UA_Nbr_Pkgs);
			paramMap.put("userid", userid);
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				log.info(" *** createUA SQL 3 *****" + strInsert_trans);
				log.info(" paramMap: " + paramMap);
				count_trans = namedParameterJdbcTemplate.update(strInsert_trans, paramMap);
				log.info("createUA count_trans: " + count_trans);
			}

			if (cntua == 0) {
				log.info(" *** createUA SQL 4 *****" + sqlft);
				paramMap.put("date_time", date_time);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(sqlft, paramMap);
			}
			log.info(" *** createUA SQL 5 *****" + esnupdsql);
			log.info(" paramMap: " + paramMap);
			count = namedParameterJdbcTemplate.update(esnupdsql, paramMap);
			log.info("createUA count: " + count);

			if (count == 0) {
				log.info("Writing from UAEJB.createUA esnupdsql");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			} else if (count > 0) {
				sb = new StringBuffer();
				sb.append(":ictype,:dpname,:nric_no,to_date(:date_time,'DD/MM/YYYY HH24:MI'),");
				sb.append(":UA_Nbr_Pkgs,:ua_nom_wt,:ua_nom_vol,:Bill_ton,:userid,sysdate,:userid");
				sb.append(",sysdate,:strCntrNum,to_date(:strUnStuffDt,'DD/MM/YYYY HH24:MI'))");
				sql = sql + sb.toString();
				// UA_Nbr_Pkgs+","+ua_nom_wt+","+ua_nom_vol+","+Bill_ton+",'"+userid+"',sysdate,'"+userid+"',sysdate)";
				// VANI-Aug 13th,03
				log.info(" *** createUA SQL *****" + sql);
				paramMap.put("ictype", ictype);
				paramMap.put("dpname", dpname);
				paramMap.put("nric_no", nric_no);
				paramMap.put("date_time", date_time);
				paramMap.put("UA_Nbr_Pkgs", UA_Nbr_Pkgs);
				paramMap.put("ua_nom_wt", ua_nom_wt);
				paramMap.put("ua_nom_vol", ua_nom_vol);
				paramMap.put("Bill_ton", Bill_ton);
				paramMap.put("userid", userid);
				paramMap.put("strCntrNum", strCntrNum);
				paramMap.put("strUnStuffDt", strUnStuffDt);
				paramMap.put("UANbr", UANbr);
				log.info(" *** createUA SQL 5 *****" + sql);
				log.info(" paramMap: " + paramMap);
				countua = namedParameterJdbcTemplate.update(sql, paramMap);
				log.info("createUA countua: " + countua);

				if (logStatusGlobal.equalsIgnoreCase("Y")) {
					sb = new StringBuffer();
					sb.append(
							"insert into ua_details_trans(Trans_nbr,UA_NBR,ESN_ASN_NBR,UA_STATUS,DP_IC_TYPE,DP_NM,DP_IC_NBR, ");
					sb.append(
							"TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,UA_CREATE_LOGIN,UA_CREATE_DTTM,LAST_MODIFY_USER_ID, ");
					sb.append("LAST_MODIFY_DTTM) VALUES('0',:UANbr,:esnasnnbr,'A',:ictype ,:dpname,:nric_no, ");
					sb.append("to_date(:date_time,'DD/MM/YYYY HH24:MI'),:UA_Nbr_Pkgs,:ua_nom_wt,:ua_nom_vol");
					sb.append(",:Bill_ton,:userid,sysdate,:userid,sysdate)");
					sql_trans = sb.toString();

					paramMap.put("UANbr", UANbr);
					log.info(" *** createUA SQL 6 *****" + sql_trans);
					log.info(" paramMap: " + paramMap);
					cnt_trans = namedParameterJdbcTemplate.update(sql_trans, paramMap);
					log.info("createUA cnt_trans: " + cnt_trans);
				}

				paramMap.put("UANbr", UANbr);
				if (veh1 != null && !veh1.equals("")) {
					log.info(" *** createUA SQL 7 *****" + sqlveh1);
					paramMap.put("veh1", veh1);
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(sqlveh1, paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 21/01/2003
					{
						log.info(" *** createUA SQL 8 *****" + sqlveh1_trans);
						log.info(" paramMap: " + paramMap);
						namedParameterJdbcTemplate.update(sqlveh1_trans, paramMap);
					}
				}
				if (veh2 != null && !veh2.equals("")) {
					paramMap.put("veh2", veh2);
					log.info(" *** createUA SQL 9 *****" + sqlveh2);
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(sqlveh2, paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 21/01/2003
					{
						log.info(" *** createUA SQL 10 *****" + sqlveh2_trans);
						log.info(" paramMap: " + paramMap);
						namedParameterJdbcTemplate.update(sqlveh2_trans, paramMap);
					}
				}
				if (veh3 != null && !veh3.equals("")) {
					paramMap.put("veh3", veh3);
					log.info(" *** createUA SQL 11 *****" + sqlveh3);
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(sqlveh3, paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 21/01/2003
					{
						log.info(" *** createUA SQL 12 *****" + sqlveh3_trans);
						log.info(" paramMap: " + paramMap);
						namedParameterJdbcTemplate.update(sqlveh3_trans, paramMap);
					}
				}
				if (veh4 != null && !veh4.equals("")) {
					paramMap.put("veh4", veh4);
					log.info(" *** createUA SQL 13 *****" + sqlveh4);
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(sqlveh4, paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 21/01/2003
					{
						log.info(" *** createUA SQL 14 *****" + sqlveh4_trans);
						log.info(" paramMap: " + paramMap);
						namedParameterJdbcTemplate.update(sqlveh4_trans, paramMap);
					}
				}
				if (veh5 != null && !veh5.equals("")) {
					paramMap.put("veh5", veh5);
					log.info(" *** createUA SQL 15 *****" + sqlveh5);
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(sqlveh5, paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 21/01/2003
					{
						log.info(" *** createUA SQL 16 *****" + sqlveh5_trans);
						log.info(" paramMap: " + paramMap);
						namedParameterJdbcTemplate.update(sqlveh5_trans, paramMap);
					}
				}

			} // end if count

			if (countua == 0) {
				log.info("Writing from UAEJB.createUA");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y"))// Transaction Log Table Insertion 21/01/2003
			{
				if (cnt_trans == 0 || count_trans == 0) {
					log.info("Writing from UAEJB.createUA");
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}
			}

			log.info("END: *** createUA Result *****" + UANbr.toString());
		} catch (BusinessException e) {
			log.info("Exception createUA :", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception createUA :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception createUA :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: createUA  DAO  END");
		}
		return UANbr;
	}

	private boolean chkBalpkgs(String esnasnnbr, String nbrpkgs, String transtype) throws BusinessException {
		String sql = "";
		boolean esnstat = false;
		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String sql4 = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: chkBalpkgs  DAO  Start Obj " + " esnasnnbr:" + CommonUtility.deNull(esnasnnbr) + " nbrpkgs:" + CommonUtility.deNull(nbrpkgs)
					+ " transtype:" + CommonUtility.deNull(transtype));

			sql1 = "SELECT nbr_pkgs-ua_nbr_pkgs balnbrpkgs FROM ESN_details WHERE ESN_ASN_NBR=:esnasnnbr";
			sql2 = "SELECT nbr_pkgs-ua_nbr_pkgs balnbrpkgs FROM tesn_psa_jp WHERE ESN_ASN_NBR=:esnasnnbr";
			sql3 = "SELECT nbr_pkgs-ua_nbr_pkgs balnbrpkgs FROM tesn_jp_jp WHERE ESN_ASN_NBR=:esnasnnbr";
			sql4 = "SELECT nbr_pkgs-ua_nbr_pkgs balnbrpkgs FROM ss_details WHERE ESN_ASN_NBR=:esnasnnbr";

			if (transtype != null && !transtype.equals("") && transtype.equals("E")) {
				sql = sql1;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("C")) {
				sql = sql2;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("A")) {
				sql = sql3;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("S")) {
				sql = sql4;
			}

			log.info(" *** chkBalpkgs SQL *****" + sql);
			paramMap.put("esnasnnbr", esnasnnbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			int balnbrpkgs = 0;
			if (rs.next()) {
				balnbrpkgs = rs.getInt("balnbrpkgs");
			}
			if (Integer.parseInt(nbrpkgs) > balnbrpkgs) {
				esnstat = true;
			} else {
				esnstat = false;
			}

			log.info("END: *** chkBalpkgs Result *****" + esnstat);
		} catch (NullPointerException e) {
			log.info("Exception chkBalpkgs :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkBalpkgs :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkBalpkgs  DAO  END");
		}
		return esnstat;
	}

	@Override
	public String getVcd(String esnNo) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = "";
		String vvcd = "";
		sql = "SELECT OUT_VOY_VAR_NBR FROM ESN WHERE ESN_ASN_NBR =:esnNo";
		try {
			log.info("START: getVcd  DAO  Start Obj " + " esnNo:" + CommonUtility.deNull(esnNo));

			log.info(" *** getVcd SQL *****" + sql);

			paramMap.put("esnNo", esnNo);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				vvcd = rs.getString("OUT_VOY_VAR_NBR");
			}
			log.info("END: *** getVcd Result *****" + vvcd.toString());
		} catch (NullPointerException e) {
			log.info("Exception getVcd :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVcd :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVcd  DAO  END");
		}
		return vvcd;
	}

	@Override
	public String getSysdate() throws BusinessException {
		String sql = "";
		String sdate = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		log.info("Writing from UAEJB.getSysdate");
		sql = "SELECT TO_CHAR(SYSDATE,'DD/MM/YYYY HH24:MI') syDate FROM DUAL";
		try {
			log.info("START: getSysdate  DAO  Start Obj ");

			log.info(" *** getSysdate SQL *****" + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				sdate = rs.getString("syDate");
			}
			log.info("END: *** getSysdate Result *****" + sdate.toString());

		} catch (NullPointerException ne) {
			log.info("Exception getSysdate : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSysdate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSysdate  DAO  END");
		}
		return sdate;
	}

	@Override
	public TableResult getEsnList(String esnasnnbr, Criteria criteria) throws BusinessException {
		String sql = "";
		List<UaEsnListValueObject> esnList = new ArrayList<UaEsnListValueObject>();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		try {
			log.info("START: getEsnList  DAO  Start Obj " + " esnasnnbr:" + CommonUtility.deNull(esnasnnbr));

			if (esnasnnbr != null && !esnasnnbr.equals("")) {
				if (esnasnnbr.substring(0, 2).equals("00")) {
					if (esnasnnbr.length() >= 1)
						esnasnnbr = esnasnnbr.substring(2);
				} else if (esnasnnbr.substring(0, 1).equals("0")) {
					if (esnasnnbr.length() >= 1)
						esnasnnbr = esnasnnbr.substring(1);
				}
			}

			sb.append(" SELECT E.ESN_ASN_NBR, E.TRANS_TYPE, E.ESN_STATUS, E.bk_ref_nbr,ED.NBR_PKGS, ED.UA_NBR_PKGS, (ED.NBR_PKGS - ED.UA_NBR_PKGS) AS BALANCE,NULL EDO_ASN_NBR, 'Local' MVT, VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND FROM VESSEL_CALL VC, ESN E,ESN_DETAILS ED WHERE E.TRANS_CRG<>'Y' AND TRANS_TYPE = 'E'  AND E.ESN_ASN_NBR=ED.ESN_ASN_NBR AND E.OUT_VOY_VAR_NBR = VC.VV_CD AND ESN_STATUS='A' AND E.ESN_ASN_NBR LIKE :esnasnnbr " );
			sb.append(" UNION ");
			sb.append(" SELECT E.ESN_ASN_NBR,E.TRANS_TYPE,E.ESN_STATUS,E.bk_ref_nbr,ED.NBR_PKGS,ED.UA_NBR_PKGS,(ed.NBR_PKGS - ED.UA_NBR_PKGS) AS BALANCE,ED.EDO_ASN_NBR EDO_ASN_NBR, decode(edo.crg_status,'L','JP-JP (Local)','T', 'JP-JP (Transhipment)') MVT, VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND  FROM VESSEL_CALL VC,ESN E,TESN_JP_JP ED, GB_EDO EDO WHERE EDO.EDO_ASN_NBR = ED.EDO_ASN_NBR AND E.OUT_VOY_VAR_NBR = VC.VV_CD AND E.TRANS_CRG<>'Y' AND TRANS_TYPE = 'A' AND E.ESN_ASN_NBR=ED.ESN_ASN_NBR AND ESN_STATUS='A' AND E.ESN_ASN_NBR LIKE :esnasnnbr");
			sb.append(" UNION ");
			sb.append(" SELECT E.ESN_ASN_NBR,E.TRANS_TYPE,E.ESN_STATUS,E.bk_ref_nbr,ED.NBR_PKGS,ED.UA_NBR_PKGS,(ed.NBR_PKGS - ED.UA_NBR_PKGS) AS BALANCE,NULL EDO_ASN_NBR, 'PSA-JP' MVT, VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND FROM VESSEL_CALL VC,ESN E,TESN_PSA_JP ED WHERE E.TRANS_CRG<>'Y' AND TRANS_TYPE = 'C' AND E.ESN_ASN_NBR=ED.ESN_ASN_NBR AND E.OUT_VOY_VAR_NBR = VC.VV_CD AND ESN_STATUS='A' AND E.ESN_ASN_NBR   LIKE :esnasnnbr");
			sb.append(" UNION ");
			sb.append(" SELECT E.ESN_ASN_NBR,E.TRANS_TYPE,E.ESN_STATUS,ED.ss_ref_nbr,ED.NBR_PKGS,ED.UA_NBR_PKGS,(ed.NBR_PKGS - ED.UA_NBR_PKGS) AS BALANCE,NULL EDO_ASN_NBR, 'S/Store' MVT, VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND FROM VESSEL_CALL VC, ESN E,SS_DETAILS ED WHERE E.TRANS_CRG<>'Y' AND TRANS_TYPE = 'S' AND E.ESN_ASN_NBR=ED.ESN_ASN_NBR AND E.OUT_VOY_VAR_NBR = VC.VV_CD AND ESN_STATUS='A' AND E.ESN_ASN_NBR   LIKE :esnasnnbr");
			sb.append(" ORDER BY ESN_ASN_NBR DESC");

			sql = sb.toString();
			
			
			paramMap.put("esnasnnbr", "%" + esnasnnbr +"%");
			
			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
			}
			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());
			}
			log.info(" *** getEsnList SQL *****" + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			String esn_asn_nbr = "";
			String trans_type = "";
			String esn_status = "";
			String nbr_pkgs = "";
			String ua_nbr_pkgs = "";
			String balance = "";
			String bk_ref_nbr = "";
			String edo_asn_nbr = "";
			String mvt = "";

			while (rs.next()) {
				esn_asn_nbr = CommonUtility.deNull(rs.getString(1));
				if (esn_asn_nbr.length() == 7)
					esn_asn_nbr = "0" + esn_asn_nbr;
				if (esn_asn_nbr.length() == 6)
					esn_asn_nbr = "00" + esn_asn_nbr;
				trans_type = CommonUtility.deNull(rs.getString(2));
				esn_status = CommonUtility.deNull(rs.getString(3));
				bk_ref_nbr = CommonUtility.deNull(rs.getString(4));
				nbr_pkgs = CommonUtility.deNull(rs.getString(5));
				ua_nbr_pkgs = CommonUtility.deNull(rs.getString(6));
				balance = CommonUtility.deNull(rs.getString(7));
				edo_asn_nbr = CommonUtility.deNull(rs.getString(8));
				mvt = CommonUtility.deNull(rs.getString(9));
				UaEsnListValueObject esnObj = new UaEsnListValueObject();
				esnObj.setEsn_asn_nbr(esn_asn_nbr);
				esnObj.setTrans_type(trans_type);
				esnObj.setEsn_status(esn_status);
				esnObj.setBk_ref_nbr(bk_ref_nbr);
				esnObj.setNbr_pkgs(nbr_pkgs);
				esnObj.setUa_nbr_pkgs(ua_nbr_pkgs);
				esnObj.setBalance(balance);
				esnObj.setEdo_asn_nbr(edo_asn_nbr);
				esnObj.setMvt(mvt);
				esnObj.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				esnObj.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				esnObj.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				esnObj.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				esnList.add(esnObj);
			}
			for (UaEsnListValueObject object : esnList) {
				topsModel.put(object);
			}
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
			log.info("END: *** getEsnList Result *****" + esnList.size());

		} catch (NullPointerException ne) {
			log.info("Exception getEsnList : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEsnList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEsnList  DAO  END");
		}

		return tableResult;
	}

	@Override
	public List<UaEsnListValueObject> getEsnList(String esnasnnbr) throws BusinessException {

		String sql = "";
		StringBuilder sb = new StringBuilder();
		List<UaEsnListValueObject> esnList = new ArrayList<UaEsnListValueObject>();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: getEsnList  DAO  Start Obj " + " esnasnnbr:" + CommonUtility.deNull(esnasnnbr));

			if (esnasnnbr != null && !esnasnnbr.equals("")) {
				if (esnasnnbr.substring(0, 2).equals("00")) {
					if (esnasnnbr.length() >= 1)
						esnasnnbr = esnasnnbr.substring(2);
				} else if (esnasnnbr.substring(0, 1).equals("0")) {
					if (esnasnnbr.length() >= 1)
						esnasnnbr = esnasnnbr.substring(1);
				}
			}
			sb.append(" SELECT E.ESN_ASN_NBR, E.TRANS_TYPE, E.ESN_STATUS, E.bk_ref_nbr,ED.NBR_PKGS, ED.UA_NBR_PKGS, (ED.NBR_PKGS - ED.UA_NBR_PKGS) AS BALANCE,NULL EDO_ASN_NBR, ");
			sb.append(" 'Local' MVT, VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND FROM VESSEL_CALL VC, ESN E,ESN_DETAILS ED WHERE E.TRANS_CRG<>'Y' AND TRANS_TYPE = 'E' ");
			sb.append(" AND E.ESN_ASN_NBR=ED.ESN_ASN_NBR AND E.OUT_VOY_VAR_NBR = VC.VV_CD AND ESN_STATUS='A' AND E.ESN_ASN_NBR = :esnasnnbr ");
			sb.append(" UNION ");
			sb.append(" SELECT E.ESN_ASN_NBR,E.TRANS_TYPE,E.ESN_STATUS,E.bk_ref_nbr,ED.NBR_PKGS,ED.UA_NBR_PKGS,(ed.NBR_PKGS - ED.UA_NBR_PKGS) AS BALANCE,ED.EDO_ASN_NBR EDO_ASN_NBR, ");
			sb.append(" decode(edo.crg_status,'L','JP-JP (Local)','T', 'JP-JP (Transhipment)') MVT, VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND  FROM VESSEL_CALL VC,ESN E, ");
			sb.append(" TESN_JP_JP ED, GB_EDO EDO WHERE EDO.EDO_ASN_NBR = ED.EDO_ASN_NBR AND E.OUT_VOY_VAR_NBR = VC.VV_CD AND E.TRANS_CRG<>'Y' AND TRANS_TYPE = 'A' ");
			sb.append(" AND E.ESN_ASN_NBR=ED.ESN_ASN_NBR AND ESN_STATUS='A' AND E.ESN_ASN_NBR = :esnasnnbr ");
			sb.append(" UNION ");
			sb.append(" SELECT E.ESN_ASN_NBR,E.TRANS_TYPE,E.ESN_STATUS,E.bk_ref_nbr,ED.NBR_PKGS,ED.UA_NBR_PKGS,(ed.NBR_PKGS - ED.UA_NBR_PKGS) AS BALANCE,NULL EDO_ASN_NBR, ");
			sb.append(" 'PSA-JP' MVT, VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND FROM VESSEL_CALL VC,ESN E,TESN_PSA_JP ED WHERE E.TRANS_CRG<>'Y' ");
			sb.append(" AND TRANS_TYPE = 'C' AND E.ESN_ASN_NBR=ED.ESN_ASN_NBR AND E.OUT_VOY_VAR_NBR = VC.VV_CD AND ESN_STATUS='A' AND E.ESN_ASN_NBR = :esnasnnbr ");
			sb.append(" UNION ");
			sb.append(" SELECT E.ESN_ASN_NBR,E.TRANS_TYPE,E.ESN_STATUS,ED.ss_ref_nbr,ED.NBR_PKGS,ED.UA_NBR_PKGS,(ed.NBR_PKGS - ED.UA_NBR_PKGS) AS BALANCE,NULL EDO_ASN_NBR, ");
			sb.append(" 'S/Store' MVT, VC.TERMINAL, VC.SCHEME, VC.COMBI_GC_SCHEME, VC.COMBI_GC_OPS_IND FROM VESSEL_CALL VC, ESN E,SS_DETAILS ED WHERE E.TRANS_CRG<>'Y' ");
			sb.append(" AND TRANS_TYPE = 'S' AND E.ESN_ASN_NBR=ED.ESN_ASN_NBR AND E.OUT_VOY_VAR_NBR = VC.VV_CD AND ESN_STATUS='A' AND E.ESN_ASN_NBR = :esnasnnbr ORDER BY ESN_ASN_NBR DESC ");
			sql = sb.toString();
			
			log.info(" *** getEsnList SQL *****" + sql);
			paramMap.put("esnasnnbr", esnasnnbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			String esn_asn_nbr = "";
			String trans_type = "";
			String esn_status = "";
			String nbr_pkgs = "";
			String ua_nbr_pkgs = "";
			String balance = "";
			String bk_ref_nbr = "";
			String edo_asn_nbr = "";
			String mvt = "";

			while (rs.next()) {
				esn_asn_nbr = CommonUtility.deNull(rs.getString(1));
				if (esn_asn_nbr.length() == 7)
					esn_asn_nbr = "0" + esn_asn_nbr;
				if (esn_asn_nbr.length() == 6)
					esn_asn_nbr = "00" + esn_asn_nbr;
				trans_type = CommonUtility.deNull(rs.getString(2));
				esn_status = CommonUtility.deNull(rs.getString(3));
				bk_ref_nbr = CommonUtility.deNull(rs.getString(4));
				nbr_pkgs = CommonUtility.deNull(rs.getString(5));
				ua_nbr_pkgs = CommonUtility.deNull(rs.getString(6));
				balance = CommonUtility.deNull(rs.getString(7));
				edo_asn_nbr = CommonUtility.deNull(rs.getString(8));
				mvt = CommonUtility.deNull(rs.getString(9));
				UaEsnListValueObject esnObj = new UaEsnListValueObject();
				esnObj.setEsn_asn_nbr(esn_asn_nbr);
				esnObj.setTrans_type(trans_type);
				esnObj.setEsn_status(esn_status);
				esnObj.setBk_ref_nbr(bk_ref_nbr);
				esnObj.setNbr_pkgs(nbr_pkgs);
				esnObj.setUa_nbr_pkgs(ua_nbr_pkgs);
				esnObj.setBalance(balance);
				esnObj.setEdo_asn_nbr(edo_asn_nbr);
				esnObj.setMvt(mvt);
				esnObj.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				esnObj.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				esnObj.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				esnObj.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				esnList.add(esnObj);
			}
			log.info("END: *** getEsnList Result *****" + esnList.toString());
			
		} catch (NullPointerException ne) {
			log.info("Exception getEsnList : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEsnList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEsnList  DAO  END");
		}

		return esnList;
	}
	
	@Override
	public void updFtrans(String esnasnnbr, String transtype, String ftransdate) throws BusinessException {
		String sql = "";
		int count = 0;
		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String sql4 = "";
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: updFtrans  DAO  Start Obj " + " esnasnnbr:" + CommonUtility.deNull(esnasnnbr) + " transtype:" + CommonUtility.deNull(transtype)
					+ " ftransdate:" + CommonUtility.deNull(ftransdate));

			sql1 = "update ESN_details set FIRST_TRANS_DTTM =to_date(:ftransdate,'dd/mm/yyyy hh24:mi') WHERE ESN_ASN_NBR=:esnasnnbr";
			sql2 = "update tesn_psa_jp set FIRST_TRANS_DTTM =to_date(:ftransdate,'dd/mm/yyyy hh24:mi') WHERE ESN_ASN_NBR=:esnasnnbr";
			sql3 = "update tesn_jp_jp set FIRST_TRANS_DTTM =to_date(:ftransdate,'dd/mm/yyyy hh24:mi') WHERE ESN_ASN_NBR=:esnasnnbr";
			sql4 = "update ss_details set FIRST_TRANS_DTTM =to_date(:ftransdate,'dd/mm/yyyy hh24:mi') WHERE ESN_ASN_NBR=:esnasnnbr";

			if (transtype != null && !transtype.equals("") && transtype.equals("E")) {
				sql = sql1;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("C")) {
				sql = sql2;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("A")) {
				sql = sql3;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("S")) {
				sql = sql4;
			}

			log.info(" *** updFtrans SQL *****" + sql);

			paramMap.put("ftransdate", ftransdate);
			paramMap.put("esnasnnbr", esnasnnbr);
			log.info(" paramMap: " + paramMap);
			count = namedParameterJdbcTemplate.update(sql, paramMap);
			log.info("updFTrans count: " + count);
			if (count == 0) {
				log.info("Writing from UAEJB.updFtrans");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			log.info("END: *** updFtrans Result *****");

		} catch (NullPointerException ne) {
			log.info("Exception updFtrans : ", ne);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception updFtrans : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception updFtrans : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updFtrans  DAO  END");
		}

	}

	//ejb.sessionBeans.gbms.containerised.ua
	@Override
	public List<UaEsnDetValueObject> getUAViewPrint(String UANbr, String esnasnnbr, String transtype)
			throws BusinessException {
		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String sql = "";
		String uasql = "";
		String uavehsql = "";
		String sql4 = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		List<UaEsnDetValueObject> esnList = new ArrayList<UaEsnDetValueObject>();
		SqlRowSet rs = null;
		try {
			log.info("START: getUAViewPrint  DAO  Start Obj " + " UANbr:" + CommonUtility.deNull(UANbr) + " esnasnnbr:" + CommonUtility.deNull(esnasnnbr)
					+ " transtype:" + CommonUtility.deNull(transtype));

			sb.append("SELECT to_char(atb_dttm,'dd/mm/yyyy hh24:mi') as atb,  vsl_nm, vessel_call.out_voy_nbr, ");
			sb.append("esn_details.esn_wt as dec_wt,esn_details.esn_vol as dec_vol, ");
			sb.append("esn_details.nbr_pkgs as dec_qty, bk_details.CNTR_TYPE as cntr_type, ");
			sb.append("bk_details.CNTR_SIZE as cntr_size, markings , ");
			sb.append("esn_details.crg_des ,cntr_nbr as cont_no, ");
			sb.append("esn.esn_asn_nbr  as esn_asn ,esn.bk_ref_nbr,esn_details.nbr_pkgs-esn_details.ua_nbr_pkgs, ");
			sb.append("to_char(GB_COD_DTTM,'dd/mm/yyyy hh24:mi') as cod,esn_details.acct_nbr,");
			sb.append(
					"to_char(ETB_DTTM,'dd/mm/yyyy hh24:mi') as etb,to_char(VSL_BERTH_DTTM,'dd/mm/yyyy hh24:mi') as btr ");
			sb.append("from  esn , vessel_call,berthing , esn_details , bk_details , esn_markings , esn_cntr ");
			sb.append("where esn.esn_asn_nbr = esn_details.esn_asn_nbr ");
			sb.append("and esn.esn_asn_nbr = esn_details.esn_asn_nbr ");
			sb.append("and esn.out_voy_var_nbr = vessel_call.vv_cd ");
			sb.append("and esn.esn_asn_nbr = esn_markings.esn_asn_nbr ");
			sb.append("and esn.esn_asn_nbr = esn_cntr.esn_asn_nbr(+) ");
			sb.append("and vessel_call.vv_cd = berthing.vv_cd and  ");
			sb.append("esn.bk_ref_nbr = bk_details.bk_ref_nbr and shift_ind=1 and esn.TRANS_CRG<>'Y' AND ");
			sb.append("esn.esn_asn_nbr =:esnasnnbr");
			sql1 = sb.toString();

			sb = new StringBuffer();
			sb.append("SELECT to_char(atb_dttm,'dd/mm/yyyy hh24:mi') as atb, vsl_nm, vessel_call.out_voy_nbr, ");
			sb.append("tesn_psa_jp.gross_wt as dec_wt,tesn_psa_jp.gross_vol as dec_vol, ");
			sb.append("tesn_psa_jp.nbr_pkgs as dec_qty, bk_details.CNTR_TYPE as cntr_type, ");
			sb.append("bk_details.CNTR_SIZE as cntr_size, markings , ");
			sb.append("tesn_psa_jp.crg_des ,  cntr_nbr as cont_no , ");
			sb.append(
					"esn.esn_asn_nbr  as esn_asn, esn.bk_ref_nbr as bk_ref,tesn_psa_jp.nbr_pkgs-tesn_psa_jp.ua_nbr_pkgs,  ");
			sb.append("to_char(GB_COD_DTTM,'dd/mm/yyyy hh24:mi') as cod,tesn_psa_jp.acct_nbr ,");
			sb.append(
					"to_char(ETB_DTTM,'dd/mm/yyyy hh24:mi') as etb,to_char(VSL_BERTH_DTTM,'dd/mm/yyyy hh24:mi') as btr ");
			sb.append("from  esn , vessel_call,berthing , tesn_psa_jp , bk_details , ");
			sb.append("esn_markings , esn_cntr where  esn.esn_asn_nbr = tesn_psa_jp.esn_asn_nbr ");
			sb.append("and esn.out_voy_var_nbr = vessel_call.vv_cd ");
			sb.append("and esn.esn_asn_nbr = esn_markings.esn_asn_nbr ");
			sb.append("and esn.esn_asn_nbr = esn_cntr.esn_asn_nbr(+) ");
			sb.append("and vessel_call.vv_cd = berthing.vv_cd and  ");
			sb.append("esn.bk_ref_nbr = bk_details.bk_ref_nbr ");
			sb.append("and esn.trans_type='C' and shift_ind=1 and esn.TRANS_CRG<>'Y' AND ");
			sb.append("esn.esn_asn_nbr =:esnasnnbr");
			sql2 = sb.toString();

			sb = new StringBuffer();
			sb.append("select to_char(atb_dttm,'dd/mm/yyyy hh24:mi') as atb , vsl_nm, vessel_call.out_voy_nbr, ");
			sb.append("tesn_jp_jp.nom_wt as dec_wt,tesn_jp_jp.nom_vol as dec_vol, ");
			sb.append("tesn_jp_jp.nbr_pkgs as dec_qty, manifest_details.CNTR_TYPE as cntr_type, ");
			sb.append("manifest_details.CNTR_SIZE as cntr_size, mft_markings , ");
			sb.append("manifest_details.crg_des , bl_cntr_details.cntr_nbr as cont_no , ");
			sb.append(
					"esn.esn_asn_nbr  as esn_asn, esn.bk_ref_nbr as bk_ref,tesn_jp_jp.nbr_pkgs-tesn_jp_jp.ua_nbr_pkgs, ");
			sb.append("to_char(GB_COD_DTTM,'dd/mm/yyyy hh24:mi') as cod,tesn_jp_jp.acct_nbr ,");
			sb.append(
					"to_char(ETB_DTTM,'dd/mm/yyyy hh24:mi') as etb,to_char(VSL_BERTH_DTTM,'dd/mm/yyyy hh24:mi') as btr ");
			sb.append("from  esn , vessel_call,berthing , tesn_jp_jp , gb_edo , ");
			sb.append("manifest_details ,mft_markings ,bl_cntr_details ");
			sb.append("where  esn.esn_asn_nbr = tesn_jp_jp.esn_asn_nbr ");
			sb.append("and esn.out_voy_var_nbr = vessel_call.vv_cd ");
			sb.append("and tesn_jp_jp.edo_asn_nbr = gb_edo.edo_asn_nbr ");
			sb.append("and gb_edo.mft_seq_nbr = manifest_details.mft_seq_nbr ");
			sb.append("and manifest_details.MFT_SEQ_NBR = mft_markings.MFT_SQ_NBR ");
			sb.append("and manifest_details.MFT_SEQ_NBR = bl_cntr_details.MFT_SEQ_NBR(+) ");
			sb.append("and vessel_call.vv_cd = berthing.vv_cd and");
			sb.append(" esn.trans_type='A' and shift_ind=1 and esn.TRANS_CRG<>'Y' AND ");
			sb.append("esn.esn_asn_nbr =:esnasnnbr");
			sql3 = sb.toString();

			sb = new StringBuffer();
			sb.append("SELECT to_char(atb_dttm,'dd/mm/yyyy hh24:mi') as atb, vsl_nm, vessel_call.out_voy_nbr, ");
			sb.append("SS.SS_WT as dec_wt,SS.SS_VOL as dec_vol, ");
			sb.append("SS.nbr_pkgs as dec_qty, '','',markings ,SS.CRG_DES ,  '',   ");
			sb.append("esn.esn_asn_nbr  as esn_asn,SS.ss_ref_nbr,SS.nbr_pkgs-SS.ua_nbr_pkgs,  ");
			sb.append("to_char(GB_COD_DTTM,'dd/mm/yyyy hh24:mi') as cod,SS.acct_nbr ,");
			sb.append(
					"to_char(ETB_DTTM,'dd/mm/yyyy hh24:mi') as etb,to_char(VSL_BERTH_DTTM,'dd/mm/yyyy hh24:mi') as btr ");
			sb.append("from  esn , vessel_call,berthing , ss_details SS, ");
			sb.append("esn_markings  where  esn.esn_asn_nbr = SS.esn_asn_nbr ");
			sb.append("and esn.out_voy_var_nbr = vessel_call.vv_cd ");
			sb.append("and esn.esn_asn_nbr = esn_markings.esn_asn_nbr ");
			sb.append("and vessel_call.vv_cd = berthing.vv_cd ");
			sb.append("and esn.trans_type='S' and shift_ind=1 and esn.TRANS_CRG<>'Y' AND ");
			sb.append("esn.esn_asn_nbr =:esnasnnbr");
			sql4 = sb.toString();

			sb = new StringBuffer();
			sb.append("SELECT TO_CHAR(TRANS_DTTM,'DD/MM/YYYY HH24:MI')AS T_DTTM,NBR_PKGS,DP_IC_NBR,DP_IC_TYPE,DP_NM,");
			sb.append(
					"BILLABLE_TON,CNTR_NBR,TO_CHAR(UNSTUFF_DTTM,'DD/MM/YYYY HH24:MI')AS UNSTUFF_DT FROM UA_DETAILS WHERE UA_NBR=:UANbr");
			uasql = sb.toString();

			log.info("uasql == " + uasql);
			uavehsql = "SELECT * FROM UA_VEH WHERE UA_NBR=:UANbr ";

			if (transtype != null && !transtype.equals("") && transtype.equals("E")) {
				sql = sql1;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("C")) {
				sql = sql2;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("A")) {
				sql = sql3;
			} else if (transtype != null && !transtype.equals("") && transtype.equals("S")) {
				sql = sql4;
			}

			paramMap.put("esnasnnbr", esnasnnbr);
			log.info(" *** getUAViewPrint SQL 1 *****" + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			UaEsnDetValueObject esnObj = new UaEsnDetValueObject();

			while (rs.next()) {
				esnObj.setAtb(CommonUtility.deNull(rs.getString(1)));
				esnObj.setVessel_name(CommonUtility.deNull(rs.getString(2)));
				esnObj.setOut_voy_nbr(CommonUtility.deNull(rs.getString(3)));
				esnObj.setWeight(CommonUtility.deNull(rs.getString(4)));
				esnObj.setVolume(CommonUtility.deNull(rs.getString(5)));
				esnObj.setDecl_pkg(CommonUtility.deNull(rs.getString(6)));
				esnObj.setConttype(CommonUtility.deNull(rs.getString(7)));
				esnObj.setContsize(CommonUtility.deNull(rs.getString(8)));
				esnObj.setCargo_markings(CommonUtility.deNull(rs.getString(9)));
				esnObj.setCargo_desc(CommonUtility.deNull(rs.getString(10)));
				esnObj.setContno(CommonUtility.deNull(rs.getString(11)));
				esnObj.setEsn_asn_nbr(CommonUtility.deNull(rs.getString(12)));
				esnObj.setBk_ref_nbr(CommonUtility.deNull(rs.getString(13)));
				esnObj.setBal_pkg(CommonUtility.deNull(rs.getString(14)));
				esnObj.setCod(CommonUtility.deNull(rs.getString(15)));
				esnObj.setAct_no(CommonUtility.deNull(rs.getString(16)));
				esnObj.setTrans_type(transtype);
				esnObj.setEtb(CommonUtility.deNull(rs.getString("etb")));
				esnObj.setBtr(CommonUtility.deNull(rs.getString("btr")));
			}
			paramMap.put("UANbr", UANbr);
			log.info(" *** getUAViewPrint SQL 2 *****" + uasql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(uasql, paramMap);
			while (rs.next()) {
				esnObj.setDate_time(CommonUtility.deNull(rs.getString(1)));
				esnObj.setUanbrpkgs(CommonUtility.deNull(rs.getString(2)));
				esnObj.setNric_no(CommonUtility.deNull(rs.getString(3)));
				esnObj.setIctype(CommonUtility.deNull(rs.getString(4)));
				esnObj.setDpname(CommonUtility.deNull(rs.getString(5)));
				esnObj.setBilltons(CommonUtility.deNull(rs.getString(6)));
				log.info("\nFFFF from EJB " + rs.getString(7) + "\t" + rs.getString(8));
				esnObj.setCntrNbr(CommonUtility.deNull(rs.getString(7)));
				esnObj.setUnStuffDate(CommonUtility.deNull(rs.getString(8)));
			}
			log.info(" *** getUAViewPrint SQL 3 *****" + uavehsql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(uavehsql, paramMap);
			int vehsqno = 0;
			while (rs.next()) {
				vehsqno = rs.getInt("UA_VEH_SEQ");
				if (vehsqno == 1)
					esnObj.setVeh1(CommonUtility.deNull(rs.getString("VEH_NO")));
				if (vehsqno == 2)
					esnObj.setVeh2(CommonUtility.deNull(rs.getString("VEH_NO")));
				if (vehsqno == 3)
					esnObj.setVeh3(CommonUtility.deNull(rs.getString("VEH_NO")));
				if (vehsqno == 4)
					esnObj.setVeh4(CommonUtility.deNull(rs.getString("VEH_NO")));
				if (vehsqno == 5)
					esnObj.setVeh5(CommonUtility.deNull(rs.getString("VEH_NO")));
			}
			esnList.add(esnObj);
			log.info("END: *** getUAViewPrint Result *****" + esnList.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getUAViewPrint : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getUAViewPrint : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getUAViewPrint  DAO  END");
		}
		return esnList;
	}

	@Override
	public void purgetemptableUA(String uanbr) throws BusinessException {
		String sql = "";
		int count = 0;
		String sql1 = "";
		int count1 = 0;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: purgetemptableUA  DAO  Start Obj " + " uanbr:" + CommonUtility.deNull(uanbr));

			sql = "delete from webdnuatemp where TransRefno =:uanbr ";
			sql1 = "delete from sst_bill where print_ind = 'WEB' and dn_ua_nbr =:uanbr ";

			log.info(" *** purgetemptableUA SQL 1 *****" + sql);
			paramMap.put("uanbr", uanbr);
			log.info(" paramMap: " + paramMap);
			count = namedParameterJdbcTemplate.update(sql, paramMap);
			log.info("purgetemptableUA count: " + count);

			log.info(" *** purgetemptableUA SQL 2 *****" + sql1);
			log.info(" paramMap: " + paramMap);
			count1 = namedParameterJdbcTemplate.update(sql1, paramMap);
			log.info("purgetemptableUA count1: " + count1);

			log.info("purge ---wednuatemp-from bean-- > " + count);
			log.info("purge ---sst_bill-from bean-- > " + count1);
			log.info("END: *** purgetemptableUA Result *****");
		} catch (NullPointerException ne) {
			log.info("Exception purgetemptableUA : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception purgetemptableUA : ", e);
		} finally {
			log.info("END: purgetemptableUA  DAO  END");
		}
	}

	@Override
	public String insertTempUAPrintOut(String UANbr, String esnasnnbr, String transtype) throws BusinessException {
		String sql = "";
		int countua = 0;
		List<UaEsnDetValueObject> temptablevect = new ArrayList<UaEsnDetValueObject>();
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
		String balpkgs = "";
		// String act_nbr = "";
		String date_time = "";
		String UA_Nbr_Pkgs = "";
		String nric_no = "";
		String ictype = "";
		// String dpname = "";
		String veh1 = "";
		String veh2 = "";
		String veh3 = "";
		String veh4 = "";
		String veh5 = "";
		String ttype = "";
		String dateval = "";
		String etb = "";
		String btr = "";
		String strCntrNum = "";
		String strUnStuffDt = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: insertTempUAPrintOut  DAO  Start Obj " + " UANbr:" + CommonUtility.deNull(UANbr) + " esnasnnbr:" + CommonUtility.deNull(esnasnnbr)
					+ " transtype:" + CommonUtility.deNull(transtype));

			temptablevect = getUAViewPrint(UANbr, esnasnnbr, transtype);
			log.info("temptablevect == " + temptablevect.size());
			for (int i = 0; i < temptablevect.size(); i++) {
				UaEsnDetValueObject esnObj = new UaEsnDetValueObject();
				esnObj = (UaEsnDetValueObject) temptablevect.get(i);
				esnno = esnObj.getEsn_asn_nbr();
				vslnm = esnObj.getVessel_name();
				outvoy = esnObj.getOut_voy_nbr();
				contno = esnObj.getContno();
				conttype = esnObj.getConttype();
				contsize = esnObj.getContsize();
				bkref = esnObj.getBk_ref_nbr();
				dcpkgs = esnObj.getDecl_pkg();
				wt = esnObj.getWeight();
				vol = esnObj.getVolume();
				crgdesc = esnObj.getCargo_desc();
				markings = esnObj.getCargo_markings();
				balpkgs = esnObj.getBal_pkg();
				// cod = esnObj.getCod();
				// act_nbr = esnObj.getAct_no();
				date_time = esnObj.getDate_time();
				UA_Nbr_Pkgs = esnObj.getUanbrpkgs();
				nric_no = esnObj.getNric_no();
				ictype = esnObj.getIctype();
				// dpname = esnObj.getDpname();
				veh1 = esnObj.getVeh1();
				veh2 = esnObj.getVeh2();
				veh3 = esnObj.getVeh3();
				veh4 = esnObj.getVeh4();
				veh5 = esnObj.getVeh5();
				ttype = esnObj.getTrans_type();
				atb = esnObj.getAtb();
				etb = esnObj.getEtb();
				btr = esnObj.getBtr();
				strCntrNum = esnObj.getCntrNbr();
				strUnStuffDt = esnObj.getUnStuffDate();
				// log.info("\nfrom Bean strCntrNum == "+strCntrNum+"\tstrUnStuffDt ==
				// "+strUnStuffDt);
				if (atb != null && !atb.equals(""))
					dateval = atb;
				else if (etb != null && !etb.equals(""))
					dateval = etb;
				else if (btr != null && !btr.equals(""))
					dateval = btr;

				if (ttype.equals("E") || ttype.equals("S"))
					ttype = "L";
				else
					ttype = "T";

				sb.append("Insert into webdnuatemp(DateTime,TransRefno,ATB,COD,Vslnm,voyno,contno,");
				sb.append("transtype,contsize,conttype,asnno,crgref,wt,vol,declqty,transqty,");
				sb.append("balqty,nricpassportno,marking,crg_desc,veh1,veh2,veh3,veh4,veh5,cntr_nbr,stuff_dttm) ");
				sb.append(
						"values(to_date(:date_time,'DD/MM/YYYY HH24:MI'),:UANbr,to_date(:dateval,'DD/MM/YYYY HH24:MI'),");
				sb.append("'',:vslnm,:outvoy,:contno,:ttype,:contsize, :conttype, :esnno, :bkref, :wt, :vol,");
				sb.append(":dcpkgs,:UA_Nbr_Pkgs,:balpkgs,:nric_no,:markings,:crgdesc,:veh1,");
				sb.append(":veh2,:veh3,:veh4,:veh5,:strCntrNum,to_date(:strUnStuffDt,'DD/MM/YYYY HH24:MI'))");
				sql = sb.toString();
				log.info("Insert Query  sql" + sql);

				log.info(" *** insertTempUAPrintOut SQL *****" + sql);

				paramMap.put("date_time", date_time);
				paramMap.put("UANbr", UANbr);
				paramMap.put("dateval", dateval);
				paramMap.put("vslnm", GbmsCommonUtility.addApostr(vslnm));
				paramMap.put("outvoy", outvoy);
				paramMap.put("contno", contno);
				paramMap.put("ttype", GbmsCommonUtility.addApostr(ttype));
				paramMap.put("contsize", contsize);
				paramMap.put("conttype", conttype);
				paramMap.put("esnno", esnno);
				paramMap.put("bkref", GbmsCommonUtility.addApostr(bkref));
				paramMap.put("wt", wt);
				paramMap.put("vol", vol);
				paramMap.put("dcpkgs", dcpkgs);
				paramMap.put("UA_Nbr_Pkgs", UA_Nbr_Pkgs);
				paramMap.put("balpkgs", balpkgs);
				paramMap.put("ictype", ictype);
				paramMap.put("nric_no", ictype + nric_no);
				paramMap.put("markings", GbmsCommonUtility.addApostr(markings));
				paramMap.put("crgdesc", GbmsCommonUtility.addApostr(crgdesc));
				paramMap.put("veh1", GbmsCommonUtility.addApostr(veh1));
				paramMap.put("veh2", GbmsCommonUtility.addApostr(veh2));
				paramMap.put("veh3", GbmsCommonUtility.addApostr(veh3));
				paramMap.put("veh4", GbmsCommonUtility.addApostr(veh4));
				paramMap.put("veh5", GbmsCommonUtility.addApostr(veh5));
				paramMap.put("strCntrNum", strCntrNum);
				paramMap.put("strUnStuffDt", strUnStuffDt);
				
				log.info(" paramMap: " + paramMap);
				countua = countua + namedParameterJdbcTemplate.update(sql, paramMap);
				log.info("insertTempUAPrintOut countua: " + countua);
			} // end for
			log.info("countuarecs ---------------->" + countua);

			if (countua == 0) {
				log.info("Writing from UAEJB.insertTempUAPrintOut");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}

			log.info("END: *** insertTempUAPrintOut Result *****");
		} catch (BusinessException be) {
			log.info("Exception insertTempUAPrintOut : ", be);
			throw new BusinessException(be.getMessage());
		} catch (NullPointerException ne) {
			log.info("Exception insertTempUAPrintOut : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception insertTempUAPrintOut : ", e);
		} finally {
			log.info("END: insertTempUAPrintOut  DAO  END");
		}
		return "" + countua;
	}

	@Override
	public List<UACntrJasperReport> getUaCntrJasperContent(String uaNbr) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getUaCntrJasperContent  DAO dnNbr:" + CommonUtility.deNull(uaNbr));

			sb.append(" SELECT TO_CHAR(WEBDNUATEMP.DATETIME, 'DD/MM/YYYY HH24:MI') DATETIME, ");
			sb.append(" TO_CHAR(WEBDNUATEMP.ATB, 'DD/MM/YYYY HH24:MI') ATB, ");
			sb.append(" TO_CHAR(WEBDNUATEMP.COD, 'DD/MM/YYYY HH24:MI') COD, WEBDNUATEMP.TRANSREFNO,");
			sb.append(" TO_CHAR(WEBDNUATEMP.STUFF_DTTM, 'DD/MM/YYYY HH24:MI') STUFF_DTTM, ");
			sb.append(" WEBDNUATEMP.VSLNM, WEBDNUATEMP.VOYNO, WEBDNUATEMP.CONTSIZE, ");
			sb.append(" WEBDNUATEMP.CONTTYPE, WEBDNUATEMP.ASNNO, WEBDNUATEMP.CRGREF, WEBDNUATEMP.WT, WEBDNUATEMP.VOL, ");
			sb.append(" WEBDNUATEMP.DECLQTY, WEBDNUATEMP.TRANSQTY, WEBDNUATEMP.BALQTY, WEBDNUATEMP.NRICPASSPORTNO, ");
			sb.append(" WEBDNUATEMP.TRANSTYPE, WEBDNUATEMP.VEH1, WEBDNUATEMP.VEH2, WEBDNUATEMP.VEH3, WEBDNUATEMP.VEH4, ");
			sb.append(" WEBDNUATEMP.VEH5, WEBDNUATEMP.MARKING, WEBDNUATEMP.CRG_DESC, SST_BILL.ACCT_NBR_SER_CHRG,");
			sb.append(" SST_BILL.EDO_ACCT_NBR, SST_BILL.TOTAL_AMT_SER_CHRG, SST_BILL.TOTAL_AMT_WHARF_CHRG, ");
			sb.append(" SST_BILL.TOTAL_AMT_STORE_CHRG, SST_BILL.TOTAL_AMT_SR_CHRG, SST_BILL.TOTAL_AMT_SER_WHARF_CHRG, ");
			sb.append(" SST_BILL.TARRIF_CD_SER_CHRG, SST_BILL.TARRIF_DESC_SER_CHRG, SST_BILL.BILLABLE_TON_SER_CHRG, ");
			sb.append(
					" SST_BILL.UNIT_RATE_SER_CHRG, SST_BILL.TARRIF_CD_WHARF_CHRG, SST_BILL.TARRIF_DESC_WHARF_CHRG, ");
			sb.append(
					" SST_BILL.BILLABLE_TON_WHARF_CHRG, SST_BILL.UNIT_RATE_WHARF_CHRG, SST_BILL.TARRIF_CD_STORE_CHRG,");
			sb.append(
					" SST_BILL.TARRIF_DESC_STORE_CHRG, SST_BILL.BILLABLE_TON_STORE_CHRG, SST_BILL.UNIT_RATE_STORE_CHRG, ");
			sb.append(
					" SST_BILL.TARRIF_CD_SR_CHRG, SST_BILL.TARRIF_DESC_SR_CHRG, SST_BILL.BILLABLE_TON_SR_CHRG,");
			sb.append("  SST_BILL.UNIT_RATE_SR_CHRG, SST_BILL.UNIT_RATE_SER_WHARF_CHRG, SST_BILL.BILLABLE_TON_SER_WHARF_CHRG, ");
			sb.append(
					" SST_BILL.TARRIF_DESC_SER_WHARF_CHRG, SST_BILL.TARRIF_CD_SER_WHARF_CHRG, SST_BILL.TIME_UNIT_SER,");
			sb.append("  SST_BILL.TIME_UNIT_WHF, SST_BILL.TIME_UNIT_SR, SST_BILL.TIME_UNIT_SER_WHF, SST_BILL.TIME_UNIT_STORE, ");
			sb.append("  WEBDNUATEMP.CNTR_NBR FROM GBMS.WEBDNUATEMP WEBDNUATEMP, GBMS.SST_BILL SST_BILL WHERE ");
			sb.append(
					" (WEBDNUATEMP.TRANSREFNO=SST_BILL.DN_UA_NBR) AND WEBDNUATEMP.TRANSREFNO=:uaNbr ");
			sb.append(" ORDER BY WEBDNUATEMP.TRANSREFNO	");

			paramMap.put("uaNbr", uaNbr);

			log.info(" ***listRecords SQL *****" + sb.toString() + " paramMap " + paramMap);
			return (List<UACntrJasperReport>) namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<UACntrJasperReport>(UACntrJasperReport.class));
		} catch (Exception e) {
			log.info("Exception getUaCntrJasperContent : ", e);
			return null;
		} finally {
			log.info("END: getUaCntrJasperContent  DAO ");
		}

	}

	// ejb.sessionBeans.reports-->ReportPrintingBeanEJB -->printReport
	// remove printing method, only get filename.
	@Override
	public String getPdfFileName(ReportValueObject rvo, String dnNbr) throws BusinessException {

		String pdfDir = printingBeanPdf + '/';

		String printer = ConstantUtil.ReportPrintingBean_printer;

		if (DEBUG)
			log.info(rvo.toString());

		try {
			log.info("START: getPdfFileName  DAO  Start Obj rvo:" + CommonUtility.deNull(rvo.toString()) + " dnNbr: " + CommonUtility.deNull(dnNbr));
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
			// String psFile = psDir + s2;

			// TuanTA10 start at 20/08/2007
			boolean isPDF = false;
			// String htmlFile = htmlDir + s3;
			// String xlsFile = xlsDir + s4;

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
			log.info("Exception printReport : ", e);

		} catch (Exception ex) {
			log.info("Exception printReport : ", ex);

		} finally {
			log.info("END: printReport  DAO  END");
		}
		return rvo.getReportFileName();

	}

	private String getPdfName(String generatedReportFileName, ReportValueObject rvo) {
		String pdfName = generatedReportFileName;
		try {
			log.info("START: getPdfName  DAO  Start Obj generatedReportFileName:" + CommonUtility.deNull(generatedReportFileName) + ",rvo:"
					+ CommonUtility.deNull(rvo.toString()));
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
		} catch (Exception e) {
			log.info("Exception getPdfName : ", e);
		} finally {
			log.info("END: getPdfName  DAO  END");
		}
		return pdfName;
	}

	private String generateFileName(ReportValueObject rvo) {
		String retVal = null;
		try {
			log.info("START: generateFileName  DAO  Start Obj rvo:" + CommonUtility.deNull(rvo.toString()));
			Random rnd = new Random();
			String fileFormat = ConstantUtil.ReportPrintingBean_fileFormat;
			String dateFormat = ConstantUtil.ReportPrintingBean_dateFormat;
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

			Date date = new Date(System.currentTimeMillis());
			String sDate = sdf.format(date);
			String sRandom = "" + rnd.nextInt(100000);
			String sReport = rvo.getReportFileName();
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
		} catch (Exception e) {
			log.info("Exception generateFileName : ", e);
		} finally {
			log.info("END: generateFileName  DAO  END");
		}
		return retVal;
	}

	private void createDirectory(String dir) {
		try {
			log.info("START: createDirectory  DAO  Start Obj dir:" + CommonUtility.deNull(dir));
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

	private void savePdfName(String generatedReportFileName, ReportValueObject rvo) throws BusinessException {
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
			log.info("START: savePdfName  DAO  Start Obj generatedReportFileName:" + CommonUtility.deNull(generatedReportFileName) + ",rvo:"
					+ CommonUtility.deNull(rvo.toString()));

			savePdfName(billNbr.split("-")[0], isTaxInvoice, isSupportDocument, isDnCn, generatedReportFileName,
					rvo.getParam("INVOICE_FORMAT"), rvo.getParam("INVOICE_EMAIL"));

		} catch (BusinessException e) {
			log.info("Exception savePdfName : ", e);
			throw new BusinessException(e.getMessage());
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
			log.info("START: savePdfName  DAO  Start Obj billNbr: " + CommonUtility.deNull(billNbr) + " , isTaxInvoice: " + CommonUtility.deNull(String.valueOf(isTaxInvoice))
					+ " , isSupportDocument: " + CommonUtility.deNull(String.valueOf(isSupportDocument)) + " , isDnCn: " + CommonUtility.deNull(String.valueOf(isDnCn))
					+ " , generatedReportFileName: " + CommonUtility.deNull(generatedReportFileName) + " , invoiceFormat: " + CommonUtility.deNull(invoiceFormat)
					+ " , invoiceEmail: " + CommonUtility.deNull(invoiceEmail));

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
			log.info(" *** savePdfName SQL *****" + sql);
			paramMap.put("generatedReportFileName", generatedReportFileName);
			if (isSupportDocument) {
				paramMap.put("billNbr", billNbr);
			} else {
				paramMap.put("invoiceFormat", invoiceFormat);
				paramMap.put("invoiceEmail", invoiceEmail);
				paramMap.put("billNbr", billNbr);
			}
			log.info(" paramMap: " + paramMap);
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
	public String insertTempBill(String uanbr, String tarcdser, String tardescser, double billtonsser, double urateser,
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
			log.info("START: insertTempBill  DAO  Start Obj uanbr: " + CommonUtility.deNull(uanbr) + " , tarcdser: " + CommonUtility.deNull(tarcdser)
				+ " , tardescser: " + CommonUtility.deNull(tardescser) + " , billtonsser: " + CommonUtility.deNull(String.valueOf(billtonsser))
				+ " , urateser: " + CommonUtility.deNull(String.valueOf(urateser)) + " , totchrgamtser: " + CommonUtility.deNull(String.valueOf(totchrgamtser))
				+ " , actnbrser: " + CommonUtility.deNull(actnbrser) + " , tarcdwf: " + CommonUtility.deNull(tarcdwf)
				+ " , tardescwf: " + CommonUtility.deNull(tardescwf) + " , billtonswf: " + CommonUtility.deNull(String.valueOf(billtonswf))
				+ " , uratewf: " + CommonUtility.deNull(String.valueOf(uratewf)) + " , totchrgamtwf: " + CommonUtility.deNull(String.valueOf(totchrgamtwf))
				+ " , actnbrwf: " + CommonUtility.deNull(actnbrwf) + " , tarcdsr: " + CommonUtility.deNull(tarcdsr)
				+ " , tardescsr: " + CommonUtility.deNull(tardescsr) + " , billtonssr: " + CommonUtility.deNull(String.valueOf(billtonssr))
				+ " , uratesr: " + CommonUtility.deNull(String.valueOf(uratesr)) + " , totchrgamtsr: " + CommonUtility.deNull(String.valueOf(totchrgamtsr))
				+ " , actnbrsr: " + CommonUtility.deNull(actnbrsr) + " , UserID: " + CommonUtility.deNull(UserID)
				+ " , edo_act_nbr: " + CommonUtility.deNull(edo_act_nbr) + " , tarcdsr1: " + CommonUtility.deNull(tarcdsr1)
				+ " , tardescsr1: " + CommonUtility.deNull(tardescsr1) + " , billtonssr1: " + CommonUtility.deNull(String.valueOf(billtonssr1))
				+ " , uratesr1: " + CommonUtility.deNull(String.valueOf(uratesr1)) + " , totchrgamtsr1: " + CommonUtility.deNull(String.valueOf(totchrgamtsr1))
				+ " , actnbrsr1: " + CommonUtility.deNull(actnbrsr1) + " , tarcdsr2: " + CommonUtility.deNull(tarcdsr2)
				+ " , tardescsr2: " + CommonUtility.deNull(tardescsr2) + " , billtonssr2: " + CommonUtility.deNull(String.valueOf(billtonssr2))
				+ " , uratesr2: " + CommonUtility.deNull(String.valueOf(uratesr2)) + " , totchrgamtsr2: " + CommonUtility.deNull(String.valueOf(totchrgamtsr2))
				+ " , actnbrsr2: " + CommonUtility.deNull(actnbrsr2) + " , tunitser: " + CommonUtility.deNull(String.valueOf(tunitser))
				+ " , tunitwhf: " + CommonUtility.deNull(String.valueOf(tunitwhf)) + " , tunitsr: " + CommonUtility.deNull(String.valueOf(tunitsr))
				+ " , tunitstore: " + CommonUtility.deNull(String.valueOf(tunitstore)) + " , tunitserwhf: " + CommonUtility.deNull(String.valueOf(tunitserwhf)));

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
			sb.append("values(:uanbr, :tarcdser, :tardescser,:billtonsser,");
			sb.append(":urateser, :totchrgamtser, :tarcdwf, :tardescwf,");
			sb.append(":billtonswf,:uratewf,:totchrgamtwf,:tarcdsr,");
			sb.append(":tardescsr ,:billtonssr,:uratesr,:totchrgamtsr,");
			sb.append(":actnbrser,:actnbrwf,:actnbrsr,'WEB',:UserID,sysdate,:edo_act_nbr,:tarcdsr1,");
			sb.append(":tardescsr1,:billtonssr1 ,:uratesr1,:totchrgamtsr1,:actnbrsr1,:tarcdsr2,");
			sb.append(":tardescsr2 ,:billtonssr2,:uratesr2 ,:totchrgamtsr2,:actnbrsr2,");
			sb.append(":tunitser,:tunitwhf ,:tunitsr,:tunitstore,:tunitserwhf)");

			sql = sb.toString();

			log.info(" *** insertTempBill SQL *****" + sql);

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
			log.info(" paramMap: " + paramMap);
			countua = namedParameterJdbcTemplate.update(sql, paramMap);
			log.info("insertTempBill countua: " + countua);

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

	// ejb.sessionBeans.gbms.ops.dnua.ua --> UAEJB --> checkTransType()
	@Override
	public String checkTransType(String esnNbr) throws BusinessException {
		String transType = "";
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: checkTransType  DAO  Start Obj esnNbr: " + CommonUtility.deNull(esnNbr));
			sql = "SELECT TRANS_TYPE FROM ESN WHERE ESN_ASN_NBR = :esnNbr";
			paramMap.put("esnNbr", esnNbr);
			log.info(" checkTransType SQL: " + sql.toString() + "paramMap" + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				transType = rs.getString("TRANS_TYPE");
			}
		} catch (NullPointerException e) {
			log.info("Exception checkTransType : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkTransType : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("[**** END checkTransType() ****** ");
		}
		return transType;
	}

	@Override
	public List<UaEsnListValueObject> getTransferredCargo(String esn) throws BusinessException {
		List<UaEsnListValueObject> transferredCargo = new ArrayList<UaEsnListValueObject>();
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		
		sb.append(" select esn_asn_nbr from esn where bk_ref_nbr in ( select old_bk_ref from esn a, bk_details b ");
		sb.append(" where a.bk_ref_nbr = b.bk_ref_nbr  and a.bk_ref_nbr in ( ");
		sb.append(" select bk_ref_nbr from esn where esn_asn_nbr = :esn");
		sb.append(" and trans_crg = 'Y' and esn_status = 'A'))");
		sql = sb.toString();
		try {
			log.info("START: getTransferredCargo  DAO  Start Obj " + " esn:" + CommonUtility.deNull(esn));

			log.info(" *** getTransferredCargo SQL *****" + sql);
			
			paramMap.put("esn", esn);
			
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				UaEsnListValueObject esnObj = new UaEsnListValueObject();
				esnObj.setEsn_asn_nbr(CommonUtility.deNull(rs.getString(1)));
				transferredCargo.add(esnObj);
			}

			log.info("END: *** getTransferredCargo Result *****" + transferredCargo.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getTransferredCargo : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTransferredCargo : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getTransferredCargo  DAO  END");
		}

		return transferredCargo;
	}

	@Override
	public boolean checkESNCntr(String esnasn) throws BusinessException {

		String sql = "";
		StringBuffer sb = new StringBuffer();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			
			sb.append("select a.cntr_seq_nbr, b.cntr_nbr from esn_mot_cntr a, cntr b  where a.esn_asn_nbr = :esnasn ");
			sb.append(" and a.cntr_seq_nbr = b.cntr_seq_nbr ");
			sb.append(" and b.txn_status <> 'D' and b.txn_status <> 'I'");

			sql = sb.toString();
			log.info("START: checkESNCntr  DAO  Start Obj esnasn: " + CommonUtility.deNull(esnasn));
			log.info(" *** checkESNCntr SQL *****" + sql);
			paramMap.put("esnasn", esnasn);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {

				return true;
			}
			log.info("END: *** checkESNCntr Result *****");
			return false;

		} catch (NullPointerException ne) {
			log.info("Exception checkESNCntr : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkESNCntr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkESNCntr  DAO  END");
		}

	}

	@Override
	public boolean isAsnShut(String esnasnnbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		String sqlvoy = "";

		// String sql = "";
		boolean bvslind = false;
		// String varno = "";

		// Amended by Dongsheng on 29/3/2012 for SL-CIM-20120327-01. The way below to
		// check if GB_CLOSE_SHP_IND ='Y' will always return false!!!
		// sqlvoy = "SELECT OUT_VOY_VAR_NBR FROM ESN WHERE ESN_ASN_NBR ='" + esnasnnbr +
		// "'";
		// sqlvoy = "SELECT GB_CLOSE_SHP_IND FROM VESSEL_CALL WHERE GB_CLOSE_SHP_IND='Y'
		// AND VV_CD " +
		// "in (SELECT OUT_VOY_VAR_NBR FROM ESN WHERE ESN_ASN_NBR='" + esnasnnbr + "')";

		// Amended as user requirement is changed to:
		// 1. UA cannot be created if close shipment is done.
		// 2. UA can be created if shipment is re-opened and new booking reference no
		// cum ESN are created.
		// sqlvoy = "select (select count(*) from ESN e, action_Log al where
		// e.ESN_ASN_NBR='" + esnasnnbr + "' " +
		// " and e.OUT_VOY_VAR_NBR = al.REF_NBR and al.action_cd='SHC') + " +
		// " (SELECT count(*) FROM VESSEL_CALL WHERE GB_CLOSE_SHP_IND='Y' AND VV_CD in "
		// +
		// " (SELECT OUT_VOY_VAR_NBR FROM ESN WHERE ESN_ASN_NBR='" + esnasnnbr + "')) as
		// cnt from dual ";

		try {
			log.info("START: isAsnShut  DAO  Start Obj " + " esnasnnbr:" + CommonUtility.deNull(esnasnnbr));

			sb.append(" select bk_details.bk_Ref_nbr,bk_details.shutout_qty  From esn, bk_details  where ");
			sb.append(
					" shutout_qty > 0 and bk_details.bk_ref_nbr = esn.bk_ref_nbr and esn.esn_status = 'A' and bk_details.bk_status = 'A' and ESN_ASN_NBR=:esnasnnbr ");
			sqlvoy = sb.toString();

			log.info(" *** isAsnShut SQL *****" + sqlvoy);

			paramMap.put("esnasnnbr", esnasnnbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlvoy, paramMap);

			if (rs.next()) {
				bvslind = true;
			}

			log.info("END: *** isAsnShut Result *****" + bvslind);
		} catch (NullPointerException ne) {
			log.info("Exception isAsnShut : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isAsnShut : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isAsnShut  DAO  END");
		}

		return bvslind;
	}

	@Override
	public String createUA(String esnasnnbr, String transtype, String Esn_Nbr_Pkgs, String NomWt, String NomVol,
			String date_time, String UA_Nbr_Pkgs, String nric_no, String ictype, String dpname, String veh1,
			String userid) throws BusinessException {
		String sql = "";
		String UANbr = "";
		String ftrans = "";
		String sqlua = "";
		String esnupdsql = "";
		String uanbrtrans = "";
		String sqlveh1 = "";
		String sqlft = "";
		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String sql4 = "";
		String sql1_trans = "";
		String sql2_trans = "";
		String sql3_trans = "";
		String sql4_trans = "";
		String sqlveh1_trans = "";
		// ++ VietND02 remover
		/*
		 * String sqlveh2_trans = ""; String sqlveh3_trans = ""; String sqlveh4_trans =
		 * ""; String sqlveh5_trans = "";
		 */
		// -- VietND02
		String sql_trans = "";
		String sqltlog = "";
		String strInsert_trans = "";

		StringTokenizer uatrans = null;

		int countua = 0;
		int count = 0;
		double Bill_ton = 0.0;
		double ua_nom_wt = 0.0;
		double ua_nom_vol = 0.0;

		boolean vslstat = chkVslStat(esnasnnbr);
		if (vslstat) {
			log.info("Writing from UAEJB.createUA");
			log.info("Vessel Status is closed cannot Create UA");
			throw new BusinessException("M20621");
		}
		boolean esnstat = chkESNStatus(esnasnnbr);
		if (esnstat) {
			log.info("Writing from UAEJB.CreateUA");
			log.info("ESN Cancelled cannot create UA");
			throw new BusinessException("M20622");
		}
		boolean balpkgs = chkBalpkgs(esnasnnbr, UA_Nbr_Pkgs, transtype);
		if (balpkgs) {
			log.info("Writing from UAEJB.CreateUA");
			log.info("UANbrPkgs greater than EsnPkgs cannot create UA");
			throw new BusinessException("M20623");
		}

		uanbrtrans = getUANbr(esnasnnbr);
		uatrans = new StringTokenizer(uanbrtrans, "-");
		UANbr = (uatrans.nextToken()).trim();
		ftrans = (uatrans.nextToken()).trim();

		ua_nom_wt = (Integer.parseInt(UA_Nbr_Pkgs) / Integer.parseInt(Esn_Nbr_Pkgs)) * Double.parseDouble(NomWt);
		ua_nom_vol = (Integer.parseInt(UA_Nbr_Pkgs) / Integer.parseInt(Esn_Nbr_Pkgs)) * Double.parseDouble(NomVol);

		if (ftrans.equals("True")) {
			if ((Double.parseDouble(NomWt) / 1000) > Double.parseDouble(NomVol)) {
				Bill_ton = Double.parseDouble(NomWt) / 1000;
			} else {
				Bill_ton = Double.parseDouble(NomVol);
			} // end if nomwt
		} else {
			Bill_ton = 0;
		} // end if ftrans

		int cntua = 0;
		int cnt_trans = 0;
		int stransno = 0;
		int count_trans = 0;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {
			log.info("START: createUA  DAO  Start Obj " + " esnasnnbr:" + CommonUtility.deNull(esnasnnbr) + " transtype:" + CommonUtility.deNull(transtype)
					+ " Esn_Nbr_Pkgs:" + CommonUtility.deNull(Esn_Nbr_Pkgs) + " NomWt:" + CommonUtility.deNull(NomWt) + " NomVol:" + CommonUtility.deNull(NomVol) + " date_time:"
					+ CommonUtility.deNull(date_time) + " UA_Nbr_Pkgs:" + CommonUtility.deNull(UA_Nbr_Pkgs) + " ictype:" + CommonUtility.deNull(ictype)
					+ " dpname:" + CommonUtility.deNull(dpname) + " veh1:" + CommonUtility.deNull(veh1) + " userid:" + CommonUtility.deNull(userid));

			sqlua = "select count(*) from ua_details where ua_status = 'A' and esn_asn_nbr = :esnasnnbr";

			log.info(" *** createUA SQL 1 *****" + sqlua);

			paramMap.put("esnasnnbr", esnasnnbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlua.toString(), paramMap);

			if (rs.next()) {
				cntua = rs.getInt(1);
			}
			if (cntua == 0) {
				sql1 = "update esn_details set FIRST_TRANS_DTTM = to_date(:date_time,'dd/mm/yyyy hh24:mi') where esn_asn_nbr=:esnasnnbr";
				sql2 = "update tesn_psa_jp set FIRST_TRANS_DTTM = to_date(:date_time,'dd/mm/yyyy hh24:mi') where esn_asn_nbr=:esnasnnbr";
				sql3 = "update tesn_jp_jp set FIRST_TRANS_DTTM = to_date(:date_time,'dd/mm/yyyy hh24:mi') where esn_asn_nbr=:esnasnnbr";
				sql4 = "update ss_details set FIRST_TRANS_DTTM = to_date(:date_time,'dd/mm/yyyy hh24:mi') where esn_asn_nbr=:esnasnnbr";
			}

			sql1_trans = "SELECT MAX(TRANS_NBR) FROM ESN_DETAILS_TRANS WHERE ESN_ASN_NBR=:esnasnnbr";
			sql2_trans = "SELECT MAX(TRANS_NBR) FROM TESN_JP_JP_TRANS WHERE ESN_ASN_NBR=:esnasnnbr";
			sql3_trans = "SELECT MAX(TRANS_NBR) FROM TESN_PSA_JP_TRANS WHERE ESN_ASN_NBR=:esnasnnbr";
			sql4_trans = "SELECT MAX(TRANS_NBR) FROM SS_DETAILS_TRANS WHERE ESN_ASN_NBR=:esnasnnbr";

			if (transtype != null && !transtype.equals("") && transtype.equals("E")) {
				esnupdsql = "update esn_details set ua_nbr_pkgs =ua_nbr_pkgs+ :UA_Nbr_Pkgs where esn_asn_nbr =:esnasnnbr";
				sqlft = sql1;
				sqltlog = sql1_trans;
				strInsert_trans = "INSERT INTO ESN_DETAILS_TRANS ";
			}
			if (transtype != null && !transtype.equals("") && transtype.equals("A")) {
				esnupdsql = "update tesn_jp_jp set ua_nbr_pkgs=ua_nbr_pkgs+ :UA_Nbr_Pkgs where esn_asn_nbr =:esnasnnbr";
				sqlft = sql3;
				sqltlog = sql2_trans;
				strInsert_trans = "INSERT INTO TESN_JP_JP_TRANS ";
			}
			if (transtype != null && !transtype.equals("") && transtype.equals("C")) {
				esnupdsql = "update tesn_psa_jp set ua_nbr_pkgs=ua_nbr_pkgs+ :UA_Nbr_Pkgs where esn_asn_nbr =:esnasnnbr";
				sqlft = sql2;
				sqltlog = sql3_trans;
				strInsert_trans = "INSERT INTO TESN_PSA_JP_TRANS ";
			}
			if (transtype != null && !transtype.equals("") && transtype.equals("S")) {
				esnupdsql = "update ss_details set ua_nbr_pkgs=ua_nbr_pkgs+ :UA_Nbr_Pkgs where esn_asn_nbr =:esnasnnbr";
				sqlft = sql4;
				sqltlog = sql4_trans;
				strInsert_trans = "INSERT INTO SS_DETAILS_TRANS ";
			}

			paramMap.put("date_time", date_time);
			paramMap.put("UA_Nbr_Pkgs", UA_Nbr_Pkgs);
			paramMap.put("esnasnnbr", esnasnnbr);
			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003
				log.info(" *** createUA SQL 2 *****" + sqltlog.toString());
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog.toString(), paramMap);
				if (rs.next()) {
					stransno = (rs.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
			}
			strInsert_trans = strInsert_trans + " (TRANS_NBR,ESN_ASN_NBR,";
			strInsert_trans = strInsert_trans + "UA_NBR_PKGS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,REMARKS) ";
			strInsert_trans = strInsert_trans + "VALUES(:stransno,:esnasnnbr,";
			strInsert_trans = strInsert_trans + ":UA_Nbr_Pkgs,:userid ,sysdate,'UA ADD')";

			paramMap.put("stransno", stransno);
			paramMap.put("esnasnnbr", esnasnnbr);
			paramMap.put("userid", userid);
			paramMap.put("UA_Nbr_Pkgs", UA_Nbr_Pkgs);

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				log.info(" *** createUA SQL 3 *****" + strInsert_trans.toString());
				log.info(" paramMap: " + paramMap);
				count_trans = namedParameterJdbcTemplate.update(strInsert_trans.toString(), paramMap);
				log.info("createUA count_trans: " + count_trans);
			}
			if (cntua == 0) {
				log.info(" *** createUA SQL 4 *****" + sqlft.toString());
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(sqlft.toString(), paramMap);

			}
			log.info(" *** createUA SQL 5 *****" + esnupdsql.toString());
			log.info(" paramMap: " + paramMap);
			count = namedParameterJdbcTemplate.update(esnupdsql.toString(), paramMap);
			log.info("createUA count: " + count);
			if (count == 0) {

				log.info("Writing from UAEJB.createUA esnupdsql");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			} else if (count > 0) {

				sqlveh1 = "insert into ua_veh(UA_NBR,UA_VEH_SEQ,VEH_NO) values(:UANbr,1,:veh1)";
				// ++ VietND02
				/*
				 * sqlveh2 = "insert into ua_veh(UA_NBR,UA_VEH_SEQ,VEH_NO) values('" + UANbr +
				 * "',2,'" + veh2 + "')"; sqlveh3 =
				 * "insert into ua_veh(UA_NBR,UA_VEH_SEQ,VEH_NO) values('" + UANbr + "',3,'" +
				 * veh3 + "')"; sqlveh4 =
				 * "insert into ua_veh(UA_NBR,UA_VEH_SEQ,VEH_NO) values('" + UANbr + "',4,'" +
				 * veh4 + "')"; sqlveh5 =
				 * "insert into ua_veh(UA_NBR,UA_VEH_SEQ,VEH_NO) values('" + UANbr + "',5,'" +
				 * veh5 + "')";
				 */
				// -- VietND02

				sqlveh1_trans = "insert into ua_veh_trans(trans_nbr,UA_NBR,UA_VEH_SEQ,VEH_NO) values('0',:UANbr,1,:veh1)";
				// ++ VietND02
				/*
				 * sqlveh2_trans =
				 * "insert into ua_veh_trans(trans_nbr,UA_NBR,UA_VEH_SEQ,VEH_NO) values('0','" +
				 * UANbr + "',2,'" + veh2 + "')"; sqlveh3_trans =
				 * "insert into ua_veh_trans(trans_nbr,UA_NBR,UA_VEH_SEQ,VEH_NO) values('0','" +
				 * UANbr + "',3,'" + veh3 + "')"; sqlveh4_trans =
				 * "insert into ua_veh_trans(trans_nbr,UA_NBR,UA_VEH_SEQ,VEH_NO) values('0','" +
				 * UANbr + "',4,'" + veh4 + "')"; sqlveh5_trans =
				 * "insert into ua_veh_trans(trans_nbr,UA_NBR,UA_VEH_SEQ,VEH_NO) values('0','" +
				 * UANbr + "',5,'" + veh5 + "')";
				 */
				// -- VietND02
				StringBuffer sb = new StringBuffer();

				sb.append("insert into ua_details(TRUCK_NBR,UA_NBR,ESN_ASN_NBR,UA_STATUS,DP_IC_TYPE,DP_NM,DP_IC_NBR, ");
				sb.append(
						"TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,UA_CREATE_LOGIN,UA_CREATE_DTTM,LAST_MODIFY_USER_ID, ");
				sb.append("LAST_MODIFY_DTTM, GATE_OUT_DTTM) VALUES(:veh1,:UANbr,:esnasnnbr,'A',");
				sb.append(":ictype,:dpname,:nric_no,to_date(:date_time,'DD/MM/YYYY HH24:MI'),:UA_Nbr_Pkgs,");
				sb.append(":ua_nom_wt,:ua_nom_vol,:Bill_ton,:userid,sysdate,:userid,sysdate,");
				sb.append(
						"CASE WHEN TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') > SYSDATE-(SELECT VALUE/24 FROM SYSTEM_PARA WHERE PARA_CD='CL_UA') THEN NULL ELSE TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') END)");

				sql = sb.toString();

				paramMap.put("veh1", veh1);
				paramMap.put("UANbr", UANbr);
				paramMap.put("esnasnnbr", esnasnnbr);
				paramMap.put("ictype", ictype);
				paramMap.put("dpname", dpname);
				paramMap.put("nric_no", nric_no);
				paramMap.put("date_time", date_time);
				paramMap.put("UA_Nbr_Pkgs", UA_Nbr_Pkgs);
				paramMap.put("ua_nom_wt", ua_nom_wt);
				paramMap.put("ua_nom_vol", ua_nom_vol);
				paramMap.put("Bill_ton", Bill_ton);
				paramMap.put("userid", userid);
				paramMap.put("date_time", date_time);
				
				log.info(" *** createUA SQL 6 *****" + sql.toString());
				log.info(" paramMap: " + paramMap);
				countua = namedParameterJdbcTemplate.update(sql.toString(), paramMap);
				log.info("createUA countua: " + countua);
				if (logStatusGlobal.equalsIgnoreCase("Y")) {

					sb = new StringBuffer();
					sb.append(
							"insert into ua_details_trans(TRUCK_NBR,Trans_nbr,UA_NBR,ESN_ASN_NBR,UA_STATUS,DP_IC_TYPE,DP_NM,DP_IC_NBR, ");
					sb.append(
							"TRANS_DTTM,NBR_PKGS,NOM_WT,NOM_VOL,BILLABLE_TON,UA_CREATE_LOGIN,UA_CREATE_DTTM,LAST_MODIFY_USER_ID, ");
					sb.append(
							"LAST_MODIFY_DTTM, GATE_OUT_DTTM) VALUES(:veh1,'0',:UANbr,:esnasnnbr,'A',:ictype,:dpname,");
					sb.append(":nric_no,to_date(:date_time,'DD/MM/YYYY HH24:MI'),");
					sb.append(":UA_Nbr_Pkgs,:ua_nom_wt,:ua_nom_vol,:Bill_ton");
					sb.append(",:userid,sysdate,:userid,sysdate,");

					// For back-dated UA of more than x hours, default the truck's gate-out time as
					// the back-dated time. 26/5/2010.
					sb.append(
							"CASE WHEN TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') > SYSDATE-(SELECT VALUE/24 FROM SYSTEM_PARA WHERE PARA_CD='CL_UA') THEN NULL ELSE TO_DATE(:date_time,'DD/MM/YYYY HH24:MI') END)");
					sql_trans = sb.toString();

					paramMap.put("veh1", veh1);
					paramMap.put("UANbr", UANbr);
					paramMap.put("esnasnnbr", esnasnnbr);
					paramMap.put("ictype", ictype);
					paramMap.put("dpname", dpname);
					paramMap.put("nric_no", nric_no);
					paramMap.put("UA_Nbr_Pkgs", UA_Nbr_Pkgs);
					paramMap.put("ua_nom_wt", ua_nom_wt);
					paramMap.put("ua_nom_vol", ua_nom_vol);
					paramMap.put("Bill_ton", Bill_ton);
					paramMap.put("userid", userid);
					paramMap.put("date_time", date_time);
					log.info(" *** createUA SQL 7 *****" + sql_trans.toString());
					log.info(" paramMap: " + paramMap);
					cnt_trans = namedParameterJdbcTemplate.update(sql_trans.toString(), paramMap);
					log.info("createUA cnt_trans: " + cnt_trans);
				}

				paramMap.put("veh1", veh1);
				paramMap.put("UANbr", UANbr);
				if (veh1 != null && !veh1.equals("")) {
					log.info(" *** createUA SQL 8 *****" + sqlveh1.toString());
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(sqlveh1.toString(), paramMap);
					if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 21/01/2003
						log.info(" *** createUA SQL 9 *****" + sqlveh1_trans.toString());
						log.info(" paramMap: " + paramMap);
						namedParameterJdbcTemplate.update(sqlveh1_trans.toString(), paramMap);
					}
				}

			} // end if count

			if (countua == 0) {

				log.info("Writing from UAEJB.createUA");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 21/01/2003
				if (cnt_trans == 0 || count_trans == 0) {

					log.info("Writing from UAEJB.createUA");
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}
			}

			log.info("END: *** createUA Result *****" + UANbr.toString());
		} catch (NullPointerException e) {
			log.info("Exception createUA :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception createUA :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: createUA  DAO  END");
		}
		return UANbr;
	} // end of createUA
	
	/**
	 * Update UA No
	 * 
	 * @param uaNo
	 * @param vehicleNo
	 * @throws BusinessException
	 */
	public void updateVehicleNo(String uaNo, String vehicleNo) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = null;
		try {

			log.info("START: updateVehicleNo  DAO  Start uaNo:" + CommonUtility.deNull(uaNo) + " vehicleNo" + CommonUtility.deNull(vehicleNo));
			sql = "UPDATE UA_DETAILS SET TRUCK_NBR= :vehicleNo WHERE UA_NBR = :uaNo ";

			paramMap.put("vehicleNo", vehicleNo);
			paramMap.put("uaNo", uaNo);

			log.info(" *** updateVehicleNo SQL *****" + sql + " paramMap " + paramMap.toString());
			namedParameterJdbcTemplate.update(sql, paramMap);
		} catch (NullPointerException e) {
			log.info("Exception: updateVehicleNo ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: updateVehicleNo ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** updateVehicleNo  END *****");
		}
	}
}
