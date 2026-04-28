package sg.com.jp.generalcargo.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.jp.generalcargo.dao.DpeCargoRepository;
import sg.com.jp.generalcargo.dao.ProcessGBGenericRepository;
import sg.com.jp.generalcargo.dao.ProcessGBStoreRepository;
import sg.com.jp.generalcargo.dao.ShutOutCargoRepository;
import sg.com.jp.generalcargo.dao.TariffMainRepository;
import sg.com.jp.generalcargo.domain.CargoEnquiryDetails;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.ProcessGBValueObject;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselTxnEventLogValueObject;
import sg.com.jp.generalcargo.service.OutwardCargoShutoutReportService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Service
public class OutwardCargoShutoutReportServiceImpl implements OutwardCargoShutoutReportService {

	@Autowired
	ShutOutCargoRepository shutOutCargoRepo;
	@Autowired
	DpeCargoRepository dpeCargoRepo;
	@Autowired
	TariffMainRepository tariffMainRepo;
	@Autowired
	ProcessGBStoreRepository processGBStoreRepo;
	@Autowired
	ProcessGBGenericRepository processGBGenericRepo;
	
	private static final Log log = LogFactory.getLog(OutwardCargoShutoutReportServiceImpl.class);

	@Override
	public TableResult getShutoutCargoMtrgList(String dateFrom, String dateTo, String vslName,
			String outVoyNbr, String esnEdoNbr, String cargoType, String terminal, String dwellDays, String custCode,
			Criteria criteria) throws BusinessException{
		return shutOutCargoRepo.getShutoutCargoMtrgList(dateFrom, dateTo, vslName, outVoyNbr, esnEdoNbr, cargoType,
				terminal, dwellDays,custCode,criteria);
	}

	@Override
	public int updateDeliveryStatus(String bkgRefNbr, String deliveredPackages, String deliveryRemarks, String userId, String userName, String dateTime, String status) throws BusinessException, SQLException {
		return shutOutCargoRepo.updateDeliveryStatus(bkgRefNbr, deliveredPackages, deliveryRemarks, userId, userName, dateTime, status);
	}
	
	public CargoEnquiryDetails getCargoEnquiryRecord(String edoNbr, Long esnNbr, String type) throws BusinessException{
		CargoEnquiryDetails record=null ;
		try {
			log.info("START: getCargoEnquiryRecord edoNbr:"+CommonUtility.deNull(edoNbr)+"esnNbr:"+esnNbr+"type:"+CommonUtility.deNull(type));
			record = dpeCargoRepo.getCargoRecord(edoNbr, esnNbr, type);
			log.info("record:"+record.toString());
		} catch (Exception e) {
			log.info("Exception getCargoEnquiryRecord : ", e);
			throw new BusinessException("M4201");
		}
		if (record != null) {
			try {
				log.info("START: getCargoEnquiryRecord edoNbr:"+CommonUtility.deNull(edoNbr)+"esnNbr:"+esnNbr+"type:"+CommonUtility.deNull(type));
				Collection<VesselTxnEventLogValueObject> vslTxnArrayList = null;
				//process to calculate FREE_STG_END
				if (record.getDisc_vv_cd() != null)  {
					// EDO case
					GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();
					String discVvCd = record.getDisc_vv_cd();// get from GB_EDO.VAR_NBR ?
					String edoAsnNbr = record.getEdo_asn_nbr(); // get from GB_EDO.EDO_ASN_NBR ?
					String mvmt = record.getDisc_status(); // get from EDO.CRG_STATUS (L: Local, T: Transhipment) ?
					String vvInd = ""; //how to get data for this param ?

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
					//in case of ESN
					GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();
					String esnAsnNbr = record.getEsn_asn_nbr(); // get from ESN.ESN_ASN_NBR ?
					String loadVvCd = record.getLoad_vv_cd();;// get from ESN.OUT_VOY_VAR_NBR?
					String vvInd = "123"; // how to get data for this param ?
					String mvmt = "123"; //how to get data for this param ?
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
				//process to calculate STORE RENT AMOUNT
				Collection<GeneralEventLogValueObject> gbEventLogList = null;
				List<ChargeableBillValueObject>	discChargeableBillList	= new ArrayList<ChargeableBillValueObject>(1);
				List<ChargeableBillValueObject>	loadChargeableBillList	= new ArrayList<ChargeableBillValueObject>(1);
				if (vslTxnArrayList != null) {
					if (record.getEdo_asn_nbr() != null) {
						for (VesselTxnEventLogValueObject object : vslTxnArrayList) {
							gbEventLogList = dpeCargoRepo.getGBEventLog(object.getTxnDttm(), object.getVvCd(), record.getEdo_asn_nbr(), ProcessChargeConst.DISC_VV_IND);
							if (gbEventLogList != null) {
								for (GeneralEventLogValueObject generalEventLogValueObject : gbEventLogList) {
	                          	  List<ChargeableBillValueObject> storeRentList = new ArrayList<ChargeableBillValueObject>(1);
		                          storeRentList = processGBStoreRepo.calculateStoreBillCharge(generalEventLogValueObject, generalEventLogValueObject.getRefInd());
		                          for (int k=0; k<storeRentList.size(); k++) {
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
							gbEventLogList = dpeCargoRepo.getGBEventLog(object.getTxnDttm(), object.getVvCd(), record.getEsn_asn_nbr(), ProcessChargeConst.LOAD_VV_IND);
							if (gbEventLogList != null) {
								for (GeneralEventLogValueObject generalEventLogValueObject : gbEventLogList) {
	                          	  List<ChargeableBillValueObject> storeRentList = new ArrayList<ChargeableBillValueObject>(1);
		                          storeRentList = processGBStoreRepo.calculateStoreBillCharge(generalEventLogValueObject, generalEventLogValueObject.getRefInd());
		                          for (int k=0; k<storeRentList.size(); k++) {
		                              ChargeableBillValueObject chargeValueObject = new ChargeableBillValueObject();
		                              chargeValueObject = storeRentList.get(k);
		                              loadChargeableBillList.add(chargeValueObject);
		                          }
							  }
							}
						}
					}
				}
				if (discChargeableBillList.size() > 0) {
					double discStoreAmount = 0;

					List<ProcessGBValueObject>	processGBList		= new ArrayList<ProcessGBValueObject>();
	                processGBList	= processGBGenericRepo.retrieveBillable(discChargeableBillList);
	                if (processGBList != null) {
	                	for (int i = 0; i < processGBList.size(); i ++) {
	                		ChargeableBillValueObject chargeObject =  processGBList.get(i).getCharge(i);
	                		discStoreAmount = discStoreAmount + chargeObject.getTotalChargeAmt();
	                	}
	                }
	               if (discStoreAmount > 0) {
	            	   record.setDisc_store_rent_amount(new BigDecimal(discStoreAmount));
	               }
				}

				if (loadChargeableBillList.size() > 0) {
					double loadStoreAmount = 0;
					List<ProcessGBValueObject>	processGBList		= new ArrayList<ProcessGBValueObject>();
	                processGBList		= processGBGenericRepo.retrieveBillable(loadChargeableBillList);
	                if (processGBList != null) {
	                	for (int i = 0; i < processGBList.size(); i ++) {
	                		ChargeableBillValueObject chargeObject = processGBList.get(i).getCharge(i);
	                		loadStoreAmount = loadStoreAmount + chargeObject.getTotalChargeAmt();
	                	}
	                }
	               if (loadStoreAmount > 0) {
	            	   record.setDisc_store_rent_amount(new BigDecimal(loadStoreAmount));
	               }
				}
				log.info("END: *** getCargoEnquiryRecord Result *****" + record);
			} catch (Exception e) {
				log.info("Exception getCargoEnquiryRecord : ", e);
				throw new BusinessException("M4201");
			}
			finally {
				log.info("END: getCargoEnquiryRecord SERVICE");
			}
			}
		return record;
	}

	@Override
	public String getUserNameMap(String userId) throws BusinessException {
		return shutOutCargoRepo.getUserNameMap(userId);
	}
}
