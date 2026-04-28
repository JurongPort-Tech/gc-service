package sg.com.jp.generalcargo.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.LineTowedVesselRepo;
import sg.com.jp.generalcargo.domain.LineTowedVesselValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("lineTowedVesselRepo")
public class LineTowedVesselJdbcRepo implements LineTowedVesselRepo {
	private static final Log log = LogFactory.getLog(LineTowedVesselJdbcRepo.class);

	// ejb.sessionBeans.gbms.ops.vesselact.linetowedvessel -->LineTowedVesselEJB
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void addDockage(String vvcd, List<LineTowedVesselValueObject> co) throws BusinessException {
		String sql = "", sql1 = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> paramMap1 = new HashMap<String, Object>();
		Map<Object, Object> temp = new HashMap<Object, Object>();
		StringBuffer sb = new StringBuffer();
		SqlRowSet rs = null;
		try {
			log.info("START: addDockage  DAO vvcd: " + vvcd + ",co:" + co.toString());

			sb.append("insert into  gb_towed_vessel_dockage ( VV_CD, ");
			sb.append("TARIFF_MAIN_CAT_CD,  TARIFF_SUB_CAT_CD, START_DTTM, END_DTTM ,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM ) " );
	        sb.append("values (:vvCd,:tariffMainCd,:tariffSubCd,:startDttm,:endDttm,:user,sysdate)"); 
			sql = sb.toString();
			
			sb = new StringBuffer();
			sb.append("SELECT TERMINAL, CEMENT_VSL_IND, BULK_VSL_IND, VSL_TYPE, ALLOWED_START_TIME, ALLOWED_END_TIME, ");
			sb.append("(CASE WHEN ACTUAL_START_TIME>=ALLOWED_END_TIME AND ACTUAL_START_TIME<ALLOWED_START_TIME THEN 'ERR' ELSE 'OK' END) CHECK_STATUS ");
			sb.append("FROM (SELECT TERMINAL, CEMENT_VSL_IND, BULK_VSL_IND, VSL_TYPE, S.MISC_TYPE_NM ALLOWED_START_TIME, ");
			sb.append("(SELECT T.MISC_TYPE_NM FROM SYSTEM_CONFIG T WHERE T.CAT_CD='LTB_SPECIAL_DOCKAGE_TIME' AND T.MISC_TYPE_CD='END_TIME') ALLOWED_END_TIME, :startDttm ACTUAL_START_TIME ");
			sb.append("FROM SYSTEM_CONFIG S, (SELECT V.VV_CD, V.TERMINAL, V.CEMENT_VSL_IND, C.BULK_VSL_IND, ");
			sb.append("DECODE(V.TERMINAL,'CT','CT',DECODE(NVL(V.CEMENT_VSL_IND,'N'),'Y','CEMENT',DECODE(NVL(C.BULK_VSL_IND,'N'),'Y','OBC','GC'))) VSL_TYPE ");
			sb.append("FROM VESSEL_CALL V, VESSEL_PRE_OPS P, CARGO_CLIENT_CODE C WHERE V.VV_CD=P.VV_CD AND P.CC_CD=C.CC_CD AND V.VV_CD=:vvcd) A ");
			sb.append("WHERE S.CAT_CD='LTB_SPECIAL_DOCKAGE_TIME' AND S.MISC_TYPE_CD='START_TIME_'||A.VSL_TYPE)");
			sql1 = sb.toString();
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm"); 
	  		SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
	  		SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy"); 
	  		SimpleDateFormat formatter3 = new SimpleDateFormat("HHmm"); 
	  		
  			this.delete(vvcd);
  			int result;
            int count = 0;
            
            for (Iterator<LineTowedVesselValueObject> iterator = co.iterator(); iterator.hasNext();) {
            	paramMap = new HashMap<String, Object>();
				LineTowedVesselValueObject vo = (LineTowedVesselValueObject) iterator.next();
				paramMap.put("vvCd", vo.getVvCode());
				paramMap.put("tariffMainCd", vo.getTariffMainCatCode());
				paramMap.put("tariffSubCd", vo.getTariffSubCatCode());
				if(vo.getTariffSubCatCode().equals("07")){ 
					paramMap.put("startDttm", vo.getStartTimestamp());
					paramMap.put("endDttm", vo.getEndTimestamp());
                	String aa = formatter1.format(vo.getStartTimestamp());
                	log.info(aa);
                } else {
                	paramMap.put("startDttm", vo.getStartTimestamp());
					paramMap.put("endDttm", vo.getEndTimestamp());
                }
				paramMap.put("user", vo.getLastModifyUserId());
				if(vo.getTariffSubCatCode().equals("07")){ 
		           	String aa = formatter.format(vo.getStartTimestamp());
		           	log.info(aa);
		           	String bb = formatter.format(vo.getEndTimestamp());
		           	log.info(bb);
					int compare = formatter.parse(
							formatter.format(vo.getStartTimestamp())).compareTo(formatter.parse(
									formatter.format(vo.getEndTimestamp())));
					if (compare == 0) {
						throw new BusinessException(
								"End Time of Special Dockage must be later than the Start Time of Special Dockage.");
					}
                

					paramMap1.put("startDttm", formatter3.format(vo.getStartTimestamp()));
					paramMap1.put("vvcd", vvcd);
	                rs = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap1);
					if (rs.next()) {
						String chkStatus = rs.getString("CHECK_STATUS");
						String allStartTime = rs.getString("ALLOWED_START_TIME");
						String allEndTime = rs.getString("ALLOWED_END_TIME");
						if (chkStatus.equalsIgnoreCase("ERR")) {
							throw new BusinessException(
									"Start Time of Special Dockage must be between "
											+ allStartTime + " and " + allEndTime
											+ ".");
						}
						log.info("getEndTimestamp dd-MM-yyyy HH:mm: ----- : " +  formatter.format(vo.getEndTimestamp()));	
						
						allEndTime = allEndTime.substring(0,2)+":"+allEndTime.substring(2); //added : into time from db
						String allowedEndDateTime = formatter2.format(vo.getEndTimestamp())	+" " + allEndTime;  // append date to compare whole datetime
						log.info("allowedEndDateTime ---- : " + allowedEndDateTime.toString());	
						
						compare = formatter.parse(
								formatter.format(vo.getEndTimestamp())).compareTo( //25052022 0645
										formatter.parse(allowedEndDateTime.toString())); //25052022 0630
						
						if (compare > 0) {
							throw new BusinessException(
									"End Time of Special Dockage cannot be later than "
											+ allEndTime + ".");
						}
					}
                }
                temp.put(count, paramMap); 
                count++;
			}

            for (int i = 0; i < temp.size(); i++) {
            	Map<String, Object> paramMap2 = (Map<String, Object>) temp.get(i);
            	log.info(" *** addDockage SQL *****" + sql + " paramMap " + paramMap2.toString());
				result=namedParameterJdbcTemplate.update(sql, paramMap2);
	            if (result>1) throw (new SQLException("more than 1 row updated"));     
	            
			}
			
		} catch (BusinessException ex) {
			log.info("Exception addDockage : ", ex);
			throw new BusinessException(ex.getMessage());
		} catch (NullPointerException e) {
			log.error("Exception: addDockage ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: addDockage ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: addDockage  DAO " );
		}
	}

	public void delete(String strvvcd) throws BusinessException {
		int result = 0;
		String sql = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: delete  DAO strvvcd:" + strvvcd);

			sql = "delete from  gb_towed_vessel_dockage where vv_cd = :strvvcd ";

			paramMap.put("strvvcd", strvvcd);

			log.info(" *** delete SQL *****" + sql + " paramMap " + paramMap.toString());
			result = namedParameterJdbcTemplate.update(sql, paramMap);

			if (result < 0)
				throw (new Exception("DB error"));

		} catch (NullPointerException e) {
			log.error("Exception: delete ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: delete ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: delete  DAO Result:" + result);
		}
	}

	@Override
	public List<LineTowedVesselValueObject> getDockageList(String vvCd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = "";
		List<LineTowedVesselValueObject> dockageArrayList = new ArrayList<LineTowedVesselValueObject>();
		try {
			log.info("START: getDockageList  DAO  Start Obj vvCd" + vvCd);

			sql = "SELECT * FROM gb_towed_vessel_dockage A WHERE A.vv_cd = :vvCd order by start_dttm";

			paramMap.put("vvCd", vvCd);

			log.info(" *** getBundleList SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {
				LineTowedVesselValueObject vo = new LineTowedVesselValueObject();
				vo.setVvCode(vvCd);
				vo.setTariffMainCatCode(CommonUtility.deNull(rs.getString("TARIFF_MAIN_CAT_CD")));
				vo.setTariffSubCatCode(CommonUtility.deNull(rs.getString("TARIFF_SUB_CAT_CD")));
				vo.setStartTimestamp(rs.getTimestamp("START_DTTM"));
				vo.setEndTimestamp(rs.getTimestamp("END_DTTM"));
				vo.setLastModifyUserId(rs.getString("LAST_MODIFY_USER_ID"));
				vo.setLastModifyTimestamp(rs.getTimestamp("LAST_MODIFY_DTTM"));
				dockageArrayList.add(vo);
			}
			log.info("END: *** getDockageList Result *****" + dockageArrayList.toString());
		} catch (NullPointerException e) {
			log.error("Exception: getDockageList ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getDockageList ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getDockageList  DAO  END");
		}
		return dockageArrayList;
	}

	@Override
	public boolean getDockageStatus(String custCd) throws BusinessException {
		boolean result = false;
		String sql = "select count(1) cnt  from misc_type_code where cat_cd = 'SPDOCKAGE' AND MISC_TYPE_NM = :custCd and rec_status = 'A'";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs1 = null;
		try {
			log.info("START: getDockageStatus  DAO  Start Obj " + " custCd:" + custCd);

			paramMap.put("custCd", custCd);

			log.info(" *** getDockageStatus SQL *****" + sql + " paramMap " + paramMap.toString());
			rs1 = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			int i = 0;
			if (rs1.next()) {
				i = rs1.getInt(1);
			}

			if (i > 0) {
				result = true;
			} else {
				result = false;
			}

			log.info("END: *** getDockageStatus Result *****" + result);
		} catch (NullPointerException e) {
			log.error("Exception: getDockageStatus ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getDockageStatus ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getDockageStatus  DAO  END");
		}
		return result;
	}

}
