package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.HSCode;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface ManifestRepository {

	// ejb.sessionBeans.gbms.cargo.manifest -->ManifestEJB-->chkVslStat()
	public boolean chkVslStat(String varno) throws BusinessException;

	public boolean isManClose(String vesselCd) throws BusinessException;

	public List<VesselVoyValueObject> getVesselVoyList(String cocode, String vesselName, String voyageNumber,
			String terminal) throws BusinessException;

	public ManifestValueObject mftRetrieve(String blno, String varno, String seqno) throws BusinessException;

	public String MftUpdationForDPE(String usrid, String coCd, String seqno, String varno, String blno, String crgtyp,
			String hscd, String hsSubCodeFr, String hsSubCodeTo, String crgdesc, String mark, String nopkgs, String gwt,
			String gvol, String crgstat, String dgind, String stgind, String dop, String pkgtyp, String coname,
			String consigneeCoyCode, String poL, String poD, String poFD, String cntrtype, String cntrsize,
			String cntr1, String cntr2, String cntr3, String cntr4, String autParty, String adviseBy, String adviseDate,
			String adviseMode, String amendChargedTo, String waiveCharge, String waiveReason, String category, 
			String customHsCode, String conAddr, String shipperNm, String shipperAddr, String notifyParty, 
			String notifyPartyAddr, String placeofDelivery, String placeofReceipt, List<HsCodeDetails> multiHsCodeList)
			throws BusinessException;

	public String getCrgNm(String crgtyp) throws BusinessException;

	public List<Object> transMftUpdate(String userID, String varnoF, String varnoT,
			List<ManifestValueObject> vseqblno) throws BusinessException;

	public int getManifestListCount(String vvcode, String coCode, Criteria criteria) throws BusinessException;

	public List<ManifestValueObject> getHSCodeList(String status) throws BusinessException;
	
	public List<HSCode> getHSSubCodeList(String hsCode) throws BusinessException;
	
	public String getHSSubCodeDes(String hsCode, String hsSubCodeFr, String hsSubCodeTo) throws BusinessException;
	
}
