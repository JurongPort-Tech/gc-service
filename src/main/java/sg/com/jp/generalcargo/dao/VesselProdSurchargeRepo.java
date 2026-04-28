package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.domain.CustVslProdValueObject;
import sg.com.jp.generalcargo.domain.PubVslProdValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface VesselProdSurchargeRepo {

	public PubVslProdValueObject getPublishedRate(Double tonnage, String vvcd) throws BusinessException;

	public CustVslProdValueObject getCustomisedRate(Double tonnage, String custCd, String vvcd)
			throws BusinessException;
}
