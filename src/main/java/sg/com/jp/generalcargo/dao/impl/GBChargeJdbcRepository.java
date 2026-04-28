package sg.com.jp.generalcargo.dao.impl;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.GBChargeRepository;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;

@Repository("GBChargeRepository")
public class GBChargeJdbcRepository implements  GBChargeRepository {

	private static final Log log = LogFactory.getLog(GBChargeJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	protected JdbcTemplate jdbcTemplate;

	//StartRegion  GBChargeJdbcRepository

	@Override
	public List<ChargeableBillValueObject> getGBCharge(String refNbr, String refInd) throws BusinessException {
		Map<String, String> paramMap = new HashMap<String, String>();
		List<ChargeableBillValueObject> gbChargeArrayList = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		try {
			log.info("START: getGBCharge  DAO  Start Obj :: refNbr: " + CommonUtility.deNull(refNbr) + " refInd: " + CommonUtility.deNull(refInd));
			sb.append(" select txn_dttm, tariff_type, tariff_main_cat_cd, tariff_sub_cat_cd, version_nbr,  ");
			sb.append(" tariff_cd, tier_seq_nbr, cust_cd, acct_nbr, contract_nbr, tariff_desc, nbr_cntr, nbr_time_unit, ");
			sb.append(" nbr_other_unit, unit_rate, gst_charge, gst_amt, total_charge_amt, vv_cd, bill_ind, local_leg, contractual_yr, fmas_gst_cd from gb_bill_charge ");
			sb.append(" where ref_nbr=:refNbr and ref_ind=:refInd");
			sb.append(" order by substr(TARIFF_main_cat_CD,1,1) desc, substr(TARIFF_main_cat_CD,2,1) asc, tariff_cd asc, TIER_SEQ_NBR asc");

			paramMap.put("refNbr", refNbr);
			paramMap.put("refInd", refInd);
			
			log.info(" getGBCharge SQL: " + sb.toString() + "paramMap" + paramMap.toString());
			
			gbChargeArrayList = namedParameterJdbcTemplate.query(sb.toString(), paramMap, new BeanPropertyRowMapper<ChargeableBillValueObject>(ChargeableBillValueObject.class));
			
			log.info(" getGBCharge result : " + gbChargeArrayList.size());
		}
		catch (Exception ex) {
			log.info("Exception getGBCharge : ", ex);
			throw new BusinessException("M4201");		}        
		finally {
			log.info("[**** END getGBCharge() ****** "); 
		}        
		return gbChargeArrayList;

	}

	public void cancelGBCharge(String refNbr, String refInd) throws BusinessException  {
		String sql = "";
		Map<String,String> paramMap = new HashMap<String,String>();
		try {
			log.info("START: cancelGBCharge  DAO  Start Obj :: refNbr: " + CommonUtility.deNull(refNbr) + " refInd: " + CommonUtility.deNull(refInd));
			sql = "update gb_bill_charge set bill_ind='D' where ref_nbr=:refNbr and ref_ind=:refInd";
			
			paramMap.put("refNbr", refNbr);
			paramMap.put("refInd", refInd);

			log.info(" cancelGBCharge SQL: " + sql + "paramMap" + paramMap);
			int ctr = namedParameterJdbcTemplate.update(sql, paramMap);
			log.info("ctr: " + ctr);
			if (ctr <= 0) {
				throw new BusinessException ("ErrorMsg_Cannot_Cancel_GB_BillCharge");
			}
		} catch (BusinessException ex) {
			log.info("Exception cancelGBCharge : ", ex);
			throw new BusinessException(ex.getMessage());      
		} catch (Exception ex) {
			log.info("Exception cancelGBCharge : ", ex);
			throw new BusinessException("M4201");     
		}        
		finally {
			log.info(" END cancelGBCharge DAO " );
		}        
	}

	// package: ejb.sessionBeans.cab.gbCharge-->GBChargeEJB
		// method: addGBCharge()
		/**
		 * This method adds the computed wharfage, service charge & store rent charges
		 * into the gb_bill_charge table.
		 *
		 * @param ChargeableBillValueObject
		 * @exception NamingException
		 * @exception SQLException
		 * @exception Exception
		 */
		@Override
		public void addGBCharge(List<ChargeableBillValueObject> chargeArrayList) throws BusinessException {
			StringBuilder sb = new StringBuilder();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			try {
				log.info("START: addGBCharge  DAO  Start Obj :: chargeArrayList: " + CommonUtility.deNull(chargeArrayList.toString()));
				sb.append("	INSERT ");
				sb.append("		INTO ");
				sb.append("		gb_bill_charge (ref_nbr,");
				sb.append("		txn_dttm, ");
				sb.append("		ref_ind, ");
				sb.append("		tariff_type,");
				sb.append("		tariff_main_cat_cd, ");
				sb.append("		tariff_sub_cat_cd, ");
				sb.append("		version_nbr, ");
				sb.append("		tariff_cd,");
				sb.append("		tier_seq_nbr,");
				sb.append("		cust_cd, ");
				sb.append("		acct_nbr, ");
				sb.append("		contract_nbr, ");
				sb.append("		tariff_desc, ");
				sb.append("		nbr_cntr, ");
				sb.append("		nbr_time_unit, ");
				sb.append("		nbr_other_unit, ");
				sb.append("		unit_rate, ");
				sb.append("		gst_charge, ");
				sb.append("		gst_amt, ");
				sb.append("		total_charge_amt, ");
				sb.append("		bill_ind, ");
				sb.append("		last_modify_user_id, ");
				sb.append("		last_modify_dttm, ");
				sb.append("		local_leg, ");
				sb.append("		contractual_yr, ");
				sb.append("		fmas_gst_cd ");
				sb.append(")VALUES (:refNbr,");
				sb.append("	TO_TIMESTAMP(:txnDttm,  'YYYY-MM-DD HH24:MI:SS'), "); 
 				sb.append("	:refInd, ");
				sb.append("	:tariffType, ");
				sb.append("	:tariffMainCatCd, ");
				sb.append("	:tariffSubCatCd, ");
				sb.append("	:versionNbr, ");
				sb.append("	:tariffCd, ");
				sb.append("	:tierSeqNbr, ");
				sb.append("	:custCd, ");
				sb.append("	:acctNbr, ");
				sb.append("	:contractNbr, ");
				sb.append("	:tariffDesc, ");
				sb.append("	:nbrCntr, ");
				sb.append("	:nbrTimeUnit, ");
				sb.append("	:nbrOtherUnit, ");
				sb.append("	:unitRate, ");
				sb.append("	:gstCharge, ");
				sb.append("	:gstAmt, ");
				sb.append("	:totalChargeAmt, ");
				sb.append("	:billInd, ");
				sb.append("	:lastModifyUserId, ");
				sb.append("	to_date(:lastModifyDttm, 'YYYY-MM-DD HH24:MI:SS'), ");
				sb.append("	:localLeg, ");
				sb.append("	:contractualYr, ");
				sb.append("	:fmasGstCd)  ");
 				// End Added by Irene Tan on 3/12/2002 : CTCAB20020044
				log.info(" addGBCharge SQL: " + sb.toString());
				SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
 				for (int i = 0; i < chargeArrayList.size(); i++) {
					ChargeableBillValueObject chargeableBillValueObject = new ChargeableBillValueObject();
					chargeableBillValueObject = (ChargeableBillValueObject) chargeArrayList.get(i);

					paramMap.put("refNbr", chargeableBillValueObject.getRefNbr());
					paramMap.put("txnDttm", sm.format(chargeableBillValueObject.getTxnDttm()));
					paramMap.put("refInd", chargeableBillValueObject.getRefInd());
					paramMap.put("tariffType", chargeableBillValueObject.getTariffType());
					paramMap.put("tariffMainCatCd", chargeableBillValueObject.getTariffMainCatCd());
					paramMap.put("tariffSubCatCd", chargeableBillValueObject.getTariffSubCatCd());
					paramMap.put("versionNbr", chargeableBillValueObject.getVersionNbr());
					paramMap.put("tariffCd", chargeableBillValueObject.getTariffCd());
					paramMap.put("tierSeqNbr", chargeableBillValueObject.getTierSeqNbr());
					paramMap.put("custCd", chargeableBillValueObject.getCustCd());
					paramMap.put("acctNbr", chargeableBillValueObject.getAcctNbr());
					paramMap.put("contractNbr", chargeableBillValueObject.getContractNbr());
					paramMap.put("tariffDesc", chargeableBillValueObject.getTariffDesc());
					paramMap.put("nbrCntr", chargeableBillValueObject.getNbrCntr());
					paramMap.put("nbrTimeUnit", chargeableBillValueObject.getNbrTimeUnit());
					paramMap.put("nbrOtherUnit", chargeableBillValueObject.getNbrOtherUnit());
					paramMap.put("unitRate", chargeableBillValueObject.getUnitRate());
					paramMap.put("gstCharge", chargeableBillValueObject.getGstCharge());
					paramMap.put("gstAmt", chargeableBillValueObject.getGstAmt());
					paramMap.put("totalChargeAmt", chargeableBillValueObject.getTotalChargeAmt());
					//paramMap.put("vvCd", chargeableBillValueObject.getVvCd());
					paramMap.put("billInd", chargeableBillValueObject.getBillInd());
					paramMap.put("lastModifyUserId", chargeableBillValueObject.getLastModifyUserId());
					paramMap.put("lastModifyDttm", sm.format(chargeableBillValueObject.getLastModifyDttm()));
					paramMap.put("localLeg", chargeableBillValueObject.getLocalLeg());
					paramMap.put("contractualYr", chargeableBillValueObject.getContractualYr());
					// Added by Irene Tan on 3/12/2002 : CTCAB20020044 - To crater for GST changes
					// on 1 Jan 2003
					paramMap.put("fmasGstCd", chargeableBillValueObject.getFmasGstCd());
					// End Added by Irene Tan on 3/12/2002 : CTCAB20020044
					log.info("paramMap: " + paramMap);
					int ctr = namedParameterJdbcTemplate.update(sb.toString(),  paramMap);
					if (ctr <= 0) {
						throw new BusinessException(
								ConstantUtil.errMsg_GBCharge_err01);
					}
				}

			} catch (NullPointerException e) {
				log.info("Exception addGBCharge :" , e);
				throw new BusinessException("M4201");
			} catch (BusinessException e) {
				log.info("Exception addGBCharge :" , e);
				throw new BusinessException(e.getMessage());
			} catch (Exception e) {
				log.info("Exception addGBCharge :" , e);
				throw new BusinessException("M4201");
			} finally {
				log.info("[**** END addGBCharge() ****** ");
			}
		}

	//EndRegion GBChargeJdbcRepository

}