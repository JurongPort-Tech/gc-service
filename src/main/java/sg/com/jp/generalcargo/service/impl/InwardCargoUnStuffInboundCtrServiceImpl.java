package sg.com.jp.generalcargo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.ManifestRepository;
import sg.com.jp.generalcargo.dao.UnStuffingRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.Dept;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.HSCode;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.UnStuffingCargoValueObject;
import sg.com.jp.generalcargo.domain.UnStuffingValueObject;
import sg.com.jp.generalcargo.domain.VesselSearchResponse;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.InwardCargoUnStuffInboundCtrService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service("InwardCargoService")
public class InwardCargoUnStuffInboundCtrServiceImpl implements InwardCargoUnStuffInboundCtrService {

	@Autowired
	private UnStuffingRepository unStuffingRepo;
	@Autowired
	private ManifestRepository manifestRepo;

	// StartRegion UnStuffingHandler
	@Override
	public List<VesselVoyValueObject> getVesselVoy(String cocode) throws BusinessException {

		return unStuffingRepo.getVesselVoy(cocode);
	}

	@Override
	public List<Dept> listCompany(String keyword, Integer start, Integer limit, String type) throws BusinessException {

		return unStuffingRepo.listCompany(keyword, start, limit, type);
	}

	@Override
	public List<String> getContainerNos(String vvcode) throws BusinessException {

		return unStuffingRepo.getContainerNos(vvcode);
	}

	@Override
	public String getWaiveStatus(String varnbr, String cntrnbr, String cntrseqnbr) throws BusinessException {

		return unStuffingRepo.getWaiveStatus(varnbr, cntrnbr, cntrseqnbr);
	}

	@Override
	public String checkUnStuffClosed(String cntrno, String cntrseqno, String varno) throws BusinessException {

		return unStuffingRepo.checkUnStuffClosed(cntrno, cntrseqno, varno);
	}

	@Override
	public String getUnStuffDttm(String varnbr, String cntrnbr, String cntrseqnbr) throws BusinessException {

		return unStuffingRepo.getUnStuffDttm(varnbr, cntrnbr, cntrseqnbr);
	}

	@Override
	public TableResult getManifestList(String vvcode, String cntrno, Criteria criteria) throws BusinessException {

		return unStuffingRepo.getManifestList(vvcode, cntrno, criteria);
	}

	@Override
	public List<VesselSearchResponse> getVesselsNameBySearch(String vesselName, String cocode) throws BusinessException {
		return unStuffingRepo.getVesselsNameBySearch(vesselName, cocode);
	}

	@Override
	public List<HSCode> getHSSubCodeList(String hsCode) throws BusinessException {
		return unStuffingRepo.getHSSubCodeList(hsCode);
	}

	@Override
	public List<ManifestValueObject> getPkgList() throws BusinessException {
		return unStuffingRepo.getPkgList();
	}

	@Override
	public List<EsnListValueObject> getPkgList(String getText) throws BusinessException {
		return unStuffingRepo.getPkgList(getText);
	}

	@Override
	public TableResult getPortList(String pCode, String pDesc, Criteria criteria) throws BusinessException {
		return unStuffingRepo.getPortList(pCode, pDesc, criteria);
	}

	@Override
	public TableResult getPortList(Criteria criteria) throws BusinessException {
		return unStuffingRepo.getPortList(criteria);
	}
	// EndRegion UnStuffingHandler

	// StartRegion UnStuffingAddHandler

	@Override
	public String getHSSubCodeDes(String hsCode, String hsSubCodeFr, String hsSubCodeTo) throws BusinessException {

		return manifestRepo.getHSSubCodeDes(hsCode, hsSubCodeFr, hsSubCodeTo);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String MftInsertion(String addval, String coCd, String varno, String blno, String cntrno, String dttm,
			String waivechrg, String crgtyp, String hscd, String hscdFr, String hscdTo, String crgdesc, String mark,
			String nopkgs, String gwt, String gvol, String crgstat, String dgind, String billparty, String consNM,
			String consCoCd, String stgind, String pol, String pod, String pkgtype, String poFD)
			throws BusinessException {

		return unStuffingRepo.MftInsertion(addval, coCd, varno, blno, cntrno, dttm, waivechrg, crgtyp, hscd, hscdFr,
				hscdTo, crgdesc, mark, nopkgs, gwt, gvol, crgstat, dgind, billparty, consNM, consCoCd, stgind, pol, pod,
				pkgtype, poFD);
	}

	@Override
	public String getPortName(String portcd) throws BusinessException {

		return unStuffingRepo.getPortName(portcd);
	}

	@Override
	public String getPkgName(String pkgtype) throws BusinessException {

		return unStuffingRepo.getPkgName(pkgtype);
	}

	@Override
	public UnStuffingValueObject mftRetrieve(String blno, String varno, String seqno) throws BusinessException {

		return unStuffingRepo.mftRetrieve(blno, varno, seqno);
	}

	@Override
	public List<UnStuffingValueObject> getAddcrgList() throws BusinessException {

		return unStuffingRepo.getAddcrgList();
	}

	@Override
	public List<ManifestValueObject> getHSCodeList(String status) throws BusinessException {

		return manifestRepo.getHSCodeList(status);
	}

	@Override
	public List<String> getPorts(String cntr_seq_no) throws BusinessException {

		return unStuffingRepo.getPorts(cntr_seq_no);
	}
	// EndRegion UnStuffingAddHandler

	// StartRegion UnStuffingCancelHandler
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void mftCancel(String coCd, String seqno, String varno, String blno, String cntrNoSeqNo)
			throws BusinessException {

		unStuffingRepo.mftCancel(coCd, seqno, varno, blno, cntrNoSeqNo);
	}
	// EndRegion UnStuffingCancelHandler

	// StartRegion UnStuffingAmendHandler
	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String MftUpdation(String usrid, String coCd, String seqno, String varno, String blno, String crgtyp,
			String hscd, String hscdFr, String hscdTo, String crgdesc, String mark, String nopkgs, String gwt,
			String gvol, String crgstat, String dgind, String blParty, String stgind, String dop, String pkgtyp,
			String coname, String consCoCd, String poL, String poD, String poFD, String cntrtype, String cntrsize,
			String cntr1, String cntr2, String cntr3, String cntrno_sqno) throws BusinessException {

		return unStuffingRepo.MftUpdation(usrid, coCd, seqno, varno, blno, crgtyp, hscd, hscdFr, hscdTo, crgdesc, mark,
				nopkgs, gwt, gvol, crgstat, dgind, blParty, stgind, dop, pkgtyp, coname, consCoCd, poL, poD, poFD,
				cntrtype, cntrsize, cntr1, cntr2, cntr3, cntrno_sqno);
	}

	@Override
	public String getScheme(String voy_nbr) throws BusinessException {

		return unStuffingRepo.getScheme(voy_nbr);
	}

	@Override
	public String getSchemeInd(String voy_nbr) throws BusinessException {

		return unStuffingRepo.getSchemeInd(voy_nbr);
	}

	@Override
	public String getClBjInd(String seqnbr) throws BusinessException {

		return unStuffingRepo.getClBjInd(seqnbr);
	}

	@Override
	public List<String> getSAacctno(String voy_nbr) throws BusinessException {

		return unStuffingRepo.getSAacctno(voy_nbr);
	}

	@Override
	public List<UnStuffingValueObject> getABacctno(String voy_nbr) throws BusinessException {

		return unStuffingRepo.getABacctno(voy_nbr);
	}

	@Override
	public String getSchemeName(String voy_nbr) throws BusinessException {

		return unStuffingRepo.getSchemeName(voy_nbr);
	}

	@Override
	public String getBPacctnbr(String voy_nbr, String seqno) throws BusinessException {

		return unStuffingRepo.getBPacctnbr(voy_nbr, seqno);
	}

	@Override
	public String getVCactnbr(String voy_nbr) throws BusinessException {

		return unStuffingRepo.getVCactnbr(voy_nbr);
	}

	@Override
	public String getABactnbr(String voy_nbr) throws BusinessException {

		return unStuffingRepo.getABactnbr(voy_nbr);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void MftAssignBillUpdate(String voy_nbr, String status, String seqno, String userid)
			throws BusinessException {

		unStuffingRepo.MftAssignBillUpdate(voy_nbr, status, seqno, userid);
	}
	
	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void MftAssignVslUpdate(String voy_nbr, String status, String userid) throws BusinessException {

		unStuffingRepo.MftAssignVslUpdate(voy_nbr, status, userid);
	}

	@Override
	public boolean chkNbrEdopkgs(String seqno, String varno, String blno) throws BusinessException {

		return unStuffingRepo.chkNbrEdopkgs(seqno, varno, blno);
	}

	@Override
	public List<UnStuffingCargoValueObject> getMftAssignCargo() throws BusinessException {

		return unStuffingRepo.getMftAssignCargo();
	}

	@Override
	public String MftAssignCrgvalCheck(String voy_nbr, String seqno) throws BusinessException {

		return unStuffingRepo.MftAssignCrgvalCheck(voy_nbr, seqno);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void MftAssignCrgvalUpdate(String voy_nbr, String crgval, String seqno, String userid)
			throws BusinessException {

		unStuffingRepo.MftAssignCrgvalUpdate(voy_nbr, crgval, seqno, userid);
	}
	// EndRegion UnStuffingAmendHandler

	// StartRegion UnStuffingCloseHandler
	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public void updateWaiverStatus(String varno, String vslInVoynbr, String cntrno, String cntrseqno, String waiversts,
			String usrid) throws BusinessException {

		unStuffingRepo.updateWaiverStatus(varno, vslInVoynbr, cntrno, cntrseqno, waiversts, usrid);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public boolean closeUnStuffing(String usrid, String vslInVoynbr, String varno, String containerno, String unStfDttm,
			String waiveUnStfChrg) throws BusinessException {

		return unStuffingRepo.closeUnStuffing(usrid, vslInVoynbr, varno, containerno, unStfDttm, waiveUnStfChrg);
	}
	// EndRegion UnStuffingCloseHandler

	// StartRegion UnStuffingBlPartyHandler
	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public UnStuffingValueObject MftAssignBlParty(String varno, String cntrno, String blPartyAcctNo)
			throws BusinessException {

		return unStuffingRepo.MftAssignBlParty(varno, cntrno, blPartyAcctNo);
	}

	@Override
	public List<String> getBlPartyList(String varno, String vslInVoynbr, String cntrno) throws BusinessException {

		return unStuffingRepo.getBlPartyList(varno, vslInVoynbr, cntrno);
	}
	// EndRegion UnStuffingBlPartyHandler

	@Override
	public List<Dept> listCompanyStart(String keyword, Integer start, Integer limit) throws BusinessException {
		return unStuffingRepo.listCompanyStart(keyword, start, limit);
	}
}
