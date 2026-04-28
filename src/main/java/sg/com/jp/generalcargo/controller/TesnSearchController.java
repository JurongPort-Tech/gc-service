package sg.com.jp.generalcargo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TesnJpJpValueObject;
import sg.com.jp.generalcargo.domain.TesnPsaJpEsnListValueObject;
import sg.com.jp.generalcargo.domain.TesnSearchValueObject;
import sg.com.jp.generalcargo.domain.TesnValueObject;
import sg.com.jp.generalcargo.domain.TesnVesselVoyValueObject;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.TesnSearchService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = TesnSearchController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TesnSearchController {

	// TesnSearchHandler

	public static final String ENDPOINT = "gc/renominateCargo/tesn";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(TesnJpPsaController.class);

	@Autowired
	private TesnSearchService renomTesnSearchService;

	// delegate.helper.gbms.cargo.tesn-->TesnSearchHandler
	@PostMapping(value = "/TesnSearch")
	public ResponseEntity<?> TesnSearch(HttpServletRequest request) throws BusinessException {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			Criteria criteria = CommonUtil.getCriteria(request);

			errorMessage = null;
			log.info("** TesnSearch Start criteria :" + criteria.toString());
			String transType = "";
			String tesnNo = CommonUtility.deNull(criteria.getPredicates().get("tesnNo"));

			transType = renomTesnSearchService.tesnSearch(tesnNo);
			if (transType.equalsIgnoreCase("A") || transType == "A")
				tesnSearchJpJp(criteria, map);
			if (transType.equalsIgnoreCase("B") || transType == "B")
				tesnSearchJpPsa(criteria, map);
			if (transType.equalsIgnoreCase("C") || transType == "C")
				tesnSearchPsaJp(criteria, map);

			map.put("transType", transType);

		} catch (BusinessException be) {
			log.info("Exception TesnSearch: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception TesnSearch : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setErrors(map);
				result.setSuccess(false);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: TesnSearch result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// uiServlet.gbms.cargo.tesn-->TesnSearchJpJpServlet
	@SuppressWarnings("unchecked")
	private void tesnSearchJpJp(Criteria criteria, Map<String, Object> map) {
		TableResult tableResult = new TableResult();
		try {
			log.info("START: tesnSearchJpJp "+" criteria:"+criteria.toString() +" map:"+map);

			String tesnNo = CommonUtility.deNull(criteria.getPredicates().get("tesnNo"));
			String outVoyNo = "";
			TesnSearchValueObject tesnSearchValueObject = new TesnSearchValueObject();
			outVoyNo = tesnSearchValueObject.getOutVoyNo();
			
			log.info("outVoyNo: " + outVoyNo);

			// String UserID =
			// CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String strVoyno = "";

			List<TesnJpJpValueObject> tesnjpjplist = new ArrayList<TesnJpJpValueObject>();
			List<TesnJpJpValueObject> tesnjpjplist1 = new ArrayList<TesnJpJpValueObject>();
			List<TesnVesselVoyValueObject> vslList = renomTesnSearchService.getVslList(coCd);
			TopsModel topsModel = new TopsModel();
			for (int i = 0; i < vslList.size(); i++) {
				TesnVesselVoyValueObject vslObj = new TesnVesselVoyValueObject();
				vslObj = (TesnVesselVoyValueObject) vslList.get(i);
				topsModel.put(vslObj);
			}

			map.put("strVoyno", strVoyno);
			List<String> vsllist = new ArrayList<String>();
			List<String> voylist = new ArrayList<String>();
			List<String> vvvoy = new ArrayList<String>();

			for (int i = 0; i < topsModel.getSize(); i++) {
				TesnVesselVoyValueObject vvobj = new TesnVesselVoyValueObject();
				vvobj = (TesnVesselVoyValueObject) topsModel.get(i);
				String vname = vvobj.getVslName();
				String voyno = vvobj.getVoyNo();
				String vvVoy = vvobj.getVvVoy();
				vsllist.add(vname);
				voylist.add(voyno);
				vvvoy.add(vvVoy);
			}

			map.put("VslName", vsllist);
			map.put("VoyNo", voylist);
			map.put("VvVoy", vvvoy);

			int total = 0;
			tableResult = renomTesnSearchService.getTesnJpJpList(strVoyno, criteria);
			tesnjpjplist = tableResult.getData().getListData().getTopsModel();
			total = tableResult.getData().getTotal();

			for (int i = 0; i < tesnjpjplist.size(); i++) {
				tesnjpjplist1.add(tesnjpjplist.get(i));
			}
			map.put("tesnjpjplist", tesnjpjplist1);
			map.put("total", total);

			String delete_status = "0";
			int validTESN = renomTesnSearchService.tesnjpjpValidCheck(tesnNo, strVoyno);
			int validStatus = renomTesnSearchService.tesnjpjpValidCheck_status(tesnNo, strVoyno);
			int chkDn = renomTesnSearchService.tesnjpjpValidCheck_DN(tesnNo, strVoyno);
			if (validTESN == 1 && validStatus == 1 && chkDn == 1)
				delete_status = "1";
			else
				delete_status = "0";

			map.put("delete_status", delete_status);
			TesnJpJpValueObject tesnjpjpobj = new TesnJpJpValueObject();
			tesnjpjpobj = renomTesnSearchService.tesnjpjpView(strVoyno, tesnNo, "");
			map.put("tesnjpjpdisp", "Display");
			map.put("tesnjpjpobj", tesnjpjpobj);
			map.put("tesnNo", tesnNo);
			String outVoyNoNew = "";
			outVoyNoNew = tesnjpjpobj.getOut_voy_nbr();
			for (int i = 0; i < topsModel.getSize(); i++) {
				TesnVesselVoyValueObject vvobj = new TesnVesselVoyValueObject();
				vvobj = (TesnVesselVoyValueObject) topsModel.get(i);
				String voyno = vvobj.getVoyNo();
				String vvVoy = vvobj.getVvVoy();
				if (vvVoy.equalsIgnoreCase(outVoyNoNew))
					strVoyno = voyno;
			}

			map.put("strVoyno", strVoyno);
			map.put("tesnNo", tesnNo);
			map.put("topsModel", topsModel);

		} catch (BusinessException be) {
			log.info("Exception tesnSearchJpJp: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception tesnSearchJpJp : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			log.info("END: tesnSearchJpJp");
		}
	}

	// uiServlet.gbms.cargo.tesn-->TesnSearchJpPsaServlet
	private void tesnSearchJpPsa(Criteria criteria, Map<String, Object> map) {
		try {
			log.info("START: tesnSearchJpPsa "+" criteria:"+criteria.toString() +" map:"+map);
			String inVoyVarNo = "";
			String secCarrierName = "";
			int tnop = 0;
			double tgwt = 0.0D;
			double tvol = 0.0D;
			TopsModel topsmodel = new TopsModel();
			List<String> arraylist = new ArrayList<String>();
			List<String> firstCarrierNameList = new ArrayList<String>();
			List<String> inVoyVarNoList = new ArrayList<String>();
			List<String> nbrPkgsList = new ArrayList<String>();
			List<String> crgDesList = new ArrayList<String>();
			List<String> edoAsnNbrList = new ArrayList<String>();
			List<String> inVarNbrList = new ArrayList<String>();
			List<String> inVslNmList = new ArrayList<String>();
			List<String> varNbrList = new ArrayList<String>();
			String tesnNo = criteria.getPredicates().get("tesnNo");
			String vslNew = criteria.getPredicates().get("vslnew");

			// String userId =
			// CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			List<VesselVoyValueObject> vesselVoyList = renomTesnSearchService.getVesselVoy(coCode);
			List<String> vslNameList = new ArrayList<String>();
			List<String> voyList = new ArrayList<String>();
			List<String> varNoList = new ArrayList<String>();

			for (int j = 0; j < vesselVoyList.size(); j++) {
				VesselVoyValueObject vesselVoyValueObject = new VesselVoyValueObject();
				vesselVoyValueObject = (VesselVoyValueObject) vesselVoyList.get(j);
				String vslName = vesselVoyValueObject.getVslName();
				String voy = vesselVoyValueObject.getVoyNo();
				String varNo = vesselVoyValueObject.getVarNbr();
				vslNameList.add(vslName);
				voyList.add(voy);
				varNoList.add(varNo);
			}

			map.put("VslName", vslNameList);
			map.put("VoyNo", voyList);
			map.put("VarNo", varNoList);
			List<TesnValueObject> tesnJpPsaList = renomTesnSearchService.getTesnSearchJpPsaList(tesnNo, coCode);
			for (int j = 0; j < tesnJpPsaList.size(); j++) {
				TesnValueObject tesnvalueobject = new TesnValueObject();
				tesnvalueobject = (TesnValueObject) tesnJpPsaList.get(j);
				log.info("tesnvalueobject: " + tesnvalueobject);
			}
			
			

			List<TesnValueObject> searchList = null;

			if (criteria.getPredicates().get("PageIndex") == null
					|| criteria.getPredicates().get("PageIndex").equals("null") || vslNew.equals("VSLNEW")) {
				searchList = renomTesnSearchService.getTesnSearchJpPsaList(tesnNo, coCode);

			}

			for (int j = 0; j < searchList.size(); j++) {
				TesnValueObject tesnValueObject = new TesnValueObject();
				tesnValueObject = (TesnValueObject) searchList.get(j);
				arraylist.add(tesnValueObject.getEsnAsnNbr());
				firstCarrierNameList.add(tesnValueObject.getVslNm());
				inVoyVarNoList.add(tesnValueObject.getInVoyNbr());
				nbrPkgsList.add(tesnValueObject.getNbrPkgs());
				crgDesList.add(tesnValueObject.getCrgDes());
				edoAsnNbrList.add(tesnValueObject.getEdoAsnNbr());
				inVarNbrList.add(tesnValueObject.getInVarNbr());
				inVslNmList.add(tesnValueObject.getInVslNm());
				varNbrList.add(tesnValueObject.getVarNbr());
			}

			map.put("total", "" + searchList.size());
			map.put("esnAsnNbr", arraylist);
			map.put("vslNm", firstCarrierNameList);
			map.put("inVoyNbr", inVoyVarNoList);
			map.put("nbrPkgs", nbrPkgsList);
			map.put("crgDes", crgDesList);
			map.put("edoAsn", edoAsnNbrList);
			map.put("inVarNm", inVarNbrList);
			map.put("inVesNm", inVslNmList);
			inVoyVarNo = (String) varNbrList.get(0);
			secCarrierName = (String) varNbrList.get(0);
			map.put("selVoyno", inVoyVarNo);
			map.put("usrtyp", coCode);
			map.put("tnop", "" + tnop);
			map.put("tgwt", "" + tgwt);
			map.put("tvol", "" + tvol);

			List<TesnValueObject> tesnDetailsList = new ArrayList<TesnValueObject>();
			tesnDetailsList = renomTesnSearchService.getDisplayCargoDetails(tesnNo, inVoyVarNo, secCarrierName);
			for (int j = 0; j < tesnDetailsList.size(); j++) {
				TesnValueObject tesnValueObject = new TesnValueObject();
				tesnValueObject = (TesnValueObject) tesnDetailsList.get(j);
				topsmodel.put(tesnValueObject);
			}

			List<String> pkgTypeList = new ArrayList<String>();
			List<String> hsCodeList = new ArrayList<String>();
			List<String> cntrSizeList = new ArrayList<String>();
			List<String> cntrTypeList = new ArrayList<String>();
			List<String> crgDescList = new ArrayList<String>();
			List<String> dgIndList = new ArrayList<String>();
			List<String> edoList = new ArrayList<String>();
			List<String> esnList = new ArrayList<String>();
			List<String> inVoyList = new ArrayList<String>();
			List<String> markingList = new ArrayList<String>();
			List<String> vslNmList = new ArrayList<String>();
			List<String> nomWtList = new ArrayList<String>();
			List<String> nomVolList = new ArrayList<String>();
			List<String> cntrNo1List = new ArrayList<String>();
			List<String> cargoTypeList = new ArrayList<String>();
			List<String> cntrNo2List = new ArrayList<String>();
			List<String> cntrNo3List = new ArrayList<String>();
			List<String> cntrNo4List = new ArrayList<String>();
			List<String> cargoTypeNameList = new ArrayList<String>();
			List<String> pkgTypeDescList = new ArrayList<String>();
			List<String> noOfPkgList = new ArrayList<String>();
			List<String> tesnList = new ArrayList<String>();
			List<String> tesnPkgsList = new ArrayList<String>();
			List<String> podList = new ArrayList<String>();
			List<String> secCarrierList = new ArrayList<String>();
			List<String> secCarrierVoyList = new ArrayList<String>();
			List<String> shipperList = new ArrayList<String>();
			List<String> tesnNomWtList = new ArrayList<String>();
			List<String> tesnNomVolList = new ArrayList<String>();
			List<String> varList = new ArrayList<String>();
			List<String> polList = new ArrayList<String>();
			List<String> edoPkgsList = new ArrayList<String>();
			List<String> closeBJIndList = new ArrayList<String>();
			List<String> transDNPkgsList = new ArrayList<String>();
			List<String> edoNomWtList = new ArrayList<String>();
			List<String> edoNomVolList = new ArrayList<String>();

			for (int j = 0; j < topsmodel.getSize(); j++) {
				TesnValueObject tesnValueObject = (TesnValueObject) topsmodel.get(j);
				String pkgType = tesnValueObject.getPkgType();
				String hsCode = tesnValueObject.getHsCode();
				String cntrSize = tesnValueObject.getCntrSize();
				String cntrType = tesnValueObject.getCntrType();
				String crgDesc = tesnValueObject.getCrgDes();
				String dgInd = tesnValueObject.getDgInd();
				String edoAsnNo = tesnValueObject.getEdoAsnNbr();
				String esnAsnNo = tesnValueObject.getEsnAsnNbr();
				String inVoyNo = tesnValueObject.getInVoyNbr();
				String markings = tesnValueObject.getMftMarkings();
				String vslName = tesnValueObject.getVslNm();
				String nomWt = tesnValueObject.getNomWt();
				String nomVol = tesnValueObject.getNomVol();
				String cntrNo1 = tesnValueObject.getCntrNbr1();
				String cargoType = tesnValueObject.getCargoType();
				String cntrNo2 = tesnValueObject.getCntrNbr2();
				String cntrNo3 = tesnValueObject.getCntrNbr3();
				String cntrNo4 = tesnValueObject.getCntrNbr4();
				String cargoTypeName = tesnValueObject.getCargoTypeNm();
				String pkgTypeDesc = tesnValueObject.getPkgTypeDesc();
				String noOfPkgs = tesnValueObject.getNbrPkgs();
				String tesnAsnNo = tesnValueObject.getTesnAsnNbr();
				String tesnNoOfPkgs = tesnValueObject.getTesnNbrPkgs();
				String pod = tesnValueObject.getTesnPortDis();
				String tesnSecCarrier = tesnValueObject.getTesnSecCar();
				String tesnSecVoy = tesnValueObject.getTesnSecVoy();
				String shipper = tesnValueObject.getTesnShipper();
				String tesnNomWt = tesnValueObject.getTesnNomWt();
				String tesnNomVol = tesnValueObject.getTesnNomVol();
				String varNo = tesnValueObject.getVarNbr();
				String pol = tesnValueObject.getPortLn();
				String edoNoOfPkgs = tesnValueObject.getEdoNbrPkgs();
				String closeBJInd = tesnValueObject.getGbCloseBjInd();
				String transDnPkgs = tesnValueObject.getTransDnNbrPkgs();

				pkgTypeList.add(pkgType);
				hsCodeList.add(hsCode);
				cntrSizeList.add(cntrSize);
				cntrTypeList.add(cntrType);
				crgDescList.add(crgDesc);
				dgIndList.add(dgInd);
				edoList.add(edoAsnNo);
				esnList.add(esnAsnNo);
				inVoyList.add(inVoyNo);
				markingList.add(markings);
				vslNmList.add(vslName);
				nomWtList.add(nomWt);
				nomVolList.add(nomVol);
				cntrNo1List.add(cntrNo1);
				cargoTypeList.add(cargoType);
				cntrNo2List.add(cntrNo2);
				cntrNo3List.add(cntrNo3);
				cntrNo4List.add(cntrNo4);
				cargoTypeNameList.add(cargoTypeName);
				pkgTypeDescList.add(pkgTypeDesc);
				noOfPkgList.add(noOfPkgs);
				tesnList.add(tesnAsnNo);
				tesnPkgsList.add(tesnNoOfPkgs);
				podList.add(pod);
				secCarrierList.add(tesnSecCarrier);
				secCarrierVoyList.add(tesnSecVoy);
				shipperList.add(shipper);
				tesnNomWtList.add(tesnNomWt);
				tesnNomVolList.add(tesnNomVol);
				varList.add(varNo);
				polList.add(pol);
				edoPkgsList.add(edoNoOfPkgs);
				closeBJIndList.add(closeBJInd);
				transDNPkgsList.add(transDnPkgs);
				edoNomWtList.add(nomWt);
				edoNomVolList.add(nomVol);
			}

			map.put("PkgType", pkgTypeList);
			map.put("HsCode", hsCodeList);
			map.put("CntrSize", cntrSizeList);
			map.put("CntrType", cntrTypeList);
			map.put("CrgDes", crgDescList);
			map.put("DgInd", dgIndList);
			map.put("EdoAsnNbr", edoList);
			map.put("EsnAsnNbr", esnList);
			map.put("InVoyNbr", inVoyList);
			map.put("MftMarkings", markingList);
			map.put("VslNm", vslNmList);
			map.put("NomWt", nomWtList);
			map.put("NomVol", nomVolList);
			map.put("CntrNbr1", cntrNo1List);
			map.put("CargoType", cargoTypeList);
			map.put("CntrNbr2", cntrNo2List);
			map.put("CntrNbr3", cntrNo3List);
			map.put("CntrNbr4", cntrNo4List);
			map.put("CargoTypeNm", cargoTypeNameList);
			map.put("PkgTypeDesc", pkgTypeDescList);
			map.put("NbrPkgs", noOfPkgList);
			map.put("TesnAsnNbr", tesnList);
			map.put("NbrPkgs", tesnPkgsList);
			map.put("PortDis", podList);
			map.put("SecCarVes", secCarrierList);
			map.put("SecCarVoy", secCarrierVoyList);
			map.put("Shipper", shipperList);
			map.put("NomWt", tesnNomWtList);
			map.put("NomVol", tesnNomVolList);
			map.put("VarNbr", varList);
			map.put("PortName", polList);
			map.put("EdoNbrPkgs", edoPkgsList);
			map.put("GbCloseBjInd", closeBJIndList);
			map.put("TransDnNbrPkgs", transDNPkgsList);
			map.put("EdoNomWt", edoNomWtList);
			map.put("EdoNomVol", edoNomVolList);

			map.put("topsmodel", topsmodel);

		} catch (BusinessException be) {
			log.info("Exception tesnSearchJpPsa: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception tesnSearchJpPsa : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			log.info("END: tesnSearchJpPsa");
		}
	}

	// uiServlet.gbms.cargo.tesn-->TesnSearchPsaJpServlet
	private void tesnSearchPsaJp(Criteria criteria, Map<String, Object> map) {
		try {
			log.info("START: tesnSearchPsaJp "+" criteria:"+criteria.toString() +" map:"+map);
			TopsModel topsmodel = new TopsModel();

			String coCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String selVoy = "";
			String tesnNo = criteria.getPredicates().get("tesnNo");
			log.info("TESN NO. :".concat(String.valueOf(String.valueOf(tesnNo))));

			
			List<String> esnList = new ArrayList<String>();
			List<String> firstCarrierList = new ArrayList<String>();
			List<String> inVoyList = new ArrayList<String>();
			List<String> crgDescList = new ArrayList<String>();
			List<String> noOfPkgList = new ArrayList<String>();
			List<String> wtList = new ArrayList<String>();
			List<String> volList = new ArrayList<String>();
			List<String> vslNameList = new ArrayList<String>();
			List<String> voyList = new ArrayList<String>();
			List<String> varNoList = new ArrayList<String>();

			List<VesselVoyValueObject> vslList = renomTesnSearchService.getVesselList(coCode);
			log.info("inside loop Tesn Search Psa Jp ");
			for (int i = 0; i < vslList.size(); i++) {
				VesselVoyValueObject vesselVoyValueObject = (VesselVoyValueObject) vslList.get(i);
				String varNo = vesselVoyValueObject.getVarNbr();
				String vslName = vesselVoyValueObject.getVslName();
				String voy = vesselVoyValueObject.getVoyNo();
				vslNameList.add(vslName);
				voyList.add(voy);
				varNoList.add(varNo);
			}
			map.put("VslName", vslNameList);
			map.put("VoyNo", voyList);
			map.put("VarNbr", varNoList);

			log.info("inside Tesn Search Psa Jp  1");
			List<TesnPsaJpEsnListValueObject> esnDetails = renomTesnSearchService.getEsnDetails(tesnNo, coCode);
			List<TesnPsaJpEsnListValueObject> cntrDetails = renomTesnSearchService.getCntrDetails(tesnNo);
			map.put("selVoyno", selVoy);
			map.put("firstCName", firstCarrierList);
			map.put("noOfPkgs", noOfPkgList);
			map.put("esnNbr", esnList);
			map.put("firstCNbr", inVoyList);
			map.put("weight", wtList);
			map.put("volume", volList);
			map.put("crgDesc", crgDescList);
			map.put("esnDetails", esnDetails);
			map.put("custCd", coCode);
			map.put("cntrDetails", cntrDetails);
			map.put("custCd", coCode);
			map.put("varNbr", varNoList);
			map.put("topsmodel", topsmodel);

		} catch (BusinessException be) {
			log.info("Exception tesnSearchPsaJp: ", be);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception tesnSearchPsaJp : ", e);
			errorMessage = ConstantUtil.TESN_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			log.info("END: tesnSearchPsaJp");
		}
	}

}
