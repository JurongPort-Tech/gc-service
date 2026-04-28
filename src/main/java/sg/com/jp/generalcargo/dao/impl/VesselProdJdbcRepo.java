package sg.com.jp.generalcargo.dao.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.VesselProdRepo;
import sg.com.jp.generalcargo.dao.VesselProdSurchargeRepo;
import sg.com.jp.generalcargo.domain.BerthUtilisationValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.CustVslProdValueObject;
import sg.com.jp.generalcargo.domain.PubVslProdValueObject;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VslProductivityValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("vesselProdRepo")
public class VesselProdJdbcRepo implements VesselProdRepo {

	private static final Log log = LogFactory.getLog(VesselProdJdbcRepo.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private VesselProdSurchargeRepo vesselProdSurcharge;

	private static final String dateFormat = "dd/MM/yyyy";

	// ejb.sessionBeans.reports.VesselProd -->VesselProdEjb
	/**
	 * This function rounds the double value to the desired no. of decimal places
	 *
	 * @param val
	 * @param decimalPlaces
	 * @return double rounded 2 decimal places
	 */
	private double roundDouble(double val, int decimalPlaces) {
		try {
			log.info("START: roundDouble  DAO  Start Obj " + " val:" + val + " decimalPlaces:" + decimalPlaces);
			BigDecimal bd = new BigDecimal(val);
			bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
			return bd.doubleValue();
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}

	// ejb.sessionBeans.reports.VesselProd -->VesselProdEjb

	/**
	 * Creates the Berth Utilisation Report
	 *
	 * @param dateFrom
	 * @param dateTo
	 * @param cntrTonnParam
	 * @return List of objects that are displayed in the report
	 * @throws RemoteException
	 * @throws BusinessException
	 */
	@Override
	public TableResult getBerthUtilisationRpt(java.util.Date dateFrom, java.util.Date dateTo, String cntrTonnParam,
			Criteria criteria) throws BusinessException {

		BerthUtilisationValueObject berthUtilVO = null;
		// calculate the values for the summary table and put the values in
		// ArrayList

		Map<String, String> paramMap = new HashMap<String, String>();

		SqlRowSet rsA1 = null;
		SqlRowSet rsA2 = null;
		SqlRowSet rsB = null;
		SqlRowSet rsC1 = null;
		SqlRowSet rsC2 = null;
		SqlRowSet rsD1 = null;
		SqlRowSet rsD2 = null;
		SqlRowSet rsE1 = null;
		SqlRowSet rsE2 = null;
		SqlRowSet rsE3 = null;

		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		TableResult tableResult = new TableResult();

		String berthNumber = null;
		String berthLength = null;
		int vesselCount = 0;
		double totDurVslHr = 0.0;
		double totDurVslDay = 0.0;
		double cisTonnage = 0.0;
		double cesTonnage = 0.0;
		double tonnageGC = 0.0;
		double tonnageBC = 0.0;
		double tonnageTotal = 0.0;
		double tonnageCtr = 0.0;
		double tonnageCtrDecWt = 0.0;
		double tonnageCtrImp = 0.0;
		double tonnageCtrExp = 0.0;
		double grossWt = 0.0;
		double esnWt = 0.0;
		String vvCode = null;
		try {

			log.info("START: getBerthUtilisationRpt  DAO  Start dateFrom " + dateFrom + " dateTo" + dateTo
					+ " cntrTonnParam" + cntrTonnParam);

			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

			String fromDate = sdf.format(dateFrom);
			String toDate = sdf.format(dateTo);
			String sql_get_berth_nbr = "SELECT BERTH_NBR, BERTHABLE_LENGTH FROM BERTHING_LENGTH ORDER BY DISPLAY_ORDER_NBR";

			log.info(" getBerthUtilisationRpt  DAO  SQL " + sql_get_berth_nbr.toString());
			String sqls = sql_get_berth_nbr.toString();
			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sqls + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
			}
			if (criteria.isPaginated() && criteria.getPredicates().get("excel") == "false") {
				sqls = CommonUtil.getPaginatedSql(sqls, criteria.getStart(), criteria.getLimit());
			}

			rsA1 = namedParameterJdbcTemplate.queryForRowSet(sqls, paramMap);
			while (rsA1.next()) {
				vesselCount = 0;
				berthUtilVO = new BerthUtilisationValueObject();
				// Results from Query A1
				berthNumber = CommonUtility.deNull(rsA1.getString("BERTH_NBR"));
				berthLength = CommonUtility.deNull(rsA1.getString("BERTHABLE_LENGTH"));

				StringBuffer sb = new StringBuffer();

				sb.append("SELECT ");
				sb.append("	count(vc.VV_CD) vessel_count ");
				sb.append("FROM ");
				sb.append("	BERTHING B, ");
				sb.append("	VESSEL_CALL VC ");
				sb.append("WHERE ");
				sb.append("	(B.BERTH_NBR = :berthNumber ");
				sb.append(
						"	OR substr(B.BERTH_NBR, 1, 1)|| SUBSTR(B.BERTH_NBR, 2, INSTR(B.BERTH_NBR, '/')-2) = :berthNumber ");
				sb.append(
						"	OR substr(B.BERTH_NBR, 1, 1)|| SUBSTR(B.BERTH_NBR, INSTR(B.BERTH_NBR, '/')+ 1) = :berthNumber) ");
				sb.append("	AND VC.VV_CD = B.VV_CD ");
				sb.append("	AND VC.VV_STATUS_IND = 'CL' ");
				sb.append("	AND vc.TERMINAL IN('GB', 'CT') ");
				sb.append("	AND B.SHIFT_IND = 1 ");
				sb.append(
						"	AND b.ATB_DTTM BETWEEN to_date(:fromDate || ' 000000', 'DD/MM/RRRR HH24MISS') AND to_date(:toDate || ' 235959', 'DD/MM/RRRR HH24MISS') ");
				sb.append("GROUP BY ");
				sb.append("	:berthNumber");
				String sql_get_berth_length = sb.toString();

				log.info(" getBerthUtilisationRpt  DAO  SQL " + sql_get_berth_length.toString());
				// Execute Query A2
				paramMap.put("berthNumber", berthNumber);
				paramMap.put("fromDate", fromDate);
				paramMap.put("toDate", toDate);

				/*
				 * pstmtA2.setString(1, berthNumber); pstmtA2.setString(2, berthNumber);
				 * pstmtA2.setString(3, berthNumber); pstmtA2.setString(4, berthNumber);
				 * pstmtA2.setString(5, berthNumber); pstmtA2.setString(6, fromDate);
				 * pstmtA2.setString(7, toDate); pstmtA2.setString(8, berthNumber);
				 */

				rsA2 = namedParameterJdbcTemplate.queryForRowSet(sql_get_berth_length.toString(), paramMap);
				// Results from Query A2
				while (rsA2.next()) {
					vesselCount = rsA2.getInt("VESSEL_COUNT");
				}

				StringBuffer sb1 = new StringBuffer();
				sb1.append("SELECT ");
				sb1.append("	vc.VV_CD, ");
				sb1.append("	(( ");
				sb1.append("	SELECT ");
				sb1.append("		MAX(ATU_DTTM) ");
				sb1.append("	FROM ");
				sb1.append("		berthing b1 ");
				sb1.append("	WHERE ");
				sb1.append("		b1.vv_cd = b.vv_cd) -b.ATB_DTTM)* 24 total_dur_vessel_hr, ");
				sb1.append("	(( ");
				sb1.append("	SELECT ");
				sb1.append("		MAX(ATU_DTTM) ");
				sb1.append("	FROM ");
				sb1.append("		berthing b1 ");
				sb1.append("	WHERE ");
				sb1.append("		b1.vv_cd = b.vv_cd) -b.ATB_DTTM) total_dur_vessel_day ");
				sb1.append("FROM ");
				sb1.append("	BERTHING B, ");
				sb1.append("	VESSEL_CALL VC ");
				sb1.append("WHERE ");
				sb1.append("	VC.VV_CD = B.VV_CD ");
				sb1.append("	AND VC.VV_STATUS_IND = 'CL' ");
				sb1.append("	AND B.SHIFT_IND = 1 ");
				sb1.append("	AND vc.TERMINAL IN('GB', 'CT') ");
				sb1.append("	AND b.BERTH_NBR = :berthNumber ");
				sb1.append(
						"	AND b.ATB_DTTM BETWEEN to_date(:fromDate || ' 000000', 'DD/MM/RRRR HH24MISS') AND to_date(:toDate || ' 235959', 'DD/MM/RRRR HH24MISS') ");
				sb1.append("UNION ");
				sb1.append("SELECT ");
				sb1.append("	vc.VV_CD, ");
				sb1.append("	((( ");
				sb1.append("	SELECT ");
				sb1.append("		MAX(ATU_DTTM) ");
				sb1.append("	FROM ");
				sb1.append("		berthing b1 ");
				sb1.append("	WHERE ");
				sb1.append("		b1.vv_cd = b.vv_cd) -b.ATB_DTTM)* 24)/ 2 total_dur_vessel_hr, ");
				sb1.append("	(( ");
				sb1.append("	SELECT ");
				sb1.append("		MAX(ATU_DTTM) ");
				sb1.append("	FROM ");
				sb1.append("		berthing b1 ");
				sb1.append("	WHERE ");
				sb1.append("		b1.vv_cd = b.vv_cd) -b.ATB_DTTM)/ 2 total_dur_vessel_day ");
				sb1.append("FROM ");
				sb1.append("	BERTHING B, ");
				sb1.append("	VESSEL_CALL VC ");
				sb1.append("WHERE ");
				sb1.append("	VC.VV_CD = B.VV_CD ");
				sb1.append("	AND VC.VV_STATUS_IND = 'CL' ");
				sb1.append("	AND vc.TERMINAL IN('GB', 'CT') ");
				sb1.append("	AND B.SHIFT_IND = 1 ");
				sb1.append(
						"	AND (substr(B.BERTH_NBR, 1, 1)|| SUBSTR(B.BERTH_NBR, 2, INSTR(B.BERTH_NBR, '/')-2) = :berthNumber ");
				sb1.append(
						"	OR substr(B.BERTH_NBR, 1, 1)|| SUBSTR(B.BERTH_NBR, INSTR(B.BERTH_NBR, '/')+ 1) = :berthNumber) ");
				sb1.append(
						"	AND b.ATB_DTTM BETWEEN to_date(:fromDate || ' 000000', 'DD/MM/RRRR HH24MISS') AND to_date(:toDate || ' 235959', 'DD/MM/RRRR HH24MISS')");

				// Execute Query B
				paramMap.put("berthNumber", berthNumber);
				paramMap.put("fromDate", fromDate);
				paramMap.put("toDate", toDate);

				/*
				 * pstmtB.setString(1, berthNumber); pstmtB.setString(2, fromDate);
				 * pstmtB.setString(3, toDate); pstmtB.setString(4, berthNumber);
				 * pstmtB.setString(5, berthNumber); pstmtB.setString(6, berthNumber);
				 * pstmtB.setString(7, berthNumber); pstmtB.setString(8, fromDate);
				 * pstmtB.setString(9, toDate);
				 */
				log.info(" getBerthUtilisationRpt  DAO  SQL " + sb1.toString());
				rsB = namedParameterJdbcTemplate.queryForRowSet(sb1.toString(), paramMap);

				totDurVslHr = 0.0;
				totDurVslDay = 0.0;
				cisTonnage = 0.0;
				cesTonnage = 0.0;
				grossWt = 0.0;
				esnWt = 0.0;
				tonnageCtr = 0.0;
				tonnageCtrDecWt = 0.0;
				tonnageCtrImp = 0.0;
				tonnageCtrExp = 0.0;
				boolean rsBVVCode = true;

				while (rsB.next()) {
					rsBVVCode = false;
					totDurVslHr += rsB.getDouble("TOTAL_DUR_VESSEL_HR");
					totDurVslDay += rsB.getDouble("TOTAL_DUR_VESSEL_DAY");
					// Get VV code
					vvCode = rsB.getString("VV_CD");

					StringBuffer sb2 = new StringBuffer();
					sb2.append("SELECT ");
					sb2.append(
							"	DECODE(INSTR(b.berth_nbr, '/'), 0, sum(cis.TONNAGE), (sum(cis.TONNAGE)/ 2)) cis_tonnage ");
					sb2.append("FROM ");
					sb2.append("	cargo_import_summary cis, ");
					sb2.append("	Berthing b ");
					sb2.append("WHERE ");
					sb2.append("	cis.VAR_NBR = b.VV_CD ");
					sb2.append("	AND b.vv_cd = :vvCode ");
					sb2.append("	AND B.SHIFT_IND = 1 ");
					sb2.append("	AND cis.VAR_NBR = :vvCode ");
					sb2.append("GROUP BY ");
					sb2.append("	b.berth_nbr");

					String sql_get_cis_tonnage = sb2.toString();

					// Execute Query C1
					paramMap.put("vvCode", vvCode);

					log.info(" getBerthUtilisationRpt  DAO  SQL " + sql_get_cis_tonnage.toString());
					rsC1 = namedParameterJdbcTemplate.queryForRowSet(sql_get_cis_tonnage.toString(), paramMap);

					while (rsC1.next()) {
						// Get cesTonnage
						cisTonnage += rsC1.getDouble("CIS_TONNAGE");
					}

					StringBuffer sb4 = new StringBuffer();
					sb4.append("SELECT ");
					sb4.append(
							"	DECODE(INSTR(b.berth_nbr, '/'), 0, sum(ces.TONNAGE), (sum(ces.TONNAGE)/ 2)) ces_TONNAGE ");
					sb4.append("FROM ");
					sb4.append("	cargo_export_summary ces, ");
					sb4.append("	Berthing b ");
					sb4.append("WHERE ");
					sb4.append("	ces.VAR_NBR = b.vv_cd ");
					sb4.append("	AND b.vv_cd = :vvCode ");
					sb4.append("	AND b.shift_ind = 1 ");
					sb4.append("	AND ces.VAR_NBR = :vvCode ");
					sb4.append("GROUP BY ");
					sb4.append("	b.berth_nbr");

					String sql_get_ces_tonnage = sb4.toString();

					// Execute Query C2
					paramMap.put("vvCode", vvCode);

					log.info(" getBerthUtilisationRpt  DAO  SQL " + sql_get_ces_tonnage.toString());
					rsC2 = namedParameterJdbcTemplate.queryForRowSet(sql_get_ces_tonnage.toString(), paramMap);

					while (rsC2.next()) {
						// Get cesTonnage
						cesTonnage += rsC2.getDouble("CES_TONNAGE");
					}

					tonnageGC = cisTonnage + cesTonnage;

					StringBuffer sb5 = new StringBuffer();
					sb5.append("SELECT ");
					sb5.append(
							"	DECODE(INSTR(b.berth_nbr, '/'), 0, sum(bmd.GROSS_WT)/ 1000, ((sum(bmd.GROSS_WT)/ 1000)/ 2)) gross_wt ");
					sb5.append("FROM ");
					sb5.append("	BULK_MANIFEST_DETAILS bmd, ");
					sb5.append("	Berthing b ");
					sb5.append("WHERE ");
					sb5.append("	b.vv_cd = bmd.var_nbr ");
					sb5.append("	AND b.shift_ind = 1 ");
					sb5.append("	AND bmd.VAR_NBR = :vvCode ");
					sb5.append("GROUP BY ");
					sb5.append("	berth_nbr");

					String sql_get_gross_wt = sb5.toString();

					// Execute Query D1
					paramMap.put("vvCode", vvCode);
					log.info(" getBerthUtilisationRpt  DAO  SQL " + sql_get_gross_wt.toString());
					rsD1 = namedParameterJdbcTemplate.queryForRowSet(sql_get_gross_wt.toString(), paramMap);

					while (rsD1.next()) {
						// Get grossWeight
						grossWt += rsD1.getDouble("GROSS_WT");
					}

					StringBuffer sb6 = new StringBuffer();
					sb6.append("SELECT ");
					sb6.append(
							"	DECODE(INSTR(berth_nbr, '/'), 0, sum(bed.ESN_WT)/ 1000, ((sum(bed.ESN_WT)/ 1000)/ 2)) ESN_WT ");
					sb6.append("FROM ");
					sb6.append("	BULK_ESN be, ");
					sb6.append("	BULK_ESN_DETAILS bed, ");
					sb6.append("	Berthing b ");
					sb6.append("WHERE ");
					sb6.append("	be.ESN_ASN_NBR = bed.ESN_ASN_NBR ");
					sb6.append("	AND b.shift_ind = 1 ");
					sb6.append("	AND b.vv_cd = be.OUT_VOY_VAR_NBR ");
					sb6.append("	AND be.OUT_VOY_VAR_NBR = :vvCode ");
					sb6.append("GROUP BY ");
					sb6.append("	berth_nbr");

					String sql_get_esn_wt = sb6.toString();

					// Execute Query D2
					paramMap.put("vvCode", vvCode);
					log.info(" getBerthUtilisationRpt  DAO  SQL " + sql_get_esn_wt.toString());
					rsD2 = namedParameterJdbcTemplate.queryForRowSet(sql_get_esn_wt.toString(), paramMap);

					while (rsD2.next()) {
						// Get esnWeight
						esnWt += rsD2.getDouble("ESN_WT");
					}

					tonnageBC = grossWt + esnWt;

					StringBuffer sb7 = new StringBuffer();
					sb7.append("SELECT ");
					sb7.append(
							"	DECODE(INSTR(b.berth_nbr, '/'), 0, sum(c.DECLR_WT / 1000 ), (sum(c.DECLR_WT / 1000 )/ 2)) DECLR_WT ");
					sb7.append("FROM ");
					sb7.append("	cntr c, ");
					sb7.append("	Berthing b ");
					sb7.append("WHERE ");
					sb7.append("	b.VV_CD = :vvCode ");
					sb7.append("	AND (c.DISC_VV_CD = :vvCode ");
					sb7.append("	OR c.LOAD_VV_CD = :vvCode) ");
					sb7.append("	AND c.TXN_STATUS <> 'D' ");
					sb7.append("	AND b.shift_ind = 1 ");
					sb7.append("GROUP BY ");
					sb7.append("	b.berth_nbr");

					String sql_get_declr_wt = sb7.toString();

					// Execute Query E1
					paramMap.put("vvCode", vvCode);

					log.info(" getBerthUtilisationRpt  DAO  SQL " + sql_get_declr_wt.toString());
					rsE1 = namedParameterJdbcTemplate.queryForRowSet(sql_get_declr_wt.toString(), paramMap);
					while (rsE1.next()) {
						tonnageCtrDecWt += rsE1.getDouble("DECLR_WT");
					}

					// if containerTonnage is Conversion(GBO/MKT)
					if (!("ACTUAL".equalsIgnoreCase(cntrTonnParam))) {

						StringBuffer sb8 = new StringBuffer();
						sb8.append("select case   when (:cntrTonnParam = 'Conversion(GBO)') then ");
						sb8.append(" DECODE(INSTR(b.berth_nbr,'/'),0,(((cntr_i_s.EMPTY_20 * (SELECT VALUE FROM ");
						sb8.append(" SYSTEM_PARA WHERE PARA_CD = 'GB20E') + cntr_i_s.EMPTY_40 * (SELECT VALUE FROM ");
						sb8.append(" SYSTEM_PARA WHERE PARA_CD = 'GB40E') + cntr_i_s.LADEN_20 * (SELECT VALUE FROM ");
						sb8.append(" SYSTEM_PARA WHERE PARA_CD = 'GB20L') + cntr_i_s.LADEN_40 * (SELECT VALUE FROM ");
						sb8.append(" SYSTEM_PARA WHERE PARA_CD = 'GB40L') ))),(((cntr_i_s.EMPTY_20 * (SELECT ");
						sb8.append(" VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'GB20E') + cntr_i_s.EMPTY_40 * (SELECT ");
						sb8.append(" VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'GB40E') + cntr_i_s.LADEN_20 * (SELECT ");
						sb8.append(" VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'GB20L') + cntr_i_s.LADEN_40 * (SELECT ");
						sb8.append(" VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'GB40L')))/2)) when (:cntrTonnParam = ");
						sb8.append(" 'Conversion(MKT)') then DECODE(INSTR(b.berth_nbr,'/'),0,((( cntr_i_s.EMPTY_20 * ");
						sb8.append(" (SELECT VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'MK20E') + cntr_i_s.EMPTY_40 * ");
						sb8.append(" (SELECT VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'MK40E') + cntr_i_s.LADEN_20 * ");
						sb8.append(" (SELECT VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'MK20L') + cntr_i_s.LADEN_40 * ");
						sb8.append(" (SELECT VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'MK40L') ))),((( ");
						sb8.append(
								" cntr_i_s.EMPTY_20 * (SELECT VALUE FROM TOPS.SYSTEM_PARA WHERE PARA_CD = 'MK20E') ");
						sb8.append(" + cntr_i_s.EMPTY_40 * (SELECT VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'MK40E') + ");
						sb8.append(" cntr_i_s.LADEN_20 * (SELECT VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'MK20L') + ");
						sb8.append(" cntr_i_s.LADEN_40 * (SELECT VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'MK40L') ) ");
						sb8.append(" )/2)) end as conn_imp from cntr_import_summary cntr_i_s,Berthing b where ");
						sb8.append(" cntr_i_s.VV_CD = :vvCode AND b.shift_ind = 1 and b.VV_CD = :vvCode");

						String sql_get_conn_imp = sb8.toString();

						paramMap.put("cntrTonnParam", cntrTonnParam);
						paramMap.put("vvCode", vvCode);
						log.info(" getBerthUtilisationRpt  DAO  SQL " + sql_get_conn_imp.toString());
						rsE2 = namedParameterJdbcTemplate.queryForRowSet(sql_get_conn_imp.toString(), paramMap);
						while (rsE2.next()) {
							tonnageCtrImp += rsE2.getDouble("CONN_IMP");
						}

						StringBuffer sb9 = new StringBuffer();
						sb9.append("select case   when (:cntrTonnParam = 'Conversion(GBO)') then ");
						sb9.append("  DECODE(INSTR(b.berth_nbr,'/'),0,(((cntr_e_s.EMPTY_20 * (SELECT VALUE FROM ");
						sb9.append("  SYSTEM_PARA WHERE PARA_CD = 'GB20E') + cntr_e_s.EMPTY_40 * (SELECT VALUE FROM ");
						sb9.append(" SYSTEM_PARA WHERE PARA_CD = 'GB40E') + cntr_e_s.LADEN_20 * (SELECT VALUE FROM ");
						sb9.append(" SYSTEM_PARA WHERE PARA_CD = 'GB20L') + cntr_e_s.LADEN_40 * (SELECT VALUE FROM ");
						sb9.append(" SYSTEM_PARA WHERE PARA_CD = 'GB40L') ) )),(((cntr_e_s.EMPTY_20 * (SELECT ");
						sb9.append(" VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'GB20E') + cntr_e_s.EMPTY_40 * (SELECT ");
						sb9.append(" VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'GB40E') + cntr_e_s.LADEN_20 * (SELECT ");
						sb9.append(" VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'GB20L') + cntr_e_s.LADEN_40 * (SELECT ");
						sb9.append(" VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'GB40L') ) )/2)) when (:cntrTonnParam ");
						sb9.append("  = 'Conversion(MKT)') then DECODE(INSTR(b.berth_nbr,'/'),0,((( ");
						sb9.append(" cntr_e_s.EMPTY_20 * (SELECT VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'MK20E') + ");
						sb9.append(" cntr_e_s.EMPTY_40 * (SELECT VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'MK40E') + ");
						sb9.append(" cntr_e_s.LADEN_20 * (SELECT VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'MK20L') + ");
						sb9.append(" cntr_e_s.LADEN_40 * (SELECT VALUE FROM SYSTEM_PARA WHERE PARA_CD = 'MK40L') ) ");
						sb9.append(" )),((( cntr_e_s.EMPTY_20 * (SELECT VALUE FROM TOPS.SYSTEM_PARA WHERE ");
						sb9.append(" PARA_CD = 'MK20E') + cntr_e_s.EMPTY_40 * (SELECT VALUE FROM SYSTEM_PARA WHERE ");
						sb9.append(" PARA_CD = 'MK40E') + cntr_e_s.LADEN_20 * (SELECT VALUE FROM SYSTEM_PARA WHERE ");
						sb9.append(" PARA_CD = 'MK20L') + cntr_e_s.LADEN_40 * (SELECT VALUE FROM SYSTEM_PARA WHERE ");
						sb9.append(" PARA_CD = 'MK40L') ) )/2)) end as conn_exp from cntr_export_summary ");
						sb9.append(" cntr_e_s,Berthing b where cntr_e_s.VV_CD = :vvCode ");
						sb9.append(" AND b.shift_ind = 1 and   b.VV_CD = :vvCode");

						String sql_get_conn_exp = sb9.toString();

						// Execute Query E3
						paramMap.put("cntrTonnParam", cntrTonnParam);
						paramMap.put("vvCode", vvCode);

						log.info(" getBerthUtilisationRpt  DAO  SQL " + sql_get_conn_exp.toString());
						rsE3 = namedParameterJdbcTemplate.queryForRowSet(sql_get_conn_exp.toString(), paramMap);
						while (rsE3.next()) {
							tonnageCtrExp += rsE3.getDouble("CONN_EXP");
						}

					}
				}

				if (rsBVVCode == true) {
					tonnageGC = 0.0;
					tonnageBC = 0.0;
					tonnageCtr = 0.0;
				}

				if ("ACTUAL".equalsIgnoreCase(cntrTonnParam)) {
					// Amended by Punitha on 25/11/2008
					// tonnageCtr = tonnageCtrDecWt + tonnageGC;
					tonnageCtr = tonnageCtrDecWt;
					tonnageTotal = tonnageGC + tonnageBC + tonnageCtr;// calculation of containerTonnage is Actual
				} else {
					tonnageCtr = tonnageCtrDecWt + tonnageCtrImp + tonnageCtrExp;
					tonnageTotal = tonnageCtr + tonnageBC + tonnageGC;// calculation of containerTonnage is Conversion
				}
				// add all the values to Value Object
				berthUtilVO.setBerthNumber(berthNumber);
				berthUtilVO.setBerthLength(berthLength);
				berthUtilVO.setTonnageGC(roundDouble(tonnageGC, 2));
				berthUtilVO.setTonnageBC(roundDouble(tonnageBC, 2));
				berthUtilVO.setTonnageCNTR(roundDouble(tonnageCtr, 2));
				berthUtilVO.setTotalTonnage(roundDouble(tonnageTotal, 2));
				berthUtilVO.setVesselCount(vesselCount);
				berthUtilVO.setTotalDurationOfVesselStay(roundDouble(totDurVslHr, 2));
				berthUtilVO.setAverageTonnageHandledPerHour(roundDouble(tonnageTotal / totDurVslHr, 2));
				berthUtilVO.setAverageTonnageHandledPerDay(roundDouble(tonnageTotal / totDurVslDay, 2));
				berthUtilVO.setAverageTonnageHandledPerVessel(roundDouble(tonnageTotal / vesselCount, 2));

				topsModel.put((Serializable) berthUtilVO);

			}

			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			log.info("END: *** getBerthUtilisationRpt Result *****" + topsModel.toString());
		} catch (NullPointerException e) {
			log.error("Exception getBerthUtilisationRpt :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getBerthUtilisationRpt :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBerthUtilisationRpt  DAO  END");
		}
		return tableResult;
	}

	// ejb.sessionBeans.reports.VesselProd -->VesselProdEjb

	/*
	 * menthod to get the number of records for paging
	 */
	@Override
	public Integer getPaginationRecordsNumber() throws BusinessException {
		String pageRecNo = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getPaginationRecordsNumber  DAO  Start  ");

			String sql = "SELECT VALUE FROM TEXT_PARA WHERE PARA_CD='VPBURNO'";

			log.info(" getPaginationRecordsNumber  DAO  SQL " + sql.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			rs.next();
			pageRecNo = rs.getString(1);

			log.info(" getPaginationRecordsNumber  DAO  Result" + pageRecNo.toString());
		} catch (NullPointerException e) {
			log.error("Exception getPaginationRecordsNumber :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getPaginationRecordsNumber :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPaginationRecordsNumber  DAO  END");
		}

		return new Integer(pageRecNo);

	}

	@Override
	public TableResult getVesselProdReport(Date fromDt, Date toDt, String displayView, String[] category,
			String tonnage, String rainRecord, String vesselType, String dateType, Criteria criteria)
			throws BusinessException {

		return getVesselProdRpt(fromDt, toDt, displayView, category, tonnage, rainRecord, vesselType, dateType,
				criteria);
	}

	public TableResult getVesselProdRpt(Date dateFrom, Date dateTo, String rptType, String[] vslCategory,
			String cntrTonnage, String rainRcrds, String vesselType, String dateType, Criteria criteria)
			throws BusinessException {
		log.info("Into VesselProdEjb.getVesselProdRpt***************** " + "dateFrom" + dateFrom + "dateTo" + dateTo
				+ "rptType" + CommonUtility.deNull(rptType) + "cntrTonnage" + CommonUtility.deNull(cntrTonnage)
				+ "rainRcrds" + CommonUtility.deNull(rainRcrds) + "vesselType" + CommonUtility.deNull(vesselType)
				+ "dateType" + CommonUtility.deNull(dateType + "criteria" + criteria.toString()));

		List<VslProductivityValueObject> vslList = new ArrayList<VslProductivityValueObject>();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		TableResult tableResult = new TableResult();
		int paramValue = 0;
		String paramCd = null;
		int MK20E_TON = 0;
		int MK20L_TON = 0;
		int MK40E_TON = 0;
		int MK40L_TON = 0;
		int ML20E_TON = 0;
		int ML20L_TON = 0;
		int ML40E_TON = 0;
		int ML40L_TON = 0;
		int MT20E_TON = 0;
		int MT20L_TON = 0;
		int MT40E_TON = 0;
		int MT40L_TON = 0;
		int GB20E_TON = 0;
		int GB20L_TON = 0;
		int GB40E_TON = 0;
		int GB40L_TON = 0;
		int GL20E_TON = 0;
		int GL20L_TON = 0;
		int GL40E_TON = 0;
		int GL40L_TON = 0;
		int GT20E_TON = 0;
		int GT20L_TON = 0;
		int GT40E_TON = 0;
		int GT40L_TON = 0;
		StringBuffer sqlQuery = new StringBuffer();
		StringBuffer sb = new StringBuffer();

		SqlRowSet rst = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		SqlRowSet rs = null;

		SqlRowSet rs2 = null;

		SqlRowSet rs3 = null;
		try {
			log.info("START: getVesselProdRpt  DAO  Start Obj ");

			sb.append("SELECT PARA_CD, VALUE FROM SYSTEM_PARA ");
			sb.append(
					" WHERE PARA_CD IN	('MK20E','MK20L','MK40E','MK40L','ML20E','ML20L','ML40E','ML40L','MT20E','MT20L','MT40E','MT40L', ");
			sb.append(
					" 'GB20E','GB20L','GB40E','GB40L','GL20E','GL20L','GL40E','GL40L','GT20E','GT20L','GT40E','GT40L') ");
			String sql = sb.toString();

			log.info(" *** getVesselProdRpt SQL *****" + sql);
			log.info(" *** getVesselProdRpt paramMap *****" + paramMap);
			rst = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rst.next()) {
				paramCd = rst.getString("PARA_CD");
				paramValue = rst.getInt("VALUE");
				if (paramCd.equals("MK20E"))
					MK20E_TON = paramValue;
				else if (paramCd.equals("MK20L"))
					MK20L_TON = paramValue;
				else if (paramCd.equals("MK40E"))
					MK40E_TON = paramValue;
				else if (paramCd.equals("MK40L"))
					MK40L_TON = paramValue;
				else if (paramCd.equals("ML20L"))
					// Amended by Punitha on 11/01/2010
					// MK20L_TON = paramValue;
					ML20L_TON = paramValue;
				else if (paramCd.equals("ML20E"))
					ML20E_TON = paramValue;
				else if (paramCd.equals("ML40E"))
					ML40E_TON = paramValue;
				else if (paramCd.equals("ML40L"))
					ML40L_TON = paramValue;
				else if (paramCd.equals("MT20E"))
					MT20E_TON = paramValue;
				else if (paramCd.equals("MT20L"))
					MT20L_TON = paramValue;
				else if (paramCd.equals("MT40E"))
					MT40E_TON = paramValue;
				else if (paramCd.equals("MT40L"))
					MT40L_TON = paramValue;
				else if (paramCd.equals("GB20E"))
					GB20E_TON = paramValue;
				else if (paramCd.equals("GB20L"))
					GB20L_TON = paramValue;
				else if (paramCd.equals("GB40E"))
					GB40E_TON = paramValue;
				else if (paramCd.equals("GB40L"))
					GB40L_TON = paramValue;
				else if (paramCd.equals("GL20E"))
					GL20E_TON = paramValue;
				else if (paramCd.equals("GL20L"))
					GL20L_TON = paramValue;
				else if (paramCd.equals("GL40E"))
					GL40E_TON = paramValue;
				else if (paramCd.equals("GL40L"))
					GL40L_TON = paramValue;
				else if (paramCd.equals("GT20E"))
					GT20E_TON = paramValue;
				else if (paramCd.equals("GT20L"))
					GT20L_TON = paramValue;
				else if (paramCd.equals("GT40E"))
					GT40E_TON = paramValue;
				else if (paramCd.equals("GT40L"))
					GT40L_TON = paramValue;

			}

			// MC Consulting
			String searchDate = "B.ATB_DTTM", otherDate = "B.ATU_DTTM", orderByDate = "ATB_DTTM, ATU_DTTM";
			if (dateType != null) {
				if ("ATU".equals(dateType)) {
					searchDate = "(SELECT MAX(ATU_DTTM) FROM BERTHING B1 WHERE B1.VV_CD = V.VV_CD)";
					otherDate = "B.ATB_DTTM";
					orderByDate = "ATU_DTTM, ATB_DTTM";
				}
			}

			sqlQuery.setLength(0);
			// sqlQuery.append(" SELECT V.VV_CD VV_CD, (V.VSL_NM || ' / ' || V.OUT_VOY_NBR)
			// VSL_VOY, C.CO_NM AGENT, ");
			sqlQuery.append(
					" SELECT * FROM (SELECT V.VV_CD VV_CD, (V.VSL_NM || ' / ' || V.OUT_VOY_NBR) VSL_VOY, C.CO_NM AGENT, ");

			// Jacky SL-OPS-20100713-01 JWP scheme should be included 13/07/2010
			sqlQuery.append(
					" (SELECT STEV_CO_NM FROM VV_STEVEDORE VS, STEVEDORE_COMPANY WHERE STEV_CO_CD=STEV_CO_CD1 AND VS.VV_CD=V.VV_CD) STEVEDORE, V.SCHEME, DECODE(V.SCHEME,'JBT','NON-LINER','JWP','NON-LINER','JNL','NON-LINER','LINER') LINER, V.VSL_LOA, B.BERTH_NBR, B.GANG_NBR, B.HATCH_NBR, B.ATB_DTTM, ");
			sqlQuery.append(" (SELECT MAX(ATU_DTTM) FROM BERTHING B1 WHERE B1.VV_CD = V.VV_CD) ATU_DTTM, ");
			sqlQuery.append(
					" (SELECT 24* (MAX(ATU_DTTM) - B.ATB_DTTM) FROM BERTHING B1 WHERE B1.VV_CD = V.VV_CD) BERTH_HR, ");
			sqlQuery.append(
					" (SELECT 24* (GREATEST(NVL(MAX(GB_COD_DTTM),MAX(GB_COL_DTTM)),NVL(MAX(GB_COL_DTTM),MAX(GB_COD_DTTM))) - B.ATB_DTTM) ");
			sqlQuery.append("  FROM BERTHING B1 WHERE B1.VV_CD = V.VV_CD) CARGO_WORK_HR, ");
			sqlQuery.append(
					"  (SELECT 24* (GREATEST(NVL(MAX(GB_BCOD_DTTM),MAX(GB_BCOL_DTTM)),NVL(MAX(GB_BCOL_DTTM),MAX(GB_BCOD_DTTM))) - B.ATB_DTTM) ");
			sqlQuery.append(" FROM BERTHING B1 WHERE B1.VV_CD = V.VV_CD) BULK_WORK_HR, ");
			// Added by Punitha on 21/04/2009
			sqlQuery.append(" B.GB_FIRST_ACT_DTTM, B.GB_LAST_ACT_DTTM, ");
			// End
			sqlQuery.append(
					" (SELECT GREATEST(NVL(MAX(GB_COD_DTTM),MAX(GB_COL_DTTM)),NVL(MAX(GB_COL_DTTM),MAX(GB_COD_DTTM))) ");
			sqlQuery.append(
					" FROM BERTHING B1 WHERE B1.VV_CD = V.VV_CD) ACT_LAST_CARGO_DTTM, B.GB_COD_DTTM, B.GB_COL_DTTM, B.GB_BCOD_DTTM, B.GB_BCOL_DTTM, ");

			// Cally 27/07/2011 SL-OPS-20110719-02: For import & export of LCL cargo,
			// tonnage shall NOT contribute to vessel productivity throughput.
			// sqlQuery.append(" (SELECT NVL(SUM(CI.TONNAGE),0) FROM CARGO_IMPORT_SUMMARY CI
			// WHERE CI.VAR_NBR=V.VV_CD) CARGO_TON_DISC, ");
			sqlQuery.append(
					" (SELECT NVL(SUM(TONNAGE),0) FROM ( SELECT mft.var_nbr, vc.scheme, mft.crg_type, mft.hs_code, SUM(GREATEST((mft.gross_wt/1000), mft.gross_vol))- ");
			sqlQuery.append(
					" NVL( SUM((DECODE(nbr_pkgs, 0, 0, nbr_pkgs_in_port/nbr_pkgs))* GREATEST((mft.gross_wt/1000), mft.gross_vol)),'0') AS TONNAGE FROM MANIFEST_DETAILS mft, vessel_call vc WHERE mft.var_nbr=vc.vv_cd ");
			sqlQuery.append(
					" AND mft.bl_status='A' AND UNSTUFF_SEQ_NBR=0 GROUP BY vc.scheme, mft.var_nbr, mft.crg_type, mft.hs_code) WHERE VAR_NBR=V.VV_CD) CARGO_TON_DISC, ");
			sqlQuery.append(
					" (SELECT NVL(SUM(CE.TONNAGE),0) FROM CARGO_EXPORT_SUMMARY CE WHERE CE.VAR_NBR=V.VV_CD) CARGO_TON_LOAD, ");
			// Added by Punitha on 12/12/2008
			sqlQuery.append(" (SELECT SUM( GREATEST(S.SS_WT/1000, S.SS_VOL)) FROM ESN E, SS_DETAILS S ");
			sqlQuery.append(" WHERE E.ESN_ASN_NBR = S.ESN_ASN_NBR AND E.OUT_VOY_VAR_NBR=V.VV_CD) SHIP_STORE_TON, ");
			// End
			// Added by Punitha on 11/01/2010
			sqlQuery.append(" ((B.GB_LAST_ACT_DTTM - B.GB_FIRST_ACT_DTTM)*24) TIME_AT_WORK_HR, ");
			sqlQuery.append(" DECODE(V.FLOAT_CRANE_IND,'Y','Yes','N','No',V.FLOAT_CRANE_IND) FLOAT_CRANE_IND, ");
			sqlQuery.append(
					" (CASE WHEN V.HLIFT_OVERSIDE > 0 THEN V.HLIFT_OVERSIDE || 'T' ELSE '' END) HLIFT_OVERSIDE, ");
			sqlQuery.append(
					" (SELECT VTC.VSL_TYPE_NM FROM VESSEL_TYPE_CODE VTC, VESSEL VSL WHERE VTC.VSL_TYPE_CD = VSL.VSL_TYPE_CD AND VSL.VSL_NM = V.VSL_NM) VSL_TYPE, ");
			// End
			// Jacky SL-OPS-20100713-01 Rain hours be included 13/07/2010

			// Cally 27/07/2011 SL-OPS-20110719-02: When a rain record is deleted for a
			// particular date in JPOL. It should not affect the generation of vessel
			// productivity report in JPOL for that same date.
			// Commented by chue thing 11/12/2013 for the rain detectors data flow CR
			// sqlQuery.append(" NVL((SELECT ((SELECT MAX(ATU_DTTM) FROM BERTHING B1 WHERE
			// B1.VV_CD = V.VV_CD) - B.ATB_DTTM)*24 FROM RAIN_RECORD R WHERE STATUS_CD = 'A'
			// AND R.START_DTTM < B.ATB_DTTM AND R.END_DTTM > (SELECT MAX(ATU_DTTM) FROM
			// BERTHING B1 WHERE B1.VV_CD = V.VV_CD)),0) + ");
			// sqlQuery.append(" NVL((SELECT SUM(END_DTTM - START_DTTM)*24 FROM RAIN_RECORD
			// R WHERE STATUS_CD = 'A' AND R.START_DTTM >= B.ATB_DTTM AND R.END_DTTM <=
			// (SELECT MAX(ATU_DTTM) FROM BERTHING B1 WHERE B1.VV_CD = V.VV_CD)),0) + ");
			// sqlQuery.append(" NVL((SELECT (END_DTTM - B.ATB_DTTM)*24 FROM RAIN_RECORD R
			// WHERE STATUS_CD = 'A' AND R.START_DTTM < B.ATB_DTTM AND R.END_DTTM >
			// B.ATB_DTTM AND R.END_DTTM <= (SELECT MAX(ATU_DTTM) FROM BERTHING B1 WHERE
			// B1.VV_CD = V.VV_CD)),0) + ");
			// sqlQuery.append(" NVL((SELECT ((SELECT MAX(ATU_DTTM) FROM BERTHING B1 WHERE
			// B1.VV_CD = V.VV_CD) - START_DTTM)*24 FROM RAIN_RECORD R WHERE STATUS_CD = 'A'
			// AND R.START_DTTM > B.ATB_DTTM AND R.START_DTTM < (SELECT MAX(ATU_DTTM) FROM
			// BERTHING B1 WHERE B1.VV_CD = V.VV_CD) AND R.END_DTTM > (SELECT MAX(ATU_DTTM)
			// FROM BERTHING B1 WHERE B1.VV_CD = V.VV_CD)),0) RAIN_HR, ");
			// End
			// Chue thing modified for the rain detectors data flow CR
			sqlQuery.append(" (SELECT ROUND(SUM( ");
			sqlQuery.append("    CASE  ");
			sqlQuery.append(" WHEN R.START_DTTM >=  B.ATB_DTTM AND R.END_DTTM <= B.ATU_DTTM "); // atb and atu full
																								// covered rain record
			sqlQuery.append(" THEN (R.END_DTTM - R.START_DTTM) * 24 ");
			sqlQuery.append(" WHEN (R.START_DTTM <  B.ATB_DTTM AND R.END_DTTM < B.ATU_DTTM) "); // rain record covered
																								// atb
			sqlQuery.append(" THEN  (R.END_DTTM - B.ATB_DTTM) * 24 ");
			sqlQuery.append(" WHEN (R.START_DTTM >  B.ATB_DTTM AND R.END_DTTM > B.ATU_DTTM) "); // rain record covered
																								// atu
			sqlQuery.append(" THEN  (B.ATU_DTTM - R.START_DTTM) * 24 ");
			sqlQuery.append(" WHEN R.START_DTTM <  B.ATB_DTTM AND R.END_DTTM > B.ATU_DTTM "); // Rain record full
																								// covered atb and atu
			sqlQuery.append(" THEN (B.ATU_DTTM - B.ATB_DTTM) * 24    ");
			sqlQuery.append("   END ),2) AS RAIN_HR ");
			sqlQuery.append(
					" FROM BERTHING B, BERTHING_LENGTH BL, RAIN_RECORD R WHERE (BL.BERTH_NBR = SUBSTR(B.BERTH_NBR,0,INSTR(B.BERTH_NBR,'/')-1) OR BL.BERTH_NBR = B.BERTH_NBR) ");
			sqlQuery.append(
					" AND (BL.LOCATION_CD = R.LOCATION_CD OR  R.LOCATION_CD = 'A')AND STATUS_CD = 'A' AND  ((R.START_DTTM  BETWEEN B.ATB_DTTM AND B.ATU_DTTM) OR (R.END_DTTM  BETWEEN B.ATB_DTTM AND B.ATU_DTTM) OR (R.START_DTTM <  B.ATB_DTTM AND R.END_DTTM > B.ATU_DTTM ) )  ");
			sqlQuery.append(" AND V.VV_CD = B.VV_CD) ");
			sqlQuery.append(" RAIN_HR,     ");
			// END
			sqlQuery.append(
					"  (SELECT (SUM(BM.GROSS_WT)/1000) FROM BULK_MANIFEST_DETAILS BM WHERE BM.VAR_NBR = V.VV_CD AND BM.BL_STATUS = 'A') BULK_TON_DISC, ");
			sqlQuery.append(
					" (SELECT (SUM(BED.ESN_WT)/1000) FROM BULK_ESN_DETAILS BED, BULK_ESN BE WHERE BE.OUT_VOY_VAR_NBR = V.VV_CD ");
			sqlQuery.append(" AND BE.ESN_STATUS = 'A' AND BE.ESN_ASN_NBR = BED.ESN_ASN_NBR) BULK_TON_LOAD, ");
			// Amended by Punitha on 12/01/2010
			// LC_20E_D
			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT <= '20' AND C.STATUS = 'E' AND C.TXN_STATUS != 'D' ");
			sqlQuery.append(
					" AND ((C.PURP_CD IN ('IM') AND C.DISC_VV_CD = V.VV_CD AND C.SHIPMENT_STATUS != 'SH'))) LC_20E_D, ");
			// LC_20E_L
			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT <= '20' AND C.STATUS = 'E' AND C.TXN_STATUS != 'D' ");
			sqlQuery.append(
					" AND ((C.PURP_CD IN ('EX','RS') AND C.LOAD_VV_CD = V.VV_CD AND C.SHIPMENT_STATUS != 'SO'))) LC_20E_L, ");
			// LC_40E_D
			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT > '20' AND C.STATUS = 'E' AND C.TXN_STATUS != 'D' ");
			sqlQuery.append(
					" AND ((C.PURP_CD IN ('IM') AND C.DISC_VV_CD = V.VV_CD AND C.SHIPMENT_STATUS != 'SH'))) LC_40E_D, ");
			// LC_40E_L
			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT > '20' AND C.STATUS = 'E' AND C.TXN_STATUS != 'D' ");
			sqlQuery.append(
					" AND ((C.PURP_CD IN ('EX','RS') AND C.LOAD_VV_CD = V.VV_CD AND C.SHIPMENT_STATUS != 'SO'))) LC_40E_L, ");
			// LC_20L_D
			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT <= '20' AND C.STATUS != 'E' AND C.TXN_STATUS != 'D' ");
			sqlQuery.append(
					" AND ((C.PURP_CD IN ('IM') AND C.DISC_VV_CD = V.VV_CD AND C.SHIPMENT_STATUS != 'SH'))) LC_20L_D, ");
			// LC_20L_L
			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT <= '20' AND C.STATUS != 'E' AND C.TXN_STATUS != 'D' ");
			sqlQuery.append(
					" AND ((C.PURP_CD IN ('EX','RS') AND C.LOAD_VV_CD = V.VV_CD AND C.SHIPMENT_STATUS != 'SO'))) LC_20L_L, ");
			// LC_40L_D
			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT > '20' AND C.STATUS != 'E' AND C.TXN_STATUS != 'D' ");
			sqlQuery.append(
					" AND ((C.PURP_CD IN ('IM') AND C.DISC_VV_CD = V.VV_CD AND C.SHIPMENT_STATUS != 'SH'))) LC_40L_D, ");
			// LC_40L_L
			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT > '20' AND C.STATUS != 'E' AND C.TXN_STATUS != 'D' ");
			sqlQuery.append(
					" AND ((C.PURP_CD IN ('EX','RS') AND C.LOAD_VV_CD = V.VV_CD AND C.SHIPMENT_STATUS != 'SO'))) LC_40L_L, ");
			// TS_20E_D
			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT <= '20' AND C.STATUS = 'E' AND C.TXN_STATUS != 'D' AND C.PURP_CD IN ('RE','TS') ");
			sqlQuery.append(" AND ((C.DISC_VV_CD = V.VV_CD AND C.SHIPMENT_STATUS != 'SH'))) TS_20E_D, ");
			// TS_20E_L
			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT <= '20' AND C.STATUS = 'E' AND C.TXN_STATUS != 'D' AND C.PURP_CD IN ('RE','TS') ");
			sqlQuery.append(" AND ((C.LOAD_VV_CD = V.VV_CD AND C.SHIPMENT_STATUS != 'SO'))) TS_20E_L, ");
			// TS_40E_D
			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT > '20' AND C.STATUS = 'E' AND C.TXN_STATUS != 'D' AND C.PURP_CD IN ('RE','TS') ");
			sqlQuery.append(" AND ((C.DISC_VV_CD = V.VV_CD AND C.SHIPMENT_STATUS != 'SH'))) TS_40E_D, ");
			// TS_40E_L
			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT > '20' AND C.STATUS = 'E' AND C.TXN_STATUS != 'D' AND C.PURP_CD IN ('RE','TS') ");
			sqlQuery.append(" AND ((C.LOAD_VV_CD = V.VV_CD AND C.SHIPMENT_STATUS != 'SO'))) TS_40E_L, ");
			// TS_20L_D
			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT <= '20' AND C.STATUS != 'E' AND C.TXN_STATUS != 'D' AND C.PURP_CD IN ('RE','TS') ");
			sqlQuery.append(" AND ((C.DISC_VV_CD = V.VV_CD AND C.SHIPMENT_STATUS != 'SH'))) TS_20L_D, ");
			// TS_20L_L
			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT <= '20' AND C.STATUS != 'E' AND C.TXN_STATUS != 'D' AND C.PURP_CD IN ('RE','TS') ");
			sqlQuery.append(" AND ((C.LOAD_VV_CD = V.VV_CD AND C.SHIPMENT_STATUS != 'SO'))) TS_20L_L, ");
			// TS_40L_D
			sqlQuery.append(
					"  (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT > '20' AND C.STATUS != 'E' AND C.TXN_STATUS != 'D' AND C.PURP_CD IN ('RE','TS') ");
			sqlQuery.append(" AND ((C.DISC_VV_CD = V.VV_CD AND C.SHIPMENT_STATUS != 'SH'))) TS_40L_D, ");
			// TS_40L_L
			sqlQuery.append(
					"  (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT > '20' AND C.STATUS != 'E' AND C.TXN_STATUS != 'D' AND C.PURP_CD IN ('RE','TS') ");
			sqlQuery.append(" AND ((C.LOAD_VV_CD = V.VV_CD AND C.SHIPMENT_STATUS != 'SO'))) TS_40L_L, ");

			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT <= '20' AND C.STATUS = 'E' AND C.PURP_CD = 'LN' ");
			sqlQuery.append(
					" AND C.LOAD_VV_CD = V.VV_CD AND C.TXN_STATUS != 'D' AND C.SHIPMENT_STATUS != 'SO') LR_20E, ");
			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT > '20' AND C.STATUS = 'E' AND C.PURP_CD = 'LN' ");
			sqlQuery.append(
					" AND C.LOAD_VV_CD = V.VV_CD AND C.TXN_STATUS != 'D' AND C.SHIPMENT_STATUS != 'SO') LR_40E, ");
			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT <= '20' AND C.STATUS != 'E' AND C.PURP_CD = 'LN' ");
			sqlQuery.append(
					" AND C.LOAD_VV_CD = V.VV_CD AND C.TXN_STATUS != 'D' AND C.SHIPMENT_STATUS != 'SO') LR_20L, ");
			sqlQuery.append(
					" (SELECT COUNT(C.CNTR_SEQ_NBR) FROM CNTR C WHERE C.SIZE_FT > '20' AND C.STATUS != 'E' AND C.PURP_CD = 'LN' ");
			sqlQuery.append(
					" AND C.LOAD_VV_CD = V.VV_CD AND C.TXN_STATUS != 'D' AND C.SHIPMENT_STATUS != 'SO') LR_40L, VS.ab_cd ,v.create_cust_cd, VS.acct_nbr ");
			sqlQuery.append(
					"  FROM VESSEL_CALL V, VESSEL_SCHEME VS, BERTHING B, COMPANY_CODE C, VESSEL_PRE_OPS P, CARGO_CLIENT_CODE L ");
			sqlQuery.append("  WHERE " + searchDate
					+ " BETWEEN TO_DATE(:fromDate,'DDMMYYYY HH24MISS') AND TO_DATE(:toDate,'DDMMYYYY HH24MISS') ");
			sqlQuery.append("  AND " + otherDate
					+ " IS NOT NULL AND B.SHIFT_IND = '1' AND V.TERMINAL = 'GB' AND (V.GB_CLOSE_VSL_IND = 'Y' OR V.GB_CLOSE_SHP_IND = 'Y' OR V.GB_CLOSE_BJ_IND='Y') ");
			sqlQuery.append(
					" AND B.VV_CD = V.VV_CD AND V.VSL_OPR_CD = C.CO_CD AND C.REC_STATUS = 'A' AND V.VV_CD = P.VV_CD AND P.CC_CD = L.CC_CD AND V.SCHEME = VS.SCHEME_CD ");
			// Check for the parameters
			boolean liner = false;
			boolean nonJNL = false;
			boolean nonJBT = false;
			for (int i = 0; i < vslCategory.length; i++) {
				// log.info("Category*** :"+vslCategory[i]);
				if (vslCategory[i].equals("LINEAR"))
					liner = true;

				else if (vslCategory[i].equals("NON-LINEAR-JNL"))
					nonJNL = true;

				else if (vslCategory[i].equals("NON-LINEAR-JBT"))
					nonJBT = true;
			}

			if (liner && nonJNL && !nonJBT)
				sqlQuery.append(" AND V.SCHEME NOT IN ('JBT','JWP') "); // Jacky SL-OPS-20100713-01 JWP scheme should be
																		// included 13/07/2010
			else if (liner && nonJBT && !nonJNL)
				sqlQuery.append(" AND V.SCHEME NOT IN ('JNL') ");
			else if (nonJNL && nonJBT && !liner)
				sqlQuery.append(" AND V.SCHEME IN ('JNL', 'JBT','JWP') "); // Jacky SL-OPS-20100713-01 JWP scheme should
																			// be included 13/07/2010
			else if (liner && !nonJNL && !nonJBT)
				sqlQuery.append(" AND V.SCHEME NOT IN ('JNL', 'JBT','JWP') "); // Jacky SL-OPS-20100713-01 JWP scheme
																				// should be included 13/07/2010
			else if (nonJNL && !liner && !nonJBT)
				sqlQuery.append(" AND V.SCHEME IN ('JNL') ");
			else if (nonJBT && !liner && !nonJNL)
				sqlQuery.append(" AND V.SCHEME IN ('JBT','JWP') "); // Jacky SL-OPS-20100713-01 JWP scheme should be
																	// included 13/07/2010

			if (vesselType.equals("BULK"))
				sqlQuery.append(" AND L.BULK_VSL_IND = 'Y' ");
			else if (vesselType.equals("NON-BULK"))
				sqlQuery.append(" AND L.BULK_VSL_IND = 'N' ");

			sqlQuery.append(") ORDER BY " + orderByDate + " ");
			// log.info("Query : "+sqlQuery);
			SimpleDateFormat sf = new SimpleDateFormat("ddMMyyyy");
			String fromDate = sf.format(dateFrom) + " 000000";
			String toDate = sf.format(dateTo) + " 235959";
			// log.info("from Date : "+fromDate);
			// log.info("To date : "+toDate);

			log.info(" *** getVesselProdRpt SQL *****" + sqlQuery.toString());
			log.info(" *** getVesselProdRpt paramMap *****" + paramMap);

			paramMap.put("fromDate", fromDate);
			paramMap.put("toDate", toDate);
			String sqls = sqlQuery.toString();
			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sqls + ")",
						paramMap, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
			}
			if (criteria.isPaginated() && criteria.getPredicates().get("excel") == "false") {
				sqls = CommonUtil.getPaginatedSql(sqls, criteria.getStart(), criteria.getLimit());
			}

			rs = namedParameterJdbcTemplate.queryForRowSet(sqls, paramMap);

			String vvCode = "";

			String shpgAgent = "";
			String TAHolder = "";
			String TAAcctNbr = "";

			CustVslProdValueObject custVslProdValueObject = null;

			PubVslProdValueObject pubVslProdVO = null;

			log.info("Into VesselProdEjb.getVesselProdRpt*****************  rs.getFetchSize() = ");

			while (rs.next()) {

				VslProductivityValueObject vslVO = new VslProductivityValueObject();
				vslVO.setVvCode(rs.getString("VV_CD"));
				log.info("Into VesselProdEjb.getVesselProdRpt*****************  vslVO.getVvCode() = "
						+ vslVO.getVvCode());

				vvCode = vslVO.getVvCode();
				vslVO.setVesselName(rs.getString("VSL_VOY"));
				vslVO.setAgent(rs.getString("AGENT"));
				vslVO.setStevedore(rs.getString("STEVEDORE"));
				vslVO.setScheme(rs.getString("SCHEME"));
				vslVO.setLiner(rs.getString("LINER"));
				vslVO.setLOA(new Integer(rs.getInt("VSL_LOA")));
				vslVO.setBerth(rs.getString("BERTH_NBR"));
				vslVO.setGangsSupplied(rs.getInt("GANG_NBR"));
				vslVO.setHatchNbr(rs.getInt("HATCH_NBR"));
				vslVO.setATB(rs.getTimestamp("ATB_DTTM"));
				vslVO.setATU(rs.getTimestamp("ATU_DTTM"));
				vslVO.setBerthHr(rs.getDouble("BERTH_HR"));
				vslVO.setActualCargoDttm(rs.getTimestamp("ACT_LAST_CARGO_DTTM"));
				// Calculating CNTR_TON
				double cntrTon = 0.0;
				double discCntrTon = 0.0;
				double loadCntrTon = 0.0;
				if (cntrTonnage.equals("Conversion(MKT)")) {
					// Added by Punitha on 12/01/2010
					discCntrTon = (rs.getInt("LC_20E_D") * MK20E_TON) + (rs.getInt("LC_40E_D") * MK40E_TON)
							+ (rs.getInt("LC_20L_D") * MK20L_TON) + (rs.getInt("LC_40L_D") * MK40L_TON)
							+ (rs.getInt("TS_20E_D") * MT20E_TON) + (rs.getInt("TS_40E_D") * MT40E_TON)
							+ (rs.getInt("TS_20L_D") * MT20L_TON) + (rs.getInt("TS_40L_D") * MT40L_TON)
							+ (rs.getInt("LR_20E") * ML20E_TON) + (rs.getInt("LR_40E") * ML40E_TON)
							+ (rs.getInt("LR_20L") * ML20L_TON) + (rs.getInt("LR_40L") * ML40L_TON);

					// Added L&R containers. 7/5/2013. SL-OPS-20130507-01:
					// The captioned vessel has landing & reshipping of 39 units 20ft the
					// computation of Productivity Surcharge should be 39 units x 15t x 2. But
					// system computed as 4 times instead 2 times. To rectify the computation error.
					loadCntrTon = (rs.getInt("LC_20E_L") * MK20E_TON) + (rs.getInt("LC_40E_L") * MK40E_TON)
							+ (rs.getInt("LC_20L_L") * MK20L_TON) + (rs.getInt("LC_40L_L") * MK40L_TON)
							+ (rs.getInt("TS_20E_L") * MT20E_TON) + (rs.getInt("TS_40E_L") * MT40E_TON)
							+ (rs.getInt("TS_20L_L") * MT20L_TON) + (rs.getInt("TS_40L_L") * MT40L_TON)
							+ (rs.getInt("LR_20E") * ML20E_TON) + (rs.getInt("LR_40E") * ML40E_TON)
							+ (rs.getInt("LR_20L") * ML20L_TON) + (rs.getInt("LR_40L") * ML40L_TON);

					cntrTon = discCntrTon + loadCntrTon;
					// End

				} else if (cntrTonnage.equals("Conversion(GBO)")) {
					// Added by Punitha on 12/01/2010
					discCntrTon = (rs.getInt("LC_20E_D") * GB20E_TON) + (rs.getInt("LC_40E_D") * GB40E_TON)
							+ (rs.getInt("LC_20L_D") * GB20L_TON) + (rs.getInt("LC_40L_D") * GB40L_TON)
							+ (rs.getInt("TS_20E_D") * GT20E_TON) + (rs.getInt("TS_40E_D") * GT40E_TON)
							+ (rs.getInt("TS_20L_D") * GT20L_TON) + (rs.getInt("TS_40L_D") * GT40L_TON)
							+ (rs.getInt("LR_20E") * GL20E_TON) + (rs.getInt("LR_20E") * GL40E_TON)
							+ (rs.getInt("LR_20L") * GL20L_TON) + (rs.getInt("LR_40L") * GL40L_TON);

					// Added L&R containers. 7/5/2013. SL-OPS-20130507-01:
					// The captioned vessel has landing & reshipping of 39 units 20ft the
					// computation of Productivity Surcharge should be 39 units x 15t x 2. But
					// system computed as 4 times instead 2 times. To rectify the computation error.
					loadCntrTon = (rs.getInt("LC_20E_L") * GB20E_TON) + (rs.getInt("LC_40E_L") * GB40E_TON)
							+ (rs.getInt("LC_20L_L") * GB20L_TON) + (rs.getInt("LC_40L_L") * GB40L_TON)
							+ (rs.getInt("TS_20E_L") * GT20E_TON) + (rs.getInt("TS_40E_L") * GT40E_TON)
							+ (rs.getInt("TS_20L_L") * GT20L_TON) + (rs.getInt("TS_40L_L") * GT40L_TON)
							+ (rs.getInt("LR_20E") * GL20E_TON) + (rs.getInt("LR_20E") * GL40E_TON)
							+ (rs.getInt("LR_20L") * GL20L_TON) + (rs.getInt("LR_40L") * GL40L_TON);

					cntrTon = discCntrTon + loadCntrTon;
					// End

				} else {
					StringBuffer sb1 = new StringBuffer();
					// Added by Punitha on 12/01/2010
					sb1 = new StringBuffer();
					sb1.append(" SELECT (SUM(C.DECLR_WT)/1000) CNTR_TON FROM CNTR C ");
					sb1.append(" WHERE C.PURP_CD IN ('IM','RE','TS','LN') AND C.TXN_STATUS != 'D' ");
					sb1.append(" AND (C.DISC_VV_CD = :vvCode AND C.SHIPMENT_STATUS != 'SH') ");
					String sql1 = sb1.toString();

					log.info(" *** getVesselProdRpt SQL *****" + sql1.toString());
					paramMap.put("vvCode", vvCode);
					log.info(" *** getVesselProdRpt paramMap *****" + paramMap);
					rs2 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);
					if (rs2.next()) {
						discCntrTon = rs2.getDouble("CNTR_TON");
					}

					// Added L&R containers. 7/5/2013. SL-OPS-20130507-01:
					// The captioned vessel has landing & reshipping of 39 units 20ft the
					// computation of Productivity Surcharge should be 39 units x 15t x 2. But
					// system computed as 4 times instead 2 times. To rectify the computation error.
					sb1 = new StringBuffer();
					sb1.append(" SELECT (SUM(C.DECLR_WT)/1000) CNTR_TON FROM CNTR C ");
					sb1.append(" WHERE C.PURP_CD IN ('EX','RS','RE','TS','LN') AND C.TXN_STATUS != 'D' ");
					sb1.append(" AND (C.LOAD_VV_CD = :vvCode AND C.SHIPMENT_STATUS != 'SO') ");
					String sql2 = sb1.toString();
					paramMap.put("vvCode", vvCode);

					log.info(" *** getVesselProdRpt SQL2 *****" + sql2.toString());
					log.info(" *** getVesselProdRpt paramMap *****" + paramMap);
					rs3 = namedParameterJdbcTemplate.queryForRowSet(sql2.toString(), paramMap);
					if (rs3.next()) {
						loadCntrTon = rs3.getDouble("CNTR_TON");
					}
					cntrTon = discCntrTon + loadCntrTon;
					// End
				}

				vslVO.setCntrTonnage(cntrTon);
				// Added by Punitha on 12/01/2010
				vslVO.setDiscCntrTonnage(discCntrTon);
				vslVO.setLoadCntrTonnage(loadCntrTon);
				// End
				// Calculating cargoTonnage,totalTonnage and bulkTonnage;
				vslVO.setCargoDischargeTonnage(rs.getDouble("CARGO_TON_DISC"));
				vslVO.setCargoLoadTonnage(rs.getDouble("CARGO_TON_LOAD"));
				/*
				 * SSL-OPS- 0000395 - To fix the correct TA Indicator for Vessel Productivity
				 * Report 2013 Nov 13 Cally loading no shipstore
				 */
				// vslVO.setCargoTonnage(vslVO.getCargoDischargeTonnage() +
				// vslVO.getCargoLoadTonnage() + rs.getDouble("SHIP_STORE_TON"));
				vslVO.setCargoTonnage(vslVO.getCargoDischargeTonnage() + vslVO.getCargoLoadTonnage());
				log.info("Into VesselProdEjb.getVesselProdRpt*****************  vslVO.getCargoTonnage() = "
						+ vslVO.getCargoTonnage());
				vslVO.setTotalTonnage(vslVO.getCargoTonnage() + vslVO.getCntrTonnage());
				vslVO.setBulkDischargeTonnage(rs.getDouble("BULK_TON_DISC"));
				vslVO.setBulkLoadTonnage(rs.getDouble("BULK_TON_LOAD"));
				vslVO.setBulkTonnage(vslVO.getBulkDischargeTonnage() + vslVO.getBulkLoadTonnage());
				// Added by Punitha on 21/04/2009
				/*
				 * SSL-OPS- 0000395 - To fix the correct TA Indicator for Vessel Productivity
				 * Report 2013 Nov 13 Cally loading no shipstore
				 */
				// vslVO.setGenCargoLoadTonnage(rs.getDouble("CARGO_TON_LOAD") +
				// rs.getDouble("SHIP_STORE_TON"));
				vslVO.setGenCargoLoadTonnage(rs.getDouble("CARGO_TON_LOAD"));
				// Calculating cargo category
				if (vslVO.getTotalTonnage() <= 0)
					vslVO.setCargoCategory("");
				else if (vslVO.getTotalTonnage() < 1000)
					vslVO.setCargoCategory("<1000T");
				else if (vslVO.getTotalTonnage() <= 5000)
					vslVO.setCargoCategory("1000-5000T");
				else if (vslVO.getTotalTonnage() <= 10000)
					vslVO.setCargoCategory("5000-10000T");
				else
					vslVO.setCargoCategory(">10000T");
				// Calculating bench mark
				/*
				 * SSL-OPS- 0000395 - To fix the correct TA Indicator for Vessel Productivity
				 * Report 2013 Nov 13 Cally if(vslVO.getTotalTonnage() < 1000)
				 * vslVO.setBenchmark(0); else if(vslVO.getTotalTonnage() <= 5000)
				 * vslVO.setBenchmark(85); else if(vslVO.getTotalTonnage() <= 10000)
				 * vslVO.setBenchmark(110); else vslVO.setBenchmark(150);
				 */
				// step 1 : to Retrive the customised Productivity rate

				int rate = 0;

				Double total_cargo_tonnage = new Double(vslVO.getTotalTonnage());
				log.info("Into VesselProdEjb.getVesselProdRpt*****************  total_cargo_tonnage = "
						+ total_cargo_tonnage);

				shpgAgent = rs.getString("create_cust_cd");
				log.info(" THE shipping agent IS ----- " + shpgAgent);
				log.info("THE shipping agent IS ----- " + shpgAgent);

				TAHolder = rs.getString("ab_cd");// vessel_scheme.ab_cd
				TAAcctNbr = rs.getString("acct_nbr");// vessel_scheme.acct_nbr
				log.info(" THE TA Holder IS ----- " + TAHolder);
				log.info(" THE TA Acct Nbr IS ----- " + TAAcctNbr);
				log.info("THE TA Holder IS ----- " + TAHolder);
				log.info("THE TA Acct Nbr IS ----- " + TAAcctNbr);
				// seep 4 : Retrive customised rate for the customer if present
				// based on total cargo tonnage

				pubVslProdVO = null;

				custVslProdValueObject = null;

				if (TAHolder == null || TAHolder.equals(""))
					custVslProdValueObject = vesselProdSurcharge.getCustomisedRate(total_cargo_tonnage, shpgAgent,
							vslVO.getVvCode());
				else
					custVslProdValueObject = vesselProdSurcharge.getCustomisedRate(total_cargo_tonnage, TAHolder,
							vslVO.getVvCode());

				log.info(" Searching for customised tariff exists :" + custVslProdValueObject.isRateExist());
				log.info("Searching for customised tariff exists :" + custVslProdValueObject.isRateExist());
				log.info("Searching for customised tariff exists custVslProdValueObject.getRate():"
						+ custVslProdValueObject.getRate());

				boolean isRateExist = custVslProdValueObject.isRateExist();
				// VslProdValueObject vslProdVO = null;
				if (isRateExist) {
					log.info("--> customised rate  --");
					if (custVslProdValueObject.getRate().equals("")) {
						rate = 0;
					} else {
						log.info("--------- ");
						rate = Integer.parseInt(custVslProdValueObject.getRate());

					}
					// vslProdVO = custVslProdValueObject;
					log.info(" The customised rate is -- " + rate + " --");
					log.info(" The customised rate is -- " + rate + " --");

				} else {
					log.info("--> looking for published rate  --");

					pubVslProdVO = vesselProdSurcharge.getPublishedRate(total_cargo_tonnage, vslVO.getVvCode());

					// log.info("--> The published rate is -- " + pubVslProdVO.getRate()+"
					// --" );

					log.info("--> The Version Number  is  " + pubVslProdVO.getVersion_nbr());

					log.info("--> The published rate is  -- " + pubVslProdVO.getRate() + " --");
					log.info("--> The Version Number  is  " + pubVslProdVO.getVersion_nbr());

					if (pubVslProdVO.getRate().equals(""))
						rate = 0;
					else
						rate = Integer.parseInt(pubVslProdVO.getRate());
				}

				log.info(" rate = " + rate);
				log.info(" rate = " + rate);

				vslVO.setBenchmark(rate);

				// Calculating the actual wrk hr
				double cargoWorkHr = rs.getDouble("CARGO_WORK_HR");
				double bulkWorkHr = rs.getDouble("BULK_WORK_HR");
				if (cargoWorkHr > 0)
					vslVO.setActualWorkHr(cargoWorkHr);
				else if (bulkWorkHr > 0)
					vslVO.setActualWorkHr(bulkWorkHr);
				else
					vslVO.setActualWorkHr(0);
				// Added by Punitha on 21/04/2009
				vslVO.setGbFirstActDttm(rs.getTimestamp("GB_FIRST_ACT_DTTM"));
				vslVO.setGbLastActDttm(rs.getTimestamp("GB_LAST_ACT_DTTM"));
				// Calculation the gross tons per hour
				if (vslVO.getTotalTonnage() > 0)
					vslVO.setGrossTonsPerHour(vslVO.getTotalTonnage() / vslVO.getBerthHr());
				else if (vslVO.getBulkTonnage() > 0)
					vslVO.setGrossTonsPerHour(vslVO.getBulkTonnage() / vslVO.getBerthHr());
				else
					vslVO.setGrossTonsPerHour(0);
				// End
				// Calculating tons per hour
				if (vslVO.getActualWorkHr() <= 0)
					vslVO.setTonsPerHour(0);
				else if (vslVO.getTotalTonnage() > 0)
					vslVO.setTonsPerHour(vslVO.getTotalTonnage() / vslVO.getActualWorkHr());
				else if (vslVO.getBulkTonnage() > 0)
					vslVO.setTonsPerHour(vslVO.getBulkTonnage() / vslVO.getActualWorkHr());
				else
					vslVO.setTonsPerHour(0);
				// Calculating the tons per gang hr
				if (vslVO.getActualWorkHr() <= 0)
					vslVO.setTonsPerGangHour(0);
				else if (vslVO.getTotalTonnage() > 0)
					vslVO.setTonsPerGangHour(
							vslVO.getTotalTonnage() / vslVO.getActualWorkHr() / vslVO.getGangsSupplied());
				else
					vslVO.setTonsPerGangHour(0);

				// Calculating daily rate
				if (vslVO.getTonsPerHour() == 0)
					vslVO.setDailyRateGeneralCargo(0);
				else
					vslVO.setDailyRateGeneralCargo(vslVO.getTotalTonnage() / vslVO.getActualWorkHr() / 24);
				// Calculating daily rate load
				if (vslVO.getTonsPerHour() == 0)
					vslVO.setDailyRateMtrLOA(0);
				else
					vslVO.setDailyRateMtrLOA(
							vslVO.getTotalTonnage() / vslVO.getActualWorkHr() / 24 / vslVO.getLOA().doubleValue());
				// Calculating allocated work hours
				if (vslVO.getTotalTonnage() <= 0)
					vslVO.setWorkHours(0);
				else if (vslVO.getBenchmark() == 0)
					vslVO.setWorkHours(0);
				else
					vslVO.setWorkHours(vslVO.getTotalTonnage() / vslVO.getBenchmark());

				// Calculating the cargo allocation time and grace time
				// DecimalFormat decimalFormat =new DecimalFormat("0.00");
				// String workHr = decimalFormat.format(vslVO.getWorkHours());
				String workHr = String.valueOf(vslVO.getWorkHours());
				// log.info("Allocated Wrk Hr :"+workHr);
				String strHour = workHr.substring(0, workHr.indexOf("."));
				String strMin = workHr.substring((workHr.indexOf(".")), workHr.length()); // With decimal pt.
				double minutes = Double.parseDouble(strMin) * 60;
				Calendar c = Calendar.getInstance();
				c.setTime(vslVO.getATB());
				// c.add(Calendar.HOUR, new Double(vslVO.getWorkHours()).intValue());
				c.add(Calendar.HOUR, Integer.parseInt(strHour));
				c.add(Calendar.MINUTE, (int) minutes);
				if (vslVO.getWorkHours() == 0)
					vslVO.setLastCargoDttm(null);
				else
					vslVO.setLastCargoDttm(c.getTime());

				Calendar c1 = Calendar.getInstance();
				c1.setTime(vslVO.getATB());
				if (vslVO.getWorkHours() == 0)
					vslVO.setLastCargoGraceDttm(null);
				else if ((vslVO.getWorkHours() * 0.1) > 4) {
					// int wh = new Double(vslVO.getWorkHours()).intValue() + 4;
					int wh = Integer.parseInt(strHour) + 4;
					c1.add(Calendar.HOUR, wh);
					c1.add(Calendar.MINUTE, (int) minutes);
					vslVO.setLastCargoGraceDttm(c1.getTime());
				} else {
					// int wh =new Double(vslVO.getWorkHours()* 1.1).intValue();
					// DecimalFormat decimalFormat1 =new DecimalFormat("0.00");
					String workHrs = String.valueOf(vslVO.getWorkHours() * 1.1);
					String strHours = workHrs.substring(0, workHrs.indexOf("."));
					String strMins = workHrs.substring((workHrs.indexOf(".")), workHrs.length());
					double minute = Double.parseDouble(strMins) * 60;
					c1.add(Calendar.HOUR, Integer.parseInt(strHours));
					c1.add(Calendar.MINUTE, (int) minute);
					vslVO.setLastCargoGraceDttm(c1.getTime());
				}

				// Added by Punitha on 21/04/2009
				if (vslVO.getActualCargoDttm() != null && !"".equals(vslVO.getActualCargoDttm())
						&& vslVO.getLastCargoGraceDttm() != null && !"".equals(vslVO.getLastCargoGraceDttm())) {
					double timeExceeded = gethoursBetweenDates(vslVO.getActualCargoDttm(),
							vslVO.getLastCargoGraceDttm());
					vslVO.setTimeExceeded(timeExceeded);
				} else
					vslVO.setTimeExceeded(0);
				// Calculating production surcharge

				if (vslVO.getBenchmark() == 0)
					vslVO.setProdSurcharge("");
				else if (vslVO.getActualCargoDttm().after(vslVO.getLastCargoGraceDttm()))
					vslVO.setProdSurcharge("Yes");
				else
					vslVO.setProdSurcharge("No");

				// Calculating cargo type
				if (rs.getDate("GB_COD_DTTM") != null || rs.getDate("GB_COL_DTTM") != null)
					if (rs.getDate("GB_BCOD_DTTM") == null && rs.getDate("GB_BCOL_DTTM") == null)
						vslVO.setCargoType("General");
					else
						vslVO.setCargoType("General & Bulk");
				else if (rs.getDate("GB_BCOD_DTTM") != null || rs.getDate("GB_BCOL_DTTM") != null)
					vslVO.setCargoType("Bulk");
				else
					vslVO.setCargoType("");

				// Added by Punitha on 11/01/2010
				vslVO.setTimeAtWork(rs.getDouble("TIME_AT_WORK_HR"));
				vslVO.setFloatCraneInd(CommonUtility.deNull(rs.getString("FLOAT_CRANE_IND")));
				vslVO.setHeavyLiftOverside(CommonUtility.deNull(rs.getString("HLIFT_OVERSIDE")));
				vslVO.setVesselType(CommonUtility.deNull(rs.getString("VSL_TYPE")));
				// End

				// Jacky SL-OPS-20100713-01 Rain hours be included 13/07/2010
				vslVO.setRainHours(rs.getDouble("RAIN_HR"));
				// End

				topsModel.put(vslVO);
			}
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			log.info("Vessel List Size :" + vslList.size());

			log.info("END: *** getVesselProdRpt Result *****" + vslList.toString());
		} catch (NullPointerException e) {
			log.error("Exception getVesselProdRpt :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getVesselProdRpt :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselProdRpt  DAO  END");
		}

		return tableResult;
	}

	private float gethoursBetweenDates(java.util.Date date, java.util.Date date2) throws BusinessException {

		log.info("START: gethoursBetweenDates  DAO  Start Obj " + " date:" + date + " date2:" + date2);
		double diffMillis = 0;
		float diffHours = 0;

		Calendar startCalendar = new GregorianCalendar();
		Calendar endCalendar = new GregorianCalendar();
		String startString = "";
		if (date != null) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			startString = df.format(date);
		}
		String[] dateAndTime = startString.split(" ");
		String[] dateString = dateAndTime[0].split("/");
		String[] timeString = dateAndTime[1].split(":");

		int year = Integer.parseInt(dateString[2]);
		int month = Integer.parseInt(dateString[1]);
		int day = Integer.parseInt(dateString[0]);

		int hour = Integer.parseInt(timeString[0]);
		int min = Integer.parseInt(timeString[1]);

		startCalendar.set(year, month - 1, day, hour, min);
		// LogManager.instance.logDebug("Start String :"+startString);

		// log.info("start date :" + year+" " +month+" " +day+" " +hour+" " +min);
		String endString = "";
		if (date2 != null) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			endString = df.format(date2);
		}
		// LogManager.instance.logDebug("End String :"+endString);
		dateAndTime = endString.split(" ");
		dateString = dateAndTime[0].split("/");
		timeString = dateAndTime[1].split(":");

		year = Integer.parseInt(dateString[2]);
		month = Integer.parseInt(dateString[1]);
		day = Integer.parseInt(dateString[0]);

		hour = Integer.parseInt(timeString[0]);
		min = Integer.parseInt(timeString[1]);
		// log.info("end date :" + year+" " +month+" " +day+" " +hour+" " +min);

		endCalendar.set(year, month - 1, day, hour, min);

		// log.info("Plugin mills :" + startCalendar.getTimeInMillis());
		// log.info("Plugout mills:" + endCalendar.getTimeInMillis());
		diffMillis = (double) startCalendar.getTimeInMillis() - endCalendar.getTimeInMillis();
		diffHours = (float) (diffMillis / (60 * 60 * 1000));

		// log.info(diffHours);
		// log.info(FieldValidation.floatFormat(diffHours));
		
		log.info("END: *** gethoursBetweenDates Result *****" + diffHours);
		return diffHours;

	}

	@Override
	public String getColNames() throws BusinessException {
		String colNames = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getColNames  DAO  Start Obj ");

			String sql = "select value from TEXT_PARA where PARA_CD='VSPD_RPT_M'";

			log.info(" *** getColNames SQL *****" + sql);

			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
			while (rs.next())
				colNames = rs.getString("VALUE");
			log.info("END: *** getColNames Result *****" + colNames.toString());
		} catch (NullPointerException e) {
			log.error("Exception getColNames :", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getColNames :", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getColNames  DAO  END");
		}

		return colNames;
	}

}
