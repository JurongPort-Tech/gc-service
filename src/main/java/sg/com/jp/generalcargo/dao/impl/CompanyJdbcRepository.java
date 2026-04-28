package sg.com.jp.generalcargo.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.CompanyRepository;
import sg.com.jp.generalcargo.domain.CompanyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("CompanyRepository")
public class CompanyJdbcRepository implements CompanyRepository {

	private static final Log log = LogFactory.getLog(CompanyJdbcRepository.class);
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	//ejb.sessionBeans.codes.Company --> CompanyEJB
	@Override
	public CompanyValueObject getCompanyInfo(String companyCode) throws BusinessException
    {
        String queryString = new String();
        CompanyValueObject companyValueObj= new CompanyValueObject();
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
        try
        {
        	log.info("START: getCompanyInfo  DAO  Start Obj "+" companyCode:"+companyCode );    		
            queryString = "SELECT * FROM company_code  WHERE co_cd = :companyCode ";
    		log.info(" *** getCompanyInfo SQL *****" + queryString);
    		paramMap.put("companyCode", companyCode);
			log.info(" ***paramMap *****" + paramMap.toString());
            rs = namedParameterJdbcTemplate.queryForRowSet(queryString, paramMap);
            if (rs.next())
            {
                // For the Company Code selected from the database,
                // set all its attributes into the Company Code's Value Object
                companyValueObj.setCompanyCode(CommonUtility.deNull(rs.getString("co_cd")));
                companyValueObj.setPsaCompanyCode(CommonUtility.deNull(rs.getString("psa_co_cd")));
                companyValueObj.setCompanyName(CommonUtility.deNull(rs.getString("co_nm")));
                companyValueObj.setLOB(CommonUtility.deNull(rs.getString("lob_cd")));
                companyValueObj.setCustRelationInd(CommonUtility.deNull(rs.getString("cust_relation_ind")));
                companyValueObj.setStatus(CommonUtility.deNull(rs.getString("rec_status")));
                companyValueObj.setLastModifiedBy(CommonUtility.deNull(rs.getString("last_modify_user_id")));
				companyValueObj.setLastModifiedDate(CommonUtility.deNull(CommonUtil.parseDBDateToStr(rs.getTimestamp("last_modify_dttm"))));

                ///tuanta10 add at 07/08/2007
                /// to update 5 new fields added to company_code table

                companyValueObj.setAllowJPOnline(CommonUtility.deNull(rs.getString("allow_jponline")));
                companyValueObj.setRegFeeChargeStatus(CommonUtility.deNull(rs.getString("reg_fee_charge_sts")));
                companyValueObj.setSubFeeChargeStatus(CommonUtility.deNull(rs.getString("sub_fee_charge_sts")));
                companyValueObj.setAcToBill(CommonUtility.deNull(rs.getString("ac_to_bill")));
                companyValueObj.setContractNumber(CommonUtility.deNull(rs.getString("contract_nbr")));
                ///

                //Added by Jade for CR-FMAS-20120202-001
                companyValueObj.setBgSdAmt(rs.getDouble("BG_SD_AMT")+"");
                companyValueObj.setCreditControlInd(rs.getString("CREDIT_CONTROL_IND"));
                companyValueObj.setCreditControlStatus(rs.getString("CREDIT_CONTROL_ST"));
                companyValueObj.setUnpaidPercent(rs.getDouble("UNPAID_PCT")+"");
                companyValueObj.setUnpaid60Amt(rs.getDouble("UNPAID_60D_AMT")+"");
                companyValueObj.setUnpaid90Amt(rs.getDouble("UNPAID_90D_AMT")+"");
                //End of adding by Jade for CR-FMAS-20120202-001
            }

    		log.info("END: *** getCompanyInfo Result *****" + companyValueObj.toString());
    		} catch (NullPointerException e) { 
    			log.info("Exception getCompanyInfo : ", e);
    			throw new BusinessException("M4201");
    		} catch (Exception e) {
    			log.info("Exception getCompanyInfo : ", e);
    			throw new BusinessException("M4201");
    		} finally{
    			log.info("END: getCompanyInfo  DAO  END");
    		}
        return companyValueObj;
    }
}
