package sg.com.jp.generalcargo.controller;

import java.util.Date;
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

import io.swagger.annotations.ApiOperation;
import sg.com.jp.generalcargo.domain.BalanceCargoVo;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableData;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.service.InwardCargoBalanceCargoService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = InwardCargoBalanceCargoController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InwardCargoBalanceCargoController {
	
	public static final String ENDPOINT = "gc/inwardcargo/balanceCargo";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private static final Log log = LogFactory.getLog(InwardCargoBalanceCargoController.class);
	private String errorMessage = null;
	
	@Autowired
	private InwardCargoBalanceCargoService balanceCargoService;
	
	
	@ApiOperation(value = "getVesselListForDPE", response = String.class)
	@PostMapping(value = "/getVesselListForDPE")  
	public ResponseEntity<?> getVesselListForDPE(HttpServletRequest request) {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		List<BalanceCargoVo> list = null;
		errorMessage = null;
		try {
			log.info("** getVesselListForDPE Start criteria :" + criteria.toString());
			list = balanceCargoService.getVesselListForDPE(criteria);
		} catch (BusinessException e) {
		 	log.info("Exception getVesselListForDPE : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
		} catch (Exception e) {
			log.info("Exception getVesselListForDPE : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result.setSuccess(false);
				result.setError(errorMessage);
		} else {
				result.setSuccess(true);
				result.setData(list);
			}
			log.info("getVesselListForDPE Result:" + result.toString());
		}
		return ResponseEntityUtil.success(result);
	}
	
	@ApiOperation(value = "getCompanyList", response = String.class)
	@PostMapping(value = "/getCompanyList")  
	public ResponseEntity<?> getCompanyList(HttpServletRequest request) {
		Criteria criteria = CommonUtil.getCriteria(request);
		errorMessage="";
		List<BalanceCargoVo> list = null;
		Result result = new Result();
		try {
			log.info("** getCompanyList Start criteria :" + criteria.toString());
			list = balanceCargoService.getCompanyList();
		} catch (BusinessException e) {
		 	log.info("Exception getCompanyList : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
		} catch (Exception e) {
			log.info("Exception getCompanyList : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result.setSuccess(false);
				result.setError(errorMessage);
		} else {
				result.setSuccess(true);
				result.setData(list);
			}
			log.info("getCompanyList Result:" + result.toString());
		}
		return ResponseEntityUtil.success(result);
	}
	
	@ApiOperation(value = "getOutStanding Cargo List ", response = String.class)
	@PostMapping(value = "/getCargoDeliveryList") //Top in old jp code
	public ResponseEntity<?> getCargoDeliveryList(HttpServletRequest request) {
		Criteria criteria = CommonUtil.getCriteria(request);
		String cargoType = CommonUtility.deNull(criteria.getPredicates().get("cargoType"));
		TableData tableData=null;
		TableResult tableResult = new TableResult();
		errorMessage="";
		try {
			log.info("** getCargoDeliveryList Start criteria :" + criteria.toString());
			if ("OSC".equalsIgnoreCase(cargoType)) {
				tableData = balanceCargoService.getOutStandingCargoList(criteria); 	
			} else{
				//getCargoCompletedList
				tableData = balanceCargoService.getCompletedDeliveryCargoList(criteria);
			}
			log.info("table data :"+ tableData.toString());	
		} catch (BusinessException e) {
		 	log.info("Exception getCargoDeliveryList : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
		} catch (Exception e) {
			log.info("Exception getCargoDeliveryList : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				HashMap<String, String> errorMspMap = new HashMap<String,String>();
				errorMspMap.put("errorMessage", errorMessage);
				tableResult.setSuccess(false);
				tableResult.setErrors(errorMspMap);
		} else {
				tableResult.setData(tableData);
				tableResult.setSuccess(true);
			}
			log.info("getCargoDeliveryList Result:" + tableResult.toString());
		}
		return ResponseEntityUtil.success(tableResult);
	}
	
	@ApiOperation(value="update Cargo Balance Details",response = String.class)
	@PostMapping(value ="/updateCargoDetail")
	public ResponseEntity<?> updateCargoDetail(HttpServletRequest request){
		Criteria criteria = CommonUtil.getCriteria(request);
		String cargoType = CommonUtility.deNull(criteria.getPredicates().get("cargoType"));
		Result result = new Result();
		Map<String,Object> map = new HashMap<>();
		errorMessage="";
		try {
			String dateTime = CommonUtility.formatDateToStr(new Date(), "ddMMyyyy HHmm");
			log.info("** Update Cargo balance Details Start :" + criteria.toString());
			 //commented by Dongsheng on 14/1/2013. For CR-CIM-20121224-003. Change to use inVoyNbr
			//String outVoyNbr = (String)request.getSession(false).getAttribute("outVoy"));
			/*Amended by Punitha on 16/02/2009 to add BL Nbr
			String edoAsnNbr = (String)request.getSession(false).getAttribute("esnEdo");*/
		 	/*Amended by Punitha on 17/02/2009*/
			String updateEdoAsnNbr =  CommonUtil.deNull(criteria.getPredicates().get("edoAsnNbr"));
			String updateBlNbr =  CommonUtil.deNull(criteria.getPredicates().get("blNbr"));
			String balancePkgs = CommonUtil.deNull(criteria.getPredicates().get("balancePackages"));
			String actionRemarks = CommonUtil.deNull(criteria.getPredicates().get("actionRemarks"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			/*Amended by Punitha on 17/02/2009*/
			//20180108 koktsing
			String vesselVvCd = CommonUtil.deNull(criteria.getPredicates().get("vesselVvCd"));

			log.info("Before updating balance &&& :" +updateEdoAsnNbr + ":"+updateBlNbr);

			//SSL No SL-CIM-20170927-01
			//20171207 koktsing call the new overridden method updateCargoBalanceStatus
			balanceCargoService.updateCargoBalanceStatus(updateBlNbr, vesselVvCd, Long.parseLong(balancePkgs), actionRemarks,userId,dateTime);
			if (updateEdoAsnNbr!=null && !updateEdoAsnNbr.equalsIgnoreCase("")) {
				balanceCargoService.updateCargoBalanceStatus(Long.parseLong(updateEdoAsnNbr), Long.parseLong(balancePkgs), actionRemarks,userId,dateTime);
			}
			log.info("After updating balance &&& :" +updateEdoAsnNbr +":"+updateBlNbr);

			TableData tableData=null;
			if ("OSC".equalsIgnoreCase(cargoType)) {
				tableData = balanceCargoService.getOutStandingCargoList(criteria); 
				
			}else{
				//getCargoCompletedList
				tableData = balanceCargoService.getCompletedDeliveryCargoList(criteria);
			}
			
			map.put("BalaneCargoList", tableData.getListData());
			map.put("total", tableData.getTotal());
			map.put("totalPages", tableData.getTotalPage());
			map.put("pageIndex", tableData.getPageIndex());
			result.setData(map);
			result.setSuccess(true);
		} catch (BusinessException e) {
		 	log.info("Exception updateCargoDetail : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
		} catch (Exception e) {
			log.info("Exception updateCargoDetail : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result.setSuccess(false);
				result.setError(errorMessage);
		} else {
				result.setSuccess(true);
				result.setData(map);
			}
			log.info("Update Cargo balance Details Result:" + result.toString());
		}	
		return ResponseEntityUtil.success(result);
	}
	
	@ApiOperation(value="Download Cargo Balance Details",response = String.class)
	@PostMapping(value ="/downloadBalanceCargoList")
	public ResponseEntity<?> downloadBalanceCargoList(HttpServletRequest request){
		Criteria criteria = CommonUtil.getCriteria(request);
		String cargoType = CommonUtil.deNull(criteria.getPredicates().get("cargoType"));
		Result result = new Result();
		errorMessage = null;
		TableData tableData=null;
		try {
			log.info("** downloadBalanceCargoList Start criteria :" + criteria.toString());
			if ("OSC".equalsIgnoreCase(cargoType)) {
				tableData = balanceCargoService.getOutStandingCargoList(criteria); 
			}else{
				//getCargoCompletedList
				tableData = balanceCargoService.getCompletedDeliveryCargoList(criteria);
			}
		} catch (BusinessException e) {
		 	log.info("Exception downloadBalanceCargoList : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
		} catch (Exception e) {
			log.info("Exception downloadBalanceCargoList : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result.setSuccess(false);
				result.setError(errorMessage);
		} else {
				result.setData(tableData.getListData());
				result.setSuccess(true);
			}
			log.info("downloadBalanceCargoList Result:" + result.toString());
		}
		return ResponseEntityUtil.success(result);
	
	}
}
