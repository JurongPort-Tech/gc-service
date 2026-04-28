package sg.com.jp.generalcargo.dao.impl;

import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hpsf.Array;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.controller.UploadDocument;
import sg.com.jp.generalcargo.dao.GBMiscApplicationRepository;
import sg.com.jp.generalcargo.domain.AttachmentFileValueObject;
import sg.com.jp.generalcargo.domain.CompanyValueObject;
import sg.com.jp.generalcargo.domain.ContainerValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.Email;
import sg.com.jp.generalcargo.domain.EmailValueObject;
import sg.com.jp.generalcargo.domain.EnquireListingAppValueObject;
import sg.com.jp.generalcargo.domain.EnquireListingValueObject;
import sg.com.jp.generalcargo.domain.EnquireQueryObject;
import sg.com.jp.generalcargo.domain.EnquireSummarySlotValueObject;
import sg.com.jp.generalcargo.domain.ExceptionAlertValueObject;
import sg.com.jp.generalcargo.domain.ExpiredCompanyValueObject;
import sg.com.jp.generalcargo.domain.IMessageValueObject;
import sg.com.jp.generalcargo.domain.MiscAppParkingAreaObject;
import sg.com.jp.generalcargo.domain.MiscAppTpaApproveValueObject;
import sg.com.jp.generalcargo.domain.MiscAppValueObject;
import sg.com.jp.generalcargo.domain.MiscBargeValueObject;
import sg.com.jp.generalcargo.domain.MiscContractValueObject;
import sg.com.jp.generalcargo.domain.MiscCustValueObject;
import sg.com.jp.generalcargo.domain.MiscHotworkValueObject;
import sg.com.jp.generalcargo.domain.MiscParkMacValueObject;
import sg.com.jp.generalcargo.domain.MiscReeferValueObject;
import sg.com.jp.generalcargo.domain.MiscSpaceValueObject;
import sg.com.jp.generalcargo.domain.MiscSpreaderValueObject;
import sg.com.jp.generalcargo.domain.MiscVehValueObject;
import sg.com.jp.generalcargo.domain.OvrNghtPrkgVehValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.Sms;
import sg.com.jp.generalcargo.domain.StorageOrderValueObject;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.TpaDetailsHistoryVO;
import sg.com.jp.generalcargo.domain.TpaVO;
import sg.com.jp.generalcargo.domain.TypeCdVO;
import sg.com.jp.generalcargo.domain.VehicleDetailsVO;
import sg.com.jp.generalcargo.domain.VehicleVO;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.Constants;
import sg.com.jp.generalcargo.util.DBConstants;
import sg.com.jp.generalcargo.util.MiscAppCommonUtility;
import sg.com.jp.generalcargo.util.SummaryRowComparator;
import sg.com.jp.generalcargo.util.TpaConstants;

@Repository("gbMiscApplicationRepo")
public class GBMiscApplicationJdbcRepository implements GBMiscApplicationRepository {
	private static final Log log = LogFactory.getLog(GBMiscApplicationJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private MiscAppCommonUtility miscAppCommonUtility;

	@Value("${jp.common.notificationProperties.emailEndpoint}")
	String commonServiceUrl;
	
	@Value("${jp.common.notificationProperties.Alert.Sender}")
	String alert_Sender;
	
	@Value("${jp.common.notificationProperties.GBMisc.SenderEmail}")
	String gBMiscSenderEmail;
	
	@Value("${jp.common.notificationProperties.email.HireWoodenSubmit_subject}")
	String HireWoodenSubmit_subject;
	
	@Value("${jp.common.notificationProperties.email.TemplateSubmit_body}")
	String TemplateSubmit_body_template;
	
	@Value("${jp.common.notificationProperties.email.OvernightParkVecSubmit_subject}")
	String OvernightParkVecSubmit_subject;
	
	@Value("${jp.common.notificationProperties.email.ParkLineTowBargeSubmit_subject}")
	String ParkLineTowBargeSubmit_subject;
	
	@Value("${jp.common.notificationProperties.email.ReeferCntrPowerOutletSubmit_subject}")
	String ReeferCntrPowerOutletSubmit_subject;
	
	@Value("${jp.common.notificationProperties.email.OvernightParkForkliftSubmit_subject}")
	String OvernightParkForkliftSubmit_subject;
	
	@Value("${jp.common.notificationProperties.email.SpaceAppSubmit_subject}")
	String SpaceAppSubmit_subject;
	
	@Value("${jp.common.notificationProperties.email.StationingForkliftSubmit_subject}")
	String StationingForkliftSubmit_subject;
	
	@Value("${jp.common.notificationProperties.email.ContractorPermitSubmit_subject}")
	String ContractorPermitSubmit_subject;
		
	@Value("${jp.common.notificationProperties.email.HotWorkPermitSubmit_subject}")
	String HotWorkPermitSubmit_subject;
	
	@Value("${jp.common.notificationProperties.email.TrailerParkingAppSubmit_subject}")
	String TrailerParkingAppSubmit_subject;
	
	@Value("${jp.common.notificationProperties.email.OvernightParkVecVoid_body}")
	String OvernightParkVecVoid_body_template;
	
	@Value("${jp.common.notificationProperties.email.OvernightParkVecApproved_body}")
	String OvernightParkVecApproved_body_template;
	
		
	public static final long MILISECONDS_IN_A_MINUTE = 60 * 1000;
	public static final long ONE_DAYS_IN_MINUTE = 24 * 60;
	public static final long MILISECONDS_IN_1_HOUR = 1 * 60 * 60 * 1000;
	public static final int DEFAULT_VALUE = 0;
	public static final int NOT_APPLICATE_VALUE = -1;

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getATUGracePeriod()
	private int getATUGracePeriod() throws BusinessException {
		log.info("START getATUGracePeriod()");
		int days = 0;
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getATUGracePeriod  DAO  Start ");

			sb.append(" SELECT VALUE FROM SYSTEM_PARA  WHERE PARA_CD = 'ATUGP' ");

			log.info(" ***getATUGracePeriod SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			while (rs.next()) {
				days = rs.getInt("VALUE");
			}
			log.info("END: *** getATUGracePeriod Result *****" + days);
		} catch (NullPointerException e) {
			log.info("Exception: getATUGracePeriod ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: getATUGracePeriod ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getATUGracePeriod  DAO  END");
		}
		return days;
	}

	// MISC APP LIST

	public AttachmentFileValueObject getFileAttachment(String miscSeqNumber, String fileName) throws BusinessException {

		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();

		AttachmentFileValueObject obj = new AttachmentFileValueObject();
		try {
			log.info("START: getFileAttachment Dao Start miscSeqNumber:" + CommonUtility.deNull(miscSeqNumber)
					+ " fileName:" + CommonUtility.deNull(fileName));

			sb.append("select misc_seq_nbr, doc_type, upload_file_nm, ");
			sb.append(" assign_file_nm, create_user_id, create_dttm from misc_upload_doc ");
			sb.append("where misc_seq_nbr=:miscSeqNumber and ASSIGN_FILE_NM=:fileName");

			paramMap.put("miscSeqNumber", miscSeqNumber);
			paramMap.put("fileName", fileName);
			log.info(" ***getFileAttachment SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next())
				obj.setAssign_file_nm(rs.getString("assign_file_nm"));
			obj.setUpload_file_nm(rs.getString("upload_file_nm"));
			obj.setMisc_seq_nbr(rs.getString("misc_seq_nbr"));
			obj.setDoc_type(rs.getString("doc_type"));
			obj.setCreate_user_id(rs.getString("create_user_id"));
			obj.setCreate_dttm(rs.getString("create_dttm"));

			log.info("END: *** getFileAttachment Result *****" + obj.toString());
		} catch (NullPointerException e) {
			log.info("exception: getFileAttachment ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getFileAttachment ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getFileAttachment  DAO  END");
		}
		return obj;
	}

	@Override
	public Result saveFileAttachment(AttachmentFileValueObject attObj) throws BusinessException {
		StringBuilder sb = null;
		Result result = new Result();
//		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: saveFileAttachment DAo attObj;" + attObj.toString());
			sb = new StringBuilder();
			if (attObj.getMisc_seq_nbr() != null && attObj.getUpdateInd().equalsIgnoreCase("U")) {
				sb.append("update ");
				sb.append("	misc_upload_doc ");
				sb.append(" set ");
				sb.append("	doc_type =:docType, ");
				sb.append("	upload_file_nm =:uploadFileNm, ");
				sb.append("	assign_file_nm =:assignFileNm, ");
				sb.append("	create_user_id =:userId, ");
				sb.append("	create_dttm = SYSDATE ");
				sb.append("where ");
				sb.append("	misc_seq_nbr =:miscSeqNbr");
			}

			else if (attObj.getMisc_seq_nbr() != null && attObj.getUpdateInd().equalsIgnoreCase("D")) {
				sb.append("delete from misc_upload_doc where misc_seq_nbr=:miscSeqNbr");
			} else {
				sb.append(" insert into misc_upload_doc (misc_seq_nbr, doc_type, upload_file_nm, ");
				sb.append(" assign_file_nm, create_user_id, create_dttm) values (");
				sb.append(":misc_seq_nbr,:doc_type,:upload_file_nm,:assign_file_nm,:create_user_id,sysdate) ");

			}
			log.info("SQL" + sb.toString());
			int update = namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(attObj));
			if (update != 0) {

				result.setSuccess(true);
				result.setData(attObj);
			}
			log.info("END: saveFileAttachment DAO Result:" + result.toString());
		} catch (Exception e) {
			log.info("Exception saveFileAttachment: ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: saveFileAttachment DAO");
		}
		return result;
	}

	@Override
	public List<AttachmentFileValueObject> getFileAttachmentList(String miscSeqNumber, String string)
			throws BusinessException {

		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<AttachmentFileValueObject> listObj = new ArrayList<AttachmentFileValueObject>();
		AttachmentFileValueObject obj = new AttachmentFileValueObject();
		try {
			log.info("START: getFileAttachmentList Dao Start userId:" + CommonUtility.deNull(string) + " miscSeqNumber:"
					+ CommonUtility.deNull(miscSeqNumber));

			sb.append("select misc_seq_nbr, doc_type, upload_file_nm, ");
			sb.append(" assign_file_nm, create_user_id, create_dttm from misc_upload_doc");
			sb.append("where misc_seq_nbr=:miscSeqNumber");

			paramMap.put("miscSeqNumber", miscSeqNumber);
			log.info(" ***getFileAttachmentList SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next())
				obj.setAssign_file_nm("assign_file_nm");
			obj.setUpload_file_nm("upload_file_nm");
			obj.setMisc_seq_nbr("misc_seq_nbr");
			obj.setDoc_type("doc_type");
			obj.setCreate_user_id("create_user_id");
			obj.setCreate_dttm("create_dttm");

			listObj.add(obj);

			log.info("END: *** getFileAttachmentList Result *****" + listObj.toString());
		} catch (NullPointerException e) {
			log.info("exception: getFileAttachmentList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getFileAttachmentList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getFileAttachmentList  DAO  END");
		}
		return listObj;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getUserName()
	@Override
	public String getUserName(String userId) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String userName = "";
		try {
			log.info("START: getUserName Dao Start userId:" + CommonUtility.deNull(userId));

			sb.append(" select /* MiscAppEJB - getUserName */ user_nm from logon_acct ");
			sb.append(" where login_id =UPPER(:userId) ");

			paramMap.put("userId", userId);
			log.info(" ***getUserName SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next())
				userName = rs.getString("user_nm");

			log.info("END: *** getUserName Result *****" + userName.toString());
		} catch (NullPointerException e) {
			log.info("exception: getUserName ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getUserName ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getUserName  DAO  END");
		}
		return userName;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getOvernightParkingVehList()

	/**
	 * This method is to get a list of overnight parking vehicles
	 *
	 * @param fromDate - creation from date
	 * @param toDate   - creation to date
	 *
	 * @return list of vehicles
	 * @throws BusinessException
	 */
	// Added by Dong Sheng on 3/1/2011 for CR-OPS-20110110-09

	@Override
	public List<OvrNghtPrkgVehValueObject> getOvernightParkingVehList(String type) throws BusinessException {
		List<OvrNghtPrkgVehValueObject> vehicleList = new ArrayList<OvrNghtPrkgVehValueObject>();
		int i = 0;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getOvernightParkingVehList  DAO  Start type:" + CommonUtility.deNull(type));

			// Get the vehicle list based on date time range
			OvrNghtPrkgVehValueObject ovrNghtPrkgVehValueObject = null;

			// sb.append(" SELECT UPPER(D.VEH_CHAS_NBR) VEH_NBR, ");
			// sb.append(" (CASE WHEN NBR_NIGHT>0 THEN TO_CHAR(V.FR_DTTM,'DDMMYYYY')||'
			// 2359' ");
			// sb.append(" ELSE TO_CHAR(V.FR_DTTM,'DDMMYYYY HH24MI') END) FR_DTTM, ");
			// sb.append(" (CASE WHEN NBR_NIGHT>0 THEN TO_CHAR(V.TO_DTTM,'DDMMYYYY')||'
			// 0700' ");
			// sb.append(" ELSE TO_CHAR(V.TO_DTTM,'DDMMYYYY HH24MI') END) TO_DTTM, ");
			// sb.append(" M.MISC_TYPE_NM APPLN_STATUS, C.CO_NM, A.ACCT_NBR,
			// A.CONTACT_PERSON, ");
			// sb.append(" A.CONTACT_TEL, A.REF_NBR ");
			// sb.append(" FROM MISC_VEHICLE_DET D, MISC_VEHICLE V, MISC_APP A,
			// MISC_TYPE_CODE M, COMPANY_CODE C ");
			// sb.append(" WHERE SYSDATE BETWEEN ((CASE WHEN NBR_NIGHT>0 THEN ");
			// sb.append(" TO_DATE(TO_CHAR(V.FR_DTTM,'DDMMYYYY')||' 2359','DDMMYYYY HH24MI')
			// ");
			// sb.append(" ELSE V.FR_DTTM END)-((NVL((SELECT VALUE FROM SYSTEM_PARA WHERE
			// PARA_CD='TRA_F'),20000))/24)) ");
			// sb.append(" AND ((CASE WHEN NBR_NIGHT>0 THEN
			// TO_DATE(TO_CHAR(V.TO_DTTM,'DDMMYYYY')||' 0700','DDMMYYYY HH24MI') ");
			// sb.append(" ELSE V.TO_DTTM END)+((NVL((SELECT VALUE FROM SYSTEM_PARA WHERE
			// PARA_CD='TRA_T'),20000))/24)) ");
			// sb.append(" AND D.MISC_SEQ_NBR=V.MISC_SEQ_NBR AND
			// V.MISC_SEQ_NBR=A.MISC_SEQ_NBR AND A.APP_TYPE ='ONV' ");
			// sb.append(" AND M.CAT_CD='MISC_STAT' AND A.APP_STATUS=M.MISC_TYPE_CD (+) AND
			// A.CUST_CD=C.CO_CD ");
			// sb.append(" ORDER BY UPPER(D.VEH_CHAS_NBR) ");

			sb.append(" SELECT UPPER(D.VEH_CHAS_NBR) VEH_NBR, DECODE(A.APP_TYPE,'TPA', ");
			sb.append(" AREA_CD|| ' / ' || SLOT_NBR,'') ASSIGNED_SLOT, ");
			sb.append(" (CASE WHEN NBR_NIGHT>0 THEN TO_CHAR(NVL(V.ACTUAL_FR_DTTM,V.FR_DTTM), ");
			sb.append(" 'DDMMYYYY')||' 2359' ELSE TO_CHAR(NVL(V.ACTUAL_FR_DTTM,V.FR_DTTM), ");
			sb.append(" 'DDMMYYYY HH24MI') END) FR_DTTM, ");
			sb.append(" (CASE WHEN NBR_NIGHT>0 THEN TO_CHAR(NVL(V.ACTUAL_TO_DTTM,V.TO_DTTM), ");
			sb.append(" 'DDMMYYYY')||' 0700' ELSE TO_CHAR(NVL(V.ACTUAL_TO_DTTM,V.TO_DTTM), ");
			sb.append(" 'DDMMYYYY HH24MI') END) TO_DTTM, ");
			sb.append(" M.MISC_TYPE_NM APPLN_STATUS, C.CO_NM, A.ACCT_NBR, ");
			sb.append(" A.CONTACT_PERSON, A.CONTACT_TEL, A.REF_NBR ");
			sb.append(" FROM MISC_VEHICLE_DET D, MISC_VEHICLE V, MISC_APP A, ");
			sb.append(" MISC_TYPE_CODE M, COMPANY_CODE C ");
			sb.append(" WHERE SYSDATE BETWEEN ");
			// sb.append(" BETWEEN ((CASE WHEN NBR_NIGHT>0 THEN
			// TO_DATE(TO_CHAR(NVL(V.ACTUAL_FR_DTTM,V.FR_DTTM),'DDMMYYYY')||'
			// 2359','DDMMYYYY HH24MI') ELSE NVL(V.ACTUAL_FR_DTTM,V.FR_DTTM)
			// END)-((NVL((SELECT VALUE FROM SYSTEM_PARA WHERE PARA_CD='TRA_F'),2))/24)) ");
			// sb.append(" AND ((CASE WHEN NBR_NIGHT>0 THEN
			// TO_DATE(TO_CHAR(NVL(V.ACTUAL_TO_DTTM,V.TO_DTTM),'DDMMYYYY')||'
			// 0700','DDMMYYYY HH24MI') ELSE NVL(V.ACTUAL_TO_DTTM,V.TO_DTTM)
			// END)+((NVL((SELECT VALUE FROM SYSTEM_PARA WHERE PARA_CD='TRA_T'),2))/24)) ");
			sb.append("((CASE WHEN NBR_NIGHT>0 THEN (TO_DATE(TO_CHAR(NVL(V.ACTUAL_FR_DTTM, ");
			sb.append(" V.FR_DTTM),'DDMMYYYY')||' 2359','DDMMYYYY HH24MI')-((NVL((SELECT ");
			sb.append(" VALUE FROM SYSTEM_PARA WHERE PARA_CD='TRA_F'),2))/24)) ");
			sb.append(" ELSE NVL(V.ACTUAL_FR_DTTM,V.FR_DTTM) END)) AND ");
			sb.append(" ((CASE WHEN NBR_NIGHT>0 THEN (TO_DATE(TO_CHAR(NVL(V.ACTUAL_TO_DTTM, ");
			sb.append(" V.TO_DTTM),'DDMMYYYY')||' 0700','DDMMYYYY HH24MI')+((NVL((SELECT ");
			sb.append(" VALUE FROM SYSTEM_PARA WHERE PARA_CD='TRA_T'),2))/24)) ");
			sb.append(" ELSE NVL(V.ACTUAL_TO_DTTM,V.TO_DTTM) END)) ");
			sb.append(" AND D.MISC_SEQ_NBR=V.MISC_SEQ_NBR AND V.MISC_SEQ_NBR=A.MISC_SEQ_NBR ");
			sb.append(" AND A.APP_TYPE =:type AND M.CAT_CD='MISC_STAT' ");
			sb.append(" AND A.APP_STATUS=M.MISC_TYPE_CD (+) AND A.CUST_CD=C.CO_CD ");
			sb.append(" ORDER BY UPPER(D.VEH_CHAS_NBR) ");

			log.info(" ***getOvernightParkingVehList SQL *****" + sb.toString());

			paramMap.put("type", type);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				ovrNghtPrkgVehValueObject = new OvrNghtPrkgVehValueObject();
				i++;
				ovrNghtPrkgVehValueObject.setSNo(i);
				ovrNghtPrkgVehValueObject.setVehNbr(CommonUtility.deNull(rs.getString("VEH_NBR")));
				ovrNghtPrkgVehValueObject.setFromDate(CommonUtility.deNull(rs.getString("FR_DTTM")));
				ovrNghtPrkgVehValueObject.setToDate(CommonUtility.deNull(rs.getString("TO_DTTM")));
				ovrNghtPrkgVehValueObject.setStatus(CommonUtility.deNull(rs.getString("APPLN_STATUS")));
				ovrNghtPrkgVehValueObject.setCompanyName(CommonUtility.deNull(rs.getString("CO_NM")));
				ovrNghtPrkgVehValueObject.setAcctNbr(CommonUtility.deNull(rs.getString("ACCT_NBR")));
				ovrNghtPrkgVehValueObject.setContactPerson(CommonUtility.deNull(rs.getString("CONTACT_PERSON")));
				ovrNghtPrkgVehValueObject.setContactNbr(CommonUtility.deNull(rs.getString("CONTACT_TEL")));
				ovrNghtPrkgVehValueObject.setRefNbr(CommonUtility.deNull(rs.getString("REF_NBR")));
				ovrNghtPrkgVehValueObject.setAreaCode(CommonUtility.deNull(rs.getString("ASSIGNED_SLOT")));
				vehicleList.add(ovrNghtPrkgVehValueObject);
			}
			log.info("=============== getOvernightParkingVehList() - vehicleList.Size:" + vehicleList.size());
			log.info("END: *** getOvernightParkingVehList Result *****" + vehicleList.toString());
			return vehicleList;
		} catch (NullPointerException e) {
			log.info("exception: getOvernightParkingVehList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getOvernightParkingVehList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getOvernightParkingVehList  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getApplicationList()
	// List Applications
	@Override
	public TableResult getApplicationList(String coCd, String appType, String refNo, String appStatus,
			String appFromDttm, String appToDttm, String payMode, String companyCD, String machineNo, String vehicleNo,
			String containerNo, Criteria criteria, Boolean allData) throws BusinessException {
		List<MiscAppValueObject> appList = new ArrayList<MiscAppValueObject>();
		MiscAppValueObject miscAppObj;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		String sql = "";
		try {
			log.info("START: getApplicationList  DAO  Start coCd:" + CommonUtility.deNull(coCd) + "appType:"
					+ CommonUtility.deNull(appType) + "refNo:" + CommonUtility.deNull(refNo) + "appStatus:"
					+ CommonUtility.deNull(appStatus) + "appFromDttm:" + CommonUtility.deNull(appFromDttm)
					+ "appToDttm:" + CommonUtility.deNull(appToDttm) + "payMode:" + CommonUtility.deNull(payMode)
					+ "companyCD:" + CommonUtility.deNull(companyCD) + "machineNo:" + CommonUtility.deNull(machineNo)
					+ "vehicleNo:" + CommonUtility.deNull(vehicleNo) + "containerNo:"
					+ CommonUtility.deNull(containerNo));

			if (coCd != null && coCd.equals("JP")) {
				sb.append(" select /* MiscAppEJB - getApplicationList() */ a.misc_seq_nbr, ");
				sb.append(" a.ref_nbr, d.misc_type_nm, ");
				// "a.app_type, to_char(a.app_dttm,'ddmmyyyy HH24mi') app_dttm, a.app_status, "
				sb.append(" a.app_type, app_dttm, a.app_status, ");
				sb.append(" m.misc_type_nm appStatusName, c.co_nm,c.co_cd, a.bill_nbr ");
				// Added by Dong Sheng on 28/12/2010 for Overnight Parking Application CR.
				// ", DECODE(A.APP_TYPE,'ONV',(SELECT COUNT(*) FROM MISC_VEHICLE_DET V WHERE
				// V.MISC_SEQ_NBR=A.MISC_SEQ_NBR)," +
				sb.append(" , (CASE WHEN A.APP_TYPE IN ('ONV','TPA') THEN (SELECT TO_CHAR(COUNT(*)) ");
				sb.append(" FROM MISC_VEHICLE_DET V WHERE V.MISC_SEQ_NBR=A.MISC_SEQ_NBR) ");
				// START 17-Mar-2011 - TPA - Thanhnv2 change for TPA.
				// " 'TPA',(SELECT COUNT(*) FROM MISC_VEHICLE_DET V WHERE
				// V.MISC_SEQ_NBR=A.MISC_SEQ_NBR),'') NO_OF_VEH " +
				sb.append(" WHEN A.APP_TYPE IN ('ONE','STE') THEN (SELECT TO_CHAR(COUNT(*)) ");
				sb.append(" FROM MISC_MACHINE_DET V WHERE V.MISC_SEQ_NBR=A.MISC_SEQ_NBR) ");
				sb.append(" ELSE '' END) NO_OF_VEH ");
				// END 17-Mar-2011 - TPA - Thanhnv2 change for TPA.
				sb.append(" from misc_app a, company_code c, misc_type_code d, misc_type_code m ");
				sb.append(" where d.cat_cd = 'MISC_APP' and a.app_type = d.misc_type_cd ");
				sb.append(" and c.co_cd = a.cust_cd ");
				sb.append(" and m.cat_cd = 'MISC_STAT' and a.app_status = m.misc_type_cd ");
				// Commented on 12/05/2008 by Punitha
				// "and c.rec_status = 'A' ";
				// End by Punitha
				if (companyCD != null && !companyCD.equalsIgnoreCase("")) {
					sb.append(" AND a.cust_cd =:companyCD ");
				}

			} else {
				sb.append(" select /* MiscAppEJB - getApplicationList() */ a.misc_seq_nbr, a.ref_nbr, ");
				sb.append(" d.misc_type_nm, ");
				// "a.app_type, to_char(a.app_dttm,'ddmmyyyy HH24mi') app_dttm, a.app_status, "
				// +
				sb.append(" a.app_type, app_dttm, a.app_status, ");
				sb.append(" m.misc_type_nm appStatusName, c.co_nm,c.co_cd, a.bill_nbr ");
				// Added by Dong Sheng on 28/12/2010 for Overnight Parking Application CR.
				// ", DECODE(A.APP_TYPE,'ONV',(SELECT COUNT(*) FROM MISC_VEHICLE_DET V WHERE
				// V.MISC_SEQ_NBR=A.MISC_SEQ_NBR)" +
				sb.append(" , (CASE WHEN A.APP_TYPE IN ('ONV','TPA') THEN (SELECT ");
				sb.append(" TO_CHAR(COUNT(*)) FROM MISC_VEHICLE_DET V WHERE ");
				sb.append(" V.MISC_SEQ_NBR=A.MISC_SEQ_NBR) ");
				// ", 'TPA',(SELECT COUNT(*) FROM MISC_VEHICLE_DET V WHERE
				// V.MISC_SEQ_NBR=A.MISC_SEQ_NBR),'') NO_OF_VEH " +
				sb.append(" WHEN A.APP_TYPE IN ('ONE','STE') THEN (SELECT TO_CHAR(COUNT(*)) ");
				sb.append(" FROM MISC_MACHINE_DET V WHERE V.MISC_SEQ_NBR=A.MISC_SEQ_NBR) ");
				sb.append(" ELSE '' END) NO_OF_VEH ");
				sb.append(" from misc_app a,  company_code c, misc_type_code d, misc_type_code m ");
				sb.append(" where d.cat_cd = 'MISC_APP' and a.app_type = d.misc_type_cd ");
				sb.append(" and a.cust_cd =:coCd and  c.co_cd = a.cust_cd ");
				sb.append(" and m.cat_cd = 'MISC_STAT' and a.app_status = m.misc_type_cd ");
				// Commented on 12/05/2008 by Punitha
				// "and c.rec_status = 'A' ";
				// End by Punitha

			}
			if (appType != null && !appType.equals("")) {
				// Add by ZanFeng for new search condition ::start 26/01/2011
				if ((machineNo != null && !"".equals(machineNo)) && ("STE".equals(appType) || "ONE".equals(appType))) {
					machineNo = machineNo.trim();

					sb.append(" and a.misc_seq_nbr in (select MA.MISC_SEQ_NBR from misc_app ma, ");
					sb.append(" misc_machine_det mmd ");
					sb.append(" where ma.app_type in ('STE','ONE') and MA.MISC_SEQ_NBR = ");
					sb.append(" MMD.MISC_SEQ_NBR ");
					sb.append(" and upper(REPLACE(mmd.REG_NBR,' ','')) like upper(:machineNo) || '%') ");

				} else if ((vehicleNo != null && !"".equals(vehicleNo))
						&& (containerNo != null && !"".equals(containerNo))) {
					vehicleNo = vehicleNo.trim();
					containerNo = containerNo.trim();
					if (appType.equalsIgnoreCase("TPA")) {

						sb.append(" and a.misc_seq_nbr in (SELECT MA.MISC_SEQ_NBR FROM MISC_APP MA, ");
						sb.append(" MISC_VEHICLE_DET MVD ");
						sb.append(" WHERE MA.APP_TYPE IN ('TPA') AND MA.MISC_SEQ_NBR = ");
						sb.append(" MVD.MISC_SEQ_NBR ");
						sb.append(" AND UPPER(REPLACE(MVD.VEH_CHAS_NBR,' ','')) LIKE ");
						sb.append(" UPPER(:vehicleNo ) || '%' INTERSECT ");
						sb.append(" SELECT MA.MISC_SEQ_NBR FROM MISC_APP MA, MISC_VEHICLE_DET MVD ");
						sb.append(" WHERE MA.APP_TYPE IN ('TPA') AND MA.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
						sb.append(" AND UPPER(REPLACE(MVD.CNTR_NBR,' ','')) = UPPER(:containerNo)) ");

					} else if (appType.equalsIgnoreCase("ONV")) {

						sb.append(" and a.misc_seq_nbr in (SELECT MA.MISC_SEQ_NBR FROM ");
						sb.append(" MISC_APP MA, MISC_VEHICLE_DET MVD ");
						sb.append(" WHERE MA.APP_TYPE IN ('ONV') AND MA.MISC_SEQ_NBR = ");
						sb.append(" MVD.MISC_SEQ_NBR ");
						sb.append(" AND UPPER(REPLACE(MVD.VEH_CHAS_NBR,' ','')) LIKE ");
						sb.append(" UPPER(:vehicleNo) || '%' INTERSECT ");
						sb.append(" SELECT MA.MISC_SEQ_NBR FROM MISC_APP MA, MISC_VEHICLE_DET MVD ");
						sb.append(" WHERE MA.APP_TYPE IN ('ONV') AND MA.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
						sb.append(" AND UPPER(REPLACE(MVD.CNTR_NBR,' ','')) = UPPER(:containerNo)) ");

					} else if (appType.equalsIgnoreCase("SPA")) {

						sb.append(" and a.misc_seq_nbr in (SELECT MA.MISC_SEQ_NBR FROM ");
						sb.append(" MISC_APP MA, CNTR C, PREGATE P ");
						sb.append(" WHERE MA.APP_TYPE = 'SPA' AND MA.REF_NBR = C.MISC_APP_NBR ");
						sb.append(" AND C.TXN_STATUS <> 'D' ");
						sb.append(" AND C.CNTR_SEQ_NBR = P.CNTR_SEQ_NBR AND P.LICENSE_PLATE_NBR ");
						sb.append(" LIKE UPPER(:vehicleNo) || '%' INTERSECT ");
						sb.append(" SELECT MA.MISC_SEQ_NBR FROM MISC_APP MA, CNTR C ");
						sb.append(" WHERE MA.APP_TYPE = 'SPA' AND MA.REF_NBR = ");
						sb.append(" C.MISC_APP_NBR AND C.TXN_STATUS <> 'D' ");
						sb.append(" AND C.CNTR_NBR = UPPER(:containerNo)) ");

					}

				} else if ((vehicleNo != null && !"".equals(vehicleNo))
						&& ("SPA".equals(appType) || "ONV".equals(appType) || "TPA".equals(appType))) {
					vehicleNo = vehicleNo.trim();

					sb.append(" and a.misc_seq_nbr in (SELECT MA.MISC_SEQ_NBR FROM MISC_APP MA, ");
					sb.append(" MISC_VEHICLE_DET MVD ");
					sb.append(" WHERE MA.APP_TYPE IN ('ONV') AND MA.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
					sb.append(" AND UPPER(REPLACE(MVD.VEH_CHAS_NBR,' ','')) LIKE ");
					sb.append(" UPPER(:vehicleNo) || '%' UNION ");
					sb.append(" SELECT MA.MISC_SEQ_NBR FROM MISC_APP MA, MISC_VEHICLE_DET MVD ");
					sb.append(" WHERE MA.APP_TYPE IN ('TPA') AND MA.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
					sb.append(" AND UPPER(REPLACE(MVD.VEH_CHAS_NBR,' ','')) LIKE ");
					sb.append(" UPPER(:vehicleNo) || '%' UNION ");
					sb.append(" SELECT MA.MISC_SEQ_NBR FROM MISC_APP MA, CNTR C, PREGATE P ");
					sb.append(" WHERE MA.APP_TYPE = 'SPA' AND MA.REF_NBR = C.MISC_APP_NBR ");
					sb.append(" AND C.TXN_STATUS <> 'D' ");
					sb.append(" AND C.CNTR_SEQ_NBR = P.CNTR_SEQ_NBR AND P.LICENSE_PLATE_NBR ");
					sb.append(" LIKE UPPER(:vehicleNo) || '%') ");

				} else if ((containerNo != null && !"".equals(containerNo)) && ("SPA".equals(appType)
						|| "ONV".equals(appType) || "TPA".equals(appType) || "ELE".equals(appType))) {
					containerNo = containerNo.trim();

					sb.append(" and a.misc_seq_nbr in (SELECT MA.MISC_SEQ_NBR FROM MISC_APP MA, ");
					sb.append(" MISC_VEHICLE_DET MVD ");
					sb.append(" WHERE MA.APP_TYPE IN ('ONV') AND MA.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
					sb.append(" AND UPPER(REPLACE(MVD.CNTR_NBR,' ','')) = UPPER(:containerNo) UNION ");
					sb.append(" SELECT MA.MISC_SEQ_NBR FROM MISC_APP MA, MISC_VEHICLE_DET MVD ");
					sb.append(" WHERE MA.APP_TYPE IN ('TPA') AND MA.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
					sb.append(" AND UPPER(REPLACE(MVD.CNTR_NBR,' ','')) = UPPER(:containerNo) UNION ");
					sb.append(" SELECT MA.MISC_SEQ_NBR FROM MISC_APP MA, MISC_REEFER_DET MRD ");
					sb.append(" WHERE MA.APP_TYPE = 'ELE' AND MA.MISC_SEQ_NBR = MRD.MISC_SEQ_NBR ");
					sb.append(" AND UPPER(REPLACE(MRD.CNTR_NBR,' ','')) = UPPER(:containerNo) UNION ");
					sb.append(" SELECT MA.MISC_SEQ_NBR FROM MISC_APP MA, CNTR C ");
					sb.append(" WHERE MA.APP_TYPE = 'SPA' AND MA.REF_NBR = C.MISC_APP_NBR ");
					sb.append(" AND C.TXN_STATUS <> 'D' ");
					sb.append(" AND C.CNTR_NBR = UPPER(:containerNo)) ");

				} else {

				}
				// Add by ZanFeng for new search condition ::end 26/01/2011
				sb.append(" and a.app_type =:appType ");

			}
			// Add by ZanFeng for new search condition ::start 26/01/2011
			/*
			 * if(companyCD != null && !"".equals(companyCD)){ sql = sql +
			 * " and c.co_cd = ' "); companyCD +"' "; }
			 */
			// Add by ZanFeng for new search condition ::end 26/01/2011
			if (refNo != null && !refNo.equals(""))
				sb.append(" and a.ref_nbr =:refNo ");
			if (appStatus != null && !appStatus.equals(""))
				sb.append(" and a.app_status =:appStatus ");
			if ((appFromDttm != null && !appFromDttm.equals("")) || (appToDttm != null && !appToDttm.equals(""))) {
				sb.append(" and a.app_dttm between to_date(:appFromDttm,'dd/mm/yyyy HH24mi') and ");
				sb.append(" to_date(:appToDttm,'dd/mm/yyyy HH24mi') ");
			}

			if ("A".equals(payMode))
				sb.append(" and a.acct_nbr IS NOT NULL ");
			if ("C".equals(payMode))
				sb.append(" and a.acct_nbr IS NULL ");

			sb.append(" order by app_dttm desc, a.app_type, c.co_nm, a.ref_nbr ");

			// log.info("~~~~~~~~~~~ Sql : " + sql);
			// log.info("~~~~~~~~~~~ appType : " + appType);
			// log.info("~~~~~~~~~~~ ref_nbr : *" + refNo + "*");

			log.info(" ***getApplicationList SQL *****" + sb.toString());

			if (coCd != null && coCd.equals("JP")) {
				if (companyCD != null && !companyCD.equalsIgnoreCase("")) {
					paramMap.put("companyCD", companyCD);
				}
			} else {
				paramMap.put("coCd", coCd);
			}
			if (appType != null && !appType.equals("")) {
				if ((machineNo != null && !"".equals(machineNo)) && ("STE".equals(appType) || "ONE".equals(appType))) {
					machineNo = machineNo.trim();
					paramMap.put("machineNo", machineNo);
				} else if ((vehicleNo != null && !"".equals(vehicleNo))
						&& (containerNo != null && !"".equals(containerNo))) {
					vehicleNo = vehicleNo.trim();
					containerNo = containerNo.trim();
					if (appType.equalsIgnoreCase("TPA")) {
						paramMap.put("vehicleNo", vehicleNo);
						paramMap.put("containerNo", containerNo);
					} else if (appType.equalsIgnoreCase("ONV")) {
						paramMap.put("vehicleNo", vehicleNo);
						paramMap.put("containerNo", containerNo);
					} else if (appType.equalsIgnoreCase("SPA")) {
						paramMap.put("vehicleNo", vehicleNo);
						paramMap.put("containerNo", containerNo);
					}

				} else if ((vehicleNo != null && !"".equals(vehicleNo))
						&& ("SPA".equals(appType) || "ONV".equals(appType) || "TPA".equals(appType))) {
					vehicleNo = vehicleNo.trim();
					paramMap.put("vehicleNo", vehicleNo);
				} else if ((containerNo != null && !"".equals(containerNo)) && ("SPA".equals(appType)
						|| "ONV".equals(appType) || "TPA".equals(appType) || "ELE".equals(appType))) {
					containerNo = containerNo.trim();
					paramMap.put("containerNo", containerNo);
				}
				paramMap.put("appType", appType);
			}
			if (refNo != null && !refNo.equals("")) {
				paramMap.put("refNo", refNo);
			}
			if (appStatus != null && !appStatus.equals("")) {
				paramMap.put("appStatus", appStatus);
			}
			if ((appFromDttm != null && !appFromDttm.equals("")) || (appToDttm != null && !appToDttm.equals(""))) {
				String a = appFromDttm + " 0000";
				String b = appToDttm + " 2359";
				paramMap.put("appFromDttm", a);
				paramMap.put("appToDttm", b);
			}

			if (allData == false) {
				if (criteria.isPaginated()) {
					sql = CommonUtil.getPaginatedSql(sb.toString(), criteria.getStart(), criteria.getLimit());

				}
				if (criteria.isPaginated()) {
					tableData.setTotal(namedParameterJdbcTemplate
							.queryForObject("SELECT COUNT(*) FROM (" + sb.toString() + ")", paramMap, Integer.class));
					log.info("filter.total=" + tableData.getTotal());
				}

			} else {
				sql = sb.toString();
			}

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			int i = 1;
			while (rs.next()) {
				miscAppObj = new MiscAppValueObject();
				miscAppObj.setSNo(i + "");
				miscAppObj.setAppSeqNbr(CommonUtility.deNull(rs.getString("misc_seq_nbr")));
				miscAppObj.setAppRefNbr(CommonUtility.deNull(rs.getString("ref_nbr")));
				// log.info("~~~~~~~~~~~ ref_nbr : *" +
				// miscAppObj.getAppRefNbr());
				miscAppObj.setAppTypeCd(CommonUtility.deNull(rs.getString("app_type")));
				miscAppObj.setAppTypeName(CommonUtility.deNull(rs.getString("misc_type_nm")));
				// miscAppObj.setAppDttm(CommonUtility.deNull(rs.getString("app_dttm")));
				miscAppObj.setAppDttm(CommonUtility.parseDateToFmtStr(rs.getTimestamp("app_dttm"), "ddMMyyyy HHmm"));
				miscAppObj.setCoName(CommonUtility.deNull(rs.getString("co_nm")));
				miscAppObj.setAppStatusCd(CommonUtility.deNull(rs.getString("app_status")));
				miscAppObj.setAppStatusName(CommonUtility.deNull(rs.getString("appStatusName")));
				miscAppObj.setBillNbr(CommonUtility.deNull(rs.getString("bill_nbr")));
				// Added by Dong Sheng on 28/12/2010 for Overnight Parking Application CR.
				miscAppObj.setNbrOfVehicle(CommonUtility.deNull(rs.getString("NO_OF_VEH")));
				miscAppObj.setCoCd(CommonUtility.deNull(rs.getString("co_cd")));
				appList.add(miscAppObj);
				i++;
			}

			// Added by Punitha on 06/05/2008.
			miscAppObj = new MiscAppValueObject();
			for (int j = 0; j < appList.size(); j++) {
				miscAppObj = (MiscAppValueObject) appList.get(j);
				if (miscAppObj.getAppTypeCd().equalsIgnoreCase("CTP")) {
					String waiveInd = CommonUtility.deNull(getWaiveIndicator(miscAppObj.getAppSeqNbr()));
					if (waiveInd.equals("Y")) {
						miscAppObj.setBillNbr("Waived");
					}
				}

			}
			for (MiscAppValueObject object : appList) {
				topsModel.put(object);
			}

			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);

			log.info("END: *** getApplicationList Result *****" + appList.size());

		} catch (BusinessException e) {
			log.info("exception: getApplicationList ", e);
			throw new BusinessException("M4201");
		} catch (NullPointerException e) {
			log.info("exception: getApplicationList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getApplicationList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getApplicationList  DAO  END");
		}
		return tableResult;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->checkApprovePermission()
	@Override
	public int checkApprovePermission(String loginId, String appType) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		int count = 0;

		try {
			log.info("START: checkApprovePermission  DAO  Start loginId:" + CommonUtility.deNull(loginId) + "appType:"
					+ CommonUtility.deNull(appType));

			sb.append(" SELECT COUNT(*) FROM DEPT_ACCT D, TEXT_PARA T WHERE ");
			sb.append(" D.LOGIN_ID=:loginId AND D.DEPT_ID=T.VALUE ");
			sb.append(" AND T.PARA_CD LIKE :appType ");

			log.info(" ***checkApprovePermission SQL *****" + sb.toString());
			paramMap.put("loginId", loginId);
			paramMap.put("appType", "DEPT_" + appType + "%");

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next())
				count = rs.getInt(1);

			log.info("END: *** checkApprovePermission Result *****" + count);
		} catch (NullPointerException e) {
			log.info("exception: checkApprovePermission ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: checkApprovePermission ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkApprovePermission  DAO  END");
		}
		return count;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getWaiveIndicator()
	// Added by Punitha on 06/05/2008.To retrieve the waive Indicator
	public String getWaiveIndicator(String appSeqNbr) throws BusinessException {
		// log.info("<========= Start getWaiveIndicator() ========>
		// ");
		String waiveInd = null;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getWaiveIndicator  DAO  Start appSeqNbr:" + CommonUtility.deNull(appSeqNbr));

			sb.append(" select /* MiscAppEJB - getWaiveIndicator */ waive_ind ");
			sb.append(" from misc_contractor ");
			sb.append(" where misc_seq_nbr =:appSeqNbr ");

			log.info(" ***getWaiveIndicator SQL *****" + sb.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				waiveInd = rs.getString("waive_ind");
			}

			log.info("END: *** getWaiveIndicator Result *****" + waiveInd.toString());
			// log.info("<========= End getWaiveIndicator() ========> ");
		} catch (NullPointerException e) {
			log.info("exception: getWaiveIndicator ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getWaiveIndicator ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getWaiveIndicator  DAO  END");
		}
		return waiveInd;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getApplicationTypeList()
	@Override
	public List<MiscAppValueObject> getApplicationTypeList() throws BusinessException {
		List<MiscAppValueObject> appList = new ArrayList<MiscAppValueObject>();
		MiscAppValueObject miscAppObj;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getApplicationTypeList  DAO  Start");

			sb.append(" select /* MiscAppEJB - getApplicationTypeList() */ ");
			sb.append(" misc_type_cd, misc_type_nm ");
			sb.append(" from misc_type_code where cat_cd = 'MISC_APP' ");
			sb.append(" and rec_status = 'A' order by misc_type_nm ");

			log.info(" ***getApplicationTypeList SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				miscAppObj = new MiscAppValueObject();
				miscAppObj.setAppTypeCd(rs.getString("misc_type_cd"));
				miscAppObj.setAppTypeName(rs.getString("misc_type_nm"));
				appList.add(miscAppObj);
			}

			log.info("END: *** getApplicationTypeList Result *****" + appList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getApplicationTypeList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getApplicationTypeList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getApplicationTypeList  DAO  END");
		}
		return appList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getCompanyList1()
	@Override
	public List<CompanyValueObject> getCompanyList1() throws BusinessException {
		List<CompanyValueObject> companyList = new ArrayList<CompanyValueObject>();
		CompanyValueObject companyObj;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getCompanyList1  DAO  Start");

			sb.append(" select co_nm, co_cd from company_code where co_cd in ");
			sb.append(" (select cust_cd from misc_app group by cust_cd) order by co_nm ");

			log.info(" ***getCompanyList1 SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				companyObj = new CompanyValueObject();
				companyObj.setCompanyCode(rs.getString("co_cd"));
				companyObj.setCompanyName(rs.getString("co_nm"));
				companyList.add(companyObj);
			}

			log.info("END: *** getCompanyList1 Result *****" + companyList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getCompanyList1 ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getCompanyList1 ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCompanyList1  DAO  END");
		}
		return companyList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getApplicationStatusList()
	@Override
	public List<MiscAppValueObject> getApplicationStatusList() throws BusinessException {
		List<MiscAppValueObject> statusList = new ArrayList<MiscAppValueObject>();
		MiscAppValueObject miscAppObj;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getApplicationStatusList  DAO  Start");

			sb.append(" select /* MiscAppEJB - getApplicationStatusList() */ ");
			sb.append(" misc_type_cd, misc_type_nm ");
			sb.append(" from misc_type_code where cat_cd = 'MISC_STAT' ");
			sb.append(" and rec_status = 'A' order by last_modify_dttm ");

			log.info(" ***getApplicationStatusList SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				miscAppObj = new MiscAppValueObject();
				miscAppObj.setAppStatusCd(rs.getString("misc_type_cd"));
				miscAppObj.setAppStatusName(rs.getString("misc_type_nm"));
				statusList.add(miscAppObj);
			}
			log.info("END: *** getApplicationStatusList Result *****" + statusList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getApplicationStatusList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getApplicationStatusList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getApplicationStatusList  DAO  END");
		}
		return statusList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getPurposeList()
	@Override
	public List<MiscAppValueObject> getPurposeList() throws BusinessException {
		List<MiscAppValueObject> purposeList = new ArrayList<MiscAppValueObject>();
		MiscAppValueObject miscAppObj;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getPurposeList  DAO  Start");

			sb.append(" select /* MiscAppEJB - getPurposeList() */ ");
			sb.append(" misc_type_cd, misc_type_nm ");
			sb.append(" from misc_type_code where cat_cd = 'MISC_PURP' ");
			sb.append(" and rec_status = 'A' order by misc_type_nm ");

			log.info(" ***getPurposeList SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				miscAppObj = new MiscAppValueObject();
				miscAppObj.setTypeCd(rs.getString("misc_type_cd"));
				miscAppObj.setTypeName(rs.getString("misc_type_nm"));
				purposeList.add(miscAppObj);
			}

			log.info("END: *** getPurposeList Result *****" + purposeList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getPurposeList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getPurposeList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPurposeList  DAO  END");
		}
		return purposeList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getUploadDocumentTypeList()
	@Override
	public List<MiscAppValueObject> getUploadDocumentTypeList() throws BusinessException {
		List<MiscAppValueObject> docList = new ArrayList<MiscAppValueObject>();
		MiscAppValueObject miscAppObj;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getUploadDocumentTypeList  DAO  Start");

			sb.append(" select /* MiscAppEJB - getUploadDocumentTypeList() */ ");
			sb.append(" misc_type_cd, misc_type_nm ");
			sb.append(" from misc_type_code ");
			sb.append(" where cat_cd = 'MISC_MDOC' and rec_status ");
			sb.append(" = 'A' order by misc_type_nm ");

			log.info(" ***getUploadDocumentTypeList SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				miscAppObj = new MiscAppValueObject();
				miscAppObj.setTypeCd(rs.getString("misc_type_cd"));
				miscAppObj.setTypeName(rs.getString("misc_type_nm"));
				docList.add(miscAppObj);
			}

			log.info("END: *** getUploadDocumentTypeList Result *****" + docList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getUploadDocumentTypeList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getUploadDocumentTypeList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getUploadDocumentTypeList  DAO  END");
		}
		return docList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getContractTypeList()
	@Override
	public List<MiscAppValueObject> getContractTypeList() throws BusinessException {
		List<MiscAppValueObject> docList = new ArrayList<MiscAppValueObject>();
		MiscAppValueObject miscAppObj;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getContractTypeList  DAO  Start");

			sb.append(" select /* MiscAppEJB - getContractTypeList() */ ");
			sb.append(" misc_type_cd, misc_type_nm from misc_type_code ");
			sb.append(" where cat_cd = 'MISC_CDOC' and rec_status = 'A' ");
			sb.append(" order by misc_type_nm ");

			log.info(" ***getContractTypeList SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				miscAppObj = new MiscAppValueObject();
				miscAppObj.setTypeCd(rs.getString("misc_type_cd"));
				miscAppObj.setTypeName(rs.getString("misc_type_nm"));
				docList.add(miscAppObj);
			}

			log.info("END: *** getContractTypeList Result *****" + docList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getContractTypeList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getContractTypeList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getContractTypeList  DAO  END");
		}
		return docList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getUploadDocumentList()
	@Override
	public List<MiscAppValueObject> getUploadDocumentList() throws BusinessException {
		ArrayList<MiscAppValueObject> docList = new ArrayList<MiscAppValueObject>();
		MiscAppValueObject miscAppObj;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getUploadDocumentList  DAO  Start");

			sb.append(" select /* MiscAppEJB - getUploadDocumentList() */ ");
			sb.append(" misc_type_nm, b.upload_file_nm doc_name, ");
			sb.append(" b.create_dttm, b.create_user_id ");
			sb.append(" from misc_type_code a, misc_upload_doc b ");
			sb.append(" where cat_cd = 'MISC_MDOC' ");
			sb.append(" and a.misc_type_cd = b.doc_type and a.rec_status ");
			sb.append(" = 'A' order by a.misc_type_nm ");

			log.info(" ***getUploadDocumentList SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				miscAppObj = new MiscAppValueObject();
				miscAppObj.setTypeName(rs.getString("misc_type_nm"));
				miscAppObj.setDocName(rs.getString("doc_name"));
				miscAppObj.setAppDttm(rs.getString("create_dttm"));
				miscAppObj.setUserId(rs.getString("create_user_id"));
				docList.add(miscAppObj);
			}
			log.info("END: *** getUploadDocumentList Result *****" + docList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getUploadDocumentList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getUploadDocumentList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getUploadDocumentList  DAO  END");
		}
		return docList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getContractUploadDocumentList()
	@Override
	public List<MiscAppValueObject> getContractUploadDocumentList() throws BusinessException {
		List<MiscAppValueObject> docList = new ArrayList<MiscAppValueObject>();
		MiscAppValueObject miscAppObj;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getContractUploadDocumentList  DAO  Start");

			sb.append(" select /* MiscAppEJB - getContractTypeList() */ ");
			sb.append(" misc_type_cd, misc_type_nm from misc_type_code ");
			sb.append(" where cat_cd = 'MISC_CDOC' and rec_status ");
			sb.append(" = 'A'  order by misc_type_nm ");

			log.info(" ***getContractUploadDocumentList SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				miscAppObj = new MiscAppValueObject();
				miscAppObj.setTypeCd(rs.getString("misc_type_cd"));
				miscAppObj.setTypeName(rs.getString("misc_type_nm"));
				docList.add(miscAppObj);
			}

			log.info("END: *** getContractUploadDocumentList Result *****" + docList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getContractUploadDocumentList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getContractUploadDocumentList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getContractUploadDocumentList  DAO  END");
		}
		return docList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getCustomerList()
	@Override
	public ArrayList<MiscAppValueObject> getCustomerList(String userId, String coCd, String custName)
			throws BusinessException {
		ArrayList<MiscAppValueObject> custList = new ArrayList<MiscAppValueObject>();
		MiscAppValueObject miscAppObj;
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		if (custName == null)
			custName = "";

		try {
			log.info("START: getCustomerList  DAO  Start userId:" + CommonUtility.deNull(userId) + "coCd:"
					+ CommonUtility.deNull(coCd) + "custName:" + CommonUtility.deNull(custName));

			if ("JP".equals(coCd)) {
				sb.append(" select /* MiscAppEJB - getCustomerList() */ ");
				sb.append(" co_cd, co_nm from company_code where co_nm like ");
				sb.append(" UPPER(:custName) and rec_status ");
				sb.append(" = 'A' order by co_cd,co_nm ");
			} else if (!"JP".equals(coCd)) {
				sb.append(" select /* MiscAppEJB - getCustomerList() */ ");
				sb.append(" a.co_cd, a.co_nm from company_code a, logon_acct b ");
				sb.append(" where b.login_id =:userId and a.co_cd = b.cust_cd ");
				sb.append(" and rec_status = 'A'  order by co_cd,co_nm ");
			}

			log.info(" ***getCustomerList SQL *****" + sb.toString());

			if ("JP".equals(coCd)) {
				paramMap.put("custName", "%" + custName + "%");
			} else if (!"JP".equals(coCd)) {
				paramMap.put("userId", userId);
			}

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				miscAppObj = new MiscAppValueObject();
				miscAppObj.setCoCd(rs.getString("co_cd"));
				miscAppObj.setCoName(rs.getString("co_nm"));
				custList.add(miscAppObj);
			}

			log.info("END: *** getCustomerList Result *****" + custList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getCustomerList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getCustomerList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCustomerList  DAO  END");
		}
		return custList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getAccountList()
	@Override
	public ArrayList<MiscAppValueObject> getAccountList(String userId, String coCd, String cust)
			throws BusinessException {
		ArrayList<MiscAppValueObject> acctList = new ArrayList<MiscAppValueObject>();
		MiscAppValueObject miscAppObj;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getAccountList  DAO  Start userId:" + CommonUtility.deNull(userId) + "coCd:"
					+ CommonUtility.deNull(coCd) + "cust:" + CommonUtility.deNull(cust));

			if ("JP".equals(coCd) && cust != null) {
				sb.append(" select /* MiscAppEJB - getAccountList() */ ");
				sb.append(" a.acct_nbr from cust_acct a where a.cust_cd =:cust ");
				sb.append(" and a.business_type = 'G' and a.acct_status_cd ");
				sb.append(" = 'A' order by acct_nbr ");
			} else if (!"JP".equals(coCd)) {
				sb.append(" select /* MiscAppEJB - getAccountList() */ ");
				sb.append(" a.acct_nbr from cust_acct a, logon_acct b ");
				sb.append(" where b.login_id =:userId and a.cust_cd = b.cust_cd ");
				sb.append(" and a.business_type = 'G' and a.acct_status_cd = 'A' order by acct_nbr ");
			}

			log.info(" ***getAccountList SQL *****" + sb.toString());

			if ("JP".equals(coCd) && cust != null) {
				paramMap.put("cust", cust);
			} else if (!"JP".equals(coCd)) {
				paramMap.put("userId", userId);
			}

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				miscAppObj = new MiscAppValueObject();
				miscAppObj.setAcctNbr(rs.getString("acct_nbr"));
				acctList.add(miscAppObj);
			}

			log.info("END: *** getAccountList Result *****" + acctList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getCustomerList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getCustomerList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAccountList  DAO  END");
		}
		return acctList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getVesselList()
	@Override
	public List<String> getVesselList(String vesselName) throws BusinessException {
		List<String> vsList = new ArrayList<String>();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getVesselList  DAO  Start vesselName:" + CommonUtility.deNull(vesselName));

			int atuGracePeriod = getATUGracePeriod();

			sb.append(" select /* MiscAppEJB - getVesselList() */ ");
			sb.append(" distinct vsl_nm from vessel_call ");
			sb.append(" where vsl_nm LIKE UPPER(:vesselName) ");
			sb.append(" and vv_status_ind not in ('CX','CL')  ");
			sb.append(" AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')  ");
			// " terminal = 'GB' ");
			sb.append(" UNION ");
			sb.append(" SELECT  DISTINCT(V.VSL_NM) FROM VESSEL_CALL V, BERTHING B  ");
			sb.append(" WHERE V.VSL_NM LIKE UPPER(:vesselName) ");
			sb.append(" AND V.VV_STATUS_IND IN ('CL')  ");
			sb.append(" AND((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')  ");
			// " AND V.TERMINAL = 'GB' " +
			sb.append(" AND B.ATU_DTTM > = SYSDATE - :atuGracePeriod "); // Parametrized value
			sb.append(" AND B.VV_CD = V.VV_CD ");

			log.info(" ***getVesselList SQL *****" + sb.toString());

			paramMap.put("vesselName", vesselName + "%");
			paramMap.put("atuGracePeriod", atuGracePeriod);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			while (rs.next()) {
				vsList.add(rs.getString("vsl_nm"));
			}

			log.info("END: *** getVesselList Result *****" + vsList.toString());
		} catch (BusinessException e) {
			log.info("exception: getVesselList ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getVesselList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getVesselList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselList  DAO  END");
		}
		return vsList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getVoyageList()

	@Override
	public List<MiscAppValueObject> getVoyageList(String vesselName) throws BusinessException {
		List<MiscAppValueObject> voyList = new ArrayList<MiscAppValueObject>();
		MiscAppValueObject miscAppObj;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getVoyageList  DAO  Start vesselName:" + CommonUtility.deNull(vesselName));

			int atuGracePeriod = getATUGracePeriod();

			sb.append(" select /* MiscAppEJB - getVoyageList() */ ");
			sb.append(" vv_cd, in_voy_nbr, out_voy_nbr,terminal from vessel_call ");
			sb.append(" where vsl_nm = UPPER(:vesselName) ");
			sb.append(" and vv_status_ind not in ('CX','CL')  ");
			sb.append(" AND((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')  ");
			// " and terminal = 'GB' ");
			sb.append(" UNION  ");
			sb.append(" SELECT V.VV_CD, V.IN_VOY_NBR, V.OUT_VOY_NBR, V.TERMINAL FROM VESSEL_CALL V, BERTHING B  ");
			sb.append(" WHERE V.VSL_NM = UPPER(:vesselName) ");
			sb.append(" AND V.VV_STATUS_IND IN ('CL')  ");
			sb.append(" AND((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')  ");
			// " AND TERMINAL = 'GB' ");
			sb.append(" AND B.ATU_DTTM > = SYSDATE - :atuGracePeriod ");// Parametrized value
			sb.append(" AND B.VV_CD = V.VV_CD ");

			log.info(" ***getVoyageList SQL *****" + sb.toString());

			paramMap.put("vesselName", vesselName);
			paramMap.put("atuGracePeriod", atuGracePeriod);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				miscAppObj = new MiscAppValueObject();
				miscAppObj.setVarCode(rs.getString("vv_cd"));
				miscAppObj.setVoyNbr(rs.getString("in_voy_nbr") + " / " + rs.getString("out_voy_nbr"));
				miscAppObj.setTerminal(rs.getString("terminal"));
				voyList.add(miscAppObj);
			}

			log.info("END: *** getVoyageList Result *****" + voyList.toString());
		} catch (BusinessException e) {
			log.info("exception: getVoyageList ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getVoyageList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getVoyageList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVoyageList  DAO  END");
		}
		return voyList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->checkVarcode()

	@Override
	public String checkVarcode(String varcode) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String result = "InValid";

		try {
			log.info("START: checkVarcode  DAO  Start varcode:" + CommonUtility.deNull(varcode));

			int atuGracePeriod = getATUGracePeriod();

			sb.append(" select /* MiscAppEJB - checkVarcode() */ vv_cd from vessel_call ");
			sb.append(" where vv_cd =:varcode");
			sb.append(" and vv_status_ind not in ('CX','CL') ");
			sb.append(" AND((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT') ");
			// " and terminal = 'GB'" +
			sb.append(" UNION ");
			sb.append(" SELECT V.VV_CD FROM VESSEL_CALL V, BERTHING B ");
			sb.append(" WHERE V.VV_CD =:varcode");
			sb.append(" AND V.VV_STATUS_IND IN ('CL') ");
			sb.append(" AND((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT') ");
			// " AND TERMINAL = 'GB' " +
			sb.append(" AND B.ATU_DTTM > = SYSDATE - :atuGracePeriod ");// Parametrized value
			sb.append(" AND B.VV_CD = V.VV_CD ");

			log.info(" ***checkVarcode SQL *****" + sb.toString());

			paramMap.put("varcode", varcode);
			paramMap.put("atuGracePeriod", atuGracePeriod);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				result = "Valid";
			}

			log.info("END: *** checkVarcode Result *****" + result.toString());
		} catch (BusinessException e) {
			log.info("exception: checkVarcode ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: checkVarcode ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: checkVarcode ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkVarcode  DAO  END");
		}
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getCustomerDetails()

	@Override
	public List<MiscCustValueObject> getCustomerDetails(String userId, String cust, String account)
			throws BusinessException {
		List<MiscCustValueObject> custList = new ArrayList<MiscCustValueObject>();
		MiscCustValueObject miscCustValueObject = new MiscCustValueObject();
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> paramMap1 = new HashMap<String, Object>();
		Map<String, Object> paramMap2 = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();

		try {
			log.info("START: getCustomerDetails  DAO  Start userId:" + CommonUtility.deNull(userId) + " cust:"
					+ CommonUtility.deNull(cust) + " account:" + CommonUtility.deNull(account));

			if (cust != null) {
				sb.append(" select /* MiscAppEJB - getCustomerDetails(sql1) */ ");
				sb.append(" co_cd, co_nm from company_code ");
				// Amended on 12/05/2008 by Punitha
				// " where rec_status = 'A' and co_cd = UPPER('" + cust + "')";
				sb.append(" where co_cd = UPPER(:cust) ");
				// End by Punitha
			} else {
				sb.append(" select /* MiscAppEJB - getCustomerDetails(sql1) */ ");
				sb.append(" a.co_cd, a.co_nm from company_code a, logon_acct b ");
				sb.append(" where b.login_id =:userId and a.co_cd = b.cust_cd ");
				// Amended by Punitha on 12/05/2008
				// " and rec_status = 'A' order by co_nm";
				sb.append(" order by co_nm ");
				// End by Punitha
			}

			sb1.append(" select /* MiscAppEJB - getCustomerDetails(sql2) */ ");
			sb1.append(" phone1_nbr from cust_contact where cust_cd =UPPER(");

			sb2.append(" select /* MiscAppEJB - getCustomerDetails(sql3) */ ");
			sb2.append(" add_l1, add_l2, add_city, add_post_cd ");
			sb2.append(" from cust_address where cust_cd =UPPER(");

			log.info(" ***getCustomerDetails SQL *****" + sb.toString());

			if (cust != null) {
				paramMap.put("cust", cust);
			} else {
				paramMap.put("userId", userId);
			}

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				miscCustValueObject.setCoCd(rs.getString("co_cd"));
				miscCustValueObject.setCoName(rs.getString("co_nm"));
			}

			if (cust != null) {
				sb1.append(":cust)");
			} else {
				sb1.append(":coCd)");
			}

			log.info(" ***getCustomerDetails SQL *****" + sb1.toString());

			if (cust != null) {
				paramMap1.put("cust", cust);
			} else {
				paramMap1.put("coCd", miscCustValueObject.getCoCd());
			}

			log.info(" *** paramMap1: *****" + paramMap1.toString());

			rs1 = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap1);
			if (rs1.next()) {
				miscCustValueObject.setContact1(rs1.getString("phone1_nbr"));
			}

			if (cust != null) {
				sb2.append(":cust)");
			} else {
				sb2.append(":coCd)");
			}

			log.info(" ***getCustomerDetails SQL *****" + sb2.toString());

			if (cust != null) {
				paramMap2.put("cust", cust);
			} else {
				paramMap2.put("coCd", miscCustValueObject.getCoCd());
			}

			log.info(" *** paramMap2: *****" + paramMap2.toString());

			rs2 = namedParameterJdbcTemplate.queryForRowSet(sb2.toString(), paramMap);
			if (rs2.next()) {
				miscCustValueObject.setAddress1(rs2.getString("add_l1"));
				miscCustValueObject.setAddress2(rs2.getString("add_l2"));
				miscCustValueObject.setCity(rs2.getString("add_city"));
				miscCustValueObject.setPin(rs2.getString("add_post_cd"));
			}

			miscCustValueObject.setAcctNbr(account);
			custList.add(miscCustValueObject);

			log.info("END: *** getCustomerDetails Result *****" + custList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getCustomerDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getCustomerDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCustomerDetails  DAO  END");
		}
		return custList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->addOvernightParkingVehicleDetails()

	@Override
	public void addOvernightParkingVehicleDetails(String userId, String applyType, String status, String cust,
			String account, String varcode, String fromDate, String toDate, String noNights, String parkReason,
			String[] vehNo, String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson,
			String conTel) throws BusinessException {
		addOvernightParkingVehicleDetails(userId, applyType, status, cust, account, varcode, fromDate, toDate, noNights,
				parkReason, vehNo, cntNo, asnNo, coName, appDate, conPerson, conTel, "");
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->addOvernightParkingVehicleDetails()

	@Override
	public void addOvernightParkingVehicleDetails(String userId, String applyType, String status, String cust,
			String account, String varcode, String fromDate, String toDate, String noNights, String parkReason,
			String[] vehNo, String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson,
			String conTel, String conEmail) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		String miscSeqNbr = null;
		try {
			log.info("START: addOvernightParkingVehicleDetails  DAO  Start userId:" + CommonUtility.deNull(userId)
					+ "applyType:" + CommonUtility.deNull(applyType) + "status:" + CommonUtility.deNull(status)
					+ "cust:" + CommonUtility.deNull(cust) + "account:" + CommonUtility.deNull(account) + "varcode:"
					+ CommonUtility.deNull(varcode) + "fromDate:" + CommonUtility.deNull(fromDate) + "toDate:"
					+ CommonUtility.deNull(toDate) + "noNights:" + CommonUtility.deNull(noNights) + "parkReason:"
					+ CommonUtility.deNull(parkReason) + "vehNo:" + vehNo.toString() + "cntNo:" + cntNo.toString()
					+ "asnNo:" + asnNo.toString() + "coName:" + CommonUtility.deNull(coName) + "appDate:"
					+ CommonUtility.deNull(appDate) + "conPerson:" + CommonUtility.deNull(conPerson) + "conTel:"
					+ CommonUtility.deNull(conTel) + "conEmail:" + CommonUtility.deNull(conEmail));

			sb.append(" insert into misc_vehicle(misc_seq_nbr, fr_dttm, to_dttm, ");
			sb.append(" nbr_night, park_reason, ");
			// " actual_fr_dttm, actual_to_dttm, actual_nbr_night, " +
			sb.append(" last_modify_user_id, last_modify_dttm) values ( ");
			sb.append(" :miscSeqNbr,to_date(:fromDate,'dd/mm/yyyy'), ");
			sb.append(" to_date(:toDate,'dd/mm/yyyy'),:noNights, ");
			sb.append(" :parkReason,:userId, sysdate) ");

			sb1.append(" insert into misc_vehicle_det(misc_seq_nbr, item_nbr, ");
			sb1.append(" veh_chas_nbr, cntr_nbr, ");
			sb1.append(" asn_nbr, last_modify_user_id, last_modify_dttm) values ");
			sb1.append(" (:miscSeqNbr,:itemNbr,:vehChasNbr,:cntrNbr, ");
			sb1.append(" :asnNbr,:userId,sysdate) ");

			// miscSeqNbr = insertMiscAppDetails( userId, applyType, status, cust,
			// account, varcode, appDate/);
			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
			miscSeqNbr = insertMiscAppDetails(userId, applyType, status, cust, account, varcode, appDate, conPerson,
					conTel, conEmail);

			log.info(" ***addOvernightParkingVehicleDetails SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);
			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			paramMap.put("noNights", noNights);
			paramMap.put("parkReason", parkReason);
			paramMap.put("userId", userId);
			log.info(" *** paramMap: *****" + paramMap.toString());
			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			paramMap = new HashMap<String, Object>();
			// log.info("Going to process veh Details========> ");
			for (int i = 0, j = 1; i < vehNo.length; i++, j++) {

				log.info(" ***addOvernightParkingVehicleDetails SQL *****" + sb1.toString());

				paramMap.put("miscSeqNbr", miscSeqNbr);
				paramMap.put("itemNbr", j);
				paramMap.put("vehChasNbr",
						(vehNo[i] != null && !vehNo[i].equals(""))
								? CommonUtility.getStringTokens(vehNo[i]).toUpperCase()
								: vehNo[i]);
				paramMap.put("cntrNbr", cntNo[i].toUpperCase());
				paramMap.put("asnNbr", asnNo[i]);
				paramMap.put("userId", userId);
				log.info(" *** paramMap: *****" + paramMap.toString());
				if ((vehNo[i] != null && !vehNo[i].equals(""))) /*
																 * || (cntNo[i] != null && !cntNo[i].equals("")) ||
																 * (asnNo[i] != null && !asnNo[i].equals("")) )
																 */ {
					namedParameterJdbcTemplate.update(sb1.toString(), paramMap);
				}
			}
			// Send Email & SMS
			if (status != null && status.equals("S")) {
				String alertCode = ConstantUtil.alertCode;
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = OvernightParkVecSubmit_subject;
				String templateEmailFile = TemplateSubmit_body_template;
				
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Overnight Parking of Vehicle");
				
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * + "Type of Application: Overnight Parking of Vehicle \n" + "Reference No.: "
				 * + CommonUtility.deNull(refNbr) + " \n" + "Application Date/Time: " +
				 * CommonUtility.deNull(appDttm) + " \n" + "Company: " + coName + "\n";
				 */

				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Overnight Parking of Vehicle, " + "Ref. No.: "
						+ CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm) + " , by " + coName;
				sendSMS(alertCode, sms);
			}

			log.info("END: *** addOvernightParkingVehicleDetails Result *****");
		} catch (BusinessException e) {
			log.info("exception: addOvernightParkingVehicleDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: addOvernightParkingVehicleDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: addOvernightParkingVehicleDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: addOvernightParkingVehicleDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getVesselDetails()
	// Parking of Line-Tow Barge

	@Override
	public List<String> getVesselDetails() throws BusinessException {
		List<String> vslList = new ArrayList<String>();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getVesselDetails  DAO  Start");

			sb.append(" select /* MiscAppEJB - getVesselDetails() */ ");
			sb.append(" distinct vsl_nm from vessel_call ");
			sb.append(" where ");
			sb.append(" ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) ");
			sb.append(" OR TERMINAL NOT IN 'CT') ");
			// " terminal = 'GB' " +
			sb.append(" and vv_status_ind not in ('CX', 'CL') order by vsl_nm ");

			log.info(" ***getVesselDetails SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				vslList.add(rs.getString("vsl_nm"));
			}
			log.info("END: *** getVesselDetails Result *****" + vslList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getVesselDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getVesselDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselDetails  DAO  END");
		}
		log.info("END: *** getVesselDetails Result *****" + vslList.toString());
		return vslList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->addParkingOfLineTowBargeDetails()
	/*
	 * public void addParkingOfLineTowBargeDetails(String userId, String applyType,
	 * String status, String cust, String account, String varcode, String bargeName,
	 * String bargeLOA, String draft, String tugboat, String contactNo, String
	 * fromDate, String toDate, String motherShip, String berthNo, String dg, String
	 * cargoType, String className, String coName, String appDate) throws
	 * BusinessException{
	 */
	// Amended on 14/06/2007 by Punitha. To add Contact Person and Contact Tel

	@Override
	public void addParkingOfLineTowBargeDetails(String userId, String applyType, String status, String cust,
			String account, String varcode, String bargeName, String bargeLOA, String draft, String tugboat,
			String contactNo, String fromDate, String toDate, String motherShip, String berthNo, String dg,
			String cargoType, String className, String coName, String appDate, String conPerson, String conTel)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String miscSeqNbr = null;

		try {
			log.info("START: addParkingOfLineTowBargeDetails  DAO  Start userId:" + CommonUtility.deNull(userId)
					+ "applyType:" + CommonUtility.deNull(applyType) + "status:" + CommonUtility.deNull(status)
					+ "cust:" + CommonUtility.deNull(cust) + "account:" + CommonUtility.deNull(account) + "varcode:"
					+ CommonUtility.deNull(varcode) + "bargeName:" + CommonUtility.deNull(bargeName) + "bargeLOA:"
					+ CommonUtility.deNull(bargeLOA) + "draft:" + CommonUtility.deNull(draft) + "tugboat:"
					+ CommonUtility.deNull(tugboat) + "contactNo:" + CommonUtility.deNull(contactNo) + "fromDate:"
					+ CommonUtility.deNull(fromDate) + "toDate:" + CommonUtility.deNull(toDate) + "motherShip:"
					+ CommonUtility.deNull(motherShip) + "berthNo:" + CommonUtility.deNull(berthNo) + "dg:"
					+ CommonUtility.deNull(dg) + "cargoType:" + CommonUtility.deNull(cargoType) + "className:"
					+ CommonUtility.deNull(className) + "coName:" + CommonUtility.deNull(coName) + "appDate:"
					+ CommonUtility.deNull(appDate) + "conPerson:" + CommonUtility.deNull(cargoType) + "conTel:"
					+ CommonUtility.deNull(conTel));

			sb.append(" insert into misc_barge(misc_seq_nbr, barge_nm, barge_loa, ");
			sb.append(" max_draft_alongside, tug_boat, contact_nbr, fr_dttm, to_dttm, ");
			sb.append(" vsl_nm, berth_nbr, dg_ind, cargo_type, dg_class, ");
			// " alloc_berth_nbr, alloc_wharf_mark_fr, alloc_wharf_mark_to, barge_atb,
			// barge_atu, " +
			sb.append(" last_modify_user_id, last_modify_dttm) values ( ");
			sb.append(" :miscSeqNbr,:bargeName,:bargeLOA,:draft, ");
			sb.append(" :tugboat,:contactNo,to_date(:fromDate,'dd/mm/yyyy HH24:mi'), ");
			sb.append(" to_date(:toDate,'dd/mm/yyyy HH24:mi'),:motherShip,:berthNo,:dg, ");
			sb.append(" :cargoType,:className,:userId,sysdate) ");

			// miscSeqNbr = insertMiscAppDetails( userId, applyType, status, cust,
			// account, varcode, appDate);
			// Amended by Punitha on 14/06/2007. To add Contact Person and Contact Tel
			miscSeqNbr = insertMiscAppDetails(userId, applyType, status, cust, account, varcode, appDate, conPerson,
					conTel);
			// Ended by Punitha
			// log.info("ParkingOfLineTowBarge : Going to process details
			// ========> ");

			log.info(" ***addParkingOfLineTowBargeDetails SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);
			paramMap.put("bargeName", bargeName);
			paramMap.put("bargeLOA", bargeLOA);
			paramMap.put("draft", draft);
			paramMap.put("tugboat", tugboat);
			paramMap.put("contactNo", contactNo);
			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			paramMap.put("motherShip", motherShip);
			paramMap.put("berthNo", berthNo);
			paramMap.put("dg", dg);
			paramMap.put("cargoType", cargoType);
			paramMap.put("className", className);
			paramMap.put("userId", userId);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			// log.info("ParkingOfLineTowBarge : Inserted
			// Details========> ");
			
			// Sending Mail
			if (status != null && status.equals("S")) {
				String alertCode = ConstantUtil.alertCodeMAB;
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = ParkLineTowBargeSubmit_subject;
				String templateEmailFile = TemplateSubmit_body_template;
								
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Parking of Line-Tow Barge");
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * + "Type of Application: Parking of Line-Tow Barge \n" + "Reference No.: " +
				 * CommonUtility.deNull(refNbr) + " \n" + "Application Date/Time: " +
				 * CommonUtility.deNull(appDttm) + " \n" + "Company: " + coName + "\n";
				 */
				
				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Parking of Line-Tow Barge, " + "Ref. No.: "
						+ CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm) + " , by " + coName;
				sendSMS(alertCode, sms);
			}
			log.info("END: *** addParkingOfLineTowBargeDetails Result *****");
		} catch (BusinessException e) {
			log.info("exception: addParkingOfLineTowBargeDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: addParkingOfLineTowBargeDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: addParkingOfLineTowBargeDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: addParkingOfLineTowBargeDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->addReeferContainerPowerOutletDetails()
	// Reefer Containers Power Outlet
	/*
	 * public void addReeferContainerPowerOutletDetails(String userId, String
	 * applyType, String status, String cust, String account, String varcode,
	 * String[] cntrNo, String[] cntrSize,String[] cntrStatus, String coName, String
	 * appDate) throws BusinessException{
	 */
	// Added on 28/05/2007 by Punitha . To add Contact Person and Contact Tel

	@Override
	public void addReeferContainerPowerOutletDetails(String userId, String applyType, String status, String cust,
			String account, String varcode, String[] cntrNo, String[] cntrSize, String[] cntrStatus, String coName,
			String appDate, String conPerson, String conTel) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String miscSeqNbr = null;

		try {
			log.info("START: addReeferContainerPowerOutletDetails  DAO  Start userId:" + CommonUtil.deNull(userId)
					+ "applyType:" + CommonUtil.deNull(applyType) + "status:" + CommonUtil.deNull(status) + "cust:"
					+ CommonUtil.deNull(cust) + "account:" + CommonUtil.deNull(account) + "varcode:"
					+ CommonUtil.deNull(varcode) + "cntrNo:" + cntrNo.toString() + "cntrSize:" + cntrSize.toString()
					+ "cntrStatus:" + cntrStatus.toString() + "coName:" + CommonUtil.deNull(coName) + "appDate:"
					+ CommonUtil.deNull(appDate) + "conPerson:" + CommonUtil.deNull(conPerson) + "conTel:"
					+ CommonUtil.deNull(conTel));

			sb.append(" insert into misc_reefer_det (misc_seq_nbr, item_nbr, ");
			sb.append(" cntr_nbr, cntr_size, cntr_status, ");
			// " plug_in_dttm, plug_out_dttm, delivery_dttm, dn_po_nbr, remarks, " +
			sb.append(" last_modify_user_id, last_modify_dttm) values ( ");
			sb.append(" :miscSeqNbr, :itemNbr, :cntrNo, :cntrSize, ");
			sb.append(" :cntrStatus, :userId,sysdate) ");

			// miscSeqNbr = insertMiscAppDetails( userId, applyType, status, cust,
			// account, varcode, appDate);
			// Added on 28/05/2007 by Punitha . To add Contact Person and Contact Tel
			miscSeqNbr = insertMiscAppDetails(userId, applyType, status, cust, account, varcode, appDate, conPerson,
					conTel);

			// log.info("Going to process Reefer Details========> ");

			for (int i = 0, j = 1; i < cntrNo.length; i++) {
				if ((cntrNo[i] != null && !cntrNo[i].equals("")) || (cntrSize[i] != null && !cntrSize[i].equals(""))) {
					paramMap.put("miscSeqNbr", miscSeqNbr);
					paramMap.put("itemNbr", j);
					paramMap.put("cntrNo", cntrNo[i]);
					paramMap.put("cntrSize", cntrSize[i]);
					paramMap.put("cntrStatus", cntrStatus[i]);
					paramMap.put("userId", userId);

					log.info(" ***addReeferContainerPowerOutletDetails SQL *****" + sb.toString());
					log.info(" *** paramMap: *****" + paramMap.toString());

					namedParameterJdbcTemplate.update(sb.toString(), paramMap);
					j++;
				}
			}
			
			// Send Email
			if (status != null && status.equals("S")) {
				String alertCode = ConstantUtil.alertCode;
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = ReeferCntrPowerOutletSubmit_subject;
				String templateEmailFile = TemplateSubmit_body_template;
								
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Reefer Containers Power Outlet");
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);

				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * + "Type of Application: Reefer Containers Power Outlet \n" +
				 * "Reference No.: " + CommonUtility.deNull(refNbr) + " \n" +
				 * "Application Date/Time: " + CommonUtility.deNull(appDttm) + " \n" +
				 * "Company: " + coName + "\n";
				 */

				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Reefer Containers Power Outlet, " + "Ref. No.: "
						+ CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm) + " , by " + coName;
				sendSMS(alertCode, sms);
			}
			log.info("END: *** addReeferContainerPowerOutletDetails Result *****");
		} catch (BusinessException e) {
			log.info("exception: addReeferContainerPowerOutletDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: addReeferContainerPowerOutletDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: addReeferContainerPowerOutletDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: addReeferContainerPowerOutletDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->addUseOfSpaceDetails()
	// Use Of Space
	/*
	 * public void addUseOfSpaceDetails(String userId, String applyType, String
	 * status, String cust, String account, String varcode, String coName, String
	 * spaceType, String purpose, String fromDate, String toDate, String reason,
	 * String billNbr, String marks, String packages, String cargoDesc, String
	 * tonnage, String newMarks, String newPackages, String newCargoDesc, String
	 * newTonnage, String appDate) throws BusinessException{
	 */
	// Amended by Punitha on 08/06/2007. To add Contact Person and Contact Tel

	@Override
	public void addUseOfSpaceDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String coName, String spaceType, String purpose, String fromDate, String toDate,
			String reason, String billNbr, String marks, String packages, String cargoDesc, String tonnage,
			String newMarks, String newPackages, String newCargoDesc, String newTonnage, String appDate,
			String conPerson, String conTel) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		String miscSeqNbr = null;

		try {
			log.info("START: addUseOfSpaceDetails  DAO  Start userId:" + CommonUtil.deNull(userId) + "applyType:"
					+ CommonUtil.deNull(applyType) + "status:" + CommonUtil.deNull(status) + "cust:"
					+ CommonUtil.deNull(cust) + "account:" + CommonUtil.deNull(account) + "varcode:"
					+ CommonUtil.deNull(varcode) + "coName:" + CommonUtil.deNull(coName) + "spaceType:"
					+ CommonUtil.deNull(spaceType) + "purpose:" + CommonUtil.deNull(purpose) + "reason:"
					+ CommonUtil.deNull(reason) + "billNbr:" + CommonUtil.deNull(billNbr) + "fromDate:"
					+ CommonUtil.deNull(fromDate) + "toDate:" + CommonUtil.deNull(toDate) + "marks:"
					+ CommonUtil.deNull(marks) + "packages:" + CommonUtil.deNull(packages) + "cargoDesc:"
					+ CommonUtil.deNull(cargoDesc) + "tonnage:" + CommonUtil.deNull(tonnage) + "newMarks:"
					+ CommonUtil.deNull(newMarks) + "newPackages:" + CommonUtil.deNull(newPackages) + "newCargoDesc:"
					+ CommonUtil.deNull(newCargoDesc) + "newTonnage:" + CommonUtil.deNull(newTonnage) + "appDate:"
					+ CommonUtil.deNull(appDate) + "conPerson:" + CommonUtil.deNull(conPerson) + "conTel:"
					+ CommonUtil.deNull(conTel));

			sb.append(" insert into misc_space (misc_seq_nbr, space_type, space_purpose, ");
			sb.append(" fr_dttm, to_dttm,space_reason, bl_do_nbr, orig_mark_nbr, ");
			sb.append(" orig_nbr_pkg, orig_cargo_desc, orig_ton_measure, new_mark_nbr, ");
			sb.append(" new_nbr_pkg, new_cargo_desc, new_ton_measure, last_modify_user_id, ");
			sb.append(" last_modify_dttm) values (:miscSeqNbr,:spaceType, ");
			sb.append(" :purpose,to_date(:fromDate,'dd/mm/yyyy HH24:mi'), ");
			sb.append(" to_date(:toDate,'dd/mm/yyyy HH24:mi'),:reason, ");
			sb.append(" :billNbr,:marks,:packages,:cargoDesc, ");
			sb.append(" :tonnage,:newMarks,:newPackages,:newCargoDesc, ");
			sb.append(" :newTonnage,:userId,sysdate) ");

			sb1.append(" insert into misc_space_det(misc_seq_nbr, item_nbr, ");
			sb1.append(" bay_nbr, area_use, ops_start_dttm, ");
			sb1.append(" ops_end_dttm, last_modify_user_id, last_modify_dttm) ");
			sb1.append(" values (:miscSeqNbr,:itemNbr,:bayNbr, ");
			sb1.append(" :areaUse,:opsStartDttm,:opsEndDttm,:userId,sysdate) ");
			// miscSeqNbr = insertMiscAppDetails( userId, applyType, status, cust,
			// account, varcode, appDate);
			// Amended by Punitha on 08/06/2007. To add Contact Person and Contact Tel
			miscSeqNbr = insertMiscAppDetails(userId, applyType, status, cust, account, varcode, appDate, conPerson,
					conTel);
			// Ended by Punitha
			// log.info("Going to process Space Details========> ");

			log.info(" ***addUseOfSpaceDetails SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);
			paramMap.put("spaceType", spaceType);
			paramMap.put("purpose", purpose);
			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			paramMap.put("reason", reason);
			paramMap.put("billNbr", billNbr);
			paramMap.put("marks", marks);
			paramMap.put("packages", packages);
			paramMap.put("cargoDesc", cargoDesc);
			paramMap.put("tonnage", tonnage);
			paramMap.put("newMarks", newMarks);
			paramMap.put("newPackages", newPackages);
			paramMap.put("newCargoDesc", newCargoDesc);
			paramMap.put("newTonnage", newTonnage);
			paramMap.put("userId", userId);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			
			// Send Email
			if (status != null && status.equals("S")) {
				String alertCode = ConstantUtil.alertCode;
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = SpaceAppSubmit_subject;
				String templateEmailFile = TemplateSubmit_body_template;
								
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Use Of Space");
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*String msgBody = "The following application is submitted for approval: \n\n"
						+ "Type of Application: Use Of Space \n" + "Reference No.: " + CommonUtility.deNull(refNbr)
						+ " \n" + "Application Date/Time: " + CommonUtility.deNull(appDttm) + " \n" + "Company: "
						+ coName + "\n";*/
				
				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Use Of Space, " + "Ref. No.: "
						+ CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm) + " , by " + coName;
				sendSMS(alertCode, sms);
			}
			log.info("END: *** addUseOfSpaceDetails Result *****");
		} catch (BusinessException e) {
			log.info("exception: addUseOfSpaceDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: addUseOfSpaceDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: addUseOfSpaceDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: addUseOfSpaceDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->addParkingOfForkliftShorecrane()
	// Overnight Parking for Forklift/Shore Crane

	@Override
	public String addParkingOfForkliftShorecrane(String userId, String applyType, String status, String cust,
			String account, String varcode, String coName, String macType, String fromDate, String toDate,
			String remarks, List<MiscAppValueObject> docType, List<String> file, List<String> regNbr, String appDate)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		String miscSeqNbr = null;

		try {
			log.info("START: addParkingOfForkliftShorecrane  DAO  Start userId:" + CommonUtil.deNull(userId)
					+ "applyType:" + CommonUtil.deNull(applyType) + "status:" + CommonUtil.deNull(status) + "cust:"
					+ CommonUtil.deNull(cust) + "account:" + CommonUtil.deNull(account) + "varcode:"
					+ CommonUtil.deNull(varcode) + "coName:" + CommonUtil.deNull(coName) + "macType:"
					+ CommonUtil.deNull(macType) + "fromDate:" + CommonUtil.deNull(fromDate) + "toDate:"
					+ CommonUtil.deNull(toDate) + "remarks:" + CommonUtil.deNull(remarks) + "docType:"
					+ docType.toString() + "file:" + file + "regNbr:" + regNbr + "appDate:"
					+ CommonUtil.deNull(appDate));

			sb.append(" insert into misc_machine (misc_seq_nbr, mac_type, fr_dttm, ");
			sb.append(" to_dttm, remarks, last_modify_user_id, last_modify_dttm ) ");
			sb.append(" values (:miscSeqNbr,:macType,to_date(:fromDate,'dd/mm/yyyy HH24:mi'), ");
			sb.append(" to_date(:toDate,'dd/mm/yyyy HH24:mi'),:remarks,:userId,sysdate) ");

			sb1.append(" insert into misc_machine_det (misc_seq_nbr, item_nbr, reg_nbr, ");
			sb1.append(" last_modify_user_id, last_modify_dttm) values ( ");
			sb1.append(" :miscSeqNbr,:itemNbr,:regNbr,:userId,sysdate) ");

			sb2.append(" insert into misc_upload_doc (misc_seq_nbr, doc_type, upload_file_nm, ");
			sb2.append(" assign_file_nm, create_user_id, create_dttm) values ( ");
			sb2.append(" :miscSeqNbr,:docType,:uploadFileNm,:assignFileNm,:userId,sysdate) ");

			miscSeqNbr = insertMiscAppDetails(userId, applyType, status, cust, account, varcode, appDate, null, null);
			// log.info("Going to process Parking for Forklift/Shore
			// Crane Details========> ");

			log.info(" ***addParkingOfForkliftShorecrane SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);
			paramMap.put("macType", macType);
			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			paramMap.put("remarks", remarks);
			paramMap.put("userId", userId);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			// log.info("Going to process MacDet Details========> ");
			paramMap = new HashMap<String, Object>();

			if (regNbr != null) {
				for (int i = 0, j = 1; i < regNbr.size(); i++, j++) {
					paramMap.put("miscSeqNbr", miscSeqNbr);
					paramMap.put("itemNbr", j + "");
					paramMap.put("regNbr", (String) regNbr.get(i));
					paramMap.put("userId", userId);

					log.info(" ***addParkingOfForkliftShorecrane SQL2 *****" + sb1.toString());
					log.info(" *** paramMap: *****" + paramMap.toString());

					namedParameterJdbcTemplate.update(sb1.toString(), paramMap);
				}
			}

			/*
			 * String name = null; if(file != null){ String[] uploadName =
			 * (String[])file.toArray(new String[0]); for(int i = 0; i < uploadName.length;
			 * i++){ name = uploadName[i].substring(uploadName[i].lastIndexOf("\\")+1);
			 * assignedFileNameList.add(getNextDocSeqNumber(con) + "_" + name); } }
			 */

			// log.info("Going to process doc Details========> ");

			paramMap = new HashMap<String, Object>();

			if (docType != null && file != null) {
				for (int i = 0; i < docType.size(); i++) {
					String fileName = (String) file.get(i);
					paramMap.put("miscSeqNbr", miscSeqNbr);
					paramMap.put("docType", docType.get(i));
					paramMap.put("uploadFileNm", fileName.substring(fileName.indexOf("_") + 1));
					paramMap.put("assignFileNm", fileName.substring(fileName.lastIndexOf("/") + 1));
					paramMap.put("userId", userId);

					log.info(" ***addParkingOfForkliftShorecrane SQL *****" + sb2.toString());
					log.info(" *** paramMap: *****" + paramMap.toString());

					namedParameterJdbcTemplate.update(sb1.toString(), paramMap);
				}
			}
			// Send Email
			if (status != null && status.equals("S")) {
				String alertCode = "MAF";
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = OvernightParkForkliftSubmit_subject;
				String templateEmailFile = TemplateSubmit_body_template;
								
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Overnight Parking of Forklift/Shore Crane");
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				
				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * + "Type of Application: Overnight Parking of Forklift/Shore Crane \n" +
				 * "Reference No.: " + CommonUtility.deNull(refNbr) + " \n" +
				 * "Application Date/Time: " + CommonUtility.deNull(appDttm) + " \n" +
				 * "Company: " + coName + "\n";
				 */
				
				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Overnight Parking of Forklift/Shore Crane, "
						+ "Ref. No.: " + CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm)
						+ " , by " + coName;
				sendSMS(alertCode, sms);
			}
			log.info("END: *** addParkingOfForkliftShorecrane Result ***** iscSeqNbr: " + miscSeqNbr);
		} catch (BusinessException e) {
			log.info("exception: addParkingOfForkliftShorecrane ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: addParkingOfForkliftShorecrane ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: addParkingOfForkliftShorecrane ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: addParkingOfForkliftShorecrane  DAO  END **** miscSeqNbr: " + miscSeqNbr);
		}
		return miscSeqNbr;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->addStationingOfMacDetails()

	@Override
	public String addStationingOfMacDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String coName, MiscParkMacValueObject obj, String appDate) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		String miscSeqNbr = null;

		try {
			log.info("START: addStationingOfMacDetails  DAO  Start userId:" + CommonUtil.deNull(userId) + "applyType:"
					+ CommonUtil.deNull(applyType) + "status:" + CommonUtil.deNull(status) + "cust:"
					+ CommonUtil.deNull(cust) + "account:" + CommonUtil.deNull(account) + "varcode:"
					+ CommonUtil.deNull(varcode) + "coName:" + CommonUtil.deNull(coName) + "obj:" + obj.toString()
					+ "appDate:" + CommonUtil.deNull(appDate));

			sb.append(" insert into misc_machine (misc_seq_nbr, mac_type, fr_dttm, to_dttm, ");
			sb.append(" last_modify_user_id, last_modify_dttm ) ");
			sb.append(" values (:miscSeqNbr,:macType,to_date(:fromDate,'dd/mm/yyyy HH24:mi'), ");
			sb.append(" to_date(:toDate,'dd/mm/yyyy HH24:mi'),:userId,sysdate) ");

			sb1.append(" insert into misc_machine_det (misc_seq_nbr, item_nbr, reg_nbr, ");
			sb1.append(" lift_capacity, insurance_nbr, insurance_exp_dttm, last_modify_user_id, ");
			sb1.append(" last_modify_dttm) values (:miscSeqNbr,:itemNbr,:regNbr,:liftCapacity, ");
			sb1.append(" :insuranceNbr,to_date(:insExpDttm,'dd/mm/yyyy'),:userId,sysdate) ");

			String macDetSql = sb1.toString();
//			sb2.append(" insert into misc_upload_doc (misc_seq_nbr, doc_type, upload_file_nm, ");
//			sb2.append(" assign_file_nm, create_user_id, create_dttm) values ( ");
//			sb2.append(" :miscSeqNbr,:docType,:uploadFileNm,:assignFileNm,:userId,sysdate) ");

			miscSeqNbr = insertMiscAppDetails(userId, applyType, status, cust, account, varcode, appDate, null, null);
			// log.info("Going to process Stationing for Mac
			// Details========> ");

			log.info(" ***addStationingOfMacDetails SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);
			paramMap.put("macType", obj.getMacType());
			paramMap.put("fromDate", obj.getFromDate());
			paramMap.put("toDate", obj.getToDate());
			paramMap.put("userId", userId);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

//			if (regNbr != null) {
//				pstmt1 = NamedPreparedStatement.prepareStatement( sb1.toString());
//				log.info(" ***addStationingOfMacDetails SQL *****" + sb1.toString());
//				for (int i = 0, j = 1; i < regNbr.length; i++, j++) {
//					paramMap.put("miscSeqNbr", miscSeqNbr);
//					paramMap.put("itemNbr", j + "");
//					paramMap.put("regNbr", regNbr[i]);
//					paramMap.put("liftCapacity", liftCapacity[i]);
//					paramMap.put("insuranceNbr", insuranceNbr[i]);
//					paramMap.put("insExpDttm", insExpDttm[i]);
//					paramMap.put("userId", userId);
//
//					namedParameterJdbcTemplate.update(sb.toString(), paramMap);
//				}
//			}

			// code modified for mac details
			List<MiscParkMacValueObject> macDetList = obj.getMacDetList();
			log.info(" ***addStationingOfMacDetails macDetSql SQL2 *****" + macDetSql.toString());
			paramMap = new HashMap<String, Object>();
			if (macDetList != null) {
				for (int i = 0; i < macDetList.size(); i++) {
					// MiscParkMacValueObject vo =
					// objectMapper.getTypeFactory().constructCollectionType(ArrayList.class,
					// MiscParkMacValueObject.class);
					MiscParkMacValueObject vo = (MiscParkMacValueObject) macDetList.get(i);
					paramMap.put("miscSeqNbr", miscSeqNbr);
					paramMap.put("itemNbr", String.valueOf(i + 1));
					paramMap.put("regNbr", (String) vo.getRegNbrValue());
					paramMap.put("liftCapacity", (String) vo.getLiftCapacityValue());
					paramMap.put("insuranceNbr", (String) vo.getInsuranceNbrValue());
					paramMap.put("insExpDttm", (String) vo.getInsExpDttmValue());
					paramMap.put("userId", userId);

					log.info(" *** paramMap: *****" + paramMap.toString());

					namedParameterJdbcTemplate.update(macDetSql.toString(), paramMap);

				}
			}

			// log.info("Going to process doc Details========> ");
			// String[] docType = obj.getDocType();
			// String[] file = obj.getDocName();
			/*
			 * if(file != null){ assignedFileNameList = getAssignedFileNameList( file,
			 * miscSeqNbr); } String name = null; if(file != null){ for(int i = 0; i <
			 * file.length; i++){ name = file[i].substring(file[i].lastIndexOf("\\")+1);
			 * assignedFileNameList.add(getNextDocSeqNumber(con) + "_" + name); } }
			 */
			
			// Send Email
			if (status != null && status.equals("S")) {
				// Amended on 03/12/2007 by Ai Lin - To rectify the alert party
				// String alertCode = ConstantUtil.alertCode;
				String alertCode = "MAS";
				// End amended on 03/12/2007 by Ai Lin - To rectify the alert party
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = StationingForkliftSubmit_subject;
				String templateEmailFile = TemplateSubmit_body_template;
								
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Stationing of Forklift / Container Lifter / Wheel Loader / Shore Crane");
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * +
				 * "Type of Application: Stationing of Forklift / Container Lifter / Wheel Loader / Shore Crane \n"
				 * + "Reference No.: " + CommonUtility.deNull(refNbr) + " \n" +
				 * "Application Date/Time: " + CommonUtility.deNull(appDttm) + " \n" +
				 * "Company: " + coName + "\n";
				 */
				
				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Stationing of Forklift / Container Lifter / Wheel Loader / Shore Crane, "
						+ "Ref. No.: " + CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm)
						+ " , by " + coName;
				sendSMS(alertCode, sms);
			}
			log.info("END: *** addStationingOfMacDetails Result ***** miscSeqNbr: " + miscSeqNbr);
		} catch (BusinessException e) {
			log.info("exception: addStationingOfMacDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: addStationingOfMacDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: addStationingOfMacDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: addStationingOfMacDetails  DAO  END ****** miscSeqNbr: " + miscSeqNbr);
		}
		return miscSeqNbr;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->addContractorPermitDetails()
	// Contractor Permit

	@Override
	public String addContractorPermitDetails(String userId, String applyType, String status, String cust,
			String account, String varcode, String coName, String location, String description, String others,
			String fromDate, String toDate, String licType, String licNo, String remarks, String waiver,
			String contCoNm, String contCoAddr, String contactNm, String contactNric, String designation,
			List<MiscAppValueObject> docType, List<String> file, String appDate) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		String miscSeqNbr = null;

		try {
			log.info("START: addContractorPermitDetails  DAO  Start userId:" + CommonUtil.deNull(userId) + "applyType:"
					+ CommonUtil.deNull(applyType) + "status:" + CommonUtil.deNull(status) + "cust:"
					+ CommonUtil.deNull(cust) + "account:" + CommonUtil.deNull(account) + "varcode:"
					+ CommonUtil.deNull(varcode) + "coName:" + CommonUtil.deNull(coName) + "location:"
					+ CommonUtil.deNull(location) + "description:" + CommonUtil.deNull(description) + "others:"
					+ CommonUtil.deNull(others) + "fromDate:" + CommonUtil.deNull(fromDate) + "toDate:"
					+ CommonUtil.deNull(toDate) + "licType:" + CommonUtil.deNull(licType) + "licNo:"
					+ CommonUtil.deNull(licNo) + "remarks:" + CommonUtil.deNull(remarks) + "waiver:" + waiver
					+ "contCoNm:" + contCoNm + "contCoAddr:" + contCoAddr + "contactNm:" + CommonUtil.deNull(contactNm)
					+ "contactNric:" + CommonUtil.deNull(contactNric) + "designation:" + CommonUtil.deNull(designation)
					+ "docType:" + docType.toString() + "file:" + file + "appDate:"
					+ CommonUtil.deNull(appDate));

			sb.append(" insert into misc_contractor(misc_seq_nbr, exact_loc, exact_desc, ");
			sb.append(" other_desc, fr_dttm, to_dttm, type, psa_license_nbr, remarks, ");
			sb.append(" waive_ind, contract_co_nm, contract_addr, contact_nm, ");
			sb.append(" contact_nric, designation, last_modify_user_id, last_modify_dttm  ) ");
			sb.append(" values (:miscSeqNbr,:location,:description,:others, ");
			sb.append(" to_date(:fromDate,'dd/mm/yyyy HH24:mi'),to_date(:toDate,'dd/mm/yyyy HH24:mi'), ");
			sb.append(" :type,:psaNbr,:remarks,:waiver, ");
			sb.append(" :contCoNm,:contCoAddr,:contactNm,:contactNric, ");
			sb.append(" :designation,:userId,sysdate) ");

//			sb1.append(" insert into misc_upload_doc (misc_seq_nbr, doc_type, upload_file_nm, ");
//			sb1.append(" assign_file_nm, create_user_id, create_dttm) values ( ");
//			sb1.append(" :miscSeqNbr,:docType,:uploadFileNm,:assignFileNm,:userId,sysdate) ");

			miscSeqNbr = insertMiscAppDetails(userId, applyType, status, cust, account, varcode, appDate, null, null);
			// log.info("Going to process Parking for Forklift/Shore
			// Crane Details========> ");

			log.info(" ***addContractorPermitDetails SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);
			paramMap.put("location", location);
			paramMap.put("description", description);
			paramMap.put("others", others);
			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			paramMap.put("type", ""); // Bhuvana 22/2/2016
			paramMap.put("psaNbr", "");
			paramMap.put("remarks", remarks);
			paramMap.put("waiver", waiver);
			paramMap.put("contCoNm", contCoNm);
			paramMap.put("contCoAddr", contCoAddr);
			paramMap.put("contactNm", contactNm);
			paramMap.put("contactNric", contactNric);
			paramMap.put("designation", designation);
			paramMap.put("userId", userId);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			// log.info("Going to process Contract doc Details========>
			// ");
			/*
			 * if(file != null){ assignedFileNameList = getAssignedFileNameList(
			 * (String[])file.toArray(new String[0]), miscSeqNbr); }
			 * 
			 * String name = null; if(file != null){ String[] uploadName =
			 * (String[])file.toArray(new String[0]); for(int i = 0; i < uploadName.length;
			 * i++){ name = uploadName[i].substring(uploadName[i].lastIndexOf("\\")+1);
			 * assignedFileNameList.add(getNextDocSeqNumber(con) + "_" + name); } }
			 */
			sb1.append("update ");
			sb1.append("misc_upload_doc ");
			sb1.append("set ");
			sb1.append("misc_seq_nbr =:miscSeqNbr ");
			sb1.append("where ");
			sb1.append("misc_seq_nbr =:docMiscSeqNbr");
			log.info(" ***addContractorPermitDetails SQL *****" + sb1.toString());
			paramMap = new HashMap<String, Object>();
			
			// Send Email
			if (status != null && status.equals("S")) {
				String alertCode = "MAF";
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = ContractorPermitSubmit_subject;
				String templateEmailFile = TemplateSubmit_body_template;
								
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Contractor Permit");
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * + "Type of Application: Contractor Permit \n" + "Reference No.: " +
				 * CommonUtility.deNull(refNbr) + " \n" + "Application Date/Time: " +
				 * CommonUtility.deNull(appDttm) + " \n" + "Company: " + coName + "\n";
				 */
				
				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Contractor Permit , " + "Ref. No.: "
						+ CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm) + " , by " + coName;
				sendSMS(alertCode, sms);
			}
			log.info("END: *** addContractorPermitDetails Result *****");
		} catch (BusinessException e) {
			log.info("exception: addContractorPermitDetails ", e);
			throw new BusinessException("M4201");
		} catch (NullPointerException e) {
			log.info("exception: addContractorPermitDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: addContractorPermitDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: addContractorPermitDetails  DAO  END");
		}

		log.info("END: *** addContractorPermitDetails Result ***** miscSeqNbr: " + miscSeqNbr);

		return miscSeqNbr;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->addSpreaderDetails()

	@Override
	public boolean addSpreaderDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String coName, String spreaderType, String fromDate, String toDate, String remarks,
			String appDate) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		String miscSeqNbr = null;

		try {
			log.info("START: addSpreaderDetails  DAO  Start userId:" + CommonUtil.deNull(userId) + "applyType:"
					+ CommonUtil.deNull(applyType) + "status:" + CommonUtil.deNull(status) + "cust:"
					+ CommonUtil.deNull(cust) + "account:" + CommonUtil.deNull(account) + "varcode:"
					+ CommonUtil.deNull(varcode) + "coName:" + CommonUtil.deNull(coName) + "spreaderType:"
					+ CommonUtil.deNull(spreaderType) + "fromDate:" + CommonUtil.deNull(fromDate) + "toDate:"
					+ CommonUtil.deNull(toDate) + "remarks:" + CommonUtil.deNull(remarks) + "appDate:"
					+ CommonUtil.deNull(appDate));

			sb1.append(" SELECT VALUE - ( SELECT COUNT(S.MISC_SEQ_NBR) ");
			sb1.append(" FROM MISC_APP A, MISC_SPREADER S ");
			sb1.append(" WHERE A.APP_TYPE='WSS' AND A.APP_STATUS IN ");
			sb1.append(" ('S','A','B','C','P','U') AND A.MISC_SEQ_NBR=S.MISC_SEQ_NBR ");
			sb1.append(" AND S.SPREADER_TYPE=:spreaderType AND ");
			sb1.append(" ((FR_DTTM <= TO_DATE(:fromDate,'DDMMYYYY HH24MI') AND ");
			sb1.append(" TO_DTTM >= TO_DATE(:toDate, 'DDMMYYYY HH24MI')) ");
			sb1.append(" OR (TO_DTTM > TO_DATE(:fromDate,'DDMMYYYY HH24MI') AND ");
			sb1.append(" TO_DTTM <= TO_DATE(:toDate,'DDMMYYYY HH24MI')) ");
			sb1.append(" OR (FR_DTTM >= TO_DATE(:fromDate,'DDMMYYYY HH24MI') AND ");
			sb1.append(" FR_DTTM < TO_DATE(:toDate,'DDMMYYYY HH24MI'))) ) APP ");
			sb1.append(" FROM USER_PARA WHERE PARA_CD='WSS_TOT_' || :spreaderType ");

			// These code to check remainder of the spreader
			log.info(" ***addSpreaderDetails SQL *****" + sb1.toString());

			paramMap.put("spreaderType", spreaderType);
			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
			int row = 0;
			while (rs.next()) {
				row = rs.getInt("APP");
				if (row <= 0) {
					log.info("END: addSpreaderDetails  DAO  END ***** Result: false");
					return false;
				}
			}

			miscSeqNbr = insertMiscAppDetails(userId, applyType, status, cust, account, varcode, appDate, null, null);
			// log.info("Hire of Wooden / Steel Spreader : Going to
			// process details ========> ");
			paramMap = new HashMap<String, Object>();
			sb.append(" insert into misc_spreader (misc_seq_nbr, fr_dttm, to_dttm, remarks, ");
			// " issue_dttm, issue_by_staff, receive_by_cust, return_dttm, receive_by_staff,
			// return_by_cust, " +
			sb.append(" last_modify_user_id, last_modify_dttm, spreader_type) values ( ");
			sb.append(" :miscSeqNbr,to_date(:fromDate,'dd/mm/yyyy HH24:mi'), ");
			sb.append(" to_date(:toDate,'dd/mm/yyyy HH24:mi'), ");
			sb.append(" :remarks,:userId,sysdate,:spreaderType) ");

			log.info(" ***addSpreaderDetails SQL2 *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);
			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			paramMap.put("remarks", remarks);
			paramMap.put("userId", userId);
			paramMap.put("spreaderType", spreaderType);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			// log.info("Hire of Wooden / Steel Spreader : Inserted
			// Details========> ");

			// Sending Mail
			if (status != null && status.equals("S")) {
				String alertCode = "MAF";
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = HireWoodenSubmit_subject;
				String templateEmailFile = TemplateSubmit_body_template;
				
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
				
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Hire of Wooden / Steel Spreader");
				
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * + "Type of Application: Hire of Wooden / Steel Spreader \n" +
				 * "Reference No.: " + CommonUtility.deNull(refNbr) + " \n" +
				 * "Application Date/Time: " + CommonUtility.deNull(appDttm) + " \n" +
				 * "Company: " + coName + "\n";
				 */
				
				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Hire of Wooden / Steel Spreader , " + "Ref. No.: "
						+ CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm) + " , by " + coName;
				sendSMS(alertCode, sms);
			}
			log.info("END: addSpreaderDetails  DAO  END ***** Result: true");
			return true;
		} catch (BusinessException e) {
			log.info("exception: addSpreaderDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: addSpreaderDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: addSpreaderDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: addSpreaderDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->addHotworkDetails()
	// Hot Work Permit

	@Override
	public void addHotworkDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String coName, String location, String description, String fromDate, String toDate,
			String appDate) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String miscSeqNbr = null;

		try {
			log.info("START: addHotworkDetails  DAO  Start userId:" + CommonUtil.deNull(userId) + "applyType:"
					+ CommonUtil.deNull(applyType) + "status:" + CommonUtil.deNull(status) + "cust:"
					+ CommonUtil.deNull(cust) + "account:" + CommonUtil.deNull(account) + "varcode:"
					+ CommonUtil.deNull(varcode) + "coName:" + CommonUtil.deNull(coName) + "location:"
					+ CommonUtil.deNull(location) + "fromDate:" + CommonUtil.deNull(fromDate) + "toDate:"
					+ CommonUtil.deNull(toDate) + "appDate:" + CommonUtil.deNull(appDate));

			miscSeqNbr = insertMiscAppDetails(userId, applyType, status, cust, account, varcode, appDate, null, null);

			sb.append(" insert into misc_hotwork ( misc_seq_nbr, hw_loc, hw_desc, ");
			sb.append(" fr_dttm, to_dttm, ");
			// " tot_standby_hr, " +
			sb.append(" last_modify_user_id, last_modify_dttm) values ( ");
			sb.append(" " + miscSeqNbr + ",:location,:description,to_date(:fromDate,'dd/mm/yyyy HH24:mi'), ");
			sb.append(" to_date(:toDate,'dd/mm/yyyy HH24:mi'),:userId,sysdate) ");

			log.info(" ***addHotworkDetails SQL *****" + sb.toString());

			paramMap.put("location", location);
			paramMap.put("description", description);
			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			paramMap.put("userId", userId);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			// Sending Mail
			if (status != null && status.equals("S")) {
				String alertCode = "MAF";
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = HotWorkPermitSubmit_subject;
				String templateEmailFile = TemplateSubmit_body_template;
				
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Hot Work Permit");
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
						
				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * + "Type of Application: Hot Work Permit \n" + "Reference No.: " +
				 * CommonUtility.deNull(refNbr) + " \n" + "Application Date/Time: " +
				 * CommonUtility.deNull(appDttm) + " \n" + "Company: " + coName + "\n";
				 */
				
				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Hot Work Permit , " + "Ref. No.: "
						+ CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm) + " , by " + coName;
				sendSMS(alertCode, sms);
			}
			log.info("END: *** addHotworkDetails Result *****");
		} catch (BusinessException e) {
			log.info("exception: addHotworkDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: addHotworkDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: addHotworkDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: addHotworkDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getOvernightParkingVehicleDetails()
	// START 02-Mar-2011 - TPA - Thanhnv2 added to get list of Trailer Parking
	// Application for Approve.
	/**
	 * To get get list of Trailer Parking Application for Approve
	 */

	@Override
	public List<Object> getOvernightParkingVehicleDetails(String userId, String applyType, String appSeqNbr,
			String applyTypeNm) throws BusinessException {
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		SqlRowSet rs3 = null;
		SqlRowSet rs4 = null;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		StringBuilder sb3 = new StringBuilder();
		StringBuilder sb4 = new StringBuilder();
		List<Object> result = new ArrayList<Object>();

		try {
			log.info("START: getOvernightParkingVehicleDetails  DAO  Start userId:" + CommonUtil.deNull(userId)
					+ " applyType:" + CommonUtil.deNull(applyType) + " appSeqNbr:" + CommonUtil.deNull(appSeqNbr)
					+ " applyTypeNm:" + CommonUtil.deNull(applyTypeNm));

			sb.append(" SELECT /* MiscAppEJB - getTpaForApproveDetails(vehSql) */ mv.misc_seq_nbr, ");
			sb.append(" to_char(mv.fr_dttm,'ddmmyyyy') frDttm, to_char(mv.fr_dttm,'HH24mi') frTime,  ");
			sb.append(
					" to_char(mv.to_dttm, 'ddmmyyyy') toDttm, to_char(mv.to_dttm, 'HH24mi') toTime, mv.nbr_night, mv.park_reason,   ");
			sb.append(" to_char(mv.actual_fr_dttm,'ddmmyyyy') actualFrDttm,   ");
			sb.append(" to_char(mv.actual_to_dttm, 'ddmmyyyy') actualToDttm,   ");
			sb.append(" mv.actual_nbr_hour, mv.last_modify_user_id, mv.last_modify_dttm, mv.cargo_type,   ");
			sb.append(" mtc.misc_type_nm, ACTUAL_NBR_NIGHT   ");
			sb.append(" FROM misc_vehicle mv  ");
			sb.append(" LEFT JOIN misc_type_code mtc ON mv.park_reason_cd = mtc.misc_type_cd  ");
			sb.append(" WHERE mv.misc_seq_nbr =:appSeqNbr  ");

			sb1.append(" SELECT /* MiscAppEJB - getTpaForApproveDetails(vehDetSql) */ ");
			sb1.append(" mvd.misc_seq_nbr, mvd.item_nbr, mvd.veh_chas_nbr, mvd.cntr_nbr, mvd.asn_nbr, ");
			sb1.append(" mvd.area_cd, mvd.slot_nbr, ");
			sb1.append(" mvd.last_modify_user_id, mvd.last_modify_dttm, mvd.pref_area_cd, mvd.remarks ");
			sb1.append(" FROM misc_vehicle_det mvd ");
			sb1.append(" WHERE mvd.misc_seq_nbr =:appSeqNbr ORDER BY mvd.item_nbr ");

			sb2.append(" SELECT (CNTR_STORAGE_CD||'/'||JP_GROUP||'/'||IMO_CL) CNTR_DG_DESC ");
			sb2.append(" FROM ");
			sb2.append(" (SELECT CNTR_STORAGE_CD, JP_GROUP, IMO_CL ");
			sb2.append(" FROM PM4 ");
			sb2.append(" WHERE CNTR_SEQ_NBR = ");
			sb2.append(" (SELECT CNTR_SEQ_NBR ");
			sb2.append(" FROM CNTR WHERE CNTR_NBR=:cntrNbr AND TXN_STATUS='A') ");
			sb2.append("  HAVING CNTR_TEU = ");
			sb2.append(" ( ");
			sb2.append(" SELECT MAX(CNTR_TEU) ");
			sb2.append(" FROM PM4 B ");
			sb2.append(" WHERE B.CNTR_SEQ_NBR = ");
			sb2.append(" (SELECT CNTR_SEQ_NBR ");
			sb2.append(" FROM CNTR ");
			sb2.append(" WHERE CNTR_NBR=:cntrNbr AND TXN_STATUS='A' ");
			sb2.append(" ) AND RECORD_TYPE<>'D' ");
			sb2.append("  ) ");
			sb2.append(" AND MPA_APPV_STATUS = 'A' ");
			sb2.append(" AND JP_APPV_STATUS = 'A' ");
			sb2.append(" AND RECORD_TYPE <> 'D' ");
			sb2.append(" GROUP BY CNTR_STORAGE_CD, JP_GROUP, CNTR_GROUP, ");
			sb2.append(" IMO_CL, CNTR_TEU, CNTR_SEQ_NBR, MPA_APPV_STATUS, ");
			sb2.append(" JP_APPV_STATUS, RECORD_TYPE, OPR_TYPE ");
			sb2.append(" ) ");

			sb3.append(" SELECT (DG_STORAGE_CD||'/'||JP_GROUP||'/'||IMO_CL) CARGO_DG_DESC ");
			sb3.append(" FROM PM4 ");
			sb3.append(" WHERE( ");
			sb3.append(" BL_NBR= ");
			sb3.append(" ( ");
			sb3.append(" SELECT BL_NBR ");
			sb3.append(" FROM GB_EDO ");
			sb3.append(" WHERE EDO_ASN_NBR =:asnNbr  ");
			sb3.append(" ) ");
			sb3.append(" OR ");
			sb3.append(" UCR_NBR= ");
			sb3.append(" ( ");
			sb3.append(" SELECT BK_REF_NBR ");
			sb3.append(" FROM ESN ");
			sb3.append(" WHERE ESN_ASN_NBR =:asnNbr  ");
			sb3.append(" ) ");
			sb3.append(" ) ");
			sb3.append(" AND MPA_APPV_STATUS = 'A' ");
			sb3.append(" AND JP_APPV_STATUS = 'A' ");
			sb3.append(" AND RECORD_TYPE <> 'D' ");

			sb4.append(" SELECT NVL2(OOG_UNIT,  ");
			sb4.append(" (DECODE(OOG_UNIT,'C','CM',OOG_UNIT)||'/'|| OOG_OH||'/'||  ");
			sb4.append(" OOG_OL_FRONT||'/'||OOG_OL_BACK||'/'||  ");
			sb4.append(" OOG_OW_RIGHT||'/'|| OOG_OW_LEFT),  ");
			sb4.append(" '') OOG_DESC  ");
			sb4.append(" FROM CNTR  ");
			sb4.append(" WHERE CNTR_NBR =:cntrNbr AND TXN_STATUS = 'A' ");

			List<Object> temp = (ArrayList<Object>) getMiscAppDetails(userId, applyType, appSeqNbr, applyTypeNm);
			if (temp != null && temp.size() > 0) {
				result.add(temp.get(0));// MiscAppValueObject
				result.add(temp.get(1));// MiscCustValueObject
			}
			MiscVehValueObject veh = new MiscVehValueObject();

			log.info("MiscAppEJB - getTpaForApproveDetails(vehSql)========> " + sb.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				veh.setFromDate(CommonUtility.deNull(rs.getString("frDttm")));
				veh.setFromTime(CommonUtility.deNull(rs.getString("frTime")));
				veh.setToDate(CommonUtility.deNull(rs.getString("toDttm")));
				veh.setToTime(CommonUtility.deNull(rs.getString("toTime")));
				veh.setNoNights(rs.getString("nbr_night"));
				veh.setParkReason(CommonUtility.deNull(rs.getString("park_reason")));
				veh.setApplicationReason(CommonUtility.deNull(rs.getString("misc_type_nm")));
				veh.setActFromDate(CommonUtility.deNull(rs.getString("actualFrDttm")));
				veh.setActToDate(CommonUtility.deNull(rs.getString("actualToDttm")));
				veh.setActNoHours(CommonUtility.deNull(rs.getString("actual_nbr_hour")));
				veh.setCargoType(CommonUtility.deNull(rs.getString("cargo_type")));
				veh.setActNoNights(CommonUtility.deNull(rs.getString("ACTUAL_NBR_NIGHT")));
			}

			List<String> vehChasNbr = new ArrayList<String>();
			List<String> cntrNbr = new ArrayList<String>();
			List<String> asnNbr = new ArrayList<String>();
			List<String> prefAreaCd = new ArrayList<String>();
			List<String> remarks = new ArrayList<String>();
			List<String> dgInfo = new ArrayList<String>();
			List<String> oogInfo = new ArrayList<String>();
			List<String> area = new ArrayList<String>();
			List<String> slot = new ArrayList<String>();

			paramMap = new HashMap<String, Object>();
			log.info("MiscAppEJB - getTpaForApproveDetails(vehDetSql)========> " + sb1.toString());
			paramMap.put("appSeqNbr", appSeqNbr);

			rs1 = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
			int count = 0;
			while (rs1.next()) {
				vehChasNbr.add(CommonUtility.deNull(rs1.getString("veh_chas_nbr")));
				cntrNbr.add(CommonUtility.deNull(rs1.getString("cntr_nbr")));
				asnNbr.add(CommonUtility.deNull(rs1.getString("asn_nbr")));
				prefAreaCd.add(CommonUtility.deNull(rs1.getString("pref_area_cd")));
				remarks.add(CommonUtility.deNull(rs1.getString("remarks")));
				area.add(CommonUtility.deNull(rs1.getString("area_cd")));
				slot.add(CommonUtility.deNull(rs1.getString("slot_nbr")));
				count++;
			}
			veh.setVehChasNbr((String[]) vehChasNbr.toArray(new String[0]));
			veh.setCntrNbr((String[]) cntrNbr.toArray(new String[0]));
			veh.setAsnNbr((String[]) asnNbr.toArray(new String[0]));
			veh.setPreferredArea((String[]) prefAreaCd.toArray(new String[0]));
			veh.setRemarks((String[]) remarks.toArray(new String[0]));
			veh.setArea((String[]) area.toArray(new String[0]));
			veh.setSlot((String[]) slot.toArray(new String[0]));

			String dg = null;
			String oog = null;
			for (int i = 0; i < count; i++) {
				dg = "";
				oog = "";
				if ("D".equals(veh.getCargoType())) {
					if (cntrNbr.get(i) != null && !"".equals(cntrNbr.get(i))) {

						paramMap = new HashMap<String, Object>();
						paramMap.put("cntrNbr", (String) cntrNbr.get(i));
						log.info("MiscAppEJB - getTpaForApproveDetails(cntrDgDescSql)========> " + sb2.toString());
						rs2 = namedParameterJdbcTemplate.queryForRowSet(sb2.toString(), paramMap);
						while (rs2.next()) {
							dg = CommonUtility.deNull(rs2.getString("CNTR_DG_DESC"));
						}
						dgInfo.add(dg);
						oogInfo.add("");

					} else if (asnNbr.get(i) != null && !"".equals(asnNbr.get(i))
							&& NumberUtils.isNumber((String) asnNbr.get(i))) {
						paramMap = new HashMap<String, Object>();

						paramMap.put("asnNbr", (String) asnNbr.get(i));

						log.info("MiscAppEJB - getTpaForApproveDetails(cargoDgDescSql)========> " + sb3.toString());
						rs3 = namedParameterJdbcTemplate.queryForRowSet(sb3.toString(), paramMap);
						while (rs3.next()) {
							dg = CommonUtility.deNull(rs3.getString("CARGO_DG_DESC"));
						}
						dgInfo.add(dg);
						oogInfo.add("");

					} else {
						oogInfo.add("");
						dgInfo.add("");
					}
				} else if ("O".equals(veh.getCargoType())) {
					paramMap = new HashMap<String, Object>();
					paramMap.put("cntrNbr", (String) cntrNbr.get(i));
					log.info("MiscAppEJB - getTpaForApproveDetails(oogDescSql)========> " + sb4.toString());
					rs4 = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
					while (rs4.next()) {
						oog = CommonUtility.deNull(rs4.getString("OOG_DESC"));
					}
					oogInfo.add(oog);
					dgInfo.add("");
				} else {
					oogInfo.add("");
					dgInfo.add("");
				}
			}
			veh.setDgInfo((String[]) dgInfo.toArray(new String[0]));
			veh.setOogInfo((String[]) oogInfo.toArray(new String[0]));
			result.add(veh);

			log.info("END: *** getOvernightParkingVehicleDetails Result *****" + result.toString());
		} catch (BusinessException e) {
			log.info("exception: getOvernightParkingVehicleDetails ", e);
			throw new BusinessException(e.getMessages());
		} catch (NullPointerException e) {
			log.info("exception: getOvernightParkingVehicleDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getOvernightParkingVehicleDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getOvernightParkingVehicleDetails  DAO  END");
		}
		return result;
	}

	// END 02-Mar-2011 - TPA - Thanhnv2 added to get list of Trailer Parking
	// Application for Approve.

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateOvernightParkingVehicleDetails()

	@Override
	public void updateOvernightParkingVehicleDetails(String userId, String miscSeqNbr, String status, String fromDate,
			String toDate, String noNights, String parkReason, String[] vehNo, String[] cntNo, String[] asnNo,
			String coName, String applyType, String account, String appStatusCd, String conPerson, String conTel)
			throws BusinessException {
		updateOvernightParkingVehicleDetails(userId, miscSeqNbr, status, fromDate, toDate, noNights, parkReason, vehNo,
				cntNo, asnNo, coName, applyType, account, appStatusCd, conPerson, conTel, "");
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateOvernightParkingVehicleDetails()
	/*
	 * public void updateOvernightParkingVehicleDetails(String userId, String
	 * miscSeqNbr, String status, String fromDate, String toDate, String noNights,
	 * String parkReason, String[] vehNo, String[] cntNo, String[] asnNo, String
	 * coName, String applyType, String account, String appStatusCd,String
	 * conPerson,String conTel) throws BusinessException{
	 */

	// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

	@Override
	public void updateOvernightParkingVehicleDetails(String userId, String miscSeqNbr, String status, String fromDate,
			String toDate, String noNights, String parkReason, String[] vehNo, String[] cntNo, String[] asnNo,
			String coName, String applyType, String account, String appStatusCd, String conPerson, String conTel,
			String conEmail) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();

		try {
			log.info("START: updateOvernightParkingVehicleDetails  DAO  Start userId:" + CommonUtil.deNull(userId)
					+ " miscSeqNbr:" + CommonUtil.deNull(miscSeqNbr) + " status:" + CommonUtil.deNull(status)
					+ " fromDate:" + CommonUtil.deNull(fromDate) + " toDate:" + CommonUtil.deNull(toDate) + " noNights:"
					+ CommonUtil.deNull(noNights) + " parkReason:" + CommonUtil.deNull(parkReason) + " vehNo:"
					+ vehNo.toString() + " cntNo:" + cntNo.toString() + " asnNo:" + asnNo.toString() + " coName:"
					+ CommonUtil.deNull(coName) + "applyType:" + CommonUtil.deNull(applyType) + " account:"
					+ CommonUtil.deNull(account) + " appStatusCd:" + CommonUtil.deNull(appStatusCd) + " conPerson:"
					+ CommonUtil.deNull(conPerson) + " conTel:" + CommonUtil.deNull(conTel) + " conEmail:"
					+ CommonUtil.deNull(conEmail));

			sb.append(" update /* MiscAppEJB - updateOvernightParkingVehicleDetails(vehSql) */ ");
			sb.append(" misc_vehicle set fr_dttm = to_date(:fromDate,'dd/mm/yyyy'), ");
			sb.append(" to_dttm = to_date(:toDate,'dd/mm/yyyy'), nbr_night =:noNights, ");
			sb.append(" park_reason=:parkReason , last_modify_user_id =:userId, ");
			sb.append(" last_modify_dttm = sysdate where misc_seq_nbr =:miscSeqNbr ");

			sb1.append(" delete /* MiscAppEJB - updateOvernightParkingVehicleDetails(vehDelSql) */ ");
			sb1.append(" from misc_vehicle_det where misc_seq_nbr = :miscSeqNbr ");

			sb2.append(" insert into misc_vehicle_det(misc_seq_nbr, item_nbr, veh_chas_nbr, ");
			sb2.append(" cntr_nbr, asn_nbr, last_modify_user_id, last_modify_dttm) values ");
			sb2.append(" (:miscSeqNbr,:itemNbr,:vehNo,:cntNo,:asnNo,:userId,sysdate) ");

			// updateMiscAppDetails( userId, status, miscSeqNbr, applyType, account,
			// appStatusCd);
			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
			updateMiscAppDetails(userId, status, miscSeqNbr, applyType, account, appStatusCd, conPerson, conTel,
					conEmail);
			// log.info("Updated MiscAppDetails ========> ");

			log.info(" ***updateOvernightParkingVehicleDetails SQL *****" + sb.toString());

			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			paramMap.put("noNights", noNights);
			paramMap.put("parkReason", parkReason);
			paramMap.put("userId", userId);
			paramMap.put("miscSeqNbr", miscSeqNbr);
			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			// log.info("Delete Veh Details ========> ");

			paramMap = new HashMap<String, Object>();

			log.info(" ***updateOvernightParkingVehicleDetails SQL *****" + sb1.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb1.toString(), paramMap);
			// log.info("Deleted!!!");

			// log.info("Going to process veh Details========> ");
			if (vehNo != null) {
				log.info(" ***updateOvernightParkingVehicleDetails SQL2 *****" + sb2.toString());
				paramMap = new HashMap<String, Object>();
				for (int i = 0, j = 1; i < vehNo.length; i++, j++) {

					paramMap.put("miscSeqNbr", miscSeqNbr);
					paramMap.put("itemNbr", j);
					paramMap.put("vehNo",
							(vehNo[i] != null && !vehNo[i].equals(""))
									? CommonUtility.getStringTokens(vehNo[i]).toUpperCase()
									: vehNo[i]);
					paramMap.put("cntNo", cntNo[i]);
					paramMap.put("asnNo", asnNo[i]);
					paramMap.put("userId", userId);

					log.info(" *** paramMap: *****" + paramMap.toString());
					if ((vehNo[i] != null && !vehNo[i].equals(""))) {
						namedParameterJdbcTemplate.update(sb2.toString(), paramMap);
					}
				}
			}
			// Send Email
			if (status != null && status.equals("S")) {
				String alertCode = ConstantUtil.alertCode;
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = OvernightParkVecSubmit_subject; 
				String templateEmailFile = TemplateSubmit_body_template;
				
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Overnight Parking of Vehicle");
				
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * + "Type of Application: Overnight Parking of Vehicle \n" + "Reference No.: "
				 * + CommonUtility.deNull(refNbr) + " \n" + "Application Date/Time: " +
				 * CommonUtility.deNull(appDttm) + " \n" + "Company: " + coName + "\n";
				 */
				
				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Overnight Parking of Vehicle, " + "Ref. No.: "
						+ CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm) + " , by " + coName;
				sendSMS(alertCode, sms);
			}
			log.info("END: *** updateOvernightParkingVehicleDetails Result *****");
		} catch (BusinessException e) {
			log.info("exception: updateOvernightParkingVehicleDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: updateOvernightParkingVehicleDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateOvernightParkingVehicleDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateOvernightParkingVehicleDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getStationingOfMacDetails()

	@Override
	public List<Object> getStationingOfMacDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		SqlRowSet rs3 = null;
		StringBuilder macSql = new StringBuilder();
		StringBuilder macDetSql = new StringBuilder();
		StringBuilder macDocSql = new StringBuilder();
		List<Object> result = new ArrayList<Object>();

		try {
			log.info("START: getStationingOfMacDetails  DAO  Start userId:" + CommonUtil.deNull(userId) + "applyType:"
					+ CommonUtil.deNull(applyType) + "appSeqNbr:" + CommonUtil.deNull(appSeqNbr) + "applyTypeNm:"
					+ CommonUtil.deNull(applyTypeNm));

			macSql.append(" select /* MiscAppEJB - getStationingOfMacDetails() */ ");
			macSql.append(" misc_seq_nbr, mac_type, to_char(fr_dttm,'ddmmyyyy') fromDttm, ");
			macSql.append(" to_char(fr_dttm,'HH24mi') fromTime, to_char(to_dttm,'ddmmyyyy') toDttm, ");
			macSql.append(" to_char(to_dttm,'HH24mi') toTime, remarks from misc_machine ");
			macSql.append(" where misc_seq_nbr =:appSeqNbr ");

			// Sripriya 13/05/2011
			macDetSql.append(" select /* MiscAppEJB - getStationingOfMacDetails() */ ");
			macDetSql.append(" item_nbr, UPPER(REPLACE(reg_nbr,' ','')) reg_nbr, ");
			macDetSql.append(" lift_capacity, insurance_nbr, ");
			macDetSql.append(" to_char(insurance_exp_dttm,'ddmmyyyy') insurance_exp_dttm, ");
			macDetSql.append(" to_char(phase_out_dt,'ddmmyyyy') phase_out_dt from misc_machine_det ");
			macDetSql.append(" where misc_seq_nbr =:appSeqNbr ");

			macDocSql.append(" select /* MiscAppEJB - getStationingOfMacDetails() */ doc_type, ");
			macDocSql.append(" a.misc_type_nm typeNm, upload_file_nm, create_user_id, ");
			macDocSql.append(" to_char(create_dttm,'ddmmyyyy HH24mi') create_dttm, assign_file_nm ");
			macDocSql.append(" from misc_upload_doc, misc_type_code a where misc_seq_nbr =:appSeqNbr ");
			macDocSql.append(" and a.cat_cd = 'MISC_MDOC' and  misc_upload_doc.doc_type = a.misc_type_cd ");
			macDocSql.append(" and a.rec_status = 'A' order by create_dttm ");

			List<Object> temp = (ArrayList<Object>) getMiscAppDetails(userId, applyType, appSeqNbr, applyTypeNm);
			if (temp != null && temp.size() > 0) {
				result.add(temp.get(0));// MiscAppValueObject
				result.add(temp.get(1));// MiscCustValueObject
			}
			// log.info("Got MiscApp Details========> " + temp.size());
			MiscParkMacValueObject parkMac = new MiscParkMacValueObject();

			log.info(" ***getStationingOfMacDetails SQL *****" + macSql.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(macSql.toString(), paramMap);
			if (rs.next()) {
				parkMac.setMacType(CommonUtility.deNull(rs.getString("mac_type")));
				parkMac.setFromDate(CommonUtility.deNull(rs.getString("fromDttm")));
				parkMac.setFromTime(CommonUtility.deNull(rs.getString("fromTime")));
				parkMac.setToDate(CommonUtility.deNull(rs.getString("toDttm")));
				parkMac.setToTime(CommonUtility.deNull(rs.getString("toTime")));
				parkMac.setRemarks(CommonUtility.deNull(rs.getString("remarks")));
			}

			// log.info("Got Mac Details========> ");

			// log.info("Going to get macDet Details========> ");

			List<String> regNbr = new ArrayList<String>();
			List<String> liftCapacity = new ArrayList<String>();
			List<String> insuranceNbr = new ArrayList<String>();
			List<String> insExpDttm = new ArrayList<String>();
			List<String> phaseOutDt = new ArrayList<String>();

			paramMap = new HashMap<String, Object>();
			log.info(" ***getStationingOfMacDetails SQL2 *****" + macDetSql.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs1 = namedParameterJdbcTemplate.queryForRowSet(macDetSql.toString(), paramMap);
			while (rs1.next()) {
				regNbr.add(CommonUtility.deNull(rs1.getString("reg_nbr")));
				liftCapacity.add(CommonUtility.deNull(rs1.getString("lift_capacity")));
				insuranceNbr.add(CommonUtility.deNull(rs1.getString("insurance_nbr")));
				insExpDttm.add(CommonUtility.deNull(rs1.getString("insurance_exp_dttm")));
				phaseOutDt.add(CommonUtility.deNull(rs1.getString("phase_out_dt")));
			}
			parkMac.setRegNbr((String[]) regNbr.toArray(new String[0]));
			parkMac.setLiftCapacity((String[]) liftCapacity.toArray(new String[0]));
			parkMac.setInsuranceNbr((String[]) insuranceNbr.toArray(new String[0]));
			parkMac.setInsExpDttm((String[]) insExpDttm.toArray(new String[0]));
			parkMac.setPhaseOutDt((String[]) phaseOutDt.toArray(new String[0]));

			// log.info("Going to get doc Details========> ");
			String fullPath = UploadDocument.getOutputFileDir("MACHINE", "upload");
			List<String> docType = new ArrayList<String>();
			List<String> docTypeCd = new ArrayList<String>();
			List<String> docName = new ArrayList<String>();
			List<String> uploadDttm = new ArrayList<String>();
			List<String> uploadBy = new ArrayList<String>();
			List<String> assignedFileName = new ArrayList<String>();

			paramMap = new HashMap<String, Object>();
			log.info(" ***getStationingOfMacDetails SQL *****" + macDocSql.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			rs2 = namedParameterJdbcTemplate.queryForRowSet(macDocSql.toString(), paramMap);
			while (rs2.next()) {
				docType.add(CommonUtility.deNull(rs2.getString("doc_type")));
				docTypeCd.add(CommonUtility.deNull(rs2.getString("typeNm")));
				docName.add(CommonUtility.deNull(rs2.getString("upload_file_nm")));
				uploadDttm.add(CommonUtility.deNull(rs2.getString("create_dttm")));
				uploadBy.add(getUserName(CommonUtility.deNull(rs2.getString("create_user_id"))));
				assignedFileName.add(fullPath + CommonUtility.deNull(rs2.getString("assign_file_nm")));
			}
			parkMac.setDocType((String[]) docType.toArray(new String[0]));
			parkMac.setDocTypeCd((String[]) docTypeCd.toArray(new String[0]));
			parkMac.setDocName((String[]) docName.toArray(new String[0]));
			parkMac.setUploadDttm((String[]) uploadDttm.toArray(new String[0]));
			parkMac.setUploadBy((String[]) uploadBy.toArray(new String[0]));
			parkMac.setAssignedFileName((String[]) assignedFileName.toArray(new String[0]));
			result.add(parkMac);
			// log.info("<========= End getStationingOfMacDetails()
			// ========> ");

			// Added by Punitha on 24/01/2008
			paramMap = new HashMap<String, Object>();
			log.info(" ***getStationingOfMacDetails SQL *****" + macDetSql.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs3 = namedParameterJdbcTemplate.queryForRowSet(macDetSql.toString(), paramMap);
			ArrayList<MiscParkMacValueObject> macDetList = new ArrayList<MiscParkMacValueObject>();
			while (rs3.next()) {
				parkMac = new MiscParkMacValueObject();
				parkMac.setRegNbrValue(CommonUtility.deNull(rs3.getString("reg_nbr")));
				parkMac.setLiftCapacityValue(CommonUtility.deNull(rs3.getString("lift_capacity")));
				parkMac.setInsuranceNbrValue(CommonUtility.deNull(rs3.getString("insurance_nbr")));
				parkMac.setInsExpDttmValue(CommonUtility.deNull(rs3.getString("insurance_exp_dttm")));
				parkMac.setPhaseOutDtValue(CommonUtility.deNull(rs3.getString("phase_out_dt")));
				macDetList.add(parkMac);
			}
			result.add(macDetList);

			log.info("END: *** getStationingOfMacDetails Result *****" + result.toString());
		} catch (BusinessException e) {
			log.info("exception: getStationingOfMacDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getStationingOfMacDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getStationingOfMacDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getStationingOfMacDetails  DAO  END");
		}

		log.info("END: *** getStationingOfMacDetails Result *****" + result.toString());
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateStationingOfMacDetails()

	@Override
	public void updateStationingOfMacDetails(String userId, String miscSeqNbr, String status, String coName,
			String applyType, String account, String appStatusCd, MiscParkMacValueObject obj) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();

		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		StringBuilder sb4 = new StringBuilder();

		try {
			log.info("START: updateStationingOfMacDetails  DAO  Start userId:" + CommonUtil.deNull(userId)
					+ "miscSeqNbr:" + CommonUtil.deNull(miscSeqNbr) + "status:" + CommonUtil.deNull(status) + "coName:"
					+ CommonUtil.deNull(coName) + "applyType:" + CommonUtil.deNull(applyType) + "account:"
					+ CommonUtil.deNull(account) + "appStatusCd:" + CommonUtil.deNull(appStatusCd) + "obj:"
					+ obj.toString());

			sb.append(" update /* MiscAppEJB - updateStationingOfMacDetails() */ misc_machine ");
			sb.append(" set mac_type = :macType, fr_dttm = to_date(:fromDate,'dd/mm/yyyy'), ");
			sb.append(" to_dttm = to_date(:toDate,'dd/mm/yyyy'), last_modify_user_id = :userId, ");
			sb.append(" last_modify_dttm = sysdate where misc_seq_nbr = :miscSeqNbr ");
			String macSql = sb.toString();

			sb1.append(" delete /* MiscAppEJB - updateStationingOfMacDetails() */ ");
			sb1.append(" from misc_machine_det where misc_seq_nbr =:miscSeqNbr ");
			String macDelSql = sb1.toString();

			sb2.append(" insert into misc_machine_det (misc_seq_nbr, item_nbr, reg_nbr, ");
			sb2.append(" lift_capacity, insurance_nbr, insurance_exp_dttm, phase_out_dt, ");
			sb2.append(" last_modify_user_id, last_modify_dttm) ");
			sb2.append(" values (:miscSeqNbr,:itemNbr,:regNbr,:liftCapacity,:insuranceNbr, ");
			sb2.append(" to_date(:insExpDttm,'dd/mm/yyyy'), ");
			sb2.append(" to_date(:phaseOutDt,'dd/mm/yyyy'),:userId,sysdate) ");
			String macDetSql = sb2.toString();

//			sb3.append(" delete from misc_upload_doc where misc_seq_nbr = :miscSeqNbr ");
//			String macDocDelSql = sb3.toString();

			sb4.append(" insert into misc_upload_doc (misc_seq_nbr, doc_type, upload_file_nm, ");
			sb4.append(" assign_file_nm, create_user_id, create_dttm) values (:miscSeqNbr,:docType, ");
			sb4.append(" :uploadFileNm,:assignFileNm,:userId,sysdate) ");
			String macDocSql = sb4.toString();

			updateMiscAppDetails(userId, status, miscSeqNbr, applyType, account, appStatusCd, null, null);
			// log.info("Updated MiscAppDetails ========> ");
			// log.info("From Date ========> " + obj.getFromDate());
			// log.info("To Date ========> " + obj.getToDate());

			log.info(" ***updateStationingOfMacDetails SQL *****" + macSql.toString());

			paramMap.put("macType", obj.getMacType());
			paramMap.put("fromDate", obj.getFromDate());
			paramMap.put("toDate", obj.getToDate());
			paramMap.put("userId", userId);
			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(macSql.toString(), paramMap);

			log.info(" ***updateStationingOfMacDetails macDelSql SQL2 *****" + macDelSql.toString());

			paramMap = new HashMap<String, Object>();
			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(macDelSql.toString(), paramMap);

			log.info("Deleted MacDet Details !!!");

			log.info("Going to process MacDet Details========> ");
			// String[] regNbr = obj.getRegNbr();
			// String[] liftCapacity = obj.getLiftCapacity();
			// String[] insuranceNbr = obj.getInsuranceNbr();
			// String[] insExpDttm = obj.getInsExpDttm();
			// String[] phaseOutDt = obj.getPhaseOutDt();
			/*
			 * log.info("regNbr========> " + regNbr.length);
			 * log.info("liftCapacity========> " + liftCapacity);
			 * log.info("insuranceNbr========> " + insuranceNbr);
			 * log.info("insExpDttm========> " + insExpDttm);
			 */
			// Amended by Punitha on 24/01/2008
			/*
			 * if(phaseOutDt != null) //log.info("phaseOutDt========> " +
			 * phaseOutDt.length);
			 * 
			 * pstmt = con.prepareStatement(macDetSql); if(regNbr != null){ for(int i=0,j=1;
			 * i <regNbr.length; i++,j++){ paramMap.put(1, miscSeqNbr); paramMap.put(2,
			 * j+""); paramMap.put(3, regNbr[i]); paramMap.put(4, liftCapacity[i]);
			 * paramMap.put(5, insuranceNbr[i]); paramMap.put(6, insExpDttm[i]);
			 * if(phaseOutDt != null && phaseOutDt.length > i && phaseOutDt[i] != null){
			 * //log.info("phaseOutDt========> " + phaseOutDt.length); paramMap.put(7,
			 * phaseOutDt[i]); }else{ paramMap.put(7, null); } paramMap.put(8, userId);
			 * 
			 * namedParameterJdbcTemplate.update(sb.toString(), paramMap); } }
			 * pstmt.close();
			 */
			// End by Punitha
			// //Added by Punitha on 24/01/2008
			List<MiscParkMacValueObject> macDetList = obj.getMacDetList();
			paramMap = new HashMap<String, Object>();
			log.info(" ***updateStationingOfMacDetails macDetSql SQL3 *****" + macDetSql.toString());

			if (macDetList != null) {
				for (int i = 0; i < macDetList.size(); i++) {
					// MiscParkMacValueObject vo =
					// objectMapper.getTypeFactory().constructCollectionType(ArrayList.class,
					// MiscParkMacValueObject.class);
					MiscParkMacValueObject vo = (MiscParkMacValueObject) macDetList.get(i);
					paramMap.put("miscSeqNbr", miscSeqNbr);
					paramMap.put("itemNbr", String.valueOf(i + 1));
					paramMap.put("regNbr", (String) vo.getRegNbrValue());
					paramMap.put("liftCapacity", (String) vo.getLiftCapacityValue());
					paramMap.put("insuranceNbr", (String) vo.getInsuranceNbrValue());
					paramMap.put("insExpDttm", (String) vo.getInsExpDttmValue());
					paramMap.put("phaseOutDt", (String) CommonUtility.deNull(vo.getPhaseOutDtValue()));
					paramMap.put("userId", userId);

					log.info(" *** paramMap: *****" + paramMap.toString());

					namedParameterJdbcTemplate.update(macDetSql.toString(), paramMap);

				}
			}

			// End by Punitha
			log.info("Updated mac details !!!");
//			pstmt3 = NamedPreparedStatement.prepareStatement( macDocDelSql.toString());
//			log.info(" ***updateStationingOfMacDetails macDocDelSql SQL *****" + macDocDelSql.toString());
//
//			pstmt3.setString("miscSeqNbr", miscSeqNbr);
//
//			pstmt3.executeUpdate();

			// log.info("Deleted doc Details !!!");

			String[] docType = obj.getDocType();
			String[] docName = obj.getDocName();

			log.info(" ***updateStationingOfMacDetails macDocSql SQL *****" + macDocSql.toString());
			paramMap = new HashMap<String, Object>();
			if (docType != null && docName != null) {
				for (int i = 0; i < docType.length; i++) {
					String fileName = docName[i];
					paramMap.put("miscSeqNbr", miscSeqNbr);
					paramMap.put("docType", docType[i]);
					paramMap.put("uploadFileNm", fileName.substring(fileName.indexOf("_") + 1));
					// paramMap.put(4, (String) assignedFileNameList.get(i));
					// paramMap.put(4, (String)
					// fileName.substring(fileName.lastIndexOf("\\")+1));
					paramMap.put("assignFileNm", fileName.substring(fileName.lastIndexOf("/") + 1));// for Server
					paramMap.put("userId", userId);

					log.info(" *** paramMap: *****" + paramMap.toString());

					namedParameterJdbcTemplate.update(macDocSql.toString(), paramMap);
				}
			}

			// Send Email
			if (status != null && status.equals("S")) {
				String alertCode = ConstantUtil.alertCode;
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = StationingForkliftSubmit_subject;
				String templateEmailFile = TemplateSubmit_body_template;
								
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Stationing of Forklift / Container Lifter / Wheel Loader / Shore Crane");
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * +
				 * "Type of Application: Stationing of Forklift / Container Lifter / Wheel Loader / Shore Crane \n"
				 * + "Reference No.: " + CommonUtility.deNull(refNbr) + " \n" +
				 * "Application Date/Time: " + CommonUtility.deNull(appDttm) + " \n" +
				 * "Company: " + coName + "\n";
				 */
				
				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Stationing of Forklift / Container Lifter / Wheel Loader / Shore Crane, "
						+ "Ref. No.: " + CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm)
						+ " , by " + coName;
				sendSMS(alertCode, sms);
			}

			log.info("END: *** updateStationingOfMacDetails Result *****");
		} catch (BusinessException e) {
			log.info("exception: updateStationingOfMacDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: updateStationingOfMacDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateStationingOfMacDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateStationingOfMacDetails  DAO  END");
		}
		// return assignedFileNameList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getParkingOfLineTowBargeDetails()

	@Override
	public List<Object> getParkingOfLineTowBargeDetails(String userId, String applyType, String appSeqNbr,
			String applyTypeNm) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<Object> result = new ArrayList<Object>();
		try {
			log.info("START: getParkingOfLineTowBargeDetails  DAO  Start userId:" + CommonUtil.deNull(userId)
					+ "applyType:" + CommonUtil.deNull(applyType) + "appSeqNbr:" + CommonUtil.deNull(appSeqNbr)
					+ "applyTypeNm:" + CommonUtil.deNull(applyTypeNm));

			sb.append(" select /* MiscAppEJB - getParkingOfLineTowBargeDetails() */ ");
			sb.append(" misc_seq_nbr, barge_nm, ");
			sb.append(" barge_loa, max_draft_alongside, ");
			sb.append(" tug_boat, contact_nbr, to_char(fr_dttm,'ddmmyyyy') fromDttm, ");
			sb.append(" to_char(fr_dttm,'HH24mi') fromTime, ");
			sb.append(" to_char(to_dttm,'ddmmyyyy') toDttm, to_char(to_dttm,'HH24mi') toTime, ");
			sb.append(" vsl_nm, berth_nbr, ");
			sb.append(" dg_ind, cargo_type, dg_class, ");
			sb.append(" alloc_berth_nbr, alloc_wharf_mark_fr, alloc_wharf_mark_to, ");
			sb.append(" act_wharf_mark_fr, act_wharf_mark_to, ");
			sb.append(" to_char(barge_atb,'ddmmyyyy') bargeAtbDttm, ");
			sb.append(" to_char(barge_atb,'HH24mi') bargeAtbTime, ");
			sb.append(" to_char(barge_atu,'ddmmyyyy') bargeAtuDttm, ");
			sb.append(" to_char(barge_atu,'HH24mi') bargeAtuTime ");
			sb.append(" from misc_barge where misc_seq_nbr =:appSeqNbr ");

			List<Object> temp = getMiscAppDetails(userId, applyType, appSeqNbr, applyTypeNm);
			if (temp != null && temp.size() > 0) {
				result.add(temp.get(0));// MiscAppValueObject
				result.add(temp.get(1));// MiscCustValueObject
			}
			// log.info("Got MiscApp Details in
			// getParkingOfLineTowBargeDetails========> " + temp.size());
			MiscBargeValueObject barge = new MiscBargeValueObject();

			log.info(" ***getParkingOfLineTowBargeDetails SQL *****" + sb.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				barge.setBargeName(CommonUtility.deNull(rs.getString("barge_nm")));
				barge.setBargeLOA(CommonUtility.deNull(rs.getString("barge_loa")));
				barge.setDraft(CommonUtility.deNull(rs.getString("max_draft_alongside")));
				barge.setTugboat(CommonUtility.deNull(rs.getString("tug_boat")));
				barge.setContactNo(CommonUtility.deNull(rs.getString("contact_nbr")));
				barge.setFromDate(CommonUtility.deNull(rs.getString("fromDttm")));
				barge.setFromTime(CommonUtility.deNull(rs.getString("fromTime")));
				barge.setToDate(CommonUtility.deNull(rs.getString("toDttm")));
				barge.setToTime(CommonUtility.deNull(rs.getString("toTime")));
				barge.setMotherShip(CommonUtility.deNull(rs.getString("vsl_nm")));
				barge.setBerthNo(CommonUtility.deNull(rs.getString("berth_nbr")));
				barge.setDg(CommonUtility.deNull(rs.getString("dg_ind")));
				barge.setCargoType(CommonUtility.deNull(rs.getString("cargo_type")));
				barge.setClassName(CommonUtility.deNull(rs.getString("dg_class")));
				barge.setAllocBerthNo(CommonUtility.deNull(rs.getString("alloc_berth_nbr")));
				barge.setWharfMarkFr(CommonUtility.deNull(rs.getString("alloc_wharf_mark_fr")));
				barge.setWharfMarkTo(CommonUtility.deNull(rs.getString("alloc_wharf_mark_to")));
				barge.setActwharfMarkFr(CommonUtility.deNull(rs.getString("act_wharf_mark_fr")));
				barge.setActwharfMarkTo(CommonUtility.deNull(rs.getString("act_wharf_mark_to")));
				barge.setBargeAtbDttm(CommonUtility.deNull(rs.getString("bargeAtbDttm")));
				barge.setBargeAtbTime(CommonUtility.deNull(rs.getString("bargeAtbTime")));
				barge.setBargeAtuDttm(CommonUtility.deNull(rs.getString("bargeAtuDttm")));
				barge.setBargeAtuTime(CommonUtility.deNull(rs.getString("bargeAtuTime")));
			}
			result.add(barge);

			log.info("END: *** getParkingOfLineTowBargeDetails Result *****" + result.toString());
		} catch (BusinessException e) {
			log.info("exception: getParkingOfLineTowBargeDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getParkingOfLineTowBargeDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getParkingOfLineTowBargeDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getParkingOfLineTowBargeDetails  DAO  END");
		}
		log.info("END: *** getParkingOfLineTowBargeDetails Result *****" + result.toString());
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateParkingOfLineTowBargeDetails()

	@Override
	public void updateParkingOfLineTowBargeDetails(String userId, String miscSeqNbr, String status, String coName,
			String appType, String account, String appStatusCd, MiscBargeValueObject obj, String conPerson,
			String conTel) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: updateParkingOfLineTowBargeDetails  DAO  Start userId:" + CommonUtil.deNull(userId)
					+ "miscSeqNbr:" + CommonUtil.deNull(miscSeqNbr) + "status:" + CommonUtil.deNull(status) + "coName:"
					+ CommonUtil.deNull(coName) + "appType:" + CommonUtil.deNull(appType) + "account:"
					+ CommonUtil.deNull(account) + "appStatusCd:" + CommonUtil.deNull(appStatusCd) + "obj:"
					+ obj.toString() + "conPerson:" + CommonUtil.deNull(conPerson) + "conTel:"
					+ CommonUtil.deNull(conTel));

			sb.append(" update /* MiscAppEJB - updateParkingOfLineTowBargeDetails() */ ");
			sb.append(" misc_barge set barge_nm =:bargeName, barge_loa = :bargeLOA, ");
			sb.append(" max_draft_alongside = :draft , tug_boat = :tugboat, ");
			sb.append(" contact_nbr = :contactNo, fr_dttm = to_date(:frDttm,'dd/mm/yyyy HH24mi'), ");
			sb.append(" to_dttm = to_date(:toDttm,'dd/mm/yyyy HH24mi'), vsl_nm = :motherShip , ");
			sb.append(" berth_nbr = :berthNo, dg_ind = :dg, ");
			sb.append(" cargo_type = :cargoType, dg_class = :className, last_modify_user_id ");
			sb.append(" = :userId, last_modify_dttm = sysdate ");
			sb.append(" where misc_seq_nbr = :miscSeqNbr ");

			// updateMiscAppDetails(conn, userId, status, miscSeqNbr, appType, account,
			// appStatusCd);
			// Amended on 14/06/2007 by Punitha. To add Contact Person and Contact tel
			updateMiscAppDetails(userId, status, miscSeqNbr, appType, account, appStatusCd, conPerson, conTel);
			// Ended by Punitha
			// log.info("Updated MiscAppDetails ========> ");

			log.info(" ***updateParkingOfLineTowBargeDetails SQL *****" + sb.toString());

			paramMap.put("bargeName", obj.getBargeName());
			paramMap.put("bargeLOA", obj.getBargeLOA());
			paramMap.put("draft", obj.getDraft());
			paramMap.put("tugboat", obj.getTugboat());
			paramMap.put("contactNo", obj.getContactNo());
			paramMap.put("frDttm", obj.getFromDate() + obj.getFromTime());
			paramMap.put("toDttm", obj.getToDate() + obj.getToTime());
			paramMap.put("motherShip", obj.getMotherShip());
			paramMap.put("berthNo", obj.getBerthNo());
			paramMap.put("dg", obj.getDg());
			paramMap.put("cargoType", obj.getCargoType());
			paramMap.put("className", obj.getClassName());
			paramMap.put("userId", userId);
			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			// Sending Mail
			if (status != null && status.equals("S")) {
				String alertCode = ConstantUtil.alertCodeMAB;
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr); 
				String subject = ParkLineTowBargeSubmit_subject;
				String templateEmailFile = TemplateSubmit_body_template;
								
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Parking of Line-Tow Barge");
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * + "Type of Application: Parking of Line-Tow Barge \n" + "Reference No.: " +
				 * CommonUtility.deNull(refNbr) + " \n" + "Application Date/Time: " +
				 * CommonUtility.deNull(appDttm) + " \n" + "Company: " + coName + "\n";
				 */
				
				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Parking of Line-Tow Barge, " + "Ref. No.: "
						+ CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm) + " , by " + coName;
				sendSMS(alertCode, sms);
			}
			log.info("END: *** updateParkingOfLineTowBargeDetails Result *****");
		} catch (BusinessException e) {
			log.info("exception: updateParkingOfLineTowBargeDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: updateParkingOfLineTowBargeDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateParkingOfLineTowBargeDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateParkingOfLineTowBargeDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getParkingOfForkliftShorecrane()

	@Override
	public List<Object> getParkingOfForkliftShorecrane(String userId, String applyType, String appSeqNbr,
			String applyTypeNm) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		SqlRowSet rs3 = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		List<Object> result = new ArrayList<Object>();

		try {
			log.info("START: getParkingOfForkliftShorecrane  DAO  Start userId:" + CommonUtil.deNull(userId)
					+ "applyType:" + CommonUtil.deNull(applyType) + "appSeqNbr:" + CommonUtil.deNull(appSeqNbr)
					+ "applyTypeNm:" + CommonUtil.deNull(applyTypeNm));

			sb.append(" select /* MiscAppEJB - getParkingOfForkliftShorecrane() */ ");
			sb.append(" misc_seq_nbr, mac_type, ");
			sb.append(" to_char(fr_dttm,'ddmmyyyy') fromDttm, ");
			sb.append(" to_char(fr_dttm,'HH24mi') fromTime, to_char(to_dttm,'ddmmyyyy') toDttm, ");
			sb.append(" to_char(to_dttm,'HH24mi') toTime, remarks from misc_machine ");
			sb.append(" where misc_seq_nbr =:appSeqNbr ");

			// sripriya 13/05/2011 Changed reg_nbr to uppercase
			sb1.append(" select /* MiscAppEJB - getParkingOfForkliftShorecrane() */ ");
			sb1.append(" item_nbr, UPPER(REPLACE(reg_nbr,' ','')) reg_nbr ");
			sb1.append(" from misc_machine_det where misc_seq_nbr = :appSeqNbr ");

			sb2.append(" select /* MiscAppEJB - getParkingOfForkliftShorecrane() */ ");
			sb2.append(" doc_type, a.misc_type_nm typeNm, upload_file_nm, create_user_id, ");
			sb2.append(" to_char(create_dttm,'ddmmyyyy HH24mi') create_dttm, assign_file_nm ");
			sb2.append(" from misc_upload_doc, misc_type_code a where misc_seq_nbr =:appSeqNbr ");
			sb2.append(" and a.cat_cd = 'MISC_MDOC' and  misc_upload_doc.doc_type = a.misc_type_cd ");
			sb2.append(" and a.rec_status = 'A' order by create_dttm ");

			List<Object> temp = (ArrayList<Object>) getMiscAppDetails(userId, applyType, appSeqNbr, applyTypeNm);
			if (temp != null && temp.size() > 0) {
				result.add(temp.get(0));// MiscAppValueObject
				result.add(temp.get(1));// MiscCustValueObject
			}
			// log.info("Got MiscApp Details========> " + temp.size());
			MiscParkMacValueObject parkMac = new MiscParkMacValueObject();

			log.info(" ***getParkingOfForkliftShorecrane SQL *****" + sb.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				parkMac.setMacType(CommonUtility.deNull(rs.getString("mac_type")));
				parkMac.setFromDate(CommonUtility.deNull(rs.getString("fromDttm")));
				parkMac.setFromTime(CommonUtility.deNull(rs.getString("fromTime")));
				parkMac.setToDate(CommonUtility.deNull(rs.getString("toDttm")));
				parkMac.setToTime(CommonUtility.deNull(rs.getString("toTime")));
				parkMac.setRemarks(CommonUtility.deNull(rs.getString("remarks")));
			}

			// log.info("Got Mac Details========> ");

			// log.info("Going to get macDet Details========> ");

			List<String> regNbr = new ArrayList<String>();
			log.info(" ***getParkingOfForkliftShorecrane SQL2 *****" + sb1.toString());
			paramMap = new HashMap<String, Object>();
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs1 = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
			while (rs1.next()) {
				regNbr.add(CommonUtility.deNull(rs1.getString("reg_nbr")));
			}
			parkMac.setRegNbr((String[]) regNbr.toArray(new String[0]));

			// log.info("Going to get doc Details========> ");
			String fullPath = UploadDocument.getOutputFileDir("MACHINE", "upload");
			// log.info("fullPath --> " + fullPath);
			List<String> docType = new ArrayList<String>();
			List<String> docTypeCd = new ArrayList<String>();
			List<String> docName = new ArrayList<String>();
			List<String> uploadDttm = new ArrayList<String>();
			List<String> uploadBy = new ArrayList<String>();
			List<String> assignedFileName = new ArrayList<String>();

			log.info(" ***getParkingOfForkliftShorecrane SQL *****" + sb2.toString());

			paramMap = new HashMap<String, Object>();
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs2 = namedParameterJdbcTemplate.queryForRowSet(sb2.toString(), paramMap);
			while (rs2.next()) {
				docType.add(CommonUtility.deNull(rs2.getString("doc_type")));
				docTypeCd.add(CommonUtility.deNull(rs2.getString("typeNm")));
				docName.add(CommonUtility.deNull(rs2.getString("upload_file_nm")));
				uploadDttm.add(CommonUtility.deNull(rs2.getString("create_dttm")));
				uploadBy.add(getUserName(CommonUtility.deNull(rs2.getString("create_user_id"))));
				assignedFileName.add(fullPath + CommonUtility.deNull(rs2.getString("assign_file_nm")));
			}
			parkMac.setDocType((String[]) docType.toArray(new String[0]));
			parkMac.setDocTypeCd((String[]) docTypeCd.toArray(new String[0]));
			parkMac.setDocName((String[]) docName.toArray(new String[0]));
			parkMac.setUploadDttm((String[]) uploadDttm.toArray(new String[0]));
			parkMac.setUploadBy((String[]) uploadBy.toArray(new String[0]));
			parkMac.setAssignedFileName((String[]) assignedFileName.toArray(new String[0]));
			result.add(parkMac);
			// log.info("<========= End getParkingOfForkliftShorecrane()
			// ========> ");

			// Amended by Punitha on 28/01/2008
			List<MiscParkMacValueObject> macDetList = new ArrayList<MiscParkMacValueObject>();

			log.info(" ***getParkingOfForkliftShorecrane SQL *****" + sb1.toString());
			paramMap = new HashMap<String, Object>();
			paramMap.put("appSeqNbr", appSeqNbr);

			rs3 = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
			while (rs3.next()) {
				parkMac = new MiscParkMacValueObject();
				parkMac.setRegNbrValue(CommonUtility.deNull(rs3.getString("reg_nbr")));
				macDetList.add(parkMac);
			}
			result.add(macDetList);

			log.info("END: *** getParkingOfForkliftShorecrane Result *****" + result.toString());
		} catch (BusinessException e) {
			log.info("exception: getParkingOfForkliftShorecrane ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getParkingOfForkliftShorecrane ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getParkingOfForkliftShorecrane ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getParkingOfForkliftShorecrane  DAO  END");
		}

		log.info("END: getParkingOfForkliftShorecrane  DAO  END Result ***** result: " + result.toString());
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateParkingOfForkliftShorecrane()

	@Override
	public void updateParkingOfForkliftShorecrane(String userId, String miscSeqNbr, String status, String coName,
			String applyType, String account, String appStatusCd, MiscParkMacValueObject obj) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		StringBuilder sb3 = new StringBuilder();
		StringBuilder sb4 = new StringBuilder();

		try {
			log.info("START: updateParkingOfForkliftShorecrane  DAO  Start userId:" + CommonUtil.deNull(userId)
					+ "miscSeqNbr:" + CommonUtil.deNull(miscSeqNbr) + "status:" + CommonUtil.deNull(status) + "coName:"
					+ CommonUtil.deNull(coName) + "applyType:" + CommonUtil.deNull(applyType) + "account:"
					+ CommonUtil.deNull(account) + "appStatusCd:" + CommonUtil.deNull(appStatusCd) + "obj:"
					+ obj.toString());

			sb.append(" update /* MiscAppEJB - updateParkingOfForkliftShorecrane() */ ");
			sb.append(" misc_machine set mac_type = :macType, fr_dttm = ");
			sb.append(" to_date(:frDttm,'dd/mm/yyyy HH24:mi'), ");
			sb.append(" to_dttm = to_date(:toDttm,'dd/mm/yyyy HH24:mi'), remarks ");
			sb.append(" = :remarks , last_modify_user_id = :userId, ");
			sb.append(" last_modify_dttm = sysdate where misc_seq_nbr = :miscSeqNbr ");

			sb1.append(" delete /* MiscAppEJB - updateParkingOfForkliftShorecrane() */ ");
			sb1.append(" from misc_machine_det where misc_seq_nbr = :miscSeqNbr ");

			sb2.append(" insert into misc_machine_det (misc_seq_nbr, item_nbr, reg_nbr, ");
			sb2.append(" last_modify_user_id, last_modify_dttm) values ");
			sb2.append(" (:miscSeqNbr,:itemNbr,:regNbr,:userId,sysdate) ");

			// sb3.append(" delete from misc_upload_doc where misc_seq_nbr =:miscSeqNbr ");

			sb4.append(" insert into misc_upload_doc (misc_seq_nbr, doc_type, upload_file_nm, ");
			sb4.append(" assign_file_nm, create_user_id, create_dttm) values ");
			sb4.append(" (:miscSeqNbr,:docType,:uploadFileNm,:assignFileNm,:userId,sysdate) ");

			updateMiscAppDetails(userId, status, miscSeqNbr, applyType, account, appStatusCd, null, null);
			// log.info("Updated MiscAppDetails ========> ");
			// log.info("From Date ========> " + obj.getFromDate() +
			// obj.getFromTime());
			// log.info("To Date ========> " + obj.getToDate() +
			// obj.getToTime());

			log.info(" ***updateParkingOfForkliftShorecrane SQL *****" + sb.toString());

			paramMap.put("macType", obj.getMacType());
			paramMap.put("frDttm", obj.getFromDate() + obj.getFromTime());
			paramMap.put("toDttm", obj.getToDate() + obj.getToTime());
			paramMap.put("remarks", obj.getRemarks());
			paramMap.put("userId", userId);
			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			paramMap = new HashMap<String, Object>();
			log.info(" ***updateParkingOfForkliftShorecrane SQL2 *****" + sb1.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb1.toString(), paramMap);

			// log.info("Deleted MacDet Details !!!");

			// log.info("Going to process MacDet Details========> ");
			// String[] regNbr = obj.getRegNbr();
			// pstmt = conn.prepareStatement(macDetSql);
			// if(regNbr != null){
			// for(int i=0,j=1; i < regNbr.length; i++,j++){
			// paramMap.put(1, miscSeqNbr);
			// paramMap.put(2, j+"");
			// paramMap.put(3, regNbr[i]);
			// paramMap.put(4, userId);
			//
			// namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			// }
			// }
			// pstmt.close();

			// //Added by Punitha on 28/01/2008
			// List<MiscParkMacValueObject> macDetList = obj.getMacDetList();

			paramMap = new HashMap<String, Object>();
			log.info(" ***updateParkingOfForkliftShorecrane SQL3 *****" + sb2.toString());

//			if (macDetList != null) {
//				for (int i = 0; i < macDetList.size(); i++) {
//					MiscParkMacValueObject vo = (MiscParkMacValueObject) macDetList.get(i);
//
//					paramMap.put("miscSeqNbr", miscSeqNbr);
//					paramMap.put("itemNbr", String.valueOf(i + 1));
//					paramMap.put("regNbr", (String) vo.getRegNbrValue());
//					paramMap.put("userId", userId);
//
//					namedParameterJdbcTemplate.update(sb.toString(), paramMap);
//
//				}
//			}

			String[] regNbr = obj.getRegNbr();
			if (regNbr != null) {
				for (int i = 0, j = 1; i < regNbr.length; i++, j++) {
					paramMap.put("miscSeqNbr", miscSeqNbr);
					paramMap.put("itemNbr", j + "");
					paramMap.put("regNbr", regNbr[i]);
					paramMap.put("userId", userId);

					log.info(" *** paramMap: *****" + paramMap.toString());

					namedParameterJdbcTemplate.update(sb2.toString(), paramMap);
				}
			}
			// End by Punitha

			/*
			 * log.info("Going to process doc Details========> "); String[] docType =
			 * obj.getDocType(); String[] docName = obj.getDocName();
			 * log.info("docType ========> " + docType.length); log.info("docName========> "
			 * + docName.length);
			 * 
			 * if(docName != null){ assignedFileNameList = getAssignedFileNameList(conn,
			 * docName, miscSeqNbr); } ArrayList assignList = (ArrayList)
			 * assignedFileNameList.get(1); log.info("====assignList  " +
			 * assignList.size());
			 */

			// pstmt3 = NamedPreparedStatement.prepareStatement( sb3.toString());
			log.info(" ***updateParkingOfForkliftShorecrane SQL4 *****" + sb3.toString());

			// pstmt3.setString("miscSeqNbr", miscSeqNbr);

			// pstmt3.executeUpdate();

			// log.info("Deleted doc Details !!!");

			String[] docType = obj.getDocType();
			String[] docName = obj.getDocName();

			log.info(" ***updateParkingOfForkliftShorecrane SQL *****" + sb4.toString());
			if (docType != null && docName != null) {
				for (int i = 0; i < docType.length; i++) {
					String fileName = docName[i];
					paramMap.put("miscSeqNbr", miscSeqNbr);
					paramMap.put("docType", docType[i]);
					paramMap.put("uploadFileNm", fileName.substring(fileName.indexOf("_") + 1));
					// paramMap.put(4, (String) assignedFileNameList.get(i));
					// paramMap.put(4, fileName.substring(fileName.lastIndexOf("\\")+1));
					paramMap.put("assignFileNm", fileName.substring(fileName.lastIndexOf("/") + 1));// for Server
					paramMap.put("userId", userId);

					log.info(" *** paramMap: *****" + paramMap.toString());

					namedParameterJdbcTemplate.update(sb4.toString(), paramMap);
				}
			}
			// Send Email
			if (status != null && status.equals("S")) {
				String alertCode = "MAF";
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = OvernightParkForkliftSubmit_subject; 
				String templateEmailFile = TemplateSubmit_body_template;
								
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Overnight Parking of Forklift/Shore Crane");
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * + "Type of Application: Overnight Parking of Forklift/Shore Crane \n" +
				 * "Reference No.: " + CommonUtility.deNull(refNbr) + " \n" +
				 * "Application Date/Time: " + CommonUtility.deNull(appDttm) + " \n" +
				 * "Company: " + coName + "\n";
				 */
				
				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Overnight Parking of Forklift/Shore Crane, "
						+ "Ref. No.: " + CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm)
						+ " , by " + coName;
				sendSMS(alertCode, sms);
			}
			log.info("END: *** updateParkingOfForkliftShorecrane Result *****");
		} catch (BusinessException e) {
			log.info("exception: updateParkingOfForkliftShorecrane ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: updateParkingOfForkliftShorecrane ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateParkingOfForkliftShorecrane ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateParkingOfForkliftShorecrane  DAO  END");
		}
		// return assignedFileNameList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getReeferContainerPowerOutletDetails()

	@Override
	public List<Object> getReeferContainerPowerOutletDetails(String userId, String applyType, String appSeqNbr,
			String applyTypeNm) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<Object> result = new ArrayList<Object>();
		try {
			log.info("START: getReeferContainerPowerOutletDetails  DAO  Start userId:" + CommonUtil.deNull(userId)
					+ "applyType:" + CommonUtil.deNull(applyType) + "appSeqNbr:" + CommonUtil.deNull(appSeqNbr)
					+ "applyTypeNm:" + CommonUtil.deNull(applyTypeNm));

			sb.append(" select /* MiscAppEJB - getReeferContainerPowerOutletDetails() */ ");
			sb.append(" misc_seq_nbr, item_nbr, cntr_nbr, cntr_size, cntr_status, ");
			sb.append(" to_char(plug_in_dttm,'ddmmyyyy') plugInDt, ");
			sb.append(" to_char(plug_in_dttm,'HH24mi') plugInTime, ");
			sb.append(" to_char(plug_out_dttm,'ddmmyyyy') plugOutDt, ");
			sb.append(" to_char(plug_out_dttm,'HH24mi') plugOutTime, ");
			sb.append(" to_char(delivery_dttm,'ddmmyyyy') delivery_dttm, dn_po_nbr, remarks ");
			// " last_modify_user_id, last_modify_dtt " +
			sb.append(" from misc_reefer_det where misc_seq_nbr =:appSeqNbr ");

			List<Object> temp = (ArrayList<Object>) getMiscAppDetails(userId, applyType, appSeqNbr, applyTypeNm);
			if (temp != null && temp.size() > 0) {
				result.add(temp.get(0));// MiscAppValueObject
				result.add(temp.get(1));// MiscCustValueObject
			}
			// log.info("Got MiscApp Details in
			// getReeferContainerPowerOutletDetails========> " + temp.size());
			MiscReeferValueObject reefer = new MiscReeferValueObject();

			log.info(" ***getReeferContainerPowerOutletDetails SQL *****" + sb.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			List<String> cntrNo = new ArrayList<String>();
			List<String> cntrSize = new ArrayList<String>();
			List<String> cntrStatus = new ArrayList<String>();
			List<String> plugInDt = new ArrayList<String>();
			List<String> plugInTime = new ArrayList<String>();
			List<String> plugOutDt = new ArrayList<String>();
			List<String> plugOutTime = new ArrayList<String>();
			List<String> deliveryDttm = new ArrayList<String>();
			List<String> dnPoNbr = new ArrayList<String>();
			List<String> remarks = new ArrayList<String>();

			while (rs.next()) {
				cntrNo.add(CommonUtility.deNull(rs.getString("cntr_nbr")));
				cntrSize.add(CommonUtility.deNull(rs.getString("cntr_size")));
				cntrStatus.add(CommonUtility.deNull(rs.getString("cntr_status")));
				plugInDt.add(CommonUtility.deNull(rs.getString("plugInDt")));
				plugInTime.add(CommonUtility.deNull(rs.getString("plugInTime")));
				plugOutDt.add(CommonUtility.deNull(rs.getString("plugOutDt")));
				plugOutTime.add(CommonUtility.deNull(rs.getString("plugOutTime")));
				deliveryDttm.add(CommonUtility.deNull(rs.getString("delivery_dttm")));
				dnPoNbr.add(CommonUtility.deNull(rs.getString("dn_po_nbr")));
				remarks.add(CommonUtility.deNull(rs.getString("remarks")));
			}
			reefer.setCntrNo((String[]) cntrNo.toArray(new String[0]));
			reefer.setCntrSize((String[]) cntrSize.toArray(new String[0]));
			reefer.setCntrStatus((String[]) cntrStatus.toArray(new String[0]));
			reefer.setPlugInDt((String[]) plugInDt.toArray(new String[0]));
			reefer.setPlugInTime((String[]) plugInTime.toArray(new String[0]));
			reefer.setPlugOutDt((String[]) plugOutDt.toArray(new String[0]));
			reefer.setPlugOutTime((String[]) plugOutTime.toArray(new String[0]));
			reefer.setDeliveryDttm((String[]) deliveryDttm.toArray(new String[0]));
			reefer.setDnPoNbr((String[]) dnPoNbr.toArray(new String[0]));
			reefer.setRemarks((String[]) remarks.toArray(new String[0]));
			result.add(reefer);

			log.info("END: *** getReeferContainerPowerOutletDetails Result *****" + result.toString());
		} catch (BusinessException e) {
			log.info("exception: getReeferContainerPowerOutletDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getReeferContainerPowerOutletDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getReeferContainerPowerOutletDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getReeferContainerPowerOutletDetails  DAO  END");
		}
		log.info("END: getReeferContainerPowerOutletDetails  DAO  END Result: ****** result: " + result.toString());
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateReeferContainerPowerOutletDetails()

	/*
	 * * public void updateReeferContainerPowerOutletDetails(String userId, String
	 * miscSeqNbr, String status, String coName, String appType, String account,
	 * String appStatusCd, MiscReeferValueObject obj,String conPerson,String conTel)
	 * throws BusinessException{
	 */

	// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

	@Override
	public void updateReeferContainerPowerOutletDetails(String userId, String miscSeqNbr, String status, String coName,
			String appType, String account, String appStatusCd, MiscReeferValueObject obj, String conPerson,
			String conTel) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();

		try {
			log.info("START: updateReeferContainerPowerOutletDetails  DAO  Start userId:" + CommonUtil.deNull(userId)
					+ "miscSeqNbr:" + CommonUtil.deNull(miscSeqNbr) + "status:" + CommonUtil.deNull(status) + "coName:"
					+ CommonUtil.deNull(coName) + "appType:" + CommonUtil.deNull(appType) + "account:"
					+ CommonUtil.deNull(account) + "appStatusCd:" + CommonUtil.deNull(appStatusCd) + "obj:"
					+ obj.toString() + "conPerson:" + CommonUtil.deNull(conPerson) + "conTel:"
					+ CommonUtil.deNull(conTel));

			sb.append(" delete /* MiscAppEJB - updateReeferContainerPowerOutletDetails() */ ");
			sb.append(" from misc_reefer_det where misc_seq_nbr =:miscSeqNbr ");

			sb1.append(" insert into misc_reefer_det (misc_seq_nbr, item_nbr, cntr_nbr, ");
			sb1.append(" cntr_size, cntr_status, plug_in_dttm, plug_out_dttm, ");
			sb1.append(" delivery_dttm, dn_po_nbr, remarks, ");
			sb1.append(" last_modify_user_id, last_modify_dttm) values ( ");
			sb1.append(" :miscSeqNbr, :itemNbr, :cntrNo, :cntrSize, ");
			sb1.append(" :cntrStatus, to_date(:plugIn,'dd/mm/yyyy HH24:mi'), ");
			sb1.append(" to_date(:plugOut,'dd/mm/yyyy HH24:mi'), to_date(:deliveryDttm,'dd/mm/yyyy'), ");
			sb1.append(" :dnPoNbr, :remarks, :userId,sysdate) ");

			// updateMiscAppDetails(conn, userId, status, miscSeqNbr, appType, account,
			// appStatusCd);

			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
			updateMiscAppDetails(userId, status, miscSeqNbr, appType, account, appStatusCd, conPerson, conTel);

			// log.info("Updated MiscAppDetails ========> ");

			log.info(" ***updateReeferContainerPowerOutletDetails SQL2 *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			// log.info("Deleted!!!");

			// log.info("Going to process Reefer Details========> ");
			String[] cntrNo = obj.getCntrNo();
			String[] cntrSize = obj.getCntrSize();
			String[] cntrStatus = obj.getCntrStatus();
			String[] plugInDt = obj.getPlugInDt();
			String[] plugInTime = obj.getPlugInTime();
			String[] plugOutDt = obj.getPlugOutDt();
			String[] plugOutTime = obj.getPlugOutTime();
			String[] deliveryDttm = obj.getDeliveryDttm();
			String[] dnPoNbr = obj.getDnPoNbr();
			String[] remarks = obj.getRemarks();

			paramMap = new HashMap<String, Object>();
			log.info(" ***updateReeferContainerPowerOutletDetails SQL3 *****" + sb1.toString());
			for (int i = 0, j = 1; i < cntrNo.length; i++, j++) {
				if ((cntrNo[i] != null && !cntrNo[i].equals("")) || (cntrSize[i] != null && !cntrSize[i].equals(""))) {
					paramMap.put("miscSeqNbr", miscSeqNbr);
					paramMap.put("itemNbr", j);
					paramMap.put("cntrNo", cntrNo[i]);
					paramMap.put("cntrSize", cntrSize[i]);
					paramMap.put("cntrStatus", cntrStatus[i]);
					if (plugInDt != null && plugInDt.length > i) {
						paramMap.put("plugIn", plugInDt[i] + plugInTime[i]);
						paramMap.put("plugOut", plugOutDt[i] + plugOutTime[i]);
						if (deliveryDttm != null && deliveryDttm.length > i)
							paramMap.put("deliveryDttm", deliveryDttm[i]);
						else
							paramMap.put("deliveryDttm", null);
						if (dnPoNbr != null && dnPoNbr.length > i)
							paramMap.put("dnPoNbr", dnPoNbr[i]);
						else
							paramMap.put("dnPoNbr", null);
						if (remarks != null && remarks.length > i)
							paramMap.put("remarks", remarks[i]);
						else
							paramMap.put("remarks", null);
					} else {
						paramMap.put("plugIn", null);
						paramMap.put("plugOut", null);
						paramMap.put("deliveryDttm", null);
						paramMap.put("dnPoNbr", null);
						paramMap.put("remarks", null);
					}
					paramMap.put("userId", userId);

					log.info(" *** paramMap: *****" + paramMap.toString());

					namedParameterJdbcTemplate.update(sb1.toString(), paramMap);
				}
			}
			// Send Email
			if (status != null && status.equals("S")) {
				String alertCode = ConstantUtil.alertCode;
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = ReeferCntrPowerOutletSubmit_subject;
				String templateEmailFile = TemplateSubmit_body_template;
								
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Reefer Containers Power Outlet");
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * + "Type of Application: Reefer Containers Power Outlet \n" +
				 * "Reference No.: " + CommonUtility.deNull(refNbr) + " \n" +
				 * "Application Date/Time: " + CommonUtility.deNull(appDttm) + " \n" +
				 * "Company: " + coName + "\n";
				 */

				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Reefer Containers Power Outlet, " + "Ref. No.: "
						+ CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm) + " , by " + coName;
				sendSMS(alertCode, sms);
			}
			log.info("END: *** updateReeferContainerPowerOutletDetails Result *****");
		} catch (BusinessException e) {
			log.info("exception: updateReeferContainerPowerOutletDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: updateReeferContainerPowerOutletDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateReeferContainerPowerOutletDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateReeferContainerPowerOutletDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getUseOfSpaceDetails()

	@Override
	public List<Object> getUseOfSpaceDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
			throws BusinessException {
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		List<Object> result = new ArrayList<Object>();
		try {
			log.info("START: getUseOfSpaceDetails  DAO  Start userId:" + CommonUtil.deNull(userId) + "applyType:"
					+ CommonUtil.deNull(applyType) + "appSeqNbr:" + CommonUtil.deNull(appSeqNbr) + "applyTypeNm:"
					+ CommonUtil.deNull(applyTypeNm));

			sb.append(" select /* MiscAppEJB - getUseOfSpaceDetails() */ ");
			sb.append(" misc_seq_nbr, space_type, ");
			sb.append(" space_purpose, a.misc_type_nm purpose,  ");
			sb.append(" to_char(fr_dttm,'ddmmyyyy') fromDttm,  ");
			sb.append(" to_char(fr_dttm,'HH24mi') fromTime, to_char(to_dttm,'ddmmyyyy') toDttm,  ");
			sb.append(" to_char(to_dttm,'HH24mi') toTime,  ");
			sb.append(" space_reason, bl_do_nbr, orig_mark_nbr, orig_nbr_pkg, orig_cargo_desc, orig_ton_measure,  ");
			sb.append(" new_mark_nbr, new_nbr_pkg, new_cargo_desc, new_ton_measure  ");
			sb.append(" from misc_space, misc_type_code a  ");
			sb.append(" where misc_seq_nbr =:appSeqNbr and a.cat_cd = 'MISC_PURP'  ");
			sb.append(" and misc_space.space_purpose = a.misc_type_cd ");

			sb1.append(" select /* MiscAppEJB - getUseOfSpaceDetails() */ ");
			sb1.append(" item_nbr, bay_nbr, area_use,  ");
			sb1.append(" to_char(ops_start_dttm,'ddmmyyyy HH24mi') ops_start_dttm,  ");
			sb1.append(" to_char(ops_end_dttm,'ddmmyyyy HH24mi') ops_end_dttm  ");
			sb1.append(" from misc_space_det where misc_seq_nbr =:appSeqNbr ");

			log.info("Got MiscApp Details in getUseOfSpaceDetails========> spaceSql=" + sb.toString());
			log.info("Got MiscApp Details in getUseOfSpaceDetails========> spaceDetSql=" + sb1.toString());
			log.info("Got MiscApp Details in getUseOfSpaceDetails========> appSeqNbr=" + appSeqNbr);

			log.info("Got MiscApp Details in getUseOfSpaceDetails========> applyTypeNm=" + applyTypeNm);
			log.info("Got MiscApp Details in getUseOfSpaceDetails========> applyType=" + applyType);
			log.info("Got MiscApp Details in getUseOfSpaceDetails========> userId=" + userId);

			List<Object> temp = (ArrayList<Object>) getMiscAppDetails(userId, applyType, appSeqNbr, applyTypeNm);
			if (temp != null && temp.size() > 0) {
				result.add(temp.get(0));// MiscAppValueObject
				result.add(temp.get(1));// MiscCustValueObject
			}
			MiscSpaceValueObject space = new MiscSpaceValueObject();

			log.info(" ***getUseOfSpaceDetails SQL *****" + sb.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				space.setSpaceType(CommonUtility.deNull(rs.getString("space_type")));
				space.setPurpose(CommonUtility.deNull(rs.getString("purpose")));
				space.setFromDate(CommonUtility.deNull(rs.getString("fromDttm")));
				space.setFromTime(CommonUtility.deNull(rs.getString("fromTime")));
				space.setToDate(CommonUtility.deNull(rs.getString("toDttm")));
				space.setToTime(CommonUtility.deNull(rs.getString("toTime")));
				space.setReason(CommonUtility.deNull(rs.getString("space_reason")));
				space.setBillNbr(CommonUtility.deNull(rs.getString("bl_do_nbr")));
				space.setMarks(CommonUtility.deNull(rs.getString("orig_mark_nbr")));
				space.setPackages(CommonUtility.deNull(rs.getString("orig_nbr_pkg")));
				space.setCargoDesc(CommonUtility.deNull(rs.getString("orig_cargo_desc")));
				space.setTonnage(CommonUtility.deNull(rs.getString("orig_ton_measure")));
				space.setNewMarks(CommonUtility.deNull(rs.getString("new_mark_nbr")));
				space.setNewPackages(CommonUtility.deNull(rs.getString("new_nbr_pkg")));
				space.setNewCargoDesc(CommonUtility.deNull(rs.getString("new_cargo_desc")));
				space.setNewTonnage(CommonUtility.deNull(rs.getString("new_ton_measure")));
			}

			log.info(" ***getUseOfSpaceDetails SQL2 *****" + sb1.toString());

			paramMap = new HashMap<String, Object>();
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs1 = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
			List<String> bayNbr = new ArrayList<String>();
			List<String> areaUsed = new ArrayList<String>();
			List<String> opsStartDttm = new ArrayList<String>();
			List<String> opsEndDttm = new ArrayList<String>();

			while (rs1.next()) {
				bayNbr.add(CommonUtility.deNull(rs1.getString("bay_nbr")));
				areaUsed.add(CommonUtility.deNull(rs1.getString("area_use")));
				opsStartDttm.add(CommonUtility.deNull(rs1.getString("ops_start_dttm")));
				opsEndDttm.add(CommonUtility.deNull(rs1.getString("ops_end_dttm")));
			}
			space.setAreaUsed((String[]) areaUsed.toArray(new String[0]));
			space.setBayNbr((String[]) bayNbr.toArray(new String[0]));
			space.setOpsStartDttm((String[]) opsStartDttm.toArray(new String[0]));
			space.setOpsEndDttm((String[]) opsEndDttm.toArray(new String[0]));

			result.add(space);

			log.info("END: *** getUseOfSpaceDetails Result *****" + result.toString());
		} catch (BusinessException e) {
			log.info("exception: getUseOfSpaceDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getUseOfSpaceDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getUseOfSpaceDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getUseOfSpaceDetails  DAO  END");
		}
		log.info("END: getUseOfSpaceDetails  DAO  END Result ***** result: " + result.toString());
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateUseOfSpaceDetails()
	/*
	 * public void updateUseOfSpaceDetails(String userId, String miscSeqNbr, String
	 * status, String coName, String appType, String account, String appStatusCd,
	 * MiscSpaceValueObject obj) throws BusinessException{
	 */

	// Amended on 08/06/2007 by Punitha. To add Contact Person and Contact Tel

	@Override
	public void updateUseOfSpaceDetails(String userId, String miscSeqNbr, String status, String coName, String appType,
			String account, String appStatusCd, MiscSpaceValueObject obj, String conPerson, String conTel)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		try {
			log.info("START: updateUseOfSpaceDetails  DAO  Start userId:" + CommonUtil.deNull(userId) + "miscSeqNbr:"
					+ CommonUtil.deNull(miscSeqNbr) + "status:" + CommonUtil.deNull(status) + "coName:"
					+ CommonUtil.deNull(coName) + "appType:" + CommonUtil.deNull(appType) + "account:"
					+ CommonUtil.deNull(account) + "appStatusCd:" + CommonUtil.deNull(appStatusCd) + "obj:"
					+ obj.toString() + "conPerson:" + CommonUtil.deNull(conPerson) + "conTel:"
					+ CommonUtil.deNull(conTel));
			sb.append(" update /* MiscAppEJB - updateUseOfSpaceDetails() */ ");
			sb.append(" misc_space set space_type = :spaceType, ");
			sb.append(" space_purpose =:purpose, ");
			sb.append(" fr_dttm = to_date(:frDate,'dd/mm/yyyy HH24mi'), ");
			sb.append(" to_dttm = to_date(:toDate,'dd/mm/yyyy HH24mi'), ");
			sb.append(" space_reason =:reason, bl_do_nbr =:billNbr, ");
			sb.append(" orig_mark_nbr =:marks, orig_nbr_pkg =:packages, ");
			sb.append(" orig_cargo_desc = :cargoDesc, ");
			sb.append(" orig_ton_measure =:tonnage, new_mark_nbr =:newMarks, ");
			sb.append(" new_nbr_pkg =:newPackages, new_cargo_desc =:newCargoDesc, ");
			sb.append(" new_ton_measure =:newTonnage, last_modify_user_id = :userId, ");
			sb.append(" last_modify_dttm = sysdate ");
			sb.append(" where misc_seq_nbr = :miscSeqNbr ");

			// updateMiscAppDetails(conn, userId, status, miscSeqNbr, appType, account,
			// appStatusCd,null,null);
			// Amended on 08/06/2007 by Punitha. To add Contact Person and Contact Tel
			updateMiscAppDetails(userId, status, miscSeqNbr, appType, account, appStatusCd, conPerson, conTel);
			// Amended on 08/06/2007 by Punitha. To add Contact Person and Contact Tel
			// log.info("Updated MiscAppDetails ========> ");

			log.info(" ***updateUseOfSpaceDetails SQL *****" + sb.toString());

			paramMap.put("spaceType", obj.getSpaceType());
			paramMap.put("purpose", obj.getPurpose());
			paramMap.put("frDate", obj.getFromDate() + obj.getFromTime());
			paramMap.put("toDate", obj.getToDate() + obj.getToTime());
			paramMap.put("reason", obj.getReason());
			paramMap.put("billNbr", obj.getBillNbr());
			paramMap.put("marks", obj.getMarks());
			paramMap.put("packages", obj.getPackages());
			paramMap.put("cargoDesc", obj.getCargoDesc());
			paramMap.put("tonnage", obj.getTonnage());
			paramMap.put("newMarks", obj.getNewMarks());
			paramMap.put("newPackages", obj.getNewPackages());
			paramMap.put("newCargoDesc", obj.getNewCargoDesc());
			paramMap.put("newTonnage", obj.getNewTonnage());
			paramMap.put("userId", userId);
			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			// Added on 05/01/2009 by Punitha.
			sb1.append(" update misc_space_det set ops_start_dttm = ");
			sb1.append(" to_date(:frTime,'dd/mm/yyyy HH24:mi'), ");
			sb1.append(" ops_end_dttm = to_date(:toTime,'dd/mm/yyyy HH24:mi'), ");
			sb1.append(" last_modify_user_id = :userId, ");
			sb1.append(" last_modify_dttm = sysdate where misc_seq_nbr =:miscSeqNbr ");

			log.info(" ***updateUseOfSpaceDetails SQL2 *****" + sb1.toString());

			paramMap = new HashMap<String, Object>();
			paramMap.put("frTime", obj.getFromDate() + obj.getFromTime());
			paramMap.put("toTime", obj.getToDate() + obj.getToTime());
			paramMap.put("userId", userId);
			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb1.toString(), paramMap);

			// End
			// Send Email
			if (status != null && status.equals("S")) {
				String alertCode = ConstantUtil.alertCode;
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = SpaceAppSubmit_subject;
				String templateEmailFile = TemplateSubmit_body_template;
								
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Use Of Space");
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * + "Type of Application: Use Of Space \n" + "Reference No.: " +
				 * CommonUtility.deNull(refNbr) + " \n" + "Application Date/Time: " +
				 * CommonUtility.deNull(appDttm) + " \n" + "Company: " + coName + "\n";
				 */
				
				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Use Of Space, " + "Ref. No.: "
						+ CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm) + " , by " + coName;
				sendSMS(alertCode, sms);
			}

			log.info("END: *** updateUseOfSpaceDetails Result *****");
		} catch (BusinessException e) {
			log.info("exception: updateUseOfSpaceDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: updateUseOfSpaceDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateUseOfSpaceDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateUseOfSpaceDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getSpreaderDetails()

	@Override
	public List<Object> getSpreaderDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
			throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<Object> result = new ArrayList<Object>();
		try {
			log.info("START: getSpreaderDetails  DAO  Start userId:" + CommonUtil.deNull(userId) + "applyType:"
					+ CommonUtil.deNull(applyType) + "appSeqNbr:" + CommonUtil.deNull(appSeqNbr) + "applyTypeNm:"
					+ CommonUtil.deNull(applyTypeNm));

			sb.append(" select /* MiscAppEJB - getSpreaderDetails() */ ");
			sb.append(" misc_seq_nbr, spreader_type, ");
			sb.append(" to_char(fr_dttm,'ddmmyyyy') fromDttm, ");
			sb.append(" to_char(fr_dttm,'HH24mi') fromTime, to_char(to_dttm,'ddmmyyyy') toDttm, ");
			sb.append(" to_char(to_dttm,'HH24mi') toTime, remarks, ");
			sb.append(" to_char(issue_dttm,'ddmmyyyy') issueDt, ");
			sb.append(" to_char(issue_dttm,'HH24mi') issueTime, issue_by_staff, receive_by_cust, ");
			sb.append(" to_char(return_dttm,'ddmmyyyy') receiveDt, ");
			sb.append(" to_char(return_dttm,'HH24mi') receiveTime, ");
			sb.append(" receive_by_staff, return_by_cust ");
			sb.append(" from misc_spreader where misc_seq_nbr = :appSeqNbr ");

			List<Object> temp = (ArrayList<Object>) getMiscAppDetails(userId, applyType, appSeqNbr, applyTypeNm);
			if (temp != null && temp.size() > 0) {
				result.add(temp.get(0));// MiscAppValueObject
				result.add(temp.get(1));// MiscCustValueObject
			}
			MiscSpreaderValueObject spreader = new MiscSpreaderValueObject();

			log.info(" ***getSpreaderDetails SQL *****" + sb.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				spreader.setSpreaderType(CommonUtility.deNull(rs.getString("spreader_type")));
				spreader.setFromDate(CommonUtility.deNull(rs.getString("fromDttm")));
				spreader.setFromTime(CommonUtility.deNull(rs.getString("fromTime")));
				spreader.setToDate(CommonUtility.deNull(rs.getString("toDttm")));
				spreader.setToTime(CommonUtility.deNull(rs.getString("toTime")));
				spreader.setRemarks(CommonUtility.deNull(rs.getString("remarks")));
				spreader.setIssueDt(CommonUtility.deNull(rs.getString("issueDt")));
				spreader.setIssueTime(CommonUtility.deNull(rs.getString("issueTime")));
				spreader.setIssueByStaff(CommonUtility.deNull(rs.getString("issue_by_staff")));
				spreader.setReceiveByCust(CommonUtility.deNull(rs.getString("receive_by_cust")));
				spreader.setReceiveDt(CommonUtility.deNull(rs.getString("receiveDt")));
				spreader.setReceiveTime(CommonUtility.deNull(rs.getString("receiveTime")));
				spreader.setReceiveByStaff(CommonUtility.deNull(rs.getString("receive_by_staff")));
				spreader.setReturnByCust(CommonUtility.deNull(rs.getString("return_by_cust")));
			}
			result.add(spreader);

			log.info("END: *** getSpreaderDetails Result *****" + result.toString());
		} catch (BusinessException e) {
			log.info("exception: getSpreaderDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getSpreaderDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getSpreaderDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSpreaderDetails  DAO  END");
		}
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateSpreaderDetails()

	@Override
	public boolean updateSpreaderDetails(String userId, String miscSeqNbr, String status, String coName, String appType,
			String account, String appStatusCd, MiscSpreaderValueObject obj) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		try {
			log.info("START: updateSpreaderDetails  DAO  Start userId:" + CommonUtil.deNull(userId) + " miscSeqNbr:"
					+ CommonUtil.deNull(miscSeqNbr) + " status:" + CommonUtil.deNull(status) + " coName:"
					+ CommonUtil.deNull(coName) + " appType:" + CommonUtil.deNull(appType) + " account:"
					+ CommonUtil.deNull(account) + " appStatusCd:" + CommonUtil.deNull(appStatusCd) + " obj:"
					+ obj.toString());

			sb.append(" update /* MiscAppEJB - updateSpreaderDetails() */ misc_spreader ");
			sb.append(" set spreader_type =:spreaderType, ");
			sb.append(" fr_dttm = to_date(:frDate,'dd/mm/yyyy HH24mi'), ");
			sb.append(" to_dttm = to_date(:toDate,'dd/mm/yyyy HH24mi'), ");
			sb.append(" remarks =:remarks, last_modify_user_id ");
			sb.append(" = :userId, last_modify_dttm = sysdate ");
			sb.append(" where misc_seq_nbr = :miscSeqNbr ");

			sb1.append(" SELECT VALUE - ( SELECT COUNT(S.MISC_SEQ_NBR) ");
			sb1.append(" FROM MISC_APP A, MISC_SPREADER S ");
			sb1.append(" WHERE A.APP_TYPE='WSS' AND A.APP_STATUS IN ");
			sb1.append(" ('S','A','B','C','P','U') AND A.MISC_SEQ_NBR=S.MISC_SEQ_NBR ");
			sb1.append(" AND S.SPREADER_TYPE=:spreaderType ");
			sb1.append(" AND ((FR_DTTM <= TO_DATE(:fromDate,'DDMMYYYY HH24MI') ");
			sb1.append(" AND TO_DTTM >= TO_DATE(:toDate, 'DDMMYYYY HH24MI'))  ");
			sb1.append(" OR (TO_DTTM > TO_DATE(:fromDate,'DDMMYYYY HH24MI') ");
			sb1.append(" AND TO_DTTM <= TO_DATE(:toDate,'DDMMYYYY HH24MI'))  ");
			sb1.append(" OR (FR_DTTM >= TO_DATE(:fromDate,'DDMMYYYY HH24MI') ");
			sb1.append(" AND FR_DTTM < TO_DATE(:toDate ,'DDMMYYYY HH24MI')))  ");
			sb1.append(" ) APP FROM USER_PARA WHERE PARA_CD='WSS_TOT_' ||  :spreaderType ");

			paramMap = new HashMap<String, Object>();
			// These code to check remainder of the spreader
			log.info(" ***updateSpreaderDetails SQL *****" + sb1.toString());

			paramMap.put("spreaderType", obj.getSpreaderType());
			paramMap.put("fromDate", obj.getFromDate());
			paramMap.put("toDate", obj.getToDate());

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
			int row = 0;
			while (rs.next()) {
				row = rs.getInt("APP");
				if (row <= 0) {
					log.info("END: *** updateSpreaderDetails Result ***** result: true");
					return false;
				}
			}

			updateMiscAppDetails(userId, status, miscSeqNbr, appType, account, appStatusCd, null, null);
			// log.info("Updated MiscAppDetails ========> ");

			log.info(" ***updateSpreaderDetails SQL2 *****" + sb.toString());

			paramMap.put("spreaderType", obj.getSpreaderType());
			paramMap.put("frDate", obj.getFromDate());
			paramMap.put("toDate", obj.getToDate());
			paramMap.put("remarks", obj.getRemarks());
			paramMap.put("userId", userId);
			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			// Sending Mail
			if (status != null && status.equals("S")) {
				String alertCode = "MAF";
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = HireWoodenSubmit_subject; 
				String templateEmailFile = TemplateSubmit_body_template;
				
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
				
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Hire of Wooden / Steel Spreader");
				
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * + "Type of Application: Hire of Wooden / Steel Spreader \n" +
				 * "Reference No.: " + CommonUtility.deNull(refNbr) + " \n" +
				 * "Application Date/Time: " + CommonUtility.deNull(appDttm) + " \n" +
				 * "Company: " + coName + "\n";
				 */
				
				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Hire of Wooden / Steel Spreader , " + "Ref. No.: "
						+ CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm) + " , by " + coName;
				sendSMS(alertCode, sms);
			}
			log.info("END: *** updateSpreaderDetails Result ***** result: true");
			return true;
		} catch (BusinessException e) {
			log.info("exception: updateSpreaderDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: updateSpreaderDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateSpreaderDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateSpreaderDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getHotworkDetails()

	@Override
	public List<Object> getHotworkDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
			throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs1 = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		List<Object> result = new ArrayList<Object>();
		try {
			log.info("START: getHotworkDetails  DAO  Start userId:" + CommonUtil.deNull(userId) + "applyType:"
					+ CommonUtil.deNull(applyType) + "appSeqNbr:" + CommonUtil.deNull(appSeqNbr) + "applyTypeNm:"
					+ CommonUtil.deNull(applyTypeNm));

			sb.append(" select /* MiscAppEJB - getHotworkDetails() */ ");
			sb.append(" misc_seq_nbr, hw_loc, hw_desc, ");
			sb.append("  to_char(fr_dttm,'ddmmyyyy') fromDttm, ");
			sb.append("  to_char(fr_dttm,'HH24mi') fromTime, to_char(to_dttm,'ddmmyyyy') toDttm, ");
			sb.append("  to_char(to_dttm,'HH24mi') toTime, tot_standby_hr, ");
			// Added on 30/05/2007 by Punitha. To add checkbox for InspectInd
			sb.append("  no_fireman_ind ");
			// Added on 30/05/2007 by Punitha. To add checkbox for InspectInd
			sb.append("  from misc_hotwork where misc_seq_nbr =:appSeqNbr ");

			sb1.append(" select /* MiscAppEJB - getHotworkDetails() */ ");
			sb1.append(" to_char(standby_fr_dttm,'ddmmyyyy') standby_fr_dttm, ");
			sb1.append(" to_char(standby_fr_dttm,'HH24mi') standby_fr_time, ");
			sb1.append(" to_char(standby_to_dttm,'HH24mi') standby_to_time, charge_time, fireman_nm ");
			sb1.append(" from misc_hotwork_det where  misc_seq_nbr =:appSeqNbr ");

			List<Object> temp = (ArrayList<Object>) getMiscAppDetails(userId, applyType, appSeqNbr, applyTypeNm);
			if (temp != null && temp.size() > 0) {
				result.add(temp.get(0));// MiscAppValueObject
				result.add(temp.get(1));// MiscCustValueObject
			}
			// log.info("Got MiscApp Details in
			// getHotworkDetails========> " + temp.size());
			MiscHotworkValueObject hotwork = new MiscHotworkValueObject();

			log.info(" ***getHotworkDetails SQL *****" + sb.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				hotwork.setLocation(CommonUtility.deNull(rs.getString("hw_loc")));
				hotwork.setDescription(CommonUtility.deNull(rs.getString("hw_desc")));
				hotwork.setFromDate(CommonUtility.deNull(rs.getString("fromDttm")));
				hotwork.setFromTime(CommonUtility.deNull(rs.getString("fromTime")));
				hotwork.setToDate(CommonUtility.deNull(rs.getString("toDttm")));
				hotwork.setToTime(CommonUtility.deNull(rs.getString("toTime")));
				hotwork.setTotStandbyHr(CommonUtility.deNull(rs.getString("tot_standby_hr")));
				// Added on 30/05/2007 by Punitha. To add checkbox for InspectInd
				hotwork.setInspectInd(CommonUtility.deNull(rs.getString("no_fireman_ind")));
				// Added on 30/05/2007 by Punitha. To add checkbox for InspectInd
			}

			paramMap = new HashMap<String, Object>();

			log.info(" ***getHotworkDetails SQL2 *****" + sb1.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs1 = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
			List<String> standbyFrDttm = new ArrayList<String>();
			List<String> standbyFrTime = new ArrayList<String>();
			List<String> standbyToTime = new ArrayList<String>();
			List<String> chargeTime = new ArrayList<String>();
			List<String> fireManNm = new ArrayList<String>();
			while (rs1.next()) {
				standbyFrDttm.add(CommonUtility.deNull(rs1.getString("standby_fr_dttm")));
				standbyFrTime.add(CommonUtility.deNull(rs1.getString("standby_fr_time")));
				standbyToTime.add(CommonUtility.deNull(rs1.getString("standby_to_time")));
				// chargeTime.add(CommonUtility.deNull(rs.getString("charge_time")));
				if (rs1.getString("charge_time") != null)
					chargeTime.add(rs1.getDouble("charge_time") + "");
				else
					chargeTime.add("");
				fireManNm.add(CommonUtility.deNull(rs1.getString("fireman_nm")));
			}
			// log.info("chargeTime in getHotworkDetails() ========> " +
			// chargeTime);
			hotwork.setStandbyFrDttm((String[]) standbyFrDttm.toArray(new String[0]));
			hotwork.setStandbyFrTime((String[]) standbyFrTime.toArray(new String[0]));
			hotwork.setStandbyToTime((String[]) standbyToTime.toArray(new String[0]));
			hotwork.setChargeTime((String[]) chargeTime.toArray(new String[0]));
			hotwork.setFireManNm((String[]) fireManNm.toArray(new String[0]));
			result.add(hotwork);
			log.info("END: *** getHotworkDetails Result *****" + result.toString());
		} catch (BusinessException e) {
			log.info("exception: getHotworkDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getHotworkDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getHotworkDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getHotworkDetails  DAO  END");
		}
		log.info("END: getHotworkDetails  DAO  END Result ***** result: " + result.toString());
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateHotworkDetails()

	@Override
	public void updateHotworkDetails(String userId, String miscSeqNbr, String status, String coName, String appType,
			String account, String appStatusCd, MiscHotworkValueObject obj) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: updateHotworkDetails  DAO  Start userId:" + CommonUtil.deNull(userId) + "miscSeqNbr:"
					+ CommonUtil.deNull(miscSeqNbr) + "status:" + CommonUtil.deNull(status) + "coName:"
					+ CommonUtil.deNull(coName) + "appType:" + CommonUtil.deNull(appType) + "account:"
					+ CommonUtil.deNull(account) + "appStatusCd:" + CommonUtil.deNull(appStatusCd) + "obj:"
					+ obj.toString());

			sb.append(" update /* MiscAppEJB - updateHotworkDetails() */ misc_hotwork ");
			sb.append(" set hw_loc =:location, hw_desc =:description, ");
			sb.append(" fr_dttm = to_date(:fromDate,'dd/mm/yyyy HH24mi'), ");
			sb.append(" to_dttm = to_date(:toDate,'dd/mm/yyyy HH24mi'), ");
			sb.append(" last_modify_user_id =:userId, last_modify_dttm = sysdate ");
			sb.append(" where misc_seq_nbr =:miscSeqNbr ");

			updateMiscAppDetails(userId, status, miscSeqNbr, appType, account, appStatusCd, null, null);
			// log.info("Updated MiscAppDetails ========> ");

			log.info(" ***updateHotworkDetails SQL *****" + sb.toString());

			paramMap.put("location", obj.getLocation());
			paramMap.put("description", obj.getDescription());
			paramMap.put("fromDate", obj.getFromDate() + obj.getFromTime());
			paramMap.put("toDate", obj.getToDate() + obj.getToTime());
			paramMap.put("userId", userId);
			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			// Sending Mail
			if (status != null && status.equals("S")) {
				String alertCode = "MAF";
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr); 
				String subject = HotWorkPermitSubmit_subject;
				String templateEmailFile = TemplateSubmit_body_template;
				
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Hot Work Permit");
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * + "Type of Application: Hot Work Permit \n" + "Reference No.: " +
				 * CommonUtility.deNull(refNbr) + " \n" + "Application Date/Time: " +
				 * CommonUtility.deNull(appDttm) + " \n" + "Company: " + coName + "\n";
				 */
				
				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Hot Work Permit , " + "Ref. No.: "
						+ CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm) + " , by " + coName;
				sendSMS(alertCode, sms);
			}
			log.info("END: *** updateHotworkDetails Result *****");
		} catch (BusinessException e) {
			log.info("exception: updateHotworkDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: updateHotworkDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateHotworkDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateHotworkDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getContractorPermitDetails()

	@Override
	public List<Object> getContractorPermitDetails(String userId, String applyType, String appSeqNbr,
			String applyTypeNm) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs1 = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		List<Object> result = new ArrayList<Object>();

		try {
			log.info("START: getContractorPermitDetails  DAO  Start userId:" + CommonUtil.deNull(userId) + "applyType:"
					+ CommonUtil.deNull(applyType) + "appSeqNbr:" + CommonUtil.deNull(appSeqNbr) + "applyTypeNm:"
					+ CommonUtil.deNull(applyTypeNm));

			sb.append(" select /* MiscAppEJB - getContractorPermitDetails() */ ");
			sb.append(" misc_seq_nbr, exact_loc, ");
			sb.append(" exact_desc, a.misc_type_nm descr, other_desc,  ");
			sb.append(" to_char(fr_dttm,'ddmmyyyy') fromDttm,  ");
			sb.append(" to_char(fr_dttm,'HH24mi') fromTime, to_char(to_dttm,'ddmmyyyy') toDttm,  ");
			sb.append(" to_char(to_dttm,'HH24mi') toTime, type, psa_license_nbr, remarks, waive_ind,  ");
			sb.append(" contract_co_nm, contract_addr, contact_nm, contact_nric, designation  ");
			// Added by Punitha on 06/05/2008
			sb.append(" , waive_reason  ");
			sb.append(" from misc_contractor, misc_type_code a   ");
			sb.append(" where misc_seq_nbr =:appSeqNbr and a.cat_cd = 'MISC_CONT'  ");
			sb.append(" and misc_contractor.exact_desc = a.misc_type_cd ");

			sb1.append(" select /* MiscAppEJB - getContractorPermitDetails() */ doc_type,  ");
			sb1.append(" a.misc_type_nm typeNm, upload_file_nm, create_user_id,  ");
			sb1.append(" to_char(create_dttm,'ddmmyyyy HH24mi') create_dttm, assign_file_nm  ");
			sb1.append(" from misc_upload_doc, misc_type_code a where misc_seq_nbr =:appSeqNbr  ");
			sb1.append(" and a.cat_cd = 'MISC_CDOC' and  misc_upload_doc.doc_type = a.misc_type_cd  ");
			sb1.append(" and a.rec_status = 'A' order by create_dttm ");

			List<Object> temp = (ArrayList<Object>) getMiscAppDetails(userId, applyType, appSeqNbr, applyTypeNm);
			if (temp != null && temp.size() > 0) {
				result.add(temp.get(0));// MiscAppValueObject
				result.add(temp.get(1));// MiscCustValueObject
			}
			// log.info("Got MiscApp Details========> " + temp.size());
			MiscContractValueObject obj = new MiscContractValueObject();

			log.info(" ***getContractorPermitDetails SQL *****" + sb.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				obj.setLocation(CommonUtility.deNull(rs.getString("exact_loc")));
				obj.setDescription(CommonUtility.deNull(rs.getString("exact_desc")));
				obj.setDescName(CommonUtility.deNull(rs.getString("descr")));
				obj.setOthers(CommonUtility.deNull(rs.getString("other_desc")));
				obj.setFromDate(CommonUtility.deNull(rs.getString("fromDttm")));
				obj.setFromTime(CommonUtility.deNull(rs.getString("fromTime")));
				obj.setToDate(CommonUtility.deNull(rs.getString("toDttm")));
				obj.setToTime(CommonUtility.deNull(rs.getString("toTime")));
				obj.setLicType(""); // Bhuvana 22/2/2016
				obj.setLicNo("");
				obj.setRemarks(CommonUtility.deNull(rs.getString("remarks")));
				obj.setWaiver(CommonUtility.deNull(rs.getString("waive_ind")));
				obj.setContCoNm(CommonUtility.deNull(rs.getString("contract_co_nm")));
				obj.setContCoAddr(CommonUtility.deNull(rs.getString("contract_addr")));
				obj.setContactNm(CommonUtility.deNull(rs.getString("contact_nm")));
				obj.setContactNric(CommonUtility.deNull(rs.getString("contact_nric")));
				obj.setDesignation(CommonUtility.deNull(rs.getString("designation")));
				// Added by Punitha on 06/05 2008
				obj.setReasonWaive(CommonUtility.deNull(rs.getString("waive_reason")));
				// End by Punitha

			}

			// log.info("Got Contract Details========> ");

			// log.info("Going to get doc Details========> ");
			String fullPath = UploadDocument.getOutputFileDir("CONTRACT", "upload");
			// log.info("fullPath --> " + fullPath);
			List<String> docType = new ArrayList<String>();
			List<String> docTypeCd = new ArrayList<String>();
			List<String> docName = new ArrayList<String>();
			List<String> uploadDttm = new ArrayList<String>();
			List<String> uploadBy = new ArrayList<String>();
			List<String> assignedFileName = new ArrayList<String>();

			log.info(" ***getContractorPermitDetails SQL2 *****" + sb1.toString());
			paramMap = new HashMap<String, Object>();
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs1 = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
			while (rs1.next()) {
				docType.add(CommonUtility.deNull(rs1.getString("doc_type")));
				docTypeCd.add(CommonUtility.deNull(rs1.getString("typeNm")));
				docName.add(CommonUtility.deNull(rs1.getString("upload_file_nm")));
				uploadDttm.add(CommonUtility.deNull(rs1.getString("create_dttm")));
				uploadBy.add(getUserName(CommonUtility.deNull(rs1.getString("create_user_id"))));
				assignedFileName.add(fullPath + CommonUtility.deNull(rs1.getString("assign_file_nm")));
			}
			obj.setDocType((String[]) docType.toArray(new String[0]));
			obj.setDocTypeCd((String[]) docTypeCd.toArray(new String[0]));
			obj.setDocName((String[]) docName.toArray(new String[0]));
			obj.setUploadDttm((String[]) uploadDttm.toArray(new String[0]));
			obj.setUploadBy((String[]) uploadBy.toArray(new String[0]));
			obj.setAssignedFileName((String[]) assignedFileName.toArray(new String[0]));
			result.add(obj);
		} catch (BusinessException e) {
			log.info("exception: getContractorPermitDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getContractorPermitDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getContractorPermitDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getContractorPermitDetails  DAO  END");
		}
		log.info("END: getContractorPermitDetails  DAO  END Result **** result: " + result.toString());
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateContractorPermitDetails()

	@Override
	public void updateContractorPermitDetails(String userId, String miscSeqNbr, String status, String coName,
			String applyType, String account, String appStatusCd, MiscContractValueObject obj)
			throws BusinessException {
		// ArrayList assignedFileNameList = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();

		try {
			log.info("START: updateContractorPermitDetails  DAO  Start userId:" + CommonUtil.deNull(userId)
					+ "miscSeqNbr:" + CommonUtil.deNull(miscSeqNbr) + "status:" + CommonUtil.deNull(status) + "coName:"
					+ CommonUtil.deNull(coName) + "applyType:" + CommonUtil.deNull(applyType) + "account:"
					+ CommonUtil.deNull(account) + "appStatusCd:" + CommonUtil.deNull(appStatusCd) + "obj:"
					+ obj.toString());

			sb.append(" update /* MiscAppEJB - updateContractorPermitDetails() */ ");
			sb.append(" misc_contractor set exact_loc =:location, exact_desc");
			sb.append(" =:description, other_desc =:others,");
			sb.append(" fr_dttm =to_date(:fromDate,'dd/mm/yyyy HH24:mi'),");
			sb.append(" to_dttm =to_date(:toDate,'dd/mm/yyyy HH24:mi'),");
			sb.append(" type =:type, psa_license_nbr =:psaNbr,remarks");
			sb.append(" =:remarks, waive_ind =:waiver, contract_co_nm =:contCoNm,");
			sb.append(" contract_addr =:contCoAddr, contact_nm =:contactNm,");
			sb.append(" contact_nric =:contactNric, designation =:designation,");
			sb.append(" last_modify_user_id =:userId, last_modify_dttm = sysdate ");
			sb.append(" where misc_seq_nbr =:miscSeqNbr ");



			updateMiscAppDetails(userId, status, miscSeqNbr, applyType, account, appStatusCd, null, null);
			/*
			 * log.info("Updated MiscAppDetails ========> ");
			 * log.info("From Date ========> " + obj.getFromDate() + obj.getFromTime());
			 * log.info("To Date ========> " + obj.getToDate() + obj.getToTime());
			 */

			log.info(" ***updateContractorPermitDetails SQL *****" + sb.toString());

			paramMap.put("location", obj.getLocation());
			paramMap.put("description", obj.getDescription());
			paramMap.put("others", obj.getOthers());
			paramMap.put("fromDate", obj.getFromDate());
			paramMap.put("toDate", obj.getToDate());
			paramMap.put("type", ""); // Bhuvana 22/2/2016
			paramMap.put("psaNbr", "");
			paramMap.put("remarks", obj.getRemarks());
			paramMap.put("waiver", obj.getWaiver());
			paramMap.put("contCoNm", obj.getContCoNm());
			paramMap.put("contCoAddr", obj.getContCoAddr());
			paramMap.put("contactNm", obj.getContactNm());
			paramMap.put("contactNric", obj.getContactNric());
			paramMap.put("designation", obj.getDesignation());
			paramMap.put("userId", userId);
			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			// delete
			log.info(" ***updateContractorPermitDetails SQL *****" + sb1.toString());

			log.info(" ***updateContractorPermitDetails SQL *****" + sb2.toString());


			// Send Email
			if (status != null && status.equals("S")) {
				String alertCode = "MAF";
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = ContractorPermitSubmit_subject; 
				String templateEmailFile = TemplateSubmit_body_template;
								
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Contractor Permit");
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * + "Type of Application: Contractor Permit \n" + "Reference No.: " +
				 * CommonUtility.deNull(refNbr) + " \n" + "Application Date/Time: " +
				 * CommonUtility.deNull(appDttm) + " \n" + "Company: " + coName + "\n";
				 */
				
				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Contractor Permit , " + "Ref. No.: "
						+ CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm) + " , by " + coName;
				sendSMS(alertCode, sms);
			}
		} catch (BusinessException e) {
			log.info("exception: updateContractorPermitDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: updateContractorPermitDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateContractorPermitDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateContractorPermitDetails  DAO  END");
		}
		// return assignedFileNameList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateContractorPermitDetails()

	@Override
	public void deleteFileData(String userId, String miscSeqNbr, String assignedName) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: deleteFileData obj *** userId: " + CommonUtil.deNull(userId) + ", miscSeqNbr: "
					+ CommonUtil.deNull(miscSeqNbr) + ", assignedName: " + CommonUtil.deNull(assignedName));
			sb.append(
					" delete from misc_upload_doc where misc_seq_nbr =:miscSeqNbr and ASSIGN_FILE_NM =:assignedName ");
			paramMap.put("miscSeqNbr", miscSeqNbr);
			paramMap.put("assignedName", assignedName);
			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
		} catch (NullPointerException e) {
			log.info("exception: deleteFileData ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: deleteFileData ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: deleteFileData  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->voidApplication()
	// Void Application

	@Override
	public void voidApplication(String userId, String coCd, String appSeqNbr) throws BusinessException {
		// String status = null;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		// boolean update = false;
		int cnt = 0;

		try {
			log.info("START: voidApplication  DAO  Start userId:" + CommonUtility.deNull(userId) + "coCd:"
					+ CommonUtility.deNull(coCd) + "appSeqNbr:" + CommonUtility.deNull(appSeqNbr));

			sb.append(" update /* MiscAppEJB - voidApplication() */ ");
			sb.append(" misc_app set app_status = 'V', ");
			sb.append(" void_dttm = sysdate, void_user_id =:userId,");
			sb.append(" last_modify_user_id =:userId, last_modify_dttm = sysdate ");
			sb.append(" where misc_seq_nbr =:appSeqNbr ");

			sb1.append(" SELECT COUNT(*) FROM MISC_VEHICLE ");
			sb1.append(" where misc_seq_nbr =:appSeqNbr ");
			sb1.append(" AND (FR_DTTM - SYSDATE)*24 > (SELECT VALUE FROM ");
			sb1.append(" SYSTEM_PARA WHERE PARA_CD='TPVCT') ");

			log.info(" ***voidApplication sb1 SQL *****" + sb1.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
			if (rs.next())
				cnt = rs.getInt(1);
			if ((cnt == 0) && (!coCd.equalsIgnoreCase("JP"))) {
				throw new BusinessException("Void is not allowed after closing time.");
			}

			log.info(" ***voidApplication sb SQL2 *****" + sb.toString());

			paramMap.put("userId", userId);
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			// log.info("<========= End voidApplication() ========> ");

			MiscAppValueObject obj = getMiscAppInfo(appSeqNbr);

			if (obj.getAppTypeCd().equals("ONV")) {
				String fullStatus = "Voided";
				String alertCode = ConstantUtil.alertCode;
				String refNbr = getMiscRefNbr(appSeqNbr);
				String appDttm = getApplicationDttm(appSeqNbr);

				String subject = OvernightParkVecSubmit_subject;
				String templateEmailFile = OvernightParkVecVoid_body_template;
						
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", obj.getCoName());
				emailInputData.put("typeApp", "Overnight Parking of Vehicle");
				emailInputData.put("fullStatus", fullStatus);
				emailInputData.put("fromDate", obj.getStr_from_dttm());
				emailInputData.put("toDate", obj.getStr_to_dttm());
				emailInputData.put("noOfNight", obj.getNoOfNights());
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*
				 * String msgBody = "The following application has been " + fullStatus +
				 * ": \n\n" + "Type of Application: Overnight Parking of Vehicle \n" +
				 * "Reference No.: " + CommonUtility.deNull(refNbr) + " \n" +
				 * "Application Date/Time: " + CommonUtility.deNull(appDttm) + " \n" +
				 * "Company: " + obj.getCoName() + "\n\n" + "From Date: " +
				 * obj.getStr_from_dttm() + " \n" + "To Date: " + obj.getStr_to_dttm() + " \n" +
				 * "No of Nights: " + obj.getNoOfNights() + " \n";
				 */

				EmailValueObject evo = new EmailValueObject();

				evo.setRecipientAddress(obj.getConEmail().split(";"));
				evo.setCcAddress(getMailRecipients(alertCode, "EML"));

				// Retrieve sender email address
				String sender = gBMiscSenderEmail;
				// String sender = "test@jp.com";
				evo.setSenderAddress(sender);
				evo.setSubject(subject);
				evo.setMessage(msgBody);
				sendMessage(evo);

				String sms = "Application submitted has been " + fullStatus + ": Overnight Parking of Vehicle, "
						+ "Ref. No.: " + CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm);
				sendSMS(alertCode, sms);
			}
		} catch (BusinessException e) {
			log.info("exception: voidApplication ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: voidApplication ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: voidApplication ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: voidApplication  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->supportApplication()
	// Support By Operation

	@Override
	public void supportApplication(String userId, String appStatus, String appSeqNbr, String remarks,
			String supportDate) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: supportApplication  DAO  Start userId:" + CommonUtility.deNull(userId) + "appStatus:"
					+ CommonUtility.deNull(appStatus) + "appSeqNbr:" + CommonUtility.deNull(appSeqNbr) + "remarks:"
					+ CommonUtility.deNull(remarks) + "supportDate:" + CommonUtility.deNull(supportDate));

			sb.append(" update /* MiscAppEJB - supportApplication() */ misc_app  ");
			sb.append(" set app_status =:appStatus, support_dttm =  ");
			sb.append(" to_date(:supportDate,'dd/mm/yyyy HH24mi'),  ");
			sb.append(" support_user_id =:userId, support_remarks =:remarks  ");
			sb.append(" , last_modify_user_id =:userId, last_modify_dttm = sysdate  ");
			sb.append(" where misc_seq_nbr =:appSeqNbr ");

			log.info(" ***supportApplication SQL *****" + sb.toString());

			paramMap.put("appStatus", appStatus);
			paramMap.put("supportDate", supportDate);
			paramMap.put("userId", userId);
			paramMap.put("remarks", remarks);
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
		} catch (NullPointerException e) {
			log.info("exception: supportApplication ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: supportApplication ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: supportApplication  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->approveApplication()
	// Approve Application

	@Override
	public void approveApplication(String userId, String appStatus, String appSeqNbr, String remarks,
			String approveDate) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: approveApplication  DAO  Start userId:" + CommonUtility.deNull(userId) + " appStatus:"
					+ CommonUtility.deNull(appStatus) + " appSeqNbr:" + CommonUtility.deNull(appSeqNbr) + " remarks:"
					+ CommonUtility.deNull(remarks) + " approveDate:" + CommonUtility.deNull(approveDate));

			sb.append(" update /* MiscAppEJB - approveApplication() */ misc_app ");
			sb.append(" set app_status =:appStatus, approve_dttm = ");
			sb.append(" to_date(:approveDate,'dd/mm/yyyy HH24mi'), ");
			sb.append(" approve_user_id =:userId, approve_remarks =:remarks, ");
			sb.append(" last_modify_user_id =:userId, last_modify_dttm = sysdate ");
			sb.append(" where misc_seq_nbr =:appSeqNbr ");

			log.info(" ***approveApplication SQL *****" + sb.toString());

			paramMap.put("appStatus", appStatus);
			paramMap.put("approveDate", approveDate);
			paramMap.put("userId", userId);
			paramMap.put("remarks", remarks);
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
		} catch (NullPointerException e) {
			log.info("exception: approveApplication ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: approveApplication ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: approveApplication  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->approveOnvApplication()
	// Approve Application

	@Override
	public void approveOnvApplication(String userId, String appStatus, String appSeqNbr, String remarks,
			String approveDate) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: approveOnvApplication  DAO  Start userId:" + CommonUtility.deNull(userId) + "appStatus:"
					+ CommonUtility.deNull(appStatus) + "appSeqNbr:" + CommonUtility.deNull(appSeqNbr) + "remarks:"
					+ CommonUtility.deNull(remarks) + "approveDate:" + CommonUtility.deNull(approveDate));

			sb.append(" update /* MiscAppEJB - approveApplication() */ misc_app ");
			sb.append(" set app_status =:appStatus, approve_dttm = ");
			sb.append(" to_date(:approveDate,'dd/mm/yyyy HH24mi'), ");
			sb.append(" approve_user_id =:userId, approve_remarks =:remarks,");
			sb.append(" last_modify_user_id =:userId, last_modify_dttm = sysdate ");
			sb.append(" where misc_seq_nbr =:appSeqNbr ");

			log.info(" ***approveOnvApplication SQL *****" + sb.toString());

			paramMap.put("appStatus", appStatus);
			paramMap.put("approveDate", approveDate);
			paramMap.put("userId", userId);
			paramMap.put("remarks", remarks);
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			// log.info("<========= End approveApplication() ========>
			// ");

			// Send Email & SMS

			if (appStatus != null && (appStatus.equals("A") || appStatus.equals("R"))) {
				String fullStatus = appStatus.equals("A") ? "Approved" : "Rejected";
				String alertCode = ConstantUtil.alertCode;
				String refNbr = getMiscRefNbr(appSeqNbr);
				String appDttm = getApplicationDttm(appSeqNbr);
				MiscAppValueObject obj = getMiscAppInfo(appSeqNbr);
				String subject = OvernightParkVecSubmit_subject;
				String templateEmailFile = OvernightParkVecApproved_body_template;
						
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", obj.getCoName());
				emailInputData.put("typeApp", "Overnight Parking of Vehicle");
				emailInputData.put("fullStatus", fullStatus);
				emailInputData.put("fromDate", obj.getStr_from_dttm());
				emailInputData.put("toDate", obj.getStr_to_dttm());
				emailInputData.put("noOfNight", obj.getNoOfNights());
				emailInputData.put("approveRemarks", obj.getApproveRemarks());
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*
				 * String msgBody = "The following application has been " + fullStatus +
				 * ": \n\n" + "Type of Application: Overnight Parking of Vehicle \n" +
				 * "Reference No.: " + CommonUtility.deNull(refNbr) + " \n" +
				 * "Application Date/Time: " + CommonUtility.deNull(appDttm) + " \n" +
				 * "Company: " + obj.getCoName() + "\n\n" + "From Date: " +
				 * obj.getStr_from_dttm() + " \n" + "To Date: " + obj.getStr_to_dttm() + " \n" +
				 * "No of Nights: " + obj.getNoOfNights() + " \n" + "Approve Remarks: " +
				 * obj.getApproveRemarks() + " \n";
				 */

				EmailValueObject evo = new EmailValueObject();

				evo.setRecipientAddress(obj.getConEmail().split(";"));
				evo.setCcAddress(getMailRecipients(alertCode, "EML"));

				// Retrieve sender email address
				String sender = gBMiscSenderEmail;
				// String sender = "test@jp.com";
				evo.setSenderAddress(sender);
				evo.setSubject(subject);
				evo.setMessage(msgBody);
				sendMessage(evo);

				String sms = "Application submitted has been " + fullStatus + ": Overnight Parking of Vehicle, "
						+ "Ref. No.: " + CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm);
				sendSMS(alertCode, sms);
			}
		} catch (BusinessException e) {
			log.info("exception: approveOnvApplication ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: approveOnvApplication ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: approveOnvApplication ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: approveOnvApplication  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->approveContractApplication()

	@Override
	public void approveContractApplication(String userId, String appStatus, String appSeqNbr, String remarks,
			String approveDate, String waivePermit, String reasonWaive) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();

		try {
			log.info("START: approveContractApplication  DAO  Start userId:" + CommonUtility.deNull(userId)
					+ "appStatus:" + CommonUtility.deNull(appStatus) + "appSeqNbr:" + CommonUtility.deNull(appSeqNbr)
					+ "remarks:" + CommonUtility.deNull(remarks) + "approveDate:" + CommonUtility.deNull(approveDate)
					+ "waivePermit:" + CommonUtility.deNull(waivePermit) + "reasonWaive:"
					+ CommonUtility.deNull(reasonWaive));

			sb.append(" update /* MiscAppEJB - approveContractApplication() */ misc_app ");
			sb.append(" set app_status =:appStatus, approve_dttm = ");
			sb.append(" to_date(:approveDate,'dd/mm/yyyy HH24mi'), ");
			sb.append(" approve_user_id =:userId, approve_remarks =:remarks, ");
			sb.append(" last_modify_user_id =:userId, last_modify_dttm = sysdate ");
			sb.append(" where misc_seq_nbr =:appSeqNbr ");

			sb1.append(" update /* MiscAppEJB - approveBargeApplication() */ misc_contractor ");
			sb1.append(" set waive_ind =:waivePermit, waive_reason=:reasonWaive, ");
			sb1.append(" last_modify_user_id = :userId, last_modify_dttm = sysdate ");
			sb1.append(" where misc_seq_nbr = :appSeqNbr ");

			log.info(" ***approveContractApplication SQL *****" + sb1.toString());
			paramMap = new HashMap<String, Object>();

			paramMap.put("waivePermit", waivePermit);
			paramMap.put("reasonWaive", reasonWaive);
			paramMap.put("userId", userId);
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb1.toString(), paramMap);

			log.info(" ***approveContractApplication SQL2 *****" + sb.toString());

			paramMap.put("appStatus", appStatus);
			paramMap.put("approveDate", approveDate);
			paramMap.put("userId", userId);
			paramMap.put("remarks", remarks);
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

		} catch (NullPointerException e) {
			log.info("exception: approveContractApplication ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: approveContractApplication ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: approveContractApplication  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->approveBargeApplication()

	@Override
	public void approveBargeApplication(String userId, String appStatus, String appSeqNbr, String remarks,
			MiscBargeValueObject obj, String approveDate) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();

		try {
			log.info("START: approveBargeApplication  DAO  Start userId:" + CommonUtility.deNull(userId) + "appStatus:"
					+ CommonUtility.deNull(appStatus) + "appSeqNbr:" + CommonUtility.deNull(appSeqNbr) + "remarks:"
					+ CommonUtility.deNull(remarks) + "approveDate:" + CommonUtility.deNull(approveDate) + "obj:"
					+ obj.toString());

			sb.append(" update /* MiscAppEJB - approveBargeApplication() */ misc_app ");
			sb.append(" set app_status =:appStatus, ");
			sb.append(" approve_dttm = to_date(:approveDate,'dd/mm/yyyy HH24mi'), ");
			sb.append(" approve_user_id =:userId, approve_remarks =:remarks , ");
			sb.append(" last_modify_user_id =:userId, last_modify_dttm = sysdate ");
			sb.append(" where misc_seq_nbr =:appSeqNbr ");

			sb1.append(" update /* MiscAppEJB - approveBargeApplication() */ misc_barge ");
			sb1.append(" set alloc_berth_nbr = :allocBerthNo, ");
			sb1.append(" alloc_wharf_mark_fr =:wharfMarkFr, ");
			sb1.append(" alloc_wharf_mark_to = :wharfMarkTo, last_modify_user_id =:userId, ");
			sb1.append(" last_modify_dttm = sysdate ");
			sb1.append(" where misc_seq_nbr = :appSeqNbr ");

			log.info(" ***approveBargeApplication SQL *****" + sb1.toString());

			paramMap.put("allocBerthNo", obj.getAllocBerthNo());
			paramMap.put("wharfMarkFr", obj.getWharfMarkFr());
			paramMap.put("wharfMarkTo", obj.getWharfMarkTo());
			paramMap.put("userId", userId);
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb1.toString(), paramMap);

			log.info(" ***approveBargeApplication SQL2 *****" + sb.toString());
			paramMap = new HashMap<String, Object>();
			paramMap.put("appStatus", appStatus);
			paramMap.put("approveDate", approveDate);
			paramMap.put("userId", userId);
			paramMap.put("remarks", remarks);
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
		} catch (NullPointerException e) {
			log.info("exception: approveBargeApplication ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: approveBargeApplication ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: approveBargeApplication  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->approveBillHotworkApplication()
	// Approve Hotwork Bill Application

	@Override
	public void approveBillHotworkApplication(String userId, String appSeqNbr, String approveBillDate)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: approveBillHotworkApplication  DAO  Start userId:" + CommonUtility.deNull(userId)
					+ "appSeqNbr:" + CommonUtility.deNull(appSeqNbr) + "approveBillDate:"
					+ CommonUtility.deNull(approveBillDate));

			sb.append(" update /* MiscAppEJB - approveBillHotworkApplication() */ misc_app ");
			sb.append(" set app_status = 'C', approve_bill_dttm = to_date(:approveBillDate, ");
			sb.append(" 'dd/mm/yyyy HH24mi'), ");
			sb.append(" approve_bill_user_id =:userId, last_modify_user_id =:userId, ");
			sb.append(" last_modify_dttm = sysdate ");
			sb.append(" where misc_seq_nbr =:appSeqNbr ");

			log.info(" ***approveBillHotworkApplication SQL *****" + sb.toString());

			paramMap.put("approveBillDate", approveBillDate);
			paramMap.put("userId", userId);
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
		} catch (NullPointerException e) {
			log.info("exception: approveBillHotworkApplication ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: approveBillHotworkApplication ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: approveBillHotworkApplication  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->closeBillOvernightParkingVehicleDetails()

	@Override
	public void closeBillOvernightParkingVehicleDetails(String userId, String miscSeqNbr, MiscVehValueObject obj,
			String closeDate) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: closeBillOvernightParkingVehicleDetails  DAO  Start userId:" + CommonUtility.deNull(userId)
					+ " miscSeqNbr:" + CommonUtility.deNull(miscSeqNbr) + " obj:" + obj.toString() + " closeDate:"
					+ CommonUtility.deNull(closeDate));

			sb.append(" update /* MiscAppEJB - closeBillOvernightParkingVehicleDetails() */ ");
			sb.append(" misc_vehicle set actual_fr_dttm = to_date(:actFromDate,'dd/mm/yyyy'), ");
			sb.append("  actual_to_dttm = to_date(:actToDate,'dd/mm/yyyy'), ");
			sb.append(" actual_nbr_night = :actNoNights, ");
			sb.append(" last_modify_user_id = :userId, last_modify_dttm = ");
			sb.append(" sysdate where misc_seq_nbr =:miscSeqNbr ");

			String status = "C";
			closeBillApplication(userId, status, miscSeqNbr, closeDate);
			// log.info("Updated MiscAppDetails ========> ");
			// log.info("getActNoNights ========> " +
			// obj.getActNoNights());
			// log.info("getActFromDate ========> " +
			// obj.getActFromDate());

			log.info(" ***closeBillOvernightParkingVehicleDetails SQL *****" + sb.toString());

			paramMap.put("actFromDate", obj.getActFromDate());
			paramMap.put("actToDate", obj.getActToDate());
			paramMap.put("actNoNights", obj.getActNoNights());
			paramMap.put("userId", userId);
			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
		} catch (BusinessException e) {
			log.info("exception: closeBillOvernightParkingVehicleDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: closeBillOvernightParkingVehicleDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: closeBillOvernightParkingVehicleDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: closeBillOvernightParkingVehicleDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->closeBillParkingOfLineTowBargeDetails()

	@Override
	public void closeBillParkingOfLineTowBargeDetails(String userId, String miscSeqNbr, MiscBargeValueObject obj,
			String closeDate) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: closeBillParkingOfLineTowBargeDetails  DAO  Start userId:" + CommonUtility.deNull(userId)
					+ "miscSeqNbr:" + CommonUtility.deNull(miscSeqNbr) + "obj:" + obj.toString() + "closeDate:"
					+ CommonUtility.deNull(closeDate));

			sb.append(" update /* MiscAppEJB - closeBillParkingOfLineTowBargeDetails() */ ");
			sb.append(" misc_barge set barge_atb = to_date(:bargeAtb,'dd/mm/yyyy HH24:mi'), ");
			sb.append(" barge_atu = to_date(:bargeAtu,'dd/mm/yyyy HH24:mi'), ");
			sb.append(" last_modify_user_id = :userId, last_modify_dttm = sysdate ");
			sb.append(" where misc_seq_nbr = :miscSeqNbr ");

			String status = "C";
			closeBillApplication(userId, status, miscSeqNbr, closeDate);

			log.info(" ***closeBillParkingOfLineTowBargeDetails SQL *****" + sb.toString());

			/*
			 * paramMap.put(1, obj.getAllocBerthNo()); paramMap.put(2,
			 * obj.getWharfMarkFr()); paramMap.put(3, obj.getWharfMarkTo());
			 */
			paramMap.put("bargeAtb", obj.getBargeAtbDttm() + obj.getBargeAtbTime());
			paramMap.put("bargeAtu", obj.getBargeAtuDttm() + obj.getBargeAtuTime());
			paramMap.put("userId", userId);
			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
		} catch (BusinessException e) {
			log.info("exception: closeBillParkingOfLineTowBargeDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: closeBillParkingOfLineTowBargeDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: closeBillParkingOfLineTowBargeDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: closeBillParkingOfLineTowBargeDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->closeBillSpreaderDetails()

	@Override
	public void closeBillSpreaderDetails(String userId, String miscSeqNbr, MiscSpreaderValueObject obj,
			String closeDate) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: closeBillSpreaderDetails  DAO  Start userId:" + CommonUtility.deNull(userId)
					+ " miscSeqNbr:" + CommonUtility.deNull(miscSeqNbr) + " obj:" + obj.toString() + " closeDate:"
					+ CommonUtility.deNull(closeDate));
					
			String issueDateTime = obj.getIssueDt() + " " + obj.getIssueTime();
			String receiveDateTime = obj.getReceiveDt() + " " + obj.getReceiveTime();
			
			log.info(" issueDateTime : " + issueDateTime + "receiveDateTime : " + receiveDateTime );
			
			sb.append(" update /* MiscAppEJB - closeBillSpreaderDetails() */ ");
			sb.append(" misc_spreader set issue_dttm = to_date(:issueDt,'dd/mm/yyyy HH24:mi'), ");
			sb.append(" issue_by_staff = :issueByStaff, receive_by_cust = :receiveByCust, ");
			sb.append(" return_dttm = to_date(:receiveDt,'dd/mm/yyyy HH24:mi'), ");
			sb.append(" receive_by_staff = :receiveByStaff, return_by_cust = ");
			sb.append(" :returnByCust, last_modify_user_id = :userId, ");
			sb.append(" last_modify_dttm = sysdate where misc_seq_nbr = :miscSeqNbr ");

			String status = "C";
			closeBillApplication(userId, status, miscSeqNbr, closeDate);

			log.info(" ***closeBillSpreaderDetails SQL *****" + sb.toString());

			paramMap.put("issueDt", issueDateTime);
			paramMap.put("issueByStaff", obj.getIssueByStaff());
			paramMap.put("receiveByCust", obj.getReceiveByCust());
			paramMap.put("receiveDt", receiveDateTime);
			paramMap.put("receiveByStaff", obj.getReceiveByStaff());
			paramMap.put("returnByCust", obj.getReturnByCust());
			paramMap.put("userId", userId);
			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			int i = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			log.info("i = " + i);
		} catch (BusinessException e) {
			log.info("exception: closeBillSpreaderDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: closeBillSpreaderDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: closeBillSpreaderDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: closeBillSpreaderDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->closeBillReeferContainerPowerOutletDetails()
	@Override
	public void closeBillReeferContainerPowerOutletDetails(String userId, String miscSeqNbr, MiscReeferValueObject obj,
			String closeDate) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();

		try {
			log.info("START: closeBillReeferContainerPowerOutletDetails  DAO  Start userId:"
					+ CommonUtility.deNull(userId) + "miscSeqNbr:" + CommonUtility.deNull(miscSeqNbr) + "obj:"
					+ obj.toString() + "closeDate:" + CommonUtility.deNull(closeDate));

			sb.append(" delete /* MiscAppEJB - closeBillReeferContainerPowerOutletDetails() */ ");
			sb.append(" from misc_reefer_det where misc_seq_nbr =:miscSeqNbr ");

			sb1.append(" insert into misc_reefer_det (misc_seq_nbr, item_nbr, ");
			sb1.append(" cntr_nbr, cntr_size, cntr_status, ");
			sb1.append(" plug_in_dttm, plug_out_dttm, delivery_dttm, dn_po_nbr, remarks, ");
			sb1.append(" last_modify_user_id, last_modify_dttm) values ( ");
			sb1.append(" :miscSeqNbr, :itemNbr, :cntrNo, :cntrSize, ");
			sb1.append(" :cntrStatus, to_date(:plugIn,'dd/mm/yyyy HH24:mi'), ");
			sb1.append(" to_date(:plugOut,'dd/mm/yyyy HH24:mi'), to_date(:deliveryDttm,'dd/mm/yyyy'), ");
			sb1.append(" :dnPoNbr, :remarks, :userId,sysdate) ");

			String status = "C";
			closeBillApplication(userId, status, miscSeqNbr, closeDate);
			// log.info("Updated MiscAppDetails ========> ");

			log.info(" ***closeBillReeferContainerPowerOutletDetails SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			// log.info("Deleted!!!");

			// log.info("Going to process Reefer Details========> ");
			String[] cntrNo = obj.getCntrNo();
			String[] cntrSize = obj.getCntrSize();
			String[] cntrStatus = obj.getCntrStatus();
			String[] plugInDt = obj.getPlugInDt();
			// String[] plugInTime = obj.getPlugInTime();
			String[] plugOutDt = obj.getPlugOutDt();
			// String[] plugOutTime = obj.getPlugOutTime();
			String[] deliveryDttm = obj.getDeliveryDttm();
			String[] dnPoNbr = obj.getDnPoNbr();
			String[] remarks = obj.getRemarks();

			if (cntrNo != null) {
				log.info(" ***closeBillReeferContainerPowerOutletDetails SQL *****" + sb1.toString());

				for (int i = 0, j = 1; i < cntrNo.length; i++, j++) {
					paramMap.put("miscSeqNbr", miscSeqNbr);
					paramMap.put("itemNbr", j);
					paramMap.put("cntrNo", cntrNo[i]);
					paramMap.put("cntrSize", cntrSize[i]);
					paramMap.put("cntrStatus", cntrStatus[i]);
					paramMap.put("plugIn", plugInDt[i]);
					paramMap.put("plugOut", plugOutDt[i]);
					if (deliveryDttm != null && deliveryDttm[i] != null)
						paramMap.put("deliveryDttm", deliveryDttm[i]);
					else
						paramMap.put("deliveryDttm", null);
					if (dnPoNbr != null && dnPoNbr[i] != null)
						paramMap.put("dnPoNbr", dnPoNbr[i]);
					else
						paramMap.put("dnPoNbr", null);
					if (remarks != null && remarks[i] != null)
						paramMap.put("remarks", remarks[i]);
					else
						paramMap.put("remarks", null);
					paramMap.put("userId", userId);

					log.info(" *** paramMap: *****" + paramMap.toString());

					namedParameterJdbcTemplate.update(sb1.toString(), paramMap);
				}
			}
		} catch (BusinessException e) {
			log.info("exception: closeBillReeferContainerPowerOutletDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: closeBillReeferContainerPowerOutletDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: closeBillReeferContainerPowerOutletDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: closeBillReeferContainerPowerOutletDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->closeBillUseOfSpaceDetails()

	@Override
	public void closeBillUseOfSpaceDetails(String userId, String miscSeqNbr, MiscSpaceValueObject obj, String closeDate)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		try {
			log.info("START: closeBillUseOfSpaceDetails  DAO  Start userId:" + CommonUtility.deNull(userId)
					+ "miscSeqNbr:" + CommonUtility.deNull(miscSeqNbr) + "obj:" + obj.toString() + "closeDate:"
					+ CommonUtility.deNull(closeDate));
			// Amended by Punitha on 19/11/2009
			sb.append(" delete  from misc_space_det where misc_seq_nbr =:miscSeqNbr ");

			sb1.append(" insert into misc_space_det (misc_seq_nbr, item_nbr, ");
			sb1.append(" bay_nbr, area_use, ops_start_dttm, ops_end_dttm, last_modify_user_id, ");
			sb1.append(" last_modify_dttm) values ( ");
			sb1.append(" :miscSeqNbr, :itemNbr, :bayNbr, :areaUsed, to_date(:opsStartDttm,'dd/mm/yyyy HH24:mi'), ");
			sb1.append(" to_date(:opsEndDttm,'dd/mm/yyyy HH24:mi'), :userId,sysdate) ");

			String status = "C";
			closeBillApplication(userId, status, miscSeqNbr, closeDate);

			log.info(" ***closeBillUseOfSpaceDetails SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			String[] bayNbr = obj.getBayNbr();
			String[] areaUsed = obj.getAreaUsed();
			String[] opsStartDttm = obj.getOpsStartDttm();
			String[] opsEndDttm = obj.getOpsEndDttm();

			List<String> bayNbrList = null;
			List<String> areaUsedList = null;
			List<String> opsStartDttmList = null;
			List<String> opsEndDttmList = null;

			if (bayNbr != null) {
				bayNbrList = new ArrayList<String>();
				String[] tempBayNbrList = bayNbr[0].split(",");
				for (int i = 0; i < tempBayNbrList.length; i++) {
					bayNbrList.add(tempBayNbrList[i]);
				}
			}

			if (areaUsed != null) {
				areaUsedList = new ArrayList<String>();
				String[] tempAreaUsedList = areaUsed[0].split(",");
				for (int i = 0; i < tempAreaUsedList.length; i++) {
					areaUsedList.add(tempAreaUsedList[i]);
				}
			}

			if (opsStartDttm != null) {
				opsStartDttmList = new ArrayList<String>();
				String[] tempOpsStartDttmList = opsStartDttm[0].split(",");
				for (int i = 0; i < tempOpsStartDttmList.length; i++) {
					opsStartDttmList.add(tempOpsStartDttmList[i]);
				}
			}

			if (opsEndDttm != null) {
				opsEndDttmList = new ArrayList<String>();
				String[] tempOpsEndDttmList = opsEndDttm[0].split(",");
				for (int i = 0; i < tempOpsEndDttmList.length; i++) {
					opsEndDttmList.add(tempOpsEndDttmList[i]);
				}
			}

			if (bayNbr != null) {
				for (int i = 0, j = 1; i < bayNbrList.size(); i++, j++) {

					log.info(" ***closeBillUseOfSpaceDetails SQL *****" + sb1.toString());

					paramMap.put("miscSeqNbr", miscSeqNbr);
					paramMap.put("itemNbr", j);
					paramMap.put("bayNbr", bayNbrList.get(i).toString());
					paramMap.put("areaUsed", areaUsedList.get(i).toString());
					paramMap.put("opsStartDttm", opsStartDttmList.get(i).toString());
					paramMap.put("opsEndDttm", opsEndDttmList.get(i).toString());
					paramMap.put("userId", userId);

					log.info(" *** paramMap: *****" + paramMap.toString());

					namedParameterJdbcTemplate.update(sb1.toString(), paramMap);
				}
			}

		} catch (BusinessException e) {
			log.info("exception: closeBillUseOfSpaceDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: closeBillUseOfSpaceDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: closeBillUseOfSpaceDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: closeBillUseOfSpaceDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->closeBillHotworkDetails()

	@Override
	public void closeBillHotworkDetails(String userId, String miscSeqNbr, MiscHotworkValueObject obj, String closeDate)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();

		try {
			log.info("START: closeBillHotworkDetails  DAO  Start userId:" + CommonUtility.deNull(userId)
					+ " miscSeqNbr:" + CommonUtility.deNull(miscSeqNbr) + " obj:" + obj.toString() + " closeDate:"
					+ CommonUtility.deNull(closeDate));

			sb.append(" delete /* MiscAppEJB - closeBillHotworkDetails() */ ");
			sb.append(" from misc_hotwork_det where misc_seq_nbr =:miscSeqNbr ");

			sb1.append(" insert into misc_hotwork_det (misc_seq_nbr, item_nbr, ");
			sb1.append(" standby_fr_dttm, standby_to_dttm, charge_time, last_modify_user_id, ");
			sb1.append(" last_modify_dttm, fireman_nm) values ( ");
			sb1.append(" :miscSeqNbr, :itemNbr, to_date(:standbyFrFr,'dd/mm/yyyy HH24:mi'), ");
			sb1.append(" to_date(:standbyFrTo,'dd/mm/yyyy HH24:mi'), ");
			sb1.append(" :chargeTime, :userId, sysdate, :fireManNm) ");

			sb2.append(" update /* MiscAppEJB - closeBillHotworkDetails() */ misc_hotwork ");
			sb2.append(" set tot_standby_hr =:totStandbyHr, last_modify_user_id =:userId, ");
			sb2.append(" last_modify_dttm = sysdate,no_fireman_ind =:noFiremanInd ");
			sb2.append(" where misc_seq_nbr =:miscSeqNbr ");

			String status = "P";
			closeBillApplication(userId, status, miscSeqNbr, closeDate);
			// log.info("Updated MiscAppDetails ========> ");
			// log.info("Delete hotwork Details ========> ");

			log.info(" ***closeBillHotworkDetails SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			// log.info("Deleted!!!");

			// log.info("Going to process hotwork Details========> ");
			String[] standbyFrDttm = obj.getStandbyFrDttm();
			String[] standbyFrTime = obj.getStandbyFrTime();
			String[] standbyToTime = obj.getStandbyToTime();
			String[] chargeTime = obj.getChargeTime();
			String[] fireManNm = obj.getFireManNm();

			List<String> standbyFrDttmList = null;
			List<String> fireManNmList = null;
			List<String> standbyFrTimeList = null;
			List<String> standbyToTimeList = null;
			List<String> chargeTimeList = null;

			if (standbyFrDttm != null) {
				standbyFrDttmList = new ArrayList<String>();
				String[] tempStandbyFrDttmList = standbyFrDttm[0].split(",");
				for (int i = 0; i < tempStandbyFrDttmList.length; i++) {
					standbyFrDttmList.add(tempStandbyFrDttmList[i]);
				}
			}

			if (fireManNm != null) {
				fireManNmList = new ArrayList<String>();
				String[] tempFireManNmList = fireManNm[0].split(",");
				for (int i = 0; i < tempFireManNmList.length; i++) {
					fireManNmList.add(tempFireManNmList[i]);
				}
			}

			if (standbyFrTime != null) {
				standbyFrTimeList = new ArrayList<String>();
				String[] tempStandbyFrTimeList = standbyFrTime[0].split(",");
				for (int i = 0; i < tempStandbyFrTimeList.length; i++) {
					standbyFrTimeList.add(tempStandbyFrTimeList[i]);
				}
			}

			if (standbyToTime != null) {
				standbyToTimeList = new ArrayList<String>();
				String[] tempStandbyToTimeList = standbyToTime[0].split(",");
				for (int i = 0; i < tempStandbyToTimeList.length; i++) {
					standbyToTimeList.add(tempStandbyToTimeList[i]);
				}
			}

			if (chargeTime != null) {
				chargeTimeList = new ArrayList<String>();
				String[] tempChargeTimeList = chargeTime[0].split(",");
				for (int i = 0; i < tempChargeTimeList.length; i++) {
					chargeTimeList.add(tempChargeTimeList[i]);
				}
			}

			if (standbyFrDttm != null) {
				paramMap = new HashMap<String, Object>();
				log.info(" ***closeBillHotworkDetails SQL2 *****" + sb1.toString());
				log.info(" ***what is this :  *****" + chargeTimeList.get(0).toString());

				for (int i = 0, j = 1; i < standbyFrDttmList.size(); i++, j++) {
					paramMap.put("miscSeqNbr", miscSeqNbr);
					paramMap.put("itemNbr", j);
					paramMap.put("standbyFrFr",
							standbyFrDttmList.get(i).toString() + standbyFrTimeList.get(i).toString());
					paramMap.put("standbyFrTo",
							standbyFrDttmList.get(i).toString() + standbyToTimeList.get(i).toString());
					paramMap.put("chargeTime", chargeTimeList.get(i).toString());
					paramMap.put("userId", userId);
					paramMap.put("fireManNm", fireManNmList.get(i).toString());

					log.info(" *** paramMap: *****" + paramMap.toString());

					namedParameterJdbcTemplate.update(sb1.toString(), paramMap);
				}
			}

			log.info(" ***closeBillHotworkDetails SQL3 *****" + sb2.toString());
			paramMap = new HashMap<String, Object>();
			paramMap.put("totStandbyHr", obj.getTotStandbyHr());
			paramMap.put("userId", userId);
			// Added on 30/05/2007 by Punitha. To add checkbox for InspectInd
			if (CommonUtility.deNull(obj.getInspectInd()).equals(""))
				paramMap.put("noFiremanInd", "N");
			else
				paramMap.put("noFiremanInd", "Y");

			paramMap.put("miscSeqNbr", miscSeqNbr);
			log.info(" *** paramMap: *****" + paramMap.toString());
			// Added on 30/05/2007 by Punitha. To add checkbox for InspectInd

			namedParameterJdbcTemplate.update(sb2.toString(), paramMap);

		} catch (BusinessException e) {
			log.info("exception: closeBillHotworkDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: closeBillHotworkDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: closeBillHotworkDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: closeBillHotworkDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->uploadDocument()

	@Override
	public String uploadDocument(String userId, String miscSeqNbr, String status, String docType, String docName)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		String name = null;
		String assignedName = null;

		try {
			log.info("START: uploadDocument  DAO  Start userId:" + CommonUtility.deNull(userId) + "miscSeqNbr:"
					+ CommonUtility.deNull(miscSeqNbr) + "status:" + CommonUtility.deNull(status) + "docType:"
					+ CommonUtility.deNull(docType) + "docName:" + CommonUtility.deNull(docName));

			sb1.append(" delete /* MiscAppEJB - uploadDocument() */ ");
			sb1.append(" from misc_upload_doc where misc_seq_nbr = :miscSeqNbr ");

			sb.append(" insert into misc_upload_doc (misc_seq_nbr, doc_type, upload_file_nm, ");
			sb.append(" assign_file_nm, create_user_id, create_dttm) values ");
			sb.append(" (:miscSeqNbr,:docType,:name,:assignedName,:userId,sysdate) ");
			/*
			 * log.info("Going to process doc Details========> ");
			 * log.info("docType ========> " + docType); log.info("docName========> " +
			 * docName);
			 */
			name = docName.substring(docName.lastIndexOf("\\") + 1);
			assignedName = getNextDocSeqNumber() + "_" + name;
			if (docType != null && docName != null) {

				log.info(" ***uploadDocument SQL *****" + sb.toString());

				paramMap.put("miscSeqNbr", miscSeqNbr);
				paramMap.put("docType", docType);
				paramMap.put("name", name);
				paramMap.put("assignedName", assignedName);
				paramMap.put("userId", userId);

				log.info(" *** paramMap: *****" + paramMap.toString());

				namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			}
		} catch (BusinessException e) {
			log.info("exception: uploadDocument ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: uploadDocument ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: uploadDocument ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: uploadDocument  DAO  END Result **** assignedName: " + CommonUtility.deNull(assignedName));
		}
		return assignedName;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getUploadDocumentDetails()

	@Override
	public List<MiscParkMacValueObject> getUploadDocumentDetails(String appSeqNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<MiscParkMacValueObject> result = new ArrayList<MiscParkMacValueObject>();

		try {
			log.info("START: getUploadDocumentDetails  DAO  Start appSeqNbr:" + CommonUtility.deNull(appSeqNbr));

			sb.append(" select /* MiscAppEJB - getUploadDocumentDetails() */ doc_type, ");
			sb.append(" a.misc_type_nm typeNm, upload_file_nm, create_user_id, ");
			sb.append(" to_char(create_dttm,'ddmmyyyy HH24mi') create_dttm, assign_file_nm ");
			sb.append(" from misc_upload_doc, misc_type_code a where misc_seq_nbr =:appSeqNbr ");
			sb.append(" and a.cat_cd = 'MISC_MDOC' and  misc_upload_doc.doc_type = a.misc_type_cd ");
			sb.append(" and a.rec_status = 'A' order by create_dttm ");

			MiscParkMacValueObject parkMac = new MiscParkMacValueObject();

			// log.info("Going to get doc Details========> ");
			String fullPath = UploadDocument.getOutputFileDir("MACHINE", "upload");
			// log.info("fullPath --> " + fullPath);
			List<String> docType = new ArrayList<String>();
			List<String> docTypeCd = new ArrayList<String>();
			List<String> docName = new ArrayList<String>();
			List<String> uploadDttm = new ArrayList<String>();
			List<String> uploadBy = new ArrayList<String>();
			List<String> assignedFileName = new ArrayList<String>();

			log.info(" ***getUploadDocumentDetails SQL *****" + sb.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				docType.add(CommonUtility.deNull(rs.getString("doc_type")));
				docTypeCd.add(CommonUtility.deNull(rs.getString("typeNm")));
				docName.add(CommonUtility.deNull(rs.getString("upload_file_nm")));
				uploadDttm.add(CommonUtility.deNull(rs.getString("create_dttm")));
				uploadBy.add(getUserName(CommonUtility.deNull(rs.getString("create_user_id"))));
				assignedFileName.add(fullPath + CommonUtility.deNull(rs.getString("assign_file_nm")));
			}

			parkMac.setDocType((String[]) docType.toArray(new String[0]));
			parkMac.setDocTypeCd((String[]) docTypeCd.toArray(new String[0]));
			parkMac.setDocName((String[]) docName.toArray(new String[0]));
			parkMac.setUploadDttm((String[]) uploadDttm.toArray(new String[0]));
			parkMac.setUploadBy((String[]) uploadBy.toArray(new String[0]));
			parkMac.setAssignedFileName((String[]) assignedFileName.toArray(new String[0]));
			result.add(parkMac);
		} catch (BusinessException e) {
			log.info("exception: getUploadDocumentDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getUploadDocumentDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getUploadDocumentDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getUploadDocumentDetails  DAO  END Result **** result: " + result.toString());
		}
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getContractUploadDocumentDetails()

	@Override
	public List<MiscContractValueObject> getContractUploadDocumentDetails(String appSeqNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<MiscContractValueObject> result = new ArrayList<MiscContractValueObject>();

		try {
			log.info(
					"START: getContractUploadDocumentDetails  DAO  Start appSeqNbr:" + CommonUtility.deNull(appSeqNbr));

			sb.append(" select /* MiscAppEJB - getContractUploadDocumentDetails() */ doc_type, ");
			sb.append(" a.misc_type_nm typeNm, upload_file_nm, create_user_id, ");
			sb.append(" to_char(create_dttm,'ddmmyyyy HH24mi') create_dttm, assign_file_nm ");
			sb.append(" from misc_upload_doc, misc_type_code a where misc_seq_nbr =:appSeqNbr ");
			sb.append(" and a.cat_cd = 'MISC_CDOC' and  misc_upload_doc.doc_type = a.misc_type_cd ");
			sb.append(" and a.rec_status = 'A' order by create_dttm ");

			MiscContractValueObject contractObj = new MiscContractValueObject();

			// log.info("Going to get doc Details========> ");
			String fullPath = UploadDocument.getOutputFileDir("CONTRACT", "upload");
			// log.info("fullPath --> " + fullPath);
			List<String> docType = new ArrayList<String>();
			List<String> docTypeCd = new ArrayList<String>();
			List<String> docName = new ArrayList<String>();
			List<String> uploadDttm = new ArrayList<String>();
			List<String> uploadBy = new ArrayList<String>();
			List<String> assignedFileName = new ArrayList<String>();

			log.info(" ***getContractUploadDocumentDetails SQL *****" + sb.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				docType.add(CommonUtility.deNull(rs.getString("doc_type")));
				docTypeCd.add(CommonUtility.deNull(rs.getString("typeNm")));
				docName.add(CommonUtility.deNull(rs.getString("upload_file_nm")));
				uploadDttm.add(CommonUtility.deNull(rs.getString("create_dttm")));
				uploadBy.add(getUserName(CommonUtility.deNull(rs.getString("create_user_id"))));
				assignedFileName.add(fullPath + CommonUtility.deNull(rs.getString("assign_file_nm")));
			}

			contractObj.setDocType((String[]) docType.toArray(new String[0]));
			contractObj.setDocTypeCd((String[]) docTypeCd.toArray(new String[0]));
			contractObj.setDocName((String[]) docName.toArray(new String[0]));
			contractObj.setUploadDttm((String[]) uploadDttm.toArray(new String[0]));
			contractObj.setUploadBy((String[]) uploadBy.toArray(new String[0]));
			contractObj.setAssignedFileName((String[]) assignedFileName.toArray(new String[0]));
			result.add(contractObj);
		} catch (BusinessException e) {
			log.info("exception: getContractUploadDocumentDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getContractUploadDocumentDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getContractUploadDocumentDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getContractUploadDocumentDetails  DAO  END Result **** result: " + result.toString());
		}
		return result;

	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->deleteDocument()

	@Override
	public void deleteDocument(String miscSeqNbr, String[] docName) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String name = null;
		try {
			log.info("START: deleteDocument  DAO  Start miscSeqNbr:" + CommonUtility.deNull(miscSeqNbr) + "docName:"
					+ docName.toString());
			sb.append(" delete /* MiscAppEJB - deleteDocument() */ ");
			sb.append(" from misc_upload_doc where misc_seq_nbr ");
			sb.append(" =:miscSeqNbr and assign_file_nm =:name ");

			log.info(" ***deleteDocument SQL *****" + sb.toString());

			for (int i = 0; i < docName.length; i++) {
				name = docName[i].substring(docName[i].lastIndexOf("\\") + 1);
				paramMap.put("miscSeqNbr", miscSeqNbr);
				paramMap.put("name", name);

				log.info(" *** paramMap: *****" + paramMap.toString());

				namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			}
		} catch (NullPointerException e) {
			log.info("exception: deleteDocument ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: deleteDocument ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: deleteDocument  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getNextDocSeqNumber()

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getClosingTime()
	// Added on 13/06 2007 by Punitha. To retrieve the closing time parameter value
	// Retrieve the Closing Time Parameter

	@Override
	public String getClosingTime() throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String closingTime = "";

		try {
			log.info("START: getClosingTime  DAO  Start ");

			sb.append(" select /* MiscAppEJB - getClosingTime() */ value ");
			sb.append(" from system_para where para_cd = 'MILTB' ");

			log.info(" ***getClosingTime SQL *****" + sb.toString());

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				closingTime = CommonUtility.deNull(rs.getString("value"));
			}
		} catch (NullPointerException e) {
			log.info("exception: getClosingTime ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getClosingTime ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getClosingTime  DAO  END Result **** closingTime: " + CommonUtility.deNull(closingTime));
		}
		return closingTime;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getVesselDetails()
	// Added on 20/07/2007 by Punitha.To display the vessel details

	@Override
	public List<MiscAppValueObject> getVesselDetails(String varCode) throws BusinessException {
		List<MiscAppValueObject> vesselList = new ArrayList<MiscAppValueObject>();
		MiscAppValueObject miscAppObj;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getVesselDetails  DAO  Start varCode:" + CommonUtility.deNull(varCode));

			sb.append("	select /* MiscAppEJB - getVesselDetails() */ ");
			sb.append("	a.vsl_nm,a.in_voy_nbr,a.out_voy_nbr,a.vv_cd, ");
			sb.append("	to_char(b.atb_dttm,'ddmmyyyy HH24mi') atb_dttm, ");
			sb.append("	to_char(b.atu_dttm,'ddmmyyyy HH24mi') atu_dttm ");
			sb.append("	from vessel_call a,berthing b ");
			sb.append("	where a.vv_cd =:varCode and a.vv_cd = b.vv_cd ");

			log.info(" ***getVesselDetails SQL *****" + sb.toString());

			paramMap.put("varCode", varCode);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				miscAppObj = new MiscAppValueObject();
				miscAppObj.setVarCode(rs.getString("vv_cd"));
				miscAppObj.setVslName(rs.getString("vsl_nm"));
				miscAppObj.setInVoyNbr(rs.getString("in_voy_nbr"));
				miscAppObj.setOutVoyNbr(rs.getString("out_voy_nbr"));
				miscAppObj.setAtbDttm(rs.getString("atb_dttm"));
				miscAppObj.setAtuDttm(rs.getString("atu_dttm"));
				vesselList.add(miscAppObj);
			}
		} catch (NullPointerException e) {
			log.info("exception: getVesselDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getVesselDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselDetails  DAO  END Result **** vesselList: " + vesselList.toString());
		}
		return vesselList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getApproverId()
	// Added by Punitha on 05/05/2008. To get the approver id for the application

	@Override
	public String getApproverId(String appSeqNbr) throws BusinessException {
		String approver = null;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getApproverId  DAO  Start appSeqNbr:" + CommonUtility.deNull(appSeqNbr));

			sb.append(" select /* MiscAppEJB - getApproverId */ approve_user_id ");
			sb.append(" from misc_app where misc_seq_nbr =:appSeqNbr ");

			log.info(" ***getApproverId SQL *****" + sb.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				approver = rs.getString("approve_user_id");
			}
		} catch (NullPointerException e) {
			log.info("exception: getApproverId ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getApproverId ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getApproverId  DAO  END Result***** approver: " + CommonUtility.deNull(approver));
		}
		return approver;
	} // Ended by Punitha

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getExpiredCompanyList()
	// get all companies whose equipment's insurence expired

	@Override
	public List<ExpiredCompanyValueObject> getExpiredCompanyList() throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<ExpiredCompanyValueObject> list = new ArrayList<ExpiredCompanyValueObject>();

		try {
			log.info("START: getExpiredCompanyList  DAO  Start ");
			// Hoang Chu start on 6/6/2008
			sb.append(" 	SELECT	");
			sb.append(" 	app.ref_nbr	");
			sb.append(" 	,   app.app_dttm 	");
			sb.append(" 	,   com_cod.co_nm		");
			sb.append(" 	,   mac.fr_dttm  	");
			sb.append(" 	,   mac.mac_type 	");
			// NguyenQuyen start on 06/11/2008
			sb.append(" 	,   mac.misc_type_nm 	");
			// NguyenQuyen end on 06/11/2008
			// + " , mac_det.reg_nbr " //Sripriya 13/05/2011
			sb.append("  	, UPPER(REPLACE(mac_det.reg_nbr,' ','')) reg_nbr ");
			sb.append(" 	,   mac_det.lift_capacity	");
			sb.append(" 	,   mac_det.insurance_nbr	");
			sb.append(" 	,   mac_det.insurance_exp_dttm	");
			sb.append(" 	FROM MISC_APP app 	");
			// NguyenQuyen start on 06/11/2008
			sb.append(" 	INNER JOIN ");
			sb.append(" 	(select A.*,  B.misc_type_nm from MISC_MACHINE  A, misc_type_code B  ");
			sb.append(" 	where A.mac_type=B.misc_type_cd and B.cat_cd='MISC_MAC') mac ");
			sb.append(" 	ON app.MISC_SEQ_NBR = mac.MISC_SEQ_NBR	");
			// NguyenQuyen end on 06/11/2008
			sb.append(" 	INNER JOIN MISC_MACHINE_DET mac_det ON app.MISC_SEQ_NBR = mac_det.MISC_SEQ_NBR	");
			sb.append(" 	INNER JOIN COMPANY_CODE com_cod ON com_cod.CO_CD = app.CUST_CD ");
			sb.append(" 	WHERE mac_det.insurance_exp_dttm <= SYSDATE + ");
			sb.append(" 	NVL((SELECT TO_NUMBER(VALUE) FROM TEXT_PARA WHERE PARA_CD='STE_INS_EX'),31)	");
			sb.append(" 	AND (mac_det.phase_out_dt is null OR mac_det.phase_out_dt > sysdate) ");
			sb.append(" 	AND app.APP_STATUS in ('A','B')	");
			sb.append(" 	AND app.app_type = 'STE'	");
			sb.append(" 	ORDER BY com_cod.CO_NM,app.REF_NBR ");

			log.info(" ***getExpiredCompanyList SQL *****" + sb.toString());

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			// fetch entire list
			ExpiredCompanyValueObject ecVO;
			while (rs.next()) {
				ecVO = new ExpiredCompanyValueObject();

				ecVO.setAppRefNo(rs.getString(DBConstants.MISC_APP.REF_NBR));
				ecVO.setAppDate(rs.getTimestamp(DBConstants.MISC_APP.APP_DTTM));
				ecVO.setCompanyName(rs.getString(DBConstants.COMPANY_CODE.CO_NM));
				ecVO.setFromDate(rs.getTimestamp(DBConstants.MISC_MACHINE.FR_DTTM));
				ecVO.setInsuranceExpiryDate(rs.getTimestamp(DBConstants.MISC_MACHINE_DET.INSURANCE_EXP_DTTM));
				ecVO.setInsuranceNo(rs.getString(DBConstants.MISC_MACHINE_DET.INSURANCE_NBR));

				if (rs.getDouble(DBConstants.MISC_MACHINE_DET.LIFT_CAPACITY) == 0.00) {
					ecVO.setLiftCapacity(null);

				} else {
					ecVO.setLiftCapacity(Double.valueOf(rs.getDouble(DBConstants.MISC_MACHINE_DET.LIFT_CAPACITY)));
				}

				// ecVO.setMachineType(rs.getString(DBConstants.MISC_MACHINE.MAC_TYPE));
				// NguyenQuyen start on 06/11/2008
				ecVO.setMachineType(rs.getString(DBConstants.MISC_TYPE_CODE.MISC_TYPE_NM));
				// NguyenQuyen end on 06/11/2008
				ecVO.setMachineryRegNo(rs.getString(DBConstants.MISC_MACHINE_DET.REG_NBR));
				list.add(ecVO);
			}

			// return list;
		} catch (NullPointerException e) {
			log.info("exception: getExpiredCompanyList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getExpiredCompanyList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getExpiredCompanyList  DAO  END Result **** list: " + removeCommonInfo(list).toString());
		}
		return removeCommonInfo(list);
	} // Hoang Chu end on 6/6/2008

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getAllMailAccounts()
	// Hoang Chu start on 6/6/2008
	/**
	 * Get all email of FSS
	 * 
	 * @return list of FSS's emails
	 * @throws SQLException
	 */

	@Override
	public List<String> getAllMailAccounts() throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<String> result = new ArrayList<String>();

		try {
			log.info("START: getAllMailAccounts  DAO  Start");

			sb.append(" SELECT ACCOUNT FROM Exception_Alert WHERE ");
			sb.append(" ALERT_CODE = 'FER' AND DELIVERY_MODE = 'EML' ");
			sb.append(" AND REC_STATUS='A' AND NAME <> 'GBMisc' ");

			log.info(" ***getAllMailAccounts SQL *****" + sb.toString());

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			String toAddress = "";
			// fetch entire list
			while (rs.next()) {
				toAddress = rs.getString(DBConstants.MAIL.TO_ADDRESS);
				if (toAddress != null && !toAddress.trim().equals("")) {
					result.add(toAddress);
				}

			}
		} catch (NullPointerException e) {
			log.info("exception: getAllMailAccounts ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getAllMailAccounts ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAllMailAccounts  DAO  END Result **** results: " + result.toString());
		}
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getSenderAccount()
	/**
	 * Get sender email Address of FSS
	 * 
	 * @return list of FSS's emails
	 * @throws SQLException
	 */

	@Override
	public String getSenderAccount() throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String toAddress = "";

		try {
			log.info("START: getSenderAccount  DAO  Start");

			sb.append(" SELECT ACCOUNT FROM Exception_Alert WHERE ");
			sb.append(" ALERT_CODE = 'FER' AND DELIVERY_MODE = 'EML' ");
			sb.append(" AND REC_STATUS='A' AND NAME = 'GBMisc' ");

			log.info(" ***getSenderAccount SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			while (rs.next()) {
				toAddress = rs.getString(DBConstants.MAIL.TO_ADDRESS);
				if (toAddress == null) {
					toAddress = "";
					break;
				}
			}

			// return result;
		} catch (NullPointerException e) {
			log.info("exception: getSenderAccount ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getSenderAccount ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getSenderAccount  DAO  END Result ***** toAddress: " + toAddress);
		}
		return toAddress;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->approveSpaceApplication()
	// Added by Punitha on 19/11/2009

	@Override
	public void approveSpaceApplication(String userId, String appStatus, String appSeqNbr, String remarks,
			MiscSpaceValueObject obj, String approveDate) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();

		try {
			log.info("START: approveSpaceApplication  DAO  Start userId:" + CommonUtility.deNull(userId) + "appStatus:"
					+ CommonUtility.deNull(appStatus) + "appSeqNbr:" + CommonUtility.deNull(appSeqNbr) + "remarks:"
					+ CommonUtility.deNull(remarks) + "obj:" + obj.toString() + "approveDate:"
					+ CommonUtility.deNull(approveDate));
			sb.append(" update /* MiscAppEJB - approveSpaceApplication() */ ");
			sb.append(" misc_app set app_status =:appStatus, ");
			sb.append(" approve_dttm = to_date(:approveDate,'dd/mm/yyyy HH24mi'), ");
			sb.append(" approve_user_id =:userId, approve_remarks =:remarks, ");
			sb.append(" last_modify_user_id =:userId, last_modify_dttm = sysdate ");
			sb.append(" where misc_seq_nbr =:appSeqNbr ");

			sb1.append(" delete /* MiscAppEJB - approveSpaceApplication() */ from misc_space_det ");
			sb1.append(" where misc_seq_nbr =:appSeqNbr ");

			sb2.append(" insert into misc_space_det (misc_seq_nbr, item_nbr, ");
			sb2.append(" bay_nbr, area_use, ops_start_dttm, ops_end_dttm, ");
			sb2.append(" last_modify_user_id, last_modify_dttm) values ( ");
			sb2.append(" :appSeqNbr, :itemNbr, :bayNbr, :areaUsed, ");
			sb2.append(" to_date(:opsStartDttm,'dd/mm/yyyy HH24:mi'), ");
			sb2.append(" to_date(:opsEndDttm,'dd/mm/yyyy HH24:mi'), :userId,sysdate) ");

			log.info(" ***approveSpaceApplication SQL *****" + sb.toString());

			paramMap.put("appStatus", appStatus);
			paramMap.put("approveDate", approveDate);
			paramMap.put("userId", userId);
			paramMap.put("remarks", remarks);
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			// log.info("Updated MiscAppDetails ========> ");

			log.info(" ***approveSpaceApplication SQL2 *****" + sb1.toString());

			paramMap = new HashMap<String, Object>();
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb1.toString(), paramMap);
			// log.info("Deleted!!!");

			// log.info("Going to process Space Details========> ");
			String[] bayNbr = obj.getBayNbr();
			String[] areaUsed = obj.getAreaUsed();
			String[] opsStartDttm = obj.getOpsStartDttm();
			String[] opsEndDttm = obj.getOpsEndDttm();

			List<String> bayNbrList = null;
			List<String> areaUsedList = null;
			List<String> opsStartDttmList = null;
			List<String> opsEndDttmList = null;

			if (bayNbr != null) {
				bayNbrList = new ArrayList<String>();
				String[] tempBayNbrList = bayNbr[0].split(",");
				for (int i = 0; i < tempBayNbrList.length; i++) {
					bayNbrList.add(tempBayNbrList[i]);
				}
			}

			if (areaUsed != null) {
				areaUsedList = new ArrayList<String>();
				String[] tempAreaUsedList = areaUsed[0].split(",");
				for (int i = 0; i < tempAreaUsedList.length; i++) {
					areaUsedList.add(tempAreaUsedList[i]);
				}
			}

			if (opsStartDttm != null) {
				opsStartDttmList = new ArrayList<String>();
				String[] tempOpsStartDttmList = opsStartDttm[0].split(",");
				for (int i = 0; i < tempOpsStartDttmList.length; i++) {
					opsStartDttmList.add(tempOpsStartDttmList[i]);
				}
			}

			if (opsEndDttm != null) {
				opsEndDttmList = new ArrayList<String>();
				String[] tempOpsEndDttmList = opsEndDttm[0].split(",");
				for (int i = 0; i < tempOpsEndDttmList.length; i++) {
					opsEndDttmList.add(tempOpsEndDttmList[i]);
				}
			}

			if (bayNbr != null) {
				for (int i = 0, j = 1; i < bayNbrList.size(); i++, j++) {

					paramMap = new HashMap<String, Object>();
					log.info(" ***approveSpaceApplication SQL3 *****" + sb2.toString());

					paramMap.put("appSeqNbr", appSeqNbr);
					paramMap.put("itemNbr", j);
					paramMap.put("bayNbr", bayNbrList.get(i).toString());
					paramMap.put("areaUsed", areaUsedList.get(i).toString());
					paramMap.put("opsStartDttm", opsStartDttmList.get(i).toString());
					paramMap.put("opsEndDttm", opsEndDttmList.get(i).toString());
					paramMap.put("userId", userId);

					log.info(" *** paramMap: *****" + paramMap.toString());

					namedParameterJdbcTemplate.update(sb2.toString(), paramMap);
				}
			}

		} catch (NullPointerException e) {
			log.info("exception: approveSpaceApplication ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: approveSpaceApplication ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: approveSpaceApplication  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getMotCntrList()
	/**
	 * This method is to get list of container storage order
	 *
	 * @param cntrNo   - container number
	 * @param fromDate - creation from date
	 * @param toDate   - creation to date
	 * @param co       - company code of login user
	 *
	 * @return list of container
	 * @throws BusinessException
	 */

	// Cally CR-OPS-20100923-009 Use of Space & Storing Order

	@Override
	public List<StorageOrderValueObject> getMotCntrList(String refNo, String appSeqNbr) throws BusinessException {
		List<StorageOrderValueObject> cntrList = new ArrayList<StorageOrderValueObject>();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getMotCntrList  DAO  Start refNo:" + CommonUtility.deNull(refNo) + "appSeqNbr:"
					+ CommonUtility.deNull(appSeqNbr));

			// boolean relChk = chkOprAgeRelation( coCode, cntrOprCd);
			// boolean relChk = chkOprAgeRelation(coCode, cntrOprCd);
			// Get the container List based on cntrNo No and date time range
			// StorageOrderValueObject storageOrderValueObject = null;
			sb.append(" SELECT CNTR.CNTR_SEQ_NBR ");
			sb.append(", CNTR.CNTR_NBR ");
			sb.append(", CNTR.STATUS ");
			sb.append(", CNTR.ISO_SIZE_TYPE_CD ");
			sb.append(", CNTR.SIZE_FT ");
			sb.append(", CNTR.HT_FT ");
			sb.append(", CNTR.DECLR_WT ");
			sb.append(", CNTR.REFR_TEMP ");
			sb.append(", CNTR.IMDG_CL_CD ");
			sb.append(", CNTR.OOG_UNIT ");
			sb.append(", CNTR.OOG_OH ");
			sb.append(", CNTR.OOG_OL_FRONT ");
			sb.append(", CNTR.OOG_OL_BACK ");
			sb.append(", CNTR.OOG_OW_RIGHT ");
			sb.append(", CNTR.OOG_OW_LEFT ");
			sb.append(", CNTR.CAT_CD ");
			sb.append(", CNTR.IMP_HAUL_CD ");
			sb.append(", CNTR.EXP_HAUL_CD ");
			sb.append(", CNTR.MISC_APP_NBR ");
			sb.append(", CNTR.CREATE_DTTM ");
			sb.append(", OPR.OPR_STATE ");
			sb.append(", p_IMP.license_plate_nbr IMP_license_plate_nbr ");
			sb.append(", p_EXP.license_plate_nbr EXP_license_plate_nbr ");
			// sb.append(", p_IMP.pm_arrive_dttm Gate_out ");
			// sb.append(", p_EXP.pm_arrive_dttm Gate_in ");
			// format date time sripriya 13/05/2011
			sb.append(", TO_CHAR(p_IMP.pm_arrive_dttm,'DDMMYYYY HH24MI')  Gate_out ");
			sb.append(", TO_CHAR(p_EXP.pm_arrive_dttm,'DDMMYYYY HH24MI') Gate_in ");
			sb.append(", p_IMP.hdlg_ind IMP_hdlg_ind ");
			sb.append(", p_EXP.hdlg_ind EXP_hdlg_ind ");
			sb.append(
					" FROM CNTR, CNTR_OPERATION OPR , (SELECT * FROM pregate WHERE hdlg_ind = 'O' ) p_IMP, (SELECT * FROM pregate WHERE hdlg_ind = 'I' ) p_EXP ");
			sb.append("  WHERE CNTR.TXN_STATUS in ( 'A','I') ");
			sb.append("  AND CNTR.PURP_CD = 'ST' ");
			// sb.append(" AND CNTR.MISC_APP_NBR IS NOT NULL ");
			if (!"".equals(CommonUtility.deNull(refNo))) {
				sb.append(" AND CNTR.MISC_APP_NBR =:refNo ");
			} else if (!"".equals(CommonUtility.deNull(appSeqNbr))) {
				sb.append(" AND CNTR.MISC_APP_NBR = (SELECT ref_nbr FROM misc_app WHERE misc_seq_nbr =:appSeqNbr)");
			}
			sb.append(" AND CNTR.CNTR_SEQ_NBR = OPR.CNTR_SEQ_NBR ");
			sb.append(" AND CNTR.CNTR_SEQ_NBR = p_IMP.CNTR_SEQ_NBR (+) ");
			sb.append(" AND CNTR.CNTR_SEQ_NBR = p_EXP.CNTR_SEQ_NBR (+)  ");
			sb.append(" AND CNTR.IMP_HAUL_CD =p_IMP.HAULIER_CD (+) ");
			sb.append(" AND CNTR.EXP_HAUL_CD =p_EXP.HAULIER_CD (+) ");
			sb.append(" ORDER BY CNTR.CNTR_NBR ");

			log.info("=============== getMotCntrList() - refNo:" + refNo);
			log.info("=============== getMotCntrList() - appSeqNbr:" + appSeqNbr);
			log.info("=============== getMotCntrList() - sql:" + sb.toString());

			log.info(" ***getMotCntrList SQL *****" + sb.toString());

			if (!"".equals(CommonUtility.deNull(refNo))) {
				paramMap.put("refNo", refNo);
			} else if (!"".equals(CommonUtility.deNull(appSeqNbr))) {
				paramMap.put("appSeqNbr", appSeqNbr);
			}

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			int i = 0;
			String refrTemp = "";
			String imdgClass = "";
			String oogUnit = "";
			int oh = 0;
			int olFront = 0;
			int olBack = 0;
			int owRight = 0;
			int owLeft = 0;
			String height = "";
			String catCd = "";

			while (rs.next()) {
				StorageOrderValueObject storageOrderValueObject = new StorageOrderValueObject();
				i++;

				refrTemp = CommonUtility.deNull(rs.getString("REFR_TEMP"));
				imdgClass = CommonUtility.deNull(rs.getString("IMDG_CL_CD"));
				oogUnit = CommonUtility.deNull(rs.getString("OOG_UNIT"));
				if (rs.getString("OOG_OH") != null)
					oh = rs.getInt("OOG_OH");
				if (rs.getString("OOG_OL_FRONT") != null)
					olFront = rs.getInt("OOG_OL_FRONT");
				if (rs.getString("OOG_OL_BACK") != null)
					olBack = rs.getInt("OOG_OL_BACK");
				if (rs.getString("OOG_OW_RIGHT") != null)
					owRight = rs.getInt("OOG_OW_RIGHT");
				if (rs.getString("OOG_OW_LEFT") != null)
					owLeft = rs.getInt("OOG_OW_LEFT");
				height = CommonUtility.deNull(rs.getString("HT_FT"));
				// catCd = CommonUtility.deNull(rs.getString("CAT_CD"));

				storageOrderValueObject.setSeqNo(i + "");
				storageOrderValueObject.setCntrSeqNo(CommonUtility.deNull(rs.getString("CNTR_SEQ_NBR")));
				storageOrderValueObject.setCntrNo(CommonUtility.deNull(rs.getString("CNTR_NBR")));
				storageOrderValueObject.setStatus(CommonUtility.deNull(rs.getString("STATUS")));
				storageOrderValueObject.setIso(CommonUtility.deNull(rs.getString("ISO_SIZE_TYPE_CD")));
				storageOrderValueObject.setLength(CommonUtility.deNull(rs.getString("SIZE_FT")));
				storageOrderValueObject.setHeight(height);
				storageOrderValueObject.setWeight(CommonUtility.deNull(rs.getString("DECLR_WT")));
				storageOrderValueObject.setImpHaulier(CommonUtility.deNull(rs.getString("IMP_HAUL_CD")));
				storageOrderValueObject.setExpHaulier(CommonUtility.deNull(rs.getString("EXP_HAUL_CD")));
				// Cally CR-OPS-20100923-009 Use of Space & Storing Order 23 Sep 10
				storageOrderValueObject.setImpTruckNbr(CommonUtility.deNull(rs.getString("IMP_license_plate_nbr")));
				storageOrderValueObject.setExpTruckNbr(CommonUtility.deNull(rs.getString("EXP_license_plate_nbr")));
				storageOrderValueObject.setImpGateOut(CommonUtility.deNull(rs.getString("Gate_out")));
				storageOrderValueObject.setExpGateIn(CommonUtility.deNull(rs.getString("Gate_in")));
				storageOrderValueObject.setMiscAppNo(CommonUtility.deNull(rs.getString("MISC_APP_NBR")));
				String temp = CommonUtility.deNull(rs.getString("OPR_STATE"));
				if (temp.equals("")) {
					storageOrderValueObject.setArrvStat("");
				} else {
					char arrvStat = temp.charAt(0);
					switch (arrvStat) {
					case 'D':
						storageOrderValueObject.setArrvStat("Discharged");
						break;
					case 'A':
						storageOrderValueObject.setArrvStat("Arrived");
						break;
					case 'E':
						storageOrderValueObject.setArrvStat("Exit");
						break;
					case 'L':
						storageOrderValueObject.setArrvStat("Loaded");
						break;
					default:
						storageOrderValueObject.setArrvStat("");
						break;
					}
				}
				storageOrderValueObject.setDateCreate(
						CommonUtility.deNull(CommonUtility.parseDateToFmtStr(rs.getTimestamp("CREATE_DTTM"))));

				storageOrderValueObject.setRemarks(this.getCntrRemrks(refrTemp, imdgClass, oogUnit, oh, olFront, olBack,
						owRight, owLeft, height, catCd));
				cntrList.add(storageOrderValueObject);
			}
			log.info("END: getMotCntrList  DAO  END Result ****** cntrList: " + cntrList.toString());
			return cntrList;
		} catch (NullPointerException e) {
			log.info("exception: getMotCntrList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getMotCntrList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getMotCntrList  DAO  END");
		}
	} // Cally CR-OPS-20100923-009 Use of Space & Storing Order

	// jp.src.sg.com.ncs.common-->Container-->getCntrRemrks()

	/**
	 * Remarks for the container based on other container values
	 *
	 * @author Sudha Swedaranyam 12/12/2001
	 *
	 * @param String reeferTemp
	 * @param String imoClass
	 * @param String oogUnit
	 * @param int    oogHeight
	 * @param int    oogFront
	 * @param int    oogBack
	 * @param int    oogRight
	 * @param int    oogLeft
	 * @param String height
	 * @param String catCd
	 * @return String remarks
	 */

	public String getCntrRemrks(String reeferTemp, String imoClass, String oogUnit, int oogHeight, int oogFront,
			int oogBack, int oogRight, int oogLeft, String height, String catCd) {

		log.info("START: getCntrRemrks DAO **** reeferTemp: " + CommonUtility.deNull(reeferTemp) + ", imoClass: "
				+ CommonUtility.deNull(imoClass) + ", oogUnit: " + CommonUtility.deNull(oogUnit) + ", oogHeight: "
				+ oogHeight + ", oogFront: " + oogFront + ", oogBack: " + oogBack + ", oogRight: " + oogRight
				+ ", oogLeft: " + oogLeft + ", height: " + CommonUtility.deNull(height) + ", catCd: "
				+ CommonUtility.deNull(catCd));
		String remarks = "";
		String unit = "";
		int tempMea = -1;

		if (reeferTemp != null) {
			reeferTemp = reeferTemp.trim();
			int tempLen = reeferTemp.length();
			if (tempLen > 0) {
				remarks = reeferTemp.substring(0, tempLen - 1) + "&deg;" + reeferTemp.substring(tempLen - 1, tempLen);
				log.info("END: getCntrRemrks Result**** remarks: " + remarks);
				return remarks;
			}
		}
		if (imoClass != null) {
			if (imoClass.trim().length() > 0) {
				remarks = imoClass.trim();
				log.info("END: getCntrRemrks Result**** remarks: " + remarks);
				return remarks;
			}
		}
		// oog
		if (oogUnit != null)
			if (oogUnit.length() > 0) {
				if (oogUnit.equalsIgnoreCase("C"))
					unit = "cm";
				if (oogUnit.equalsIgnoreCase("I"))
					unit = "Inch";
			}
		if (unit.length() > 0) {
			if (oogHeight > 0) {
				remarks = "OH";
				tempMea = oogHeight;
			}
			if ((oogFront > 0) || (oogBack > 0)) {
				if (remarks.length() > 0)
					remarks = remarks + "/OL";
				else
					remarks = "OL";

				if ((oogFront > 0) && (oogBack == 0)) {
					tempMea = oogFront;
				}
				if ((oogFront == 0) && (oogBack > 0)) {
					tempMea = oogBack;
				}
			}
			if ((oogRight > 0) || (oogLeft > 0)) {
				if (remarks.length() > 0)
					remarks = remarks + "/OW";
				else
					remarks = "OW";

				if ((oogRight > 0) && (oogLeft == 0)) {
					tempMea = oogRight;
				}
				if ((oogRight == 0) && (oogLeft > 0)) {
					tempMea = oogLeft;
				}
			}
			if ((remarks.length() == 2) && (tempMea > 0))
				remarks = remarks + Integer.valueOf(tempMea).toString() + unit;
			if (remarks.length() > 0) {
				log.info("END: getCntrRemrks Result**** remarks: " + remarks);
				return remarks;
			}
		}
		// hi cube
		if (height != null)
			if ((!height.equals("")) && (height.equals("9'6") || height.equals(">8'6"))) {
				remarks = height;
				log.info("END: getCntrRemrks Result**** remarks: " + remarks);
				return remarks;
			}
		if (catCd != null)
			remarks = catCd;

		log.info("END: getCntrRemrks Result**** remarks: " + remarks);
		return remarks;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getMotCntrSummary()

	@Override
	public List<StorageOrderValueObject> getMotCntrSummary(String refNo, String appSeqNbr) throws BusinessException {
		List<StorageOrderValueObject> cntrSummary = new ArrayList<StorageOrderValueObject>();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getMotCntrSummary  DAO  Start refNo:" + CommonUtility.deNull(refNo) + "appSeqNbr:"
					+ CommonUtility.deNull(appSeqNbr));

			// boolean relChk = chkOprAgeRelation( coCode, cntrOprCd);
			// boolean relChk = chkOprAgeRelation(coCode, cntrOprCd);
			// Get the container List based on cntrNo No and date time range
			StorageOrderValueObject storageOrderValueObject = null;
			sb.append(" SELECT COUNT (*) total, size_ft ");
			sb.append(" FROM CNTR ");
			sb.append("  WHERE CNTR.TXN_STATUS in ( 'A','I')  ");
			sb.append("  AND CNTR.PURP_CD = 'ST' ");
			if (!"".equals(CommonUtility.deNull(refNo))) {
				sb.append(" AND CNTR.MISC_APP_NBR =:refNo ");
			} else if (!"".equals(CommonUtility.deNull(appSeqNbr))) {
				sb.append(" AND CNTR.MISC_APP_NBR = (SELECT ref_nbr FROM misc_app WHERE misc_seq_nbr =:appSeqNbr )");
			}
			sb.append(" GROUP BY size_ft ");
			log.info("=============== getMotCntrSummary() - refNo:" + refNo);
			log.info("=============== getMotCntrSummary() - appSeqNbr:" + appSeqNbr);

			log.info("=============== getMotCntrSummary() - sql:" + sb.toString());

			log.info(" ***getMotCntrSummary SQL *****" + sb.toString());

			if (!"".equals(CommonUtility.deNull(refNo))) {
				paramMap.put("refNo", refNo);
			} else if (!"".equals(CommonUtility.deNull(appSeqNbr))) {
				paramMap.put("appSeqNbr", appSeqNbr);
			}

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			while (rs.next()) {
				storageOrderValueObject = new StorageOrderValueObject();
				storageOrderValueObject.setTotal(CommonUtility.deNull(rs.getString("total")));
				storageOrderValueObject.setLength(CommonUtility.deNull(rs.getString("size_ft")));
				cntrSummary.add(storageOrderValueObject);
			}
			log.info("END: getMotCntrSummary  DAO  END Result ***** cntrSummary: " + cntrSummary.toString());
			return cntrSummary;
		} catch (NullPointerException e) {
			log.info("exception: getMotCntrSummary ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getMotCntrSummary ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getMotCntrSummary  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->addTpaDetails()
	// START 01-Mar-2011 - TPA ThangNC added ADD get trailer parking applications
	/**
	 * To save data for TRAILER PARKING APPLICATION
	 * 
	 * @throws BusinessException
	 */

	@Override
	public void addTpaDetails(String userId, String appType, String status, String cust, String account, String varcode,
			String fromDate, String toDate, String noHours, String applicationRemarks, String[] vehNo, String[] cntNo,
			String[] asnNo, String coName, String appDate, String conPerson, String conTel, String[] preferredArea,
			String[] remarks, String reasonForApplication, String cargoType) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();

		String miscSeqNbr = null;

		try {
			log.info("START: addTpaDetails  DAO  Start userId:" + userId + "appType:" + appType + "status:" + status
					+ "cust:" + cust + "account:" + account + "varcode:" + varcode + "fromDate:" + fromDate + "toDate:"
					+ toDate + "noHours:" + noHours + "applicationRemarks:" + applicationRemarks + "vehNo:" + vehNo
					+ "cntNo:" + cntNo + "asnNo:" + asnNo + "coName:" + coName + "appDate:" + appDate + "conPerson:"
					+ conPerson + "conTel:" + conTel + "preferredArea:" + preferredArea + "remarks:" + remarks
					+ "reasonForApplication:" + reasonForApplication + "cargoType:" + cargoType);

			sb.append(" insert into misc_vehicle(misc_seq_nbr, fr_dttm, to_dttm, ");
			sb.append(" nbr_night, park_reason, last_modify_user_id, last_modify_dttm, ");
			sb.append(" NBR_HOUR, PARK_REASON_CD, CARGO_TYPE) values ( ");
			sb.append(" :miscSeqNbr,to_date(:fromDate,'ddMMyyyy HH24MI'), ");
			sb.append(" to_date(:toDate,'ddMMyyyy HH24MI'), ");
			sb.append(" :nbrHr,:applicationRemarks,:userId, sysdate, ");
			sb.append(" :noHours, :reasonForApplication, :cargoType) ");

			sb1.append(" insert into misc_vehicle_det(misc_seq_nbr, item_nbr, ");
			sb1.append(" veh_chas_nbr, cntr_nbr, ");
			sb1.append(" asn_nbr, last_modify_user_id, last_modify_dttm, ");
			sb1.append(" REMARKS, PREF_AREA_CD) values ");
			sb1.append(" (:miscSeqNbr,:itemNbr, :vehNo,:cntNo,:asnNo,:userId, ");
			sb1.append(" sysdate,:remarks, :preferredArea) ");

			miscSeqNbr = insertMiscAppDetails(userId, appType, status, cust, account, varcode, appDate, conPerson,
					conTel);

			log.info(" ***addTpaDetails SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);
			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			// Number of night - default to 0
			paramMap.put("nbrHr", "0");
			paramMap.put("applicationRemarks", applicationRemarks);
			paramMap.put("userId", userId);
			paramMap.put("noHours", noHours);
			paramMap.put("reasonForApplication", reasonForApplication);
			paramMap.put("cargoType", cargoType);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			log.info("Going to process veh Details========> ");
			int j = 1;
			for (int i = 0; i < vehNo.length; i++) {

				log.info(" ***addTpaDetails SQL2 *****" + sb1.toString());
				paramMap = new HashMap<String, Object>();
				paramMap.put("miscSeqNbr", miscSeqNbr);
				paramMap.put("vehNo",
						(vehNo[i] != null && !vehNo[i].equals(""))
								? CommonUtility.getStringTokens(vehNo[i]).toUpperCase()
								: vehNo[i]);
				paramMap.put("cntNo", cntNo[i].toUpperCase());
				paramMap.put("asnNo", asnNo[i]);
				paramMap.put("userId", userId);
				paramMap.put("remarks", remarks[i]);
				paramMap.put("preferredArea", preferredArea[i]);
				if ((vehNo[i] != null && !vehNo[i].equals(""))) {
					paramMap.put("itemNbr", j);
					j++;
					log.info(" *** paramMap: *****" + paramMap.toString());
					namedParameterJdbcTemplate.update(sb1.toString(), paramMap);
				}

			}
			// Send Email & SMS
			if (status != null && status.equals("S")) {
				String alertCode = ConstantUtil.alertCode;
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = TrailerParkingAppSubmit_subject;
				String templateEmailFile = TemplateSubmit_body_template;
								
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Trailer Parking Application");
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);

				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * + "Type of Application: Trailer Parking Application \n" + "Reference No.: " +
				 * CommonUtility.deNull(refNbr) + " \n" + "Application Date/Time: " +
				 * CommonUtility.deNull(appDttm) + " \n" + "Company: " + coName + "\n";
				 */
				
				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Trailer Parking Application, " + "Ref. No.: "
						+ CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm) + " , by " + coName;
				sendSMS(alertCode, sms);
			}
		} catch (BusinessException e) {
			log.info("exception: addTpaDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: addTpaDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: addTpaDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: addTpaDetails  DAO  END");
		}
	} // END 01-Mar-2011 - TPA ThangNC added to ADD trailer parking applications

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getTrailerParkingApplicationDetails()

	@Override
	public List<Object> getTrailerParkingApplicationDetails(String userId, String applyType, String appSeqNbr,
			String applyTypeNm) throws BusinessException {
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		List<Object> result = new ArrayList<Object>();
		try {
			log.info("START: getTrailerParkingApplicationDetails  DAO  Start userId:" + CommonUtility.deNull(userId)
					+ "applyType:" + CommonUtility.deNull(applyType) + "appSeqNbr:" + CommonUtility.deNull(appSeqNbr)
					+ "applyTypeNm:" + CommonUtility.deNull(applyTypeNm));

			sb.append(" select  ");
			sb.append(" misc_seq_nbr, ");
			sb.append(" to_char(fr_dttm,'ddmmyyyy') frDttm, to_char(fr_dttm,'HH24mi') frTime, ");
			sb.append(" to_char(to_dttm, 'ddmmyyyy') toDttm, to_char(to_dttm, 'HH24mi') toTime, ");
			sb.append(" nbr_hour, park_reason, ");
			sb.append(" to_char(actual_fr_dttm,'ddmmyyyy') actualFrDttm, ");
			sb.append(" to_char(actual_fr_dttm,'HH24mi') actualFrTime, ");
			sb.append(" to_char(actual_to_dttm, 'ddmmyyyy') actualToDttm, ");
			sb.append(" to_char(actual_to_dttm, 'HH24mi') actualToTime, ");
			sb.append(" ACTUAL_NBR_HOUR, mv.last_modify_user_id, mv.last_modify_dttm, ");
			sb.append(" CARGO_TYPE, mtc.misc_type_nm ");
			sb.append(" from misc_vehicle mv left join misc_type_code mtc ");
			sb.append(" on mtc.misc_type_cd = PARK_REASON_CD where misc_seq_nbr =:appSeqNbr ");

			sb1.append(" select ");
			sb1.append(" misc_seq_nbr, ");
			sb1.append(" item_nbr, veh_chas_nbr, cntr_nbr, asn_nbr, REMARKS, PREF_AREA_CD, ");
			sb1.append(" AREA_CD, SLOT_NBR, ");
			sb1.append(" last_modify_user_id, last_modify_dttm ");
			sb1.append(" from misc_vehicle_det where misc_seq_nbr =:appSeqNbr ORDER BY item_nbr ");

			List<Object> temp = (ArrayList<Object>) getMiscAppDetails(userId, applyType, appSeqNbr, applyTypeNm);
			if (temp != null && temp.size() > 0) {
				MiscAppValueObject miscAppValueObject = (MiscAppValueObject) temp.get(0);
				if ((miscAppValueObject.getApproveBy() == null
						|| miscAppValueObject.getApproveBy().equalsIgnoreCase(""))
						&& (miscAppValueObject.getApproveDttm() != null
								&& miscAppValueObject.getApproveDttm().trim().length() > 0)) {
					miscAppValueObject.setApproveBy("AUTO");
				}
				result.add(miscAppValueObject);// MiscAppValueObject
				result.add(temp.get(1));// MiscCustValueObject
			}
			MiscVehValueObject tpa = new MiscVehValueObject();

			log.info(" ***getTrailerParkingApplicationDetails SQL *****" + sb.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				tpa.setFromDate(CommonUtility.deNull(rs.getString("frDttm")));
				tpa.setFromTime(CommonUtility.deNull(rs.getString("frTime")));
				tpa.setToDate(CommonUtility.deNull(rs.getString("toDttm")));
				tpa.setToTime(CommonUtility.deNull(rs.getString("toTime")));
				tpa.setNoHours(Double.toString(rs.getDouble("nbr_hour")));
				tpa.setParkReason(CommonUtility.deNull(rs.getString("park_reason")));
				tpa.setApplicationReason(CommonUtility.deNull(rs.getString("misc_type_nm")));
				tpa.setActFromDate(CommonUtility.deNull(rs.getString("actualFrDttm")));
				tpa.setActFromTime(CommonUtility.deNull(rs.getString("actualFrTime")));
				tpa.setActToDate(CommonUtility.deNull(rs.getString("actualToDttm")));
				tpa.setActToTime(CommonUtility.deNull(rs.getString("actualToTime")));
				tpa.setActNoHours(Double.toString(rs.getDouble("ACTUAL_NBR_HOUR")));
				tpa.setCargoType(CommonUtility.deNull(rs.getString("CARGO_TYPE")));
			}

			// log.info("Going to get veh Details========> ");
			List<String> vehChasNbr = new ArrayList<String>();
			List<String> cntrNbr = new ArrayList<String>();
			List<String> asnNbr = new ArrayList<String>();
			List<String> remarks = new ArrayList<String>();
			List<String> preArea = new ArrayList<String>();
			List<String> area = new ArrayList<String>();
			List<String> slot = new ArrayList<String>();

			log.info(" ***getTrailerParkingApplicationDetails SQL2 *****" + sb1.toString());
			paramMap = new HashMap<String, Object>();
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs1 = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
			while (rs1.next()) {
				vehChasNbr.add(CommonUtility.deNull(rs1.getString("veh_chas_nbr")));
				cntrNbr.add(CommonUtility.deNull(rs1.getString("cntr_nbr")));
				asnNbr.add(CommonUtility.deNull(rs1.getString("asn_nbr")));
				remarks.add(CommonUtility.deNull(rs1.getString("REMARKS")));
				preArea.add(CommonUtility.deNull(rs1.getString("PREF_AREA_CD")));
				area.add(CommonUtility.deNull(rs1.getString("AREA_CD")));
				slot.add(CommonUtility.deNull(rs1.getString("SLOT_NBR")));
			}
			tpa.setVehChasNbr((String[]) vehChasNbr.toArray(new String[0]));
			tpa.setCntrNbr((String[]) cntrNbr.toArray(new String[0]));
			tpa.setAsnNbr((String[]) asnNbr.toArray(new String[0]));
			tpa.setRemarks((String[]) remarks.toArray(new String[0]));
			tpa.setPreferredArea((String[]) preArea.toArray(new String[0]));
			tpa.setArea((String[]) area.toArray(new String[0]));
			tpa.setSlot((String[]) slot.toArray(new String[0]));
			result.add(tpa);
		} catch (BusinessException e) {
			log.info("exception: getTrailerParkingApplicationDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getTrailerParkingApplicationDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getTrailerParkingApplicationDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTrailerParkingApplicationDetails  DAO  END Result ****** result: " + result.toString());
		}
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getParkingReasonList()
	/**
	 * This method is to get a list of reason for application.
	 * 
	 * @return list of reason
	 * @throws BusinessException
	 */

	// Added by TungNQ1 for Trailer parking

	@Override
	public List<Map<String, Object>> getParkingReasonList() throws BusinessException {
		List<Map<String, Object>> parkingReasonList = new ArrayList<Map<String, Object>>();
		String catCdParam = "TPA_PKRSN";
		HashMap<String, Object> reasonValues = new LinkedHashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getParkingReasonList  DAO  Start ");

			sb.append(" SELECT * FROM MISC_TYPE_CODE WHERE CAT_CD = :catCdParam ");
			sb.append(" AND rec_status= 'A'  ORDER BY MISC_TYPE_NM ASC "); // TPA_PKRSN

			log.info(" ***getParkingReasonList SQL *****" + sb.toString());

			paramMap.put("catCdParam", catCdParam);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				reasonValues.put(rs.getString("MISC_TYPE_CD"), rs.getString("MISC_TYPE_NM"));
			}
			parkingReasonList.add(reasonValues);

			log.info("END: getParkingReasonList  DAO  END Result ***** parkingReasonList: "
					+ parkingReasonList.toString());

			return parkingReasonList;
		} catch (NullPointerException e) {
			log.info("exception: getParkingReasonList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getParkingReasonList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getParkingReasonList  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getParkingAreaList()
	// START 02-Mar-2011 - TPA - ThangNC added to get list of available parking area
	// slot
	/**
	 * To get parking area list in this format AREA(NO_SLOT): ML-A(3)
	 */

	@Override
	public List<MiscAppParkingAreaObject> getParkingAreaList(String slotType, String startDate, String toDate)
			throws BusinessException {
		List<MiscAppParkingAreaObject> parkingAreaList = new ArrayList<MiscAppParkingAreaObject>();
		// --1. FR_DDTM between selected from date and selected to date "
		// --2. TO_DDTM between selected from date and selected to date "
		// --3. FR_DTTM <= selected from date AND TO_DTTM > selected to date "
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getParkingAreaList  DAO  Start slotType:" + CommonUtility.deNull(slotType) + "startDate:"
					+ CommonUtility.deNull(startDate) + "toDate:" + CommonUtility.deNull(toDate));

			sb.append(" SELECT AREA_CD, COUNT(*) AS NO_SLOT FROM MISC_PARKING_SLOT ");
			sb.append(" WHERE SLOT_STATUS='OPN' AND SLOT_TYPE =:slotType AND (AREA_CD, SLOT_NBR) NOT IN ");
			sb.append(" ( SELECT MPS.AREA_CD, MPS.SLOT_NBR FROM MISC_PARKING_SLOT MPS ");
			sb.append(" INNER JOIN MISC_VEHICLE_DET MVD ");
			sb.append(" ON MPS.AREA_CD = MVD.AREA_CD AND MPS.SLOT_NBR = MVD.SLOT_NBR ");
			sb.append(" INNER JOIN MISC_VEHICLE MV ON MV.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
			sb.append(" INNER JOIN MISC_APP MA ON MV.MISC_SEQ_NBR = MA.MISC_SEQ_NBR ");
			sb.append("  WHERE SLOT_STATUS='OPN' AND SLOT_TYPE =:slotType ");
			sb.append(" AND MVD.AREA_CD IS NOT NULL AND MVD.SLOT_NBR IS NOT NULL  ");
			sb.append(" AND (MA.app_status='A' or MA.app_status='C') ");
			sb.append(" AND ( ");
			// sb.append(" (FR_DTTM BETWEEN TO_DATE(?, 'ddMMyyyy HH24MI') and TO_DATE(?,
			// 'ddMMyyyy HH24MI') ) "
			// sb.append(" OR (TO_DTTM BETWEEN TO_DATE(?, 'ddMMyyyy HH24MI') and TO_DATE(?,
			// 'ddMMyyyy HH24MI') ) "
			// sb.append(" OR (FR_DTTM <= TO_DATE(?, 'ddMMyyyy HH24MI') and TO_DTTM >=
			// TO_DATE(?, 'ddMMyyyy HH24MI') ) "
			// sb.append(" OR (FR_DTTM > TO_DATE(?, 'ddMMyyyy HH24MI') and TO_DTTM >=
			// TO_DATE(?, 'ddMMyyyy HH24MI') ) "
			sb.append("        (TO_DATE(:startDate, 'ddMMyyyy HH24MI') > ");
			sb.append(" NVL2(ACTUAL_FR_DTTM,ACTUAL_FR_DTTM,FR_DTTM) and ");
			sb.append(" TO_DATE(:startDate, 'ddMMyyyy HH24MI') < ");
			sb.append(" NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM) ) ");
			sb.append("       OR (TO_DATE(:toDate, 'ddMMyyyy HH24MI') > ");
			sb.append(" NVL2(ACTUAL_FR_DTTM,ACTUAL_FR_DTTM,FR_DTTM) and ");
			sb.append(" TO_DATE(:toDate, 'ddMMyyyy HH24MI') < ");
			sb.append(" NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM) ) ");
			sb.append("        OR (TO_DATE(:startDate, 'ddMMyyyy HH24MI') <= ");
			sb.append(" NVL2(ACTUAL_FR_DTTM,ACTUAL_FR_DTTM,FR_DTTM)  and ");
			sb.append(" TO_DATE(:toDate, 'ddMMyyyy HH24MI') >= ");
			sb.append(" NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM)) ");
			sb.append("    ) ");
			sb.append(" ) ");

			sb.append(" GROUP BY AREA_CD ORDER BY AREA_CD ");

			log.info(" ***getParkingAreaList SQL *****" + sb.toString());

			paramMap.put("slotType", slotType);
			paramMap.put("startDate", startDate);
			paramMap.put("toDate", toDate);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				MiscAppParkingAreaObject miscAppParkingAreaObject = new MiscAppParkingAreaObject();
				miscAppParkingAreaObject.setAreaCode(rs.getString("AREA_CD"));
				miscAppParkingAreaObject.setNoOfSlot(rs.getString("NO_SLOT"));
				parkingAreaList.add(miscAppParkingAreaObject);
			}

			log.info("END: getParkingAreaList  DAO  END Result ****** parkingAreaList: " + parkingAreaList.toString());

			return parkingAreaList;

		} catch (NullPointerException e) {
			log.info("exception: getParkingAreaList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getParkingAreaList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getParkingAreaList  DAO  END");
		}
	}

	// END 02-Mar-2011 - TPA - ThangNC added to get list of available parking area
	// slot

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getTpaForApproveDetails()
	// START 02-Mar-2011 - TPA - Thanhnv2 added to get list of Trailer Parking
	// Application for Approve.
	/**
	 * To get get list of Trailer Parking Application for Approve
	 */

	@Override
	public List<Object> getTpaForApproveDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
			throws BusinessException {
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		SqlRowSet rs3 = null;
		SqlRowSet rs4 = null;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		StringBuilder sb3 = new StringBuilder();
		StringBuilder sb4 = new StringBuilder();
		List<Object> result = new ArrayList<Object>();

		try {
			log.info("START: getTpaForApproveDetails  DAO  Start userId:" + CommonUtility.deNull(userId) + "applyType:"
					+ CommonUtility.deNull(applyType) + "appSeqNbr:" + CommonUtility.deNull(appSeqNbr) + "applyTypeNm:"
					+ CommonUtility.deNull(applyTypeNm));

			sb.append(" SELECT /* MiscAppEJB - getTpaForApproveDetails(vehSql) */ mv.misc_seq_nbr, ");
			sb.append(" to_char(mv.fr_dttm,'ddmmyyyy') frDttm, to_char(mv.fr_dttm,'HH24mi') frTime, ");
			sb.append(" to_char(mv.to_dttm, 'ddmmyyyy') toDttm, to_char(mv.to_dttm, 'HH24mi') toTime, ");
			sb.append(" mv.nbr_hour, mv.park_reason, ");
			sb.append(" to_char(mv.actual_fr_dttm,'ddmmyyyy') actualFrDttm, ");
			sb.append(" to_char(mv.actual_to_dttm, 'ddmmyyyy') actualToDttm, ");
			sb.append(" mv.actual_nbr_hour, mv.last_modify_user_id, mv.last_modify_dttm, mv.cargo_type, ");
			sb.append(" mtc.misc_type_nm FROM misc_vehicle mv ");
			sb.append(" LEFT JOIN misc_type_code mtc ON mv.park_reason_cd = mtc.misc_type_cd ");
			sb.append(" WHERE mv.misc_seq_nbr =:appSeqNbr ");

			sb1.append(" SELECT /* MiscAppEJB - getTpaForApproveDetails(vehDetSql) */ ");
			sb1.append(" mvd.misc_seq_nbr, mvd.item_nbr, mvd.veh_chas_nbr, mvd.cntr_nbr, mvd.asn_nbr, ");
			sb1.append(" mvd.area_cd, mvd.slot_nbr, ");
			sb1.append(" mvd.last_modify_user_id, mvd.last_modify_dttm, mvd.pref_area_cd, mvd.remarks ");
			sb1.append(" FROM misc_vehicle_det mvd WHERE mvd.misc_seq_nbr =:appSeqNbr ");
			sb1.append(" ORDER BY mvd.item_nbr ");

			sb2.append(" SELECT (CNTR_STORAGE_CD||'/'||JP_GROUP||'/'||IMO_CL) CNTR_DG_DESC FROM ");
			sb2.append(" (SELECT CNTR_STORAGE_CD, JP_GROUP, IMO_CL FROM PM4 WHERE CNTR_SEQ_NBR = ");
			sb2.append(" (SELECT CNTR_SEQ_NBR FROM CNTR WHERE CNTR_NBR=:cntrNbr AND TXN_STATUS='A') ");
			sb2.append(" HAVING CNTR_TEU = ( SELECT MAX(CNTR_TEU) FROM PM4 B WHERE B.CNTR_SEQ_NBR = ");
			sb2.append(" (SELECT CNTR_SEQ_NBR FROM CNTR WHERE CNTR_NBR=:cntrNbr AND TXN_STATUS='A' ");
			sb2.append(" ) AND RECORD_TYPE<>'D' ) AND MPA_APPV_STATUS = 'A' AND JP_APPV_STATUS = 'A' ");
			sb2.append(" AND RECORD_TYPE <> 'D' GROUP BY CNTR_STORAGE_CD, JP_GROUP, CNTR_GROUP, ");
			sb2.append(" IMO_CL, CNTR_TEU, CNTR_SEQ_NBR, MPA_APPV_STATUS, JP_APPV_STATUS, ");
			sb2.append(" RECORD_TYPE, OPR_TYPE ) ");

			sb3.append(" SELECT (DG_STORAGE_CD||'/'||JP_GROUP||'/'||IMO_CL) CARGO_DG_DESC FROM PM4 ");
			sb3.append(" WHERE( BL_NBR= ( SELECT BL_NBR FROM GB_EDO WHERE EDO_ASN_NBR =:asnNbr ");
			sb3.append(" ) OR UCR_NBR= ( SELECT BK_REF_NBR FROM ESN ");
			sb3.append(" WHERE ESN_ASN_NBR =:asnNbr)) AND MPA_APPV_STATUS = 'A' ");
			sb3.append(" AND JP_APPV_STATUS = 'A' AND RECORD_TYPE <> 'D' ");

			sb4.append(" SELECT NVL2(OOG_UNIT, (DECODE(OOG_UNIT,'C','CM',OOG_UNIT)||'/'|| OOG_OH||'/'|| ");
			sb4.append(" OOG_OL_FRONT||'/'||OOG_OL_BACK||'/'|| OOG_OW_RIGHT||'/'|| ");
			sb4.append(" OOG_OW_LEFT), '') OOG_DESC ");
			sb4.append(" FROM CNTR WHERE CNTR_NBR =:cntrNbr AND TXN_STATUS = 'A' ");

			List<Object> temp = (ArrayList<Object>) getMiscAppDetails(userId, applyType, appSeqNbr, applyTypeNm);
			if (temp != null && temp.size() > 0) {
				result.add(temp.get(0));// MiscAppValueObject
				result.add(temp.get(1));// MiscCustValueObject
			}
			MiscVehValueObject veh = new MiscVehValueObject();

			log.info(" ***getTpaForApproveDetails SQL *****" + sb.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				veh.setFromDate(CommonUtility.deNull(rs.getString("frDttm")));
				veh.setFromTime(CommonUtility.deNull(rs.getString("frTime")));
				veh.setToDate(CommonUtility.deNull(rs.getString("toDttm")));
				veh.setToTime(CommonUtility.deNull(rs.getString("toTime")));
				veh.setNoHours(Double.toString(rs.getDouble("nbr_hour")));
				veh.setParkReason(CommonUtility.deNull(rs.getString("park_reason")));
				veh.setApplicationReason(CommonUtility.deNull(rs.getString("misc_type_nm")));
				veh.setActFromDate(CommonUtility.deNull(rs.getString("actualFrDttm")));
				veh.setActToDate(CommonUtility.deNull(rs.getString("actualToDttm")));
				veh.setActNoHours(CommonUtility.deNull(rs.getString("actual_nbr_hour")));
				veh.setCargoType(CommonUtility.deNull(rs.getString("cargo_type")));
			}

			// log.info("Going to get veh Details========> ");
			List<String> vehChasNbr = new ArrayList<String>();
			List<String> cntrNbr = new ArrayList<String>();
			List<String> asnNbr = new ArrayList<String>();
			List<String> prefAreaCd = new ArrayList<String>();
			List<String> remarks = new ArrayList<String>();
			List<String> dgInfo = new ArrayList<String>();
			List<String> oogInfo = new ArrayList<String>();
			List<String> area = new ArrayList<String>();
			List<String> slot = new ArrayList<String>();

			log.info(" ***getTpaForApproveDetails SQL2 *****" + sb1.toString());
			paramMap = new HashMap<String, Object>();
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs1 = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);
			int count = 0;
			while (rs1.next()) {
				vehChasNbr.add(CommonUtility.deNull(rs1.getString("veh_chas_nbr")));
				cntrNbr.add(CommonUtility.deNull(rs1.getString("cntr_nbr")));
				asnNbr.add(CommonUtility.deNull(rs1.getString("asn_nbr")));
				prefAreaCd.add(CommonUtility.deNull(rs1.getString("pref_area_cd")));
				remarks.add(CommonUtility.deNull(rs1.getString("remarks")));
				area.add(CommonUtility.deNull(rs1.getString("area_cd")));
				slot.add(CommonUtility.deNull(rs1.getString("slot_nbr")));
				count++;
			}
			veh.setVehChasNbr((String[]) vehChasNbr.toArray(new String[0]));
			veh.setCntrNbr((String[]) cntrNbr.toArray(new String[0]));
			veh.setAsnNbr((String[]) asnNbr.toArray(new String[0]));
			veh.setPreferredArea((String[]) prefAreaCd.toArray(new String[0]));
			veh.setRemarks((String[]) remarks.toArray(new String[0]));
			veh.setArea((String[]) area.toArray(new String[0]));
			veh.setSlot((String[]) slot.toArray(new String[0]));

			String dg = null;
			String oog = null;
			for (int i = 0; i < count; i++) {
				dg = "";
				oog = "";
				if ("D".equals(veh.getCargoType())) {
					if (cntrNbr.get(i) != null && !"".equals(cntrNbr.get(i))) {
						log.info(" ***getTpaForApproveDetails SQL3 *****" + sb2.toString());
						paramMap = new HashMap<String, Object>();
						paramMap.put("cntrNbr", (String) cntrNbr.get(i));

						log.info(" *** paramMap: *****" + paramMap.toString());

						rs2 = namedParameterJdbcTemplate.queryForRowSet(sb2.toString(), paramMap);
						while (rs2.next()) {
							dg = CommonUtility.deNull(rs2.getString("CNTR_DG_DESC"));
						}
						dgInfo.add(dg);
						oogInfo.add("");

					} else if (asnNbr.get(i) != null && !"".equals(asnNbr.get(i))
							&& NumberUtils.isNumber((String) asnNbr.get(i))) {
						paramMap = new HashMap<String, Object>();
						log.info(" ***getTpaForApproveDetails SQL4 *****" + sb3.toString());

						paramMap.put("asnNbr", (String) asnNbr.get(i));

						log.info(" *** paramMap: *****" + paramMap.toString());

						rs3 = namedParameterJdbcTemplate.queryForRowSet(sb3.toString(), paramMap);
						while (rs3.next()) {
							dg = CommonUtility.deNull(rs3.getString("CARGO_DG_DESC"));
						}
						dgInfo.add(dg);
						oogInfo.add("");

					} else {
						oogInfo.add("");
						dgInfo.add("");
					}
				} else if ("O".equals(veh.getCargoType())) {
					log.info(" ***getTpaForApproveDetails SQL5 *****" + sb4.toString());
					paramMap = new HashMap<String, Object>();
					paramMap.put("cntrNbr", (String) cntrNbr.get(i));

					log.info(" *** paramMap: *****" + paramMap.toString());

					rs4 = namedParameterJdbcTemplate.queryForRowSet(sb4.toString(), paramMap);
					while (rs4.next()) {
						oog = CommonUtility.deNull(rs4.getString("OOG_DESC"));
					}
					oogInfo.add(oog);
					dgInfo.add("");
				} else {
					oogInfo.add("");
					dgInfo.add("");
				}
			}
			veh.setDgInfo((String[]) dgInfo.toArray(new String[0]));
			veh.setOogInfo((String[]) oogInfo.toArray(new String[0]));
			result.add(veh);

			log.info("END: *** getTpaForApproveDetails Result *****" + result.toString());
		} catch (BusinessException e) {
			log.info("exception: getTpaForApproveDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getTpaForApproveDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getTpaForApproveDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTpaForApproveDetails  DAO  END");
		}
		return result;
	}

	// END 02-Mar-2011 - TPA - Thanhnv2 added to get list of Trailer Parking
	// Application for Approve.

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getParkingAreaSlotAvailableList()

	@Override
	public List<MiscAppParkingAreaObject> getParkingAreaSlotAvailableList(String areaCd, String slotType,
			String startDate, String toDate) throws BusinessException {
		List<MiscAppParkingAreaObject> parkingSlotList = new ArrayList<MiscAppParkingAreaObject>();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getParkingAreaSlotAvailableList  DAO  Start areaCd:" + CommonUtility.deNull(areaCd)
					+ "slotType:" + CommonUtility.deNull(slotType) + "startDate:" + CommonUtility.deNull(startDate)
					+ "toDate:" + CommonUtility.deNull(toDate));

			sb.append(" SELECT AREA_CD, SLOT_NBR, SLOT_TYPE, SLOT_STATUS FROM MISC_PARKING_SLOT ");
			sb.append(" WHERE SLOT_STATUS='OPN' AND SLOT_TYPE =:slotType AND AREA_CD ");
			sb.append(" =:areaCd AND (AREA_CD, SLOT_NBR) NOT IN ");
			sb.append(" ( SELECT MPS.AREA_CD, MPS.SLOT_NBR FROM MISC_PARKING_SLOT MPS ");
			sb.append(" INNER JOIN MISC_VEHICLE_DET MVD ");
			sb.append(" ON MPS.AREA_CD = MVD.AREA_CD AND MPS.SLOT_NBR = MVD.SLOT_NBR ");
			sb.append(" INNER JOIN MISC_VEHICLE MV ON MV.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
			sb.append(" INNER JOIN MISC_APP MA ON MV.MISC_SEQ_NBR = MA.MISC_SEQ_NBR ");
			sb.append(" WHERE SLOT_STATUS='OPN' AND SLOT_TYPE = :slotType ");
			sb.append("  AND MVD.AREA_CD IS NOT NULL AND MVD.SLOT_NBR IS NOT NULL  ");
			sb.append(" AND (MA.app_status='A' or MA.app_status='C') AND ( ");
			sb.append(" (TO_DATE(:startDate, 'ddMMyyyy HH24MI') > ");
			sb.append(" NVL2(ACTUAL_FR_DTTM,ACTUAL_FR_DTTM,FR_DTTM) and ");
			sb.append(" TO_DATE(:startDate, 'ddMMyyyy HH24MI') < ");
			sb.append(" NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM) ) ");
			sb.append(" OR (TO_DATE(:toDate, 'ddMMyyyy HH24MI') > ");
			sb.append(" NVL2(ACTUAL_FR_DTTM,ACTUAL_FR_DTTM,FR_DTTM) and ");
			sb.append(" TO_DATE(:toDate, 'ddMMyyyy HH24MI') < ");
			sb.append(" NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM) ) ");
			sb.append(" OR (TO_DATE(:startDate, 'ddMMyyyy HH24MI') <= ");
			sb.append(" NVL2(ACTUAL_FR_DTTM,ACTUAL_FR_DTTM,FR_DTTM)  and ");
			sb.append(" TO_DATE(:toDate, 'ddMMyyyy HH24MI') >=NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM)) ");
			sb.append("  ) ) ORDER BY AREA_CD, SLOT_NBR ");

			log.info(" ***getParkingAreaSlotAvailableList SQL *****" + sb.toString());

			paramMap.put("slotType", slotType);
			paramMap.put("areaCd", areaCd);
			paramMap.put("startDate", startDate);
			paramMap.put("toDate", toDate);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			while (rs.next()) {
				MiscAppParkingAreaObject miscAppParkingAreaObject = new MiscAppParkingAreaObject();
				miscAppParkingAreaObject.setAreaCode(rs.getString("AREA_CD"));
				miscAppParkingAreaObject.setSlotNumber(rs.getString("SLOT_NBR"));
				miscAppParkingAreaObject.setSlotStatus(rs.getString("SLOT_TYPE"));
				miscAppParkingAreaObject.setSlotType(rs.getString("SLOT_STATUS"));

				parkingSlotList.add(miscAppParkingAreaObject);
			}
			log.info("END: *** getParkingAreaSlotAvailableList Result ***** Size: " + parkingSlotList.size());
			return parkingSlotList;

		} catch (NullPointerException e) {
			log.info("exception: getParkingAreaSlotAvailableList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getParkingAreaSlotAvailableList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getParkingAreaSlotAvailableList  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->approveTpaApplication()

	@Override
	public void approveTpaApplication(String[] areaCd, String[] slotCd, String userId, String appStatus,
			String appSeqNbr, String remarks, String approveDate) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();

		try {
			log.info("START: approveTpaApplication  DAO  Start areaCd:" + areaCd.toString() + "slotCd:"
					+ slotCd.toString() + "userId:" + CommonUtility.deNull(userId) + "appStatus:"
					+ CommonUtility.deNull(appStatus) + "appSeqNbr:" + CommonUtility.deNull(appSeqNbr) + "remarks:"
					+ CommonUtility.deNull(remarks) + "approveDate:" + CommonUtility.deNull(approveDate));

			sb.append(" update /* MiscAppEJB - approveTpaApplication() */ misc_app ");
			sb.append(" set app_status =:appStatus, approve_dttm = to_date(:approveDate,'dd/mm/yyyy HH24mi'), ");
			sb.append(" approve_user_id =:userId, approve_remarks = :remarks ,");
			sb.append(" last_modify_user_id =:userId, last_modify_dttm = sysdate ");
			sb.append(" where misc_seq_nbr =:appSeqNbr ");

			sb1.append(" UPDATE misc_vehicle_det mvd SET ");
			sb1.append(" mvd.area_cd = :areaCd , ");
			sb1.append(" mvd.slot_nbr = :slotCd, ");
			sb1.append(" mvd.last_modify_dttm = sysdate, ");
			sb1.append(" mvd.last_modify_user_id =:userId ");
			sb1.append(" WHERE mvd.misc_seq_nbr =:appSeqNbr ");
			sb1.append(" AND mvd.item_nbr = :itemNbr ");

			log.info(" ***approveTpaApplication SQL *****" + sb.toString());

			paramMap.put("appStatus", appStatus);
			paramMap.put("approveDate", approveDate);
			paramMap.put("userId", userId);
			paramMap.put("remarks", remarks);
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			if (areaCd != null && slotCd != null) {
				for (int i = 0, j = 1; i < areaCd.length; i++, j++) {
					paramMap = new HashMap<String, Object>();

					log.info(" ***approveTpaApplication SQL2 *****" + sb1.toString());
					paramMap = new HashMap<String, Object>();
					paramMap.put("areaCd", areaCd[i]);
					paramMap.put("slotCd", slotCd[i]);
					paramMap.put("itemNbr", j);
					paramMap.put("userId", userId);
					paramMap.put("appSeqNbr", appSeqNbr);

					log.info(" *** paramMap: *****" + paramMap.toString());
					if (areaCd[i] != null && !areaCd[i].equals("") && slotCd[i] != null && !slotCd[i].equals("")) {
						namedParameterJdbcTemplate.update(sb1.toString(), paramMap);
					}
				}
			}

			log.info("END: *** approveTpaApplication Result *****");
		} catch (NullPointerException e) {
			log.info("exception: approveTpaApplication ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: approveTpaApplication ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: approveTpaApplication  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getApproveTpaList()

	@Override
	public TableResult getApproveTpaList(String startDate, String toDate, Criteria criteria) throws BusinessException {
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		TableResult tableResult = new TableResult();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		List<MiscAppTpaApproveValueObject> tpaList = new ArrayList<MiscAppTpaApproveValueObject>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getApproveTpaList  DAO  Start startDate:" + CommonUtility.deNull(startDate) + "toDate:"
					+ CommonUtility.deNull(toDate));

			sb.append(" SELECT  ");
			sb.append(" MA.ACCT_NBR, MA.CONTACT_PERSON, MA.CONTACT_TEL, MA.REF_NBR, CC.CO_NM,  ");
			sb.append(" TO_CHAR(NVL2(ACTUAL_FR_DTTM,ACTUAL_FR_DTTM,FR_DTTM),'DD/MM/YYYY HH24MI') FR_DTTM, ");
			sb.append(" TO_CHAR(NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM),'DD/MM/YYYY HH24MI') TO_DTTM, ");
			sb.append(" MVD.VEH_CHAS_NBR, ");
			sb.append(" MVD.AREA_CD, MVD.SLOT_NBR, MPS.SLOT_TYPE, ");
			sb.append(" NVL2(ACTUAL_TO_DTTM,ACTUAL_NBR_HOUR,NBR_HOUR) NBR_HOUR ");
			sb.append(" FROM MISC_APP MA ");
			sb.append(" LEFT JOIN COMPANY_CODE CC ON MA.CUST_CD = CC.CO_CD ");
			sb.append(" INNER JOIN MISC_VEHICLE MV ON MA.MISC_SEQ_NBR = MV.MISC_SEQ_NBR ");
			sb.append(" INNER JOIN MISC_VEHICLE_DET MVD ON MV.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
			sb.append(" INNER JOIN MISC_PARKING_SLOT MPS ON MVD.SLOT_NBR = ");
			sb.append(" MPS.SLOT_NBR AND MVD.AREA_CD = MPS.AREA_CD ");
			sb.append(" WHERE MA.APP_TYPE='TPA' ");
			sb.append(" AND MA.APP_STATUS IN ('A','B','C') ");
			sb.append(" AND NVL2(ACTUAL_FR_DTTM,ACTUAL_FR_DTTM,FR_DTTM) ");
			sb.append(" BETWEEN TO_DATE(:startDate || ' 000000', 'DDMMYYYY HH24MISS') ");
			sb.append(" AND TO_DATE(:toDate || ' 235959', 'DDMMYYYY HH24MISS') ");
			sb.append(" ORDER BY MV.FR_DTTM, MV.TO_DTTM ASC ");

			log.info(" ***getApproveTpaList SQL *****" + sb.toString());

			paramMap.put("startDate", startDate);
			paramMap.put("toDate", toDate);

			log.info(" *** paramMap: *****" + paramMap.toString());

			int i = 1;
			MiscAppTpaApproveValueObject miscAppParkingAreaObject = null;
			String sql = sb.toString();
			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());

				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());
			}
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				miscAppParkingAreaObject = new MiscAppTpaApproveValueObject();
				miscAppParkingAreaObject.setSNo(i + "");
				miscAppParkingAreaObject.setAccountNo(CommonUtility.deNull(rs.getString("ACCT_NBR")));
				miscAppParkingAreaObject.setContactPerson(CommonUtility.deNull(rs.getString("CONTACT_PERSON")));
				miscAppParkingAreaObject.setContactTel(CommonUtility.deNull(rs.getString("CONTACT_TEL")));
				miscAppParkingAreaObject.setReferenceNo(CommonUtility.deNull(rs.getString("REF_NBR")));
				miscAppParkingAreaObject.setCompanyName(CommonUtility.deNull(rs.getString("CO_NM")));
				miscAppParkingAreaObject.setFromDate(CommonUtility.deNull(rs.getString("FR_DTTM")));
				miscAppParkingAreaObject.setToDate(CommonUtility.deNull(rs.getString("TO_DTTM")));
				miscAppParkingAreaObject.setChassisNo(CommonUtility.deNull(rs.getString("VEH_CHAS_NBR")));
				miscAppParkingAreaObject.setAssignedArea(CommonUtility.deNull(rs.getString("AREA_CD")));
				miscAppParkingAreaObject.setAssignedSlot(CommonUtility.deNull(rs.getString("SLOT_NBR")));
				miscAppParkingAreaObject.setSlotType(CommonUtility.deNull(rs.getString("SLOT_TYPE")));
				miscAppParkingAreaObject.setDurationOfStay(Double.toString(rs.getDouble("NBR_HOUR")));
				tpaList.add(miscAppParkingAreaObject);
				i++;
			}
			log.info("END: *** getApproveTpaList Result ***** Size: " + tpaList.size());
			topsModel.put((Serializable) tpaList);
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
		} catch (NullPointerException e) {
			log.info("exception: getApproveTpaList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getApproveTpaList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getApproveTpaList  DAO  END");
		}
		return tableResult;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getTpaBlockOfHours()

	@Override
	public String getTpaBlockOfHours() throws BusinessException {
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		String blockHours = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getTpaBlockOfHours  DAO  Start ");

			sb.append(" SELECT sp.value FROM system_para sp WHERE sp.para_cd = 'TPBLK' ");

			log.info(" ***getTpaBlockOfHours SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				blockHours = CommonUtility.deNull(rs.getString("value"));
			}
			log.info("END: *** getTpaBlockOfHours Result ***** blockHours: " + CommonUtility.deNull(blockHours));
		} catch (NullPointerException e) {
			log.info("exception: getTpaBlockOfHours ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getTpaBlockOfHours ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTpaBlockOfHours  DAO  END blockHours: " + CommonUtility.deNull(blockHours));
		}
		return blockHours;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->closeBillTrailerParkingApplicationDetails()

	@Override
	public void closeBillTrailerParkingApplicationDetails(String userId, String miscSeqNbr, MiscVehValueObject obj,
			String closeDate) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE misc_vehicle mv ");
		sb.append(" SET mv.actual_fr_dttm = to_date(:actFromDate,'ddMMyyyy HH24MI'), ");
		sb.append(" mv.actual_to_dttm = to_date(:actToDate,'ddMMyyyy HH24MI'), mv.actual_nbr_hour = :actNoHours, ");
		sb.append(" mv.last_modify_user_id = :userId, mv.last_modify_dttm = sysdate WHERE misc_seq_nbr = :miscSeqNbr");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: closeBillTrailerParkingApplicationDetails  DAO  Start userId:"
					+ CommonUtility.deNull(userId) + "miscSeqNbr:" + CommonUtility.deNull(miscSeqNbr) + "obj:"
					+ obj.toString() + "closeDate:" + CommonUtility.deNull(closeDate));

			String status = "C";
			closeBillApplication(userId, status, miscSeqNbr, closeDate);

			log.info(" ***closeBillTrailerParkingApplicationDetails SQL *****" + sb.toString());

			paramMap.put("actFromDate", obj.getActFromDate());
			paramMap.put("actToDate", obj.getActToDate());
			paramMap.put("actNoHours", obj.getActNoHours());
			paramMap.put("userId", userId);
			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			log.info("END: *** closeBillTrailerParkingApplicationDetails Result *****");
		} catch (BusinessException e) {
			log.info("exception: closeBillTrailerParkingApplicationDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: closeBillTrailerParkingApplicationDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: closeBillTrailerParkingApplicationDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: closeBillTrailerParkingApplicationDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->processForTpaEmptyNormal()

	@Override
	public String processForTpaEmptyNormal(String userId, String appType, String status, String cust, String account,
			String varcode, String fromDate, String toDate, String noHours, String applicationRemarks, String[] vehNo,
			String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson, String conTel,
			String[] preferredArea, String[] remarks, String reasonForApplication, String cargoType)
			throws BusinessException {

		try {

			log.info("START: processForTpaEmptyNormal DAO *** userId: " + CommonUtility.deNull(userId) + ", appType: "
					+ CommonUtility.deNull(appType) + ", status: " + CommonUtility.deNull(status) + ", cust: "
					+ CommonUtility.deNull(cust) + ", account: " + CommonUtility.deNull(account) + ", varcode: "
					+ CommonUtility.deNull(varcode) + ", fromDate: " + CommonUtility.deNull(fromDate) + ", toDate: "
					+ CommonUtility.deNull(toDate) + ", noHours: " + CommonUtility.deNull(noHours)
					+ ", applicationRemarks: " + CommonUtility.deNull(applicationRemarks) + ", vehNo: "
					+ vehNo.toString() + ", cntNo: " + cntNo.toString() + ", asnNo: " + asnNo.toString() + ", coName: "
					+ CommonUtility.deNull(coName) + ", appDate: " + CommonUtility.deNull(appDate) + ", conPerson: "
					+ CommonUtility.deNull(conPerson) + ", conTel: " + CommonUtility.deNull(conTel)
					+ ", preferredArea: " + preferredArea.toString() + ", remarks: " + remarks.toString()
					+ ", reasonForApplication: " + CommonUtility.deNull(reasonForApplication) + ", cargoType: "
					+ CommonUtility.deNull(cargoType));

			String miscSeqNumber = "";
			// To get list of available parking slot for empty normal type in selected range
			// date
			List<MiscAppParkingAreaObject> parkingSlotList = new ArrayList<MiscAppParkingAreaObject>();
			int vehSize = 0;
			parkingSlotList = this.getAvailableParkingSlots(cargoType, fromDate, toDate);
			for (int i = 0; i < vehNo.length; i++) {
				if ((vehNo[i] != null && !vehNo[i].equals(""))) {
					vehSize++;
				}
			}
			log.info("vehNo length:" + vehNo.length);
			log.info("vehNo real length:" + vehSize);

			log.info("parkingSlotList size:" + parkingSlotList.size());
			if (parkingSlotList != null && parkingSlotList.size() < vehSize) {
				// Return 0 if the number of available slots are smaller than no of applied
				// trailers
				log.info("Return empty");

				miscSeqNumber = "";
			} else if (parkingSlotList != null && parkingSlotList.size() >= vehSize) {
				log.info("Start assign and auto approve");
				String[] assignedArea = new String[5];
				String[] assignedSlot = new String[5];
				// Auto assign parking slot and approve. No email sent.
				for (int i = 0; i < vehNo.length; i++) {
					if ((vehNo[i] != null && !vehNo[i].equals(""))) {
						if (preferredArea[i] != null && preferredArea[i] != "") {
							log.info("preferredArea[i]:" + preferredArea[i]);
							for (int j = 0; j < parkingSlotList.size(); j++) {
								MiscAppParkingAreaObject miscAppParkingAreaObject = (MiscAppParkingAreaObject) parkingSlotList
										.get(j);
								log.info("miscAppParkingAreaObject.getAreaCode():"
										+ miscAppParkingAreaObject.getAreaCode());
								log.info("miscAppParkingAreaObject.getSlotNumber():"
										+ miscAppParkingAreaObject.getSlotNumber());
								if (preferredArea[i].equalsIgnoreCase(miscAppParkingAreaObject.getAreaCode())) {
									log.info("Area exists.");
									// To assign parking slot for each vehicle
									assignedArea[i] = miscAppParkingAreaObject.getAreaCode();
									assignedSlot[i] = miscAppParkingAreaObject.getSlotNumber();

									parkingSlotList.remove(j);
									break;
								}
							}

						} else {
							log.info("preferredArea is empty");
							MiscAppParkingAreaObject miscAppParkingAreaObject = (MiscAppParkingAreaObject) parkingSlotList
									.get(0);
							assignedArea[i] = miscAppParkingAreaObject.getAreaCode();
							assignedSlot[i] = miscAppParkingAreaObject.getSlotNumber();

							parkingSlotList.remove(0);
						}

					} else {
						log.info("Vehicle no is empty");
					}
				}

				// To add and approved
				miscSeqNumber = this.autoAssignApproveTpa(userId, appType, "A", cust, account, varcode, fromDate,
						toDate, noHours, applicationRemarks, vehNo, cntNo, asnNo, coName, appDate, conPerson, conTel,
						preferredArea, remarks, reasonForApplication, cargoType, assignedArea, assignedSlot);
			}
			log.info("END processForTpaEmptyNormal()");
			log.info("END: *** processForTpaEmptyNormal Result *****" + miscSeqNumber.toString());
			return miscSeqNumber;
		} catch (BusinessException e) {
			log.info("exception: processForTpaEmptyNormal ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: processForTpaEmptyNormal ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: processForTpaEmptyNormal ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: processForTpaEmptyNormal  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getTpaDatePeriod()

	@Override
	public int getTpaDatePeriod() throws BusinessException {
		int days = 0;
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getTpaDatePeriod  DAO  Start");

			sb.append(" select VALUE from SYSTEM_PARA  where para_cd = 'TPMXD' ");

			log.info(" ***getTpaDatePeriod SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				days = rs.getInt("VALUE");
			}
			log.info("END: *** getTpaDatePeriod Result *****" + days);
			return days;
		} catch (NullPointerException e) {
			log.info("exception: getTpaDatePeriod ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getTpaDatePeriod ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTpaDatePeriod  DAO  END Result: days: ");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateTrailerParkingApplicationDetails()

	@Override
	public void updateTrailerParkingApplicationDetails(String userId, String miscSeqNbr, String status, String fromDate,
			String toDate, String noHours, String parkReason, String[] vehNo, String[] cntNo, String[] asnNo,
			String coName, String applyType, String account, String appStatusCd, String conPerson, String conTel,
			String[] preferredArea, String[] remarks, String reasonForApplication, String cargoType)
			throws BusinessException {
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: updateTrailerParkingApplicationDetails  DAO  Start userId:" + CommonUtility.deNull(userId)
					+ "status:" + CommonUtility.deNull(status) + ", miscSeqnbr: " + CommonUtility.deNull(miscSeqNbr)
					+ "account:" + CommonUtility.deNull(account) + "fromDate:" + CommonUtility.deNull(fromDate)
					+ "toDate:" + CommonUtility.deNull(toDate) + "noHours:" + CommonUtility.deNull(noHours) + "vehNo:"
					+ vehNo + "cntNo:" + cntNo + "asnNo:" + asnNo + "coName:" + coName + "conPerson:"
					+ CommonUtility.deNull(conPerson) + "conTel:" + CommonUtility.deNull(conTel) + "preferredArea:"
					+ preferredArea.toString() + "remarks:" + remarks.toString() + "reasonForApplication:"
					+ CommonUtility.deNull(reasonForApplication) + "cargoType:" + CommonUtility.deNull(cargoType));

			sb.append(" update /* MiscAppEJB - updateTrailerParkingApplicationDetails(vehSql) */ ");
			sb.append(" misc_vehicle set fr_dttm = to_date(:fromDate,'ddMMyyyy HH24MI'), ");
			sb.append(" to_dttm = to_date(:toDate,'ddMMyyyy HH24MI'), nbr_night = :nbrNight, ");
			sb.append(" park_reason= :parkReason , ");
			sb.append(" last_modify_user_id = :userId, last_modify_dttm = sysdate, ");
			sb.append(" NBR_HOUR = :noHours, PARK_REASON_CD = :reasonForApplication, CARGO_TYPE = :cargoType");
			sb.append(" where misc_seq_nbr = :miscSeqNbr ");

			sb1.append(" delete /* MiscAppEJB - updateTrailerParkingApplicationDetails(vehDelSql) */ ");
			sb1.append(" from misc_vehicle_det where misc_seq_nbr = :miscSeqNbr ");

			sb2.append(" insert into misc_vehicle_det(misc_seq_nbr, item_nbr, ");
			sb2.append(" veh_chas_nbr, cntr_nbr, ");
			sb2.append(" asn_nbr, last_modify_user_id, last_modify_dttm, ");
			sb2.append(" REMARKS, PREF_AREA_CD) values ( ");
			sb2.append(" :miscSeqNbr,:itemNbr,:vehNo,:cntNo,:asnNo, ");
			sb2.append(" :userId,sysdate, :remarks, :preferredArea) ");

			updateMiscAppDetails(userId, status, miscSeqNbr, applyType, account, appStatusCd, conPerson, conTel);

			log.info(" ***updateTrailerParkingApplicationDetails SQL *****" + sb.toString());

			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			paramMap.put("nbrNight", "0");
			paramMap.put("parkReason", parkReason);
			paramMap.put("userId", userId);
			paramMap.put("noHours", noHours);
			paramMap.put("reasonForApplication", reasonForApplication);
			paramMap.put("cargoType", cargoType);
			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			log.info(" ***updateTrailerParkingApplicationDetails SQL2 *****" + sb1.toString());
			paramMap = new HashMap<String, Object>();
			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb1.toString(), paramMap);

			if (vehNo != null) {
				log.info(" ***updateTrailerParkingApplicationDetails SQL3 *****" + sb2.toString());
				paramMap = new HashMap<String, Object>();
				int j = 1;
				for (int i = 0; i < vehNo.length; i++) {

					paramMap.put("miscSeqNbr", miscSeqNbr);
					paramMap.put("vehNo",
							(vehNo[i] != null && !vehNo[i].equals(""))
									? CommonUtility.getStringTokens(vehNo[i]).toUpperCase()
									: vehNo[i]);
					paramMap.put("cntNo", cntNo[i]);
					paramMap.put("asnNo", asnNo[i]);
					paramMap.put("userId", userId);
					paramMap.put("remarks", remarks[i]);
					paramMap.put("preferredArea", preferredArea[i]);

					if ((vehNo[i] != null && !vehNo[i].equals(""))) {
						paramMap.put("itemNbr", j);
						j++;
						log.info(" *** paramMap: *****" + paramMap.toString());
						namedParameterJdbcTemplate.update(sb2.toString(), paramMap);
					}
				}
			}
			// Send Email
			if (status != null && status.equals("S")) {
				String alertCode = ConstantUtil.alertCode;
				String refNbr = getMiscRefNbr(miscSeqNbr);
				String appDttm = getApplicationDttm(miscSeqNbr);
				String subject = TrailerParkingAppSubmit_subject;
				String templateEmailFile = TemplateSubmit_body_template;
								
				String contentMail = CommonUtil.readTemplateFromFile(getClass().getClassLoader(), templateEmailFile);
				Map<String, String> emailInputData = new HashMap<String, String>();
								
				subject = StringUtils.replace(subject, "<refNbr>", CommonUtility.deNull(refNbr));
				emailInputData.put("refNbr", CommonUtility.deNull(refNbr));
				emailInputData.put("appDttm", CommonUtility.deNull(appDttm));
				emailInputData.put("coName", coName);
				emailInputData.put("typeApp", "Trailer Parking Application");
								
				String msgBody = CommonUtil.replaceVariablesInHtml(contentMail, emailInputData);
				
				/*
				 * String msgBody = "The following application is submitted for approval: \n\n"
				 * + "Type of Application: Trailer Parking Application \n" + "Reference No.: " +
				 * CommonUtility.deNull(refNbr) + " \n" + "Application Date/Time: " +
				 * CommonUtility.deNull(appDttm) + " \n" + "Company: " + coName + "\n";
				 */
				
				sendMail(alertCode, subject, msgBody);

				String sms = "Application submitted for approval: Trailer Parking Application, " + "Ref. No.: "
						+ CommonUtility.deNull(refNbr) + " , on " + CommonUtility.deNull(appDttm) + " , by " + coName;
				sendSMS(alertCode, sms);
			}
			log.info("END: *** updateTrailerParkingApplicationDetails Result *****");
		} catch (BusinessException e) {
			log.info("exception: updateTrailerParkingApplicationDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: updateTrailerParkingApplicationDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateTrailerParkingApplicationDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateTrailerParkingApplicationDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateForTpaEmptyNormal()

	@Override
	public String updateForTpaEmptyNormal(String userId, String appType, String status, String cust, String account,
			String varcode, String fromDate, String toDate, String noHours, String applicationRemarks, String[] vehNo,
			String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson, String conTel,
			String[] preferredArea, String[] remarks, String reasonForApplication, String cargoType, String miscSeqNbr,
			String appStatusCd) throws BusinessException {
		try {
			log.info("START: updateForTpaEmptyNormal DAO *** userId: " + CommonUtility.deNull(userId) + ", appType: "
					+ CommonUtility.deNull(appType) + ", status: " + CommonUtility.deNull(status) + ", cust: "
					+ CommonUtility.deNull(cust) + ", account: " + CommonUtility.deNull(account) + ", varcode: "
					+ CommonUtility.deNull(varcode) + ", fromDate: " + CommonUtility.deNull(fromDate) + ", toDate: "
					+ CommonUtility.deNull(toDate) + ", noHours: " + CommonUtility.deNull(noHours)
					+ ", applicationRemarks: " + CommonUtility.deNull(applicationRemarks) + ", vehNo: "
					+ vehNo.toString() + ", cntNo: " + cntNo.toString() + ", asnNo: " + asnNo.toString() + ", coName: "
					+ CommonUtility.deNull(coName) + ", appDate: " + CommonUtility.deNull(appDate) + ", conPerson: "
					+ CommonUtility.deNull(conPerson) + ", conTel: " + CommonUtility.deNull(conTel)
					+ ", preferredArea: " + preferredArea.toString() + ", remarks: " + remarks.toString()
					+ ", reasonForApplication: " + CommonUtility.deNull(reasonForApplication) + ", cargoType: "
					+ CommonUtility.deNull(cargoType) + ", miscSeqNbr: " + CommonUtility.deNull(miscSeqNbr)
					+ ", appStatusCd: " + CommonUtility.deNull(appStatusCd));
			String miscSeqNumber = "";
			// To get list of available parking slot for empty normal type in selected range
			// date
			List<MiscAppParkingAreaObject> parkingSlotList = new ArrayList<MiscAppParkingAreaObject>();
			int vehSize = 0;
			parkingSlotList = this.getAvailableParkingSlots(cargoType, fromDate, toDate);
			for (int i = 0; i < vehNo.length; i++) {
				if ((vehNo[i] != null && !vehNo[i].equals(""))) {
					vehSize++;
				}
			}
			log.info("vehNo.length:" + vehSize);

			log.info("parkingSlotList size:" + parkingSlotList.size());
			if (parkingSlotList != null && parkingSlotList.size() < vehSize) {
				// Return 0 if the number of available slots are smaller than no of applied
				// trailers
				log.info("Return empty");

				miscSeqNumber = "";
			} else if (parkingSlotList != null && parkingSlotList.size() >= vehSize) {
				log.info("Start assign and auto approve");
				String[] assignedArea = new String[5];
				String[] assignedSlot = new String[5];

				for (int i = 0; i < vehNo.length; i++) {
					if ((vehNo[i] != null && !vehNo[i].equals(""))) {
						if (preferredArea[i] != null && preferredArea[i] != "") {
							log.info("preferredArea[i]:" + preferredArea[i]);
							log.info("parkingSlotList.size():" + parkingSlotList.size());
							for (int j = 0; j < parkingSlotList.size(); j++) {
								MiscAppParkingAreaObject miscAppParkingAreaObject = (MiscAppParkingAreaObject) parkingSlotList
										.get(j);
								log.info("miscAppParkingAreaObject.getAreaCode():"
										+ miscAppParkingAreaObject.getAreaCode());
								log.info("miscAppParkingAreaObject.getSlotNumber():"
										+ miscAppParkingAreaObject.getSlotNumber());
								if (preferredArea[i].equalsIgnoreCase(miscAppParkingAreaObject.getAreaCode())) {
									log.info("Area exists.");
									// To assign parking slot for each vehicle
									assignedArea[i] = miscAppParkingAreaObject.getAreaCode();
									assignedSlot[i] = miscAppParkingAreaObject.getSlotNumber();

									parkingSlotList.remove(j);
									break;
								}
							}

						} else {
							log.info("preferredArea is empty");
							MiscAppParkingAreaObject miscAppParkingAreaObject = (MiscAppParkingAreaObject) parkingSlotList
									.get(0);
							assignedArea[i] = miscAppParkingAreaObject.getAreaCode();
							assignedSlot[i] = miscAppParkingAreaObject.getSlotNumber();

							parkingSlotList.remove(0);
						}

					} else {
						log.info("Vehicle no is empty");
					}
				}

				// To add and approved
				miscSeqNumber = this.autoAssignApproveForUpdateTpa(userId, appType, "A", cust, account, varcode,
						fromDate, toDate, noHours, applicationRemarks, vehNo, cntNo, asnNo, coName, appDate, conPerson,
						conTel, preferredArea, remarks, reasonForApplication, cargoType, miscSeqNbr, appStatusCd,
						assignedArea, assignedSlot);
			}
			log.info("END updateForTpaEmptyNormal()");
			log.info("END: *** updateForTpaEmptyNormal Result *****" + miscSeqNumber.toString());
			return miscSeqNumber;
		} catch (BusinessException e) {
			log.info("exception: updateForTpaEmptyNormal ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: updateForTpaEmptyNormal ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateForTpaEmptyNormal ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateForTpaEmptyNormal  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getAvailableParkingSlots()
	/**
	 * Get list of parking area slot with all information
	 * 
	 * @param slotType
	 * @param startDate
	 * @param toDate
	 * @return
	 * @throws BusinessException
	 */

	@Override
	public List<MiscAppParkingAreaObject> getAvailableParkingSlots(String slotType, String startDate, String toDate)
			throws BusinessException {
		List<MiscAppParkingAreaObject> parkingSlotList = new ArrayList<MiscAppParkingAreaObject>();
		// --1. FR_DDTM between selected from date and selected to date "
		// --2. TO_DDTM between selected from date and selected to date "
		// --2. TO_DDTM between selected from date and selected to date "

		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getAvailableParkingSlots  DAO  Start slotType:" + CommonUtility.deNull(slotType)
					+ "startDate:" + CommonUtility.deNull(startDate) + "toDate:" + CommonUtility.deNull(toDate));

			sb.append(" SELECT AREA_CD, SLOT_NBR, SLOT_TYPE, SLOT_STATUS FROM MISC_PARKING_SLOT ");
			sb.append(" WHERE SLOT_STATUS='OPN' AND SLOT_TYPE = :slotType AND ");
			sb.append(" (AREA_CD, SLOT_NBR) NOT IN ");
			sb.append(" ( SELECT MPS.AREA_CD, MPS.SLOT_NBR FROM MISC_PARKING_SLOT MPS ");
			sb.append(" INNER JOIN MISC_VEHICLE_DET MVD ");
			sb.append(" ON MPS.AREA_CD = MVD.AREA_CD AND MPS.SLOT_NBR = MVD.SLOT_NBR ");
			sb.append(" INNER JOIN MISC_VEHICLE MV ON MV.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
			sb.append(" INNER JOIN MISC_APP MA ON MV.MISC_SEQ_NBR = MA.MISC_SEQ_NBR ");
			sb.append("  WHERE SLOT_STATUS='OPN' AND SLOT_TYPE = :slotType ");
			sb.append(" AND MVD.AREA_CD IS NOT NULL AND MVD.SLOT_NBR IS NOT NULL  ");
			sb.append(" AND (MA.app_status='A' or MA.app_status='C') AND ( ");
			// sb.append(" (FR_DTTM BETWEEN TO_DATE(?, 'ddMMyyyy HH24MI') and TO_DATE(?,
			// 'ddMMyyyy HH24MI') ) "
			// sb.append(" OR (TO_DTTM BETWEEN TO_DATE(?, 'ddMMyyyy HH24MI') and TO_DATE(?,
			// 'ddMMyyyy HH24MI') ) "
			// sb.append(" OR (FR_DTTM <= TO_DATE(?, 'ddMMyyyy HH24MI') and TO_DTTM >=
			// TO_DATE(?, 'ddMMyyyy HH24MI') ) "
			// sb.append(" OR (FR_DTTM > TO_DATE(?, 'ddMMyyyy HH24MI') and TO_DTTM >=
			// TO_DATE(?, 'ddMMyyyy HH24MI') ) "
			sb.append(" (TO_DATE(:startDate, 'ddMMyyyy HH24MI') > NVL2(ACTUAL_FR_DTTM, ");
			sb.append(" ACTUAL_FR_DTTM,FR_DTTM) and TO_DATE(:startDate, 'ddMMyyyy HH24MI') ");
			sb.append(" < NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM) ) ");
			sb.append(" OR (TO_DATE(:toDate, 'ddMMyyyy HH24MI') > NVL2(ACTUAL_FR_DTTM, ");
			sb.append(" ACTUAL_FR_DTTM,FR_DTTM) and TO_DATE(:toDate, 'ddMMyyyy HH24MI') < ");
			sb.append(" NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM) ) ");
			sb.append(" OR (TO_DATE(:startDate, 'ddMMyyyy HH24MI') <= NVL2(ACTUAL_FR_DTTM, ");
			sb.append(" ACTUAL_FR_DTTM,FR_DTTM)  and TO_DATE(:toDate, 'ddMMyyyy HH24MI') >= ");
			sb.append(" NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM)) ");
			sb.append("    ) )  ORDER BY AREA_CD, SLOT_NBR ");

			log.info(" ***getAvailableParkingSlots SQL *****" + sb.toString());

			paramMap.put("slotType", slotType);
			paramMap.put("startDate", startDate);
			paramMap.put("toDate", toDate);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				MiscAppParkingAreaObject miscAppParkingAreaObject = new MiscAppParkingAreaObject();
				miscAppParkingAreaObject.setAreaCode(rs.getString("AREA_CD"));
				miscAppParkingAreaObject.setSlotNumber(rs.getString("SLOT_NBR"));
				miscAppParkingAreaObject.setSlotStatus(rs.getString("SLOT_TYPE"));
				miscAppParkingAreaObject.setSlotType(rs.getString("SLOT_STATUS"));

				parkingSlotList.add(miscAppParkingAreaObject);
			}
			log.info("END: *** getAvailableParkingSlots Result *****" + parkingSlotList.toString());
			return parkingSlotList;

		} catch (NullPointerException e) {
			log.info("exception: getAvailableParkingSlots ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getAvailableParkingSlots ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAvailableParkingSlots  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->enquireSummaryParkingSlot()

	@Override
	public List<EnquireSummarySlotValueObject> enquireSummaryParkingSlot(EnquireQueryObject queryObj)
			throws BusinessException {
		String[] blockTime = miscAppCommonUtility.getBlockTime();
		String[] timeList = miscAppCommonUtility.createTimeList(blockTime);
		int numberOfTimeSlot = timeList.length;
		int hourPerBlock = miscAppCommonUtility.getHourPerBlock();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (queryObj == null || !checkQueryObject(queryObj, numberOfTimeSlot, hourPerBlock)) {
			return new ArrayList<EnquireSummarySlotValueObject>();
		}
		List<EnquireSummarySlotValueObject> tmpResult = new ArrayList<EnquireSummarySlotValueObject>();
		EnquireSummarySlotValueObject rowObj = null;

		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: enquireSummaryParkingSlot  DAO  Start queryObj:" + queryObj.toString());

			// start modified on 20/09/13 by thanhbtl6b for TPA Enhancement

			sb.append(" SELECT TRIM(mpl.AREA_CD), TRIM(mpl.SLOT_NBR), TRIM(mpl.SLOT_TYPE), ");
			sb.append(" TRIM(mpl.TRAILER_TYPE), TRIM(mpl.TRAILER_SIZE), ");
			// TRAILER_TYPE AND TRAILER_SIZE by thanhbtl6b
			sb.append(" appdet.FR_DTTM , appdet.TO_DTTM ");
			sb.append(" FROM MISC_PARKING_SLOT mpl ");
			sb.append(" LEFT JOIN ( ");
			sb.append(" SELECT NVL2(mv.ACTUAL_FR_DTTM,mv.ACTUAL_FR_DTTM,FR_DTTM) ");
			sb.append(" AS FR_DTTM, NVL2(mv.ACTUAL_TO_DTTM,mv.ACTUAL_TO_DTTM,TO_DTTM) AS TO_DTTM, ");
			sb.append(" mvd.AREA_CD,  mvd.SLOT_NBR ");
			sb.append(" FROM ");
			sb.append(" MISC_VEHICLE_DET mvd ");
			sb.append(" INNER JOIN MISC_VEHICLE mv ON mvd.MISC_SEQ_NBR = mv.MISC_SEQ_NBR ");
			sb.append(" INNER JOIN  MISC_APP ma ON ma.MISC_SEQ_NBR = mv.MISC_SEQ_NBR ");
			sb.append(" WHERE ");
			sb.append(" (ma.APP_STATUS <> 'R' OR ma.APP_STATUS IS NULL) AND  ma.APP_TYPE = 'TPA' ");
			sb.append(" ) appdet ");
			sb.append(" ON mpl.AREA_CD = appdet.AREA_CD AND mpl.SLOT_NBR = appdet.SLOT_NBR ");
			sb.append(" WHERE ");
			sb.append(" mpl.SLOT_STATUS= 'OPN' ");

			if (!"ALL".equals(queryObj.getAreaCode())) {
				sb.append(" AND mpl.AREA_CD =:areaCode ");
			}

			if (!"ALL".equals(queryObj.getSlotType())) {
				sb.append(" AND mpl.SLOT_TYPE =:slotType ");
			}

			// trailer by thanhbtl6b
			if (queryObj.getTrailerSize() == 20 || queryObj.getTrailerSize() == 40) {
				sb.append(" AND mpl.TRAILER_SIZE =:trailerSize ");
			}

			if ("E".equals(queryObj.getTrailerType()) || "L".equals(queryObj.getTrailerType())) {
				sb.append(" AND mpl.TRAILER_TYPE =:trailerType ");
			}

			// End modified on 20/09/13 by thanhbtl6b for TPA Enhancement

			sb.append(" ORDER BY mpl.AREA_CD ASC, mpl.SLOT_TYPE ASC, ");
			sb.append(" mpl.TRAILER_SIZE ASC, mpl.TRAILER_TYPE ASC, mpl.SLOT_NBR ASC ");

			log.info(" ***enquireSummaryParkingSlot SQL *****" + sb.toString());

			if (!"ALL".equals(queryObj.getAreaCode())) {
				paramMap.put("areaCode", StringUtils.trimToEmpty(queryObj.getAreaCode()));
			}

			if (!"ALL".equals(queryObj.getSlotType())) {
				paramMap.put("slotType", StringUtils.trimToEmpty(queryObj.getSlotType()));
			}

			// trailer by thanhbtl6b
			if (queryObj.getTrailerSize() == 20 || queryObj.getTrailerSize() == 40) {
				paramMap.put("trailerSize", queryObj.getTrailerSize());
			}

			if ("E".equals(queryObj.getTrailerType()) || "L".equals(queryObj.getTrailerType())) {
				paramMap.put("trailerType", queryObj.getTrailerType());
			}

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				rowObj = new EnquireSummarySlotValueObject();
				rowObj.setAreaCode(rs.getString(1));
				rowObj.setSlotNumber(rs.getString(2));
				rowObj.setSlotType(rs.getString(3));

				// Start modified on 20/09/13 by thanhbtl6b for TPA Enhancement
				rowObj.setTrailerType(rs.getString(4));
				rowObj.setTrailerSize(rs.getInt(5));
				// End modified on 20/09/13 by thanhbtl6b for TPA Enhancement

				rowObj.setFromDate(rs.getTimestamp(6));
				rowObj.setToDate(rs.getTimestamp(7));

				tmpResult.add(rowObj);
			}
			log.info("END: *** enquireSummaryParkingSlot Result *****" + tmpResult);
		} catch (NullPointerException e) {
			log.info("exception: enquireSummaryParkingSlot ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: enquireSummaryParkingSlot ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: enquireSummaryParkingSlot  DAO  END");
		}
		return calculateObjectsForSummaryScreen(tmpResult, queryObj, numberOfTimeSlot, hourPerBlock);
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->listParkingSlotForEnquire()
	/**
	 * Get list of slot that belongs to selected area. If areaCode is empty, get all
	 * slots that available (slot's status differs 'Deleted' ).
	 * 
	 * @param currentAreaCode code of selected area.
	 * @return list of slot.
	 * @throws BusinessException if has any business error.
	 */

	@Override
	public List<Object> listParkingSlotForEnquire(EnquireQueryObject queryObj, Criteria criteria, Boolean excel)
			throws BusinessException {
		List<Object> slotList = new ArrayList<Object>();
		EnquireListingValueObject slotObj = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sbStart = new StringBuilder();
		StringBuilder sbEnd = new StringBuilder();
		String sql = "";

		// Start modified by Hoa Nguyen on 20-Sep-2013 for TPA enhancement
		int availableCol = miscAppCommonUtility.calculateAvailableCol(queryObj.getStartDate(), queryObj.getEndDate());
		// End modified by Hoa Nguyen on 20-Sep-2013 for TPA enhancement
		log.info("queryObj.getStartDate():" + queryObj.getStartDate());
		log.info("getStartTime():" + queryObj.getStartTime());
		log.info("getEndDate():" + queryObj.getEndDate());
		log.info("getEndTime():" + queryObj.getEndTime());
		log.info("getAreaCode():" + queryObj.getAreaCode());
		log.info("getSlotType():" + queryObj.getSlotType());
		log.info("getTrailerSize():[" + queryObj.getTrailerSize() + "]");
		log.info("getTrailerType():[" + queryObj.getTrailerType() + "]");

		String fromDate = queryObj.getStartDate() + " " + queryObj.getStartTime();
		String toDate = queryObj.getEndDate() + " " + queryObj.getEndTime();

		try {
			log.info("START: listParkingSlotForEnquire  DAO  Start queryObj:" + queryObj.toString() + ", criteria: "
					+ criteria.toString() + ", excel: " + excel.toString());

			sbStart.append("  SELECT COUNT(*) as count FROM ( ");
			sbEnd.append(" )");

			sb.append(" SELECT MPS.AREA_CD, MPS.SLOT_NBR, MPS.SLOT_TYPE, MPS.SLOT_STATUS, ");
			sb.append(" MPS.TRAILER_TYPE, MPS.TRAILER_SIZE, ");
			// <-- add more TRAILER SIZE & TYPE by thanhbtl6b for TPA enhancement
			sb.append(" APP_DET.MISC_SEQ_NBR, APP_DET.VEH_CHAS_NBR,  ");
			sb.append(" TO_CHAR(APP_DET.FR_DTTM,'DDMMYYYY') FRDTTM, ");
			sb.append(" TO_CHAR(APP_DET.FR_DTTM,'HH24MI') FRTIME, ");
			sb.append(" TO_CHAR(APP_DET.TO_DTTM,'DDMMYYYY') TODTTM, ");
			sb.append(" TO_CHAR(APP_DET.TO_DTTM,'HH24MI') TOTIME,  ");
			sb.append(" APP_DET.REMARKS, APP_DET.misc_type_nm  ");
			sb.append(" FROM MISC_PARKING_SLOT MPS ");
			sb.append(" LEFT JOIN ( ");
			sb.append(" SELECT MVD.MISC_SEQ_NBR, MVD.VEH_CHAS_NBR, ");
			sb.append(" NVL2(ACTUAL_FR_DTTM,ACTUAL_FR_DTTM,FR_DTTM) as FR_DTTM, ");
			sb.append(" NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM) as TO_DTTM,  ");
			sb.append(" MVD.AREA_CD,  MVD.SLOT_NBR, MVD.REMARKS, mtc.misc_type_nm ");
			sb.append(" FROM MISC_VEHICLE_DET MVD ");
			sb.append(" INNER JOIN MISC_VEHICLE MV ON MV.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
			sb.append(" INNER JOIN MISC_APP MA ON MA.MISC_SEQ_NBR = MV.MISC_SEQ_NBR ");
			sb.append(" left join misc_type_code mtc on mtc.misc_type_cd = MA.APP_TYPE ");
			sb.append(" WHERE MA.APP_TYPE = 'TPA' ");
			sb.append(" AND MA.APP_STATUS IN ('A','B','C') ");
			sb.append(" AND (  ");
			sb.append(" (TO_DATE(:fromDate, 'ddMMyyyy HH24MI') > NVL2(ACTUAL_FR_DTTM,ACTUAL_FR_DTTM, ");
			sb.append(" FR_DTTM) and TO_DATE(:fromDate, 'ddMMyyyy HH24MI') < ");
			sb.append(" NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM) ) ");
			sb.append(" OR (TO_DATE(:toDate, 'ddMMyyyy HH24MI') > NVL2(ACTUAL_FR_DTTM, ");
			sb.append(" ACTUAL_FR_DTTM,FR_DTTM) and TO_DATE(:toDate, 'ddMMyyyy HH24MI') < ");
			sb.append(" NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM) ) ");
			sb.append(" OR (TO_DATE(:fromDate, 'ddMMyyyy HH24MI') <= NVL2(ACTUAL_FR_DTTM, ");
			sb.append(" ACTUAL_FR_DTTM,FR_DTTM)  and TO_DATE(:toDate, 'ddMMyyyy HH24MI') >= ");
			sb.append(" NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM)) ");
			sb.append(" ) ) APP_DET ON MPS.AREA_CD = APP_DET.AREA_CD AND MPS.SLOT_NBR = APP_DET.SLOT_NBR ");
			sb.append("  WHERE MPS.SLOT_STATUS != 'DLTD' ");

			if (!"ALL".equalsIgnoreCase(queryObj.getAreaCode())) {
				sb.append(" AND MPS.AREA_CD = :areaCode ");
			}

			if (!"ALL".equalsIgnoreCase(queryObj.getSlotType())) {
				sb.append(" AND MPS.SLOT_TYPE = :slotType ");
			}
			// Start modified on 20/09/13 by thanhbtl6b for TPA Enhancement
			if (queryObj.getTrailerSize() == 20 || queryObj.getTrailerSize() == 40) {
				sb.append("AND MPS.TRAILER_SIZE = :trailerSize ");
			}
			if (Constants.TRAILER_TYPE_CODE_E.equals(queryObj.getTrailerType())
					|| Constants.TRAILER_TYPE_CODE_L.equals(queryObj.getTrailerType())) {
				sb.append("AND MPS.TRAILER_TYPE = :trailerType ");
			}
			// End modified on 20/09/13 by thanhbtl6b for TPA Enhancement

			sb.append(" ORDER BY MPS.AREA_CD, MPS.SLOT_NBR ");

			if (excel) {
				sql = sb.toString();
			} else {
				if (criteria.isPaginated()) {
					sql = CommonUtil.getPaginatedSql(sb.toString(), criteria.getStart(), criteria.getLimit());
				}
			}

			log.info(" ***listParkingSlotForEnquire SQL *****" + sb.toString());

			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);

			log.info(" *** paramMap: *****" + paramMap.toString());

			// int index = 7;//for increase the 1st arg of paramMap.put(,) by thanhbtl6b

			if (!"ALL".equalsIgnoreCase(queryObj.getAreaCode()) && !"ALL".equalsIgnoreCase(queryObj.getSlotType())) {
				paramMap.put("areaCode", queryObj.getAreaCode());
				paramMap.put("slotType", queryObj.getSlotType());
				paramMap.put("areaCode", queryObj.getAreaCode());
				paramMap.put("slotType", queryObj.getSlotType());
			} else {
				if (!"ALL".equalsIgnoreCase(queryObj.getAreaCode())) {
					paramMap.put("areaCode", queryObj.getAreaCode());
					paramMap.put("areaCode", queryObj.getAreaCode());
				} else {
					if (!"ALL".equalsIgnoreCase(queryObj.getSlotType())) {
						paramMap.put("slotType", queryObj.getSlotType());
						paramMap.put("slotType", queryObj.getSlotType());
					}
				}
			}

			// Start modified on 20/09/13 by thanhbtl6b for TPA Enhancement
			if (queryObj.getTrailerSize() == 20 || queryObj.getTrailerSize() == 40) {
				paramMap.put("trailerSize", queryObj.getTrailerSize());
				paramMap.put("trailerSize", queryObj.getTrailerSize());
			}
			if (Constants.TRAILER_TYPE_CODE_E.equals(queryObj.getTrailerType())
					|| Constants.TRAILER_TYPE_CODE_L.equals(queryObj.getTrailerType())) {
				paramMap.put("trailerType", queryObj.getTrailerType());
				paramMap.put("trailerType", queryObj.getTrailerType());
			}

			// End modified on 20/09/13 by thanhbtl6b for TPA Enhancement

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			rs1 = namedParameterJdbcTemplate.queryForRowSet(sbStart.toString() + sb.toString() + sbEnd.toString(),
					paramMap);
			int count = 0;
			if (rs1.next()) {
				count = rs1.getInt("count");
			}

			String areaCode = null;
			String slotNumber = null;
			while (rs.next()) {
				if (areaCode == null && slotNumber == null) {
					areaCode = StringUtils.trimToNull(rs.getString("AREA_CD"));
					slotNumber = StringUtils.trimToNull(rs.getString("SLOT_NBR"));
					if (slotObj == null) {
						slotObj = new EnquireListingValueObject();
						slotObj.setAreaCode(StringUtils.trimToNull(rs.getString("AREA_CD")));
						slotObj.setSlotNumber(StringUtils.trimToNull(rs.getString("SLOT_NBR")));
						slotObj.setSlotStatus(rs.getString("SLOT_STATUS"));
						slotObj.setSlotType(rs.getString("SLOT_TYPE"));

						// Start modified on 20/09/13 by thanhbtl6b for TPA Enhancement
						slotObj.setTrailerTypeCode(rs.getString("TRAILER_TYPE"));
						slotObj.setTrailerTypeName((rs.getString("TRAILER_TYPE") != null)
								? (String) Constants.TRAILER_TYPE_TABLE.get(rs.getString("TRAILER_TYPE"))
								: "");
						slotObj.setTrailerSize(rs.getInt("TRAILER_SIZE"));
						// End modified on 20/09/13 by thanhbtl6b for TPA Enhancement
					}
				}
				log.info("areaCode from rs = " + StringUtils.trimToNull(rs.getString("AREA_CD")));
				log.info("slotNumber from rs = " + StringUtils.trimToNull(rs.getString("SLOT_NBR")));

				log.info("areaCode = " + areaCode);
				log.info("slotNumber = " + slotNumber);

				EnquireListingAppValueObject enquireListingAppValueObject = new EnquireListingAppValueObject();
				enquireListingAppValueObject.setAreaCode(StringUtils.trimToNull(rs.getString("AREA_CD")));
				enquireListingAppValueObject.setSlotNumber(StringUtils.trimToNull(rs.getString("SLOT_NBR")));
				enquireListingAppValueObject.setSlotStatus(rs.getString("SLOT_STATUS"));
				enquireListingAppValueObject.setSlotType(rs.getString("SLOT_TYPE"));

				enquireListingAppValueObject.setFromDate(rs.getString("FRDTTM"));
				enquireListingAppValueObject.setFromTime(rs.getString("FRTIME"));
				enquireListingAppValueObject.setToDate(rs.getString("TODTTM"));
				enquireListingAppValueObject.setToTime(rs.getString("TOTIME"));
				enquireListingAppValueObject.setRemarks(rs.getString("REMARKS"));
				enquireListingAppValueObject.setMiscSeqNumber(rs.getString("MISC_SEQ_NBR"));
				enquireListingAppValueObject.setVehChassNo(rs.getString("VEH_CHAS_NBR"));
				enquireListingAppValueObject.setAppTypeName(rs.getString("misc_type_nm"));

				// Start modified on 20/09/13 by thanhbtl6b for TPA Enhancement
				enquireListingAppValueObject.setTrailerTypeCode(rs.getString("TRAILER_TYPE"));
				log.info("TRAILER_TYPE = " + rs.getString("TRAILER_TYPE"));
				enquireListingAppValueObject.setTrailerTypeName((rs.getString("TRAILER_TYPE") != null)
						? (String) Constants.TRAILER_TYPE_TABLE.get(rs.getString("TRAILER_TYPE"))
						: "");
				enquireListingAppValueObject.setTrailerSize(rs.getInt("TRAILER_SIZE"));
				// End modified on 20/09/13 by thanhbtl6b for TPA Enhancement

				this.calculateCellValue(enquireListingAppValueObject, queryObj);
				if (areaCode.equalsIgnoreCase(StringUtils.trimToNull(rs.getString("AREA_CD")))
						&& slotNumber.equalsIgnoreCase(StringUtils.trimToNull(rs.getString("SLOT_NBR")))) {
					log.info("Equal then Add to slot object");
					slotObj.getAppValueObjects().add(enquireListingAppValueObject);
				} else {
					log.info("Not equal then Add to slot list");
					this.calculateCellValueForRow(slotObj, availableCol);
					slotList.add(slotObj);

					slotObj = new EnquireListingValueObject();
					slotObj.setAreaCode(StringUtils.trimToNull(rs.getString("AREA_CD")));
					slotObj.setSlotNumber(StringUtils.trimToNull(rs.getString("SLOT_NBR")));
					slotObj.setSlotStatus(rs.getString("SLOT_STATUS"));
					slotObj.setSlotType(rs.getString("SLOT_TYPE"));
					slotObj.getAppValueObjects().add(enquireListingAppValueObject);

					// Start modified on 20/09/13 by thanhbtl6b for TPA Enhancement
					slotObj.setTrailerTypeCode(rs.getString("TRAILER_TYPE"));
					slotObj.setTrailerTypeName((rs.getString("TRAILER_TYPE") != null)
							? (String) Constants.TRAILER_TYPE_TABLE.get(rs.getString("TRAILER_TYPE"))
							: "");
					slotObj.setTrailerSize(rs.getInt("TRAILER_SIZE"));
					// End modified on 20/09/13 by thanhbtl6b for TPA Enhancement

					areaCode = StringUtils.trimToNull(rs.getString("AREA_CD"));
					slotNumber = StringUtils.trimToNull(rs.getString("SLOT_NBR"));

				}

			}
			if (slotObj != null) {
				this.calculateCellValueForRow(slotObj, availableCol);
				slotList.add(slotObj);
				slotList.add(count);
			}
			log.info("END: *** listParkingSlotForEnquire Result *****" + slotList.toString());
		} catch (NullPointerException e) {
			log.info("exception: listParkingSlotForEnquire ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: listParkingSlotForEnquire ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: listParkingSlotForEnquire  DAO  END");
		}
		return slotList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getTpaList()
	// BEGIN CODE FOR NEW TRAILER PARKING APPLICATION

	@Override
	public List<TpaVO> getTpaList(Integer start, Integer limit, String sort, String dir, Map<String, Object> filters)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<TpaVO> tpaList = new ArrayList<TpaVO>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		String sql = "";

		try {
			log.info("START: getTpaList  DAO  Start start:" + start + "limit:" + limit + "sort:"
					+ CommonUtility.deNull(sort) + "dir:" + CommonUtility.deNull(dir) + "filters:"
					+ filters.toString());

			sb.append(" SELECT MVD.NO_OF_VEHICLES, MVD.VEH_CHAS_NO, MVD.CNTR_NBR, ");
			sb.append(" MA.MISC_SEQ_NBR, MA.REF_NBR, MA.ACCT_NBR, MA.BILL_NBR, ");
			sb.append(" MA.APP_STATUS, MA.APP_DTTM, ");
			sb.append(" MA.CONTACT_PERSON, MA.CONTACT_TEL, MA.CONTACT_EMAIL, ");
			sb.append(" MA.VV_CD, MA.VOID_REMARKS, MA.CUST_CD, ");
			sb.append(" MA.LAST_MODIFY_USER_ID, MA.LAST_MODIFY_DTTM, CC.CO_NM, ");
			sb.append(" LA.USER_NM AS LAST_UPDATED_BY, ");
			sb.append(" TO_CHAR(MA.LAST_MODIFY_DTTM, 'DD/MM/YYYY HH24MI') LAST_MODIFY_DATE_TIME, ");
			sb.append(" TO_CHAR(MA.APP_DTTM, 'DD/MM/YYYY HH24MI') APP_DATE_TIME, ");
			sb.append(" TO_CHAR(MV.ACTUAL_FR_DTTM, 'DD/MM/YYYY HH24MI') ACTUAL_FROM_DATE_TIME, ");
			sb.append(" TO_CHAR(MV.ACTUAL_TO_DTTM, 'DD/MM/YYYY HH24MI') ACTUAL_TO_DATE_TIME, ");
			sb.append(" TO_CHAR(MV.FR_DTTM, 'DD/MM/YYYY HH24MI') FROM_DATE_TIME, ");
			sb.append(" TO_CHAR(MV.TO_DTTM, 'DD/MM/YYYY HH24MI') TO_DATE_TIME, ");
			sb.append(" TO_CHAR(NVL2(MV.ACTUAL_FR_DTTM, MV.ACTUAL_FR_DTTM, ");
			sb.append(" MV.FR_DTTM),'DD/MM/YYYY HH24MI') TPA_FROM_DATE_TIME, ");
			sb.append(" TO_CHAR(NVL2(MV.ACTUAL_TO_DTTM, MV.ACTUAL_TO_DTTM, ");
			sb.append(" MV.TO_DTTM),'DD/MM/YYYY HH24MI') TPA_TO_DATE_TIME, ");
			sb.append(" NVL2(MV.ACTUAL_TO_DTTM, MV.ACTUAL_NBR_HOUR, MV.NBR_HOUR) DURATION_OF_STAY, ");
			sb.append(" MV.NBR_HOUR, MV.ACTUAL_NBR_HOUR, MV.TRAILER_TYPE, MV.TRAILER_SIZE, ");
			sb.append(" MV.PARK_REASON, MV.CARGO_TYPE, MV.PARK_REASON_CD, ");
			sb.append(" MTC.MISC_TYPE_NM AS REASON_FOR_APPLICATION ");
			sb.append(" FROM MISC_APP MA ");
			sb.append(" LEFT JOIN LOGON_ACCT LA ON MA.LAST_MODIFY_USER_ID = LA.LOGIN_ID ");
			sb.append(" INNER JOIN MISC_VEHICLE MV ON MA.MISC_SEQ_NBR = MV.MISC_SEQ_NBR ");
			sb.append(" LEFT JOIN ( ");
			sb.append(" SELECT ");
			sb.append(" MISC_SEQ_NBR, ");
			sb.append(" WM_CONCAT(CNTR_NBR)     AS CNTR_NBR, ");
			sb.append(" WM_CONCAT(VEH_CHAS_NBR) AS VEH_CHAS_NO, ");
			sb.append(" COUNT(*) AS NO_OF_VEHICLES ");
			sb.append(" FROM MISC_VEHICLE_DET ");
			sb.append(" GROUP BY MISC_SEQ_NBR ");
			sb.append(" ) MVD ON MV.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
			sb.append(" LEFT JOIN COMPANY_CODE CC ON MA.CUST_CD = CC.CO_CD ");
			sb.append(" LEFT JOIN MISC_TYPE_CODE MTC ON MTC.MISC_TYPE_CD = MV.PARK_REASON_CD ");
			sb.append(" AND MTC.CAT_CD = 'TPA_PKRSN' ");
			sb.append(" WHERE MA.APP_TYPE = 'TPA' ");

			if (filters.containsKey("co_cd")) {
				if (filters.get("co_cd").toString().trim().equalsIgnoreCase("JP")) {
					if (filters.containsKey("companyCode")) {
						if (!filters.get("companyCode").toString().trim().equalsIgnoreCase("")) {
							sb.append(" AND ");
							sb.append("UPPER(CC.CO_CD) = '")
									.append(filters.get("companyCode").toString().toUpperCase().trim()).append("'");
						}
					}
				} else {
					sb.append(" AND ");
					sb.append(" ( ");
					sb.append("	UPPER(CC.CO_CD) = '").append(filters.get("co_cd").toString().toUpperCase().trim())
							.append("'");
					sb.append(" AND ");
					sb.append(" MA.APP_STATUS <> 'D' ");
					sb.append(" ) ");
				}
			}

			if (filters.containsKey("referenceNo")) {
				if (!filters.get("referenceNo").toString().trim().equalsIgnoreCase("")) {
					sb.append(" AND ");
					sb.append("UPPER(MA.REF_NBR) LIKE :referenceNo ");
				}
			}

			if (filters.containsKey("applicationStatus")) {
				if (!filters.get("applicationStatus").toString().trim().equalsIgnoreCase("")) {
					if (!filters.get("applicationStatus").toString().trim().equalsIgnoreCase("All")) {
						if (filters.get("applicationStatus").toString().trim().equalsIgnoreCase("L")) {
							sb.append(" AND ");
							sb.append("MA.APP_STATUS IN ('D', 'A', 'S')");
						} else {
							sb.append(" AND ");
							sb.append("MA.APP_STATUS = '")
									.append(filters.get("applicationStatus").toString().toUpperCase().trim())
									.append("'");
						}

					}
				}
			}

			if (filters.containsKey("vehicleTrailerRegistrationNo")) {
				if (!filters.get("vehicleTrailerRegistrationNo").toString().trim().equalsIgnoreCase("")) {
					sb.append(" AND ");
					sb.append("UPPER(MVD.VEH_CHAS_NO) LIKE :vehicleTrailerRegistrationNo ");
				}
			}

			if (filters.containsKey("containerNo")) {
				if (!filters.get("containerNo").toString().trim().equalsIgnoreCase("")) {
					sb.append(" AND ");
					sb.append("UPPER(MVD.CNTR_NBR) LIKE :containerNo ");
				}
			}

			if (filters.containsKey("applicationDateFromTpa") && filters.containsKey("applicationDateToTpa")) {
				if (!filters.get("applicationDateFromTpa").toString().trim().equalsIgnoreCase("")
						&& !filters.get("applicationDateToTpa").toString().trim().equalsIgnoreCase("")) {
					sb.append(" AND ");
					sb.append("MA.APP_DTTM BETWEEN TIMESTAMP'");
					sb.append(filters.get("applicationDateFromTpa").toString() + "'");
					sb.append(" AND TIMESTAMP'");
					sb.append(filters.get("applicationDateToTpa").toString() + "'");
				}
			}

			if (sort.equalsIgnoreCase("nbr_of_blocks")) {
				sb.append(" ORDER BY DURATION_OF_STAY ").append(dir.toUpperCase());
			} else if (sort.equalsIgnoreCase("app_status_nm")) {
				sb.append(" ORDER BY APP_STATUS ").append(dir.toUpperCase());
			} else if (sort.equalsIgnoreCase("tpa_from_date_time")) {
				sb.append(" ORDER BY FR_DTTM ").append(dir.toUpperCase());
			} else if (sort.equalsIgnoreCase("tpa_to_date_time")) {
				sb.append(" ORDER BY TO_DTTM ").append(dir.toUpperCase());
			} else if (sort.equalsIgnoreCase("last_modify_date_time")) {
				sb.append(" ORDER BY LAST_MODIFY_DTTM ").append(dir.toUpperCase());
			} else {
				sb.append(" ORDER BY ").append(sort.toUpperCase()).append(" ").append(dir.toUpperCase());
			}

			sql = sb.toString();

			if (start >= 0 && limit > 0) {
				sql = getPaginationString(sql, start, limit);
			}

			log.info(" ***getTpaList SQL *****" + sql);

			paramMap = setParametersForTpaListSql(filters);

			log.info(" *** paramMap: *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			int hoursPerBlock = Integer.parseInt(filters.get("hoursPerBlock").toString());

			TpaVO vo = null;
			while (rs.next()) {
				vo = new TpaVO();
				vo.setAcct_nbr(rs.getString("ACCT_NBR"));
				vo.setActual_from_date_time(rs.getString("ACTUAL_FROM_DATE_TIME"));
				vo.setActual_to_date_time(rs.getString("ACTUAL_TO_DATE_TIME"));
				vo.setFrom_date_time(rs.getString("FROM_DATE_TIME"));
				vo.setTo_date_time(rs.getString("TO_DATE_TIME"));
				vo.setTpa_from_date_time(rs.getString("TPA_FROM_DATE_TIME"));
				vo.setTpa_to_date_time(rs.getString("TPA_TO_DATE_TIME"));
				vo.setApp_status(rs.getString("APP_STATUS"));
				vo.setBill_nbr(rs.getString("BILL_NBR"));
				vo.setCargo_type(rs.getString("CARGO_TYPE"));
				vo.setCo_nm(rs.getString("CO_NM"));
				vo.setCon_email(rs.getString("CONTACT_EMAIL"));
				vo.setCon_person(rs.getString("CONTACT_PERSON"));
				vo.setCon_tel(rs.getString("CONTACT_TEL"));
				vo.setCust_cd(rs.getString("CUST_CD"));
				vo.setNbr_hour(rs.getDouble("NBR_HOUR"));
				vo.setActual_nbr_hour(rs.getDouble("ACTUAL_NBR_HOUR"));
				vo.setDuration_of_stay(rs.getDouble("DURATION_OF_STAY"));
				vo.setNbr_of_blocks(Math.ceil(rs.getDouble("DURATION_OF_STAY") / hoursPerBlock));
				vo.setActual_nbr_of_blocks(Math.ceil(vo.getActual_nbr_hour() / hoursPerBlock));
				vo.setMisc_seq_nbr(rs.getString("MISC_SEQ_NBR"));
				vo.setRef_nbr(rs.getString("REF_NBR"));
				vo.setNo_of_vehicles(rs.getInt("NO_OF_VEHICLES"));
				vo.setPark_reason(rs.getString("PARK_REASON"));
				vo.setPark_reason_cd(rs.getString("PARK_REASON_CD"));
				vo.setReason_for_application(rs.getString("REASON_FOR_APPLICATION"));
				vo.setTrailer_type(rs.getString("TRAILER_TYPE"));
				vo.setTrailer_size(rs.getInt("TRAILER_SIZE"));
				vo.setVoid_remarks(rs.getString("VOID_REMARKS"));
				vo.setVv_cd(rs.getString("VV_CD"));
				vo.setVeh_chas_no(rs.getString("VEH_CHAS_NO"));
				vo.setLast_updated_by(rs.getString("LAST_UPDATED_BY"));
				vo.setLast_modify_user_id(rs.getString("LAST_MODIFY_USER_ID"));
				vo.setLast_modify_date_time(rs.getString("LAST_MODIFY_DATE_TIME"));
				vo.setLast_modify_dttm(rs.getTimestamp("LAST_MODIFY_DTTM"));
				vo.setApp_date_time(rs.getString("APP_DATE_TIME"));

				tpaList.add(vo);
			}
			log.info("END: *** getTpaList Result *****" + tpaList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getTpaList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getTpaList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTpaList  DAO  END");
		}
		return tpaList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getCountRecords_TpaList()

	@Override
	public int getCountRecords_TpaList(Integer start, Integer limit, String sort, String dir,
			Map<String, Object> filters) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		String sql = "";
		int size = 0;

		try {
			log.info("START: getCountRecords_TpaList  DAO  Start start:" + start + "limit:" + limit + "sort:"
					+ CommonUtility.deNull(sort) + "dir:" + CommonUtility.deNull(dir) + "filters:"
					+ filters.toString());

			sb.append(" SELECT COUNT(*) ");
			sb.append(" FROM MISC_APP MA ");
			sb.append(" LEFT JOIN LOGON_ACCT LA ON MA.LAST_MODIFY_USER_ID = LA.LOGIN_ID ");
			sb.append(" INNER JOIN MISC_VEHICLE MV ON MA.MISC_SEQ_NBR = MV.MISC_SEQ_NBR ");
			sb.append(" LEFT JOIN ( ");
			sb.append(" SELECT ");
			sb.append(" MISC_SEQ_NBR, ");
			sb.append(" WM_CONCAT(CNTR_NBR)     AS CNTR_NBR, ");
			sb.append(" WM_CONCAT(VEH_CHAS_NBR) AS VEH_CHAS_NO, ");
			sb.append(" COUNT(*) AS NO_OF_VEHICLES ");
			sb.append(" FROM MISC_VEHICLE_DET ");
			sb.append(" GROUP BY MISC_SEQ_NBR ");
			sb.append(" ) MVD ON MV.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
			sb.append(" LEFT JOIN COMPANY_CODE CC ON MA.CUST_CD = CC.CO_CD ");
			sb.append(" LEFT JOIN MISC_TYPE_CODE MTC ON MTC.MISC_TYPE_CD = MV.PARK_REASON_CD ");
			sb.append(" AND MTC.CAT_CD = 'TPA_PKRSN' ");
			sb.append(" WHERE MA.APP_TYPE = 'TPA' ");

			if (filters.containsKey("co_cd")) {
				if (filters.get("co_cd").toString().trim().equalsIgnoreCase("JP")) {
					if (filters.containsKey("companyCode")) {
						if (!filters.get("companyCode").toString().trim().equalsIgnoreCase("")) {
							sb.append(" AND ");
							sb.append("UPPER(CC.CO_CD) = '" + filters.get("companyCode").toString().toUpperCase().trim()
									+ "'");
						}
					}
				} else {
					sb.append(" AND ");
					sb.append(" ( ");
					sb.append("	UPPER(CC.CO_CD) = '" + filters.get("co_cd").toString().toUpperCase().trim() + "'");
					sb.append(" AND ");
					sb.append(" MA.APP_STATUS <> 'D' ");
					sb.append(" ) ");
				}
			}

			if (filters.containsKey("referenceNo")) {
				if (!filters.get("referenceNo").toString().trim().equalsIgnoreCase("")) {
					sb.append(" AND ");
					sb.append("UPPER(MA.REF_NBR) LIKE :referenceNo ");
				}
			}

			if (filters.containsKey("applicationStatus")) {
				if (!filters.get("applicationStatus").toString().trim().equalsIgnoreCase("")) {
					if (!filters.get("applicationStatus").toString().trim().equalsIgnoreCase("All")) {
						if (filters.get("applicationStatus").toString().trim().equalsIgnoreCase("L")) {
							sb.append(" AND ");
							sb.append("MA.APP_STATUS IN ('D', 'A', 'S')");
						} else {
							sb.append(" AND ");
							sb.append("MA.APP_STATUS = '"
									+ filters.get("applicationStatus").toString().toUpperCase().trim() + "'");
						}

					}
				}
			}

			if (filters.containsKey("vehicleTrailerRegistrationNo")) {
				if (!filters.get("vehicleTrailerRegistrationNo").toString().trim().equalsIgnoreCase("")) {
					sb.append(" AND ");
					sb.append("UPPER(MVD.VEH_CHAS_NO) LIKE :vehicleTrailerRegistrationNo ");
				}
			}

			if (filters.containsKey("containerNo")) {
				if (!filters.get("containerNo").toString().trim().equalsIgnoreCase("")) {
					sb.append(" AND ");
					sb.append("UPPER(MVD.CNTR_NBR) LIKE :containerNo ");
				}
			}

			if (filters.containsKey("applicationDateFromTpa") && filters.containsKey("applicationDateToTpa")) {
				if (!filters.get("applicationDateFromTpa").toString().trim().equalsIgnoreCase("")
						&& !filters.get("applicationDateToTpa").toString().trim().equalsIgnoreCase("")) {
					sb.append(" AND ");
					sb.append("MA.APP_DTTM BETWEEN TIMESTAMP'");
					sb.append(filters.get("applicationDateFromTpa").toString() + "'");
					sb.append(" AND TIMESTAMP'");
					sb.append(filters.get("applicationDateToTpa").toString() + "'");
				}
			}
			sql = sb.toString();
			log.info(" ***getCountRecords_TpaList SQL *****" + sql);

			paramMap = setParametersForTpaListSql(filters);
			log.info(" *** paramMap: *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				size = rs.getInt(1);
			}
			log.info("END: *** getCountRecords_TpaList Result *****" + size);
		} catch (NullPointerException e) {
			log.info("exception: getCountRecords_TpaList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getCountRecords_TpaList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCountRecords_TpaList  DAO  END");
		}
		return size;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getActiveTpaList()

	@Override
	public List<TpaVO> getActiveTpaList(Integer start, Integer limit, String sort, String dir,
			Map<String, Object> filters) throws BusinessException {
		List<TpaVO> tpaList = new ArrayList<TpaVO>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getActiveTpaList  DAO  Start start:" + start + "limit:" + limit + "sort:"
					+ CommonUtility.deNull(sort) + "dir:" + CommonUtility.deNull(dir) + "filters:"
					+ filters.toString());

			sb.append(" SELECT ");
			sb.append(" MVD.NO_OF_VEHICLES, MVD.VEH_CHAS_NO, MVD.AREA_CD, MVD.SLOT_NBR, MVD.CNTR_NBR, ");
			sb.append(" MA.MISC_SEQ_NBR, MA.REF_NBR, MA.ACCT_NBR, MA.BILL_NBR, MA.APP_STATUS, MA.APP_DTTM, ");
			sb.append(" MA.CONTACT_PERSON, MA.CONTACT_TEL, MA.CONTACT_EMAIL, ");
			sb.append(" MA.VV_CD, MA.VOID_REMARKS, MA.CUST_CD, ");
			sb.append(" MA.LAST_MODIFY_USER_ID, MA.LAST_MODIFY_DTTM, ");
			sb.append(" CC.CO_NM, LA.USER_NM AS LAST_UPDATED_BY, ");
			sb.append(" TO_CHAR(MA.LAST_MODIFY_DTTM, 'DD/MM/YYYY HH24:MI') LAST_MODIFY_DATE_TIME, ");
			sb.append(" TO_CHAR(MA.APP_DTTM, 'DD/MM/YYYY HH24:MI') APP_DATE_TIME, ");
			sb.append(" TO_CHAR(MV.ACTUAL_FR_DTTM, 'DD/MM/YYYY HH24:MI') ACTUAL_FROM_DATE_TIME, ");
			sb.append(" TO_CHAR(MV.ACTUAL_TO_DTTM, 'DD/MM/YYYY HH24:MI') ACTUAL_TO_DATE_TIME, ");
			sb.append(" TO_CHAR(MV.FR_DTTM, 'DD/MM/YYYY HH24:MI') FROM_DATE_TIME, ");
			sb.append(" TO_CHAR(MV.TO_DTTM, 'DD/MM/YYYY HH24:MI') TO_DATE_TIME, ");
			sb.append(" TO_CHAR(NVL2(MV.ACTUAL_FR_DTTM, MV.ACTUAL_FR_DTTM, ");
			sb.append(" MV.FR_DTTM),'DD/MM/YYYY HH24:MI') TPA_FROM_DATE_TIME, ");
			sb.append(" TO_CHAR(NVL2(MV.ACTUAL_TO_DTTM, MV.ACTUAL_TO_DTTM, MV.TO_DTTM), ");
			sb.append(" 'DD/MM/YYYY HH24:MI') TPA_TO_DATE_TIME, ");
			sb.append(" NVL2(MV.ACTUAL_TO_DTTM, MV.ACTUAL_NBR_HOUR, MV.NBR_HOUR) DURATION_OF_STAY, ");
			sb.append(" MV.NBR_HOUR, MV.ACTUAL_NBR_HOUR, MV.TRAILER_TYPE, MV.TRAILER_SIZE, ");
			sb.append(" MV.PARK_REASON, MV.CARGO_TYPE, MV.PARK_REASON_CD, ");
			sb.append(" MTC.MISC_TYPE_NM AS REASON_FOR_APPLICATION ");
			sb.append(" FROM MISC_APP MA ");
			sb.append(" LEFT JOIN LOGON_ACCT LA ON MA.LAST_MODIFY_USER_ID = LA.LOGIN_ID ");
			sb.append(" INNER JOIN MISC_VEHICLE MV ON MA.MISC_SEQ_NBR = MV.MISC_SEQ_NBR ");
			sb.append(" LEFT JOIN ( ");
			sb.append(" SELECT ");
			sb.append(" MISC_SEQ_NBR, ");
			sb.append(" WM_CONCAT(CNTR_NBR)     AS CNTR_NBR, ");
			sb.append(" WM_CONCAT(VEH_CHAS_NBR) AS VEH_CHAS_NO, ");
			sb.append(" WM_CONCAT(AREA_CD) AS AREA_CD, ");
			sb.append(" WM_CONCAT(SLOT_NBR) AS SLOT_NBR, ");
			sb.append(" COUNT(*) AS NO_OF_VEHICLES ");
			sb.append(" FROM MISC_VEHICLE_DET ");
			sb.append(" GROUP BY MISC_SEQ_NBR ");
			sb.append(" ) MVD ON MV.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
			sb.append(" LEFT JOIN COMPANY_CODE CC ON MA.CUST_CD = CC.CO_CD ");
			sb.append(" LEFT JOIN MISC_TYPE_CODE MTC ON MTC.MISC_TYPE_CD = MV.PARK_REASON_CD ");
			sb.append(" AND MTC.CAT_CD = 'TPA_PKRSN' ");
			sb.append(" WHERE MA.APP_TYPE = 'TPA' ");
			sb.append(" AND MA.APP_STATUS = 'A' ");
			sb.append(" AND MV.FR_DTTM <= SYSDATE AND MV.TO_DTTM >= SYSDATE ");
			sb.append(" ORDER BY ").append(sort.toUpperCase()).append(" " + dir.toUpperCase());

			log.info(" ***getActiveTpaList SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			int hoursPerBlock = Integer.parseInt(filters.get("hoursPerBlock").toString());

			TpaVO vo = null;
			while (rs.next()) {
				vo = new TpaVO();
				vo.setAcct_nbr(rs.getString("ACCT_NBR"));
				vo.setActual_from_date_time(rs.getString("ACTUAL_FROM_DATE_TIME"));
				vo.setActual_to_date_time(rs.getString("ACTUAL_TO_DATE_TIME"));
				vo.setFrom_date_time(rs.getString("FROM_DATE_TIME"));
				vo.setTo_date_time(rs.getString("TO_DATE_TIME"));
				vo.setTpa_from_date_time(rs.getString("TPA_FROM_DATE_TIME"));
				vo.setTpa_to_date_time(rs.getString("TPA_TO_DATE_TIME"));
				vo.setApp_status(rs.getString("APP_STATUS"));
				vo.setBill_nbr(rs.getString("BILL_NBR"));
				vo.setCargo_type(rs.getString("CARGO_TYPE"));
				vo.setCo_nm(rs.getString("CO_NM"));
				vo.setCon_email(rs.getString("CONTACT_EMAIL"));
				vo.setCon_person(rs.getString("CONTACT_PERSON"));
				vo.setCon_tel(rs.getString("CONTACT_TEL"));
				vo.setCust_cd(rs.getString("CUST_CD"));
				vo.setNbr_hour(rs.getDouble("NBR_HOUR"));
				vo.setActual_nbr_hour(rs.getDouble("ACTUAL_NBR_HOUR"));
				vo.setDuration_of_stay(rs.getDouble("DURATION_OF_STAY"));
				vo.setNbr_of_blocks(Math.ceil(rs.getDouble("DURATION_OF_STAY") / hoursPerBlock));
				vo.setActual_nbr_of_blocks(Math.ceil(vo.getActual_nbr_hour() / hoursPerBlock));
				vo.setMisc_seq_nbr(rs.getString("MISC_SEQ_NBR"));
				vo.setRef_nbr(rs.getString("REF_NBR"));
				vo.setNo_of_vehicles(rs.getInt("NO_OF_VEHICLES"));
				vo.setPark_reason(rs.getString("PARK_REASON"));
				vo.setPark_reason_cd(rs.getString("PARK_REASON_CD"));
				vo.setReason_for_application(rs.getString("REASON_FOR_APPLICATION"));
				vo.setTrailer_type(rs.getString("TRAILER_TYPE"));
				vo.setTrailer_size(rs.getInt("TRAILER_SIZE"));
				vo.setVoid_remarks(rs.getString("VOID_REMARKS"));
				vo.setVv_cd(rs.getString("VV_CD"));
				vo.setVeh_chas_no(rs.getString("VEH_CHAS_NO"));
				vo.setLast_updated_by(rs.getString("LAST_UPDATED_BY"));
				vo.setLast_modify_user_id(rs.getString("LAST_MODIFY_USER_ID"));
				vo.setLast_modify_date_time(rs.getString("LAST_MODIFY_DATE_TIME"));
				vo.setLast_modify_dttm(rs.getTimestamp("LAST_MODIFY_DTTM"));
				vo.setApp_date_time(rs.getString("APP_DATE_TIME"));

				// HaiTTH1 added 20/11/2013
				String areaList = rs.getString("AREA_CD");
				// String slotList = rs.getString("SLOT_NBR");
				String[] areaArr = areaList.split(",");
				// String[] slotArr = slotList.split(",");
				String assign_area_slot = "";
				for (int i = 0; i < areaArr.length; i++) {
					assign_area_slot += areaArr[i].trim();// + "/" + slotArr[i].trim();
					if (i < areaArr.length - 1) {
						assign_area_slot += ",";
					}
				}
				vo.setAssigned_area_slot(assign_area_slot);
				// HaiTTH1 added 20/11/2013
				tpaList.add(vo);
			}
			log.info("END: *** getActiveTpaList Result *****" + tpaList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getActiveTpaList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getActiveTpaList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getActiveTpaList  DAO  END");
		}

		return tpaList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getTpaListForDownloading()

	@Override
	public List<TpaVO> getTpaListForDownloading(Integer start, Integer limit, String sort, String dir,
			Map<String, Object> filters) throws BusinessException {

		List<TpaVO> tpaList = new ArrayList<TpaVO>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getTpaListForDownloading  DAO  Start start:" + start + "limit:" + limit + "sort:"
					+ CommonUtility.deNull(sort) + "dir:" + CommonUtility.deNull(dir) + "filters:"
					+ filters.toString());

			sb.append(" SELECT ");
			sb.append(" MVD.NO_OF_VEHICLES, MVD.VEH_CHAS_NO, MVD.CNTR_NBR, ");
			sb.append(" MA.MISC_SEQ_NBR, MA.REF_NBR, MA.ACCT_NBR, MA.BILL_NBR, MA.APP_STATUS, MA.APP_DTTM, ");
			sb.append(" MA.CONTACT_PERSON, MA.CONTACT_TEL, MA.CONTACT_EMAIL, MA.VV_CD, MA.VOID_REMARKS, MA.CUST_CD, ");
			sb.append(" MA.LAST_MODIFY_USER_ID, MA.LAST_MODIFY_DTTM, CC.CO_NM, LA.USER_NM AS LAST_UPDATED_BY, ");
			sb.append(" TO_CHAR(MA.LAST_MODIFY_DTTM, 'DD/MM/YYYY HH24:MI') LAST_MODIFY_DATE_TIME, ");
			sb.append(" TO_CHAR(MA.APP_DTTM, 'DD/MM/YYYY HH24:MI') APP_DATE_TIME, ");
			sb.append(" TO_CHAR(MV.ACTUAL_FR_DTTM, 'DD/MM/YYYY HH24:MI') ACTUAL_FROM_DATE_TIME, ");
			sb.append(" TO_CHAR(MV.ACTUAL_TO_DTTM, 'DD/MM/YYYY HH24:MI') ACTUAL_TO_DATE_TIME, ");
			sb.append(" TO_CHAR(MV.FR_DTTM, 'DD/MM/YYYY HH24:MI') FROM_DATE_TIME, ");
			sb.append(" TO_CHAR(MV.TO_DTTM, 'DD/MM/YYYY HH24:MI') TO_DATE_TIME, ");
			sb.append(" TO_CHAR(NVL2(MV.ACTUAL_FR_DTTM, MV.ACTUAL_FR_DTTM, ");
			sb.append(" MV.FR_DTTM),'DD/MM/YYYY HH24:MI') TPA_FROM_DATE_TIME, ");
			sb.append(" TO_CHAR(NVL2(MV.ACTUAL_TO_DTTM, MV.ACTUAL_TO_DTTM, ");
			sb.append(" MV.TO_DTTM),'DD/MM/YYYY HH24:MI') TPA_TO_DATE_TIME, ");
			sb.append(" NVL2(MV.ACTUAL_TO_DTTM, MV.ACTUAL_NBR_HOUR, MV.NBR_HOUR) DURATION_OF_STAY, ");
			sb.append(" MV.NBR_HOUR, MV.ACTUAL_NBR_HOUR, MV.TRAILER_TYPE, MV.TRAILER_SIZE, ");
			sb.append(" MV.PARK_REASON, MV.CARGO_TYPE, MV.PARK_REASON_CD, ");
			sb.append(" MTC.MISC_TYPE_NM AS REASON_FOR_APPLICATION ");
			sb.append(" FROM MISC_APP MA ");
			sb.append(" LEFT JOIN LOGON_ACCT LA ON MA.LAST_MODIFY_USER_ID = LA.LOGIN_ID ");
			sb.append(" INNER JOIN MISC_VEHICLE MV ON MA.MISC_SEQ_NBR = MV.MISC_SEQ_NBR ");
			sb.append(" LEFT JOIN ( ");
			sb.append(" SELECT ");
			sb.append(" MISC_SEQ_NBR, ");
			sb.append(" WM_CONCAT(CNTR_NBR)     AS CNTR_NBR, ");
			sb.append(" WM_CONCAT(VEH_CHAS_NBR) AS VEH_CHAS_NO, ");
			sb.append(" COUNT(*) AS NO_OF_VEHICLES ");
			sb.append(" FROM MISC_VEHICLE_DET ");
			sb.append(" GROUP BY MISC_SEQ_NBR ");
			sb.append(" ) MVD ON MV.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
			sb.append(" LEFT JOIN COMPANY_CODE CC ON MA.CUST_CD = CC.CO_CD ");
			sb.append(" LEFT JOIN MISC_TYPE_CODE MTC ON MTC.MISC_TYPE_CD = MV.PARK_REASON_CD ");
			sb.append(" AND MTC.CAT_CD = 'TPA_PKRSN' ");
			sb.append(" WHERE MA.APP_TYPE = 'TPA' ");

			if (filters.containsKey("co_cd")) {
				if (filters.get("co_cd").toString().trim().equalsIgnoreCase("JP")) {
					if (filters.containsKey("companyCode")) {
						if (!filters.get("companyCode").toString().trim().equalsIgnoreCase("")) {
							sb.append(" AND ");
							sb.append("UPPER(CC.CO_CD) = '" + filters.get("companyCode").toString().toUpperCase().trim()
									+ "'");
						}
					}
				} else {
					sb.append(" AND ");
					sb.append(" ( ");
					sb.append("	UPPER(CC.CO_CD) = '" + filters.get("co_cd").toString().toUpperCase().trim() + "'");
					sb.append(" AND ");
					sb.append(" MA.APP_STATUS <> 'D' ");
					sb.append(" ) ");
				}
			}

			if (filters.containsKey("referenceNo")) {
				if (!filters.get("referenceNo").toString().trim().equalsIgnoreCase("")) {
					sb.append(" AND ");
					sb.append("UPPER(MA.REF_NBR) LIKE :referenceNo ");
				}
			}

			if (filters.containsKey("applicationStatus")) {
				if (!filters.get("applicationStatus").toString().trim().equalsIgnoreCase("")) {
					if (!filters.get("applicationStatus").toString().trim().equalsIgnoreCase("All")) {
						if (filters.get("applicationStatus").toString().trim().equalsIgnoreCase("L")) {
							sb.append(" AND ");
							sb.append("MA.APP_STATUS IN ('D', 'A', 'S')");
						} else {
							sb.append(" AND ");
							sb.append("MA.APP_STATUS = '"
									+ filters.get("applicationStatus").toString().toUpperCase().trim() + "'");
						}

					}
				}
			}

			if (filters.containsKey("vehicleTrailerRegistrationNo")) {
				if (!filters.get("vehicleTrailerRegistrationNo").toString().trim().equalsIgnoreCase("")) {
					sb.append(" AND ");
					sb.append("UPPER(MVD.VEH_CHAS_NO) LIKE :vehicleTrailerRegistrationNo ");
				}
			}

			if (filters.containsKey("containerNo")) {
				if (!filters.get("containerNo").toString().trim().equalsIgnoreCase("")) {
					sb.append(" AND ");
					sb.append("UPPER(MVD.CNTR_NBR) LIKE :containerNo ");
				}
			}

			if (filters.containsKey("applicationDateFromTpa") && filters.containsKey("applicationDateToTpa")) {
				if (!filters.get("applicationDateFromTpa").toString().trim().equalsIgnoreCase("")
						&& !filters.get("applicationDateToTpa").toString().trim().equalsIgnoreCase("")) {
					sb.append(" AND ");
					sb.append("MA.APP_DTTM BETWEEN TIMESTAMP'");
					sb.append(filters.get("applicationDateFromTpa").toString() + "'");
					sb.append(" AND TIMESTAMP'");
					sb.append(filters.get("applicationDateToTpa").toString() + "'");
				}
			}

			if (sort.equalsIgnoreCase("nbr_of_blocks")) {
				sb.append(" ORDER BY DURATION_OF_STAY ").append(dir.toUpperCase());
			} else if (sort.equalsIgnoreCase("app_status_nm")) {
				sb.append(" ORDER BY APP_STATUS ").append(dir.toUpperCase());
			} else if (sort.equalsIgnoreCase("tpa_from_date_time")) {
				sb.append(" ORDER BY FR_DTTM ").append(dir.toUpperCase());
			} else if (sort.equalsIgnoreCase("tpa_to_date_time")) {
				sb.append(" ORDER BY TO_DTTM ").append(dir.toUpperCase());
			} else if (sort.equalsIgnoreCase("last_modify_date_time")) {
				sb.append(" ORDER BY LAST_MODIFY_DTTM ").append(dir.toUpperCase());
			} else {
				sb.append(" ORDER BY ").append(sort.toUpperCase()).append(" ").append(dir.toUpperCase());
			}

			if (start >= 0 && limit > 0) {
				sql = getPaginationString(sql, start, limit);
			}

			log.info(" ***getTpaListForDownloading SQL *****" + sb.toString());

			paramMap = setParametersForTpaListSql(filters);
			log.info(" *** paramMap: *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			int hoursPerBlock = Integer.parseInt(filters.get("hoursPerBlock").toString());

			TpaVO vo = null;
			while (rs.next()) {
				vo = new TpaVO();
				vo.setAcct_nbr(rs.getString("ACCT_NBR"));
				vo.setActual_from_date_time(rs.getString("ACTUAL_FROM_DATE_TIME"));
				vo.setActual_to_date_time(rs.getString("ACTUAL_TO_DATE_TIME"));
				vo.setFrom_date_time(rs.getString("FROM_DATE_TIME"));
				vo.setTo_date_time(rs.getString("TO_DATE_TIME"));
				vo.setTpa_from_date_time(rs.getString("TPA_FROM_DATE_TIME"));
				vo.setTpa_to_date_time(rs.getString("TPA_TO_DATE_TIME"));
				vo.setApp_status(rs.getString("APP_STATUS"));
				vo.setBill_nbr(rs.getString("BILL_NBR"));
				vo.setCargo_type(rs.getString("CARGO_TYPE"));
				vo.setCo_nm(rs.getString("CO_NM"));
				vo.setCon_email(rs.getString("CONTACT_EMAIL"));
				vo.setCon_person(rs.getString("CONTACT_PERSON"));
				vo.setCon_tel(rs.getString("CONTACT_TEL"));
				vo.setCust_cd(rs.getString("CUST_CD"));
				vo.setNbr_hour(rs.getDouble("NBR_HOUR"));
				vo.setActual_nbr_hour(rs.getDouble("ACTUAL_NBR_HOUR"));
				vo.setDuration_of_stay(rs.getDouble("DURATION_OF_STAY"));
				vo.setNbr_of_blocks(Math.ceil(rs.getDouble("DURATION_OF_STAY") / hoursPerBlock));
				vo.setActual_nbr_of_blocks(Math.ceil(vo.getActual_nbr_hour() / hoursPerBlock));
				vo.setMisc_seq_nbr(rs.getString("MISC_SEQ_NBR"));
				vo.setRef_nbr(rs.getString("REF_NBR"));
				vo.setNo_of_vehicles(rs.getInt("NO_OF_VEHICLES"));
				vo.setPark_reason(rs.getString("PARK_REASON"));
				vo.setPark_reason_cd(rs.getString("PARK_REASON_CD"));
				vo.setReason_for_application(rs.getString("REASON_FOR_APPLICATION"));
				vo.setTrailer_type(rs.getString("TRAILER_TYPE"));
				vo.setTrailer_size(rs.getInt("TRAILER_SIZE"));
				vo.setVoid_remarks(rs.getString("VOID_REMARKS"));
				vo.setVv_cd(rs.getString("VV_CD"));
				vo.setVeh_chas_no(rs.getString("VEH_CHAS_NO"));
				vo.setLast_updated_by(rs.getString("LAST_UPDATED_BY"));
				vo.setLast_modify_user_id(rs.getString("LAST_MODIFY_USER_ID"));
				vo.setLast_modify_date_time(rs.getString("LAST_MODIFY_DATE_TIME"));
				vo.setLast_modify_dttm(rs.getTimestamp("LAST_MODIFY_DTTM"));
				vo.setApp_date_time(rs.getString("APP_DATE_TIME"));

				tpaList.add(vo);
			}
			log.info("END: *** getTpaListForDownloading Result *****" + tpaList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getTpaListForDownloading ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getTpaListForDownloading ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTpaListForDownloading  DAO  END");
		}
		return tpaList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getTpaVehicleDetails()

	@Override
	public List<VehicleDetailsVO> getTpaVehicleDetails(Integer start, Integer limit, String sort, String dir,
			Map<String, Object> filters) throws BusinessException {
		List<VehicleDetailsVO> tpaVehicleDetailsList = new ArrayList<VehicleDetailsVO>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getTpaVehicleDetails  DAO  Start start:" + start + "limit:" + limit + "sort:"
					+ CommonUtility.deNull(sort) + "dir:" + CommonUtility.deNull(dir) + "filters:"
					+ filters.toString());

			sb.append(" SELECT ");
			sb.append(" MVD.MISC_SEQ_NBR, MVD.ITEM_NBR, MVD.VEH_CHAS_NBR, ");
			sb.append(" MVD.CNTR_NBR, MVD.CNTR_CRG_STATUS, ");
			sb.append(" MVD.ASN_NBR, MVD.REMARKS, MVD.PREF_AREA_CD, MVD.AREA_CD, MVD.SLOT_NBR, ");
			sb.append(" MVD.LAST_MODIFY_USER_ID, MVD.LAST_MODIFY_DTTM, ");
			sb.append(" M1.MISC_TYPE_NM CNTR_CRG_STATUS_NM ");
			sb.append(" FROM MISC_VEHICLE_DET MVD ");
			sb.append(" LEFT JOIN MISC_TYPE_CODE M1 ON M1.MISC_TYPE_CD = ");
			sb.append(" MVD.CNTR_CRG_STATUS AND M1.CAT_CD = 'PURP_CD' ");
			sb.append(" WHERE MVD.MISC_SEQ_NBR = :miscSeqNbr ");
			sb.append(" ORDER BY ").append(sort.toUpperCase()).append(" " + dir.toUpperCase());

			log.info(" ***getTpaVehicleDetails SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", filters.get("misc_seq_nbr").toString().trim());
			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			VehicleDetailsVO vo = null;
			while (rs.next()) {
				vo = new VehicleDetailsVO();
				vo.setItem_nbr(rs.getInt("ITEM_NBR"));
				vo.setMisc_seq_nbr(rs.getString("MISC_SEQ_NBR"));
				vo.setVeh_chas_nbr(rs.getString("VEH_CHAS_NBR"));
				vo.setCntr_nbr(rs.getString("CNTR_NBR"));
				vo.setAsn_nbr(rs.getString("ASN_NBR"));
				vo.setCntr_crg_status(rs.getString("CNTR_CRG_STATUS"));

				if (TpaConstants.TPA_CNTR_CRG_STATUS_L.equalsIgnoreCase(vo.getCntr_crg_status())) {
					vo.setCntr_crg_status_nm(TpaConstants.TPA_CNTR_CRG_STATUS_L_NM);
				} else if (TpaConstants.TPA_CNTR_CRG_STATUS_T.equalsIgnoreCase(vo.getCntr_crg_status())) {
					vo.setCntr_crg_status_nm(TpaConstants.TPA_CNTR_CRG_STATUS_T_NM);
				} else if (TpaConstants.TPA_CNTR_CRG_STATUS_R.equalsIgnoreCase(vo.getCntr_crg_status())) {
					vo.setCntr_crg_status_nm(TpaConstants.TPA_CNTR_CRG_STATUS_R_NM);
				} else {
					vo.setCntr_crg_status_nm(rs.getString("CNTR_CRG_STATUS_NM"));
				}

				vo.setPref_area_cd(rs.getString("PREF_AREA_CD"));
				vo.setArea_cd(rs.getString("AREA_CD"));
				vo.setSlot_nbr(rs.getString("SLOT_NBR"));
				vo.setRemarks(rs.getString("REMARKS"));

				tpaVehicleDetailsList.add(vo);
			}
			log.info("END: *** getTpaVehicleDetails Result *****" + tpaVehicleDetailsList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getTpaVehicleDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getTpaVehicleDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTpaVehicleDetails  DAO  END");
		}
		return tpaVehicleDetailsList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getTpaVehicleDetailsByMiscSeqNumber()

	@Override
	public List<VehicleDetailsVO> getTpaVehicleDetailsByMiscSeqNumber(String miscSeqNumber) throws BusinessException {
		List<VehicleDetailsVO> tpaVehicleDetailsList = new ArrayList<VehicleDetailsVO>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getTpaVehicleDetailsByMiscSeqNumber  DAO  Start miscSeqNumber:"
					+ CommonUtility.deNull(miscSeqNumber));

			sb.append(" SELECT ");
			sb.append(" MVD.MISC_SEQ_NBR, MVD.ITEM_NBR, MVD.VEH_CHAS_NBR, ");
			sb.append(" MVD.CNTR_NBR, MVD.CNTR_CRG_STATUS, ");
			sb.append(" MVD.ASN_NBR, MVD.REMARKS, MVD.PREF_AREA_CD, MVD.AREA_CD, MVD.SLOT_NBR, ");
			sb.append(" MVD.LAST_MODIFY_USER_ID, MVD.LAST_MODIFY_DTTM, ");
			sb.append(" M1.MISC_TYPE_NM CNTR_CRG_STATUS_NM ");
			sb.append(" FROM MISC_VEHICLE_DET MVD ");
			sb.append(" LEFT JOIN MISC_TYPE_CODE M1 ON M1.MISC_TYPE_CD = ");
			sb.append(" MVD.CNTR_CRG_STATUS AND M1.CAT_CD = 'PURP_CD' ");
			sb.append(" WHERE MVD.MISC_SEQ_NBR = :miscSeqNumber ");

			log.info(" ***getTpaVehicleDetailsByMiscSeqNumber SQL *****" + sb.toString());

			paramMap.put("miscSeqNumber", miscSeqNumber.trim());
			log.info(" *** paramMap: *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			VehicleDetailsVO vo = null;
			while (rs.next()) {
				vo = new VehicleDetailsVO();
				vo.setItem_nbr(rs.getInt("ITEM_NBR"));
				vo.setMisc_seq_nbr(rs.getString("MISC_SEQ_NBR"));
				vo.setVeh_chas_nbr(rs.getString("VEH_CHAS_NBR"));
				vo.setCntr_nbr(rs.getString("CNTR_NBR"));
				vo.setAsn_nbr(rs.getString("ASN_NBR"));
				vo.setCntr_crg_status(rs.getString("CNTR_CRG_STATUS"));

				if (TpaConstants.TPA_CNTR_CRG_STATUS_L.equalsIgnoreCase(vo.getCntr_crg_status())) {
					vo.setCntr_crg_status_nm(TpaConstants.TPA_CNTR_CRG_STATUS_L_NM);
				} else if (TpaConstants.TPA_CNTR_CRG_STATUS_T.equalsIgnoreCase(vo.getCntr_crg_status())) {
					vo.setCntr_crg_status_nm(TpaConstants.TPA_CNTR_CRG_STATUS_T_NM);
				} else if (TpaConstants.TPA_CNTR_CRG_STATUS_R.equalsIgnoreCase(vo.getCntr_crg_status())) {
					vo.setCntr_crg_status_nm(TpaConstants.TPA_CNTR_CRG_STATUS_R_NM);
				} else {
					vo.setCntr_crg_status_nm(rs.getString("CNTR_CRG_STATUS_NM"));
				}

				vo.setPref_area_cd(rs.getString("PREF_AREA_CD"));
				vo.setArea_cd(rs.getString("AREA_CD"));
				vo.setSlot_nbr(rs.getString("SLOT_NBR"));
				vo.setRemarks(rs.getString("REMARKS"));

				tpaVehicleDetailsList.add(vo);
			}
			log.info("END: *** getTpaVehicleDetailsByMiscSeqNumber Result *****" + tpaVehicleDetailsList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getTpaVehicleDetailsByMiscSeqNumber ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getTpaVehicleDetailsByMiscSeqNumber ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTpaVehicleDetailsByMiscSeqNumber  DAO  END");
		}
		return tpaVehicleDetailsList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getTpaDetailsHistory()

	@Override
	public List<TpaDetailsHistoryVO> getTpaDetailsHistory(Integer start, Integer limit, String sort, String dir,
			Map<String, Object> filters) throws BusinessException {
		List<TpaDetailsHistoryVO> tpaDetailsHistoryList = new ArrayList<TpaDetailsHistoryVO>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getTpaDetailsHistory  DAO  Start start:" + start + "limit:" + limit + "sort:"
					+ CommonUtility.deNull(sort) + "dir:" + CommonUtility.deNull(dir) + "filters:"
					+ filters.toString());

			sb.append(" SELECT AMA.*, ");
			sb.append(" TO_CHAR(AMA.AUDIT_DTTM, 'DD/MM/YYYY HH24MI') AUDIT_DATE_TIME, ");
			sb.append(" LA.USER_NM AS LAST_UPDATED_BY ");
			sb.append(" FROM AUDIT_TRAIL_MISC_APP AMA ");
			sb.append(" LEFT JOIN LOGON_ACCT LA ON AMA.LAST_MODIFY_USER_ID = LA.LOGIN_ID ");
			sb.append(" WHERE AMA.APP_TYPE = 'TPA' ");
			sb.append(" AND AMA.MISC_SEQ_NBR = :miscSeqNbr ");
			sb.append(" ORDER BY AMA.AUDIT_DTTM ASC ");

			log.info(" ***getTpaDetailsHistory SQL *****" + sb.toString());
			paramMap.put("miscSeqNbr", filters.get("misc_seq_nbr").toString().trim());
			log.info(" *** paramMap: *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			TpaDetailsHistoryVO vo = null;
			while (rs.next()) {
				vo = new TpaDetailsHistoryVO();
				vo.setMisc_seq_nbr(rs.getString("MISC_SEQ_NBR"));
				vo.setAudit_date_time(rs.getString("AUDIT_DATE_TIME"));
				vo.setApp_status(rs.getString("APP_STATUS"));
				vo.setApprove_remarks(rs.getString("APPROVE_REMARKS"));
				vo.setExtend_dttm(rs.getTimestamp("EXTEND_DTTM"));
				vo.setExtend_remarks(rs.getString("EXTEND_REMARKS"));
				vo.setVoid_remarks(rs.getString("VOID_REMARKS"));
				vo.setClose_for_bill_remarks(rs.getString("CLOSE_FOR_BILL_REMARKS"));
				vo.setLast_modify_user_id(rs.getString("LAST_MODIFY_USER_ID"));
				vo.setLast_updated_by(rs.getString("LAST_UPDATED_BY"));

				tpaDetailsHistoryList.add(vo);
			}
			log.info("END: *** getTpaDetailsHistory Result *****" + tpaDetailsHistoryList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getTpaDetailsHistory ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getTpaDetailsHistory ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTpaDetailsHistory  DAO  END");
		}
		return tpaDetailsHistoryList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->voidApplication4NewTpa()

	@Override
	public void voidApplication4NewTpa(TpaVO vo) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: voidApplication4NewTpa  DAO  Start vo:" + vo.toString());

			sb.append(" UPDATE MISC_APP ");
			sb.append(" SET APP_STATUS = 'V', VOID_DTTM = SYSDATE, ");
			sb.append(" VOID_USER_ID = :userId, ");
			sb.append(" VOID_REMARKS = :remarks, ");
			sb.append(" LAST_MODIFY_USER_ID = :userId, ");
			sb.append(" LAST_MODIFY_DTTM = SYSDATE ");
			sb.append(" WHERE MISC_SEQ_NBR = miscSeqNbr ");

			log.info(" ***voidApplication4NewTpa SQL *****" + sb.toString());

			paramMap.put("userId", vo.getLast_modify_user_id());
			paramMap.put("remarks", vo.getVoid_remarks());
			paramMap.put("miscSeqNbr", vo.getMisc_seq_nbr());
			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			log.info("END: *** voidApplication4NewTpa Result *****");
		} catch (NullPointerException e) {
			log.info("exception: voidApplication4NewTpa ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: voidApplication4NewTpa ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: voidApplication4NewTpa  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->closeTpaForBill()

	@Override
	public void closeTpaForBill(Map<String, Object> filters) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: closeTpaForBill  DAO  Start filters:" + filters.toString());

			sb.append(" UPDATE MISC_VEHICLE MV ");
			sb.append(" SET MV.ACTUAL_FR_DTTM = TO_DATE(:fromDttmStr,'ddMMyyyy HH24MI'), ");
			sb.append(" MV.ACTUAL_TO_DTTM = TO_DATE(:toDttmStr,'ddMMyyyy HH24MI'), ");
			sb.append(" MV.ACTUAL_NBR_HOUR = :actualNoOfHours, ");
			sb.append(" MV.LAST_MODIFY_USER_ID = :userId, MV.LAST_MODIFY_DTTM = ");
			sb.append(" SYSDATE WHERE MISC_SEQ_NBR = :miscSeqNbr ");

			String status = "C";
			String userId = filters.get("userId").toString();
			String misc_seq_nbr = filters.get("misc_seq_nbr").toString();
			String actualNoOfHours = filters.get("actualNoOfHours").toString();
			String closeForBillRemarks = filters.get("closeForBillRemarks").toString();
			String fromDttmStr = filters.get("fromDttmStr").toString();
			String toDttmStr = filters.get("toDttmStr").toString();

			closeBillApplication4NewTpa(userId, status, misc_seq_nbr, closeForBillRemarks);

			log.info(" ***closeTpaForBill SQL *****" + sb.toString());

			paramMap.put("fromDttmStr", fromDttmStr);
			paramMap.put("toDttmStr", toDttmStr);
			paramMap.put("actualNoOfHours", actualNoOfHours);
			paramMap.put("userId", userId);
			paramMap.put("miscSeqNbr", misc_seq_nbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			log.info("END: *** closeTpaForBill Result *****");
		} catch (BusinessException e) {
			log.info("exception: closeTpaForBill ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: closeTpaForBill ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: closeTpaForBill ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: closeTpaForBill  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->extendReduceTpa()

	@Override
	public void extendReduceTpa(Map<String, Object> filters) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: extendReduceTpa  DAO  Start filters:" + filters.toString());

			sb.append(" UPDATE MISC_VEHICLE ");
			sb.append(" SET TO_DTTM = TO_DATE(:toDttmStr,'ddMMyyyy HH24MI'), ");
			sb.append(" NBR_HOUR = :noOfHours, ");
			sb.append(" LAST_MODIFY_USER_ID = :userId, LAST_MODIFY_DTTM ");
			sb.append(" = SYSDATE WHERE MISC_SEQ_NBR = :miscSeqNbr ");

			String status = "S";
			String userId = filters.get("userId").toString();
			String misc_seq_nbr = filters.get("misc_seq_nbr").toString();
			String noOfHours = filters.get("noOfHours").toString();
			String toDttmStr = filters.get("toDttmStr").toString();
			String extend_remarks = filters.get("extend_remarks").toString();

			updateTpaForExtendReduce(userId, status, misc_seq_nbr, extend_remarks);

			log.info(" ***extendReduceTpa SQL *****" + sb.toString());

			paramMap.put("toDttmStr", toDttmStr);
			paramMap.put("noOfHours", noOfHours);
			paramMap.put("userId", userId);
			paramMap.put("miscSeqNbr", misc_seq_nbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			log.info("END: *** extendReduceTpa Result *****");
		} catch (BusinessException e) {
			log.info("exception: extendReduceTpa ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: extendReduceTpa ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: extendReduceTpa ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: extendReduceTpa  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getTpaDetails()

	@Override
	public TpaVO getTpaDetails(String misc_seq_nbr) throws BusinessException {
		TpaVO vo = new TpaVO();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getTpaDetails  DAO  Start misc_seq_nbr:" + CommonUtility.deNull(misc_seq_nbr));

			sb.append(" SELECT ");
			sb.append(" MA.MISC_SEQ_NBR, MA.APP_DTTM, MA.ACCT_NBR, MA.APP_STATUS, ");
			sb.append(" MA.CONTACT_PERSON, MA.CONTACT_TEL, MA.CONTACT_EMAIL, ");
			sb.append(" MA.LAST_MODIFY_USER_ID, MA.CUST_CD, CC.CO_NM, LA.USER_NM AS LAST_UPDATED_BY, ");
			sb.append(" TO_CHAR(MA.APP_DTTM, 'DD/MM/YYYY HH24MI') APP_DATE_TIME, ");
			sb.append(" TO_CHAR(MV.ACTUAL_FR_DTTM, 'DD/MM/YYYY HH24MI') ACTUAL_FROM_DATE_TIME, ");
			sb.append(" TO_CHAR(MV.ACTUAL_TO_DTTM, 'DD/MM/YYYY HH24MI') ACTUAL_TO_DATE_TIME, ");
			sb.append(" TO_CHAR(MV.FR_DTTM, 'DD/MM/YYYY HH24MI') FROM_DATE_TIME, ");
			sb.append(" TO_CHAR(MV.TO_DTTM, 'DD/MM/YYYY HH24MI') TO_DATE_TIME, ");
			sb.append(" NVL2(MV.ACTUAL_TO_DTTM, MV.ACTUAL_NBR_HOUR, MV.NBR_HOUR) DURATION_OF_STAY, ");
			sb.append(" MV.NBR_HOUR, MV.ACTUAL_NBR_HOUR, MV.TRAILER_TYPE, MV.TRAILER_SIZE, ");
			sb.append(" MV.PARK_REASON, MV.CARGO_TYPE, MV.PARK_REASON_CD, ");
			sb.append(" MTC.MISC_TYPE_NM AS REASON_FOR_APPLICATION, ");
			sb.append(" MTC1.MISC_TYPE_NM AS HOUR_PER_BLOCK, ");
			sb.append(" MA.VV_CD, ");
			sb.append(" TO_CHAR(MA.LAST_MODIFY_DTTM, 'DD/MM/YYYY HH24MI') LAST_MODIFY_DATE_TIME, ");
			sb.append(" MA.APPROVE_REMARKS, MA.VOID_REMARKS, MA.REF_NBR ");
			sb.append(" FROM MISC_APP MA ");
			sb.append(" INNER JOIN MISC_VEHICLE MV ON MA.MISC_SEQ_NBR = MV.MISC_SEQ_NBR ");
			sb.append(" LEFT JOIN COMPANY_CODE CC ON MA.CUST_CD = CC.CO_CD ");
			sb.append(" LEFT JOIN MISC_TYPE_CODE MTC ON MTC.MISC_TYPE_CD = MV.PARK_REASON_CD ");
			sb.append(" AND MTC.CAT_CD = 'TPA_PKRSN' ");
			sb.append(" LEFT JOIN MISC_TYPE_CODE MTC1 ON MTC1.MISC_TYPE_CD = 'HOUR_PER_BLK' ");
			sb.append(" AND MTC1.CAT_CD = 'TPA_TIME' ");
			sb.append(" LEFT JOIN LOGON_ACCT LA ON MA.LAST_MODIFY_USER_ID = LA.LOGIN_ID ");
			sb.append(" WHERE MA.MISC_SEQ_NBR = :miscSeqNbr ");
			sb.append(" AND APP_TYPE = 'TPA' ");

			log.info(" ***getTpaDetails SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", misc_seq_nbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				vo.setCo_nm(rs.getString("CO_NM"));
				vo.setAcct_nbr(rs.getString("ACCT_NBR"));
				vo.setCon_email(rs.getString("CONTACT_EMAIL"));
				vo.setCon_person(rs.getString("CONTACT_PERSON"));
				vo.setCon_tel(rs.getString("CONTACT_TEL"));
				vo.setApp_status(rs.getString("APP_STATUS"));
				vo.setApp_date_time(rs.getString("APP_DATE_TIME"));
				vo.setActual_from_date_time(rs.getString("ACTUAL_FROM_DATE_TIME"));
				vo.setActual_to_date_time(rs.getString("ACTUAL_TO_DATE_TIME"));
				vo.setFrom_date_time(rs.getString("FROM_DATE_TIME"));
				vo.setTo_date_time(rs.getString("TO_DATE_TIME"));
				vo.setCargo_type(rs.getString("CARGO_TYPE"));
				vo.setCust_cd(rs.getString("CUST_CD"));
				vo.setNbr_hour(rs.getDouble("NBR_HOUR"));
				vo.setActual_nbr_hour(rs.getDouble("ACTUAL_NBR_HOUR"));
				vo.setDuration_of_stay(rs.getDouble("DURATION_OF_STAY"));

				String hour = rs.getString("HOUR_PER_BLOCK");
				if (null == hour || "".equalsIgnoreCase(hour)) {
					hour = TpaConstants.HOUR_PER_BLOCK;
				}
				double hourPerBlock = Double.parseDouble(hour);
				vo.setNbr_of_blocks(Math.ceil(vo.getNbr_hour() / hourPerBlock));
				vo.setActual_nbr_of_blocks(Math.ceil(vo.getActual_nbr_hour() / hourPerBlock));

				vo.setMisc_seq_nbr(rs.getString("MISC_SEQ_NBR"));
				vo.setPark_reason(rs.getString("PARK_REASON"));
				vo.setPark_reason_cd(rs.getString("PARK_REASON_CD"));
				vo.setReason_for_application(rs.getString("REASON_FOR_APPLICATION"));
				vo.setTrailer_type(rs.getString("TRAILER_TYPE"));
				vo.setTrailer_size(rs.getInt("TRAILER_SIZE"));
				vo.setVv_cd(rs.getString("VV_CD"));
				vo.setLast_modify_user_id(rs.getString("LAST_MODIFY_USER_ID"));
				vo.setLast_modify_date_time(rs.getString("LAST_MODIFY_DATE_TIME"));

				if (!"".equalsIgnoreCase(rs.getString("LAST_UPDATED_BY"))) {
					vo.setLast_updated_by(rs.getString("LAST_UPDATED_BY"));
				} else {
					vo.setLast_updated_by(rs.getString("LAST_MODIFY_USER_ID"));
				}

				vo.setApprove_reject_remarks(rs.getString("APPROVE_REMARKS"));
				vo.setVoid_remarks(rs.getString("VOID_REMARKS"));
				vo.setRef_nbr(rs.getString("REF_NBR"));
			}
			log.info("END: *** getTpaDetails Result *****" + vo.toString());
		} catch (NullPointerException e) {
			log.info("exception: getTpaDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getTpaDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTpaDetails  DAO  END");
		}
		return vo;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getInVoyageList()

	@Override
	public List<MiscAppValueObject> getInVoyageList(String vesselName) throws BusinessException {
		List<MiscAppValueObject> inVoyList = new ArrayList<MiscAppValueObject>();
		MiscAppValueObject miscAppObj;
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getInVoyageList  DAO  Start vesselName:" + CommonUtility.deNull(vesselName));

			int atuGracePeriod = getATUGracePeriod();
			sb.append(" select /* MiscAppEJB - getInVoyageList() */ ");
			sb.append(" vv_cd, in_voy_nbr from vessel_call ");
			sb.append(" where vsl_nm = UPPER(:vesselName)  ");
			sb.append(" and vv_status_ind not in ('CX','CL')  ");
			sb.append(" AND((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')  ");
			// " and terminal = 'GB' "+
			sb.append(" UNION ");
			sb.append(" SELECT V.VV_CD, V.IN_VOY_NBR FROM VESSEL_CALL V, BERTHING B ");
			sb.append(" WHERE V.VSL_NM = UPPER(:vesselName)  ");
			sb.append(" AND V.VV_STATUS_IND IN ('CL')  ");
			sb.append(" AND((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')  ");
			// " AND TERMINAL = 'GB' ");
			sb.append(" AND B.ATU_DTTM > = SYSDATE - :atuGracePeriod "); // Parametrized value
			sb.append(" AND B.VV_CD = V.VV_CD ");

			log.info(" ***getInVoyageList SQL *****" + sb.toString());

			paramMap.put("vesselName", vesselName);
			paramMap.put("atuGracePeriod", atuGracePeriod);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				miscAppObj = new MiscAppValueObject();
				miscAppObj.setVarCode(rs.getString("vv_cd"));
				miscAppObj.setInVoyNbr(rs.getString("in_voy_nbr"));
				inVoyList.add(miscAppObj);
			}
			log.info("END: *** getInVoyageList Result *****" + inVoyList.toString());
		} catch (BusinessException e) {
			log.info("exception: getInVoyageList ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getInVoyageList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getInVoyageList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getInVoyageList  DAO  END");
		}
		return inVoyList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getOutVoyageList()

	@Override
	public List<MiscAppValueObject> getOutVoyageList(String vesselName) throws BusinessException {
		List<MiscAppValueObject> outVoyList = new ArrayList<MiscAppValueObject>();
		MiscAppValueObject miscAppObj;
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getOutVoyageList  DAO  Start vesselName:" + CommonUtility.deNull(vesselName));

			int atuGracePeriod = getATUGracePeriod();

			sb.append(" select /* MiscAppEJB - getVoyageList() */ ");
			sb.append(" vv_cd, out_voy_nbr from vessel_call ");
			sb.append(" where vsl_nm = UPPER(:vesselName)  ");
			sb.append(" and vv_status_ind not in ('CX','CL')  ");
			sb.append(" AND((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')  ");
			// " and terminal = 'GB' ");
			sb.append(" UNION ");
			sb.append(" SELECT V.VV_CD, V.OUT_VOY_NBR FROM VESSEL_CALL V, BERTHING B ");
			sb.append(" WHERE V.VSL_NM = UPPER(:vesselName)  ");
			sb.append(" AND V.VV_STATUS_IND IN ('CL')  ");
			sb.append(" AND((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR TERMINAL NOT IN 'CT')  ");
			// " AND TERMINAL = 'GB' ");
			sb.append(" AND B.ATU_DTTM > = SYSDATE - :atuGracePeriod "); // Parametrized value
			sb.append(" AND B.VV_CD = V.VV_CD ");

			log.info(" ***getOutVoyageList SQL *****" + sb.toString());

			paramMap.put("vesselName", vesselName);
			paramMap.put("atuGracePeriod", atuGracePeriod);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				miscAppObj = new MiscAppValueObject();
				miscAppObj.setVarCode(rs.getString("vv_cd"));
				miscAppObj.setOutVoyNbr(rs.getString("out_voy_nbr"));
				outVoyList.add(miscAppObj);
			}

			log.info("END: *** getInVoyageList Result *****" + outVoyList.toString());
		} catch (BusinessException e) {
			log.info("exception: getInVoyageList ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getOutVoyageList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getOutVoyageList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getOutVoyageList  DAO  END");
		}
		return outVoyList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getMiscTypeCode()

	@Override
	public List<Map<String, Object>> getMiscTypeCode(String catCdParam, String miscTypeCdParam)
			throws BusinessException {
		List<Map<String, Object>> misctypeCodeList = new ArrayList<Map<String, Object>>();
		Map<String, Object> typeCodeValues = new LinkedHashMap<String, Object>();

		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getMiscTypeCode  DAO  Start catCdParam:" + CommonUtility.deNull(catCdParam)
					+ "miscTypeCdParam:" + CommonUtility.deNull(miscTypeCdParam));

			sb.append(" SELECT * FROM MISC_TYPE_CODE WHERE CAT_CD = :catCdParam ");
			sb.append(" AND MISC_TYPE_CD= :miscTypeCdParam AND rec_status= 'A' ");
			sb.append(" ORDER BY MISC_TYPE_NM ASC "); // TPA_PKRSN

			log.info(" ***getMiscTypeCode SQL *****" + sb.toString());

			paramMap.put("catCdParam", catCdParam);
			paramMap.put("miscTypeCdParam", miscTypeCdParam);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				typeCodeValues.put(rs.getString("MISC_TYPE_CD"), rs.getString("MISC_TYPE_NM"));
			}
			misctypeCodeList.add(typeCodeValues);
			log.info("END: *** getMiscTypeCode Result *****" + misctypeCodeList.toString());
			return misctypeCodeList;
		} catch (NullPointerException e) {
			log.info("exception: getMiscTypeCode ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getMiscTypeCode ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getMiscTypeCode  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getParkingAreaList4NewTpa()

	@Override
	public List<MiscAppParkingAreaObject> getParkingAreaList4NewTpa(String slotType, String startDate, String toDate,
			String trailerSize, String trailerType) throws BusinessException {
		List<MiscAppParkingAreaObject> parkingAreaList = new ArrayList<MiscAppParkingAreaObject>();
		// --1. FR_DDTM between selected from date and selected to date "
		// --2. TO_DDTM between selected from date and selected to date "
		// --3. FR_DTTM <= selected from date AND TO_DTTM > selected to date "

		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getParkingAreaList4NewTpa  DAO  Start slotType:" + CommonUtility.deNull(slotType)
					+ "startDate:" + CommonUtility.deNull(startDate) + "toDate:" + CommonUtility.deNull(toDate)
					+ "trailerSize:" + CommonUtility.deNull(trailerSize) + "trailerType:"
					+ CommonUtility.deNull(trailerType));

			sb.append(" SELECT AREA_CD, COUNT(*) AS NO_SLOT FROM MISC_PARKING_SLOT ");
			sb.append(" WHERE SLOT_STATUS='OPN' AND SLOT_TYPE =:slotType AND ");
			sb.append(" TRAILER_TYPE =:trailerType AND (AREA_CD, SLOT_NBR) NOT IN ");
			sb.append(" SELECT MPS.AREA_CD, MPS.SLOT_NBR FROM MISC_PARKING_SLOT MPS ");
			sb.append(" INNER JOIN MISC_VEHICLE_DET MVD ");
			sb.append(" ON MPS.AREA_CD = MVD.AREA_CD AND MPS.SLOT_NBR = MVD.SLOT_NBR ");
			sb.append(" INNER JOIN MISC_VEHICLE MV ON MV.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
			sb.append(" INNER JOIN MISC_APP MA ON MV.MISC_SEQ_NBR = MA.MISC_SEQ_NBR ");
			sb.append("  WHERE SLOT_STATUS='OPN' AND SLOT_TYPE =:slotType ");
			sb.append(" AND MVD.AREA_CD IS NOT NULL AND MVD.SLOT_NBR IS NOT NULL ");
			sb.append(" AND (MA.app_status='A' or MA.app_status='C') ");
			sb.append(" AND ( ");
			sb.append(" (TO_DATE(:startDate, 'ddMMyyyy HH24MI') > NVL2(ACTUAL_FR_DTTM, ");
			sb.append(" ACTUAL_FR_DTTM,FR_DTTM) and TO_DATE(:startDate, 'ddMMyyyy HH24MI') ");
			sb.append(" < NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM) ) ");
			sb.append(" OR (TO_DATE(:toDate, 'ddMMyyyy HH24MI') > NVL2(ACTUAL_FR_DTTM, ");
			sb.append(" ACTUAL_FR_DTTM,FR_DTTM) and TO_DATE(:toDate, 'ddMMyyyy HH24MI') < ");
			sb.append(" NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM) ) ");
			sb.append(" OR (TO_DATE(:startDate, 'ddMMyyyy HH24MI') <= NVL2(ACTUAL_FR_DTTM, ");
			sb.append(" ACTUAL_FR_DTTM,FR_DTTM)  and TO_DATE(:toDate, 'ddMMyyyy HH24MI') >= ");
			sb.append(" NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM)) ");
			sb.append(" ) ");
			if (StringUtils.equalsIgnoreCase(TpaConstants.TPA_TRAILER_SIZE_40_CD, trailerSize)) {
				sb.append(" AND TRAILER_SIZE IN (:trailerSize) ");
			}

			sb.append(" GROUP BY AREA_CD ORDER BY AREA_CD ");

			log.info(" ***getParkingAreaList4NewTpa SQL *****" + sb.toString());

			paramMap.put("slotType", slotType);
			paramMap.put("trailerType", trailerType);
			paramMap.put("startDate", startDate);
			paramMap.put("toDate", toDate);

			log.info(" *** paramMap: *****" + paramMap.toString());

			if (StringUtils.equalsIgnoreCase(TpaConstants.TPA_TRAILER_SIZE_40_CD, trailerSize)) {
				paramMap.put("trailerSize", trailerSize);
			}

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				MiscAppParkingAreaObject miscAppParkingAreaObject = new MiscAppParkingAreaObject();
				miscAppParkingAreaObject.setAreaCode(rs.getString("AREA_CD"));
				miscAppParkingAreaObject.setNoOfSlot(rs.getString("NO_SLOT"));
				parkingAreaList.add(miscAppParkingAreaObject);
			}
			log.info("END: *** getParkingAreaList4NewTpa Result *****" + parkingAreaList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getParkingAreaList4NewTpa ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getParkingAreaList4NewTpa ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getParkingAreaList4NewTpa  DAO  END");
		}
		return parkingAreaList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getParkingAreaSlotAvailableList4NewTpa()

	@Override
	public List<MiscAppParkingAreaObject> getParkingAreaSlotAvailableList4NewTpa(String areaCd, String slotType,
			String startDate, String toDate, String trailerSize, String trailerType) throws BusinessException {
		List<MiscAppParkingAreaObject> parkingSlotList = new ArrayList<MiscAppParkingAreaObject>();

		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getParkingAreaSlotAvailableList4NewTpa  DAO  Start areaCd:" + CommonUtility.deNull(areaCd)
					+ "slotType:" + CommonUtility.deNull(slotType) + "startDate:" + CommonUtility.deNull(startDate)
					+ "toDate:" + CommonUtility.deNull(toDate) + "trailerSize:" + CommonUtility.deNull(trailerSize)
					+ "trailerType:" + CommonUtility.deNull(trailerType));

			sb.append(" SELECT AREA_CD, SLOT_NBR, SLOT_TYPE, SLOT_STATUS FROM MISC_PARKING_SLOT ");
			sb.append(" WHERE SLOT_STATUS='OPN' AND SLOT_TYPE =:slotType AND AREA_CD ");
			sb.append(" =:areaCd AND TRAILER_TYPE =:trailerType AND (AREA_CD, SLOT_NBR) NOT IN ");
			sb.append(" ( SELECT MPS.AREA_CD, MPS.SLOT_NBR FROM MISC_PARKING_SLOT MPS ");
			sb.append(" INNER JOIN MISC_VEHICLE_DET MVD ");
			sb.append(" ON MPS.AREA_CD = MVD.AREA_CD AND MPS.SLOT_NBR = MVD.SLOT_NBR ");
			sb.append(" INNER JOIN MISC_VEHICLE MV ON MV.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
			sb.append(" INNER JOIN MISC_APP MA ON MV.MISC_SEQ_NBR = MA.MISC_SEQ_NBR ");
			sb.append(" WHERE SLOT_STATUS='OPN' AND SLOT_TYPE =:slotType ");
			sb.append(" AND MVD.AREA_CD IS NOT NULL AND MVD.SLOT_NBR IS NOT NULL  ");
			sb.append(" AND (MA.app_status='A' or MA.app_status='C') ");
			sb.append(" AND ( ");
			sb.append(" (TO_DATE(:startDate, 'ddMMyyyy HH24MI') > NVL2(ACTUAL_FR_DTTM,ACTUAL_FR_DTTM, ");
			sb.append(" FR_DTTM) and TO_DATE(:startDate, 'ddMMyyyy HH24MI') < NVL2(ACTUAL_TO_DTTM, ");
			sb.append(" ACTUAL_TO_DTTM,TO_DTTM) ) ");
			sb.append(" OR (TO_DATE(:toDate, 'ddMMyyyy HH24MI') > NVL2(ACTUAL_FR_DTTM,ACTUAL_FR_DTTM, ");
			sb.append(" FR_DTTM) and TO_DATE(:toDate, 'ddMMyyyy HH24MI') < NVL2(ACTUAL_TO_DTTM, ");
			sb.append(" ACTUAL_TO_DTTM,TO_DTTM) ) ");
			sb.append("  OR (TO_DATE(:startDate, 'ddMMyyyy HH24MI') <= NVL2(ACTUAL_FR_DTTM, ");
			sb.append(" ACTUAL_FR_DTTM,FR_DTTM)  and TO_DATE(:toDate, 'ddMMyyyy HH24MI') >= ");
			sb.append(" NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM)) ) ) ");

			if (StringUtils.equalsIgnoreCase(TpaConstants.TPA_TRAILER_SIZE_40_CD, trailerSize)) {
				sb.append(" AND TRAILER_SIZE IN (:trailerSize) ");
			}

			sb.append(" ORDER BY AREA_CD, SLOT_NBR ");

			log.info(" ***getParkingAreaSlotAvailableList4NewTpa SQL *****" + sb.toString());

			paramMap.put("slotType", slotType);
			paramMap.put("areaCd", areaCd);
			paramMap.put("trailerType", trailerType);
			paramMap.put("startDate", startDate);
			paramMap.put("toDate", toDate);

			log.info(" *** paramMap: *****" + paramMap.toString());

			if (StringUtils.equalsIgnoreCase(TpaConstants.TPA_TRAILER_SIZE_40_CD, trailerSize)) {
				paramMap.put("trailerSize", trailerSize);
			}

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				MiscAppParkingAreaObject miscAppParkingAreaObject = new MiscAppParkingAreaObject();
				miscAppParkingAreaObject.setAreaCode(rs.getString("AREA_CD"));
				miscAppParkingAreaObject.setSlotNumber(rs.getString("SLOT_NBR"));
				miscAppParkingAreaObject.setSlotStatus(rs.getString("SLOT_TYPE"));
				miscAppParkingAreaObject.setSlotType(rs.getString("SLOT_STATUS"));

				parkingSlotList.add(miscAppParkingAreaObject);
			}

			log.info("END: *** getParkingAreaSlotAvailableList4NewTpa Result *****" + parkingSlotList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getParkingAreaSlotAvailableList4NewTpa ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getParkingAreaSlotAvailableList4NewTpa ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getParkingAreaSlotAvailableList4NewTpa  DAO  END");
		}
		return parkingSlotList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getAvailableParkingSlots4NewTpa()

	@Override
	public List<MiscAppParkingAreaObject> getAvailableParkingSlots4NewTpa(String slotType, String startDate,
			String toDate, String trailerSize, String trailerType) throws BusinessException {
		List<MiscAppParkingAreaObject> parkingSlotList = new ArrayList<MiscAppParkingAreaObject>();

		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getAvailableParkingSlots4NewTpa  DAO  Start slotType:" + CommonUtility.deNull(slotType)
					+ "startDate:" + CommonUtility.deNull(startDate) + "toDate:" + CommonUtility.deNull(toDate)
					+ "trailerSize:" + CommonUtility.deNull(trailerSize) + "trailerType:"
					+ CommonUtility.deNull(trailerType));

			sb.append(" SELECT AREA_CD, SLOT_NBR, SLOT_TYPE, SLOT_STATUS FROM MISC_PARKING_SLOT ");
			sb.append(" WHERE SLOT_STATUS='OPN' AND SLOT_TYPE = :slotType  AND ");
			sb.append(" TRAILER_TYPE = :trailerType AND (AREA_CD, SLOT_NBR) NOT IN ");
			sb.append(" ( SELECT MPS.AREA_CD, MPS.SLOT_NBR FROM MISC_PARKING_SLOT MPS ");
			sb.append(" INNER JOIN MISC_VEHICLE_DET MVD ");
			sb.append(" ON MPS.AREA_CD = MVD.AREA_CD AND MPS.SLOT_NBR = MVD.SLOT_NBR ");
			sb.append(" INNER JOIN MISC_VEHICLE MV ON MV.MISC_SEQ_NBR = MVD.MISC_SEQ_NBR ");
			sb.append(" INNER JOIN MISC_APP MA ON MV.MISC_SEQ_NBR = MA.MISC_SEQ_NBR ");
			sb.append("  WHERE SLOT_STATUS='OPN' AND SLOT_TYPE = :slotType ");
			sb.append(" AND MVD.AREA_CD IS NOT NULL AND MVD.SLOT_NBR IS NOT NULL  ");
			sb.append(" AND (MA.app_status='A' or MA.app_status='C') AND ( ");
			sb.append(" (TO_DATE(:startDate, 'ddMMyyyy HH24MI') > NVL2(ACTUAL_FR_DTTM,ACTUAL_FR_DTTM, ");
			sb.append(" FR_DTTM) and TO_DATE(:startDate, 'ddMMyyyy HH24MI') < NVL2(ACTUAL_TO_DTTM, ");
			sb.append(" ACTUAL_TO_DTTM,TO_DTTM) ) ");
			sb.append(" OR (TO_DATE(:toDate, 'ddMMyyyy HH24MI') > NVL2(ACTUAL_FR_DTTM,ACTUAL_FR_DTTM, ");
			sb.append(" FR_DTTM) and TO_DATE(:toDate, 'ddMMyyyy HH24MI') < NVL2(ACTUAL_TO_DTTM, ");
			sb.append(" ACTUAL_TO_DTTM,TO_DTTM) ) ");
			sb.append(" OR (TO_DATE(:startDate, 'ddMMyyyy HH24MI') <= NVL2(ACTUAL_FR_DTTM, ");
			sb.append(" ACTUAL_FR_DTTM,FR_DTTM)  and TO_DATE(:toDate, 'ddMMyyyy HH24MI') >= ");
			sb.append(" NVL2(ACTUAL_TO_DTTM,ACTUAL_TO_DTTM,TO_DTTM)) ");
			sb.append(" )  ) ");
			if (StringUtils.equalsIgnoreCase(TpaConstants.TPA_TRAILER_SIZE_40_CD, trailerSize)) {
				sb.append(" AND TRAILER_SIZE IN (:trailerSize) ");
			}
			sb.append(" ORDER BY AREA_CD, SLOT_NBR ");

			log.info(" ***getAvailableParkingSlots4NewTpa SQL *****" + sb.toString());

			paramMap.put("slotType", slotType);
			paramMap.put("trailerType", trailerType);
			paramMap.put("startDate", startDate);
			paramMap.put("toDate", toDate);
			log.info(" *** paramMap: *****" + paramMap.toString());

			if (StringUtils.equalsIgnoreCase(TpaConstants.TPA_TRAILER_SIZE_40_CD, trailerSize)) {
				paramMap.put("trailerSize", trailerSize);
			}

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				MiscAppParkingAreaObject miscAppParkingAreaObject = new MiscAppParkingAreaObject();
				miscAppParkingAreaObject.setAreaCode(rs.getString("AREA_CD"));
				miscAppParkingAreaObject.setSlotNumber(rs.getString("SLOT_NBR"));
				miscAppParkingAreaObject.setSlotStatus(rs.getString("SLOT_TYPE"));
				miscAppParkingAreaObject.setSlotType(rs.getString("SLOT_STATUS"));

				parkingSlotList.add(miscAppParkingAreaObject);
			}
			log.info("END: *** getAvailableParkingSlots4NewTpa Result *****" + parkingSlotList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getAvailableParkingSlots4NewTpa ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getAvailableParkingSlots4NewTpa ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAvailableParkingSlots4NewTpa  DAO  END");
		}
		return parkingSlotList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->checkActiveASNNo()

	@Override
	public Boolean checkActiveASNNo(String asnNo, String vv_cd) throws BusinessException {
		Boolean isActiveASNno = false;
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: checkActiveASNNo  DAO  Start asnNo:" + CommonUtility.deNull(asnNo) + "vv_cd:"
					+ CommonUtility.deNull(vv_cd));

			if (!"".equalsIgnoreCase(vv_cd) && vv_cd != null) {
				sb.append(" select /* MiscAppEJB - checkActiveASNNo() */ EDO_ASN_NBR from GB_EDO ");
				sb.append(" where EDO_ASN_NBR = :asnNo "); // + asnNo + " "
				sb.append(" and EDO_STATUS = 'A' ");
				sb.append(" and VAR_NBR = :vvCd UNION ");
				sb.append(" select /* MiscAppEJB - checkActiveASNNo() */ ESN_ASN_NBR as EDO_ASN_NBR from ESN ");
				sb.append(" where ESN_ASN_NBR = :asnNo "); // asnNo + "
				sb.append(" and ESN_STATUS = 'A' ");
				sb.append(" and (IN_VOY_VAR_NBR = :vvCd OR OUT_VOY_VAR_NBR = :vvCd) ");
			} else {
				sb.append(" select /* MiscAppEJB - checkActiveASNNo() */ EDO_ASN_NBR from GB_EDO ");
				sb.append(" where EDO_ASN_NBR =:asnNo "); // + asnNo + "
				sb.append(" and EDO_STATUS = 'A' UNION ");
				sb.append(" select /* MiscAppEJB - checkActiveASNNo() */ ESN_ASN_NBR as EDO_ASN_NBR from ESN ");
				sb.append(" where ESN_ASN_NBR = :asnNo "); // asnNo + " "
				sb.append(" and ESN_STATUS = 'A' ");
			}
			log.info(" ***checkActiveASNNo SQL *****" + sb.toString());
			if (!"".equalsIgnoreCase(vv_cd) && vv_cd != null) {

				paramMap.put("asnNo", Integer.parseInt(asnNo));
				paramMap.put("vvCd", vv_cd);
			} else {

				log.info(" ***checkActiveASNNo SQL *****" + sb.toString());

				paramMap.put("asnNo", Integer.parseInt(asnNo));
			}

			log.info(" *** paramMap: *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			while (rs.next()) {
				if (asnNo.equalsIgnoreCase(rs.getString("EDO_ASN_NBR"))) {
					isActiveASNno = true;
				}
			}
			log.info("END: *** checkActiveASNNo Result *****" + isActiveASNno.toString());
		} catch (NullPointerException e) {
			log.info("exception: checkActiveASNNo ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: checkActiveASNNo ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkActiveASNNo  DAO  END");
		}
		return isActiveASNno;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->checkActiveContainerNo()

	@Override
	public Boolean checkActiveContainerNo(String containerNo, String vv_cd) throws BusinessException {
		Boolean isActiveCNTRno = false;
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: checkActiveContainerNo  DAO  Start containerNo:" + CommonUtility.deNull(containerNo)
					+ "vv_cd:" + CommonUtility.deNull(vv_cd));

			if (!"".equalsIgnoreCase(vv_cd) && vv_cd != null) {
				sb.append(" select /* MiscAppEJB - checkActiveASNNo() */ CNTR_NBR from CNTR ");
				sb.append(" where CNTR_NBR = :containerNo "); // containerNo
				sb.append(" and TXN_STATUS = 'A' ");
				sb.append(" and (LOAD_VV_CD = :vvCd or DISC_VV_CD = :vvCd or ");
				sb.append(" NOM_LOAD_VV_CD = :vvCd or NOM_DISC_VV_CD = :vvCd) ");
			} else {
				sb.append(" select /* MiscAppEJB - checkActiveASNNo() */ CNTR_NBR from CNTR ");
				sb.append(" where CNTR_NBR = :containerNo "); // containerNo
				sb.append(" and TXN_STATUS = 'A' ");
			}

			if (!"".equalsIgnoreCase(vv_cd) && vv_cd != null) {

				log.info(" ***checkActiveContainerNo SQL *****" + sb.toString());

				paramMap.put("containerNo", containerNo);
				paramMap.put("vvCd", vv_cd);
			} else {

				log.info(" ***checkActiveContainerNo SQL *****" + sb.toString());

				paramMap.put("containerNo", containerNo);
			}
			log.info(" *** paramMap: *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			while (rs.next()) {
				if (containerNo.equalsIgnoreCase(rs.getString("CNTR_NBR"))) {
					isActiveCNTRno = true;
				}
			}
			log.info("END: *** checkActiveContainerNo Result *****" + isActiveCNTRno.toString());
		} catch (NullPointerException e) {
			log.info("exception: checkActiveContainerNo ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: checkActiveContainerNo ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkActiveContainerNo  DAO  END");
		}
		return isActiveCNTRno;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getCargoContainerStatus()

	@Override
	public TypeCdVO getCargoContainerStatus(String cntrAsnNo, Boolean isCntr) throws BusinessException {
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		String cargoContainerStatus = "";
		TypeCdVO typecd = new TypeCdVO();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getCargoContainerStatus  DAO  Start cntrAsnNo:" + CommonUtility.deNull(cntrAsnNo)
					+ "isCntr:" + isCntr);

			if (isCntr == true) {
				sb.append(" SELECT distinct misc.MISC_TYPE_CD as CNTR_STATUS, ");
				sb.append(" misc.MISC_TYPE_NM as CNTR_STATUS_NM FROM CNTR cn ");
				sb.append(" LEFT JOIN MISC_TYPE_CODE misc ON (misc.CAT_CD = 'PURP_CD' ");
				sb.append(" AND misc.MISC_TYPE_CD = cn.PURP_CD) ");
				sb.append(" WHERE TXN_STATUS = 'A' and cn.CNTR_NBR =:cntrAsnNo "); // cntrNo
			} else {
				sb.append(" SELECT distinct CRG_STATUS as CNTR_STATUS, ");
				sb.append(" CRG_STATUS as CNTR_STATUS_NM FROM GB_EDO ");
				sb.append(" WHERE EDO_STATUS  = 'A' and EDO_ASN_NBR = :cntrAsnNo ");
				sb.append(" union ");
				sb.append(" SELECT trans_type as CNTR_STATUS, trans_type as CNTR_STATUS_NM FROM ESN ");
				sb.append(" WHERE ESN_STATUS  = 'A' and ESN_ASN_NBR = :cntrAsnNo "); // rAsnNo
			}

			log.info(" ***getCargoContainerStatus SQL *****" + sb.toString());

			if (isCntr == true) {
				paramMap.put("cntrAsnNo", cntrAsnNo);
			} else {
				paramMap.put("cntrAsnNo", cntrAsnNo);
			}
			log.info(" *** paramMap: *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				cargoContainerStatus = CommonUtility.deNull(rs.getString("CNTR_STATUS"));
				if (isCntr == true) {
					typecd.setMiscTypeCd(cargoContainerStatus);
					typecd.setMiscTypeNm(rs.getString("CNTR_STATUS_NM"));
				} else {
					if (TpaConstants.TPA_CNTR_CRG_STATUS_L.equalsIgnoreCase(cargoContainerStatus)
							|| TpaConstants.TPA_CNTR_CRG_STATUS_E.equalsIgnoreCase(cargoContainerStatus)) {
						typecd.setMiscTypeCd(cargoContainerStatus);
						typecd.setMiscTypeNm(TpaConstants.TPA_CNTR_CRG_STATUS_L_NM);
					} else if (TpaConstants.TPA_CNTR_CRG_STATUS_T.equalsIgnoreCase(cargoContainerStatus)) {
						typecd.setMiscTypeCd(cargoContainerStatus);
						typecd.setMiscTypeNm(TpaConstants.TPA_CNTR_CRG_STATUS_T_NM);
					} else if (TpaConstants.TPA_CNTR_CRG_STATUS_R.equalsIgnoreCase(cargoContainerStatus)) {
						typecd.setMiscTypeCd(cargoContainerStatus);
						typecd.setMiscTypeNm(TpaConstants.TPA_CNTR_CRG_STATUS_R_NM);
					} else {
						typecd.setMiscTypeCd(cargoContainerStatus);
						typecd.setMiscTypeNm(TpaConstants.TPA_CNTR_CRG_STATUS_T_NM);
					}
				}
			}
			log.info("END: *** getCargoContainerStatus Result *****" + typecd.toString());
		} catch (NullPointerException e) {
			log.info("exception: getCargoContainerStatus ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getCargoContainerStatus ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoContainerStatus  DAO  END");
		}
		return typecd;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getPM4ID()

	@Override
	public String getPM4ID(String cntrAsnNo, String varCode, Boolean isCntr) throws BusinessException {
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		String cargoContainerStatus = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getPM4ID  DAO  Start cntrAsnNo:" + CommonUtility.deNull(cntrAsnNo) + "varCode:"
					+ CommonUtility.deNull(varCode) + "isCntr:" + isCntr);

			if (isCntr == true) {
				sb.append(" SELECT PM4_ID FROM PM4 WHERE VV_CD =:varCode and CNTR_NBR =:cntrAsnNo ");
			} else {
				sb.append(" SELECT PM4_ID FROM PM4 WHERE UCR_NBR = (SELECT BL_NBR FROM ");
				sb.append(" GB_EDO WHERE EDO_ASN_NBR =:cntrAsnNo) OR ");
				sb.append(" BL_NBR = (SELECT BL_NBR FROM GB_EDO WHERE EDO_ASN_NBR =:cntrAsnNo) ");
				sb.append(" UNION SELECT PM4_ID FROM PM4 WHERE UCR_NBR = (SELECT ");
				sb.append(" BK_REF_NBR FROM ESN WHERE ESN_ASN_NBR =:cntrAsnNo) OR ");
				sb.append(" BL_NBR = (SELECT BK_REF_NBR FROM ESN WHERE ESN_ASN_NBR =:cntrAsnNo) ");
			}

			log.info(" ***getPM4ID SQL *****" + sb.toString());

			if (isCntr == true) {
				paramMap.put("varCode", varCode);
				paramMap.put("cntrAsnNo", cntrAsnNo);
			} else {
				paramMap.put("cntrAsnNo", cntrAsnNo);
			}

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				cargoContainerStatus = rs.getString(1);
			}
			log.info("END: *** getPM4ID Result *****" + cargoContainerStatus.toString());
		} catch (NullPointerException e) {
			log.info("exception: getPM4ID ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getPM4ID ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPM4ID  DAO  END");
		}
		return cargoContainerStatus;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getOogDimension()

	@Override
	public List<ContainerValueObject> getOogDimension(String cntrNo, String varCode) throws BusinessException {
		List<ContainerValueObject> dimensionList = new ArrayList<ContainerValueObject>();
		ContainerValueObject cntrValueObj;
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getOogDimension  DAO  Start cntrNo:" + CommonUtility.deNull(cntrNo) + "varCode:"
					+ CommonUtility.deNull(varCode));

			sb.append(" SELECT OOG_OH, OOG_OL_FRONT, OOG_OL_BACK, OOG_OW_RIGHT, OOG_OW_LEFT ");
			sb.append(" FROM CNTR WHERE CNTR_NBR =:cntrNo ");
			sb.append(" AND (DISC_VV_CD =:varCode OR NOM_DISC_VV_CD =:varCode ");
			sb.append(" OR NOM_LOAD_VV_CD =:varCode OR LOAD_VV_CD =:varCode) ");

			log.info(" ***getOogDimension SQL *****" + sb.toString());

			paramMap.put("cntrNo", cntrNo);
			paramMap.put("varCode", varCode);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				cntrValueObj = new ContainerValueObject();
				cntrValueObj.setOogOH(Integer.parseInt(rs.getString("OOG_OH")));
				cntrValueObj.setOogOlFront(Integer.parseInt(rs.getString("OOG_OL_FRONT")));
				cntrValueObj.setOogOlBack(Integer.parseInt(rs.getString("OOG_OL_BACK")));
				cntrValueObj.setOogOwRight(Integer.parseInt(rs.getString("OOG_OW_RIGHT")));
				cntrValueObj.setOogOwRight(Integer.parseInt(rs.getString("OOG_OW_LEFT")));
				dimensionList.add(cntrValueObj);
				break;
			}
			log.info("END: *** getOogDimension Result *****" + dimensionList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getOogDimension ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getOogDimension ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getOogDimension  DAO  END");
		}
		return dimensionList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->processForNewTpaEmptyNormal()

	@Override
	public String processForNewTpaEmptyNormal(MiscAppValueObject miscApp, VehicleVO vehVo, String vehicleItems,
			int hoursPerBlock) throws BusinessException {
		try {
			log.info("START: processForNewTpaEmptyNormal *** miscApp: " + miscApp.toString() + ", vehVo: "
					+ vehVo.toString() + ", vehicleItems: " + CommonUtility.deNull(vehicleItems));
			String miscSeqNumber = "";
			List<MiscAppParkingAreaObject> threeBlockSlotList = new ArrayList<MiscAppParkingAreaObject>();
			List<MiscAppParkingAreaObject> currentBlockSlotList = new ArrayList<MiscAppParkingAreaObject>();
			List<MiscAppParkingAreaObject> availableAreaList = new ArrayList<MiscAppParkingAreaObject>();
			String[] assignedArea = new String[5];
			String[] assignedSlot = new String[5];
			JSONArray vehItems = null;
			vehItems = new JSONArray(vehicleItems);
			int vehSize = vehItems.length();
			JSONObject o = null;

			Timestamp fromDate = getTimeStampFromString(vehVo.getFr_dttm(), "ddMMyyyy HHmm");
			Timestamp oneBlockAgo = new Timestamp(fromDate.getTime() - (60 * 60 * 1000 * hoursPerBlock));
			String oneBlockAgoStr = formatTimeStamp(oneBlockAgo, "ddMMyyyy HHmm");

			Timestamp toDate = getTimeStampFromString(vehVo.getTo_dttm(), "ddMMyyyy HHmm");
			Timestamp oneBlockNext = new Timestamp(toDate.getTime() + (60 * 60 * 1000 * hoursPerBlock));
			String oneBlockNextStr = formatTimeStamp(oneBlockNext, "ddMMyyyy HHmm");

			threeBlockSlotList = this.getAvailableParkingSlots4NewTpa(vehVo.getCargo_type(), oneBlockAgoStr,
					oneBlockNextStr, String.valueOf(vehVo.getTrailer_size()), vehVo.getTrailer_type());
			currentBlockSlotList = this.getAvailableParkingSlots4NewTpa(vehVo.getCargo_type(), vehVo.getFr_dttm(),
					vehVo.getTo_dttm(), String.valueOf(vehVo.getTrailer_size()), vehVo.getTrailer_type());
			availableAreaList = this.getParkingAreaList4NewTpa(vehVo.getCargo_type(), vehVo.getFr_dttm(),
					vehVo.getTo_dttm(), String.valueOf(vehVo.getTrailer_size()), vehVo.getTrailer_type());

			// ===================================Start
			// processing=============================================
			if (currentBlockSlotList != null && currentBlockSlotList.size() < vehSize) {
				// Return 0 if the number of available slots are smaller than no of applied
				// trailers
				log.info("Return empty");

				miscSeqNumber = "";
			} else if (currentBlockSlotList != null && currentBlockSlotList.size() >= vehSize) {
				for (int i = 0; i < vehSize; i++) {
					o = vehItems.getJSONObject(i);
					if (o.getString("veh_chas_nbr") != null && !"".equalsIgnoreCase(o.getString("veh_chas_nbr"))) {
						if (o.getString("pref_area_cd") != null && !"".equalsIgnoreCase(o.getString("pref_area_cd"))) {
							List<Object> resultList = this.assignSlot(o.getString("pref_area_cd"), threeBlockSlotList,
									currentBlockSlotList);
							if (resultList.size() == 5) {
								// To assign parking slot for each vehicle
								assignedArea[i] = (String) resultList.get(1);
								assignedSlot[i] = (String) resultList.get(2);

								Object listObject = null;
								listObject = resultList.get(3);
								threeBlockSlotList = new ArrayList<MiscAppParkingAreaObject>();
								if (listObject instanceof List) {
									for (int j = 0; j < ((List<?>) listObject).size(); j++) {
										Object item = ((List<?>) listObject).get(j);
										if (item instanceof Object) {
											threeBlockSlotList.add((MiscAppParkingAreaObject) item);
										}
									}
								}

								listObject = resultList.get(4);
								currentBlockSlotList = new ArrayList<MiscAppParkingAreaObject>();
								if (listObject instanceof List) {
									for (int j = 0; j < ((List<?>) listObject).size(); j++) {
										Object item = ((List<?>) listObject).get(j);
										if (item instanceof Object) {
											currentBlockSlotList.add((MiscAppParkingAreaObject) item);
										}
									}
								}
							} else {
								for (int j = 0; j <= availableAreaList.size(); j++) {
									MiscAppParkingAreaObject areaObj = (MiscAppParkingAreaObject) availableAreaList
											.get(j);
									if (o.getString("pref_area_cd").equalsIgnoreCase(areaObj.getAreaCode())) {
										availableAreaList.remove(j);
									} else {
										List<Object> result = new ArrayList<Object>();
										result = this.assignSlot(areaObj.getAreaCode(), threeBlockSlotList,
												currentBlockSlotList);
										if (result.size() == 5) {
											// To assign parking slot for each vehicle
											assignedArea[i] = (String) result.get(1);
											assignedSlot[i] = (String) result.get(2);

											Object listObject = null;
											listObject = result.get(3);
											threeBlockSlotList = new ArrayList<MiscAppParkingAreaObject>();
											if (listObject instanceof List) {
												for (int k = 0; k < ((List<?>) listObject).size(); k++) {
													Object item = ((List<?>) listObject).get(k);
													if (item instanceof Object) {
														threeBlockSlotList.add((MiscAppParkingAreaObject) item);
													}
												}
											}

											listObject = result.get(4);
											currentBlockSlotList = new ArrayList<MiscAppParkingAreaObject>();
											if (listObject instanceof List) {
												for (int k = 0; k < ((List<?>) listObject).size(); k++) {
													Object item = ((List<?>) listObject).get(k);
													if (item instanceof Object) {
														currentBlockSlotList.add((MiscAppParkingAreaObject) item);
													}
												}
											}
											break;
										}
									}
								}
							}
						} else {
							log.info("preferredArea is empty");
							// MiscAppParkingAreaObject miscAppParkingAreaObject =
							// (MiscAppParkingAreaObject)parkingSlotList.get(0);
							// assignedArea[i] = miscAppParkingAreaObject.getAreaCode();
							// assignedSlot[i] = miscAppParkingAreaObject.getSlotNumber();

							// parkingSlotList.remove(0);
						}

					} else {
						log.info("Vehicle no is empty");
					}
				}

				// To add and approved
				miscSeqNumber = this.autoAssignApproveNewTpa(miscApp, vehVo, vehItems, assignedArea, assignedSlot);
			}

			// ===================================End
			// processing===============================================

			log.info("END: *** processForNewTpaEmptyNormal Result *****" + miscSeqNumber.toString());
			return miscSeqNumber;
		} catch (BusinessException e) {
			log.info("exception: processForNewTpaEmptyNormal ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: processForNewTpaEmptyNormal ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: processForNewTpaEmptyNormal ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: processForNewTpaEmptyNormal  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->submitNewTpa()

	@Override
	public String submitNewTpa(MiscAppValueObject miscApp, VehicleVO veh, String vehDet) throws BusinessException {
		try {
			log.info("START: submitNewTpa  DAO  Start Obj " + " miscApp:" + miscApp + ", veh: " + veh + ", vehDet: "
					+ CommonUtility.deNull(vehDet));
			String miscSeqNbr = null;
			miscSeqNbr = this.addMiscApp4NewTPA(miscApp);
			Timestamp tmp = getCurrentTimeStamp();
			if (miscSeqNbr != null && !"".equalsIgnoreCase(miscSeqNbr)) {
				veh.setMisc_seq_nbr(miscSeqNbr);
				this.addVehicle4NewTPA(veh);
				// Process vehicleItems

				JSONArray vehItems = null;
				vehItems = new JSONArray(vehDet);
				VehicleDetailsVO item = null;
				JSONObject o = null;
				for (int i = 0; i < vehItems.length(); i++) {
					try {
						o = vehItems.getJSONObject(i);
						item = new VehicleDetailsVO();
						item.setMisc_seq_nbr(miscSeqNbr);
						item.setItem_nbr(i + 1);
						item.setAsn_nbr(o.getString("asn_nbr"));
						item.setCntr_nbr(o.getString("cntr_nbr"));
						item.setCntr_crg_status_nm(o.getString("cntr_crg_status_nm"));
						item.setCntr_crg_status(o.getString("cntr_crg_status"));
						item.setRemarks(o.getString("remarks"));
						item.setPref_area_cd(o.getString("pref_area_cd"));
						item.setVeh_chas_nbr(o.getString("veh_chas_nbr"));
						item.setLast_modify_user_id(miscApp.getSubmitBy());
						item.setLast_modify_dttm(tmp);
						this.addVehicleDetails4NewTPA(item);
					} catch (Exception e) {
						log.info("Exception submitNewTpa : ", e);
					}
				}
			}
			log.info("END: addMiscApp4NewTPA Result **** miscSeqNbr: " + CommonUtility.deNull(miscSeqNbr));

			return miscSeqNbr;
		} catch (BusinessException e) {
			log.info("exception: addMiscApp4NewTPA ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: addMiscApp4NewTPA ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: addMiscApp4NewTPA ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: addMiscApp4NewTPA  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->approveRejectNewTpa()

	@Override
	public String approveRejectNewTpa(String userId, String status, String remarks, String misc_seq_nbr,
			String vehicleItems, String apprDttm, String preStatus, int hoursPerBlock) throws BusinessException {
		String result = "";
		try {

			log.info("START: approveRejectNewTpa obj ** userId: " + CommonUtility.deNull(userId) + ", status: "
					+ CommonUtility.deNull(status) + ", remarks: " + CommonUtility.deNull(remarks) + ", misc_seq_nbr: "
					+ CommonUtility.deNull(misc_seq_nbr) + ", vehicleItems: " + CommonUtility.deNull(vehicleItems)
					+ ", apprDttm: " + CommonUtility.deNull(apprDttm) + ", preStatus: "
					+ CommonUtility.deNull(preStatus) + ", hoursPerBlock: " + hoursPerBlock);

			VehicleVO veh = getVehicle4NewTPA(misc_seq_nbr);
			List<Object> vector = autoGetAssignAreaList4NewTPA(veh, vehicleItems, hoursPerBlock, preStatus);
			if (vector.size() > 1) {
				this.approveApplication(userId, status, misc_seq_nbr, remarks, apprDttm);
				this.updateVehForApproveReject(userId, misc_seq_nbr, vehicleItems, vector);
				result = TpaConstants.TPA_RESULT_OK;
			} else {
				result = TpaConstants.TPA_RESULT_ERROR;
			}

		} catch (BusinessException e) {
			log.info("exception: approveRejectNewTpa ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: approveRejectNewTpa ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception approveRejectNewTpa : ", e);
		} finally {
			log.info("END: approveRejectNewTpa  DAO  END");
		}
		log.info("END:  approveRejectNewTpa Result **** result: " + result.toString());
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateNewTpa()
	@Override
	public void updateNewTpa(MiscAppValueObject miscApp, VehicleVO veh, String vehDet) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Timestamp tmp = getCurrentTimeStamp();
		sb.append(" delete /* MiscAppEJB - updateTrailerParkingApplicationDetails(vehDelSql) */ ");
		sb.append(" from misc_vehicle_det where misc_seq_nbr = :appSeqNbr ");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: updateNewTpa  DAO  Start miscApp:" + miscApp.toString() + "veh:" + veh.toString()
					+ "vehDet:" + vehDet);

			if (miscApp.getAppSeqNbr() != null && !"".equalsIgnoreCase(miscApp.getAppSeqNbr())) {
				this.updateMiscApp4NewTpa(miscApp);
				this.updateVehicle4NewTPA(veh);

				log.info(" ***updateNewTpa SQL *****" + sb.toString());

				paramMap.put("appSeqNbr", miscApp.getAppSeqNbr());

				log.info(" *** paramMap: *****" + paramMap.toString());

				namedParameterJdbcTemplate.update(sb.toString(), paramMap);

				// Process vehicleItems
				JSONArray vehItems = null;
				vehItems = new JSONArray(vehDet);
				VehicleDetailsVO item = null;
				JSONObject o = null;
				for (int i = 0; i < vehItems.length(); i++) {
					try {
						o = vehItems.getJSONObject(i);
						item = new VehicleDetailsVO();
						item.setMisc_seq_nbr(miscApp.getAppSeqNbr());
						item.setItem_nbr(i + 1);
						item.setAsn_nbr(o.getString("asn_nbr"));
						item.setCntr_nbr(o.getString("cntr_nbr"));
						item.setCntr_crg_status_nm(o.getString("cntr_crg_status_nm"));
						item.setCntr_crg_status(o.getString("cntr_crg_status"));
						item.setRemarks(o.getString("remarks"));
						item.setPref_area_cd(o.getString("pref_area_cd"));
						item.setVeh_chas_nbr(o.getString("veh_chas_nbr"));
						item.setSlot_nbr(o.getString("slot_nbr"));
						item.setArea_cd(o.getString("area_cd"));
						item.setLast_modify_user_id(miscApp.getSubmitBy());
						item.setLast_modify_dttm(tmp);
						this.updateVehicleDetails4NewTPA(item);
					} catch (Exception e) {
						log.info("exception: updateNewTpa ", e);
					}
				}
			}
		} catch (BusinessException e) {
			log.info("exception: updateNewTpa ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: updateNewTpa ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateNewTpa ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateNewTpa  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getEmailAddress()

	@Override
	public String getEmailAddress(String userAcct) throws BusinessException {
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		String email = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getEmailAddress  DAO  Start userAcct:" + CommonUtility.deNull(userAcct));

			sb.append(" SELECT EMAIL FROM ADM_USER WHERE USER_ACCT = :userAcct ");

			log.info(" ***getEmailAddress SQL *****" + sb.toString());

			paramMap.put("userAcct", userAcct);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				email = rs.getString("EMAIL");
			}
			log.info("END: *** getEmailAddress Result *****" + email.toString());
		} catch (NullPointerException e) {
			log.info("exception: getEmailAddress ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getEmailAddress ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getEmailAddress  DAO  END");
		}
		return email;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getCustomerList4NewTpa()
	// HaiTTH1 added on 18/2/2014

	@Override
	public List<MiscAppValueObject> getCustomerList4NewTpa(String userId, String coCd, String custName)
			throws BusinessException {
		List<MiscAppValueObject> custList = new ArrayList<MiscAppValueObject>();
		MiscAppValueObject miscAppObj;
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (custName == null)
			custName = "";

		try {
			log.info("START: getCustomerList4NewTpa  DAO  Start userId:" + CommonUtility.deNull(userId) + "coCd:"
					+ CommonUtility.deNull(coCd) + "custName:" + CommonUtility.deNull(custName));

			if ("JP".equals(coCd)) {
				sb.append(" select /* MiscAppEJB - getCustomerList() */ co_cd, co_nm  ");
				sb.append(" from company_code cc  ");
				sb.append(" left join cust_acct ca on cc.co_cd = ca.cust_cd  ");
				sb.append(" where co_nm like UPPER(:custName)  ");
				sb.append(" and ca.business_type = 'G'  and ca.acct_status_cd = 'A'  ");
				sb.append(" and rec_status = 'A' order by co_nm ");
			} else if (!"JP".equals(coCd)) {
				sb.append(" select /* MiscAppEJB - getCustomerList() */ distinct a.co_cd, a.co_nm  ");
				sb.append(" from company_code a, logon_acct b, cust_acct c  ");
				sb.append(" where b.login_id = :userId and a.co_cd = b.cust_cd  ");
				sb.append(" and and a.co_cd = c.cust_cd and c.business_type = 'G' and c.acct_status_cd = 'A'  ");
				sb.append(" and rec_status = 'A' ");
			}

			log.info(" ***getCustomerList4NewTpa SQL *****" + sb.toString());

			if ("JP".equals(coCd)) {
				paramMap.put("custName", custName + "%");
			} else if (!"JP".equals(coCd)) {
				paramMap.put("userId", userId);
			}

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				miscAppObj = new MiscAppValueObject();
				miscAppObj.setCoCd(rs.getString("co_cd"));
				miscAppObj.setCoName(rs.getString("co_nm"));
				custList.add(miscAppObj);
			}
			log.info("END: *** getCustomerList4NewTpa Result *****" + custList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getCustomerList4NewTpa ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getCustomerList4NewTpa ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCustomerList4NewTpa  DAO  END");
		}
		return custList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateVehForApproveReject()

	@Override
	public void updateVehForApproveReject(String userId, String misc_seq_nbr, String vehicleItems, List<Object> vector)
			throws BusinessException {
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: updateVehForApproveReject  DAO  Start userId:" + CommonUtility.deNull(userId)
					+ "misc_seq_nbr:" + CommonUtility.deNull(misc_seq_nbr) + "vehicleItems:"
					+ CommonUtility.deNull(vehicleItems) + "vector:" + vector);

			sb.append(" UPDATE MISC_VEHICLE SET LAST_MODIFY_USER_ID =:userId, ");
			sb.append(" LAST_MODIFY_DTTM = SYSDATE WHERE MISC_SEQ_NBR = :miscSeqNbr ");

			sb1.append(" UPDATE MISC_VEHICLE_DET ");
			sb1.append(" SET LAST_MODIFY_USER_ID = :userId, LAST_MODIFY_DTTM = SYSDATE, ");
			sb1.append(" AREA_CD = :assignAreaList, SLOT_NBR = :assignSlotList, ");
			sb1.append(" REMARKS = :remarks, PREF_AREA_CD = :prefAreaCd, ");
			sb1.append(" VEH_CHAS_NBR = :vehChasNbr, CNTR_NBR = :cntrNbr, ");
			sb1.append(" ASN_NBR = :asnNbr, CNTR_CRG_STATUS = :status ");
			sb1.append(" WHERE MISC_SEQ_NBR = :miscSeqNbr AND ITEM_NBR= :itemNbr ");

			log.info(" ***updateVehForApproveReject SQL *****" + sb.toString());

			paramMap.put("userId", userId);
			paramMap.put("miscSeqNbr", misc_seq_nbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			log.info("Going to update veh Details========> ");
			JSONArray vehItems = null;
			vehItems = new JSONArray(vehicleItems);
			JSONObject o = null;
			if (vector.size() > 1) {
				String[] assignAreaList = (String[]) vector.get(0);
				String[] assignSlotList = (String[]) vector.get(1);
				paramMap = new HashMap<String, Object>();
				for (int i = 0; i < vehItems.length(); i++) {
					try {
						o = vehItems.getJSONObject(i);
						log.info(" ***updateVehForApproveReject SQL2 *****" + sb1.toString());

						paramMap.put("userId", userId);
						paramMap.put("assignAreaList", assignAreaList[i]);
						paramMap.put("assignSlotList", assignSlotList[i]);
						paramMap.put("remarks", o.getString("remarks"));
						paramMap.put("prefAreaCd", o.getString("pref_area_cd"));
						paramMap.put("vehChasNbr", o.getString("veh_chas_nbr"));
						paramMap.put("cntrNbr", o.getString("cntr_nbr"));
						paramMap.put("asnNbr", o.getString("asn_nbr"));
						paramMap.put("status", o.getString("cntr_crg_status"));
						paramMap.put("miscSeqNbr", misc_seq_nbr);
						paramMap.put("itemNbr", o.getInt("item_nbr"));

						log.info(" *** paramMap: *****" + paramMap.toString());

						namedParameterJdbcTemplate.update(sb1.toString(), paramMap);

					} catch (Exception e) {
						log.info("exception: updateVehForApproveReject ", e);
					}
				}

			}
		} catch (NullPointerException e) {
			log.info("exception: updateVehForApproveReject ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateVehForApproveReject ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateVehForApproveReject  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getVehicle4NewTPA()

	@Override

	public VehicleVO getVehicle4NewTPA(String miscSeqNumber) throws BusinessException {
		VehicleVO veh = new VehicleVO();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getVehicle4NewTPA  DAO  Start miscSeqNumber:" + CommonUtility.deNull(miscSeqNumber));
			getATUGracePeriod();
			sb.append(" select * from MISC_VEHICLE where misc_seq_nbr =:miscSeqNumber ");
			log.info(" ***getVehicle4NewTPA SQL *****" + sb.toString());

			paramMap.put("miscSeqNumber", miscSeqNumber);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				veh.setCargo_type(rs.getString("CARGO_TYPE"));
				veh.setTrailer_size(rs.getInt("TRAILER_SIZE"));
				veh.setTrailer_type(rs.getString("TRAILER_TYPE"));
				veh.setFr_dttm(rs.getString("FR_DTTM"));
				veh.setTo_dttm(rs.getString("TO_DTTM"));
				veh.setMisc_seq_nbr(rs.getString("MISC_SEQ_NBR"));
			}
			log.info("END: *** getVehicle4NewTPA Result *****" + veh.toString());
		} catch (BusinessException e) {
			log.info("exception: getVehicle4NewTPA ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getVehicle4NewTPA ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getVehicle4NewTPA ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVehicle4NewTPA  DAO  END");
		}
		return veh;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getMiscAppTypePendingCases()

	@Override

	public List<MiscAppValueObject> getMiscAppTypePendingCases() throws BusinessException {
		List<MiscAppValueObject> list = new ArrayList<MiscAppValueObject>();
		MiscAppValueObject miscAppObj;
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getMiscAppTypePendingCases  DAO  Start ");

			sb.append(" select app_type,count(app_type) no_case  from misc_app ");
			sb.append(" where app_status in( 'S','U') group by app_type order by app_type ");

			log.info(" ***getMiscAppTypePendingCases SQL *****" + sb.toString());

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				miscAppObj = new MiscAppValueObject();
				miscAppObj.setAppTypeCd((rs.getString("app_type")));
				miscAppObj.setNoOfCases((rs.getInt("no_case")));
				list.add(miscAppObj);
			}
			log.info("END: *** getMiscAppTypePendingCases Result *****" + list.toString());
		} catch (NullPointerException e) {
			log.info("exception: getMiscAppTypePendingCases ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getMiscAppTypePendingCases ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getMiscAppTypePendingCases  DAO  END");
		}
		return list;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->validateGCStuffIndicatorMiscApp()

	@Override

	public boolean validateGCStuffIndicatorMiscApp(String refNbr) throws BusinessException {
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: validateGCStuffIndicatorMiscApp  DAO  Start refNbr:" + CommonUtility.deNull(refNbr));

			sb.append(" select * from misc_app where app_type = 'SPA' and ");
			sb.append(" app_status <> 'D'  and ref_nbr =:refNbr ");

			log.info(" ***validateGCStuffIndicatorMiscApp SQL *****" + sb.toString());

			paramMap.put("refNbr", refNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				log.info("END: validateGCStuffIndicatorMiscApp  DAO  END Result: true");
				return true;
			}
			log.info("END: *** validateGCStuffIndicatorMiscApp Result *****");
		} catch (NullPointerException e) {
			log.info("exception: validateGCStuffIndicatorMiscApp ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: validateGCStuffIndicatorMiscApp ", e);
			throw new BusinessException("M4201");
		} finally {

		}
		log.info("END: validateGCStuffIndicatorMiscApp  DAO  END Result: false");
		return false;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->checkMiscAppOnvChassis()

	@Override
	public List<String> checkMiscAppOnvChassis(String[] chassisNo) throws BusinessException {
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<String> invalidChassis = new ArrayList<String>();

		try {
			log.info("START: checkMiscAppOnvChassis  DAO  Start chassisNo:" + chassisNo.toString());

			for (int i = 0; i < chassisNo.length; i++) {
				sb.setLength(0);
				if (!chassisNo[i].equals("")) {
					sb.append(
							"select * from gss_veh_info where veh_type = 'TR' and status_cd ='A' and veh_nbr =:chassisNo ");

					log.info(" ***checkMiscAppOnvChassis SQL *****" + sb.toString());

					paramMap.put("chassisNo", chassisNo[i]);

					log.info(" *** paramMap: *****" + paramMap.toString());

					rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
					if (rs.next()) {

					} else {
						invalidChassis.add(chassisNo[i]); // invalid chassis
					}
				}
			}
			log.info("END: *** checkMiscAppOnvChassis Result *****" + invalidChassis.toString());
		} catch (NullPointerException e) {
			log.info("exception: checkMiscAppOnvChassis ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: checkMiscAppOnvChassis ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkMiscAppOnvChassis  DAO  END");
		}
		return invalidChassis;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->checkMiscAppOnvContainer()

	@Override
	public List<String> checkMiscAppOnvContainer(String[] cntrNo) throws BusinessException {
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<String> invalidContainer = new ArrayList<String>();

		try {
			log.info("START: checkMiscAppOnvContainer  DAO  Start cntrNo:" + cntrNo.toString());

			for (int i = 0; i < cntrNo.length; i++) {
				sb.setLength(0);
				if (!cntrNo[i].equals("")) {
					sb.append(" select * from cntr where cntr_nbr =:cntrNo ");

					log.info(" ***checkMiscAppOnvContainer SQL *****" + sb.toString());

					paramMap.put("cntrNo", cntrNo[i]);

					log.info(" *** paramMap: *****" + paramMap.toString());

					rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
					if (rs.next()) {

					} else {
						invalidContainer.add(cntrNo[i]); // invalid container
					}
				}
			}
			log.info("END: *** checkMiscAppOnvContainer Result ***** invalidContainer: " + invalidContainer.toString());
		} catch (NullPointerException e) {
			log.info("exception: checkMiscAppOnvContainer ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: checkMiscAppOnvContainer ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkMiscAppOnvContainer  DAO  END");
		}
		return invalidContainer;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->checkMiscAppOnvAsn()
	@Override

	public List<String> checkMiscAppOnvAsn(String[] asn) throws BusinessException {
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<String> invalidAsn = new ArrayList<String>();

		try {
			log.info("START: checkMiscAppOnvAsn  DAO  Start asn:" + asn.toString());

			for (int i = 0; i < asn.length; i++) {
				sb = new StringBuilder();
				if (!asn[i].equals("")) {
					sb.append(" select edo_asn_nbr asn from gb_edo where edo_status = 'A' ");
					sb.append(" and  edo_asn_nbr =:asn ");
					sb.append(" union ");
					sb.append(" select esn_asn_nbr asn from esn where esn_status = 'A' ");
					sb.append(" and  esn_asn_nbr =:asn ");

					log.info(" ***checkMiscAppOnvAsn SQL *****" + sb.toString());

					paramMap.put("asn", asn[i]);

					log.info(" *** paramMap: *****" + paramMap.toString());

					rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
					if (rs.next()) {

					} else {
						invalidAsn.add(asn[i]); // invalid asn
					}
				}
			}
		} catch (NullPointerException e) {
			log.info("exception: checkMiscAppOnvAsn ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: checkMiscAppOnvAsn ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkMiscAppOnvAsn  DAO  END Result - invalidAsn: " + invalidAsn.toString());
		}
		return invalidAsn;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getATUGracePeriod()
	/*
	 * 
	 * private int getATUGracePeriod() throws BusinessException {
	 * log.info("START getATUGracePeriod()"); int days = 0; Connection con = null;
	 * NamedPreparedStatement pstmt = null; ResultSet rs = null; StringBuilder sb =
	 * new StringBuilder();
	 * 
	 * try { log.info("START: getATUGracePeriod  DAO  Start "); con =
	 * this.jdbcTemplate.getDataSource().getConnection();
	 * 
	 * sb.append(" SELECT VALUE FROM SYSTEM_PARA  WHERE PARA_CD = 'ATUGP' ");
	 * 
	 * 
	 * log.info(" ***getATUGracePeriod SQL *****" + sb.toString());
	 * 
	 * rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(),paramMap);
	 * 
	 * while (rs.next()) { days = rs.getInt("VALUE"); }
	 * log.info("END: *** getATUGracePeriod Result *****" + days); } catch
	 * (SQLException e) { e.printStackTrace();
	 * log.info("exception: getATUGracePeriod " + e.toString()); throw new
	 * BusinessException(
	 * "There are some error with your request. Please contact administrator if problem persists."
	 * ); } catch (NullPointerException e) { e.printStackTrace();
	 * log.info("exception: getATUGracePeriod " + e.toString()); throw new
	 * BusinessException(
	 * "There are some error with your request. Please contact administrator if problem persists."
	 * ); } finally { closeResultSet(rs); closeNamedPreparedStatement(pstmt);
	 * closeConnection(con); log.info("END: getATUGracePeriod  DAO  END"); } return
	 * days; }
	 */

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->insertMiscAppDetails()

	public String insertMiscAppDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String appDate, String conPerson, String conTel) throws BusinessException {
		return insertMiscAppDetails(userId, applyType, status, cust, account, varcode, appDate, conPerson, conTel, "");
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->insertMiscAppDetails()
	/*
	 * public String insertMiscAppDetails( String userId, String applyType, String
	 * status, String cust, String account, String varcode, String appDate) throws
	 * BusinessException{
	 */
	// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

	public String insertMiscAppDetails(String userId, String applyType, String status, String cust, String account,
			String varcode, String appDate, String conPerson, String conTel, String conEmail) throws BusinessException {
		String miscSeqNbr = null;
		String miscRefNbr = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: insertMiscAppDetails  DAO  Start userId:" + CommonUtility.deNull(userId) + " applyType:"
					+ CommonUtility.deNull(applyType) + " status:" + CommonUtility.deNull(status) + " cust:"
					+ CommonUtility.deNull(cust) + " account:" + CommonUtility.deNull(account) + " varcode:"
					+ CommonUtility.deNull(varcode) + " appDate:" + CommonUtility.deNull(appDate) + " conPerson:"
					+ CommonUtility.deNull(conPerson) + " conTel:" + CommonUtility.deNull(conTel) + " conEmail:"
					+ CommonUtility.deNull(conEmail));

			sb.append(" insert into misc_app(misc_seq_nbr, ref_nbr, app_type, ");
			sb.append(" app_dttm , app_status, cust_cd, ");
			sb.append(" acct_nbr, vv_cd, create_dttm, create_user_id, ");
			sb.append(" submit_dttm, submit_user_id, ");
			// " support_dttm, support_user_id, support_remarks, approve_dttm, " +
			// " approve_user_id, approve_remarks, close_dttm, close_user_id,
			// approve_bill_dttm, approve_bill_user_id , bill_nbr, " +
			sb.append(" last_modify_user_id, last_modify_dttm, contact_person, ");
			sb.append(" contact_tel, contact_email) values ( ");
			sb.append(" :miscSeqNbr,:miscRefNbr,:applyType,to_date(:appDate,'dd/mm/yyyy  HH24mi'), ");
			sb.append(" :status,:cust,:account,:varcode,sysdate, ");
			sb.append(" :userId,:dttm,:userId1,:userId,sysdate, ");
			sb.append(" :conPerson,:conTel,:conEmail) ");

			// To stop Cash payment temporary.Punitha- 20/11/2008 ***
			if (account == null || account.equals("")) {
				log.info("ERROR-AddMiscApplnWithoutAcct cust:" + cust + ", userId:" + userId + ", applyType:"
						+ applyType + ", account:" + account);
				throw new BusinessException("Application without account is not allowed.");

			}
			// **********************************************
			miscSeqNbr = getNextMiscSeqNumber();
			/*
			 * log.info("miscSeqNbr ========> " + miscSeqNbr); log.info("status ========> "
			 * + status); log.info("account ========> " + account);
			 * log.info("appDate ========> " + appDate);
			 */

			log.info(" ***insertMiscAppDetails SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);
			if (status != null && status.equals("S")) {
				miscRefNbr = applyType + getNextMiscRefNumber();
				// log.info("miscRefNbr ========> " + miscRefNbr);
				if (account != null && !account.equals("")) {
					paramMap.put("miscRefNbr", miscRefNbr);
				} else {
					paramMap.put("miscRefNbr", miscRefNbr + "C");
				}
			} else {
				paramMap.put("miscRefNbr", null);
			}
			paramMap.put("applyType", applyType);
			paramMap.put("appDate", appDate);
			paramMap.put("status", status);
			paramMap.put("cust", cust);
			paramMap.put("account", account);
			paramMap.put("varcode", varcode);
			paramMap.put("userId", userId);

			if (status != null && status.equals("S")) {
				paramMap.put("dttm", CommonUtility.toTimestamp(CommonUtility.getSysDate()));
				paramMap.put("userId1", userId);
			} else {
				paramMap.put("dttm", null);
				paramMap.put("userId1", null);
			}
			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

			paramMap.put("conPerson", conPerson);
			paramMap.put("conTel", conTel);
			paramMap.put("conEmail", conEmail);

			log.info(" *** paramMap: *****" + paramMap.toString());

			// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			// log.info("<========= End insertMiscAppDetails() ========>
			// ");
			log.info("END: *** insertMiscAppDetails Result *****" + miscSeqNbr.toString());
		} catch (BusinessException e) {
			log.info("exception: insertMiscAppDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: insertMiscAppDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: insertMiscAppDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertMiscAppDetails  DAO  END");
		}
		return miscSeqNbr;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getNextMiscSeqNumber()

	public String getNextMiscSeqNumber() throws BusinessException {
		// log.info("<========= Start getNextMiscSeqNumber()
		// ========> ");
		int seqNbr = 0;
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getNextMiscSeqNumber  DAO  Start");

			sb.append(" SELECT misc_app_seq_nbr.nextval FROM dual ");

			log.info(" ***getNextMiscSeqNumber SQL *****" + sb.toString());

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				seqNbr = rs.getInt(1);
			}
			log.info("END: *** getNextMiscSeqNumber Result *****" + seqNbr);
		} catch (NullPointerException e) {
			log.info("exception: getNextMiscSeqNumber ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getNextMiscSeqNumber ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getNextMiscSeqNumber  DAO  END");
		}
		return seqNbr + "";
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getNextMiscRefNumber()

	public String getNextMiscRefNumber() throws BusinessException {
		// log.info("<========= Start getNextMiscRefNumber()
		// ========> ");
		int seqNbr = 0;
		String refNbr = null;
		GregorianCalendar gcal = new GregorianCalendar();
		String year = gcal.get(Calendar.YEAR) + "";
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getNextMiscRefNumber  DAO  Start");

			sb.append(" SELECT misc_app_ref_nbr.nextval FROM dual ");

			log.info(" ***getNextMiscRefNumber SQL *****" + sb.toString());

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				// get the database values
				seqNbr = rs.getInt(1);
				refNbr = year.substring(2) + "/" + CommonUtility.lPad(seqNbr + "", 5, "0");
			}
			log.info("END: *** getNextMiscRefNumber Result *****" + CommonUtility.deNull(refNbr.toString()));
		} catch (NullPointerException e) {
			log.info("exception: getNextMiscRefNumber ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getNextMiscRefNumber ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getNextMiscRefNumber  DAO  END");
		}
		return refNbr;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getMiscRefNbr()

	public String getMiscRefNbr(String miscSeqNbr) throws BusinessException {
		// log.info("<========= Start getMiscRefNbr() ========> ");
		String refNbr = null;
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getMiscRefNbr  DAO  Start miscSeqNbr:" + CommonUtility.deNull(miscSeqNbr));

			sb.append(" select /* MiscAppEJB - getMiscRefNbr */ ");
			sb.append(" ref_nbr from misc_app where misc_seq_nbr =:miscSeqNbr ");

			log.info(" ***getMiscRefNbr SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				refNbr = rs.getString("ref_nbr");
			}
			log.info("END: *** getMiscRefNbr Result *****" + CommonUtility.deNull(refNbr.toString()));
		} catch (NullPointerException e) {
			log.info("exception: getMiscRefNbr ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getMiscRefNbr ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getMiscRefNbr  DAO  END");
		}
		return refNbr;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getApplicationDttm()

	public String getApplicationDttm(String miscSeqNbr) throws BusinessException {
		String appDttm = null;
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getApplicationDttm  DAO  Start miscSeqNbr:" + CommonUtility.deNull(miscSeqNbr));

			sb.append(" select /* MiscAppEJB - getMiscRefNbr */ app_dttm ");
			// "to_char(app_dttm,'dd/mm/yyyy HH24:mm') app_dttm " +
			sb.append(" from misc_app where misc_seq_nbr =:miscSeqNbr ");

			log.info(" ***getApplicationDttm SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				// appDttm = rs.getString("app_dttm");
				appDttm = CommonUtility.parseDateToFmtStr(rs.getTimestamp("app_dttm"), "ddMMyyyy HHmm");
			}
			log.info("END: *** getApplicationDttm Result *****" + CommonUtility.deNull(appDttm.toString()));
		} catch (NullPointerException e) {
			log.info("exception: getApplicationDttm ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getApplicationDttm ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getApplicationDttm  DAO  END");
		}
		return appDttm;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->sendMail()

	public void sendMail(String alertCd, String subject, String msgBody) throws BusinessException {
		try {
			log.info("START: sendMail  DAO  Start alertCd:" + CommonUtility.deNull(alertCd) + ",subject:"
					+ CommonUtility.deNull(subject) + ",msgBody:" + CommonUtility.deNull(msgBody));
			EmailValueObject evo = new EmailValueObject();
			evo.setRecipientAddress(getMailRecipients(alertCd, "EML")); 
			// Retrieve sender email address
			String sender = gBMiscSenderEmail;
			// String sender = "test@jp.com";
			evo.setSenderAddress(sender);
			evo.setSubject(subject);
			evo.setMessage(msgBody);
			sendMessage(evo);
		} catch (BusinessException e) {
			log.info("exception: sendMail ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("exception: sendMail ", e);
			throw new BusinessException("Unexpected Error.  Unable to connect to database.");
		} finally {
			log.info("END sendMail");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->sendSMS()

	public void sendSMS(String alertCd, String content) throws BusinessException {
		try {
			// Sending SMS
			log.info("START: sendSMS  DAO  Start alertCd:" + CommonUtility.deNull(alertCd) + ",content:"
					+ CommonUtility.deNull(content));
			Sms obj = new Sms();
			String recipients[] = getMailRecipients(alertCd, "SMS");
			List<String> recipientsNo = new ArrayList<String>();
			recipientsNo = Arrays.asList(recipients);
			obj.setToList(recipientsNo);
			obj.setMessage(content);

			sendMessage(obj);
		} catch (BusinessException e) {
			log.info("exception: sendSMS ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception ex) {
			log.info("Exception sendSMS : ", ex);
			throw new BusinessException("M4922");
		} finally {
			log.info("END: sendSMS");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->sendMail()

	public String[] getMailRecipients(String alertCode, String deliveryMode) throws BusinessException {

		log.info("START: getMailRecipients  DAO  Start alertCode:" + CommonUtility.deNull(alertCode) + " deliveryMode:"
				+ CommonUtility.deNull(deliveryMode));
		if (alertCode == null || deliveryMode == null) {
			throw new BusinessException("Invalid input for email recipients");
		}
		// String msgInfo = "GetMailRecipients:- AlertCode= " + alertCode + ",
		// DeliveryMode= " + deliveryMode + ":";
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {

			sb.append(" select /* Misc AppEJB - getMailRecipients() */ account ");
			sb.append(" from exception_alert where alert_code = :alertCode ");
			sb.append(" and delivery_mode = :deliveryMode and rec_status = 'A' ");

			log.info(" ***getMailRecipients SQL *****" + sb.toString());

			paramMap.put("alertCode", alertCode);
			paramMap.put("deliveryMode", deliveryMode);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			List<String> mailList = new ArrayList<String>();
			while (rs.next()) {
				mailList.add(rs.getString("account"));
			}

			String mail[] = (String[]) mailList.toArray(new String[0]);

			log.info("END: *** getMailRecipients Result ***** " + mail.toString());
			return mail;

		} catch (NullPointerException e) {
			log.info("exception: getMailRecipients ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getMailRecipients ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getMailRecipients  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->sendMail()

	public List<Object> getMiscAppDetails(String userId, String applyType, String appSeqNbr, String applyTypeNm)
			throws BusinessException {
		// ");
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		ArrayList<Object> result = new ArrayList<Object>();
		MiscAppValueObject obj = new MiscAppValueObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getMiscAppDetails  DAO  Start userId:" + CommonUtility.deNull(userId) + " applyType:"
					+ CommonUtility.deNull(applyType) + " appSeqNbr:" + CommonUtility.deNull(appSeqNbr)
					+ " applyTypeNm:" + CommonUtility.deNull(applyTypeNm));

			sb.append(" select /* MiscAppEJB - getMiscAppDetails() */ misc_seq_nbr, ");
			sb.append(" ref_nbr, app_type, ");
			sb.append(" to_char(app_dttm,'ddmmyyyy HH24mi') app_dttm, app_status, ");
			sb.append(" cust_cd, acct_nbr, vv_cd, ");
			sb.append(" to_char(create_dttm,'ddmmyyyy HH24mi') create_dttm, create_user_id, ");
			sb.append(" to_char(submit_dttm,'ddmmyyyy HH24mi') submit_dttm, submit_user_id, ");
			sb.append(" to_char(support_dttm,'ddmmyyyy HH24mi') support_dttm, ");
			sb.append(" support_user_id, support_remarks, ");
			sb.append(" to_char(approve_dttm,'ddmmyyyy HH24mi') approve_dttm, ");
			sb.append(" approve_user_id, approve_remarks, ");
			sb.append(" to_char(close_dttm,'ddmmyyyy HH24mi') close_dttm, close_user_id, ");
			sb.append(" to_char(approve_bill_dttm,'ddmmyyyy HH24mi') approve_bill_dttm, ");
			sb.append(" approve_bill_user_id , ");
			sb.append(" bill_nbr, a.last_modify_user_id, a.last_modify_dttm, misc_type_nm, ");
			sb.append(" a.contact_person, a.contact_tel, a.contact_email ");
			sb.append(" from misc_app a,  misc_type_code b ");
			sb.append(" where misc_seq_nbr =:appSeqNbr and b.cat_cd = 'MISC_STAT' ");
			sb.append(" and app_status = b.misc_type_cd ");
			sb.append(" order by misc_seq_nbr ");

			log.info(" ***getMiscAppDetails SQL *****" + sb.toString());

			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				obj.setAppTypeCd(CommonUtility.deNull(rs.getString("app_type")));
				obj.setAppTypeName(applyTypeNm);
				obj.setAppSeqNbr(CommonUtility.deNull(rs.getString("misc_seq_nbr")));
				obj.setAcctNbr(CommonUtility.deNull(rs.getString("acct_nbr")));
				obj.setAppRefNbr(CommonUtility.deNull(rs.getString("ref_nbr")));
				obj.setAppStatusCd(CommonUtility.deNull(rs.getString("app_status")));
				obj.setAppStatusName(CommonUtility.deNull(rs.getString("misc_type_nm")));
				obj.setCoCd(CommonUtility.deNull(rs.getString("cust_cd")));
				obj.setAppDttm(CommonUtility.deNull(rs.getString("app_dttm")));
				obj.setSubmitDttm(CommonUtility.deNull(rs.getString("submit_dttm")));
				// obj.setSubmitBy(CommonUtility.deNull(rs.getString("submit_user_id")));
				obj.setSubmitBy(getUserName(CommonUtility.deNull(rs.getString("submit_user_id"))));
				obj.setSupportDttm(CommonUtility.deNull(rs.getString("support_dttm")));
				// obj.setSupportBy(CommonUtility.deNull(rs.getString("support_user_id")));
				obj.setSupportBy(getUserName(CommonUtility.deNull(rs.getString("support_user_id"))));
				obj.setSupportRemarks(CommonUtility.deNull(rs.getString("support_remarks")));
				obj.setApproveDttm(CommonUtility.deNull(rs.getString("approve_dttm")));
				// obj.setApproveBy(CommonUtility.deNull(rs.getString("approve_user_id")));
				obj.setApproveBy(getUserName(CommonUtility.deNull(rs.getString("approve_user_id"))));
				obj.setCloseDttm(CommonUtility.deNull(rs.getString("close_dttm")));
				// obj.setCloseBy(CommonUtility.deNull(rs.getString("close_user_id")));
				obj.setCloseBy(getUserName(CommonUtility.deNull(rs.getString("close_user_id"))));
				obj.setAppRemarks(CommonUtility.deNull(rs.getString("approve_remarks")));
				obj.setApproveBillDttm(CommonUtility.deNull(rs.getString("approve_bill_dttm")));
				// obj.setApproveBillBy(CommonUtility.deNull(rs.getString("approve_bill_user_id")));
				obj.setApproveBillBy(getUserName(CommonUtility.deNull(rs.getString("approve_bill_user_id"))));
				// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
				obj.setConPerson(CommonUtility.deNull(rs.getString("contact_person")));
				obj.setConTel(CommonUtility.deNull(rs.getString("contact_tel")));
				obj.setConEmail(CommonUtility.deNull(rs.getString("contact_email")));
				// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
				// Added on 18/07/2007 by Punitha.To obtain the varcode value
				obj.setVarCode(CommonUtility.deNull(rs.getString("vv_cd")));
				// Ended by Punitha
			}
			result.add(obj);
			List<MiscCustValueObject> cust = (ArrayList<MiscCustValueObject>) getCustomerDetails(userId, obj.getCoCd(),
					obj.getAcctNbr());
			if (cust.size() > 0) {
				result.add((MiscCustValueObject) cust.get(0));
			}
			log.info("END: *** getMiscAppDetails Result *****" + result.toString());
		} catch (BusinessException e) {
			log.info("exception: getMiscAppDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: getMiscAppDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getMiscAppDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getMiscAppDetails  DAO  END Result: result: " + result.toString());
		}
		return result;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateMiscAppDetails()

	public void updateMiscAppDetails(String userId, String status, String miscSeqNbr, String applyType, String account,
			String appStatusCd, String conPerson, String conTel) throws BusinessException {
		updateMiscAppDetails(userId, status, miscSeqNbr, applyType, account, appStatusCd, conPerson, conTel, "");
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateMiscAppDetails()
	/*
	 * public void updateMiscAppDetails( String userId, String status, String
	 * miscSeqNbr, String applyType, String account, String appStatusCd,String
	 * conPerson,String conTel) throws BusinessException{
	 */
	// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel

	public void updateMiscAppDetails(String userId, String status, String miscSeqNbr, String applyType, String account,
			String appStatusCd, String conPerson, String conTel, String conEmail) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		String miscRefNbr = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: updateMiscAppDetails  DAO  Start userId:" + CommonUtility.deNull(userId) + " status:"
					+ CommonUtility.deNull(status) + " miscSeqNbr:" + CommonUtility.deNull(miscSeqNbr) + " applyType:"
					+ CommonUtility.deNull(applyType) + " account:" + CommonUtility.deNull(account) + " appStatusCd:"
					+ CommonUtility.deNull(appStatusCd) + " conPerson:" + CommonUtility.deNull(conPerson) + " conTel:"
					+ CommonUtility.deNull(conTel) + " conEmail:" + CommonUtility.deNull(conEmail));
//			
			/*
			 * log.info("miscSeqNbr ========> " + miscSeqNbr); log.info("status ========> "
			 * + status); log.info("appStatusCd ========> " + appStatusCd);
			 * log.info("Contact person :"+conPerson); log.info("Contact Tel :"+conTel);
			 */
			if ("S".equals(status) && "D".equals(appStatusCd)) {
				sb.append(" update /* MiscAppEJB - updateMiscAppDetails(sql) */ ");
				sb.append(" misc_app set ref_nbr = :miscRefNbr, ");
				sb.append(" app_status =:status, submit_dttm = sysdate, submit_user_id =:userId, ");
				sb.append(" last_modify_user_id =:userId, last_modify_dttm = sysdate, ");
				// " where misc_seq_nbr = ?";
				// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
				sb.append(" contact_person =:conPerson, contact_tel =:conTel , ");
				sb.append(" contact_email =:conEmail where misc_seq_nbr =:miscSeqNbr ");
			} else {
				sb.append(" update /* MiscAppEJB - updateMiscAppDetails(sql1) */ ");
				sb.append(" misc_app set app_status =:status, last_modify_user_id =:userId, ");
				sb.append(" last_modify_dttm = sysdate, ");
				// " where misc_seq_nbr = ?";
				// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
				sb.append(" contact_person =:conPerson, contact_tel =:conTel, ");
				sb.append(" contact_email =:conEmail  where misc_seq_nbr =:miscSeqNbr ");

			}

			if ("S".equals(status) && "D".equals(appStatusCd)) {

				log.info(" ***updateMiscAppDetails SQL *****" + sb.toString());

				miscRefNbr = applyType + getNextMiscRefNumber();
				// log.info("miscRefNbr ========> " + miscRefNbr);
				if (account != null && !account.equals("")) {
					paramMap.put("miscRefNbr", miscRefNbr);
				} else {
					paramMap.put("miscRefNbr", miscRefNbr + "C");
				}
				paramMap.put("status", status);
				paramMap.put("userId", userId);
				// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
				paramMap.put("conPerson", conPerson);
				paramMap.put("conTel", conTel);
				paramMap.put("conEmail", conEmail);
				// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
				paramMap.put("miscSeqNbr", miscSeqNbr);

			} else {

				log.info(" ***updateMiscAppDetails SQL *****" + sb.toString());
				/*
				 * if("S".equals(appStatusCd)){ paramMap.put(1, appStatusCd); }else{
				 * paramMap.put(1, status); }
				 */
				if (!"D".equals(appStatusCd)) {
					paramMap.put("status", appStatusCd);
				} else {
					paramMap.put("status", status);
				}

				paramMap.put("userId", userId);
				// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
				paramMap.put("conPerson", conPerson);
				paramMap.put("conTel", conTel);
				paramMap.put("conEmail", conEmail);
				// Added on 28/05/2007 by Punitha. To add Contact Person and Contact Tel
				paramMap.put("miscSeqNbr", miscSeqNbr);

			}

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
		} catch (BusinessException e) {
			log.info("exception: updateMiscAppDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: updateMiscAppDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateMiscAppDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateMiscAppDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getMiscAppInfo()

	public MiscAppValueObject getMiscAppInfo(String miscSeqNbr) throws BusinessException {
		// String contact_email = null;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		MiscAppValueObject obj = new MiscAppValueObject();

		try {
			log.info("START: getMiscAppInfo  DAO  Start miscSeqNbr:" + CommonUtility.deNull(miscSeqNbr));

			sb.append(" select /* MiscAppEJB - getMiscRefNbr */ ");
			sb.append(" contact_email, co_nm, app_type, nbr_night, approve_remarks, ");
			sb.append(" fr_dttm, to_dttm, ");
			// "to_char(app_dttm,'dd/mm/yyyy HH24:mm') app_dttm " +
			sb.append(" to_char(fr_dttm,'dd/mm/yyyy') ||  ' 23:59' str_fr_dttm, ");
			sb.append(" to_char(to_dttm,'dd/mm/yyyy') || ' 07:00' str_to_dttm  ");
			sb.append(" from misc_app a left outer join company_code b on a.cust_cd = b.co_cd ");
			sb.append(" left outer join misc_vehicle c on a.misc_seq_nbr = c.misc_seq_nbr ");
			sb.append(" where a.misc_seq_nbr =:miscSeqNbr ");

			log.info(" ***getMiscAppInfo SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				// appDttm = rs.getString("app_dttm");
				obj.setConEmail(CommonUtility.deNull(rs.getString("contact_email")));
				obj.setCoName(CommonUtility.deNull(rs.getString("co_nm")));
				obj.setAppTypeCd(CommonUtility.deNull(rs.getString("app_type")));

				obj.setNoOfNights(CommonUtility.deNull(rs.getString("nbr_night")));

				obj.setApproveRemarks(CommonUtility.deNull(rs.getString("approve_remarks")));
				obj.setFrom_dttm((rs.getDate("fr_dttm")));
				obj.setTo_dttm((rs.getDate("to_dttm")));

				obj.setStr_from_dttm(CommonUtility.deNull(rs.getString(("str_fr_dttm"))));
				obj.setStr_to_dttm(CommonUtility.deNull(rs.getString(("str_to_dttm"))));
			}
			log.info("END: *** getMiscAppInfo Result *****" + obj.toString());
		} catch (NullPointerException e) {
			log.info("exception: getMiscAppInfo ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getMiscAppInfo ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getMiscAppInfo  DAO  END Result: obj: " + obj.toString());
		}
		return obj;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->closeBillApplication()
	// Close Bill Application

	public void closeBillApplication(String userId, String appStatus, String appSeqNbr, String closeDate)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: closeBillApplication  DAO  Start userId:" + CommonUtility.deNull(userId) + " appStatus:"
					+ CommonUtility.deNull(appStatus) + " appSeqNbr:" + CommonUtility.deNull(appSeqNbr) + " closeDate:"
					+ CommonUtility.deNull(closeDate));

			sb.append(" update /* MiscAppEJB - closeBillApplication() */ misc_app ");
			sb.append(" set app_status =:appStatus, close_dttm = ");
			sb.append(" to_date(:closeDate,'dd/mm/yyyy HH24mi'), ");
			sb.append(" close_user_id =:userId, last_modify_user_id =:userId, ");
			sb.append(" last_modify_dttm = sysdate ");
			sb.append(" where misc_seq_nbr =:appSeqNbr ");

			log.info(" ***closeBillApplication SQL *****" + sb.toString());

			paramMap.put("appStatus", appStatus);
			paramMap.put("closeDate", closeDate);
			paramMap.put("userId", userId);
			paramMap.put("appSeqNbr", appSeqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			int i = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			log.info("i = " + i);
		} catch (NullPointerException e) {
			log.info("exception: closeBillApplication ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: closeBillApplication ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: closeBillApplication  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getNextDocSeqNumber()

	public String getNextDocSeqNumber() throws BusinessException {
		int seqNbr = 0;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: getNextDocSeqNumber  DAO  Start ");

			sb.append(" SELECT misc_doc_seq_nbr.nextval FROM dual ");

			log.info(" ***getNextDocSeqNumber SQL *****" + sb.toString());

			log.info(" *** paramMap: *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				seqNbr = rs.getInt(1);
			}
			log.info("END: *** getNextDocSeqNumber Result *****" + seqNbr);
		} catch (NullPointerException e) {
			log.info("exception: getNextDocSeqNumber ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getNextDocSeqNumber ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getNextDocSeqNumber  DAO  END seqNbr: " + seqNbr + "");
		}
		return seqNbr + "";
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->removeCommonInfo()
	// Hoang Chu start at 6/6/2008
	/**
	 * this function to remove common info
	 *
	 */

	private List<ExpiredCompanyValueObject> removeCommonInfo(List<ExpiredCompanyValueObject> expiredCompanyList) {

		log.info("START: removeCommonInfo obj expiredCompanyList: " + expiredCompanyList.toString());

		// deeply copy expiredCompanyList
		List<ExpiredCompanyValueObject> list = new ArrayList<ExpiredCompanyValueObject>();
		ExpiredCompanyValueObject obj;
		for (int i = 0; i < expiredCompanyList.size(); i++) {
			obj = (ExpiredCompanyValueObject) (expiredCompanyList.get(i));
			list.add((ExpiredCompanyValueObject) obj.clone());
		}

		ExpiredCompanyValueObject currentVO;
		ExpiredCompanyValueObject nextVO;
		ExpiredCompanyValueObject nextCopiedVO;

		for (int i = 0; i < expiredCompanyList.size(); i++) {

			currentVO = (ExpiredCompanyValueObject) (expiredCompanyList.get(i));

			if (i < expiredCompanyList.size() - 1) {
				nextVO = (ExpiredCompanyValueObject) (expiredCompanyList.get(i + 1));
				nextCopiedVO = (ExpiredCompanyValueObject) (list.get(i + 1));
				// NguyenQuyen start on 06/12/2008
				if (
				// Application Reference No
				(currentVO.getAppRefNo() != null && nextVO.getAppRefNo() != null)
						&& currentVO.getAppRefNo().equals(nextVO.getAppRefNo())
						// Date of Application
						&& (currentVO.getAppDate() != null && nextVO.getAppDate() != null)
						&& currentVO.getAppDate().equals(nextVO.getAppDate())
						// Application From Date
						&& (currentVO.getFromDate() != null && nextVO.getFromDate() != null)
						&& currentVO.getFromDate().equals(nextVO.getFromDate())
						// Company Name
						&& (currentVO.getCompanyName() != null && nextVO.getCompanyName() != null)
						&& currentVO.getCompanyName().equals(nextVO.getCompanyName())
						// Type of machinery
						&& (currentVO.getMachineType() != null && nextVO.getMachineType() != null)
						&& currentVO.getMachineType().equals(nextVO.getMachineType())) {
					nextCopiedVO.setAppRefNo("");
					nextCopiedVO.setAppDate(null);
					nextCopiedVO.setFromDate(null);
					nextCopiedVO.setCompanyName(null);
					nextCopiedVO.setMachineType(null);
				}
				// NguyenQuyen end on 06/12/2008
			}

		}
		log.info("END: removeCommonInfo Result - list: " + list.toString());
		return list;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->autoAssignApproveTpa()

	public String autoAssignApproveTpa(String userId, String appType, String status, String cust, String account,
			String varcode, String fromDate, String toDate, String noHours, String applicationRemarks, String[] vehNo,
			String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson, String conTel,
			String[] preferredArea, String[] remarks, String reasonForApplication, String cargoType,
			String[] assignedArea, String[] assignedSlot) throws BusinessException {

		log.info("<========= Start  autoAssignApproveTpa() ========> ");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		String miscSeqNbr = null;

		try {
			log.info("START: autoAssignApproveTpa  DAO  Start userId:" + userId + "appType:" + appType + "status:"
					+ status + "cust:" + cust + "account:" + account + "varcode:" + varcode + "fromDate:" + fromDate
					+ "toDate:" + toDate + "noHours:" + noHours + "applicationRemarks:" + applicationRemarks + "vehNo:"
					+ vehNo + "cntNo:" + cntNo + "asnNo:" + asnNo + "coName:" + coName + "appDate:" + appDate
					+ "conPerson:" + conPerson + "conTel:" + conTel + "preferredArea:" + preferredArea + "remarks:"
					+ remarks + "reasonForApplication:" + reasonForApplication + "cargoType:" + cargoType
					+ "assignedArea:" + assignedArea + "assignedSlot:" + assignedSlot);

			sb.append(" insert into misc_vehicle(misc_seq_nbr, fr_dttm, to_dttm, ");
			sb.append(" nbr_night, park_reason, ");
			sb.append(" last_modify_user_id, last_modify_dttm, NBR_HOUR, ");
			sb.append(" PARK_REASON_CD, CARGO_TYPE) values ( ");
			sb.append(" :miscSeqNbr,to_date(:fromDate,'ddMMyyyy HH24MI'), ");
			sb.append(" to_date(:toDate,'ddMMyyyy HH24MI'),:nbrHr,:applicationRemarks, ");
			sb.append(" :userId, sysdate, :noHours, :reasonForApplication, :cargoType) ");

			sb1.append(" insert into misc_vehicle_det(misc_seq_nbr, item_nbr, ");
			sb1.append(" veh_chas_nbr, cntr_nbr, ");
			sb1.append(" asn_nbr, last_modify_user_id, last_modify_dttm, REMARKS, ");
			sb1.append(" PREF_AREA_CD, AREA_CD, SLOT_NBR) values ");
			sb1.append(" (:miscSeqNbr,:itemNbr,:vehNo,:cntNo,:asnNo,:userId, ");
			sb1.append(" sysdate, :remarks, :preferredArea, :assignedArea, :assignedSlot) ");

			miscSeqNbr = autoInsertMiscTpaDetails(userId, appType, status, cust, account, varcode, appDate, conPerson,
					conTel);

			log.info(" ***autoAssignApproveTpa SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);
			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			// Number of night - default to 0
			paramMap.put("nbrHr", "0");
			paramMap.put("applicationRemarks", applicationRemarks);
			paramMap.put("userId", userId);
			paramMap.put("noHours", noHours);
			paramMap.put("reasonForApplication", reasonForApplication);
			paramMap.put("cargoType", cargoType);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			log.info("Going to process veh Details========> ");
			int j = 1;
			paramMap = new HashMap<String, Object>();
			for (int i = 0; i < vehNo.length; i++) {
				log.info(" ***autoAssignApproveTpa SQL *****" + sb1.toString());

				paramMap.put("miscSeqNbr", miscSeqNbr);
				// pstmt1.setInt(2, j);
				paramMap.put("vehNo",
						(vehNo[i] != null && !vehNo[i].equals(""))
								? CommonUtility.getStringTokens(vehNo[i]).toUpperCase()
								: vehNo[i]);
				paramMap.put("cntNo", cntNo[i].toUpperCase());
				paramMap.put("asnNo", asnNo[i]);
				paramMap.put("userId", userId);
				paramMap.put("remarks", remarks[i]);
				paramMap.put("preferredArea", preferredArea[i]);
				paramMap.put("assignedArea", assignedArea[i]);
				paramMap.put("assignedSlot", assignedSlot[i]);
				log.info(" *** paramMap: *****" + paramMap.toString());
				if ((vehNo[i] != null && !vehNo[i].equals(""))) {
					paramMap.put("itemNbr", j);
					log.info(" *** paramMap: *****" + paramMap.toString());
					j++;
					namedParameterJdbcTemplate.update(sb1.toString(), paramMap);
				}
				// if((vehNo[i] != null && !vehNo[i].equals(""))) {
				// namedParameterJdbcTemplate.update(sb.toString(), paramMap);
				// }

			}

			log.info("END: *** autoAssignApproveTpa Result *****" + miscSeqNbr.toString());
			return miscSeqNbr;
		} catch (BusinessException e) {
			log.info("exception: autoAssignApproveTpa ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: autoAssignApproveTpa ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: autoAssignApproveTpa ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: autoAssignApproveTpa  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->autoInsertMiscTpaDetails()

	public String autoInsertMiscTpaDetails(String userId, String appType, String status, String cust, String account,
			String varcode, String appDate, String conPerson, String conTel) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String miscSeqNbr = null;
		String miscRefNbr = null;

		try {
			log.info("START: autoInsertMiscTpaDetails  DAO  Start userId:" + userId + "appType:" + appType + "status:"
					+ status + "cust:" + cust + "account:" + account + "varcode:" + varcode + "appDate:" + appDate
					+ "conPerson:" + conPerson + "conTel:" + conTel);

			sb.append(" insert into misc_app(misc_seq_nbr, ref_nbr, app_type, app_dttm , ");
			sb.append(" app_status, cust_cd, ");
			sb.append(" acct_nbr, vv_cd, create_dttm, create_user_id, submit_dttm, ");
			sb.append(" submit_user_id, approve_dttm, ");
			sb.append(" approve_user_id, approve_remarks, ");
			sb.append(" last_modify_user_id, last_modify_dttm, contact_person, ");
			sb.append(" contact_tel) values (:miscSeqNbr,:miscRefNbr,:appType, ");
			sb.append(" to_date(:appDate,'dd/mm/yyyy  HH24mi'),:status,:cust, ");
			sb.append(" :account,:varcode,sysdate,:userId,:dttm, ");
			sb.append(" :userId,sysdate,:approveDttm,:approveUserId,:userId, ");
			sb.append(" sysdate,:conPerson,:conTel) ");

			miscSeqNbr = getNextMiscSeqNumber();

			log.info(" ***autoInsertMiscTpaDetails SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);
			miscRefNbr = appType + getNextMiscRefNumber();
			if (account != null && !account.equals("")) {
				paramMap.put("miscRefNbr", miscRefNbr);
			} else {
				paramMap.put("miscRefNbr", miscRefNbr + "C");
			}
			paramMap.put("appType", appType);
			paramMap.put("appDate", appDate);
			paramMap.put("status", status);
			paramMap.put("cust", cust);
			paramMap.put("account", account);
			paramMap.put("varcode", varcode);
			paramMap.put("userId", userId);
			paramMap.put("dttm", CommonUtility.toTimestamp(CommonUtility.getSysDate()));
			paramMap.put("approveDttm", "AUTO");
			paramMap.put("approveUserId", "AUTO");
			paramMap.put("conPerson", conPerson);
			paramMap.put("conTel", conTel);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
		} catch (BusinessException e) {
			log.info("exception: autoInsertMiscTpaDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: autoInsertMiscTpaDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: autoInsertMiscTpaDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: autoInsertMiscTpaDetails  DAO  END Result: miscSeqNbr: " + CommonUtility.deNull(miscRefNbr));
		}
		return miscSeqNbr;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->autoAssignApproveForUpdateTpa()

	public String autoAssignApproveForUpdateTpa(String userId, String appType, String status, String cust,
			String account, String varcode, String fromDate, String toDate, String noHours, String applicationRemarks,
			String[] vehNo, String[] cntNo, String[] asnNo, String coName, String appDate, String conPerson,
			String conTel, String[] preferredArea, String[] remarks, String reasonForApplication, String cargoType,
			String seqNbr, String appStatusCd, String[] assignedArea, String[] assignedSlot) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();

		try {
			log.info("START: autoAssignApproveForUpdateTpa  DAO  Start userId:" + userId + "appType:" + appType
					+ "status:" + status + "cust:" + cust + "account:" + account + "varcode:" + varcode + "fromDate:"
					+ fromDate + "toDate:" + toDate + "noHours:" + noHours + "applicationRemarks:" + applicationRemarks
					+ "vehNo:" + vehNo + "cntNo:" + cntNo + "asnNo:" + asnNo + "coName:" + coName + "appDate:" + appDate
					+ "conPerson:" + conPerson + "conTel:" + conTel + "preferredArea:" + preferredArea + "remarks:"
					+ remarks + "reasonForApplication:" + reasonForApplication + "cargoType:" + cargoType);

			sb.append(" update /* MiscAppEJB - autoAssignApproveForUpdateTpa(vehSql) */ ");
			sb.append(" misc_vehicle set fr_dttm = to_date(:fromDate,'ddMMyyyy HH24MI'), ");
			sb.append(" to_dttm = to_date(:toDate,'ddMMyyyy HH24MI'), nbr_night =:nbNight, ");
			sb.append(" park_reason= :applicationRemarks , ");
			sb.append(" last_modify_user_id = :userId, last_modify_dttm = sysdate, ");
			sb.append(" NBR_HOUR = :noHours, PARK_REASON_CD = :reasonForApplication, CARGO_TYPE = :cargoType");
			sb.append(" where misc_seq_nbr = :seqNbr ");

			sb1.append(" delete /* MiscAppEJB - autoAssignApproveForUpdateTpa(vehDelSql) */ ");
			sb1.append(" from misc_vehicle_det where misc_seq_nbr = :seqNbr ");

			sb2.append(" insert into misc_vehicle_det(misc_seq_nbr, item_nbr, veh_chas_nbr, cntr_nbr, ");
			sb2.append(" asn_nbr, last_modify_user_id, last_modify_dttm, REMARKS, ");
			sb2.append(" PREF_AREA_CD, AREA_CD, SLOT_NBR) values ");
			sb2.append(" (:seqNbr,:itemNbr,:vehNo,:cntNo,:asnNo,:userId,sysdate, ");
			sb2.append(" :remarks, :preferredArea,:assignedArea,:assignedSlot) ");

			autoUpdateMiscAppDetails(userId, status, seqNbr, appType, account, appStatusCd, conPerson, conTel);

			log.info(" ***autoAssignApproveForUpdateTpa SQL *****" + sb.toString());

			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			paramMap.put("nbNight", "0");
			paramMap.put("applicationRemarks", applicationRemarks);
			paramMap.put("userId", userId);
			paramMap.put("noHours", noHours);
			paramMap.put("reasonForApplication", reasonForApplication);
			paramMap.put("cargoType", cargoType);
			paramMap.put("seqNbr", seqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			log.info(" ***autoAssignApproveForUpdateTpa SQL *****" + sb1.toString());
			paramMap = new HashMap<String, Object>();
			paramMap.put("seqNbr", seqNbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb1.toString(), paramMap);

			if (vehNo != null) {
				log.info(" ***autoAssignApproveForUpdateTpa SQL *****" + sb2.toString());

				int j = 1;
				paramMap = new HashMap<String, Object>();
				for (int i = 0; i < vehNo.length; i++) {
					paramMap.put("seqNbr", seqNbr);
					// pstmt2.setInt(2, j);
					paramMap.put("vehNo",
							(vehNo[i] != null && !vehNo[i].equals(""))
									? CommonUtility.getStringTokens(vehNo[i]).toUpperCase()
									: vehNo[i]);
					paramMap.put("cntNo", cntNo[i]);
					paramMap.put("asnNo", asnNo[i]);
					paramMap.put("userId", userId);
					paramMap.put("remarks", remarks[i]);
					paramMap.put("preferredArea", preferredArea[i]);
					paramMap.put("assignedArea", assignedArea[i]);
					paramMap.put("assignedSlot", assignedSlot[i]);
					// if((vehNo[i] != null && !vehNo[i].equals(""))){
					// namedParameterJdbcTemplate.update(sb.toString(), paramMap);
					// }
					log.info(" *** paramMap: *****" + paramMap.toString());
					if ((vehNo[i] != null && !vehNo[i].equals(""))) {
						paramMap.put("itemNbr", j);
						log.info(" *** paramMap: *****" + paramMap.toString());
						j++;
						namedParameterJdbcTemplate.update(sb2.toString(), paramMap);
					}
				}
			}
			log.info("END: *** autoAssignApproveForUpdateTpa Result *****" + seqNbr.toString());
			return seqNbr;
		} catch (BusinessException e) {
			log.info("exception: autoAssignApproveForUpdateTpa ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: autoAssignApproveForUpdateTpa ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: autoAssignApproveForUpdateTpa ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: autoAssignApproveForUpdateTpa  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->autoUpdateMiscAppDetails()

	public void autoUpdateMiscAppDetails(String userId, String status, String miscSeqNbr, String applyType,
			String account, String appStatusCd, String conPerson, String conTel) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		String miscRefNbr = "";

		try {

			log.info("START: autoUpdateMiscAppDetails obj userId: " + CommonUtility.deNull(userId) + ", status: "
					+ CommonUtility.deNull(status) + ", miscSeqNbr: " + CommonUtility.deNull(miscSeqNbr)
					+ ", applyType: " + CommonUtility.deNull(applyType) + ", account: " + CommonUtility.deNull(account)
					+ ", appStatusCd: " + CommonUtility.deNull(appStatusCd) + ", conPerson: "
					+ CommonUtility.deNull(conPerson) + ", conTel: " + CommonUtility.deNull(conTel));

			sb.append(" update /* MiscAppEJB - autoUpdateMiscAppDetails(sql) */ ");
			sb.append(" misc_app set ref_nbr = :miscRefNbr, ");
			sb.append(" app_status = :status, submit_dttm = sysdate, submit_user_id = :userId, ");
			sb.append(" last_modify_user_id = :userId, last_modify_dttm = sysdate, ");
			sb.append(" contact_person = :conPerson, contact_tel = :conTel,  approve_dttm =sysdate, ");
			sb.append(" approve_user_id = 'AUTO', approve_remarks ='AUTO' where misc_seq_nbr = :miscSeqNbr ");

			sb1.append(" update /* MiscAppEJB - autoUpdateMiscAppDetails(sql) */ misc_app ");
			sb1.append(" set app_status = :status, submit_dttm = sysdate, ");
			sb1.append(" submit_user_id =:userId, last_modify_user_id = :userId, ");
			sb1.append(" last_modify_dttm = sysdate, ");
			sb1.append(" contact_person = :conPerson, contact_tel = :conTel, ");
			sb1.append(" approve_dttm =sysdate, approve_user_id = 'AUTO', approve_remarks ='AUTO' ");
			sb1.append(" where misc_seq_nbr =:miscSeqNbr ");

			if ("D".equals(appStatusCd)) {
				miscRefNbr = applyType + getNextMiscRefNumber();
				if (account != null && !account.equals("")) {
					paramMap.put("miscRefNbr", miscRefNbr);
				} else {
					paramMap.put("miscRefNbr", miscRefNbr + "C");
				}
				paramMap.put("status", status);
				paramMap.put("userId", userId);
				paramMap.put("conPerson", conPerson);
				paramMap.put("conTel", conTel);
				paramMap.put("miscSeqNbr", miscSeqNbr);

				log.info(" ***autoUpdateMiscAppDetails SQL *****" + sb.toString());
				log.info(" *** paramMap: *****" + paramMap.toString());

				namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			} else {
				log.info(" ***autoUpdateMiscAppDetails SQL *****" + sb1.toString());

				paramMap.put("status", status);
				paramMap.put("userId", userId);
				paramMap.put("conPerson", conPerson);
				paramMap.put("conTel", conTel);
				paramMap.put("miscSeqNbr", miscSeqNbr);

				log.info(" ***autoUpdateMiscAppDetails SQL *****" + sb1.toString());
				log.info(" *** paramMap: *****" + paramMap.toString());

				namedParameterJdbcTemplate.update(sb1.toString(), paramMap);
			}
		} catch (BusinessException e) {
			log.info("exception: autoUpdateMiscAppDetails ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: autoUpdateMiscAppDetails ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: autoUpdateMiscAppDetails ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: autoUpdateMiscAppDetails  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->closeBillApplication4NewTpa()

	public void closeBillApplication4NewTpa(String userId, String appStatus, String misc_seq_nbr,
			String closeForBillRemarks) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START: closeBillApplication4NewTpa  DAO  Start userId:" + CommonUtility.deNull(userId)
					+ "appStatus:" + CommonUtility.deNull(appStatus) + "misc_seq_nbr:"
					+ CommonUtility.deNull(misc_seq_nbr) + "closeForBillRemarks:"
					+ CommonUtility.deNull(closeForBillRemarks));

			sb.append(" UPDATE MISC_APP ");
			sb.append(" SET APP_STATUS =:appStatus, CLOSE_DTTM = SYSDATE, ");
			sb.append(" CLOSE_USER_ID = :userId, LAST_MODIFY_USER_ID = :userId, ");
			sb.append(" LAST_MODIFY_DTTM = SYSDATE, ");
			sb.append(" CLOSE_FOR_BILL_REMARKS = :closeForBillRemarks ");
			sb.append("  WHERE MISC_SEQ_NBR = :miscSeqNbr ");

			log.info(" ***closeBillApplication4NewTpa SQL *****" + sb.toString());

			paramMap.put("appStatus", appStatus);
			paramMap.put("userId", userId);
			paramMap.put("closeForBillRemarks", closeForBillRemarks);
			paramMap.put("miscSeqNbr", misc_seq_nbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
		} catch (NullPointerException e) {
			log.info("exception: closeBillApplication4NewTpa ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: closeBillApplication4NewTpa ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: closeBillApplication4NewTpa  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateTpaForExtendReduce()

	public void updateTpaForExtendReduce(String userId, String appStatus, String misc_seq_nbr, String extend_remarks)
			throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: updateTpaForExtendReduce  DAO  Start userId: " + CommonUtility.deNull(userId)
					+ ",appStatus: " + CommonUtility.deNull(appStatus) + ", misc_seq_nbr: "
					+ CommonUtility.deNull(misc_seq_nbr) + ", extend_remarks: " + CommonUtility.deNull(extend_remarks));

			sb.append(" UPDATE MISC_APP ");
			sb.append(" SET APP_STATUS = :appStatus, EXTEND_DTTM = SYSDATE, ");
			sb.append(" EXTEND_USER_ID = :userId, LAST_MODIFY_USER_ID = :userId, ");
			sb.append(" LAST_MODIFY_DTTM = SYSDATE, ");
			sb.append(" EXTEND_REMARKS = :extendRemarks ");
			sb.append("  WHERE MISC_SEQ_NBR = :miscSeqNbr ");

			log.info(" ***updateTpaForExtendReduce SQL *****" + sb.toString());

			paramMap.put("appStatus", appStatus);
			paramMap.put("userId", userId);
			paramMap.put("extendRemarks", extend_remarks);
			paramMap.put("miscSeqNbr", misc_seq_nbr);

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

		} catch (NullPointerException e) {
			log.info("exception: updateTpaForExtendReduce ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateTpaForExtendReduce ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateTpaForExtendReduce  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->checkQueryObject()

	private boolean checkQueryObject(EnquireQueryObject queryObj, int numberOfTimeSlot, int hourPerBlock) {
		log.info("START: checkQueryObject obj queryObj: " + queryObj.toString() + ", numberOfTimeSlot: "
				+ numberOfTimeSlot + ", hourPerBlock: " + hourPerBlock);

		Calendar startDateTime = miscAppCommonUtility.parseDateTime(queryObj.getStartDate(), queryObj.getStartTime());
		Calendar endDateTime = miscAppCommonUtility.parseDateTime(queryObj.getEndDate(), queryObj.getEndTime());
		int numberOfDays = miscAppCommonUtility.getNumberOfDays();
		long differentTimeInMinutes = (endDateTime.getTimeInMillis() - startDateTime.getTimeInMillis())
				/ MILISECONDS_IN_A_MINUTE;
		log.info("differentTimeInMinutes= " + differentTimeInMinutes);

		if (differentTimeInMinutes == 0) {
			return false;
		}
		if (differentTimeInMinutes > 0 && differentTimeInMinutes <= (numberOfDays * ONE_DAYS_IN_MINUTE)) {
			// update maximum of parking days up to 3 days by thanhbtl6b for TPA Enhancement
			/* Initialize timeslot */

			int[] intializeCountSlotNumber = new int[numberOfTimeSlot];
			Arrays.fill(intializeCountSlotNumber, NOT_APPLICATE_VALUE);
			// long numberAvailableTimeSlot = differentTimeInMinutes / 30;
			long numberAvailableTimeSlot = differentTimeInMinutes / (hourPerBlock * 60);
			// update different time between slots to 12 hours by thanhbtl6b for TPA
			// Enhancement
			log.info("numberAvailableTimeSlot= " + numberAvailableTimeSlot);

			int startHour = startDateTime.get(Calendar.HOUR_OF_DAY);
			log.info("startHour= " + startHour);
			int startMinute = startDateTime.get(Calendar.MINUTE);
			log.info("startMinute= " + startMinute);
			// int startTimeIndex = (startHour * 60 + startMinute)/30;
			int startTimeIndex = (startHour * 60 + startMinute) / (hourPerBlock * 60);
			// update different time between slots to 12 hours by thanhbtl6b for TPA
			// Enhancement

			log.info("startTimeIndex= " + startTimeIndex);
			int endTimeIndex = (int) (startTimeIndex + numberAvailableTimeSlot - 1);
			log.info("endTimeIndex= " + endTimeIndex);
			if (endTimeIndex <= (numberOfTimeSlot - 1)) {
				Arrays.fill(intializeCountSlotNumber, startTimeIndex, endTimeIndex + 1, DEFAULT_VALUE);
			} else {
				Arrays.fill(intializeCountSlotNumber, startTimeIndex, numberOfTimeSlot, DEFAULT_VALUE);
				Arrays.fill(intializeCountSlotNumber, 0, (endTimeIndex - numberOfTimeSlot + 1), DEFAULT_VALUE);
			}
			queryObj.setIntializeCountSlotNumber(intializeCountSlotNumber);

			log.info("END: queryObj Result: true");
			return true;
		} else {
			log.info("END: queryObj Result: false");
			return false;
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->calculateObjectsForSummaryScreen()

	private List<EnquireSummarySlotValueObject> calculateObjectsForSummaryScreen(
			List<EnquireSummarySlotValueObject> avaiableSlotList, EnquireQueryObject queryObj, int numberOfTimeSlot,
			int hourPerBlock) {

		log.info("START: calculateObjectsForSummaryScreen obj avaiableSlotList: " + avaiableSlotList.toString()
				+ ", queryObj: " + queryObj.toString() + ", numberOfTimeSlot: " + numberOfTimeSlot + ", hourPerBlock: "
				+ hourPerBlock);

		List<EnquireSummarySlotValueObject> freeSlotList = getListSlotByFreeTime(avaiableSlotList, queryObj,
				numberOfTimeSlot, hourPerBlock);
		Collections.sort(freeSlotList, new SummaryRowComparator());

		/*
		 * EnquireSummarySlotValueObject enquireSummaryObj1 = null;
		 * log.info("--------> freeSlotList size= " + freeSlotList.size()); for(int j =
		 * 0; j < freeSlotList.size(); j ++) { enquireSummaryObj1 =
		 * (EnquireSummarySlotValueObject) freeSlotList.get(j);
		 * log.info("+++++> area = " + enquireSummaryObj1.getAreaCode());
		 * log.info("+++++> type = " + enquireSummaryObj1.getSlotType());
		 * log.info("+++++> number = " + enquireSummaryObj1.getSlotNumber()); int[]
		 * test = enquireSummaryObj1.getNumberFreeSlot(); for(int k = 0; k <
		 * test.length; k++) { log.info("[" + test[k] + "]"); } }
		 */

		List<EnquireSummarySlotValueObject> summaryRows = new ArrayList<EnquireSummarySlotValueObject>();
		EnquireSummarySlotValueObject enquireSummaryObj = null;
		EnquireSummarySlotValueObject rowObj = null;

		boolean isNewRow = false;
		for (int i = 0; i < freeSlotList.size(); i++) {
			log.info(">>> i  = " + i);
			rowObj = (EnquireSummarySlotValueObject) freeSlotList.get(i);
			// Start modified by Hoa Nguyen for TPA Enhancement on 1-Nov-2013
			isNewRow = (enquireSummaryObj == null)
					|| !(StringUtils.equals(rowObj.getAreaCode(), enquireSummaryObj.getAreaCode())
							&& StringUtils.equals(rowObj.getSlotType(), enquireSummaryObj.getSlotType())
							&& rowObj.getTrailerSize() == enquireSummaryObj.getTrailerSize()
							&& StringUtils.equals(rowObj.getTrailerType(), enquireSummaryObj.getTrailerType()));
			// End modified by Hoa Nguyen for TPA Enhancement on 1-Nov-2013
			if (isNewRow) {
				enquireSummaryObj = new EnquireSummarySlotValueObject();
				enquireSummaryObj.setAreaCode(rowObj.getAreaCode());
				enquireSummaryObj.setSlotType(rowObj.getSlotType());
				enquireSummaryObj.setFlagColunm(queryObj.getIntializeCountSlotNumber(numberOfTimeSlot));
				enquireSummaryObj.setNumberFreeSlot(rowObj.getNumberFreeSlot());

				// Start modified on 26/09/13 by thanhbtl6b for TPA Enhancement
				enquireSummaryObj.setTrailerSize(rowObj.getTrailerSize());
				enquireSummaryObj.setTrailerType(rowObj.getTrailerType());
				// End modified on 26/09/13 by thanhbtl6b for TPA Enhancement

				summaryRows.add(enquireSummaryObj);
			} else {
				for (int j = 0; j < numberOfTimeSlot; j++) {
					enquireSummaryObj.getNumberFreeSlot()[j] = enquireSummaryObj.getNumberFreeSlot()[j]
							+ rowObj.getNumberFreeSlot()[j];
				}
			}
		}
		/*
		 * log.info("--------> summaryRows size= " + summaryRows.size()); for(int j =
		 * 0; j < summaryRows.size(); j ++) { enquireSummaryObj =
		 * (EnquireSummarySlotValueObject) summaryRows.get(j);
		 * log.info("--------> area = " + enquireSummaryObj.getAreaCode());
		 * log.info("--------> type = " + enquireSummaryObj.getSlotType());
		 * log.info("--------> number = " + enquireSummaryObj.getSlotNumber()); int[]
		 * test = enquireSummaryObj.getNumberFreeSlot(); for(int k = 0; k < test.length;
		 * k++) { log.info("[" + test[k] + "]"); }
		 * 
		 * }
		 */

		log.info("END: calculateObjectsForSummaryScreen Result - summaryRows: " + summaryRows.toString());

		return summaryRows;

	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->getListSlotByFreeTime()

	private List<EnquireSummarySlotValueObject> getListSlotByFreeTime(
			List<EnquireSummarySlotValueObject> avaiableSlotList, EnquireQueryObject queryObj, int numberOfTimeSlot,
			int hourPerBlock) {
		log.info("START: getListSlotByFreeTime obj avaiableSlotList: " + avaiableSlotList.toString() + ", queryObj: "
				+ queryObj.toString() + ", numberOfTimeSlot: " + numberOfTimeSlot);
		List<EnquireSummarySlotValueObject> freeSlotList = new ArrayList<EnquireSummarySlotValueObject>();
		EnquireSummarySlotValueObject enquireSummaryObj = null;
		EnquireSummarySlotValueObject rowObj = null;

		Date realStartDateTime = null;
		Date realEndDateTime = null;
		Date queryStartDateTime = (Date) miscAppCommonUtility
				.parseDateTime(queryObj.getStartDate(), queryObj.getStartTime()).getTime();
		Date queryEndDateTime = (Date) miscAppCommonUtility.parseDateTime(queryObj.getEndDate(), queryObj.getEndTime())
				.getTime();

		boolean isNewSlot = false;
		boolean temp1 = false;
		boolean temp2 = false;

		for (int i = 0; i < avaiableSlotList.size(); i++) {

			rowObj = (EnquireSummarySlotValueObject) avaiableSlotList.get(i);
			// Start modified on 26/09/13 by thanhbtl6b for TPA Enhancement
			isNewSlot = (enquireSummaryObj == null)
					|| !(StringUtils.equals(rowObj.getAreaCode(), enquireSummaryObj.getAreaCode())
							&& StringUtils.equals(rowObj.getSlotNumber(), enquireSummaryObj.getSlotNumber())
							&& StringUtils.equals(rowObj.getSlotType(), enquireSummaryObj.getSlotType())
							&& rowObj.getTrailerSize() == enquireSummaryObj.getTrailerSize()
							&& StringUtils.equals(rowObj.getTrailerType(), enquireSummaryObj.getTrailerType()));

			// End modified on 26/09/13 by thanhbtl6b for TPA Enhancement
			if (isNewSlot) {
				enquireSummaryObj = new EnquireSummarySlotValueObject();
				enquireSummaryObj.setAreaCode(rowObj.getAreaCode());
				enquireSummaryObj.setSlotType(rowObj.getSlotType());
				enquireSummaryObj.setSlotNumber(rowObj.getSlotNumber());
				enquireSummaryObj.setNumberFreeSlot(queryObj.getIntializeCountSlotNumber(numberOfTimeSlot));
				// Start modified on 26/09/13 by thanhbtl6b for TPA Enhancement
				enquireSummaryObj.setTrailerSize(rowObj.getTrailerSize());
				enquireSummaryObj.setTrailerType(rowObj.getTrailerType());
				// End modified on 26/09/13 by thanhbtl6b for TPA Enhancement

				freeSlotList.add(enquireSummaryObj);
				// find free time duration of slot and update for time slot array
				if (rowObj.getToDate() == null || rowObj.getFromDate() == null) {
					realStartDateTime = queryStartDateTime;
					realEndDateTime = queryEndDateTime;
					updateFreeSlotTime(enquireSummaryObj, realStartDateTime, realEndDateTime, isNewSlot, hourPerBlock,
							numberOfTimeSlot);
					continue;
				}
				temp1 = queryEndDateTime.after(rowObj.getToDate());
				temp2 = queryStartDateTime.before(rowObj.getFromDate());
				if (temp1 && temp2) {
					realStartDateTime = queryStartDateTime;
					realEndDateTime = (Date) rowObj.getFromDate();
					updateFreeSlotTime(enquireSummaryObj, realStartDateTime, realEndDateTime, isNewSlot, hourPerBlock,
							numberOfTimeSlot);

					realStartDateTime = (Date) rowObj.getToDate();
					realEndDateTime = queryEndDateTime;
					updateFreeSlotTime(enquireSummaryObj, realStartDateTime, realEndDateTime, isNewSlot, hourPerBlock,
							numberOfTimeSlot);
					continue;
				}

				if (temp2) {
					realStartDateTime = queryStartDateTime;
					realEndDateTime = (queryEndDateTime.before(rowObj.getFromDate())) ? queryEndDateTime
							: (Date) rowObj.getFromDate();
					updateFreeSlotTime(enquireSummaryObj, realStartDateTime, realEndDateTime, isNewSlot, hourPerBlock,
							numberOfTimeSlot);
					continue;
				}

				if (temp1) {
					realStartDateTime = (queryStartDateTime.after(rowObj.getToDate())) ? queryStartDateTime
							: (Date) rowObj.getToDate();
					realEndDateTime = queryEndDateTime;
					updateFreeSlotTime(enquireSummaryObj, realStartDateTime, realEndDateTime, isNewSlot, hourPerBlock,
							numberOfTimeSlot);
					continue;
				} else {
					continue;
				}

			}

			// find used time duration in query duration and update for time slot array
			if (rowObj.getToDate() == null || rowObj.getFromDate() == null) {
				continue;
			}
			temp1 = queryEndDateTime.after(rowObj.getToDate());
			temp2 = queryStartDateTime.before(rowObj.getFromDate());
			if (temp1 && temp2) {
				realStartDateTime = (Date) rowObj.getFromDate();
				realEndDateTime = (Date) rowObj.getToDate();
				updateFreeSlotTime(enquireSummaryObj, realStartDateTime, realEndDateTime, isNewSlot, hourPerBlock,
						numberOfTimeSlot);
				continue;
			}

			if (temp2) {
				realStartDateTime = (Date) rowObj.getFromDate();
				realEndDateTime = queryEndDateTime;
				updateFreeSlotTime(enquireSummaryObj, realStartDateTime, realEndDateTime, isNewSlot, hourPerBlock,
						numberOfTimeSlot);
				continue;
			}

			if (temp1) {
				realStartDateTime = queryStartDateTime;
				realEndDateTime = (Date) rowObj.getToDate();
				updateFreeSlotTime(enquireSummaryObj, realStartDateTime, realEndDateTime, isNewSlot, hourPerBlock,
						numberOfTimeSlot);
				continue;
			} else {
				realStartDateTime = queryStartDateTime;
				realEndDateTime = queryEndDateTime;
				updateFreeSlotTime(enquireSummaryObj, realStartDateTime, realEndDateTime, isNewSlot, hourPerBlock,
						numberOfTimeSlot);
				continue;
			}

		}

		log.info("END: getListSlotByFreeTime Result - freeSlotList: " + freeSlotList.toString());

		return freeSlotList;

	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateFreeSlotTime()

	private void updateFreeSlotTime(EnquireSummarySlotValueObject enquireSummaryObj, Date realStartDateTime,
			Date realEndDateTime, boolean isNewSlot, int hourPerBlock, int numberOfTimeSlot) {

		log.info("START: updateFreeSlotTime obj enquireSummaryObj: " + enquireSummaryObj.toString()
				+ " realStartDateTime: " + realStartDateTime.toString() + " realEndDateTime: "
				+ realEndDateTime.toString() + ",isNewSlot: " + isNewSlot + ",hourPerBlock: " + hourPerBlock
				+ ",numberOfTimeSlot: " + numberOfTimeSlot);
		Calendar startCalendar = GregorianCalendar.getInstance();
		startCalendar.setTime(realStartDateTime);

		Calendar endCalendar = GregorianCalendar.getInstance();
		endCalendar.setTime(realEndDateTime);

		// long numberAvailableTimeSlot = (endCalendar.getTimeInMillis() -
		// startCalendar.getTimeInMillis()) / MILISECONDS_IN_HALF_HOUR;
		long numberAvailableTimeSlot = (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis())
				/ (hourPerBlock * MILISECONDS_IN_1_HOUR);
		// change numberAvailableTimeSlot by thanhbtl6b for TPA Enhancement

		if (numberAvailableTimeSlot <= 0) {
			return;
		}

		int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
		int startMinute = startCalendar.get(Calendar.MINUTE);
		// int startTimeIndex = (startHour * 60 + startMinute)/30;
		int startTimeIndex = (startHour * 60 + startMinute) / hourPerBlock * 60;
		// update different time between slots to 12 hours by thanhbtl6b for TPA
		// Enhancement

		int endTimeIndex = (int) (startTimeIndex + numberAvailableTimeSlot - 1);

		if (isNewSlot && endTimeIndex <= (numberOfTimeSlot - 1)) {
			for (int i = startTimeIndex; i <= endTimeIndex; i++) {
				enquireSummaryObj.getNumberFreeSlot()[i] = 1;
			}
		} else if (isNewSlot) {
			for (int i = 0; i < numberOfTimeSlot; i++) {
				if (i < startTimeIndex && i > (endTimeIndex - numberOfTimeSlot)) {
					continue;
				} else {
					enquireSummaryObj.getNumberFreeSlot()[i] = 1;
				}
			}
		} else if (endTimeIndex <= (numberOfTimeSlot - 1)) {
			for (int i = startTimeIndex; i <= endTimeIndex; i++) {
				enquireSummaryObj.getNumberFreeSlot()[i] = 0;
			}
		} else {
			for (int i = 0; i < numberOfTimeSlot; i++) {
				if (i < startTimeIndex && i > (endTimeIndex - numberOfTimeSlot)) {
					continue;
				} else {
					enquireSummaryObj.getNumberFreeSlot()[i] = 0;
				}
			}
		}
		log.info("END: updateFreeSlotTime");
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->calculateCellValue()

	private void calculateCellValue(EnquireListingAppValueObject slotObj, EnquireQueryObject queryObj) {
		log.info("START: calculateCellValue obj slotObj: " + slotObj.toString() + ", queryObj: " + queryObj.toString());
		String[] blockTime = miscAppCommonUtility.getBlockTime();
		String[] timeList = miscAppCommonUtility.createTimeList(blockTime);
		// get number of block search
		int avaiableCol = miscAppCommonUtility.calculateAvailableCol(queryObj.getStartDate(), queryObj.getEndDate());

		String[] cellValueArray = new String[avaiableCol]; // change size from 48 to 6 on 27/09/13 by thanhbtl6b for TPA
		// Enhancement

		// Set to closed when status is closed
		if (Constants.SLOT_STATUS_CLOSE_CD.equalsIgnoreCase(slotObj.getSlotStatus())) {
			for (int i = 0; i < cellValueArray.length; i++) {
				String startTimeRange = timeList[i];
				String toTimeRange = null;
				if (i + 1 == cellValueArray.length) {
					toTimeRange = "0000";
				} else {
					toTimeRange = timeList[i + 1];
				}
				if (!this.checkOccupied(queryObj.getStartDate(), queryObj.getStartTime(), queryObj.getEndDate(),
						queryObj.getEndTime(), startTimeRange, toTimeRange, queryObj.getStartTime(),
						queryObj.getEndTime(), queryObj.getStartDate(), queryObj.getEndDate())) {
					cellValueArray[i] = "NA";
				} else {
					cellValueArray[i] = "CLOSED";
				}

			}
			// Set to reserved when status is reserved
		} else if (Constants.SLOT_STATUS_RESERVED_CD.equalsIgnoreCase(slotObj.getSlotStatus())) {
			for (int i = 0; i < cellValueArray.length; i++) {
				String startTimeRange = timeList[i];
				String toTimeRange = null;
				if (i + 1 == cellValueArray.length) {
					toTimeRange = "0000";
				} else {
					toTimeRange = timeList[i + 1];
				}
				if (!this.checkOccupied(queryObj.getStartDate(), queryObj.getStartTime(), queryObj.getEndDate(),
						queryObj.getEndTime(), startTimeRange, toTimeRange, queryObj.getStartTime(),
						queryObj.getEndTime(), queryObj.getStartDate(), queryObj.getEndDate())) {
					cellValueArray[i] = "NA";
				} else {
					cellValueArray[i] = "RESERVED";
				}

			}
		} else {
			// Set to vehchassis no, remarks, hyper link to view application details(if
			// exist) if it's open
			String fromDate = slotObj.getFromDate();
			String toDate = slotObj.getToDate();
			// log.info("fromDate:" + fromDate);
			// log.info("toDate:" + toDate);
			// log.info("getFromTime:" + slotObj.getFromTime());
			// log.info("getToTime:" + slotObj.getToTime());
			// No date means available
			if (fromDate == null || "".equals(fromDate) || toDate == null || "".equals(toDate)) {
				for (int i = 0; i < cellValueArray.length; i++) {
					String startTimeRange = timeList[i];
					String toTimeRange = null;
					if (i + 1 == cellValueArray.length) {
						toTimeRange = "0000";
					} else {
						toTimeRange = timeList[i + 1];
					}
					if (!this.checkOccupied(queryObj.getStartDate(), queryObj.getStartTime(), queryObj.getEndDate(),
							queryObj.getEndTime(), startTimeRange, toTimeRange, queryObj.getStartTime(),
							queryObj.getEndTime(), queryObj.getStartDate(), queryObj.getEndDate())) {
						cellValueArray[i] = "NA";
					} else {
						cellValueArray[i] = "";
					}

				}
			} else {
				// Occupied, to compare from time, to time value with each time range
				StringBuffer cellValue = new StringBuffer();

				if (!queryObj.isStaff()) {
					cellValue.append("OCCUPIED");
				} else {
					if (queryObj.isDownload()) {
						cellValue.append(slotObj.getVehChassNo());
						if (slotObj.getRemarks() != null && slotObj.getRemarks().trim().length() > 0) {
							cellValue.append(" ");
							cellValue.append("(" + slotObj.getRemarks() + ")");
						}
					} else {
						// <a href="javascript:viewApplicationDetails(miscSeqNbr, appName)">cell
						// value</a>
						cellValue.append("<a href='javascript:viewApplicationDetails(")
								.append(slotObj.getMiscSeqNumber()).append(",").append("\"")
								.append(slotObj.getAppTypeName()).append("\"").append(")'>")
								.append(slotObj.getVehChassNo());
						if (slotObj.getRemarks() != null && slotObj.getRemarks().trim().length() > 0) {
							cellValue.append(" ");
							cellValue.append("(" + slotObj.getRemarks() + ")");
						}

						cellValue.append("</a>");
					}
				}

				for (int i = 0; i < cellValueArray.length; i++) {
					String startTimeRange = timeList[i];
					String toTimeRange = null;
					if (i + 1 == cellValueArray.length) {
						toTimeRange = "0000";
					} else {
						toTimeRange = timeList[i + 1];
					}
					if (!this.checkOccupied(queryObj.getStartDate(), queryObj.getStartTime(), queryObj.getEndDate(),
							queryObj.getEndTime(), startTimeRange, toTimeRange, queryObj.getStartTime(),
							queryObj.getEndTime(), queryObj.getStartDate(), queryObj.getEndDate())) {
						cellValueArray[i] = "NA";
					} else {
						// log.info("----------slotObj.getFromDate():" + slotObj.getFromDate());
						// log.info("----------getFromTime():" + slotObj.getFromTime());
						// log.info("----------slotObj.getFromDate():" + slotObj.getToDate());
						// log.info("----------slotObj.getFromDate():" + slotObj.getToTime());
						// log.info("Calculate for time in selected range");
						if (this.checkOccupied(slotObj.getFromDate(), slotObj.getFromTime(), slotObj.getToDate(),
								slotObj.getToTime(), startTimeRange, toTimeRange, queryObj.getStartTime(),
								queryObj.getEndTime(), queryObj.getStartDate(), queryObj.getEndDate())) {//
							cellValueArray[i] = cellValue.toString();
						} else {
							cellValueArray[i] = "";
						}
					}
				}
			}
		}
		slotObj.setCellValueArray(cellValueArray);
		log.info("END: calculateCellValue Result - cellValueArray: " + cellValueArray.toString());
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->checkOccupied()

	private boolean checkOccupied(String fromDateStr, String fromTime, String toDateStr, String toTime,
			String startTimeRange, String toTimeRange, String selectedFromTime, String selectedToTime,
			String selectedFromDate, String selectedToDate) {
		log.info("START: checkOccupied obj fromDateStr: " + CommonUtility.deNull(fromDateStr) + ", fromTime: "
				+ CommonUtility.deNull(fromTime) + ", toDateStr: " + CommonUtility.deNull(toDateStr) + ", toTime: "
				+ CommonUtility.deNull(toTime) + ", startTimeRange: " + CommonUtility.deNull(startTimeRange)
				+ ", toTimeRange: " + CommonUtility.deNull(toTimeRange) + ", selectedFromTime: "
				+ CommonUtility.deNull(selectedFromTime) + ", selectedFromDate: "
				+ CommonUtility.deNull(selectedFromDate) + ", selectedToDate: " + CommonUtility.deNull(selectedToDate));
		String[] blockTime = miscAppCommonUtility.getBlockTime();
		String[] timeList = miscAppCommonUtility.createTimeList(blockTime);
		Date fromDate = null;
		Date toDate = null;
		Date fromDateRange = null;
		Date toDateRange = null;
		List<String> test = new ArrayList<String>();
		int timeListSize = 0;
		for (int i = 0; i < timeList.length; i++) {
			test.add(i, timeList[i]);
		}
		fromDate = (Date) miscAppCommonUtility.parseStrToDate(fromDateStr + " " + fromTime, "ddMMyyyy HHmm");
		toDate = (Date) miscAppCommonUtility.parseStrToDate(toDateStr + " " + toTime, "ddMMyyyy HHmm");
		timeListSize = timeList.length;
		// Calculate date for startTimeRange and toTimeRange
		if (selectedFromDate.equalsIgnoreCase(selectedToDate)) {
			// log.info("Same day");
			fromDateRange = (Date) miscAppCommonUtility.parseStrToDate(selectedFromDate + " " + startTimeRange,
					"ddMMyyyy HHmm");
			toDateRange = (Date) miscAppCommonUtility.parseStrToDate(selectedFromDate + " " + toTimeRange,
					"ddMMyyyy HHmm");

			// From time and to time contains range
			if (fromDate.compareTo(toDateRange) >= 0 || toDate.compareTo(fromDateRange) <= 0) {
				// log.info("FINISH checkOccupied --> not occupied");
				return false;
			} else {
				// log.info("FINISH checkOccupied --> occupied");
				return true;
			}

		} else {
			String baseDateStr = "";
			int startTimeRangeInd = test.indexOf(startTimeRange);
			int fromTimeInd = test.indexOf(selectedFromTime);
			if (startTimeRangeInd >= fromTimeInd && startTimeRangeInd <= (timeListSize - 1)) {// change
				// "startTimeRangeInd <=
				// 47" to "<=5" by
				// thanhbtl6b on
				// 27/09/13 for TPA
				// Enhancement
				baseDateStr = selectedFromDate;
			} else {
				baseDateStr = selectedToDate;
			}
			fromDateRange = (Date) miscAppCommonUtility.parseStrToDate(baseDateStr + " " + startTimeRange,
					"ddMMyyyy HHmm");
			// HaiTTh1 23/1/2014
			if (startTimeRange.compareTo(toTimeRange) > 0) {
				Date baseDate = null;
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
					baseDate = (Date) dateFormat.parse(baseDateStr);
					Calendar cal = Calendar.getInstance();
					cal.setTime(baseDate);
					log.info(baseDate);
					cal.add(Calendar.DATE, 1);
					baseDateStr = dateFormat.format(cal.getTime());
					log.info("Date increase by one.." + baseDateStr);
				} catch (Exception e) {
					log.info("Exception checkOccupied: ", e);

				}
			}
			// HaiTTH1 23/1/2014
			if (startTimeRangeInd == (timeListSize - 1)) {// change "startTimeRangeInd <= 47" to "<=5" by thanhbtl6b on
				// 27/09/13 for TPA Enhancement
				toDateRange = (Date) miscAppCommonUtility.parseStrToDate(selectedToDate + " " + toTimeRange,
						"ddMMyyyy HHmm");
			} else {
				toDateRange = (Date) miscAppCommonUtility.parseStrToDate(baseDateStr + " " + toTimeRange,
						"ddMMyyyy HHmm");
			}
			// From time and to time contains range
			if (fromDate.compareTo(toDateRange) >= 0 || toDate.compareTo(fromDateRange) <= 0) {
				log.info("END: checkOccupied Result: false");
				return false;
			} else {
				log.info("END: checkOccupied Result: true");
				return true;
			}

		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->calculateCellValueForRow()

	private void calculateCellValueForRow(EnquireListingValueObject slotObj, int avaiableCol) {
		log.info(
				"START: calculateCellValueForRow obj slotObj: " + slotObj.toString() + ", avaiableCol: " + avaiableCol);
		List<EnquireListingAppValueObject> appValueObjs = slotObj.getAppValueObjects();
		if (appValueObjs.size() == 1) {
			EnquireListingAppValueObject enquireListingAppValueObject = (EnquireListingAppValueObject) appValueObjs
					.get(0);
			slotObj.setCellValueArray(enquireListingAppValueObject.getCellValueArray());
		} else {
			String[] cellValueArray = new String[avaiableCol]; // change array size from 48 to 6 by thanhbtl6b for TPA
			// Enhancement
			for (int i = 0; i < appValueObjs.size(); i++) {
				EnquireListingAppValueObject enquireListingAppValueObject = (EnquireListingAppValueObject) appValueObjs
						.get(i);
				String[] cellValueArrayItem = enquireListingAppValueObject.getCellValueArray();
				for (int j = 0; j < avaiableCol; j++) {// change index from 48 to 6 by thanhbtl6b for TPA Enhancement
					if (cellValueArray[j] == null || "".equals(cellValueArray[j])) {
						// OCCUPIED, NA, CLOSED, RESERVED
						if (cellValueArrayItem[j] != null && cellValueArrayItem[j].trim().length() > 0) {
							cellValueArray[j] = cellValueArrayItem[j];
							log.info("cellValueArray " + j + " = " + cellValueArray[j]);
						} else {
							log.info("Cell is available");
							// OPEN
							cellValueArray[j] = "";
						}
					}
				}

			}

			slotObj.setCellValueArray(cellValueArray);
		}
		log.info("End calculateCellValueForRow");
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->setParametersForTpaListSql()

	public Map<String, Object> setParametersForTpaListSql(Map<String, Object> filters) {
		// int index = 1;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: setParametersForTpaListSql obj filters: " + filters.toString());
			if (filters.containsKey("referenceNo")) {
				if (!filters.get("referenceNo").toString().trim().equalsIgnoreCase("")) {
					paramMap.put("referenceNo", "%" + filters.get("referenceNo").toString().toUpperCase().trim() + "%");
				}
			}

			if (filters.containsKey("vehicleTrailerRegistrationNo")) {
				if (!filters.get("vehicleTrailerRegistrationNo").toString().trim().equalsIgnoreCase("")) {
					paramMap.put("vehicleTrailerRegistrationNo",
							"%" + filters.get("vehicleTrailerRegistrationNo").toString().toUpperCase().trim() + "%");
				}
			}

			if (filters.containsKey("containerNo")) {
				if (!filters.get("containerNo").toString().trim().equalsIgnoreCase("")) {
					paramMap.put("containerNo", "%" + filters.get("containerNo").toString().toUpperCase().trim() + "%");
				}
			}

		} catch (Exception e) {
			log.info("Exception setParametersForTpaListSql: ", e);
		}
		log.info("END: setParametersForTpaListSql Result - paramMap: " + paramMap.toString());
		return paramMap;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->assignSlot()

	private List<Object> assignSlot(String pre_area_cd, List<MiscAppParkingAreaObject> threeBlockSlotList,
			List<MiscAppParkingAreaObject> currentBlockSlotList) throws BusinessException {

		try {
			log.info("START: assignSlot Obj pre_area_cd: " + CommonUtility.deNull(pre_area_cd)
					+ ", threeBlockSlotList: " + threeBlockSlotList.toString() + ", currentBlockSlotList: "
					+ currentBlockSlotList.toString());
			boolean assign_flag = false;
			List<Object> resultList = new ArrayList<Object>();
			for (int j = 0; j < threeBlockSlotList.size(); j++) {
				MiscAppParkingAreaObject obj = (MiscAppParkingAreaObject) threeBlockSlotList.get(j);
				if (pre_area_cd.equalsIgnoreCase(obj.getAreaCode())) {
					assign_flag = true;

					threeBlockSlotList.remove(j);
					removeSlotFromList(obj.getAreaCode(), obj.getSlotNumber(), currentBlockSlotList);

					resultList = new ArrayList<Object>();
					resultList.add(0, assign_flag);
					resultList.add(1, obj.getAreaCode());
					resultList.add(2, obj.getSlotNumber());
					resultList.add(3, threeBlockSlotList);
					resultList.add(4, currentBlockSlotList);
					break;
				} else {
					assign_flag = false;
					resultList = new ArrayList<Object>();
					resultList.add(0, assign_flag);
				}
			}

			if (!assign_flag) {
				for (int j = 0; j < currentBlockSlotList.size(); j++) {
					MiscAppParkingAreaObject obj = (MiscAppParkingAreaObject) currentBlockSlotList.get(j);
					if (pre_area_cd.equalsIgnoreCase(obj.getAreaCode())) {
						assign_flag = true;

						currentBlockSlotList.remove(j);
						removeSlotFromList(obj.getAreaCode(), obj.getSlotNumber(), threeBlockSlotList);

						resultList = new ArrayList<Object>();
						resultList.add(0, assign_flag);
						resultList.add(1, obj.getAreaCode());
						resultList.add(2, obj.getSlotNumber());
						resultList.add(3, threeBlockSlotList);
						resultList.add(4, currentBlockSlotList);
						break;
					} else {
						assign_flag = false;
						resultList = new ArrayList<Object>();
						resultList.add(0, assign_flag);
					}
				}
			}
			log.info("END: assignSlot Result - resultList: " + resultList.toString());
			return resultList;
		} catch (NullPointerException e) {
			log.info("exception: assignSlot ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: assignSlot ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: assignSlot  DAO  END ");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->removeSlotFromList()

	private List<MiscAppParkingAreaObject> removeSlotFromList(String areaCd, String slotNbr,
			List<MiscAppParkingAreaObject> sourceList) {
		List<MiscAppParkingAreaObject> resultList = new ArrayList<MiscAppParkingAreaObject>();
		MiscAppParkingAreaObject obj = null;

		log.info("START: removeSlotFromList - areaCd: " + CommonUtility.deNull(areaCd) + ", slotNbr: "
				+ CommonUtility.deNull(slotNbr) + ", sourceList: " + sourceList.toString());

		if (sourceList != null && sourceList.size() > 0) {
			for (int i = 0; i < sourceList.size(); i++) {
				obj = (MiscAppParkingAreaObject) sourceList.get(i);
				if (areaCd.equals(obj.getAreaCode()) && slotNbr.equals(obj.getSlotNumber())) {
					sourceList.remove(i);
				}
			}
		}

		resultList = sourceList;

		log.info("END: removeSlotFromList Result: resultList: " + resultList.toString());

		return resultList;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->autoAssignApproveNewTpa()

	private String autoAssignApproveNewTpa(MiscAppValueObject miscApp, VehicleVO vehVo, JSONArray vehItems,
			String[] assignedArea, String[] assignedSlot) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String miscSeqNbr = null;
		try {
			log.info("START: autoAssignApproveNewTpa  DAO  Start miscApp:" + miscApp + "vehVo:" + vehVo + "vehItems:"
					+ vehItems + "assignedArea:" + assignedArea + "assignedSlot:" + assignedSlot);

			sb.append(" insert into misc_vehicle_det(misc_seq_nbr, item_nbr, veh_chas_nbr, ");
			sb.append(" cntr_nbr, asn_nbr, last_modify_user_id, last_modify_dttm, ");
			sb.append(" REMARKS, PREF_AREA_CD, AREA_CD, SLOT_NBR, CNTR_CRG_STATUS) values ");
			sb.append(" (:miscSeqNbr,:itemNbr,:vehChasNbr,:cntrNbr,:asnNbr, ");
			sb.append(" :submitBy,sysdate, :remarks, :prefAreaCd, :assignedArea, ");
			sb.append(" :assignedSlot, :cntrCrgStatus) ");

			miscApp.setAppStatusCd(TpaConstants.TPA_STATUS_ACCEPTED);
			miscSeqNbr = this.autoAddMiscApp4NewTPA(miscApp);

			vehVo.setMisc_seq_nbr(miscSeqNbr);
			this.addVehicle4NewTPA(vehVo);

			int j = 1;
			JSONObject o = null;
			for (int i = 0; i < vehItems.length(); i++) {
				o = vehItems.getJSONObject(i);
				if (o.getString("veh_chas_nbr") != null && !"".equalsIgnoreCase(o.getString("veh_chas_nbr"))) {
					// VehicleDetailsVO vehDetail = new VehicleDetailsVO();

					log.info(" ***autoAssignApproveNewTpa SQL *****" + sb.toString());

					paramMap.put("miscSeqNbr", miscSeqNbr);
					paramMap.put("itemNbr", j);
					paramMap.put("vehChasNbr",
							(CommonUtility.getStringTokens(o.getString("veh_chas_nbr")).toUpperCase()));
					paramMap.put("cntrNbr", o.getString("cntr_nbr").toUpperCase());
					paramMap.put("asnNbr", o.getString("asn_nbr"));
					paramMap.put("submitBy", miscApp.getSubmitBy());
					paramMap.put("remarks", o.getString("remarks"));
					paramMap.put("prefAreaCd", o.getString("pref_area_cd"));
					paramMap.put("assignedArea", assignedArea[i]);
					paramMap.put("assignedSlot", assignedSlot[i]);
					paramMap.put("cntrCrgStatus", o.getString("cntr_crg_status"));

					log.info(" *** paramMap: *****" + paramMap.toString());

					namedParameterJdbcTemplate.update(sb.toString(), paramMap);
					j++;
				}
			}
			log.info("END: *** autoAssignApproveNewTpa Result *****" + miscSeqNbr.toString());
		} catch (BusinessException e) {
			log.info("exception: autoAssignApproveNewTpa ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: autoAssignApproveNewTpa ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: autoAssignApproveNewTpa ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: autoAssignApproveNewTpa  DAO  END Result miscSeqNbr: " + CommonUtility.deNull(miscSeqNbr));
		}
		return miscSeqNbr;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->autoAddMiscApp4NewTPA()

	private String autoAddMiscApp4NewTPA(MiscAppValueObject miscApp) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String miscSeqNbr = null;
		String miscRefNbr = null;
		try {
			log.info("START: autoAddMiscApp4NewTPA  DAO  Start miscApp:" + miscApp.toString());

			sb.append(" insert into misc_app(misc_seq_nbr, ref_nbr, app_type, app_dttm , ");
			sb.append(" app_status, cust_cd,");
			sb.append(" acct_nbr, vv_cd, create_dttm, create_user_id, submit_dttm, ");
			sb.append(" submit_user_id, approve_dttm, ");
			sb.append(" approve_user_id, approve_remarks, last_modify_user_id, ");
			sb.append(" last_modify_dttm, contact_person, contact_tel, contact_email) ");
			sb.append(" values (:miscSeqNbr,:miscRefNbr,:appTypeCd, ");
			sb.append(" to_date(:appDttm,'dd/mm/yyyy  HH24mi'), :appStatusCd,:coCd, ");
			sb.append(" :acctNbr,:varCode,sysdate,:submitBy,:dttm, ");
			sb.append(" :submitBy,sysdate,:approveUserId,:approveRemarks, ");
			sb.append(" :submitBy,sysdate,:conPerson,:conTel,:conEmail) ");

			miscSeqNbr = getNextMiscSeqNumber();

			log.info(" ***autoAddMiscApp4NewTPA SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);
			miscRefNbr = miscApp.getAppTypeCd() + getNextMiscRefNumber();
			if (miscApp.getAcctNbr() != null && !"".equalsIgnoreCase(miscApp.getAcctNbr())) {
				paramMap.put("miscRefNbr", miscRefNbr);
			} else {
				paramMap.put("miscRefNbr", miscRefNbr + "C");
			}
			paramMap.put("appTypeCd", miscApp.getAppTypeCd());
			paramMap.put("appDttm", miscApp.getAppDttm());
			paramMap.put("appStatusCd", miscApp.getAppStatusCd());
			paramMap.put("coCd", miscApp.getCoCd());
			paramMap.put("acctNbr", miscApp.getAcctNbr());
			paramMap.put("varCode", miscApp.getVarCode());
			paramMap.put("submitBy", miscApp.getSubmitBy());
			paramMap.put("dttm", CommonUtility.toTimestamp(CommonUtility.getSysDate()));
			paramMap.put("approveUserId", "AUTO");
			paramMap.put("approveRemarks", "AUTO");
			paramMap.put("conPerson", miscApp.getConPerson());
			paramMap.put("conTel", miscApp.getConTel());
			paramMap.put("conEmail", miscApp.getConEmail());

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			log.info("END: *** autoAddMiscApp4NewTPA Result *****" + miscSeqNbr.toString());
		} catch (BusinessException e) {
			log.info("exception: autoAddMiscApp4NewTPA ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: autoAddMiscApp4NewTPA ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: autoAddMiscApp4NewTPA ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: autoAddMiscApp4NewTPA  DAO  END Result miscSeqNbr: " + CommonUtility.deNull(miscRefNbr));
		}
		return miscSeqNbr;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->addVehicle4NewTPA()

	private void addVehicle4NewTPA(VehicleVO vo) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: addVehicle4NewTPA  DAO  Start vo:" + vo.toString());
			sb.append(" insert into misc_vehicle(misc_seq_nbr, fr_dttm, to_dttm, ");
			sb.append(" nbr_night, park_reason, ");
			sb.append(" last_modify_user_id, last_modify_dttm, NBR_HOUR, ");
			sb.append(" PARK_REASON_CD, CARGO_TYPE,TRAILER_SIZE, TRAILER_TYPE) values ( ");
			sb.append(" :miscSeqNbr,to_date(:ftDttm,'ddMMyyyy HH24MI'),to_date(:toDttm,'ddMMyyyy HH24MI'), ");
			sb.append(" :nbrNight,:parkReason,:userId, sysdate, ");
			sb.append(" :noHrs, :parkReasonCd, :cargoType, :trailerSize, :trailerType) ");

			// miscSeqNbr = insertMiscAppDetails(conn, userId, appType, status, cust,
			// account, varcode, appDate,conPerson,conTel);

			log.info(" ***addVehicle4NewTPA SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", vo.getMisc_seq_nbr());
			paramMap.put("ftDttm", vo.getFr_dttm());
			paramMap.put("toDttm", vo.getTo_dttm());
			// Default = 0
			paramMap.put("nbrNight", "0");
			paramMap.put("parkReason", vo.getPark_reason());
			paramMap.put("userId", vo.getLast_modify_user_id());
			paramMap.put("noHrs", vo.getNo_of_hours());
			paramMap.put("parkReasonCd", vo.getPark_reason_cd());
			paramMap.put("cargoType", vo.getCargo_type());
			paramMap.put("trailerSize", vo.getTrailer_size());
			paramMap.put("trailerType", vo.getTrailer_type());

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
		} catch (NullPointerException e) {
			log.info("exception: addVehicle4NewTPA ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: addVehicle4NewTPA ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: addVehicle4NewTPA  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->addMiscApp4NewTPA()

	private String addMiscApp4NewTPA(MiscAppValueObject vo) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String miscSeqNbr = null;
		String miscRefNbr = null;

		try {
			log.info("START: addMiscApp4NewTPA  DAO  Start vo:" + vo.toString());

			sb.append(" insert into misc_app(misc_seq_nbr, ref_nbr, app_type, app_dttm , ");
			sb.append(" app_status, cust_cd,");
			sb.append(" acct_nbr, vv_cd, create_dttm, create_user_id, submit_dttm, ");
			sb.append(" submit_user_id, ");
			sb.append(" last_modify_user_id, last_modify_dttm, contact_person, contact_tel, ");
			sb.append(" contact_email) values (");
			sb.append(" :miscSeqNbr,:miscRefNbr,:appType,to_date(:appDttm,'dd/mm/yyyy  HH24mi'), ");
			sb.append(" :appStatusCd,:coCd,:acctNbr,:varCode,sysdate, ");
			sb.append(" :submitBy,:dttm,:submitBy1,:submitBy,sysdate, ");
			sb.append(" :conPerson,:conTel,:conEmail) ");

			// To stop Cash payment temporary.Punitha- 20/11/2008 ***
			if (vo.getAcctNbr() == null || "".equalsIgnoreCase(vo.getAcctNbr())) {
				// LogManager.instance.logInfo("ERROR-AddMiscApplnWithoutAcct cust:" + cust + ",
				// userId:" + userId + ", applyType:" + applyType + ", account:" + account);
				// throw new BusinessException("M22222");
				//
			}
			// **********************************************
			miscSeqNbr = getNextMiscSeqNumber();

			log.info(" ***addMiscApp4NewTPA SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", miscSeqNbr);
			if (vo.getAppStatusCd() != null && "S".equalsIgnoreCase(vo.getAppStatusCd())) {
				miscRefNbr = vo.getAppTypeCd() + getNextMiscRefNumber();
				// LogManager.instance.logInfo("miscRefNbr ========> " + miscRefNbr);
				if (vo.getAcctNbr() != null && !"".equalsIgnoreCase(vo.getAcctNbr())) {
					paramMap.put("miscRefNbr", miscRefNbr);
				} else {
					paramMap.put("miscRefNbr", miscRefNbr + "C");
				}
			} else {
				paramMap.put("miscRefNbr", null);
			}
			paramMap.put("appType", TpaConstants.TPA_TYPE);
			paramMap.put("appDttm", vo.getAppDttm());
			paramMap.put("appStatusCd", vo.getAppStatusCd());
			paramMap.put("coCd", vo.getCoCd());
			paramMap.put("acctNbr", vo.getAcctNbr());
			paramMap.put("varCode", vo.getVarCode());
			paramMap.put("submitBy", vo.getSubmitBy());

			if (vo.getAppStatusCd() != null && "S".equalsIgnoreCase(vo.getAppStatusCd())) {
				paramMap.put("dttm", CommonUtility.toTimestamp(CommonUtility.getSysDate()));
				paramMap.put("submitBy1", vo.getSubmitBy());
			} else {
				paramMap.put("dttm", null);
				paramMap.put("submitBy1", null);
			}
			paramMap.put("conPerson", vo.getConPerson());
			paramMap.put("conTel", vo.getConTel());
			paramMap.put("conEmail", vo.getConEmail());

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

			log.info("END: *** addMiscApp4NewTPA Result *****" + miscSeqNbr.toString());
		} catch (BusinessException e) {
			log.info("exception: addMiscApp4NewTPA ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: addMiscApp4NewTPA ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: addMiscApp4NewTPA ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: addMiscApp4NewTPA  DAO  END");
		}

		log.info("END: addMiscApp4NewTPA  DAO  END Result miscSeqNbr: " + CommonUtility.deNull(miscSeqNbr));
		return miscSeqNbr;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->addMiscApp4NewTPA()

	private void addVehicleDetails4NewTPA(VehicleDetailsVO vo) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: addVehicleDetails4NewTPA  DAO  Start vo:" + vo.toString());

			sb.append(" insert into misc_vehicle_det(misc_seq_nbr, item_nbr, ");
			sb.append(" veh_chas_nbr, cntr_nbr, ");
			sb.append(" asn_nbr, last_modify_user_id, last_modify_dttm, REMARKS, ");
			sb.append(" PREF_AREA_CD, CNTR_CRG_STATUS) values ");
			sb.append(" (:miscSeqNbr,:itemNbr,:vehChasNbr,:cntrNbr, ");
			sb.append(" :asnrNbr,:userId,sysdate, :remarks, :prefAreaCd, :status) ");

			log.info(" ***addVehicleDetails4NewTPA SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", vo.getMisc_seq_nbr());
			paramMap.put("itemNbr", vo.getItem_nbr());
			paramMap.put("vehChasNbr",
					(vo.getVeh_chas_nbr() != null && !"".equalsIgnoreCase(vo.getVeh_chas_nbr()))
							? CommonUtility.getStringTokens(vo.getVeh_chas_nbr()).toUpperCase()
							: vo.getVeh_chas_nbr());
			paramMap.put("cntrNbr", vo.getCntr_nbr());
			paramMap.put("asnrNbr", vo.getAsn_nbr());
			paramMap.put("userId", vo.getLast_modify_user_id());
			paramMap.put("remarks", vo.getRemarks());
			paramMap.put("prefAreaCd", vo.getPref_area_cd());
			paramMap.put("status", vo.getCntr_crg_status());
			log.info("status cd= " + vo.getCntr_crg_status());

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);
		} catch (NullPointerException e) {
			log.info("exception: addVehicleDetails4NewTPA ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: addVehicleDetails4NewTPA ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: addVehicleDetails4NewTPA  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->addMiscApp4NewTPA()
	// HaiTTH1 added on 21/1/2014 for auto-assign when approve new TPA

	private List<Object> autoGetAssignAreaList4NewTPA(VehicleVO vehVo, String vehicleItems, int hoursPerBlock,
			String preStatus) throws BusinessException {
		log.info("START: autoGetAssignAreaList4NewTPA DAO: vehVo: " + vehVo.toString() + ", vehicleItems: "
				+ CommonUtility.deNull(vehicleItems) + ", hoursPerBlock: " + hoursPerBlock);
		log.info("cargoType:" + vehVo.getCargo_type());
		List<Object> vector = new ArrayList<Object>();
		List<MiscAppParkingAreaObject> threeBlockSlotList = new ArrayList<MiscAppParkingAreaObject>();
		List<MiscAppParkingAreaObject> currentBlockSlotList = new ArrayList<MiscAppParkingAreaObject>();
		List<MiscAppParkingAreaObject> availableAreaList = new ArrayList<MiscAppParkingAreaObject>();
		String[] assignedArea = new String[5];
		String[] assignedSlot = new String[5];
		JSONArray vehItems = null;
		vehItems = new JSONArray(vehicleItems);
		int vehSize = vehItems.length();
		JSONObject o = null;

		Timestamp fromDate = getTimeStampFromString(vehVo.getFr_dttm(), "yyyy-MM-dd HH:mm");
		Timestamp oneBlockAgo = new Timestamp(fromDate.getTime() - (60 * 60 * 1000 * hoursPerBlock));
		String oneBlockAgoStr = formatTimeStamp(oneBlockAgo, "ddMMyyyy HHmm");

		Timestamp toDate = getTimeStampFromString(vehVo.getTo_dttm(), "yyyy-MM-dd HH:mm");
		Timestamp oneBlockNext = new Timestamp(toDate.getTime() + (60 * 60 * 1000 * hoursPerBlock));
		String oneBlockNextStr = formatTimeStamp(oneBlockNext, "ddMMyyyy HHmm");

		String str_fromDttm = formatTimeStamp(fromDate, "ddMMyyyy HHmm");
		String str_toDttm = formatTimeStamp(toDate, "ddMMyyyy HHmm");

		threeBlockSlotList = this.getAvailableParkingSlots4NewTpa(vehVo.getCargo_type(), oneBlockAgoStr,
				oneBlockNextStr, String.valueOf(vehVo.getTrailer_size()), vehVo.getTrailer_type());
		currentBlockSlotList = this.getAvailableParkingSlots4NewTpa(vehVo.getCargo_type(), str_fromDttm, str_toDttm,
				String.valueOf(vehVo.getTrailer_size()), vehVo.getTrailer_type());
		availableAreaList = this.getParkingAreaList4NewTpa(vehVo.getCargo_type(), str_fromDttm, str_toDttm,
				String.valueOf(vehVo.getTrailer_size()), vehVo.getTrailer_type());

		List<VehicleDetailsVO> vehicleList = getTpaVehicleDetailsByMiscSeqNumber(vehVo.getMisc_seq_nbr());
		if (StringUtils.equalsIgnoreCase(TpaConstants.TPA_STATUS_ACCEPTED, preStatus)) {// pre_status = 'A'
			for (VehicleDetailsVO vo : vehicleList) {
				MiscAppParkingAreaObject obj = new MiscAppParkingAreaObject();
				obj.setAreaCode(vo.getArea_cd());
				obj.setSlotNumber(vo.getSlot_nbr());
				threeBlockSlotList.add(obj);
				currentBlockSlotList.add(obj);
				boolean exist = false;
				for (int i = 0; i < availableAreaList.size(); i++) {
					MiscAppParkingAreaObject misc = (MiscAppParkingAreaObject) availableAreaList.get(i);
					if (StringUtils.equalsIgnoreCase(misc.getAreaCode(), vo.getArea_cd())) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					availableAreaList.add(obj);
				}
			}
		}

		// ===================================Start
		// processing=============================================
		if (currentBlockSlotList != null && currentBlockSlotList.size() < vehSize) {
			vector.add("Unable to submit as slot is not available.");
			log.info("Return empty");
		} else if (currentBlockSlotList != null && currentBlockSlotList.size() >= vehSize) {
			for (int i = 0; i < vehSize; i++) {
				o = vehItems.getJSONObject(i);
				if (o.getString("veh_chas_nbr") != null && !"".equalsIgnoreCase(o.getString("veh_chas_nbr"))) {
					if (o.getString("area_cd") != null && !"".equalsIgnoreCase(o.getString("area_cd"))) {
						List<Object> resultList = this.assignSlot(o.getString("area_cd"), threeBlockSlotList,
								currentBlockSlotList);
						if (resultList.size() == 5) {
							// To assign parking slot for each vehicle
							assignedArea[i] = (String) resultList.get(1);
							assignedSlot[i] = (String) resultList.get(2);

							Object listObject = null;
							listObject = resultList.get(3);
							threeBlockSlotList = new ArrayList<MiscAppParkingAreaObject>();
							if (listObject instanceof List) {
								for (int j = 0; j < ((List<?>) listObject).size(); j++) {
									Object item = ((List<?>) listObject).get(j);
									if (item instanceof Object) {
										threeBlockSlotList.add((MiscAppParkingAreaObject) item);
									}
								}
							}

							listObject = null;
							listObject = resultList.get(4);
							currentBlockSlotList = new ArrayList<MiscAppParkingAreaObject>();
							if (listObject instanceof List) {
								for (int j = 0; j < ((List<?>) listObject).size(); j++) {
									Object item = ((List<?>) listObject).get(j);
									if (item instanceof Object) {
										currentBlockSlotList.add((MiscAppParkingAreaObject) item);
									}
								}
							}

						} else {
							for (int j = 0; j <= availableAreaList.size(); j++) {
								MiscAppParkingAreaObject areaObj = (MiscAppParkingAreaObject) availableAreaList.get(j);
								if (o.getString("area_cd").equalsIgnoreCase(areaObj.getAreaCode())) {
									availableAreaList.remove(j);
								} else {
									List<Object> result = new ArrayList<Object>();
									result = this.assignSlot(areaObj.getAreaCode(), threeBlockSlotList,
											currentBlockSlotList);
									if (result.size() == 5) {
										// To assign parking slot for each vehicle
										assignedArea[i] = (String) result.get(1);
										assignedSlot[i] = (String) result.get(2);

										Object listObject = null;
										listObject = null;
										listObject = result.get(3);
										threeBlockSlotList = new ArrayList<MiscAppParkingAreaObject>();
										if (listObject instanceof List) {
											for (int k = 0; k < ((List<?>) listObject).size(); k++) {
												Object item = ((List<?>) listObject).get(k);
												if (item instanceof Object) {
													threeBlockSlotList.add((MiscAppParkingAreaObject) item);
												}
											}
										}

										listObject = null;
										listObject = result.get(4);
										currentBlockSlotList = new ArrayList<MiscAppParkingAreaObject>();
										if (listObject instanceof List) {
											for (int k = 0; k < ((List<?>) listObject).size(); k++) {
												Object item = ((List<?>) listObject).get(k);
												if (item instanceof Object) {
													currentBlockSlotList.add((MiscAppParkingAreaObject) item);
												}
											}
										}
										break;
									}
								}
							}
						}
					} else {
						log.info("Area is empty");
					}

				} else {
					log.info("Vehicle no is empty");
				}
			}
			vector.add(assignedArea);
			vector.add(assignedSlot);
		}

		log.info("END: autoGetAssignAreaList4NewTPA Result: vector: " + vector.toString());

		return vector;
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateMiscApp4NewTpa()

	private void updateMiscApp4NewTpa(MiscAppValueObject vo) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String miscRefNbr = null;
		try {
			log.info("START: updateMiscApp4NewTpa  DAO  Start vo:" + vo.toString());
			if (TpaConstants.TPA_STATUS_DRAFT.equalsIgnoreCase(vo.getAppStatusCd())) {
				sb.append(" update /* MiscAppEJB - updateMiscAppDetails(sql) */ ");
				sb.append(" misc_app set ref_nbr = :miscRefNbr,  ");
				sb.append(" app_status = :status, submit_dttm = sysdate, submit_user_id = :submitBy,  ");
				sb.append(" last_modify_user_id = :submitBy, last_modify_dttm = sysdate,  ");
				sb.append(
						" contact_person = :conPerson, contact_tel = :conTel, contact_email = :conEmail, cust_cd = :coCd, ");
				sb.append(" acct_nbr = :acctNbr, vv_cd = :vvCd  ");
				sb.append(" where misc_seq_nbr = :appSeqNbr ");
			} else {
				sb.append(" update /* MiscAppEJB - updateMiscAppDetails(sql1) */ ");
				sb.append(" umisc_app set app_status =:status, ");
				sb.append(" ulast_modify_user_id = :submitBy, last_modify_dttm = sysdate, ");
				sb.append(" ucontact_person = :submitBy, contact_tel = :conTel,contact_email = :conEmail, ");
				sb.append(" ucust_cd = :coCd, acct_nbr = :acctNbr, vv_cd = :vvCd ");
				sb.append(" uwhere misc_seq_nbr = :appSeqNbr ");
			}

			if (TpaConstants.TPA_STATUS_DRAFT.equalsIgnoreCase(vo.getAppStatusCd())) {

				log.info(" ***updateMiscApp4NewTpa SQL *****" + sb.toString());
				miscRefNbr = vo.getAppTypeCd() + getNextMiscRefNumber();

				if (vo.getAcctNbr() != null && !"".equalsIgnoreCase(vo.getAcctNbr())) {
					paramMap.put("miscRefNbr", miscRefNbr);
				} else {
					paramMap.put("miscRefNbr", miscRefNbr + "C");
				}
				paramMap.put("status", TpaConstants.TPA_STATUS_SUBMITTED);
				paramMap.put("submitBy", vo.getSubmitBy());
				paramMap.put("conPerson", vo.getConPerson());
				paramMap.put("conTel", vo.getConTel());
				paramMap.put("conEmail", vo.getConEmail());
				paramMap.put("coCd", vo.getCoCd());
				paramMap.put("acctNbr", vo.getAcctNbr());
				paramMap.put("vvCd", vo.getVarCode());
				paramMap.put("appSeqNbr", vo.getAppSeqNbr());
			} else {

				log.info(" ***updateMiscApp4NewTpa SQL *****" + sb.toString());

				paramMap.put("status", TpaConstants.TPA_STATUS_SUBMITTED);
				paramMap.put("submitBy", vo.getSubmitBy());
				paramMap.put("conPerson", vo.getConPerson());
				paramMap.put("conTel", vo.getConTel());
				paramMap.put("conEmail", vo.getConEmail());
				paramMap.put("coCd", vo.getCoCd());
				paramMap.put("acctNbr", vo.getAcctNbr());
				paramMap.put("vvCd", vo.getVarCode());
				paramMap.put("appSeqNbr", vo.getAppSeqNbr());

			}

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

		} catch (BusinessException e) {
			log.info("exception: updateMiscApp4NewTpa ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.info("exception: updateMiscApp4NewTpa ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateMiscApp4NewTpa ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateMiscApp4NewTpa  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateMiscApp4NewTpa()

	private void updateVehicle4NewTPA(VehicleVO vo) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: updateVehicle4NewTPA  DAO  Start vo:" + vo.toString());

			sb.append(" update /* MiscAppEJB - updateTrailerParkingApplicationDetails(vehSql) */ ");
			sb.append(" misc_vehicle set fr_dttm = to_date(:fromDate,'ddMMyyyy HH24MI'), ");
			sb.append(" to_dttm = to_date(:toDate,'ddMMyyyy HH24MI'), nbr_night = :nbrNight, ");
			sb.append(" park_reason= :parkReason , ");
			sb.append(" last_modify_user_id = :userId, last_modify_dttm = sysdate, ");
			sb.append(" NBR_HOUR = :noHours, PARK_REASON_CD = :reasonForApplication, CARGO_TYPE = :cargoType");
			sb.append(" where misc_seq_nbr = :miscSeqNbr ");

			log.info(" ***updateVehicle4NewTPA SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", vo.getMisc_seq_nbr());
			paramMap.put("ftDttm", vo.getFr_dttm());
			paramMap.put("toDttm", vo.getTo_dttm());
			// Default = 0
			paramMap.put("nbrNight", "0");
			paramMap.put("parkReason", vo.getPark_reason());
			paramMap.put("userId", vo.getLast_modify_user_id());
			paramMap.put("noHrs", vo.getNo_of_hours());
			paramMap.put("parkReasonCd", vo.getPark_reason_cd());
			paramMap.put("cargoType", vo.getCargo_type());
			paramMap.put("trailerSize", vo.getTrailer_size());
			paramMap.put("trailerType", vo.getTrailer_type());
			paramMap.put("miscSeqNbr", vo.getMisc_seq_nbr());

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

		} catch (NullPointerException e) {
			log.info("exception: updateVehicle4NewTPA ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateVehicle4NewTPA ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateVehicle4NewTPA  DAO  END");
		}
	}

	// jp.src.ejb.sessionBeans.gbms.miscApp-->MiscAppEJB-->updateMiscApp4NewTpa()

	private void updateVehicleDetails4NewTPA(VehicleDetailsVO vo) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: updateVehicleDetails4NewTPA  DAO  Start vo:" + vo.toString());

			sb.append(" insert into misc_vehicle_det(misc_seq_nbr, item_nbr, veh_chas_nbr, ");
			sb.append(" cntr_nbr, asn_nbr, last_modify_user_id, last_modify_dttm, REMARKS, ");
			sb.append(" PREF_AREA_CD, CNTR_CRG_STATUS, SLOT_NBR, AREA_CD) values ");
			sb.append(" (:miscSeqNbr,:itemNbr,:vehChasNbr,:cntrNbr, ");
			sb.append(" :asnrNbr,:userId,sysdate, :remarks, :prefAreaCd, ");
			sb.append(" :status, :slotNbr, :areaCd) ");

			log.info(" ***updateVehicleDetails4NewTPA SQL *****" + sb.toString());

			paramMap.put("miscSeqNbr", vo.getMisc_seq_nbr());
			paramMap.put("itemNbr", vo.getItem_nbr());
			paramMap.put("vehChasNbr",
					(vo.getVeh_chas_nbr() != null && !"".equalsIgnoreCase(vo.getVeh_chas_nbr()))
							? CommonUtility.getStringTokens(vo.getVeh_chas_nbr()).toUpperCase()
							: vo.getVeh_chas_nbr());
			paramMap.put("cntrNbr", vo.getCntr_nbr());
			paramMap.put("asnrNbr", vo.getAsn_nbr());
			paramMap.put("userId", vo.getLast_modify_user_id());
			paramMap.put("remarks", vo.getRemarks());
			paramMap.put("prefAreaCd", vo.getPref_area_cd());
			paramMap.put("status", vo.getCntr_crg_status());
			paramMap.put("slotNbr", vo.getSlot_nbr());
			paramMap.put("areaCd", vo.getArea_cd());

			log.info(" *** paramMap: *****" + paramMap.toString());

			namedParameterJdbcTemplate.update(sb.toString(), paramMap);

		} catch (NullPointerException e) {
			log.info("exception: updateVehicleDetails4NewTPA ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: updateVehicleDetails4NewTPA ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateVehicleDetails4NewTPA  DAO  END");
		}
	}

	// package: ejb.sessionBeans.messenger-->MessengerEJB-->sendMessage()
	/*
	 * public boolean sendMessage(IMessageValueObject mVO) { // Is Email if (mVO
	 * instanceof EmailValueObject) { EmailValueObject emailVO = (EmailValueObject)
	 * mVO; // need to fix // sendEmail(emailVO); } else if (mVO instanceof
	 * SMSValueObject) { SMSValueObject smsVO = (SMSValueObject) mVO; // need to fix
	 * // sendSMS(smsVO); } return true; }
	 */

	// jp.src.sg.com.jp.ntpa.utils-->TpaUtils-->getPaginationString()
	public static String getPaginationString(String sql, Integer start, Integer limit) {
		log.info("START: getPaginationString: sql: " + CommonUtility.deNull(sql) + ", start: " + start + ", limit: "
				+ limit);
		sql = sql.trim();
		int end = start.intValue() + limit.intValue();
		StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
		pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
		pagingSelect.append(sql);

		pagingSelect.append(") row_   " + " where rownum <= ");
		pagingSelect.append(String.valueOf(end));
		pagingSelect.append(") where rownum_ > ");
		pagingSelect.append(start.toString());

		log.info("END: getPaginationString Result: " + CommonUtility.deNull(pagingSelect.toString()));

		return pagingSelect.toString();
	}

	// jp.src.sg.com.jp.ntpa.utils-->TpaUtils-->getTimeStampFromString()
	public static Timestamp getTimeStampFromString(String datestr, String format) {
		Timestamp retval = null;

		try {

			log.info("START: getTimeStampFromString: datestr: " + CommonUtility.deNull(datestr) + ", format: "
					+ CommonUtility.deNull(format));

			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date dttm = (Date) sdf.parse(datestr);
			retval = new Timestamp(dttm.getTime());
		} catch (Exception e) {
			log.info("Exception getTimeStampFromString: ", e);
			retval = new Timestamp(Calendar.getInstance().getTimeInMillis());
		}

		log.info("END: getTimeStampFromString Result: " + CommonUtility.deNull(retval.toString()));

		return retval;
	}

	// jp.src.sg.com.jp.ntpa.utils-->TpaUtils-->formatTimeStamp()
	public static String formatTimeStamp(Timestamp value, String format) {
		String ret = "";
		log.info("START: formatTimeStamp value: " + CommonUtility.deNull(value.toString()) + ", format: "
				+ CommonUtility.deNull(format));
		if (value != null) {
			try {
				DateFormat df1 = new SimpleDateFormat(format);
				ret = df1.format(value);
			} catch (Exception e) {
				log.info("Exception formatTimeStamp : ", e);
			}
		}

		log.info("END: formatTimeStamp Result: " + CommonUtility.deNull(ret));

		return ret;
	}

	// jp.src.sg.com.jp.ntpa.utils-->TpaUtils-->getCurrentTimeStamp()
	public static Timestamp getCurrentTimeStamp() {
		try {
			log.info("START: getCurrentTimeStamp");
			log.info("END: getCurrentTimeStamp: " + new Timestamp(Calendar.getInstance().getTimeInMillis()));
			return new Timestamp(Calendar.getInstance().getTimeInMillis());
		} catch (Exception e) {
			log.info("Exception getCurrentTimeStamp: ", e);
			return new Timestamp(Calendar.getInstance().getTimeInMillis());
		}

	}

	// misc app top
	// added by Nasir on 30/04/2021
	@Override
	public String getCompanyName(String coCd) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String companyName = "";
		try {
			log.info("START: getCompanyName coCd:" + CommonUtility.deNull(coCd));
			sb.append(" SELECT CO_NM AS coNm FROM tops.COMPANY_CODE WHERE CO_CD LIKE :coCd ");
			paramMap.put("coCd", coCd);
			companyName = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, String.class);
			log.info("SQL" + sb.toString());
		} catch (NullPointerException e) {
			log.info("exception: getCompanyName ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getCompanyName ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getCompanyName Result: companyName: " + CommonUtility.deNull(companyName));
		}
		return companyName;
	}
	
	@Override
	public Map<String,String> getUserInfo(String userAccount) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> userInfo = new HashMap<String, String>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		try {
			log.info("START: getUserInfo userAccount:" + CommonUtility.deNull(userAccount));
			sb.append(" SELECT USER_NAME, COMPANY_ID, USER_ACCT FROM ADM_USER WHERE USER_ACCT = :userAcct ");
			paramMap.put("userAcct", userAccount);
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			// return description
			if (rs.next()) {
				userInfo.put("userName", rs.getString("USER_NAME"));
				userInfo.put("companyId", rs.getString("COMPANY_ID"));
				userInfo.put("userAccount", rs.getString("USER_ACCT"));
			} else {
				userInfo.put("userName", "");
				userInfo.put("companyId", "");
				userInfo.put("userAccount", "");
			}
			log.info("SQL" + sb.toString());
		} catch (NullPointerException e) {
			log.info("exception: getUserInfo ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getUserInfo ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO getUserInfo Result: getUserInfo: " + userInfo);
		}
		return userInfo;
	}

	// end

	// misc app void
	// ejb.sessionBeans.cim.ExceptionAlert -->ExceptionAlertEJB -->sendFlexiAlert
	@Override
	public void sendFlexiAlert(String alertCode, String smsMessage, String emailMessage, String emailSubject,
			String emailSender, String emailAttachmentFilename) throws BusinessException {

		ExceptionAlertValueObject alertVO;

		try {
			log.info("START: sendFlexiAlert - alertCode: " + CommonUtility.deNull(alertCode) + ", smsMessage: "
					+ CommonUtility.deNull(smsMessage) + ", emailMessage: " + CommonUtility.deNull(emailMessage)
					+ ", emailSubject: " + CommonUtility.deNull(emailSubject) + ", emailSender: "
					+ CommonUtility.deNull(emailSender) + CommonUtility.deNull(emailAttachmentFilename));
			List<ExceptionAlertValueObject> vector = getExceptionAlertInfo(alertCode);
			if (vector == null || vector.size() < 1)
				return; // nothing to send

			List<String> emailArr = new ArrayList<String>();
			List<String> smsArr = new ArrayList<String>();

			// process all recipients into email or sms recipients
			for (int i = 0; i < vector.size(); i++) {
				alertVO = (ExceptionAlertValueObject) vector.get(i);

				if (alertVO.getDeliveryMode().equals(ExceptionAlertValueObject.DELIVERY_MODE_EML)) {
					// by email
					emailArr.add(alertVO.getAccount());
				} else if (alertVO.getDeliveryMode().equals(ExceptionAlertValueObject.DELIVERY_MODE_SMS)) {
					// by sms or pager
					smsArr.add(alertVO.getAccount());
				}
			}

			// send email if any
			if (emailArr.size() > 0) {
				EmailValueObject emailVO = new EmailValueObject();
				emailVO.setMessage(emailMessage);
				if ("".equals(emailSender) || emailSender.equals(null)) {
					String sender = alert_Sender;
					emailVO.setSenderAddress(sender);
				} else {
					emailVO.setSenderAddress(emailSender);
				}
				if ("".equals(emailSubject) || emailSubject.equals(null))
					emailVO.setSubject(getAlertDesc(alertCode) + " alert");
				else
					emailVO.setSubject(emailSubject);
				if (emailAttachmentFilename != null && !"".equals(emailAttachmentFilename)) {
					String fileName = "";
					String reportroot = Constants.ExceptionAlert_ReportRoot;
					fileName = reportroot + emailAttachmentFilename;

					File ne = new File(fileName);
					if (!ne.exists()) {
						log.info(fileName + " does not exist in the system");
					}
					emailVO.addAttachment(ne.getName(), ne.getParent());
				}
				emailVO.setRecipientAddress((String[]) emailArr.toArray(new String[0]));

				sendMessage(emailVO);
			}

			if (smsArr.size() > 0) {
				Sms smsVO = new Sms();
				smsVO.setMessage(smsMessage);
				smsVO.setToList(smsArr);
				sendMessage(smsVO);
			}

		} catch (BusinessException e) {
			log.info("exception: sendFlexiAlert ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception exp) {
			log.info("exception: sendFlexiAlert ", exp);
		}

		log.info("END: sendFlexiAlert");
	}

	// package: ejb.sessionBeans.messenger-->MessengerEJB
	// method: sendMessage()
	public boolean sendMessage(IMessageValueObject mVO) throws BusinessException {
		// Is Email
		try {
			log.info("START: sendMessage mVO: " + mVO);
			if (mVO instanceof EmailValueObject) {
				EmailValueObject emailVO = (EmailValueObject) mVO;
				Email emailObj = new Email();
				emailObj.setFrom(emailVO.getSenderAddress());
				emailObj.setFromName(emailVO.getSenderAddress());
				emailObj.setToList(Arrays.asList(emailVO.getRecipientAddress()));
				emailObj.setSubject(emailVO.getSubject());
				emailObj.setContent(emailVO.getMessage());
				emailObj.setEmailSvcUrl(commonServiceUrl);
				emailObj.setContentType("text/html");
				log.info("***emailObj*******" + emailObj.toString());
				CommonUtil.sendEmail(emailObj, "");
			} else if (mVO instanceof Sms) {
				Sms smsVO = (Sms) mVO;
				CommonUtil.sendSMS(smsVO);
			}
		} catch (NullPointerException e) {
			log.info("Exception: sendMessage ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception: sendMessage ", e);
			throw new BusinessException("M4201");
		}

		log.info("END: sendMessage Result: true");

		return true;
	}

	private List<ExceptionAlertValueObject> getExceptionAlertInfo(String alertCode) throws BusinessException {
		return getExceptionAlertInfo(alertCode, null);
	}

	// ejb.sessionBeans.cim.ExceptionAlert -->ExceptionAlertEJB -->getAlertDesc
	private String getAlertDesc(String alertCode) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();

		if (alertCode == null || alertCode.equals("")) {
			return "";
		}

		try {
			log.info("START: getAlertDesc  DAO  Start Obj alertCode: " + CommonUtility.deNull(alertCode));

			String sql = "SELECT MISC_TYPE_NM FROM MISC_TYPE_CODE WHERE CAT_CD='ALERT_CODE' AND MISC_TYPE_CD=:alertCode";
			paramMap.put("alertCode", alertCode);
			log.info(" *** getAlertDesc SQL *****" + sql);
			log.info(" *** paramMap: *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			// return description
			if (rs.next()) {
				return rs.getString("MISC_TYPE_NM");
			}

		} catch (NullPointerException e) {
			log.info("exception: getAlertDesc ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getAlertDesc ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getAlertDesc Result *****" + alertCode.toString());
		}

		return alertCode;
	}

	// ejb.sessionBeans.cim.ExceptionAlert -->ExceptionAlertEJB
	// -->getExceptionAlertInfo
	private List<ExceptionAlertValueObject> getExceptionAlertInfo(String alertCode, String deliveryMode)
			throws BusinessException {
		StringBuffer queryString = new StringBuffer();
		List<ExceptionAlertValueObject> vector = new ArrayList<ExceptionAlertValueObject>();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getExceptionAlertInfo  DAO  Start Obj " + " alertCode:" + CommonUtility.deNull(alertCode)
					+ " deliveryMode:" + CommonUtility.deNull(deliveryMode));

			if (alertCode == null || alertCode.equals("")) {
				// no alert code given!
				log.info("getExceptionAlertInfo called with invalid alertCode");
				return vector;
			}

			queryString.append("select name, account, delivery_mode from exception_alert where alert_code=:alertCode");
			queryString.append(" and rec_status='A'");
			if (deliveryMode != null && !deliveryMode.equals("")) {
				queryString.append(" and delivery_mode =:deliveryMode");
			}

			String sql = queryString.toString();
			paramMap.put("alertCode", alertCode);
			if (deliveryMode != null && !deliveryMode.equals(""))
				paramMap.put("deliveryMode", deliveryMode);
			log.info(" *** getExceptionAlertInfo SQL *****" + sql);
			log.info(" *** paramMap: *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				ExceptionAlertValueObject exceptionAlertValueObj = new ExceptionAlertValueObject();

				exceptionAlertValueObj.setName(rs.getString("name"));
				exceptionAlertValueObj.setAccount(rs.getString("account"));
				exceptionAlertValueObj.setDeliveryMode(rs.getString("delivery_mode"));
				exceptionAlertValueObj.setAlertCode(alertCode);

				vector.add(exceptionAlertValueObj);
			}
			log.info("END: *** getExceptionAlertInfo Result *****" + vector.toString());
			return vector;

		} catch (NullPointerException e) {
			log.info("exception: getExceptionAlertInfo ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getExceptionAlertInfo ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getExceptionAlertInfo  DAO  END vector: " + CommonUtility.deNull(vector.toString()));
		}
		// return null;
	}
	// end

	@Override
	public List<String> getDelFile(String fileNames, String miscSeqNbr, String Type, String catCd) throws BusinessException {
		List<String> deletedFile = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getDelFile  DAO  Start Obj " + " fileNames:" + fileNames.toString() + ", miscSeqNbr : " + miscSeqNbr +", Type : " + Type);
			sb.append(" select assign_file_nm ");
			sb.append(" from misc_upload_doc, misc_type_code a where assign_file_nm in (:fileNames) ");
			sb.append(" AND misc_seq_nbr =:miscSeqNbr AND a.cat_cd = :catCd ");
			sb.append(" AND  misc_upload_doc.doc_type = a.misc_type_cd AND a.rec_status = 'A'");
			
			paramMap.put("fileNames", fileNames);
			paramMap.put("miscSeqNbr", miscSeqNbr);
			paramMap.put("catCd", catCd);
			
			log.info(" *** getDelFile SQL *****" + sb.toString());
			log.info(" *** paramMap: *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			String fullPath = UploadDocument.getOutputFileDir(Type, "upload");
			
			while(rs.next()) {
				deletedFile.add(fullPath + CommonUtility.deNull(rs.getString("assign_file_nm")));
			}
			
			
		} catch (NullPointerException e) {
			log.info("exception: getDelFile ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getDelFile ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getDelFile  DAO  END deletedFile: " + CommonUtility.deNull(deletedFile.toString()));
		}
		return deletedFile;

	}
	

}
