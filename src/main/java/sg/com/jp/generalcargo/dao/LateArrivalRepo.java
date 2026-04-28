package sg.com.jp.generalcargo.dao;

import java.math.BigDecimal;

import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.domain.GbArrivalWaiver;

public interface LateArrivalRepo {
	
	public void sendSubmissionAlert(String vvCd, String[] approverEmail) throws BusinessException;

	public GbArrivalWaiver retrieveGbArrivalWaiver(String vvCd) throws  BusinessException ;
	
	public BigDecimal calculateGbArrivalWaiverAmount(String vvCd) throws BusinessException;
	
	public void updateGbArrivalWaiver(String vvCd,
	        String gbArrivalWaiverInd, BigDecimal gbArrivalWaiverAmount,
	        String lastModifyUserId) throws  BusinessException;
	
	 public String[] retrieveApproverEmail(double gbArrivalWaiverAmount) throws BusinessException ;
}
