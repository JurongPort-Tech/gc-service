package sg.com.jp.generalcargo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.jp.generalcargo.dao.CargoSummaryRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.service.InwardCargoSummaryService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service
public class InwardCargoSummaryServiceImpl implements InwardCargoSummaryService {

	@Autowired
	private CargoSummaryRepository cargoSummary;

	@Override
	public TableResult getCargoSummaryList(String strCustCode, Criteria criteria) throws BusinessException {
		return cargoSummary.getCargoSummaryList(strCustCode, criteria);
	}
	
	@Override
	public String getCompanyName(String coCd) throws BusinessException {
		return cargoSummary.getCompanyName(coCd);
	}

}
