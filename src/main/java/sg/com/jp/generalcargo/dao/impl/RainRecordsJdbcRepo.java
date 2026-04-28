package sg.com.jp.generalcargo.dao.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.RainRecordsRepo;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.RainRecordsValueObject;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("rainRecordsRepo")
public class RainRecordsJdbcRepo implements RainRecordsRepo {

	private static final Log log = LogFactory.getLog(RainRecordsJdbcRepo.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// ejb.sessionBeans.ops.RainRecords -->RainRecordsEjb

	@Override
	public boolean updateRainRecord(String rainCntr, String startDate, String startTime, String endDate, String endTime,
			String rainCategoryCd, String userId, String statusCd, String location) throws BusinessException {
		String actionCD = "";
		String sqlUpdate = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		if (statusCd.equalsIgnoreCase("A")) {
			// In audit log Action CD 'U' for updation 'D' for deletion
			actionCD = "U";
		} else {
			actionCD = "D";
		}

		boolean result = false;
		try {
			log.info("START: updateRainRecord  DAO  Start Obj " + " rainCntr:" + rainCntr + " startDate:" + startDate
					+ " startTime:" + startTime + " endDate:" + endDate + " endTime:" + endTime + " rainCategoryCd:"
					+ rainCategoryCd + " userId:" + userId + " statusCd:" + statusCd + " location:" + location);

			sb.append(
					"SELECT COUNT (*)   FROM GBMS.rain_record  WHERE status_cd = 'A' AND location_cd = :location AND RAIN_CNT!= ");
			sb.append(" :rainCntr AND (TO_DATE ((:startDate||' '||:startTime");
			sb.append("), 'DD/MM/RRRR hh24:mi:ss')  BETWEEN start_dttm  AND end_dttm OR TO_DATE ((:endDate ");
			sb.append(
					"||' '||:endTime), 'DD/MM/RRRR hh24:mi:ss') BETWEEN start_dttm  AND end_dttm OR (start_dttm between TO_DATE((");
			sb.append(":startDate||' '||:startTime), 'DD/MM/RRRR hh24:mi:ss')  and TO_DATE ((");
			sb.append(":endDate||' '||:endTime), 'DD/MM/RRRR hh24:mi:ss') OR (end_dttm between TO_DATE ((");
			sb.append(":startDate||' '||:startTime), 'DD/MM/RRRR hh24:mi:ss')  and TO_DATE ((");
			sb.append(":endDate||' '||:endTime), 'DD/MM/RRRR hh24:mi:ss')))) ");
			String sql = sb.toString();

			sb = new StringBuffer();
			sb.append("update GBMS.rain_record set START_DTTM = TO_DATE((:startDate||' '||");
			sb.append(":startTime), 'DD/MM/RRRR hh24:mi:ss'), END_DTTM = TO_DATE((:endDate||' '||");
			sb.append(":endTime), 'DD/MM/RRRR hh24:mi:ss'), STATUS_CD = :statusCd, ");
			sb.append("CAT_CD = :rainCategoryCd, LOCATION_CD = :location where  RAIN_CNT = :rainCntr");

			sqlUpdate = sb.toString();

			sb = new StringBuffer();
			sb.append(
					"INSERT INTO GBMS.rain_record_log (RAIN_CNT, LOG_DTTM, USER_ID, ACTION_CD, START_DTTM, END_DTTM, CAT_CD, LOCATION_CD)");
			sb.append(" VALUES (:rainCntr, SYSDATE , :userId, :actionCD, TO_DATE((:startDate");
			sb.append("||' '||:startTime), 'DD/MM/RRRR hh24:mi:ss'), TO_DATE((:endDate");
			sb.append("||' '||:endTime), 'DD/MM/RRRR hh24:mi:ss'), :rainCategoryCd, :location)");

			String sqlInsertLog = sb.toString();

			if (statusCd.equalsIgnoreCase("A")) {
				/*
				 * Checks before updation whther there is an existing record with same values or
				 * not
				 */
				try {

					paramMap.put("location", location);
					paramMap.put("rainCntr", rainCntr);
					paramMap.put("startDate", startDate);
					paramMap.put("startTime", startTime);
					paramMap.put("endDate", endDate);
					paramMap.put("endTime", endTime);

					log.info(" *** updateRainRecord SQL *****" + sql + " paramMap " + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

				} catch (Exception e) {
					log.error("Exception: updateRainRecord ", e);
					throw new BusinessException("M4201");
				}

				if (rs.next()) {
					if (rs.getInt(1) == 0) {
						/*
						 * Updates the RainRecords
						 */
						paramMap = new HashMap<String, Object>();

						paramMap.put("location", location);
						paramMap.put("rainCntr", rainCntr);
						paramMap.put("startDate", startDate);
						paramMap.put("startTime", startTime);
						paramMap.put("endDate", endDate);
						paramMap.put("endTime", endTime);
						paramMap.put("statusCd", statusCd);
						paramMap.put("rainCategoryCd", rainCategoryCd);
						paramMap.put("userId", userId);

						log.info(" *** updateRainRecord SQL *****" + sqlUpdate + " paramMap " + paramMap.toString());
						int resultUpdate = namedParameterJdbcTemplate.update(sqlUpdate, paramMap);

						log.info("RainRecords add block insertResult:::::::::" + resultUpdate);
						if (resultUpdate > 0) {

							/*
							 * Insert into the RainRecord_log table for audit log
							 */

							paramMap = new HashMap<String, Object>();
							paramMap.put("actionCD", actionCD);
							paramMap.put("location", location);
							paramMap.put("rainCntr", rainCntr);
							paramMap.put("startDate", startDate);
							paramMap.put("startTime", startTime);
							paramMap.put("endDate", endDate);
							paramMap.put("endTime", endTime);
							paramMap.put("statusCd", statusCd);
							paramMap.put("rainCategoryCd", rainCategoryCd);
							paramMap.put("userId", userId);

							log.info(" *** updateRainRecord SQL *****" + sqlInsertLog + " paramMap "
									+ paramMap.toString());
							int insertResultLog = namedParameterJdbcTemplate.update(sqlInsertLog, paramMap);

							if (insertResultLog == 0) {
								throw new BusinessException("M1007");
							}
							result = true;
						} else {
							if (resultUpdate == 0) {
								throw new BusinessException("M1007");
							}

						}

					} else {
						// throw business exception "record already exists it can
						// not be updated"
						log.info("-----------RainRecords----------------Record already exists");
						if (statusCd.equalsIgnoreCase("A")) {
							throw new BusinessException("M50014");
						}

					}
				}
			} else {
				/*
				 * Updates the RainRecords when mode value is 'delete'
				 */
				paramMap = new HashMap<String, Object>();
				paramMap.put("location", location);
				paramMap.put("rainCntr", rainCntr);
				paramMap.put("startDate", startDate);
				paramMap.put("startTime", startTime);
				paramMap.put("endDate", endDate);
				paramMap.put("endTime", endTime);
				paramMap.put("statusCd", statusCd);
				paramMap.put("rainCategoryCd", rainCategoryCd);
				log.info(" *** updateRainRecord SQL *****" + sqlUpdate + " paramMap " + paramMap.toString());
				int resultUpdate = namedParameterJdbcTemplate.update(sqlUpdate, paramMap);

				log.info("RainRecords  block updateResult:::::::::" + resultUpdate);
				if (resultUpdate > 0) {

					/*
					 * Insert into the RainRecord_log table for audit log
					 */
					paramMap = new HashMap<String, Object>();
					paramMap.put("actionCD", actionCD);
					paramMap.put("location", location);
					paramMap.put("rainCntr", rainCntr);
					paramMap.put("startDate", startDate);
					paramMap.put("startTime", startTime);
					paramMap.put("endDate", endDate);
					paramMap.put("endTime", endTime);
					paramMap.put("statusCd", statusCd);
					paramMap.put("rainCategoryCd", rainCategoryCd);
					paramMap.put("userId", userId);
					log.info(" *** updateRainRecord SQL *****" + sqlInsertLog + " paramMap " + paramMap.toString());
					int insertResultLog = namedParameterJdbcTemplate.update(sqlInsertLog, paramMap);
					if (insertResultLog == 0) {
						throw new BusinessException("M1007");
					}
					result = true;
				} else {
					if (resultUpdate == 0) {
						throw new BusinessException("M1007");
					}

				}

			}

			log.info("END: *** updateRainRecord Result *****" + result);
		} catch (BusinessException e) {
			log.error("Exception: updateRainRecord ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.error("Exception: updateRainRecord ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: updateRainRecord ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateRainRecord  DAO  END");
		}

		return result;
	}

	@Override
	public boolean addRainRecord(String startDate, String startTime, String endDate, String endTime,
			String rainCategoryCd, String userId, String location) throws BusinessException {
		boolean result = false;
		String actionCD = "C";
		String sqlInsert = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		try {

			log.info("START: addRainRecord  DAO  Start Obj " + " startDate:" + startDate + " startTime:" + startTime
					+ " endDate:" + endDate + " endTime:" + endTime + " rainCategoryCd:" + rainCategoryCd + " userId:"
					+ userId + " location:" + location);

			/*
			 * This Query gives the count of the active existing records for the Dates and
			 * Time entered by the user to add the rain records. If any record exists the
			 * record is not inserted and a business exception is thrown
			 */
			/*
			 * SELECT COUNT (*) FROM rain_record WHERE status_cd = 'A' AND ( TO_DATE ((
			 * :start_date || ' ' || :start_time ), 'DD/MM/RRRR hh24:mi:ss') BETWEEN
			 * start_dttm AND end_dttm OR TO_DATE (( :end_date || ' ' || :end_time ),
			 * 'DD/MM/RRRR hh24:mi:ss') BETWEEN start_dttm AND end_dttm OR (start_dttm
			 * between TO_DATE(( :start_date ||' '|| :start_time ), 'DD/MM/RRRR hh24:mi:ss')
			 * AND TO_DATE (( :end_date ||' '|| :end_time ), 'DD/MM/RRRR hh24:mi:ss') OR
			 * (end_dttm between TO_DATE (( :start_date ||' '|| :start_time ), 'DD/MM/RRRR
			 * hh24:mi:ss') AND TO_DATE (( :end_date ||' '|| :end_time ), 'DD/MM/RRRR
			 * hh24:mi:ss'))) )
			 */

			sb.append(
					"SELECT COUNT (*)   FROM GBMS.rain_record  WHERE status_cd = 'A'  AND location_cd = :location AND (   TO_DATE ((");
			sb.append(
					":startDate||' '||:startTime), 'DD/MM/RRRR hh24:mi:ss')  BETWEEN start_dttm  AND end_dttm OR TO_DATE ((");
			sb.append(
					":endDate||' '||:endTime), 'DD/MM/RRRR hh24:mi:ss') BETWEEN start_dttm  AND end_dttm OR (start_dttm between TO_DATE((");
			sb.append(":startDate||' '||:startTime), 'DD/MM/RRRR hh24:mi:ss')  and TO_DATE ((");
			sb.append(":endDate||' '||:endTime), 'DD/MM/RRRR hh24:mi:ss') OR (end_dttm between TO_DATE ((");
			sb.append(":startDate||' '||:startTime), 'DD/MM/RRRR hh24:mi:ss')  and TO_DATE ((");
			sb.append(":endDate||' '||:endTime), 'DD/MM/RRRR hh24:mi:ss'))) ) ");

			String sql = sb.toString();

			paramMap.put("location", location);
			paramMap.put("startDate", startDate);
			paramMap.put("startTime", startTime);
			paramMap.put("endDate", endDate);
			paramMap.put("endTime", endTime);

			log.info(" *** addRainRecord SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				log.info("rs.getInt(1)::::::::::::::::" + rs.getInt(1));
				if (rs.getInt(1) == 0) {
					sb = new StringBuffer();
					/*
					 * INSERT INTO RAIN_RECORD ( RAIN_CNT, START_DTTM, END_DTTM, STATUS_CD,CAT_CD )
					 * VALUES ( RAIN_CNT_SEQ.nextval, TO_DATE((:start_date||' '||:start_time),
					 * 'DD/MM/RRRR hh24:mi:ss'), TO_DATE((:end_date||' '||:end_time), 'DD/MM/RRRR
					 * hh24:mi:ss'), 'A', :v_misc_type_code);
					 */

					sb.append(
							"INSERT INTO GBMS.RAIN_RECORD ( RAIN_CNT, START_DTTM, END_DTTM, STATUS_CD,CAT_CD, LOCATION_CD ) VALUES (");
					sb.append(
							" to_char(sysdate,'yyyymmdd')||lpad(RAIN_CNT_SEQ.nextval,4,0), TO_DATE((:startDate||' '||");
					sb.append(":startTime), 'DD/MM/RRRR hh24:mi:ss'), ");
					sb.append(
							"TO_DATE((:endDate||' '||:endTime), 'DD/MM/RRRR hh24:mi:ss'), 'A', :rainCategoryCd, :location)");

					sqlInsert = sb.toString();

					paramMap = new HashMap<String, Object>();

					paramMap.put("location", location);
					paramMap.put("startDate", startDate);
					paramMap.put("startTime", startTime);
					paramMap.put("endDate", endDate);
					paramMap.put("endTime", endTime);
					paramMap.put("rainCategoryCd", rainCategoryCd);

					log.info(" *** addRainRecord SQL *****" + sqlInsert + " paramMap " + paramMap.toString());
					int insertResult = namedParameterJdbcTemplate.update(sqlInsert, paramMap);
					log.info("RainRecords add block insertResult:::::::::" + insertResult);
					if (insertResult > 0) {
						/**
						 * Insert into the RainRecord_log table for audit log
						 */

						sb = new StringBuffer();

						sb.append(
								"INSERT INTO GBMS.rain_record_log (RAIN_CNT, LOG_DTTM, USER_ID, ACTION_CD, START_DTTM, END_DTTM, CAT_CD, LOCATION_CD)");
						sb.append(" VALUES (to_char(sysdate,'yyyymmdd')||lpad(RAIN_CNT_SEQ.currval,4,0),");
						sb.append(
								" SYSDATE, :userId, :actionCD, TO_DATE((:startDate||' '||:startTime), 'DD/MM/RRRR hh24:mi:ss'), ");
						sb.append(
								"TO_DATE((:endDate||' '||:endTime), 'DD/MM/RRRR hh24:mi:ss'), :rainCategoryCd, :location)");

						String sqlInsertLog = sb.toString();

						paramMap = new HashMap<String, Object>();
						paramMap.put("actionCD", actionCD);
						paramMap.put("location", location);
						paramMap.put("startDate", startDate);
						paramMap.put("startTime", startTime);
						paramMap.put("endDate", endDate);
						paramMap.put("endTime", endTime);
						paramMap.put("rainCategoryCd", rainCategoryCd);
						paramMap.put("userId", userId);

						log.info(" *** addRainRecord SQL *****" + sqlInsertLog + " paramMap " + paramMap.toString());
						int insertResultLog = namedParameterJdbcTemplate.update(sqlInsertLog, paramMap);
						if (insertResultLog == 0) {
							log.info("-----------RainRecords----------------Insert into log table failled ");
							throw new BusinessException("M50013");
						}

						result = true;
					} else {
						if (insertResult == 0) {

							throw new BusinessException("M50013");
						}
					}

				} else {
					// throw business exception "record overlaps with existing records"

					log.info("-----------RainRecords----------------Record overlaps with existing record");
					throw new BusinessException("M50014");
				}
			}

			log.info("END: *** addRainRecord Result *****" + result);
		} catch (BusinessException e) {
			log.error("Exception: addRainRecord ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.error("Exception: addRainRecord ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: addRainRecord ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: addRainRecord  DAO  END");
		}

		return result;
	}

	@Override
	public boolean isEditableRainRecord(String rainCntr, String mode) throws BusinessException {
		boolean result = false;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = "";
		StringBuffer sb = new StringBuffer();
		try {

			log.info("START: isEditableRainRecord  DAO  Start Obj " + " rainCntr:" + rainCntr + " mode:" + mode);

			/*
			 * SELECT (CASE WHEN (TO_NUMBER (SYSDATE - rr.start_dttm) <= (SELECT sp.VALUE
			 * FROM system_para "SP" WHERE sp.para_cd = 'RAIND') ) THEN 'TRUE' WHEN
			 * (TO_NUMBER (SYSDATE - rr.end_dttm) <= (SELECT sp.VALUE FROM system_para "SP"
			 * WHERE sp.para_cd = 'RAIND') ) THEN 'TRUE' ELSE 'FALSE' END ) "Result" FROM
			 * rain_record "RR"
			 * 
			 * WHERE rr.rain_cnt = '229426'
			 */

			sb.append("SELECT (CASE  WHEN (TO_NUMBER (SYSDATE - rr.start_dttm) <= (SELECT sp.VALUE ");
			sb.append(" FROM TOPS.system_para \"SP\" WHERE sp.para_cd = 'RAIND') ) THEN 'TRUE' ");
			sb.append("WHEN (TO_NUMBER (SYSDATE - rr.end_dttm) <= (SELECT sp.VALUE FROM TOPS.system_para \"SP\" ");
			sb.append("WHERE sp.para_cd = 'RAIND') ) THEN 'TRUE' ELSE 'FALSE' END ) \"Result\" ");
			sb.append("  FROM GBMS.rain_record \"RR\" WHERE rr.rain_cnt = :rainCntr ");

			sql = sb.toString();
			paramMap.put("rainCntr", rainCntr);

			log.info(" *** isEditableRainRecord SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while (rs.next()) {

				String resultStr = rs.getString(1);
				if (resultStr.equalsIgnoreCase("TRUE")) {
					result = true;
				} else {
					if (mode.equalsIgnoreCase("updateRecord") || mode.equalsIgnoreCase("update")) {
						// throw new BusinessException("M50011");
						return false;
					} else if (mode.equalsIgnoreCase("delete")) {
						// throw new BusinessException("M50010");
						return false;
					}

				}
			}

			log.info("END: *** isEditableRainRecord Result *****" + result);
		} catch (NullPointerException e) {
			log.error("Exception: isEditableRainRecord ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: isEditableRainRecord ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: isEditableRainRecord  DAO  END");
		}

		return result;
	}

	@Override
	public TableResult getRainRecordsByDate(String dateFrom, String dateTo, String location, Criteria criteria)
			throws BusinessException {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		TableResult tableResult = new TableResult();
		if (dateTo == null || dateTo.equals("")) {
			// as per the specs, if 'To Date' is not given in the screen 'To
			// Date' will take 'From Date'
			dateTo = dateFrom;
		}
		try {

			log.info("START: getRainRecordsByDate  DAO  Start Obj " + " dateFrom:" + dateFrom + " dateTo:" + dateTo
					+ " location:" + location);

			/*
			 * sql =
			 * "select RAIN_CNT,to_char(START_DTTM,'DD/MM/YYYY') start_date,to_char(START_DTTM,'hh24:mi:ss') start_time,to_char(END_DTTM,'DD/MM/YYYY') "
			 * +
			 * "end_date,to_char(END_DTTM,'hh24:mi:ss') end_time, mtc.MISC_TYPE_NM, mtc.MISC_TYPE_CD, mtc2.MISC_TYPE_NM LOCATION"
			 * + " from RAIN_RECORD rr, misc_type_code mtc,  misc_type_code mtc2" +
			 * " where mtc.MISC_TYPE_CD = rr.cat_cd and mtc2.MISC_TYPE_CD = rr.location_cd"
			 * + " and status_cd = 'A'" + " and mtc.CAT_CD = 'RAIN_CAT' " +
			 * "   AND	( (TRUNC(rr.START_DTTM) between  to_date ('" + dateFrom +
			 * "','DD/MM/RRRR') and " + "to_date('" + dateTo +
			 * "','DD/MM/RRRR')) OR (TRUNC(END_DTTM)   BETWEEN    " + "to_date ('" +
			 * dateFrom + "','DD/MM/RRRR') and to_date('" + dateTo + "','DD/MM/RRRR')) )  "
			 * ; if(!location.equalsIgnoreCase("All")){ sql = sql +
			 * " AND rr.location_cd = '" +location + "'"; } sql = sql +
			 * " ORDER BY START_DTTM";
			 */

			sb.append(
					" select a.RAIN_CNT,to_char(a.START_DTTM,'DD/MM/YYYY') start_date,to_char(a.START_DTTM,'hh24:mi:ss') start_time,to_char(a.END_DTTM,'DD/MM/YYYY') ");
			sb.append(
					" end_date,to_char(a.END_DTTM,'hh24:mi:ss') end_time, b.MISC_TYPE_NM, b.MISC_TYPE_CD, b2.MISC_TYPE_NM LOCATION_NAME,");
			sb.append(" b2.MISC_TYPE_CD LOCATION_CD ,JAS.USER_NAME, (a.end_dttm - a.start_dttm) * 24 TIMEDIFF ");
			sb.append(" from GBMS.rain_record a left outer join misc_type_code b on a.cat_cd = b.MISC_TYPE_CD");
			sb.append(" left outer join TOPS.misc_type_code b2 on a.location_cd = b2.MISC_TYPE_CD  ");
			sb.append(
					" left outer join GBMS.rain_record_log c on (a.rain_cnt = c.rain_cnt and a.start_dttm = c.start_dttm and a.end_dttm = c.end_dttm) ");
			sb.append(" LEFT JOIN ADM_USER JAS ON ");
			sb.append(" (C.USER_ID = JAS.USER_ACCT) ");
			sb.append(" where (b2.cat_cd = 'RAIN_LOC') and status_cd = 'A' ");
			sb.append(" and b.CAT_CD = 'RAIN_CAT' ");
			sb.append("   AND    ( (TRUNC(a.START_DTTM) between  to_date (:dateFrom");
			sb.append(",'DD/MM/RRRR') and ");
			sb.append("to_date(:dateTo,'DD/MM/RRRR')) OR (TRUNC(a.END_DTTM)   BETWEEN    ");
			sb.append("to_date (:dateFrom,'DD/MM/RRRR') and to_date(:dateTo,'DD/MM/RRRR')) )  ");
			if (!location.equalsIgnoreCase("All")) {
				sb.append(" AND a.location_cd = :location");
				paramMap.put("location", location);
			}
			sb.append(
					"  group by a.RAIN_CNT,to_char(a.START_DTTM,'DD/MM/YYYY') ,to_char(a.START_DTTM,'hh24:mi:ss') , ");
			sb.append(" to_char(a.END_DTTM,'DD/MM/YYYY')  ,to_char(a.END_DTTM,'hh24:mi:ss') , ");
			sb.append(
					" b.MISC_TYPE_NM, b.MISC_TYPE_CD, b2.MISC_TYPE_NM , b2.MISC_TYPE_CD  , JAS.USER_NAME , (a.end_dttm - a.start_dttm) * 24  ");

			sb.append(" ORDER BY RAIN_CNT DESC ");
			sql = sb.toString();
			log.info("Sql Query:::::::::" + sql);

			paramMap.put("dateTo", dateTo);
			paramMap.put("dateFrom", dateFrom);

			String sql1 = sql;

			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql1 + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
			}
			if (criteria.isPaginated()) {
				sql1 = CommonUtil.getPaginatedSql(sql1, criteria.getStart(), criteria.getLimit());
			}

			log.info(" *** getRainRecordsByDate SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);

			while (rs.next()) {
				RainRecordsValueObject rvo = new RainRecordsValueObject();

				String rainCnt = CommonUtility.deNull(rs.getString(1));
				String startDate = CommonUtility.deNull(rs.getString(2));
				String startTime = CommonUtility.deNull(rs.getString(3));
				String endDate = CommonUtility.deNull(rs.getString(4));
				String endTime = CommonUtility.deNull(rs.getString(5));
				String rainCategoryText = CommonUtility.deNull(rs.getString(6));
				String rainCategoryCode = CommonUtility.deNull(rs.getString(7));
				String rainLocationText = CommonUtility.deNull(rs.getString(8));
				String rainLocationCode = CommonUtility.deNull(rs.getString(9));
				String lastModifiedBy = CommonUtility.deNull(rs.getString(10));
				double timeDiff = rs.getDouble(11);
				CommonUtility.deNull(rs.getString(11));
				log.info("rainCnt" + rainCnt);
				log.info("startDate" + startDate);
				log.info("startTime" + startTime);
				log.info("endDate" + endDate);
				log.info("rainCategoryText" + rainCategoryText);
				log.info("rainCategoryCode" + rainCategoryCode);

				rvo.setRainCNT(rainCnt);
				rvo.setStartDate(startDate);
				rvo.setStartTime(startTime);
				rvo.setEndDate(endDate);
				rvo.setEndTime(endTime);
				rvo.setSelectedRaincategorytext(rainCategoryText);
				rvo.setSelectedRaincategorycode(rainCategoryCode);
				rvo.setSelectedRainLocationText(rainLocationText);
				rvo.setSelectedRainLocationCode(rainLocationCode);
				rvo.setLastModifiedBy(lastModifiedBy);
				rvo.setTimeDifference(timeDiff);
				log.info("----valueobject created in ejb------------------------------");
				// adding RainRecordsValueObject to the list

				topsModel.put((Serializable) rvo);

			}

			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			log.info("END: *** getRainRecordsByDate Result *****" + tableResult.toString());
		} catch (NullPointerException e) {
			log.error("Exception: getRainRecordsByDate ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getRainRecordsByDate ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getRainRecordsByDate  DAO  END");
		}

		return tableResult;
	}

	@Override
	public Map<String, String> getRainLocations() throws BusinessException {
		Map<String, String> rainLocationMap = null;
		String sql = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {

			log.info("START: getRainLocations  DAO  Start Obj ");
			/*
			 * select MISC_TYPE_CD, MISC_TYPE_nm from misc_type_code where CAT_CD =
			 * 'RAIN_CAT'
			 */
			sql = "select MISC_TYPE_CD, MISC_TYPE_nm from TOPS.misc_type_code where CAT_CD = 'RAIN_LOC' and MISC_TYPE_CD <> 'A' ORDER BY MISC_TYPE_nm";

			log.info(" *** getRainLocations SQL *****" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			rainLocationMap = new HashMap<String, String>();
			while (rs.next()) {
				rainLocationMap.put(rs.getString(1), rs.getString(2));

			}

			log.info("END: *** getRainLocations Result *****" + rainLocationMap.toString());
		} catch (NullPointerException e) {
			log.error("Exception: getRainLocations ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getRainLocations ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getRainLocations  DAO  END");
		}

		return rainLocationMap;

	}

	@Override
	public Map<String, String> getRainCategories() throws BusinessException {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, String> rainCatMap = null;
		String sql = "";
		SqlRowSet rs = null;
		try {

			log.info("START: getRainCategories  DAO  Start Obj ");

			/*
			 * select MISC_TYPE_CD, MISC_TYPE_nm from misc_type_code where CAT_CD =
			 * 'RAIN_CAT'
			 */
			sql = "select MISC_TYPE_CD, MISC_TYPE_nm from TOPS.misc_type_code where CAT_CD = 'RAIN_CAT' ORDER BY MISC_TYPE_nm";

			log.info(" *** getRainCategories SQL *****" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			rainCatMap = new HashMap<String, String>();
			while (rs.next()) {
				rainCatMap.put(rs.getString(1), rs.getString(2));

			}

			log.info("END: *** getRainCategories Result *****" + rainCatMap.toString());
		} catch (NullPointerException e) {
			log.error("Exception: getRainCategories ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getRainCategories ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getRainCategories  DAO  END");
		}

		return rainCatMap;

	}

	@Override
	public Integer getAllRainRecordsCount() throws BusinessException {
		String sql = "";
		int totalRainRecords = 0;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getAllRainRecordsCount  DAO  Start Obj ");

			/* select count(*) from rain_record where status_cd='A' */
			sql = "select count(*) from GBMS.rain_record where status_cd='A' ";

			log.info(" *** getAllRainRecordsCount SQL *****" + sql);

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				totalRainRecords = rs.getInt(1);
			}
			log.info("END: *** getAllRainRecordsCount Result *****" + new Integer(totalRainRecords));
		} catch (NullPointerException e) {
			log.error("Exception: getAllRainRecordsCount ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getAllRainRecordsCount ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getAllRainRecordsCount  DAO  END");
		}

		return new Integer(totalRainRecords);
	}

	@Override
	public Integer getRainRecordEditableDays() throws BusinessException {

		String editableDays = "";
		String sql = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {

			log.info("START: getRainRecordEditableDays  DAO  Start Obj ");

			sql = "SELECT VALUE FROM TOPS.SYSTEM_PARA WHERE PARA_CD='RAIND'";

			log.info(" *** getRainRecordEditableDays SQL *****" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			rs.next();
			editableDays = rs.getString(1);

			log.info("END: *** getRainRecordEditableDays Result *****" + new Integer(editableDays));
		} catch (NullPointerException e) {
			log.error("Exception: getRainRecordEditableDays ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getRainRecordEditableDays ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getRainRecordEditableDays  DAO  END");
		}

		return new Integer(editableDays);

	}

	@Override
	public Integer getPaginationRecordCount() throws BusinessException {
		String pageRecNo = "";
		String sql = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {

			log.info("START: getPaginationRecordCount  DAO  Start Obj ");

			sql = "SELECT VALUE FROM TOPS.TEXT_PARA WHERE PARA_CD='VPRRMNO'";

			log.info(" *** getCargoCategoryCode_CargoCategoryName SQL *****" + sql);

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			rs.next();
			pageRecNo = rs.getString(1);

			log.info("END: *** getPaginationRecordCount Result *****" + new Integer(pageRecNo));
		} catch (NullPointerException e) {
			log.error("Exception: getPaginationRecordCount ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getPaginationRecordCount ", e);
			throw new BusinessException("M4201");
		} finally {

			log.info("END: getPaginationRecordCount  DAO  END");
		}

		return new Integer(pageRecNo);

	}
}
