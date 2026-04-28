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

import sg.com.jp.generalcargo.dao.ProcessOMCGenericRepo;
import sg.com.jp.generalcargo.domain.BillJobVO;
import sg.com.jp.generalcargo.util.BusinessException;

@Repository("processOMCGenericRepo")
public class ProcessOMCGenericJdbcRepo implements ProcessOMCGenericRepo{

	private static final Log log = LogFactory.getLog(ProcessOMCGenericJdbcRepo.class);
	
	//ejb.sessionBeans.cab.processCharges -->ProcessOMCGenericEJB
	
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	
	@Override
	 public List<BillJobVO> retriveveOMCJobOrderDetail(long jobOrderId) throws BusinessException {
	    	log.info("[CAB],ProcessOMCGenericEJB :: retriveveOMCJobOrderDetail(jobOrderId : " + jobOrderId + " )");
	    	SqlRowSet rs = null;
	        Map<String, Object> paramMap = new HashMap<String, Object>();  
	        StringBuffer sqlQuery = null;

	        BillJobVO 	  billJobVO 	= null;
	        List<BillJobVO>    jobOrderDetaillList            = null;

	        try {
	        	log.info("START: retriveveOMCJobOrderDetail  DAO  Start Obj "+" jobOrderId:"+jobOrderId );

	            // initialize the job order detail list to populate all
	            jobOrderDetaillList        = new ArrayList<BillJobVO>();

	            // formulate the sql query and execute the query
	            sqlQuery = new StringBuffer();
	            sqlQuery.append("SELECT JOB_DET_ID, J.JOB_ID JOB_ID, M1.MISC_TYPE_NM SERVICE_CD, M2.MISC_TYPE_NM SUB_SERVICE_CD, FROM_DTTM, "
	                            + "TO_DTTM, UNIT, TOTAL_AMT, LOCATION ");
	            sqlQuery.append("FROM OMC_JOB_DET D ");
	            sqlQuery.append("JOIN OMC_JOB J ON J.JOB_ID = D.JOB_ID ");
	            sqlQuery.append("LEFT JOIN OMC_MISC_TYPE_CODE M1 ON D.SERVICE_CD = M1.MISC_TYPE_CD ");
	            //commented out this on 28 DEC 2012 and append with new condition
	            //sqlQuery.append("LEFT JOIN OMC_MISC_TYPE_CODE M2 ON D.SUB_SERVICE_CD = M2.MISC_TYPE_CD ");
	            sqlQuery.append("LEFT JOIN OMC_MISC_TYPE_CODE M2 ON D.SUB_SERVICE_CD = M2.MISC_TYPE_CD AND M2.CAT_CD = 'OMC_' || J.SERVICE_TYPE || '_' || D.SERVICE_CD ");
	            //end of comment 28 DEC 2012
	            sqlQuery.append("WHERE J.JOB_ID =? ");
	            sqlQuery.append("AND M1.CAT_CD = 'OMC_JOB_' || J.SERVICE_TYPE ");
	            //chuething - SSL-OMC-20121123-01 23 Nov 2012
	            //commented out this on 28 DEC 2012 because wrong place to put
	            //sqlQuery.append("AND M2.CAT_CD = 'OMC_' || J.SERVICE_TYPE || '_' || D.SERVICE_CD ");
	            //end chuething - SSL-OMC-20121123-01 23 Nov 2012

	            
	          
	    		paramMap.put("jobOrderId", jobOrderId);
	    		log.info(" *** retriveveOMCJobOrderDetail SQL *****" + sqlQuery.toString() +" paramMap "+paramMap.toString());
	    		rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);

	            while (rs.next()) {
	            	billJobVO = new BillJobVO();
	            	billJobVO.setJobId(rs.getLong("JOB_ID"));
	            	billJobVO.setJobDetId(rs.getLong("JOB_DET_ID"));
	            	billJobVO.setServiceCd(rs.getString("SERVICE_CD"));
	            	billJobVO.setSubServiceCd(rs.getString("SUB_SERVICE_CD"));
	            	billJobVO.setFromDttm(rs.getTimestamp("FROM_DTTM"));
	            	billJobVO.setToDttm(rs.getTimestamp("TO_DTTM"));
	            	billJobVO.setUnit(rs.getInt("UNIT"));
	            	billJobVO.setTotalAmt(rs.getDouble("TOTAL_AMT"));
	            	billJobVO.setLocation(rs.getString("LOCATION"));

	                // add to list
	            	jobOrderDetaillList.add(billJobVO);
	            }
	            log.info("END: *** retriveveOMCJobOrderDetail Result *****" + jobOrderDetaillList.toString());
			}  catch (NullPointerException e) {
				log.error("Exception: retriveveOMCJobOrderDetail ", e);
				throw new BusinessException("M4201");
			} catch (Exception e) {
				log.error("Exception: retriveveOMCJobOrderDetail ", e);
				throw new BusinessException("M4201");
			} finally{
				log.info("END: retriveveOMCJobOrderDetail  DAO  END");
			}


	    	return jobOrderDetaillList;
	    }
	
}
