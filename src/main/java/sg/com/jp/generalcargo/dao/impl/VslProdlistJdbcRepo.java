package sg.com.jp.generalcargo.dao.impl;

import java.io.Serializable;
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

import sg.com.jp.generalcargo.dao.VslProdlistRepo;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VslProdVO;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("vslProdlistRepo")
public class VslProdlistJdbcRepo implements VslProdlistRepo {

	private static final Log log = LogFactory.getLog(VslProdlistJdbcRepo.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// ejb.sessionBeans.gbms.ops.vesselproductivity -->VslProdlistEjb
	@Override
	public List<String> getDelayOfWork() throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		List<String> al = new ArrayList<String>();
		try {
			log.info("START: getDelayOfWork  DAO  Start Obj ");

			String get_reason_delay_work = " SELECT MISC_TYPE_NM, MISC_TYPE_CD FROM MISC_TYPE_CODE WHERE CAT_CD='WC_DELAY' AND REC_STATUS = 'A' ORDER BY MISC_TYPE_NM ";

			log.info(" getDelayOfWork  DAO  SQL " + get_reason_delay_work.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(get_reason_delay_work.toString(), paramMap);
			String resForDelay = null;
			while (rs.next()) {
				resForDelay = rs.getString("MISC_TYPE_NM");
				al.add(resForDelay);
			}
			log.info("END: *** getDelayOfWork Result *****" + al.toString());
		} catch (NullPointerException e) {
			log.error("Exception getDelayOfWork :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getDelayOfWork :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getDelayOfWork  DAO  END");
		}
		return al;
	}

	// ejb.sessionBeans.gbms.ops.vesselproductivity -->VslProdlistEjb
	@Override
	public List<VslProdVO> getUpdatedVslDtls(String vvcd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		List<VslProdVO> updVslDtlsList = new ArrayList<VslProdVO>();

		try {
			log.info("START: getUpdatedVslDtls  DAO  Start vvcd " + vvcd);

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT ");
			sb.append("	DISTINCT GANG_NBR, ");
			sb.append("	HATCH_NBR, ");
			sb.append("	REMARKS, ");
			sb.append("	( ");
			sb.append("	SELECT ");
			sb.append("		misc_type_nm ");
			sb.append("	FROM ");
			sb.append("		misc_type_code ");
			sb.append("	WHERE ");
			sb.append("		cat_cd = 'WC_DELAY' ");
			sb.append("		AND rec_status = 'A' ");
			sb.append("		AND misc_type_cd = DELAY_RSN_CD) DELAY_RSN_CD ");
			sb.append("FROM ");
			sb.append("	BERTHING ");
			sb.append("WHERE ");
			sb.append("	VV_CD = :vvcd");

			String get_updated_vsl_dtls = sb.toString();

			paramMap.put("vvcd", vvcd);
			log.info(" getUpdatedVslDtls  DAO  SQL " + get_updated_vsl_dtls.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(get_updated_vsl_dtls.toString(), paramMap);
			String gangNbr = null;
			String wrkbleHatches = null;
			String remarks = null;
			String delayRsn = null;

			while (rs.next()) {
				VslProdVO vslProdVO = new VslProdVO();

				gangNbr = CommonUtility.deNull(rs.getString("GANG_NBR"));
				wrkbleHatches = CommonUtility.deNull(rs.getString("HATCH_NBR"));
				remarks = CommonUtility.deNull(rs.getString("REMARKS"));
				delayRsn = CommonUtility.deNull(rs.getString("DELAY_RSN_CD"));
				vslProdVO.setNoOfGangs(gangNbr);
				vslProdVO.setWorkableHatches(wrkbleHatches);
				vslProdVO.setRemarks(remarks);
				vslProdVO.setDelaywork(delayRsn);

				updVslDtlsList.add(vslProdVO);
			}
			log.info("END: *** getUpdatedVslDtls Result *****" + updVslDtlsList.toString());
		} catch (NullPointerException e) {
			log.error("Exception getUpdatedVslDtls :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getUpdatedVslDtls :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getUpdatedVslDtls  DAO  END");
		}
		return updVslDtlsList;
	}

	// ejb.sessionBeans.gbms.ops.vesselproductivity -->VslProdlistEjb
	@Override
	public List<VslProdVO> getVesselSchemeCode() throws BusinessException {

		List<VslProdVO> vslSchemeCodeList = new ArrayList<VslProdVO>();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();

		try {
			log.info("START: getVesselSchemeCode  DAO  Start ");

			String get_all_vessel_scheme_list = " SELECT SCHEME_CD, UPPER(SCHEME_DESC) SCHEME_DESC FROM VESSEL_SCHEME WHERE REC_STATUS='A' ORDER BY SCHEME_DESC ";

			log.info(" getVesselSchemeCode  DAO  SQL " + get_all_vessel_scheme_list.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(get_all_vessel_scheme_list.toString(), paramMap);
			String vslCode = null;
			String schDesc = null;
			while (rs.next()) {
				VslProdVO vslProdVO = new VslProdVO();
				vslCode = CommonUtility.deNull(rs.getString("SCHEME_CD"));
				schDesc = CommonUtility.deNull(rs.getString("SCHEME_DESC"));
				vslProdVO.setSchemeCode(vslCode);
				vslProdVO.setSchemeDesc(schDesc);
				vslSchemeCodeList.add(vslProdVO);
			}
			log.info(" getVesselSchemeCode  DAO  Result" + vslSchemeCodeList.size());
		} catch (NullPointerException e) {
			log.error("Exception getVesselSchemeCode :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getVesselSchemeCode :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselSchemeCode  DAO  END");
		}
		return vslSchemeCodeList;
	}

	// ejb.sessionBeans.gbms.ops.vesselproductivity -->VslProdlistEjb
	@Override
	public void updateVesselInfo(String vvcd, int noOfGangs, int wrkHatches, String delayOfWrk, String remarks)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {
			log.info("START: updateVesselInfo  DAO  Start vvcd " + vvcd + " noOfGangs" + noOfGangs + " wrkHatches"
					+ wrkHatches + " delayOfWrk" + delayOfWrk + " remarks" + remarks);

			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ");
			sb.append("	BERTHING ");
			sb.append("SET ");
			sb.append("	GANG_NBR = :noOfGangs , ");
			sb.append("	HATCH_NBR = :wrkHatches , ");
			sb.append("	REMARKS = :remarks, ");
			sb.append("	DELAY_RSN_CD = ( ");
			sb.append("	SELECT ");
			sb.append("		misc_type_cd ");
			sb.append("	FROM ");
			sb.append("		misc_type_code ");
			sb.append("	WHERE ");
			sb.append("		cat_cd = 'WC_DELAY' ");
			sb.append("		AND rec_status = 'A' ");
			sb.append("		AND misc_type_nm = :delayOfWrk) ");
			sb.append("WHERE ");
			sb.append("	VV_CD = :vvcd");

			String update_rec = sb.toString();

			paramMap.put("noOfGangs", noOfGangs);
			paramMap.put("wrkHatches", wrkHatches);
			paramMap.put("remarks", remarks);
			paramMap.put("delayOfWrk", delayOfWrk);
			paramMap.put("vvcd", vvcd);
			log.info(" updateVesselInfo  DAO  SQL " + update_rec.toString());
			int nre = namedParameterJdbcTemplate.update(update_rec.toString(), paramMap);
			log.debug(" updated Vessels Count === : " + nre);
		} catch (NullPointerException e) {
			log.error("Exception updateVesselInfo :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception updateVesselInfo :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateVesselInfo  DAO  END");
		}
	}

	// ejb.sessionBeans.gbms.ops.vesselproductivity -->VslProdlistEjb
	@Override
	public TableResult getClosedVessels(String selSchmDesc, Criteria criteria) throws BusinessException {

		List<VslProdVO> closedVslSchemeList = new ArrayList<VslProdVO>();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();

		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		TableResult tableResult = new TableResult();

		String vslName = null;
		String schDesc = null;
		String inVoygNo = null;
		String outVoygNo = null;
		String atb = null;
		String atu = null;
		String firstActDTTM = null;
		String firstCargoActDTTM = null;
		String vvcd = null;
		String schemeCode = null;
		
		String sql_cls_vsl_vvcd = "";
		try {
			log.info("START: getClosedVessels  DAO  Start  selSchmDesc" + selSchmDesc);

			sb.append("	SELECT VC.VSL_NM, VC.IN_VOY_NBR, VC.OUT_VOY_NBR, VS.SCHEME_DESC, TO_CHAR(B.ATB_DTTM, 'DD-MM-YYYY HH24:MI') AS ATB_DTTM, ");
			sb.append("	TO_CHAR((SELECT MAX(ATU_DTTM) FROM BERTHING B1 WHERE B1.VV_CD = VC.VV_CD), 'DD-MM-YYYY HH24:MI') AS ATU_DTTM, VC.VV_CD, ");
			sb.append("	VC.SCHEME, NVL((SELECT DECODE(V.GB_CLOSE_BJ_IND, 'N', 'Pending', 'Y', 'Closed', V.GB_CLOSE_BJ_IND) FROM VESSEL_CALL V WHERE ");
			sb.append("	V.VV_CD = VC.VV_CD AND V.VV_CD IN (SELECT M.VAR_NBR FROM MANIFEST_DETAILS M WHERE M.VAR_NBR = VC.VV_CD AND M.BL_STATUS = 'A')), 'N/A') GB_FIRST_ACT_DTTM, ");
			sb.append("	NVL((SELECT DECODE(V.GB_CLOSE_SHP_IND, 'N', 'Pending', 'Y', 'Closed', V.GB_CLOSE_SHP_IND) FROM VESSEL_CALL V WHERE V.VV_CD = VC.VV_CD AND ");
			sb.append("	V.VV_CD IN (SELECT E.OUT_VOY_VAR_NBR FROM ESN E WHERE E.OUT_VOY_VAR_NBR = VC.VV_CD AND E.ESN_STATUS = 'A')), 'N/A') GB_FIRST_CARGO_ACT_DTTM FROM ");
			sb.append("	VESSEL_SCHEME VS, VESSEL_CALL VC, BERTHING B WHERE VC.SCHEME = VS.SCHEME_CD AND VC.VV_CD = B.VV_CD AND B.SHIFT_IND = '1' AND ");
			sb.append("	VC.VV_CD IN ( SELECT VC.VV_CD FROM VESSEL_CALL VC, VESSEL_SCHEME VS WHERE VC.SCHEME = VS.SCHEME_CD AND VC.GB_CLOSE_PROD_IND = 'N' AND terminal = 'GB' AND ");
			sb.append("	VC.GB_CLOSE_SHP_IND = 'Y' AND VC.VV_CD IN ( SELECT E.OUT_VOY_VAR_NBR FROM ESN E WHERE E.OUT_VOY_VAR_NBR IS NOT NULL AND E.ESN_STATUS = 'A' ) UNION ");
			sb.append("	SELECT VC.VV_CD FROM VESSEL_CALL VC, VESSEL_SCHEME VS WHERE VC.SCHEME = VS.SCHEME_CD AND VC.GB_CLOSE_PROD_IND = 'N' AND terminal = 'GB' AND ");
			sb.append("	VC.GB_CLOSE_BJ_IND = 'Y' AND VC.VV_CD IN ( SELECT M.VAR_NBR FROM MANIFEST_DETAILS M WHERE M.BL_STATUS = 'A' ) UNION SELECT VC.VV_CD FROM VESSEL_CALL VC, ");
			sb.append("	VESSEL_SCHEME VS WHERE VC.SCHEME = VS.SCHEME_CD AND VC.GB_CLOSE_PROD_IND = 'N' AND terminal = 'GB' AND VC.GB_CLOSE_VSL_IND = 'Y' AND ");
			sb.append("	VC.VV_CD IN ( SELECT B.OUT_VOY_VAR_NBR FROM BULK_ESN B WHERE B.OUT_VOY_VAR_NBR IS NOT NULL AND B.ESN_STATUS = 'A' ) UNION SELECT VC.VV_CD FROM ");
			sb.append("	VESSEL_CALL VC, VESSEL_SCHEME VS WHERE VC.SCHEME = VS.SCHEME_CD AND VC.GB_CLOSE_PROD_IND = 'N' AND terminal = 'GB' AND VC.GB_CLOSE_VSL_IND = 'Y' AND ");
			sb.append("	VC.VV_CD IN ( SELECT B.VAR_NBR FROM BULK_MANIFEST_DETAILS B WHERE BL_STATUS = 'A' ) UNION SELECT VC.VV_CD FROM VESSEL_CALL VC, VESSEL_SCHEME VS WHERE ");
			sb.append("	VC.SCHEME = VS.SCHEME_CD AND VC.GB_CLOSE_PROD_IND = 'N' AND terminal = 'GB' AND (VC.GB_CLOSE_SHP_IND = 'Y' OR VC.GB_CLOSE_BJ_IND = 'Y') AND ");
			sb.append("	vv_status_ind = 'CL' AND VC.VV_CD IN ( SELECT c.disc_vv_cd FROM cntr c WHERE c.disc_vv_cd IS NOT NULL AND c.txn_STATUS != 'D' ) UNION ");
			sb.append("	SELECT VC.VV_CD FROM VESSEL_CALL VC, VESSEL_SCHEME VS WHERE VC.SCHEME = VS.SCHEME_CD AND VC.GB_CLOSE_PROD_IND = 'N' AND terminal = 'GB' AND ");
			sb.append("	(VC.GB_CLOSE_SHP_IND = 'Y' OR VC.GB_CLOSE_BJ_IND = 'Y') AND vv_status_ind = 'CL' AND VC.VV_CD IN ( SELECT c.load_vv_cd FROM cntr c WHERE ");
			sb.append("	c.load_vv_cd IS NOT NULL AND c.txn_STATUS != 'D' ) ) ");

			paramMap.put("selSchmDesc", selSchmDesc);
			// Added option for sorting by scheme. 11/1/2011.
			if (selSchmDesc == null || "".equals(selSchmDesc)) {
				sql_cls_vsl_vvcd = sb.toString() + " ORDER BY VSL_NM ";
			
			} else if ("ALL - Sort By Vessel".equals(selSchmDesc)) {
				sql_cls_vsl_vvcd = sb.toString() + " ORDER BY VSL_NM ";

			} else if ("ALL - Sort By Scheme".equals(selSchmDesc)) {
				sql_cls_vsl_vvcd = sb.toString() + " ORDER BY SCHEME_DESC, VSL_NM ";

			} else {
				sb = new StringBuffer();
				sb.append("	SELECT VC.VSL_NM, VC.IN_VOY_NBR, VC.OUT_VOY_NBR, VS.SCHEME_DESC, TO_CHAR(B.ATB_DTTM, 'DD-MM-YYYY HH24:MI') AS ATB_DTTM,  ");
				sb.append("	TO_CHAR((SELECT MAX(ATU_DTTM) FROM BERTHING B1 WHERE B1.VV_CD = VC.VV_CD), 'DD-MM-YYYY HH24:MI') AS ATU_DTTM, VC.VV_CD,  ");
				sb.append("	VC.SCHEME, NVL((SELECT DECODE(V.GB_CLOSE_BJ_IND, 'N', 'Pending', 'Y', 'Closed', V.GB_CLOSE_BJ_IND) FROM VESSEL_CALL V WHERE  ");
				sb.append("	V.VV_CD = VC.VV_CD AND V.VV_CD IN (SELECT M.VAR_NBR FROM MANIFEST_DETAILS M WHERE M.VAR_NBR = VC.VV_CD AND M.BL_STATUS = 'A')), 'N/A') GB_FIRST_ACT_DTTM,  ");
				sb.append("	NVL((SELECT DECODE(V.GB_CLOSE_SHP_IND, 'N', 'Pending', 'Y', 'Closed', V.GB_CLOSE_SHP_IND) FROM VESSEL_CALL V WHERE V.VV_CD = VC.VV_CD AND  ");
				sb.append("	V.VV_CD IN (SELECT E.OUT_VOY_VAR_NBR FROM ESN E WHERE E.OUT_VOY_VAR_NBR = VC.VV_CD AND E.ESN_STATUS = 'A')), 'N/A') GB_FIRST_CARGO_ACT_DTTM FROM  ");
				sb.append("	VESSEL_SCHEME VS, VESSEL_CALL VC, BERTHING B WHERE VC.VV_CD IN ( SELECT VC.VV_CD FROM VESSEL_CALL VC WHERE VC.SCHEME = :selSchmDesc AND  ");
				sb.append("	VC.GB_CLOSE_PROD_IND = 'N' AND VC.GB_CLOSE_SHP_IND = 'Y' AND VC.VV_CD IN ( SELECT E.OUT_VOY_VAR_NBR FROM ESN E WHERE E.OUT_VOY_VAR_NBR IS NOT NULL  ");
				sb.append("	AND E.ESN_STATUS = 'A' ) UNION SELECT VC.VV_CD FROM VESSEL_CALL VC WHERE VC.SCHEME = :selSchmDesc AND VC.GB_CLOSE_PROD_IND = 'N'  ");
				sb.append("	AND VC.GB_CLOSE_BJ_IND = 'Y' AND VC.VV_CD IN ( SELECT M.VAR_NBR FROM MANIFEST_DETAILS M WHERE BL_STATUS = 'A' ) UNION  ");
				sb.append("	SELECT VC.VV_CD FROM VESSEL_CALL VC WHERE VC.SCHEME = :selSchmDesc AND VC.GB_CLOSE_PROD_IND = 'N' AND VC.GB_CLOSE_VSL_IND = 'Y' AND  ");
				sb.append("	VC.VV_CD IN ( SELECT B.OUT_VOY_VAR_NBR FROM BULK_ESN B WHERE B.OUT_VOY_VAR_NBR IS NOT NULL AND B.ESN_STATUS = 'A' ) UNION  ");
				sb.append("	SELECT VC.VV_CD FROM VESSEL_CALL VC WHERE VC.SCHEME =:selSchmDesc AND VC.GB_CLOSE_PROD_IND = 'N' AND VC.GB_CLOSE_VSL_IND = 'Y' AND  ");
				sb.append("	VC.VV_CD IN ( SELECT B.VAR_NBR FROM BULK_MANIFEST_DETAILS B WHERE BL_STATUS = 'A' ) UNION SELECT VC.VV_CD FROM VESSEL_CALL VC WHERE  ");
				sb.append("	VC.SCHEME = :selSchmDesc AND VC.GB_CLOSE_PROD_IND = 'N' AND terminal = 'GB' AND (VC.GB_CLOSE_SHP_IND = 'Y' OR VC.GB_CLOSE_BJ_IND = 'Y')  ");
				sb.append("	AND vv_status_ind = 'CL' AND VC.VV_CD IN ( SELECT c.disc_vv_cd FROM cntr c WHERE c.disc_vv_cd IS NOT NULL AND c.txn_STATUS != 'D' ) UNION  ");
				sb.append("	SELECT VC.VV_CD FROM VESSEL_CALL VC WHERE VC.SCHEME = :selSchmDesc AND VC.GB_CLOSE_PROD_IND = 'N' AND terminal = 'GB' AND (VC.GB_CLOSE_SHP_IND = 'Y' OR  ");
				sb.append("	VC.GB_CLOSE_BJ_IND = 'Y') AND vv_status_ind = 'CL' AND VC.VV_CD IN ( SELECT c.load_vv_cd FROM cntr c WHERE c.load_vv_cd IS NOT NULL AND c.txn_STATUS != 'D' ) ) "); 
				sb.append("	AND VC.SCHEME = VS.SCHEME_CD AND VC.VV_CD = B.VV_CD AND B.SHIFT_IND = '1' ORDER BY VC.VSL_NM ");

				sql_cls_vsl_vvcd = sb.toString();
				log.debug("getClosedVessels selSchmDesc value : " + selSchmDesc);

			}
			
			if (criteria.isPaginated()) {
				tableData.setTotal((int) namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql_cls_vsl_vvcd + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
			
				sql_cls_vsl_vvcd = CommonUtil.getPaginatedSql(sql_cls_vsl_vvcd, criteria.getStart(), criteria.getLimit());
			}
			
			log.info(" getClosedVessels  DAO  SQL " + sql_cls_vsl_vvcd.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql_cls_vsl_vvcd.toString(), paramMap);


			while (rs.next()) {
				VslProdVO vslProdVO = new VslProdVO();

				vslName = CommonUtility.deNull(rs.getString("VSL_NM"));
				inVoygNo = CommonUtility.deNull(rs.getString("IN_VOY_NBR"));
				outVoygNo = CommonUtility.deNull(rs.getString("OUT_VOY_NBR"));
				schDesc = CommonUtility.deNull(rs.getString("SCHEME_DESC"));
				atb = CommonUtility.deNull(rs.getString("ATB_DTTM"));
				atu = CommonUtility.deNull(rs.getString("ATU_DTTM"));
				firstActDTTM = CommonUtility.deNull(rs.getString("GB_FIRST_ACT_DTTM"));
				firstCargoActDTTM = CommonUtility.deNull(rs.getString("GB_FIRST_CARGO_ACT_DTTM"));
				vvcd = CommonUtility.deNull(rs.getString("VV_CD"));
				schemeCode = CommonUtility.deNull(rs.getString("SCHEME"));
				vslProdVO.setVesselName(vslName);
				vslProdVO.setInVoygNo(inVoygNo);
				vslProdVO.setOutVoygNo(outVoygNo);
				vslProdVO.setSchemeDesc(schDesc);
				vslProdVO.setAtb(atb);
				vslProdVO.setAtu(atu);
				vslProdVO.setFirstActDTTM(firstActDTTM);
				vslProdVO.setFirstCargoActDTTM(firstCargoActDTTM);
				vslProdVO.setVvcd(vvcd);
				vslProdVO.setSchemeCode(schemeCode);
				closedVslSchemeList.add(vslProdVO);
				log.debug("vvcd : " + vvcd);

			}

			
			topsModel.put((Serializable) closedVslSchemeList);
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
		} catch (NullPointerException e) {
			log.error("Exception getClosedVessels :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getClosedVessels :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getClosedVessels  DAO  END");
		}
		return tableResult;
	}

	// ejb.sessionBeans.gbms.ops.vesselproductivity -->VslProdlistEjb
	@Override
	public String closeVessel(String vvcd) throws BusinessException {

		Map<String, String> paramMap = new HashMap<String, String>();
		String closeStatus = null;

		try {
			log.info("START: closeVessel  DAO  Start  " + " vvcd:" + vvcd);

			closeStatus = checkAllClosed(vvcd);
			if (closeStatus != null && closeStatus.equals("true")) {
				String close_vessel = " UPDATE VESSEL_CALL VC SET GB_CLOSE_PROD_IND = 'Y' , last_modify_user_id ='SYSTEM' , last_modify_dttm = SYSDATE WHERE VC.VV_CD =:vvcd ";
				// Create the Prepared Statement.

				paramMap.put("vvcd", vvcd);
				log.info(" closeVessel  DAO  SQL " + close_vessel.toString());
				int nre = namedParameterJdbcTemplate.update(close_vessel.toString(), paramMap);
				log.debug(" No Of Vessels Closed ==== : " + nre);
				return "true";
			} else {
				log.debug(" Vessel not closed reason=" + closeStatus);
				return closeStatus;
			}
		} catch (BusinessException e) {
			log.error("Exception closeVessel :", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.error("Exception closeVessel :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception closeVessel :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: closeVessel  DAO  END");
		}
	}

	// ejb.sessionBeans.gbms.ops.vesselproductivity -->VslProdlistEjb
	public String checkAllClosed(String vvcd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String closeStatus = null;
		String errorCode = null;

		try {
			log.info("START: checkAllClosed  DAO  Start vvcd " + vvcd);

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT VC.GB_CLOSE_SHP_IND ");
			sb.append(" FROM VESSEL_CALL VC WHERE VC.GB_CLOSE_PROD_IND = 'N' AND terminal='GB' ");
			sb.append("  AND VC.VV_CD = :vvcd ");
			sb.append(
					" AND VC.VV_CD IN ( SELECT E.OUT_VOY_VAR_NBR FROM ESN E WHERE E.OUT_VOY_VAR_NBR IS NOT NULL AND E.ESN_STATUS = 'A' )");
			String get_gb_close_shp_ind = sb.toString();

			paramMap.put("vvcd", vvcd);
			log.info(" checkAllClosed  DAO  SQL " + get_gb_close_shp_ind.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(get_gb_close_shp_ind.toString(), paramMap);
			String GB_CLOSE_SHP_IND = null;
			boolean GBCLOSESHPIND = false;
			if (rs.next()) {
				GB_CLOSE_SHP_IND = rs.getString("GB_CLOSE_SHP_IND");
			}
			log.info(" checkAllClosed  DAO  Result" + GB_CLOSE_SHP_IND);
			log.debug("~~~ In checkAllClosed ~~~  GB_CLOSE_SHP_IND=" + GB_CLOSE_SHP_IND);

			if (GB_CLOSE_SHP_IND != null) {
				if (GB_CLOSE_SHP_IND.equals("N")) {

					errorCode = "M70008";
					throw new BusinessException(errorCode);
				} else
					GBCLOSESHPIND = true;
			} else
				GBCLOSESHPIND = true;

			log.debug("~~~ In checkAllClosed ~~~  GBCLOSESHPIND=" + GBCLOSESHPIND);
			StringBuffer sb1 = new StringBuffer();
			sb1.append("SELECT ");
			sb1.append("	VC.GB_CLOSE_BJ_IND ");
			sb1.append("FROM ");
			sb1.append("	VESSEL_CALL VC ");
			sb1.append("WHERE ");
			sb1.append("	VC.GB_CLOSE_PROD_IND = 'N' ");
			sb1.append("	AND terminal = 'GB' ");
			sb1.append("	AND VC.VV_CD = :vvcd ");
			sb1.append("	AND VC.VV_CD IN ( ");
			sb1.append("	SELECT ");
			sb1.append("		M.VAR_NBR ");
			sb1.append("	FROM ");
			sb1.append("		MANIFEST_DETAILS M ");
			sb1.append("	WHERE ");
			sb1.append("		BL_STATUS = 'A' )");
			// GB_CLOSE_BJ_IND
			String get_gb_close_bj_ind = sb1.toString();

			paramMap.put("vvcd", vvcd);

			log.info(" checkAllClosed  DAO  SQL " + get_gb_close_bj_ind.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(get_gb_close_bj_ind.toString(), paramMap);
			String GB_CLOSE_BJ_IND = null;
			boolean GBCLOSEBJIND = false;
			if (rs.next()) {
				GB_CLOSE_BJ_IND = rs.getString("GB_CLOSE_BJ_IND");
			}

			log.info(" checkAllClosed  DAO  Result" + GB_CLOSE_BJ_IND);
			log.debug("~~~ In checkAllClosed ~~~  GB_CLOSE_BJ_IND=" + GB_CLOSE_BJ_IND);

			if (GB_CLOSE_BJ_IND != null) {
				if (GB_CLOSE_BJ_IND.equals("N")) {

					// errorCode = "M70009";
					throw new BusinessException("M70009");
				} else
					GBCLOSEBJIND = true;
			} else
				GBCLOSEBJIND = true;
			log.debug("~~~ In checkAllClosed ~~~  GBCLOSEBJIND=" + GBCLOSEBJIND);

			String get_gb_close_vsl_ind = " SELECT VC.GB_CLOSE_VSL_IND FROM VESSEL_CALL VC WHERE VC.GB_CLOSE_PROD_IND = 'N'  AND TERMINAL='GB' AND VC.VV_CD = :vvcd ";

			// GB_CLOSE_VSL_IND

			paramMap.put("vvcd", vvcd);
			log.debug("~~~ In checkAllClosed ~~~  GET_GB_CLOSE_VSL_IND=" + get_gb_close_vsl_ind);
			log.info(" checkAllClosed  DAO  SQL " + get_gb_close_vsl_ind.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(get_gb_close_vsl_ind.toString(), paramMap);
			String GB_CLOSE_VSL_IND = null;
			boolean GBCLOSEVSLIND = false;
			if (rs.next()) {
				GB_CLOSE_VSL_IND = rs.getString("GB_CLOSE_VSL_IND");
			}
			log.info(" checkAllClosed  DAO  Result" + GB_CLOSE_VSL_IND);
			log.debug("~~~ In checkAllClosed ~~~  GB_CLOSE_VSL_IND=" + GB_CLOSE_VSL_IND);

			if (GB_CLOSE_VSL_IND != null) {
				if (GB_CLOSE_VSL_IND.equals("N")) {
					// return "M70010";
					// errorCode = "M70010";
					throw new BusinessException("M70010");
				} else
					GBCLOSEVSLIND = true;
			} else
				GBCLOSEVSLIND = true;
			log.debug("~~~ In checkAllClosed ~~~  GBCLOSEVSLIND=" + GBCLOSEVSLIND);

			if (GBCLOSESHPIND && GBCLOSEBJIND && GBCLOSEVSLIND)
				closeStatus = "true";
			log.debug("~~~ In checkAllClosed ~~~  closeStatus=" + closeStatus);

		} catch (BusinessException e) {
			log.error("Exception checkAllClosed :", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.error("Exception checkAllClosed :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception checkAllClosed :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: checkAllClosed  DAO  END");
		}
		return closeStatus;
	}

	// ejb.sessionBeans.gbms.ops.vesselproductivity -->VslProdlistEjb
	@Override
	public String getClosedVslDtls(String vvcd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String gangs = null;
		try {
			log.info("START: getClosedVslDtls  DAO  Start  " + " vvcd:" + vvcd);

			String get_updated_vsl_dtls = " SELECT DISTINCT GANG_NBR, HATCH_NBR, REMARKS, (SELECT misc_type_nm FROM misc_type_code WHERE cat_cd = 'WC_DELAY' AND rec_status = 'A' AND misc_type_cd= DELAY_RSN_CD) DELAY_RSN_CD FROM BERTHING  WHERE VV_CD =:vvcd ";

			log.info(" getClosedVslDtls  DAO  SQL " + get_updated_vsl_dtls.toString());

			paramMap.put("vvcd", vvcd);
			rs = namedParameterJdbcTemplate.queryForRowSet(get_updated_vsl_dtls.toString(), paramMap);
			while (rs.next()) {
				gangs = CommonUtility.deNull(rs.getString("GANG_NBR"));
			}

			log.info(" getClosedVslDtls  DAO  Result" + gangs);
		} catch (NullPointerException e) {
			log.error("Exception getClosedVslDtls :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getClosedVslDtls :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getClosedVslDtls  DAO  END");
		}
		return gangs;
	}

	// ejb.sessionBeans.gbms.ops.vesselproductivity -->VslProdlistEjb

	@Override
	public Map<String, Object> getVesselScheme() throws BusinessException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> codeList = new ArrayList<String>();
		List<String> nameList = new ArrayList<String>();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {

			log.info("START: getVesselScheme  DAO  Start Obj ");

			String sql = "SELECT * FROM VESSEL_SCHEME WHERE (REC_STATUS = 'A') AND (SCHEME_CD <> 'JCT') ORDER BY SCHEME_CD";

			log.info(" *** getVesselScheme SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				String code = CommonUtility.deNull(rs.getString("SCHEME_CD"));
				String name = CommonUtility.deNull(rs.getString("SCHEME_DESC"));
				codeList.add(code);
				nameList.add(code + " - " + name);
			}
			log.info("END: *** getVesselScheme Result *****" + map.toString());
		} catch (NullPointerException e) {
			log.error("Exception: getVesselScheme ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getVesselScheme ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getVesselScheme  DAO  END");
		}

		map.put("codeList", codeList);
		map.put("nameList", nameList);
		return map;
	}

}
