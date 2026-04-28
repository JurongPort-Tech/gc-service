package sg.com.jp.generalcargo.dao;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;
import sg.com.jp.generalcargo.domain.MiscCodeValueObject;
import sg.com.jp.generalcargo.domain.UatFormValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface UatRepository {

	public List<MiscCodeValueObject> getTenantCompanyList() throws BusinessException;

	public String getDriverName(String driverPass) throws BusinessException;

	public UatFormValueObject searchUat(UatFormValueObject uatFormVO) throws BusinessException;

	public boolean createUat(UatFormValueObject uatFormVO) throws BusinessException;

	public boolean checkDeleted(UatFormValueObject uatFormVO) throws BusinessException;

	public boolean deleteUat(UatFormValueObject uatFormVO) throws BusinessException;

	public UatFormValueObject viewUat(UatFormValueObject uatFormVO) throws BusinessException;

	public String getCompanyName(String coCd) throws BusinessException;

	public JasperPrint fillReports(InputStream is, Map<String, Object> parameters);
}
