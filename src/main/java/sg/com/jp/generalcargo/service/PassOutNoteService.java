package sg.com.jp.generalcargo.service;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.MiscCodeValueObject;
import sg.com.jp.generalcargo.domain.PassOutNoteFormValueObject;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.util.BusinessException;

public interface PassOutNoteService {

	public List<MiscCodeValueObject> getTenantCompanyList() throws BusinessException;

	public String getDriverName(String driverPass) throws BusinessException;

	public boolean createPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO) throws BusinessException;

	public TableResult searchPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO, Criteria criteria)
			throws BusinessException;

	public boolean checkDeletedPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO) throws BusinessException;

	public boolean deletePassOutNote(PassOutNoteFormValueObject passOutNoteFormVO) throws BusinessException;

	public PassOutNoteFormValueObject viewPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO)
			throws BusinessException;

	public JasperPrint jasperPrint(Map<String, Object> parameters, String fileName, String nbr, List<?> records) throws BusinessException, Exception;
	
	public List<Map<String, Object>> printPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO) throws BusinessException;

	public String getCompanyName(String cbCompany) throws BusinessException;
	
	public String getCompanyCode(String companyName) throws BusinessException;


}
