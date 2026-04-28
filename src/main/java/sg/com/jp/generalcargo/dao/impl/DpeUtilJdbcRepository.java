package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.DpeUtilRepository;
import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.util.BusinessException;

@Repository("DpeUtilRepository")
public class DpeUtilJdbcRepository implements DpeUtilRepository {

	private static final Log log = LogFactory.getLog(DpeUtilJdbcRepository.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// jp.src.sg.com.jp.dpe.dao--->DpeUtilJdbcDao-->listVesselByName()
	@Override
	public List<DPEUtil> listVesselByName(Integer start, Integer limit, String name, String coCd)
			throws BusinessException {
		String sql = "SELECT DISTINCT VSL_NM FROM VESSEL_CALL WHERE VV_STATUS_IND NOT IN ('CX') AND LOWER(VSL_NM) LIKE :vsl_nm";
		Map<String, String> params = new HashMap<String, String>();
		List<DPEUtil> listVesselByName = new ArrayList<DPEUtil>();
		try {
			log.info("START listVesselByName DAO : " + " Start: " + start + " limit: " + limit + " name: " + name
					+ " coCd: " + coCd);
			if (!"JP".equalsIgnoreCase(coCd)) {
				sql = sql + " AND (VSL_OPR_CD = :coCd OR CREATE_CUST_CD = :coCd)";
				params.put("coCd", coCd);
			}
			params.put("vsl_nm", name.toLowerCase() + "%");
			sql += " ORDER BY VSL_NM ASC";
			if (start >= 0 && limit > 0) {
//					sql = getPaginationString(sql, params, start, limit);
			}
			log.info("listVesselByName SQL " + sql.toString() + ", paramMap = " + params.toString());
			listVesselByName = namedParameterJdbcTemplate.query(sql, params,
					new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			log.info("listVesselByName Result: " + listVesselByName.toString());
		} catch (Exception e) {
			log.info("Exception listVesselByName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END listVesselByName DAO");
		}
		return listVesselByName;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeUtilJdbcDao-->countVesselByName()
	@Override
	public int countVesselByName(String name, String coCd) throws BusinessException {
		String sql = ("SELECT COUNT(DISTINCT VSL_NM) FROM VESSEL_CALL WHERE VV_STATUS_IND NOT IN ('CX') AND LOWER(VSL_NM) LIKE :vsl_nm");
		Map<String, String> params = new HashMap<String, String>();
		int count = 0;
		try {
			log.info("START countVesselByName DAO : cocd = " + coCd + " name = " + name);
			if (!"JP".equalsIgnoreCase(coCd)) {
				sql = sql + " AND (VSL_OPR_CD = :coCd OR CREATE_CUST_CD = :coCd)";
				params.put("coCd", coCd);
			}
			params.put("vsl_nm", name.toLowerCase() + "%");
			log.info("countVesselByName SQL " + sql.toString() + ", paramMap = " + params.toString());
			count = namedParameterJdbcTemplate.queryForObject(sql.toString(), params, Integer.class);
			log.info("countVesselByName Result: " + count);
		} catch (Exception e) {
			log.info("Exception countVesselByName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END countVesselByName DAO");
		}
		return count;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeUtilJdbcDao-->getInVoyageList()
	@Override
	public List<DPEUtil> getInVoyageList(String name, String coCd, String voyNbr, String ind) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> params = new HashMap<String, String>();
		List<DPEUtil> inVoyageList = new ArrayList<DPEUtil>();
		try {
			log.info("START getInVoyageList DAO: " + " name: " + name + " coCd: " + coCd + " voyNbr: " + voyNbr
					+ " ind: " + ind);
			sb.append(" SELECT VV_CD, IN_VOY_NBR FROM VESSEL_CALL ");
			sb.append(" WHERE VV_STATUS_IND NOT IN ('CX') AND VSL_NM = UPPER(:vsl_nm) ");
			sb.append(" AND UPPER(IN_VOY_NBR) LIKE :voy_nbr ");
			if (StringUtils.isBlank(ind) && !"JP".equalsIgnoreCase(coCd)) {
				sb.append(" AND (VSL_OPR_CD = :coCd OR CREATE_CUST_CD = :coCd)");
				params.put("coCd", coCd);
			}
			sb.append(" ORDER BY IN_VOY_NBR ASC ");
			params.put("vsl_nm", name.toUpperCase());
			params.put("voy_nbr", (voyNbr.toUpperCase()) + "%");
			log.info("getInVoyageList SQL " + sb.toString() + ", paramMap = " + params.toString());
			inVoyageList = namedParameterJdbcTemplate.query(sb.toString(), params,
					new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			log.info(" getInVoyageList Result: " + inVoyageList.toString());
		} catch (Exception e) {
			log.info("Exception getInVoyageList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getInVoyageList DAO END");
		}
		return inVoyageList;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeUtilJdbcDao-->getOutVoyageList()
	@Override
	public List<DPEUtil> getOutVoyageList(String name, String coCd, String voyNbr, String ind)
			throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> params = new HashMap<String, String>();
		List<DPEUtil> outVoyageList = new ArrayList<DPEUtil>();
		try {
			log.info("START getOutVoyageList DAO: name = " + name + ", cocd = " + coCd + ", voyNbr = " + voyNbr
					+ ", ind = " + ind);
			sb.append(" SELECT VV_CD, OUT_VOY_NBR FROM VESSEL_CALL ");
			sb.append(" WHERE VV_STATUS_IND NOT IN ('CX') AND VSL_NM = UPPER(:vsl_nm) ");
			sb.append(" AND UPPER(OUT_VOY_NBR) LIKE :voy_nbr ");
			if (StringUtils.isBlank(ind) && !"JP".equalsIgnoreCase(coCd)) {
				sb.append(" AND (VSL_OPR_CD = :coCd OR CREATE_CUST_CD = :coCd)");
				params.put("coCd", coCd);
			}
			sb.append(" ORDER BY OUT_VOY_NBR ASC ");
			params.put("vsl_nm", name.toUpperCase());
			params.put("voy_nbr", (voyNbr.toUpperCase()) + "%");
			log.info("getOutVoyageList SQL " + sb.toString() + ", paramMap = " + params.toString());
			outVoyageList = namedParameterJdbcTemplate.query(sb.toString(), params,
					new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			log.info("getOutVoyageList Result: " + outVoyageList.toString());
		} catch (Exception e) {
			log.info("Exception getOutVoyageList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getOutVoyageList DAO");
		}
		return outVoyageList;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeUtilJdbcDao-->getVesselDetail()
	@Override
	public DPEUtil getVesselDetail(String vv_cd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> params = new HashMap<String, String>();
		DPEUtil dpe = new DPEUtil();
		List<DPEUtil> tmp = null;
		try {
			log.info("START getVesselDetail DAO vvcd =" + vv_cd);
			sb.append(" SELECT A.VSL_NM, A.IN_VOY_NBR, A.OUT_VOY_NBR, A.VV_CD, CC.CO_NM");
			sb.append(" FROM VESSEL_CALL A LEFT JOIN COMPANY_CODE CC ON A.VSL_OPR_CD = CC.CO_CD");
			sb.append(" WHERE A.VV_CD = (:vv_cd) ");
			params.put("vv_cd", vv_cd);
			log.info("getVesselDetail SQL " + sb.toString() + ", paramMap = " + params.toString());
			tmp = namedParameterJdbcTemplate.query(sb.toString(), params,
					new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			log.info("getVesselDetail Result: " + tmp.toString());
			if (tmp != null) {
				log.info("tmp size = " + tmp.size());
				if (tmp.size() > 0) {
					dpe = (DPEUtil) tmp.get(0);
				} else {
					dpe = new DPEUtil();
				}
			}
		} catch (Exception e) {
			log.info("Exception getVesselDetail : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getVesselDetail DAO");
		}
		return dpe;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeUtilJdbcDao-->listCompanyByName()
	@Override
	public List<DPEUtil> listCompanyByName(Integer start, Integer limit, String name) throws BusinessException {
		String sql = "";
		Map<String, String> params = new HashMap<String, String>();
		List<DPEUtil> dpe = new ArrayList<DPEUtil>();
		try {
			log.info(
					"***** Start listCompanyByName DAO :" + " Start: " + start + " limit: " + limit + " name: " + name);
			sql = "SELECT CO_CD AS CO_CD, CO_NM AS CO_NM FROM COMPANY_CODE WHERE LOWER(CO_NM) LIKE :co_nm AND REC_STATUS = 'A'";
			params.put("co_nm", name.toLowerCase() + "%");
			sql += " ORDER BY CO_NM ASC";

			log.info("listCompanyByName Sql: " + sql + " paramMap: " + params.toString());
			if (start >= 0 && limit > 0) {
//				sql = getPaginationString(sql, params, start, limit);
			}
			dpe = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			log.info("listCompanyByName Result: " + dpe.toString());
		} catch (Exception e) {
			log.info("Exception listCompanyByName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END listCompanyByName DAO");
		}
		return dpe;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeUtilJdbcDao-->countCompanyByName()
	@Override
	public int countCompanyByName(String name) throws BusinessException {
		String sql = ("SELECT COUNT(CO_CD) FROM COMPANY_CODE WHERE LOWER(CO_NM) LIKE :co_nm AND REC_STATUS = 'A'");
		Map<String, String> params = new HashMap<String, String>();
		int count = 0;
		try {
			log.info("START countCompanyByName DAO: " + " name " + name);
			params.put("co_nm", name.toLowerCase() + "%");
			log.info("countCompanyByName Sql: " + sql + " paramMap: " + params.toString());
			count = namedParameterJdbcTemplate.queryForObject(sql.toString(), params, Integer.class);
			log.info("countCompanyByName Result: " + count);
		} catch (Exception e) {
			log.info("Exception listCompanyByName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END countCompanyByName DAO");
		}
		return count;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeUtilJdbcDao-->countHaulierCompanyByName()
	public int countHaulierCompanyByName(String name) throws BusinessException {
		String sql = "";
		Map<String, String> params = new HashMap<String, String>();
		int countHaulier = 0;
		try {
			log.info("START countHaulierCompanyByName DAO: name = " + name);
			sql = " SELECT COUNT(CO_CD) FROM COMPANY_CODE WHERE REC_STATUS = 'A' AND LOB_CD = 'HAU' AND LOWER(CO_NM) LIKE :co_nm";
			params.put("co_nm", name.toLowerCase() + "%");
			log.info("countHaulierCompanyByName SQL " + sql.toString() + ", paramMap = " + params.toString());
			countHaulier = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
			log.info("countHaulierCompanyByName Result: " + countHaulier);
		} catch (Exception e) {
			log.info("Exception countHaulierCompanyByName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END countHaulierCompanyByName DAO");
		}
		return countHaulier;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeUtilJdbcDao-->listHaulierCompanyByName()
	public List<DPEUtil> listHaulierCompanyByName(Integer start, Integer limit, String name) throws BusinessException {
		String sql = "";
		Map<String, String> params = new HashMap<String, String>();
		List<DPEUtil> haulierList = new ArrayList<DPEUtil>();
		try {
			log.info(
					"START listHaulierCompanyByName DAO: start = " + start + ", limit = " + limit + ", name = " + name);
			sql = " SELECT CO_CD, CO_NM FROM COMPANY_CODE WHERE REC_STATUS = 'A' AND LOB_CD = 'HAU' AND LOWER(CO_NM) LIKE :co_nm ORDER BY CO_NM ASC";
			params.put("co_nm", name.toLowerCase() + "%");
			log.info("listHaulierCompanyByName SQL " + sql.toString() + ", paramMap = " + params.toString());
			haulierList = namedParameterJdbcTemplate.query(sql, params,
					new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			log.info("listHaulierCompanyByName Result: " + haulierList.toString());
		} catch (Exception e) {
			log.info("Exception listHaulierCompanyByName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END listHaulierCompanyByName DAO");
		}
		return haulierList;
	}

	// method : sg.com.jp.dpe.dao.DpeUtilJdbcDao-->listVesselByNameForMonitoring
	@Override
	public List<DPEUtil> listVesselByNameForMonitoring(String name, String coCd) throws BusinessException {
		String sql = "";
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		List<DPEUtil> listVesselByNameForMonitoring = new ArrayList<DPEUtil>();
		try {
			log.info("START: listVesselByNameForMonitoring start:name: " + name + ",coCd: " + coCd);
			sb.append(" SELECT VV_CD, (VSL_NM || '/' || OUT_VOY_NBR) VSL_NM ");
			sb.append(" FROM VESSEL_CALL WHERE ");
//								sb.append(" VV_STATUS_IND NOT IN ('CX') AND ");
			sb.append(" VV_STATUS_IND NOT IN ('CL') AND ");
			sb.append(" (LOWER(VSL_NM) || '/' || LOWER(OUT_VOY_NBR)) LIKE :vsl_nm ");
			if (!"JP".equalsIgnoreCase(coCd)) {
				sb.append(" AND (VSL_OPR_CD = :coCd OR CREATE_CUST_CD = :coCd) ");
				paramMap.put("coCd", coCd);
			}
			paramMap.put("vsl_nm", name.toLowerCase() + "%");
			sb.append(" ORDER BY VSL_NM ASC ");
			sql = sb.toString();

			log.info("listVesselByNameForMonitoring SQL" + sql + ",ParamMap" + paramMap.toString());
			listVesselByNameForMonitoring = namedParameterJdbcTemplate.query(sql, paramMap,
					new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			log.info("listVesselByNameForMonitoring Result" + listVesselByNameForMonitoring.toString());
		} catch (Exception e) {
			log.info("Exception listVesselByNameForMonitoring : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO listVesselByNameForMonitoring");
		}
		return listVesselByNameForMonitoring;
	}

	// method : sg.com.jp.dpe.dao.DpeUtilJdbcDao-->countVesselByNameForMonitoring
	@Override
	public int countVesselByNameForMonitoring(String name, String coCd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		int count = 0;
		try {
			log.info("START: countVesselByNameForMonitoring name: " + name + "coCd: " + coCd);
			sb.append(" SELECT COUNT(VV_CD) FROM VESSEL_CALL ");
			sb.append(" WHERE VV_STATUS_IND NOT IN ('CX') AND (LOWER(VSL_NM) ");
			sb.append(" || '/' || LOWER(OUT_VOY_NBR)) LIKE :vsl_nm ");
			if (!"JP".equalsIgnoreCase(coCd)) {
				sb.append(" AND (VSL_OPR_CD = :coCd OR CREATE_CUST_CD = :coCd) ");
				paramMap.put("coCd", coCd);
			}
			paramMap.put("vsl_nm", name.toLowerCase() + "%");
			log.info("countVesselByNameForMonitoring SQL" + sb.toString() + ",ParamMap" + paramMap.toString());
			count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
			log.info("countVesselByNameForMonitoring result: " + count);
		} catch (Exception e) {
			log.info("Exception countVesselByNameForMonitoring : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO countVesselByNameForMonitoring");
		}
		return count;
	}

	// sg.com.jp.dpe.action -->DPEUtilAction -->DpeUtilJdbcDao
	// -->getVesselListForAddTransferOfCargo()
	@Override
	public List<DPEUtil> listVesselForAddTransferCargo(Integer start, Integer limit, String name, String coCd)
			throws BusinessException {

		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<>();
		List<DPEUtil> listVessel = null;
		try {
			log.info("START:  *** listVesselForAddTransferCargo Dao Start criteria : *** " + start + limit + name
					+ coCd);

			sb.append(" SELECT DISTINCT VC.VSL_NM");
			sb.append(" FROM VESSEL_CALL VC  ");
			sb.append(" WHERE VC.VV_STATUS_IND NOT IN ('CX', 'UB', 'CL') ");
			sb.append("AND LOWER(VC.VSL_NM) LIKE :vsl_nm ");
			sb.append(" ORDER BY VSL_NM ASC");
			log.info(
					"*** listVesselForAddTransferCargo SQL *****" + sb.toString() + " paramMap " + paramMap.toString());

			paramMap.put("vsl_nm", name.toLowerCase() + "%");
			listVessel = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));

			if (start >= 0 && limit > 0) {
				// sql = getPaginationString(sql, params, start, limit);
			}
			log.info("END: *** listVesselForAddTransferCargo  *****" + listVessel.toString());
		} catch (Exception e) {
			log.info("Exception listVesselForAddTransferCargo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: listVesselForAddTransferCargo  DAO  END");
		}
		return listVessel;
	}

	// sg.com.jp.dpe.action -->DPEUtilAction -->DpeUtilJdbcDao
	// -->getOutVoyageList4Transfer()
	@Override
	public List<DPEUtil> getOutVoyageList4Transfer(String name, String coCd, String voyNbr, String ind)
			throws BusinessException {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<DPEUtil> listVessel = null;
		try {
			log.info("START: DAO getOutVoyageList4Transfer name:" + name + "coCd:" + coCd + "voyNbr:" + voyNbr + "ind:"
					+ ind);
			sb.append(" SELECT VV_CD, OUT_VOY_NBR FROM VESSEL_CALL ");
			sb.append(" WHERE VV_STATUS_IND NOT IN ('CX') AND VSL_NM = UPPER(:vsl_nm) ");
			sb.append(" AND UPPER(OUT_VOY_NBR) LIKE :voy_nbr ");
			if (StringUtils.isBlank(ind) && !"JP".equalsIgnoreCase(coCd)) {
				sb.append(" AND (VSL_OPR_CD = :coCd OR CREATE_CUST_CD = :coCd)");
				params.put("coCd", coCd);
			}
			sb.append(" AND VV_STATUS_IND NOT IN ('CL','UB') ");
			// Start #35052 : Add close shipment status - NS OCT 2023
			sb.append(" AND NVL(GB_CLOSE_SHP_IND,'N') <> 'Y' ");
			// End #35052 : Add close shipment status - NS OCT 2023
			sb.append(" ORDER BY OUT_VOY_NBR ASC ");
			params.put("vsl_nm", name.toUpperCase());
			params.put("voy_nbr", StringUtils.upperCase(voyNbr) + "%");
			log.info("getOutVoyageList4Transfer SQL" + sb.toString() + ", params:" + params.toString());
			listVessel = namedParameterJdbcTemplate.query(sb.toString(), params,
					new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			log.info("listVessel" + listVessel.toString());
		} catch (Exception e) {
			log.info("Exception END:getOutVoyageList4Transfer : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:getOutVoyageList4Transfer ");
		}
		return listVessel;

	}

}
