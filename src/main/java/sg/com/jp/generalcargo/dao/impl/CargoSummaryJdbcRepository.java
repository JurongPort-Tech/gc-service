package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.CargoSummaryRepository;
import sg.com.jp.generalcargo.domain.CargoSummaryValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("cargoSummaryRepository")
public class CargoSummaryJdbcRepository implements CargoSummaryRepository {

	private static final Log log = LogFactory.getLog(CargoSummaryJdbcRepository.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private static final String param = "  paramMap = ";
	
	// ejb.sessionBeans.gbms.cargo.cargosummary-->CargoSummary
	@Override
	public TableResult getCargoSummaryList(String strCustCode, Criteria criteria) throws BusinessException {
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		TableResult tableResult = new TableResult();
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		String sql = "";
		SqlRowSet rs = null;

		if (strCustCode.equalsIgnoreCase("JP")) {

			sb.append("SELECT ");
			sb.append("	DISTINCT (A.VV_CD), ");
			sb.append("	A.VSL_NM, ");
			sb.append("	A.IN_VOY_NBR, ");
			sb.append("	B.ETB_DTTM, ");
			sb.append("	B.ETU_DTTM, ");
			sb.append("	SUM(M.NBR_PKGS), ");
			sb.append("	SUM(M.EDO_NBR_PKGS), ");
			sb.append("	A.TERMINAL ");
			sb.append("FROM ");
			sb.append("	VESSEL_CALL A, ");
			sb.append("	BERTHING B, ");
			sb.append("	MANIFEST_DETAILS M ");
			sb.append("WHERE ");
			sb.append("	B.SHIFT_IND = '1' ");
			sb.append("	AND A.GB_CLOSE_BJ_IND != 'Y' ");
			sb.append("	AND A.VV_CD = B.VV_CD ");
			sb.append("	AND ((TERMINAL IN 'CT' ");
			sb.append("	AND COMBI_GC_OPS_IND IN('Y', NULL)) ");
			sb.append("	OR TERMINAL NOT IN 'CT') ");
			sb.append("	AND A.VV_CD = M.VAR_NBR ");
			sb.append("	AND M.BL_STATUS = 'A' ");
			sb.append("GROUP BY ");
			sb.append("	A.VV_CD, ");
			sb.append("	A.VSL_NM, ");
			sb.append("	A.IN_VOY_NBR, ");
			sb.append("	A.TERMINAL, ");
			sb.append("	B.ETB_DTTM, ");
			sb.append("	B.ETU_DTTM ");
			sb.append("ORDER BY ");
			sb.append("	A.TERMINAL DESC, ");
			sb.append("	A.VSL_NM");

			sql = sb.toString();

		} else {
			sb = new StringBuffer();
			sb.append("SELECT ");
			sb.append("	DISTINCT (A.VV_CD), ");
			sb.append("	A.VSL_NM, ");
			sb.append("	A.IN_VOY_NBR, ");
			sb.append("	B.ETB_DTTM, ");
			sb.append("	B.ETU_DTTM, ");
			sb.append("	SUM(M.NBR_PKGS), ");
			sb.append("	SUM(M.EDO_NBR_PKGS), ");
			sb.append("	A.TERMINAL ");
			sb.append("FROM ");
			sb.append("	VESSEL_CALL A, ");
			sb.append("	BERTHING B, ");
			sb.append("	MANIFEST_DETAILS M ");
			sb.append("WHERE ");
			sb.append("	B.SHIFT_IND = '1' ");
			sb.append("	AND A.GB_CLOSE_BJ_IND != 'Y' ");
			sb.append("	AND A.VV_CD = B.VV_CD ");
			sb.append("	AND ((TERMINAL IN 'CT' ");
			sb.append("	AND COMBI_GC_OPS_IND IN('Y', NULL)) ");
			sb.append("	OR TERMINAL NOT IN 'CT') ");
			sb.append("	AND A.CREATE_CUST_CD = :strCustCode ");
			sb.append("	AND A.VV_CD = M.VAR_NBR ");
			sb.append("	AND M.BL_STATUS = 'A' ");
			sb.append("GROUP BY ");
			sb.append("	A.VV_CD, ");
			sb.append("	A.VSL_NM, ");
			sb.append("	A.IN_VOY_NBR, ");
			sb.append("	A.TERMINAL, ");
			sb.append("	B.ETB_DTTM, ");
			sb.append("	B.ETU_DTTM ");
			sb.append("ORDER BY ");
			sb.append("	A.TERMINAL DESC, ");
			sb.append("	A.VSL_NM");

			sql = sb.toString();
		}
		List<CargoSummaryValueObject> cargosummarylist = new ArrayList<CargoSummaryValueObject>();
		try {

			log.info("START: getCargoSummaryList  DAO  Start strCustCode" + strCustCode + "criteria: " + criteria.toString());

			if (!strCustCode.equalsIgnoreCase("JP")) {
				paramMap.put("strCustCode", strCustCode);
			}

			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						paramMap, Integer.class));
			}

			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());

			}
			
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			log.info(" getCargoSummaryList  DAO  SQL " + sql + param + paramMap);

			while (rs.next()) {
				CargoSummaryValueObject cargoSummaryValueObject = new CargoSummaryValueObject();
				String vvcd = CommonUtility.deNull(rs.getString(1));
				String vslnm = CommonUtility.deNull(rs.getString(2));
				String invoynbr = CommonUtility.deNull(rs.getString(3));
				String etbdttm = CommonUtility.deNull(rs.getString(4));
				String etudttm = CommonUtility.deNull(rs.getString(5));
				String mftnbrpkgs = CommonUtility.deNull(rs.getString(6));
				String edonbrpkgs = CommonUtility.deNull(rs.getString(7));
				String terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				cargoSummaryValueObject.setVarNbr(vvcd);
				cargoSummaryValueObject.setVslNm(vslnm);
				cargoSummaryValueObject.setInVoyNbr(invoynbr);
				cargoSummaryValueObject.setEtbDttm(etbdttm);
				cargoSummaryValueObject.setEtuDttm(etudttm);
				cargoSummaryValueObject.setMftNbrPkgs(mftnbrpkgs);
				cargoSummaryValueObject.setEdoNbrPkgs(edonbrpkgs);
				cargoSummaryValueObject.setTerminal(terminal);
				topsModel.put(cargoSummaryValueObject);
			}
			log.info(" getCargoSummaryList  DAO  Result" + cargosummarylist.toString());
		} catch (NullPointerException e) {
			log.info("Exception getCargoSummaryList : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCargoSummaryList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoSummaryList  DAO  END");
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
		}
		return tableResult;
	}
	
	// added by syazwani on 21/05/2021
	@Override
	public String getCompanyName(String coCd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		String companyName = "";
		try {
			log.info("START: getCompanyName coCd:" + coCd);
			sb.append(" SELECT CO_NM AS coNm FROM tops.COMPANY_CODE WHERE CO_CD LIKE :coCd ");
			paramMap.put("coCd", coCd);
			
			companyName = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, String.class);
			log.info("SQL" + sb.toString()  + param + paramMap);
		} catch (Exception e) {
			log.info("Exception getCompanyName : ", e);
		} finally {
			log.info("END: DAO getCompanyName");
		}
		return companyName;
	}

}
