package sg.com.jp.generalcargo.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.BillSupportInfoRepository;
import sg.com.jp.generalcargo.dao.TariffVersionRepository;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("BillSupportInfoRepository")
public class BillSupportInfoJdbcRepository implements BillSupportInfoRepository {

	private static final Log log = LogFactory.getLog(BillSupportInfoJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	@Autowired
	TariffVersionRepository tariffVerRepo;
	// StartRegion BillSupportInfoJdbcRepository

	// jp.src.ejb.sessionBeans.cab.tariff--->TariffVersionEJB--->getCurrentVersion()
	public int[] getIndicator(String tariffCode) throws BusinessException {
		StringBuffer mainCat = new StringBuffer();
		StringBuffer subCat = new StringBuffer();
		StringBuffer cntrCat = new StringBuffer();
		StringBuffer bType = new StringBuffer();
		try {
			log.info("START getIndicator(String tariffCode) DAO :: tariffCode: " + CommonUtility.deNull(tariffCode));
			log.info("---Get Indicator (TariffCode)---");
			log.info("Tariff : " + tariffCode);

			// 0 12 34 56789 0123
			// C SV CN 2MTTS 0457
			// G WF GL NLL00 1236
			// G SC GL L2TSG 1111

			bType.append(tariffCode.charAt(0));
			mainCat.append(tariffCode.charAt(1));
			mainCat.append(tariffCode.charAt(2));
			subCat.append(tariffCode.charAt(3));
			subCat.append(tariffCode.charAt(4));

			log.info("bType   : " + bType.toString());
			log.info("mainCat : " + mainCat.toString());
			log.info("subCat  : " + subCat.toString());

			// get the container category
			if (bType.toString().equals(ProcessChargeConst.CONTAINER_BUSINESS)) {
				cntrCat.setLength(0);
				cntrCat.append(tariffCode.charAt(6));
				cntrCat.append(tariffCode.charAt(7));
				if (!cntrCat.equals("UC")) { // for uc cntr
					cntrCat.setLength(0);
					cntrCat.append('~');
				}
			} else if (bType.toString().equals(ProcessChargeConst.GENERAL_BUSINESS)
					|| bType.toString().equals(ProcessChargeConst.BULK_BUSINESS)) {
				if (mainCat.toString().equals(ProcessChargeConst.TARIFF_MAIN_GB_CONTAINER_SERVICE_CHARGE)
						|| mainCat.toString().equals(ProcessChargeConst.TARIFF_MAIN_GB_CONTAINER_STORE_RENT)) {
					// follow CT for containerised cargo
					cntrCat.setLength(0);
					cntrCat.append(tariffCode.charAt(6));
					cntrCat.append(tariffCode.charAt(7));
					if (!cntrCat.equals("UC")) { // for uc cntr
						cntrCat.setLength(0);
						cntrCat.append('~');
					}
				} else {
					cntrCat.setLength(0);
					cntrCat.append('~');
				}

			} else {
				cntrCat.append('~');
			}
			log.info("cntrCat : " + cntrCat);
		} catch (Exception e) {
			log.info("Exception getIndicator : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getIndicator(String tariffCode) DAO");
		}
		return getIndicator(bType.toString(), mainCat.toString(), subCat.toString(), cntrCat.toString());
	}

	public int[] getIndicator(String businessType, String mainCategory, String subCategory, String containerCategory)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String sql = "";
	
		int retVal[] = { -1, -1, -1 };
		try {
			log.info("START getIndicator DAO :: businessType: " + CommonUtility.deNull(businessType) + " mainCategory: " + CommonUtility.deNull(mainCategory)
					+ " subCategory: " + CommonUtility.deNull(subCategory) + " containerCategory: " + CommonUtility.deNull(containerCategory));
			log.info("---Get Indicator---");
			log.info("Business :\t" + businessType);
			log.info("MainCat  :\t" + mainCategory);
			log.info("SubCat   :\t" + subCategory);
			log.info("CntrCat  :\t" + containerCategory);
			sql = "SELECT cntr_unit, time_unit, other_unit FROM tariff_billable WHERE business_type=:businessType AND main_cat_cd=:mainCategory AND sub_cat_cd=:subCategory AND cntr_cat_cd=:containerCategory ";
		
			
			paramMap.put("businessType", businessType);
			paramMap.put("mainCategory", mainCategory);
			paramMap.put("subCategory", subCategory);
			paramMap.put("containerCategory", containerCategory);
			log.info(" ***SQL *****" + sql.toString());
			log.info(" ***paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			String s[] = { "", "", "" };
			if (rs != null && rs.next()) {
				s[0] = rs.getString("cntr_unit");
				s[1] = rs.getString("time_unit");
				s[2] = rs.getString("other_unit");
			}

			for (int i = 0; i < s.length; i++) {
				if (s[i] != null) {
					if (s[i].equals("Y")) {
						retVal[i] = 1;
					} else if (s[i].equals("N")) {
						retVal[i] = 0;
					} else {
						retVal[i] = -1;
					}
				} else {
					retVal[i] = -1;
				}
			}
			/*
			 * log.info("retVal :\t" + businessType + "," + mainCategory + ", " +
			 * subCategory + ", " + containerCategory + " : " + retVal[0] + ", " + retVal[1]
			 * + ", " + retVal[2]); log.info("---Done---"); //
			 */
	
		} catch (Exception e) {
			log.info("Exception getIndicator : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getIndicator DAO  retVal: " + retVal);
			
		}
		return retVal;
	}
	// EndRegion TariffVersionJdbcRepository

}
