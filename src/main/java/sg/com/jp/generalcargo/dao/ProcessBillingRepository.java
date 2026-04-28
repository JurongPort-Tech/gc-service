package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.CargoDeclarationVO;
import sg.com.jp.generalcargo.util.BusinessException;

public interface ProcessBillingRepository {

	// StartRegion ProcessBillingRepository
	
	public List<CargoDeclarationVO> retrieveDSAChargableBillVOs(String dsaNo) throws BusinessException;
	
	// EndRegion ProcessBillingRepository

}
