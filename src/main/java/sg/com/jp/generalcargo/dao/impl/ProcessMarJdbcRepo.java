package sg.com.jp.generalcargo.dao.impl;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.LineTowedVesselRepo;
import sg.com.jp.generalcargo.dao.ProcessCommonRepo;
import sg.com.jp.generalcargo.dao.ProcessGenericRepository;
import sg.com.jp.generalcargo.dao.ProcessMarRepo;
import sg.com.jp.generalcargo.dao.TariffMainRepository;
import sg.com.jp.generalcargo.domain.AccountValueObject;
import sg.com.jp.generalcargo.domain.BerthRelatedValueObject;
import sg.com.jp.generalcargo.domain.BillErrorVO;
import sg.com.jp.generalcargo.domain.CntrEventLogValueObject;
import sg.com.jp.generalcargo.domain.ContractSearchKeyValueObject;
import sg.com.jp.generalcargo.domain.ContractValueObject;
import sg.com.jp.generalcargo.domain.GstCodeValueObject;
import sg.com.jp.generalcargo.domain.LineTowedVesselValueObject;
import sg.com.jp.generalcargo.domain.ProcessValueObject;
import sg.com.jp.generalcargo.domain.TariffContainerVO;
import sg.com.jp.generalcargo.domain.TariffMainVO;
import sg.com.jp.generalcargo.domain.TariffTierVO;
import sg.com.jp.generalcargo.domain.VesselRelatedValueObject;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ProcessChargeConst;

@Repository("processMarRepo")
public class ProcessMarJdbcRepo implements ProcessMarRepo {

	private static final Log log = LogFactory.getLog(ProcessMarJdbcRepo.class);

	// ejb.sessionBeans.cab.processCharges -->ProcessMarEJB

	private String tariffMainCat = ProcessChargeConst.TARIFF_MAIN_MARINE;

	private final int nbrSubCat = 9;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private ProcessGenericRepository processGenericRepo;

	@Autowired
	private ProcessCommonRepo processCommonRepo;

	@Autowired
	private LineTowedVesselRepo lineTowedVesselRepo;

	@Autowired
	private TariffMainRepository tariffMainRepo;

	private final String[] subCatArray = { ProcessChargeConst.TARIFF_SUB_MAR_DOCKAGE,
			ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY, ProcessChargeConst.TARIFF_SUB_MAR_BERTH_UNBERTH,
			ProcessChargeConst.TARIFF_SUB_MAR_BERTH_APPL_QUAYCRANE,
			ProcessChargeConst.TARIFF_SUB_MAR_BERTH_APPL_NO_QUAYCRANE, ProcessChargeConst.TARIFF_SUB_MAR_LATE_ARRIVAL,
			ProcessChargeConst.TARIFF_SUB_MAR_SPECIAL_DOCKAGE, // }; Commented by Ruchika (Zensar)
			// Ruchika (Zensar): Adding new constant for overstay dockage on 13/08/2007
			ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY_DOCKAGE, ProcessChargeConst.TARIFF_SUB_MAR_CT_SPECIAL_LINETOW };
	// Added for Combi Sripriya May 2018
	// End of addition by Ruchika (Zensar)

	/*
	 * private final String[] CTArray = {ProcessChargeConst.TARIFF_SUB_MAR_DOCKAGE,
	 * ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY,
	 * ProcessChargeConst.TARIFF_SUB_MAR_BERTH_UNBERTH,
	 * ProcessChargeConst.TARIFF_SUB_MAR_BERTH_APPL_QUAYCRANE, " ", " ", " ",
	 * //};Commented by Ruchika (Zensar) " "};
	 * //ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY_DOCKAGE};// <cfg 26.jun.08:
	 * removed subcat 08 for CT MA>
	 */
	// <cfg commented^^ to put back OSD changes, 26.jun.08>
	private final String[] CTArray = { ProcessChargeConst.TARIFF_SUB_MAR_DOCKAGE,
			// ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY,
			" ", // <cfg commented, 29.apr.08, CT to follow GB overstay dockage>
			ProcessChargeConst.TARIFF_SUB_MAR_BERTH_UNBERTH, ProcessChargeConst.TARIFF_SUB_MAR_BERTH_APPL_QUAYCRANE,
			" ", " ", " ", ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY_DOCKAGE, // CT Line Tow Barge May 2018
			ProcessChargeConst.TARIFF_SUB_MAR_CT_SPECIAL_LINETOW // };Combi Line Tow Barge May 2018
	}; // <cfg 29.apr.08/>;
		// ^^ <cfg commented OSD changes, 09.jun.08 -- cfg put back OSD changes,
		// 26.jun.08>*/

	private final String[] GBArray = { ProcessChargeConst.TARIFF_SUB_MAR_DOCKAGE, " ",
			// Commented by Ruchika as Gb will use sub-cat 08 not 02:
			// ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY,
			ProcessChargeConst.TARIFF_SUB_MAR_BERTH_UNBERTH, " ",
			ProcessChargeConst.TARIFF_SUB_MAR_BERTH_APPL_NO_QUAYCRANE, ProcessChargeConst.TARIFF_SUB_MAR_LATE_ARRIVAL,
			// " "}; by YJ 12 Jan 2004
			ProcessChargeConst.TARIFF_SUB_MAR_SPECIAL_DOCKAGE, // by YJ 12 Jan 04 for linetowed barge
			// }; Commented by Ruchika (Zensar)
			// Ruchika (Zensar): Adding new constant for overstay dockage on 13/08/2007
			ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY_DOCKAGE, " " };
	// Not applicable for GB - Combi Enhancements
	// End of addition by Ruchika (Zensar)

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public Map<String, Object> determineOverStayAndAmount(String vvCode) throws BusinessException {
		return determineOverStayAndAmount(vvCode, "");
	}

	public Map<String, Object> determineOverStayAndAmount(String vvCode, String billableDuration)
			throws BusinessException {
		List<ProcessValueObject> marineDtlCollection = new ArrayList<ProcessValueObject>();
		Map<String, Object> hmOverstayDetails = null;
		Timestamp atbDttm = null;
		int maxShiftInd = 0;
		int versionNbr = 0;
		BerthRelatedValueObject berthRelatedValueObject1 = null;
		BerthRelatedValueObject berthRelatedValueObject2 = null;
		ProcessValueObject processValueObject = null;
		Object[] marineDtlArray = null;
		Object[] osdDtlArray = null;
		List<ProcessValueObject> marineDtlCollectionPubl = new ArrayList<ProcessValueObject>();
		Object[] marineDtlArrayPubl = null;
		List<ProcessValueObject> marineDtlCollectionCust = new ArrayList<ProcessValueObject>();
		Object[] marineDtlArrayCust = null;
		List<Object> billCollectionPubl = new ArrayList<Object>();
		List<Object> billCollectionCust = new ArrayList<Object>();
		try {
			log.info("START: determineOverStayAndAmount DAO vvCode:" + vvCode + " ,billableDuration:"
					+ billableDuration);

			VesselRelatedValueObject vesselRelatedValueObject = new VesselRelatedValueObject();

			log.info("before getVesselInfo(vvCode)");
			vesselRelatedValueObject = getVesselInfo(vvCode);
			log.info("after getVesselInfo(vvCode)");
			log.info("after getVesselInfo(vvCode)--vesselRelatedValueObject--" + vesselRelatedValueObject);

			// retrieve the atb timestamp for further processing
			// amended by swho on 22/04/2002 for extraction of GBMS vessels for marine
			// charges
			berthRelatedValueObject1 = (BerthRelatedValueObject) processGenericRepo
					.retrieveBerthDttm(vesselRelatedValueObject, 1, tariffMainCat);
			log.info("berthRelatedValueObject1---" + berthRelatedValueObject1);
			atbDttm = berthRelatedValueObject1.getAtbDttm();
			// end amend by swho
			log.info("atb_dttm = " + atbDttm + "...");

			// retrieve the maximum shift indicator value
			maxShiftInd = processGenericRepo.retrieveMaxShiftInd(vvCode);
			log.info("max shift ind = " + maxShiftInd + "...");

			// retrieve the atu, cod and col timestamp for further processing
			// amended by swho on 22/04/2002 for extraction of GBMS vessels for marine
			// charges
			berthRelatedValueObject2 = (BerthRelatedValueObject) processGenericRepo
					.retrieveBerthDttm(vesselRelatedValueObject, maxShiftInd, tariffMainCat);

			// retrieve the relevant tariff version number based on the atb timestamp
			log.info("processGeneric.retrieveTariffVersion(atbDttm, vesselRelatedValueObject, tariffMainCat);");
			versionNbr = (int) processGenericRepo.retrieveTariffVersion(atbDttm, vesselRelatedValueObject,
					tariffMainCat);
			log.info("version nbr = " + versionNbr + "...");

			// retrieve the details relevant to marine events for
			// 1) Dockage
			// 2) Overstay Dockage
			// 3) Berthing/Unberthing
			// 4) BA Late Amend/Cancellation
			// amended by swho on 22/04/2002 for extraction of GBMS vessels for marine
			// charges
			// marineDtlCollection = processMar.retrieveMarineDtl(vesselRelatedValueObject,
			// atbDttm, maxShiftInd, vslLoa, baCnt, vesselStayPeriod, overStayPeriod,
			// versionNbr);
			log.info("before processMar.retrieveMarineDtl(");
			// Added changes for OverStayDockage Partial Waiver Request -Mcconsulting pteLtd
			marineDtlCollection = retrieveMarineDtl(vesselRelatedValueObject, berthRelatedValueObject1,
					berthRelatedValueObject2, maxShiftInd, versionNbr, billableDuration);
			// Changes ends for OverStayDockage Partial Waiver request form
			// changes-MCConsulting pte ltd
			log.info("after processMar.retrieveMarineDtl(");

			marineDtlArray = marineDtlCollection.toArray();
			log.info("marine details length = " + marineDtlArray.length + "...");

			if (marineDtlArray.length != 0) {
				// check to ensure that the error indicator in the last record of the array
				// is = false, else proceed to process the next vessel as partial bill is not
				// allowed
				processValueObject = (ProcessValueObject) Array.get(marineDtlArray, (marineDtlArray.length) - 1);

				if (processValueObject.getErrorInd()) {
					log.error("Error in Process Marine encountered");
				}
				log.info("Getting the marine data retrieved");
				List<ProcessValueObject> osdDtls = new ArrayList<ProcessValueObject>();
				TariffMainVO tariffMainVO = null;
				for (int cnt = 0; cnt < marineDtlArray.length; cnt++) {
					processValueObject = (ProcessValueObject) Array.get(marineDtlArray, cnt);
					log.info("--------------At " + cnt + " ------------ " + processValueObject.getMainCount()
							+ "-------");
					int mainCnt = processValueObject.getMainCount();

					if (mainCnt != 0) {
						tariffMainVO = processValueObject.getMain(0);
					}

					if (mainCnt == 2) {
						tariffMainVO = processValueObject.getMain(1);
					}
					// String sOsdCT = new
					// StringBuffer().append(ProcessChargeConst.TARIFF_MAIN_MARINE).append(ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY).toString();
					// //<cfg commented for OSD- CT to follow GB-- put back, 26.jun.08>
					String sOsdCT = new StringBuffer().append(ProcessChargeConst.TARIFF_MAIN_MARINE)
							.append(ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY_DOCKAGE).toString(); // <cfg uncommented^
																									// OSD changes for
																									// wooden craft to
																									// port first in
																									// prod, 09.jun.08>
					String sOsdGB = new StringBuffer().append(ProcessChargeConst.TARIFF_MAIN_MARINE)
							.append(ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY_DOCKAGE).toString();
					log.info("--------------sOsdCT ------------ " + sOsdCT + "------------sOsdGB  ------------ "
							+ sOsdGB + "-------");
					log.info("--------------tariffCode() is ------------ " + (tariffMainVO == null ? "null" : tariffMainVO.getCode()) + "-------");
					if (tariffMainVO != null && tariffMainVO.getCode() != null
							&& (tariffMainVO.getCode().substring(1, 5).equalsIgnoreCase(sOsdCT)
									|| tariffMainVO.getCode().substring(1, 5).equalsIgnoreCase(sOsdGB))) {
						log.info("--------------Addng Tariff Code ------------ " + (tariffMainVO == null ? "null" : tariffMainVO.getCode()) + "-------");

						osdDtls.add(processValueObject);
					}

				}
				log.info("-------OSD relevant data size: " + osdDtls + "-----------");
				if (osdDtls.size() > 0) {
					osdDtlArray = osdDtls.toArray();
				} else {
					osdDtlArray = marineDtlArray;
				}
				hmOverstayDetails = new HashMap<String, Object>();
				// retrieve the sorted list of marine details relevant to marine events
				// for published tariff
				// marineDtlCollectionPubl = processCommon.sortCntrEventDtl(marineDtlArray,
				// ProcessChargeConst.TARIFF_TYPE_PUBLISH, tariffMainCat);
				marineDtlCollectionPubl = processCommonRepo.sortCntrEventDtl(osdDtlArray,
						ProcessChargeConst.TARIFF_TYPE_PUBLISH, tariffMainCat);
				marineDtlArrayPubl = marineDtlCollectionPubl.toArray();
				log.info("-------marineDtlCollectionPubl size: " + marineDtlCollectionPubl.size() + "-----------");
				log.info("sorted publish marine details length = " + marineDtlArrayPubl.length + "...");

				// marineDtlCollectionCust = processCommon.sortCntrEventDtl(marineDtlArray,
				// ProcessChargeConst.TARIFF_TYPE_CUSTOMIZE, tariffMainCat);
				marineDtlCollectionCust = processCommonRepo.sortCntrEventDtl(osdDtlArray,
						ProcessChargeConst.TARIFF_TYPE_CUSTOMIZE, tariffMainCat);
				marineDtlArrayCust = marineDtlCollectionCust.toArray();
				log.info("-------marineDtlCollectionCust size: " + marineDtlCollectionCust.size() + "-----------");
				log.info("sorted customize marine details length = " + marineDtlArrayCust.length + "...");

				// generate bill details
				billCollectionPubl = processCommonRepo.processBill(marineDtlArrayPubl, billCollectionPubl,
						ProcessChargeConst.TARIFF_TYPE_PUBLISH);
				log.info("processed published bill");
				billCollectionCust = processCommonRepo.processBill(marineDtlArrayCust, billCollectionPubl,
						ProcessChargeConst.TARIFF_TYPE_CUSTOMIZE);
				log.info("processed customized bill");

				// insert all bill details into the respective bill tables: Ruchika: This is not
				// needed
				// processGeneric.insertBillDetails(billCollectionPubl, billCollectionCust);
				log.info("insertion complete");
				log.info("-------billCollectionPubl size: " + billCollectionPubl.size() + "-----------");
				log.info("-------billCollectionCust size: " + billCollectionCust.size() + "-----------");

				hmOverstayDetails.put("P", billCollectionPubl);
				hmOverstayDetails.put("C", billCollectionCust);
				hmOverstayDetails.put("OPeriod", new Long(processValueObject.getOverStayPeriod()));
				// update vessel call record's bill_stev_ind to indicate
				// that marine charge billed Ruchika: This is not needed
				// processGeneric.updateVesselCallBillInd(vvCode, tariffMainCat);
				log.info("Set all the data");
			}

			log.info("END: *** determineOverStayAndAmount Result *****" + hmOverstayDetails.toString());
		} catch (BusinessException e) {
			log.error("Exception: determineOverStayAndAmount ", e);
			throw new BusinessException(e.getMessage());
		} catch (NullPointerException e) {
			log.error("Exception: determineOverStayAndAmount ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: determineOverStayAndAmount ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: determineOverStayAndAmount  DAO  END");
		}
		return hmOverstayDetails; // marineDtlCollection;
	}

	public List<ProcessValueObject> retrieveMarineDtl(VesselRelatedValueObject vesselRelatedValueObject,
			BerthRelatedValueObject berthRelatedValueObject1, BerthRelatedValueObject berthRelatedValueObject2,
			int maxShiftInd, int versionNbr) throws BusinessException {
		return retrieveMarineDtl(vesselRelatedValueObject, berthRelatedValueObject1, berthRelatedValueObject2,
				maxShiftInd, versionNbr, "");
	}

	public List<ProcessValueObject> retrieveMarineDtl(VesselRelatedValueObject vesselRelatedValueObject,
			BerthRelatedValueObject berthRelatedValueObject1, BerthRelatedValueObject berthRelatedValueObject2,
			int maxShiftInd, int versionNbr, String billableDuration) throws BusinessException {
		List<ProcessValueObject> marineDtlList = null;
		ProcessValueObject processValueObjectComb = null;
		ProcessValueObject processValueObjectPubl = null;
		BillErrorVO billErrorValueObject = null;
		int xcnt = 0;
		boolean vesselError = false;
		String vvCd = null;
		String vslNm = null;
		String inVoyNbr = null;
		String outVoyNbr = null;
		String shpgSvcCd = null;
		String vslOprCd = null;
		// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
		String cntrVslInd = null;
		String schemeType = null;
		String termType = null;
		String billAcctNbr = null;
		String gbArrivalWaiveCd = null;
		String gbDepartureWaiveCd = null;
		String gbBerthBillInd = null;
		int berthCnt = 0;
		int nbrOSTimeAllow = 0;
		Timestamp atbDttm = null;
		Timestamp lastActDttm = null;
		// end add by swho
		String tariffMainCat = null;
		String tariffSubCat = null;
		int vslLoa = 0;
		int baCnt = 0;
		long vesselStayPeriod = 0;
		long overStayPeriod = 0;
		long specialDockagePeriod = 0; // Added by YJ on 12 Jan for linetowed barge
		// Added by Ruchika (Zensar): 15/08/2007 for overstay dockage waiver details
		String osdWaiverCd = null;
		String isRejected = null;
		// End of change by Ruchika (Zensar)
		String waiverType = null;
		// Added on 07/05/2002 by Ai Lin - For Late Arrival
		double lateArrivalPeriod = 0;
		// End added on 07/05/2002 by Ai Lin - For Late Arrival
		// added by YJ on 12 Jan 04 for linetowed barge
		boolean linetowedBargeInd = false;
		try {
			log.info("START: retrieveMarineDtl DAO vesselRelatedValueObject:" + vesselRelatedValueObject.toString()
					+ " ,berthRelatedValueObject1:" + berthRelatedValueObject1.toString()
					+ " ,berthRelatedValueObject2: " + berthRelatedValueObject2.toString() + ",maxShiftInd:"
					+ maxShiftInd + ",versionNbr:" + versionNbr + ",billableDuration:" + billableDuration);
			// initialize the vessel error indicator to false;
			vesselError = false;
			// initialize the marine dtl arraylist
			marineDtlList = new ArrayList<ProcessValueObject>();

			// from the vessel records, get the basic details
			vvCd = vesselRelatedValueObject.getVvCd();
			vslNm = vesselRelatedValueObject.getVslNm();
			inVoyNbr = vesselRelatedValueObject.getInVoyNbr();
			outVoyNbr = vesselRelatedValueObject.getOutVoyNbr();
			shpgSvcCd = vesselRelatedValueObject.getShpgSvcCd();
			vslOprCd = vesselRelatedValueObject.getVslOprCd();
			// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
			cntrVslInd = vesselRelatedValueObject.getCntrVslInd();
			schemeType = vesselRelatedValueObject.getSchemeType();
			termType = vesselRelatedValueObject.getTermType();
			billAcctNbr = vesselRelatedValueObject.getBillAcctNbr();
			gbArrivalWaiveCd = vesselRelatedValueObject.getGbArrivalWaiveCd();
			gbDepartureWaiveCd = vesselRelatedValueObject.getGbDepartureWaiveCd();
			gbBerthBillInd = vesselRelatedValueObject.getGbBerthWaiveInd();
			// end add by swho
			// Added by Ruchika (Zensar): 15/08/2007 for overstay dockage waiver details
			vesselRelatedValueObject = getOSDWaiverDtls(vesselRelatedValueObject);
			osdWaiverCd = vesselRelatedValueObject.getOsdWaiverCd();
			isRejected = vesselRelatedValueObject.getIsRejected();
			waiverType = vesselRelatedValueObject.getWaiverType();
			// End of change by Ruchika (Zensar)

			log.info("[CAB],vvCd = " + vvCd + "...");
			log.info("[CAB],vslNm = " + vslNm + "...");
			log.info("[CAB],inVoyNbr = " + inVoyNbr + "...");
			log.info("[CAB],outVoyNbr = " + outVoyNbr + "...");
			log.info("[CAB],shpgSvcCd = " + shpgSvcCd + "...");
			log.info("[CAB],vslOprCd = " + vslOprCd + "...");
			// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
			log.info("[CAB],cntrVslInd = " + cntrVslInd + "...");
			log.info("[CAB],schemeType = " + schemeType + "...");
			log.info("[CAB],termType = " + termType + "...");
			log.info("[CAB],billAcctNbr = " + billAcctNbr + "...");
			log.info("[CAB],gbArrivalWaiveCd = " + gbArrivalWaiveCd + "...");
			log.info("[CAB],gbDepartureWaiveCd = " + gbDepartureWaiveCd + "...");
			log.info("[CAB],gbBerthBillInd = " + gbBerthBillInd + "...");
			// end add by swho
			// Added by Ruchika (Zensar): 15/08/2007 for overstay dockage waiver details
			log.info("[CAB],osdWaiverCd = " + osdWaiverCd + "...");
			log.info("[CAB],isRejected = " + isRejected + "...");
			// End of change by Ruchika (Zensar)

			// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
			// retrieve the vessel loa
			vslLoa = retrieveVesselDtl(vslNm);
			log.info("[CAB],vsl loa = " + vslLoa + "...");

			// retrieve the number of berthing
			if ((gbBerthBillInd.equals("N")) || (schemeType.equals(ProcessChargeConst.BARTER_TRADER_SCHEME))) {
				berthCnt = 0;
			} else {
				berthCnt = retrieveNbrChargeBerth(termType, maxShiftInd);
			}
			log.info("[CAB],berthCnt = " + berthCnt + "...");

			// amended by swho on 28/06/2002 for charging of late BA for BT vessels
			// retrieve the number of late berth advice, amendment, cancellation
			// if (schemeType.equals(ProcessChargeConst.BARTER_TRADER_SCHEME)) {
			// baCnt = 0;
			// }
			// else {
			baCnt = retrieveBACnt(vvCd, termType);
			// }
			// end amend by swho
			log.info("[CAB] baCnt = " + baCnt + "...");
			// end add by swho

			// set the tariff main category code
			tariffMainCat = ProcessChargeConst.TARIFF_MAIN_MARINE;

			// added by YJ on 12 Jan 04 for linetowed barge
			List<LineTowedVesselValueObject> linetowedDockageList = this.getLineTowedDockageList(vvCd);

			if (linetowedDockageList.size() > 0) {
				linetowedBargeInd = true;
			} else {
				linetowedBargeInd = false;
			}
			// end added by YJ on 12 Jan for linetowed barge

			// loop thru the total number of sub categories available for this run
			for (xcnt = 0; xcnt < nbrSubCat; xcnt++) {
				try {
					// set the tariff sub category code
					tariffSubCat = subCatArray[xcnt];
					log.info("[CAB],------------subCatArray[" + xcnt + "] = " + subCatArray[xcnt] + "--------");

					// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
					if (cntrVslInd.equals(ProcessChargeConst.CONTAINER_VSL)) {
						if (!tariffSubCat.equals(CTArray[xcnt])) {
							log.info("[CAB],------------CTArray[" + xcnt + "] = " + CTArray[xcnt] + "--------");
							continue;
						}
					} else {
						if (!tariffSubCat.equals(GBArray[xcnt])) {
							log.info("[CAB],------------GBArray[" + xcnt + "] = " + GBArray[xcnt] + "--------");
							continue;
						}
					}

					if ((schemeType.equals(ProcessChargeConst.BARTER_TRADER_SCHEME)) &&
					// amended by swho on 28/06/2002 for charging of late BA for BT vessels
					// (!tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_DOCKAGE))) {
							(!tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_DOCKAGE))
							&& (!tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_BERTH_APPL_NO_QUAYCRANE))) {
						// end amend by swho
						continue;
					}
					// end add by swho

					// add new scheme for LCT, 13.feb.11 by hpeng
					if ((schemeType.equals(ProcessChargeConst.LCT_SCHEME))
							&& (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_BERTH_UNBERTH)
									|| tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_SPECIAL_DOCKAGE))) {
						log.info("[CAB],------------schemeType = " + schemeType + "--------");
						continue;
					}

					// <added for wooden craft vessel scheme, 12.may.08-cfg>
					if ((schemeType.equals(ProcessChargeConst.WOODEN_CRAFT_SCHEME))
							&& (!tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_DOCKAGE))
							&& (!tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_BERTH_APPL_NO_QUAYCRANE))) {
						// <cfg removed^^ Berth Appl Late/Cancel/Amend w/o Quay Crane for wooden craft,
						// 30.may.08>
						// <cfg 25.jun.08 added back ^^ condition block for SUBCATS mar-dockage and
						// mar-berth-appl-no-quaycrane>
						log.info("[CAB],------------schemeType = " + schemeType + "--------");
						continue;
					}
					// <added for wooden craft vessel scheme, 12.may.08-cfg/>
					// <cfg 24.jun.08: commented ^^ to revert charges for Wooden Craft>

					log.info("[CAB],------------tariffMainCat 111 = " + tariffMainCat + "--------");
					log.info("[CAB],------------tariffSubCat  222 = " + tariffSubCat + "--------");

					// retrieve the published tariff
					// amended by swho on 181202 to cater for GST change wef 1 Jan 2003
					// processValueObjectPubl = retrieveTariffDtls(versionNbr, tariffMainCat,
					// tariffSubCat, null, null, null, vesselRelatedValueObject,
					// berthRelatedValueObject2);
					atbDttm = berthRelatedValueObject1.getAtbDttm();
					// Added changes for OverStayDockage Partial Waiver Request -Mcconsulting pteLtd
					// When waiver processing is pending and bill runs,we should not show Overstay
					// dockage in the bill
					if (!(waiverType != null && osdWaiverCd != null && isRejected == null
							&& tariffSubCat.equals("08"))) {
						// Changes ends for OverStayDockage Partial Waiver request form
						// changes-MCConsulting pte ltd
						// New EVM Enhancements
						processValueObjectPubl = retrieveTariffDtls(versionNbr, tariffMainCat, tariffSubCat, null, null,
								null, vesselRelatedValueObject, berthRelatedValueObject2, berthRelatedValueObject1,
								atbDttm);
						// end amend by swho

						// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges

						// Ruchika: Code starts here for overstay calculation
						// retrieve nbr of overstay dockage time allowance based on the type of vsl
						nbrOSTimeAllow = retrieveNbrOSTimeAllow(cntrVslInd, berthRelatedValueObject2);
						log.info("[CAB],nbrOSTimeAllow = " + nbrOSTimeAllow + "...");
						log.info("[CAB] nbrOSTimeAllow = " + nbrOSTimeAllow + "...");

						// determine last activity timestamp based on the type of vsl
						lastActDttm = determineLastActivityDttm(cntrVslInd, berthRelatedValueObject2);
						log.info("[CAB],lastActDttm = " + lastActDttm + "...");

						// determine the overstay period
						// Changed method signature by Ruchika: 21-Aug-07 to cater to new flow for
						// overstay dockage in case waiver is not processed
						// at the time of billing
						// overStayPeriod = determineOverStayPeriod(cntrVslInd, schemeType,
						// gbDepartureWaiveCd, nbrOSTimeAllow, lastActDttm, berthRelatedValueObject2);
						// Amended by Dongsheng on 1/4/2013 for CR-OPS-20130326-002
						// overStayPeriod = determineOverStayPeriod(cntrVslInd, schemeType,
						// gbDepartureWaiveCd, nbrOSTimeAllow, lastActDttm, berthRelatedValueObject2,
						// osdWaiverCd, isRejected);
						overStayPeriod = determineOverStayPeriod(vvCd, cntrVslInd, schemeType, gbDepartureWaiveCd,
								nbrOSTimeAllow, lastActDttm, berthRelatedValueObject2, osdWaiverCd, isRejected,
								waiverType);
						// End of change by Ruchika: 21-Aug-07

						// Added by YJ for the overstay period
						if (linetowedBargeInd) {
							// Changed by Ruchika: 21-Aug-07 to cater to new sub-cat of 08 for overstay
							// dockage
							overStayPeriod = determineLineTowedStayPeriod(linetowedDockageList,
									ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY,
									ProcessChargeConst.TARIFF_SUB_MAR_OVERSTAY_DOCKAGE);
							// End of change by Ruchika
						}
						// Added by YJ for over stay period
						log.info("[CAB][retrieveMarineDtl] overStayPeriod = " + overStayPeriod + "..."
								+ ":Line Tow Barge***" + linetowedBargeInd);

						// <cfg 14.jul -- removed addtl codes for late arrival...>

						// determine the vessel stay period
						// Amended by Dongsheng on 1/4/2013 for CR-OPS-20130326-002
						// vesselStayPeriod = determineVesselStayPeriod(cntrVslInd, overStayPeriod,
						// nbrOSTimeAllow, lastActDttm, berthRelatedValueObject1,
						// berthRelatedValueObject2);
						vesselStayPeriod = determineVesselStayPeriod(vvCd, cntrVslInd, overStayPeriod, nbrOSTimeAllow,
								lastActDttm, berthRelatedValueObject1, berthRelatedValueObject2);
						log.info("[CAB][retrieveMarineDtl] vesselStayPeriod = " + vesselStayPeriod + "...");

						// Added by YJ for normal/special dockage time for linetowed barge:
						if (linetowedBargeInd) {
							// Adding blank string as 3rd input parameter due to change in method
							// signature------Here at end of the day
							vesselStayPeriod = determineLineTowedStayPeriod(linetowedDockageList,
									ProcessChargeConst.TARIFF_SUB_MAR_DOCKAGE, "");
							specialDockagePeriod = determineLineTowedStayPeriod(linetowedDockageList,
									ProcessChargeConst.TARIFF_SUB_MAR_SPECIAL_DOCKAGE, "");
							// End of change by Ruchika
						}
						// May 2018 Added to include additional condition to check for Line Tow Barge
						// for CT tariff Combi Enhancement
						if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_SPECIAL_DOCKAGE)
								|| tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_CT_SPECIAL_LINETOW)) {
							vesselStayPeriod = specialDockagePeriod; // to use vesselStayPeriod to pass in
																		// specialdockagePeriod to be used in
																		// determinTierUnits routine
						}
						// End Added by YJ for normal/special dockage time for linetowed barge:

						log.info("[CAB],vesselStayPeriod = " + vesselStayPeriod + "...");

						// Added on 07/05/2002 by Ai Lin - To determine late ariival period
						lateArrivalPeriod = determineLateArrivalPeriod(schemeType, gbArrivalWaiveCd,
								berthRelatedValueObject1, vvCd);
						log.info("[CAB],lateArrivalPeriod = " + lateArrivalPeriod + "...");
						// End added on 07/05/2002 by Ai Lin - To determine late ariival period
						// end add by swho

						// populate the details to retrieve the customize tariff
						atbDttm = berthRelatedValueObject1.getAtbDttm();
						if (cntrVslInd.equals(ProcessChargeConst.CONTAINER_VSL)) {
							processValueObjectComb = populateForCustomizeTariff(processValueObjectPubl,
									vesselRelatedValueObject, berthRelatedValueObject2, vslOprCd, shpgSvcCd, atbDttm,
									tariffMainCat, tariffSubCat);
						} else {
							// To change here New EVM Enhancements
							processValueObjectComb = populateForCustomizeTariff(processValueObjectPubl,
									vesselRelatedValueObject, berthRelatedValueObject2, berthRelatedValueObject1,
									atbDttm, tariffMainCat, tariffSubCat);
						}
						// Added changes for OverStayDockage Partial Waiver Request -Mcconsulting pteLtd
						processValueObjectComb.setBillableDuration(vesselRelatedValueObject.getBillableDuartion());
						processValueObjectComb.setWaiverType(vesselRelatedValueObject.getWaiverType());
						processValueObjectComb.setIsRejected(vesselRelatedValueObject.getIsRejected());
						long billableDurationValue = 0;
						if (StringUtils.isNotEmpty(billableDuration)) {
							billableDurationValue = billableDuration != null ? Long.parseLong(billableDuration) : 0;
							overStayPeriod = (billableDurationValue * 60 * 1000);
						} else {
							billableDurationValue = vesselRelatedValueObject.getBillableDuartion() != null
									? Long.parseLong(vesselRelatedValueObject.getBillableDuartion())
									: 0;
							overStayPeriod = (isRejected != null)
									? retrieveOverStayPeriodByWaiverType(vesselRelatedValueObject.getWaiverType(),
											billableDurationValue, overStayPeriod, isRejected)
									: retrieveOverStayPeriodByWaiverType(vesselRelatedValueObject.getWaiverType(),
											billableDurationValue, overStayPeriod);

						}
						// Changes ends for OverStayDockage Partial Waiver request form
						// changes-MCConsulting pte ltd
						// determine the tier units for both time and other units
						// Amended on 07/05/2002 by Ai Lin
						// processValueObjectComb = determineTierUnits(processValueObjectComb, berthCnt,
						// vslLoa, baCnt, vesselStayPeriod, overStayPeriod, tariffSubCat);
						processValueObjectComb = determineTierUnits(processValueObjectComb, berthCnt, vslLoa, baCnt,
								vesselStayPeriod, overStayPeriod, tariffSubCat, lateArrivalPeriod, cntrVslInd);
						// End amended on 07/05/2002 by Ai Lin

						// Added by Jitten (Zensar) on 23rd august 2007 starts here
						log.info("[CAB],overStayPeriod==" + overStayPeriod);
						processValueObjectComb.setOverStayPeriod(overStayPeriod);
						// Added by Jitten (Zensar) on 23rd august 2007 ends here

						marineDtlList.add(processValueObjectComb);
					}
				} catch (Exception processChargeException) {

					billErrorValueObject = new BillErrorVO();
					billErrorValueObject.setRunInd(BillErrorVO.RUN_IND_CREATE_BILL);
					billErrorValueObject.setTariffMainCat(ProcessChargeConst.TARIFF_MAIN_MARINE);
					billErrorValueObject.setTariffSubCat(tariffSubCat);
					billErrorValueObject.setRemarks("ProcessChargeException occurred for ~tariff_main_cat_cd ("
							+ tariffMainCat + ") ~tariff_sub_cat_cd (" + tariffSubCat + ") ~vv_cd (" + vvCd
							+ ") ~Msg = " + processChargeException.getMessage());

					// insert into Bill Error table
					// billError.insertBillError(billErrorValueObject);

					vesselError = true;
					continue;
				}
			} // end for loop

			// if there is error in the processing of any of the event log
			// records, set the error indicator in the last record of the
			// ProcessValueObject ArrayList = true
			if (vesselError) {
				processValueObjectComb = new ProcessValueObject();
				processValueObjectComb.setErrorInd(true);

				marineDtlList.add(processValueObjectComb);
			}
		} catch (Exception e) {
			try {
				billErrorValueObject = new BillErrorVO();
				billErrorValueObject.setRunInd(BillErrorVO.RUN_IND_CREATE_BILL);
				billErrorValueObject.setTariffMainCat(ProcessChargeConst.TARIFF_MAIN_MARINE);
				billErrorValueObject.setTariffSubCat(tariffSubCat);
				billErrorValueObject
						.setRemarks("Exception occurred for ~vv_cd (" + vvCd + ") ~Msg = " + e.getMessage());
				// insert into Bill Error table
				// billError.insertBillError(billErrorValueObject);
				new Exception("Exception occurred for vv cd = " + vvCd + " Msg = ", e);
			} catch (Exception billErrorException) {
				log.info("[CAB],[ProcessMarEJB Error] >> Inserting billError exception = "
						+ billErrorException.getMessage() + "Exception occurred for vv cd = " + vvCd + " Msg = ", e);
				new Exception("[ProcessMarEJB Error] >> Inserting billError exception = "
						+ billErrorException.getMessage() + "Exception occurred for vv cd = " + vvCd + " Msg = ", e);
			}
		} finally {
			log.info("END: retrieveMarineDtl  DAO  END");
		}
		return marineDtlList;
	}

	@Override
	public VesselRelatedValueObject getVesselInfo(String vvCode) throws BusinessException {
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		VesselRelatedValueObject vesselVO = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			log.info("START: getVesselInfo  DAO vvCode:" + vvCode);

			sb.append("SELECT VSL_NM, IN_VOY_NBR, OUT_VOY_NBR, VV_CD ,");
			sb.append("LAST_MODIFY_USER_ID, LAST_MODIFY_DTTM, VV_STATUS_IND, VSL_OPR_CD, ");
			sb.append("CNTR_VSL_IND, SCHEME, TERMINAL, BILL_ACCT_NBR, GB_ARRIVAL_WAIVER_CD, GB_DEPARTURE_WAIVER_CD, ");
			sb.append("GB_BERT_BILL_IND, ");
			sb.append("VSL_LOA, VV_CLOSE_DTTM, SHPG_SVC_CD ");
			sb.append("FROM VESSEL_CALL ");
			sb.append("WHERE VV_CD = :vvCode ");
			String sqlViewVessel = sb.toString();

			paramMap.put("vvCode", vvCode);
			log.info(" *** getVesselInfo SQL *****" + sqlViewVessel + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlViewVessel, paramMap);

			if (rs.next()) {
				vesselVO = new VesselRelatedValueObject();
				vesselVO.setVvCd(rs.getString("VV_CD"));
				vesselVO.setVslNm(rs.getString("VSL_NM"));
				vesselVO.setShpgSvcCd(rs.getString("SHPG_SVC_CD"));
				vesselVO.setInVoyNbr(rs.getString("IN_VOY_NBR"));
				vesselVO.setOutVoyNbr(rs.getString("OUT_VOY_NBR"));
				vesselVO.setVslOprCd(rs.getString("VSL_OPR_CD"));
				vesselVO.setCntrVslInd(rs.getString("CNTR_VSL_IND"));
				vesselVO.setSchemeType(rs.getString("SCHEME"));
				vesselVO.setTermType(rs.getString("TERMINAL"));
				vesselVO.setBillAcctNbr(rs.getString("BILL_ACCT_NBR"));
				vesselVO.setGbArrivalWaiveCd(rs.getString("GB_ARRIVAL_WAIVER_CD"));
				vesselVO.setGbDepartureWaiveCd(rs.getString("GB_DEPARTURE_WAIVER_CD"));
				vesselVO.setGbBerthWaiveInd(rs.getString("GB_BERT_BILL_IND"));
			}

			log.info("END: *** getVesselInfo Result *****" + vesselVO.toString());
		} catch (NullPointerException e) {
			log.error("Exception: getVesselInfo ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getVesselInfo ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getVesselInfo  DAO  END");
		}
		return vesselVO;
	}

	public int retrieveVesselDtl(String vslNm) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		StringBuffer sqlQuery = null;
		int vslLoa = 0;
		try {
			log.info("START: retrieveVesselDtl DAO vslNm:" + vslNm);

			// formulate the sql query and execute the query
			sqlQuery = new StringBuffer();
			sqlQuery.append("select vsl_loa ");
			sqlQuery.append("from vessel ");
			sqlQuery.append("where vsl_nm= :vslNm ");

			paramMap.put("vslNm", vslNm);
			log.info(" *** retrieveVesselDtl SQL *****" + sqlQuery.toString() + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery.toString(), paramMap);

			if (rs.next()) {
				// get the database values
				vslLoa = rs.getInt("vsl_loa");
			}
			log.info("END: *** retrieveVesselDtl Result *****" + vslLoa);
		} catch (NullPointerException e) {
			log.error("Exception: retrieveVesselDtl ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveVesselDtl ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveVesselDtl  DAO  END");
		}
		return vslLoa;
	}

	private VesselRelatedValueObject getOSDWaiverDtls(VesselRelatedValueObject vesselRelatedValueObject)
			throws BusinessException {
		SqlRowSet rs = null;
		Map<String, String> paramMap = new HashMap<String, String>();
		String osdWaiverCd = null;
		String isBilled = null;
		String isRejected = null;
		String waiverType = null;
		String billableDuration = null;
		try {
			log.info("START: getOSDWaiverDtls  DAO vesselRelatedValueObject:" + vesselRelatedValueObject.toString());

			String sqlWaiverDtls = "SELECT WAIVER_CD, ISBILLED, ISREJECTED,WAIVER_TYPE,BILLABLE_DURATION FROM OVERSTAY_DOCKAGE_WAIVER WHERE VV_CD = :vvCd ";

			paramMap.put("vvCd", vesselRelatedValueObject.getVvCd());
			log.info(" *** getOSDWaiverDtls SQL *****" + sqlWaiverDtls + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlWaiverDtls, paramMap);

			if (rs.next()) {
				osdWaiverCd = rs.getString("WAIVER_CD");
				isBilled = rs.getString("ISBILLED");
				isRejected = rs.getString("ISREJECTED");
				// Added changes for OverStayDockage Partial Waiver Request -Mcconsulting pteLtd
				waiverType = rs.getString("WAIVER_TYPE");
				billableDuration = rs.getString("BILLABLE_DURATION");
				// Changes ends for OverStayDockage Partial Waiver request form
				// changes-MCConsulting pte ltd
			}

			if (osdWaiverCd != null) {
				osdWaiverCd = osdWaiverCd.trim();
			}
			if (isBilled != null) {
				isBilled = isBilled.trim();
			}
			if (isRejected != null) {
				isRejected = isRejected.trim();
			}

			vesselRelatedValueObject.setOsdWaiverCd(osdWaiverCd);
			vesselRelatedValueObject.setIsBilled(isBilled);
			vesselRelatedValueObject.setIsRejected(isRejected);
			// Added changes for OverStayDockage Partial Waiver Request -Mcconsulting pteLtd
			vesselRelatedValueObject.setWaiverType(waiverType);
			vesselRelatedValueObject.setBillableDuartion(billableDuration);
			// Changes ends for OverStayDockage Partial Waiver request form
			// changes-MCConsulting pte ltd
			log.info("END: *** getOSDWaiverDtls Result *****" + vesselRelatedValueObject.toString());
		} catch (NullPointerException e) {
			log.error("Exception: getOSDWaiverDtls ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getOSDWaiverDtls ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getOSDWaiverDtls  DAO  END");
		}
		return vesselRelatedValueObject;
	}

	public int retrieveNbrChargeBerth(String termType, int maxShiftInd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sqlQuery = null;
		String berthChargeCntStr = null;
		int berthChargeCnt = 0;
		try {
			log.info("START: retrieveNbrChargeBerth  DAO termType:" + termType + ",maxShiftInd:" + maxShiftInd);

			// formulate the sql query and execute the query
			sqlQuery = "select value from system_para where para_cd= :paraCd ";

			if (termType.equals(ProcessChargeConst.CONTAINER_TERMINAL)) {
				paramMap.put("paraCd", ProcessChargeConst.BERTH_CHARGE_COUNT_CT);
			} else if (termType.equals(ProcessChargeConst.GENERAL_BULK_TERMINAL)) {
				paramMap.put("paraCd", ProcessChargeConst.BERTH_CHARGE_COUNT_GB);
			}

			log.info(" *** retrieveNbrChargeBerth SQL *****" + sqlQuery + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, paramMap);

			if (rs.next()) {
				// get the database values
				berthChargeCntStr = rs.getString("value");

				if (berthChargeCntStr != null) {
					berthChargeCntStr = berthChargeCntStr.trim();
				}
			}

			if (berthChargeCntStr == null || (berthChargeCntStr.equals(""))) {
				berthChargeCnt = 0;
			} else {
				berthChargeCnt = Integer.parseInt(berthChargeCntStr);
			}

			if (berthChargeCnt != 1) {
				berthChargeCnt = maxShiftInd;
			}

			log.info("END: *** retrieveNbrChargeBerth Result *****" + berthChargeCnt);
		} catch (NullPointerException e) {
			log.error("Exception: retrieveNbrChargeBerth ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveNbrChargeBerth ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveNbrChargeBerth  DAO  END");
		}
		return berthChargeCnt;
	}

	public int retrieveBACnt(String vvCd, String termType) throws BusinessException {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		List<String> txnCd = new ArrayList<String>();
		SqlRowSet rs = null;
		String sqlQuery = null;
		int nbrFreeAmend = 0;
		int baCnt = 0;
		try {
			log.info("START: retrieveBACnt  DAO vvCd:" + vvCd + ",termType:" + termType);
			// amended by swho on 22/04/2002 for extraction of GBMS vessels for marine
			// charges
			//// added by swho on 160402 to allow for free first ? amendment
			// nbrFreeAmend = retrieveNbrFreeAmend();
			//// end add by swho
			nbrFreeAmend = retrieveNbrFreeAmend(termType);
			// end amend by swho

			// formulate the sql query and execute the query
			// amended by swho on 070602 to put in additional waive indicator check
			//// amended by swho on 160402 to allow for free first ? amendment
			//// sqlQuery = "select count(*) from vessel_call_txn where vv_cd=? and
			// close_time_ind=? and txn_cd in (?,?,?) ";
			// sqlQuery = "select count(*) from vessel_call_txn where ((txn_cd in (?,?) and
			// close_time_ind=?) or (txn_cd in (?) and close_time_ind=? and bill_ind > ?))
			// and vv_cd=? ";
			sqlQuery = "select count(*) from vessel_call_txn where ((txn_cd in (:txnCreate) and close_time_ind= :closeTimeInd) or (txn_cd in (:txnCd) and close_time_ind= :closeTimeInd and bill_ind > :nbrFreeAmend)) and vv_cd= :vvCd and waive_ind = :waiveInd ";

			// paramMap.put(1, vvCd);
			// paramMap.put(2, "Y");
			// paramMap.put(3, ProcessChargeConst.VSL_CALL_TXN_BA_CREATE);
			// paramMap.put(4, ProcessChargeConst.VSL_CALL_TXN_BA_AMEND);
			// paramMap.put(5, ProcessChargeConst.VSL_CALL_TXN_BA_CANCEL);
			txnCd.add(ProcessChargeConst.VSL_CALL_TXN_BA_CREATE);
			txnCd.add(ProcessChargeConst.VSL_CALL_TXN_BA_CANCEL);

			paramMap.addValue("txnCreate", txnCd);
			paramMap.addValue("closeTimeInd", "Y");
			paramMap.addValue("txnCd", ProcessChargeConst.VSL_CALL_TXN_BA_AMEND);
			paramMap.addValue("nbrFreeAmend", nbrFreeAmend);
			paramMap.addValue("vvCd", vvCd);
			paramMap.addValue("waiveInd", "N");
			// end amend by swho

			log.info(" *** retrieveBACnt SQL *****" + sqlQuery + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, paramMap);

			if (rs.next()) {
				// get the database values
				baCnt = rs.getInt(1);
			}
			log.info("END: *** retrieveBACnt Result *****" + baCnt);
		} catch (NullPointerException e) {
			log.error("Exception: retrieveBACnt ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveBACnt ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveBACnt  DAO  END");
		}
		return baCnt;
	}

	private int retrieveNbrFreeAmend(String termType) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sqlQuery = null;
		String nbrFreeAmendStr = null;
		int nbrFreeAmend = 0;
		try {
			log.info("START: retrieveNbrFreeAmend  DAO termType:" + termType);

			// formulate the sql query and execute the query
			sqlQuery = "select value from system_para where para_cd= :berth  ";

			// amended by swho on 22/04/2002 for extraction of GBMS vessels for marine
			// charges
			// paramMap.put(1, "FBACT");
			if (termType.equals(ProcessChargeConst.CONTAINER_TERMINAL)) {
				paramMap.put("berth", ProcessChargeConst.FREE_BERTH_AMEND_NBR_CT);
			} else if (termType.equals(ProcessChargeConst.GENERAL_BULK_TERMINAL)) {
				paramMap.put("berth", ProcessChargeConst.FREE_BERTH_AMEND_NBR_GB);
			}
			// end amend by swho

			log.info(" *** retrieveNbrFreeAmend SQL *****" + sqlQuery + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, paramMap);

			if (rs.next()) {
				// get the database values
				nbrFreeAmendStr = rs.getString("value");

				if (nbrFreeAmendStr != null) {
					nbrFreeAmendStr = nbrFreeAmendStr.trim();
				}
			}

			if (nbrFreeAmendStr == null || (nbrFreeAmendStr.equals(""))) {
				nbrFreeAmend = 0;
			} else {
				nbrFreeAmend = Integer.parseInt(nbrFreeAmendStr);
			}
			log.info("END: *** retrieveNbrFreeAmend Result *****" + nbrFreeAmend);
		} catch (NullPointerException e) {
			log.error("Exception: retrieveNbrFreeAmend ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveNbrFreeAmend ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveNbrFreeAmend  DAO  END");
		}
		return nbrFreeAmend;
	}

	private List<LineTowedVesselValueObject> getLineTowedDockageList(String vvcd) throws BusinessException {
		List<LineTowedVesselValueObject> dockageList = new ArrayList<LineTowedVesselValueObject>();
		try {
			log.info("START: getLineTowedDockageList  DAO vvcd:" + vvcd);
			dockageList = lineTowedVesselRepo.getDockageList(vvcd);
			log.info("END: *** getLineTowedDockageList Result *****" + dockageList.toString());
		} catch (Exception e) {
			log.error("exception: getLineTowedDockageList " + e.toString());
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getLineTowedDockageList  DAO  END");
		}
		return dockageList;
	}

	public ProcessValueObject retrieveTariffDtls(int versionNbr, String tariffMainCat, String tariffSubCat,
			String custCd, String acctNbr, String contractNbr, VesselRelatedValueObject vesselRelatedValueObject,
			BerthRelatedValueObject berthRelatedValueObject, BerthRelatedValueObject berthRelatedValueObject11,
			Timestamp varDttm) throws BusinessException {
		ProcessValueObject processValueObject = null;
		TariffMainVO tariffMainValueObject = null;
		String vvCd = null;
		String vslNm = null;
		String inVoyNbr = null;
		String outVoyNbr = null;
		String shpgSvcCd = null;
		String vslOprCd = null;
		// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
		String schemeType = null;
		String cntrVslInd = null;
		String businessType = null;
		// end add by swho
		// added by swho on 181202 to cater for GST changes wef 1 Jan 2003
		GstCodeValueObject gstCodeValueObject = null;
		List<GstCodeValueObject> gstCodeList = null;
		// end add by swho
		try {
			log.info("START: retrieveTariffDtls  DAO versionNbr:" + versionNbr + ",tariffMainCat:" + tariffMainCat
					+ ",tariffSubCat:" + tariffSubCat + ",custCd:" + custCd + ",varDttm:" + varDttm + ",contractNbr:"
					+ contractNbr + ",vesselRelatedValueObject:" + vesselRelatedValueObject.toString()
					+ ",berthRelatedValueObject:" + berthRelatedValueObject.toString() + ",berthRelatedValueObject11:"
					+ berthRelatedValueObject11 + ",varDttm:" + varDttm);

			// initialize the tariff value objects
			processValueObject = new ProcessValueObject();

			// retrieve tariff details
			// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
			schemeType = vesselRelatedValueObject.getSchemeType();

			log.info("cfg schemetype==================" + schemeType);

			if (!(schemeType.equals(ProcessChargeConst.LINER_SCHEME)
					|| schemeType.equals(ProcessChargeConst.NON_LINER_SCHEME) ||
					// add new scheme for LCT, 13.feb.11 by hpeng
					schemeType.equals(ProcessChargeConst.LCT_SCHEME) ||
					// schemeType.equals(ProcessChargeConst.BARTER_TRADER_SCHEME))) { //<commented
					// to add wooden craft vessel scheme, 12.may.08-cfg>
					schemeType.equals(ProcessChargeConst.BARTER_TRADER_SCHEME)
					|| schemeType.equals(ProcessChargeConst.WOODEN_CRAFT_SCHEME))) {
				schemeType = ProcessChargeConst.LINER_SCHEME;
			}

			cntrVslInd = vesselRelatedValueObject.getCntrVslInd();

			berthRelatedValueObject11.getBerthNbr();
			String cementVslInd = vesselRelatedValueObject.getCementVslInd();
			boolean isSteelVsl = processGenericRepo.determineSteelVsl(vesselRelatedValueObject.getVvCd());
			businessType = processGenericRepo.determineBusinessType(cntrVslInd, berthRelatedValueObject11, cementVslInd,
					isSteelVsl);
			// New EVM Enhancements Sripriya 13 Feb 2018

			// tariffMainValueObject = tariffMain.retrieveTariffMainTier(versionNbr, custCd,
			// acctNbr, contractNbr, tariffMainCat, tariffSubCat);
			// amended by hujun on 7/6/2011 for add tariff item effective date parameter
			tariffMainValueObject = tariffMainRepo.retrieveTariffMainTier(versionNbr, custCd, acctNbr, contractNbr,
					tariffMainCat, tariffSubCat, schemeType, businessType, varDttm);
			// amended end
			// end add by swho

			// for customize tariff.
			if (!(custCd == null && acctNbr == null && contractNbr == null)) {
				if (tariffMainValueObject.isModified(tariffMainValueObject)) {
					processValueObject.setVersionNbrCust(new Integer(versionNbr));
					processValueObject.setContractNbr(contractNbr);
					// added by swho on 181202 to cater for GST changes wef 1 Jan 2003
					// Obtain the new gst rate based on timestamp
					gstCodeList = new ArrayList<GstCodeValueObject>(1);
					gstCodeList = processGenericRepo.getGstCharge(tariffMainValueObject.getGSTCode(), varDttm);

					if (gstCodeList.size() == 0) {
						throw new BusinessException("#GST rate cannot be found# for ~tariff_gst_cd ("
								+ tariffMainValueObject.getGSTCode() + ") and ~eff_dttm (" + varDttm + ")");
					}

					gstCodeValueObject = (GstCodeValueObject) gstCodeList.get(0);

					tariffMainValueObject.setGSTCode(gstCodeValueObject.getCode());
					tariffMainValueObject.setGST(gstCodeValueObject.getGstCharge());
					// end add by swho
					processValueObject.setTariffMainValueObject(tariffMainValueObject);
				}
			}
			// for publish tariff
			else {
				if (tariffMainValueObject.isModified(tariffMainValueObject)) {
					// set published tariff version number into ProcessValueObject
					processValueObject.setVersionNbrPubl(new Integer(versionNbr));
					// added by swho on 181202 to cater for GST changes wef 1 Jan 2003
					// Obtain the new gst rate based on timestamp
					gstCodeList = new ArrayList<GstCodeValueObject>(1);
					gstCodeList = processGenericRepo.getGstCharge(tariffMainValueObject.getGSTCode(), varDttm);

					if (gstCodeList.size() == 0) {
						throw new BusinessException("#GST rate cannot be found# for ~tariff_gst_cd ("
								+ tariffMainValueObject.getGSTCode() + ") and ~eff_dttm (" + varDttm + ")");
					}

					gstCodeValueObject = (GstCodeValueObject) gstCodeList.get(0);

					tariffMainValueObject.setGSTCode(gstCodeValueObject.getCode());
					tariffMainValueObject.setGST(gstCodeValueObject.getGstCharge());
					// end add by swho
					processValueObject.setTariffMainValueObject(tariffMainValueObject);
				} else {
					// from the vessel records, get the basic details
					vvCd = vesselRelatedValueObject.getVvCd();
					vslNm = vesselRelatedValueObject.getVslNm();
					inVoyNbr = vesselRelatedValueObject.getInVoyNbr();
					outVoyNbr = vesselRelatedValueObject.getOutVoyNbr();
					shpgSvcCd = vesselRelatedValueObject.getShpgSvcCd();
					vslOprCd = vesselRelatedValueObject.getVslOprCd();
					throw new BusinessException("#Publish Tariff cannot be found# for ~vv_cd (" + vvCd + ") ~vsl_nm ("
							+ vslNm + ") ~in_voy_nbr (" + inVoyNbr + ") ~out_voy_nbr (" + outVoyNbr + ") ~shpg_svc_cd ("
							+ shpgSvcCd + ") ~vsl_opr_cd (" + vslOprCd + ") ~version_nbr (" + versionNbr
							+ ") ~cust_cd (" + custCd + ") ~acct_nbr (" + acctNbr + ") ~contract_nbr (" + contractNbr
							+ ")");

				}
			}
		} catch (Exception e) {
			log.error("exception: retrieveTariffDtls " + e.toString());
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveTariffDtls  DAO Result:"
					+ (processValueObject != null ? processValueObject.toString() : ""));
		}
		return processValueObject;
	}

	public int retrieveNbrOSTimeAllow(String cntrVslInd, BerthRelatedValueObject berthRelatedValueObject)
			throws BusinessException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SqlRowSet rs = null;
		String sqlQuery = null;
		Timestamp codDttm = null;
		Timestamp colDttm = null;
		Timestamp bcodDttm = null;
		Timestamp bcolDttm = null;
		long codDttmLong = 0;
		long colDttmLong = 0;
		long dttmLong = 0;
		long bdttmLong = 0;
		String nbrOSTimeAllowStr = null;
		int nbrOSTimeAllow = 0;
		try {
			log.info("START: retrieveNbrOSTimeAllow  DAO cntrVslInd: " + cntrVslInd + ",berthRelatedValueObject:"
					+ berthRelatedValueObject.toString());

			// formulate the sql query and execute the query
			sqlQuery = "select value from system_para where para_cd= :paraCd ";

			// set param for container business
			if (cntrVslInd.equals(ProcessChargeConst.CONTAINER_VSL)) {
				paramMap.put("paraCd", ProcessChargeConst.OVERSTAY_DOCK_ALLOW_CT);
			}
			// set param for general / bulk business depending on the lastest timestamp
			else if (cntrVslInd.equals(ProcessChargeConst.GENERAL_BULK_VSL)) {
				codDttm = berthRelatedValueObject.getGbCodDttm();
				colDttm = berthRelatedValueObject.getGbColDttm();
				bcodDttm = berthRelatedValueObject.getGbBCodDttm();
				bcolDttm = berthRelatedValueObject.getGbBColDttm();

				if (bulkCargoExist(berthRelatedValueObject)) {
					if (generalCargoExist(berthRelatedValueObject)) {
						// compare cod/col timestamp for general business
						codDttmLong = 0;
						colDttmLong = 0;
						if (codDttm != null) {
							codDttmLong = codDttm.getTime();
						}
						if (colDttm != null) {
							colDttmLong = colDttm.getTime();
						}
						if (codDttmLong > colDttmLong) {
							dttmLong = codDttmLong;
						} else {
							dttmLong = colDttmLong;
						}

						// compare cod/col timestamp for bulk business
						codDttmLong = 0;
						colDttmLong = 0;
						if (bcodDttm != null) {
							codDttmLong = bcodDttm.getTime();
						}
						if (bcolDttm != null) {
							colDttmLong = bcolDttm.getTime();
						}
						if (codDttmLong > colDttmLong) {
							bdttmLong = codDttmLong;
						} else {
							bdttmLong = colDttmLong;
						}

						// compare timestamps for general & bulk and
						// get the time allowance for the later timestamp
						if (dttmLong > bdttmLong) {
							paramMap.put("paraCd", ProcessChargeConst.OVERSTAY_DOCK_ALLOW_GNRL);
						} else {
							paramMap.put("paraCd", ProcessChargeConst.OVERSTAY_DOCK_ALLOW_BULK);
						}
					} else {
						paramMap.put("paraCd", ProcessChargeConst.OVERSTAY_DOCK_ALLOW_BULK);
					}
				} else {
					paramMap.put("paraCd", ProcessChargeConst.OVERSTAY_DOCK_ALLOW_GNRL);
				} // end-if for bcod/bcol not equal null
			} // end-if for comparison of business type

			log.info(" *** retrieveNbrOSTimeAllow SQL *****" + sqlQuery + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, paramMap);

			if (rs.next()) {
				// get the database values
				nbrOSTimeAllowStr = rs.getString("value");

				if (nbrOSTimeAllowStr != null) {
					nbrOSTimeAllowStr = nbrOSTimeAllowStr.trim();
				}
			}

			if (nbrOSTimeAllowStr == null || (nbrOSTimeAllowStr.equals(""))) {
				nbrOSTimeAllow = 0;
			} else {
				nbrOSTimeAllow = Integer.parseInt(nbrOSTimeAllowStr);
			}

			log.info("END: *** retrieveNbrOSTimeAllow Result *****" + nbrOSTimeAllow);
		} catch (NullPointerException e) {
			log.error("Exception: retrieveNbrOSTimeAllow ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveNbrOSTimeAllow ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveNbrOSTimeAllow  DAO  END");
		}
		return nbrOSTimeAllow;
	}

	private boolean bulkCargoExist(BerthRelatedValueObject berthRelatedValueObject) throws BusinessException {
		Timestamp codDttm = null;
		Timestamp colDttm = null;
		boolean existInd = false;
		try {
			log.info("START: bulkCargoExist  DAO berthRelatedValueObject:" + berthRelatedValueObject.toString());
			codDttm = berthRelatedValueObject.getGbBCodDttm();
			colDttm = berthRelatedValueObject.getGbBColDttm();

			if (codDttm != null || colDttm != null) {
				existInd = true;
			} else {
				existInd = false;
			} // end-if for cod/col not equal null
		} catch (Exception e) {
			log.error("Exception: bulkCargoExist ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: bulkCargoExist  DAO existInd:" + existInd);
		}
		return existInd;
	}

	private boolean generalCargoExist(BerthRelatedValueObject berthRelatedValueObject) throws BusinessException {
		Timestamp codDttm = null;
		Timestamp colDttm = null;
		boolean existInd = false;
		try {
			log.info("START: generalCargoExist  DAO berthRelatedValueObject:" + berthRelatedValueObject.toString());
			codDttm = berthRelatedValueObject.getGbCodDttm();
			colDttm = berthRelatedValueObject.getGbColDttm();

			if (codDttm != null || colDttm != null) {
				existInd = true;
			} else {
				existInd = false;
			} // end-if for cod/col not equal null
		} catch (Exception e) {
			log.error("Exception: generalCargoExist ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: generalCargoExist  DAO existInd:" + existInd);
		}
		return existInd;
	}

	private Timestamp determineLastActivityDttm(String cntrVslInd, BerthRelatedValueObject berthRelatedValueObject)
			throws BusinessException {
		String gcOperations = null;
		Timestamp lastActDttm = null;
		Timestamp codDttm = null;
		Timestamp colDttm = null;
		Timestamp gbLastActivityDttm = null;
		long codDttmLong = 0;
		long colDttmLong = 0;
		try {
			log.info("START: determineLastActivityDttm  DAO cntrVslInd:" + cntrVslInd + ",berthRelatedValueObject:"
					+ berthRelatedValueObject.toString());
			if (cntrVslInd.equals(ProcessChargeConst.CONTAINER_VSL)) {
				codDttm = berthRelatedValueObject.getCodDttm();
				colDttm = berthRelatedValueObject.getColDttm();
				gbLastActivityDttm = berthRelatedValueObject.getGbLastActDttm();
				gcOperations = berthRelatedValueObject.getGcOperations();

				if (codDttm == null) {
					lastActDttm = colDttm;
				} else if (colDttm == null) {
					lastActDttm = codDttm;
				} else if (codDttm != null && colDttm != null) {
					codDttmLong = codDttm.getTime();
					colDttmLong = colDttm.getTime();

					if (colDttmLong > codDttmLong) {
						lastActDttm = colDttm;
					} else {
						lastActDttm = codDttm;
					}
				}
				if (gcOperations != null && gcOperations.equalsIgnoreCase("Y")) {
					if (codDttm == null) {
						if (colDttm == null) {
							if (gbLastActivityDttm != null) {
								lastActDttm = gbLastActivityDttm;
							}
						} else {
							if (gbLastActivityDttm != null) {
								lastActDttm = (gbLastActivityDttm.getTime() > colDttm.getTime()) ? gbLastActivityDttm
										: colDttm;
							} else {
								lastActDttm = colDttm;
							}
						}
					} else if (colDttm == null) {
						if (gbLastActivityDttm != null) {
							lastActDttm = (gbLastActivityDttm.getTime() > codDttm.getTime()) ? gbLastActivityDttm
									: codDttm;
						} else {
							lastActDttm = codDttm;
						}
					} else if (codDttm != null && colDttm != null && gbLastActivityDttm == null) {
						lastActDttm = (colDttm.getTime() > codDttm.getTime()) ? colDttm : codDttm;
					} else if (codDttm != null && colDttm != null && gbLastActivityDttm != null) {
						lastActDttm = gbLastActivityDttm
								.getTime() > (codDttm.getTime() > colDttm.getTime() ? codDttm.getTime()
										: colDttm.getTime()) ? gbLastActivityDttm
												: ((codDttm.getTime() > colDttm.getTime()) ? codDttm : colDttm);
					}
					log.info("[CAB],\n BERTH NBR :=" + berthRelatedValueObject.getBerthNbr() + "\n COMBI_INDICATOR ='Y'"
							+ "\n codDttm := " + codDttm + "\n colDttm := " + colDttm + "\n gbLastActivityDttm := "
							+ gbLastActivityDttm + "\n last ActDttm timestamp  for Marine Charges  := " + lastActDttm);

				}
			} else if (cntrVslInd.equals(ProcessChargeConst.GENERAL_BULK_VSL)) {
				lastActDttm = berthRelatedValueObject.getGbLastActDttm();
			}
		} catch (Exception e) {
			log.error("Exception: determineLastActivityDttm ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: determineLastActivityDttm  DAO Result:" + lastActDttm);
		}
		return lastActDttm;
	}

	private long determineOverStayPeriod(String vv_cd, String cntrVslInd, String schemeType, String gbDepartureWaiveCd,
			int nbrOSTimeAllow, Timestamp lastActDttm, BerthRelatedValueObject berthRelatedValueObject,
			String osdWaiverCd, String isRejected, String waiverType) throws BusinessException {
		Timestamp atuDttm = null;
		long atuDttmLong = 0;
		long lastActDttmLong = 0;
		long nbrOSTimeAllowLong = 0;
		double tempPeriodDouble = 0;
		long overStayPeriod = 0;
		long tempPeriodLong = 0;
		try {
			log.info("START: determineOverStayPeriod  DAO vv_cd:" + vv_cd + ",cntrVslInd:" + cntrVslInd + ",schemeType:"
					+ schemeType + ",gbDepartureWaiveCd:" + gbDepartureWaiveCd + ",nbrOSTimeAllow:" + nbrOSTimeAllow
					+ ",berthRelatedValueObject:" + berthRelatedValueObject.toString() + ",osdWaiverCd:" + osdWaiverCd
					+ ",isRejected:" + isRejected + ",waiverType:" + waiverType);
			// get the ATU timestamp from the BerthRelatedValueObject
			atuDttm = berthRelatedValueObject.getAtuDttm();

			// convert the timestamps to milliseconds
			atuDttmLong = atuDttm.getTime();
			lastActDttmLong = lastActDttm.getTime();
			nbrOSTimeAllowLong = nbrOSTimeAllow * (60 * 60 * 1000);
			if (isCementVessel(vv_cd)) {
				nbrOSTimeAllowLong = (90 * 60 * 1000); // hardcoded to 90mins for cement bulk vessels as requested by
														// Kong Meng
				log.info("[CAB],hkm nbrOSTimeAllowLong = " + nbrOSTimeAllowLong + "...");
			}
			log.info("[CAB],hkm nbrOSTimeAllowLong = " + nbrOSTimeAllowLong + "...");
			log.info("[CAB],atu dttm long = " + atuDttmLong + "...");
			log.info("[CAB],last act dttm long = " + lastActDttmLong + "...");
			log.info("[CAB][determineOverStayPeriod] nbr os time allow long = " + nbrOSTimeAllowLong + "...");

			// compute over stay period in hours
			tempPeriodDouble = (atuDttmLong - lastActDttmLong) / (60 * 60 * 1000.0);
			// line added by Ho Kong Meng 3 Apr 2013 //
			tempPeriodLong = (atuDttmLong - lastActDttmLong);
			log.info("[CAB],tempPeriodLong = " + tempPeriodLong + "...");

			log.info("[CAB],temp period double = " + tempPeriodDouble + "...");

			log.info("[CAB],cntrVslInd = " + cntrVslInd + "...");
			log.info("[CAB],schemeType = " + schemeType + "...");
			log.info("[CAB],gbDepartureWaiveCd = " + gbDepartureWaiveCd + "...");
			log.info("[CAB],osdWaiverCd = " + osdWaiverCd + "...");
			log.info("[CAB],isRejected = " + isRejected + "...");

			// determine what is the over stay period
			// if ((schemeType.equals(ProcessChargeConst.BARTER_TRADER_SCHEME)) ||
			// <cfg commented^^ to remove osd charges for wooden craft, 04.jul.08>
			if ((schemeType.equals(ProcessChargeConst.BARTER_TRADER_SCHEME)
					|| schemeType.equals(ProcessChargeConst.WOODEN_CRAFT_SCHEME)) ||
			// <cfg commented^^ to remove osd charges for wooden craft, 04.jul.08/>
			// Changed method signature by Ruchika: 21-Aug-07 to cater to new flow for
			// overstay dockage in case waiver is not processed at the time of billing
			// (gbDepartureWaiveCd != null) ||
			// Added changes for OverStayDockage Partial Waiver Request -Mcconsulting pteLtd
					(osdWaiverCd != null && ((isRejected == null) || (waiverType != null && waiverType.equals("F")
							&& isRejected != null && isRejected.equalsIgnoreCase("N")))
					// Changes ends for OverStayDockage Partial Waiver request form
					// changes-MCConsulting pte ltd
					) ||
					// End of change by Ruchika: 21-Aug-07
					// change by Ho Kong Meng: 3 Apr 2013 //
					// (tempPeriodDouble <= nbrOSTimeAllow)) //
					(tempPeriodLong <= nbrOSTimeAllowLong)) {
				log.info("[CAB],nbrOSTimeAllowLong = " + nbrOSTimeAllowLong + "...");
				log.info("[CAB],inside tempPeriodLong = " + tempPeriodLong + "...");
				overStayPeriod = 0;
			} else {
				overStayPeriod = atuDttmLong - (lastActDttmLong + nbrOSTimeAllowLong);
				//CH-7 changes starts
				log.info("overstay period before removing exemption duration: "+overStayPeriod);
				overStayPeriod = overStayPeriod - retrieveExemptionDuration("O", vv_cd);
				overStayPeriod = overStayPeriod < 0 ? 0: overStayPeriod;
				//CH-7 changes ends
				log.info("[CAB],in overstay occurred");
				log.info("[CAB] in overstay occurred " + overStayPeriod);
			}
		} catch (Exception e) {
			log.error("Exception: determineOverStayPeriod ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: determineOverStayPeriod  DAO Result:" + lastActDttm);
		}
		return overStayPeriod;
	}

	public boolean isCementVessel(String vv_cd) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sqlQuery = null;
		int nbrNoRecFound = 0;
		boolean shorterStay = false;
		try {
			log.info("START: isCementVessel  DAO vv_cd:" + vv_cd);

			// formulate the sql query and execute the query
			sqlQuery = " select count(*) from VESSEL_PRE_OPS where vv_cd = :vvCd and instr ((select value from text_para where para_cd ='OSCBV_CCD' ), ''''||cc_cd||'''' ) >0  ";

			paramMap.put("vvCd", vv_cd);

			log.info(" *** isCementVessel SQL *****" + sqlQuery + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, paramMap);

			if (rs.next()) {
				// get the database values
				nbrNoRecFound = rs.getInt(1);

				if (nbrNoRecFound > 0) {
					shorterStay = true;
				} else {
					shorterStay = false;
				}
			}
			log.info("END: *** isCementVessel Result *****" + shorterStay);
		} catch (NullPointerException e) {
			log.error("Exception: isCementVessel ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: isCementVessel ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: isCementVessel  DAO  END");
		}
		return shorterStay;
	}

	private long determineLineTowedStayPeriod(List<LineTowedVesselValueObject> co, String subcat, String subcat2)
			throws BusinessException {
		long period = 0L;
		try {
			log.info("START: determineLineTowedStayPeriod  DAO co:" + co.toString() + ",subcat:" + subcat + ",subcat:"
					+ subcat);
			for (Iterator<LineTowedVesselValueObject> iterator = co.iterator(); iterator.hasNext();) {
				LineTowedVesselValueObject o = (LineTowedVesselValueObject) iterator.next();

				String tariffSubCatCd = o.getTariffSubCatCode();
				// Changed by Ruchika: 21-Aug-07 to cater to new sub-cat of 08 for overstay
				// dockage
				// if (tariffSubCatCd.equalsIgnoreCase(subcat))
				if (tariffSubCatCd.equalsIgnoreCase(subcat) || tariffSubCatCd.equalsIgnoreCase(subcat2))
				// End of change by Ruchika
				{
					period = o.getEndTimestamp().getTime() - o.getStartTimestamp().getTime();
					break;
				}
			}
		} catch (Exception e) {
			log.error("Exception: determineLineTowedStayPeriod ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: determineLineTowedStayPeriod  DAO Result:" + period);
		}
		return period;
	}

	private long determineVesselStayPeriod(String vv_cd, String cntrVslInd, long overStayPeriod, int nbrOSTimeAllow,
			Timestamp lastActDttm, BerthRelatedValueObject berthRelatedValueObject1,
			BerthRelatedValueObject berthRelatedValueObject2) throws BusinessException {
		Timestamp atuDttm = null;
		Timestamp atbDttm = null;
		long atuDttmLong = 0;
		long atbDttmLong = 0;
		long lastActDttmLong = 0;
		long nbrOSTimeAllowLong = 0;
		long vesselStayPeriod = 0;
		try {
			log.info("START: determineVesselStayPeriod  DAO vv_cd:" + vv_cd + ",cntrVslInd:" + cntrVslInd
					+ ",overStayPeriod:" + overStayPeriod + ",nbrOSTimeAllow:" + nbrOSTimeAllow + ",lastActDttm:"
					+ lastActDttm + ",berthRelatedValueObject1:" + berthRelatedValueObject1.toString()
					+ ",berthRelatedValueObject2:" + berthRelatedValueObject2.toString());
			// get the ATU/ATB timestamp from the BerthRelatedValueObject
			atuDttm = berthRelatedValueObject2.getAtuDttm();
			atbDttm = berthRelatedValueObject1.getAtbDttm();
			log.info("[CAB],atu dttm = " + atuDttm + "...");
			log.info("[CAB],atb dttm = " + atbDttm + "...");

			// convert the timestamps to milliseconds
			atuDttmLong = atuDttm.getTime();
			atbDttmLong = atbDttm.getTime();
			log.info("[CAB],atu dttm long = " + atuDttmLong + "...");
			log.info("[CAB],atb dttm long = " + atbDttmLong + "...");

			// determine what is the vessel stay period i.e. chargeable standard dockage
			// if ((overStayPeriod == 0) ||
			// (cntrVslInd.equals(ProcessChargeConst.CONTAINER_VSL))) { // <cfg commented
			// for overstay dockage - CT to follow GB, 29.apr.08>
			if (overStayPeriod == 0) { // <cfg added for overstay dockage>
				/*
				 * <cfg commented ^ OSD changes for wooden craft to port first in prod,
				 * 09.jun.08 cfg put back OSD changes - CT to follow GB, 26.jun.08>
				 */
				vesselStayPeriod = atuDttmLong - atbDttmLong;
			} else {
				// convert the timestamp to milliseconds
				lastActDttmLong = lastActDttm.getTime();
				nbrOSTimeAllowLong = nbrOSTimeAllow * (60 * 60 * 1000);
				// added by Dongsheng on 1/4/2013 for CR-OPS-20130326-002 - to change Overstay
				// Dockage grace period from 2 hours to 1.5 hours for Cement Bulk Vessels
				if (isCementVessel(vv_cd)) {
					nbrOSTimeAllowLong = (90 * 60 * 1000); // hardcoded to 90 for cement bulk vessels.
				}
				log.info("[CAB],last act dttm long = " + lastActDttmLong + "...");
				log.info("[CAB], [determineVesselStayPeriod] nbr os time allow long = " + nbrOSTimeAllowLong + "...");

				vesselStayPeriod = (lastActDttmLong + nbrOSTimeAllowLong) - atbDttmLong;
			}

		} catch (Exception e) {
			log.error("Exception: determineVesselStayPeriod ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: determineVesselStayPeriod  DAO Result:" + vesselStayPeriod);
		}
		return vesselStayPeriod;
	}

	private double determineLateArrivalPeriod(String schemeType, String gbArrivalWaiveCd,
			BerthRelatedValueObject berthRelatedValueObject, String vvcd) throws BusinessException {
		Timestamp atbDttm = null;
		Timestamp etbDttm = null;
		double lateArrivalPeriod = 0;
		try {
			log.info("START: determineLateArrivalPeriod  DAO schemeType:" + schemeType + ",gbArrivalWaiveCd:"
					+ gbArrivalWaiveCd + ",berthRelatedValueObject:" + berthRelatedValueObject.toString());
			if ((!schemeType.equals(ProcessChargeConst.BARTER_TRADER_SCHEME))
					&& ((gbArrivalWaiveCd == null) || (gbArrivalWaiveCd.equals("")))) {
				atbDttm = berthRelatedValueObject.getAtbDttm();
				etbDttm = berthRelatedValueObject.getEtbDttm();

				// Gets late arrival period in minutes
				lateArrivalPeriod = (atbDttm.getTime() - etbDttm.getTime()
						- (getLateArrChargeTime(ProcessChargeConst.LATE_ARRIVAL_CHARGE_VAR) * 60000)) / 60000.0;
				//CH-7 changes starts
				log.info("Late arrival Period before exemption duration: " + lateArrivalPeriod );
				lateArrivalPeriod = lateArrivalPeriod - retrieveExemptionDuration("L", vvcd);
				lateArrivalPeriod = lateArrivalPeriod < 0 ? 0: lateArrivalPeriod;
				//CH-7 changes ends
				log.info("[CAB],[ProvessMarine.determineLateArrivalPeriod] lateArrivalPeriod = " + lateArrivalPeriod);

				if (lateArrivalPeriod < 0) { // <cfg commented to remove late arrival charges for wooden craft,
												// 04.jul.08>
					// if (schemeType.equals(ProcessChargeConst.WOODEN_CRAFT_SCHEME) ||
					// lateArrivalPeriod < 0) { //<cfg remove addtl codes for late arrival..,
					// 14.jul.08>
					lateArrivalPeriod = 0;
				}
			}
		} catch (Exception e) {
			log.error("Exception: determineLateArrivalPeriod ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: determineLateArrivalPeriod  DAO Result:" + lateArrivalPeriod);
		}
		return lateArrivalPeriod;
	}

	private int getLateArrChargeTime(String lateArr) throws BusinessException {
		SqlRowSet rs = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = "";
		int lateArrChargeTime = 0;
		try {
			log.info("START: getLateArrChargeTime  DAO lateArr:" + lateArr);

			sql = "SELECT VALUE FROM SYSTEM_PARA WHERE PARA_CD = :lateArr";
			paramMap.put("lateArr", lateArr);

			log.info(" *** getLateArrChargeTime SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				// get the database value
				lateArrChargeTime = Integer.parseInt(CommonUtility.deNull(rs.getString("VALUE")));
			}
			log.info("END: *** getLateArrChargeTime Result *****" + lateArrChargeTime);
		} catch (NullPointerException e) {
			log.error("Exception: getLateArrChargeTime ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: getLateArrChargeTime ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: getLateArrChargeTime  DAO  END");
		}
		return lateArrChargeTime;
	}

	private ProcessValueObject populateForCustomizeTariff(ProcessValueObject processValueObjectPubl,
			VesselRelatedValueObject vesselRelatedValueObject, BerthRelatedValueObject berthRelatedValueObject,
			String vslOprCd, String shpgSvcCd, Timestamp varDttm, String tariffMainCat, String tariffSubCat)
			throws BusinessException {
		ProcessValueObject processValueObjectComb = null;
		ProcessValueObject processValueObjectCust = null;
		TariffMainVO tariffMainValueObject = null;
		TariffTierVO tariffTierValueObject = null;
		TariffContainerVO tariffContainerValueObject = null;
		CntrEventLogValueObject cntrEventLogValueObject = null;
		AccountValueObject accountValueObject = null;
		ContractSearchKeyValueObject contractSearchKeyValueObject = null;
		int xcnt = 0;
		String cntrOprCd = null;
		String slotOprCd = null;
		Timestamp startDttm = null;
		String startDttmTypeCd = null;
		Timestamp endDttm = null;
		String endDttmTypeCd = null;
		String tariffCd = null;
		String billParty = null;
		String oprCd = null;
		String lineCd = null;
		String idCode = null;
		String idCodeInd = null;
		int custVersionNbr = 0;
		String custCd = null;
		String acctNbr = null;
		String contractNbr = null;
		double timeUnit = 0;
		double otherUnit = 0;
		try {
			log.info("START: populateForCustomizeTariff  DAO processValueObjectPubl:"
					+ processValueObjectPubl.toString() + ",vesselRelatedValueObject:"
					+ vesselRelatedValueObject.toString() + ",berthRelatedValueObject:"
					+ berthRelatedValueObject.toString() + ",vslOprCd:" + vslOprCd + ",shpgSvcCd:" + shpgSvcCd
					+ ",varDttm:" + varDttm + ",tariffMainCat:" + tariffMainCat + ",tariffSubCat:" + tariffSubCat);

			// initialize the value object
			processValueObjectCust = new ProcessValueObject();
			processValueObjectComb = new ProcessValueObject();
			cntrEventLogValueObject = new CntrEventLogValueObject();

			// get the bill party from the ProcessValueObject
			billParty = (processValueObjectPubl.getTariffMainValueObject()).getBillParty();
			if (billParty == null) {
				tariffCd = (processValueObjectPubl.getTariffMainValueObject()).getCode();

				String[] tmp = { tariffCd };

				throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrMsg_Bill_Party_Not_Found, tmp));

			}

			if (billParty.equals(ProcessChargeConst.BILL_PARTY_VESL_OPR)) {
				// set operator code = vessel operator code
				oprCd = vslOprCd;
			} else {
				throw new BusinessException(ConstantUtil.ErrMsg_Bill_Party_Not_Vessel_Operator);

			}

			// retrieve the line code based on the derived operator code
			lineCd = processGenericRepo.retrieveLineCode(oprCd);

			// retrieve the customer account details for further processing Ruchika(below
			// method needs change if business type other than C%)
			accountValueObject = processGenericRepo.retrieveCustAcct(lineCd);

			if (accountValueObject == null)
				log.info("[CAB],account value object = " + accountValueObject + "...");

			// set data for retrieval of customer contract
			idCode = shpgSvcCd;
			idCodeInd = "1";

			// retrieve the customer contract and determine its existence
			contractSearchKeyValueObject = processGenericRepo.retrieveCustContractExist(accountValueObject, idCodeInd,
					idCode, varDttm);

			// contract exist, search customize tariff
			if (contractSearchKeyValueObject.isModified(contractSearchKeyValueObject)) {
				// retrieve customer tariff version number
				custVersionNbr = processGenericRepo.retrieveCustTariffVersion(contractSearchKeyValueObject, varDttm);

				// retrieve the customized tariff, when customize version number is valid
				if (custVersionNbr != 0) {
					custCd = contractSearchKeyValueObject.getCustomerCode();
					acctNbr = contractSearchKeyValueObject.getAccountNumber();
					contractNbr = contractSearchKeyValueObject.getContractNumber();
					// amended by swho on 181202 to cater for GST change wef 1 Jan 2003
					// processValueObjectCust = retrieveTariffDtls(custVersionNbr, tariffMainCat,
					// tariffSubCat, custCd, acctNbr, contractNbr, vesselRelatedValueObject,
					// berthRelatedValueObject);
					processValueObjectCust = retrieveTariffDtls(custVersionNbr, tariffMainCat, tariffSubCat, custCd,
							acctNbr, contractNbr, vesselRelatedValueObject, berthRelatedValueObject, varDttm);
					// end amend by swho
				} // end-if for customize version number = valid
			} // end-if for existence of contract

			// Combine both the publish and customize ProcessValueObject
			processValueObjectComb.setVersionNbrPubl(processValueObjectPubl.getVersionNbrPubl());
			processValueObjectComb.setVersionNbrCust(processValueObjectCust.getVersionNbrCust());
			processValueObjectComb.setCustCd(accountValueObject.getCustomerCode());
			processValueObjectComb.setAcctNbr(accountValueObject.getAccountNumber());
			processValueObjectComb.setContractNbr(null);
			processValueObjectComb.setCurrency(accountValueObject.getCurrency());
			processValueObjectComb.setCntrOprCd(cntrOprCd);
			processValueObjectComb.setSlotOprCd(slotOprCd);
			processValueObjectComb.setOprCd(oprCd);
			processValueObjectComb.setStartDttm(startDttm);
			processValueObjectComb.setStartDttmTypeCd(startDttmTypeCd);
			processValueObjectComb.setEndDttm(endDttm);
			processValueObjectComb.setEndDttmTypeCd(endDttmTypeCd);

			cntrEventLogValueObject.setDiscVvCd(vesselRelatedValueObject.getVvCd());
			processValueObjectComb.setCntrEventLogValueObject(cntrEventLogValueObject);

			if (processValueObjectPubl.isModified(processValueObjectPubl)) {
				tariffMainValueObject = processValueObjectPubl.getTariffMainValueObject();
				tariffContainerValueObject = processValueObjectPubl.getTariffContainerValueObject();

				// loop thru the tariff tier records and set
				// TariffTierValueObject into TariffMainValueObject
				for (xcnt = 0; xcnt < tariffMainValueObject.getTierCount(); xcnt++) {
					// get the individual record of the tariff tier record
					tariffTierValueObject = tariffMainValueObject.getTier(xcnt);

					tariffTierValueObject.setTimeUnit(timeUnit);
					tariffTierValueObject.setOtherUnit(otherUnit);
				}
				tariffMainValueObject.addContainer(tariffContainerValueObject);
				processValueObjectComb.addMain(tariffMainValueObject);
			}

			if (processValueObjectCust.isModified(processValueObjectCust)) {
				tariffMainValueObject = processValueObjectCust.getTariffMainValueObject();
				tariffContainerValueObject = processValueObjectCust.getTariffContainerValueObject();

				// loop thru the tariff tier records and set
				// TariffTierValueObject into TariffMainValueObject
				for (xcnt = 0; xcnt < tariffMainValueObject.getTierCount(); xcnt++) {
					// get the individual record of the tariff tier record
					tariffTierValueObject = tariffMainValueObject.getTier(xcnt);
					tariffTierValueObject.setTimeUnit(timeUnit);
					tariffTierValueObject.setOtherUnit(otherUnit);
				}
				tariffMainValueObject.addContainer(tariffContainerValueObject);
				processValueObjectComb.setContractNbr(processValueObjectCust.getContractNbr());
				processValueObjectComb.addMain(tariffMainValueObject);
			}
		} catch (Exception e) {
			log.error("Exception: populateForCustomizeTariff ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: populateForCustomizeTariff  DAO  Result:"
					+ (processValueObjectComb != null ? processValueObjectComb.toString() : ""));
		}
		return processValueObjectComb;
	}

	public ProcessValueObject retrieveTariffDtls(int versionNbr, String tariffMainCat, String tariffSubCat,
			String custCd, String acctNbr, String contractNbr, VesselRelatedValueObject vesselRelatedValueObject,
			BerthRelatedValueObject berthRelatedValueObject, Timestamp varDttm) throws BusinessException {
		ProcessValueObject processValueObject = null;
		TariffMainVO tariffMainValueObject = null;
		String vvCd = null;
		String vslNm = null;
		String inVoyNbr = null;
		String outVoyNbr = null;
		String shpgSvcCd = null;
		String vslOprCd = null;
		// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
		String schemeType = null;
		String cntrVslInd = null;
		String businessType = null;
		// end add by swho
		GstCodeValueObject gstCodeValueObject = null;
		List<GstCodeValueObject> gstCodeList = null;
		// end add by swho
		try {
			log.info("START: retrieveTariffDtls  DAO versionNbr:" + versionNbr + ",tariffMainCat:" + tariffMainCat
					+ ",tariffSubCat:" + tariffSubCat + ",custCd:" + custCd + ",acctNbr:" + acctNbr + ",contractNbr:"
					+ contractNbr + ",vesselRelatedValueObject:" + vesselRelatedValueObject.toString()
					+ ",berthRelatedValueObject:" + berthRelatedValueObject.toString() + ",varDttm:" + varDttm);

			// end add by swho

			// initialize the tariff value objects
			processValueObject = new ProcessValueObject();

			// retrieve tariff details
			// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
			schemeType = vesselRelatedValueObject.getSchemeType();

			log.info("cfg schemetype==================" + schemeType);

			if (!(schemeType.equals(ProcessChargeConst.LINER_SCHEME)
					|| schemeType.equals(ProcessChargeConst.NON_LINER_SCHEME) ||
					// add new scheme for LCT, 13.feb.11 by hpeng
					schemeType.equals(ProcessChargeConst.LCT_SCHEME) ||
					// schemeType.equals(ProcessChargeConst.BARTER_TRADER_SCHEME))) { //<commented
					// to add wooden craft vessel scheme, 12.may.08-cfg>
					schemeType.equals(ProcessChargeConst.BARTER_TRADER_SCHEME)
					|| schemeType.equals(ProcessChargeConst.WOODEN_CRAFT_SCHEME))) {
				schemeType = ProcessChargeConst.LINER_SCHEME;
			}

			cntrVslInd = vesselRelatedValueObject.getCntrVslInd();
			businessType = determineBusinessType(cntrVslInd, berthRelatedValueObject);

			// tariffMainValueObject = tariffMain.retrieveTariffMainTier(versionNbr, custCd,
			// acctNbr, contractNbr, tariffMainCat, tariffSubCat);
			// amended by hujun on 7/6/2011 for add tariff item effective date parameter
			tariffMainValueObject = tariffMainRepo.retrieveTariffMainTier(versionNbr, custCd, acctNbr, contractNbr,
					tariffMainCat, tariffSubCat, schemeType, businessType, varDttm);
			// amended end
			// end add by swho

			// for customize tariff.
			if (!(custCd == null && acctNbr == null && contractNbr == null)) {
				if (tariffMainValueObject.isModified(tariffMainValueObject)) {
					processValueObject.setVersionNbrCust(new Integer(versionNbr));
					processValueObject.setContractNbr(contractNbr);
					// added by swho on 181202 to cater for GST changes wef 1 Jan 2003
					// Obtain the new gst rate based on timestamp
					gstCodeList = new ArrayList<GstCodeValueObject>(1);
					gstCodeList = processGenericRepo.getGstCharge(tariffMainValueObject.getGSTCode(), varDttm);

					if (gstCodeList.size() == 0) {
						throw new BusinessException("#GST rate cannot be found# for ~tariff_gst_cd ("
								+ tariffMainValueObject.getGSTCode() + ") and ~eff_dttm (" + varDttm + ")");
					}

					gstCodeValueObject = (GstCodeValueObject) gstCodeList.get(0);

					tariffMainValueObject.setGSTCode(gstCodeValueObject.getCode());
					tariffMainValueObject.setGST(gstCodeValueObject.getGstCharge());
					// end add by swho
					processValueObject.setTariffMainValueObject(tariffMainValueObject);
				}
			}
			// for publish tariff
			else {
				if (tariffMainValueObject.isModified(tariffMainValueObject)) {
					// set published tariff version number into ProcessValueObject
					processValueObject.setVersionNbrPubl(new Integer(versionNbr));
					// added by swho on 181202 to cater for GST changes wef 1 Jan 2003
					// Obtain the new gst rate based on timestamp
					gstCodeList = new ArrayList<GstCodeValueObject>(1);
					gstCodeList = processGenericRepo.getGstCharge(tariffMainValueObject.getGSTCode(), varDttm);

					if (gstCodeList.size() == 0) {
						throw new BusinessException("#GST rate cannot be found# for ~tariff_gst_cd ("
								+ tariffMainValueObject.getGSTCode() + ") and ~eff_dttm (" + varDttm + ")");
					}

					gstCodeValueObject = (GstCodeValueObject) gstCodeList.get(0);

					tariffMainValueObject.setGSTCode(gstCodeValueObject.getCode());
					tariffMainValueObject.setGST(gstCodeValueObject.getGstCharge());
					// end add by swho
					processValueObject.setTariffMainValueObject(tariffMainValueObject);
				} else {
					// from the vessel records, get the basic details
					vvCd = vesselRelatedValueObject.getVvCd();
					vslNm = vesselRelatedValueObject.getVslNm();
					inVoyNbr = vesselRelatedValueObject.getInVoyNbr();
					outVoyNbr = vesselRelatedValueObject.getOutVoyNbr();
					shpgSvcCd = vesselRelatedValueObject.getShpgSvcCd();
					vslOprCd = vesselRelatedValueObject.getVslOprCd();
					throw new BusinessException("#Publish Tariff cannot be found# for ~vv_cd (" + vvCd + ") ~vsl_nm ("
							+ vslNm + ") ~in_voy_nbr (" + inVoyNbr + ") ~out_voy_nbr (" + outVoyNbr + ") ~shpg_svc_cd ("
							+ shpgSvcCd + ") ~vsl_opr_cd (" + vslOprCd + ") ~version_nbr (" + versionNbr
							+ ") ~cust_cd (" + custCd + ") ~acct_nbr (" + acctNbr + ") ~contract_nbr (" + contractNbr
							+ ")");

				}
			}
			log.info("END: *** retrieveTariffDtls Result *****" + processValueObject.toString());
		} catch (Exception e) {
			log.error("Exception: retrieveTariffDtls ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveTariffDtls  DAO  END");
		}
		return processValueObject;
	}

	private String determineBusinessType(String cntrVslInd, BerthRelatedValueObject berthRelatedValueObject)
			throws BusinessException {
		String businessType = null;
		try {
			log.info("START: determineBusinessType  DAO cntrVslInd:" + cntrVslInd + ",berthRelatedValueObject:"
					+ berthRelatedValueObject.toString());
			// set param for container business
			if (cntrVslInd.equals(ProcessChargeConst.CONTAINER_VSL)) {
				businessType = ProcessChargeConst.CONTAINER_BUSINESS;
			}
			// set param for general / bulk business
			else if (cntrVslInd.equals(ProcessChargeConst.GENERAL_BULK_VSL)) {
				if (bulkCargoExist(berthRelatedValueObject)) {
					businessType = ProcessChargeConst.BULK_BUSINESS;
				} else {
					businessType = ProcessChargeConst.GENERAL_BUSINESS;
				}
			}
		} catch (Exception e) {
			log.error("Exception: determineBusinessType ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: determineBusinessType  DAO  END");
		}
		return businessType;
	}

	private ProcessValueObject populateForCustomizeTariff(ProcessValueObject processValueObjectPubl,
			VesselRelatedValueObject vesselRelatedValueObject, BerthRelatedValueObject berthRelatedValueObject,
			BerthRelatedValueObject berthRelatedValueObject11, Timestamp varDttm, String tariffMainCat,
			String tariffSubCat) throws BusinessException {
		Exception ex = null;
		ProcessValueObject processValueObjectComb = null;
		ProcessValueObject processValueObjectCust = null;
		TariffMainVO tariffMainValueObject = null;
		TariffTierVO tariffTierValueObject = null;
		TariffContainerVO tariffContainerValueObject = null;
		CntrEventLogValueObject cntrEventLogValueObject = null;
		AccountValueObject accountValueObject = null;
		ContractValueObject contractValueObject = null;
		int xcnt = 0;
		String vvCd = null;
		String billAcctNbr = null;
		String cntrOprCd = null;
		String slotOprCd = null;
		Timestamp startDttm = null;
		String startDttmTypeCd = null;
		Timestamp endDttm = null;
		String endDttmTypeCd = null;
		String oprCd = null;
		int custVersionNbr = 0;
		String custCd = null;
		String acctNbr = null;
		String contractNbr = null;
		double timeUnit = 0;
		double otherUnit = 0;
		try {
			log.info(
					"START: populateForCustomizeTariff  DAO processValueObjectPubl:" + processValueObjectPubl.toString()
							+ ",vesselRelatedValueObject:" + vesselRelatedValueObject.toString()
							+ ",berthRelatedValueObject:" + berthRelatedValueObject.toString()
							+ ",berthRelatedValueObject11:" + berthRelatedValueObject11.toString() + ",varDttm:"
							+ varDttm + ",tariffMainCat:" + tariffMainCat + ",tariffSubCat:" + tariffSubCat);
			// initialize the value object
			processValueObjectCust = new ProcessValueObject();
			processValueObjectComb = new ProcessValueObject();
			cntrEventLogValueObject = new CntrEventLogValueObject();

			// from the vessel records, get the basic details
			vvCd = vesselRelatedValueObject.getVvCd();
			billAcctNbr = vesselRelatedValueObject.getBillAcctNbr();

			if (billAcctNbr == null) {
				ex = new BusinessException("#Billable account nbr not found# for ~vv_cd (" + vvCd + ")");
				throw ex;
			}

			// retrieve the customer account details for further processing
			accountValueObject = processGenericRepo.retrieveCustAcct(null, billAcctNbr);

			if (accountValueObject == null)
				log.info("[CAB],account value object = " + accountValueObject + "...");

			// retrieve the customer contract
			contractValueObject = processGenericRepo.retrieveCustContractByAcctNbr(accountValueObject, varDttm);

			// contract exist, search for customize tariff
			if (contractValueObject.isModified(contractValueObject)) {
				// retrieve customer tariff version number
				custVersionNbr = contractValueObject.getCustomerTariffVersion();

				// retrieve the customized tariff, when customize version number is valid
				if (custVersionNbr != 0) {
					custCd = contractValueObject.getCustomerCode();
					acctNbr = contractValueObject.getAccountNumber();
					contractNbr = contractValueObject.getContractNumber();
					// amended by swho on 181202 to cater for GST change wef 1 Jan 2003
					// processValueObjectCust = retrieveTariffDtls(custVersionNbr, tariffMainCat,
					// tariffSubCat, custCd, acctNbr, contractNbr, vesselRelatedValueObject,
					// berthRelatedValueObject);
					log.info("[CAB],=====cUSTOMIZED TARIFF FOUND FOR " + custCd);
					// Determine Business Type //EVM Enhancements

					processValueObjectCust = retrieveTariffDtls(custVersionNbr, tariffMainCat, tariffSubCat, custCd,
							acctNbr, contractNbr, vesselRelatedValueObject, berthRelatedValueObject,
							berthRelatedValueObject11, varDttm);
					// end amend by swho
				} // end-if for customize version number = valid

			} // end-if for existence of contract

			// Combine both the publish and customize ProcessValueObject
			processValueObjectComb.setVersionNbrPubl(processValueObjectPubl.getVersionNbrPubl());
			processValueObjectComb.setVersionNbrCust(processValueObjectCust.getVersionNbrCust());
			processValueObjectComb.setCustCd(accountValueObject.getCustomerCode());
			processValueObjectComb.setAcctNbr(accountValueObject.getAccountNumber());
			processValueObjectComb.setContractNbr(null);
			processValueObjectComb.setCurrency(accountValueObject.getCurrency());
			processValueObjectComb.setCntrOprCd(cntrOprCd);
			processValueObjectComb.setSlotOprCd(slotOprCd);
			processValueObjectComb.setOprCd(oprCd);
			processValueObjectComb.setStartDttm(startDttm);
			processValueObjectComb.setStartDttmTypeCd(startDttmTypeCd);
			processValueObjectComb.setEndDttm(endDttm);
			processValueObjectComb.setEndDttmTypeCd(endDttmTypeCd);

			cntrEventLogValueObject.setDiscVvCd(vesselRelatedValueObject.getVvCd());
			processValueObjectComb.setCntrEventLogValueObject(cntrEventLogValueObject);

			// BY YJ 12 Jan 2004 for special dockage rate for Eng Lee & PN shipping lines of
			// $100 flat rate
			if (tariffSubCat.equals(ProcessChargeConst.TARIFF_SUB_MAR_SPECIAL_DOCKAGE)
					&& determineSpecialDockageRate(custCd)) {

				int SP_DOCKAGE_RATE = ConstantUtil.SP_DOCKAGE_RATE;
				if (processValueObjectPubl.isModified(processValueObjectPubl)) {
					tariffMainValueObject = processValueObjectPubl.getTariffMainValueObject();
					tariffContainerValueObject = processValueObjectPubl.getTariffContainerValueObject();

					for (xcnt = 0; xcnt < tariffMainValueObject.getTierCount(); xcnt++) {
						tariffTierValueObject = tariffMainValueObject.getTier(xcnt);
						if (xcnt == 0) {
							tariffTierValueObject.setRate(SP_DOCKAGE_RATE); // $100 regardless of duration
							tariffTierValueObject.setAdjustmentType("Z");
							tariffTierValueObject.setTimeUnit(1);
							tariffTierValueObject.setOtherUnit(1);
						} else {
							tariffTierValueObject.setRate(0);
							tariffTierValueObject.setTimeUnit(0);
							tariffTierValueObject.setOtherUnit(0);
						}
					}

					tariffMainValueObject.addContainer(tariffContainerValueObject);
					processValueObjectComb.addMain(tariffMainValueObject);
				}

				if (processValueObjectCust.isModified(processValueObjectCust)) {
					tariffMainValueObject = processValueObjectCust.getTariffMainValueObject();
					tariffContainerValueObject = processValueObjectCust.getTariffContainerValueObject();

					for (xcnt = 0; xcnt < tariffMainValueObject.getTierCount(); xcnt++) {
						tariffTierValueObject = tariffMainValueObject.getTier(xcnt);
						if (xcnt == 0) {
							tariffTierValueObject.setRate(SP_DOCKAGE_RATE); // $100 regardless of duration
							tariffTierValueObject.setAdjustmentType("Z");
							tariffTierValueObject.setTimeUnit(1);
							tariffTierValueObject.setOtherUnit(1);
						} else {
							tariffTierValueObject.setRate(0);
							tariffTierValueObject.setTimeUnit(0);
							tariffTierValueObject.setOtherUnit(0);
						}
					}
					tariffMainValueObject.addContainer(tariffContainerValueObject);
					processValueObjectComb.setContractNbr(processValueObjectCust.getContractNbr());

					processValueObjectComb.addMain(tariffMainValueObject);
				}
				return processValueObjectComb;
			}
			// End BY YJ 12 Jan 2004 for special dockage rate for Eng Lee & PN shipping
			// lines

			if (processValueObjectPubl.isModified(processValueObjectPubl)) {
				tariffMainValueObject = processValueObjectPubl.getTariffMainValueObject();
				tariffContainerValueObject = processValueObjectPubl.getTariffContainerValueObject();

				// loop thru the tariff tier records and set
				// TariffTierValueObject into TariffMainValueObject
				for (xcnt = 0; xcnt < tariffMainValueObject.getTierCount(); xcnt++) {
					// get the individual record of the tariff tier record
					tariffTierValueObject = tariffMainValueObject.getTier(xcnt);
					tariffTierValueObject.setTimeUnit(timeUnit);
					tariffTierValueObject.setOtherUnit(otherUnit);
				}
				tariffMainValueObject.addContainer(tariffContainerValueObject);

				processValueObjectComb.addMain(tariffMainValueObject);

			}

			if (processValueObjectCust.isModified(processValueObjectCust)) {
				tariffMainValueObject = processValueObjectCust.getTariffMainValueObject();
				tariffContainerValueObject = processValueObjectCust.getTariffContainerValueObject();

				// loop thru the tariff tier records and set
				// TariffTierValueObject into TariffMainValueObject
				for (xcnt = 0; xcnt < tariffMainValueObject.getTierCount(); xcnt++) {
					// get the individual record of the tariff tier record
					tariffTierValueObject = tariffMainValueObject.getTier(xcnt);
					tariffTierValueObject.setTimeUnit(timeUnit);
					tariffTierValueObject.setOtherUnit(otherUnit);
				}
				tariffMainValueObject.addContainer(tariffContainerValueObject);
				processValueObjectComb.setContractNbr(processValueObjectCust.getContractNbr());

				processValueObjectComb.addMain(tariffMainValueObject);
			}

		} catch (Exception e) {
			log.error("Exception: populateForCustomizeTariff ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: populateForCustomizeTariff  DAO Result:"
					+ (processValueObjectComb != null ? processValueObjectComb.toString() : ""));
		}
		return processValueObjectComb;
	}

	private boolean determineSpecialDockageRate(String custCd) {
		boolean result = false;
		try {
			log.info("START: determineSpecialDockageRate  DAO custCd:" + custCd);
			result = lineTowedVesselRepo.getDockageStatus(custCd);
		} catch (Exception e) {
			log.error("Exception: determineSpecialDockageRate ", e);
			result = false;
		} finally {
			log.info("END: determineSpecialDockageRate  DAO Result:" + result);
		}
		return result;
	}

	public ProcessValueObject determineTierUnits(ProcessValueObject processValueObject, int berthCnt, int vslLoa,
			int baCnt, long vesselStayPeriod, long overStayPeriod, String tariffSubCat, double lateArrivalPeriod,
			String cntrVslInd) throws BusinessException {
		TariffMainVO tariffMainValueObject = null;
		TariffTierVO tariffTierValueObject = null;
		int mainCnt = 0;
		int tierCnt = 0;
		double rangeFrom = 0;
		double rangeTo = 0;
		double perHour = 0;
		double perOther = 0;
		Long timeUnitLong = new Long(0);
		double timeUnit = 0;
		double otherUnit = 0;
		// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
		String perHourType = null;
		double roundUpUnit = 0;
		// end add by swho
		double vslDockageLength = 0;
		boolean determineSpecialDockageRate = false; // by YJ @ 14Jan 04 for line-towed barge
		double gbDockageMinHour = 1.0; // SL-CAB-20090216-01 minimum 1 hour for GB dockage
		long originalOverStayPeriod = overStayPeriod; // lsw 20090917 stores original value SL-CAB-20090923-01
		try {
			log.info("START: determineTierUnits  DAO processValueObject:" + processValueObject.toString() + ",berthCnt:"
					+ berthCnt + ",vslLoa:" + vslLoa + ",baCnt:" + baCnt + ",vesselStayPeriod:" + vesselStayPeriod
					+ ",overStayPeriod:" + overStayPeriod + ",tariffSubCat:" + tariffSubCat + ",lateArrivalPeriod:"
					+ lateArrivalPeriod + ",cntrVslInd:" + cntrVslInd);

			// added by YJ @ 14Jan04 for line-towed barge
			determineSpecialDockageRate = determineSpecialDockageRate(processValueObject.getCustCd());

			// loop thru the tariff main records
			// for both published and customized (if any)
			for (mainCnt = 0; mainCnt < processValueObject.getMainCount(); mainCnt++) {
				tariffMainValueObject = processValueObject.getMain(mainCnt);
				// Added by Ruchika: 29-Aug-2007
				tariffMainValueObject.setMainCategory(ProcessChargeConst.TARIFF_MAIN_MARINE);
				tariffMainValueObject.setSubCategory(tariffSubCat);
				// End of addition by Ruchika
				log.info("[CAB],tariff cd = " + tariffMainValueObject.getCode() + "...sub cat = "
						+ tariffMainValueObject.getSubCategory() + " ....");
				// loop thru the tariff tier records
				for (tierCnt = 0; tierCnt < tariffMainValueObject.getTierCount(); tierCnt++) {
					// get the individual record of the tariff tier record
					tariffTierValueObject = tariffMainValueObject.getTier(tierCnt);

					rangeFrom = tariffTierValueObject.getRangeFrom();
					log.info("[CAB],range from = " + rangeFrom + "...");
					rangeTo = tariffTierValueObject.getRangeTo();
					log.info("[CAB],range to = " + rangeTo + "...");
					perHour = tariffTierValueObject.getPerHour();
					if (perHour < 0) {
						perHour = 1;
					}
					log.info("[CAB],per hour = " + perHour + "...");
					perOther = tariffTierValueObject.getPerUnit();
					if (perOther < 0) {
						perOther = 1;
					}
					log.info("[CAB],per other = " + perOther + "...");

					perHourType = tariffTierValueObject.getPerHourType();
					log.info("[CAB],per hour type = " + perHourType + "...");
					log.info("[CAB],rate = " + tariffTierValueObject.getRate() + "...");

					if (tariffSubCat.equals(subCatArray[0])) {
						// amended by swho on 22/04/2002 for extraction of GBMS vessels for marine
						// charges
						// timeUnit = vesselStayPeriod/perHour;
						roundUpUnit = determineTimeRoundUpUnit(perHour, perHourType);
						if (cntrVslInd.equals(ProcessChargeConst.CONTAINER_VSL)) {
							timeUnit = Math.ceil(vesselStayPeriod / roundUpUnit);
						} else {
							timeUnitLong = new Long(Math.round(vesselStayPeriod / roundUpUnit));
							timeUnit = timeUnitLong.doubleValue();
							// SL-CAB-20090216-01 change to 1 hour if timeUnit is less than that 23 Feb 2009
							// sauwoon
							if (Double.compare(timeUnit, gbDockageMinHour) < 0) // timeUnit is less than 1 hour
								timeUnit = gbDockageMinHour; // default timeUnit to 1 hour
						}
						// end amend by swho
						if (tierCnt == 0) {
							otherUnit = rangeTo / perOther;
							vslDockageLength = rangeTo;
						} else if (tierCnt > 0) {
							if (vslLoa <= vslDockageLength) {
								otherUnit = 0;
							} else {
								otherUnit = (vslLoa - rangeFrom) / perOther;
							}
						}
					} else if (tariffSubCat.equals(subCatArray[1])) {
						// amended by swho on 22/04/2002 for extraction of GBMS vessels for marine
						// charges
						// timeUnit = overStayPeriod/perHour;
						roundUpUnit = determineTimeRoundUpUnit(perHour, perHourType);
						timeUnit = Math.ceil(overStayPeriod / roundUpUnit);
						// end amend by swho
						otherUnit = vslLoa / perOther;
					} else if (tariffSubCat.equals(subCatArray[2])) {
						timeUnit = 0 / perHour;
						otherUnit = berthCnt / perOther;
					} else if (tariffSubCat.equals(subCatArray[3])) {
						timeUnit = 0 / perHour;
						otherUnit = baCnt / perOther;
					}
					// added by swho on 22/04/2002 for extraction of GBMS vessels for marine charges
					else if (tariffSubCat.equals(subCatArray[4])) {
						timeUnit = 0 / perHour;
						otherUnit = baCnt / perOther;
					}
					// end add by swho
					// Added on 07/05/2002 by Ai Lin - For Late Arrival
					else if (tariffSubCat.equals(subCatArray[5])) {
						otherUnit = 0;

						if (tierCnt == 0) {
							if (lateArrivalPeriod == 0) {
								timeUnit = 0;
							} else {
								timeUnit = 1;
							}
						} else if (tierCnt > 0) {
							if (lateArrivalPeriod <= rangeFrom) {
								timeUnit = 0;
							} else {
								timeUnit = (lateArrivalPeriod - rangeFrom) / perHour;
								BigDecimal timeUnitBD = new BigDecimal(timeUnit).setScale(0, BigDecimal.ROUND_UP);
								timeUnit = timeUnitBD.doubleValue();
							}
						}

						log.info("[CAB],[ProcessMarEJB.detaermineTierUnits] lateArrivalPeriod = " + lateArrivalPeriod);
						log.info("[CAB],[ProcessMarEJB.detaermineTierUnits] tier = " + tierCnt);
						log.info("[CAB],[ProcessMarEJB.detaermineTierUnits] rangeFrom = " + rangeFrom);
						log.info("[CAB],[ProcessMarEJB.detaermineTierUnits] rangeTo = " + rangeTo);
						log.info("[CAB],[ProcessMarEJB.detaermineTierUnits] per # min = " + perHour);
						log.info("[CAB],[ProcessMarEJB.detaermineTierUnits] timeUnit = " + timeUnit);
					}
					// End added on 07/05/2002 by Ai Lin - For Late Arrival

					// Added by YJ on 12Jan04 for special dockage for linetowed barge
					// Added OR condition for Line Tow Barge Sripriya Combi Enhancement May 2018
					else if (tariffSubCat.equals(subCatArray[6]) || (tariffSubCat.equals(subCatArray[8]))) {
						// End OR condition for Line Tow Barge Sripriya Combi Enhancement May 2018

						roundUpUnit = determineTimeRoundUpUnit(perHour, perHourType);
						timeUnitLong = new Long(Math.round(vesselStayPeriod / roundUpUnit));
						timeUnit = timeUnitLong.doubleValue();
						if (determineSpecialDockageRate) // for customer paying the flat rate of 100 regardless of
															// duration
						{
							if (tierCnt == 0 && vesselStayPeriod > 0) {
								otherUnit = 1;
								timeUnit = 1;
							} else {
								otherUnit = 0;
								timeUnit = 0;
							}
						} else {
							if (tierCnt == 0) {
								otherUnit = rangeTo / perOther;
								vslDockageLength = rangeTo;
							} else if (tierCnt > 0) {
								if (vslLoa <= vslDockageLength) {
									otherUnit = 0;
								} else {
									otherUnit = (vslLoa - rangeFrom) / perOther;
								}
							}
						}
					}
					// End Added by YJ on 12Jan04 for special dockage for linetowed barge

					// Added by Ruchika on 16-Aug-07 for new overstay dockage sub-cat
					else if (tariffSubCat.equals(subCatArray[7])) {
						roundUpUnit = determineTimeRoundUpUnit(perHour, perHourType);
						double newRoundUpUnit = (((rangeTo - rangeFrom) / perHour) * roundUpUnit);
						otherUnit = vslLoa / perOther;
						log.info("[CAB],[ProcessMarEJB.determineTierUnits.subCatArray[7]] overStayPeriod at start = "
								+ overStayPeriod);
						BigDecimal tbd = null;

						// lsw 20090917 SL-CAB-20090923-01
						// this is a fix. When overstay dockage has customised tariff, there will be 2
						// sets of tariffMainValueObject in the processValueObject
						// parameter passed instead of one (not that determineTierUnits is run twice!).
						// overstay dockage tariff 3 tiers, and the below logic is supposed to determine
						// how many units (where each unit is 15min) to bill for each tier
						// tier 1 (tierCnt 0) is billing from 0 - 2 hrs, then 2-4 hrs, then 4 - infinite
						// hours
						// after calculating tier 1, the logic below will minus away the 2 hrs billed to
						// tier 1,
						// and determine how many leftover time to bill for tier 2. Similarly for tier 3
						// however, when there are 2 sets of tariffMainValueObject, after going through
						// the first set (for published),
						// overStayPeriod will be minus off unti there is 0 units left. So when the
						// logic process through for tier 1
						// of customised tariff, it will determine that amount of time billed for
						// customised tariff tier 1 is 0 hour
						// which is wrong! So what the below if statement does is to pass back the
						// correct OverStayPeriod into the variable
						// for calculation when it encounters 2nd set of TariffMainValueObject
						// this prob did not happen for the rest of the marine tariffs as none of them
						// has multiple tier
						// that bill according to ranges while MA01 (normal dockage) already has its
						// logic for the 2 tiers built in

						if (tierCnt == 0 && overStayPeriod != originalOverStayPeriod) {
							overStayPeriod = originalOverStayPeriod;
						}

						if (overStayPeriod < newRoundUpUnit) {
							timeUnit = Math.ceil(overStayPeriod / roundUpUnit);
							tbd = new BigDecimal(timeUnit).setScale(0, BigDecimal.ROUND_UP);
							timeUnit = tbd.doubleValue();
							overStayPeriod = 0;
						} else {
							timeUnit = newRoundUpUnit / roundUpUnit;
							tbd = new BigDecimal(timeUnit).setScale(0, BigDecimal.ROUND_UP);
							timeUnit = tbd.doubleValue();
							overStayPeriod -= newRoundUpUnit;
						}
						/*
						 * if (tierCnt == 0) { if (overStayPeriod == 0) { timeUnit = 0; } else {
						 * timeUnit = 1; } } else if (tierCnt > 0) { if (overStayPeriod <= rangeFrom) {
						 * timeUnit = 0; } else { timeUnit = (overStayPeriod - rangeFrom)/roundUpUnit;
						 * BigDecimal timeUnitBD = new
						 * BigDecimal(timeUnit).setScale(0,BigDecimal.ROUND_UP); timeUnit =
						 * timeUnitBD.doubleValue(); } }
						 */
						log.info("[CAB],[ProcessMarEJB.determineTierUnits.subCatArray[7]] overStayPeriod = "
								+ overStayPeriod);
						log.info("[CAB],[ProcessMarEJB.determineTierUnits.subCatArray[7]] tier = " + tierCnt);
						log.info("[CAB],[ProcessMarEJB.determineTierUnits.subCatArray[7]] rangeFrom = " + rangeFrom);
						log.info("[CAB],[ProcessMarEJB.determineTierUnits.subCatArray[7]] rangeTo = " + rangeTo);
						log.info("[CAB],[ProcessMarEJB.determineTierUnits.subCatArray[7]] timeUnit = " + timeUnit);
						log.info("[CAB],[ProcessMarEJB.determineTierUnits.subCatArray[7]] otherUnit = " + otherUnit);
					}
					// End of change by Ruchika for new sub-cat

					tariffTierValueObject.setTimeUnit(timeUnit);
					log.info("[CAB],time unit = " + timeUnit + "...");
					tariffTierValueObject.setOtherUnit(otherUnit);
					log.info("[CAB],other unit = " + otherUnit + "...");
				}
			}
		} catch (Exception e) {
			log.error("Exception: determineTierUnits ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: determineTierUnits  DAO Result:"
					+ (processValueObject != null ? processValueObject.toString() : ""));
		}
		return processValueObject;
	}

	private double determineTimeRoundUpUnit(double perHour, String perHourType) throws BusinessException {
		double roundUpUnit = 0;
		try {
			log.info("START: determineTimeRoundUpUnit  DAO perHour:" + perHour + ",perHourType:" + perHourType);
			if (perHourType.equals(ProcessChargeConst.TARIFF_HOUR_RATE_HOUR)) {
				roundUpUnit = perHour * 60 * 60 * 1000;
			} else if (perHourType.equals(ProcessChargeConst.TARIFF_HOUR_RATE_MIN)) {
				roundUpUnit = perHour * 60 * 1000;
			}
		} catch (Exception e) {
			log.error("Exception: determineTimeRoundUpUnit ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: determineTimeRoundUpUnit  DAO Result:" + roundUpUnit);
		}
		return roundUpUnit;
	}

	private long retrieveOverStayPeriodByWaiverType(String waiveType, long billableDurationValue, long overStayPeriod,
			String isRejected) throws BusinessException {
		try {
			log.info("START: retrieveOverStayPeriodByWaiverType  DAO waiveType:" + waiveType + ",billableDurationValue:"
					+ billableDurationValue + ",overStayPeriod:" + overStayPeriod + ",isRejected:" + isRejected);
			if (waiveType != null) {
				// waive Type is partial
				if (!waiveType.equals("F")) {
					// Approved case
					if (isRejected != null && isRejected.equals("N")) {
						// bill the amount with the newer amount
						overStayPeriod = (billableDurationValue * 60 * 1000);
					}
				}
			}
		} catch (Exception e) {
			log.error("Exception: retrieveOverStayPeriodByWaiverType ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveOverStayPeriodByWaiverType  DAO Result:" + overStayPeriod);
		}
		return overStayPeriod;
	}

	private long retrieveOverStayPeriodByWaiverType(String waiveType, long billableDurationValue, long overStayPeriod)
			throws BusinessException {
		try {
			log.info("START: retrieveOverStayPeriodByWaiverType  DAO waiveType:" + waiveType + ",billableDurationValue:"
					+ billableDurationValue + ",overStayPeriod:" + overStayPeriod);
			if (waiveType != null) {
				// waive Type is partial
				if (!waiveType.equals("F")) {
					// bill the amount with the newer amount
					overStayPeriod = (billableDurationValue * 60 * 1000);
				}
			}
		} catch (Exception e) {
			log.error("Exception: retrieveOverStayPeriodByWaiverType ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveOverStayPeriodByWaiverType  DAO Result:" + overStayPeriod);
		}
		return overStayPeriod;
	}
	
	//CH-7 changes starts
	private long retrieveExemptionDuration(String exemptionType, String vvCode) throws BusinessException {		
		SqlRowSet rs = null;
		StringBuffer sb = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		long exemptionDuration = 0;
		try {
			log.info("START: retrieveExemptionDuration  DAO vvCode: " + vvCode + " exemptionType: "+ exemptionType);

			sb.append("select sum(exemption_mins) as exemption_mins from osd_review_exemption e ");
			sb.append("join osd_review r on e.osd_review_id = r.osd_review_id ");
			sb.append("where exemption_type = :exemptionType and r.vv_cd = :vvCode ");
			String sql = sb.toString();

			paramMap.put("vvCode", vvCode);
			paramMap.put("exemptionType", exemptionType);
			log.info(" *** retrieveExemptionDuration SQL *****" + sql + " paramMap " + paramMap.toString());
			rs = namedParameterJdbcTemplate.queryForRowSet(sql, paramMap);

			if (rs.next()) {
				exemptionDuration = rs.getLong("exemption_mins") * 60 * 1000;
			}
			log.info(" *** retrieveExemptionDuration Result *****" + exemptionDuration);
		} catch (NullPointerException e) {
			log.error("Exception: retrieveExemptionDuration ", e);
			throw new BusinessException("M4201");
		} catch (Exception e) {
			log.error("Exception: retrieveExemptionDuration ", e);
			throw new BusinessException("M4201");
		} finally {
			log.info("END: retrieveExemptionDuration  DAO  END");
		}
		return exemptionDuration;
	}
	//CH-7 changes ends
}
