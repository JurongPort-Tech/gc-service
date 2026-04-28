package sg.com.jp.generalcargo.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.jp.generalcargo.dao.BookingRefRepository;
import sg.com.jp.generalcargo.dao.DpeCargoRepository;
import sg.com.jp.generalcargo.dao.DpeOutstandingRepository;
import sg.com.jp.generalcargo.dao.DpeUtilRepository;
import sg.com.jp.generalcargo.dao.ProcessGBGenericRepository;
import sg.com.jp.generalcargo.dao.ProcessGBStoreRepository;
import sg.com.jp.generalcargo.dao.TariffMainRepository;
import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.CargoEnquiryDetails;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.OutstandingVO;
import sg.com.jp.generalcargo.domain.ProcessGBValueObject;
import sg.com.jp.generalcargo.domain.VesselTxnEventLogValueObject;
import sg.com.jp.generalcargo.service.OutstandingEdoEsnService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.DpeCommonUtil;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Service("OutstandingEdoEsnServiceImpl")
public class OutstandingEdoEsnServiceImpl implements OutstandingEdoEsnService {

	private static final Log log = LogFactory.getLog(OutstandingEdoEsnServiceImpl.class);
	private static final String EDO_TYPE = "GB_EDO";
	private static final String ESN_TYPE = "ESN";
	private boolean isAccountContain = false;
	private boolean isUserAccountContain = false;
	@Autowired
	DpeOutstandingRepository dpeOutRepo;
	@Autowired
	DpeCargoRepository dpeCargoRepo;
	@Autowired
	DpeUtilRepository dpeUtilRepo;
	@Autowired
	ProcessGBGenericRepository processGBGenericRepo;
	@Autowired
	ProcessGBStoreRepository processGBStoreRepo;
	@Autowired
	TariffMainRepository tariffMainRepo;
	@Autowired
	BookingRefRepository bookRefRepo;

	@Override
	public List<DPEUtil> listHaulierCompanyByName(Integer start, Integer limit, String name) throws BusinessException {
		return dpeUtilRepo.listHaulierCompanyByName(start, limit, name);
	}

	public int countHaulierCompanyByName(String name) throws BusinessException {
		return dpeUtilRepo.countHaulierCompanyByName(name);
	}

	@Override
	public int countRecords(Map<String, Object> filters) throws BusinessException {
		return dpeOutRepo.countRecords(filters);
	}

	@Override
	public List<OutstandingVO>  listRecords(Integer start, Integer limit, String sort, String dir, Map<String, Object> filters,
			Criteria criteria, Boolean needAllData) throws BusinessException {
		List<OutstandingVO> resultList = dpeOutRepo.listRecords(start, limit, sort, dir, filters, criteria, needAllData);
		try {
			log.info("START: listRecords Service" +" start:"+start  +" limit:"+limit  +" sort:"+sort  +" dir:"+ CommonUtility.deNull(dir) +
					" filters:"+filters +" criteria:"+criteria.toString()+" needAllData:"+needAllData);
			for (OutstandingVO outVo : resultList) {
				// process to calculate FREE_STG_END
				if (EDO_TYPE.equalsIgnoreCase(outVo.getCrg_type())) {
					// EDO case
					GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();
					String discVvCd = outVo.getVar_nbr();// get from GB_EDO.VAR_NBR ?
					String loadVvCd = "123";// how to get data for this param ?
					String edoAsnNbr = outVo.getEdo_asn_nbr(); // get from GB_EDO.EDO_ASN_NBR ?
					String mvmt = outVo.getCrg_status(); // get from EDO.CRG_STATUS (L: Local, T: Transhipment) ?
					String vvInd = ""; // how to get data for this param ?

					generalEventLogValueObject.setEdoAsnNbr(edoAsnNbr);
					generalEventLogValueObject.setLoadVvCd(loadVvCd);
					generalEventLogValueObject.setDiscVvCd(discVvCd);
					generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_EDO);
					generalEventLogValueObject.setMvmt(mvmt);
					generalEventLogValueObject.setVvInd(vvInd);

					int dayFps = tariffMainRepo.getGeneralCargoCustFspDays(generalEventLogValueObject);
					Date freeStgDttm = DpeCommonUtil.addDayToDate(outVo.getCod_dttm(), dayFps);
					outVo.setEdo_free_stg_end(freeStgDttm != null ? new Timestamp(freeStgDttm.getTime()) : null);

				} else if (ESN_TYPE.equalsIgnoreCase(outVo.getCrg_type())) {
					// in case of ESN
					GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();
					String esnAsnNbr = outVo.getEsn_asn_nbr(); // get from ESN.ESN_ASN_NBR ?
					String discVvCd = outVo.getOut_voy_nbr(); // get from ESN.OUT_VOY_VAR_NBR?
					String loadVvCd = "123";// how to get data for this param ?
					String vvInd = "123"; // how to get data for this param ?
					String mvmt = "123"; // how to get data for this param ?
					generalEventLogValueObject.setEdoAsnNbr(esnAsnNbr);
					generalEventLogValueObject.setLoadVvCd(loadVvCd);
					generalEventLogValueObject.setDiscVvCd(discVvCd);
					generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_ESN);
					generalEventLogValueObject.setMvmt(mvmt);
					generalEventLogValueObject.setVvInd(vvInd);
					int dayFps = tariffMainRepo.getGeneralCargoCustFspDays(generalEventLogValueObject);
					Date freeStgDttm = DpeCommonUtil.addDayToDate(outVo.getFirst_ua(), dayFps);
					outVo.setEsn_free_stg_end(freeStgDttm != null ? new Timestamp(freeStgDttm.getTime()) : null);
				}
			}
			log.info("Finish listRecords::: resultList = " + resultList.size());

		} catch (Exception e) {
			log.info("Exception listRecords : ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: listRecords Service");
		}
		return resultList;
	}

	public DPEUtil getVesselDetail(String name) throws BusinessException {
		return dpeUtilRepo.getVesselDetail(name);
	}

	@Override
	public List<DPEUtil> listVesselByName(Integer start, Integer limit, String name, String coCd) throws BusinessException {
		return dpeUtilRepo.listVesselByName(start, limit, name, coCd);
	}

	public int countVesselByName(String name, String coCd) throws BusinessException {
		return dpeUtilRepo.countVesselByName(name, coCd);
	}

	public List<DPEUtil> getInVoyageList(String name, String coCd, String voyNbr, String ind) throws BusinessException {
		return dpeUtilRepo.getInVoyageList(name, coCd, voyNbr, ind);
	}

	public List<DPEUtil> getOutVoyageList(String name, String coCd, String voyNbr, String ind) throws BusinessException {
		return dpeUtilRepo.getOutVoyageList(name, coCd, voyNbr, ind);
	}

	@Override
	public CargoEnquiryDetails getCargoEnquiryRecord(String edoNbr, Long esnNbr, String type) throws BusinessException {
		CargoEnquiryDetails record = null;
		try {
			log.info("START: getCargoEnquiryRecord edoNbr:" + CommonUtility.deNull(edoNbr) + "esnNbr:" + esnNbr + "type:" + CommonUtility.deNull(type));
			record = dpeCargoRepo.getCargoRecord(edoNbr, esnNbr, type);
			log.info("record:" + record.toString());
		} catch (Exception e) {
			log.info("Exception getCargoEnquiryRecord : ", e);
			throw new BusinessException("M4201");
		}
		if (record != null) {
			try {
				record = dpeCargoRepo.getCargoRecord(edoNbr, esnNbr, type);
				log.info("START: getCargoEnquiryRecord edoNbr:" + CommonUtility.deNull(edoNbr) + "esnNbr:" + esnNbr + "type:" + CommonUtility.deNull(type));
				Collection<VesselTxnEventLogValueObject> vslTxnArrayList = null;
				// process to calculate FREE_STG_END
				if (record.getDisc_vv_cd() != null) {
					// EDO case
					GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();
					String discVvCd = record.getDisc_vv_cd();// get from GB_EDO.VAR_NBR ?
					String edoAsnNbr = record.getEdo_asn_nbr(); // get from GB_EDO.EDO_ASN_NBR ?
					String mvmt = record.getDisc_status(); // get from EDO.CRG_STATUS (L: Local, T: Transhipment) ?
					String vvInd = ""; // how to get data for this param ?

					generalEventLogValueObject.setEdoAsnNbr(edoAsnNbr);
					generalEventLogValueObject.setDiscVvCd(discVvCd);
					generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_EDO);
					generalEventLogValueObject.setMvmt(mvmt);
					generalEventLogValueObject.setVvInd(vvInd);

					int dayFps = tariffMainRepo.getGeneralCargoCustFspDays(generalEventLogValueObject);
					Date freeStgDttm = CommonUtil.addDayToDate(record.getCompletetion_disc(), dayFps);
					if (freeStgDttm != null) {
						record.setDisc_free_store_rent_expiry(CommonUtil.formatDateTime(freeStgDttm));
					}
					vslTxnArrayList = dpeCargoRepo.getUnprocessGBVesselTxnEventLog(record.getDisc_vv_cd());
				}
				if (record.getLoad_vv_cd() != null) {
					// in case of ESN
					GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();
					String esnAsnNbr = record.getEsn_asn_nbr(); // get from ESN.ESN_ASN_NBR ?
					String loadVvCd = record.getLoad_vv_cd();
					;// get from ESN.OUT_VOY_VAR_NBR?
					String vvInd = "123"; // how to get data for this param ?
					String mvmt = "123"; // how to get data for this param ?
					generalEventLogValueObject.setEdoAsnNbr(esnAsnNbr);
					generalEventLogValueObject.setLoadVvCd(loadVvCd);
					generalEventLogValueObject.setRefInd(ProcessChargeConst.REF_IND_ESN);
					generalEventLogValueObject.setMvmt(mvmt);
					generalEventLogValueObject.setVvInd(vvInd);
					int dayFps = tariffMainRepo.getGeneralCargoCustFspDays(generalEventLogValueObject);
				
					Date freeStgDttm = CommonUtil.addDayToDate(record.getFirstUa(), dayFps);
					if (freeStgDttm != null) {
						record.setLoad_free_store_rent_expiry(CommonUtil.formatDateTime(freeStgDttm));
					}
					if (vslTxnArrayList == null || vslTxnArrayList.isEmpty()) {
						vslTxnArrayList = dpeCargoRepo.getUnprocessGBVesselTxnEventLog(record.getLoad_vv_cd());
					}
				}
				// process to calculate STORE RENT AMOUNT
				Collection<GeneralEventLogValueObject> gbEventLogList = null;
				List<ChargeableBillValueObject> discChargeableBillList = new ArrayList<ChargeableBillValueObject>(1);
				List<ChargeableBillValueObject> loadChargeableBillList = new ArrayList<ChargeableBillValueObject>(1);
				if (vslTxnArrayList != null) {
					if (record.getEdo_asn_nbr() != null) {
						for (VesselTxnEventLogValueObject object : vslTxnArrayList) {
							gbEventLogList = dpeCargoRepo.getGBEventLog(object.getTxnDttm(), object.getVvCd(),
									record.getEdo_asn_nbr(), ProcessChargeConst.DISC_VV_IND);
							if (gbEventLogList != null) {
								for (GeneralEventLogValueObject generalEventLogValueObject : gbEventLogList) {
									List<ChargeableBillValueObject> storeRentList = new ArrayList<ChargeableBillValueObject>(1);
									storeRentList = processGBStoreRepo.calculateStoreBillCharge(generalEventLogValueObject,
											generalEventLogValueObject.getRefInd());
									for (int k = 0; k < storeRentList.size(); k++) {
										ChargeableBillValueObject chargeValueObject = new ChargeableBillValueObject();
										chargeValueObject = storeRentList.get(k);
										discChargeableBillList.add(chargeValueObject);
									}
								}
							}
						}
					}
					if (record.getEsn_asn_nbr() != null) {
						for (VesselTxnEventLogValueObject object : vslTxnArrayList) {
							gbEventLogList = dpeCargoRepo.getGBEventLog(object.getTxnDttm(), object.getVvCd(),
									record.getEsn_asn_nbr(), ProcessChargeConst.LOAD_VV_IND);
							if (gbEventLogList != null) {
								for (GeneralEventLogValueObject generalEventLogValueObject : gbEventLogList) {
									List<ChargeableBillValueObject> storeRentList = new ArrayList<ChargeableBillValueObject>(1);
									storeRentList = processGBStoreRepo.calculateStoreBillCharge(generalEventLogValueObject,
											generalEventLogValueObject.getRefInd());
									for (int k = 0; k < storeRentList.size(); k++) {
										ChargeableBillValueObject chargeValueObject = new ChargeableBillValueObject();
										chargeValueObject =  storeRentList.get(k);
										loadChargeableBillList.add(chargeValueObject);
									}
								}
							}
						}
					}
				}
				if (discChargeableBillList.size() > 0) {
					double discStoreAmount = 0;

					List<ProcessGBValueObject> processGBList = new ArrayList<ProcessGBValueObject>();
					processGBList = processGBGenericRepo.retrieveBillable(discChargeableBillList);
					if (processGBList != null) {
						for (int i = 0; i < processGBList.size(); i++) {
							ChargeableBillValueObject chargeObject = processGBList.get(i).getCharge(i);
							discStoreAmount = discStoreAmount + chargeObject.getTotalChargeAmt();
						}
					}
					if (discStoreAmount > 0) {
						record.setDisc_store_rent_amount(new BigDecimal(discStoreAmount));
					}
				}

				if (loadChargeableBillList.size() > 0) {
					double loadStoreAmount = 0;
					List<ProcessGBValueObject> processGBList = new ArrayList<ProcessGBValueObject>();
					processGBList = processGBGenericRepo.retrieveBillable(loadChargeableBillList);
					if (processGBList != null) {
						for (int i = 0; i < processGBList.size(); i++) {
							ChargeableBillValueObject chargeObject =  processGBList.get(i).getCharge(i);
							loadStoreAmount = loadStoreAmount + chargeObject.getTotalChargeAmt();
						}
					}
					if (loadStoreAmount > 0) {
						record.setDisc_store_rent_amount(new BigDecimal(loadStoreAmount));
					}
				}
				log.info("record : " + record.toString());
			} catch (Exception e) {
				log.info("Exception getCargoEnquiryRecord : ", e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END: getCargoEnquiryRecord SERVICE");
			}
		}
		return record;
	}

	@Override
	public List<List<String>> getCargoType() throws BusinessException {
		return bookRefRepo.getCargoType();
	}

	@Override
	public Map<String, String> getCargoCategoryCode_CargoCategoryName() throws BusinessException {
		return bookRefRepo.getCargoCategoryCode_CargoCategoryName();
	}

	@Override
	public List<BookingReferenceValueObject> fetchBKDetails(String brno) throws BusinessException {
		return bookRefRepo.fetchBKDetails(brno);
	}

	@Override
	public String chkCancelAmend(String brno, String userCoyCode, String mode) throws BusinessException {
		return bookRefRepo.chkCancelAmend(brno, userCoyCode, mode);
	}

	@Override
	public boolean getCheckUserBookingReference(String coCd, String brno) throws BusinessException {
		return bookRefRepo.getCheckUserBookingReference(coCd, brno);
	}

	@Override
	public int retrieveMaxCargoTon(String varno) throws BusinessException {
		return bookRefRepo.retrieveMaxCargoTon(varno);
	}

}
