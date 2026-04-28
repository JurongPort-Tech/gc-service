package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.ShutOutCargoRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ShutOutCargoVo;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("ShutOutCargoRepo")
public class ShutOutCargoJdbcRepository implements ShutOutCargoRepository {
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	private static final Log log = LogFactory.getLog(ShutOutCargoJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private final static String  UPDATE_BY_JP_STAFF = "Updated by JP Staff - " ;
	
	@Override
	public List<ShutOutCargoVo> getShutoutCargoMtrgList(String dateFrom, String dateTo, String vslName,
			String outVoyNbr, String esnEdoNbr, String cargoType, String terminal, String dwellDays)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		ShutOutCargoVo vo = null;
		List<ShutOutCargoVo> list = new ArrayList<>();
		int serialNbr = 1;
		int activeNbr = 1;
		int inactiveNbr = 1;
		synchronized (this) {
			try {
				log.info("START:  *** getShutoutCargoMtrgList Dao Start : *** " + " Date From: "
						+ CommonUtility.deNull(dateFrom) + " Date To: " + CommonUtility.deNull(dateTo)
						+ " Vessel Name: " + CommonUtility.deNull(vslName) + " Out Voy Nbr: "
						+ CommonUtility.deNull(outVoyNbr));
				// StringBuffer query = formQuery(dateFrom, dateTo, vslName, outVoyNbr,
				// esnEdoNbr, cargoType);
				// Query for Local Export Cargo
				StringBuffer query = formQueryForEnhancement(dateFrom, dateTo, vslName, outVoyNbr, esnEdoNbr, cargoType,
						terminal, dwellDays);

				if (dateFrom != null && !"".equals(dateFrom.trim())) {
					paramMap.put("dateFrom", dateFrom.trim());
				}
				if (dateTo != null && !"".equals(dateTo.trim())) {
					paramMap.put("dateTo", dateTo.trim());
				}
				if (vslName != null && !"".equals(vslName.trim())) {
					paramMap.put("vslName", vslName.toUpperCase().trim());
				}
				if (outVoyNbr != null && !"".equals(outVoyNbr.trim())) {
					paramMap.put("outVoyNbr", outVoyNbr.toUpperCase().trim());
				}
				if (StringUtils.isNotEmpty(esnEdoNbr)) {
					paramMap.put("esnEdoNbr", esnEdoNbr.trim());
				}
				// pstmt.setString("cargoType", cargoType);
				if (terminal != null && !"".equals(terminal.trim())) {
					paramMap.put("terminal", terminal.toUpperCase().trim());
				}
				if (dwellDays != null && !"".equals(dwellDays.trim())) {
					paramMap.put("dwellDays", dwellDays);
				}
				log.info("Query Local Export: " + query.toString());
				log.info("Local Export paramMap: " + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(query.toString(), paramMap);
				while (rs.next()) {
					// vo = populateVO(rs, con, cargoType, serialNbr++);
					vo = populateVOForEnhancement(rs, cargoType, serialNbr++, true);
					if (cargoType.equalsIgnoreCase("A")) {
						ShutOutCargoVo vo1 = (ShutOutCargoVo) vo;
						
						 //if (!(vo1.getLyingPkgs().equalsIgnoreCase("0"))){
                        if ( (Integer.parseInt(vo1.getBalancePkgsToShutout())>0) && !vo1.getCloseShutOutLoop().equalsIgnoreCase ("Y")) {
                            vo1.setSerialNbr(""+activeNbr++);
                            list.add(vo1);
                        }
                    //}
                        
					} else {
						ShutOutCargoVo vo2 = (ShutOutCargoVo) vo;
						if ( (Integer.parseInt(vo2.getBalancePkgsToShutout())==0) || (Integer.parseInt(vo2.getBalancePkgsToShutout())>0) && vo2.getCloseShutOutLoop().equalsIgnoreCase ("Y")) {
                            vo2.setBalancePkgsToShutout("0"); // set all the inactive record to balance 0
                            vo2.setSerialNbr(""+inactiveNbr++);
                            list.add(vo2);
                        }
					}
				}
				log.info("Local Export **:" + list.size());
				StringBuffer query1 = formQueryForPsatoJP(dateFrom, dateTo, vslName, outVoyNbr, esnEdoNbr, cargoType,
						terminal, dwellDays);

				// stmt = con.createStatement();

				if (dateFrom != null && !"".equals(dateFrom.trim())) {
					paramMap.put("dateFrom", dateFrom.trim());
				}
				if (dateTo != null && !"".equals(dateTo.trim())) {
					paramMap.put("dateTo", dateTo.trim());
				}
				if (vslName != null && !"".equals(vslName.trim())) {
					paramMap.put("vslName", vslName.toUpperCase().trim());
				}
				if (outVoyNbr != null && !"".equals(outVoyNbr.trim())) {
					paramMap.put("outVoyNbr", outVoyNbr.toUpperCase().trim());
				}
				if (StringUtils.isNotEmpty(esnEdoNbr)) {
					paramMap.put("esnEdoNbr", esnEdoNbr.trim());
				}
				// pstmt.setString("cargoType", cargoType);
				if (terminal != null && !"".equals(terminal.trim())) {
					paramMap.put("terminal", terminal.toUpperCase().trim());
				}
				if (dwellDays != null && !"".equals(dwellDays.trim())) {
					paramMap.put("dwellDays", dwellDays);
				}
				log.info("Query PSA JP: " + query1.toString());
				log.info("paramMap: " + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(query1.toString(), paramMap);
				while (rs.next()) {
					// vo = populateVO(rs, con, cargoType, serialNbr++);
					vo = populateVOForEnhancement(rs, cargoType, serialNbr++, true);
					if (cargoType.equalsIgnoreCase("A")) {
						ShutOutCargoVo vo1 = (ShutOutCargoVo) vo;
						//if (!(vo1.getLyingPkgs().equalsIgnoreCase("0"))){
						if ( (Integer.parseInt(vo1.getBalancePkgsToShutout())>0) && !vo1.getCloseShutOutLoop().equalsIgnoreCase ("Y")) {
							    vo1.setSerialNbr(""+activeNbr++);
							    list.add(vo1);
							}
						//}
					} else {
						ShutOutCargoVo vo2 = (ShutOutCargoVo) vo;
						if ( (Integer.parseInt(vo2.getBalancePkgsToShutout())==0) || (Integer.parseInt(vo2.getBalancePkgsToShutout())>0) && vo2.getCloseShutOutLoop().equalsIgnoreCase ("Y")) {
						    vo2.setBalancePkgsToShutout("0"); // set all the inactive record to balance 0
							vo2.setSerialNbr(""+inactiveNbr++);
							list.add(vo2);
						}
					}
				}
				log.info("PSA  **:" + list.size());

			} catch (Exception e) {
				log.info("Exception getShutoutCargoMtrgList : ", e);
				throw new BusinessException("M4201");

			} finally {
				log.info("END: *** getShutoutCargoMtrgList  END *****");
			}
		}
		return list;
	}

	// ejb.sessionBeans.gbms.cargo.generalcargo-->ShutoutCargoEJB-->getUserNameMap()
	// Enhancement - Rafidah
	@Override
	public String getUserNameMap(String loginId) throws BusinessException {
		String userNm = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = "";
		try {
			log.info("START:  *** getUserNameMap Dao Start : *** loginId: " + CommonUtility.deNull(loginId));
			sql = " SELECT USER_NM FROM LOGON_ACCT where LOGIN_ID = :loginId";
			paramMap.put("loginId", loginId);
			log.info("getUserNameMap SQL: " + sql.toString());
			log.info("paramMap: " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				userNm = CommonUtility.deNull(rs.getString("USER_NM"));
			}
			log.info("User Names Populated : " + CommonUtility.deNull(userNm));

		} catch (NullPointerException e) {
			log.info("Exception getUserNameMap : ", e);
			throw new BusinessException("M0010");
		} catch (Exception e) {
			log.info("Exception getUserNameMap : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getUserNameMap  END *****");
		}
		return userNm;
	}

	// ejb.sessionBeans.gbms.cargo.generalcargo-->ShutoutCargoEJB-->formQueryForEnhancement()
	private StringBuffer formQueryForEnhancement(String dateFrom, String dateTo, String vslName, String outVoyNbr,
			String esnEdoNbr, String status, String terminal, String dwellDays) throws BusinessException {
		
		StringBuffer sb = new StringBuffer();
		
		try {
			log.info("START formQueryForEnhancement :: " + " Date From: " + CommonUtility.deNull(dateFrom)
					+ " Date To: " + CommonUtility.deNull(dateTo) + " Vessel Name: " + CommonUtility.deNull(vslName)
					+ " Out Voy Nbr: " + CommonUtility.deNull(outVoyNbr) + " ESN EDO Nbr: "
					+ CommonUtility.deNull(esnEdoNbr) + " Status: " + CommonUtility.deNull(status) + " Terminal: "
					+ CommonUtility.deNull(terminal) + " Dwell Days: " + CommonUtility.deNull(dwellDays));

			sb.setLength(0);
			sb.append(" SELECT DISTINCT  round((SYSDATE-nvl(UA.min_dttm,sysdate)),2) DWELL_DAYS,RECS.* FROM (  ");
			sb.append(" SELECT DISTINCT vc.vsl_nm, vc.terminal, vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND,  shutout_ind, ");
			sb.append("  NVL(TRANSFER.TRANSFER_PKGS,0) transfer_nbr_pkgs, ");
			sb.append(" nvl(shutedo.dn_nbr_pkgs,0) dn_nbr_pkgs, ");
			sb.append("  vc.out_voy_nbr, ");
			sb.append("  vc.vv_cd, ");
			sb.append("  bk.bk_nbr_pkgs, ");
			sb.append("  bk.bk_ref_nbr, ");
			sb.append(
					"  DECODE(esn.trans_type,'A','JP-JP', 'B', 'JP_PSA', 'C', 'PSA_JP','E', 'LOCAL', 'R', 'RESHIP', 'S', 'SHIPSTORE') crg_status, ");
			sb.append("  vc.scheme, ");
			sb.append("  esnd.trucker_nm AS trucker_name, ");
			sb.append("  esnd.esn_asn_nbr, ");
			sb.append("  esnd.nbr_pkgs, ");
			sb.append("  esnd.ua_nbr_pkgs, ");
			sb.append("  nvl(bk.actual_nbr_shipped,0) actual_nbr_shipped, ");
			sb.append("  (nvl(esnd.ua_nbr_pkgs,0) -  nvl(bk.actual_nbr_shipped,0)) lyingPkgs, ");
			sb.append("  (nvl(bk.shutout_qty,0)) shutout_pkgs, ");
			sb.append("  ROUND(((bk.shutout_qty)/esnd.nbr_pkgs)*esnd.esn_wt, 2) total_esn_wt, ");
			sb.append("  ROUND(((bk.shutout_qty)/esnd.nbr_pkgs)*esnd.esn_vol, 2) total_esn_vol, ");
			sb.append("  bk.shutout_delivery_remarks, ");
			sb.append("  nvl(bk.shutout_delivery_pkgs,0) shutout_delivery_pkgs, ");
			sb.append("  TO_CHAR(bk.shutout_update_dttm,'DD/MM/YYYY HH24:MI') shutout_update_dttm, ");
			sb.append("  bk.shutout_update_user_id ");
			sb.append("FROM bk_details bk ");
			sb.append("INNER JOIN esn ");
			sb.append("ON esn.bk_ref_nbr = bk.BK_REF_NBR  and esn.esn_status = 'A' and (nvl(bk.shutout_qty,0) > 0)");

			if (StringUtils.isNotEmpty(esnEdoNbr)) {
				esnEdoNbr = esnEdoNbr.trim();
				esnEdoNbr = StringUtils.replace(esnEdoNbr, ",", "','");
				esnEdoNbr = "'" + esnEdoNbr + "'";
				sb.append(" and esn.esn_asn_nbr in (:esnEdoNbr) ");
				log.info("esnNbr here **" + esnEdoNbr);
			}
			sb.append("INNER JOIN esn_details esnd ");
			sb.append(" ON esn.esn_asn_nbr = esnd.esn_asn_nbr ");
			sb.append((" AND nvl(esnd.ua_nbr_pkgs,0) >0"));
			// sb.append("INNER JOIN tesn_psa_jp psajp ");
			// sb.append("ON esn.esn_asn_nbr = psajp.esn_asn_nbr and nvl(bk.shutout_qty,0) >
			// 0 ");
			sb.append("LEFT JOIN ");
			sb.append("  (SELECT SUM( ( ");
			sb.append("    CASE ");
			sb.append("      WHEN ESN_NEW.TRANS_TYPE ='E' ");
			sb.append("      THEN NVL(ED.UA_NBR_PKGS,0) ");
			sb.append("      WHEN ESN_NEW.TRANS_TYPE ='A' ");
			sb.append("      THEN NVL(TESNJPJP.UA_NBR_PKGS,0) ");
			sb.append("      WHEN ESN_NEW.TRANS_TYPE ='C' ");
			sb.append("      THEN NVL(TESNPSAJP.UA_NBR_PKGS,0) ");
			sb.append("      ELSE 0 ");
			sb.append("    END) ) TRANSFER_PKGS, ");
			sb.append("    ESN_ORG.ESN_ASN_NBR ");
			sb.append("  FROM ESN ESN_NEW, ");
			sb.append("    BK_DETAILS BK_NEW, ");
			sb.append("    ESN ESN_ORG, ");
			sb.append("    BK_DETAILS BK_OLD, ");
			sb.append("    VESSEL_CALL VC_NEW, ");
			sb.append("    ESN_DETAILS ED, ");
			sb.append("    TESN_JP_JP TESNJPJP, ");
			sb.append("    TESN_PSA_JP TESNPSAJP ");
			sb.append("  WHERE ESN_NEW.BK_REF_NBR    = BK_NEW.BK_REF_NBR ");
			sb.append("  AND BK_NEW.OLD_BK_REF       = BK_OLD.BK_REF_NBR ");

			sb.append("  AND ESN_ORG.BK_REF_NBR      = BK_OLD.BK_REF_NBR ");
			sb.append("  AND VC_NEW.VV_CD            = ESN_NEW.OUT_VOY_VAR_NBR ");
			sb.append("  AND BK_OLD.VAR_NBR          = BK_NEW.BK_ORIGINAL_VAR_NBR ");
			sb.append("  AND ESN_NEW.ESN_ASN_NBR     = ED.ESN_ASN_NBR(+) ");
			sb.append("  AND TESNJPJP.ESN_ASN_NBR(+) = ESN_NEW.ESN_ASN_NBR ");
			sb.append("  AND TESNPSAJP.ESN_ASN_NBR(+)= ESN_NEW.ESN_ASN_NBR and ESN_NEW.esn_status = 'A' ");
			sb.append("  GROUP BY ESN_ORG.ESN_ASN_NBR ");
			sb.append("  ) TRANSFER ");
			sb.append("ON TRANSFER.esn_asn_nbr = ESN.ESN_ASN_NBR ");

			sb.append("INNER JOIN vessel_call vc ");
			sb.append("ON vc.vv_cd = esn.out_voy_var_nbr ");
			sb.append("INNER JOIN VESSEL vs ");
			sb.append("ON vs.vsl_nm = vc.vsl_nm ");
			sb.append("LEFT JOIN VESSEL_DECLARANT VD ");
			sb.append("ON VD.vv_cd = vc.vv_cd ");
			sb.append("INNER JOIN berthing b ");
			sb.append("ON b.vv_cd = vc.vv_cd ");
			sb.append("LEFT JOIN bk_details bk_new ");
			sb.append("ON ( bk_new.old_bk_ref = bk.bk_ref_nbr ");
			sb.append("AND (nvl(bk_new.shutout_qty,0)    > 0 )");
			sb.append("AND bk.bk_status       = 'A' ");
			sb.append("AND bk.trans_crg       = 'Y' ");
			sb.append("AND bk_new.bk_status   = 'A' ");
			sb.append("AND bk.var_nbr         = bk_new.bk_original_var_nbr ) ");
			// sb.append("LEFT JOIN gb_edo ");
			// sb.append("ON esn.esn_asn_nbr = gb_edo.esn_asn_nbr ");
			// sb.append("AND shutout_ind = 'Y' ");
			sb.append("LEFT JOIN ");
			sb.append(" (select sum(dn_nbr_pkgs) dn_nbr_pkgs, gb_edo.esn_asn_nbr,gb_edo.shutout_ind from gb_edo, esn esn_new1 ");
			sb.append(" where esn_new1.esn_asn_nbr = gb_edo.esn_asn_nbr and gb_edo.shutout_ind = 'Y' and gb_edo.edo_status = 'A'" );
			sb.append("  and esn_new1.esn_status = 'A' group by gb_edo.esn_asn_nbr,gb_edo.shutout_ind");
			sb.append(" ) SHUTEDO on shutedo.esn_asn_nbr = esn.esn_asn_nbr");
			sb.append(
					" WHERE ((UPPER(VC.TERMINAL) IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR UPPER(VC.TERMINAL) NOT IN 'CT') ");
			sb.append(" AND ( 'JP'                    = :custCode");
			sb.append(" OR esn.ESN_CREATE_CD          = :custCode ");
			sb.append(" OR esnd.HA_CUST_CD            = :custCode ");
			sb.append(" OR vc.VSL_OPR_CD              = :custCode");
			sb.append(" OR esnd.esn_asn_nbr          IN ");
			sb.append("  (SELECT esn_asn_nbr ");
			sb.append("  FROM sub_adp ");
			sb.append("  WHERE status_cd   = 'A' ");
			sb.append("  AND edo_esn_ind  != 1 ");
			sb.append("  AND trucker_co_cd = :custCode ");
			sb.append("  ) )");

			if (sb.length() > 0) {
				if (dateFrom != null && !"".equals(dateFrom.trim())) {
					sb.append(" and b.atb_dttm >= to_date(:dateFrom 00:00','DDMMYYYY HH24:MI') ");
				}
				if (dateTo != null && !"".equals(dateTo.trim())) {
					sb.append(" and b.atb_dttm <= to_date(:dateTo 23:59','DDMMYYYY HH24:MI') ");
				}
				if (vslName != null && !"".equals(vslName.trim())) {
					sb.append(" and (vc.vsl_nm like '%:vslName%' ");
					sb.append(" or vs.vsl_full_nm like '%:vslName%') ");
				}
				if (outVoyNbr != null && !"".equals(outVoyNbr.trim())) {
					sb.append(" and upper(vc.out_voy_nbr) = :outVoyNbr ");
				}

				if (terminal != null && !"".equals(terminal.trim())) {
					sb.append(" and upper(vc.terminal) = :terminal");
				}

				sb.append(" GROUP BY vc.vsl_nm,  ");
				sb.append("  TRANSFER.TRANSFER_PKGS, ");
				sb.append("  shutedo.dn_nbr_pkgs, ");
				sb.append("  vc.out_voy_nbr, ");
				sb.append("  vc.vv_cd, ");
				sb.append("  bk.bk_nbr_pkgs, ");
				sb.append("  bk.bk_ref_nbr, ");
				sb.append(
						"  DECODE(esn.trans_type,'A','JP-JP', 'B', 'JP_PSA', 'C', 'PSA_JP','E', 'LOCAL', 'R', 'RESHIP', 'S', 'SHIPSTORE'), ");
				sb.append("  vc.scheme, vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND,vc.terminal,shutout_ind, ");
				sb.append("  esnd.trucker_nm, ");
				sb.append("  esnd.esn_asn_nbr, ");
				sb.append("  esnd.nbr_pkgs, ");
				sb.append("  esnd.ua_nbr_pkgs, ");
				sb.append("  bk.actual_nbr_shipped, ");

				sb.append("  (esnd.ua_nbr_pkgs - bk.actual_nbr_shipped), ");
				// sb.append(" gb_edo.dn_nbr_pkgs, ");
				sb.append("     bk.shutout_qty,  ");
				sb.append("  ROUND(((bk.shutout_qty)/esnd.nbr_pkgs)*esnd.esn_wt, 2), ");
				sb.append("  ROUND(((bk.shutout_qty)/esnd.nbr_pkgs)*esnd.esn_vol, 2),  ");
				sb.append("  bk.shutout_delivery_remarks, ");
				sb.append("  bk.shutout_delivery_pkgs, ");
				sb.append("  TO_CHAR(bk.shutout_update_dttm,'DD/MM/YYYY HH24:MI'), ");
				sb.append("  bk.shutout_update_user_id");
				// sb.append(" ORDER BY VC.TERMINAL DESC, VC.VSL_NM ");
				sb.append(" ) RECS LEFT OUTER JOIN (select esn_asn_nbr, min(ua_create_dttm) min_dttm from UA_DETAILS where UA_STATUS = 'A' ");
				sb.append(
						" group by esn_asn_nbr) UA ON RECS.ESN_ASN_NBR=UA.ESN_ASN_NBR  where (SYSDATE-nvl(UA.min_dttm,sysdate))  >= :dwell ");

				sb.append("  ");
				sb.append(" GROUP BY VSL_NM, TERMINAL, COMBI_GC_SCHEME, COMBI_GC_OPS_IND, shutout_ind, (SYSDATE-nvl(UA.min_dttm,sysdate)) ,  ");
				sb.append(" TRANSFER_NBR_PKGS, DN_NBR_PKGS, OUT_VOY_NBR, VV_CD, BK_NBR_PKGS, ");
				sb.append(" BK_REF_NBR, CRG_STATUS, SCHEME, TRUCKER_NAME, RECS.ESN_ASN_NBR, ");
				sb.append(" RECS.NBR_PKGS, RECS.UA_NBR_PKGS, ACTUAL_NBR_SHIPPED, LYINGPKGS, ");
				sb.append(" SHUTOUT_PKGS, TOTAL_ESN_WT, TOTAL_ESN_VOL, SHUTOUT_DELIVERY_REMARKS, ");
				sb.append(" SHUTOUT_DELIVERY_PKGS, SHUTOUT_UPDATE_DTTM, SHUTOUT_UPDATE_USER_ID ");
			}
		} catch (Exception e) {
			log.info("Exception formQueryForEnhancement : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("formQueryForEnhancement DAO");
		}
		log.info("return query **" + sb.toString());
		return sb;
	}

	// ejb.sessionBeans.gbms.cargo.generalcargo-->ShutoutCargoEJB-->populateVOForEnhancement()
	private ShutOutCargoVo populateVOForEnhancement(SqlRowSet rs, String status, int serialNbr, boolean isFrontEnd)
			throws BusinessException {

		ShutOutCargoVo vo = new ShutOutCargoVo();
		try {
			log.info("START: DAO populateVOForEnhancement status:" + CommonUtility.deNull(status) + "serialNbr:"
					+ CommonUtility.deNull(String.valueOf(serialNbr)));
			vo.setSerialNbr(CommonUtility.deNull(String.valueOf(serialNbr)));
			vo.setTerminal(CommonUtility.deNull(rs.getString("terminal")));
			vo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
			vo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
			vo.setDeclarePkgs(CommonUtility.deNull(rs.getString("bk_nbr_pkgs")));
			vo.setVesselName(CommonUtility.deNull(rs.getString("vsl_nm")));
			vo.setBkgRefNbr(CommonUtility.deNull(rs.getString("bk_ref_nbr")));
			vo.setOutVoyNbr(CommonUtility.deNull(rs.getString("out_voy_nbr")));
			vo.setVesselCallCode(CommonUtility.deNull(rs.getString("vv_cd")));
			vo.setBkgRefNbr(CommonUtility.deNull(rs.getString("bk_ref_nbr")));
			vo.setEsnAsnNbr(CommonUtility.deNull(rs.getString("esn_asn_nbr")));
			vo.setNbrOfPkgs(CommonUtility.deNull(rs.getString("nbr_pkgs")));
			vo.setReceivedPkgs(CommonUtility.deNull(rs.getString("ua_nbr_pkgs")));
			vo.setActNbrOfPkgsShipped(CommonUtility.deNull(rs.getString("actual_nbr_shipped")));
			vo.setShutoutEsnWeight(formatDecimalString(CommonUtility.deNull(rs.getString("total_esn_wt")), 2));
			vo.setShutoutEsnVolume(formatDecimalString(CommonUtility.deNull(rs.getString("total_esn_vol")), 2));
			vo.setTransferDTTM(CommonUtility.deNull(rs.getString("shutout_update_dttm")));
			vo.setScheme(CommonUtility.deNull(rs.getString("scheme")));
			vo.setCargoStatus(CommonUtility.deNull(rs.getString("crg_status")));
			vo.setTruckerName(CommonUtility.deNull(rs.getString("trucker_name")));
			vo.setShutoutPkgs(CommonUtility.deNull(rs.getString("shutout_pkgs")));
			vo.setShutoutUserId(CommonUtility.deNull(rs.getString("shutout_update_user_id")));
			vo.setShutoutUserName(CommonUtility.deNull(getUserNameMap(rs.getString("shutout_update_user_id"))));
			vo.setShutoutDeliveredPkgs(CommonUtility.deNull(rs.getString("dn_nbr_pkgs"))); // shutout delivered pkgs
			vo.setShutoutDeliveryRemarks(CommonUtility.deNull(rs.getString("shutout_delivery_remarks")));
			vo.setTransferredPkgs(CommonUtility.deNull(rs.getString("transfer_nbr_pkgs")));
			vo.setDeclarePkgs(CommonUtility.deNull(rs.getString("bk_nbr_pkgs")));
			vo.setShutOutInd(CommonUtility.deNull(rs.getString("shutout_ind")));
			vo.setDnNbrPkgs(CommonUtility.deNull(rs.getString("dn_nbr_pkgs")));
			

			long shutout_pkgs = Long.parseLong(String.valueOf(CommonUtility.deNull(rs.getString("shutout_pkgs"))));
			vo.setBalancToLoadPkgs(CommonUtility.deNull(rs.getString("lyingPkgs")));
			long shutout_delivery_pkgs = Long.parseLong(String.valueOf(CommonUtility.deNull(rs.getString("shutout_delivery_pkgs"))));
			long dn_nbr_pkgs = Long.parseLong(String.valueOf(CommonUtility.deNull(rs.getString("dn_nbr_pkgs")))); //shutout delivered pkgs
			//long balancePkgs = shutoutPkgs - deliveredPkgs;
			long balancePkgs = 0;
			String lypkgs = CommonUtility.deNull(rs.getString("lyingPkgs"));

			//balancePkgs = shutoutPkgs - deliveredPkgs-Long.parseLong(vo.getTransferredPkgs());  //CT 20230504 - for the cancelled shutout or no longer shutout (all transferred out or all DN delivery or partially transferred + partially DN delivery)
			//long tmpShutUpdate = 0;
			/*if (vo.getShutoutDeliveryRemarks()!=null && !vo.getShutoutDeliveryRemarks().equalsIgnoreCase("")){
	    		if (!vo.getShutoutDeliveryRemarks().startsWith("Cancel")){ // To update the balance accordingly
	    		//	deliveredPkgs = deliveredPkgs + shutUpdPkgs;
	    			vo.setShutoutDeliveredPkgs(String.valueOf(deliveredPkgs));
	    			if(deliveredPkgs==shutUpdPkgs){
	    			    balancePkgs = shutoutPkgs -deliveredPkgs-Long.parseLong(vo.getTransferredPkgs());  //CT 20230504 - if 0 means all the shutout delivery same as DN packages
	    			}else if(deliveredPkgs!=shutUpdPkgs){
	    			    if( deliveredPkgs !=0 ){
	    			        balancePkgs = shutoutPkgs -shutUpdPkgs-Long.parseLong(vo.getTransferredPkgs());  //CT 20230504 - if 0 means updated by JP staff, jp staff updated by putting the shut out delivery to bypass + transferred cargo
	    			        //balancePkgs = shutoutPkgs -deliveredPkgs-shutUpdPkgs-Long.parseLong(vo.getTransferredPkgs());
	    			    }else{
	    			        vo.setCloseShutOutLoop("Y");
	    			    }

	    			}
	    			 tmpShutUpdate =  shutUpdPkgs;
	    		}
			}*/
			if(dn_nbr_pkgs==0){
			    if(shutout_delivery_pkgs!=0){
			        balancePkgs = shutout_pkgs - Long.parseLong(vo.getTransferredPkgs());
			        if(vo.getShutoutDeliveryRemarks().contains(UPDATE_BY_JP_STAFF)){  //make sure JP staff updated
	                    vo.setCloseShutOutLoop("Y");
	                }
			    }else{
			        balancePkgs = shutout_pkgs - Long.parseLong(vo.getTransferredPkgs()); //make sure all transferred

			    }
			}else{
			    balancePkgs =  shutout_pkgs - dn_nbr_pkgs - Long.parseLong(vo.getTransferredPkgs());  //make sure shut deliver + transferred
			}
			//long tranpkgs = 0;
			//tranpkgs = Long.parseLong(vo.getTransferredPkgs());
			vo.setBalancePkgsToShutout(balancePkgs+"");
			//vo.setLyingPkgs(String.valueOf(Long.parseLong(lypkgs)-deliveredPkgs-tranpkgs-tmpShutUpdate));
			//if (Integer.parseInt(vo.getBalancePkgsToShutout())<0){ //forcely make it to zero
			//    vo.setBalancePkgsToShutout("0")	;
			//}
			//if (Integer.parseInt(vo.getLyingPkgs())<0){
			//	vo.setLyingPkgs("0");
			//}

			vo.setShutOutEdoPkgs(CommonUtility.deNull(rs.getString("dn_nbr_pkgs")));
			if(isFrontEnd){
			    vo.setDwellDays(CommonUtility.deNull(rs.getString("DWELL_DAYS")));
			}else{
			    vo.setDwellDays("");
			}

			log.info("vo:" + vo.toString());
		} catch (BusinessException e) {
			log.info("Exception populateVOForEnhancement : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception populateVOForEnhancement : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO populateVOForEnhancement");
		}

		return vo;
	}

	// ejb.sessionBeans.gbms.cargo.generalcargo-->ShutoutCargoEJB-->formQueryForPsatoJP()
	private StringBuffer formQueryForPsatoJP(String dateFrom, String dateTo, String vslName, String outVoyNbr,
			String esnEdoNbr, String status, String terminal, String dwellDays, String custCode)
			throws BusinessException {

		StringBuffer sb = new StringBuffer();

		try {
			log.info("START: formQueryForPsatoJP dateFrom:" + CommonUtility.deNull(dateFrom) + "dateTo:"
					+ CommonUtility.deNull(dateTo) + "vslName:" + CommonUtility.deNull(vslName) + "outVoyNbr:"
					+ CommonUtility.deNull(outVoyNbr) + "esnEdoNbr" + CommonUtility.deNull(esnEdoNbr) + "status:"
					+ CommonUtility.deNull(status) + "terminal:" + CommonUtility.deNull(terminal) + "dwellDays:"
					+ CommonUtility.deNull(dwellDays) + "custCode:" + CommonUtility.deNull(custCode));

			sb.setLength(0);
			sb.append(" SELECT DISTINCT  round((SYSDATE-nvl(UA.min_dttm,sysdate)),2) DWELL_DAYS,RECS.* FROM (  ");
			sb.append("SELECT DISTINCT vc.vsl_nm, vc.terminal, vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND,shutout_ind, ");
			sb.append("  NVL(TRANSFER.TRANSFER_PKGS,0) transfer_nbr_pkgs, ");
			sb.append(" nvl(shutedo.dn_nbr_pkgs,0) dn_nbr_pkgs, ");
			sb.append("  vc.out_voy_nbr, ");
			sb.append("  vc.vv_cd, ");
			sb.append("  bk.bk_nbr_pkgs, ");
			sb.append("  bk.bk_ref_nbr, ");
			sb.append(
					"  DECODE(esn.trans_type,'A','JP-JP', 'B', 'JP_PSA', 'C', 'PSA_JP','E', 'LOCAL', 'R', 'RESHIP', 'S', 'SHIPSTORE') crg_status, ");
			sb.append("  vc.scheme, ");
			sb.append("  esnd.trucker_nm AS trucker_name, ");
			sb.append("  esnd.esn_asn_nbr, ");
			sb.append("  esnd.nbr_pkgs, ");
			sb.append("  esnd.ua_nbr_pkgs, ");
			sb.append("  nvl(bk.actual_nbr_shipped,0) actual_nbr_shipped, ");
			sb.append("  (nvl(esnd.ua_nbr_pkgs,0) -  nvl(bk.actual_nbr_shipped,0)) lyingPkgs, ");
			sb.append("  (nvl(bk.shutout_qty,0)) shutout_pkgs, ");
			sb.append("  ROUND(((bk.shutout_qty)/esnd.nbr_pkgs)*esnd.gross_wt, 2) total_esn_wt, ");
			sb.append("  ROUND(((bk.shutout_qty)/esnd.nbr_pkgs)*esnd.gross_vol, 2) total_esn_vol, ");
			sb.append("  bk.shutout_delivery_remarks, ");
			sb.append("  nvl(bk.shutout_delivery_pkgs,0) shutout_delivery_pkgs, ");
			sb.append("  TO_CHAR(bk.shutout_update_dttm,'DD/MM/YYYY HH24:MI') shutout_update_dttm, ");
			sb.append("  bk.shutout_update_user_id ");
			sb.append("FROM bk_details bk ");
			sb.append("INNER JOIN esn ");
			sb.append("ON esn.bk_ref_nbr = bk.BK_REF_NBR  and esn.esn_status = 'A' and (nvl(bk.shutout_qty,0) > 0)");

			if (StringUtils.isNotEmpty(esnEdoNbr)) {
				esnEdoNbr = esnEdoNbr.trim();
				esnEdoNbr = StringUtils.replace(esnEdoNbr, ",", "','");
				esnEdoNbr = "'" + esnEdoNbr + "'";
				sb.append(" and esn.esn_asn_nbr=:esnEdoNbr ");
				log.info("esnNbr here **" + esnEdoNbr);
			}
			sb.append("INNER JOIN tesn_psa_jp esnd ");
			sb.append(" ON esn.esn_asn_nbr = esnd.esn_asn_nbr ");
			sb.append((" AND nvl(esnd.ua_nbr_pkgs,0) >0"));
			// sb.append("INNER JOIN tesn_psa_jp psajp ");
			// sb.append("ON esn.esn_asn_nbr = psajp.esn_asn_nbr and nvl(bk.shutout_qty,0) >
			// 0 ");
			sb.append(" LEFT JOIN ");
			sb.append("  (SELECT SUM( ( ");
			sb.append("    CASE ");
			sb.append("      WHEN ESN_NEW.TRANS_TYPE ='E' ");
			sb.append("      THEN NVL(ED.UA_NBR_PKGS,0) ");
			sb.append("      WHEN ESN_NEW.TRANS_TYPE ='A' ");
			sb.append("      THEN NVL(TESNJPJP.UA_NBR_PKGS,0) ");
			sb.append("      WHEN ESN_NEW.TRANS_TYPE ='C' ");
			sb.append("      THEN NVL(TESNPSAJP.UA_NBR_PKGS,0) ");
			sb.append("      ELSE 0 ");
			sb.append("    END) ) TRANSFER_PKGS, ");
			sb.append("    ESN_ORG.ESN_ASN_NBR ");
			sb.append("  FROM ESN ESN_NEW, ");
			sb.append("    BK_DETAILS BK_NEW, ");
			sb.append("    ESN ESN_ORG, ");
			sb.append("    BK_DETAILS BK_OLD, ");
			sb.append("    VESSEL_CALL VC_NEW, ");
			sb.append("    ESN_DETAILS ED, ");
			sb.append("    TESN_JP_JP TESNJPJP, ");
			sb.append("    TESN_PSA_JP TESNPSAJP ");
			sb.append("  WHERE ESN_NEW.BK_REF_NBR    = BK_NEW.BK_REF_NBR ");
			sb.append("  AND BK_NEW.OLD_BK_REF       = BK_OLD.BK_REF_NBR ");

			sb.append("  AND ESN_ORG.BK_REF_NBR      = BK_OLD.BK_REF_NBR ");
			sb.append("  AND VC_NEW.VV_CD            = ESN_NEW.OUT_VOY_VAR_NBR ");
			sb.append("  AND BK_OLD.VAR_NBR          = BK_NEW.BK_ORIGINAL_VAR_NBR ");
			sb.append("  AND ESN_NEW.ESN_ASN_NBR     = ED.ESN_ASN_NBR(+) ");
			sb.append("  AND TESNJPJP.ESN_ASN_NBR(+) = ESN_NEW.ESN_ASN_NBR ");
			sb.append("  AND TESNPSAJP.ESN_ASN_NBR(+)= ESN_NEW.ESN_ASN_NBR and ESN_NEW.esn_status = 'A' ");
			sb.append("  GROUP BY ESN_ORG.ESN_ASN_NBR ");
			sb.append("  ) TRANSFER ");
			sb.append("ON TRANSFER.esn_asn_nbr = ESN.ESN_ASN_NBR ");

			sb.append("INNER JOIN vessel_call vc ");
			sb.append("ON vc.vv_cd = esn.out_voy_var_nbr ");
			sb.append("INNER JOIN VESSEL vs ");
			sb.append("ON vs.vsl_nm = vc.vsl_nm ");
			sb.append("LEFT JOIN VESSEL_DECLARANT VD ");
			sb.append("ON VD.vv_cd = vc.vv_cd ");
			sb.append("INNER JOIN berthing b ");
			sb.append("ON b.vv_cd = vc.vv_cd ");
			sb.append("LEFT JOIN bk_details bk_new ");
			sb.append("ON ( bk_new.old_bk_ref = bk.bk_ref_nbr ");
			sb.append("AND (nvl(bk_new.shutout_qty,0)    > 0 )");
			sb.append("AND bk.bk_status       = 'A' ");
			sb.append("AND bk.trans_crg       = 'Y' ");
			sb.append("AND bk_new.bk_status   = 'A' ");
			sb.append("AND bk.var_nbr         = bk_new.bk_original_var_nbr ) ");
			sb.append(" LEFT JOIN ");
			sb.append(" (select sum(dn_nbr_pkgs) dn_nbr_pkgs, gb_edo.esn_asn_nbr, gb_edo.shutout_ind  from gb_edo, esn esn_new1 ");
			sb.append(" where esn_new1.esn_asn_nbr = gb_edo.esn_asn_nbr and gb_edo.shutout_ind = 'Y' and gb_edo.edo_status = 'A'" );
			sb.append("  and esn_new1.esn_status = 'A' group by gb_edo.esn_asn_nbr, gb_edo.shutout_ind ");
			sb.append(" ) SHUTEDO on shutedo.esn_asn_nbr = esn.esn_asn_nbr");
			// sb.append("LEFT JOIN gb_edo ");
			// sb.append("ON esn.esn_asn_nbr = gb_edo.esn_asn_nbr ");
			// sb.append("AND shutout_ind = 'Y' ");
			sb.append(
					" WHERE ((UPPER(VC.TERMINAL) IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR UPPER(VC.TERMINAL) NOT IN 'CT') ");
			sb.append(" AND ( 'JP'                    = :custCode ");
			sb.append(" OR esn.ESN_CREATE_CD          = :custCode ");
			sb.append(" OR esnd.trucker_co_cd         =:custCode ");
			sb.append("OR vc.VSL_OPR_CD              = :custCode ");
			sb.append("OR esnd.esn_asn_nbr          IN ");
			sb.append("  (SELECT esn_asn_nbr ");
			sb.append("  FROM sub_adp ");
			sb.append("  WHERE status_cd   = 'A' ");
			sb.append("  AND edo_esn_ind  != 1 ");
			sb.append("  AND trucker_co_cd = :custCode");
			sb.append("  ) )");

			if (sb.length() > 0) {
				if (dateFrom != null && !"".equals(dateFrom.trim())) {
					sb.append(" and b.atb_dttm >= to_date(:dateFrom ,'DDMMYYYY HH24:MI') ");
				}
				if (dateTo != null && !"".equals(dateTo.trim())) {
					sb.append(" and b.atb_dttm <= to_date(:dateTo,'DDMMYYYY HH24:MI') ");
				}
				if (vslName != null && !"".equals(vslName.trim())) {
					sb.append(" and (vc.vsl_nm like :vslName");
					sb.append(" or vs.vsl_full_nm like :vslName) ");
				}
				if (outVoyNbr != null && !"".equals(outVoyNbr.trim())) {
					sb.append(" and upper(vc.out_voy_nbr) = :outVoyNbr ");
				}
				if (terminal != null && !"".equals(terminal.trim())) {
					sb.append(" and upper(vc.terminal) = :terminal");
				}
				
				sb.append(" GROUP BY  vc.vsl_nm, ");
				sb.append("  TRANSFER.TRANSFER_PKGS, ");
				sb.append("  shutedo.dn_nbr_pkgs, ");
				sb.append("  vc.out_voy_nbr, ");
				sb.append("  vc.vv_cd, ");
				sb.append("  bk.bk_nbr_pkgs, ");
				sb.append("  bk.bk_ref_nbr, ");
				sb.append(
						"  DECODE(esn.trans_type,'A','JP-JP', 'B', 'JP_PSA', 'C', 'PSA_JP','E', 'LOCAL', 'R', 'RESHIP', 'S', 'SHIPSTORE'), ");
				sb.append("  vc.scheme,vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND,vc.terminal, shutout_ind,");
				sb.append("  esnd.trucker_nm, ");
				sb.append("  esnd.esn_asn_nbr, ");
				sb.append("  esnd.nbr_pkgs, ");
				sb.append("  esnd.ua_nbr_pkgs, ");
				sb.append("  bk.actual_nbr_shipped, ");

				sb.append("  (esnd.ua_nbr_pkgs - bk.actual_nbr_shipped ), ");
				// sb.append(" gb_edo.dn_nbr_pkgs, ");
				sb.append("     bk.shutout_qty,  ");
				sb.append("  ROUND(((bk.shutout_qty)/esnd.nbr_pkgs)*esnd.gross_wt, 2), ");
				sb.append("  ROUND(((bk.shutout_qty)/esnd.nbr_pkgs)*esnd.gross_vol, 2),  ");
				sb.append("  bk.shutout_delivery_remarks, ");
				sb.append("  bk.shutout_delivery_pkgs, ");
				sb.append("  TO_CHAR(bk.shutout_update_dttm,'DD/MM/YYYY HH24:MI'), ");
				sb.append("  bk.shutout_update_user_id");
				// sb.append(" ORDER BY VC.TERMINAL DESC, VC.VSL_NM ");
				sb.append("  ) RECS LEFT OUTER JOIN (select esn_asn_nbr, min(ua_create_dttm) min_dttm from UA_DETAILS where UA_STATUS = 'A'  ");
				sb.append(
						" group by esn_asn_nbr) UA ON RECS.ESN_ASN_NBR=UA.ESN_ASN_NBR  where (SYSDATE-nvl(UA.min_dttm,sysdate))  >= ");
				sb.append(":dwellDays");
				sb.append("  ");
				sb.append(" GROUP BY VSL_NM, TERMINAL, COMBI_GC_SCHEME, COMBI_GC_OPS_IND,shutout_ind, (SYSDATE-nvl(UA.min_dttm,sysdate)) , ");
				sb.append(" TRANSFER_NBR_PKGS, DN_NBR_PKGS, OUT_VOY_NBR, VV_CD, BK_NBR_PKGS, ");
				sb.append(" BK_REF_NBR, CRG_STATUS, SCHEME, TRUCKER_NAME, RECS.ESN_ASN_NBR, ");
				sb.append(" RECS.NBR_PKGS, RECS.UA_NBR_PKGS, ACTUAL_NBR_SHIPPED, LYINGPKGS, ");
				sb.append(" SHUTOUT_PKGS, TOTAL_ESN_WT, TOTAL_ESN_VOL, SHUTOUT_DELIVERY_REMARKS, ");
				sb.append(" SHUTOUT_DELIVERY_PKGS, SHUTOUT_UPDATE_DTTM, SHUTOUT_UPDATE_USER_ID ");
			}
			// LogManager.instance.logInfo("return query **" + sb.toString());
		} catch (Exception e) {
			log.info("Exception END:formQueryForPsatoJP : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:formQueryForPsatoJP DAO");
		}
		return sb;

	}

	// ejb.sessionBeans.gbms.cargo.generalcargo-->ShutoutCargoEJB-->formatDecimalString()
	private String formatDecimalString(String s, int decimalLength) throws Exception {
		log.info("START: formatDecimalString "+" s:"+CommonUtility.deNull(s) +" decimalLength:"+ decimalLength );

		String newString = s;
		if (s != null && s.indexOf(".") == -1) {
			return s + ".00";
		} else if (s != null && s.indexOf(".") != -1) {
			int zeroIndex = s.indexOf(".");
			if (s.indexOf(".") == 0) {
				newString = "0" + newString;
			}

			int len = s.substring(zeroIndex + 1).length();
			for (int i = len; i < decimalLength; i++) {
				newString += "0";
			}
			log.info("END: *** formatDecimalString Result *****" + CommonUtility.deNull(newString));
			return newString;
		}
		log.info("END: *** formatDecimalString Result *****" + CommonUtility.deNull(s));
		return s;
	}

	// ejb.sessionBeans.gbms.cargo.generalcargo-->ShutoutCargoEJB-->formQueryForPsatoJP()
	private StringBuffer formQueryForPsatoJP(String dateFrom, String dateTo, String vslName, String outVoyNbr,
			String esnEdoNbr, String status, String terminal, String dwellDays) throws BusinessException {

		// String custCode =
		// CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: formQueryForPsatoJP dateFrom:" + CommonUtility.deNull(dateFrom) + "dateTo:"
					+ CommonUtility.deNull(dateTo) + "vslName:" + CommonUtility.deNull(vslName) + "outVoyNbr:"
					+ CommonUtility.deNull(outVoyNbr) + "esnEdoNbr" + CommonUtility.deNull(esnEdoNbr) + "status:"
					+ CommonUtility.deNull(status) + "terminal:" + CommonUtility.deNull(terminal) + "dwellDays:"
					+ CommonUtility.deNull(dwellDays));
			sb.setLength(0);
			sb.append(" SELECT DISTINCT  round((SYSDATE-nvl(UA.min_dttm,sysdate)),2) DWELL_DAYS,RECS.* FROM (  ");
			sb.append("SELECT DISTINCT vc.vsl_nm, vc.terminal, vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND,shutout_ind, ");
			sb.append("  NVL(TRANSFER.TRANSFER_PKGS,0) transfer_nbr_pkgs, ");
			sb.append(" nvl(shutedo.dn_nbr_pkgs,0) dn_nbr_pkgs, ");
			sb.append("  vc.out_voy_nbr, ");
			sb.append("  vc.vv_cd, ");
			sb.append("  bk.bk_nbr_pkgs, ");
			sb.append("  bk.bk_ref_nbr, ");
			sb.append(
					"  DECODE(esn.trans_type,'A','JP-JP', 'B', 'JP_PSA', 'C', 'PSA_JP','E', 'LOCAL', 'R', 'RESHIP', 'S', 'SHIPSTORE') crg_status, ");
			sb.append("  vc.scheme, ");
			sb.append("  esnd.trucker_nm AS trucker_name, ");
			sb.append("  esnd.esn_asn_nbr, ");
			sb.append("  esnd.nbr_pkgs, ");
			sb.append("  esnd.ua_nbr_pkgs, ");
			sb.append("  nvl(bk.actual_nbr_shipped,0) actual_nbr_shipped, ");
			sb.append("  (nvl(esnd.ua_nbr_pkgs,0) -  nvl(bk.actual_nbr_shipped,0)) lyingPkgs, ");
			sb.append("  (nvl(bk.shutout_qty,0)) shutout_pkgs, ");
			sb.append("  ROUND(((bk.shutout_qty)/esnd.nbr_pkgs)*esnd.gross_wt, 2) total_esn_wt, ");
			sb.append("  ROUND(((bk.shutout_qty)/esnd.nbr_pkgs)*esnd.gross_vol, 2) total_esn_vol, ");
			sb.append("  bk.shutout_delivery_remarks, ");
			sb.append("  nvl(bk.shutout_delivery_pkgs,0) shutout_delivery_pkgs, ");
			sb.append("  TO_CHAR(bk.shutout_update_dttm,'DD/MM/YYYY HH24:MI') shutout_update_dttm, ");
			sb.append("  bk.shutout_update_user_id ");
			sb.append("FROM bk_details bk ");
			sb.append("INNER JOIN esn ");
			sb.append("ON esn.bk_ref_nbr = bk.BK_REF_NBR  and esn.esn_status = 'A' and (nvl(bk.shutout_qty,0) > 0)");

			if (StringUtils.isNotEmpty(esnEdoNbr)) {
				esnEdoNbr = esnEdoNbr.trim();
				esnEdoNbr = StringUtils.replace(esnEdoNbr, ",", "','");
				esnEdoNbr = "'" + esnEdoNbr + "'";
				sb.append(" and esn.esn_asn_nbr in (:esnEdoNbr) ");
				log.info("esnNbr here **" + esnEdoNbr);
			}
			sb.append("INNER JOIN tesn_psa_jp esnd ");
			sb.append(" ON esn.esn_asn_nbr = esnd.esn_asn_nbr ");
			sb.append((" AND nvl(esnd.ua_nbr_pkgs,0) >0"));
			// sb.append("INNER JOIN tesn_psa_jp psajp ");
			// sb.append("ON esn.esn_asn_nbr = psajp.esn_asn_nbr and nvl(bk.shutout_qty,0) >
			// 0 ");
			sb.append(" LEFT JOIN ");
			sb.append("  (SELECT SUM( ( ");
			sb.append("    CASE ");
			sb.append("      WHEN ESN_NEW.TRANS_TYPE ='E' ");
			sb.append("      THEN NVL(ED.UA_NBR_PKGS,0) ");
			sb.append("      WHEN ESN_NEW.TRANS_TYPE ='A' ");
			sb.append("      THEN NVL(TESNJPJP.UA_NBR_PKGS,0) ");
			sb.append("      WHEN ESN_NEW.TRANS_TYPE ='C' ");
			sb.append("      THEN NVL(TESNPSAJP.UA_NBR_PKGS,0) ");
			sb.append("      ELSE 0 ");
			sb.append("    END) ) TRANSFER_PKGS, ");
			sb.append("    ESN_ORG.ESN_ASN_NBR ");
			sb.append("  FROM ESN ESN_NEW, ");
			sb.append("    BK_DETAILS BK_NEW, ");
			sb.append("    ESN ESN_ORG, ");
			sb.append("    BK_DETAILS BK_OLD, ");
			sb.append("    VESSEL_CALL VC_NEW, ");
			sb.append("    ESN_DETAILS ED, ");
			sb.append("    TESN_JP_JP TESNJPJP, ");
			sb.append("    TESN_PSA_JP TESNPSAJP ");
			sb.append("  WHERE ESN_NEW.BK_REF_NBR    = BK_NEW.BK_REF_NBR ");
			sb.append("  AND BK_NEW.OLD_BK_REF       = BK_OLD.BK_REF_NBR ");

			sb.append("  AND ESN_ORG.BK_REF_NBR      = BK_OLD.BK_REF_NBR ");
			sb.append("  AND VC_NEW.VV_CD            = ESN_NEW.OUT_VOY_VAR_NBR ");
			sb.append("  AND BK_OLD.VAR_NBR          = BK_NEW.BK_ORIGINAL_VAR_NBR ");
			sb.append("  AND ESN_NEW.ESN_ASN_NBR     = ED.ESN_ASN_NBR(+) ");
			sb.append("  AND TESNJPJP.ESN_ASN_NBR(+) = ESN_NEW.ESN_ASN_NBR ");
			sb.append("  AND TESNPSAJP.ESN_ASN_NBR(+)= ESN_NEW.ESN_ASN_NBR and ESN_NEW.esn_status = 'A' ");
			sb.append("  GROUP BY ESN_ORG.ESN_ASN_NBR ");
			sb.append("  ) TRANSFER ");
			sb.append("ON TRANSFER.esn_asn_nbr = ESN.ESN_ASN_NBR ");

			sb.append("INNER JOIN vessel_call vc ");
			sb.append("ON vc.vv_cd = esn.out_voy_var_nbr ");
			sb.append("INNER JOIN VESSEL vs ");
			sb.append("ON vs.vsl_nm = vc.vsl_nm ");
			sb.append("LEFT JOIN VESSEL_DECLARANT VD ");
			sb.append("ON VD.vv_cd = vc.vv_cd ");
			sb.append("INNER JOIN berthing b ");
			sb.append("ON b.vv_cd = vc.vv_cd ");
			sb.append("LEFT JOIN bk_details bk_new ");
			sb.append("ON ( bk_new.old_bk_ref = bk.bk_ref_nbr ");
			sb.append("AND (nvl(bk_new.shutout_qty,0)    > 0 )");
			sb.append("AND bk.bk_status       = 'A' ");
			sb.append("AND bk.trans_crg       = 'Y' ");
			sb.append("AND bk_new.bk_status   = 'A' ");
			sb.append("AND bk.var_nbr         = bk_new.bk_original_var_nbr ) ");
			sb.append(" LEFT JOIN ");
			sb.append(" (select sum(dn_nbr_pkgs) dn_nbr_pkgs, gb_edo.esn_asn_nbr, gb_edo.shutout_ind  from gb_edo, esn esn_new1 ");
			sb.append(" where esn_new1.esn_asn_nbr = gb_edo.esn_asn_nbr and gb_edo.shutout_ind = 'Y' and gb_edo.edo_status = 'A'" );
			sb.append("  and esn_new1.esn_status = 'A' group by gb_edo.esn_asn_nbr, gb_edo.shutout_ind ");
			sb.append(" ) SHUTEDO on shutedo.esn_asn_nbr = esn.esn_asn_nbr");
			// sb.append("LEFT JOIN gb_edo ");
			// sb.append("ON esn.esn_asn_nbr = gb_edo.esn_asn_nbr ");
			// sb.append("AND shutout_ind = 'Y' ");
			sb.append(
					" WHERE ((UPPER(VC.TERMINAL) IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR UPPER(VC.TERMINAL) NOT IN 'CT') ");
			sb.append(" AND ( 'JP'                    = :custCode ");
			sb.append(" OR esn.ESN_CREATE_CD          = :custCode ");
			sb.append(" OR esnd.trucker_co_cd         = :custCode ");
			sb.append("OR vc.VSL_OPR_CD              = :custCode ");
			sb.append("OR esnd.esn_asn_nbr          IN ");
			sb.append("  (SELECT esn_asn_nbr ");
			sb.append("  FROM sub_adp ");
			sb.append("  WHERE status_cd   = 'A' ");
			sb.append("  AND edo_esn_ind  != 1 ");
			sb.append("  AND trucker_co_cd = :custCode ");
			sb.append("  ) )");

			if (sb.length() > 0) {
				if (dateFrom != null && !"".equals(dateFrom.trim())) {
					sb.append(" and b.atb_dttm >= to_date(:dateFrom 00:00','DDMMYYYY HH24:MI') ");
				}
				if (dateTo != null && !"".equals(dateTo.trim())) {
					sb.append(" and b.atb_dttm <= to_date(:dateTo 23:59','DDMMYYYY HH24:MI') ");
				}
				if (vslName != null && !"".equals(vslName.trim())) {
					sb.append(" and (vc.vsl_nm like '%:vslName%' ");
					sb.append(" or vs.vsl_full_nm like '%:vslName%') ");
				}
				if (outVoyNbr != null && !"".equals(outVoyNbr.trim())) {
					sb.append(" and upper(vc.out_voy_nbr) = :outVoyNbr ");
				}
				if (terminal != null && !"".equals(terminal.trim())) {
					sb.append(" and upper(vc.terminal) = :terminal");
				}

				sb.append(" GROUP BY  vc.vsl_nm, ");
				sb.append("  TRANSFER.TRANSFER_PKGS, ");
				sb.append("  shutedo.dn_nbr_pkgs, ");
				sb.append("  vc.out_voy_nbr, ");
				sb.append("  vc.vv_cd, ");
				sb.append("  bk.bk_nbr_pkgs, ");
				sb.append("  bk.bk_ref_nbr, ");
				sb.append(
						"  DECODE(esn.trans_type,'A','JP-JP', 'B', 'JP_PSA', 'C', 'PSA_JP','E', 'LOCAL', 'R', 'RESHIP', 'S', 'SHIPSTORE'), ");
				sb.append("  vc.scheme,vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND,vc.terminal, shutout_ind,");
				sb.append("  esnd.trucker_nm, ");
				sb.append("  esnd.esn_asn_nbr, ");
				sb.append("  esnd.nbr_pkgs, ");
				sb.append("  esnd.ua_nbr_pkgs, ");
				sb.append("  bk.actual_nbr_shipped, ");

				sb.append("  (esnd.ua_nbr_pkgs - bk.actual_nbr_shipped ), ");
				// sb.append(" gb_edo.dn_nbr_pkgs, ");
				sb.append("     bk.shutout_qty,  ");
				sb.append("  ROUND(((bk.shutout_qty)/esnd.nbr_pkgs)*esnd.gross_wt, 2), ");
				sb.append("  ROUND(((bk.shutout_qty)/esnd.nbr_pkgs)*esnd.gross_vol, 2),  ");
				sb.append("  bk.shutout_delivery_remarks, ");
				sb.append("  bk.shutout_delivery_pkgs, ");
				sb.append("  TO_CHAR(bk.shutout_update_dttm,'DD/MM/YYYY HH24:MI'), ");
				sb.append("  bk.shutout_update_user_id");
				// sb.append(" ORDER BY VC.TERMINAL DESC, VC.VSL_NM ");
				sb.append(" ) RECS LEFT OUTER JOIN (select esn_asn_nbr, min(ua_create_dttm) min_dttm from UA_DETAILS where UA_STATUS = 'A'  ");
				sb.append(" group by esn_asn_nbr) UA ON RECS.ESN_ASN_NBR=UA.ESN_ASN_NBR  where (SYSDATE-nvl(UA.min_dttm,sysdate))  >= ");
				sb.append(":dwell");
				sb.append("  ");
				sb.append(" GROUP BY VSL_NM, TERMINAL, COMBI_GC_SCHEME, COMBI_GC_OPS_IND,shutout_ind, (SYSDATE-nvl(UA.min_dttm,sysdate)) ,  ");
				sb.append(" TRANSFER_NBR_PKGS, DN_NBR_PKGS, OUT_VOY_NBR, VV_CD, BK_NBR_PKGS, ");
				sb.append(" BK_REF_NBR, CRG_STATUS, SCHEME, TRUCKER_NAME, RECS.ESN_ASN_NBR, ");
				sb.append(" RECS.NBR_PKGS, RECS.UA_NBR_PKGS, ACTUAL_NBR_SHIPPED, LYINGPKGS, ");
				sb.append(" SHUTOUT_PKGS, TOTAL_ESN_WT, TOTAL_ESN_VOL, SHUTOUT_DELIVERY_REMARKS, ");
				sb.append(" SHUTOUT_DELIVERY_PKGS, SHUTOUT_UPDATE_DTTM, SHUTOUT_UPDATE_USER_ID ");
			}
		} catch (Exception e) {
			log.info("Exception formQueryForPsatoJP : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("formQueryForPsatoJP DAO");
		}
		
		log.info("return query **" + sb.toString());
		return sb;

	}

	// ejb.sessionBeans.gbms.cargo.generalcargo-->ShutoutCargoEJB-->updateDeliveryStatus()
	@Override
	public int updateDeliveryStatus(String bkgRefNbr, String deliveredPackages, String deliveryRemarks, String userId,
			String userName, String dateTime, String status) throws BusinessException {
		int result = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		sb.setLength(0);
		sb.append(
				" UPDATE BK_DETAILS SET SHUTOUT_DELIVERY_PKGS = :deliveredPackages, SHUTOUT_DELIVERY_REMARKS = :deliveryRemarks, SHUTOUT_UPDATE_USER_ID = :userId, ");
		sb.append(" SHUTOUT_UPDATE_DTTM = to_date(:dateTime, 'DDMMYYYY HH24MI') ");
		sb.append(" where bk_ref_nbr = :bkgRefNbr ");
		try {
			log.info("START:  *** updateDeliveryStatus Dao Start : *** bkgRefNbr: " + CommonUtility.deNull(bkgRefNbr) 
					+ " deliveredPackages: " + CommonUtility.deNull(deliveredPackages) + " deliveryRemarks: " + CommonUtility.deNull(deliveryRemarks)
					+ " userId: " + CommonUtility.deNull(userId) + " dateTiem: " + CommonUtility.deNull(dateTime));

			 if(status.equals("A")){  //if updae in the "Active" status page means to close the shut out cargo loop
			        deliveryRemarks =  deliveryRemarks + " " +UPDATE_BY_JP_STAFF + " " + userName;
			    }

			 
			paramMap.put("deliveredPackages", Long.parseLong(deliveredPackages));
			paramMap.put("deliveryRemarks", deliveryRemarks);
			paramMap.put("userId", userId);
			paramMap.put("dateTime", dateTime);
			paramMap.put("bkgRefNbr", bkgRefNbr);
			log.info("updateDeliveryStatus SQL: " + sb.toString());
			log.info("paramMap: " + paramMap.toString());
			result = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
			log.info("result = " + result);

		} catch (NullPointerException e) {
			log.info("Exception updateDeliveryStatus : ", e);
			throw new BusinessException("M0010");
		} catch (Exception e) {
			log.info("Exception updateDeliveryStatus : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateDeliveryStatus DAO ");
		}
		return result;
	}

	// ejb.sessionBeans.gbms.cargo.generalcargo-->ShutoutCargoEJB-->getShutoutCargoMtrgList()
	@Override
	public TableResult getShutoutCargoMtrgList(String dateFrom, String dateTo, String vslName, String outVoyNbr,
			String esnEdoNbr, String cargoType, String terminal, String dwellDays, String custCode, Criteria criteria)
			throws BusinessException {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		ShutOutCargoVo vo = null;
		List<ShutOutCargoVo> list = new ArrayList<>();
		int serialNbr = 1;
		int activeNbr = 1;
		int inactiveNbr = 1;
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		synchronized (this) {
			try {
				log.info("START: getShutoutCargoMtrgList dateFrom: " + CommonUtility.deNull(dateFrom) + " dateTo: "
						+ CommonUtility.deNull(dateTo) + " vslName: " + CommonUtility.deNull(vslName) + " outVoyNbr: "
						+ CommonUtility.deNull(outVoyNbr) + " esnEdoNbr: " + CommonUtility.deNull(esnEdoNbr) + " cargoType: "
						+ CommonUtility.deNull(cargoType) + " terminal: " + CommonUtility.deNull(terminal) + " dwellDays: "
						+ CommonUtility.deNull(dwellDays) + " custCode: " + CommonUtility.deNull(custCode));
				log.info("***  criteria *******" + criteria.toString());

				// StringBuffer query = formQuery(dateFrom, dateTo, vslName, outVoyNbr,
				// esnEdoNbr, cargoType);
				// Query for Local Export Cargo
				// Added For Pagination
				StringBuffer query = formQueryForEnhancement(dateFrom, dateTo, vslName, outVoyNbr, esnEdoNbr, cargoType,
						terminal, dwellDays, custCode);
				
				String sql = query.toString();
//				if (criteria.isPaginated()) {
//					sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());
//
//				}
				// End Added
				if (StringUtils.isNotEmpty(esnEdoNbr)) {

					paramMap.put("esnEdoNbr", esnEdoNbr);

					log.info("esnNbr here **" + esnEdoNbr);
				}

				if (dateFrom != null && !"".equals(dateFrom.trim())) {
					paramMap.put("dateFrom", dateFrom.trim());
				}
				if (dateTo != null && !"".equals(dateTo.trim())) {
					paramMap.put("dateTo", dateTo.trim());
				}
				if (vslName != null && !"".equals(vslName.trim())) {
					paramMap.put("vslName", "%" + vslName.toUpperCase().trim() + "%");
				}
				if (outVoyNbr != null && !"".equals(outVoyNbr.trim())) {
					paramMap.put("outVoyNbr", outVoyNbr.toUpperCase().trim());
				}

				if (terminal != null && !"".equals(terminal.trim())) {
					paramMap.put("terminal", terminal.toUpperCase().trim());
				}
				paramMap.put("custCode", custCode);

				paramMap.put("dwellDays", dwellDays);
				
				
				sql = " WITH active_esns AS ( SELECT TO_CHAR(gb_edo.esn_asn_nbr) as esn_asn_nbr FROM gb_edo WHERE shutout_ind='Y' AND edo_status='A' UNION SELECT TO_CHAR(ua_details.esn_asn_nbr) as esn_asn_nbr FROM ua_details WHERE ua_status='A' ) SELECT c.* FROM ( " + sql + " ) c INNER JOIN active_esns a ON a.esn_asn_nbr = c.esn_asn_nbr";
				
				
				log.info("formQueryForEnhancement SQL: " + sql);
				log.info("formQueryForEnhancement paramMap: " + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

				while (rs.next()) {
					// vo = populateVO(rs, con, cargoType, serialNbr++);
					vo = populateVOForEnhancement(rs, cargoType, serialNbr++, true);
		
					if (cargoType.equalsIgnoreCase("A")) {
						ShutOutCargoVo vo1 = (ShutOutCargoVo) vo;
						//if (!(vo1.getLyingPkgs().equalsIgnoreCase("0"))){
						if ((Integer.parseInt(vo1.getBalancePkgsToShutout())>0) && !vo1.getCloseShutOutLoop().equalsIgnoreCase ("Y")) {
							vo1.setSerialNbr(""+activeNbr++);
							list.add(vo1);
						}
					}
					else {
							ShutOutCargoVo vo2 = (ShutOutCargoVo) vo;
							if ( (Integer.parseInt(vo2.getBalancePkgsToShutout())==0) || (Integer.parseInt(vo2.getBalancePkgsToShutout())>0) && vo2.getCloseShutOutLoop().equalsIgnoreCase ("Y")) {
							vo2.setBalancePkgsToShutout("0"); // set all the inactive record to balance 0
							vo2.setSerialNbr(""+inactiveNbr++);
							list.add(vo2);
						}
					}
				}
				
				log.info("Enhancement Query Size **:" + list.size());
				StringBuffer query1 = formQueryForPsatoJP(dateFrom, dateTo, vslName, outVoyNbr, esnEdoNbr, cargoType,
						terminal, dwellDays, custCode);
				// stmt = con.createStatement();
				// Added For Pagination
				String sql1 = query1.toString();
				
				
				sql1 = " WITH active_esns AS ( SELECT TO_CHAR(gb_edo.esn_asn_nbr) as esn_asn_nbr FROM gb_edo WHERE shutout_ind='Y' AND edo_status='A' UNION SELECT TO_CHAR(ua_details.esn_asn_nbr) as esn_asn_nbr FROM ua_details WHERE ua_status='A' ) SELECT c.* FROM ( " + sql1 + " ) c INNER JOIN active_esns a ON a.esn_asn_nbr = c.esn_asn_nbr";

//				if (criteria.isPaginated()) {
//					sql1 = CommonUtil.getPaginatedSql(sql1, criteria.getStart(), criteria.getLimit());
//
//				}
				// end added

				if (StringUtils.isNotEmpty(esnEdoNbr)) {
					paramMap.put("esnEdoNbr", esnEdoNbr.trim());
					log.info("esnNbr here **" + esnEdoNbr);
				}
				if (dateFrom != null && !"".equals(dateFrom.trim())) {
					paramMap.put("dateFrom", dateFrom.trim());
				}
				if (dateTo != null && !"".equals(dateTo.trim())) {
					paramMap.put("dateTo", dateTo.trim());
				}
				if (vslName != null && !"".equals(vslName.trim())) {
					paramMap.put("vslName", vslName.toUpperCase().trim());
				}
				if (outVoyNbr != null && !"".equals(outVoyNbr.trim())) {
					paramMap.put("outVoyNbr", outVoyNbr.toUpperCase().trim());
				}
				if (terminal != null && !"".equals(terminal.trim())) {
					paramMap.put("terminal", terminal.toUpperCase().trim());
				}
				paramMap.put("custCode", custCode);

				paramMap.put("dwellDays", dwellDays);
				log.info("Query here PSA JP: " + sql1.toString());
				log.info("PSA JP paramMap: " + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);

				while (rs.next()) {
					// vo = populateVO(rs, con, cargoType, serialNbr++);
					vo = populateVOForEnhancement(rs, cargoType, serialNbr++, true);
					if (cargoType.equalsIgnoreCase("A")) {
						ShutOutCargoVo vo1 = (ShutOutCargoVo) vo;
						//if (!(vo1.getLyingPkgs().equalsIgnoreCase("0"))){
						if ( (Integer.parseInt(vo1.getBalancePkgsToShutout())>0) && !vo1.getCloseShutOutLoop().equalsIgnoreCase ("Y")) {
								vo1.setSerialNbr(""+activeNbr++);
								list.add(vo1);
							}
						
					} else {
						ShutOutCargoVo vo2 = (ShutOutCargoVo) vo;
						if ( (Integer.parseInt(vo2.getBalancePkgsToShutout())==0) || (Integer.parseInt(vo2.getBalancePkgsToShutout())>0) && vo2.getCloseShutOutLoop().equalsIgnoreCase ("Y")) {
							vo2.setBalancePkgsToShutout("0"); // set all the inactive record to balance 0
							vo2.setSerialNbr(""+inactiveNbr++);
							list.add(vo2);
						}
					}
				}
				try {
					log.info("START getTableResult ");
					for (ShutOutCargoVo object : list) {
						topsModel.put(object);
					}
					tableData.setListData(topsModel);
					tableData.setTotal(topsModel.getSize());
					tableResult.setData(tableData);
				} catch (Exception e) {
					log.info("Exception getShutoutCargoMtrgList getTableResult: ", e);
					throw new BusinessException("M4201");
				} finally {
					log.info("END getTableResult ");
				}

				log.info("PSA  **:" + list.size());

			} catch (NullPointerException ne) {
				log.info("Exception getShutoutCargoMtrgList : ", ne);
				throw new BusinessException("M0010");
			} catch (BusinessException be) {
				log.info("Exception getShutoutCargoMtrgList : ", be);
				throw new BusinessException(be.getMessage());
			} catch (Exception e) {
				log.info("Exception getShutoutCargoMtrgList : ", e);
				throw new BusinessException("M0010");
			} finally {
				log.info("END: *** getShutoutCargoMtrgList  END *****");
			}
		}
		return tableResult;
	}

	// ejb.sessionBeans.gbms.cargo.generalcargo-->ShutoutCargoEJB-->formQueryForEnhancement()
	private StringBuffer formQueryForEnhancement(String dateFrom, String dateTo, String vslName, String outVoyNbr,
			String esnEdoNbr, String status, String terminal, String dwellDays, String custCode)
			throws BusinessException {
		StringBuffer sb = new StringBuffer();

		try {
			log.info("START: formQueryForEnhancement dateFrom:" + CommonUtility.deNull(dateFrom) + "dateTo:"
					+ CommonUtility.deNull(dateTo) + "vslName:" + CommonUtility.deNull(vslName) + "outVoyNbr:"
					+ CommonUtility.deNull(outVoyNbr) + "esnEdoNbr" + CommonUtility.deNull(esnEdoNbr) + "status:"
					+ CommonUtility.deNull(status) + "terminal:" + CommonUtility.deNull(terminal) + "dwellDays:"
					+ CommonUtility.deNull(dwellDays) + "custCode" + CommonUtility.deNull(custCode));

			sb.setLength(0);
			sb.append(" SELECT DISTINCT  round((SYSDATE-nvl(UA.min_dttm,sysdate)),2) DWELL_DAYS,RECS.* FROM (  ");
			sb.append(" SELECT DISTINCT vc.vsl_nm, vc.terminal, vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND,  shutout_ind, ");
			sb.append("  NVL(TRANSFER.TRANSFER_PKGS,0) transfer_nbr_pkgs, ");
			sb.append(" nvl(shutedo.dn_nbr_pkgs,0) dn_nbr_pkgs, ");
			sb.append("  vc.out_voy_nbr, ");
			sb.append("  vc.vv_cd, ");
			sb.append("  bk.bk_nbr_pkgs, ");
			sb.append("  bk.bk_ref_nbr, ");
			sb.append(
					"  DECODE(esn.trans_type,'A','JP-JP', 'B', 'JP_PSA', 'C', 'PSA_JP','E', 'LOCAL', 'R', 'RESHIP', 'S', 'SHIPSTORE') crg_status, ");
			sb.append("  vc.scheme, ");
			sb.append("  esnd.trucker_nm AS trucker_name, ");
			sb.append("  esnd.esn_asn_nbr, ");
			sb.append("  esnd.nbr_pkgs, ");
			sb.append("  esnd.ua_nbr_pkgs, ");
			sb.append("  nvl(bk.actual_nbr_shipped,0) actual_nbr_shipped, ");
			sb.append("  (nvl(esnd.ua_nbr_pkgs,0) -  nvl(bk.actual_nbr_shipped,0)) lyingPkgs, ");
			sb.append("  (nvl(bk.shutout_qty,0)) shutout_pkgs, ");
			sb.append("  ROUND(((bk.shutout_qty)/esnd.nbr_pkgs)*esnd.esn_wt, 2) total_esn_wt, ");
			sb.append("  ROUND(((bk.shutout_qty)/esnd.nbr_pkgs)*esnd.esn_vol, 2) total_esn_vol, ");
			sb.append("  bk.shutout_delivery_remarks, ");
			sb.append("  nvl(bk.shutout_delivery_pkgs,0) shutout_delivery_pkgs, ");
			sb.append("  TO_CHAR(bk.shutout_update_dttm,'DD/MM/YYYY HH24:MI') shutout_update_dttm, ");
			sb.append("  bk.shutout_update_user_id ");
			sb.append("FROM bk_details bk ");
			sb.append("INNER JOIN esn ");
			sb.append("ON esn.bk_ref_nbr = bk.BK_REF_NBR  and esn.esn_status = 'A' and (nvl(bk.shutout_qty,0) > 0)");
			if (StringUtils.isNotEmpty(esnEdoNbr)) {
				sb.append(" and esn.esn_asn_nbr =:esnEdoNbr ");
				log.info("esnNbr here **" + esnEdoNbr);
			}
			sb.append("INNER JOIN esn_details esnd ");
			sb.append(" ON esn.esn_asn_nbr = esnd.esn_asn_nbr ");
			sb.append((" AND nvl(esnd.ua_nbr_pkgs,0) >0"));
			// sb.append("INNER JOIN tesn_psa_jp psajp ");
			// sb.append("ON esn.esn_asn_nbr = psajp.esn_asn_nbr and nvl(bk.shutout_qty,0) >
			// 0 ");
			sb.append("LEFT JOIN ");
			sb.append("  (SELECT SUM( ( ");
			sb.append("    CASE ");
			sb.append("      WHEN ESN_NEW.TRANS_TYPE ='E' ");
			sb.append("      THEN NVL(ED.UA_NBR_PKGS,0) ");
			sb.append("      WHEN ESN_NEW.TRANS_TYPE ='A' ");
			sb.append("      THEN NVL(TESNJPJP.UA_NBR_PKGS,0) ");
			sb.append("      WHEN ESN_NEW.TRANS_TYPE ='C' ");
			sb.append("      THEN NVL(TESNPSAJP.UA_NBR_PKGS,0) ");
			sb.append("      ELSE 0 ");
			sb.append("    END) ) TRANSFER_PKGS, ");
			sb.append("    ESN_ORG.ESN_ASN_NBR ");
			sb.append("  FROM ESN ESN_NEW, ");
			sb.append("    BK_DETAILS BK_NEW, ");
			sb.append("    ESN ESN_ORG, ");
			sb.append("    BK_DETAILS BK_OLD, ");
			sb.append("    VESSEL_CALL VC_NEW, ");
			sb.append("    ESN_DETAILS ED, ");
			sb.append("    TESN_JP_JP TESNJPJP, ");
			sb.append("    TESN_PSA_JP TESNPSAJP ");
			sb.append("  WHERE ESN_NEW.BK_REF_NBR    = BK_NEW.BK_REF_NBR ");
			sb.append("  AND BK_NEW.OLD_BK_REF       = BK_OLD.BK_REF_NBR ");

			sb.append("  AND ESN_ORG.BK_REF_NBR      = BK_OLD.BK_REF_NBR ");
			sb.append("  AND VC_NEW.VV_CD            = ESN_NEW.OUT_VOY_VAR_NBR ");
			sb.append("  AND BK_OLD.VAR_NBR          = BK_NEW.BK_ORIGINAL_VAR_NBR ");
			sb.append("  AND ESN_NEW.ESN_ASN_NBR     = ED.ESN_ASN_NBR(+) ");
			sb.append("  AND TESNJPJP.ESN_ASN_NBR(+) = ESN_NEW.ESN_ASN_NBR ");
			sb.append("  AND TESNPSAJP.ESN_ASN_NBR(+)= ESN_NEW.ESN_ASN_NBR and ESN_NEW.esn_status = 'A' ");
			sb.append("  GROUP BY ESN_ORG.ESN_ASN_NBR ");
			sb.append("  ) TRANSFER ");
			sb.append("ON TRANSFER.esn_asn_nbr = ESN.ESN_ASN_NBR ");

			sb.append("INNER JOIN vessel_call vc ");
			sb.append("ON vc.vv_cd = esn.out_voy_var_nbr ");
			sb.append("INNER JOIN VESSEL vs ");
			sb.append("ON vs.vsl_nm = vc.vsl_nm ");
			sb.append("LEFT JOIN VESSEL_DECLARANT VD ");
			sb.append("ON VD.vv_cd = vc.vv_cd ");
			sb.append("INNER JOIN berthing b ");
			sb.append("ON b.vv_cd = vc.vv_cd ");
			sb.append("LEFT JOIN bk_details bk_new ");
			sb.append("ON ( bk_new.old_bk_ref = bk.bk_ref_nbr ");
			sb.append("AND (nvl(bk_new.shutout_qty,0)    > 0 )");
			sb.append("AND bk.bk_status       = 'A' ");
			sb.append("AND bk.trans_crg       = 'Y' ");
			sb.append("AND bk_new.bk_status   = 'A' ");
			sb.append("AND bk.var_nbr         = bk_new.bk_original_var_nbr ) ");
			// sb.append("LEFT JOIN gb_edo ");
			// sb.append("ON esn.esn_asn_nbr = gb_edo.esn_asn_nbr ");
			// sb.append("AND shutout_ind = 'Y' ");
			sb.append("LEFT JOIN ");
			sb.append(" (select sum(dn_nbr_pkgs) dn_nbr_pkgs, gb_edo.esn_asn_nbr,gb_edo.shutout_ind from gb_edo, esn esn_new1 ");
			sb.append(" where esn_new1.esn_asn_nbr = gb_edo.esn_asn_nbr and gb_edo.shutout_ind = 'Y' and gb_edo.edo_status = 'A'" );
			sb.append("  and esn_new1.esn_status = 'A' group by gb_edo.esn_asn_nbr,gb_edo.shutout_ind");
			sb.append(" ) SHUTEDO on shutedo.esn_asn_nbr = esn.esn_asn_nbr");
			sb.append(
					" WHERE ((UPPER(VC.TERMINAL) IN 'CT' AND VC.COMBI_GC_OPS_IND IN('Y',null)) OR UPPER(VC.TERMINAL) NOT IN 'CT') ");
			sb.append(" AND ( 'JP'                    = :custCode ");
			sb.append(" OR esn.ESN_CREATE_CD          = :custCode ");
			sb.append(" OR esnd.HA_CUST_CD            = :custCode ");
			sb.append(" OR vc.VSL_OPR_CD              = :custCode ");
			sb.append(" OR esnd.esn_asn_nbr          IN ");
			sb.append("  (SELECT esn_asn_nbr ");
			sb.append("  FROM sub_adp ");
			sb.append("  WHERE status_cd   = 'A' ");
			sb.append("  AND edo_esn_ind  != 1 ");
			sb.append("  AND trucker_co_cd = :custCode ");
			sb.append("  ) )");

			if (sb.length() > 0) {
				if (dateFrom != null && !"".equals(dateFrom.trim())) {
					sb.append(" and b.atb_dttm >= to_date(:dateFrom,'DDMMYYYY') ");
				}
				if (dateTo != null && !"".equals(dateTo.trim())) {
					sb.append(" and b.atb_dttm <= to_date(:dateTo,'DDMMYYYY') ");
				}
				if (vslName != null && !"".equals(vslName.trim())) {
					sb.append(" and (vc.vsl_nm like :vslName");
					sb.append(" or vs.vsl_full_nm like :vslName) ");
				}
				if (outVoyNbr != null && !"".equals(outVoyNbr.trim())) {
					sb.append(" and upper(vc.out_voy_nbr) = :outVoyNbr ");
				}

				if (terminal != null && !"".equals(terminal.trim())) {
					sb.append(" and upper(vc.terminal) = :terminal ");
				}
				
				
				sb.append(" GROUP BY vc.vsl_nm,  ");
				sb.append("  TRANSFER.TRANSFER_PKGS, ");
				sb.append("  shutedo.dn_nbr_pkgs, ");
				sb.append("  vc.out_voy_nbr, ");
				sb.append("  vc.vv_cd, ");
				sb.append("  bk.bk_nbr_pkgs, ");
				sb.append("  bk.bk_ref_nbr, ");
				sb.append(
						"  DECODE(esn.trans_type,'A','JP-JP', 'B', 'JP_PSA', 'C', 'PSA_JP','E', 'LOCAL', 'R', 'RESHIP', 'S', 'SHIPSTORE'), ");
				sb.append("  vc.scheme, vc.COMBI_GC_SCHEME, vc.COMBI_GC_OPS_IND,vc.terminal,shutout_ind, ");
				sb.append("  esnd.trucker_nm, ");
				sb.append("  esnd.esn_asn_nbr, ");
				sb.append("  esnd.nbr_pkgs, ");
				sb.append("  esnd.ua_nbr_pkgs, ");
				sb.append("  bk.actual_nbr_shipped, ");

				sb.append("  (esnd.ua_nbr_pkgs - bk.actual_nbr_shipped), ");
				// sb.append(" gb_edo.dn_nbr_pkgs, ");
				sb.append("     bk.shutout_qty,  ");
				sb.append("  ROUND(((bk.shutout_qty)/esnd.nbr_pkgs)*esnd.esn_wt, 2), ");
				sb.append("  ROUND(((bk.shutout_qty)/esnd.nbr_pkgs)*esnd.esn_vol, 2),  ");
				sb.append("  bk.shutout_delivery_remarks, ");
				sb.append("  bk.shutout_delivery_pkgs, ");
				sb.append("  TO_CHAR(bk.shutout_update_dttm,'DD/MM/YYYY HH24:MI'), ");
				sb.append("  bk.shutout_update_user_id");
				// sb.append(" ORDER BY VC.TERMINAL DESC, VC.VSL_NM ");
				sb.append("  ) RECS LEFT OUTER JOIN (select esn_asn_nbr, min(ua_create_dttm) min_dttm from UA_DETAILS where UA_STATUS = 'A' ");
				sb.append(
						" group by esn_asn_nbr) UA ON RECS.ESN_ASN_NBR=UA.ESN_ASN_NBR  where (SYSDATE-nvl(UA.min_dttm,sysdate))  >= ");
				sb.append(":dwellDays");
				sb.append(" ");
				sb.append(" GROUP BY VSL_NM, TERMINAL, COMBI_GC_SCHEME, COMBI_GC_OPS_IND,shutout_ind, (SYSDATE-nvl(UA.min_dttm,sysdate)) ,  ");
				sb.append(" TRANSFER_NBR_PKGS, DN_NBR_PKGS, OUT_VOY_NBR, VV_CD, BK_NBR_PKGS, ");
				sb.append(" BK_REF_NBR, CRG_STATUS, SCHEME, TRUCKER_NAME, RECS.ESN_ASN_NBR, ");
				sb.append(" RECS.NBR_PKGS, RECS.UA_NBR_PKGS, ACTUAL_NBR_SHIPPED, LYINGPKGS, ");
				sb.append(" SHUTOUT_PKGS, TOTAL_ESN_WT, TOTAL_ESN_VOL, SHUTOUT_DELIVERY_REMARKS, ");
				sb.append(" SHUTOUT_DELIVERY_PKGS, SHUTOUT_UPDATE_DTTM, SHUTOUT_UPDATE_USER_ID ");
			}
			log.info("return query **" + sb.toString());
		} catch (Exception e) {
			log.info("Exception formQueryForEnhancement : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: formQueryForEnhancement DAO");
		}
		return sb;

	}

	
}
