package sg.com.jp.generalcargo.dao.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.TesnRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TesnValueObject;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.DpeCommonUtil;
import sg.com.jp.generalcargo.util.GbmsCommonUtility;

@Repository("tesnRepository")
public class TesnJdbcRepository implements TesnRepository {

	private static final Log log = LogFactory.getLog(TesnJdbcRepository.class);

	private static final String param = " paramMap = ";

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	// ejb.sessionBeans.gbms.cargo.tesn.tesnJpPsa-->TesnEJB

	@Override
	public List<TesnValueObject> getTesnSearchJpPsaList(String tesnNo, String coCode) throws BusinessException {
		String sql1 = "";
		String sql2 = "";
		List<TesnValueObject> tesnList = new ArrayList<TesnValueObject>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		try {
			log.info("START: getTesnSearchJpPsaList  DAO  Start tesnNo" + CommonUtility.deNull(tesnNo) + " coCode" + CommonUtility.deNull(coCode));
			
			String vvcode = "";
			sql1 = "SELECT IN_VOY_VAR_NBR FROM ESN WHERE ESN_ASN_NBR = :tesnNo ";
			paramMap.put("tesnNo", tesnNo);

			log.info(" getTesnSearchJpPsaList  DAO  SQL " + sql1.toString() + param  + paramMap);
			for (rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap); rs1.next();)
				vvcode = CommonUtility.deNull(rs1.getString("IN_VOY_VAR_NBR"));

			String esnAsnNbr = "";
			String nbrPkgs = "";
			String crgDes = "";
			if (coCode.equals("JP"))
				sql2 = "SELECT TE.ESN_ASN_NBR,TE.EDO_ASN_NBR,VES.VSL_NM,VES.IN_VOY_NBR,TE.SECOND_CAR_VOY_NBR,TE.SECOND_CAR_VES_NM,TE.NBR_PKGS,MFT.CRG_DES FROM ESN E,TESN_JP_PSA TE,MANIFEST_DETAILS MFT,MFT_MARKINGS MARK,GB_EDO EDO,VESSEL_CALL VES WHERE TE.ESN_ASN_NBR = E.ESN_ASN_NBR AND E.ESN_STATUS = 'A' AND EDO.MFT_SEQ_NBR = MFT.MFT_SEQ_NBR AND MARK.MFT_SQ_NBR = MFT.MFT_SEQ_NBR AND  VES.VV_CD = E.IN_VOY_VAR_NBR AND E.IN_VOY_VAR_NBR= :vvcode AND EDO.CRG_STATUS IN ('T','R') AND EDO.EDO_ASN_NBR = TE.EDO_ASN_NBR ORDER BY TE.ESN_ASN_NBR ASC ";
			else
				sql2 = "SELECT TE.ESN_ASN_NBR,TE.EDO_ASN_NBR,VES.VSL_NM,VES.IN_VOY_NBR,TE.SECOND_CAR_VOY_NBR,TE.SECOND_CAR_VES_NM,TE.NBR_PKGS,MFT.CRG_DES FROM ESN E,TESN_JP_PSA TE,MANIFEST_DETAILS MFT,MFT_MARKINGS MARK,GB_EDO EDO,VESSEL_CALL VES WHERE TE.ESN_ASN_NBR = E.ESN_ASN_NBR AND E.ESN_STATUS = 'A' AND EDO.MFT_SEQ_NBR = MFT.MFT_SEQ_NBR AND MARK.MFT_SQ_NBR = MFT.MFT_SEQ_NBR AND  VES.VV_CD = E.IN_VOY_VAR_NBR AND E.IN_VOY_VAR_NBR= :vvcode AND EDO.CRG_STATUS IN ('T','R') AND E.ESN_CREATE_CD = :coCode AND EDO.EDO_ASN_NBR = TE.EDO_ASN_NBR ORDER BY TE.ESN_ASN_NBR ASC";
			int recCount = 1;
			String nextRec = "";
			String prevRec = "";
			String inVesNm = "";
			String inVarNm = "";
			String secCar = "";
			String secVoy = "";
			String edoAsnNbr = "";
			TesnValueObject tesnValueObject = null;

			if (coCode.equals("JP")) {
				paramMap.put("vvcode", vvcode);
			} else {
				paramMap.put("vvcode", vvcode);
				paramMap.put("coCode", coCode);
			}

			log.info(" getTesnSearchJpPsaList  DAO  SQL " + sql2.toString());
			log.info(param + paramMap);
			rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);
			do {
				if (!rs2.next())
					break;
				esnAsnNbr = CommonUtility.deNull(rs2.getString("ESN_ASN_NBR"));
				edoAsnNbr = CommonUtility.deNull(rs2.getString("EDO_ASN_NBR"));
				secVoy = CommonUtility.deNull(rs2.getString("SECOND_CAR_VOY_NBR"));
				inVesNm = CommonUtility.deNull(rs2.getString("VSL_NM"));
				inVarNm = CommonUtility.deNull(rs2.getString("IN_VOY_NBR"));
				secCar = CommonUtility.deNull(rs2.getString("SECOND_CAR_VES_NM"));
				nbrPkgs = CommonUtility.deNull(rs2.getString("NBR_PKGS"));
				crgDes = rs2.getString("CRG_DES");
				if (recCount == 1) {
					prevRec = esnAsnNbr;
					recCount++;
				} else {
					nextRec = esnAsnNbr;
					if (nextRec.equalsIgnoreCase(prevRec))
						continue;
					prevRec = nextRec;
				}
				tesnValueObject = new TesnValueObject();
				tesnValueObject.setEsnAsnNbr(esnAsnNbr);
				tesnValueObject.setEdoAsnNbr(edoAsnNbr);
				tesnValueObject.setInVslNm(inVesNm);
				tesnValueObject.setInVarNbr(inVarNm);
				tesnValueObject.setVslNm(secCar);
				tesnValueObject.setInVoyNbr(secVoy);
				tesnValueObject.setNbrPkgs(nbrPkgs);
				tesnValueObject.setCrgDes(crgDes);
				tesnValueObject.setVarNbr(vvcode);
				tesnList.add(tesnValueObject);
			} while (true);

		log.info("Result: getTesnSearchJpPsaList DAO " + tesnList.toString());
		} catch (NullPointerException e) {
			log.info("Exception getTesnSearchJpPsaList : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTesnSearchJpPsaList : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTesnSearchJpPsaList  DAO  END");
		}
		return tesnList;
	}

	public boolean chkCrgAgtAdpEdo(String edoNbr, String compCode) throws BusinessException {
		
		String sql = "";
		boolean edo_asn_nbr_fl = false;
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT gb_edo.edo_asn_nbr ");
		sb.append("FROM ");
		sb.append("gb_edo ");
		sb.append("LEFT JOIN vessel_call ves ON ");
		sb.append("gb_edo.var_nbr = ves.vv_cd ");
		sb.append("WHERE ");
		sb.append("ves.vv_status_ind != 'CX' ");
		sb.append("AND gb_edo.edo_asn_nbr = :edoNbr ");
		sb.append("AND (gb_edo.CA_CUST_CD = :compCode ");
		sb.append("OR gb_edo.ADP_CUST_CD = :compCode ");
		sb.append("OR gb_edo.edo_asn_nbr IN ( ");
		sb.append("SELECT ");
		sb.append("	esn_asn_nbr ");
		sb.append("FROM ");
		sb.append("	sub_adp ");
		sb.append("WHERE ");
		sb.append("	esn_asn_nbr = :edoNbr ");
		sb.append("	AND edo_esn_ind = '1' ");
		sb.append("	AND status_cd = 'A') )");

		sql = sb.toString();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		log.info("only Cargo Agent & ADP of the first carrier Sql=" + sql);
		try {
			log.info("START: chkCrgAgtAdpEdo  DAO  Start edoNbr" + CommonUtility.deNull(edoNbr) + " compCode" + CommonUtility.deNull(compCode));
			
			paramMap.put("compCode", compCode);
			paramMap.put("edoNbr", edoNbr);
			log.info(" chkCrgAgtAdpEdo  DAO  SQL " + sql.toString());
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				edo_asn_nbr_fl = true;
			} else {
				edo_asn_nbr_fl = false;
			}

		log.info(" Result: chkCrgAgtAdpEdo DAO :" + edo_asn_nbr_fl);
		} catch (NullPointerException e) {
			log.info("Exception chkCrgAgtAdpEdo : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkCrgAgtAdpEdo : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkCrgAgtAdpEdo  DAO  END");

		}
		return edo_asn_nbr_fl;
	}

	/**
	 * This method retrieves the BL No for a MFT Sequenece No.
	 *
	 * @param mftsqnbr MFT Sequence No
	 * @return String BL No
	 * @exception BusinessException
	 * @exception RemoteException
	 */
	private String getBLNnbr(String mftsqnbr) throws BusinessException {
		
		String sql = "";
		String total = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		
		sql = "select sum(nvl(TRANS_NBR_PKGS,0)+nvl(CUT_OFF_NBR_PKGS,0)) from gb_edo edo where MFT_SEQ_NBR = :mftsqnbr ";
		try {
			log.info("START: getBLNnbr  DAO  Start mftsqnbr" + CommonUtility.deNull(mftsqnbr));
			
			paramMap.put("mftsqnbr", mftsqnbr);
			log.info(" getBLNnbr  DAO  SQL " + sql);
			log.info(param + paramMap);
			for (rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap); rs.next();)
				total = rs.getString(1);

			log.info(" Result: getBLNnbr DAO :" + CommonUtility.deNull(total));
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

	/**
	 * This method retrieves the Cargo Details for a particular EDO.
	 *
	 * @param asnNo EDO ASN No
	 * @return ArrayList List of EDO details
	 * @exception BusinessException
	 * @exception RemoteException
	 */
	public List<TesnValueObject> getCargoDetails(String asnNo) throws BusinessException {
		String sql1 = "";
		String sql1_1 = "";
		String sql2 = "";
		String sql3 = "";
		String sql4 = "";
		String sql5 = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs1 = null;
		List<TesnValueObject> crgDetails = new ArrayList<TesnValueObject>();
		SqlRowSet rs1_1 = null;
		SqlRowSet rs2 = null;
		SqlRowSet rs3 = null;
		SqlRowSet rs4 = null;
		SqlRowSet rs41 = null;
		SqlRowSet rs5 = null;
		SqlRowSet rsNew = null;
		try {
			log.info("START: getCargoDetails  DAO  Start asnNo" + CommonUtility.deNull(asnNo));
			

			sql1 = "SELECT * FROM GB_EDO WHERE EDO_ASN_NBR = :asnNo AND EDO_STATUS = 'A' AND CRG_STATUS IN ('T','R')";
			paramMap.put("asnNo", asnNo);

			log.info(" getCargoDetails  DAO  SQL " + sql1);
			log.info(param + paramMap);
			rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
			
			String varNbr = "";
			String edoAsnNbr = "";
			boolean checkEdo1 = true;
			TesnValueObject tesnValueObject = new TesnValueObject();
			while (rs1.next()) {
				varNbr = CommonUtility.deNull(rs1.getString("VAR_NBR"));
				edoAsnNbr = CommonUtility.deNull(rs1.getString("EDO_ASN_NBR"));
				checkEdo1 = false;
			}
			tesnValueObject.setVarNbr(CommonUtility.deNull(varNbr));
			tesnValueObject.setEdoAsnNbr(CommonUtility.deNull(edoAsnNbr));
			if (checkEdo1)
				throw new BusinessException("Invalid ASN No.");
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT ");
			sb.append("	EDO.NBR_PKGS, ");
			sb.append("	EDO.TRANS_DN_NBR_PKGS, ");
			sb.append("	EDO.TRANS_NBR_PKGS, ");
			sb.append("	EDO.CRG_STATUS ");
			sb.append("FROM ");
			sb.append("	GB_EDO EDO, ");
			sb.append("	MANIFEST_DETAILS MFT, ");
			sb.append("	MFT_MARKINGS MARK ");
			sb.append("WHERE ");
			sb.append("	EDO_ASN_NBR = :asnNo ");
			sb.append("	AND EDO.MFT_SEQ_NBR = MFT.MFT_SEQ_NBR ");
			sb.append("	AND MARK.MFT_SQ_NBR = MFT.MFT_SEQ_NBR ");
			sb.append("	AND EDO_STATUS = 'A' ");
			sb.append("	AND BL_STATUS = 'A' ");
			sb.append("	AND EDO.CRG_STATUS IN ('T', 'R')");

			sql1_1 = sb.toString();

			paramMap.put("asnNo", asnNo);

			log.info(" getCargoDetails  DAO  SQL " + sql1_1);
			log.info(param + paramMap);
			rs1_1 = namedParameterJdbcTemplate.queryForRowSet(sql1_1, paramMap);
			int nbrPkgsN = 0;
			int transDnNbrPkgsN = 0;
			int transNbrPkgsN = 0;
			int finNbrPkgsIntN = 0;
			String crgStatus;
			for (crgStatus = ""; rs1_1.next(); crgStatus = rs1_1.getString("CRG_STATUS")) {
				nbrPkgsN = rs1_1.getInt("NBR_PKGS");
				transDnNbrPkgsN = rs1_1.getInt("TRANS_DN_NBR_PKGS");
				transNbrPkgsN = rs1_1.getInt("TRANS_NBR_PKGS");
				log.info("transDnNbrPkgsN = " + transDnNbrPkgsN);
				log.info("transNbrPkgsN = " + transNbrPkgsN);
			}
			// Start fix nbr packages calculation - NS FEB 2022
//			finNbrPkgsIntN = nbrPkgsN;
//			
//			StringBuffer  sbfr = new StringBuffer();
//			sbfr.append("SELECT TE.NBR_PKGS ");
//			sbfr.append("FROM ESN E, TESN_JP_PSA TE, ");
//			sbfr.append("MANIFEST_DETAILS MFT, ");
//			sbfr.append("MFT_MARKINGS MARK, ");
//			sbfr.append("GB_EDO EDO, ");
//			sbfr.append("VESSEL_CALL VES, ");
//			sbfr.append("adm_user ad, ");
//			sbfr.append("( ");
//			sbfr.append("SELECT ");
//			sbfr.append("esn_asn_nbr, min(last_modify_user_id) last_modify_user_id, trans_nbr ");
//			sbfr.append("FROM ");
//			sbfr.append("TESN_JP_PSA_TRANS ");
//			sbfr.append("GROUP BY ");
//			sbfr.append("esn_asn_nbr, trans_nbr ");
//			sbfr.append("HAVING ");
//			sbfr.append("trans_nbr = 0 ) tmp ");
//			sbfr.append("WHERE ");
//			sbfr.append("TE.ESN_ASN_NBR = E.ESN_ASN_NBR ");
//			sbfr.append("AND E.ESN_STATUS = 'A' ");
//			sbfr.append("AND EDO.MFT_SEQ_NBR = MFT.MFT_SEQ_NBR ");
//			sbfr.append("AND ad.user_acct(+) = tmp.last_modify_user_id ");
//			sbfr.append("AND tmp.esn_asn_nbr (+) = TE.esn_asn_nbr ");
//			sbfr.append("AND MARK.MFT_SQ_NBR = MFT.MFT_SEQ_NBR ");
//			sbfr.append("AND VES.VV_CD = E.IN_VOY_VAR_NBR ");
//			sbfr.append("AND EDO.VAR_NBR = :vvcode ");
//			sbfr.append("AND EDO.CRG_STATUS IN ('T', ");
//			sbfr.append("'R') ");
//			sbfr.append("AND EDO.EDO_ASN_NBR = TE.EDO_ASN_NBR ");
//			sbfr.append("AND TE.EDO_ASN_NBR = :edoAsn ");
//			sbfr.append("ORDER BY ");
//			sbfr.append("TE.ESN_ASN_NBR ASC");
//			paramMap.put("vvcode",varNbr);
//			paramMap.put("edoAsn",asnNo);
//			log.info("*** SQL getCargoDetails " + sbfr.toString()+ param + paramMap);
//			rs1_1 = namedParameterJdbcTemplate.queryForRowSet(sbfr.toString(), paramMap);
//			while (rs1_1.next()) {
//				nbrPkgsN = rs1_1.getInt("NBR_PKGS");
//				finNbrPkgsIntN = finNbrPkgsIntN-nbrPkgsN;
//			}
			// End fix nbr packages calculation - NS FEB 2022
			finNbrPkgsIntN = nbrPkgsN - transNbrPkgsN;
			if (finNbrPkgsIntN == 0 || finNbrPkgsIntN < 0)
				if (crgStatus.equalsIgnoreCase("T")) {
					log.info("Writing from Tesn.getCargoDetails");
					log.info("All packages in this EDO have been transshipped.");
					throw new BusinessException("All packages in this EDO have been transshipped.");
				} else {
					log.info("Writing from Tesn.getCargoDetails");
					log.info("Invalid ASN No.");
					throw new BusinessException("Invalid ASN No.");
				}
			sql2 = "SELECT VV_CD,VV_STATUS_IND,GB_CLOSE_BJ_IND,GB_CLOSE_SHP_IND, GB_CLOSE_VSL_IND,VSL_NM,IN_VOY_NBR FROM VESSEL_CALL VC, GB_EDO EDO WHERE EDO_ASN_NBR = :asnNo AND VC.VV_CD = EDO.VAR_NBR AND EDO_STATUS='A' AND CRG_STATUS IN ('T','R')";

			paramMap.put("asnNo", asnNo);

			log.info(" getCargoDetails  DAO  SQL " + sql2);
			log.info(param + paramMap);
			rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);

			String vslNm = "";
			String inVoyNbr = "";
			for (; rs2.next(); tesnValueObject.setInVoyNbr(CommonUtility.deNull(inVoyNbr))) {				
				vslNm = CommonUtility.deNull(rs2.getString("VSL_NM"));
				inVoyNbr = CommonUtility.deNull(rs2.getString("IN_VOY_NBR"));
				tesnValueObject.setVslNm(CommonUtility.deNull(vslNm));
			}
			log.info("CLOSE BJ Chk-Disabled");// Bhuvana-29/09/2011

			sql3 = "SELECT MFT.CRG_TYPE,CRG.CRG_TYPE_NM FROM GB_EDO EDO, MANIFEST_DETAILS MFT,CRG_TYPE CRG  WHERE EDO_ASN_NBR = :edoAsnNbr AND EDO.MFT_SEQ_NBR = MFT.MFT_SEQ_NBR AND CRG.CRG_TYPE_CD=MFT.CRG_TYPE AND EDO.CRG_STATUS in ('T','R')";
			paramMap.put("edoAsnNbr", edoAsnNbr);

			log.info(" getCargoDetails  DAO  SQL " + sql3);
			log.info(param + paramMap);
			rs3 = namedParameterJdbcTemplate.queryForRowSet(sql3, paramMap);
			String cargoType = "";
			String cargoTypeNm = "";
			for (; rs3.next(); tesnValueObject.setCargoTypeNm(CommonUtility.deNull(cargoTypeNm))) {
				cargoType = CommonUtility.deNull(rs3.getString("CRG_TYPE"));
				cargoTypeNm = CommonUtility.deNull(rs3.getString("CRG_TYPE_NM"));
				tesnValueObject.setCargoType(CommonUtility.deNull(cargoType));
			}

			StringBuffer sb1 = new StringBuffer();
			sb1.append("SELECT ");
			sb1.append("	MFT.PKG_TYPE, ");
			sb1.append("	MFT.HS_CODE, ");
			sb1.append("	MFT.CRG_DES, ");
			sb1.append("	MARK.MFT_MARKINGS, ");
			sb1.append("	MFT.MFT_SEQ_NBR, ");
			sb1.append("	MFT.DG_IND, ");
			sb1.append("	MFT.CNTR_TYPE, ");
			sb1.append("	MFT.CNTR_SIZE, ");
			sb1.append("	EDO.TRANS_DN_NBR_PKGS, ");
			sb1.append("	EDO.NBR_PKGS, ");
			sb1.append("	EDO.TRANS_NBR_PKGS, ");
			sb1.append("	EDO.NOM_WT, ");
			sb1.append("	EDO.NOM_VOL, ");
			sb1.append("	NVL(EDO.CUT_OFF_NBR_PKGS, 0) AS CUT_OFF_NBR_PKGS, ");
			sb1.append("	NVL(EDO.DN_NBR_PKGS, 0) AS DN_NBR_PKGS, ");
			sb1.append("	NVL(MFT.NBR_PKGS_IN_PORT, 0) AS NBR_PKGS_IN_PORT, ");
			sb1.append("	nvl(mft.NBR_PKGS, 0)-nvl(mft.CUT_OFF_NBR_PKGS, 0) AS blnbrpkgs ");
			sb1.append("FROM ");
			sb1.append("	GB_EDO EDO, ");
			sb1.append("	MANIFEST_DETAILS MFT, ");
			sb1.append("	MFT_MARKINGS MARK ");
			sb1.append("WHERE ");
			sb1.append("	EDO_ASN_NBR = :asnNo ");
			sb1.append("	AND EDO.MFT_SEQ_NBR = MFT.MFT_SEQ_NBR ");
			sb1.append("	AND MARK.MFT_SQ_NBR = MFT.MFT_SEQ_NBR ");
			sb1.append("	AND EDO.CRG_STATUS IN ('T', 'R')");

			sql4 = sb1.toString();

			paramMap.put("asnNo", asnNo);

			log.info(" getCargoDetails  DAO  SQL " + sql4);
			log.info(param + paramMap);
			rs4 = namedParameterJdbcTemplate.queryForRowSet(sql4, paramMap);
			String pkgType = "";
			String hsCode = "";
			String hsCodeDisp = "";
			String crgDes = "";
			String mftMarkings = "";
			String mftSeqNbr = "";
			String dgInd = "";
			String cntrType = "";
			String cntrSize = "";
			String finNbrPkgs = "";
			int nbrPkgs = 0;
			String nbrPkgsStr = "";
			int transDnNbrPkgs = 0;
			int transNbrPkgs = 0;
			int finNbrPkgsInt = 0;
			int cutoffNbr = 0;
			int shortlandnbr = 0;
			int dnnbr = 0;
			int blnbrpkgs = 0;
			String balance = "";
			String nomWt = "";
			String nomVol = "";
			for (; rs4.next(); tesnValueObject.setNomVol(CommonUtility.deNull(nomVol))) {
				pkgType = CommonUtility.deNull(rs4.getString("PKG_TYPE"));
				hsCode = CommonUtility.deNull(rs4.getString("HS_CODE"));
				crgDes = CommonUtility.deNull(rs4.getString("CRG_DES"));
				mftMarkings = CommonUtility.deNull(rs4.getString("MFT_MARKINGS"));
				mftSeqNbr = CommonUtility.deNull(rs4.getString("MFT_SEQ_NBR"));
				// START CR FTZ - NS JUNE 2024
				hsCodeDisp = this.getHsCodeDisplay(asnNo, mftSeqNbr);
				// END CR FTZ - NS JUNE 2024
				dgInd = CommonUtility.deNull(rs4.getString("DG_IND"));
				cntrType = CommonUtility.deNull(rs4.getString("CNTR_TYPE"));
				cntrSize = CommonUtility.deNull(rs4.getString("CNTR_SIZE"));
				transDnNbrPkgs = rs4.getInt("TRANS_DN_NBR_PKGS");
				log.info("transDnNbrPkgs = " + transDnNbrPkgs);
				
				nbrPkgs = rs4.getInt("NBR_PKGS");
				transNbrPkgs = rs4.getInt("TRANS_NBR_PKGS");
				nomWt = CommonUtility.deNull(rs4.getString("NOM_WT"));
				nomVol = CommonUtility.deNull(rs4.getString("NOM_VOL"));
				cutoffNbr = rs4.getInt("CUT_OFF_NBR_PKGS");
				shortlandnbr = rs4.getInt("NBR_PKGS_IN_PORT");
				dnnbr = rs4.getInt("DN_NBR_PKGS");
				blnbrpkgs = rs4.getInt("blnbrpkgs");
				balance = getBLNnbr(mftSeqNbr);
				int bal1 = blnbrpkgs - Integer.parseInt(balance);
				finNbrPkgsInt = nbrPkgs - transNbrPkgs - cutoffNbr - dnnbr;
				if (bal1 > finNbrPkgsInt)
					log.info("finNbrPkgs = " + finNbrPkgs.toString());
				else
					finNbrPkgsInt -= shortlandnbr;
				finNbrPkgs = " ".concat(String.valueOf(String.valueOf(finNbrPkgsInt)));
				tesnValueObject.setPkgType(CommonUtility.deNull(pkgType));
				tesnValueObject.setHsCode(CommonUtility.deNull(hsCode));
				// START CR FTZ - NS JUNE 2024
				tesnValueObject.setHsCodeDisp(CommonUtility.deNull(hsCodeDisp));
				// END CR FTZ - NS JUNE 2024
				tesnValueObject.setCrgDes(CommonUtility.deNull(crgDes));
				tesnValueObject.setMftMarkings(CommonUtility.deNull(mftMarkings));
				tesnValueObject.setDgInd(CommonUtility.deNull(dgInd));
				tesnValueObject.setCntrType(CommonUtility.deNull(cntrType));
				tesnValueObject.setCntrSize(CommonUtility.deNull(cntrSize));
				tesnValueObject.setNbrPkgs(CommonUtility.deNull(finNbrPkgs));
				tesnValueObject.setNomWt(CommonUtility.deNull(nomWt));
			}
			// START CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
			// Add SQL to get balance weight and volume
			StringBuffer sbNew = new StringBuffer();
			sbNew.append("SELECT ");
			sbNew.append("(ed.nom_wt - NVL((SELECT SUM(tesn.NOM_WT) FROM TESN_JP_JP tesn, esn esn WHERE tesn.edo_asn_nbr = ");
			sbNew.append(":edoAsn AND tesn.ESN_ASN_NBR = esn.esn_asn_nbr AND esn_status = 'A' GROUP BY tesn.edo_asn_nbr), 0) ");
			sbNew.append("- NVL((SELECT SUM(nom_wt) FROM dn_details WHERE edo_asn_nbr = :edoAsn AND dn_status = 'A' AND TESN_ASN_NBR IS NULL GROUP BY edo_asn_nbr), 0) ");
			sbNew.append("- NVL((SELECT SUM(psa.NOM_WT) FROM TESN_JP_PSA psa, esn esn WHERE psa.edo_asn_nbr = :edoAsn AND psa.ESN_ASN_NBR = esn.esn_asn_nbr AND esn_status = 'A' ");
			sbNew.append("GROUP BY psa.edo_asn_nbr), 0)) edo_nom_wt, ");
			sbNew.append("(ed.nom_vol - NVL((SELECT SUM(tesn.NOM_VOL) FROM TESN_JP_JP tesn, esn esn WHERE tesn.edo_asn_nbr = :edoAsn AND tesn.ESN_ASN_NBR = esn.esn_asn_nbr ");
			sbNew.append("AND esn.esn_status = 'A' GROUP BY tesn.edo_asn_nbr), 0) - NVL((SELECT SUM(nom_vol) FROM dn_details WHERE edo_asn_nbr = :edoAsn AND dn_status = 'A' ");
			sbNew.append("AND TESN_ASN_NBR IS NULL GROUP BY edo_asn_nbr), 0) - NVL((SELECT SUM(psa.NOM_VOL) FROM TESN_JP_PSA psa, esn esn WHERE psa.edo_asn_nbr = :edoAsn ");
			sbNew.append("AND psa.ESN_ASN_NBR = esn.esn_asn_nbr AND esn.esn_status = 'A' GROUP BY psa.edo_asn_nbr), 0)) edo_nom_vol, ");
			sbNew.append("ed.trans_nbr_pkgs, ");
			sbNew.append("ed.nbr_pkgs-nvl(ed.CUT_OFF_NBR_PKGS, 0) AS edo_nbr_pkgs, ");
			sbNew.append("nvl(ed.nbr_pkgs, 0)-nvl(ed.trans_nbr_pkgs, 0)-nvl(ed.DN_NBR_PKGS, 0) balance ");
			sbNew.append("FROM ");
			sbNew.append("gb_edo ed ");
			sbNew.append("WHERE ");
			sbNew.append("ed.edo_asn_nbr = :edoAsn");

			String sqlNew = sbNew.toString();
			paramMap.put("edoAsn", asnNo);
			
			log.info(" get balance weight and volume  DAO  SQL " + sqlNew);
			log.info(param + paramMap);
			rsNew = namedParameterJdbcTemplate.queryForRowSet(sqlNew, paramMap);
			for (; rsNew.next(); tesnValueObject.setEdoNomVol(CommonUtility.deNull(nomVol))) {
				tesnValueObject.setEdoNomWt(CommonUtility.deNull(rsNew.getString("edo_nom_wt")));
				nomVol = CommonUtility.deNull(rsNew.getString("edo_nom_vol"));
			}
			// END CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
			nbrPkgsStr = "".concat(String.valueOf(String.valueOf(nbrPkgs)));
			log.info("nbrPkgsStr = " + nbrPkgsStr.toString());
			String sql41 = "SELECT PKG_DESC FROM PKG_TYPES WHERE PKG_TYPE_CD = :pkgType ";
			paramMap.put("pkgType", pkgType);

			log.info(" getCargoDetails  DAO  SQL " + sql41);
			log.info(param + paramMap);
			rs41 = namedParameterJdbcTemplate.queryForRowSet(sql41, paramMap);
			String pkgDesc = "";
			for (; rs41.next(); tesnValueObject.setPkgTypeDesc(CommonUtility.deNull(pkgDesc)))
				pkgDesc = CommonUtility.deNull(rs41.getString("PKG_DESC"));

			sql5 = "SELECT * FROM BL_CNTR_DETAILS WHERE MFT_SEQ_NBR = :mftSeqNbr ";
			paramMap.put("mftSeqNbr", mftSeqNbr);

			log.info(" getCargoDetails  DAO  SQL " + sql5);
			log.info(param + paramMap);
			rs5 = namedParameterJdbcTemplate.queryForRowSet(sql5, paramMap);
			String cntr[] = new String[4];
			for (int i = 0; rs5.next(); i++)
				cntr[i] = CommonUtility.deNull(rs5.getString("CNTR_NBR"));

			tesnValueObject.setCntrNbr1(CommonUtility.deNull(cntr[0]));
			tesnValueObject.setCntrNbr2(CommonUtility.deNull(cntr[1]));
			tesnValueObject.setCntrNbr3(CommonUtility.deNull(cntr[2]));
			tesnValueObject.setCntrNbr4(CommonUtility.deNull(cntr[3]));
			crgDetails.add(tesnValueObject);

			log.info(" Result: getCargoDetails  DAO :"+ crgDetails.toString());
		} catch (NullPointerException e) {
			log.info("Exception getCargoDetails : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception getCargoDetails : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getCargoDetails : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoDetails  DAO  END ");
		}
		return crgDetails;
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
			log.info(param + paramMap);
			rs4 = namedParameterJdbcTemplate.queryForRowSet(sql4, paramMap);
			String hsCode = "", hsSubFr = "", hsSubTo = "", customHs = "";
			while (rs4.next()) {
				hsCode = rs4.getString("HS_CODE");
				hsSubFr = rs4.getString("HS_SUB_CODE_FR");
				hsSubTo = rs4.getString("HS_SUB_CODE_TO");
				customHs = CommonUtil.deNull(rs4.getString("CUSTOM_HS_CODE"));
				slist.add(hsCode + " (" + hsSubFr + (hsSubFr.equalsIgnoreCase(hsSubTo) ? "" : "-" + hsSubTo) +  ") " + (customHs.isEmpty() ? "" : "~" + customHs ));
			}
			if(slist.size() == 0) {
				sb1.setLength(0);
				sb1.append("SELECT HS_CODE, HS_SUB_CODE_FR, HS_SUB_CODE_TO, CUSTOM_HS_CODE FROM GB_EDO EDO,MANIFEST_DETAILS MFT WHERE EDO_ASN_NBR = :asnNo AND EDO.MFT_SEQ_NBR = MFT.MFT_SEQ_NBR ");
				paramMap.put("asnNo", asnNo);

				log.info(" getHSEdoDetails  DAO  SQL " + sb1.toString());
				log.info(param + paramMap);
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

	/**
	 * This method retrieves the list of port.
	 *
	 * @return ArrayList List of Port Codes/Port Names
	 * @exception BusinessException
	 * @exception RemoteException
	 */
	public List<TesnValueObject> getPortList() throws BusinessException {
		
		String sql = "";
		List<TesnValueObject> portList = new ArrayList<TesnValueObject>();
		TesnValueObject tesnValueObject = null;
		sql = "SELECT PORT_CD, PORT_NM FROM UN_PORT_CODE ";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		
		try {
			log.info("START: getPortList  DAO  Start ");
		
			log.info(" getPortList  DAO  SQL " + sql);

			for (rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap); rs.next(); portList.add(tesnValueObject)) {
				tesnValueObject = new TesnValueObject();
				tesnValueObject.setPortL(CommonUtility.deNull(rs.getString("PORT_CD")));
				tesnValueObject.setPortLn(CommonUtility.deNull(rs.getString("PORT_NM")));
			}
			log.info(" Result: getPortList " + portList.toString());
		} catch (NullPointerException e) {
			log.info("Exception getPortList : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getPortList : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPortList  DAO  END");
		}
		return portList;
	}

	/**
	 * This method cancels a particular TESN.
	 *
	 * @param tesnNbr    TESN ASN No
	 * @param edoAsnNbrG EDO ASN No
	 * @param uid        User ID
	 * @exception BusinessException
	 * @exception RemoteException
	 */
	public String deleteRecord(String tesnNbr, String edoAsnNbrG, String uid) throws BusinessException {
		
		String sql1 = "";
		String sql2 = "";
		String sql5 = "";
		String sql6 = "";
		String res = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		try {
			log.info("START: deleteRecord  DAO  Start tesnNbr" + CommonUtility.deNull(tesnNbr) + " edoAsnNbrG" + CommonUtility.deNull(edoAsnNbrG) 
			+ " uid" + CommonUtility.deNull(uid));
			
			sql1 = "SELECT NBR_PKGS FROM TESN_JP_PSA WHERE ESN_ASN_NBR= :tesnNbr";
			paramMap.put("tesnNbr", tesnNbr);

			log.info(" deleteRecord  DAO  SQL " + sql1);
			log.info(param + paramMap);
			rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
			int tesnNbrPkgs;
			for (tesnNbrPkgs = 0; rs1.next(); tesnNbrPkgs = rs1.getInt("NBR_PKGS"));
			sql2 = "SELECT TRANS_NBR_PKGS FROM GB_EDO WHERE EDO_ASN_NBR= :edoAsnNbrG";
			paramMap.put("edoAsnNbrG", edoAsnNbrG);

			log.info(" deleteRecord  DAO  SQL " + sql2);
			log.info(param + paramMap);
			rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);
			int transNbrPkgs;
			for (transNbrPkgs = 0; rs2.next(); transNbrPkgs = rs2.getInt("TRANS_NBR_PKGS"));
			int finNbrPkgs = 0;
			finNbrPkgs = transNbrPkgs - tesnNbrPkgs;
			sql5 = "UPDATE ESN SET ESN_STATUS = 'X',last_modify_dttm = sysdate, last_modify_user_id = :uid  WHERE  ESN_ASN_NBR = :tesnNbr ";
			
			paramMap.put("uid", uid);
			paramMap.put("tesnNbr", tesnNbr);
			log.info(" deleteRecord  DAO  SQL " + sql5);
			log.info(param + paramMap);
			namedParameterJdbcTemplate.update(sql5, paramMap);
			// Added by Alvin for Audit Trial on 14/06/2004
			int transNo = 0;
			String transSql = "";
			transNo = getMaxTransNo("ESN_TRANS", "ESN_ASN_NBR", tesnNbr);
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT ");
			sb.append("	INTO ");
			sb.append("	ESN_TRANS (ESN_ASN_NBR, ");
			sb.append("	TRANS_NBR, ");
			sb.append("	ESN_STATUS, ");
			sb.append("	LAST_MODIFY_USER_ID, ");
			sb.append("	LAST_MODIFY_DTTM) ");
			sb.append("VALUES (:esnasnnbr, ");
			sb.append(":transNo, ");
			sb.append("'X', ");
			sb.append(":uid, ");
			sb.append("sysdate)");

			transSql = sb.toString();
			paramMap.put("esnasnnbr", tesnNbr);
			paramMap.put("transNo", transNo);
			paramMap.put("uid", uid);
			log.info(" deleteRecord  DAO  SQL " + transSql);
			log.info(param + paramMap);
			namedParameterJdbcTemplate.update(transSql, paramMap);

			res = "* * *";
			sql6 = "UPDATE GB_EDO SET TRANS_NBR_PKGS = :finNbrPkgs WHERE EDO_ASN_NBR = :edoAsnNbrG ";
		
			paramMap.put("finNbrPkgs", finNbrPkgs);
			paramMap.put("edoAsnNbrG", edoAsnNbrG);
			log.info(param + paramMap);
			namedParameterJdbcTemplate.update(sql6, paramMap);
			// Added by Alvin for Audit Trial on 14/06/2004
			transNo = getMaxTransNo("GB_EDO_TRANS", "EDO_ASN_NBR", edoAsnNbrG);
			StringBuffer sb2 = new StringBuffer();
			sb2.append("INSERT INTO GB_EDO_TRANS (EDO_ASN_NBR, TRANS_NBR, TRANS_NBR_PKGS, ");
			sb2.append(" LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID ) VALUES ");
			sb2.append("(:edoAsnNbrG, :transNo,:finNbrPkgs, sysdate,:uid)");

			transSql = sb2.toString();
			paramMap.put("edoAsnNbrG", edoAsnNbrG);
			paramMap.put("transNo", transNo);
			paramMap.put("finNbrPkgs", finNbrPkgs);
			paramMap.put("uid", uid);
			log.info(" deleteRecord  DAO  SQL " + transSql);
			log.info(param + paramMap);
			namedParameterJdbcTemplate.update(transSql, paramMap);
			
			log.info(" Result: deleteRecord "+ res);
	
		} catch (NullPointerException e) {
			log.info("Exception deleteRecord : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception deleteRecord : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception deleteRecord : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: deleteRecord  DAO  END");
		}

		return res;
	}

	/**
	 * This method updates the changes to a particular TESN
	 *
	 * @param uid        User ID
	 * @param ps         Port Code
	 * @param secCarves  Second Carrier Vessel Name
	 * @param secCarvoy  Second Carrier Out Voyage No
	 * @param noOfPkgs   No of Packages
	 * @param shipper    Shipper Name
	 * @param tesn       TESN ASN No
	 * @param edoAsnNbrG EDO ASN No
	 * @param bkref      Booking Reference No
	 * @param acctNbr    Account No
	 * @exception BusinessException
	 * @exception RemoteException
	 */
	public String updateRecord(String uid, String ps, String secCarves, String secCarvoy, int noOfPkgs, String shipper,
			String tesnNbr, String edoAsnNbrG, String bkref, String acctNbr, String category, String nomWtStr, String nomVolStr) throws BusinessException {
		
		String sql1 = "";
		String sql4 = "";
		String sql4_1 = "";
		String sql6 = "";
		String res = "";
		// added by Alvin Chia on 02 July 2004 : enhancing audit trail logging
		int transNo = 0;
		String transSql = "";
		// end added by Alvin Chia on 02 July 2004
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs1 = null;
		SqlRowSet rs4 = null;
		SqlRowSet rs4_1 = null;
		try {

			log.info("START: updateRecord  DAO  Start uid" + CommonUtility.deNull(uid) + " ps" + CommonUtility.deNull(ps) + " secCarves" + CommonUtility.deNull(secCarves) + " secCarvoy"
					+ CommonUtility.deNull(secCarvoy) + " noOfPkgs" + noOfPkgs + " shipper" + CommonUtility.deNull(shipper) + " tesnNbr" + CommonUtility.deNull(tesnNbr) + " edoAsnNbrG"
					+ CommonUtility.deNull(edoAsnNbrG) + " bkref" + CommonUtility.deNull(bkref) + " acctNbr" + CommonUtility.deNull(acctNbr) + " category" + CommonUtility.deNull(category)
					+ " weight" + CommonUtility.deNull(nomWtStr) + " volume" + CommonUtility.deNull(nomVolStr));
			
			
			TesnValueObject tesnValueObject = new TesnValueObject();
			sql1 = "SELECT PORT_CD,PORT_NM FROM UN_PORT_CODE WHERE PORT_CD = :ps ";
			String portNm = "";
			boolean checkPort = true;

			paramMap.put("ps", ps);

			log.info(" updateRecord  DAO  SQL " + sql1);
			log.info(param + paramMap);
			for (rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap); rs1.next();) {
				portNm = CommonUtility.deNull(rs1.getString("PORT_NM"));
				checkPort = false;
			}

			tesnValueObject.setPortLn(CommonUtility.deNull(portNm));
			if (checkPort) {
				log.info("Writing from Tesn.getCargoDetails");
				log.info("Invalid port code");
				throw new BusinessException("Invalid port code");
			}
			sql4 = "SELECT * FROM GB_EDO EDO,MANIFEST_DETAILS MFT,MFT_MARKINGS MARK WHERE EDO_ASN_NBR = :edoAsnNbrG AND EDO.MFT_SEQ_NBR = MFT.MFT_SEQ_NBR AND MARK.MFT_SQ_NBR = MFT.MFT_SEQ_NBR AND EDO.CRG_STATUS IN ('T','R')";
			
			paramMap.put("edoAsnNbrG", edoAsnNbrG);

			log.info(" updateRecord  DAO  SQL " + sql4);
			log.info(param + paramMap);
			rs4 = namedParameterJdbcTemplate.queryForRowSet(sql4, paramMap);
			int nbrPkgs = 0;
			int transNbrPkgs = 0;
			int finNbrPkgsInt = 0;
			String nomWt = "";
			String nomVol;
			for (nomVol = ""; rs4.next(); nomVol = CommonUtility.deNull(rs4.getString("NOM_VOL"))) {
				nbrPkgs = rs4.getInt("NBR_PKGS");
				transNbrPkgs = rs4.getInt("TRANS_NBR_PKGS");
				nomWt = CommonUtility.deNull(rs4.getString("NOM_WT"));
			}

			float tesnNomWt = 0.0F;
			float tesnNomVol = 0.0F;
			float edoNbrPkgs = 0.0F;
			float edoNomWt = 0.0F;
			float edoNomVol = 0.0F;
			edoNomWt = Float.parseFloat(nomWt);
			edoNomVol = Float.parseFloat(nomVol);
			sql4_1 = "SELECT NBR_PKGS FROM TESN_JP_PSA WHERE ESN_ASN_NBR= :tesnNbr";
			paramMap.put("tesnNbr", tesnNbr);

			log.info(" updateRecord  DAO  SQL " + sql4_1);
			log.info(param + paramMap);
			rs4_1 = namedParameterJdbcTemplate.queryForRowSet(sql4_1, paramMap);
			int tesnNbrPkgs;
			for (tesnNbrPkgs = 0; rs4_1.next(); tesnNbrPkgs = rs4_1.getInt("NBR_PKGS"))
				;
			edoNbrPkgs = nbrPkgs - transNbrPkgs;
			if (edoNbrPkgs == (float) 0)
				edoNbrPkgs = tesnNbrPkgs;
			if (edoNbrPkgs < (float) tesnNbrPkgs)
				edoNbrPkgs += tesnNbrPkgs;
			tesnNomWt = ((float) noOfPkgs / edoNbrPkgs) * edoNomWt;
			tesnNomVol = ((float) noOfPkgs / edoNbrPkgs) * edoNomVol;
			String tesnNomWtStr = "";
			String tesnNomVolStr = "";
			tesnNomWtStr = "".concat(String.valueOf(String.valueOf(nomWtStr)));
			tesnNomVolStr = "".concat(String.valueOf(String.valueOf(nomVolStr)));

			finNbrPkgsInt = (transNbrPkgs - tesnNbrPkgs) + noOfPkgs;
			String shipperApp = "";
			shipperApp = GbmsCommonUtility.addApostr(shipper);

			transNo = getMaxTransNo("TESN_JP_PSA_TRANS", "ESN_ASN_NBR", tesnNbr);

			StringBuffer sb1 = new StringBuffer();
			sb1.append("INSERT ");
			sb1.append("INTO ");
			sb1.append("TESN_JP_PSA_TRANS(ESN_ASN_NBR, ");
			sb1.append("	EDO_ASN_NBR, ");
			sb1.append("	TRANS_NBR, ");
			sb1.append("	DIS_PORT, ");
			sb1.append("	SECOND_CAR_VOY_NBR, ");
			sb1.append("	SECOND_CAR_VES_NM, ");
			sb1.append("	SHIPPER_NM, ");
			sb1.append("	EDO_NBR_PKGS, ");
			sb1.append("	NBR_PKGS, ");
			sb1.append("	NOM_WT, ");
			sb1.append("	NOM_VOL, ");
			sb1.append("	LAST_MODIFY_DTTM, ");
			sb1.append("	LAST_MODIFY_USER_ID) ");
			sb1.append("VALUES(:tesnNbr, ");
			sb1.append(":edoAsnNbrG, ");
			sb1.append(":transNo, ");
			sb1.append(":ps, ");
			sb1.append(":secCarvoy, ");
			sb1.append(":secCarves, ");
			sb1.append(":shipperApp, ");
			sb1.append(":finNbrPkgsInt, ");
			sb1.append(":noOfPkgs, ");
			sb1.append(":tesnNomWtStr, ");
			sb1.append(":tesnNomVolStr, ");
			sb1.append("sysdate, ");
			sb1.append(":uid) ");

			transSql = sb1.toString();

			paramMap.put("tesnNbr", tesnNbr);
			paramMap.put("edoAsnNbrG", edoAsnNbrG);
			paramMap.put("transNo", transNo);
			paramMap.put("ps", ps);
			paramMap.put("secCarvoy", secCarvoy);
			paramMap.put("secCarves", secCarves);
			paramMap.put("shipperApp", shipperApp);
			paramMap.put("finNbrPkgsInt", finNbrPkgsInt);
			paramMap.put("noOfPkgs", noOfPkgs);
			paramMap.put("tesnNomWtStr", tesnNomWtStr);
			paramMap.put("tesnNomVolStr", tesnNomVolStr);
			paramMap.put("uid", uid);
			log.info(" updateRecord  DAO  SQL " + transSql);
			log.info(param + paramMap);
			namedParameterJdbcTemplate.update(transSql, paramMap);
			
			// Start fix update records - NS FEB 2022
			sb1 = new StringBuffer();
			sb1.append("UPDATE TESN_JP_PSA");
			sb1.append(" SET ");
			sb1.append("	DIS_PORT = :ps, ");
			sb1.append("	SECOND_CAR_VOY_NBR = :secCarvoy, ");
			sb1.append("	SECOND_CAR_VES_NM = :secCarves, ");
			sb1.append("	SHIPPER_NM = :shipperApp, ");
			sb1.append("	EDO_NBR_PKGS = :finNbrPkgsInt, ");
			sb1.append("	NBR_PKGS = :noOfPkgs, ");
			// START CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
			sb1.append("	NOM_WT = :tesnNomWtStr, ");
			sb1.append("	NOM_VOL = :tesnNomVolStr, ");
			// END CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
			sb1.append("	SEC_BK_REF_NBR = :bkref, ");
			sb1.append("	SEC_ACCT_NBR = :acctNbr ");
			sb1.append(" WHERE ESN_ASN_NBR = :tesnNbr AND EDO_ASN_NBR = :edoAsnNbrG");

			transSql = sb1.toString();
			paramMap.put("tesnNbr", tesnNbr);
			paramMap.put("edoAsnNbrG", edoAsnNbrG);
			paramMap.put("ps", ps);
			paramMap.put("secCarvoy", secCarvoy);
			paramMap.put("secCarves", secCarves);
			paramMap.put("shipperApp", shipperApp);
			paramMap.put("finNbrPkgsInt", finNbrPkgsInt);
			paramMap.put("noOfPkgs", noOfPkgs);
			// START CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
			paramMap.put("tesnNomWtStr", tesnNomWtStr);
			paramMap.put("tesnNomVolStr", tesnNomVolStr);
			// END CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
			paramMap.put("bkref", bkref);
			paramMap.put("acctNbr", acctNbr);
			log.info(" updateRecord  DAO  SQL " + transSql);
			log.info(param + paramMap);
			namedParameterJdbcTemplate.update(transSql, paramMap);
			// End fix update records - NS FEB 2022

			res = "* * *";
			sql6 = "UPDATE GB_EDO SET TRANS_NBR_PKGS = :finNbrPkgsInt WHERE EDO_ASN_NBR = :edoAsnNbrG";

			paramMap.put("edoAsnNbrG", edoAsnNbrG);
			paramMap.put("finNbrPkgsInt", finNbrPkgsInt);
			log.info(" updateRecord  DAO  SQL " + sql6);
			log.info(param + paramMap);
			namedParameterJdbcTemplate.update(sql6, paramMap);

			transNo = getMaxTransNo("GB_EDO_TRANS", "EDO_ASN_NBR", edoAsnNbrG);
			StringBuffer sb2 = new StringBuffer();
			sb2.append("INSERT INTO GB_EDO_TRANS (EDO_ASN_NBR, TRANS_NBR, TRANS_NBR_PKGS, ");
			sb2.append(" LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID ) VALUES ");
			sb2.append("(:edoAsnNbrG, :transNo,:finNbrPkgsInt, sysdate,:uid)");

			transSql = sb2.toString();
			paramMap.put("edoAsnNbrG", edoAsnNbrG);
			paramMap.put("transNo", transNo);
			paramMap.put("finNbrPkgsInt", finNbrPkgsInt);
			paramMap.put("uid", uid);
			log.info(" updateRecord  DAO  SQL " + transSql);
			log.info(param + paramMap);
			namedParameterJdbcTemplate.update(transSql, paramMap);

			String newsql = "UPDATE ESN SET CARGO_CATEGORY_CD = :category WHERE ESN_ASN_NBR = :tesnNbr ";
			paramMap.put("category", category);
			paramMap.put("tesnNbr", tesnNbr);
			log.info(" updateRecord  DAO  SQL " + newsql);
			log.info(param + paramMap);
			namedParameterJdbcTemplate.update(newsql, paramMap);

			log.info(" Result: updateRecord "+ res);
		} catch (NullPointerException e) {
			log.info("Exception updateRecord : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception updateRecord : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception updateRecord : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateRecord  DAO  END");
		}
		return res;
	}

	/**
	 * This method retrieves the TESN's nominated weight and volume.
	 *
	 * @param tesnNbr TESN ASN No
	 * @return String TESN's nominated weight and volume
	 * @exception BusinessException
	 * @exception RemoteException
	 */
	public String getTesnWtVol(String tesnNbr) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String sql = "";
		String swtvol = "";
		sql = "SELECT nom_wt,nom_vol from tesn_jp_psa where esn_asn_nbr= :tesnNbr";

		try {
			log.info("START: getTesnWtVol  DAO  Start asnNo " + CommonUtility.deNull(tesnNbr));
			
			paramMap.put("tesnNbr", tesnNbr);

			log.info(" getTesnWtVol  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next())
				swtvol = String
						.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(rs.getString(1)))))
								.append("-").append(rs.getString(2))));

			log.info(" getTesnWtVol  DAO  Result" + CommonUtility.deNull(swtvol));
	
		} catch (NullPointerException e) {
			log.info("Exception getTesnWtVol : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTesnWtVol : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTesnWtVol  DAO  END");
		}
		return swtvol;

	}

	public String getVslTypeByAsnNo(String asnNo) throws BusinessException {
		String vslType = "";
		String sql = "select C.VSL_TYPE_CD from vessel_call a ,gb_edo b ,vessel c where a.vv_cd = b.var_nbr and c.vsl_nm = A.vsl_nm and b.edo_asn_nbr= :asnNo";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		try {
			log.info("START: getVslTypeByAsnNo  DAO  Start asnNo " + CommonUtility.deNull(asnNo));
			
			paramMap.put("asnNo", asnNo);

			log.info(" getVslTypeByAsnNo  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				vslType = rs.getString("VSL_TYPE_CD");
			}
			log.info(" getVslTypeByAsnNo  DAO  Result" + CommonUtility.deNull(vslType));
		
		} catch (NullPointerException e) {
			log.info("Exception getVslTypeByAsnNo : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVslTypeByAsnNo : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVslTypeByAsnNo  DAO  END");
		}
		return vslType;
	}

	public List<TesnValueObject> getDisplayCargoDetails(String asnNo, String vvCd, String secCarNm)
			throws BusinessException {
		String sql1_1 = "";
		String sqlOne = "";
		String sqlTwo = "";
		String sqlThree = "";
		String sql4 = "";
		String sql5 = "";
		List<TesnValueObject> crgDetails = new ArrayList<TesnValueObject>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs1 = null;
		SqlRowSet rsOne = null;
		SqlRowSet rs1_1 = null;
		SqlRowSet rsTwo = null;
		SqlRowSet rsThree = null;
		SqlRowSet rs4 = null;
		SqlRowSet rs41 = null;
		SqlRowSet rs5 = null;
		SqlRowSet rs6 = null;
		SqlRowSet rs7 = null;
		SqlRowSet rs8 = null;
		SqlRowSet rs9 = null;
		SqlRowSet rsNew = null;

		try {
			log.info("START: getDisplayCargoDetails  DAO  Start asnNo " + CommonUtility.deNull(asnNo) + " vvCd" + CommonUtility.deNull(vvCd) + " secCarNm"
					+ CommonUtility.deNull(secCarNm));
			
			String sql1 = "SELECT NBR_PKGS,EDO_ASN_NBR,DN_NBR_PKGS FROM TESN_JP_PSA WHERE ESN_ASN_NBR = :asnNo";

			paramMap.put("asnNo", asnNo);

			log.info(" getDisplayCargoDetails  DAO  SQL " + sql1);
			log.info(param + paramMap);
			rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
			String edoAsn = "";
			int tesnNbrPkgsInt = 0;
			int transDnNbrPkgs;
			for (transDnNbrPkgs = 0; rs1.next(); transDnNbrPkgs = rs1.getInt("DN_NBR_PKGS")) {
				edoAsn = CommonUtility.deNull(rs1.getString("EDO_ASN_NBR"));
				tesnNbrPkgsInt = rs1.getInt("NBR_PKGS");
			}

			sqlOne = "SELECT * FROM GB_EDO WHERE EDO_ASN_NBR = :edoAsn AND CRG_STATUS IN ('T','R')";
			paramMap.put("edoAsn", edoAsn);

			log.info(" getDisplayCargoDetails  DAO  SQL " + sqlOne);
			log.info(param + paramMap);
			rsOne = namedParameterJdbcTemplate.queryForRowSet(sqlOne, paramMap);
			String varNbr = "";
			String edoAsnNbr = "";
			TesnValueObject tesnValueObject = new TesnValueObject();
			while (rsOne.next()) {
				varNbr = CommonUtility.deNull(rsOne.getString("VAR_NBR"));
				edoAsnNbr = CommonUtility.deNull(rsOne.getString("EDO_ASN_NBR"));
			}
			tesnValueObject.setVarNbr(CommonUtility.deNull(varNbr));
			tesnValueObject.setEdoAsnNbr(CommonUtility.deNull(edoAsnNbr));
			StringBuffer sb = new StringBuffer();
			sb.append(
					"SELECT EDO.NBR_PKGS,EDO.TRANS_NBR_PKGS,EDO.TRANS_DN_NBR_PKGS FROM GB_EDO EDO,MANIFEST_DETAILS MFT, ");
			sb.append("MFT_MARKINGS MARK WHERE EDO_ASN_NBR = :edoAsn AND EDO.MFT_SEQ_NBR = MFT.MFT_SEQ_NBR  ");
			sb.append("AND MARK.MFT_SQ_NBR = MFT.MFT_SEQ_NBR AND EDO.CRG_STATUS IN ('T','R')");

			sql1_1 = sb.toString();
			paramMap.put("edoAsn", edoAsn);

			log.info(" getDisplayCargoDetails  DAO  SQL " + sql1_1);
			log.info(param + paramMap);
			rs1_1 = namedParameterJdbcTemplate.queryForRowSet(sql1_1, paramMap);
			String finNbrPkgs = "";
			int nbrPkgs = 0;
			int finNbrPkgsInt = 0;
			 int transNbrPkgs = 0;
		      String closeBjInd = "";
			while (rs1_1.next()) {
				nbrPkgs = rs1_1.getInt("NBR_PKGS");
				transNbrPkgs = rs1_1.getInt("TRANS_NBR_PKGS");
			}
//			// Start fix nbr packages calculation - NS FEB 2022
//			finNbrPkgsInt = nbrPkgs;
//			
//			StringBuffer  sbfr = new StringBuffer();
//			sbfr.append("SELECT TE.NBR_PKGS ");
//			sbfr.append("FROM ESN E, TESN_JP_PSA TE, ");
//			sbfr.append("MANIFEST_DETAILS MFT, ");
//			sbfr.append("MFT_MARKINGS MARK, ");
//			sbfr.append("GB_EDO EDO, ");
//			sbfr.append("VESSEL_CALL VES, ");
//			sbfr.append("adm_user ad, ");
//			sbfr.append("( ");
//			sbfr.append("SELECT ");
//			sbfr.append("esn_asn_nbr, min(last_modify_user_id) last_modify_user_id, trans_nbr ");
//			sbfr.append("FROM ");
//			sbfr.append("TESN_JP_PSA_TRANS ");
//			sbfr.append("GROUP BY ");
//			sbfr.append("esn_asn_nbr, trans_nbr ");
//			sbfr.append("HAVING ");
//			sbfr.append("trans_nbr = 0 ) tmp ");
//			sbfr.append("WHERE ");
//			sbfr.append("TE.ESN_ASN_NBR = E.ESN_ASN_NBR ");
//			sbfr.append("AND E.ESN_STATUS = 'A' ");
//			sbfr.append("AND EDO.MFT_SEQ_NBR = MFT.MFT_SEQ_NBR ");
//			sbfr.append("AND ad.user_acct(+) = tmp.last_modify_user_id ");
//			sbfr.append("AND tmp.esn_asn_nbr (+) = TE.esn_asn_nbr ");
//			sbfr.append("AND MARK.MFT_SQ_NBR = MFT.MFT_SEQ_NBR ");
//			sbfr.append("AND VES.VV_CD = E.IN_VOY_VAR_NBR ");
//			sbfr.append("AND EDO.VAR_NBR = :vvcode ");
//			sbfr.append("AND EDO.CRG_STATUS IN ('T', ");
//			sbfr.append("'R') ");
//			sbfr.append("AND EDO.EDO_ASN_NBR = TE.EDO_ASN_NBR ");
//			sbfr.append("AND TE.EDO_ASN_NBR = :edoAsn ");
//			sbfr.append("ORDER BY ");
//			sbfr.append("TE.ESN_ASN_NBR ASC");
//			paramMap.put("vvcode",vvCd);
//			paramMap.put("edoAsn",edoAsn);
//			log.info(" getDisplayCargoDetails  DAO  SQL " + sbfr.toString() + param + paramMap);
//			rs1_1 = namedParameterJdbcTemplate.queryForRowSet(sbfr.toString(), paramMap);
//			while (rs1_1.next()) {
//				nbrPkgs = rs1_1.getInt("NBR_PKGS");
//				finNbrPkgsInt = finNbrPkgsInt-nbrPkgs;
//			}
////			if (finNbrPkgsInt == 0)
////				finNbrPkgsInt = tesnNbrPkgsInt;
////			if (finNbrPkgsInt < tesnNbrPkgsInt)
////				finNbrPkgsInt += tesnNbrPkgsInt;
//			finNbrPkgs = " ".concat(String.valueOf(String.valueOf(finNbrPkgsInt)));
//			tesnValueObject.setEdoNbrPkgs(finNbrPkgs.trim());
//			// End fix nbr packages calculation - NS FEB 2022
			
			finNbrPkgsInt = nbrPkgs - transNbrPkgs;
		      if (finNbrPkgsInt == 0)
		        finNbrPkgsInt = tesnNbrPkgsInt; 
		      if (finNbrPkgsInt < tesnNbrPkgsInt)
		        finNbrPkgsInt += tesnNbrPkgsInt; 
		      finNbrPkgs = " ".concat(String.valueOf(String.valueOf(finNbrPkgsInt)));
		      tesnValueObject.setEdoNbrPkgs(finNbrPkgs.trim());
			
			String transDnNbrPkgsStr = "";
			transDnNbrPkgsStr = "".concat(String.valueOf(String.valueOf(transDnNbrPkgs)));
			tesnValueObject.setTransDnNbrPkgs(transDnNbrPkgsStr);
			sqlTwo = "SELECT VV_STATUS_IND,GB_CLOSE_BJ_IND,GB_CLOSE_SHP_IND, GB_CLOSE_VSL_IND,VSL_NM,IN_VOY_NBR FROM VESSEL_CALL, GB_EDO EDO WHERE EDO.EDO_ASN_NBR = :edoAsn AND VV_CD =EDO.VAR_NBR AND EDO.CRG_STATUS IN ('T','R')";

			paramMap.put("edoAsn", edoAsn);

			log.info(" getDisplayCargoDetails  DAO  SQL " + sqlTwo);
			log.info(param + paramMap);
			rsTwo = namedParameterJdbcTemplate.queryForRowSet(sqlTwo, paramMap);
			String gbCloseBj = "";
			String vslNm = "";
			String inVoyNbr = "";
			for (; rsTwo.next(); tesnValueObject.setInVoyNbr(CommonUtility.deNull(inVoyNbr))) {
				gbCloseBj = CommonUtility.deNull(rsTwo.getString("GB_CLOSE_BJ_IND"));
				vslNm = CommonUtility.deNull(rsTwo.getString("VSL_NM"));
				inVoyNbr = CommonUtility.deNull(rsTwo.getString("IN_VOY_NBR"));
				tesnValueObject.setVslNm(CommonUtility.deNull(vslNm));
			}

			tesnValueObject.setGbCloseBjInd(CommonUtility.deNull(gbCloseBj));
			StringBuffer sb1 = new StringBuffer();
			sb1.append(
					"SELECT MFT.CRG_DES,MFT.CRG_TYPE,CRG.CRG_TYPE_NM FROM GB_EDO EDO,MANIFEST_DETAILS MFT,MFT_MARKINGS MARK, CRG_TYPE CRG  WHERE EDO_ASN_NBR = :edoAsn ");
			sb1.append(
					"AND EDO.MFT_SEQ_NBR = MFT.MFT_SEQ_NBR AND  MARK.MFT_SQ_NBR = MFT.MFT_SEQ_NBR AND CRG.CRG_TYPE_CD =  MFT.CRG_TYPE AND EDO.CRG_STATUS IN ('T','R')");

			sqlThree = sb1.toString();

			paramMap.put("edoAsn", edoAsn);

			log.info(" getDisplayCargoDetails  DAO  SQL " + sqlThree);
			log.info(param + paramMap);
			rsThree = namedParameterJdbcTemplate.queryForRowSet(sqlThree, paramMap);
			String cargoDesc = "";
			String cargoType = "";
			String cargoTypeNm = "";
			for (; rsThree.next(); tesnValueObject.setCargoTypeNm(CommonUtility.deNull(cargoTypeNm))) {
				cargoDesc = CommonUtility.deNull(rsThree.getString("CRG_DES"));
				cargoType = CommonUtility.deNull(rsThree.getString("CRG_TYPE"));
				cargoTypeNm = CommonUtility.deNull(rsThree.getString("CRG_TYPE_NM"));
				tesnValueObject.setCrgDes(CommonUtility.deNull(cargoDesc));
				tesnValueObject.setCargoType(CommonUtility.deNull(cargoType));
			}

			sql4 = "SELECT * FROM GB_EDO EDO,MANIFEST_DETAILS MFT,MFT_MARKINGS MARK WHERE EDO_ASN_NBR = :edoAsn AND EDO.MFT_SEQ_NBR = MFT.MFT_SEQ_NBR AND MARK.MFT_SQ_NBR = MFT.MFT_SEQ_NBR";

			paramMap.put("edoAsn", edoAsn);

			log.info(" getDisplayCargoDetails  DAO  SQL " + sql4);
			log.info(param + paramMap);
			rs4 = namedParameterJdbcTemplate.queryForRowSet(sql4, paramMap);
			String pkgType = "";
			String hsCode = "";
			// START CR FTZ - NS JUNE 2024
			String hsCodeDisp = "";
			// END CR FTZ - NS JUNE 2024
			String mftMarkings = "";
			String mftSeqNbr = "";
			String dgInd = "";
			String cntrType = "";
			String cntrSize = "";
			String edoNbrPkgs = "";
			String edoNomWt = "";
			String edoNomVol = "";
			for (; rs4.next(); tesnValueObject.setNomVol(CommonUtility.deNull(edoNomVol))) {
				pkgType = CommonUtility.deNull(rs4.getString("PKG_TYPE"));
				hsCode = CommonUtility.deNull(rs4.getString("HS_CODE"));
				mftMarkings = CommonUtility.deNull(rs4.getString("MFT_MARKINGS"));
				mftSeqNbr = CommonUtility.deNull(rs4.getString("MFT_SEQ_NBR"));
				// START CR FTZ - NS JUNE 2024
				hsCodeDisp = this.getHsCodeDisplay(edoAsn, mftSeqNbr);
				// START CR FTZ - NS JUNE 2024
				dgInd = CommonUtility.deNull(rs4.getString("DG_IND"));
				cntrType = CommonUtility.deNull(rs4.getString("CNTR_TYPE"));
				cntrSize = CommonUtility.deNull(rs4.getString("CNTR_SIZE"));
				edoNbrPkgs = CommonUtility.deNull(rs4.getString("NBR_PKGS"));
				edoNomWt = CommonUtility.deNull(rs4.getString("NOM_WT"));
				edoNomVol = CommonUtility.deNull(rs4.getString("NOM_VOL"));
				tesnValueObject.setPkgType(CommonUtility.deNull(pkgType));
				tesnValueObject.setHsCode(CommonUtility.deNull(hsCode));
				// START CR FTZ - NS JUNE 2024
				tesnValueObject.setHsCodeDisp(CommonUtility.deNull(hsCodeDisp).isEmpty() ? hsCode : hsCodeDisp);
				// END CR FTZ - NS JUNE 2024
				tesnValueObject.setMftMarkings(CommonUtility.deNull(mftMarkings));
				tesnValueObject.setDgInd(CommonUtility.deNull(dgInd));
				tesnValueObject.setCntrType(CommonUtility.deNull(cntrType));
				tesnValueObject.setCntrSize(CommonUtility.deNull(cntrSize));
				tesnValueObject.setNbrPkgs(CommonUtility.deNull(edoNbrPkgs));
				tesnValueObject.setNomWt(CommonUtility.deNull(edoNomWt));
			}
			// START CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
			// Add SQL to get balance weight and volume
			StringBuffer sbNew = new StringBuffer();
			sbNew.append("SELECT ");
			sbNew.append("(ed.nom_wt - NVL((SELECT SUM(tesn.NOM_WT) FROM TESN_JP_JP tesn, esn esn WHERE tesn.edo_asn_nbr = ");
			sbNew.append(":edoAsn AND tesn.ESN_ASN_NBR = esn.esn_asn_nbr AND esn_status = 'A' GROUP BY tesn.edo_asn_nbr), 0) ");
			sbNew.append("- NVL((SELECT SUM(nom_wt) FROM dn_details WHERE edo_asn_nbr = :edoAsn AND dn_status = 'A' AND TESN_ASN_NBR IS NULL GROUP BY edo_asn_nbr), 0) ");
			sbNew.append("- NVL((SELECT SUM(psa.NOM_WT) FROM TESN_JP_PSA psa, esn esn WHERE psa.edo_asn_nbr = :edoAsn AND psa.ESN_ASN_NBR = esn.esn_asn_nbr AND esn_status = 'A' ");
			sbNew.append("GROUP BY psa.edo_asn_nbr), 0)) edo_nom_wt, ");
			sbNew.append("(ed.nom_vol - NVL((SELECT SUM(tesn.NOM_VOL) FROM TESN_JP_JP tesn, esn esn WHERE tesn.edo_asn_nbr = :edoAsn AND tesn.ESN_ASN_NBR = esn.esn_asn_nbr ");
			sbNew.append("AND esn.esn_status = 'A' GROUP BY tesn.edo_asn_nbr), 0) - NVL((SELECT SUM(nom_vol) FROM dn_details WHERE edo_asn_nbr = :edoAsn AND dn_status = 'A' ");
			sbNew.append("AND TESN_ASN_NBR IS NULL GROUP BY edo_asn_nbr), 0) - NVL((SELECT SUM(psa.NOM_VOL) FROM TESN_JP_PSA psa, esn esn WHERE psa.edo_asn_nbr = :edoAsn ");
			sbNew.append("AND psa.ESN_ASN_NBR = esn.esn_asn_nbr AND esn.esn_status = 'A' GROUP BY psa.edo_asn_nbr), 0)) edo_nom_vol, ");
			sbNew.append("ed.trans_nbr_pkgs, ");
			sbNew.append("ed.nbr_pkgs-nvl(ed.CUT_OFF_NBR_PKGS, 0) AS edo_nbr_pkgs, ");
			sbNew.append("nvl(ed.nbr_pkgs, 0)-nvl(ed.trans_nbr_pkgs, 0)-nvl(ed.DN_NBR_PKGS, 0) balance ");
			sbNew.append("FROM ");
			sbNew.append("gb_edo ed ");
			sbNew.append("WHERE ");
			sbNew.append("ed.edo_asn_nbr = :edoAsn");
			
			String sqlNew = sbNew.toString();
			paramMap.put("edoAsn", edoAsn);

			log.info(" get balance weight and volume  DAO  SQL " + sqlNew);
			log.info(param + paramMap);
			rsNew = namedParameterJdbcTemplate.queryForRowSet(sqlNew, paramMap);
			for (; rsNew.next(); tesnValueObject.setEdoNomVol(CommonUtility.deNull(edoNomVol))) {
				tesnValueObject.setEdoNomWt(CommonUtility.deNull(rsNew.getString("edo_nom_wt")));
				edoNomVol = CommonUtility.deNull(rsNew.getString("edo_nom_vol"));
			}
			// END CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
			String sql41 = "SELECT PKG_DESC FROM PKG_TYPES WHERE PKG_TYPE_CD = :pkgType ";
			paramMap.put("pkgType", pkgType);

			log.info(" getDisplayCargoDetails  DAO  SQL " + sql41);
			log.info(param + paramMap);
			rs41 = namedParameterJdbcTemplate.queryForRowSet(sql41, paramMap);
			
			String pkgDesc = "";
			for (; rs41.next(); tesnValueObject.setPkgTypeDesc(CommonUtility.deNull(pkgDesc)))
				pkgDesc = CommonUtility.deNull(rs41.getString("PKG_DESC"));

			sql5 = "SELECT * FROM BL_CNTR_DETAILS WHERE MFT_SEQ_NBR = :mftSeqNbr";

			paramMap.put("mftSeqNbr", mftSeqNbr);

			log.info(" getDisplayCargoDetails  DAO  SQL " + sql5);
			log.info(param + paramMap);
			rs5 = namedParameterJdbcTemplate.queryForRowSet(sql5, paramMap);
			
			String cntr[] = new String[4];
			for (int i = 0; rs5.next(); i++)
				cntr[i] = CommonUtility.deNull(rs5.getString("CNTR_NBR"));

			tesnValueObject.setCntrNbr1(CommonUtility.deNull(cntr[0]));
			tesnValueObject.setCntrNbr2(CommonUtility.deNull(cntr[1]));
			tesnValueObject.setCntrNbr3(CommonUtility.deNull(cntr[2]));
			tesnValueObject.setCntrNbr4(CommonUtility.deNull(cntr[3]));
			String sql6 = "SELECT * FROM TESN_JP_PSA WHERE ESN_ASN_NBR = :asnNo";
			String tesnSecCar = "";
			String tesnSecVoy = "";
			String tesnPortDis = "";
			String tesnShipper = "";
			String tesnNbrPkgs = "";
			String tesnNomWt = "";
			String tesnNomVol = "";
			String tesnBkRef = "";
			String tesnAcctNbr = "";
			paramMap.put("asnNo", asnNo);

			log.info(" getDisplayCargoDetails  DAO  SQL " + sql6);
			log.info(param + paramMap);
			for (rs6 = namedParameterJdbcTemplate.queryForRowSet(sql6, paramMap); rs6.next();) {
				tesnSecCar = CommonUtility.deNull(rs6.getString("SECOND_CAR_VES_NM"));
				tesnSecVoy = CommonUtility.deNull(rs6.getString("SECOND_CAR_VOY_NBR"));
				tesnPortDis = CommonUtility.deNull(rs6.getString("DIS_PORT"));
				tesnShipper = CommonUtility.deNull(rs6.getString("NBR_PKGS"));
				tesnNbrPkgs = CommonUtility.deNull(rs6.getString("SHIPPER_NM"));
				tesnNomWt = CommonUtility.deNull(rs6.getString("NOM_WT"));
				tesnNomVol = CommonUtility.deNull(rs6.getString("NOM_VOL"));
				tesnBkRef = CommonUtility.deNull(rs6.getString("SEC_BK_REF_NBR"));
				tesnAcctNbr = CommonUtility.deNull(rs6.getString("SEC_ACCT_NBR"));
			}

			tesnValueObject.setTesnAsnNbr(CommonUtility.deNull(asnNo));
			tesnValueObject.setTesnSecCar(CommonUtility.deNull(tesnSecCar));
			tesnValueObject.setTesnSecVoy(CommonUtility.deNull(tesnSecVoy));
			tesnValueObject.setTesnPortDis(CommonUtility.deNull(tesnPortDis));
			tesnValueObject.setTesnShipper(CommonUtility.deNull(tesnShipper));
			tesnValueObject.setTesnNbrPkgs(CommonUtility.deNull(tesnNbrPkgs));
			tesnValueObject.setTesnNomWt(CommonUtility.deNull(tesnNomWt));
			tesnValueObject.setTesnNomVol(CommonUtility.deNull(tesnNomVol));
			tesnValueObject.setTesnNomVol(CommonUtility.deNull(tesnNomVol));
			tesnValueObject.setTesnNomVol(CommonUtility.deNull(tesnNomVol));
			tesnValueObject.setBkRef(CommonUtility.deNull(tesnBkRef));
			tesnValueObject.setAccount(CommonUtility.deNull(tesnAcctNbr));
			String sql7 = "";
			sql7 = "SELECT DIS_PORT FROM TESN_JP_PSA WHERE ESN_ASN_NBR = :asnNo ";
			String disPort = "";

			paramMap.put("asnNo", asnNo);

			log.info(" getDisplayCargoDetails  DAO  SQL " + sql7);
			log.info(param + paramMap);
			for (rs7 = namedParameterJdbcTemplate.queryForRowSet(sql7, paramMap); rs7.next();)
				disPort = CommonUtility.deNull(rs7.getString("DIS_PORT"));

			String sql8 = "";
			sql8 = "SELECT PORT_NM FROM UN_PORT_CODE WHERE PORT_CD = :disPort";
			paramMap.put("disPort", disPort);

			log.info(" getDisplayCargoDetails  DAO  SQL " + sql8);
			log.info(param + paramMap);
			rs8 = namedParameterJdbcTemplate.queryForRowSet(sql8, paramMap);
			String portName;
			for (portName = ""; rs8.next(); portName = CommonUtility.deNull(rs8.getString("PORT_NM")))
				;
			tesnValueObject.setPortLn(CommonUtility.deNull(portName));

			// add by Zhenguo Deng on 14/02/2011 for Cargo Category
			String newsql = "select b.cc_cd,b.cc_name from esn a, cargo_category_code b where ESN_ASN_NBR = :asnNo and a.cargo_category_cd = b.cc_cd";
			paramMap.put("asnNo", asnNo);

			log.info(" getDisplayCargoDetails  DAO  SQL " + newsql);
			log.info(param + paramMap);
			rs9 = namedParameterJdbcTemplate.queryForRowSet(newsql, paramMap);
			String category = "";
			String categoryValue = "";
			while (rs9.next()) {
				category = CommonUtility.deNull(rs9.getString("cc_cd"));
				categoryValue = CommonUtility.deNull(rs9.getString("cc_name"));
			}
			tesnValueObject.setCategory(category);
			tesnValueObject.setCategoryValue(categoryValue);
			// end add

			crgDetails.add(tesnValueObject);

			log.info(" Result: getDisplayCargoDetails  "+ crgDetails.size());
		} catch (NullPointerException e) {
			log.info("Exception getDisplayCargoDetails : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getDisplayCargoDetails : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getDisplayCargoDetails  DAO  END");
		}
		return crgDetails;
	}

	/**
	 * This method retrieves the current system date/time.
	 *
	 * @return String Current System Date/Time
	 * @exception BusinessException
	 * @exception RemoteException
	 */
	public String getSysdate() throws BusinessException {
		
		String sql = "";
		String sdate = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		sql = "SELECT TO_CHAR(SYSDATE,'DDMMYYYY HH24MI') FROM DUAL";
		try {
			log.info("START: getSysdate  DAO  Start  ");

			log.info(" isEsn  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next())
				sdate = rs.getString(1);

			log.info(" Result: getSysdate  "+ sdate);
		} catch (NullPointerException e) {
			log.info("Exception getSysdate : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSysdate : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSysdate  DAO  END");
		}

		return sdate;

	}

	public String getBLNo(String tesn) throws BusinessException {
		String sql = "";
		String blno = "";
		sql = "select gb_edo.bl_nbr as blno from tesn_jp_psa, gb_edo  where tesn_jp_psa.edo_asn_nbr = gb_edo.edo_Asn_nbr  and tesn_jp_psa.edo_asn_nbr = :tesn ";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		try {
			log.info("START: getBLNo  DAO  Start  tesn" + CommonUtility.deNull(tesn));
			
			paramMap.put("tesn", String.valueOf(tesn));

			log.info(" getBLNo  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next())
				blno = rs.getString("blno");
			log.info(" getBLNo  DAO  Result blno" + CommonUtility.deNull(blno));
		} catch (NullPointerException e) {
			log.info("Exception getBLNo : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBLNo : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBLNo  DAO  END");
		}
		return blno;
	}

	/**
	 * This method retrieves the TDB CR No for a particular EDO.
	 *
	 * @param edo EDO ASN No
	 * @return String TDB CR No
	 * @exception BusinessException
	 * @exception RemoteException
	 */
	public String getTdbCrno(String edo) throws BusinessException {
		String sql = "";
		String tdb = "";
		sql = "select gb_edo.CA_IC_TDBCR_NBR as tdb from gb_edo  where edo_Asn_nbr = :edo ";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		try {
			log.info("START: getTdbCrno  DAO  Start  edo" + CommonUtility.deNull(edo));
			
			paramMap.put("edo", String.valueOf(edo));

			log.info(" getTdbCrno  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next())
				tdb = rs.getString("tdb");
			
			log.info(" Result: getTdbCrno  "+ CommonUtility.deNull(tdb));
	
		} catch (NullPointerException e) {
			log.info("Exception getTdbCrno : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTdbCrno : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTdbCrno  DAO  END");
		}
		return tdb;
	}

	/**
	 * This method creates the nominated vessel for JP to PSA.
	 *
	 * @param vslName Vessel Name
	 * @param voyNbr  Voyage No
	 * @param userid  User Id
	 * @exception BusinessException
	 * @exception RemoteException
	 */
	public String createNomVesselJPPsa(String vslName, String voyNbr, String userid) throws BusinessException {
		String Status = "";		
		String sql = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		sql = "SELECT  * from nominated_vsl where out_voy_nbr = :voyNbr";
		
		try {
			log.info("START: createNomVesselJPPsa  DAO  Start vslName " + CommonUtility.deNull(vslName) + " voyNbr" + CommonUtility.deNull(voyNbr) + " userid"
					+ CommonUtility.deNull(userid));
			
		
			paramMap.put("voyNbr", voyNbr);

			log.info(" createNomVesselJPPsa  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next())
				Status = "Y";
			else
				Status = "N";

			if (Status.equals("N")) {

				String vvcode = "";
				String sql3 = "select nom_vv_code_sequence.nextval from dual ";

				log.info(" isEsn  DAO  SQL " + sql3);
				log.info(param + paramMap);
				for (SqlRowSet rs1 = namedParameterJdbcTemplate.queryForRowSet(sql3, paramMap); rs1.next();)
					vvcode = rs1.getString(1);

				sql = "insert into  nominated_vsl (nom_vv_cd , vsl_nm , out_voy_nbr , last_modify_user_id , last_modify_dttm) values (:vvcode,:vslName,:voyNbr,:userid,sysdate)";
				
				paramMap.put("vvcode", vvcode);
				paramMap.put("vslName", vslName);
				paramMap.put("voyNbr", voyNbr);
				paramMap.put("userid", userid);
				log.info(" createNomVesselJPPsa  DAO  SQL " + sql);
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(sql, paramMap);
			}

			log.info(" Result: createNomVesselJPPsa  "+ CommonUtility.deNull(Status));
		} catch (NullPointerException e) {
			log.info("Exception createNomVesselJPPsa : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception createNomVesselJPPsa : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: createNomVesselJPPsa  DAO  END");
		}

		return Status;

	}

	/**
	 * Gets the Max for the field TRANS_NBR from the given table name and field name
	 * 
	 * @param Connection con
	 * @param String     tableName
	 * @param String     fieldName
	 * @param String     fieldValue
	 * @return int
	 * @exception BusinessExcepton
	 */
	private int getMaxTransNo(String tableName, String fieldName, String fieldValue)
			throws BusinessException {
		String transNbr = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		try {
			log.info("START: getMaxTransNo  DAO  Start tableName " + CommonUtility.deNull(tableName) + " fieldName" + CommonUtility.deNull(fieldName)
					+ " fieldValue" + CommonUtility.deNull(fieldValue));

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT MAX(TRANS_NBR) FROM ");
			sb.append(tableName);
			sb.append(" WHERE ");
			sb.append(fieldName);
			sb.append(" = ");
			sb.append(fieldValue);
			String sql = sb.toString();


			log.info(" getMaxTransNo  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				transNbr = rs.getString(1);
			}
			if (transNbr == null || transNbr == "") {
				return 0;
			} else {
				int transNbrInt = Integer.parseInt(transNbr);
				transNbrInt++;
				return transNbrInt;
			}
			
		} catch (NullPointerException e) {
			log.info("Exception getMaxTransNo : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getMaxTransNo : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getMaxTransNo  DAO  END");


		}
	}

	/**
	 * This method adds a new TESN-JP-PSA.
	 *
	 * @param uid        User ID
	 * @param ps         Port Code
	 * @param secCarves  Second Carrier Vessel Name
	 * @param secCarvoy  Second Carrier Out Voyage
	 * @param noOfPkgs   No of Packages
	 * @param shipper    Shipper Name
	 * @param edoAsnNbrG EDO ASN No
	 * @param bkref      Booking Reference No
	 * @param acctNbr    Account No
	 * @return ArrayList List of TESN
	 * @exception BusinessException
	 *
	 */
	public List<TesnValueObject> addRecordForDPE(String uid, String ps, String secCarves, String secCarvoy,
			int noOfPkgs, String shipper, String edoAsnNbrG, String bkref, String acctNbr, String category, String nomWt, String nomVol)
			throws BusinessException {
		
		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String sql3_1 = "";
		String sql4 = "";
		String sql5 = "";
		String sql5_1 = "";
		String sql6 = "";
		String varNbrG = "";
		String coCode = ""; // COMPANY CODE SHOULD BE COMING FROM LOGON_ACCOUNT.
		int transNbrPkgsG = 0;
	
		// Added By Alvin on 14/06/2004 for audit trail logging
		int transNo = 0;
		String transSql = "";
		// end added by Alvin on 14/06/2004
		List<TesnValueObject> addRecordList = new ArrayList<TesnValueObject>();
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		SqlRowSet rsx = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		SqlRowSet rs3_1 = null;
		SqlRowSet rs3 = null;
		SqlRowSet rsasn = null;
		SqlRowSet rsedo = null;
		SqlRowSet rs5_1 = null;
		StringBuffer sbNew = new StringBuffer();
		Map<String, String> paramMap1 = new HashMap<String, String>();
		try {
			log.info("START: isEsn  DAO  Start uid " + CommonUtility.deNull(uid) + " ps" + CommonUtility.deNull(ps) + " secCarves" + CommonUtility.deNull(secCarves) + " secCarvoy"
					+ CommonUtility.deNull(secCarvoy) + " noOfPkgs" + noOfPkgs + " shipper" + CommonUtility.deNull(shipper) + " edoAsnNbrG" + CommonUtility.deNull(edoAsnNbrG) + " bkref"
					+ CommonUtility.deNull(bkref) + " acctNbr" + CommonUtility.deNull(acctNbr) + " category" + CommonUtility.deNull(category)
					+ " nomWt:" + CommonUtility.deNull(nomWt) + " nomVol:" + CommonUtility.deNull(nomVol));
			
		
			String login = "select cust_cd from logon_acct where login_id = :uid";
			paramMap.addValue("uid", uid);
			log.info(" addRecordForDPE  DAO  SQL " + login);
			log.info(param + paramMap.getValues());
			rsx = namedParameterJdbcTemplate.queryForRowSet(login, paramMap);
			if (rsx.next()) {
				coCode = CommonUtility.deNull(rsx.getString(1));
			}

			TesnValueObject tesnValueObject = new TesnValueObject();
			sql1 = "SELECT PORT_CD,PORT_NM FROM UN_PORT_CODE WHERE PORT_CD = :ps ";
			String portNm = "";
			boolean checkPort = true;
			paramMap.addValue("ps", ps);

			log.info(" addRecordForDPE  DAO  SQL " + sql1);
			log.info(param + paramMap.getValues());
			for (rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap); rs1.next();) {
				portNm = CommonUtility.deNull(rs1.getString("PORT_NM"));
				checkPort = false;
			}

			tesnValueObject.setPortLn(CommonUtility.deNull(portNm));
			if (checkPort) {
				log.info("Writing from Tesn.getCargoDetails");
				log.info("Invalid port code");
				throw new BusinessException("Invalid port code");
			}
			log.info("CHECKED PORT CODE edoAsnNbrG".concat(String.valueOf(String.valueOf(edoAsnNbrG))));
			sql2 = "SELECT VC.GB_CLOSE_BJ_IND,VC.VV_CD FROM VESSEL_CALL VC,GB_EDO EDO WHERE VC.VV_CD =EDO.VAR_NBR AND EDO_ASN_NBR =:edoAsnNbrG ";

			paramMap.addValue("edoAsnNbrG", edoAsnNbrG);

			log.info(" addRecordForDPE  DAO  SQL " + sql2);
			log.info(param + paramMap.getValues());
			rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);
			String gbCloseBj = "";
			while (rs2.next()) {
				gbCloseBj = CommonUtility.deNull(rs2.getString(1));
				varNbrG = CommonUtility.deNull(rs2.getString(2));
			}
			tesnValueObject.setGbCloseBjInd(CommonUtility.deNull(gbCloseBj));
			log.info("CHECKED CLOSE BJ-Disabled");// Bhuvana-29/09/2011
			/*
			 * if(gbCloseBj.equalsIgnoreCase("Y")) { log.info("Writing from Tesn.addCargo");
			 * log.info("THE BJ RECORD IS CLOSED NO MORE RECORDS CAN BE ADDED"); throw new
			 * BusinessException("M22602"); }
			 */
			sql3_1 = "SELECT TO_CHAR(SYSDATE,'Y/MM/DD') AS STRDATE FROM DUAL";
			String stresnasnnbr = "";
			String esnasnnbr = "";
			String strsqldate = "";


			log.info(" addRecordForDPE  DAO  SQL " + sql3_1);
			log.info(param + paramMap.getValues());
			for (rs3_1 = namedParameterJdbcTemplate.queryForRowSet(sql3_1, paramMap); rs3_1.next();)
				strsqldate = CommonUtility.deNull(rs3_1.getString(1));

			String strsqlyy = strsqldate.substring(0, 1);
			String strsqlmm = strsqldate.substring(2, 4);
			if ((strsqlyy + strsqlmm.substring(0, 1)).equals("00")// Bhuvana 15/09/2010
					|| (strsqlyy + strsqlmm.substring(0, 1)).equals("01")) { // For year ends with 0. ie. 2010, 2020,
																				// etc.
				sql3 = "SELECT MAX(ESN_ASN_NBR) FROM ESN WHERE ESN_ASN_NBR < 1300000";
			} else {
				// sql3 = "SELECT MAX(ESN_ASN_NBR) FROM ESN ";
				// eg. For 2011: Retrieve the max ESN No between ESN No 10000000 and 19999999.
				sql3 = "SELECT MAX(ESN_ASN_NBR) FROM ESN WHERE ESN_ASN_NBR BETWEEN (SELECT TO_NUMBER(TO_CHAR(SYSDATE,'Y')) * 10000000 FROM DUAL)  AND (SELECT ((TO_NUMBER(TO_CHAR(SYSDATE,'Y'))+1) * 10000000)-1 FROM DUAL)";
			}

			log.info(" addRecordForDPE  DAO  SQL " + sql3);
			log.info(param + paramMap.getValues());
			for (rs3 = namedParameterJdbcTemplate.queryForRowSet(sql3, paramMap); rs3.next();)
				stresnasnnbr = CommonUtility.deNull(rs3.getString(1));

			/*
			 * SqlRowSet rs3_1; for(rs3_1 = stmt.executeQuery(sql3_1); rs3_1.next();)
			 * strsqldate = CommonUtility.deNull(rs3_1.getString(1));
			 * 
			 * rs3_1.close();
			 */
			if (stresnasnnbr.equalsIgnoreCase(""))
				// stresnasnnbr = "00000002";
				stresnasnnbr = "00100000";
			if (stresnasnnbr.length() == 7)
				stresnasnnbr = "0".concat(stresnasnnbr);
			if (stresnasnnbr.length() == 6)
				stresnasnnbr = "00".concat(stresnasnnbr);
			if (stresnasnnbr.length() == 5)
				stresnasnnbr = "000".concat(stresnasnnbr);
			int intesnasnnbr = Integer.parseInt(stresnasnnbr.substring(3, 8));
			String stresnasnnbryy = stresnasnnbr.substring(0, 1);
			String stresnasnnbrmm = stresnasnnbr.substring(1, 3);

			if (stresnasnnbryy.equalsIgnoreCase(strsqlyy) && stresnasnnbrmm.equalsIgnoreCase(strsqlmm)) {
				stresnasnnbr = stresnasnnbryy.concat(stresnasnnbrmm);
				intesnasnnbr += 2;
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
					sqlasn = "select ESN_ASN_NBR from ESN where ESN_ASN_NBR in (:asnStr)";
					ArrayList<String> existAsnNbrs = new ArrayList<String>();
					paramMap.addValue("asnStr", asnNbrs);

					log.info(" addRecordForDPE  DAO  SQL " +sqlasn);
					log.info(param + paramMap.getValues());
					rsasn = namedParameterJdbcTemplate.queryForRowSet(sqlasn, paramMap);
					while (rsasn.next()) {
						dbAsnNbr = CommonUtility.deNull(rsasn.getString(1));
						existAsnNbrs.add(dbAsnNbr);
						log.info("SqlRowSet = " + dbAsnNbr);
					}
					asnNbrs.removeAll(existAsnNbrs);

					if (asnNbrs.size() > 0) {
						stresnasnnbr = asnNbrs.get(0);
						isValid = true;
						log.info("New ASN Nbr = " + stresnasnnbr);
					}
				}

			} else {
				stresnasnnbr = strsqlyy.concat(strsqlmm);
				stresnasnnbr = stresnasnnbr.concat("00002"); 

			}
			esnasnnbr = stresnasnnbr;
			log.info("GENERATED ESN ASN NBR :".concat(String.valueOf(String.valueOf(esnasnnbr))));
			// String sqledo = String.valueOf(String.valueOf((new StringBuffer("SELECT
			// NBR_PKGS,NOM_WT,NOM_VOL,TRANS_NBR_PKGS FROM GB_EDO WHERE EDO_ASN_NBR
			// ='")).append(edoAsnNbrG).append("'"))); // YJ 15 Jul 2009
//			String sqledo = "SELECT NBR_PKGS,NOM_WT,NOM_VOL,TRANS_NBR_PKGS,nvl(nbr_pkgs,0)-nvl(trans_nbr_pkgs,0)-nvl(DN_NBR_PKGS,0) balance FROM GB_EDO WHERE EDO_ASN_NBR = :edoAsnNbrG "; // YJ
			// Start #31377, update SQL to retrieve balance of weight and volume, NS June 2023
			sbNew.append("SELECT ");
			sbNew.append("(ed.nom_wt - NVL((SELECT SUM(tesn.NOM_WT) FROM TESN_JP_JP tesn, esn esn WHERE tesn.edo_asn_nbr = ");
			sbNew.append(":edoAsnNbrG AND tesn.ESN_ASN_NBR = esn.esn_asn_nbr AND esn_status = 'A' GROUP BY tesn.edo_asn_nbr), 0) ");
			sbNew.append("- NVL((SELECT SUM(nom_wt) FROM dn_details WHERE edo_asn_nbr = :edoAsnNbrG AND dn_status = 'A' AND TESN_ASN_NBR IS NULL GROUP BY edo_asn_nbr), 0) ");
			sbNew.append("- NVL((SELECT SUM(psa.NOM_WT) FROM TESN_JP_PSA psa, esn esn WHERE psa.edo_asn_nbr = :edoAsnNbrG AND psa.ESN_ASN_NBR = esn.esn_asn_nbr AND esn_status = 'A' ");
			sbNew.append("GROUP BY psa.edo_asn_nbr), 0)) edo_nom_wt, ");
			sbNew.append("(ed.nom_vol - NVL((SELECT SUM(tesn.NOM_VOL) FROM TESN_JP_JP tesn, esn esn WHERE tesn.edo_asn_nbr = :edoAsnNbrG AND tesn.ESN_ASN_NBR = esn.esn_asn_nbr ");
			sbNew.append("AND esn.esn_status = 'A' GROUP BY tesn.edo_asn_nbr), 0) - NVL((SELECT SUM(nom_vol) FROM dn_details WHERE edo_asn_nbr = :edoAsnNbrG AND dn_status = 'A' ");
			sbNew.append("AND TESN_ASN_NBR IS NULL GROUP BY edo_asn_nbr), 0) - NVL((SELECT SUM(psa.NOM_VOL) FROM TESN_JP_PSA psa, esn esn WHERE psa.edo_asn_nbr = :edoAsnNbrG ");
			sbNew.append("AND psa.ESN_ASN_NBR = esn.esn_asn_nbr AND esn.esn_status = 'A' GROUP BY psa.edo_asn_nbr), 0)) edo_nom_vol, ");
			sbNew.append("ed.trans_nbr_pkgs, ");
			sbNew.append("ed.nbr_pkgs-nvl(ed.CUT_OFF_NBR_PKGS, 0) AS edo_nbr_pkgs, ");
			sbNew.append("nvl(ed.nbr_pkgs, 0)-nvl(ed.trans_nbr_pkgs, 0)-nvl(ed.DN_NBR_PKGS, 0) balance ");
			sbNew.append("FROM ");
			sbNew.append("gb_edo ed ");
			sbNew.append("WHERE ");
			sbNew.append("ed.edo_asn_nbr = :edoAsnNbrG");																																							// 15
																																															// Jul
			String sqledo = sbNew.toString();																																											// 2009
			String edoNbrPkgsG = "0";
			String edoNomWtG = "0";
			String edoNomVolG = "0";
			String StrtransNbrPkgs = "0";
			int pkgBalance = 0;

			paramMap1.put("edoAsnNbrG",edoAsnNbrG);
			
			log.info(" addRecordForDPE  DAO  SQL " + sqledo);
			log.info(param + paramMap1);

			for (rsedo = namedParameterJdbcTemplate.queryForRowSet(sqledo, paramMap1); rsedo.next();) {
				edoNbrPkgsG = CommonUtility.deNull(rsedo.getString(4));
				edoNomWtG = CommonUtility.deNull(rsedo.getString(1));
				edoNomVolG = CommonUtility.deNull(rsedo.getString(2));
				StrtransNbrPkgs = CommonUtility.deNull(rsedo.getString(3));
				pkgBalance = rsedo.getInt(5);
			}
			// End #31377, update SQL to retrieve balance of weight and volume, NS June 2023
			log.info(
					"GOT EDO NBR PKGS :".concat(String.valueOf(String.valueOf(edoNbrPkgsG))) + " balane:" + pkgBalance);
			if (edoNbrPkgsG.equalsIgnoreCase(""))
				edoNbrPkgsG = "0";
			if (edoNomWtG.equalsIgnoreCase(""))
				edoNomWtG = "0";
			if (edoNomVolG.equalsIgnoreCase(""))
				edoNomVolG = "0";
			if (StrtransNbrPkgs.equalsIgnoreCase(""))
				StrtransNbrPkgs = "0";
			transNbrPkgsG = Integer.parseInt(StrtransNbrPkgs);
			int edoNbrPkgsInt = Integer.parseInt(edoNbrPkgsG);
			log.info("edoNbrPkgsInt = " + edoNbrPkgsInt);
			
			// if(noOfPkgs > edoNbrPkgsInt) { // YJ 15 Jul 2009
			if (noOfPkgs > pkgBalance) { // YJ 15 Jul 2009
				log.info("Writing from Tesn.addCargo");
				log.info(String.valueOf(String.valueOf(
						(new StringBuffer("NO. OF PACKAGES CANNOT BE MORE THAN EDO NO. OF PACKAGES AVAILABLE :"))
								.append(edoNbrPkgsG).append(": ENTERED :").append(noOfPkgs))));
				throw new BusinessException("No. of records cannot be more than EDO No. of packages.");
			}
			float edoNbrPkgsIntg = 0.0F;
			float edoNomWt = 0.0F;
			float edoNomVol = 0.0F;
			edoNbrPkgsIntg = Float.parseFloat(edoNbrPkgsG);
			edoNomWt = Float.parseFloat(edoNomWtG);
			edoNomVol = Float.parseFloat(edoNomVolG);
			float tesnNomWt = 0.0F;
			float tesnNomVol = 0.0F;
			// Start #31377, update calculation to get new weight and volume, NS June 2023
			tesnNomWt = ((float) noOfPkgs / pkgBalance) * edoNomWt;
			tesnNomVol = ((float) noOfPkgs / pkgBalance) * edoNomVol;
			// End #31377, update calculation to get new weight and volume, NS June 2023
			String tesnNomWtStr = "";
			String tesnNomVolStr = "";
			tesnNomWtStr = "".concat(String.valueOf(String.valueOf(nomWt)));
			tesnNomVolStr = "".concat(String.valueOf(String.valueOf(nomVol)));
			
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT ");
			sb.append("	INTO ");
			sb.append("	ESN(ESN_ASN_NBR, ");
			sb.append("	DECLARANT_CR_NO, ");
			sb.append("	BK_REF_NBR, ");
			sb.append("	TRANS_TYPE, ");
			sb.append("	IN_VOY_VAR_NBR, ");
			sb.append("	OUT_VOY_VAR_NBR, ");
			sb.append("	ESN_STATUS, ");
			sb.append("	ESN_CREATE_CD, ");
			sb.append("	LAST_MODIFY_USER_ID, ");
			sb.append("	LAST_MODIFY_DTTM, ");
			sb.append("	CARGO_CATEGORY_CD) ");
			sb.append("VALUES (:esnasnnbr, ");
			sb.append("'O', ");
			sb.append("'', ");
			sb.append("'B', ");
			sb.append(":varNbrG, ");
			sb.append("'', ");
			sb.append("'A', ");
			sb.append(":coCode, ");
			sb.append(":uid, ");
			sb.append("sysdate, ");
			sb.append(":category)");

			sql4 = sb.toString();

			paramMap.addValue("esnasnnbr", esnasnnbr);
			paramMap.addValue("varNbrG", varNbrG);
			paramMap.addValue("coCode", coCode);
			paramMap.addValue("uid", uid);
			paramMap.addValue("category", category);
			log.info(" addRecordForDPE  DAO  SQL " + sql4);
			log.info(param + paramMap.getValues());
			namedParameterJdbcTemplate.update(sql4, paramMap);
			// Added by Alvin for Audit Trial on 14/06/2004
			transNo = getMaxTransNo("ESN_TRANS", "ESN_ASN_NBR", esnasnnbr);
			sb = new StringBuffer();
			sb.append("INSERT ");
			sb.append("	INTO ");
			sb.append("	ESN_TRANS (ESN_ASN_NBR, ");
			sb.append("	TRANS_NBR, ");
			sb.append("	DECLARANT_CR_NO, ");
			sb.append("	BK_REF_NBR, ");
			sb.append("	TRANS_TYPE, ");
			sb.append("	ESN_STATUS, ");
			sb.append("	STUFF_IND, ");
			sb.append("	IN_VOY_VAR_NBR, ");
			sb.append("	OUT_VOY_VAR_NBR, ");
			sb.append("	ESN_CREATE_CD, ");
			sb.append("	LAST_MODIFY_USER_ID, ");
			sb.append("	LAST_MODIFY_DTTM) ");
			sb.append("VALUES (:esnasnnbr, ");
			sb.append(":transNo, ");
			sb.append("'O', ");
			sb.append("'', ");
			sb.append("'B', ");
			sb.append("'A', ");
			sb.append("'N', ");
			sb.append(":varNbrG, ");
			sb.append("'', ");
			sb.append(":coCode, ");
			sb.append(":uid, ");
			sb.append("sysdate)");

			transSql = sb.toString();
			paramMap.addValue("esnasnnbr", esnasnnbr);
			paramMap.addValue("transNo", transNo);
			paramMap.addValue("varNbrG", varNbrG);
			paramMap.addValue("coCode", coCode);
			paramMap.addValue("uid", uid);
			log.info(" addRecordForDPE  DAO  SQL " + transSql);
			log.info(param + paramMap.getValues());
			namedParameterJdbcTemplate.update(transSql, paramMap);

			log.info("INSERT INTO ESN TABLE");
			String shipperApp = "";
			shipperApp = GbmsCommonUtility.addApostr(shipper);
			StringBuffer sb1 = new StringBuffer();
			sb1.append("INSERT ");
			sb1.append("	INTO ");
			sb1.append("	TESN_JP_PSA(ESN_ASN_NBR, ");
			sb1.append("	EDO_ASN_NBR, ");
			sb1.append("	DIS_PORT, ");
			sb1.append("	SECOND_CAR_VOY_NBR, ");
			sb1.append("	SECOND_CAR_VES_NM, ");
			sb1.append("	SHIPPER_NM, ");
			sb1.append("	EDO_NBR_PKGS, ");
			sb1.append("	EDO_CARGO_TYPE, ");
			sb1.append("	NBR_PKGS, ");
			sb1.append("	NOM_WT, ");
			sb1.append("	NOM_VOL, ");
			sb1.append("	SEC_BK_REF_NBR, ");
			sb1.append("	SEC_ACCT_NBR) ");
			sb1.append("VALUES (:esnasnnbr, ");
			sb1.append(":edoAsnNbrG, ");
			sb1.append(":ps, ");
			sb1.append(":secCarvoy, ");
			sb1.append(":secCarves, ");
			sb1.append(":shipperApp, ");
			sb1.append(":edoNbrPkgsG, ");
			sb1.append("'A', ");
			sb1.append(":noOfPkgs, ");
			sb1.append(":tesnNomWtStr, ");
			sb1.append(":tesnNomVolStr, ");
			sb1.append(":bkref, ");
			sb1.append(":acctNbr)");

			sql5 = sb1.toString();
			paramMap.addValue("esnasnnbr", esnasnnbr);
			paramMap.addValue("edoAsnNbrG", edoAsnNbrG);
			paramMap.addValue("ps", ps);
			paramMap.addValue("secCarvoy", secCarvoy);
			paramMap.addValue("secCarves", secCarves);
			paramMap.addValue("shipperApp", shipperApp);
			paramMap.addValue("edoNbrPkgsG", edoNbrPkgsG);
			paramMap.addValue("noOfPkgs", noOfPkgs);
			paramMap.addValue("tesnNomWtStr", tesnNomWtStr);
			paramMap.addValue("tesnNomVolStr", tesnNomVolStr);
			paramMap.addValue("bkref", bkref);
			paramMap.addValue("acctNbr", acctNbr);
			log.info(" addRecordForDPE  DAO  SQL " + sql5);
			log.info(param + paramMap.getValues());
			namedParameterJdbcTemplate.update(sql5, paramMap);
			// Added by Alvin for Audit Trial on 14/06/2004
			transNo = getMaxTransNo( "TESN_JP_PSA_TRANS", "ESN_ASN_NBR", esnasnnbr);
			sb1 = new StringBuffer();
			sb1.append("INSERT ");
			sb1.append("	INTO ");
			sb1.append("	TESN_JP_PSA_TRANS(ESN_ASN_NBR, ");
			sb1.append("	EDO_ASN_NBR, ");
			sb1.append("	TRANS_NBR, ");
			sb1.append("	DIS_PORT, ");
			sb1.append("	SECOND_CAR_VOY_NBR, ");
			sb1.append("	SECOND_CAR_VES_NM, ");
			sb1.append("	SHIPPER_NM, ");
			sb1.append("	EDO_NBR_PKGS, ");
			sb1.append("	EDO_CARGO_TYPE, ");
			sb1.append("	NBR_PKGS, ");
			sb1.append("	NOM_WT, ");
			sb1.append("	NOM_VOL, ");
			sb1.append("	LAST_MODIFY_DTTM, ");
			sb1.append("	LAST_MODIFY_USER_ID) ");
			sb1.append("VALUES (:esnasnnbr, ");
			sb1.append(":edoAsnNbrG, ");
			sb1.append(":transNo, ");
			sb1.append(":ps, ");
			sb1.append(":secCarvoy, ");
			sb1.append(":secCarves, ");
			sb1.append(":shipperApp, ");
			sb1.append(":edoNbrPkgsG, ");
			sb1.append("'A', ");
			sb1.append(":noOfPkgs, ");
			sb1.append(":tesnNomWtStr, ");
			sb1.append(":tesnNomVolStr, ");
			sb1.append("sysdate, ");
			sb1.append(":uid) ");

			transSql = sb1.toString();

			paramMap.addValue("esnasnnbr", esnasnnbr);
			paramMap.addValue("edoAsnNbrG", edoAsnNbrG);
			paramMap.addValue("transNo", transNo);
			paramMap.addValue("ps", ps);
			paramMap.addValue("secCarvoy", secCarvoy);
			paramMap.addValue("secCarves", secCarves);
			paramMap.addValue("shipperApp", shipperApp);
			paramMap.addValue("edoNbrPkgsG", edoNbrPkgsG);
			paramMap.addValue("noOfPkgs", noOfPkgs);
			paramMap.addValue("tesnNomWtStr", tesnNomWtStr);
			paramMap.addValue("tesnNomVolStr", tesnNomVolStr);
			paramMap.addValue("uid", uid);
			log.info(" addRecordForDPE  DAO  SQL " + transSql);
			log.info(param + paramMap.getValues());
			namedParameterJdbcTemplate.update(transSql, paramMap);

			log.info("INSERT INTO TESN JP PSA TABLE");
			transNbrPkgsG += noOfPkgs;
			sql6 = "UPDATE GB_EDO SET TRANS_NBR_PKGS = :transNbrPkgsG WHERE EDO_ASN_NBR =:edoAsnNbrG ";
			paramMap.addValue("transNbrPkgsG", transNbrPkgsG);
			paramMap.addValue("edoAsnNbrG", edoAsnNbrG);

			log.info(" addRecordForDPE  DAO  SQL " + sql6);
			log.info(param + paramMap.getValues());
			namedParameterJdbcTemplate.update(sql6, paramMap);
			// Added by Alvin for Audit Trial on 14/06/2004
			transNo = getMaxTransNo("GB_EDO_TRANS", "EDO_ASN_NBR", edoAsnNbrG);
			StringBuffer sb2 = new StringBuffer();
			sb2.append("INSERT INTO GB_EDO_TRANS (EDO_ASN_NBR, TRANS_NBR, TRANS_NBR_PKGS, ");
			sb2.append(" LAST_MODIFY_DTTM, LAST_MODIFY_USER_ID ) VALUES ");
			sb2.append("(:edoAsnNbrG, :transNo,:transNbrPkgsG, sysdate,:uid)");

			transSql = sb2.toString();
			paramMap.addValue("edoAsnNbrG", edoAsnNbrG);
			paramMap.addValue("transNo", transNo);
			paramMap.addValue("transNbrPkgsG", transNbrPkgsG);
			paramMap.addValue("uid", uid);
			log.info(" addRecordForDPE  DAO  SQL " + transSql);
			log.info(param + paramMap.getValues());
			namedParameterJdbcTemplate.update(transSql, paramMap);
			log.info("UPDATE INTO GB_EDO TABLE");
			// conn.commit();
			sql5_1 = "SELECT * FROM TESN_JP_PSA WHERE ESN_ASN_NBR = :esnasnnbr";
			paramMap.addValue("esnasnnbr", esnasnnbr);

			log.info(" addRecordForDPE  DAO  SQL " + sql5_1);
			log.info(param + paramMap.getValues());
			for (rs5_1 = namedParameterJdbcTemplate.queryForRowSet(sql5_1, paramMap); rs5_1.next();) {
				tesnNomWtStr = CommonUtility.deNull(rs5_1.getString("NOM_WT"));
				tesnNomVolStr = CommonUtility.deNull(rs5_1.getString("NOM_VOL"));
			}

			tesnValueObject.setTesnAsnNbr(CommonUtility.deNull(esnasnnbr));
			tesnValueObject.setTesnNomWt(CommonUtility.deNull(tesnNomWtStr));
			tesnValueObject.setTesnNomVol(CommonUtility.deNull(tesnNomVolStr));
			tesnValueObject.setTransDnNbrPkgs(String.valueOf(transNbrPkgsG));
			addRecordList.add(tesnValueObject);

			log.info(" Result: addRecordForDPE  "+ addRecordList.toString());
		} catch (NullPointerException e) {
			log.info("Exception addRecordForDPE : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception addRecordForDPE : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception addRecordForDPE : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: addRecordForDPE  DAO  END");
		}
		return addRecordList;
	}

	/**
	 * This method checks ATU of second vessel.
	 * 
	 * @param vslNm     Vessel Name
	 * @param outVoyNbr Out Voy Nbr
	 * @return boolean
	 * @exception RemoteException
	 */
	public boolean chkDttmOfSecondCarrierVsl(String vslNm, String outVoyNbr) throws BusinessException {

		String sql;
		int count = 0;
		boolean result = false;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		sql = "SELECT count(*) FROM nominated_vsl VSL WHERE VSL_NM = :vslNm AND OUT_VOY_NBR = :outVoyNbr AND ATU_DTTM IS NULL AND REC_STATUS = 'A'";

		try {
			log.info("START: chkDttmOfSecondCarrierVsl  DAO  Start vslNm " + CommonUtility.deNull(vslNm) + " outVoyNbr" + CommonUtility.deNull(outVoyNbr));
			
			paramMap.put("vslNm", vslNm);
			paramMap.put("outVoyNbr", outVoyNbr);
			log.info(" chkDttmOfSecondCarrierVsl  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				count = rs.getInt(1);
				if (count > 0)
					result = true;
				else
					result = false;
			}
			log.info(" chkDttmOfSecondCarrierVsl  DAO  Result" + result);

		
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

	public boolean chkSecondCarrierVsl(String vslNm, String outVoyNbr) throws BusinessException {

		String sql;
		int count = 0;
		boolean result = false;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		sql = "SELECT count(*) FROM nominated_vsl VSL WHERE VSL_NM = :vslNm AND OUT_VOY_NBR = :outVoyNbr AND REC_STATUS = 'A'";

		try {
			log.info("START: chkSecondCarrierVsl  DAO  Start vslNm " + CommonUtility.deNull(vslNm) + " outVoyNbr" + CommonUtility.deNull(outVoyNbr));
			
			paramMap.put("vslNm", vslNm);
			paramMap.put("outVoyNbr", outVoyNbr);
			log.info(" chkSecondCarrierVsl  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				count = rs.getInt(1);
				if (count > 0)
					result = true;
				else
					result = false;
			} else
				result = false;

			log.info(" chkSecondCarrierVsl  DAO  Result" + result);
	
		} catch (NullPointerException e) {
			log.info("Exception chkSecondCarrierVsl : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkSecondCarrierVsl : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkSecondCarrierVsl  DAO  END");
		}
		return result;
	}

	/**
	 * This method retrieves the list of TESN-JP-PSA for a particular Vessel/Voyage.
	 *
	 * @param vvcode    Vessel Call ID
	 * @param coCode    Company Code
	 * @param ArrayList List of TESN-JP-PSA
	 * @exception BusinessException
	 * 
	 */
	public TableResult getTesnJpPsaList(String vvcode, String coCode, Criteria criteria) throws BusinessException {

		String sql = "";
		String esnAsnNbr = "";
		String nbrPkgs = "", nomWt = "", nomVol = "";
		String crgDes = "";
		StringBuffer sb = new StringBuffer();
		SqlRowSet rs = null;
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		TableResult tableResult = new TableResult();
		Map<String, String> paramMap = new HashMap<>();
		// VietNguyen (FPT) Document Process Enhancement 07-Jan-2014 : START
		if (coCode.equals("JP")) {

			sb.append("SELECT ");
			sb.append("	TE.ESN_ASN_NBR, ");
			sb.append("	TE.EDO_ASN_NBR, ");
			sb.append("	VES.TERMINAL, ");
			sb.append("	VES.COMBI_GC_OPS_IND, ");
			sb.append("	VES.COMBI_GC_SCHEME, ");
			sb.append("	VES.VSL_NM, ");
			sb.append("	VES.IN_VOY_NBR, ");
			sb.append("	TE.SECOND_CAR_VOY_NBR, ");
			sb.append("	TE.SECOND_CAR_VES_NM, ");
			sb.append("	TE.NBR_PKGS, ");
			// START - Added nom weight and vol to display in listing table - NS MAY 2023
			sb.append("	TE.NOM_WT, ");
			sb.append("	TE.NOM_VOL, ");
			// END - Added nom weight and vol to display in listing table - NS MAY 2023
			sb.append("	MFT.CRG_DES, ");
			sb.append("	to_char(E.LAST_MODIFY_DTTM, 'dd/mm/yyyy hh24:mi') AS LAST_MODIFY_DTTM, ");
			sb.append("	ad.user_name ESN_CREATE_CD, ");
			sb.append("	VES.SCHEME ");
			sb.append("FROM ");
			sb.append("	ESN E, ");
			sb.append("	TESN_JP_PSA TE, ");
			sb.append("	MANIFEST_DETAILS MFT, ");
			sb.append("	MFT_MARKINGS MARK, ");
			sb.append("	GB_EDO EDO, ");
			sb.append("	VESSEL_CALL VES, ");
			sb.append("	adm_user ad, ");
			sb.append("	( ");
			sb.append("	SELECT ");
			sb.append("		esn_asn_nbr, ");
			sb.append("		min(last_modify_user_id) last_modify_user_id, ");
			sb.append("		trans_nbr ");
			sb.append("	FROM ");
			sb.append("		TESN_JP_PSA_TRANS ");
			sb.append("	GROUP BY ");
			sb.append("		esn_asn_nbr, ");
			sb.append("		trans_nbr ");
			sb.append("	HAVING ");
			sb.append("		trans_nbr = 0 ) tmp ");
			sb.append("WHERE ");
			sb.append("	TE.ESN_ASN_NBR = E.ESN_ASN_NBR ");
			sb.append("	AND E.ESN_STATUS = 'A' ");
			sb.append("	AND EDO.MFT_SEQ_NBR = MFT.MFT_SEQ_NBR ");
			sb.append("	AND ad.user_acct(+) = tmp.last_modify_user_id ");
			sb.append("	AND tmp.esn_asn_nbr (+) = TE.esn_asn_nbr ");
			sb.append("	AND MARK.MFT_SQ_NBR = MFT.MFT_SEQ_NBR ");
			sb.append("	AND VES.VV_CD = E.IN_VOY_VAR_NBR ");
			sb.append("	AND EDO.VAR_NBR = :vvcode ");
			sb.append("	AND EDO.CRG_STATUS IN ('T', 'R') ");
			sb.append("	AND EDO.EDO_ASN_NBR = TE.EDO_ASN_NBR ");
			sb.append("ORDER BY ");
			sb.append("	TE.ESN_ASN_NBR ASC");

		} else {
			sb = new StringBuffer();
			sb.append("SELECT ");
			sb.append("	TE.ESN_ASN_NBR, ");
			sb.append("	TE.EDO_ASN_NBR, ");
			sb.append("	VES.TERMINAL, ");
			sb.append("	VES.COMBI_GC_OPS_IND, ");
			sb.append("	VES.COMBI_GC_SCHEME, ");
			sb.append("	VES.VSL_NM, ");
			sb.append("	VES.IN_VOY_NBR, ");
			sb.append("	TE.SECOND_CAR_VOY_NBR, ");
			sb.append("	TE.SECOND_CAR_VES_NM, ");
			sb.append("	TE.NBR_PKGS, ");
			// START - Added nom weight and vol to display in listing table - NS MAY 2023
			sb.append("	TE.NOM_WT, ");
			sb.append("	TE.NOM_VOL, ");
			// END - Added nom weight and vol to display in listing table - NS MAY 2023
			sb.append("	MFT.CRG_DES, ");
			sb.append("	to_char(E.LAST_MODIFY_DTTM, 'dd/mm/yyyy hh24:mi') AS LAST_MODIFY_DTTM, ");
			sb.append("	ad.user_name ESN_CREATE_CD, ");
			sb.append("	VES.SCHEME ");
			sb.append("FROM ");
			sb.append("	ESN E, ");
			sb.append("	TESN_JP_PSA TE, ");
			sb.append("	MANIFEST_DETAILS MFT, ");
			sb.append("	MFT_MARKINGS MARK, ");
			sb.append("	GB_EDO EDO, ");
			sb.append("	VESSEL_CALL VES, ");
			sb.append("	adm_user ad, ");
			sb.append("	( ");
			sb.append("	SELECT ");
			sb.append("		esn_asn_nbr, ");
			sb.append("		min(last_modify_user_id) last_modify_user_id, ");
			sb.append("		trans_nbr ");
			sb.append("	FROM ");
			sb.append("		TESN_JP_PSA_TRANS ");
			sb.append("	GROUP BY ");
			sb.append("		esn_asn_nbr, ");
			sb.append("		trans_nbr ");
			sb.append("	HAVING ");
			sb.append("		trans_nbr = 0 ) tmp ");
			sb.append("WHERE ");
			sb.append("	TE.ESN_ASN_NBR = E.ESN_ASN_NBR ");
			sb.append("	AND E.ESN_STATUS = 'A' ");
			sb.append("	AND EDO.MFT_SEQ_NBR = MFT.MFT_SEQ_NBR ");
			sb.append("	AND ad.user_acct(+) = tmp.last_modify_user_id ");
			sb.append("	AND tmp.esn_asn_nbr (+) = TE.esn_asn_nbr ");
			sb.append("	AND MARK.MFT_SQ_NBR = MFT.MFT_SEQ_NBR ");
			sb.append("	AND VES.VV_CD = E.IN_VOY_VAR_NBR ");
			sb.append("	AND EDO.VAR_NBR = :vvcode ");
			sb.append("	AND EDO.CRG_STATUS IN ('T', 'R') ");
			sb.append("	AND E.ESN_CREATE_CD = :coCode ");
			sb.append("	AND EDO.EDO_ASN_NBR = TE.EDO_ASN_NBR ");
			sb.append("ORDER BY ");
			sb.append("	TE.ESN_ASN_NBR ASC");
		}
		sql = sb.toString();
		try {
			log.info("START: getTesnJpPsaList  DAO  Start vvcode " + CommonUtility.deNull(vvcode) + " coCode" + CommonUtility.deNull(coCode) +" criteria:"+criteria.toString()) ;
			
			int recCount = 1;
			String nextRec = "";
			String prevRec = "";
			String inVesNm = "";
			String inVarNm = "";
			String secCar = "";
			String secVoy = "";
			String edoAsnNbr = "";

			String truckerNm = "";
			String lastModifyDttm = "";
			String createdBy = "";
			String scheme = "";
			String subScheme = "";
			String gcOperations = "";
			String terminal = "";
			TesnValueObject tesnValueObject = null;

			if (coCode.equals("JP")) {
				paramMap.put("vvcode", vvcode);

			} else {
				paramMap.put("vvcode", vvcode);
				paramMap.put("coCode", coCode);
			}

			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						paramMap, Integer.class));
			}

			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());

			}

			log.info(" getTesnJpPsaList  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			do {
				if (!rs.next())
					break;
				esnAsnNbr = CommonUtility.deNull(rs.getString("ESN_ASN_NBR"));
				edoAsnNbr = CommonUtility.deNull(rs.getString("EDO_ASN_NBR"));
				secVoy = CommonUtility.deNull(rs.getString("SECOND_CAR_VOY_NBR"));
				inVesNm = CommonUtility.deNull(rs.getString("VSL_NM"));
				inVarNm = CommonUtility.deNull(rs.getString("IN_VOY_NBR"));
				secCar = CommonUtility.deNull(rs.getString("SECOND_CAR_VES_NM"));
				nbrPkgs = CommonUtility.deNull(rs.getString("NBR_PKGS"));
				// START - Added nom weight and vol to display in listing table - NS MAY 2023
				nomWt = CommonUtility.deNull(rs.getString("NOM_WT"));
				nomVol = CommonUtility.deNull(rs.getString("NOM_VOL"));
				// END - Added nom weight and vol to display in listing table - NS MAY 2023
				crgDes = rs.getString("CRG_DES");
				// VietNguyen (FPT) Document Process Enhancement 07-Jan-2014 : START
				lastModifyDttm = CommonUtility.deNull(rs.getString("LAST_MODIFY_DTTM"));
				createdBy = CommonUtility.deNull(rs.getString("ESN_CREATE_CD"));
				scheme = CommonUtility.deNull(rs.getString("SCHEME"));
				subScheme = CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME"));
				gcOperations = CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND"));
				terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				// VietNguyen (FPT) Document Process Enhancement 07-Jan-2014 : END
				if (recCount == 1) {
					prevRec = esnAsnNbr;
					recCount++;
				} else {
					nextRec = esnAsnNbr;
					if (nextRec.equalsIgnoreCase(prevRec))
						continue;
					prevRec = nextRec;
				}
				tesnValueObject = new TesnValueObject();
				tesnValueObject.setEsnAsnNbr(esnAsnNbr);
				tesnValueObject.setEdoAsnNbr(edoAsnNbr);
				tesnValueObject.setInVslNm(inVesNm);
				tesnValueObject.setInVarNbr(inVarNm);
				tesnValueObject.setVslNm(secCar);
				tesnValueObject.setInVoyNbr(secVoy);
				tesnValueObject.setNbrPkgs(nbrPkgs);
				// START - Added nom weight and vol to display in listing table - NS MAY 2023
				tesnValueObject.setNomWt(nomWt);
				tesnValueObject.setNomVol(nomVol);
				// END - Added nom weight and vol to display in listing table - NS MAY 2023
				tesnValueObject.setCrgDes(crgDes);
				tesnValueObject.setVarNbr(vvcode);
				// VietNguyen (FPT) Document Process Enhancement 07-Jan-2014 : START
				tesnValueObject.setTruckerNm(truckerNm);
				tesnValueObject.setLastModifyDttm(lastModifyDttm);
				tesnValueObject.setCreatedBy(createdBy);
				tesnValueObject.setScheme(scheme);
				// VietNguyen (FPT) Document Process Enhancement 07-Jan-2014 : END
				tesnValueObject.setSubScheme(subScheme);
				tesnValueObject.setGcOperations(gcOperations);
				tesnValueObject.setTerminal(terminal);
				topsModel.put(tesnValueObject);
			} while (true);
			log.info(" getTesnJpPsaList  DAO  Result" + topsModel.toString());
	
		} catch (NullPointerException e) {
			log.info("Exception getTesnJpPsaList : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTesnJpPsaList : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTesnJpPsaList  DAO  END");
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
		}
		return tableResult;
	}

	public VesselVoyValueObject getVessel(String vesselName, String invoyNbr, String coCd) throws BusinessException {
		String custCd = coCd;
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		SqlRowSet rs = null;
		
		if (custCd.equals("JP")) {
			sb.append("SELECT ");
			sb.append("	DISTINCT VV_CD, ");
			sb.append("	VSL_NM, ");
			sb.append("	IN_VOY_NBR, ");
			sb.append("	TERMINAL ");
			sb.append("FROM ");
			sb.append("	esn e, ");
			sb.append("	TESN_JP_PSA te, ");
			sb.append("	vessel_call ves ");
			sb.append("WHERE ");
			sb.append("	VSL_NM = :vesselName ");
			sb.append("	AND IN_VOY_NBR = :invoyNbr ");
			sb.append("	AND e.trans_type = 'B' ");
			sb.append("	AND e.esn_status = 'A' ");
			sb.append("	AND e.in_voy_var_nbr = ves.vv_cd ");
			sb.append("	AND te.ESN_ASN_NBR = e.ESN_ASN_NBR ");
			sb.append("ORDER BY ");
			sb.append("	TERMINAL DESC, ");
			sb.append("	VSL_NM, ");
			sb.append("	IN_VOY_NBR");

		} else {
			sb = new StringBuffer();
			sb.append("SELECT ");
			sb.append("	DISTINCT VV_CD, ");
			sb.append("	VSL_NM, ");
			sb.append("	IN_VOY_NBR, ");
			sb.append("	TERMINAL ");
			sb.append("FROM ");
			sb.append("	esn e, ");
			sb.append("	TESN_JP_PSA te, ");
			sb.append("	vessel_call ves ");
			sb.append("WHERE ");
			sb.append("	VSL_NM =  :vesselName ");
			sb.append("	AND IN_VOY_NBR = :invoyNbr ");
			sb.append("	AND e.trans_type = 'B' ");
			sb.append("	AND ( e.ESN_CREATE_CD = :custCd ");
			sb.append("	OR ves.CREATE_CUST_CD = :custCd) ");
			sb.append("	AND e.esn_status = 'A' ");
			sb.append("	AND e.in_voy_var_nbr = ves.vv_cd ");
			sb.append("	AND te.ESN_ASN_NBR = e.ESN_ASN_NBR ");
			sb.append("ORDER BY ");
			sb.append("	TERMINAL DESC, ");
			sb.append("	VSL_NM, ");
			sb.append("	IN_VOY_NBR");

		}
		sql = sb.toString();
		List<VesselVoyValueObject> vesselList = new ArrayList<VesselVoyValueObject>();
		VesselVoyValueObject vesselVoyValueObject = null;
		try {
			log.info("START: getVessel  DAO  Start vesselName " + CommonUtility.deNull(vesselName) + " invoyNbr" + CommonUtility.deNull(invoyNbr) + " coCd" + CommonUtility.deNull(coCd));
			
			if (custCd.equals("JP")) {
				paramMap.put("vesselName", vesselName);
				paramMap.put("invoyNbr", invoyNbr);
			} else {
				paramMap.put("vesselName", vesselName);
				paramMap.put("invoyNbr", invoyNbr);
				paramMap.put("custCd", custCd);
			}

			log.info(" getVessel  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				vesselVoyValueObject = new VesselVoyValueObject();
				vesselVoyValueObject.setVoyNo(CommonUtility.deNull(rs.getString("IN_VOY_NBR")));
				vesselVoyValueObject.setVslName(CommonUtility.deNull(rs.getString("VSL_NM")));
				vesselVoyValueObject.setVarNbr(CommonUtility.deNull(rs.getString("VV_CD")));
				vesselVoyValueObject.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				vesselList.add(vesselVoyValueObject);

			}
			log.info(" getVessel  DAO  Result" + vesselList.toString());
		
		} catch (NullPointerException e) {
			log.info("Exception getVessel : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVessel : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVessel  DAO  END");
		}

		return vesselVoyValueObject;
	}

	public List<VesselVoyValueObject> getVesselVoy(String coCode) throws BusinessException {
		String sql = "";
		List<VesselVoyValueObject> voyList = new ArrayList<VesselVoyValueObject>();
		StringBuffer sb = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		
		if (coCode.equals("JP")) {
			sql = "SELECT DISTINCT IN_VOY_NBR,VSL_NM,VV_CD,TERMINAL FROM ESN E, VESSEL_CALL VES WHERE E.TRANS_TYPE='B' AND E.ESN_STATUS = 'A' AND E.IN_VOY_VAR_NBR = VES.VV_CD AND  (VES.VV_STATUS_IND='AP' OR VES.VV_STATUS_IND='PR' OR VES.VV_STATUS_IND='AL' OR VES.VV_STATUS_IND='BR' OR VES.VV_STATUS_IND='UB') ORDER BY TERMINAL DESC,VSL_NM,IN_VOY_NBR ASC";
		} else {
			sb.append(
					"SELECT DISTINCT IN_VOY_NBR,VSL_NM,VV_CD, TERMINAL FROM ESN E, VESSEL_CALL VES WHERE E.TRANS_TYPE = 'B' AND E.ESN_STATUS = 'A' AND E.IN_VOY_VAR_NBR = VES.VV_CD AND E.ESN_CREATE_CD = :coCode ");
			sb.append("AND (VES.VV_STATUS_IND='AP' OR VES.VV_STATUS_IND='PR' OR");
			sb.append(" VES.VV_STATUS_IND='AL' OR VES.VV_STATUS_IND='BR')");
			sb.append(" ORDER BY TERMINAL DESC,VSL_NM,IN_VOY_NBR ASC");
			sql = sb.toString();
		}
		try {
			log.info("START: getVesselVoy  DAO  Start coCode " + CommonUtility.deNull(coCode));
			
			if (!coCode.equals("JP")) {
				paramMap.put("coCode", coCode);
			}
			log.info(" getVesselVoy  DAO  SQL " + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			String voynbr = "";
			String vslName = "";
			String vv_cd = "";
			String terminal = "";
			VesselVoyValueObject vesselVoyValueObject = new VesselVoyValueObject();
			for (; rs.next(); voyList.add(vesselVoyValueObject)) {
				voynbr = CommonUtility.deNull(rs.getString("IN_VOY_NBR"));
				vslName = CommonUtility.deNull(rs.getString("VSL_NM"));
				vv_cd = CommonUtility.deNull(rs.getString("VV_CD"));
				terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				vesselVoyValueObject = new VesselVoyValueObject();
				vesselVoyValueObject.setVoyNo(CommonUtility.deNull(voynbr));
				vesselVoyValueObject.setVslName(CommonUtility.deNull(vslName));
				vesselVoyValueObject.setVarNbr(CommonUtility.deNull(vv_cd));
				vesselVoyValueObject.setTerminal(terminal);
			}

			log.info(" getVesselVoy  DAO  Result" + vesselVoyValueObject.toString());
	
		} catch (NullPointerException e) {
			log.info("Exception getVesselVoy : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVesselVoy : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselVoy  DAO  END");
		}

		return voyList;
	}

}
