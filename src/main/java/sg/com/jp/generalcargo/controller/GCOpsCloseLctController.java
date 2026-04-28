package sg.com.jp.generalcargo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import sg.com.jp.generalcargo.domain.CloseLctValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.service.GCOpsCloseLctService;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = GCOpsCloseLctController.ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class GCOpsCloseLctController {

	public static final String ENDPOINT = "gc/gcOps";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";

	private static final Log log = LogFactory.getLog(GCOpsCloseLctController.class);
	String errorMessage = null;

	@Autowired
	private GCOpsCloseLctService closeLctService;

	// delegate.helper.gbms.ops.closeLct-->CloseLctHandler-->perform()
	@PostMapping(value = "/closeLctList")
	public ResponseEntity<?> closeLctList(HttpServletRequest request) throws BusinessException {
		TopsModel topsModel = new TopsModel();
		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: closeLctPerform criteria:" + criteria.toString());

			log.info("****CloseLctHandler****");
			String mode = CommonUtility.deNull(criteria.getPredicates().get("mode"));
			String vslCall = CommonUtility.deNull(criteria.getPredicates().get("vslCall"));
			String vslName;
			String inVoNo;
			String outVoNo;
			int searchMode = 0;
			if (StringUtils.isNotBlank(vslCall)) {
				searchMode = 1;
				String[] str = vslCall.split("/");
				vslName = str[0];
				inVoNo = str[1];
				outVoNo = str[2];
				map.put("vslCall", vslCall);
			} else {
				vslName = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
				inVoNo = CommonUtility.deNull(criteria.getPredicates().get("inVoNo"));
				outVoNo = CommonUtility.deNull(criteria.getPredicates().get("outVoNo"));
				if (null != vslName) {
					if ("null".equals(vslName)) {
						vslName = "";
					}
					vslName = vslName.trim();
				}
				if (null != inVoNo) {
					if ("null".equals(inVoNo)) {
						inVoNo = "";
					}
					inVoNo = inVoNo.trim();
				}
				if (null != outVoNo) {
					if ("null".equals(outVoNo)) {
						outVoNo = "";
					}
					outVoNo = outVoNo.trim();
				}
				map.put("vslName", vslName);
				map.put("inVoNo", inVoNo);
				map.put("outVoNo", outVoNo);
			}

			if (mode != null) {
				if (mode.equalsIgnoreCase("close")) {
					String vv_cds = criteria.getPredicates().get("hidVvcd");
					String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
					closeLctService.closeLct(vv_cds, userId);
					List<CloseLctValueObject> list = closeLctService.listLct(vv_cds);

					log.info("calling method");
					for (int i = 0; i < list.size(); i++) {
						CloseLctValueObject vo = (CloseLctValueObject) list.get(i);
						closeLctService.TriggerLct(vo, userId);
						closeLctService.insertOpsCCEventLogForLct(vo, userId);
					}
					log.info("vslDtls ~~~~~~~ : " + vv_cds);
				} else if (mode.equalsIgnoreCase("open")) // Added by MC consulting, For open LCT.
				{
					String vv_cds = criteria.getPredicates().get("hidVvcd");
					String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
					closeLctService.openLct(vv_cds, userId);
					// List<CloseLctValueObject> list = closeLctService.listLct(vv_cds);
					log.info("open LCT vslDtls ~~~~~~~ : " + vv_cds);
				}
			}
			List<CloseLctValueObject> vesselList = closeLctService.listVessel();
			map.put("vesselList", vesselList);
			List<CloseLctValueObject> lctList = new ArrayList<CloseLctValueObject>();
			// Amended by Jade for CR-CAB-20161006-002
			// when searchmode=0, meaning search by vsl name and in voy/out voy entered from
			// the screen. either in voy or out voy will do, do not ask for both
			// if (StringUtils.isNotBlank(vslName) && StringUtils.isNotBlank(inVoNo) &&
			// StringUtils.isNotBlank(outVoNo))
			if (StringUtils.isNotBlank(vslName) && (StringUtils.isNotBlank(inVoNo) || StringUtils.isNotBlank(outVoNo)))
			// CR-CAB-20161006-002
			{
						
				TableResult tableResult = closeLctService.listLct(vslName, inVoNo, outVoNo, searchMode, criteria);
				
				map.put("total", tableResult.getData().getTotal());
				
				int size = tableResult.getData().getListData().getTopsModel().size();
				CloseLctValueObject objList = null; 
				for (int i = 0; i < size; i++) {
					objList = (CloseLctValueObject) tableResult.getData().getListData().getTopsModel().get(i);
					lctList.add(objList);
					CloseLctValueObject clLctValObject = (CloseLctValueObject) lctList.get(i);
					topsModel.put(clLctValObject);
				}
						
						
			}
			map.put("lctList", topsModel);
			map.put("screen", "closeLctList");

		} catch (BusinessException e) {
			log.error("Exception: closeLctPerform ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		}catch (Exception e) {
			log.error("Exception: closeLctPerform",e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get("M4201");
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
				log.info("END: closeLctPerform result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

}
