package sg.com.jp.generalcargo.controller;

import java.util.List;

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
import sg.com.jp.generalcargo.domain.ViewTransferredCargo;
import sg.com.jp.generalcargo.service.OutwardViewTransferredCargoService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = OutwardViewTransferredCargoController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OutwardViewTransferredCargoController {

	@Autowired
	private OutwardViewTransferredCargoService viewTransferredCargoService;

	public static final String ENDPOINT = "gc/outwardcargo";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(OutwardViewTransferredCargoController.class);

	// StartRegion getTransferredCargoDetails

	@PostMapping(value = "/viewTransferredCargo")
	public ResponseEntity<?> viewTransferredCargo(HttpServletRequest request) {
		Result result = new Result();
		errorMessage = null;
		List<ViewTransferredCargo> transCargoList = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("** viewTransferredCargo Start criteria :" + criteria.toString());

			String vslIndicator = criteria.getPredicates().get("vesselIndicator").toUpperCase();
			String vslName = criteria.getPredicates().get("vesselName").toUpperCase();
			String outVoyNo = criteria.getPredicates().get("outVoyNo").toUpperCase();
			String custCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			transCargoList = viewTransferredCargoService.getTransferredCargoDetails(vslIndicator, vslName, outVoyNo, custCode);
			log.info("viewTransferredCargo criteria:" + criteria.toString());
			
		} catch (BusinessException e) {
			log.info("Exception viewTransferredCargo: ", e);
			errorMessage=ConstantUtil.TRANSFER_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if(errorMessage == null) {
				errorMessage = e.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception viewTransferredCargo : ", e);
			errorMessage = ConstantUtil.SHUTOUT_CARGO_MONITORING_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(transCargoList);
				result.setSuccess(true);
			}
			log.info("viewTransferredCargo result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// EndRegion getTransferredCargoDetails
}
