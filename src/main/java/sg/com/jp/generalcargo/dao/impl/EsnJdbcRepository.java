package sg.com.jp.generalcargo.dao.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javassist.tools.rmi.RemoteException;
import sg.com.jp.generalcargo.dao.EsnRepository;
import sg.com.jp.generalcargo.dao.ProcessGBLogRepository;
import sg.com.jp.generalcargo.dao.TextParaRepository;
import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.TextParaVO;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.GbmsCommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository
public class EsnJdbcRepository implements EsnRepository {
	private static final Log log = LogFactory.getLog(EsnJdbcRepository.class);
	private static final String logStatusGlobal = "Y";
	final static String TEXT_PARA_GC_VIEW_ESN = "GC_V_ESN";
	private static final String TXN_CD = "EADM";
	private static final int MAX_TRY = 10;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private TextParaRepository textParaRepo;

	@Autowired
	@Lazy
	private ProcessGBLogRepository processGBLogRepo;

	@Override
	public void updateShutEdoQtyAfterCancel(String esnAsnNbr, int qty, String userid) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sql = new StringBuilder();

		try {
			log.info("START: updateShutEdoQtyAfterCancel  DAO esnAsnNbr:" + CommonUtility.deNull(esnAsnNbr) + "qty;" + CommonUtility.deNull(String.valueOf(qty)) + "userid:"
					+ CommonUtility.deNull(userid));
			sql.append(
					" UPDATE BK_DETAILS SET SHUTOUT_DELIVERY_REMARKS='Cancel ShutEDO',LAST_MODIFY_DTTM = SYSDATE, LAST_MODIFY_USER_ID = :userid");
			sql.append(" ,shutout_delivery_pkgs = shutout_delivery_pkgs - :qty");
			sql.append("  WHERE bk_ref_nbr = (select bk_Ref_nbr from esn where esn_asn_nbr =:esnAsnNbr) ");

			log.info("updateShutEdoQtyAfterCancel SQL" + sql.toString());
			paramMap.put("userid", userid);
			paramMap.put("esnAsnNbr", esnAsnNbr);
			paramMap.put("qty", qty);
			log.info("paramMap: " + paramMap);
			namedParameterJdbcTemplate.update(sql.toString(), paramMap);

		} catch (NullPointerException ne) {
			log.info("Exception updateShutEdoQtyAfterCancel : ", ne);
			throw new BusinessException("M1004");
		} catch (Exception e) {
			log.info("Exception updateShutEdoQtyAfterCancel : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateShutEdoQtyAfterCancel DAO");
		}
	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->chkBKNo();
	@Override
	public boolean chkBKNo(String bk_ref_nbr[]) throws BusinessException {
		String sql = "";
		boolean bkrefnbr = false;
		int count = 0;
		String bknbr = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {
			log.info("START: chkBKNo  DAO  Start Obj " + CommonUtility.deNull(String.valueOf(bk_ref_nbr)));

			for (int i = 0; i < bk_ref_nbr.length; i++) {
				bknbr = bk_ref_nbr[i];
				sql = "SELECT * FROM BK_DETAILS WHERE BK_STATUS = 'A' AND BK_REF_NBR=:bknbr";

				paramMap.put("bknbr", bknbr);
				log.info(" *** chkBKNo SQL *****" + sql);
				log.info(" *** chkBKNo paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
				if (rs.next()) {
					count = count + 1;
				}
			}

			if (count > 0)
				bkrefnbr = true;

		} catch (NullPointerException ne) {
			log.info("Exception chkBKNo : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkBKNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** chkBKNo  END bkrefnbr: " + bkrefnbr);
		}
		return bkrefnbr;
	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->getEsndtls();
	@Override
	public List<BookingReferenceValueObject> getBKDetails(String bknbr) throws BusinessException {
		String sql = "";
		List<BookingReferenceValueObject> bkvec = new ArrayList<BookingReferenceValueObject>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		try {
			log.info("START:  *** getBKDetails Dao Start : *** " + CommonUtility.deNull(bknbr));

			sql = "SELECT * FROM BK_DETAILS WHERE BK_REF_NBR=:bknbr";
			paramMap.put("bknbr", bknbr);
			log.info("getBKDetails SQL=" + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);


			BookingReferenceValueObject bkvo = new BookingReferenceValueObject();
			if (rs.next()) {
				bkvo.setBkStatus(rs.getString("BK_STATUS"));
				bkvo.setCargoType(rs.getString("CARGO_TYPE"));
				bkvo.setPackageNos(rs.getString("BK_NBR_PKGS"));
				bkvo.setWeight(rs.getString("BK_WT"));
				bkvo.setVolume(rs.getString("BK_VOL"));
				bkvo.setPackageVariance(rs.getString("VARIANCE_PKGS"));
				bkvo.setVolumeVariance(rs.getString("VARIANCE_VOL"));
				bkvo.setWeightVariance(rs.getString("VARIANCE_WT"));
				bkvo.setPortOfDischarge(rs.getString("PORT_DIS"));
				bkvo.setEsnDeclarantNo(rs.getString("DECLARANT_CD"));
				bkvo.setBkCreateCd(rs.getString("BK_CREATE_CD"));
				bkvo.setBkShipperCd(rs.getString("SHIPPER_CD"));
				bkvo.setShipperCrNo(rs.getString("SHIPPER_CR_NBR"));
				bkvo.setShipperContact(rs.getString("SHIPPER_CONTACT"));
				bkvo.setShipperAddress(rs.getString("SHIPPER_ADDR"));
				bkvo.setShipperName(rs.getString("SHIPPER_NM"));
				bkvo.setContainerType(rs.getString("CNTR_TYPE"));
				bkvo.setNoContainer(rs.getString("NBR_OF_CNTR"));
				bkvo.setContainerSize(rs.getString("CNTR_SIZE"));
				bkvec.add(bkvo);
			}

		} catch (NullPointerException ne) {
			log.info("Exception getBKDetails : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBKDetails : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: *** getBKDetails  END  bkvec.size: " + bkvec.size());
		}
		return bkvec;
	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->getEsndtls();
	@Override
	public List<EsnListValueObject> getEsndtls(String esnasnnbr) throws BusinessException {
		String sql = "";
		List<EsnListValueObject> esnvec = new ArrayList<EsnListValueObject>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		try {
			log.info("START:  *** getEsndtls Dao Start : *** " + CommonUtility.deNull(esnasnnbr));

			sql = "SELECT * FROM ESN WHERE ESN_ASN_NBR=:esnasnnbr";
			paramMap.put("esnasnnbr", esnasnnbr);
			log.info("getEsndtls SQL=" + sql);
			log.info("paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			
			EsnListValueObject esnvo = new EsnListValueObject();

			if (rs.next()) {
				esnvo.setTranstype(rs.getString("TRANS_TYPE"));
				esnvo.setInvoyvarnbr(CommonUtility.deNull(rs.getString("IN_VOY_VAR_NBR")));
				esnvo.setEsnstatus(rs.getString("ESN_STATUS"));
				esnvo.setDeclarantcd(rs.getString("ESN_CREATE_CD"));
				esnvo.setCrgcatcd(rs.getString("CARGO_CATEGORY_CD"));
				esnvo.setWhind(rs.getString("WH_IND"));
				esnvo.setFsdays(rs.getString("FREE_STG_DAYS"));
				esnvo.setWhrem(rs.getString("WH_REMARKS"));
				esnvo.setWhaggrnbr(rs.getString("WH_AGGR_NBR"));
				esnvo.setStfInd(rs.getString("STUFF_IND"));
				esnvo.setCntr1(rs.getString("cntr_seq_nbr"));
				esnvo.setDeliveryToEPC(rs.getString("EPC_IND"));
				esnvo.setMiscAppNo(rs.getString("MISC_APP_NBR"));
				esnvec.add(esnvo);
			}

		} catch (NullPointerException ne) {
			log.info("Exception getEsndtls : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEsndtls : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getEsndtls  END  esnvec.size: " + esnvec.size());
		}
		return esnvec;
	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->getEsnNoForDPE();
	@Override
	public String getEsnNoForDPE() throws BusinessException {
		String esnasnnbr = "";
		String sql3 = "";
		String sql3_1 = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();

		SqlRowSet rs3_1 = null;
		SqlRowSet rs3 = null;
		SqlRowSet rsasn = null;
		try {
			log.info("START: getEsnNoForDPE  DAO  Start Obj ");

			////// ----------------
			String stresnasnnbr = "";
			String strsqldate = "";
			// sql3 = "SELECT MAX(ESN_ASN_NBR) FROM ESN ";
			sql3_1 = "SELECT TO_CHAR(SYSDATE,'Y/MM/DD') AS STRDATE FROM DUAL";
			log.info("*** SQL sql3_1*****" + sql3_1);
			log.info(" paramMap: " + paramMap);
			rs3_1 = namedParameterJdbcTemplate.queryForRowSet(sql3_1.toString(), paramMap);

			while (rs3_1.next()) {
				strsqldate = CommonUtility.deNull(rs3_1.getString(1));
			}
			log.info(" strsqldate: " + strsqldate);
			String strsqlyy = strsqldate.substring(0, 1);
			String strsqlmm = strsqldate.substring(2, 4);

			log.info(" strsqlyy: " + strsqlyy);
			log.info(" strsqlmm: " + strsqlmm);
			if ((strsqlyy + strsqlmm.substring(0, 1)).equals("00")// Bhuvana 15/09/2010
					|| (strsqlyy + strsqlmm.substring(0, 1)).equals("01")) { // For year ends with 0. ie. 2010, 2020,
																				// etc.
				sql3 = "SELECT MAX(ESN_ASN_NBR) FROM ESN WHERE ESN_ASN_NBR < 1300000";

			} else {
				// sql= sql3 = "SELECT MAX(ESN_ASN_NBR) FROM ESN ";
				// eg. For 2011: Retrieve the max ESN No between ESN No 10000000 and 19999999.
				sql3 = "SELECT MAX(ESN_ASN_NBR) FROM ESN WHERE ESN_ASN_NBR BETWEEN (SELECT TO_NUMBER(TO_CHAR(SYSDATE,'Y')) * 10000000 FROM DUAL) ";
				sql3 = sql3 + " AND (SELECT ((TO_NUMBER(TO_CHAR(SYSDATE,'Y'))+1) * 10000000)-1 FROM DUAL)";
			}
			log.info("*** SQL sql3*****" + sql3);
			log.info(" paramMap:  " + paramMap);
			rs3 = namedParameterJdbcTemplate.queryForRowSet(sql3.toString(), paramMap);
			while (rs3.next()) {
				stresnasnnbr = CommonUtility.deNull(rs3.getString(1));
			}
			log.info(" stresnasnnbr:  " + stresnasnnbr);
			// generating next number
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
			log.info(" stresnasnnbr after concat substring 1:  " + stresnasnnbr);
			// int intesnasnnbr = Integer.parseInt(stresnasnnbr.substring(3, 8));
			String stresnasnnbryy = stresnasnnbr.substring(0, 1);
			String stresnasnnbrmm = stresnasnnbr.substring(1, 3);

			log.info(" stresnasnnbryy: " + stresnasnnbryy);
			log.info(" stresnasnnbrmm: " + stresnasnnbrmm);
			if ((stresnasnnbryy.equalsIgnoreCase(strsqlyy)) && (stresnasnnbrmm.equalsIgnoreCase(strsqlmm))) {
				stresnasnnbr = (stresnasnnbryy).concat(stresnasnnbrmm);
				log.info(" stresnasnnbr after concat substring 2:  " + stresnasnnbr);
				// Added by Babatunde on Jan., 2014 : START
				boolean isValid = false;
				String randomAsnNbr = null;
				String dbAsnNbr = null;
				String sqlasn;

				ArrayList<String> asnNbrs;

				while (!isValid) {
					asnNbrs = new ArrayList<String>();

					for (int i = 0; i <= 19; i++) {
						randomAsnNbr = stresnasnnbr.concat(CommonUtility.generateRandomNumber(5, true));
						asnNbrs.add(randomAsnNbr);
					}
					
					sqlasn = " select ESN_ASN_NBR from BULK_ESN where ESN_ASN_NBR in (:asnStr)";
					
					MapSqlParameterSource parameters = new MapSqlParameterSource();
					parameters.addValue("asnStr", asnNbrs);
					log.info("*** SQL sqlasn*****" + sqlasn);
					log.info(" parameters:  " + parameters.getValues());
					rsasn = namedParameterJdbcTemplate.queryForRowSet(sqlasn.toString(), parameters);
					
					List<String> existAsnNbrs = new ArrayList<String>();
					while (rsasn.next()) {
						dbAsnNbr = CommonUtility.deNull(rsasn.getString(1));
						// Wanyi added on Jan 2020 to cater for 6 or 7 digit ASN numbers
						if (dbAsnNbr.length() == 6) {
							dbAsnNbr = "00" + dbAsnNbr;
						} else if (dbAsnNbr.length() == 7) {
							dbAsnNbr = "0" + dbAsnNbr;
						}
						existAsnNbrs.add(dbAsnNbr);
						log.info("Resultset = " + dbAsnNbr);
					}
					log.info("asnNbrs before removeAll() = " + asnNbrs); // Wanyi added
					log.info("existAsnNbrs to be remove = " + existAsnNbrs); // Wanyi added
					asnNbrs.removeAll(existAsnNbrs);
					log.info("asnNbrs after removeAll() = " + asnNbrs); // Wanyi added

					if (asnNbrs.size() > 0) {
						stresnasnnbr = asnNbrs.get(0);
						isValid = true;
						log.info("New ASN Nbr = " + stresnasnnbr);
					}
				}
				// Added by Babatunde on Jan., 2014 : END

				// intesnasnnbr = intesnasnnbr + 2;

				// String strtempnbr = Integer.toString(intesnasnnbr);
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
			} else {
				stresnasnnbr = (strsqlyy).concat(strsqlmm);
				stresnasnnbr = stresnasnnbr.concat("00002");
			}
			
			log.info(" stresnasnnbr final:  " + stresnasnnbr);
			// new number generated
			esnasnnbr = stresnasnnbr;

		} catch (NullPointerException e) {
			log.info("Exception getEsnNoForDPE : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEsnNoForDPE : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEsnNoForDPE  DAO  END  esnasnnbr: " + esnasnnbr);
		}
		return esnasnnbr;
	}

	@Override
	public List<EsnListValueObject> getEsnDetails(String esnNbr, String custId) throws BusinessException {
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
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		List<EsnListValueObject> esnList = new ArrayList<EsnListValueObject>();
		EsnListValueObject esnListValueObject = new EsnListValueObject();
		try {
			log.info("START: getEsnDetails  DAO  Start Obj " + "esnNbr:" + CommonUtility.deNull(esnNbr) + "custId:" + CommonUtility.deNull(custId));
			sb.append(
					"SELECT A.*,B.CNTR_NBR FROM (SELECT E.BK_REF_NBR AS BKRNBR,SHIPPER_NM,SHIPPER_CD,PKG_TYPE,ES.CRG_DES AS CRGDESC,ESN_HS_CODE,NBR_PKGS, ");
			sb.append("ESN_WT,ESN_VOL,ESN_DG_IND,ESN_OPS_IND,STG_IND,ESN_LOAD_FROM,HS_SUB_CODE_FR, HS_SUB_CODE_TO, ");
			sb.append(
					"ESN_DUTY_GOOD_IND,TRUCKER_PHONE_NBR,TRUCKER_IC,TRUCKER_NM,PAYMENT_MODE,ACCT_NBR,ESN_PORT_DIS,STG_DAYS,UA_NBR_PKGS, ");
			sb.append(
					"VARIANCE_PKGS,VARIANCE_VOL,VARIANCE_WT,BK_NBR_PKGS,BK_WT,BK_VOL,E.STUFF_IND,CODE.CC_NAME,CODE.CC_CD, ");
			sb.append("ES.TRUCKER_NBR_PKGS, E.EPC_IND,E.CNTR_SEQ_NBR, E.MISC_APP_NBR, ES.CUSTOM_HS_CODE, ");
			sb.append(" BK.SHIPPER_ADDR, BK.CONS_NM, BK.CONSIGNEE_ADDR, BK.NOTIFY_PARTY, BK.NOTIFY_PARTY_ADDR, BK.PLACE_OF_DELIVERY, BK.PLACE_OF_RECEIPT, BK.BL_NBR");
			sb.append(" FROM BK_DETAILS BK, ESN E, ESN_DETAILS ES ,CARGO_CATEGORY_CODE CODE   ");
			sb.append("WHERE BK.BK_REF_NBR = E.BK_REF_NBR ");
			sb.append(
					"AND E.CARGO_CATEGORY_CD = CODE.CC_CD AND E.ESN_ASN_NBR = ES.ESN_ASN_NBR AND E.ESN_ASN_NBR =:esnNo ) A ");
			sb.append("LEFT OUTER JOIN CNTR B ON A.CNTR_SEQ_NBR = B.CNTR_SEQ_NBR ");
			sql = sb.toString();

			paramMap.put("esnNo", esnNbr);
			log.info("END: *** getEsnDetails SQL *****" + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				esnListValueObject.setBookingRefNo(CommonUtility.deNull(rs.getString("BKRNBR")));
				bookingRefNbr = esnListValueObject.getBookingRefNo();
				clsShpInd = getClsShpInd(bookingRefNbr);
				esnListValueObject.setShipperName(CommonUtility.deNull(rs.getString("SHIPPER_NM")));
				esnListValueObject.setShipperCode(CommonUtility.deNull(rs.getString("SHIPPER_CD")));
				esnListValueObject.setPkgType(CommonUtility.deNull(rs.getString("PKG_TYPE")));
				esnListValueObject.setCrgDesc(CommonUtility.deNull(rs.getString("CRGDESC")));
				esnListValueObject.setHsCode(CommonUtility.deNull(rs.getString("ESN_HS_CODE")));
				esnListValueObject.setNoOfCntr(rs.getInt("NBR_PKGS"));
				esnListValueObject.setVarNoofPakgs(rs.getDouble("VARIANCE_PKGS"));
				esnListValueObject.setVarGrVolume(rs.getDouble("VARIANCE_VOL"));
				esnListValueObject.setVarGrWt(rs.getDouble("VARIANCE_WT"));
				esnListValueObject.setBGrVolume(rs.getDouble("BK_VOL"));
				esnListValueObject.setBGrWt(rs.getDouble("BK_WT"));
				esnListValueObject.setBNoofPkgs(rs.getInt("BK_NBR_PKGS"));
				esnListValueObject.setGrWt(rs.getDouble("ESN_WT"));
				esnListValueObject.setGrVolume(rs.getDouble("ESN_VOL"));
				esnListValueObject.setDgInd(CommonUtility.deNull(rs.getString("ESN_DG_IND")));
				esnListValueObject.setOpInd(CommonUtility.deNull(rs.getString("ESN_OPS_IND")));
				esnListValueObject.setStgInd(CommonUtility.deNull(rs.getString("STG_IND")));
				esnListValueObject.setPortL(CommonUtility.deNull(rs.getString("ESN_LOAD_FROM")));
				esnListValueObject.setDutiGI(CommonUtility.deNull(rs.getString("ESN_DUTY_GOOD_IND")));
				esnListValueObject.setNoOfdays(rs.getInt("STG_DAYS"));
				esnListValueObject.setUaNoofPkgs(rs.getInt("UA_NBR_PKGS"));
				esnListValueObject.setPkgDesc(pkgsDesc);
				esnListValueObject.setCrgMarking(markings);
				esnListValueObject.SetTruckerCNo(CommonUtility.deNull(rs.getString("TRUCKER_PHONE_NBR")));
				esnListValueObject.setTruckerNo(CommonUtility.deNull(rs.getString("TRUCKER_IC")));
				esnListValueObject.setTruckerName(CommonUtility.deNull(rs.getString("TRUCKER_NM")));
				esnListValueObject.setStfInd(CommonUtility.deNull(rs.getString("STUFF_IND"))); // added by vani
				esnListValueObject.setCategory(rs.getString("CC_NAME")); // added by ZhenguoDeng(harbor)
				payMode = CommonUtility.deNull(rs.getString("PAYMENT_MODE"));
				accNo = CommonUtility.deNull(rs.getString("ACCT_NBR"));
				if (payMode.equals("A")) {
					billableParty = getBillablePartyName(accNo);
					esnListValueObject.setBillPartyName(billableParty);
				}
				portDis = CommonUtility.deNull(rs.getString("ESN_PORT_DIS"));
				portDisDesc = getPortDisDesc(portDis);
				esnListValueObject.setPayMode(payMode);
				esnListValueObject.setAccNo(accNo);
				esnListValueObject.setPortDesc(portDisDesc);
				esnListValueObject.setPortD(portDis);
				esnListValueObject.setClsShpInd(clsShpInd);

				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
				esnListValueObject.setHsSubCodeFr(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				esnListValueObject.setHsSubCodeTo(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));
				esnListValueObject.setHsSubCodeDesc(getHSSubCodeDes(esnListValueObject.getHsCode(),
						esnListValueObject.getHsSubCodeFr(), esnListValueObject.getHsSubCodeTo()));
				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END
				// HaiTTH1 added on 18/1/2014
				esnListValueObject.setTrucker_nbr_pkg(CommonUtility.deNull(rs.getString("TRUCKER_NBR_PKGS")));
				esnListValueObject.setCc_cd(CommonUtility.deNull(rs.getString("cc_cd")));
				// MCC for EPC_IND
				esnListValueObject.setDeliveryToEPC(CommonUtility.deNull(rs.getString("EPC_IND")));

				esnListValueObject.setCntrNbr(CommonUtility.deNull(rs.getString("CNTR_NBR")));
				//  CR FTZ HSCODE - NS JULY 2024
				esnListValueObject.setCustomHsCode(CommonUtility.deNull(rs.getString("CUSTOM_HS_CODE")));
				esnListValueObject.setShipperAddr(CommonUtility.deNull(rs.getString("SHIPPER_ADDR")));
				esnListValueObject.setConsNm(CommonUtility.deNull(rs.getString("CONS_NM")));
				esnListValueObject.setConsigneeAddr(CommonUtility.deNull(rs.getString("CONSIGNEE_ADDR")));
				esnListValueObject.setNotifyParty(CommonUtility.deNull(rs.getString("NOTIFY_PARTY")));
				esnListValueObject.setNotifyPartyAddr(CommonUtility.deNull(rs.getString("NOTIFY_PARTY_ADDR")));
				esnListValueObject.setPlaceOfDelivery(CommonUtility.deNull(rs.getString("PLACE_OF_DELIVERY")));
				esnListValueObject.setPlaceOfReceipt(CommonUtility.deNull(rs.getString("PLACE_OF_RECEIPT")));
				esnListValueObject.setBlNbr(CommonUtility.deNull(rs.getString("BL_NBR")));
				esnList.add(esnListValueObject);
			}

		} catch (NullPointerException e) {
			log.info("Exception getEsnDetails : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEsnDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEsnDetails  DAO  END  esnList.size: " + esnList.size());
		}
		return esnList;
	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->getBillablePartyName();
	public String getBillablePartyName(String accNbr) throws BusinessException {

		String sql = "";
		String billablePartyName = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START:  *** getBillablePartyName Dao Start criteria : *** " + CommonUtility.deNull(accNbr));

			sb.append("SELECT CO.CO_NM FROM CUST_ACCT CA, COMPANY_CODE CO WHERE CA.ACCT_STATUS_CD='A' ");
			sb.append("AND CO.CO_CD = CA.CUST_CD AND CA.ACCT_NBR =:accNbr");

			sql = sb.toString();
			log.info("END: *** getBillablePartyName SQL *****" + sql);

			// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
			paramMap.put("accNbr", (accNbr));
			log.info(" paramMap  : " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				billablePartyName = CommonUtility.deNull(rs.getString("co_nm"));
			}

		} catch (NullPointerException e) {
			log.info("Exception getBillablePartyName : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBillablePartyName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getBillablePartyName  END  billablePartyName: " + CommonUtility.deNull(billablePartyName));
		}
		return billablePartyName;

	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->getPortDisDesc();
	private String getPortDisDesc(String port_Dis) throws BusinessException {
		String portDis = port_Dis;
		String portDisDesc = "";
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		try {
			log.info("START:  *** getPortDisDesc Dao Start criteria : *** " + CommonUtility.deNull(portDis));
			sql = "select port_nm from un_port_code where port_cd =:portDis";
			log.info("END: *** getPortDisDesc SQL *****" + sql);

			// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
			paramMap.put("portDis", (portDis));
			log.info(" paramMap:  " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				portDisDesc = CommonUtility.deNull(rs.getString("port_nm"));
			}

		} catch (NullPointerException e) {
			log.info("Exception getBillablePartyName : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBillablePartyName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getPortDisDesc  END  portDisDesc: " + CommonUtility.deNull(portDisDesc));
		}
		return portDisDesc;

	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->getClsShpInd();
	private String getClsShpInd(String bookref) throws BusinessException {

		String clsShpInd = "";
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START:  *** getClsShpInd Dao Start criteria : *** " + CommonUtility.deNull(bookref));

			sb.append("SELECT VESSEL_CALL.GB_CLOSE_SHP_IND FROM VESSEL_CALL,BK_DETAILS  ");
			sb.append("WHERE VESSEL_CALL.VV_CD = BK_DETAILS.VAR_NBR AND BK_REF_NBR =:bookref  ");
			sql = sb.toString();

			log.info("END: *** getClsShpInd SQL *****" + sql);
			paramMap.put("bookref", bookref);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				clsShpInd = CommonUtility.deNull(rs.getString("gb_close_shp_ind"));
			}

		} catch (NullPointerException e) {
			log.info("Exception getClsShpInd : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getClsShpInd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getClsShpInd  END  clsShpInd: " + CommonUtility.deNull(clsShpInd));
		}
		return clsShpInd;
	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->getHSSubCodeDes();
	@Override
	public String getHSSubCodeDes(String hsCode, String hsSubCodeFr, String hsSubCodeTo) throws BusinessException {

		String sql = "";
		String desc = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		try {
			log.info("START:  *** getHSSubCodeDes Dao Start criteria : *** " + "hsCode :" + CommonUtility.deNull(hsCode) + "hsSubCodeFr:"
					+ CommonUtility.deNull(hsSubCodeFr) + "hsSubCodeTo:" + CommonUtility.deNull(hsSubCodeTo));
			sql = "SELECT HS_SUB_DESC FROM HS_SUB_CODE WHERE HS_CODE=:hsCode AND HS_SUB_CODE_FR =:hsSubCodeFr ";

			if (hsSubCodeTo != null && !"".equalsIgnoreCase(hsSubCodeTo)) {
				sql = sql + " AND HS_SUB_CODE_TO = :hsSubCodeTo";
			}
			log.info("END: *** getHSSubCodeDes SQL *****" + sql);

			paramMap.put("hsCode", hsCode);
			paramMap.put("hsSubCodeFr", hsSubCodeFr);
			if (hsSubCodeTo != null && !"".equalsIgnoreCase(hsSubCodeTo)) {
				paramMap.put("hsSubCodeTo", hsSubCodeTo);
			}
			log.info(" paramMap:  " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				desc = CommonUtility.deNull(rs.getString("HS_SUB_DESC"));
			}
			log.info("Enhanment HSCode from FPT in getHSSubCodeDes : desc " + CommonUtility.deNull(desc));

		} catch (NullPointerException e) {
			log.info("Exception getPortDisDesc : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getPortDisDesc : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getPortDisDesc  END  desc: " + CommonUtility.deNull(desc));
		}
		return desc;
	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->getPkgsType();
	private String getPkgsType(String esnNbr) throws BusinessException {

		String sql = "";
		String pkgsDesc = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START:  *** getPkgsType Dao Start criteria : *** " + CommonUtility.deNull(esnNbr));

			sb.append(
					"SELECT PKG_TYPE_CD,PKG_DESC FROM PKG_TYPES,ESN_DETAILS,ESN WHERE PKG_TYPES.PKG_TYPE_CD = ESN_DETAILS.PKG_TYPE   ");
			sb.append("AND ESN.ESN_ASN_NBR = ESN_DETAILS.ESN_ASN_NBR AND ESN.ESN_ASN_NBR = :esnNbr ");
			sql = sb.toString();

			log.info("END: *** getPkgsType SQL *****" + sql);
			paramMap.put("esnNbr", esnNbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				pkgsDesc = CommonUtility.deNull(rs.getString("PKG_DESC"));
			}

		} catch (NullPointerException e) {
			log.info("Exception getPkgsType : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getPkgsType : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getPkgsType  END  pkgsDesc: " + CommonUtility.deNull(pkgsDesc));
		}
		return pkgsDesc;

	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->getTruckerCd();
	@Override
	public String getTruckerCd(String trcIcNo) throws BusinessException {
		String truckerIcNo = trcIcNo;
		String sql;
		String truckerCd = "";
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		// Bhuvana - UEN No. Enhancement
		// Amended by Dongsheng on 26/8/2013. If the sql retrieves more than one
		// records, the record which is active and its line of
		// business is haulier will be the last one and be the trucker code to be
		// returned.
		// sql = "select cust_cd from customer where upper(tdb_cr_nbr) = '" +
		// GbmsCommonUtility.addApostr(truckerIcNo) + "' OR upper(uen_nbr) = '" +
		// GbmsCommonUtility.addApostr(truckerIcNo) + "'";

		try {

			log.info("START:  *** getTruckerCd Dao Start : *** " + CommonUtility.deNull(truckerIcNo));

			sql = "'" + GbmsCommonUtility.addApostr(truckerIcNo) + "'  '" + GbmsCommonUtility.addApostr(truckerIcNo)
					+ "') )";
			sb.append(" SELECT CUST.CUST_CD FROM COMPANY_CODE COCODE,CUSTOMER CUST WHERE CUST.CUST_CD= COCODE.CO_CD  ");
			sb.append(" AND (UPPER(TDB_CR_NBR)=:truckerIcNo ");
			sb.append(" OR UPPER(UEN_NBR) =:truckerIcNo) ");
			sb.append(" ORDER BY COCODE.REC_STATUS DESC, (CASE WHEN COCODE.LOB_CD = 'HAU' ");
			sb.append(" THEN 2 ELSE 1 END )");
			sql = sb.toString();
			// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
			paramMap.put("truckerIcNo", (truckerIcNo));
			log.info("END: *** getTruckerCd SQL *****" + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			
			// if (rs.next()) {
			// truckerCd = rs.getString("cust_cd");
			// }
			// else {
			// truckerCd = "";
			// }
			truckerCd = "";
			while (rs.next()) {
				truckerCd = rs.getString("cust_cd");
				log.info("*********Writing from EsnEJB.getTruckerCd --truckerCd: " + CommonUtility.deNull(truckerCd));
			}

		} catch (NullPointerException e) {
			log.info("Exception getTruckerCd : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTruckerCd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getTruckerCd  END  truckerCd: " + CommonUtility.deNull(truckerCd));
		}
		return truckerCd;
	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->getUAfirsttransdttm();
	@Override
	public String getUAfirsttransdttm(String esnnbr, String transtype) throws BusinessException {
		String sql = "";
		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String firsttransdttm = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		try {
			log.info("START:  *** getUAfirsttransdttm Dao Start criteria : *** " + "esnnbr :" + CommonUtility.deNull(esnnbr) + "transtype:"
					+ CommonUtility.deNull(transtype));

			sql1 = "select to_char(FIRST_TRANS_DTTM,'dd/mm/yyyy hh24:mi') FDTTM from esn_details where esn_asn_nbr = :esnnbr";
			sql2 = "select to_char(FIRST_TRANS_DTTM,'dd/mm/yyyy hh24:mi') FDTTM from tesn_jp_jp where esn_asn_nbr = :esnnbr";
			sql3 = "select to_char(FIRST_TRANS_DTTM,'dd/mm/yyyy hh24:mi') FDTTM from tesn_psa_jp where esn_asn_nbr = :esnnbr";

			if (transtype != null && !transtype.equals("") && transtype.equals("E"))
				sql = sql1;
			else if (transtype != null && !transtype.equals("") && transtype.equals("A"))
				sql = sql2;
			else if (transtype != null && !transtype.equals("") && transtype.equals("C"))
				sql = sql3;

			log.info("END: *** getUAfirsttransdttm SQL *****" + sql);

			paramMap.put("esnnbr", esnnbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				firsttransdttm = CommonUtility.deNull(rs.getString("FDTTM"));
			} else
				firsttransdttm = "";

		} catch (NullPointerException e) {
			log.info("Exception getUAfirsttransdttm : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getUAfirsttransdttm : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getUAfirsttransdttm  END  firsttransdttm: " + CommonUtility.deNull(firsttransdttm));
		}
		return firsttransdttm;
	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->getTesnPsaJpDetails();
	@Override
	public List<EsnListValueObject> getTesnPsaJpDetails(String esnNbr, String custId) throws BusinessException {
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
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();

		List<EsnListValueObject> esnList = new ArrayList<EsnListValueObject>();
		EsnListValueObject esnListValueObject = new EsnListValueObject();
		try {
			log.info("START:  *** getTesnPsaJpDetails Dao Start criteria : *** " + "esnNbr:" + CommonUtility.deNull(esnNbr) + "custId:"
					+ CommonUtility.deNull(custId));

			sb.append(
					" SELECT E.BK_REF_NBR AS BKRNBR, BK.SHIPPER_NM AS SHIPPER_NM, PKG_TYPE, ES.CRG_DES AS CRGDESC, HS_CD, ");
			sb.append(" ES.HS_SUB_CODE_FR, ES.HS_SUB_CODE_TO, ");
			sb.append(" ES.NBR_PKGS AS NBR_PKGS, GROSS_WT, GROSS_VOL, DG_IND, OPS_IND, STORAGE_IND, ");
			sb.append(
					" DIS_PORT, STORAGE_DAYS, UA_NBR_PKGS, VARIANCE_PKGS, VARIANCE_VOL, VARIANCE_WT, BK_VOL, BK_WT, BK_NBR_PKGS, ");
			sb.append(" FIRST_CAR_VOY_NBR, FIRST_CAR_VES_NM,PAYMENT_MODE,ACCT_NBR ");
			sb.append(" FROM BK_DETAILS BK, ESN E, TESN_PSA_JP ES ");
			sb.append(" WHERE BK.BK_REF_NBR = E.BK_REF_NBR ");
			sb.append(" AND E.ESN_ASN_NBR = ES.ESN_ASN_NBR AND ES.ESN_ASN_NBR = :esnNbr");
			sql = sb.toString();
			log.info("END: *** getTesnPsaJpDetails SQL *****" + sql);

			paramMap.put("esnNbr", esnNbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (rs.next()) {
				esnListValueObject.setBookingRefNo(CommonUtility.deNull(rs.getString("BKRNBR")));
				bookingRefNbr = esnListValueObject.getBookingRefNo();
				clsShpInd = getClsShpInd(bookingRefNbr);
				esnListValueObject.setShipperName(CommonUtility.deNull(rs.getString("SHIPPER_NM")));
				esnListValueObject.setPkgType(CommonUtility.deNull(rs.getString("PKG_TYPE")));
				esnListValueObject.setCrgDesc(CommonUtility.deNull(rs.getString("CRGDESC")));
				esnListValueObject.setHsCode(CommonUtility.deNull(rs.getString("HS_CD")));
				esnListValueObject.setHsSubCodeFr(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				esnListValueObject.setHsSubCodeTo(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));
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
				esnList.add(esnListValueObject);
			}

		} catch (NullPointerException e) {
			log.info("Exception getTesnPsaJpDetails : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTesnPsaJpDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getTesnPsaJpDetails  END  esnList.size: " + esnList.size());
		}
		return esnList;
	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->getEdiBlNbr();
	@Override
	public String getEdiBlNbr(String bkrefNbr) throws BusinessException {
		// String esnNo = esnNbr;
		String sql = "";
		String blNbr = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		try {
			log.info("START:  *** getEdiBlNbr Dao Start criteria : *** " + CommonUtility.deNull(bkrefNbr));

			sql = "select BL_NO from igd_detail_buffer where BK_REF_NBR = :bkrefNbr";
			log.info("getEdiBlNbr SQL *****" + sql);

			paramMap.put("bkrefNbr", bkrefNbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				blNbr = CommonUtility.deNull(rs.getString("BL_NO"));
			} else
				blNbr = "";

		} catch (NullPointerException e) {
			log.info("Exception getEdiBlNbr : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEdiBlNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getEdiBlNbr  END  blNbr: " + CommonUtility.deNull(blNbr));
		}
		return blNbr;
	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->getJpJpDetails();
	@Override
	public List<EsnListValueObject> getJpJpDetails(String esnNbr) throws BusinessException {
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		List<EsnListValueObject> esnList = new ArrayList<EsnListValueObject>();
		EsnListValueObject esnListValueObject = new EsnListValueObject();
		try {
			log.info("START:  *** getJpJpDetails Dao Start criteria : *** " + CommonUtility.deNull(esnNbr));
			sql = "SELECT * FROM TESN_JP_JP WHERE ESN_ASN_NBR =:esnNbr";
			log.info("END: *** getJpJpDetails SQL *****" + sql);

			paramMap.put("esnNbr", esnNbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				esnListValueObject.setBookingRefNo(CommonUtility.deNull(rs.getString("EDO_ASN_NBR")));
				esnListValueObject.setOpInd(CommonUtility.deNull(rs.getString("LD_IND")));
				esnListValueObject.setNoOfCntr(rs.getInt("NBR_PKGS"));
				esnListValueObject.setGrWt(rs.getDouble("NOM_WT"));
				esnListValueObject.setGrVolume(rs.getDouble("NOM_VOL"));
				esnListValueObject.setPayMode(rs.getString("PAYMENT_MODE"));
				esnListValueObject.setAccNo(rs.getString("ACCT_NBR"));
				esnListValueObject.setUaNoofPkgs(rs.getInt("UA_NBR_PKGS"));
				esnListValueObject.setBNoofPkgs(rs.getInt("DN_NBR_PKGS"));

				esnList.add(esnListValueObject);
			}

		} catch (NullPointerException e) {
			log.info("Exception getJpJpDetails : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getJpJpDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getJpJpDetails  END  esnList.size: " + esnList.size());
		}
		return esnList;
	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->getMarkings();
	@Override
	public String getMarkings(String esnNbr) throws BusinessException {

		String sql = "";
		String markings = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;

		try {
			log.info("START:  *** getMarkings Dao Start criteria : *** " + CommonUtility.deNull(esnNbr));

			sql = "select * from ESN_MARKINGS where ESN_ASN_NBR = :esnNbr";
			log.info("END: *** getMarkings SQL *****" + sql);
			paramMap.put("esnNbr", esnNbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				markings = CommonUtility.deNull(rs.getString("MARKINGS"));
			}

		} catch (NullPointerException e) {
			log.info("Exception getMarkings : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getMarkings : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getMarkings  END  markings: " + CommonUtility.deNull(markings));
		}
		return markings;
	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->getCntrDetails();
	@Override
	public List<EsnListValueObject> getCntrDetails(String esnNbr) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		List<EsnListValueObject> cntrDetails = new ArrayList<EsnListValueObject>();
		EsnListValueObject esnListValueObject = new EsnListValueObject();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START:  *** getCntrDetails Dao Start criteria : *** " + CommonUtility.deNull(esnNbr));
			sb.append("SELECT CNTR_NBR,ESN_CNTR_SEQ,CNTR_TYPE,CNTR_SIZE FROM BK_DETAILS BKD,ESN ES,ESN_CNTR EC  ");
			sb.append(
					"WHERE ES.BK_REF_NBR = BKD.BK_REF_NBR AND ES.ESN_ASN_NBR = EC.ESN_ASN_NBR AND ES.ESN_ASN_NBR=:esnNbr");
			sql = sb.toString();

			log.info("END: *** getCntrDetails SQL *****" + sql);

			paramMap.put("esnNbr", esnNbr);
			log.info(" paramMap: " + paramMap);
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
			}
		} catch (NullPointerException e) {
			log.info("Exception getCntrDetails : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCntrDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getCntrDetails  END  cntrDetails: " + cntrDetails.size());
		}
		return cntrDetails;
	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->TransferCrgUpdateForDPE();
	@Override
	public List<EsnListValueObject> TransferCrgUpdateForDPE(String bk_ref_nbr[], String newbkrefnbr[], String esnarr[],
			String transtypearr[], String transNbr[], String shutoutqty[], String actnbrshped[], String uanbrpkgs[],
			String uaftdttm[], String Toutvoynbr, String varnoF, String varnoT, String UserID)
			throws BusinessException {
		
		// TODO multiplehscode

		String sql = "";
		String bksql = "";
		String esnsql = "";
		String strEsnDetails = "";
		String strMark = "";
		String newesnno = "";
		String esnmarkings = "";
		String esncntrsql = "";
		String bkupdsql = "";
		String sqlft = "";

		String sql1 = "";
		String sql2 = "";
		String sql3 = "";

		String sqlUpdate = "";
		String sqlUpdate_esn = "";
		String strtesnpjDetails = "";
		String tesnjpjpdetails = "";

		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();

		// Vector bk_detailst = new Vector();
		EsnListValueObject esnListValueObject = null;
		BookingReferenceValueObject bkvo = null;

		List<EsnListValueObject> vc1 = new ArrayList<EsnListValueObject>();
		List<BookingReferenceValueObject> bkvec = new ArrayList<BookingReferenceValueObject>();
		// Vector vc2 = new Vector();
		List<EsnListValueObject> esnvec = new ArrayList<EsnListValueObject>();
		List<EsnListValueObject> esndetvec = new ArrayList<EsnListValueObject>();
		List<EsnListValueObject> esncntrvec = new ArrayList<EsnListValueObject>();
		List<EsnListValueObject> tesnpsajpdetvec = new ArrayList<EsnListValueObject>();
		List<EsnListValueObject> tesnjjdetvec = new ArrayList<EsnListValueObject>();

		double bk_varpkgs = 0.0;
		double bk_varwt = 0.0;
		double bk_varvol = 0.0;

		BigDecimal bdvarpkgs = null;
		BigDecimal bdvarwt = null;
		BigDecimal bdvarvol = null;

		int bkcnt = 0;
		int esncnt = 0;
		int esndetcnt = 0;
		int esnmarkcnt = 0;
		int tesnpjcnt = 0;
		int tesnjjcnt = 0;
		int bkupdcnt = 0;

		StringBuilder sb = new StringBuilder();

		try {
			log.info("START:  *** TransferCrgUpdateForDPE Dao Start criteria : *** " + "bk_ref_nbr:"
					+ CommonUtility.deNull(Arrays.toString(bk_ref_nbr)) + " ,newbkrefnbr:" + CommonUtility.deNull(Arrays.toString(newbkrefnbr)) + " ,esnarr:"
					+ CommonUtility.deNull(Arrays.toString(esnarr)) + " ,transtypearr:" + CommonUtility.deNull(Arrays.toString(transtypearr)) + " ,transNbr:"
					+ CommonUtility.deNull(Arrays.toString(transNbr)) + " ,shutoutqty:" + CommonUtility.deNull(Arrays.toString(shutoutqty)) + ",actnbrshped:"
					+ CommonUtility.deNull(Arrays.toString(actnbrshped)) + " ,uanbrpkgs:" + CommonUtility.deNull(Arrays.toString(uanbrpkgs)) + " ,uaftdttm:"
					+ CommonUtility.deNull(Arrays.toString(uaftdttm)) + "Toutvoynbr:" + CommonUtility.deNull(Toutvoynbr) + "varnoF:" + CommonUtility.deNull(varnoF)
					+ "varnoT:" + CommonUtility.deNull(varnoT) + "UserID:" + CommonUtility.deNull(UserID));

			boolean bbkno = chkBKNo(newbkrefnbr);
			log.info("bbkno value: " + bbkno);
			if (bbkno) {
				log.info("EsnEJB.TransferCrgUpdate : Booking Ref No already exists");
				throw new BusinessException("M20608");

			}

			for (int i = 0; i < bk_ref_nbr.length; i++) {
				double transNbr_int = Double.parseDouble(transNbr[i]);
				String newbknbr = newbkrefnbr[i];
				int shutoutqtyval = Integer.parseInt(shutoutqty[i]);
				String bkrNbr_arr = bk_ref_nbr[i];
				String esnnbr = esnarr[i];
				String transtype = transtypearr[i];
				int actnbrshpval = 0;
				actnbrshpval = Integer.parseInt(actnbrshped[i]);
				int uanbrpkgsval = Integer.parseInt(uanbrpkgs[i]);
				String uaftransdttm = uaftdttm[i];

				if (transNbr_int > 0 && actnbrshpval > 0 && shutoutqtyval <= uanbrpkgsval) {

					bkvec = getBKDetails(bkrNbr_arr);

					if (bkvec.size() > 0) {
						bkvo =(BookingReferenceValueObject) bkvec.get(0);
						double bk_wt = (Double.parseDouble(bkvo.getWeight()) / Double.parseDouble(bkvo.getPackageNos()))
								* transNbr_int;
						double bk_vol = (Double.parseDouble(bkvo.getVolume())
								/ Double.parseDouble(bkvo.getPackageNos())) * transNbr_int;

						if (Double.parseDouble(bkvo.getPackageVariance()) > 0) {
							bk_varpkgs = (Double.parseDouble(bkvo.getPackageVariance())
									/ Double.parseDouble(bkvo.getPackageNos())) * transNbr_int;
							bdvarpkgs = new BigDecimal(bk_varpkgs).setScale(2, BigDecimal.ROUND_HALF_UP);
						} else {
							bdvarpkgs = new BigDecimal("" + 0);
						}

						if (Double.parseDouble(bkvo.getWeightVariance()) > 0) {
							bk_varwt = (Double.parseDouble(bkvo.getWeightVariance())
									/ Double.parseDouble(bkvo.getPackageNos())) * transNbr_int;
							bdvarwt = new BigDecimal(bk_varwt).setScale(2, BigDecimal.ROUND_HALF_UP);
						} else {
							bdvarwt = new BigDecimal("" + 0);
						}

						if (Double.parseDouble(bkvo.getVolumeVariance()) > 0) {
							bk_varvol = (Double.parseDouble(bkvo.getVolumeVariance())
									/ Double.parseDouble(bkvo.getPackageNos())) * transNbr_int;
							bdvarvol = new BigDecimal(bk_varvol).setScale(2, BigDecimal.ROUND_HALF_UP);
						} else {
							bdvarvol = new BigDecimal("" + 0);
						}

						BigDecimal bdWt = new BigDecimal(bk_wt).setScale(2, BigDecimal.ROUND_HALF_UP);
						BigDecimal bdVol = new BigDecimal(bk_vol).setScale(2, BigDecimal.ROUND_HALF_UP);
						sb.append(
								"INSERT INTO BK_DETAILS(BK_REF_NBR,BK_STATUS,ESN_DECLARED,VAR_NBR,OUT_VOY_NBR,CARGO_TYPE, ");
						sb.append(
								"BK_NBR_PKGS,BK_WT,BK_VOL,VARIANCE_PKGS,VARIANCE_VOL,VARIANCE_WT,PORT_DIS,DECLARANT_CD,BK_CREATE_CD, ");
						sb.append(
								"SHIPPER_CD,SHIPPER_CR_NBR,SHIPPER_CONTACT,SHIPPER_ADDR,SHIPPER_NM,CNTR_TYPE,NBR_OF_CNTR,CNTR_SIZE, ");
						sb.append(
								"LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,OLD_BK_REF,BK_ORIGINAL_VAR_NBR,TRANSFER_DTTM,Trans_crg) ");
						sb.append(
								"VALUES(:newbknbr,'A','Y',:varnoT,:Toutvoynbr,:cargo_Type,:transNbr_int, :bdWt ,:bdVol,:bdvarpkgs, ");
						sb.append(
								":bdvarvol,:bdvarwt,:port_Of_Discharge,:esn_Declarant_No,:bkCreateCd,:bkShipperCd,:shipper_Cr_No, ");
						sb.append(
								":shipper_Contact,:shipper_Add,:shipper_Name,:container_Type,:no_Of_Containers,:container_Size,:UserID, ");
						sb.append(" SYSDATE,:bkrNbr_arr,:varnoF,sysdate,'Y' )");

						bksql = sb.toString();
						paramMap.put("newbknbr", newbknbr);
						paramMap.put("varnoT", varnoT);
						paramMap.put("Toutvoynbr", Toutvoynbr);
						paramMap.put("cargo_Type", bkvo.getCargoType());
						paramMap.put("transNbr_int", transNbr_int);
						paramMap.put("bdWt", bdWt);
						paramMap.put("bdVol", bdVol);
						paramMap.put("bdvarpkgs", bdvarpkgs);
						paramMap.put("port_Of_Discharge", bkvo.getPortOfDischarge());
						paramMap.put("esn_Declarant_No", bkvo.getEsnDeclarantNo());
						paramMap.put("bkCreateCd", bkvo.getBkCreateCd());
						paramMap.put("bkShipperCd", bkvo.getBkShipperCd());
						paramMap.put("shipper_Cr_No", bkvo.getShipperCrNo());
						paramMap.put("shipper_Contact", bkvo.getShipperContactNo());
						// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
						paramMap.put("shipper_Add",
								(CommonUtility.deNull(bkvo.getShipperAddress())));
						paramMap.put("shipper_Name",
								(CommonUtility.deNull(bkvo.getShipperName())));
						paramMap.put("container_Type", CommonUtility.deNull(bkvo.getContainerType()));
						paramMap.put("no_Of_Containers", CommonUtility.deNull(bkvo.getNoContainer()));
						paramMap.put("container_Size", CommonUtility.deNull(bkvo.getContainerSize()));
						paramMap.put("UserID", UserID);
						paramMap.put("bkrNbr_arr", bkrNbr_arr);
						paramMap.put("varnoF", varnoF);
						paramMap.put("bdvarvol", bdvarvol);
						paramMap.put("bdvarwt", bdvarwt);
						log.info("TransferCrgUpdateForDPE bksql SQL" + bksql.toString());
						log.info(" paramMap: " + paramMap);
						bkcnt = namedParameterJdbcTemplate.update(bksql.toString(), paramMap);
						log.info("TransferCrgUpdateForDPE bksql SQL :: bkcnt result: " + bkcnt);
						if ((bkcnt == 0)) {
							log.info("EsnEJB.TransferCrgUpdate bkcnt : Record Cannot be added to Database");
							throw new BusinessException("M4201");
						}

						sb.setLength(0);
						paramMap = new HashMap<String, Object>();
						sb.append(
								"INSERT INTO BK_DETAILS_TRANS(TRANS_NBR,BK_REF_NBR,ESN_DECLARED,VAR_NBR,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
						sb.append("VALUES( 1 ,:newbknbr,'Y',:varnoT,:UserID,sysdate)");

						String strBKDetailsTrans = sb.toString();
						paramMap.put("newbknbr", newbknbr);
						paramMap.put("varnoT", varnoT);
						paramMap.put("UserID", UserID);
						log.info("TransferCrgUpdateForDPE strBKDetailsTrans SQL" + strBKDetailsTrans.toString());
						log.info(" paramMap: " + paramMap);
						bkcnt = namedParameterJdbcTemplate.update(strBKDetailsTrans.toString(), paramMap);
						log.info("TransferCrgUpdateForDPE strBKDetailsTrans SQL :: bkcnt result: " + bkcnt);
						if ((bkcnt == 0)) {
							log.info("EsnEJB.TransferCrgUpdate bkcnt : Record Cannot be added to Database");
							throw new BusinessException("M4201");
						}
						// VietNguyen

						// end of Add by Revathi

						// CR-CIM- 0000109
						// bkupdsql = "UPDATE BK_DETAILS SET TRANSFER_PKGS=TRANSFER_PKGS+" +
						sb.setLength(0);
						paramMap = new HashMap<String, Object>();
						sb.append("UPDATE BK_DETAILS SET TRANSFER_PKGS=NVL(TRANSFER_PKGS,0)+ :transNbr_int,");
						sb.append(
								"LAST_MODIFY_DTTM=SYSDATE,LAST_MODIFY_USER_ID=:UserID WHERE BK_REF_NBR=:bkrNbr_arr  ");
						bkupdsql = sb.toString();
						paramMap.put("bkrNbr_arr", bkrNbr_arr);
						paramMap.put("transNbr_int", transNbr_int);
						paramMap.put("UserID", UserID);
						log.info("TransferCrgUpdateForDPE bkupdsql SQL" + bkupdsql.toString());
						log.info(" paramMap: " + paramMap);
						bkupdcnt = namedParameterJdbcTemplate.update(bkupdsql.toString(), paramMap);
						log.info("TransferCrgUpdateForDPE bkupdsql SQL :: bkupdcnt result: " + bkupdcnt);
						if ((bkupdcnt == 0)) {
							log.info("EsnEJB.TransferCrgUpdate bkupdsql : Record Cannot be added to Database");
							throw new BusinessException("M4201");
						}

					} // if bk vec

					esnvec = getEsndtls(esnnbr);
					if (esnvec.size() > 0) {

						EsnListValueObject esnVO = (EsnListValueObject) esnvec.get(0);

						newesnno = getEsnNoForDPE();
						sb.setLength(0);
						paramMap = new HashMap<String, Object>();
						sb.append("INSERT INTO ESN(ESN_ASN_NBR,DECLARANT_CR_NO,BK_REF_NBR,TRANS_TYPE,IN_VOY_VAR_NBR, ");
						sb.append(
								"OUT_VOY_VAR_NBR,ESN_STATUS,ESN_CREATE_CD,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,CARGO_CATEGORY_CD, ");
						sb.append(
								"WH_IND,FREE_STG_DAYS,WH_REMARKS,WH_AGGR_NBR,STUFF_IND,CNTR_SEQ_NBR,MISC_APP_NBR,EPC_IND,Trans_crg) ");
						sb.append(
								"VALUES( :newesnno,'O',:newbknbr,:transtype,:invoyvarnbr,:varnoT,'A',:declarantcd,:UserID,sysdate, ");
						sb.append(
								":crgcatcd,:whind,:fsday,:whrems,:whaggrnbr,:stfInd,:cntr1,:miscAppNo,:deliveryToEPC,'Y' )");

						esnsql = sb.toString();
						paramMap.put("newesnno", newesnno);
						paramMap.put("newbknbr", newbknbr);
						paramMap.put("transtype", transtype);
						paramMap.put("invoyvarnbr", esnVO.getInvoyvarnbr());
						paramMap.put("varnoT", varnoT);
						paramMap.put("declarantcd", esnVO.getDeclarantcd());
						paramMap.put("UserID", UserID);
						paramMap.put("crgcatcd", esnVO.getCrgcatcd());
						paramMap.put("whind", CommonUtility.deNull(esnVO.getWhind()));
						paramMap.put("fsday", CommonUtility.deNull(esnVO.getFsdays()));
						paramMap.put("whrems", CommonUtility.deNull(esnVO.getWhrem()));
						paramMap.put("whaggrnbr", CommonUtility.deNull(esnVO.getWhaggrnbr()));
						paramMap.put("stfInd", CommonUtility.deNull(esnVO.getStfInd()));
						paramMap.put("cntr1", CommonUtility.deNull(esnVO.getCntr1()));
						paramMap.put("miscAppNo", CommonUtility.deNull(esnVO.getMiscAppNo()));
						paramMap.put("deliveryToEPC", CommonUtility.deNull(esnVO.getDeliveryToEPC()));
						log.info("TransferCrgUpdateForDPE esnsql SQL" + esnsql.toString());
						log.info(" paramMap: " + paramMap);
						esncnt = namedParameterJdbcTemplate.update(esnsql.toString(), paramMap);
						log.info("TransferCrgUpdateForDPE esnsql SQL :: esncnt result: " + esncnt);
						if (esncnt == 0) {
							log.info("EsnEJB.TransferCrgUpdate esnsql : Record Cannot be added to Database");
							throw new BusinessException("M4201");
						}

						sb.setLength(0);
						paramMap = new HashMap<String, Object>();
						sb.append(
								"INSERT INTO ESN_Trans(ESN_ASN_NBR,TRANS_NBR,DECLARANT_CR_NO,BK_REF_NBR,TRANS_TYPE, ");
						sb.append(
								"OUT_VOY_VAR_NBR,ESN_CREATE_CD,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,ESN_STATUS,STUFF_IND,CARGO_CATEGORY_CD) ");
						sb.append(
								"VALUES( :newesnno,0,'O',:newbknbr,'E',:varnoT,:declarantcd,:UserID,sysdate,'A',:stfInd, :crgCatCd ) ");

						String strInsertTrans = sb.toString();
						paramMap.put("newesnno", newesnno);
						paramMap.put("newbknbr", newbknbr);
						paramMap.put("varnoT", varnoT);
						paramMap.put("declarantcd", esnVO.getDeclarantcd());
						paramMap.put("UserID", UserID);
						paramMap.put("stfInd", esnVO.getStfInd());
						paramMap.put("crgCatCd", esnVO.getCrgcatcd());
						log.info("TransferCrgUpdateForDPE strInsertTrans SQL" + strInsertTrans.toString());
						log.info(" paramMap: " + paramMap);
						esncnt = namedParameterJdbcTemplate.update(strInsertTrans.toString(), paramMap);
						log.info("TransferCrgUpdateForDPE strInsertTrans SQL :: esncnt result: " + esncnt);
						if (esncnt == 0) {
							log.info("EsnEJB.TransferCrgUpdate esnsql : Record Cannot be added to Database");
							throw new BusinessException("M4201");
						}
					} // if esnvec

					esndetvec = getEsnDetails(esnnbr, ""); // method
					if (esndetvec.size() > 0) {
						EsnListValueObject esndetVO =  esndetvec.get(0);
						String hacustcd = getTruckerCd(esndetVO.getTruckerNo());	
						String firsttransdttm = getUAfirsttransdttm(esnnbr, transtype); // method
						double esn_wt = esndetVO.getGrWt() / Double.parseDouble("" + esndetVO.getNoOfCntr()) * transNbr_int;
						double esn_vol = esndetVO.getGrVolume() / Double.parseDouble("" + esndetVO.getNoOfCntr())
								* transNbr_int;
						BigDecimal bdesnWt = new BigDecimal(esn_wt).setScale(2, BigDecimal.ROUND_HALF_UP);
						BigDecimal bdesnVol = new BigDecimal(esn_vol).setScale(2, BigDecimal.ROUND_HALF_UP);
						
							sb.setLength(0);
							paramMap = new HashMap<String, Object>();
							sb.append(
									"INSERT INTO ESN_DETAILS(ESN_ASN_NBR,HA_CUST_CD,ESN_PORT_DIS,ESN_OPS_IND,ESN_LOAD_FROM, ");
							sb.append("ESN_DG_IND,ESN_HS_CODE,ESN_DUTY_GOOD_IND,TRUCKER_NM,TRUCKER_IC, ");
							sb.append(
									"STG_DAYS,STG_IND,PKG_TYPE,NBR_PKGS,ESN_WT,ESN_VOL,UA_NBR_PKGS,ACCT_NBR,PAYMENT_MODE,CRG_DES, ");
							sb.append("TRUCKER_PHONE_NBR, HS_SUB_CODE_FR, HS_SUB_CODE_TO,FIRST_TRANS_DTTM, CUSTOM_HS_CODE) ");
							sb.append(
									"VALUES(:newesnno,:hacustcd,:portD,:opInd,:portL,:dgInd,:hsCode,:dutiGI,:truckerName, ");
							sb.append(
									":truckerNo,:noOfDays,:stgInd,:pkgType,:transNbr_int,:bdesnWt,:bdesnVol,:transNbr_int, ");
							sb.append(
									":accNo,:payMode,:crgDesc,:truckerContactNo,:hsSubCodeFr,:hsSubCodeTo,TO_DATE(:firsttransdttm,'DD/MM/YYYY HH24:MI'), :customHsCode)");
	
							strEsnDetails = sb.toString();
							paramMap.put("newesnno", newesnno);
							paramMap.put("hacustcd", CommonUtility.deNull(hacustcd));
							paramMap.put("portD", esndetVO.getPortD());
							paramMap.put("opInd", esndetVO.getOpInd());
							paramMap.put("portL", esndetVO.getPortL());
							paramMap.put("dgInd", esndetVO.getDgInd());
							paramMap.put("hsCode", esndetVO.getHsCode());
							paramMap.put("dutiGI", esndetVO.getDutiGI());
							// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
							paramMap.put("truckerName",
									(CommonUtility.deNull(esndetVO.getTruckerName())));
							paramMap.put("truckerNo",
									(CommonUtility.deNull(esndetVO.getTruckerNo())));
							paramMap.put("noOfDays", esndetVO.getNoOfdays());
							paramMap.put("stgInd", esndetVO.getStgInd());
							paramMap.put("pkgType", esndetVO.getPkgType());
							paramMap.put("transNbr_int", transNbr_int);
							paramMap.put("bdesnWt", bdesnWt);
							paramMap.put("bdesnVol", bdesnVol);
							paramMap.put("transNbr_int", transNbr_int);
							paramMap.put("accNo", esndetVO.getAccNo());
							paramMap.put("payMode", esndetVO.getPayMode());
							paramMap.put("crgDesc",
									(CommonUtility.deNull(esndetVO.getCrgDesc())));
							paramMap.put("truckerContactNo", esndetVO.getTruckerCNo());
							paramMap.put("hsSubCodeFr", esndetVO.getHsSubCodeFr());
							paramMap.put("hsSubCodeTo", esndetVO.getHsSubCodeTo());
							paramMap.put("customHsCode", esndetVO.getCustomHsCode());
							paramMap.put("firsttransdttm", firsttransdttm);
						 // if esndetvec
						if (transtype.equals("E")) {
							
							log.info("TransferCrgUpdateForDPE strEsnDetails SQL" + strEsnDetails.toString());
							log.info(" paramMap: " + paramMap);
							esndetcnt = namedParameterJdbcTemplate.update(strEsnDetails.toString(), paramMap);
							log.info("TransferCrgUpdateForDPE strEsnDetails SQL :: esndetcnt result: " + esndetcnt);
	
							if (esndetcnt == 0) {
								log.info("EsnEJB.TransferCrgUpdate strEsnDetails : Record Cannot be added to Database");
								throw new BusinessException("M4201");
							}
						} // if E
					}
					
					tesnpsajpdetvec = getTesnPsaJpDetails(esnnbr, "");

					if (tesnpsajpdetvec.size() > 0) {
						EsnListValueObject tesnpjVO = (EsnListValueObject) tesnpsajpdetvec.get(0);

						double tesnpj_wt = tesnpjVO.getGrWt() / Double.parseDouble("" + tesnpjVO.getNoOfCntr())
								* transNbr_int;
						double tesnpj_vol = tesnpjVO.getGrVolume() / Double.parseDouble("" + tesnpjVO.getNoOfCntr())
								* transNbr_int;

						BigDecimal bdtesnpjWt = new BigDecimal(tesnpj_wt).setScale(2, BigDecimal.ROUND_HALF_UP);
						BigDecimal bdtesnpjVol = new BigDecimal(tesnpj_vol).setScale(2, BigDecimal.ROUND_HALF_UP);
						log.info("bdtesnpjVol" + bdtesnpjVol.toString());
						
						String blNbr = getEdiBlNbr(bkrNbr_arr);
						String firsttransdttm = getUAfirsttransdttm(esnnbr, transtype);

						sb.setLength(0);
						paramMap = new HashMap<String, Object>();
						sb.append(
								"INSERT INTO TESN_PSA_JP(ESN_ASN_NBR,DIS_PORT,HS_CD,PKG_TYPE,CRG_DES,NBR_PKGS,UA_NBR_PKGS, ");
						sb.append(
								"GROSS_WT,GROSS_VOL,DG_IND,STORAGE_IND,STORAGE_DAYS,OPS_IND,FIRST_CAR_VES_NM,FIRST_CAR_VOY_NBR, ");
						sb.append("PAYMENT_MODE,ACCT_NBR,BL_NBR,FIRST_TRANS_DTTM, HS_SUB_CODE_FR, HS_SUB_CODE_TO, CUSTOM_HS_CODE) ");
						sb.append(
								"VALUES(:newesnno,:portD,:hsCode,:pkgType,:crgDesc,:transNbr_int,:transNbr_int,:bdtesnpjWt, ");
						sb.append(
								":bdtesnpjVol,:dgInd,:stgInd,:noOfDays,:opInd,:firstCName,:invoyageNo,:payMode,:accNo,:blNbr, ");
						sb.append("to_date(:firsttransdttm,'DD/MM/YYYY HH24:MI'),:hsSubCodeFr,:hsSubCodeTo, :customHsCode )");

						strtesnpjDetails = sb.toString();
						paramMap.put("newesnno", newesnno);
						// start fixing Itsm #40244. added missing parameter set - NS May 2024
						paramMap.put("bdtesnpjVol", bdtesnpjVol);
						// end fixing Itsm #40244. added missing parameter set - NS May 2024
						paramMap.put("portD", tesnpjVO.getPortD());
						paramMap.put("hsCode", tesnpjVO.getHsCode());
						paramMap.put("pkgType", tesnpjVO.getPkgType());
						// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
						paramMap.put("crgDesc",
								(CommonUtility.deNull(tesnpjVO.getCrgDesc())));
						paramMap.put("transNbr_int", transNbr_int);
						paramMap.put("transNbr_int", transNbr_int);
						paramMap.put("bdtesnpjWt", bdtesnpjWt);
						paramMap.put("dgInd", tesnpjVO.getDgInd());
						paramMap.put("stgInd", tesnpjVO.getStgInd());
						paramMap.put("noOfDays", tesnpjVO.getNoOfdays());
						paramMap.put("opInd", tesnpjVO.getOpInd());
						paramMap.put("firstCName",
								(CommonUtility.deNull(tesnpjVO.getFirstCName())));
						paramMap.put("invoyageNo",
								(CommonUtility.deNull(tesnpjVO.getInvoyageNo())));
						paramMap.put("payMode", tesnpjVO.getPayMode());
						paramMap.put("accNo", (CommonUtility.deNull(tesnpjVO.getAccNo())));
						paramMap.put("blNbr", blNbr);
						paramMap.put("firsttransdttm", firsttransdttm);
						paramMap.put("hsSubCodeFr", tesnpjVO.getHsSubCodeFr());
						paramMap.put("hsSubCodeTo", tesnpjVO.getHsSubCodeTo());
						paramMap.put("customHsCode", tesnpjVO.getCustomHsCode());
					
						if (transtype.equals("C")) {
							log.info("TransferCrgUpdateForDPE strtesnpjDetails SQL" + strtesnpjDetails.toString());
							log.info("paramMap: " + paramMap);
							tesnpjcnt = namedParameterJdbcTemplate.update(strtesnpjDetails.toString(), paramMap);
							log.info("TransferCrgUpdateForDPE strtesnpjDetails SQL :: tesnpjcnt result: " + tesnpjcnt);
							
							if (tesnpjcnt == 0) {
								log.info("EsnEJB.TransferCrgUpdate strtesnpjDetails : Record Cannot be added to Database");
								throw new BusinessException("M4201");
							}
						} // if C
					} // if tesnpsajpdetvec

					tesnjjdetvec = getJpJpDetails(esnnbr);

					if (tesnjjdetvec.size() > 0) {
						EsnListValueObject tesnjjVO = tesnjjdetvec.get(0);

						double tesnjj_wt = tesnjjVO.getGrWt() / Double.parseDouble("" + tesnjjVO.getNoOfCntr())
								* transNbr_int;
						double tesnjj_vol = tesnjjVO.getGrVolume() / Double.parseDouble("" + tesnjjVO.getNoOfCntr())
								* transNbr_int;

						BigDecimal bdtesnjjWt = new BigDecimal(tesnjj_wt).setScale(2, BigDecimal.ROUND_HALF_UP);
						BigDecimal bdtesnjjVol = new BigDecimal(tesnjj_vol).setScale(2, BigDecimal.ROUND_HALF_UP);
						String firsttransdttm = getUAfirsttransdttm(esnnbr, transtype);

						sb.setLength(0);
						paramMap = new HashMap<String, Object>();
						sb.append(
								"INSERT INTO TESN_JP_JP(ESN_ASN_NBR,EDO_ASN_NBR,LD_IND,NBR_PKGS,NOM_WT,NOM_VOL,PAYMENT_MODE, ");
						sb.append("ACCT_NBR,UA_NBR_PKGS,DN_NBR_PKGS,FIRST_TRANS_DTTM) ");
						sb.append(
								"VALUES(:newesnno,:bookingRefNo,:opInd,:transNbr_int,:bdtesnjjWt,:bdtesnjjVol,:payMode, ");
						sb.append(":accNo,:transNbr_int,:transNbr_int,TO_DATE(:firsttransdttm,'DD/MM/YYYY HH24:MI')) ");

						tesnjpjpdetails = sb.toString();
						paramMap.put("newesnno", newesnno);
						paramMap.put("bookingRefNo", tesnjjVO.getBookingRefNo());
						paramMap.put("opInd", tesnjjVO.getOpInd());
						paramMap.put("transNbr_int", transNbr_int);
						paramMap.put("bdtesnjjWt", bdtesnjjWt);
						paramMap.put("bdtesnjjVol", bdtesnjjVol);
						paramMap.put("payMode", tesnjjVO.getPayMode());
						paramMap.put("accNo", tesnjjVO.getAccNo());
						paramMap.put("transNbr_int", transNbr_int);
						paramMap.put("transNbr_int", transNbr_int);
						paramMap.put("firsttransdttm", firsttransdttm);
						paramMap.put("newesnno", newesnno);
						// "'," + tesnjjVO.getUaNoofPkgs() + "," +
						// tesnjjVO.getBNoofPkgs() + ",TO_DATE('" +
						if (transtype.equals("A")) {
							log.info("TransferCrgUpdateForDPE tesnjpjpdetails SQL" + tesnjpjpdetails.toString());
							log.info(" paramMap: " + paramMap);
							tesnjjcnt = namedParameterJdbcTemplate.update(tesnjpjpdetails.toString(), paramMap);
							log.info("TransferCrgUpdateForDPE tesnjpjpdetails SQL :: tesnjjcnt result: " + tesnjjcnt);
							if (tesnjjcnt == 0) {
								log.info("EsnEJB.TransferCrgUpdate tesnjpjpdetails : Record Cannot be added to Database");
								throw new BusinessException("M4201");
							}

						} // if A
					} // if tesnjjdetvec

					
					esnmarkings = getMarkings(esnnbr);

					strMark = "INSERT INTO ESN_MARKINGS(ESN_ASN_NBR,MARKINGS) VALUES (:newesnno,:esnmarkings )";
					if (esnmarkings != null && !esnmarkings.equals("")) {

						paramMap.put("newesnno", newesnno);
						paramMap.put("esnmarkings", esnmarkings);
						log.info("TransferCrgUpdateForDPE strMark SQL" + strMark.toString());
						log.info(" paramMap: " + paramMap);
						esnmarkcnt = namedParameterJdbcTemplate.update(strMark.toString(), paramMap);
						log.info("TransferCrgUpdateForDPE strMark SQL :: esnmarkcnt result: " + esnmarkcnt);
						if (esnmarkcnt == 0) {
							log.info("EsnEJB.TransferCrgUpdate strMark : Record Cannot be added to Database");
							throw new BusinessException("M4201");
						}
					} // if esnmark
					esncntrvec = getCntrDetails(esnnbr);

					if (esncntrvec.size() > 0) {
						for (int j = 0; j < esncntrvec.size(); j++) {
							EsnListValueObject esncntrVO = (EsnListValueObject) esncntrvec.get(0);

							if (esncntrVO.getCntr1() != null && !(esncntrVO.getCntr1()).equals("")
									&& !(esncntrVO.getCntr1()).equals("null") && (esncntrVO.getCntr1()).length() > 0) {
								esncntrsql = "INSERT INTO ESN_CNTR(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES(1,:newesnno,:cntr1";

								paramMap.put("newesnno", newesnno);
								paramMap.put("cntr1", esncntrVO.getCntr1());
								log.info("TransferCrgUpdateForDPE esncntrsql SQL" + esncntrsql.toString());
								log.info(" paramMap: " + paramMap);
								namedParameterJdbcTemplate.update(esncntrsql.toString(), paramMap);
								

							}
							if (esncntrVO.getCntr2() != null && !(esncntrVO.getCntr2()).equals("")
									&& !(esncntrVO.getCntr2()).equals("null") && (esncntrVO.getCntr2()).length() > 0) {
								esncntrsql = "INSERT INTO ESN_CNTR(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES(2,:newesnno,:cntr2)";

								paramMap.put("newesnno", newesnno);
								paramMap.put("cntr2", esncntrVO.getCntr2());
								log.info("TransferCrgUpdateForDPE esncntrsql SQL" + esncntrsql.toString());
								log.info(" paramMap: " + paramMap);
								namedParameterJdbcTemplate.update(esncntrsql.toString(), paramMap);
								
							}
							if (esncntrVO.getCntr3() != null && !(esncntrVO.getCntr3()).equals("")
									&& !(esncntrVO.getCntr3()).equals("null") && (esncntrVO.getCntr3()).length() > 0) {
								esncntrsql = "INSERT INTO ESN_CNTR(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES(3,:newesnno,:cntr3)";

								paramMap.put("newesnno", newesnno);
								paramMap.put("cntr3", esncntrVO.getCntr3());
								log.info("TransferCrgUpdateForDPE esncntrsql SQL" + esncntrsql.toString());
								log.info(" paramMap: " + paramMap);
								namedParameterJdbcTemplate.update(esncntrsql.toString(), paramMap);
							}
							if (esncntrVO.getCntr4() != null && !(esncntrVO.getCntr4()).equals("")
									&& !(esncntrVO.getCntr4()).equals("null") && (esncntrVO.getCntr4()).length() > 0) {
								esncntrsql = "INSERT INTO ESN_CNTR(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES(4,:newesnno,:cntr4)";

								paramMap.put("newesnno", newesnno);
								paramMap.put("cntr4", esncntrVO.getCntr4());
								log.info("TransferCrgUpdateForDPE esncntrsql SQL" + esncntrsql.toString());
								log.info(" paramMap: " + paramMap);
								namedParameterJdbcTemplate.update(esncntrsql.toString(), paramMap);
								
							}
						}
					} // if esncntrvec
				} // if(transNbr_int > 0 && transNbr_int!=balvalue)

				if (bkrNbr_arr != null && !bkrNbr_arr.equals("")) {
					paramMap = new HashMap<String, Object>();
					sql1 = "update ESN_details set FIRST_TRANS_DTTM =to_date(:uaftransdttm,'dd/mm/yyyy hh24:mi') WHERE ESN_ASN_NBR=:esnnbr";
					sql2 = "update tesn_psa_jp set FIRST_TRANS_DTTM =to_date(:uaftransdttm,'dd/mm/yyyy hh24:mi') WHERE ESN_ASN_NBR=:esnnbr";
					sql3 = "update tesn_jp_jp set FIRST_TRANS_DTTM =to_date(:uaftransdttm,'dd/mm/yyyy hh24:mi') WHERE ESN_ASN_NBR=:esnnbr";

					if (transtype != null && !transtype.equals("") && transtype.equals("E")) {
						sqlft = sql1;
					} else if (transtype != null && !transtype.equals("") && transtype.equals("C")) {
						sqlft = sql2;
					} else if (transtype != null && !transtype.equals("") && transtype.equals("A")) {
						sqlft = sql3;
					}

					paramMap.put("uaftransdttm", uaftransdttm);
					paramMap.put("esnnbr", esnnbr);
					log.info("TransferCrgUpdateForDPE sqlft SQL: " + sqlft.toString());
					log.info(" paramMap: " + paramMap);
					int count = namedParameterJdbcTemplate.update(sqlft.toString(), paramMap);
					log.info("TransferCrgUpdateForDPE sqlft SQL :: count : " + count);

				}

				if (transNbr_int > 0 && actnbrshpval == 0 && shutoutqtyval == uanbrpkgsval) {

					sb.setLength(0);
					paramMap = new HashMap<String, Object>();
					sb.append(
							"UPDATE bk_details SET VAR_NBR=:varnoT,OUT_VOY_NBR=:Toutvoynbr,BK_ORIGINAL_VAR_NBR=:varnoF, ");
					sb.append("LAST_MODIFY_USER_ID=:UserID,LAST_MODIFY_DTTM=SYSDATE,TRANSFER_DTTM=sysdate, ");
					sb.append("TRANSFER_PKGS = TRANSFER_PKGS +:transNbr_int,TRANS_CRG='Y',GB_CLOSE_SHP_IND = 'N' ");
					sb.append("WHERE BK_REF_NBR=:bkrNbr_arr AND VAR_NBR=:varnoF");
					sqlUpdate = sb.toString();

					sb.setLength(0);
					paramMap = new HashMap<String, Object>();
					sb.append("UPDATE ESN SET OUT_VOY_VAR_NBR =:varnoT,LAST_MODIFY_USER_ID=:UserID, ");
					sb.append("LAST_MODIFY_DTTM = SYSDATE,TRANS_CRG='Y',BILL_SERVICE_TRIGGERED_IND='N', ");
					sb.append(
							"BILL_WHARF_TRIGGERED_IND='N',BILL_STORE_TRIGGERED_IND='N',BILL_IM_SERVICE_TRIGGERED_IND='N', ");
					sb.append("BILL_IM_WHARF_TRIGGERED_IND='N',BILL_IM_STORE_TRIGGERED_IND='N' ");
					sb.append("WHERE BK_REF_NBR = :bkrNbr_arr AND OUT_VOY_VAR_NBR = :varnoF");
					sqlUpdate_esn = sb.toString();

					paramMap.put("varnoT", varnoT);
					paramMap.put("Toutvoynbr", Toutvoynbr);
					paramMap.put("varnoF", varnoF);
					paramMap.put("UserID", UserID);
					paramMap.put("transNbr_int", transNbr_int);
					paramMap.put("bkrNbr_arr", bkrNbr_arr);
					
					log.info("TransferCrgUpdateForDPE sqlUpdate SQL" + sqlUpdate.toString());
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(sqlUpdate.toString(), paramMap);
					

					paramMap.put("varnoT", varnoT);
					paramMap.put("UserID", UserID);
					paramMap.put("bkrNbr_arr", bkrNbr_arr);
					paramMap.put("varnoF", varnoF);

					log.info("TransferCrgUpdateForDPE sqlUpdate_esn SQL" + sqlUpdate_esn.toString());
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(sqlUpdate_esn.toString(), paramMap);
					

				} // if (transNbr_int > 0 && actnbrshpval==0 && shutoutqtyval==uanbrpkgsval)

				String tempbk = "";

				if (transNbr_int > 0 && actnbrshpval > 0 && shutoutqtyval != uanbrpkgsval)
					tempbk = newbknbr;
				else if (transNbr_int > 0 && actnbrshpval == 0 && shutoutqtyval == uanbrpkgsval)
					tempbk = bkrNbr_arr;

				sb.setLength(0);
				paramMap = new HashMap<String, Object>();
				sb.append(
						"SELECT BK.BK_REF_NBR AS BKRNBR,BK.SHUTOUT_QTY AS SHUTOUTQTY,BK.ACTUAL_NBR_SHIPPED,BK.TRANSFER_PKGS AS tsfnbr,E.TRANS_TYPE,NVL(ED.UA_NBR_PKGS,0) AS nbr,NVL(TESNJPJP.UA_NBR_PKGS,0) AS jpjpnbr,");
				sb.append(
						"NVL(TESNPSAJP.UA_NBR_PKGS,0) AS psajpnbr,E.ESN_ASN_NBR FROM BK_DETAILS BK,ESN E,ESN_DETAILS ED,TESN_JP_JP TESNJPJP,TESN_PSA_JP TESNPSAJP ");
				sb.append(
						"WHERE BK.BK_REF_NBR = E.BK_REF_NBR AND E.ESN_ASN_NBR = ED.ESN_ASN_NBR(+) AND TESNJPJP.ESN_ASN_NBR(+)= E.ESN_ASN_NBR ");
				sb.append(
						"AND TESNPSAJP.ESN_ASN_NBR(+)= E.ESN_ASN_NBR AND BK.BK_STATUS = 'A' AND E.ESN_STATUS = 'A' AND E.TRANS_TYPE IN('A','E','C') ");
				sb.append("AND BK.BK_REF_NBR =:tempbk AND BK.VAR_NBR=:varnoT ");

				sql = sb.toString();
				paramMap.put("tempbk", tempbk);
				paramMap.put("varnoT", varnoT);
				log.info("TransferCrgUpdateForDPE sql SQL" + sql.toString());
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

				while (rs.next()) {
					String bkrNbr = CommonUtility.deNull(rs.getString("BKRNBR"));
					String shutoutqnty = CommonUtility.deNull(rs.getString("SHUTOUTQTY"));
					String acctNbr = CommonUtility.deNull(rs.getString("ACTUAL_NBR_SHIPPED"));

					String TrnsNbr = "" + Math.round(transNbr_int);
					String trans_type = CommonUtility.deNull(rs.getString("TRANS_TYPE"));
					int esn_nbr_pkgs = rs.getInt(6);
					int jpjp_nbr_pkgs = rs.getInt(7);
					int psajp_nbr_pkgs = rs.getInt(8);
					int nbr_pkgs = 0;
					long esnnum = rs.getLong(9);

					if (trans_type.equals("E")) {
						nbr_pkgs = esn_nbr_pkgs;
					} else if (trans_type.equals("A")) {
						nbr_pkgs = jpjp_nbr_pkgs;
					} else if (trans_type.equals("C")) {
						nbr_pkgs = psajp_nbr_pkgs;
					}

					esnListValueObject = new EsnListValueObject();
					esnListValueObject.setBookingRefNo(bkrNbr);
					esnListValueObject.setCrgType(shutoutqnty);
					esnListValueObject.setAccNo(acctNbr);
					esnListValueObject.setBNoofPkgs(Integer.parseInt(TrnsNbr));
					esnListValueObject.setNoofPkgs(nbr_pkgs);
					esnListValueObject.setEsnNbr(esnnum);
					vc1.add(esnListValueObject);
				} // while(rs.next())
			} // end of for loop

		} catch (NullPointerException ne) {
			log.info("Exception TransferCrgUpdateForDPE : ", ne);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception TransferCrgUpdateForDPE : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception TransferCrgUpdateForDPE : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** TransferCrgUpdateForDPE  END  vc1.size: " + vc1.size());
		}
		return vc1;
	}

	// jp.src.ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->getVesselListSearch()
	@Override
	public List<VesselVoyValueObject> getVesselListSearch(String custId, String esnNbr)
			throws BusinessException, RemoteException {
		String custCd = custId;
		String esnNo = esnNbr;
		StringBuilder sb = new StringBuilder();
		boolean isShowEsnInfo = false;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			TextParaVO code = new TextParaVO();
			code.setParaCode(TEXT_PARA_GC_VIEW_ESN);
			TextParaVO result = textParaRepo.getParaCodeInfo(code);
			isShowEsnInfo = isShowEsnInfo(custId, result);
		} catch (Exception e) {
			log.info("Exception getVesselListSearch : ", e);
		}

		if (isShowEsnInfo) {
			sb.append("select distinct VV_CD,VSL_NM,TERMINAL,OUT_VOY_NBR ");
			sb.append("from esn e, vessel_call ves ");
			sb.append(" where e.trans_type='E' and e.esn_status = 'A' and e.out_voy_var_nbr = ves.vv_cd ");
			sb.append("AND nvl(ves.GB_CLOSE_SHP_IND,'N') !='Y' and e.esn_asn_nbr =:esnNo");
		} else {
			sb.append("select distinct VV_CD,VSL_NM,TERMINAL,OUT_VOY_NBR ");
			sb.append("from esn e, vessel_call ves where e.trans_type='E' ");
			sb.append(" and (e.ESN_CREATE_CD =:custCd");
			sb.append(" OR ves.CREATE_CUST_CD =:custCd ");
			sb.append(" )" + " and e.esn_status = 'A' and e.out_voy_var_nbr = ves.vv_cd ");
			sb.append(" AND nvl(ves.GB_CLOSE_SHP_IND,'N') !='Y' and e.esn_asn_nbr =:esnNo");
		}
		List<VesselVoyValueObject> vesselList = new ArrayList<VesselVoyValueObject>();
		VesselVoyValueObject vesselVoyValueObject = null;

		try {
			log.info("START: getBVesselListSearch Dao Start custId:" + CommonUtility.deNull(custId) + "esnNo:" + CommonUtility.deNull(esnNo));
			paramMap.put("esnNo", esnNo);
			if (!isShowEsnInfo) {
				paramMap.put("custCd", custCd);
			}
			log.info("getVesselListSearch SQL: " + sb.toString());
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				vesselVoyValueObject = new VesselVoyValueObject();
				vesselVoyValueObject.setVoyNo(CommonUtility.deNull(rs.getString("OUT_VOY_NBR")));
				vesselVoyValueObject.setVslName(CommonUtility.deNull(rs.getString("VSL_NM")));
				vesselVoyValueObject.setVarNbr(CommonUtility.deNull(rs.getString("VV_CD")));
				vesselVoyValueObject.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				vesselList.add(vesselVoyValueObject);
			}
		} catch (Exception se) {
			log.info("Exception getVesselListSearch : ", se);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselListSearch DAO  vesselList.size: " + vesselList.size());
		}
		return vesselList;
	}

	// jp.src.ejb.sessionBeans.gbms.cargo.esn -->EsnEJB -->isShowEsnInfo()
	private boolean isShowEsnInfo(String companyCode, TextParaVO result) {
		boolean showInfo = false;
		try {
			log.info("START: isShowEsnInfo DAO  companyCode: " + CommonUtility.deNull(companyCode) + " result " 
					+ CommonUtility.deNull(result.toString()));
			if (result != null && result.getValue() != null && !"".equals(result.getValue())) {
				String[] textArr = result.getValue().split("/");
				String text = "";
				if (textArr != null && textArr.length > 0) {
					for (int i = 0; i < textArr.length; i++) {
						text = textArr[i];
						if (text != null && text.equals(companyCode)) {
							showInfo = true;
						}
					}
				}
			} else {
				showInfo = false;
			}
		} finally {
			log.info("END: isShowEsnInfo DAO  showInfo: " + showInfo);
		}
		return showInfo;

	}

	// jp.src.ejb.sessionBeans.gbms.cargo.esn-->EsnEJB--->getTruckerList()
	@Override
	public List<TruckerValueObject> getTruckerList(String esnNbr) throws BusinessException {
		SqlRowSet rs = null;
		String sql = "";
		List<TruckerValueObject> truckerList = new ArrayList<TruckerValueObject>();
		TruckerValueObject truckerValueObject = new TruckerValueObject();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getTruckerList Dao Start esnNbr:" + CommonUtility.deNull(esnNbr));
			sql = "select * from SUB_ADP where esn_asn_nbr =:esnNbr and edo_esn_ind = 0 and status_cd = 'A' ";
			paramMap.put("esnNbr", esnNbr);
			log.info("getTruckerList SQL: " + sql.toString());
			log.info("getTruckerList paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				truckerValueObject = new TruckerValueObject();
				truckerValueObject.setTruckerIc(CommonUtility.deNull(rs.getString("TRUCKER_IC")));
				truckerValueObject.setTruckerNm(CommonUtility.deNull(rs.getString("TRUCKER_NM")));
				truckerValueObject.setTruckerCd(CommonUtility.deNull(rs.getString("TRUCKER_CO_CD")));
				truckerValueObject.setTruckerContact(CommonUtility.deNull(rs.getString("TRUCKER_CONTACT_NBR")));
				truckerValueObject.setTruckerPkgs(CommonUtility.deNull(rs.getString("TRUCKER_NBR_PKGS")));
				truckerList.add(truckerValueObject);
			}
		} catch (Exception e) {
			log.info("Exception getTruckerList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTruckerList DAO  truckerList: " + truckerList.size());
		}
		return truckerList;
	}

	// jp.src.ejb.sessionBeans.gbms.cargo.esn-->EsnEJB--->getAccNo()
	@Override
	public List<EsnListValueObject> getAccNo(String truckerIcNo) throws BusinessException {
		SqlRowSet rs = null;
		String truckerCd = "";
		String tdbcrNo = truckerIcNo;
		StringBuilder sb = new StringBuilder();
		List<EsnListValueObject> accNoList = new ArrayList<EsnListValueObject>();
		EsnListValueObject esnListValueObject = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getAccNo Dao Start truckerIcNo:" + CommonUtility.deNull(truckerIcNo));
			truckerCd = getCustId(tdbcrNo);
			sb.append(
					" select ACCT_NBR,company_code.CO_NM from cust_acct,company_code where cust_acct.cust_cd = company_code.co_cd ");
			sb.append(" and upper(cust_acct.cust_cd)=:truckerCd and acct_status_cd='A' ");
			sb.append(" and business_type like '%G%' and trial_ind='N' and ACCT_NBR is not null ORDER BY ACCT_NBR ");
			paramMap.put("truckerCd", truckerCd);
			log.info("getAccNo SQL: " + sb.toString());
			log.info("paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				esnListValueObject = new EsnListValueObject();
				esnListValueObject.setAccNo(CommonUtility.deNull(rs.getString("ACCT_NBR")));
				esnListValueObject.setCompanyName(CommonUtility.deNull(rs.getString("CO_NM")));
				accNoList.add(esnListValueObject);
			}
		} catch (Exception e) {
			log.info("Exception getAccNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAccNo DAO  accNoList: " + accNoList.size());
		}
		return accNoList;
	}

	// jp.src.ejb.sessionBeans.gbms.cargo.esn-->EsnEJB--->getCustId()
	public String getCustId(String tdbcrNo) throws BusinessException, RemoteException {
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String truckerCd = tdbcrNo;
		String custCd = "";
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getCustId Dao Start tdbcrNo:" + CommonUtility.deNull(tdbcrNo));

			sb.append(" select cust_cd from customer where upper(tdb_cr_nbr) =:tdb_cr_nbr ");
			sb.append(" OR upper(uen_nbr) =:uen_nbr ");
			// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
			paramMap.put("tdb_cr_nbr", (truckerCd));
			paramMap.put("uen_nbr", (truckerCd));
			log.info("getCustId SQL: " + sb.toString());
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				custCd = CommonUtility.deNull(rs.getString("cust_cd"));
			}
		} catch (Exception e) {
			log.info("Exception getCustId : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCustId DAO  custCd: " + CommonUtility.deNull(custCd));
		}
		return custCd;
	}

	// jp.src.ejb.sessionBeans.gbms.cargo.esn-->EsnEJB--->getUserAccNo()
	@Override
	public List<EsnListValueObject> getUserAccNo(String bookingRfnbr, String custId, String accNbr)
			throws BusinessException {
		SqlRowSet rs = null;
		String bookingRefNo = bookingRfnbr;
		String accNo = accNbr;
		StringBuilder sb = new StringBuilder();
		String esnDeclrCd = getDeclarant(bookingRefNo);
		List<EsnListValueObject> UserAccNo = new ArrayList<EsnListValueObject>();
		Map<String, String> paramMap = new HashMap<String, String>();
		EsnListValueObject esnListValueObject = null;
		try {
			log.info("START: getUserAccNo Dao Start bookingRfnbr:" + CommonUtility.deNull(bookingRfnbr) + ", custId" 
					+ CommonUtility.deNull(custId) + ", accNbr" + CommonUtility.deNull(accNbr));
			sb.append(
					" select ACCT_NBR,company_code.CO_NM from cust_acct,company_code where cust_acct.cust_cd = company_code.co_cd ");
			sb.append(
					" and cust_acct.cust_cd =:esnDeclrCd and cust_acct.acct_status_cd='A' and cust_acct.business_type like '%G%' ");
			sb.append(
					" and cust_acct.trial_ind='N' and cust_acct.ACCT_NBR is not null and cust_acct.ACCT_NBR !=:accNo ");
			paramMap.put("esnDeclrCd", esnDeclrCd);
			paramMap.put("accNo", accNo);
			log.info("getUserAccNo SQL" + sb.toString());
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				esnListValueObject = new EsnListValueObject();
				esnListValueObject.setAccNo(CommonUtility.deNull(rs.getString("ACCT_NBR")));
				esnListValueObject.setCompanyName(CommonUtility.deNull(rs.getString("CO_NM")));
				UserAccNo.add(esnListValueObject);
			}
		} catch (Exception e) {
			log.info("Exception getAccNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAccNo DAO  UserAccNo: " + UserAccNo.size());
		}
		return UserAccNo;
	}

	// jp.src.ejb.sessionBeans.gbms.cargo.esn-->EsnEJB--->getDeclarant()
	public String getDeclarant(String bkrefnbr) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String bookingRefNo = bkrefnbr;
		String sql = "";
		String custId = "";
		try {
			log.info("START: getDeclarant Dao Start bkrefnbr:" + CommonUtility.deNull(bkrefnbr));
			sql = "select DECLARANT_CD from bk_details where bk_ref_nbr =:bookingRefNo ";

			paramMap.put("bookingRefNo", bookingRefNo);
			log.info("getDeclarant SQL" + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				custId = CommonUtility.deNull(rs.getString("DECLARANT_CD"));
			}
		} catch (Exception e) {
			log.info("Exception getDeclarant : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getDeclarant DAO  custId: " + CommonUtility.deNull(custId));
		}
		return custId;
	}

	// jp.src.ejb.sessionBeans.gbms.cargo.esn-->EsnEJB--->TruckerValueObject()
	@Override
	public TruckerValueObject getTruckerDetails(String truckerIc) throws BusinessException {
		SqlRowSet rs = null;
		SqlRowSet rs2 = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		TruckerValueObject truckerValueObject = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info(" START: getTruckerDetails Dao Start truckerIc:" + CommonUtility.deNull(truckerIc));
			sb.append(
					" SELECT CO_NM, cust.cust_cd as CO_CD, NVL(PHONE1_NBR, PHONE2_NBR) PHONE1_NBR, ADD_L1 FROM customer cust ");
			sb.append(" LEFT JOIN company_code cc on cust.cust_cd = cc.co_cd ");
			sb.append(" LEFT JOIN cust_contact ct on cust.cust_cd = ct.CUST_CD ");
			sb.append(" LEFT JOIN cust_address ca on cust.cust_cd = ca.cust_cd ");
			sb.append(" WHERE (TDB_CR_NBR =:truckerIc  or UEN_NBR =:truckerIc ");
			sb.append(
					" ) AND cc.rec_status = 'A' order by cc.rec_status desc, (case when cc.lob_cd = 'HAU' then 2 else 1 end)");

			sb2.append("SELECT DISTINCT(JC.CUST_NAME) CO_NM, ");
			sb2.append("JC.CUST_CD CO_CD, ");
			sb2.append("NVL(CT.PHONE1_NBR, CT.PHONE2_NBR) PHONE1_NBR, ");
			sb2.append("CA.ADD_L1 ");
			sb2.append("FROM JC_CARDDTL JC ");
			sb2.append("LEFT JOIN CUST_CONTACT CT ON JC.CUST_CD = CT.CUST_CD ");
			sb2.append("LEFT JOIN CUST_ADDRESS CA ON JC.CUST_CD = CA.CUST_CD ");
			sb2.append(
					" WHERE UPPER(jc.passport_no) =:truckerIc OR UPPER(jc.NRIC_NO) =:truckerIc OR UPPER(jc.FIN_NO) =:truckerIc ");

			paramMap.put("truckerIc", truckerIc);
			log.info("getTruckerDetails SQL 1" + sb.toString());
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			truckerValueObject = new TruckerValueObject();
			truckerValueObject.setTruckerIc(truckerIc);
			if (rs.next()) {
				truckerValueObject.setTruckerNm(CommonUtility.deNull(rs.getString("CO_NM")));
				truckerValueObject.setTruckerContact(CommonUtility.deNull(rs.getString("PHONE1_NBR")));
				truckerValueObject.setTruckerAdd(CommonUtility.deNull(rs.getString("ADD_L1")));
				truckerValueObject.setTruckerCd(CommonUtility.deNull(rs.getString("CO_CD")));
			} else {
				paramMap.put("truckerIc", truckerIc);
				log.info("getTruckerDetails SQL 2" + sb2.toString());
				rs2 = namedParameterJdbcTemplate.queryForRowSet(sb2.toString(), paramMap);
				if (rs2.next()) {
					truckerValueObject.setTruckerNm(CommonUtility.deNull(rs2.getString("CO_NM")));
					truckerValueObject.setTruckerContact(CommonUtility.deNull(rs2.getString("PHONE1_NBR")));
					truckerValueObject.setTruckerAdd(CommonUtility.deNull(rs2.getString("ADD_L1")));
					truckerValueObject.setTruckerCd(CommonUtility.deNull(rs2.getString("CO_CD")));
				}
			}
		} catch (Exception e) {
			log.info("Exception getTruckerDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTruckerDetails DAO  truckerValueObject: " + truckerValueObject.toString());
		}
		return truckerValueObject;
	}

	// jp.src.ejb.sessionBeans.gbms.cargo.esn-->EsnEJB--->checkValidTrucker()
	@Override
	public boolean checkValidTrucker(String truckerIcNo) throws BusinessException {
		SqlRowSet rs = null;
		SqlRowSet rs2 = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		boolean isValid = false;
		int count = 0;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info(" START: checkValidTrucker Dao Start truckerIc:" + CommonUtility.deNull(truckerIcNo));
			sb.append(" SELECT count(*) as COUNT FROM COMPANY_CODE A, CUSTOMER B ");
			sb.append(" WHERE A.CO_CD=B.CUST_CD ");
			sb.append(" AND (UPPER(B.TDB_CR_NBR)=UPPER(:truckerIcNo) ");
			sb.append(" OR UPPER(B.UEN_NBR)= UPPER(:truckerIcNo))");

			sb2.append("SELECT count(*) as COUNT ");
			sb2.append("FROM JC_CARDDTL ");
			sb2.append("WHERE UPPER(PASSPORT_NO)=:truckerIcNo ");

			paramMap.put("truckerIcNo", truckerIcNo);
			log.info("checkValidTrucker SQL" + sb.toString());
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				count = Integer.parseInt(CommonUtility.deNull(rs.getString("COUNT")));
			}
			if (count < 1) {
				rs2 = namedParameterJdbcTemplate.queryForRowSet(sb2.toString(), paramMap);
				if (rs2.next()) {
					count = Integer.parseInt(CommonUtility.deNull(rs2.getString("COUNT")));
				}
			}
			log.info("Enhanment HSCode from FPT in getHSSubCodeDes : count: " + count);
			if (count < 1) {
				isValid = false;
			} else {
				isValid = true;
			}
		} catch (Exception e) {
			log.info("Exception checkValidTrucker : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkValidTrucker DAO  isValid: " + isValid);
		}
		return isValid;
	}

	// jp.src.ejb.sessionBeans.gbms.cargo.esn-->EsnEJB--->chkNoOfPkgs()
	@Override
	public boolean chkNoOfPkgs(String bookRefNo, int noOfpk) throws BusinessException, RemoteException {
		String bookingRefNo = bookRefNo;
		int noOfPkgs = noOfpk;
		double varientPkgs = 0;
		double allowedPkgs = 0;
		String sql = "";
		SqlRowSet rs = null;
		boolean result = true;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START chkNoOfPkgs DAO.  bookRefNo: " + CommonUtility.deNull(bookRefNo) + " , noOfpk: " 
					+ CommonUtility.deNull(String.valueOf(noOfpk)));
			sql = "SELECT BK_NBR_PKGS,VARIANCE_PKGS FROM bk_details WHERE BK_REF_NBR =:bookingRefNo ";
			paramMap.put("bookingRefNo", bookingRefNo);
			log.info("chkNoOfPkgs SQL: " + sql);
			log.info(" paramMap: " + paramMap);
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
		} catch (Exception se) {
			log.info("Exception chkNoOfPkgs : ", se);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkNoOfPkgs DAO  result: " + result);
		}
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.cargo.esn-->EsnEJB--->isOutWardPm()
	@Override
	public boolean isOutWardPm(String bkRefNo, String vvCd) throws BusinessException {
		SqlRowSet rs = null;
		boolean exists = false;
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START isOutWardPm DAO bkRefNo: " + CommonUtility.deNull(bkRefNo) + " , vvCd: " + CommonUtility.deNull(vvCd));
			sb.append(" select mpa_appv_status,jp_appv_status,vv_cd,UCR_NBR from pm4 ");
			sb.append(" where (ucr_nbr=:bkRefNo) and (vv_cd=:vvCd) ");
			sb.append(" and (opr_type='L') and (RECORD_TYPE<>'D')");
			paramMap.put("bkRefNo", bkRefNo);
			paramMap.put("vvCd", vvCd);
			log.info("isOutWardPm SQL: " + sb.toString());
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			String mpaAppvStatus = "";
			String jpAppvStatus = "";
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
		} catch (Exception exception) {
			log.info("Exception isOutWardPm : ", exception);
			throw new BusinessException("M4201");
		} finally {
			log.info("END isOutWardPm DAO  exists: " + exists);
		}
		return exists;
	}

	// jp.src.ejb.sessionBeans.gbms.cargo.esn-->EsnEJB--->chkPkgsType()
	@Override
	public boolean chkPkgsType(String pkgs_Type) throws BusinessException {
		String pkgsType = pkgs_Type;
		String sql;
		SqlRowSet rs = null;
		boolean result = true;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START chkPkgsType DAO.  pkgs_Type: " + CommonUtility.deNull(pkgs_Type));
			sql = "SELECT PKG_TYPE_CD FROM PKG_TYPES WHERE PKG_TYPE_CD =:pkgsType AND REC_STATUS='A'";
			paramMap.put("pkgsType", pkgsType);
			log.info("chkPkgsType SQL: " + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next())
				result = true;
			else
				result = false;
		} catch (Exception e) {
			log.info("Exception chkPkgsType : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END chkPkgsType DAO  result: " + result);
		}
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.cargo.esn-->EsnEJB--->chkWeight()
	@Override
	public boolean chkWeight(String bookRefNo, double weight_s) throws BusinessException, RemoteException {
		String bookingRefNo = bookRefNo;
		double weight = weight_s;
		double varientWeight = 0;
		double allowedWeight = 0;
		String sql;
		SqlRowSet rs = null;
		boolean result = true;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("END chkWeight DAO.  bookRefNo: " + CommonUtility.deNull(bookRefNo) + " , weight_s: " + CommonUtility.deNull(String.valueOf(weight_s)));
			sql = "SELECT BK_WT,VARIANCE_WT FROM bk_details WHERE BK_REF_NBR =:bookingRefNo ";
			paramMap.put("bookingRefNo", bookingRefNo);
			log.info("chkWeight SQL: " + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			BigDecimal wt = null;
			BigDecimal wt1 = null;
			BigDecimal wt2 = null;
			BigDecimal wt3 = null;

			if (rs.next()) {
				varientWeight = rs.getDouble("VARIANCE_WT") / 100;
				allowedWeight = (varientWeight + 1) * (rs.getDouble("BK_WT"));
				wt = new BigDecimal(allowedWeight);
				wt1 = wt.setScale(2, BigDecimal.ROUND_HALF_UP);
				wt2 = new BigDecimal(weight);
				wt3 = wt2.setScale(2, BigDecimal.ROUND_HALF_UP);
				if (wt3.doubleValue() > wt1.doubleValue())
					result = false;
				else
					result = true;
			} else
				result = false;
		} catch (Exception e) {
			log.info("Exception chkWeight : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END chkWeight DAO   result: " + result);
		}
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.cargo.esn-->EsnEJB--->chkVolume()
	@Override
	public boolean chkVolume(String bookRefNo, double volume_s) throws BusinessException, RemoteException {
		String bookingRefNo = bookRefNo;
		double volume = volume_s;
		double varientVolume = 0;
		double allowedVolume = 0;
		String sql;
		SqlRowSet rs = null;
		boolean result = true;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START chkVolume DAO.  bookRefNo: " + CommonUtility.deNull(bookRefNo) + " , volume_s" + CommonUtility.deNull(String.valueOf(volume_s)));
			sql = "SELECT BK_VOL,VARIANCE_VOL FROM bk_details WHERE BK_REF_NBR =:bookingRefNo";
			paramMap.put("bookingRefNo", bookingRefNo);
			log.info("chkVolume SQL: " + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			BigDecimal vl = null;
			BigDecimal vl1 = null;
			BigDecimal vl2 = null;
			BigDecimal vl3 = null;

			if (rs.next()) {
				varientVolume = rs.getDouble("VARIANCE_VOL") / 100;
				allowedVolume = (varientVolume + 1) * (rs.getDouble("BK_VOL"));
				vl = new BigDecimal(allowedVolume);
				vl1 = vl.setScale(2, BigDecimal.ROUND_HALF_UP);
				vl2 = new BigDecimal(volume);
				vl3 = vl2.setScale(2, BigDecimal.ROUND_HALF_UP);

				if (vl3.doubleValue() > vl1.doubleValue())
					result = false;
				else
					result = true;
			} else
				result = false;
		} catch (Exception e) {
			log.info("Exception chkVolume : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END chkVolume DAO   result: " + result);
		}
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.cargo.esn-->EsnEJB--->esnUpdateForDPE()
	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String esnUpdateForDPE(int noOfPkgs, String hscd, String pkgsType, String mark, String truckerName,
			String truckerNo, String lopInd, String dgInd, String stgInd, String loadingFrom, String poD,
			int noOfStorageDay, String dutiInt, String payMode, String accNo, String esnNbr, String cargoDes,
			String truckerCd, double weight, double volume, String truckerCNo, String cntr1, String cntr2, String cntr3,
			String cntr4, String stfInd, String strUAFlag, String strUserID, String category, String hsSubCodeFr,
			String hsSubCodeTo, List<TruckerValueObject> truckerList, int truckerNbrPkgs, String custCd)
			throws BusinessException {

		String customHsCode = ""; 
		List<HsCodeDetails> multiHsCodeList = new ArrayList<>();
		
		return esnUpdateForDPE(noOfPkgs, hscd, pkgsType, mark, truckerName, truckerNo, lopInd, dgInd, stgInd,
				loadingFrom, poD, noOfStorageDay, dutiInt, payMode, accNo, esnNbr, cargoDes, truckerCd, weight, volume,
				truckerCNo, cntr1, cntr2, cntr3, cntr4, stfInd, strUAFlag, strUserID, category, hsSubCodeFr,
				hsSubCodeTo, truckerList, truckerNbrPkgs, custCd, "", "", "", customHsCode, multiHsCodeList);
	}

	public String esnUpdateForDPE(int noOfPkgs, String hscd, String pkgsType, String mark, String truckerName,
			String truckerNo, String lopInd, String dgInd, String stgInd, String loadingFrom, String poD,
			int noOfStorageDay, String dutiInt, String payMode, String accNo, String esnNbr, String cargoDes,
			String truckerCd, double weight, double volume, String truckerCNo, String cntr1, String cntr2, String cntr3,
			String cntr4, String stfInd, String strUAFlag, String strUserID, String category, String hsSubCodeFr,
			String hsSubCodeTo, List<TruckerValueObject> truckerList, int truckerNbrPkgs, String custCd,
			String deliveryToEPC, String cntrSeqNbr, String miscAppNo, String customHsCode, List<HsCodeDetails> multiHsCodeList) throws BusinessException, RemoteException { // MCC
			// esn																										// added
																													// deliveryToEPC
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String strUpdate = new String();
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		String strMark = new String();
		String esnNo = esnNbr;

		String sqlTrans = "";
		String strInsertTrans = new String();
		String strMarkTrans = new String();
		String strCntr1Trans = new String();
		String strCntr2Trans = new String();
		String strCntr3Trans = new String();
		String strCntr4Trans = new String();
		String strEsnDetailsTrans = new String();
		String transNumEsnStr = "";
		String strESNUpdate = "";
		String strTransNbr = "";
		String strMarkTransNbr = "";
		int transNumEsnInt = 0;

		String strCntr1 = new String();
		String strCntr2 = new String();
		String strCntr3 = new String();
		String strCntr4 = new String();
		try {
			log.info("START: esnUpdateForDPE. noOfPkgs: " + CommonUtility.deNull(String.valueOf(noOfPkgs)) + " , hscd: "
					+ CommonUtility.deNull(hscd) + " , pkgsType: " + CommonUtility.deNull(pkgsType) + " , mark: "
					+ CommonUtility.deNull(mark) + " , truckerName: " + CommonUtility.deNull(truckerName)
					+ " , truckerNo: " + CommonUtility.deNull(truckerNo) + " , lopInd: " + CommonUtility.deNull(lopInd)
					+ " , dgInd: " + CommonUtility.deNull(dgInd) + " , stgInd: " + CommonUtility.deNull(stgInd)
					+ " , loadingFrom: " + CommonUtility.deNull(loadingFrom) + " , poD: " + CommonUtility.deNull(poD)
					+ " , noOfStorageDay: " + CommonUtility.deNull(String.valueOf(noOfStorageDay)) + " , dutiInt: "
					+ CommonUtility.deNull(dutiInt) + " , payMode: " + CommonUtility.deNull(payMode) + " , accNo: "
					+ CommonUtility.deNull(accNo) + " , esnNbr: " + CommonUtility.deNull(esnNbr) + " , cargoDes: "
					+ CommonUtility.deNull(cargoDes) + " , truckerCd: " + CommonUtility.deNull(truckerCd)
					+ " , weight: " + CommonUtility.deNull(String.valueOf(weight)) + " , volume: "
					+ CommonUtility.deNull(String.valueOf(volume)) + " , truckerCNo: "
					+ CommonUtility.deNull(truckerCNo) + " , cntr1: " + CommonUtility.deNull(cntr1) + " , cntr2: "
					+ CommonUtility.deNull(cntr2) + " , cntr3: " + CommonUtility.deNull(cntr3) + " , cntr4: "
					+ CommonUtility.deNull(cntr4) + " , stfInd: " + CommonUtility.deNull(stfInd) + " , strUAFlag: "
					+ CommonUtility.deNull(strUAFlag) + " , strUserID: " + CommonUtility.deNull(strUserID)
					+ " , category: " + CommonUtility.deNull(category) + " , hsSubCodeFr: "
					+ CommonUtility.deNull(hsSubCodeFr) + " , hsSubCodeTo: " + CommonUtility.deNull(hsSubCodeTo)
					+ " , truckerList: " + CommonUtility.deNull(truckerList.toString()) + " , truckerNbrPkgs: "
					+ CommonUtility.deNull(String.valueOf(truckerNbrPkgs)) + " , custCd: "
					+ CommonUtility.deNull(custCd) + " , deliveryToEPC: " + CommonUtility.deNull(deliveryToEPC)
					+ " , cntrSeqNbr: " + CommonUtility.deNull(cntrSeqNbr) + " , miscAppNo: "
					+ CommonUtility.deNull(miscAppNo));
			sb.append("UPDATE esn SET stuff_ind=:stfInd, ");
			sb.append("LAST_MODIFY_DTTM=SYSDATE, LAST_MODIFY_USER_ID=:strUserID, ");
			sb.append("CARGO_CATEGORY_CD =:category, CNTR_SEQ_NBR =:cntrSeqNbr, ");
			sb.append("MISC_APP_NBR =:miscAppNo");
			if (deliveryToEPC != null && !deliveryToEPC.equalsIgnoreCase("null") && deliveryToEPC.trim().length() > 0) {
				sb.append(",EPC_IND=:deliveryToEPC ");
			}
			sb.append(" WHERE esn_asn_nbr =:esnNo ");
			strESNUpdate = sb.toString();

			sb.setLength(0);
			sb.append("UPDATE esn_details SET NBR_PKGS=:noOfPkgs,");
			sb.append("ESN_HS_CODE=:hscd,HS_SUB_CODE_FR=:hsSubCdFr,");
			sb.append("HS_SUB_CODE_TO=:hsSubCdTo,PKG_TYPE=:pkgsType, CUSTOM_HS_CODE = :customHsCode, ");
			sb.append("TRUCKER_NM=:trckrNm, ");
			sb.append("TRUCKER_NBR_PKGS=:truckerNbrPkgs, TRUCKER_IC=:trckIc,");
			sb.append("ESN_OPS_IND=:lopInd,ESN_DG_IND=:dgInd,");
			sb.append("STG_IND=:stgInd,ESN_LOAD_FROM=:loadingFrom,");
			sb.append("ESN_PORT_DIS=:poD,STG_DAYS=:noOfStorageDay,");
			sb.append("ESN_DUTY_GOOD_IND=:dutiInt,PAYMENT_MODE=:payMode,");
			sb.append("ACCT_NBR=:accNbr,");
			sb.append("CRG_DES=:crgDes,");
			sb.append("HA_CUST_CD=:truckerCd,TRUCKER_PHONE_NBR=:truckerCNo,");
			sb.append("ESN_WT=:weight,ESN_VOL=:volume WHERE ");
			sb.append("ESN_ASN_NBR=:esnNo");
			strUpdate = sb.toString();

			strMark = "UPDATE esn_markings SET MARKINGS=:marking WHERE ESN_ASN_NBR=:esnNo ";
			sqlTrans = "SELECT MAX(TRANS_NBR) FROM ESN_DETAILS_TRANS WHERE ESN_ASN_NBR =:esnNo ";
			paramMap.put("esnNo", esnNo);
			if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y") {
				log.info("esnUpdateForDPE sqlTrans SQL 1: " + sqlTrans);
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

				sb.setLength(0);
				sb.append(
						"INSERT INTO ESN_DETAILS_Trans(ESN_ASN_NBR,TRANS_NBR,ESN_OPS_IND,ESN_LOAD_FROM,ESN_DG_IND,ESN_HS_CODE, ");
				sb.append("ESN_DUTY_GOOD_IND,TRUCKER_NM,TRUCKER_IC, ");
				sb.append("STG_DAYS,STG_IND,NBR_PKGS,ESN_WT,ESN_VOL,ACCT_NBR,CRG_DES,TRUCKER_PHONE_NBR,HA_CUST_CD, ");
				sb.append("LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,PAYMENT_MODE,PKG_TYPE, CUSTOM_HS_CODE) ");
				sb.append("VALUES(:esnNo,:transNumEsnInt,:lopInd,:loadingFrom,:dgInd,:hscd, ");
				sb.append(":dutiInt,:truckerName,:truckerNo,");
				sb.append(":noOfStorageDay,:stgInd,:noOfPkgs,:weight,:volume,:accNo,:cargoDes, ");
				sb.append(":truckerCNo,:truckerCd,:strUserID,sysdate,:payMode,:pkgsType,:customHsCode) ");
				strEsnDetailsTrans = sb.toString();

				sqlTrans = "SELECT MAX(TRANS_NBR) FROM ESN_TRANS WHERE ESN_ASN_NBR=:esnNo";
				paramMap.put("esnNo", esnNo);
				log.info("esnUpdateForDPE sqlTrans SQL 2: " + sqlTrans);
				log.info(" paramMap: " + paramMap);
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqlTrans, paramMap);
				while (rs1.next()) {
					strTransNbr = CommonUtility.deNull(rs1.getString(1));
				}
				if (strTransNbr.equalsIgnoreCase("")) {
					strTransNbr = "0";
				} else {
					strTransNbr = String.valueOf(Integer.parseInt(strTransNbr) + 1);
				}
				deliveryToEPC = (deliveryToEPC == null || deliveryToEPC.equalsIgnoreCase("null")
						|| deliveryToEPC.trim().length() == 0) ? "N" : deliveryToEPC;

				sb.setLength(0);
				sb.append(
						"INSERT INTO ESN_Trans(ESN_ASN_NBR,TRANS_NBR,DECLARANT_CR_NO,TRANS_TYPE,LAST_MODIFY_USER_ID, ");
				sb.append("LAST_MODIFY_DTTM,ESN_STATUS,STUFF_IND,EPC_IND,CNTR_SEQ_NBR, MISC_APP_NBR) ");
				sb.append("VALUES(:esnNo,:strTransNbr,'O','E',:strUserID,sysdate,'A',:stfInd, ");
				sb.append(":deliveryToEPC,:cntrSeqNbr,:miscAppNo)");
				strInsertTrans = sb.toString();

				sqlTrans = "SELECT MAX(TRANS_NBR) FROM ESN_MARKINGS_TRANS WHERE ESN_ASN_NBR=:esnNo ";
				paramMap.put("esnNo", esnNo);
				log.info("esnUpdateForDPE sqlTrans SQL 3: " + sqlTrans);
				log.info(" paramMap: " + paramMap);
				rs2 = namedParameterJdbcTemplate.queryForRowSet(sqlTrans, paramMap);
				while (rs2.next()) {
					strMarkTransNbr = CommonUtility.deNull(rs2.getString(1));
				}

				if (strMarkTransNbr.equalsIgnoreCase("")) {
					strMarkTransNbr = "0";
				} else {
					strMarkTransNbr = String.valueOf(Integer.parseInt(strMarkTransNbr) + 1);
				}

				sb.setLength(0);
				sb.append(
						"INSERT INTO ESN_MARKINGS_Trans(ESN_ASN_NBR,TRANS_NBR,MARKINGS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)VALUES(");
				sb.append(":esnNo,:strMarkTransNbr,:mark,");
				sb.append(":strUserID,sysdate)");
				strMarkTrans = sb.toString();

				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y") {
					paramMap.put("esnNo", esnNo);
					paramMap.put("strTransNbr", strTransNbr);
					paramMap.put("strUserID", strUserID);
					paramMap.put("stfInd", stfInd);
					paramMap.put("deliveryToEPC", deliveryToEPC);
					paramMap.put("cntrSeqNbr", cntrSeqNbr);
					paramMap.put("miscAppNo", miscAppNo);
					paramMap.put("esnNo", esnNo);
					log.info("esnUpdateForDPE strInsertTrans SQL: " + strInsertTrans);
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(strInsertTrans, paramMap);

					paramMap.put("esnNo", esnNo);
					paramMap.put("strMarkTransNbr", strMarkTransNbr);
					// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
					paramMap.put("mark", (mark));
					paramMap.put("strUserID", strUserID);
					log.info("esnUpdateForDPE strMarkTrans SQL: " + strMarkTrans);
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(strMarkTrans, paramMap);

					paramMap.put("esnNo", esnNo);
					paramMap.put("transNumEsnInt", transNumEsnInt);
					paramMap.put("lopInd", lopInd);
					paramMap.put("loadingFrom", loadingFrom);
					paramMap.put("dgInd", dgInd);
					paramMap.put("hscd", hscd);
					paramMap.put("dutiInt", dutiInt);
					paramMap.put("truckerName", (CommonUtility.deNull(truckerName).trim()));
					paramMap.put("truckerNo", (CommonUtility.deNull(truckerNo).trim()));
					paramMap.put("noOfStorageDay", noOfStorageDay);
					paramMap.put("stgInd", stgInd);
					paramMap.put("noOfPkgs", noOfPkgs);
					paramMap.put("weight", weight);
					paramMap.put("volume", volume);
					paramMap.put("accNo", (accNo));
					paramMap.put("cargoDes", (cargoDes));
					paramMap.put("truckerCNo", truckerCNo);
					paramMap.put("truckerCd", truckerCd);
					paramMap.put("strUserID", strUserID);
					paramMap.put("payMode", payMode);
					paramMap.put("pkgsType", pkgsType);
					paramMap.put("customHsCode", customHsCode);
					log.info("esnUpdateForDPE strEsnDetailsTrans SQL: " + strEsnDetailsTrans);
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(strEsnDetailsTrans, paramMap);

				}
			}

			paramMap.put("stfInd", stfInd);
			paramMap.put("strUserID", strUserID);
			paramMap.put("category", category);
			paramMap.put("cntrSeqNbr", cntrSeqNbr);
			paramMap.put("miscAppNo", miscAppNo);
			if (deliveryToEPC != null && !deliveryToEPC.equalsIgnoreCase("null") && deliveryToEPC.trim().length() > 0) {
				paramMap.put("deliveryToEPC", deliveryToEPC);
			}
			paramMap.put("esnNo", esnNo);
			log.info("esnUpdateForDPE strESNUpdate SQL: " + strESNUpdate);
			log.info(" paramMap: " + paramMap);
			int countStfInd = namedParameterJdbcTemplate.update(strESNUpdate, paramMap);

			paramMap.put("noOfPkgs", noOfPkgs);
			paramMap.put("hscd", hscd);
			paramMap.put("hsSubCdFr", CommonUtility.deNull(hsSubCodeFr));
			paramMap.put("hsSubCdTo", CommonUtility.deNull(hsSubCodeTo));
			paramMap.put("pkgsType", pkgsType);
			// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
			paramMap.put("trckrNm", (CommonUtility.deNull(truckerName).trim()));
			paramMap.put("truckerNbrPkgs", truckerNbrPkgs);
			paramMap.put("trckIc", (CommonUtility.deNull(truckerNo).trim()));
			paramMap.put("lopInd", lopInd);
			paramMap.put("dgInd", dgInd);
			paramMap.put("stgInd", stgInd);
			paramMap.put("loadingFrom", loadingFrom);
			paramMap.put("poD", poD);
			paramMap.put("noOfStorageDay", noOfStorageDay);
			paramMap.put("dutiInt", dutiInt);
			paramMap.put("payMode", payMode);
			paramMap.put("accNbr", (accNo));
			paramMap.put("crgDes", (cargoDes));
			paramMap.put("truckerCd", truckerCd);
			paramMap.put("truckerCNo", truckerCNo);
			paramMap.put("weight", weight);
			paramMap.put("volume", volume);
			paramMap.put("esnNo", esnNo);
			paramMap.put("customHsCode", customHsCode);
			log.info("esnUpdateForDPE strUpdate SQL: " + strUpdate);
			log.info(" paramMap: " + paramMap);
			int count = namedParameterJdbcTemplate.update(strUpdate, paramMap);
			log.info("esnUpdateForDPE strUpdate count: " + count);
			
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
					paramMap.put("userId", strUserID);
				
					
					if(hsCodeObj.getIsHsCodeChange().equalsIgnoreCase("A")) { // Add										
						// get ESN_HSCODE_SEQ_NBR 
						StringBuilder sbSeq = new StringBuilder();
						sbSeq.append("SELECT GBMS.SEQ_ESN_HSCODE_SEQ_NBR.nextval AS seqVal FROM DUAL");
						Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(), new HashMap<String, String>());
						BigDecimal seqValue = (BigDecimal) results.get("seqVal");
						// end
						
						sb.setLength(0);
						sb.append(" INSERT INTO GBMS.ESN_HSCODE_DETAILS  ");
						sb.append(" (ESN_ASN_NBR,ESN_HSCODE_SEQ_NBR,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,CUSTOM_HS_CODE,CRG_DES, HS_SUB_CODE_DESC,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
						sb.append(" VALUES(:ESN_ASN_NBR,:ESN_HSCODE_SEQ_NBR,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:NBR_PKGS,:GROSS_WT,:GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
						
	
						paramMap.put("ESN_HSCODE_SEQ_NBR", seqValue);
						paramMap.put("REC_STATUS", "A");
						log.info("SQL" + sb.toString());
						log.info("params: " + paramMap.toString());
						int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
						log.info("counths : " + counths);
						
					}else if(hsCodeObj.getIsHsCodeChange().equalsIgnoreCase("E")) { // Edit
						sb.setLength(0);
						sb.append(" UPDATE GBMS.ESN_HSCODE_DETAILS SET HS_CODE=:HS_CODE, HS_SUB_CODE_FR=:HS_SUB_CODE_FR, HS_SUB_CODE_TO=:HS_SUB_CODE_TO,");
						sb.append(" NBR_PKGS=:NBR_PKGS, GROSS_WT=:GROSS_WT, GROSS_VOL=:GROSS_VOL,CUSTOM_HS_CODE=:CUSTOM_HS_CODE, CRG_DES=:CRG_DES, ");
						sb.append(" HS_SUB_CODE_DESC=:HS_SUB_CODE_DESC, LAST_MODIFY_USER_ID=:userId, LAST_MODIFY_DTTM=SYSDATE WHERE");
						sb.append(" ESN_ASN_NBR = :ESN_ASN_NBR AND ESN_HSCODE_SEQ_NBR=:ESN_HSCODE_SEQ_NBR ");
						
	
						paramMap.put("ESN_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());
						paramMap.put("REC_STATUS", "A");
						
						log.info("SQL" + sb.toString());
						log.info("params: " + paramMap.toString());
						int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
						log.info("counths : " + counths);
						
					}else if(hsCodeObj.getIsHsCodeChange().equalsIgnoreCase("D")) {
						sb.setLength(0);
						sb.append(" DELETE FROM GBMS.ESN_HSCODE_DETAILS WHERE ESN_HSCODE_SEQ_NBR = :ESN_HSCODE_SEQ_NBR ");
					
						paramMap.put("REC_STATUS", "I");
						paramMap.put("ESN_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());
						log.info("SQL" + sb.toString());
						log.info("params: " + paramMap.toString());
						int counths = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
						log.info("counths : " + counths);
					}
					
					sb.setLength(0);
					sb.append(" INSERT INTO GBMS.ESN_HSCODE_DETAILS_TRANS  ");
					sb.append(" (ESN_ASN_NBR,ESN_HSCODE_SEQ_NBR,AUDIT_DTTM, REC_STATUS,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,CUSTOM_HS_CODE,CRG_DES, HS_SUB_CODE_DESC,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
					sb.append(" VALUES(:ESN_ASN_NBR,:ESN_HSCODE_SEQ_NBR, SYSDATE, :REC_STATUS,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:NBR_PKGS,:GROSS_WT,:GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
		
					log.info("SQL" + sb.toString());
					log.info("params: " + paramMap.toString());
					int counthsAudit = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
					log.info("counths : " + counthsAudit);
				
				}
			}
			// END CR FTZ HSCODE - NS JULY 2024
			
			sb.setLength(0);
			sb.append(" UPDATE SUB_ADP SET STATUS_CD = 'X',LAST_MODIFY_DTTM=SYSDATE, LAST_MODIFY_USER_ID=:strUserID");
			sb.append(" WHERE STATUS_CD= 'A' AND ESN_ASN_NBR =:esnNo AND EDO_ESN_IND = 0 ");
			String strDeleteADP = sb.toString();

			paramMap.put("esnNo", esnNo);
			paramMap.put("strUserID", strUserID);
			log.info("esnUpdateForDPE strDeleteADP SQL: " + strDeleteADP);
			log.info(" paramMap: " + paramMap);
			int rs4DeleteAdp = namedParameterJdbcTemplate.update(strDeleteADP, paramMap);
			log.info("esnUpdateForDPE strDeleteADP rs4DeleteAdp: " + rs4DeleteAdp);
			
			sb.setLength(0);
			sb.append(	"UPDATE (SELECT txn.status_cd status, txn_dttm, txn_user_id  from sub_adp_txn txn inner join sub_adp sub on txn.sub_adp_nbr = sub.sub_adp_nbr");
			sb.append(" where txn.status_cd = 'A' and sub.edo_esn_ind = '0' and esn_asn_nbr =:esnNo)");
			sb.append(" t set t.status='X', txn_dttm=SYSDATE, txn_user_id=:strUserID");
			String strDeleteAdpTxn = sb.toString();
			paramMap.put("esnNo", esnNo);
			paramMap.put("strUserID", strUserID);
			log.info("esnUpdateForDPE strDeleteAdpTxn SQL: " + strDeleteAdpTxn);
			log.info(" paramMap: " + paramMap);
			namedParameterJdbcTemplate.update(strDeleteAdpTxn, paramMap);
			
			if (rs4DeleteAdp >= 0) {
				if (truckerList != null && truckerList.size() > 1) {
					for (int i = 1; i < truckerList.size(); i++) {
						TruckerValueObject adpObj = new TruckerValueObject();
						adpObj = (TruckerValueObject) truckerList.get(i);
						try {
							insertTruckerInfor(CommonUtility.deNull(adpObj.getTruckerIc()).trim(),
									CommonUtility.deNull(adpObj.getTruckerNm()).trim(), adpObj.getTruckerContact(),
									Integer.parseInt(adpObj.getTruckerPkgs()), adpObj.getTruckerCd(), esnNo, "A", 0,
									strUserID);

						} catch (NumberFormatException e) {
							log.info("Exception esnUpdateForDPE : ", e);
							throw new BusinessException("M4201");
						}
					}

				}
			}

			paramMap.put("esnNo", esnNo);
			// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
			paramMap.put("marking", (mark));
			log.info("esnUpdateForDPE strMark SQL: " + strMark);
			log.info(" paramMap: " + paramMap);
			int cntmark = namedParameterJdbcTemplate.update(strMark, paramMap);
			log.info("esnUpdateForDPE strMark cntmark: " + cntmark);
			
			strCntr1 = "UPDATE ESN_CNTR SET CNTR_NBR=:cntr1 WHERE ESN_ASN_NBR=:esnNo AND ESN_CNTR_SEQ=1";
			paramMap.put("esnNo", esnNo);
			paramMap.put("cntr1", (cntr1));
			log.info("esnUpdateForDPE strCntr1 SQL 1: " + strCntr1);
			log.info(" paramMap: " + paramMap);
			int cntcntr1 = namedParameterJdbcTemplate.update(strCntr1, paramMap);
			log.info("esnUpdateForDPE strCntr1 cntcntr1: " + cntcntr1);
			if (cntcntr1 == 0 && cntr1 != "") {
				strCntr1 = "INSERT INTO ESN_CNTR(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES(1,:esnNo,:cntr1)";
				paramMap.put("esnNo", esnNo);
				paramMap.put("cntr1", (cntr1));
				log.info("esnUpdateForDPE strCntr1 SQL 2: " + strCntr1);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strCntr1, paramMap);
			}

			strCntr2 = "UPDATE ESN_CNTR SET CNTR_NBR=:cntr2 WHERE ESN_ASN_NBR=:esnNo AND ESN_CNTR_SEQ=2";
			paramMap.put("esnNo", esnNo);
			paramMap.put("cntr2", (cntr2));
			log.info("esnUpdateForDPE strCntr2 SQL 1: " + strCntr2);
			log.info(" paramMap: " + paramMap);
			int cntcntr2 = namedParameterJdbcTemplate.update(strCntr2, paramMap);
			log.info("esnUpdateForDPE strCntr2 cntcntr2: " + cntcntr2);
			if (cntcntr2 == 0 && cntr2 != "") {
				strCntr2 = "INSERT INTO ESN_CNTR(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES(2,:esnNo,:cntr2)";
				paramMap.put("esnNo", esnNo);
				// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
				paramMap.put("cntr2", (cntr2));
				log.info("esnUpdateForDPE strCntr2 SQL 2: " + strCntr2);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strCntr2, paramMap);
			}

			strCntr3 = "UPDATE ESN_CNTR SET CNTR_NBR=:cntr3 WHERE ESN_ASN_NBR=:esnNo AND ESN_CNTR_SEQ=3";
			paramMap.put("esnNo", esnNo);
			// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
			paramMap.put("cntr3", (cntr3));
			log.info("esnUpdateForDPE strCntr3 SQL 1: " + strCntr3);
			log.info(" paramMap: " + paramMap);
			int cntcntr3 = namedParameterJdbcTemplate.update(strCntr3, paramMap);
			log.info("esnUpdateForDPE strCntr3 cntcntr3: " + cntcntr3);
			if (cntcntr3 == 0 && cntr3 != "") {
				strCntr3 = "INSERT INTO ESN_CNTR(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES(3,:esnNo,:cntr3)";
				paramMap.put("esnNo", esnNo);
				paramMap.put("cntr3", (cntr3));
				log.info("esnUpdateForDPE strCntr3 SQL 2: " + strCntr3);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strCntr3, paramMap);
			}

			strCntr4 = "UPDATE ESN_CNTR SET CNTR_NBR=:cntr4 WHERE ESN_ASN_NBR=:esnNo AND ESN_CNTR_SEQ=4";
			paramMap.put("esnNo", esnNo);
			// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
			paramMap.put("cntr4", (cntr4));
			log.info("esnUpdateForDPE strCntr4 SQL 1: " + strCntr4);
			log.info(" paramMap: " + paramMap);
			int cntcntr4 = namedParameterJdbcTemplate.update(strCntr4, paramMap);
			log.info("esnUpdateForDPE strCntr4 cntcntr4: " + cntcntr4);
			if (cntcntr4 == 0 && cntr4 != "") {
				strCntr4 = "INSERT INTO ESN_CNTR(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES(4,:esnNo,:cntr4)";
				paramMap.put("esnNo", esnNo);
				paramMap.put("cntr4", (cntr4));
				log.info("esnUpdateForDPE strCntr4 SQL 2: " + strCntr4);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strCntr4, paramMap);
			}

			strCntr1Trans = "INSERT INTO esn_cntr_Trans(ESN_CNTR_SEQ,ESN_ASN_NBR,TRANS_NBR,CNTR_NBR) VALUES('1',:esnNo,:transNumEsnInt,:cntr1)";
			strCntr2Trans = "INSERT INTO esn_cntr_Trans(ESN_CNTR_SEQ,ESN_ASN_NBR,TRANS_NBR,CNTR_NBR) VALUES('2',:esnNo,:transNumEsnInt,:cntr2)";
			strCntr3Trans = "INSERT INTO esn_cntr_Trans(ESN_CNTR_SEQ,ESN_ASN_NBR,TRANS_NBR,CNTR_NBR) VALUES('3',:esnNo,:transNumEsnInt,:cntr3)";
			strCntr4Trans = "INSERT INTO esn_cntr_Trans(ESN_CNTR_SEQ,ESN_ASN_NBR,TRANS_NBR,CNTR_NBR) VALUES('4',:esnNo,:transNumEsnInt,:cntr4)";

			// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
			if (cntr1 != null && !cntr1.equals("")) {
				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y")
					paramMap.put("transNumEsnInt", transNumEsnInt);
				paramMap.put("cntr1", (cntr1));
				paramMap.put("esnNo", esnNo);
				log.info("esnUpdateForDPE strCntr1Trans SQL: " + strCntr1Trans);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strCntr1Trans, paramMap);
			}

			if (cntr2 != null && !cntr2.equals("")) {
				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y")
					paramMap.put("transNumEsnInt", transNumEsnInt);
				paramMap.put("cntr2", (cntr2));
				paramMap.put("esnNo", esnNo);
				log.info("esnUpdateForDPE strCntr2Trans SQL: " + strCntr2Trans);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strCntr2Trans, paramMap);
			}

			if (cntr3 != null && !cntr3.equals("")) {
				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y")
					paramMap.put("transNumEsnInt", transNumEsnInt);
				paramMap.put("cntr3", (cntr3));
				paramMap.put("esnNo", esnNo);
				log.info("esnUpdateForDPE strCntr3Trans SQL: " + strCntr3Trans);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strCntr3Trans, paramMap);
			}

			if (cntr4 != null && !cntr4.equals("")) {
				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y")
					paramMap.put("transNumEsnInt", transNumEsnInt);
				paramMap.put("cntr4", (cntr4));
				paramMap.put("esnNo", esnNo);
				log.info("esnUpdateForDPE strCntr4Trans SQL: " + strCntr4Trans);
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strCntr4Trans, paramMap);
			}
			if ("JP".equalsIgnoreCase(custCd)) {
				sb.setLength(0);
				sb.append("SELECT DISTINCT vsl.vv_cd FROM esn esn LEFT JOIN vessel_call vsl");
				sb.append(" ON esn.out_voy_var_nbr = vsl.vv_cd LEFT JOIN berthing be ");
				sb.append(" ON vsl.vv_cd = be.vv_cd AND shift_ind = 1 ");
				sb.append(" WHERE be.atu_dttm   < sysdate AND esn.esn_asn_nbr =:esnNbr");
				String atuSql = sb.toString();
				paramMap.put("esnNbr", esnNbr);
				log.info("esnUpdateForDPE atuSql SQL: " + atuSql);
				log.info(" paramMap: " + paramMap);
				SqlRowSet atuRs = namedParameterJdbcTemplate.queryForRowSet(atuSql, paramMap);
				String vvCd = "";
				while (atuRs.next()) {
					vvCd = atuRs.getString(1);
				}

				if (StringUtils.isNotBlank(vvCd)) {
					String query1 = "SELECT MISC_EVENT_LOG_SEQ_NBR.nextVal FROM DUAL ";
					atuRs = namedParameterJdbcTemplate.queryForRowSet(query1, paramMap);
					long nextValue = 0;
					Long nextMiscSeqNbr = null;
					if (atuRs.next()) {
						nextValue = atuRs.getLong("NEXTVAL");
						nextMiscSeqNbr = new Long(nextValue);
					}
					sb.setLength(0);
					sb.append("INSERT INTO MISC_EVENT_LOG ");
					sb.append(
							"(MISC_SEQ_NBR,TXN_DTTM,TXN_CD,BILL_IND,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,VV_CD,REF_NBR) ");
					sb.append(" VALUES (:nextMiscSeqNbr, sysdate,:TXN_CD, 'N',");
					sb.append(":strUserID,sysdate,:vvCd,:esnNbr)");
					String sqlInsertMiscEventLog = sb.toString();
					paramMap.put("nextMiscSeqNbr", nextMiscSeqNbr);
					paramMap.put("TXN_CD", TXN_CD);
					paramMap.put("strUserID", strUserID);
					paramMap.put("vvCd", vvCd);
					paramMap.put("esnNbr", esnNbr);
					log.info("esnUpdateForDPE sqlInsertMiscEventLog SQL: " + sqlInsertMiscEventLog);
					log.info(" paramMap: " + paramMap);
					int count1 = namedParameterJdbcTemplate.update(sqlInsertMiscEventLog, paramMap);
					log.info("esnUpdateForDPE sqlInsertMiscEventLog count1: " + count1);
					
					if (count1 == 0) {
						log.info("Record Cannot be added to Database");
						throw new BusinessException("M4201");
					}
				}
			}
			if (!strUAFlag.equals("") && strUAFlag != null && strUAFlag.equals("UA")) {
			} else {
				if (countStfInd == 0 || count == 0 || cntmark == 0) {
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}
			}

			if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y") {
				log.info(strInsertTrans);
				log.info(strMarkTrans);
				log.info(strEsnDetailsTrans);
				log.info(strCntr1Trans);
				log.info(strCntr2Trans);
				log.info(strCntr3Trans);
				log.info(strCntr4Trans);
			}
		} catch (BusinessException e) {
			log.info("Exception esnUpdateForDPE : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception esnUpdateForDPE : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END esnUpdateForDPE DAO");
			log.info(strUpdate);
			log.info(strMark);
		}
		return esnNo;
	}

	public void insertTruckerInfor(String truckerCiNo, String truckerName, String truckerContact, int truckerPackage,
			String truckerCd, String esnAsnNbr, String statusCd, int edoEsn, String userId) throws BusinessException {
		SqlRowSet rs = null;
		String sql = "";
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sqllog = "";
		int adpSeq = 0;
		try {
			log.info("START insertTruckerInfor DAO.  truckerCiNo: " + CommonUtility.deNull(truckerCiNo) + " , truckerName: " 
					+ CommonUtility.deNull(truckerName) + " , truckerContact: " + CommonUtility.deNull(truckerContact) + " , truckerPackage: "
					+ CommonUtility.deNull(String.valueOf(truckerPackage)) + " , truckerCd: " + CommonUtility.deNull(truckerCd) 
					+ " , esnAsnNbr: " + CommonUtility.deNull(esnAsnNbr) + " , statusCd: " + CommonUtility.deNull(statusCd) 
					+ " , edoEsn: " + CommonUtility.deNull(String.valueOf(edoEsn)) + " , userId: " + CommonUtility.deNull(userId));
			sb.append(" INSERT INTO SUB_ADP(ESN_ASN_NBR, STATUS_CD, TRUCKER_CO_CD, TRUCKER_NM, TRUCKER_IC, ");
			sb.append(
					" TRUCKER_CONTACT_NBR, CREATE_USER_ID, CREATE_DTTM, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, EDO_ESN_IND, TRUCKER_NBR_PKGS, SUB_ADP_NBR) ");
			sb.append(
					" VALUES (:esnAsnNbr,:statusCd,:truckerCd,:truckerName,:truckerCiNo,:truckerContact,:userId,sysdate,:userId,sysdate,:edoEsn,:truckerPackage,:adpSeq)");
			sql = sb.toString();
			sqllog = "SELECT MAX(SUB_ADP_NBR) FROM sub_adp";
			log.info("insertTruckerInfor sqllog SQL: " + sqllog);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqllog, paramMap);
			if (rs.next()) {
				adpSeq = Integer.parseInt(CommonUtility.deNull(rs.getString(1)));
			}
            
			paramMap.put("esnAsnNbr", esnAsnNbr);
			paramMap.put("statusCd", statusCd);
			paramMap.put("truckerCd", truckerCd);
			paramMap.put("truckerName", CommonUtility.deNull(truckerName).trim());
			paramMap.put("truckerCiNo", CommonUtility.deNull(truckerCiNo).trim());
			paramMap.put("truckerContact", truckerContact);
			paramMap.put("userId", userId);
			paramMap.put("userId", userId);
			paramMap.put("edoEsn", edoEsn);
			paramMap.put("truckerPackage", truckerPackage);
			paramMap.put("adpSeq", adpSeq + 1); 
			log.info("insertTruckerInfor sql SQL: " + sql);
			log.info(" paramMap: " + paramMap);
			namedParameterJdbcTemplate.update(sql, paramMap);

			TruckerValueObject trkObj = new TruckerValueObject();
			trkObj.setTruckerCd(truckerCd);
			trkObj.setTruckerNm(CommonUtility.deNull(truckerName).trim());
			trkObj.setTruckerIc(CommonUtility.deNull(truckerCiNo).trim());
			trkObj.setTruckerContact(truckerContact);
			trkObj.setTruckerPkgs(String.valueOf(truckerPackage));
			insertSubAdpTxn(adpSeq, userId, trkObj);
		} catch (Exception e) {
			log.info("Exception insertTruckerInfor : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END insertTruckerInfor DAO");
		}
	}

	@Transactional(rollbackFor = BusinessException.class)
	private void insertSubAdpTxn(int subAdpNbr, String creat_userID, TruckerValueObject trkObj)
			throws BusinessException {
		StringBuilder sb = new StringBuilder();
		String SQL_INSERT_SUBADP_TXN = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {
			log.info("START insertSubAdpTxn DAO. subAdpNbr: " + CommonUtility.deNull(String.valueOf(subAdpNbr)) + " , creat_userID: " + CommonUtility.deNull(creat_userID)
					+ " , trkObj: " + CommonUtility.deNull(trkObj.toString()));
			
			sb.append(" insert into SUB_ADP_TXN(SUB_ADP_NBR, TXN_DTTM, TXN_USER_ID, STATUS_CD,TRUCKER_CO_CD," );
			sb.append(" TRUCKER_NM,TRUCKER_IC,TRUCKER_CONTACT_NBR,EDO_ESN_IND,TRUCKER_NBR_PKGS) ");
			sb.append(" values(:subAdpNbr,TO_DATE(:date_time,'YYYY-MM-DD hh24:mi:ss'),:creat_userID,:status_cd,:trkCd,:trkNm,:trkIc,:trkContact,:edoInd,:trkPkgs) ");
			SQL_INSERT_SUBADP_TXN = sb.toString();
			
			// START: Added on 18012023 : Change SYSDATE to new date to fix issue unique constraint - NS
			String dttm = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date());
			// END : Added on 18012023 : Change SYSDATE to new date to fix issue unique constraint - NS
			paramMap.put("subAdpNbr", subAdpNbr);
			paramMap.put("date_time", dttm);
			paramMap.put("creat_userID", creat_userID);
			paramMap.put("status_cd", "A");
			paramMap.put("trkCd", trkObj.getTruckerCd());
			paramMap.put("trkNm", CommonUtility.deNull(trkObj.getTruckerNm()).trim());
			paramMap.put("trkIc", CommonUtility.deNull(trkObj.getTruckerIc()).trim());
			paramMap.put("trkContact", trkObj.getTruckerContact());
			paramMap.put("edoInd", "0");
			paramMap.put("trkPkgs", Integer.parseInt(trkObj.getTruckerPkgs()));
			log.info("insertSubAdpTxn SQL_INSERT_SUBADP_TXN SQL: " + SQL_INSERT_SUBADP_TXN);
			log.info(" paramMap: " + paramMap);
			
			int count = namedParameterJdbcTemplate.update(SQL_INSERT_SUBADP_TXN, paramMap);
			log.info("insertSubAdpTxn SQL_INSERT_SUBADP_TXN count: " + count);
			if (count == 0) {
				log.info("Writing from EdoEjb.insertSubAdpTxn");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");

			}
		} catch (NullPointerException e) {
			log.info("Exception insertSubAdpTxn : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException se) {
			log.info("Exception insertSubAdpTxn: ", se);
			throw new BusinessException(se.getMessage());
		} catch (Exception se) {
			log.info("Exception insertSubAdpTxn: ", se);
			throw new BusinessException("M4201");
		} finally {
			log.info("END insertSubAdpTxn DAO");

		}
	}

	// ejb.sessionBeans.gbms.cargo.esn -->EsnEJB
	/**
	 * This public method chkAccNo check the account nbr active or not and
	 * business_type any of G or trail_ind is N.
	 *
	 * @param accNo String
	 * @return Boolean if AccNo is True or False
	 * @throws BusinessException
	 * 
	 */
	@Override
	public boolean chkAccNo(String accNo) throws BusinessException {
		String AccountNo = accNo.toUpperCase();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		boolean result = true;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: chkAccNo Dao Start accNo:" + CommonUtility.deNull(accNo));

			sb.append(" select ACCT_NBR from cust_acct where upper(ACCT_NBR) =:accountNo ");
			sb.append(" and acct_status_cd='A' and business_type like '%G%' and trial_ind='N' ");

			// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
			paramMap.put("accountNo", (AccountNo));
			log.info("chkAccNo sb.toString() SQL: " + sb.toString());
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next())
				result = true;
			else
				result = false;
		} catch (Exception e) {
			log.info("Exception chkAccNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkAccNo DAO  result: " + result);
		}
		return result;
	}

	// package: ejb.sessionBeans.gbms.cargo.esn-->EsnEJB
	// method: AssignWhindUpdate()
	/**
	 * The method AssignWhindCheck to get whind indicator
	 * 
	 * @param String crgval
	 * @param String esnnbr
	 * @param String whappnbr
	 * @param String remarks
	 * @param String nodays
	 * @param String userId
	 * @exception BusinessException
	 *
	 */
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void assignWhindUpdate(String crgval, String esnnbr, String whappnbr, String remarks, String nodays,
			String userId) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sqltlog = new StringBuilder();
		StringBuilder strInsert_trans = new StringBuilder();
		int stransno = 0;
		int count_trans = 0;

		if (crgval.equals("Y"))
			sql.append(
					" UPDATE esn SET WH_IND =:crgval,WH_AGGR_NBR =:whappnbr,WH_REMARKS=:remarks,FREE_STG_DAYS = 0,LAST_MODIFY_USER_ID=:userId,LAST_MODIFY_DTTM = sysdate WHERE ESN_ASN_NBR =:esnnbr ");
		else if (crgval.equals("NO"))
			sql.append(
					" UPDATE esn SET WH_IND ='N',WH_AGGR_NBR='',WH_REMARKS = '',FREE_STG_DAYS = 0,LAST_MODIFY_USER_ID=:userId,LAST_MODIFY_DTTM = sysdate WHERE ESN_ASN_NBR =:esnnbr ");
		else
			sql.append(
					" UPDATE esn SET WH_IND =:crgval,WH_AGGR_NBR='',WH_REMARKS = '',FREE_STG_DAYS =:nodays,LAST_MODIFY_USER_ID=:userId,LAST_MODIFY_DTTM = sysdate WHERE ESN_ASN_NBR =:esnnbr ");
		try {
			log.info("START: SMSEmailAlert Dao Start crgval:" + CommonUtility.deNull(crgval) + "esnnbr:" + CommonUtility.deNull(esnnbr) + "whappnbr:" + CommonUtility.deNull(whappnbr)
					+ "remarks:" + CommonUtility.deNull(remarks) + "nodays:" + CommonUtility.deNull(nodays) + "userId:" + CommonUtility.deNull(userId));

			paramMap.put("crgval", crgval);
			paramMap.put("whappnbr", whappnbr);
			// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
			paramMap.put("remarks", (remarks));
			paramMap.put("userId", userId);
			paramMap.put("esnnbr", esnnbr);
			paramMap.put("nodays", nodays);
			log.info("assignWhindUpdate sql SQL: " + sql.toString());
			log.info(" paramMap: " + paramMap);
			int count = namedParameterJdbcTemplate.update(sql.toString(), paramMap);
			log.info("assignWhindUpdate sql.toString() count: " + count);

			sqltlog.append(" SELECT MAX(TRANS_NBR) FROM ESN_TRANS WHERE ESN_ASN_NBR=:esnnbr ");

			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003

				paramMap.put("esnnbr", esnnbr);
				log.info("SQL Query:" + sqltlog.toString());
				log.info("assignWhindUpdate sqltlog SQL: " + sqltlog.toString());
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog.toString(), paramMap);

				if (rs.next()) {
					stransno = (rs.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
			}
			if (crgval.equals("Y")) {
				strInsert_trans.append(" INSERT INTO ESN_TRANS(TRANS_NBR,ESN_ASN_NBR, ");
				strInsert_trans
						.append(" WH_IND,WH_AGGR_NBR,WH_REMARKS,FREE_STG_DAYS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
				strInsert_trans.append(" VALUES(:stransno,:esnnbr,:crgval,:whappnbr,:remarks,0,:userId,sysdate) ");
			} else if (crgval.equals("NO")) {
				strInsert_trans.append(" INSERT INTO ESN_TRANS(TRANS_NBR,ESN_ASN_NBR, ");
				strInsert_trans
						.append(" WH_IND,WH_AGGR_NBR,WH_REMARKS,FREE_STG_DAYS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
				strInsert_trans.append(" VALUES(:stransno,:esnnbr,'N','','', 0,:userId,sysdate) ");

			} else {
				strInsert_trans.append(" INSERT INTO ESN_TRANS(TRANS_NBR,ESN_ASN_NBR, ");
				strInsert_trans
						.append(" WH_IND,WH_AGGR_NBR,WH_REMARKS,FREE_STG_DAYS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
				strInsert_trans.append(" VALUES(:stransno,:esnnbr,:crgval,'','',:nodays,:userId,sysdate) ");

			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				paramMap.put("stransno", stransno);
				paramMap.put("crgval", crgval);
				paramMap.put("whappnbr", whappnbr);
				// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
				paramMap.put("remarks", (remarks));
				paramMap.put("userId", userId);
				paramMap.put("esnnbr", esnnbr);
				paramMap.put("nodays", nodays);
				log.info("assignWhindUpdate strInsert_trans SQL: " + strInsert_trans.toString());
				log.info(" paramMap: " + paramMap);
				count_trans = namedParameterJdbcTemplate.update(strInsert_trans.toString(), paramMap);
				log.info("assignWhindUpdate strInsert_trans.toString() count_trans: " + count_trans);
			}

			if (count == 0) {

				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 21/01/2003
				if (count_trans == 0) {
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}
			}
		} catch (BusinessException e) {
			log.info("Exception SMSEmailAlert : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception SMSEmailAlert : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: SMSEmailAlert DAO");
		}
	}

	@Override
	public String getVesselType(String bookref) throws BusinessException {
		String vslType = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getVesselType  DAO  Start Obj " + " bookref:" + CommonUtility.deNull(bookref));
			String sql = "select C.VSL_TYPE_CD from vessel_call a ,bk_details b ,vessel c where a.vv_cd = b.var_nbr and c.vsl_nm = A.vsl_nm and b.bk_ref_nbr=:bookref";
			log.info(" *** getVesselType SQL *****" + sql);
			paramMap.put("bookref", bookref);
			log.info("getVesselType sql SQL: " + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				vslType = rs.getString(1);
			}
			log.info("END: *** getVesselType Result *****" + vslType.toString());
		} catch (Exception e) {
			log.info("Exception getVesselType : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselType DAO");
		}
		return vslType;
	}

	@Override
	public void updateCargoCategoryCode(String cargoCategoryCode, String bkRefNbr) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: updateCargoCategoryCode DAO cargoCategoryCode:" + CommonUtility.deNull(cargoCategoryCode) + " bkRefNbr"
					+ CommonUtility.deNull(bkRefNbr));
			sb.append(" UPDATE esn SET cargo_category_cd =:cargoCategoryCode ");
			sb.append(" WHERE bk_ref_nbr =:bkRefNbr ");
			sb.append(" AND esn_status = 'A' ");
			paramMap.put("cargoCategoryCode", cargoCategoryCode);
			paramMap.put("bkRefNbr", bkRefNbr);
			log.info("updateCargoCategoryCode sb SQL: " + sb.toString());
			log.info(" paramMap: " + paramMap);
			int count = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			log.info("END: *** updateCargoCategoryCode Result *****" + count);
		} catch (Exception e) {
			log.info("Exception updateCargoCategoryCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateCargoCategoryCode DAO");
		}
	}

	@Override
	public List<Map<String, Object>> getCategoryList() throws BusinessException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: getCategoryList  DAO  Start Obj ");

			String sql = "SELECT CC_NAME, CC_CD FROM CARGO_CATEGORY_CODE WHERE INSTR((SELECT VALUE FROM TEXT_PARA WHERE PARA_CD='VEH_CARGO'),CC_CD)>0 AND CC_STATUS='A' ORDER BY CC_NAME ";

			log.info(" *** getCategoryList SQL *****" + sql);
			log.info("getCategoryList sql SQL: " + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ccCd", rs.getString("CC_CD"));
				map.put("ccName", rs.getString("CC_NAME"));
				list.add(map);
			}
			log.info("END: *** getCategoryList Result *****" + list.toString());

		} catch (NullPointerException e) {
			log.info("exception getCategoryList :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCategoryList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCategoryList  DAO  END");
		}

		return list;
	}

	@Override
	public String getCategoryValue(String ccCd) throws BusinessException {
		String ccName = "";
		String sql = "SELECT CC_NAME FROM CARGO_CATEGORY_CODE WHERE CC_CD =:ccCd ";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: getCategoryValue  DAO  Start Obj " + " ccCd:" + CommonUtility.deNull(ccCd));

			log.info(" *** getCategoryValue SQL *****" + sql);
			paramMap.put("ccCd", ccCd);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				ccName = rs.getString(1);
			}
			log.info("END: *** getCategoryValue Result *****" + ccName.toString());

		} catch (NullPointerException e) {
			log.info("exception getCategoryValue :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCategoryValue : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCategoryValue  DAO  END");
		}

		return ccName;
	}

	@Override
	public List<VesselVoyValueObject> getVesselList(String custId) throws BusinessException {
		String custCd = custId;
		log.info("*******Cust Code******[" + CommonUtility.deNull(custId) + "]");
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();

		boolean isShowEsnInfo = false;
		try {

			TextParaVO code = new TextParaVO();
			code.setParaCode(TEXT_PARA_GC_VIEW_ESN);
			TextParaVO result = textParaRepo.getParaCodeInfo(code);
			isShowEsnInfo = isShowEsnInfo(custId, result);
			// isShowEsnInfo = true; //MCC remove this line
		} catch (Exception e) {
			log.info("Retriving text para error: " + e.getMessage());
			log.info("Exception getVesselList : ", e);
			throw new BusinessException("M4201");
		}

		List<VesselVoyValueObject> vesselList = new ArrayList<VesselVoyValueObject>();
		VesselVoyValueObject vesselVoyValueObject = null;
		try {
			log.info("START: getVesselList  DAO  Start Obj " + " custId:" + CommonUtility.deNull(custId));

			if (isShowEsnInfo) {
				sql = "select distinct VV_CD,VSL_NM,TERMINAL,OUT_VOY_NBR from esn e, vessel_call ves  where e.trans_type='E' and e.esn_status = 'A' and ves.VV_STATUS_IND != 'CX' and e.out_voy_var_nbr = ves.vv_cd AND nvl(ves.GB_CLOSE_SHP_IND,'N') !='Y' ORDER BY TERMINAL DESC,VSL_NM,OUT_VOY_NBR";
			} else {

				sb.append(
						"select distinct VV_CD,VSL_NM,TERMINAL,OUT_VOY_NBR from esn e, vessel_call ves where e.trans_type='E'");
				sb.append(" and (e.ESN_CREATE_CD = :custCd OR ves.CREATE_CUST_CD = :custCd)");
				sb.append(" and (ves.VV_STATUS_IND != 'UB' or ves.VV_STATUS_IND != 'CX')");
				sb.append(
						" and e.esn_status = 'A' and e.out_voy_var_nbr = ves.vv_cd AND nvl(ves.GB_CLOSE_SHP_IND,'N') !='Y' ORDER BY TERMINAL DESC,VSL_NM,OUT_VOY_NBR");
				sql = sb.toString();
			}

			log.info(" *** getVesselList SQL *****" + sql);

			if (!isShowEsnInfo) {
				paramMap.put("custCd", custCd);
			}
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				vesselVoyValueObject = new VesselVoyValueObject();
				vesselVoyValueObject.setVoyNo(CommonUtility.deNull(rs.getString("OUT_VOY_NBR")));
				vesselVoyValueObject.setVslName(CommonUtility.deNull(rs.getString("VSL_NM")));
				vesselVoyValueObject.setVarNbr(CommonUtility.deNull(rs.getString("VV_CD")));
				vesselVoyValueObject.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				vesselList.add(vesselVoyValueObject);
				// log.info("Db Value :"+rs.getString("esn_nbr"));
			}
			log.info("END: *** getVesselList Result *****" + vesselList.toString());

		} catch (NullPointerException e) {
			log.info("exception getVesselList :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVesselList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselList  DAO  END");
		}

		return vesselList;

	}

	@Override
	public VesselVoyValueObject getVessel(String vesselName, String voyageNbr, String custId) throws BusinessException {
		String custCd = custId;
		log.info("*******Cust Code******[" + CommonUtility.deNull(custId) + "]");
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();

		boolean isShowEsnInfo = false;
		try {

			TextParaVO code = new TextParaVO();
			code.setParaCode(TEXT_PARA_GC_VIEW_ESN);
			TextParaVO result = textParaRepo.getParaCodeInfo(code);
			isShowEsnInfo = isShowEsnInfo(custId, result);
		} catch (Exception e) {

			log.info("Retriving text para error: " + e.getMessage());
			log.info("Exception getVessel : ", e);
			throw new BusinessException("M4201");
		}

		List<VesselVoyValueObject> vesselList = new ArrayList<VesselVoyValueObject>();
		VesselVoyValueObject vesselVoyValueObject = null;
		try {
			log.info("START: getVessel  DAO  Start Obj " + " vesselName:" + CommonUtility.deNull(vesselName) + " voyageNbr:" + CommonUtility.deNull(voyageNbr)
					+ " custId:" + CommonUtility.deNull(custId));

			if (isShowEsnInfo) {

				sb.append("select distinct ves.VV_CD, ves.VSL_NM, ves.OUT_VOY_NBR, ves.SCHEME, ");
				// sb.append(" TO_CHAR(B.ATB_DTTM, 'dd/mm/yyyy HH24MI') as ARRIVAL ,"
				// + " TO_CHAR(B.ATU_DTTM, 'dd/mm/yyyy HH24MI') AS DEPARTURE, "
				sb.append(
						" TO_CHAR(DECODE(SIGN(DECODE(SIGN(VES.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, VES.VSL_BERTH_DTTM,B.ETB_DTTM )- NVL(B.ATB_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VES.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, VES.VSL_BERTH_DTTM,B.ETB_DTTM ),B.ATB_DTTM ), 'dd/mm/yyyy HH24MI') ARRIVAL, ");
				sb.append(
						" TO_CHAR(DECODE(SIGN(DECODE(SIGN(VES.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, VES.VSL_ETD_DTTM,B.ETU_DTTM )- NVL(B.ATU_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VES.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, VES.VSL_ETD_DTTM,B.ETU_DTTM ),B.ATU_DTTM ), 'dd/mm/yyyy HH24MI') DEPARTURE, ");
				sb.append(" TO_CHAR(B.COL_DTTM,'dd/mm/yyyy HH24MI') as COL_DTTM, ");
				sb.append(" TO_CHAR(B.ETB_DTTM,'dd/mm/yyyy HH24MI') as ETB_DTTM ");
				sb.append(" from esn e, vessel_call ves, berthing B  where");
				sb.append(" VSL_NM = :vesselName");
				sb.append(" AND OUT_VOY_NBR = :voyageNbr");
				sb.append(" AND e.trans_type='E' and e.esn_status = 'A' ");
				sb.append(" AND ves.VV_CD = B.VV_CD AND B.SHIFT_IND = 1 ");
				sb.append(" and e.out_voy_var_nbr = ves.vv_cd ");
				sb.append(" ORDER BY VSL_NM,OUT_VOY_NBR");
			} else {
				sb.append("select distinct ves.VV_CD,ves.VSL_NM,ves.OUT_VOY_NBR, ves.SCHEME, ");
				// + " TO_CHAR(B.ATB_DTTM, 'dd/mm/yyyy HH24MI') as ARRIVAL ,"
				// + " TO_CHAR(B.ATU_DTTM, 'dd/mm/yyyy HH24MI') AS DEPARTURE, "
				sb.append(
						" TO_CHAR(DECODE(SIGN(DECODE(SIGN(VES.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, VES.VSL_BERTH_DTTM,B.ETB_DTTM )- NVL(B.ATB_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VES.VSL_BERTH_DTTM- NVL(B.ETB_DTTM, sysdate - 9000)), 1, VES.VSL_BERTH_DTTM,B.ETB_DTTM ),B.ATB_DTTM ), 'dd/mm/yyyy HH24MI') ARRIVAL, ");
				sb.append(
						" TO_CHAR(DECODE(SIGN(DECODE(SIGN(VES.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, VES.VSL_ETD_DTTM,B.ETU_DTTM )- NVL(B.ATU_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VES.VSL_ETD_DTTM- NVL(B.ETU_DTTM, sysdate - 9000)), 1, VES.VSL_ETD_DTTM,B.ETU_DTTM ),B.ATU_DTTM ), 'dd/mm/yyyy HH24MI') DEPARTURE, ");
				sb.append(" TO_CHAR(B.COL_DTTM,'dd/mm/yyyy HH24MI') as COL_DTTM, ");
				sb.append(" TO_CHAR(B.ETB_DTTM,'dd/mm/yyyy HH24MI') as ETB_DTTM ");
				sb.append(" from esn e, vessel_call ves, berthing B  where");
				sb.append(" VSL_NM = :vesselName");
				sb.append(" AND OUT_VOY_NBR = :voyageNbr");
				sb.append(" AND e.trans_type='E'");
				sb.append(" and (e.ESN_CREATE_CD = :custCd OR ves.CREATE_CUST_CD = :custCd)");
				sb.append(" and e.esn_status = 'A' and e.out_voy_var_nbr = ves.vv_cd ");
				sb.append(" AND ves.VV_CD = B.VV_CD AND B.SHIFT_IND = 1 ");
				sb.append(" ORDER BY VSL_NM,OUT_VOY_NBR");
			}

			sql = sb.toString();

			log.info(" *** getVessel SQL *****" + sql);

			if (isShowEsnInfo) {
				paramMap.put("vesselName", vesselName);
				paramMap.put("voyageNbr", voyageNbr);
			} else {
				paramMap.put("vesselName", vesselName);
				paramMap.put("voyageNbr", voyageNbr);
				paramMap.put("custCd", custCd);
			}
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				vesselVoyValueObject = new VesselVoyValueObject();
				vesselVoyValueObject.setVoyNo(CommonUtility.deNull(rs.getString("OUT_VOY_NBR")));
				vesselVoyValueObject.setVslName(CommonUtility.deNull(rs.getString("VSL_NM")));
				vesselVoyValueObject.setVarNbr(CommonUtility.deNull(rs.getString("VV_CD")));
				vesselVoyValueObject.setArrival(CommonUtility.deNull(rs.getString("ARRIVAL")));
				vesselVoyValueObject.setDeparture(CommonUtility.deNull(rs.getString("DEPARTURE")));
				vesselVoyValueObject.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				vesselVoyValueObject.setCol_dttm(CommonUtility.deNull(rs.getString("COL_DTTM")));
				vesselVoyValueObject.setEtb_dttm(CommonUtility.deNull(rs.getString("ETB_DTTM")));
				vesselList.add(vesselVoyValueObject);
				log.info("END: *** getVessel Result *****" + vesselVoyValueObject.toString());
			}

		} catch (NullPointerException e) {
			log.info("exception getVessel :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVessel : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVessel  DAO  END");
		}

		return vesselVoyValueObject;

	}

	@Override
	public VesselVoyValueObject getVesselInfo(String vv_cd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		String sql = "";
		VesselVoyValueObject vessel = new VesselVoyValueObject();

		try {
			log.info("START: getVesselInfo  DAO  Start Obj " + " vv_cd:" + CommonUtility.deNull(vv_cd));

			sql = " select A.*, CC.CO_NM from vessel_call A LEFT JOIN COMPANY_CODE CC ON A.VSL_OPR_CD = CC.CO_CD where A.vv_cd = :vv_cd ";

			log.info(" *** getVesselInfo SQL *****" + sql);
			paramMap.put("vv_cd", vv_cd);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				String vsl_nm = CommonUtility.deNull(rs.getString("VSL_NM"));
				String in_voy_nbr = CommonUtility.deNull(rs.getString("OUT_VOY_NBR"));
				String agent = CommonUtility.deNull(rs.getString("CO_NM"));
				vessel.setVslName(vsl_nm);
				vessel.setVoyNo(in_voy_nbr);
				vessel.setAgent(agent);
			}

			
			log.info("END: *** getVesselInfo Result *****" + vessel.toString());
			return vessel;

		} catch (NullPointerException e) {
			log.info("exception getVesselInfo :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVesselInfo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselInfo  DAO  END");
		}

	}

	@Override
	public List<EsnListValueObject> getEsnList(String selectVoyNo, String custId, Criteria criteria)
			throws BusinessException {
		String selVoyNo = selectVoyNo;
		String custCd = custId;
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		boolean isShowEsnInfo = false;
		
		try {

			TextParaVO code = new TextParaVO();
			code.setParaCode(TEXT_PARA_GC_VIEW_ESN);
			TextParaVO result = textParaRepo.getParaCodeInfo(code);
			isShowEsnInfo = isShowEsnInfo(custId, result);
		} catch (Exception e) {

			log.info("Retriving text para error: " + e.getMessage());
			log.info("Exception getEsnList : ", e);
			throw new BusinessException("M4201");
		}

		if (isShowEsnInfo) {

			sb.append(
					"select DISTINCT esn.esn_asn_nbr,shipper_nm,CRG_TYPE_NM,esnd.NBR_PKGS,esnd.ESN_WT,esnd.ESN_VOL,esn.BK_REF_NBR,esn.STUFF_IND,code.CC_NAME,esnd.CRG_DES,esnd.ESN_HS_CODE,esnd.HS_SUB_CODE_FR, esnd.HS_SUB_CODE_TO, NVL(vsh.SCHEME_CD, VC.SCHEME) SCHEME_CD, esn.EPC_IND, NVL(vsh.SCHEME_CD, VC.COMBI_GC_SCHEME) COMBI_GC_SCHEME_CD, VC.COMBI_GC_OPS_IND, VC.TERMINAL from esn,bk_details bkd,  ");
			sb.append(
					"esn_details esnd, CRG_TYPE , vessel_call VC, CARGO_CATEGORY_CODE code, vessel_scheme vsh where esnd.esn_asn_nbr = esn.esn_asn_nbr and esn_Status = 'A' ");
			sb.append(
					"and esn.out_voy_var_nbr = VC.vv_cd and bkd.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD and bkd.bk_ref_nbr = esn.BK_REF_NBR and ");
			sb.append(" esnd.mixed_scheme_acct_nbr = vsh.acct_nbr(+) and vsh.rec_status(+) = 'A'  ");
			sb.append(
					" and esn.CARGO_CATEGORY_CD = code.CC_CD and esn.OUT_VOY_VAR_NBR=:selVoyNo ORDER BY esn.esn_asn_nbr");

		} else {
			// Amended by Dongsheng on 8/6/2011. The space at the end of the line was
			// mistakenly removed by vendor. SL-OPS-20110608-01
			// sql = "select
			// esn.esn_asn_nbr,shipper_nm,CRG_TYPE_NM,esnd.NBR_PKGS,esnd.ESN_WT,esnd.ESN_VOL,esn.BK_REF_NBR,esn.STUFF_IND,code.CC_NAME
			// ,esnd.CRG_DES" +

			sb.append(
					"select DISTINCT esn.esn_asn_nbr,shipper_nm,CRG_TYPE_NM,esnd.NBR_PKGS,esnd.ESN_WT,esnd.ESN_VOL,esn.BK_REF_NBR,esn.STUFF_IND,code.CC_NAME ,esnd.CRG_DES, esnd.ESN_HS_CODE,esnd.HS_SUB_CODE_FR, esnd.HS_SUB_CODE_TO, NVL(vsh.SCHEME_CD, VC.SCHEME) SCHEME_CD, esn.EPC_IND,NVL(vsh.SCHEME_CD, VC.COMBI_GC_SCHEME) COMBI_GC_SCHEME_CD, VC.COMBI_GC_OPS_IND,VC.TERMINAL ");
			sb.append(
					"from esn,bk_details bkd,esn_details esnd, CRG_TYPE, vessel_call VC,CARGO_CATEGORY_CODE code, vessel_scheme vsh   ");
			sb.append("where esnd.esn_asn_nbr = esn.esn_asn_nbr and esn_Status = 'A' ");
			sb.append("and esn.out_voy_var_nbr = VC.vv_cd ");
			sb.append("and bkd.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD and bkd.bk_ref_nbr = esn.BK_REF_NBR  and ");
			sb.append(" esnd.mixed_scheme_acct_nbr = vsh.acct_nbr(+) and vsh.rec_status(+) = 'A'  ");
			sb.append("and esn.OUT_VOY_VAR_NBR=:selVoyNo");
			sb.append(
					" and esn.CARGO_CATEGORY_CD = code.CC_CD and (esn.ESN_CREATE_CD = :custCd OR VC.CREATE_CUST_CD = :custCd)");
			sb.append(" ORDER BY esn.esn_asn_nbr");

		}

		List<EsnListValueObject> esnList = new ArrayList<EsnListValueObject>();
		EsnListValueObject esnListValueObject = null;
		sql = sb.toString();
		try {
			log.info("START: getEsnList  DAO  Start Obj selectVoyNo: " + CommonUtility.deNull(selectVoyNo) + " custId: " + CommonUtility.deNull(custId)
				+ " criteria: " + CommonUtility.deNull(String.valueOf(criteria)));

			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sb.toString(), criteria.getStart(), criteria.getLimit());
			}

			log.info(" *** getEsnList SQL *****" + sql);

			if (isShowEsnInfo) {
				paramMap.put("selVoyNo", selVoyNo);
			} else {
				paramMap.put("selVoyNo", selVoyNo);
				paramMap.put("custCd", custCd);
			}
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				esnListValueObject = new EsnListValueObject();
				esnListValueObject.setEsnNbr(rs.getLong("ESN_ASN_NBR"));
				esnListValueObject.setBookingRefNo(CommonUtility.deNull(rs.getString("BK_REF_NBR")));
				esnListValueObject.setShipperName(CommonUtility.deNull(rs.getString("shipper_nm")));
				esnListValueObject.setCrgDesc(CommonUtility.deNull(rs.getString("CRG_DES")));
				esnListValueObject.setCrgType(CommonUtility.deNull(rs.getString("CRG_TYPE_NM")));
				esnListValueObject.setNoofPkgs(rs.getInt(4));
				esnListValueObject.setGrWt(rs.getDouble("ESN_WT"));
				esnListValueObject.setGrVolume(rs.getDouble("ESN_VOL"));
				esnListValueObject.setStfInd(CommonUtility.deNull(rs.getString("STUFF_IND"))); // added by vani
				esnListValueObject.setCategory(rs.getString("CC_NAME"));

				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
				esnListValueObject.setHsCode(CommonUtility.deNull(rs.getString("ESN_HS_CODE")));
				esnListValueObject.setHsSubCodeFr(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				esnListValueObject.setHsSubCodeTo(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));
				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

				// haiTTH1 added on 19/3/2014
				esnListValueObject.setScheme(CommonUtility.deNull(rs.getString("SCHEME_CD")));
				// haiTTH1 ended on 19/3/2014

				esnListValueObject.setDeliveryToEPC(CommonUtility.deNull(rs.getString("EPC_IND"))); // MCC for EPC_IND
				esnListValueObject.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME_CD")));
				esnListValueObject.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				esnListValueObject.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				esnList.add(esnListValueObject);
			}
			log.info("END: *** getEsnList Result *****" + esnList.toString());

		} catch (NullPointerException e) {
			log.info("exception getEsnList :", e);
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
	public String getScheme(String out_voyno) throws BusinessException {
		String sql = "";
		String msch = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: getScheme  DAO  Start Obj " + " out_voyno:" + CommonUtility.deNull(out_voyno));

			sql = "SELECT AB_CD FROM VESSEL_CALL VC, VESSEL_SCHEME VS WHERE VC.SCHEME=VS.SCHEME_CD AND VC.VV_CD=:out_voyno";

			log.info(" *** getScheme SQL *****" + sql);
			paramMap.put("out_voyno", out_voyno);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				msch = rs.getString(1);
			}

			log.info("END: *** getScheme Result *****" + msch);
			return msch;

		} catch (NullPointerException e) {
			log.info("exception getScheme :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getScheme : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getScheme  DAO  END");
		}

	}

	@Override
	public String getSchemeInd(String out_voyno) throws BusinessException {
		String sql = "";
		String msch = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getSchemeInd  DAO  Start Obj  out_voyno:" + CommonUtility.deNull(out_voyno));

			sql = "SELECT MIXED_SCHEME_IND FROM VESSEL_CALL WHERE VV_CD=:out_voyno";

			log.info(" *** getSchemeInd SQL *****" + sql);
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
			log.info("exception getSchemeInd :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSchemeInd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSchemeInd  DAO  END");
		}

	}

	@Override
	public String getClsShipInd_bkr(String bkrNbr) throws BusinessException {
		String clsShpInd = "";
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
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
			log.info("exception getClsShipInd_bkr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getClsShipInd_bkr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getClsShipInd_bkr  DAO  END");
		}
		return clsShpInd;

	}
	
	public String getClsShipInd(String varNo) throws BusinessException {
		String clsShpInd = "";
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		sql = "select gb_close_shp_ind from vessel_Call where vv_cd = :varNbr";
		try {
			log.info("START: getClsShipInd  DAO  Start Obj  varNo:" + CommonUtility.deNull(varNo));
			paramMap.put("varNbr", varNo);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				clsShpInd = CommonUtility.deNull(rs.getString("gb_close_shp_ind"));
			}
		} catch (NullPointerException e) {
			log.info("exception getClsShipInd :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getClsShipInd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getClsShipInd  DAO  END result :" + clsShpInd);
		}
		return clsShpInd;
	}

	@Override
	public void AssignCrgvalUpdate(String crgval, String esnnbr, String userId) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sqltlog = "";
		String strInsert_trans = "";
		int stransno = 0;
		int count_trans = 0;

		try {
			log.info("START: AssignCrgvalUpdate  DAO  Start Obj  crgval:" + CommonUtility.deNull(crgval) + " esnnbr:" + CommonUtility.deNull(esnnbr)
					+ " userId:" + CommonUtility.deNull(userId));

			sql = "UPDATE esn SET CARGO_CATEGORY_CD =:crgval,LAST_MODIFY_USER_ID=:userId,LAST_MODIFY_DTTM = sysdate WHERE ESN_ASN_NBR =:esnnbr";

			log.info(" *** AssignCrgvalUpdate SQL *****" + sql);

			paramMap.put("crgval", crgval);
			paramMap.put("userId", userId);
			paramMap.put("esnnbr", esnnbr);
			log.info(" paramMap: " + paramMap);
			int count = namedParameterJdbcTemplate.update(sql, paramMap);
			log.info("AssignCrgvalUpdate sql (update esn) count: " + count);
			sqltlog = "SELECT MAX(TRANS_NBR) FROM ESN_TRANS WHERE ESN_ASN_NBR='" + esnnbr + "'";

			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003

				log.info(" *** AssignCrgvalUpdate SQL *****" + sqltlog);
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqltlog, paramMap);
				if (rs.next()) {
					stransno = (rs.getInt(1)) + 1;
				} else {
					stransno = 0;
				}
			}
			StringBuffer sb1 = new StringBuffer();
			sb1.append("INSERT INTO ESN_TRANS(TRANS_NBR,ESN_ASN_NBR, ");
			sb1.append("CARGO_CATEGORY_CD,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
			sb1.append("VALUES(:stransno ,:esnnbr,:crgval,:userId,sysdate)");

			strInsert_trans = sb1.toString();

			if (logStatusGlobal.equalsIgnoreCase("Y")) {

				log.info(" *** AssignCrgvalUpdate SQL *****" + strInsert_trans);
				paramMap.put("stransno", Integer.toString(stransno));
				paramMap.put("esnnbr", esnnbr);
				paramMap.put("crgval", crgval);
				paramMap.put("userId", userId);
				
				log.info(" paramMap: " + paramMap);
				count_trans = namedParameterJdbcTemplate.update(strInsert_trans, paramMap);
				log.info("AssignCrgvalUpdate sql (insert into esn_trans) count_trans: " + count_trans);
			}

			if (count == 0) {
				log.info("Writing from EsnEJB.AssignCrgvalUpdate");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 21/01/2003
				if (count_trans == 0) {
					log.info("Writing from EsnEJB.AssignCrgvalUpdate");
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}
			}

			log.info("END: *** AssignCrgvalUpdate Result *****" + count);

		} catch (NullPointerException e) {
			log.info("exception AssignCrgvalUpdate :", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception AssignCrgvalUpdate : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception AssignCrgvalUpdate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: AssignCrgvalUpdate  DAO  END");
		}

	}

	@Override
	public String AssignWhindCheck(String esnnbr) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String crgCd = "";

		try {
			log.info("START: AssignWhindCheck  DAO  Start Obj " + " esnnbr:" + CommonUtility.deNull(esnnbr));

			sql = "SELECT WH_IND from esn WHERE ESN_ASN_NBR =:esnnbr";

			log.info(" *** AssignWhindCheck SQL *****" + sql);

			paramMap.put("esnnbr", esnnbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				crgCd = rs.getString("WH_IND");
			}

			log.info("END: *** AssignWhindCheck Result *****" + crgCd.toString());
			return crgCd;

		} catch (NullPointerException e) {
			log.info("exception AssignWhindCheck :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception AssignWhindCheck : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: AssignWhindCheck  DAO  END");
		}
	}

	@Override
	public List<String> getWHDetails(String esnWhindcheck, String esnnbr) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		List<String> whIndDetails = new ArrayList<String>();
		String remarks = "";
		String nodays = "";
		String whappnbr = "";

		try {
			log.info("START: getWHDetails  DAO  Start Obj " + " esnWhindcheck:" + CommonUtility.deNull(esnWhindcheck) + " esnnbr:" + CommonUtility.deNull(esnnbr));

			if (esnWhindcheck != null && !esnWhindcheck.equals("") && esnWhindcheck.equals("Y"))
				sql = "SELECT WH_REMARKS,WH_AGGR_NBR from esn WHERE ESN_ASN_NBR =:esnnbr";

			else
				sql = "SELECT FREE_STG_DAYS from esn WHERE ESN_ASN_NBR =:esnnbr";

			log.info(" *** getWHDetails SQL *****" + sql);
			paramMap.put("esnnbr", esnnbr);
			log.info(" paramMap: " + paramMap);
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
			return whIndDetails;

		} catch (NullPointerException e) {
			log.info("exception getWHDetails :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getWHDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getWHDetails  DAO  END");
		}

	}

	@Override
	public String AssignCrgvalCheck(String esnnbr) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String crgCd = "";

		sql = "SELECT CARGO_CATEGORY_CD from esn WHERE ESN_ASN_NBR =:esnnbr";

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
			log.info("exception AssignCrgvalCheck :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception AssignCrgvalCheck : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: AssignCrgvalCheck  DAO  END");
		}

	}

	@Override
	public List<EsnListValueObject> getAssignCargo() throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String cc_cd = "";
		String cicos_cd = "";
		String cc_name = "";
		List<EsnListValueObject> maniveclist = new ArrayList<EsnListValueObject>();
		EsnListValueObject esnListValueObject = null;

		// Added in by Linus on 08 Oct 2003
		sql = "SELECT cc_cd, cicos_cd,cc_name FROM cargo_category_code WHERE cc_status='A'";

		// Before
		// sql = "SELECT cc_cd, cicos_cd,cc_name FROM cargo_category_code";
		// End Change
		try {
			log.info("START: getAssignCargo  DAO  Start Obj ");

			log.info(" *** getAssignCargo SQL *****" + sql);
			
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				esnListValueObject = new EsnListValueObject();
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
			log.info("exception getAssignCargo :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getAssignCargo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAssignCargo  DAO  END");
		}

	}

	@Override
	public boolean checkExistSubAdp(String esnNo) throws BusinessException {

		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		boolean exists = false;

		try {
			log.info("START: checkExistSubAdp  DAO  Start Obj esnNo: " + CommonUtility.deNull(esnNo));

			String sql = "SELECT * FROM SUB_ADP WHERE STATUS_CD = 'A' AND ESN_ASN_NBR = :esnNo";

			log.info(" *** checkExistSubAdp SQL *****" + sql);
			paramMap.put("esnNo", esnNo);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				exists = true;
			} else {
				log.info("SUBADP does not exist");
				exists = false;
			}
			log.info("END: *** checkExistSubAdp Result *****" + exists);

		} catch (NullPointerException e) {
			log.info("exception checkExistSubAdp :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkExistSubAdp : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkExistSubAdp  DAO  END");
		}

		return exists;
	}

	@Override
	public boolean isEsnCreator(String esnAsnNbr, String esnCreateCd) throws BusinessException {
		boolean isEsnCreator = false;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();

		try {
			log.info(
					"START: isEsnCreator  DAO  Start Obj " + " esnAsnNbr:" + CommonUtility.deNull(esnAsnNbr) + " esnCreateCd:" + CommonUtility.deNull(esnCreateCd));

			sql.append(" SELECT ESN_ASN_NBR");
			sql.append("   FROM ESN");
			sql.append("  WHERE ESN_ASN_NBR = :esnAsnNbr ");
			sql.append("    AND ESN_CREATE_CD = :esnCreateCd ");

			log.info(" *** isEsnCreator SQL *****" + sql.toString());
			paramMap.put("esnAsnNbr", esnAsnNbr);
			paramMap.put("esnCreateCd", esnCreateCd);
			log.info(" paramMap: " + paramMap);
			SqlRowSet resultSet = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			if (resultSet.next()) {
				isEsnCreator = true;
			}

			log.info("END: *** isEsnCreator Result *****" + isEsnCreator);

		} catch (NullPointerException e) {
			log.info("exception isEsnCreator :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isEsnCreator : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isEsnCreator  DAO  END");
		}

		return isEsnCreator;
	}

	@Override
	public List<EsnListValueObject> getBkRefNo(String bkRefNo, String cutId) throws BusinessException {
		String bookingRefNo = "";
		String custId = "";
		bookingRefNo = bkRefNo;
		custId = cutId;
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		List<EsnListValueObject> bookingRef = new ArrayList<EsnListValueObject>();
		EsnListValueObject esnListValueObject = null;
		String portDisDesc = "";
		try {
			log.info("START: getBkRefNo  DAO  Start Obj " + " bkRefNo:" + CommonUtility.deNull(bkRefNo) + " cutId:" + CommonUtility.deNull(cutId));

			if (custId.equals("JP")) {
				// sql = "select
				// BK_REF_NBR,BKD.OUT_VOY_NBR,CNTR_SIZE,CNTR_TYPE,CRG_TYPE_NM,SHIPPER_NM,PORT_DIS,DECLARANT_CD,VSL_NM,NBR_OF_CNTR,VAR_NBR,BK_WT,BK_VOL,BK_NBR_PKGS,VARIANCE_PKGS,VARIANCE_VOL,VARIANCE_WT,VSL.gb_close_shp_ind,
				// VSL.SCHEME from " +

				sb.append(
						"select BK_REF_NBR,BKD.OUT_VOY_NBR,CNTR_SIZE,CNTR_TYPE,CRG_TYPE_NM, CARGO_CATEGORY_CD, SHIPPER_NM,PORT_DIS,DECLARANT_CD,VSL_NM,NBR_OF_CNTR,VAR_NBR,BK_WT,BK_VOL,BK_NBR_PKGS,VARIANCE_PKGS,VARIANCE_VOL,VARIANCE_WT,VSL.gb_close_shp_ind, VSL.SCHEME from ");
				sb.append("bk_details bkd,VESSEL_CALL VSL,CRG_TYPE where bk_ref_nbr = :bookingRefNo ");
				sb.append("AND VSL.VV_CD = BKD.VAR_NBR AND  BKD.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD ");

				sql = sb.toString();
			} else {
				// sql = "select
				// BK_REF_NBR,BKD.OUT_VOY_NBR,CNTR_SIZE,CNTR_TYPE,CRG_TYPE_NM,SHIPPER_NM,PORT_DIS,DECLARANT_CD,VSL_NM,NBR_OF_CNTR,VAR_NBR,BK_WT,BK_VOL,BK_NBR_PKGS,VARIANCE_PKGS,VARIANCE_VOL,VARIANCE_WT,VSL.gb_close_shp_ind,
				// VSL.SCHEME from " +

				sb.append(
						"select BK_REF_NBR,BKD.OUT_VOY_NBR,CNTR_SIZE,CNTR_TYPE,CRG_TYPE_NM, CARGO_CATEGORY_CD, SHIPPER_NM,PORT_DIS,DECLARANT_CD,VSL_NM,NBR_OF_CNTR,VAR_NBR,BK_WT,BK_VOL,BK_NBR_PKGS,VARIANCE_PKGS,VARIANCE_VOL,VARIANCE_WT,VSL.gb_close_shp_ind, VSL.SCHEME from ");
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
			
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				// bkRefNo_flag = true;
				esnListValueObject = new EsnListValueObject();
				esnListValueObject.setVoyNo(CommonUtility.deNull(rs.getString("OUT_VOY_NBR")));
				esnListValueObject.setVslName(CommonUtility.deNull(rs.getString("vsl_nm")));
				esnListValueObject.setVarNbr(CommonUtility.deNull(rs.getString("VAR_NBR")));
				esnListValueObject.setBookingRefNo(CommonUtility.deNull(rs.getString("BK_REF_NBR")));
				esnListValueObject.setCrgType(CommonUtility.deNull(rs.getString("CRG_TYPE_NM")));
				esnListValueObject.setCategory(CommonUtility.deNull(rs.getString("CARGO_CATEGORY_CD")));
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
				// HaiTTH1 added on 10/1/2014
				esnListValueObject.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				bookingRef.add(esnListValueObject);
				// log.info("Db Value :"+rs.getString("esn_nbr"));
			}
			log.info("END: *** getBkRefNo Result *****" + bookingRef.toString());

		} catch (NullPointerException e) {
			log.info("exception getBkRefNo :", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception getBkRefNo : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBkRefNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBkRefNo  DAO  END");
		}

		return bookingRef;
	}

	@Override
	public boolean checkDisbaleOverSideFroDPE(String varno) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = "";
		boolean checkResult = false;
		try {
			log.info("START: checkDisbaleOverSideFroDPE  DAO  Start Obj " + " varno:" + CommonUtility.deNull(varno));

			sql = "SELECT COUNT(*) C FROM VESSEL_CALL WHERE SCHEME IN ('JBT', 'JCL', 'JWP') AND  VV_CD = :varno ";

			log.info(" *** checkDisbaleOverSideFroDPE SQL *****" + sql);
			paramMap.put("varno", varno);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			rs.next();
			if (rs.getInt("C") > 0) {
				checkResult = true;
			} else {
				checkResult = false;
			}
			log.info("END: *** checkDisbaleOverSideFroDPE Result *****" + checkResult);

		} catch (NullPointerException e) {
			log.info("exception checkDisbaleOverSideFroDPE :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkDisbaleOverSideFroDPE : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkDisbaleOverSideFroDPE  DAO  END");
		}

		return checkResult;
	}

	@Override
	public int getUaNoPkgs(String esnNo) throws BusinessException {
		String esnNbr = esnNo;
		int uaNoPkgs = 0;
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		sql = "select ua_nbr_pkgs from esn_Details where esn_asn_nbr =:esnNbr";

		try {
			log.info("START: getUaNoPkgs  DAO  Start Obj " + " esnNo:" + CommonUtility.deNull(esnNo));

			log.info(" *** getUaNoPkgs SQL *****" + sql);
			paramMap.put("esnNbr", esnNbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				uaNoPkgs = rs.getInt("ua_nbr_pkgs");
			}
			log.info("END: *** getUaNoPkgs Result *****" + uaNoPkgs);

		} catch (NullPointerException e) {
			log.info("exception getUaNoPkgs :", e);
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
	public boolean isBillChargesRaised(String esnNo) throws BusinessException {
		// call processGBLog.cancelBillableCharges(usNbr, 'UA'); if true then cancel ua
		// else error message(bill raised)
		String uanbr = null;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		boolean result = false;
		boolean temp = false;
		try {
			log.info("START: isBillChargesRaised  DAO  Start Obj " + " esnNo:" + CommonUtility.deNull(esnNo));

			String sql = "select ua_nbr from ua_details where ua_status='A' and esn_asn_nbr=:esnNo and (bill_service_triggered_ind='Y' or bill_wharf_triggered_ind='Y')";

			log.info(" *** isBillChargesRaised SQL *****" + sql);

			paramMap.put("esnNo", esnNo);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				uanbr = rs.getString(1);
			}

			if (uanbr != null && !"".equals(uanbr)) {
				log.info("[Before  processgblog UA,ESN]==>" + uanbr + "," + esnNo);

				temp = processGBLogRepo.cancelBillableCharges(uanbr, "UA");
				result = (!temp);
				log.info("[After  processgblog UA,ESN,(!processgblog.cancelBillableCharges())]==>" + uanbr + "," + esnNo
						+ "," + result);
			}
			log.info("END: *** isBillChargesRaised Result *****" + result);

		} catch (NullPointerException e) {
			log.info("exception isBillChargesRaised :", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception isBillChargesRaised : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isBillChargesRaised : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: isBillChargesRaised  DAO  END");
		}

		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.cargo.esn-->EsnEJB--->getPkgsDesc()

	@Override
	public String getPkgsDesc(String esnNbr) throws BusinessException {
		String esnNo = esnNbr;
		String sql = "";
		String pkgsDesc = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: getPkgsDesc  DAO  Start Obj " + " esnNbr:" + CommonUtility.deNull(esnNbr));

			sql = "select PKG_TYPE_CD,PKG_DESC from PKG_TYPES,esn_details,esn where PKG_TYPES.PKG_TYPE_CD = esn_details.PKG_TYPE and esn.esn_asn_nbr = esn_details.esn_asn_nbr and esn.ESN_ASN_NBR =:esnNo ";

			log.info(" *** getPkgsDesc SQL *****" + sql);
			paramMap.put("esnNo", esnNo);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				pkgsDesc = CommonUtility.deNull(rs.getString("PKG_DESC"));
			}
			log.info("END: *** getPkgsDesc Result *****" + pkgsDesc.toString());

		} catch (NullPointerException e) {
			log.info("exception getPkgsDesc :", e);
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
	public String getBPacctnbr(String esno, String voy_nbr) throws BusinessException {
		String sql = "";
		String acctnbr = "";
		String scheme = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		sql = "SELECT MIXED_SCHEME_ACCT_NBR FROM ESN_DETAILS WHERE ESN_ASN_NBR=:esno ";

		try {
			log.info("START: getBPacctnbr  DAO  Start Obj esno: " + CommonUtility.deNull(esno) + " voy_nbr: " + CommonUtility.deNull(voy_nbr));

			log.info(" *** getBPacctnbr SQL *****" + sql);
			paramMap.put("esno", esno);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				acctnbr = rs.getString(1);
			}
			if (acctnbr != null && !acctnbr.equals("") && !acctnbr.equals("null")) {
				log.info("acctnbr" + acctnbr.toString());
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

			log.info("END: *** getBPacctnbr Result *****");
			return acctnbr;

		} catch (NullPointerException e) {
			log.info("exception getBPacctnbr :", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception getBPacctnbr : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBPacctnbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBPacctnbr  DAO  END");
		}
	}

	@Override
	public String getABactnbr(String voy_nbr) throws BusinessException {
		String sql = "";
		String bactnbr = null;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: getABactnbr  DAO  Start Obj " + " voy_nbr:" + CommonUtility.deNull(voy_nbr));

			sql = "SELECT VS.ACCT_NBR FROM VESSEL_CALL VC,VESSEL_SCHEME VS WHERE VC.SCHEME=VS.SCHEME_CD AND VC.VV_CD =:voy_nbr ";

			log.info(" *** getABactnbr SQL *****" + sql);
			paramMap.put("voy_nbr", voy_nbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				bactnbr = rs.getString(1);
			}

			log.info("END: *** getABactnbr Result *****" + CommonUtility.deNull(bactnbr));
			return bactnbr;

		} catch (Exception e) {
			log.info("Exception getABactnbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getABactnbr  DAO  END");
		}

	}

	@Override
	public String getVCactnbr(String voy_nbr) throws BusinessException {
		String sql = "";
		String bactnbr = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

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

			log.info("END: *** getVCactnbr Result *****" + bactnbr.toString());
			return bactnbr;

		} catch (NullPointerException e) {
			log.info("exception getVCactnbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVCactnbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVCactnbr  DAO  END");
		}

	}

	@Override
	public String getSchemeName(String voy_nbr) throws BusinessException {
		String sql = "";
		String sch = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		sql = "SELECT SCHEME FROM VESSEL_CALL WHERE VV_CD=:voy_nbr ";

		try {
			log.info("START: getSchemeName  DAO  Start Obj " + " voy_nbr:" + CommonUtility.deNull(voy_nbr));

			log.info(" *** getSchemeName SQL *****" + sql);
			paramMap.put("voy_nbr", voy_nbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				sch = rs.getString(1);
			}

			log.info("END: *** getSchemeName Result *****" + sch.toString());
			return sch;

		} catch (NullPointerException e) {
			log.info("exception getSchemeName :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSchemeName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSchemeName  DAO  END");
		}

	}

	@Override
	public List<EsnListValueObject> getABacctnoForSA(String out_voyno) throws BusinessException {
		String sql = "";
		List<EsnListValueObject> vacctno = new ArrayList<EsnListValueObject>();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: getABacctnoForSA  DAO  Start Obj " + " out_voyno:" + CommonUtility.deNull(out_voyno));

			sql = "SELECT VSL.SCHEME_CD, VSL.ACCT_NBR FROM VESSEL_SCHEME VSL, NOMINATED_SCHEME NOM WHERE VSL.SCHEME_CD = NOM.SCHEME_CD AND NOMINATE_STATUS = 'APP' AND AB_CD IS NOT NULL AND NOM.VV_CD=:out_voyno ";

			log.info(" *** getABacctnoForSA SQL *****" + sql);

			paramMap.put("out_voyno", out_voyno);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			EsnListValueObject esnListValueObject = null;
			while (rs.next()) {

				esnListValueObject = new EsnListValueObject();
				esnListValueObject.setCc_cd("" + rs.getString(1));
				esnListValueObject.setCc_name("" + rs.getString(2));
				vacctno.add(esnListValueObject);
			}

			log.info("END: *** getABacctnoForSA Result *****" + vacctno.toString());
			return vacctno;

		} catch (NullPointerException e) {
			log.info("exception getABacctnoForSA :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getABacctnoForSA : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getABacctnoForSA  DAO  END");
		}

	}

	@Override
	public List<EsnListValueObject> getABacctno(String out_voyno) throws BusinessException {
		String sql = "";
		List<EsnListValueObject> vacctno = new ArrayList<EsnListValueObject>();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		/*
		 * sql= "SELECT A.ACCT_NBR FROM CUST_ACCT A, VESSEL_CALL V ,VESSEL_SCHEME VS"
		 * +" WHERE VS.SCHEME_CD=V.SCHEME AND VS.AB_CD = A.CUST_CD AND A.BUSINESS_TYPE LIKE '%G%' AND "
		 * +" A.ACCT_NBR IS NOT NULL AND A.ACCT_STATUS_CD='A' AND A.TRIAL_IND='N' AND"
		 * +" V.OUT_VOY_NBR = '"+out_voyno+"' ORDER BY A.ACCT_NBR";
		 */

		try {
			log.info("START: getABacctno  DAO  Start Obj " + " out_voyno:" + CommonUtility.deNull(out_voyno));

			sql = "SELECT SCHEME_CD,ACCT_NBR FROM VESSEL_SCHEME WHERE AB_CD IS NOT NULL";

			log.info(" *** getABacctno SQL *****" + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			EsnListValueObject esnListValueObject = null;
			while (rs.next()) {

				esnListValueObject = new EsnListValueObject();
				esnListValueObject.setCc_cd("" + rs.getString(1));
				esnListValueObject.setCc_name("" + rs.getString(2));
				vacctno.add(esnListValueObject);
			}

			log.info("END: *** getABacctno Result *****" + vacctno.toString());
			return vacctno;

		} catch (NullPointerException e) {
			log.info("exception getABacctno :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getABacctno : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getABacctno  DAO  END");
		}

	}

	@Override
	public List<String> getSAacctno(String vv_cd) throws BusinessException {
		String sql = "";
		List<String> vacctno = new ArrayList<String>();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		/*
		 * sql= "SELECT A.ACCT_NBR FROM CUST_ACCT A, VESSEL_CALL V "
		 * +" WHERE A.BUSINESS_TYPE LIKE '%G%' AND"
		 * +" A.ACCT_NBR IS NOT NULL AND A.ACCT_STATUS_CD='A' AND A.TRIAL_IND='N' AND"
		 * +" V.CREATE_CUST_CD = A.CUST_CD AND V.OUT_VOY_NBR = '"+out_voyno+"'"
		 * +" ORDER BY A.ACCT_NBR";
		 */

		try {
			log.info("START: getSAacctno  DAO  Start Obj " + " vv_cd:" + CommonUtility.deNull(vv_cd));

			sql = "SELECT BILL_ACCT_NBR FROM VESSEL_CALL WHERE VV_CD =:vv_cd ";

			log.info(" *** getSAacctno SQL *****" + sql);
			paramMap.put("vv_cd", vv_cd);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				vacctno.add("" + rs.getString(1));
			}

			log.info("END: *** getSAacctno Result *****" + vacctno.toString());
			return vacctno;

		} catch (NullPointerException e) {
			log.info("exception getSAacctno :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getSAacctno : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSAacctno  DAO  END");
		}

	}

	@Override
	public boolean checkDeleteEsn(String edoEsnno) throws BusinessException {
		String sql = "";
		boolean result = true;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		sql = " select count(*) from wa_appln_details where edo_esn_nbr = :edoEsnno and purp_cd='EX'  and rec_status not in ('R','X') ";

		try {
			log.info("START: checkDeleteEsn  DAO  Start Obj " + " edoEsnno:" + CommonUtility.deNull(edoEsnno));

			log.info(" *** checkDeleteEsn SQL *****" + sql);

			paramMap.put("edoEsnno", edoEsnno);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) {
					result = false;
				}
			} else {
				result = true;
			}

			log.info("END: *** checkDeleteEsn Result *****" + result);

		} catch (NullPointerException e) {
			log.info("exception checkDeleteEsn :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkDeleteEsn : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkDeleteEsn  DAO  END");
		}

		return result;
	}

	@Override
	public void EsnAssignVslUpdate(String vv_cd, String status, String userId) throws BusinessException {
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: EsnAssignVslUpdate  DAO  Start Obj " + " vv_cd:" + CommonUtility.deNull(vv_cd) + " status:" + CommonUtility.deNull(status) + " userId:"
					+ CommonUtility.deNull(userId));

			sql = "UPDATE vessel_call SET mixed_scheme_ind=:status,LAST_MODIFY_DTTM=sysdate ,LAST_MODIFY_USER_ID=:userId WHERE vv_cd =:vv_cd";

			log.info(" *** EsnAssignVslUpdate SQL *****" + sql);
			paramMap.put("status", status);
			paramMap.put("userId", userId);
			paramMap.put("vv_cd", vv_cd);
			log.info(" paramMap: " + paramMap);
			int count = namedParameterJdbcTemplate.update(sql, paramMap);

			// log.info("sql count "+count);

			if (count == 0) {
				log.info("Writing from EsnEJB.EsnAssignVslUpdate");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			log.info("END: *** EsnAssignVslUpdate Result *****" + count);

		} catch (NullPointerException e) {
			log.info("exception EsnAssignVslUpdate :", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception EsnAssignVslUpdate : ", e);
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
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();

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

		sql = "UPDATE ESN_DETAILS SET MIXED_SCHEME_ACCT_NBR=:acctnbr WHERE  ESN_ASN_NBR =:esno ";

		try {
			log.info("START: EsnAssignBillUpdate  DAO  Start Obj " + " acctnbr:" + CommonUtility.deNull(acctnbr) + " esno:" + CommonUtility.deNull(esno)
					+ " userid:" + CommonUtility.deNull(userid));

			log.info(" *** EsnAssignBillUpdate SQL *****" + sql);
			paramMap.put("acctnbr", acctnbr);
			paramMap.put("esno", esno);
			log.info(" paramMap: " + paramMap);
			int count = namedParameterJdbcTemplate.update(sql, paramMap);

			sqltlog = "SELECT MAX(TRANS_NBR) FROM ESN_DETAILS_TRANS WHERE ESN_ASN_NBR=:esno ";

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

			sb = new StringBuffer();
			sb.append("INSERT INTO ESN_DETAILS_TRANS(TRANS_NBR,ESN_ASN_NBR,");
			sb.append("MIXED_SCHEME_ACCT_NBR,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
			sb.append("VALUES(:stransno,:esno,:acctnbr,:userid,sysdate)");

			strInsert_trans = sb.toString();

			if (logStatusGlobal.equalsIgnoreCase("Y")) {
				log.info(" *** EsnAssignBillUpdate SQL *****" + strInsert_trans);

				paramMap.put("stransno", Integer.toString(stransno));
				paramMap.put("esno", esno);
				paramMap.put("acctnbr", acctnbr);
				paramMap.put("userid", userid);
				log.info(" paramMap: " + paramMap);

				count_trans = namedParameterJdbcTemplate.update(strInsert_trans, paramMap);
			}

			if (count == 0) {
				log.info("Writing from EsnEJB.EsnAssignBillUpdate");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}
			if (logStatusGlobal.equalsIgnoreCase("Y")) { // Transaction Log Table Insertion 22/01/2003
				if (count_trans == 0) {
					log.info("Writing from EsnEJB.EsnAssignBillUpdate");
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}
			}
			log.info("END: *** EsnAssignBillUpdate Result *****");

		} catch (NullPointerException e) {
			log.info("exception EsnAssignBillUpdate :", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception EsnAssignBillUpdate : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception EsnAssignBillUpdate : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: EsnAssignBillUpdate  DAO  END");
		}

	}

	private boolean checkAccountNbr(String accnbr) throws BusinessException {

		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		String sql = "";
		String straccnbrcount = "";
		try {
			log.info("START: checkAccountNbr  DAO  Start Obj " + " accnbr:" + CommonUtility.deNull(accnbr));

			sb.append("SELECT COUNT(*) FROM CUST_ACCT A, COMPANY_CODE B, ");
			sb.append("CUSTOMER C  WHERE A.BUSINESS_TYPE LIKE '%G%' AND ");
			sb.append("A.ACCT_NBR IS NOT NULL AND B.CO_CD=C.CUST_CD  AND ");
			sb.append("A.acct_status_cd='A' and UPPER(A.ACCT_NBR)=UPPER(:accnbr)");
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

			if (intaccnbrcount > 0) {
				return true;
			} else {
				return false;
			}

		} catch (NullPointerException e) {
			log.info("exception checkAccountNbr :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkAccountNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkAccountNbr  DAO  END");
		}

	}

	@Override
	public void esnCancel(String esnNo, String bookingRefno, String strUAFlag, String strUserID)
			throws BusinessException { // added strUAFlag by vani -- 21 Oct,03
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rsUA = null;
		SqlRowSet rs1 = null;
		String strUpdate = new String();
		String strBkdetails = new String();
		String strCnacelUASql = "";

		// Added by Revathi

		String strUADetailsTransSQL = new String();
		String strUANbrSql = new String();
		String strUATransNbrSql = new String();
		String strUATransNbr = new String();
		int intUATransNbr = 0;
		String strTransNum = "";
		String transNumEsnStr = "";
		int transNumEsnInt = 0;
		String strBKDetailsTrans = new String();
		String strBKTrans = new String();
		int transNumBKInt = 0;
		String strInsertTrans = new String();
		String strEsnNo = "";
		// added by vani -- to cancel UA's related to the given esnNo start -- 21 Oct,03
		if (!strUAFlag.equals("") && strUAFlag != null && strUAFlag.equals("UA")) {

			if (esnNo.length() == 7)
				strEsnNo = "0" + esnNo;
			// added by Irene Tan on 24 Jun 04 : SL-GBMS-20040621-1
			else
				strEsnNo = esnNo;
			// end added by Irene Tan on 24 Jun 04 : SL-GBMS-20040621-1

			strCnacelUASql = "UPDATE ua_details SET ua_status='X',LAST_MODIFY_DTTM=SYSDATE,LAST_MODIFY_USER_ID=:strUserID  WHERE UA_NBR LIKE :strEsnNo and ua_status='A'";
			// end Changed by Irene Tan on 05 July 2004
		} // added by vani -- to cancel UA's related to the given esnNo end -- 21 Oct,03

		strUpdate = "UPDATE ESN SET ESN_STATUS='X',LAST_MODIFY_DTTM=SYSDATE,LAST_MODIFY_USER_ID=:strUserID WHERE ESN_ASN_NBR= :esnNo";
		strBkdetails = "UPDATE bk_details SET ESN_DECLARED='N',LAST_MODIFY_DTTM=SYSDATE,LAST_MODIFY_USER_ID=:strUserID WHERE BK_REF_NBR=:bookingRefno ";

		try {
			log.info("START: esnCancel  DAO  Start Obj " + " esnNo:" + CommonUtility.deNull(esnNo) + " bookingRefno:" + CommonUtility.deNull(bookingRefno)
					+ " strUAFlag:" + CommonUtility.deNull(strUAFlag) + " strUserID:" + CommonUtility.deNull(strUserID));

			// added by vani -- to cancel UA's related to the given esnNo start -- 21 Oct,03
			int cntCancelUA = 0;
			if (!strUAFlag.equals("") && strUAFlag != null && strUAFlag.equals("UA")) {
				// added by Revathi
				if (logStatusGlobal.equalsIgnoreCase("Y")) {

					strUANbrSql = "SELECT UA_NBR FROM UA_DETAILS WHERE ESN_ASN_NBR =:esnNo and ua_status='A'";
					// end changed by Irene Tan on 05 July 2004

					log.info(" *** esnCancel SQL *****" + strUANbrSql);
					paramMap.put("esnNo", esnNo);
					log.info(" paramMap: " + paramMap);
					rsUA = namedParameterJdbcTemplate.queryForRowSet(strUANbrSql, paramMap);

					// changed by Irene Tan on 05 July 2004 : to rectify the logging of multiple
					// ua_details_trans
					List<String> uaList = new ArrayList<String>();
					int uaCtr = 0;
					while (rsUA.next()) {
						String uaNo = rsUA.getString("ua_nbr");
						// log.info("UA No: " + uaNo);
						uaList.add(uaCtr++, uaNo);
					}

					for (int i = 0; i < uaList.size(); i++) {
						// while(rsUA.next()){
						// strUATransNbr = rsUA.getString(1);
						// end changed by Irene Tan on 05 July 2004
						strUATransNbr = (String) uaList.get(i);
						strUATransNbrSql = "SELECT MAX(TRANS_NBR) FROM UA_DETAILS_TRANS WHERE UA_NBR =:strUATransNbr ";

						log.info(" *** esnCancel SQL *****" + strUATransNbrSql);
						paramMap.put("strUATransNbr", strUATransNbr);
						log.info(" paramMap: " + paramMap);
						rs1 = namedParameterJdbcTemplate.queryForRowSet(strUATransNbrSql, paramMap);
						while (rs1.next()) {
							strTransNum = rs1.getString(1);
						}

						if (strTransNum == null || strTransNum == "") {
							intUATransNbr = 0;
						} else {
							intUATransNbr = Integer.parseInt(strTransNum);
							intUATransNbr++;
						}

						strUADetailsTransSQL = "INSERT INTO UA_DETAILS_TRANS (UA_NBR,TRANS_NBR,UA_STATUS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) VALUES  (:strUATransNbr,:intUATransNbr,'X',:strUserID,SYSDATE)";

						log.info(" *** esnCancel SQL *****" + strUADetailsTransSQL);

						paramMap.put("strUATransNbr", strUATransNbr);
						paramMap.put("intUATransNbr", Integer.toString(intUATransNbr));
						paramMap.put("strUserID", strUserID);
						log.info(" paramMap: " + paramMap);
						namedParameterJdbcTemplate.update(strUADetailsTransSQL, paramMap);
					}
				}
				// end of add by Revathi

				log.info(" *** esnCancel SQL *****" + strCnacelUASql);
				paramMap.put("strUserID", strUserID);
				paramMap.put("strEsnNo", "U" + strEsnNo + "%");
				log.info(" paramMap: " + paramMap);
				cntCancelUA = namedParameterJdbcTemplate.update(strCnacelUASql, paramMap);
			} // added by vani -- to cancel UA's related to the given esnNo end -- 21 Oct,03

			// Added by Revathi
			// updating for ESN_TRANS table
			if (logStatusGlobal.equalsIgnoreCase("Y")) {

				// updating UA_DETAILS_TRANS table
				String sqlTrans = "SELECT MAX(TRANS_NBR) FROM ESN_TRANS WHERE ESN_ASN_NBR = :esnNo ";

				log.info(" *** esnCancel SQL *****" + sqlTrans);
				paramMap.put("esnNo", esnNo);
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
				StringBuffer sb1 = new StringBuffer();
				sb1.append(
						"INSERT INTO ESN_Trans(ESN_ASN_NBR,TRANS_NBR, ESN_STATUS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) ");
				sb1.append("VALUES(:esnNo,:transNumEsnInt,'X',:strUserID,sysdate)");

				strInsertTrans = sb1.toString();

				// updating BK_Details_Trans Table
				rs = null;

				strBKTrans = "SELECT MAX(TRANS_NBR) FROM BK_DETAILS_TRANS WHERE BK_REF_NBR=:bookingRefno ";

				log.info(" *** esnCancel SQL *****" + strBKTrans);
				paramMap.put("bookingRefno", bookingRefno);
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(strBKTrans, paramMap);
				while (rs.next()) {
					strBKTrans = rs.getString(1);
				}

				if (strBKTrans == null || strBKTrans == "") {
					transNumBKInt = 0;
				} else {
					transNumBKInt = Integer.parseInt(strBKTrans);
					transNumBKInt++;
				}
				strBKDetailsTrans = "INSERT INTO BK_DETAILS_TRANS(TRANS_NBR,BK_REF_NBR,ESN_DECLARED,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) VALUES(:transNumBKInt,:bookingRefno,'N',:strUserID,sysdate)";
				// end of Add by Revathi

				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y") {

					log.info(" *** esnCancel SQL *****" + strInsertTrans);

					paramMap.put("esnNo", esnNo);
					paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
					paramMap.put("strUserID", strUserID);
					log.info(" paramMap: " + paramMap);

					namedParameterJdbcTemplate.update(strInsertTrans, paramMap);

					log.info(" *** esnCancel SQL *****" + strBKDetailsTrans);

					paramMap.put("transNumBKInt", Integer.toString(transNumBKInt));
					paramMap.put("bookingRefno", bookingRefno);
					paramMap.put("strUserID", strUserID);
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(strBKDetailsTrans, paramMap);

				}

			}

			// end of add by Revathi

			log.info(" *** esnCancel SQL *****" + strUpdate);

			paramMap.put("strUserID", strUserID);
			paramMap.put("esnNo", esnNo);
			log.info(" paramMap: " + paramMap);
			int count = namedParameterJdbcTemplate.update(strUpdate, paramMap);

			log.info(" *** esnCancel SQL *****" + strBkdetails);

			paramMap.put("strUserID", strUserID);
			paramMap.put("bookingRefno", bookingRefno);
			log.info(" paramMap: " + paramMap);
			int count1 = namedParameterJdbcTemplate.update(strBkdetails, paramMap);

			// added by vani -- to cancel UA's related to the given esnNo start -- 21th
			// Oct,03
			if (!strUAFlag.equals("") && strUAFlag != null && strUAFlag.equals("UA")) {
				if (cntCancelUA == 0 || count == 0 || count1 == 0) {
					log.info("Writing from EsnEJB.esnCancel");
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}
			} else { // added by vani -- to cancel UA's related to the given esnNo end -- 21th Oct,03
				if (count == 0 || count1 == 0) {
					log.info("Writing from EsnEJB.esnCancel");
					log.info("Record Cannot be added to Database");
					throw new BusinessException("M4201");
				}
			}
			log.info("END: *** esnCancel Result *****" + count);

		} catch (NullPointerException e) {
			log.info("exception esnCancel :", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception esnCancel : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception esnCancel : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: esnCancel  DAO  END");
		}

	}

	@Override
	public String getEsnDeclared(String bookref) throws BusinessException {
		String bookinRefNo = bookref;
		String esnDeclared = "";
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: getEsnDeclared  DAO  Start Obj " + " bookref:" + CommonUtility.deNull(bookref));

			sql = "select var_nbr, esn_declared, declarant_cd from bk_details where bk_ref_nbr = :bookinRefNo and bk_status='A'";

			log.info(" *** getEsnDeclared SQL *****" + sql);
			paramMap.put("bookinRefNo", bookinRefNo);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				esnDeclared = CommonUtility.deNull(rs.getString("esn_declared"));
			}

			log.info("END: *** getEsnDeclared Result *****" + esnDeclared.toString());

		} catch (NullPointerException e) {
			log.info("exception getEsnDeclared :", e);
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
	public String getTerminal(String bookref) throws BusinessException {
		String bookinRefNo = bookref;
		String terminal = "";
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		sql = String.valueOf(String.valueOf((new StringBuffer(
				"select terminal from vessel_call,bk_details where vessel_call.vv_cd = bk_details.var_nbr and bk_ref_nbr = :bookinRefNo "))));
		try {
			log.info("START: getTerminal  DAO  Start Obj " + " bookref:" + CommonUtility.deNull(bookref));

			log.info(" *** getTerminal SQL *****" + sql);
			paramMap.put("bookinRefNo", bookinRefNo);
			log.info(" paramMap: " + paramMap);
			for (rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap); rs.next();)
				terminal = CommonUtility.deNull(rs.getString("terminal"));

			log.info("END: *** getTerminal Result *****" + terminal.toString());

		} catch (NullPointerException e) {
			log.info("exception getTerminal :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTerminal : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTerminal  DAO  END");
		}
		return terminal;

	}

	// ejb.sessionBeans.gbms.cargo.esn.EsnEJB.getVvStatus()
	@Override
	public String getVvStatus(String bookref) throws BusinessException {
		String bookinRefNo = bookref;
		String vvStatus = "";
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		
		// Start bypass the system validation on vessel-unberthed status. - NS May 2023
		sb.append(" SELECT (CASE WHEN VV_STATUS_IND='UB' AND REOPEN_IND='Y' THEN 'BR' ELSE VV_STATUS_IND END) VV_STATUS_IND FROM (");
		// End bypass the system validation on vessel-unberthed status.
		
		sb.append(" select vv_status_ind,gb_close_bj_ind,vessel_call.gb_close_shp_ind,gb_close_vsl_ind  ");
		
		// Start bypass the system validation on vessel-unberthed status. - NS May 2023
		sb.append(" ,CASE WHEN (NVL((SELECT (SYSDATE-TO_DATE(TO_CHAR(CREATE_DTTM,'DD-MM-YYYY HH24MISS'),'DD-MM-YYYY HH24MISS'))*24*60- ");
		sb.append(" (SELECT TO_NUMBER(MISC_TYPE_NM) FROM SYSTEM_CONFIG WHERE CAT_CD='REOPEN_DOC_SETTING' AND REC_STATUS='A' AND MISC_TYPE_CD='GC_ESN') ");
		sb.append(" FROM SYSTEM_CONFIG WHERE CAT_CD='REOPEN_DOC_GC_ESN' AND REC_STATUS='A' AND MISC_TYPE_CD=bk_details.var_nbr),1))>0 THEN 'N' ELSE 'Y' END REOPEN_IND ");
		// End bypass the system validation on vessel-unberthed status.
		
		sb.append(" from vessel_call,bk_details where vessel_call.vv_cd = bk_details.var_nbr and bk_ref_nbr =:bookinRefNo )");
		sql = sb.toString();
		
		//sql = "select vv_status_ind,gb_close_bj_ind,vessel_call.gb_close_shp_ind,gb_close_vsl_ind from vessel_call,bk_details where vessel_call.vv_cd = bk_details.var_nbr and bk_ref_nbr =:bookinRefNo ";

		try {
			log.info("START: getVvStatus  DAO  Start Obj " + " bookref:" + CommonUtility.deNull(bookref));

			log.info(" *** getVvStatus SQL *****" + sql);
			paramMap.put("bookinRefNo", bookinRefNo);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				vvStatus = CommonUtility.deNull(rs.getString("vv_status_ind"));
			}
			log.info("END: *** getVvStatus Result *****" + vvStatus.toString());

		} catch (NullPointerException e) {
			log.info("exception getVvStatus :", e);
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
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		
		sql = "select var_nbr, esn_declared, declarant_cd from bk_details where bk_ref_nbr = :bookinRefNo and bk_status='A'";

		try {
			log.info("START: getDeclarentCd  DAO  Start Obj " + " bookref:" + CommonUtility.deNull(bookref));

			log.info(" *** getDeclarentCd SQL *****" + sql);
			paramMap.put("bookinRefNo", bookinRefNo);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				esnDeclaredCd = CommonUtility.deNull(rs.getString("declarant_cd"));
			}
			log.info("END: *** getDeclarentCd Result *****" + esnDeclaredCd.toString());

		} catch (NullPointerException e) {
			log.info("exception getDeclarentCd :", e);
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
	public String getCrgTypeCd(String crgType) throws BusinessException {
		String crgTypeDesc = crgType;
		String sql = "";
		String crgTypeCd = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		
		sql = "select CRG_TYPE_CD from CRG_TYPE where CRG_TYPE_NM=:crgTypeDesc ";

		try {
			log.info("START: getCrgTypeCd  DAO  Start Obj " + " crgType:" + CommonUtility.deNull(crgType));

			log.info(" *** getCrgTypeCd SQL *****" + sql);

			paramMap.put("crgTypeDesc", crgTypeDesc);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				crgTypeCd = CommonUtility.deNull(rs.getString("CRG_TYPE_CD"));
			}

			log.info("END: *** getCrgTypeCd Result *****" + crgTypeCd.toString());

		} catch (NullPointerException e) {
			log.info("exception getCrgTypeCd :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCrgTypeCd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCrgTypeCd  DAO  END");
		}

		return crgTypeCd;

	}

	@Override
	public String getClsVslInd(String bookref) throws BusinessException {
		String bookinRefNo = bookref;
		String clsVslInd = "";
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		sql = "select vv_status_ind,gb_close_bj_ind,vessel_call.gb_close_shp_ind,gb_close_vsl_ind from vessel_call,bk_details where vessel_call.vv_cd = bk_details.var_nbr and bk_ref_nbr =:bookinRefNo ";

		try {
			log.info("START: getClsVslInd  DAO  Start Obj " + " bookref:" + CommonUtility.deNull(bookref));

			log.info(" *** getClsVslInd SQL *****" + sql);

			paramMap.put("bookinRefNo", bookinRefNo);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				clsVslInd = CommonUtility.deNull(rs.getString("gb_close_shp_ind"));
			}
			log.info("END: *** getClsVslInd Result *****" + clsVslInd.toString());

		} catch (NullPointerException e) {
			log.info("exception getClsVslInd :", e);
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
	public String getClsBjInd(String bookref) throws BusinessException {
		String bookinRefNo = bookref;
		String clsBjInd = "";
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		sql = "select vv_status_ind,gb_close_bj_ind,vessel_call.gb_close_shp_ind,gb_close_vsl_ind from vessel_call,bk_details where vessel_call.vv_cd = bk_details.var_nbr and bk_ref_nbr = :bookinRefNo ";

		try {
			log.info("START: getClsBjInd  DAO  Start Obj " + " bookref:" + CommonUtility.deNull(bookref));

			log.info(" *** getClsBjInd SQL *****" + sql);
			paramMap.put("bookinRefNo", bookinRefNo);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				clsBjInd = CommonUtility.deNull(rs.getString("gb_close_bj_ind"));
			}
			log.info("END: *** getClsBjInd Result *****" + clsBjInd.toString());

		} catch (NullPointerException e) {
			log.info("exception getClsBjInd :", e);
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
	public String insertEsnDetailsForDPE(String varno, String truckerIcNo, String truckerCNo, String custCd,
			String bookingRefNo, String marking, String portD, String lopInd, String loadFrom, String dgIn,
			String hsCode, String dutiDI, String truckerName, int storageDay, String storageInd, String pkgsType,
			int noOfPkgs, double weight, double volume, String accNo, String payMode, String cargoDesc,
			String truckerCd, String cntr1, String cntr2, String cntr3, String cntr4, String UserID,
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			// String strStfInd,String category) throws BusinessException, RemoteException {
			String strStfInd, String category, String hsSubCodeFr, String hsSubCodeTo, int trucker_nbr_pkgs,
			String deliveryToEPC, String cntr_seq_nbr, String miscAppNo,String customHsCode, List<HsCodeDetails> multiHsCodeList) // MCC for EPC_IND
			throws BusinessException {
		// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END
	
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs1 = null;
		String strMark = new String();
		String strCntr1 = new String();
		String strCntr2 = new String();
		String strCntr3 = new String();
		String strCntr4 = new String();
		String strEsnDetails = new String();
		String strUpdate = new String();
		String sqlTrans = "";
		String strInsertTrans = new String();
		String strMarkTrans = new String();
		String strCntr1Trans = new String();
		String strCntr2Trans = new String();
		String strCntr3Trans = new String();
		String strCntr4Trans = new String();
		String esnNo = new String();
		String strEsnDetailsTrans = new String();
		// Added By Revathi
		String strBKDetailsTrans = new String();
		String strBKTrans = new String();
		int transNumBKInt = 0;
		// end of Add by Revtahi
		String esnDeclrCd = "";
		String transNumEsnStr = "";
		int transNumEsnInt = 0;

		try {
			log.info("START: insertEsnDetailsForDPE  DAO  Start Obj " + " varno:" + CommonUtility.deNull(varno) + " truckerIcNo:"
					+ CommonUtility.deNull(truckerIcNo) + " truckerCNo:" + CommonUtility.deNull(truckerCNo) + " custCd:" + CommonUtility.deNull(custCd)
					+ " bookingRefNo:" + CommonUtility.deNull(bookingRefNo) + " marking:" + CommonUtility.deNull(marking) + " portD:" + CommonUtility.deNull(portD)
					+ " lopInd:" + CommonUtility.deNull(lopInd) + " loadFrom:" + CommonUtility.deNull(loadFrom) + " dgIn:" + CommonUtility.deNull(dgIn)
					+ " hsCode:" + CommonUtility.deNull(hsCode) + " dutiDI:" + CommonUtility.deNull(dutiDI) + " truckerName:" + CommonUtility.deNull(truckerName)
					+ " storageDay:" + CommonUtility.deNull(String.valueOf(storageDay)) + " storageInd:" + CommonUtility.deNull(storageInd)
					+ " pkgsType:" + CommonUtility.deNull(pkgsType) + " noOfPkgs:" + CommonUtility.deNull(String.valueOf(noOfPkgs))
					+ " weight:" + CommonUtility.deNull(String.valueOf(weight)) + " volume:" + CommonUtility.deNull(String.valueOf(volume))
					+ " accNo:" + CommonUtility.deNull(accNo) + " payMode:" + CommonUtility.deNull(payMode) + " cargoDesc:" + CommonUtility.deNull(cargoDesc)
					+ " truckerCd:" + CommonUtility.deNull(truckerCd) + " cntr1:" + CommonUtility.deNull(cntr1) + " cntr2:" + CommonUtility.deNull(cntr2)
					+ " cntr3:" + CommonUtility.deNull(cntr3) + " cntr4:" + CommonUtility.deNull(cntr4) + " UserID:" + CommonUtility.deNull(UserID)
					+ " strStfInd:" + CommonUtility.deNull(strStfInd) + " category:" + CommonUtility.deNull(category) + " hsSubCodeFr:" + CommonUtility.deNull(hsSubCodeFr)
					+ " hsSubCodeTo:" + CommonUtility.deNull(hsSubCodeTo) + " trucker_nbr_pkgs:" + CommonUtility.deNull(String.valueOf(trucker_nbr_pkgs))
					+ " deliveryToEPC:" + CommonUtility.deNull(deliveryToEPC) + " cntr_seq_nbr:" + CommonUtility.deNull(cntr_seq_nbr) + " miscAppNo:" + CommonUtility.deNull(miscAppNo)
					+ ",customHsCode:" + customHsCode + ", multiHsCodeList : " + multiHsCodeList.toString());

			if (custCd.equals("JP"))
				esnDeclrCd = getDeclarant(bookingRefNo);
			else
				esnDeclrCd = custCd;

			esnNo = getEsnNoForDPE();

			// Added ny MCconsulting , deliveryToEPC set to 'N' if deliveryToEPC is null or
			// empty.
			deliveryToEPC = (deliveryToEPC == null || deliveryToEPC.equalsIgnoreCase("null")
					|| deliveryToEPC.trim().length() == 0) ? "N" : deliveryToEPC;

			// scheme = getSchemeName(varno);
			// if(scheme.equals("JLR"))
			// mschactnbr = getVCactnbr(varno);
			// else if(!scheme.equals("JLR") && !scheme.equals("JNL") &&
			// !scheme.equals("JBT"))
			// mschactnbr = getABactnbr(varno);
			int result = -2;
			int cnt = 0; // for limiting number of tries to insert bill
			while (result == -2 && cnt < MAX_TRY) {
				result = this.addEsn(esnNo, bookingRefNo, varno, esnDeclrCd, UserID, strStfInd, category, deliveryToEPC,
						cntr_seq_nbr, miscAppNo);
				log.info("result" + result);
				if (result == -2) {
					esnNo = this.getEsnNoForDPE();
					cnt++;
					continue;
				} else {
					break;
				}
			}

			sqlTrans = "SELECT MAX(TRANS_NBR) FROM ESN_TRANS WHERE ESN_ASN_NBR = :esnNo";

			log.info(" *** insertEsnDetailsForDPE SQL *****" + sqlTrans);

			if (logStatusGlobal.equalsIgnoreCase("Y")) {

				paramMap.put("esnNo", esnNo);
				log.info(" paramMap: " + paramMap);
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

				// Added by Revathi
				//strBKTrans = "SELECT MAX(TRANS_NBR) FROM BK_DETAILS_TRANS WHERE BK_REF_NBR=:bookingRefNo ";
				
				//log.info(" *** insertEsnDetailsForDPE SQL *****" + strBKTrans);
				//paramMap.put("bookingRefNo", bookingRefNo);
				//log.info(" paramMap: " + paramMap);
				//rs1 = namedParameterJdbcTemplate.queryForRowSet(strBKTrans, paramMap);
				//while (rs1.next()) {
				//	strBKTrans = rs1.getString(1);
				//}

				// START :Update code to solve user requested cancel - NS MAY 2023
				String sql1 = "select trans_nbr from bk_details_trans where BK_REF_NBR = :bkRefNbr ";
				List<Integer> transNbrList = new ArrayList<Integer>();
				paramMap.put("bkRefNbr", bookingRefNo);
				
				log.info(" *** insertBK SQL *****" + sql1);
				log.info(" *** insertBK params *****" + paramMap.toString());
				
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
				while(rs1.next()) {
					int trans_nbr = Integer.parseInt(rs1.getString("trans_nbr"));
					transNbrList.add(trans_nbr);
				}

				String max = null;
				if(transNbrList.size() > 0) {
					max = Collections.max(transNbrList).toString();
					if (max != null && max.equals(""))
						max = "0";
					if (max == null)
						max = "0";
				} else {
					if (max == null)
						max = "0";
				}
				strBKTrans = String.valueOf(Integer.parseInt(max));
				// END :  Update code to solve user requested cancel - NS MAY 2023
				
				if (strBKTrans == null || strBKTrans == "") {
					transNumBKInt = 0;
				} else {
					transNumBKInt = Integer.parseInt(strBKTrans);
					transNumBKInt++;
				}
				StringBuffer sb1 = new StringBuffer();
				sb1.append(
						"INSERT INTO ESN_Trans(ESN_ASN_NBR,TRANS_NBR,DECLARANT_CR_NO,BK_REF_NBR,TRANS_TYPE,OUT_VOY_VAR_NBR,ESN_CREATE_CD,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,ESN_STATUS,STUFF_IND,CARGO_CATEGORY_CD,EPC_IND, cntr_seq_nbr,misc_app_nbr) ");
				sb1.append(
						"VALUES(:esnNo,:transNumEsnInt,'O',:bookingRefNo,'E',:varno,:esnDeclrCd,:UserID,sysdate,'A',:strStfInd,:category,:deliveryToEPC,:cntr_seq_nbr,:miscAppNo )");

				strInsertTrans = sb1.toString();

				strBKDetailsTrans = "INSERT INTO BK_DETAILS_TRANS(TRANS_NBR,BK_REF_NBR,ESN_DECLARED,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)VALUES(:transNumBKInt,:bookingRefNo,'Y',:UserID,sysdate)";
				// end of Add by Revathi
				StringBuffer sb2 = new StringBuffer();
				sb2.append(
						"INSERT INTO ESN_DETAILS_Trans(ESN_ASN_NBR,TRANS_NBR,HA_CUST_CD,ESN_PORT_DIS,ESN_OPS_IND,ESN_LOAD_FROM,ESN_DG_IND,ESN_HS_CODE,ESN_DUTY_GOOD_IND,TRUCKER_NM,TRUCKER_IC, ");
				sb2.append(
						"STG_DAYS,STG_IND,PKG_TYPE,NBR_PKGS,ESN_WT,ESN_VOL,ACCT_NBR,CRG_DES,TRUCKER_PHONE_NBR,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,PAYMENT_MODE, CUSTOM_HS_CODE) ");
				sb2.append("VALUES(:esnNo,:transNumEsnInt,:truckerCd,:portD,:lopInd,:loadFrom,:dgIn,:hsCode, ");
				sb2.append(
						":dutiDI,:truckerName,:truckerIcNo,:storageDay,:storageInd,:pkgsType,:noOfPkgs,:weight,:volume,:accNo, ");
				sb2.append(":cargoDesc,:truckerCNo,:UserID,sysdate,:payMode, :customHsCode)");

				strEsnDetailsTrans = sb2.toString();

				strMarkTrans = "INSERT INTO ESN_MARKINGS_Trans(ESN_ASN_NBR,TRANS_NBR,MARKINGS,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM)VALUES(:esnNo,:transNumEsnInt,:marking,:UserID,sysdate)";

				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y") {

					log.info(" *** insertEsnDetailsForDPE SQL *****" + strInsertTrans);

					paramMap.put("esnNo", esnNo);
					paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
					paramMap.put("bookingRefNo", bookingRefNo);
					paramMap.put("varno", varno);
					paramMap.put("esnDeclrCd", esnDeclrCd);
					paramMap.put("UserID", UserID);
					paramMap.put("strStfInd", strStfInd);
					paramMap.put("category", category);
					paramMap.put("deliveryToEPC", deliveryToEPC);
					paramMap.put("cntr_seq_nbr", cntr_seq_nbr);
					paramMap.put("miscAppNo", miscAppNo);
					
					log.info(" *** insertEsnDetailsForDPE SQL strInsertTrans*****" + strInsertTrans);
					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(strInsertTrans, paramMap);

					log.info(" *** insertEsnDetailsForDPE SQL strEsnDetailsTrans*****" + strEsnDetailsTrans);

					paramMap.put("esnNo", esnNo);
					paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
					paramMap.put("truckerCd", truckerCd);
					paramMap.put("portD", portD);
					paramMap.put("lopInd", lopInd);
					paramMap.put("loadFrom", loadFrom);
					paramMap.put("dgIn", dgIn);
					paramMap.put("hsCode", hsCode);
					paramMap.put("dutiDI", dutiDI);
					// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
					paramMap.put("truckerName", (CommonUtility.deNull(truckerName).trim()));
					paramMap.put("truckerIcNo", (CommonUtility.deNull(truckerIcNo).trim()));
					paramMap.put("storageDay", Integer.toString(storageDay));
					paramMap.put("storageInd", storageInd);
					paramMap.put("pkgsType", pkgsType);
					paramMap.put("noOfPkgs", Integer.toString(noOfPkgs));
					paramMap.put("weight", Double.toString(weight));
					paramMap.put("volume", Double.toString(volume));
					paramMap.put("accNo", (accNo));
					paramMap.put("cargoDesc", (cargoDesc));
					paramMap.put("truckerCNo", truckerCNo);
					paramMap.put("UserID", UserID);
					paramMap.put("payMode", payMode);
					paramMap.put("customHsCode", customHsCode);

					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(strEsnDetailsTrans, paramMap);

					log.info(" *** insertEsnDetailsForDPE SQL strMarkTrans*****" + strMarkTrans);

					paramMap.put("esnNo", esnNo);
					paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
					// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
					paramMap.put("marking", (marking));
					paramMap.put("UserID", UserID);

					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(strMarkTrans, paramMap);

					// Added by Revathi

					log.info(" *** insertEsnDetailsForDPE SQL strBKDetailsTrans*****" + strBKDetailsTrans);

					paramMap.put("transNumBKInt", Integer.toString(transNumBKInt));
					paramMap.put("bookingRefNo", bookingRefNo);
					paramMap.put("UserID", UserID);

					log.info(" paramMap: " + paramMap);
					namedParameterJdbcTemplate.update(strBKDetailsTrans, paramMap);
					// end of Add by Revathi
				}
			}
			StringBuffer sb3 = new StringBuffer();
			sb3.append(
					"INSERT INTO ESN_DETAILS(ESN_ASN_NBR,HA_CUST_CD,ESN_PORT_DIS,ESN_OPS_IND,ESN_LOAD_FROM,ESN_DG_IND,ESN_HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,ESN_DUTY_GOOD_IND,TRUCKER_NM,TRUCKER_IC, ");
			sb3.append(
					"STG_DAYS,STG_IND,PKG_TYPE,NBR_PKGS,ESN_WT,ESN_VOL,UA_NBR_PKGS,ACCT_NBR,PAYMENT_MODE,CRG_DES,TRUCKER_PHONE_NBR, TRUCKER_NBR_PKGS, CUSTOM_HS_CODE) ");
			sb3.append("VALUES(:esnNo,:truckerCd,:portD,:lopInd,:loadFrom,:dgIn,:hsCode,:hsSubCodeFr,:hsSubCodeTo, ");
			sb3.append(
					":dutiDI,:truckerName,:truckerIcNo,:storageDay,:storageInd,:pkgsType,:noOfPkgs,:weight,:volume,'0',:accNo, ");
			sb3.append(":payMode,:cargoDesc,:truckerCNo, :trucker_nbr_pkgs , :customHsCode)");

			strEsnDetails = sb3.toString();

			strMark = "INSERT INTO ESN_MARKINGS(ESN_ASN_NBR,MARKINGS)VALUES(:esnNo,:marking)";

			strUpdate = "UPDATE BK_DETAILS SET ESN_DECLARED='Y',LAST_MODIFY_DTTM=SYSDATE,LAST_MODIFY_USER_ID=:UserID WHERE BK_REF_NBR=:bookingRefNo";

			log.info(" *** insertEsnDetailsForDPE SQL strEsnDetails*****" + strEsnDetails);

			paramMap.put("esnNo", esnNo);
			paramMap.put("truckerCd", truckerCd);
			paramMap.put("portD", portD);
			paramMap.put("lopInd", lopInd);
			paramMap.put("loadFrom", loadFrom);
			paramMap.put("dgIn", dgIn);
			paramMap.put("hsCode", hsCode);
			paramMap.put("hsSubCodeFr", hsSubCodeFr);
			paramMap.put("hsSubCodeTo", hsSubCodeTo);
			paramMap.put("dutiDI", dutiDI);
			// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
			paramMap.put("truckerName", (CommonUtility.deNull(truckerName).trim()));
			paramMap.put("truckerIcNo", (CommonUtility.deNull(truckerIcNo).trim()));
			paramMap.put("storageDay", Integer.toString(storageDay));
			paramMap.put("storageInd", storageInd);
			paramMap.put("pkgsType", pkgsType);
			paramMap.put("noOfPkgs", Integer.toString(noOfPkgs));
			paramMap.put("weight", Double.toString(weight));
			paramMap.put("volume", Double.toString(volume));
			paramMap.put("accNo", (accNo));
			paramMap.put("payMode", payMode);
			paramMap.put("cargoDesc", (cargoDesc));
			paramMap.put("truckerCNo", truckerCNo);
			paramMap.put("trucker_nbr_pkgs", Integer.toString(trucker_nbr_pkgs));
			paramMap.put("customHsCode", customHsCode);
			log.info("SQL" + strEsnDetails);

			log.info(" paramMap: " + paramMap);
			int cntEsnDetails = namedParameterJdbcTemplate.update(strEsnDetails, paramMap);

			log.info(" *** insertEsnDetailsForDPE SQL strMark*****" + strMark);

			paramMap.put("esnNo", esnNo);
			// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
			paramMap.put("marking", (marking));

			int cntmrk = namedParameterJdbcTemplate.update(strMark, paramMap);

			log.info(" *** insertEsnDetailsForDPE SQL strUpdate*****" + strUpdate);

			paramMap.put("UserID", UserID);
			paramMap.put("bookingRefNo", bookingRefNo);
			log.info(" paramMap: " + paramMap);
			int cntUpdate = namedParameterJdbcTemplate.update(strUpdate, paramMap);

			strCntr1 = "INSERT INTO esn_cntr(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES('1',:esnNo,:cntr1)";

			strCntr2 = "INSERT INTO esn_cntr(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES('2',:esnNo,:cntr2)";

			strCntr3 = "INSERT INTO esn_cntr(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES('3',:esnNo,:cntr3)";

			strCntr4 = "INSERT INTO esn_cntr(ESN_CNTR_SEQ,ESN_ASN_NBR,CNTR_NBR) VALUES('4',:esnNo,:cntr4)";

			strCntr1Trans = "INSERT INTO esn_cntr_Trans(ESN_CNTR_SEQ,ESN_ASN_NBR,TRANS_NBR,CNTR_NBR) VALUES('1',:esnNo,:transNumEsnInt,:cntr1)";

			strCntr2Trans = "INSERT INTO esn_cntr_Trans(ESN_CNTR_SEQ,ESN_ASN_NBR,TRANS_NBR,CNTR_NBR) VALUES('2',:esnNo,:transNumEsnInt,:cntr2)";

			strCntr3Trans = "INSERT INTO esn_cntr_Trans(ESN_CNTR_SEQ,ESN_ASN_NBR,TRANS_NBR,CNTR_NBR) VALUES('3',:esnNo,:transNumEsnInt,:cntr3)";

			strCntr4Trans = "INSERT INTO esn_cntr_Trans(ESN_CNTR_SEQ,ESN_ASN_NBR,TRANS_NBR,CNTR_NBR) VALUES('4',:esnNo,:transNumEsnInt,:cntr4)";

			if (cntr1 != null && !cntr1.equals("")) {

				log.info(" *** insertEsnDetailsForDPE SQL strCntr1*****" + strCntr1);

				paramMap.put("esnNo", esnNo);
				// RE: Customer not able to create Cargo ESN for UEN No 200822834N : ESN ERROR - remove usage for addApostr to proper get the value - NS Oct 2023
				paramMap.put("cntr1", (cntr1));
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strCntr1, paramMap);

//				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y")
//					sqlstmt = null;

				log.info(" *** insertEsnDetailsForDPE SQL *****" + strCntr1Trans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
				paramMap.put("cntr1", (cntr1));
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strCntr1Trans, paramMap);
			}
			if (cntr2 != null && !cntr2.equals("")) {

				log.info(" *** insertEsnDetailsForDPE SQL *****" + strCntr2);

				paramMap.put("esnNo", esnNo);
				paramMap.put("cntr2", (cntr2));
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strCntr2, paramMap);

//				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y")
//					sqlstmt = null;

				log.info(" *** insertEsnDetailsForDPE SQL *****" + strCntr2Trans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
				paramMap.put("cntr2", (cntr2));
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strCntr2Trans, paramMap);
			}
			if (cntr3 != null && !cntr3.equals("")) {

				log.info(" *** insertEsnDetailsForDPE SQL *****" + strCntr3);
				paramMap.put("esnNo", esnNo);
				paramMap.put("cntr3", (cntr3));
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strCntr3, paramMap);

//				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y")
//					sqlstmt = null;

				log.info(" *** insertEsnDetailsForDPE SQL *****" + strCntr3Trans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
				paramMap.put("cntr3", (cntr3));
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strCntr3Trans, paramMap);
			}
			if (cntr4 != null && !cntr4.equals("")) {

				log.info(" *** insertEsnDetailsForDPE SQL *****" + strCntr4);
				paramMap.put("esnNo", esnNo);
				paramMap.put("cntr4", (cntr4));
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strCntr4, paramMap);
//				if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y")
//					
				log.info(" *** insertEsnDetailsForDPE SQL *****" + strCntr4Trans);

				paramMap.put("esnNo", esnNo);
				paramMap.put("transNumEsnInt", Integer.toString(transNumEsnInt));
				paramMap.put("cntr4", (cntr4));
				log.info(" paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strCntr4Trans, paramMap);
			}

			if (cntEsnDetails == 0 || cntmrk == 0 || cntUpdate == 0) {

				log.info("Writing from Esn.insertEsnDetails");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			} else {
				
				// START CR FTZ HSCODE - NS JULY 2024
				for (HsCodeDetails hsCodeObj : multiHsCodeList) {					

					// get ESN_HSCODE_SEQ_NBR 
					StringBuilder sbSeq = new StringBuilder();
					sbSeq.append("SELECT GBMS.SEQ_ESN_HSCODE_SEQ_NBR.nextval AS seqVal FROM DUAL");
					Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(), new HashMap<String, String>());
					BigDecimal seqValue = (BigDecimal) results.get("seqVal");
					// end
					
					StringBuilder sbhscode = new StringBuilder();
					sbhscode.append(" INSERT INTO GBMS.ESN_HSCODE_DETAILS  ");
					sbhscode.append(" (ESN_ASN_NBR,ESN_HSCODE_SEQ_NBR,HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,CUSTOM_HS_CODE,CRG_DES, HS_SUB_CODE_DESC,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
					sbhscode.append(" VALUES(:ESN_ASN_NBR,:ESN_HSCODE_SEQ_NBR,:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:NBR_PKGS,:GROSS_WT,:GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
					paramMap.put("ESN_ASN_NBR", esnNo);
					paramMap.put("ESN_HSCODE_SEQ_NBR", seqValue);
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
					sbhscode.append(" INSERT INTO GBMS.ESN_HSCODE_DETAILS_TRANS  ");
					sbhscode.append(" (ESN_ASN_NBR, ESN_HSCODE_SEQ_NBR, AUDIT_DTTM, REC_STATUS, HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,CUSTOM_HS_CODE,CRG_DES, HS_SUB_CODE_DESC,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
					sbhscode.append(" VALUES(:ESN_ASN_NBR,:ESN_HSCODE_SEQ_NBR, SYSDATE, 'A',:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:NBR_PKGS,:GROSS_WT,:GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
					paramMap.put("ESN_ASN_NBR", esnNo);
					paramMap.put("ESN_HSCODE_SEQ_NBR", seqValue);
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
				// END CR FTZ HSCODE - NS JULY 2024
			}

			if (logStatusGlobal.equalsIgnoreCase("N") || logStatusGlobal == "N") {
				log.trace(strInsertTrans);
				log.trace(strMarkTrans);
				log.trace(strEsnDetailsTrans);
				log.trace(strCntr1Trans);
				log.trace(strCntr2Trans);
				log.trace(strCntr3Trans);
				log.trace(strCntr4Trans);
			}
			log.info("END: *** insertEsnDetailsForDPE Result *****" + esnNo.toString());

		} catch (NullPointerException e) {
			log.info("exception insertEsnDetailsForDPE :", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception insertEsnDetailsForDPE : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception insertEsnDetailsForDPE : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertEsnDetailsForDPE  DAO  END");
		}
		return esnNo;
	}

	private int addEsn(String esnNo, String bookingRefNo, String varno, String esnDeclrCd, String userID,
			String strStfInd, String category, String deliveryToEPC, String cntr_seq_nbr, String miscAppNo)
			throws BusinessException {
		String strInsert = "";
		int count = 0;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: addEsn  DAO  Start Obj " + " esnNo:" + CommonUtility.deNull(esnNo) + " bookingRefNo:" + CommonUtility.deNull(bookingRefNo) + " varno:"
					+ CommonUtility.deNull(varno) + " esnDeclrCd:" + CommonUtility.deNull(esnDeclrCd) + " userID:" + CommonUtility.deNull(userID)
					+ " strStfInd:" + CommonUtility.deNull(strStfInd) + " category:" + CommonUtility.deNull(category) + " deliveryToEPC:" + CommonUtility.deNull(deliveryToEPC)
					+ " cntr_seq_nbr:" + CommonUtility.deNull(cntr_seq_nbr) + " miscAppNo:" + CommonUtility.deNull(miscAppNo));
			// Added ny MCconsulting , deliveryToEPC set to 'N' if deliveryToEPC is null or
			// empty.
			deliveryToEPC = (deliveryToEPC == null || deliveryToEPC.equalsIgnoreCase("null")
					|| deliveryToEPC.trim().length() == 0) ? "N" : deliveryToEPC;

			sb.append(
					"INSERT INTO ESN(ESN_ASN_NBR,DECLARANT_CR_NO,BK_REF_NBR,TRANS_TYPE,OUT_VOY_VAR_NBR,ESN_STATUS,ESN_CREATE_CD,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,STUFF_IND,CARGO_CATEGORY_CD,EPC_IND, CNTR_SEQ_NBR, MISC_APP_NBR)");
			sb.append("VALUES(:esnNo,'O',:bookingRefNo,'E',:varno,'A',:esnDeclrCd,:userID");
			sb.append(",sysdate,:strStfInd,:category,:deliveryToEPC,:cntr_seq_nbr,:miscAppNo )");

			strInsert = sb.toString();

			// added strStfInd,vani

			log.info(" *** addEsn SQL *****" + strInsert);

			paramMap.put("esnNo", esnNo);
			paramMap.put("bookingRefNo", bookingRefNo);
			paramMap.put("varno", varno);
			paramMap.put("esnDeclrCd", esnDeclrCd);
			paramMap.put("userID", userID);
			paramMap.put("strStfInd", strStfInd);
			paramMap.put("category", category);
			paramMap.put("deliveryToEPC", deliveryToEPC);
			paramMap.put("cntr_seq_nbr", cntr_seq_nbr);
			paramMap.put("miscAppNo", miscAppNo);
			log.info("addEsn paramMap: " + paramMap);
			count = namedParameterJdbcTemplate.update(strInsert, paramMap);
			log.info("addEsn count: " + count);
		} catch (Exception sqle) {
			if (sqle.getMessage().indexOf("ORA-00001") >= 0) {
				return -2;
			} else {
				throw new BusinessException("M4201");
			}
		} finally {
			log.info("END: addEsn  DAO  END");
		}
		return count;
	}

	@Override
	public List<EsnListValueObject> getEsnList(String selectVoyNo, String custId) throws BusinessException {
		String selVoyNo = selectVoyNo;
		String custCd = custId;
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();

		boolean isShowEsnInfo = false;
		try {

			TextParaVO code = new TextParaVO();
			code.setParaCode(TEXT_PARA_GC_VIEW_ESN);
			TextParaVO result = textParaRepo.getParaCodeInfo(code);
			isShowEsnInfo = isShowEsnInfo(custId, result);
		} catch (Exception e) {
			log.info("Exception getEsnList ParaCode : ", e);
		}

		if (isShowEsnInfo) {

			sb.append(
					"select DISTINCT esn.esn_asn_nbr,shipper_nm,CRG_TYPE_NM,esnd.NBR_PKGS,esnd.ESN_WT,esnd.ESN_VOL,esn.BK_REF_NBR,esn.STUFF_IND,code.CC_NAME,esnd.CRG_DES,esnd.ESN_HS_CODE,esnd.HS_SUB_CODE_FR, esnd.HS_SUB_CODE_TO, NVL(vsh.SCHEME_CD, VC.SCHEME) SCHEME_CD, esn.EPC_IND, NVL(vsh.SCHEME_CD, VC.COMBI_GC_SCHEME) COMBI_GC_SCHEME_CD, VC.COMBI_GC_OPS_IND, VC.TERMINAL from esn,bk_details bkd,  ");
			sb.append(
					"esn_details esnd, CRG_TYPE , vessel_call VC, CARGO_CATEGORY_CODE code, vessel_scheme vsh where esnd.esn_asn_nbr = esn.esn_asn_nbr and esn_Status = 'A' ");
			sb.append(
					"and esn.out_voy_var_nbr = VC.vv_cd and bkd.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD and bkd.bk_ref_nbr = esn.BK_REF_NBR and ");
			sb.append(" esnd.mixed_scheme_acct_nbr = vsh.acct_nbr(+) and vsh.rec_status(+) = 'A'  ");
			sb.append(
					" and esn.CARGO_CATEGORY_CD = code.CC_CD and esn.OUT_VOY_VAR_NBR=:selVoyNo ORDER BY esn.esn_asn_nbr");

			sql = sb.toString();
		} else {
			// Amended by Dongsheng on 8/6/2011. The space at the end of the line was
			// mistakenly removed by vendor. SL-OPS-20110608-01
			// sql = "select
			// esn.esn_asn_nbr,shipper_nm,CRG_TYPE_NM,esnd.NBR_PKGS,esnd.ESN_WT,esnd.ESN_VOL,esn.BK_REF_NBR,esn.STUFF_IND,code.CC_NAME
			// ,esnd.CRG_DES" +

			sb.append(
					"select DISTINCT esn.esn_asn_nbr,shipper_nm,CRG_TYPE_NM,esnd.NBR_PKGS,esnd.ESN_WT,esnd.ESN_VOL,esn.BK_REF_NBR,esn.STUFF_IND,code.CC_NAME ,esnd.CRG_DES, esnd.ESN_HS_CODE,esnd.HS_SUB_CODE_FR, esnd.HS_SUB_CODE_TO, NVL(vsh.SCHEME_CD, VC.SCHEME) SCHEME_CD, esn.EPC_IND,NVL(vsh.SCHEME_CD, VC.COMBI_GC_SCHEME) COMBI_GC_SCHEME_CD, VC.COMBI_GC_OPS_IND,VC.TERMINAL ");
			sb.append(
					"from esn,bk_details bkd,esn_details esnd, CRG_TYPE, vessel_call VC,CARGO_CATEGORY_CODE code, vessel_scheme vsh   ");
			sb.append("where esnd.esn_asn_nbr = esn.esn_asn_nbr and esn_Status = 'A' ");
			sb.append("and esn.out_voy_var_nbr = VC.vv_cd ");
			sb.append("and bkd.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD and bkd.bk_ref_nbr = esn.BK_REF_NBR  and ");
			sb.append(" esnd.mixed_scheme_acct_nbr = vsh.acct_nbr(+) and vsh.rec_status(+) = 'A'  ");
			sb.append("and esn.OUT_VOY_VAR_NBR=:selVoyNo");
			sb.append(
					" and esn.CARGO_CATEGORY_CD = code.CC_CD and (esn.ESN_CREATE_CD = :custCd OR VC.CREATE_CUST_CD = :custCd)");
			sb.append(" ORDER BY esn.esn_asn_nbr");
			sql = sb.toString();
		}

		List<EsnListValueObject> esnList = new ArrayList<EsnListValueObject>();
		EsnListValueObject esnListValueObject = null;

		try {
			log.info("START: getEsnList  DAO  Start Obj selectVoyNo: " + CommonUtility.deNull(selectVoyNo) + " custId: " + CommonUtility.deNull(custId));

			log.info(" *** getEsnList SQL *****" + sql);

			if (isShowEsnInfo) {
				paramMap.put("selVoyNo", selVoyNo);
			} else {
				paramMap.put("selVoyNo", selVoyNo);
				paramMap.put("custCd", custCd);
			}

			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				esnListValueObject = new EsnListValueObject();
				esnListValueObject.setEsnNbr(rs.getLong("ESN_ASN_NBR"));
				esnListValueObject.setBookingRefNo(CommonUtility.deNull(rs.getString("BK_REF_NBR")));
				esnListValueObject.setShipperName(CommonUtility.deNull(rs.getString("shipper_nm")));
				esnListValueObject.setCrgDesc(CommonUtility.deNull(rs.getString("CRG_DES")));
				esnListValueObject.setCrgType(CommonUtility.deNull(rs.getString("CRG_TYPE_NM")));
				esnListValueObject.setNoofPkgs(rs.getInt(4));
				esnListValueObject.setGrWt(rs.getDouble("ESN_WT"));
				esnListValueObject.setGrVolume(rs.getDouble("ESN_VOL"));
				esnListValueObject.setStfInd(CommonUtility.deNull(rs.getString("STUFF_IND"))); // added by vani
				esnListValueObject.setCategory(rs.getString("CC_NAME"));

				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
				esnListValueObject.setHsCode(CommonUtility.deNull(rs.getString("ESN_HS_CODE")));
				esnListValueObject.setHsSubCodeFr(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				esnListValueObject.setHsSubCodeTo(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));
				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

				// haiTTH1 added on 19/3/2014
				esnListValueObject.setScheme(CommonUtility.deNull(rs.getString("SCHEME_CD")));
				// haiTTH1 ended on 19/3/2014

				esnListValueObject.setDeliveryToEPC(CommonUtility.deNull(rs.getString("EPC_IND"))); // MCC for EPC_IND
				esnListValueObject.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME_CD")));
				esnListValueObject.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				esnListValueObject.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				esnList.add(esnListValueObject);
			}
			log.info("END: *** getEsnList Result *****" + esnList.toString());

		} catch (NullPointerException e) {
			log.info("exception getEsnList :", e);
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
	public int getEsnListCount(String selectVoyNo, String custId, Criteria criteria) throws BusinessException {
		String selVoyNo = selectVoyNo;
		String custCd = custId;
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		boolean isShowEsnInfo = false;
		int count = 0;
		try {

			TextParaVO code = new TextParaVO();
			code.setParaCode(TEXT_PARA_GC_VIEW_ESN);
			TextParaVO result = textParaRepo.getParaCodeInfo(code);
			isShowEsnInfo = isShowEsnInfo(custId, result);
		} catch (Exception e) {

			log.info("Retriving text para error: " + e.getMessage());
			log.info("Exception getEsnList : ", e);
			throw new BusinessException("M4201");
		}

		if (isShowEsnInfo) {

			sb.append("select COUNT(*) from esn,bk_details bkd,  ");
			sb.append(
					"esn_details esnd, CRG_TYPE , vessel_call VC, CARGO_CATEGORY_CODE code, vessel_scheme vsh where esnd.esn_asn_nbr = esn.esn_asn_nbr and esn_Status = 'A' ");
			sb.append(
					"and esn.out_voy_var_nbr = VC.vv_cd and bkd.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD and bkd.bk_ref_nbr = esn.BK_REF_NBR and ");
			sb.append(" esnd.mixed_scheme_acct_nbr = vsh.acct_nbr(+) and vsh.rec_status(+) = 'A'  ");
			sb.append(
					" and esn.CARGO_CATEGORY_CD = code.CC_CD and esn.OUT_VOY_VAR_NBR=:selVoyNo ORDER BY esn.esn_asn_nbr");

		} else {
			// Amended by Dongsheng on 8/6/2011. The space at the end of the line was
			// mistakenly removed by vendor. SL-OPS-20110608-01
			// sql = "select
			// esn.esn_asn_nbr,shipper_nm,CRG_TYPE_NM,esnd.NBR_PKGS,esnd.ESN_WT,esnd.ESN_VOL,esn.BK_REF_NBR,esn.STUFF_IND,code.CC_NAME
			// ,esnd.CRG_DES" +

			sb.append("select COUNT(*) ");
			sb.append(
					" from esn,bk_details bkd,esn_details esnd, CRG_TYPE, vessel_call VC,CARGO_CATEGORY_CODE code, vessel_scheme vsh   ");
			sb.append("where esnd.esn_asn_nbr = esn.esn_asn_nbr and esn_Status = 'A' ");
			sb.append("and esn.out_voy_var_nbr = VC.vv_cd ");
			sb.append("and bkd.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD and bkd.bk_ref_nbr = esn.BK_REF_NBR  and ");
			sb.append(" esnd.mixed_scheme_acct_nbr = vsh.acct_nbr(+) and vsh.rec_status(+) = 'A'  ");
			sb.append("and esn.OUT_VOY_VAR_NBR=:selVoyNo");
			sb.append(
					" and esn.CARGO_CATEGORY_CD = code.CC_CD and (esn.ESN_CREATE_CD = :custCd OR VC.CREATE_CUST_CD = :custCd)");
			sb.append(" ORDER BY esn.esn_asn_nbr");

		}

		List<EsnListValueObject> esnList = new ArrayList<EsnListValueObject>();
		
		try {
			log.info("START: getEsnList  DAO  Start Obj selectVoyNo: " + CommonUtility.deNull(selectVoyNo) + " custId: " + CommonUtility.deNull(custId)
				+ " criteria: " + CommonUtility.deNull(String.valueOf(criteria)));

			sql = sb.toString();
			log.info(" *** getEsnList SQL *****" + sql);

			if (isShowEsnInfo) {
				paramMap.put("selVoyNo", selVoyNo);
			} else {
				paramMap.put("selVoyNo", selVoyNo);
				paramMap.put("custCd", custCd);
			}

			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				count = rs.getInt(1);
			}

			log.info("END: *** getEsnList Result *****" + esnList.toString());

		} catch (NullPointerException e) {
			log.info("exception getEsnList :", e);
			throw new BusinessException("M4201");		
		} catch (Exception e) {
			log.info("Exception getEsnList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEsnList  DAO  END");
		}

		return count;

	}

	@Override
	public List<EsnListValueObject> getEsnDetails(String esnNbr, String custId, Criteria criteria)
			throws BusinessException {
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
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		List<EsnListValueObject> esnList = new ArrayList<EsnListValueObject>();
		EsnListValueObject esnListValueObject = new EsnListValueObject();

		try {
			log.info("START: getEsnDetails  DAO  Start Obj " + "esnNbr:" + CommonUtility.deNull(esnNbr) + "custId:" + CommonUtility.deNull(custId));

			sb.append(
					"SELECT A.*,B.CNTR_NBR FROM (SELECT E.BK_REF_NBR AS BKRNBR,SHIPPER_NM,PKG_TYPE,ES.CRG_DES AS CRGDESC,ESN_HS_CODE,NBR_PKGS, ");
			sb.append("ESN_WT,ESN_VOL,ESN_DG_IND,ESN_OPS_IND,STG_IND,ESN_LOAD_FROM,HS_SUB_CODE_FR, HS_SUB_CODE_TO, ");
			sb.append(
					"ESN_DUTY_GOOD_IND,TRUCKER_PHONE_NBR,TRUCKER_IC,TRUCKER_NM,PAYMENT_MODE,ACCT_NBR,ESN_PORT_DIS,STG_DAYS,UA_NBR_PKGS, ");
			sb.append(
					"VARIANCE_PKGS,VARIANCE_VOL,VARIANCE_WT,BK_NBR_PKGS,BK_WT,BK_VOL,E.STUFF_IND,CODE.CC_NAME,CODE.CC_CD, ");
			sb.append("ES.TRUCKER_NBR_PKGS, E.EPC_IND,E.CNTR_SEQ_NBR, E.MISC_APP_NBR, ES.CUSTOM_HS_CODE ");
			sb.append(" FROM BK_DETAILS BK, ESN E, ESN_DETAILS ES ,CARGO_CATEGORY_CODE CODE   ");
			sb.append("WHERE BK.BK_REF_NBR = E.BK_REF_NBR ");
			sb.append(
					"AND E.CARGO_CATEGORY_CD = CODE.CC_CD AND E.ESN_ASN_NBR = ES.ESN_ASN_NBR AND E.ESN_ASN_NBR =:esnNo ) A ");
			sb.append("LEFT OUTER JOIN CNTR B ON A.CNTR_SEQ_NBR = B.CNTR_SEQ_NBR ");

			sql = sb.toString();

			paramMap.put("esnNo", esnNbr);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			log.info("END: *** getEsnDetails SQL *****" + sql);
			if (rs.next()) {
				esnListValueObject.setBookingRefNo(CommonUtility.deNull(rs.getString("BKRNBR")));
				bookingRefNbr = esnListValueObject.getBookingRefNo();
				clsShpInd = getClsShpInd(bookingRefNbr);
				esnListValueObject.setShipperName(CommonUtility.deNull(rs.getString("SHIPPER_NM")));
				esnListValueObject.setPkgType(CommonUtility.deNull(rs.getString("PKG_TYPE")));
				esnListValueObject.setCrgDesc(CommonUtility.deNull(rs.getString("CRGDESC")));
				esnListValueObject.setHsCode(CommonUtility.deNull(rs.getString("ESN_HS_CODE")));
				esnListValueObject.setNoOfCntr(rs.getInt("NBR_PKGS"));
				esnListValueObject.setVarNoofPakgs(rs.getDouble("VARIANCE_PKGS"));
				esnListValueObject.setVarGrVolume(rs.getDouble("VARIANCE_VOL"));
				esnListValueObject.setVarGrWt(rs.getDouble("VARIANCE_WT"));
				esnListValueObject.setBGrVolume(rs.getDouble("BK_VOL"));
				esnListValueObject.setBGrWt(rs.getDouble("BK_WT"));
				esnListValueObject.setBNoofPkgs(rs.getInt("BK_NBR_PKGS"));
				esnListValueObject.setGrWt(rs.getDouble("ESN_WT"));
				esnListValueObject.setGrVolume(rs.getDouble("ESN_VOL"));
				esnListValueObject.setDgInd(CommonUtility.deNull(rs.getString("ESN_DG_IND")));
				esnListValueObject.setOpInd(CommonUtility.deNull(rs.getString("ESN_OPS_IND")));
				esnListValueObject.setStgInd(CommonUtility.deNull(rs.getString("STG_IND")));
				esnListValueObject.setPortL(CommonUtility.deNull(rs.getString("ESN_LOAD_FROM")));
				esnListValueObject.setDutiGI(CommonUtility.deNull(rs.getString("ESN_DUTY_GOOD_IND")));
				esnListValueObject.setNoOfdays(rs.getInt("STG_DAYS"));
				esnListValueObject.setUaNoofPkgs(rs.getInt("UA_NBR_PKGS"));
				esnListValueObject.setPkgDesc(pkgsDesc);
				esnListValueObject.setCrgMarking(markings);
				esnListValueObject.SetTruckerCNo(CommonUtility.deNull(rs.getString("TRUCKER_PHONE_NBR")));
				esnListValueObject.setTruckerNo(CommonUtility.deNull(rs.getString("TRUCKER_IC")));
				esnListValueObject.setTruckerName(CommonUtility.deNull(rs.getString("TRUCKER_NM")));
				esnListValueObject.setStfInd(CommonUtility.deNull(rs.getString("STUFF_IND"))); // added by vani
				esnListValueObject.setCategory(rs.getString("CC_NAME")); // added by ZhenguoDeng(harbor)
				payMode = CommonUtility.deNull(rs.getString("PAYMENT_MODE"));
				accNo = CommonUtility.deNull(rs.getString("ACCT_NBR"));
				if (payMode.equals("A")) {
					billableParty = getBillablePartyName(accNo);
					esnListValueObject.setBillPartyName(billableParty);
				}
				portDis = CommonUtility.deNull(rs.getString("ESN_PORT_DIS"));
				portDisDesc = getPortDisDesc(portDis);
				esnListValueObject.setPayMode(payMode);
				esnListValueObject.setAccNo(accNo);
				esnListValueObject.setPortDesc(portDisDesc);
				esnListValueObject.setPortD(portDis);
				esnListValueObject.setClsShpInd(clsShpInd);

				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
				esnListValueObject.setHsSubCodeFr(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				esnListValueObject.setHsSubCodeTo(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));
				esnListValueObject.setHsSubCodeDesc(getHSSubCodeDes(esnListValueObject.getHsCode(),
						esnListValueObject.getHsSubCodeFr(), esnListValueObject.getHsSubCodeTo()));
				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END
				// HaiTTH1 added on 18/1/2014
				esnListValueObject.setTrucker_nbr_pkg(CommonUtility.deNull(rs.getString("TRUCKER_NBR_PKGS")));
				esnListValueObject.setCc_cd(CommonUtility.deNull(rs.getString("cc_cd")));
				// MCC for EPC_IND
				esnListValueObject.setDeliveryToEPC(CommonUtility.deNull(rs.getString("EPC_IND")));

				esnListValueObject.setCntrNbr(CommonUtility.deNull(rs.getString("CNTR_NBR")));
				// START CR FTZ HSCODE - NS JULY 2024
				esnListValueObject.setCustomHsCode(CommonUtility.deNull(rs.getString("CUSTOM_HS_CODE")));
				// END CR FTZ HSCODE - NS JULY 2024
				esnList.add(esnListValueObject);

			}

		} catch (NullPointerException e) {
			log.info("exception getEsnDetails :", e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception getEsnDetails : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEsnDetails : ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getEsnDetails  DAO  END");
		}
		return esnList;
	}

	@Override
	public List<VesselVoyValueObject> getTransferVslCrgList_T(String vslnm, String ovoynbr) throws BusinessException {
		String sql = "";
		int i = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append("	vv_cd , ");
		sb.append("	out_voy_nbr, ");
		sb.append("	vsl_nm ");
		sb.append("FROM ");
		sb.append("	vessel_call ");
		sb.append("WHERE ");
		sb.append("	vv_status_ind IN ('PR', 'AP', 'AL', 'BR', 'UB') ");
		sb.append("	AND create_cust_cd IS NOT NULL ");
		sb.append("	AND vsl_nm = :vslnm ");
		sb.append("	AND out_voy_nbr = :ovoynbr");

		sql = sb.toString();
		Map<String, String> paramMap = new HashMap<String, String>();
		List<VesselVoyValueObject> vesselList = new ArrayList<VesselVoyValueObject>();
		VesselVoyValueObject vesselVoyValueObject = null;
		SqlRowSet rs = null;
		try {
			log.info("START: getTransferVslCrgList_T  DAO  Start vslnm " + CommonUtility.deNull(vslnm) + " ovoynbr:" + CommonUtility.deNull(ovoynbr));

			paramMap.put("vslnm", vslnm);
			paramMap.put("ovoynbr", ovoynbr);

			log.info(" *** getTransferVslCrgList_T SQL *****" + sql + " paramMap " + paramMap.toString());
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				i++;
				vesselVoyValueObject = new VesselVoyValueObject();
				vesselVoyValueObject.setVoyNo(CommonUtility.deNull(rs.getString("out_voy_nbr")));
				vesselVoyValueObject.setVslName(CommonUtility.deNull(rs.getString("vsl_nm")));
				vesselVoyValueObject.setVarNbr(CommonUtility.deNull(rs.getString("vv_cd")));
				vesselList.add(vesselVoyValueObject);
			}
			if (i == 0) {

				log.info("Invalid Vessel Voyage ");
				throw new BusinessException("M20635");
			}
			log.info("vesselList: " + vesselList);
			return vesselList;
		} catch (BusinessException e) {
			log.info("Exception: getTransferVslCrgList_T", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception: getTransferVslCrgList_T", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception: getTransferVslCrgList_T", e);
			throw new BusinessException(e.getMessage());
		} finally {
			log.info("END: *** getTransferVslCrgList_T  END *****");
		}
	}

	@Override
	public List<VesselVoyValueObject> getTransferVslCrgList_F(String vslnm, String ovoynbr) throws BusinessException {
		String sql = "";
		int i = 0;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append("	vv_cd , ");
		sb.append("	out_voy_nbr, ");
		sb.append("	vsl_nm ");
		sb.append("FROM ");
		sb.append("	vessel_call ");
		sb.append("WHERE ");
		sb.append("	vv_status_ind IN ('PR', 'AP', 'AL', 'BR', 'UB') ");
		sb.append("	AND create_cust_cd IS NOT NULL ");
		sb.append("	AND vsl_nm = :vslnm ");
		sb.append("	AND out_voy_nbr = :ovoynbr");

		sql = sb.toString();
		SqlRowSet rs = null;
		List<VesselVoyValueObject> vesselList = new ArrayList<VesselVoyValueObject>();
		VesselVoyValueObject vesselVoyValueObject = null;
		try {
			log.info("START: getTransferVslCrgList_F  DAO  Start vslnm " + CommonUtility.deNull(vslnm) + " ovoynbr:" + CommonUtility.deNull(ovoynbr));

			paramMap.put("vslnm", vslnm);
			paramMap.put("ovoynbr", ovoynbr);

			log.info(" *** getTransferVslCrgList_F SQL *****" + sql + " paramMap " + paramMap.toString());
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				i++;
				vesselVoyValueObject = new VesselVoyValueObject();
				vesselVoyValueObject.setVoyNo(CommonUtility.deNull(rs.getString("out_voy_nbr")));
				vesselVoyValueObject.setVslName(CommonUtility.deNull(rs.getString("vsl_nm")));
				vesselVoyValueObject.setVarNbr(CommonUtility.deNull(rs.getString("vv_cd")));
				vesselList.add(vesselVoyValueObject);
			}
			if (i == 0) {
				log.info("Invalid Vessel Voyage ");
				throw new BusinessException("M20635");
			}
			log.info("vesselList: " + vesselList);
			return vesselList;
		} catch (BusinessException e) {
			log.info("Exception: getTransferVslCrgList_F", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception: getTransferVslCrgList_F", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception: getTransferVslCrgList_F", e);
			throw new BusinessException(e.getMessage());
		} finally {
			log.info("END: *** getTransferVslCrgList_F  END *****");
		}

	}

	@Override
	public String getTransferVarno(String vslnm, String voynbr, String ind, String cust_cd) throws BusinessException {
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		String varnbr = "";
		if (ind.equals("F")) {
			sql = "SELECT vc.vv_cd from vessel_call vc WHERE vc.vsl_nm = :vslnm and vc.out_voy_nbr= :voynbr and   vc.vv_status_ind not in ('CX') ";
			paramMap.put("vslnm", vslnm);
			paramMap.put("voynbr", voynbr);
		} else {
			if (cust_cd.equals("JP")) {
				sql = "SELECT vv_cd from vessel_call  WHERE vsl_nm = :vslnm and out_voy_nbr= :voynbr and   vv_status_ind not in ('CL, CX') and gb_close_shp_ind <> 'Y'";
				paramMap.put("vslnm", vslnm);
				paramMap.put("voynbr", voynbr);
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT ");
				sb.append("	vc.vv_cd ");
				sb.append("FROM ");
				sb.append("	vessel_call vc, ");
				sb.append("	vessel_declarant vd ");
				sb.append("WHERE ");
				sb.append("	vc.vsl_nm = :vslnm ");
				sb.append("	AND vc.out_voy_nbr = :voynbr ");
				sb.append("	AND vc.vv_status_ind NOT IN ('UB, CL, CX') ");
				sb.append("	AND vc.gb_close_shp_ind <> 'Y' ");
				sb.append("	AND vc.vv_cd = vd.vv_cd(+) ");
				sb.append("	AND (vc.create_cust_cd = :cust_cd ");
				sb.append("	OR vd.vv_cd = :cust_cd)");
				sql = sb.toString();
				paramMap.put("vslnm", vslnm);
				paramMap.put("voynbr", voynbr);
				paramMap.put("cust_cd", cust_cd);
			}
		}
		try {
			log.info("START: getTransferVarno  DAO  Start vslnm: " + CommonUtility.deNull(vslnm) + " voynbr:" + CommonUtility.deNull(voynbr) + " cust_cd: "
					+ CommonUtility.deNull(cust_cd) + " ind: " + CommonUtility.deNull(ind));

			log.info(" *** getTransferVarno SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				varnbr = rs.getString("vv_cd");
			}
			log.info("varnbr: " + varnbr);
			return varnbr;
		} catch (NullPointerException e) {
			log.info("Exception: getTransferVarno ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: getTransferVarno ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getTransferVarno  END *****");
		}
	}

	@Override
	public List<EsnListValueObject> getTransferDetails(String vv_cd, String cust_cd) throws BusinessException {
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;
		if (cust_cd.equals("JP")) {
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT ");
			sb.append("	bk.BK_REF_NBR AS bkrnbr, ");
			sb.append("	bk.actual_nbr_shipped AS actnbrshp, ");
			sb.append("	nvl(bk.shutout_qty, 0) AS shutout_qty, ");
			sb.append("	nvl(bk.TRANSFER_PKGS, 0) AS tsfNbr, ");
			sb.append("	e.TRANS_TYPE, ");
			sb.append("	nvl(ed.UA_NBR_PKGS, 0) AS nbr, ");
			sb.append("	nvl(tesnjpjp.UA_NBR_PKGS, 0) AS jpjpnbr, ");
			sb.append("	nvl(tesnpsajp.UA_NBR_PKGS, 0) AS psajpnbr, ");
			sb.append("	e.esn_asn_nbr, ");
			sb.append("	to_char(ed.first_trans_dttm, 'dd/mm/yyyy hh24:mi') AS esnfdttm , ");
			sb.append("	to_char(tesnjpjp.first_trans_dttm, 'dd/mm/yyyy hh24:mi') AS tesnjpjpfdttm, ");
			sb.append("	to_char(tesnpsajp.first_trans_dttm, 'dd/mm/yyyy hh24:mi') AS tesnpsajpfdttm ");
			sb.append("FROM ");
			sb.append("	bk_details bk, ");
			sb.append("	esn e, ");
			sb.append("	esn_details ed, ");
			sb.append("	tesn_jp_jp tesnjpjp, ");
			sb.append("	tesn_psa_jp tesnpsajp ");
			sb.append("WHERE ");
			sb.append("	bk.BK_REF_NBR = e.BK_REF_NBR ");
			sb.append("	AND e.ESN_ASN_NBR = ed.ESN_ASN_NBR(+) ");
			sb.append("	AND tesnjpjp.ESN_ASN_NBR(+)= e.ESN_ASN_NBR ");
			sb.append("	AND tesnpsajp.ESN_ASN_NBR(+)= e.ESN_ASN_NBR ");
			sb.append("	AND bk.bk_status = 'A' ");
			sb.append("	AND e.ESN_STATUS = 'A' ");
			sb.append("	AND e.TRANS_TYPE IN('A', 'E', 'C') ");
			sb.append("	AND OUT_VOY_VAR_NBR = :vv_cd");

			sql = sb.toString();
			paramMap.put("vv_cd", vv_cd);
		} else {

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT ");
			sb.append("	bk.BK_REF_NBR AS bkrnbr, ");
			sb.append("	bk.actual_nbr_shipped AS actnbrshp, ");
			sb.append("	nvl(bk.shutout_qty, 0) AS shutout_qty, ");
			sb.append("	nvl(bk.TRANSFER_PKGS, 0) AS tsfNbr, ");
			sb.append("	e.TRANS_TYPE, ");
			sb.append("	nvl(ed.UA_NBR_PKGS, 0) AS nbr, ");
			sb.append("	nvl(tesnjpjp.UA_NBR_PKGS, 0) AS jpjpnbr, ");
			sb.append("	nvl(tesnpsajp.UA_NBR_PKGS, 0) AS psajpnbr, ");
			sb.append("	e.esn_asn_nbr, ");
			sb.append("	to_char(ed.first_trans_dttm, 'dd/mm/yyyy hh24:mi') AS esnfdttm , ");
			sb.append("	to_char(tesnjpjp.first_trans_dttm, 'dd/mm/yyyy hh24:mi') AS tesnjpjpfdttm, ");
			sb.append("	to_char(tesnpsajp.first_trans_dttm, 'dd/mm/yyyy hh24:mi') AS tesnpsajpfdttm ");
			sb.append("FROM ");
			sb.append("	bk_details bk, ");
			sb.append("	esn e, ");
			sb.append("	esn_details ed, ");
			sb.append("	tesn_jp_jp tesnjpjp, ");
			sb.append("	tesn_psa_jp tesnpsajp ");
			sb.append("WHERE ");
			sb.append("	bk.BK_REF_NBR = e.BK_REF_NBR ");
			sb.append("	AND e.ESN_ASN_NBR = ed.ESN_ASN_NBR(+) ");
			sb.append("	AND tesnjpjp.ESN_ASN_NBR(+)= e.ESN_ASN_NBR ");
			sb.append("	AND tesnpsajp.ESN_ASN_NBR(+)= e.ESN_ASN_NBR ");
			sb.append("	AND bk.bk_status = 'A' ");
			sb.append("	AND e.ESN_STATUS = 'A' ");
			sb.append("	AND e.TRANS_TYPE IN('A', 'E', 'C') ");
			sb.append("	AND OUT_VOY_VAR_NBR = :vv_cd ");
			sb.append("	AND e.ESN_CREATE_CD = :cust_cd");

			sql = sb.toString();
			paramMap.put("vv_cd", vv_cd);
			paramMap.put("cust_cd", cust_cd);
		}
		log.info(sql);
		List<EsnListValueObject> bk_detailst = new ArrayList<EsnListValueObject>();
		EsnListValueObject esnListValueObject = null;
		String actnbrshp = "";
		try {
			log.info("START: getTransferDetails  DAO  Start vv_cd: " + CommonUtility.deNull(vv_cd) + " cust_cd: " + CommonUtility.deNull(cust_cd));

			log.info(" *** getTransferDetails SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				actnbrshp = rs.getString("actnbrshp");

				if (actnbrshp == null || actnbrshp.equals(""))
					actnbrshp = "X";

				esnListValueObject = new EsnListValueObject();
				esnListValueObject.setBookingRefNo(CommonUtility.deNull(rs.getString("bkrnbr")));
				esnListValueObject.setCrgType(actnbrshp);
				esnListValueObject.setAccNo(CommonUtility.deNull(rs.getString("shutout_qty")));
				esnListValueObject.setBNoofPkgs(rs.getInt(4));
				String transtype = CommonUtility.deNull(rs.getString("TRANS_TYPE"));

				int esn_nbr_pkgs = rs.getInt(6);
				int jpjp_nbr_pkgs = rs.getInt(7);
				int psajp_nbr_pkgs = rs.getInt(8);

				int nbr_pkgs = 0;
				String ftdttm = "";

				if (transtype.equals("E")) {
					nbr_pkgs = esn_nbr_pkgs;
					ftdttm = rs.getString("esnfdttm");
				} else if (transtype.equals("A")) {
					nbr_pkgs = jpjp_nbr_pkgs;
					ftdttm = rs.getString("tesnjpjpfdttm");
				} else if (transtype.equals("C")) {
					nbr_pkgs = psajp_nbr_pkgs;
					ftdttm = rs.getString("tesnpsajpfdttm");
				}

				esnListValueObject.setNoofPkgs(nbr_pkgs);
				esnListValueObject.setEsnNbr(rs.getLong(9));
				esnListValueObject.setTranstype(transtype);
				esnListValueObject.setFirstCName(ftdttm);

				bk_detailst.add(esnListValueObject);
			}
		} catch (NullPointerException e) {
			log.info("Exception: getTransferDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: getTransferDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getTransferDetails  END  bk_detailst: " + bk_detailst);
		}
		return bk_detailst;
	}

	@Override
	public String getSysdate() throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = "";
		String sdate = "";
		sql = "SELECT TO_CHAR(SYSDATE,'DD/MM/YYYY HH24:MI') FROM DUAL";
		try {
			log.info("START: getSysdate  DAO  Start  ");
			log.info("getSysdate SQL: " + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				sdate = rs.getString(1);
			}
		} catch (NullPointerException e) {
			log.info("Exception: getSysdate ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: getSysdate ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getSysdate  END  sdate: " + sdate);
		}
		return sdate;
	}

	@Override
	public String getBkStatus(String bookref) throws BusinessException {
		String bookinRefNo = bookref;
	    String bkStatus = "";
	    String sql = "";
	    SqlRowSet rs = null;
	    Map<String, String> paramMap = new HashMap<>();
	    try {
	    	log.info("START: getBkStatus  bookref: " + CommonUtility.deNull(bookref));
	    	sql = " select bk_status from bk_details where bk_ref_nbr = :bookinRefNo ";
	    	paramMap.put("bookinRefNo", bookinRefNo);
	    	log.info("getBkStatus SQL: " + sql + " , paramMap: " + paramMap);
	    	rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
	    	while (rs.next()) {
	            bkStatus = CommonUtility.deNull(rs.getString("bk_status"));
	        }
	    } catch (NullPointerException e) {
			log.info("Exception: getBkStatus ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: getBkStatus ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getBkStatus  END   bkStatus: " + bkStatus);
		}
		return bkStatus;
	}

	@Override
	public Boolean getVesselATUDttm(String bookingRefNo) throws BusinessException{
		
			SqlRowSet rs = null;
			Map<String, String> paramMap = new HashMap<String, String>();
			StringBuilder sb = new StringBuilder();
			String sql = "";
			boolean vesselATUDttm = false;
			try {
				log.info("START: getVesselATUDttm  DAO  Start Obj " + " bookingRefNo:" + CommonUtility.deNull(bookingRefNo));

				sb.append("SELECT B.ATU_DTTM FROM BERTHING B WHERE B.VV_CD = (SELECT BK.VAR_NBR ");
				sb.append("FROM BK_DETAILS BK WHERE BK.BK_REF_NBR = :bookingRefNo )");
				sb.append("ORDER BY SHIFT_IND DESC"); //Added By TOS for PB#123
				sql = sb.toString();
				log.info(" *** getVesselATUDttm SQL *****" + sql);
				paramMap.put("bookingRefNo", bookingRefNo);
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				
				if (rs.next()) {
					vesselATUDttm = rs.getString("ATU_DTTM") == null ? false : true ;
				}
				log.info("END: *** getVesselATUDttm Result *****" + vesselATUDttm);

			} catch (NullPointerException e) {
				log.info("exception getVesselATUDttm :", e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception getVesselATUDttm : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: getVesselATUDttm  DAO  END");
			}

			return vesselATUDttm;
		}

	// START CR FTZ HSCODE - NS JULY 2024
	@Override
	public List<HsCodeDetails> getHsCodeDetailList(String esnNo) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: getHsCodeDetailList  DAO esnNo:" + esnNo );
			sb.append(" SELECT ESN_HSCODE_SEQ_NBR HSCODE_SEQ_NBR, ESN_ASN_NBR, HS_CODE, CUSTOM_HS_CODE, HS_SUB_CODE_FR,");
			sb.append("HS_SUB_CODE_TO, HS_SUB_CODE_DESC, NBR_PKGS, GROSS_WT, GROSS_VOL, CRG_DES, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM");
			sb.append(" FROM GBMS.ESN_HSCODE_DETAILS WHERE ESN_ASN_NBR = :esnNo ");	
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
	
	@Override
	public boolean updateCustomDetail(List<HsCodeDetails> multiHsCodeList,String esnNo, String userId) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: getHsCodeDetailList  DAO esnNo:" + esnNo +", multiHsCodeList :" + multiHsCodeList.toString() );
			for (HsCodeDetails hsCodeObj : multiHsCodeList) {					

				StringBuilder sbhscode = new StringBuilder();
				if(hsCodeObj.getIsHsCodeChange().equalsIgnoreCase("Y")) {
					sbhscode.append(" UPDATE GBMS.ESN_DETAILS SET CUSTOM_HS_CODE = :CUSTOM_HS_CODE ");
					sbhscode.append(" where ESN_ASN_NBR =:esnNo ");
					paramMap.put("esnNo", esnNo);
					paramMap.put("CUSTOM_HS_CODE",hsCodeObj.getCustomHsCode());
					log.info("SQL" + sbhscode.toString());
					log.info("paramMap" + paramMap.toString());
					int countmain = namedParameterJdbcTemplate.update(sbhscode.toString(), paramMap);
					log.info("countmain : " + countmain);
				}
				
				if( !hsCodeObj.getHscodeSeqNbr().isEmpty()) {
					sbhscode.setLength(0);
					sbhscode.append(" UPDATE GBMS.ESN_HSCODE_DETAILS SET CUSTOM_HS_CODE = :CUSTOM_HS_CODE, LAST_MODIFY_USER_ID = :userId,  LAST_MODIFY_DTTM = SYSDATE   ");
					sbhscode.append(" where ESN_HSCODE_SEQ_NBR =:ESN_HSCODE_SEQ_NBR ");
					paramMap.put("ESN_HSCODE_SEQ_NBR", hsCodeObj.getHscodeSeqNbr());	
					paramMap.put("CUSTOM_HS_CODE",hsCodeObj.getCustomHsCode());
					paramMap.put("userId", userId);
					log.info("SQL" + sbhscode.toString());
					log.info("paramMap" + paramMap.toString());
					int counths = namedParameterJdbcTemplate.update(sbhscode.toString(), paramMap);
					log.info("counths : " + counths);
					
					sbhscode.setLength(0);
					sbhscode.append(" INSERT INTO GBMS.ESN_HSCODE_DETAILS_TRANS  ");
					sbhscode.append(" (ESN_ASN_NBR, ESN_HSCODE_SEQ_NBR, AUDIT_DTTM, REC_STATUS, HS_CODE,HS_SUB_CODE_FR,HS_SUB_CODE_TO,NBR_PKGS,GROSS_WT,GROSS_VOL,CUSTOM_HS_CODE,CRG_DES, HS_SUB_CODE_DESC,LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
					sbhscode.append(" VALUES(:ESN_ASN_NBR,:ESN_HSCODE_SEQ_NBR, SYSDATE, 'A',:HS_CODE,:HS_SUB_CODE_FR,:HS_SUB_CODE_TO,:NBR_PKGS,:GROSS_WT,:GROSS_VOL,:CUSTOM_HS_CODE,:CRG_DES,:HS_SUB_CODE_DESC,:userId,SYSDATE) ");
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
					paramMap.put("userId", userId);
					log.info("SQL" + sbhscode.toString());
					log.info("paramMap" + paramMap.toString());
					int counthsAudit = namedParameterJdbcTemplate.update(sbhscode.toString(), paramMap);
					log.info("counths : " + counthsAudit);
				}
			}
			
		} catch (NullPointerException e) {
			log.info("Exception loadHSSubCode : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception loadHSSubCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: loadHSSubCode  DAO ");
		}
		return true;
	}
	// END CR FTZ HSCODE - NS JULY 2024
	
}
