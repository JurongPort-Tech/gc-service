package sg.com.jp.generalcargo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.ManifestRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.service.InwardTransferOfManifestService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service
public class InwardTransferOfManifestServiceImpl implements InwardTransferOfManifestService {

	@Autowired
	private ManifestRepository manifestRepo;

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public List<Object> transMftUpdate(String userID, String varnoF, String varnoT,
			List<ManifestValueObject> vseqblno) throws BusinessException {
		return manifestRepo.transMftUpdate(userID, varnoF, varnoT, vseqblno);
	}

	@Override
	public int getManifestListCount(String vvcode, String coCode, Criteria criteria) throws BusinessException {
		return manifestRepo.getManifestListCount(vvcode, coCode, criteria);
	}

}
