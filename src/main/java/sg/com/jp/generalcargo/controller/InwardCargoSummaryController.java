package sg.com.jp.generalcargo.controller;

//import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import sg.com.jp.generalcargo.domain.CargoSummaryValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.service.InwardCargoSummaryService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = InwardCargoSummaryController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InwardCargoSummaryController {

	public static final String ENDPOINT = "gc/inwardcargo";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(InwardCargoSummaryController.class);

	@Autowired
	private InwardCargoSummaryService cargoSummaryService;

	// delegate.helper.gbms.cargo.cargosummary-->CargoSummaryHandler
	@PostMapping(value = "/cargosummary")
	public ResponseEntity<?> cargosummary(HttpServletRequest request) throws BusinessException {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		TableResult tableResult = new TableResult();
		TopsModel topsmodel = new TopsModel();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** cargosummary Start criteria :" + criteria.toString());
//			List<CargoSummaryValueObject> list = new ArrayList<CargoSummaryValueObject>();
			String s1 = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			tableResult = cargoSummaryService.getCargoSummaryList(s1, criteria);
//			list = tableResult.getData().getListData().getTopsModel();
			for (int l = 0; l < tableResult.getData().getListData().getTopsModel().size(); l++) {
				CargoSummaryValueObject cargosummaryvalueobject1 = (CargoSummaryValueObject) tableResult.getData().getListData().getTopsModel().get(l);
				topsmodel.put(cargosummaryvalueobject1);
			}

			map.put("total", tableResult.getData().getTotal());
			map.put("topsModel", topsmodel);
		} catch (BusinessException e) {
			log.info("Exception cargosummary : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception cargosummary : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
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
				log.info("END: cargosummary result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}
	
	@RequestMapping(value = "/getCompanyName", method = RequestMethod.POST)
	public ResponseEntity<?> getCompanyName(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: get company name");
			Criteria criteria = CommonUtil.getCriteria(request);
			String userAccount = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String companyName = cargoSummaryService.getCompanyName(companyCode);
			map.put("companyName", companyName);
			map.put("companyCode", companyCode);
			map.put("userAccount", userAccount);
			
		} catch (Exception e) {
			log.info("Exception cargosummary : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result = new Result();
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);
			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: get company name Result" + result.toString());
			}
		
		}
		return ResponseEntityUtil.success(result.toString());
	}
}
