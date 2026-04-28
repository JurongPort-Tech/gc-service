package sg.com.jp.generalcargo.service;

import java.util.List;

import javassist.tools.rmi.RemoteException;
import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.HSCode;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CargoAmendmentService {

	// StartRegion General Cargo Amendment
	public DPEUtil getDiscargingCargo(String asnNbr) throws BusinessException;

	public List<VesselVoyValueObject> getVesselVoyList(String cocode, String vesselName, String voyageNumber, String terminal) throws BusinessException;

	public ManifestValueObject mftRetrieve(String blno, String varno, String seqno) throws BusinessException;

	public boolean isManClose(String vesselCd) throws BusinessException;

	public List<DPEUtil> listAcount(String vvCd) throws BusinessException;

	public List<VesselVoyValueObject> getVesselListSearch(String custId, String esnNo) throws Exception;

	public List<EsnListValueObject> getEsnDetails(String esnNbr, String custId) throws BusinessException;

	public DPEUtil getEsnVessel(String asnNbr) throws BusinessException;

	public List<EsnListValueObject> getCntrDetails(String esnNo) throws BusinessException;

	public List<TruckerValueObject> getTruckerList(String esnNbr) throws BusinessException;

	public DPEUtil getHsSubCodeDesc(String hsCode, String hsSubCode);

	public List<EsnListValueObject> getListAccount(String truckNo, String bookRefNo, String accountNo, String custCd) throws BusinessException;

	public String getCrgNm(String crgtyp) throws BusinessException, RemoteException;

	public boolean isApplicableCargoCategory(String cargoType, String cargoCategory, String module)
			throws BusinessException;

	public String MftUpdationForDPE(String usrid, String coCd, String seqno, String varno, String blno, String crgtyp,
			String hscd, String hsSubCodeFr, String hsSubCodeTo, String crgdesc, String mark, String nopkgs, String gwt,
			String gvol, String crgstat, String dgind, String stgind, String dop, String pkgtyp, String coname,
			String consigneeCoyCode, String poL, String poD, String poFD, String cntrtype, String cntrsize,
			String cntr1, String cntr2, String cntr3, String cntr4, String autParty, String adviseBy, String adviseDate,
			String adviseMode, String amendChargedTo, String waiveCharge, String waiveReason, String category,
			String customHsCode, String conAddr, String shipperNm, String shipperAddr, String notifyParty,
			String notifyPartyAddr, String placeofDelivery, String placeofReceipt, List<HsCodeDetails> multiHsCodeList)
			throws BusinessException;

	public List<DPEUtil> listCrgType(String vslType) throws BusinessException;

	public List<DPEUtil> getShipper(String name, String shipperCode, Criteria criteria) throws BusinessException;

	public List<DPEUtil> currentListCargoCategory(String cargoTypeCode, String companyCode) throws BusinessException;

	public List<HSCode> listHsCode(String status) throws BusinessException;

	public List<BookingReferenceValueObject> fetchBKDetails(String brno) throws RemoteException, BusinessException;

	public List<DPEUtil> listHsSubCode(String status, String hsCode) throws BusinessException;

	public List<DPEUtil> listAuthorizedParty(String name, String vvCd, Criteria criteria) throws BusinessException;

	public List<DPEUtil> listPort(String name, Criteria criteria) throws BusinessException;

	public List<DPEUtil> listPackaging(String name, Criteria criteria) throws BusinessException;

	public List<DPEUtil> listCompany(String name, Criteria criteria) throws BusinessException;

	public TruckerValueObject getTruckerDetails(String truckerIc) throws BusinessException;

	public boolean checkValidTrucker(String truckerIcNo) throws BusinessException;

	public List<EsnListValueObject> getAccNo(String truckNo) throws BusinessException;

	public List<EsnListValueObject> getUserAccNo(String truckerIcNo, String custCd, String accNo) throws BusinessException;

	public DPEUtil getShipperInformation(String name);

	public boolean chkNoOfPkgs(String bookingRefNo, int noOfPkgs) throws BusinessException;

	public boolean isOutWardPm(String bookingRefNo, String varNbr) throws BusinessException;

	public boolean chkPkgsType(String pkgsType) throws BusinessException;

	public boolean chkWeight(String bookingRefNo, double weight) throws BusinessException;

	public boolean chkVolume(String bookingRefNo, double volume) throws BusinessException;

	public boolean chkAccNo(String accNo) throws BusinessException;

	public String updateCargoTypeCargoCategory(String bookingRefNo, String category, String cargoTypeCode) throws BusinessException;

	public String esnUpdateForDPE(int noOfPkgs, String hscd, String pkgsType, String mark, String truckerName,
			String truckerNo, String lopInd, String dgInd, String stgInd, String loadingFrom, String poD,
			int noOfStorageDay, String dutiInt, String payMode, String accNo, String esnNbr, String cargoDes,
			String trucker_cd, double weight, double volume, String truckerCNo, String cntr1, String cntr2,
			String cntr3, String cntr4, String stfInd, String strUAFlag, String strUserID, String category,
			String hsSubCodeFr, String hsSubCodeTo, List<TruckerValueObject> truckerList, int mainTrkPkgs, String coCd,
			String customHsCode, List<HsCodeDetails> multiHsCodeList) throws BusinessException;

	public int updateBkDetails(String bookingRefNo, String shipperNbr, String shipperAddress, String shipperNm,
			String strUserID, String conNm, String conAddr, String shipperAddr, String notifyParty,
			String notifyPartyAddr, String placeofDelivery, String placeofReceipt, String blNbr);

	public List<HsCodeDetails> getHsCodeDetailList(String mft_seq_nbr) throws BusinessException;

	public List<HsCodeDetails> getHsCodeEsnDetailList(String asn_nbr) throws BusinessException;
	
	// End Region
}
