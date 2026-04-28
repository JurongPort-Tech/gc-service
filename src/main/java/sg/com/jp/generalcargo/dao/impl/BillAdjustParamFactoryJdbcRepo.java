package sg.com.jp.generalcargo.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.BillAdjustParamFactoryRepo;
import sg.com.jp.generalcargo.domain.BillAdjParamCOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamCTOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamCTVO;
import sg.com.jp.generalcargo.domain.BillAdjParamCVO;
import sg.com.jp.generalcargo.domain.BillAdjParamOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamTOVO;
import sg.com.jp.generalcargo.domain.BillAdjParamTVO;
import sg.com.jp.generalcargo.domain.BillAdjustParam;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("billAdjustParamFactoryRepo")
public class BillAdjustParamFactoryJdbcRepo implements BillAdjustParamFactoryRepo {
	private static final Log log = LogFactory.getLog(BillAdjustParamFactoryJdbcRepo.class);

	// valueObject.cab.billing -->BillAdjustParamFactory
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public BillAdjustParam create(String tariffCode) throws BusinessException {
		int ind[] = null;
		try {
			log.info("START: create DAO tariffCode: " + tariffCode);
			ind = getIndicator(tariffCode);
		} catch (NullPointerException e) {
			log.error("Exception: chkEdoCrgStatus ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getWHIndicator ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getWHIndicator  DAO  END");
		}
		return getParam(ind);
	}

	private static BillAdjustParam getParam(int ind[]) throws BusinessException {
		BillAdjustParam retVal = null;
		try {
			log.info("START: getParam DAO ind: " + ind);
			if (ind == null)
				return null;

			if (ind[0] == 1 && ind[1] == 0 && ind[2] == 0) {
				retVal = new BillAdjParamCVO();
			} else if (ind[0] == 0 && ind[1] == 1 && ind[2] == 0) {
				retVal = new BillAdjParamTVO();
			} else if (ind[0] == 0 && ind[1] == 0 && ind[2] == 1) {
				retVal = new BillAdjParamOVO();
			} else if (ind[0] == 1 && ind[1] == 1 && ind[2] == 0) {
				retVal = new BillAdjParamCTVO();
			} else if (ind[0] == 1 && ind[1] == 0 && ind[2] == 1) {
				retVal = new BillAdjParamCOVO();
			} else if (ind[0] == 0 && ind[1] == 1 && ind[2] == 1) {
				retVal = new BillAdjParamTOVO();
			} else if (ind[0] == 1 && ind[1] == 1 && ind[2] == 1) {
				retVal = new BillAdjParamCTOVO();
			} else {
				retVal = null;
			}
		} catch (Exception e) {
			log.error("Exception: getParam ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getParam  DAO  END");
		}
		return retVal;
	}

	public int[] getIndicator(String tariffCode) throws BusinessException {
		StringBuffer mainCat = new StringBuffer();
		StringBuffer subCat = new StringBuffer();
		StringBuffer cntrCat = new StringBuffer();
		StringBuffer bType = new StringBuffer();
		int[] ind = null;
		try {
			log.info("START: getIndicator DAO tariffCode: " + tariffCode);

			bType.append(tariffCode.charAt(0));
			mainCat.append(tariffCode.charAt(1));
			mainCat.append(tariffCode.charAt(2));
			subCat.append(tariffCode.charAt(3));
			subCat.append(tariffCode.charAt(4));

			// get the container category
			if (bType.toString().equals(ProcessChargeConst.CONTAINER_BUSINESS)) {
				cntrCat.setLength(0);
				cntrCat.append(tariffCode.charAt(6));
				cntrCat.append(tariffCode.charAt(7));
				if (!(cntrCat.toString()).equals("UC")) { // for uc cntr
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
					if (!(cntrCat.toString()).equals("UC")) { // for uc cntr
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
			ind = getIndicator(bType.toString(), mainCat.toString(), subCat.toString(), cntrCat.toString());
		} catch (BusinessException e) {
			log.error("Exception: getIndicator ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.error("Exception: getIndicator ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getIndicator DAO Result:" + ind);
		}
		return ind;
	}

	public int[] getIndicator(String businessType, String mainCategory, String subCategory, String containerCategory)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		int retVal[] = { -1, -1, -1 };
		SqlRowSet rs = null;
		try {
			log.info("START: getIndicator DAO businessType: " + businessType + ",mainCategory:" + mainCategory
					+ ",subCategory:" + subCategory + ",containerCategory:" + containerCategory);

			sb.append("SELECT cntr_unit, time_unit, other_unit FROM tariff_billable WHERE ");
			sb.append("business_type= :businessType AND main_cat_cd= :mainCategory AND ");
			sb.append("sub_cat_cd= :subCategory AND cntr_cat_cd= :containerCategory ");
			String sql = sb.toString();

			paramMap.put("businessType", businessType);
			paramMap.put("mainCategory", mainCategory);
			paramMap.put("subCategory", subCategory);
			paramMap.put("containerCategory", containerCategory);

			log.info(" *** getIndicator SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			String s[] = { "", "", "" };
			if (rs != null && rs.next()) {
				s[0] = rs.getString("cntr_unit");
				s[1] = rs.getString("time_unit");
				s[2] = rs.getString("other_unit");

				rs = null;
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
		} catch (NullPointerException e) {
			log.error("Exception: getIndicator ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getIndicator ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getIndicator  DAO  Result:" + retVal);
		}
		return retVal;
	}

}
