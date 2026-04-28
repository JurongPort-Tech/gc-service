package sg.com.jp.generalcargo.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.ContainerRepository;
import sg.com.jp.generalcargo.dao.EdoRepository;
import sg.com.jp.generalcargo.dao.EsnRepository;
import sg.com.jp.generalcargo.dao.ManifestRepository;
import sg.com.jp.generalcargo.dao.UARepository;
import sg.com.jp.generalcargo.dao.impl.BookingRefJdbcRepository;
import sg.com.jp.generalcargo.dao.impl.CashSalesJdbcRepository;
import sg.com.jp.generalcargo.dao.impl.UAOpsJdbcRepository;
import sg.com.jp.generalcargo.domain.CashSalesValueObject;
import sg.com.jp.generalcargo.domain.ContainerValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.domain.UaEsnListValueObject;
import sg.com.jp.generalcargo.domain.UaListObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.OutwardCargoESNService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service("ESNService")
public class OutwardCargoESNServiceImpl implements OutwardCargoESNService {

	@Autowired
	private EsnRepository esnRepo;

	@Autowired
	private ManifestRepository manifestRepo;

	@Autowired
	private EdoRepository edoRepo;

	@Autowired
	private ContainerRepository containerRepo;

	@Autowired
	private UARepository UARepo;
	
	@Autowired
	private UAOpsJdbcRepository UAOpsRepo;

	@Autowired
	private BookingRefJdbcRepository bookingRefJdbcRepository;
	
	@Autowired
	private CashSalesJdbcRepository cashSalesJdbcRepository;
	
	

	@Override
	public List<VesselVoyValueObject> getVesselList(String custCd) throws BusinessException {

		return esnRepo.getVesselList(custCd);
	}

	@Override
	public VesselVoyValueObject getVessel(String fetchVesselName, String fetchVoyageNbr, String custCd)
			throws BusinessException {

		return esnRepo.getVessel(fetchVesselName, fetchVoyageNbr, custCd);
	}

	@Override
	public VesselVoyValueObject getVesselInfo(String selVoyno) throws BusinessException {

		return esnRepo.getVesselInfo(selVoyno);
	}

	@Override
	public List<EsnListValueObject> getEsnList(String selVoyno, String custCd) throws BusinessException {

		return esnRepo.getEsnList(selVoyno, custCd);
	}

	@Override
	public List<EsnListValueObject> getEsnDetails(String esnNo, String custCd) throws BusinessException {

		return esnRepo.getEsnDetails(esnNo, custCd);
	}

	@Override
	public List<EsnListValueObject> getCntrDetails(String s5) throws BusinessException {

		return esnRepo.getCntrDetails(s5);
	}

	@Override
	public List<TruckerValueObject> getTruckerList(String s5) throws BusinessException {

		return esnRepo.getTruckerList(s5);
	}

	@Override
	public String getScheme(String s4) throws BusinessException {

		return esnRepo.getScheme(s4);
	}

	@Override
	public String getSchemeInd(String s4) throws BusinessException {

		return esnRepo.getSchemeInd(s4);
	}

	@Override
	public String getClsShipInd_bkr(String s12) throws BusinessException {

		return esnRepo.getClsShipInd_bkr(s12);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void AssignCrgvalUpdate(String s13, String s5, String s1) throws BusinessException {

		esnRepo.AssignCrgvalUpdate(s13, s5, s1);
	}

	@Override
	public String AssignWhindCheck(String s5) throws BusinessException {

		return esnRepo.AssignWhindCheck(s5);
	}

	@Override
	public List<String> getWHDetails(String s17, String s5) throws BusinessException {

		return esnRepo.getWHDetails(s17, s5);
	}

	@Override
	public String AssignCrgvalCheck(String s5) throws BusinessException {

		return esnRepo.AssignCrgvalCheck(s5);
	}

	@Override
	public List<EsnListValueObject> getAssignCargo() throws BusinessException {

		return esnRepo.getAssignCargo();
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void AssignWhindUpdate(String s15, String s5, String s19, String s24, String s28, String s1)
			throws BusinessException {

		esnRepo.assignWhindUpdate(s15, s5, s19, s24, s28, s1);
	}

	@Override
	public boolean checkExistSubAdp(String s5) throws BusinessException {

		return esnRepo.checkExistSubAdp(s5);
	}

	@Override
	public boolean isEsnCreator(String esnAsnNbr, String companyCode) throws BusinessException {

		return esnRepo.isEsnCreator(esnAsnNbr, companyCode);
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
	public List<EsnListValueObject> getBkRefNo(String bookingRefNo, String custCd) throws BusinessException {

		return esnRepo.getBkRefNo(bookingRefNo, custCd);
	}

	@Override
	public boolean checkDisbaleOverSideFroDPE(String vvCd) throws BusinessException {

		return esnRepo.checkDisbaleOverSideFroDPE(vvCd);
	}

	@Override
	public TruckerValueObject getTruckerDetails(String trucker_ciNo) throws BusinessException {

		return esnRepo.getTruckerDetails(trucker_ciNo);
	}

	@Override
	public List<ManifestValueObject> getHSCodeList(String string) throws BusinessException {

		return manifestRepo.getHSCodeList(string);
	}

	@Override
	public String getCategoryValue(String category) throws BusinessException {

		return esnRepo.getCategoryValue(category);
	}

	@Override
	public String getHSSubCodeDes(String s3, String hsSubCodeFr, String hsSubCodeTo) throws BusinessException {

		return esnRepo.getHSSubCodeDes(s3, hsSubCodeFr, hsSubCodeTo);
	}

	@Override
	public boolean validateGCStuffIndicatorCntr(String loadVVCd, String cntrNbr) throws BusinessException {

		return containerRepo.validateGCStuffIndicatorCntr(loadVVCd, cntrNbr);
	}

	@Override
	public ContainerValueObject getContainerInformation(String cntrNbr) throws BusinessException {

		return containerRepo.getContainerInformation(cntrNbr);
	}

	@Override
	public boolean chkNoOfPkgs(String s32, int i) throws BusinessException {

		return esnRepo.chkNoOfPkgs(s32, i);
	}

	@Override
	public boolean isOutWardPm(String s32, String s41) throws BusinessException {

		return esnRepo.isOutWardPm(s32, s41);
	}

	@Override
	public boolean chkPkgsType(String s5) throws BusinessException {

		return esnRepo.chkPkgsType(s5);
	}

	@Override
	public boolean chkWeight(String s32, double d) throws BusinessException {

		return esnRepo.chkWeight(s32, d);
	}

	@Override
	public boolean chkVolume(String s32, double d1) throws BusinessException {

		return esnRepo.chkVolume(s32, d1);
	}

	@Override
	public List<EsnListValueObject> getAccNo(String s19) throws BusinessException {

		return esnRepo.getAccNo(s19);
	}

	@Override
	public List<EsnListValueObject> getUserAccNo(String s32, String s1, String s73) throws BusinessException {

		return esnRepo.getUserAccNo(s32, s1, s73);
	}

	@Override
	public boolean chkAccNo(String s28) throws BusinessException {

		return esnRepo.chkAccNo(s28);
	}

	@Override
	public int getUaNoPkgs(String s35) throws BusinessException {

		return esnRepo.getUaNoPkgs(s35);
	}

	@Override
	public String getClsShipInd(String s41) throws BusinessException {

		return esnRepo.getClsShipInd(s41);
	}

	@Override
	public boolean isBillChargesRaised(String s35) throws BusinessException {

		return esnRepo.isBillChargesRaised(s35);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void esnUpdateForDPE(int i, String s3, String s5, String s9, String trucker_nm, String trucker_ic,
			String s11, String s15, String s17, String s13, String s24, int j, String s26, String s31, String s28,
			String s35, String s7, String trucker_cd, double d, double d1, String trucker_ct, String s46, String s48,
			String s50, String s52, String stfInd, String strUAFlag, String s, String category, String hsSubCodeFr,
			String hsSubCodeTo, List<TruckerValueObject> truckerVector, int trucker_pkg, String s1,
			String deliveryToEPC, String cntrSeqNbr, String appNo,String customHsCode, List<HsCodeDetails> multiHsCodeList) throws BusinessException { // esn

		esnRepo.esnUpdateForDPE(i, s3, s5, s9, trucker_nm, trucker_ic, s11, s15, s17, s13, s24, j, s26, s31, s28, s35,
				s7, trucker_cd, d, d1, trucker_ct, s46, s48, s50, s52, stfInd, strUAFlag, s, category, hsSubCodeFr,
				hsSubCodeTo, truckerVector, trucker_pkg, s1, deliveryToEPC, cntrSeqNbr, appNo, customHsCode ,multiHsCodeList);
	}

	@Override
	public String getPkgsDesc(String s35) throws BusinessException {

		return esnRepo.getPkgsDesc(s35);
	}

	@Override
	public String getBillablePartyName(String s28) throws BusinessException {

		return esnRepo.getBillablePartyName(s28);
	}

	@Override
	public List<String> getSAacctno(String s41) throws BusinessException {

		return esnRepo.getSAacctno(s41);
	}

	@Override
	public List<EsnListValueObject> getABacctno(String s41) throws BusinessException {

		return esnRepo.getABacctno(s41);
	}

	@Override
	public List<EsnListValueObject> getABacctnoForSA(String s41) throws BusinessException {

		return esnRepo.getABacctnoForSA(s41);
	}

	@Override
	public String getBPacctnbr(String s35, String s41) throws BusinessException {

		return esnRepo.getBPacctnbr(s35, s41);
	}

	@Override
	public String getSchemeName(String s41) throws BusinessException {

		return esnRepo.getSchemeName(s41);
	}

	@Override
	public String getVCactnbr(String s41) throws BusinessException {

		return esnRepo.getVCactnbr(s41);
	}

	@Override
	public String getABactnbr(String s41) throws BusinessException {

		return esnRepo.getABactnbr(s41);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void EsnAssignBillUpdate(String s59, String s35, String s) throws BusinessException {

		esnRepo.EsnAssignBillUpdate(s59, s35, s);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void EsnAssignVslUpdate(String s41, String string, String s) throws BusinessException {

		esnRepo.EsnAssignVslUpdate(s41, string, s);
	}

	@Override
	public String getVesselScheme(String s41) throws BusinessException {

		return edoRepo.getVesselScheme(s41);
	}

	@Override
	public boolean checkDeleteEsn(String esnNo) throws BusinessException {

		return esnRepo.checkDeleteEsn(esnNo);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void esnCancel(String esnNo, String bookingRefNo, String strUAFlag, String strUserId)
			throws BusinessException {

		esnRepo.esnCancel(esnNo, bookingRefNo, strUAFlag, strUserId);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean isAsnShut(String esnNo) throws BusinessException {

		return UARepo.isAsnShut(esnNo);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updFtrans(String esnNo, String transtype, String ftransdtm, String userId) throws BusinessException {
		UAOpsRepo.updFtrans(esnNo, transtype, ftransdtm, userId);
	}

	@Override
	public List<UaEsnListValueObject> getEsnList(String esn_asn_nbr) throws BusinessException {

		return UARepo.getEsnList(esn_asn_nbr);
	}

	@Override
	public List<UaEsnDetValueObject> getEsnView(String esnNo, String transtype) throws BusinessException {

		return UARepo.getEsnView(esnNo, transtype);
	}

	@Override
	public List<UaListObject> getUAList(String esnNo) throws BusinessException {

		return UAOpsRepo.getUAList(esnNo);
	}

	@Override
	public String getSysdate() throws BusinessException {

		return UARepo.getSysdate();
	}

	@Override
	public List<UaEsnListValueObject> getTransferredCargo(String s2) throws BusinessException {

		return UARepo.getTransferredCargo(s2);
	}

	@Override
	public boolean checkESNCntr(String s5) throws BusinessException {

		return UARepo.checkESNCntr(s5);
	}

	@Override
	public List<VesselVoyValueObject> getVesselListSearch(String s, String s1) throws Exception {

		return esnRepo.getVesselListSearch(s, s1);
	}

	@Override
	public String getEsnDeclared(String bookingRefNo) throws BusinessException {

		return esnRepo.getEsnDeclared(bookingRefNo);
	}

	@Override
	public String getBkStatus(String bookingRefNo) throws BusinessException {

		return esnRepo.getBkStatus(bookingRefNo);
	}

	@Override
	public String getDeclarentCd(String bookingRefNo) throws BusinessException {

		return esnRepo.getDeclarentCd(bookingRefNo);
	}

	@Override
	public String getVvStatus(String bookingRefNo) throws BusinessException {

		return esnRepo.getVvStatus(bookingRefNo);
	}

	@Override
	public String getTerminal(String bookingRefNo) throws BusinessException {

		return esnRepo.getTerminal(bookingRefNo);
	}

	@Override
	public String getClsBjInd(String bookingRefNo) throws BusinessException {

		return esnRepo.getClsBjInd(bookingRefNo);
	}

	@Override
	public String getClsVslInd(String bookingRefNo) throws BusinessException {

		return esnRepo.getClsVslInd(bookingRefNo);
	}

	@Override
	public String getCrgTypeCd(String crgType) throws BusinessException {

		return esnRepo.getCrgTypeCd(crgType);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String insertEsnDetailsForDPE(String varNbr, String truckerIcNo, String truckerCNo, String custCd,
			String bookingRefNo, String marking, String portD, String loadOperInd, String loadFrom, String dgInd,
			String hsCode, String dutyGoodInd, String truckerName, int noOfStorageDay, String storageInd,
			String pkgsType, int noOfPkgs, double weight, double volume, String accNo_I, String payMode,
			String cargoDesc, String truckerCd, String cntr1, String cntr2, String cntr3, String cntr4, String userID,
			String strStfInd, String category, String hsSubCodeFr, String hsSubCodeTo, int truckerPkg,
			String deliveryToEPC, String cntrSeqNbr, String appNo, String customHsCode, List<HsCodeDetails> multiHsCodeList) throws BusinessException {

		return esnRepo.insertEsnDetailsForDPE(varNbr, truckerIcNo, truckerCNo, custCd, bookingRefNo, marking, portD,
				loadOperInd, loadFrom, dgInd, hsCode, dutyGoodInd, truckerName, noOfStorageDay, storageInd, pkgsType,
				noOfPkgs, weight, volume, accNo_I, payMode, cargoDesc, truckerCd, cntr1, cntr2, cntr3, cntr4, userID,
				strStfInd, category, hsSubCodeFr, hsSubCodeTo, truckerPkg, deliveryToEPC, cntrSeqNbr, appNo, customHsCode, multiHsCodeList);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void insertTruckerInfor(String string, String string2, String string3, int parseInt, String string4,
			String esnNo, String string5, int i, String userID) throws BusinessException {

		esnRepo.insertTruckerInfor(string, string2, string3, parseInt, string4, esnNo, string5, i, userID);
	}

	@Override
	public List<EsnListValueObject> getEsnList(String selectVoyNo, String custId, Criteria criteria)
			throws BusinessException {

		return esnRepo.getEsnList(selectVoyNo, custId, criteria);
	}

	public int getEsnListCount(String selectVoyNo, String custId, Criteria criteria) throws BusinessException {
		return esnRepo.getEsnListCount(selectVoyNo, custId, criteria);
	}

	@Override
	public List<EsnListValueObject> getEsnDetails(String esnNbr, String custId, Criteria criteria)
			throws BusinessException {

		return esnRepo.getEsnDetails(esnNbr, custId, criteria);
	}

	public Map<String, String> getCargoCategoryCode_CargoCategoryName() throws BusinessException {
		return bookingRefJdbcRepository.getCargoCategoryCode_CargoCategoryName();
	}

	@Override
	public CashSalesValueObject getCashSales(String refNbr) throws BusinessException {
		return cashSalesJdbcRepository.getCashSales(refNbr);
	}

	@Override
	public String getUAtransDttm(String esnasnnbr) throws BusinessException {
		return UAOpsRepo.getUAtransDttm(esnasnnbr);
	}

	@Override
	public List<String> indicationStatus(String vvCd) throws BusinessException {
		return edoRepo.indicationStatus(vvCd);
	}

	@Override
	public Boolean getVesselATUDttm(String bookingRefNo) throws BusinessException {
		return esnRepo.getVesselATUDttm(bookingRefNo);
	}

	@Override
	public List<HsCodeDetails> getHsCodeDetailList(String esnNo) throws BusinessException {
		return esnRepo.getHsCodeDetailList(esnNo);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public boolean updateCustomDetail(List<HsCodeDetails> multiHsCodeList,String esnNo, String userId) throws BusinessException {
		return esnRepo.updateCustomDetail(multiHsCodeList,esnNo, userId);
	}

}
