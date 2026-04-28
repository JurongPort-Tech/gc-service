package sg.com.jp.generalcargo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.VslProdlistRepo;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VslProdVO;
import sg.com.jp.generalcargo.service.GCOpsCloseVesselProdService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service("closeVesselProdService")
public class GCOpsCloseVesselProdServiceImpl implements GCOpsCloseVesselProdService {

	@Autowired
	private VslProdlistRepo vslProdlistRepo;

	@Override
	public String getClosedVslDtls(String vvcd) throws BusinessException {
		return vslProdlistRepo.getClosedVslDtls(vvcd);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String closeVessel(String vvcd) throws BusinessException {
		return vslProdlistRepo.closeVessel(vvcd);
	}

	@Override
	public TableResult getClosedVessels(String selSchmDesc, Criteria criteria) throws BusinessException {
		return vslProdlistRepo.getClosedVessels(selSchmDesc, criteria);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void updateVesselInfo(String vvcd, int noOfGangs, int wrkHatches, String delayOfWrk, String remarks)
			throws BusinessException {
		vslProdlistRepo.updateVesselInfo(vvcd, noOfGangs, wrkHatches, delayOfWrk, remarks);

	}

	@Override
	public List<String> getDelayOfWork() throws BusinessException {
		return vslProdlistRepo.getDelayOfWork();
	}

	@Override
	public List<VslProdVO> getVesselSchemeCode() throws BusinessException {
		return vslProdlistRepo.getVesselSchemeCode();
	}

	@Override
	public List<VslProdVO> getUpdatedVslDtls(String vvcd) throws BusinessException {
		return vslProdlistRepo.getUpdatedVslDtls(vvcd);
	}

}
