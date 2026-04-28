package sg.com.jp.generalcargo.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.GBCCCargoRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.GbccCargoOpenBalVO;
import sg.com.jp.generalcargo.domain.GbccCargoOprPlanVO;
import sg.com.jp.generalcargo.domain.MiscTypeCode;
import sg.com.jp.generalcargo.service.CargoOpenBalService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.ConstantUtil;

@Service
public class CargoOpenBalServiceImpl implements CargoOpenBalService {

	@Autowired
	private GBCCCargoRepository gbccRepo;

	@Override
	public List<MiscTypeCode> cc_getMiscTypeCodeDelayReason() throws BusinessException {
		String catCd = ConstantUtil.MISCTYPECD_DELAY_REASON;
		return cc_getMiscTypeCode(catCd);
	}

	private List<MiscTypeCode> cc_getMiscTypeCode(String catCd) throws BusinessException {
		return gbccRepo.getMiscTypeCode(catCd);
	}

	@Override
	public GbccCargoOpenBalVO cc_getCargoOpenBalById(String vvCd, String stevCd) throws BusinessException {
		return gbccRepo.getCargoOpenBalById(vvCd, stevCd);
	}

	@Override
	public GbccCargoOprPlanVO cc_getCargoOprPlanById(String vvCd, String stevCd, Date crDttm) throws BusinessException {
		return gbccRepo.getCargoOprPlanById(vvCd, stevCd, crDttm);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean cc_persistCargoOpenBal(GbccCargoOpenBalVO transientObject) throws BusinessException {
		return gbccRepo.saveCargoOpenBal(transientObject);
	}
	
	@Override
	public List<GbccCargoOpenBalVO> cc_getCargoOpenBal(String CustCode, String sortBy, Criteria criteria, Boolean needAllData) throws BusinessException {
		return gbccRepo.getCargoOpenBal("", CustCode, sortBy, criteria, needAllData, "", "", "");
	}
	
	@Override
	public List<GbccCargoOpenBalVO> cc_getCargoOpenBal(String CustCode, String sortBy, Criteria criteria, Boolean needAllData, String ATBFrom, String ATBTo, String listAllChk) throws BusinessException {
		return gbccRepo.getCargoOpenBal("", CustCode, sortBy, criteria, needAllData, ATBFrom, ATBTo, listAllChk);
	}

}
