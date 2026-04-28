package sg.com.jp.generalcargo.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.GBCCCargoRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.GbccCargoTimesheetVO;
import sg.com.jp.generalcargo.domain.MiscTypeCode;
import sg.com.jp.generalcargo.domain.StevedoreCompany;
import sg.com.jp.generalcargo.domain.VesselCall;
import sg.com.jp.generalcargo.service.CargoTimeSheetService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service
public class CargoTimeSheetServiceImpl implements CargoTimeSheetService {

	private static final Log log = LogFactory.getLog(CargoTimeSheetServiceImpl.class);
	@Autowired
	private GBCCCargoRepository cargoDao;

	@Override
	public List<MiscTypeCode> cc_getMiscTypeCode(String catCd) throws BusinessException {
		return cargoDao.getMiscTypeCode(catCd);
	}

	@Override
	public GbccCargoTimesheetVO cc_getCargoTimeSheetById(String vvCd, String stevCoCd, Date crDttm)
			throws BusinessException {
		return cargoDao.getCargoTimeSheetById(vvCd, stevCoCd, crDttm);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean cc_persistCargoTimeSheet(GbccCargoTimesheetVO transientObject) throws BusinessException {
		boolean result = false;
		try {
			log.info("START: cc_persistCargoTimeSheet services "+" transientObject:"+transientObject.toString() );

			result = cargoDao.saveCargoTimeSheet(transientObject);
			// cc_sendLateSub4CTM(transientObject);
			
			log.info("END: *** cc_persistCargoTimeSheet Result *****" + result);
		} catch (BusinessException e) {
			log.info("Exception cc_persistCargoTimeSheet : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception cc_persistCargoTimeSheet : ", e);
			throw new BusinessException("M4201");
		}
		return result;
	}

	@Override
	public List<VesselCall> cc_getCargoVesselCall(String custCd) throws BusinessException {
		return cargoDao.getCargoVesselCall(custCd);
	}

	@Override
	public List<StevedoreCompany> cc_getCargoStevedore() throws BusinessException {
		return cargoDao.getCargoStevedore();
	}

	@Override
	public List<String> cc_getCargoBerth(String custCd) throws BusinessException {
		return cargoDao.getCargoBerth(custCd);
	}

	@Override
	public List<GbccCargoTimesheetVO> cc_getCargoTimeSheet(String CustCode, String vvCd, String stevCoCd, String berthNo, String sortBy,
			Criteria criteria, Boolean needAllData) throws BusinessException {

		return cargoDao.getCargoTimeSheet(CustCode, vvCd, stevCoCd, berthNo, sortBy, criteria, needAllData);
	}

}
