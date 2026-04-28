package sg.com.jp.generalcargo.dao.impl;

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
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.JPPsaRepository;
import sg.com.jp.generalcargo.domain.JPPSAValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("jpPsaRepository")
public class JPPsaJdbcRepository implements JPPsaRepository {

	private static final Log log = LogFactory.getLog(JPPsaJdbcRepository.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	// ejb.sessionBeans.gbms.jppsa-->jppsa

	@Transactional(rollbackFor = BusinessException.class)
	public String updateIGD(String filename, String VoyageNo, String mode, String status) throws BusinessException {

		String sql = "";
		int k = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {
			log.info("START: updateIGD  DAO  Start filename " + filename + " VoyageNo" + VoyageNo + " mode" + mode
					+ " status" + status);

			sql = "update igd_detail_buffer set is_processed = :status , file_nm = :filename where DISC_VV_NO = :VoyageNo and is_processed ='N' ";
		
			paramMap.put("status", status);
			paramMap.put("filename", filename);
			paramMap.put("VoyageNo", VoyageNo);
			log.info(" updateIGD  DAO  SQL " + sql);
			log.info(" *** updateIGD params *****" + paramMap.toString());

			k = namedParameterJdbcTemplate.update(sql, paramMap);

			if (k == 1)
				status = "Y";
			else
				status = "N";
			log.info("END: *** updateIGD Result *****" + CommonUtility.deNull(status));

		} catch (NullPointerException ne) {
			log.info("Exception updateIGD : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception updateIGD : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: updateIGD  DAO  END");
		}
		return status;

	}

	public List<JPPSAValueObject> searchByVoyage(String VoyageNo, String mode) throws BusinessException {

		String sql = "";
		int headerOnce = 0;
		List<JPPSAValueObject> igdAllVect = new ArrayList<JPPSAValueObject>();
		List<JPPSAValueObject> igdDetailsVect = new ArrayList<JPPSAValueObject>();
		JPPSAValueObject jppsavalueObj = new JPPSAValueObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		sql = "SELECT  igd_detail_buffer.* , to_char(SYSDATE,'YYYYMMDDHHMI') as createdate,to_char(sysdate,'YYYYMMDDHHMISS') AS DateVal  from igd_detail_buffer where DISC_VV_NO = :VoyageNo and IS_PROCESSED = 'N' ";
		try {

			log.info("START: searchByVoyage  DAO  Start VoyageNo " + VoyageNo + " mode" + mode);
		

			paramMap.put("VoyageNo", VoyageNo);

			log.info(" searchByVoyage  DAO  SQL " + sql);
			log.info(" *** searchByVoyage params *****" + paramMap.toString());
			JPPSAValueObject jppsavalueObj1;
			for (rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap); rs.next(); igdDetailsVect.add(jppsavalueObj1)) {
				if (headerOnce == 0) {
					jppsavalueObj.setICHRecType("H");
					jppsavalueObj.setICHCreateDate(CommonUtility.deNull(rs.getString("createdate")));
					jppsavalueObj.setICHAbbVslName(CommonUtility.deNull(rs.getString("DISC_AB_VSL_NM")));
					jppsavalueObj.setICHDisAbbVslName(CommonUtility.deNull(rs.getString("DISC_VV_NO")));
					headerOnce = 1;
				}
				jppsavalueObj1 = new JPPSAValueObject();
				jppsavalueObj1.setICDRecordType("D");
				jppsavalueObj1.setICDFunction(mode);
				jppsavalueObj1.setICDBillofLading(CommonUtility.deNull(rs.getString("BL_NO")));
				jppsavalueObj1.setICDHScode(CommonUtility.deNull(rs.getString("HS_CD")));
				jppsavalueObj1.setICDPackageType(CommonUtility.deNull(rs.getString("PKG_TYPE")));
				jppsavalueObj1.setICDNoofPackage(CommonUtility.deNull(rs.getString("NO_OF_PKG")));
				jppsavalueObj1.setICDWeight(CommonUtility.deNull(rs.getString("WEIGHT")));
				jppsavalueObj1.setICDVolume(CommonUtility.deNull(rs.getString("VOLUME")));
				jppsavalueObj1.setICDDGIndicator(CommonUtility.deNull(rs.getString("DG_IND")));
				jppsavalueObj1.setICDShipperName(CommonUtility.deNull(rs.getString("SHIPPER")));
				jppsavalueObj1.setICDCargoType(CommonUtility.deNull(rs.getString("CRG_TYPE")));
				jppsavalueObj1.setICDLoadingVessel(CommonUtility.deNull(rs.getString("LGD_VSL")));
				jppsavalueObj1.setICDLoadingVoyage(CommonUtility.deNull(rs.getString("LDG_VV_CD")));
				jppsavalueObj1.setICDPortOfDischarge(CommonUtility.deNull(rs.getString("DISC_PORT")));
				jppsavalueObj1.setICDContainerNumber(CommonUtility.deNull(rs.getString("CNT_NBR")));
				jppsavalueObj1.setICDDirectInterGateWay(CommonUtility.deNull(rs.getString("DIRECTION")));
				jppsavalueObj1.setICDBookingRef(CommonUtility.deNull(rs.getString("BK_REF_NBR")));
				jppsavalueObj1.setICDAccount(CommonUtility.deNull(rs.getString("ACCT_NBR")));
				jppsavalueObj1.setICDTdbNo(CommonUtility.deNull(rs.getString("TDB_CR_NBR")));
				jppsavalueObj1.setICDCargoDescription(CommonUtility.deNull(rs.getString("CRG_DESC")));
				jppsavalueObj1.setICDMarking(CommonUtility.deNull(rs.getString("MARKINGS")));
				jppsavalueObj1.setDate(CommonUtility.deNull(rs.getString("DateVal")));
			}

			jppsavalueObj.setCargoDetails(igdDetailsVect);
			igdAllVect.add(jppsavalueObj);

			log.info("END: *** searchByVoyage Result *****" + igdAllVect.size());
		} catch (NullPointerException ne) {
			log.info("Exception searchByVoyage : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception searchByVoyage : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: searchByVoyage  DAO  END");		}

		return igdAllVect;

	}

	@Transactional(rollbackFor = BusinessException.class)
	public String insertIGD(JPPSAValueObject jpvalueObj) throws BusinessException {

		String sql = "";
		String status = "N";
		int k = 0;
		List<JPPSAValueObject> cargoVect = new ArrayList<JPPSAValueObject>();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {
			log.info("START: insertIGD  DAO  Start JPPSAValueObject " + jpvalueObj.toString());
		

			cargoVect = jpvalueObj.getCargoDetails();
			for (int i = 0; i < cargoVect.size(); i++) {
				JPPSAValueObject jpvalueMultiple = new JPPSAValueObject();
				jpvalueMultiple = (JPPSAValueObject) cargoVect.get(i);
				if (jpvalueMultiple.getICDFunction().trim().equals("U")) {
					String sql11 = "Update igd_detail_buffer set is_processed = 'A' WHERE BK_REF_NBR = :iCDBookingRef AND LDG_VV_CD = :iCDLoadingVoyage ";

					paramMap.put("iCDBookingRef", jpvalueMultiple.getICDBookingRef().trim());
					paramMap.put("iCDLoadingVoyage", jpvalueMultiple.getICDLoadingVoyage().trim());
					log.info(" insertIGD  DAO  SQL " + sql11);
					log.info(" *** insertIGD params *****" + paramMap.toString());
					k = namedParameterJdbcTemplate.update(sql11, paramMap);
				}
				if (jpvalueMultiple.getICDFunction().trim().equals("D")) {
					String sql11 = "Update igd_detail_buffer set is_processed = 'A' , NO_OF_PKG = 0 ,WEIGHT = '0', VOLUME = '0'  WHERE BK_REF_NBR = :iCDBookingRef AND LDG_VV_CD = :iCDLoadingVoyage";

					paramMap.put("iCDBookingRef", jpvalueMultiple.getICDBookingRef().trim());
					paramMap.put("iCDLoadingVoyage", jpvalueMultiple.getICDLoadingVoyage().trim());
					log.info(" insertIGD  DAO  SQL " + sql11);
					log.info(" *** insertIGD params *****" + paramMap.toString());
					k = namedParameterJdbcTemplate.update(sql11, paramMap);
				}

				StringBuffer sb = new StringBuffer();
				sb.append(
						"insert into igd_detail_buffer (FILE_NM,DISC_AB_VSL_NM,DISC_VV_NO,REC_FUNCTION,BL_NO,HS_CD,PKG_TYPE,NO_OF_PKG, ");
				sb.append(
						"WEIGHT,VOLUME,DG_IND,SHIPPER,CRG_TYPE,LGD_VSL,LDG_VV_CD,DISC_PORT,CNT_NBR,DIRECTION,BK_REF_NBR,ACCT_NBR,TDB_CR_NBR, ");
				sb.append("crg_desc,markings,is_processed,error,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM) values( ");
				sb.append(
						":fileName,:icHAbbVslName,:iCHDisAbbVslName,:icDFunction,:iCDBillofLading,:icDHScode,:iCDPackagingType,:iCDNoofPackage, ");
				sb.append(
						":iCDWeight,:iCDVolume,:iCDDGIndicator,:iCDShipperName,:iCDCargoType,:iCDLoadingVessel,:iCDLoadingVoyage,:iCDPortOfDischarge, ");
				sb.append(
						":iCDContainerNumber,:iCDDirectInterGateWay,:iCDBookingRef,:iCDAccount,:iCDTdbNo,:iCDCargoDescription,:iCDMarking,'N','',:user ");
				sb.append(",sysdate)");
				sql = sb.toString();

				paramMap.put("fileName",
						!StringUtils.isEmpty(jpvalueObj.getFileName()) ? jpvalueObj.getFileName().trim() : "");
				paramMap.put("icHAbbVslName",
						!StringUtils.isEmpty(jpvalueObj.getICHAbbVslName()) ? jpvalueObj.getICHAbbVslName().trim()
								: "");
				paramMap.put("iCHDisAbbVslName",
						!StringUtils.isEmpty(jpvalueObj.getICHDisAbbVslName()) ? jpvalueObj.getICHDisAbbVslName().trim()
								: "");
				paramMap.put("icDFunction",
						!StringUtils.isEmpty(jpvalueMultiple.getICDFunction()) ? jpvalueMultiple.getICDFunction().trim()
								: "");
				paramMap.put("iCDBillofLading",
						!StringUtils.isEmpty(jpvalueMultiple.getICDBillofLading())
								? jpvalueMultiple.getICDBillofLading().trim()
								: "");
				paramMap.put("iCDBillofLading",
						!StringUtils.isEmpty(jpvalueMultiple.getICDBillofLading())
								? jpvalueMultiple.getICDBillofLading().trim()
								: "");
				paramMap.put("icDHScode",
						!StringUtils.isEmpty(jpvalueMultiple.getICDHScode()) ? jpvalueMultiple.getICDHScode().trim()
								: "");
				paramMap.put("iCDPackagingType", jpvalueMultiple.getICDPackagingType());
				paramMap.put("iCDNoofPackage",
						!StringUtils.isEmpty(jpvalueMultiple.getICDNoofPackage())
								? jpvalueMultiple.getICDNoofPackage().trim()
								: "");
				paramMap.put("iCDWeight",
						!StringUtils.isEmpty(jpvalueMultiple.getICDWeight()) ? jpvalueMultiple.getICDWeight().trim()
								: "");
				paramMap.put("iCDVolume",
						!StringUtils.isEmpty(jpvalueMultiple.getICDVolume()) ? jpvalueMultiple.getICDVolume().trim()
								: "");
				paramMap.put("iCDDGIndicator",
						!StringUtils.isEmpty(jpvalueMultiple.getICDDGIndicator())
								? jpvalueMultiple.getICDDGIndicator().trim()
								: "");
				paramMap.put("iCDShipperName",
						!StringUtils.isEmpty(jpvalueMultiple.getICDShipperName())
								? jpvalueMultiple.getICDShipperName().trim()
								: "");
				paramMap.put("iCDCargoType",
						!StringUtils.isEmpty(jpvalueMultiple.getICDCargoType())
								? jpvalueMultiple.getICDCargoType().trim()
								: "");
				paramMap.put("iCDLoadingVessel",
						!StringUtils.isEmpty(jpvalueMultiple.getICDLoadingVessel())
								? jpvalueMultiple.getICDLoadingVessel().trim()
								: "");
				paramMap.put("iCDLoadingVoyage",
						!StringUtils.isEmpty(jpvalueMultiple.getICDLoadingVoyage())
								? jpvalueMultiple.getICDLoadingVoyage().trim()
								: "");
				paramMap.put("iCDPortOfDischarge",
						!StringUtils.isEmpty(jpvalueMultiple.getICDPortOfDischarge())
								? jpvalueMultiple.getICDPortOfDischarge().trim()
								: "");
				paramMap.put("iCDContainerNumber",
						!StringUtils.isEmpty(jpvalueMultiple.getICDContainerNumber())
								? jpvalueMultiple.getICDContainerNumber().trim()
								: "");
				paramMap.put("iCDDirectInterGateWay",
						!StringUtils.isEmpty(jpvalueMultiple.getICDDirectInterGateWay())
								? jpvalueMultiple.getICDDirectInterGateWay().trim()
								: "");
				paramMap.put("iCDBookingRef",
						!StringUtils.isEmpty(jpvalueMultiple.getICDBookingRef())
								? jpvalueMultiple.getICDBookingRef().trim()
								: "");
				paramMap.put("iCDAccount",
						!StringUtils.isEmpty(jpvalueMultiple.getICDAccount()) ? jpvalueMultiple.getICDAccount().trim()
								: "");
				paramMap.put("iCDTdbNo",
						!StringUtils.isEmpty(jpvalueMultiple.getICDTdbNo()) ? jpvalueMultiple.getICDTdbNo().trim()
								: "");
				paramMap.put("iCDCargoDescription",
						!StringUtils.isEmpty(jpvalueMultiple.getICDCargoDescription())
								? jpvalueMultiple.getICDCargoDescription().trim()
								: "");
				paramMap.put("iCDMarking",
						!StringUtils.isEmpty(jpvalueMultiple.getICDMarking()) ? jpvalueMultiple.getICDMarking().trim()
								: "");
				paramMap.put("user", jpvalueObj.getUser());

				log.info(" insertIGD  DAO  SQL " + sql);
				log.info(" *** insertIGD params *****" + paramMap.toString());
				k = namedParameterJdbcTemplate.update(sql, paramMap);
			}
			if (k == 1)
				status = "Y";
			else
				status = "N";

			log.info("END: *** insertIGD Result *****" + CommonUtility.deNull(status));
		} catch (NullPointerException ne) {
			log.info("Exception insertIGD : ", ne);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception insertIGD : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: insertIGD  DAO  END");

		}

		return status;

	}

}
