package sg.com.jp.generalcargo.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.CustomDetailsRepository;
import sg.com.jp.generalcargo.dao.InwardCargoManifestRepository;
import sg.com.jp.generalcargo.domain.BookingReferenceFileUploadDetails;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.CustomDetails;
import sg.com.jp.generalcargo.domain.CustomDetailsActionTrail;
import sg.com.jp.generalcargo.domain.CustomDetailsActionTrailDetails;
import sg.com.jp.generalcargo.domain.CustomDetailsFileUploadDetails;
import sg.com.jp.generalcargo.domain.CustomDetailsUploadActionTrail;
import sg.com.jp.generalcargo.domain.CustomDetailsUploadConfig;
import sg.com.jp.generalcargo.domain.PageDetails;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.Template;
import sg.com.jp.generalcargo.domain.TextParaVO;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;

@Repository("CustomDetailsRepository")
public class CustomDetailsJdbcRepository implements CustomDetailsRepository {

	private static final Log log = LogFactory.getLog(CustomDetailsJdbcRepository.class);

	@Autowired
	private InwardCargoManifestRepository inwardCargoManifestRepository;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	final static String TEXT_PARA_GC_VIEW_MANIFEST = "GC_V_MFST";

	@Override
	public List<VesselVoyValueObject> getlistVessel(String coCd, String search) throws BusinessException {
		SqlRowSet rs = null;
		List<VesselVoyValueObject> voyList = new ArrayList<VesselVoyValueObject>();
		boolean isShowManifestInfo = false;
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getlistVessel DAO START coCd:" + coCd + ", searchKey :" + search);
			TextParaVO code = new TextParaVO();
			code.setParaCode(TEXT_PARA_GC_VIEW_MANIFEST);
			TextParaVO result = inwardCargoManifestRepository.getParaCodeInfo(code);
			isShowManifestInfo = inwardCargoManifestRepository.isShowManifestInfo(coCd, result);
			log.info("isShowManifestInfo :" + isShowManifestInfo);
			if (isShowManifestInfo) {
				sb.append(
						"SELECT IN_VOY_NBR,OUT_VOY_NBR,VSL_NM,VV_CD,TERMINAL FROM TOPS.VESSEL_CALL WHERE VV_STATUS_IND IN ('PR','AP','AL','BR','UB')");
				sb.append(" AND nvl(GB_CLOSE_BJ_IND,'N') <> 'Y' ");
				sb.append(" AND (IN_VOY_NBR LIKE :searchVoy OR VSL_NM LIKE :searchVsl ) ");
				sb.append(" ORDER BY TERMINAL DESC,VSL_NM,IN_VOY_NBR");
			} else {
				sb.append("SELECT DISTINCT IN_VOY_NBR, ");
				sb.append("	VSL_NM,  ");
				sb.append("	VC.VV_CD, TERMINAL, OUT_VOY_NBR  ");
				sb.append("	FROM TOPS.VESSEL_CALL VC   ");
				sb.append("	LEFT OUTER JOIN GBMS.VESSEL_DECLARANT VD   ");
				sb.append("	ON (VD.VV_CD                     = VC.VV_CD   ");
				sb.append("	AND VD.STATUS                    = 'A')   ");
				sb.append("	WHERE VV_STATUS_IND             IN ('PR','AP','AL','BR','UB')   ");
				sb.append("	AND NVL(VC.GB_CLOSE_BJ_IND,'N') <> 'Y' ");
				sb.append("	AND (VD.CUST_CD                  =:coCode   ");
				sb.append("	OR VC.CREATE_CUST_CD             =:coCode  )   ");
				sb.append(" AND (IN_VOY_NBR LIKE :searchVoy OR VSL_NM LIKE :searchVsl ) ");
				sb.append("	ORDER BY TERMINAL DESC, VSL_NM, ");
				sb.append("	IN_VOY_NBR");
			}

			if (!isShowManifestInfo) {
				paramMap.put("coCode", coCd);
			}

			String searchVoy = "";
			String searchVsl = "";
			if (search.contains("-")) {
				searchVoy = search.split("-")[1];
				searchVsl = search.split("-")[0];
			} else {
				searchVoy = search;
				searchVsl = search;
			}
			paramMap.put("searchVoy", "%" + (searchVoy.toUpperCase()) + "%");
			paramMap.put("searchVsl", "%" + (searchVsl.toUpperCase()) + "%");
			log.info("***** getVesselVoy SQL *****" + sb.toString());
			log.info("params: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			String voynbr = "";
			String inVoyNo = "";
			String outVoyNo = "";
			String vslName = "";
			String VV_CD = "";
			String terminal = "";
			while (rs.next()) {
				voynbr = CommonUtility.deNull(rs.getString("IN_VOY_NBR"))+"/"+CommonUtility.deNull(rs.getString("OUT_VOY_NBR"));
				vslName = CommonUtility.deNull(rs.getString("VSL_NM"));
				VV_CD = CommonUtility.deNull(rs.getString("VV_CD"));
				terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
				inVoyNo = CommonUtility.deNull(rs.getString("IN_VOY_NBR"));
				outVoyNo = CommonUtility.deNull(rs.getString("OUT_VOY_NBR")); // Added

				VesselVoyValueObject vvvObj = new VesselVoyValueObject();
				vvvObj.setVoyNo(voynbr);
				vvvObj.setVslName(vslName);
				vvvObj.setVarNbr(VV_CD);
				vvvObj.setTerminal(terminal);
				vvvObj.setOutVoyNo(outVoyNo);
				vvvObj.setInVoyNo(inVoyNo);
				vvvObj.setVslVoy(vslName + "-" + voynbr);
				voyList.add(vvvObj);
			}
			log.info("voyList: getVesselVoy" + voyList.toString());
		} catch (Exception e) {
			log.info("Exception getVesselVoy : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getVesselVoy Result ***** " + voyList.toString());
		}
		return voyList;
	}

	@Override
	public TableResult getCustomDetailsActionTrail(Criteria criteria) throws BusinessException {
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		StringBuffer sb = new StringBuffer();
		String sql = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {

			String vvCd = (CommonUtil.deNull(criteria.getPredicates().get("varNbr"))).trim();

			log.info("START getCustomDetailsActionTrail:" + "vvCd:" + vvCd);

			sb.append(" SELECT trl.CUSTOM_DETAILS_ACT_TRL_ID, trl.VV_CD, trl.TYPE_CD, ");
			sb.append(
					" CASE WHEN ac.user_nm IS NOT NULL THEN  CONCAT (CONCAT (ac.user_nm, ' - '), ac.CUST_CD) ELSE '' END last_modify_user_id , ");
			sb.append(" TO_CHAR(trl.LAST_MODIFY_DTTM,'DD-MM-YYYY HH24:MI') LAST_MODIFY_DTTM, trl.REMARKS, ");
			sb.append(" CASE WHEN trl.TYPE_CD='E' Then 'EXCEL' else 'CUSCAR' END TYPE ");
			sb.append(" FROM GBMS.CUSTOM_DETAILS_ACT_TRL trl  ");
			sb.append(
					" LEFT JOIN TOPS.logon_acct ac ON ac.login_id= SUBSTR(trl.LAST_MODIFY_USER_ID, INSTR( trl.LAST_MODIFY_USER_ID, '/', -1 ) + 1 )   ");
			sb.append(" JOIN GBMS.CUSTOM_UPLOAD_DETAILS cud ON  cud.LAST_MODIFY_DTTM = trl.LAST_MODIFY_DTTM ");
			sb.append(" WHERE trl.VV_CD = :vvCd  ");
			sb.append(" ORDER BY trl.LAST_MODIFY_DTTM DESC");

			paramMap.put("vvCd", vvCd);
			sql = sb.toString();

			if (criteria.isPaginated()) {
				tableData.setTotal((int) namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());

				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());
			}

			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {

				CustomDetailsUploadActionTrail customActionTrail = new CustomDetailsUploadActionTrail();
				customActionTrail.setcustomDetails_act_trl_id(Long.valueOf(rs.getString("CUSTOM_DETAILS_ACT_TRL_ID")));
				customActionTrail.setVv_cd(vvCd);
				customActionTrail.setTypeCd(rs.getString("TYPE_CD"));
				customActionTrail.setType(rs.getString("TYPE"));
				String remarks = rs.getString("REMARKS");
				if (customActionTrail.getTypeCd().equalsIgnoreCase("C")) {
					remarks = formatRecord(remarks);
					customActionTrail.setRemarks(remarks);
				} else {
					customActionTrail.setRemarks(remarks);
				}
				customActionTrail.setLast_modify_user_id(rs.getString("last_modify_user_id"));
				customActionTrail.setLast_modify_dttm(rs.getString("LAST_MODIFY_DTTM"));
				topsModel.put(customActionTrail);
			}

			log.info(" getCustomDetailsActionTrail Dao  Result" + topsModel.getSize());

			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);

		} catch (Exception e) {
			log.info("Exception getCustomDetailsActionTrail : ", e);
			tableResult.setSuccess(false);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getCustomDetailsActionTrail");
		}
		return tableResult;
	}
	
	@Override
	public PageDetails getVesselCallDetails(String vvCd) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		PageDetails vesselCallDetails = null; 
		try {
			log.info("START getVesselCallDetails" + "Params:" + vvCd);
			sb.append(
					"SELECT VSL_NM AS vesselName ,VV_CD AS vvCd,IN_VOY_NBR AS inwardVoyNo, OUT_VOY_NBR as outVoyNo, IN_VOY_NBR ||'/'||OUT_VOY_NBR as voyageNo  FROM tops.VESSEL_CALL WHERE VV_CD=:vvCd");
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("vvCd", vvCd);
			log.info("getVesselCallDetails :" + "SQL:" + sb.toString() + "Param:" + paramMap.toString());
			vesselCallDetails = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap,
					new BeanPropertyRowMapper<PageDetails>(PageDetails.class));
			log.info("getVesselCallDetails : " + vesselCallDetails.toString());
		} catch (Exception e) {
			log.info("Exception getVesselCallDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info(" END getVesselCallDetails ");
		}
		return vesselCallDetails;
	}

	@Override
	public PageDetails customDetailsUploadDetail(String vvCd) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		PageDetails pageDetails = new PageDetails();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START getPageDetails : VVCd:" + vvCd);

			// set vessel details into response
			pageDetails = this.getVesselCallDetails(vvCd);
			
			sb.append("SELECT  REMARKS FROM tops.SYSTEM_CONFIG sc WHERE CAT_CD ='CUSTOM_UPLOAD_INST' AND REC_STATUS ='A'");
			log.info("getPageDetails   : SQL:" + sb.toString() + "Params:" + vvCd);
			List<String> instructions = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap,
					(String.class));
			log.info("getPageDetails size:" + instructions.size());
			pageDetails.setInstructions(instructions);
			pageDetails.setVvCd(vvCd);
			Template template = new Template();
			template.setFileName(ConstantUtil.customDetails + ConstantUtil.file_ext);
			template.setRefId(vvCd);
			template.setRefType(ConstantUtil.typeCd_CustomDetailsExcel);
			template.setIsSplitBL(false);
			List<Template> templateDet = new ArrayList<Template>();
			templateDet.add(template);

			pageDetails.setTemplate(templateDet);
			
		} catch (Exception e) {
			log.info("Exception getPageDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getPageDetails");
		}
		return pageDetails;
	}

	@Override
	public String getTemplateVersionNo() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		String versionNo = null;
		log.info("START: getTemplateVersionNo  DAO  :");
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			sb.append(
					"SELECT MISC_TYPE_NM FROM TOPS.SYSTEM_CONFIG sc WHERE CAT_CD='CUSTOM_TEMPLATE_VERSION' AND MISC_TYPE_CD='CUSTOM_DETAILS_TEMPLATE' AND REC_STATUS='A'");
			versionNo = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, String.class);
			log.info("getTemplateVersionNo:" + versionNo);
		} catch (Exception e) {
			log.info("Exception getTemplateVersionNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTemplateVersionNo ");
		}
		return versionNo;
	}

	@Override
	public List<CustomDetails> getCustomDetails(String vvCd) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<CustomDetails> customDetailsData = new ArrayList<>();
		SqlRowSet rs = null;
		try {
			log.info("START getCustomDetails: Vvcd: " + vvCd);
			List<String> consigneeDropdownList = inwardCargoManifestRepository.getConsigneee();

			sb.append("	SELECT '");
			sb.append(ConstantUtil.action_NA + "' as action, ");
			sb.append(
					"(SELECT PKG_TYPE_CD||'-'||PKG_DESC FROM GBMS.PKG_TYPES pt WHERE pt.PKG_TYPE_CD =  mc.PACKAGE_TYPE) AS PACKAGE_TYPE_REF, "); // packge_type
			sb.append(
					"(SELECT IMDG_CL_CD || ' (' || IMDG_CL_NM || ')' CL_NM FROM IMDG_CLASS im WHERE im.REC_STATUS='A' AND im.IMDG_CL_CD = mc.IMO_CLASS) AS IMO_CLASS_REF, "); // imo
																																												// //
																																												// class
			sb.append(" mc.* from gbms.MANIFEST_CONTAINERIZE mc where VV_CD = :vvCd ");
			sb.append(" AND REC_STATUS = 'A'");
			sb.append(" ORDER BY INSTRUCTION_TYPE ASC");

			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("vvCd", vvCd);
			log.info("getCustomDetails : SQL:" + sb.toString() + "Params:" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				CustomDetails cd = new CustomDetails();
				cd.setAction(rs.getString("action"));
				cd.setVv_cd(rs.getString("VV_CD"));
				cd.setVessel_dis_port(rs.getString("VESSEL_DIS_PORT"));
				cd.setBl_nbr(rs.getString("BL_NBR"));
				cd.setMaster_bl_nbr(rs.getString("MASTER_BL_NBR"));
				cd.setBl_nbr_remarks(rs.getString("BL_NBR_REMARKS"));
				cd.setCntr_nbr(rs.getString("CNTR_NBR"));
				cd.setCntr_status(CommonUtil.getKeyFromValue(ConstantUtil.CUSTOM_DETAILS_CNTR_STATUS_MAP,
						rs.getString("CNTR_STATUS")));
				cd.setRec_status(rs.getString("REC_STATUS"));
				cd.setInstruction_type(CommonUtil.getKeyFromValue(ConstantUtil.CUSTOM_DETAILS_INSTRUCTION_TYPE_MAP,
						rs.getString("INSTRUCTION_TYPE")));
				cd.setOri_load_port(rs.getString("ORI_LOAD_PORT"));
				cd.setLoad_port(rs.getString("LOAD_PORT"));
				cd.setDis_port(rs.getString("DIS_PORT"));
				cd.setDest_port(rs.getString("DEST_PORT"));
				cd.setPlace_of_receipt_name(rs.getString("PLACE_OF_RECEIPT_NAME"));
				cd.setPlace_of_delivery_name(rs.getString("PLACE_OF_DELIVERY_NAME"));
				
				String cons = !(CommonUtil.deNull(rs.getString("CONSIGNEE_CD")).isEmpty())
						? CommonUtil.deNull(rs.getString("CONSIGNEE_NAME")) + " ("
								+ CommonUtil.deNull(rs.getString("CONSIGNEE_CD")) + ")"
						: "";
				cd.setConsignee(cons);
				cd.setConsignee_cd(CommonUtil.deNull(rs.getString("CONSIGNEE_CD")));
				cd.setConsignee_name(CommonUtil.deNull(rs.getString("CONSIGNEE_NAME")));
				cd.setConsignee_uen(rs.getString("CONSIGNEE_UEN"));
				cd.setConsignee_address(rs.getString("CONSIGNEE_ADDRESS"));
				// shipper
				String shipper = rs.getString("SHIPPER_NAME") + " (" + rs.getString("SHIPPER_CD") + ")";
				cd.setShipper(shipper);
				cd.setShipper_cd(rs.getString("SHIPPER_CD"));
				cd.setShipper_name(rs.getString("SHIPPER_NAME"));
				cd.setShipper_uen(rs.getString("SHIPPER_UEN"));
				cd.setShipper_address(rs.getString("SHIPPER_ADDRESS"));

				cd.setNotify_party_name(rs.getString("NOTIFY_PARTY_NAME"));
				cd.setNotify_party_uen(rs.getString("NOTIFY_PARTY_UEN"));
				cd.setNotify_party_contact(rs.getString("NOTIFY_PARTY_CONTACT"));
				cd.setNotify_party_email(rs.getString("NOTIFY_PARTY_EMAIL"));
				cd.setNotify_party_address(rs.getString("NOTIFY_PARTY_ADDRESS"));
				cd.setFreight_fowarder_name(rs.getString("FREIGHT_FOWARDER_NAME"));
				cd.setFreight_fowarder_uen(rs.getString("FREIGHT_FOWARDER_UEN"));
				cd.setFreight_fowarder_contact(rs.getString("FREIGHT_FOWARDER_CONTACT"));
				cd.setFreight_fowarder_email(rs.getString("FREIGHT_FOWARDER_EMAIL"));
				cd.setFreight_fowarder_address(rs.getString("FREIGHT_FOWARDER_ADDRESS"));
				cd.setStevedore_name(rs.getString("STEVEDORE_NAME"));
				cd.setStevedore_uen(rs.getString("STEVEDORE_UEN"));
				cd.setStevedore_contact(rs.getString("STEVEDORE_CONTACT"));
				cd.setStevedore_email(rs.getString("STEVEDORE_EMAIL"));
				cd.setStevedore_address(rs.getString("STEVEDORE_ADDRESS"));
				cd.setCargo_agent_name(rs.getString("CARGO_AGENT_NAME"));
				cd.setCargo_agent_uen(rs.getString("CARGO_AGENT_UEN"));
				cd.setCargo_agent_contact(rs.getString("CARGO_AGENT_CONTACT"));
				cd.setCargo_agent_email(rs.getString("CARGO_AGENT_EMAIL"));
				cd.setCargo_agent_address(rs.getString("CARGO_AGENT_ADDRESS"));
				cd.setItem_no(rs.getString("ITEM_NO"));
				cd.setPackage_type(rs.getString("PACKAGE_TYPE_REF"));
				cd.setHscode(rs.getString("HSCODE"));
				cd.setPackage_quantity(rs.getString("PACKAGE_QUANTITY"));
				cd.setWeight(rs.getString("WEIGHT"));
				cd.setMeasurement(rs.getString("MEASUREMENT"));
				cd.setHandling_instruction(CommonUtil.getKeyFromValue(
						ConstantUtil.CUSTOM_DETAILS_HANDLING_INSTRUCTION_MAP, rs.getString("HANDLING_INSTRUCTION")));
				cd.setCargo_description(rs.getString("CARGO_DESCRIPTION"));
				cd.setMark_and_no(rs.getString("MARK_AND_NO"));
				cd.setDg_ind(CommonUtil.deNull(rs.getString("DG_IND")).equalsIgnoreCase("Y") ? ConstantUtil.CUSTOM_YES
						: ConstantUtil.CUSTOM_NO);
				cd.setImo_class(rs.getString("IMO_CLASS_REF"));
				cd.setUndg_nbr(rs.getString("UNDG_NBR"));
				cd.setFlashpoint(rs.getString("FLASHPOINT"));
				cd.setPacking_grp(rs.getString("PACKING_GRP"));
				cd.setIso(rs.getString("ISO"));
				cd.setGross_wt(rs.getString("GROSS_WT"));
				cd.setSeal_nbr_carrier(rs.getString("SEAL_NBR_CARRIER"));
				cd.setLast_modify_user_id(rs.getString("LAST_MODIFY_USER_ID"));
				cd.setLast_modify_dttm(rs.getString("LAST_MODIFY_DTTM"));
				customDetailsData.add(cd);
			}

			log.info("getCustomDetails : size" + customDetailsData.size());
		} catch (Exception e) {
			log.info("Exception getCustomDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info(" END getCustomDetails ");
		}
		return customDetailsData;
	}

	@Override
	public List<CustomDetailsUploadConfig> getTemplateHeader() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<CustomDetailsUploadConfig> customDetailsUploadConfig = null;
		try {
			log.info("START getTemplateHeader");
			sb.append(
					"SELECT CUSTOM_DETAILS_UPLOAD_CONFIG_ID,ATTR_NM attr_name,ATTR_DESC,INPUT_TYPE,DISPLAY_SEQ,MANDATORY_IND,LOOKUP_TABLE,");
			sb.append("LOOKUP_CAT_CD,COLUMN_NM, MAX_LENGTH, LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM ");
			sb.append("FROM GBMS.CUSTOM_DETAILS_UPLOAD_CONFIG WHERE TYPE_CD ='E' ORDER BY DISPLAY_SEQ ASC");
			log.info("getTemplateHeader :" + "SQL:" + sb.toString());

			customDetailsUploadConfig = namedParameterJdbcTemplate.query(sb.toString(),
					new BeanPropertyRowMapper<CustomDetailsUploadConfig>(CustomDetailsUploadConfig.class));

			log.info("getSplitBlTemplateHeader : size:" + customDetailsUploadConfig.size());
		} catch (Exception e) {
			log.info("Exception getSplitBlTemplateHeader : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getSplitBlTemplateHeader");
		}
		return customDetailsUploadConfig;
	}

	@Override
	public List<String> getIMOClassList() throws BusinessException {
		log.info("START: getIMOClassList");
		List<String> listIMDGCls = new ArrayList<String>();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		try {

			String sql = "SELECT IMDG_CL_CD || ' (' || IMDG_CL_NM || ')' CL_NM FROM IMDG_CLASS WHERE REC_STATUS='A' ORDER BY IMDG_CL_CD";
			log.info("SQL getIMOClassList : " + sql);
			log.info("paramMap getIMOClassList : " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				listIMDGCls.add(rs.getString("CL_NM"));
			}
			log.info("getIMOClassList Result: " + listIMDGCls.size());
		} catch (Exception e) {
			log.error("Exception getIMOClassList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("End getIMOClassList DAO");
		}
		return listIMDGCls;
	}

	@Override
	public boolean insertActionTrial(String varNbr, String typeCd, String summary, String lastTimestamp, String userId)
			throws BusinessException {
		StringBuffer sb_insert = new StringBuffer();
		CustomDetailsActionTrail customDetailsActionTrail = new CustomDetailsActionTrail();
		boolean insert = false;
		try {
			log.info("START insertActionTrial :  varNbr:" + varNbr + "typeCd:" + typeCd + "summary:" + summary
					+ "lastTimestamp:" + lastTimestamp + "userId:" + userId);

			StringBuilder sbSeq = new StringBuilder();
			sbSeq.append("SELECT GBMS.CUSTOM_DETAILS_ACT_TRL_ID.nextval AS seqVal FROM DUAL");
			Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(),
					new HashMap<String, String>());
			BigDecimal id = (BigDecimal) results.get("seqVal");

			sb_insert.append("INSERT INTO GBMS.CUSTOM_DETAILS_ACT_TRL");
			sb_insert.append(
					"( CUSTOM_DETAILS_ACT_TRL_ID, VV_CD, TYPE_CD, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, REMARKS)");
			sb_insert.append(" VALUES(:custom_details_act_trl_id, :vv_cd, :type_cd, :last_modify_user_id,  ");
			sb_insert.append(" TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'),:remarks)");

			customDetailsActionTrail.setCustom_details_act_trl_id(id.longValue());
			customDetailsActionTrail.setVv_cd(varNbr);
			customDetailsActionTrail.setType_cd(typeCd);
			customDetailsActionTrail.setLast_modify_user_id(userId);
			customDetailsActionTrail.setLast_modify_dttm(lastTimestamp);
			customDetailsActionTrail.setRemarks(summary);
			log.info("insertActionTrial:SQL" + customDetailsActionTrail.toString() + "SQL : " + sb_insert.toString());
			int rows = namedParameterJdbcTemplate.update(sb_insert.toString(),
					new BeanPropertySqlParameterSource(customDetailsActionTrail));
			log.info("insertActionTrial:" + rows);
			insert = true;
		} catch (Exception e) {
			log.info("Exception insertActionTrial : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END insertActionTrial");
		}
		return insert;
	}

	@Override
	public Long insertCustomExcelDetails(CustomDetailsFileUploadDetails customDetailsFileUploadDetails)
			throws BusinessException {
		StringBuffer sb = new StringBuffer();
		try {

			log.info("Start insertCustomExcelDetails :" + customDetailsFileUploadDetails.toString());

			StringBuilder sbSeq = new StringBuilder();
			sbSeq.append("SELECT GBMS.CUSTOM_DETAILS_UPLOAD_SEQ.nextval AS seqVal FROM DUAL");
			Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(),
					new HashMap<String, String>());
			BigDecimal seqNbr = (BigDecimal) results.get("seqVal");
			log.info("seqNbr " + seqNbr);

			sb.append("INSERT INTO GBMS.CUSTOM_UPLOAD_DETAILS ");
			sb.append(
					"( CUSTOM_UPLOAD_SEQ_NBR, ACTUAL_FILE_NM, VV_CD, ASSIGNED_FILE_NM, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM)");
			sb.append("VALUES( :seq_id, :actual_file_name, :vv_cd, :assigned_file_name, ");
			sb.append(
					":last_modified_user_id, TO_TIMESTAMP(:last_modified_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");

			customDetailsFileUploadDetails.setSeq_id(seqNbr.longValue());
			log.info("insertCustomExcelDetails:SQL" + customDetailsFileUploadDetails.toString() + "SQL : "
					+ sb.toString());

			int rows = namedParameterJdbcTemplate.update(sb.toString(),
					new BeanPropertySqlParameterSource(customDetailsFileUploadDetails));
			log.info("rows " + rows);

			sb.setLength(0);
			sb.append("INSERT INTO GBMS.CUSTOM_UPLOAD_DETAILS_TRANS ");
			sb.append("(AUDIT_DTTM, CUSTOM_UPLOAD_SEQ_NBR, VV_CD, ACTUAL_FILE_NM, ASSIGNED_FILE_NM, ");
			sb.append("LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
			sb.append("VALUES(sysdate, :seq_id, :vv_cd, :actual_file_name, :assigned_file_name, ");
			sb.append(
					":last_modified_user_id, TO_TIMESTAMP(:last_modified_dttm,'dd-mm-yyyy hh24:mi:ss.ff3')) ");
			int rowsTrans = namedParameterJdbcTemplate.update(sb.toString(),
					new BeanPropertySqlParameterSource(customDetailsFileUploadDetails));
			log.info("rowsTrans " + rowsTrans);

			return (seqNbr.longValue());
		} catch (Exception e) {
			log.info("Exception insertCustomExcelDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END insertCustomExcelDetails ");
		}
	}

	@Override
	public boolean updateCustomDetailsExcelDetails(Long seq_id, String outputFileName) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		boolean update = true;
		try {
			log.info("START updateCustomDetailsExcelDetails : " + "seq_id:" + seq_id + "outputFileName:"
					+ outputFileName);
			sb.append("UPDATE GBMS.CUSTOM_UPLOAD_DETAILS ");
			sb.append("SET ");
			sb.append("PROCESSED_FILE_NM =:outputFileName ");
			sb.append("WHERE CUSTOM_UPLOAD_SEQ_NBR=:seq_id");

			paramMap.put("outputFileName", outputFileName);
			paramMap.put("seq_id", seq_id);

			log.info("updateCustomDetailsExcelDetails: " + "SQL:" + sb.toString() + "Params:" + paramMap.toString());
			int rows = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			log.info("updateCustomDetailsExcelDetails:rows " + rows);

		} catch (Exception e) {
			log.info("Exception updateCustomDetailsExcelDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END updateCustomDetailsExcelDetails");
		}
		return update;
	}

	@Override
	public List<CustomDetails> insertCustomDetailsData(List<CustomDetails> customDetailsRecords, String vvCd,
			String userId, String companyCode) throws BusinessException {
		try {
			log.info("START insertCustomDetailsData " + " vvCd :" + vvCd + "userId :" + userId
					+ ", customDetailsRecords :" + customDetailsRecords.size());
			boolean processsResponse = false;
			for (CustomDetails customDetails : customDetailsRecords) {
				try {
					processsResponse = false;
					customDetails.setLast_modify_user_id(userId);
					customDetails.setVv_cd(vvCd);
					String cntrNbr = customDetails.getCntr_nbr();

					// Action - Insert and success row validated records
					if (customDetails.getAction() != null
							&& customDetails.getAction().equalsIgnoreCase(ConstantUtil.action_delete)
							&& customDetails.getMessage().equalsIgnoreCase(ConstantUtil.success)) {
						log.info("delete customDetailsRecords :" + customDetails.toString());

						// DELETE
						if (cntrNbr == null || cntrNbr == "") {
							log.info(" Excel processExcelCustomDetailsDetails :" + ConstantUtil.ErrorMsg_cntrNbrNotExist
									+ ", customs details record is :" + customDetails.toString());
							customDetails.setMessage(ConstantUtil.ErrorMsg_cntrNbrNotExist);
							continue;
						}

						boolean isCustomDetailsDeleted = deleteCustomDetails(customDetails);
						log.info("isCustomDetailsDeleted : " + isCustomDetailsDeleted);
					} else if (customDetails.getMessage().equalsIgnoreCase(ConstantUtil.success)) {

						log.info("insert/update customDetailsData :" + customDetails.toString());
						if (customDetails.getAction().equalsIgnoreCase(ConstantUtil.action_add)) {
							// ADD
							processsResponse = insertCustomDetails(customDetails);
						} else if (customDetails.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
							// UPDATE
							if (cntrNbr == null || cntrNbr == "") {
								log.info(
										" Excel processExcelCustomDetailsDetails :" + ConstantUtil.ErrorMsg_BlNoNotExist
												+ ", customs details record is :" + customDetails.toString());
								customDetails.setMessage(ConstantUtil.ErrorMsg_BlNoNotExist);
								continue;
							}
							processsResponse = updateCustomDetails(customDetails);
						}
						if (!processsResponse) {
							log.info(" Excel processExcelCustomDetailsDetails :"
									+ ConstantUtil.ErrorMsg_CustomDetailsProcess + ", customs details record is :"
									+ customDetails.toString());
							customDetails.setMessage(ConstantUtil.ErrorMsg_CustomDetailsProcess);
							continue;
						}

					}
				} catch (Exception e) {
					log.info("Exception insertCustomDetailsData : ", e);
					log.info(" Exception in Excel processExcelCustomDetailsDetails iteration  "
							+ " for  customDetails :" + customDetails.toString() + ", excpetion " + e.toString());
					customDetails.setMessage(ConstantUtil.ErrorMsg_Common);
				}

				customDetails.setBl_nbr(customDetails.getBl_nbr() == null ? "" : customDetails.getBl_nbr());
			}
		} catch (Exception e) {
			log.info("Exception insertCustomDetailsData : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END insertCustomDetailsData ");
		}
		return customDetailsRecords;
	}

	private boolean deleteCustomDetails(CustomDetails customDetails) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		boolean delete = false;
		try {
			log.info("START deleteCustomDetails : " + "customDetails:" + customDetails.toString());

			sb.append("UPDATE GBMS.MANIFEST_CONTAINERIZE ");
			sb.append("SET REC_STATUS='I', LAST_MODIFY_USER_ID=:last_modify_user_id,");
			sb.append("LAST_MODIFY_DTTM=SYSDATE ");
			sb.append("WHERE CNTR_NBR=:cntr_nbr ");
			sb.append("AND VV_CD = :vv_cd AND REC_STATUS ='A' ");

			log.info(" *** deleteCustomDetails SQL *****" + sb.toString());
			int deleted = namedParameterJdbcTemplate.update(sb.toString(),
					new BeanPropertySqlParameterSource(customDetails));
			delete = deleted > 0 ? true : false;

		} catch (Exception e) {
			log.info("Exception deleteCustomDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END deleteCustomDetails results:" + delete);
		}
		return delete;
	}

	private boolean insertCustomDetails(CustomDetails customDetails) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		boolean insert = false;
		try {
			log.info("START insertCustomDetails customDetails :" + customDetails.toString());
			
			StringBuilder sbSeq = new StringBuilder();
			sbSeq.append("SELECT GBMS.CUSTOM_DETAILS_MANIFEST_CONTAINERIZE.nextval AS seqVal FROM DUAL");
			Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(),
					new HashMap<String, String>());
			BigDecimal id = (BigDecimal) results.get("seqVal");
			customDetails.setMft_cntr_seq_nbr(id.longValue());
			
			sb.append("INSERT INTO GBMS.MANIFEST_CONTAINERIZE ");
			sb.append("(MFT_CNTR_SEQ_NBR, VV_CD, VESSEL_DIS_PORT, BL_NBR, MASTER_BL_NBR,");
			sb.append("BL_NBR_REMARKS, INSTRUCTION_TYPE, ORI_LOAD_PORT, ");
			sb.append("LOAD_PORT, DIS_PORT, DEST_PORT, PLACE_OF_RECEIPT_NAME,");
			sb.append("PLACE_OF_DELIVERY_NAME, CONSIGNEE_NAME, CONSIGNEE_UEN, ");
			sb.append("CONSIGNEE_ADDRESS, SHIPPER_NAME, SHIPPER_UEN, SHIPPER_ADDRESS, ");
			sb.append("NOTIFY_PARTY_NAME, NOTIFY_PARTY_UEN, NOTIFY_PARTY_CONTACT, ");
			sb.append("NOTIFY_PARTY_EMAIL, NOTIFY_PARTY_ADDRESS, FREIGHT_FOWARDER_NAME, ");
			sb.append("FREIGHT_FOWARDER_UEN, FREIGHT_FOWARDER_CONTACT, FREIGHT_FOWARDER_EMAIL, ");
			sb.append("FREIGHT_FOWARDER_ADDRESS, STEVEDORE_NAME, STEVEDORE_UEN, STEVEDORE_CONTACT,");
			sb.append("STEVEDORE_EMAIL, STEVEDORE_ADDRESS, CARGO_AGENT_NAME, CARGO_AGENT_UEN,");
			sb.append("CARGO_AGENT_CONTACT, CARGO_AGENT_EMAIL, CARGO_AGENT_ADDRESS, ITEM_NO, ");
			sb.append("PACKAGE_TYPE, HSCODE, PACKAGE_QUANTITY, WEIGHT, MEASUREMENT, ");
			sb.append("HANDLING_INSTRUCTION, CARGO_DESCRIPTION, MARK_AND_NO, DG_IND, ");
			sb.append("IMO_CLASS, UNDG_NBR, FLASHPOINT, PACKING_GRP, CNTR_NBR, ");
			sb.append("CNTR_STATUS, ISO, GROSS_WT, SEAL_NBR_CARRIER, CONSIGNEE_CD, SHIPPER_CD,");
			sb.append("LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, REC_STATUS)");
			sb.append("VALUES(:mft_cntr_seq_nbr, :vv_cd, :vessel_dis_port, :bl_nbr, :master_bl_nbr,");
			sb.append(":bl_nbr_remarks, :instruction_type, :ori_load_port, ");
			sb.append(":load_port, :dis_port, :dest_port, :place_of_receipt_name,");
			sb.append(":place_of_delivery_name, :consignee_name, :consignee_uen, ");
			sb.append(":consignee_address, :shipper_name, :shipper_uen, :shipper_address, ");
			sb.append(":notify_party_name, :notify_party_uen, :notify_party_contact,");
			sb.append(":notify_party_email, :notify_party_address, :freight_fowarder_name, ");
			sb.append(":freight_fowarder_uen, :freight_fowarder_contact, :freight_fowarder_email, ");
			sb.append(":freight_fowarder_address, :stevedore_name, :stevedore_uen, :stevedore_contact,");
			sb.append(":stevedore_email, :stevedore_address, :cargo_agent_name, :cargo_agent_uen,");
			sb.append(":cargo_agent_contact, :cargo_agent_email, :cargo_agent_address, :item_no,");
			sb.append(":package_type, :hscode, :package_quantity, :weight, :measurement,");
			sb.append(":handling_instruction, :cargo_description,:mark_and_no, :dg_ind,");
			sb.append(":imo_class, :undg_nbr, :flashpoint, :packing_grp, :cntr_nbr, ");
			sb.append(":cntr_status, :iso, :gross_wt, :seal_nbr_carrier, :consignee_cd, :shipper_cd,");
			sb.append(":last_modify_user_id, SYSDATE, 'A')");

			log.info(" *** insertCustomDetails SQL *****" + sb.toString());
			int inserted = namedParameterJdbcTemplate.update(sb.toString(),
					new BeanPropertySqlParameterSource(customDetails));

			insert = inserted > 0 ? true : false;

		} catch (Exception e) {
			log.info("Exception insertCustomDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END insertCustomDetails result:" + insert);
		}
		return insert;
	}

	private boolean updateCustomDetails(CustomDetails customDetails) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		boolean update = false;
		try {
			log.info("START updateCustomDetails customDetails :" + customDetails.toString());
			sb.append("UPDATE GBMS.MANIFEST_CONTAINERIZE ");
			sb.append("SET VV_CD=:vv_cd, VESSEL_DIS_PORT=:vessel_dis_port,");
			sb.append("MASTER_BL_NBR=:master_bl_nbr, BL_NBR_REMARKS=:bl_nbr_remarks, ");
			sb.append("INSTRUCTION_TYPE=:instruction_type, ORI_LOAD_PORT=:ori_load_port, ");
			sb.append("LOAD_PORT=:load_port, DIS_PORT=:dis_port, DEST_PORT=:dest_port,");
			sb.append("PLACE_OF_RECEIPT_NAME=:place_of_receipt_name, ");
			sb.append("PLACE_OF_DELIVERY_NAME=:place_of_delivery_name, ");
			sb.append("CONSIGNEE_NAME=:consignee_name, CONSIGNEE_UEN=:consignee_uen, ");
			sb.append("CONSIGNEE_ADDRESS=:consignee_address, SHIPPER_NAME=:shipper_name, ");
			sb.append("SHIPPER_UEN=:shipper_uen, SHIPPER_ADDRESS=:shipper_address, ");
			sb.append("NOTIFY_PARTY_NAME=:notify_party_name,");
			sb.append("NOTIFY_PARTY_UEN=:notify_party_uen, NOTIFY_PARTY_CONTACT=:notify_party_contact, ");
			sb.append("NOTIFY_PARTY_EMAIL=:notify_party_email, NOTIFY_PARTY_ADDRESS=:notify_party_address, ");
			sb.append("FREIGHT_FOWARDER_NAME=:freight_fowarder_name, FREIGHT_FOWARDER_UEN=:freight_fowarder_uen, ");
			sb.append("FREIGHT_FOWARDER_CONTACT=:freight_fowarder_contact, ");
			sb.append("FREIGHT_FOWARDER_EMAIL=:freight_fowarder_email, ");
			sb.append("FREIGHT_FOWARDER_ADDRESS=:freight_fowarder_address, STEVEDORE_NAME=:stevedore_name, ");
			sb.append("STEVEDORE_UEN=:stevedore_uen, STEVEDORE_CONTACT=:stevedore_contact, ");
			sb.append("STEVEDORE_EMAIL=:stevedore_email, STEVEDORE_ADDRESS=:stevedore_address, ");
			sb.append("CARGO_AGENT_NAME=:cargo_agent_name, CARGO_AGENT_UEN=:cargo_agent_uen,");
			sb.append("CARGO_AGENT_CONTACT=:cargo_agent_contact, CARGO_AGENT_EMAIL=:cargo_agent_email, ");
			sb.append("CARGO_AGENT_ADDRESS=:cargo_agent_address, ITEM_NO=:item_no, PACKAGE_TYPE=:package_type, ");
			sb.append("HSCODE=:hscode, PACKAGE_QUANTITY=:package_quantity, WEIGHT=:weight, ");
			sb.append("MEASUREMENT=:measurement, HANDLING_INSTRUCTION=:handling_instruction, ");
			sb.append("CARGO_DESCRIPTION=:cargo_description, MARK_AND_NO=:mark_and_no, DG_IND=:dg_ind,");
			sb.append("IMO_CLASS=:imo_class, UNDG_NBR=:undg_nbr, ");
			sb.append("FLASHPOINT=:flashpoint, PACKING_GRP=:packing_grp, ");
			sb.append("CNTR_STATUS=:cntr_status, ISO=:iso, GROSS_WT=:gross_wt, SEAL_NBR_CARRIER=:seal_nbr_carrier,");
			sb.append("CONSIGNEE_CD=:consignee_cd, SHIPPER_CD=:shipper_cd,");
			sb.append("LAST_MODIFY_USER_ID=:last_modify_user_id, LAST_MODIFY_DTTM=SYSDATE ");
			sb.append("WHERE CNTR_NBR=:cntr_nbr ");
			sb.append("AND VV_CD = :vv_cd AND REC_STATUS ='A' ");

			log.info(" *** updateCustomDetails SQL *****" + sb.toString());
			int updated = namedParameterJdbcTemplate.update(sb.toString(),
					new BeanPropertySqlParameterSource(customDetails));

			update = updated > 0 ? true : false;

		} catch (Exception e) {
			log.info("Exception updateCustomDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END updateCustomDetails result:" + update);
		}
		return update;
	}

	@Override
	public CustomDetailsActionTrailDetails customDetailsActionTrailDetail(String custom_act_trl_id, String typeCd)
			throws BusinessException {
		StringBuffer sb = new StringBuffer();
		CustomDetailsActionTrailDetails custom_trail_details = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START customDetailsActionTrailDetail " + "Params:" + custom_act_trl_id + ",typeCd:" + typeCd);

			sb.append("	SELECT ");
			sb.append("	cud.CUSTOM_UPLOAD_SEQ_NBR as seq_id, ");
			sb.append("	cud.ACTUAL_FILE_NM as actual_file_name, ");
			sb.append("	cud.ASSIGNED_FILE_NM as assigned_file_name, ");
			sb.append("	cud.VV_CD as vv_cd, ");
			sb.append("	cud.PROCESSED_FILE_NM AS output_file_name, ");
			sb.append("	TO_CHAR(cdat.LAST_MODIFY_DTTM, 'DD-MM-YYYY HH24:MI') last_modified_dttm, ");
			sb.append(
					" CASE WHEN ac.user_nm IS NOT NULL THEN  CONCAT (CONCAT (ac.user_nm, ' - '), ac.CUST_CD) ELSE '' END last_modified_user_id , ");
			sb.append("	cdat.REMARKS  as remarks");
			sb.append("	FROM ");
			sb.append("	gbms.CUSTOM_DETAILS_ACT_TRL cdat ");
			sb.append(
					" LEFT JOIN gbms.CUSTOM_UPLOAD_DETAILS cud ON cdat.LAST_MODIFY_DTTM = cud.LAST_MODIFY_DTTM AND cdat.VV_CD = cud.VV_CD  ");
			sb.append(
					" LEFT JOIN TOPS.logon_acct ac ON ac.login_id= SUBSTR(cdat.LAST_MODIFY_USER_ID, INSTR( cdat.LAST_MODIFY_USER_ID, '/', -1 ) + 1 ) ");
			sb.append("	WHERE ");
			sb.append("	cdat.CUSTOM_DETAILS_ACT_TRL_ID = :custom_act_trl_id  ");

			paramMap.put("custom_act_trl_id", custom_act_trl_id);
			log.info("getBkActionTrailDetail SQL:" + sb.toString() + "param:" + custom_act_trl_id);
			custom_trail_details = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap,
					new BeanPropertyRowMapper<CustomDetailsActionTrailDetails>(CustomDetailsActionTrailDetails.class));
			Pattern p = Pattern.compile("-?\\d+");
			Matcher m = p.matcher(custom_trail_details.getRemarks());
			int mIndex = 0;
			while (m.find()) {
				if (typeCd.equalsIgnoreCase(ConstantUtil.excel_type_cd)) {
					if (mIndex == 0) {
						custom_trail_details.setTotalLineProcessed(m.group());
					} else if (mIndex == 1) {
						custom_trail_details.setTotalSuccess(m.group());
					} else if (mIndex == 2) {
						custom_trail_details.setTotalFail(m.group());
					}
				} else if (typeCd.equalsIgnoreCase(ConstantUtil.cuscar_type_cd)) {
					if (mIndex == 0) {
						custom_trail_details.setTotalRecordRcv(m.group());
					} else if (mIndex == 1) {
						custom_trail_details.setTotalError(m.group());
					} else if (mIndex == 2) {
						custom_trail_details.setTotalSuccess(m.group());
					} else if (mIndex == 3) {
						custom_trail_details.setTotalCreated(m.group());
					} else if (mIndex == 4) {
						custom_trail_details.setTotalUpdated(m.group());
					} else if (mIndex == 5) {
						custom_trail_details.setTotalDeleted(m.group());
					}
					String remarks = custom_trail_details.getRemarks();
					remarks = formatRecord(remarks);
					custom_trail_details.setRemarks(remarks);
				}
				mIndex++;
			}
		} catch (Exception e) {
			log.info("Exception customDetailsActionTrailDetail : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END customDetailsActionTrailDetail");
		}
		return custom_trail_details;
	}
	
	private String formatRecord(String remarks) throws BusinessException {
		try {
			String marker = "Total Record Processed Successfully :";
	        if (remarks.contains("Created") || remarks.contains("Updated") || remarks.contains("Deleted")) {
	            int endIndex = remarks.indexOf(marker);
	            if (endIndex != -1) {
	                int afterMarker = remarks.indexOf("/", endIndex);
	                if (afterMarker != -1) {
	                    return remarks.substring(0, afterMarker).trim();
	                } else {
	                    return remarks.trim();
	                }
	            }
	        }
	        return remarks.trim();
		} catch (Exception e) {
			log.info("Exception formatRecord : ", e);
			throw new BusinessException("M4201");
		}
	}

	@Override
	public BookingReferenceFileUploadDetails getCustomDetailFileUploadDetails(String seq_id) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		BookingReferenceFileUploadDetails fileDetails = null;
		try {
			log.info("START getCustomDetailFileUploadDetails :" + seq_id);
			Map<String, String> paramMap = new HashMap<String, String>();
			sb.append("SELECT ");
			sb.append(" VV_CD as vv_cd, ");
			sb.append(" ASSIGNED_FILE_NM as assigned_file_name, ");
			sb.append("	ACTUAL_FILE_NM as actual_file_name, ");
			sb.append(" PROCESSED_FILE_NM as output_file_name ");
			sb.append(" FROM gbms.CUSTOM_UPLOAD_DETAILS ");
			sb.append(" WHERE CUSTOM_UPLOAD_SEQ_NBR = :seq_id");
			paramMap.put("seq_id", seq_id);
			log.info("getCustomDetailFileUploadDetails :" + "SQL:" + sb.toString() + ", paramap: "
					+ paramMap.toString());
			fileDetails = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap,
					new BeanPropertyRowMapper<BookingReferenceFileUploadDetails>(
							BookingReferenceFileUploadDetails.class));
			log.info("getCargoBkFileUploadDetails :" + fileDetails.toString());
		} catch (Exception e) {
			log.info("Exception getCustomDetailFileUploadDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getCustomDetailFileUploadDetails ");
		}
		return fileDetails;
	}

	@Override
	public int customDetailIsExist(String data, String varNbr, String instrucionType) throws BusinessException {
		int count = 0;
		String sql = "";
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: customDetailIsExist  DAO  Start Obj " + " data:" + CommonUtility.deNull(data)
					+ ",varNbr:" + varNbr);
			sb.append("SELECT count(*) from GBMS.MANIFEST_CONTAINERIZE where ");
			sb.append("VV_CD = :varNbr AND REC_STATUS ='A' ");
			sb.append("AND CNTR_NBR = :data AND INSTRUCTION_TYPE in (:instrucionType) ");
			sql = sb.toString();
			paramMap.put("data", data);
			paramMap.put("varNbr", varNbr);
			paramMap.put("instrucionType", instrucionType);
			log.info(" *** customDetailIsExist SQL *****" + sql);
			log.info(" *** customDetailIsExist params *****" + paramMap.toString());
			count = (int) namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
		} catch (Exception e) {
			log.info("Exception customDetailIsExist : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: customDetailIsExist DAO Result:" + count);
		}
		return count;
	}

	@Override
	public int containerIsExist(String cntrNbr, String varNbr) throws BusinessException {
		int count = 0;
		String sql = "";
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: containerIsExist  DAO  Start Obj " + " cntrNbr:" + CommonUtility.deNull(cntrNbr) + ", varNbr : " + varNbr);
			sb.append("SELECT count(*) from TOPS.CNTR where ");
			sb.append("CNTR_NBR = :cntrNbr AND ");
			sb.append("(DISC_VV_CD =:varNbr OR LOAD_VV_CD = :varNbr) AND txn_status <> 'D'");
			
			sql = sb.toString();
			paramMap.put("cntrNbr", (cntrNbr.toUpperCase()).trim());
			paramMap.put("varNbr", varNbr);
			log.info(" *** containerIsExist SQL *****" + sql);
			log.info(" *** containerIsExist params *****" + paramMap.toString());
			count = (int) namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
		} catch (Exception e) {
			log.info("Exception containerIsExist : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: containerIsExist DAO Result:" + count);
		}
		return count;
	}
	

	@Override
	public boolean isCntrDetailsMatch(String data, String cntrNbr, String type, String vvCd) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		boolean isMatch = false;
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: isCntrDetailsMatch  DAO  Start Obj " + " data:" + CommonUtility.deNull(data) + " type:"
					+ CommonUtility.deNull(type) + " vvCd:" + CommonUtility.deNull(vvCd));
			sb.append("SELECT STATUS, DG_IND, BILL_LADING_NBR,");	
			sb.append("CASE WHEN PURP_CD = 'IM' THEN 'LI'  ");
			sb.append("WHEN PURP_CD = 'EX' OR (PURP_CD = 'RS' AND PREV_PURP_CD = 'EX') THEN 'LE' ");
			sb.append("WHEN ((DISC_VV_CD = :vvCd AND LOAD_VV_CD = :vvCd) AND PURP_CD = 'LN') THEN 'T'  ");
			sb.append("WHEN DISC_VV_CD = :vvCd  AND PURP_CD NOT IN ('LN','SH') THEN 'TS' ");
			sb.append("WHEN (LOAD_VV_CD = :vvCd AND NVL(DISC_VV_CD, ' ') <> :vvCd ) THEN 'TE'  ");
			sb.append("ELSE 'T' END AS PURPOSE_CODE ");
			sb.append("from TOPS.CNTR where ");
			sb.append("CNTR_NBR = :cntrNbr ");
			sb.append("AND (LOAD_VV_CD = :vvCd OR DISC_VV_CD = :vvCd) ");
			sb.append("AND txn_status<>'D'  ");
			sb.append("ORDER BY LAST_MODIFY_DTTM DESC ");
			
			sql = sb.toString();
			paramMap.put("cntrNbr", cntrNbr);
			paramMap.put("vvCd", vvCd);
			log.info(" *** isCntrDetailsMatch SQL *****" + sql);
			log.info(" *** isCntrDetailsMatch params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				if (type.equalsIgnoreCase(ConstantUtil.CUSTOM_CNTR_STATUS)) {
					isMatch = CommonUtil.deNull(rs.getString("STATUS")).equalsIgnoreCase(CommonUtil.deNull(data));
				} else if (type.equalsIgnoreCase(ConstantUtil.CUSTOM_INSTRUCTION_TYPE)) {
					isMatch = CommonUtil.deNull(rs.getString("PURPOSE_CODE")).equalsIgnoreCase(CommonUtil.deNull(data));
				} else if (type.equalsIgnoreCase(ConstantUtil.CUSTOM_DG_IND)) {
					isMatch = CommonUtil.deNull(rs.getString("DG_IND")).equalsIgnoreCase(CommonUtil.deNull(data));
				} else if (type.equalsIgnoreCase(ConstantUtil.CUSTOM_BL_NBR)) {
					String blTemp = CommonUtil.deNull(rs.getString("BILL_LADING_NBR"));
					if (!(blTemp).isEmpty()) {
						isMatch = blTemp.equalsIgnoreCase(CommonUtil.deNull(data));
					} else {
						isMatch = true;
					}
				}
			}
		} catch (Exception e) {
			log.info("Exception isCntrDetailsMatch : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isCntrDetailsMatch DAO Result:" + isMatch);
		}
		return isMatch;
	}

	@Override
	public String getTotalCntrForVessel(String varNbr) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		int totalCntrDisc = 0;
		int totalUploadedDisc = 0;
		int totalCntrLoad = 0;
		int totalUploadedLoad = 0;
		String summary = null;
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getTotalCntrForVessel  DAO  Start " + " varNbr:" + CommonUtility.deNull(varNbr));
			sb.append(" SELECT SUM(CASE WHEN (c.LOAD_VV_CD = :varNbr) THEN 1 ELSE 0 END)  AS load_count ");
			sb.append(" FROM CNTR c,  ith_cntr i WHERE ");
			sb.append(" c.SHIPMENT_STATUS NOT IN ('SO', 'SH') ");
			sb.append(" AND c.PURP_CD NOT IN ('LN','SH') ");
			sb.append(" AND c.cntr_seq_nbr = i.cntr_seq_nbr(+) ");
			sb.append(" AND c.txn_status <> 'D' ");
			sb.append(" AND ((c.INTERGATEWAY_IND IS NULL OR c.INTERGATEWAY_IND = 'N') ");
			sb.append(" OR (i.MCT_STATUS IS NULL OR i.MCT_STATUS = 'A')) ");
			
			sql = sb.toString();
			paramMap.put("varNbr", varNbr);
			log.info(" *** getTotalCntrForVessel SQL LOAD*****" + sql);
			log.info(" *** getTotalCntrForVessel params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				totalCntrLoad = rs.getInt("load_count");				
			}
			
			sb.setLength(0);
			sb.append(" SELECT SUM(CASE WHEN (a.DISC_VV_CD = :varNbr) THEN 1 ELSE 0 END)  AS disc_count ");
			sb.append(" FROM cntr a, ith_cntr b, berthing d, arr_bayplan bay WHERE ");
			sb.append(" a.cntr_seq_nbr = b.cntr_seq_nbr(+) ");
			sb.append(" AND a.txn_status <> 'D' ");
			sb.append(" AND (a.shipment_status <> 'SH' OR a.shipment_status IS NULL) ");
			sb.append(" AND a.disc_vv_cd = d.vv_cd(+) ");
			sb.append(" AND d.shift_ind = 1 ");
			sb.append(" AND bay.vv_cd(+)=:varNbr ");
			sb.append(" AND a.cntr_nbr = bay.cntr_nbr(+) ");
			sb.append(" AND a.purp_cd IN ('IM', 'TS', 'RE', 'RS', 'LN','SH') ");
			sb.append(" AND a.cntr_opr_cd IN (SELECT  DISTINCT opr_cd FROM voyage_operator WHERE vv_cd=:varNbr AND opr_type='IC' ) ");
			sql = sb.toString();
			paramMap.put("varNbr", varNbr);
			log.info(" *** getTotalCntrForVessel SQL DISC *****" + sql);
			log.info(" *** getTotalCntrForVessel params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				totalCntrDisc = rs.getInt("disc_count");		
			}
			
			sb.setLength(0);
			sb.append(" SELECT ");
			sb.append(" COUNT(DISTINCT CASE WHEN INSTRUCTION_TYPE IN ('LI', 'TS') THEN CNTR_NBR END) AS disc_count, ");
			sb.append(" COUNT(DISTINCT CASE WHEN INSTRUCTION_TYPE IN ('LE', 'TE') THEN CNTR_NBR END) AS load_count ");
			sb.append(" FROM GBMS.MANIFEST_CONTAINERIZE a ");
			sb.append(" WHERE a.rec_status = 'A' AND a.vv_cd = :varNbr ");
			sql = sb.toString();
			paramMap.put("varNbr", varNbr);
			log.info(" *** getTotalCntrForVessel SQL *****" + sql);
			log.info(" *** getTotalCntrForVessel params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				totalUploadedDisc = rs.getInt("disc_count");
				totalUploadedLoad = rs.getInt("load_count");	
			}
			summary = "Discharge : Total declared in JPOM is " + totalCntrDisc +", Customs declared " + totalUploadedDisc + " /n ";
			summary += "Loading : Total declared in JPOM is " + totalCntrLoad +", Customs declared " + totalUploadedLoad;
		} catch (Exception e) {
			log.info("Exception getTotalCntrForVessel : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTotalCntrForVessel DAO summary:" + summary );
		}
		return summary;
	}

	@Override
	public boolean isShipmentStatusValid(String cntrSeqNbr,String varNbr) throws BusinessException {
		boolean result = false;
		String sql = "";
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: isShipmentStatusValid  DAO  Start Obj " + " cntrSeqNbr:" + CommonUtility.deNull(cntrSeqNbr));
			sb.append("SELECT SHIPMENT_STATUS FROM CNTR WHERE ");
			sb.append("CNTR_SEQ_NBR = :cntrSeqNbr AND ");
			sb.append("(DISC_VV_CD =:varNbr OR LOAD_VV_CD = :varNbr)");
			
			sql = sb.toString();
			paramMap.put("cntrSeqNbr", cntrSeqNbr);
			paramMap.put("varNbr", varNbr);
			log.info(" *** isShipmentStatusValid SQL *****" + sql);
			log.info(" *** isShipmentStatusValid params *****" + paramMap.toString());
			
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if(rs.next()) {
				String shipmentStatus = rs.getString("SHIPMENT_STATUS");
				if(!shipmentStatus.equalsIgnoreCase("SO")) {
					result = true;
				}
			}
		} catch (Exception e) {
			log.info("Exception isShipmentStatusValid : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isShipmentStatusValid DAO Result:" + result);
		}
		return result;
	}

	@Override
	public String getVvcdFromVesselDetails(String vslName, String inVoyNo, String outVoyNo) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		String vvcd = "";
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getVvcdFromVesselDetails  DAO  Start vslName:" + CommonUtility.deNull(vslName)+ " inVoyNo:" + CommonUtility.deNull(inVoyNo)+ " outVoyNo:" + CommonUtility.deNull(outVoyNo));
			sb.append(" SELECT VV_CD FROM VESSEL_CALL WHERE VSL_NM = :vslName ");
			if(!CommonUtil.deNull(inVoyNo).isEmpty()) {sb.append(" AND IN_VOY_NBR = :inVoyNo ");}
			if(!CommonUtil.deNull(outVoyNo).isEmpty()) {sb.append(" AND OUT_VOY_NBR = :outVoyNo ");}
			sql = sb.toString();
			paramMap.put("vslName", vslName.toUpperCase());
			paramMap.put("inVoyNo", inVoyNo.toUpperCase());
			paramMap.put("outVoyNo", outVoyNo.toUpperCase());
			log.info(" *** getTotalCntrForVessel SQL *****" + sql);
			log.info(" *** getTotalCntrForVessel params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				vvcd = CommonUtil.deNull(rs.getString("VV_CD"));				
			}else {
				throw new BusinessException(ConstantUtil.ErrorMsg_Invalid_Voy);
			}
			
		} catch (BusinessException e) {
			log.info("Exception getVvcdFromVesselDetails : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getVvcdFromVesselDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVvcdFromVesselDetails DAO vvcd:" + vvcd );
		}
		return vvcd;
	}

	@Override
	public Map<String, String> getCntrdetailsMap(String cellData_cntr, String cellData_instructionType, String vvCd)
			throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, String> detailsMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getCntrdetailsMap  DAO  Start cntr Nbr:" + CommonUtility.deNull(cellData_cntr)+ " Instruction Type:" + CommonUtility.deNull(cellData_instructionType));

			sb.append("WITH ranked_rows AS ( ");
			sb.append("SELECT  CNTR_SEQ_NBR, CNTR_NBR, DG_IND, BILL_LADING_NBR, PURP_CD, LAST_MODIFY_DTTM, ");
			sb.append("ROW_NUMBER() OVER (PARTITION BY PURP_CD ORDER BY LAST_MODIFY_DTTM DESC) AS rn ");
			sb.append("FROM TOPS.CNTR WHERE  CNTR_NBR = :cntrNbr AND  ");
			sb.append("CASE WHEN PURP_CD = 'IM' THEN 'LI'  ");
			sb.append("WHEN PURP_CD = 'EX' OR (PURP_CD = 'RS' AND PREV_PURP_CD = 'EX') THEN 'LE' ");
			sb.append("WHEN ((DISC_VV_CD = :vvCd AND LOAD_VV_CD = :vvCd) AND PURP_CD = 'LN') THEN 'T'  ");
			sb.append("WHEN DISC_VV_CD = :vvCd  AND PURP_CD NOT IN ('LN','SH') THEN 'TS' ");
			sb.append("WHEN (LOAD_VV_CD = :vvCd AND NVL(DISC_VV_CD, ' ') <> :vvCd ) THEN 'TE'  ");
			sb.append("ELSE 'T'  ");
			sb.append("END = :cellData_instructionType ");
			sb.append("AND (LOAD_VV_CD = :vvCd OR DISC_VV_CD = :vvCd) ");
			sb.append("AND txn_status <> 'D') ");
			sb.append("SELECT CNTR_SEQ_NBR, CNTR_NBR, DG_IND, BILL_LADING_NBR ");
			sb.append("FROM ranked_rows WHERE rn = 1 ORDER BY LAST_MODIFY_DTTM DESC ");
			sql = sb.toString();
			paramMap.put("cellData_instructionType", cellData_instructionType);
			paramMap.put("cntrNbr", cellData_cntr.toUpperCase());
			paramMap.put("vvCd", vvCd);
			
			log.info(" *** getCntrdetailsMap SQL *****" + sql);
			log.info(" *** getCntrdetailsMap params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				detailsMap.put("CNTR_SEQ_NBR", CommonUtil.deNull(rs.getString("CNTR_SEQ_NBR")));
				detailsMap.put("CNTR_NBR", CommonUtil.deNull(rs.getString("CNTR_NBR")));
				detailsMap.put("BILL_LADING_NBR", CommonUtil.deNull(rs.getString("BILL_LADING_NBR")));
				detailsMap.put("PURP_CD", cellData_instructionType);
				detailsMap.put("DG_IND", CommonUtil.deNull(rs.getString("DG_IND")));
			}
			
		
		} catch (Exception e) {
			log.info("Exception getCntrdetailsMap : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCntrdetailsMap DAO detailsMap:" + detailsMap.toString() );
		}
		return detailsMap;
	}
	
	@Override
	public String getConsigneeShipperCd(String consName) throws BusinessException {
		String consShipCd = ConstantUtil.others.toUpperCase();
		String sql = "";
		StringBuffer sb = new StringBuffer();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getConsigneeShipperCd  DAO  Start Obj " + " consName/ShipName:" + CommonUtility.deNull(consName));
			sb.append("SELECT  co_cd  FROM  tops.company_code WHERE  rec_status='A' ");
			sb.append("AND co_nm LIKE :consName ");
			sql = sb.toString();
			paramMap.put("consName", consName+"%");
			log.info(" *** getConsigneeShipperCd SQL *****" + sql);
			log.info(" *** getConsigneeShipperCd params *****" + paramMap.toString());
			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if(rs.next()) {
				consShipCd = rs.getString("co_cd");
			}
		} catch (Exception e) {
			log.info("Exception getConsigneeShipperCd : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getConsigneeShipperCd DAO Result:" + consShipCd);
		}
		return consShipCd;
	}

	@Override
	// retrieve party name from UEN number if Party Name not given in CUSCAR file
	public String getPartyName(String uenNbr) throws BusinessException {
		String sql = "";
		StringBuilder sb = new StringBuilder();
		Map<String, String> paramMap = new HashMap<>();
		SqlRowSet rs = null;
		String result = "";
		try {
			log.info("START DAO getPartyName. uenNbr: " + uenNbr);
			sb.append(" SELECT a.co_nm partyNm FROM tops.company_code a JOIN tops.customer b ON a.co_cd = b.cust_cd ");
			sb.append(" WHERE b.uen_nbr = :uenNbr ");
			sql = sb.toString();
			log.info("getPartyName SQL: " + sql);
			paramMap.put("uenNbr", uenNbr);
			log.info("paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				result = CommonUtility.deNull(rs.getString("partyNm"));
			}
		} catch (Exception e) {
			log.error("Exception getPartyName: ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END DAO getPartyName. result: " + result);
		}
		return result;
	}

	@Override
	public Long insertCustomCUSCAR(CustomDetailsFileUploadDetails customDetailsFileUploadDetails)
			throws BusinessException {
		StringBuffer sb = new StringBuffer();
		try {

			log.info("Start insertCustomCUSCAR :" + customDetailsFileUploadDetails.toString());

			StringBuilder sbSeq = new StringBuilder();
			sbSeq.append("SELECT GBMS.CUSTOM_DETAILS_UPLOAD_SEQ.nextval AS seqVal FROM DUAL");
			Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(),
					new HashMap<String, String>());
			BigDecimal seqNbr = (BigDecimal) results.get("seqVal");
			log.info("seqNbr " + seqNbr);

			sb.append("INSERT INTO GBMS.CUSTOM_UPLOAD_DETAILS ");
			sb.append(
					"( CUSTOM_UPLOAD_SEQ_NBR, ACTUAL_FILE_NM, VV_CD, TYPE_CD, ASSIGNED_FILE_NM, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM)");
			sb.append("VALUES( :seq_id, :actual_file_name, :vv_cd, 'C', :assigned_file_name, ");
			sb.append(
					":last_modified_user_id, TO_TIMESTAMP(:last_modified_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'))");

			customDetailsFileUploadDetails.setSeq_id(seqNbr.longValue());
			log.info("insertCustomCUSCAR:SQL" + customDetailsFileUploadDetails.toString() + "SQL : "
					+ sb.toString());

			int rows = namedParameterJdbcTemplate.update(sb.toString(),
					new BeanPropertySqlParameterSource(customDetailsFileUploadDetails));
			log.info("rows " + rows);

			sb.setLength(0);
			sb.append("INSERT INTO GBMS.CUSTOM_UPLOAD_DETAILS_TRANS ");
			sb.append("(AUDIT_DTTM, CUSTOM_UPLOAD_SEQ_NBR, VV_CD, TYPE_CD, ACTUAL_FILE_NM, ASSIGNED_FILE_NM, ");
			sb.append("LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM) ");
			sb.append("VALUES(sysdate, :seq_id, :vv_cd, 'C', :actual_file_name, :assigned_file_name, ");
			sb.append(
					":last_modified_user_id, TO_TIMESTAMP(:last_modified_dttm,'dd-mm-yyyy hh24:mi:ss.ff3')) ");
			int rowsTrans = namedParameterJdbcTemplate.update(sb.toString(),
					new BeanPropertySqlParameterSource(customDetailsFileUploadDetails));
			log.info("rowsTrans " + rowsTrans);

			return (seqNbr.longValue());
		} catch (Exception e) {
			log.info("Exception insertCustomCUSCAR : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END insertCustomCUSCAR ");
		}
	}

	@Override
	public List<String> getSelectionList(String type) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		List<String> selectionList = new ArrayList<String>();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START getSelectionList type:" + type);
			if (type.equalsIgnoreCase(ConstantUtil.CUSTOM_PACKAGE_TYPE)) {
				sb.append("SELECT PKG_TYPE_CD FROM GBMS.PKG_TYPES ");
				sb.append("WHERE REC_STATUS='A' ORDER BY PKG_TYPE_CD asc");
			} else if (type.equalsIgnoreCase(ConstantUtil.CUSTOM_IMO_CLASS)) {
				sb.append("SELECT IMDG_CL_CD FROM IMDG_CLASS ");
				sb.append("WHERE REC_STATUS='A' ORDER BY IMDG_CL_CD");
			} else if (type.equalsIgnoreCase(ConstantUtil.CUSTOM_CONSIGNEE)) {
				sb.append("SELECT co_nm FROM  tops.company_code ");
				sb.append("WHERE  rec_status='A' ORDER BY co_nm");
			} 
			log.info("getSelectionList SQL: " + sb.toString());
			selectionList = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, (String.class));
		} catch (Exception e) {
			log.info("Exception getSelectionList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info(" END getSelectionList " + selectionList.size());
		}
		return selectionList;
	}
}
