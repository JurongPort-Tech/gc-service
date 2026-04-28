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

import sg.com.jp.generalcargo.dao.CutoffCargoRepository;
import sg.com.jp.generalcargo.domain.CutoffValueObject;
import sg.com.jp.generalcargo.domain.CuttoffEdoValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;

@Repository("cutoffCargoRepository")
public class CutoffCargoJdbcRepository implements CutoffCargoRepository{

	private static final Log log = LogFactory.getLog(CutoffCargoJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	//	ejb.sessionBeans.gbms.cargo.cutoff -->CutoffCargo 

	// ejb.sessionBeans.gbms.cargo.edo--->EdoEjb-->getBLDetails()
	@Override
	public List<CuttoffEdoValueObject> getBLDetails(String mftSeqNbr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = "";
		StringBuilder sb = new StringBuilder();

		List<CuttoffEdoValueObject> bldetailsList = new ArrayList<CuttoffEdoValueObject>();
		try {
			log.info("START:  *** getBLDetails Dao Start : *** " + mftSeqNbr);
			
			// ++ 19.10.2009 FPT added for GB CR
			// 16.01.02
			/*
			 * sql="SELECT B.CRG_TYPE_CD, B.CRG_TYPE_NM, A.HS_CODE, A.CRG_DES, "
			 * +"A.CRG_STATUS, A.DG_IND, A.STG_TYPE, A.DIS_TYPE, C.PKG_TYPE_CD, "
			 * +"C.PKG_DESC, A.NBR_PKGS-(A.EDO_NBR_PKGS+NVl(A.CUT_OFF_NBR_PKGS,0)) AS NBR_PKG, M.MFT_MARKINGS,"
			 * +"A.CONS_NM FROM MANIFEST_DETAILS A, MFT_MARKINGS M,  "
			 * +"CRG_TYPE B, PKG_TYPES C WHERE A.CRG_TYPE=B.CRG_TYPE_CD AND "
			 * +"A.PKG_TYPE=C.PKG_TYPE_CD AND A.MFT_SEQ_NBR=M.MFT_SQ_NBR AND "
			 * +"A.MFT_SEQ_NBR='"+strMftSeqNbr+"'";
			 */

			sb.append("SELECT x.CRG_TYPE_CD, x.CRG_TYPE_NM, x.HS_CODE, x.CRG_DES, ");
			sb.append("x.CRG_STATUS, x.DG_IND, x.STG_TYPE, x.DIS_TYPE, x.PKG_TYPE_CD,");
			sb.append("x.PKG_DESC,x.NBR_PKG, x.MFT_MARKINGS, ");
			sb.append("x.CONS_NM,(x.gross_wt - nvl(y.sum1,0)) wt,(x.gross_vol - nvl(y.sum2,0)) vol, EPC_IND FROM ");
			sb.append("(SELECT  A.MFT_SEQ_NBR, B.CRG_TYPE_CD, B.CRG_TYPE_NM, A.HS_CODE, A.CRG_DES, ");
			sb.append("A.CRG_STATUS, A.DG_IND, A.STG_TYPE, A.DIS_TYPE, C.PKG_TYPE_CD,");
			sb.append("C.PKG_DESC,A.NBR_PKGS-(A.EDO_NBR_PKGS+NVl(A.CUT_OFF_NBR_PKGS,0)) AS NBR_PKG, M.MFT_MARKINGS, ");
			sb.append("A.CONS_NM, A.GROSS_WT,A.GROSS_VOL, A.EPC_IND ");
			sb.append("FROM MANIFEST_DETAILS A, MFT_MARKINGS M, ");
			sb.append("CRG_TYPE B, PKG_TYPES C ");
			sb.append("WHERE    A.CRG_TYPE=B.CRG_TYPE_CD AND ");
			sb.append("A.PKG_TYPE=C.PKG_TYPE_CD ");
			sb.append("AND A.MFT_SEQ_NBR=M.MFT_SQ_NBR ");
			sb.append(") x LEFT OUTER JOIN  ");
			sb.append(
					"(SELECT edo.mft_seq_nbr, SUM(edo.nom_wt) sum1, sum(edo.nom_vol) sum2 FROM GB_EDO edo WHERE EDO_STATUS = 'A' GROUP BY edo.mft_seq_nbr) y ");
			sb.append("ON x.mft_seq_nbr = y.mft_seq_nbr ");
			sb.append("WHERE x.MFT_SEQ_NBR = :mftSeqNbr");
			sql = sb.toString();

			// log.info("inside bean try");
			// log.info("inside bean con");
			// log.info("inside bean sta");
			log.info("END: *** getBLDetails SQL *****" + sql);
			paramMap.put("mftSeqNbr", mftSeqNbr);
			log.info(" *** getBLDetails params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			CuttoffEdoValueObject edoValueObject = new CuttoffEdoValueObject();
			while (rs.next()) {
				String crg_type_cd = CommonUtility.deNull(rs.getString("CRG_TYPE_CD"));
				String crg_type_nm = CommonUtility.deNull(rs.getString("CRG_TYPE_NM"));
				String hs_code = CommonUtility.deNull(rs.getString("HS_CODE"));
				String crg_des = CommonUtility.deNull(rs.getString("CRG_DES"));
				String mft_markings = CommonUtility.deNull(rs.getString("MFT_MARKINGS"));
				String cons_nm = CommonUtility.deNull(rs.getString("CONS_NM"));
				String crg_status = CommonUtility.deNull(rs.getString("CRG_STATUS"));
				String dg_ind = CommonUtility.deNull(rs.getString("DG_IND"));
				String stg_type = CommonUtility.deNull(rs.getString("STG_TYPE"));
				String dis_type = CommonUtility.deNull(rs.getString("DIS_TYPE"));
				String pkg_type_cd = CommonUtility.deNull(rs.getString("PKG_TYPE_CD"));
				String pkg_type_desc = CommonUtility.deNull(rs.getString("PKG_DESC"));
				String nbr_pkgs = CommonUtility.deNull(rs.getString("NBR_PKG"));
				String wt = CommonUtility.deNull(rs.getString("wt"));// Added by Thanhnv2
				String vol = CommonUtility.deNull(rs.getString("vol"));// Added by Thanhnv2
				log.info("===============EDO.BLDetail: " + wt + " - " + vol);
				String deliveryToEPC = CommonUtility.deNull(rs.getString("EPC_IND"));
				edoValueObject.setCrgTypeCd(crg_type_cd);
				edoValueObject.setCrgTypeNm(crg_type_nm);
				edoValueObject.setHsCode(hs_code);
				edoValueObject.setCrgDes(crg_des);
				edoValueObject.setMftMarkings(mft_markings);
				edoValueObject.setCrgStatus(crg_status);
				edoValueObject.setDgInd(dg_ind);
				edoValueObject.setStgType(stg_type);
				edoValueObject.setDisType(dis_type);
				edoValueObject.setPkgTypeCd(pkg_type_cd);
				edoValueObject.setPkgTypeDesc(pkg_type_desc);
				edoValueObject.setNbrPkgs(nbr_pkgs);
				edoValueObject.setCrgStatus(crg_status);
				edoValueObject.setConsNm(cons_nm);
				edoValueObject.setNomWeight(wt);// Added by Thanhnv2 for GB CR
				edoValueObject.setNomVolume(vol);// Added by Thanhnv2 for GB CR
				edoValueObject.setDeliveryToEPC(deliveryToEPC); // MCC get EPC_IND
			}
			List<String> containerList = new ArrayList<String>();

			containerList = getContinerDetailsList(mftSeqNbr, "");
			edoValueObject.setContinerDetails(containerList);
			bldetailsList.add(edoValueObject);

			log.info("END: *** getBLDetails Result *****" + bldetailsList.toString());
	
		} catch (NullPointerException e) {
			log.info("Exception getBLDetails : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception getBLDetails : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getBLDetails : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getBLDetails  END *****");
		}
		return bldetailsList;
	}

	// ejb.sessionBeans.gbms.containerised.dn-->dnBean-->getContinerDetailsList
	public List<String> getContinerDetailsList(String mftSeqNbr, String edoAsnNbr) throws BusinessException {
		String sql = "";
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<String> containervector = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();

		try {
			log.info("START:  *** getContinerDetailsList Dao Start : *** " + mftSeqNbr + edoAsnNbr);
			//
			if (mftSeqNbr.equalsIgnoreCase("")) {
				sb.append("SELECT UNIQUE A.CNTR_TYPE,A.CNTR_SIZE,B.CNTR_NBR, B.CNTR_BL_SEQ ");
				sb.append(" FROM MANIFEST_DETAILS A, BL_CNTR_DETAILS B, GB_EDO E WHERE ");
				sb.append("A.MFT_SEQ_NBR=B.MFT_SEQ_NBR AND A.MFT_SEQ_NBR=E.MFT_SEQ_NBR ");
				sb.append("AND A.CRG_TYPE IN ('02','03') AND EDO_ASN_NBR=:edoAsnNbr ");
				sb.append("ORDER BY B.CNTR_BL_SEQ");

			} else {
				sb.append("SELECT A.CNTR_TYPE,A.CNTR_SIZE,B.CNTR_NBR, B.CNTR_BL_SEQ FROM ");
				sb.append("MANIFEST_DETAILS A, BL_CNTR_DETAILS B WHERE ");
				sb.append("A.MFT_SEQ_NBR=B.MFT_SEQ_NBR AND A.CRG_TYPE IN ('02','03') AND A.MFT_SEQ_NBR=:mftSeqNbr ");
				sb.append("  ORDER BY B.CNTR_BL_SEQ");
			}
			sql = sb.toString();
			log.info("END: *** getContinerDetailsList SQL *****" + sql);

			
			if (mftSeqNbr.equalsIgnoreCase(""))
				paramMap.put("edoAsnNbr", edoAsnNbr);
			else
				paramMap.put("mftSeqNbr", mftSeqNbr);
			
			log.info(" *** getContinerDetailsList params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			while (rs.next()) {
				containervector.add(CommonUtility.deNull(rs.getString(1)));
				containervector.add(CommonUtility.deNull(rs.getString(2)));
				containervector.add(CommonUtility.deNull(rs.getString(3)));
			}
			log.info("END: *** getContinerDetailsList Result *****" + containervector.toString());

	
		} catch (NullPointerException e) {
			log.info("Exception getContinerDetailsList : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getContinerDetailsList : " , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: *** getContinerDetailsList  END *****");
		}

		return containervector;
	}

	@Override
	public List<CutoffValueObject> viewEdoDetails(String blno, String varnbr, String edoasnno, int cutoffno) throws BusinessException {

		SqlRowSet rs1 = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		List<CutoffValueObject> edoviewlistvector= new ArrayList<CutoffValueObject>();
		String sql = "";
		try {
			log.info("START: viewEdoDetails  DAO  Start Obj: "+" blno:"+blno+" varnbr:"+varnbr+" edoasnno:"+edoasnno+" cutoffno:"+cutoffno );
			

			if(edoasnno.trim().length() != 0) {
				sb.append( "select edo.edo_asn_nbr as Edo_Asn_Nbr, edo.bl_nbr as Bl_Nbr,");
				sb.append(" mft.CRG_DES as Crg_Desc, mft.HS_CODE as Hs_Code,");
				sb.append(" mft.pkg_type || ' -- ' || (Select pkg_desc from pkg_types where");
				sb.append(" pkg_type_cd = mft.pkg_type) as Pkg_Type,");
				sb.append(" cof.cut_off_nbr as Cut_off_Qty, cof.cut_off_nbr, cof.cut_off_type, ");
				sb.append(" mft.CONS_NM as Consignee,");
				sb.append(" (Select mm.mft_markings from mft_markings mm where");
				sb.append(" mm.mft_sq_nbr = cof.MFT_SEQ_NBR) as Crg_Markings,");
				sb.append(" (Select crg.crg_type_nm from crg_type crg where crg.crg_type_cd");
				sb.append(" = mft.crg_type) as Crg_Type, edo.MFT_SEQ_NBR as Mft_Seq_Nbr from gb_edo edo, Manifest_details mft,");
				sb.append(" Cut_off_details cof where edo.BL_NBR = Mft.BL_NBR and edo.var_nbr = Mft.VAR_NBR and");
				sb.append(" edo.var_nbr = cof.VAR_NBR and cof.EDO_ASN_NBR = edo.EDO_ASN_NBR and");
				sb.append(" edo.Bl_nbr = :blno and  edo.var_nbr = :varnbr and");
				sb.append(" edo.edo_asn_nbr = :edoasnno and cof.cut_off_nbr = :cutoffno ");
				sql =sb.toString();
				
				paramMap.put("cutoffno", cutoffno);
				paramMap.put("edoasnno", edoasnno);
				paramMap.put("blno", blno);
				paramMap.put("varnbr", varnbr);
			}
			else
			{
				sb.append("select '-NA-', mft.bl_nbr, mft.CRG_DES as Crg_Desc, mft.HS_CODE as Hs_Code,");
				sb.append(" mft.pkg_type || ' -- ' || (Select pkg_desc from pkg_types where");
				sb.append(" pkg_type_cd = mft.pkg_type) as Pkg_Type, cof.cut_off_nbr_pkgs, cof.cut_off_nbr, cof.cut_off_type, ");
				sb.append(" mft.CONS_NM as Consignee, (Select mm.mft_markings from mft_markings mm where");
				sb.append(" mm.mft_sq_nbr = cof.MFT_SEQ_NBR) as Crg_Markings,");
				sb.append(" (Select crg.crg_type_nm from crg_type crg where crg.crg_type_cd = mft.crg_type) as Crg_Type," );
				sb.append(" mft.MFT_SEQ_NBR as Mft_Seq_Nbr" );
				sb.append(" from Manifest_details mft, Cut_off_details cof where cof.cut_off_nbr = :cutoffno and");
				sb.append(" cof.MFT_SEQ_NBR = mft.mft_seq_nbr order by cof.cut_off_nbr asc");
				sql = sb.toString();
				
				paramMap.put("cutoffno", cutoffno);
			}

			
			log.info(" *** viewEdoDetails SQL *****" + sql);
			log.info(" *** viewEdoDetails params *****" + paramMap.toString());

			rs1 = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			CutoffValueObject cutoffValueObject = new CutoffValueObject();
			while(rs1.next()) {
				String edoasnnbr=CommonUtility.deNull(rs1.getString(1));
				String blnbr=CommonUtility.deNull(rs1.getString(2));
				String crgdesc=CommonUtility.deNull(rs1.getString(3));
				String hscode=CommonUtility.deNull(rs1.getString(4));
				String pkgtype=CommonUtility.deNull(rs1.getString(5));
				String cutoffqty=CommonUtility.deNull(rs1.getString(6));
				String cutoffnbr=CommonUtility.deNull(rs1.getString(7));
				String cutofftype=CommonUtility.deNull(rs1.getString(8));
				String consignee=CommonUtility.deNull(rs1.getString(9));
				String crgmarking=CommonUtility.deNull(rs1.getString(10));
				String crgtype=CommonUtility.deNull(rs1.getString(11));
				//SL-GBMS-20050214-1 : Change by Liu Foong on 14 Feb 2005: To retrive mft_srq_nbr from resultSet
				String mftseqnbr=CommonUtility.deNull(rs1.getString(12));
				//End Change

				cutoffValueObject.setCutoffNbr(cutoffnbr);
				cutoffValueObject.setEdoAsnNbr(edoasnnbr);
				cutoffValueObject.setBlNbr(blnbr);
				cutoffValueObject.setCargoDesc(crgdesc);
				cutoffValueObject.setHsCode(hscode);
				cutoffValueObject.setPkgType(pkgtype);
				cutoffValueObject.setCutoffQty(cutoffqty);
				cutoffValueObject.setConsignee(consignee);
				cutoffValueObject.setCargoMarking(crgmarking);
				cutoffValueObject.setCargoType(crgtype);
				//SL-GBMS-20050214-1 : Change by Liu Foong on 14 Feb 2005: To include mft_srq_nbr into the valueObject
				cutoffValueObject.setMftSeqNbr(mftseqnbr);				
				//End Change
				cutoffValueObject.setCutoffType(cutofftype);

				edoviewlistvector.add(cutoffValueObject);
			}

			log.info("END: *** viewEdoDetails Result *****" + edoviewlistvector.toString());
	
		} catch (NullPointerException e) {
			log.info("Exception viewEdoDetails : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception viewEdoDetails : " , e);
			throw new BusinessException("M4201");
		}finally{
			log.info("END: viewEdoDetails  DAO  END");
		}

		return edoviewlistvector;
	}

	@Override
	public List<CuttoffEdoValueObject> getEdoBLDetails(String edonumber) throws BusinessException {

		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		List<CuttoffEdoValueObject> bldetailsvector=new ArrayList<CuttoffEdoValueObject>();

		try {
			log.info("START: getEdoBLDetails  DAO  Start Obj: "+" edonumber:"+edonumber );
			
			sb.append("SELECT d.CUT_OFF_NBR_PKGS,d.CRG_STATUS,B.CRG_TYPE_CD, B.CRG_TYPE_NM, A.HS_CODE, A.CRG_DES, ");
			sb.append( "A.CRG_STATUS, A.DG_IND, A.STG_TYPE, A.DIS_TYPE, C.PKG_TYPE_CD, ");
			sb.append("C.PKG_DESC, d.NBR_PKGS-NVl(d.TRANS_DN_NBR_PKGS,0)-NVl(d.CUT_OFF_NBR_PKGS,0)-nvl(d.release_nbr_pkgs,0)-NVl(d.DN_NBR_PKGS,0)-nvl(NBR_PKGS_IN_PORT,0) AS NBR_PKG_T, M.MFT_MARKINGS, ");
			sb.append("A.CONS_NM,d.NBR_PKGS-NVl(d.DN_NBR_PKGS,0)-NVl(d.CUT_OFF_NBR_PKGS,0)-NVl(d.TRANS_DN_NBR_PKGS,0)-nvl(NBR_PKGS_IN_PORT,0) AS NBR_PKG_L FROM MANIFEST_DETAILS A, MFT_MARKINGS M, ");
			sb.append("CRG_TYPE B, PKG_TYPES C,gb_edo d WHERE A.CRG_TYPE=B.CRG_TYPE_CD AND ");
			sb.append("A.PKG_TYPE=C.PKG_TYPE_CD AND A.MFT_SEQ_NBR=M.MFT_SQ_NBR AND A.MFT_SEQ_NBR = d.MFT_SEQ_NBR ");
			sb.append("and d.EDO_ASN_NBR = :edonumber ");

			String sql=sb.toString();
	
			paramMap.put("edonumber", edonumber);

			log.info(" *** getEdoBLDetails SQL *****" + sql);
			log.info(" *** getEdoBLDetails params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			String status = "";
			CuttoffEdoValueObject edoValueObject = new CuttoffEdoValueObject();
			while(rs.next()) {
				String crg_type_cd=CommonUtility.deNull(rs.getString("CRG_TYPE_CD"));
				String crg_type_nm=CommonUtility.deNull(rs.getString("CRG_TYPE_NM"));
				String hs_code=CommonUtility.deNull(rs.getString("HS_CODE"));
				String crg_des=CommonUtility.deNull(rs.getString("CRG_DES"));
				String mft_markings=CommonUtility.deNull(rs.getString("MFT_MARKINGS"));
				String cons_nm=CommonUtility.deNull(rs.getString("CONS_NM"));
				String crg_status=CommonUtility.deNull(rs.getString("CRG_STATUS"));
				String dg_ind=CommonUtility.deNull(rs.getString("DG_IND"));
				String stg_type=CommonUtility.deNull(rs.getString("STG_TYPE"));
				String dis_type=CommonUtility.deNull(rs.getString("DIS_TYPE"));
				String pkg_type_cd=CommonUtility.deNull(rs.getString("PKG_TYPE_CD"));
				String pkg_type_desc=CommonUtility.deNull(rs.getString("PKG_DESC"));
				String nbr_pkgs= "";
				int nn_temp = 0;

				status = CommonUtility.deNull(rs.getString("CRG_STATUS"));

				if(status.equals("L"))
					nbr_pkgs = CommonUtility.deNull(rs.getString("NBR_PKG_L"));
				else
					nbr_pkgs = CommonUtility.deNull(rs.getString("NBR_PKG_T"));

				nn_temp = Integer.parseInt(nbr_pkgs);
				if(nn_temp < 0)
					nbr_pkgs = "0";
				edoValueObject.setCrgTypeCd(crg_type_cd);
				edoValueObject.setCrgTypeNm(crg_type_nm);
				edoValueObject.setHsCode(hs_code);
				edoValueObject.setCrgDes(crg_des);
				edoValueObject.setMftMarkings(mft_markings);
				edoValueObject.setCrgStatus(crg_status);
				edoValueObject.setDgInd(dg_ind);
				edoValueObject.setStgType(stg_type);
				edoValueObject.setDisType(dis_type);
				edoValueObject.setPkgTypeCd(pkg_type_cd);
				edoValueObject.setPkgTypeDesc(pkg_type_desc);
				edoValueObject.setNbrPkgs(nbr_pkgs);
				edoValueObject.setMftMarkings(mft_markings);
				edoValueObject.setCrgStatus(crg_status);
				edoValueObject.setConsNm(cons_nm);
			}
			bldetailsvector.add(edoValueObject);

			log.info("END: *** getEdoBLDetails Result *****" + bldetailsvector.toString());
		
		} catch (NullPointerException e) {
			log.info("Exception getEdoBLDetails : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEdoBLDetails : " , e);
			throw new BusinessException("M4201");
		}finally{
			log.info("END: getEdoBLDetails  DAO  END");
		}

		return bldetailsvector;
	}


	@Override
	public List<CutoffValueObject> getEdoNbr(String strblnbr, String strvarnbr) throws BusinessException {

		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<CutoffValueObject> edolistvector=new ArrayList<CutoffValueObject>();

		try {
			log.info("START: getEdoNbr  DAO  Start Obj: "+" strblnbr:"+strblnbr+" strvarnbr:"+strvarnbr );
			

			String sql = "select edo.edo_asn_nbr from gb_edo edo where edo.mft_seq_nbr = '"+strblnbr+
					"' and edo.var_nbr = :strvarnbr ";

			paramMap.put("strvarnbr", strvarnbr);
			log.info(" *** getEdoNbr SQL *****" + sql);
			log.info(" *** getEdoNbr params *****" + paramMap.toString());

			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while(rs.next()) {

				CutoffValueObject cutoffValueObject = new CutoffValueObject();
				String edonbr=CommonUtility.deNull(rs.getString(1));
				cutoffValueObject.setEdoAsnNbr(edonbr);
				edolistvector.add(cutoffValueObject);
			}

			log.info("END: *** getEdoNbr Result *****" + edolistvector.toString());
	
		} catch (NullPointerException e) {
			log.info("Exception getEdoNbr : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getEdoNbr : " , e);
			throw new BusinessException("M4201");
		}finally{
			log.info("END: getEdoNbr  DAO  END");
		}

		return edolistvector;
	}

	@Override
	public List<CuttoffEdoValueObject> getBLNbrList(String strvarnbr, String strscreen) throws BusinessException {

		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = "";
		List<CuttoffEdoValueObject> blnbrlistvector=new ArrayList<CuttoffEdoValueObject>();
		try {
			log.info("START: getBLNbrList  DAO  Start Obj "+" strblnbr: "+ strvarnbr + " strscreen: " + strscreen);
			

			sql= "SELECT UNIQUE(BL_NBR), MFT_SEQ_NBR FROM MANIFEST_DETAILS  WHERE "
					+"BL_STATUS='A' AND VAR_NBR= :strvarnbr ORDER BY MFT_SEQ_NBR";

			paramMap.put("strvarnbr", strvarnbr);
			
			log.info(" *** getBLNbrList SQL *****" + sql);
			log.info(" *** getBLNbrList params *****" + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);
			while(rs.next()) {
				CuttoffEdoValueObject edoValueObject = new CuttoffEdoValueObject();
				String bl_nbr=CommonUtility.deNull(rs.getString("BL_NBR"));
				String mft_seq_nbr=CommonUtility.deNull(rs.getString("MFT_SEQ_NBR"));
				edoValueObject.setBlNbr(bl_nbr) ;
				edoValueObject.setMftSeqNbr(mft_seq_nbr) ;
				blnbrlistvector.add(edoValueObject);
			}


			log.info("END: *** getBLNbrList Result *****" + blnbrlistvector.toString());
	
		} catch (NullPointerException e) {
			log.info("Exception getBLNbrList : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception getBLNbrList : " , e);
			throw new BusinessException("M4201");
		}finally{
			
			log.info("END: getBLNbrList  DAO  END");
		}

		return blnbrlistvector;
	}


	@Override
	public String deleteBlCutoff(String mftnbr, String cutoffno) throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		String deleted="";
		try {
			log.info("START: deleteBlCutoff  DAO  Start Obj "+"mftnbr:"+mftnbr+" cutoffno:"+cutoffno );
			

			sb.append("update manifest_details set cut_off_nbr_pkgs=");
			sb.append("to_number((select cut_off_nbr_pkgs from manifest_details where mft_seq_nbr= :mftnbr) - ");
			sb.append("(select cut_off_nbr_pkgs from cut_off_details where cut_off_nbr = :mftnbr )) where mft_seq_nbr=:mftnbr ");
			String sql = sb.toString();
			
			String sql1 = "update cut_off_details set cut_off_status='X' where cut_off_nbr= :cutoffno ";

			paramMap.put("mftnbr", Integer.parseInt(mftnbr));
			paramMap.put("cutoffno", Integer.parseInt(cutoffno));

			log.info(" *** deleteBlCutoff SQL *****" + sql);
			log.info(" *** deleteBlCutoff params *****" + paramMap.toString());
			int result = namedParameterJdbcTemplate.update(sql, paramMap);


			log.info(" *** deleteBlCutoff SQL *****" + sql1);
			log.info(" *** deleteBlCutoff params *****" + paramMap.toString());
			int result1 =namedParameterJdbcTemplate.update(sql1, paramMap);

			if(result!=0 && result1!=0)
				deleted="DELETED";
			else
				deleted="NOT DELETED";

			log.info("END: *** deleteBlCutoff Result *****" + CommonUtility.deNull(deleted));
	
		} catch (NullPointerException e) {
			log.info("Exception deleteBlCutoff : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception deleteBlCutoff : " , e);
			throw new BusinessException("M4201");
		}finally{

			log.info("END: deleteBlCutoff  DAO  END");
		}
		return deleted;
	}

	@Override
	public String deleteEdoCutoff(String edoasnnbr, String cutoffnbr) throws BusinessException {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		String deleted="";
		try {
			log.info("START: deleteEdoCutoff  DAO  Start Obj "+" edoasnnbr:"+edoasnnbr+" cutoffnbr:"+cutoffnbr );
			

			sb.append("update gb_edo set cut_off_nbr_pkgs=");
			sb.append("to_number((select cut_off_nbr_pkgs from gb_edo where edo_asn_nbr= :edoasnnbr ) - ");
			sb.append("(select cut_off_nbr_pkgs from cut_off_details where cut_off_nbr = :cutoffnbr )) where edo_asn_nbr=:edoasnnbr ");
			String sql = sb.toString();
			log.info("the delete status sql is =================================>>>>>>>>> " + sql);

			String sql1 = "update cut_off_details set cut_off_status='X' where cut_off_nbr= :cutoffnbr ";
			log.info("the delete status sql1  is =================================>>>>>>>>> " + sql1);

			paramMap.put("edoasnnbr", edoasnnbr);
			paramMap.put("cutoffnbr", Integer.parseInt(cutoffnbr));

			log.info(" *** deleteEdoCutoff SQL *****" + sql);
			log.info(" *** deleteEdoCutoff params *****" + paramMap.toString());
			int result = namedParameterJdbcTemplate.update(sql, paramMap);


			log.info(" *** deleteEdoCutoff SQL *****" + sql1);
			log.info(" *** deleteEdoCutoff params *****" + paramMap.toString());
			int result1 = namedParameterJdbcTemplate.update(sql1, paramMap);

			if(result!=0 && result1!=0)
				deleted="DELETED";
			else
				deleted="NOT DELETED";

			log.info("END: *** deleteEdoCutoff Result *****" + CommonUtility.deNull(deleted));
		
		} catch (NullPointerException e) {
			log.info("Exception deleteEdoCutoff : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception deleteEdoCutoff : " , e);
			throw new BusinessException("M4201");
		}finally{
			
			log.info("END: deleteEdoCutoff  DAO  END");
		}
		return deleted;
	}

	@Override
	public String saveCutoffDetails(String varnbr, String vesselname, String invoyage, String blnbr, String cargotype,
			String hscode, String cargodesc, String cargomarking, String cargostatus, String edonumber,
			String storageind, String cutofftype, String pkgtype, String newnbrpkgs1, String struserid)
					throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		int cutoffnbr=0;
		String returnstring="";
		try {
			log.info("START: saveCutoffDetails  DAO  Start Obj "+"varnbr:"+varnbr+" vesselname:"+vesselname+" invoyage:"+invoyage+" blnbr:"+blnbr
					+" cargotype:"+cargotype+" hscode:"+hscode+" cargodesc:"+cargodesc+" cargomarking:"+cargomarking+" cargostatus:"+cargostatus
					+" edonumber:"+edonumber+" storageind:"+storageind+" cutofftype:"+cutofftype+" pkgtype:"+pkgtype
					+" newnbrpkgs1:"+newnbrpkgs1+" struserid:"+struserid);
			

			String getcut_off_nbr = "Select NVL(MAX(CUT_OFF_NBR) + 1 , 1) as Cut_off_Nbr from cut_off_details";

			log.info(" *** saveCutoffDetails SQL *****" + getcut_off_nbr);
			rs = namedParameterJdbcTemplate.queryForRowSet(getcut_off_nbr, paramMap);
			while(rs.next()) {
				cutoffnbr = rs.getInt("Cut_off_Nbr");
			}

			String sqlMftUpd = new String();

			sqlMftUpd = "update manifest_details set cut_off_nbr_pkgs = nvl(cut_off_nbr_pkgs,0) + :newnbrpkgs1 where mft_seq_nbr= :blnbr and var_nbr= :varnbr";
			paramMap.put("newnbrpkgs1", newnbrpkgs1);
			paramMap.put("blnbr", blnbr);
			paramMap.put("varnbr", varnbr);

			log.info("CutoffCargo::saveCutoffDetails::::sqlMftUpd= "+sqlMftUpd);
			log.info(" *** saveCutoffDetails params *****" + paramMap.toString());
			namedParameterJdbcTemplate.update(sqlMftUpd, paramMap);


			//save cutoff cargo into the file
			sb.append("INSERT INTO CUT_OFF_DETAILS (");
			sb.append(" CUT_OFF_NBR, CUT_OFF_TYPE, CUT_OFF_NBR_PKGS,");
			sb.append(" MFT_SEQ_NBR, TRANS_TYPE,");
			sb.append(" LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, VAR_NBR)");
			sb.append(" VALUES( :cutoffnbr,:cutofftype, ");
			sb.append(" :newnbrpkgs1,:blnbr,");
			sb.append(" 'B', :struserid, sysdate,:varnbr)");

			String sql = sb.toString();
	
			log.info("CutoffCargo::saveCutoffDetails::::sql= " + sql);
			
			paramMap.put("cutoffnbr", cutoffnbr);
			paramMap.put("cutofftype", cutofftype);
			paramMap.put("newnbrpkgs1", Integer.parseInt(newnbrpkgs1));
			paramMap.put("blnbr", blnbr);
			paramMap.put("struserid", struserid);
			paramMap.put("varnbr", varnbr);
			
			log.info(" *** saveCutoffDetails params *****" + paramMap.toString());
			int count = namedParameterJdbcTemplate.update(sql, paramMap);
			if(count==0) {
				throw new BusinessException(ConstantUtil.ErrorMsg_Insert_Failed);
			}

			returnstring = Integer.toString(cutoffnbr);

			log.info("END: *** saveCutoffDetails Result *****" + CommonUtility.deNull(returnstring));
		
		} catch (NullPointerException e) {
			log.info("Exception saveCutoffDetails : " , e);
			throw new BusinessException("M4201");
		} catch (BusinessException e) {
			log.info("Exception saveCutoffDetails : " , e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception saveCutoffDetails : " , e);
			throw new BusinessException("M4201");
		}finally{
			log.info("END: saveCutoffDetails  DAO  END");
		}

		return returnstring;
	}

	@Override
	public String saveEdoCutoffDetails(String varnbr, String vesselname, String invoyage, String blnbr,
			String cargotype, String hscode, String cargodesc, String cargomarking, String cargostatus,
			String edonumber, String storageind, String cutofftype, String pkgtype, String newnbrpkgs1,
			String struserid) throws BusinessException {

		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		int cutoffnbr=0;
		String returnstring="";
		try {
			log.info("START: saveEdoCutoffDetails  DAO  Start Obj "+"varnbr:"+varnbr+" vesselname:"+vesselname+" invoyage:"+invoyage+" blnbr:"+blnbr
					+" cargotype:"+cargotype+" hscode:"+hscode+" cargodesc:"+cargodesc+" cargomarking:"+cargomarking+" cargostatus:"+cargostatus
					+" edonumber:"+edonumber+" storageind:"+storageind+" cutofftype:"+cutofftype+" pkgtype:"+pkgtype
					+" newnbrpkgs1:"+newnbrpkgs1+" struserid:"+struserid);
			

			String getcut_off_nbr = "Select NVL(MAX(CUT_OFF_NBR) + 1 , 1) as Cut_off_Nbr from cut_off_details";

			log.info(" *** saveEdoCutoffDetails SQL *****" + getcut_off_nbr);
			rs = namedParameterJdbcTemplate.queryForRowSet(getcut_off_nbr, paramMap);
			
			while(rs.next()) {
				cutoffnbr = rs.getInt("Cut_off_Nbr");
			}

			String sqlMftUpd = new String();

			sqlMftUpd =  "update gb_edo set cut_off_nbr_pkgs = nvl(cut_off_nbr_pkgs,0) + :newnbrpkgs1 where edo_asn_nbr = :edonumber ";
			paramMap.put("edonumber", edonumber);
			paramMap.put("newnbrpkgs1", newnbrpkgs1);
			
			log.info("CutoffCargo::saveEdoCutoffDetails::::sqlMftUpd= "+sqlMftUpd);
			log.info(" *** saveEdoCutoffDetails params *****" + paramMap.toString());
			namedParameterJdbcTemplate.update(sqlMftUpd, paramMap);
			sb.append("INSERT INTO CUT_OFF_DETAILS (");
			sb.append(" CUT_OFF_NBR, CUT_OFF_TYPE, CUT_OFF_NBR_PKGS,");
			sb.append(" MFT_SEQ_NBR, EDO_ASN_NBR, TRANS_TYPE,");
			sb.append(" LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, VAR_NBR)");
			sb.append(" VALUES(:cutoffnbr,:cutofftype");
			sb.append(",:newnbrpkgs1,:blnbr,");
			sb.append(":edonumber,'E',:struserid,sysdate,:varnbr)");

			paramMap.put("cutoffnbr", cutoffnbr);
			paramMap.put("cutofftype", cutofftype);
			paramMap.put("newnbrpkgs1", Integer.parseInt(newnbrpkgs1));
			paramMap.put("blnbr", blnbr);
			paramMap.put("edonumber", edonumber);
			paramMap.put("struserid", struserid);
			paramMap.put("varnbr", varnbr);
			
			String sql = sb.toString();
			log.info("CutoffCargo::saveEdoCutoffDetails::::sql= " + sql);
			log.info(" *** saveEdoCutoffDetails params *****" + paramMap.toString());
			namedParameterJdbcTemplate.update(sql, paramMap);

			returnstring = Integer.toString(cutoffnbr);

			log.info("END: *** saveEdoCutoffDetails Result *****" + CommonUtility.deNull(returnstring));
		
		} catch (NullPointerException e) {
			log.info("Exception saveEdoCutoffDetails : " , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.info("Exception saveEdoCutoffDetails : " , e);
			throw new BusinessException("M4201");
		}finally{
			
			log.info("END: saveEdoCutoffDetails  DAO  END");
		}

		return returnstring;
	}


}
