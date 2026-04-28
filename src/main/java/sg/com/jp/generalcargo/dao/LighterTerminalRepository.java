package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.domain.CargoDeclarationVO;
import sg.com.jp.generalcargo.util.BusinessException;

public interface LighterTerminalRepository {

	//StartRegion  LighterTerminalRepository
	
	public CargoDeclarationVO getCargoDeclarationByDsaNbr(String dsa_nbr) throws BusinessException;
	
	//EndRegion LighterTerminalRepository

}
