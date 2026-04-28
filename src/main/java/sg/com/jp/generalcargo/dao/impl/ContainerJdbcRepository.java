package sg.com.jp.generalcargo.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.ContainerRepository;
import sg.com.jp.generalcargo.domain.ContainerValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;

@Repository("containerRepository")
public class ContainerJdbcRepository implements ContainerRepository{

	private static final Log log = LogFactory.getLog(ContainerJdbcRepository.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	//ejb.sessionBeans.cim.Esn --> ContainerEJB
	@Override
	public ContainerValueObject getContainerInformation(String cntrNo) throws BusinessException {
    	ContainerValueObject cntrValueObject = null;
        String sql = "";
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
        try {
        	log.info("START: getContainerInformation  DAO  Start Obj "+" cntrNo:"+cntrNo );

            sql = "SELECT cntr_seq_nbr,misc_app_nbr,imp_haul_cd, exp_haul_cd, purp_cd, size_ft, iso_size_type_cd FROM cntr WHERE txn_status = 'A' AND cntr_nbr = :cntrNo ";
            
    		log.info(" *** getContainerInformation SQL *****" + sql);
    		paramMap.put("cntrNo", cntrNo);
             rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

            while (rs.next()) {
            	cntrValueObject = new ContainerValueObject();
            	cntrValueObject.setContainerSeqNo(rs.getLong("cntr_seq_nbr"));
            	cntrValueObject.setMisc_app_nbr(CommonUtility.deNull(rs.getString("misc_app_nbr")));
            	cntrValueObject.setImportHaulier(CommonUtility.deNull(rs.getString("imp_haul_cd")));
            	cntrValueObject.setExportHaulier(CommonUtility.deNull(rs.getString("exp_haul_cd")));
            	cntrValueObject.setPurposeCode(CommonUtility.deNull(rs.getString("purp_cd")));
            	cntrValueObject.setSizeFt(CommonUtility.deNull(rs.getString("size_ft")));
            	cntrValueObject.setIsoCode(CommonUtility.deNull(rs.getString("iso_size_type_cd")));
            	cntrValueObject.setContainerNo(cntrNo);
            }
            
           
            log.info("END: *** getContainerInformation Result *****" + cntrValueObject.toString());
            return cntrValueObject;
		}catch (NullPointerException e) { 
			log.info("Exception getContainerInformation : ", e);
			throw new BusinessException("M4201");
		}catch (Exception e) {
			log.info("Exception getContainerInformation : ", e);
			throw new BusinessException("M4201");
		}finally{
			log.info("END: getContainerInformation  DAO  END");
		}

    }
	
	@Override
	public boolean validateGCStuffIndicatorCntr(String loadVVCd, String cntrNbr) throws BusinessException{
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();	
		String sql = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			log.info("START: validateGCStuffIndicatorCntr  DAO  Start Obj "+" loadVVCd:"+loadVVCd+" cntrNbr:"+cntrNbr );
			sb.append("select cntr.cntr_nbr From cntr, cntr_operation where cntr.load_vv_cd = :loadVVCd and cntr.purp_cd in ('EX','RS') and cntr.txn_status <> 'D' ");
			sb.append(" and cntr.cntr_seq_nbr = cntr_operation.cntr_seq_nbr and cntr_operation.arr_dttm is not null and cntr.cntr_nbr = :cntrNbr ");
			sql = sb.toString();
			log.info(" *** validateGCStuffIndicatorCntr SQL *****" + sql);
			paramMap.put("loadVVCd", loadVVCd);
			paramMap.put("cntrNbr", cntrNbr);
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if(rs.next()) {
				return true;
			}

			log.info("END: *** validateGCStuffIndicatorCntr Result *****");
		}catch (NullPointerException e) { 
			log.info("Exception validateGCStuffIndicatorCntr : ", e);
			throw new BusinessException("M4201");
		}catch (Exception e) {
			log.info("Exception validateGCStuffIndicatorCntr : ", e);
			throw new BusinessException("M4201");
		}finally{
			log.info("END: validateGCStuffIndicatorCntr  DAO  END");
		}

		return false;
	}
}
