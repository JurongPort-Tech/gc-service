package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface GBChargeRepository {

	//StartRegion  GBChargeRepository
	 public List<ChargeableBillValueObject> getGBCharge(String refNbr, String refInd) throws BusinessException;
	 
	 public void cancelGBCharge(String refNbr, String refInd) throws BusinessException;
	 
	 public void addGBCharge(List<ChargeableBillValueObject> chargeArrayList) throws BusinessException ;
	 
	//EndRegion GBChargeRepository

}
