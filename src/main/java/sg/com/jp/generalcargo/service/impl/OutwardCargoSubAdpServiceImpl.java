package sg.com.jp.generalcargo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.SubAdpRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.SubAdpValueObject;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.service.OutwardCargoSubAdpService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service
public class OutwardCargoSubAdpServiceImpl implements OutwardCargoSubAdpService{
	
	@Autowired
	private SubAdpRepository subAdp;

	@Override
	public List<SubAdpValueObject> getSubADP(String esnasnnbr,Criteria criteria) throws BusinessException {
		
		return subAdp.getSubADP(esnasnnbr,criteria);
	}

	@Override
	public boolean checkEsnExist(String esnasnnbr) throws BusinessException {
		
		return subAdp.checkEsnExist(esnasnnbr);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void delADPForDPE(List<String> subAdpNbr_Vector, String userId, List<String> status_Cd_Vector,
			List<String> trucker_CoCd_Vector, List<String> trucker_Nm_Vector, List<String> trucker_Ic_Vector,
			List<String> trucker_Contact_Nbr_Vector, List<String> trucker_nbr_pkg_Vector) throws BusinessException {
		subAdp.delADPForDPE(subAdpNbr_Vector, userId, status_Cd_Vector, trucker_CoCd_Vector, trucker_Nm_Vector, trucker_Ic_Vector, trucker_Contact_Nbr_Vector, trucker_nbr_pkg_Vector);
		
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void creatMultiTruckers(String esnNbr, List<TruckerValueObject> truckerList, String creat_userID, String status_Cd, String totPkg_s)
			throws BusinessException {
		
		subAdp.creatMultiTruckers(esnNbr, truckerList, creat_userID, status_Cd, totPkg_s);
	}

	@Override
	public String getTruckerCdByTruckerIcNo(String trcIcNo) throws BusinessException {
		
		return subAdp.getTruckerCdByTruckerIcNo(trcIcNo);
	}

	@Override
	public List<TruckerValueObject> getTruckerList(String esnNbr) throws BusinessException {
		
		return subAdp.getTruckerList(esnNbr);
	}

	@Override
	public TruckerValueObject getTruckerDetails(String truckerIc) throws BusinessException {
		
		return subAdp.getTruckerDetails(truckerIc);
	}

	@Override
	public UaEsnDetValueObject getEsnDetail(String esnasnnbr, String esnTransType, String coCode, String userId)
			throws BusinessException {
		
		return subAdp.getEsnDetail(esnasnnbr, esnTransType, coCode, userId);
	}

	@Override
	public String getEsnTranType(String esnasnnbr) throws BusinessException {
		
		return subAdp.getEsnTranType(esnasnnbr);
	}

	@Override
	public int getSubADPTotal(String esnasnnbr) throws BusinessException {
		
		return subAdp.getSubADPTotal(esnasnnbr);
	}
	
	
	
	
	

}
