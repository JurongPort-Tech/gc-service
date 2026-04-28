package sg.com.jp.generalcargo.service;

import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface InwardTransferOfManifestService {

	//Transfer of Manifest
	public List<Object> transMftUpdate(String userID, String varnoF, String varnoT, List<ManifestValueObject> vseqblno) throws BusinessException;
	
	public int getManifestListCount(String vvcode, String coCode,Criteria criteria) throws BusinessException;

}
