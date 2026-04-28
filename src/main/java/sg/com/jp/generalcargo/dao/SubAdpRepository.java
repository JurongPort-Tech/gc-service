package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.SubAdpValueObject;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface SubAdpRepository {
	public List<SubAdpValueObject> getSubADP(String esnasnnbr,Criteria criteria) throws BusinessException;
	public boolean checkEsnExist(String esnasnnbr) throws BusinessException;
	public void delADPForDPE(List<String> subAdpNbr_Vector, String userId,
			List<String> status_Cd_Vector, List<String> trucker_CoCd_Vector,
			List<String> trucker_Nm_Vector, List<String> trucker_Ic_Vector,
			List<String> trucker_Contact_Nbr_Vector, List<String> trucker_nbr_pkg_Vector) throws BusinessException;
	public void creatMultiTruckers(String esnNbr, List<TruckerValueObject> truckerList, String creat_userID, String status_Cd, String totPkg_s) throws BusinessException ;
	public String getTruckerCdByTruckerIcNo(String trcIcNo) throws BusinessException;
	public List<TruckerValueObject> getTruckerList(String esnNbr) throws BusinessException;
	public TruckerValueObject getTruckerDetails(String truckerIc) throws BusinessException;
	public UaEsnDetValueObject getEsnDetail(String esnasnnbr,String esnTransType, String coCode, String userId)throws BusinessException;
	public String getEsnTranType(String esnasnnbr) throws BusinessException;
	public int getSubADPTotal(String esnasnnbr) throws BusinessException; 

}
