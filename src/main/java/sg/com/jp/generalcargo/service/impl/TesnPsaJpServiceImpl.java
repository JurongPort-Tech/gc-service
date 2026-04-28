package sg.com.jp.generalcargo.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.BookingRefRepository;
import sg.com.jp.generalcargo.dao.EsnRepository;
import sg.com.jp.generalcargo.dao.ManifestRepository;
import sg.com.jp.generalcargo.dao.TesnPsaJpRepository;
import sg.com.jp.generalcargo.dao.UARepository;
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
import sg.com.jp.generalcargo.service.TesnPsaJpService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service("TesnPsaJpService")
public class TesnPsaJpServiceImpl implements TesnPsaJpService{

	
	@Autowired
	private TesnPsaJpRepository tesnPsaJpRepo;
	
	@Autowired
	private EsnRepository esnRepo;
	
	@Autowired
	private ManifestRepository manifestRepo;
	
	@Autowired
	private UARepository UARepo;
	
	@Autowired
	private BookingRefRepository bookingRefRepo;
	
	@Override
	public List<VesselVoyValueObject> getVesselList(String custCd) throws BusinessException {
		
		return tesnPsaJpRepo.getVesselList(custCd);
	}

	@Override
	public TableResult getEsnList(String selVoyno, String custCd, Criteria criteria) throws BusinessException {
		
		return tesnPsaJpRepo.getEsnList(selVoyno, custCd, criteria);
	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getEsnDetails(String esnNo, String custCd) throws BusinessException {
		
		return tesnPsaJpRepo.getEsnDetails(esnNo, custCd);
	}

	@Override
	public VesselVoyValueObject getVessel(String fetchVesselName, String fetchVoyageNbr, String custCd)
			throws BusinessException {
		
		return tesnPsaJpRepo.getVessel(fetchVesselName, fetchVoyageNbr, custCd);
	}

	@Override
	public List<String> getSAacctno(String s1) throws BusinessException {
		
		return tesnPsaJpRepo.getSAacctno(s1);
	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getABacctno(String s1) throws BusinessException {
		
		return tesnPsaJpRepo.getABacctno(s1);
	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getABacctnoForSA(String s1) throws BusinessException {
		
		return tesnPsaJpRepo.getABacctnoForSA(s1);
	}

	@Override
	public String getBPacctnbr(String s2, String s1) throws BusinessException {
		
		return tesnPsaJpRepo.getBPacctnbr(s2, s1);
	}

	@Override
	public String getScheme(String s1) throws BusinessException {
		
		return tesnPsaJpRepo.getScheme(s1);
	}

	@Override
	public String getSchemeInd(String s1) throws BusinessException {
		
		return tesnPsaJpRepo.getSchemeInd(s1);
	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getCntrDetails(String s2) throws BusinessException {
		
		return tesnPsaJpRepo.getCntrDetails(s2);
	}

	@Override
	public String AssignWhindCheck(String s2) throws BusinessException {
		
		return tesnPsaJpRepo.AssignWhindCheck(s2);
	}

	@Override
	public List<String> getWHDetails(String s13, String s2) throws BusinessException {
		
		return tesnPsaJpRepo.getWHDetails(s13, s2);
	}

	@Override
	public List<TruckerValueObject> getTruckerList(String s2) throws BusinessException {
		
		return tesnPsaJpRepo.getTruckerList(s2);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String getEdiUpdate(String s14, String s17) throws BusinessException {
		
		return tesnPsaJpRepo.getEdiUpdate(s14, s17);
	}

	@Override
	public String getClsShipInd_bkr(String s18) throws BusinessException {
		
		return tesnPsaJpRepo.getClsShipInd_bkr(s18);
	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getEdiDetails(String s26) throws BusinessException {
		
		return tesnPsaJpRepo.getEdiDetails(s26);
	}

	@Override
	public String AssignCrgvalCheck(String s2) throws BusinessException {
		
		return tesnPsaJpRepo.AssignCrgvalCheck(s2);
	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getAssignCargo() throws BusinessException {
		
		return tesnPsaJpRepo.getAssignCargo();
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void AssignCrgvalUpdate(String s21, String s2, String s6) throws BusinessException {
		
		tesnPsaJpRepo.AssignCrgvalUpdate(s21, s2, s6);
	}

	@Override
	public String getSchemeName(String s1) throws BusinessException {
		
		return tesnPsaJpRepo.getSchemeName(s1);
	}

	@Override
	public String getVCactnbr(String s1) throws BusinessException {
		
		return tesnPsaJpRepo.getVCactnbr(s1);
	}

	@Override
	public String getABactnbr(String s1) throws BusinessException {
		
		return tesnPsaJpRepo.getABactnbr(s1);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void EsnAssignBillUpdate(String s22, String s2, String s6) throws BusinessException {
		
		tesnPsaJpRepo.EsnAssignBillUpdate(s22, s2, s6);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void EsnAssignVslUpdate(String s1, String string, String s6) throws BusinessException {
		
		tesnPsaJpRepo.EsnAssignVslUpdate(s1, string, s6);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void AssignWhindUpdate(String s24, String s2, String s30, String s35, String s38, String s6)
			throws BusinessException {
		
		tesnPsaJpRepo.AssignWhindUpdate(s24, s2, s30, s35, s38, s6);
	}

	@Override
	public String getVesselType(String bookingRefNo) throws BusinessException {
		
		return esnRepo.getVesselType(bookingRefNo);
	}

	@Override
	public List<Map<String, Object>> getCategoryList() throws BusinessException {
		
		return esnRepo.getCategoryList();
	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getBkRefNo(String bookingRefNo, String custCd) throws BusinessException {
		
		return tesnPsaJpRepo.getBkRefNo(bookingRefNo, custCd);
	}

	@Override
	public List<ManifestValueObject> getHSCodeList(String string) throws BusinessException {
		
		return manifestRepo.getHSCodeList(string);
	}

	@Override
	public TruckerValueObject getTruckerDetails(String truckerIc) throws BusinessException {
		
		return tesnPsaJpRepo.getTruckerDetails(truckerIc);
	}

	@Override
	public boolean chkDttmOfSecondCarrierVsl(String varNbr) throws BusinessException {
		
		return tesnPsaJpRepo.chkDttmOfSecondCarrierVsl(varNbr);
	}

	@Override
	public boolean chkOutwardPM4(String bookingRefNo, String varNbr) throws BusinessException {
		
		return tesnPsaJpRepo.chkOutwardPM4(bookingRefNo, varNbr);
	}

	@Override
	public boolean chkNoOfPkgs(String bookingRefNo, int noOfPkgs) throws BusinessException {
		
		return tesnPsaJpRepo.chkNoOfPkgs(bookingRefNo, noOfPkgs);
	}

	@Override
	public boolean chkPkgsType(String pkgsType) throws BusinessException {
		
		return tesnPsaJpRepo.chkPkgsType(pkgsType);
	}

	@Override
	public boolean chkWeight(String bookingRefNo, double weight) throws BusinessException {
		
		return tesnPsaJpRepo.chkWeight(bookingRefNo, weight);
	}

	@Override
	public boolean chkVolume(String bookingRefNo, double volume) throws BusinessException {
		
		return tesnPsaJpRepo.chkVolume(bookingRefNo, volume);
	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getAccNo(String varNbr) throws BusinessException {
		
		return tesnPsaJpRepo.getAccNo(varNbr);
	}

	@Override
	public List<TesnPsaJpEsnListValueObject> getUserAccNo(String bookingRefNo, String custCd, String getAccNo) throws BusinessException {
		
		return tesnPsaJpRepo.getUserAccNo(bookingRefNo, custCd, getAccNo);
	}

	@Override
	public boolean chkAccNo(String accNo_I) throws BusinessException {
		
		return tesnPsaJpRepo.chkAccNo(accNo_I);
	}

	@Override
	public int getUaNoPkgs(String esnNo) throws BusinessException {
		
		return tesnPsaJpRepo.getUaNoPkgs(esnNo);
	}

	@Override
	public String getClsShipInd(String varNbr) throws BusinessException {
		
		return tesnPsaJpRepo.getClsShipInd(varNbr);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void esnUpdateForDPE(int noOfPkgs, String hsCode, String hsSubCodeFr, String hsSubCodeTo, String pkgsType,
			String marking, String lopInd, String dgInd, String storageInd, String portD, int noOfStorageDay,
			String payMode, String accNo_I, String esnNo, String cargoDesc, double weight, double volume, String cntr1,
			String cntr2, String cntr3, String cntr4, String bookingRefNo, String deNull, String userID,
			String category, List<TruckerValueObject> truckerList, String deliveryToEPC, String customHsCode,
			List<HsCodeDetails> multiHsCodeList) throws BusinessException {
		
		tesnPsaJpRepo.esnUpdateForDPE( noOfPkgs,  hsCode,  hsSubCodeFr,  hsSubCodeTo,  pkgsType,
				 marking,  lopInd,  dgInd,  storageInd,  portD,  noOfStorageDay,
				 payMode,  accNo_I,  esnNo,  cargoDesc,  weight,  volume,  cntr1,
				 cntr2,  cntr3,  cntr4,  bookingRefNo,  deNull,  userID,
				 category,  truckerList,  deliveryToEPC, customHsCode, multiHsCodeList);
	}

	@Override
	public String getCategoryValue(String category) throws BusinessException {
		
		return esnRepo.getCategoryValue(category);
	}

	@Override
	public String getPkgsDesc(String esnNo) throws BusinessException {
		
		return tesnPsaJpRepo.getPkgsDesc(esnNo);
	}

	@Override
	public String getBillablePartyName(String accNo_I) throws BusinessException {
		
		return tesnPsaJpRepo.getBillablePartyName(accNo_I);
	}

	@Override
	public boolean isAsnShut(String esnNo) throws BusinessException {
		
		return UARepo.isAsnShut(esnNo);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void esnCancel(String esnNo, String bookingRefNo, String userID) throws BusinessException {
		
		tesnPsaJpRepo.esnCancel(esnNo, bookingRefNo, userID);
	}

	@Override
	public String getEsnDeclared(String bookingRefNo) throws BusinessException {
		
		return tesnPsaJpRepo.getEsnDeclared(bookingRefNo);
	}

	@Override
	public String getBkStatus(String bookingRefNo) throws BusinessException {
		
		return tesnPsaJpRepo.getBkStatus(bookingRefNo);
	}

	@Override
	public String getDeclarentCd(String bookingRefNo) throws BusinessException {
		
		return tesnPsaJpRepo.getDeclarentCd(bookingRefNo);
	}

	@Override
	public String getVvStatus(String bookingRefNo) throws BusinessException {
		
		return tesnPsaJpRepo.getVvStatus(bookingRefNo);
	}

	@Override
	public String getClsVslInd(String bookingRefNo) throws BusinessException {
		
		return tesnPsaJpRepo.getClsVslInd(bookingRefNo);
	}

	@Override
	public String getClsBjInd(String bookingRefNo) throws BusinessException {
		
		return tesnPsaJpRepo.getClsBjInd(bookingRefNo);
	}

	@Override
	public List<BookingReferenceValueObject> fetchBKDetails(String bookingRefNo) throws BusinessException {
		
		return bookingRefRepo.fetchBKDetails(bookingRefNo);
	}

	@Override
	public boolean chkFirstCarrierVsl(String firstCName, String inVoyageNo) throws BusinessException {
		
		return tesnPsaJpRepo.chkFirstCarrierVsl(firstCName, inVoyageNo);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String insertEsnDetailsForDPE(String userID, String varNbr, String custCd, String bookingRefNo,
			String marking, String portD, String loadOperInd, String dgInd, String hsCode, int noOfStorageDay,
			String storageInd, String pkgsType, int noOfPkgs, double weight, double volume, String accNo_I,
			String payMode, String cargoDesc, String cntr1, String cntr2, String cntr3, String cntr4, String firstCName,
			String inVoyageNo, String deNull, String category, String hsSubCodeFr, String hsSubCodeTo,
			List<TruckerValueObject> truckerList, String deliveryToEPC, String customHsCode, 
			List<HsCodeDetails> multiHsCodeList) throws BusinessException {
		
		return tesnPsaJpRepo.insertEsnDetailsForDPE( userID,  varNbr,  custCd,  bookingRefNo,
				 marking,  portD,  loadOperInd,  dgInd,  hsCode, noOfStorageDay,
				 storageInd,  pkgsType,  noOfPkgs,  weight,  volume,  accNo_I,
				 payMode,  cargoDesc,  cntr1,  cntr2,  cntr3,  cntr4,  firstCName,
				 inVoyageNo,  deNull,  category,  hsSubCodeFr,  hsSubCodeTo,
				 truckerList,  deliveryToEPC, customHsCode, multiHsCodeList);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String createNomVesselPsaJp(String firstCName, String inVoyageNo, String userID) throws BusinessException {
		
		return tesnPsaJpRepo.createNomVesselPsaJp(firstCName, inVoyageNo, userID);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updFtrans(String esnNo, String transtype, String ftransdtm) throws BusinessException {
		UARepo.updFtrans(esnNo, transtype, ftransdtm);
	}

	@Override
	public List<UaEsnDetValueObject> getEsnView(String esnNo, String transtype) throws BusinessException {
		return UARepo.getEsnView(esnNo, transtype);
	}

	@Override
	public List<UaListObject> getUAList(String esnNo) throws BusinessException {
		return UARepo.getUAList(esnNo);
	}

	@Override
	public String getSysdate() throws BusinessException {
		return UARepo.getSysdate() ;
	}

	@Override
	public List<UaEsnListValueObject> getEsnList(String esn_asn_nbr) throws BusinessException {
		return UARepo.getEsnList(esn_asn_nbr) ;
	}

	@Override
	public List<HsCodeDetails> getHsCodeDetailList(String esnNo) throws BusinessException {
		return tesnPsaJpRepo.getHsCodeDetailList(esnNo);
	}


}
