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
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VslProdVO;
import sg.com.jp.generalcargo.service.GCOpsCloseVesselProdService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = GCOpsCloseVesselProdController.ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class GCOpsCloseVesselProdController {

	public static final String ENDPOINT = "gc/gcOps";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";

	private static final Log log = LogFactory.getLog(GCOpsCloseVesselProdController.class);
	String errorMessage = null;

	@Autowired
	private GCOpsCloseVesselProdService vesselProdListService;

	// delegate.helper.gbms.ops.vesselproductivity-->VesselProductivityListHandler
	@PostMapping(value = "/vesselProductivityList")
	public ResponseEntity<?> vesselProductivityList(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		String selSchmDesc = "";
		String vvcd = null;
		String noofGangs = "";
		String wrkblHatches = "";
		String delayOfWrk = null;
		String remarks = null;
		TableResult tableResult = new TableResult();
		try {
			log.info("START: vesselProductivityList criteria:" + criteria.toString());

			selSchmDesc = CommonUtility.deNull(criteria.getPredicates().get("vslSchemdCd"));
			vvcd = CommonUtility.deNull(criteria.getPredicates().get("hidVvcd"));
			noofGangs = CommonUtility.deNull(criteria.getPredicates().get("NoOfGangs"));
			wrkblHatches = CommonUtility.deNull(criteria.getPredicates().get("WrkHatches"));
			delayOfWrk = CommonUtility.deNull(criteria.getPredicates().get("delayReason"));
			remarks = CommonUtility.deNull(criteria.getPredicates().get("remarks"));
			String mode = CommonUtility.deNull(criteria.getPredicates().get("mode"));
			log.debug("selSchmDesc value : " + selSchmDesc + " vvcd : " + vvcd + " mode :  " + mode);
			if (selSchmDesc != null) {
				// if(selSchmDesc.equals("--Select All--")){
				// selSchmDesc = "";
				// }
			}

			boolean bRefresh = false;

			List<VslProdVO> vslSchemeCdList;
			String vslDtls = null;

			vslSchemeCdList = vesselProdListService.getVesselSchemeCode();
			map.put("VslSchemeCdList", vslSchemeCdList);

			if (mode != null) {
				if (mode.equalsIgnoreCase("updateRecord")) {
					List<String> delayOfWrkList = new ArrayList<String>();
					List<VslProdVO> updVslDtls = new ArrayList<VslProdVO>();
					updVslDtls = vesselProdListService.getUpdatedVslDtls(vvcd);
					delayOfWrkList = vesselProdListService.getDelayOfWork();
					map.put("updVslDtls", updVslDtls);
					map.put("delayOfWrkList", delayOfWrkList);
					map.put("request", "VesselProductivityList");
					result.setData(map);
					result.setSuccess(true);
					log.info("END: perform result: " + result.toString());
					return ResponseEntityUtil.success(result.toString());
				}
				// if user clicks on Update
				if (mode.equalsIgnoreCase("update")) {
					int noOfGangs;
					int wrkHatches;

					noOfGangs = (noofGangs.equals("") || noofGangs == null) ? 0 : Integer.parseInt(noofGangs);
					wrkHatches = (wrkblHatches.equals("") || wrkblHatches == null) ? 0 : Integer.parseInt(wrkblHatches);

					vesselProdListService.updateVesselInfo(vvcd, noOfGangs, wrkHatches, delayOfWrk, remarks);
					bRefresh = true;
					// LogManager.instance.logInfo("MODE IS UPDATE closedVslList -->> " +
					// closedVslList.size());

				}
				// if user clicks on the close button.
				if (mode.equalsIgnoreCase("close")) {
					String closeStatus = null;
					List<String> listItems =  new ArrayList<String>(); 
					String items = null;
					String size = CommonUtility.deNull(criteria.getPredicates().get("size"));
					int sizeSelected = !size.isEmpty() ? Integer.parseInt(size) : 0;
					if (sizeSelected != 0) {
						for (int i = 0; i < sizeSelected; i++) {
							items = (String) criteria.getPredicates().get("clsVslChkBox" + i);
							listItems.add(items);
						}
					}
					if (items != null) {
						for (int i = 0; i < listItems.size(); i++) {
							String delims = ",";
							String schemeCd = "";
							StringTokenizer tokens = new StringTokenizer(listItems.get(i), delims);
							while (tokens.hasMoreTokens()) {
								vvcd = tokens.nextToken();
								schemeCd = tokens.nextToken();
								// LogManager.instance.logInfo("vvcd : " + vvcd + " schemeCd : " + schemeCd);
								if (schemeCd.equalsIgnoreCase("JBT") || schemeCd.equalsIgnoreCase("000")
										|| schemeCd.equalsIgnoreCase("JWP") || schemeCd.equalsIgnoreCase("JNL")
										|| schemeCd.equalsIgnoreCase("JCT") || schemeCd.equalsIgnoreCase("JCL")
										|| schemeCd.equalsIgnoreCase("JWW")) {
									closeStatus = vesselProdListService.closeVessel(vvcd);
								} else {
									// if any vessel contains gangs supplied, workable hatches then we will allow to
									// close
									vslDtls = (String) vesselProdListService.getClosedVslDtls(vvcd);
									// LogManager.instance.logInfo("vslDtls ~~~~~~~ : " + vslDtls );
									if (!"".equals(vslDtls)) {
										closeStatus = vesselProdListService.closeVessel(vvcd);
									} else {
										// throw new BusinessException("M1005");
										errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get("M70002");
										throw new BusinessException("M70002");

									}
								}
								// LogManager.instance.logInfo("closeStatus ~~~~~~~ : " + closeStatus );
								// Cally SSL-OPS- 0000255 - Duplicate Vessel Productivity Bill Printed

								if (closeStatus != null && !closeStatus.equals("true"))
									throw new BusinessException(closeStatus);
							}
						}
					} else {

						int noOfGangs;
						int wrkHatches;

						noOfGangs = (noofGangs.equals("") || noofGangs == null) ? 0 : Integer.parseInt(noofGangs);
						wrkHatches = (wrkblHatches.equals("") || wrkblHatches == null) ? 0
								: Integer.parseInt(wrkblHatches);
						vesselProdListService.updateVesselInfo(vvcd, noOfGangs, wrkHatches, delayOfWrk, remarks);
						// Cally SSL-OPS- 0000255 - Duplicate Vessel Productivity Bill Printed
						closeStatus = vesselProdListService.closeVessel(vvcd);
						if (closeStatus != null && !closeStatus.equals("true"))
							throw new BusinessException(closeStatus);
					}
				}
			}
			if (selSchmDesc != null && mode.equals("SUBMIT")) {
				bRefresh = true;
			}
			if (bRefresh || (criteria.getPredicates().get("PageIndex") == null)
					|| (((String) criteria.getPredicates().get("PageIndex")).equals("null"))) {
				tableResult = vesselProdListService.getClosedVessels(selSchmDesc, criteria);

			}

			map.put("total", tableResult.getData().getTotal());
			map.put("topsModel", tableResult.getData().getListData().get(0));

			map.put("request", "VesselProductivityList");
		} catch (BusinessException e) {
			log.error("Exception vesselProductivityList : ", e);
			errorMessage = ConstantUtil.GC_OPS_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.error("Exception vesselProductivityList : ", e);
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
				log.info("END: vesselProductivityList result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

}
