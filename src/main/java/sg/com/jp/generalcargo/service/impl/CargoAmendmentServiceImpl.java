package sg.com.jp.generalcargo.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javassist.tools.rmi.RemoteException;
import sg.com.jp.generalcargo.dao.BookingRefRepository;
import sg.com.jp.generalcargo.dao.DpeGeneralCargoAmendRepository;
import sg.com.jp.generalcargo.dao.EsnRepository;
import sg.com.jp.generalcargo.dao.InwardCargoManifestRepository;
import sg.com.jp.generalcargo.dao.ManifestRepository;
import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.HSCode;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.CargoAmendmentService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;

@Service("CargoAmendmentService")
public class CargoAmendmentServiceImpl implements CargoAmendmentService {

	private static final Log log = LogFactory.getLog(CargoAmendmentServiceImpl.class);
	private boolean isAccountContain = false;
	private boolean isUserAccountContain = false;
	@Autowired
	private DpeGeneralCargoAmendRepository dpeCargoAmendRepo;
	@Autowired
	private ManifestRepository manifestRepo;
	@Autowired
	private BookingRefRepository bookingRefRepo;
	@Autowired
	private EsnRepository esnRepo;
	@Autowired
	private InwardCargoManifestRepository cargoManifestRepo;

	// StartRegion General Cargo Amendment
	@Override
	public DPEUtil getDiscargingCargo(String asnNbr) throws BusinessException {
		return dpeCargoAmendRepo.getDiscargingCargo(asnNbr);
	}

	public List<BookingReferenceValueObject> fetchBKDetails(String brno) throws RemoteException, BusinessException {
		return bookingRefRepo.fetchBKDetails(brno);
	}

	public List<VesselVoyValueObject> getVesselVoyList(String cocode, String vesselName, String voyageNumber, String terminal) throws BusinessException {
		return manifestRepo.getVesselVoyList(cocode, vesselName, voyageNumber, terminal);
	}

	public ManifestValueObject mftRetrieve(String blno, String varno, String seqno) throws BusinessException {
		return manifestRepo.mftRetrieve(blno, varno, seqno);
	}

	public boolean isManClose(String vesselCd) throws BusinessException {
		return manifestRepo.isManClose(vesselCd);
	}

	public List<DPEUtil> listAcount(String vvCd) throws BusinessException {
		return dpeCargoAmendRepo.listAcount(vvCd);
	}

	@Override
	public List<VesselVoyValueObject> getVesselListSearch(String custId, String esnNo) throws Exception {
		return esnRepo.getVesselListSearch(custId, esnNo);
	}

	@Override
	public List<EsnListValueObject> getEsnDetails(String esnNbr, String custId) throws BusinessException {
		return esnRepo.getEsnDetails(esnNbr, custId);
	}

	@Override
	public DPEUtil getEsnVessel(String asnNbr) throws BusinessException {
		return dpeCargoAmendRepo.getEsnVessel(asnNbr);
	}

	@Override
	public List<EsnListValueObject> getCntrDetails(String esnNo) throws BusinessException {
		return esnRepo.getCntrDetails(esnNo);
	}

	@Override
	public List<TruckerValueObject> getTruckerList(String esnNbr) throws BusinessException {
		return esnRepo.getTruckerList(esnNbr);
	}

	@Override
	public DPEUtil getHsSubCodeDesc(String hsCode, String hsSubCode) {
		return dpeCargoAmendRepo.getHsSubCodeDesc(hsCode, hsSubCode);
	}

	// sg.com.jp.dpe.action -->GeneralCargoAmendAction -->getListAccount()
	public List<EsnListValueObject> getListAccount(String truckerNo, String bkRefNo, String accountNo, String custCd) throws BusinessException {
		String accNo = "";
		List<EsnListValueObject> listAccount = esnRepo.getAccNo(truckerNo);
		List<EsnListValueObject> list = new ArrayList<EsnListValueObject>();
		try {
			log.info("START: getListAccount Service" +" truckerNo:"+CommonUtility.deNull(truckerNo) +" bkRefNo:"+CommonUtility.deNull(bkRefNo) 
			+" accountNo:"+CommonUtility.deNull(accountNo) +" custCd:"+CommonUtility.deNull(custCd) );
			if (listAccount == null || listAccount.size() == 0) {
				accNo = "No";
			} else {
				int size = listAccount.size();
				accNo = listAccount.get(size - 1).getAccNo();
				for (EsnListValueObject esnListValueObject : listAccount) {
					esnListValueObject.setCompanyName("Trucker - " + esnListValueObject.getAccNo());
					if (StringUtils.equalsIgnoreCase(esnListValueObject.getAccNo(), accountNo)) {
						isAccountContain = true;						
					}
				}

			}
			List<EsnListValueObject> userAccNo = esnRepo.getUserAccNo(bkRefNo, custCd, accNo);
			if (userAccNo != null && userAccNo.size() > 0) {
				for (EsnListValueObject esnListValueObject : userAccNo) {
					esnListValueObject.setCompanyName("ESN Declarant - " + esnListValueObject.getAccNo());
					if (StringUtils.equalsIgnoreCase(esnListValueObject.getAccNo(), accountNo)) {
						isUserAccountContain = true;						
					}
				}
			}
			log.info("isAccountContain : " + isAccountContain);
			log.info("isUserAccountContain : " + isUserAccountContain);
			list.addAll(listAccount);
			list.addAll(userAccNo);
			EsnListValueObject esnListValueObject = new EsnListValueObject();
			esnListValueObject.setAccNo("CA");
			esnListValueObject.setCompanyName("CASH PAYMENT");
			list.add(esnListValueObject);
			log.info("END: *** getListAccount Result *****" + list.size());
		} catch (BusinessException e) {
			log.info("Exception getListAccount : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getListAccount : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getListAccount Service");
		}
		return list;
	}

	@Override
	public String getCrgNm(String crgtyp) throws BusinessException {
		return manifestRepo.getCrgNm(crgtyp);
	}

	@Override
	public boolean isApplicableCargoCategory(String cargoType, String cargoCategory, String module)
			throws BusinessException {
		boolean contains = false;
		try {
			log.info("START: isApplicableCargoCategory Service" +" cargoType:"+CommonUtility.deNull(cargoType) 
			+" cargoCategory:"+CommonUtility.deNull(cargoCategory)
			+" module:"+CommonUtility.deNull(module));
			List<BookingReferenceValueObject> brvoList = getBRVOAmendList(module);
			BookingReferenceValueObject brvo = new BookingReferenceValueObject();
			for (BookingReferenceValueObject vo : brvoList) {
				if (vo.getCargoType().equals(cargoType)) {
					brvo = vo;
					break;
				}
			}
			String applicableCargoCategoryList = brvo.getCargoCategory();
			if(applicableCargoCategoryList != null) {
				contains = applicableCargoCategoryList.contains(cargoCategory);
			}
			
			
			log.info("END: *** isApplicableCargoCategory Result *****" + contains);
		} catch (BusinessException e) {
			log.info("Exception isApplicableCargoCategory : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception isApplicableCargoCategory : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isApplicableCargoCategory Service");
		}
		return contains;
	}

	public List<BookingReferenceValueObject> getBRVOAmendList(String module) throws BusinessException {
		List<BookingReferenceValueObject> brvoList = new ArrayList<BookingReferenceValueObject>();
		try {
			log.info("START: getBRVOAmendList Service"+" module:"+module );
			String cargoType_cargoCategoryString = bookingRefRepo.getAmendParaCargoTypeCode_CargoCategoryCode(module);
			String[] cargoTypeCargoCategory = cargoType_cargoCategoryString.split(",");
			for (int i = 0; i < cargoTypeCargoCategory.length; i++) {
				String[] oneCtCc = cargoTypeCargoCategory[i].split("-");
				BookingReferenceValueObject bookingReferenceVO = new BookingReferenceValueObject();
				bookingReferenceVO.setCargoType(oneCtCc[0]);
				bookingReferenceVO.setCargoCategory(formatApplicableCargoCategoryList(oneCtCc[1]));
				brvoList.add(bookingReferenceVO);
			}
			
			log.info("END: *** getBRVOAmendList Result *****" + brvoList.size());
		} catch (BusinessException e) {
			log.info("Exception getBRVOAmendList : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception getBRVOAmendList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getBRVOAmendList Service");
		}
		return brvoList;
	}

	private String formatApplicableCargoCategoryList(String cargoCategoryCodes) throws BusinessException {
		StringBuilder cargoCategoryName = new StringBuilder();
		try {
			log.info("START: formatApplicableCargoCategoryList Service"+" cargoCategoryCodes:"+CommonUtility.deNull(cargoCategoryCodes));
			Map<String, String> cargoCode_cargoName = bookingRefRepo.getCargoCategoryCode_CargoCategoryName();
			String[] applicableCargoCategoryCode = cargoCategoryCodes.split("/");
			for (int i = 0; i < applicableCargoCategoryCode.length; i++) {
				cargoCategoryName.append(cargoCode_cargoName.get(applicableCargoCategoryCode[i])).append("=")
						.append(applicableCargoCategoryCode[i]).append(",");
			}
			cargoCategoryName.deleteCharAt(cargoCategoryName.length() - 1);
			log.info("END: *** formatApplicableCargoCategoryList Result *****" + cargoCategoryName.toString());
		} catch (BusinessException e) {
			log.info("Exception formatApplicableCargoCategoryList : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception formatApplicableCargoCategoryList : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: formatApplicableCargoCategoryList Service");
		}
		return cargoCategoryName.toString();
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String MftUpdationForDPE(String usrid, String coCd, String seqno, String varno, String blno, String crgtyp,
			String hscd, String hsSubCodeFr, String hsSubCodeTo, String crgdesc, String mark, String nopkgs, String gwt,
			String gvol, String crgstat, String dgind, String stgind, String dop, String pkgtyp, String coname,
			String consigneeCoyCode, String poL, String poD, String poFD, String cntrtype, String cntrsize,
			String cntr1, String cntr2, String cntr3, String cntr4, String autParty, String adviseBy, String adviseDate,
			String adviseMode, String amendChargedTo, String waiveCharge, String waiveReason, String category,	
			String customHsCode, String conAddr, String shipperNm, String shipperAddr, String notifyParty,
			String notifyPartyAddr, String placeofDelivery, String placeofReceipt, List<HsCodeDetails> multiHsCodeList)
			throws BusinessException {

		return manifestRepo.MftUpdationForDPE(usrid, coCd, seqno, varno, blno, crgtyp, hscd, hsSubCodeFr, hsSubCodeTo,
				crgdesc, mark, nopkgs, gwt, gvol, crgstat, dgind, stgind, dop, pkgtyp, coname, consigneeCoyCode, poL,
				poD, poFD, cntrtype, cntrsize, cntr1, cntr2, cntr3, cntr4, autParty, adviseBy, adviseDate, adviseMode,
				amendChargedTo, waiveCharge, waiveReason, category, customHsCode, conAddr, shipperNm, shipperAddr,
				notifyParty, notifyPartyAddr, placeofDelivery, placeofReceipt,multiHsCodeList);
	}

	public List<DPEUtil> listCrgType(String vslType) throws BusinessException{
		return dpeCargoAmendRepo.listCrgType(vslType);
	}

	@Override
	public List<DPEUtil> getShipper(String name, String shipperCode, Criteria criteria) throws BusinessException {
		return dpeCargoAmendRepo.getShipper(name, shipperCode, criteria);
	}

	@Override
	public List<DPEUtil> currentListCargoCategory(String cargoTypeCode, String companyCode)
			throws BusinessException {
		return dpeCargoAmendRepo.currentListCargoCategory(cargoTypeCode, companyCode);
	}

	@Override
	public List<HSCode> listHsCode(String status) throws BusinessException {
		return dpeCargoAmendRepo.listHsCode(status);
	}

	@Override
	public List<DPEUtil> listHsSubCode(String status, String hsCode) throws BusinessException {
		return dpeCargoAmendRepo.listHsSubCode(status, hsCode);
	}

	@Override
	public List<DPEUtil> listAuthorizedParty(String name, String vvCd, Criteria criteria) throws BusinessException {
		return dpeCargoAmendRepo.listAuthorizedParty(name, vvCd, criteria);
	}

	@Override
	public List<DPEUtil> listPort(String name, Criteria criteria) throws BusinessException {
		return dpeCargoAmendRepo.listPort(name, criteria);
	}

	@Override
	public List<DPEUtil> listPackaging(String name, Criteria criteria) throws BusinessException {
		return dpeCargoAmendRepo.listPackaging(name, criteria);
	}

	@Override
	public List<DPEUtil> listCompany(String name, Criteria criteria) throws BusinessException {
		return dpeCargoAmendRepo.listCompany(name, criteria);
	}

	public TruckerValueObject getTruckerDetails(String truckerIc) throws BusinessException {
		return esnRepo.getTruckerDetails(truckerIc);
	}

	public boolean checkValidTrucker(String truckerIcNo) throws BusinessException {
		return esnRepo.checkValidTrucker(truckerIcNo);
	}

	@Override
	public List<EsnListValueObject> getAccNo(String truckNo) throws BusinessException {
		return esnRepo.getAccNo(truckNo);
	}

	@Override
	public List<EsnListValueObject> getUserAccNo(String truckerIcNo, String custCd, String accNo) throws BusinessException {
		return esnRepo.getUserAccNo(truckerIcNo, custCd, accNo);
	}

	@Override
	public DPEUtil getShipperInformation(String name) {
		return dpeCargoAmendRepo.getShipperInformation(name);
	}

	@Override
	public boolean chkNoOfPkgs(String bookingRefNo, int noOfPkgs) throws BusinessException {
		return esnRepo.chkNoOfPkgs(bookingRefNo, noOfPkgs);
	}

	@Override
	public boolean isOutWardPm(String bookingRefNo, String varNbr) throws BusinessException {
		return esnRepo.isOutWardPm(bookingRefNo, varNbr);
	}

	@Override
	public boolean chkPkgsType(String pkgsType) throws BusinessException {
		return esnRepo.chkPkgsType(pkgsType);
	}

	@Override
	public boolean chkWeight(String bookingRefNo, double weight) throws BusinessException {
		return esnRepo.chkWeight(bookingRefNo, weight);
	}

	@Override
	public boolean chkVolume(String bookingRefNo, double volume) throws BusinessException {
		return esnRepo.chkVolume(bookingRefNo, volume);
	}

	@Override
	public boolean chkAccNo(String accNo) throws BusinessException {
		return esnRepo.chkAccNo(accNo);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String updateCargoTypeCargoCategory(String bookingRefNo, String category, String cargoTypeCode)
			throws BusinessException {
		return bookingRefRepo.updateCargoTypeCargoCategory(bookingRefNo, category, cargoTypeCode);
	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public String esnUpdateForDPE(int noOfPkgs, String hscd, String pkgsType, String mark, String truckerName,
			String truckerNo, String lopInd, String dgInd, String stgInd, String loadingFrom, String poD,
			int noOfStorageDay, String dutiInt, String payMode, String accNo, String esnNbr, String cargoDes,
			String trucker_cd, double weight, double volume, String truckerCNo, String cntr1, String cntr2,
			String cntr3, String cntr4, String stfInd, String strUAFlag, String strUserID, String category,
			String hsSubCodeFr, String hsSubCodeTo, List<TruckerValueObject> truckerList, int mainTrkPkgs,
			String coCd, String customHsCode, List<HsCodeDetails> multiHsCodeList) throws BusinessException {
		return esnRepo.esnUpdateForDPE(noOfPkgs, hscd, pkgsType, mark, truckerName, truckerNo, lopInd, dgInd, stgInd,
				loadingFrom, poD, noOfStorageDay, dutiInt, payMode, accNo, esnNbr, cargoDes, trucker_cd, weight, volume,
				truckerCNo, cntr1, cntr2, cntr3, cntr4, stfInd, strUAFlag, strUserID, category, hsSubCodeFr,
				hsSubCodeTo, truckerList, mainTrkPkgs, coCd, "", "", "", customHsCode, multiHsCodeList);

	}

	@Transactional(rollbackFor = BusinessException.class)
	@Override
	public int updateBkDetails(String updateBkDetails, String shipperNbr, String shipperAddress, String shipperNm,
			String strUserID, String conNm, String conAddr, String shipperAddr, String notifyParty,
			String notifyPartyAddr, String placeofDelivery, String placeofReceipt, String blNbr) {
		return dpeCargoAmendRepo.updateBkDetails(updateBkDetails, shipperNbr, shipperAddress, shipperNm, strUserID,
				conNm, conAddr, shipperAddr, notifyParty, notifyPartyAddr, placeofDelivery, placeofReceipt, blNbr);

	}
	// End Region

	@Override
	public List<HsCodeDetails> getHsCodeDetailList(String seqno) throws BusinessException {
		return cargoManifestRepo.getHsCodeDetailList(seqno);
	}

	@Override
	public List<HsCodeDetails> getHsCodeEsnDetailList(String esnNo) throws BusinessException {
		return esnRepo.getHsCodeDetailList(esnNo);
	}
}
