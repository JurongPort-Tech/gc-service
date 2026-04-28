package sg.com.jp.generalcargo.dao;

import java.sql.SQLException;
import java.util.List;

import javassist.tools.rmi.RemoteException;
import sg.com.jp.generalcargo.domain.CargoDeclarationVO;
import sg.com.jp.generalcargo.util.BusinessException;

public interface LighterBillRepository {

	//StartRegion  LighterBillRepository
	
	public List<CargoDeclarationVO> loadDSAForBillGen(String dsaNo) throws RemoteException, BusinessException, SQLException;
	
	//EndRegion LighterBillRepository

}
