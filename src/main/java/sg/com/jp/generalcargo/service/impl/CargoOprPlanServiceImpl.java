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
import sg.com.jp.generalcargo.domain.GbccCargoOprPlanDet;
import sg.com.jp.generalcargo.domain.GbccCargoOprPlanVO;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.service.CargoOprPlanService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service
public class CargoOprPlanServiceImpl implements CargoOprPlanService {

	private static final Log log = LogFactory.getLog(CargoOprPlanServiceImpl.class);

	@Autowired
	private GBCCCargoRepository gbccRepo;

	@Override
	public GbccCargoOprPlanVO cc_getCargoOprPlanById(String vvCd, String stevCd, Date crDttm) throws BusinessException {
		return gbccRepo.getCargoOprPlanById(vvCd, stevCd, crDttm);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean cc_persistCargoOprPlan(GbccCargoOprPlanVO transientObject) throws BusinessException {
		boolean result = false;
		try {
			log.info("START: cc_persistCargoOprPlan service transientObject:" + transientObject.toString());
			result = gbccRepo.saveCargoOprPlan(transientObject);
			List<GbccCargoOprPlanDet> detlst = transientObject.getCargoOprPlanDetVO();
			int hatchSize = detlst.size();
			for (int i = 0; i < hatchSize; i++) {
				GbccCargoOprPlanDet detVO = (GbccCargoOprPlanDet) detlst.get(i);
				if (detVO.getId().getCreateDttm() == null)
					detVO.getId().setCreateDttm(transientObject.getCreateDttm());

				gbccRepo.addCargoOprPlanDet(detVO);
			}
			// cc_sendLateSub4COP(transientObject); cvs
		} catch (BusinessException e) {
			log.info("Exception cc_persistCargoOprPlan : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception cc_persistCargoOprPlan : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: cc_persistCargoOprPlan service result: " + result);
		}
		return result;
	}

	@Override
	public TableResult cc_getCargoOprPlan(String CustCode, String sortBy, Criteria criteria, Boolean needAllData)
			throws BusinessException {
		return gbccRepo.getCargoOprPlan(CustCode, sortBy, criteria, needAllData);
	}

	@Override
	public TableResult cc_getCargoOprPlan(String CustCode, String sortBy, Criteria criteria, Boolean needAllData,
			String ETBFrom, String ETBTo, String listAllChk) throws BusinessException {
		return gbccRepo.getCargoOprPlan(CustCode, sortBy, criteria, needAllData, ETBFrom, ETBTo, listAllChk);
	}

	@Override
	public String getCompanyName(String coCd) throws BusinessException {
		return gbccRepo.getCompanyName(coCd);
	}

}
