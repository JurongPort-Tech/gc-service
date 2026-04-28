package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.ViewTransferredCargo;
import sg.com.jp.generalcargo.util.BusinessException;

public interface ViewTransferredCargoRepository {
	
	//StartRegion  getTransferredCargoDetails
	
	public List<ViewTransferredCargo> getTransferredCargoDetails(String vslIndicator, String vslName, String outVoyNo,String custCode) throws BusinessException;
	
	//EndRegion getTransferredCargoDetails

}
