package sg.com.jp.generalcargo.dao.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.jp.generalcargo.dao.BookingRefRepository;
import sg.com.jp.generalcargo.dao.EsnRepository;
import sg.com.jp.generalcargo.domain.BkRefActionTrail;
import sg.com.jp.generalcargo.domain.BkRefActionTrailDetails;
import sg.com.jp.generalcargo.domain.BkRefUploadConfig;
import sg.com.jp.generalcargo.domain.BookRefvoyageOutwardValueObject;
import sg.com.jp.generalcargo.domain.BookingReference;
import sg.com.jp.generalcargo.domain.BookingReferenceFileUploadDetails;
import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.BkRefActionTrailDetails;
import sg.com.jp.generalcargo.domain.PageDetails;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.Template;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.GbmsCommonUtility;

@Repository("BookingRefJdbcRepo")
public class BookingRefJdbcRepository implements BookingRefRepository {

	public final boolean isToLogTxn = true;
	private static final Log log = LogFactory.getLog(BookingRefJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	@Autowired
	private EsnRepository esnRepo;

	// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->getCargoType()
	@Override
	public List<List<String>> getCargoType() throws BusinessException {

		SqlRowSet  rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = "";
	
		List<String> cargoVect = new ArrayList<String>();
        List<String> cargoTempvect = new ArrayList<String>(cargoVect);
		List<List<String>> cargoTypeRowVect = new ArrayList<List<String>>();
			
		try {
			log.info("START getCargoType DAO");
			sql = "SELECT CRG_TYPE_CD,CRG_TYPE_NM from crg_type where rec_status='A' and crg_type_cd not in ('00','01','02','03') ORDER BY CRG_TYPE_CD";
		
			log.info(" ***SQL *****" + sql.toString());
			log.info(" ***paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			for (; rs.next(); cargoVect.clear()) {
				cargoVect.add(CommonUtility.deNull(rs.getString("CRG_TYPE_CD")));
				cargoVect.add(CommonUtility.deNull(rs.getString("CRG_TYPE_NM")));
				cargoTempvect = new ArrayList<String>(cargoVect);
				cargoVect.clear();				
				cargoTypeRowVect.add(cargoTempvect);
			}
			log.info("END: *** getCargoType Result *****" + cargoTypeRowVect.toString());
		} catch (Exception se) {
			log.info("Exception getCargoType : ", se);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getCargoType DAO");
		}
		return cargoTypeRowVect;
	}

	// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->chkCancelAmend()
	@Override
	public String chkCancelAmend(String bkRefNumber, String bkCmpCode, String mode)
			throws BusinessException {

		SqlRowSet  rs = null;
		SqlRowSet  rs1 = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();

		String sql = "";
		String sqlvar = "";
		String status = "N";
		try {
			log.info("START chkCancelAmend DAO bkrefnumber: " + CommonUtility.deNull(bkRefNumber) + ", bkcompcode : " + CommonUtility.deNull(bkCmpCode)
					+ ", mode: " + CommonUtility.deNull(mode));
			sqlvar = "select a.gb_close_shp_ind as gb_close_shp_ind from bk_details a, vessel_call b where b.vv_cd = a.var_nbr and bk_ref_nbr =:bkRefNumber ";
			sql = "select *  from esn where bk_ref_nbr =:bkRefNumber and esn_status = 'A'";
			
			if ((!bkCmpCode.equals("JP")) || mode.equals("C")) {
				paramMap.put("bkRefNumber", bkRefNumber);
				log.info("chkCancelAmend SQL " + sql.toString() + ", paramMap = " + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs.next()) {
					status = "Y_ESN_OPEN";
				}
			}
			
			paramMap.put("bkRefNumber", bkRefNumber);
			log.info("chkCancelAmend SQL " + sqlvar.toString() + ", paramMap = " + paramMap.toString());
			rs1 = namedParameterJdbcTemplate.queryForRowSet(sqlvar, paramMap);
			if (rs1.next()) {
				if (CommonUtility.deNull(rs1.getString("gb_close_shp_ind")).equals("Y")) {
					status = "Y_CLOSE_SHIP";
				}
			}
			log.info("END: *** chkCancelAmend Result *****" + CommonUtility.deNull(status));
		} catch (Exception se) {
			log.info("Exception chkCancelAmend : ", se);
			throw new BusinessException("M4201");
		} finally {
			log.info("END chkCancelAmend DAO");
		}
		return status;
	}

	// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->getCheckUserBookingReference()
	@Override
	public boolean getCheckUserBookingReference(String coCd, String brNo) throws BusinessException {
		
		SqlRowSet  rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		boolean retVal = false;
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START getCheckUserBookingReference DAO : cocd = " + CommonUtility.deNull(coCd) + ", brNo : " + CommonUtility.deNull(brNo));
			sb.append(" SELECT COUNT(*) as BK_COUNT ");
			sb.append(" FROM VESSEL_CALL VC,BK_DETAILS BD WHERE VC.VV_CD = BD.VAR_NBR ");
			sb.append(" and BD.BK_REF_NBR =:brNo and BD.BK_CREATE_CD =:coCd ");
			log.info("SQL Check User Booking Reference=== " + sb.toString());
			paramMap.put("brNo", brNo);
			paramMap.put("coCd", coCd);
			log.info("getCheckUserBookingReference SQL " + sb.toString() + ", paramMap = " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			while (rs.next()) {
				log.info("BK_COUNT===== " + rs.getInt("BK_COUNT"));
				if (rs.getInt("BK_COUNT") > 0) {
					retVal = true;
				}
			}
			log.info("END: *** getCheckUserBookingReference Result *****" + retVal);

		} catch (Exception e) {
			log.info("Exception getCheckUserBookingReference : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getCheckUserBookingReference DAO");
		}
		return retVal;
	}

	// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->retrieveMaxCargoTon()
	@Override
	public int retrieveMaxCargoTon(String vvCd) throws  BusinessException {
		int maxCargoTon = 0;
	
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {
			log.info("START retrieveMaxCargoTon DAO vvcd : " + CommonUtility.deNull(vvCd));
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT MAX_CARGO_TON ");
			sql.append(" FROM VESSEL_SCHEME ");
			sql.append(" WHERE SCHEME_CD = ( ");
			sql.append(" SELECT SCHEME ");
			sql.append(" FROM VESSEL_CALL ");
			sql.append(" WHERE VV_CD =:vvCd ");
			sql.append(" )");
			
			paramMap.put("vvCd", vvCd);
			log.info("retrieveMaxCargoTon SQL " + sql.toString() + ", paramMap = " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			if (rs.next()) {				
					maxCargoTon = Integer.parseInt(rs.getString("MAX_CARGO_TON"));
			}
			log.info("END: *** retrieveMaxCargoTon Result *****" + maxCargoTon);
		} catch (Exception e) {
			log.info("Exception retrieveMaxCargoTon : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END retrieveMaxCargoTon DAO");
		}
		return maxCargoTon;
	}

	@Override
	// ejb.sessionBeans.gbms.cargo.bookingReference--->BookingReferenceEJBBean-->fetchBKDetails()
	public List<BookingReferenceValueObject> fetchBKDetails(String bkRefNo) throws BusinessException {
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		List<BookingReferenceValueObject> BKDetailsVect = new ArrayList<BookingReferenceValueObject>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: fetchBKDetails DAO bkRefNo:" + CommonUtility.deNull(bkRefNo));
			sb.append(
					" SELECT a.*,b.Vsl_full_Nm , d.port_nm,f.co_nm,(SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) ");
			sb.append(" FROM customer where cust_cd =f.co_cd) tdb_cr_nbr, b.vsl_nm from bk_details a , ");
			sb.append(" vessel b ,vessel_call c, un_port_code d , customer e,company_code f ");
			sb.append(" where c.vsl_nm = b.VSL_NM and c.vv_cd = a.var_nbr and d.port_cd = a.port_dis ");
			sb.append(" and a.DECLARANT_CD = f.co_cd and e.cust_cd = f.co_cd and a.bk_ref_nbr =:bkRefNo ");
			paramMap.put("bkRefNo", bkRefNo);
			log.info("SQL" + sb.toString() + ", paramMap = " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
			BookingReferenceValueObject brvo = null;
			for (; rs.next(); BKDetailsVect.add(brvo)) {
				brvo = new BookingReferenceValueObject();
				brvo.setBrNo(CommonUtility.deNull(rs.getString("bk_ref_nbr")));
				brvo.setVesselName(CommonUtility.deNull(rs.getString("Vsl_full_Nm")));
				String shippercrnbr = CommonUtility.deNull(rs.getString("SHIPPER_CR_NBR"));
				brvo.setShipperCrNo(shippercrnbr);
				brvo.setVoyageNo(CommonUtility.deNull(rs.getString("out_voy_nbr")));
				brvo.setCargoType(CommonUtility.deNull(rs.getString("cargo_type")));
				brvo.setCargoCategory(CommonUtility.deNull(rs.getString("cargo_category_cd")));
				brvo.setPackageNos(CommonUtility.deNull(rs.getString("BK_NBR_PKGS")));
				brvo.setWeight(CommonUtility.deNull(rs.getString("bk_wt")));
				brvo.setVolume(CommonUtility.deNull(rs.getString("bk_vol")));
				brvo.setPackageVariance(CommonUtility.deNull(rs.getString("VARIANCE_PKGS")));
				brvo.setVolumeVariance(CommonUtility.deNull(rs.getString("VARIANCE_VOL")));
				brvo.setWeightVariance(CommonUtility.deNull(rs.getString("VARIANCE_WT")));
				brvo.setPortOfDischarge(CommonUtility.deNull(rs.getString("port_dis")));
				brvo.setShipperContact(CommonUtility.deNull(rs.getString("SHIPPER_CONTACT")));
				brvo.setShipperAddress(CommonUtility.deNull(rs.getString("SHIPPER_ADDR")));
				brvo.setShipperName(CommonUtility.deNull(rs.getString("SHIPPER_NM")));
				brvo.setShipperCoyCode(CommonUtility.deNull(rs.getString("SHIPPER_CD")));
				brvo.setEsnDeclarantNo(CommonUtility.deNull(rs.getString("tdb_cr_nbr")));
				brvo.setContainerType(CommonUtility.deNull(rs.getString("CNTR_TYPE")));
				brvo.setContainerSize(CommonUtility.deNull(rs.getString("CNTR_SIZE")));
				brvo.setNoContainer(CommonUtility.deNull(rs.getString("NBR_OF_CNTR")));
				brvo.setPortName(CommonUtility.deNull(rs.getString("port_nm")));
				brvo.setEsnDeclarantName(CommonUtility.deNull(rs.getString("co_nm")));
				brvo.setBkCreateCd(CommonUtility.deNull(rs.getString("BK_CREATE_CD")));
				brvo.setAbbrVslName(CommonUtility.deNull(rs.getString("vsl_nm")));
				// START CR FTZ - NS JUNE 2024
				brvo.setConName(CommonUtility.deNull(rs.getString("CONS_NM")));
				brvo.setConsigneeAddr(CommonUtility.deNull(rs.getString("CONSIGNEE_ADDR")));
				brvo.setNotifyParty(CommonUtility.deNull(rs.getString("NOTIFY_PARTY")));
				brvo.setNotifyPartyAddr(CommonUtility.deNull(rs.getString("NOTIFY_PARTY_ADDR")));
				brvo.setPlaceofDelivery(CommonUtility.deNull(rs.getString("PLACE_OF_DELIVERY")));
				brvo.setPlaceofReceipt(CommonUtility.deNull(rs.getString("PLACE_OF_RECEIPT")));
				brvo.setBlNbr(CommonUtility.deNull(rs.getString("BL_NBR")).toUpperCase());
				// END CR FTZ - NS JUNE 2024
				log.info("Added Shipper Name & code display:" + brvo.getShipperName() + ":" + brvo.getShipperCoyCode());
			}
			log.info("END: *** fetchBKDetails Result *****" + BKDetailsVect.toString());
		} catch (Exception ne) {
			log.info("Exception fetchBKDetails : ", ne);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: fetchBKDetails DAO bkRefNo:" + bkRefNo);
		}
		return BKDetailsVect;
	}

	@Override
	// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->getCargoCategoryCode_CargoCategoryName()
	public Map<String, String> getCargoCategoryCode_CargoCategoryName() throws BusinessException {
		Map<String, String> cc_cn = new HashMap<String, String>();
		String sql = "SELECT cc_cd, cc_name FROM cargo_category_code";
		Map<String,String> paramMap = new HashMap<String,String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getCargoCategoryCode_CargoCategoryName DAO");
			log.info("SQL" + sql);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			String cargoCategoryCode = "";
			String cargoCategoryName = "";
			while (rs.next()) {
				cargoCategoryCode = rs.getString("cc_cd");
				cargoCategoryName = rs.getString("cc_name");
				cc_cn.put(cargoCategoryCode, cargoCategoryName);
			}
			log.info("END: getCargoCategoryCode_CargoCategoryName RESULT: " + cc_cn);
		} catch (Exception e) {
			log.info("Exception getCargoCategoryCode_CargoCategoryName : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoCategoryCode_CargoCategoryName DAO");
		}
		return cc_cn;
	}

	// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->getCargoTypeNotShow()
	@Override
	public String getCargoTypeNotShow() throws BusinessException {
		String cargoType = "";
		String sql = "select VALUE from text_para where para_cd = 'CT_NOTSHOW'";
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: getCargoTypeNotShow DAO");

			log.info("SQL" + sql + "paramMap" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				cargoType = rs.getString("VALUE");
			}
			log.info("END: getCargoTypeNotShow DAO cargoType:" +  CommonUtility.deNull(cargoType));
		} catch (Exception e) {
			log.info("Exception getCargoTypeNotShow : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCargoTypeNotShow DAO END");
		}
		return cargoType;

	}

	// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->isShowAllCargoCategoryCode()
	@Override
	public boolean isShowAllCargoCategoryCode(String companyCode) throws BusinessException {
		try {
			log.info("START: isShowAllCargoCategoryCode DAO companyCode:" + CommonUtility.deNull(companyCode));
			String companyCodeAllCargoCategory = getCompanyCodeAllCargoCategory();
			String[] applicableCompanyCodes = companyCodeAllCargoCategory.split(",");
			for (String cc : applicableCompanyCodes) {
				if (CommonUtility.trimString(cc).equalsIgnoreCase(CommonUtility.trimString(companyCode))) {
					return true;
				}
			}
		} catch (Exception e) {
			log.info("Exception isShowAllCargoCategoryCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: DAO isShowAllCargoCategoryCode");
		}
		return false;
	}

	// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->getCompanyCodeAllCargoCategory()
	private String getCompanyCodeAllCargoCategory() throws BusinessException {
		String companyCode = "";
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		String sql = "select VALUE from text_para where PARA_CD = 'CMP_CD_CC'";
		try {
			log.info("START getCompanyCodeAllCargoCategory DAO:");
			log.info("SQL" + sql + "paramMap"+ paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				companyCode = rs.getString("VALUE");
			}
			log.info("END: *** getCompanyCodeAllCargoCategory Result *****" + CommonUtility.deNull(companyCode));
		} catch (Exception e) {
			log.info("Exception getCompanyCodeAllCargoCategory : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getCompanyCodeAllCargoCategory DAO");
		}
		return companyCode;
	}

	// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->getNotShowCargoCategoryCode()
	@Override
	public String getNotShowCargoCategoryCode() throws BusinessException {
		String cargoCategoryCode = "";
		String sql = "select VALUE from text_para where PARA_CD = 'CC_NOTSHOW'";
		Map<String,String> paramMap = new HashMap<String,String>();
		SqlRowSet rs = null;
		try {
			log.info("START: getNotShowCargoCategoryCode DAO");
			log.info("SQL" + sql);
			log.info(" *** getNotShowCargoCategoryCode params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				cargoCategoryCode = rs.getString("VALUE");
			}
			log.info("END: getNotShowCargoCategoryCode DAO cargoCategoryCode:" + CommonUtility.deNull(cargoCategoryCode));
		} catch (Exception e) {
			log.info("Exception getNotShowCargoCategoryCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getNotShowCargoCategoryCode DAO");
		}
		return cargoCategoryCode;
	}

	// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->getBRVOList()
	@Override
	public List<BookingReferenceValueObject> getBRVOList(String module) throws BusinessException {
		List<BookingReferenceValueObject> brvoList = new ArrayList<BookingReferenceValueObject>();
		try {
			log.info("START: getBRVOList DAO module:" + CommonUtility.deNull(module));
			String cargoType_cargoCategoryString = getParaCargoTypeCode_CargoCategoryCode(module);
			String[] cargoTypeCargoCategory = cargoType_cargoCategoryString.split(",");
			for (int i = 0; i < cargoTypeCargoCategory.length; i++) {
				String[] oneCtCc = cargoTypeCargoCategory[i].split("-");
				BookingReferenceValueObject bookingReferenceVO = new BookingReferenceValueObject();
				bookingReferenceVO.setCargoType(oneCtCc[0]);
				bookingReferenceVO.setCargoCategory(formatApplicableCargoCategoryList(oneCtCc[1]));
				brvoList.add(bookingReferenceVO);
			}
			log.info("END: getBRVOList DAO brvoList;" + brvoList.toString());
		} catch (BusinessException e) {
			log.info("Exception getBRVOList : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getBRVOList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBRVOList DAO END");
		}
		return brvoList;
	}

	// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->formatApplicableCargoCategoryList()
	private String formatApplicableCargoCategoryList(String cargoCategoryCodes) throws BusinessException {
		Map<String, String> cargoCode_cargoName = null;
		StringBuilder cargoCategoryName = new StringBuilder();
		try {
			log.info("START: formatApplicableCargoCategoryList DAO cargoCategoryCodes:" + CommonUtility.deNull(cargoCategoryCodes));
			cargoCode_cargoName = getCargoCategoryCode_CargoCategoryName();
			String[] applicableCargoCategoryCode = cargoCategoryCodes.split("/");
			for (int i = 0; i < applicableCargoCategoryCode.length; i++) {
				cargoCategoryName.append(cargoCode_cargoName.get(applicableCargoCategoryCode[i])).append("=")
						.append(applicableCargoCategoryCode[i]).append(",");
			}
			cargoCategoryName.deleteCharAt(cargoCategoryName.length() - 1);
		} catch (BusinessException e) {
			log.info("Exception formatApplicableCargoCategoryList : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception formatApplicableCargoCategoryList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: formatApplicableCargoCategoryList DAO cargoCategoryName.toString():"
					+ cargoCategoryName.toString());
		}
		return cargoCategoryName.toString();
	}

	// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->getParaCargoTypeCode_CargoCategoryCode()
	private String getParaCargoTypeCode_CargoCategoryCode(String module) throws BusinessException {
		String ct_cc = "";
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		String sql = "select * from text_para where PARA_CD = 'CTACC'";
		if (module.equals("AssignCargoCategory")) {
			sql = "select * from text_para where PARA_CD = 'CTACC_ACC'";
		}
		try {
			log.info("START: getParaCargoTypeCode_CargoCategoryCode DAO module:" + CommonUtility.deNull(module));
			log.info("SQL" + sql);
			log.info("paramMap" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				ct_cc = rs.getString("VALUE");
			}
			log.info("END:: getParaCargoTypeCode_CargoCategoryCode DAO ct_cc:" +  CommonUtility.deNull(ct_cc));
		} catch (Exception e) {
			log.info("Exception getParaCargoTypeCode_CargoCategoryCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END:: getParaCargoTypeCode_CargoCategoryCode DAO");
		}
		return ct_cc;

	}

	// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->getParaCargoTypeCode_CargoCategoryCode()
	@Override
	public String getAmendParaCargoTypeCode_CargoCategoryCode(String module) throws BusinessException {
		String ct_cc = "";
		String sql = "";
		SqlRowSet rs = null;
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: getParaCargoTypeCode_CargoCategoryCode  DAO  Start Obj " + CommonUtility.deNull(module));
			sql = "SELECT * FROM TEXT_PARA WHERE PARA_CD = 'CTACC'";
			if (module.equals("AssignCargoCategory")) {
				sql = "SELECT * FROM TEXT_PARA WHERE PARA_CD = 'CTACC_ACC'";
			}

			log.info(" *** getParaCargoTypeCode_CargoCategoryCode SQL *****" + sql);
			log.info(" paramMap: " + paramMap);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				ct_cc = rs.getString("VALUE");
			}
			log.info("END: *** getParaCargoTypeCode_CargoCategoryCode Result *****" + ct_cc.toString());
		} catch (NullPointerException e) {
			log.info("Exception getParaCargoTypeCode_CargoCategoryCode : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getParaCargoTypeCode_CargoCategoryCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getParaCargoTypeCode_CargoCategoryCode  DAO  END");
		}
		return ct_cc;
	}

	// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->updateCargoTypeCargoCategory()
	@Override
	public String updateCargoTypeCargoCategory(String bookingRefNbr, String cargoCategoryCode, String cargoTypeCode) throws BusinessException {
		String sql = "";
		String status = "N";
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START updateCargoTypeCargoCategory DAO" + " bookingRefNbr: " + CommonUtility.deNull(bookingRefNbr) + " cargoCategoryCode: "
					+ CommonUtility.deNull(cargoCategoryCode) + " cargoTypeCode: " + CommonUtility.deNull(cargoTypeCode));
			sql = "UPDATE bk_details SET cargo_type =:cargo_type, cargo_category_cd =:cargo_category_cd WHERE bk_ref_nbr =:bk_ref_nbr ";
			log.info("SQL: " + sql);
			log.info(" paramMap: " + paramMap);
			paramMap.put("cargo_type", cargoTypeCode);
			paramMap.put("cargo_category_cd", cargoCategoryCode);
			paramMap.put("bk_ref_nbr", bookingRefNbr);
			int i = namedParameterJdbcTemplate.update(sql, paramMap);
			if (i == 1) {
				status = "Y";
			}
		} catch (Exception e) {
			log.info("Exception updateCargoTypeCargoCategory : ", e);
			throw new BusinessException("M4201");
		} finally {
				log.info("END: updateCargoTypeCargoCategory Dao End");
			}
		return status;
	}
	
	// ejb.sessionBeans.gbms.cargo.bookingReference -->BookingReferenceEJBBean
		@Override
		public List<BookRefvoyageOutwardValueObject> getVoyageName(String crNo) throws BusinessException {
			SqlRowSet rs = null;
			Map<String, Object> paramMap = new HashMap<String, Object>();
			StringBuilder sb = new StringBuilder();
			List<BookRefvoyageOutwardValueObject> voyageRowvect = new ArrayList<BookRefvoyageOutwardValueObject>();

			try {
				log.info("START: getVoyageName Dao Start crNo:" + CommonUtility.deNull(crNo));
				if (crNo.equals("JP")) {
					sb.append(" select a.vv_cd ,a.TERMINAL, a.out_voy_nbr, a.vsl_nm, b.VSL_FULL_NM ");
					sb.append(" from vessel_call a, vessel b where a.vsl_nm = b.vsl_nm and ");
					sb.append(" (VV_STATUS_IND IN ('PR','AP','AL','BR','UB') AND ");
					sb.append(" ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) ");
					sb.append(" OR TERMINAL NOT IN 'CT')) and nvl(gb_close_shp_ind,'N') ");
					sb.append(" <> 'Y' and create_cust_cd is not null ");
					sb.append(" order by a.terminal desc,a.vsl_nm,a.out_voy_nbr ");
				} else {
					sb.append(" select distinct c.vv_cd , c.TERMINAL, c.out_voy_nbr, c.vsl_nm, c.VSL_FULL_NM ");
					sb.append(" from (  (select a.vv_cd ,a.TERMINAL, a.out_voy_nbr, a.vsl_nm,b.VSL_FULL_NM, ");
					sb.append(" a.create_cust_cd from vessel_call a, vessel b where a.vsl_nm = b.vsl_nm ");
					sb.append(" and (a.vv_status_ind in ('PR','AP','AL','BR') ");
					// Start bypass the system validation on vessel-unberthed status. - NS May 2023
					sb.append(" OR (a.vv_status_ind='UB' AND A.VV_CD IN (SELECT X.VV_CD FROM VESSEL_CALL X, SYSTEM_CONFIG Y ");
					sb.append("  WHERE Y.CAT_CD='REOPEN_DOC_GC_ESN' AND Y.REC_STATUS='A' AND Y.MISC_TYPE_CD=X.VV_CD ");
					sb.append("  AND (SYSDATE-TO_DATE(TO_CHAR(CREATE_DTTM,'DD-MM-YYYY HH24MISS'),'DD-MM-YYYY HH24MISS'))*24*60-"); 
					sb.append("  (SELECT TO_NUMBER(MISC_TYPE_NM) FROM SYSTEM_CONFIG ");
					sb.append("  WHERE CAT_CD='REOPEN_DOC_SETTING' AND REC_STATUS='A' AND MISC_TYPE_CD='GC_BOOKING')<=0)))"); 
					// End bypass the system validation on vessel-unberthed status.
					sb.append(" AND ((TERMINAL IN 'CT' AND COMBI_GC_OPS_IND IN('Y',null)) OR ");
					sb.append(" TERMINAL NOT IN 'CT') and nvl(a.gb_close_shp_ind,'N') <> 'Y' ) c ");
					sb.append(" LEFT OUTER JOIN VESSEL_DECLARANT VD ON (VD.VV_CD = c.VV_CD AND VD.STATUS = 'A')) ");
					sb.append(" where  (c.create_cust_cd =:crNo or VD.CUST_CD =:crNo) ");
					sb.append(" order by c.terminal desc,c.vsl_nm , c.out_voy_nbr ");
				}

				if (!crNo.equals("JP")) {
					paramMap.put("crNo", crNo);
				}
				log.info("SQL" + sb.toString());
				log.info(" *** getVoyageName params *****" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				while (rs.next()) {
					BookRefvoyageOutwardValueObject BookingRefVesselVoyVO = new BookRefvoyageOutwardValueObject();
					BookingRefVesselVoyVO.setVvCd(CommonUtility.deNull(rs.getString("vv_cd")));
					BookingRefVesselVoyVO.setOutVoyNbr(CommonUtility.deNull(rs.getString("out_voy_nbr")));
					BookingRefVesselVoyVO.setVslNm(CommonUtility.deNull(rs.getString("vsl_nm")));
					BookingRefVesselVoyVO.setVslFullNm(CommonUtility.deNull(rs.getString("VSL_FULL_NM")));
					BookingRefVesselVoyVO.setTerminal(CommonUtility.deNull(rs.getString("TERMINAL")));

					voyageRowvect.add(BookingRefVesselVoyVO);
				}
				log.info("END: *** getVoyageName Result *****" + voyageRowvect.toString());

			} catch (Exception e) {
				log.info("Exception getVoyageName : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: getVoyageName DAO");
			}
			return voyageRowvect;
		}
		
		// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean--> getVslDetailsForDPE
		@Override
		public List<VesselVoyValueObject> getVslDetailsForDPE(String VslNm, String outVoyage, String coCode) throws BusinessException {
			SqlRowSet rs = null;
			StringBuffer sb = new StringBuffer();	
			String sql = "";
			List<VesselVoyValueObject> VesselVect = new ArrayList<VesselVoyValueObject>();
			Map<String, String> paramMap = new HashMap<String, String>();
			try {
				log.info("START: getVslDetailsForDPE  DAO  Start Obj "+" VslNm:"+CommonUtility.deNull(VslNm)+" outVoyage:"+CommonUtility.deNull(outVoyage)
						+" coCode:"+CommonUtility.deNull(coCode));
				if (coCode.equals("JP")) {
					sb.append(" SELECT V.VSL_FULL_NM, VC.out_voy_nbr, VC.VV_CD,VC.TERMINAL, ");
					sb.append(" TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(BT.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,BT.ETB_DTTM )- NVL(BT.ATB_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(BT.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,BT.ETB_DTTM ),BT.ATB_DTTM ), 'dd/mm/yyyy HH24MI') ARRIVAL, " );
					sb.append(" TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(BT.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,BT.ETU_DTTM )- NVL(BT.ATU_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(BT.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,BT.ETU_DTTM ),BT.ATU_DTTM ), 'dd/mm/yyyy HH24MI') DEPARTURE, ");
					sb.append(" TO_CHAR(BT.COL_DTTM,'dd/mm/yyyy HH24MI') as COL_DTTM, ");
					sb.append(" TO_CHAR(BT.ETB_DTTM,'dd/mm/yyyy HH24MI') as ETB_DTTM ");
					sb.append(" FROM VESSEL_CALL VC ");
					sb.append(" INNER JOIN VESSEL V ON VC.VSL_NM = V.VSL_NM ");
					sb.append(" LEFT JOIN BERTHING BT ON (VC.VV_CD = BT.VV_CD AND BT.SHIFT_IND = 1) ");
					sb.append(" WHERE VC.VSL_NM = :VslNm AND VC.OUT_VOY_NBR= :outVoyage ");
					sql =  sb.toString();
				}
				else {
					sb.append(" SELECT V.VSL_FULL_NM, VC.out_voy_nbr, VC.VV_CD,VC.TERMINAL, ");
					sb.append(" TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(BT.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,BT.ETB_DTTM )- NVL(BT.ATB_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(BT.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,BT.ETB_DTTM ),BT.ATB_DTTM ), 'dd/mm/yyyy HH24MI') ARRIVAL, ");
					sb.append(" TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(BT.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,BT.ETU_DTTM )- NVL(BT.ATU_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(BT.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,BT.ETU_DTTM ),BT.ATU_DTTM ), 'dd/mm/yyyy HH24MI') DEPARTURE, ");
					sb.append( " TO_CHAR(BT.COL_DTTM,'dd/mm/yyyy HH24MI') as COL_DTTM, ");
					sb.append(" TO_CHAR(BT.ETB_DTTM,'dd/mm/yyyy HH24MI') as ETB_DTTM ");
					sb.append( " FROM VESSEL_CALL VC ");
					sb.append(" INNER JOIN VESSEL V ON VC.VSL_NM = V.VSL_NM ");
					sb.append(" LEFT JOIN BERTHING BT ON (VC.VV_CD = BT.VV_CD AND BT.SHIFT_IND = 1) ");
					sb.append(" LEFT OUTER JOIN VESSEL_DECLARANT VD ON (VD.VV_CD = VC.VV_CD AND VD.STATUS = 'A') ");
					sb.append(" WHERE VC.VSL_NM = :VslNm AND VC.OUT_VOY_NBR= :outVoyage ");
					sb.append(" AND (VC.CREATE_CUST_CD = :coCode OR VD.CUST_CD = :coCode)");
					sql = sb.toString();
				}
				log.info(" *** getVslDetailsForDPE SQL *****" + sql);
				if (coCode.equals("JP")) {
					paramMap.put("VslNm", VslNm);
					paramMap.put("outVoyage", outVoyage);
				}else {
					paramMap.put("VslNm", VslNm);
					paramMap.put("outVoyage", outVoyage);
					paramMap.put("coCode", coCode);
				}
				log.info(" *** getVslDetailsForDPE params *****" + paramMap.toString());

				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				VesselVoyValueObject vslValueObject;
				for (; rs.next(); VesselVect.add(vslValueObject)) {
					vslValueObject = new VesselVoyValueObject();
					vslValueObject.setTerminal(CommonUtility.deNull(rs.getString(
							"TERMINAL")));
					vslValueObject.setVarNbr(CommonUtility.deNull(rs.getString(
							"VV_CD")));
					vslValueObject.setVslName(CommonUtility.deNull(rs.
							getString("Vsl_full_Nm")));
					vslValueObject.setVoyNo(CommonUtility.deNull(rs.
							getString("out_voy_nbr")));
					vslValueObject.setArrival(CommonUtility.deNull(rs.
							getString("ARRIVAL")));
					vslValueObject.setDepartural(CommonUtility.deNull(rs.
							getString("DEPARTURE")));
					vslValueObject.setCol_dttm(CommonUtility.deNull(rs.
							getString("COL_DTTM")));
					vslValueObject.setEtb_dttm(CommonUtility.deNull(rs.
							getString("ETB_DTTM")));
				}

				log.info("END: *** getVslDetailsForDPE Result *****" + VesselVect.toString());
			} catch (NullPointerException e) { 
				log.info("Exception getVslDetailsForDPE : ", e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception getVslDetailsForDPE : ", e);
				throw new BusinessException("M4201");
			} finally{
				log.info("END: getVslDetailsForDPE  DAO  END");
			}
			return VesselVect;

		}
		
		// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->  getVslDetails
		@Override
		public List<VesselVoyValueObject> getVslDetails(String varNo, String coCode) throws BusinessException {
			SqlRowSet rs = null;
			StringBuffer sb = new StringBuffer();	
			String sql = "";
			List<VesselVoyValueObject> VesselVect = new ArrayList<VesselVoyValueObject>();
			Map<String, String> paramMap = new HashMap<String, String>();
			try {
				log.info("START: getVslDetails  DAO  Start Obj "+" varNo:"+CommonUtility.deNull(varNo)+" coCode:"+CommonUtility.deNull(coCode) );

				if (coCode.equals("JP")) {
					sb.append(" SELECT V.VSL_FULL_NM, VC.out_voy_nbr, VC.VV_CD,VC.TERMINAL, " );
					sb.append(" TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(BT.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,BT.ETB_DTTM )- NVL(BT.ATB_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(BT.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,BT.ETB_DTTM ),BT.ATB_DTTM ), 'dd/mm/yyyy HH24MI') ARRIVAL, ");
					sb.append( " TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(BT.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,BT.ETU_DTTM )- NVL(BT.ATU_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(BT.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,BT.ETU_DTTM ),BT.ATU_DTTM ), 'dd/mm/yyyy HH24MI') DEPARTURE, " );
					sb.append( " TO_CHAR(BT.COL_DTTM,'dd/mm/yyyy HH24MI') as COL_DTTM, ");
					sb.append(" TO_CHAR(BT.ETB_DTTM,'dd/mm/yyyy HH24MI') as ETB_DTTM ");
					sb.append(" FROM VESSEL_CALL VC ");
					sb.append(" INNER JOIN VESSEL V ON VC.VSL_NM = V.VSL_NM ");
					sb.append(" LEFT JOIN BERTHING BT ON (VC.VV_CD = BT.VV_CD AND BT.SHIFT_IND = 1) ");
					sb.append(" WHERE VC.VV_CD = :varNo ");

					sql = sb.toString();
				}
				else {
					sb.append(" SELECT V.VSL_FULL_NM, VC.out_voy_nbr, VC.VV_CD,VC.TERMINAL, ");
					sb.append(" TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(BT.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,BT.ETB_DTTM )- NVL(BT.ATB_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_BERTH_DTTM- NVL(BT.ETB_DTTM, sysdate - 9000)), 1, VC.VSL_BERTH_DTTM,BT.ETB_DTTM ),BT.ATB_DTTM ), 'dd/mm/yyyy HH24MI') ARRIVAL, ");
					sb.append(" TO_CHAR(DECODE(SIGN(DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(BT.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,BT.ETU_DTTM )- NVL(BT.ATU_DTTM, sysdate - 9000)), 1, DECODE(SIGN(VC.VSL_ETD_DTTM- NVL(BT.ETU_DTTM, sysdate - 9000)), 1, VC.VSL_ETD_DTTM,BT.ETU_DTTM ),BT.ATU_DTTM ), 'dd/mm/yyyy HH24MI') DEPARTURE, ");
					sb.append( " TO_CHAR(BT.COL_DTTM,'dd/mm/yyyy HH24MI') as COL_DTTM, ");
					sb.append(" TO_CHAR(BT.ETB_DTTM,'dd/mm/yyyy HH24MI') as ETB_DTTM ");
					sb.append(" FROM VESSEL_CALL VC ");
					sb.append( " INNER JOIN VESSEL V ON VC.VSL_NM = V.VSL_NM ");
					sb.append(" LEFT JOIN BERTHING BT ON (VC.VV_CD = BT.VV_CD AND BT.SHIFT_IND = 1) ");
					sb.append(" LEFT OUTER JOIN VESSEL_DECLARANT VD ON (VD.VV_CD = VC.VV_CD AND VD.STATUS = 'A') ");
					sb.append(" WHERE VC.VV_CD = :varNo ");
					sb.append(" AND (VC.CREATE_CUST_CD = :coCode OR VD.CUST_CD = :coCode)");
					sql = sb.toString();
				}
				log.info(" *** getVslDetails SQL *****" + sql);

				if (coCode.equals("JP")) {
					paramMap.put("varNo", varNo);
				} else {
					paramMap.put("varNo", varNo);
					paramMap.put("coCode", coCode);
				}
				log.info(" *** getVslDetails params *****" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				VesselVoyValueObject vslValueObject;
				for (; rs.next(); VesselVect.add(vslValueObject)) {
					vslValueObject = new VesselVoyValueObject();
					vslValueObject.setTerminal(CommonUtility.deNull(rs.getString(
							"terminal")));
					vslValueObject.setVarNbr(CommonUtility.deNull(rs.getString(
							"VV_CD")));
					vslValueObject.setVslName(CommonUtility.deNull(rs.
							getString("Vsl_full_Nm")));
					vslValueObject.setVoyNo(CommonUtility.deNull(rs.
							getString("out_voy_nbr")));
					vslValueObject.setArrival(CommonUtility.deNull(rs.
							getString("ARRIVAL")));
					vslValueObject.setDepartural(CommonUtility.deNull(rs.
							getString("DEPARTURE")));
					vslValueObject.setCol_dttm(CommonUtility.deNull(rs.
							getString("COL_DTTM")));
					vslValueObject.setEtb_dttm(CommonUtility.deNull(rs.
							getString("ETB_DTTM")));
				}
				log.info("END: *** getVslDetails Result *****" + VesselVect.toString());
			} catch (NullPointerException e) { 
				log.info("Exception getVslDetails : ", e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception getVslDetails : ", e);
				throw new BusinessException("M4201");
			} finally{
				log.info("END: getVslDetails  DAO  END");
			}
			return VesselVect;

		}
		
		// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->  getBKDetailsList
		@Override
		public List<BookingReferenceValueObject> getBKDetailsList(String varNo, String coCode,Criteria criteria) throws  BusinessException {
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();	
		String sql = "";
		List<BookingReferenceValueObject> BKDetailsVect = new ArrayList<BookingReferenceValueObject>();
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getBKDetailsList  DAO  Start Obj "+" varNo:"+CommonUtility.deNull(varNo)+" coCode:"+CommonUtility.deNull(coCode) );
			/*
			 * sql = String.valueOf(String.valueOf( (new StringBuffer("SELECT
			 * a.*,b.Vsl_full_Nm,d.CRG_TYPE_NM from bk_details a , vessel b
			 * ,vessel_call c, crg_type d where c.vsl_nm = b.vsl_nm and c.vv_cd =
			 * '")). append(varNo).append( "' and a.var_nbr =
			 * '").append(varNo).append("' and bk_status = 'A' and a.cargo_type =
			 * d.CRG_TYPE_CD order by lower(bk_ref_nbr)")));
			 */

			if (coCode.equals("JP")) {
				sb.append("SELECT a.*,b.Vsl_full_Nm,d.CRG_TYPE_NM, ");
				// Start bypass the system validation on vessel-unberthed status. - NS May 2023
				sb.append(" (CASE WHEN c.vv_status_ind='UB' AND ");
				sb.append(" (NVL((SELECT (SYSDATE-TO_DATE(TO_CHAR(CREATE_DTTM,'DD-MM-YYYY HH24MISS'),'DD-MM-YYYY HH24MISS'))*24*60- ");
				sb.append(" (SELECT TO_NUMBER(MISC_TYPE_NM) FROM SYSTEM_CONFIG WHERE CAT_CD='REOPEN_DOC_SETTING' ");
				sb.append(" AND REC_STATUS='A' AND MISC_TYPE_CD='GC_BOOKING') FROM SYSTEM_CONFIG WHERE CAT_CD='REOPEN_DOC_GC_ESN' ");
				sb.append(" AND REC_STATUS='A' AND MISC_TYPE_CD=c.vv_cd),1))<=0 THEN 'BR' ELSE c.vv_status_ind END) vv_status_ind, ");
				// End bypass the system validation on vessel-unberthed status.
				sb.append(" c.vsl_nm, c.terminal,c.scheme,c.COMBI_GC_SCHEME,c.COMBI_GC_OPS_IND, c.GB_CLOSE_SHP_IND as close_ship_ind ");
				sb.append(" from bk_details a , vessel b ,vessel_call c, crg_type d");
				sb.append(" where  c.vsl_nm = b.vsl_nm and c.vv_cd = :varNo ");
				sb.append(" and  a.var_nbr = :varNo ");
				sb.append(" and bk_status = 'A' and a.cargo_type = d.CRG_TYPE_CD order by lower(bk_ref_nbr)");

				sql = sb.toString();
			} else {
				sb.append("SELECT a.*,b.Vsl_full_Nm,d.CRG_TYPE_NM,");
				// Start bypass the system validation on vessel-unberthed status. - NS May 2023
				sb.append(" (CASE WHEN c.vv_status_ind='UB' AND ");
				sb.append(" (NVL((SELECT (SYSDATE-TO_DATE(TO_CHAR(CREATE_DTTM,'DD-MM-YYYY HH24MISS'),'DD-MM-YYYY HH24MISS'))*24*60- ");
				sb.append(" (SELECT TO_NUMBER(MISC_TYPE_NM) FROM SYSTEM_CONFIG WHERE CAT_CD='REOPEN_DOC_SETTING'  ");
				sb.append(" AND REC_STATUS='A' AND MISC_TYPE_CD='GC_BOOKING') FROM SYSTEM_CONFIG WHERE CAT_CD='REOPEN_DOC_GC_ESN' ");
				sb.append(" AND REC_STATUS='A' AND MISC_TYPE_CD=c.vv_cd),1))<=0 THEN 'BR' ELSE c.vv_status_ind END) vv_status_ind, ");
				// End bypass the system validation on vessel-unberthed status.
				sb.append(" c.vsl_nm,  c.terminal,c.scheme,c.COMBI_GC_SCHEME,c.COMBI_GC_OPS_IND, c.GB_CLOSE_SHP_IND as close_ship_ind ");
				sb.append(" from bk_details a , vessel b ,vessel_call c, crg_type d");
				sb.append(" where  c.vsl_nm = b.vsl_nm and c.vv_cd =:varNo");
				sb.append(" and  a.var_nbr =:varNo ");
				sb.append(" and bk_status = 'A' and a.cargo_type = d.CRG_TYPE_CD");
				sb.append(" and (c.CREATE_CUST_CD = :coCode");
				sb.append(" OR a.BK_CREATE_CD = :coCode )");
				sb.append(" order by lower(bk_ref_nbr)");
				sql = sb.toString();
			}

			if (criteria.isPaginated() && CommonUtil.deNull(criteria.getPredicates().get("updateBL")).isEmpty()) {
				sql = CommonUtil.getPaginatedSql(sb.toString(), criteria.getStart(), criteria.getLimit());
			}
			log.info(" *** getBKDetailsList SQL *****" + sql);

			if (coCode.equals("JP")) {
				paramMap.put("varNo", varNo);
			} else {
				paramMap.put("varNo", varNo);
				paramMap.put("coCode", coCode);
			}
			log.info(" *** getBKDetailsList params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			BookingReferenceValueObject brvo;
			for (; rs.next(); BKDetailsVect.add(brvo)) {
				brvo = new BookingReferenceValueObject();
				brvo.setBrNo(CommonUtility.deNull(rs.getString("bk_ref_nbr")));
				brvo.setVesselName(CommonUtility.deNull(rs.getString("Vsl_full_Nm")));
				brvo.setVoyageNo(CommonUtility.deNull(rs.getString("out_voy_nbr")));
				brvo.setCargoType(CommonUtility.deNull(rs.getString("cargo_type")));
				// BEGIN added by Maksym JCMS Smart CR 6.10
				brvo.setCargoCategory(CommonUtility.deNull(rs.getString("cargo_category_cd")));
				// END added by Maksym JCMS Smart CR 6.10
				brvo.setPackageNos(CommonUtility.deNull(rs.getString("BK_NBR_PKGS")));
				brvo.setWeight(CommonUtility.deNull(rs.getString("bk_wt")));
				brvo.setVolume(CommonUtility.deNull(rs.getString("bk_vol")));
				brvo.setPortOfDischarge(CommonUtility.deNull(rs.getString("port_dis")));
				brvo.setShipperContact(CommonUtility.deNull(rs.getString("SHIPPER_CONTACT")));
				brvo.setShipperAddress(CommonUtility.deNull(rs.getString("SHIPPER_ADDR")));
				brvo.setShipperName(CommonUtility.deNull(rs.getString("SHIPPER_NM")));
				brvo.setShipperCoyCode(CommonUtility.deNull(rs.getString("SHIPPER_CD")));
				brvo.setVarNbr(CommonUtility.deNull(rs.getString("VAR_NBR")));//DPE tungnm3 added
				brvo.setVvIdnStatus(CommonUtility.deNull(rs.getString("VV_STATUS_IND")));//DPE tungnm3 added
				brvo.setAbbrVslName(CommonUtility.deNull(rs.getString("VSL_NM")));//DPE tungnm3 added
				brvo.setVslCloseShipInd(CommonUtility.deNull(rs.getString("close_ship_ind")));
				brvo.setEsnDeclarantNo(CommonUtility.deNull(rs.getString("ESN_DECLARED")));
				brvo.setTerminal(CommonUtility.deNull(rs.getString("terminal")));
				brvo.setScheme(CommonUtility.deNull(rs.getString("scheme")));
				brvo.setSubScheme(CommonUtility.deNull(rs.getString("COMBI_GC_SCHEME")));
				brvo.setGcOperations(CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND")));
				// START CR FTZ - NS JUNE 2024
				brvo.setConName(CommonUtility.deNull(rs.getString("CONS_NM")));
				brvo.setConsigneeAddr(CommonUtility.deNull(rs.getString("CONSIGNEE_ADDR")));
				brvo.setNotifyParty(CommonUtility.deNull(rs.getString("NOTIFY_PARTY")));
				brvo.setNotifyPartyAddr(CommonUtility.deNull(rs.getString("NOTIFY_PARTY_ADDR")));
				brvo.setPlaceofDelivery(CommonUtility.deNull(rs.getString("PLACE_OF_DELIVERY")));
				brvo.setPlaceofReceipt(CommonUtility.deNull(rs.getString("PLACE_OF_RECEIPT")));
				brvo.setBlNbr(CommonUtility.deNull(rs.getString("BL_NBR")).toUpperCase());
				// END CR FTZ - NS JUNE 2024
			}
			log.info("END: *** getBKDetailsList Result *****" + BKDetailsVect.toString());
		} catch (NullPointerException e) { 
			log.info("Exception getBKDetailsList : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBKDetailsList : ", e);
			throw new BusinessException("M4201");
		} finally{
			log.info("END: getBKDetailsList  DAO  END");
		}
		return BKDetailsVect;
	}
		
	// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->  getCarCarrierVesselCode
	@Override
	public String getCarCarrierVesselCode() throws BusinessException {
		String getCarCarrierVesselCode = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: getCarCarrierVesselCode  DAO  Start Obj " );
			String sql = "select VALUE from text_para where para_cd = 'VSL_CC'";
			log.info(" *** getCarCarrierVesselCode SQL *****" + sql);
			log.info(" *** getCarCarrierVesselCode paramMap *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if (rs.next()) {
				getCarCarrierVesselCode = rs.getString("VALUE");
			}
			log.info("END: *** getCarCarrierVesselCode Result *****" +  CommonUtility.deNull(getCarCarrierVesselCode));
		} catch (NullPointerException e) { 
			log.info("Exception getCarCarrierVesselCode : ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getCarCarrierVesselCode : ", e);
			throw new BusinessException("M4201");
		} finally{
			log.info("END: getCarCarrierVesselCode  DAO  END");
		}
		return getCarCarrierVesselCode;
	}
	
	// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->
		// updateBKForDPE
		@Override
		public String updateBKForDPE(String bkRefNbr, String crgStatus, String varNo, String cntrNo, String cntrType,
				String cntrSize, String vslId,
				// BEGIN amended by Maksym JCMS Smart CR 6.10
				// String outVoyNbr, String conrCode, String cargoType,
				String outVoyNbr, String conrCode, String cargoType, String cargoCategory,
				// END amended by Maksym JCMS Smart CR 6.10
				String shpCrNo, String shpContactNo, String shpAddr, String shpNm, String bkWt, String bkVol,
				String bkNoOfPkg, String varPkgs, String varVol, String varWt, String portDis, String adpCustCd,
				String bkCmpCode, String user, boolean checkAmendConsignee, String conName, String consigneeAddr,
				String notifyParty, String notifyPartyAddr, String placeofDelivery, String placeofReceipt, String blNbr, boolean isExcelUpload) throws BusinessException {
			SqlRowSet rsAdpCustCdCheck = null;
			Map<String, Object> paramMap = new HashMap<String, Object>();
			SqlRowSet rs1 = null;
			String sql = "";
			String status = "N";
			String shpCd = shpCrNo; // send in via this parameter
			String shpCrName = shpNm;
			String shpadd = shpAddr;
			String businessexception = null;
			String updateStatus = null;
			String uaStatus = null;
			String sqltrans = null;
			// wuld be changed in the second phase .......
			// if (bkCmpCode.equals("JP"))
			// uaStatus = "Y";
			// else
			if (!checkAmendConsignee) {
				updateStatus = chkUpdate(bkRefNbr);
				// uaStatus = chkUA(bkRefNbr);
				uaStatus = "Y";
			} else {
				updateStatus = "Y";
				uaStatus = "Y";
			}
			// 20181130 KokTsing check if adpCustCdChanged changed by user or not
			boolean isAdpCustCdChanged = false;

			// 20180530 koktsing check port discharge for combi vessel
			boolean chkPort = chkDiscPortCd(portDis, varNo);
			if (!chkPort) {
				log.info("Invalid Port Code " + portDis);
				businessexception = "M42261";
			}

			if (updateStatus.equals("Y") && uaStatus.equals("Y")) {
				try {
					log.info("START: updateBKForDPE  DAO  Start Obj " + " bkRefNbr:" + CommonUtility.deNull(bkRefNbr) + " crgStatus:" + CommonUtility.deNull(crgStatus)
							+ " varNo:" + CommonUtility.deNull(varNo) + " cntrNo:" + CommonUtility.deNull(cntrNo) + " cntrType:" + CommonUtility.deNull(cntrType)
							+ " cntrSize:" + CommonUtility.deNull(cntrSize) + " vslId:" + CommonUtility.deNull(vslId) + " outVoyNbr:" + CommonUtility.deNull(outVoyNbr)
							+ " conrCode:" + CommonUtility.deNull(conrCode) + " cargoType:" + CommonUtility.deNull(cargoType) + " cargoCategory:" + CommonUtility.deNull(cargoCategory)
							+ " shpCrNo:" + CommonUtility.deNull(shpCrNo) + " shpContactNo:" + CommonUtility.deNull(shpContactNo) + " shpAddr:" + CommonUtility.deNull(shpAddr)
							+ " shpNm:" + CommonUtility.deNull(shpNm) + " bkWt:" + CommonUtility.deNull(bkWt) + " bkVol:" + CommonUtility.deNull(bkVol) + " bkNoOfPkg:"
							+ CommonUtility.deNull(bkNoOfPkg) + " varPkgs:" + CommonUtility.deNull(varPkgs) + " varVol:" + CommonUtility.deNull(varVol) + " varWt:"
							+ CommonUtility.deNull(varWt) + " portDis:" + CommonUtility.deNull(portDis) + " adpCustCd:" + CommonUtility.deNull(adpCustCd) + " bkCmpCode:"
							+ CommonUtility.deNull(bkCmpCode) + " user:" + CommonUtility.deNull(user) + " checkAmendConsignee:" + CommonUtility.deNull(String.valueOf(checkAmendConsignee))
							+ " conName:" + CommonUtility.deNull(conName)+ " consigneeAddr:" + CommonUtility.deNull(consigneeAddr)+ " notifyParty:" + CommonUtility.deNull(notifyParty)
							+ " notifyPartyAddr:" + CommonUtility.deNull(notifyPartyAddr)+ " placeofDelivery:" + CommonUtility.deNull(placeofDelivery)+ " placeofReceipt:" + CommonUtility.deNull(placeofReceipt)
							+ " blNbr:" + CommonUtility.deNull(blNbr));
					// con.setAutoCommit(false);
					// Statement sqlstmtchk = con.createStatement();
					// Bhuvana - UEN No.enhancement
					/*
					 * StringBuffer sqlCust = new StringBuffer(); sqlCust.setLength(0); sqlCust.
					 * append("select b.CO_NM,b.co_cd,c.add_l1 from customer a , company_code b , cust_address c where a.cust_cd = b.co_cd and b.REC_STATUS = 'A' "
					 * ); sqlCust.
					 * append(" and a.cust_cd = c.cust_cd and ( upper(a.TDB_CR_NBR) = upper('");
					 * sqlCust.append(shpCrNo).append("') OR upper(a.UEN_NBR) = upper('").append(
					 * shpCrNo).append("'))"); sqlchk = sqlCust.toString(); ResultSet rs =
					 * sqlstmtchk.executeQuery(sqlchk); if (rs.next()) { shpCrName =
					 * CommonUtility.deNull(rs.getString("CO_NM")); shpCd =
					 * CommonUtility.deNull(rs.getString("co_cd")); shpadd =
					 * CommonUtility.deNull(rs.getString("add_l1")); } else { if
					 * (shpContactNo.equals("")) businessexception = "M20614"; if
					 * (shpAddr.equals("")) businessexception = "M20613"; if (shpNm.equals(""))
					 * businessexception = "M20612"; } rs.close(); //
					 */

					// 20181130 KokTsing
					// Based on the param - adpCustCd, check if the DECLARANT_CD is changed before
					// or not
					// Check if the BK_DETAILS.DECLARANT_CD = CUSTOMER.CUST_CD (based on
					// CUSTOMER.TDB_CR_NBR OR CUSTOMER.UEN_NBR)
					StringBuffer sqlAdpCustCdCheck = new StringBuffer();
					sqlAdpCustCdCheck.setLength(0);
					sqlAdpCustCdCheck.append("SELECT * FROM BK_DETAILS_TRANS WHERE BK_REF_NBR = '").append(bkRefNbr)
							.append("'");
					sqlAdpCustCdCheck.append(" AND DECLARANT_CD in");
					sqlAdpCustCdCheck.append(" (");
					sqlAdpCustCdCheck.append(" SELECT CUST_CD FROM CUSTOMER WHERE");
					sqlAdpCustCdCheck.append(" ( upper(TDB_CR_NBR) = upper(:adpCustCd)");
					sqlAdpCustCdCheck.append(" OR upper(UEN_NBR) = upper(:adpCustCd))");
					sqlAdpCustCdCheck.append(" )");
					String strSqlAdpCustCdCheck = sqlAdpCustCdCheck.toString();
					log.info("strSqlAdpCustCdCheck sql=" + strSqlAdpCustCdCheck);

					log.info(" *** updateBKForDPE SQL *****" + strSqlAdpCustCdCheck);

					paramMap.put("adpCustCd", adpCustCd);
					log.info(" *** updateBKForDPE params *****" + paramMap.toString());
					rsAdpCustCdCheck = namedParameterJdbcTemplate.queryForRowSet(strSqlAdpCustCdCheck, paramMap);
					if (!rsAdpCustCdCheck.next()) {
						// Cannot match the Declarant code, means the adpCustCd was changed
						// which means this is not a disabledAmend case from BRUpdateHandler.java
						// if disabledAmend case, Declarant code can't be changed in the form/jsp
						isAdpCustCdChanged = true;
					}

					log.info("Getting max trans nbr " + businessexception);
					int maxTransNbr = 1;
					String esnCustCode = fetchCustomerCode(adpCustCd);

					log.info("Getting max trans nbr " + isToLogTxn);
					if (isToLogTxn) {
						String sqlgetTransBasedBk = "select trans_nbr from bk_details_trans where BK_REF_NBR = :bkRefNbr ORDER BY trans_nbr DESC ";
						paramMap.put("bkRefNbr", bkRefNbr);
						log.info(" *** updateBKForDPE SQL *****" + sqlgetTransBasedBk);
						log.info(" *** updateBKForDPE params *****" + paramMap.toString());
						SqlRowSet rsTrabsBk = namedParameterJdbcTemplate.queryForRowSet(sqlgetTransBasedBk, paramMap);
						if (rsTrabsBk.next()) {
							String maxno = rsTrabsBk.getString(1);
							if (maxno != null && maxno.equals(""))
								maxno = "0";
							if (maxno == null)
								maxno = "0";
							maxTransNbr = Integer.parseInt(maxno) + 1;
							log.info("MaxTxnNbr: " + maxTransNbr);
						}					
					}
					
					if(!isExcelUpload) {
						Map<String, String> mapErrorLength = this.checkLegthValidation(consigneeAddr, notifyParty,
								notifyPartyAddr, placeofDelivery, placeofReceipt, shpCrName, shpAddr, blNbr);
						if (mapErrorLength.size() > 0) {
							String[] tmpString = new String[mapErrorLength.size()];
							int count = 0;
							for (Entry<String, String> entry : mapErrorLength.entrySet()) {
								String key = entry.getKey();
								tmpString[count] = key;
								count++;
							}
							String errorMessage = CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_invalidLength, tmpString);
							throw new BusinessException(errorMessage);
						}
					}
					
					StringBuffer sb = new StringBuffer();
					sb.setLength(0);
					sb.append("UPDATE ");
					sb.append("	bk_details ");
					sb.append("SET ");
					sb.append("	crg_status =:crg_status, ");
					sb.append("	var_nbr =:var_nbr, ");
					sb.append("	nbr_of_cntr =:nbr_of_cntr , ");
					sb.append("	cntr_type =:cntr_type, ");
					sb.append("	cntr_size =:cntr_size, ");
					sb.append("	out_voy_nbr =:out_voy_nbr, ");
					sb.append("	cargo_type = :cargo_type, ");
					sb.append("	cargo_category_cd = :cargo_category_cd, ");
					sb.append("	shipper_cr_nbr = :shipper_cr_nbr, ");
					sb.append("	shipper_contact = :shipper_contact, ");
					sb.append("	shipper_addr = :shipper_addr, ");
					sb.append("	shipper_nm = :shipper_nm, ");
					sb.append("	shipper_cd = :shipper_cd, ");
					sb.append("	bk_wt = :bk_wt, ");
					sb.append("	bk_vol = :bk_vol , ");
					sb.append("	bk_nbr_pkgs = :bk_nbr_pkgs , ");
					sb.append("	variance_pkgs =:variance_pkgs , ");
					sb.append("	variance_vol = :variance_vol , ");
					sb.append("	variance_wt = :variance_wt , ");
					sb.append("	port_dis =:port_dis, ");
					// 20181130 KokTsing
					// only update declarant_cd if isAdpCustCdChanged = true & esnCustCode not null
					if (isAdpCustCdChanged && (esnCustCode != null && esnCustCode.trim().length() > 0)) {
						sb.append("declarant_cd=:declarant_cd, ");
					}
					sb.append("	last_modify_user_id =:user, ");
					sb.append("	last_modify_dttm = SYSDATE , ");
					sb.append("	bk_create_cd =:coCd ");
					//START CR FTZ - NS JUNE 2024
					sb.append(", CONS_NM=:conName, CONSIGNEE_ADDR=:consigneeAddr, NOTIFY_PARTY=:notifyParty ");
					sb.append(", NOTIFY_PARTY_ADDR=:notifyPartyAddr, PLACE_OF_DELIVERY=:placeofDelivery ");
					sb.append(", PLACE_OF_RECEIPT=:placeofReceipt ,BL_NBR=:blNbr");
					//END CR FTZ - NS JUNE 2024
					sb.append(" WHERE ");
					sb.append("	bk_ref_nbr =:bk_ref_nbr");

					sql = sb.toString();

					// changed by Irene Tan on 14 Jun 2004 : audit trail logging
					// sqltrans = String.valueOf(String.valueOf( (new
					// StringBuffer("INSERT INTO bk_details_trans
					// (TRANS_NBR,BK_REF_NBR,CRG_STATUS,VAR_NBR,NBR_OF_CNTR,CNTR_TYPE,CNTR_SIZE,OUT_VOY_NBR,CARGO_TYPE,SHIPPER_CR_NBR,SHIPPER_CONTACT,SHIPPER_ADDR,SHIPPER_NM,BK_WT,BK_VOL,BK_NBR_PKGS,VARIANCE_PKGS,VARIANCE_VOL,VARIANCE_WT,PORT_DIS,DECLARANT_CD,BK_CREATE_CD,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,SHIPPER_CD)
					// values('" +
					sb.setLength(0);

					StringBuffer varname1 = new StringBuffer();
					varname1.append("INSERT ");
					varname1.append("	INTO ");
					varname1.append("		bk_details_trans ( trans_nbr, ");
					varname1.append("		bk_ref_nbr, ");
					varname1.append("		bk_status, ");
					varname1.append("		esn_declared, ");
					varname1.append("		crg_status, ");
					varname1.append("		var_nbr, ");
					varname1.append("		nbr_of_cntr, ");
					varname1.append("		cntr_type, ");
					varname1.append("		cntr_size, ");
					varname1.append("		out_voy_nbr, ");
					varname1.append("		cargo_type, ");
					varname1.append("		cargo_category_cd, ");
					varname1.append("		shipper_cr_nbr, ");
					varname1.append("		shipper_contact, ");
					varname1.append("		shipper_addr, ");
					varname1.append("		shipper_nm, ");
					varname1.append("		bk_wt, ");
					varname1.append("		bk_vol, ");
					varname1.append("		bk_nbr_pkgs, ");
					varname1.append("		variance_pkgs, ");
					varname1.append("		variance_vol, ");
					varname1.append("		variance_wt, ");
					varname1.append("		port_dis, ");
					varname1.append("		declarant_cd, ");
					varname1.append("		bk_create_cd, ");
					varname1.append("		last_modify_user_id, ");
					varname1.append("		last_modify_dttm, ");
					varname1.append("		shipper_cd ");
					// START CR FTZ - JUNE 2024
					varname1.append(",CONS_NM, CONSIGNEE_ADDR, NOTIFY_PARTY");
					varname1.append(",NOTIFY_PARTY_ADDR, PLACE_OF_DELIVERY, PLACE_OF_RECEIPT, BL_NBR)");
					// END CR FTZ - JUNE 2024
					varname1.append("	VALUES ( :trans_nbr , ");
					varname1.append("	:bk_ref_nbr , ");
					varname1.append("	:bk_status , ");
					varname1.append("	:esn_declared , ");
					varname1.append("	:crg_status , ");
					varname1.append("	:var_nbr, ");
					varname1.append("	:nbr_of_cntr , ");
					varname1.append("	:cntr_type, ");
					varname1.append("	:cntr_size , ");
					varname1.append("	:out_voy_nbr , ");
					varname1.append("	:cargo_type , ");
					varname1.append("	:cargo_category_cd , ");
					varname1.append("	:shipper_cr_nbr , ");
					varname1.append("	:shipper_contact , ");
					varname1.append("	:shipper_addr , ");
					varname1.append("	:shipper_nm , ");
					varname1.append("	:bk_wt , ");
					varname1.append("	:bk_vol , ");
					varname1.append("	:bk_nbr_pkgs , ");
					varname1.append("	:variance_pkgs , ");
					varname1.append("	:variance_vol , ");
					varname1.append("	:variance_wt , ");
					varname1.append("	:port_dis , ");
					varname1.append("	:declarant_cd , ");
					varname1.append("	:bk_create_cd , ");
					varname1.append("	:userId , ");
					varname1.append("	SYSDATE , ");
					varname1.append("	:shipper_cd");
					// START CR FTZ - JUNE 224
					varname1.append(", :conName, :consigneeAddr, :notifyParty ");
					varname1.append(", :notifyPartyAddr, :placeofDelivery, :placeofReceipt ");
					varname1.append(", :blNbr )");
					// END CR FTZ - JUNE 2024
					sqltrans = varname1.toString();

					paramMap = new HashMap<String, Object>();
					paramMap.put("crg_status", CommonUtility.deNull(crgStatus));
					paramMap.put("var_nbr", CommonUtility.deNull(varNo));
					paramMap.put("nbr_of_cntr", CommonUtility.deNull(cntrNo));
					paramMap.put("cntr_type", CommonUtility.deNull(cntrType));
					paramMap.put("cntr_size", CommonUtility.deNull(cntrSize));
					paramMap.put("out_voy_nbr", CommonUtility.deNull(outVoyNbr));
					paramMap.put("cargo_type", CommonUtility.deNull(cargoType));
					paramMap.put("cargo_category_cd", CommonUtility.deNull(cargoCategory));
					paramMap.put("shipper_cr_nbr", CommonUtility.deNull(shpCrNo));
					paramMap.put("shipper_contact", GbmsCommonUtility.addApostr(CommonUtility.deNull(shpContactNo)));
					paramMap.put("shipper_addr", GbmsCommonUtility.addApostr(CommonUtility.deNull(shpadd)));
					paramMap.put("shipper_nm", GbmsCommonUtility.addApostr(CommonUtility.deNull(shpCrName)));
					paramMap.put("shipper_cd", GbmsCommonUtility.addApostr(CommonUtility.deNull(shpCd)));
					paramMap.put("bk_wt", CommonUtility.deNull(bkWt));
					paramMap.put("bk_vol", CommonUtility.deNull(bkVol));
					paramMap.put("bk_nbr_pkgs", CommonUtility.deNull(bkNoOfPkg));
					paramMap.put("variance_pkgs", CommonUtility.deNull(varPkgs));
					paramMap.put("variance_vol", CommonUtility.deNull(varVol));
					paramMap.put("variance_wt", CommonUtility.deNull(varWt));
					paramMap.put("port_dis", CommonUtility.deNull(portDis));
					// START CR FTZ NS JUNE 2024
					paramMap.put("conName", conName);
					paramMap.put("consigneeAddr", consigneeAddr);
					paramMap.put("notifyParty", notifyParty);
					paramMap.put("notifyPartyAddr", notifyPartyAddr);
					paramMap.put("placeofDelivery", placeofDelivery);
					paramMap.put("placeofReceipt", placeofReceipt);
					paramMap.put("blNbr", blNbr);
					// START CR FTZ NS JUNE 2024
					if (isAdpCustCdChanged && (esnCustCode != null && esnCustCode.trim().length() > 0)) {
						paramMap.put("declarant_cd", CommonUtility.deNull(esnCustCode));
					}
					paramMap.put("user", CommonUtility.deNull(user));
					paramMap.put("coCd", CommonUtility.deNull(bkCmpCode));
					paramMap.put("bk_ref_nbr", CommonUtility.deNull(bkRefNbr));

					log.info(" *** updateBKForDPE SQL *****" + sql);
					log.info(" *** updateBKForDPE params *****" + paramMap.toString());
					int i = namedParameterJdbcTemplate.update(sql, paramMap);
				
					paramMap.put("trans_nbr", maxTransNbr);
					paramMap.put("bk_ref_nbr", bkRefNbr);
					paramMap.put("bk_status", "A");
					paramMap.put("esn_declared", this.getEsnDeclaredStatus("G", bkRefNbr));
					paramMap.put("crg_status", crgStatus);
					paramMap.put("var_nbr", varNo);
					paramMap.put("nbr_of_cntr", cntrNo);
					paramMap.put("cntr_type", cntrType);
					paramMap.put("cntr_size", cntrSize);
					paramMap.put("out_voy_nbr", outVoyNbr);
					paramMap.put("cargo_type", cargoType);
					paramMap.put("cargo_category_cd", cargoCategory);
					paramMap.put("shipper_cr_nbr", "NULL");
					paramMap.put("shipper_contact", "NULL");
					paramMap.put("shipper_addr", "NULL");
					paramMap.put("shipper_nm", GbmsCommonUtility.addApostr(shpCrName));
					paramMap.put("bk_wt", bkWt);
					paramMap.put("bk_vol", bkVol);
					paramMap.put("bk_nbr_pkgs", bkNoOfPkg);
					paramMap.put("variance_pkgs", varPkgs);
					paramMap.put("variance_vol", varVol);
					paramMap.put("variance_wt", varWt);
					paramMap.put("port_dis", portDis);
					paramMap.put("declarant_cd", esnCustCode);
					paramMap.put("bk_create_cd", user);
					paramMap.put("userId", user);
					paramMap.put("shipper_cd", shpCd);
					log.info(" *** updateBKForDPE SQL *****" + sqltrans);
					log.info(" *** updateBKForDPE params *****" + paramMap.toString());
					if (isToLogTxn)
						namedParameterJdbcTemplate.update(sqltrans, paramMap);

					if (i == 1)
						status = "Y";

					// con.commit();
					// BEGIN added by Maksym JCMS Smart CR 6.10

					esnRepo.updateCargoCategoryCode(cargoCategory, bkRefNbr);
					// END added by Maksym JCMS Smart CR 6.10

					log.info("END: *** updateBKForDPE Result *****" + CommonUtility.deNull(status));
				} catch (BusinessException e) { 
					log.info("Exception updateBKForDPE : ", e);
					throw new BusinessException(e.getMessage());
				} catch (NullPointerException e) { 
					log.info("Exception updateBKForDPE : ", e);
					throw new BusinessException("M4201");
				} catch (Exception e) {
					log.info("Exception updateBKForDPE : ", e);
					throw new BusinessException("M4201");
				} finally{
					log.info("END: updateBKForDPE  DAO  END");
					if (businessexception != null)
						throw new BusinessException(businessexception);
				} 
				return status;
			} else {
				throw new BusinessException("The shipment is closed");
			}
		}
		
		// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->  chkUpdate
		private String chkUpdate(String bkRefNbr) throws  BusinessException {
			SqlRowSet rs = null;
			String sql = "";
			String status = "";
			Map<String, String> paramMap = new HashMap<String, String>();
			try {
				log.info("START: chkUpdate  DAO  Start Obj "+" bkRefNbr:"+CommonUtility.deNull(bkRefNbr) );
				sql = "select bk_status from bk_details where bk_ref_nbr = :bkRefNbr";
				log.info(" *** chkUpdate SQL *****" + sql);
				paramMap.put("bkRefNbr", bkRefNbr);
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs.next()) {
					String bkStatus = rs.getString("bk_status");
					if (bkStatus.equals("X")) {
						status = "N";
					} else {
						status = "Y";
					}
				}
				log.info("END: *** chkUpdate Result *****" + status);
			} catch (NullPointerException e) { 
				log.info("Exception chkUpdate : ", e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception chkUpdate : ", e);
				throw new BusinessException("M4201");
			} finally{
				log.info("END: chkUpdate  DAO  END");
			} 
			if (status.equals("Y"))
				return status;
			else
				throw new BusinessException("The ESN CR NO Does not exist");
		}
		
		// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->
		// chkDiscPortCd
		private boolean chkDiscPortCd(String portCd, String vvCd) throws BusinessException {
			SqlRowSet rs = null;
			String sql = "";
			int count = 0;
			Map<String, Object> paramMap = new HashMap<String, Object>();
			try {
				log.info("START: chkDiscPortCd  DAO  Start Obj " + " portCd:" + CommonUtility.deNull(portCd) + " vvCd:" + CommonUtility.deNull(vvCd));
				sql = "SELECT TERMINAL, SHPG_SVC_CD, SHPG_ROUTE_NBR, COMBI_GC_OPS_IND FROM VESSEL_CALL WHERE VV_CD = :vvCd ";
				log.info(" *** chkDiscPortCd SQL *****" + sql);
				paramMap.put("vvCd", vvCd);
				
				log.info(" *** chkDiscPortCd params *****" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				
				String terminal = "";
				String svcCd = "";
				int routeNo = 0;
				String gcOperations = "";

				if (rs.next()) {
					terminal = CommonUtility.deNull(rs.getString("TERMINAL"));
					svcCd = CommonUtility.deNull(rs.getString("SHPG_SVC_CD"));
					routeNo = rs.getInt("SHPG_ROUTE_NBR");
					gcOperations = CommonUtility.deNull(rs.getString("COMBI_GC_OPS_IND"));
				} else {
					log.info("No record found in vessel call for vv_cd: " + vvCd);
					throw new BusinessException("M4201");
				}

				// CT vessels with GC Ops Ind - Y, check whether the port is part of service
				// route
				if (terminal.equalsIgnoreCase("CT") && gcOperations.equalsIgnoreCase("Y")) {
					if (svcCd.equals("") || (routeNo == 0)) {
						log.info("Service details in vessel_call table is null for vv_cd " + vvCd);
						throw new BusinessException("M4201");
					}

					sql = "SELECT POC_SEQ_NBR FROM SHPG_ROUTE_POC WHERE SHPG_SVC_CD = :svcCd AND SHPG_ROUTE_NBR = :routeNo AND PORT_CD LIKE 'SG%'";
					log.info(" *** chkDiscPortCd SQL *****" + sql);
					paramMap.put("svcCd", svcCd);
					paramMap.put("routeNo", routeNo);
					log.info(" *** chkDiscPortCd params *****" + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

					int seqNo = 0;
					if (rs.next()) {
						seqNo = rs.getInt("POC_SEQ_NBR");
					} else {
						log.info("Route Not pass thru Singapore for svcCd = " + svcCd + " and route No = " + routeNo);
						throw new BusinessException("Service Route does not pass via Singapore.");
					}

					sql = "SELECT COUNT(*) FROM SHPG_ROUTE_POC WHERE SHPG_SVC_CD = :svcCd AND SHPG_ROUTE_NBR =:routeNo AND PORT_CD = :portCd AND POC_SEQ_NBR > :seqNo ORDER BY POC_SEQ_NBR";

					log.info(" *** chkDiscPortCd SQL *****" + sql);
					paramMap.put("svcCd", svcCd);
					paramMap.put("routeNo", routeNo);
					paramMap.put("portCd", portCd);
					paramMap.put("seqNo", seqNo);
					log.info(" *** chkDiscPortCd params *****" + paramMap.toString());
					rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

					if (rs.next()) {
						count = rs.getInt(1);
					} else {
						count = 0;
					}

					if (count > 0) {
						return true;
					} else {
						// return false;
						log.info(portCd + " is not in Service Route sequence for svcCd = " + svcCd + " and route No = "
								+ routeNo + " after sequence " + seqNo);
						throw new BusinessException("M42271");
					}
				}

				log.info("END: *** chkDiscPortCd Result *****" + count);
				return true;
			} catch (BusinessException e) { 
				log.info("Exception chkDiscPortCd : ", e);
				throw new BusinessException(e.getMessage());
			} catch (NullPointerException e) { 
				log.info("Exception chkDiscPortCd : ", e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception chkDiscPortCd : ", e);
				throw new BusinessException("M4201");
			} finally{
				log.info("END: chkDiscPortCd  DAO  END");
			} 
		}
		
		// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean--> fetchCustomerCode
		private String fetchCustomerCode(String esnCrNo) throws  BusinessException {
			SqlRowSet rs = null;
			String sql = "";
			String custCode = "";
			Map<String, String> paramMap = new HashMap<String, String>();
			// Bhuvana UEN No. enhancement
			try {
				log.info("START: fetchCustomerCode  DAO  Start Obj "+" esnCrNo:"+CommonUtility.deNull(esnCrNo) );
				sql = "select b.co_cd from customer a , company_code b where a.cust_cd = b.co_cd AND b.REC_STATUS = 'A' and ( upper(a.tdb_cr_nbr) = upper(:esnCrNo) OR upper(a.UEN_NBR) = upper(:esnCrNo))";
				log.info(" *** fetchCustomerCode SQL *****" + sql);
				paramMap.put("esnCrNo", esnCrNo);
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs.next()) {
					custCode = rs.getString("co_cd");
				}
				log.info("END: *** fetchCustomerCode Result *****" + custCode.toString());
			} catch (NullPointerException e) { 
				log.info("Exception fetchCustomerCode : ", e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception fetchCustomerCode : ", e);
				throw new BusinessException("M4201");
			} finally{
				log.info("END: fetchCustomerCode  DAO  END");
			} 
			return custCode;
		}
		
		private String getEsnDeclaredStatus(String generalBulkInd, String bookingRef) throws BusinessException {
			SqlRowSet rs = null;
			Map<String, Object> paramMap = new HashMap<String, Object>();
			String esnDeclared = "";
			StringBuffer sb = new StringBuffer();
			try {
				log.info("START: getEsnDeclaredStatus Dao Start" + " generalBulkInd: " + CommonUtility.deNull(generalBulkInd) + " bookingRef: " + CommonUtility.deNull(bookingRef));
				
				sb.append("select esn_declared from ");
				if (generalBulkInd.equals("G")) {
					sb.append("bk_details ");
				} else {
					sb.append("bulk_bk_details ");
				}
				sb.append("where bk_ref_nbr=:bookingRef");
				
				paramMap.put("bookingRef", bookingRef);
				log.info(" paramMap: " + paramMap);
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				if (rs.next()) {
					esnDeclared = rs.getString("esn_declared");
				}
			} catch (Exception se) {
				log.info("Exception getEsnDeclaredStatus : ", se);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: getEsnDeclaredStatus Dao End");
			}
			return esnDeclared;
		}
		
		// ejb.sessionBeans.gbms.cargo.bookingReference -->BookingReferenceEJBBean
		/**
		 * This method chkPortCode check port code in database
		 *
		 * @param String portCode
		 * @return String status string if exits "Y" else "N"
		 * 
		 */
		@Override
		public String chkPortCode(String portCode) throws BusinessException {
			SqlRowSet rs = null;
			Map<String, Object> paramMap = new HashMap<String, Object>();
			String status = "N";
			StringBuilder sb = new StringBuilder();
			try {
				log.info("START: chkPortCode Dao Start portCode:" + CommonUtility.deNull(portCode));
				sb.append("SELECT * from un_port_code where rec_status = 'A' and port_cd =:portCode");
				paramMap.put("portCode", portCode);
				log.info("SQL" + sb.toString());
				log.info(" *** chkPortCode params *****" + paramMap.toString());

				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				if (rs.next()) {
					status = "Y";
				} else {
					status = "N";
				}
				log.info("END: *** chkPortCode Result *****" + CommonUtility.deNull(status));

			} catch (Exception e) {
				log.info("Exception chkPortCode : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: chkPortCode DAO");
			}
			if (status.equals("N")) {
				throw new BusinessException("M20607");
			} else {
				return status;
			}
		}
		
		// ejb.sessionBeans.gbms.cargo.bookingReference -->BookingReferenceEJBBean
		/**
		 * This method chkCrNo - check cargo no in database
		 *
		 * @param String crNo
		 * @return String status string Y/N
		 * 
		 */
		@Override
		public String chkCrNo(String crNo) throws BusinessException {
			SqlRowSet rs = null;
			Map<String, String> paramMap = new HashMap<String, String>();
			String status = "N";
			StringBuilder sb = new StringBuilder();

			try {
				log.info("START: chkCrNo Dao Start crNo:" + CommonUtility.deNull(crNo));
				// Bhuvana UEN No. enhancement
				sb.setLength(0);
				sb.append("SELECT b.co_cd FROM customer a, company_code b ");
				sb.append("WHERE a.cust_cd=b.co_cd  ");
				sb.append("AND b.REC_STATUS='A' ");
				sb.append("AND (");
				sb.append("UPPER(a.tdb_cr_nbr)=UPPER(:crNo) ");
				sb.append("OR UPPER(a.uen_nbr)=UPPER(:crNo) ");
				sb.append(") ");
				paramMap.put("crNo", crNo);
				log.info("SQL" + sb.toString() + "pstmt:");
				log.info(" *** chkCrNo params *****" + paramMap.toString());

				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				if (rs.next()) {
					status = "Y";
				}
				log.info("END: *** chkCrNo Result *****" + CommonUtility.deNull(status));

			} catch (Exception e) {
				log.info("Exception chkCrNo : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: chkCrNo DAO");
			}
			if (status.equals("Y"))
				return status;
			else
				throw new BusinessException("BK20606"); // M20606
		}
		
		// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->
		// chkQuantity
		@Override
		public String chkQuantity(String bkWt, String bkVol, String bkNoOfPkg, String varPkgs, String varVol, String varWt,
				String bkRefNbr) throws BusinessException {
			SqlRowSet rsWt = null;
			SqlRowSet rsVol = null;
			SqlRowSet rsPkgs = null;
			SqlRowSet rsVarVol = null;
			SqlRowSet rsVarWt = null;
			SqlRowSet rsVarPkgs = null;
			StringBuffer sb = new StringBuffer();
			String status = "Y";
			String exitStatus = "N";
			String businessException = "";
			Map<String, String> paramMap = new HashMap<String, String>();
			try {
				log.info("START: chkQuantity  DAO  Start Obj " + " bkWt:" + CommonUtility.deNull(bkWt) + " bkVol:" + CommonUtility.deNull(bkVol) + " bkNoOfPkg:"
						+ CommonUtility.deNull(bkNoOfPkg) + " varPkgs:" + CommonUtility.deNull(varPkgs) + " varVol:" + CommonUtility.deNull(varVol)
						+ " varWt:" + CommonUtility.deNull(varWt) + " bkRefNbr:" + CommonUtility.deNull(bkRefNbr));

				sb.setLength(0);
				sb.append("SELECT * FROM bk_details a, esn b ");
				sb.append(" WHERE a.bk_wt>:bkWt ");
				sb.append(" AND a.bk_ref_nbr=:bkRefNbr");
				sb.append(" AND a.bk_ref_nbr=b.bk_ref_nbr and b.esn_status = 'A'");
				String sqlwt = sb.toString();

				sb.setLength(0);
				sb.append("SELECT * FROM bk_details a, esn b ");
				// START - Fix wrong parameter passed - NS MAY 2024
				sb.append(" WHERE a.bk_vol> :bkVol ");
				// END - Fix wrong parameter passed - NS MAY 2024
				sb.append(" AND a.bk_ref_nbr=:bkRefNbr");
				sb.append(" AND a.bk_ref_nbr=b.bk_ref_nbr and b.esn_status = 'A'");
				String sqlvol = sb.toString();
				
				sb.setLength(0);
				sb.append("SELECT * FROM bk_details a, esn b ");
				sb.append(" WHERE a.BK_NBR_PKGS>").append(bkNoOfPkg);
				sb.append(" AND a.bk_ref_nbr='").append(bkRefNbr).append("'");
				sb.append(" AND a.bk_ref_nbr=b.bk_ref_nbr and b.esn_status = 'A' ");
				String sqlpkgs = sb.toString();
				log.info("sqlpkgs =" + sqlpkgs.toString());

				sb.setLength(0);
				sb.append("SELECT * FROM bk_details a, esn b ");
				sb.append(" WHERE a.VARIANCE_PKGS>:varPkgs ");
				sb.append(" AND a.bk_ref_nbr=:bkRefNbr");
				sb.append(" AND a.bk_ref_nbr=b.bk_ref_nbr and b.esn_status = 'A' ");
				String sqlvarpkgs = sb.toString();

				sb.setLength(0);
				sb.append("SELECT * FROM bk_details a, esn b ");
				sb.append(" WHERE a.VARIANCE_VOL>:varVol ");
				sb.append(" AND a.BK_REF_NBR=:bkRefNbr");
				sb.append(" AND a.bk_ref_nbr=b.bk_ref_nbr and b.esn_status = 'A'");
				String sqlvarvol = sb.toString();

				sb.setLength(0);
				sb.append("SELECT * FROM bk_details a, esn b ");
				sb.append(" WHERE a.VARIANCE_WT>:varWt ");
				sb.append(" AND a.BK_REF_NBR=:bkRefNbr ");
				sb.append(" AND a.bk_ref_nbr=b.bk_ref_nbr and b.esn_status = 'A'");
				String sqlvarwt = sb.toString();

				// Added by Punitha on 18/05/2009
				sb.setLength(0);
				sb.append(
						" SELECT DECODE (ED.NBR_PKGS, NULL, 0, ED.NBR_PKGS) + DECODE (TJJ.NBR_PKGS, NULL, 0, TJJ.NBR_PKGS)  +  DECODE (TPJ.NBR_PKGS, NULL, 0, TPJ.NBR_PKGS) NBR_PKGS_CREATED ");
				sb.append(
						" FROM ESN E, ESN_DETAILS ED, TESN_JP_JP TJJ, TESN_PSA_JP TPJ WHERE  E.ESN_ASN_NBR = ED.ESN_ASN_NBR(+) ");
				sb.append(
						" AND E.ESN_ASN_NBR = TJJ.ESN_ASN_NBR(+) AND E.ESN_ASN_NBR = TPJ.ESN_ASN_NBR(+)  and E.ESN_STATUS = 'A' ");
				sb.append(" AND E.BK_REF_NBR = :bkRefNbr ");
				String sqlPkgsCheck = sb.toString();

				log.info(" *** chkQuantity SQL *****" + sqlwt);

				paramMap.put("bkWt", bkWt);
				paramMap.put("bkRefNbr", bkRefNbr);
				log.info(" *** chkQuantity params *****" + paramMap.toString());

				rsWt = namedParameterJdbcTemplate.queryForRowSet(sqlwt, paramMap);
				if (rsWt.next()) {
					status = "The weight cannot be less than the current one";
					exitStatus = "Y";
					businessException = "M20600";
				}
				log.info(" *** chkQuantity SQL *****" + sqlvol);

				// START - Fix wrong parameter passed - NS MAY 2024
				paramMap.put("bkVol", bkVol);				
				// END - Fix wrong parameter passed - NS MAY 2024
				paramMap.put("bkRefNbr", bkRefNbr);
				log.info(" *** chkQuantity params *****" + paramMap.toString());
				rsVol = namedParameterJdbcTemplate.queryForRowSet(sqlvol, paramMap);
				if (rsVol.next() && exitStatus == "N") {
					status = "The Volume cannot be less than the current one";
					exitStatus = "Y";
					businessException = "M20601";
				}

				// Amended by Punitha on 18/05/2009
				/*
				 * Statement sqlstmtPkgs = con.createStatement(); ResultSet rsPkgs =
				 * sqlstmtPkgs.executeQuery(sqlpkgs); if (rsPkgs.next() && exitStatus == "N") {
				 * status = "The number of Packages cannot be less than the current
				 * one"; exitStatus = "Y"; businessException = "M20602"; } rsPkgs.close();
				 * sqlstmtPkgs.close();
				 */

				log.info(" *** chkQuantity SQL *****" + sqlPkgsCheck);
				paramMap.put("bkRefNbr", bkRefNbr);
				log.info(" *** chkQuantity params *****" + paramMap.toString());
				rsPkgs = namedParameterJdbcTemplate.queryForRowSet(sqlPkgsCheck, paramMap);
				if (rsPkgs.next()) {
					String pkgsCreated = rsPkgs.getString("NBR_PKGS_CREATED");
					if (Integer.parseInt(bkNoOfPkg) < Integer.parseInt(pkgsCreated)) {
						status = "The number of Packages cannot be less than the existing balance";
						exitStatus = "Y";
						businessException = "M20602";
					}

				}

				log.info(" *** chkQuantity SQL *****" + sqlvarvol);
				paramMap.put("bkRefNbr", bkRefNbr);
				paramMap.put("varVol", varVol);
				log.info(" *** chkQuantity params *****" + paramMap.toString());
				rsVarVol = namedParameterJdbcTemplate.queryForRowSet(sqlvarvol, paramMap);
				if (rsVarVol.next() && exitStatus == "N") {
					status = "The Variance volume cannot be less than the current one";
					exitStatus = "Y";
					businessException = "M20603";

				}

				log.info(" *** chkQuantity SQL *****" + sqlvarwt);
				paramMap.put("bkRefNbr", bkRefNbr);
				paramMap.put("varWt", varWt);
				log.info(" *** chkQuantity params *****" + paramMap.toString());
				rsVarWt = namedParameterJdbcTemplate.queryForRowSet(sqlvarwt, paramMap);
				if (rsVarWt.next() && exitStatus == "N") {
					status = "The variance weight cannot be less than the current one";
					exitStatus = "Y";
					businessException = "M20604";

				}

				log.info(" *** chkQuantity SQL *****" + sqlvarpkgs);
				paramMap.put("bkRefNbr", bkRefNbr);
				paramMap.put("varPkgs", varPkgs);
				log.info(" *** chkQuantity params *****" + paramMap.toString());
				rsVarPkgs = namedParameterJdbcTemplate.queryForRowSet(sqlvarpkgs, paramMap);
				if (rsVarPkgs.next() && exitStatus == "N") {
					status = "The Variance package cannot be less than the current one";
					businessException = "M20605";

				}

				log.info("END: *** chkQuantity Result *****" +  CommonUtility.deNull(status));
			} catch (Exception e) {
				log.info("Exception chkQuantity : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: chkQuantity DAO");
			}
			if (status.equals("Y")) {
				return status;
			} else {
				throw new BusinessException(businessException);
			}
		}
		
		// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean--> getBrSearchDetails
		@Override
		public List<BookingReferenceValueObject> getBrSearchDetails(String bkRefNo, String coCode) throws BusinessException {
			SqlRowSet rs = null;
			StringBuffer sb = new StringBuffer();	
			String sql = "";
			List<BookingReferenceValueObject> BKDetailsVect = new ArrayList<BookingReferenceValueObject>();
			Map<String, String> paramMap = new HashMap<String, String>();
			try {
				log.info("START: getBrSearchDetails  DAO  Start Obj "+" bkRefNo:"+CommonUtility.deNull(bkRefNo) +" coCode:"+CommonUtility.deNull(coCode));
				if ("JP".equalsIgnoreCase(coCode)) {
					sb.append("SELECT a.*,b.Vsl_full_Nm , d.port_nm,f.co_nm,");
					sb.append("(SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) from customer where cust_cd =f.co_cd) tdb_cr_nbr, b.vsl_nm ");
					sb.append("from bk_details a , vessel b ,vessel_call c, un_port_code d , customer e,company_code f ");
					sb.append("where c.vsl_nm = b.VSL_NM and c.vv_cd = a.var_nbr and d.port_cd = a.port_dis");
					sb.append( " and a.DECLARANT_CD = f.co_cd and f.REC_STATUS = 'A'  and e.cust_cd = f.co_cd ");
					sb.append("and a.bk_ref_nbr = :bkRefNo ");
					sql = sb.toString();
				} else {
					sb.append("SELECT a.*,b.Vsl_full_Nm , d.port_nm,f.co_nm,");
					sb.append("(SELECT DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) from customer where cust_cd =f.co_cd) tdb_cr_nbr, b.vsl_nm ");
					sb.append( "from bk_details a , vessel b ,vessel_call c, un_port_code d , customer e,company_code f ");
					sb.append("where c.vsl_nm = b.VSL_NM and c.vv_cd = a.var_nbr and d.port_cd = a.port_dis");
					sb.append(" and a.DECLARANT_CD = f.co_cd and f.REC_STATUS = 'A'  and e.cust_cd = f.co_cd ");
					sb.append("and a.bk_ref_nbr = :bkRefNo ");
					sb.append("and (c.CREATE_CUST_CD = :coCode OR a.BK_CREATE_CD = :coCode) ");

					sql = sb.toString();
				}
				log.info(" *** getBrSearchDetails SQL *****" + sql);

				if ("JP".equalsIgnoreCase(coCode)) {
					paramMap.put("bkRefNo", bkRefNo);
				}else {
					paramMap.put("bkRefNo", bkRefNo);
					paramMap.put("coCode", coCode);
				}
				log.info(" *** getBrSearchDetails params *****" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				BookingReferenceValueObject brvo;
				while(rs.next()) {
					brvo = new BookingReferenceValueObject();
					brvo.setBrNo(CommonUtility.deNull(rs.getString("bk_ref_nbr")));
					brvo.setVesselName(CommonUtility.deNull(rs.getString("Vsl_full_Nm")));
					String shippercrnbr = CommonUtility.deNull(rs.getString("SHIPPER_CR_NBR"));
					brvo.setShipperCrNo(shippercrnbr);
					brvo.setVoyageNo(CommonUtility.deNull(rs.getString("out_voy_nbr")));
					brvo.setCargoType(CommonUtility.deNull(rs.getString("cargo_type")));
					// BEGIN added by Maksym JCMS Smart CR 6.10
					brvo.setCargoCategory(CommonUtility.deNull(rs.getString("cargo_category_cd")));
					// END added by Maksym JCMS Smart CR 6.10
					brvo.setPackageNos(CommonUtility.deNull(rs.getString("BK_NBR_PKGS")));
					brvo.setWeight(CommonUtility.deNull(rs.getString("bk_wt")));
					brvo.setVolume(CommonUtility.deNull(rs.getString("bk_vol")));
					brvo.setPackageVariance(CommonUtility.deNull(rs.getString("VARIANCE_PKGS")));
					brvo.setVolumeVariance(CommonUtility.deNull(rs.getString("VARIANCE_VOL")));
					brvo.setWeightVariance(CommonUtility.deNull(rs.getString("VARIANCE_WT")));
					brvo.setPortOfDischarge(CommonUtility.deNull(rs.getString("port_dis")));
					brvo.setShipperContact(CommonUtility.deNull(rs.getString("SHIPPER_CONTACT")));
					brvo.setShipperAddress(CommonUtility.deNull(rs.getString("SHIPPER_ADDR")));
					brvo.setShipperName(CommonUtility.deNull(rs.getString("SHIPPER_NM")));
					brvo.setEsnDeclarantNo(CommonUtility.deNull(rs.getString("tdb_cr_nbr")));
					brvo.setContainerType(CommonUtility.deNull(rs.getString("CNTR_TYPE")));
					brvo.setContainerSize(CommonUtility.deNull(rs.getString("CNTR_SIZE")));
					brvo.setNoContainer(CommonUtility.deNull(rs.getString("NBR_OF_CNTR")));
					brvo.setPortName(CommonUtility.deNull(rs.getString("port_nm")));
					brvo.setEsnDeclarantName(CommonUtility.deNull(rs.getString("co_nm")));
					brvo.setAbbrVslName(CommonUtility.deNull(rs.getString("vsl_nm")));

					BKDetailsVect.add(brvo);
				}
				log.info("END: *** getBrSearchDetails Result *****" + BKDetailsVect.toString());
			} catch (Exception e) {
				log.info("Exception getBrSearchDetails : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: getBrSearchDetails DAO");
			}
			return BKDetailsVect;
		}
		
		// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->  chkQuantity
		@Override
		public Hashtable<String,String> getVoyageDetails(String brn) throws BusinessException {
			SqlRowSet rs = null;
			StringBuffer qry = new StringBuffer();
			Hashtable<String,String> values = new Hashtable<String,String>();
			Map<String, String> paramMap = new HashMap<String, String>();
			try {
				log.info("START: getVoyageDetails  DAO  Start Obj "+" brn:"+CommonUtility.deNull(brn) );

				qry.append("SELECT ");
				qry.append("var_nbr ");
				qry.append(",bk.out_voy_nbr ");
				qry.append(",vsl_nm ");
				qry.append(" FROM ");
				qry.append("bk_details bk ");
				qry.append(",vessel_call ");
				qry.append(" WHERE ");
				qry.append("vv_cd=var_nbr ");
				qry.append(" AND ");
				qry.append("bk_status='A' ");
				qry.append(" AND ");
				qry.append("bk.bk_ref_nbr = :brn ");

				log.info(" *** getVoyageDetails SQL *****" + qry.toString());

				paramMap.put("brn", brn);
				log.info(" *** getVoyageDetails params *****" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(qry.toString(), paramMap);
				if (rs.next()) {
					values.put("varno", CommonUtility.deNull(rs.getString(1)));
					values.put("outvoy", CommonUtility.deNull(rs.getString(2)));
					values.put("vslid", CommonUtility.deNull(rs.getString(3)));
				}
				log.info("END: *** getVoyageDetails Result *****" + values.toString());
			} catch (Exception e) {
				log.info("Exception getVoyageDetails : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: getVoyageDetails DAO");
			}

			return values;
		}
		
		// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->
		// changed
		@Override
		public String cancelBK(String bkRefNbr, String userId) throws BusinessException {
			// end changed by Irene Tan on 14-06-2004
			String sql = "";
			String status = "N";
			String businessException = "";
			SqlRowSet rs = null;
			StringBuffer sb = new StringBuffer();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			try {
				log.info("START: cancelBK  DAO  Start Obj " + " bkRefNbr:" + CommonUtility.deNull(bkRefNbr) + " userId:" + CommonUtility.deNull(userId));
				log.info("Check Cancel Amend");
				String chkCancelStatus = chkCancelAmend(bkRefNbr, " ", "C");
				log.info("Check Cancel Amend: " + chkCancelStatus);
				if (!chkCancelStatus.equals("N")) {
					if (chkCancelStatus.equals("Y_ESN_OPEN")) {
						businessException = "The ESN is Open cannot Cancel";
					}
					if (chkCancelStatus.equals("Y_CLOSE_SHIP")) {
						businessException = "The shipment is closed";
					}
					log.info("Check Cancel Amend: throwing " + businessException);
					throw new BusinessException(businessException);
				}

				Calendar cal = new GregorianCalendar();
				int year = 0;
				String monthC = null;
				// Get the components of the date
				int era = cal.get(Calendar.ERA); // 0=BC, 1=AD
				log.info("era =" + era);
				year = cal.get(Calendar.YEAR); // 2002
				int month = cal.get(Calendar.MONTH) + 1;
				monthC = month + "";
				if (monthC.length() == 1) {
					monthC = "0" + monthC;
				}
				int day = cal.get(Calendar.DAY_OF_MONTH);
				String dayC = day + "";
				if (dayC.length() == 1) {
					dayC = "0" + dayC;
				}
				int hours = cal.get(Calendar.HOUR);
				String hoursC = hours + "";
				if (hoursC.length() == 1) {
					hoursC = "0" + hoursC;
				}
				int min = cal.get(Calendar.MINUTE);
				String minC = min + "";
				if (minC.length() == 1) {
					minC = "0" + minC;
				}
				int sec = cal.get(Calendar.SECOND);
				String secC = sec + "";
				if (secC.length() == 1) {
					secC = "0" + secC;
				}
				int millisec = cal.get(Calendar.MILLISECOND);
				String millisecC = millisec + "00";
				millisecC = millisecC.substring(0, 3);
				String bknew = "CBK" + year + monthC + dayC + minC + secC + millisecC;

				sb.setLength(0);
				sb.append("update bk_details set bk_status='X'");
				sb.append(", BK_REF_NBR=:bknew");
				sb.append(", OLD_BK_REF=:bkRefNbr");
				sb.append(" WHERE BK_REF_NBR=:bkRefNbr");

				sql = sb.toString();

				log.info(" *** cancelBK SQL *****" + sql);

				paramMap.put("bknew", bknew);
				paramMap.put("bkRefNbr", bkRefNbr.trim());
				
				log.info(" *** cancelBK params *****" + paramMap.toString());

				int i = namedParameterJdbcTemplate.update(sql, paramMap);
				if (i == 1)
					status = "Y";

				// added by Irene Tan on 14 Jun 2004 : audit trail logging
				int transNbr = 0;
				String transNbrSql = "select max(trans_nbr) from bk_details_trans where bk_ref_nbr=:bkRefNbr ";
				log.info(" *** cancelBK SQL *****" + transNbrSql);
				paramMap.put("bkRefNbr", bkRefNbr);
				log.info(" *** cancelBK params *****" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(transNbrSql, paramMap);
				if (rs.next()) {
					transNbr = rs.getInt(1);
				}
				if (transNbr > 0) {
					transNbr += 1;
				}
				String insertBkDetailsTransSql = "insert into bk_details_trans (bk_ref_nbr,trans_nbr, bk_status, last_modify_user_id, last_modify_dttm) values (:bkRefNbr,:transNbr,'X',:userId,sysdate)";
				log.info(" *** cancelBK SQL *****" + insertBkDetailsTransSql);

				paramMap.put("bkRefNbr", bkRefNbr);
				paramMap.put("transNbr", transNbr);
				paramMap.put("userId", userId);
				log.info(" *** cancelBK params *****" + paramMap.toString());
				int cnt = namedParameterJdbcTemplate.update(insertBkDetailsTransSql, paramMap);
				// end changed by Irene Tan on 14 Jun 2004
				log.info("END: *** cancelBK Result *****" + cnt);
			} catch (NullPointerException e) {
				log.info("Exception cancelBK : ", e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception cancelBK : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: cancelBK DAO");
			}
			return status;
		}
		
		// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->
		// getVslTypeCdByFullName
		@Override
		public String getVslTypeCdByFullName(String vslName) throws BusinessException {
			String vslTypeCd = "";
			String sql = "select VSL_TYPE_CD from vessel where VSL_NM  = :vslName ";
			SqlRowSet rs = null;
			Map<String, String> paramMap = new HashMap<String, String>();
			try {
				log.info("START: getVslTypeCdByFullName  DAO  Start Obj " + " fullName:" + CommonUtility.deNull(vslName));
				log.info(" *** getVslTypeCdByFullName SQL *****" + sql);
				paramMap.put("vslName", vslName);
				log.info("paramMap = " + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs.next()) {
					vslTypeCd = rs.getString("VSL_TYPE_CD");
				}
				log.info("END: *** getVslTypeCdByFullName Result *****" + vslTypeCd.toString());
			} catch (NullPointerException e) {
				log.info("Exception getVslTypeCdByFullName : ", e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception getVslTypeCdByFullName : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: getVslTypeCdByFullName DAO");
			}
			return vslTypeCd;
		}
		
		// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->getCargoTypeNotShow()
		// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->
		// getDefaultCargoCategoryCode
		@Override
		public String getDefaultCargoCategoryCode() throws BusinessException {
			String defaultCargoCategoryCode = "";
			String sql = "select VALUE from text_para where para_cd = 'DEF_CC'";
			SqlRowSet rs = null;
			Map<String, String> paramMap = new HashMap<String, String>();
			try {
				log.info("START: getDefaultCargoCategoryCode  DAO  Start Obj ");
				log.info(" *** getDefaultCargoCategoryCode SQL *****" + sql);
				log.info(" *** getDefaultCargoCategoryCode paramMap *****" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs.next()) {
					defaultCargoCategoryCode = rs.getString("VALUE");
				}

				log.info("END: *** getDefaultCargoCategoryCode Result *****" +  CommonUtility.deNull(defaultCargoCategoryCode));
			} catch (NullPointerException e) {
				log.info("Exception getDefaultCargoCategoryCode : ", e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception getDefaultCargoCategoryCode : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: getDefaultCargoCategoryCode DAO");
			}
			return defaultCargoCategoryCode;
		}
		
		// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->
		// chkBKCode
		@Override
		public String chkBKCode(String bkCode) throws BusinessException {
			SqlRowSet rs = null;
			String sql = "";
			String status = "N";
			sql = "SELECT * from bk_details where BK_REF_NBR = :bkCode ";
			Map<String, String> paramMap = new HashMap<String, String>();
			try {
				log.info("START: chkBKCode  DAO  Start Obj " + " bkCode:" + CommonUtility.deNull(bkCode));
				log.info(" *** chkBKCode SQL *****" + sql);
				paramMap.put("bkCode", bkCode);
				log.info(" *** chkBKCode params *****" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs.next()) {
					status = "Y";
				}
				log.info("END: *** chkBKCode Result *****" + CommonUtility.deNull(status));
			} catch (NullPointerException e) {
				log.info("Exception chkBKCode : ", e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception chkBKCode : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: chkBKCode DAO");
			}
			if (status.equals("N"))
				return status;
			else
				throw new BusinessException("M20608");
		}
		
		// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->
		// insertBK
		@Override
		public String insertBK(String bkRefNbr, String crgStatus, String varNo, String cntrNo, String cntrType,
				String cntrSize, String outVoyNbr,
				// BEGIN amended by Maksym JCMS Smart CR 6.10
				// String conrCode, String cargoType, String shpCrNo,
				String conrCode, String cargoType, String cargoCategory, String shpCrNo,
				// END amended by Maksym JCMS Smart CR 6.10
				String shpContactNo, String shpAddr, String shpNm, String bkWt, String bkVol, String bkNoOfPkg,
				String varPkgs, String varVol, String varWt, String portDis, String esnCustCd, String bkCreateCD,
				String user, String conName, String consigneeAddr, String notifyParty, String notifyPartyAddr, String placeofDelivery,
				String placeofReceipt, String blNbr, boolean isExcelUpload) throws BusinessException {
			String sql = "";
			String sqltrans = "";
			String status = "N";
			String shpCrName = shpNm;
			String shpCd = shpCrNo;
			int maxTransNbr = 1;
			String createcustcd = bkCreateCD;
			if (shpCrName == null) {
				shpCrName = "";
			}

			StringBuffer sb = new StringBuffer();
			bkRefNbr = bkRefNbr.trim();
			
			SqlRowSet rs1 = null;
			SqlRowSet rsTrans = null;
			String businessexception = null;
			Map<String, Object> paramMap = new HashMap<String, Object>();
			// Bhuvana - UEN No.enhancement
			/*
			 * StringBuffer sqlCust = new StringBuffer(); sqlCust.setLength(0); sqlCust.
			 * append("SELECT b.co_nm, b.co_cd, c.add_l1 FROM customer a, company_code b, cust_address c WHERE a.cust_cd = b.co_cd and b.REC_STATUS = 'A' "
			 * );
			 * sqlCust.append(" AND a.cust_cd=c.cust_cd AND ( UPPER(a.TDB_CR_NBR) = UPPER('"
			 * + shpCrNo + "') OR UPPER(a.UEN_NBR) = UPPER('" + shpCrNo + "'))"); sqlchk =
			 * sqlCust.toString(); //
			 */

			try {
				log.info("START: insertBK  DAO  Start Obj " + " bkRefNbr:" + CommonUtility.deNull(bkRefNbr) + " crgStatus:" + CommonUtility.deNull(crgStatus)
						+ " varNo:" + CommonUtility.deNull(varNo) + " cntrNo:" + CommonUtility.deNull(cntrNo) + " cntrType:" + CommonUtility.deNull(cntrType)
						+ " cntrSize:" + CommonUtility.deNull(cntrSize) + " outVoyNbr:" + CommonUtility.deNull(outVoyNbr) + " conrCode:" + CommonUtility.deNull(conrCode)
						+ " cargoType:" + CommonUtility.deNull(cargoType) + " cargoCategory:" + CommonUtility.deNull(cargoCategory) + " shpCrNo:" + CommonUtility.deNull(shpCrNo)
						+ " shpContactNo:" + CommonUtility.deNull(shpContactNo) + " shpAddr:" + CommonUtility.deNull(shpAddr) + " shpNm:" + CommonUtility.deNull(shpNm)
						+ " bkWt:" + CommonUtility.deNull(bkWt) + " bkVol:" + CommonUtility.deNull(bkVol) + " bkNoOfPkg:" + CommonUtility.deNull(bkNoOfPkg) + " varPkgs:" + CommonUtility.deNull(varPkgs)
						+ " varVol:" + CommonUtility.deNull(varVol) + " varWt:" + CommonUtility.deNull(varWt) + " portDis:" + CommonUtility.deNull(portDis) + " esnCustCd:" + CommonUtility.deNull(esnCustCd)
						+ " bkCreateCD:" + CommonUtility.deNull(bkCreateCD) + " user:" + CommonUtility.deNull(user) + " conName:" + CommonUtility.deNull(conName)
						+ " consigneeAddr:" + CommonUtility.deNull(consigneeAddr)+ " notifyParty:" + CommonUtility.deNull(notifyParty)+ " notifyPartyAddr:" + CommonUtility.deNull(notifyPartyAddr)
						+ " placeofDelivery:" + CommonUtility.deNull(placeofDelivery) + " placeofReceipt:" + CommonUtility.deNull(placeofReceipt)+ " blNbr:" + CommonUtility.deNull(blNbr));

				sb.setLength(0);
				sb.append("insertBK() ");
				sb.append("|bkRefNbr:").append(bkRefNbr);
				sb.append("|crgStatus:").append(crgStatus);
				sb.append("|varNo:").append(varNo);
				sb.append("|cntrNo:").append(cntrNo);
				sb.append("|cntrType:").append(cntrType);
				sb.append("|cntrSize:").append(cntrSize);
				sb.append("|outVoyNbr:").append(outVoyNbr);
				sb.append("|conrCode:").append(conrCode);
				sb.append("|cargoType:").append(cargoType);
				// BEGIN added by Maksym JCMS Smart CR 6.10
				sb.append("|cargoCategory:").append(cargoCategory);
				// END added by Maksym JCMS Smart CR 6.10
				sb.append("|shpCrNo:").append(shpCrNo);
				sb.append("|shpContactNo:").append(shpContactNo);
				sb.append("|shpAddr:").append(shpAddr);
				sb.append("|shpNm:").append(shpNm);
				sb.append("|bkWt:").append(bkWt);
				sb.append("|bkVol:").append(bkVol);
				sb.append("|bkNoOfPkg:").append(bkNoOfPkg);
				sb.append("|varPkgs:").append(varPkgs);
				sb.append("|varVol:").append(varVol);
				sb.append("|varWt:").append(varWt);
				sb.append("|portDis:").append(portDis);
				sb.append("|esnCustCd:").append(esnCustCd);
				sb.append("|bkCreateCD:").append(bkCreateCD);
				sb.append("|user:").append(user);
				log.info(sb.toString());

				/*
				 * String sqlcr = "Select nvl(declarant_cust_cd , create_cust_cd) as
				 * create_cust_cd from vessel_call where vv_cd = '" + varNo + "'";
				 */
				// changed by vietnd02
				String sqlcr = "SELECT create_cust_cd AS create_cust_cd FROM vessel_call WHERE vv_cd=:varNo ";

				// 20180530 koktsing check port discharge for combi vessel
				boolean chkPort = chkDiscPortCd(portDis, varNo);
				if (!chkPort) {
					log.info("Invalid Port Code " + portDis);
					businessexception = "M42261";
				}

				if (isToLogTxn) {
					// Commented on 28012023 by NS
//					log.info("select max(trans_nbr) from bk_details_trans where BK_REF_NBR = '" + bkRefNbr + "'");
//					String sql1 = "select max(trans_nbr) from bk_details_trans where BK_REF_NBR = :bkRefNbr ";
//
//					paramMap.put("bkRefNbr", bkRefNbr);
//					
//					log.info(" *** insertBK SQL *****" + sql1);
//					log.info(" *** insertBK params *****" + paramMap.toString());
//
//					rsTrans = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
//					if (rsTrans.next()) {
//						String maxno = rsTrans.getString(1);
//						if (maxno != null && maxno.equals(""))
//							maxno = "0";
//						if (maxno == null)
//							maxno = "0";
//						maxTransNbr = Integer.parseInt(maxno) + 1;
//					}
					// Commented on 28012023 by NS
					
					// START : Added 15022023 : Update code to solve unique constraint - NS
					String sql1 = "select trans_nbr from bk_details_trans where BK_REF_NBR = :bkRefNbr ";
					List<Integer> transNbrList = new ArrayList<Integer>();
					paramMap.put("bkRefNbr", bkRefNbr);
					
					log.info(" *** insertBK SQL *****" + sql1);
					log.info(" *** insertBK params *****" + paramMap.toString());
					
					rsTrans = namedParameterJdbcTemplate.queryForRowSet(sql1, paramMap);
					while(rsTrans.next()) {
						int trans_nbr = Integer.parseInt(rsTrans.getString("trans_nbr"));
						transNbrList.add(trans_nbr);
					}

					String max = null;
					if(transNbrList.size() > 0) {
						max = Collections.max(transNbrList).toString();
						if (max != null && max.equals(""))
							max = "0";
						if (max == null)
							max = "0";
					} else {
						if (max == null)
							max = "0";
					}
					maxTransNbr = Integer.parseInt(max) + 1;
					// END : Added 15022023 : Update code to solve unique constraint - NS
				}
				
				/*
				 * log.info(sqlchk); ResultSet rs = sqlstmtchk.executeQuery(sqlchk); if
				 * (rs.next()) { shpCrName = CommonUtility.deNull(rs.getString("CO_NM")); shpCd
				 * = CommonUtility.deNull(rs.getString("co_cd")); shpadd =
				 * CommonUtility.deNull(rs.getString("add_l1")); } else { if
				 * (shpContactNo.equals("")) businessexception = "M20614"; if
				 * (shpAddr.equals("")) businessexception = "M20613"; if (shpNm.equals(""))
				 * businessexception = "M20612"; } rs.close(); sqlstmtchk.close(); //
				 */
				log.info("check bkCreateCd");
				if (bkCreateCD.equals("JP")) {
					paramMap.put("varNo", varNo);
					log.info(" *** insertBK SQL *****" + sqlcr);
					log.info(" *** insertBK params *****" + paramMap.toString());
					rs1 = namedParameterJdbcTemplate.queryForRowSet(sqlcr, paramMap);
					while (rs1.next()) {
						createcustcd = rs1.getString("create_cust_cd");
					}
				}
				
				if(!isExcelUpload) {
					Map<String, String> mapErrorLength = this.checkLegthValidation(consigneeAddr, notifyParty,
							notifyPartyAddr, placeofDelivery, placeofReceipt, shpCrName, shpAddr, blNbr);
					if (mapErrorLength.size() > 0) {
						String[] tmpString = new String[mapErrorLength.size()];
						int count = 0;
						for (Entry<String, String> entry : mapErrorLength.entrySet()) {
							String key = entry.getKey();
							tmpString[count] = key;
							count++;
						}
						String errorMessage = CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_invalidLength, tmpString);
						throw new BusinessException(errorMessage);
					}
				}

				if (businessexception == null) {
					log.info("fetchCustomerCode");
					String esnCustCode = fetchCustomerCode(esnCustCd);
					// StringBuffer sb = new StringBuffer();
					sb.setLength(0);
					sb.append("INSERT INTO bk_details (");
					sb.append("BK_REF_NBR, CRG_STATUS, VAR_NBR, NBR_OF_CNTR, CNTR_TYPE, ");
					// BEGIN amended by Maksym JCMS Smart CR 6.10
					// sb.append("CNTR_SIZE, OUT_VOY_NBR, CARGO_TYPE, SHIPPER_CR_NBR,
					// SHIPPER_CONTACT, ");
					sb.append("CNTR_SIZE, OUT_VOY_NBR, CARGO_TYPE, CARGO_CATEGORY_CD, SHIPPER_CR_NBR, SHIPPER_CONTACT, ");
					// END amended by Maksym JCMS Smart CR 6.10
					sb.append("SHIPPER_ADDR, SHIPPER_NM, BK_WT, BK_VOL, BK_NBR_PKGS, ");
					sb.append("VARIANCE_PKGS, VARIANCE_VOL, VARIANCE_WT, PORT_DIS, DECLARANT_CD, ");
					sb.append("BK_CREATE_CD, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, SHIPPER_CD");
					// START CR FTZ - JUNE 2024
					sb.append(", CONS_NM, CONSIGNEE_ADDR, NOTIFY_PARTY, NOTIFY_PARTY_ADDR");
					sb.append(", PLACE_OF_DELIVERY, PLACE_OF_RECEIPT, BL_NBR");
					// END CR FTZ - JUNE 2024
					sb.append(") VALUES (");
					sb.append("  :bkRefNbr");
					sb.append(" ,:crgStatus");
					sb.append(" ,:varNo");
					sb.append(" , :cntrNo ");
					sb.append(" ,:cntrType");
					sb.append(" ,:cntrSize");
					sb.append(" ,:outVoyNbr");
					sb.append(" ,:cargoType");
					// BEGIN added by Maksym JCMS Smart CR 6.10
					sb.append(" ,:cargoCategory");
					// END added by Maksym JCMS Smart CR 6.10
					// sb.append(" ,'").append(shpCrNo).append("'");
					// sb.append(" ,'").append(shpContactNo).append("'");
					// sb.append(" ,'").append(GbmsCommonUtility.addApostr(shpadd)).append("'");
					sb.append(" , NULL");
					sb.append(" , NULL");
					sb.append(" ,:shpAddr");
					sb.append(" ,:shpCrName");
					sb.append(" , :bkWt ");
					sb.append(" , :bkVol ");
					sb.append(" , :bkNoOfPkg ");
					sb.append(" , :varPkgs ");
					sb.append(" , :varVol ");
					sb.append(" , :varWt ");
					sb.append(" ,:portDis");
					sb.append(" ,:esnCustCode");
					sb.append(" ,:createcustcd");
					sb.append(" ,:user");
					sb.append(" , sysdate ");
					sb.append(" ,:shpCd");
					// START CR FTZ - JUNE 2024
					sb.append(" ,:conName");
					sb.append(" ,:consigneeAddr");
					sb.append(" ,:notifyParty");
					sb.append(" ,:notifyPartyAddr");
					sb.append(" ,:placeofDelivery");
					sb.append(" ,:placeofReceipt");
					sb.append(" ,:blNbr");
					// END CR FTZ - JUNE 2024
					sb.append(")");

					sql = sb.toString();

					// Changed by Irene Tan on 14 Jun 2004 : audit trail logging
					// sqltrans = String.valueOf(String.valueOf( (new
					// StringBuffer("INSERT INTO bk_details_trans
					// (TRANS_NBR,BK_REF_NBR,CRG_STATUS,VAR_NBR,NBR_OF_CNTR,CNTR_TYPE,CNTR_SIZE,OUT_VOY_NBR,CARGO_TYPE,SHIPPER_CR_NBR,SHIPPER_CONTACT,SHIPPER_ADDR,SHIPPER_NM,BK_WT,BK_VOL,BK_NBR_PKGS,VARIANCE_PKGS,VARIANCE_VOL,VARIANCE_WT,PORT_DIS,DECLARANT_CD,BK_CREATE_CD,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM,SHIPPER_CD)
					// values('" +
					sb.setLength(0);
					sb.append("INSERT INTO bk_details_trans (");
					sb.append("TRANS_NBR, BK_REF_NBR, bk_status, esn_declared, CRG_STATUS, ");
					sb.append("VAR_NBR, NBR_OF_CNTR, CNTR_TYPE, CNTR_SIZE, OUT_VOY_NBR, ");
					// BEGIN amended by Maksym JCMS Smart CR 6.10
					// sb.append("CARGO_TYPE, SHIPPER_CR_NBR, SHIPPER_CONTACT, SHIPPER_ADDR,
					// SHIPPER_NM, ");
					sb.append("CARGO_TYPE, CARGO_CATEGORY_CD, SHIPPER_CR_NBR, SHIPPER_CONTACT, SHIPPER_ADDR, SHIPPER_NM, ");
					// END amended by Maksym JCMS Smart CR 6.10
					sb.append("BK_WT, BK_VOL, BK_NBR_PKGS, VARIANCE_PKGS, VARIANCE_VOL, ");
					sb.append("VARIANCE_WT, PORT_DIS, DECLARANT_CD, BK_CREATE_CD, LAST_MODIFY_USER_ID, ");
					sb.append("LAST_MODIFY_DTTM, SHIPPER_CD");
					// START CR FTZ - JUNE 2024
					sb.append(", CONS_NM, CONSIGNEE_ADDR, NOTIFY_PARTY, NOTIFY_PARTY_ADDR");
					sb.append(", PLACE_OF_DELIVERY, PLACE_OF_RECEIPT, BL_NBR");
					// END CR FTZ - JUNE 2024
					sb.append(")values(");
					sb.append("  :maxTransNbr");// maxTransNbr
					sb.append(" ,:bkRefNbr"); // "','")).append(bkRefNbr).append("','").append(
					sb.append(" ,'").append("A").append("'"); // bkstatus
					sb.append(" ,'").append("N").append("'"); // esn_declared
					sb.append(" ,:crgStatus"); // cargo status
					sb.append(" ,:varNo"); // var_nbr // end changed by Irene Tan on 14 Jun 2004
					sb.append(" ,:cntrNo ");
					sb.append(" ,:cntrType");
					sb.append(" ,:cntrSize");
					sb.append(" ,:outVoyNbr");
					sb.append(" ,:cargoType");
					// BEGIN added by Maksym JCMS Smart CR 6.10
					sb.append(" ,:cargoCategory");
					// END added by Maksym JCMS Smart CR 6.10

					// sb.append(" ,'").append(shpCrNo).append("'");
					// sb.append(" ,'").append(shpContactNo).append("'");
					// sb.append(" ,'").append(GbmsCommonUtility.addApostr(shpadd)).append("'");

					sb.append(" , NULL");
					sb.append(" , NULL");
					sb.append(" ,:shpAddr");

					sb.append(" ,:shpCrName");
					sb.append(" , :bkWt ");
					sb.append(" , :bkVol ");
					sb.append(" , :bkNoOfPkg ");
					sb.append(" , :varPkgs ");
					sb.append(" , :varVol ");
					sb.append(" , :varWt ");
					sb.append(" ,:portDis");
					sb.append(" ,:esnCustCode");
					sb.append(" ,:createcustcd");
					sb.append(" ,:user");
					sb.append(" , sysdate ");
					sb.append(" ,:shpCd");
					// START CR FTZ - JUNE 2024
					sb.append(" ,:conName");
					sb.append(" ,:consigneeAddr");
					sb.append(" ,:notifyParty");
					sb.append(" ,:notifyPartyAddr");
					sb.append(" ,:placeofDelivery");
					sb.append(" ,:placeofReceipt");
					sb.append(" ,:blNbr");
					// END CR FTZ - JUNE 2024
					sb.append(")");
					sqltrans = sb.toString();

					log.info(" *** insertBK SQL *****" + sql);
					paramMap.put("bkRefNbr", bkRefNbr);
					paramMap.put("crgStatus", crgStatus);
					paramMap.put("varNo", varNo);
					paramMap.put("cntrNo", cntrNo);
					paramMap.put("cntrType", cntrType);
					paramMap.put("cntrSize", cntrSize);
					paramMap.put("cargoType", cargoType);
					paramMap.put("outVoyNbr", outVoyNbr);
					paramMap.put("cargoCategory", cargoCategory);
					paramMap.put("shpCrName", GbmsCommonUtility.addApostr(shpCrName));
					paramMap.put("bkWt", bkWt);
					paramMap.put("bkVol", bkVol);
					paramMap.put("bkNoOfPkg", bkNoOfPkg);
					paramMap.put("varPkgs", varPkgs);
					paramMap.put("varVol", varVol);
					paramMap.put("varWt", varWt);
					paramMap.put("portDis", portDis);
					paramMap.put("esnCustCode", esnCustCode);
					paramMap.put("createcustcd", createcustcd);
					paramMap.put("user", user);
					paramMap.put("shpCd", shpCd);
					// START CR FTZ - JUNE 2024
					paramMap.put("shpAddr", shpAddr);
					paramMap.put("conName", conName);
					paramMap.put("consigneeAddr", consigneeAddr);
					paramMap.put("notifyParty", notifyParty);
					paramMap.put("notifyPartyAddr", notifyPartyAddr);
					paramMap.put("placeofDelivery", placeofDelivery);
					paramMap.put("placeofReceipt", placeofReceipt);
					paramMap.put("blNbr", blNbr);
					// END CR FTZ - JUNE 2024

					log.info(" *** insertBK SQL *****" + sql);
					log.info(" *** insertBK params *****" + paramMap.toString());
					
					int i = namedParameterJdbcTemplate.update(sql, paramMap);

					if (isToLogTxn) {
						paramMap.put("maxTransNbr", maxTransNbr);
						paramMap.put("bkRefNbr", bkRefNbr);
						paramMap.put("crgStatus", crgStatus);
						paramMap.put("varNo", varNo);
						paramMap.put("cntrNo", cntrNo);
						paramMap.put("cntrType", cntrType);
						paramMap.put("cntrSize", cntrSize);
						paramMap.put("outVoyNbr", outVoyNbr);
						paramMap.put("cargoType", cargoType);
						paramMap.put("cargoCategory", cargoCategory);
						paramMap.put("shpCrName", GbmsCommonUtility.addApostr(shpCrName));
						paramMap.put("bkWt", bkWt);
						paramMap.put("bkVol", bkVol);
						paramMap.put("bkNoOfPkg", bkNoOfPkg);
						paramMap.put("varPkgs", varPkgs);
						paramMap.put("varVol", varVol);
						paramMap.put("varWt", varWt);
						paramMap.put("portDis", portDis);
						paramMap.put("esnCustCode", esnCustCode);
						paramMap.put("createcustcd", createcustcd);
						paramMap.put("user", user);
						paramMap.put("shpCd", shpCd);
						// START CR FTZ - JUNE 2024
						paramMap.put("shpAddr", shpAddr);
						paramMap.put("conName", conName);
						paramMap.put("consigneeAddr", consigneeAddr);
						paramMap.put("notifyParty", notifyParty);
						paramMap.put("notifyPartyAddr", notifyPartyAddr);
						paramMap.put("placeofDelivery", placeofDelivery);
						paramMap.put("placeofReceipt", placeofReceipt);
						paramMap.put("blNbr", blNbr);
						// END CR FTZ - JUNE 2024
						
						log.info(" *** insertBK SQL *****" + sqltrans);
						log.info(" *** insertBK params *****" + paramMap.toString());
						namedParameterJdbcTemplate.update(sqltrans, paramMap);
						
					} else {
						log.info(" *** insertBK SQL *****" + sql);
					}

					if (i == 1) {
						status = "Y";
					}
				}

				log.info("END: *** insertBK Result *****" + CommonUtility.deNull(status));
			} catch (BusinessException e) {
				log.info("Exception insertBK : ", e);
				throw new BusinessException(e.getMessage());
			} catch (NullPointerException e) {
				log.info("Exception insertBK : ", e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception insertBK : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: insertBK DAO");
			}
			return status;
		}
		
		// ejb.sessionBeans.gbms.cargo.bookingReference-->BookingReferenceEJBBean-->
		// getBKDetailsList
		@Override
		public int getBKDetailsListCount(String varNo, String coCode, Criteria criteria) throws BusinessException {
			SqlRowSet rs = null;
			StringBuffer sb = new StringBuffer();
			String sql = "";
			int count = 0;
			Map<String, String> paramMap = new HashMap<String, String>();
			try {
				log.info("START: getBKDetailsListCount  DAO  Start Obj " + " varNo:" + CommonUtility.deNull(varNo) + " coCode:" + CommonUtility.deNull(coCode));
				if (coCode.equals("JP")) {
					sb.append("SELECT COUNT(*) ");
					sb.append(" from bk_details a , vessel b ,vessel_call c, crg_type d");
					sb.append(" where  c.vsl_nm = b.vsl_nm and c.vv_cd = :varNo ");
					sb.append(" and  a.var_nbr = :varNo ");
					sb.append(" and bk_status = 'A' and a.cargo_type = d.CRG_TYPE_CD order by lower(bk_ref_nbr)");

					sql = sb.toString();
				} else {
					sb.append("SELECT COUNT(*) ");
					sb.append(" from bk_details a , vessel b ,vessel_call c, crg_type d");
					sb.append(" where  c.vsl_nm = b.vsl_nm and c.vv_cd =:varNo");
					sb.append(" and  a.var_nbr =:varNo ");
					sb.append(" and bk_status = 'A' and a.cargo_type = d.CRG_TYPE_CD");
					sb.append(" and (c.CREATE_CUST_CD = :coCode");
					sb.append(" OR a.BK_CREATE_CD = :coCode )");
					sb.append(" order by lower(bk_ref_nbr)");
					sql = sb.toString();
				}
				log.info(" *** getBKDetailsListCount SQL *****" + sql);

				if (coCode.equals("JP")) {
					paramMap.put("varNo", varNo);
				} else {
					paramMap.put("varNo", varNo);
					paramMap.put("coCode", coCode);
				}
				log.info(" *** getBKDetailsListCount params *****" + paramMap.toString());
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				if (rs.next()) {
					count = rs.getInt(1);
				}
				log.info("END: *** getBKDetailsListCount Result *****" + count);

			} catch (NullPointerException e) {
				log.info("Exception getBKDetailsListCount : ", e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.info("Exception getBKDetailsListCount : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: getBKDetailsListCount DAO");
			}
			return count;
		}

		//ejb.sessionBeans.gbms.cargo.bookingReference;-->BookingReferenceEJBBean-->getPortCode()
		@Override
		public TableResult getPortCode(Criteria criteria) throws BusinessException {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			String sql = "";
			SqlRowSet rs = null;
			TableData tableData = new TableData();
			TopsModel topsModel = new TopsModel();
			TableResult tableResult = new TableResult();
			List<Object> voyagevect = new ArrayList<>();
			Map<String, String> map = new HashMap<String, String>();
			sql = "select * from un_port_code";
			try {
				log.info("START : getPortCode DAO START");
				
				if (criteria.isPaginated()) {
					tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
							paramMap, Integer.class));
					log.info("filter.total=" + tableData.getTotal());
				}
				if (criteria.isPaginated()) {
					sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());
				}
				
				log.info("***** getPortCode SQL *****" + sql.toString());
				log.info("params: " + paramMap.toString());
				
				rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
				while (rs.next()) {
					
					map = new HashMap<String, String>();
					String portL = "";
					String portLn = "";
					portL = rs.getString("PORT_CD");
					portLn = rs.getString("PORT_NM");
					map.put("portL", portL);
					map.put("portLn", portLn);
					voyagevect.add(map);
				}
				
				topsModel.put((Serializable) voyagevect);
				tableData.setListData(topsModel);
				tableResult.setData(tableData);
				log.info("END: *** getPortCode ***** Result portList: " + voyagevect.size());
			} catch (Exception e) {
				log.info("Exception getPortCode : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: *** getPortCode EDO ******");
			}
			return tableResult;
		}


		// for excel
		@Override
		public List<BookingReference> getBkRefDetails(String vvCd) throws BusinessException {
			StringBuffer sb = new StringBuffer();
			List<BookingReference> bookingReference = null;
			try {
				log.info("START getBkRefDetails: Vvcd: " + vvCd);
				sb.append("	SELECT '");
				sb.append(ConstantUtil.action_NA + "' as action,");
				sb.append(" bk.BK_REF_NBR, bk.BL_NBR bl_number, bk.SHIPPER_ADDR, ");
				sb.append(" bk.CARGO_TYPE cargoTypeDesc, bk.BK_NBR_PKGS, bk.VARIANCE_PKGS, bk.BK_WT, ");
				sb.append(" bk.VARIANCE_WT, bk.BK_VOL, bk.VARIANCE_VOL, bk.PORT_DIS port_of_discharge, ");
				sb.append(" bk.DECLARANT_CD declarant, bk.CONS_NM consignee_nm, bk.CONSIGNEE_ADDR, bk.NOTIFY_PARTY notify_party_nm, ");
				sb.append(" bk.NOTIFY_PARTY_ADDR, bk.PLACE_OF_DELIVERY, bk.PLACE_OF_RECEIPT, ");
				sb.append(" (SELECT CRG_TYPE_CD||'-'||CRG_TYPE_NM FROM GBMS.CRG_TYPE ct WHERE ct.CRG_TYPE_CD = bk.CARGO_TYPE) AS cargoTypeDesc, ");
				sb.append(" (SELECT PORT_CD FROM UN_PORT_CODE WHERE PORT_CD = bk.PORT_DIS ) AS port_of_discharge, ");
				sb.append(" (SELECT  DECODE(TDB_CR_NBR,null,UEN_NBR,TDB_CR_NBR) ");
				sb.append(" FROM customer where cust_cd = bk.DECLARANT_CD ) DECLARANT,");
				sb.append(" CASE WHEN bk.SHIPPER_CD = 'OTHERS' THEN ");
				sb.append("			'OTHERS' ELSE   ");
				sb.append("			(SELECT co_nm || ' (' || co_cd || ')' as co_nm FROM  tops.company_code WHERE  ");
				sb.append("			co_cd = BK.SHIPPER_CD) END AS shipper_nm,   ");
				sb.append(" CASE WHEN bk.SHIPPER_CD = 'OTHERS' THEN ");
				sb.append("			bk.shipper_nm ELSE   ");
				sb.append("			NULL END AS shipper_nm_others   ");
				sb.append(" from bk_details bk where var_nbr = :vvCd AND BK_STATUS!='X'");
				
				Map<String, String> paramMap = new HashMap<>();
				paramMap.put("vvCd", vvCd);
				log.info("getManifestDetails : SQL:" + sb.toString() + "Params:" + paramMap.toString());
				bookingReference = namedParameterJdbcTemplate.query(sb.toString(), paramMap,
						new BeanPropertyRowMapper<BookingReference>(BookingReference.class));
				log.info("getBkRefDetails : size" + bookingReference.size());
			} catch (Exception e) {
				log.info("Exception getBkRefDetails : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info(" END getBkRefDetails ");
			}
			return bookingReference;
		}
		
		@Override
		public PageDetails getBkRefDocumentDetail(String vvCd) throws BusinessException {
			StringBuffer sb = new StringBuffer();
			PageDetails pageDetails = null;
			try {
				log.info("START getPageDetails : VVCd:" + vvCd);
				pageDetails = getVesselCallDetails(vvCd);
				sb.append("");
				log.info("getPageDetails   : SQL:" + sb.toString() + "Params:" + vvCd);
				List<String> instructions = Arrays.asList(new String[]{"Please download the latest file to ensure successful file upload.", 
																		"Please ensure that the correct action is selected to add/update/delete corresponding Booking Reference line item in the Excel file. Note that only successfully processed line items will be updated in the system."});//namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, (String.class));
				log.info("getPageDetails size:" + instructions.size());
				pageDetails.setInstructions(instructions);
				Template template = new Template();
				template.setFileName(ConstantUtil.bkRef + ConstantUtil.file_ext);
				template.setRefId(vvCd);     

				List<Template> templateDet = new ArrayList<Template>();
				templateDet.add(template);
				pageDetails.setTemplate(templateDet);
			} catch (Exception e) {
				log.info("Exception getPageDetails : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END getPageDetails");
			}
			return pageDetails;
		}
		
		public Boolean isBkSubmissionAllowed(Criteria criteria) throws BusinessException {
			StringBuffer sb = new StringBuffer();
			Boolean result = true;
			try {
				log.info("START isBkSubmissionAllowed criteria: " + criteria.toString());
				log.info("isBkSubmissionAllowed DAO criteria:" + criteria.toString());
				String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
				String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
				Map<String, String> paramMap = new HashMap<>();
				int count = 0;
				paramMap = new HashMap<>();
				paramMap.put("vvCd", vvCd);
				if (coCd.equalsIgnoreCase("JP")) {
					sb = new StringBuffer();
					sb.append("select count(*) from TOPS.VESSEL_CALL WHERE VV_CD=:vvCd AND GB_CLOSE_BJ_IND='Y'  ");
					log.info("isSubmissionAllowed SQL : " + sb.toString() + "parammap" + paramMap.toString());
					count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
				} else {
					sb = new StringBuffer();
					sb.append("select count(*) from TOPS.VESSEL_CALL WHERE VV_CD=:vvCd AND GB_CLOSE_BJ_IND='Y'  ");
					log.info("isSubmissionAllowed SQL : " + sb.toString() + "parammap" + paramMap.toString());
					count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
					if (count == 0) {
						sb = new StringBuffer();
						sb.append(
								"select count(*) from TOPS.AUDIT_TRAIL_VESSEL_CALL WHERE VV_CD=:vvCd AND GB_CLOSE_BJ_IND='Y' ");
						log.info("isSubmissionAllowed SQL : " + sb.toString() + "parammap" + paramMap.toString());
						count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
					}
				}
				if (count > 0)
					result = false;
			} catch (Exception e) {
				log.info("Exception isBkSubmissionAllowed : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END isBkSubmissionAllowed : " + result);
			}
			return result;
		}
			
		public PageDetails getVesselCallDetails(String vvCd) throws BusinessException {
			StringBuffer sb = new StringBuffer();
			PageDetails vesselCallDetails = null; 
			try {
				log.info("START getVesselCallDetails" + "Params:" + vvCd);
				sb.append("SELECT VSL_NM AS vesselName ,VV_CD AS vvCd,IN_VOY_NBR AS inwardVoyNo, OUT_VOY_NBR as outVoyNo, IN_VOY_NBR ||'-'||OUT_VOY_NBR as voyageNo  FROM tops.VESSEL_CALL WHERE VV_CD=:vvCd");
				Map<String, String> paramMap = new HashMap<>();
				paramMap.put("vvCd", vvCd);
				log.info("getVesselCallDetails :" + "SQL:" + sb.toString() + "Param:" + paramMap.toString());
				vesselCallDetails = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap,
						new BeanPropertyRowMapper<PageDetails>(PageDetails.class));
				log.info("getVesselCallDetails : " + vesselCallDetails.toString());
			} catch (Exception e) {
				log.info("Exception getVesselCallDetails : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info(" END getVesselCallDetails ");
			}
			return vesselCallDetails;
		}
		
		@Override
		public TableResult getBkActionTrail(Criteria criteria) throws BusinessException {
			TableResult result = new TableResult();
			StringBuffer sb = new StringBuffer();
			String sql = null;
			Map<String, String> paramMap = new HashMap<String, String>();
			List<BkRefActionTrail> info = new ArrayList<BkRefActionTrail>();
			try {

				String vvCd = criteria.getPredicates().get("vvCd");
				String transDate = criteria.getPredicates().get("transDate");
				String actionBy = criteria.getPredicates().get("actionBy");
				String summary = criteria.getPredicates().get("summary");
				int start = criteria.getStart();
				int limit = criteria.getLimit();

				log.info("START bkActionTrail:" + "vvCd:" + vvCd + "transDate:" + transDate + "actionBy:"
						+ actionBy + "summary:" + summary + "start:" + start + "limit:" + limit);
				TableData tableData = new TableData();
				sb.append("SELECT trl.BK_ACT_TRL_ID, trl.VV_CD,");
				sb.append("CASE WHEN ac.user_nm IS NOT NULL THEN  CONCAT (CONCAT (ac.user_nm, ' - '), ac.CUST_CD) ELSE '' END last_modify_user_id , ");
				sb.append(
						" TO_CHAR(trl.LAST_MODIFY_DTTM,'DD-MM-YYYY HH24:MI') LAST_MODIFY_DTTM, trl.REMARKS ");
				sb.append("FROM GBMS.BK_ACT_TRL trl  ");
				sb.append(" LEFT JOIN TOPS.logon_acct ac ON ac.login_id= SUBSTR(trl.LAST_MODIFY_USER_ID, INSTR( trl.LAST_MODIFY_USER_ID, '/', -1 ) + 1 )   ");
				sb.append(" WHERE trl.VV_CD = :vvCd ORDER BY trl.LAST_MODIFY_DTTM DESC ");

				paramMap.put("vvCd", vvCd);				
				sql = sb.toString();
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")", paramMap,Integer.class));
				sql = CommonUtil.getPaginatedSql(sql, start, limit);
				
				info = namedParameterJdbcTemplate.query(sql, paramMap,new BeanPropertyRowMapper<BkRefActionTrail>(BkRefActionTrail.class));
				log.info("bkActionTrail SQL" + sql.toString() + "Params:" + paramMap.toString());
				
				TopsModel topsModel = new TopsModel();
				for (BkRefActionTrail object : info) {
					topsModel.put(object);
				}
				tableData.setListData(topsModel);
				result.setSuccess(true);
				result.setData(tableData);
			} catch (Exception e) {
				log.info("Exception bkActionTrail : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END bkActionTrail");
			}
			return result;
		}

		@Override
		public List<BkRefUploadConfig> getBkTemplateHeader() throws BusinessException {
			StringBuffer sb = new StringBuffer();
			List<BkRefUploadConfig> bkRefUploadConfig = null;
			try {
				log.info("START getBkTemplateHeader");
				sb.append("SELECT BK_UPLOAD_CONFIG_ID,ATTR_NM attr_name,ATTR_DESC,INPUT_TYPE,DISPLAY_SEQ,MANDATORY_IND,LOOKUP_TABLE,");
				sb.append("LOOKUP_CAT_CD,COLUMN_NM,LAST_MODIFY_USER_ID,LAST_MODIFY_DTTM ");
				sb.append("FROM GBMS.BK_UPLOAD_CONFIG ORDER BY DISPLAY_SEQ ASC");
				log.info("getTemplateHeader :" + "SQL:" + sb.toString());
				
				bkRefUploadConfig = namedParameterJdbcTemplate.query(sb.toString(),
						new BeanPropertyRowMapper<BkRefUploadConfig>(BkRefUploadConfig.class));
				
				
				log.info("getTemplateHeader : size:" + bkRefUploadConfig.size());
			} catch (Exception e) {
				log.info("Exception getBkTemplateHeader : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END getBkTemplateHeader");
			}
			return bkRefUploadConfig;
		}

		@Override
		public Long insertBkrefExcelDetails(BookingReferenceFileUploadDetails bookingReferenceFileUploadDetails)
				throws BusinessException {

			StringBuffer sb = new StringBuffer();
			try {
				log.info("Start insertBkrefExcelDetails :" + bookingReferenceFileUploadDetails.toString());
				
				StringBuilder sbSeq = new StringBuilder();
				sbSeq.append("SELECT GBMS.SEQ_BK_UPLOAD_SEQ_NBR.nextval AS seqVal FROM DUAL");
				Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(), new HashMap<String, String>());
				BigDecimal seqNbr = (BigDecimal) results.get("seqVal");
				log.info("seqNbr " + seqNbr);
				
				sb.append("INSERT INTO GBMS.BK_UPLOAD_DETAILS ");
				sb.append("( BK_UPLOAD_SEQ_NBR, ACTUAL_FILE_NM, VV_CD, ASSIGNED_FILE_NM, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM,SUMMARY_DESC)");
				sb.append("VALUES( :seq_id, :actual_file_name, :vv_cd, :assigned_file_name, ");
				sb.append(":last_modified_user_id, TO_TIMESTAMP(:last_modified_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'),:remarks)");
				
				bookingReferenceFileUploadDetails.setSeq_id(seqNbr.longValue());
				log.info("insertBkrefExcelDetails:SQL" + bookingReferenceFileUploadDetails.toString() + "SQL : " + sb.toString());
				
				int rows = namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(bookingReferenceFileUploadDetails));
				log.info("rows " + rows);
				
				
				// Audit table.
				sb.setLength(0);
				sb.append("INSERT INTO GBMS.BK_UPLOAD_DETAILS_TRANS ");
				sb.append("(AUDIT_DTTM, BK_UPLOAD_SEQ_NBR, VV_CD, ACTUAL_FILE_NM, ASSIGNED_FILE_NM, ");
				sb.append("LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, SUMMARY_DESC) ");
				sb.append("VALUES(sysdate, :seq_id, :vv_cd, :actual_file_name, :assigned_file_name, ");
				sb.append(":last_modified_user_id, TO_TIMESTAMP(:last_modified_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'), :remarks) ");
				int rowsTrans = namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(bookingReferenceFileUploadDetails));
				log.info("rowsTrans " + rowsTrans);
				
				return (seqNbr.longValue());
				
			} catch (Exception e) {
				log.info("Exception insertBkrefExcelDetails : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END insertBkrefExcelDetails ");
			}
			
		
		}
		
		@Override
		public boolean updateBkExcelDetails(Long seq_id, String output_file_name) throws BusinessException {
			StringBuffer sb = new StringBuffer();
			boolean update = false;
			try {
				log.info("START updateBkExcelDetails : " + "seq_id:" + seq_id + "output_file_name:"
						+ output_file_name);
				sb.append("UPDATE GBMS.BK_UPLOAD_DETAILS ");
				sb.append("SET ");
				sb.append("PROCESSED_FILE_NM =:output_file_name ");
				sb.append("WHERE BK_UPLOAD_SEQ_NBR=:seq_id");
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("output_file_name", output_file_name);
				paramMap.put("seq_id", seq_id);
				log.info("updateManifestExcelDetails: " + "SQL:" + sb.toString() + "Params:" + paramMap.toString());
				int rows = namedParameterJdbcTemplate.update(sb.toString(), paramMap);
				log.info("updateBkExcelDetails:rows " + rows);
				update = true;
			} catch (Exception e) {
				log.info("Exception updateBkExcelDetails : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END updateBkExcelDetails");
			}
			return update;
		}
		
		@Override
		public List<BookingReference> insertBkRefData(List<BookingReference> bkrefRecords, String vvCd,
				String userId, String companyCode) throws BusinessException {
			try {
				log.info("START insertBkRefData " + " vvCd :" + vvCd + "userId :" + userId + ", bkrefRecords :"
						+ bkrefRecords.size());
				boolean processsResponse = false;
				for (BookingReference bookingReference : bkrefRecords) {

					try {
						processsResponse = false;
						bookingReference.setVar_nbr(vvCd);
						bookingReference.setLast_modify_user_id(userId);
						String bkRefNbr = bookingReference.getBk_ref_nbr();

						Map<String, String> vvStatus = checkUnberthStatus(vvCd);
						// Action - Insert and success row validated records
						if (bookingReference.getAction() != null
								&& bookingReference.getAction().equalsIgnoreCase(ConstantUtil.action_delete)
								&& bookingReference.getMessage().equalsIgnoreCase(ConstantUtil.success)) {
							log.info("delete BkData :" + bookingReference.toString());

							// DELETE
							if (bkRefNbr == null || bkRefNbr == "") {
								log.info(" Excel processExcelBkDetails :" + ConstantUtil.ErrorMsg_BkNbr_NotExist
										+ ", bkref record is :" + bookingReference.toString());
								bookingReference.setMessage(ConstantUtil.ErrorMsg_BkNbr_NotExist);
								continue;
							}
							

							String checkCancelAmendResultC = chkCancelAmend(bkRefNbr, companyCode, "C");
							if (!checkCancelAmendResultC.equals("N")) {
								log.info(" Excel processExcelBkDetails :" + ConstantUtil.ErrorMsg_cannot_delete
										+ ", bkref record is :" + bookingReference.toString());
								bookingReference.setMessage(ConstantUtil.ErrorMsg_cannot_delete);
								continue;
							} 
							
							boolean isCargoDeleted = deleteBkDetails(userId, bkRefNbr);
							log.info("isCargoDeleted : " + isCargoDeleted);

						} else if (bookingReference.getMessage().equalsIgnoreCase(ConstantUtil.success)) {

							log.info("insert/update BkData :" + bookingReference.toString());

							if (bookingReference.getAction().equalsIgnoreCase(ConstantUtil.action_add)) {
								// ADD
								
								if (vvStatus.get("vv_status_ind").equalsIgnoreCase("UB")
										|| vvStatus.get("vv_status_ind").equalsIgnoreCase("CL")
										|| (vvStatus.get("gb_close_ship_ind").equalsIgnoreCase("Y")
												&& companyCode != "JP")) {
									
									log.info(" Excel processExcelBkDetails Unberth Vessel vvCd :" + vvCd
											+ ", bkRef record is :" + bookingReference.toString());
									bookingReference.setMessage(ConstantUtil.ErrorMsg_VesselUnberth_Add);
									continue;
								}
//								// checking for existing record and correct adpNo	
								String bkref = "";
								String declarantExist = "";
								try {
									 bkref = chkBKCode(bkRefNbr);
									 declarantExist = chkCrNo(bookingReference.getDeclarant());
								} catch (BusinessException e) {
									String errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
									if (errorMessage == null) {
										errorMessage = CommonUtility.getExceptionMessage(e);
									}
									bookingReference.setMessage(errorMessage);
									continue;
								}

								if (bkref.equals("Y")) {
									log.info(" Excel processExcelBkDetails Existing  :" + bkRefNbr
											+ ", bkRef record is :" + bookingReference.toString());
									bookingReference.setMessage(ConstantUtil.ErrorMsg_Existing_BkNbr);
									continue;
								} else if (declarantExist.equals("N")) {
									log.info(" Excel processExcelBkDetails Not valid Esn decalarant  :"
											+ bookingReference.getDeclarant() + ", bkRef record is :"
											+ bookingReference.toString());
									bookingReference.setMessage(ConstantUtil.ErrorMsg_Valid_Declarant);
									continue;
								}

								processsResponse = insertBkDetails(bookingReference, companyCode, userId);

							} else if (bookingReference.getAction().equalsIgnoreCase(ConstantUtil.action_update)) {
								// UPDATE
								
								if (bkRefNbr == null || bkRefNbr == "") {
									log.info(" Excel processExcelBkDetails :" + ConstantUtil.ErrorMsg_BkNbr_NotExist
											+ ", bkRef record is :" + bookingReference.toString());
									bookingReference.setMessage(ConstantUtil.ErrorMsg_BkNbr_NotExist);
									continue;
								} else if (checkATUDttm(vvCd)) {
									log.info(" Excel processExcelBkDetails :" + ConstantUtil.ErrorMsg_VesselUnberth
											+ ", bkRef record is :" + bookingReference.toString());
									bookingReference.setMessage(ConstantUtil.ErrorMsg_VesselUnberth);
									continue;
								}

								processsResponse = updateBkDetails(bookingReference);

							} else if (bookingReference.getAction()
									.equalsIgnoreCase(ConstantUtil.action_custom_info)) {
								processsResponse = updateBlNbrBkDetails(bookingReference);
							}

							if (!processsResponse) {
								log.info(" Excel processExcelBkDetails :" + ConstantUtil.ErrorMsg_BkDetailsProcess
										+ ", bk record is :" + bookingReference.toString());
								bookingReference.setMessage(ConstantUtil.ErrorMsg_BkDetailsProcess);
								continue;
							}

						}
					} catch (Exception e) {
						log.info("Exception insertBkRefData : ", e);
						log.info(" Exception in Excel processExcelBkrefDetails iteration  " + " for  bookingReference :"
								+ bookingReference.toString() + ", excpetion " + e.toString());
						bookingReference.setMessage(ConstantUtil.ErrorMsg_Common);
					}
				}
			} catch (Exception e) {
				log.info("Exception insertBkRefData : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END insertBkRefData ");
			}
			return bkrefRecords;
		}
		
		private Map<String, String> checkUnberthStatus(String vvCd) throws BusinessException {
			SqlRowSet rs = null;
			StringBuffer sb = new StringBuffer();
			Map<String, String> paramMap = new HashMap<String, String>();
			Map<String, String> map = new HashMap<String, String>();
			try {
				log.info("START checkUnberthStatus vvcd:" + vvCd);
				sb.append(" SELECT GB_CLOSE_SHP_IND, (CASE WHEN c.vv_status_ind='UB' AND  ");
				sb.append(" (NVL((SELECT (SYSDATE-TO_DATE(TO_CHAR(CREATE_DTTM,'DD-MM-YYYY HH24MISS'),'DD-MM-YYYY HH24MISS'))*24*60-  ");
				sb.append(" (SELECT TO_NUMBER(MISC_TYPE_NM) FROM SYSTEM_CONFIG WHERE CAT_CD='REOPEN_DOC_SETTING'  ");
				sb.append(" AND REC_STATUS='A' AND MISC_TYPE_CD='GC_BOOKING') FROM SYSTEM_CONFIG WHERE CAT_CD='REOPEN_DOC_GC_ESN'  ");
				sb.append("	AND REC_STATUS='A' AND MISC_TYPE_CD=c.vv_cd),1))<=0 THEN 'BR' ELSE c.vv_status_ind END) vv_status_ind ");
				sb.append(" FROM vessel_call c WHERE c.vv_cd = :vvcd ");
				paramMap.put("vvcd", vvCd);
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				if (rs.next()) {
					map.put("vv_status_ind",rs.getString("vv_status_ind"));
					map.put("gb_close_ship_ind",rs.getString("GB_CLOSE_SHP_IND"));
				}
				
			} catch (Exception e) {
				log.info("Exception checkUnberthStatus : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END checkUnberthStatus map:" + map.toString());
			}
			return map;

		}

		@Override
		public boolean checkATUDttm(String vvcd) throws BusinessException {
			boolean prohibit = false;
			SqlRowSet rs = null;
			StringBuffer sb = new StringBuffer();
			Map<String, String> paramMap = new HashMap<String, String>();
			Date atudttm = new Date();
			try {
				log.info("START checkATUDttm : " + "vvcd:" + vvcd);
				sb.append("SELECT ATU_DTTM FROM TOPS.BERTHING WHERE ");
				sb.append("VV_CD=:vvcd");
				paramMap.put("vvcd", vvcd);
				rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), paramMap);
				if(rs.next()) {
					atudttm = rs.getDate("ATU_DTTM");
				}
				if(atudttm != null && new Date().after(atudttm)) {
					prohibit = true;
				}
			} catch (Exception e) {
				log.info("Exception checkATUDttm : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END checkATUDttm prohibited:" + prohibit);
			}
			return prohibit;
		}

	private boolean deleteBkDetails(String userId, String bkRefNbr) throws BusinessException {
		boolean update = false;
		try {
			log.info("START deleteBkDetails : " + "userId:" + userId + "bkRefNbr:" + bkRefNbr);
			String status = this.cancelBK(bkRefNbr, userId); 
			
			if(status.equalsIgnoreCase("Y")) {
				update = true;
			}
			
		} catch (BusinessException e) {
			log.info("Exception deleteBkDetails : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception deleteBkDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END deleteBkDetails");
		}
		return update;
	}

	
	private boolean insertBkDetails(BookingReference bookingReference, String coCd, String userId) throws BusinessException {

		boolean insert = false;
		try {
			log.info("START insertBkDetails bookingReference :" + bookingReference.toString());
			
			String bkRefNbr = bookingReference.getBk_ref_nbr();
			String crgStatus = null;
			String varNo = bookingReference.getVar_nbr();
			String cntrNo = null;
			String cntrType = null;
			String cntrSize = null;
			PageDetails vsldetails = getVesselCallDetails(varNo);
			String outVoyNbr = vsldetails.getOutVoyNo();
			String conrCode = null;//
			String cargoType = bookingReference.getCargoTypeDesc().split("-")[0];
			String cargoCategory = ConstantUtil.DefaultCargoCategory; // Default Cargo Category
			String shpCrNo = bookingReference.getShipper_cd();
			String shpContactNo = null;
			String shpAddr = bookingReference.getShipper_addr();
			String shpNm = bookingReference.getShipper_nm();
			String bkWt = bookingReference.getBk_wt();
			String bkVol = bookingReference.getBk_vol();
			String bkNoOfPkg = bookingReference.getBk_nbr_pkgs();
			String varPkgs = bookingReference.getVariance_pkgs();
			String varVol = bookingReference.getVariance_vol();
			String varWt = bookingReference.getVariance_wt();
			String portDis = bookingReference.getPort_of_discharge();
			String adpCustCd = bookingReference.getDeclarant();
			String conName = bookingReference.getConsignee_nm();
			String consigneeAddr = bookingReference.getConsignee_addr();
			String notifyParty = bookingReference.getNotify_party_nm();
			String notifyPartyAddr = bookingReference.getNotify_party_addr();
			String placeofDelivery = bookingReference.getPlace_of_delivery();
			String placeofReceipt = bookingReference.getPlace_of_receipt();
			String blNbr = bookingReference.getBl_number();

			String status = this.insertBK(bkRefNbr, crgStatus, varNo, cntrNo, cntrType, cntrSize, outVoyNbr, conrCode,
					cargoType, cargoCategory, shpCrNo, shpContactNo, shpAddr, shpNm, bkWt, bkVol, bkNoOfPkg, varPkgs,
					varVol, varWt, portDis, adpCustCd, coCd, userId, conName, consigneeAddr, notifyParty,
					notifyPartyAddr, placeofDelivery, placeofReceipt, blNbr, true);

			if (status.equalsIgnoreCase("Y")) {
				insert = true;
			}
		} catch (Exception e) {
			log.info("Exception updateBkDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END updateBkDetails");
		}
		return insert;
	}
	
	private boolean updateBkDetails(BookingReference bookingReference) throws BusinessException {

		boolean update = false;
		try {
			log.info("START updateBkDetails bookingReference :" + bookingReference.toString());
			
			String bkRefNbr = bookingReference.getBk_ref_nbr();
			String crgStatus = null;
			String varNo = bookingReference.getVar_nbr();
			String cntrNo = null;
			String cntrType = null;
			String cntrSize = null;
			PageDetails vsldetails = getVesselCallDetails(varNo);

			String vslId = vsldetails.getVesselName();
			String outVoyNbr = vsldetails.getOutVoyNo();
			String conrCode = null;//
			String cargoType = bookingReference.getCargoTypeDesc().split("-")[0];
			String cargoCategory = ConstantUtil.DefaultCargoCategory; // Default Cargo Category
			String shpCrNo = bookingReference.getShipper_cd();
			String shpContactNo = null;
			String shpAddr = bookingReference.getShipper_addr();
			String shpNm = bookingReference.getShipper_nm();
			String bkWt = bookingReference.getBk_wt();
			String bkVol = bookingReference.getBk_vol();
			String bkNoOfPkg = bookingReference.getBk_nbr_pkgs();
			String varPkgs = bookingReference.getVariance_pkgs();
			String varVol = bookingReference.getVariance_vol();
			String varWt = bookingReference.getVariance_wt();
			String portDis = bookingReference.getPort_of_discharge();
			String adpCustCd = bookingReference.getDeclarant();
			String bkCmpCode = getBkCmpCode(bkRefNbr);
			String user = bookingReference.getLast_modify_user_id();
			String conName = bookingReference.getConsignee_nm();
			String consigneeAddr = bookingReference.getConsignee_addr();
			String notifyParty = bookingReference.getNotify_party_nm();
			String notifyPartyAddr = bookingReference.getNotify_party_addr();
			String placeofDelivery = bookingReference.getPlace_of_delivery();
			String placeofReceipt = bookingReference.getPlace_of_receipt();
			String blNbr = bookingReference.getBl_number();

			String status = this.updateBKForDPE(bkRefNbr, crgStatus, varNo, cntrNo, cntrType, cntrSize, vslId, outVoyNbr,
					conrCode, cargoType, cargoCategory, shpCrNo, shpContactNo, shpAddr, shpNm, bkWt, bkVol, bkNoOfPkg,
					varPkgs, varVol, varWt, portDis, adpCustCd, bkCmpCode, user, false, conName, consigneeAddr,
					notifyParty, notifyPartyAddr, placeofDelivery, placeofReceipt, blNbr, true);

			if (status.equalsIgnoreCase("Y")) {
				update = true;
			}
		} catch (Exception e) {
			log.info("Exception updateBkDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END updateBkDetails");
		}
		return update;
	}
	

	private String getBkCmpCode(String bkRefNbr) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		String bkCd = null;
		try {
			log.info("START updateBlNbrBkDetails bkRefNbr :" + bkRefNbr);
			
			sb.append("SELECT bk_create_cd FROM BK_DETAILS");
			sb.append(" WHERE bk_ref_nbr =:bkref");
			String sql = sb.toString();
			paramMap.put("bkref", bkRefNbr);
			log.info("sql:" + sql);
			log.info("paramMap:" + paramMap.toString());

			SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			if(rs.next()) {
				bkCd = rs.getString("bk_create_cd");
			}
			
		} catch (Exception e) {
			log.info("Exception updateBlNbrBkDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END updateBlNbrBkDetails result" + bkCd);
		}
		
		return bkCd;
	}

	private boolean updateBlNbrBkDetails(BookingReference bookingReference) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
	StringBuffer sb = new StringBuffer();
	int result = 0;
	boolean update = false;
	try {
		log.info("START updateBlNbrBkDetails bookingReference :" + bookingReference.toString());

		sb.append("UPDATE BK_DETAILS SET BL_NBR=:blNbr, SHIPPER_NM=:shipperNm, SHIPPER_ADDR=:shipperAddr,");
		sb.append("CONS_NM=:consigneeNm, CONSIGNEE_ADDR=:consigneeAddr, NOTIFY_PARTY=:notifyPartyNm,");
		sb.append("NOTIFY_PARTY_ADDR=:notifyPrtyAddr, PLACE_OF_DELIVERY=:pod, PLACE_OF_RECEIPT=:por ");
		sb.append(",SHIPPER_CD=:shipperCd ");
		sb.append(" WHERE bk_ref_nbr =:bkref");
		String sql = sb.toString();
		paramMap.put("blNbr", bookingReference.getBl_number());
		paramMap.put("shipperNm", bookingReference.getShipper_nm());
		paramMap.put("shipperAddr", bookingReference.getShipper_addr());
		paramMap.put("consigneeNm", bookingReference.getConsignee_nm());
		paramMap.put("consigneeAddr", bookingReference.getConsignee_addr());
		paramMap.put("notifyPartyNm", bookingReference.getNotify_party_nm());
		paramMap.put("notifyPrtyAddr", bookingReference.getNotify_party_addr());
		paramMap.put("pod", bookingReference.getPlace_of_delivery());
		paramMap.put("por", bookingReference.getPlace_of_receipt());
		paramMap.put("shipperCd", bookingReference.getShipper_cd());
		paramMap.put("bkref", bookingReference.getBk_ref_nbr());
		log.info("sql:" + sql);
		log.info("paramMap:" + paramMap.toString());
		result = namedParameterJdbcTemplate.update(sql, paramMap);
		log.info("result:" + result);
		if(result > 0) {
			update = true; 
		}
		
	} catch (Exception e) {
		log.info("Exception updateBlNbrBkDetails : ", e);
		throw new BusinessException("M4201");
	} finally {
		log.info("END updateBlNbrBkDetails result" + result);
	}
	return update;
	}

	@Override
	public boolean insertActionTrail(String vvCd, String summary, String lastTimestamp, String userId) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		BkRefActionTrail bkRefActionTrl = new BkRefActionTrail();
		boolean insert = false;
		try {
			log.info("START insertActionTrail :  vvCd:" + vvCd + "summary:" + summary
					+ "lastTimestamp:" + lastTimestamp + "userId:" + userId);
			
			StringBuilder sbSeq = new StringBuilder();
			sbSeq.append("SELECT GBMS.SEQ_BK_ACT_TRL_ID.nextval AS seqVal FROM DUAL");
			Map<String, Object> results = namedParameterJdbcTemplate.queryForMap(sbSeq.toString(), new HashMap<String, String>());
			BigDecimal id = (BigDecimal) results.get("seqVal");

			sb.append("INSERT INTO GBMS.BK_ACT_TRL");
			sb.append("( BK_ACT_TRL_ID, VV_CD, LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, REMARKS)");
			sb.append("VALUES( :bk_act_trl_id, :vv_cd, :last_modify_user_id,  TO_TIMESTAMP(:last_modify_dttm,'dd-mm-yyyy hh24:mi:ss.ff3'),:remarks)");

			bkRefActionTrl.setBk_act_trl_id(id.longValue());
			bkRefActionTrl.setVv_cd(vvCd);
			bkRefActionTrl.setLast_modify_user_id(userId);
			bkRefActionTrl.setLast_modify_dttm(lastTimestamp);
			bkRefActionTrl.setRemarks(summary);
			log.info("insertManifest_action_trial:SQL" + bkRefActionTrl.toString() + "SQL : " + sb.toString());
			
			int rows = namedParameterJdbcTemplate.update(sb.toString(), new BeanPropertySqlParameterSource(bkRefActionTrl));
			log.info("insertActionTrail:" + rows);
			insert = true;
						
		} catch (Exception e) {
			log.info("Exception insertActionTrail : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END insertActionTrail");
		}
		return insert;
	}
	
	@Override
	public BkRefActionTrailDetails getBkActionTrailDetail(String bk_act_trl_id) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		BkRefActionTrailDetails bkRef_trail_details = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START getBkActionTrailDetail " + "Params:" + bk_act_trl_id);
			sb.append("	SELECT ");
			sb.append("	bkud.BK_UPLOAD_SEQ_NBR as seq_id, ");
			sb.append("	bkud.ACTUAL_FILE_NM as actual_file_name, ");
			sb.append("	bkud.ASSIGNED_FILE_NM as assigned_file_name, ");
			sb.append("	bkud.VV_CD as vv_cd, ");
			sb.append("	bkud.PROCESSED_FILE_NM AS output_file_name, ");
			sb.append("	TO_CHAR(bkat.LAST_MODIFY_DTTM, 'DD-MM-YYYY HH24:MI') last_modified_dttm, ");
			sb.append(" CASE WHEN ac.user_nm IS NOT NULL THEN  CONCAT (CONCAT (ac.user_nm, ' - '), ac.CUST_CD) ELSE '' END last_modified_user_id , ");
			sb.append("	bkat.REMARKS  as remarks");
			sb.append("	FROM ");
			sb.append("	gbms.BK_ACT_TRL bkat ");
			sb.append(" LEFT JOIN gbms.BK_UPLOAD_DETAILS bkud ON bkat.LAST_MODIFY_DTTM = bkud.LAST_MODIFY_DTTM AND bkat.VV_CD = bkud.VV_CD  ");
			sb.append(" LEFT JOIN TOPS.logon_acct ac ON ac.login_id= SUBSTR(bkat.LAST_MODIFY_USER_ID, INSTR( bkat.LAST_MODIFY_USER_ID, '/', -1 ) + 1 ) ");
			sb.append("	WHERE ");
			sb.append("	bkat.BK_ACT_TRL_ID = :bk_act_trl_id  ");
			paramMap.put("bk_act_trl_id", bk_act_trl_id);
			log.info("getBkActionTrailDetail SQL:" + sb.toString() + "param:" + bk_act_trl_id);
			bkRef_trail_details = namedParameterJdbcTemplate.queryForObject(sb.toString(),
					paramMap, new BeanPropertyRowMapper<BkRefActionTrailDetails>(BkRefActionTrailDetails.class));
			Pattern p = Pattern.compile("-?\\d+");
			Matcher m = p.matcher(bkRef_trail_details.getRemarks());
			int mIndex = 0;
			while (m.find()) {
				if (mIndex == 0) {
					bkRef_trail_details.setTotalLineProcessed(m.group());
				} else if (mIndex == 1) {
					bkRef_trail_details.setTotalSuccess(m.group());
				} else if (mIndex == 2) {
					bkRef_trail_details.setTotalFail(m.group());
				}
				mIndex++;
			}
		} catch (Exception e) {
			log.info("Exception getBkActionTrailDetail : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getBkActionTrailDetail");
		}
		return bkRef_trail_details;
	}


	@Override
	public void updateBlNbr(Criteria criteria) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		int result = 0;
		String sql = null;
		String brNo, blNbr;
		try {
			log.info("START updateBlNbr criteria :" + criteria.toString());
			int arrSize = Integer.parseInt(CommonUtil.deNull(criteria.getPredicates().get("arrSize")));
			
			sb.append("UPDATE BK_DETAILS SET BL_NBR=:blNbr");
			sb.append(" WHERE bk_ref_nbr =:bkref");
			sql = sb.toString();
			log.info("sql:" + sql);
			for (int i = 0; i < arrSize; i++) {
				blNbr = (String) criteria.getPredicates().get("blNbr" + i);
				brNo = (String) criteria.getPredicates().get("brNo" + i);
				paramMap.put("blNbr", blNbr);
				paramMap.put("bkref", brNo);
				log.info("paramMap:" + paramMap.toString());
				result = namedParameterJdbcTemplate.update(sql, paramMap);
				log.info("result:" + result);
			}
		} catch (Exception e) {
			log.info("Exception updateBlNbr : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END updateBlNbr result" + result);
		}
	}

	@Override
	public BookingReferenceFileUploadDetails getCargoBkFileUploadDetails(String seq_id) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		BookingReferenceFileUploadDetails fileDetails = null;
		try {
			log.info("START getCargoBkFileUploadDetails :" + seq_id);
			Map<String, String> paramMap = new HashMap<String, String>();
			sb.append("SELECT ");
			sb.append(" VV_CD as vv_cd, ");
			sb.append(" ASSIGNED_FILE_NM as assigned_file_name, ");
			sb.append("	ACTUAL_FILE_NM as actual_file_name, ");
			sb.append(" PROCESSED_FILE_NM as output_file_name ");
			sb.append(" FROM gbms.BK_UPLOAD_DETAILS ");
			sb.append(" WHERE BK_UPLOAD_SEQ_NBR = :seq_id");
			paramMap.put("seq_id", seq_id);
			log.info("getCargoBkFileUploadDetails :" + "SQL:" + sb.toString() + ", paramap: "
					+ paramMap.toString());
			fileDetails = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap,
					new BeanPropertyRowMapper<BookingReferenceFileUploadDetails>(BookingReferenceFileUploadDetails.class));
			log.info("getCargoBkFileUploadDetails :" + fileDetails.toString());
		} catch (Exception e) {
			log.info("Exception getCargoBkFileUploadDetails : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getCargoBkFileUploadDetails ");
		}
		return fileDetails;
	}
	
	@Override
	public String getVarcode(Criteria criteria) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		SqlRowSet rs = null;
		String vvcd = "";
		try {
			log.info("START getVarcode criteria :" + criteria.toString());
			String vslNm = CommonUtil.deNull(criteria.getPredicates().get("vesselName"));
			String voyOut = CommonUtil.deNull(criteria.getPredicates().get("voyageNumber"));
			
			sb.append("SELECT VV_CD FROM TOPS.VESSEL_CALL WHERE ");
			sb.append("VSL_NM = :vslNm AND (IN_VOY_NBR = :voyOut ");
			sb.append("OR OUT_VOY_NBR = :voyOut)");
			paramMap.put("vslNm", vslNm);
			paramMap.put("voyOut", voyOut);
			String sql = sb.toString();
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			log.info("sql:" + sql);
			if(rs.next()) {
				vvcd = rs.getString("VV_CD");
			}
		} catch (Exception e) {
			log.info("Exception getVarcode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getVarcode result:" + vvcd);
		}
		return vvcd;
	}
	
	@Override
	public int bookingRefExist(String bk_nbr, String vvcd) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		int count = 0;
		try {
			log.info("START bookingRefExist for vvcd :" + vvcd + " , bk_nbr :" + bk_nbr);
			sb.append(
					"SELECT COUNT(BK_REF_NBR) FROM GBMS.BK_DETAILS bk WHERE VAR_NBR=:vvcd AND BK_REF_NBR = :bk_nbr AND BK_STATUS ='A'");
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("bk_nbr", bk_nbr);
			paramMap.put("vvcd", vvcd);
			log.info("bookingRefExist " + sb.toString() + ", paramap :" + paramMap.toString());
			count = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, Integer.class);
		} catch (Exception e) {
			log.info("Exception bookingRefExist : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END bookingRefExist for vvcd :" + vvcd + " result :" + count);
		}
		return count;
	}
	
	private Map<String, String> checkLegthValidation(String conAddr, String notifyParty,
			String notifyPartyAddr, String placeDel, String placeReceipt, String shipperNm, String shipperAdd, String blNbr) throws BusinessException {
		Map<String, String> mapErrorLength = new HashMap<String, String>();
		try {
			log.info("START: checkLegthValidation DAO conAddr:" + conAddr
					+ "notifyParty:" + notifyParty+ "notifyPartyAddr:" + notifyPartyAddr
					+ "placeDel:" + placeDel+ "placeReceipt:" + placeReceipt+ "shipperNm:" + shipperNm+ "shipperAdd:" + shipperAdd);
			
		
			if(!CommonUtil.deNull(notifyParty).isEmpty() && notifyParty.length() > 70) {
				mapErrorLength.put("Notify party", String.valueOf(notifyParty.length()));
			}
			if(!CommonUtil.deNull(placeDel).isEmpty() && placeDel.length() > 70) {
				mapErrorLength.put("Place of Delivery", String.valueOf(placeDel.length()));
			}
			if(!CommonUtil.deNull(placeReceipt).isEmpty() && placeReceipt.length() > 70) {
				mapErrorLength.put("Place of Receipt", String.valueOf(placeReceipt.length()));
			}
			if(!CommonUtil.deNull(shipperNm).isEmpty() && shipperNm.length() > 70) {
				mapErrorLength.put("Shipper Name", String.valueOf(shipperNm.length()));
			}		
			if(!CommonUtil.deNull(conAddr).isEmpty() && conAddr.length() > 500) {
				mapErrorLength.put("Consignee Address", String.valueOf(conAddr.length()));
			}
			if(!CommonUtil.deNull(shipperAdd).isEmpty() && shipperAdd.length() > 500) {
				mapErrorLength.put("Shipper Address", String.valueOf(shipperAdd.length()));
			}
			if(!CommonUtil.deNull(notifyPartyAddr).isEmpty() && notifyPartyAddr.length() > 500) {
				mapErrorLength.put("Notify Party Address", String.valueOf(notifyPartyAddr.length()));
			}
			if(!CommonUtil.deNull(blNbr).isEmpty() && blNbr.length() > 100) {
				mapErrorLength.put("Bill-of-Lading No.", String.valueOf(blNbr.length()));
			}
			
		} catch (Exception e) {
			log.info("Exception checkLegthValidation : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** checkLegthValidation  DAO  **** bb1no: " + mapErrorLength.toString());
		}
		return mapErrorLength;
	}
	
	@Override
	public String getTemplateVersionNo() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		String versionNo = null;
		log.info("START: getTemplateVersionNo  DAO  :");
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			sb.append(
					"SELECT MISC_TYPE_NM FROM TOPS.SYSTEM_CONFIG sc WHERE CAT_CD='BK_TEMPLATE_VERSION' AND MISC_TYPE_CD='BK_TEMPLATE' AND REC_STATUS='A'");
			versionNo = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, String.class);
			log.info("getTemplateVersionNo:" + versionNo);
		} catch (Exception e) {
			log.info("Exception getTemplateVersionNo : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getTemplateVersionNo ");
		}
		return versionNo;
	}
}
