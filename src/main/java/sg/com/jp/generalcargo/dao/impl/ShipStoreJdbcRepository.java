package sg.com.jp.generalcargo.dao.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

import sg.com.jp.generalcargo.dao.OPSEventLogRepository;
import sg.com.jp.generalcargo.dao.ShipStoreRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ShipStoreValueObject;
import sg.com.jp.generalcargo.domain.StoreRentCrReport;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.DpeCommonUtil;
import sg.com.jp.generalcargo.util.GbmsCommonUtility;

@Repository("shipStoreRepository")
public class ShipStoreJdbcRepository implements ShipStoreRepository {
	private static final Log log = LogFactory.getLog(ShipStoreJdbcRepository.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public String logStatusGlobal = "N";
	private static final int MAX_TRY = 10;
	public static final String TXN_CD_SS_ADMIN = "SSAD";
	@Autowired
	private OPSEventLogRepository oPSEventLog;

	// ejb.sessionBeans.gbms.cargo.shipstore-->ShipStore

	public String updateSSDetails(String ShpStrNo, String custCd, String truckerIcNo, String truckerCNo, String marking,
			String dgIn, String hsCode, String dutiDI, String truckerName, String pkgsType, int noOfPkgs, double weight,
			double volume, String accNo, String payMode, String cargoDesc, String truckerCd, String UserID,
			String crgType, String shpradd, String adminFeeInd, String reasonForWaive) throws BusinessException {
		SqlRowSet rs1 = null;
		String strMark = new String();
		String strEsnDetails = new String();
		String sqlTrans = "";
		String strMarkTrans = new String();
		String strInsertTrans = new String();
		String transNumShpStr = "";
		int transNumShpInt = 0;

		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE ");
		sb.append("	SS_DETAILS ");
		sb.append("SET ");
		sb.append("	NBR_PKGS = :noOfPkgs, ");
		sb.append("	SS_WT = :weight, ");
		sb.append("	SS_VOL = :volume, ");
		sb.append("	CRG_DES = :cargoDesc, ");
		sb.append("	PKG_TYPE = :pkgsType, ");
		sb.append("	SS_HS_CODE = :hsCode, ");
		sb.append("	CARGO_TYPE = :crgType, ");
		sb.append("	SHIPPER_CD = :custCd, ");
		sb.append("	SHIPPER_CR_NBR = :truckerIcNo, ");
		sb.append("	SHIPPER_CONTACT =:truckerCNo, ");
		sb.append("	SHIPPER_ADDR = :shpradd, ");
		sb.append("	SHIPPER_NM = :truckerName, ");
		sb.append("	ACCT_NBR = :accNo, ");
		sb.append("	SS_DG_IND = :dgIn, ");
		sb.append("	SS_DUTY_GOOD_IND = :dutiDI, ");
		sb.append("	PAYMENT_MODE = :payMode, ");
		sb.append("	LAST_MODIFY_USER_ID = :UserID, ");
		sb.append("	LAST_MODIFY_DTTM = sysdate , ");
		sb.append("	ADMIN_FEE_WAIVER_IND = :adminFeeInd, ");
		sb.append("	ADMIN_FEE_WAIVER_REASON = :reasonForWaive ");
		sb.append("WHERE ");
		sb.append("	ESN_ASN_NBR = :ShpStrNo");
		strEsnDetails = sb.toString();

		strMark = "UPDATE ESN_MARKINGS SET MARKINGS = :marking WHERE ESN_ASN_NBR = :ShpStrNo";

		sqlTrans = "SELECT MAX(TRANS_NBR) FROM SS_DETAILS_TRANS WHERE ESN_ASN_NBR = :ShpStrNo ";

		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: updateSSDetails  DAO  Start ShpStrNo " + CommonUtility.deNull(ShpStrNo) + " custCd" + CommonUtility.deNull(custCd) + " truckerIcNo "
					+ CommonUtility.deNull(truckerIcNo) + "truckerCNo" + CommonUtility.deNull(truckerCNo) + " marking " + CommonUtility.deNull(marking)
					+ " dgIn " + CommonUtility.deNull(dgIn) + " hsCode " + CommonUtility.deNull(hsCode) + " dutiDI " + CommonUtility.deNull(dutiDI)
					+ " truckerName " + CommonUtility.deNull(truckerName) + " pkgsType " + CommonUtility.deNull(pkgsType) + " noOfPkgs " + CommonUtility.deNull(String.valueOf(noOfPkgs)) 
					+ " weight" + CommonUtility.deNull(String.valueOf(weight)) + " volume " + CommonUtility.deNull(String.valueOf(volume)) + " accNo " + CommonUtility.deNull(accNo)
					+ " payMode " + CommonUtility.deNull(payMode) + " cargoDesc " + CommonUtility.deNull(cargoDesc) + " truckerCd " + CommonUtility.deNull(truckerCd) + " UserID "
					+ CommonUtility.deNull(UserID) + " crgType" + CommonUtility.deNull(crgType) + " shpradd " + CommonUtility.deNull(shpradd) + " adminFeeInd "
					+ CommonUtility.deNull(adminFeeInd) + " reasonForWaive " + CommonUtility.deNull(reasonForWaive));

			if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y") {
				paramMap.put("ShpStrNo", ShpStrNo);

				log.info(" updateSSDetails  DAO  SQL " + sqlTrans + " paramMap: " + paramMap);
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqlTrans, paramMap);
				while (rs1.next()) {
					transNumShpStr = rs1.getString(1);
				}

				if (transNumShpStr == null || transNumShpStr == "") {
					transNumShpInt = 0;
				} else {
					transNumShpInt = Integer.parseInt(transNumShpStr);
					transNumShpInt++;
				}

				StringBuffer sb5 = new StringBuffer();
				sb5.append("INSERT ");
				sb5.append("	INTO ");
				sb5.append("	SS_DETAILS_TRANS(ESN_ASN_NBR, ");
				sb5.append("	TRANS_NBR, ");
				sb5.append("	NBR_PKGS, ");
				sb5.append("	SS_WT, ");
				sb5.append("	SS_VOL, ");
				sb5.append("	CRG_DES, ");
				sb5.append("	PKG_TYPE, ");
				sb5.append("	SS_HS_CODE, ");
				sb5.append("	CARGO_TYPE, ");
				sb5.append("	SHIPPER_CD, ");
				sb5.append("	SHIPPER_CR_NBR, ");
				sb5.append("	SHIPPER_CONTACT, ");
				sb5.append("	SHIPPER_ADDR, ");
				sb5.append("	SHIPPER_NM, ");
				sb5.append("	ACCT_NBR, ");
				sb5.append("	SS_DG_IND, ");
				sb5.append("	SS_DUTY_GOOD_IND, ");
				sb5.append("	UA_NBR_PKGS, ");
				sb5.append("	PAYMENT_MODE, ");
				sb5.append("	LAST_MODIFY_USER_ID, ");
				sb5.append("	LAST_MODIFY_DTTM) ");
				sb5.append("VALUES(:ShpStrNo, ");
				sb5.append(":shpStrRefNo, ");
				sb5.append(":transNumShpInt, ");
				sb5.append(":noOfPkgs, ");
				sb5.append(":weight, ");
				sb5.append(":volume, ");
				sb5.append(":cargoDesc, ");
				sb5.append(":pkgsType, ");
				sb5.append(":hsCode, ");
				sb5.append(":crgType, ");
				sb5.append(":custCd, ");
				sb5.append(":truckerIcNo, ");
				sb5.append(":truckerCNo, ");
				sb5.append(":shpradd, ");
				sb5.append(":truckerName, ");
				sb5.append(":accNo, ");
				sb5.append(":dgIn, ");
				sb5.append(":dutiDI, ");
				sb5.append("'0', ");
				sb5.append(":payMode, ");
				sb5.append(":UserID, ");
				sb5.append("sysdate)");
				strInsertTrans = sb5.toString();

				strMarkTrans = "INSERT INTO ESN_MARKINGS_Trans(ESN_ASN_NBR,TRANS_NBR,MARKINGS)VALUES(:ShpStrNo,:transNumShpInt,:marking)";

				paramMap = new HashMap<String, Object>();
				paramMap.put("ShpStrNo", ShpStrNo);
				paramMap.put("transNumShpInt", transNumShpInt);
				paramMap.put("noOfPkgs", noOfPkgs);
				paramMap.put("weight", weight);
				paramMap.put("volume", volume);
				paramMap.put("cargoDesc", GbmsCommonUtility.addApostr(cargoDesc));
				paramMap.put("pkgsType", pkgsType);
				paramMap.put("hsCode", hsCode);
				paramMap.put("crgType", crgType);
				paramMap.put("custCd", custCd);
				paramMap.put("truckerIcNo", GbmsCommonUtility.addApostr(truckerIcNo));
				paramMap.put("truckerCNo", truckerCNo);
				paramMap.put("shpradd", shpradd);
				paramMap.put("truckerName", GbmsCommonUtility.addApostr(truckerName));
				paramMap.put("accNo", GbmsCommonUtility.addApostr(accNo));
				paramMap.put("dgIn", dgIn);
				paramMap.put("dutiDI", dutiDI);
				paramMap.put("payMode", payMode);
				paramMap.put("UserID", UserID);
				log.info(" updateSSDetails  DAO  SQL " + strInsertTrans + " paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strInsertTrans, paramMap);

				paramMap = new HashMap<String, Object>();
				paramMap.put("ShpStrNo", ShpStrNo);
				paramMap.put("transNumShpInt", transNumShpInt);
				paramMap.put("marking", GbmsCommonUtility.addApostr(marking));
				log.info(" updateSSDetails  DAO  SQL " + strMarkTrans + " paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strMarkTrans, paramMap);
			}

			paramMap = new HashMap<String, Object>();
			paramMap.put("ShpStrNo", ShpStrNo);
			paramMap.put("noOfPkgs", noOfPkgs);
			paramMap.put("weight", weight);
			paramMap.put("volume", volume);
			paramMap.put("cargoDesc", GbmsCommonUtility.addApostr(cargoDesc));
			paramMap.put("pkgsType", pkgsType);
			paramMap.put("hsCode", hsCode);
			paramMap.put("crgType", crgType);
			paramMap.put("custCd", custCd);
			paramMap.put("truckerIcNo", GbmsCommonUtility.addApostr(truckerIcNo));
			paramMap.put("truckerCNo", truckerCNo);
			paramMap.put("shpradd", shpradd);
			paramMap.put("truckerName", GbmsCommonUtility.addApostr(truckerName));
			paramMap.put("accNo", GbmsCommonUtility.addApostr(accNo));
			paramMap.put("dgIn", dgIn);
			paramMap.put("dutiDI", dutiDI);
			paramMap.put("payMode", payMode);
			paramMap.put("UserID", UserID);
			paramMap.put("adminFeeInd", adminFeeInd);
			paramMap.put("reasonForWaive", reasonForWaive);
			log.info(" updateSSDetails  DAO  SQL " + strEsnDetails + " paramMap: " + paramMap);
			int cntEsnDetails = namedParameterJdbcTemplate.update(strEsnDetails, paramMap);

			paramMap.put("ShpStrNo", ShpStrNo);
			paramMap.put("marking", GbmsCommonUtility.addApostr(marking));
			log.info(" updateSSDetails  DAO  SQL " + strMark + " paramMap: " + paramMap);
			int cntmrk = namedParameterJdbcTemplate.update(strMark, paramMap);
			log.info("updateSSDetails cntmrk: " + cntmrk);

			if (cntEsnDetails == 0 || cntmrk == 0) {
				log.info("Writing from ShipStore.insertEsnDetails");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}

		} catch (BusinessException e) {
			log.info("Exception updateSSDetails : ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception updateSSDetails : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception updateSSDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateSSDetails  DAO  END   ShpStrNo: " + CommonUtility.deNull(ShpStrNo));
		}
		return ShpStrNo;
	}

	public void shpStrCancel(String esnNo) throws BusinessException {
		String strUpdate = new String();
		Map<String, String> paramMap = new HashMap<String, String>();
		strUpdate = "UPDATE ESN SET ESN_STATUS='X' WHERE ESN_ASN_NBR= :esnNo";
		String sql2 = "update misc_event_log set bill_ind = 'X' where txn_cd='SSAD' and ref_nbr = :esnNo ";
		try {
			log.info("START: shpStrCancel  DAO  Start esnNo " + CommonUtility.deNull(esnNo));
			paramMap.put("esnNo", esnNo);

			log.info(" shpStrCancel  DAO  SQL " + strUpdate + " paramMap: " + paramMap);
			int count = namedParameterJdbcTemplate.update(strUpdate, paramMap);

			if (count == 0) {
				log.info("Writing from ShipStore.shpStrCancel");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}

			log.info("Start to cancel billable events for ship store admin fee if any...");
			paramMap.put("esnNo", esnNo);

			log.info(" shpStrCancel  DAO  SQL " + sql2 + " paramMap: " + paramMap);
			namedParameterJdbcTemplate.update(sql2, paramMap);
			log.info("End of cancelling billable events for ship store admin fee if any...");

		} catch (BusinessException e) {
			log.info("Exception shpStrCancel : ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception shpStrCancel : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception shpStrCancel : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: shpStrCancel  DAO  END");
		}
	}

	public List<ShipStoreValueObject> getPkgList(String text) throws BusinessException {
		String sql = "";
		String pkgsText = text;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		if (pkgsText.equals("ALL"))
			// amended by deng zheng guo on 1/8/2011 to fix wrong sql
			sql = "select * from PKG_TYPES where rec_status='A' ORDER BY PKG_TYPE_CD ";
		else
			sql = "select * from PKG_TYPES WHERE PKG_TYPE_CD LIKE :pkgsText and rec_status='A' ORDER BY PKG_TYPE_CD";
		// amended end
		List<ShipStoreValueObject> pkgsList = new ArrayList<ShipStoreValueObject>();
		ShipStoreValueObject shipStoreValueObject = null;
		try {
			log.info("START: getPkgList  DAO  Start text " + CommonUtility.deNull(text));
			if (!pkgsText.equals("ALL"))
				paramMap.put("pkgsText", pkgsText + "%");
			log.info(" getPkgList  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				shipStoreValueObject = new ShipStoreValueObject();
				shipStoreValueObject.setPkgDesc(CommonUtility.deNull(rs.getString("PKG_DESC")));
				shipStoreValueObject.setPkgType(CommonUtility.deNull(rs.getString("PKG_TYPE_CD")));
				pkgsList.add(shipStoreValueObject);
			}

		} catch (NullPointerException e) {
			log.info("Exception getPkgList : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getPkgList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPkgList  DAO  END  pkgsList: " + pkgsList.size());
		}
		return pkgsList;
	}

	public String getVslScheme(String vvCd) throws BusinessException {
		SqlRowSet rs = null;
		String sql = "select scheme from vessel_call where vv_cd =:vvCd";
		String result = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getVslScheme  DAO  Start vvCd " + CommonUtility.deNull(vvCd));
			paramMap.put("vvCd", vvCd);
			log.info(" getVslScheme  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				result = CommonUtility.deNull(rs.getString("scheme"));
			}
		} catch (NullPointerException e) {
			log.info("Exception getVslScheme : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVslScheme : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVslScheme  DAO  END  result: " + result);
		}
		return result;
	}

	private String getPkgsType(String esnNbr) throws BusinessException {
		String esnNo = esnNbr;
		String sql = "";
		String pkgsDesc = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		sql = "select PKG_TYPE_CD,PKG_DESC from PKG_TYPES,ss_details,esn where PKG_TYPES.PKG_TYPE_CD = ss_details.PKG_TYPE and esn.esn_asn_nbr = ss_details.esn_asn_nbr and esn.ESN_ASN_NBR = :esnNo";
		SqlRowSet rs = null;
		try {
			log.info("START: getPkgsType  DAO  Start esnNbr " + CommonUtility.deNull(esnNbr));
			paramMap.put("esnNo", esnNo);
			log.info(" getPkgsType  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				pkgsDesc = CommonUtility.deNull(rs.getString("PKG_DESC"));
			}
			log.info(" getPkgsType  DAO  Result" + pkgsDesc.toString());
		} catch (NullPointerException e) {
			log.info("Exception getPkgsType : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getPkgsType : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPkgsType  DAO  END");
		}
		return pkgsDesc;
	}

	private String getMarkings(String esnNbr) throws BusinessException {
		String esnNo = esnNbr;
		String sql = "";
		String markings = "";
		sql = "select * from ESN_MARKINGS where ESN_ASN_NBR = :esnNo";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getMarkings  DAO  Start esnNbr " + CommonUtility.deNull(esnNbr));
			paramMap.put("esnNo", esnNo);
			log.info(" getMarkings  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				markings = CommonUtility.deNull(rs.getString("MARKINGS"));
			}
			log.info(" getMarkings  DAO  Result" + markings.toString());
		} catch (NullPointerException e) {
			log.info("Exception getMarkings : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getMarkings : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getMarkings  DAO  END");
		}
		return markings;
	}

	private String getClsShpInd(String ssNo) throws BusinessException {
		String shpStr = ssNo;
		String clsShpInd = "";
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		sql = "select vessel_call.gb_close_shp_ind from vessel_call,esn where vessel_call.vv_cd = esn.OUT_VOY_VAR_NBR and ESN_ASN_NBR = :shpStr";
		try {
			log.info("START: getClsShpInd  DAO  Start ssNo " + CommonUtility.deNull(ssNo));
			paramMap.put("shpStr", shpStr);
			log.info(" getClsShpInd  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				clsShpInd = CommonUtility.deNull(rs.getString("gb_close_shp_ind"));
			}
			log.info(" getClsShpInd  DAO  Result" + clsShpInd.toString());
		} catch (NullPointerException e) {
			log.info("Exception clsShpInd : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception clsShpInd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: clsShpInd  DAO  END");
		}
		return clsShpInd;
	}

	private String getCrgTypeCd(String crgType) throws BusinessException {
		String crgTypeDesc = crgType;
		String sql = "";
		String crgTypeCd = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		sql = "select CRG_TYPE_NM from CRG_TYPE where CRG_TYPE_CD= :crgTypeDesc";
		SqlRowSet rs = null;
		try {
			log.info("START: getCrgTypeCd  DAO  Start crgType " + CommonUtility.deNull(crgType));
			paramMap.put("crgTypeDesc", crgTypeDesc);
			log.info(" getCrgTypeCd  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				crgTypeCd = CommonUtility.deNull(rs.getString("CRG_TYPE_NM"));
			}
			log.info(" getCrgTypeCd  DAO  Result" + crgTypeCd.toString());
		} catch (NullPointerException e) {
			log.info("Exception getCrgTypeCd : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCrgTypeCd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCrgTypeCd  DAO  END");
		}
		return crgTypeCd;
	}

	private String getBillablePartyName(String accNbr) throws BusinessException {
		String accNo = accNbr;
		String sql = "";
		String billablePartyName = "";
		SqlRowSet rs = null;
		sql = "select co.co_nm from cust_acct ca, company_code co where co.co_cd = ca.cust_cd and ca.ACCT_NBR = :accNo";
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getBillablePartyName  DAO  Start accNbr " + CommonUtility.deNull(accNbr));
			paramMap.put("accNo", GbmsCommonUtility.addApostr(accNo));
			log.info(" getBillablePartyName  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				billablePartyName = CommonUtility.deNull(rs.getString("co_nm"));
			}
			log.info(" getBillablePartyName  DAO  Result" + billablePartyName.toString());
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

	public List<ShipStoreValueObject> getShpStrDetails(String esnNbr, String custId) throws BusinessException {
		String esnNo = esnNbr;
		String pkgsDesc = getPkgsType(esnNo);
		String markings = getMarkings(esnNo);
		String clsShpInd = "";
		String payMode = "";
		String billableParty = "";
		String accNo = "";
		String sql = "";
		SqlRowSet rs = null;
		sql = "select * From ss_details ss, esn e where e.esn_asn_nbr = ss.esn_asn_nbr and e.esn_asn_nbr = :esnNo ";
		Map<String, String> paramMap = new HashMap<String, String>();
		List<ShipStoreValueObject> esnList = new ArrayList<ShipStoreValueObject>();
		ShipStoreValueObject shipStoreValueObject = new ShipStoreValueObject();
		try {
			log.info("START: getShpStrDetails  DAO  Start esnNbr " + CommonUtility.deNull(esnNbr) + " custId" + CommonUtility.deNull(custId));
			paramMap.put("esnNo", esnNo);
			log.info(" getShpStrDetails  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				clsShpInd = getClsShpInd(esnNo);
				shipStoreValueObject.setBookingRefNo(CommonUtility.deNull(rs.getString("SS_REF_NBR")));
				shipStoreValueObject.setShipperName(CommonUtility.deNull(rs.getString("SHIPPER_ADDR")));
				shipStoreValueObject.setPkgType(CommonUtility.deNull(rs.getString("PKG_TYPE")));
				shipStoreValueObject.setCrgDesc(CommonUtility.deNull(rs.getString("CRG_DES")));
				shipStoreValueObject.setHsCode(CommonUtility.deNull(rs.getString("SS_HS_CODE")));
				shipStoreValueObject.setNoOfCntr(rs.getInt("NBR_PKGS"));
				shipStoreValueObject.setGrWt(rs.getDouble("SS_WT"));
				shipStoreValueObject.setGrVolume(rs.getDouble("SS_VOL"));
				shipStoreValueObject.setDgInd(CommonUtility.deNull(rs.getString("SS_DG_IND")));
				shipStoreValueObject.setDutiGI(CommonUtility.deNull(rs.getString("SS_DUTY_GOOD_IND")));
				shipStoreValueObject.setUaNoofPkgs(rs.getInt("UA_NBR_PKGS"));
				shipStoreValueObject.setPkgDesc(pkgsDesc);
				shipStoreValueObject.setCrgMarking(markings);
				shipStoreValueObject.SetTruckerCNo(CommonUtility.deNull(rs.getString("SHIPPER_CONTACT")));
				shipStoreValueObject.setTruckerNo(CommonUtility.deNull(rs.getString("SHIPPER_CR_NBR")));
				shipStoreValueObject.setTruckerName(CommonUtility.deNull(rs.getString("SHIPPER_NM")));
				shipStoreValueObject.setCrgType(CommonUtility.deNull(rs.getString("CARGO_TYPE")));
				String crgTypeCd = shipStoreValueObject.getCrgType();
				String crgDesc = getCrgTypeCd(crgTypeCd);
				shipStoreValueObject.setCrgStatus(crgDesc);
				shipStoreValueObject.setClsShpInd(clsShpInd);
				shipStoreValueObject.setCustId(CommonUtility.deNull(rs.getString("ESN_CREATE_CD")));
				payMode = CommonUtility.deNull(rs.getString("PAYMENT_MODE"));
				accNo = CommonUtility.deNull(rs.getString("ACCT_NBR"));
				shipStoreValueObject.setAdminFeeInd(CommonUtility.deNull(rs.getString("ADMIN_FEE_WAIVER_IND")));
				shipStoreValueObject.setReasonForWaive(CommonUtility.deNull(rs.getString("ADMIN_FEE_WAIVER_REASON")));
				// Added by mc consulting
				if (payMode.equals("A")) {
					billableParty = getBillablePartyName(accNo);
					shipStoreValueObject.setBillPartyName(billableParty);
				}
				shipStoreValueObject.setAccNo(accNo);
				shipStoreValueObject.setPayMode(CommonUtility.deNull(rs.getString("PAYMENT_MODE")));
				esnList.add(shipStoreValueObject);
			}
			log.info(" getShpStrDetails  DAO  Result" + esnList.toString());
		} catch (BusinessException e) {
			log.info("Exception getShpStrDetails : ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception getShpStrDetails : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getShpStrDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getShpStrDetails  DAO  END");
		}
		return esnList;
	}

	private boolean getAdminWaiveIndUA(String esnNo) throws BusinessException {
		String admin_fee_waiver_ind = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = "select admin_fee_waiver_ind from ss_details where esn_asn_nbr= :esnNo";
		boolean adminFeeWaiver = false;
		try {
			log.info("START: getAdminWaiveIndUA  DAO  Start esnNo " + CommonUtility.deNull(esnNo));
			paramMap.put("esnNo", esnNo);
			log.info(" getAdminWaiveIndUA  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				admin_fee_waiver_ind = CommonUtility.deNull(rs.getString(1));
			}
			log.info("admin_fee_waiver_ind = " + admin_fee_waiver_ind);
			if (admin_fee_waiver_ind != null && admin_fee_waiver_ind.trim().length() > 0
				&& admin_fee_waiver_ind.equalsIgnoreCase("N")) {
				adminFeeWaiver = true;
			}
		} catch (NullPointerException e) {
			log.info("Exception getAdminWaiveIndUA : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getAdminWaiveIndUA : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAdminWaiveIndUA  DAO  END   adminFeeWaiver: " + adminFeeWaiver);
		}

		return adminFeeWaiver;
	}

	private String getVv_cdByEsnno(String esnNo) throws BusinessException {
		SqlRowSet rs = null;
		String sql = "select vessel_call.vv_cd from vessel_call,esn where vessel_call.vv_cd = esn.OUT_VOY_VAR_NBR and ESN_ASN_NBR = :esnNo ";
		String vv_cd = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getVv_cdByEsnno  DAO  Start esnNo " + CommonUtility.deNull(esnNo));
			paramMap.put("esnNo", esnNo);
			log.info(" getVv_cdByEsnno  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				vv_cd = CommonUtility.deNull(rs.getString("vv_cd"));
			}
			log.info(" getVv_cdByEsnno  DAO  Result" + vv_cd.toString());
		} catch (NullPointerException e) {
			log.info("Exception getVv_cdByEsnno : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVv_cdByEsnno : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVv_cdByEsnno  DAO  END");
		}
		return vv_cd;
	}

	/*
	 * Added by MC consulting, calculateSSAdminFeeCharges from SS .
	 */
	public void insertSSAdminFeeEvent(String esnNo, String userID) throws BusinessException {
		try {
			log.info("START: insertSSAdminFeeEvent  DAO  Start esnNo " + CommonUtility.deNull(esnNo) + " userID" + CommonUtility.deNull(userID));
			boolean adminFereeWaiverFlag = getAdminWaiveIndUA(esnNo);
			log.info("****AdminWaiveIndUA Flag =" + adminFereeWaiverFlag);
			Date sysDate = CommonUtility.getSysDate();
			String vv_cd = getVv_cdByEsnno(esnNo);
			oPSEventLog.insertOpsMiscEventLog(new Timestamp(sysDate.getTime()), TXN_CD_SS_ADMIN, null, vv_cd, esnNo,
					userID);
		} catch (BusinessException e) {
			log.info("Exception insertSSAdminFeeEvent : ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception insertSSAdminFeeEvent: ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception insertSSAdminFeeEvent : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertSSAdminFeeEvent  DAO  END");
		}
	}

	private String getDeclarant(String vvCd) throws BusinessException {
		String vvcd = vvCd;
		String sql = "select CREATE_CUST_CD from vessel_call where vv_cd = :vvcd";
		String custId = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getDeclarant  DAO  Start vvCd " + CommonUtility.deNull(vvCd));
			paramMap.put("vvcd", vvcd);
			log.info(" getDeclarant  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next())
				custId = CommonUtility.deNull(rs.getString("CREATE_CUST_CD"));

			log.info(" getDeclarant  DAO  Result" + custId.toString());
		} catch (NullPointerException e) {
			log.info("Exception getDeclarant : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getDeclarant : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getDeclarant  DAO  END");
		}
		return custId;

	}

	private String getShpStrNoForDPE() throws BusinessException {
		String esnasnnbr = "";
		String sql3 = "";
		String sql3_1 = "";
		SqlRowSet rs3_1 = null;
		SqlRowSet rs3 = null;
		SqlRowSet rsasn = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getShpStrNoForDPE  DAO  Start  ");
			String stresnasnnbr = "";
			String strsqldate = "";
			// sql3 = "SELECT MAX(ESN_ASN_NBR) FROM ESN ";
			sql3_1 = "SELECT TO_CHAR(SYSDATE,'Y/MM/DD') AS STRDATE FROM DUAL";
			log.info(" getShpStrNoForDPE  DAO  SQL " + sql3_1 + " paramMap: " + paramMap);
			rs3_1 = namedParameterJdbcTemplate.queryForRowSet(sql3_1, paramMap);
			while (rs3_1.next()) {
				strsqldate = CommonUtility.deNull(rs3_1.getString(1));
			}

			String strsqlyy = strsqldate.substring(0, 1);
			String strsqlmm = strsqldate.substring(2, 4);

			if ((strsqlyy + strsqlmm.substring(0, 1)).equals("00")// Bhuvana 15/09/2010
					|| (strsqlyy + strsqlmm.substring(0, 1)).equals("01")) { // For year ends with 0. ie. 2010, 2020,
																				// etc.
				sql3 = "SELECT MAX(ESN_ASN_NBR) FROM ESN WHERE ESN_ASN_NBR < 1300000";
			} else {
				// sql= sql3 = "SELECT MAX(ESN_ASN_NBR) FROM ESN ";
				// eg. For 2011: Retrieve the max ESN No between ESN No 10000000 and 19999999.
				sql3 = "SELECT MAX(ESN_ASN_NBR) FROM ESN WHERE ESN_ASN_NBR BETWEEN (SELECT TO_NUMBER(TO_CHAR(SYSDATE,'Y')) * 10000000 FROM DUAL)  AND (SELECT ((TO_NUMBER(TO_CHAR(SYSDATE,'Y'))+1) * 10000000)-1 FROM DUAL)";
			}

			log.info(" getShpStrNoForDPE  DAO  SQL " + sql3 + " paramMap: " + paramMap);
			rs3 = namedParameterJdbcTemplate.queryForRowSet(sql3, paramMap);
			while (rs3.next()) {
				stresnasnnbr = CommonUtility.deNull(rs3.getString(1));
			}

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

			int intesnasnnbr = Integer.parseInt(stresnasnnbr.substring(3, 8));
			String stresnasnnbryy = stresnasnnbr.substring(0, 1);
			String stresnasnnbrmm = stresnasnnbr.substring(1, 3);

			if ((stresnasnnbryy.equalsIgnoreCase(strsqlyy)) && (stresnasnnbrmm.equalsIgnoreCase(strsqlmm))) {
				stresnasnnbr = (stresnasnnbryy).concat(stresnasnnbrmm);
				intesnasnnbr = intesnasnnbr + 2;
				

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

					String asnStr = "'";
					for (int i = 0; i <= asnNbrs.size() - 1; i++) {

						if (i <= 18) {
							asnStr = asnStr + asnNbrs.get(i) + "', '";
						}

						if (i == 19) {
							asnStr = asnStr + asnNbrs.get(1) + "'";
						}

					}

					log.info("asnStr = " + asnStr); // Wanyi added

					sqlasn = "select ESN_ASN_NBR from ESN where ESN_ASN_NBR in (:asnStr)";
					List<String> existAsnNbrs = new ArrayList<String>();

					MapSqlParameterSource parameters = new MapSqlParameterSource();
					parameters.addValue("asnStr", Arrays.asList(asnStr.replaceAll("'", "").trim().split(",")));

					log.info(" getShpStrNoForDPE  DAO  SQL " + sqlasn + " paramMap: " + parameters.getValues());
					rsasn = namedParameterJdbcTemplate.queryForRowSet(sqlasn, parameters);
					while (rsasn.next()) {
						dbAsnNbr = CommonUtility.deNull(rsasn.getString(1));

						// Wanyi added on Mar 2020 to cater for 6 or 7 digit ASN numbers
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

			} else {
				stresnasnnbr = (strsqlyy).concat(strsqlmm);
				stresnasnnbr = stresnasnnbr.concat("00002");
			}
			// new number generated
			esnasnnbr = stresnasnnbr;
			log.info(" getShpStrNoForDPE  DAO  Result: " + esnasnnbr.toString());
		} catch (NullPointerException e) {
			log.info("Exception getShpStrNoForDPE : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getShpStrNoForDPE : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getShpStrNoForDPE  DAO  END");
		}
		return esnasnnbr;
	}

	private int addEsn(String esnNo, String varno, String custCd, String UserID) throws BusinessException {
		String strEsnDetails = "";
		int count = 0;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: addEsn  DAO  Start esnNo " + CommonUtility.deNull(esnNo) + " varno" + CommonUtility.deNull(varno) + " custCd"
					+ CommonUtility.deNull(custCd) + " UserID" + CommonUtility.deNull(UserID));
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT ");
			sb.append("	INTO ");
			sb.append("	ESN(ESN_ASN_NBR, ");
			sb.append("	DECLARANT_CR_NO, ");
			sb.append("	TRANS_TYPE, ");
			sb.append("	OUT_VOY_VAR_NBR, ");
			sb.append("	ESN_STATUS, ");
			sb.append("	ESN_CREATE_CD, ");
			sb.append("	LAST_MODIFY_USER_ID, ");
			sb.append("	LAST_MODIFY_DTTM) ");
			sb.append("VALUES(:esnNo, ");
			sb.append("'O', ");
			sb.append("'S', ");
			sb.append(":varno, ");
			sb.append("'A', ");
			sb.append(":custCd, ");
			sb.append(":UserID, ");
			sb.append("sysdate)");
			strEsnDetails = sb.toString();

			paramMap.put("esnNo", esnNo);
			paramMap.put("varno", varno);
			paramMap.put("custCd", custCd);
			paramMap.put("UserID", UserID);
			log.info(" isEsn  DAO  SQL " + strEsnDetails  + " paramMap: " + paramMap);

			count = namedParameterJdbcTemplate.update(strEsnDetails, paramMap);
			log.info("addEsn count: " + count);
		} catch (Exception sqle) {
			if (sqle.getMessage().indexOf("ORA-00001") >= 0) {
				return -2;
			} else {
				log.info("Exception addEsn : ", sqle);
				throw new BusinessException("M4201");
			}
		} finally {
			log.info("END: addEsn  DAO  END   count: " + count);
		}
		return count;
	}

	public String insertSSDetailsForDPE(String varno, String custCd, String truckerIcNo, String truckerCNo,
			String marking, String dgIn, String hsCode, String dutiDI, String truckerName, String pkgsType,
			int noOfPkgs, double weight, double volume, String accNo, String payMode, String cargoDesc,
			String truckerCd, String UserID, String crgType, String shpStrRefNo, String shpradd, String adminFeeInd,
			String reasonForWaive) throws BusinessException {
		String strInsert = new String();
		String strMark = new String();
		String sqlTrans = "";
		String strInsertTrans = new String();
		String strMarkTrans = new String();
		String strEsnDetailsTrans = new String();
		String transNumShpStr = "";
		String ShpStrNo = "";
		int transNumShpInt = 0;
		if (custCd.equals("JP"))
			custCd = getDeclarant(varno);
	

		SqlRowSet rs1 = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: insertSSDetailsForDPE  DAO  Start varno " + CommonUtility.deNull(varno) + " custCd" + CommonUtility.deNull(custCd) + " truckerIcNo"
					+ CommonUtility.deNull(truckerIcNo) + " truckerCNo" + CommonUtility.deNull(truckerCNo) + " marking" + CommonUtility.deNull(marking)
					+ " dgIn" + CommonUtility.deNull(dgIn) + " hsCode" + CommonUtility.deNull(hsCode) + " dutiDI" + CommonUtility.deNull(dutiDI)
					+ " truckerName" + CommonUtility.deNull(truckerName) + "pkgsType" + CommonUtility.deNull(pkgsType) + " noOfPkgs"
					+ CommonUtility.deNull(String.valueOf(noOfPkgs)) + " weight" + CommonUtility.deNull(String.valueOf(weight)) + " volume"
					+ CommonUtility.deNull(String.valueOf(volume)) + " accNo" + CommonUtility.deNull(accNo) + " payMode" + CommonUtility.deNull(payMode)
					+ " cargoDesc" + CommonUtility.deNull(cargoDesc) + " truckerCd" + CommonUtility.deNull(truckerCd) + " UserID" + CommonUtility.deNull(UserID)
					+ " crgType" + CommonUtility.deNull(crgType) + " shpStrRefNo" + CommonUtility.deNull(shpStrRefNo) + " shpradd" + CommonUtility.deNull(shpradd)
					+ " adminFeeInd" + CommonUtility.deNull(adminFeeInd) + " reasonForWaive" + CommonUtility.deNull(reasonForWaive));

			ShpStrNo = getShpStrNoForDPE();
			// To avoid duplicate entries as done in ESNEJB Sripriya Bug Fix 16/4/2013

			int result = -2;
			int cnt = 0; // for limiting number of tries to insert bill
			while (result == -2 && cnt < MAX_TRY) {
				result = this.addEsn(ShpStrNo, varno, custCd, UserID);
				if (result == -2) {
					ShpStrNo = this.getShpStrNoForDPE();
					cnt++;
					continue;
				} else {
					break;
				}
			}

			StringBuffer sb = new StringBuffer();
			sb.append("INSERT ");
			sb.append("	INTO ");
			sb.append("	SS_DETAILS(ESN_ASN_NBR, ");
			sb.append("	SS_REF_NBR, ");
			sb.append("	NBR_PKGS, ");
			sb.append("	SS_WT, ");
			sb.append("	SS_VOL, ");
			sb.append("	CRG_DES, ");
			sb.append("	PKG_TYPE, ");
			sb.append("	SS_HS_CODE, ");
			sb.append("	CARGO_TYPE, ");
			sb.append("	SHIPPER_CD, ");
			sb.append("	SHIPPER_CR_NBR, ");
			sb.append("	SHIPPER_CONTACT, ");
			sb.append("	SHIPPER_ADDR, ");
			sb.append("	SHIPPER_NM, ");
			sb.append("	ACCT_NBR, ");
			sb.append("	SS_DG_IND, ");
			sb.append("	SS_DUTY_GOOD_IND, ");
			sb.append("	UA_NBR_PKGS, ");
			sb.append("	PAYMENT_MODE, ");
			sb.append("	LAST_MODIFY_USER_ID, ");
			sb.append("	LAST_MODIFY_DTTM, ");
			sb.append("	ADMIN_FEE_WAIVER_IND, ");
			sb.append("	ADMIN_FEE_WAIVER_REASON) ");
			sb.append("VALUES(:ShpStrNo, ");
			sb.append(":shpStrRefNo, ");
			sb.append(":noOfPkgs, ");
			sb.append(":weight, ");
			sb.append(":volume, ");
			sb.append(":cargoDesc, ");
			sb.append(":pkgsType, ");
			sb.append(":hsCode, ");
			sb.append(":crgType, ");
			sb.append(":custCd, ");
			sb.append(":truckerIcNo, ");
			sb.append(":truckerCNo, ");
			sb.append(":shpradd, ");
			sb.append(":truckerName, ");
			sb.append(":accNo, ");
			sb.append(":dgIn, ");
			sb.append(":dutiDI, ");
			sb.append("'0', ");
			sb.append(":payMode, ");
			sb.append(":UserID, ");
			sb.append("sysdate, ");
			sb.append(":adminFeeInd, ");
			sb.append(":reasonForWaive)");
			strInsert = sb.toString();
			strMark = "INSERT INTO ESN_MARKINGS(ESN_ASN_NBR,MARKINGS)VALUES(:ShpStrNo,:marking)";

			sqlTrans = "SELECT MAX(TRANS_NBR) FROM SS_Details_TRANS WHERE ESN_ASN_NBR = :ShpStrNo";

			if (logStatusGlobal.equalsIgnoreCase("Y") || logStatusGlobal == "Y") {
				paramMap.put("ShpStrNo", ShpStrNo);
				log.info(" insertSSDetailsForDPE  DAO  SQL " + sqlTrans + " paramMap: " + paramMap);
				
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sqlTrans, paramMap);

				while (rs1.next()) {
					transNumShpStr = rs1.getString(1);
				}

				if (transNumShpStr == null || transNumShpStr == "") {
					transNumShpInt = 0;
				} else {
					transNumShpInt = Integer.parseInt(transNumShpStr);
					transNumShpInt++;
				}
				StringBuffer sb5 = new StringBuffer();
				sb5.append("INSERT ");
				sb5.append("	INTO ");
				sb5.append("	SS_DETAILS_TRANS(ESN_ASN_NBR, ");
				sb5.append("	SS_REF_NBR, ");
				sb5.append("	TRANS_NBR, ");
				sb5.append("	NBR_PKGS, ");
				sb5.append("	SS_WT, ");
				sb5.append("	SS_VOL, ");
				sb5.append("	CRG_DES, ");
				sb5.append("	PKG_TYPE, ");
				sb5.append("	SS_HS_CODE, ");
				sb5.append("	CARGO_TYPE, ");
				sb5.append("	SHIPPER_CD, ");
				sb5.append("	SHIPPER_CR_NBR, ");
				sb5.append("	SHIPPER_CONTACT, ");
				sb5.append("	SHIPPER_ADDR, ");
				sb5.append("	SHIPPER_NM, ");
				sb5.append("	ACCT_NBR, ");
				sb5.append("	SS_DG_IND, ");
				sb5.append("	SS_DUTY_GOOD_IND, ");
				sb5.append("	UA_NBR_PKGS, ");
				sb5.append("	PAYMENT_MODE, ");
				sb5.append("	LAST_MODIFY_USER_ID, ");
				sb5.append("	LAST_MODIFY_DTTM) ");
				sb5.append("VALUES(:ShpStrNo, ");
				sb5.append(":shpStrRefNo, ");
				sb5.append(":transNumShpInt, ");
				sb5.append(":noOfPkgs, ");
				sb5.append(":weight, ");
				sb5.append(":volume, ");
				sb5.append(":cargoDesc, ");
				sb5.append(":pkgsType, ");
				sb5.append(":hsCode, ");
				sb5.append(":crgType, ");
				sb5.append(":custCd, ");
				sb5.append(":truckerIcNo, ");
				sb5.append(":truckerCNo, ");
				sb5.append(":shpradd, ");
				sb5.append(":truckerName, ");
				sb5.append(":accNo, ");
				sb5.append(":dgIn, ");
				sb5.append(":dutiDI, ");
				sb5.append("'0', ");
				sb5.append(":payMode, ");
				sb5.append(":UserID, ");
				sb5.append("sysdate)");

				strInsertTrans = sb5.toString();

				StringBuffer sb1 = new StringBuffer();
				sb1.append("INSERT ");
				sb1.append("	INTO ");
				sb1.append("	ESN_Trans(ESN_ASN_NBR, ");
				sb1.append("	TRANS_NBR, ");
				sb1.append("	DECLARANT_CR_NO, ");
				sb1.append("	TRANS_TYPE, ");
				sb1.append("	OUT_VOY_VAR_NBR, ");
				sb1.append("	ESN_CREATE_CD, ");
				sb1.append("	LAST_MODIFY_USER_ID, ");
				sb1.append("	LAST_MODIFY_DTTM) ");
				sb1.append("VALUES(:ShpStrNo, ");
				sb1.append(":transNumShpInt, ");
				sb1.append("'O', ");
				sb1.append("'S', ");
				sb1.append(":varno, ");
				sb1.append(":custCd, ");
				sb1.append(":UserID, ");
				sb1.append("sysdate)");

				strEsnDetailsTrans = sb1.toString();

				strMarkTrans = "INSERT INTO ESN_MARKINGS_Trans(ESN_ASN_NBR,TRANS_NBR,MARKINGS)VALUES(:ShpStrNo,:transNumShpInt,:marking)";

				paramMap.put("ShpStrNo", ShpStrNo);
				paramMap.put("shpStrRefNo", GbmsCommonUtility.addApostr(shpStrRefNo));
				paramMap.put("transNumShpInt", transNumShpInt);
				paramMap.put("noOfPkgs", noOfPkgs);
				paramMap.put("weight", weight);
				paramMap.put("volume", volume);
				paramMap.put("cargoDesc", GbmsCommonUtility.addApostr(cargoDesc));
				paramMap.put("pkgsType", pkgsType);
				paramMap.put("hsCode", hsCode);
				paramMap.put("crgType", crgType);
				paramMap.put("custCd", custCd);
				paramMap.put("truckerIcNo", GbmsCommonUtility.addApostr(truckerIcNo));
				paramMap.put("truckerCNo", truckerCNo);
				paramMap.put("shpradd", shpradd);
				paramMap.put("truckerName", GbmsCommonUtility.addApostr(truckerName));
				paramMap.put("accNo", GbmsCommonUtility.addApostr(accNo));
				paramMap.put("dgIn", dgIn);
				paramMap.put("dutiDI", dutiDI);
				paramMap.put("payMode", payMode);
				paramMap.put("UserID", UserID);

				log.info(" insertSSDetailsForDPE  DAO  SQL " + strInsertTrans + " paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strInsertTrans, paramMap);

				paramMap = new HashMap<String, Object>();
				paramMap.put("ShpStrNo", ShpStrNo);
				paramMap.put("varno", varno);
				paramMap.put("custCd", custCd);
				paramMap.put("UserID", UserID);
				paramMap.put("transNumShpInt", transNumShpInt);
				log.info(" insertSSDetailsForDPE  DAO  SQL " + strEsnDetailsTrans + " paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strEsnDetailsTrans, paramMap);

				paramMap = new HashMap<String, Object>();
				paramMap.put("ShpStrNo", ShpStrNo);
				paramMap.put("transNumShpInt", transNumShpInt);
				paramMap.put("marking", GbmsCommonUtility.addApostr(marking));
				log.info(" insertSSDetailsForDPE  DAO  SQL " + strMarkTrans + " paramMap: " + paramMap);
				namedParameterJdbcTemplate.update(strMarkTrans, paramMap);

			}
			// Commented as it is updated via addEsn
			// int cntEsnDetails = sqlstmt.executeUpdate(strEsnDetails);
			paramMap.put("ShpStrNo", ShpStrNo);
			paramMap.put("marking", GbmsCommonUtility.addApostr(marking));
			log.info(" insertSSDetailsForDPE  DAO  SQL " + strMark + " paramMap: " + paramMap);
			int cntmrk = namedParameterJdbcTemplate.update(strMark, paramMap);
			log.info("cntmrk: " + cntmrk);

			paramMap = new HashMap<String, Object>();
			paramMap.put("ShpStrNo", ShpStrNo);
			paramMap.put("shpStrRefNo", GbmsCommonUtility.addApostr(shpStrRefNo));
			paramMap.put("noOfPkgs", noOfPkgs);
			paramMap.put("weight", weight);
			paramMap.put("volume", volume);
			paramMap.put("cargoDesc", GbmsCommonUtility.addApostr(cargoDesc));
			paramMap.put("pkgsType", pkgsType);
			paramMap.put("hsCode", hsCode);
			paramMap.put("crgType", crgType);
			paramMap.put("custCd", custCd);
			paramMap.put("truckerIcNo", GbmsCommonUtility.addApostr(truckerIcNo));
			paramMap.put("truckerCNo", GbmsCommonUtility.addApostr(truckerCNo));
			paramMap.put("shpradd", GbmsCommonUtility.addApostr(shpradd));
			paramMap.put("truckerName", GbmsCommonUtility.addApostr(truckerName));
			paramMap.put("accNo", GbmsCommonUtility.addApostr(accNo));
			paramMap.put("dgIn", dgIn);
			paramMap.put("dutiDI", dutiDI);
			paramMap.put("payMode", payMode);
			paramMap.put("UserID", UserID);
			paramMap.put("adminFeeInd", adminFeeInd);
			paramMap.put("reasonForWaive", reasonForWaive);
			log.info(" insertSSDetailsForDPE  DAO  SQL " + strInsert  + " paramMap: " + paramMap);
			int count = namedParameterJdbcTemplate.update(strInsert, paramMap);
			log.info("count: " + count);
			
			if (count == 0 || cntmrk == 0) {
				log.info("Writing from ShipStore.insertEsnDetails");
				log.info("Record Cannot be added to Database");
				throw new BusinessException("M4201");
			}

		} catch (BusinessException e) {
			log.info("Exception insertSSDetailsForDPE : ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception insertSSDetailsForDPE : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception insertSSDetailsForDPE : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertSSDetailsForDPE  DAO  END   ShpStrNo: " + ShpStrNo);
		}
		return ShpStrNo;
	}

	public boolean chkAccNo(String accNo) throws BusinessException {
		String AccountNo = accNo.toUpperCase();
		String sql;
		boolean result = true;
		sql = "select ACCT_NBR from cust_acct where upper(ACCT_NBR) = :AccountNo and acct_status_cd='A' and business_type like '%G%' and trial_ind='N'";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: chkAccNo  DAO  Start accNo " + CommonUtility.deNull(accNo));
			paramMap.put("AccountNo", GbmsCommonUtility.addApostr(AccountNo));
			log.info(" chkAccNo  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next())
				result = true;
			else
				result = false;

			log.info(" chkAccNo  DAO  Result" + result);

		} catch (NullPointerException e) {
			log.info("Exception chkAccNo : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception chkAccNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkAccNo  DAO  END");
		}
		return result;
	}

	public List<ShipStoreValueObject> getUserAccNo(String custId, String accNbr) throws BusinessException {
		String custCd = custId;
		String accNo = accNbr;
		String sql = "";
		sql = "select ACCT_NBR from cust_acct where cust_cd = :custCd and acct_status_cd='A' and business_type like '%G%' and trial_ind='N' and ACCT_NBR is not null and ACCT_NBR != :accNo ";
		SqlRowSet rs = null;
		List<ShipStoreValueObject> UserAccNo = new ArrayList<ShipStoreValueObject>();
		ShipStoreValueObject shipStoreValueObject = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getUserAccNo  DAO  Start custId " + CommonUtility.deNull(custId) + "accNbr" + CommonUtility.deNull(accNbr));
			paramMap.put("custCd", custCd);
			paramMap.put("accNo", accNo);
			log.info(" getUserAccNo  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				shipStoreValueObject = new ShipStoreValueObject();
				shipStoreValueObject.setAccNo(CommonUtility.deNull(rs.getString("ACCT_NBR")));
				UserAccNo.add(shipStoreValueObject);

			}
			log.info(" getUserAccNo  DAO  Result" + UserAccNo.toString());
		} catch (NullPointerException e) {
			log.info("Exception getUserAccNo : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getUserAccNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getUserAccNo  DAO  END");
		}
		return UserAccNo;
	}

	public String getTruckerName(String custCode, String trcIcNo) throws BusinessException {
		String truckerIcNo = trcIcNo;
		String truckerName = "";
		String sql;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		sql = "select co_nm from company_code cocode,customer cust where cust.cust_cd= cocode.co_cd and (upper(tdb_cr_nbr)= :truckerIcNo OR upper(uen_nbr) =:truckerIcNo)";
		try {
			log.info("START: getTruckerName  DAO  Start custCode " + CommonUtility.deNull(custCode) + " trcIcNo" + CommonUtility.deNull(trcIcNo));
			paramMap.put("truckerIcNo", GbmsCommonUtility.addApostr(truckerIcNo));
			log.info(" getTruckerName  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				truckerName = rs.getString("co_nm");
			}
			log.info(" getTruckerName  DAO  Result" + truckerName.toString());
		} catch (NullPointerException e) {
			log.info("Exception getTruckerName : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTruckerName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTruckerName  DAO  END");
		}
		return truckerName;
	}

	private String getCustId(String tdbcrNo) throws BusinessException {
		String truckerCd = tdbcrNo;
		String custCd = "";
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		sql = "select cust_cd from customer where upper(tdb_cr_nbr) = :truckerCd OR upper(uen_nbr)= :truckerCd";
		try {
			log.info("START: getCustId  DAO  Start tdbcrNo " + CommonUtility.deNull(tdbcrNo));
			paramMap.put("truckerCd", GbmsCommonUtility.addApostr(truckerCd));
			log.info(" getCustId  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				custCd = CommonUtility.deNull(rs.getString("cust_cd"));
			}
			log.info(" getCustId  DAO  Result" + custCd.toString());
		} catch (NullPointerException e) {
			log.info("Exception getCustId : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCustId : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCustId  DAO  END");
		}
		return custCd;
	}

	public List<ShipStoreValueObject> getAccNo(String truckerIcNo) throws BusinessException {
		String truckerCd = "";
		String tdbcrNo = truckerIcNo;
		String sql = "";
		truckerCd = getCustId(tdbcrNo);
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		sql = "select ACCT_NBR from cust_acct where upper(cust_cd)= :truckerCd and acct_status_cd='A' and business_type like '%G%' and trial_ind='N' and ACCT_NBR is not null ORDER BY ACCT_NBR";
		List<ShipStoreValueObject> accNoList = new ArrayList<ShipStoreValueObject>();
		ShipStoreValueObject shipStoreValueObject = null;
		try {
			log.info("START: getAccNo  DAO  Start truckerIcNo " + CommonUtility.deNull(truckerIcNo));
			paramMap.put("truckerCd", truckerCd);
			log.info(" getAccNo  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				shipStoreValueObject = new ShipStoreValueObject();
				shipStoreValueObject.setAccNo(CommonUtility.deNull(rs.getString("ACCT_NBR")));
				accNoList.add(shipStoreValueObject);
			}
			log.info(" getAccNo  DAO  Result" + accNoList.toString());
		} catch (NullPointerException e) {
			log.info("Exception getAccNo : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getAccNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAccNo  DAO  END");
		}
		return accNoList;
	}

	public String getTruckerCd(String trcIcNo) throws BusinessException {
		String truckerIcNo = trcIcNo;
		String sql;
		String truckerCd = "";
		SqlRowSet rs = null;
		sql = "select cust_cd from customer where upper(tdb_cr_nbr) = :truckerIcNo OR upper(uen_nbr) = :truckerIcNo ";
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getTruckerCd  DAO  Start Obj " + CommonUtility.deNull(trcIcNo));
			paramMap.put("truckerIcNo", GbmsCommonUtility.addApostr(truckerIcNo));
			log.info(" getTruckerCd  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				truckerCd = rs.getString("cust_cd");
			} else {
				truckerCd = "";
			}
			log.info(" getTruckerCd  DAO  Result" + truckerCd.toString());

		} catch (NullPointerException e) {
			log.info("Exception getTruckerCd : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTruckerCd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTruckerCd  DAO  END");
		}
		return truckerCd;
	}

	@Override
	public List<VesselVoyValueObject> getVesselList(String custId) throws BusinessException {
		String custCd = custId;
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		if (custCd.equals("JP")) {
			sql = "SELECT distinct VV_CD,VSL_NM,OUT_VOY_NBR,TERMINAL FROM VESSEL_CALL WHERE VV_STATUS_IND IN ('PR','AP','AL','BR','UB') AND (TERMINAL = 'CT' OR TERMINAL = 'GB') AND GB_CLOSE_SHP_IND <> 'Y' ORDER BY TERMINAL DESC,VSL_NM,OUT_VOY_NBR";
		} else {

			sql = "SELECT distinct VV_CD,VSL_NM,OUT_VOY_NBR,TERMINAL FROM VESSEL_CALL WHERE VV_STATUS_IND IN ('PR','AP','AL','BR') AND (TERMINAL = 'CT' OR TERMINAL = 'GB') AND GB_CLOSE_SHP_IND <> 'Y' ORDER BY TERMINAL DESC,VSL_NM,OUT_VOY_NBR";
		}
		List<VesselVoyValueObject> vesselList = new ArrayList<VesselVoyValueObject>();
		VesselVoyValueObject vesselVoyValueObject = null;
		try {
			log.info("START: getVesselList  DAO  Start custId " + CommonUtility.deNull(custId));
			log.info(" getUserAccNo  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				vesselVoyValueObject = new VesselVoyValueObject();
				vesselVoyValueObject.setVoyNo(CommonUtility.deNull(rs.getString("OUT_VOY_NBR")));
				vesselVoyValueObject.setVslName(CommonUtility.deNull(rs.getString("VSL_NM")));
				vesselVoyValueObject.setVarNbr(CommonUtility.deNull(rs.getString("VV_CD")));
				vesselVoyValueObject.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				vesselList.add(vesselVoyValueObject);
			}
		} catch (NullPointerException e) {
			log.info("Exception getVesselList : ", e);
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
	public List<ShipStoreValueObject> getshpStrList(String selectVoyNo, String custId, Criteria criteria)
			throws BusinessException {
		String selVoyNo = selectVoyNo;
		String custCd = custId;
		String sql = "";
		StringBuffer sb = new StringBuffer();
		if (custCd.equals("JP")) {
			sb = new StringBuffer();
			sb.append("SELECT ");
			sb.append("	ss.esn_asn_nbr, ");
			sb.append("	CRG_TYPE_NM, ");
			sb.append("	ss.NBR_PKGS, ");
			sb.append("	ss.SS_WT, ");
			sb.append("	ss.SS_VOL, ");
			sb.append("	ss.CRG_DES, ");
			sb.append("	ss.SHIPPER_NM, ");
			sb.append("	vc.terminal, ");
			sb.append("	vc.scheme, ");
			sb.append("	vc.COMBI_GC_SCHEME, ");
			sb.append("	vc.COMBI_GC_OPS_IND ");
			sb.append("FROM ");
			sb.append("	SS_DETAILS ss, ");
			sb.append("	esn e, ");
			sb.append("	vessel_call vc, ");
			sb.append("	CRG_TYPE ");
			sb.append("WHERE ");
			sb.append("	e.esn_asn_nbr = ss.esn_asn_nbr ");
			sb.append("	AND ss.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD ");
			sb.append("	AND esn_Status = 'A' ");
			sb.append("	AND e.OUT_VOY_VAR_NBR = :selVoyNo ");
			sb.append("	AND vc.VV_CD = :selVoyNo ");
			sb.append("ORDER BY ");
			sb.append("	ss.esn_asn_nbr");

			sql = sb.toString();

		} else {
			sb = new StringBuffer();
			sb.append("SELECT ");
			sb.append("	ss.esn_asn_nbr, ");
			sb.append("	CRG_TYPE_NM, ");
			sb.append("	ss.NBR_PKGS, ");
			sb.append("	ss.SS_WT, ");
			sb.append("	ss.SS_VOL, ");
			sb.append("	ss.CRG_DES, ");
			sb.append("	ss.SHIPPER_NM, ");
			sb.append("	vc.terminal, ");
			sb.append("	vc.scheme, ");
			sb.append("	vc.COMBI_GC_SCHEME, ");
			sb.append("	vc.COMBI_GC_OPS_IND ");
			sb.append("FROM ");
			sb.append("	SS_DETAILS ss, ");
			sb.append("	esn e, ");
			sb.append("	vessel_call vc, ");
			sb.append("	CRG_TYPE ");
			sb.append("WHERE ");
			sb.append("	e.esn_asn_nbr = ss.esn_asn_nbr ");
			sb.append("	AND ss.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD ");
			sb.append("	AND esn_Status = 'A' ");
			sb.append("	AND e.OUT_VOY_VAR_NBR =:selVoyNo ");
			sb.append("	AND vc.VV_CD =:selVoyNo ");
			sb.append("	AND e.ESN_CREATE_CD =:custCd ");
			sb.append("ORDER BY ");
			sb.append("	ss.esn_asn_nbr");
			sql = sb.toString();
		}
		if (criteria.isPaginated()) {
			sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());
		}
		SqlRowSet rs = null;
		List<ShipStoreValueObject> shpStrList = new ArrayList<ShipStoreValueObject>();
		ShipStoreValueObject shipStoreValueObject = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getshpStrList  DAO  Start selectVoyNo " + CommonUtility.deNull(selectVoyNo) + " custId" + CommonUtility.deNull(custId));

			if (custCd.equals("JP")) {
				paramMap.put("selVoyNo", selVoyNo);
			} else {
				paramMap.put("selVoyNo", selVoyNo);
				paramMap.put("custCd", custCd);

			}

			log.info(" getshpStrList  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				shipStoreValueObject = new ShipStoreValueObject();
				shipStoreValueObject.setShpStrNbr(rs.getLong("ESN_ASN_NBR"));
				// shipStoreValueObject.setCrgDesc(CommonUtility.deNull(rs.getString("CRG_TYPE_NM")));//Amended
				// by VietNguyen 20/03/2014
				shipStoreValueObject.setCrgDesc(CommonUtility.deNull(rs.getString("CRG_DES")));// Added by VietNguyen
																								// 20/03/2014
				shipStoreValueObject.setBNoofPkgs(rs.getInt("NBR_PKGS"));
				// shipStoreValueObject.setBNoofPkgs(rs.getInt(3));
				shipStoreValueObject.setTruckerName(CommonUtility.deNull(rs.getString("SHIPPER_NM")));
				shipStoreValueObject.setGrWt(rs.getDouble("SS_WT"));
				shipStoreValueObject.setGrVolume(rs.getDouble("SS_VOL"));
				shipStoreValueObject.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));
				shipStoreValueObject.setScheme(CommonUtility.deNull(rs.getString("SCHEME")));
				shipStoreValueObject.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				shipStoreValueObject.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				shpStrList.add(shipStoreValueObject);
			}

		} catch (NullPointerException e) {
			log.info("Exception getshpStrList : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getshpStrList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getshpStrList  DAO  END  shpStrList: " + shpStrList.size());
		}

		return shpStrList;

	}

	@Override
	public TruckerValueObject getTruckerDetails(String truckerIc) throws BusinessException {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();

		sb.append("SELECT ");
		sb.append("	CO_NM, ");
		sb.append("	cust.cust_cd AS CO_CD, ");
		sb.append("	NVL(PHONE1_NBR, PHONE2_NBR) PHONE1_NBR, ");
		sb.append("	ADD_L1 ");
		sb.append("FROM ");
		sb.append("	customer cust ");
		sb.append("LEFT JOIN company_code cc ON ");
		sb.append("	cust.cust_cd = cc.co_cd ");
		sb.append("LEFT JOIN cust_contact ct ON ");
		sb.append("	cust.cust_cd = ct.CUST_CD ");
		sb.append("LEFT JOIN cust_address ca ON ");
		sb.append("	cust.cust_cd = ca.cust_cd ");
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
		sb1.append("	OR (JC.NRIC_NO) = :truckerIc ");
		sb1.append("	OR (JC.FIN_NO) = :truckerIc");

		String sql2 = sb1.toString();
		SqlRowSet rs = null;
		TruckerValueObject truckerValueObject = null;
		try {
			log.info("START: getTruckerDetails  DAO  Start truckerIc " + CommonUtility.deNull(truckerIc));
			paramMap.put("truckerIc", truckerIc);

			log.info(" getTruckerDetails  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			truckerValueObject = new TruckerValueObject();
			truckerValueObject.setTruckerIc(truckerIc);
			if (rs.next()) {
				truckerValueObject.setTruckerNm(CommonUtility.deNull(rs.getString("CO_NM")));
				truckerValueObject.setTruckerContact(CommonUtility.deNull(rs.getString("PHONE1_NBR")));
				truckerValueObject.setTruckerAdd(CommonUtility.deNull(rs.getString("ADD_L1")));
				truckerValueObject.setTruckerCd(CommonUtility.deNull(rs.getString("CO_CD")));
			} else {
				paramMap.put("truckerIc", truckerIc);

				log.info(" getTruckerDetails  DAO  SQL " + sql2 + " paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql2, paramMap);

				if (rs.next()) {
					truckerValueObject.setTruckerNm(CommonUtility.deNull(rs.getString("CO_NM")));
					truckerValueObject.setTruckerContact(CommonUtility.deNull(rs.getString("PHONE1_NBR")));
					truckerValueObject.setTruckerAdd(CommonUtility.deNull(rs.getString("ADD_L1")));
					truckerValueObject.setTruckerCd(CommonUtility.deNull(rs.getString("CO_CD")));
				}
			}
			log.info(" getTruckerDetails  DAO  Result" + truckerValueObject.toString());
		} catch (NullPointerException e) {
			log.info("Exception getTruckerDetails : ", e);
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
	public boolean chkPkgsType(String pkgs_Type) throws BusinessException {
		String pkgsType = pkgs_Type;
		String sql;
		boolean result = true;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		sql = "SELECT PKG_TYPE_CD FROM PKG_TYPES WHERE PKG_TYPE_CD = :pkgsType and rec_status='A'";

		try {
			log.info("START: chkPkgsType  DAO  Start pkgs_Type " + CommonUtility.deNull(pkgs_Type));
			paramMap.put("pkgsType", pkgsType);
			log.info(" chkPkgsType  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next())
				result = true;
			else
				result = false;

		} catch (NullPointerException e) {
			log.info("Exception getTruckerDetails : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTruckerDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: chkPkgsType  DAO  END");
		}
		return result;
	}

	@Override
	public int getshpStrListCount(String selectVoyNo, String custId) throws BusinessException {
		String selVoyNo = selectVoyNo;
		String custCd = custId;
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		int count = 0;

		if (custCd.equals("JP")) {
			sb = new StringBuffer();
			sb.append("SELECT ");
			sb.append("	COUNT(*) ");
			sb.append("FROM ");
			sb.append("	SS_DETAILS ss, ");
			sb.append("	esn e, ");
			sb.append("	vessel_call vc, ");
			sb.append("	CRG_TYPE ");
			sb.append("WHERE ");
			sb.append("	e.esn_asn_nbr = ss.esn_asn_nbr ");
			sb.append("	AND ss.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD ");
			sb.append("	AND esn_Status = 'A' ");
			sb.append("	AND e.OUT_VOY_VAR_NBR = :selVoyNo ");
			sb.append("	AND vc.VV_CD = :selVoyNo ");
			sb.append("ORDER BY ");
			sb.append("	ss.esn_asn_nbr");

			sql = sb.toString();

		} else {
			sb = new StringBuffer();
			sb.append("SELECT ");
			sb.append("	COUNT(*) ");
			sb.append("FROM ");
			sb.append("	SS_DETAILS ss, ");
			sb.append("	esn e, ");
			sb.append("	vessel_call vc, ");
			sb.append("	CRG_TYPE ");
			sb.append("WHERE ");
			sb.append("	e.esn_asn_nbr = ss.esn_asn_nbr ");
			sb.append("	AND ss.CARGO_TYPE = CRG_TYPE.CRG_TYPE_CD ");
			sb.append("	AND esn_Status = 'A' ");
			sb.append("	AND e.OUT_VOY_VAR_NBR =:selVoyNo ");
			sb.append("	AND vc.VV_CD =:selVoyNo ");
			sb.append("	AND e.ESN_CREATE_CD =:custCd ");
			sb.append("ORDER BY ");
			sb.append("	ss.esn_asn_nbr");
			sql = sb.toString();
		}

		SqlRowSet rs = null;
		try {
			log.info("START: getshpStrList  DAO  Start selectVoyNo " + CommonUtility.deNull(selectVoyNo) + " custId" + CommonUtility.deNull(custId));

			if (custCd.equals("JP")) {
				paramMap.put("selVoyNo", selVoyNo);
			} else {
				paramMap.put("selVoyNo", selVoyNo);
				paramMap.put("custCd", custCd);

			}

			log.info(" getshpStrList  DAO  SQL " + sql + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (NullPointerException e) {
			log.info("Exception getshpStrList : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getshpStrList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getshpStrList  DAO  END  count: " + count);
		}
		return count;

	}

	public String getEsnDeclarantCd(String coCd, String varno) throws BusinessException {
		String custCd = coCd;
		String sql = "select (SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) FROM CUSTOMER WHERE CUST_CD=COCODE.CO_CD) TDB_CR_NBR,co_cd from company_code cocode,customer cust where cust.cust_cd= cocode.co_cd and co_cd =:co_cd";
		String esnDeclarant = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getEsnDeclarantCd DAO coCd:" + CommonUtility.deNull(coCd) + "varno:" + CommonUtility.deNull(varno));
			if (custCd.equals("JP"))
				custCd = getDeclarant(varno);
//			else
//				custCd = custCd;

			paramMap.put("co_cd", custCd);
			log.info("sql:" + sql + "paramMap:" + paramMap.toString());
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				esnDeclarant = rs.getString("tdb_cr_nbr");
			} else {
				esnDeclarant = "";
			}
		} catch (BusinessException e) {
			log.info("Exception getEsnDeclarantCd : ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("Exception getEsnDeclarantCd : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEsnDeclarantCd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:DAO getEsnDeclarantCd esnDeclarant:" + CommonUtility.deNull(esnDeclarant));
		}
		return esnDeclarant;

	}

	public String getEsnDeclarantName(String custCode, String trcIcNo) throws BusinessException {
		String truckerIcNo = trcIcNo;
		String truckerName = "";
		String sql = "";
		Map<String, String> paramMap = new HashMap<String, String>();
		sql = "select co_nm from company_code cocode,customer cust where cust.cust_cd= cocode.co_cd and (upper(tdb_cr_nbr)=:truckerIcNo OR upper(uen_nbr) =:truckerIcNo)";
		try {
			log.info("START: getEsnDeclarantName DAO custCode:" + CommonUtility.deNull(custCode) + "trcIcNo:" + CommonUtility.deNull(trcIcNo));
			paramMap.put("truckerIcNo", truckerIcNo);
			log.info("sql:" + sql + "paramMap:" + paramMap.toString());
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				truckerName = rs.getString("co_nm");
			}
		} catch (NullPointerException e) {
			log.info("Exception getEsnDeclarantName : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEsnDeclarantName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEsnDeclarantName DAO truckerName:" + CommonUtility.deNull(truckerName));
		}

		return truckerName;

	}

	// inter terminal store rent get data for report
	@Override
	public List<StoreRentCrReport> getStoreRentReports(String billmonth, String tsdirection) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		List<StoreRentCrReport> listSRDetails = new ArrayList<StoreRentCrReport>();
		try {
			log.info("getStoreRentReports DAO START billmonth : " + CommonUtility.deNull(billmonth) +" tsdirection : "+CommonUtility.deNull(tsdirection) );
			
			if (billmonth != null && billmonth.trim().length() > 0) {
				if (tsdirection.equals("jptopsa") || tsdirection.equals("both")) {
				sb.setLength(0);
				sb.append(" select sno,discvvcd,discvoy,loadvvcd,loadvoy,pkgs,billton,acct_nbr,storerentdays, CASE WHEN totalamount = 0 THEN '' ELSE totalamount||chr(0) END totalamount, ");
				sb.append(" CASE WHEN length(rownum_) = 1 THEN a.sno||'00'||rownum_");
				sb.append(" when length(rownum_) = 2  THEN a.sno||'0'||rownum_ ");
				sb.append(" ELSE a.sno||rownum_ ");
				sb.append(" END AS sno1           ");
				sb.append(" from ( ");
				sb.append(" select b.*, rownum rownum_ from ( ");
				sb.append(" select ");
				sb.append(" jp.sno, jp.discVVcd, jp.discVoy, jp.loadVvCd, jp.loadVoy, jp.pkgs, round(jp.billTon,2) BillTon, ");
				sb.append(" jp.acct_nbr, ");
				sb.append(" (CASE WHEN jp.SRDays > 0 THEN jp.SRDays||' Days'    ");
				sb.append(" ELSE '' END) StoreRentDays,  ");
				sb.append(" round( ");
				sb.append(" (CASE ");
			    sb.append(" WHEN jp.SRDays <=0 THEN 0 ");
			    sb.append(" WHEN jp.SRDays <=14 THEN  ");
			    sb.append(" jp.SRDays * jp.billTon * (select amt_charge from tariff_tier where tariff_cd='GSRGL0TSTN0633' and version_nbr=(select max(version_nbr) from tariff_version where atb_dttm >= eff_start_dttm )  and tier_seq_nbr=1 AND cust_cd IS NULL)  ");
			    sb.append(" WHEN jp.SRDays <=42  THEN ");
			    sb.append(" 14 * jp.billTon * (select amt_charge from tariff_tier where tariff_cd='GSRGL0TSTN0633' and version_nbr=(select max(version_nbr) from tariff_version where atb_dttm >= eff_start_dttm )  and tier_seq_nbr=1 AND cust_cd IS NULL) + ");
			    sb.append(" (jp.SRDays - 14) * jp.billTon * (select amt_charge from tariff_tier where tariff_cd='GSRGL0TSTN0633' and version_nbr=(select max(version_nbr) from tariff_version where atb_dttm >= eff_start_dttm )   and tier_seq_nbr=2 AND cust_cd IS NULL) ");
			    sb.append(" ELSE  ");
			    sb.append(" 14 * jp.billTon * (select amt_charge from tariff_tier where tariff_cd='GSRGL0TSTN0633' and version_nbr=(select max(version_nbr) from tariff_version where atb_dttm >= eff_start_dttm )   and tier_seq_nbr=1 AND cust_cd IS NULL) + ");
			    sb.append(" 28 * jp.billTon * (select amt_charge from tariff_tier where tariff_cd='GSRGL0TSTN0633' and version_nbr=(select max(version_nbr) from tariff_version where atb_dttm >= eff_start_dttm )   and tier_seq_nbr=2 AND cust_cd IS NULL) + ");
			    sb.append(" (jp.SRDays - 42) * jp.billTon * (select amt_charge from tariff_tier where tariff_cd='GSRGL0TSTN0633' and version_nbr=(select max(version_nbr) from tariff_version where atb_dttm >= eff_start_dttm )   and tier_seq_nbr=3 AND cust_cd IS NULL) ");
				sb.append(" END), ");
				sb.append(" 2) TotalAmount ");
				sb.append(" from ");
				sb.append(" (select 'JP' sno, ");
				sb.append(" in_vsl.vsl_nm  || chr(10) || es.edo_asn_nbr || chr(10) || to_char((select distinct gb_cod_dttm from berthing where vv_cd=e.in_voy_var_nbr  and shift_ind = 1), 'DD-MON-YY HH24:MI')  discVvCd,  ");
				sb.append(" in_vsl.in_voy_nbr discVoy, ");
				sb.append(" out_vsl.vsl_nm || chr(10) || es.SEC_BK_REF_NBR || chr(10) || to_char(out_vsl.atb_dttm, 'DD-MON-YY HH24:MI') loadVvCd, ");
				sb.append(" out_vsl.out_voy_nbr loadVoy, ");
				sb.append(" es.nbr_pkgs pkgs, ");
				sb.append(" greatest (es.nom_wt/1000, es.nom_vol) billTon,  ");
				sb.append(" out_vsl.atb_dttm, ");
				sb.append(" (select co_nm from company_code where co_cd = (select cust_cd from cust_acct where acct_nbr=ed.acct_nbr )) || chr(10) || ed.acct_nbr acct_nbr,  ");
				sb.append(" ceil(out_vsl.atb_dttm - (select distinct gb_cod_dttm from berthing where vv_cd=e.in_voy_var_nbr and shift_ind = 1))-14 SRDays  ");
				sb.append(" from esn e, tesn_jp_psa es left join nominated_vsl out_vsl on (upper(out_vsl.vsl_nm)=upper(es.second_car_ves_nm) and upper(out_vsl.out_voy_nbr)= upper(es.second_car_voy_nbr) AND REC_STATUS='A'), ");
				sb.append(" vessel_call in_vsl, gb_edo ed ");
				sb.append(" where e.esn_asn_nbr = es.esn_asn_nbr and es.edo_asn_nbr=ed.edo_asn_nbr ");
				sb.append(" and in_vsl.vv_cd = e.in_voy_var_nbr ");
				sb.append(" and in_vsl.vv_status_ind <> 'CX'  ");
				sb.append(" and e.esn_status = 'A'  ");
				sb.append(" AND Abs(out_vsl.atb_dttm - e.last_modify_dttm) <= 365  ");
				sb.append(" and to_char(out_vsl.atb_dttm, 'MM/YYYY')= :billmonth  ");
				sb.append(" )jp ");
				sb.append(" )b ");
				sb.append(" )a order by sno1 asc ");
				
				paramMap.put("billmonth", billmonth);
				log.info("SQL : " + sb.toString() + " paramMap : " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				while (rs.next()) {
					StoreRentCrReport SRVo = new StoreRentCrReport();
					SRVo.setAcct_nbr(rs.getString("acct_nbr"));
					SRVo.setBillTon(new BigDecimal(rs.getString("BillTon")));
					SRVo.setDiscVoy(rs.getString("discVoy"));
					SRVo.setDiscVvCd(rs.getString("discVVcd"));
					SRVo.setLoadVoy(rs.getString("loadVoy"));
					SRVo.setLoadVvCd(rs.getString("loadVvCd"));
					SRVo.setPkgs(Integer.parseInt(rs.getString("pkgs")));
					SRVo.setSno1(rs.getString("sno1"));
					SRVo.setStoreRentDays(rs.getString("StoreRentDays"));
					SRVo.setTotalAmount(rs.getString("TotalAmount"));
					listSRDetails.add(SRVo);
					}
				}
					
				if (tsdirection.equals("psatojp") || tsdirection.equals("both")) {
					sb.setLength(0);
					sb.append(" select sno,discvvcd,discvoy,loadvvcd,loadvoy,pkgs,billton,acct_nbr,storerentdays, CASE WHEN totalamount = 0 THEN '' ELSE totalamount||chr(0) END totalamount, ");
					sb.append(" CASE WHEN length(rownum_) = 1 THEN a.sno||'00'||rownum_ ");
					sb.append(" when length(rownum_) = 2  THEN a.sno||'0'||rownum_ ");
					sb.append(" ELSE a.sno||rownum_ ");
					sb.append(" END AS sno1           ");
					sb.append(" from ( ");
					sb.append(" select c.*, rownum rownum_ from ( ");
					sb.append(" select 'PJ' sno, (in_vsl.vsl_nm || chr(10) || to_char(in_vsl.cod_dttm, 'DD-MON-YYYY HH24:MI')) discVvCd, ");
					sb.append(" in_vsl.in_voy_nbr discVoy, ");
					sb.append(" out_vsl.vsl_nm || chr(10) || es.esn_asn_nbr || chr(10) || to_char(b.atb_dttm, 'DD-MON-YYYY HH24:MI') loadVvCd, ");
					sb.append(" out_vsl.out_voy_nbr loadVoy, ");
					sb.append(" es.nbr_pkgs pkgs, ");
					sb.append(" bi.nbr_other_unit BillTon, ");
					sb.append(" (select co_nm from company_code where co_cd = (select cust_cd from cust_acct where acct_nbr=bi.acct_nbr )) || chr(10) || bi.acct_nbr acct_nbr, ");
					sb.append(" sum(bi.nbr_time_unit)||' Days' || chr(10) || bi.bill_nbr StoreRentDays, ");
					sb.append(" sum(bi.total_item_amt) TotalAmount ");
					sb.append(" from bill_item bi left join bill_charge bc on bi.tariff_type = bc.tariff_type and bi.bill_nbr = bc.bill_nbr and bi.item_nbr = bc.item_nbr,   ");
					sb.append(" esn e, tesn_psa_jp es  left join nominated_vsl in_vsl on (upper(in_vsl.vsl_nm)=upper(es.first_car_ves_nm) and  upper(in_vsl.in_voy_nbr)= upper(es.first_car_voy_nbr) AND REC_STATUS='A'), ");
					sb.append(" vessel_call out_vsl, berthing b ");
					sb.append(" where e.esn_asn_nbr = es.esn_asn_nbr and bi.vv_cd=e.out_voy_var_nbr and b.vv_cd=e.out_voy_var_nbr and b.shift_ind=1 ");
					sb.append(" and out_vsl.vv_cd = e.out_voy_var_nbr and out_vsl.vv_cd = bi.vv_cd ");
					sb.append(" and bi.tariff_type='C' and bi.tariff_main_cat_cd='SR' ");
					sb.append(" and bi.tariff_sub_cat_cd='GL' and bi.total_item_amt>0 ");
					sb.append(" and out_vsl.vv_status_ind <> 'CX' ");
					sb.append(" and bc.ref_ind = 'ES' and bc.ref_nbr = e.esn_asn_nbr||''   ");
					sb.append(" and e.esn_status = 'A' ");
					sb.append(" and to_char(b.atb_dttm, 'MM/YYYY')= :billmonth ");
					sb.append(" group by e.in_voy_var_nbr, in_vsl.vsl_nm, in_vsl.cod_dttm, in_vsl.in_voy_nbr, e.out_voy_var_nbr, out_vsl.vsl_nm, out_vsl.out_voy_nbr, es.esn_asn_nbr, bi.bill_nbr, bi.bill_dttm, bi.acct_nbr, es.nbr_pkgs , bi.nbr_other_unit, b.atb_dttm ");
					sb.append(" )c ");
					sb.append(" )a order by sno1 asc ");
					
					paramMap.put("billmonth", billmonth);
					log.info("SQL : " + sb.toString() + " paramMap : " + paramMap);
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
					while (rs1.next()) {
						StoreRentCrReport SRVo2 = new StoreRentCrReport();
						SRVo2.setAcct_nbr(rs1.getString("acct_nbr"));
						SRVo2.setBillTon(new BigDecimal(rs1.getString("BillTon")));
						SRVo2.setDiscVoy(rs1.getString("discVoy"));
						SRVo2.setDiscVvCd(rs1.getString("discVVcd"));
						SRVo2.setLoadVoy(rs1.getString("loadVoy"));
						SRVo2.setLoadVvCd(rs1.getString("loadVvCd"));
						SRVo2.setPkgs(Integer.parseInt(rs1.getString("pkgs")));
						SRVo2.setSno1(rs1.getString("sno1"));
						SRVo2.setStoreRentDays(rs1.getString("StoreRentDays"));
						SRVo2.setTotalAmount(rs1.getString("TotalAmount"));
						listSRDetails.add(SRVo2);
					}
				}
			}
		return listSRDetails;
		} catch (NullPointerException e) {
			log.info("Exception getStoreRentReports : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getStoreRentReports : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("getStoreRentReports DAO end listSRDetails : " + listSRDetails.size());
		}
	}
}
