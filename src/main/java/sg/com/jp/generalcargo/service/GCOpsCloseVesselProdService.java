package sg.com.jp.generalcargo.service;

import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VslProdVO;
import sg.com.jp.generalcargo.util.BusinessException;

public interface GCOpsCloseVesselProdService {
	public String getClosedVslDtls(String vvcd) throws BusinessException;

	public String closeVessel(String vvcd) throws BusinessException;

	public TableResult getClosedVessels(String selSchmDesc, Criteria criteria) throws BusinessException;

	public void updateVesselInfo(String vvcd, int noOfGangs, int wrkHatches, String delayOfWrk, String remarks)
			throws BusinessException;

	public List<String> getDelayOfWork() throws BusinessException;

	public List<VslProdVO> getVesselSchemeCode() throws BusinessException;

	public List<VslProdVO> getUpdatedVslDtls(String vvcd) throws BusinessException;

}
