package sg.com.jp.generalcargo.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.InwardCargoManifestService;
import sg.com.jp.generalcargo.service.InwardTransferOfManifestService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = InwardTransferOfManifestController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InwardTransferOfManifestController {

	@Autowired
	private InwardTransferOfManifestService transfermanifestService;
	@Autowired
	private InwardCargoManifestService manifestService;

	public static final String ENDPOINT = "gc/inwardcargo/tranferofmanifest";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(InwardTransferOfManifestController.class);

	// Transfer of Manifest JP-MIGRATION WRAPPER START REGION

	// delegate.helper.gbms.cargo.manifest --> TransManHandler
	@PostMapping(value = "/TransMan")
	public ResponseEntity<?> TransMan(HttpServletRequest request) {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		String varnoF = "";
		String varnoT = "";
		String vslvoyF = "";
		String vslvoyT = "";
		try {
			log.info("START: TransMan criteria:" + criteria.toString());
			TopsModel topsModel = new TopsModel();
			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			varnoF = CommonUtility.deNull(criteria.getPredicates().get("varnoF"));
			varnoT = CommonUtility.deNull(criteria.getPredicates().get("varnoT"));
			vslvoyF = CommonUtility.deNull(criteria.getPredicates().get("vslvoyF"));
			vslvoyT = CommonUtility.deNull(criteria.getPredicates().get("vslvoyT"));

			List<Object> mftlist = new ArrayList<Object>();
			List<ManifestValueObject> vseqblno = new ArrayList<ManifestValueObject>();
			Object vect1 = new ArrayList<ManifestValueObject>();
			Object vect2 = new ArrayList<ManifestValueObject>();

			int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
			List<String> seqblarr =  new ArrayList<String>();
			String seqblNo;
//			String seqblarr[] = request.getParameterValues("seqblval");
			
			if (sizeSelected != 0) {
				for (int i = 0; i < sizeSelected; i++) {
					seqblNo = (String) criteria.getPredicates().get("chkBx" + i);
					seqblarr.add(seqblNo);
				}
			}
			
			String seqno = "";
			String blnumb = "";
			StringTokenizer seqbl = null;

			for (int i = 0; i < seqblarr.size(); i++) {
//				if (seqblarr[i].trim() != "") {
					seqbl = new StringTokenizer(seqblarr.get(i), "-");
					blnumb = (seqbl.nextToken()).trim();
					seqno = (seqbl.nextToken()).trim();

					ManifestValueObject mftObj = new ManifestValueObject();
					mftObj.setBlNo(blnumb);
					mftObj.setSeqNo(seqno);
					vseqblno.add(mftObj);
//				}
			}

			mftlist = transfermanifestService.transMftUpdate(UserID, varnoF, varnoT, vseqblno);

			int size = mftlist.size();

			if (size > 0) {
				vect1 = mftlist.get(0);
				vect2 = mftlist.get(1);
			}
//			if (size > 1) {
//				vect2.add(mftlist.get(1));
//			}

			log.info("size  ** " + size);

			map.put("vect1", vect1);
			map.put("vect2", vect2);

			map.put("vslvoyF", vslvoyF);
			map.put("vslvoyT", vslvoyT);

			map.put("ListData", topsModel);

		} catch (BusinessException e) {
			log.info("Exception TransMan : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception TransMan : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("error", errorMessage);
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: TransMan result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.manifest --> TransManFPHandler
	@PostMapping(value = "/TransManFP")
	public ResponseEntity<?> TransManFP(HttpServletRequest request) {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		int total = 0;
		try {
			log.info("START: TransManFP criteria:" + criteria.toString());
			TopsModel topsModel = new TopsModel();
			TopsModel topsModelTo = new TopsModel();

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String voyval = "";
			String varnoF = "";
			String varnoT = "";
			String vslvoyF = "";
			String vslvoyT = "";

			voyval = CommonUtility.deNull(criteria.getPredicates().get("voyval"));
			varnoF = CommonUtility.deNull(criteria.getPredicates().get("varnoF"));
			varnoT = CommonUtility.deNull(criteria.getPredicates().get("varnoT"));
			vslvoyF = CommonUtility.deNull(criteria.getPredicates().get("vslvoyF"));
			vslvoyT = CommonUtility.deNull(criteria.getPredicates().get("vslvoyT"));

			List<String> blnolist = new ArrayList<String>();
			List<String> crglist = new ArrayList<String>();
			List<String> npkglist = new ArrayList<String>();
			List<String> gwtlist = new ArrayList<String>();
			List<String> gvolist = new ArrayList<String>();
			List<String> edostatlist = new ArrayList<String>();
			List<String> seqnolist = new ArrayList<String>();
			List<String> crgstatlist = new ArrayList<String>();
			List<ManifestValueObject> mftlist = new ArrayList<ManifestValueObject>();

			List<VesselVoyValueObject> vesselCallList = manifestService.getVesselVoy(coCd);

			for (int i = 0; i < vesselCallList.size(); i++) {
				VesselVoyValueObject vvvObj = new VesselVoyValueObject();
				vvvObj = (VesselVoyValueObject) vesselCallList.get(i);
				topsModel.put(vvvObj);
			}

			List<VesselVoyValueObject> vesselCallListTo = manifestService.getVesselVoyTo(coCd);
			for (int i = 0; i < vesselCallListTo.size(); i++) {
				VesselVoyValueObject vvvObj = new VesselVoyValueObject();
				vvvObj = (VesselVoyValueObject) vesselCallList.get(i);
				topsModelTo.put(vvvObj);
			}
			
			if (voyval != null && !voyval.equals("") && voyval.equals("voyval")) {
				mftlist = manifestService.getManifestList(varnoF, coCd, criteria);// changed by vietnd02 - add parameter
																					// coCd
				total = transfermanifestService.getManifestListCount(varnoF, coCd, criteria);
				int size = mftlist.size();
				int i = 0;

				for (i = 0; i < size; i++) {
					ManifestValueObject mftvObj = new ManifestValueObject();
					mftvObj = (ManifestValueObject) mftlist.get(i);

					blnolist.add((String) mftvObj.getBlNo());
					crglist.add((String) mftvObj.getCrgType());
					npkglist.add((String) mftvObj.getNoofPkgs());
					gwtlist.add((String) mftvObj.getGrWt());
					gvolist.add((String) mftvObj.getGrMsmt());
					edostatlist.add((String) mftvObj.getEdostat());
					seqnolist.add((String) mftvObj.getSeqNo());
					crgstatlist.add((String) mftvObj.getCrgStatus());
				}

			} // end of if voyval

			map.put("total", total);
			map.put("blnolist", blnolist);
			map.put("crglist", crglist);
			map.put("npkglist", npkglist);
			map.put("gwtlist", gwtlist);
			map.put("gvolist", gvolist);
			map.put("edostatlist", edostatlist);
			map.put("seqnolist", seqnolist);
			map.put("crgstatlist", crgstatlist);

			map.put("varnoF", varnoF);
			map.put("varnoT", varnoT);
			map.put("vslvoyF", vslvoyF);
			map.put("vslvoyT", vslvoyT);
			map.put("voyval", voyval);

			map.put("ListData", topsModel);
			map.put("ListVoyageTo", topsModelTo);

		} catch (BusinessException e) {
			log.info("Exception TransManFP : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			
			log.info("Exception TransManFP : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
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
				log.info("END: TransManFP result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

}
