package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.BalanceCargoVo;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.util.BusinessException;

public interface BalanceCargoRepository {

	// Region Cargo Balance
	List<BalanceCargoVo> getVesselListForDPE(Criteria criteria) throws BusinessException;

	List<BalanceCargoVo> getCompanyList() throws BusinessException;

	public TableData getOutStandingCargoList(Criteria criteria) throws BusinessException;

	public TableData getCompletedDeliveryCargoList(Criteria criteria) throws BusinessException;

	int updateCargoBalanceStatus(String blNbr, String vesselVvCd, long balancePackages, String actionRemarks,
			String userId, String updateDttm) throws BusinessException;

	int updateCargoBalanceStatus(long esnAsnNbr, long balancePackages, String actionRemarks, String userId,
			String updateDttm) throws BusinessException;

}
