package sg.com.jp.generalcargo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import sg.com.jp.generalcargo.domain.AccessCompanyValueObject;
import sg.com.jp.generalcargo.domain.AccountValueObject;
import sg.com.jp.generalcargo.domain.AdminFeeWaiverValueObject;
import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.CargoDimensionDeclaration;
import sg.com.jp.generalcargo.domain.CargoDimensionDeclarationResponse;
import sg.com.jp.generalcargo.domain.CargoDimensionDetails;
import sg.com.jp.generalcargo.domain.CargoManifestFileUploadDetails;
import sg.com.jp.generalcargo.domain.CompanyValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.HSCode;
import sg.com.jp.generalcargo.domain.HatchBreakDownPageDetail;
import sg.com.jp.generalcargo.domain.HatchWisePackageDetail;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManifestActionTrailDetails;
import sg.com.jp.generalcargo.domain.ManifestCargoValueObject;
import sg.com.jp.generalcargo.domain.ManifestPkgDimDetails;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.MiscDetail;
import sg.com.jp.generalcargo.domain.PageDetails;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.Summary;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.InwardCargoManifestService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ProcessChargeConst;
import sg.com.jp.generalcargo.util.RecordPaging;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = InwardCargoManifestContoller.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InwardCargoManifestContoller {

	@Autowired
	private InwardCargoManifestService manifestService;

	public static final String ENDPOINT = "gc/inwardcargo/manifest";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(InwardCargoManifestContoller.class);

	// Region JPONLINE_MIGRATED

	@PostMapping(value = "/getManifestVslList")
	public ResponseEntity<?> getManifestVslList(HttpServletRequest request) {
		errorMessage = null;
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** getManifestVslList Start criteria :" + criteria.toString());
			TopsModel topsModel = new TopsModel();
			String coCD = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			List<VesselVoyValueObject> vesselCallList = manifestService.getVesselVoy(coCD);

			for (int i = 0; i < vesselCallList.size(); i++) {
				VesselVoyValueObject vvvObj = new VesselVoyValueObject();
				vvvObj = (VesselVoyValueObject) vesselCallList.get(i);
				topsModel.put(vvvObj);
			}
			List<String> terminallist = new ArrayList<String>();
			List<String> vsllist = new ArrayList<String>();
			List<String> varlist = new ArrayList<String>();
			List<String> voylist = new ArrayList<String>();
			for (int i = 0; i < topsModel.getSize(); i++) {
				VesselVoyValueObject vvobj = new VesselVoyValueObject();
				String terminal = "";
				String vname = "";
				String voyno = "";
				String varnbr = "";
				vvobj = (VesselVoyValueObject) topsModel.get(i);
				// Changed by LongDh09::Start
				if (vvobj != null) {
					terminal = vvobj.getTerminal();
					vname = vvobj.getVslName();
					voyno = vvobj.getVoyNo();
					varnbr = vvobj.getVarNbr();
				}
				// Changed by LongDh09::End
				terminallist.add(terminal);
				vsllist.add(vname);
				voylist.add(voyno);
				varlist.add(varnbr);
			}
			map.put("VslTerminal", terminallist);
			map.put("VslName", vsllist);
			map.put("VoyNo", voylist);
			map.put("VarNo", varlist);
		} catch (BusinessException e) {
			log.info("Exception getManifestVslList : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
		} catch (Exception e) {
			log.info("Exception getManifestVslList : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END:getManifestVslList  result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/getManifest")
	public ResponseEntity<?> getManifest(HttpServletRequest request) {
		errorMessage = null;
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		String selVoyno = "";
		int inop = 0;
		double dgwt = 0.0;
		double dvol = 0.0;
		// ++ Added by LongDh09
		String isFetch = "";
		String vesselName = "";
		String voyageNumber = "";
		// -- Added by LongDh09
		// haiTTH1 added on 17/1/2014
		String fetch_vv_cd = "";
		String terminal = "";
		TopsModel topsModel = new TopsModel();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** getManifest Start criteria :" + criteria.toString());
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			// String coCd = "JP";
			String fetchView = CommonUtility.deNull(criteria.getPredicates().get("fetchView"));
			String vslName = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
			String vslnew = CommonUtility.deNull(criteria.getPredicates().get("vslnew"));
			String SelectDropDown = CommonUtility.deNull(criteria.getPredicates().get("SelectDropDown"));
			String vesselName1 = CommonUtility.deNull(criteria.getPredicates().get("vesselName1"));
			String voyageNumber1 = CommonUtility.deNull(criteria.getPredicates().get("voyageNumber1"));

			String PageIndex = ((CommonUtility.deNull(criteria.getPredicates().get("PageIndex"))).equals("") ? "1"
					: CommonUtility.deNull(criteria.getPredicates().get("PageIndex")));
			String RowPerPage = ((CommonUtility.deNull(criteria.getPredicates().get("RowOfPage"))).equals("") ? "10"
					: CommonUtility.deNull(criteria.getPredicates().get("RowOfPage")));
			selVoyno = vslName;
			isFetch = fetchView; // Added

			vesselName = (CommonUtility.deNull(criteria.getPredicates().get("vesselName"))).trim().toUpperCase();
			voyageNumber = (CommonUtility.deNull(criteria.getPredicates().get("voyageNumber"))).trim().toUpperCase();
			terminal = (CommonUtility.deNull(criteria.getPredicates().get("terminal"))).trim().toUpperCase();

			boolean isFetchmode = false;

			if (fetchView != null) {
				String fetchmode = fetchView;
				if (fetchmode.equalsIgnoreCase("FETCH")) {
					isFetchmode = true;
				}
			}

			if (isFetchmode) {

				if ("".equals(vesselName) || vesselName == null) {
					vesselName = CommonUtility.deNull(vesselName1).trim().toUpperCase();
					voyageNumber = CommonUtility.deNull(voyageNumber1).trim().toUpperCase();
					if ("".equals(vesselName) || vesselName == null) {
						vesselName = CommonUtility.deNull((String) criteria.getPredicates().get("vesselName")).trim()
								.toUpperCase();
						voyageNumber = CommonUtility.deNull((String) criteria.getPredicates().get("voyageNumber"))
								.trim().toUpperCase();
					}
					if ("".equals(vesselName) || vesselName == null) {
						vesselName = CommonUtility.deNull((String) criteria.getPredicates().get("vslName2009")).trim()
								.toUpperCase();
						voyageNumber = CommonUtility.deNull((String) criteria.getPredicates().get("vslvoy2009")).trim()
								.toUpperCase();
					}
				} else {

				}
			}

			else {
				List<VesselVoyValueObject> vsNmVoyList = manifestService.getVsNmVoy(selVoyno);
				VesselVoyValueObject vvvObj = new VesselVoyValueObject();
				if (vsNmVoyList.size() > 0) {
					vvvObj = (VesselVoyValueObject) vsNmVoyList.get(0);
				}

				if (vvvObj != null) {
					vesselName = (String) vvvObj.getVslName();
					voyageNumber = (String) vvvObj.getVoyNo();
				}
			}

			map.put("vesselName", vesselName);
			map.put("voyageNumber", voyageNumber);

			String selectDropDown = CommonUtility.deNull(SelectDropDown);

			map.put("selectDropDown", selectDropDown);
			List<ManifestValueObject> mftlis = new ArrayList<ManifestValueObject>();
			List<String> blnolist = new ArrayList<String>();
			List<String> crglist = new ArrayList<String>();
			List<String> npkglist = new ArrayList<String>();
			List<String> gwtlist = new ArrayList<String>();
			List<String> gvolist = new ArrayList<String>();
			List<String> edostatlist = new ArrayList<String>();
			List<String> epcindlist = new ArrayList<String>();
			// Manifest listing
			List<String> seqnolist = new ArrayList<String>();
			List<String> crgstatlist = new ArrayList<String>();
			List<String> categorynbrList = new ArrayList<String>();
			List<String> unStfIndlist = new ArrayList<String>();
			List<String> dgIndList = new ArrayList<String>();
			List<String> opnIndList = new ArrayList<String>();
			List<String> stgIndList = new ArrayList<String>();
			List<String> categoryValueList = new ArrayList<String>();
			// CR-CIM- 0000108

			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			List<String> hsCodeList = new ArrayList<String>();
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

			List<VesselVoyValueObject> vesselCallList = manifestService.getVesselVoy(coCd);

			for (int i = 0; i < vesselCallList.size(); i++) {
				VesselVoyValueObject vvvObj = new VesselVoyValueObject();
				vvvObj = (VesselVoyValueObject) vesselCallList.get(i);
				topsModel.put(vvvObj);
			}
			// add three variables varNumber, veName, voNumber
			String varNumber = "";
			String veName = "";
			String voNumber = "";
			// HaiTTH1 added on 3/1/2014
			String arrival = "";
			String departure = "";
			String cod = "";
			String etb = "";
			String indicationOfArrival = "";
			String indicationOfDeparture = "";
			String berthNo = "";
			String vesselType = "";
			List<String> schemeList = new ArrayList<String>();
			List<String> subSchemeList = new ArrayList<String>();
			List<String> gcOperationsList = new ArrayList<String>();
			List<String> terminalList = new ArrayList<String>();
			if (isFetchmode) {
				List<VesselVoyValueObject> vesselCallList1 = manifestService.getVesselVoyList(coCd, vesselName, voyageNumber,
						terminal, vslName); 
				VesselVoyValueObject vvvObj = new VesselVoyValueObject();
				if (vesselCallList1.size() > 0) {
					vvvObj = (VesselVoyValueObject) vesselCallList1.get(0);
				}

				if (vvvObj != null) {
					selVoyno = (String) vvvObj.getVarNbr();
					varNumber = selVoyno;
					veName = (String) vvvObj.getVslName();
					voNumber = (String) vvvObj.getVoyNo();
					arrival = (String) vvvObj.getArrival();
					departure = (String) vvvObj.getDepartural();
					cod = (String) vvvObj.getCod_dttm();
					etb = (String) vvvObj.getEtb_dttm();
					fetch_vv_cd = (String) vvvObj.getVarNbr();
					indicationOfArrival = (String) vvvObj.getIndicationOfArrival();
					indicationOfDeparture = (String) vvvObj.getIndicationOfDeparture();
					berthNo = (String) vvvObj.getBerthNo();
					vesselType = (String) vvvObj.getVesselType();
				} else {

				}
			}

			else if (selVoyno != null && !selVoyno.equals("")) {
				// HaiTTH1 added on 15/1/2014
				VesselVoyValueObject veslObj1 = manifestService.getVesselInfo(selVoyno);
				String vsl_nm = veslObj1.getVslName();
				String voy_nbr = veslObj1.getVoyNo();
				List<VesselVoyValueObject> vesselarryList = manifestService.getVesselVoyList(coCd, vsl_nm, voy_nbr, terminal, vslName);
				VesselVoyValueObject vvvObj = new VesselVoyValueObject();
				if (vesselarryList.size() > 0)
					vvvObj = (VesselVoyValueObject) vesselarryList.get(0);
				if (vvvObj != null) {
					arrival = (String) vvvObj.getArrival();
					departure = (String) vvvObj.getDepartural();
					cod = (String) vvvObj.getCod_dttm();
					etb = (String) vvvObj.getEtb_dttm();
					fetch_vv_cd = (String) vvvObj.getVarNbr();
					indicationOfArrival = (String) vvvObj.getIndicationOfArrival();
					indicationOfDeparture = (String) vvvObj.getIndicationOfDeparture();
					berthNo = (String) vvvObj.getBerthNo();
					vesselType = (String) vvvObj.getVesselType();
				}
			}

			if ("".equals(varNumber) || varNumber == null) {
				varNumber = selVoyno;
			}
			if ("".equals(veName) || veName == null) {
				veName = vesselName;
				voNumber = voyageNumber;
			}
			// tienlc, check this ship must be closed in time or not: start.
			boolean checkVoyNumberStatus = manifestService.chkVslStat(varNumber);
			boolean mftStatus = manifestService.isManClose(selVoyno);
			String strMftStatus = mftStatus ? "closed" : "notclosed";
			map.put("mftStatus", strMftStatus);
			String cVNStatusStr = "FALSE";
			if (checkVoyNumberStatus == true) {
				cVNStatusStr = "TRUE";
			}
			map.put("cVNStatusStr", cVNStatusStr);
			// tienlc, check this ship must be closed in time or not: end.

			map.put("varNumber", varNumber);
			map.put("veName", veName);
			map.put("voNumber", voNumber);
			map.put("isFetch", isFetch);
			// HaiTTH1 added on 3/1/2014
			map.put("arrival", arrival);
			map.put("departure", departure);
			map.put("cod", cod);
			map.put("etb", etb);
			// Changed by LongDh09::End
			map.put("indicationOfArrival", indicationOfArrival);
			map.put("indicationOfDeparture", indicationOfDeparture);
			map.put("vesselType", vesselType);
			map.put("berthNo", berthNo);
			map.put("selVoyno", selVoyno);
			map.put("fetchView", isFetch); // Added by LongDh09
			// Amended by Punitha on 13/05/2009
			RecordPaging recPg = new RecordPaging();
			int numOfPage = 0;
			int curPage = 1;
			List<ManifestValueObject> recs = null;
			boolean getInd = true;

			if (PageIndex != null && !PageIndex.equals("")) {
				curPage = ( Integer.valueOf(PageIndex)).intValue();
			}

			if (selVoyno != null && !selVoyno.equals("")) {
				mftlis = manifestService.getManifestList(selVoyno, coCd, null);
				for (int i = 0; i < mftlis.size(); i++) {
					ManifestValueObject mftvObj = new ManifestValueObject();
					mftvObj = (ManifestValueObject) mftlis.get(i);
					inop = inop + Integer.valueOf(mftvObj.getNoofPkgs()).intValue();
					dgwt = dgwt + Double.valueOf(mftvObj.getGrWt()).doubleValue();
					dvol = dvol + Double.valueOf(mftvObj.getGrMsmt()).doubleValue();
				}
				if ((PageIndex == null) || vslnew.equals("VSLNEW")) {
					if (mftlis.size() > 0) {
						numOfPage = recPg.createRecordPagingCache("ListManifest", mftlis, Integer.parseInt(RowPerPage));

						if (!(PageIndex == null)) {
							if (curPage > numOfPage) {
								curPage = numOfPage;
							}
						}
					} else {
						getInd = false;
					}
				} else {
					if (mftlis.size() > 0) {
						numOfPage = recPg.createRecordPagingCache("ListManifest", mftlis, Integer.parseInt(RowPerPage));
					} else {
						getInd = false;
					}
				}

				if (getInd) {
					// To get the Data for the first page
					recs = recPg.getRecordsPage("ListManifest", curPage);

					int size = recs.size();
					int i = 0;
					String categoryValue = "";
					String category = "00";

					String hsCode = "";
					String hsSubCodeFr = "";
					String hsSubCodeTo = "";

					for (i = 0; i < size; i++) {
						ManifestValueObject mftvObj = new ManifestValueObject();
						mftvObj = (ManifestValueObject) recs.get(i);

						blnolist.add((mftvObj.getBlNo() == null) ? "" : (String) mftvObj.getBlNo());
						crglist.add((mftvObj.getCrgDesc() == null) ? "" : (String) mftvObj.getCrgDesc());
						npkglist.add((mftvObj.getNoofPkgs() == null) ? "" : (String) mftvObj.getNoofPkgs());
						gwtlist.add((mftvObj.getGrWt() == null) ? "" : (String) mftvObj.getGrWt());
						gvolist.add((mftvObj.getGrMsmt() == null) ? "" : (String) mftvObj.getGrMsmt());
						edostatlist.add((mftvObj.getEdostat() == null) ? "" : (String) mftvObj.getEdostat());
						epcindlist.add((mftvObj.getDeliveryToEPC() == null) ? "" : (String) mftvObj.getDeliveryToEPC());
						seqnolist.add((mftvObj.getSeqNo() == null) ? "" : (String) mftvObj.getSeqNo());
						crgstatlist.add((mftvObj.getCrgStatus() == null) ? "" : (String) mftvObj.getCrgStatus());
						unStfIndlist.add((mftvObj.getUnStfInd() == null) ? "" : (String) mftvObj.getUnStfInd());//
						category = (String) mftvObj.getCategory();
						categorynbrList.add((category == null) ? "" : category);
						// CR-CIM- 0000108
						dgIndList.add((mftvObj.getDgInd() == null) ? "" : (String) mftvObj.getDgInd());
						opnIndList.add((mftvObj.getOpInd() == null) ? "" : (String) mftvObj.getOpInd());
						stgIndList.add((mftvObj.getStgInd() == null) ? "" : (String) mftvObj.getStgInd());
						categoryValue = "";
						if (!StringUtils.isEmpty(category)) {
							categoryValue = manifestService.getCategoryValue(category);
						}
						categoryValueList.add((categoryValue == null) ? "" : categoryValue);
						// CR-CIM- 0000108

						// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 :
						// START
						hsCode = mftvObj.getHsCode();
						hsSubCodeFr = mftvObj.getHsSubCodeFr();
						hsSubCodeTo = mftvObj.getHsSubCodeTo();

						if (hsSubCodeTo != null && !"".equalsIgnoreCase(hsSubCodeTo)) {
							hsSubCodeFr = hsSubCodeFr + " - " + hsSubCodeTo;
						}

						if (hsSubCodeFr != null && !"".equalsIgnoreCase(hsSubCodeFr)) {
							hsCode = hsCode + "(" + hsSubCodeFr + ")";
						}
						hsCodeList.add(hsCode);
						// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 :
						// END

						// HaiTTH1 added on 10/1/2014
						schemeList.add((mftvObj.getScheme() == null) ? "" : (String) mftvObj.getScheme());
						subSchemeList.add((mftvObj.getSubScheme() == null) ? "" : (String) mftvObj.getSubScheme());
						gcOperationsList
								.add((mftvObj.getGcOperations() == null) ? "" : (String) mftvObj.getGcOperations());
						terminalList.add((mftvObj.getTerminal() == null) ? "" : (String) mftvObj.getTerminal());

					}

				} // if

			}
			map.put("total", mftlis.size());
			map.put("terminal", (terminalList != null && terminalList.size() > 0) ? terminalList.get(0) : "");
			map.put("totalPages", numOfPage + "");
			map.put("pageIndex", curPage + "");
			map.put("blnolist", blnolist);
			map.put("crglist", crglist);
			map.put("npkglist", npkglist);
			map.put("gwtlist", gwtlist);
			map.put("gvolist", gvolist);
			map.put("edostatlist", edostatlist);
			map.put("epcindlist", epcindlist); // MCC for EPC_IND in listing
			map.put("seqnolist", seqnolist);
			map.put("crgstatlist", crgstatlist);
			map.put("unStfIndlist", unStfIndlist);// added by vani -- 30th
													// Oct,03
			map.put("usrtyp", coCd);
			map.put("tnop", "" + inop);
			map.put("tgwt", "" + dgwt);
			map.put("tvol", "" + dvol);
			map.put("categorynbrList", categorynbrList);
			// CR-CIM- 0000108
			map.put("dgIndList", dgIndList);
			map.put("opnIndList", opnIndList);
			map.put("stgIndList", stgIndList);
			map.put("categoryValueList", categoryValueList);
			// CR-CIM- 0000108

			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			map.put("hsCodeList", hsCodeList);
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

			// HaiTTH1 added on 10/1/2014
			map.put("schemeList", schemeList);
			boolean canAdd = true;
			if (!StringUtils.equalsIgnoreCase("JP", coCd) && !StringUtils.isEmpty(fetch_vv_cd)) {
				canAdd = manifestService.checkAddManifest(fetch_vv_cd, coCd);
			}

			boolean isSubmissionAllowed = false;
			String createCustCode = "";
			boolean isVesselDeclarantAvailable = false;
			try {

				Criteria critObj = new Criteria();
				critObj.addPredicate("vvCd", varNumber);
				critObj.addPredicate("companyCode", coCd);
				isSubmissionAllowed = manifestService.isManifestSubmissionAllowed(critObj);
				createCustCode = manifestService.getCreateCustCdOfVessel(varNumber);
				isVesselDeclarantAvailable = manifestService.vesselDeclarantExists(varNumber);

			} catch (Exception ee) {
				log.info("Exception getManifest : ", ee);
			}

			map.put("createCustCode", createCustCode);
			map.put("isVesselDeclarantAvailable", isVesselDeclarantAvailable);
			map.put("canAddManifest", canAdd);
			map.put("ListManifest", recs);
			map.put("Screen", "ManifestVesselCallList");
			map.put("subSchemeList", subSchemeList);
			map.put("gcOperationsList", gcOperationsList);
			map.put("terminalList", terminalList);
			map.put("isSubmissionAllowed", isSubmissionAllowed);
			log.info("Final : " + map.toString());
		} catch (BusinessException e) {
			log.info("Exception getManifest : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
		} catch (Exception e) {
			log.info("Exception getManifest : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		}
		if (errorMessage != null) {
			map.put("errorMessage", errorMessage);
			result = new Result();
			result.setErrors(map);
			result.setError(errorMessage);
			result.setSuccess(false);
			result.setData(map);

		} else {
			result = new Result();
			result.setData(map);
			result.setSuccess(true);

		}

		return ResponseEntityUtil.success(result.toString());
	}

	// Region Manifest Add/Amend

	// Region manifestAdd
	
	@PostMapping(value = "/getManifestAddView")
	public ResponseEntity<?> ManifestAddView(HttpServletRequest request) {
		return this.ManifestAdd(request);
	}
	
	@PostMapping(value = "/ManifestAddOlbl")
	public ResponseEntity<?> ManifestAddOlbl(HttpServletRequest request) {
		return this.ManifestAdd(request);
	}
	
	@PostMapping(value = "/ManifestAdd")
	public ResponseEntity<?> ManifestAdd(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** ManifestAdd Start criteria :" + criteria.toString());
			String fromEDO = CommonUtility.deNull(criteria.getPredicates().get("fromEDO"));
			String varno = "";
			String vslvoy = "";
			StringTokenizer vslInvoy = null;
			StringTokenizer nobl = null;
			// For Display
			String disp = "";
			String blnum = "";
			String seqno = "";
			String sBlno = "";
			boolean isSplitBl = false;
			String BlNoRoot = null;
			String submitNextCommand = "";
			// add by deng 06/03/2011
			String category = "00";
			if (!StringUtils.isEmpty(CommonUtility.deNull(criteria.getPredicates().get("category")))) {
				category = CommonUtility.deNull(criteria.getPredicates().get("category"));

			}
			map.put("category", category);
			// For Addition
			String distype = ""; // <cfg 01.sep.08>
			String isToInsert = "";
			String sSeqno = "";
			String snoblno = "";
			String blno = "";
			String crgtype = "";
			String hscd = "";
			String crgdesc = "";
			String mark = "";
			String nop = "";
			String gwt = "";
			String gvol = "";
			String crgstat = "";
			String dgind = "";
			String stgind = "";
			String dopind = "";
			String pkgtype = "";
			String coname = "";
			String consigneeCoyCode = "";
			String tbConsigneeSearch = "";
			String pol = "";
			String pod = "";
			String pofd = "";
			String cntrtype = "";
			String cntrsize = "";
			String cntr1 = "";
			String cntr2 = "";
			String cntr3 = "";
			String cntr4 = "";
			String poln = "";
			String podn = "";
			String pofdn = "";
			String pkgn = "";
			String edonbrpkgs = "";
			// START CR FTZ HSCODE - NS JULY 2024
			String conAddr = "";
			String notifyParty = "";
			String notifyPartyAddr = "";
			String placeDel = "";
			String placeReceipt = "";
			String shipperNm = "";
			String shipperAdd = "";
			String customHsCode = "";
			int hsCodeSize = 0;
			
			// END CR FTZ HSCODE - NS JULY 2024
			boolean bedonbrpkgs = false;
			String vslnm = "";
			String invoynbr = "";
			String autParty = ""; // Added by thanhnv2
			// MCC
			String deliveryToEPC = "";
			// Added by LongDh09::Start
			String varNumber = "";
			String vesselName = CommonUtility.deNull(criteria.getPredicates().get("vesselName1"));
			String voyageNumber = CommonUtility.deNull(criteria.getPredicates().get("voyageNumber1"));
			String terminal = CommonUtility.deNull(criteria.getPredicates().get("terminal1"));

			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			String hsSubCode = "";
			String hsSubCodeFr = "";
			String hsSubCodeTo = "";
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

			String vslName2009 = CommonUtility.deNull(criteria.getPredicates().get("vslName2009"));
			String vslvoy2009 = CommonUtility.deNull(criteria.getPredicates().get("vslvoy2009"));
			String selectedCargo = CommonUtility.deNull(criteria.getPredicates().get("selectedCargo"));
			
			 String varNo = CommonUtility.deNull(criteria.getPredicates().get("varnbr2"));

			if ("".equals(vslName2009) || vslName2009 == null) {
				vslName2009 = CommonUtility.deNull(criteria.getPredicates().get("vslName2009")).trim().toUpperCase();
				vslvoy2009 = CommonUtility.deNull(criteria.getPredicates().get("vslvoy2009")).trim().toUpperCase();
			}

			map.put("vslName2009", vslName2009);
			map.put("vslvoy2009", vslvoy2009);

			// tienlc
			String isFetch = "";
			if (CommonUtility.deNull(criteria.getPredicates().get("fetchView")) != null) {
				map.put("fetchSession", CommonUtility.deNull(criteria.getPredicates().get("fetchView")));
				isFetch = (String) CommonUtility.deNull(criteria.getPredicates().get("fetchView"));
				if (!isFetch.equalsIgnoreCase("FETCH")) {
					vesselName = "";
					voyageNumber = "";
				}
//						HttpSession sessionFetch = request.getSession();
//						sessionFetch.setAttribute("fetchView", isFetch);
				map.put("fetchView", isFetch);
			}

			// Added by LongDh09::End
			boolean bvslstat = false;
			String vslstat = "";
			boolean bdnbrpkgs = false;
			String dnbrpkgs = "";
			boolean btnbrpkgs = false;
			String tnbrpkgs = "";
			boolean btdnbrpkgs = false;
			String tdnbrpkgs = "";
			boolean bnbredopkgs = false;
			boolean mftStat = false;
			String strMftStat = "";

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			map.put("USERID", userId); // Added by LongDh09:: for GB CR
			map.put("coCd", coCd); // Added by VietND02
			List<Map<String, String>> list = manifestService.getCategoryList();
			map.put("categoryList", list);
			if (!StringUtils.isEmpty(category)) {
				String categoryValue = manifestService.getCategoryValue(category);

				map.put("categoryValue", categoryValue);
			}
			Map<String, String> cargoCategoryCode_cargoCategoryName = manifestService
					.getCargoCategoryCode_CargoCategoryName();
			map.put("cargoCategoryCode_cargoCategoryName", cargoCategoryCode_cargoCategoryName);
			List<BookingReferenceValueObject> brvoList = manifestService.getBRVOList("other");
			map.put("brvoList", brvoList);
			boolean showAllCargoCategory = manifestService.isShowAllCargoCategoryCode(coCd);
			map.put("showAllCargoCategory", showAllCargoCategory);

			String notShowCargoCategoryCode = manifestService.getNotShowCargoCategoryCode();
			map.put("notShowCargoCategoryCode", notShowCargoCategoryCode);
			String vslCarCarrier = manifestService.getCarCarrierVesselCode();
			map.put("vslCarCarrier", vslCarCarrier);

			String defaultCargoCategoryCode = manifestService.getDefaultCargoCategoryCode();
			map.put("defaultCargoCategoryCode", defaultCargoCategoryCode);

			String cargoTypeNotShow = manifestService.getCargoTypeNotShow();
			map.put("cargoTypeNotShow", cargoTypeNotShow);

			// END added by Maksym JCMS Smart CR 6.10

			// Added by Satish on Mar 16 2004 : Rectified hypen vessel & special
			// char in text area : SL-GBMS-20040306-1
			List<String> vesselListName = new ArrayList<String>();
			List<String> vesselListVoyNo = new ArrayList<String>();
			List<String> vesselListVarNbr = new ArrayList<String>();
			List<String> vesselListTerminal = new ArrayList<String>();
			// Changed by LongDh09::Start
			if (vesselName.equals("") || vesselName == null) {
				List<VesselVoyValueObject> vesselList = manifestService.getVesselVoy(coCd);
				VesselVoyValueObject vvobj = new VesselVoyValueObject();
				if (vesselList != null && vesselList.size() > 0) {
					for (int i = 0; i < vesselList.size(); i++) {
						vvobj = (VesselVoyValueObject) vesselList.get(i);
						String vname = "";
						String voyno = "";
						String varNbr = "";
						String vTerminal = "";
						varNbr = vvobj.getVarNbr();
						vname = vvobj.getVslName();
						voyno = vvobj.getVoyNo();
						vesselListName.add(vname);
						vesselListVoyNo.add(voyno);
						vesselListVarNbr.add(varNbr);
						vesselListTerminal.add(vTerminal);
					}
					map.put("VslNameList", vesselListName);
					map.put("VoyNoList", vesselListVoyNo);
					map.put("VarNbrList", vesselListVarNbr);
					map.put("terminalList", vesselListTerminal);
				}
			} else {
				List<VesselVoyValueObject> vesselList = manifestService.getVesselVoyList(coCd, vesselName,
						voyageNumber, terminal, varNo);
				VesselVoyValueObject vvobj = new VesselVoyValueObject();
				String vname = "";
				String voyno = "";
				String varNbr = "";
				String vTerminal = "";
				if (vesselList != null && vesselList.size() > 0) {
					for (int i = 0; i < vesselList.size(); i++) {
						vvobj = (VesselVoyValueObject) vesselList.get(i);
						varNbr = vvobj.getVarNbr();
						vname = vvobj.getVslName();
						voyno = vvobj.getVoyNo();
						vTerminal = vvobj.getTerminal();
						varNumber = varNbr;
						vesselListName.add(vname);
						vesselListVoyNo.add(voyno);
						vesselListVarNbr.add(varNbr);
						vesselListTerminal.add(vTerminal);
					}
					map.put("VslNameList", vesselListName);
					map.put("VoyNoList", vesselListVoyNo);
					map.put("VarNbrList", vesselListVarNbr);
					map.put("terminalList", vesselListTerminal);
				}
			}
			try {
				vslvoy = CommonUtility.deNull(criteria.getPredicates().get("vslvoy"));
				if ((vslvoy == null || "".equalsIgnoreCase(vslvoy))
						&& CommonUtility.deNull(criteria.getPredicates().get("vslNm")) != null) {
					String vsName = (String) CommonUtility.deNull(criteria.getPredicates().get("vslNm"));
					String vsVoyNum = (String) CommonUtility.deNull(criteria.getPredicates().get("vslVoy"));
					vslvoy = vsName + "-" + vsVoyNum;
				}

				vslInvoy = new java.util.StringTokenizer(vslvoy, "-");
				isToInsert = CommonUtility.deNull(criteria.getPredicates().get("Insertion"));
				disp = CommonUtility.deNull(criteria.getPredicates().get("disp"));
				blnum = CommonUtility.deNull(criteria.getPredicates().get("BlNo"));
				seqno = CommonUtility.deNull(criteria.getPredicates().get("SeqNo"));

				if (!"FETCH".equals(isFetch)) {
					varno = CommonUtility.deNull(criteria.getPredicates().get("vslName"));

					if (varno.equals("--Select--")) {
						varno = CommonUtility.deNull(criteria.getPredicates().get("vvcdval"));
						vslInvoy = new StringTokenizer(vslvoy, "--");
					}
					try {
						vslnm = vslInvoy.nextToken().trim();
						invoynbr = vslInvoy.nextToken().trim();
					} catch (Exception e) {
						log.info("Exception MAnifestAdd : ", e);
					}

				} else {
					varno = varNumber;
					vslnm = vesselName;
					invoynbr = voyageNumber;

				}

			} catch (Exception e) {
				log.info("Exception MAnifestAdd : ", e);
				varno = varNumber;
				vslnm = vesselName;
				invoynbr = voyageNumber;
			}

			List<ManifestValueObject> mftaddcrglist = null;
			TopsModel topsModel = new TopsModel();
			if (isToInsert != null && !isToInsert.equals("") && isToInsert.equals("Add")) {
				distype = CommonUtility.deNull(criteria.getPredicates().get("distype"));
				blno = CommonUtility.deNull(criteria.getPredicates().get("BlNo"));
				crgtype = CommonUtility.deNull(criteria.getPredicates().get("crgtype"));
				hscd = CommonUtility.deNull(criteria.getPredicates().get("HsCode"));
				crgdesc = CommonUtility.deNull(criteria.getPredicates().get("CrgDesc"));
				mark = CommonUtility.deNull(criteria.getPredicates().get("Markings"));
				nop = CommonUtility.deNull(criteria.getPredicates().get("NoOfPkgs"));
				gwt = CommonUtility.deNull(criteria.getPredicates().get("Gwt"));
				gvol = CommonUtility.deNull(criteria.getPredicates().get("Msmt"));
				crgstat = CommonUtility.deNull(criteria.getPredicates().get("crgStat"));
				dgind = CommonUtility.deNull(criteria.getPredicates().get("DgInd"));
				stgind = CommonUtility.deNull(criteria.getPredicates().get("StgInd"));
				dopind = CommonUtility.deNull(criteria.getPredicates().get("DOpInd"));
				pkgtype = CommonUtility.deNull(criteria.getPredicates().get("PkgType"));
				coname = CommonUtility.deNull(criteria.getPredicates().get("ConName"));
				// START CR FTZ HSCODE - NS JULY 2024
				conAddr = CommonUtility.deNull(criteria.getPredicates().get("conAddr"));
				notifyParty = CommonUtility.deNull(criteria.getPredicates().get("notifyParty"));
				notifyPartyAddr = CommonUtility.deNull(criteria.getPredicates().get("notifyPartyAddr"));
				placeDel = CommonUtility.deNull(criteria.getPredicates().get("placeDel"));
				placeReceipt = CommonUtility.deNull(criteria.getPredicates().get("placeReceipt"));
				shipperNm = CommonUtility.deNull(criteria.getPredicates().get("shipperNm"));
				shipperAdd = CommonUtility.deNull(criteria.getPredicates().get("shipperAdd"));
				customHsCode = CommonUtility.deNull(criteria.getPredicates().get("customHsCode"));
				hsCodeSize = Integer.valueOf( CommonUtility.deNull(criteria.getPredicates().get("hsCodeSize")));
				
				List<HsCodeDetails> multiHsCodeList = new ArrayList<HsCodeDetails>();
				HsCodeDetails hsCodeDetails = new HsCodeDetails();
				double totalWt = 0;
				double totalVol = 0;
				if (hsCodeSize > 0) {
					for (int i = 0; i < hsCodeSize; i++) {
						hsCodeDetails = new HsCodeDetails();
						hsCodeDetails.setCrgDes(CommonUtility.deNull(criteria.getPredicates().get("CrgDescArr" + i)));
						hsCodeDetails.setHsCode(CommonUtility.deNull(criteria.getPredicates().get("HsCodeArr" + i)));
						hsCodeDetails.setNbrPkgs(CommonUtility.deNull(criteria.getPredicates().get("NoOfPKgsArr" + i)));
						hsCodeDetails.setCustomHsCode(CommonUtility.deNull(criteria.getPredicates().get("customHsCodeArr" + i)));
						hsCodeDetails.setGrossWt(CommonUtility.deNull(criteria.getPredicates().get("gwtArr" + i)));
						if((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))).indexOf("-") == -1) {
							hsCodeDetails.setHsSubCodeFr((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))));					
							hsCodeDetails.setHsSubCodeTo((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))));	
						} else {
							hsCodeDetails.setHsSubCodeFr((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))).split("-")[0]);
							hsCodeDetails.setHsSubCodeTo((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))).split("-")[1]);
						}
						hsCodeDetails.setGrossVol(CommonUtility.deNull(criteria.getPredicates().get("mSmtArr" + i)));
						hsCodeDetails.setHsSubCodeDesc( CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeDescArr" + i)));
						multiHsCodeList.add(hsCodeDetails);

						totalWt += Double.valueOf(CommonUtility.deNull(criteria.getPredicates().get("gwtArr" + i)));
						totalVol += Double.valueOf(CommonUtility.deNull(criteria.getPredicates().get("mSmtArr" + i)));
					}
					
				}
				
				// END CR FTZ HSCODE - NS JULY 2024
				isSplitBl = ( CommonUtility.deNull(criteria.getPredicates().get("isSplitBl")).equalsIgnoreCase("true"))? true : false;
				BlNoRoot = criteria.getPredicates().get("BlNoRoot");
				
				if(isSplitBl) {
					BlNoRoot = BlNoRoot == null ? blno : BlNoRoot;
				}

				
				// Added on 25 June 2014 to trim space
				if (coname != null && !coname.equals("")) {
					coname = coname.trim();
				}
				consigneeCoyCode = CommonUtility.deNull(criteria.getPredicates().get("lstConsignee"));
				tbConsigneeSearch = CommonUtility.deNull(criteria.getPredicates().get("tbConsigneeSearch"));

				if (consigneeCoyCode != null && !consigneeCoyCode.equals("")) {
					if (!consigneeCoyCode.equalsIgnoreCase("OTHERS")) {
						try {

							CompanyValueObject vo = manifestService.getCompanyInfo(consigneeCoyCode);
							coname = vo.getCompanyName();
							if (coname == null || coname.equals("")) {
								errorMessage = ConstantUtil.ErrorMsg_Company_info_not_found;
								return null;
							}

						} catch (Exception e) {
							errorMessage = ConstantUtil.ErrorMsg_finding_company_error;
							return null;
						}
					} else {
						// do nothing. get the co name from parameter passed in
					}

				}
				if (coname == null || coname.equals("")) {
					errorMessage = ConstantUtil.ErrorMsg_Company_info_not_found;
					return null;
				}
				// Consignee Name is null 12 Aug 2015

				if (crgdesc != null && crgdesc.trim().length() > 4000) {
					errorMessage = ConstantUtil.ErrorMsg_Cargo_Desc_size_more_than_4000_characters;
					// errorMessage(request, "Cargo Desc size is more than 200
					// characters!");
					return null;
				}

				pol = CommonUtility.deNull(criteria.getPredicates().get("PortL"));
				pod = CommonUtility.deNull(criteria.getPredicates().get("PortD"));
				pofd = CommonUtility.deNull(criteria.getPredicates().get("PortFD"));
				cntrtype = CommonUtility.deNull(criteria.getPredicates().get("CntrType"));
				cntrsize = CommonUtility.deNull(criteria.getPredicates().get("CntrSize"));
				cntr1 = CommonUtility.deNull(criteria.getPredicates().get("contNo1"));
				cntr2 = CommonUtility.deNull(criteria.getPredicates().get("contNo2"));
				cntr3 = CommonUtility.deNull(criteria.getPredicates().get("contNo3"));
				cntr4 = CommonUtility.deNull(criteria.getPredicates().get("contNo4"));

				// vietnd02 5/11/09
				String amendChargedTo = "";
				String waiveCharge = "";
				String waiveReason = "";
				// Added by thanhnv2::Start

				autParty = CommonUtility.deNull(criteria.getPredicates().get("autParty"));

				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
				hsSubCode = CommonUtility.deNull(criteria.getPredicates().get("hsSubCode"));
				hsSubCodeFr = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeFr"));
				hsSubCodeTo = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeTo"));
				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END

				// Added back end validation 12 Aug 2015 for hs code sub code
				// not null
				if (hsSubCode == null || hsSubCodeFr == null || hsSubCodeTo == null
						|| hsSubCodeFr.trim().equalsIgnoreCase("") || hsSubCode.trim().equalsIgnoreCase("")
						|| hsSubCodeTo.trim().equalsIgnoreCase("")) {
					errorMessage = ConstantUtil.ErrorMsg_HS_Sub_Code_null;
					return null;
				}

				deliveryToEPC = CommonUtility.deNull(criteria.getPredicates().get("deliveryToEPC"));
				
				// Start added validation to check special case character in BL No
				log.info(blno);
				boolean blNoContainSpecialChar = CommonUtility.containSpecialCharacter(blno);
				if(blNoContainSpecialChar) {
					throw new BusinessException("SpecialCharNotValid");
				}
				// end added validation to check special case character in BL No

				if ("JP".equalsIgnoreCase(coCd)) {
					String adviseBy = CommonUtility.deNull(criteria.getPredicates().get("adviseBy"));
					adviseBy = (adviseBy == null) ? "" : adviseBy;

					String adviseDate = CommonUtility.deNull(criteria.getPredicates().get("adviseDate"));
					adviseDate = (adviseDate == null) ? "" : adviseDate;

					String adviseTime = CommonUtility.deNull(criteria.getPredicates().get("adviseTime"));
					adviseTime = (adviseTime == null) ? "" : adviseTime;

					String adviceDttm = adviseDate + "  " + adviseTime;

					String adviseMode = CommonUtility.deNull(criteria.getPredicates().get("adviseMode"));
					adviseMode = (adviseMode == null) ? "" : adviseMode;

					amendChargedTo = CommonUtility.deNull(criteria.getPredicates().get("selectAmendCharged"));
					amendChargedTo = (amendChargedTo == null) ? "" : amendChargedTo;

					waiveCharge = CommonUtility.deNull(criteria.getPredicates().get("waiveCharge"));
					waiveCharge = (waiveCharge == null) ? "" : waiveCharge;

					if ("Y".equalsIgnoreCase(waiveCharge)) {
						waiveReason = CommonUtility.deNull(criteria.getPredicates().get("waiveReason"));
						waiveReason = (waiveReason == null) ? "" : waiveReason;
					}
					
					snoblno = manifestService.MftInsertionForEnhancementHSCode(distype, disp, userId, varno, blno,
							crgtype, hscd, hsSubCodeFr, hsSubCodeTo, crgdesc, mark, nop, gwt, gvol, crgstat, dgind,
							stgind, dopind, pkgtype, coname, consigneeCoyCode, pol, pod, pofd, cntrtype, cntrsize,
							cntr1, cntr2, cntr3, cntr4, autParty, adviseBy, adviceDttm, adviseMode, amendChargedTo,
							waiveCharge, waiveReason, category, deliveryToEPC, userId, selectedCargo, conAddr, notifyParty,
							notifyPartyAddr, placeDel, placeReceipt, shipperNm, shipperAdd, customHsCode, multiHsCodeList, BlNoRoot, isSplitBl); // MCC add
																										// EPC_IND
					log.info("snoblno" + snoblno);
					nobl = new StringTokenizer(snoblno, "-");
					sSeqno = (nobl.nextToken()).trim();
					sBlno = (nobl.nextToken()).trim();
					if ("JP".equalsIgnoreCase(coCd)) {
						if (waiveCharge != null && waiveCharge.equalsIgnoreCase("Y")) {
							log.info("******* OSCAR microservice request start********* ");
							int adviceId = 0;
							adviceId = manifestService.captureWaiverAdviceRequest(sBlno, userId,
									ProcessChargeConst.MF_MADD, false, null, varno, waiveReason);

							try {
								AdminFeeWaiverValueObject waiveVO = manifestService.invokeOscarWaiverRequest(adviceId,
										sBlno, userId, ProcessChargeConst.MF_MADD);
								// Bhuvana enhance oscar waiver call to microservice 10/7/2018
								boolean waiverRequestToOscarSucceed = manifestService
										.sendAdminWaiverRequestToOscar(waiveVO); // Bhuvana call microservice 10/7/2018
								if (!waiverRequestToOscarSucceed) {
									log.info("OSCAR microservice request failed. Update Waiver msg status as Error");
									waiveVO.setWaiverStatus("E");
									waiveVO.setApprovalRemarks("Error in Oscar service call");
									waiveVO.setAdviceId(String.valueOf(adviceId));
									try {
										manifestService.updateWaiverAdvice(waiveVO, waiveVO.getCreateUserId());
									} catch (Exception e) {
										log.info("Exception MAnifestAdd : ", e);
									}
								}
							} catch (Exception e) {
								log.info("Exception MAnifestAdd : ", e);
							}
						} else {
							// if waiver charge is No insert into misc events
							// here
							log.info("MADD Waiver charge value is No.. insert into misc events");
							manifestService.insertMiscEvtLog(ProcessChargeConst.MF_MADD, varno, sBlno, userId);
						}

					}

				} else {
					// non-jp
					// <cfg 01.sep.08 use overloaded MftInsertion() with
					// distype>
					autParty = coCd;
					snoblno = manifestService.MftInsertion(distype, disp, userId, varno, blno, crgtype, hscd,
							hsSubCodeFr, hsSubCodeTo, crgdesc, mark, nop, gwt, gvol, crgstat, dgind, stgind, dopind,
							pkgtype, coname, consigneeCoyCode, pol, pod, pofd, cntrtype, cntrsize, cntr1, cntr2, cntr3,
							cntr4, autParty, category, deliveryToEPC, userId, selectedCargo,conAddr, notifyParty,
							notifyPartyAddr, placeDel, placeReceipt, shipperNm, shipperAdd, customHsCode, multiHsCodeList,
							BlNoRoot, isSplitBl); 
				}

				nobl = new StringTokenizer(snoblno, "-");
				sSeqno = (nobl.nextToken()).trim();
				sBlno = (nobl.nextToken()).trim();

				poln = manifestService.getPortName(pol);
				podn = manifestService.getPortName(pod);
				if (pofd != null && !pofd.equals("")) {
					pofdn = manifestService.getPortName(pofd);
				}
				pkgn = manifestService.getPkgName(pkgtype);
				bedonbrpkgs = manifestService.chkEdonbrPkgs(sSeqno, varno, blno);
				bnbredopkgs = manifestService.chkNbrEdopkgs(sSeqno, varno, blno);
				if (bedonbrpkgs) {
					edonbrpkgs = "1";
				} else {
					edonbrpkgs = "0";
				}
				bvslstat = manifestService.chkVslStat(varno);
				if (bvslstat) {
					vslstat = "closed";
				} else {
					vslstat = "notclosed";
				}

				bdnbrpkgs = manifestService.chkDNnbrPkgs(sSeqno, varno, blno);
				btnbrpkgs = manifestService.chkTnbrPkgs(sSeqno, varno, blno);
				btdnbrpkgs = manifestService.chkTDNnbrPkgs(sSeqno, varno, blno);
				if (bdnbrpkgs) {
					dnbrpkgs = "1";
				} else {
					dnbrpkgs = "0";
				}

				if (btnbrpkgs) {
					tnbrpkgs = "1";
				} else {
					tnbrpkgs = "0";
				}

				if (btdnbrpkgs) {
					tdnbrpkgs = "1";
				} else {
					tdnbrpkgs = "0";
				}
				poln = poln == null ? "" : poln;
				podn = podn == null ? "" : podn;
				pofdn = pofdn == null ? "" : pofdn;
				pkgn = pkgn == null ? "" : pkgn;
				sSeqno = sSeqno == null ? "" : sSeqno;
				edonbrpkgs = edonbrpkgs == null ? "" : edonbrpkgs;
				vslstat = vslstat == null ? "" : vslstat;
				coCd = coCd == null ? "" : coCd;
				dnbrpkgs = dnbrpkgs == null ? "" : dnbrpkgs;
				tnbrpkgs = tnbrpkgs == null ? "" : tnbrpkgs;
				tdnbrpkgs = tdnbrpkgs == null ? "" : tdnbrpkgs;
				sBlno = sBlno == null ? "" : sBlno;
				map.put("poln", poln);
				map.put("podn", podn);
				map.put("pofdn", pofdn);
				map.put("pkgn", pkgn);
				map.put("sSeqno", sSeqno);
				map.put("edonbrpkgs", edonbrpkgs);
				map.put("vslstat", vslstat);
				map.put("usrtyp", coCd);
				map.put("dnbrpkgs", dnbrpkgs);
				map.put("tnbrpkgs", tnbrpkgs);
				map.put("tdnbrpkgs", tdnbrpkgs);
				map.put("sBlno", sBlno);
				map.put("nbredopkgs", "" + bnbredopkgs);
				map.put("clbjind", "" + manifestService.getClBjInd(sSeqno));
				// start::Added by vietnd02 10-11-09 to get data after insert
				ManifestValueObject mftvobj = new ManifestValueObject();
				mftvobj = manifestService.mftRetrieve(sBlno, varno, sSeqno);
				// createCustCd
				if (mftvobj.getCreateCustCd() == null)
					mftvobj.setCreateCustCd("");
				map.put("mftretrieve", mftvobj);
				// end::vietnd02
				map.put("submitNext", "NO");

				// Added by Punitha on 11/11/2009
				// To bring the values into the jsp screen if the user click
				// 'Submit Next' button

				if (CommonUtility.deNull(criteria.getPredicates().get("contNo4")).equalsIgnoreCase("SubmitNext")) {

					map.put("submitNext", "YES");
					map.put("vesselName", vslnm);
					// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END
					mftaddcrglist = manifestService.getAddcrgList();
					if (mftaddcrglist == null) {
						return null;
					}
					if (mftaddcrglist.size() == 0) {
						errorMessage = ConstantUtil.ErrorMsg_Error_in_Cargo_List;
						return null;
					}
					for (int i = 0; i < mftaddcrglist.size(); i++) {
						ManifestValueObject mftObj = new ManifestValueObject();
						mftObj = (ManifestValueObject) mftaddcrglist.get(i);
						topsModel.put(mftObj);
					}
					map.put("mftaddcrglist", topsModel);
				}
				
				// Start for OLBL multiHS - NS Oct 2024
				if (disp != null && !disp.equals("") && (disp.equals("Add") || disp.equals("AddOl"))) {
					
					List<HsCodeDetails> hscodeDetailsList = manifestService.getHsCodeDetailList(sSeqno);
					map.put("hscodeDetailsList", hscodeDetailsList);
					
					map.put("conAddr", conAddr);
					map.put("notifyParty", notifyParty);
					map.put("notifyPartyAddr", notifyPartyAddr);
					map.put("placeDel", placeDel);
					map.put("placeReceipt", placeReceipt);
					map.put("shipperNm", shipperNm);
					map.put("shipperAdd", shipperAdd);
					map.put("customHsCode", customHsCode);
				}
				// End for OLBL multiHS - NS Oct 2024
			} else if (disp != null && !disp.equals("") && disp.equals("Display")) {
				if ("".equalsIgnoreCase(varno) || varno == null) {
					varno = criteria.getPredicates().get("varnbr2");
				}
				ManifestValueObject mftvobj = new ManifestValueObject();
				// varno = "0700005140";
				mftvobj = manifestService.mftRetrieve(blnum, varno, seqno);
				List<HsCodeDetails> hscodeDetailsList = manifestService.getHsCodeDetailList(seqno);
				bnbredopkgs = manifestService.chkNbrEdopkgs(seqno, varno, blnum);
				// added by Vinayak on 13 Feb 2004 : to fix the view Manifest
				bvslstat = manifestService.chkVslStat(varno);
				if (bvslstat) {
					vslstat = "closed";
				} else {
					vslstat = "notclosed";
				}
				map.put("vslstat", vslstat);
				mftStat = manifestService.isManClose(varno);
				if (mftStat) {
					strMftStat = "closed";
				} else {
					strMftStat = "notclosed";
				}
				if (mftvobj.getAdviseDate() == null)
					mftvobj.setAdviseDate("");
				// createCustCd
				if (mftvobj.getCreateCustCd() == null)
					mftvobj.setCreateCustCd("");
				map.put("mftStat", strMftStat);
				// end added by Vinayak on 13 Feb 2004
				map.put("mftdisp", "Display");
				map.put("mftretrieve", mftvobj);
				map.put("hscodeDetailsList", hscodeDetailsList);
				blnum = blnum == null ? "" : blnum;
				map.put("blnum", blnum);
				map.put("usrtyp", coCd);
				map.put("nbredopkgs", "" + bnbredopkgs);
				map.put("clbjind", "" + manifestService.getClBjInd(seqno));

				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
				List<String> hsCodeList = new ArrayList<String>();

				List<ManifestValueObject> listManifestValueObject = manifestService.getHSCodeList("1","");
				if (listManifestValueObject != null && listManifestValueObject.size() > 0) {
					String hsCode = "";
					for (int i = 0; i < listManifestValueObject.size(); i++) {
						ManifestValueObject manifestValueObject = new ManifestValueObject();
						manifestValueObject = (ManifestValueObject) listManifestValueObject.get(i);
						hsCode = manifestValueObject.getHsCode();
						hsCodeList.add(hsCode);
					}
				}
				map.put("hsCodeList", hsCodeList);
			} else if (disp != null && !disp.equals("") && (disp.equals("Add") || disp.equals("AddOl"))) {
				// START - #39699 : CR TO VALIDATE CLOSE LCT - NS JUNE 2024
				if(manifestService.checkCloseLCT(varNo)) {
					throw new BusinessException(ConstantUtil.vesselCloseLCT);
				}
				// END - #39699 : CR TO VALIDATE CLOSE LCT - NS JUNE 2024
				mftaddcrglist = manifestService.getAddcrgList();
				if (mftaddcrglist == null) {
					errorMessage = ConstantUtil.ErrorMsg_Error_in_Cargo_List;
					return null;
				}
				if (mftaddcrglist.size() == 0) {
					errorMessage = ConstantUtil.ErrorMsg_Error_in_Cargo_List;
					return null;
				}

				for (int i = 0; i < mftaddcrglist.size(); i++) {
					ManifestValueObject mftObj = new ManifestValueObject();
					mftObj = (ManifestValueObject) mftaddcrglist.get(i);
					
					topsModel.put(mftObj);
				}
				
				
				List<HsCodeDetails> hscodeDetailsList = manifestService.getHsCodeDetailList(seqno);
				map.put("hscodeDetailsList", hscodeDetailsList);
				
				map.put("conAddr", conAddr);
				map.put("notifyParty", notifyParty);
				map.put("notifyPartyAddr", notifyPartyAddr);
				map.put("placeDel", placeDel);
				map.put("placeReceipt", placeReceipt);
				map.put("shipperNm", shipperNm);
				map.put("shipperAdd", shipperAdd);
				map.put("customHsCode", customHsCode);
				
				isSplitBl = ( CommonUtility.deNull(criteria.getPredicates().get("isSplitBl")).equalsIgnoreCase("true"))? true : false;
				
			}

			map.put("fromEDO", fromEDO);
			// start SubmitNext
			map.put("distype", distype);
			map.put("usrtyp", coCd);
			map.put("BlNo", blno);
			map.put("isSplitBl", isSplitBl);
			
			if(isSplitBl) {
				// generate new BL for splitted BL				
				map.put("splilBl", manifestService.generateSplitBl(BlNoRoot, varno)); 
				map.put("BlNoRoot", BlNoRoot);
				submitNextCommand = ( CommonUtility.deNull(criteria.getPredicates().get("submitNextCommand")).equalsIgnoreCase("SubmitNext"))? "YES" : "No";			
				map.put("submitNext", submitNextCommand);
				
			}
			map.put("crgtype", crgtype);
			map.put("HsCode", hscd);
			map.put("CrgDesc", crgdesc);
			map.put("Markings", mark);
			map.put("NoOfPkgs", nop);
			map.put("Gwt", gwt);
			map.put("Msmt", gvol);
			map.put("crgStat", crgstat);
			map.put("DgInd", dgind);
			map.put("StgInd", stgind);
			map.put("DOpInd", dopind);
			map.put("PkgType", pkgtype);

			map.put("ConName", coname);

			map.put("lstConsignee", consigneeCoyCode);
			map.put("tbConsigneeSearch", tbConsigneeSearch);
			map.put("PortL", pol);
			map.put("PortD", pod);
			map.put("PortFD", pofd);
			cntrtype = cntrtype == null ? "" : cntrtype;
			map.put("CntrType", cntrtype);
			map.put("CntrSize", cntrsize);
			map.put("contNo1", cntr1);
			map.put("contNo2", cntr2);
			map.put("contNo3", cntr3);
			map.put("contNo4", cntr4);
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			map.put("hsSubCodeFr", hsSubCodeFr);
			map.put("hsSubCodeTo", hsSubCodeTo);
			map.put("hsSubCode", hsSubCode);
			String hsSubCodeDesc = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeDesc"));
			map.put("hsSubCodeDesc", hsSubCodeDesc);
			// end SubmitNext
			map.put("blnum", blnum);
			map.put("deliveryToEPC", deliveryToEPC);
			map.put("ConName", coname);
			map.put("lstConsignee", consigneeCoyCode);
			map.put("tbConsigneeSearch", tbConsigneeSearch);

			map.put("vesselVoy", (vslnm + "-" + invoynbr));
			map.put("varNo", varno);

			map.put("terminal", criteria.getPredicates().get("terminal"));
			// START GET SCHEME IF VALUE NULL - NS FEB 2024
			map.put("scheme", CommonUtil.deNull(criteria.getPredicates().get("scheme")).isEmpty() ?  manifestService.getSchemeName(varno) : criteria.getPredicates().get("scheme"));
			// END GET SCHEME IF VALUE NULL - NS FEB 2024

			map.put("usrtyp", coCd);
			map.put("vesselName",vslnm);
			String vslType = manifestService.getVesselTypeByVslNm(vslnm);
			map.put("vslType", vslType);
			map.put("Voy", invoynbr);
			// HaiTTH1 added on 6/1/2014
			String arrival = criteria.getPredicates().get("hiddenArrival");
			map.put("arrival", arrival);
			String departure = criteria.getPredicates().get("hiddenDeparture");
			map.put("departure", departure);

			map.put("mftaddcrglist", topsModel);
			List<String> crgcdlist = new ArrayList<String>();
			List<String> crgdesclist = new ArrayList<String>();

			if ((topsModel.getSize()) != 0) {
				for (int i = 0; i < topsModel.getSize(); i++) {
					ManifestValueObject mftvobj = new ManifestValueObject();
					String crgcd = "";
					String crg_desc = "";
					mftvobj = (ManifestValueObject) topsModel.get(i);
					crgcd = mftvobj.getCrgType();
					crgdesc = mftvobj.getCrgDesc();
					crgcdlist.add(crgcd);
					crgdesclist.add(crg_desc);
				}

			} else {
				log.info("TopsModel return 0 results to servlet");
			}

			map.put("CrgCdList", crgcdlist);
			map.put("CrgDescList", crgdesclist);
			List<VesselVoyValueObject> vsNmVoyList = manifestService.getVsNmVoy(varno);
			VesselVoyValueObject vvvObj = new VesselVoyValueObject();
			String vesselName1 = "";
			String voyageNumber1 = "";
			if (vsNmVoyList.size() > 0) {
				vvvObj = (VesselVoyValueObject) vsNmVoyList.get(0);
			}

			if (vvvObj != null) {
				vesselName1 = (String) vvvObj.getVslName();
				voyageNumber1 = (String) vvvObj.getVoyNo();
			}

			boolean userDBVessel = manifestService.getUserAdminVessel(coCd, vesselName1, voyageNumber1, blnum);
			String userIdDBVessel = "FALSE";
			if (userDBVessel == true) {
				userIdDBVessel = "TRUE";
			}

			map.put("USERIDVESSEL", userIdDBVessel);
			// 19.10.2009: end decentralization
			// Added by thanhnv2::Start
			List<AccountValueObject> arrList = null;
			if ("JP".equals(coCd)) {
				List<AccessCompanyValueObject> autPartyList = new ArrayList<AccessCompanyValueObject>();
				autPartyList = manifestService.getAutPartyListOfVessel(varno);
				List<String> comCd = new ArrayList<String>();
				List<String> coNm = new ArrayList<String>();
				for (int i = 0; i < autPartyList.size(); i++) {
					comCd.add(((AccessCompanyValueObject) autPartyList.get(i)).getCompanyCode());
					coNm.add(((AccessCompanyValueObject) autPartyList.get(i)).getCompanyName());
				}
				map.put("companyCode", comCd);
				map.put("companyName", coNm);
				String createCust = "";
				createCust = manifestService.getCreateCustCdOfVessel(varno) != null
						? manifestService.getCreateCustCdOfVessel(varno)
						: createCust;
				map.put("createCust", createCust);
				String now = "";
				now = CommonUtility.parseDateToFmtStr(new java.util.Date());
				map.put("now", now);
				arrList = manifestService.getListAmendmentChargedTo(varno);

				List<String> ar1 = new ArrayList<String>();
				List<String> ar2 = new ArrayList<String>();
				for (int i = 0; i < arrList.size(); i++) {
					ar1.add(((AccountValueObject) arrList.get(i)).getAccountNumber());
					ar2.add(((AccountValueObject) arrList.get(i)).getCustomerCode());
				}
				map.put("accountNumber", ar1);
				map.put("customerCode", ar2);
			}

			// Added by thanhnv2::End

			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			List<String> hsCodeList = new ArrayList<String>();

			List<ManifestValueObject> listManifestValueObject = manifestService.getHSCodeList("1","");
			if (listManifestValueObject != null && listManifestValueObject.size() > 0) {
				String hsCode = "";
				for (int i = 0; i < listManifestValueObject.size(); i++) {
					ManifestValueObject manifestValueObject = new ManifestValueObject();
					manifestValueObject = (ManifestValueObject) listManifestValueObject.get(i);
					hsCode = manifestValueObject.getHsCode();
					hsCodeList.add(hsCode);
				}
			}
			map.put("hsCodeList", hsCodeList);

			boolean checkResult = manifestService.checkDisbaleOverSideFroDPE(varno);
			String hiddenOverSide = "FALSE";
			if (!StringUtils.equalsIgnoreCase("JP", coCd) && checkResult) {
				hiddenOverSide = "TRUE";
			}
			
			//if ("".equals(map.get("VesselName")) || map.get("VesselName") == null) {
			//	map.put("VesselName", CommonUtility.deNull((String) map.get("vslName2009")).trim().toUpperCase());
			//	map.put("Voy", CommonUtility.deNull((String) map.get("vslName2009")).trim().toUpperCase());
			//}
			map.put("hiddenOverSide", hiddenOverSide);
			// Added by ZanFeng::end

			// 30/06/2011 PCYAP To check tonnage for manifest/booking/ship store
			int maxCargoTon = manifestService.retrieveMaxCargoTon(varno);
			map.put("maxCargoTon", Integer.valueOf(maxCargoTon));
			map.put("nextRequest", "ManifestInsert");
			map.put("coCD", coCd);
		
			
		} catch (BusinessException e) {
			log.info("Exception MAnifestAdd : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if(errorMessage == null) {
				errorMessage = e.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception MAnifestAdd : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null && errorMessage.length() != 0) {
				Map<String, String> errorMap = new HashMap<String, String>();
				result.setError(errorMessage);
				errorMap.put("error", errorMessage);
				result.setSuccess(false);
				result.setData(errorMap);
			} else {
				result.setSuccess(true);
				result.setData(map);
			}
			log.info("END: MAnifestAdd result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/reportAction")
	public ResponseEntity<?> reportAction(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		List<AccessCompanyValueObject> companyList = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** ReportAction Start criteria :" + criteria.toString());

			String method = CommonUtility.deNull(criteria.getPredicates().get("method"));
			String keyword = CommonUtility.deNull(criteria.getPredicates().get("filter"));
			String startValue = CommonUtility.deNull(criteria.getPredicates().get("start"));
			String limitValue = CommonUtility.deNull(criteria.getPredicates().get("limit"));
			String filterStart = CommonUtility.deNull(criteria.getPredicates().get("filterStart"));
			int start, limit;
			if (!startValue.equals("")) {
				start = Integer.parseInt(startValue);
			} else {
				start = 0;
			}
			if (!limitValue.equals("")) {
				limit = Integer.parseInt(limitValue);
			} else {
				limit = 50;
			}
			if (method.equals("listCompany")) {

				if (!"".equalsIgnoreCase(filterStart) && filterStart != null) {
					companyList = manifestService.listCompanyStart(keyword, start, limit);
				} else {
					companyList = manifestService.listCompany(keyword, start, limit);
				}
			}

			map.put("data", companyList);
			map.put("total", Integer.valueOf(companyList.size()));
		} catch (BusinessException be) {
			log.info("Exception ReportAction : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception ReportAction : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: ReportAction result:" + result.toString());
		}

		return ResponseEntityUtil.success(result.toString());

	}

	@PostMapping(value = "/hsSubCodeList")
	public ResponseEntity<?> hsSubCodeList(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		List<HSCode> hsSubCodeLs = new ArrayList<HSCode>();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** HSSubCodeList Start criteria :" + criteria.toString());
			String hsCode = CommonUtility.deNull(criteria.getPredicates().get("hsCode"));
			hsSubCodeLs = manifestService.getHSSubCodeList(hsCode);
			map.put("hsSubCodeLs", hsSubCodeLs);
		} catch (BusinessException be) {
			log.info("Exception HSSubCodeList : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception HSSubCodeList : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: HSSubCodeList result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/packagingList")
	public ResponseEntity<?> packagingList(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		List<String> pkgcdlist = new ArrayList<String>();
		List<String> pkgnmlist = new ArrayList<String>();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** PackagingList Start criteria :" + criteria.toString());
			String screen = CommonUtility.deNull(criteria.getPredicates().get("screen"));
			if (screen.equals("Manifest")) {// Manifest

				List<ManifestValueObject> pkglist = new ArrayList<ManifestValueObject>();
				pkglist = manifestService.getPkgList();
				for (int i = 0; i < pkglist.size(); i++) {
					ManifestValueObject mftvObj = new ManifestValueObject();
					mftvObj = (ManifestValueObject) pkglist.get(i);
					pkgcdlist.add((String) mftvObj.getPkgType());
					pkgnmlist.add((String) mftvObj.getPkgn());
				}
			} else {
				String getText = CommonUtility.deNull(criteria.getPredicates().get("getText"));

				List<EsnListValueObject> pkglist = new ArrayList<EsnListValueObject>();
				if (getText != null && !getText.equals("")) {
					pkglist = manifestService.getPkgList(getText);
				}
				for (int i = 0; i < pkglist.size(); i++) {
					EsnListValueObject esnValueObject = new EsnListValueObject();
					esnValueObject = (EsnListValueObject) pkglist.get(i);
					pkgcdlist.add((String) esnValueObject.getPkgType());
					pkgnmlist.add((String) esnValueObject.getPkgDesc());
				}

			}
			map.put("pkgcdlist", pkgcdlist);
			map.put("pkgnmlist", pkgnmlist);
		} catch (BusinessException be) {
			log.info("Exception PackagingList : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception PackagingList : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: PackagingList result:" + result.toString());
		}

		return ResponseEntityUtil.success(result.toString());

	}

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/portList")
	public ResponseEntity<?> portList(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		List<ManifestValueObject> portlist = new ArrayList<ManifestValueObject>();
		List<ManifestValueObject> newList = new ArrayList<ManifestValueObject>();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** PortList Start criteria :" + criteria.toString());
			String screen = CommonUtility.deNull(criteria.getPredicates().get("screen"));
			int pageNo = Integer
					.parseInt(((CommonUtility.deNull(criteria.getPredicates().get("PageIndex"))).equals("") ? "1"
							: CommonUtility.deNull(criteria.getPredicates().get("PageIndex"))));
			int recPerPage = Integer
					.parseInt(((CommonUtility.deNull(criteria.getPredicates().get("RowOfPage"))).equals("") ? "10"
							: CommonUtility.deNull(criteria.getPredicates().get("RowOfPage"))));
			String pCode = CommonUtility.deNull(criteria.getPredicates().get("pCode"));
			String pDesc = CommonUtility.deNull(criteria.getPredicates().get("pDesc"));
			int numOfPage = 0;

			if (screen.equals("Manifest")) {// Manifest

				if (pCode != null || pDesc != null) {
					portlist = manifestService.getPortList(pCode, pDesc);
				} else {
					portlist = manifestService.getPortList();
				}

			} else // production Temp fix for other(other than manifest) screens under GC
			{
				if (pCode != null || pDesc != null) {
					portlist = manifestService.getPortList(pCode, pDesc);
				} else {
					portlist = manifestService.getPortList();
				}
			}
			// Paging
			RecordPaging recPg = new RecordPaging();
			if (portlist.size() > 0) {
				numOfPage = recPg.createRecordPagingCache("ManifestPortList", portlist, recPerPage);
				if (pageNo > numOfPage) {
					pageNo = numOfPage;
				}
				newList = recPg.getRecordsPage("ManifestPortList", pageNo);
			} else {
				newList = portlist;
			}

			// Display
			List<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();
			if (screen.equals("Manifest")) {
				HashMap<String, String> hMap;
				ManifestValueObject mftvObj;
				for (int i = 0; i < newList.size(); i++) {
					mftvObj = new ManifestValueObject();
					hMap = new HashMap<String, String>();
					mftvObj = (ManifestValueObject) newList.get(i);
					hMap.put("pCode", (String) mftvObj.getPortL());
					hMap.put("pDesc", (String) mftvObj.getPortLn());
					listData.add(hMap);
				}
			} else {
				HashMap<String, String> hMap;
				ManifestValueObject mftvObj;
				for (int i = 0; i < newList.size(); i++) {
					hMap = new HashMap<String, String>();
					mftvObj = new ManifestValueObject();
					mftvObj = (ManifestValueObject) newList.get(i);
					hMap.put("pCode", (String) mftvObj.getPortL());
					hMap.put("pDesc", (String) mftvObj.getPortLn());
					listData.add(hMap);
				}
			}

			map.put("portlist", listData);
			map.put("total", portlist.size());
		} catch (BusinessException be) {
			log.info("Exception PortList : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception PortList : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: PortList result:" + result.toString());
		}

		return ResponseEntityUtil.success(result.toString());

	}

	@PostMapping(value = "/manifestAmend")
	public ResponseEntity<?> manifestAmend(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START:manifestAmend criteria:" + criteria.toString());
			String varno = "";
			String vslvoy = "";
			StringTokenizer vslInvoy = null;
			// For Display
			String disp = "";
			String blnum = "";
			// For Updation
			String sSeqno = "";
			String blno = "";
			String crgtype = "";
			String hscd = "";
			String crgdesc = "";
			String mark = "";
			String nop = "";
			String gwt = "";
			String gvol = "";
			String crgstat = "";
			String dgind = "";
			String stgind = "";
			String dopind = "";
			String pkgtype = "";
			String coname = "";
			String consigneeCoyCode = "";
			String pol = "";
			String pod = "";
			String pofd = "";
			String cntrtype = "";
			String cntrsize = "";
			String cntr1 = "";
			String cntr2 = "";
			String cntr3 = "";
			String cntr4 = "";
			String seqno = "";
			String poln = "";
			String podn = "";
			String pofdn = "";
			String pkgn = "";
			String edonbrpkgs = "";
			boolean bedonbrpkgs = false;
			boolean bvslstat = false;
			String vslstat = "";
			// MCC for EPC_IND
			String deliveryToEPC = "";
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
			String hsSubCode = "";
			String hsSubCodeFr = "";
			String hsSubCodeTo = "";
			// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END
			// for assign bill laks 5 jul
			String schval = "";
			String schind = "";
			String sacctnbr = "";
			List<String> vsactno = new ArrayList<String>();
			List<ManifestValueObject> vabactno = new ArrayList<ManifestValueObject>();
			// for assign bill laks 5 jul
			boolean bdnbrpkgs = false;
			String dnbrpkgs = "";
			boolean btnbrpkgs = false;
			String tnbrpkgs = "";
			boolean btdnbrpkgs = false;
			String tdnbrpkgs = "";
			boolean bnbredopkgs = false;
			String selectedCargo = "";
			List<ManifestCargoValueObject> manifestcargo = new ArrayList<ManifestCargoValueObject>();
			// Vector manifestcheck = new Vector(0,1);
			String manifestcheck = "";
//			ModelManager modelManager = getModelManager();
			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			List<Map<String, String>> list = manifestService.getCategoryList();
			map.put("categoryList", list);
			String category = "";
			// END amended by Maksym JCMS Smart CR 6.10
			if (!StringUtils.isEmpty(CommonUtility.deNull(criteria.getPredicates().get("category")))) {
				// added by Deng Zhengguo 18/5/2011. Amended by Dongsheng on
				// 16/2/2012. This is the right place to put
				category = CommonUtility.deNull(criteria.getPredicates().get("category"));
				// end add
				String categoryValue = manifestService.getCategoryValue(category);
				map.put("categoryValue", categoryValue);
				// Commented by Dongsheng on 16/1/2012. The statement below
				// should be shifted up before String categoryValue= ...
				// category = CommonUtility.deNull(criteria.getPredicates().get("category");
			}
			// added by Deng Zhengguo 18/5/2011
			map.put("category", category);
			// end add
			// Added by Satish on Mar 16 2004 : Rectified hypen vessel & special
			// char in text area : SL-GBMS-20040306-1
			List<String> vesselListName = new ArrayList<String>();
			List<String> vesselListVoyNo = new ArrayList<String>();
			List<String> vesselListVarNbr = new ArrayList<String>();
			List<String> vesselListTerminal = new ArrayList<String>();
			List<VesselVoyValueObject> vesselList = manifestService.getVesselVoy(coCd);
			VesselVoyValueObject vvobj = new VesselVoyValueObject();
			String vesselName = ""; // 20.10.2009 Added by FPT
			String voyageNumber = ""; // 20.10.2009 Added by FPT
			if (vesselList != null && vesselList.size() > 0) {
				for (int i = 0; i < vesselList.size(); i++) {
					vvobj = (VesselVoyValueObject) vesselList.get(i);
					String vname = "";
					String voyno = "";
					String varNbr = "";
					String vTerminal = "";
					varNbr = vvobj.getVarNbr();
					vname = vvobj.getVslName();
					voyno = vvobj.getVoyNo();
					vTerminal = vvobj.getTerminal();
					vesselListName.add(vname);
					vesselListVoyNo.add(voyno);
					vesselListVarNbr.add(varNbr);
					vesselListTerminal.add(vTerminal);
				}
				map.put("VslNameList", vesselListName);
				map.put("VoyNoList", vesselListVoyNo);
				map.put("VarNbrList", vesselListVarNbr);
				map.put("VTerminalList", vesselListTerminal);
			}
			// End added by Satish
			if ("".equals(vesselName) || vesselName == null) {
				vesselName = CommonUtility.deNull((String) CommonUtility
						.deNull(criteria.getPredicates().get("vslName2009")).trim().toUpperCase());
				voyageNumber = CommonUtility.deNull(
						(String) CommonUtility.deNull(criteria.getPredicates().get("vslvoy2009")).trim().toUpperCase());
			}
			varno = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
			vslvoy = CommonUtility.deNull(criteria.getPredicates().get("vslvoy"));
			if (vesselName == null || "".equalsIgnoreCase(vesselName)) {
				vesselName = (String) CommonUtility.deNull(criteria.getPredicates().get("vslNm"));
				voyageNumber = (String) CommonUtility.deNull(criteria.getPredicates().get("vslVoy"));
				vslvoy = vesselName + "-" + voyageNumber;
			}
			vslInvoy = new java.util.StringTokenizer(vslvoy, "-");
			disp = CommonUtility.deNull(criteria.getPredicates().get("amend"));
			blnum = CommonUtility.deNull(criteria.getPredicates().get("BlNo"));
			seqno = CommonUtility.deNull(criteria.getPredicates().get("seqno"));
			List<ManifestValueObject> mftaddcrglist = null;
			TopsModel topsModel = new TopsModel();
			if (vesselName == null || "".equalsIgnoreCase(vesselName)) {
//							vesselName = (vslInvoy.nextToken()).trim();
//							voyageNumber = (vslInvoy.nextToken()).trim();
				if (!"-".equalsIgnoreCase(vslvoy)) {
					String[] parts = vslvoy.split("-");
					vesselName = parts[0].trim();
					voyageNumber = parts[1].trim();
				}
			}
			map.put("vesselName", vesselName); // 20.10.2009 Updated by FPT
			map.put("Voy", voyageNumber); // 20.10.2009 Updated by FPT
			map.put("vesselVoy", vslvoy);
			map.put("varNo", varno);
			String vslType = manifestService.getVesselTypeByVslNm(vesselName);
			map.put("vslType", vslType);
			// map.put("usrtyp",coCd);

			// 19.10.2009 : start decentralization(tienlc) for GB CR
			List<VesselVoyValueObject> vsNmVoyList = manifestService.getVsNmVoy(varno);
			VesselVoyValueObject vvvObj = new VesselVoyValueObject();
			String vesselName1 = "";
			String voyageNumber1 = "";
			if (vsNmVoyList.size() > 0)
				vvvObj = (VesselVoyValueObject) vsNmVoyList.get(0);
			if (vvvObj != null) {
				vesselName1 = (String) vvvObj.getVslName();
				voyageNumber1 = (String) vvvObj.getVoyNo();
			}
			boolean userDBVessel = manifestService.getUserAdminVessel(coCd, vesselName1, voyageNumber1, blnum);
			String userIdDBVessel = "FALSE";
			if (userDBVessel == true) {
				userIdDBVessel = "TRUE";
			}
			map.put("coCd", coCd);
			map.put("USERIDVESSEL", userIdDBVessel);
			if (disp != null && !disp.equals("") && disp.equals("Amend")) {
				blno = CommonUtility.deNull(criteria.getPredicates().get("BlNo"));
				crgtype = CommonUtility.deNull(criteria.getPredicates().get("crgtype"));
				hscd = CommonUtility.deNull(criteria.getPredicates().get("HsCode"));
				crgdesc = CommonUtility.deNull(criteria.getPredicates().get("CrgDesc"));
				mark = CommonUtility.deNull(criteria.getPredicates().get("Markings"));
				nop = CommonUtility.deNull(criteria.getPredicates().get("NoOfPkgs"));
				gwt = CommonUtility.deNull(criteria.getPredicates().get("Gwt"));
				gvol = CommonUtility.deNull(criteria.getPredicates().get("Msmt"));
				crgstat = CommonUtility.deNull(criteria.getPredicates().get("crgStat"));
				dgind = CommonUtility.deNull(criteria.getPredicates().get("DgInd"));
				stgind = CommonUtility.deNull(criteria.getPredicates().get("StgInd"));
				dopind = CommonUtility.deNull(criteria.getPredicates().get("DOpInd"));
				pkgtype = CommonUtility.deNull(criteria.getPredicates().get("PkgType"));
				coname = CommonUtility.deNull(criteria.getPredicates().get("ConName"));
				if (CommonUtility.deNull(category) != "" && CommonUtility.deNull(crgtype) != "") {

				}
				if (coname != null && !coname.equals("")) {
					coname = coname.trim();
				}
				consigneeCoyCode = CommonUtility.deNull(criteria.getPredicates().get("lstConsignee"));
				// To get company name others name
				// Consignee Name is null 12 Aug 2015
				if (consigneeCoyCode != null && !consigneeCoyCode.equals("")) {
					if (!consigneeCoyCode.equalsIgnoreCase("OTHERS")) {
						try {

							CompanyValueObject vo = manifestService.getCompanyInfo(consigneeCoyCode);
							coname = vo.getCompanyName();
							if (coname == null || coname.equals("")) {
								errorMessage = ConstantUtil.ErrorMsg_Company_info_not_found;
								return null;
							}
						} catch (Exception e) {
							errorMessage = ConstantUtil.ErrorMsg_finding_company_error;
							return null;
						}
					} else {
					}
				}
				if (coname == null || coname.equals("")) {
					errorMessage = ConstantUtil.ErrorMsg_Company_info_not_found;
					return null;
				}
				if (crgdesc != null && crgdesc.trim().length() > 4000) {
					errorMessage = ConstantUtil.ErrorMsg_Cargo_Desc_size_more_than_4000_characters;
					return null;
				}
				pol = CommonUtility.deNull(criteria.getPredicates().get("PortL"));
				pod = CommonUtility.deNull(criteria.getPredicates().get("PortD"));
				pofd = CommonUtility.deNull(criteria.getPredicates().get("PortFD"));
				cntrtype = CommonUtility.deNull(criteria.getPredicates().get("CntrType"));
				cntrsize = CommonUtility.deNull(criteria.getPredicates().get("CntrSize"));
				cntr1 = CommonUtility.deNull(criteria.getPredicates().get("contNo1"));
				cntr2 = CommonUtility.deNull(criteria.getPredicates().get("contNo2"));
				cntr3 = CommonUtility.deNull(criteria.getPredicates().get("contNo3"));
				cntr4 = CommonUtility.deNull(criteria.getPredicates().get("contNo4"));
				seqno = CommonUtility.deNull(criteria.getPredicates().get("seqno"));
				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
				hsSubCode = CommonUtility.deNull(criteria.getPredicates().get("hsSubCode"));
				hsSubCodeFr = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeFr"));
				hsSubCodeTo = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeTo"));
				selectedCargo = CommonUtility.deNull(criteria.getPredicates().get("selectedCargo"));

				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END
				// Added back end validation 12 Aug 2015 for hs code sub code not null
				if (hsSubCode == null || hsSubCodeFr == null || hsSubCodeTo == null
						|| hsSubCodeFr.trim().equalsIgnoreCase("") || hsSubCode.trim().equalsIgnoreCase("")
						|| hsSubCodeTo.trim().equalsIgnoreCase("")) {
					errorMessage = ConstantUtil.ErrorMsg_HS_Sub_Code_null;
					return null;
				}
				// End
				// String changeStat =
				// CommonUtility.deNull(criteria.getPredicates().get("ChangeStattus");
				// LogManager.instance.logDebug("ChangeStattus: " + changeStat);
				// Changer by Thanhnv2::Start
				String adviseBy = "";
				String adviseDate = "";
				String adviseTime = "";
				String adviseMode = "";
				String amendChargedTo = "";
				String waiveCharge = "";
				String waiveReason = "";
				String adviceDttm = "";
				String autParty = CommonUtility.deNull(criteria.getPredicates().get("autParty"));
				if ("JP".equalsIgnoreCase(coCd)) {
					// autParty=coCd;
					if (CommonUtility.deNull(criteria.getPredicates().get("adviseBy")) != null) {
						adviseBy = CommonUtility.deNull(criteria.getPredicates().get("adviseBy"));
					}

					if (CommonUtility.deNull(criteria.getPredicates().get("adviseDate")) != null) {
						adviseDate = CommonUtility.deNull(criteria.getPredicates().get("adviseDate"));
					}

					if (CommonUtility.deNull(criteria.getPredicates().get("adviseTime")) != null) {
						adviseTime = CommonUtility.deNull(criteria.getPredicates().get("adviseTime"));
					}

					adviceDttm = adviseDate + adviseTime;

					if (CommonUtility.deNull(criteria.getPredicates().get("adviseMode")) != null) {
						adviseMode = CommonUtility.deNull(criteria.getPredicates().get("adviseMode"));
					}

					if (CommonUtility.deNull(criteria.getPredicates().get("selectAmendCharged")) != null) {
						amendChargedTo = CommonUtility.deNull(criteria.getPredicates().get("selectAmendCharged"));
					}
					//
					if (CommonUtility.deNull(criteria.getPredicates().get("waiveCharge")) != null) {
						waiveCharge = CommonUtility.deNull(criteria.getPredicates().get("waiveCharge"));
						waiveReason = CommonUtility.deNull(criteria.getPredicates().get("waiveReason"));

						waiveReason = (waiveReason == null ? "" : waiveReason);
						if (waiveCharge == "Y") {
							if (CommonUtility.deNull(criteria.getPredicates().get("waiveReason")) != null) {
								waiveReason = CommonUtility.deNull(criteria.getPredicates().get("waiveReason"));
							}
						}
					}
				}
				String closed = CommonUtility.deNull(criteria.getPredicates().get("closed"));
				String mftStat = CommonUtility.deNull(criteria.getPredicates().get("mftStat"));
				String strDNbrPkgs = CommonUtility.deNull(criteria.getPredicates().get("strDNbrPkgs"));
				String strTdnbrpkgs = CommonUtility.deNull(criteria.getPredicates().get("strTdnbrpkgs"));
				
				// START CR FTZ HSCODE - NS JULY 2024
				
				String conAddr = CommonUtility.deNull(criteria.getPredicates().get("conAddr"));
				String notifyParty = CommonUtility.deNull(criteria.getPredicates().get("notifyParty"));
				String notifyPartyAddr = CommonUtility.deNull(criteria.getPredicates().get("notifyPartyAddr"));
				String placeDel = CommonUtility.deNull(criteria.getPredicates().get("placeDel"));
				String placeReceipt = CommonUtility.deNull(criteria.getPredicates().get("placeReceipt"));
				String shipperNm = CommonUtility.deNull(criteria.getPredicates().get("shipperNm"));
				String shipperAdd = CommonUtility.deNull(criteria.getPredicates().get("shipperAdd"));
				String customHsCode = CommonUtility.deNull(criteria.getPredicates().get("customHsCode"));
				int hsCodeSize = Integer.valueOf( CommonUtility.deNull(criteria.getPredicates().get("hsCodeSize")));
				
				List<HsCodeDetails> multiHsCodeList = new ArrayList<HsCodeDetails>();
				HsCodeDetails hsCodeDetails = new HsCodeDetails();
				if (hsCodeSize > 0) {
					for (int i = 0; i < hsCodeSize; i++) {
						hsCodeDetails = new HsCodeDetails();
						hsCodeDetails.setCrgDes(CommonUtility.deNull(criteria.getPredicates().get("CrgDescArr" + i)));
						hsCodeDetails.setHsCode(CommonUtility.deNull(criteria.getPredicates().get("HsCodeArr" + i)));
						hsCodeDetails.setNbrPkgs(CommonUtility.deNull(criteria.getPredicates().get("NoOfPKgsArr" + i)));
						hsCodeDetails.setCustomHsCode(CommonUtility.deNull(criteria.getPredicates().get("customHsCodeArr" + i)));
						hsCodeDetails.setGrossWt(CommonUtility.deNull(criteria.getPredicates().get("gwtArr" + i)));
						if((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))).indexOf("-") == -1) {
							hsCodeDetails.setHsSubCodeFr((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))));	
							hsCodeDetails.setHsSubCodeTo((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))));	
						} else {
							hsCodeDetails.setHsSubCodeFr((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))).split("-")[0]);
							hsCodeDetails.setHsSubCodeTo((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + i))).split("-")[1]);
						}
						hsCodeDetails.setGrossVol(CommonUtility.deNull(criteria.getPredicates().get("mSmtArr" + i)));
						hsCodeDetails.setHsSubCodeDesc( CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeDescArr" + i)));
						hsCodeDetails.setIsHsCodeChange(CommonUtility.deNull(criteria.getPredicates().get("isHsCodeChange" + i)));
						hsCodeDetails.setHscodeSeqNbr(CommonUtility.deNull(criteria.getPredicates().get("hscodeSeqNbr" + i)));
						multiHsCodeList.add(hsCodeDetails);
					}
				}
				
				
				// END CR FTZ HSCODE - NS JULY 2024
				
				// MCC for EPC_IND
				deliveryToEPC = CommonUtility.deNull(criteria.getPredicates().get("deliveryToEPC"));
				map.put("mftStat", mftStat);
				log.info(" MftUpdation to JDBC Start :");
				if ((closed != null && !"".equalsIgnoreCase(closed) && "closed".equalsIgnoreCase(closed))
						|| ("closed".equalsIgnoreCase(mftStat) && !"JP".equalsIgnoreCase(coCd))
						|| !"0".equals(strDNbrPkgs) || !"0".equals(strTdnbrpkgs)) {
					String adviseDateTime = adviseDate + adviseTime;

					sSeqno = manifestService.MftUpdationWhenClosedDPE(UserID, coCd, seqno, varno, blno, crgdesc, mark,
							adviseBy, adviseDateTime, adviseMode, amendChargedTo, waiveCharge, waiveReason, hscd,
							hsSubCodeFr, hsSubCodeTo, coname, consigneeCoyCode, selectedCargo,conAddr, notifyParty,
							notifyPartyAddr, placeDel, placeReceipt, shipperNm, shipperAdd, customHsCode, multiHsCodeList); 

				} else {
 					sSeqno = manifestService.MftUpdationForEnhancementHSCode(UserID, coCd, seqno, varno, blno, crgtype,
							hscd, hsSubCodeFr, hsSubCodeTo, crgdesc, mark, nop, gwt, gvol, crgstat, dgind, stgind,
							dopind, pkgtype, coname, consigneeCoyCode, pol, pod, pofd, cntrtype, cntrsize, cntr1, cntr2,
							cntr3, cntr4, autParty, adviseBy, adviceDttm, adviseMode, amendChargedTo, waiveCharge,
							waiveReason, category, deliveryToEPC, selectedCargo,conAddr, notifyParty,
							notifyPartyAddr, placeDel, placeReceipt, shipperNm, shipperAdd, customHsCode, multiHsCodeList); 
				}
				log.info(" MftUpdation to JDBC Ends - sSeqno : " + sSeqno);
				// MCC for oscar admin fee request
				if ("JP".equalsIgnoreCase(coCd)) {
					if (waiveCharge != null && waiveCharge.equalsIgnoreCase("Y")) {
						log.info("******* OSCAR microservice request start*********  sSeqno:" + sSeqno);
						int adviceId = 0;
						adviceId = manifestService.captureWaiverAdviceRequest(blno, UserID, ProcessChargeConst.MF_MADM,
								false, null, varno, waiveReason); // Fixed. to pass vv_cd
						try {
							AdminFeeWaiverValueObject waiveVO = manifestService.invokeOscarWaiverRequest(adviceId, blno,
									UserID, ProcessChargeConst.MF_MADM);
							// System.setProperty("javax.xml.soap.MessageFactory","weblogic.webservice.core.soap.MessageFactoryImpl");
							// SOAPClientUtilsForAdminFeeWaiver.callOscarWaiverWebService(waiveVO);
							// Bhuvana enhance oscar waiver call to microservice 10/7/2018
							boolean waiverRequestToOscarSucceed = manifestService
									.sendAdminWaiverRequestToOscar(waiveVO); // Bhuvana call microservice 10/7/2018
							if (!waiverRequestToOscarSucceed) {
								log.info("OSCAR microservice request failed. Update Waiver msg status as Error");
								waiveVO.setWaiverStatus("E");
								waiveVO.setApprovalRemarks("Error in Oscar service call");
								waiveVO.setAdviceId(String.valueOf(adviceId));
								try {
									manifestService.updateWaiverAdvice(waiveVO, waiveVO.getCreateUserId());
								} catch (BusinessException be) {
									log.info("Exception Manifest Amend : ", be);
									errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
								} catch (Exception e) {
									log.info("Exception Manifest Amend : ", e);
								}
							}
						} catch (BusinessException be) {
							log.info("Exception Manifest Amend : ", be);
							errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
						} catch (Exception e) {
							log.info("Exception Manifest Amend : ", e);
						} finally {
							log.info("******* OSCAR microservice request Ends*********  sSeqno:" + sSeqno);
						}
					} else {
						// if waiver charge is No insert into misc events here
						log.info("******* OSCAR microservice waiver charge is No , So insert into misc events , blno :"
								+ blno + " , sSeqno:" + sSeqno);
						manifestService.insertMiscEvtLog(ProcessChargeConst.MF_MADM, varno, blno, UserID);
					}
				}
				poln = manifestService.getPortName(pol);
				podn = manifestService.getPortName(pod);
				if (pofd != null && !pofd.equals("")) {
					pofdn = manifestService.getPortName(pofd);
				}
				pkgn = manifestService.getPkgName(pkgtype);
				bedonbrpkgs = manifestService.chkEdonbrPkgs(sSeqno, varno, blno);
				bnbredopkgs = manifestService.chkNbrEdopkgs(sSeqno, varno, blno);
				if (bedonbrpkgs) {
					edonbrpkgs = "1";
				} else {
					edonbrpkgs = "0";
				}
				bvslstat = manifestService.chkVslStat(varno);
				if (bvslstat) {
					vslstat = "closed";
				} else {
					vslstat = "notclosed";
				}
				bdnbrpkgs = manifestService.chkDNnbrPkgs(sSeqno, varno, blno);
				btnbrpkgs = manifestService.chkTnbrPkgs(sSeqno, varno, blno);
				btdnbrpkgs = manifestService.chkTDNnbrPkgs(sSeqno, varno, blno);
				if (bdnbrpkgs) {
					dnbrpkgs = "1";
				} else {
					dnbrpkgs = "0";
				}

				if (btnbrpkgs) {
					tnbrpkgs = "1";
				} else {
					tnbrpkgs = "0";
				}

				if (btdnbrpkgs) {
					tdnbrpkgs = "1";
				} else {
					tdnbrpkgs = "0";
				}
				map.put("poln", poln);
				map.put("podn", podn);
				map.put("pofdn", pofdn);
				map.put("pkgn", pkgn);
				map.put("sSeqno", sSeqno);
				map.put("edonbrpkgs", edonbrpkgs);
				map.put("vslstat", vslstat);
				map.put("usrtyp", coCd);
				map.put("tnbrpkgs", tnbrpkgs);
				map.put("sBlno", blno);
				map.put("nbredopkgs", "" + bnbredopkgs);
				map.put("clbjind", "" + manifestService.getClBjInd(sSeqno));
				// added by Irene Tan on 15/07/2004 : SL-GBMS-20040715-2
				ManifestValueObject mftvobj = new ManifestValueObject();
				mftvobj = manifestService.mftRetrieve(blnum, varno, seqno);
				map.put("mftdisp", "Display");
				map.put("mftretrieve", mftvobj);
				map.put("blnum", blnum);
				// end added by Irene Tan on 15/07/2004 : SL-GBMS-20040715-2
				// added laks 5 jul
				schval = manifestService.getScheme(varno);
				schind = manifestService.getSchemeInd(varno);
				map.put("schval", schval);
				map.put("schind", schind);

				// added laks 5 jul
			} /* Changes done on 17 june for mixed scheme indiactor */
			else if (disp != null && !disp.equals("") && disp.equals("Display")) {
				ManifestValueObject mftvobj = new ManifestValueObject();
				mftvobj = manifestService.mftRetrieve(blnum, varno, seqno);
				map.put("mftdisp", "Display");
				map.put("mftretrieve", mftvobj);
				map.put("blnum", blnum);
				map.put("usrtyp", coCd);
				map.put("clbjind", "" + manifestService.getClBjInd(seqno));
				// added laks 5 jul
				schval = manifestService.getScheme(varno);
				schind = manifestService.getSchemeInd(varno);
				map.put("schval", schval);
				map.put("schind", schind);
				String fetchView = CommonUtility.deNull(criteria.getPredicates().get("fetchView"));
				map.put("fetchView", fetchView);
				vesselName = (vslInvoy.nextToken()).trim();
				voyageNumber = (vslInvoy.nextToken()).trim();
				map.put("vesselName", vesselName);
				map.put("voyageNumber", voyageNumber);
				bvslstat = manifestService.chkVslStat(varno);
				if (bvslstat) {
					vslstat = "closed";
				} else {
				}
				map.put("vslstat", vslstat);
				// Added by thanhnv2::Start
				boolean mftStat = manifestService.isManClose(varno);
				// VietNguyen - no need to hceck is Man Close here
				String strMftStat = mftStat ? "closed" : "notclosed";
				map.put("mftStat", strMftStat);
				List<AccountValueObject> arrList = null;
				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
				List<String> hsCodeList = new ArrayList<String>();
				List<ManifestValueObject> listManifestValueObject = manifestService.getHSCodeList("1","");
				if (listManifestValueObject != null && listManifestValueObject.size() > 0) {
					String hsCode = "";
					for (int i = 0; i < listManifestValueObject.size(); i++) {
						ManifestValueObject manifestValueObject = new ManifestValueObject();
						manifestValueObject = (ManifestValueObject) listManifestValueObject.get(i);
						hsCode = manifestValueObject.getHsCode();
						hsCodeList.add(hsCode);
					}
				}
				map.put("hsCodeList", hsCodeList);
				List<HsCodeDetails> hscodeDetailsList = manifestService.getHsCodeDetailList(seqno);
				map.put("hscodeDetailsList", hscodeDetailsList);
				if ("JP".equals(coCd)) {
					List<AccessCompanyValueObject> autPartyList = new ArrayList<AccessCompanyValueObject>();
					autPartyList = manifestService.getAutPartyListOfVessel(varno);
					List<String> comCd = new ArrayList<String>();
					List<String> coNm = new ArrayList<String>();
					for (int i = 0; i < autPartyList.size(); i++) {
						comCd.add(((AccessCompanyValueObject) autPartyList.get(i)).getCompanyCode());
						coNm.add(((AccessCompanyValueObject) autPartyList.get(i)).getCompanyName());
					}
					map.put("companyCode", comCd);
					map.put("companyName", coNm);

					String now = CommonUtility.parseDateToFmtStr(new java.util.Date());
					map.put("now", now);

					arrList = manifestService.getListAmendmentChargedTo(varno);
					List<String> ar1 = new ArrayList<String>();// nbr
					List<String> ar2 = new ArrayList<String>();// custcd
					for (int i = 0; i < arrList.size(); i++) {
						ar1.add(((AccountValueObject) arrList.get(i)).getAccountNumber());
						ar2.add(((AccountValueObject) arrList.get(i)).getCustomerCode());
					}
					map.put("accountNumber", ar1);
					map.put("customerCode", ar2);
				}
				mftvobj = manifestService.mftRetrieve(blnum, varno, seqno);
				String currentCargoType = mftvobj.getCrgType();
				map.put("currentCargoType", currentCargoType);
				Map<String, String> cargoCategoryCode_cargoCategoryName = manifestService
						.getCargoCategoryCode_CargoCategoryName();
				map.put("cargoCategoryCode_cargoCategoryName", cargoCategoryCode_cargoCategoryName);
				List<BookingReferenceValueObject> brvoList = manifestService.getBRVOList("other");
				map.put("brvoList", brvoList);
				String[] currentApplicableCargoCategoryList = new String[] {};
				for (int i = 0; i < brvoList.size(); i++) {
					if (brvoList.get(i).getCargoType().equals(currentCargoType)) {
						currentApplicableCargoCategoryList = brvoList.get(i).getCargoCategory().split(",");
					}
				}
				map.put("currentApplicableCargoCategoryList", currentApplicableCargoCategoryList);
				List<ManifestValueObject> crgTypeVector = manifestService.getCargoType();
				map.put("crgTypeVector", crgTypeVector);
				boolean showAllCargoCategory = manifestService.isShowAllCargoCategoryCode(coCd);
				map.put("showAllCargoCategory", showAllCargoCategory);
				String notShowCargoCategoryCode = manifestService.getNotShowCargoCategoryCode();
				map.put("notShowCargoCategoryCode", notShowCargoCategoryCode);
				String vslCarCarrier = manifestService.getCarCarrierVesselCode();
				map.put("vslCarCarrier", vslCarCarrier);
				String cargoTypeNotShow = manifestService.getCargoTypeNotShow();
				map.put("cargoTypeNotShow", cargoTypeNotShow);
				// END added by Maksym JCMS Smart CR 6.10
				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END
			} else if (disp != null && !disp.equals("") && disp.equals("Assign")) {
				// added laks 5 jul
				map.put("clbjind", "" + manifestService.getClBjInd(seqno));
				vsactno = manifestService.getSAacctno(varno);

				// Modified by SONLT3 on 11/02/2010 to get Acct No. for SA
				if ("JP".equals(coCd)) {
					vabactno = manifestService.getABacctno(varno);
				} else {
					vabactno = manifestService.getABacctnoForSA(varno);
				}
				// End by SONLT3 on 11/02/2010
				map.put("vsactno", vsactno);
				map.put("vabactno", vabactno);
				sacctnbr = manifestService.getBPacctnbr(varno, seqno);
				map.put("sacctnbr", sacctnbr);
				// added laks 5 jul
			} else if (disp != null && !disp.equals("") && disp.equals("Assign_Bill")) {
				String acctnbr = CommonUtility.deNull(criteria.getPredicates().get("billparty"));
				String scheme = "";
				String mschacctnbr = "";
				scheme = manifestService.getSchemeName(varno);

				if (scheme.equals("JLR")) {
					mschacctnbr = manifestService.getVCactnbr(varno);
				} else if (!scheme.equals("JLR") && !scheme.equals("JNL") && !scheme.equals("JBT")
						&& !scheme.equals("JWP") && !scheme.equals(ProcessChargeConst.LCT_SCHEME)) {
					mschacctnbr = manifestService.getABactnbr(varno);
				}
				manifestService.MftAssignBillUpdate(varno, acctnbr, seqno, UserID);
				if (mschacctnbr != acctnbr) {
					manifestService.MftAssignVslUpdate(varno, "Y", UserID);
				}
				bnbredopkgs = manifestService.chkNbrEdopkgs(seqno, varno, blnum);
				ManifestValueObject mftvobj = new ManifestValueObject();
				mftvobj = manifestService.mftRetrieve(blnum, varno, seqno);
				map.put("nbredopkgs", "" + bnbredopkgs);
				map.put("mftdisp", "Display");
				map.put("mftretrieve", mftvobj);
				map.put("blnum", blnum);
				map.put("usrtyp", coCd);
				map.put("clbjind", "" + manifestService.getClBjInd(seqno));
				bvslstat = manifestService.chkVslStat(varno);
				if (bvslstat) {
					vslstat = "closed";
				} else {
					vslstat = "notclosed";
				}
				map.put("vslstat", vslstat);
				// added laks 5 jul
				schval = manifestService.getScheme(varno);
				schind = manifestService.getSchemeInd(varno);
				map.put("schval", schval);
				map.put("schind", schind);
				// added laks 5 jul
			} else if (disp != null && !disp.equals("") && disp.equals("AssignCargo")) {
				// Changes done on 17 june for mixed scheme indiactor
				manifestcargo = manifestService.getMftAssignCargo();
				// added laks 5 jul
				map.put("mancargovec", manifestcargo);
				// -----baskie 25jul
				manifestcheck = manifestService.MftAssignCrgvalCheck(varno, seqno);
				map.put("manifestcheck", manifestcheck);
				// ---------baskie 25jul
				// added laks 5 jul
				ManifestValueObject mftvobj = new ManifestValueObject();
				mftvobj = manifestService.mftRetrieve(blnum, varno, seqno);
				String currentCargoType = mftvobj.getCrgType();
				map.put("currentCargoType", currentCargoType);
				map.put("mftretrieve", mftvobj);
				map.put("blnum", blnum);
				Map<String, String> cargoCategoryCode_cargoCategoryName = manifestService
						.getCargoCategoryCode_CargoCategoryName();
				map.put("cargoCategoryCode_cargoCategoryName", cargoCategoryCode_cargoCategoryName);
				List<BookingReferenceValueObject> brvoList = manifestService.getBRVOList("AssignCargoCategory");
				map.put("brvoList", brvoList);
				String[] currentApplicableCargoCategoryList = new String[] {};
				for (int i = 0; i < brvoList.size(); i++) {
					if (brvoList.get(i).getCargoType().equals(currentCargoType)) {
						currentApplicableCargoCategoryList = brvoList.get(i).getCargoCategory().split(",");
					}
				}
				map.put("currentApplicableCargoCategoryList", currentApplicableCargoCategoryList);
				List<ManifestValueObject> crgTypeVector = manifestService.getCargoType();
				map.put("crgTypeVector", crgTypeVector);
				boolean showAllCargoCategory = manifestService.isShowAllCargoCategoryCode(coCd);
				map.put("showAllCargoCategory", showAllCargoCategory);
				String notShowCargoCategoryCode = manifestService.getNotShowCargoCategoryCode();
				map.put("notShowCargoCategoryCode", notShowCargoCategoryCode);
				String vslCarCarrier = manifestService.getCarCarrierVesselCode();
				map.put("vslCarCarrier", vslCarCarrier);
				String cargoTypeNotShow = manifestService.getCargoTypeNotShow();
				map.put("cargoTypeNotShow", cargoTypeNotShow);
				// END added by Maksym JCMS Smart CR 6.10
			} else if (disp != null && !disp.equals("") && disp.equals("Assign_Crg_Val")) {
				String cargocategory = CommonUtility.deNull(criteria.getPredicates().get("cargocategory"));
				// BEGIN amended by Maksym JCMS Smart CR 6.10
				// mftrem.MftAssignCrgvalUpdate(varno, cargocategory, seqno, UserID);
				String cargoType = CommonUtility.deNull(criteria.getPredicates().get("crgtype"));
				manifestService.MftAssignCargoCategoryCargoTypeUpdate(varno, cargocategory, cargoType, seqno, UserID);
				ManifestValueObject mftvobj = new ManifestValueObject();
				mftvobj = manifestService.mftRetrieve(blnum, varno, seqno);
				map.put("nbredopkgs", "" + bnbredopkgs);
				map.put("mftdisp", "Display");
				map.put("mftretrieve", mftvobj);
				map.put("blnum", blnum);
				map.put("usrtyp", coCd);
				map.put("clbjind", "" + manifestService.getClBjInd(seqno));
				String categoryValue = manifestService.getCategoryValue(cargocategory);
				map.put("categoryValue", categoryValue);
				// added laks 5 jul
				schval = manifestService.getScheme(varno);
				schind = manifestService.getSchemeInd(varno);
				map.put("schval", schval);
				map.put("schind", schind);
				// added laks 5 jul
				bvslstat = manifestService.chkVslStat(varno);
				if (bvslstat) {
					vslstat = "closed";
				} else {
					vslstat = "notclosed";
				}
				map.put("vslstat", vslstat);
			}
			mftaddcrglist = manifestService.getAddcrgList();
			if (mftaddcrglist == null) {
				errorMessage = ConstantUtil.ErrorMsg_Error_in_Cargo_List;
				return null;
			}
			if (mftaddcrglist.size() == 0) {
				errorMessage = ConstantUtil.ErrorMsg_Error_in_Cargo_List;
				return null;
			}
			for (int i = 0; i < mftaddcrglist.size(); i++) {
				ManifestValueObject mftObj = new ManifestValueObject();
				mftObj = (ManifestValueObject) mftaddcrglist.get(i);
				topsModel.put(mftObj);
			}
			boolean checkResult = manifestService.checkDisbaleOverSideFroDPE(varno);
			String hiddenOverSide = "FALSE";
			if (!StringUtils.equalsIgnoreCase("JP", coCd) && checkResult) {
				hiddenOverSide = "TRUE";
			}
			map.put("hiddenOverSide", hiddenOverSide);
			int maxCargoTon = manifestService.retrieveMaxCargoTon(varno);
			map.put("maxCargoTon", Integer.valueOf(maxCargoTon));
			map.put("dnbrpkgs", dnbrpkgs);
			map.put("tdnbrpkgs", tdnbrpkgs);
			// need to fix
			// result.fromBean(topsModel);
			map.put("request", "ManifestUpdate");
			map.put("topsModel", topsModel);
		} catch (BusinessException be) {
			log.info("Exception getManifestVslList : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception getManifestVslList : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null && errorMessage.length() != 0) {
				map.put("errorMessage", errorMessage);
				result.setSuccess(false);
				result.setError(errorMessage);
				result.setErrors(map);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
		}
		log.info("END: Manifest Amend result:" + result.toString());
		return ResponseEntityUtil.success(result.toString());
	}
	// EndRegion

	@PostMapping(value = "/getCargoSelectionList")
	public ResponseEntity<?> getCargoSelectionList(HttpServletRequest request) {
		Result result = new Result();
		List<MiscDetail> cargoSelection = new ArrayList<MiscDetail>();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START:getCargoSelectionListcriteria:" + criteria.toString());
			cargoSelection = manifestService.getCargoSelectionList(criteria);
		} catch (BusinessException be) {
			log.info("Exception getManifestVslList : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception getManifestVslList : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(cargoSelection);
				result.setSuccess(true);
			}
			log.info("END:getCargoSelectionList result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// EndRegion

	// EndRegion

	// Region NEW Featured By NS

	// Region ExcelProcessingManifest
	@RequestMapping(value = "/manifestUpload", method = RequestMethod.POST)
	public ResponseEntity<?> manifestUpload(@RequestParam("file") MultipartFile uploadingFile,
			HttpServletRequest request) {
		Result result = new Result();
		errorMessage = null;
		String assignedFileName = null;
		CargoManifestFileUploadDetails cargoManifestFileUploadDetails = new CargoManifestFileUploadDetails();
		try {
			log.info("START manifestUpload ");
			Criteria criteria = CommonUtil.getCriteria(request);
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String typeCd = CommonUtility.deNull(criteria.getPredicates().get("typeCd"));
			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			boolean isSplitBL = (CommonUtility.deNull(criteria.getPredicates().get("isSplitBL")).equalsIgnoreCase("true"))? true : false;

			log.info("manifestUpload Request Params:" + criteria.toString());
			Summary summary = new Summary();
			if (uploadingFile.getSize() > 0) {
				log.info("manifestUpload upload file size:" + uploadingFile.getSize() + ", File :"
						+ uploadingFile.getOriginalFilename());
			}

			if (uploadingFile.getSize() > 0) {
				log.info("Excel Process: Create a random gen UUID for " + uploadingFile.getOriginalFilename());
				String extension = FilenameUtils.getExtension(uploadingFile.getOriginalFilename());
				if (extension.equals("xls") || extension.equals("xlsx")) {
					/*
					 * UUID uuid = UUID.randomUUID(); assignedFileName = uuid.toString() + "." +
					 * extension; log.info("assignedFileName:" + assignedFileName); boolean
					 * fileStatus = manifestService.fileUpload(assignedFileName,
					 * uploadingFile,vvCd);
					 */
					assignedFileName = manifestService.fileUpload(uploadingFile, vvCd);
					log.info("fileUpload status :" + assignedFileName);
					if (assignedFileName != null && assignedFileName != "") {
						String lastTimestamp = manifestService.getTimeStamp();
						cargoManifestFileUploadDetails.setVv_cd((vvCd));
						cargoManifestFileUploadDetails.setActual_file_name(uploadingFile.getOriginalFilename());
						cargoManifestFileUploadDetails.setAssigned_file_name(assignedFileName);
						cargoManifestFileUploadDetails.setLast_modified_user_id(userId);
						cargoManifestFileUploadDetails.setLast_modified_dttm(lastTimestamp);
						cargoManifestFileUploadDetails.setTypeCd(typeCd);
						cargoManifestFileUploadDetails.setUpdateTypeCd(isSplitBL);

						if (typeCd.equalsIgnoreCase(ConstantUtil.manifest_type_cd)) {
							summary = manifestService.processManifestDetails(uploadingFile,
									cargoManifestFileUploadDetails, vvCd, userId, companyCode, isSplitBL);
						} else {
							summary = manifestService.processPackagingExcel(uploadingFile,
									cargoManifestFileUploadDetails, vvCd, userId);

						}

						result.setSuccess(true);
						result.setData(summary);
						log.info("excelProcessUpload: result: " + result.toString());
						// 4)insert action trail
						if(isSplitBL) { typeCd = "S";}
						boolean res = manifestService.insertActionTrial(criteria.getPredicates().get("vvCd"), typeCd,
								summary, lastTimestamp, userId);
						log.info("insertActionTrial:" + res);
					}
				} else {
					log.info("Exception manifestUpload : File should be xls or xlsx format");
					errorMessage = "M0010";
					result.setSuccess(false);
					result.setError(errorMessage);
					errorMessage = "";
				}
			} else {
				log.info("manifestUpload File missing ");
				result.setSuccess(false);
				result.setError("Upload file missing ");
			}
			
		} catch (BusinessException e) {
			log.error("Exception manifestUpload : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
			result.setSuccess(false);
			result.setError(errorMessage);
			result.setData(null);
		} catch (Exception ex) {
			log.info("Exception manifestUpload : ", ex);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
			result.setSuccess(false);
			result.setError(errorMessage);
		} finally {
			log.info("END manifestUpload :" + result.toString());
		}
		log.info("excelProcessUpload:result" + result.toString());
		return ResponseEntityUtil.success(result.toString());
	}

	@RequestMapping(value = "/fileDownload", method = RequestMethod.POST)
	public ResponseEntity<?> fileDownload(HttpServletRequest request) {
		Resource resource = null;
		try {
			log.info("START fileDownload");
			Criteria criteria = CommonUtil.getCriteria(request);
			String type = CommonUtility.deNull(criteria.getPredicates().get("typeCd"));
			String refId = CommonUtility.deNull(criteria.getPredicates().get("refId"));
			log.info("fileDownload : Param: " + criteria.toString());

			resource = manifestService.fileDownload(refId, type);

			String contentType = null;
			try {
				contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			} catch (Exception ex) {
				log.info(" fileDownload Could not determine file type.");
			}
			// Fallback to the default content type if type could not be determined
			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} catch (Exception ex) {
			log.info("Exception fileDownload : ", ex);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resource);
		} finally {
			log.info("END fileDownload");
		}
	}

	@RequestMapping(value = "/manifestDownload", method = RequestMethod.POST)
	public void manifestDownload(HttpServletRequest request, HttpServletResponse response) {
		try {
			log.info("START manifestDownload");
			Criteria criteria = CommonUtil.getCriteria(request);

			log.info(" manifestDownload criteria :" + criteria.toString());
			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			String refType = CommonUtility.deNull(criteria.getPredicates().get("refType"));
			// Add Split BL parameter - NS Jan 2025
			boolean isSplitBL = (CommonUtility.deNull(criteria.getPredicates().get("isSplitBL")).equalsIgnoreCase("true"))? true : false;
			XSSFWorkbook wb = null;
			if (ConstantUtil.manifest_type_cd.equalsIgnoreCase(refType)) {
				wb = manifestService.manifestDetailExcelDownload(vvCd, isSplitBL);
								
			} else if (ConstantUtil.packaging_type_cd.equalsIgnoreCase(refType)) {
				wb = manifestService.packagingDownload(vvCd);
			}
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=Manifest.xlsx");
			ServletOutputStream out = response.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
			wb.close();
		} catch (BusinessException be) {
			log.info("Exception manifestDownload : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception manifestDownload : ", e);
		} finally {
			log.info("END manifestDownload");
		}
	}

	@RequestMapping(value = "/manifestUploadDetail", method = RequestMethod.POST)
	public ResponseEntity<?> manifestUploadDetail(HttpServletRequest request) {
		Result result = new Result();
		PageDetails pageDetails = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START manifestUploadDetail criteria: " + criteria.toString());
			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			log.info(" manifestUploadDetail: Param:vvCd:" + vvCd);
			pageDetails = manifestService.manifestUploadDetail(vvCd);
			if (pageDetails != null) {
				Boolean isSubmissionAllowed = manifestService.isManifestSubmissionAllowed(criteria);
				pageDetails.setIsSubmissionAllowed(isSubmissionAllowed);
			}
			log.info(" manifestUploadDetail: result:" + result.toString());
		} catch (BusinessException be) {
			log.info("Exception manifestUploadDetail : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception manifestUploadDetail : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(pageDetails);
				result.setSuccess(true);
			}
			log.info("END manifestUploadDetail");
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@RequestMapping(value = "/manifestActionTrailInfo", method = RequestMethod.POST)
	public ResponseEntity<?> manifestActionTrailInfo(HttpServletRequest request) {
		TableResult manifest_hatch_act_trl = null;
		try {
			log.info("START : manifestActionTrailInfo");
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("Params:" + criteria.toString());

			manifest_hatch_act_trl = manifestService.getManifestActionTrail(criteria);

			log.info("manifestActionTrailInfo:" + manifest_hatch_act_trl.toString());
		} catch (BusinessException be) {
			log.info("Exception manifestActionTrail : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception manifestActionTrail : ", e);
		} finally {
			log.info("END manifestActionTrail");
		}
		return ResponseEntityUtil.success(manifest_hatch_act_trl.toString());
	}

	@RequestMapping(value = "/manifestActionTrailDetail", method = RequestMethod.POST)
	public ResponseEntity<?> manifestActionTrailDetail(HttpServletRequest request) {
		Result result = new Result();
		errorMessage = null;
		ManifestActionTrailDetails manifestActionTrailDetail = null;
		try {
			log.info("START : manifestActionTrailDetail");
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("manifestActionTrailDetail Params:" + criteria.toString());
			String mft_act_trl_id = CommonUtility.deNull(criteria.getPredicates().get("mft_act_trl_id"));
			manifestActionTrailDetail = manifestService.manifestActionTrailDetail(mft_act_trl_id);
		} catch (BusinessException be) {
			log.info("Exception manifestActionTrailDetail : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception manifestActionTrailDetail : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(manifestActionTrailDetail);
				result.setSuccess(true);
			}
			log.info("END manifestActionTrailDetail result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// EndRegion ExcelManifest

	// Region HatchBreakdown

	@PostMapping(value = "/getHatchBreakDownDetails")
	public ResponseEntity<?> getHatchBreakDownDetails(HttpServletRequest request) {
		Result result = new Result();
		HatchBreakDownPageDetail hatchBreakDownDetails = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START:getHatchBreakDownDetails criteria:" + criteria.toString());
			hatchBreakDownDetails = manifestService.getHatchBreakDownDetails(criteria);
		} catch (BusinessException be) {
			log.info("Exception getHatchBreakDownDetails : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception getHatchBreakDownDetails : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(hatchBreakDownDetails);
				result.setSuccess(true);
			}
			log.info("END:getHatchBreakDownDetails result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/saveHatchBreakDownDetails")
	public ResponseEntity<?> saveHatchBreakDownDetails(@RequestBody HatchWisePackageDetail saveManifestHatchBrkDown,
			HttpServletRequest request) {
		Result result = new Result();
		boolean rslt = true;
		errorMessage = null;
		try {
			log.info("START: *****saveHatchBreakDownDetails saveManifestHatchBrkDown:"
					+ saveManifestHatchBrkDown.toString());
			Criteria criteria = CommonUtil.getCriteria(request);
			String userAccount = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			saveManifestHatchBrkDown.setUserId(userAccount);
			saveManifestHatchBrkDown.setCoCode(companyCode);
			rslt = manifestService.saveManifestHatchDetails(saveManifestHatchBrkDown);
			log.info("rslt:" + rslt);
		} catch (BusinessException be) {
			log.info("Exception saveHatchBreakDownDetails : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception saveHatchBreakDownDetails : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(rslt);
			} else {
				result.setData(saveManifestHatchBrkDown.getVvCd());
				result.setSuccess(rslt);
			}
			log.info("END: saveHatchBreakDownDetails result:****" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/getHatchBreakDownHistoryDetail")
	public ResponseEntity<?> getHatchBreakDownHistoryDetail(HttpServletRequest request) {
		Result result = new Result();
		HatchBreakDownPageDetail hatchBreakDownPageDetail = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("getHatchBreakDownHistoryDetail criteria:" + criteria.toString());
			hatchBreakDownPageDetail = manifestService.getHatchBreakDownHistoryDetail(criteria);
		} catch (BusinessException be) {
			log.info("Exception getHatchBreakDownHistoryDetail : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception getHatchBreakDownHistoryDetail : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(hatchBreakDownPageDetail);
				result.setSuccess(true);
			}
			log.info("getHatchBreakDownHistoryDetail result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}
	// EndRegion HatchBreakdown

	// Region CargoDimesionDeclartion
	@PostMapping(value = "/getCargoDimensionDeclaration")
	public ResponseEntity<?> getCargoDimensionDeclaration(HttpServletRequest request) {
		CargoDimensionDeclarationResponse response = null;
		Result result = new Result();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** CargoDeclrartionList Start criteria :" + criteria.toString());
			List<CargoDimensionDeclaration> cargoDimensDecDet = manifestService.getCargoDimensionDeclaration(criteria);
			response = new CargoDimensionDeclarationResponse();
			response.setCargoDimensDecDet(cargoDimensDecDet);
			Boolean isSubmissionAllowed = manifestService.isManifestSubmissionAllowed(criteria);
			response.setIsSubmissionAllowed(isSubmissionAllowed);
		} catch (Exception ex) {
			log.info("Exception CargoDeclrartionList : ", ex);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(response);
				result.setSuccess(true);
			}
			log.info("** CargoDeclrartionList END. \n Response :" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/saveCargoDimensionDeclaration")
	public ResponseEntity<?> saveCargoDimensionDeclaration(@RequestBody List<CargoDimensionDeclaration> declInfo,
			HttpServletRequest request) {
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("** saveCargoDimensionDeclarationList Start criteria ** ");
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("saveCargoDimensionDeclaration : criteria: " + criteria.toString() + " declInfo : " + declInfo.toString());
			String userAccount = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			result = manifestService.saveCargoDimensionDeclaration(declInfo, userAccount);
		} catch (BusinessException be) {
			log.info("Exception saveCargoDimensionDeclarationList : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception saveCargoDimensionDeclarationList : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(declInfo);
				result.setSuccess(true);
			}
			log.info("END: saveCargoDimensionDeclarationList Result" + result.toString());
		}
		return ResponseEntityUtil.success(result);
	}
	// EndRegion CargoDimesionDeclartion

	// Region Cargo Dimension[CD]

	@PostMapping(value = "/getCargoDimensionsList")
	public ResponseEntity<?> getCargoDimensionsList(HttpServletRequest request) {
		TableResult result = new TableResult();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START:getCargoDimensionsList criteria:" + criteria.toString());
			result = manifestService.getCargoDimensionList(criteria);
		} catch (BusinessException be) {
			log.info("Exception getCargoDimensionsList : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception getCargoDimensionsList : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			log.info("END:getCargoDimensionsList result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/getCargoDimensionInfo")
	public ResponseEntity<?> getCargoDimensionInfo(HttpServletRequest request) {
		Result result = new Result();
		errorMessage = null;
		List<ManifestPkgDimDetails> list = new ArrayList<ManifestPkgDimDetails>();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: *****saveManifestHatchDetails criteria:" + criteria.toString());
			list = manifestService.getCargoDimensionDetails(criteria);
		} catch (BusinessException be) {
			log.info("Exception getCargoDimensionInfo : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception getCargoDimensionInfo : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(list);
				result.setSuccess(true);
			}
			log.info("END:getCargoDimensionInfo result:****" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/saveCargoDimensionDetails")
	public ResponseEntity<?> saveCargoDimensionDetails(@RequestBody CargoDimensionDetails saveCargoDimensionDetails,
			HttpServletRequest request) {
		Result result = new Result();
		boolean rslt = false;
		errorMessage = null;
		try {
			log.info("START: *****saveCargoDimensionDetails saveManifestHatchBrkDown:"
					+ saveCargoDimensionDetails.toString());
			Criteria criteria = CommonUtil.getCriteria(request);
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userAccount = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			saveCargoDimensionDetails.setCoCode(coCd);
			saveCargoDimensionDetails.setUserId(userAccount);
			rslt = manifestService.saveCargoDimensionDetails(saveCargoDimensionDetails);
		} catch (BusinessException be) {
			log.info("Exception saveCargoDimensionDetails : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception saveCargoDimensionDetails : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(rslt);
			} else {
				result.setData(saveCargoDimensionDetails);
				result.setSuccess(rslt);
			}
			log.info("END:saveCargoDimensionDetails result:****" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/getCargoDimensionAuditInfo")
	public ResponseEntity<?> getCargoDimensionAuditInfo(HttpServletRequest request) {
		Result result = new Result();
		errorMessage = null;
		List<ManifestPkgDimDetails> list = new ArrayList<ManifestPkgDimDetails>();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: *****getCargoDimensionAuditInfo criteria:" + criteria.toString());
			list = manifestService.getCargoDimensionAuditDetails(criteria);
			result.setData(list);
			result.setSuccess(true);
		} catch (BusinessException be) {
			log.info("Exception getCargoDimensionAuditInfo : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception getCargoDimensionAuditInfo : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(list);
				result.setSuccess(true);
			}
			log.info("getCargoDimensionAuditInfo result:****" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// EndRegion CD

	@PostMapping(value = "/isManifestSubmissionAllowed")
	public ResponseEntity<?> isManifestSubmissionAllowed(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		Boolean isManifestSubmissionAllowed = true;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: *****isManifestSubmissionAllowed criteria:" + criteria.toString());
			isManifestSubmissionAllowed = manifestService.isManifestSubmissionAllowed(criteria);
			map.put("isManifestSubmissionAllowed", isManifestSubmissionAllowed);
		} catch (BusinessException be) {
			log.info("Exception isManifestSubmissionAllowed : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception isManifestSubmissionAllowed : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
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
			}
			log.info("END: isManifestSubmissionAllowed result:****" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// Transfer Manifest Validation
	@PostMapping(value = "/validateTransferofManifest")
	public ResponseEntity<?> validateTransferofManifest(HttpServletRequest request) {
		Result result = new Result();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: *****validateTransferofManifest criteria:" + criteria.toString());
			result = manifestService.validateTransferofManifest(criteria);
		} catch (BusinessException be) {
			log.info("Exception validateTransferofManifest : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception validateTransferofManifest : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			log.info("END: validateTransferofManifest result:****" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/removeHatchBreakDownDetails")
	public ResponseEntity<?> removeHatchBreakDownDetails(HttpServletRequest request) {
		Result result = new Result();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: *****removeHatchBreakDownDetails criteria:" + criteria.toString());
			result = manifestService.removeHatchBreakDownDetails(criteria);
		} catch (BusinessException be) {
			log.info("Exception removeHatchBreakDownDetails : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception removeHatchBreakDownDetails : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			log.info("END: removeHatchBreakDownDetails result:****" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}
	
	@PostMapping(value = "/manifestCancel")
	public ResponseEntity<?> manifestCancel(HttpServletRequest request) {
		Result result = new Result();
		errorMessage = null;
		String seqno = "";
		String varno = "";
		String blno = "";
		boolean isManifestCancel = true;
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: *****manifestCancel criteria:" + criteria.toString());
			varno = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
			seqno = CommonUtility.deNull(criteria.getPredicates().get("seqno"));
			blno = CommonUtility.deNull(criteria.getPredicates().get("BlNo"));
			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			boolean vslstat = manifestService.chkVslStat(varno);
			if (vslstat) {
				log.info("Vessel Status is closed cannot Delete" + varno);
				throw new BusinessException("M21605");
			}
			boolean edostat = manifestService.chkEdonbrPkgs(seqno, varno, blno);
			if (edostat) {
				log.info("Edo Created cannot Delete" + blno);
				throw new BusinessException("M20202");
			}
			log.info("======== BEFORE chkDNnbrPkgs");
			boolean dnstat = manifestService.chkDNnbrPkgs(seqno, varno, blno);
			if (dnstat) {
				log.info("DN Printed cannot Delete" + blno);
				throw new BusinessException("M20208");
			}
			boolean tnstat = manifestService.chkTnbrPkgs(seqno, varno, blno);
			if (tnstat) {
				log.info("Transhipment done cannot Delete" + blno);
				throw new BusinessException("M20209");
			}
			boolean tdnstat = manifestService.chkTDNnbrPkgs(seqno, varno, blno);
			if (tdnstat) {
				log.info("Transhipment done cannot Delete" + blno);
				throw new BusinessException("M20209");
			}
			isManifestCancel = manifestService.mftCancel(UserID, seqno, varno, blno);
		
		} catch (BusinessException be) {
			log.info("Exception manifestCancel : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception manifestCancel : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result.setData(map);
				result.setSuccess(isManifestCancel);
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// START CR TO DISABLE VOLUME - NS FEB 2024
	@PostMapping(value = "/isDisabledVolume")
	public ResponseEntity<?> isDisabledVolume(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		// START - Fix showing previous error message when no error occur - NS APRIL 2024
		errorMessage = null;
		// END - Fix showing previous error message when no error occur - NS APRIL 2024
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: isDisabledVolume criteria:" + criteria.toString());
			map.put("disabledVolume", manifestService.isDisabledVolume(criteria));
		} catch (BusinessException be) {
			log.info("Exception isDisabledVolume : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception isDisabledVolume : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}
	// END CR TO DISABLE VOLUME - NS FEB 2024

	// START CR FTZ HSCODE - NS JULY 2024
	@PostMapping(value = "/lookup")
	public ResponseEntity<?> lookup(HttpServletRequest request) {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: lookup criteria:" + criteria.toString());
			String query =  CommonUtility.deNull(criteria.getPredicates().get("query")).trim();
			String type = CommonUtility.deNull(criteria.getPredicates().get("type")).trim();
			String hsCode = CommonUtility.deNull(criteria.getPredicates().get("hsCode")).trim();
			if (type.equalsIgnoreCase("listHSCode")) {
				map.put("data", manifestService.getHSCodeList("1", query));
			} else if (type.equalsIgnoreCase("loadHSSubCode")) {
				map.put("data", manifestService.loadHSSubCode("", hsCode));
			} 
			result.setData(map);
			result.setSuccess(true);
		} catch (BusinessException be) {
			log.info("Exception lookup : ", be);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception lookup : ", e);
			errorMessage = ConstantUtil.MANIFEST_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}
	// END CR FTZ HSCODE - NS JULY 2024
	// EndRegion NS
}
