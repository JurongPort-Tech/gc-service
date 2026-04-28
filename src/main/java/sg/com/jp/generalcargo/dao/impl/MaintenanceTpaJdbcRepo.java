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

import sg.com.jp.generalcargo.dao.MaintenanceTpaRepo;
import sg.com.jp.generalcargo.util.BusinessException;

@Repository("maintenanceTpaRepo")
public class MaintenanceTpaJdbcRepo implements MaintenanceTpaRepo {

	private static final Log log = LogFactory.getLog(MaintenanceTpaJdbcRepo.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	// jp.src.ejb.sessionBeans.codes.MaintenanceTpa-->MaintenanceTpaEJB-->getParkingAreaList()
	@Override
	public List<String> getParkingAreaList() throws BusinessException {
		List<String> areaList = new ArrayList<String>();
		String areaCode;
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null;
		Map<String,String>  paramMap = new HashMap<String,String>();
		try {
			log.info("START: getParkingAreaList  DAO  Start ");

			// GET_LIST_PARKING_AREA
			sb.append(" SELECT DISTINCT AREA_CD FROM ");
			sb.append(" MISC_PARKING_SLOT mpl INNER JOIN MISC_TYPE_CODE mtc ");
			sb.append(" ON ( mpl.SLOT_STATUS = mtc.MISC_TYPE_CD AND mtc.CAT_CD ");
			sb.append(" ='TPA_SLT_ST') ORDER BY mpl.AREA_CD ");

			log.info(" ***getParkingAreaList SQL *****" + sb.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);

			while (rs.next()) {
				areaCode = rs.getString(1);
				areaList.add(areaCode);
			}

			log.info("END: ** getParkingAreaList Result ****" + areaList.toString());
		} catch (NullPointerException e) {
			log.info("Exception getParkingAreaList: " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getParkingAreaList: " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getParkingAreaList  DAO  END");
		}
		return areaList;
	}

}
