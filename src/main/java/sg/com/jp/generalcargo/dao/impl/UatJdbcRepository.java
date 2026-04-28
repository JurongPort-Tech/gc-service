package sg.com.jp.generalcargo.dao.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import sg.com.jp.generalcargo.dao.UatRepository;
import sg.com.jp.generalcargo.domain.MiscCodeValueObject;
import sg.com.jp.generalcargo.domain.UatFormValueObject;
import sg.com.jp.generalcargo.domain.UatValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

@Repository("UatRepository")
public class UatJdbcRepository implements UatRepository {

	private static final Log log = LogFactory.getLog(UatJdbcRepository.class);
	private static final String param = " paramMap = ";
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// jp.src.ejb.sessionBeans.gbms.ops.tenant.uat-->UatEJB-->getTenantCompanyList()
	
	@Override
	public List<MiscCodeValueObject> getTenantCompanyList() throws BusinessException {
		List<MiscCodeValueObject> tenantCompanyList = new ArrayList<MiscCodeValueObject>();
		MiscCodeValueObject miscCodeVO = null;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getTenantCompanyList  DAO  Start ");
			
			sb.append(" select * from MISC_TYPE_CODE where CAT_CD = 'TENANT' ");
			sb.append(" and rec_status = 'A' ORDER BY misc_type_nm ");

			log.info(" ***getTenantCompanyList SQL *****" + sb.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				miscCodeVO = new MiscCodeValueObject();
				miscCodeVO.setCatCode(rs.getString("cat_cd"));
				miscCodeVO.setTypeCode(rs.getString("misc_type_cd"));
				miscCodeVO.setTypeName(rs.getString("misc_type_nm"));
				miscCodeVO.setStatus(rs.getString("rec_status"));
				tenantCompanyList.add(miscCodeVO);
			}
			log.info("END: *** getTenantCompanyList Result *****" + tenantCompanyList.toString());
		} catch (NullPointerException e) {
			log.info("Exception getTenantCompanyList :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getTenantCompanyList :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTenantCompanyList  DAO  END");
		}
		return tenantCompanyList;
	}

	// jp.src.ejb.sessionBeans.gbms.ops.tenant.uat-->UatEJB-->getDriverName()
	@Override
	public String getDriverName(String driverPass) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		String driverName = null;
		if (driverPass != null) {
			driverPass = driverPass.trim().toUpperCase();

			try {
				log.info("START: getDriverName  DAO  Start driverPass:" + driverPass);
			
				sb.append(" select * from JC_CARDDTL where ID_NO =:driverPass ");

				paramMap.put("driverPass", driverPass);
				log.info(" ***getDriverName SQL *****" + sb.toString());
				log.info(param + paramMap);

				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

				if (rs.next()) {
					driverName = rs.getString("CARDHLDR_NAME");
					log.info("END: *** getDriverName Result *****" + driverName.toString());
				}
			} catch (NullPointerException e) {
				log.info("Exception getDriverName :" , e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception getDriverName :" , e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: getDriverName  DAO  END");
			}
		}

		return driverName;
	}

	// jp.src.ejb.sessionBeans.gbms.ops.tenant.uat-->UatEJB-->searchUat()
	/**
	 * Search Uat
	 * 
	 * @param uatFormVO
	 * @return list of results
	 */
	@Override
	public UatFormValueObject searchUat(UatFormValueObject uatFormVO) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		UatValueObject rsUatVO = null;

		try {
			log.info("START: searchUat  DAO  Start uatFormVO:" + uatFormVO.toString());

			String srchUatNo = uatFormVO.getUatValueObject().getSearchUatNo();
			String srchDateFrom = uatFormVO.getUatValueObject().getSearchDateFrom();
			String srchDateTo = uatFormVO.getUatValueObject().getSearchDateTo();
			String srchCompanyCode = uatFormVO.getUatValueObject().getSearchCompanyCode();
			boolean srchActive = uatFormVO.getUatValueObject().isSearchActive();
			boolean flagCompany = false;
			if (srchUatNo == null || srchUatNo.equals("")) {
				boolean flagDate = false;

				sb.append(" SELECT TO_CHAR(create_dttm, 'DDMMYYYY HH24MI') create_dttm, ");
				sb.append(" create_dttm create_dttm1, uat_id, truck_nbr, ");
				sb.append(" TO_CHAR(gate_out_dttm, 'DDMMYYYY HH24MI') gate_out_dttm, ");
				sb.append(" uat_status, pkgs_nbr, crg_desc FROM tenant_ua WHERE ");
				if (srchCompanyCode != null && !srchCompanyCode.equals("")) {
					sb.append(" tenant_co_cd =:srchCompanyCode ");
					flagCompany = true;
				}

				if (srchDateFrom != null && !srchDateFrom.equals("") && srchDateFrom != null
						&& !srchDateFrom.equals("")) {
					if (flagCompany) {
						sb.append(" and ");
					}
					sb.append(" TO_DATE(TO_CHAR(create_dttm,'ddmmyyyy'),'ddmmyyyy') >= ");
					sb.append(" TO_DATE(:srchDateFrom,'DDMMYYYY') and TO_DATE(TO_CHAR(create_dttm, ");
					sb.append(" 'ddmmyyyy'),'ddmmyyyy') <= TO_DATE(:srchDateTo,'DDMMYYYY') ");
					flagDate = true;
				}

				if (srchActive) {
					sb.append(" AND uat_status = 'A' AND gate_out_dttm is null ");
				}

				sb.append(" ORDER BY create_dttm1 DESC ");
				log.info(" ***searchUat SQL *****" + sb.toString());
				if (flagCompany) {
					paramMap.put("srchCompanyCode", srchCompanyCode);
					if (flagDate) {
						paramMap.put("srchDateFrom", srchDateFrom);
						paramMap.put("srchDateTo", srchDateTo);
					}
				} else {
					paramMap.put("srchDateFrom", srchDateFrom);
					paramMap.put("srchDateTo", srchDateTo);
				}

			}

			else {
				sb.append(" SELECT TO_CHAR(create_dttm, 'DDMMYYYY HH24MI') create_dttm, ");
				sb.append(" create_dttm create_dttm1,uat_id, truck_nbr, ");
				sb.append(" TO_CHAR(gate_out_dttm, 'DDMMYYYY HH24MI') gate_out_dttm, ");
				sb.append(" uat_status, pkgs_nbr, ");
				sb.append(" crg_desc FROM tenant_ua WHERE uat_id =:srchUatNo ");
				if (srchActive) {
					sb.append(" AND uat_status = 'A' ");
				}

				if (srchCompanyCode != null && !srchCompanyCode.equals("")) {
					sb.append(" AND tenant_co_cd =:srchCompanyCode ");
					flagCompany = true;
				}

				sb.append(" ORDER BY create_dttm1 DESC ");
				
				log.info(" ***searchUat SQL *****" + sb.toString());
				paramMap.put("srchUatNo", srchUatNo);
				if (flagCompany) {
					paramMap.put("srchCompanyCode", srchCompanyCode);
				}
			}
			log.info("searchUat SQL " + sb.toString());
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			List<UatValueObject> tmpUatVOList = new ArrayList<UatValueObject>();
			while (rs.next()) {
				rsUatVO = new UatValueObject();

				String createDttm = rs.getString("create_dttm");
				String uatId = rs.getString("uat_id");
				String truckNbr = rs.getString("truck_nbr");
				String gateOutDttm = rs.getString("gate_out_dttm");
				String uatStatus = rs.getString("uat_status");
				String pkgsNbr = rs.getString("pkgs_nbr");
				String crgDesc = rs.getString("crg_desc");

				rsUatVO.setDateTime(createDttm);
				rsUatVO.setUatId(uatId);
				rsUatVO.setTruckPlaceNo(truckNbr);
				rsUatVO.setGateOutDttm(gateOutDttm);
				rsUatVO.setStatus(uatStatus);
				rsUatVO.setNoOfPkgs(pkgsNbr);
				rsUatVO.setCargoDesc(crgDesc);

				tmpUatVOList.add(rsUatVO);
			}
			uatFormVO.setUatValueObjectList(tmpUatVOList);

			log.info("searchUat Result" + uatFormVO.toString());

		} catch (NullPointerException e) {
			log.info("Exception searchUat :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception searchUat :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: searchUat  DAO  END");
		}
		return uatFormVO;
	}

	// jp.src.ejb.sessionBeans.gbms.ops.tenant.uat-->UatEJB-->createUat()
	/**
	 * Create Uat
	 * 
	 * @param uatFormVO
	 * @return boolean
	 */
	@Override
	public boolean createUat(UatFormValueObject uatFormVO) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String uatSeq = null;
		String tmpUatSeq = null;
		String standCompanyCode = null;
		String uatId = null;

		try {
			log.info("START: createUat  DAO  Start uatFormVO:" + uatFormVO.toString());
			
//				//Try to get max seq uat value
//				try{
//					String sqlGetUatId = "select TENANT_UA_SEQ.NEXTVAL seqno from dual";
//					pstmt = conn.prepareStatement(sqlGetUatId);
//		            rs = pstmt.executeQuery();
//		            if (rs.next()){
//		            	tmpUatSeq = rs.getString("seqno");
//		            }	            
//		            tmpUatSeq = "000000" + tmpUatSeq;
//		            int uatLength = tmpUatSeq.length();
//		            uatSeq = tmpUatSeq.substring(uatLength-6,uatLength);
//				}catch (Exception e) {
//					e.printStackTrace();
//					return false;
//		        }

			try {
				StringBuilder sqlGetStandCoCd = new StringBuilder();
				sqlGetStandCoCd.append(" SELECT misc_type_nm FROM misc_type_code ");
				sqlGetStandCoCd.append(" WHERE cat_cd = 'TENANT_REF' and MISC_TYPE_CD ");
				sqlGetStandCoCd.append(" =:miscTypeCd AND REC_STATUS = 'A' ");

				log.info(" ***createUat SQL *****" + sqlGetStandCoCd.toString());
				paramMap.put("miscTypeCd", uatFormVO.getUatValueObject().getCompanyCode());
				log.info(param + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqlGetStandCoCd.toString(), paramMap);
				if (rs.next()) {
					standCompanyCode = rs.getString("misc_type_nm");
				}
				if (standCompanyCode == null || standCompanyCode.equals("")) {
					return false;
				}
			} catch (Exception e) {
				log.info("Exception createUat :" , e);
				return false;
			}

			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int yearYY = year % 100;

			try {
				StringBuilder sqlGetUatId = new StringBuilder();
				sqlGetUatId.append(" SELECT uat_id FROM tenant_ua WHERE uat_id LIKE :standCoCd ");
				sqlGetUatId.append(" ORDER BY uat_id DESC ");

				log.info(" ***createUat SQL *****" + sqlGetUatId.toString());
				paramMap.put("standCoCd", "T" + standCompanyCode + "%");
				log.info(param + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqlGetUatId.toString(), paramMap);
				if (rs.next()) {
					tmpUatSeq = rs.getString("uat_id");
					if (tmpUatSeq != null && tmpUatSeq.length() == 13) {
						tmpUatSeq = tmpUatSeq.substring(7);
					} else {
						tmpUatSeq = null;
					}
				}
				if (tmpUatSeq == null) {
					tmpUatSeq = "000000";
				}
				tmpUatSeq = "" + (Integer.parseInt(tmpUatSeq) + 1);
				tmpUatSeq = "000000" + tmpUatSeq;
				log.info("=== tmpUatSeq:" + tmpUatSeq);
				int uatLength = tmpUatSeq.length();
				uatSeq = tmpUatSeq.substring(uatLength - 6, uatLength);
				log.info("=== uatSeq:" + uatSeq);
			} catch (Exception e) {
				log.info("Exception createUat :" , e);
				return false;
			}

			if (uatSeq != null) {
				uatId = "T" + standCompanyCode + yearYY + uatSeq;
			} else {
				return false;
			}

			StringBuilder sqlCreateUat = new StringBuilder();
			sqlCreateUat.append(" INSERT INTO tenant_ua (uat_id, truck_nbr, driver_pass_id, ");
			sqlCreateUat.append(" tenant_co_cd, pkgs_nbr, crg_marks, crg_desc, create_remarks, ");
			sqlCreateUat.append(" create_user_id, create_dttm, last_modify_user_id, ");
			sqlCreateUat.append(" last_modify_dttm, uat_status ) VALUES (:uatId, ");
			sqlCreateUat.append(" :truckNbr,:driverPassId,:tenantCoCd,:pkgsNbr, ");
			sqlCreateUat.append(" :crgMarks,:crgDesc,:createRemarks,:userId, ");
			sqlCreateUat.append(" SYSDATE,:userId,SYSDATE,:status) ");

			log.info(" ***createUat SQL *****" + sqlCreateUat.toString());
			// uat_id
			paramMap.put("uatId", uatId);
			// truck_nbr
			paramMap.put("truckNbr", uatFormVO.getUatValueObject().getTruckPlaceNo());
			// driver_pass_id
			paramMap.put("driverPassId", uatFormVO.getUatValueObject().getDriverPassNo());
			// tenant_co_cd
			paramMap.put("tenantCoCd", uatFormVO.getUatValueObject().getCompanyCode());
			// pkgs_nbr
			paramMap.put("pkgsNbr", Integer.parseInt(uatFormVO.getUatValueObject().getNoOfPkgs()));
			// crg_marks
			paramMap.put("crgMarks", uatFormVO.getUatValueObject().getCargoMarks());
			// crg_desc
			paramMap.put("crgDesc", uatFormVO.getUatValueObject().getCargoDesc());
			// create_remarks
			paramMap.put("createRemarks", uatFormVO.getUatValueObject().getRemarks());
			// create_user_id
			paramMap.put("userId", uatFormVO.getUatValueObject().getLoginUser());
			// create_dttm
			// pstmt.setString(10, uatFormVO.getUatValueObject().getDateTime());
			// last_modify_user_id
			// last_modify_dttm
			// pstmt.setString(12, uatFormVO.getUatValueObject().getDateTime());
			// uat_status
			paramMap.put("status", uatFormVO.getUatValueObject().getStatus());
			log.info(param + paramMap);
			int i = namedParameterJdbcTemplate.update(sqlCreateUat.toString(), paramMap);

			if (i == 0) {
				return false;
			} else {
				StringBuilder sqlGetUat = new StringBuilder();
				sqlGetUat.append(" SELECT TO_CHAR(create_dttm, 'DDMMYYYY HH24MI') create_dttm, ");
				sqlGetUat.append(" truck_nbr, gate_out_dttm, uat_status, pkgs_nbr, crg_Desc ");
				sqlGetUat.append(" FROM tenant_ua WHERE uat_id =:uatId ");

				log.info(" ***createUat SQL *****" + sqlGetUat.toString());
				paramMap.put("uatId", uatId);
				log.info(param + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sqlGetUat.toString(), paramMap);
				if (rs.next()) {
					String createDttm = rs.getString("create_dttm");
					String truckNbr = rs.getString("truck_nbr");
					String gateOutDttm = rs.getString("gate_out_dttm");
					String uatStatus = rs.getString("uat_status");
					int pkgsNbr = rs.getInt("pkgs_nbr");
					String crgDesc = rs.getString("crg_Desc");
					// String deleteRemark = rs.getString("delete_remarks");
					uatFormVO.getUatValueObject().setDateTime(createDttm);
					uatFormVO.getUatValueObject().setUatId(uatId);
					uatFormVO.getUatValueObject().setTruckPlaceNo(truckNbr);
					uatFormVO.getUatValueObject().setGateOutDttm(gateOutDttm);
					uatFormVO.getUatValueObject().setStatus(uatStatus);
					uatFormVO.getUatValueObject().setNoOfPkgs("" + pkgsNbr);
					uatFormVO.getUatValueObject().setCargoDesc(crgDesc);
					// uatFormVO.getUatValueObject().setDeleteRemark(deleteRemark);
				}
				return true;
			}
		
		} catch (NullPointerException e) {
			log.info("Exception createUat :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception createUat :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: createUat  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.ops.tenant.uat-->UatEJB-->checkDeleted()
	/**
	 * Check an Uat is deleted or not Return false if cannot delete
	 * 
	 * @param uatFormVO
	 * @return
	 */
	@Override
	public boolean checkDeleted(UatFormValueObject uatFormVO) throws BusinessException {
		boolean canDeleted = true;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: checkDeleted  DAO  Start uatFormVO:" + uatFormVO.toString());
			
			String[] selectedUat = uatFormVO.getSelectedUat();
			StringBuffer deletedUat = new StringBuffer();
			if (selectedUat == null || selectedUat.length == 0) {
				canDeleted = false;
			} else {
				deletedUat.append("('" + selectedUat[0] + "'");

				for (int i = 1; i < selectedUat.length; i++) {
					deletedUat.append(",'" + selectedUat[i] + "'");
				}

				deletedUat.append(")");

				sb.append(" SELECT uat_status FROM tenant_ua WHERE ");
				sb.append(" uat_id IN :deletedUat ");

				log.info(" ***checkDeleted SQL *****" + sb.toString());

				paramMap.put("deletedUat", deletedUat.toString());
				log.info(param + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

				while (rs.next()) {
					String status = rs.getString("uat_status");
					if (!"A".equals(status)) {
						canDeleted = false;
						break;
					}
				}
			}
			
			log.info("END: *** checkDeleted Result *****" + canDeleted);
		} catch (NullPointerException e) {
			log.info("Exception checkDeleted :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception checkDeleted :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkDeleted  DAO  END");
		}
		return canDeleted;
	}

	// jp.src.ejb.sessionBeans.gbms.ops.tenant.uat-->UatEJB-->deleteUat()
	/**
	 * deleteUat
	 * 
	 * @param uatFormVO
	 * @return
	 */
	@Override
	public boolean deleteUat(UatFormValueObject uatFormVO) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		boolean result = false;
		try {
			log.info("START: deleteUat  DAO  Start uatFormVO:" + uatFormVO.toString());
			
			String[] selectedUat = uatFormVO.getSelectedUat();
			StringBuffer deletedUat = new StringBuffer();
			if (selectedUat == null || selectedUat.length == 0) {
				result = false;
			} else {
				deletedUat.append("('" + selectedUat[0] + "'");

				for (int i = 1; i < selectedUat.length; i++) {
					deletedUat.append(",'" + selectedUat[i] + "'");
				}

				deletedUat.append(")");

				sb.append(" UPDATE tenant_ua SET uat_status='X', last_modify_user_id=:userId, ");
				sb.append(" last_modify_dttm=SYSDATE, delete_remarks=:deleteRemarks WHERE ");
				sb.append(" uat_id IN ");
				sb.append(deletedUat.toString());
				// log.info("=== Delete Sql: "+sqlDeleteUat);

				
				paramMap.put("userId", uatFormVO.getUatValueObject().getCreatedBy());
				paramMap.put("deleteRemarks", uatFormVO.getUatValueObject().getDeleteRemark());

				log.info(" ***deleteUat SQL *****" + sb.toString());
				log.info(param + paramMap);
				int i = namedParameterJdbcTemplate.update(sb.toString(), paramMap);;

				if (i == 0) {
					result = false;
				} else {
					result = true;
				}
			}

			log.info("END: *** deleteUat Result *****" + result);
		} catch (NullPointerException e) {
			log.info("Exception deleteUat :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception deleteUat :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: deleteUat  DAO  END");
		}
		uatFormVO.setResult(result);
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.ops.tenant.uat-->UatEJB-->viewUat()
	/**
	 * View Uat by UatId
	 * 
	 * @param uatFormVO
	 * @return
	 */
	@Override
	public UatFormValueObject viewUat(UatFormValueObject uatFormVO) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: viewUat  DAO  Start uatFormVO:" + uatFormVO.toString());
			
			sb.append(" SELECT TO_CHAR(tenant_ua.create_dttm, 'DDMMYYYY HH24MI') ");
			sb.append(" create_dttm, uat_id, truck_nbr, gate_out_dttm, uat_status, ");
			sb.append(" pkgs_nbr, user_nm, tenant_co_cd, driver_pass_id, crg_marks, ");
			sb.append(" crg_Desc, create_remarks, delete_remarks, misc_type_nm ");
			sb.append(" ,cardhldr_name FROM tenant_ua JOIN logon_acct ON ");
			sb.append(" tenant_ua.create_user_id = logon_acct.login_id ");
			sb.append(" JOIN misc_type_code ON tenant_ua.tenant_co_cd = ");
			sb.append(" misc_type_code.misc_type_cd LEFT JOIN jc_carddtl on ");
			sb.append(" tenant_ua.driver_pass_id = jc_carddtl.id_no ");
			sb.append(" WHERE uat_id =:uatId AND misc_type_code.cat_cd = 'TENANT' ");

		
			log.info(" ***viewUat SQL *****" + sb.toString());
			paramMap.put("uatId", uatFormVO.getUatValueObject().getUatId());
			log.info(param + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			// UatValueObject uatVO = new UatValueObject();

			if (rs.next()) {
				String uatId = rs.getString("uat_id");
				String createDttm = rs.getString("create_dttm");
				String truckNbr = rs.getString("truck_nbr");
				String gateOutDttm = rs.getString("gate_out_dttm");
				String uatStatus = rs.getString("uat_status");
				int pkgsNbr = rs.getInt("pkgs_nbr");
				// String createdBy = rs.getString("create_user_id");
				String createdBy = rs.getString("user_nm");
				String companyCode = rs.getString("tenant_co_cd");
				String driverPassNo = rs.getString("driver_pass_id");
				String crgMark = rs.getString("crg_marks");
				String crgDesc = rs.getString("crg_Desc");
				String createRemark = rs.getString("create_remarks");
				String deleteRemark = rs.getString("delete_remarks");
				String companyName = rs.getString("misc_type_nm");
				String driverName = rs.getString("cardhldr_name");

				uatFormVO.getUatValueObject().setDateTime(createDttm);
				uatFormVO.getUatValueObject().setUatId(uatId);
				uatFormVO.getUatValueObject().setTruckPlaceNo(truckNbr);
				uatFormVO.getUatValueObject().setGateOutDttm(gateOutDttm);
				uatFormVO.getUatValueObject().setStatus(uatStatus);
				uatFormVO.getUatValueObject().setNoOfPkgs("" + pkgsNbr);
				uatFormVO.getUatValueObject().setCargoDesc(crgDesc);
				uatFormVO.getUatValueObject().setDeleteRemark(deleteRemark);

				uatFormVO.getUatValueObject().setCompanyCode(companyCode);
				uatFormVO.getUatValueObject().setCompanyName(companyName);
				uatFormVO.getUatValueObject().setDriverPassNo(driverPassNo);
				uatFormVO.getUatValueObject().setCargoMarks(crgMark);
				uatFormVO.getUatValueObject().setDeleteRemark(deleteRemark);
				uatFormVO.getUatValueObject().setRemarks(createRemark);

				uatFormVO.getUatValueObject().setDriverName(driverName);
				uatFormVO.getUatValueObject().setCreatedBy(createdBy);
			}

			log.info("END: *** viewUat Result *****" + uatFormVO.toString());
		} catch (NullPointerException e) {
			log.info("Exception viewUat :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception viewUat :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: viewUat  DAO  END");
		}
		return uatFormVO;
	}
	
	
	// added by Nasir on 22/04/2021
	@Override
	public String getCompanyName(String coCd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		String companyName = "";
		try {
			log.info("START: getCompanyName coCd:" + coCd);
			sb.append(" SELECT CO_NM AS coNm FROM tops.COMPANY_CODE WHERE CO_CD LIKE :coCd ");
			paramMap.put("coCd", coCd);
			log.info("SQL" + sb.toString());
			log.info(param + paramMap);
			companyName = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, String.class);
			
		} catch (Exception e) {
			log.info("Exception getCompanyName :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getCompanyName" + companyName);
		}
		return companyName;
	}

	@Override
	public JasperPrint fillReports(InputStream is, Map<String, Object> parameters) {
		JasperPrint jasperPrint = null;
		try {
			log.info("START: fillReports  DAO  Start parameters:" + parameters+" is:"+is );
			
			jasperPrint = JasperFillManager.fillReport(is, parameters);
			log.info("END: *** fillReports Result *****" + jasperPrint.toString());
		} catch (Exception e) {
			log.info("Exception fillReports :" , e);
		} finally {
			
			log.info("END: fillReports  DAO  END");
		}
		return jasperPrint;
	}
	
	

	
}
