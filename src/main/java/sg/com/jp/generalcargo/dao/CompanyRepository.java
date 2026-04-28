package sg.com.jp.generalcargo.dao;

import sg.com.jp.generalcargo.domain.CompanyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CompanyRepository {

	public CompanyValueObject getCompanyInfo(String companyCode) throws BusinessException;
}
