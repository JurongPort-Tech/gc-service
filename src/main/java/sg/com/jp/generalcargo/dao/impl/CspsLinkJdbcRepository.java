package sg.com.jp.generalcargo.dao.impl;

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

import sg.com.jp.generalcargo.dao.CspsLinkRepository;
import sg.com.jp.generalcargo.domain.CspsLinkValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

@Repository("cspsLinkRepository")
public class CspsLinkJdbcRepository implements CspsLinkRepository{

	private static final Log log = LogFactory.getLog(CspsLinkJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	//ejb.sessionBeans.gbcc.cspsLink -->CspsLinkEJB

	
	 
	@Override
	public List<CspsLinkValueObject> getLocationListBasedOnLocationType(String locType) throws BusinessException {
		List<CspsLinkValueObject> zoneList = new ArrayList<CspsLinkValueObject>();
		CspsLinkValueObject cspsLinkObj;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = null;

		try {
			log.info("START: getLocationListBasedOnLocationType  DAO  Start Obj "+" locType:"+locType );

			if ("B".equalsIgnoreCase(locType)) {
				sql = " SELECT BERTH_NBR LOC_CD, BERTH_NBR LOC_DESC FROM BERTHING_LENGTH ORDER BY DISPLAY_ORDER_NBR";

			} else if ("Y".equalsIgnoreCase(locType)) {
				//Amended by Jade for SL-GBCC-20140923-01
				//sql = " SELECT DISTINCT STG_ZONE LOC_CD, STG_ZONE LOC_DESC FROM CSPS_MD_STORAGE WHERE STG_TYPE='Y' ORDER BY STG_ZONE ";
				sql = "SELECT DISTINCT ZONE_CD LOC_CD, DECODE(ZONE_CD, 'MLW', 'WEST', 'MLE', 'EAST', 'MLC', 'CENTRAL', 'PDL', 'PDL') LOC_DESC FROM SMART_BLK WHERE REC_STATUS = 'A' AND BLK_TYPE_CD = 'Y' ORDER BY LOC_DESC"; 			

			} else if ("W".equalsIgnoreCase(locType)) {
				//Amended by Jade for SL-GBCC-20140923-01
				//sql = " SELECT DISTINCT STG_ZONE LOC_CD, STG_ZONE LOC_DESC FROM CSPS_MD_STORAGE WHERE STG_TYPE='W' ORDER BY STG_ZONE ";
				sql = "SELECT DISTINCT ZONE_CD LOC_CD, DECODE(ZONE_CD, 'MLW', 'WEST', 'MLE', 'EAST', 'MLC', 'CENTRAL', 'PDL', 'PDL') LOC_DESC FROM SMART_BLK WHERE REC_STATUS = 'A' AND BLK_TYPE_CD = 'W' ORDER BY LOC_DESC"; 

			} else if("L".equalsIgnoreCase(locType)) {
				sql = " SELECT MISC_TYPE_CD LOC_CD, MISC_TYPE_NM LOC_DESC FROM MISC_TYPE_CODE WHERE CAT_CD = 'LEASE_AREA' AND REC_STATUS = 'A' ORDER BY MISC_TYPE_NM ";

			} else if("O".equalsIgnoreCase(locType)) {
				sql = " SELECT MISC_TYPE_CD LOC_CD, MISC_TYPE_NM LOC_DESC FROM MISC_TYPE_CODE WHERE CAT_CD = 'OTHER_AREA' AND REC_STATUS = 'A' ORDER BY MISC_TYPE_NM ";

			} else {
				cspsLinkObj = new CspsLinkValueObject();
				cspsLinkObj.setLocType("E");
				cspsLinkObj.setLocCd("ERROR");
				cspsLinkObj.setLocDesc("Invalid Location Type");
				zoneList.add(cspsLinkObj);
				return zoneList;
			}

			log.info("@@@@@@ In getLocationListBasedOnLocationType, sql = " + sql);

			rs = 	namedParameterJdbcTemplate.queryForRowSet(sql,paramMap);


			while (rs.next()) {	
				cspsLinkObj = new CspsLinkValueObject();
				cspsLinkObj.setLocType(locType);
				cspsLinkObj.setLocCd(rs.getString("LOC_CD"));
				cspsLinkObj.setLocDesc(rs.getString("LOC_DESC"));

				zoneList.add(cspsLinkObj);
			}
			log.info("END: *** getLocationListBasedOnLocationType Result *****" + zoneList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getLocationListBasedOnLocationType " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getLocationListBasedOnLocationType " , e);
			throw new BusinessException("M4201");
		}finally{
			log.info("END: getLocationListBasedOnLocationType  DAO  END");
		}
		return zoneList;
	}

	@Override
	public List<CspsLinkValueObject> getAreaListBasedOnStorageZone(String stgType, String stgZone)
			throws BusinessException {

		List<CspsLinkValueObject> areaList = new ArrayList<CspsLinkValueObject>();
		CspsLinkValueObject cspsLinkObj;
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		String sql = null;

		//Amended by Jade SL-GBCC-20140923-01
		//sql = " SELECT STG_NM FROM CSPS_MD_STORAGE WHERE STG_TYPE ='" + stgType
		//		+ "'  AND STG_ZONE='" + stgZone + "' AND (EFF_DATE  <= SYSDATE) AND  (END_DATE IS NULL OR END_DATE > SYSDATE) ORDER BY STG_NM ";

		String smartZone = getZone4Smart(stgZone);
		log.info("@@@@@@ In getAreaListBasedOnStorageZone, smartZone = " + smartZone);

		try {
			log.info("START: getAreaListBasedOnStorageZone  DAO  Start Obj "+"stgType:"+stgType +"stgZone:"+stgZone );

			sb.append("SELECT BLK_NM STG_NM FROM SMART_BLK WHERE REC_STATUS = 'A' AND BLK_TYPE_CD =:stgType AND ZONE_CD =:smartZone  ORDER BY STG_NM");
			sql = sb.toString();
			paramMap.put("smartZone", smartZone);
			log.info("@@@@@@ In getAreaListBasedOnStorageZone, sql = " + sql);
			paramMap.put("stgType", stgType);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql,paramMap);


			while (rs.next()) {
				cspsLinkObj = new CspsLinkValueObject();
				cspsLinkObj.setLocType(stgType);
				cspsLinkObj.setStgZone(stgZone);
				cspsLinkObj.setStgName(rs.getString("STG_NM"));
				areaList.add(cspsLinkObj);
			}
			log.info("END: *** getAreaListBasedOnStorageZone Result *****" + areaList.toString());
		} catch (NullPointerException e) {
			log.info("exception: getAreaListBasedOnStorageZone " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("exception: getAreaListBasedOnStorageZone " , e);
			throw new BusinessException("M4201");
		}finally{
			log.info("END: getAreaListBasedOnStorageZone  DAO  END");
		}
		return areaList;
	}

	private String getZone4Smart(String stgZone) {		
		String result = "";
		log.info("@@ in getZone4Smart, stgZone = " + stgZone);

		if (stgZone != null && stgZone.trim().length() > 0) {
			if (stgZone.trim().equalsIgnoreCase("EAST")) {
				result = "MLE";
			} else if (stgZone.trim().equalsIgnoreCase("WEST")) {
				result = "MLW";
			} else if (stgZone.trim().equalsIgnoreCase("CENTRAL")) {
				result = "MLC";
			} else {
				result = stgZone.trim();
			}
		}		

		log.info("@@ in getZone4Smart, result = " + result);

		return result;
	}

}
