package sg.com.jp.generalcargo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.BalanceCargoRepository;
import sg.com.jp.generalcargo.domain.BalanceCargoVo;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.service.InwardCargoBalanceCargoService;
import sg.com.jp.generalcargo.util.BusinessException;


@Service("InwardCargoBalanceCargoService")
public class InwardCargoBalanceCargoServiceImpl implements InwardCargoBalanceCargoService {

	@Autowired
	BalanceCargoRepository balanceCargoRepo;

	
	@Override
	public List<BalanceCargoVo> getVesselListForDPE(Criteria criteria) throws BusinessException {
		return balanceCargoRepo.getVesselListForDPE(criteria); 
	}
	
	@Override
	public List<BalanceCargoVo> getCompanyList() throws BusinessException {
		return balanceCargoRepo.getCompanyList();
	}
	
	@Override
	public TableData getOutStandingCargoList(Criteria criteria) throws BusinessException {
		return balanceCargoRepo.getOutStandingCargoList(criteria);
	}

	@Override
	public TableData getCompletedDeliveryCargoList(Criteria criteria) throws BusinessException {
		return balanceCargoRepo.getCompletedDeliveryCargoList(criteria);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public int updateCargoBalanceStatus(String updateBlNbr, String vesselVvCd, long balancePackages, String actionRemarks,
			String userId, String dateTime) throws BusinessException {
		return balanceCargoRepo.updateCargoBalanceStatus(updateBlNbr, vesselVvCd, balancePackages, actionRemarks, userId, dateTime);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public int updateCargoBalanceStatus(long updateEdoAsnNbr, long balancePkgs, String actionRemarks, String userId,
			String dateTime) throws BusinessException {
		return balanceCargoRepo.updateCargoBalanceStatus(updateEdoAsnNbr, balancePkgs, actionRemarks, userId, dateTime);
	}

}
