package sg.com.jp.generalcargo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.GCARepository;
import sg.com.jp.generalcargo.dao.ManifestRepository;
import sg.com.jp.generalcargo.domain.HSCode;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.service.AmendHSCodeService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service
public class AmendHSCodeServiceImpl implements AmendHSCodeService {

	@Autowired
	private ManifestRepository manifestRepo;

	@Autowired
	private GCARepository gcaRepo;

	@Override
	public List<String> getEdoDetails(String edoNo) throws BusinessException {

		return gcaRepo.getEdoDetails(edoNo);
	}

	@Override
	public List<String> getEsnDetails(String esnNo) throws BusinessException {

		return gcaRepo.getEsnDetails(esnNo);
	}

	@Override
	public List<ManifestValueObject> getHSCodeList(String status) throws BusinessException {

		return manifestRepo.getHSCodeList(status);
	}

	@Override
	public List<String> getEdoDetails(String edoNo, String hsCode, String hsCodeFrom, String hsCodeTo,
			String hsSubCodeDesc) throws BusinessException {

		return gcaRepo.getEdoDetails(edoNo, hsCode, hsCodeFrom, hsCodeTo, hsSubCodeDesc);
	}

	@Override
	public List<String> getEsnDetails(String esnNo, String hsCode, String hsCodeFrom, String hsCodeTo,
			String hsSubCodeDesc) throws BusinessException {

		return gcaRepo.getEsnDetails(esnNo, hsCode, hsCodeFrom, hsCodeTo, hsSubCodeDesc);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean updateManifestGCAHsCode(String mftSeqNbr, String hsCode, String hsCodeFrom, String hsCodeTo)
			throws BusinessException {

		return gcaRepo.updateManifestGCAHsCode(mftSeqNbr, hsCode, hsCodeFrom, hsCodeTo);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean updateEsnGCAHsCode(String esnNbr, String hsCode, String hsCodeFrom, String hsCodeTo)
			throws BusinessException {

		return gcaRepo.updateEsnGCAHsCode(esnNbr, hsCode, hsCodeFrom, hsCodeTo);
	}

	@Override
	public List<HSCode> getHSSubCodeList(String hsCd) throws BusinessException {

		return manifestRepo.getHSSubCodeList(hsCd);
	}

}
