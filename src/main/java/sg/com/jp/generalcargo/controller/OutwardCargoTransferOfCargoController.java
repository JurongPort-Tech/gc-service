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
import sg.com.jp.generalcargo.domain.EsnListValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.OutwardCargoTransferOfCargoService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = OutwardCargoTransferOfCargoController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OutwardCargoTransferOfCargoController {

	public static final String ENDPOINT = "gc/outwardcargo/outTransferOfCargo";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(OutwardCargoTransferOfCargoController.class);

	@Autowired
	private OutwardCargoTransferOfCargoService transferOfCargoService;

	private TopsModel topsModel = null;

	// delegate.helper.gbms.cargo.esn -->TransferCrgVslListHandler
	@PostMapping(value = "/transferCrgVslList") //
	public ResponseEntity<?> transferCrgVslList(HttpServletRequest request) throws BusinessException {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			errorMessage = null;
			log.info("** transferCrgVslList Start criteria :" + criteria.toString());

			topsModel = new TopsModel();

			String custCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String mode = "";
			String varnoF = "";
			String varnoT = "";

			mode = criteria.getPredicates().get("mode");

			String vslf = CommonUtility.deNull(criteria.getPredicates().get("vslf"));
			String vslt = CommonUtility.deNull(criteria.getPredicates().get("vslt"));
			String voyf = CommonUtility.deNull(criteria.getPredicates().get("voyf"));
			String voyt = CommonUtility.deNull(criteria.getPredicates().get("voyt"));

			String vslvoyT = CommonUtility.deNull(criteria.getPredicates().get("vslvoyT"));

			String vslnm = ""; //warning but vslnm need in service param to seprate.
			String outvoynbr = "";
			String sysdate = "";

			StringTokenizer vslOutvoy = null;

			if (vslvoyT != null && !vslvoyT.equals("")) {
				vslOutvoy = new java.util.StringTokenizer(vslvoyT,"-");
				vslnm = vslOutvoy.nextToken().trim();
				outvoynbr = vslOutvoy.nextToken().trim();
			}
			log.info("vslNm: " + vslnm);

			sysdate = (String) transferOfCargoService.getSysdate();
			map.put("sysdate", sysdate);

			List<EsnListValueObject> bk_details = new ArrayList<EsnListValueObject>();

			if (mode != null && !mode.equals("") && mode.equals("bkdetails")) {

				List<VesselVoyValueObject> vesselSel_F = transferOfCargoService.getTransferVslCrgList_F(vslf, voyf);
				List<VesselVoyValueObject> vesselSel_T = transferOfCargoService.getTransferVslCrgList_T(vslt, voyt);
				
				log.info("vesselSel_F : " + vesselSel_F);
				log.info("vesselSel_T : " + vesselSel_T);

				/*
				 * varnoF = esn.getVarno(vslf,voyf); varnoT =
				 * esn.getVarno(vslt,vooutwardCargoTransferOfCargoService.
				 */
				varnoF = transferOfCargoService.getTransferVarno(vslf, voyf, "F", custCd);
				varnoT = transferOfCargoService.getTransferVarno(vslt, voyt, "T", custCd);
				log.info("varnof bkdetails " + varnoF);
				log.info("varnot bkdetails " + varnoT);

				// CR-CIM- 0000109
				if (varnoF.equals("")) {
					errorMessage = ConstantUtil.ErrorMsg_Transfer_Of_Cargo1;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: transferCrgVslList result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
				if (varnoT.equals("")) {
					errorMessage = ConstantUtil.ErrorMsg_Transfer_Of_Cargo2;

					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setErrors(map);
						result.setSuccess(false);
						result.setData(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: transferCrgVslList result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
				// bk_details = (Vector)esn.getTransferDetails(varnoF);
				bk_details = transferOfCargoService.getTransferDetails(varnoF, custCd);
				map.put("bk_details", bk_details);
				map.put("varnoF", varnoF);
				map.put("varnoT", varnoT);
				map.put("topsModel", topsModel);
				map.put("TransferCrgDetailsList", "TransferCrgDetailsList");
			} else if (mode != null && !mode.equals("") && mode.equals("update")) {
				varnoF = CommonUtility.deNull(criteria.getPredicates().get("varnoF"));
				varnoT = CommonUtility.deNull(criteria.getPredicates().get("varnoT"));

				log.info("varnof update " + varnoF);
				log.info("varnot update " + varnoT);

				String bk_ref_nbr[] = request.getParameterValues("bk_ref_nbr");
				String newbknbr[] = request.getParameterValues("newbknbr");
				String esn_asn_nbr[] = request.getParameterValues("esn_asn_nbr");
				String trans_type[] = request.getParameterValues("trans_type");
				String transNbr[] = request.getParameterValues("transNbr");

				String shutoutqty[] = request.getParameterValues("shutoutqty");
				String actnbrshipped[] = request.getParameterValues("actnbrshipped");
				String uanbrpkgs[] = request.getParameterValues("uanbrpkgs");
				String ftransdttm[] = request.getParameterValues("ftransdttm");

				List<EsnListValueObject> vc1 = new ArrayList<EsnListValueObject>();

				vc1 = transferOfCargoService.TransferCrgUpdateForDPE(bk_ref_nbr, newbknbr, esn_asn_nbr, trans_type,
						transNbr, shutoutqty, actnbrshipped, uanbrpkgs, ftransdttm, outvoynbr, varnoF, varnoT, UserID);

				map.put("bk_details_Succ", vc1);
				map.put("varnoF", varnoF);
				map.put("varnoT", varnoT);
				map.put("topsModel", topsModel);
				map.put("TransferCrgDetailsView", "TransferCrgDetailsView");
			} else {
				map.put("topsModel", topsModel);
				map.put("TransferCrgVslList", "TransferCrgVslList");
			}

		} catch (BusinessException e) {
			log.info("Exception transferCrgVslList", e);
			errorMessage = ConstantUtil.TRANSFER_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null)
				errorMessage = CommonUtility.getExceptionMessage(e);
		} catch (Exception e) {
			log.info("Exception transferCrgVslList", e);
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} finally {
			log.info("transferCrgVslList End");
		}
		if (errorMessage != null) {
			map.put("error", errorMessage);
			result = new Result();
			result.setError(errorMessage);
			result.setSuccess(false);

		} else {
			result = new Result();
			result.setData(map);
			result.setSuccess(true);

		}
		return ResponseEntityUtil.success(result.toString());
	}

}
