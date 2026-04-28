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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import sg.com.jp.generalcargo.domain.ContainerDetailObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.SchemeAccountObject;
import sg.com.jp.generalcargo.domain.StuffingDetailObject;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.OutwardCargoStuffOutboundCtrService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = OutwardCargoStuffOutboundCtrController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OutwardCargoStuffOutboundCtrController {

	public static final String ENDPOINT = "gc/containerised";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(OutwardCargoStuffOutboundCtrController.class);

	@Autowired
	private OutwardCargoStuffOutboundCtrService outwardCargoService;


	// jp.src.delegate.helper.gbms.containerised.stuffing-->StuffingHandler
	// StartRegion StuffingHandler
	// method: perform()
	/**
	 * This method is used to interact with StuffingEJB and perform database
	 * operation for fetching vessel voyage details,container and its stuffing
	 * details.
	 * 
	 * @param httpservletrequest HttpServlet Request object.
	 * @throws RequestException
	 */
	@ApiOperation(value = "stuffing", response = String.class)
	@RequestMapping(value = "/outwardCargo/stuffing", method = RequestMethod.POST)
	public ResponseEntity<?> stuffing(HttpServletRequest request) throws BusinessException {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		List<ContainerDetailObject> containerdetailslist = new ArrayList<ContainerDetailObject>();
		errorMessage = null;
		try {
			log.info("START: stuffing criteria:" + criteria.toString());	
			String vvcode = "";
			String containernumber = "";
			String companycode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			try {
				vvcode = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
     	        // s1 = criteria.getPredicates().get("vslnew");

			log.info("S in Stuffing Handler is " + vvcode + "\nSl in Stuffing Handler is ");
			} catch (Exception exception) {
				log.info("Exception stuffing : ", exception);
				errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
				throw new BusinessException(errorMessage);
			}
			try {
				containernumber = CommonUtility.deNull(criteria.getPredicates().get("contno"));
			} catch (Exception e) {
				log.info("Exception stuffing : ", e);
				errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
				throw new BusinessException(errorMessage);
			}

			log.info("containernumber  is >> " + containernumber);
			List<VesselVoyValueObject> vesselvoy = outwardCargoService.getVesselVoyage(companycode);
			List<String> containerlist = outwardCargoService.getContainerNos(vvcode);
			log.info(" containerlist.size().." + containerlist.size());
			TopsModel topsmodel = new TopsModel();
			for (int lp = 0; (vesselvoy != null && lp < vesselvoy.size()); lp++) {
				VesselVoyValueObject vesselvoyvalueobject = null;
				vesselvoyvalueobject = (VesselVoyValueObject) vesselvoy.get(lp);
				topsmodel.put(vesselvoyvalueobject);
			}
			
			int total = 0;
			if (vvcode != null && !vvcode.equals("") && containernumber != null && !containernumber.equals("")) {
				if (criteria.getPredicates().get("actioncmd") == null
						|| criteria.getPredicates().get("actioncmd").equalsIgnoreCase("new")) {
					TableResult tableResult = outwardCargoService.getContainerDetails(vvcode, containernumber, criteria);
					 containerdetailslist = new ArrayList<ContainerDetailObject>();
						int size = tableResult.getData().getListData().getTopsModel().size();
						ContainerDetailObject containerList = new ContainerDetailObject();
						for (int i = 0; i < size; i++) {
							containerList = new ContainerDetailObject();
						  containerList = (ContainerDetailObject) tableResult.getData().getListData().getTopsModel().get(i);
						  containerdetailslist.add(containerList);
						}
					 total = tableResult.getData().getTotal();
				}
			}


			map.put("selVoyno", vvcode);
			map.put("selcontno", containernumber);
			map.put("containerlist", containerlist);
			map.put("containerdetailpage", containerdetailslist);

			map.put("total",  total);
			map.put("usrtyp", companycode);

			map.put("topsmodel", topsmodel);
			map.put("screen", "StuffingVesselCallList");

			
		} catch (BusinessException be) {
			log.info("Exception stuffing: " + be.getMessage());
			errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception stuffing : ", e);
			errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result = new Result();
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END stuffing result:" + result.toString());
		}
		return ResponseEntityUtil.success(result);
	}
	// EndRegion StuffingHandler

	@ApiOperation(value = "stuffingAddView", response = String.class)
	@RequestMapping(value = "/outwardCargo/stuffingAddView", method = RequestMethod.POST)
	public ResponseEntity<?> stuffingAddView(HttpServletRequest request) throws BusinessException {
		return this.stuffingAdd(request);
	}
	
	// jp.src.delegate.helper.gbms.containerised.stuffing-->StuffingAddHandler
	// StartRegion StuffingAddHandler
	// method: perform()
	@ApiOperation(value = "stuffingAdd", response = String.class)
	@RequestMapping(value = "/outwardCargo/stuffingAdd", method = RequestMethod.POST)
	public ResponseEntity<?> stuffingAdd(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;

		try {
			log.info("START: stuffingAdd criteria:" + criteria.toString());

			String vvcode = null;
			String vslnm = null;
			String outvoyno = null;
			String actioncmd = null;

			//String edoesnnos[] = null;
			//String pkgs[] = null;

			List<String> edonos = null;
			List<String> esnnos = null;
			List<String> edopkgs = null;
			List<String> esnpkgs = null;
			// ArrayList chkESNStuffIndAL= null; //added by vinayak on 09/02/2004

			String vsl = null;
			StringTokenizer cntr = null;

			StringBuffer msg = new StringBuffer();

			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			//String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			//boolean retrieveInd = false;

			try {
				vvcode = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
			} catch (Exception e) {
				log.info("Exception stuffingAdd : ", e);
				errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
				throw new BusinessException(errorMessage);
			}
			try {
				vsl = new String(CommonUtility.deNull(criteria.getPredicates().get("vslvoy")));

				if (vsl.indexOf("-") > 0) {
					vslnm = vsl.substring(0, vsl.lastIndexOf("-")).trim();
					outvoyno = vsl.substring(vsl.indexOf("-") + 1).trim();
				}
//			          vslnm = vsl.nextToken();
//			          outvoyno = vsl.nextToken();
			} catch (Exception e) {
				log.info("Exception stuffingAdd : ", e);
				errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
				throw new BusinessException(errorMessage);
			}
			actioncmd = CommonUtility.deNull(criteria.getPredicates().get("actioncmd"));

			map.put("vslnm", vslnm);
			map.put("vvcode", vvcode);
			map.put("outvoyno", outvoyno);
			map.put("contno", criteria.getPredicates().get("contno"));
			map.put("seqno", criteria.getPredicates().get("seqno"));

			ContainerDetailObject cdo = new ContainerDetailObject();
			try {
				cntr = new StringTokenizer(criteria.getPredicates().get("contno"), "::");
				cdo.setContainerNo(cntr.nextElement().toString());
				cdo.setContainerSeqNo(cntr.nextElement().toString());
			} catch (Exception e) {
				log.info("Exception stuffingAdd : ", e);
				errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
			}
			cdo.setSeqNo(criteria.getPredicates().get("seqno"));
			cdo.setVarNo(vvcode);
			log.info("actioncmd :" + actioncmd);
			if (actioncmd.trim().equalsIgnoreCase("insert")) {
				log.info("inside insert*************136");
				Integer totalItem = Integer.parseInt(criteria.getPredicates().get("totalItem"));
				//edoesnnos = request.getParameterValues("edoesnno");
				//pkgs = request.getParameterValues("pkgs");
			    //isedo=new boolean[edoesnnos.length];
				edonos = new ArrayList<String>();
				esnnos = new ArrayList<String>();
				edopkgs = new ArrayList<String>();
				esnpkgs = new ArrayList<String>();

				for (int lp = 0; lp < totalItem; lp++) {
					log.info("----146");
					// if (edoesnnos[lp] != null && !edoesnnos[lp].equals("") && pkgs[lp] != null) {
					if (Integer.valueOf(criteria.getPredicates().get("pkgs" + lp)).intValue() > 0) {
						log.info("*** 149");
						if (criteria.getPredicates().get("edoesn" + lp) != null
								&& criteria.getPredicates().get("edoesn" + lp).equalsIgnoreCase("edo")) {
							log.info("--152");
							edonos.add(criteria.getPredicates().get("edoesnno" + lp));
							edopkgs.add(criteria.getPredicates().get("pkgs" + lp));
						} else {
							log.info("157 ****");
							esnnos.add(criteria.getPredicates().get("edoesnno" + lp));
							esnpkgs.add(criteria.getPredicates().get("pkgs" + lp));
						} // end-else
					} // end if integer
						// } // if null
				} // end-for

//					for (int lp = 0; lp < pkgs.length; lp++) {
//						log.info("----146");
//						if (edoesnnos[lp] != null && !edoesnnos[lp].equals("") && pkgs[lp] != null) {
//							if (Integer.valueOf(pkgs[lp]).intValue() > 0) {
//								log.info("*** 149");
//								if (criteria.getPredicates().get("edoesn" + lp) != null
//										&& criteria.getPredicates().get("edoesn" + lp).equalsIgnoreCase("edo")) {
//									log.info("--152");
//									edonos.add(edoesnnos[lp]);
//									edopkgs.add(pkgs[lp]);
//								} else {
//									log.info("157 ****");
//									esnnos.add(edoesnnos[lp]);
//									esnpkgs.add(pkgs[lp]);
//								} // end-else
//							} // end if integer
//						} // if null
//					} // end-for
				List<String> chkpkgs = null;
				try {
					log.info("165 ---");
					chkpkgs = outwardCargoService.checkEdoNoPkgs(edonos, edopkgs, vvcode, cdo.getSeqNo(), true);
					for (int lp = 0; chkpkgs != null && lp < chkpkgs.size(); lp++) {
						log.info("169 **");
						msg.append(chkpkgs.get(lp).toString());
					}
				} catch (Exception e) {
					log.info("Exception stuffingAdd : ", e);
					errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
					throw new BusinessException(errorMessage);
				}

				// chkESNStuffIndAL=new ArrayList();
				try {
					// vinayak added on 2 jan 2004
					String strAlEsnNbr = "";
					List<ContainerDetailObject> alEsnNbr = outwardCargoService.chkESNStuffInd(esnnos);
					log.info("182 alEsnNbr size :" + alEsnNbr.size());

					for (int lp = 0; lp < alEsnNbr.size(); lp++) {
						ContainerDetailObject cd = new ContainerDetailObject();
						cd = (ContainerDetailObject) alEsnNbr.get(lp);
						log.info("DummyESn: " + cd.getDummyEsn() + "ESN: " + cd.getEsnNbr());
						if (!cd.getDummyEsn().equalsIgnoreCase("Stuff Indicator")) {
							if (strAlEsnNbr.trim().length() > 0) {
								strAlEsnNbr += ",";
							}
							strAlEsnNbr = strAlEsnNbr + " " + cd.getEsnNbr();
							log.info("192 strAlEsnNbr :" + strAlEsnNbr);
							if (lp == (alEsnNbr.size() - 1)) {
								log.info("inside loop strAlEsnNbr :" + strAlEsnNbr);
								String[] tmp = {strAlEsnNbr};
								errorMessage = CommonUtil.getErrorMessage(ConstantUtil.errMsg_ESN_Not_For_Stuffing, tmp);
								throw new BusinessException(errorMessage);
							}
						}
						/*
						 * else { log.info("200 before adding ESN nbr"+cd.getEsnNbr());
						 * chkESNStuffIndAL.add(cd.getEsnNbr()); // vinayak added on 09/02/2004 }
						 */
					}

					if (chkpkgs == null || (chkpkgs != null && chkpkgs.size() <= 0)) {
						log.info("207 ---");
						chkpkgs = outwardCargoService.checkEsnNoPkgs(esnnos, esnpkgs, vvcode, cdo.getSeqNo(), true);
						for (int lp = 0; chkpkgs != null && lp < chkpkgs.size(); lp++) {
							log.info("211 //");
							msg.append(chkpkgs.get(lp).toString());
						}
					}
				} catch (BusinessException ex1) {
					log.info("Exception stuffingAdd : ", ex1);
					errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(ex1));
					if(errorMessage == null) {
						errorMessage = ex1.getMessage();
					}	
					throw new BusinessException(errorMessage);
				} catch(Exception e) {
					log.info("Exception stuffingAdd : ", e);
					errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
					throw new BusinessException(errorMessage);
				}

				if (chkpkgs != null && chkpkgs.size() > 0) {
					log.info("223 --");
					errorMessage = msg.toString();
					map.put("strmode", "ONE");
				} else {
					log.info("inside else part if chkpkgs 228---------");
					if (!outwardCargoService.isClosed(vvcode, cdo.getContainerNo(), cdo.getContainerSeqNo(),
							cdo.getSeqNo())) {
						log.info("231 **");
						cdo.setSeqNo(outwardCargoService.insertStuffing(edonos, edopkgs, esnnos, esnpkgs,
								cdo.getSeqNo(), vvcode, cdo.getContainerNo(), cdo.getContainerSeqNo(), UserID));
					} else {
						log.info("****** 238 ---");
						errorMessage = "M41201";
						map.put("strmode", "ONE");
					}
				}
				log.info("243 --");
				List<StuffingDetailObject> stuffingdetaillist = outwardCargoService.getStuffingDetails(cdo.getVarNo(),
						cdo.getContainerNo(), cdo.getContainerSeqNo(), cdo.getSeqNo());

				if (stuffingdetaillist.size() > 0)
					map.put("stuffingdetaillist", stuffingdetaillist);

			} else if (actioncmd.equalsIgnoreCase("view")) {
				log.info("253 --");
				List<StuffingDetailObject> stuffingdetaillist = outwardCargoService.getStuffingDetails(cdo.getVarNo(),
						cdo.getContainerNo(), cdo.getContainerSeqNo(), cdo.getSeqNo());
				log.info("257 //");
				if (stuffingdetaillist.size() > 0) {
					map.put("stuffingdetaillist", stuffingdetaillist);
					map.put("total", stuffingdetaillist.size());
				} else {
					log.info("261");
					errorMessage = "M41205";
					map.put("strmode", "ONE");
					map.put("stuffingdetaillist", stuffingdetaillist);

				}
			}

			map.put("containerdetailobject", cdo);
			map.put("screen", "StuffingAdd");

			result.setData(map);
			result.setSuccess(true);
		} catch (BusinessException be) {
			log.info("Exception stuffingAdd: " + be.getMessage());
			errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception stuffingAdd : ", e);
			errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				if (errorMessage != "M41205") {
					result.setError(errorMessage);
					result.setSuccess(false);
				}

			}
			log.info("END stuffingAdd result:" + result.toString());
		}

		return ResponseEntityUtil.success(result);

	}
	// EndRegion StuffingAddHandler

	// jp.src.delegate.helper.gbms.containerised.stuffing-->StuffingAmendHandler
	// StartRegion StuffingAmendHandler
	// method: perform()
	@ApiOperation(value = "stuffingAmend", response = String.class)
	@RequestMapping(value = "/outwardCargo/stuffingAmend", method = RequestMethod.POST)
	public ResponseEntity<?> stuffingAmend(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: stuffingAmend criteria:" + criteria.toString());

			String vvcode = null;
			String vslnm = null;
			String outvoyno = null;
			String actioncmd = null;

			List<String> edonos = null;
			List<String> esnnos = null;
			List<String> edopkgs = null;
			List<String> esnpkgs = null;

			String vsl = null;
			StringTokenizer cntr = null;

			StringBuffer msg = new StringBuffer();

			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			//String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			try {
				vvcode = criteria.getPredicates().get("vslName");
			} catch (Exception e) {
				log.info("Exception stuffingAmend : ", e);
				errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
				throw new BusinessException(errorMessage);
			}
			try {
				vsl = new String(CommonUtility.deNull(criteria.getPredicates().get("vslvoy")));

				if (vsl.indexOf("-") > 0) {
					vslnm = vsl.substring(0, vsl.lastIndexOf("-")).trim();
					outvoyno = vsl.substring(vsl.indexOf("-") + 1).trim();
				}

			} catch (Exception e) {
				log.info("Exception stuffingAmend : ", e);
				errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
			}
			actioncmd = CommonUtility.deNull(criteria.getPredicates().get("actioncmd"));

			map.put("vslnm", vslnm);
			map.put("vvcode", vvcode);
			map.put("outvoyno", outvoyno);
			map.put("contno", criteria.getPredicates().get("contno"));
			map.put("seqno", criteria.getPredicates().get("seqno"));

			log.info("115 ---");
			TableResult tableResult = outwardCargoService.getContainerDetails(vvcode,
					criteria.getPredicates().get("contno"), criteria);
			List<ContainerDetailObject> containerdetailslist = new ArrayList<ContainerDetailObject>();
			int size = tableResult.getData().getListData().getTopsModel().size();
			ContainerDetailObject containerList = new ContainerDetailObject();
			for (int i = 0; i < size; i++) {
				containerList = new ContainerDetailObject();
			  containerList = (ContainerDetailObject) tableResult.getData().getListData().getTopsModel().get(i);
			  containerdetailslist.add(containerList);
			}
			ContainerDetailObject cdo = null;
			for (int lp = 0; lp < containerdetailslist.size(); lp++)
				cdo = (ContainerDetailObject) containerdetailslist.get(lp);

			if (cdo == null)
				cdo = new ContainerDetailObject();

			try {
				log.info("128 ---");
				cntr = new StringTokenizer(criteria.getPredicates().get("contno"), "::");
				cdo.setContainerNo(cntr.nextElement().toString());
				cdo.setContainerSeqNo(cntr.nextElement().toString());
			} catch (Exception e) {
				log.info("Exception stuffingAmend : ", e);
				errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
				throw new BusinessException(errorMessage);
			}
			cdo.setSeqNo(criteria.getPredicates().get("seqno"));
			cdo.setVarNo(vvcode);

			map.put("containerdetailobject", cdo);

			if (actioncmd.trim().equalsIgnoreCase("update")) {
				log.info("141 ---");
				Integer totalItem = Integer.parseInt(criteria.getPredicates().get("totalItem"));
				edonos = new ArrayList<String>();
				esnnos = new ArrayList<String>();
				edopkgs = new ArrayList<String>();
				esnpkgs = new ArrayList<String>();

				for (int lp = 0; lp < totalItem; lp++) {
					log.info("150 //");
					// if (edoesnnos[lp] != null && !edoesnnos[lp].equals("") && pkgs[0] != null) {
					if (Integer.valueOf(criteria.getPredicates().get("pkgs" + lp)).intValue() > 0) {
						log.info("153");
						if (criteria.getPredicates().get("edoesn" + lp) != null
								&& criteria.getPredicates().get("edoesn" + lp).equalsIgnoreCase("edo")) {
							log.info("156");
							edonos.add(criteria.getPredicates().get("edoesnno" + lp));
							edopkgs.add(criteria.getPredicates().get("pkgs" + lp));
						} else {
							log.info("161****");
							esnnos.add(criteria.getPredicates().get("edoesnno" + lp));
							esnpkgs.add(criteria.getPredicates().get("pkgs" + lp));
						}
					}
					// }
				}

//					for (int lp = 0; lp < pkgs.length; lp++) {
//						log.info("150 //");
//						if (edoesnnos[lp] != null && !edoesnnos[lp].equals("") && pkgs[0] != null) {
//							if (Integer.valueOf(pkgs[lp]).intValue() > 0) {
//								log.info("153");
//								if (criteria.getPredicates().get("edoesn" + lp) != null
//										&& criteria.getPredicates().get("edoesn" + lp).equalsIgnoreCase("edo")) {
//									log.info("156");
//									edonos.add(edoesnnos[lp]);
//									edopkgs.add(pkgs[lp]);
//								} else {
//									log.info("161****");
//									esnnos.add(edoesnnos[lp]);
//									esnpkgs.add(pkgs[lp]);
//								}
//							}
//						}
//					}
				List<String> chkpkgs = null;
				try {
					log.info("169 ///");
					chkpkgs = outwardCargoService.checkEdoNoPkgs(edonos, edopkgs, vvcode, cdo.getSeqNo(), false);
					for (int lp = 0; chkpkgs != null && lp < chkpkgs.size(); lp++) {
						log.info("173 ///");
						msg.append(chkpkgs.get(lp).toString());
					}
				} catch (Exception e) {
					log.info("Exception stuffingAmend : ", e);
					errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
					throw new BusinessException(errorMessage);
				}

				try {
					// vinayak added on 2 jan 2004
					String strAlEsnNbr = "";
					List<ContainerDetailObject> alEsnNbr = outwardCargoService.chkESNStuffInd(esnnos);
					log.info("185 alEsnNbr :" + alEsnNbr.size());

					for (int lp = 0; lp < alEsnNbr.size(); lp++) {

						ContainerDetailObject cd = new ContainerDetailObject();
						cd = (ContainerDetailObject) alEsnNbr.get(lp);
						if (!cd.getDummyEsn().equalsIgnoreCase("Stuff Indicator")) {
							if (strAlEsnNbr.trim().length() > 0) {
								strAlEsnNbr += ",";
							}
							strAlEsnNbr = strAlEsnNbr + " " + cd.getEsnNbr();
							log.info("196+2 strAlEsnNbr :" + strAlEsnNbr);
							if (lp == (alEsnNbr.size() - 1)) {
								errorMessage = "ESN ASN " + strAlEsnNbr + " not for Stuffing.";
								String[] tempString = {strAlEsnNbr}; 
								throw new BusinessException(CommonUtil.getErrorMessage(ConstantUtil.ErrorMsg_ASNNbr_Stuffing, tempString));
								// return null;
							}
						}
					}

					if (chkpkgs == null || (chkpkgs != null && chkpkgs.size() <= 0)) {
						log.info("206 ---");
						chkpkgs = outwardCargoService.checkEsnNoPkgs(esnnos, esnpkgs, vvcode, cdo.getSeqNo(), false);
						for (int lp = 0; chkpkgs != null && lp < chkpkgs.size(); lp++) {
							msg.append(chkpkgs.get(lp).toString());
						}
					}
				} catch (BusinessException be) {
					log.info("Exception stuffingAmend: " + be.getMessage());
					errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
					if(errorMessage == null) {
						errorMessage = be.getMessage();
					}	
					throw new BusinessException(errorMessage);
				} catch(Exception e) {
					log.info("Exception stuffingAmend : ", e);
					errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
					throw new BusinessException(errorMessage);
				}

				if (chkpkgs != null && chkpkgs.size() > 0) {
					log.info("220 ---");
					errorMessage = msg.toString();
					map.put("strmode", "ONE");
				} else
					cdo.setSeqNo(outwardCargoService.updateStuffing(edonos, edopkgs, esnnos, esnpkgs, cdo.getSeqNo(),
							vvcode, cdo.getContainerNo(), cdo.getContainerSeqNo(), UserID));

				List<StuffingDetailObject> stuffingdetaillist = outwardCargoService.getStuffingDetails(cdo.getVarNo(),
						cdo.getContainerNo(), cdo.getContainerSeqNo(), cdo.getSeqNo());
				log.info("232 ---");
				if (stuffingdetaillist.size() > 0)
					map.put("stuffingdetaillist", stuffingdetaillist);
				else {
					errorMessage = "Error";
					map.put("strmode", "ONE");
				}

			} else if (actioncmd.trim().equalsIgnoreCase("amend")) {
				log.info("242 ---");
				List<StuffingDetailObject> stuffingdetaillist = outwardCargoService.getStuffingDetailsToAmend(cdo.getVarNo(),
						cdo.getContainerNo(), cdo.getContainerSeqNo(), cdo.getSeqNo());
				log.info("247 :" + stuffingdetaillist.size());
				if (stuffingdetaillist.size() > 0)
					map.put("stuffingdetaillist", stuffingdetaillist);
				else {
					errorMessage = "M41202";
					map.put("strmode", "ONE");
				}
			} else if (actioncmd.trim().equalsIgnoreCase("assignbill")) {
				log.info("256 ---");
				List<String> billaccountnos = outwardCargoService.getBillAccountNos(vvcode);
				List<SchemeAccountObject> schemeaccountlist = outwardCargoService.getSchemeAccountNos();

				map.put("billaccountnos", billaccountnos);
				map.put("schemeaccountlist", schemeaccountlist);
			} else if (actioncmd.trim().equalsIgnoreCase("updateassignbill")) {
				log.info("264 ---");
				String acctno = null;
				if (CommonUtility.deNull(criteria.getPredicates().get("billparty")).trim().equalsIgnoreCase("avlbp"))
					acctno = CommonUtility.deNull(criteria.getPredicates().get("acctno")).trim();
				else
					acctno = CommonUtility.deNull(criteria.getPredicates().get("otheracctno")).trim();

				outwardCargoService.assignBillableParty(vvcode, cdo.getContainerNo(), cdo.getContainerSeqNo(),
						cdo.getSeqNo(), acctno, UserID);
				log.info("276---");
			}

			map.put("screen", "StuffingAmend");

			result.setData(map);
			result.setSuccess(true);
		} catch (BusinessException be) {
			log.info("Exception stuffingAmend: " + be.getMessage());
			errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception stuffingAmend : ", e);
			errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result.setError(errorMessage);
				result.setSuccess(false);
			}
			log.info("END stuffingAmend result:" + result.toString());
		}

		return ResponseEntityUtil.success(result);

	}
	// EndRegion StuffingAmendHandler

	// jp.src.delegate.helper.gbms.containerised.stuffing-->StuffingCancelHandler
	// StartRegion StuffingCancelHandler
	// method: perform()
	/**
	 * This method is used to interact with StuffingEJB and perform database
	 * operation for cancellation of stuffing.
	 * 
	 * @param request HttpServlet Request object.
	 * @throws RequestException
	 */
	@ApiOperation(value = "stuffingCancel", response = String.class)
	@RequestMapping(value = "/outwardCargo/stuffingCancel", method = RequestMethod.POST)
	public ResponseEntity<?> stuffingCancel(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("START: stuffingCancel criteria:" + criteria.toString());

			String vvcode = null;
			String vslnm = null;
			String outvoyno = null;
			String actioncmd = null;

			String vsl = null;
			StringTokenizer cntr = null;

			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			//String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			try {
				vvcode = criteria.getPredicates().get("vslName");
			} catch (Exception e) {
				log.info("Exception stuffingCancel : ", e);
				errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
				throw new BusinessException(errorMessage);
			}
			try {
				vsl = new String(CommonUtility.deNull(criteria.getPredicates().get("vslvoy")));

				if (vsl.indexOf("-") > 0) {
					vslnm = vsl.substring(0, vsl.lastIndexOf("-")).trim();
					outvoyno = vsl.substring(vsl.indexOf("-") + 1).trim();
				}

			} catch (Exception e) {
				log.info("Exception stuffingCancel : ", e);
				errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
			}
			actioncmd = CommonUtility.deNull(criteria.getPredicates().get("actioncmd"));

			map.put("vslnm", vslnm);
			map.put("vvcode", vvcode);
			map.put("outvoyno", outvoyno);
			map.put("contno", criteria.getPredicates().get("contno"));
			map.put("seqno", criteria.getPredicates().get("seqno"));

			TableResult tableResult = outwardCargoService.getContainerDetails(vvcode,
					criteria.getPredicates().get("contno"), criteria);
			List<ContainerDetailObject> containerdetailslist = new ArrayList<ContainerDetailObject>();
			int size = tableResult.getData().getListData().getTopsModel().size();
			ContainerDetailObject containerList = new ContainerDetailObject();
			for (int i = 0; i < size; i++) {
				containerList = new ContainerDetailObject();
			  containerList = (ContainerDetailObject) tableResult.getData().getListData().getTopsModel().get(i);
			  containerdetailslist.add(containerList);
			}
			ContainerDetailObject cdo = null;
			for (int lp = 0; lp < containerdetailslist.size(); lp++)
				cdo = (ContainerDetailObject) containerdetailslist.get(lp);

			if (cdo == null)
				cdo = new ContainerDetailObject();

			try {
				cntr = new StringTokenizer(criteria.getPredicates().get("contno"), "::");
				cdo.setContainerNo(cntr.nextElement().toString());
				cdo.setContainerSeqNo(cntr.nextElement().toString());
			} catch (Exception e) {
				log.info("Exception stuffingCancel : ", e);
				errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
				throw new BusinessException(errorMessage);
			}
			cdo.setSeqNo(criteria.getPredicates().get("seqno"));
			cdo.setVarNo(vvcode);

			map.put("containerdetailobject", cdo);

			if (actioncmd.trim().equalsIgnoreCase("cancel")) {
				if (outwardCargoService.isClosed(vvcode, cdo.getContainerNo(), cdo.getContainerSeqNo(),
						cdo.getSeqNo())) {
					if (outwardCargoService.isChkDNCreated(cdo.getSeqNo())) {
						errorMessage = "Cancel DN for Transhipment/Reexport EDO";
						return null;
					} else {
					}
				}
				outwardCargoService.cancelStuffing(vvcode, cdo.getContainerNo(), cdo.getContainerSeqNo(),
						cdo.getSeqNo(), UserID);
				map.put("seqno", "--");
			}
			map.put("screen", "StuffingFPSvlt");

			result.setData(map);
			result.setSuccess(true);
			
		} catch (BusinessException be) {
			log.info("Exception stuffingCancel: " + be.getMessage());
			errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception stuffingCancel : ", e);
			errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result.setError(errorMessage);
				result.setSuccess(false);
			}
			log.info("END stuffingCancel result:" + result.toString());
		}

		return ResponseEntityUtil.success(result);

	}
	// EndRegion StuffingCancelHandler

	// jp.src.delegate.helper.gbms.containerised.stuffing-->StuffingCloseHandler
	// StartRegion StuffingCloseHandler
	// method: perform()
	@ApiOperation(value = "stuffingClose", response = String.class)
	@RequestMapping(value = "/outwardCargo/stuffingClose", method = RequestMethod.POST)
	public ResponseEntity<?> stuffingClose(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;

		try {
			log.info("START: stuffingClose criteria:" + criteria.toString());

			String vvcode = null;
			String vslnm = null;
			String outvoyno = null;
			String actioncmd = null;


			String vsl = null;
			StringTokenizer cntr = null;

			String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			//String companycode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));


			try {
				vvcode = criteria.getPredicates().get("vslName");
			} catch (Exception e) {
				log.info("Exception stuffingClose : ", e);
				errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
				throw new BusinessException(errorMessage);
			}
			try {
				vsl = new String(CommonUtility.deNull(criteria.getPredicates().get("vslvoy")));

				if (vsl.indexOf("-") > 0) {
					vslnm = vsl.substring(0, vsl.lastIndexOf("-")).trim();
					outvoyno = vsl.substring(vsl.indexOf("-") + 1).trim();
				}

			} catch (Exception e) {
				log.info("Exception stuffingClose : ", e);
				errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
				throw new BusinessException(errorMessage);
			}
			actioncmd = CommonUtility.deNull(criteria.getPredicates().get("actioncmd"));

			map.put("vslnm", vslnm);
			map.put("vvcode", vvcode);
			map.put("outvoyno", outvoyno);
			map.put("contno", criteria.getPredicates().get("contno"));
			map.put("seqno", criteria.getPredicates().get("seqno"));

			ContainerDetailObject cdo = new ContainerDetailObject();
			try {
				cntr = new StringTokenizer(criteria.getPredicates().get("contno"), "::");
				cdo.setContainerNo(cntr.nextElement().toString());
				cdo.setContainerSeqNo(cntr.nextElement().toString());
			} catch (Exception e) {
				log.info("Exception stuffingClose : ", e);
				errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");
			}
			
			cdo.setSeqNo(criteria.getPredicates().get("seqno"));
			cdo.setVarNo(vvcode);
			cdo.setWaiveCharge(criteria.getPredicates().get("waive"));
			cdo.setStuffingDttm(criteria.getPredicates().get("stfdt"));

			if (actioncmd.trim().equalsIgnoreCase("close")) {
				log.info("Handler stage 1");
				if (outwardCargoService.isStuffingDttmLesser(cdo.getStuffingDttm())) {
					// added by vinayak on 11/02/2004
					if (!outwardCargoService.isGbEdoUpd(cdo.getSeqNo())) {
						if (!outwardCargoService.isClosed(vvcode, cdo.getContainerNo(), cdo.getContainerSeqNo(),
								cdo.getSeqNo())) {
							outwardCargoService.closeStuffing(vvcode, cdo.getContainerNo(), cdo.getContainerSeqNo(),
									cdo.getSeqNo(), cdo.getStuffingDttm(), cdo.getWaiveCharge(), UserID);
						} else if (outwardCargoService.isClosed(vvcode, cdo.getContainerNo(), cdo.getContainerSeqNo(),
								cdo.getSeqNo())) {
							errorMessage = "Stuffing Record Already Closed";
							map.put("strmode", "ONE");
						}
					} else {
						errorMessage = "Insufficient DN number of packages for Stuffing";

					}
				} else {
					errorMessage = "Stuffing Date and Time Should Be Less Than Current Date and Time.";
					map.put("strmode", "ONE");
				}

				//List<VesselVoyValueObject> vesselvoy = outwardCargoService.getVesselVoyage(companycode);
				//List<String> containerlist = outwardCargoService.getContainerNos(vvcode);
				
			} else if (actioncmd.trim().equalsIgnoreCase("closeupdate")) {
				log.info("Handler stage 2");
				outwardCargoService.updateWaiverStatus(vvcode, cdo.getContainerNo(), cdo.getContainerSeqNo(),
						cdo.getSeqNo(), cdo.getWaiveCharge(), UserID);
				log.info("Handler stage 3");
			}
			map.put("containerdetailobject", cdo);
			map.put("screen", "StuffingClose");

		} catch (BusinessException be) {
			log.info("Exception stuffingClose: " + be.getMessage());
			errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}	
		} catch (Exception e) {
			log.info("Exception stuffingClose : ", e);
			errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");;
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END stuffingClose result:" + result.toString());
		}

		return ResponseEntityUtil.success(result);

	}

	// EndRegion StuffingCloseHandler
	
	
	
	
	// jp.src.delegate.helper.gbms.containerised.stuffing-->StuffingShowHandler
	// StartRegion StuffingCloseHandler
	// method: perform()
	@ApiOperation(value = "isTesnNbr", response = String.class)
	@RequestMapping(value = "/outwardCargo/isTesnNbr", method = RequestMethod.POST)
	public ResponseEntity<?> isTesnNbr(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		boolean isTesn = false;
		try {
			log.info("START: isTesnNbr criteria:" + criteria.toString());

			String esnNo = CommonUtility.deNull(criteria.getPredicates().get("esnNo"));
			isTesn = outwardCargoService.isTesnNbr(esnNo);
			map.put("isTesn", isTesn);
		
		} catch (Exception e) {
			log.info("Exception isTesnNbr : ", e);
			errorMessage = ConstantUtil.OUTWARD_STUFF_OUTBOUND_CTR_MAP.get("M4201");;
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				result.setError(errorMessage);
				result.setSuccess(false);
			} else {
				result.setData(map);
				result.setSuccess(true);
			}
			log.info("END isTesnNbr result:" + result.toString());
		}

		return ResponseEntityUtil.success(result);

	}
}
