package sg.com.jp.generalcargo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import sg.com.jp.generalcargo.dao.CabDataFetchRepository;
import sg.com.jp.generalcargo.dao.ProcessGBLogRepository;
import sg.com.jp.generalcargo.domain.CabDataFetch;
import sg.com.jp.generalcargo.domain.GbmsCargoBillingValueObject;
import sg.com.jp.generalcargo.domain.GeneralEventLogValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("CabDataFetchRepository")
public abstract class CabDataFetchJdbcRepo extends CabDataFetch implements CabDataFetchRepository {
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	ProcessGBLogRepository processGBLogRepo;
	
	private static final Log log = LogFactory.getLog(CabDataFetchJdbcRepo.class);
	
	/**
	 * Abstract method
	 * 
	 * @param java.lang.String strcode
	 * @param java.lang.String struserid
	 * @exception @return java.util.ArrayList
	 */	
	protected abstract Map<String, ArrayList<?>> getSqlDetails(String strcode);

	/**
	 * Abstract method
	 * 
	 * @param GbmsCargoBillingValueObject gbmsCargoBillingValueObject
	 * @exception @return java.lang.String
	 */
	protected abstract Map<String, Object> compareSqlDetails(GbmsCargoBillingValueObject gbmsCargoBillingValueObject);
	
	@Override
	public List<GeneralEventLogValueObject> processDetails(String strcode, String struserid) throws BusinessException {
		List<GbmsCargoBillingValueObject> GbmsCargoBillingArrayList = new ArrayList<GbmsCargoBillingValueObject>();
		List<GeneralEventLogValueObject> GeneralEventLogArrayList = new ArrayList<GeneralEventLogValueObject>();
		SqlRowSet rs1 = null;
		SqlRowSet rs2 = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String sql1 = "";
		try {
			log.info("START processDetails DAO :: strcode: " + CommonUtility.deNull(strcode) + " struserid: " + CommonUtility.deNull(struserid));
			Map<String, ArrayList<?>> arraylist = new HashMap<>();
			arraylist = getSqlDetails(strcode);
			
//			@SuppressWarnings("unchecked")
			List<String> sqlarraylist = (List<String>) arraylist.get("sql");
			
//			@SuppressWarnings("unchecked")
			List<Map<String, String>> maparraylist = (List<Map<String, String>>) arraylist.get("paramMap");
			
			for (int i = 0; i < sqlarraylist.size(); i++) {
				paramMap = new HashMap<>();
				sql1 = (String) sqlarraylist.get(i);
				paramMap = maparraylist.get(i);
				log.info("SQL " + (i+1) + "******************************* " + sql1.toString() + " paramMap: " + paramMap);
				rs1 = namedParameterJdbcTemplate.queryForRowSet(sql1.toString(), paramMap);
				while (rs1.next()) {
					log.info("inside rs sql1=" + sql1);
					GbmsCargoBillingValueObject gbmsCargoBillingValueObject = new GbmsCargoBillingValueObject();
					gbmsCargoBillingValueObject.setCargoCategory(CommonUtility.deNull(rs1.getString("cargoCategory")));
					String AbCd = CommonUtility.deNull(rs1.getString("AbCd"));
					gbmsCargoBillingValueObject.setVesselScheme(getVesselScheme(CommonUtility.deNull(rs1.getString("vesselScheme")), AbCd));
					gbmsCargoBillingValueObject.setCargoStatus(CommonUtility.deNull(rs1.getString("cargoStatus")));
					gbmsCargoBillingValueObject.setTransStatus(CommonUtility.deNull(rs1.getString("transStatus")));
					gbmsCargoBillingValueObject.setMixedSchemeAcct(CommonUtility.deNull(rs1.getString("mixedSchemeAcct")));
					gbmsCargoBillingValueObject.setSaAcct(CommonUtility.deNull(rs1.getString("saAcct")));
					gbmsCargoBillingValueObject.setAbAcct(CommonUtility.deNull(rs1.getString("abAcct")));
					gbmsCargoBillingValueObject.setCargoAcct(getCargoAcct(CommonUtility.deNull(rs1.getString("cargoAcct"))));
					gbmsCargoBillingValueObject.setAbCd(AbCd);
					gbmsCargoBillingValueObject.setPaymentMode(CommonUtility.deNull(rs1.getString("paymentMode")));
					gbmsCargoBillingValueObject.setTransType(CommonUtility.deNull(rs1.getString("transType")));
					gbmsCargoBillingValueObject.setVesselType(getVesselType(CommonUtility.deNull(rs1.getString("type"))));
					gbmsCargoBillingValueObject.setOpsInd(CommonUtility.deNull(rs1.getString("opsInd")));
					gbmsCargoBillingValueObject.setSubCat(getSubCat(CommonUtility.deNull(rs1.getString("cargoCategory"))));
					gbmsCargoBillingValueObject.setDiscVvCd(CommonUtility.deNull(rs1.getString("discVvCd")));
					gbmsCargoBillingValueObject.setLoadVvCd(CommonUtility.deNull(rs1.getString("loadVvCd")));
					gbmsCargoBillingValueObject.setVvInd(CommonUtility.deNull(rs1.getString("vvInd")));
					gbmsCargoBillingValueObject.setBusinessType(CommonUtility.deNull(rs1.getString("businessType")));
					gbmsCargoBillingValueObject.setSchemeCd(CommonUtility.deNull(rs1.getString("schemeCd")));
					gbmsCargoBillingValueObject.setTariffMainCatCd(CommonUtility.deNull(rs1.getString("tariffMainCatCd")));
					gbmsCargoBillingValueObject.setTariffSubCatCd(CommonUtility.deNull(rs1.getString("tariffSubCatCd")));
					gbmsCargoBillingValueObject.setMvmt(CommonUtility.deNull(rs1.getString("mvmt")));
					gbmsCargoBillingValueObject.setType(CommonUtility.deNull(rs1.getString("type")));
					gbmsCargoBillingValueObject.setCargoType(CommonUtility.deNull(rs1.getString("cargoType")));
					gbmsCargoBillingValueObject.setLocalLeg(CommonUtility.deNull(rs1.getString("localLeg")));
					gbmsCargoBillingValueObject.setDiscGateway(CommonUtility.deNull(rs1.getString("discGateway")));
					gbmsCargoBillingValueObject.setRefInd(CommonUtility.deNull(rs1.getString("refInd")));
					gbmsCargoBillingValueObject.setBillAcctNbr(CommonUtility.deNull(rs1.getString("billAcctNbr")));
					gbmsCargoBillingValueObject.setPrintDttm(getPrintDttm(rs1.getObject("printDttm")));
					gbmsCargoBillingValueObject.setBillInd(CommonUtility.deNull(rs1.getString("billInd")));
					gbmsCargoBillingValueObject.setLastModifyUserId(struserid);
					gbmsCargoBillingValueObject.setLastModifyDttm(getLastModifyDttm(rs1.getObject("lastModifyDttm")));
					gbmsCargoBillingValueObject.setBlNbr(CommonUtility.deNull(rs1.getString("blNbr")));
					gbmsCargoBillingValueObject.setEdoAsnNbr(CommonUtility.deNull(rs1.getString("edoAsnNbr")));
					gbmsCargoBillingValueObject.setBkRefNbr(CommonUtility.deNull(rs1.getString("bkRefNbr")));
					gbmsCargoBillingValueObject.setEsnAsnNbr(CommonUtility.deNull(rs1.getString("esnAsnNbr")));
					gbmsCargoBillingValueObject.setDnNbr(CommonUtility.deNull(rs1.getString("dnNbr")));
					gbmsCargoBillingValueObject.setUaNbr(CommonUtility.deNull(rs1.getString("uaNbr")));
					gbmsCargoBillingValueObject.setBillTonBl(getBillTonBl(CommonUtility.deNull(rs1.getString("billTonBl"))));
					gbmsCargoBillingValueObject.setBillTonEdo(getBillTonEdo(CommonUtility.deNull(rs1.getString("billTonEdo"))));
					gbmsCargoBillingValueObject.setBillTonDn(getBillTonDn(CommonUtility.deNull(rs1.getString("billTonDn"))));
					gbmsCargoBillingValueObject.setBillTonEsn(getBillTonEsn(CommonUtility.deNull(rs1.getString("billTonEsn"))));
					gbmsCargoBillingValueObject.setBillTonBkg(getBillTonBkg(CommonUtility.deNull(rs1.getString("billTonBkg"))));
					gbmsCargoBillingValueObject.setLoadTonCs(getLoadTonCs(CommonUtility.deNull(rs1.getString("loadTonCs"))));
					gbmsCargoBillingValueObject.setShutoutTonCs(getShutoutTonCs(CommonUtility.deNull(rs1.getString("shutoutTonCs"))));
					gbmsCargoBillingValueObject.setCountUnit(getCountUnit(CommonUtility.deNull(rs1.getString("countUnit"))));
					gbmsCargoBillingValueObject.setTotalPackEdo(getTotalPackEdo(CommonUtility.deNull(rs1.getString("totalPackEdo"))));
					gbmsCargoBillingValueObject.setTotalPackDn(getTotalPackDn(CommonUtility.deNull(rs1.getString("totalPackDn"))));
					gbmsCargoBillingValueObject.setMschInd(rs1.getString("MschInd"));
					gbmsCargoBillingValueObject.setChType(rs1.getString("chval"));
					gbmsCargoBillingValueObject.setEdotAcctNbr(rs1.getString("edoacctnbr"));
					gbmsCargoBillingValueObject.setFirstcarSch(rs1.getString("firstcarsch"));
					gbmsCargoBillingValueObject.setSatenind(rs1.getString("SAtenind"));
					gbmsCargoBillingValueObject.setSctenind(rs1.getString("SCtenind"));
					gbmsCargoBillingValueObject.setWhind(rs1.getString("whind"));
					gbmsCargoBillingValueObject.setFsdays(rs1.getString("fsdays"));
					log.info("ducta1 $$$$$$$$$$$CHECK RORO$$$$$$$$$$$$$");
					// ducta1 start on 26/12/2008
					String typeForRORO = CommonUtility.deNull(rs1.getString("type"));
					log.info("typeForRORO 1" + typeForRORO);
					String tariffSubCatCd = "";
					if (ProcessChargeConst.CARGO_CATEGORY_CODE.PASSENGER_CAR.equals(typeForRORO)
							|| ProcessChargeConst.CARGO_CATEGORY_CODE.STATION_WAGON_VAN.equals(typeForRORO)
							|| ProcessChargeConst.CARGO_CATEGORY_CODE.BUSES_LORRIES.equals(typeForRORO)) {

						tariffSubCatCd = ProcessChargeConst.TARIFF_SUB_WHARF_RORO_VSL;
						gbmsCargoBillingValueObject.setType(typeForRORO);
						gbmsCargoBillingValueObject.setTariffSubCatCd(tariffSubCatCd);
						String mvmt = processMvmtForRORO(CommonUtility.deNull(rs1.getString("mvmt")), gbmsCargoBillingValueObject);
						gbmsCargoBillingValueObject.setMvmt(mvmt);
					}
					// ducta1 end
					log.info("ducta1 $$$$$$$$$$$$$$$$$$$$$ ADD Success $$$$$$$$$$$$$$$$$$$$");
					GbmsCargoBillingArrayList.add(gbmsCargoBillingValueObject);
				}
			}
		} catch (Exception e) {
			log.info("Exception processDetails : ", e);
			throw new BusinessException("M4201");
		}
		for (int i = 0; i < GbmsCargoBillingArrayList.size(); i++) {
			GbmsCargoBillingValueObject gbmsCargoBillingValueObject = new GbmsCargoBillingValueObject();
			gbmsCargoBillingValueObject = (GbmsCargoBillingValueObject) GbmsCargoBillingArrayList.get(i);
			String sql2 = new String();
			Map<String, Object> sqlmap = new HashMap<>();
			try {
				sqlmap = compareSqlDetails(gbmsCargoBillingValueObject);
				sql2 = (String) sqlmap.get("sql");
				
//				@SuppressWarnings("unchecked")
				Map<String, String> paramMap1 = (Map<String, String>) sqlmap.get("paramMap");
				
				log.info("SQL " + (i+1) + "******************************* " + sql2.toString() + " paramMap: " + paramMap1);
				rs2 = namedParameterJdbcTemplate.queryForRowSet(sql2.toString(), paramMap1);
				while (rs2.next()) {
					log.info("got sql2 inside rs :" + sql2);
					GeneralEventLogValueObject generalEventLogValueObject = new GeneralEventLogValueObject();
					
					String discVvCd = CommonUtility.deNull(rs2.getString("DISC_VV_CD"));
					String loadVvCd = CommonUtility.deNull(rs2.getString("LOAD_VV_CD"));
					String vvInd = CommonUtility.deNull(rs2.getString("VV_IND"));
					String businessType = CommonUtility.deNull(rs2.getString("BUSINESS_TYPE"));
					String SchemeCd = CommonUtility.deNull(rs2.getString("SCHEME_CD"));
					String tariffMainCatCd = CommonUtility.deNull(rs2.getString("TARIFF_MAIN_CAT_CD"));
					String tariffSubCatCd = CommonUtility.deNull(rs2.getString("TARIFF_SUB_CAT_CD"));
					String mvmt = CommonUtility.deNull(rs2.getString("MVMT"));
					String type = CommonUtility.deNull(rs2.getString("TYPE"));
					String cargoType = CommonUtility.deNull(rs2.getString("CARGO_TYPE"));
					String localLeg = CommonUtility.deNull(rs2.getString("LOCAL_LEG"));
					String discGateWay = CommonUtility.deNull(rs2.getString("DISC_GATEWAY"));
					String refInd = CommonUtility.deNull(rs2.getString("REF_IND"));
					String blNbr = CommonUtility.deNull(rs2.getString("BL_NBR"));
					String edoAsnNbr = CommonUtility.deNull(rs2.getString("EDO_ASN_NBR"));
					String bkRefNbr = CommonUtility.deNull(rs2.getString("BK_REF_NBR"));
					String esnAsnNbr = CommonUtility.deNull(rs2.getString("ESN_ASN_NBR"));
					String dnNbr = CommonUtility.deNull(rs2.getString("DN_NBR"));
					String uaNbr = CommonUtility.deNull(rs2.getString("UA_NBR"));
					double billTonBl = rs2.getDouble("BILL_TON_BL");
					double billTonEdo = rs2.getDouble("BILL_TON_EDO");
					double billTonDn = rs2.getDouble("BILL_TON_DN");
					double billTonEsn = rs2.getDouble("BILL_TON_ESN");
					double billTonBkg = rs2.getDouble("BILL_TON_BKG");
					double loadTonCs = rs2.getDouble("LOAD_TON_CS");
					double shutoutTonCs = rs2.getDouble("SHUTOUT_TON_CS");
					int countUnit = rs2.getInt("COUNT_UNIT");
					int totalPackEdo = rs2.getInt("TOTAL_PACK_EDO");
					int totalPackDn = rs2.getInt("TOTAL_PACK_DN");
					String billAcctNbr = CommonUtility.deNull(rs2.getString("BILL_ACCT_NBR"));
					
					generalEventLogValueObject.setDiscVvCd(processDiscVVcd(discVvCd, gbmsCargoBillingValueObject));
					generalEventLogValueObject.setLoadVvCd(processLoadVVcd(loadVvCd, gbmsCargoBillingValueObject));
					generalEventLogValueObject.setVvInd(processVvInd(vvInd, gbmsCargoBillingValueObject));
					generalEventLogValueObject
							.setBusinessType(processBusinessType(businessType, gbmsCargoBillingValueObject));
					generalEventLogValueObject.setSchemeCd(processSchemeCd(SchemeCd, gbmsCargoBillingValueObject));
					generalEventLogValueObject
							.setTariffMainCatCd(processTariffMainCatCd(tariffMainCatCd, gbmsCargoBillingValueObject));
					generalEventLogValueObject
							.setTariffSubCatCd(processTariffSubCatCd(tariffSubCatCd, gbmsCargoBillingValueObject));
					generalEventLogValueObject.setMvmt(processMvmt(mvmt, gbmsCargoBillingValueObject));
//					generalEventLogValueObject.setType(processType(type,
//						gbmsCargoBillingValueObject));
					generalEventLogValueObject.setType(type);
					generalEventLogValueObject.setCargoType(processCargoType(cargoType, gbmsCargoBillingValueObject));
					generalEventLogValueObject.setLocalLeg(processLocalLeg(localLeg, gbmsCargoBillingValueObject));
					generalEventLogValueObject
							.setDiscGateway(processDiscGateway(discGateWay, gbmsCargoBillingValueObject));
					generalEventLogValueObject.setRefInd(processRefInd(refInd, gbmsCargoBillingValueObject));
					generalEventLogValueObject.setBlNbr(processBlNbr(blNbr, gbmsCargoBillingValueObject));
					generalEventLogValueObject.setEdoAsnNbr(processEdoAsnNbr(edoAsnNbr, gbmsCargoBillingValueObject));
					generalEventLogValueObject.setBkRefNbr(processBkRefNbr(bkRefNbr, gbmsCargoBillingValueObject));
					generalEventLogValueObject.setEsnAsnNbr(processEsnAsnNbr(esnAsnNbr, gbmsCargoBillingValueObject));
					generalEventLogValueObject.setDnNbr(processDnNbr(dnNbr, gbmsCargoBillingValueObject));
					generalEventLogValueObject.setUaNbr(processUaNbr(uaNbr, gbmsCargoBillingValueObject));
					generalEventLogValueObject.setBillTonBl(processBillTonBl(billTonBl, gbmsCargoBillingValueObject));
					generalEventLogValueObject
							.setBillTonEdo(processBillTonEdo(billTonEdo, gbmsCargoBillingValueObject));
					generalEventLogValueObject.setBillTonDn(processBillTonDn(billTonDn, gbmsCargoBillingValueObject));
					generalEventLogValueObject
							.setBillTonEsn(processBillTonEsn(billTonEsn, gbmsCargoBillingValueObject));
					generalEventLogValueObject
							.setBillTonBkg(processBillTonBkg(billTonBkg, gbmsCargoBillingValueObject));
					generalEventLogValueObject.setLoadTonCs(processLoadTonCs(loadTonCs, gbmsCargoBillingValueObject));
					generalEventLogValueObject
							.setShutoutTonCs(processShutoutTonCs(shutoutTonCs, gbmsCargoBillingValueObject));
					generalEventLogValueObject.setCountUnit(processCountUnit(countUnit, gbmsCargoBillingValueObject));
					generalEventLogValueObject
							.setTotalPackEdo(processTotalPackEdo(totalPackEdo, gbmsCargoBillingValueObject));
					generalEventLogValueObject
							.setTotalPackDn(processTotalPackDn(totalPackDn, gbmsCargoBillingValueObject));
					generalEventLogValueObject
							.setBillAcctNbr(processBillAcctNbr(billAcctNbr, gbmsCargoBillingValueObject));
					generalEventLogValueObject.setLastModifyUserId(struserid);
					// generalEventLogValueObject.setLastModifyDttm(gbmsCargoBillingValueObject.getLastModifyDttm());
					// generalEventLogValueObject.setLastModifyDttm(gbmsCargoBillingValueObject.getPrintDttm());
					// add new scheme for LCT, 28.feb.11 by hpeng
					if (ProcessChargeConst.LCT_SCHEME.equals(SchemeCd)) {
						generalEventLogValueObject.setLastModifyDttm(gbmsCargoBillingValueObject.getLastModifyDttm());
					} else {
						generalEventLogValueObject.setLastModifyDttm(gbmsCargoBillingValueObject.getPrintDttm());
					}
					generalEventLogValueObject.setPrintDttm(gbmsCargoBillingValueObject.getPrintDttm());
					// GeneralEventLogArrayList.add(generalEventLogValueObject);
					// lak added for store rent
//					String impSch = "";
//					String expSch = "";
					String chType = "";
//					String edoactnbr = "";
					String ttype = "";

					String satenind = "";
					String sctenind = "";
					String fsdays = "";
					String crgactnbr = "";
					
//					expSch = gbmsCargoBillingValueObject.getVesselScheme();
//					impSch = gbmsCargoBillingValueObject.getFirstcarSch();
					chType = gbmsCargoBillingValueObject.getChType();
//					edoactnbr = gbmsCargoBillingValueObject.getEdotAcctNbr();
					ttype = gbmsCargoBillingValueObject.getTransType();

					satenind = gbmsCargoBillingValueObject.getSatenind();
					sctenind = gbmsCargoBillingValueObject.getSctenind();
					fsdays = gbmsCargoBillingValueObject.getFsdays();
					crgactnbr = gbmsCargoBillingValueObject.getCargoAcct();

					log.info("satenind " + satenind);
					log.info("sctenind " + sctenind);
					log.info("fsdays " + fsdays);
					
					if (!chType.equals("SR")) {
						GeneralEventLogArrayList.add(generalEventLogValueObject);
					}
					
					if (chType.equals("SR") && ttype.equals("E") && satenind != null && !satenind.equals("")
							&& satenind.equals("N")
							&& ((!crgactnbr.equalsIgnoreCase("CASH") && sctenind != null && !sctenind.equals("")
									&& sctenind.equals("N")) || crgactnbr.equalsIgnoreCase("CASH"))) {
						log.info("checkAnyStoreRent " + processGBLogRepo.checkAnyStoreRent(generalEventLogValueObject,
								Integer.parseInt(fsdays)));
						if (this.processGBLogRepo.checkAnyStoreRent(generalEventLogValueObject,
								Integer.parseInt(fsdays))) {
							GeneralEventLogArrayList.add((GbmsCargoBillingValueObject) generalEventLogValueObject);
						}
					} else if (chType.equals("SR") && !ttype.equals("E")) {
						log.info("checkAnyStoreRent " + processGBLogRepo.checkAnyStoreRent(generalEventLogValueObject,
								Integer.parseInt(fsdays)));
						if (this.processGBLogRepo.checkAnyStoreRent(generalEventLogValueObject,
								Integer.parseInt(fsdays))) {
							GeneralEventLogArrayList.add((GbmsCargoBillingValueObject) generalEventLogValueObject);
						}
					}
					/*
					 * if(chType.equals("SR") && satenind.equals("N") && sctenind.equals("N") &&
					 * (expSch.equals("JLR") && (ttype.equals("E") || ttype.equals("A")) ) ||
					 * ((ttype.equals("E") || ttype.equals("A")) && (impSch.equals("JLR") ||
					 * expSch.equals("JLR")))) { // For Store Rent //The charges should be raised
					 * for the following conditions //(i) Exp leg Liner scheme ESN JPJP //(ii) Exp
					 * Leg Liner or Imp Leg Liner ESN JPJP (for FSP)
					 * 
					 * if(ProcessGBLogremote.checkAnyStoreRent
					 * (generalEventLogValueObject,Integer.parseInt(fsdays)))
					 * GeneralEventLogArrayList.add(generalEventLogValueObject); } // Exp Leg Non
					 * Liner or Barter Trader AND Imp Leg NL or BT ESN JPJP (for USP)
					 * if((ttype.equals("E") || ttype.equals("A")) && (impSch.equals("JNL") ||
					 * impSch.equals("JBT")) && (expSch.equals("JNL") || expSch.equals("JBT")))
					 * GeneralEventLogArrayList.add(generalEventLogValueObject);
					 */
					// lak added for store rent
					
					log.info("**** CHARGES ******");
					log.info("|discVvCd : " + generalEventLogValueObject.getDiscVvCd());
					log.info("|LoadVvCd : " + generalEventLogValueObject.getLoadVvCd());
					log.info("|vvInd : " + generalEventLogValueObject.getVvInd());
					log.info("|businessType : " + generalEventLogValueObject.getBusinessType());
					log.info("|schemeCd : " + generalEventLogValueObject.getSchemeCd());
					log.info("|tariffMainCatCd : " + generalEventLogValueObject.getTariffMainCatCd());
					log.info("|tariffSubCatCd : " + generalEventLogValueObject.getTariffSubCatCd());
					log.info("|mvmt : " + generalEventLogValueObject.getMvmt());
					log.info("|type : " + generalEventLogValueObject.getType());
//					log.info("|type : "+generalEventLogValueObject.getCargoType());
					log.info("|localLeg : " + generalEventLogValueObject.getLocalLeg());
					log.info("|discGateway : " + generalEventLogValueObject.getDiscGateway());
					log.info("|RefInd : " + generalEventLogValueObject.getRefInd());
					log.info("|blNbr : " + generalEventLogValueObject.getBlNbr());
					log.info("|edoAsnNbr : " + generalEventLogValueObject.getEdoAsnNbr());
					log.info("|BkRefNbr : " + generalEventLogValueObject.getBkRefNbr());
					log.info("|EsnAsnNbr : " + generalEventLogValueObject.getEsnAsnNbr());
					log.info("|DnNbr : " + generalEventLogValueObject.getDnNbr());
					log.info("|UaNbr : " + generalEventLogValueObject.getUaNbr());
					log.info("|BillTonBl : " + generalEventLogValueObject.getBillTonBl());
					log.info("|BillTonEdo : " + generalEventLogValueObject.getBillTonEdo());
					log.info("|BillTonDn : " + generalEventLogValueObject.getBillTonDn());
					log.info("|BillTonEsn : " + generalEventLogValueObject.getBillTonEsn());
					log.info("|BillTonBkg : " + generalEventLogValueObject.getBillTonBkg());
					log.info("|LoadTonCs : " + generalEventLogValueObject.getLoadTonCs());
					log.info("|ShutoutTonCs : " + generalEventLogValueObject.getShutoutTonCs());
					log.info("|CountUnit : " + generalEventLogValueObject.getCountUnit());
					log.info("|TotalPackEdo : " + generalEventLogValueObject.getTotalPackEdo());
					log.info("|TotalPackDn : " + generalEventLogValueObject.getTotalPackDn());
					log.info("|BillAcctNbr : " + generalEventLogValueObject.getBillAcctNbr());
					log.info("|lastModifyUserId : " + generalEventLogValueObject.getLastModifyUserId());
					log.info("|lastModifyDttm : " + generalEventLogValueObject.getLastModifyDttm());
					log.info("==== END OF CHARGES ====");
				}
			} catch (NullPointerException e) {
				log.info("Exception processDetails :" , e);
				throw new BusinessException("M4201");
			} catch (BusinessException e) {
				log.info("Exception processDetails :" , e);
				throw new BusinessException(e.getMessage());
			} catch (Exception e) {
				log.info("Exception processDetails :" , e);
				throw new BusinessException("M4201");
			} finally {
				log.info("END processDetails DAO  GeneralEventLogArrayList: " + GeneralEventLogArrayList.size());
			}
		}
		return GeneralEventLogArrayList;
	}

}
