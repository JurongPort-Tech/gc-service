package sg.com.jp.generalcargo.dao.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.VesselProdSurchargeRepo;
import sg.com.jp.generalcargo.domain.CustVslProdValueObject;
import sg.com.jp.generalcargo.domain.PubVslProdValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("vesselProdSurchargeRepo")
public class VesselProdSurchargeJdbcRepo implements VesselProdSurchargeRepo{

	private static final Log log = LogFactory.getLog(VesselProdSurchargeJdbcRepo.class);


	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


	//ejb.sessionBeans.gbms.cab.vesselProdSurcharge -->VesselProdSurchargeEjb

	@Override
	public CustVslProdValueObject getCustomisedRate(Double tonnage, String custCd, String vvcd) throws BusinessException{


		Map<String,Object> paramMap = new HashMap<String, Object>();
		PubVslProdValueObject pubVslProdValueObject = null;
		CustVslProdValueObject custVslProdValueObject = new CustVslProdValueObject();
		Timestamp tstmp = null;
		String version_nbr = "0";
		SqlRowSet rs_ver = null;
		StringBuffer sb = new StringBuffer();
		SqlRowSet rs = null;
		try {
			log.info("START: getCustomisedRate  DAO  Start Obj "+" tonnage:"+tonnage+" custCd:"+custCd+" vvcd:"+vvcd );

			tstmp = getAtb( vvcd );


			sb.append(" SELECT * FROM PUBLISH_VSL_PROD_RATE_VER P WHERE (P.EFF_START_DTTM <= :tstmp AND (P.EFF_END_DTTM+1) > :tstmp AND P.STATUS = 'A') ");
			sb.append(" OR ( P.EFF_START_DTTM <= :tstmp AND P.EFF_END_DTTM IS NULL AND P.STATUS  = 'A') ");
			String sql= sb.toString();


			log.info(" *** getCustomisedRate SQL *****" + sql);


			paramMap.put("tstmp",tstmp);

			rs_ver = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			while( rs_ver.next() ) {
				custVslProdValueObject.setVersion_nbr( CommonUtility.deNull( rs_ver.getString( "VERSION_NBR") ) )  ;
				custVslProdValueObject.setEff_start_Dt( CommonUtility.deNull( rs_ver.getString( "EFF_START_DTTM") ) ) ;
				custVslProdValueObject.setEff_end_Dt( CommonUtility.deNull( rs_ver.getString( "EFF_END_DTTM") ) );
				version_nbr = rs_ver.getString("VERSION_NBR");

				log.info( " ---- version no to get customised rate is "+custVslProdValueObject.getVersion_nbr() );
			}

			// throw process exception if version no is null

			log.info( " Customer Code  -- "+custCd );
			log.info( " Tonnage Double -- "+tonnage.toString());
			log.info( " Version number -- "+version_nbr);
			// get the grace period parameters for the tonnage from
			// published range for the tonnage
			pubVslProdValueObject = getPublishedRate(tonnage,vvcd);

			String gracePrdMax = pubVslProdValueObject.getGrace_prd_max();
			String gracePrdHr =  pubVslProdValueObject.getGrace_prd_hrs();
			String gracePrdPct = pubVslProdValueObject.getGrace_prd_pct();

			if ( gracePrdMax == null || gracePrdMax.equals("") ) {
				gracePrdMax = new Integer(0).toString();
				gracePrdHr = new Integer(0).toString();
				gracePrdPct = new Integer(0).toString();
			}
			custVslProdValueObject.setGrace_prd_max( gracePrdMax );
			custVslProdValueObject.setGrace_prd_hrs( gracePrdHr );
			custVslProdValueObject.setGrace_prd_pct( gracePrdPct );

			log.info( " gracePerdMax  -- "+gracePrdMax );
			log.info( " gracePrdHr    -- "+gracePrdHr);
			log.info( " gracePrdPct   -- "+gracePrdPct);

			int rate = 0;

			sb = new StringBuffer();	

			sb.append(" SELECT * FROM ");
			sb.append(" CUST_VSL_PROD_RATE C ");
			sb.append(" WHERE C.CUST_CD = :custCd ");
			sb.append( " AND C.VERSION_NBR = :version_nbr ");
			sb.append(" AND :tonnageFr  >= C.FROM_TON AND ( :tonnageTo <= C.TO_TON OR C.TO_TON IS NULL ) ");
			sb.append(" AND C.STATUS = 'A'  ");
			sb.append( " AND  C.FROM_TON_INCL_IND = 'Y'  ");
			sb.append(" AND C.TO_TON_INCL_IND = 'Y' ");

			String strQuery =sb.toString();

			for( int cnt = 0; cnt < 4; cnt++ ){

				if( cnt == 1 ){

					sb = new StringBuffer();	

					sb.append(" SELECT * FROM ");
					sb.append(" CUST_VSL_PROD_RATE C ");
					sb.append(" WHERE C.CUST_CD = :custCd ");
					sb.append(" AND C.VERSION_NBR = :version_nbr ");
					sb.append(" AND :tonnageFr  >= C.FROM_TON AND ( :tonnageTo < C.TO_TON OR C.TO_TON IS NULL ) ");
					sb.append(" AND C.STATUS = 'A'  ");
					sb.append(" AND  C.FROM_TON_INCL_IND = 'Y'  ");
					sb.append( " AND C.TO_TON_INCL_IND = 'N' ");
					strQuery =sb.toString();
				}
				if( cnt == 2 ){
					sb = new StringBuffer();
					sb.append(" SELECT * FROM ");
					sb.append(" CUST_VSL_PROD_RATE C ");
					sb.append(" WHERE C.CUST_CD = :custCd ");
					sb.append(" AND C.VERSION_NBR = :version_nbr ");
					sb.append(" AND :tonnageFr  > C.FROM_TON AND ( :tonnageTo <= C.TO_TON OR C.TO_TON IS NULL ) ");
					sb.append(" AND C.STATUS = 'A'  ");
					sb.append(" AND  C.FROM_TON_INCL_IND = 'N'  ");
					sb.append(" AND C.TO_TON_INCL_IND = 'Y' ");
					strQuery =sb.toString();
				}
				if( cnt == 3 ){
					sb = new StringBuffer();
					sb.append(" SELECT * FROM ");
					sb.append(" CUST_VSL_PROD_RATE C ");
					sb.append(" WHERE C.CUST_CD = :custCd ");
					sb.append(" AND C.VERSION_NBR = :version_nbr ");
					sb.append(" AND :tonnageFr  > C.FROM_TON AND ( :tonnageTo < C.TO_TON OR C.TO_TON IS NULL ) ");
					sb.append(" AND C.STATUS = 'A'  ");
					sb.append(" AND  C.FROM_TON_INCL_IND = 'N'  ");
					sb.append(" AND C.TO_TON_INCL_IND = 'N' ");
					strQuery =sb.toString();
				}

				log.info(" cnt value is ~~~ "+cnt);
				log.info( " Now the qry is ~~~~~"+strQuery);

				log.info(" *** getCustomisedRate SQL *****" + strQuery);

				paramMap.put("custCd", custCd.trim());
				paramMap.put("version_nbr", version_nbr.trim() );
				paramMap.put("tonnageFr", tonnage.toString().trim() );
				paramMap.put("tonnageTo", tonnage.toString());

				rs = namedParameterJdbcTemplate.queryForRowSet(strQuery.toString(), paramMap);

				while( rs.next() ){
					log.info("in while getting customised rate .... ");
					log.info("the rate is "+rs.getString("CUST_RATE"));
					rate = rs.getInt("CUST_RATE");
					custVslProdValueObject.setRate(new Integer(rate).toString() );
					custVslProdValueObject.setRateExist(true);
				}



				log.info( " got the rate.. "+rate);
				if (  rate != 0 ){
					break;
				}
				else if ( rate == 0 ){
					continue;
				}
			}
			log.info("END: *** getCustomisedRate Result *****" + custVslProdValueObject.toString());
		} catch (NullPointerException e) {
			log.error("Exception getCustomisedRate :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getCustomisedRate :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getCustomisedRate  DAO  END");
		}

		return custVslProdValueObject;
	}

	@Override
	public PubVslProdValueObject getPublishedRate(Double tonnage,String vvcd) throws BusinessException{
		log.info( "-- In getPublishedRate --");

		PubVslProdValueObject pubVslProdVerVO = new PubVslProdValueObject();
		Timestamp tstmp = null;
		String version_nbr = "0";
		SqlRowSet rs_ver = null;
		SqlRowSet rs =null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer();
		try {
			log.info("START: getPublishedRate  DAO  Start Obj " + " tonnage:" + tonnage + " vvcd:" + CommonUtil.deNull(vvcd));

			tstmp = getAtb( vvcd );

			sb.append(" SELECT * FROM PUBLISH_VSL_PROD_RATE_VER P WHERE (P.EFF_START_DTTM <= :tstmp AND (P.EFF_END_DTTM+1) > :tstmp AND P.STATUS = 'A') " );
			sb.append(" OR ( P.EFF_START_DTTM <= :tstmp AND P.EFF_END_DTTM IS NULL AND P.STATUS  = 'A') ");
			String sql=	sb.toString();

			log.info(" *** getPublishedRate SQL *****" + sql);

			paramMap.put("tstmp",tstmp);


			rs_ver =  namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);

			while( rs_ver.next() ) {
				pubVslProdVerVO.setVersion_nbr( CommonUtility.deNull( rs_ver.getString( "VERSION_NBR") ) )  ;
				pubVslProdVerVO.setEff_start_Dt( CommonUtility.deNull( rs_ver.getString( "EFF_START_DTTM") ) ) ;
				pubVslProdVerVO.setEff_end_Dt( CommonUtility.deNull( rs_ver.getString( "EFF_END_DTTM") ) );
				version_nbr = rs_ver.getString("VERSION_NBR");


				log.info( " ----- "+pubVslProdVerVO.getVersion_nbr() );
			}

			sb = new StringBuffer();	

			sb.append(" SELECT * FROM ");
			sb.append(" PUBLISH_VSL_PROD_RATE P ");
			sb.append(" WHERE P.VERSION_NBR = :version_nbr ");
			sb.append(" AND :tonnage >= P.FROM_TON AND ( :tonnage <= P.TO_TON OR P.TO_TON IS NULL ) ");
			sb.append(" AND P.STATUS = 'A' ");
			sb.append(" AND  P.FROM_TON_INCL_IND = 'Y' ");
			sb.append(" AND P.TO_TON_INCL_IND = 'Y' ");
			String strQuery =sb.toString();
			int rate = 0;

			for( int cnt = 0; cnt < 4; cnt++ ){

				if( cnt == 1 ){
					sb = new StringBuffer();
					sb.append(" SELECT * FROM ");
					sb.append(" PUBLISH_VSL_PROD_RATE P ");
					sb.append(" WHERE P.VERSION_NBR = :version_nbr ");
					sb.append(" AND :tonnage >= P.FROM_TON AND ( :tonnage < P.TO_TON OR P.TO_TON IS NULL ) ");
					sb.append(" AND P.STATUS = 'A' ");
					sb.append(" AND  P.FROM_TON_INCL_IND = 'Y' ");
					sb.append(" AND P.TO_TON_INCL_IND = 'N' ");
					strQuery =sb.toString();
				}
				if( cnt == 2 ){

					sb = new StringBuffer();
					sb.append(" SELECT * FROM ");
					sb.append(" PUBLISH_VSL_PROD_RATE P ");
					sb.append(" WHERE P.VERSION_NBR = :version_nbr ");
					sb.append(" AND :tonnage > P.FROM_TON AND ( :tonnage <= P.TO_TON OR P.TO_TON IS NULL ) ");
					sb.append(" AND P.STATUS = 'A' ");
					sb.append(" AND  P.FROM_TON_INCL_IND = 'N' ");
					sb.append(" AND P.TO_TON_INCL_IND = 'Y' ");
					strQuery =sb.toString();
				}
				if( cnt == 3 ){
					sb = new StringBuffer();
					sb.append(" SELECT * FROM ");
					sb.append(" PUBLISH_VSL_PROD_RATE P ");
					sb.append(" WHERE P.VERSION_NBR = :version_nbr ");
					sb.append(" AND :tonnage > P.FROM_TON AND ( :tonnage < P.TO_TON OR P.TO_TON IS NULL ) ");
					sb.append(" AND P.STATUS = 'A' ");
					sb.append(" AND  P.FROM_TON_INCL_IND = 'N' ");
					sb.append(" AND P.TO_TON_INCL_IND = 'N' ");
					strQuery =sb.toString();
				}

				log.info(" cnt value is ~~~ "+cnt);
				log.info( " Now the qry is ~~~~~"+strQuery);


				log.info(" *** getPublishedRate SQL *****" + strQuery);

				paramMap.put("version_nbr", version_nbr);
				paramMap.put("tonnage", tonnage.toString() );

				rs = namedParameterJdbcTemplate.queryForRowSet(strQuery.toString(), paramMap);

				while( rs.next() ){
					log.info("got the rate  "+rs.getString("RATE"));
					rate = rs.getInt("RATE");

					String publish_rate = rs.getString("RATE");
					pubVslProdVerVO.setGrace_prd_max( rs.getString("GRACE_MAX"));
					pubVslProdVerVO.setRate(publish_rate);
					pubVslProdVerVO.setGrace_prd_hrs(rs.getString("GRACE_HR"));
					pubVslProdVerVO.setGrace_prd_pct(rs.getString("GRACE_PCT"));
				}


				log.info( " finally the rate is "+rate);
				if (  rate != 0 ){
					break;
				}
				else if ( rate == 0 ){
					continue;
				}

			}
			/*	stmt =  conn.prepareStatement(SQL_SELECT_PUBLISHED_RATE);
			stmt.setString( 1, version_nbr );
			log.info( " tonnage Double "+tonnage.toString());
			stmt.setString( 2, tonnage.toString() );
			SqlRowSet rs_rate = stmt.executeQuery();
			log.info( " Got the rate details ");

			while( rs_rate.next() ) {
				String publish_rate = rs_rate.getString("RATE");
				pubVslProdVerVO.setGrace_prd_max( rs_rate.getString("GRACE_MAX"));
				 pubVslProdVerVO.setRate(publish_rate);
				 pubVslProdVerVO.setGrace_prd_hrs(rs_rate.getString("GRACE_HR"));
				 pubVslProdVerVO.setGrace_prd_pct(rs_rate.getString("GRACE_PCT"));
			}*/


			log.info("END: *** getPublishedRate Result *****" + pubVslProdVerVO.toString());
		} catch (NullPointerException e) {
			log.error("Exception getPublishedRate :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getPublishedRate :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getPublishedRate  DAO  END");
		}

		return pubVslProdVerVO;
	}

	public Timestamp getAtb(String vvcd )throws BusinessException{

		Map<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();	
		Timestamp tstmp = null;
		SqlRowSet rs_Atb = null;
		try {
			log.info("START: getAtb  DAO  Start Obj "+" vvcd:"+vvcd );

			//SL-OPS-20110511-01: 2. For the purpose of PSS, the calculation of vessel's allocated & actual port stay should be determined from vessel's ATB & not vessel's final 'end time' of shifting, swinging & hauling activity.

			sb.append(" SELECT ATB_DTTM FROM BERTHING B ");
			sb.append(" WHERE B.VV_CD = :vvcd ");
			sb.append(" AND B.SHIFT_IND = 1 ");
			String sqlQuery = sb.toString();

			log.info(" *** getAtb SQL *****" + sqlQuery);

			paramMap.put( "vvcd",vvcd );

			rs_Atb =  namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);
			while( rs_Atb.next() ){
				tstmp = rs_Atb.getTimestamp("ATB_DTTM");
			}
			log.info("END: *** getAtb Result *****" + tstmp.toString());
		} catch (NullPointerException e) {
			log.error("Exception getAtb :" , e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception getAtb :" , e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getAtb  DAO  END");
		}

		return tstmp;
	}

}
