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
import sg.com.jp.generalcargo.domain.GbEdoObj;
import sg.com.jp.generalcargo.domain.HSCode;
import sg.com.jp.generalcargo.domain.ManifestValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.service.AmendHSCodeService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = AmendHSCodeController.ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class AmendHSCodeController {
	public static final String ENDPOINT = "gc";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private static final Log log = LogFactory.getLog(AmendHSCodeController.class);
	private TopsModel topsModel = null;
	String errorMessage = null;

	@Autowired
	private AmendHSCodeService amendHSCodeService;

	// delegate.helper.gbms.cargo.generalcargo-->AmendGCHSCodeHandler
	@PostMapping(value = "/amendHSCode")
	public ResponseEntity<?> amendHSCode(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		List<HSCode> hsSubCode = null;
		errorMessage = null;
		try {
			log.info("START: amendHSCode criteria:" + criteria.toString());

			String type = (String) CommonUtility.deNull(criteria.getPredicates().get("type"));
			log.info("type in controller :" + type);
			if (type == null || type.equals("")) {

				init(request, map);
			} else if (type.equalsIgnoreCase("fetch")) {
				log.info("type in controller fetch :" + type);
				refresh(request, map);
			} else if (type.equalsIgnoreCase("update")) {
				log.info("type in controller update :" + type);
				updateHSCode(request, map);
			} else if (type.equalsIgnoreCase("back")) {
				map.put("method", "back");
				map.put("request", "AmendGCHSCodeListSer");
			} else if (type.equalsIgnoreCase("hsSubCode")) {
				hsSubCode = listHsSubCode(request);
				map.put("hsSubCode", hsSubCode);
			}
		} catch (BusinessException e) {
			log.info("Exception amendHSCode : ", e);
			errorMessage = ConstantUtil.AMENDHSCODE_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception amendHSCode : ", e);
			errorMessage = ConstantUtil.AMENDHSCODE_ERROR_CONSTANT_MAP.get("M4201");
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
				log.info("END: amendHSCode result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	private void init(HttpServletRequest request, Map<String, Object> map) throws BusinessException {
		List<String> edoList = null;
		List<String> esnList = null;
		String esnNo = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: init criteria: " + criteria + " , map: " + map);
			topsModel = new TopsModel();
			GbEdoObj gbEdoObj = new GbEdoObj();
			try {
				esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
				gbEdoObj.setEdoAsnNo(esnNo);

			} catch (Exception e) {
				log.info("Exception init : ", e);
			}
			edoList = amendHSCodeService.getEdoDetails(esnNo);
			log.info("edoList ***" + edoList.size());

			esnList = amendHSCodeService.getEsnDetails(esnNo);
			log.info("esnList ***" + esnList.size());

			if (edoList.size() == 0 && esnList.size() == 0) {
				errorMessage = "Enter valid Esn Number";
				return;
			}

			List<String> hsCodeList = new ArrayList<String>();

			List<ManifestValueObject> listManifestValueObject = amendHSCodeService.getHSCodeList("1");
			if (listManifestValueObject != null && listManifestValueObject.size() > 0) {
				String hsCode = "";
				for (int i = 0; i < listManifestValueObject.size(); i++) {
					ManifestValueObject manifestValueObject = new ManifestValueObject();
					manifestValueObject = (ManifestValueObject) listManifestValueObject.get(i);
					hsCode = manifestValueObject.getHsCode();
					hsCodeList.add(hsCode);
				}
			}

			String hscd = "";
			String hsSubCodeFr = "";
			String hsSubCodeTo = "";
			// To get HS Code , HS Code From & HS Code To from the edoList first time
			if (edoList != null && edoList.size() > 0) {
				hscd = (String) edoList.get(1);
				hsSubCodeFr = (String) edoList.get(2);
				hsSubCodeTo = (String) edoList.get(3);
				log.info("hscd:" + hscd + ":" + hsSubCodeFr + ":" + hsSubCodeTo);

			}

			if (esnList != null && esnList.size() > 0) {
				hscd = (String) esnList.get(1);
				hsSubCodeFr = (String) esnList.get(2);
				hsSubCodeTo = (String) esnList.get(3);

				log.info("hscd:" + hscd + ":" + hsSubCodeFr + ":" + hsSubCodeTo);
			}

			map.put("hsCodeList", hsCodeList);
			if (edoList.size() > 0)
				map.put("amendHSCodeEdoList", edoList);
			if (esnList.size() > 0) {
				log.info("esnList size in controller**" + esnList.size());
				map.put("amendHSCodeEsnList", esnList);
			}

			topsModel.put(gbEdoObj);
			map.put("topsModel", topsModel);
			map.put("method", "init");
			log.info("type in controller init :" + CommonUtility.deNull(criteria.getPredicates().get("method")));
			map.put("request", "AmendGCHSCodeListSer");
			log.info("END: init result: " + map);
		} catch (BusinessException e) {
			log.info("Exception init : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception init : ", e);
			throw new BusinessException("M4201");
		}
	}

	private void refresh(HttpServletRequest request, Map<String, Object> map) throws BusinessException {
		List<String> edoList = null;
		List<String> esnList = null;
		String esnNo = null;
		List<String> hsCodeList = new ArrayList<String>();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START refresh criteria: " + criteria + " , map: " + map);
			log.info("Beginning Refresh enter :" + CommonUtility.deNull(criteria.getPredicates().get("method")));
			topsModel = new TopsModel();
			GbEdoObj gbEdoObj = new GbEdoObj();
			try {
				esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
				gbEdoObj.setEdoAsnNo(esnNo);

			} catch (Exception e) {
				log.info("Exception refresh : ", e);
			}

			List<ManifestValueObject> listManifestValueObject = amendHSCodeService.getHSCodeList("1");
			if (listManifestValueObject != null && listManifestValueObject.size() > 0) {
				String hsCode = "";
				for (int i = 0; i < listManifestValueObject.size(); i++) {
					ManifestValueObject manifestValueObject = new ManifestValueObject();
					manifestValueObject = (ManifestValueObject) listManifestValueObject.get(i);
					hsCode = manifestValueObject.getHsCode();
					hsCodeList.add(hsCode);
				}
			}

			String hscd = (String) CommonUtility.deNull(criteria.getPredicates().get("HsCode"));
			String hsSubCodeFr = (String) CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeFr"));
			String hsSubCodeTo = (String) CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeTo"));
			String hsSubCodeDesc = (String) CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeDesc"));
			log.info("Refresh****" + hsSubCodeDesc);

			edoList = amendHSCodeService.getEdoDetails(esnNo, hscd, hsSubCodeFr, hsSubCodeTo, hsSubCodeDesc);
			esnList = amendHSCodeService.getEsnDetails(esnNo, hscd, hsSubCodeFr, hsSubCodeTo, hsSubCodeDesc);

			map.put("hsCodeList", hsCodeList);
			if (edoList != null && edoList.size() > 0)
				map.put("amendHSCodeEdoList", edoList);
			if (esnList != null && esnList.size() > 0)
				map.put("amendHSCodeEsnList", esnList);

			topsModel.put(gbEdoObj);
			map.put("topsModel", topsModel);
			map.put("method", "refresh");
			log.info("type in controller refresh :" + CommonUtility.deNull(criteria.getPredicates().get("method")));
			map.put("request", "AmendGCHSCodeListSer");
			log.info("END: refresh result: " + map);
		} catch (BusinessException e) {
			log.info("Exception refresh : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception refresh : ", e);
			throw new BusinessException("M4201");
		}

	}

	private void updateHSCode(HttpServletRequest request, Map<String, Object> map) throws BusinessException {
		boolean flag = false;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: updateHSCode criteria: " + criteria.toString() + " , " + map);
			String mftSeqNbr = CommonUtility.deNull(criteria.getPredicates().get("mftSeqNbr"));
			// String GCAId = (String) CommonUtility.deNull(criteria.getPredicates().get("GCAID"));
			String esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
			String hscode = CommonUtility.deNull(criteria.getPredicates().get("HsCode"));
			String hsSubCodeFr = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeFr"));
			String hsSubCodeTo = CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeTo"));
			// String prodType = (String) CommonUtility.deNull(criteria.getPredicates().get("prodType"));

			topsModel = new TopsModel();
			GbEdoObj gbEdoObj = new GbEdoObj();

			esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
			gbEdoObj.setEdoAsnNo(esnNo);

			if (!mftSeqNbr.equals("")&&mftSeqNbr != null) {
				log.info("mftSeqNbr***:" + mftSeqNbr);
				flag = amendHSCodeService.updateManifestGCAHsCode(mftSeqNbr, hscode, hsSubCodeFr, hsSubCodeTo);
			}
			if (mftSeqNbr.trim().equalsIgnoreCase("")) {
				log.info("mftSeqNbr is null***:" + mftSeqNbr);
				flag = amendHSCodeService.updateEsnGCAHsCode(esnNo, hscode, hsSubCodeFr, hsSubCodeTo);
			}

			topsModel.put(gbEdoObj);
			map.put("topsModel", topsModel);
			map.put("method", "ack");
			map.put("flag", String.valueOf(flag));
			map.put("request", "AmendGCHSCodeListSer");
			log.info("END updateHSCode result: " + map);
		} catch (BusinessException e) {
			log.info("Exception updateHSCode : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception updateHSCode : ", e);
			throw new BusinessException("M4201");
		}

	}

	public List<HSCode> listHsSubCode(HttpServletRequest request) throws BusinessException {
		List<HSCode> listHsSubCode = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: listHsSubCode :: criteria : " + criteria.toString());
			String hsCode = CommonUtility.deNull(criteria.getPredicates().get("HsCode")).trim();
			listHsSubCode = amendHSCodeService.getHSSubCodeList(hsCode);
		} catch (BusinessException e) {
			log.info("Exception listHsSubCode : ", e);
			throw new BusinessException(e.getMessage());
		} catch (Exception e) {
			log.info("Exception listHsSubCode : ", e);
			throw new BusinessException("M4201");
		}
		log.info("END: listHsSubCode :: result : " + listHsSubCode);
		return listHsSubCode;
	}

}
