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
import sg.com.jp.generalcargo.domain.GbccCargoTallysheetVO;
import sg.com.jp.generalcargo.domain.GbccViewVvStevedore;
import sg.com.jp.generalcargo.domain.MiscTypeCode;
import sg.com.jp.generalcargo.domain.StevedoreCompany;
import sg.com.jp.generalcargo.domain.VesselCall;
import sg.com.jp.generalcargo.service.CargoTallySheetService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;

@Service
public class CargoTallySheetServiceImpl implements CargoTallySheetService {

	@Autowired
	private GBCCCargoRepository cargoDao;

	private static final Log log = LogFactory.getLog(CargoTallySheetServiceImpl.class);

	@Override
	public List<MiscTypeCode> cc_getMiscTypeCode(String catCd) throws BusinessException {
		return cargoDao.getMiscTypeCode(catCd);
	}

	@Override
	public GbccCargoTallysheetVO cc_getCargoTallySheetById(String custCd, String vvCd, Date crDttm, String oprType,
			Integer hatchNo, String stevCd) throws BusinessException {
		return cargoDao.getCargoTallySheetById(custCd, vvCd, crDttm, oprType, hatchNo, stevCd);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean cc_persistCargoTallySheet(GbccCargoTallysheetVO transientObject) throws BusinessException {
		boolean result = false;
		try {
			log.info("START: cc_persistCargoTallySheet service "+" transientObject:"+transientObject.toString());
			result = cargoDao.saveCargoTallySheet(transientObject);
			// cc_sendLateSub4CTL(transientObject);
			log.info("END: *** cc_persistCargoTallySheet Result *****" + result);
		} catch (BusinessException e) {
			log.info("Exception cc_persistCargoTallySheet : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception cc_persistCargoTallySheet : ", e);
			throw new BusinessException("M4201");
		}
		return result;
	}

	@Override
	public List<VesselCall> cc_getCargoOprVesselCall(String custCd) throws BusinessException {
		return cargoDao.getCargoOprVesselCall(custCd);
	}

	@Override
	public List<StevedoreCompany> cc_getCargoOprStevedore() throws BusinessException {
		return cargoDao.getCargoOprStevedore();
	}

	@Override
	public List<String> cc_getCargoOprBerth(String custCd) throws BusinessException {
		return cargoDao.getCargoOprBerth(custCd);
	}

	@Override
	public List<GbccCargoTallysheetVO> cc_getCargoTallySheet(String CustCode, String vvCd, String berthNo, String oprType, Integer hatchNo,
			String sortBy, Criteria criteria, Boolean needAllData) throws BusinessException {
		return cargoDao.getCargoTallySheet(CustCode, vvCd, berthNo, oprType, hatchNo, sortBy, criteria, needAllData);
	}

	@Override
	public String cc_checkCargoTallysheetAdd(String vvCd, String stevCd, String coCd) throws BusinessException {
		log.info("start cc_checkCargoTallysheetAdd");
		try {
			log.info("START: cc_checkCargoTallysheetAdd service "+" vvCd:"+ CommonUtility.deNull(vvCd) +" stevCd:"+ CommonUtility.deNull(stevCd)
			+" coCd:"+ CommonUtility.deNull(coCd) );
			List<GbccViewVvStevedore> stev = cargoDao.getViewVvStevedoreByVvCd(vvCd, coCd);
			if (stev != null) {
				if (stev.size() == 0)
					return ConstantUtil.CargoOprErr_NOSTEV;

				if (!stevCd.equalsIgnoreCase("")) {
					boolean stevokey = false;
					for (int i = 0; i < stev.size(); i++) {
						GbccViewVvStevedore s = (GbccViewVvStevedore) stev.get(i);
						if (s.getId().getStevCoCd().equalsIgnoreCase(stevCd)) {
							stevokey = true;
							break;
						}
					}
					if (!stevokey)
						return ConstantUtil.CargoOprErr_NOSTEV;
				}
			}
		} catch (Exception e) {
			log.info("Exception cc_checkCargoTallysheetAdd : ", e);
		}
		log.info("end cc_checkCargoTallysheetAdd");
		return "";
	}

}
