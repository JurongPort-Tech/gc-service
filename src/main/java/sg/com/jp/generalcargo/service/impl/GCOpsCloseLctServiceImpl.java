package sg.com.jp.generalcargo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.CCEventLogRepo;
import sg.com.jp.generalcargo.dao.CloseLctRepo;
import sg.com.jp.generalcargo.dao.TransactionLoggerRepo;
import sg.com.jp.generalcargo.domain.CloseLctValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.service.GCOpsCloseLctService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service("closeLctService")
public class GCOpsCloseLctServiceImpl implements GCOpsCloseLctService {

	@Autowired
	private CCEventLogRepo ccEventLogRepo;

	@Autowired
	private CloseLctRepo closeLctRepo;

	@Autowired
	private TransactionLoggerRepo transactionLoggerRepo;

	@Override
	public void insertOpsCCEventLogForLct(CloseLctValueObject vo, String userId) throws BusinessException {
		ccEventLogRepo.insertOpsCCEventLogForLct(vo, userId);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void closeLct(String vv_cds, String userId) throws BusinessException {
		closeLctRepo.closeLct(vv_cds, userId);
	}

	@Override
	public List<CloseLctValueObject> listLct(String vv_cds) throws BusinessException {
		return closeLctRepo.listLct(vv_cds);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void openLct(String vv_cds, String userId) throws BusinessException {
		closeLctRepo.openLct(vv_cds, userId);
	}

	@Override
	public List<CloseLctValueObject> listVessel() throws BusinessException {
		return closeLctRepo.listVessel();
	}

	@Override
	public TableResult listLct(String vslName, String inVoNo, String outVoNo, int searchMode, Criteria criteria)
			throws BusinessException {
		return closeLctRepo.listLct(vslName, inVoNo, outVoNo, searchMode, criteria);
	}

	@Override
	public void TriggerLct(CloseLctValueObject vo, String userId) throws Exception {
		transactionLoggerRepo.TriggerLct(vo, userId);
	}

}
