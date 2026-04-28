package sg.com.jp.generalcargo.dao.impl;

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

import sg.com.jp.generalcargo.dao.ContainerCommonFunctionRepo;
import sg.com.jp.generalcargo.domain.PM4OpsValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;


@Repository("containerCommonFunctionRepo")
public class ContainerCommonFunctionJdbcRepo implements ContainerCommonFunctionRepo {

	private static final Log log = LogFactory.getLog(ContainerCommonFunctionJdbcRepo.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// ejb.sessionBeans.cim.ContainerCommonFunction-->ContainerCommonFunctionEJB-->getCntrCatCd()
	@Override
	  public String getCntrCatCd(String iso, String imdgClsCd, int oh, int olFront, int olBack, int owRight, int owLeft,
	            String refrInd, String ucInd, String oogInd, String specDetail, String status) throws BusinessException {
	        return this.getCntrCatCd(iso, imdgClsCd, oh, olFront, olBack, owRight, owLeft, refrInd, ucInd, oogInd,
	                specDetail, status, null);
	    }
	  
	@Override
	public String getCntrCatCd(String iso, String imdgClsCd, int oh, int olFront, int olBack, int owRight, int owLeft,
			String refrInd, String ucInd, String oogInd, String specDetail, String status, String dgInd)
			throws BusinessException {

		/////////////////////////////////////////////////////////////
		// Initialisation & Initial Checkings
		////////////////////////////////////////////////////////////
		String S_SpecDetCat = "S1,S2,S3,S4,S5,S6,S7,S8,S9";
		String DM_SpecDetCat = "DM";
		String HT_SpecDetCat = "HT";
		String ER_SpecDetCat = "ER";
		String AB_SpecDetCat = "AB";
		String TP_SpecDetCat = "TP"; // Test value
		String UD_SpecDetCat = "UD";
		String OD_SpecDetCat = "OD";
		String ND_SpecDetCat = "ND";
		String DO_SpecDetCat = "DO";
		String TS_SpecDetCat = "TS";
		String SW_SpecDetCat = "SW";
		String UP_SpecDetCat = "UP";
		String NP_SpecDetCat = "NP";
		String MB_SpecDetCat = "MB";
		String FT_SpecDetCat = "FT";
		String[] refrTypeCode = { "H", "R", "3", "4" };

		iso = CommonUtility.deNull(iso);
		imdgClsCd = CommonUtility.deNull(imdgClsCd);
		refrInd = CommonUtility.deNull(refrInd);
		ucInd = CommonUtility.deNull(ucInd);
		oogInd = CommonUtility.deNull(oogInd);
		specDetail = CommonUtility.deNull(specDetail);
		status = CommonUtility.deNull(status);
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql = "";

		try {
			log.info("START: getCntrCatCd  DAO  Start iso: " + iso + " imdgClsCd" + imdgClsCd + " oh:" + oh
					+ " olFront:" + olFront + " olBack:" + olBack + " owRight:" + owRight + " owLeft:" + owLeft
					+ " refrInd:" + refrInd + " ucInd:" + ucInd + " oogInd:" + oogInd + " specDetail:" + specDetail
					+ " status:" + status + " dgInd:" + dgInd);
			

			String type = "";
			String height = "";
			String isoInd;

			/******/
			// refrInd = "N" : Non-reefer. Empty cntr, no temperature.
			// refrInd = "D" : Dry-reefer. Laden cntr, no temperature.
			// refrInd = "O" : Operational-reefer. Laden cntr with temperature.
			// refrInd = "N";
			String refrTypeInd = "N";
			/******/
			if (!(iso.equals(""))) {

				isoInd = getIsoOldNewInd(iso);
				List<String> temp = getIsoHeightWidth(iso, isoInd);
				if (temp != null) {
					Iterator<String> iterator = temp.iterator();
					if (iterator.hasNext()) {
						height = (String) iterator.next();
					}
				}
				type = getIsoType(iso, isoInd);
				if (height == null || type == null)
					throw new BusinessException("Invalid ISO type code.");

				// Set RefrInd based on iso value
				/*****/
				// refrInd = "N";
				/*****/
				String typeCd = iso.substring(2, 3);
				for (int i = 0; i < refrTypeCode.length; i++) {
					if (refrTypeCode[i].equalsIgnoreCase(typeCd)) {
						/*****/
						// refrInd = "Y";
						refrTypeInd = "Y";
						/*****/
						break;
					}
				}
			}

			//
			String[] specDet = null;
			if (specDetail.length() > 0) {
				if (specDetail.length() % 2 != 0)
					return null;
				int noOfElements = specDetail.length() / 2;
				specDet = new String[noOfElements];

				for (int i = 0; i < noOfElements; i++)
					specDet[i] = specDetail.substring(i * 2, i * 2 + 2);

			}

			// String[] specDet = Container.getSpecialDet(specDetail);
			int count = 0;
			String catCd = "";

			///////////////////////////////////////
			// Category for Empty Containers
			///////////////////////////////////////
			if (status.equals("E")) {

				// Check for S1 - S9 in Special Details
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (S_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = specDet[i];
							return catCd;
						}
					}
				}

				// Check for 'DM' in Special Detail
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (DM_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = specDet[i];
							return catCd;
						}
					}
				}

				// Start - Modified by Valli for SL-CIM-20060111-02 - 20-Jan-2006
				if (PM4OpsValueObject.DG_IND_YES.equalsIgnoreCase(dgInd) || ((imdgClsCd != null)
						&& (!imdgClsCd.equals("")) && (!PM4OpsValueObject.DG_IND_NO.equalsIgnoreCase(dgInd)))) {
					catCd = "DG";
				}
				// Tank type container
				if (!type.equals("")) {
					sql = "SELECT COUNT(*) FROM MISC_TYPE_CODE WHERE CAT_CD = 'TN_CNTR' AND MISC_TYPE_CD =:type";

					
					log.info(" ***getCntrCatCd SQL *****" + sql);
					paramMap.put("type", type);
					log.info(" ***getCntrCatCd paramMap *****" + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
					if (rs.next()) {
						count = rs.getInt("COUNT(*)");
						if (count > 0) {
							// Changed by Valli for SL-CIM-20060111-02
							catCd = "DG".equalsIgnoreCase(catCd) ? "DE" : "TK";
							return catCd;
						}
					}
				}
				if ("DG".equalsIgnoreCase(catCd)) {
					return catCd;
				}
				// End - Modified by Valli for SL-CIM-20060111-02 - 20-Jan-2006

				// Check for 'HT' (Hangertainer) in Special Detail
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (HT_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = "EH";
							return catCd;
						}
					}
				}

				if (refrTypeInd.equals("Y")) {
					// Reefer Type and high cube
					if ((!height.equals("")) && (height.equals("9'6") || height.equals(">8'6"))) {
						catCd = "RH";
						return catCd;
					}
					// Reefer Type
					else {
						catCd = "MR";
						return catCd;
					}
				}

				// Flat rack type container
				if (!type.equals("")) {
					sql = "SELECT COUNT(*) FROM MISC_TYPE_CODE WHERE CAT_CD = 'PS_CNTR' AND MISC_TYPE_CD =:type";

					
					log.info(" ***getCntrCatCd SQL *****" + sql);
					paramMap.put("type", type);
					log.info(" ***getCntrCatCd paramMap *****" + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
					if (rs.next()) {
						count = rs.getInt("COUNT(*)");
						if (count > 0) {
							catCd = "FE";
							return catCd;
						}
					}
				}

				// Platform collapsible type container
				if (!type.equals("")) {
					sql = "SELECT COUNT(*) FROM MISC_TYPE_CODE WHERE CAT_CD = 'PC_CNTR' AND MISC_TYPE_CD =:type";

					log.info(" ***getCntrCatCd SQL *****" + sql);
					paramMap.put("type", type);
					log.info(" ***getCntrCatCd paramMap *****" + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
					if (rs.next()) {
						count = rs.getInt("COUNT(*)");
						if (count > 0) {
							catCd = "FE";
							return catCd;
						}
					}
				}

				// Platform type container
				if (!type.equals("")) {
					sql = "SELECT COUNT(*) FROM MISC_TYPE_CODE WHERE CAT_CD = 'PL_CNTR' AND MISC_TYPE_CD =:type";

				
					log.info(" ***getCntrCatCd SQL *****" + sql);
					paramMap.put("type", type);
					log.info(" ***getCntrCatCd paramMap *****" + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
					if (rs.next()) {
						count = rs.getInt("COUNT(*)");
						if (count > 0) {
							catCd = "PF";
							return catCd;
						}
					}
				}

				// Half Height
				if ((!height.equals("")) && (height.length() >= 2)) {
					if (height.substring(0, 2).equals("4'") || height.equals(">4'")) {
						catCd = "HE";
						return catCd;
					}
				}

				// Open Top type container
				if (!type.equals("")) {
					sql = "SELECT COUNT(*) FROM MISC_TYPE_CODE WHERE CAT_CD = 'UT_CNTR' AND MISC_TYPE_CD =:type";

					log.info(" ***getCntrCatCd SQL *****" + sql);
					paramMap.put("type", type);
					log.info(" ***getCntrCatCd paramMap *****" + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
					if (rs.next()) {
						count = rs.getInt("COUNT(*)");
						if (count > 0) {
							catCd = "TE";
							return catCd;
						}
					}
				}

				// Open Side type container
				if (!type.equals("")) {
					sql = "SELECT COUNT(*) FROM MISC_TYPE_CODE WHERE CAT_CD = 'US_CNTR' AND MISC_TYPE_CD = :type";

			
					log.info(" ***getCntrCatCd SQL *****" + sql);
					paramMap.put("type", type);
					log.info(" ***getCntrCatCd paramMap *****" + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
					if (rs.next()) {
						count = rs.getInt("COUNT(*)");
						if (count > 0) {
							catCd = "SE";
							return catCd;
						}
					}
				}

				// High Cube
				if ((!height.equals("")) && (height.equals("9'6") || height.equals(">8'6"))) {
					catCd = "MH";
					return catCd;
				}

				// None of the above
				catCd = "MT";
				return catCd;
			}

			///////////////////////////////////////
			// Category for Laden Containers
			///////////////////////////////////////
			else {

				// Check for S1 - S9 in Special Details
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (S_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = specDet[i];
							return catCd;
						}
					}
				}

				// UC cargo
				if (ucInd.equals("Y")) {
					catCd = "UC";
					return catCd;
				}

				// Reefer container.
				if (refrTypeInd.equals("Y")) {
					// IMDG class not blank
					if ((imdgClsCd != null) && (!imdgClsCd.equals(""))) {
						// Operational.
						if (refrInd.equals("O")) {
							catCd = "RG";
							return catCd;
						}
						// Dry.
						else {
							catCd = "DD";
							return catCd;
						}
					}

					// Highcube Reefer
					if ((!height.equals("")) && (height.equals("9'6") || height.equals(">8'6"))) {
						// Operational.
						if (refrInd.equals("O")) {
							catCd = "HR";
							return catCd;
						}
						// Dry.
						else {
							catCd = "HD";
							return catCd;
						}
					}

					// Operational.
					if (refrInd.equals("O")) {
						catCd = "RF";
						return catCd;
					}
					// Dry.
					else {
						catCd = "RD";
						return catCd;
					}
				}

				// Check for Fantainer ('FT' in Special Detail)
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (FT_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = specDet[i];
							return catCd;
						}
					}
				}

				// With IMDG
				if ((imdgClsCd != null) && (!imdgClsCd.equals(""))) {
					// Dry Reefer & IMDG class not blank
					if (refrInd.equals("D")) {
						catCd = "DD";

						return catCd;
					}

					// Over-size & IMDG class not blank
					if (oogInd.equals("Y")) {
						catCd = "OG";

						return catCd;
					}

					// IMDG class not blank
					if ((imdgClsCd != null) && (!imdgClsCd.equals(""))
							&& (!PM4OpsValueObject.DG_IND_NO.equalsIgnoreCase(dgInd))) {

						catCd = "DG";

						return catCd;
					}
				}

				// Check for 'ER' in Special Detail
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (ER_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = specDet[i];

							return catCd;
						}
					}
				}

				// Over size container
				if (oogInd.equals("Y")) {

					// Over Height only
					if ((oh > 0) && (olFront == 0) && (olBack == 0) && (owRight == 0) && (owLeft == 0)) {
						catCd = "OH";

						return catCd;
					}

					// Over Width only
					if ((oh == 0) && (olFront == 0) && (olBack == 0) && ((owRight > 0) || (owLeft > 0))) {
						catCd = "OW";

						return catCd;
					}

					// Over Length only
					if ((oh == 0) && ((olFront > 0) || (olBack > 0)) && (owRight == 0) && (owLeft == 0)) {
						catCd = "OL";

						return catCd;
					}

					// Combination of over-size
					catCd = "HW";

					return catCd;
				}

				// Check for 'AB' in Special Detail
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (AB_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = specDet[i];

							return catCd;
						}
					}
				}

				// Tank type container
				if (!type.equals("")) {
					sql = "SELECT COUNT(*) FROM MISC_TYPE_CODE WHERE CAT_CD = 'TN_CNTR' AND MISC_TYPE_CD =:type";

					log.info(" ***getCntrCatCd SQL *****" + sql);
					paramMap.put("type", type);
					log.info(" ***getCntrCatCd paramMap *****" + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
					if (rs.next()) {
						count = rs.getInt("COUNT(*)");

						if (count > 0) {
							catCd = "TK";

							return catCd;
						}
					}
				}

				// Check for 'HT' (Hangertainer) in Special Detail
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (HT_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = "HT";

							return catCd;
						}
					}
				}

				// Check for 'TP' in Special Detail
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (TP_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = specDet[i];

							return catCd;
						}
					}
				}

				// Flat rack type container
				if (!type.equals("")) {
					sql = "SELECT COUNT(*) FROM MISC_TYPE_CODE WHERE CAT_CD = 'PS_CNTR' AND MISC_TYPE_CD = :type";

					log.info(" ***getCntrCatCd SQL *****" + sql);
					paramMap.put("type", type);
					log.info(" ***getCntrCatCd paramMap *****" + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
					if (rs.next()) {
						count = rs.getInt("COUNT(*)");

						if (count > 0) {
							catCd = "FR";

							return catCd;
						}
					}
				}

				// Platform collapsible type container
				if (!type.equals("")) {
					sql = "SELECT COUNT(*) FROM MISC_TYPE_CODE WHERE CAT_CD = 'PC_CNTR' AND MISC_TYPE_CD = :type";

					
					log.info(" ***getCntrCatCd SQL *****" + sql);
					paramMap.put("type", type);
					log.info(" ***getCntrCatCd paramMap *****" + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
					if (rs.next()) {
						count = rs.getInt("COUNT(*)");

						if (count > 0) {
							catCd = "FR";

							return catCd;
						}
					}
				}

				// Platform type container
				if (!type.equals("")) {
					sql = "SELECT COUNT(*) FROM MISC_TYPE_CODE WHERE CAT_CD = 'PL_CNTR' AND MISC_TYPE_CD =:type";

					
					log.info(" ***getCntrCatCd SQL *****" + sql);
					paramMap.put("type", type);
					log.info(" ***getCntrCatCd paramMap *****" + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
					if (rs.next()) {
						count = rs.getInt("COUNT(*)");

						if (count > 0) {
							catCd = "PF";

							return catCd;
						}
					}
				}

				// Half Height
				if ((!height.equals("")) && (height.length() >= 2)) {
					if (height.substring(0, 2).equals("4'") || height.equals(">4'")) {
						catCd = "HH";

						return catCd;
					}
				}

				// Open Top type container
				if (!type.equals("")) {
					sql = "SELECT COUNT(*) FROM MISC_TYPE_CODE WHERE CAT_CD = 'UT_CNTR' AND MISC_TYPE_CD =:type";

					
					log.info(" ***getCntrCatCd SQL *****" + sql);
					paramMap.put("type", type);
					log.info(" ***getCntrCatCd paramMap *****" + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
					if (rs.next()) {
						count = rs.getInt("COUNT(*)");

						if (count > 0) {
							catCd = "OT";

							return catCd;
						}
					}
				}

				// Open Side type container
				if (!type.equals("")) {
					sql = "SELECT COUNT(*) FROM MISC_TYPE_CODE WHERE CAT_CD = 'US_CNTR' AND MISC_TYPE_CD =:type";

					
					log.info(" ***getCntrCatCd SQL *****" + sql);
					paramMap.put("type", type);
					log.info(" ***getCntrCatCd paramMap *****" + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
					if (rs.next()) {
						count = rs.getInt("COUNT(*)");

						if (count > 0) {
							catCd = "OS";

							return catCd;
						}
					}
				}

				// Check for 'UD' in Special Detail
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (UD_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = specDet[i];

							return catCd;
						}
					}
				}

				// Check for 'OD' in Special Detail
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (OD_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = specDet[i];

							return catCd;
						}
					}
				}

				// Check for 'ND' in Special Detail
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (ND_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = specDet[i];

							return catCd;
						}
					}
				}

				// Check for 'DO' in Special Detail
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (DO_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = specDet[i];

							return catCd;
						}
					}
				}

				// Check for 'TS' in Special Detail
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (TS_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = specDet[i];

							return catCd;
						}
					}
				}

				// Check for 'SW' in Special Detail
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (SW_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = specDet[i];

							return catCd;
						}
					}
				}

				// Check for 'UP' in Special Detail
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (UP_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = specDet[i];

							return catCd;
						}
					}
				}

				// Check for 'NP' in Special Detail
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (NP_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = specDet[i];

							return catCd;
						}
					}
				}

				// Check for 'MB' in Special Detail
				if (specDet != null) {
					for (int i = 0; i < specDet.length; i++) {
						if (MB_SpecDetCat.indexOf(specDet[i]) >= 0) {
							catCd = specDet[i];

							return catCd;
						}
					}
				}

				// High Cube
				if ((!height.equals("")) && (height.equals("9'6") || height.equals(">8'6"))) {
					catCd = "HC";

					return catCd;
				}

				// None of the above

				catCd = "GP";

				log.info("END: ** getCntrCatCd Result ****" + catCd.toString());

				return catCd;
			}

		} catch (NullPointerException e) {
			log.info("Exception getCntrCatCd :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCntrCatCd :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCntrCatCd  DAO  END");
		}
	}
	
	// jp.src.ejb.sessionBeans.cim.ContainerCommonFunction-->ContainerCommonFunctionEJB-->getIsoHeightWidth()
		private List<String> getIsoHeightWidth(String ISO, String oldNewInd) throws BusinessException {

			if (ISO == null)
				return null;

			if (ISO.length() != 4)
				return null;

			SqlRowSet rs = null;
			Map<String, String> paramMap = new HashMap<String, String>();
			
			try {
				log.info("START: getIsoHeightWidth  DAO  Start ISO:" + ISO + " oldNewInd:" + oldNewInd);

				String secChar = ISO.substring(1, 2);
				// logMsg("ISO sec char " + secChar);
				List<String> isoHeightWidth = new ArrayList<String>();
				String getIsoHeightSQL = "Select cntr_height_ft,cntr_width_ft from cntr_height_code where REC_STATUS='A' and old_new_ind= :oldNewInd and cntr_height_cd=:secChar";
				
				// logMsg(getIsoHeightSQL);
				paramMap.put("oldNewInd", parseString(oldNewInd));
				paramMap.put("secChar", parseString(secChar));
				log.info(" ***getIsoHeightWidth SQL *****" + getIsoHeightSQL.toString());

				rs = namedParameterJdbcTemplate.queryForRowSet(getIsoHeightSQL.toString(), paramMap);

				if (rs.next()) {
					isoHeightWidth.add(rs.getString("cntr_height_ft"));
					isoHeightWidth.add(rs.getString("cntr_width_ft"));
				}
				if (isoHeightWidth.size() != 2)
					return null;

				log.info("END: ** getIsoHeightWidth Result ****" + isoHeightWidth.toString());

				return isoHeightWidth;
			} catch (NullPointerException e) {
				log.info("Exception getIsoHeightWidth :" , e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception getIsoHeightWidth :" , e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: getIsoHeightWidth  DAO  END");
			}
		}

	// jp.src.ejb.sessionBeans.cim.ContainerCommonFunction-->ContainerCommonFunctionEJB-->getIsoOldNewInd()
		private String getIsoOldNewInd(String ISO) {
			if (ISO == null)
				return null;
			if (ISO.length() != 4)
				return null;
			String thirdChar = ISO.substring(2, 3);
			// logMsg("ISO third char " + thirdChar);
			try {

				int i = Integer.parseInt(thirdChar);
				log.info("i =" + i);
				return "O";
			} catch (NumberFormatException nfe) {
				return "N";
			}
			// return "N";
		}
		
		private String parseString(String inputStr) {

			inputStr = CommonUtility.deNull(inputStr);
			inputStr = inputStr.trim().toUpperCase();
			return inputStr;
		}
	
	// ejb.sessionBeans.cim.ContainerCommonFunction-->ContainerCommonFunctionEJB-->isTSContainerExitToIM()
	@Override
	public boolean isTSContainerExitToIM(int cntrSeqNbr) throws BusinessException {
		boolean isTSContainerExitToIM = false;
		String purpCd = "";
		String nomLoadVvCd = "";
		String loadVvCd = "";
		String shipmentStatus = "";
		SqlRowSet rs = null;
		StringBuilder sb = new StringBuilder();
		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		try {
			log.info("START: isTSContainerExitToIM  DAO  Start cntrSeqNbr:" + cntrSeqNbr);

			sb.append(" SELECT purp_cd, load_vv_cd, nom_load_vv_cd, shipment_status ");
			sb.append(" FROM cntr WHERE cntr_seq_nbr = :cntrSeqNbr ");
			
			log.info(" ***isTSContainerExitToIM SQL *****" + sb.toString());

			paramMap.put("cntrSeqNbr", cntrSeqNbr);

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			if (rs.next()) {
				purpCd = rs.getString("purp_cd");
				loadVvCd = CommonUtility.deNull(rs.getString("load_vv_cd"));
				nomLoadVvCd = CommonUtility.deNull(rs.getString("nom_load_vv_cd"));
				shipmentStatus = CommonUtility.deNull(rs.getString("shipment_status"));
			}

			log.info("ContainerCommonFunctionsEJB.isTSContainerExitToIM - cntr " + cntrSeqNbr + "/" + purpCd + "/"
					+ loadVvCd + "/" + nomLoadVvCd);
			if (purpCd.equalsIgnoreCase("TS") || purpCd.equalsIgnoreCase("RE")) {
				if (shipmentStatus.equalsIgnoreCase("SO")
						|| (loadVvCd.equalsIgnoreCase("") && nomLoadVvCd.equalsIgnoreCase(""))) {
					isTSContainerExitToIM = true;
				}
			}
			log.info("END: ** isTSContainerExitToIM Result ****" + isTSContainerExitToIM);
		} catch (NullPointerException e) {
			log.info("Exception isTSContainerExitToIM : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception isTSContainerExitToIM : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isTSContainerExitToIM  DAO  END");
		}
		return isTSContainerExitToIM;
	}

	
	// jp.src.ejb.sessionBeans.cim.ContainerCommonFunction-->ContainerCommonFunctionEJB-->getIsoType()
		private String getIsoType(String ISO, String oldNewInd) throws BusinessException {
			if (ISO == null)
				return null;

			if (ISO.length() != 4)
				return null;

			SqlRowSet rs = null;
			Map<String, String> paramMap = new HashMap<String, String>();
			StringBuilder sb = new StringBuilder();

			try {
				log.info("START: getIsoType  DAO  Start ISO:" + ISO + " oldNewInd:" + oldNewInd);

				String lastTwoChar = ISO.substring(2, 4);
				// logMsg("ISO last two char " + lastTwoChar);
				String isoType = null;

				sb.append(" Select cntr_type_cd from cntr_type_code where REC_STATUS='A' ");
				sb.append(" and old_new_ind=:oldNewInd and cntr_type_cd=:lastTwoChar ");
				// logMsg(getIsoTypeSQL);

				
				log.info(" ***getIsoType SQL *****" + sb.toString());

				paramMap.put("oldNewInd", parseString(oldNewInd));
				paramMap.put("lastTwoChar", parseString(lastTwoChar));
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				if (rs.next()) {
					isoType = rs.getString("cntr_type_cd");
				}
				isoType = CommonUtility.deNull(isoType);
				if (isoType.length() == 0)
					return null;

				log.info("END: ** getIsoType Result ****" + isoType);

				return isoType;
			} catch (NullPointerException e) {
				log.info("Exception getIsoType :" , e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception getIsoType :" , e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: getIsoType  DAO  END");
			}
		}
}
