package sg.com.jp.generalcargo.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.TesnPsaJpRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TesnPsaJpEsnListValueObject;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.DpeCommonUtil;
import sg.com.jp.generalcargo.util.GbmsCommonUtility;

@Repository("tesnPsaJpRepository")
public class TesnPsaJpJdbcRepository implements TesnPsaJpRepository {

	private static final Log log = LogFactory.getLog(TesnPsaJpJdbcRepository.class);
	private static final String param = " paramMap = ";
	public String logStatusGlobal = "Y";

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	// ejb.sessionBeans.gbms.cargo.tesn.tesnpsajp --> TesnPsaJpEJB

	@Override
	public String insertEsnDetailsForDPE(String UserID, String varno, String custCd, String bookingRefNo,
			String marking, String portD, String lopInd, String dgIn, String hsCode, int storageDay, String storageInd,
			String pkgsType, int noOfPkgs, double weight, double volume, String accNo, String payMode, String cargoDesc,
			String cntr1, String cntr2, String cntr3, String cntr4, String firstCName, String inVoyageNo,
			String stuffind, String category, String hsSubCodeFr, String hsSubCodeTo,
			List<TruckerValueObject> truckerList, String deliveryToEPC, String customHsCode, 
			List<HsCodeDetails> multiHsCodeList) throws BusinessException {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		String strInsert = new String();
		SqlRowSet rs1 = null;
		String strMark = new String();
		String strEsnDetails = new String();
		String strUpdate = new String();
		String strCntr1 = new String();
		String strCntr2 = new String();
		String strCntr3 = new String();
		String strCntr4 = new String();
		String sqlTrans = "";
		String strInsertTrans = new String();
		String strMarkTrans = new String();
		String strCntr1Trans = new String();
		String strCntr2Trans = new String();
		String strCntr3Trans = new String();
		String strCntr4Trans = new String();
		String strEsnDetailsTrans = new String();
		String transNumEsnStr = "";
		// Added by Revathi
		String sqllog = "";
		String strESNTransNbr = "";
		String strMarkTransNbr = "";
		String strBKTransNbr = "";

		int transNumEsnInt = 0;
		String esnDeclrCd = "";
		if (custCd.equals("JP"))
			esnDeclrCd = getDeclarant(bookingRefNo);
		else
			esnDeclrCd = custCd;

		String esnNo = getEsnNoForDPE();
		// String tdbCrNo = getTdbCRNo(custCd);
		String blNbr = getEdiBlNbr(bookingRefNo);
		// Added by VietNguyen
		String truckerIc = "";
		String truckerNm = "";
		String truckerContact = "";
		String truckerPkgs = null;
		String truckerCd = "";

		// Added ny MCconsulting , deliveryToEPC set to 'N' if deliveryToEPC is null or
		// empty.
		deliveryToEPC = (deliveryToEPC == null || deliveryToEPC.equalsIgnoreCase("null")
				|| deliveryToEPC.trim().length() == 0) ? "N" : deliveryToEPC;

		if (truckerList != null && truckerList.size() > 0) {
			TruckerValueObject trucker = truckerList.get(0);
			if (trucker != null) {
				truckerIc = StringUtils.upperCase(trucker.getTruckerIc());
				truckerNm = trucker.getTruckerNm();
				truckerContact = trucker.getTruckerContact();
				truckerPkgs = trucker.getTruckerPkgs();
				truckerCd = trucker.getTruckerCd();
			}
		}

		try {
			log.info("START: insertEsnDetailsForDPE  DAO  Start Obj " + " UserID:" + UserID + " varno:" + varno
					+ " custCd:" + custCd + " bookingRefNo:" + bookingRefNo + " marking:" + marking + " portD:" + portD
					+ " lopInd:" + lopInd + " dgIn:" + dgIn + " hsCode:" + hsCode + " storageDay:" + storageDay
					+ " storageInd:" + storageInd + " pkgsType:" + pkgsType + " noOfPkgs:" + noOfPkgs + " weight:"
					+ weight + " volume:" + volume + " accNo:" + accNo + " payMode:" + payMode + " cargoDesc:"
					+ cargoDesc + " cntr1:" + cntr1 + " cntr2:" + cntr2 + " cntr3:" + cntr3 + " cntr4:" + cntr4
					+ " firstCName:" + firstCName + " inVoyageNo:" + inVoyageNo + " stuffind:" + stuffind + " category:"
					+ category + " hsSubCodeFr:" + hsSubCodeFr + " hsSubCodeTo:" + hsSubCodeTo + " truckerList:"
					+ truckerList + " deliveryToEPC:" + deliveryToEPC + ",customHsCode:" + customHsCode + ", multiHsCodeList : " + multiHsCodeList.toString());

			sqlTrans = "SELECT MAX(TRANS_NBR) FROM TESN_PSA_JP_TRANS WHERE ESN_ASN_NBR = :esnNo ";

			if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y") {

				log.info(" *** insertEsnDetailsForDPE SQL *****" + sqlTrans);

				paramMap.put("esnNo", esnNo);
				log.info(param + paramMap);
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqlTrans, paramMap);
				while (rs1.next()) {
					transNumEsnStr = rs1.getString(1);
				}

				if (transNumEsnStr == null || transNumEsnStr == "") {
					transNumEsnInt = 0;
				} else {
					transNumEsnInt = Integer.parseInt(transNumEsnStr);
					transNumEsnInt++;
				}

				sqllog = "SELECT MAX(TRANS_NBR) FROM esn_trans WHERE ESN_ASN_NBR=:esnNo ";

				rs1 = null;
				log.info(" *** insertEsnDetailsForDPE SQL *****" + sqllog);

				paramMap.put("esnNo", esnNo);
				log.info(param + paramMap);
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
				while (rs1.next()) {
					strESNTransNbr = CommonUtility.deNull(rs1.getString(1));
				}

				if (strESNTransNbr.equalsIgnoreCase("")) {
					strESNTransNbr = "0";
				} else {
					strESNTransNbr = String.valueOf(Integer.parseInt(strESNTransNbr) + 1);
				}

				sqllog = null;
				sqllog = "SELECT MAX(TRANS_NBR) FROM esn_markings_trans WHERE ESN_ASN_NBR=:esnNo ";

				rs1 = null;
				log.info(" *** insertEsnDetailsForDPE SQL *****" + sqllog);

				paramMap.put("esnNo", esnNo);
				log.info(param + paramMap);
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
				while (rs1.next()) {
					strMarkTransNbr = CommonUtility.deNull(rs1.getString(1));
				}

				if (strMarkTransNbr.equalsIgnoreCase("")) {
					strMarkTransNbr = "0";
				} else {
					strMarkTransNbr = String.valueOf(Integer.parseInt(strMarkTransNbr) + 1);
				}

				sqllog = "";
				sqllog = "SELECT MAX(TRANS_NBR) FROM bk_details_trans WHERE BK_REF_NBR=:bookingRefNo ";

				rs1 = null;
				log.info(" *** insertEsnDetailsForDPE SQL *****" + sqllog);

				paramMap.put("bookingRefNo", bookingRefNo);
				log.info(param + paramMap);
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
				while (rs1.next()) {
					strBKTransNbr = CommonUtility.deNull(rs1.getString(1));
				}

				if (strBKTransNbr.equalsIgnoreCase("")) {
					strBKTransNbr = "0";
				} else {
					strBKTransNbr = String.valueOf(Integer.parseInt(strBKTransNbr) + 1);
				}

				StringBuffer sb = new StringBuffer();
				sb.append(
						"INSERT INTO ESN_Trans(ESN_ASN_NBR,TRANS_NBR,DECLARANT_CR_NO,BK_REF_NBR,TRANS_TYPE,OUT_VOY_VAR_NBR,ESN_STATUS,ESN_CREATE_CD,STUFF_IND,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,EPC_IND)");
				sb.append(
						"VALUES(:esnNo,:strESNTransNbr,'O',:bookingRefNo,'C',:varno,'A',:esnDeclrCd,:stuffind,:UserID,sysdate,:deliveryToEPC)");

				strInsertTrans = sb.toString();

				log.info(" *** insertEsnDetailsForDPE SQL *****" + strInsertTrans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("strESNTransNbr", strESNTransNbr);
				paramMap.put("bookingRefNo", bookingRefNo);
				paramMap.put("varno", varno);
				paramMap.put("esnDeclrCd", esnDeclrCd);
				paramMap.put("stuffind", stuffind);
				paramMap.put("UserID", UserID);
				paramMap.put("deliveryToEPC", deliveryToEPC);
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strInsertTrans, paramMap);

				sb = new StringBuffer();

				sb.append(
						"INSERT INTO TESN_PSA_JP_TRANS(ESN_ASN_NBR,TRANS_NBR,DIS_PORT,HS_CD,PKG_TYPE,CRG_DES,NBR_PKGS,GROSS_WT,GROSS_VOL,DG_IND,STORAGE_IND,STORAGE_DAYS,OPS_IND,FIRST_CAR_VES_NM,FIRST_CAR_VOY_NBR,ACCT_NBR,PAYMENT_MODE,LAST_MODIFY_USER_ID,lAST_MODIFY_DTTM,");
				sb.append("TRUCKER_NM, TRUCKER_IC, TRUCKER_NBR_PKGS, TRUCKER_CONTACT_NBR, TRUCKER_CO_CD, CUSTOM_HS_CODE )");
				sb.append(
						"VALUES(:esnNo,:transNumEsnInt,:portD,:hsCode,:pkgsType,:cargoDesc,:noOfPkgs,:weight,:volume,  ");
				sb.append(
						":dgIn,:storageInd,:storageDay,:lopInd,:firstCName,:inVoyageNo,:accNo,:payMode,:UserID,sysdate, ");
				sb.append(":truckerNm,:truckerIc,:truckerPkgs,:truckerContact,:truckerCd, :customHsCode) ");

				strEsnDetailsTrans = sb.toString();

				log.info(" *** insertEsnDetailsForDPE SQL *****" + strEsnDetailsTrans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
				paramMap.put("portD", portD);
				paramMap.put("hsCode", hsCode);
				paramMap.put("pkgsType", pkgsType);
				paramMap.put("cargoDesc", GbmsCommonUtility.addApostr(cargoDesc));
				paramMap.put("noOfPkgs", Integer.toString(noOfPkgs));
				paramMap.put("weight", Double.toString(weight));
				paramMap.put("volume", Double.toString(volume));
				paramMap.put("dgIn", dgIn);
				paramMap.put("storageInd", storageInd);
				paramMap.put("storageDay", Integer.toString(storageDay));
				paramMap.put("lopInd", lopInd);
				paramMap.put("firstCName", GbmsCommonUtility.addApostr(firstCName));
				paramMap.put("inVoyageNo", GbmsCommonUtility.addApostr(inVoyageNo));
				paramMap.put("accNo", GbmsCommonUtility.addApostr(accNo));
				paramMap.put("payMode", payMode);
				paramMap.put("UserID", UserID);
				paramMap.put("truckerNm", truckerNm);
				paramMap.put("truckerIc", truckerIc);
				paramMap.put("truckerPkgs", truckerPkgs);
				paramMap.put("truckerContact", truckerContact);
				paramMap.put("truckerCd", truckerCd);
				paramMap.put("customHsCode", customHsCode);
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strEsnDetailsTrans, paramMap);

				sb = new StringBuffer();

				sb.append(
						"INSERT INTO ESN_MARKINGS_Trans(ESN_ASN_NBR,TRANS_NBR,MARKINGS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
				sb.append("VALUES(:esnNo,:strMarkTransNbr,:marking,:UserID,sysdate) ");

				strMarkTrans = sb.toString();

				log.info(" *** insertEsnDetailsForDPE SQL *****" + strMarkTrans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("strMarkTransNbr", strMarkTransNbr);
				paramMap.put("marking", GbmsCommonUtility.addApostr(marking));
				paramMap.put("UserID", UserID);
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strMarkTrans, paramMap);

				sb = new StringBuffer();

				sb.append(
						"INSERT INTO BK_DETAILS_Trans(TRANS_NBR,bk_ref_nbr,ESN_DECLARED,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
				sb.append("VALUES(:strBKTransNbr,:bookingRefNo,'Y',:UserID,sysdate) ");

				String strBKTrans = sb.toString();

				log.info(" *** insertEsnDetailsForDPE SQL *****" + strBKTrans);

				paramMap.put("strBKTransNbr", strBKTransNbr);
				paramMap.put("bookingRefNo", bookingRefNo);
				paramMap.put("UserID", UserID);
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strBKTrans, paramMap);

			}

			StringBuffer sb = new StringBuffer();

			sb.append(
					"INSERT INTO ESN(ESN_ASN_NBR,DECLARANT_CR_NO,BK_REF_NBR,TRANS_TYPE,OUT_VOY_VAR_NBR,ESN_STATUS,ESN_CREATE_CD,STUFF_IND,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,CARGO_CATEGORY_CD,EPC_IND) ");
			sb.append(
					"VALUES(:esnNo,'0',:bookingRefNo,'C',:varno,'A',:esnDeclrCd,:stuffind,:UserID,sysdate,:category,:deliveryToEPC)");

			strInsert = sb.toString();
			log.info(" ***strInsert insertEsnDetailsForDPE SQL *****" + strInsert);

			paramMap.put("esnNo", esnNo);
			paramMap.put("bookingRefNo", bookingRefNo);
			paramMap.put("varno", varno);
			paramMap.put("esnDeclrCd", esnDeclrCd);
			paramMap.put("stuffind", stuffind);
			paramMap.put("UserID", UserID);
			paramMap.put("category", category);
			paramMap.put("deliveryToEPC", deliveryToEPC);
			log.info(param + paramMap);
			int count = namedParameterJdbcTemplate.update(strInsert, paramMap);

			sb = new StringBuffer();

			sb.append(
					"INSERT INTO TESN_PSA_JP(ESN_ASN_NBR,DIS_PORT,HS_CD,PKG_TYPE,CRG_DES,NBR_PKGS,GROSS_WT,GROSS_VOL,DG_IND,STORAGE_IND,STORAGE_DAYS,OPS_IND,FIRST_CAR_VES_NM,FIRST_CAR_VOY_NBR,PAYMENT_MODE,ACCT_NBR,BL_NBR, HS_SUB_CODE_FR, HS_SUB_CODE_TO, ");
			sb.append("TRUCKER_NM, TRUCKER_IC, TRUCKER_NBR_PKGS, TRUCKER_CONTACT_NBR, TRUCKER_CO_CD, CUSTOM_HS_CODE )");
			sb.append(
					"VALUES(:esnNo,:portD,:hsCode,:pkgsType,:cargoDesc,:noOfPkgs,:weight,:volume,:dgIn,:storageInd, ");
			sb.append(
					":storageDay,:lopInd,:firstCName,:inVoyageNo,:payMode,:accNo,:blNbr,:hsSubCodeFr,:hsSubCodeTo,:truckerNm,:truckerIc,:truckerPkgs,:truckerContact,:truckerCd, :customHsCode) ");

			strEsnDetails = sb.toString();
			log.info(" ***strEsnDetails insertEsnDetailsForDPE SQL *****" + strEsnDetails);

			paramMap.put("esnNo", esnNo);
			paramMap.put("portD", portD);
			paramMap.put("hsCode", hsCode);
			paramMap.put("pkgsType", pkgsType);
			paramMap.put("cargoDesc", GbmsCommonUtility.addApostr(cargoDesc));
			paramMap.put("noOfPkgs", Integer.toString(noOfPkgs));
			paramMap.put("weight", Double.toString(weight));
			paramMap.put("volume", Double.toString(volume));
			paramMap.put("dgIn", dgIn);
			paramMap.put("storageInd", storageInd);
			paramMap.put("storageDay", Integer.toString(storageDay));
			paramMap.put("lopInd", lopInd);
			paramMap.put("firstCName", GbmsCommonUtility.addApostr(firstCName));
			paramMap.put("inVoyageNo", GbmsCommonUtility.addApostr(inVoyageNo));
			paramMap.put("payMode", payMode);
			paramMap.put("accNo", GbmsCommonUtility.addApostr(accNo));
			paramMap.put("blNbr", blNbr);
			paramMap.put("hsSubCodeFr", hsSubCodeFr);
			paramMap.put("hsSubCodeTo", hsSubCodeTo);
			paramMap.put("truckerNm", truckerNm);
			paramMap.put("truckerIc", truckerIc);
			paramMap.put("truckerPkgs", truckerPkgs);
			paramMap.put("truckerContact", truckerContact);
			paramMap.put("truckerCd", truckerCd);
			paramMap.put("customHsCode", customHsCode);
			log.info(param + paramMap);
			int cntEsnDetails = namedParameterJdbcTemplate.update(strEsnDetails, paramMap);
			
			if(cntEsnDetails>0) {
				// START CR FTZ - NS JULY 2024
				for (HsCodeDetails hsCodeObj : multiHsCodeList) {					

					// get MFT_HSCODE_SEQ_NBR 
					StringBuilder sbSeq = new StringBuilder();
					sbSeq.append("SELECT GBMS.SEQ_TESN_HSCODE_SEQ_NBR.nextval AS seqVal FROM DUAL");
					Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(), new HashMap<String, String>());
					BigDecimal seqValue = (BigDecimal) results.get("seqVal");
					// end
					
					StringBuilder sbhscode = new StringBuilder();
					sbhscode.append(" INSERT INTO GBMS.TESN_PSA_JP_HSCODE_DETAILS  ");
					sbhscode.append(" (ESN_ASN_NBR,TESN_HSCODE_SEQ_NBR,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,CUSTOM_HS_CODE,CRG_DES, HS_SUB_CODE_DESC,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
					sbhscode.append(" VALUES(:ESN_ASN_NBR,:TESN_HSCODE_SEQ_NBR,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:NBR_PKGS,:GROSS_WT,:GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
					paramMap.put("ESN_ASN_NBR", esnNo);
					paramMap.put("TESN_HSCODE_SEQ_NBR", seqValue);
					paramMap.put("HS_CODE",hsCodeObj.getHsCode());
					paramMap.put("HS_SUB_CODE_FR",hsCodeObj.getHsSubCodeFr());
					paramMap.put("HS_SUB_CODE_TO",hsCodeObj.getHsSubCodeTo());
					paramMap.put("NBR_PKGS",hsCodeObj.getNbrPkgs());
					paramMap.put("GROSS_WT",hsCodeObj.getGrossWt());
					paramMap.put("GROSS_VOL",hsCodeObj.getGrossVol());
					paramMap.put("CUSTOM_HS_CODE",hsCodeObj.getCustomHsCode());
					paramMap.put("CRG_DES",hsCodeObj.getCrgDes());	
					paramMap.put("HS_SUB_CODE_DESC",hsCodeObj.getHsSubCodeDesc());	
					paramMap.put("userId", UserID);
					log.info("SQL" + sbhscode.toString());
					int counths = namedParameterJdbcTemplate.update(sbhscode.toString(), paramMap);
					log.info("counths : " + counths);
					
					sbhscode.setLength(0);
					sbhscode.append(" INSERT INTO GBMS.TESN_PSA_JP_HSCODE_DETAILS_TRANS  ");
					sbhscode.append(" (ESN_ASN_NBR, TESN_HSCODE_SEQ_NBR, AUDIT_DTTM, REC_STATUS, HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,CUSTOM_HS_CODE,CRG_DES, HS_SUB_CODE_DESC,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
					sbhscode.append(" VALUES(:ESN_ASN_NBR,:TESN_HSCODE_SEQ_NBR, SYSDATE, 'A',:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:NBR_PKGS,:GROSS_WT,:GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
					paramMap.put("ESN_ASN_NBR", esnNo);
					paramMap.put("TESN_HSCODE_SEQ_NBR", seqValue);
					paramMap.put("HS_CODE",hsCodeObj.getHsCode());
					paramMap.put("HS_SUB_CODE_FR",hsCodeObj.getHsSubCodeFr());
					paramMap.put("HS_SUB_CODE_TO",hsCodeObj.getHsSubCodeTo());
					paramMap.put("NBR_PKGS",hsCodeObj.getNbrPkgs());
					paramMap.put("GROSS_WT",hsCodeObj.getGrossWt());
					paramMap.put("GROSS_VOL",hsCodeObj.getGrossVol());
					paramMap.put("CUSTOM_HS_CODE",hsCodeObj.getCustomHsCode());
					paramMap.put("CRG_DES",hsCodeObj.getCrgDes());	
					paramMap.put("HS_SUB_CODE_DESC",hsCodeObj.getHsSubCodeDesc());	
					paramMap.put("userId", UserID);
					log.info("SQL" + sbhscode.toString());
					int counthsAudit = namedParameterJdbcTemplate.update(sbhscode.toString(), paramMap);
					log.info("counths : " + counthsAudit);
				}
				// END CR FTZ - NS JULY 2024
			}
			

			strMark = "INSERT INTO ESN_MARKINGS(ESN_ASN_NBR,MARKINGS)VALUES(:esnNo,:marking)";

			strEsnDetails = sb.toString();
			log.info(" ***strMark insertEsnDetailsForDPE SQL *****" + strMark);

			paramMap.put("esnNo", esnNo);
			paramMap.put("marking", GbmsCommonUtility.addApostr(marking));
			log.info(param + paramMap);
			int cntmrk = namedParameterJdbcTemplate.update(strMark, paramMap);

			sb = new StringBuffer();

			sb.append("UPDATE BK_DETAILS SET ESN_DECLARED= 'Y',LAST_MODIFY_DTTM=");
			sb.append("SYSDATE, LAST_MODIFY_USER_ID=:UserID WHERE BK_REF_NBR=:bookingRefNo ");
			strUpdate = sb.toString();

			log.info(" ***strUpdate insertEsnDetailsForDPE SQL *****" + strUpdate);

			paramMap.put("UserID", UserID);
			paramMap.put("bookingRefNo", bookingRefNo);
			log.info(param + paramMap);
			int cntUpdate = namedParameterJdbcTemplate.update(strUpdate, paramMap);
			String strSubAdp = "";
			Integer adpSeq = 1;
			int cntAdp = 1;
			if (truckerList != null && truckerList.size() > 1) {
				sqllog = "";
				sqllog = "SELECT MAX(SUB_ADP_NBR) FROM sub_adp";

				log.info(" ***strUpdate insertEsnDetailsForDPE SQL *****" + sqllog);
				log.info(param + paramMap);
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
				while (rs1.next()) {
					adpSeq = rs1.getInt(1);
				}

				if (adpSeq != null) {
					adpSeq = adpSeq + 1;
				}

				for (int i = 1; i < truckerList.size(); i++) {
					TruckerValueObject trucker = truckerList.get(i);
					if (trucker != null) {

						sb = new StringBuffer();

						sb.append(
								"INSERT INTO sub_adp(SUB_ADP_NBR, STATUS_CD, ESN_ASN_NBR,TRUCKER_IC,TRUCKER_NM,TRUCKER_CONTACT_NBR, ");
						sb.append(
								"TRUCKER_NBR_PKGS,EDO_ESN_IND,CREATE_USER_ID,CREATE_DTTM,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,TRUCKER_CO_CD) ");
						sb.append(
								"VALUES(:adpSeq, 'A',:esnNo,:truckerIc,:truckerNm,:truckerContact,:truckerPkgs,'0',:UserID, sysdate,:UserID, sysdate,:truckerCd) ");

						strSubAdp = sb.toString();

						log.info(" ***Sql Sub Adp table insertEsnDetailsForDPE SQL *****" + strSubAdp);

						paramMap.put("adpSeq", Integer.toString(adpSeq));
						paramMap.put("esnNo", esnNo);
						paramMap.put("truckerIc", StringUtils.upperCase(trucker.getTruckerIc()));
						paramMap.put("truckerNm", trucker.getTruckerNm());
						paramMap.put("truckerContact", trucker.getTruckerContact());
						paramMap.put("truckerPkgs", trucker.getTruckerPkgs());
						paramMap.put("UserID", UserID);
						paramMap.put("truckerCd", trucker.getTruckerCd());
						log.info(param + paramMap);
						cntAdp = namedParameterJdbcTemplate.update(strSubAdp, paramMap);
						if (cntAdp == 0) {
							break;
						}
						sb = new StringBuffer();
						strSubAdp = "";

						sb.append(
								"INSERT INTO sub_adp_txn(SUB_ADP_NBR, STATUS_CD, TRUCKER_CO_CD, TRUCKER_IC,TRUCKER_NM,TRUCKER_CONTACT_NBR ");
						sb.append(",TRUCKER_NBR_PKGS,EDO_ESN_IND,TXN_USER_ID,TXN_DTTM) ");
						sb.append(
								"VALUES(:adpSeq, 'A',:truckerCd,:truckerIc,:truckerNm,:truckerContact,:truckerPkgs, '0',:UserID, sysdate) ");

						strSubAdp = sb.toString();

						paramMap.put("adpSeq", Integer.toString(adpSeq));
						paramMap.put("esnNo", esnNo);
						paramMap.put("truckerIc", StringUtils.upperCase(trucker.getTruckerIc()));
						paramMap.put("truckerNm", trucker.getTruckerNm());
						paramMap.put("truckerContact", trucker.getTruckerContact());
						paramMap.put("truckerPkgs", trucker.getTruckerPkgs());
						paramMap.put("UserID", UserID);
						paramMap.put("truckerCd", trucker.getTruckerCd());
						log.info(param + paramMap);
						cntAdp = namedParameterJdbcTemplate.update(strSubAdp, paramMap);
						if (cntAdp == 0) {
							break;
						}
						adpSeq++;
					}
				}
			}

			strCntr1 = "INSERT INTO esn_cntr(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES('1',:esnNo,:cntr1 )";
			strCntr2 = "INSERT INTO esn_cntr(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES('2',:esnNo,:cntr2 )";
			strCntr3 = "INSERT INTO esn_cntr(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES('3',:esnNo,:cntr3 )";
			strCntr4 = "INSERT INTO esn_cntr(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES('4',:esnNo,:cntr4 )";

			strCntr1Trans = "INSERT INTO esn_cntr_Trans(ESN_CNTR_SEQ,ESN_ASN_NBR,TRANS_NBR,CNTR_NBR) VALUES('1',:esnNo,:transNumEsnInt,:cntr1 )";
			strCntr2Trans = "INSERT INTO esn_cntr_Trans(ESN_CNTR_SEQ,ESN_ASN_NBR,TRANS_NBR,CNTR_NBR) VALUES('2',:esnNo,:transNumEsnInt,:cntr2)";
			strCntr3Trans = "INSERT INTO esn_cntr_Trans(ESN_CNTR_SEQ,ESN_ASN_NBR,TRANS_NBR,CNTR_NBR) VALUES('3',:esnNo,:transNumEsnInt,:cntr3)";
			strCntr4Trans = "INSERT INTO esn_cntr_Trans(ESN_CNTR_SEQ,ESN_ASN_NBR,TRANS_NBR,CNTR_NBR) VALUES('4',:esnNo,:transNumEsnInt,:cntr4)";

			if (cntr1 != null && !cntr1.equals("")) {
				log.info(" *** insertEsnDetailsForDPE SQL *****" + strCntr1);

				paramMap.put("esnNo", esnNo);
				paramMap.put("cntr1", GbmsCommonUtility.addApostr(cntr1));
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strCntr1, paramMap);
				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y")

					log.info(" *** insertEsnDetailsForDPE SQL *****" + strCntr1Trans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
				paramMap.put("cntr1", GbmsCommonUtility.addApostr(cntr1));
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strCntr1Trans, paramMap);
			}
			if (cntr2 != null && !cntr2.equals("")) {

				log.info(" *** insertEsnDetailsForDPE SQL *****" + strCntr2);

				paramMap.put("esnNo", esnNo);
				paramMap.put("cntr2", GbmsCommonUtility.addApostr(cntr2));
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strCntr2, paramMap);

				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y")

					log.info(" *** insertEsnDetailsForDPE SQL *****" + strCntr2Trans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
				paramMap.put("cntr2", GbmsCommonUtility.addApostr(cntr2));
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strCntr2Trans, paramMap);
			}
			if (cntr3 != null && !cntr3.equals("")) {

				log.info(" *** insertEsnDetailsForDPE SQL *****" + strCntr3);

				paramMap.put("esnNo", esnNo);
				paramMap.put("cntr3", GbmsCommonUtility.addApostr(cntr3));
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strCntr3, paramMap);

				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y")

					log.info(" *** insertEsnDetailsForDPE SQL *****" + strCntr3Trans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
				paramMap.put("cntr3", GbmsCommonUtility.addApostr(cntr3));
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strCntr3Trans, paramMap);
			}
			if (cntr4 != null && !cntr4.equals("")) {

				log.info(" *** insertEsnDetailsForDPE SQL *****" + strCntr4);

				paramMap.put("esnNo", esnNo);
				paramMap.put("cntr4", GbmsCommonUtility.addApostr(cntr4));
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strCntr4, paramMap);

				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y")

					log.info(" *** insertEsnDetailsForDPE SQL *****" + strCntr4Trans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
				paramMap.put("cntr4", GbmsCommonUtility.addApostr(cntr4));
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strCntr4Trans, paramMap);
			}
			if (count == 0 || cntEsnDetails == 0 || cntUpdate == 0 || cntmrk == 0 || cntAdp == 0) {
				log.info("Writing from Esn.insertEsnDetails");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			log.info("END: *** insertEsnDetailsForDPE Result *****" + esnNo.toString());

		} catch (NullPointerException ne) {
			log.info("Exception insertEsnDetailsForDPE : ", ne);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception insertEsnDetailsForDPE : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception insertEsnDetailsForDPE : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertEsnDetailsForDPE  DAO  END");
		}

		return esnNo;
	}

	private String getEsnNoForDPE() throws BusinessException {
		String esnasnnbr = "";
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		SqlRowSet rs3_1 = null;
		SqlRowSet rs3 = null;
		SqlRowSet rsasn = null;
		String sql3 = "";
		String sql3_1 = "";
		try {
			log.info("START: getEsnNoForDPE  DAO  Start Obj ");

			////// ----------------
			String stresnasnnbr = "";
			String strsqldate = "";
			// sql3 = "SELECT MAX(ESN_ASN_NBR) FROM ESN ";
			sql3_1 = "SELECT TO_CHAR(SYSDATE,'Y/MM/DD') AS STRDATE FROM DUAL";

			log.info(" *** getEsnNoForDPE SQL *****" + sql3_1);
			log.info(param + paramMap.getValues());
			rs3_1 = namedParameterJdbcTemplate.queryForRowSet(sql3_1, paramMap);
			while (rs3_1.next()) {
				strsqldate = CommonUtility.deNull(rs3_1.getString(1));
			}

			String strsqlyy = strsqldate.substring(0, 1);
			String strsqlmm = strsqldate.substring(2, 4);
			if ((strsqlyy + strsqlmm.substring(0, 1)).equals("00") // Bhuvana 15/09/2010
					|| (strsqlyy + strsqlmm.substring(0, 1)).equals("01")) { // For year ends with 0. ie. 2010, 2020,
																				// etc.
				sql3 = "SELECT MAX(ESN_ASN_NBR) FROM ESN WHERE ESN_ASN_NBR < 1300000";
			} else {
				// sql3 = "SELECT MAX(ESN_ASN_NBR) FROM ESN ";
				// eg. For 2011: Retrieve the max ESN No between ESN No 10000000 and 19999999.
				sql3 = "SELECT MAX(ESN_ASN_NBR) FROM ESN WHERE ESN_ASN_NBR BETWEEN (SELECT TO_NUMBER(TO_CHAR(SYSDATE,'Y')) * 10000000 FROM DUAL) ";
				sql3 = sql3 + " AND (SELECT ((TO_NUMBER(TO_CHAR(SYSDATE,'Y'))+1) * 10000000)-1 FROM DUAL)";
			}

			log.info(" *** getEsnNoForDPE SQL *****" + sql3);
			log.info(param + paramMap.getValues());
			rs3 = namedParameterJdbcTemplate.queryForRowSet(sql3, paramMap);
			while (rs3.next()) {
				stresnasnnbr = CommonUtility.deNull(rs3.getString(1));
			}

			// generating next number
			/*
			 * SqlRowSet rs3_1 = stmt.executeQuery(sql3_1); while (rs3_1.next()) {
			 * strsqldate = CommonUtility.deNull(rs3_1.getString(1)); } rs3_1.close();
			 */
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

					sqlasn = "select ESN_ASN_NBR from ESN where ESN_ASN_NBR in (:asnStr)";
					List<String> existAsnNbrs = new ArrayList<String>();

					log.info(" *** getEsnNoForDPE SQL *****" + sqlasn);

					paramMap.addValue("asnStr", asnNbrs);
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
				// Added by Babatunde on Jan., 2014 : END
//	        Commented by Babatunde on Jan., 2014 : START
//	        if (strtempnbr.length() == 1) {
//	          stresnasnnbr = stresnasnnbr.concat("0000");
//	          stresnasnnbr = stresnasnnbr.concat(strtempnbr);
//	        }
//	        if (strtempnbr.length() == 2) {
//	          stresnasnnbr = stresnasnnbr.concat("000");
//	          stresnasnnbr = stresnasnnbr.concat(strtempnbr);
//	        }
//	        if (strtempnbr.length() == 3) {
//	          stresnasnnbr = stresnasnnbr.concat("00");
//	          stresnasnnbr = stresnasnnbr.concat(strtempnbr);
//	        }
//	        if (strtempnbr.length() == 4) {
//	          stresnasnnbr = stresnasnnbr.concat("0");
//	          stresnasnnbr = stresnasnnbr.concat(strtempnbr);
//	        }
//	        if (strtempnbr.length() == 5) {
//	            stresnasnnbr = stresnasnnbr.concat(strtempnbr);
//	          }
//	        Commented by Babatunde on Jan., 2014 : START
			} else {
				stresnasnnbr = (strsqlyy).concat(strsqlmm);
				stresnasnnbr = stresnasnnbr.concat("00002");
			}
			// new number generated
			esnasnnbr = stresnasnnbr;

			log.info("END: *** getEsnNoForDPE Result *****" + esnasnnbr.toString());

		} catch (NullPointerException ne) {
			log.info("Exception getEsnNoForDPE : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEsnNoForDPE : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEsnNoForDPE  DAO  END");
		}
		return esnasnnbr;
	}

	@Override
	public String createNomVesselPsaJp(String vslName, String voyNbr, String userid) throws BusinessException {
		String Status = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		String vvcode = "";
		String sql = "";

		try {
			log.info("START: createNomVesselPsaJp  DAO  Start Obj " + " vslName:" + vslName + " voyNbr:" + voyNbr
					+ " userid:" + userid);

			sql = "SELECT  * from nominated_vsl where in_voy_nbr =:voyNbr ";

			log.info(" *** createNomVesselPsaJp SQL *****" + sql);

			paramMap.put("voyNbr", voyNbr);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				Status = "Y";
			} else {
				Status = "N";
			}

			if (Status.equals("N")) {

				String sql3 = "select nom_vv_code_sequence.nextval from dual ";

				log.info(" *** createNomVesselPsaJp SQL *****" + sql3);
				log.info(param + paramMap);
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sql3, paramMap);
				while (rs1.next())
					vvcode = rs1.getString(1);

				sql = "";
				sql = "insert into  nominated_vsl (nom_vv_cd , vsl_nm , in_voy_nbr , last_modify_user_id ,last_modify_dttm) values (:vvcode,:vslName,:voyNbr,:userid,sysdate)";

				log.info(" *** createNomVesselPsaJp SQL *****" + sql);

				paramMap.put("vvcode", vvcode);
				paramMap.put("vslName", vslName);
				paramMap.put("voyNbr", voyNbr);
				paramMap.put("userid", userid);
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(sql, paramMap);
			}
			log.info("END: *** createNomVesselPsaJp Result *****" + Status);

		} catch (NullPointerException ne) {
			log.info("Exception createNomVesselPsaJp : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception createNomVesselPsaJp : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: createNomVesselPsaJp  DAO  END");
		}

		return Status;

	}

	@Override
	public boolean chkFirstCarrierVsl(String vslNm, String inVoyNbr) throws BusinessException {

		String sql;
		int count = 0;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		boolean result = false;

		sql = "SELECT count(*) FROM nominated_vsl VSL WHERE VSL_NM = :vslNm AND IN_VOY_NBR = :inVoyNbr AND REC_STATUS = 'A'";

		try {
			log.info("START: chkFirstCarrierVsl  DAO  Start Obj " + " vslNm:" + vslNm + " inVoyNbr:" + inVoyNbr);

			log.info(" *** chkFirstCarrierVsl SQL *****" + sql);

			paramMap.put("vslNm", vslNm);
			paramMap.put("inVoyNbr", inVoyNbr);
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

			log.info("END: *** chkFirstCarrierVsl Result *****" + result);

		} catch (NullPointerException ne) {
			log.info("Exception chkFirstCarrierVsl : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkFirstCarrierVsl : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: chkFirstCarrierVsl  DAO  END");
		}
		return result;
	}

	@Override
	public String getClsBjInd(String bookref) throws BusinessException {
		String bookinRefNo = bookref;
		String clsBjInd = "";
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		sql = "select vv_status_ind,vessel_call.gb_close_bj_ind,vessel_call.gb_close_shp_ind,gb_close_vsl_ind from vessel_call,bk_details where vessel_call.vv_cd = bk_details.var_nbr and bk_ref_nbr = :bookinRefNo ";

		try {
			log.info("START: getClsBjInd  DAO  Start Obj " + " bookinRefNo:" + bookinRefNo);
			log.info(" *** getClsBjInd SQL *****" + sql);

			paramMap.put("bookinRefNo", bookinRefNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				clsBjInd = CommonUtility.deNull(rs.getString("gb_close_bj_ind"));
			}
			log.info("END: *** getClsBjInd Result *****" + clsBjInd.toString());

		} catch (NullPointerException ne) {
			log.info("Exception getClsBjInd : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getClsBjInd : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getClsBjInd  DAO  END");
		}

		return clsBjInd;
	}

	@Override
	public String getClsVslInd(String bookref) throws BusinessException {
		String bookinRefNo = bookref;
		String clsVslInd = "";
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		sql = "select vv_status_ind,vessel_call.gb_close_bj_ind,vessel_call.gb_close_shp_ind,gb_close_vsl_ind from vessel_call,bk_details where vessel_call.vv_cd = bk_details.var_nbr and bk_ref_nbr = :bookinRefNo ";

		try {
			log.info("START: getClsVslInd  DAO  Start Obj " + " bookref:" + bookref);

			log.info(" *** getClsVslInd SQL *****" + sql);

			paramMap.put("bookinRefNo", bookinRefNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				clsVslInd = CommonUtility.deNull(rs.getString("gb_close_shp_ind"));
			}
			log.info("END: *** getClsVslInd Result *****" + clsVslInd.toString());

		} catch (NullPointerException ne) {
			log.info("Exception getClsVslInd : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getClsVslInd : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getClsVslInd  DAO  END");
		}

		return clsVslInd;
	}

	@Override
	public String getVvStatus(String bookref) throws BusinessException {
		String bookinRefNo = bookref;
		String vvStatus = "";
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		sql = "select vv_status_ind,vessel_call.gb_close_bj_ind,vessel_call.gb_close_shp_ind,gb_close_vsl_ind from vessel_call,bk_details where vessel_call.vv_cd = bk_details.var_nbr and bk_ref_nbr =:bookinRefNo ";

		try {
			log.info("START: getVvStatus  DAO  Start Obj " + " bookref:" + bookref);
			log.info(" *** getVvStatus SQL *****" + sql);

			paramMap.put("bookinRefNo", bookinRefNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				vvStatus = CommonUtility.deNull(rs.getString("vv_status_ind"));
			}
			log.info("END: *** getVvStatus Result *****" + vvStatus.toString());

		} catch (NullPointerException ne) {
			log.info("Exception getVvStatus : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVvStatus : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getVvStatus  DAO  END");
		}

		return vvStatus;
	}

	@Override
	public String getDeclarentCd(String bookref) throws BusinessException {
		String bookinRefNo = bookref;
		String esnDeclaredCd = "";
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		sql = "select var_nbr, esn_declared, declarant_cd from bk_details where bk_ref_nbr = :bookinRefNo and bk_status='A'";

		try {
			log.info("START: getDeclarentCd  DAO  Start Obj " + " bookinRefNo:" + bookinRefNo);

			log.info(" *** getDeclarentCd SQL *****" + sql);

			paramMap.put("bookinRefNo", bookinRefNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				esnDeclaredCd = CommonUtility.deNull(rs.getString("declarant_cd"));
			}
			log.info("END: *** getDeclarentCd Result *****" + esnDeclaredCd.toString());

		} catch (NullPointerException ne) {
			log.info("Exception getDeclarentCd : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getDeclarentCd : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getDeclarentCd  DAO  END");
		}
		return esnDeclaredCd;
	}

	@Override
	public String getBkStatus(String bookref) throws BusinessException {
		String bookinRefNo = bookref;
		String bkStatus = "";
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		
		sql = "select bk_status from bk_details where bk_ref_nbr = :bookinRefNo ";

		try {
			log.info("START: getBkStatus  DAO  Start Obj " + " bookref:" + bookref);
			log.info(" *** getBkStatus SQL *****" + sql);

			paramMap.put("bookinRefNo", bookinRefNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				bkStatus = CommonUtility.deNull(rs.getString("bk_status"));
			}
			log.info("END: *** getBkStatus Result *****" + bkStatus.toString());

		} catch (NullPointerException ne) {
			log.info("Exception getBkStatus : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBkStatus : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getBkStatus  DAO  END");
		}
		return bkStatus;
	}

	@Override
	public String getEsnDeclared(String bookref) throws BusinessException {
		String bookinRefNo = bookref;
		String esnDeclared = "";
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		sql = "select var_nbr, esn_declared, declarant_cd from bk_details where bk_ref_nbr =:bookinRefNo and bk_status='A'";

		try {
			log.info("START: getEsnDeclared  DAO  Start Obj " + " bookinRefNo:" + bookinRefNo);

			log.info(" *** getEsnDeclared SQL *****" + sql);

			paramMap.put("bookinRefNo", bookinRefNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				esnDeclared = CommonUtility.deNull(rs.getString("esn_declared"));
			}
			log.info("END: *** getEsnDeclared Result *****" + esnDeclared.toString());

		} catch (NullPointerException ne) {
			log.info("Exception getEsnDeclared : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEsnDeclared : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getEsnDeclared  DAO  END");
		}

		return esnDeclared;
	}

	@Override
	public void esnCancel(String esnNo, String bookingRefno, String UserId) throws BusinessException {

		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		String strUpdate = new String();
		Map<String, String> paramMap = new HashMap<String, String>();
		String strBkdetails = new String();

		try {
			log.info("START: esnCancel  DAO  Start Obj " + " esnNo:" + esnNo + " bookingRefno:" + bookingRefno
					+ " UserId:" + UserId);

			strUpdate = "UPDATE ESN SET ESN_STATUS='X',LAST_MODIFY_DTTM=SYSDATE, LAST_MODIFY_USER_ID=:UserId WHERE ESN_ASN_NBR=:esnNo ";

			strBkdetails = "UPDATE bk_details SET ESN_DECLARED='N',LAST_MODIFY_DTTM=SYSDATE, LAST_MODIFY_USER_ID=:UserId WHERE BK_REF_NBR=:bookingRefno";
			// ADDED BY VIETNGUYEN 20/01/2014
			String strSubAdp = "UPDATE SUB_ADP SET STATUS_CD='X',LAST_MODIFY_DTTM=SYSDATE, LAST_MODIFY_USER_ID=:UserId  WHERE STATUS_CD= 'A' and ESN_ASN_NBR=:esnNo ";

			sb.append("UPDATE (");
			sb.append(
					"SELECT txn.status_cd status, txn_dttm, txn_user_id  from sub_adp_txn txn inner join sub_adp sub on txn.sub_adp_nbr = sub.sub_adp_nbr ");
			sb.append("where txn.status_cd= 'A' and sub.edo_esn_ind = '0' and esn_asn_nbr =:esnNo ");
			sb.append(") t set t.status='X', txn_dttm=SYSDATE, txn_user_id=:UserId ");
			String strSubAdpTxn = sb.toString();

			log.info(" *** esnCancel SQL *****" + strUpdate);

			paramMap.put("UserId", UserId);
			paramMap.put("esnNo", esnNo);
			log.info(param + paramMap);
			int count = namedParameterJdbcTemplate.update(strUpdate, paramMap);

			log.info(" *** esnCancel SQL *****" + strBkdetails);

			paramMap.put("UserId", UserId);
			paramMap.put("bookingRefno", bookingRefno);
			log.info(param + paramMap);
			int count1 = namedParameterJdbcTemplate.update(strBkdetails, paramMap);
			// ADDED BY VIETNGUYEN 20/01/2014

			log.info(" *** esnCancel SQL *****" + strSubAdp);

			paramMap.put("UserId", UserId);
			paramMap.put("esnNo", esnNo);
			log.info(param + paramMap);
			namedParameterJdbcTemplate.update(strSubAdp, paramMap);

			log.info(" *** esnCancel SQL *****" + strSubAdpTxn);

			paramMap.put("esnNo", esnNo);
			paramMap.put("UserId", UserId);
			log.info(param + paramMap);
			namedParameterJdbcTemplate.update(strSubAdpTxn, paramMap);

			// added by Revathi

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				String sqllog = "";
				String strEsnTransNbr = "";
				String strBKTransNbr = "";

				// For adding esn_trans
				sqllog = "SELECT MAX(TRANS_NBR) FROM esn_trans WHERE ESN_ASN_NBR=:esnNo ";

				log.info(" *** esnCancel SQL *****" + sqllog);

				paramMap.put("esnNo", esnNo);
				log.info(param + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
				while (rs.next()) {
					strEsnTransNbr = CommonUtility.deNull(rs.getString(1));
				}

				if (strEsnTransNbr.equalsIgnoreCase("")) {
					strEsnTransNbr = "0";
				} else {
					strEsnTransNbr = String.valueOf(Integer.parseInt(strEsnTransNbr) + 1);
				}

				// adding for bk_details_trans
				sqllog = "";
				sqllog = "SELECT MAX(TRANS_NBR) FROM BK_DETAILS_TRANS WHERE BK_REF_NBR=:bookingRefno ";

				log.info(" *** esnCancel SQL *****" + sqllog);
				paramMap.put("bookingRefno", bookingRefno);
				log.info(param + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
				while (rs.next()) {
					strBKTransNbr = CommonUtility.deNull(rs.getString(1));
				}

				if (strBKTransNbr.equalsIgnoreCase("")) {
					strBKTransNbr = "0";
				} else {
					strBKTransNbr = String.valueOf(Integer.parseInt(strBKTransNbr) + 1);
				}

				String strEsnTrans = "insert into esn_trans(trans_nbr,esn_asn_nbr,esn_status,last_modify_user_id,last_modify_dttm) values (:strEsnTransNbr,:esnNo,'X',:UserId,sysdate)";

				String strBKDetailsTrans = "INSERT INTO BK_DETAILS_TRANS(TRANS_NBR,BK_REF_NBR,ESN_DECLARED,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) VALUES(:strBKTransNbr,:bookingRefno,'N',:UserId,sysdate)";

				log.info(" *** esnCancel SQL *****" + strEsnTrans);

				paramMap.put("strEsnTransNbr", strEsnTransNbr);
				paramMap.put("esnNo", esnNo);
				paramMap.put("UserId", UserId);
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strEsnTrans, paramMap);

				log.info(" *** esnCancel SQL *****" + strBKDetailsTrans);

				paramMap.put("strBKTransNbr", strBKTransNbr);
				paramMap.put("bookingRefno", bookingRefno);
				paramMap.put("UserId", UserId);
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strBKDetailsTrans, paramMap);

			}

			// end of add by Revathi

			if (count == 0 || count1 == 0) {
				log.info("Writing from EsnEJB.esnCancel");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			log.info("END: *** esnCancel Result *****" + count);
		} catch (NullPointerException ne) {
			log.info("Exception esnCancel : ", ne);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception esnCancel : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception esnCancel : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: esnCancel  DAO  END");
		}

	}

	private String getEdiBlNbr(String bkrefNbr) throws BusinessException {
		// String esnNo = esnNbr;
		String sql = "";
		String blNbr = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		sql = "select BL_NO from igd_detail_buffer where BK_REF_NBR = :bkrefNbr ";

		try {
			log.info("START: getEdiBlNbr  DAO  Start Obj " + " bkrefNbr:" + bkrefNbr);

			log.info(" *** getEdiBlNbr SQL *****" + sql);

			paramMap.put("bkrefNbr", bkrefNbr);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				blNbr = CommonUtility.deNull(rs.getString("BL_NO"));
			} else
				blNbr = "";

			log.info("END: *** getEdiBlNbr Result *****" + blNbr.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getEdiBlNbr : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEdiBlNbr : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getEdiBlNbr  DAO  END");
		}

		return blNbr;
	}

	@Override
	public String esnUpdateForDPE(int noOfPkgs, String hscd, String hsSubCodeFr, String hsSubCodeTo, String pkgsType,
			String mark, String lopInd, String dgInd, String stgInd, String poD, int noOfStorageDay, String payMode,
			String accNo, String esnNbr, String cargoDes, double weight, double volume, String cntr1, String cntr2,
			String cntr3, String cntr4, String bookingRefNo, String stuffind, String UserId, String category,
			List<TruckerValueObject> truckerList, String deliveryToEPC, String customHsCode,
			List<HsCodeDetails> multiHsCodeList) throws BusinessException {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs1 = null;
		StringBuffer sb = new StringBuffer();

		String esnqry = new String();
		String strUpdate = new String();
		String strMark = new String();
		String esnNo = esnNbr;
		String sqlTrans = "";
		String strMarkTrans = new String();
		String strCntr1Trans = new String();
		String strCntr2Trans = new String();
		String strCntr3Trans = new String();
		String strCntr4Trans = new String();
		String strEsnDetailsTrans = new String();
		String transNumEsnStr = "";
		int transNumEsnInt = 0;
		String blNbr = getEdiBlNbr(bookingRefNo);
		String strCntr1 = new String();
		String strCntr2 = new String();
		String strCntr3 = new String();
		String strCntr4 = new String();
		// Added by VietNguyen 20/01/2014
		String truckerIc = "";
		String truckerNm = "";
		String truckerContact = "";
		String truckerPkgs = null;
		String truckerCd = "";

		try {
			log.info("START: esnUpdateForDPE  DAO  Start Obj " + " noOfPkgs:" + noOfPkgs + " hscd:" + hscd
					+ " hsSubCodeFr:" + hsSubCodeFr + " hsSubCodeTo:" + hsSubCodeTo + " pkgsType:" + pkgsType + " mark:"
					+ mark + " lopInd:" + lopInd + " dgInd:" + dgInd + " stgInd:" + stgInd + " poD:" + poD
					+ " noOfStorageDay:" + noOfStorageDay + " payMode:" + payMode + " accNo:" + accNo + " esnNbr:"
					+ esnNbr + " cargoDes:" + cargoDes + " weight:" + weight + " volume:" + volume + " cntr1:" + cntr1
					+ " cntr2:" + cntr2 + " cntr3:" + cntr3 + " cntr4:" + cntr4 + " bookingRefNo:" + bookingRefNo
					+ " stuffind:" + stuffind + " UserId:" + UserId + " category:" + category + " truckerList:"
					+ truckerList + " deliveryToEPC:" + deliveryToEPC + ",customHsCode:" + customHsCode
					+ ", multiHsCodeList : " + multiHsCodeList.toString());

			if (truckerList != null && truckerList.size() > 0) {
				TruckerValueObject trucker = truckerList.get(0);
				if (trucker != null) {
					truckerIc = StringUtils.upperCase(trucker.getTruckerIc());
					truckerNm = trucker.getTruckerNm();
					truckerContact = trucker.getTruckerContact();
					truckerPkgs = trucker.getTruckerPkgs();
					truckerCd = trucker.getTruckerCd();
				}
			}

			sqlTrans = "SELECT MAX(TRANS_NBR) FROM TESN_PSA_JP_TRANS WHERE ESN_ASN_NBR =:esnNo ";

			if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y") {

				// Added by Revathi
				String sqllog = "";
				String strMarkTransNbr = "";
				String strEsnTransNbr = "";

				log.info(" *** esnUpdateForDPE SQL *****" + sqlTrans);

				paramMap.put("esnNo", esnNo);
				log.info(param + paramMap);
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqlTrans, paramMap);
				while (rs1.next()) {
					transNumEsnStr = rs1.getString(1);
				}

				if (transNumEsnStr == null || transNumEsnStr == "") {
					transNumEsnInt = 0;
				} else {
					transNumEsnInt = Integer.parseInt(transNumEsnStr);
					transNumEsnInt++;
				}

				sqllog = "SELECT MAX(TRANS_NBR) FROM ESN_Trans WHERE ESN_ASN_NBR =:esnNo ";

				rs1 = null;
				log.info(" *** esnUpdateForDPE SQL *****" + sqllog);

				paramMap.put("esnNo", esnNo);
				log.info(param + paramMap);
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
				while (rs1.next()) {
					strEsnTransNbr = CommonUtility.deNull(rs1.getString(1));
				}

				if (strEsnTransNbr.equalsIgnoreCase("")) {
					strEsnTransNbr = "0";
				} else {
					strEsnTransNbr = String.valueOf(Integer.parseInt(strEsnTransNbr) + 1);
				}

				// Added by MCconsulting , deliveryToEPC set to 'N' if deliveryToEPC is null or
				// empty.
				deliveryToEPC = (deliveryToEPC == null || deliveryToEPC.equalsIgnoreCase("null")
						|| deliveryToEPC.trim().length() == 0) ? "N" : deliveryToEPC;

				sqllog = null;
				sqllog = "SELECT MAX(TRANS_NBR) FROM ESN_MARKINGS_Trans WHERE ESN_ASN_NBR =:esnNo ";

				rs1 = null;
				log.info(" *** esnUpdateForDPE SQL *****" + sqllog);

				paramMap.put("esnNo", esnNo);
				log.info(param + paramMap);
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
				while (rs1.next()) {
					strMarkTransNbr = CommonUtility.deNull(rs1.getString(1));
				}

				if (strMarkTransNbr.equalsIgnoreCase("")) {
					strMarkTransNbr = "0";
				} else {
					strMarkTransNbr = String.valueOf(Integer.parseInt(strMarkTransNbr) + 1);
				}

				// log.info("strMarkTrans"+strMarkTrans);

				String strEsnTrans = "INSERT INTO ESN_Trans(ESN_ASN_NBR,TRANS_NBR,STUFF_IND,ESN_STATUS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM, EPC_IND)VALUES(:esnNo,:strEsnTransNbr,:stuffind,'A',:UserId,sysdate ,:deliveryToEPC)"; // MCC

				log.info(" *** esnUpdateForDPE SQL *****" + strEsnTrans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("strEsnTransNbr", strEsnTransNbr);
				paramMap.put("stuffind", stuffind);

				paramMap.put("UserId", UserId);
				paramMap.put("deliveryToEPC", deliveryToEPC);
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strEsnTrans, paramMap);

				sb.append(
						"INSERT INTO TESN_PSA_JP_TRANS(ESN_ASN_NBR,TRANS_NBR,DIS_PORT,HS_CD,PKG_TYPE,CRG_DES,NBR_PKGS,GROSS_WT,GROSS_VOL,DG_IND,STORAGE_IND,STORAGE_DAYS,OPS_IND,ACCT_NBR,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM");
				sb.append(", TRUCKER_NM, TRUCKER_IC, TRUCKER_NBR_PKGS, TRUCKER_CO_CD , CUSTOM_HS_CODE)");
				sb.append(
						"VALUES(:esnNo,:transNumEsnInt,:poD,:hscd,:pkgsType,:cargoDes,:noOfPkgs,:weight,:volume,:dgInd, ");
				sb.append(
						":stgInd,:noOfStorageDay,:lopInd,:accNo,:UserId,sysdate,:truckerNm,:truckerIc,:truckerPkgs,:truckerCd, :customHsCode) ");

				strEsnDetailsTrans = sb.toString();

				log.info(" *** esnUpdateForDPE SQL *****" + strEsnDetailsTrans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
				paramMap.put("poD", poD);
				paramMap.put("hscd", hscd);
				paramMap.put("pkgsType", pkgsType);
				paramMap.put("cargoDes", GbmsCommonUtility.addApostr(cargoDes));
				paramMap.put("noOfPkgs", Integer.toString(noOfPkgs));
				paramMap.put("weight", Double.toString(weight));
				paramMap.put("volume", Double.toString(volume));
				paramMap.put("dgInd", dgInd);
				paramMap.put("stgInd", stgInd);
				paramMap.put("noOfStorageDay", Integer.toString(noOfStorageDay));
				paramMap.put("lopInd", lopInd);
				paramMap.put("accNo", GbmsCommonUtility.addApostr(accNo));
				paramMap.put("UserId", UserId);
				paramMap.put("truckerNm", truckerNm);
				paramMap.put("truckerIc", truckerIc);
				paramMap.put("truckerPkgs", truckerPkgs);
				paramMap.put("truckerCd", truckerCd);
				paramMap.put("customHsCode", customHsCode);
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strEsnDetailsTrans, paramMap);

				strMarkTrans = "INSERT INTO ESN_MARKINGS_Trans(ESN_ASN_NBR,TRANS_NBR,MARKINGS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) VALUES(:esnNo,:strMarkTransNbr,:mark,:UserId,sysdate)";

				log.info(" *** esnUpdateForDPE SQL *****" + strMarkTrans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("strMarkTransNbr", strMarkTransNbr);
				paramMap.put("mark", GbmsCommonUtility.addApostr(mark));
				paramMap.put("UserId", UserId);
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strMarkTrans, paramMap);
			}

			sb = new StringBuffer();
			sb.append("update esn set stuff_ind=:stuffind ,LAST_MODIFY_DTTM=SYSDATE, LAST_MODIFY_USER_ID=:UserId");

			if (deliveryToEPC != null && !deliveryToEPC.equalsIgnoreCase("null") && deliveryToEPC.trim().length() > 0) {
				sb.append(", EPC_IND=:deliveryToEPC ");

			}

			sb.append(" where esn_status='A' and esn_asn_nbr=:esnNo and CARGO_CATEGORY_CD =:category ");

			esnqry = sb.toString();

			log.info(" *** esnUpdateForDPE SQL *****" + esnqry);

			paramMap.put("stuffind", stuffind);
			paramMap.put("UserId", UserId);
			if (deliveryToEPC != null && !deliveryToEPC.equalsIgnoreCase("null") && deliveryToEPC.trim().length() > 0) {
				paramMap.put("deliveryToEPC", deliveryToEPC);
			}

			paramMap.put("esnNo", esnNo);
			paramMap.put("category", category);
			log.info(param + paramMap);
			namedParameterJdbcTemplate.update(esnqry, paramMap);

			sb = new StringBuffer();

			sb.append(
					"UPDATE TESN_PSA_JP SET NBR_PKGS=:noOfPkgs,HS_CD=:hscd,HS_SUB_CODE_FR=:hsSubCodeFr,HS_SUB_CODE_TO=:hsSubCodeTo,PKG_TYPE=:pkgsType,");
			sb.append("OPS_IND=:lopInd,DG_IND=:dgInd,STORAGE_IND=:stgInd,DIS_PORT=:poD,STORAGE_DAYS=:noOfStorageDay,");
			sb.append(
					"CRG_DES=:cargoDes,GROSS_WT=:weight,GROSS_VOL=:volume,PAYMENT_MODE=:payMode,ACCT_NBR=:accNo,BL_NBR=:blNbr ");
			sb.append(
					", TRUCKER_NM=:truckerNm,TRUCKER_IC=:truckerIc,TRUCKER_NBR_PKGS=:truckerPkgs, TRUCKER_CONTACT_NBR=:truckerContact, TRUCKER_CO_CD= :truckerCd , CUSTOM_HS_CODE =:customHsCode ");
			sb.append(" WHERE ESN_ASN_NBR=:esnNo ");

			strUpdate = sb.toString();

			log.info(" *** esnUpdateForDPE SQL *****" + strUpdate);

			paramMap.put("noOfPkgs", Integer.toString(noOfPkgs));
			paramMap.put("hscd", hscd);
			paramMap.put("hsSubCodeFr", hsSubCodeFr);
			paramMap.put("hsSubCodeTo", hsSubCodeTo);
			paramMap.put("pkgsType", pkgsType);
			paramMap.put("lopInd", lopInd);
			paramMap.put("dgInd", dgInd);
			paramMap.put("stgInd", stgInd);
			paramMap.put("poD", poD);
			paramMap.put("noOfStorageDay", Integer.toString(noOfStorageDay));
			paramMap.put("cargoDes", GbmsCommonUtility.addApostr(cargoDes));
			paramMap.put("weight", Double.toString(weight));
			paramMap.put("volume", Double.toString(volume));
			paramMap.put("payMode", payMode);
			paramMap.put("accNo", GbmsCommonUtility.addApostr(accNo));
			paramMap.put("blNbr", blNbr);
			paramMap.put("truckerNm", truckerNm);
			paramMap.put("truckerIc", truckerIc);
			paramMap.put("truckerPkgs", truckerPkgs);
			paramMap.put("truckerContact", truckerContact);
			paramMap.put("truckerCd", truckerCd);
			paramMap.put("esnNo", esnNo);
			paramMap.put("customHsCode", customHsCode);
			log.info(param + paramMap);
			int count = namedParameterJdbcTemplate.update(strUpdate, paramMap);
			
			// Start CR FTZ HSCODE - NS JULY 2024
			if(count > 0) {				
				for (HsCodeDetails hsCodeObj : multiHsCodeList) {
					paramMap.put("ESN_ASN_NBR", esnNo);
					paramMap.put("HS_CODE",hsCodeObj.getHsCode());
					paramMap.put("HS_SUB_CODE_FR",hsCodeObj.getHsSubCodeFr());
					paramMap.put("HS_SUB_CODE_TO",hsCodeObj.getHsSubCodeTo());
					paramMap.put("NBR_PKGS",hsCodeObj.getNbrPkgs());
					paramMap.put("GROSS_WT",hsCodeObj.getGrossWt());
					paramMap.put("GROSS_VOL",hsCodeObj.getGrossVol());
					paramMap.put("CUSTOM_HS_CODE",hsCodeObj.getCustomHsCode());
					paramMap.put("CRG_DES",hsCodeObj.getCrgDes());	
					paramMap.put("HS_SUB_CODE_DESC",hsCodeObj.getHsSubCodeDesc());	
					paramMap.put("userId", UserId);
				
					
					if(hsCodeObj.getIsHsCodeChange().equalsIgnoreCase("A")) { // Add
						// get TESN_HSCODE_SEQ_NBR 
						StringBuilder sbSeq = new StringBuilder();
						sbSeq.append("SELECT GBMS.SEQ_TESN_HSCODE_SEQ_NBR.nextval AS seqVal FROM DUAL");
						Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(), new HashMap<String, String>());
						BigDecimal seqValue = (BigDecimal) results.get("seqVal");
						// end
						
						sb.setLength(0);
						sb.append(" INSERT INTO GBMS.TESN_PSA_JP_HSCODE_DETAILS  ");
						sb.append(" (ESN_ASN_NBR,TESN_HSCODE_SEQ_NBR,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,CUSTOM_HS_CODE,CRG_DES, HS_SUB_CODE_DESC,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
						sb.append(" VALUES(:ESN_ASN_NBR,:TESN_HSCODE_SEQ_NBR,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:NBR_PKGS,:GROSS_WT,:GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
						
	
						paramMap.put("TESN_HSCODE_SEQ_NBR", seqValue);
						paramMap.put("REC_STATUS", "A");
						log.info("SQL" + sb.toString());
						int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
						log.info("counths : " + counths);
						
					}else if(hsCodeObj.getIsHsCodeChange().equalsIgnoreCase("E")) { // Edit
						sb.setLength(0);
						sb.append(" UPDATE GBMS.TESN_PSA_JP_HSCODE_DETAILS SET HS_CODE=:HS_CODE, HS_SUB_CODE_FR=:HS_SUB_CODE_FR, HS_SUB_CODE_TO=:HS_SUB_CODE_TO,");
						sb.append(" NBR_PKGS=:NBR_PKGS, GROSS_WT=:GROSS_WT, GROSS_VOL=:GROSS_VOL,CUSTOM_HS_CODE=:CUSTOM_HS_CODE, CRG_DES=:CRG_DES, ");
						sb.append(" HS_SUB_CODE_DESC=:HS_SUB_CODE_DESC, LAST_MODIFY_USER_ID=:userId, LAST_MODIFY_DTTM=SYSDATE WHERE");
						sb.append(" ESN_ASN_NBR = :ESN_ASN_NBR AND TESN_HSCODE_SEQ_NBR=:TESN_HSCODE_SEQ_NBR ");
						
	
						paramMap.put("TESN_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());
						paramMap.put("REC_STATUS", "A");
						
						log.info("SQL" + sb.toString());
						int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
						log.info("counths : " + counths);
						
					}else if(hsCodeObj.getIsHsCodeChange().equalsIgnoreCase("D")) {
						sb.setLength(0);
						sb.append(" DELETE FROM GBMS.TESN_PSA_JP_HSCODE_DETAILS WHERE TESN_HSCODE_SEQ_NBR = :TESN_HSCODE_SEQ_NBR ");
					
						paramMap.put("REC_STATUS", "I");
						paramMap.put("TESN_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());
						log.info("SQL" + sb.toString());
						int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
						log.info("counths : " + counths);
					}
					
					sb.setLength(0);
					sb.append(" INSERT INTO GBMS.TESN_PSA_JP_HSCODE_DETAILS_TRANS  ");
					sb.append(" (ESN_ASN_NBR,TESN_HSCODE_SEQ_NBR,AUDIT_DTTM, REC_STATUS,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,CUSTOM_HS_CODE,CRG_DES, HS_SUB_CODE_DESC,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
					sb.append(" VALUES(:ESN_ASN_NBR,:TESN_HSCODE_SEQ_NBR, SYSDATE, :REC_STATUS,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:NBR_PKGS,:GROSS_WT,:GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
		
					log.info("SQL" + sb.toString());
					int counthsAudit = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
					log.info("counths : " + counthsAudit);
				
				}
			}
			// END CR FTZ HSCODE - NS JULY 2024

			strMark = "UPDATE esn_markings SET MARKINGS=:mark WHERE ESN_ASN_NBR=:esnNo ";

			log.info(" *** esnUpdateForDPE SQL *****" + strMark);

			paramMap.put("mark", GbmsCommonUtility.addApostr(mark));
			paramMap.put("esnNo", esnNo);
			log.info(param + paramMap);
			int cntmark = namedParameterJdbcTemplate.update(strMark, paramMap);

			strCntr1 = "UPDATE ESN_CNTR SET CNTR_NBR=:cntr1 WHERE ESN_ASN_NBR=:esnNo AND ESN_CNTR_SEQ=1";

			log.info(" *** esnUpdateForDPE SQL *****" + strCntr1);

			paramMap.put("cntr1", GbmsCommonUtility.addApostr(cntr1));
			paramMap.put("esnNo", esnNo);
			log.info(param + paramMap);
			int cntcntr1 = namedParameterJdbcTemplate.update(strCntr1, paramMap);

			if (cntcntr1 == 0 && cntr1 != "") {
				strCntr1 = "";
				strCntr1 = "INSERT INTO ESN_CNTR(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES(1,:esnNo,:cntr1)";

				log.info(" *** esnUpdateForDPE SQL *****" + strCntr1);

				paramMap.put("cntr1", GbmsCommonUtility.addApostr(cntr1));
				paramMap.put("esnNo", esnNo);
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strCntr1, paramMap);
			}

			strCntr2 = "UPDATE ESN_CNTR SET CNTR_NBR=:cntr2 WHERE ESN_ASN_NBR=:esnNo AND ESN_CNTR_SEQ=2";

			log.info(" *** esnUpdateForDPE SQL *****" + strCntr2);

			paramMap.put("cntr2", GbmsCommonUtility.addApostr(cntr2));
			paramMap.put("esnNo", esnNo);
			log.info(param + paramMap);
			int cntcntr2 = namedParameterJdbcTemplate.update(strCntr2, paramMap);

			if (cntcntr2 == 0 && cntr2 != "") {
				strCntr2 = "";
				strCntr2 = "INSERT INTO ESN_CNTR(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES(2,:esnNo,:cntr2 )";

				log.info(" *** esnUpdateForDPE SQL *****" + strCntr2);

				paramMap.put("cntr2", GbmsCommonUtility.addApostr(cntr2));
				paramMap.put("esnNo", esnNo);
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strCntr2, paramMap);
			}

			strCntr3 = "UPDATE ESN_CNTR SET CNTR_NBR=:cntr3 WHERE ESN_ASN_NBR=:esnNo AND ESN_CNTR_SEQ=3";

			log.info(" *** esnUpdateForDPE SQL *****" + strCntr3);

			paramMap.put("cntr3", GbmsCommonUtility.addApostr(cntr3));
			paramMap.put("esnNo", esnNo);
			log.info(param + paramMap);
			int cntcntr3 = namedParameterJdbcTemplate.update(strCntr3, paramMap);
			if (cntcntr3 == 0 && cntr3 != "") {

				strCntr3 = "";
				strCntr3 = "INSERT INTO ESN_CNTR(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES(3,:esnNo,:cntr3)";

				log.info(" *** esnUpdateForDPE SQL *****" + strCntr3);

				paramMap.put("cntr3", GbmsCommonUtility.addApostr(cntr3));
				paramMap.put("esnNo", esnNo);
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strCntr3, paramMap);
			}
			strCntr4 = "UPDATE ESN_CNTR SET CNTR_NBR=:cntr4 WHERE ESN_ASN_NBR=:esnNo AND ESN_CNTR_SEQ=4";

			log.info(" *** esnUpdateForDPE SQL *****" + strCntr4);

			paramMap.put("cntr4", GbmsCommonUtility.addApostr(cntr4));
			paramMap.put("esnNo", esnNo);
			log.info(param + paramMap);
			int cntcntr4 = namedParameterJdbcTemplate.update(strCntr4, paramMap);
			if (cntcntr4 == 0 && cntr4 != "") {
				strCntr4 = "";
				strCntr4 = "INSERT INTO ESN_CNTR(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES(4,:esnNo,:cntr4 )";

				log.info(" *** esnUpdateForDPE SQL *****" + strCntr4);

				paramMap.put("cntr4", GbmsCommonUtility.addApostr(cntr4));
				paramMap.put("esnNo", esnNo);
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strCntr4, paramMap);
			}

			// Added by VietNguyen 20/01/2014
			String strDeleteSubAdp = "UPDATE SUB_ADP SET STATUS_CD='X',LAST_MODIFY_DTTM=SYSDATE, LAST_MODIFY_USER_ID=:UserId WHERE STATUS_CD= 'A' and EDO_ESN_IND = '0' and ESN_ASN_NBR=:esnNo";

			log.info(" *** esnUpdateForDPE SQL *****" + strDeleteSubAdp);

			paramMap.put("UserId", UserId);
			paramMap.put("esnNo", esnNo);
			log.info(param + paramMap);
			namedParameterJdbcTemplate.update(strDeleteSubAdp, paramMap);

			sb = new StringBuffer();
			sb.append(
					"UPDATE (SELECT txn.status_cd status, txn_dttm, txn_user_id  from sub_adp_txn txn inner join sub_adp sub on txn.sub_adp_nbr = sub.sub_adp_nbr ");
			sb.append(
					"where txn.status_cd = 'A' and sub.edo_esn_ind = '0' and esn_asn_nbr = :esnNo ) t set t.status='X', txn_dttm=SYSDATE, txn_user_id=:UserId ");

			String strDeleteSubAdpTxn = sb.toString();

			log.info(" *** esnUpdateForDPE SQL *****" + strDeleteSubAdpTxn);

			paramMap.put("UserId", UserId);
			paramMap.put("esnNo", esnNo);
			log.info(param + paramMap);
			namedParameterJdbcTemplate.update(strDeleteSubAdpTxn, paramMap);
			String strSubAdp = "";
			Integer adpSeq = 1;
			int cntAdp = 1;
			if (truckerList != null && truckerList.size() > 1) {
				String sqllog = "SELECT MAX(SUB_ADP_NBR) FROM sub_adp";

				rs1 = null;
				log.info(" *** esnUpdateForDPE SQL *****" + sqllog);
				log.info(param + paramMap);
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
				while (rs1.next()) {
					adpSeq = rs1.getInt(1);
				}

				if (adpSeq != null) {
					adpSeq = adpSeq + 1;
				}
				for (int i = 1; i < truckerList.size(); i++) {
					TruckerValueObject trucker = truckerList.get(i);
					if (trucker != null) {
						sb = new StringBuffer();
						sb.append(
								"INSERT INTO sub_adp(SUB_ADP_NBR, STATUS_CD, ESN_ASN_NBR,TRUCKER_IC,TRUCKER_NM,TRUCKER_CONTACT_NBR,TRUCKER_NBR_PKGS,EDO_ESN_IND,");
						sb.append("CREATE_USER_ID,CREATE_DTTM,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM, TRUCKER_CO_CD) ");
						sb.append(
								"VALUES(:adpSeq, 'A',:esnNo,:truckerIc,:truckerNm,:truckerContact,:truckerPkgs,'0',:UserId, sysdate,:UserId, sysdate,:TruckerCd )");

						strSubAdp = sb.toString();

						log.info(" *** strSubAdp SQL *****" + strSubAdp);

						paramMap.put("adpSeq", Integer.toString(adpSeq));
						paramMap.put("esnNo", esnNo);
						paramMap.put("truckerIc", StringUtils.upperCase(trucker.getTruckerIc()));
						paramMap.put("truckerNm", trucker.getTruckerNm());
						paramMap.put("truckerContact", trucker.getTruckerContact());
						paramMap.put("truckerPkgs", trucker.getTruckerPkgs());
						paramMap.put("UserId", UserId);
						paramMap.put("TruckerCd", trucker.getTruckerCd());
						log.info(param + paramMap);
						cntAdp = namedParameterJdbcTemplate.update(strSubAdp, paramMap);
						if (cntAdp == 0) {
							break;
						}
						strSubAdp = "";
						sb = new StringBuffer();

						sb.append(
								"INSERT INTO sub_adp_txn(SUB_ADP_NBR, STATUS_CD, TRUCKER_CO_CD, TRUCKER_IC,TRUCKER_NM,TRUCKER_CONTACT_NBR,TRUCKER_NBR_PKGS,EDO_ESN_IND,TXN_USER_ID,TXN_DTTM) ");
						sb.append("VALUES(:adpSeq, 'A',:truckerCd,:truckerIc,:truckerNm,:truckerContact,:truckerPkgs");
						sb.append(", '0',:UserId, sysdate)");
						strSubAdp = sb.toString();

						log.info(" *** ql Sub Adp Txn table SQL *****" + strSubAdp);

						paramMap.put("adpSeq", Integer.toString(adpSeq));
						paramMap.put("truckerIc", StringUtils.upperCase(trucker.getTruckerIc()));
						paramMap.put("truckerNm", trucker.getTruckerNm());
						paramMap.put("truckerContact", trucker.getTruckerContact());
						paramMap.put("truckerPkgs", trucker.getTruckerPkgs());
						paramMap.put("UserId", UserId);
						paramMap.put("truckerCd", trucker.getTruckerCd());
						log.info(param + paramMap);
						cntAdp = namedParameterJdbcTemplate.update(strSubAdp, paramMap);
						if (cntAdp == 0) {
							break;
						}
						adpSeq++;
					}
				}
			}

			strCntr1Trans = "INSERT INTO esn_cntr_Trans(ESN_CNTR_SEQ,ESN_ASN_NBR,TRANS_NBR,CNTR_NBR) VALUES('1',:esnNo,:transNumEsnInt,:cntr1 )";

			strCntr2Trans = "INSERT INTO esn_cntr_Trans(ESN_CNTR_SEQ,ESN_ASN_NBR,TRANS_NBR,CNTR_NBR) VALUES('2',:esnNo,:transNumEsnInt ,:cntr2 )";
			strCntr3Trans = "INSERT INTO esn_cntr_Trans(ESN_CNTR_SEQ,ESN_ASN_NBR,TRANS_NBR,CNTR_NBR) VALUES('3',:esnNo,:transNumEsnInt,:cntr3)";
			strCntr4Trans = "INSERT INTO esn_cntr_Trans(ESN_CNTR_SEQ,ESN_ASN_NBR,TRANS_NBR,CNTR_NBR) VALUES('4',:esnNo,:transNumEsnInt,:cntr4)";

			if (cntr1 != null && !cntr1.equals("")) {
				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y")

					log.info(" *** esnUpdateForDPE SQL *****" + strCntr1Trans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
				paramMap.put("cntr1", GbmsCommonUtility.addApostr(cntr1));
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strCntr1Trans, paramMap);
			}
			if (cntr2 != null && !cntr2.equals("")) {
				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y")

					log.info(" *** esnUpdateForDPE SQL *****" + strCntr2Trans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
				paramMap.put("cntr2", GbmsCommonUtility.addApostr(cntr2));
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strCntr2Trans, paramMap);
			}
			if (cntr3 != null && !cntr3.equals("")) {
				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y")

					log.info(" *** esnUpdateForDPE SQL *****" + strCntr3Trans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
				paramMap.put("cntr3", GbmsCommonUtility.addApostr(cntr3));
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strCntr3Trans, paramMap);
			}
			if (cntr4 != null && !cntr4.equals("")) {
				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y")

					log.info(" *** esnUpdateForDPE SQL *****" + strCntr4Trans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
				paramMap.put("cntr4", GbmsCommonUtility.addApostr(cntr4));
				log.info(param + paramMap);
				namedParameterJdbcTemplate.update(strCntr4Trans, paramMap);
			}

			if (count == 0 || cntmark == 0) {
				log.info("Writing from EsnEJB.esnUpdate");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			log.info("END: *** esnUpdateForDPE Result *****" + esnNo.toString());
		} catch (NullPointerException ne) {
			log.info("Exception esnUpdateForDPE : ", ne);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception esnUpdateForDPE : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception esnUpdateForDPE : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: esnUpdateForDPE  DAO  END");
		}

		return esnNo;
	}

	@Override
	public String getPkgsDesc(String esnNbr) throws BusinessException {
		String esnNo = esnNbr;
		String sql = "";
		String pkgsDesc = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		sql = "select PKG_TYPE_CD,PKG_DESC from PKG_TYPES,tesn_psa_jp where PKG_TYPES.PKG_TYPE_CD = tesn_psa_jp.PKG_TYPE and ESN_ASN_NBR =:esnNo";

		try {
			log.info("START: getPkgsDesc  DAO  Start Obj " + " esnNbr:" + esnNbr);

			log.info(" *** getPkgsDesc SQL *****" + sql);

			paramMap.put("esnNo", esnNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				pkgsDesc = CommonUtility.deNull(rs.getString("PKG_DESC"));
			}
			log.info("END: *** getPkgsDesc Result *****" + pkgsDesc.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getPkgsDesc : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getPkgsDesc : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getPkgsDesc  DAO  END");
		}

		return pkgsDesc;
	}

	@Override
	public String getClsShipInd(String varNo) throws BusinessException {
		String varNbr = varNo;
		String clsShpInd = "";
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		sql = "select gb_close_shp_ind from vessel_Call where vv_cd =:varNbr ";

		try {
			log.info("START: getClsShipInd  DAO  Start Obj " + " varNo:" + varNo);

			log.info(" *** getClsShipInd SQL *****" + sql);

			paramMap.put("varNbr", varNbr);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				clsShpInd = CommonUtility.deNull(rs.getString("gb_close_shp_ind"));
			}
			log.info("END: *** getClsShipInd Result *****" + clsShpInd.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getClsShipInd : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getClsShipInd : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getClsShipInd  DAO  END");
		}

		return clsShpInd;
	}

	@Override
	public int getUaNoPkgs(String esnNo) throws BusinessException {
		String esnNbr = esnNo;
		int uaNoPkgs = 0;
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		sql = "select UA_NBR_PKGS from tesn_psa_jp where esn_asn_nbr =:esnNbr ";

		try {
			log.info("START: getUaNoPkgs  DAO  Start Obj " +" esnNo:"+CommonUtility.deNull(esnNo));

			log.info(" *** getUaNoPkgs SQL *****" + sql);

			paramMap.put("esnNbr", esnNbr);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				uaNoPkgs = rs.getInt("ua_nbr_pkgs");
			}

			log.info("END: *** getUaNoPkgs Result *****" + uaNoPkgs);
		} catch (NullPointerException ne) {
			log.info("Exception getUaNoPkgs : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getUaNoPkgs : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getUaNoPkgs  DAO  END");
		}

		return uaNoPkgs;
	}

	@Override
	public boolean chkAccNo(String accNo) throws BusinessException {

		String AccountNo = accNo.toUpperCase();
		String sql;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		boolean result = true;
		sql = "select ACCT_NBR from cust_acct where upper(ACCT_NBR) =:AccountNo and acct_status_cd='A' and business_type like '%G%' and trial_ind='N'";

		try {
			log.info("START: chkAccNo  DAO  Start Obj " + " AccountNo:" + AccountNo);

			log.info(" *** chkAccNo SQL *****" + sql);

			paramMap.put("AccountNo", AccountNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next())
				result = true;
			else
				result = false;

			log.info("END: *** chkAccNo Result *****" + result);
		} catch (NullPointerException ne) {
			log.info("Exception chkAccNo : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkAccNo : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: chkAccNo  DAO  END");
		}

		return result;
	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getUserAccNo(String bookingRfnbr, String custId, String accNbr)
			throws BusinessException {
		String bookingRefNo = bookingRfnbr;
		String accNo = accNbr;
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String esnDeclrCd = getDeclarant(bookingRefNo);
		// if(custCd.equals("JP"))
		// sql = "select prev_acct_nbr from cust_acct where acct_status_cd='A' and
		// business_type like '%G%' and trial_ind='N' and prev_acct_nbr is not null and
		// prev_acct_nbr != '"+accNo+"'";
		// else
		sql = "select ACCT_NBR from cust_acct where cust_cd =:esnDeclrCd and acct_status_cd='A' and business_type like '%G%' and trial_ind='N' and ACCT_NBR is not null and ACCT_NBR != :accNo ";

		List<TesnPsaJpEsnListValueObject> UserAccNo = new ArrayList<TesnPsaJpEsnListValueObject>();
		TesnPsaJpEsnListValueObject esnListValueObject = null;
		try {

			log.info("START: getUserAccNo  DAO  Start Obj " + " bookingRfnbr:" + bookingRfnbr + " custId:" + custId
					+ " accNbr:" + accNbr);

			log.info(" *** getUserAccNo SQL *****" + sql);

			paramMap.put("esnDeclrCd", esnDeclrCd);
			paramMap.put("accNo", accNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				esnListValueObject = new TesnPsaJpEsnListValueObject();
				esnListValueObject.setAccNo(CommonUtility.deNull(rs.getString("ACCT_NBR")));
				UserAccNo.add(esnListValueObject);
				// log.info("Db Value :"+rs.getString("esn_nbr"));
			}
			log.info("END: *** getUserAccNo Result *****" + UserAccNo.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getUserAccNo : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getUserAccNo : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getUserAccNo  DAO  END");
		}

		return UserAccNo;
	}

	private String getDeclarant(String bkrefnbr) throws BusinessException {
		String bookingRefNo = bkrefnbr;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String sql = "select DECLARANT_CD from bk_details where bk_ref_nbr =:bookingRefNo ";
		String custId = "";
		try {
			log.info("START: getDeclarant  DAO  Start Obj " + " bookingRefNo:" + bookingRefNo);
			log.info(" *** getDeclarant SQL *****" + sql);

			paramMap.put("bookingRefNo", bookingRefNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next())
				custId = CommonUtility.deNull(rs.getString("DECLARANT_CD"));

			log.info("END: *** getDeclarant Result *****" + custId.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getDeclarant : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getDeclarant : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getDeclarant  DAO  END");
		}

		return custId;
	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getAccNo(String vslCd) throws BusinessException {
		String vsl_cd = "";
		vsl_cd = vslCd;
		String sql = "";
		// truckerCd = getCustId(tdbcrNo);
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		sql = "select co_nm,ACCT_NBR from company_code,cust_acct,vessel_call where cust_cd = co_cd and acct_status_cd='A' and business_type like '%G%' and trial_ind='N' and ACCT_NBR is not null  and co_cd = create_cust_cd and vv_cd=:vsl_cd ";

		// select prev_acct_nbr from cust_acct where cust_cd='"+truckerCd+"' and
		// acct_status_cd='A' and business_type like '%G%' and trial_ind='N' and
		// prev_acct_nbr is not null";

		List<TesnPsaJpEsnListValueObject> accNoList = new ArrayList<TesnPsaJpEsnListValueObject>();
		TesnPsaJpEsnListValueObject esnListValueObject = null;
		try {
			log.info("START: getAccNo  DAO  Start Obj " + " vsl_cd:" + vsl_cd);

			log.info(" *** getAccNo SQL *****" + sql);

			paramMap.put("vsl_cd", vsl_cd);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				esnListValueObject = new TesnPsaJpEsnListValueObject();
				esnListValueObject.setAccNo(CommonUtility.deNull(rs.getString("ACCT_NBR")));
				accNoList.add(esnListValueObject);
				// log.info("Db Value :"+rs.getString("esn_nbr"));
			}
			log.info("END: *** getAccNo Result *****" + accNoList.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getAccNo : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getAccNo : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getAccNo  DAO  END");
		}

		return accNoList;
	}

	@Override
	public boolean chkVolume(String bookRefNo, double volume_s) throws BusinessException {

		String bookingRefNo = bookRefNo;
		double volume = volume_s;
		double varientVolume = 0;
		double allowedVolume = 0;
		String sql;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		boolean result = true;

		sql = "SELECT BK_VOL,VARIANCE_VOL FROM bk_details WHERE BK_REF_NBR =:bookingRefNo ";

		try {
			log.info("START: chkVolume  DAO  Start Obj " + " bookRefNo:" + bookRefNo + " volume_s:" + volume_s);

			log.info(" *** chkVolume SQL *****" + sql);

			paramMap.put("bookingRefNo", bookingRefNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				varientVolume = rs.getDouble("VARIANCE_VOL") / 100;
				allowedVolume = (varientVolume + 1) * (rs.getDouble("BK_VOL"));
				if (volume > allowedVolume)
					result = false;
				else
					result = true;
			} else
				result = false;

			log.info("END: *** chkVolume Result *****" + result);
		} catch (NullPointerException ne) {
			log.info("Exception chkVolume : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkVolume : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: chkVolume  DAO  END");
		}

		return result;
	}

	@Override
	public boolean chkWeight(String bookRefNo, double weight_s) throws BusinessException {

		String bookingRefNo = bookRefNo;
		double weight = weight_s;
		double varientWeight = 0;
		double allowedWeight = 0;
		String sql;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		boolean result = true;

		sql = "SELECT BK_WT,VARIANCE_WT FROM bk_details WHERE BK_REF_NBR =:bookingRefNo ";

		try {
			log.info("START: chkWeight  DAO  Start Obj " + " bookingRefNo:" + bookingRefNo + " weight_s:" + weight_s);

			log.info(" *** chkWeight SQL *****" + sql);

			paramMap.put("bookingRefNo", bookingRefNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				varientWeight = rs.getDouble("VARIANCE_WT") / 100;
				allowedWeight = (varientWeight + 1) * (rs.getDouble("BK_WT"));
				if (weight > allowedWeight)
					result = false;
				else
					result = true;
			} else
				result = false;

			log.info("END: *** chkWeight Result *****" + result);
		} catch (NullPointerException ne) {
			log.info("Exception chkWeight : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkWeight : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: chkWeight  DAO  END");
		}

		return result;
	}

	@Override
	public boolean chkPkgsType(String pkgs_Type) throws BusinessException {

		String pkgsType = pkgs_Type;
		String sql;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		boolean result = true;
		// Added by Linus on 8 Oct 2003
		sql = "SELECT PKG_TYPE_CD FROM PKG_TYPES WHERE PKG_TYPE_CD =:pkgsType AND REC_STATUS='A'";
		// Before
		// sql = "SELECT PKG_TYPE_CD FROM PKG_TYPES WHERE PKG_TYPE_CD = '" + pkgsType +
		// "'";
		// End Change
		try {
			log.info("START: chkPkgsType  DAO  Start Obj " + " pkgsType:" + pkgsType);

			log.info(" *** chkPkgsType SQL *****" + sql);

			paramMap.put("pkgsType", pkgsType);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next())
				result = true;
			else
				result = false;

			log.info("END: *** chkPkgsType Result *****" + result);
		} catch (NullPointerException ne) {
			log.info("Exception chkPkgsType : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkPkgsType : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: chkPkgsType  DAO  END");
		}

		return result;
	}

	@Override
	public boolean chkNoOfPkgs(String bookRefNo, int noOfpk) throws BusinessException {

		String bookingRefNo = bookRefNo;
		int noOfPkgs = noOfpk;
		double varientPkgs = 0;
		double allowedPkgs = 0;
		String sql;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		boolean result = true;

		sql = "SELECT BK_NBR_PKGS,VARIANCE_PKGS FROM bk_details WHERE BK_REF_NBR =:bookingRefNo ";

		try {

			log.info("START: chkNoOfPkgs  DAO  Start Obj " + " bookingRefNo:" + bookingRefNo + " noOfpk:" + noOfpk);

			log.info(" *** chkNoOfPkgs SQL *****" + sql);

			paramMap.put("bookingRefNo", bookingRefNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				varientPkgs = rs.getDouble("VARIANCE_PKGS") / 100;
				allowedPkgs = (varientPkgs + 1) * (rs.getInt("BK_NBR_PKGS"));
				if (noOfPkgs > allowedPkgs)
					result = false;
				else
					result = true;
			} else
				result = false;

			log.info("END: *** chkNoOfPkgs Result *****" + result);
		} catch (NullPointerException ne) {
			log.info("Exception chkNoOfPkgs : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkNoOfPkgs : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: chkNoOfPkgs  DAO  END");
		}

		return result;
	}

	@Override
	public boolean chkOutwardPM4(String bkRefNo, String vvCd) throws BusinessException {

		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String mpaAppvStatus, jpAppvStatus;
		boolean exists = false;
		sql = "SELECT MPA_APPV_STATUS, JP_APPV_STATUS FROM PM4 WHERE (UCR_NBR = :bkRefNo) AND (VV_CD = :vvCd) AND (OPR_TYPE = 'L') AND (RECORD_TYPE <> 'D')";

		try {
			log.info("START: chkOutwardPM4  DAO  Start Obj " + " bkRefNo:" + bkRefNo + " vvCd:" + vvCd);

			log.info(" *** chkOutwardPM4 SQL *****" + sql);

			paramMap.put("bkRefNo", bkRefNo);
			paramMap.put("vvCd", vvCd);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				exists = true;
				do {
					mpaAppvStatus = CommonUtility.deNull(rs.getString(1));
					jpAppvStatus = CommonUtility.deNull(rs.getString(2));
					if (!(mpaAppvStatus.equalsIgnoreCase("A") && jpAppvStatus.equalsIgnoreCase("A"))) {
						log.info("mpaAppvStatus=" + mpaAppvStatus + ", jpAppvStatus=" + jpAppvStatus);
						exists = false;
						break;
					}
				} while (rs.next());
			} else {
				log.info("PM4 does not exist, even for a single item");
				exists = false;
			}

			/*
			 * while (rs.next()) { //recExistFlg=true; if(
			 * rs.getString("MPA_APPV_STATUS").equalsIgnoreCase("A") &&
			 * rs.getString("JP_APPV_STATUS").equalsIgnoreCase("A") ){ chkFlag=true; break;
			 * } }
			 */

			log.info("END: *** chkOutwardPM4 Result *****" + exists);
		} catch (NullPointerException ne) {
			log.info("Exception chkOutwardPM4 : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkOutwardPM4 : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: chkOutwardPM4  DAO  END");
		}

		return exists;
	}

	@Override
	public boolean chkDttmOfSecondCarrierVsl(String vvCd) throws BusinessException {

		String sql;
		int count = 0;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		boolean result = false;
		sql = "SELECT count(*) FROM vessel_call vsl WHERE vsl.vv_cd = :vvCd AND (vsl.vv_status_ind not in ('UB', 'CL', 'CX' ) AND  nvl(vsl.GB_CLOSE_SHP_IND,'N') != 'Y')";

		try {
			log.info("START: chkDttmOfSecondCarrierVsl  DAO  Start Obj " + " vvCd:" + vvCd);

			log.info(" *** chkDttmOfSecondCarrierVsl SQL *****" + sql);

			paramMap.put("vvCd", vvCd);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				count = rs.getInt(1);
				if (count > 0)
					result = true;
				else
					result = false;
			}
			log.info("END: *** chkDttmOfSecondCarrierVsl Result *****" + result);
		} catch (NullPointerException ne) {
			log.info("Exception chkDttmOfSecondCarrierVsl : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkDttmOfSecondCarrierVsl : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: chkDttmOfSecondCarrierVsl  DAO  END");
		}

		return result;
	}

	@Override
	public TruckerValueObject getTruckerDetails(String truckerIc) throws BusinessException {

		if (StringUtils.isBlank(truckerIc)) {
			return new TruckerValueObject();
		}
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		TruckerValueObject truckerValueObject = null;
		try {
			log.info("START: getTruckerDetails  DAO  Start Obj " + " truckerIc:" + truckerIc);

			sb.append("SELECT CO_NM, NVL(PHONE1_NBR, PHONE2_NBR) PHONE1_NBR, cc.CO_CD FROM customer cust ");
			sb.append("LEFT JOIN company_code cc on cust.cust_cd = cc.co_cd ");
			sb.append("LEFT JOIN cust_contact ct on cust.cust_cd = ct.CUST_CD ");
			sb.append("WHERE (LOWER(TDB_CR_NBR) = :truckerIc or LOWER(UEN_NBR) = :truckerIc");
			sb.append(
					" ) AND cc.rec_status = 'A' order by cc.rec_status desc, (case when cc.lob_cd = 'HAU' then 2 else 1 end)");
			sql = sb.toString();

			// HAU added to retreive Haulier type and valid company code

			log.info(" *** getTruckerDetails SQL *****" + sql);

			paramMap.put("truckerIc", StringUtils.lowerCase(truckerIc));
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			truckerValueObject = new TruckerValueObject();
			truckerValueObject.setTruckerIc(truckerIc);
			if (rs.next()) {
				truckerValueObject.setTruckerNm(CommonUtility.deNull(rs.getString("CO_NM")));
				truckerValueObject.setTruckerContact(CommonUtility.deNull(rs.getString("PHONE1_NBR")));
				truckerValueObject.setTruckerCd(CommonUtility.deNull(rs.getString("CO_CD")));
			} else {

				sb = new StringBuffer();

				sb.append("SELECT CUST_NAME CO_NM, cust.CUST_CD CO_CD, NVL(PHONE1_NBR, PHONE2_NBR) PHONE1_NBR ");
				sb.append("FROM JC_CARDDTL cust ");
				sb.append("LEFT JOIN cust_contact ct on cust.cust_cd = ct.CUST_CD ");
				sb.append(
						"WHERE LOWER(PASSPORT_NO)     = :truckerIc OR LOWER(cust.FIN_NO)=:truckerIc OR LOWER(cust.NRIC_NO )=:truckerIc ");
				sql = sb.toString();

				log.info(" *** getCargoCategoryCode_CargoCategoryName SQL *****" + sql);

				paramMap.put("truckerIc", StringUtils.lowerCase(truckerIc));
				log.info(param + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs.next()) {
					truckerValueObject.setTruckerNm(CommonUtility.deNull(rs.getString("CO_NM")));
					truckerValueObject.setTruckerContact(CommonUtility.deNull(rs.getString("PHONE1_NBR")));
					truckerValueObject.setTruckerCd(CommonUtility.deNull(rs.getString("CO_CD")));
				} else {
					truckerValueObject.setTruckerNm("");
					truckerValueObject.setTruckerContact("");
					truckerValueObject.setTruckerCd("");
				}
			}

			log.info("END: *** getTruckerDetails Result *****" + truckerValueObject.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getTruckerDetails : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTruckerDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTruckerDetails  DAO  END");
		}

		return truckerValueObject;
	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getBkRefNo(String bkRefNo, String cutId) throws BusinessException {
		String bookingRefNo = "";
		boolean bkRefNo_flag = false;
		String custId = "";
		bookingRefNo = bkRefNo;
		custId = cutId;
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();

		List<TesnPsaJpEsnListValueObject> bookingRef = new ArrayList<TesnPsaJpEsnListValueObject>();
		TesnPsaJpEsnListValueObject esnListValueObject = null;
		try {
			log.info("START: getBkRefNo  DAO  Start Obj " + " bkRefNo:" + bkRefNo + " cutId:" + cutId);

			String portDisDesc = "";

			if (custId.equals("JP")) {

				sb.append(
						"select BK_REF_NBR,BKD.OUT_VOY_NBR,CNTR_SIZE,CNTR_TYPE,CRG_TYPE_NM,SHIPPER_NM,PORT_DIS,DECLARANT_CD,VSL_NM,NBR_OF_CNTR,VAR_NBR,BK_WT,BK_VOL,BK_NBR_PKGS,VARIANCE_PKGS,VARIANCE_VOL,VARIANCE_WT,VSL.gb_close_shp_ind from ");
				sb.append("bk_details bkd,VESSEL_CALL VSL,CRG_TYPE where bk_ref_nbr = :bookingRefNo ");
				sb.append("AND VSL.VV_CD = BKD.VAR_NBR AND  BKD.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD ");

				sql = sb.toString();
			} else {

				sb.append(
						"select BK_REF_NBR,BKD.OUT_VOY_NBR,CNTR_SIZE,CNTR_TYPE,CRG_TYPE_NM,SHIPPER_NM,PORT_DIS,DECLARANT_CD,VSL_NM,NBR_OF_CNTR,VAR_NBR,BK_WT,BK_VOL,BK_NBR_PKGS,VARIANCE_PKGS,VARIANCE_VOL,VARIANCE_WT,VSL.gb_close_shp_ind from ");
				sb.append(
						"bk_details bkd,VESSEL_CALL VSL,CRG_TYPE where bk_ref_nbr = :bookingRefNo and BKD.DECLARANT_CD=:custId ");
				sb.append("AND VSL.VV_CD = BKD.VAR_NBR AND  BKD.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD ");

				sql = sb.toString();
			}

			log.info(" *** getBkRefNo SQL *****" + sql);

			if (custId.equals("JP")) {
				paramMap.put("bookingRefNo", bookingRefNo);
			} else {
				paramMap.put("bookingRefNo", bookingRefNo);
				paramMap.put("custId", custId);
			}
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				bkRefNo_flag = true;
				esnListValueObject = new TesnPsaJpEsnListValueObject();
				esnListValueObject.setVoyNo(CommonUtility.deNull(rs.getString("OUT_VOY_NBR")));
				esnListValueObject.setVslName(CommonUtility.deNull(rs.getString("vsl_nm")));
				esnListValueObject.setVarNbr(CommonUtility.deNull(rs.getString("VAR_NBR")));
				esnListValueObject.setBookingRefNo(CommonUtility.deNull(rs.getString("BK_REF_NBR")));
				esnListValueObject.setCrgType(CommonUtility.deNull(rs.getString("CRG_TYPE_NM")));
				esnListValueObject.setShipperName(CommonUtility.deNull(rs.getString("SHIPPER_NM")));
				esnListValueObject.setPortD(CommonUtility.deNull(rs.getString("PORT_DIS")));
				portDisDesc = getPortDisDesc(CommonUtility.deNull(rs.getString("PORT_DIS")));
				esnListValueObject.setPortL(portDisDesc);
				esnListValueObject.setCustId(CommonUtility.deNull(rs.getString("DECLARANT_CD")));
				esnListValueObject.setCntrSize(CommonUtility.deNull(rs.getString("CNTR_SIZE")));
				esnListValueObject.setCntrType(CommonUtility.deNull(rs.getString("CNTR_TYPE")));
				esnListValueObject.setNoOfCntr(rs.getInt("NBR_OF_CNTR"));
				esnListValueObject.setGrWt(rs.getDouble("BK_WT"));
				esnListValueObject.setGrVolume(rs.getDouble("BK_VOL"));
				esnListValueObject.setBNoofPkgs(rs.getInt("BK_NBR_PKGS"));
				esnListValueObject.setStgInd("gb_close_shp_ind");
				esnListValueObject.setVarGrVolume(rs.getDouble("VARIANCE_VOL"));
				esnListValueObject.setVarGrWt(rs.getDouble("VARIANCE_WT"));
				esnListValueObject.setVarNoofPakgs(rs.getDouble("VARIANCE_PKGS"));
				bookingRef.add(esnListValueObject);
				// log.info("Db Value :"+rs.getString("esn_nbr"));
			}

			log.info("END: *** getBkRefNo Result *****" + bookingRef.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getBkRefNo : ", ne);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception getBkRefNo : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getBkRefNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBkRefNo  DAO  END");
		}

		if (bkRefNo_flag) {
			return bookingRef;
		} else
			return null;
	}

	@Override
	public void AssignWhindUpdate(String crgval, String esnnbr, String whappnbr, String remarks, String nodays,
			String userId) throws BusinessException {

		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();

		String sqltlog = "";
		String strInsert_trans = "";
		int stransno = 0;
		int count_trans = 0;

		try {
			log.info("START: AssignWhindUpdate  DAO  Start Obj " + " crgval:" + crgval + " esnnbr:" + esnnbr
					+ " whappnbr:" + whappnbr + " remarks:" + remarks + " nodays:" + nodays + " userId:" + userId);

			if (crgval.equals("Y")) {
				sb.append("UPDATE esn SET WH_IND =:crgval,WH_AGGR_NBR = :whappnbr,WH_REMARKS=:remarks,");
				sb.append(
						"FREE_STG_DAYS = 0,LAST_MODIFY_USER_ID=:userId,LAST_MODIFY_DTTM = sysdate WHERE ESN_ASN_NBR =:esnnbr ");

				sql = sb.toString();
			}

			else if (crgval.equals("NO")) {
				sql = "UPDATE esn SET WH_IND ='N',WH_AGGR_NBR='',WH_REMARKS = '',FREE_STG_DAYS = 0,LAST_MODIFY_USER_ID=:userId,LAST_MODIFY_DTTM = sysdate WHERE ESN_ASN_NBR =:esnnbr";
			}

			else {
				sb.append("UPDATE esn SET WH_IND =:crgval,WH_AGGR_NBR='',WH_REMARKS = '',");
				sb.append(
						"FREE_STG_DAYS =:nodays,LAST_MODIFY_USER_ID=:userId,LAST_MODIFY_DTTM = sysdate WHERE ESN_ASN_NBR =:esnnbr");
				sql = sb.toString();
			}

			log.info(" *** AssignWhindUpdate SQL *****" + sql);

			if (crgval.equals("Y")) {
				paramMap.put("crgval", crgval);
				paramMap.put("whappnbr", whappnbr);
				paramMap.put("userId", userId);
				paramMap.put("esnnbr", esnnbr);
				paramMap.put("remarks", GbmsCommonUtility.addApostr(remarks));
			} else if (crgval.equals("NO")) {
				paramMap.put("userId", userId);
				paramMap.put("esnnbr", esnnbr);
			} else {
				paramMap.put("crgval", crgval);
				paramMap.put("nodays", nodays);
				paramMap.put("userId", userId);
				paramMap.put("esnnbr", esnnbr);
			}
			log.info(param + paramMap);
			int count = namedParameterJdbcTemplate.update(sql, paramMap);

			sqltlog = "SELECT MAX(TRANS_NBR) FROM ESN_TRANS WHERE ESN_ASN_NBR=:esnnbr ";

			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003

				log.info(" *** AssignWhindUpdate SQL *****" + sqltlog);

				paramMap.put("esnnbr", esnnbr);
				log.info(param + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);
				if (rs.next()) {
					stransno = (rs.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
			}

			sb = new StringBuffer();

			if (crgval.equals("Y")) {

				sb.append("INSERT INTO ESN_TRANS(TRANS_NBR,ESN_ASN_NBR,");
				sb.append("WH_IND,WH_AGGR_NBR,WH_REMARKS,FREE_STG_DAYS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
				sb.append("VALUES(:stransno,:esnnbr,:crgval,:whappnbr,:remarks,0,:userId,sysdate)");

				strInsert_trans = sb.toString();

			} else if (crgval.equals("NO")) {

				sb.append("INSERT INTO ESN_TRANS(TRANS_NBR,ESN_ASN_NBR,");
				sb.append("WH_IND,WH_AGGR_NBR,WH_REMARKS,FREE_STG_DAYS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)");
				sb.append(" VALUES(:stransno,:esnnbr,'N','','',0,:userId,sysdate) ");

				strInsert_trans = sb.toString();
			} else {

				sb.append("INSERT INTO ESN_TRANS(TRANS_NBR,ESN_ASN_NBR,");
				sb.append("WH_IND,WH_AGGR_NBR,WH_REMARKS,FREE_STG_DAYS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
				sb.append("VALUES(:stransno,:esnnbr,:crgval,'','',:nodays,:userId,sysdate) ");

				strInsert_trans = sb.toString();

			}

			if (logStatusGlobal.equalsIgnoreCase("Y")) {

				log.info(" *** AssignWhindUpdate SQL *****" + strInsert_trans);

				if (crgval.equals("Y")) {

					paramMap.put("stransno", Integer.toString(stransno));
					paramMap.put("esnnbr", esnnbr);
					paramMap.put("crgval", crgval);
					paramMap.put("whappnbr", whappnbr);
					paramMap.put("remarks", GbmsCommonUtility.addApostr(remarks));
					paramMap.put("userId", userId);

				} else if (crgval.equals("NO")) {

					paramMap.put("stransno", Integer.toString(stransno));
					paramMap.put("esnnbr", esnnbr);
					paramMap.put("userId", userId);
				} else {

					paramMap.put("stransno", Integer.toString(stransno));
					paramMap.put("esnnbr", esnnbr);
					paramMap.put("crgval", crgval);
					paramMap.put("nodays", nodays);
					paramMap.put("userId", userId);

				}
				log.info(param + paramMap);
				count_trans = namedParameterJdbcTemplate.update(strInsert_trans, paramMap);
			}

			if (count == 0) {
				log.info("Writing from TesnPsaJpEJB.AssignWhindUpdate");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 21/01/2003
				if (count_trans == 0) {
					log.info("Writing from TesnPsaJpEJB.AssignWhindUpdate");
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}
			}
			log.info("END: *** AssignWhindUpdate Result *****" + count_trans);
		} catch (NullPointerException ne) {
			log.info("Exception AssignWhindUpdate : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception AssignWhindUpdate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: AssignWhindUpdate  DAO  END");
		}

	}

	@Override
	public void EsnAssignVslUpdate(String vv_cd, String status, String userId) throws BusinessException {
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();

		sql = "UPDATE vessel_call SET mixed_scheme_ind=:status,LAST_MODIFY_DTTM=sysdate ,LAST_MODIFY_USER_ID=:userId WHERE vv_cd =:vv_cd ";
		// log.info("sql esn assign vsl update "+sql);
		try {
			log.info("START: EsnAssignVslUpdate  DAO  Start Obj " + " vv_cd:" + vv_cd + " status:" + status + " userId:"
					+ userId);

			log.info(" *** EsnAssignVslUpdate SQL *****" + sql);

			paramMap.put("status", status);
			paramMap.put("userId", userId);
			paramMap.put("vv_cd", vv_cd);
			log.info(param + paramMap);
			int count = namedParameterJdbcTemplate.update(sql, paramMap);

			if (count == 0) {
				log.info("Writing from EsnEJB.EsnAssignVslUpdate");
				log.info("Record Cannot be added to Database");
				//throw new BusinessException("M4201");
			}
			log.info("END: *** EsnAssignVslUpdate Result *****" + count);
		} catch (NullPointerException ne) {
			log.info("Exception EsnAssignVslUpdate : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception EsnAssignVslUpdate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: EsnAssignVslUpdate  DAO  END");
		}

	}

	@Override
	public void EsnAssignBillUpdate(String acctnbr, String esno, String userid) throws BusinessException {
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		String sqltlog = "";
		String strInsert_trans = "";
		int stransno = 0;
		int count_trans = 0;

		boolean bactnbr = checkAccountNbr(acctnbr);
		if (!bactnbr) {
			log.info("Writing from EsnEJB.EsnAssignBillUpdate");
			log.info("Invalid Account Nbr" + acctnbr);
			// throw new BusinessException("M20801");
		}

		sql = "UPDATE tesn_psa_jp SET MIXED_SCHEME_ACCT_NBR=:acctnbr WHERE  ESN_ASN_NBR =:esno ";

		try {
			log.info("START: EsnAssignBillUpdate  DAO  Start Obj " + " acctnbr:" + acctnbr + " esno:" + esno
					+ " userid:" + userid);

			log.info(" *** EsnAssignBillUpdate SQL *****" + sql);

			paramMap.put("acctnbr", acctnbr);
			paramMap.put("esno", esno);
			log.info(param + paramMap);
			int count = namedParameterJdbcTemplate.update(sql, paramMap);

			sqltlog = "SELECT MAX(TRANS_NBR) FROM TESN_PSA_JP_TRANS WHERE ESN_ASN_NBR=:esno ";

			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003

				log.info(" *** EsnAssignBillUpdate SQL *****" + sqltlog);

				paramMap.put("esno", esno);
				log.info(param + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);
				if (rs.next()) {
					stransno = (rs.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
			}

			sb.append("INSERT INTO TESN_PSA_JP_TRANS(TRANS_NBR,ESN_ASN_NBR,");
			sb.append("MIXED_SCHEME_ACCT_NBR,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
			sb.append("VALUES(:stransno,:esno,:acctnbr,:userid,sysdate)");

			strInsert_trans = sb.toString();

			if (logStatusGlobal.equalsIgnoreCase("Y")) {

				log.info(" *** EsnAssignBillUpdate SQL *****" + strInsert_trans);

				paramMap.put("stransno", Integer.toString(stransno));
				paramMap.put("esno", esno);
				paramMap.put("acctnbr", acctnbr);
				paramMap.put("userid", userid);
				log.info(param + paramMap);
				count_trans = namedParameterJdbcTemplate.update(strInsert_trans, paramMap);
			}

			if (count == 0) {
				log.info("Writing from PSAJPEJB.EsnAssignBillUpdate");
				log.info("Record Cannot be added to Database");
				//throw new BusinessException("M4201");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003
				if (count_trans == 0) {
					log.info("Writing from PSAJPEJB.EsnAssignBillUpdate");
					log.info("Record Cannot be added to Database");
					//throw new BusinessException("M4201");
				}
			}

			log.info("END: *** EsnAssignBillUpdate Result *****" + count);
		} catch (NullPointerException ne) {
			log.info("Exception EsnAssignBillUpdate : ", ne);
			throw new BusinessException("M4201");
//		} catch (BusinessException e) {
//			log.info("Exception EsnAssignBillUpdate : ", e);
//			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception EsnAssignBillUpdate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: EsnAssignBillUpdate  DAO  END");
		}

	}

	private boolean checkAccountNbr(String accnbr) throws BusinessException {

		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = "";
		int intaccnbrcount = 0;

		String straccnbrcount = "";
		try {
			log.info("START: checkAccountNbr  DAO  Start Obj " + " accnbr:" + accnbr);

			sb.append("SELECT COUNT(*) FROM CUST_ACCT A, COMPANY_CODE B, ");
			sb.append("CUSTOMER C  WHERE A.BUSINESS_TYPE LIKE '%G%' AND ");
			sb.append("A.ACCT_NBR IS NOT NULL AND B.CO_CD=C.CUST_CD  AND ");
			sb.append("UPPER(A.ACCT_NBR)=UPPER(:accnbr)");
			sql = sb.toString();

			log.info(" *** checkAccountNbr SQL *****" + sql);

			paramMap.put("accnbr", accnbr);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				straccnbrcount = CommonUtility.deNull(rs.getString(1));
			}
			if (((straccnbrcount).trim().equalsIgnoreCase("")) || straccnbrcount == null) {
				straccnbrcount = "0";
			}
			intaccnbrcount = Integer.parseInt(straccnbrcount);

			log.info("END: *** checkAccountNbr Result *****" + intaccnbrcount);
			if (intaccnbrcount > 0) {
				return true;
			} else {
				return false;
			}

		} catch (NullPointerException ne) {
			log.info("Exception checkAccountNbr : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkAccountNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkAccountNbr  DAO  END");
		}

	}

	@Override
	public void AssignCrgvalUpdate(String crgval, String esnnbr, String userId) throws BusinessException {
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();

		String sqltlog = "";
		String strInsert_trans = "";
		int stransno = 0;
		int count_trans = 0;

		sql = "UPDATE esn SET CARGO_CATEGORY_CD =:crgval,LAST_MODIFY_USER_ID=:userId,LAST_MODIFY_DTTM = sysdate WHERE ESN_ASN_NBR =:esnnbr";

		try {
			log.info("START: AssignCrgvalUpdate  DAO  Start Obj " + " crgval:" + crgval + " esnnbr:" + esnnbr
					+ " userId:" + userId);

			log.info(" *** AssignCrgvalUpdate SQL *****" + sql);

			paramMap.put("crgval", crgval);
			paramMap.put("userId", userId);
			paramMap.put("esnnbr", esnnbr);
			log.info(param + paramMap);
			int count = namedParameterJdbcTemplate.update(sql, paramMap);

			sqltlog = "SELECT MAX(TRANS_NBR) FROM ESN_TRANS WHERE ESN_ASN_NBR=:esnnbr ";

			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003

				log.info(" *** AssignCrgvalUpdate SQL *****" + sqltlog);

				paramMap.put("crgval", crgval);
				log.info(param + paramMap);
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

				paramMap.put("stransno", Integer.toString(stransno));
				paramMap.put("esnnbr", esnnbr);
				paramMap.put("crgval", crgval);
				paramMap.put("userId", userId);
				log.info(param + paramMap);
				count_trans = namedParameterJdbcTemplate.update(strInsert_trans, paramMap);
			}

			if (count == 0) {
				log.info("Writing from EsnEJB.AssignCrgvalUpdate");
				log.info("Record Cannot be added to Database");
				//throw new BusinessException("M4201");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 21/01/2003
				if (count_trans == 0) {
					log.info("Writing from EsnEJB.AssignCrgvalUpdate");
					log.info("Record Cannot be added to Database");
					//throw new BusinessException("M4201");
				}
			}
			log.info("END: *** AssignCrgvalUpdate Result *****" + count);
		} catch (NullPointerException ne) {
			log.info("Exception AssignCrgvalUpdate : ", ne);
			throw new BusinessException("M4201");
//		} catch (BusinessException e) {
//			log.info("Exception AssignCrgvalUpdate : ", e);
//			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception AssignCrgvalUpdate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: AssignCrgvalUpdate  DAO  END");
		}

	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getAssignCargo() throws BusinessException {
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String cc_cd = "";
		String cicos_cd = "";
		String cc_name = "";
		List<TesnPsaJpEsnListValueObject> maniveclist = new ArrayList<TesnPsaJpEsnListValueObject>();
		TesnPsaJpEsnListValueObject esnListValueObject = null;

		// Changed added by Linus on 8 Oct 2003
		sql = "SELECT cc_cd, cicos_cd,cc_name FROM cargo_category_code where cc_status='A'";
		// Before
		// sql = "SELECT cc_cd, cicos_cd,cc_name FROM cargo_category_code";
		// End Change

		try {
			log.info("START: getAssignCargo  DAO  Start Obj ");

			log.info(" *** getAssignCargo SQL *****" + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				esnListValueObject = new TesnPsaJpEsnListValueObject();
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
		} catch (NullPointerException ne) {
			log.info("Exception getAssignCargo : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getAssignCargo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAssignCargo  DAO  END");
		}

		return maniveclist;
	}

	@Override
	public String AssignCrgvalCheck(String esnnbr) throws BusinessException {
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String crgCd = "";

		sql = "SELECT CARGO_CATEGORY_CD from esn WHERE ESN_ASN_NBR =:esnnbr ";

		try {

			log.info("START: AssignCrgvalCheck  DAO  Start Obj " + " esnnbr:" + esnnbr);

			log.info(" *** AssignCrgvalCheck SQL *****" + sql);

			paramMap.put("esnnbr", esnnbr);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				crgCd = rs.getString("CARGO_CATEGORY_CD");
			}
			log.info("END: *** AssignCrgvalCheck Result *****" + crgCd.toString());
		} catch (NullPointerException ne) {
			log.info("Exception AssignCrgvalCheck : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception AssignCrgvalCheck : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: AssignCrgvalCheck  DAO  END");
		}

		return crgCd;

	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getEdiDetails(String bkNbr) throws BusinessException {

		String billableParty = "";
		String accNo = "";
		String portDis = "";
		String portDisDesc = "";
		String sql = "";

		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		
		sql = "select * from igd_detail_buffer where BK_REF_NBR = :bkNbr ";
		List<TesnPsaJpEsnListValueObject> esnList = new ArrayList<TesnPsaJpEsnListValueObject>();
		TesnPsaJpEsnListValueObject esnListValueObject = new TesnPsaJpEsnListValueObject();
		try {
			log.info("START: getEdiDetails  DAO  Start Obj " + " bkNbr:" + bkNbr);

			log.info(" *** getEdiDetails SQL *****" + sql);

			paramMap.put("bkNbr", bkNbr);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				esnListValueObject.setBookingRefNo(CommonUtility.deNull(rs.getString("BK_REF_NBR")));
				esnListValueObject.setShipperName(CommonUtility.deNull(rs.getString("SHIPPER")));
				esnListValueObject.setPkgType(CommonUtility.deNull(rs.getString("PKG_TYPE")));
				esnListValueObject.setCrgDesc(CommonUtility.deNull(rs.getString("CRG_DESC")));
				esnListValueObject.setHsCode(CommonUtility.deNull(rs.getString("HS_CD")));
				esnListValueObject.setBGrVolume(rs.getDouble("VOLUME"));
				esnListValueObject.setBGrWt(rs.getDouble("WEIGHT"));
				esnListValueObject.setBNoofPkgs(rs.getInt("NO_OF_PKG"));
				esnListValueObject.setDgInd(CommonUtility.deNull(rs.getString("DG_IND")));
				accNo = CommonUtility.deNull(rs.getString("ACCT_NBR"));
				billableParty = getBillablePartyName(accNo);
				esnListValueObject.setBillPartyName(billableParty);
				esnListValueObject.setCrgMarking(CommonUtility.deNull(rs.getString("MARKINGS")));
				portDis = CommonUtility.deNull(rs.getString("DISC_PORT"));
				portDisDesc = getPortDisDesc(portDis);
				esnListValueObject.setAccNo(accNo);
				esnListValueObject.setPortDesc(portDisDesc);
				esnListValueObject.setCntr1(CommonUtility.deNull(rs.getString("CNT_NBR")));
				esnListValueObject.setStgInd(CommonUtility.deNull(rs.getString("REC_FUNCTION")));
				esnList.add(esnListValueObject);
			} else {
				throw new BusinessException("M4201");
			}
			log.info("END: *** getEdiDetails Result *****" + esnList.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getEdiDetails : ", ne);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception getEdiDetails : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getEdiDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEdiDetails  DAO  END");
		}

		return esnList;
	}

	@Override
	public String getClsShipInd_bkr(String bkrNbr) throws BusinessException {
		String clsShpInd = "";
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		sql = "select gb_close_shp_ind from bk_details where bk_ref_nbr = :bkrNbr ";

		try {
			log.info("START: getClsShipInd_bkr  DAO  Start Obj " + " bkrNbr:" + bkrNbr);

			log.info(" *** getClsShipInd_bkr SQL *****" + sql);

			paramMap.put("bkrNbr", bkrNbr);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				clsShpInd = CommonUtility.deNull(rs.getString("gb_close_shp_ind"));
			}
			log.info("END: *** getClsShipInd_bkr Result *****" + clsShpInd.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getClsShipInd_bkr : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getClsShipInd_bkr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getClsShipInd_bkr  DAO  END");
		}

		return clsShpInd;

	}

	@Override
	public String getEdiUpdate(String bkrefnbr, String status) throws BusinessException {

		Map<String, String> paramMap = new HashMap<String, String>();
		String strUpdate = new String();
		String refnbr = "";

		strUpdate = "UPDATE igd_detail_buffer SET IS_PROCESSED= :status where BK_REF_NBR = :bkrefnbr ";

		try {
			log.info("START: getEdiUpdate  DAO  Start Obj " + " status:" + status + " bkrefnbr:" + bkrefnbr);

			log.info(" *** getEdiUpdate SQL *****" + strUpdate);

			paramMap.put("bkrefnbr", bkrefnbr);
			paramMap.put("status", status);
			log.info(param + paramMap);
			int count = namedParameterJdbcTemplate.update(strUpdate, paramMap);
			refnbr = "" + count;
			if (count == 0) {
				log.info("Writing from EsnEJB.getEdiUpdate");
				log.info("Record Cannot be added to Database");
				//throw new BusinessException("M4201");
			}

			log.info("END: *** getEdiUpdate Result *****" + refnbr);
		} catch (NullPointerException ne) {
			log.info("Exception getEdiUpdate : ", ne);
			throw new BusinessException("M4201");
//		} catch (BusinessException e) {
//			log.info("Exception getEdiUpdate : ", e);
//			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getEdiUpdate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEdiUpdate  DAO  END");
		}

		return refnbr;
	}

	@Override
	public List<TruckerValueObject> getTruckerList(String esnNbr) throws BusinessException {

		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		List<TruckerValueObject> truckerVector = new ArrayList<TruckerValueObject>();
		TruckerValueObject truckerValueObject = null;
		try {
			log.info("START: getTruckerList  DAO  Start Obj " + " esnNbr:" + esnNbr);

			sb.append(
					"select tesn.TRUCKER_IC, tesn.TRUCKER_NM, tesn.TRUCKER_NBR_PKGS, tesn.TRUCKER_CONTACT_NBR, tesn.TRUCKER_CO_CD from tesn_psa_jp tesn ");
			sb.append("where tesn.ESN_ASN_NBR =:esnNbr and tesn.TRUCKER_IC is not null ");
			String sql = sb.toString();

			sb = new StringBuffer();
			sb.append(
					"select sub.TRUCKER_IC, sub.TRUCKER_NM, sub.TRUCKER_NBR_PKGS, sub.TRUCKER_CONTACT_NBR, sub.TRUCKER_CO_CD from sub_adp sub ");
			sb.append("where sub.ESN_ASN_NBR =:esnNbr and STATUS_CD = 'A' and EDO_ESN_IND = '0' order by SUB_ADP_NBR ");
			String subsql = sb.toString();

			log.info(" *** getTruckerList SQL *****" + sql);

			paramMap.put("esnNbr", esnNbr);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				truckerValueObject = new TruckerValueObject();
				truckerValueObject.setTruckerIc(CommonUtility.deNull(rs.getString("TRUCKER_IC")));
				truckerValueObject.setTruckerNm(CommonUtility.deNull(rs.getString("TRUCKER_NM")));
				truckerValueObject.setTruckerPkgs(CommonUtility.deNull(rs.getString("TRUCKER_NBR_PKGS")));
				truckerValueObject.setTruckerContact(CommonUtility.deNull(rs.getString("TRUCKER_CONTACT_NBR")));
				truckerValueObject.setTruckerCd(CommonUtility.deNull(rs.getString("TRUCKER_CO_CD")));
				truckerVector.add(truckerValueObject);
			}

			log.info(" *** getTruckerList SQL *****" + subsql);

			paramMap.put("esnNbr", esnNbr);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(subsql, paramMap);
			while (rs.next()) {
				truckerValueObject = new TruckerValueObject();
				truckerValueObject.setTruckerIc(CommonUtility.deNull(rs.getString("TRUCKER_IC")));
				truckerValueObject.setTruckerNm(CommonUtility.deNull(rs.getString("TRUCKER_NM")));
				truckerValueObject.setTruckerPkgs(CommonUtility.deNull(rs.getString("TRUCKER_NBR_PKGS")));
				truckerValueObject.setTruckerContact(CommonUtility.deNull(rs.getString("TRUCKER_CONTACT_NBR")));
				truckerValueObject.setTruckerCd(CommonUtility.deNull(rs.getString("TRUCKER_CO_CD")));
				truckerVector.add(truckerValueObject);
			}
			log.info("END: *** getTruckerList Result *****" + truckerVector.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getTruckerList : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTruckerList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTruckerList  DAO  END");
		}

		return truckerVector;
	}

	@Override
	public List<String> getWHDetails(String esnWhindcheck, String esnnbr) throws BusinessException {
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		List<String> whIndDetails = new ArrayList<String>();
		String remarks = "";
		String nodays = "";
		String whappnbr = "";

		try {
			log.info("START: getWHDetails  DAO  Start Obj " + " esnnbr:" + esnnbr + " esnWhindcheck:" + esnWhindcheck);

			if (esnWhindcheck != null && !esnWhindcheck.equals("") && esnWhindcheck.equals("Y"))
				sql = "SELECT WH_REMARKS,WH_AGGR_NBR from esn WHERE ESN_ASN_NBR =:esnnbr ";
			else
				sql = "SELECT FREE_STG_DAYS from esn WHERE ESN_ASN_NBR =:esnnbr ";

			log.info(" *** getWHDetails SQL *****" + sql);

			paramMap.put("esnnbr", esnnbr);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				if (esnWhindcheck != null && !esnWhindcheck.equals("") && esnWhindcheck.equals("Y")) {
					remarks = rs.getString("WH_REMARKS");
					whappnbr = rs.getString("WH_AGGR_NBR");
					whIndDetails.add(remarks);
					whIndDetails.add(whappnbr);
				} else {
					nodays = rs.getString("FREE_STG_DAYS");
					whIndDetails.add(nodays);
				}
			}
			log.info("END: *** getWHDetails Result *****" + whIndDetails.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getWHDetails : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getWHDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getWHDetails  DAO  END");
		}

		return whIndDetails;

	}

	@Override
	public String AssignWhindCheck(String esnnbr) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String crgCd = "";

		sql = "SELECT WH_IND from esn WHERE ESN_ASN_NBR =:esnnbr";

		try {
			log.info("START: AssignWhindCheck  DAO  Start Obj " + " esnnbr:" + esnnbr);

			log.info(" *** AssignWhindCheck SQL *****" + sql);

			paramMap.put("esnnbr", esnnbr);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				crgCd = rs.getString("WH_IND");
			}

			log.info("END: *** AssignWhindCheck Result *****" + crgCd.toString());
		} catch (NullPointerException ne) {
			log.info("Exception AssignWhindCheck : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception AssignWhindCheck : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: AssignWhindCheck  DAO  END");
		}

		return crgCd;

	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getCntrDetails(String esnNbr) throws BusinessException {
		String esnNo = esnNbr;
		String sql = "";

		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		sql = "select CNTR_NBR,ESN_CNTR_SEQ,CNTR_TYPE,CNTR_SIZE from bk_details bkd,esn es,esn_cntr ec where es.BK_REF_NBR = bkd.BK_REF_NBR and es.esn_asn_nbr = ec.esn_asn_nbr and es.esn_asn_nbr=:esnNo ";

		List<TesnPsaJpEsnListValueObject> cntrDetails = new ArrayList<TesnPsaJpEsnListValueObject>();
		TesnPsaJpEsnListValueObject esnListValueObject = new TesnPsaJpEsnListValueObject();
		try {
			log.info("START: getCntrDetails  DAO  Start Obj " + " esnNbr:" + esnNbr);

			log.info(" *** getCntrDetails SQL *****" + sql);

			paramMap.put("esnNo", esnNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			int cntrsqno = 0;
			while (rs.next()) {
				esnListValueObject.setCntrSize(CommonUtility.deNull(rs.getString("CNTR_SIZE")));
				esnListValueObject.setCntrType(CommonUtility.deNull(rs.getString("CNTR_TYPE")));
				cntrsqno = rs.getInt("ESN_CNTR_SEQ");
				if (cntrsqno == 1)
					esnListValueObject.setCntr1(CommonUtility.deNull(rs.getString("CNTR_NBR")));
				if (cntrsqno == 2)
					esnListValueObject.setCntr2(CommonUtility.deNull(rs.getString("CNTR_NBR")));
				if (cntrsqno == 3)
					esnListValueObject.setCntr3(CommonUtility.deNull(rs.getString("CNTR_NBR")));
				if (cntrsqno == 4)
					esnListValueObject.setCntr4(CommonUtility.deNull(rs.getString("CNTR_NBR")));
				cntrDetails.add(esnListValueObject);
			} // while
			log.info("END: *** getCntrDetails Result *****" + cntrDetails.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getCntrDetails : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCntrDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCntrDetails  DAO  END");
		}

		return cntrDetails;
	}

	@Override
	public String getSchemeInd(String out_voyno) throws BusinessException {
		String sql = "";
		String msch = "";

		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		sql = "SELECT MIXED_SCHEME_IND FROM VESSEL_CALL WHERE VV_CD=:out_voyno ";

		try {
			log.info("START: getSchemeInd  DAO  Start Obj " + " out_voyno:" + out_voyno);

			log.info(" *** getSchemeInd SQL *****" + sql);

			paramMap.put("out_voyno", out_voyno);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				msch = rs.getString(1);
			}
			// log.info("sql sch ind "+sql);
			// log.info("msch "+msch);

			log.info("END: *** getSchemeInd Result *****" + msch.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getSchemeInd : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSchemeInd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSchemeInd  DAO  END");
		}

		return msch;
	}

	@Override
	public String getScheme(String out_voyno) throws BusinessException {
		String sql = "";
		String msch = "";

		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		sql = "SELECT AB_CD FROM VESSEL_CALL VC, VESSEL_SCHEME VS WHERE VC.SCHEME=VS.SCHEME_CD AND VC.VV_CD=:out_voyno ";

		try {
			log.info("START: getScheme  DAO  Start Obj " + " out_voyno:" + out_voyno);

			log.info(" *** getScheme SQL *****" + sql);

			paramMap.put("out_voyno", out_voyno);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				msch = rs.getString(1);
			}

			log.info("END: *** getScheme Result *****" + msch.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getScheme : ", ne);
			return null;
		} catch (Exception e) {
			log.info("Exception getScheme : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getScheme  DAO  END");
		}

		return msch;
	}

	@Override
	public String getBPacctnbr(String esno, String voy_nbr) throws BusinessException {
		String sql = "";
		String acctnbr = "";
		String scheme = "";

		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		sql = "SELECT MIXED_SCHEME_ACCT_NBR FROM tesn_psa_jp WHERE ESN_ASN_NBR=:esno ";

		try {
			log.info("START: getBPacctnbr  DAO  Start Obj " + " esno:" + esno);

			log.info(" *** getBPacctnbr SQL *****" + sql);

			paramMap.put("esno", esno);
			log.info(param + paramMap);
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
				} else if (!scheme.equals("JLR") && !scheme.equals("JNL") && !scheme.equals("JBT")) {
					acctnbr = getABactnbr(voy_nbr);
				}
			}
			log.info("END: *** getBPacctnbr Result *****" + CommonUtil.deNull(acctnbr).toString());
		} catch (NullPointerException ne) {
			log.info("Exception getBPacctnbr : ", ne);
			return null;
		} catch (BusinessException e) {
			log.info("Exception getBPacctnbr : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getBPacctnbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBPacctnbr  DAO  END");
		}
		return acctnbr;
	}

	@Override
	public String getABactnbr(String voy_nbr) throws BusinessException {
		String sql = "";
		String bactnbr = "";

		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		sql = "SELECT VS.ACCT_NBR FROM VESSEL_CALL VC,VESSEL_SCHEME VS WHERE VC.SCHEME=VS.SCHEME_CD AND VC.VV_CD =:voy_nbr ";

		try {
			log.info("START: getABactnbr  DAO  Start Obj " + " voy_nbr:" + voy_nbr);

			log.info(" *** getABactnbr SQL *****" + sql);

			paramMap.put("voy_nbr", voy_nbr);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				bactnbr = rs.getString(1);
			}

			log.info("END: *** getABactnbr Result *****");
		} catch (NullPointerException ne) {
			log.info("Exception getABactnbr : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getABactnbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getABactnbr  DAO  END");
		}

		return bactnbr;

	}

	@Override
	public String getVCactnbr(String voy_nbr) throws BusinessException {
		String sql = "";
		String bactnbr = "";

		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		sql = "SELECT BILL_ACCT_NBR FROM VESSEL_CALL WHERE VV_CD=:voy_nbr";

		try {
			log.info("START: getVCactnbr  DAO  Start Obj " + " voy_nbr:" + voy_nbr);

			log.info(" *** getVCactnbr SQL *****" + sql);

			paramMap.put("voy_nbr", voy_nbr);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				bactnbr = rs.getString(1);
			}

			log.info("END: *** getVCactnbr Result *****" + bactnbr.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getVCactnbr : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVCactnbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVCactnbr  DAO  END");
		}

		return bactnbr;

	}

	@Override
	public String getSchemeName(String voy_nbr) throws BusinessException {
		String sql = "";
		String sch = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		sql = "SELECT SCHEME FROM VESSEL_CALL WHERE VV_CD=:voy_nbr ";

		try {
			log.info("START: getSchemeName  DAO  Start Obj " + " voy_nbr:" + voy_nbr);

			log.info(" *** getSchemeName SQL *****" + sql);

			paramMap.put("voy_nbr", voy_nbr);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				sch = rs.getString(1);
			}

			log.info("END: *** getSchemeName Result *****" + sch.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getSchemeName : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSchemeName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSchemeName  DAO  END");
		}

		return sch;

	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getABacctnoForSA(String out_voyno) throws BusinessException {
		List<TesnPsaJpEsnListValueObject> vacctno = new ArrayList<TesnPsaJpEsnListValueObject>();
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		StringBuffer tempSql = new StringBuffer();

		try {
			log.info("START: getABacctnoForSA  DAO  Start Obj " + " out_voyno:" + out_voyno);

			tempSql.append("SELECT VESSEL_SCHEME.SCHEME_CD, VESSEL_SCHEME.ACCT_NBR ");
			tempSql.append("FROM VESSEL_SCHEME , NOMINATED_SCHEME ");
			// tempSql.append("WHERE VESSEL_SCHEME.AB_CD IS NOT NULL ");
			// tempSql.append("AND VESSEL_SCHEME.scheme_cd = NOMINATED_SCHEME.scheme_cd ");
			tempSql.append("WHERE VESSEL_SCHEME.scheme_cd = NOMINATED_SCHEME.scheme_cd ");
			tempSql.append("AND NOMINATED_SCHEME.nominate_status = 'APP' ");
			tempSql.append("AND VESSEL_SCHEME.AB_CD IS NOT NULL ");
			tempSql.append("AND NOMINATED_SCHEME.vv_cd =:out_voyno ");
			String sql = tempSql.toString();

			log.info(" *** getABacctnoForSA SQL *****" + sql);

			paramMap.put("out_voyno", out_voyno);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			TesnPsaJpEsnListValueObject esnListValueObject = null;
			while (rs.next()) {
				esnListValueObject = new TesnPsaJpEsnListValueObject();
				esnListValueObject.setCc_cd("" + rs.getString(1));
				esnListValueObject.setCc_name("" + rs.getString(2));
				vacctno.add(esnListValueObject);
			}

			log.info("END: *** getABacctnoForSA Result *****" + vacctno.toString());
			return vacctno;
		} catch (NullPointerException ne) {
			log.info("Exception getABacctnoForSA : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getABacctnoForSA : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getABacctnoForSA  DAO  END");
		}

	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getABacctno(String out_voyno) throws BusinessException {
		String sql = "";
		List<TesnPsaJpEsnListValueObject> vacctno = new ArrayList<TesnPsaJpEsnListValueObject>();

		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		/*
		 * sql= "SELECT A.ACCT_NBR FROM CUST_ACCT A, VESSEL_CALL V ,VESSEL_SCHEME VS"
		 * +" WHERE VS.SCHEME_CD=V.SCHEME AND VS.AB_CD = A.CUST_CD AND A.BUSINESS_TYPE LIKE '%G%' AND "
		 * +" A.ACCT_NBR IS NOT NULL AND A.ACCT_STATUS_CD='A' AND A.TRIAL_IND='N' AND"
		 * +" V.OUT_VOY_NBR = '"+out_voyno+"' ORDER BY A.ACCT_NBR";
		 */
		sql = "SELECT SCHEME_CD,ACCT_NBR FROM VESSEL_SCHEME WHERE AB_CD IS NOT NULL";

		try {
			log.info("START: getABacctno  DAO  Start Obj " + " out_voyno:" + out_voyno);

			log.info(" *** getABacctno SQL *****" + sql);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			TesnPsaJpEsnListValueObject esnListValueObject = null;
			while (rs.next()) {

				esnListValueObject = new TesnPsaJpEsnListValueObject();
				esnListValueObject.setCc_cd("" + rs.getString(1));
				esnListValueObject.setCc_name("" + rs.getString(2));
				vacctno.add(esnListValueObject);
			}

			log.info("END: *** getABacctno Result *****" + vacctno.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getABacctno : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getABacctno : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getABacctno  DAO  END");
		}

		return vacctno;

	}

	@Override
	public List<String> getSAacctno(String vv_cd) throws BusinessException {
		String sql = "";
		List<String> vacctno = new ArrayList<String>();

		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		/*
		 * sql= "SELECT A.ACCT_NBR FROM CUST_ACCT A, VESSEL_CALL V "
		 * +" WHERE A.BUSINESS_TYPE LIKE '%G%' AND"
		 * +" A.ACCT_NBR IS NOT NULL AND A.ACCT_STATUS_CD='A' AND A.TRIAL_IND='N' AND"
		 * +" V.CREATE_CUST_CD = A.CUST_CD AND V.vv_cd = '"+out_voyno+"'"
		 * +" ORDER BY A.ACCT_NBR";
		 */
		sql = "SELECT BILL_ACCT_NBR FROM VESSEL_CALL WHERE VV_CD =:vv_cd ";

		try {

			log.info("START: getSAacctno  DAO  Start Obj " + " vv_cd:" + vv_cd);

			log.info(" *** getSAacctno SQL *****" + sql);

			paramMap.put("vv_cd", vv_cd);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				vacctno.add("" + rs.getString(1));
			}

			log.info("END: *** getSAacctno Result *****" + vacctno.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getSAacctno : ", ne);
			throw new BusinessException("M4201");

		} catch (Exception e) {
			log.info("Exception getSAacctno : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSAacctno  DAO  END");
		}

		return vacctno;
	}

	@Override
	public VesselVoyValueObject getVessel(String vesselName, String outvoyNbr, String coCd) throws BusinessException {
		String custCd = coCd;
		String sql = "";

		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		List<VesselVoyValueObject> vesselList = new ArrayList<VesselVoyValueObject>();
		VesselVoyValueObject vesselVoyValueObject = null;
		try {
			log.info("START: getVessel  DAO  Start Obj " + " vesselName:" + vesselName + " outvoyNbr:" + outvoyNbr
					+ " coCd:" + coCd);

			if (custCd.equals("JP")) {
				sb.append(
						"select distinct VV_CD,VSL_NM,OUT_VOY_NBR,TERMINAL from esn e,TESN_PSA_JP te,vessel_call ves  where");
				sb.append(" VSL_NM = :vesselName AND OUT_VOY_NBR = :outvoyNbr ");
				sb.append(
						" AND e.trans_type='C' and e.esn_status = 'A' and e.out_voy_var_nbr = ves.vv_cd AND te.ESN_ASN_NBR = e.ESN_ASN_NBR ORDER BY TERMINAL DESC,VSL_NM,OUT_VOY_NBR");

				sql = sb.toString();
			} else {
				sb.append("select distinct VV_CD,VSL_NM,OUT_VOY_NBR,TERMINAL ");
				sb.append("from esn e,TESN_PSA_JP te,vessel_call ves  where");
				sb.append(" VSL_NM = :vesselName AND OUT_VOY_NBR = :outvoyNbr ");
				sb.append(" AND e.trans_type='C'");
				sb.append(" and ( e.ESN_CREATE_CD=:custCd OR ves.CREATE_CUST_CD = :custCd)");
				sb.append(
						"and e.esn_status = 'A' and e.out_voy_var_nbr = ves.vv_cd AND te.ESN_ASN_NBR = e.ESN_ASN_NBR ORDER BY TERMINAL DESC,VSL_NM,OUT_VOY_NBR");

				sql = sb.toString();
			}

			log.info(" *** getVessel SQL *****" + sql);

			if (custCd.equals("JP")) {
				paramMap.put("vesselName", vesselName);
				paramMap.put("outvoyNbr", outvoyNbr);
			} else {
				paramMap.put("vesselName", vesselName);
				paramMap.put("outvoyNbr", outvoyNbr);
				paramMap.put("custCd", custCd);
			}
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				vesselVoyValueObject = new VesselVoyValueObject();
				vesselVoyValueObject.setVoyNo(CommonUtility.deNull(rs.getString("OUT_VOY_NBR")));
				vesselVoyValueObject.setVslName(CommonUtility.deNull(rs.getString("VSL_NM")));
				vesselVoyValueObject.setVarNbr(CommonUtility.deNull(rs.getString("VV_CD")));
				// koktsing 20180803
				// Fix the wrong code from setVarNbr to setTerminal
				vesselVoyValueObject.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				vesselList.add(vesselVoyValueObject);
				// log.info("Db Value :"+rs.getString("esn_nbr"));
			}
			log.info("END: *** getVessel Result *****" + vesselVoyValueObject.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getVessel : ", ne);
		} catch (Exception e) {
			log.info("Exception getVessel : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVessel  DAO  END");
		}

		return vesselVoyValueObject;
	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getEsnDetails(String esnNbr, String custId) throws BusinessException {
		String esnNo = esnNbr;
		String pkgsDesc = getPkgsType(esnNo);
		String markings = getMarkings(esnNo);
		String bookingRefNbr = "";
		String payMode = "";
		String billableParty = "";
		String accNo = "";
		String portDis = "";
		String portDisDesc = "";
		String clsShpInd = "";
		String sql = "";

		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();

		List<TesnPsaJpEsnListValueObject> esnList = new ArrayList<TesnPsaJpEsnListValueObject>();
		TesnPsaJpEsnListValueObject esnListValueObject = new TesnPsaJpEsnListValueObject();
		try {
			log.info("START: getEsnDetails  DAO  Start Obj " + " esnNo:" + esnNo);

			sb.append(
					"select v.terminal, e.bk_ref_nbr as BKRNBR,bk.shipper_nm as shipper_nm,PKG_TYPE,es.CRG_DES as CRGDESC,HS_CD,es.NBR_PKGS as NBR_PKGS,GROSS_WT,GROSS_VOL,DG_IND,OPS_IND,STORAGE_IND,");
			sb.append(
					"DIS_PORT,STORAGE_DAYS,UA_NBR_PKGS,VARIANCE_PKGS,VARIANCE_VOL,VARIANCE_WT,BK_VOL,BK_WT,BK_NBR_PKGS,FIRST_CAR_VOY_NBR, FIRST_CAR_VES_NM,PAYMENT_MODE,ACCT_NBR,e.stuff_ind,e.CARGO_CATEGORY_CD,code.cc_name, es.hs_sub_code_fr, es.hs_sub_code_to, HS.hs_sub_desc, e.EPC_IND ,v.vv_cd, v.vsl_nm,v.out_voy_nbr, es.CUSTOM_HS_CODE From bk_details bk, esn e, tesn_psa_jp es,vessel_call v,CARGO_CATEGORY_CODE code, hs_sub_code HS where bk.bk_ref_nbr = e.bk_ref_nbr ");
			sb.append(
					" and v.vv_cd = e.out_voy_Var_nbr and  e.esn_asn_nbr = es.esn_asn_nbr and code.cc_cd = e.CARGO_CATEGORY_CD and HS.HS_CODE(+)= es.HS_CD AND HS.HS_SUB_CODE_FR(+)= es.HS_SUB_CODE_FR AND HS.HS_SUB_CODE_TO(+)= es.HS_SUB_CODE_TO and es.esn_asn_nbr = :esnNo");
			sql = sb.toString();

			log.info(" *** getEsnDetails SQL *****" + sql);

			paramMap.put("esnNo", esnNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				esnListValueObject.setBookingRefNo(CommonUtility.deNull(rs.getString("BKRNBR")));
				bookingRefNbr = esnListValueObject.getBookingRefNo();
				clsShpInd = getClsShpInd(bookingRefNbr);
				esnListValueObject.setShipperName(CommonUtility.deNull(rs.getString("shipper_nm")));
				esnListValueObject.setPkgType(CommonUtility.deNull(rs.getString("PKG_TYPE")));
				esnListValueObject.setCrgDesc(CommonUtility.deNull(rs.getString("CRGDESC")));
				esnListValueObject.setHsCode(CommonUtility.deNull(rs.getString("HS_CD")));
				esnListValueObject.setNoOfCntr(rs.getInt("NBR_PKGS"));
				esnListValueObject.setVarNoofPakgs(rs.getDouble("VARIANCE_PKGS"));
				esnListValueObject.setVarGrVolume(rs.getDouble("VARIANCE_VOL"));
				esnListValueObject.setVarGrWt(rs.getDouble("VARIANCE_WT"));
				esnListValueObject.setBGrVolume(rs.getDouble("BK_VOL"));
				esnListValueObject.setBGrWt(rs.getDouble("BK_WT"));
				esnListValueObject.setBNoofPkgs(rs.getInt("BK_NBR_PKGS"));
				esnListValueObject.setGrWt(rs.getDouble("GROSS_WT"));
				esnListValueObject.setGrVolume(rs.getDouble("GROSS_VOL"));
				esnListValueObject.setDgInd(CommonUtility.deNull(rs.getString("DG_IND")));
				esnListValueObject.setOpInd(CommonUtility.deNull(rs.getString("OPS_IND")));
				esnListValueObject.setStgInd(CommonUtility.deNull(rs.getString("STORAGE_IND")));
				esnListValueObject.setNoOfdays(rs.getInt("STORAGE_DAYS"));
				esnListValueObject.setUaNoofPkgs(rs.getInt("UA_NBR_PKGS"));
				payMode = CommonUtility.deNull(rs.getString("PAYMENT_MODE"));
				accNo = CommonUtility.deNull(rs.getString("ACCT_NBR"));
				if (payMode.equals("A")) {
					billableParty = getBillablePartyName(accNo);
					esnListValueObject.setBillPartyName(billableParty);
				}
				esnListValueObject.setPkgDesc(pkgsDesc);
				esnListValueObject.setCrgMarking(markings);
				portDis = CommonUtility.deNull(rs.getString("DIS_PORT"));
				portDisDesc = getPortDisDesc(portDis);
				esnListValueObject.setPayMode(payMode);
				esnListValueObject.setAccNo(accNo);
				esnListValueObject.setPortDesc(portDisDesc);
				esnListValueObject.setPortD(portDis);
				esnListValueObject.setClsShpInd(clsShpInd);
				esnListValueObject.setInvoyageNo(CommonUtility.deNull(rs.getString("FIRST_CAR_VOY_NBR")));
				esnListValueObject.setFirstCName(CommonUtility.deNull(rs.getString("FIRST_CAR_VES_NM")));
				esnListValueObject.setStuffingIndicator(CommonUtility.deNull(rs.getString("stuff_ind")));
				esnListValueObject.setCategory(rs.getString("CARGO_CATEGORY_CD"));
				esnListValueObject.setCategoryValue(rs.getString("CC_NAME"));

				esnListValueObject.setHsSubCodeFr(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				esnListValueObject.setHsSubCodeTo(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));
				esnListValueObject.setHsSubCodeDesc(CommonUtility.deNull(rs.getString("HS_SUB_DESC")));

				esnListValueObject.setDeliveryToEPC(CommonUtility.deNull(rs.getString("EPC_IND"))); // MCC for EPC IND
				esnListValueObject.setLoadVsl(CommonUtility.deNull(rs.getString("VSL_NM")));
				esnListValueObject.setLoadOutVoy(CommonUtility.deNull(rs.getString("OUT_VOY_NBR")));

				esnListValueObject.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				esnListValueObject.setCustomHsCode(CommonUtility.deNull(rs.getString("CUSTOM_HS_CODE"))); // CR FTZ HSCODE - NS JULY 2024
				esnList.add(esnListValueObject);
			}
			log.info("END: *** getEsnDetails Result *****" + esnList.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getEsnDetails : ", ne);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception getEsnDetails : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getEsnDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEsnDetails  DAO  END");
		}

		return esnList;
	}

	private String getPortDisDesc(String port_Dis) throws BusinessException {
		String portDis = port_Dis;
		String portDisDesc = "";
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		sql = "select port_nm from un_port_code where port_cd =:portDis ";

		try {
			log.info("START: getPortDisDesc  DAO  Start Obj " + " port_Dis:" + port_Dis);

			log.info(" *** getPortDisDesc SQL *****" + sql);

			paramMap.put("portDis", portDis);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				portDisDesc = CommonUtility.deNull(rs.getString("port_nm"));
			}
			log.info("END: *** getPortDisDesc Result *****" + portDisDesc.toString());

		} catch (NullPointerException ne) {
			log.info("Exception getPortDisDesc : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getPortDisDesc : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPortDisDesc  DAO  END");
		}

		return portDisDesc;
	}

	@Override
	public String getBillablePartyName(String accNbr) throws BusinessException {
		String accNo = accNbr;
		String sql = "";
		String billablePartyName = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		sql = "select co.co_nm from cust_acct ca, company_code co where co.co_cd = ca.cust_cd and ca.ACCT_NBR = :accNo ";

		try {
			log.info("START: getBillablePartyName  DAO  Start Obj " + " accNbr:" + accNbr);

			log.info(" *** getBillablePartyName SQL *****" + sql);

			paramMap.put("accNo", GbmsCommonUtility.addApostr(accNo));
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				billablePartyName = CommonUtility.deNull(rs.getString("co_nm"));
			}
			log.info("END: *** getBillablePartyName Result *****" + billablePartyName.toString());

		} catch (NullPointerException ne) {
			log.info("Exception getBillablePartyName : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBillablePartyName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBillablePartyName  DAO  END");
		}

		return billablePartyName;
	}

	private String getClsShpInd(String bookref) throws BusinessException {
		String bookinRefNo = bookref;
		String clsShpInd = "";
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		
		sql = "select vessel_call.gb_close_shp_ind from vessel_call,bk_details where vessel_call.vv_cd = bk_details.var_nbr and bk_ref_nbr = :bookinRefNo ";

		try {
			log.info("START: getClsShpInd  DAO  Start Obj " + " bookref:" + bookref);

			log.info(" *** getClsShpInd SQL *****" + sql);

			paramMap.put("bookinRefNo", bookinRefNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				clsShpInd = CommonUtility.deNull(rs.getString("gb_close_shp_ind"));
			}
			log.info("END: *** getClsShpInd Result *****" + clsShpInd.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getClsShpInd : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getClsShpInd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getClsShpInd  DAO  END");
		}

		return clsShpInd;
	}

	private String getMarkings(String esnNbr) throws BusinessException {
		String esnNo = esnNbr;
		String sql = "";
		String markings = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		
		sql = "select * from ESN_MARKINGS where ESN_ASN_NBR = :esnNo ";

		try {
			log.info("START: getMarkings  DAO  Start Obj ");

			log.info(" *** getMarkings SQL *****" + sql);

			paramMap.put("esnNo", esnNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				markings = CommonUtility.deNull(rs.getString("MARKINGS"));
			}
			log.info("END: *** getMarkings Result *****" + markings.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getMarkings : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getMarkings : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getMarkings  DAO  END");
		}

		return markings;
	}

	private String getPkgsType(String esnNbr) throws BusinessException {
		String esnNo = esnNbr;
		String sql = "";
		String pkgsDesc = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		try {
			log.info("START: getPkgsType  DAO  Start Obj " + " esnNbr:" + esnNbr);

			sql = "select PKG_TYPE_CD,PKG_DESC from PKG_TYPES,tesn_psa_jp where PKG_TYPES.PKG_TYPE_CD = tesn_psa_jp.PKG_TYPE and ESN_ASN_NBR =:esnNo ";

			log.info(" *** getPkgsType SQL *****" + sql);

			paramMap.put("esnNo", esnNo);
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				pkgsDesc = CommonUtility.deNull(rs.getString("PKG_DESC"));
			}
			log.info("END: *** getPkgsType Result *****" + pkgsDesc.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getPkgsType : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getPkgsType : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPkgsType  DAO  END");
		}

		return pkgsDesc;
	}

	@Override
	public TableResult getEsnList(String selectVoyNo, String custId, Criteria criteria) throws BusinessException {
		String selVoyNo = selectVoyNo;
		String custCd = custId;
		String sql = "";
		SqlRowSet rs = null;

		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		TableResult tableResult = new TableResult();
		Map<String, String> paramMap = new HashMap<>();
		StringBuffer sb = new StringBuffer();
		// VietNguyen (FPT) Documentation Processing Enhancement 03-Jan-2014: START

		// VietNguyen (FPT) Documentation Processing Enhancement 03-Jan-2014: END
		TesnPsaJpEsnListValueObject esnListValueObject = null;
		try {
			log.info("START: getEsnList  DAO  Start Obj " + " selectVoyNo:" + selectVoyNo + " custId:" + custId);

			if (custCd.equals("JP")) {

				/*
				 * sql =
				 * "select tesn.esn_asn_nbr,tesn.FIRST_CAR_VOY_NBR,tesn.FIRST_CAR_VES_NM,CRG_TYPE_NM,tesn.NBR_PKGS,tesn.GROSS_WT,tesn.GROSS_VOL,esn.stuff_ind, tesn.HS_CD, tesn.HS_SUB_CODE_FR, tesn.HS_SUB_CODE_TO, hs.HS_SUB_DESC from esn esn,bk_details bkd, "
				 * +
				 * "TESN_PSA_JP tesn, CRG_TYPE, HS_SUB_CODE hs where tesn.esn_asn_nbr = esn.esn_asn_nbr and esn_Status = 'A' "
				 * +
				 * "and bkd.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD and bkd.bk_ref_nbr = esn.BK_REF_NBR and tesn.esn_asn_nbr = esn.esn_asn_nbr "
				 * +
				 * "and HS.HS_CODE(+)= tesn.HS_CD AND HS.HS_SUB_CODE_FR(+)= tesn.HS_SUB_CODE_FR AND HS.HS_SUB_CODE_TO(+)= tesn.HS_SUB_CODE_TO "
				 * + "and esn.OUT_VOY_VAR_NBR='" + selVoyNo + "' ORDER BY tesn.esn_asn_nbr";
				 */

				sb.append(
						"select vesm.TERMINAL, vesm.COMBI_GC_OPS_IND,vesm.COMBI_GC_SCHEME, nvl(vsh.scheme_cd, vesm.scheme) scheme_cd, tesn.esn_asn_nbr, tesn.FIRST_CAR_VOY_NBR, tesn.FIRST_CAR_VES_NM, CRG_TYPE_NM,tesn.NBR_PKGS, vesm.VSL_NM, ");
				sb.append(
						"vesm.in_voy_nbr, vesm.out_voy_nbr, tesn.TRUCKER_NM, to_char(esn.LAST_MODIFY_DTTM,'dd/mm/yyyy hh24:mi') as LAST_MODIFY_DTTM, ad.user_name ESN_CREATE_CD, esn.EPC_IND from esn esn, bk_details bkd, ");
				sb.append(
						" adm_user ad, (select esn_asn_nbr, min(last_modify_user_id) last_modify_user_id,trans_nbr from TESN_PSA_JP_TRANS group by esn_asn_nbr,trans_nbr having trans_nbr=0 )  tmp, ");
				sb.append(
						"TESN_PSA_JP tesn, CRG_TYPE, vessel_call vesm, vessel_scheme vsh where tesn.esn_asn_nbr = esn.esn_asn_nbr and esn_Status = 'A' ");
				sb.append(
						"and bkd.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD and bkd.bk_ref_nbr = esn.BK_REF_NBR and tesn.esn_asn_nbr = esn.esn_asn_nbr ");
				sb.append("and esn.OUT_VOY_VAR_NBR = vesm.VV_CD and ");
				sb.append(" ad.user_acct(+) = tmp.last_modify_user_id  and tmp.esn_asn_nbr (+) = tesn.esn_asn_nbr");
				sb.append(" and tesn.mixed_scheme_acct_nbr = vsh.acct_nbr(+) and vsh.rec_status(+) = 'A'  ");
				sb.append("and esn.OUT_VOY_VAR_NBR=:selVoyNo ORDER BY tesn.esn_asn_nbr");
				sql = sb.toString();
			} else {
				/*
				 * sql =
				 * "select tesn.esn_asn_nbr,tesn.FIRST_CAR_VOY_NBR,tesn.FIRST_CAR_VES_NM,CRG_TYPE_NM,tesn.NBR_PKGS,tesn.GROSS_WT,tesn.GROSS_VOL,esn.stuff_ind,  tesn.HS_CD, tesn.HS_SUB_CODE_FR, tesn.HS_SUB_CODE_TO, hs.HS_SUB_DESC"
				 * +
				 * "from esn esn,bk_details bkd, TESN_PSA_JP tesn, CRG_TYPE,vessel_call vesm, HS_SUB_CODE hs "
				 * + "where tesn.esn_asn_nbr = esn.esn_asn_nbr and esn_Status = 'A' " +
				 * "and bkd.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD and bkd.bk_ref_nbr = esn.BK_REF_NBR and tesn.esn_asn_nbr = esn.esn_asn_nbr "
				 * + "and esn.OUT_VOY_VAR_NBR='" + selVoyNo + "' " +
				 * "and esn.out_voy_var_nbr = ves.vv_cd " +
				 * "and HS.HS_CODE(+)= tesn.HS_CD AND HS.HS_SUB_CODE_FR(+)= tesn.HS_SUB_CODE_FR AND HS.HS_SUB_CODE_TO(+)= tesn.HS_SUB_CODE_TO "
				 * + " and ( esn.ESN_CREATE_CD='" + custCd + "' OR ves.CREATE_CUST_CD = '" +
				 * custCd + "')" + " ORDER BY tesn.esn_asn_nbr";
				 */

				sb.append(
						"select vesm.TERMINAL, vesm.COMBI_GC_OPS_IND,vesm.COMBI_GC_SCHEME,nvl(vsh.scheme_cd, vesm.scheme) scheme_cd, tesn.esn_asn_nbr, tesn.FIRST_CAR_VOY_NBR, tesn.FIRST_CAR_VES_NM, CRG_TYPE_NM,tesn.NBR_PKGS, vesm.VSL_NM,");
				sb.append(
						"vesm.in_voy_nbr, vesm.out_voy_nbr, tesn.TRUCKER_NM, to_char(esn.LAST_MODIFY_DTTM,'dd/mm/yyyy hh24:mi') as LAST_MODIFY_DTTM, ad.user_name ESN_CREATE_CD, esn.EPC_IND ");
				sb.append(
						"from esn esn,bk_details bkd, TESN_PSA_JP tesn, CRG_TYPE,vessel_call vesm, vessel_scheme vsh, ");
				sb.append(
						" adm_user ad, (select esn_asn_nbr, min(last_modify_user_id) last_modify_user_id,trans_nbr from TESN_PSA_JP_TRANS group by esn_asn_nbr,trans_nbr having trans_nbr=0 )  tmp ");
				sb.append("where tesn.esn_asn_nbr = esn.esn_asn_nbr and esn_Status = 'A' ");
				sb.append(
						"and bkd.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD and bkd.bk_ref_nbr = esn.BK_REF_NBR and tesn.esn_asn_nbr = esn.esn_asn_nbr  ");
				sb.append("and esn.out_voy_var_nbr = vesm.vv_cd and ");
				sb.append("tesn.mixed_scheme_acct_nbr = vsh.acct_nbr(+) and vsh.rec_status(+) = 'A' and ");
				sb.append(" ad.user_acct(+) = tmp.last_modify_user_id  and tmp.esn_asn_nbr (+) = tesn.esn_asn_nbr");
				sb.append(" and esn.OUT_VOY_VAR_NBR=:selVoyNo ");
				sb.append(" and ( esn.ESN_CREATE_CD=:custCd OR vesm.CREATE_CUST_CD = :custCd) ");
				sb.append("ORDER BY tesn.esn_asn_nbr");

				sql = sb.toString();
			}
			if (custCd.equals("JP")) {
				paramMap.put("selVoyNo", selVoyNo);
			} else {
				paramMap.put("custCd", custCd);
				paramMap.put("selVoyNo", selVoyNo);
			}

			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						paramMap, Integer.class));
			}

			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());

			}
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			log.info(" *** getEsnList SQL *****" + sql);

			while (rs.next()) {
				esnListValueObject = new TesnPsaJpEsnListValueObject();
				esnListValueObject.setEsnNbr(rs.getLong("ESN_ASN_NBR"));
				esnListValueObject.setInvoyageNo(CommonUtility.deNull(rs.getString("FIRST_CAR_VOY_NBR")));
				esnListValueObject.setFirstCName(CommonUtility.deNull(rs.getString("FIRST_CAR_VES_NM")));
				// VietNguyen (FPT) Documentation Processing Enhancement 03-Jan-2014: START

				// esnListValueObject.setBookingRefNo(CommonUtility.deNull(rs.getString("BK_REF_NBR")));
				// esnListValueObject.setShipperName(CommonUtility.deNull(rs.getString("shipper_nm")));
				esnListValueObject.setCrgDesc(CommonUtility.deNull(rs.getString("CRG_TYPE_NM")));
				esnListValueObject.setBNoofPkgs(rs.getInt("NBR_PKGS"));
				// esnListValueObject.setGrWt(rs.getDouble("GROSS_WT"));
				// esnListValueObject.setGrVolume(rs.getDouble("GROSS_VOL"));
				// esnListValueObject.setStuffingIndicator(
				// CommonUtility.deNull(rs.getString("stuff_ind")));

				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
				// esnListValueObject.setHsCode(CommonUtility.deNull(rs.getString("HS_CD")));
				// esnListValueObject.setHsSubCodeFr(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				// esnListValueObject.setHsSubCodeTo(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));
				// esnListValueObject.setHsSubCodeDesc(CommonUtility.deNull(rs.getString("HS_SUB_DESC")));
				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

				esnListValueObject.setLoadVsl(CommonUtility.deNull(rs.getString("VSL_NM")));
				esnListValueObject.setLoadInVoy(CommonUtility.deNull(rs.getString("in_voy_nbr")));
				esnListValueObject.setLoadOutVoy(CommonUtility.deNull(rs.getString("out_voy_nbr")));
				esnListValueObject.setTruckerNm(CommonUtility.deNull(rs.getString("TRUCKER_NM")));
				esnListValueObject.setLastModifyDttm(CommonUtility.deNull(rs.getString("LAST_MODIFY_DTTM")));
				esnListValueObject.setCreatedBy(CommonUtility.deNull(rs.getString("ESN_CREATE_CD")));
				esnListValueObject.setScheme(CommonUtility.deNull(rs.getString("SCHEME_CD")));
				// VietNguyen (FPT) Documentation Processing Enhancement 03-Jan-2014: END

				esnListValueObject.setDeliveryToEPC(CommonUtility.deNull(rs.getString("EPC_IND"))); // MCC for EPC IND
				esnListValueObject.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				esnListValueObject.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				esnListValueObject.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));

				topsModel.put(esnListValueObject);
			}
			log.info("END: *** getEsnList Result *****" + topsModel.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getEsnList : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEsnList : ", e);
			throw new BusinessException("M4201");
		} finally {
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
			log.info("END: getEsnList  DAO  END");
		}

		return tableResult;
	}

	@Override
	public List<VesselVoyValueObject> getVesselList(String custId) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		String custCd = custId;
		String sql = "";
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		List<VesselVoyValueObject> vesselList = new ArrayList<VesselVoyValueObject>();
		VesselVoyValueObject vesselVoyValueObject = null;
		try {
			log.info("START: getVesselList  DAO  Start Obj " + " custId:" + custId);

			if (custCd.equals("JP")) {
				sb.append("select distinct VV_CD,VSL_NM,OUT_VOY_NBR,TERMINAL ");
				sb.append("from esn e,TESN_PSA_JP te,vessel_call ves  ");
				sb.append("where e.trans_type='C' and e.esn_status = 'A' ");
				sb.append("and e.out_voy_var_nbr = ves.vv_cd AND ves.GB_CLOSE_SHP_IND !='Y'");
				sb.append(" and te.ESN_ASN_NBR = e.ESN_ASN_NBR ORDER BY TERMINAL DESC,VSL_NM,OUT_VOY_NBR");

				sql = sb.toString();
			}

			else {
				sb.append("select distinct VV_CD,VSL_NM,OUT_VOY_NBR,TERMINAL ");
				sb.append(" from esn e,TESN_PSA_JP te,vessel_call ves  ");
				sb.append("where e.trans_type='C' ");
				sb.append(" and ( e.ESN_CREATE_CD = :custCd OR ves.CREATE_CUST_CD = :custCd)");
				sb.append(" and e.esn_status = 'A' and e.out_voy_var_nbr = ves.vv_cd ");
				sb.append("and ves.vv_status_ind != 'UB' ");
				sb.append(
						"AND ves.GB_CLOSE_SHP_IND !='Y' and te.ESN_ASN_NBR = e.ESN_ASN_NBR ORDER BY TERMINAL DESC,VSL_NM,OUT_VOY_NBR");
				sql = sb.toString();
			}

			log.info(" *** getVesselList SQL *****" + sql);

			if (!custCd.equals("JP")) {
				paramMap.put("custCd", custCd);
			}
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				vesselVoyValueObject = new VesselVoyValueObject();
				vesselVoyValueObject.setVoyNo(CommonUtility.deNull(rs.getString("OUT_VOY_NBR")));
				vesselVoyValueObject.setVslName(CommonUtility.deNull(rs.getString("VSL_NM")));
				vesselVoyValueObject.setVarNbr(CommonUtility.deNull(rs.getString("VV_CD")));
				vesselVoyValueObject.setTerminal(CommonUtility.deNull(rs.getString("Terminal")));
				vesselList.add(vesselVoyValueObject);
				// log.info("Db Value :"+rs.getString("esn_nbr"));
			}
			log.info("END: *** getVesselList Result *****" + vesselList.toString());
		} catch (NullPointerException ne) {
			log.info("Exception getVesselList : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVesselList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselList  DAO  END");
		}

		return vesselList;
	}
	
	// START CR FTZ HSCODE - NS JULY 2024
	@Override
	public List<HsCodeDetails> getHsCodeDetailList(String esnNo) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: getHsCodeDetailList  DAO esnNo:" + esnNo);
			sb.append(" SELECT TESN_HSCODE_SEQ_NBR HSCODE_SEQ_NBR, ESN_ASN_NBR, HS_CODE, CUSTOM_HS_CODE, HS_SUB_CODE_FR,");
			sb.append(" HS_SUB_CODE_TO, HS_SUB_CODE_DESC, NBR_PKGS, GROSS_WT, GROSS_VOL, CRG_DES, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM");
			sb.append(" FROM GBMS.TESN_PSA_JP_HSCODE_DETAILS WHERE ESN_ASN_NBR = :esnNo ");
			paramMap.put("esnNo", esnNo);
			log.info(" ***getHsCodeDetailList SQL *****" + sb.toString());
			log.info(" ***getHsCodeDetailList paramMap *****" + paramMap.toString());
			try {
				return namedParameterJdbcTemplate.query(sb.toString(), paramMap,
						new BeanPropertyRowMapper<HsCodeDetails>(HsCodeDetails.class));
			} catch (EmptyResultDataAccessException e) {
				return null;
			}
		} catch (NullPointerException e) {
			log.info("Exception loadHSSubCode : ", e);
		} catch (Exception e) {
			log.info("Exception loadHSSubCode : ", e);
		} finally {
			log.info("END: loadHSSubCode  DAO ");
		}
		return null;
	}
	// END CR FTZ HSCODE - NS JULY 2024
}
