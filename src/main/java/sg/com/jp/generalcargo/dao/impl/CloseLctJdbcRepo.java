package sg.com.jp.generalcargo.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.CloseLctRepo;
import sg.com.jp.generalcargo.domain.CloseLctValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("closeLctRepo")
public class CloseLctJdbcRepo implements CloseLctRepo {

	private static final Log log = LogFactory.getLog(CloseLctJdbcRepo.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public static final int SEARCH_MODE_BLUR = 0;

	// ejb.sessionBeans.gbms.ops.closeLct-->CloseLctEJB-->closeLct()
	@Override
	public void closeLct(String vv_cds, String userId) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: closeLct  DAO  Start vv_cds:" + vv_cds + " userId:" + userId);

			if (0 != checkStatus(vv_cds)) {
				throw new BusinessException("M70002");
			}

			sb.append(" UPDATE VESSEL_CALL SET GB_CLOSE_LCT_IND = 'Y',GB_CLOSE_LCT_DTTM ");
			sb.append(" = sysdate,GB_CLOSE_LCT_USER_ID = :userId ");
			sb.append(" WHERE VV_CD IN (:vvCds) ");

			paramMap.put("userId", userId);
			paramMap.put("vvCds", vv_cds);
			log.info(" *** closeLct SQL *****" + sb.toString() + " paramMap " + paramMap.toString());
			int count = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			log.info("END: ** closeLct Result ****" + count);
		} catch (BusinessException e) {
			log.error("Exception: closeLct ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.error("Exception: closeLct ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: closeLct ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** closeLct  END *****");
		}
	}

	// ejb.sessionBeans.gbms.ops.closeLct-->CloseLctEJB-->listLct()
	@Override
	public List<CloseLctValueObject> listLct(String vv_cds) throws BusinessException {
		SqlRowSet rs = null;
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		StringBuilder sb = new StringBuilder();
		List<CloseLctValueObject> list = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: listLct  DAO  Start vv_cds:" + vv_cds);

			sb = new StringBuilder();
			sb.append(" SELECT VV_CD,GB_CLOSE_BJ_IND,GB_CLOSE_SHP_IND FROM VESSEL_CALL WHERE ");
			sb.append(" VV_CD IN (:vvCds) ");

			paramMap.put("vvCds", vv_cds);

			log.info(" *** listLct SQL *****" + sb.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			list = new ArrayList<CloseLctValueObject>();
			while (rs.next()) {
				CloseLctValueObject vo = new CloseLctValueObject();
				String vvCd = CommonUtility.deNull(rs.getString("VV_CD"));
				vo.setVv_cd(vvCd);
				vo.setClose_bj(CommonUtility.deNull(rs.getString("GB_CLOSE_BJ_IND")));
				vo.setClose_shipment(CommonUtility.deNull(rs.getString("GB_CLOSE_SHP_IND")));

				sb = new StringBuilder();
				sb.append(" SELECT ua_nbr FROM ua_details ud, esn e WHERE ud.esn_asn_nbr ");
				sb.append(" =e.esn_asn_nbr AND e.out_voy_var_nbr=:vvCd ");

				paramMap.put("vvCd", vvCd);

				log.info(" *** listLct SQL *****" + sb.toString() + " paramMap " + paramMap.toString());
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				List<String> uaNbrs = new ArrayList<String>();
				while (rs1.next()) {
					uaNbrs.add(rs1.getString("UA_NBR"));
				}
				vo.setUaNbrs(uaNbrs);

				sb = new StringBuilder();
				sb.append(" SELECT dn_nbr FROM dn_details dd, gb_edo ge WHERE dd.edo_asn_nbr ");
				sb.append(" =ge.edo_asn_nbr AND ge.var_nbr=:vvCd ");

				paramMap.put("vvCd", vvCd);

				log.info(" *** listLct SQL *****" + sb.toString() + " paramMap " + paramMap.toString());
				rs2 = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				List<String> dnNbrs = new ArrayList<String>();
				while (rs2.next()) {
					dnNbrs.add(rs2.getString("DN_NBR"));
				}
				vo.setDnNbrs(dnNbrs);

				list.add(vo);
			}
			log.info("END: ** listLct Result ****" + list.size());
		} catch (NullPointerException e) {
			log.error("Exception: listLct ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: listLct ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** listLct  END *****");
		}
		return list;
	}

	// ejb.sessionBeans.gbms.ops.closeLct-->CloseLctEJB-->openLct()
	@Override
	public void openLct(String vv_cds, String userId) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: openLct  DAO  Start vv_cds:" + vv_cds + " userId:" + userId);

			sb.append(" UPDATE VESSEL_CALL SET GB_CLOSE_LCT_IND = null,GB_CLOSE_LCT_DTTM ");
			sb.append(" = null,GB_CLOSE_LCT_USER_ID =null ");
			sb.append(" WHERE VV_CD IN (:vvCds) ");

			paramMap.put("vvCds", vv_cds);
			log.info(" *** openLct SQL *****" + sb.toString() + " paramMap " + paramMap.toString());
			int count = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			log.info("END: ** openLct Result ****" + count);
		} catch (NullPointerException e) {
			log.error("Exception: openLct ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: openLct ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** openLct  END *****");
		}
	}

	// ejb.sessionBeans.gbms.ops.closeLct-->CloseLctEJB-->listVessel()
	@Override
	public List<CloseLctValueObject> listVessel() throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		List<CloseLctValueObject> list = new ArrayList<CloseLctValueObject>();
		try {
			log.info("START: listVessel  DAO  Start ");

			// LCT_LIST
			sb.append(" SELECT v.VV_CD,VSL_NM,IN_VOY_NBR,OUT_VOY_NBR,GB_CLOSE_BJ_IND, ");
			sb.append(" GB_CLOSE_SHP_IND,to_char(ATB_DTTM, 'dd/mm/yyyy hh24:mi') as ATB_DTTM, ");
			sb.append(" to_char(ATU_DTTM, 'dd/mm/yyyy hh24:mi') as ATU_DTTM,GB_CLOSE_LCT_IND, ");
			sb.append(" to_char(GB_CLOSE_LCT_DTTM, 'dd/mm/yyyy hh24:mi') as ");
			sb.append(" GB_CLOSE_LCT_DTTM,a.user_name GB_CLOSE_LCT_USER_ID ");
			sb.append(" FROM VESSEL_CALL v ");
			sb.append(" INNER JOIN BERTHING b ON v.VV_CD = b.VV_CD ");
			sb.append(" left join adm_user a on a.user_acct = v.gb_close_lct_user_id ");
			sb.append(" WHERE v.vv_cd not in ( ");
			// case 1: have valid cargoes to be discharged but BJ not closed
			sb.append(" select b.vv_cd from manifest_details a  ");
			sb.append(" left join vessel_call b on a.var_nbr = b.vv_cd ");
			sb.append(" where b.gb_close_bj_ind = 'N' ");
			sb.append(" and b.vv_status_ind <> 'CX' ");
			sb.append(" and b.scheme = 'JCL' ");
			sb.append(" and a.bl_status = 'A' ");
			sb.append("  union ");
			// case 2: have valid cargoes to be loaded but shipment not closed
			sb.append("  select b.vv_cd from esn a ");
			sb.append("  left join vessel_call b on a.out_voy_var_nbr = b.vv_cd ");
			sb.append("  where b.gb_close_shp_ind = 'N' ");
			sb.append("  and b.vv_status_ind <> 'CX' ");
			sb.append("  and b.scheme = 'JCL' ");
			sb.append("  and a.esn_status = 'A' ");
			sb.append("  union ");
			// case 3: have valid GB containers to be discharged but BJ not closed
			sb.append("  select b.vv_cd  ");
			sb.append("  from cntr c ");
			sb.append("  left join vessel_call b on c.disc_vv_cd = b.vv_cd ");
			sb.append("  where c.purp_cd in ('IM', 'TS', 'RE', 'LN') ");
			sb.append("  and c.ct_planned_disc = 'N' ");
			sb.append("  and c.shipment_status <> 'SH' ");
			sb.append("  and c.txn_status <>'D' ");
			sb.append("  and b.gb_close_bj_ind = 'N' ");
			sb.append("  and b.vv_status_ind <> 'CX' ");
			sb.append("  and b.scheme = 'JCL' ");
			sb.append("  union ");
			// case 4: have valid GB containers to be loaded but shipment not closed
			sb.append("  select b.vv_cd  ");
			sb.append("  from cntr c ");
			sb.append("  left join vessel_call b on c.load_vv_cd = b.vv_cd ");
			sb.append("  where c.purp_cd in ('EX', 'RS', 'TS', 'RE', 'LN') ");
			sb.append("  and c.ct_planned_load = 'N' ");
			sb.append("  and c.shipment_status  = 'CO' ");
			sb.append("  and c.txn_status <> 'D' ");
			sb.append("  and b.gb_close_shp_ind = 'N' ");
			sb.append("  and b.vv_status_ind <> 'CX' ");
			sb.append("  and b.scheme = 'JCL' ");
			sb.append("  ) ");
			sb.append("  and SCHEME = 'JCL' ");
			sb.append(" and v.vv_status_ind <> 'CX' and v.gb_close_lct_ind is null ");
			sb.append(" and b.atb_dttm is not null ORDER BY VSL_NM ");
			// Amended by Jade for SL-OPS-20120329-01
			// String sql = LCT_LIST.replaceAll("#filter", "");
			// Update by MC Consulting to retrieve all LCT vessels though LCT is closed
			// already to show Open LCT vessels.
			// String sql = LCT_LIST.replaceAll("#filter", " and v.vv_status_ind <> 'CX' and
			// ( v.gb_close_lct_ind is null or v.gb_close_lct_ind='Y' ) ");

			log.info(" *** listVessel SQL *****" + sb.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				CloseLctValueObject vo = new CloseLctValueObject();
				vo.setVv_cd(CommonUtility.deNull(rs.getString("VV_CD")));
				vo.setVessel_Name(CommonUtility.deNull(rs.getString("VSL_NM")));
				vo.setIn_voyage_No(CommonUtility.deNull(rs.getString("IN_VOY_NBR")));
				vo.setOut_voyage_No(CommonUtility.deNull(rs.getString("OUT_VOY_NBR")));
				vo.setClose_bj(CommonUtility.deNull(rs.getString("GB_CLOSE_BJ_IND")));
				vo.setClose_shipment(CommonUtility.deNull(rs.getString("GB_CLOSE_SHP_IND")));
				vo.setClose_lct(CommonUtility.deNull(rs.getString("GB_CLOSE_LCT_IND")));
				vo.setAtb(CommonUtility.deNull(rs.getString("ATB_DTTM")));
				vo.setAtu(CommonUtility.deNull(rs.getString("ATU_DTTM")));
				vo.setStatus(CommonUtility.deNull(rs.getString("GB_CLOSE_LCT_IND")));
				vo.setClose_at(CommonUtility.deNull(rs.getString("GB_CLOSE_LCT_DTTM")));
				vo.setClose_by(CommonUtility.deNull(rs.getString("GB_CLOSE_LCT_USER_ID")));
				list.add(vo);
			}
			log.info("END: ** listVessel Result ****" + list.size());
		} catch (NullPointerException e) {
			log.error("Exception: listVessel ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: listVessel ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** listVessel  END *****");
		}
		return list;
	}

	// ejb.sessionBeans.gbms.ops.closeLct-->CloseLctEJB-->listLct()
	@Override
	public TableResult listLct(String vslName, String inVoNo, String outVoNo, int searchMode, Criteria criteria)
			throws BusinessException {
		List<CloseLctValueObject> list = new ArrayList<CloseLctValueObject>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		TableResult tableResult = new TableResult();

		try {
			log.info("START: listLct  DAO  Start vslName:" + CommonUtility.deNull(vslName) + " inVoNo:" + CommonUtility.deNull(inVoNo) 
			+ " outVoNo:" + CommonUtility.deNull(outVoNo) + " searchMode:" + searchMode);

			// Amended by Jade for SL-OPS-20120329-01
			// StringBuffer filter = new StringBuffer(" AND VSL_NM ");
			// StringBuffer filter = new StringBuffer(" and v.vv_status_ind <> 'CX' AND
			// VSL_NM ");
			// End of amendments by Jade for SL-OPS-20120329-01
			if (SEARCH_MODE_BLUR == searchMode) {
				// Amended by Jade for CR-CAB-20161006-002
				// filter.append(" LIKE '").append(escape(vslName)).append("' AND IN_VOY_NBR
				// LIKE '").append(
				// escape(inVoNo)).append("' AND OUT_VOY_NBR LIKE
				// '").append(escape(outVoNo)).append("'");
				String andor = " OR ";

				// LCT_LIST
				sb.append(" SELECT v.VV_CD,VSL_NM,IN_VOY_NBR,OUT_VOY_NBR,GB_CLOSE_BJ_IND, ");
				sb.append(" GB_CLOSE_SHP_IND,to_char(ATB_DTTM, 'dd/mm/yyyy hh24:mi') as ATB_DTTM, ");
				sb.append(" to_char(ATU_DTTM, 'dd/mm/yyyy hh24:mi') as ATU_DTTM,GB_CLOSE_LCT_IND, ");
				sb.append(" to_char(GB_CLOSE_LCT_DTTM, 'dd/mm/yyyy hh24:mi') as ");
				sb.append(" GB_CLOSE_LCT_DTTM,a.user_name GB_CLOSE_LCT_USER_ID ");
				sb.append(" FROM VESSEL_CALL v ");
				sb.append(" INNER JOIN BERTHING b ON v.VV_CD = b.VV_CD ");
				sb.append(" left join adm_user a on a.user_acct = v.gb_close_lct_user_id ");
				sb.append(" WHERE v.vv_cd not in ( ");
				// case 1: have valid cargoes to be discharged but BJ not closed
				sb.append(" select b.vv_cd from manifest_details a  ");
				sb.append(" left join vessel_call b on a.var_nbr = b.vv_cd ");
				sb.append(" where b.gb_close_bj_ind = 'N' ");
				sb.append(" and b.vv_status_ind <> 'CX' ");
				sb.append(" and b.scheme = 'JCL' ");
				sb.append(" and a.bl_status = 'A' ");
				sb.append("  union ");
				// case 2: have valid cargoes to be loaded but shipment not closed
				sb.append("  select b.vv_cd from esn a ");
				sb.append("  left join vessel_call b on a.out_voy_var_nbr = b.vv_cd ");
				sb.append("  where b.gb_close_shp_ind = 'N' ");
				sb.append("  and b.vv_status_ind <> 'CX' ");
				sb.append("  and b.scheme = 'JCL' ");
				sb.append("  and a.esn_status = 'A' ");
				sb.append("  union ");
				// case 3: have valid GB containers to be discharged but BJ not closed
				sb.append("  select b.vv_cd  ");
				sb.append("  from cntr c ");
				sb.append("  left join vessel_call b on c.disc_vv_cd = b.vv_cd ");
				sb.append("  where c.purp_cd in ('IM', 'TS', 'RE', 'LN') ");
				sb.append("  and c.ct_planned_disc = 'N' ");
				sb.append("  and c.shipment_status <> 'SH' ");
				sb.append("  and c.txn_status <>'D' ");
				sb.append("  and b.gb_close_bj_ind = 'N' ");
				sb.append("  and b.vv_status_ind <> 'CX' ");
				sb.append("  and b.scheme = 'JCL' ");
				sb.append("  union ");
				// case 4: have valid GB containers to be loaded but shipment not closed
				sb.append("  select b.vv_cd  ");
				sb.append("  from cntr c ");
				sb.append("  left join vessel_call b on c.load_vv_cd = b.vv_cd ");
				sb.append("  where c.purp_cd in ('EX', 'RS', 'TS', 'RE', 'LN') ");
				sb.append("  and c.ct_planned_load = 'N' ");
				sb.append("  and c.shipment_status  = 'CO' ");
				sb.append("  and c.txn_status <> 'D' ");
				sb.append("  and b.gb_close_shp_ind = 'N' ");
				sb.append("  and b.vv_status_ind <> 'CX' ");
				sb.append("  and b.scheme = 'JCL' ");
				sb.append("  ) ");
				sb.append("  and SCHEME = 'JCL' ");

				sb.append(" and v.vv_status_ind <> 'CX' AND UPPER(VSL_NM) ");

				if (inVoNo != null && inVoNo.trim().length() > 0 && outVoNo != null && outVoNo.trim().length() > 0) {
					andor = " AND ";
				}
				sb.append(" LIKE :vslName AND ( UPPER(IN_VOY_NBR) LIKE :inVoNo ").append(andor)
						.append(" UPPER(OUT_VOY_NBR) LIKE :outVoNo )");
				sb.append(" AND GB_CLOSE_LCT_IND = 'Y' ");

				paramMap.put("vslName", escape(vslName).toUpperCase());
				paramMap.put("inVoNo", escape(inVoNo).toUpperCase());
				paramMap.put("outVoNo", escape(outVoNo).toUpperCase());

				// CR-CAB-20161006-002
			} else {

				// LCT_LIST
				sb.append(" SELECT v.VV_CD,VSL_NM,IN_VOY_NBR,OUT_VOY_NBR,GB_CLOSE_BJ_IND, ");
				sb.append(" GB_CLOSE_SHP_IND,to_char(ATB_DTTM, 'dd/mm/yyyy hh24:mi') as ATB_DTTM, ");
				sb.append(" to_char(ATU_DTTM, 'dd/mm/yyyy hh24:mi') as ATU_DTTM,GB_CLOSE_LCT_IND, ");
				sb.append(" to_char(GB_CLOSE_LCT_DTTM, 'dd/mm/yyyy hh24:mi') as ");
				sb.append(" GB_CLOSE_LCT_DTTM,a.user_name GB_CLOSE_LCT_USER_ID ");
				sb.append(" FROM VESSEL_CALL v ");
				sb.append(" INNER JOIN BERTHING b ON v.VV_CD = b.VV_CD ");
				sb.append(" left join adm_user a on a.user_acct = v.gb_close_lct_user_id ");
				sb.append(" WHERE v.vv_cd not in ( ");
				// case 1: have valid cargoes to be discharged but BJ not closed
				sb.append(" select b.vv_cd from manifest_details a  ");
				sb.append(" left join vessel_call b on a.var_nbr = b.vv_cd ");
				sb.append(" where b.gb_close_bj_ind = 'N' ");
				sb.append(" and b.vv_status_ind <> 'CX' ");
				sb.append(" and b.scheme = 'JCL' ");
				sb.append(" and a.bl_status = 'A' ");
				sb.append("  union ");
				// case 2: have valid cargoes to be loaded but shipment not closed
				sb.append("  select b.vv_cd from esn a ");
				sb.append("  left join vessel_call b on a.out_voy_var_nbr = b.vv_cd ");
				sb.append("  where b.gb_close_shp_ind = 'N' ");
				sb.append("  and b.vv_status_ind <> 'CX' ");
				sb.append("  and b.scheme = 'JCL' ");
				sb.append("  and a.esn_status = 'A' ");
				sb.append("  union ");
				// case 3: have valid GB containers to be discharged but BJ not closed
				sb.append("  select b.vv_cd  ");
				sb.append("  from cntr c ");
				sb.append("  left join vessel_call b on c.disc_vv_cd = b.vv_cd ");
				sb.append("  where c.purp_cd in ('IM', 'TS', 'RE', 'LN') ");
				sb.append("  and c.ct_planned_disc = 'N' ");
				sb.append("  and c.shipment_status <> 'SH' ");
				sb.append("  and c.txn_status <>'D' ");
				sb.append("  and b.gb_close_bj_ind = 'N' ");
				sb.append("  and b.vv_status_ind <> 'CX' ");
				sb.append("  and b.scheme = 'JCL' ");
				sb.append("  union ");
				// case 4: have valid GB containers to be loaded but shipment not closed
				sb.append("  select b.vv_cd  ");
				sb.append("  from cntr c ");
				sb.append("  left join vessel_call b on c.load_vv_cd = b.vv_cd ");
				sb.append("  where c.purp_cd in ('EX', 'RS', 'TS', 'RE', 'LN') ");
				sb.append("  and c.ct_planned_load = 'N' ");
				sb.append("  and c.shipment_status  = 'CO' ");
				sb.append("  and c.txn_status <> 'D' ");
				sb.append("  and b.gb_close_shp_ind = 'N' ");
				sb.append("  and b.vv_status_ind <> 'CX' ");
				sb.append("  and b.scheme = 'JCL' ");
				sb.append("  ) ");
				sb.append("  and SCHEME = 'JCL' ");

				sb.append(" and v.vv_status_ind <> 'CX' AND UPPER(VSL_NM) ");

				sb.append(" = :vslName AND IN_VOY_NBR = :inVoNo AND OUT_VOY_NBR = :outVoNo ");

				paramMap.put("vslName", vslName);
				paramMap.put("inVoNo", inVoNo);
				paramMap.put("outVoNo", outVoNo);

			}

			sb.append(" ORDER BY VSL_NM");

			String sql1 = sb.toString();

			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql1 + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
			}
			if (criteria.isPaginated()) {
				sql1 = CommonUtil.getPaginatedSql(sql1, criteria.getStart(), criteria.getLimit());
			}

			log.info(" *** listLct SQL *****" + sb.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);

			while (rs.next()) {
				CloseLctValueObject vo = new CloseLctValueObject();
				vo.setVv_cd(CommonUtility.deNull(rs.getString("VV_CD")));
				vo.setVessel_Name(CommonUtility.deNull(rs.getString("VSL_NM")));
				vo.setIn_voyage_No(CommonUtility.deNull(rs.getString("IN_VOY_NBR")));
				vo.setOut_voyage_No(CommonUtility.deNull(rs.getString("OUT_VOY_NBR")));
				vo.setClose_bj(CommonUtility.deNull(rs.getString("GB_CLOSE_BJ_IND")));
				vo.setClose_shipment(CommonUtility.deNull(rs.getString("GB_CLOSE_SHP_IND")));
				vo.setClose_lct(CommonUtility.deNull(rs.getString("GB_CLOSE_LCT_IND")));
				vo.setAtb(CommonUtility.deNull(rs.getString("ATB_DTTM")));
				vo.setAtu(CommonUtility.deNull(rs.getString("ATU_DTTM")));
				vo.setStatus(CommonUtility.deNull(rs.getString("GB_CLOSE_LCT_IND")));
				vo.setClose_at(CommonUtility.deNull(rs.getString("GB_CLOSE_LCT_DTTM")));
				vo.setClose_by(CommonUtility.deNull(rs.getString("GB_CLOSE_LCT_USER_ID")));

				// Added by Jade for CR-CAB-20161006-002
				if (SEARCH_MODE_BLUR != searchMode) {
					boolean hasDisc = checkDiscLeg(vo.getVv_cd());
					log.debug("hasDisc = " + hasDisc);
					boolean hasLoad = checkLoadLeg(vo.getVv_cd());
					log.debug("hasLoad = " + hasLoad);
					vo.setHasDisCrgCntr(hasDisc);
					vo.setHasLdCrgCntr(hasLoad);
				}
				// CR-CAB-20161006-002

				topsModel.put((Serializable) vo);

			}

			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			log.info("END: ** listLct Result ****" + list.size());
		} catch (NullPointerException e) {
			log.error("Exception: listLct ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: listLct ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** listLct  END *****");
		}
		return tableResult;
	}

	// ejb.sessionBeans.gbms.ops.closeLct-->CloseLctEJB-->checkStatus()
	private int checkStatus(String vv_cds) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		int status = 0;
		try {
			log.info("START: checkStatus  DAO  Start vv_cds:" + CommonUtility.deNull(vv_cds));

			sb.append(" SELECT 1 FROM VESSEL_CALL WHERE GB_CLOSE_LCT_IND = 'Y' AND VV_CD IN (:vvCds) ");

			paramMap.put("vvCds", vv_cds);

			log.info(" *** checkStatus SQL *****" + sb.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				status = -1;
				return status;
			}
		} catch (NullPointerException e) {
			log.error("Exception: checkStatus ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: checkStatus ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** checkStatus Result:" + status);
		}

		return status;
	}

	// ejb.sessionBeans.gbms.ops.closeLct-->CloseLctEJB-->escape()
	private String escape(String str) {
		if (StringUtils.isNotBlank(str)) {
			return "%" + str.replaceAll("\\\\", "\\\\\\\\").replaceAll("%", "\\\\%").replaceAll("_", "\\\\_") + "%";
		}
		return str;
	}

	// ejb.sessionBeans.gbms.ops.closeLct-->CloseLctEJB-->checkDiscLeg()
	private boolean checkDiscLeg(String vvCd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		boolean result = false;
		try {
			log.info("START: checkDiscLeg  DAO  Start vvCd:" + CommonUtility.deNull(vvCd));
			if (vvCd == null) {
				return false;
			}

			sb.append(" select distinct var_nbr vv_cd from manifest_details where var_nbr ");
			sb.append(" = :vvCd and bl_status = 'A' ");
			sb.append(" union ");
			sb.append(" select distinct disc_vv_cd vv_cd from cntr where disc_vv_cd ");
			sb.append(" = :vvCd and purp_cd in ('IM', 'TS', 'RE', 'LN') and ");
			sb.append(" txn_status <>'D' and shipment_status <> 'SH' and ct_planned_disc = 'N' ");

			paramMap.put("vvCd", vvCd);

			log.info(" *** checkDiscLeg SQL *****" + sb.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				result = true;
			}

			log.info("END: ** checkDiscLeg Result ****" + result);
		} catch (NullPointerException e) {
			log.error("Exception: checkDiscLeg ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: checkDiscLeg ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** checkDiscLeg  END *****");
		}

		return result;
	}

	// ejb.sessionBeans.gbms.ops.closeLct-->CloseLctEJB-->checkLoadLeg()
	private boolean checkLoadLeg(String vvCd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		boolean result = false;
		try {
			log.info("START: checkLoadLeg  DAO  Start vvCd:" + CommonUtility.deNull(vvCd));
			if (vvCd == null) {
				return false;
			}

			sb.append(" select distinct out_voy_var_nbr vv_cd from esn where out_voy_var_nbr = :vvCd ");
			sb.append(" and esn_status = 'A' union ");
			sb.append(" select distinct load_vv_cd from cntr where load_vv_cd = :vvCd ");
			sb.append(" and purp_cd in ('EX', 'RS', 'TS', 'RE', 'LN')  and ct_planned_load ");
			sb.append(" = 'N' and shipment_status = 'CO'  and txn_status <> 'D' ");

			paramMap.put("vvCd", vvCd);

			log.info(" *** checkLoadLeg SQL *****" + sb.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			if (rs.next()) {
				result = true;
			}

			log.info("END: ** checkLoadLeg Result ****" + result);
		} catch (NullPointerException e) {
			log.error("Exception: checkLoadLeg ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: checkLoadLeg ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** checkLoadLeg  END *****");
		}
		return result;
	}

}
