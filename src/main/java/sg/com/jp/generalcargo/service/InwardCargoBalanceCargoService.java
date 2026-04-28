package sg.com.jp.generalcargo.service;

import java.util.List;

import sg.com.jp.generalcargo.domain.BalanceCargoVo;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.util.BusinessException;

public interface InwardCargoBalanceCargoService {

	List<BalanceCargoVo> getVesselListForDPE(Criteria criteria) throws BusinessException;

	List<BalanceCargoVo> getCompanyList() throws BusinessException;

	public TableData getOutStandingCargoList(Criteria criteria) throws BusinessException;

	public TableData getCompletedDeliveryCargoList(Criteria criteria) throws BusinessException;

	public int updateCargoBalanceStatus(String updateBlNbr, String vesselVvCd, long parseLong, String actionRemarks,
			String userId, String dateTime) throws BusinessException;

	public int updateCargoBalanceStatus(long updateEdoAsnNbr, long balancePkgs, String actionRemarks, String userId,
			String dateTime) throws BusinessException;

}
