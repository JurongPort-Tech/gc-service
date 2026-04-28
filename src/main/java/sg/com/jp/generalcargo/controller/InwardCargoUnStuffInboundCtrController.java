package sg.com.jp.generalcargo.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.Dept;
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.HSCode;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.UnStuffingCargoValueObject;
import sg.com.jp.generalcargo.domain.UnStuffingValueObject;
import sg.com.jp.generalcargo.domain.VesselSearchResponse;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.InwardCargoUnStuffInboundCtrService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = InwardCargoUnStuffInboundCtrController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InwardCargoUnStuffInboundCtrController {

	public static final String ENDPOINT = "gc/containerised";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(InwardCargoUnStuffInboundCtrController.class);

	@Autowired
	private InwardCargoUnStuffInboundCtrService inwardCargoService;

	// jp.src.delegate.helper.gbms.containerised.unstuffing-->UnStuffingHandler
	// StartRegion UnStuffingHandler
	// method: perform()
	/**
	 * Retrieves the Vessel Voyages and Container Numbers.
	 * 
	 * @param HttpServletRequest
	 * @return void
	 * @exception BusinessException
	 */
	@ApiOperation(value = "unStuffing", response = String.class)
	@RequestMapping(value = "/inwardCargo/unStuffing", method = RequestMethod.POST)
	public ResponseEntity<?> unStuffing(HttpServletRequest request) throws BusinessException {
		
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;

		try {
			log.info("START: unStuffing criteria:" + criteria.toString());

			String vslName = "";
			String vslnew = "";
			String str = "";
			String dttm = "";
			String waiveChrg = "";
			String strUnStfCls = "";
			String containernumber = "";

			StringTokenizer token;
			String cntrnbr = new String();
			String cntrseqnbr = new String();
			String unstuffclosestatus = new String();

			//String currUser = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String userCoyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			vslName = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
			vslnew = CommonUtility.deNull(criteria.getPredicates().get("vslnew"));
			if (log.isInfoEnabled()) {
				log.info("S in UnStuffing Handler is " + vslName + "\nSl in UnStuffing Handler is " + vslnew);
			}
			containernumber = CommonUtility.deNull(criteria.getPredicates().get("contno"));
			str = CommonUtility.deNull(criteria.getPredicates().get("close"));
			dttm = CommonUtility.deNull(criteria.getPredicates().get("unStfDttm"));
			waiveChrg = CommonUtility.deNull(criteria.getPredicates().get("waiveUnStfChrg"));
			strUnStfCls = CommonUtility.deNull(criteria.getPredicates().get("unStfCls"));
			if (containernumber != null) {
				token = new StringTokenizer(containernumber, "::");
				if (token != null && token.hasMoreElements()) {
					cntrnbr = token.nextElement().toString();
				}
				if (token != null && token.hasMoreElements()) {
					cntrseqnbr = token.nextElement().toString();
				}
			}

			List<UnStuffingValueObject> arraylist = new ArrayList<UnStuffingValueObject>();
			List<String> arraylist1 = new ArrayList<String> ();
			List<String> arraylist2 = new ArrayList<String> ();
			List<String> arraylist3 = new ArrayList<String> ();
			List<String> arraylist5 = new ArrayList<String> ();
			List<String> arraylist6 = new ArrayList<String> ();
			List<String> arraylist7 = new ArrayList<String> ();
			List<String> arraylist8 = new ArrayList<String> ();
			List<String> arraylist10 = new ArrayList<String> ();
//				if(vslName.equals("")) {
//					vslVoyList = inwardCargoService.getVesselVoy(userCoyCode);
//				}

			List<String> containerlist = inwardCargoService.getContainerNos(vslName);

			TopsModel topsmodel = new TopsModel();
//				if(vslName.equals("")) {
//					for (int j = 0; j < vslVoyList.size(); j++) {
//						VesselVoyValueObject vesselvoyvalueobject = null;
//						vesselvoyvalueobject = (VesselVoyValueObject) vslVoyList.get(j);
//						topsmodel.put(vesselvoyvalueobject);
//					}
//				}

			if (waiveChrg == null || waiveChrg.equals("")) {
				waiveChrg = inwardCargoService.getWaiveStatus(vslName, cntrnbr, cntrseqnbr);
			}

			map.put("selVoyno", vslName);
			map.put("selcontno", containernumber);
			map.put("containerlist", containerlist);
			map.put("close", str);
			map.put("unStfDttm", dttm);
			map.put("waiveUnStfChrg", waiveChrg);
			map.put("unStfCls", strUnStfCls);

			unstuffclosestatus = inwardCargoService.checkUnStuffClosed(cntrnbr, cntrseqnbr, vslName);
			map.put("IsUnStuffClosed", unstuffclosestatus);
			if ((dttm == null || dttm.equals("")) && unstuffclosestatus.equalsIgnoreCase("Closed")) {
				map.put("unStfDttm", inwardCargoService.getUnStuffDttm(vslName, cntrnbr, cntrseqnbr));
			}


			int total = 0;
			if (vslName != null && !vslName.equals("") && containernumber != null && !containernumber.equals("")) {
				TableResult tableResult  = inwardCargoService.getManifestList(vslName, containernumber, criteria);
				UnStuffingValueObject manifestList = null;
				int size = tableResult.getData().getListData().getTopsModel().size();
				for (int i = 0; i < size; i++) {
					manifestList = new UnStuffingValueObject();
					manifestList = (UnStuffingValueObject) tableResult.getData().getListData().getTopsModel().get(i);
					arraylist.add(manifestList);
				}
				total = tableResult.getData().getTotal();
				/*
				 * for(int i1 = 0; i1 < arraylist.size(); i1++) { UnStuffingValueObject
				 * unstuffingvalueobject = new UnStuffingValueObject(); unstuffingvalueobject =
				 * (UnStuffingValueObject)arraylist.get(i1); i += (new
				 * Integer(unstuffingvalueobject.getNoofPkgs())).intValue(); d += (new
				 * Double(unstuffingvalueobject.getGrWt())).doubleValue(); d1 += (new
				 * Double(unstuffingvalueobject.getGrMsmt())).doubleValue(); }
				 */

			}

	
			for (int k1 = 0; k1 < arraylist.size(); k1++) {
				UnStuffingValueObject unstuffingvalueobject1 = null;
				unstuffingvalueobject1 = arraylist.get(k1);
				arraylist1.add(unstuffingvalueobject1.getBlNo());
				arraylist2.add(unstuffingvalueobject1.getCrgDesc());
				arraylist3.add(unstuffingvalueobject1.getNoofPkgs());
				// arraylist4.add(unstuffingvalueobject1.getCntrSize());--4thSep,03
				// Vani
				arraylist5.add(unstuffingvalueobject1.getGrossVol());
				// arraylist5.add(unstuffingvalueobject1.getGrMsmt());
				arraylist6.add(unstuffingvalueobject1.getEdostat());
				arraylist7.add(unstuffingvalueobject1.getCntrNbr());
				arraylist8.add(unstuffingvalueobject1.getSeqNo());
				arraylist10.add(unstuffingvalueobject1.getGrWt());
				// arraylist8.add(unstuffingvalueobject1.getCrgStatus());
			}
			
			log.info("BLNO LIST == " + arraylist1.size());
			map.put("total", total);
			map.put("blnolist", arraylist1);
			map.put("crglist", arraylist2);
			map.put("npkglist", arraylist3);
			// httpservletmap.put("cntrszlist",
			// arraylist4);--4thSep,03 Vani
			map.put("gvolist", arraylist5);
			map.put("edostatlist", arraylist6);
			map.put("seqnolist", arraylist8);
			map.put("cntrnolist", arraylist7);
			map.put("gwtlist", arraylist10);
			// httpservletmap.put("crgstatlist", arraylist8);
			map.put("usrtyp", userCoyCode);
			// httpservletmap.put("tnop", "" + i);
			// httpservletmap.put("tgwt", "" + d);
			// httpservletmap.put("tvol", "" + d1);
			map.put("topsmodel", topsmodel);
			map.put("screen", "UnStuffingVesselCallList");

			result.setData(map);
			result.setSuccess(true);
		} catch (BusinessException be) {
			log.info("Exception stuffingCancel: " + be.getMessage());
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception stuffingCancel : ", e);
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				map.put("errorMessage", "errorMessage");
				result = new Result();
				result.setError(errorMessage);
				result.setErrors(map);
				result.setSuccess(false);
			}
			log.info("END unStuffing result:" + result.toString());
		}

		return ResponseEntityUtil.success(result);

	}

	// EndRegion UnStuffingHandler

	// StartRegion listCompany
	// method : sg.com.jp.admin.action.DeptMgmtAction
	@ApiOperation(value = "List Company", response = String.class)
	@RequestMapping(value = "/inwardCargo/listCompany", method = RequestMethod.POST)
	public ResponseEntity<?> listCompany(HttpServletRequest request) {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();

		try {
			log.info("** listCompany Start criteria :" + criteria.toString());
			String keyword = CommonUtility.deNull(criteria.getPredicates().get("filter"));
			String filterStart = CommonUtility.deNull(criteria.getPredicates().get("filterStart"));
			Integer limit = Integer
					.parseInt(((CommonUtility.deNull(criteria.getPredicates().get("limit"))).equals("") ? "50" : "50"));
			Integer start = Integer
					.parseInt(((CommonUtility.deNull(criteria.getPredicates().get("start"))).equals("") ? "0" : "0"));
			String type = CommonUtility.deNull(criteria.getPredicates().get("type"));
			
			if (keyword == null) {
				keyword = "";
			}
			List<Dept> companyList = null;
			if (!"".equalsIgnoreCase(filterStart) && filterStart != null) {
	            companyList = inwardCargoService.listCompanyStart(keyword, start, limit);
	        }
	        else {
	            companyList = inwardCargoService.listCompany(keyword, start, limit, type);
	        }

			map.put("total", new Integer(companyList.size()));
			map.put("data", companyList);
			result.setData(map);
			result.setSuccess(true);
			
		} catch (BusinessException be) {
			log.info("Exception listCompany: " + be.getMessage());
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception listCompany : ", e);
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
			}
			log.info(" *******listCompany End******* result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}
	// EndRegion listCompany

	// AutoComplete Method To Get VesselName
	@ApiOperation(value = "Vessel Name By Search", response = String.class)
	@RequestMapping(value = "/inwardCargo/getVesselNameBySearch", method = RequestMethod.POST)
	public ResponseEntity<?> getVesselNameBySearch(HttpServletRequest request) {
		List<VesselSearchResponse> vesselsNameBySearch = new ArrayList<>();
		Result result = new Result();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** getVesselNameBySearch Start criteria :" + criteria.toString());
			String vesselName = CommonUtility.deNull(criteria.getPredicates().get("searchString")).trim().toUpperCase();
			String cocode = CommonUtility.deNull(criteria.getPredicates().get("companyCode")).trim().toUpperCase();
			vesselsNameBySearch = inwardCargoService.getVesselsNameBySearch(vesselName, cocode);
			log.info("*** getVesselNameBySearch Result ***" + vesselsNameBySearch.toString());
			
		} catch (BusinessException be) {
			log.info("Exception getVesselNameBySearch: " + be.getMessage());
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception getVesselNameBySearch : ", e);
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
			}
			log.info("*** getVesselNameBySearch End ****");
		}
		return searchResponse(vesselsNameBySearch);
	}

	private ResponseEntity<?> searchResponse(List<VesselSearchResponse> list) {
		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("data", list);
		response.put("data", data);
		return ResponseEntityUtil.ok(response);
	}
	
	@ApiOperation(value = "Get Company", response = String.class)
	@RequestMapping(value = "/inwardCargo/getCompany", method = RequestMethod.POST)
	public ResponseEntity<?> getCompanyCode(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** getCompanyCode Start criteria :" + criteria.toString());
			String cocode = CommonUtility.deNull(criteria.getPredicates().get("companyCode")).trim().toUpperCase();
			map.put("coCd", cocode);
		}catch (Exception e) {
			log.info("Exception getCompanyCode : ", e);
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get("M4201");
		} finally {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			log.info("END: getCompanyCode result:" + result.toString());
			
		}

		return ResponseEntityUtil.success(result.toString());
	}

	public static Map<String, String> getRequestParameters(HttpServletRequest request) {
		Map<String, String> params = new HashMap<String, String>();
		for (Enumeration<String> v = request.getParameterNames(); v.hasMoreElements();) {
			String key = (String) v.nextElement();
			params.put(key, request.getParameter(key).trim());
		}
		return params;
	}

	@ApiOperation(value = "HS Sub Code List", response = String.class)
	@RequestMapping(value = "/inwardCargo/hsSubCodeList", method = RequestMethod.POST)
	public ResponseEntity<?> hsSubCodeList(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** HSSubCodeList Start criteria :" + criteria.toString());

			String hsCode = CommonUtility.deNull(criteria.getPredicates().get("hsCode"));
			List<HSCode> hsSubCodeLs = new ArrayList<HSCode>();
			hsSubCodeLs = inwardCargoService.getHSSubCodeList(hsCode);
			map.put("hsSubCodeLs", hsSubCodeLs);
			result.setSuccess(true);
			result.setData(map);
			
		} catch (BusinessException be) {
			log.info("Exception hsSubCodeList: " + be.getMessage());
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception hsSubCodeList : ", e);
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
			}
			log.info("END: HSSubCodeList result:" + result.toString());
			
		}

		return ResponseEntityUtil.success(result.toString());

	}

	@ApiOperation(value = "Packaging List", response = String.class)
	@RequestMapping(value = "/inwardCargo/packagingList", method = RequestMethod.POST)
	public ResponseEntity<?> packagingList(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		List<String> pkgcdlist = new ArrayList<String>();
		List<String> pkgnmlist = new ArrayList<String>();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** PackagingList Start criteria :" + criteria.toString());
			String screen = CommonUtility.deNull(criteria.getPredicates().get("screen"));
			if (screen.equals("Manifest")) {// Manifest

				List<ManifestValueObject> pkglist = new ArrayList<ManifestValueObject>();
				pkglist = inwardCargoService.getPkgList();
				for (int i = 0; i < pkglist.size(); i++) {
					ManifestValueObject mftvObj = new ManifestValueObject();
					mftvObj = pkglist.get(i);
					pkgcdlist.add( mftvObj.getPkgType());
					pkgnmlist.add( mftvObj.getPkgn());
				}
			} else {
				String getText = CommonUtility.deNull(criteria.getPredicates().get("getText"));

				List<EsnListValueObject> pkglist = new ArrayList<EsnListValueObject>();
				if (getText != null && !getText.equals("")) {
					pkglist = inwardCargoService.getPkgList(getText);
				}
				for (int i = 0; i < pkglist.size(); i++) {
					EsnListValueObject esnValueObject = new EsnListValueObject();
					esnValueObject =  pkglist.get(i);
					pkgcdlist.add( esnValueObject.getPkgType());
					pkgnmlist.add( esnValueObject.getPkgDesc());
				}

			}
			map.put("pkgcdlist", pkgcdlist);
			map.put("pkgnmlist", pkgnmlist);
			result.setSuccess(true);
			result.setData(map);
			
		} catch (BusinessException be) {
			log.info("Exception PackagingList: " + be.getMessage());
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception PackagingList : ", e);
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
			}
			log.info("END: PackagingList result:" + result.toString());
			
		}

		return ResponseEntityUtil.success(result.toString());

	}

	@ApiOperation(value = "Port List", response = String.class)
	@RequestMapping(value = "/inwardCargo/portList", method = RequestMethod.POST)
	public ResponseEntity<?> portList(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		List<ManifestValueObject> newList = new ArrayList<ManifestValueObject>();
		ManifestValueObject portList = null;
		TableResult tableResult = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** PortList Start criteria :" + criteria.toString());
			String screen = CommonUtility.deNull(criteria.getPredicates().get("screen"));
			String pCode = CommonUtility.deNull(criteria.getPredicates().get("pCode"));
			String pDesc = CommonUtility.deNull(criteria.getPredicates().get("pDesc"));

			int total = 0;
			if (screen.equals("Manifest")) {// Manifest

				if (pCode != null || pDesc != null) {
					tableResult = inwardCargoService.getPortList(pCode, pDesc, criteria);
					int size = tableResult.getData().getListData().getTopsModel().size();
					for (int i = 0; i < size; i++) {
						portList = new ManifestValueObject();
						portList = (ManifestValueObject) tableResult.getData().getListData().getTopsModel().get(i);
						newList.add(portList);
					}
					total = tableResult.getData().getTotal();
				} else {
					tableResult = inwardCargoService.getPortList(criteria);
					int size = tableResult.getData().getListData().getTopsModel().size();
					for (int i = 0; i < size; i++) {
						portList = new ManifestValueObject();
						portList = (ManifestValueObject) tableResult.getData().getListData().getTopsModel().get(i);
						newList.add(portList);
					}
					total = tableResult.getData().getTotal();
				}

			}
			

			// Display
			List<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();
			log.info("screen : " + screen);
			HashMap<String, String> hMap;
			ManifestValueObject mftvObj;
			for (int i = 0; i < newList.size(); i++) {
				mftvObj = new ManifestValueObject();
				hMap = new HashMap<String, String>();
				mftvObj =  newList.get(i);
				hMap.put("pCode", (String) mftvObj.getPortL());
				hMap.put("pDesc", (String) mftvObj.getPortLn());
				listData.add(hMap);
			}

			map.put("portlist", listData);
			map.put("total", total);
			result.setSuccess(true);
			result.setData(map);
			
		} catch (BusinessException be) {
			log.info("Exception portList: " + be.getMessage());
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception portList : ", e);
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setError(errorMessage);
				result.setSuccess(false);
			}
			log.info("END: portList result:" + result.toString());
			
		}

		return ResponseEntityUtil.success(result.toString());
	}
	// EndRegion UnStuffingHandler
	
	@ApiOperation(value = "unStuffingAddView", response = String.class)
	@RequestMapping(value = "/inwardCargo/unStuffingAddView", method = RequestMethod.POST)
	public ResponseEntity<?> unStuffingAddView(HttpServletRequest request) throws BusinessException {
		return this.unStuffingAdd(request);
	}

	// jp.src.delegate.helper.gbms.containerised.unstuffing-->UnStuffingAddHandler
	// StartRegion UnStuffingAddHandler
	// method: perform()
	@ApiOperation(value = "unStuffingAdd", response = String.class)
	@RequestMapping(value = "/inwardCargo/unStuffingAdd", method = RequestMethod.POST)
	public ResponseEntity<?> unStuffingAdd(HttpServletRequest request) throws BusinessException {
		errorMessage = null;
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();

		try {
			log.info("START: unStuffingAdd criteria:" + criteria.toString());

			String varno = "";
			StringTokenizer nobl = null;
			// For Display
			String disp = "";
			String blnum = "";
			String seqno = "";
			String sBlno = "";

			// For Addition
			String insertion = "";
			String sSeqno = "";
			String snoblno = "";
			String blno = "";
			String cntrno = "";
			String crgtype = "";
			String hscd = "";
			String hsSubCodeFr = "";
			String hsSubCodeTo = "";
			String crgdesc = "";
			String mark = "";
			String nop = "";
			String gwt = "";
			String gvol = "";
			String crgstat = "";
			String dgind = "";
			String pkgtype = "";
			String pofd = "";
			String pofdn = "";
			String pkgn = "";
			
			String vslnm = "";
			String invoynbr = "";
			
			String billparty = "";
			String dttm = "";
			String waivechrg = "";
			String consigneeNM = "";

			String consCoCd = "";

			String strUnStfCls = "";

			String stgind = "";
			String pol = "";
			String pod = "";
			String pLoad = "";
			String pDisc = "";

			String currUser = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));


			try {

				/*
				 * vslvoy = criteria.getPredicates().get("vslvoy"); vslInvoy = new
				 * java.util.StringTokenizer(vslvoy, "-");
				 */// 29 Sept,03 -- vani
				insertion = CommonUtility.deNull(criteria.getPredicates().get("Insertion"));
				disp = CommonUtility.deNull(criteria.getPredicates().get("disp"));
				// log.info("@ Insertion == " + Insertion);
				// log.info("@ disp == " + disp);
				blnum = CommonUtility.deNull(criteria.getPredicates().get("BlNo"));
				seqno = CommonUtility.deNull(criteria.getPredicates().get("SeqNo"));
				varno = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
				dttm = CommonUtility.deNull(criteria.getPredicates().get("unstfdt"));
				waivechrg = CommonUtility.deNull(criteria.getPredicates().get("waive"));
				// log.info("2Unstuff dttm" + dttm + "\twaivechrg == " + waivechrg);
				strUnStfCls = criteria.getPredicates().get("unStfCls");
				if (varno.equals("--Select--")) {
					varno = CommonUtility.deNull(criteria.getPredicates().get("vvcdval"));
					// vslInvoy = new java.util.StringTokenizer(vslvoy, "--");
				}

				// added by vani 29thSept,03 -- start
				List<VesselVoyValueObject> arraylist9 = inwardCargoService.getVesselVoy(coCd);
				for (int j = 0; j < arraylist9.size(); j++) {
					VesselVoyValueObject vesselvoyvalueobject = null;
					vesselvoyvalueobject = (VesselVoyValueObject) arraylist9.get(j);

					if (vesselvoyvalueobject.getVarNbr().equals(varno)) {
						vslnm = vesselvoyvalueobject.getVslName();
						invoynbr = vesselvoyvalueobject.getVoyNo();
					}

				}
				// log.info("1111vnm == " + vslnm + "\tvYnum == " + invoynbr);
				// added by vani 29thSept,03 -- end

				/*
				 * vslnm = vslInvoy.nextToken().trim(); invoynbr = vslInvoy.nextToken().trim();
				 */
				// log.info("2222vslnm == " + vslnm + "\tinvoynbr == " + invoynbr);
			} catch (Exception e) {
				log.info("Exception unStuffingAdd : ", e);
				errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get("M4201");
				throw new BusinessException(errorMessage);
			}

			List<UnStuffingValueObject> mftaddcrglist = null;
			TopsModel topsModel = new TopsModel();
			if (insertion != null && !insertion.equals("") && insertion.equals("Add")) {
				blno = CommonUtility.deNull(criteria.getPredicates().get("BlNo"));
				crgtype = CommonUtility.deNull(criteria.getPredicates().get("crgtype"));
				hscd = CommonUtility.deNull(criteria.getPredicates().get("HsCode"));
				hsSubCodeFr = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeFr"));
				hsSubCodeTo = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeTo"));
				crgdesc = CommonUtility.deNull(criteria.getPredicates().get("CrgDesc"));
				mark = CommonUtility.deNull(criteria.getPredicates().get("Markings"));
				nop = CommonUtility.deNull(criteria.getPredicates().get("NoOfPkgs"));
				gwt = CommonUtility.deNull(criteria.getPredicates().get("Gwt"));
				gvol = CommonUtility.deNull(criteria.getPredicates().get("Msmt"));
				crgstat = CommonUtility.deNull(criteria.getPredicates().get("crgStat"));
				dgind = CommonUtility.deNull(criteria.getPredicates().get("DgInd"));
				// consigneeNM = criteria.getPredicates().get("ConsigneeNM");
				consigneeNM = CommonUtility.deNull(criteria.getPredicates().get("ConName"));
				consCoCd = CommonUtility.deNull(criteria.getPredicates().get("lstConsignee"));

				// billparty=criteria.getPredicates().get("BillParty");
				pkgtype = CommonUtility.deNull(criteria.getPredicates().get("PkgType"));
				pofd = CommonUtility.deNull(criteria.getPredicates().get("PortFD"));
				cntrno = CommonUtility.deNull(criteria.getPredicates().get("contno"));// cntrno");
				stgind = CommonUtility.deNull(criteria.getPredicates().get("StgInd"));
				pol = CommonUtility.deNull(criteria.getPredicates().get("PortL"));
				pod = CommonUtility.deNull(criteria.getPredicates().get("PortD"));

				// not null check
				if (hscd == null || hsSubCodeFr == null || hsSubCodeTo == null
						|| hsSubCodeFr.trim().equalsIgnoreCase("") || hscd.trim().equalsIgnoreCase("")
						|| hsSubCodeTo.trim().equalsIgnoreCase("")) {
					errorMessage = "ErrorMsg_HS_SubCode_Null";
					throw new BusinessException(errorMessage);
				}

				// to check hs subcode, hssubcodefrom , hssubcodeto 3 Nov 2016 Start
				String hssubcodeDesc = inwardCargoService.getHSSubCodeDes(hscd, hsSubCodeFr, hsSubCodeTo);
				log.info("hssubcodeDesc validity check in UnstuffingAdd Handler***" + hssubcodeDesc);
				if (hssubcodeDesc == null || hssubcodeDesc.equalsIgnoreCase("")) {
					errorMessage = "ErrorMsg_HS_SubCode_Invalid";
					throw new BusinessException(errorMessage);
				}

				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END
				// log.info("@mftrem.MftInsertion() ...... stgind == " + stgind);
				// log.info("@mftrem.MftInsertion() ...... pol == " + pol);
				// log.info("@mftrem.MftInsertion() ...... pod == " + pod);
				// log.info("\nb4 mftrem.MftInsertion() ...... cntrno == " + cntrno);

				snoblno = inwardCargoService.MftInsertion(disp, currUser, varno, blno, cntrno, dttm, waivechrg, crgtype,
						hscd, hsSubCodeFr, hsSubCodeTo, crgdesc, mark, nop, gwt, gvol, crgstat, dgind, billparty,
						consigneeNM, consCoCd, stgind, pol, pod, pkgtype, pofd);
				// log.info("\na4 mftrem.MftInsertion() ...... snoblno == " + snoblno);

				nobl = new StringTokenizer(snoblno, "-");
				sSeqno = (nobl.nextToken()).trim();
				sBlno = (nobl.nextToken()).trim();
				if (pofd != null && !pofd.equals("")) {
					pofdn = inwardCargoService.getPortName(pofd);
				}

				pkgn = inwardCargoService.getPkgName(pkgtype);
				/*
				 * bedonbrpkgs = mftrem.chkEdonbrPkgs(sSeqno, varno, blno); bnbredopkgs =
				 * mftrem.chkNbrEdopkgs(sSeqno, varno, blno);
				 * 
				 * if (bedonbrpkgs) edonbrpkgs = "1"; else edonbrpkgs = "0";
				 * 
				 * bvslstat = mftrem.chkVslStat(varno); if (bvslstat) vslstat = "closed"; else
				 * vslstat = "notclosed";
				 * 
				 * bdnbrpkgs = mftrem.chkDNnbrPkgs(sSeqno, varno, blno); btnbrpkgs =
				 * mftrem.chkTnbrPkgs(sSeqno, varno, blno); btdnbrpkgs =
				 * mftrem.chkTDNnbrPkgs(sSeqno, varno, blno);
				 * 
				 * if (bdnbrpkgs) dnbrpkgs = "1"; else dnbrpkgs = "0";
				 * 
				 * if (btnbrpkgs) tnbrpkgs = "1"; else tnbrpkgs = "0";
				 * 
				 * if (btdnbrpkgs) tdnbrpkgs = "1"; else tdnbrpkgs = "0";
				 */

				try {
					StringTokenizer cntrtkn = new StringTokenizer(cntrno, "::");
					String cntrnbr = cntrtkn.nextToken();
					String cntrseqno = cntrtkn.nextToken();

					String unstuffclosestatus = inwardCargoService.checkUnStuffClosed(cntrnbr, cntrseqno, varno);

					if (unstuffclosestatus.equalsIgnoreCase("Open"))
						map.put("unstuffclosestatus", "N");
					else
						map.put("unstuffclosestatus", "Y");
				} catch (BusinessException be) {
					log.info("Exception unStuffingAdd: " + be.getMessage());
					errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
					if(errorMessage == null) {
						errorMessage = be.getMessage();
					}	
					throw new BusinessException(errorMessage);
				} catch (Exception e) {
					log.info("Exception unStuffingAdd : ", e);
					errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get("M4201");
					throw new BusinessException(errorMessage);
				}

				map.put("pofdn", pofdn);
				map.put("pkgn", pkgn);
				map.put("sSeqno", sSeqno);
				map.put("usrtyp", coCd);
				map.put("sBlno", sBlno);

				UnStuffingValueObject unstuffingvalueobject3 = inwardCargoService.mftRetrieve(blno, varno, sSeqno);

				map.put("mftretrieve", unstuffingvalueobject3);
				map.put("mftdisp", "Display");

			} else if (disp != null && !disp.equals("") && disp.equals("Display")) {
				UnStuffingValueObject mftvobj = new UnStuffingValueObject();

				mftvobj = inwardCargoService.mftRetrieve(blnum, varno, seqno);

				// bnbredopkgs = mftrem.chkNbrEdopkgs(seqno, varno, blnum);
				map.put("mftdisp", "Display");
				map.put("mftretrieve", mftvobj);
				// map.put("blnum", blnum);
				map.put("usrtyp", coCd);
				cntrno = criteria.getPredicates().get("contno");
				// log.info("from add handler cntrno == " + cntrno);
				// map.put("nbredopkgs", "" + bnbredopkgs);
				// map.put("clbjind", "" +
				// mftrem.getClBjInd(seqno));
			} else if (disp != null && !disp.equals("") && (disp.equals("Add") || disp.equals("AddOl"))) {

				mftaddcrglist = inwardCargoService.getAddcrgList();
				if (mftaddcrglist == null || mftaddcrglist.size() == 0) {
					errorMessage = "ErrorMsg_Cargo_List";
					throw new BusinessException(errorMessage);
				}

				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
				List<String> hsCodeList = new ArrayList<String>();

				List<ManifestValueObject> listManifestValueObject = inwardCargoService.getHSCodeList("1");
				log.info("Inside UnstuffAdd Handlesssss:" + listManifestValueObject.size());

				if (listManifestValueObject != null && listManifestValueObject.size() > 0) {
					String hsCode = "";
					for (int i = 0; i < listManifestValueObject.size(); i++) {
						ManifestValueObject manifestValueObject = new ManifestValueObject();
						manifestValueObject = (ManifestValueObject) listManifestValueObject.get(i);
						hsCode = manifestValueObject.getHsCode();
						hsCodeList.add(hsCode);
					}
				}
				log.info("Inside Unstuff Add Handler *** " + hsCodeList.size());

				map.put("hsCodeList", hsCodeList);

				for (int i = 0; i < mftaddcrglist.size(); i++) {
					UnStuffingValueObject mftObj = new UnStuffingValueObject();
					mftObj = (UnStuffingValueObject) mftaddcrglist.get(i);
					topsModel.put(mftObj);
				}
				// Satish
				// Getting Load and Discharge Ports
				String cntr_nbr = "";
				String cntr_seq_no = "";
				cntrno = criteria.getPredicates().get("contno");
				log.info("@ cntrno == " + cntrno);
				try {
					StringTokenizer cntr_tkn = new StringTokenizer(cntrno, "::");
					cntr_nbr = cntr_tkn.nextToken();
					log.info("@ cntr_nbr == " + cntr_nbr);
					cntr_seq_no = cntr_tkn.nextToken();
					log.info("@ cntr_seq_no == " + cntr_seq_no);				
				} catch (Exception e) {
					log.info("Exception stuffingCancel : ", e);
					errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get("M4201");
					throw new BusinessException(errorMessage);
				}
				
				List<String> ports = new ArrayList<String>();
				ports = inwardCargoService.getPorts(cntr_seq_no);
				if (ports != null && ports.size() > 0 && ports.size() == 2) {
					pLoad = (String) ports.get(0);
					pDisc = (String) ports.get(1);
				}
				log.info("@ pLoad == " + pLoad);
				log.info("@ pDisc == " + pDisc);
			}
			map.put("pLoad", pLoad);
			map.put("pDisc", pDisc);

			// map.put("vesselVoy", (vslnm + "-" + invoynbr));
			map.put("varNo", varno);
			map.put("usrtyp", coCd);
			map.put("vesselName", vslnm);
			map.put("cntrno", cntrno);
			map.put("Voy", invoynbr);
			map.put("unStfCls", strUnStfCls);
			map.put("topsModel", topsModel);
			map.put("screen", "UnStuffingAdd");

			result.setData(map);
			result.setSuccess(true);
		} catch (BusinessException be) {
			log.info("Exception unStuffingAdd: " + be.getMessage());
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception unStuffingAdd : ", e);
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
			}
			log.info("END unStuffingAdd result:" + result.toString());
		}

		return ResponseEntityUtil.success(result);

	}
	// EndRegion UnStuffingAddHandler

	// jp.src.delegate.helper.gbms.containerised.unstuffing-->UnStuffingCancelHandler
	// StartRegion UnStuffingCancelHandler
	// method: perform()
	/**
	 * Retrieves the Vessel Voyages and Container Numbers.
	 * 
	 * @param HttpServletRequest
	 * @return void
	 * @exception BusinessException
	 */
	@ApiOperation(value = "unStuffingCancel", response = String.class)
	@RequestMapping(value = "/inwardCargo/unStuffingCancel", method = RequestMethod.POST)
	public ResponseEntity<?> unStuffingCancel(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;

		try {
			log.info("START: unStuffingCancel criteria:" + criteria.toString());

			String s = "";
			String s1 = "";
			String s2 = "";
			String cntrNo = "";
			String strUnStfCls = "";

			s1 = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
			s = CommonUtility.deNull(criteria.getPredicates().get("seqno"));
			s2 = CommonUtility.deNull(criteria.getPredicates().get("BlNo"));
			cntrNo = CommonUtility.deNull(criteria.getPredicates().get("contno"));
			strUnStfCls = CommonUtility.deNull(criteria.getPredicates().get("unStfCls"));

			String s3 = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			//String s4 = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			inwardCargoService.mftCancel(s3, s, s1, s2, cntrNo);

			map.put("canbl", s2);
			map.put("varNo", s1);
			map.put("cntrno", cntrNo);
			map.put("unStfCls", strUnStfCls);
			map.put("screen", "UnStuffingCancel");

			result.setData(map);
			result.setSuccess(true);
		} catch (BusinessException be) {
			log.info("Exception unStuffingCancel: " + be.getMessage());
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception unStuffingCancel : ", e);
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
			}
			log.info("END unStuffingCancel result:" + result.toString());
		}

		return ResponseEntityUtil.success(result);

	}
	// EndRegion UnStuffingCancelHandler

	// jp.src.delegate.helper.gbms.containerised.unstuffing-->UnStuffingAmendHandler
	// StartRegion UnStuffingAmendHandler
	// method: perform()
	@ApiOperation(value = "unStuffingAmend", response = String.class)
	@RequestMapping(value = "/inwardCargo/unStuffingAmend", method = RequestMethod.POST)
	public ResponseEntity<?> unStuffingAmend(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;

		try {
			log.info("START: unStuffingAmend criteria:" + criteria.toString());

			String vslName = "";
			String cntrNo = "";
			String amend = "";
			String blno = "";
			String seqNo = "";			
			String portName = "";			
			String strUnStfCls = "";
			String vslnm = "";
			String invoynbr = "";
			boolean flag10 = false;

			String currUser = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String currUserCoyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));


			vslName = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
			cntrNo = CommonUtility.deNull(criteria.getPredicates().get("contno"));
			// log.info("\n1FROM AMEND HANDLER cntrNo ==
			// "+cntrNo);
			/*
			 * s1 = httpservletcriteria.getPredicates().get("vslvoy"); stringtokenizer = new
			 * StringTokenizer(s1, "-");
			 */

			// added by vani 29thSept,03 -- start
			List<VesselVoyValueObject> arraylist9 = inwardCargoService.getVesselVoy(currUserCoyCode);
			for (int j = 0; j < arraylist9.size(); j++) {
				VesselVoyValueObject vvvo = null;
				vvvo = (VesselVoyValueObject) arraylist9.get(j);

				if (vvvo.getVarNbr().equals(vslName)) {
					vslnm = vvvo.getVslName();
					invoynbr = vvvo.getVoyNo();
				}

			}
			// added by vani 29thSept,03 -- end

			amend = CommonUtility.deNull(criteria.getPredicates().get("amend"));
			blno = CommonUtility.deNull(criteria.getPredicates().get("BlNo"));
			seqNo = CommonUtility.deNull(criteria.getPredicates().get("seqno"));
			strUnStfCls = CommonUtility.deNull(criteria.getPredicates().get("unStfCls"));

			List<UnStuffingValueObject> arraylist = null;
			TopsModel topsmodel = new TopsModel();
			if (amend != null && !amend.equals("") && amend.equals("Amend")) {
				String blNo = CommonUtility.deNull(criteria.getPredicates().get("BlNo"));
				String cargoType = CommonUtility.deNull(criteria.getPredicates().get("crgtype"));
				String hsCode = CommonUtility.deNull(criteria.getPredicates().get("HsCode"));
				String hsSubCodeFr = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeFr"));
				String hsSubCodeTo = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeTo"));
				String cargoDesc = CommonUtility.deNull(criteria.getPredicates().get("CrgDesc"));
				String markings = CommonUtility.deNull(criteria.getPredicates().get("Markings"));
				String nbrPkg = CommonUtility.deNull(criteria.getPredicates().get("NoOfPkgs"));
				String gwt = CommonUtility.deNull(criteria.getPredicates().get("Gwt"));
				String msmt = CommonUtility.deNull(criteria.getPredicates().get("Msmt"));
				String cargoStatus = CommonUtility.deNull(criteria.getPredicates().get("crgStat"));
				String dgInd = criteria.getPredicates().get("DgInd");
				String billingParty = ""; // httpservletcriteria.getPredicates().get("blParty");
				String pkgType = CommonUtility.deNull(criteria.getPredicates().get("PkgType"));
				// String consigneeName = criteria.getPredicates().get("ConsigneeNM");
				String consigneeName = CommonUtility.deNull(criteria.getPredicates().get("ConName"));
				String consigneeCoyCode = CommonUtility.deNull(criteria.getPredicates().get("lstConsignee"));
				String portFd = CommonUtility.deNull(criteria.getPredicates().get("PortFD"));
				seqNo = CommonUtility.deNull(criteria.getPredicates().get("seqno"));

				String stgind = CommonUtility.deNull(criteria.getPredicates().get("StgInd"));
				String poL = CommonUtility.deNull(criteria.getPredicates().get("PortL"));
				String poD = CommonUtility.deNull(criteria.getPredicates().get("PortD"));

				String s29 = "";
				String s41 = "";
				String s43 = "";
				String s45 = "";
				String s47 = "";
				String s49 = "";

				// not null check
				if (hsCode == null || hsSubCodeFr == null || hsSubCodeTo == null
						|| hsSubCodeFr.trim().equalsIgnoreCase("") || hsCode.trim().equalsIgnoreCase("")
						|| hsSubCodeTo.trim().equalsIgnoreCase("")) {
					errorMessage = "ErrorMsg_HS_SubCode_Null";
					throw new BusinessException(errorMessage);
				}

				// to check hs subcode, hssubcodefrom , hssubcodeto 3 Nov 2016 Start
				String hssubcodeDesc = inwardCargoService.getHSSubCodeDes(hsCode, hsSubCodeFr, hsSubCodeTo);
				log.info("hssubcodeDesc validity check in UnstuffingAdd Handler***" + hssubcodeDesc);
				if (hssubcodeDesc == null || hssubcodeDesc.equalsIgnoreCase("")) {
					errorMessage = "ErrorMsg_HS_SubCode_Invalid";
					throw new BusinessException(errorMessage);
				}
				/*
				 * MftUpdation(String usrid, String coCd, String seqno, String varno, String
				 * blno, String crgtyp, String hscd, String crgdesc, String mark, String nopkgs,
				 * String gwt, String gvol, String crgstat, String dgind, String stgind, String
				 * dop, String pkgtyp, String coname, String poL, String poD, String poFD,
				 * String cntrtype, String cntrsize, String cntr1, String cntr2, String cntr3,
				 * String cntr4)
				 */

				/*
				 * String s5 = unstuffingejbremote.MftUpdation(s84, s85, s52, s, s7, s9, s11,
				 * s13, s15, s17, s19, s21, s23, s25, s27, s29, s31, s33, s35, s37, s39, s41,
				 * s43, s45, s47, s49, cntrNo);
				 */
				if (log.isInfoEnabled()) {
					log.info("ConsigneeCoyCode: " + consigneeCoyCode);
				}
				String mUpdRes = inwardCargoService.MftUpdation(currUser, currUserCoyCode, seqNo, vslName, blNo,
						cargoType, hsCode, hsSubCodeFr, hsSubCodeTo, cargoDesc, markings, nbrPkg, gwt, msmt,
						cargoStatus, dgInd, billingParty, stgind, s29, pkgType, consigneeName, consigneeCoyCode, poL,
						poD, portFd, s41, s43, s45, s47, s49, cntrNo);

				try {
					StringTokenizer cntrtkn = new StringTokenizer(cntrNo, "::");
					String cntrnbr = cntrtkn.nextToken();
					String cntrseqno = cntrtkn.nextToken();

					String unstuffclosestatus = inwardCargoService.checkUnStuffClosed(cntrnbr, cntrseqno, vslName);

					if (unstuffclosestatus.equalsIgnoreCase("Open"))
						map.put("unstuffclosestatus", "N");
					else
						map.put("unstuffclosestatus", "Y");
				} catch (BusinessException be) {
					log.info("Exception unStuffingAmend: " + be.getMessage());
					errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
					if(errorMessage == null) {
						errorMessage = be.getMessage();
					}	
					throw new BusinessException(errorMessage);
				} catch (Exception e) {
					log.info("Exception unStuffingAmend : ", e);
					errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get("M4201");
					throw new BusinessException(errorMessage);
				}

				if (portFd != null && !portFd.equals("")) {
					portName = inwardCargoService.getPortName(portFd);
				}
				String pkgName = inwardCargoService.getPkgName(pkgType);

				map.put("pofdn", portName);
				map.put("pkgn", pkgName);
				map.put("sSeqno", mUpdRes);
				map.put("usrtyp", currUserCoyCode);
				map.put("sBlno", blNo);
				String scheme = inwardCargoService.getScheme(vslName);
				String schemeInd = inwardCargoService.getSchemeInd(vslName);
				map.put("schval", scheme);
				map.put("schind", schemeInd);
				UnStuffingValueObject unstuffingvalueobject3 = inwardCargoService.mftRetrieve(blno, vslName, seqNo);

				map.put("mftretrieve", unstuffingvalueobject3);
				map.put("mftdisp", "Display");

			} else if (amend != null && !amend.equals("") && amend.equals("Display")) {
				UnStuffingValueObject vo = new UnStuffingValueObject();

				if (log.isInfoEnabled()) {
					log.info("Amend Handler bl_nbr == " + blno + "\tvarNbr == " + vslName + "  mft_seq_nbr == " + seqNo);
				}

				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : START
				List<String> hsCodeList = new ArrayList<String>();

				List<ManifestValueObject> listManifestValueObject = inwardCargoService.getHSCodeList("1");
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
				// VietNguyen (FPT) Enhancement HS Code 09-Jul-2012 : END
				vo = inwardCargoService.mftRetrieve(blno, vslName, seqNo);
				map.put("mftdisp", "Display");
				map.put("mftretrieve", vo);
				map.put("blnum", blno);
				map.put("usrtyp", currUserCoyCode);
				// httpservletmap.put("clbjind", "" +
				// unstuffingejbremote.getClBjInd(s52));
				String scheme = inwardCargoService.getScheme(vslName);
				String schemeInd = inwardCargoService.getSchemeInd(vslName);
				map.put("schval", scheme);
				map.put("schind", schemeInd);
			} else if (amend != null && !amend.equals("") && amend.equals("Assign")) {
				map.put("clbjind", "" + inwardCargoService.getClBjInd(seqNo));
				List<String> vector1 = inwardCargoService.getSAacctno(vslName);
				List<UnStuffingValueObject> vector3 = inwardCargoService.getABacctno(vslName);
				map.put("vsactno", vector1);
				map.put("vabactno", vector3);
				String bpAcctNbr = inwardCargoService.getBPacctnbr(vslName, seqNo);
				map.put("sacctnbr", bpAcctNbr);
			} else if (amend != null && !amend.equals("") && amend.equals("Assign_Bill")) {
				String billParty = criteria.getPredicates().get("billparty");
				String schemeName = "";
				String vcAcctNbr = "";
				schemeName = inwardCargoService.getSchemeName(vslName);
				if (schemeName.equals("JLR"))
					vcAcctNbr = inwardCargoService.getVCactnbr(vslName);
				else
				// <cfg: add new scheme for Wooden Craft: JWP, 27.may.08>
				// if (!s88.equals("JLR") && !s88.equals("JNL") &&
				// !s88.equals("JBT"))
				if (!(schemeName.equals("JLR") || schemeName.equals("JNL") || schemeName.equals("JBT")
						|| schemeName.equals("JWP")))
					vcAcctNbr = inwardCargoService.getABactnbr(vslName);
				inwardCargoService.MftAssignBillUpdate(vslName, billParty, seqNo, currUser);
				if (vcAcctNbr != billParty)
					inwardCargoService.MftAssignVslUpdate(vslName, "Y", currUser);
				flag10 = inwardCargoService.chkNbrEdopkgs(seqNo, vslName, blno);
				UnStuffingValueObject unstuffingvalueobject3 = new UnStuffingValueObject();
				unstuffingvalueobject3 = inwardCargoService.mftRetrieve(blno, vslName, seqNo);
				map.put("nbredopkgs", "" + flag10);
				map.put("mftdisp", "Display");
				map.put("mftretrieve", unstuffingvalueobject3);
				map.put("blnum", blno);
				map.put("usrtyp", currUserCoyCode);
				map.put("clbjind", "" + inwardCargoService.getClBjInd(seqNo));
				String scheme = inwardCargoService.getScheme(vslName);
				String schemeInd = inwardCargoService.getSchemeInd(vslName);
				map.put("schval", scheme);
				map.put("schind", schemeInd);
			} else if (amend != null && !amend.equals("") && amend.equals("AssignCargo")) {
				log.info("Entered AssignCargo");
				List<UnStuffingCargoValueObject> vector5 = inwardCargoService.getMftAssignCargo();
				map.put("mancargovec", vector5);
				String s83 = inwardCargoService.MftAssignCrgvalCheck(vslName, seqNo);
				log.info("unstuffingcheck----" + s83);
				map.put("manifestcheck", s83);
			} else if (amend != null && !amend.equals("") && amend.equals("Assign_Crg_Val")) {
				// log.info("Updated Assign Cargo");
				String cargoCat = criteria.getPredicates().get("cargocategory");
				inwardCargoService.MftAssignCrgvalUpdate(vslName, cargoCat, seqNo, currUser);
				UnStuffingValueObject unstuffingvalueobject1 = new UnStuffingValueObject();
				unstuffingvalueobject1 = inwardCargoService.mftRetrieve(blno, vslName, seqNo);
				map.put("nbredopkgs", "" + flag10);
				map.put("mftdisp", "Display");
				map.put("mftretrieve", unstuffingvalueobject1);
				map.put("blnum", blno);
				map.put("usrtyp", currUserCoyCode);
				map.put("clbjind", "" + inwardCargoService.getClBjInd(seqNo));
				String scheme = inwardCargoService.getScheme(vslName);
				String schemeInd = inwardCargoService.getSchemeInd(vslName);
				map.put("schval", scheme);
				map.put("schind", schemeInd);
			}
			arraylist = inwardCargoService.getAddcrgList();
			if (arraylist == null) {
				errorMessage = "ErrorMsg_Cargo_List";
				throw new BusinessException(errorMessage);
			}
			if (arraylist.size() == 0) {
				errorMessage = "ErrorMsg_Cargo_List";
				throw new BusinessException(errorMessage);
			}
			for (int i = 0; i < arraylist.size(); i++) {
				UnStuffingValueObject vo = new UnStuffingValueObject();
				vo = arraylist.get(i);
				topsmodel.put(vo);
			}
			// log.info("\n FROM AMEND HANDLER cntrNo == "+cntrNo);
			/*
			 * httpservletmap.put("vesselName", stringtokenizer.nextToken().trim());
			 * httpservletmap.put("Voy", stringtokenizer.nextToken().trim());
			 */
			// httpservletmap.put("vesselVoy", s1);
			map.put("vesselName", vslnm);
			map.put("Voy", invoynbr);

			map.put("varNo", vslName);
			map.put("cntrno", cntrNo);
			map.put("topsmodel", topsmodel);
			map.put("unStfCls", strUnStfCls);
			map.put("screen", "UnStuffingAmend");

			result.setData(map);
			result.setSuccess(true);
		} catch (BusinessException be) {
			log.info("Exception unStuffingAmend: " + be.getMessage());
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception unStuffingAmend : ", e);
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
			}
			log.info("END unStuffingAmend result:" + result.toString());
		}

		return ResponseEntityUtil.success(result);

	}
	// EndRegion UnStuffingAmendHandler
	
	@ApiOperation(value = "unStuffingCloseUpd", response = String.class)
	@RequestMapping(value = "/inwardCargo/unStuffingCloseUpd", method = RequestMethod.POST)
	public ResponseEntity<?> unStuffingCloseUpd(HttpServletRequest request) throws BusinessException {
		return this.unStuffingClose(request);
	}

	// jp.src.delegate.helper.gbms.containerised.unstuffing-->UnStuffingCloseHandler
	// StartRegion UnStuffingCloseHandler
	// method: perform()
	@ApiOperation(value = "unStuffingClose", response = String.class)
	@RequestMapping(value = "/inwardCargo/unStuffingClose", method = RequestMethod.POST)
	public ResponseEntity<?> unStuffingClose(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;

		try {
			log.info("START: unStuffingClose criteria:" + criteria.toString());

			String s = "";
			String s1 = "";
			String containernumber = "";
			String strUnStfDt = "";
			String strWaiveUnStfChrg = "";
			
			String vslnm = "";
			String invoynbr = "";

			String s2 = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String s3 = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			s = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
			s1 = CommonUtility.deNull(criteria.getPredicates().get("vslnew"));

			/*
			 * vslvoy = criteria.getPredicates().get("vslvoy"); vslInvoy = new
			 * java.util.StringTokenizer(vslvoy, "-");
			 */

			if (s.equals("--Select--")) {
				// vslInvoy = new java.util.StringTokenizer(vslvoy, "--");
			}

			// added by vani 29thSept,03 -- start
			List<VesselVoyValueObject> arraylist9 = inwardCargoService.getVesselVoy(s3);
			for (int j = 0; j < arraylist9.size(); j++) {
				VesselVoyValueObject vesselvoyvalueobject = null;
				vesselvoyvalueobject = (VesselVoyValueObject) arraylist9.get(j);

				if (vesselvoyvalueobject.getVarNbr().equals(s)) {
					vslnm = vesselvoyvalueobject.getVslName();
					invoynbr = vesselvoyvalueobject.getVoyNo();
				}

			}
			log.info("1111vnm == " + vslnm + "\tvYnum == " + invoynbr);
			// added by vani 29thSept,03 -- end

			/*
			 * vslnm = vslInvoy.nextToken().trim(); invoynbr = vslInvoy.nextToken().trim();
			 */

			log.info("S in UnStuffing Handler is " + s + "\nSl in UnStuffing Handler is " + s1 + "\tinvoynbr: "
					+ invoynbr);

			containernumber = CommonUtility.deNull(criteria.getPredicates().get("contno"));
			strUnStfDt = CommonUtility.deNull(criteria.getPredicates().get("unstfdt"));
			strWaiveUnStfChrg = CommonUtility.deNull(criteria.getPredicates().get("waive"));
			log.info("CLose HANDLER Unstuff dttm" + strUnStfDt + "\twaivechrg == " + strWaiveUnStfChrg);

			boolean closed = false;
			if (criteria.getPredicates().get("disp") != null
					&& criteria.getPredicates().get("disp").trim().equalsIgnoreCase("closeupdate")) {
				closed = true;
				StringTokenizer cntrtkn = new StringTokenizer(containernumber, "::");
				inwardCargoService.updateWaiverStatus(s, invoynbr, cntrtkn.nextToken(), cntrtkn.nextToken(),
						strWaiveUnStfChrg, s2);
			} else {
				closed = inwardCargoService.closeUnStuffing(s2, invoynbr, s, containernumber, strUnStfDt,
						strWaiveUnStfChrg);
			}

			map.put("varNo", s);
			map.put("cntrno", containernumber);
			map.put("close", "closed");
			map.put("unStfDttm", strUnStfDt);
			map.put("waiveUnStfChrg", strWaiveUnStfChrg);

			if (closed) {
				map.put("unStfCls", "Y");
				map.put("screen", "UnStuffingClose");
			} else
				map.put("unStfCls", "N");
			// nextScreen(httpservletrequest, "UnStuffingClose");
			// nextScreenhas to forward the request to UnStuffingVslList function

		} catch (BusinessException be) {
			log.info("Exception unStuffingClose: " + be.getMessage());
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception unStuffingClose : ", e);
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: unStuffingClose result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}
	// EndRegion UnStuffingCloseHandler

	// jp.src.delegate.helper.gbms.containerised.unstuffing-->UnStuffingBlPartyHandler
	// StartRegion UnStuffingBlPartyHandler
	// method: perform()
	@ApiOperation(value = "unStuffingBlParty", response = String.class)
	@RequestMapping(value = "/inwardCargo/unStuffingBlParty", method = RequestMethod.POST)
	public ResponseEntity<?> unStuffingBlParty(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;

		try {
			log.info("START: unStuffingBlParty criteria:" + criteria.toString());
			
			String varno = "";
			

			// For Display
			String disp = "";
			// For Addition
			String Insertion = "";
			String cntrno = "";
			String vslnm = "";
			String invoynbr = "";
			String blPartyAcctNo = "";
			String numOfBills = "";
			String strUnStfCls = "";
			String strOtherBlParty = "";

			//String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String blParty = "";


			/*
			 * vslvoy = criteria.getPredicates().get("vslvoy"); vslInvoy = new
			 * java.util.StringTokenizer(vslvoy, "-");
			 */

			Insertion = CommonUtility.deNull(criteria.getPredicates().get("Insertion"));
			disp = CommonUtility.deNull(criteria.getPredicates().get("disp"));
			log.info("Insertion == " + Insertion + "\tdisp == " + disp);
			varno = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
			cntrno = CommonUtility.deNull(criteria.getPredicates().get("contno"));
			strUnStfCls = CommonUtility.deNull(criteria.getPredicates().get("unStfCls"));
			log.info("FROM UnstfBlPartyHandler  strUnStfCls == " + strUnStfCls);
			if (varno.equals("--Select--")) {
				varno = criteria.getPredicates().get("vvcdval");
				// vslInvoy = new java.util.StringTokenizer(vslvoy, "--");
			}
			/*
			 * vslnm = vslInvoy.nextToken().trim(); invoynbr = vslInvoy.nextToken().trim();
			 */

			// added by vani 29thSept,03 -- start
			List<VesselVoyValueObject> arraylist9 = inwardCargoService.getVesselVoy(coCd);
			for (int j = 0; j < arraylist9.size(); j++) {
				VesselVoyValueObject vesselvoyvalueobject = null;
				vesselvoyvalueobject = (VesselVoyValueObject) arraylist9.get(j);

				if (vesselvoyvalueobject.getVarNbr().equals(varno)) {
					vslnm = vesselvoyvalueobject.getVslName();
					invoynbr = vesselvoyvalueobject.getVoyNo();
				}

			}
			log.info("1111vnm == " + vslnm + "\tvYnum == " + invoynbr);
			// added by vani 29thSept,03 -- end

			List<String> blPartylist = null;

			if (Insertion != null && !Insertion.equals("") && Insertion.equals("Assign")) {

				UnStuffingValueObject mftvobj = new UnStuffingValueObject();

				blParty = criteria.getPredicates().get("blParty");
				if (blParty.equals("BP")) {
					blPartyAcctNo = CommonUtility.deNull(criteria.getPredicates().get("blParty_val"));
				} else if (blParty.equals("OBP")) {
					blPartyAcctNo = CommonUtility.deNull(criteria.getPredicates().get("BlAcctNo"));
				}

				log.info("INserttttttttt blParty == " + blParty + "\tblPartyAcctNo == " + blPartyAcctNo);
				log.info("\nb4 inwardCargoService.MftAssignBlParty() ...... cntrno == " + cntrno);
				mftvobj = inwardCargoService.MftAssignBlParty(varno, cntrno, blPartyAcctNo);
				log.info("\na4 inwardCargoService.MftAssignBlParty() ...... ");

				map.put("mftdisp", "Display");
				map.put("mftretrieve", mftvobj);
				map.put("usrtyp", coCd);

			} else if (disp != null && !disp.equals("") && (disp.equals("AssignBlParty") || disp.equals("AddOl"))) {
				log.info("in HANDLER before getting blParty list" + disp);
				blPartylist = inwardCargoService.getBlPartyList(varno, invoynbr, cntrno);
				log.info("blPartylist sz == " + blPartylist.size());

				//if (blPartylist == null) {
					//errorMessage = "Error in blPartylist List";
					//return null;
				//}
				if (blPartylist.size() == 0) {
					errorMessage = "ErrorMsg_blPartylist_List";
					throw new BusinessException(errorMessage);
				}

				blPartyAcctNo = (String) blPartylist.get(0);
				numOfBills = (String) blPartylist.get(1);
				strOtherBlParty = (String) blPartylist.get(2);

			}

			// map.put("vesselVoy", (vslnm + "-" + invoynbr));
			map.put("varNo", varno);
			map.put("usrtyp", coCd);
			map.put("vesselName", vslnm);
			map.put("cntrno", cntrno);
			map.put("Voy", invoynbr);
			map.put("blPartyAcctNo", blPartyAcctNo);
			map.put("bilNo", numOfBills);
			map.put("unStfCls", strUnStfCls);
			map.put("otherBlParty", strOtherBlParty);

			if (disp != null && !disp.equals("") && (disp.equals("AssignBlParty") || disp.equals("AddOl"))) {
				map.put("screen", "UnStuffingAssignBlParty");
			} else if (Insertion != null && !Insertion.equals("") && Insertion.equals("Assign")) {
				map.put("screen", "UnStuffingBlPartyAdd");
			}

			result.setData(map);
			result.setSuccess(true);
		} catch (BusinessException be) {
			log.info("Exception unStuffingBlParty: " + be.getMessage());
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception unStuffingBlParty : ", e);
			errorMessage = ConstantUtil.INWARD_UNSTUFF_OUTBOUND_CTR_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
			}
			log.info("END unStuffingBlParty result:" + result.toString());
		}

		return ResponseEntityUtil.success(result);

	}
	// EndRegion UnStuffingBlPartyHandler
}
