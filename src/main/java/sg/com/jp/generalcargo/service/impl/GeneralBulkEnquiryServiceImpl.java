package sg.com.jp.generalcargo.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.com.jp.generalcargo.dao.DpeCargoRepository;
import sg.com.jp.generalcargo.dao.DpeUtilRepository;
import sg.com.jp.generalcargo.dao.ProcessGBGenericRepository;
import sg.com.jp.generalcargo.dao.ProcessGBStoreRepository;
import sg.com.jp.generalcargo.dao.TariffMainRepository;
import sg.com.jp.generalcargo.domain.CargoEnquiryDetails;
import sg.com.jp.generalcargo.domain.CargoEnquiryMgmtAction;
import sg.com.jp.generalcargo.domain.ChargeableBillValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.DPEUtil;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.domain.ProcessGBValueObject;
import sg.com.jp.generalcargo.domain.VesselTxnEventLogValueObject;
import sg.com.jp.generalcargo.service.GeneralBulkEnquiryService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;
@Service
public class GeneralBulkEnquiryServiceImpl implements GeneralBulkEnquiryService {
	private static final Log log = LogFactory.getLog(GeneralBulkEnquiryServiceImpl.class);
	@Autowired
	private DpeCargoRepository dpeCargoRepo;
	@Autowired
	private DpeUtilRepository dpeCargoUtilRepo;
	@Autowired
	private TariffMainRepository tariffMainRepo;
	@Autowired
	private ProcessGBStoreRepository processGbStoreRepo;
	@Autowired
	private ProcessGBGenericRepository processGBGeneric;
	// StartRegion listRecord - General and bulk cargo enquiry
		@Override
		public List<DPEUtil> listVesselByName(Integer start, Integer limit, String name, String coCd) throws BusinessException{
			return dpeCargoUtilRepo.listVesselByName(start, limit, name, coCd);
		}

		public int countVesselByName(String name, String coCd) throws BusinessException{
			return dpeCargoUtilRepo.countVesselByName(name, coCd);
		}

		public List<DPEUtil> getInVoyageList(String name, String coCd, String voyNbr, String ind) throws BusinessException {
			return dpeCargoUtilRepo.getInVoyageList(name, coCd, voyNbr, ind);
		}

		public List<DPEUtil> getOutVoyageList(String name, String coCd, String voyNbr, String ind) throws BusinessException {
			return dpeCargoUtilRepo.getOutVoyageList(name, coCd, voyNbr, ind);
		}

		public DPEUtil getVesselDetail(String name) throws BusinessException {
			return dpeCargoUtilRepo.getVesselDetail(name);
		}

		public List<DPEUtil> listCompanyByName(Integer start, Integer limit, String name)  throws BusinessException {
			return dpeCargoUtilRepo.listCompanyByName(start, limit, name);
		}

		public int countCompanyByName(String name) throws BusinessException {
			return dpeCargoUtilRepo.countCompanyByName(name);
		}

		@Override
		public Map<String, Object> getCargoEnquiryParamsMapByCustCd(String custCd) throws BusinessException {

			Map<String, Object> filters = new HashMap<String, Object>();
			filters.put("f_acc_nbr", getAccountNbrByCustCd(custCd));
			filters.put("f_trucker_ic", getTruckerIcByCustCd(custCd));
			filters.put("f_t_vv_cd", getVvCdByAbCd(custCd));
			filters.put("f_sub_adp", getEdoNbrBySubAdp(custCd));
			filters.put("f_sub_trucker", getEsnNbrBySubTrucker(custCd));
			return filters;
		}

		public List<String> getAccountNbrByCustCd(String custCd) throws BusinessException {
			return dpeCargoRepo.getAccountNbrByCustCd(custCd);
		}

		public List<String> getTruckerIcByCustCd(String custCd) throws BusinessException {
			return dpeCargoRepo.getTruckerIcByCustCd(custCd);
		}

		public List<String> getVvCdByAbCd(String custCd) throws BusinessException {
			return dpeCargoRepo.getVvCdByAbCd(custCd);
		}

		public List<String> getEdoNbrBySubAdp(String custCd) throws BusinessException {
			return dpeCargoRepo.getEdoNbrBySubAdp(custCd);
		}

		public List<String> getEsnNbrBySubTrucker(String custCd) throws BusinessException {
			return dpeCargoRepo.getEsnNbrBySubTrucker(custCd);
		}

		@Override
		public List<CargoEnquiryMgmtAction> listCargoRecords(Integer start, Integer limit, String sort, String dir, Map<String,Object> filters,
				Criteria criteria) throws Exception {
			return dpeCargoRepo.listCargoRecords(start, limit, sort, dir, filters, criteria);
		}

		@Override
		public int countCargoRecords(Map<String, Object> filters) throws Exception{
			return dpeCargoRepo.countCargoRecords(filters);
		}
		
		public CargoEnquiryDetails getCargoEnquiryRecord(String edoNbr, Long esnNbr, String type) throws BusinessException{
			CargoEnquiryDetails record=null ;
			try {
				log.info("START: getCargoEnquiryRecord  Service "+" edoNbr:"+ CommonUtility.deNull(edoNbr) +" esnNbr:"+ esnNbr 
						+" type:"+CommonUtility.deNull(type));
				record = dpeCargoRepo.getCargoRecord(edoNbr, esnNbr, type);
				log.info("record:"+record.toString());
			} catch (Exception e) {
				log.info("Exception getCargoEnquiryRecord : ", e);
				throw new BusinessException("M4201");
			}
			if (record != null) {
				try {
					log.info("START: getCargoEnquiryRecord  Service "+" edoNbr:"+ CommonUtility.deNull(edoNbr) +" esnNbr:"+ esnNbr 
							+" type:"+CommonUtility.deNull(type));
					List<VesselTxnEventLogValueObject> vslTxnArrayList = null;
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
					List<GeneralEventLogValueObject> gbEventLogList = null;
					List<ChargeableBillValueObject> discChargeableBillList	= new ArrayList<ChargeableBillValueObject>(1);
					List<ChargeableBillValueObject> loadChargeableBillList	= new ArrayList<ChargeableBillValueObject>(1);
					if (vslTxnArrayList != null ) {
						if (record.getEdo_asn_nbr() != null) {
							for (VesselTxnEventLogValueObject object : vslTxnArrayList) {
								gbEventLogList = dpeCargoRepo.getGBEventLog(object.getTxnDttm(), object.getVvCd(), record.getEdo_asn_nbr(), ProcessChargeConst.DISC_VV_IND);
								if (gbEventLogList != null) {
									for (GeneralEventLogValueObject generalEventLogValueObject : gbEventLogList) {
		                          	  List<ChargeableBillValueObject> storeRentList = new ArrayList<ChargeableBillValueObject>(1);
			                          storeRentList = processGbStoreRepo.calculateStoreBillCharge(generalEventLogValueObject, generalEventLogValueObject.getRefInd());
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
			                          storeRentList = processGbStoreRepo.calculateStoreBillCharge(generalEventLogValueObject, generalEventLogValueObject.getRefInd());
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

						List<ProcessGBValueObject>	processGBList = new ArrayList<ProcessGBValueObject>();
		                processGBList	= processGBGeneric.retrieveBillable(discChargeableBillList);
		                if (processGBList != null) {
		                	for (int i = 0; i < processGBList.size(); i ++) {
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
						List<ProcessGBValueObject>	processGBList	= new ArrayList<ProcessGBValueObject>();
		                processGBList		= processGBGeneric.retrieveBillable(loadChargeableBillList);
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
		public String getCompanyName(String coCd) throws BusinessException {
			return dpeCargoRepo.getCompanyName(coCd);
		}
		
		// End Region - General and bulk cargo enquiry
}
