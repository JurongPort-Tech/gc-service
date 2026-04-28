package sg.com.jp.generalcargo.service;

import java.util.List;

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
import sg.com.jp.generalcargo.util.BusinessException;

public interface InwardCargoUnStuffInboundCtrService {

	// StartRegion UnStuffingHandler
	public List<VesselVoyValueObject> getVesselVoy(String cocode) throws BusinessException;

	public List<String> getContainerNos(String vvcode) throws BusinessException;

	public String getWaiveStatus(String varnbr, String cntrnbr, String cntrseqnbr) throws BusinessException;

	public String checkUnStuffClosed(String cntrno, String cntrseqno, String varno) throws BusinessException;

	public String getUnStuffDttm(String varnbr, String cntrnbr, String cntrseqnbr) throws BusinessException;

	public TableResult getManifestList(String vvcode, String cntrno, Criteria criteria) throws BusinessException;

	public List<Dept> listCompany(String keyword, Integer start, Integer limit, String type) throws BusinessException;

	public List<VesselSearchResponse> getVesselsNameBySearch(String vesselName, String cocode) throws BusinessException;

	public List<HSCode> getHSSubCodeList(String hsCode) throws BusinessException;

	public List<ManifestValueObject> getPkgList() throws BusinessException;

	public List<EsnListValueObject> getPkgList(String getText) throws BusinessException;

	public TableResult getPortList(String pCode, String pDesc, Criteria criteria) throws BusinessException;

	public TableResult getPortList( Criteria criteria) throws BusinessException;
	// EndRegion UnStuffingHandler

	// StartRegion UnStuffingAddHandler
	public String getHSSubCodeDes(String hsCode, String hsSubCodeFr, String hsSubCodeTo) throws BusinessException;

	public String MftInsertion(String addval, String coCd, String varno, String blno, String cntrno, String dttm,
			String waivechrg, String crgtyp, String hscd, String hscdFr, String hscdTo, String crgdesc, String mark,
			String nopkgs, String gwt, String gvol, String crgstat, String dgind, String billparty, String consNM,
			String consCoCd, String stgind, String pol, String pod, String pkgtype, String poFD)
			throws BusinessException;

	public String getPortName(String portcd) throws BusinessException;

	public String getPkgName(String pkgtype) throws BusinessException;

	public UnStuffingValueObject mftRetrieve(String blno, String varno, String seqno) throws BusinessException;

	public List<UnStuffingValueObject> getAddcrgList() throws BusinessException;

	public List<ManifestValueObject> getHSCodeList(String status) throws BusinessException;

	public List<String> getPorts(String cntr_seq_no) throws BusinessException;
	// EndRegion UnStuffingAddHandler

	// StartRegion UnStuffingCancelHandler
	public void mftCancel(String coCd, String seqno, String varno, String blno, String cntrNoSeqNo)
			throws BusinessException;
	// EndRegion UnStuffingCancelHandler

	// StartRegion UnStuffingAmendHandler
	public String MftUpdation(String usrid, String coCd, String seqno, String varno, String blno, String crgtyp,
			String hscd, String hscdFr, String hscdTo, String crgdesc, String mark, String nopkgs, String gwt,
			String gvol, String crgstat, String dgind, String blParty, String stgind, String dop, String pkgtyp,
			String coname, String consCoCd, String poL, String poD, String poFD, String cntrtype, String cntrsize,
			String cntr1, String cntr2, String cntr3, String cntrno_sqno) throws BusinessException;

	public String getScheme(String voy_nbr) throws BusinessException;

	public String getSchemeInd(String voy_nbr) throws BusinessException;

	public String getClBjInd(String seqnbr) throws BusinessException;

	public List<String> getSAacctno(String voy_nbr) throws BusinessException;

	public List<UnStuffingValueObject> getABacctno(String voy_nbr) throws BusinessException;

	public String getSchemeName(String voy_nbr) throws BusinessException;

	public String getBPacctnbr(String voy_nbr, String seqno) throws BusinessException;

	public String getVCactnbr(String voy_nbr) throws BusinessException;

	public String getABactnbr(String voy_nbr) throws BusinessException;

	public void MftAssignBillUpdate(String voy_nbr, String status, String seqno, String userid)
			throws BusinessException;

	public void MftAssignVslUpdate(String voy_nbr, String status, String userid) throws BusinessException;

	public boolean chkNbrEdopkgs(String seqno, String varno, String blno) throws BusinessException;

	public List<UnStuffingCargoValueObject> getMftAssignCargo() throws BusinessException;

	public String MftAssignCrgvalCheck(String voy_nbr, String seqno) throws BusinessException;

	public void MftAssignCrgvalUpdate(String voy_nbr, String crgval, String seqno, String userid)
			throws BusinessException;
	// EndRegion UnStuffingAmendHandler

	// StartRegion UnStuffingCloseHandler
	public void updateWaiverStatus(String varno, String vslInVoynbr, String cntrno, String cntrseqno, String waiversts,
			String usrid) throws BusinessException;

	public boolean closeUnStuffing(String usrid, String vslInVoynbr, String varno, String containerno, String unStfDttm,
			String waiveUnStfChrg) throws BusinessException;

	// EndRegion UnStuffingCloseHandler
	public UnStuffingValueObject MftAssignBlParty(String varno, String cntrno, String blPartyAcctNo)
			throws BusinessException;

	public List<String> getBlPartyList(String varno, String vslInVoynbr, String cntrno) throws BusinessException;
	// StartRegion UnStuffingBlPartyHandler

	public List<Dept> listCompanyStart(String keyword, Integer start, Integer limit) throws BusinessException;

	// EndRegion UnStuffingBlPartyHandler
}
