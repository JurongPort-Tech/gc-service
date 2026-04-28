package sg.com.jp.generalcargo.service;

import java.util.List;

import sg.com.jp.generalcargo.domain.ViewTransferredCargo;
import sg.com.jp.generalcargo.util.BusinessException;

public interface OutwardViewTransferredCargoService {

	//StartRegion  getTransferredCargoDetails
	
	public List<ViewTransferredCargo> getTransferredCargoDetails(String vslIndicator, String vslName, String outVoyNo,String custCode) throws BusinessException;
	
	//EndRegion getTransferredCargoDetails

}
