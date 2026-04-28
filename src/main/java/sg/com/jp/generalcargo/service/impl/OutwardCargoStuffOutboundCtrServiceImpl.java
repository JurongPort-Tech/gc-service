package sg.com.jp.generalcargo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.StuffingRepository;
import sg.com.jp.generalcargo.domain.ContainerDetailObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.SchemeAccountObject;
import sg.com.jp.generalcargo.domain.StuffingDetailObject;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.OutwardCargoStuffOutboundCtrService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service("outwardCargoService")
public class OutwardCargoStuffOutboundCtrServiceImpl implements OutwardCargoStuffOutboundCtrService {

	@Autowired
	private StuffingRepository stuffingRepo;

	// StartRegion StuffingHandler
	@Override
	public List<VesselVoyValueObject> getVesselVoyage(String companycode) throws BusinessException {

		return stuffingRepo.getVesselVoyage(companycode);
	}

	@Override
	public List<String> getContainerNos(String vvcode) throws BusinessException {

		return stuffingRepo.getContainerNos(vvcode);
	}

	@Override
	public TableResult getContainerDetails(String vvcode, String containerno, Criteria criteria) throws BusinessException {

		return stuffingRepo.getContainerDetails(vvcode, containerno, criteria);
	}
	// EndRegion StuffingHandler

	// StartRegion StuffingAddHandler
	@Override
	public List<String> checkEdoNoPkgs(List<String> edonos, List<String> edopkgs, String vvcode, String seqno, boolean insert)
			throws BusinessException {

		return stuffingRepo.checkEdoNoPkgs(edonos, edopkgs, vvcode, seqno, insert);
	}

	@Override
	public List<ContainerDetailObject> chkESNStuffInd(List<String> esnNbr) throws BusinessException {

		return stuffingRepo.chkESNStuffInd(esnNbr);
	}

	@Override
	public List<String> checkEsnNoPkgs(List<String> esnnos, List<String> esnpkgs, String vvcode, String seqno, boolean insert)
			throws BusinessException {

		return stuffingRepo.checkEsnNoPkgs(esnnos, esnpkgs, vvcode, seqno, insert);
	}

	@Override
	public boolean isClosed(String vvcode, String contno, String cntrseqno, String seqno) throws BusinessException {

		return stuffingRepo.isClosed(vvcode, contno, cntrseqno, seqno);
	}
	
	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String insertStuffing(List<String> edonos, List<String> edopkgs, List<String> esnnos, List<String> esnpkgs, String seqno,
			String vvcode, String cntrno, String cntrseqno, String userid) throws BusinessException {

		return stuffingRepo.insertStuffing(edonos, edopkgs, esnnos, esnpkgs, seqno, vvcode, cntrno, cntrseqno, userid);
	}

	@Override
	public List<StuffingDetailObject> getStuffingDetails(String vvcode, String contno, String cntrseqno, String seqno)
			throws BusinessException {

		return stuffingRepo.getStuffingDetails(vvcode, contno, cntrseqno, seqno);
	}
	// EndRegion StuffingAddHandler

	// StartRegion StuffingAmendHandler

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String updateStuffing(List<String> edonos, List<String> edopkgs, List<String> esnnos, List<String> esnpkgs, String seqno,
			String vvcode, String cntrno, String cntrseqno, String userid) throws BusinessException {

		return stuffingRepo.updateStuffing(edonos, edopkgs, esnnos, esnpkgs, seqno, vvcode, cntrno, cntrseqno, userid);
	}

	@Override
	public List<StuffingDetailObject> getStuffingDetailsToAmend(String vvcode, String contno, String cntrseqno, String seqno)
			throws BusinessException {

		return stuffingRepo.getStuffingDetailsToAmend(vvcode, contno, cntrseqno, seqno);
	}

	@Override
	public List<String> getBillAccountNos(String vvcode) throws BusinessException {

		return stuffingRepo.getBillAccountNos(vvcode);
	}

	@Override
	public List<SchemeAccountObject> getSchemeAccountNos() throws BusinessException {

		return stuffingRepo.getSchemeAccountNos();
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void assignBillableParty(String vvcode, String contno, String cntrseqno, String seqno, String acctno,
			String userid) throws BusinessException {

		stuffingRepo.assignBillableParty(vvcode, contno, cntrseqno, seqno, acctno, userid);
	}
	// EndRegion StuffingAmendHandler

	// StartRegion StuffingCancelHandler
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean isChkDNCreated(String seqno) throws BusinessException {

		return stuffingRepo.isChkDNCreated(seqno);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean cancelGbEdoUpd(String seqno) throws BusinessException {

		return stuffingRepo.cancelGbEdoUpd(seqno);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void cancelStuffing(String vvcode, String cntrno, String cntrseqno, String seqno, String userid)
			throws BusinessException {

		stuffingRepo.cancelStuffing(vvcode, cntrno, cntrseqno, seqno, userid);
	}
	// EndRegion StuffingCancelHandler

	// StartRegion StuffingCloseHandler
	@Override
	public boolean isStuffingDttmLesser(String stuffdttm) throws BusinessException {

		return stuffingRepo.isStuffingDttmLesser(stuffdttm);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean isGbEdoUpd(String seqno) throws BusinessException {

		return stuffingRepo.isGbEdoUpd(seqno);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void closeStuffing(String vvcode, String contno, String cntrseqno, String seqno, String stuffdttm,
			String waivecharge, String userid) throws Exception {

		stuffingRepo.closeStuffing(vvcode, contno, cntrseqno, seqno, stuffdttm, waivecharge, userid);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void updateWaiverStatus(String vvcode, String contno, String cntrseqno, String seqno, String waivecharge,
			String userid) throws BusinessException {

		stuffingRepo.updateWaiverStatus(vvcode, contno, cntrseqno, seqno, waivecharge, userid);
	}
	// EndRegion StuffingCloseHandler
	
	
	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public boolean isTesnNbr(String esnno) throws BusinessException {
		return stuffingRepo.isTesnNbr(esnno) ;
	}
}
