package sg.com.jp.generalcargo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.DocSubAuthurDao;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DocSubAuthorValueObject;
import sg.com.jp.generalcargo.service.DocSubAuthurService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service("DocSubAuthurService")
public class DocSubAuthurServiceImpl implements DocSubAuthurService {

	@Autowired
	private DocSubAuthurDao docSubAuthurDao;

	@Override
	public List<DocSubAuthorValueObject> getVesselVoy(String coCd) throws BusinessException {
		return docSubAuthurDao.getVesselVoy(coCd);
	}

	@Override
	public List<DocSubAuthorValueObject> getVesselList(String selVvcd, Criteria criteria) throws BusinessException {
		return docSubAuthurDao.getVesselList(selVvcd, criteria);
	}

	@Override
	public String getAuthorParty(String selVvcd) throws BusinessException {
		return docSubAuthurDao.getAuthorParty(selVvcd);
	}

	@Override
	public String checkVesselStatus(String vvcd) throws BusinessException {
		return docSubAuthurDao.checkVesselStatus(vvcd);
	}

	@Override
	public String getCustomerNbr(String docsubtdbcrnbr) throws BusinessException {
		return docSubAuthurDao.getCustomerNbr(docsubtdbcrnbr);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateADSDetails(String strcustcd, String userId, String vvcd, List<String> docsubauthorvector,
			String vslnm, String invoynbr) throws BusinessException {
		docSubAuthurDao.updateADSDetails(strcustcd, userId, vvcd, docsubauthorvector, vslnm, invoynbr);

	}

	@Override
	public List<DocSubAuthorValueObject> getVesselDetails(String vvcd) throws BusinessException {
		return docSubAuthurDao.getVesselDetails(vvcd);
	}

	public int getVesselListCount(String selVvcd, Criteria criteria) throws BusinessException {
		return docSubAuthurDao.getVesselListCount(selVvcd, criteria);
	}

}
