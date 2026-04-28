package sg.com.jp.generalcargo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import sg.com.jp.generalcargo.domain.AccessCompanyValueObject;
import sg.com.jp.generalcargo.domain.BkRefActionTrailDetails;
import sg.com.jp.generalcargo.domain.BookRefvoyageOutwardValueObject;
import sg.com.jp.generalcargo.domain.BookingReferenceFileUploadDetails;
import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.CompanyValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.PageDetails;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.Summary;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.OutwardCargoBookingReferenceService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = OutwardCargoBookingReferenceController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OutwardCargoBookingReferenceController {

	public static final String ENDPOINT = "gc/outwardcargo/bookingReference";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(OutwardCargoBookingReferenceController.class);

	@Autowired
	private OutwardCargoBookingReferenceService bookingReferenceService;

	// delegate.helper.gbms.cargo.bookingReference --> BRAddHandler
	@PostMapping(value = "/BRAdd")
	public ResponseEntity<?> BRAdd(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		TopsModel topsModel = new TopsModel();
		errorMessage = null;
		try {
			log.info("BRAdd : START " + criteria.toString());
			String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String mode = CommonUtility.deNull(criteria.getPredicates().get("mode"));

			String varno = "";
			String vslName = "";
			String status = "-1";

			// A default value for showing the buttons.........
			map.put("showCancel", "Y");
			map.put("showAmend", "Y");
			topsModel = new TopsModel();

			String bkRefNbr = "";

			String coCd = companyCode;
			// Added by thanhnv2::Start
			map.put("usrtyp", coCd);

			if (!mode.equals("doadd")) {
				List<List<String>> cargoTypeVect = bookingReferenceService.getCargoType();
				map.put("cargoTypeVect", cargoTypeVect);
				// BEGIN added by Maksym JCMS Smart CR 6.10
				if (criteria.getPredicates().get("shipname") != null) {
					StringTokenizer st = new StringTokenizer(
							CommonUtility.deNull(criteria.getPredicates().get("shipname")), "-");
					vslName = st.nextToken();
					String vslType = bookingReferenceService.getVslTypeCdByFullName(vslName);
					map.put("vslType", vslType);
				}

				String vslCarCarrier = bookingReferenceService.getCarCarrierVesselCode();
				map.put("vslCarCarrier", vslCarCarrier);

				String defaultCargoCategoryCode = bookingReferenceService.getDefaultCargoCategoryCode();
				map.put("defaultCargoCategoryCode", defaultCargoCategoryCode);

				String cargoTypeNotShow = bookingReferenceService.getCargoTypeNotShow();
				map.put("cargoTypeNotShow", cargoTypeNotShow);

				List<BookingReferenceValueObject> brvoList = bookingReferenceService.getBRVOList("other");
				map.put("brvoList", brvoList);

				Map<String, String> cargoCategoryCode_cargoCategoryName = bookingReferenceService
						.getCargoCategoryCode_CargoCategoryName();
				map.put("cargoCategoryCode_cargoCategoryName", cargoCategoryCode_cargoCategoryName);
				boolean showAllCargoCategory = bookingReferenceService.isShowAllCargoCategoryCode(coCd);
				map.put("showAllCargoCategory", showAllCargoCategory);
				String notShowCargoCategoryCode = bookingReferenceService.getNotShowCargoCategoryCode();
				map.put("notShowCargoCategoryCode", notShowCargoCategoryCode);
				// END added by Maksym JCMS Smart CR 6.10
			}
			// Condition for adding into bk details
			// Added by thanhnv2::Start
			if ("JP".equals(coCd) && !mode.equals("doadd")) {
				varno = (String) CommonUtility.deNull(criteria.getPredicates().get("selOutwardvoyage"));
				varno = varno.substring(0, 10);
				List<AccessCompanyValueObject> autPartyList = new ArrayList<AccessCompanyValueObject>();
				autPartyList = bookingReferenceService.getAutPartyListOfVessel(varno);
				List<String> comCd = new ArrayList<String>();
				List<String> coNm = new ArrayList<String>();
				for (int i = 0; i < autPartyList.size(); i++) {
					comCd.add(((AccessCompanyValueObject) autPartyList.get(i)).getCompanyCode());
					coNm.add(((AccessCompanyValueObject) autPartyList.get(i)).getCompanyName());
				}
				map.put("companyCode", comCd);
				map.put("companyName", coNm);

				String createCust = bookingReferenceService.getCreateCustCdOfVessel(varno);
				map.put("createCust", createCust);

			}
			// Added by thanhnv2::End
			if (mode.equals("doadd")) {
				// Added by thanhnv2::Start
				if ("JP".equals(coCd)) {
					coCd = (String) CommonUtility.deNull(criteria.getPredicates().get("autParty"));
				}
				// Added by thanhnv2::End
				// bkRefNbr = request.getParameter("txtBk1") +
				// request.getParameter("txtBk2");
				// Sripriya 27/05/2011 to trim space
				String txtBk1 = CommonUtility.deNull(criteria.getPredicates().get("txtBk1"));
				// String txtBk2 = request.getParameter("txtBk2");
				bkRefNbr = txtBk1.trim();
				bkRefNbr = txtBk1.replaceAll(" ", "");
				// bkRefNbr = bkRefNbr.trim();
				String crgStatus = "";// request.getParameter("txt");
				varno = CommonUtility.deNull(criteria.getPredicates().get("varno"));
				String cntrNo = CommonUtility.deNull(criteria.getPredicates().get("noCont"));
				if (cntrNo.equals("")) {
					cntrNo = "0";
				}
				String outVoyNbr = CommonUtility.deNull(criteria.getPredicates().get("outNo"));
				String conrCode = "";
				String cargoType = CommonUtility.deNull(criteria.getPredicates().get("selCargotype"));
				// BEGIN added by Maksym JCMS Smart CR 6.10
				String cargoCategory = CommonUtility.deNull(criteria.getPredicates().get("cargoCategory"));
				// END added by Maksym JCMS Smart CR 6.10
				String shpCrNo = CommonUtility.deNull(criteria.getPredicates().get("txtCrno"));
				String shpContactNo = CommonUtility.deNull(criteria.getPredicates().get("txtShpContact"));
				String shpAddr = CommonUtility.deNull(criteria.getPredicates().get("txtShpAdr"));
				String shpNm = CommonUtility.deNull(criteria.getPredicates().get("txtShpNm"));

				// Added on 25 June 2014 to trim space
				if (shpNm != null && !shpNm.equals("")) {
					shpNm = shpNm.trim();
				}

				String shipperCoyCode = CommonUtility.deNull(criteria.getPredicates().get("lstShipper"));
				shpCrNo = shipperCoyCode;

				// To get shipper name
				if (shipperCoyCode != null && !shipperCoyCode.equals("")) {
					if (!shipperCoyCode.equalsIgnoreCase("OTHERS")) {
						try {
							CompanyValueObject vo = bookingReferenceService.getCompanyInfo(shipperCoyCode);
							shpNm = vo.getCompanyName();
							if (shpNm == null || shpNm.equals("")) {
								errorMessage = ConstantUtil.ErrorMsg_Company_info_not_found;
								if (errorMessage != null) {
									map.put("errorMessage", errorMessage);
									result = new Result();
									result.setErrors(map);
									result.setSuccess(false);
									result.setData(map);

								} else {
									result.setData(map);
									result.setSuccess(true);
									log.info("END: BRAdd result: " + result.toString());
								}

								return ResponseEntityUtil.success(result.toString());
							}

						} catch (Exception e) {
							errorMessage = ConstantUtil.ErrorMsg_finding_company_error;
							if (errorMessage != null) {
								map.put("errorMessage", errorMessage);
								result = new Result();
								result.setErrors(map);
								result.setSuccess(false);
								result.setData(map);

							} else {
								result.setData(map);
								result.setSuccess(true);
								log.info("END: BRAdd result: " + result.toString());
							}

							return ResponseEntityUtil.success(result.toString());
						}
					} else {
						// do nothing. get the co name from parameter passed in
					}

				}

				if (shpNm == null || shpNm.equals("")) {
					errorMessage = ConstantUtil.ErrorMsg_Shipping_Company_Not_Found;
					if (errorMessage != null) {
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setError(errorMessage);
						result.setSuccess(false);
						result.setErrors(map);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: BRAdd result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
				String bkWt = CommonUtility.deNull(criteria.getPredicates().get("txtBk"));
				String bkVol = CommonUtility.deNull(criteria.getPredicates().get("txtVolume"));
				String bkNoOfPkg = CommonUtility.deNull(criteria.getPredicates().get("txtPack"));
				String varPkgs = CommonUtility.deNull(criteria.getPredicates().get("txtVarPack"));
				String varVol = CommonUtility.deNull(criteria.getPredicates().get("txtVarVol"));
				String varWt = CommonUtility.deNull(criteria.getPredicates().get("txtWtVar"));
				String portDis = CommonUtility.deNull(criteria.getPredicates().get("txtPortDischarge"));
				String adpCustCd = CommonUtility.deNull(criteria.getPredicates().get("txtEsnDecl"));
				String contSize = CommonUtility.deNull(criteria.getPredicates().get("contSize"));
				// START FTZ CR - NS JUNE 2024
				String conName = CommonUtility.deNull(criteria.getPredicates().get("conName"));
				String consigneeAddr = CommonUtility.deNull(criteria.getPredicates().get("consigneeAddr"));
				String notifyParty = CommonUtility.deNull(criteria.getPredicates().get("notifyParty"));
				String notifyPartyAddr = CommonUtility.deNull(criteria.getPredicates().get("notifyPartyAddr"));
				String placeofDelivery = CommonUtility.deNull(criteria.getPredicates().get("placeofDelivery"));
				String placeofReceipt = CommonUtility.deNull(criteria.getPredicates().get("placeofReceipt"));
				String blNbr = CommonUtility.deNull(criteria.getPredicates().get("blNbr"));
				// END FTZ CR - NS JUNE 2024

				if (contSize == null) {
					contSize = "";
				}
				String contType = CommonUtility.deNull(criteria.getPredicates().get("contType"));
				// System.out.println("container type " + contType);
				if (contType == null) {
					contType = "";
				}

				// Checking server side..........
				String portStatus = bookingReferenceService.chkPortCode(portDis);
				String bkref = bookingReferenceService.chkBKCode(bkRefNbr);
				String esnStatus = bookingReferenceService.chkCrNo(adpCustCd);

				if (bkref.equals("Y")) {
					map.put("errorDisplay", "The Bk number already exsist enter new one");
				} else if (portStatus.equals("N")) {
					map.put("errorDisplay", "Enter a Valid Port Code");
				} else if (esnStatus.equals("N")) {
					map.put("errorDisplay", "Enter a Valid ESN Declarant Code");
				} else {

					status = bookingReferenceService.insertBK(bkRefNbr, crgStatus, varno, cntrNo, contType, contSize,
							outVoyNbr, conrCode,
							// BEGIN amended by Maksym JCMS Smart CR 6.10
							// cargoType, shpCrNo, shpContactNo, shpAddr,
							cargoType, cargoCategory, shpCrNo, shpContactNo, shpAddr,
							// END amended by Maksym JCMS Smart CR 6.10
							shpNm, bkWt, bkVol, bkNoOfPkg, varPkgs, varVol, varWt, portDis, adpCustCd, coCd, userId
							,conName, consigneeAddr, notifyParty, notifyPartyAddr, placeofDelivery,
							placeofReceipt, blNbr);

				} // END IF
			} // Do add (end if)

			if (varno == null || varno.equals("")) {
				StringTokenizer selOutwardvoyage = new StringTokenizer(
						CommonUtility.deNull(criteria.getPredicates().get("selOutwardvoyage")), "*");
				varno = selOutwardvoyage.nextToken();
			}

			// 30/06/2011 PCYAP To check tonnage for manifest/booking/ship store
			int maxCargoTon = bookingReferenceService.retrieveMaxCargoTon(varno);
			map.put("maxCargoTon", new Integer(maxCargoTon));

			map.put("ListData", topsModel);

			if (status.equals("Y")) {
				// Fetching details for the next page .....................
				List<BookingReferenceValueObject> BKDetails = bookingReferenceService.fetchBKDetails(bkRefNbr);
				BookingReferenceValueObject bookingReferenceValueObjectDis = new BookingReferenceValueObject();
				bookingReferenceValueObjectDis = (BookingReferenceValueObject) BKDetails.get(0);
				map.put("bookingReferenceValueObject", bookingReferenceValueObjectDis);
				// nextScreen(request, "BRDis");
			} else {
				// nextScreen(request, "BRAdd");
			}

		} catch (BusinessException e) {
			log.info("Exception BRAdd : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception BRAdd : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.clear();
				map.put("errorMessage", errorMessage);
				result.setSuccess(false);
				result.setErrors(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: BRAdd result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.cargo.bookingReference --> BRCancelHandler
	@PostMapping(value = "/BRCancel")
	public ResponseEntity<?> BRCancel(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		errorMessage = null;
		try {
			log.info("BRCancel : START " + criteria.toString());
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			// String status =
			// bookingReferenceEJBRemote.cancelBK(CommonUtility.deNull(criteria.getPredicates().get("txtBR"));
			String status = bookingReferenceService
					.cancelBK(CommonUtility.deNull(criteria.getPredicates().get("txtBR")), userId);
			map.put("status", status);
			// end changed by Irene Tan on 14 Jun 2004
			if (status.equals("Y")) {
				// nextScreen(request, "BRCancel");
			}
		} catch (BusinessException e) {
			log.info("Exception BRCancel : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception BRCancel : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.clear();
				map.put("errorMessage", errorMessage);
				result.setSuccess(false);
				result.setErrors(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: BRCancel result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.cargo.bookingReference --> BRSearchHandler
	@PostMapping(value = "/BRSearch")
	public ResponseEntity<?> BRSearch(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		errorMessage = null;
		try {
			log.info("BRSearch : START " + criteria.toString());
			String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String actioncmd = null;
			String companycode = null;
			companycode = companyCode;
			actioncmd = CommonUtility.deNull(criteria.getPredicates().get("actioncmd"));

			if (actioncmd.equalsIgnoreCase("show")) {

				Map<String, String> cargoCategoryCode_cargoCategoryName = bookingReferenceService
						.getCargoCategoryCode_CargoCategoryName();
				map.put("cargoCategoryCode_cargoCategoryName", cargoCategoryCode_cargoCategoryName);

				BookingReferenceValueObject bookingreferencevalueobject = new BookingReferenceValueObject();

				// Vector valuevector = bookingreferenceejbremote.fetchBKDetails(
				// request.getParameter("bknbr"));
				// ++ changed by vietnd02 6/11/09
				List<BookingReferenceValueObject> valuevector = bookingReferenceService
						.getBrSearchDetails(CommonUtility.deNull(criteria.getPredicates().get("bknbr")), companycode);
				// -- vietnd02 6/11/09

				if (valuevector == null || valuevector.size() <= 0) {
					throw new BusinessException("M20629");
				}

				bookingreferencevalueobject = (BookingReferenceValueObject) valuevector.get(0);
				String chkcancelc = bookingReferenceService
						.chkCancelAmend(CommonUtility.deNull(criteria.getPredicates().get("brno")), companycode, "C");
				String chkcancela = bookingReferenceService
						.chkCancelAmend(CommonUtility.deNull(criteria.getPredicates().get("brno")), companycode, "A");
				if (!chkcancelc.equals("N"))
					map.put("showCancel", "N");
				else
					map.put("showCancel", "Y");
				if (!chkcancela.equals("N"))
					map.put("showAmend", "N");
				else
					map.put("showAmend", "Y");

				Hashtable<String, String> values = bookingReferenceService
						.getVoyageDetails(CommonUtility.deNull(criteria.getPredicates().get("bknbr")));

				// Added by Irene Tan on 03 March 2005 : To include search Booking Reference
				// Changed by Irene Tan on 07 March 2005 : To use abbreviated vessel name
				// map.put("selOutwardvoyage",
				// values.get("varno")+"*"+bookingreferencevalueobject.getVesselName());
				map.put("selOutwardvoyage", values.get("varno") + "*" + bookingreferencevalueobject.getAbbrVslName());
				// End Changed by Irene Tan on 07 March 2005 : To use abbreviated vessel name
				// End Added by Irene Tan on 03 March 2005 : To include search Booking Reference

				map.put("shipnm", bookingreferencevalueobject.getVesselName());
				map.put("varno", values.get("varno"));
				map.put("outvoy", values.get("outvoy"));
				map.put("vslid", values.get("vslid"));

				map.put("cargoTypeVect", bookingReferenceService.getCargoType());
				map.put("bookingReferenceValueObject", bookingreferencevalueobject);
				map.put("cargoCategoryCode_cargoCategoryName", cargoCategoryCode_cargoCategoryName);
			}

		} catch (BusinessException e) {
			log.info("Exception BRSearch : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception BRSearch : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.clear();
				map.put("errorMessage", errorMessage);
				result.setErrors(map);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: BRSearch result: " + result.toString());
			}
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
			log.info("** reportAction Start criteria :" + criteria.toString());

			String method = CommonUtility.deNull(criteria.getPredicates().get("method"));
			String keyword = CommonUtility.deNull(criteria.getPredicates().get("filter"));
			String startValue = CommonUtility.deNull(criteria.getPredicates().get("start"));
			String limitValue = CommonUtility.deNull(criteria.getPredicates().get("limit"));
			if (keyword == null) {
				keyword = "";
			}
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
			log.info("Filter: " + keyword + " Start: " + start + " Limit: " + limit + " FilterStart: " + filterStart);
			if (method.equals("listCompany")) {

				if (!"".equalsIgnoreCase(filterStart) && filterStart != null) {
					companyList = bookingReferenceService.listCompanyStart(keyword, start, limit);
				} else {
					companyList = bookingReferenceService.listCompany(keyword, start, limit);
				}
			}

			map.put("data", companyList);
			map.put("total", new Integer(companyList.size()));
		} catch (BusinessException e) {
			log.info("Exception reportAction : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception reportAction : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.clear();
				map.put("errorMessage", errorMessage);
				result.setErrors(map);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: reportAction result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.cargo.bookingReference --> BRUpdateHandler
	@PostMapping(value = "/BRUpdateView")
	public ResponseEntity<?> BRUpdateView(HttpServletRequest request) throws BusinessException {
		return this.BRUpdate(request);
	}

	// delegate.helper.gbms.cargo.bookingReference --> BRUpdateHandler
	@PostMapping(value = "/BRUpdate")
	public ResponseEntity<?> BRUpdate(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		TopsModel topsModel = new TopsModel();
		errorMessage = null;
		try {
			log.info("BRUpdate : START " + criteria.toString());
			String mode = CommonUtility.deNull(criteria.getPredicates().get("mode"));
			String disabled = CommonUtility.deNull(criteria.getPredicates().get("disabled"));
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String userCoyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String brno = CommonUtility.deNull(criteria.getPredicates().get("brno"));
			String fromESN = CommonUtility.deNull(criteria.getPredicates().get("fromESN"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			if (mode == null) {
				mode = "";
			}
			String updateBkRetStatus = "";
			String bkRefNbr = "";
			String varno = "";
			String nocont = "";
			String vslId = "";
			String selCargoType = CommonUtility.deNull(criteria.getPredicates().get("selCargotype"));
			String cargoCategory = CommonUtility.deNull(criteria.getPredicates().get("cargoCategory"));
			String txtCrno = CommonUtility.deNull(criteria.getPredicates().get("txtCrno"));
			String shipperContact = CommonUtility.deNull(criteria.getPredicates().get("txtShpContact"));
			String shipperAddress = CommonUtility.deNull(criteria.getPredicates().get("txtShpAdr"));
			String shipperName = CommonUtility.deNull(criteria.getPredicates().get("txtShpNm"));
			String shipperCoyCode = "";
			String txtBk = CommonUtility.deNull(criteria.getPredicates().get("txtBk"));
			String txtVolume = CommonUtility.deNull(criteria.getPredicates().get("txtVolume"));
			String txtPack = CommonUtility.deNull(criteria.getPredicates().get("txtPack"));
			String txtVarPack = CommonUtility.deNull(criteria.getPredicates().get("txtVarPack"));
			String txtVarVol = CommonUtility.deNull(criteria.getPredicates().get("txtVarVol"));
			String txtWtVar = CommonUtility.deNull(criteria.getPredicates().get("txtWtVar"));
			String txtPortDischarge = CommonUtility.deNull(criteria.getPredicates().get("txtPortDischarge"));
			String txtEsnDecl = CommonUtility.deNull(criteria.getPredicates().get("txtEsnDecl"));
			String contType = CommonUtility.deNull(criteria.getPredicates().get("contType"));
			String contSize = CommonUtility.deNull(criteria.getPredicates().get("contSize"));
			String noCont = CommonUtility.deNull(criteria.getPredicates().get("noCont"));
			String disabledAmend = CommonUtility.deNull(criteria.getPredicates().get("disabledAmend"));
			String autParty = CommonUtility.deNull(criteria.getPredicates().get("autParty"));
			String shipname = CommonUtility.deNull(criteria.getPredicates().get("shipname"));
			String selOutwardvoyage = CommonUtility.deNull(criteria.getPredicates().get("selOutwardvoyage"));
			String txtBR = CommonUtility.deNull(criteria.getPredicates().get("txtBR"));
			String txtShpContact = CommonUtility.deNull(criteria.getPredicates().get("txtShpContact"));
			String txtShpAdr = CommonUtility.deNull(criteria.getPredicates().get("txtShpAdr"));
			String txtShpNm = CommonUtility.deNull(criteria.getPredicates().get("txtShpNm"));
			String lstShipper = CommonUtility.deNull(criteria.getPredicates().get("lstShipper"));

			map.put("txtBk", txtBk);
			map.put("txtVolume", txtVolume);
			map.put("txtPack", txtPack);
			map.put("txtVarPack", txtVarPack);
			map.put("txtVarVol", txtVarVol);
			map.put("txtWtVar", txtWtVar);
			map.put("txtPortDischarge", txtPortDischarge);
			map.put("txtEsnDecl", txtEsnDecl);
			map.put("contType", contType);
			map.put("contSize", contSize);

			map.put("showCancel", "Y");
			map.put("showAmend", "Y");
			topsModel = new TopsModel();
			BookingReferenceValueObject brvo = new BookingReferenceValueObject();
			map.put("usrtyp", userCoyCode);

			List<List<String>> vector = bookingReferenceService.getCargoType();
			map.put("cargoTypeVect", vector);
			// BEGIN added by Maksym JCMS Smart CR 6.10
			Map<String, String> cargoCategoryCode_cargoCategoryName = bookingReferenceService
					.getCargoCategoryCode_CargoCategoryName();
			map.put("cargoCategoryCode_cargoCategoryName", cargoCategoryCode_cargoCategoryName);

			if (mode.equals("showlist")) {
				List<BookingReferenceValueObject> vector1 = bookingReferenceService.fetchBKDetails(brno);
				brvo = (BookingReferenceValueObject) vector1.get(0);
				// HoaBT2 added: get parameter from ESN screen
				map.put("fromESN", fromESN);
				String checkCancelAmendResultC = bookingReferenceService.chkCancelAmend(brno, userCoyCode, "C");
				String checkCancelAmendResultA = bookingReferenceService.chkCancelAmend(brno, userCoyCode, "A");

				boolean userDBBookingRefer = bookingReferenceService.getCheckUserBookingReference(coCd, brno);
				log.info("user DB Booking Refer ==== " + userDBBookingRefer);

				String userIdDBBookingRefe = "FALSE";
				if (userDBBookingRefer == true) {
					userIdDBBookingRefe = "TRUE";
				}
				map.put("USERIDBOOKINGREFER", userIdDBBookingRefe);
				map.put("coCd", coCd);
				// end decentralization.
				if (!checkCancelAmendResultC.equals("N")) {
					map.put("showCancel", "N");
				} else {
					map.put("showCancel", "Y");
				}
				if (!checkCancelAmendResultA.equals("N") || StringUtils.equals("disabled", disabled)) {
					map.put("showAmend", "N");
				} else {
					map.put("showAmend", "Y");
				}
			}
			// END added by Maksym JCMS Smart CR 6.10

			if (mode.equals("doshowupd")) {
				bkRefNbr = CommonUtility.deNull(criteria.getPredicates().get("txtBR"));
				varno = CommonUtility.deNull(criteria.getPredicates().get("varno"));
				nocont = CommonUtility.deNull(criteria.getPredicates().get("nocont"));
				vslId = CommonUtility.deNull(criteria.getPredicates().get("vslID"));
				selCargoType = CommonUtility.deNull(criteria.getPredicates().get("selCargotype"));
				// BEGIN added by Maksym JCMS Smart CR 6.10
				cargoCategory = CommonUtility.deNull(criteria.getPredicates().get("cargoCategory"));
				// END added by Maksym JCMS Smart CR 6.10
				txtCrno = CommonUtility.deNull(criteria.getPredicates().get("txtCrno"));
				shipperContact = CommonUtility.deNull(criteria.getPredicates().get("txtShpContact"));
				shipperAddress = CommonUtility.deNull(criteria.getPredicates().get("txtShpAdr"));
				shipperName = CommonUtility.deNull(criteria.getPredicates().get("txtShpNm"));

				// Added on 25 June 2014 to trim space
				if (shipperName != null && !shipperName.equals("")) {
					shipperName = shipperName.trim();
				}

				shipperCoyCode = CommonUtility.deNull(criteria.getPredicates().get("lstShipper"));

				// To get shipper name
				if (shipperCoyCode != null && !shipperCoyCode.equals("")) {
					if (!shipperCoyCode.equalsIgnoreCase("OTHERS")) {
						try {

							CompanyValueObject vo = bookingReferenceService.getCompanyInfo(shipperCoyCode);
							shipperName = vo.getCompanyName();
							if (shipperName == null || shipperName.equals("")) {
								errorMessage = ConstantUtil.ErrorMsg_Company_info_not_found;
								throw new BusinessException(errorMessage);
							}
						} catch (Exception e) {
							errorMessage = ConstantUtil.ErrorMsg_finding_company_error;
							throw new BusinessException(errorMessage);
						}
					} else {
						// do nothing. get the co name from parameter passed in
					}

				}

				List<BookRefvoyageOutwardValueObject> vector3 = bookingReferenceService.getVoyageName(userCoyCode);
				map.put("bookingDet", vector3);
				// Added by thanhnv2::Start
				if ("JP".equals(userCoyCode)) {

					List<AccessCompanyValueObject> autPartyList = new ArrayList<AccessCompanyValueObject>();
					autPartyList = bookingReferenceService.getAutPartyListOfVessel(varno);
					ArrayList<String> comCd = new ArrayList<String>();
					ArrayList<String> coNm = new ArrayList<String>();
					for (int i = 0; i < autPartyList.size(); i++) {
						comCd.add(((AccessCompanyValueObject) autPartyList.get(i)).getCompanyCode());
						coNm.add(((AccessCompanyValueObject) autPartyList.get(i)).getCompanyName());
					}
					map.put("companyCode", comCd);
					map.put("companyName", coNm);
				}
				// Added by thanhnv2::End

				// BEGIN added by Maksym JCMS Smart CR 6.10
				List<BookingReferenceValueObject> brvoList = new ArrayList<BookingReferenceValueObject>();
				if ("JP".equals(userCoyCode)) {
					brvoList = bookingReferenceService.getBRVOList("AssignCargoCategory");
				} else {
					brvoList = bookingReferenceService.getBRVOList("other");
				}
				String[] currentApplicableCargoCategoryList = new String[] {};
				for (int i = 0; i < brvoList.size(); i++) {
					if (brvoList.get(i).getCargoType().equals(selCargoType)) {
						currentApplicableCargoCategoryList = brvoList.get(i).getCargoCategory().split(",");
					}
				}
				map.put("currentApplicableCargoCategoryList", currentApplicableCargoCategoryList);
				map.put("brvoList", brvoList);
				boolean showAllCargoCategory = bookingReferenceService.isShowAllCargoCategoryCode(userCoyCode);
				map.put("showAllCargoCategory", showAllCargoCategory);
				String notShowCargoCategoryCode = bookingReferenceService.getNotShowCargoCategoryCode();
				map.put("notShowCargoCategoryCode", notShowCargoCategoryCode);

				String vslType = bookingReferenceService.getVesselType(bkRefNbr);
				map.put("vslType", vslType);
				String vslCarCarrier = bookingReferenceService.getCarCarrierVesselCode();
				map.put("vslCarCarrier", vslCarCarrier);
				String cargoTypeNotShow = bookingReferenceService.getCargoTypeNotShow();
				map.put("cargoTypeNotShow", cargoTypeNotShow);
				// END added by Maksym JCMS Smart CR 6.10
			}
			if (mode.equals("doupd")) {
				StringTokenizer stringtokenizer = new StringTokenizer(shipname, "-");
				String s6 = stringtokenizer.nextToken();
				String outVoy = stringtokenizer.nextToken();
				StringTokenizer stringtokenizer1 = new StringTokenizer(selOutwardvoyage, "*");
				String vvCode = stringtokenizer1.nextToken();
				vslId = stringtokenizer1.nextToken();
				bkRefNbr = txtBR;
				String cargoStatus = "";
				String nbrCntr = nocont;
				String conrCodeNotUsed = "SA001";
				selCargoType = CommonUtility.deNull(criteria.getPredicates().get("selCargotype"));
				// BEGIN added by Maksym JCMS Smart CR 6.10
				cargoCategory = CommonUtility.deNull(criteria.getPredicates().get("cargoCategory"));
				// END added by Maksym JCMS Smart CR 6.10
				// START CR FTZ - NS JUNE 2024
				String conName = CommonUtility.deNull(criteria.getPredicates().get("conName"));
				String consigneeAddr = CommonUtility.deNull(criteria.getPredicates().get("consigneeAddr"));
				String notifyParty = CommonUtility.deNull(criteria.getPredicates().get("notifyParty"));
				String notifyPartyAddr = CommonUtility.deNull(criteria.getPredicates().get("notifyPartyAddr"));
				String placeofDelivery = CommonUtility.deNull(criteria.getPredicates().get("placeofDelivery"));
				String placeofReceipt = CommonUtility.deNull(criteria.getPredicates().get("placeofReceipt"));
				String blNbr = CommonUtility.deNull(criteria.getPredicates().get("blNbr"));
				// END CR FTZ - NS JUNE 2024
				String shipperCrNo = txtCrno;
				shipperContact = txtShpContact;
				shipperAddress = txtShpAdr;
				shipperName = txtShpNm;

				shipperCoyCode = lstShipper;
				shipperCrNo = shipperCoyCode;

				// To get shipper name
				if (shipperCoyCode != null && !shipperCoyCode.equals("")) {
					if (!shipperCoyCode.equalsIgnoreCase("OTHERS")) {
						try {

							CompanyValueObject vo = bookingReferenceService.getCompanyInfo(shipperCoyCode);
							shipperName = vo.getCompanyName();
							if (shipperName == null || shipperName.equals("")) {
								errorMessage = ConstantUtil.ErrorMsg_Company_info_not_found;
								throw new BusinessException(errorMessage);
							}
						} catch (Exception e) {
							errorMessage = ConstantUtil.ErrorMsg_finding_company_error;
							throw new BusinessException(errorMessage);
						}
					} else {
						// do nothing. get the co name from parameter passed in
					}

				}

				if (shipperName == null || shipperName.equals("")) {
					errorMessage = ConstantUtil.ErrorMsg_Shipping_Company_Not_Found;
					throw new BusinessException(errorMessage);
				}
				String bk = txtBk;
				String vol = txtVolume;
				String pack = txtPack;
				String varPack = txtVarPack;
				String varVol = txtVarVol;
				String wtVar = txtWtVar;
				String portDisc = txtPortDischarge;
				String esnDecl = txtEsnDecl;
				String cntrType = contType;
				nbrCntr = noCont;

				if (noCont == null || noCont.equals("")) {
					nbrCntr = "0";
				}
				if (cntrType == null) {
					cntrType = "";
				}
				if (!cntrType.equals("E") && !cntrType.equals("F")) {
					cntrType = "";
				}
				String cntrSize = contSize;
				if (cntrSize == null) {
					cntrSize = "";
				}

				List<BookRefvoyageOutwardValueObject> vector4 = bookingReferenceService.getVoyageName(userCoyCode);
				map.put("bookingDet", vector4);

				// HaiTTH1 added on 7/4/2014
				boolean disabledAmendbool = Boolean.valueOf(disabledAmend);
				if (disabledAmendbool) {
					String bkCreateCd = userCoyCode;
					if ("JP".equals(userCoyCode)) {
						bkCreateCd = (String) autParty;
					}
					updateBkRetStatus = bookingReferenceService.updateBKForDPE(bkRefNbr, cargoStatus, vvCode, nbrCntr,
							cntrType, cntrSize,
							// BEGIN amended by Maksym JCMS Smart CR 6.10
							vslId, outVoy, conrCodeNotUsed, selCargoType, cargoCategory, shipperCrNo, shipperContact,
							shipperAddress, shipperName,
							// END amended by Maksym JCMS Smart CR 6.10
							bk, vol, pack, varPack, varVol, wtVar, portDisc, esnDecl, bkCreateCd, userId, true, conName
							,consigneeAddr ,notifyParty ,notifyPartyAddr ,placeofDelivery ,placeofReceipt, blNbr);
				} else {
					String checkPortCode = bookingReferenceService.chkPortCode(portDisc);
					if (checkPortCode.equals("N")) {
						map.put("errorDisplay", "Enter a Valid Port Code");
					} else {
						String checkEsnDeclarantCrNo = bookingReferenceService.chkCrNo(esnDecl);
						if (checkEsnDeclarantCrNo.equals("N")) {
							map.put("errorDisplay", "Enter a Valid ESN Declarant Code");
						} else {
							String checkQty = bookingReferenceService.chkQuantity(bk, vol, pack, varPack, varVol, wtVar,
									bkRefNbr);
							if (!checkQty.equals("Y")) {
								map.put("errorDisplay", checkQty);
							} else {
								String bkCreateCd = userCoyCode;
								if ("JP".equals(userCoyCode)) {
									bkCreateCd = (String) autParty;
								}
								updateBkRetStatus = bookingReferenceService.updateBKForDPE(bkRefNbr, cargoStatus,
										vvCode, nbrCntr, cntrType, cntrSize,
										// BEGIN amended by Maksym JCMS Smart CR
										// 6.10
										vslId, outVoy, conrCodeNotUsed, selCargoType, cargoCategory, shipperCrNo,
										shipperContact, shipperAddress, shipperName,
										// END amended by Maksym JCMS Smart CR
										// 6.10
										bk, vol, pack, varPack, varVol, wtVar, portDisc, esnDecl, bkCreateCd, userId,
										false, conName ,consigneeAddr ,notifyParty ,notifyPartyAddr ,placeofDelivery 
										,placeofReceipt, blNbr);
							}
						}
					}
				}
			}
			String varnoString = varno;

			// 30/06/2011 PCYAP To check tonnage for manifest/booking/ship store
			int maxCargoTon = bookingReferenceService.retrieveMaxCargoTon(varnoString);

			map.put("maxCargoTon", new Integer(maxCargoTon));
			topsModel.put(brvo);
			map.put("BookingReferenceValueObject", topsModel);
			if (mode.equals("doshowupd")) {
				map.put("screen", "BRUpd");
			} else {

				if (updateBkRetStatus.equals("Y")) {
					List<BookingReferenceValueObject> vector2 = bookingReferenceService.fetchBKDetails(bkRefNbr);
					BookingReferenceValueObject brvo2 = new BookingReferenceValueObject();
					brvo2 = (BookingReferenceValueObject) vector2.get(0);
					String checkCancelAmendC = bookingReferenceService.chkCancelAmend(
							CommonUtility.deNull(criteria.getPredicates().get("txtBR")), userCoyCode, "C");
					String checkCancelAmendA = bookingReferenceService.chkCancelAmend(
							CommonUtility.deNull(criteria.getPredicates().get("txtBR")), userCoyCode, "A");
					if (!checkCancelAmendC.equals("N")) {
						map.put("showCancel", "N");
					} else {
						map.put("showCancel", "Y");
					}
					if (!checkCancelAmendA.equals("N") || StringUtils.equals("disabled", disabled)) {
						map.put("showAmend", "N");
					} else {
						map.put("showAmend", "Y");
					}
					map.put("bookingReferenceValueObject", brvo2);
					map.put("screen", "BRDis");
				} else {
					map.put("screen", "BRUpdate");
				}
			}

		} catch (BusinessException e) {
			log.info("Exception BRUpdate : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception BRUpdate : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.clear();
				map.put("errorMessage", errorMessage);
				result.setErrors(map);
				result.setSuccess(false);

			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: BRUpdate result: " + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());

	}

	// delegate.helper.gbms.cargo.bookingReference --> BRRetrieveHandler
	@PostMapping(value = "/BRRetrieve")
	public ResponseEntity<?> BRRetrieve(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		TopsModel topsModel = new TopsModel();
		errorMessage = null;
		try {
			log.info("BRRetrieve : START " + criteria.toString());
			String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String arrival = "";
			String departure = "";
			String col = "";
			String etb = "";

			topsModel = new TopsModel();

			BookingReferenceValueObject bookingreferencevalueobject = new BookingReferenceValueObject();
			String s6 = companyCode;
			List<BookRefvoyageOutwardValueObject> vector1 = bookingReferenceService.getVoyageName(s6);
			map.put("voyageOutwardVect", vector1);
			bookingreferencevalueobject.setVoyageoutward(vector1);
			topsModel.put(bookingreferencevalueobject);
			map.put("bookingReferenceList", topsModel);
			log.info("mode" + CommonUtility.deNull(criteria.getPredicates().get("mode")));
			if (criteria.getPredicates().get("mode") != null
					&& (criteria.getPredicates().get("mode").equals("selectlist")
							|| criteria.getPredicates().get("mode").equals("showlist")
							|| criteria.getPredicates().get("mode").equals("showSearchlist"))) {
				String vvCd = "";
				String isFetch = "FALSE";
				boolean isFetchmode = false;
				if (criteria.getPredicates().get("fetchmode") != null) {
					String fetchmode = CommonUtility.deNull(criteria.getPredicates().get("fetchmode"));
					if (fetchmode.equalsIgnoreCase("TRUE")) {
						isFetchmode = true;
						isFetch = "TRUE";

					}
				}
				map.put("isFetchmode", isFetch);
				List<BookingReferenceValueObject> vector2 = new ArrayList<BookingReferenceValueObject>();
				// Added by Irene Tan on 03 March 2005 : To include search Booking Reference
				StringTokenizer stringtokenizer = null;

				// haiTTH1 added on 12/2/2014
				stringtokenizer = new StringTokenizer(
						CommonUtility.deNull(criteria.getPredicates().get("selOutwardvoyage")), "*");
				if (!isFetchmode && stringtokenizer.hasMoreTokens()) {
					vvCd = stringtokenizer.nextToken();
				}
				List<VesselVoyValueObject> vslVct = new ArrayList<VesselVoyValueObject>();
				// DPE ADD
				String fetchVesselName = null;
				String fetchVoyageNbr = null;

				if (StringUtils.isNotBlank(criteria.getPredicates().get("vesselName"))) {
					fetchVesselName = CommonUtility.deNull(criteria.getPredicates().get("vesselName")).toUpperCase();
					fetchVoyageNbr = CommonUtility.deNull(criteria.getPredicates().get("voyageNumber")).toUpperCase();
					map.put("fetchVesselName", fetchVesselName); // Added by thanhnv2
					map.put("fetchVoyageNbr", fetchVoyageNbr); // Added by thanhnv2

				}
				if (isFetchmode || StringUtils.isBlank(vvCd)) {
					vslVct = bookingReferenceService.getVslDetailsForDPE(fetchVesselName, fetchVoyageNbr, s6);
				} else {
					vslVct = bookingReferenceService.getVslDetails(vvCd, s6);
				}

				if (vslVct.size() > 0) {
					VesselVoyValueObject vslObj = new VesselVoyValueObject();
					vslObj = (VesselVoyValueObject) vslVct.get(0);
					arrival = vslObj.getArrival();
					departure = vslObj.getDepartural();
					col = vslObj.getCol_dttm();
					etb = vslObj.getEtb_dttm();
				} else {
					log.info("Vietnd02=================Invalid vessel voyage values.");
					errorMessage = "Invalid vessel voyage values.";
					if (errorMessage != null) {
						map.clear();
						map.put("errorMessage", errorMessage);
						result = new Result();
						result.setError(errorMessage);
						result.setErrors(map);
						result.setSuccess(false);

					} else {
						result.setData(map);
						result.setSuccess(true);
						log.info("END: BRRetrieve result: " + result.toString());
					}

					return ResponseEntityUtil.success(result.toString());
				}
				String s8 = "";
				if (criteria.getPredicates().get("mode").equals("showSearchList")) {
					log.info("tttttttttttttt: "
							+ CommonUtility.deNull(criteria.getPredicates().get("selOutwardvoyage")));
					stringtokenizer = new StringTokenizer(
							(String) CommonUtility.deNull(criteria.getPredicates().get("selOutwardvoyage")), "*");
				} else
					stringtokenizer = new StringTokenizer(
							CommonUtility.deNull(criteria.getPredicates().get("selOutwardvoyage")), "*");
				// StringTokenizer stringtokenizer = new
				// StringTokenizer(httpservletrequest.getParameter("selOutwardvoyage"), "*");
				// End Added by Irene Tan on 03 March 2005 : To include search Booking Reference
				if (stringtokenizer.hasMoreTokens()) {
					s8 = stringtokenizer.nextToken();
				}
				if (StringUtils.isBlank(s8)) {
					VesselVoyValueObject vslObj = new VesselVoyValueObject();
					vslObj = (VesselVoyValueObject) vslVct.get(0);
					s8 = vslObj.getVarNbr();
				}
				vector2 = bookingReferenceService.getBKDetailsList(s8, s6, criteria); // changed by vietnd02 - 15/10 -
																						// s6 = coCode

				int total = bookingReferenceService.getBKDetailsListCount(s8, s6, criteria);

				map.put("total", total);
				map.put("bkDetailsVect", vector2);
				List<String> indicationStatus = bookingReferenceService.indicationStatus(s8);
				map.put("indicationStatus", indicationStatus);
			}

			// haiTTH1 added on 12/2/2014
			map.put("arrival", arrival);
			map.put("departure", departure);
			map.put("col", col);
			map.put("etb", etb);
			map.put("userType", s6);

		} catch (BusinessException e) {
			log.info("Exception BRRetrieve : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception BRRetrieve : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result = new Result();
				map.clear();
				map.put("errorMessage", errorMessage);
				result.setError(errorMessage);
				result.setErrors(map);
				result.setSuccess(false);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: BRRetrieve result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/portList")
	public ResponseEntity<?> portList(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		List<?> portlist = new ArrayList<Object>();
		TableResult tableresult = new TableResult();
		Criteria criteria = CommonUtil.getCriteria(request);
		errorMessage = null;
		try {

			log.info("** PortList Start criteria :" + criteria.toString());

			tableresult = bookingReferenceService.getPortCode(criteria);
			portlist = (List<?>) tableresult.getData().getListData().getTopsModel().get(0);

			map.put("total", tableresult.getData().getTotal());
			map.put("portlist", portlist);

		} catch (BusinessException be) {
			log.info("Exception portList : ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception portList : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: portList result:" + result.toString());
		}

		return ResponseEntityUtil.success(result.toString());
	}

	// Region bkref uplaod
	@PostMapping(value =  "/bkrefUpload")
	public ResponseEntity<?> bkrefUpload(@RequestParam("file") MultipartFile uploadingFile,
			HttpServletRequest request) {
		Result result = new Result();
		errorMessage = null;
		String assignedFileName = null;
		BookingReferenceFileUploadDetails bookingReferenceFileUploadDetails = new BookingReferenceFileUploadDetails();
		try {
			log.info("START bkrefUpload ");
			Criteria criteria = CommonUtil.getCriteria(request);
			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));

			log.info("bkrefUpload Request Params:" + criteria.toString());
			Summary summary = new Summary();
			if (uploadingFile.getSize() > 0) {
				log.info("bkrefUpload upload file size:" + uploadingFile.getSize() + ", File :"
						+ uploadingFile.getOriginalFilename());
			}

			if (uploadingFile.getSize() > 0) {
				log.info("Excel Process: Create a random gen UUID for " + uploadingFile.getOriginalFilename());
				String extension = FilenameUtils.getExtension(uploadingFile.getOriginalFilename());
				if (extension.equals("xls") || extension.equals("xlsx")) {
					assignedFileName = bookingReferenceService.fileUpload(uploadingFile, vvCd);
					log.info("fileUpload status :" + assignedFileName);
					if (assignedFileName != null && assignedFileName != "") {
						String lastTimestamp = bookingReferenceService.getTimeStamp();
						bookingReferenceFileUploadDetails.setVv_cd((vvCd));
						bookingReferenceFileUploadDetails.setActual_file_name(uploadingFile.getOriginalFilename());
						bookingReferenceFileUploadDetails.setAssigned_file_name(assignedFileName);
						bookingReferenceFileUploadDetails.setLast_modified_user_id(userId);
						bookingReferenceFileUploadDetails.setLast_modified_dttm(lastTimestamp);

						summary = bookingReferenceService.processBkrefDetails(uploadingFile,
								bookingReferenceFileUploadDetails, vvCd, userId, companyCode);
						
						result.setSuccess(true);
						result.setData(summary);
						log.info("excelProcessUpload: result: " + result.toString());
						// 4)insert action trail
						boolean res = bookingReferenceService.insertActionTrail(criteria.getPredicates().get("vvCd"), 
								summary, lastTimestamp, userId);
						log.info("insertActionTrail:" + res);
					}
				} else {
					log.info("Exception bkrefUpload : File should be xls or xlsx format");
					errorMessage = "M0010";
					result.setSuccess(false);
					result.setError(errorMessage);
					errorMessage = "";
				}
			} else {
				log.info("bkrefUpload File missing ");
				result.setSuccess(false);
				result.setError("Upload file missing ");
			}
		} catch (Exception ex) {
			log.info("Exception bkrefUpload : ", ex);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
			result.setSuccess(false);
			result.setError(errorMessage);
		} finally {
			log.info("END bkrefUpload :" + result.toString());
		}
		log.info("excelProcessUpload:result" + result.toString());
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value = "/getBkList")
	public ResponseEntity<?> getBkList(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Result result = new Result();
		List<?> bkList = new ArrayList<Object>();
		Criteria criteria = CommonUtil.getCriteria(request);
		errorMessage = null;
		try {
			log.info("** getBkList Start criteria :" + criteria.toString());
			String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String vvcd = CommonUtility.deNull(criteria.getPredicates().get("vvcd"));
			bkList = bookingReferenceService.getBKDetailsList(vvcd, companyCode, criteria);
			map.put("bkList", bkList);
		} catch (BusinessException be) {
			log.info("Exception getBkList : ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception getBkList : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: getBkList result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value =  "/bkRefDocumentDetail")
	public ResponseEntity<?> bkRefDocumentDetail(HttpServletRequest request) {
		Result result = new Result();
		PageDetails pageDetails = null;
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START bkRefDocumentDetail criteria: " + criteria.toString());
			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			log.info(" bkRefDocumentDetail: Param:vvCd:" + vvCd);
			pageDetails = bookingReferenceService.getBkRefDocumentDetail(vvCd);
			if (pageDetails != null) {
				Boolean isSubmissionAllowed = bookingReferenceService.isBkSubmissionAllowed(criteria);
				pageDetails.setIsSubmissionAllowed(isSubmissionAllowed);
			}
			log.info(" bkRefDocumentDetail: result:" + result.toString());
		} catch (BusinessException be) {
			log.info("Exception bkRefDocumentDetail : ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception bkRefDocumentDetail : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(pageDetails);
				result.setSuccess(true);
			}
			log.info("END bkRefDocumentDetail");
		}
		return ResponseEntityUtil.success(result.toString());
	}

	@PostMapping(value =  "/bkRefDownload")
	public void bkRefDownload(HttpServletRequest request, HttpServletResponse response) {
		try {
			log.info("START bkRefDownload");
			Criteria criteria = CommonUtil.getCriteria(request);

			log.info(" bkRefDownload criteria :" + criteria.toString());
			String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("userAcct"));
			XSSFWorkbook wb = null;
			wb = bookingReferenceService.bkDetailExcelDownload(vvCd, coCd, criteria);

			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=BookingReference.xlsx");
			ServletOutputStream out = response.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
			wb.close();
		} catch (BusinessException be) {
			log.info("Exception bkRefDownload : ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception bkRefDownload : ", e);
		} finally {
			log.info("END bkRefDownload");
		}
	}
	
	@PostMapping(value = "/fileDownload")
	public ResponseEntity<?> fileDownload(HttpServletRequest request) {
		Resource resource = null;
		try {
			log.info("START fileDownload");
			Criteria criteria = CommonUtil.getCriteria(request);
			String type = CommonUtility.deNull(criteria.getPredicates().get("typeCd"));
			String refId = CommonUtility.deNull(criteria.getPredicates().get("refId"));
			log.info("fileDownload : Param: " + criteria.toString());

			resource = bookingReferenceService.fileDownload(refId, type);

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

	@PostMapping(value =  "/bkActionTrail")
	public ResponseEntity<?> bkActionTrail(HttpServletRequest request) {
		TableResult bkref_hatch_act_trl = null;
		try {
			log.info("START : bkActionTrail");
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("Params:" + criteria.toString());

			bkref_hatch_act_trl = bookingReferenceService.getBkActionTrail(criteria);

			log.info("bkActionTrail:" + bkref_hatch_act_trl.toString());
		} catch (BusinessException be) {
			log.info("Exception bkrefActionTrail : ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception bkrefActionTrail : ", e);
		} finally {
			log.info("END bkrefActionTrail");
		}
		return ResponseEntityUtil.success(bkref_hatch_act_trl.toString());
	}

	@PostMapping(value =  "/bkActionTrailDetail")
	public ResponseEntity<?> bkActionTrailDetail(HttpServletRequest request) {
		Result result = new Result();
		errorMessage = null;
		BkRefActionTrailDetails bkActionTrailDetail = null;
		try {
			log.info("START : bkActionTrailDetail");
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("bkActionTrailDetail Params:" + criteria.toString());
			String bk_act_trl_id = CommonUtility.deNull(criteria.getPredicates().get("bk_act_trl_id"));
			bkActionTrailDetail = bookingReferenceService.bkActionTrailDetail(bk_act_trl_id);
		} catch (BusinessException be) {
			log.info("Exception bkActionTrailDetail : ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception bkActionTrailDetail : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(bkActionTrailDetail);
				result.setSuccess(true);
			}
			log.info("END bkActionTrailDetail result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}
	
	@PostMapping(value = "/updateBLNbr")
	public ResponseEntity<?> updateBLNbr(HttpServletRequest request) {
		Result result = new Result();
		Criteria criteria = CommonUtil.getCriteria(request);
		errorMessage = null;
		try {
			log.info("** updateBLNbr Start criteria :" + criteria.toString());
			bookingReferenceService.updateBlNbr(criteria);
		} catch (BusinessException be) {
			log.info("Exception updateBLNbr : ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception updateBLNbr : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setSuccess(true);
			}
			log.info("END: updateBLNbr result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}
	
	@PostMapping(value = "/getVarcode")
	public ResponseEntity<?> getVarcode(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("** getVarcode Start criteria :" + criteria.toString());
			map.put("varno", bookingReferenceService.getVarcode(criteria));
		} catch (BusinessException be) {
			log.info("Exception getVarcode : ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
		} catch (Exception e) {
			log.info("Exception getVarcode : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END: getVarcode result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}
}
