package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DocSubAuthorValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface DocSubAuthurDao {

	List<DocSubAuthorValueObject> getVesselVoy(String coCd) throws BusinessException;

	List<DocSubAuthorValueObject> getVesselList(String selVvcd, Criteria criteria) throws BusinessException;

	String getAuthorParty(String selVvcd) throws BusinessException;

	String checkVesselStatus(String vvcd) throws BusinessException;

	String getCustomerNbr(String docsubtdbcrnbr) throws BusinessException;

	void updateADSDetails(String strcustcd, String userId, String vvcd, List<String> docsubauthorvector, String vslnm,
			String invoynbr) throws BusinessException;

	List<DocSubAuthorValueObject> getVesselDetails(String vvcd) throws BusinessException;

	public int getVesselListCount(String vvcode, Criteria criteria) throws BusinessException;

}
