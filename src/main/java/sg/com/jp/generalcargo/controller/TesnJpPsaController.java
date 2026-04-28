package sg.com.jp.generalcargo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.JPPSAValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TesnValueObject;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.domain.gbmsFileManager;
import sg.com.jp.generalcargo.service.TesnJpPsaService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = TesnJpPsaController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TesnJpPsaController {

	public static final String ENDPOINT = "gc/renominateCargo/tesn";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(TesnJpPsaController.class);

	@Autowired
	private TesnJpPsaService renomTesnJpPsaService;

	@Autowired
	private gbmsFileManager gbmsfile;

	// delegate.helper.gbms.cargo.tesn.tesnJpPsa-->TesnJpPsaHandler
	@PostMapping(value = "/TesnJpPsaList")
	public ResponseEntity<?> tesnJpPsaList(HttpServletRequest request) throws BusinessException {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			Criteria criteria = CommonUtil.getCriteria(request);

			errorMessage = null;
			log.info("** tesnJpPsaList Start criteria :" + criteria.toString());

			String varNo = "";
			String s = "";
			String s1 = "";
			int i = 0;
			double d = 0.0D;
			double d1 = 0.0D;

			// String s2 =
			// CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String s3 = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			/* >> Add by FPT.VietNguyen - Apr 16 2014 DPE */
			boolean isFetchmode = false;
			String fetchmode = (java.lang.String) criteria.getPredicates().get("fetchmode");
			isFetchmode = "TRUE".equalsIgnoreCase(fetchmode) ? true : false;
			String fetchVesselName = null;
			String fetchVoyageNbr = null;

			String isFetch = "FALSE";
			if (isFetchmode) {
				if (criteria.getPredicates().get("vesselName") != null
						&& criteria.getPredicates().get("voyageNbr") != null) {
					fetchVesselName = criteria.getPredicates().get("vesselName").toUpperCase();
					fetchVoyageNbr = criteria.getPredicates().get("voyageNbr").toUpperCase();
					map.put("fetchVesselName", fetchVesselName);
					map.put("fetchVoyageNbr", fetchVoyageNbr);
					isFetch = "TRUE";
					map.put("isFetchmode", isFetch);
				} else {
					log.info("Invalid vessel voyage values.");
					errorMessage = ConstantUtil.ErrorMsg_Invalid_Voy;
				}
			}

			try {
				s = criteria.getPredicates().get("vslName");

				 varNo = criteria.getPredicates().get("varNo");
				s1 = criteria.getPredicates().get("vslnew");
			} catch (Exception e) {
				log.info("Exception perform : ", e);
				throw new BusinessException("M4201");
			}

			List<String> arraylist4 = new ArrayList<String>();
			List<String> arraylist5 = new ArrayList<String>();
			List<String> arraylist6 = new ArrayList<String>();
			List<String> arraylist7 = new ArrayList<String>();
			List<String> arraylist8 = new ArrayList<String>();
			List<String> arraylist9 = new ArrayList<String>();
			List<String> arraylist10 = new ArrayList<String>();
			List<String> arraylist12 = new ArrayList<String>();
			List<String> arraylist13 = new ArrayList<String>();
			List<String> arraylist14 = new ArrayList<String>();
			List<String> schemeList = new ArrayList<String>();
			List<String> subSchemeList = new ArrayList<String>();
			List<String> gcOperationsList = new ArrayList<String>();
			List<String> terminalList = new ArrayList<String>();

			List<VesselVoyValueObject> arraylist11 = renomTesnJpPsaService.getVesselVoy(s3);

			TopsModel topsmodel = new TopsModel();
			for (int j = 0; j < arraylist11.size(); j++) {
				VesselVoyValueObject vesselVoyValueObject = new VesselVoyValueObject();
				vesselVoyValueObject = (VesselVoyValueObject) arraylist11.get(j);
				topsmodel.put(vesselVoyValueObject);
			}
			/* >> Add by FPT.VietNguyen - Apr 16 2014 DPE */
			if (isFetchmode) {
				VesselVoyValueObject vslObj1 = new VesselVoyValueObject();
				vslObj1 = renomTesnJpPsaService.getVessel(fetchVesselName, fetchVoyageNbr, s3);
				if (null != vslObj1) {
					s = vslObj1.getVarNbr(); // -- Reset the selVoyno
				} else {
					log.info("Invalid vessel voyage values.");
					errorMessage = ConstantUtil.ErrorMsg_Invalid_Voy;
				}
			}

			map.put("selVoyno", s);
			map.put("vslnew", s1);
			map.put("varNo", varNo);
			// End

			// VietNguyen (FPT) Document Process Enhancement 07-Jan-2014 : START
			List<String> truckerNmList = new ArrayList<String>();
			List<String> createdByList = new ArrayList<String>();
			List<String> lastModifyDttmList = new ArrayList<String>();
			// VietNguyen (FPT) Document Process Enhancement 07-Jan-2014 : END

	
			if (s != null && !s.equals("")) {
				TableResult tableResult = renomTesnJpPsaService.getTesnJpPsaList(s, s3, criteria);
				List<TesnValueObject> arraylist1 = tableResult.getData().getListData().getTopsModel();
				for (int k1 = 0; k1 < arraylist1.size(); k1++) {
					TesnValueObject tesnValueObject1 = new TesnValueObject();
					tesnValueObject1 = (TesnValueObject) arraylist1.get(k1);

					arraylist4.add(tesnValueObject1.getEsnAsnNbr());
					arraylist5.add(tesnValueObject1.getVslNm());
					arraylist6.add(tesnValueObject1.getInVoyNbr());
					arraylist7.add(tesnValueObject1.getNbrPkgs());
					arraylist8.add(tesnValueObject1.getCrgDes());
					arraylist9.add(tesnValueObject1.getEdoAsnNbr());
					arraylist10.add(tesnValueObject1.getInVarNbr());
					arraylist12.add(tesnValueObject1.getInVslNm());
					// START - Added nom weight and vol to display in listing table - NS MAY 2023
					arraylist13.add(tesnValueObject1.getNomWt());
					arraylist14.add(tesnValueObject1.getNomVol());
					// END - Added nom weight and vol to display in listing table - NS MAY 2023
					// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014 : START
					truckerNmList.add(tesnValueObject1.getTruckerNm());
					createdByList.add(tesnValueObject1.getCreatedBy());
					lastModifyDttmList.add(tesnValueObject1.getLastModifyDttm());
					// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014 : END
					schemeList.add(tesnValueObject1.getScheme());
					subSchemeList.add(tesnValueObject1.getSubScheme());
					gcOperationsList.add(tesnValueObject1.getGcOperations());
					terminalList.add(tesnValueObject1.getTerminal());
				}
				// log.info("flag " + flag);
				map.put("total", tableResult.getData().getTotal());
				map.put("esnAsnNbr", arraylist4);
				map.put("vslNm", arraylist5);
				map.put("inVoyNbr", arraylist6);
				map.put("nbrPkgs", arraylist7);
				// START - Added nom weight and vol to display in listing table - NS MAY 2023
				map.put("nomwt", arraylist13);
				map.put("nomvol", arraylist14);
				// END - Added nom weight and vol to display in listing table - NS MAY 2023
				map.put("crgDes", arraylist8);
				map.put("edoAsn", arraylist9);
				map.put("inVarNm", arraylist10);
				map.put("inVesNm", arraylist12);
				// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014 : START
				map.put("truckerNmList", truckerNmList);
				map.put("createdByList", createdByList);
				map.put("lastModifyDttmList", lastModifyDttmList);
				// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014 : END
				map.put("terminalList", terminalList);
				map.put("schemeList", schemeList);
				map.put("subSchemeList", subSchemeList);
				map.put("gcOperationsList", gcOperationsList);
				map.put("usrtyp", s3);
				map.put("tnop", "" + i);
				map.put("tgwt", "" + d);
				map.put("tvol", "" + d1);
			}

			tesnJpPsa(topsmodel, map);
		} catch (BusinessException be) {
			log.info("Exception tesnJpPsaList: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception tesnJpPsaList : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: tesnJpPsaList result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	private void tesnJpPsa(TopsModel topsmodel, Map<String, Object> map) {
		log.info("START: tesnJpPsa "+" topsmodel:"+topsmodel +" map:"+map);
		List<String> arraylist = new ArrayList<String>();
		List<String> arraylist1 = new ArrayList<String>();
		List<String> arraylist2 = new ArrayList<String>();
		List<String> terminalList = new ArrayList<String>();
		List<VesselVoyValueObject> vesselVoyValueObject = new ArrayList<VesselVoyValueObject>();
		
		for (int i = 0; i < topsmodel.getSize(); i++) {
			VesselVoyValueObject vesselvoyvalueobject1 = new VesselVoyValueObject();
			String s = "";
			String s1 = "";
			String s2 = "";
			String terminal = "";
			vesselvoyvalueobject1 = (VesselVoyValueObject) topsmodel.get(i);
			s = vesselvoyvalueobject1.getVslName();
			s1 = vesselvoyvalueobject1.getVoyNo();
			s2 = vesselvoyvalueobject1.getVarNbr();
			terminal = vesselvoyvalueobject1.getTerminal();
			arraylist.add(s);
			arraylist2.add(s1);
			arraylist1.add(s2);
			terminalList.add(terminal);
			vesselVoyValueObject.add(vesselvoyvalueobject1);
		}
		map.put("VslName", arraylist);
		map.put("VoyNo", arraylist2);
		map.put("VarNo", arraylist1);
		map.put("terminal", terminalList);
		map.put("vesselVoyValueObject",vesselVoyValueObject);

	}

	// delegate.helper.gbms.cargo.tesn.tesnJpPsa-->TesnJpPsaViewHandler
	@PostMapping(value = "/TesnJpPsaView")
	public ResponseEntity<?> TesnJpPsaView (HttpServletRequest request) throws BusinessException {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			Criteria criteria = CommonUtil.getCriteria(request);

			errorMessage = null;
			log.info("** tesnJpPsaView Start criteria :" + criteria.toString());

			String resButton = criteria.getPredicates().get("resButton");
			// String voyNbr = criteria.getPredicates().get("voyNo");
//				      log.info("Res Button : "+resButton);

			// String assignCargo = "";
			String crno = "";
			String secCarves = "";
			String portDis = "";
			String secCarvoy = "";
			String shipper = "";
			String noOfPkgs = "";
			int noOfPkgsInt = 0;
			String EsnAsnNbr = "";
			String InVoyNbr = "";
			String crgDes = "";
			String vslNm = "";
			String sysdatestr = "";
			String bkref = "";
			String acctno = "";
			String blno = "";
			String category = "00";
			// START CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
			String nomWt = "";
			String nomVol = "";
			// END CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
			if (!StringUtils.isEmpty(criteria.getPredicates().get("category"))) {
				category = criteria.getPredicates().get("category");
			}
			;

			if (resButton.equalsIgnoreCase("List")) {

				// assignCargo = criteria.getPredicates().get("assignCrg");
				// log.info("Res Button : assignCargo"+assignCargo);
				EsnAsnNbr = criteria.getPredicates().get("EsnAsnNbr");
				InVoyNbr = criteria.getPredicates().get("InVoyNbr");
				vslNm = criteria.getPredicates().get("vslNm");
				crgDes = criteria.getPredicates().get("crgDes");
			} else {
				secCarves = criteria.getPredicates().get("secCarves");
				portDis = criteria.getPredicates().get("portdisc");
				secCarvoy = criteria.getPredicates().get("secCarvoy");
				shipper = criteria.getPredicates().get("shipper");
				noOfPkgs = criteria.getPredicates().get("noOfPkgs");
				// EsnAsnNbr = criteria.getPredicates().get("EsnAsnNbr");
				bkref = criteria.getPredicates().get("bkref");
				acctno = criteria.getPredicates().get("acctNbr");
				log.info("this is teh account nbr" + acctno);

				noOfPkgsInt = Integer.parseInt(noOfPkgs);
				
				// START CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
				nomWt = CommonUtility.deNull(criteria.getPredicates().get("nom_wt"));
				nomVol = CommonUtility.deNull(criteria.getPredicates().get("nom_vol"));
				// END CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
			}

			String s65 = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String s66 = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			map.put("custCd", s66);
			List<Map<String, Object>> list = renomTesnJpPsaService.getCategoryList();
			map.put("categoryList", list);
			map.put("crgDes", crgDes);
			
			List<TesnValueObject> arraylist1 = new ArrayList<TesnValueObject>();
			try {
				secCarves = criteria.getPredicates().get("secCarves");
			} catch (Exception e) {
				log.info("Exception perform : ", e);
				throw new BusinessException("M4201");
			}

			if (resButton.equalsIgnoreCase("Add")) {
				java.lang.String AsnNo = criteria.getPredicates().get("AsnNo");
				log.info("AsnNo : " + AsnNo);
				// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014: START
				boolean checkSecondCarrierVsl = renomTesnJpPsaService.chkSecondCarrierVsl(secCarves, secCarvoy);
				if (!checkSecondCarrierVsl) {
					errorMessage = ConstantUtil.ErrorMsg_Voy_Vessel_Invalid;

					map.put("error", errorMessage);
					result = new Result();
					result.setError(errorMessage);
					result.setSuccess(false);
					result.setData(map);
					return ResponseEntityUtil.success(result.toString());
				}
				if (!"JP".equalsIgnoreCase(s66)) {
					boolean checkAtuSecondCarrierVsl = renomTesnJpPsaService.chkDttmOfSecondCarrierVsl(secCarves,
							secCarvoy);
					if (!checkAtuSecondCarrierVsl) {
						errorMessage = ConstantUtil.ErrorMsg_Unable_Create_After_ATU;

						map.put("error", errorMessage);
						result = new Result();
						result.setError(errorMessage);
						result.setSuccess(false);
						result.setData(map);
						return ResponseEntityUtil.success(result.toString());
					}
				}
				// START CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
				arraylist1 = renomTesnJpPsaService.addRecordForDPE(s65, portDis, secCarves, secCarvoy, noOfPkgsInt,
						shipper, AsnNo, bkref, acctno, category, nomWt, nomVol);
				// END CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
				// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014: END

				String k = (String) renomTesnJpPsaService.createNomVesselJPPsa(secCarves, secCarvoy, s65);
				// TesnValueObject tesnvalueobject = arraylist1.get(0);
				blno = renomTesnJpPsaService.getBLNo(criteria.getPredicates().get("edoNbr"));
				crno = renomTesnJpPsaService.getTdbCrno(criteria.getPredicates().get("edoNbr"));
				sysdatestr = renomTesnJpPsaService.getSysdate();
				if (category != null && !category.equals("")) {
					String categoryValue = renomTesnJpPsaService.getCategoryValue(category);
					map.put("categoryValue", categoryValue);
				}

			}
			if (resButton.equalsIgnoreCase("List")) {
				log.info("EsnAsnNbr : " + EsnAsnNbr);
				log.info("InVoyNbr : " + InVoyNbr);
				log.info("vslNm : " + vslNm);
				arraylist1 = renomTesnJpPsaService.getDisplayCargoDetails(EsnAsnNbr, InVoyNbr, vslNm);
				log.info("arraylist1 : " + arraylist1.size());

			}
			TopsModel topsmodel = new TopsModel();
			log.info("Sixe aray " + arraylist1.size());
			for (int j = 0; j < arraylist1.size(); j++) {
				TesnValueObject tesnValueObject = new TesnValueObject();
				tesnValueObject = (TesnValueObject) arraylist1.get(j);
				map.put("bkref", tesnValueObject.getBkRef());
				map.put("acctNbr", tesnValueObject.getAccount());
				if (resButton.equalsIgnoreCase("List")) {
					map.put("category", tesnValueObject.getCategory());
					map.put("categoryValue", tesnValueObject.getCategoryValue());
				}
				topsmodel.put(tesnValueObject);
				if (resButton.equalsIgnoreCase("Add")) {
					JPPSAValueObject jpvalobj = new JPPSAValueObject();
					JPPSAValueObject jpvalobj1 = new JPPSAValueObject();

					jpvalobj.setFileName(" ");

					jpvalobj.setICHRecType("H");
					jpvalobj.setICHCreateDate(sysdatestr); //
					jpvalobj.setICHAbbVslName(criteria.getPredicates().get("vslNmStr"));
					jpvalobj.setICHDisAbbVslName(criteria.getPredicates().get("inVoyNbrStr"));
					jpvalobj1.setICDRecordType("D");
					jpvalobj1.setICDFunction("N");
					jpvalobj1.setICDBillofLading(blno);
					jpvalobj1.setICDHScode(criteria.getPredicates().get("hsCodeStr"));
					jpvalobj1.setICDPackageType(criteria.getPredicates().get("pkgTypeStr"));
					jpvalobj1.setICDNoofPackage(noOfPkgs);
					jpvalobj1.setICDWeight(formatValues(tesnValueObject.getTesnNomWt()));
					jpvalobj1.setICDVolume(formatValues(tesnValueObject.getTesnNomVol()));
					jpvalobj1.setICDDGIndicator(criteria.getPredicates().get("dgIndStr"));
					jpvalobj1.setICDShipperName(shipper);
					String cargoType = CommonUtil.deNull(criteria.getPredicates().get("cargoTypeStr"));
					if (cargoType.length() > 1)
						cargoType = cargoType.charAt(1) + "";

					jpvalobj1.setICDCargoType(cargoType);
					jpvalobj1.setICDLoadingVessel(secCarves);
					jpvalobj1.setICDLoadingVoyage(secCarvoy);
					jpvalobj1.setICDPortOfDischarge(portDis);
					jpvalobj1.setICDContainerNumber(CommonUtility.deNull(criteria.getPredicates().get("cntrSizeStr")));
					jpvalobj1.setICDDirectInterGateWay("JP");
					jpvalobj1.setICDBookingRef(bkref);
					jpvalobj1.setICDAccount(acctno);
					jpvalobj1.setICDTdbNo(crno);
					jpvalobj1.setICDCargoDescription(criteria.getPredicates().get("crgDesStr"));

					String marking = criteria.getPredicates().get("mftMarkingsStr");
					if (marking == null)
						marking = "NIL";
					if (marking != null && marking.equals(""))
						marking = "NIL";

					jpvalobj1.setICDMarking(marking);
					jpvalobj.setICSRecordtype("S");
					jpvalobj.setICSTotalRec("1");
					List<JPPSAValueObject> v = new ArrayList<JPPSAValueObject>();
					v.add(jpvalobj1);
					jpvalobj.setCargoDetails(v);
					gbmsfile.insertIGD(jpvalobj, criteria);
					gbmsfile.writeJPPSA(criteria.getPredicates().get("inVoyNbrStr"), "N");
				}

			}

		
			TesnJpPsaView(criteria, topsmodel, map);
		} catch (BusinessException be) {
			log.info("Exception tesnJpPsaView: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception tesnJpPsaView : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: tesnJpPsaView result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	private void TesnJpPsaView(Criteria criteria, TopsModel topsmodel, Map<String, Object> map) throws BusinessException {
		String resButton = criteria.getPredicates().get("resButton");

		String s1 = "";
		String s2 = "";
		String s3 = "";
		String s4 = "";
		String s5 = "";
		String s6 = "";
		String s7 = "";
		String s8 = "";
		String s9 = "";
		String s10 = "";
		String s11 = "";
		String s12 = "";
		String s13 = "";
		String s14 = "";
		String s15 = "";
		String s16 = "";
		String s17 = "";
		String s18 = "";
		String s19 = "";
		String s20 = "";
		String s21 = "";
		String s22 = "";
		String s23 = "";
		String s24 = "";
		String s25 = "";
		String s26 = "";
		String s27 = "";
		String s28 = "";
		String s29 = "";
		String s30 = "";
		String s31 = "";
		String s32 = "";
		String s33 = "";
		String s34 = "";
		String s35 = "";
		String s36 = "";
		String s37 = "";
		String s38 = "";

		List<String> arraylist1 = new ArrayList<String>();
		List<String> arraylist2 = new ArrayList<String>();
		List<String> arraylist3 = new ArrayList<String>();
		List<String> arraylist4 = new ArrayList<String>();
		List<String> arraylist5 = new ArrayList<String>();
		List<String> arraylist6 = new ArrayList<String>();
		List<String> arraylist7 = new ArrayList<String>();
		List<String> arraylist8 = new ArrayList<String>();
		List<String> arraylist9 = new ArrayList<String>();
		List<String> arraylist10 = new ArrayList<String>();
		List<String> arraylist11 = new ArrayList<String>();
		List<String> arraylist12 = new ArrayList<String>();
		List<String> arraylist13 = new ArrayList<String>();
		List<String> arraylist14 = new ArrayList<String>();
		List<String> arraylist15 = new ArrayList<String>();
		List<String> arraylist16 = new ArrayList<String>();
		List<String> arraylist17 = new ArrayList<String>();
		List<String> arraylist18 = new ArrayList<String>();
		List<String> arraylist19 = new ArrayList<String>();
		List<String> arraylist20 = new ArrayList<String>();
		List<String> arraylist21 = new ArrayList<String>();
		List<String> arraylist22 = new ArrayList<String>();
		List<String> arraylist23 = new ArrayList<String>();
		List<String> arraylist24 = new ArrayList<String>();
		List<String> arraylist25 = new ArrayList<String>();
		List<String> arraylist26 = new ArrayList<String>();
		List<String> arraylist27 = new ArrayList<String>();
		List<String> arraylist28 = new ArrayList<String>();
		List<String> arraylist29 = new ArrayList<String>();
		List<String> arraylist30 = new ArrayList<String>();
		List<String> arraylist31 = new ArrayList<String>();
		List<String> arraylist32 = new ArrayList<String>();
		List<String> arraylist33 = new ArrayList<String>();
		List<String> arraylist34 = new ArrayList<String>();
		List<String> arraylist35 = new ArrayList<String>();
		List<String> arraylist36 = new ArrayList<String>();
		List<String> arraylist37 = new ArrayList<String>();
		List<String> arraylist38 = new ArrayList<String>();
		List<TesnValueObject> data = new ArrayList<TesnValueObject>();
		
		try {
			log.info("START: TesnJpPsaView Start criteria "+ "criteria:"+criteria.toString() +" topsmodel:"+topsmodel +" map:"+map);
			int i;
			if (topsmodel != null) {
				if ((i = topsmodel.getSize()) != 0) {
					for (int j = 0; j < topsmodel.getSize(); j++) {
						TesnValueObject tesnValueObject = new TesnValueObject();
						tesnValueObject = (TesnValueObject) topsmodel.get(j);
						data.add(tesnValueObject);
						
						if (resButton.equalsIgnoreCase("Add")) {
							s1 = tesnValueObject.getTesnAsnNbr();
							s2 = tesnValueObject.getTesnNomWt();
							s3 = tesnValueObject.getTesnNomVol();
							s32 = tesnValueObject.getPortLn();
							s4 = tesnValueObject.getGbCloseBjInd();
							s5 = tesnValueObject.getTransDnNbrPkgs();
							s6 = tesnValueObject.getNomWt();
							s7 = tesnValueObject.getNomVol();

							arraylist1.add(s1);
							arraylist2.add(s2);
							arraylist3.add(s3);
							arraylist32.add(s32);
							arraylist4.add(s4);
							arraylist5.add(s5);
							arraylist6.add(s6);
							arraylist7.add(s7);
						

							map.put("TesnNbr", arraylist1);
							map.put("TesnNomWt", arraylist2);
							map.put("TesnNomVol", arraylist3);
							map.put("PortName", arraylist32);
							map.put("GbCloseBjInd", arraylist4);
							map.put("TransDnNbrPkgs", arraylist5);
							map.put("EdoNomWt", arraylist6);
							map.put("EdoNomVol", arraylist7);

						}
						if (resButton.equalsIgnoreCase("List")) {
							// assignCargo = criteria.getPredicates().get("assignCrg");
							s1 = tesnValueObject.getPkgType();
							s2 = tesnValueObject.getHsCode();
							s3 = tesnValueObject.getCntrSize();
							s4 = tesnValueObject.getCntrType();
							s5 = tesnValueObject.getCrgDes();
							s6 = tesnValueObject.getDgInd();
							s7 = tesnValueObject.getEdoAsnNbr();
							s8 = tesnValueObject.getEsnAsnNbr();
							s9 = tesnValueObject.getHsCode();
							s10 = tesnValueObject.getInVoyNbr();
							s11 = tesnValueObject.getMftMarkings();
							s12 = tesnValueObject.getVslNm();
							s13 = tesnValueObject.getNomWt();
							s14 = tesnValueObject.getNomVol();
							s15 = tesnValueObject.getCntrNbr1();
							s16 = tesnValueObject.getCargoType();
							s17 = tesnValueObject.getCntrNbr2();
							s18 = tesnValueObject.getCntrNbr3();
							s19 = tesnValueObject.getCntrNbr4();
							s20 = tesnValueObject.getCargoTypeNm();
							s21 = tesnValueObject.getPkgTypeDesc();
							s22 = tesnValueObject.getNbrPkgs();
							s23 = tesnValueObject.getTesnAsnNbr();
							s24 = tesnValueObject.getTesnNbrPkgs();
							s25 = tesnValueObject.getTesnPortDis();
							s26 = tesnValueObject.getTesnSecCar();
							s27 = tesnValueObject.getTesnSecVoy();
							s28 = tesnValueObject.getTesnShipper();
							s29 = tesnValueObject.getTesnNomWt();
							s30 = tesnValueObject.getTesnNomVol();
							s31 = tesnValueObject.getVarNbr();
							s32 = tesnValueObject.getPortLn();
							s33 = tesnValueObject.getEdoNbrPkgs();
							s34 = tesnValueObject.getGbCloseBjInd();
							s35 = tesnValueObject.getTransDnNbrPkgs();
							s36 = tesnValueObject.getNomWt();
							s37 = tesnValueObject.getNomVol();
							s38 = tesnValueObject.getHsCodeDisp();

							arraylist1.add(s1);
							arraylist2.add(s2);
							arraylist3.add(s3);
							arraylist4.add(s4);
							arraylist5.add(s5);
							arraylist6.add(s6);
							arraylist7.add(s7);
							arraylist8.add(s8);
							arraylist9.add(s9);
							arraylist10.add(s10);
							arraylist11.add(s11);
							arraylist12.add(s12);
							arraylist13.add(s13);
							arraylist14.add(s14);
							arraylist15.add(s15);
							arraylist16.add(s16);
							arraylist17.add(s17);
							arraylist18.add(s18);
							arraylist19.add(s19);
							arraylist20.add(s20);
							arraylist21.add(s21);
							arraylist22.add(s22);
							arraylist23.add(s23);
							arraylist24.add(s24);
							arraylist25.add(s25);
							arraylist26.add(s26);
							arraylist27.add(s27);
							arraylist28.add(s28);
							arraylist29.add(s29);
							arraylist30.add(s30);
							arraylist31.add(s31);
							arraylist32.add(s32);
							arraylist33.add(s33);
							arraylist34.add(s34);
							arraylist35.add(s35);
							arraylist36.add(s36);
							arraylist37.add(s37);
							arraylist38.add(s38);

							map.put("PkgType", arraylist1);
							map.put("HsCode", arraylist2);
							map.put("CntrSize", arraylist3);
							map.put("CntrType", arraylist4);
							map.put("CrgDes", arraylist5);
							map.put("DgInd", arraylist6);
							map.put("EdoAsnNbr", arraylist7);
							map.put("EsnAsnNbr", arraylist8);
							map.put("HsCode", arraylist9);
							map.put("InVoyNbr", arraylist10);
							map.put("MftMarkings", arraylist11);
							map.put("VslNm", arraylist12);
							map.put("NomWt", arraylist13);
							map.put("NomVol", arraylist14);
							map.put("CntrNbr1", arraylist15);
							map.put("CargoType", arraylist16);
							map.put("CntrNbr2", arraylist17);
							map.put("CntrNbr3", arraylist18);
							map.put("CntrNbr4", arraylist19);
							map.put("CargoTypeNm", arraylist20);
							map.put("PkgTypeDesc", arraylist21);
							map.put("NbrPkgs", arraylist22);
							map.put("TesnAsnNbr", arraylist23);
							map.put("TesnNbrPkgs", arraylist24);
							map.put("PortDis", arraylist25);
							map.put("SecCarVes", arraylist26);
							map.put("SecCarVoy", arraylist27);
							map.put("Shipper", arraylist28);
							map.put("NomWt", arraylist29);
							map.put("NomVol", arraylist30);
							map.put("VarNbr", arraylist31);
							map.put("PortName", arraylist32);
							map.put("EdoNbrPkgs", arraylist33);
							map.put("GbCloseBjInd", arraylist34);
							map.put("TransDnNbrPkgs", arraylist35);
							map.put("EdoNomWt", arraylist36);
							map.put("EdoNomVol", arraylist37);
							map.put("HsCodeDisp", arraylist38);
						}
						map.put("List", data);
					}

				} else {
					log.info("TopsModel return 0 results to servlet");
				}
			} else {
				log.info("Unable to retrieve topsModel");
			}

		} catch (Exception e) {
			log.info("Exception TesnJpPsaView : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				throw new BusinessException(errorMessage);
			}
		}
	}

	public String formatValues(String value) {
		log.info("START: formatValues  "+" value:"+CommonUtility.deNull(value) );
		int decPosition = 0;
		String resultarr[] = new String[15];
		int decDig = 0;
		int digcount = 0;
		for (int i = 0; i < value.trim().length(); i++) {
			/// Check for the position of the decimal point

			if (value.charAt(i) == '.')
				decPosition = 1;

			if (decPosition == 1)
				decDig++;
			else
				digcount++;

		}

		int noofZero = 11 - digcount;
		int noofZeroDec = (4 - decDig) + 1;
		int noofzerTemp = noofZeroDec;
		int count = 0;
		for (int i = 0; i < 11; i++) {
			if (i < noofZero) {
				resultarr[i] = "0";

			} else {
				resultarr[i] = value.charAt(count) + "";
				count++;
			}

		}

		count++;
		int j = 0;
		for (int i = 11; i < 15; i++) {
			if (noofZeroDec > 0) {

				noofZeroDec--;
				resultarr[14 - j] = "0";
				j++;

			}
		}

		for (int i = 11; i < 15 - noofzerTemp; i++) {
			resultarr[i] = value.charAt(count) + "";
			count++;

		}
		String retStr = "";
		for (int i = 0; i <= 14; i++)
			retStr = retStr + resultarr[i];
		
		log.info("END: *** formatValues result*****" + CommonUtility.deNull(retStr));
		return retStr;

	}
	
	@PostMapping(value = "/TesnJpPsaAmendDelete")
	public ResponseEntity<?> TesnJpPsaAmendDelete(HttpServletRequest request) throws BusinessException {
		return this.TesnJpPsaAmend(request);
	}

	// delegate.helper.gbms.cargo.tesn.tesnJpPsa-TesnJpPsaAmendHandler
	@PostMapping(value = "/TesnJpPsaAmend")
	public ResponseEntity<?> TesnJpPsaAmend(HttpServletRequest request) throws BusinessException {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			Criteria criteria = CommonUtil.getCriteria(request);

			errorMessage = null;
			log.info("** tesnJpPsaAmend Start criteria :" + criteria.toString());

			String resButton = criteria.getPredicates().get("resButton");

			String edoAsnNbrStr = "";
			String tesnNbr = "";
			String secCarves = "";
			String portDis = "";
			String secCarvoy = "";
			String shipper = "";
			String noOfPkgs = "";
			String nomWtStr = "";
			String nomVolStr = "";
			String bkref = "";
			String acctNbr = "";
			int noOfPkgsInt = 0;

			tesnNbr = criteria.getPredicates().get("tesnNbrStr");
			secCarves = criteria.getPredicates().get("secCarves");
			portDis = criteria.getPredicates().get("portdisc");
			secCarvoy = criteria.getPredicates().get("secCarvoy");
			noOfPkgs = criteria.getPredicates().get("noOfPkgs");
			nomWtStr = criteria.getPredicates().get("nomWtStr");
			nomVolStr = criteria.getPredicates().get("nomVolStr");
			shipper = criteria.getPredicates().get("shipper");
			edoAsnNbrStr = criteria.getPredicates().get("edoAsnNbrStr");
			bkref = criteria.getPredicates().get("bkref");
			acctNbr = criteria.getPredicates().get("acctNbr");
			String category = "00";
			if (!StringUtils.isEmpty(criteria.getPredicates().get("category"))) {
				category = criteria.getPredicates().get("category");
			}
			;
			map.put("category", category);
			map.put("nomWtStr", nomWtStr);
			map.put("nomVolStr", nomVolStr);
			
			if (acctNbr == null)
				acctNbr = "";
			noOfPkgsInt = Integer.parseInt(noOfPkgs);
			String s65 = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String s66 = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String inVoyNbrStr = CommonUtility.deNull(criteria.getPredicates().get("inVoyNbrStr"));

			String vslType = renomTesnJpPsaService.getVslTypeByAsnNo(edoAsnNbrStr);
			map.put("vslType", vslType);
			if (vslType.equals("CC")) {
				List<Map<String, Object>> list = renomTesnJpPsaService.getCategoryList();
				map.put("categoryList", list);
			}

			try {
			} catch (Exception e) {
				log.info("Exception tesnJpPsaAmend : ", e);
				throw new BusinessException("M4201");
			}
		
			String s = "";
			String sysdatestr = "";
			String strwt = "";
			String strvol = "";
			String strwtvol = "";
			if (resButton.equalsIgnoreCase("Amend2")) {
				// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014: START
				boolean checkSecondCarrierVsl = renomTesnJpPsaService.chkSecondCarrierVsl(secCarves, secCarvoy);
				if (!checkSecondCarrierVsl) {
					errorMessage = ConstantUtil.ErrorMsg_Voy_Vessel_Invalid;
					map.put("errorMessage", errorMessage);
					result = new Result();
					result.setErrors(map);
					result.setSuccess(false);
					return ResponseEntityUtil.success(result.toString());
				}
				if (!"JP".equalsIgnoreCase(s66)) {
					boolean checkAtuSecondCarrierVsl = renomTesnJpPsaService.chkDttmOfSecondCarrierVsl(secCarves,
							secCarvoy);
					if (!checkAtuSecondCarrierVsl) {
						errorMessage = ConstantUtil.ErrorMsg_Unable_Create_After_ATU;
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						return ResponseEntityUtil.success(result.toString());
					}
				}
				// VietNguyen (FPT) Document Process Enhancement 06-Jan-2014: END
				// START #31377 : Added weight and volume field for TESN JP to PSA - NS Sept 2023
				s = renomTesnJpPsaService.updateRecord(s65, portDis, secCarves, secCarvoy, noOfPkgsInt, shipper,
						tesnNbr, edoAsnNbrStr, bkref, acctNbr, category, nomWtStr, nomVolStr);
				// END CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
				String k = (String) renomTesnJpPsaService.createNomVesselJPPsa(secCarves, secCarvoy, s65);
				sysdatestr = renomTesnJpPsaService.getSysdate();
				JPPSAValueObject jpvalobj = new JPPSAValueObject();
				JPPSAValueObject jpvalobj1 = new JPPSAValueObject();

				jpvalobj.setFileName(" ");
				jpvalobj.setICHRecType("H");
				jpvalobj.setICHCreateDate(sysdatestr); //
				jpvalobj.setICHAbbVslName(criteria.getPredicates().get("vslNmStr"));
				jpvalobj.setICHDisAbbVslName(criteria.getPredicates().get("inVoyNbrStr"));
				jpvalobj1.setICDRecordType("D");
				jpvalobj1.setICDFunction("U");
				String blno = renomTesnJpPsaService.getBLNo((edoAsnNbrStr));
				String crno = renomTesnJpPsaService.getTdbCrno(edoAsnNbrStr);
				jpvalobj1.setICDBillofLading(blno);
				jpvalobj1.setICDHScode(criteria.getPredicates().get("hsCodeStr"));
				jpvalobj1.setICDPackageType(criteria.getPredicates().get("pkgTypeStr"));
				jpvalobj1.setICDNoofPackage(criteria.getPredicates().get("noOfPkgs"));
				strwtvol = renomTesnJpPsaService.getTesnWtVol(tesnNbr);
				StringTokenizer strtkn = new StringTokenizer(strwtvol, "-", false);
				strwt = strtkn.nextToken();
				strvol = strtkn.nextToken();
				jpvalobj1.setICDWeight(formatValues(strwt));
				jpvalobj1.setICDVolume(formatValues(strvol));
				jpvalobj1.setICDDGIndicator(criteria.getPredicates().get("dgIndStr"));
				jpvalobj1.setICDShipperName(shipper);

				String cargoType = criteria.getPredicates().get("cargoTypeStr");
				if (cargoType.length() > 1)
					cargoType = cargoType.charAt(1) + "";

				jpvalobj1.setICDCargoType(cargoType);
				jpvalobj1.setICDLoadingVessel(secCarves);
				jpvalobj1.setICDLoadingVoyage(secCarvoy);
				jpvalobj1.setICDPortOfDischarge(portDis);

				jpvalobj1.setICDContainerNumber(CommonUtility.deNull(criteria.getPredicates().get("cntrSizeStr")));
				jpvalobj1.setICDDirectInterGateWay("JP");
				jpvalobj1.setICDBookingRef(bkref);
				jpvalobj1.setICDAccount(acctNbr);
				jpvalobj1.setICDTdbNo(crno);
				jpvalobj1.setICDCargoDescription(criteria.getPredicates().get("crgDesStr"));
				String marking = criteria.getPredicates().get("mftMarkingsStr");
				if (marking == null)
					marking = "NIL";
				if (marking != null && marking.equals(""))
					marking = "NIL";

				jpvalobj1.setICDMarking(marking);
				jpvalobj.setICSRecordtype("S");
				jpvalobj.setICSTotalRec("1");
				List<JPPSAValueObject> v = new ArrayList<JPPSAValueObject>();
				v.add(jpvalobj1);
				jpvalobj.setCargoDetails(v);
				log.info("Enters" + acctNbr);

				gbmsfile.insertIGD(jpvalobj, criteria);
				// String inVoyNbrStr =
				// CommonUtility.deNull(criteria.getPredicates().get("inVoyNbrStr"));
				gbmsfile.writeJPPSA(inVoyNbrStr, "U");
				log.info("Enters out");
			}

			if (resButton.equalsIgnoreCase("Delete1")) {
				s = renomTesnJpPsaService.deleteRecord(tesnNbr, edoAsnNbrStr, s65);
				sysdatestr = renomTesnJpPsaService.getSysdate();
				JPPSAValueObject jpvalobj = new JPPSAValueObject();
				JPPSAValueObject jpvalobj1 = new JPPSAValueObject();

				jpvalobj.setFileName(" ");

				jpvalobj.setICHRecType("H");
				jpvalobj.setICHCreateDate(sysdatestr);
				jpvalobj.setICHAbbVslName(criteria.getPredicates().get("vslNmStr"));
				jpvalobj.setICHDisAbbVslName(criteria.getPredicates().get("inVoyNbrStr"));
				jpvalobj1.setICDRecordType("D");
				jpvalobj1.setICDFunction("D");
				log.info("tesn no : " + tesnNbr);
				String blno = renomTesnJpPsaService.getBLNo((edoAsnNbrStr));
				String crno = renomTesnJpPsaService.getTdbCrno(edoAsnNbrStr);
				jpvalobj1.setICDBillofLading(blno);
				jpvalobj1.setICDHScode(criteria.getPredicates().get("hsCodeStr"));
				jpvalobj1.setICDPackageType(criteria.getPredicates().get("pkgTypeStr"));
				jpvalobj1.setICDNoofPackage(criteria.getPredicates().get("noOfPkgs"));
				jpvalobj1.setICDWeight(formatValues(criteria.getPredicates().get("nomWtStr")));
				jpvalobj1.setICDVolume(formatValues(criteria.getPredicates().get("nomVolStr")));
				jpvalobj1.setICDDGIndicator(criteria.getPredicates().get("dgIndStr"));
				jpvalobj1.setICDShipperName(criteria.getPredicates().get("shipper"));
				String cargoType = criteria.getPredicates().get("cargoTypeStr");
				if (cargoType.length() > 1)
					cargoType = cargoType.charAt(1) + "";
				jpvalobj1.setICDCargoType(cargoType);
				jpvalobj1.setICDLoadingVessel(criteria.getPredicates().get("secCarves"));
				jpvalobj1.setICDLoadingVoyage(criteria.getPredicates().get("secCarvoy"));
				jpvalobj1.setICDPortOfDischarge(criteria.getPredicates().get("portdisc"));
				jpvalobj1.setICDContainerNumber(CommonUtility.deNull(criteria.getPredicates().get("cntrSizeStr")));
				jpvalobj1.setICDDirectInterGateWay("JP");
				jpvalobj1.setICDBookingRef(bkref);
				jpvalobj1.setICDAccount(acctNbr);
				jpvalobj1.setICDTdbNo(crno);
				jpvalobj1.setICDCargoDescription(criteria.getPredicates().get("crgDesStr"));
				String marking = criteria.getPredicates().get("mftMarkingsStr");
				if (marking == null)
					marking = "NIL";
				if (marking != null && marking.equals(""))
					marking = "NIL";
				jpvalobj1.setICDMarking(marking);
				jpvalobj.setICSRecordtype("S");
				jpvalobj.setICSTotalRec("1");
				List<JPPSAValueObject> v = new ArrayList<JPPSAValueObject>();
				v.add(jpvalobj1);
				jpvalobj.setCargoDetails(v);
				log.info("Enters del");
				gbmsfile.insertIGD(jpvalobj, criteria);
				gbmsfile.writeJPPSA(inVoyNbrStr, "D");
				log.info("Enters");

			}
			if (resButton.equalsIgnoreCase("Generate")) {

				log.info("inVoyNbrStr" + criteria.getPredicates().get("inVoyNbrStr"));
				gbmsfile.writeJPPSA(inVoyNbrStr, " ");

			}

			TopsModel topsmodel = new TopsModel();
			map.put("topsmodel", topsmodel);
			tesnJpPsaAmend(criteria, map);

		} catch (BusinessException be) {
			log.info("Exception tesnJpPsaAmend: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception tesnJpPsaAmend : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: tesnJpPsaAmend result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	private void tesnJpPsaAmend(Criteria criteria, Map<String, Object> map) throws BusinessException {

		String resButton = "";
		try {
			log.info("START: tesnJpPsaAmend "+" criteria:"+criteria.toString() +" map:"+map);
			TopsModel topsmodel = (TopsModel) map.get("topsmodel");
			resButton = criteria.getPredicates().get("resButton");
			int i;
			if (topsmodel != null) {
				if ((i = topsmodel.getSize()) != 0) {
					for (int j = 0; j < topsmodel.getSize(); j++) {
						TesnValueObject tesnValueObject = new TesnValueObject();
						tesnValueObject = (TesnValueObject) topsmodel.get(j);
						map.put("tesnValueObject", tesnValueObject);
					}
				} else {
					log.info("TopsModel return 0 results to servlet");
				}
			} else {
				log.info("Unable to retrieve topsModel");
			}
			map.put("resButton", resButton);

		} catch (Exception e) {
			log.info("Exception tesnJpPsaAmend : ", e);
			throw new BusinessException("M4201");

		}

	}

	@PostMapping(value = "/portList")
	public ResponseEntity<?> portList(HttpServletRequest request) throws BusinessException {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			Criteria criteria = CommonUtil.getCriteria(request);

			errorMessage = null;
			log.info("** portList Start criteria :" + criteria.toString());

			List<TesnValueObject> portlist = new ArrayList<TesnValueObject>();
			List<String> portcdlist = new ArrayList<String>();
			List<String> portnmlist = new ArrayList<String>();
			portlist = renomTesnJpPsaService.getPortList();
			for (int i = 0; i < portlist.size(); i++) {
				TesnValueObject tesnValueObject = new TesnValueObject();
				tesnValueObject = portlist.get(i);
				portcdlist.add(tesnValueObject.getPortL());
				portnmlist.add(tesnValueObject.getPortLn());
			}
			map.put("portcdlist", portcdlist);
			map.put("portnmlist", portnmlist);

		} catch (BusinessException be) {
			log.info("Exception portList: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception portList : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: portList result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.tesn.tesnJpPsa-TesnJpPsaAddHandler
	@PostMapping(value = "/TesnJpPsaAdd")
	public ResponseEntity<?> TesnJpPsaAdd(HttpServletRequest request) throws BusinessException {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			Criteria criteria = CommonUtil.getCriteria(request);

			errorMessage = null;
			log.info("** tesnJpPsaAdd Start criteria :" + criteria.toString());

			String asnNo = "";
			// String s65 =
			// CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String s66 = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			try {
				asnNo = criteria.getPredicates().get("AsnNo");
			} catch (Exception e) {
				log.info("Exception perform : ", e);
				throw new BusinessException("M4201");
			}
			String vslType = renomTesnJpPsaService.getVslTypeByAsnNo(asnNo);
			map.put("vslType", vslType);
			if (vslType.equals("CC")) {
				 List<Map<String, Object>> list = renomTesnJpPsaService.getCategoryList();
				map.put("categoryList", list);
			}
			List<TesnValueObject> arraylist1 = renomTesnJpPsaService.getCargoDetails(asnNo);
			if (!"JP".equalsIgnoreCase(s66)) {
				boolean chkCrgAgtAdpEdo = renomTesnJpPsaService.chkCrgAgtAdpEdo(asnNo, s66);
				if (!chkCrgAgtAdpEdo) {
					errorMessage = ConstantUtil.ErrorMsg_UnAuthorized_Create_TESN;
					map.put("errorMessage", errorMessage);
					result = new Result();
					result.setErrors(map);
					result.setSuccess(false);
					result.setData(map);
					return ResponseEntityUtil.success(result.toString());
				}
			}

			TopsModel topsmodel = new TopsModel();

			for (int j = 0; j < arraylist1.size(); j++) {
				TesnValueObject tesnValueObject = new TesnValueObject();
				tesnValueObject = (TesnValueObject) arraylist1.get(j);
				topsmodel.put(tesnValueObject);
			}
			map.put("tesnValueObject", topsmodel);
			map.put("custCd", s66);
			tesnJpPsaAdd(criteria, map);

		} catch (BusinessException be) {
			log.info("Exception tesnJpPsaAdd: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception tesnJpPsaAdd : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: tesnJpPsaAdd result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	private void tesnJpPsaAdd(Criteria criteria, Map<String, Object> map) throws BusinessException {

		String s1 = "";
		String s2 = "";
		String s3 = "";
		String s4 = "";
		String s5 = "";
		String s6 = "";
		String s7 = "";
		String s8 = "";
		String s9 = "";
		String s10 = "";
		String s11 = "";
		String s12 = "";
		String s13 = "";
		String s14 = "";
		String s15 = "";
		String s16 = "";
		String s17 = "";
		String s18 = "";
		String s19 = "";
		String s20 = "";
		String s21 = "";
		String s22 = "";
		String s23 = "";
		String s24 = "";
		String s25 = "";
		String s26 = "";

		try {
			log.info("START: tesnJpPsaAdd "+" criteria:"+criteria.toString() +" map:"+map);
			TopsModel topsmodel = (TopsModel) map.get("tesnValueObject");
			List<String> arraylist1 = new ArrayList<String>();
			List<String> arraylist2 = new ArrayList<String>();
			List<String> arraylist3 = new ArrayList<String>();
			List<String> arraylist4 = new ArrayList<String>();
			List<String> arraylist5 = new ArrayList<String>();
			List<String> arraylist6 = new ArrayList<String>();
			List<String> arraylist7 = new ArrayList<String>();
			List<String> arraylist8 = new ArrayList<String>();
			List<String> arraylist9 = new ArrayList<String>();
			List<String> arraylist10 = new ArrayList<String>();
			List<String> arraylist11 = new ArrayList<String>();
			List<String> arraylist12 = new ArrayList<String>();
			List<String> arraylist13 = new ArrayList<String>();
			List<String> arraylist14 = new ArrayList<String>();
			List<String> arraylist15 = new ArrayList<String>();
			List<String> arraylist16 = new ArrayList<String>();
			List<String> arraylist17 = new ArrayList<String>();
			List<String> arraylist18 = new ArrayList<String>();
			List<String> arraylist19 = new ArrayList<String>();
			List<String> arraylist20 = new ArrayList<String>();
			List<String> arraylist21 = new ArrayList<String>();
			List<String> arraylist22 = new ArrayList<String>();
			List<String> arraylist23 = new ArrayList<String>();
			List<String> arraylist24 = new ArrayList<String>();
			List<String> arraylist25 = new ArrayList<String>();
			List<String> arraylist26 = new ArrayList<String>();

			int i;
			if (topsmodel != null) {
				if ((i = topsmodel.getSize()) != 0) {
					for (int j = 0; j < topsmodel.getSize(); j++) {
						TesnValueObject tesnValueObject = new TesnValueObject();
						tesnValueObject = (TesnValueObject) topsmodel.get(j);
						s1 = tesnValueObject.getPkgType();
						s2 = tesnValueObject.getHsCode();
						s3 = tesnValueObject.getCntrSize();
						s4 = tesnValueObject.getCntrType();
						s5 = tesnValueObject.getCrgDes();
						s6 = tesnValueObject.getDgInd();
						s7 = tesnValueObject.getEdoAsnNbr();
						s8 = tesnValueObject.getEsnAsnNbr();
						s9 = tesnValueObject.getHsCode();
						s10 = tesnValueObject.getInVoyNbr();
						s11 = tesnValueObject.getMftMarkings();
						s12 = tesnValueObject.getVslNm();
						s13 = tesnValueObject.getNomWt();
						s14 = tesnValueObject.getNomVol();
						s15 = tesnValueObject.getCntrNbr1();
						s16 = tesnValueObject.getCargoType();
						s17 = tesnValueObject.getCntrNbr2();
						s18 = tesnValueObject.getCntrNbr3();
						s19 = tesnValueObject.getCntrNbr4();
						s20 = tesnValueObject.getCargoTypeNm();
						s21 = tesnValueObject.getPkgTypeDesc();
						s22 = tesnValueObject.getNbrPkgs();
						s23 = tesnValueObject.getVarNbr();
						// START CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
						s24 = tesnValueObject.getEdoNomWt();
						s25 = tesnValueObject.getEdoNomVol();
						// END CR #31377: Added weight and volume field for TESN JP to PSA - NS Sept 2023
						// START CR FTZ - NS JUNE 2024
						s26 = (tesnValueObject.getHsCodeDisp().isEmpty() || tesnValueObject.getHsCodeDisp()==null) ?tesnValueObject.getHsCode() :tesnValueObject.getHsCodeDisp()  ;

						// END CR FTZ - NS JUNE 2024

						arraylist1.add(s1);
						arraylist2.add(s2);
						arraylist3.add(s3);
						arraylist4.add(s4);
						arraylist5.add(s5);
						arraylist6.add(s6);
						arraylist7.add(s7);
						arraylist8.add(s8);
						arraylist9.add(s9);
						arraylist10.add(s10);
						arraylist11.add(s11);
						arraylist12.add(s12);
						arraylist13.add(s13);
						arraylist14.add(s14);
						arraylist15.add(s15);
						arraylist16.add(s16);
						arraylist17.add(s17);
						arraylist18.add(s18);
						arraylist19.add(s19);
						arraylist20.add(s20);
						arraylist21.add(s21);
						arraylist22.add(s22);
						arraylist23.add(s23);
						arraylist24.add(s24);
						arraylist25.add(s25);
						// START CR FTZ - NS JUNE 2024
						arraylist26.add(s26);
						// END CR FTZ - NS JUNE 2024

					}
					map.put("PkgType", arraylist1);
					map.put("HsCode", arraylist2);
					map.put("CntrSize", arraylist3);
					map.put("CntrType", arraylist4);
					map.put("CrgDes", arraylist5);
					map.put("DgInd", arraylist6);
					map.put("EdoAsnNbr", arraylist7);
					map.put("EsnAsnNbr", arraylist8);
					map.put("HsCode", arraylist9);
					map.put("InVoyNbr", arraylist10);
					map.put("MftMarkings", arraylist11);
					map.put("VslNm", arraylist12);
					map.put("NomWt", arraylist13);
					map.put("NomVol", arraylist14);
					map.put("CntrNbr1", arraylist15);
					map.put("CargoType", arraylist16);
					map.put("CntrNbr2", arraylist17);
					map.put("CntrNbr3", arraylist18);
					map.put("CntrNbr4", arraylist19);
					map.put("CargoTypeNm", arraylist20);
					map.put("PkgTypeDesc", arraylist21);
					map.put("NbrPkgs", arraylist22);
					map.put("VarNbr", arraylist23);
					map.put("EdoNomWt", arraylist24);
					map.put("EdoNomVol", arraylist25);
					// START CR FTZ - NS JUNE 2024
					map.put("HsCodeDisp", arraylist26);
					// END CR FTZ - NS JUNE 2024

				} else {
					log.info("TopsModel return 0 results to servlet");
				}
			} else {
				log.info("Unable to retrieve topsModel");
			}
		} catch (Exception e) {
			log.info("Exception tesnJpPsaAdd : ", e);
			throw new BusinessException("M4201");

		}

	}

}
