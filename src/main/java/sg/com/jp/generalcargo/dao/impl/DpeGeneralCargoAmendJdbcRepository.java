package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.BookingRefRepository;
import sg.com.jp.generalcargo.dao.DpeGeneralCargoAmendRepository;
import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.domain.HSCode;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("DpeCargoAmendRepo")
public class DpeGeneralCargoAmendJdbcRepository implements DpeGeneralCargoAmendRepository {

	private static final Log log = LogFactory.getLog(DpeGeneralCargoAmendJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	BookingRefRepository bookRefRepo;
	@Autowired
	protected JdbcTemplate jdbcTemplate;

	// sg.com.jp.dpe.action -->DpeGeneralCargoAmendJdbcDao -->getDiscargingCargo()
	@Override
	public DPEUtil getDiscargingCargo(String asnNbr) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		DPEUtil discargingCargoList = new DPEUtil();
		String sql;
		try {
			log.info("START: getDiscargingCargo  DAO  Start Obj :" + asnNbr.toString());

			sb.append(
					"SELECT HS_SUB_CODE.HS_SUB_DESC,COMPANY_CODE.CO_NM,V.VSL_TYPE_CD, VC.MAX_CARGO_TON,VS.GB_CLOSE_BJ_IND, ");
			sb.append(
					"ED.BL_NBR, ED.MFT_SEQ_NBR,ED.VAR_NBR,VS.VSL_NM,NVL(ED.TRANS_DN_NBR_PKGS, 0) TRANS_DN_NBR_PKGS , ");
			sb.append(
					" NVL(ED.DN_NBR_PKGS, 0) DN_NBR_PKGS ,VS.IN_VOY_NBR,VS.VV_STATUS_IND,CC.CC_NAME, CC.CC_CD, VS.TERMINAL, ");
			sb.append(
					"CASE WHEN VS.COMBI_GC_OPS_IND='Y' THEN (VS.SCHEME ||'/' || NVL(VS.COMBI_GC_SCHEME,'')) ELSE VS.SCHEME END AS scheme ");
			sb.append("FROM GB_EDO ED ");
			sb.append("INNER JOIN VESSEL_CALL VS ");
			sb.append("ON VS.VV_CD = ED.VAR_NBR ");
			sb.append("INNER JOIN MANIFEST_DETAILS MD ");
			sb.append("ON MD.MFT_SEQ_NBR = ED.MFT_SEQ_NBR ");
			sb.append("LEFT JOIN CARGO_CATEGORY_CODE CC ");
			sb.append("ON ( MD.CARGO_CATEGORY_CD = CC.CC_CD ");
			sb.append("AND INSTR( ");
			sb.append("(SELECT VALUE FROM TEXT_PARA WHERE PARA_CD='CC_SET1'), ");
			sb.append("CC.CC_CD) >0 AND CC.CC_STATUS='A') ");
			sb.append("LEFT JOIN VESSEL_SCHEME VC ON VC.SCHEME_CD = VS.SCHEME ");
			sb.append("LEFT JOIN VESSEL V ON V.VSL_NM = VS.VSL_NM ");
			sb.append("LEFT JOIN COMPANY_CODE ON MD.MANIFEST_CREATE_CD = COMPANY_CODE.CO_CD ");
			sb.append("LEFT JOIN HS_SUB_CODE ON (HS_SUB_CODE.REC_STATUS = '1' AND MD.HS_CODE = HS_SUB_CODE.HS_CODE ");
			sb.append(
					"AND MD.HS_SUB_CODE_FR = HS_SUB_CODE.HS_SUB_CODE_FR AND MD.HS_SUB_CODE_TO = HS_SUB_CODE.HS_SUB_CODE_TO) ");
			sb.append("WHERE ED.EDO_ASN_NBR = :asn_nbr AND ED.EDO_STATUS = 'A' ");

			sql = sb.toString();
			paramMap.put("asn_nbr", asnNbr);
			log.info("getDiscargingCargo SQL : " + sb.toString() + "paramMap:" + paramMap.toString());

			try {
				
			discargingCargoList = namedParameterJdbcTemplate.queryForObject(sql, paramMap,
					new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			} catch (EmptyResultDataAccessException e) {
				discargingCargoList = null;
			}
			
			
			log.info("getDiscargingCargo Result : " + discargingCargoList.toString());
		} catch (Exception e) {
			log.info("Exception getDiscargingCargo : ", e);
			discargingCargoList = null;
		} finally {
			log.info("END: getDiscargingCargo  DAO  END");
		}
		return discargingCargoList;
	}

	// jp.src.sg.com.jp.dpe.dao -->DpeGeneralCargoAmendJdbcDao -->listAcount()
	public List<DPEUtil> listAcount(String vvCd) throws BusinessException {
		List<DPEUtil> dpe = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		Map<String, String> params = new HashMap<String, String>();
		try {
			log.info("START listAcount DAO"+" vvCd:"+vvCd );
			sb.append("SELECT acct_nbr, ");
			sb.append(" acct_nbr || '(' || ");
			sb.append(" cust_cd || ')'  as cust_cd ");
			sb.append(" FROM company_code, ");
			sb.append(" cust_acct, ");
			sb.append(" vessel_call ");
			sb.append(" WHERE cust_cd      = co_cd ");
			sb.append(" AND acct_status_cd = 'A' ");
			sb.append(" AND business_type LIKE '%G%' ");
			sb.append(" AND trial_ind = 'N' ");
			sb.append(" AND acct_nbr IS NOT NULL ");
			sb.append(" AND co_cd     = create_cust_cd ");
			sb.append(" AND vv_cd     = :vv_cd ");
			sb.append(" UNION ");
			sb.append(" SELECT DISTINCT ca.acct_nbr, ");
			sb.append(" ca.acct_nbr || '(' || ");
			sb.append(" ca.cust_cd || ')' as cust_cd ");
			sb.append(" FROM cust_acct ca, ");
			sb.append(" company_code cc ");
			sb.append(" WHERE ca.business_type LIKE '%G%' ");
			sb.append(" AND ca.acct_nbr     IS NOT NULL ");
			sb.append(" AND ca.acct_status_cd='A' ");
			sb.append(" AND ca.trial_ind     = 'N' ");
			sb.append(" AND ca.cust_cd       = cc.co_cd ");
			sb.append(" AND cc.rec_status    = 'A' ");
			sb.append(" AND cc.co_cd        IN ");
			sb.append(" (SELECT cust_cd FROM vessel_declarant WHERE vv_cd = :vv_cd ");
			sb.append("  )");
			params.put("vv_cd", vvCd);
			dpe = namedParameterJdbcTemplate.query(sb.toString(), params, new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			log.info("END: *** listAcount Result *****" + dpe.toString());
		} catch (Exception e) {
			log.info("Exception listAcount : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END listAcount DAO");
		}
		return dpe;
	}

	// jp.src.sg.com.jp.dpe.dao -->DpeGeneralCargoAmendJdbcDao-->getEsnVessel()
	public DPEUtil getEsnVessel(String asnNbr) throws BusinessException {
		DPEUtil dpe = new DPEUtil();
		StringBuilder sb = new StringBuilder();
		Map<String, String> params = new HashMap<String, String>();
		try {
			log.info("START getEsnVessel DAO : asnNbr " + asnNbr);
			sb.append("SELECT bk.gb_close_shp_ind, vs.vv_status_ind, v.VSL_TYPE_CD, ");
			sb.append("vs.vsl_nm, vs.in_voy_nbr, vs.out_voy_nbr, vs.vv_cd, ");
			sb.append("vs.terminal, ");
			sb.append(
					"CASE WHEN vs.combi_gc_ops_ind='Y' THEN (vs.scheme ||'/' || NVL(vs.combi_gc_scheme,'')) ELSE vs.scheme END AS scheme ");
			sb.append("FROM bk_details bk inner join vessel_call vs ");
			sb.append("on vs.vv_cd = bk.var_nbr ");
			sb.append("inner join esn on bk.bk_ref_nbr = esn.bk_ref_nbr ");
			sb.append("left join vessel v on v.vsl_nm = vs.vsl_nm ");
			sb.append("WHERE esn.esn_asn_nbr = :asn_nbr");
			params.put("asn_nbr", asnNbr);
			dpe = (DPEUtil) namedParameterJdbcTemplate.queryForObject(sb.toString(), params,
					new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			
			log.info("END: *** getEsnVessel Result *****" + dpe.toString());
		} catch (Exception e) {
			log.info("Exception getEsnVessel : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getEsnVessel DAO");
		}
		return dpe;
	}

	// jp.src.sg.com.jp.dpe.dao -->DpeGeneralCargoAmendJdbcDao-->getHsSubCodeDesc()
	public DPEUtil getHsSubCodeDesc(String hsCode, String hsSubCode) {
		DPEUtil dpe = new DPEUtil();
		StringBuilder sb = new StringBuilder();
		Map<String, String> params = new HashMap<String, String>();
		try {
			log.info("START getHsSubCodeDesc DAO : hsCode " + hsCode + ", hsSubCode " + hsSubCode);
			sb.append("SELECT  hs_sub_desc ");
			sb.append("FROM hs_sub_code ");
			sb.append(
					"where rec_status = '1' and hs_code = :hs_code and (hs_sub_code_fr || '-' || hs_sub_code_to) = :hsSubCode");
			params.put("hs_code", hsCode);
			params.put("hsSubCode", hsSubCode);
			dpe = (DPEUtil) namedParameterJdbcTemplate.queryForObject(sb.toString(), params,
					new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			log.info("END: *** getHsSubCodeDesc Result *****" + dpe.toString());
		} catch (Exception e) {
			dpe = null;
			log.info("Exception getHsSubCodeDesc : ", e);
		} finally {
			log.info("END getHsSubCodeDesc DAO");
		}
		return dpe;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeGeneralCargoAmendJdbcDao-->listCrgType()
	public List<DPEUtil> listCrgType(String vslType) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> params = new HashMap<String, String>();
		List<DPEUtil> listCrgType = new ArrayList<DPEUtil>();
		try {
			log.info("START listCrgType DAO"+" vslType:"+vslType);
			sb.append(" SELECT DISTINCT CRG_TYPE_CD, (CRG_TYPE_CD ||'-'||CRG_TYPE_NM) as CRG_TYPE_NM ");
			sb.append(" FROM CRG_TYPE WHERE CRG_TYPE_CD NOT IN ('00','01','02','03') AND REC_STATUS ='A'");
			listCrgType = namedParameterJdbcTemplate.query(sb.toString(), params, new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			if (!vslType.equals("CC")) {
				String cargoTypeNotShow = "";
				try {
					cargoTypeNotShow = bookRefRepo.getCargoTypeNotShow();
				} catch (Exception e) {
					log.info("Exception listCrgType getCargoTypeNotShow: ", e);
					throw new BusinessException("M4201");
				}
				for (int i = 0; i < listCrgType.size(); i++) {
					DPEUtil dpe = listCrgType.get(i);

					if (dpe.getCrg_type_cd().equals(cargoTypeNotShow)) {
						listCrgType.remove(i);
					}
				}
			}
			log.info("END: *** listCrgType Result *****" + listCrgType.size());
		} catch (Exception e) {
			log.info("Exception listCrgType : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END listCrgType DAO");
		}
		return listCrgType;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeGeneralCargoAmendJdbcDao-->getShipper()
	public List<DPEUtil> getShipper(String name, String shipperCode, Criteria criteria) throws BusinessException {
		StringBuilder sb = new StringBuilder();
		String sql = "";
		Map<String, String> params = new HashMap<String, String>();
		List<DPEUtil> res = new ArrayList<>();
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		try {
			log.info("START getShipper DAO" +" name:"+name +" criteria:"+criteria.toString());
			sb.append(" select b.CO_NM,b.co_cd,c.add_l1,a.TDB_CR_NBR,a.UEN_NBR from customer a , company_code b , ");
			sb.append(" cust_address c where a.cust_cd = b.co_cd and b.REC_STATUS = 'A' ");
			sb.append(" and a.cust_cd = c.cust_cd and ");
			if(!name.isEmpty()) {
			sb.append(" (LOWER(b.CO_NM) LIKE :shipperName OR LOWER(b.CO_NM) LIKE :decodeShipperName) ");
			} else {
				sb.append(" b.CO_CD = :shipperCode  ");
			}
			
			sb.append(" and (a.TDB_CR_NBR is not null or a.UEN_NBR is not null ) and c.post_ind = 'Y' ");
			params.put("shipperName", name.toLowerCase() + "%");
			params.put("decodeShipperName", CommonUtil.replaceHtmlSyntax(name.toLowerCase()));
			params.put("shipperCode", shipperCode);
			sql = sb.toString();
			if (criteria.isPaginated()) {
				tableData.setTotal(namedParameterJdbcTemplate.queryForObject("SELECT COUNT(*) FROM (" + sql + ")",
						params, Integer.class));
				log.info("filter.total=" + tableData.getTotal());
			}

			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());

			}
			res = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			for (DPEUtil object : res) {
				topsModel.put(object);
			}
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
			
			log.info("END: *** getShipper Result *****" + topsModel.getSize());
		} catch (Exception e) {
			log.info("Exception getShipper : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END getShipper DAO");
		}
		return res;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeGeneralCargoAmendJdbcDao-->getShipper()
	public List<DPEUtil> currentListCargoCategory(String cargoTypeCode, String companyCode) throws BusinessException {
		List<DPEUtil> dpeUtilList = new ArrayList<DPEUtil>();
		List<BookingReferenceValueObject> brvoList = new ArrayList<BookingReferenceValueObject>();
		try {
			log.info("START currentListCargoCategory DAO" +" cargoTypeCode:"+cargoTypeCode +" companyCode:"+companyCode );
			boolean showAllCargoCategory = bookRefRepo.isShowAllCargoCategoryCode(companyCode);
			String notShowCargoCategoryCode = bookRefRepo.getNotShowCargoCategoryCode();

			if (companyCode.equalsIgnoreCase("JP")) {
				brvoList = bookRefRepo.getBRVOList("AssignCargoCategory");
			} else {
				brvoList = bookRefRepo.getBRVOList("other");
			}

			String[] applicableCargoCategory = new String[] {};
			for (BookingReferenceValueObject brvo : brvoList) {
				if (brvo.getCargoType().equals(cargoTypeCode)) {
					applicableCargoCategory = brvo.getCargoCategory().split(",");
				}
			}

			for (int i = 0; i < applicableCargoCategory.length; i++) {
				String[] ccValue_ccCode = applicableCargoCategory[i].split("=");
				DPEUtil dpeUtil = new DPEUtil();
				if (!showAllCargoCategory) {
					if (ccValue_ccCode[1].equals(notShowCargoCategoryCode)) {
						continue;
					}
				}
				dpeUtil.setCc_cd(ccValue_ccCode[1]);
				dpeUtil.setCc_name(ccValue_ccCode[0]);
				dpeUtilList.add(dpeUtil);
			}
			log.info("END: *** currentListCargoCategory Result *****" + dpeUtilList.size());
		} catch (Exception e) {
			log.info("Exception currentListCargoCategory : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END currentListCargoCategory DAO");
		}
		return dpeUtilList;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeGeneralCargoAmendJdbcDao-->listHsCode()
	public List<HSCode> listHsCode(String status) throws BusinessException {
		List<HSCode> hsCodeList = new ArrayList<HSCode>();
		Map<String, String> params = new HashMap<String, String>();
		String sql = "";
		try {
			log.info("START listHsCode DAO" +" status:"+status);
			sql = " SELECT HS_CODE, HS_DESC FROM GBMS.HS_CODE WHERE REC_STATUS =:status ORDER BY HS_CODE ";
			params.put("status", status);
			hsCodeList = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<HSCode>(HSCode.class));
			
			log.info("END: *** listHsCode Result *****" + hsCodeList.size());

		} catch (Exception e) {
			log.info("Exception listHsCode : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END listHsCode DAO");
		}
		return hsCodeList;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeGeneralCargoAmendJdbcDao-->listHsSubCode()
	public List<DPEUtil> listHsSubCode(String status, String hsCode) throws BusinessException {
		List<DPEUtil> hsCodeList = new ArrayList<DPEUtil>();
		Map<String, String> params = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		SqlRowSet rs = null; 
		
		try {
			log.info("START listHsSubCode DAO" +" status:"+status +" hsCode:"+hsCode);
			sb.append(" SELECT (HS_SUB_CODE_FR || '-' || HS_SUB_CODE_TO) hsSubCode, ");
			sb.append(" hs_sub_desc hsSubCodeDes, hs_sub_code_fr, hs_sub_code_to ");
			sb.append(" FROM hs_sub_code WHERE hs_code  =:hs_code AND REC_STATUS =:status ");
			params.put("status", status);
			params.put("hs_code", hsCode);
			log.info(" ***listHsSubCode SQL *****" + sb.toString());
			log.info(" ***loadHSSubCode paramMap *****" + params.toString());
			//Commented for Steel Billets NOM2 CR -- starts
			//hsCodeList = namedParameterJdbcTemplate.query(sb.toString(), params,
					//new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			//Commented for Steel Billets NOM2 CR -- ends
			//Added for Steel Billets NOM2 CR -- starts
			rs = namedParameterJdbcTemplate.queryForRowSet(sb.toString(), params);
			while (rs.next()) 
			{
				DPEUtil hs = new DPEUtil();
				hs.setHsSubCode(CommonUtility.deNull(rs.getString("HSSUBCODE")));
				hs.setHs_sub_desc(CommonUtility.deNull(rs.getString("HSSUBCODEDES")));
				hs.setHs_sub_code_fr(CommonUtility.deNull(rs.getString("HS_SUB_CODE_FR")));
				hs.setHs_sub_code_to(CommonUtility.deNull(rs.getString("HS_SUB_CODE_TO")));				
				hsCodeList.add(hs);
			}				
			String filterValue =  getHSCodesFiler();
			log.info("filterValue in loadHSSubCode method " + filterValue);
			if(filterValue.equalsIgnoreCase("YES"))
			{
				log.info("the filter value is YES, apply filtering of HS code/subcodes");			
				List<String> filteringCodes = getHSCodesFilerValues();
				log.info("filtering Code List :"+ filteringCodes);				
				Set<String> codesToRemove = filteringCodes.stream().flatMap(s -> Arrays.stream(s.split(","))).map(s->s.substring(3).trim()).collect(Collectors.toSet());
				log.info("Filtering Codes To Remove-"+ codesToRemove.toString());
				log.info("hsCodeList list retrived from DB --"+ hsCodeList.toString());
				hsCodeList.removeIf(hs -> codesToRemove.contains(hs.getHsSubCode()));
				log.info("END: *** loadHSSubCode Result After Filtering *****" + hsCodeList.toString());										
			}
			else if (filterValue.equalsIgnoreCase("NO"))
			{
				log.info("the filter value is NO, do not apply filter and execute existing flow");			
		    }
			else
			{
				log.info("there is no value specific defined for Filter - do Nothing");
			}
			log.info("END: *** listHsSubCode Result *****" + hsCodeList.size());
			//Added for Steel Billets NOM2 CR -- ends			
		} 
		catch (Exception e) 
		{
			log.info("Exception listHsSubCode : ", e);
			throw new BusinessException("M4201");
		} 
		finally 
		{
			log.info("END listHsSubCode DAO");
		}
		return hsCodeList;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeGeneralCargoAmendJdbcDao-->listAuthorizedParty()
	@Override
	public List<DPEUtil> listAuthorizedParty(String name, String vvCd, Criteria criteria) throws BusinessException {
		List<DPEUtil> authPartyList = new ArrayList<DPEUtil>();
		Map<String, String> params = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		String sql = "";
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		try {
			log.info("START listAuthorizedParty DAO" +" name:"+name +" vvCd:"+vvCd+" criteria:"+criteria.toString());
			sb.append(" SELECT DISTINCT cc.co_cd, cc.co_nm FROM company_code cc ");
			sb.append(" WHERE ( cc.co_cd IN (SELECT vc.create_cust_cd FROM vessel_call vc WHERE vc.vv_cd=:vv_cd ) ");
			sb.append(" OR cc.co_cd IN (SELECT vd.cust_cd FROM vessel_declarant vd WHERE vd.vv_cd=:vv_cd ");
			sb.append(" AND vd.status ='A')) AND LOWER(cc.co_nm) LIKE :co_nm ");
			params.put("vv_cd", vvCd);
			params.put("co_nm", name.toLowerCase() + "%");
			sql = sb.toString();
			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());

			}
			authPartyList = namedParameterJdbcTemplate.query(sb.toString(), params, new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			for (DPEUtil object : authPartyList) {
				topsModel.put(object);
			}
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
			log.info("END: *** listAuthorizedParty Result *****" + authPartyList.size());
		} catch (Exception e) {
			log.info("Exception listAuthorizedParty : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END listAuthorizedParty DAO");
		}
		return authPartyList;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeGeneralCargoAmendJdbcDao-->listPort()
	@Override
	public List<DPEUtil> listPort(String name, Criteria criteria) throws BusinessException {
		List<DPEUtil> portList = new ArrayList<DPEUtil>();
		Map<String, String> params = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		String sql = "";
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		try {
			log.info("START listPort DAO" +" name:"+name +" criteria:"+criteria.toString());
			sb.append(" SELECT PORT_CD, (PORT_CD ||'-'|| PORT_NM) AS PORT_NM FROM UN_PORT_CODE ");
			sb.append(" WHERE LOWER(PORT_NM) LIKE :port_nm ");
			params.put("port_nm", name.toLowerCase() + "%");
			sql = sb.toString();
			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());

			}
			portList = namedParameterJdbcTemplate.query(sb.toString(), params,
					new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			for (DPEUtil object : portList) {
				topsModel.put(object);
			}
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
			log.info("END: *** listPort Result *****" + portList.size());
		} catch (Exception e) {
			log.info("Exception listPort : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END listPort DAO");
		}
		return portList;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeGeneralCargoAmendJdbcDao-->listPackaging()
	@Override
	public List<DPEUtil> listPackaging(String name, Criteria criteria) throws BusinessException {
		List<DPEUtil> packagingList = new ArrayList<DPEUtil>();
		Map<String, String> params = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		String sql = "";
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		try {
			log.info("START listPackaging DAO"+" name:"+name +" criteria:"+criteria.toString() );
			sb.append(" SELECT PKG_TYPE_CD, (PKG_TYPE_CD ||'-'|| PKG_DESC) AS PKG_DESC  FROM PKG_TYPES ");
			sb.append(" WHERE LOWER(PKG_DESC) LIKE :pkg_types AND REC_STATUS='A' ");
			params.put("pkg_types", name.toLowerCase() + "%");
			sql = sb.toString();
			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());

			}
			packagingList = namedParameterJdbcTemplate.query(sb.toString(), params,
					new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			for (DPEUtil object : packagingList) {
				topsModel.put(object);
			}
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
			log.info("END: *** packagingList Result *****" + packagingList.size());
		} catch (Exception e) {
			log.info("Exception packagingList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END listPackaging DAO");
		}
		return packagingList;
	}

	// jp.src.sg.com.jp.dpe.dao--->DpeGeneralCargoAmendJdbcDao-->listCompany()
	public List<DPEUtil> listCompany(String name, Criteria criteria) throws BusinessException {
		List<DPEUtil> companyList = new ArrayList<DPEUtil>();
		Map<String, String> params = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		String sql = "";
		TableResult tableResult = new TableResult();
		TableData tableData = new TableData();
		TopsModel topsModel = new TopsModel();
		try {
			log.info("START listCompany DAO"+" name:"+name +" criteria:"+criteria.toString());
			sb.append(" SELECT co_cd, co_nm || ' (' || co_cd || ')' as co_nm ");
			sb.append(" FROM company_code WHERE LOWER(co_nm) LIKE :co_nm ");
			sb.append(" AND rec_status='A' AND allow_jponline='Y' ");
			params.put("co_nm", name.toLowerCase() + "%");
			sql = sb.toString();
			if (criteria.isPaginated()) {
				sql = CommonUtil.getPaginatedSql(sql, criteria.getStart(), criteria.getLimit());

			}
			companyList = namedParameterJdbcTemplate.query(sb.toString(), params,
					new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			for (DPEUtil object : companyList) {
				topsModel.put(object);
			}
			tableData.setListData(topsModel);
			tableResult.setData(tableData);
			tableResult.setSuccess(true);
			log.info("END: *** listCompany Result *****" + companyList.size());
		} catch (Exception e) {
			log.info("Exception listCompany : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END listCompany DAO");
		}
		return companyList;
	}

	// jp.src.sg.com.jp.dpe.dao
	// -->DpeGeneralCargoAmendJdbcDao-->getShipperInformation()
	public DPEUtil getShipperInformation(String name) {
		DPEUtil dpe = new DPEUtil();
		StringBuilder sb = new StringBuilder();
		Map<String, String> params = new HashMap<String, String>();
		try {
			log.info("START getShipperInformation DAO : name " + name);
			sb.append(" select b.CO_NM,b.co_cd,c.add_l1,a.TDB_CR_NBR,a.UEN_NBR from customer a , ");
			sb.append(" company_code b , cust_address c where a.cust_cd = b.co_cd and b.REC_STATUS = 'A'");
			sb.append("and a.cust_cd = c.cust_cd and LOWER(b.co_cd) = :shipperName and c.post_ind = 'Y' ");
			params.put("shipperName", name.toLowerCase());
			try {
				dpe=namedParameterJdbcTemplate.queryForObject(sb.toString(), params,
						new BeanPropertyRowMapper<DPEUtil>(DPEUtil.class));
			} catch (EmptyResultDataAccessException e) {
				return null;
			}
			log.info("END: *** getShipperInformation Result *****" + dpe.toString());
		} catch (Exception e) {
			log.info("Exception getShipperInformation : ", e);
			return null;
		} finally {
			log.info("END getShipperInformation DAO");
		}
		return dpe;
	}

	@Override
	public int updateBkDetails(String bk_ref_nbr, String shipperNbr, String shipperAddress, String shipperNm,
			String strUserID,String conNm, String conAddr, String shipperAddr, String notifyParty,
			String notifyPartyAddr, String placeofDelivery, String placeofReceipt, String blNbr) {
		StringBuilder sb = new StringBuilder();
		Map<String, String> params = new HashMap<String, String>();
		try {
			log.info("START updateBkDetails DAO"+" bk_ref_nbr:"+bk_ref_nbr +" shipperNbr:"+shipperNbr +" shipperAddress:"+shipperAddress
					+" shipperNm:"+shipperNm +" strUserID:"+strUserID + " conNm:" + CommonUtility.deNull(conNm)+ " conAddr:" + CommonUtility.deNull(conAddr)+ " notifyParty:" + CommonUtility.deNull(notifyParty)
					+ " notifyPartyAddr:" + CommonUtility.deNull(notifyPartyAddr)+ " placeofDelivery:" + CommonUtility.deNull(placeofDelivery)+ " placeofReceipt:" + CommonUtility.deNull(placeofReceipt)
					+ " blNbr:" + CommonUtility.deNull(blNbr));
			sb.append(" update bk_details set ");
			if(shipperNbr != null){
				sb.append(" SHIPPER_CR_NBR= :shipperNbr, SHIPPER_NM= :shipperNm,");
			}
			sb.append(" SHIPPER_ADDR = :shipperAddr,  LAST_MODIFY_DTTM = SYSDATE, ");
			//START CR FTZ - NS NOV 2024
			sb.append(" CONS_NM=:conNm, CONSIGNEE_ADDR=:conAddr, NOTIFY_PARTY=:notifyParty ");
			sb.append(", NOTIFY_PARTY_ADDR=:notifyPartyAddr, PLACE_OF_DELIVERY=:placeofDelivery ");
			sb.append(", PLACE_OF_RECEIPT=:placeofReceipt ,BL_NBR=:blNbr, ");
			//END CR FTZ - NS NOV 2024
			sb.append(" LAST_MODIFY_USER_ID = :userId where BK_REF_NBR = :bk_ref_nbr ");
			
			
			params.put("bk_ref_nbr", bk_ref_nbr);
			params.put("shipperNbr", shipperNbr);
			params.put("shipperAddress", shipperAddr);
			params.put("shipperNm", shipperNm);
			params.put("userId", strUserID);
			// START CR FTZ NS NOV 2024
			params.put("shipperAddr", shipperAddr);
			params.put("conNm", conNm);
			params.put("conAddr", conAddr);
			params.put("notifyParty", notifyParty);
			params.put("notifyPartyAddr", notifyPartyAddr);
			params.put("placeofDelivery", placeofDelivery);
			params.put("placeofReceipt", placeofReceipt);
			params.put("blNbr", blNbr);
			// START CR FTZ NS NOV 2024
			int result = namedParameterJdbcTemplate.update(sb.toString(), params);
			log.info("END: *** updateBkDetails Result *****" + result);
			return result;
		} catch (Exception e) {
			log.info("Exception updateBkDetails : ", e);
			return 0;
		} finally {
			log.info("END updateBkDetails DAO");
		}
	}
	
	//Added for Steel Billets NOM2 CR -- starts
		public String getHSCodesFiler() throws BusinessException {
			StringBuffer sb = new StringBuffer();
			String filterValue = null;
			log.info("START: getHSCodesFiler  DAO  :");
			Map<String, String> paramMap = new HashMap<String, String>();
			try 
			{
				sb.append(
						"SELECT MISC_TYPE_NM FROM TOPS.SYSTEM_CONFIG sc WHERE CAT_CD='HS_SUBCODES_FILTER' AND MISC_TYPE_CD='HS_SUBCODES_FILTER' AND REC_STATUS='A'");
				filterValue = namedParameterJdbcTemplate.queryForObject(sb.toString(), paramMap, String.class);
				log.info("filterValue in getHSCodesFiler method :" + filterValue);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				log.info("Exception in getHSCodesFiler : ", e);
				throw new BusinessException("M4201");
			} 
			finally 
			{
				log.info("END: getHSCodesFiler ");
			}
			return filterValue;
		}
		
		public List<String> getHSCodesFilerValues() throws BusinessException {
			StringBuffer sb = new StringBuffer();
			
			log.info("START: getHSCodesFiler  DAO  :");
			Map<String, String> paramMap = new HashMap<String, String>();
			List<String> filterValues = null;
			try 
			{
				sb.append(
						"SELECT MISC_TYPE_NM FROM TOPS.SYSTEM_CONFIG sc WHERE CAT_CD='HS_SUBCODES_FILTER' AND MISC_TYPE_CD='SUBCODES_VALUES' AND REC_STATUS='A'");
				//List<String> filterValues = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, (String.class));
				filterValues = namedParameterJdbcTemplate.queryForList(sb.toString(), paramMap, (String.class));
				log.info("getHSCodesFilerValues() size:" + filterValues.size());
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				log.info("Exception in getHSCodesFiler : ", e);
				throw new BusinessException("M4201");
			} 
			finally 
			{
				log.info("END: getHSCodesFiler ");
			}
			return filterValues;
		}
		//Added for Steel Billets NOM2 CR -- ends
}
