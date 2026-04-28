package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.LighterBillRepository;
import sg.com.jp.generalcargo.domain.CargoDeclarationItemVO;
import sg.com.jp.generalcargo.domain.CargoDeclarationVO;
import sg.com.jp.generalcargo.domain.CranageVO;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.Constant;

@Repository("LighterBillJdbcRepository")
public class LighterBillJdbcRepository implements LighterBillRepository {

	private static final Log log = LogFactory.getLog(LighterBillJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	
	// StartRegion LighterBillJdbcRepository
	// jp.src.ejb.sessionBeans.lwms-->LighterBill
		public List<CargoDeclarationVO> loadDSAForBillGen(String dsaNo) throws BusinessException {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			SqlRowSet rs = null;
			StringBuffer query = new StringBuffer();
			List<CargoDeclarationVO> list = new ArrayList<CargoDeclarationVO>();

			StringBuffer sqlDsaItem = new StringBuffer();
			SqlRowSet rsDsaItem = null;

			StringBuffer sqlDsaCranage = new StringBuffer();
			SqlRowSet rsDsaCranage = null;

			try {
				log.info("START loadDSAForBillGen DAO"+ CommonUtility.deNull(dsaNo));
				
				query.setLength(0);
				query.append(
						"SELECT A.*, NVL(D.ATB_DTTM, SYSDATE) ATB_DTTM, NVL(A.OUTWARD_DTTM,SYSDATE) OUTWARD_CRANAGE_DTTM ");
				query.append(" FROM LWMS_DSA A ");
				query.append(" LEFT JOIN LWMS_DOCKAGE D ON (A.VSL_MVMT_NBR = D.VSL_MVMT_NBR) ");
				query.append(" WHERE A.DSA_STATUS <> 'X' ");
				if (!dsaNo.equals("")) {
					// query.append(" AND A.DSA_STATUS = 'O' ");
					query.append(" AND A.DSA_NBR = :dsaNo ");
				} else {
					query.append(" AND A.BILLING_STATUS = '" + Constant.DSA_BILL_STATUS_READY + "' ");
					query.append(" AND A.PAYM_MODE = 'A' ");
				}
				query.append(" ORDER BY DSA_NBR ");

				
				if (!dsaNo.equals("")) {
					paramMap.put("dsaNo", dsaNo);
				}
				log.info(" *** loadDSAForBillGen SQL *****" + query.toString());
				log.info(" *** loadDSAForBillGen params *****" + paramMap.toString());

				rs = namedParameterJdbcTemplate.queryForRowSet(query.toString(), paramMap);

				while (rs.next()) {
					CargoDeclarationVO vo = new CargoDeclarationVO();

					vo.setDsa_nbr(CommonUtility.deNull(rs.getString("DSA_NBR")));
					vo.setCust_cd(CommonUtility.deNull(rs.getString("CUST_CD")));
					vo.setLighter_opr_cd(CommonUtility.deNull(rs.getString("LIGHTER_OPR_CD")));
					vo.setTerminal_cd(CommonUtility.deNull(rs.getString("TERMINAL_CD")));
					vo.setDsa_status(CommonUtility.deNull(rs.getString("DSA_STATUS")));
					vo.setBilling_status(CommonUtility.deNull(rs.getString("BILLING_STATUS")));
					vo.setLighter_nm(CommonUtility.deNull(rs.getString("LIGHTER_NM")));
					vo.setLighter_eta(rs.getTimestamp("LIGHTER_ETA"));
					vo.setDcl_truck_nbr(CommonUtility.deNull(rs.getString("DCL_TRUCK_NBR")));
					vo.setAct_truck_Nbr(CommonUtility.deNull(rs.getString("ACT_TRUCK_NBR")));
					vo.setTruck_status(CommonUtility.deNull(rs.getString("TRUCK_STATUS")));
					vo.setDriver1_pass_id(CommonUtility.deNull(rs.getString("DRIVER1_PASS_ID")));
					vo.setDriver2_pass_id(CommonUtility.deNull(rs.getString("DRIVER2_PASS_ID")));
					vo.setPaym_mode(CommonUtility.deNull(rs.getString("PAYM_MODE")));
					vo.setAcct_nbr(CommonUtility.deNull(rs.getString("ACCT_NBR")));
					vo.setBill_nbr(CommonUtility.deNull(rs.getString("BILL_NBR")));
					// rs.getTi
					vo.setBill_dttm(rs.getTimestamp("BILL_DTTM"));
					vo.setShip_nm(CommonUtility.deNull(rs.getString("SHIP_NM")));
					vo.setDg_cargo_ind(CommonUtility.deNull(rs.getString("DG_CARGO_IND")));
					vo.setCargo_dest_type(CommonUtility.deNull(rs.getString("CARGO_DEST_TYPE")));
					vo.setCargo_dest_cd(CommonUtility.deNull(rs.getString("CARGO_DEST_CD")));
					vo.setGate_in_dttm(rs.getTimestamp("GATE_IN_DTTM"));
					vo.setGate_out_dttm(rs.getTimestamp("GATE_OUT_DTTM"));
					vo.setGate_status(CommonUtility.deNull(rs.getString("GATE_STATUS")));
					vo.setCrane_nbr(CommonUtility.deNull(rs.getString("CRANE_NBR")));
					vo.setCrane_opr_cd(CommonUtility.deNull(rs.getString("CRANE_OPR_CD")));
					vo.setCrane_start_dttm(rs.getTimestamp("CRANE_START_DTTM"));
					vo.setPan_dttm(rs.getTimestamp("PAN_DTTM"));

					vo.setVsl_mvmt_nbr(CommonUtility.deNull(rs.getString("VSL_MVMT_NBR")));
					vo.setCashsales_ref(CommonUtility.deNull(rs.getString("CASHSALES_REF")));
					vo.setCreate_user_id(CommonUtility.deNull(rs.getString("CREATE_USER_ID")));
					vo.setCreate_dttm(rs.getTimestamp("CREATE_DTTM"));
					vo.setSubmitted_user_id(CommonUtility.deNull(rs.getString("SUBMITTED_USER_ID")));
					vo.setSubmitted_dttm(rs.getTimestamp("SUBMITTED_DTTM"));
					vo.setLast_modify_user_id(CommonUtility.deNull(rs.getString("LAST_MODIFY_USER_ID")));
					vo.setLast_modify_dttm(rs.getTimestamp("LAST_MODIFY_DTTM"));

					vo.setDockageATB(rs.getTimestamp("ATB_DTTM"));
					vo.setOutward_dttm(rs.getTimestamp("OUTWARD_CRANAGE_DTTM"));

					// DSA Item Details
					sqlDsaItem.setLength(0);
					// sqlDsaItem.append("select * from LWMS_DSA_DETAILS ");
					// sqlDsaItem.append("where dsa_nbr = ? AND status_cd = 'A' ");
					// sqlDsaItem.append(" ORDER BY CARGO_LINE_NBR " );

					// Modified By kkchua Sum up items by DSA, direction_ind
					sqlDsaItem.setLength(0);
					sqlDsaItem.append("select DSA_NBR, DIRECTION_IND, CARGO_TYPE_CD, ");
					sqlDsaItem.append(
							"CEIL(SUM(DECLARED_WT_TON)) AS DECLARED_WT_TON, CEIL(SUM(ACTUAL_WT_TON)) AS ACTUAL_WT_TON, CEIL(SUM(REJECTED_WT_TON)) AS REJECTED_WT_TON, ");
					sqlDsaItem.append(
							"CEIL(SUM(DECLARED_VOL_M3)) AS DECLARED_VOL_M3, CEIL(SUM(ACTUAL_VOL_M3)) AS ACTUAL_VOL_M3, CEIL(SUM(REJECTED_VOL_M3)) AS REJECTED_VOL_M3, ");
					sqlDsaItem.append(
							"CEIL(SUM(DECLARED_QTY)) AS DECLARED_QTY, CEIL(SUM(ACTUAL_QTY)) AS ACTUAL_QTY, CEIL(SUM(REJECTED_QTY)) AS REJECTED_QTY ");
					sqlDsaItem.append("from LWMS_DSA_DETAILS ");
					sqlDsaItem.append("where dsa_nbr = :dsa_nbr AND status_cd = 'A' ");
					sqlDsaItem.append("GROUP BY DSA_NBR, DIRECTION_IND, CARGO_TYPE_CD ");
					sqlDsaItem.append("ORDER BY DSA_NBR, DIRECTION_IND, CARGO_TYPE_CD ");

					log.info("SQL Query    :" + sqlDsaItem.toString());

					paramMap.put("dsa_nbr", rs.getString("DSA_NBR"));

					log.info(" *** loadDSAForBillGen SQL *****" + sqlDsaItem.toString());
					log.info(" *** loadDSAForBillGen params *****" + paramMap.toString());
					rsDsaItem = namedParameterJdbcTemplate.queryForRowSet(sqlDsaItem.toString(), paramMap);

					List<CargoDeclarationItemVO> dsaItemList = new ArrayList<CargoDeclarationItemVO>();
					CargoDeclarationItemVO oiVo = null;
					while (rsDsaItem.next()) {
						oiVo = new CargoDeclarationItemVO();
						oiVo.setDsa_nbr(CommonUtility.deNull(rsDsaItem.getString("DSA_NBR")));
						// oiVo.setCargo_line_nbr(rsDsaItem.getInt("CARGO_LINE_NBR"));
						oiVo.setDirection_ind(CommonUtility.deNull(rsDsaItem.getString("DIRECTION_IND")));
						// oiVo.setCargo_desc(CommonUtility.deNull(rsDsaItem.getString("CARGO_DESC")));
						// oiVo.setCommodity_type_cd(CommonUtility.deNull(rsDsaItem.getString("COMMODITY_TYPE_CD")));
						// oiVo.setCommodity_sub_type_cd(CommonUtility.deNull(rsDsaItem.getString("COMMODITY_SUB_TYPE_CD")));
						// oiVo.setCommodity_type_nm(CommonUtility.deNull(rsDsaItem.getString("COMMONDITY_TYPE_CD")));
						oiVo.setCargo_type_cd(CommonUtility.deNull(rsDsaItem.getString("CARGO_TYPE_CD")));

						oiVo.setDeclared_wt_ton(rsDsaItem.getDouble("DECLARED_WT_TON"));
						oiVo.setActual_wt_ton(rsDsaItem.getDouble("ACTUAL_WT_TON"));
						oiVo.setRejected_wt_ton(rsDsaItem.getDouble("REJECTED_WT_TON"));
						oiVo.setDeclared_vol_m3(rsDsaItem.getDouble("DECLARED_VOL_M3"));
						oiVo.setActual_vol_m3(rsDsaItem.getDouble("ACTUAL_VOL_M3"));
						oiVo.setRejected_vol_m3(rsDsaItem.getDouble("REJECTED_VOL_M3"));
						oiVo.setDeclared_qty(rsDsaItem.getDouble("DECLARED_QTY"));
						oiVo.setActual_qty(rsDsaItem.getDouble("ACTUAL_QTY"));
						oiVo.setRejected_qty(rsDsaItem.getDouble("REJECTED_QTY"));

						// oiVo.setStatus_cd(CommonUtility.deNull(rsDsaItem.getString("STATUS_CD")));

						// oiVo.setLast_modify_user_id(CommonUtility.deNull(rsDsaItem.getString("LAST_MODIFY_USER_ID")));
						// oiVo.setLast_modify_dttm(rsDsaItem.getTimestamp("LAST_MODIFY_DTTM"));

						dsaItemList.add(oiVo);
					}

					vo.setDsaItems(dsaItemList);

					// DSA Cranage Details
					sqlDsaCranage.setLength(0);
					sqlDsaCranage.append("select * from LWMS_DSA_CRANAGE ");
					sqlDsaCranage.append("where dsa_nbr = :dsa_nbr ");
					sqlDsaCranage.append(" ORDER BY CRANAGE_LINE_NBR ");

					log.info("SQL Query    :" + sqlDsaCranage.toString());
					paramMap.put("dsa_nbr", rs.getString("DSA_NBR"));
					log.info(" *** loadDSAForBillGen SQL *****" + sqlDsaCranage.toString());
					log.info(" *** loadDSAForBillGen params *****" + paramMap.toString());
					rsDsaCranage = namedParameterJdbcTemplate.queryForRowSet(sqlDsaCranage.toString(), paramMap);

					List<CranageVO> dsaCranageList = new ArrayList<CranageVO>();
					CranageVO ocVo = null;
					while (rsDsaCranage.next()) {
						ocVo = new CranageVO();
						ocVo.setDsa_nbr(CommonUtility.deNull(rsDsaCranage.getString("DSA_NBR")));
						ocVo.setCranage_line_nbr(rsDsaCranage.getInt("CRANAGE_LINE_NBR"));
						ocVo.setCargo_type(CommonUtility.deNull(rsDsaCranage.getString("CARGO_TYPE")));

						ocVo.setNorm_lift_nbr(rsDsaCranage.getDouble("NORM_LIFT_NBR"));
						ocVo.setWharf_lift_nbr(rsDsaCranage.getDouble("WHARF_LIFT_NBR"));
						ocVo.setFrom_ton(rsDsaCranage.getDouble("FROM_TON"));
						ocVo.setTo_ton(rsDsaCranage.getDouble("TO_TON"));

						ocVo.setLast_modify_user_id(CommonUtility.deNull(rsDsaCranage.getString("LAST_MODIFY_USER_ID")));
						ocVo.setLast_modify_dttm(rsDsaCranage.getTimestamp("LAST_MODIFY_DTTM"));
						dsaCranageList.add(ocVo);
					}
					vo.setDsaCranages(dsaCranageList);
					list.add(vo);
				}
				
				log.info("END: *** loadDSAForBillGen Result *****" + list.size());
				return list;
			} catch (Exception e) {
				log.info("Exception loadDSAForBillGen : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END loadDSAForBillGen DAO");
			}
		}
	// EndRegion LighterBillJdbcRepository

}
