package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.JPPSAValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface JPPsaRepository {
	
	public String updateIGD(String filename, String VoyageNo, String mode, String status) throws BusinessException;
	public List<JPPSAValueObject> searchByVoyage(String VoyageNo, String mode) throws BusinessException;
	public String insertIGD(JPPSAValueObject jpvalueObj) throws BusinessException;

}
