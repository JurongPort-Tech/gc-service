package sg.com.jp.generalcargo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.jp.generalcargo.dao.ViewTransferredCargoRepository;
import sg.com.jp.generalcargo.domain.ViewTransferredCargo;
import sg.com.jp.generalcargo.service.OutwardViewTransferredCargoService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service("viewTransferredCargoService")
public class OutwardViewTransferredCargoServiceImpl implements OutwardViewTransferredCargoService {

	@Autowired
	private ViewTransferredCargoRepository transferredCargoRepository;


	//StartRegion  getTransferredCargoDetail
	
	public List<ViewTransferredCargo> getTransferredCargoDetails(String vslIndicator, String vslName, String outVoyNo,String custCode) throws BusinessException{
		return transferredCargoRepository.getTransferredCargoDetails(vslIndicator,vslName,outVoyNo,custCode);
	}
	
	//EndRegion getTransferredCargoDetails
	
	
	
}
