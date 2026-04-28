package sg.com.jp.generalcargo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.ReExportRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ReExportValueObject;
import sg.com.jp.generalcargo.service.ReExportService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service("reExportService")
public class ReExportServiceImpl implements ReExportService{

	@Autowired
	private ReExportRepository reExportRepo;
	
	@Override
	public List<ReExportValueObject> getVesselVoy(String coCd) throws BusinessException {
		
		return reExportRepo.getVesselVoy(coCd);
	}

	@Override
	public List<ReExportValueObject> getManifestList(String selVoyno, String coCd,Criteria criteria) throws BusinessException {
		
		return reExportRepo.getManifestList(selVoyno, coCd,criteria);
	}

	@Override
	public String checkReExportStatus(String mftSeqNo, String coCd) throws BusinessException {
		
		return reExportRepo.checkReExportStatus(mftSeqNo, coCd);
	}

	@Override
	public boolean chkPortCode(String portL) throws BusinessException {
		
		return reExportRepo.chkPortCode(portL);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String updateReExportDetails(String mftSeqNo, String portL, String userId, String coCd)
			throws BusinessException {
		
		return reExportRepo.updateReExportDetails(mftSeqNo, portL, userId, coCd);
	}

	@Override
	public List<ReExportValueObject> getPortList() throws BusinessException {
		
		return reExportRepo.getPortList();
	}
	
	public int getManifestListCount(String vvcode,String coCd,Criteria criteria) throws BusinessException{
		return reExportRepo.getManifestListCount(vvcode, coCd, criteria);
	}

}
