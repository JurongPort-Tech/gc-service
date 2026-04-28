package sg.com.jp.generalcargo.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JasperPrint;
import sg.com.jp.generalcargo.dao.PassOutNoteRepository;
import sg.com.jp.generalcargo.dao.UatRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.MiscCodeValueObject;
import sg.com.jp.generalcargo.domain.PassOutNoteFormValueObject;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.service.PassOutNoteService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.JasperUtil;

@Service("passOutNoteService")
public class PassOutNoteServiceImpl implements PassOutNoteService {

	private static final Log log = LogFactory.getLog(PassOutNoteServiceImpl.class);
	
	@Autowired
	private UatRepository uatRepo;
	
	@Autowired
	private PassOutNoteRepository passOutNoteRepository;

	@Override
	public List<MiscCodeValueObject> getTenantCompanyList() throws BusinessException {

		return uatRepo.getTenantCompanyList();
	}

	@Override
	public String getDriverName(String driverPass) throws BusinessException {

		return uatRepo.getDriverName(driverPass);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public boolean createPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO) throws BusinessException {

		return passOutNoteRepository.createPassOutNote(passOutNoteFormVO);
	}

	@Override
	public TableResult searchPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO, Criteria criteria)
			throws BusinessException {

		return passOutNoteRepository.searchPassOutNote(passOutNoteFormVO, criteria);
	}

	@Override
	public boolean checkDeletedPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO) throws BusinessException {

		return passOutNoteRepository.checkDeletedPassOutNote(passOutNoteFormVO);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean deletePassOutNote(PassOutNoteFormValueObject passOutNoteFormVO) throws BusinessException {

		return passOutNoteRepository.deletePassOutNote(passOutNoteFormVO);
	}

	@Override
	public PassOutNoteFormValueObject viewPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO)
			throws BusinessException {

		return passOutNoteRepository.viewPassOutNote(passOutNoteFormVO);
	}
	
	public JasperPrint jasperPrint(Map<String, Object> parameters, String fileName, String nbr, List<?> records) throws Exception {
		JasperPrint jasperPrint = null;
		try {
			log.info("START: getJasperPrint "+" fileName:"+CommonUtility.deNull(fileName) +" parameters:"+ parameters
					+" nbr:"+ CommonUtility.deNull(nbr) +" records:"+records.size());
			jasperPrint = JasperUtil.jasperPrint(parameters, fileName, nbr, records);
			
			log.info("END: *** getJasperPrint Result *****" + jasperPrint.toString());
		
		} catch (Exception e) {
			log.info("Exception getJasperPrint : ", e);
			throw new Exception(e.getMessage());
		}
		return jasperPrint;
	}

	@Override
	public List<Map<String, Object>> printPassOutNote(PassOutNoteFormValueObject passOutNoteFormVO)
			throws BusinessException {
		
		return passOutNoteRepository.printPassOutNote(passOutNoteFormVO);
	}
	
	@Override
	public String getCompanyName(String coCd) throws BusinessException {
		return passOutNoteRepository.getCompanyName(coCd);
	}
	
	@Override
	public String getCompanyCode(String companyName) throws BusinessException {
		return passOutNoteRepository.getCompanyCode(companyName);
	}

}
