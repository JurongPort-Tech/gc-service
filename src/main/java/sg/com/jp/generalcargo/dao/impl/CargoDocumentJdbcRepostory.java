package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.CargoDocumentRepository;
import sg.com.jp.generalcargo.domain.AuditTrailDetail;
import sg.com.jp.generalcargo.domain.CargoDocUpload;
import sg.com.jp.generalcargo.domain.CargoDocUploadDetail;
import sg.com.jp.generalcargo.domain.CargoDocUploadNotificationDetail;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.MiscDetail;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselDetail;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("CargoDocumentRepo")
public class CargoDocumentJdbcRepostory implements CargoDocumentRepository {

	private static final Log log = LogFactory.getLog(CargoDocumentJdbcRepostory.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	// Region
	public String getTimeStamp() throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info(" START getTimeStamp() ###");
			String sql = "select TO_CHAR(SYSDATE, 'dd-mm-yyyy hh24:mi:ss') from dual";
			String ts = namedParameterJdbcTemplate.queryForObject(sql, paramMap, String.class);
			log.info("getTimeStamp :" + ts);
			return ts;

		} catch (Exception ex) {
			log.info("Exception  getTimeStamp :" , ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getTimeStamp");
		}	

	}

	@Override
	public List<VesselDetail> getVesselInfo(String vesselName) throws BusinessException {
		List<VesselDetail> vesselNameList = new ArrayList<>();
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getVesselInfo  DAO  Start Obj :" + vesselName.toString());

			sb.append(
					" SELECT VSL_NM || '-' || IN_VOY_NBR || '-' || OUT_VOY_NBR || '-' || TERMINAL vesselName ,VV_CD vvCd from TOPS.VESSEL_CALL WHERE VSL_NM LIKE concat(:vesselName, '%') ");

			paramMap.put("vesselName", "%" + vesselName + "%");

			log.info("getVesselInfo SQL" + sb.toString() + "paramMap:" + paramMap.toString());

			vesselNameList = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<VesselDetail>(VesselDetail.class));

			log.info("END: *** getVesselInfo Result *****" + vesselNameList.size());
		} catch (Exception e) {
			log.info("Exception getVesselInfo :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselInfo  DAO  END");
		}

		return vesselNameList;
	}

	@Override
	public CargoDocUpload getCargoDocUploadDetails(Criteria criteria) throws BusinessException {

		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<>();
		CargoDocUpload obj = new CargoDocUpload();
		List<VesselDetail> vesselNameList = new ArrayList<>();
		try {
			log.info("getCargoDocUploadDetails DAO criteria:" + criteria.toString());
			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			String vesselName = CommonUtility.deNull(criteria.getPredicates().get("vesselName"));
			String voyageNumber = CommonUtility.deNull(criteria.getPredicates().get("voyageNumber"));
			String type = CommonUtility.deNull(criteria.getPredicates().get("type"));
			
			
			if (type.equalsIgnoreCase("VVCD")) {
				// if in case no vvcd get the
				sb = new StringBuilder();
				sb.append("	SELECT ");
				sb.append(
						"		V.VSL_NM || '-' || V.IN_VOY_NBR || '-' || V.OUT_VOY_NBR || '[' || V.TERMINAL || ']' vslNameVoyage ,");
				sb.append("		V.IN_VOY_NBR inVoyageNbr, ");
				sb.append("		V.OUT_VOY_NBR outVoyageNbr, ");
				sb.append("		V.TERMINAL terminal, ");
				sb.append("		V.VSL_NM vesselName, ");
				sb.append("		V.VV_CD vvCd, ");
				sb.append("		V.TERMINAL, ");
				sb.append(" V.CREATE_CUST_CD createdCustCode ");
				sb.append("	FROM ");
				sb.append("		TOPS.VESSEL_CALL V ");
				sb.append("	LEFT JOIN TOPS.VESSEL VS ON ");
				sb.append("		V.VSL_NM = VS.VSL_NM  ");
				sb.append("	WHERE ");
				sb.append("		 v.VV_CD =:vvCd ");
				paramMap.put("vvCd", vvCd);
			} else {
				sb = new StringBuilder();
				sb.append("	SELECT ");
				sb.append(
						"		V.VSL_NM || '-' || V.IN_VOY_NBR || '-' || V.OUT_VOY_NBR || '[' || V.TERMINAL || ']' vslNameVoyage ,");
				sb.append("		V.IN_VOY_NBR inVoyageNbr, ");
				sb.append("		V.OUT_VOY_NBR outVoyageNbr, ");
				sb.append("		V.TERMINAL terminal, ");
				sb.append("		V.VSL_NM vesselName, ");
				sb.append("		V.VV_CD vvCd, ");
				sb.append("		V.TERMINAL, ");
				sb.append(" V.CREATE_CUST_CD createdCustCode ");
				sb.append("	FROM ");
				sb.append("		TOPS.VESSEL_CALL V ");
				sb.append("	LEFT JOIN TOPS.VESSEL VS ON ");
				sb.append("		(V.VSL_NM = VS.VSL_NM) ");
				sb.append("	WHERE ");
				sb.append("		(V.VSL_NM =:vesselName ");
				sb.append("		OR VS.VSL_FULL_NM =:vesselName) ");
				sb.append("		AND (V.IN_VOY_NBR =:voyageNumber OR V.OUT_VOY_NBR=:voyageNumber) ");
				paramMap.put("vesselName", vesselName);
				paramMap.put("voyageNumber", voyageNumber);
			}
			log.info("query : " + sb.toString() + " ,params :" + paramMap.toString());
			vesselNameList = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<VesselDetail>(VesselDetail.class));
			if (vesselNameList != null && vesselNameList.size() > 0) {
				obj.setVvCd(vesselNameList.get(0).getVvCd());
				obj.setVoyNo(vesselNameList.get(0).getInVoyageNbr());
				obj.setVslName(vesselNameList.get(0).getVesselName());
				obj.setVslNameVoyage(vesselNameList.get(0).getVslNameVoyage());
				obj.setCreatedCustCode(vesselNameList.get(0).getCreatedCustCode());
				vvCd = vesselNameList.get(0).getVvCd();
			} else {
				return null;
			}

			obj.setVvCd(vvCd);
			paramMap = new HashMap<>();
			List<CargoDocUploadDetail> cargoDocUploadInfo = new ArrayList<>();
			sb = new StringBuilder();
			sb.append(" SELECT ");
			sb.append(" s.MISC_TYPE_NM documentType, ");
			sb.append(" s.MISC_TYPE_CD documentTypeCD, ");
			// sb.append(" regexp_substr(s.REMARKS, '[^,]+', 2, 1) isMandatory, ");
			sb.append(" '0' isMandatory, ");
			sb.append(" d.ACTUAL_FILE_NM actualFileName, ");
			sb.append(" d.ASSIGN_FILE_NM assignedFileName, ");
			sb.append(" d.FILE_TYPE fileType, ");
			sb.append("	ac.login_id userid ,");
			sb.append("	ac.user_nm uploadedBy ,");
			sb.append(" TO_CHAR(d.LAST_MODIFY_DTTM,'DD-MM-YYYY HH24:MI') uploadedDate  ");
			sb.append(" FROM TOPS.SYSTEM_CONFIG s  ");
			sb.append(" LEFT JOIN GBMS.CARGO_DOC_UPLOAD_DETAILS d ON d.DOC_TYPE_CD=s.MISC_TYPE_CD AND d.VV_CD=:vvCd ");
			sb.append(" LEFT JOIN GBMS.CARGO_DOC_UPLOAD u ON u.VV_CD=d.VV_CD ");
			sb.append(
					" LEFT JOIN TOPS.logon_acct ac ON ac.login_id= SUBSTR(d.LAST_MODIFY_USER_ID, INSTR( d.LAST_MODIFY_USER_ID, '/', -1 ) + 1 )   ");
			sb.append(" Where s.CAT_CD='CARGO_DOC_TYPE' ");
			// sb.append(" Order BY regexp_substr(s.REMARKS, '[^,]+', 1, 1)");
			sb.append(" Order BY s.REMARKS ");

			paramMap.put("vvCd", vvCd);
			log.info(" [getCargoInfo()] :sql :" + sb.toString() + " params :" + paramMap);
			cargoDocUploadInfo = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<CargoDocUploadDetail>(CargoDocUploadDetail.class));
			log.info(" [getCargoInfo()] :cargoInfo" + cargoDocUploadInfo.toString());
			obj.setCargoDocUploadInfo(cargoDocUploadInfo);
		}

		catch (Exception ex) {
			log.info("Exception getCargoDocUploadDetails : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoDocUploadDetails Dao END obj" + obj.toString());
		}

		return obj;
	}

	@Override
	public TableResult getCargoDocUploadAuditInfo(Criteria criteria) throws BusinessException {
		TableResult result = new TableResult();
		StringBuffer sb = new StringBuffer();
		String sql = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		List<AuditTrailDetail> info = new ArrayList<>();
		try {

			log.info("START getCargoDocUploadAuditInfo:" + "criteria :" + criteria.toString());

			String vvCd = criteria.getPredicates().get("vvCd");
			int start = criteria.getStart();
			int limit = criteria.getLimit();

			TableData tableData = new TableData();

			sb = new StringBuffer();

			sb.append(
					" SELECT  TO_CHAR(dh.ACT_DTTM, 'YYYY-MM-DD hh24:mi:ss') transTimeStamp,TO_CHAR(dh.LAST_MODIFY_DTTM, 'DD-MM-YYYY HH24:MI') transDate, ");
			sb.append(
					"CASE WHEN ac.user_nm IS NOT NULL THEN  CONCAT (CONCAT (ac.user_nm, ' - '), ac.CUST_CD) ELSE '' END modifiedBy");
			sb.append(",dh.REMARKS remarks");
			sb.append("	from GBMS.AUDIT_CARGO_DOC_UPLOAD dh ");
			sb.append(" JOIN GBMS.CARGO_DOC_UPLOAD u ON u.VV_CD=dh.VV_CD AND u.VV_CD=:vvCd");
			sb.append(
					" LEFT JOIN TOPS.logon_acct ac ON ac.login_id =  SUBSTR(dh.LAST_MODIFY_USER_ID, INSTR( dh.LAST_MODIFY_USER_ID, '/', -1 ) + 1 ) ");
			sb.append(" order by dh.LAST_MODIFY_DTTM desc");
			paramMap.put("vvCd", vvCd);
			sql = sb.toString();
			log.info("getManifestActionTrial SQL" + sql.toString() + "Params:" + paramMap.toString());
			tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")", paramMap,
					Integer.class));
			sql = CommonUtil.getPaginatedSql(sql, start, limit);
			info = namedParameterJdbcTemplate.query(sql, paramMap,
					new BeanPropertyRowMapper<AuditTrailDetail>(AuditTrailDetail.class));
			TopsModel topsModel = new TopsModel();
			for (AuditTrailDetail object : info) {
				topsModel.put(object);
			}
			tableData.setListData(topsModel);
			result.setSuccess(true);
			result.setData(tableData);
		} catch (Exception e) {
			log.info("Exception getCargoDocUploadAuditInfo:", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getCargoDocUploadAuditInfo" + result.toString());
		}
		return result;
	}

	@Override
	public Result saveCargoDocUpload(Criteria criteria) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<>();
		Result result = new Result();
		try {
			log.info("START saveCargoDocUpload DAO criteria:" + criteria.toString());

			result.setSuccess(true);
			String vvCd = CommonUtil.deNull(criteria.getPredicates().get("vvCd")).trim();
			String remarks = CommonUtil.deNull(criteria.getPredicates().get("remarks")).trim();
			String userId = CommonUtil.deNull(criteria.getPredicates().get("userAccount")).trim();

			sb = new StringBuilder();
			sb.append(" SELECT COUNT(*) from GBMS.CARGO_DOC_UPLOAD WHERE VV_CD =:vvCd ");
			paramMap.put("vvCd", vvCd);
			paramMap.put("userId", userId);
			paramMap.put("remarks", remarks);
			log.info("saveCargoDocUpload  SQL" + sb.toString() + "paramMap:" + paramMap.toString());
			int count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);

			String last_modify_dttm = getTimeStamp();
			paramMap.put("last_modify_dttm", last_modify_dttm);

			if (count == 0) {
				sb = new StringBuilder();
				sb.append(" INSERT INTO GBMS.CARGO_DOC_UPLOAD  ");
				sb.append(" (VV_CD , REMARKS, CREATED_USER_ID, CREATED_DTTM, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM ) ");
				sb.append(
						" VALUES (:vvCd, :remarks , :userId, TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'), :userId, TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3') )");
				log.info("saveCargoDocUpload INSERT SQL" + sb.toString() + "paramMap:" + paramMap.toString());
				namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			} else {
				sb = new StringBuilder();
				sb.append("UPDATE GBMS.CARGO_DOC_UPLOAD SET ");
				sb.append("    REMARKS =:remarks, ");
				sb.append("    LAST_MODIFY_USER_ID =:userId, ");
				sb.append("    LAST_MODIFY_DTTM = TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3') ");
				sb.append("  WHERE  VV_CD=:vvCd ");
				log.info("saveCargoDocUpload  UPDATE SQL" + sb.toString() + "paramMap:" + paramMap.toString());
				namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			}

			result.setData(last_modify_dttm);

		} catch (Exception e) {
			log.info("Exception saveCargoDocUpload :", e);
			result.setSuccess(false);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: saveCargoDocUpload  DAO  " + result.toString());
		}
		return result;
	}

	// Attachment Methods
	@Override
	public Result saveCargoDocUploadDetail(CargoDocUploadDetail obj) throws BusinessException {
		StringBuilder sb = null;
		Result result = new Result();
		try {

			log.info("START: saveCargoDocUploadDetail  DAO  Start Obj :" + obj.toString());
			result.setSuccess(true);
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("vvCd", obj.getVvCd());

			String cargoDtlCountSql = " SELECT COUNT(*) from GBMS.CARGO_DOC_UPLOAD_DETAILS WHERE  VV_CD=:vvCd AND DOC_TYPE_CD=:documentTypeCD  ";
			paramMap.put("documentTypeCD", obj.getDocumentTypeCD());

			log.info("updateAction SQL" + cargoDtlCountSql + "paramMap:" + paramMap.toString());
			int count = namedParameterJdbcTemplate.queryForObject(cargoDtlCountSql.toString(), paramMap, Integer.class);
			log.info("count " + count);

			if (count == 0) {
				sb = new StringBuilder();
				sb.append(" INSERT INTO GBMS.CARGO_DOC_UPLOAD_DETAILS  ");
				sb.append("  ( ");
				sb.append("		DOC_TYPE_CD, ");
				sb.append(" 	ACTUAL_FILE_NM,");
				sb.append("    	ASSIGN_FILE_NM, ");
				sb.append("    	FILE_TYPE, ");
				sb.append("    	FILE_SIZE, ");
				sb.append("    	FILE_UPLOAD_USER_ID, ");
				sb.append("    	FILE_UPLOAD_DTTM, ");
				sb.append("    	LAST_MODIFY_USER_ID, ");
				sb.append("    	LAST_MODIFY_DTTM, ");
				sb.append("    	VV_CD ");
				sb.append("  ) ");
				sb.append("  VALUES ");
				sb.append("  ( ");
				sb.append("    :documentTypeCD, ");
				sb.append("    :actualFileName, ");
				sb.append("    :assignedFileName, ");
				sb.append("    :fileType, ");
				sb.append("    :fileSize,  ");
				sb.append("    :uploadedBy, ");
				sb.append("	   TO_TIMESTAMP(:uploadedDate,'dd-mm-yyyy hh24:mi:ss.ff3') ,");
				sb.append("    :uploadedBy , ");
				sb.append("    TO_TIMESTAMP(:uploadedDate,'dd-mm-yyyy hh24:mi:ss.ff3') , ");
				sb.append("    :vvCd ");
				sb.append("  )");

				log.info("insert SQL" + sb.toString());
				log.info(" ***paramMap *****" + paramMap.toString());

				namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(obj));
			} else {
				sb = new StringBuilder();
				sb.append("UPDATE GBMS.CARGO_DOC_UPLOAD_DETAILS SET ");
				sb.append("    ACTUAL_FILE_NM =:actualFileName , ");
				sb.append("    ASSIGN_FILE_NM =:assignedFileName , ");
				sb.append("    FILE_TYPE =:fileType , ");
				sb.append("    FILE_SIZE =:fileSize , ");
				sb.append("    FILE_UPLOAD_USER_ID =:uploadedBy, ");
				sb.append("    FILE_UPLOAD_DTTM = SYSDATE, ");
				sb.append("    LAST_MODIFY_USER_ID = :uploadedBy,");
				sb.append("    LAST_MODIFY_DTTM = TO_TIMESTAMP(:uploadedDate,'dd-mm-yyyy hh24:mi:ss.ff3') ");
				sb.append("  WHERE  VV_CD=:vvCd AND DOC_TYPE_CD=:documentTypeCD ");
				log.info("Update SQL" + sb.toString());
				log.info(" ***paramMap *****" + paramMap.toString());
				namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(obj));
			}

		} catch (Exception e) {
			log.info("Exception saveCargoDocUploadDetail :", e);
			result.setSuccess(false);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: saveCargoDocUploadDetail  DAO  END");
		}
		return result;
	}

	@Override
	public CargoDocUpload getCargoDocUploadAuditDetail(Criteria criteria) throws BusinessException {

		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<>();
		CargoDocUpload obj = new CargoDocUpload();
		try {

			log.info("START getCargoDocUploadAuditDetail DAO criteria:" + criteria.toString());

			String vvCd = CommonUtil.deNull(criteria.getPredicates().get("vvCd")).trim();
			String transDate = CommonUtil.deNull(criteria.getPredicates().get("transcationDate")).trim();

			obj.setVvCd(vvCd);

			sb.append(" SELECT ");
			sb.append(" sc.MISC_TYPE_NM documentType, ");
			sb.append(" sc.MISC_TYPE_CD documentTypeCD, ");
			// sb.append(" regexp_substr(s.REMARKS, '[^,]+', 2, 1) isMandatory, ");
			sb.append(" '0' isMandatory, ");
			sb.append(" duh.ACTUAL_FILE_NM actualFileName, ");
			sb.append(" duh.ASSIGN_FILE_NM assignedFileName, ");
			sb.append(" duh.FILE_TYPE fileType, ");
			sb.append(" ac.user_nm uploadedBy, ");
			sb.append(" TO_CHAR(duh.LAST_MODIFY_DTTM,'DD-MM-YYYY HH24:MI') uploadedDate  ");
			sb.append(" FROM GBMS.AUDIT_CARGO_DOC_UPLOAD_DETAILS duh  ");
			sb.append(
					" JOIN GBMS.AUDIT_CARGO_DOC_UPLOAD dh ON dh.LAST_MODIFY_DTTM=duh.LAST_MODIFY_DTTM AND dh.VV_CD=:vvCd ");
			sb.append(" JOIN TOPS.SYSTEM_CONFIG sc on CAT_CD='CARGO_DOC_TYPE' AND duh.DOC_TYPE_CD = sc.MISC_TYPE_CD ");
			sb.append(
					" LEFT JOIN TOPS.logon_acct ac ON ac.login_id=  SUBSTR(duh.LAST_MODIFY_USER_ID, INSTR( duh.LAST_MODIFY_USER_ID, '/', -1 ) + 1 )  ");
			sb.append(" Where TO_CHAR(dh.ACT_DTTM, 'YYYY-MM-DD hh24:mi:ss')=:transDate ");
			sb.append(" and dh.VV_CD=:vvCd");
			// sb.append(" Order BY regexp_substr(s.REMARKS, '[^,]+', 1, 1)");
			// sb.append(" Order BY s.REMARKS )");

			/*
			 * sb.append(
			 * "select sc.MISC_TYPE_NM documentType,duh.ACTUAL_FILE_NM actualFileName,duh.ASSIGN_FILE_NM assignedFileName,duh.LAST_MODIFY_USER_ID modifiedBy,dh.REMARKS remarks,TO_CHAR(duh.ACT_DTTM, 'dd/mm/yyyy HH24:MI') transDate,dh.VV_CD vvCd  "
			 * ); sb.append(" from GBMS.AUDIT_CARGO_DOC_UPLOAD_DETAILS duh"); sb.append(
			 * " JOIN GBMS.AUDIT_CARGO_DOC_UPLOAD dh ON TO_CHAR(dh.ACT_DTTM, 'dd/mm/yyyy HH24:MI')=TO_CHAR(duh.ACT_DTTM, 'dd/mm/yyyy HH24:MI')"
			 * );
			 * sb.append(" JOIN TOPS.SYSTEM_CONFIG sc on duh.DOC_TYPE_CD = sc.MISC_TYPE_CD"
			 * ); sb.append(" where TO_CHAR(dh.ACT_DTTM, 'dd/mm/yyyy HH24:MI')=:transDate");
			 * sb.append(" and dh.VV_CD=:vvCd");
			 */

			paramMap.put("transDate", transDate);
			paramMap.put("vvCd", vvCd);

			log.info("getCargoDocUploadAuditDetail SQL :" + sb.toString() + " Params :" + paramMap);
			List<CargoDocUploadDetail> cargoDocUploadInfo = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<CargoDocUploadDetail>(CargoDocUploadDetail.class));
			log.info("getCargoDocUploadAuditDetail result :"+ cargoDocUploadInfo.toString());
			obj.setCargoDocUploadInfo(cargoDocUploadInfo);
		} catch (Exception e) {
			log.info("Exception getCargoDocUploadAuditDetail :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoDocUploadAuditDetail Dao END obj" + obj.toString());
		}
		return obj;
	}

	public Boolean isCheckCount(String vvCd) throws BusinessException {
		Map<String, String> paramMap = new HashMap<>();
		try {
			log.info("START: isCheckCount  DAO  Start Obj :" + vvCd.toString());
			String vvCdCount = " SELECT COUNT(*) from GBMS.CARGO_DOC_UPLOAD WHERE VV_CD =:vvCd ";
			paramMap.put("vvCd", vvCd);
			log.info("isCheckCount SQL" + vvCdCount + "paramMap:" + paramMap.toString());
			int count = namedParameterJdbcTemplate.queryForObject(vvCdCount.toString(), paramMap, Integer.class);
			if (count > 0) {
				return true;
			}
		} catch (Exception e) {
			log.info("Exception isCheckCount :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isCheckCount  DAO  END");
		}
		return false;
	}

	public Boolean isDocSubmissionAllowed(String vvCd, String coCd) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		Boolean result = true;
		try {
			log.info("START isDocSubmissionAllowed vvcd: " + vvCd + ", coCd :" + coCd);

			int count = 0;
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("vvCd", vvCd);
			if (coCd.equalsIgnoreCase("JP")) {
				 sb = new StringBuffer();
				sb.append(
						" select count(*) from TOPS.VESSEL_CALL WHERE VV_CD=:vvCd AND (GB_CLOSE_BJ_IND='Y' OR GB_CLOSE_SHP_IND='Y') ");
				
				log.info("isSubmissionAllowed SQL : " + sb.toString() + "parammap" + paramMap.toString());
				count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
			} else {

				sb = new StringBuffer();
				sb.append(
						" select count(*) from TOPS.VESSEL_CALL WHERE VV_CD=:vvCd AND (GB_CLOSE_BJ_IND='Y' OR GB_CLOSE_SHP_IND='Y') ");

				log.info("isSubmissionAllowed SQL : " + sb.toString() + "parammap" + paramMap.toString());
				count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
				if (count == 0) {

					sb = new StringBuffer();
					sb.append(
							" select count(*) from TOPS.AUDIT_TRAIL_VESSEL_CALL WHERE VV_CD=:vvCd AND (GB_CLOSE_BJ_IND='Y' OR GB_CLOSE_SHP_IND='Y') ");
					log.info("isDocSubmissionAllowed SQL : " + sb.toString() + "parammap" + paramMap.toString());
					count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
				}
			}
			log.info(" isDocSubmissionAllowed Count :" + count);
			if (count > 0)
				result = false;

		} catch (Exception e) {
			log.info("Exception isDocSubmissionAllowed", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END isDocSubmissionAllowed : " + result);
		}

		return result;

	}

	@Override
	public CargoDocUploadNotificationDetail getNotificationDetails(Criteria criteria) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<>();
		CargoDocUploadNotificationDetail obj = new CargoDocUploadNotificationDetail();
		try {
			log.info("getNotificationDetails DAO criteria:" + criteria.toString());
			String vvCd = CommonUtil.deNull(criteria.getPredicates().get("vvCd")).trim();
			String transDate = CommonUtil.deNull(criteria.getPredicates().get("uploadedDate")).trim();
			
			sb = new StringBuilder();
			
			sb= new StringBuilder();
			sb.append("	SELECT ");
			sb.append(" V.VSL_NM || '-' || V.IN_VOY_NBR || '-' || V.OUT_VOY_NBR vslNameVoyage , ");
			sb.append(" TO_CHAR(B.ETB_DTTM, 'ddmmyyyy HH24MI') AS etb,  ");
			sb.append(" TO_CHAR(B.ETU_DTTM, 'ddmmyyyy HH24MI') AS etu , ");
			sb.append(" TO_CHAR(V.VSL_BERTH_DTTM, 'ddmmyyyy HH24MI') AS btr , ");
			sb.append(" cc.co_nm AS agent ");
			sb.append("	FROM ");
			sb.append(" TOPS.VESSEL_CALL V ");
			sb.append(" LEFT OUTER JOIN COMPANY_CODE cc on V.CREATE_CUST_CD = cc.co_cd ");
			sb.append("	LEFT JOIN TOPS.BERTHING B ON (V.VV_CD = B.VV_CD	AND B.SHIFT_IND = 1) ");
			sb.append("	WHERE ");
			sb.append("	v.VV_CD =:vvCd ");
			paramMap.put("vvCd", vvCd);
			log.info("SQL :"+ sb.toString() +", params :"+ paramMap.toString());
			List<CargoDocUploadNotificationDetail> list = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<CargoDocUploadNotificationDetail>(CargoDocUploadNotificationDetail.class));
			if (list != null && list.size() > 0) {
				obj=list.get(0);
			}
			
						
			obj.setVvCd(vvCd);
			sb = new StringBuilder();
			sb.append(" SELECT ");
			sb.append(" sc.MISC_TYPE_NM documentType, ");
			sb.append(" sc.MISC_TYPE_CD documentTypeCD, ");
			sb.append(" '0' isMandatory, ");
			sb.append(" duh.ACTUAL_FILE_NM actualFileName, ");
			sb.append(" duh.ASSIGN_FILE_NM assignedFileName, ");
			sb.append(" duh.FILE_TYPE fileType, ");
			sb.append(" ac.user_nm uploadedBy, ");
			sb.append(" TO_CHAR(duh.LAST_MODIFY_DTTM,'DD-MM-YYYY HH24:MI') uploadedDate  ");
			sb.append(" FROM GBMS.AUDIT_CARGO_DOC_UPLOAD_DETAILS duh  ");
			sb.append(
					" JOIN GBMS.AUDIT_CARGO_DOC_UPLOAD dh ON dh.LAST_MODIFY_DTTM=duh.LAST_MODIFY_DTTM AND dh.VV_CD=:vvCd ");
			sb.append(" JOIN TOPS.SYSTEM_CONFIG sc on CAT_CD='CARGO_DOC_TYPE' AND duh.DOC_TYPE_CD = sc.MISC_TYPE_CD ");
			sb.append(
					" LEFT JOIN TOPS.logon_acct ac ON ac.login_id=  SUBSTR(duh.LAST_MODIFY_USER_ID, INSTR( duh.LAST_MODIFY_USER_ID, '/', -1 ) + 1 )  ");
			sb.append(" Where TO_CHAR(dh.ACT_DTTM, 'DD-MM-YYYY hh24:mi:ss')=:transDate ");
			sb.append(" and dh.VV_CD=:vvCd");
			

			paramMap.put("transDate", transDate);
			paramMap.put("vvCd", vvCd);

			log.info("Audit detail SQL :" + sb.toString() + " Params :" + paramMap);
			List<CargoDocUploadDetail> cargoDocUploadInfo = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<CargoDocUploadDetail>(CargoDocUploadDetail.class));
			log.info("Audit detail result :"+ cargoDocUploadInfo.toString());
			obj.setCargoDocUploadInfo(cargoDocUploadInfo);
			
			
		}

		catch (Exception ex) {
			log.info("Exception getNotificationDetails : ", ex);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getNotificationDetails Dao END obj" + obj.toString());
		}

		return obj;
	}
	
	public List<MiscDetail> getCargoDocEmail(String catCd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		List<MiscDetail> list = null;
		try {
			log.info("START: getCargoDocEmail  DAO  Start Obj "+" catCd:"+catCd );

			sb.append("SELECT MISC_TYPE_CD typeCode, MISC_TYPE_NM typeValue, REMARKS type FROM TOPS.SYSTEM_CONFIG sc WHERE CAT_CD =:catCd ");
			paramMap.put("catCd", catCd);
			log.info(" getSysConfigObj SQL " + sb.toString() + "paramMap" + paramMap.toString());
			list = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
					new BeanPropertyRowMapper<MiscDetail>(MiscDetail.class));
			log.info("getCargoDocEmail:" + list.toString());
		} catch (Exception e) {
			log.info("Exception getCargoDocEmail : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoDocEmail DAO ");
		}
		return list;
	}



}
