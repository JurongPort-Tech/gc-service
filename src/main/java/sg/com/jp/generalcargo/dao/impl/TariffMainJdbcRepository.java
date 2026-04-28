package sg.com.jp.generalcargo.dao.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javassist.tools.rmi.RemoteException;
import sg.com.jp.generalcargo.dao.TariffMainRepository;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.TariffCustomisedGBFspValueObject;
import sg.com.jp.generalcargo.domain.TariffGenCharVO;
import sg.com.jp.generalcargo.domain.TariffMainVO;
import sg.com.jp.generalcargo.domain.TariffTierVO;
import sg.com.jp.generalcargo.domain.UserTimestampVO;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("TariffMainJdbcRepository")
public class TariffMainJdbcRepository implements TariffMainRepository {

	private static final Log log = LogFactory.getLog(TariffMainJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	// StartRegion TariffMainJdbcRepository

	// jp.src.ejb.sessionBeans.cab.tariff--->TariffMainEJB--->retrieveTariffTier()
	public TariffMainVO retrieveTariffByCdTierSeqNbr(int versionNbr, String custCd, String acctNbr, String contractNbr,
			int contractualYr, String tariffMainCat, String tariffSubCat, String tariffCd, int tierSeqNbr,
			Timestamp varDate) throws BusinessException {
				
		SqlRowSet rs = null;
		StringBuffer sqlQuery = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();

		TariffMainVO tariffMainValueObject = null;
		TariffTierVO tariffTierValueObject = null;
		

		try {
			log.info("START: retrieveTariffByCdTierSeqNbr DAO :: versionNbr: " + CommonUtility.deNull(String.valueOf(versionNbr))
					+ " custCd: " + CommonUtility.deNull(custCd) + " acctNbr: " + CommonUtility.deNull(acctNbr) + " contractNbr: "
					+ CommonUtility.deNull(contractNbr) + " contractualYr: " + CommonUtility.deNull(String.valueOf(contractualYr))
					+ " tariffMainCat: " + CommonUtility.deNull(tariffMainCat) + " tariffSubCat: " + CommonUtility.deNull(tariffSubCat) 
					+ " tariffCd: " + CommonUtility.deNull(tariffCd) + " tierSeqNbr: " + CommonUtility.deNull(String.valueOf(tierSeqNbr))
					+ " varDate: " + CommonUtility.deNull(varDate.toString()));

			sqlQuery = new StringBuffer();
			// formulate the sql query and execute the query

			sqlQuery.append(
					" SELECT m.tariff_cd, m.tariff_desc, m.bill_party, m.coa_acct_nbr,m.gst_charge, m.gst_cd, m.create_dttm, m.waive_ind, ");
			sqlQuery.append(" m.commence_dttm, m.expiry_dttm, m.waive_reason, m.scheme_cd, m.business_type, ");
			sqlQuery.append(" m.bill_acct_nbr, m.bill_contract_nbr, m.min_ton ");
			sqlQuery.append(", ");
			sqlQuery.append(
					" t.fr_range, t.to_range, t.hour_rate, t.other_rate, t.amt_charge, t.percent_charge, t.adj_type, t.adj_amt, ");
			sqlQuery.append("  t.hour_rate_type, t.other_rate_type, t.tier_seq_nbr");
			sqlQuery.append("FROM tariff_main m, tariff_tier t ");
			sqlQuery.append("WHERE m.version_nbr=t.version_nbr ");
			sqlQuery.append("AND m.tariff_cd=t.tariff_cd ");
			sqlQuery.append("AND m.version_nbr=:versionNbr ");
			sqlQuery.append("AND m.tariff_main_cat_cd= :tariffMainCat ");
			sqlQuery.append("AND m.tariff_sub_cat_cd= :tariffSubCat ");
			sqlQuery.append("AND m.tariff_cd like :tariffCd ");

			if (Arrays.asList(ProcessChargeConst.PENJURU_MARINA_CRANE).contains(tariffMainCat)) {
			} else {
				sqlQuery.append("AND t.tier_seq_nbr= :tierSeqNbr ");
			}

			sqlQuery.append(constructNullQuery(custCd, acctNbr, contractNbr, contractualYr, true, false, false));
			if (varDate != null) {
				sqlQuery.append("AND m.EFFECT_DTTM<= :varDate ");
			}
			sqlQuery.append("ORDER BY t.tier_seq_nbr ");

			
			paramMap.put("versionNbr", versionNbr);
			paramMap.put("tariffMainCat", tariffMainCat);
			paramMap.put("tariffSubCat", tariffSubCat);
			paramMap.put("tariffCd", tariffCd.substring(0, 10) + "%");

			if (Arrays.asList(ProcessChargeConst.PENJURU_MARINA_CRANE).contains(tariffMainCat)) {
			} else {
				paramMap.put("tierSeqNbr", tierSeqNbr);
			}

			if (custCd != null)
				paramMap.put("custCd", custCd);
			if (acctNbr != null)
				paramMap.put("acctNbr", acctNbr);
			if (contractNbr != null)
				paramMap.put("contractNbr", contractNbr);
			if (contractualYr != 0)
				paramMap.put("contractualYr", contractualYr);

			if (varDate != null) {
				paramMap.put("varDate", varDate);
			}
			
			log.info("SQL: " + sqlQuery.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);

			tariffMainValueObject = new TariffMainVO();

			while (rs.next()) {
				if (!tariffMainValueObject.isModified(tariffMainValueObject)) {
					tariffMainValueObject.setCode(rs.getString("tariff_cd"));
					tariffMainValueObject.setDescription(rs.getString("tariff_desc"));
					tariffMainValueObject.setMainCategory(tariffMainCat);
					tariffMainValueObject.setSubCategory(tariffSubCat);
					tariffMainValueObject.setBillParty(rs.getString("bill_party"));
					tariffMainValueObject.setCOA(rs.getString("coa_acct_nbr"));
					tariffMainValueObject.setGST(rs.getDouble("gst_charge"));
					tariffMainValueObject.setGSTCode(rs.getString("gst_cd"));
					tariffMainValueObject.setCreateDate(rs.getTimestamp("create_dttm"));
					tariffMainValueObject.setWaived(rs.getString("waive_ind"));
					tariffMainValueObject.setWaiveStartDate(rs.getTimestamp("commence_dttm"));
					tariffMainValueObject.setWaiveEndDate(rs.getTimestamp("expiry_dttm"));
					tariffMainValueObject.setWaiveReason(rs.getString("waive_reason"));
					tariffMainValueObject.setScheme(rs.getString("scheme_cd"));
					tariffMainValueObject.setBusinessType(rs.getString("business_type"));
					tariffMainValueObject.setBillAccount(rs.getString("bill_acct_nbr"));
					tariffMainValueObject.setBillContract(rs.getString("bill_contract_nbr"));
					tariffMainValueObject.setMinTonnage(rs.getDouble("min_ton"));

					if (custCd == null && acctNbr == null && contractNbr == null) {
						tariffMainValueObject.setTariffTypeInd(ProcessChargeConst.TARIFF_TYPE_PUBLISH);
					} else {
						tariffMainValueObject.setTariffTypeInd(ProcessChargeConst.TARIFF_TYPE_CUSTOMIZE);
					}
				}

				tariffTierValueObject = new TariffTierVO();
				tariffTierValueObject.setRange(rs.getDouble("fr_range"), rs.getDouble("to_range"));
				tariffTierValueObject.setPerHour(rs.getDouble("hour_rate"));
				tariffTierValueObject.setPerUnit(rs.getDouble("other_rate"));
				tariffTierValueObject.setRate(rs.getDouble("amt_charge"));
				tariffTierValueObject.setPercentCharge(rs.getDouble("percent_charge"));
				tariffTierValueObject.setAdjustmentType(rs.getString("adj_type"));
				tariffTierValueObject.setAdjustment(rs.getDouble("adj_amt"));
				tariffTierValueObject.setPerHourType(rs.getString("hour_rate_type"));
				tariffTierValueObject.setPerUnitType(rs.getString("other_rate_type"));

				tariffMainValueObject.addTier(tariffTierValueObject);
			}
		} catch (BusinessException e) {
			log.info("Exception retrieveTariffByCdTierSeqNbr : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception retrieveTariffByCdTierSeqNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveTariffByCdTierSeqNbr DAO  tariffMainValueObject: " + tariffMainValueObject.toString());
		}	
	
		return tariffMainValueObject;
	}

	// jp.src.ejb.sessionBeans.cab.tariff--->TariffMainEJB--->constructNullQuery()
	private String constructNullQuery(String custCd, String acctNbr, String contractNbr, int contractualYr,
			boolean tierInd, boolean cntrInd, boolean genInd) throws BusinessException {
		StringBuffer sqlQuery = null;
		sqlQuery = new StringBuffer();
		try {
			log.info("START: constructNullQuery DAO :: custCd: " + CommonUtility.deNull(custCd) + " acctNbr: " + CommonUtility.deNull(acctNbr)
			+ " contractNbr: " + CommonUtility.deNull(contractNbr) + " contractualYr: " + CommonUtility.deNull(String.valueOf(contractualYr))
			+ " tierInd: " + CommonUtility.deNull(String.valueOf(tierInd)) + " cntrInd: " + CommonUtility.deNull(String.valueOf(cntrInd)) 
			+ " genInd: " + CommonUtility.deNull(String.valueOf(genInd)));
			if (custCd == null || acctNbr == null || contractNbr == null || contractualYr == 0) {
				if (custCd == null) {
					sqlQuery.append("AND m.cust_cd IS NULL ");
					if (tierInd)
						sqlQuery.append("AND t.cust_cd IS NULL ");
					if (cntrInd)
						sqlQuery.append("AND c.cust_cd IS NULL ");
					if (genInd)
						sqlQuery.append("and g.cust_cd IS NULL ");
				} else {
					if (tierInd)
						sqlQuery.append("AND m.cust_cd=t.cust_cd ");
					if (cntrInd)
						sqlQuery.append("AND m.cust_cd=c.cust_cd ");
					if (genInd)
						sqlQuery.append("AND m.cust_cd=g.cust_cd ");
					sqlQuery.append("AND m.cust_cd= :custCd ");
				}
				if (acctNbr == null) {
					sqlQuery.append("AND m.acct_nbr IS NULL ");
					if (tierInd)
						sqlQuery.append("AND t.acct_nbr IS NULL ");
					if (cntrInd)
						sqlQuery.append("AND c.acct_nbr IS NULL ");
					if (genInd)
						sqlQuery.append("AND g.acct_nbr IS NULL ");
				} else {
					if (tierInd)
						sqlQuery.append("AND m.acct_nbr=t.acct_nbr ");
					if (cntrInd)
						sqlQuery.append("AND m.acct_nbr=c.acct_nbr ");
					if (genInd)
						sqlQuery.append("AND m.acct_nbr=g.acct_nbr ");
					sqlQuery.append("AND m.acct_nbr= :acctNbr ");
				}
				if (contractNbr == null) {
					sqlQuery.append("AND m.contract_nbr IS NULL ");
					if (tierInd)
						sqlQuery.append("AND t.contract_nbr IS NULL ");
					if (cntrInd)
						sqlQuery.append("AND c.contract_nbr IS NULL ");
					if (genInd)
						sqlQuery.append("AND g.contract_nbr IS NULL ");
				} else {
					if (tierInd)
						sqlQuery.append("AND m.contract_nbr=t.contract_nbr ");
					if (cntrInd)
						sqlQuery.append("AND m.contract_nbr=c.contract_nbr ");
					if (genInd)
						sqlQuery.append("AND m.contract_nbr=g.contract_nbr ");
					sqlQuery.append("AND m.contract_nbr=:contractNbr ");
				}
				if (contractualYr == 0) {
					sqlQuery.append("AND m.contractual_yr=0 ");
					if (tierInd)
						sqlQuery.append("AND t.contractual_yr=0 ");
					if (cntrInd)
						sqlQuery.append("AND c.contractual_yr=0 ");
					if (genInd)
						sqlQuery.append("AND g.contractual_yr=0 ");
				} else {
					if (tierInd)
						sqlQuery.append("AND m.contractual_yr=t.contractual_yr ");
					if (cntrInd)
						sqlQuery.append("AND m.contractual_yr=c.contractual_yr ");
					if (genInd)
						sqlQuery.append("AND m.contractual_yr=g.contractual_yr ");
					sqlQuery.append("AND m.contractual_yr=:contractualYr ");
				}
			} else {
				if (tierInd)
					sqlQuery.append("AND m.cust_cd=t.cust_cd ");
				if (tierInd)
					sqlQuery.append("AND m.acct_nbr=t.acct_nbr ");
				if (tierInd)
					sqlQuery.append("AND m.contract_nbr=t.contract_nbr ");
				if (tierInd)
					sqlQuery.append("AND m.contractual_yr=t.contractual_yr ");
				if (cntrInd)
					sqlQuery.append("AND m.cust_cd=c.cust_cd ");
				if (cntrInd)
					sqlQuery.append("AND m.acct_nbr=c.acct_nbr ");
				if (cntrInd)
					sqlQuery.append("AND m.contract_nbr=c.contract_nbr ");
				if (cntrInd)
					sqlQuery.append("AND m.contractual_yr=c.contractual_yr ");
				if (genInd)
					sqlQuery.append("AND m.cust_cd=g.cust_cd ");
				if (genInd)
					sqlQuery.append("AND m.acct_nbr=g.acct_nbr ");
				if (genInd)
					sqlQuery.append("AND m.contract_nbr=g.contract_nbr ");
				if (genInd)
					sqlQuery.append("AND m.contractual_yr=g.contractual_yr ");
				sqlQuery.append("AND m.cust_cd=:custCd ");
				sqlQuery.append("AND m.acct_nbr=:acctNbr ");
				sqlQuery.append("AND m.contract_nbr=:contractNbr ");
				sqlQuery.append("AND m.contractual_yr= :contractualYr ");
			}
		} catch (Exception ex) {
			log.info("Exception constructNullQuery : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: constructNullQuery DAO  sqlQuery: " + sqlQuery.toString());
		}

		return sqlQuery.toString();
	}

	// jp.src.ejb.sessionBeans.cab.tariff--->TariffMainEJB-->retrieveTariffMainTierGenChar()
	public TariffMainVO retrieveTariffMainTierGenChar(int versionNbr, String custCd, String acctNbr, String contractNbr,
			int contractualYr, String tariffMainCat, String tariffSubCat, String businessType, String schemeCd,
			String mvmt, String type, String cntrCat, String cntrSize, Timestamp varDate)
			throws BusinessException {
		
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sqlQuery = null;

		TariffMainVO tariffMainValueObject = null;
		TariffTierVO tariffTierValueObject = null;
		TariffGenCharVO tariffGenCharValueObject = null;
		
		try {
			log.info("START: retrieveTariffMainTierGenChar DAO :: versionNbr: " + CommonUtility.deNull(String.valueOf(versionNbr))
			+ " custCd: " + CommonUtility.deNull(custCd) + " acctNbr: " + CommonUtility.deNull(acctNbr) + " contractNbr: "
			+ CommonUtility.deNull(contractNbr) + " contractualYr: " + CommonUtility.deNull(String.valueOf(contractualYr))
			+ " tariffMainCat: " + CommonUtility.deNull(tariffMainCat) + " tariffSubCat: " + CommonUtility.deNull(tariffSubCat) 
			+ " businessType: " + CommonUtility.deNull(businessType) + " schemeCd: " + CommonUtility.deNull(schemeCd)
			+ " mvmt: " + CommonUtility.deNull(mvmt) + " type: " + CommonUtility.deNull(type) + " cntrCat: " + CommonUtility.deNull(cntrCat)
			+ " cntrSize: " + CommonUtility.deNull(cntrSize) + " varDate: " + CommonUtility.deNull(varDate.toString()));

			sqlQuery = new StringBuffer();
			sqlQuery.append(
					" SELECT m.tariff_cd, m.tariff_desc, m.bill_party, m.coa_acct_nbr,m.gst_charge, m.gst_cd, m.create_dttm, ");
			sqlQuery.append(
					" m.waive_ind, m.commence_dttm, m.expiry_dttm, m.waive_reason, m.scheme_cd, m.business_type, ");
			sqlQuery.append(" m.bill_acct_nbr, m.bill_contract_nbr, m.min_ton ");
			sqlQuery.append(", ");
			sqlQuery.append(
					" t.fr_range, t.to_range, t.hour_rate, t.other_rate, t.amt_charge, t.percent_charge, t.adj_type, ");
			sqlQuery.append(" t.adj_amt, t.hour_rate_type, t.other_rate_type, t.tier_seq_nbr ");
			sqlQuery.append("FROM tariff_main m, tariff_tier t, tariff_gen_char g ");
			sqlQuery.append("WHERE m.version_nbr=t.version_nbr ");
			sqlQuery.append("AND m.tariff_cd=t.tariff_cd ");
			sqlQuery.append("AND m.version_nbr=g.version_nbr ");
			sqlQuery.append("AND m.tariff_cd=g.tariff_cd ");
			sqlQuery.append("AND m.version_nbr= :versionNbr ");
			sqlQuery.append("AND m.tariff_main_cat_cd=:tariffMainCat ");
			sqlQuery.append("AND m.tariff_sub_cat_cd=:tariffSubCat ");
			sqlQuery.append("AND m.scheme_cd=:schemeCd ");
			sqlQuery.append("AND m.business_type=:businessType ");
			sqlQuery.append("AND g.mvmt=:mvmt ");
			sqlQuery.append("AND g.type=:type ");
			sqlQuery.append("AND g.cntr_cat=:cntrCat ");
			sqlQuery.append("AND g.cntr_size=:cntrSize ");
			sqlQuery.append(constructNullQuery(custCd, acctNbr, contractNbr, contractualYr, true, false, true));
			if (varDate != null) {
				sqlQuery.append("AND m.EFFECT_DTTM <= TO_DATE(:varDate,'YYYY-MM-DD HH24:MI:SS') ");
			}
			sqlQuery.append("AND m.tariff_status = 'A' "); //Wanyi added on Dec 2019 to retrieve only approved tariffs for billing
			sqlQuery.append("ORDER BY t.tier_seq_nbr ");

			
			paramMap.put("versionNbr", versionNbr);
			paramMap.put("tariffMainCat", tariffMainCat);
			paramMap.put("tariffSubCat", tariffSubCat);
			paramMap.put("schemeCd", schemeCd);
			paramMap.put("businessType", businessType);
			paramMap.put("mvmt", mvmt);
			paramMap.put("type", type);
			paramMap.put("cntrCat", cntrCat);
			paramMap.put("cntrSize", cntrSize);
			if (custCd != null)
				paramMap.put("custCd", custCd);
			if (acctNbr != null)
				paramMap.put("acctNbr", acctNbr);
			if (contractNbr != null)
				paramMap.put("contractNbr", contractNbr);
			if (contractualYr != 0)
				paramMap.put("contractualYr", contractualYr);			
			if (varDate != null) {
				SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String varDate1 = sm.format(varDate);
				paramMap.put("varDate", varDate1);
			}
			log.info("SQL: " + sqlQuery.toString() + " paramMap: " + paramMap);
			rs =namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);


			tariffMainValueObject = new TariffMainVO();
			tariffGenCharValueObject = new TariffGenCharVO();

			while (rs.next()) {
				if (!tariffMainValueObject.isModified(tariffMainValueObject)) {
					tariffMainValueObject.setCode(rs.getString("tariff_cd"));
					tariffMainValueObject.setDescription(rs.getString("tariff_desc"));
					tariffMainValueObject.setMainCategory(tariffMainCat);
					tariffMainValueObject.setSubCategory(tariffSubCat);
					tariffMainValueObject.setBillParty(rs.getString("bill_party"));
					tariffMainValueObject.setCOA(rs.getString("coa_acct_nbr"));
					tariffMainValueObject.setGST(rs.getDouble("gst_charge"));
					tariffMainValueObject.setGSTCode(rs.getString("gst_cd"));
					tariffMainValueObject.setCreateDate(rs.getTimestamp("create_dttm"));
					tariffMainValueObject.setWaived(rs.getString("waive_ind"));
					tariffMainValueObject.setWaiveStartDate(rs.getTimestamp("commence_dttm"));
					tariffMainValueObject.setWaiveEndDate(rs.getTimestamp("expiry_dttm"));
					tariffMainValueObject.setWaiveReason(rs.getString("waive_reason"));
					tariffMainValueObject.setScheme(rs.getString("scheme_cd"));
					tariffMainValueObject.setBusinessType(rs.getString("business_type"));
					tariffMainValueObject.setBillAccount(rs.getString("bill_acct_nbr"));
					tariffMainValueObject.setBillContract(rs.getString("bill_contract_nbr"));
					tariffMainValueObject.setMinTonnage(rs.getDouble("min_ton"));

					if (custCd == null && acctNbr == null && contractNbr == null) {
						tariffMainValueObject.setTariffTypeInd(ProcessChargeConst.TARIFF_TYPE_PUBLISH);
					} else {
						tariffMainValueObject.setTariffTypeInd(ProcessChargeConst.TARIFF_TYPE_CUSTOMIZE);
					}
				}

				tariffTierValueObject = new TariffTierVO();
				tariffTierValueObject.setRange(rs.getDouble("fr_range"), rs.getDouble("to_range"));
				tariffTierValueObject.setPerHour(rs.getDouble("hour_rate"));
				tariffTierValueObject.setPerUnit(rs.getDouble("other_rate"));
				tariffTierValueObject.setRate(rs.getDouble("amt_charge"));
				tariffTierValueObject.setPercentCharge(rs.getDouble("percent_charge"));
				tariffTierValueObject.setAdjustmentType(rs.getString("adj_type"));
				tariffTierValueObject.setAdjustment(rs.getDouble("adj_amt"));
				tariffTierValueObject.setPerHourType(rs.getString("hour_rate_type"));
				tariffTierValueObject.setPerUnitType(rs.getString("other_rate_type"));

				tariffMainValueObject.addTier(tariffTierValueObject);
			}

			tariffGenCharValueObject.setMovement(mvmt);
			tariffGenCharValueObject.setType(type);
			tariffGenCharValueObject.setContainerCategory(cntrCat);
			tariffGenCharValueObject.setContainerSize(cntrSize);

			tariffMainValueObject.addGenChar(tariffGenCharValueObject);

		} catch (BusinessException e) {
			log.info("Exception retrieveTariffMainTierGenChar : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception retrieveTariffMainTierGenChar : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveTariffMainTierGenChar DAO  tariffMainValueObject: " + tariffMainValueObject.toString());
		}

		return tariffMainValueObject;
	}

	// jp.src.ejb.sessionBeans.cab.tariff--->TariffMainEJB-->isJNL2JNL()
	public boolean isJNL2JNL(GeneralEventLogValueObject vo) throws BusinessException {
		
		SqlRowSet  rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		boolean result = false;
		if (vo == null || vo.getDiscVvCd() == null || vo.getLoadVvCd() == null || vo.getMvmt() == null) {
			return result;
		}
		try {
			log.info("START: isJNL2JNL DAO :: vo: " + CommonUtility.deNull(vo.toString()));
			String sql = "SELECT COUNT(1) CNT FROM VESSEL_CALL WHERE VV_CD IN (:disc, :load) AND UPPER(SCHEME)='JNL'";
			paramMap.put("disc", vo.getDiscVvCd());
			paramMap.put("load", vo.getLoadVvCd());
			log.info("SQL" + sql.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {
				int cnt = rs.getInt("CNT");
				if (cnt == 2 && vo.getMvmt().trim().equalsIgnoreCase("TS")) {
					result = true;
				}
			}
		} catch (Exception e) {
			log.info("Exception isJNL2JNL : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isJNL2JNL DAO  result: " + result);
		}

		return result;
	}

	// jp.src.ejb.sessionBeans.cab.tariff--->TariffMainEJB-->getGeneralCargoCustFspDays()
	public int getGeneralCargoCustFspDays(GeneralEventLogValueObject generalEventLogValueObject)
			throws RemoteException, BusinessException {
//		String discVvCd = generalEventLogValueObject.getDiscVvCd();
//		String loadVvCd = generalEventLogValueObject.getLoadVvCd();
//
//		String mvmt = generalEventLogValueObject.getMvmt().substring(0, 1);
//		String vvInd = generalEventLogValueObject.getVvInd();
		String discVvCd = "";
		String loadVvCd = "";

		String mvmt = "";
		String vvInd = "";
		String gdsType = "G";
		String stgType = null;
		String hsCd = null;
		String acctNbr = "";
		Timestamp varDttm = null;
		String refNbr = null;
		String refInd = null;
		int discFsp = 0;
		int loadFsp = 0;
		try {
			log.info("START: getGeneralCargoCustFspDays DAO :: generalEventLogValueObject: " + CommonUtility.deNull(generalEventLogValueObject.toString()));	
			// Start: SL-CAB-20170518-01
			discVvCd = generalEventLogValueObject.getDiscVvCd();
			acctNbr = generalEventLogValueObject.getBillAcctNbr();
			loadVvCd = generalEventLogValueObject.getLoadVvCd();
			mvmt = generalEventLogValueObject.getMvmt().substring(0, 1);
			vvInd = generalEventLogValueObject.getVvInd();
			
			acctNbr = (acctNbr == null) ? "" : acctNbr.trim();
			varDttm = generalEventLogValueObject.getVarDttm();
			varDttm = (varDttm == null) ? new Timestamp(System.currentTimeMillis()) : varDttm;
			// End: SL-CAB-20170518-01

			refInd = generalEventLogValueObject.getRefInd();

			StringBuffer sqlBuffer = new StringBuffer();
			if (ProcessChargeConst.REF_IND_DN.equals(refInd)) {
				sqlBuffer.append("SELECT m.stg_type, m.hs_code ");
				sqlBuffer.append("FROM manifest_details m, gb_edo e, dn_details d ");
				sqlBuffer.append("WHERE e.bl_nbr = m.bl_nbr ");
				sqlBuffer.append("AND d.edo_asn_nbr = e.edo_asn_nbr ");
				sqlBuffer.append("AND d.dn_nbr = :refNbr ");

				refNbr = generalEventLogValueObject.getDnNbr();
			}
			if (ProcessChargeConst.REF_IND_EDO.equals(refInd)) {
				sqlBuffer.append("SELECT m.stg_type, m.hs_code ");
				sqlBuffer.append("FROM manifest_details m, gb_edo e ");
				sqlBuffer.append("WHERE e.bl_nbr = m.bl_nbr ");
				sqlBuffer.append("AND e.edo_asn_nbr=:refNbr ");

				refNbr = generalEventLogValueObject.getEdoAsnNbr();
			}
			if (ProcessChargeConst.REF_IND_ESN.equals(refInd)) {
				sqlBuffer.append("SELECT stg_ind as stg_type, esn_hs_code AS hs_code ");
				sqlBuffer.append("FROM esn_details ");
				sqlBuffer.append("WHERE esn_asn_nbr = :refNbr");

				refNbr = generalEventLogValueObject.getEsnAsnNbr();
			}

			if (refNbr != null) {
				SqlRowSet rs = null;
				Map<String, Object> paramMap = new HashMap<String, Object>();

				try {	
					
					paramMap.put("refNbr", refNbr);
					log.info("SQL: " + sqlBuffer.toString() + " paramMap: " + paramMap);
					rs = namedParameterJdbcTemplate.queryForRowSet(sqlBuffer.toString(), paramMap);

					if (rs.next()) {
						stgType = rs.getString("stg_type");
						hsCd = rs.getString("hs_code");
					}
					
				
				} catch (Exception ex) {
					log.info("Exception getGeneralCargoCustFspDays : ", ex);
					throw new BusinessException("M0010");
				} 
//				finally {
//					log.info("END: getGeneralCargoCustFspDays DAO");
//				}
			}

			log.info("--------------- General Cargo Customized FSP Start ---------------");
			log.info("--------------- discVvCd	: " + discVvCd);
			log.info("--------------- loadVvCd	: " + loadVvCd);
			log.info("--------------- mvmt		: " + mvmt);
			log.info("--------------- vvInd		: " + vvInd);
			log.info("--------------- gdsType	: " + gdsType);
			log.info("--------------- stgType	: " + stgType);
			log.info("--------------- hsCd		: " + hsCd);
			log.info("--------------- refInd		: " + refInd);
			log.info("--------------- acctNbr		: " + acctNbr);
			log.info("--------------- varDttm		: " + varDttm.toString());
			log.info("--------------- General Cargo Customized FSP End ---------------");

			String discGateway = generalEventLogValueObject.getDiscGateway();
			log.info("--------------- discGateway		: " + discGateway);

			if (!(discGateway != null && discGateway.trim().equalsIgnoreCase("P")
					&& mvmt.trim().equalsIgnoreCase("T"))) {
				// Start: SL-CAB-20170518-01 to check whether to retrieve fsp by acct number
				boolean getFSPbySRAcct = isCustFSPbySRAcct(generalEventLogValueObject);
				log.info("--------------- getFSPbySRAcct		: " + getFSPbySRAcct);
				// End: SL-CAB-20170518-01
				if (StringUtils.isNotEmpty(discVvCd)) {
					// Start: SL-CAB-20170518-01
					if (getFSPbySRAcct) {
						log.info("---------------getting discFspbySRAcct---------------");
						discFsp = getGBCustFspDays(discVvCd, mvmt, vvInd, stgType, gdsType, hsCd, acctNbr, varDttm);
						log.info("--------------- discFspbySRAcct		: " + discFsp);

					} else { // End: SL-CAB-20170518-01
						discFsp = getGBCustFspDays(discVvCd, mvmt, vvInd, stgType, gdsType, hsCd);
					}
				}
				if (StringUtils.isNotEmpty(loadVvCd)) {
					// Start: SL-CAB-20170518-01
					if (getFSPbySRAcct) {
						log.info("---------------getting loadFspbySRAcct---------------");
						loadFsp = getGBCustFspDays(loadVvCd, mvmt, vvInd, stgType, gdsType, hsCd, acctNbr, varDttm);
						log.info("--------------- loadFspbySRAcct		: " + loadFsp);
					} else { // End: SL-CAB-20170518-01
						loadFsp = getGBCustFspDays(loadVvCd, mvmt, vvInd, stgType, gdsType, hsCd);
					}
				}
				log.info(" discFsp:" + discFsp + "loadFsp:" + loadFsp);
			}
		} catch (BusinessException e) {
			log.info("Exception getGeneralCargoCustFspDays : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getGeneralCargoCustFspDays : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getGeneralCargoCustFspDays DAO");
		}
		return discFsp > loadFsp ? discFsp : loadFsp;
	}

	// jp.src.ejb.sessionBeans.cab.tariff--->TariffMainEJB-->getGBCustFspDays()
	@Override
	public int getGBCustFspDays(String vvCd, String mvmt, String vvInd, String stgType, String gdsType, String hsCd,
			String acctNbr, Timestamp txnDate) throws BusinessException {

		int fsp = 0;
		SqlRowSet  rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sqlQuery = new StringBuffer();

		try {
			log.info("START: getGBCustFspDays DAO :: vvCd: " + CommonUtility.deNull(vvCd) + " mvmt: " + CommonUtility.deNull(mvmt)
					+ " vvInd: " + CommonUtility.deNull(vvInd) + " stgType: " + CommonUtility.deNull(stgType)
					+ " gdsType: " + CommonUtility.deNull(gdsType) + " hsCd: " + CommonUtility.deNull(hsCd)
					+ " acctNbr: " + CommonUtility.deNull(acctNbr) + " txnDate: " + CommonUtility.deNull(txnDate.toString()));

			sqlQuery.append("select nvl(max(a.fsp), 0) fsp ");
			sqlQuery.append("from TARIFF_GB_CUSTOMIZED_FSP a, cust_contract b ");
			sqlQuery.append("where a.cust_cd = b.cust_cd ");
			sqlQuery.append("and a.acct_nbr = b.acct_nbr ");
			sqlQuery.append("and a.contract_nbr = b.contract_nbr ");
			sqlQuery.append("and b.commence_dttm <= :txnDate ");
			sqlQuery.append("and b.expiry_dttm >= :txnDate ");

			paramMap.put("txnDate", txnDate);

			if (vvCd != null) {
				sqlQuery.append("and (vv_cd = :vvCd or vsl_type = 'A') ");
				paramMap.put("vvCd",vvCd);
			} else {
				sqlQuery.append("and vsl_type = 'A' ");
			}
			if (mvmt != null) {
				sqlQuery.append("and (mvmt = :mvmt or mvmt = 'A') ");
				paramMap.put("mvmt",mvmt);
			} else {
				sqlQuery.append("and mvmt = 'A' ");
			}
			if (vvInd != null) {
				sqlQuery.append("and (op_type = :vvInd or op_type = 'A') ");
				paramMap.put("vvInd",vvInd);
			} else {
				sqlQuery.append("and op_type = 'A' ");
			}
			if (stgType != null) {
				sqlQuery.append("and (stg_type = :stgType or stg_type = 'A') ");
				paramMap.put("stgType",stgType);
			} else {
				sqlQuery.append("and stg_type = 'A' ");
			}
			if (gdsType != null) {
				sqlQuery.append("and (gds_type = :gdsType or gds_type = 'A') ");
				paramMap.put("gdsType",gdsType);
			} else {
				sqlQuery.append("and gds_type = 'A' ");
			}
			if (hsCd != null) {
				sqlQuery.append("and (nvl(instr(hs_cd, :hsCd),0) > 0 or hs_cd = 'ALL') ");
				paramMap.put("hsCd",hsCd);
			} else {
				sqlQuery.append("and hs_cd = 'ALL' ");
			}
			sqlQuery.append("and a.status = 'A' ");
			sqlQuery.append("and a.acct_nbr = :acctNbr ");
			paramMap.put("acctNbr",acctNbr);

			log.info("SQL STRING in getGBCustFspDays::::: " + sqlQuery.toString());
			log.info("SQL STRING in getGBCustFspDays:::::" + sqlQuery.toString());
			log.info("SQL" + sqlQuery.toString() + " paramMap: " + paramMap);

			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);

			if (rs.next()) {
				fsp = rs.getInt("fsp");
				log.info("fsp in getGBCustFspDays:::::" + fsp);
			} else {
				log.info("No cust FSP found");
			}
		
		} catch (Exception ex) {
			log.info("Exception getGBCustFspDays : ", ex);
			throw new BusinessException("M0010");
		} finally {
			log.info("END: getGBCustFspDays DAO  fsp: " + fsp);
		}

		return fsp;
	}

	// jp.src.ejb.sessionBeans.cab.tariff--->TariffMainEJB-->isCustFSPbySRAcct()
	@Override
	public boolean isCustFSPbySRAcct(GeneralEventLogValueObject vo) throws BusinessException {
		boolean isCust = false;
		try {
			log.info("START: isCustFSPbySRAcct DAO :: vo: " + CommonUtility.deNull(vo.toString()));
			
			if (vo == null || vo.equals(new GeneralEventLogValueObject())) {
				log.info("---VO is null, return false----");
				isCust = false;
			}
			
			log.info("---vo.getMvmt()----" + vo.getMvmt());
			if (isJNL2JNL(vo) || (vo.getMvmt() != null
					&& (vo.getMvmt().trim().equalsIgnoreCase("LL") || vo.getMvmt().trim().equalsIgnoreCase("L")))) {
				log.info("---isCustFSPbySRAcct is true----");
				isCust = true;
			} else {
				log.info("---isCustFSPbySRAcct is false----");
				isCust = false;
			}
		} catch (BusinessException e) {
			log.info("Exception isCustFSPbySRAcct : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception isCustFSPbySRAcct : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isCustFSPbySRAcct DAO  isCust: " + isCust);
		}
		return isCust;
	}

	// jp.src.ejb.sessionBeans.cab.tariff--->TariffMainEJB-->getGBCustFspDays()
	@Override
	public int getGBCustFspDays(String vvCd, String mvmt, String vvInd, String stgType, String gdsType, String hsCd)
			throws BusinessException {
		List<TariffCustomisedGBFspValueObject> tariffCustFspList = retrieveTariffGBCustFSP(vvCd);
		int customizedFsp = 0;
		try {
			log.info("START: getGBCustFspDays DAO :: vvCd: " + CommonUtility.deNull(vvCd) + " mvmt: " + CommonUtility.deNull(mvmt)
					+ " vvInd: " + CommonUtility.deNull(vvInd) + " stgType: " + CommonUtility.deNull(stgType)
					+ " gdsType: " + CommonUtility.deNull(gdsType) + " hsCd: " + CommonUtility.deNull(hsCd));
			for (Iterator<TariffCustomisedGBFspValueObject> it = tariffCustFspList.iterator(); it.hasNext();) {
				TariffCustomisedGBFspValueObject vo = (TariffCustomisedGBFspValueObject) it.next();
				if (vo.getVslType().equals("C") && !vo.getVvcd().equals(vvCd)) {
					continue;
				}
				if (!vo.getMvmt().equals("A") && !vo.getMvmt().equals(mvmt)) {
					continue;
				}
				if (!vo.getOpType().equals("A") && !vo.getOpType().equals(vvInd)) {
					continue;
				}
				if (!vo.getStgType().equals("A") && !vo.getStgType().equals(stgType)) {
					continue;
				}
				if (!vo.getGdsType().equals("A") && !vo.getGdsType().equals(gdsType)) {
					continue;
				}
				if (!vo.getHsCD().equals("ALL") && (vo.getHsCD().indexOf(hsCd) < 0)) {
					continue;
				}

				int fsp = Integer.parseInt(vo.getFsp());
				if (fsp > customizedFsp) {
					customizedFsp = fsp;
				}
			}
		} catch (Exception e) {
			log.info("Exception getGBCustFspDays : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getGBCustFspDays DAO  customizedFsp: " + customizedFsp);
		}
		return customizedFsp;
	}

	// jp.src.ejb.sessionBeans.cab.tariff--->TariffMainEJB-->retrieveTariffGBCustFSP()
	private List<TariffCustomisedGBFspValueObject> retrieveTariffGBCustFSP(String vvCd) throws BusinessException {
		SqlRowSet rs = null;
		StringBuffer sqlQuery = new StringBuffer();
		List<TariffCustomisedGBFspValueObject> list = new ArrayList<TariffCustomisedGBFspValueObject>();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {
			log.info("START: retrieveTariffGBCustFSP DAO vvCd:" + CommonUtility.deNull(vvCd));

			sqlQuery.append("SELECT a.* ");
			sqlQuery.append("FROM TARIFF_GB_CUSTOMIZED_FSP a, ");
			sqlQuery.append("(SELECT c.cust_cd, c.acct_nbr, c.contract_nbr ");
			sqlQuery.append("FROM cust_contract c, ");
			sqlQuery.append("(SELECT create_cust_cd AS cust_cd, bill_acct_nbr AS acct_nbr ");
			sqlQuery.append("FROM vessel_call ");
			sqlQuery.append("WHERE vv_cd = :vvCd) t ");
			sqlQuery.append("WHERE c.cust_cd = t.cust_cd ");
			sqlQuery.append("AND c.acct_nbr = t.acct_nbr ");
			sqlQuery.append("AND business_type = 'G' ");
			sqlQuery.append("AND commence_dttm < :currentTimestamp ");
			sqlQuery.append("AND expiry_dttm > :currentTimestamp) b ");
			sqlQuery.append("WHERE a.cust_cd = b.cust_cd ");
			sqlQuery.append("AND a.acct_nbr = b.acct_nbr ");
			sqlQuery.append("AND a.contract_nbr = b.contract_nbr ");
			sqlQuery.append("AND a.status = 'A'");

			Timestamp currentTimestamp = UserTimestampVO.getCurrentTimestamp();
			paramMap.put("vvCd", vvCd);
			paramMap.put("currentTimestamp", currentTimestamp);
			log.info("SQL " + sqlQuery.toString() + " paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);


			while (rs.next()) {
				TariffCustomisedGBFspValueObject vo = new TariffCustomisedGBFspValueObject();
				vo.setFspNBR(rs.getInt("GB_FSP_SEQ_NBR"));
				vo.setCustCD(rs.getString("CUST_CD"));
				vo.setAcctNBR(rs.getString("ACCT_NBR"));
				vo.setContractNBR(rs.getString("CONTRACT_NBR"));
				vo.setVslType(rs.getString("VSL_TYPE"));
				vo.setVvcd(rs.getString("VV_CD"));
				vo.setVslNm(rs.getString("VSL_NM"));
				vo.setInVOYNBR(rs.getString("IN_VOY_NBR"));
				vo.setOutVOYNBR(rs.getString("OUT_VOY_NBR"));
				vo.setMvmt(rs.getString("MVMT"));
				vo.setOpType(rs.getString("OP_TYPE"));
				vo.setStgType(rs.getString("STG_TYPE"));
				vo.setGdsType(rs.getString("GDS_TYPE"));
				vo.setHsCD(rs.getString("HS_CD"));
				vo.setFsp(rs.getString("FSP"));
				vo.setLastModifyUserId(rs.getString("LAST_MODIFY_USER_ID"));
				vo.setLastModifyDTTM(rs.getTimestamp("LAST_MODIFY_DTTM"));

				list.add(vo);
			}
		
		} catch (Exception ex) {
			log.info("Exception retrieveTariffGBCustFSP : ", ex);
			throw new BusinessException("M0010");
		} finally {
			log.info("END retrieveTariffGBCustFSP DAO  list: " + list.toString());
		}
		return list;
	}

	@Override
	// jp.src.ejb.sessionBeans.cab.tariff--->TariffMainEJB--->retrieveTariffMainTierAdm()
	public TariffMainVO retrieveTariffMainTierAdm(int versionNbr, String custCd, String acctNbr, String contractNbr,
			int contractualYr, String tariffMainCat, String tariffSubCat, String schemeCd, String businessType,
			Timestamp varDate) throws BusinessException {

		SqlRowSet  rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sqlQuery = null;

		TariffMainVO tariffMainValueObject = null;
		TariffTierVO tariffTierValueObject = null;
		try {
			log.info("START: retrieveTariffMainTierAdm DAO :: versionNbr: " + CommonUtility.deNull(String.valueOf(versionNbr)) + " custCd: " + CommonUtility.deNull(custCd)
					+ " acctNbr: " + CommonUtility.deNull(acctNbr) + " contractNbr: " + CommonUtility.deNull(contractNbr)
					+ " contractualYr: " + CommonUtility.deNull(String.valueOf(contractualYr)) + " tariffMainCat: " + CommonUtility.deNull(tariffMainCat)
					+ " tariffSubCat: " + CommonUtility.deNull(tariffSubCat) + " schemeCd: " + CommonUtility.deNull(schemeCd)
					+ " businessType: " + CommonUtility.deNull(businessType) + " varDate: " + CommonUtility.deNull(varDate.toString()));
			

			sqlQuery = new StringBuffer();
			// formulate the sql query and execute the query
			sqlQuery.append(
					" SELECT m.tariff_cd, m.tariff_desc, m.bill_party, m.coa_acct_nbr,m.gst_charge, m.gst_cd, m.create_dttm, m.waive_ind, ");
			sqlQuery.append(" m.commence_dttm, m.expiry_dttm, m.waive_reason, m.scheme_cd, m.business_type, ");
			sqlQuery.append(" m.bill_acct_nbr, m.bill_contract_nbr, m.min_ton ");
			sqlQuery.append(", ");
			sqlQuery.append(
					" t.fr_range, t.to_range, t.hour_rate, t.other_rate, t.amt_charge, t.percent_charge, t.adj_type, t.adj_amt, ");
			sqlQuery.append("  t.hour_rate_type, t.other_rate_type, t.tier_seq_nbr");
			sqlQuery.append("FROM tariff_main m, tariff_tier t ");
			sqlQuery.append("WHERE m.version_nbr=t.version_nbr ");
			sqlQuery.append("AND m.tariff_cd=t.tariff_cd ");
			sqlQuery.append("AND m.version_nbr=:versionNbr ");
			sqlQuery.append("AND m.tariff_main_cat_cd=:tariffMainCat ");
			sqlQuery.append("AND m.tariff_sub_cat_cd=:tariffSubCat ");
			if (schemeCd != null)
				sqlQuery.append("AND m.scheme_cd=:schemeCd ");
			if (businessType != null)
				sqlQuery.append("AND m.business_type=:businessType ");
			sqlQuery.append(constructNullQuery(custCd, acctNbr, contractNbr, contractualYr, true, false, false));
			// add by hujun on 7/6/2011 for add tariff item effective date parameter
			if (varDate != null) {
				sqlQuery.append("AND m.EFFECT_DTTM<=:varDate ");
			}
			// add end
			sqlQuery.append("ORDER BY t.tier_seq_nbr ");

			
			paramMap.put("versionNbr", versionNbr);
			paramMap.put("tariffMainCat", tariffMainCat);
			paramMap.put("tariffSubCat", tariffSubCat);
			if (schemeCd != null)
				paramMap.put("schemeCd", schemeCd);
			if (businessType != null)
				paramMap.put("businessType", businessType);
			
			if (custCd != null)
				paramMap.put("custCd", custCd);
			if (acctNbr != null)
				paramMap.put("acctNbr", acctNbr);
			if (contractNbr != null)
				paramMap.put("contractNbr", contractNbr);
			if (contractualYr != 0)
				paramMap.put("contractualYr", contractualYr);	
			// add by hujun on 7/6/2011 for add tariff item effective date parameter
			if (varDate != null) {
				paramMap.put("varDate", varDate);
			}
			// add end
			log.info("SQL: " + sqlQuery.toString() + " paramMap: " + paramMap);
			rs =namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);

			// initialize tariff related value object
			tariffMainValueObject = new TariffMainVO();

			while (rs.next()) {
				// set the retrieved database values into the TariffMainValueObject
				if (!tariffMainValueObject.isModified(tariffMainValueObject)) {
					// set the retrieved database values into the TariffMainValueObject
					tariffMainValueObject.setCode(rs.getString("tariff_cd"));
					tariffMainValueObject.setDescription(rs.getString("tariff_desc"));
					tariffMainValueObject.setMainCategory(tariffMainCat);
					tariffMainValueObject.setSubCategory(tariffSubCat);
					tariffMainValueObject.setBillParty(rs.getString("bill_party"));
					tariffMainValueObject.setCOA(rs.getString("coa_acct_nbr"));
					tariffMainValueObject.setGST(rs.getDouble("gst_charge"));
					tariffMainValueObject.setGSTCode(rs.getString("gst_cd"));
					tariffMainValueObject.setCreateDate(rs.getTimestamp("create_dttm"));
					tariffMainValueObject.setWaived(rs.getString("waive_ind"));
					tariffMainValueObject.setWaiveStartDate(rs.getTimestamp("commence_dttm"));
					tariffMainValueObject.setWaiveEndDate(rs.getTimestamp("expiry_dttm"));
					tariffMainValueObject.setWaiveReason(rs.getString("waive_reason"));
					tariffMainValueObject.setScheme(rs.getString("scheme_cd"));
					tariffMainValueObject.setBusinessType(rs.getString("business_type"));
					tariffMainValueObject.setBillAccount(rs.getString("bill_acct_nbr"));
					tariffMainValueObject.setBillContract(rs.getString("bill_contract_nbr"));
					tariffMainValueObject.setMinTonnage(rs.getDouble("min_ton"));

					if (custCd == null && acctNbr == null && contractNbr == null) {
						tariffMainValueObject.setTariffTypeInd(ProcessChargeConst.TARIFF_TYPE_PUBLISH);
					} else {
						tariffMainValueObject.setTariffTypeInd(ProcessChargeConst.TARIFF_TYPE_CUSTOMIZE);
					}
				}

				// set the retrieved database values into the TariffTierValueObject
				tariffTierValueObject = new TariffTierVO();
				tariffTierValueObject.setId(rs.getInt("tier_seq_nbr"));
				tariffTierValueObject.setRange(rs.getDouble("fr_range"), rs.getDouble("to_range"));
				tariffTierValueObject.setPerHour(rs.getDouble("hour_rate"));
				tariffTierValueObject.setPerUnit(rs.getDouble("other_rate"));
				tariffTierValueObject.setRate(rs.getDouble("amt_charge"));
				tariffTierValueObject.setPercentCharge(rs.getDouble("percent_charge"));
				tariffTierValueObject.setAdjustmentType(rs.getString("adj_type"));
				tariffTierValueObject.setAdjustment(rs.getDouble("adj_amt"));
				tariffTierValueObject.setPerHourType(rs.getString("hour_rate_type"));
				tariffTierValueObject.setPerUnitType(rs.getString("other_rate_type"));

				// add a TariffTierValueObject to the TariffMainValueObject
				tariffMainValueObject.addTier(tariffTierValueObject);
			}
		} catch (BusinessException e) {
			log.info("Exception retrieveTariffMainTierAdm : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception retrieveTariffMainTierAdm : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveTariffMainTierAdm DAO  tariffMainValueObject: " + tariffMainValueObject.toString());
			
		}
		return tariffMainValueObject;
	}

	// ejb.sessionBeans.cab.tariff-->TariffMainEJB-->retrieveBillPartyByTariffCd()
	@Override
	public String retrieveBillPartyByTariffCd(int versionNbr, String tariffCd) throws BusinessException {
		String sql = "";
		String billParty = null;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: retrieveBillPartyByTariffCd  DAO  Start Obj " + " versionNbr:" + versionNbr + " tariffCd:"
					+ tariffCd);
			sql = "select bill_party from tariff_main where version_nbr = :versionNbr and tariff_cd = :tariffCd and cust_cd is null and contract_nbr is null";

			paramMap.put("versionNbr", versionNbr);
			paramMap.put("tariffCd", tariffCd);
			log.info(" *** retrieveBillPartyByTariffCd SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				billParty = rs.getString(1);
				log.info("[CAB],original bill party for : " + versionNbr + " & " + tariffCd + " is " + billParty);
			}

			log.info("END: *** retrieveBillPartyByTariffCd Result *****" + billParty.toString());
		} catch (NullPointerException e) {
			log.error("Exception: retrieveBillPartyByTariffCd ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveBillPartyByTariffCd ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: retrieveBillPartyByTariffCd  DAO  END");
		}

		return billParty;
	}
	
	// ejb.sessionBeans.cab.tariff-->TariffMainEJB-->retrieveTariffMainTier()
	@Override
	public TariffMainVO retrieveTariffMainTier(int versionNbr, String custCd, String acctNbr, String contractNbr,
			// amended by hujun on 7/6/2011 for add tariff item effective date parameter
			// String tariffMainCat, String tariffSubCat, String schemeCd, String
			// businessType) throws SQLException,
			String tariffMainCat, String tariffSubCat, String schemeCd, String businessType, Timestamp varDate) throws
	// amended end
	BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sqlQuery = null;
		TariffMainVO tariffMainValueObject = null;
		TariffTierVO tariffTierValueObject = null;
		// addition of contractual year
		int contractualYr = 0;
		// end of addition
		try {
			log.info("START: retrieveTariffMainTier  DAO  Start Obj " + " versionNbr:" + versionNbr + " custCd:"
					+ custCd + " acctNbr:" + acctNbr + " contractNbr:"
					+ contractNbr+ " tariffMainCat:" + tariffMainCat + " tariffSubCat:"
					+ tariffSubCat+ " schemeCd:" + schemeCd + " businessType:"
					+ businessType + " varDate:" + varDate);

			// default the contractual yr to 0 for publish and 1 for customize for CTMS
			if (custCd == null || acctNbr == null || contractNbr == null) {
				contractualYr = 0;
			} else {
				contractualYr = 1;
			}
			// end default

			sqlQuery = new StringBuffer();

			// formulate the sql query and execute the query
			sqlQuery.append(
					"SELECT m.tariff_cd, m.tariff_desc, m.bill_party, m.coa_acct_nbr,m.gst_charge, m.gst_cd, m.create_dttm, m.waive_ind, m.commence_dttm, m.expiry_dttm, m.waive_reason, m.scheme_cd, m.business_type, m.bill_acct_nbr, m.bill_contract_nbr, m.min_ton ");
			sqlQuery.append(", ");
			sqlQuery.append(
					"t.fr_range, t.to_range, t.hour_rate, t.other_rate, t.amt_charge, t.percent_charge, t.adj_type, t.adj_amt, t.hour_rate_type, t.other_rate_type, t.tier_seq_nbr ");
			sqlQuery.append("FROM tariff_main m, tariff_tier t ");
			sqlQuery.append("WHERE m.version_nbr=t.version_nbr ");
			sqlQuery.append("AND m.tariff_cd=t.tariff_cd ");
			sqlQuery.append("AND m.version_nbr=:versionNbr ");
			sqlQuery.append("AND m.tariff_main_cat_cd=:tariffMainCat ");
			sqlQuery.append("AND m.tariff_sub_cat_cd=:tariffSubCat ");
			// addition of scheme and business type
			if (schemeCd != null)
				sqlQuery.append("AND m.scheme_cd=:schemeCd ");
			if (businessType != null)
				sqlQuery.append("AND m.business_type=:businessType ");
			// end of addition
			sqlQuery.append(constructNullQuery(custCd, acctNbr, contractNbr, contractualYr, true, false, false));
			// add by hujun on 7/6/2011 for add tariff item effective date parameter
			if (varDate != null) {
				sqlQuery.append("AND m.EFFECT_DTTM<= :varDate ");
			}
			// add end
			sqlQuery.append("ORDER BY t.tier_seq_nbr ");

			paramMap.put("versionNbr", versionNbr);
			paramMap.put("tariffMainCat", tariffMainCat);
			paramMap.put("tariffSubCat", tariffSubCat);
			// addition of scheme and business type
			if (schemeCd != null)
				paramMap.put("schemeCd", schemeCd);
			if (businessType != null)
				paramMap.put("businessType", businessType);
			// end of addition
			if (custCd != null)
				paramMap.put("custCd", custCd);
			if (acctNbr != null)
				paramMap.put("acctNbr", acctNbr);
			if (contractNbr != null)
				paramMap.put("contractNbr", contractNbr);
			// addition of contractual year
			if (contractualYr != 0)
				paramMap.put("contractualYr", contractualYr);
			// end of addition

			// add by hujun on 7/6/2011 for add tariff item effective date parameter
			if (varDate != null) {
				paramMap.put("varDate", varDate);
			}
			// add end
			log.info(" *** retrieveTariffMainTier SQL *****" + sqlQuery.toString() + " paramMap "
					+ paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);

			// initialize TariffMainValueObject
			tariffMainValueObject = new TariffMainVO();

			while (rs.next()) {
				// set the retrieved database values into the TariffMainValueObject
				if (!tariffMainValueObject.isModified(tariffMainValueObject)) {
					// set the retrieved database values into the TariffMainValueObject
					tariffMainValueObject.setCode(rs.getString("tariff_cd"));
					tariffMainValueObject.setDescription(rs.getString("tariff_desc"));
					tariffMainValueObject.setMainCategory(tariffMainCat);
					tariffMainValueObject.setSubCategory(tariffSubCat);
					tariffMainValueObject.setBillParty(rs.getString("bill_party"));
					tariffMainValueObject.setCOA(rs.getString("coa_acct_nbr"));
					tariffMainValueObject.setGST(rs.getDouble("gst_charge"));
					tariffMainValueObject.setGSTCode(rs.getString("gst_cd"));
					tariffMainValueObject.setCreateDate(rs.getTimestamp("create_dttm"));
					tariffMainValueObject.setWaived(rs.getString("waive_ind"));
					tariffMainValueObject.setWaiveStartDate(rs.getTimestamp("commence_dttm"));
					tariffMainValueObject.setWaiveEndDate(rs.getTimestamp("expiry_dttm"));
					tariffMainValueObject.setWaiveReason(rs.getString("waive_reason"));
					tariffMainValueObject.setScheme(rs.getString("scheme_cd"));
					tariffMainValueObject.setBusinessType(rs.getString("business_type"));
					tariffMainValueObject.setBillAccount(rs.getString("bill_acct_nbr"));
					tariffMainValueObject.setBillContract(rs.getString("bill_contract_nbr"));
					tariffMainValueObject.setMinTonnage(rs.getDouble("min_ton"));

					if (custCd == null && acctNbr == null && contractNbr == null) {
						tariffMainValueObject.setTariffTypeInd(ProcessChargeConst.TARIFF_TYPE_PUBLISH);
					} else {
						tariffMainValueObject.setTariffTypeInd(ProcessChargeConst.TARIFF_TYPE_CUSTOMIZE);
					}
				}

				// set the retrieved database values into the TariffTierValueObject
				tariffTierValueObject = new TariffTierVO();
				tariffTierValueObject.setRange(rs.getDouble("fr_range"), rs.getDouble("to_range"));
				tariffTierValueObject.setPerHour(rs.getDouble("hour_rate"));
				tariffTierValueObject.setPerUnit(rs.getDouble("other_rate"));
				tariffTierValueObject.setRate(rs.getDouble("amt_charge"));
				tariffTierValueObject.setPercentCharge(rs.getDouble("percent_charge"));
				tariffTierValueObject.setAdjustmentType(rs.getString("adj_type"));
				tariffTierValueObject.setAdjustment(rs.getDouble("adj_amt"));
				tariffTierValueObject.setPerHourType(rs.getString("hour_rate_type"));
				tariffTierValueObject.setPerUnitType(rs.getString("other_rate_type"));

				// add a TariffTierValueObject to the TariffMainValueObject
				tariffMainValueObject.addTier(tariffTierValueObject);
			}
			log.info("END: *** retrieveTariffMainTier Result *****" + tariffMainValueObject.toString());
		} catch (NullPointerException e) {
			log.error("Exception: retrieveTariffMainTier ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveTariffMainTier ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: retrieveTariffMainTier  DAO  END");
		}

		return tariffMainValueObject;
	}
	// EndRegion TariffMainJdbcRepository

}
