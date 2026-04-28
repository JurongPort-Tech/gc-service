package sg.com.jp.generalcargo.dao;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.PassOutNoteFormValueObject;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.util.BusinessException;

public interface PassOutNoteRepository {

	public boolean createPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO) throws BusinessException;

	public TableResult searchPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO, Criteria criteria)
			throws BusinessException;

	public boolean checkDeletedPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO) throws BusinessException;

	public boolean deletePassOutNote(PassOutNoteFormValueObject passOutNoteFormVO) throws BusinessException;

	public PassOutNoteFormValueObject viewPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO)
			throws BusinessException;
	
	public List<Map<String, Object>> printPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO)
			throws BusinessException;

	public JasperPrint fillReports(InputStream is, Map<String, Object> parameters);

	public String getCompanyName(String coCd) throws BusinessException;
	
	public String getCompanyCode(String companyName) throws BusinessException;
}
