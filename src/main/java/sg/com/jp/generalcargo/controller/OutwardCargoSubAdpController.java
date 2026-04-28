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

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.SubAdpValueObject;
import sg.com.jp.generalcargo.domain.TruckerValueObject;
import sg.com.jp.generalcargo.domain.UaEsnDetValueObject;
import sg.com.jp.generalcargo.service.OutwardCargoSubAdpService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;
import sg.com.jp.generalcargo.util.SubAdpConstant;

@CrossOrigin
@RestController
@RequestMapping(value = OutwardCargoSubAdpController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OutwardCargoSubAdpController {

	public static final String ENDPOINT = "gc/outwardcargo/subAdp";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(OutwardCargoSubAdpController.class);

	@Autowired
	private OutwardCargoSubAdpService subAdpService;

	// delegate.helper.gbms.cargo.subAdp-->SubAdpHandler

	@PostMapping(value = "/perform")
	public ResponseEntity<?> perform(HttpServletRequest request) throws BusinessException {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		Criteria criteria = CommonUtil.getCriteria(request);
		errorMessage = null;

		try {
			log.info("perform criteria:" + criteria.toString());

			String action = (String) criteria.getPredicates().get("ButtonAction");
			log.info("=================================== ButtonAction1: " + action);

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			if (action == null || action.equals("")) {

			} else {
				if (SubAdpConstant.Action.LIST_ACT.equals(action)) {
					List(request, action, map);
				} else if ("SubAdpNominateDisp".equalsIgnoreCase(action)) {// Display
																			// to
																			// Nominate
					String esnasnnbr = (String) criteria.getPredicates().get("esnNo");
					// HaiTTH1 added on 19/1/2014
					String trucker = (String) criteria.getPredicates().get("trucker");
					if (esnasnnbr == null)
						esnasnnbr = "";
					String esnTransType = subAdpService.getEsnTranType(esnasnnbr);
					log.info("=================================== esnNo - trantype: " + esnasnnbr + " - "
							+ esnTransType);
					// Retrie ESN detail
					UaEsnDetValueObject esnObj = new UaEsnDetValueObject();
					esnObj = subAdpService.getEsnDetail(esnasnnbr, esnTransType, coCode, userId);
					map.put("esnObj", esnObj);
					// HaiTTH1 added on 19/1/2014
					List<TruckerValueObject> truckerVector = new ArrayList<TruckerValueObject>();
					if (StringUtils.equalsIgnoreCase("TRUCKER", trucker)) {
//						String[] listValueTruckerIcNo = new String[0];
//						if (request.getParameterValues("truckerIc") != null) {
//							listValueTruckerIcNo = (String[]) request.getParameterValues("truckerIc");
//						}
//						String[] listValueTruckerName = new String[0];
//						if (request.getParameterValues("truckerNm") != null) {
//							listValueTruckerName = (String[]) request.getParameterValues("truckerNm");
//						}
//						String[] listValueTruckerCNo = new String[0];
//						if (request.getParameterValues("truckerContact") != null) {
//							listValueTruckerCNo = (String[]) request.getParameterValues("truckerContact");
//						}
//						String[] listValueNbrpkg = new String[0];
//						if (request.getParameterValues("truckerPkgs") != null) {
//							listValueNbrpkg = (String[]) request.getParameterValues("truckerPkgs");
//						}

						//Add by NS 17112022 : Change request array parameter value.
						int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
						List<String> listValueTruckerIcNo =  new ArrayList<String>(); 
						String truckerIcNo;
						List<String> listValueTruckerName =  new ArrayList<String>(); 
						String truckerNmNo;
						List<String> listValueTruckerCNo =  new ArrayList<String>(); 
						String truckerContactNo;
						List<String> listValueNbrpkg =  new ArrayList<String>(); 
						String truckerPkgsNo;
						
						if (sizeSelected != 0) {
							for (int i = 0; i < sizeSelected; i++) {
								truckerIcNo = (String) criteria.getPredicates().get("truckerIc" + i);
								listValueTruckerIcNo.add(truckerIcNo);
								truckerNmNo = (String) criteria.getPredicates().get("truckerNm" + i);
								listValueTruckerName.add(truckerNmNo);
								truckerContactNo = (String) criteria.getPredicates().get("truckerContact" + i);
								listValueTruckerCNo.add(truckerContactNo);
								truckerPkgsNo = (String) criteria.getPredicates().get("truckerPkgs" + i);
								listValueNbrpkg.add(truckerPkgsNo);
							}
						}
						
						String truckerDelete = criteria.getPredicates().get("truckerDelete");
						if (!"delete".equalsIgnoreCase(truckerDelete)) {
							int seq = Integer.parseInt(criteria.getPredicates().get("truckerRow"));
							if (listValueTruckerIcNo.size() >= seq) {
								String trucker_ciNo = listValueTruckerIcNo.get(seq - 1);
								log.info("trucker_ciNo: " + trucker_ciNo);
								TruckerValueObject truckerObj = new TruckerValueObject();
								boolean isValid = true;
//		    	                if (!StringUtils.isEmpty(trucker_ciNo)) {
//		    	                	isValid = subAdpService.checkValidTrucker(trucker_ciNo);
//		    	                }
								if (isValid) {
									truckerObj = subAdpService.getTruckerDetails(trucker_ciNo);
									listValueTruckerName.set(seq-1, truckerObj.getTruckerNm());
									listValueTruckerCNo.set(seq-1, truckerObj.getTruckerContact());
								} else {
									listValueTruckerName.set(seq-1, "");
									listValueTruckerCNo.set(seq-1, "");
								}

								for (int i = 0; i < listValueTruckerIcNo.size(); i++) {
									TruckerValueObject trk_obj = new TruckerValueObject();
									trk_obj.setTruckerIc(listValueTruckerIcNo.get(i));
									trk_obj.setTruckerNm(listValueTruckerName.get(i));
									trk_obj.setTruckerContact(listValueTruckerCNo.get(i));
									trk_obj.setTruckerPkgs(listValueNbrpkg.get(i));
									truckerVector.add(trk_obj);
								}
							}
						} else {
							for (int y = 0; y < listValueTruckerIcNo.size(); y++) {
								TruckerValueObject trk_obj = new TruckerValueObject();
								trk_obj.setTruckerIc(listValueTruckerIcNo.get(y));
								trk_obj.setTruckerNm(listValueTruckerName.get(y));
								trk_obj.setTruckerContact(listValueTruckerCNo.get(y));
								trk_obj.setTruckerPkgs(listValueNbrpkg.get(y));
								truckerVector.add(trk_obj);
							}
						}

						// HaiTTh1 added on 12/2/2014
						String vslnm = criteria.getPredicates().get("vslnm");
						String outvoy = criteria.getPredicates().get("outvoy");
						String esnno = criteria.getPredicates().get("esnNo");
						String actnbr = criteria.getPredicates().get("actnbr");
						String etb = criteria.getPredicates().get("etb");
						String atb = criteria.getPredicates().get("atb");
						String bkref = criteria.getPredicates().get("bkref");
						String markings = criteria.getPredicates().get("markings");
						String dcpkgs = criteria.getPredicates().get("totalPkgs");
						String balpkgs = criteria.getPredicates().get("balpkgs");
						String crgdesc = criteria.getPredicates().get("crgdesc");

						map.put("vslnm", vslnm);
						map.put("outvoy", outvoy);
						map.put("esnNo", esnno);
						map.put("actnbr", actnbr);
						map.put("etb", etb);
						map.put("atb", atb);
						map.put("bkref", bkref);
						map.put("markings", markings);
						map.put("totalPkgs", dcpkgs);
						map.put("balpkgs", balpkgs);
						map.put("crgdesc", crgdesc);

						map.put("truckerMode", trucker);
						// HaiTTh1 ended on 12/2/2014
					} else {
						// HaiTTh1 added on 12/2/2014
						truckerVector = subAdpService.getTruckerList(esnasnnbr);
					}

					map.put("truckerList", truckerVector);

				} else if (SubAdpConstant.Action.NOMINATE_ACT.equals(action)) {// Creat
																				// ADP
					log.info("=====INSIDE NOMINATE_ACT");
					String esnasnnbr = (String) criteria.getPredicates().get("esnNo");
					if (esnasnnbr == null)
						esnasnnbr = "";

					// haiTTH1 added on 19/1/2014
					String totPkg_s = (String) criteria.getPredicates().get("totalPkgs");
					String balpkgs = criteria.getPredicates().get("balpkgs");
					int totalNbrAdpPkg = 0;
//					String[] listValueTruckerIcNo = new String[0];
//					if (request.getParameterValues("truckerIc") != null) {
//						listValueTruckerIcNo = (String[]) request.getParameterValues("truckerIc");
//					}
//					String[] listValueTruckerName = new String[0];
//					if (request.getParameterValues("truckerNm") != null) {
//						listValueTruckerName = (String[]) request.getParameterValues("truckerNm");
//					}
//					String[] listValueTruckerCNo = new String[0];
//					if (request.getParameterValues("truckerContact") != null) {
//						listValueTruckerCNo = (String[]) request.getParameterValues("truckerContact");
//					}
//					String[] listValueNbrpkg = new String[0];
//					if (request.getParameterValues("truckerPkgs") != null) {
//						listValueNbrpkg = (String[]) request.getParameterValues("truckerPkgs");
//					}

					//Add by NS 17112022 : Change request array parameter value.
					int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
					List<String> listValueTruckerIcNo =  new ArrayList<String>(); 
					String truckerIcNo;
					List<String> listValueTruckerName =  new ArrayList<String>(); 
					String truckerNmNo;
					List<String> listValueTruckerCNo =  new ArrayList<String>(); 
					String truckerContactNo;
					List<String> listValueNbrpkg =  new ArrayList<String>(); 
					String truckerPkgsNo;
					
					if (sizeSelected != 0) {
						for (int i = 0; i < sizeSelected; i++) {
							truckerIcNo = (String) criteria.getPredicates().get("truckerIc" + i);
							listValueTruckerIcNo.add(truckerIcNo);
							truckerNmNo = (String) criteria.getPredicates().get("truckerNm" + i);
							listValueTruckerName.add(truckerNmNo);
							truckerContactNo = (String) criteria.getPredicates().get("truckerContact" + i);
							listValueTruckerCNo.add(truckerContactNo);
							truckerPkgsNo = (String) criteria.getPredicates().get("truckerPkgs" + i);
							listValueNbrpkg.add(truckerPkgsNo);
						}
					}

					List<TruckerValueObject> truckerList = new ArrayList<TruckerValueObject>();
		
					if (listValueTruckerIcNo.size() > 0) {
						for (int x = 0; x < listValueTruckerIcNo.size(); x++) {
							TruckerValueObject trk_obj = new TruckerValueObject();
//    	                	if (subAdprem.checkValidTrucker(listValueTruckerIcNo[x])) {
							String _trucker_cd = subAdpService.getTruckerCdByTruckerIcNo(listValueTruckerIcNo.get(x));
							trk_obj.setTruckerCd(_trucker_cd);
							trk_obj.setTruckerIc(listValueTruckerIcNo.get(x));
							trk_obj.setTruckerNm(listValueTruckerName.get(x));
							trk_obj.setTruckerContact(listValueTruckerCNo.get(x));
							trk_obj.setTruckerPkgs(listValueNbrpkg.get(x));
							truckerList.add(trk_obj);
							int nbrPkgs = Integer.parseInt(listValueNbrpkg.get(x));
							totalNbrAdpPkg = totalNbrAdpPkg + nbrPkgs;
//    	                	} else {
//    	                		errorMessage(request, "Trucker number " + listValueTruckerIcNo[x] + " is not valid.");
//    	    					return;
//    	                	}

						}
						log.info("totalNbrAdpPkg" + totalNbrAdpPkg + "balpkgs" + balpkgs + "totPkg_s" + totPkg_s);
						
						if(Integer.parseInt(totPkg_s) == Integer.parseInt(balpkgs)) {
							int totPkg_int = Integer.parseInt(totPkg_s);
							if (totalNbrAdpPkg != 0 && totalNbrAdpPkg != totPkg_int) {
								errorMessage = ConstantUtil.ErrorMsg_Invalid_Number_packages;
								map.put("error", errorMessage);
								result = new Result();
								result.setError(errorMessage);
								result.setSuccess(false);
								result.setData(map);
								return ResponseEntityUtil.success(result.toString());
							}
						} 
						 
//						Added 07/12/2022 : to enable user add or update trucker packages less than packages balance.
						else {
							if (totalNbrAdpPkg > Integer.parseInt(balpkgs)) {
								errorMessage = ConstantUtil.ErrorMsg_Balance_Package_Greater;
								map.put("error", errorMessage);
								result = new Result();
								result.setError(errorMessage);
								result.setSuccess(false);
								result.setData(map);
								return ResponseEntityUtil.success(result.toString());
							}
						}
//						for (int i=0; i < listValueTruckerIcNo.length; i++) {
//     					  String trucker_cd = subAdprem.getTruckerCdByTruckerIcNo(listValueTruckerIcNo[i]);
//     					  subAdprem.creatADP(esnasnnbr, "A", trucker_cd, listValueTruckerName[i], listValueTruckerIcNo[i], listValueTruckerCNo[i], userId, listValueNbrpkg[i]);
						subAdpService.creatMultiTruckers(esnasnnbr, truckerList, userId, "A", totPkg_s);
//						}
					} else {
						errorMessage = ConstantUtil.ErrorMsg_Trucker_Added;
						map.put("error", errorMessage);
						result = new Result();
						result.setError(errorMessage);
						result.setSuccess(false);
						result.setData(map);
						return ResponseEntityUtil.success(result.toString());
					}

					List(request, action, map);
					//
				} else if ("SubAdpDelete".equalsIgnoreCase(action)) {// Delete
																		// ADP
					log.info("=====INSIDE SubAdpDelete");
					int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
					// -- Delete Auth
					if (sizeSelected != 0) {
//						String[] subAdpNbrList = (String[]) request.getParameterValues("subAdpNbrList");
//						String[] checklist = (String[]) request.getParameterValues("checklist");
//
//						String[] statusList = (String[]) request.getParameterValues("statusList");
//						String[] truckCoCdList = (String[]) request.getParameterValues("truckCoCdList");
//						String[] truckNmList = (String[]) request.getParameterValues("truckNmList");
//						String[] truckIcList = (String[]) request.getParameterValues("truckIcList");
//						String[] truckContactList = (String[]) request.getParameterValues("truckContactList");
//						String[] truckNbrPkgList = (String[]) request.getParameterValues("truckNbrPkgList");

						List<String> subAdpNbr_Vector = new ArrayList<String>();

						List<String> status_Cd_Vector = new ArrayList<String>();
						List<String> trucker_CoCd_Vector = new ArrayList<String>();
						List<String> trucker_Nm_Vector = new ArrayList<String>();
						List<String> trucker_Ic_Vector =new ArrayList<String>();
						List<String> trucker_Contact_Nbr_Vector = new ArrayList<String>();
						List<String> trucker_nbr_pkg_Vector = new ArrayList<String>();

						//Add by NS 17112022 : Change request array parameter value.
						List<String> subAdpNbrList =  new ArrayList<String>(); 
						String subAdpNbrNo;
						List<String> checklist =  new ArrayList<String>(); 
						String checkListNo;
						List<String> statusList =  new ArrayList<String>(); 
						String statusListNo;
						List<String> truckCoCdList =  new ArrayList<String>(); 
						String truckCoCdListNo;
						List<String> truckNmList =  new ArrayList<String>(); 
						String truckNmListNo;
						
						List<String> truckIcList =  new ArrayList<String>(); 
						String truckIcListNo;
						List<String> truckContactList =  new ArrayList<String>(); 
						String truckContactListNo;
						List<String> truckNbrPkgList =  new ArrayList<String>(); 
						String truckNbrPkgListNo;
						
						// -- Selected to delete information checking
						int numOfdel = 0;

						if (sizeSelected > 0) {
							for (int i = 0; i < sizeSelected; i++) {
								subAdpNbrNo = (String) criteria.getPredicates().get("subAdpNbrList" + i);
								subAdpNbrList.add(subAdpNbrNo);
								checkListNo = (String) criteria.getPredicates().get("checklist" + i);
								checklist.add(checkListNo);
								statusListNo = (String) criteria.getPredicates().get("statusList" + i);
								statusList.add(statusListNo);
								truckCoCdListNo = (String) criteria.getPredicates().get("truckCoCdList" + i);
								truckCoCdList.add(truckCoCdListNo);
								truckNmListNo = (String) criteria.getPredicates().get("truckNmList" + i);
								truckNmList.add(truckNmListNo);
								
								truckIcListNo = (String) criteria.getPredicates().get("truckIcList" + i);
								truckIcList.add(truckIcListNo);
								truckContactListNo = (String) criteria.getPredicates().get("truckContactList" + i);
								truckContactList.add(truckContactListNo);
								truckNbrPkgListNo = (String) criteria.getPredicates().get("truckNbrPkgList" + i);
								truckNbrPkgList.add(truckNbrPkgListNo);
							}
						} 
						
						if (subAdpNbrList.size() > 0 && checklist.size() > 0) {
							for (int i = 0; i < subAdpNbrList.size(); i++) {
								for (int j = 0; j < checklist.size(); j++) {
									if (subAdpNbrList.get(i).equalsIgnoreCase(checklist.get(j))) {
										log.info("subAdpNbrList[" + i + "]:" + subAdpNbrList.get(i));
										log.info("checklist[" + j + "]:" + checklist.get(j));

										subAdpNbr_Vector.add(subAdpNbrList.get(i));

										if ("Active".equalsIgnoreCase(statusList.get(i))) {
											status_Cd_Vector.add("A");
										} else if ("Deleted".equalsIgnoreCase(statusList.get(i))) {
											status_Cd_Vector.add("X");
										}

										trucker_CoCd_Vector.add(truckCoCdList.get(i));
										trucker_Nm_Vector.add(truckNmList.get(i));
										trucker_Ic_Vector.add(truckIcList.get(i));
										trucker_Contact_Nbr_Vector.add(truckContactList.get(i));

										trucker_nbr_pkg_Vector.add(truckNbrPkgList.get(i));
										numOfdel++;
									}
								}
							}
							// -- End of Selected to delete information checking
							log.info(">> STARTING DELETE >>");
							log.info("Number of sub ADP to delete = " + numOfdel);
							subAdpService.delADPForDPE(subAdpNbr_Vector, userId, status_Cd_Vector, trucker_CoCd_Vector,
									trucker_Nm_Vector, trucker_Ic_Vector, trucker_Contact_Nbr_Vector,
									trucker_nbr_pkg_Vector);
						}
					}

					List(request, action, map);
					map.put("screen", SubAdpConstant.Screen.LIST_SCR);

				} else if (SubAdpConstant.Action.TOP_ACT.equals(action)) {
					map.put("screen", SubAdpConstant.Screen.TOP_SCR);
				}
			}
			map.put("maxiTrucker", TruckerValueObject.MAX_ADP_TRUCKER);
		} catch (BusinessException be) {
			log.info("Exception perform: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception perform : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setError(errorMessage);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: perform result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	public void List(HttpServletRequest request, String action, Map<String, Object> map) {
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("perform criteria:" + criteria.toString());

			String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			int total = 0;
			log.info("=================================== action = " + action);
			String esnasnnbr = (String) CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
			log.info("=================================== esnNo: " + esnasnnbr);
			boolean checkEsn = subAdpService.checkEsnExist(esnasnnbr);
			if (checkEsn) {// ESN No exist
				log.info("============== ESN No exist");
				String esnTransType = subAdpService.getEsnTranType(esnasnnbr);
				// Retrie ESN detail
				UaEsnDetValueObject esnObj = new UaEsnDetValueObject();
				esnObj = subAdpService.getEsnDetail(esnasnnbr, esnTransType, coCode, userId);
				map.put("esnObj", esnObj);
				List<SubAdpValueObject> subAdpVector = new ArrayList<SubAdpValueObject>();
				List<String> subAdpNbr_Vector = new ArrayList<String>();
				List<String> status_Cd_Vector = new ArrayList<String>();
				List<String> trucker_CoCd_Vector = new ArrayList<String>();
				List<String> trucker_Nm_Vector = new ArrayList<String>();
				List<String> trucker_Ic_Vector = new ArrayList<String>();
				List<String> trucker_Contact_Nbr_Vector = new ArrayList<String>();
				List<String> trucker_nbr_pkg_Vector = new ArrayList<String>();
				if (esnasnnbr != null && !esnasnnbr.equals("")) {
					subAdpVector = subAdpService.getSubADP(esnasnnbr, criteria);
					total = subAdpService.getSubADPTotal(esnasnnbr);
					// To get the Data for the first page
					int i = 0;
					for (i = 0; i < subAdpVector.size(); i++) {
						SubAdpValueObject subAdpVO = new SubAdpValueObject();
						subAdpVO = (SubAdpValueObject) subAdpVector.get(i);
						log.info("============== OUT PageIndex4: " + subAdpVO.getSubAdp_nbr());
						subAdpNbr_Vector.add(subAdpVO.getSubAdp_nbr());
						if ("A".equalsIgnoreCase(subAdpVO.getStatus())) {
							status_Cd_Vector.add("A");
						}else if ("X".equalsIgnoreCase(subAdpVO.getStatus())) {
							status_Cd_Vector.add("X");
						}
						trucker_CoCd_Vector.add(subAdpVO.getCo_Cd());
						trucker_Nm_Vector.add(subAdpVO.getTruck_nm());
						trucker_Ic_Vector.add(subAdpVO.getTruck_ic());
						trucker_Contact_Nbr_Vector.add(subAdpVO.getContact_no());
						trucker_nbr_pkg_Vector.add(subAdpVO.getTruck_pkgs());
					}
				}
				map.put("total", total);
				map.put("SubAdpList", subAdpVector);
				map.put("subAdpNbr_Vector", subAdpNbr_Vector);
				map.put("status_Cd_Vector", status_Cd_Vector);
				map.put("trucker_CoCd_Vector", trucker_CoCd_Vector);
				map.put("trucker_Nm_Vector", trucker_Nm_Vector);
				map.put("trucker_Ic_Vector", trucker_Ic_Vector);
				map.put("trucker_Contact_Nbr_Vector", trucker_Contact_Nbr_Vector);
				map.put("trucker_nbr_pkg_Vector", trucker_nbr_pkg_Vector);
			}
			map.put("screen", SubAdpConstant.Screen.LIST_SCR);

		} catch (BusinessException be) {
			log.info("Exception List: ", be);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if (errorMessage == null) {
				errorMessage = be.getMessage();
			}
		} catch (Exception e) {
			log.info("Exception List : ", e);
			errorMessage = ConstantUtil.OUTWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		}
	}

}
