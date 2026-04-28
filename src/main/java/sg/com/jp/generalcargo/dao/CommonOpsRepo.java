package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.util.BusinessException;

public interface CommonOpsRepo {

	public String prepareMessageHeader(String sql, String vv_cd) throws BusinessException;

	public void sendAlert(String[] approverEmail, String eMAIL_SUBJECT_LATE_ARRIVAL_WAIVER_SUBMISSION, String message)
			throws BusinessException;

}
