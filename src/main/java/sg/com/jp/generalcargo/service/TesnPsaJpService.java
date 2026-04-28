package sg.com.jp.generalcargo.service;

import java.util.List;
import java.util.Map;

import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TesnPsaJpEsnListValueObject;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.domain.UaEsnListValueObject;
import sg.com.jp.generalcargo.domain.UaListObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface TesnPsaJpService {

	public List<VesselVoyValueObject> getVesselList(String custCd) throws BusinessException;

	public TableResult getEsnList(String selVoyno, String custCd, Criteria criteria) throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getEsnDetails(String esnNo, String custCd) throws BusinessException;

	public VesselVoyValueObject getVessel(String fetchVesselName, String fetchVoyageNbr, String custCd) throws BusinessException;

	public List<String> getSAacctno(String s1) throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getABacctno(String s1) throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getABacctnoForSA(String s1) throws BusinessException;

	public String getBPacctnbr(String s2, String s1) throws BusinessException;

	public String getScheme(String s1) throws BusinessException;

	public String getSchemeInd(String s1) throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getCntrDetails(String s2) throws BusinessException;

	public String AssignWhindCheck(String s2) throws BusinessException;

	public List<String> getWHDetails(String s13, String s2) throws BusinessException;

	public List<TruckerValueObject> getTruckerList(String s2) throws BusinessException;

	public String getEdiUpdate(String s14, String s17) throws BusinessException;

	public String getClsShipInd_bkr(String s18) throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getEdiDetails(String s26) throws BusinessException;

	public String AssignCrgvalCheck(String s2) throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getAssignCargo() throws BusinessException;

	public void AssignCrgvalUpdate(String s21, String s2, String s6) throws BusinessException;

	public String getSchemeName(String s1) throws BusinessException;

	public String getVCactnbr(String s1) throws BusinessException;

	public String getABactnbr(String s1) throws BusinessException;

	public void EsnAssignBillUpdate(String s22, String s2, String s6) throws BusinessException;

	public void EsnAssignVslUpdate(String s1, String string, String s6) throws BusinessException;

	public void AssignWhindUpdate(String s24, String s2, String s30, String s35, String s38, String s6) throws BusinessException;

	public String getVesselType(String bookingRefNo) throws BusinessException;

	public List<Map<String, Object>> getCategoryList() throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getBkRefNo(String bookingRefNo, String custCd) throws BusinessException;

	public List<ManifestValueObject> getHSCodeList(String string) throws BusinessException;

	public TruckerValueObject getTruckerDetails(String truckerIc) throws BusinessException;

	public boolean chkDttmOfSecondCarrierVsl(String varNbr) throws BusinessException;

	public boolean chkOutwardPM4(String bookingRefNo, String varNbr) throws BusinessException;

	public boolean chkNoOfPkgs(String bookingRefNo, int noOfPkgs) throws BusinessException;

	public boolean chkPkgsType(String pkgsType) throws BusinessException;

	public boolean chkWeight(String bookingRefNo, double weight) throws BusinessException;

	public boolean chkVolume(String bookingRefNo, double volume) throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getAccNo(String varNbr) throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getUserAccNo(String bookingRefNo, String custCd, String getAccNo) throws BusinessException;

	public boolean chkAccNo(String accNo_I) throws BusinessException;

	public int getUaNoPkgs(String esnNo) throws BusinessException;

	public String getClsShipInd(String varNbr) throws BusinessException;

	public void esnUpdateForDPE(int noOfPkgs, String hsCode, String hsSubCodeFr, String hsSubCodeTo, String pkgsType,
			String marking, String lopInd, String dgInd, String storageInd, String portD, int noOfStorageDay,
			String payMode, String accNo_I, String esnNo, String cargoDesc, double weight, double volume, String cntr1,
			String cntr2, String cntr3, String cntr4, String bookingRefNo, String deNull, String userID,
			String category, List<TruckerValueObject> truckerList, String deliveryToEPC, String customHsCode,
			List<HsCodeDetails> multiHsCodeList) throws BusinessException;

	public String getCategoryValue(String category) throws BusinessException;

	public String getPkgsDesc(String esnNo) throws BusinessException;

	public String getBillablePartyName(String accNo_I) throws BusinessException;

	public boolean isAsnShut(String esnNo) throws BusinessException;

	public void esnCancel(String esnNo, String bookingRefNo, String userID) throws BusinessException;

	public String getEsnDeclared(String bookingRefNo) throws BusinessException;

	public String getBkStatus(String bookingRefNo) throws BusinessException;

	public String getDeclarentCd(String bookingRefNo) throws BusinessException;

	public String getVvStatus(String bookingRefNo) throws BusinessException;

	public String getClsVslInd(String bookingRefNo) throws BusinessException;

	public String getClsBjInd(String bookingRefNo) throws BusinessException;

	public List<BookingReferenceValueObject> fetchBKDetails(String bookingRefNo) throws BusinessException;

	public boolean chkFirstCarrierVsl(String firstCName, String inVoyageNo) throws BusinessException;

	public String insertEsnDetailsForDPE(String userID, String varNbr, String custCd, String bookingRefNo,
			String marking, String portD, String loadOperInd, String dgInd, String hsCode, int noOfStorageDay,
			String storageInd, String pkgsType, int noOfPkgs, double weight, double volume, String accNo_I,
			String payMode, String cargoDesc, String cntr1, String cntr2, String cntr3, String cntr4, String firstCName,
			String inVoyageNo, String deNull, String category, String hsSubCodeFr, String hsSubCodeTo,
			List<TruckerValueObject> truckerList, String deliveryToEPC, String customHsCode, 
			List<HsCodeDetails> multiHsCodeList) throws BusinessException;

	public String createNomVesselPsaJp(String firstCName, String inVoyageNo, String userID) throws BusinessException;

	public void updFtrans(String esnNo, String transtype, String ftransdtm) throws BusinessException;

	public List<UaEsnDetValueObject> getEsnView(String esnNo, String transtype) throws BusinessException;

	public List<UaListObject> getUAList(String esnNo) throws BusinessException;

	public String getSysdate() throws BusinessException;

	public List<UaEsnListValueObject> getEsnList(String esn_asn_nbr) throws BusinessException;

	public List<HsCodeDetails> getHsCodeDetailList(String esnNo) throws BusinessException;

}
