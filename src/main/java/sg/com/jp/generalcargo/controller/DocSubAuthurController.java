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
import sg.com.jp.generalcargo.domain.DocSubAuthorValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.service.DocSubAuthurService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = DocSubAuthurController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DocSubAuthurController {
	public static final String ENDPOINT = "gc/docsubauthor";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private static final Log log = LogFactory.getLog(DocSubAuthurController.class);
	private String errorMessage = null;

	@Autowired
	private DocSubAuthurService docSubAuthurService;

	// delegate.helper.gbms.cargo.docsubauthor --> DocSubAuthorListHandler
	// docSubAuthorList
	@PostMapping(value = "/docSubAuthorList") //
	public ResponseEntity<?> docSubAuthorList(HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		errorMessage = null;
		try {
			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: docSubAuthorList :: criteria: " + criteria.toString());
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String selVvcd = CommonUtility.deNull(criteria.getPredicates().get("vvcd"));
//			List<DocSubAuthorValueObject> mftlis = new ArrayList<DocSubAuthorValueObject>();
			List<DocSubAuthorValueObject> mftlist = new ArrayList<DocSubAuthorValueObject>();

			List<String> vvcdlist = new ArrayList<String>();
			List<String> vslnmlist = new ArrayList<String>();
			List<String> invoynbrlist = new ArrayList<String>();
			List<String> outvoynbrlist = new ArrayList<String>();
			List<String> docsubauthorlist = new ArrayList<String>();
			List<String> agentNameList = new ArrayList<String>();
			List<String> terminalList = new ArrayList<String>();
			List<String> schemeList = new ArrayList<String>();
			List<String> subSchemeList = new ArrayList<String>();
			List<String> gcOperationsList = new ArrayList<String>();
			List<String> statusList = new ArrayList<String>();
			List<String> userIdList = new ArrayList<String>();
			List<String> createDttmList = new ArrayList<String>();
			List<String> agentFullNameList = new ArrayList<String>();
			String authorParty = "";// Added by vietnd02 - get authorParty

			List<DocSubAuthorValueObject> vesselCallList = docSubAuthurService.getVesselVoy(coCd);

			TopsModel topsModel = new TopsModel();

			for (int i = 0; i < vesselCallList.size(); i++) {
				DocSubAuthorValueObject vvvObj = new DocSubAuthorValueObject();
				vvvObj = (DocSubAuthorValueObject) vesselCallList.get(i);
				topsModel.put(vvvObj);
			}

			map.put("selVvcd", selVvcd);

//			if (selVvcd != null && !selVvcd.equals("")) {
//				mftlis = docSubAuthurService.getVesselList(selVvcd, criteria);
//				for (int i = 0; i < mftlis.size(); i++) {
//					DocSubAuthorValueObject mftvObj = new DocSubAuthorValueObject();
//					mftvObj = (DocSubAuthorValueObject) mftlis.get(i);
//				}
//			}

			if (selVvcd != null && !selVvcd.equals("")) {
				mftlist = docSubAuthurService.getVesselList(selVvcd, criteria);
				for (int i = 0; i < mftlist.size(); i++) {
					DocSubAuthorValueObject mftvObj = new DocSubAuthorValueObject();
					mftvObj = (DocSubAuthorValueObject) mftlist.get(i);
					vvcdlist.add((String) mftvObj.getVvCd());
					vslnmlist.add((String) mftvObj.getVslName());
					invoynbrlist.add((String) mftvObj.getInVoyNbr());
					outvoynbrlist.add((String) mftvObj.getOutVoyNbr());
					docsubauthorlist.add((String) mftvObj.getDocSubAuthor());
					agentNameList.add((String) mftvObj.getAgtNm());
					terminalList.add((String) mftvObj.getTerminal());
					schemeList.add((String) mftvObj.getScheme());
					subSchemeList.add((String) mftvObj.getSubScheme());
					gcOperationsList.add((String) mftvObj.getGcOperations());
					statusList.add((String) mftvObj.getStatus());
					userIdList.add((String) mftvObj.getUserId());
					createDttmList.add((String) mftvObj.getCreateDttm());
					agentFullNameList.add((String) mftvObj.getAgentFullName());
				}

			}

			authorParty = docSubAuthurService.getAuthorParty(selVvcd);
			int total = docSubAuthurService.getVesselListCount(selVvcd, criteria);

			map.put("total", total);
			map.put("vvcdlist", vvcdlist);
			map.put("vslnmlist", vslnmlist);
			map.put("invoynbrlist", invoynbrlist);
			map.put("outvoynbrlist", outvoynbrlist);
			map.put("docsubauthorlist", docsubauthorlist);
			map.put("authorParty", authorParty);
			map.put("agentNameList", agentNameList);
			map.put("terminalList", terminalList);
			map.put("schemeList", schemeList);
			map.put("subSchemeList", subSchemeList);
			map.put("gcOperationsList", gcOperationsList);
			map.put("statusList", statusList);
			map.put("userIdList", userIdList);
			map.put("createDttmList", createDttmList);
			map.put("agentFullNameList", agentFullNameList);
			map.put("model", topsModel.getTopsModel());
			map.put("screen", "docSubAuthorListSer");
		} catch (BusinessException e) {
			log.info("Exception docSubAuthorList : ", e);
			errorMessage = ConstantUtil.DOCSUB_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception docSubAuthorList : ", e);
			errorMessage = ConstantUtil.DOCSUB_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				map.clear();
				map.put("errorMessage", errorMessage);
				result.setSuccess(false);
				result.setErrors(map);
			} else {
				result.setSuccess(true);
				result.setData(map);
			}
			log.info("docSubAuthorList Result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}

	// delegate.helper.gbms.cargo.docsubauthor --> DocSubAuthorUpdateHandler
	@PostMapping(value = "/docSubAuthorUpdate")
	public ResponseEntity<?> docSubAuthorUpdate(HttpServletRequest request) {
		// 12790790000R
		String vvcd = "";
		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		errorMessage = null;
		try {
			// ++ Updated by vietnd02 16-10 - get parameter to check manifest
			String vslnm = "";
			String invoynbr = "";

			Criteria criteria = CommonUtil.getCriteria(request);
			log.info("START: docSubAuthorUpdate :: criteria: " + criteria.toString());
			if (criteria.getPredicates().get("vvcd") != null || criteria.getPredicates().get("vslnm") != null
					|| criteria.getPredicates().get("invoynbr") != null) {
				vvcd = CommonUtility.deNull(criteria.getPredicates().get("vvcd"));
				vslnm = CommonUtility.deNull(criteria.getPredicates().get("vslnm"));
				invoynbr = CommonUtility.deNull(criteria.getPredicates().get("invoynbr"));
			}
			// -- Updated by vietnd02 16-10 - get parameter to check manifest
			String newstatus = docSubAuthurService.checkVesselStatus(vvcd);

			StringTokenizer st1 = new StringTokenizer(newstatus, "|");
			String newvvstatus = st1.nextToken();
			String newclosebjind = st1.nextToken();
			String newcloseshpind = st1.nextToken();
			// java.lang.String strerrorstatus="false";

			/*
			 * >> Add by FPT.Thai - Sep 30 2009 CR.BPR and WWL Documentation Enhancement
			 * URS_Clarification
			 */
			boolean deleteCmd = false;
			if (criteria.getPredicates().get("delmode") != null) {
				String delMode = CommonUtility.deNull(criteria.getPredicates().get("delmode"));
				if (delMode.equalsIgnoreCase("TRUE")) {
					deleteCmd = true;
				}
			}
			if (!deleteCmd) {

				/*
				 * << Add by FPT.Thai - Sep 30 2009 CR.BPR and WWL Documentation Enhancement
				 * URS_Clarification
				 */
				if (!(newvvstatus.equalsIgnoreCase("AP") || newvvstatus.equalsIgnoreCase("AL")
						|| newvvstatus.equalsIgnoreCase("PR") || newvvstatus.equalsIgnoreCase("BR")
						|| newvvstatus.equalsIgnoreCase("UB"))) {
					errorMessage = ConstantUtil.ErrorMsg_Vessel_Status_Changed_Cannot_Update
							+ ConstantUtil.ErrorMsg_Authorization_Document_Submission;
				}
				if (newclosebjind.equalsIgnoreCase("Y")) {
					errorMessage = ConstantUtil.ErrorMsg_Vessel_Status_Changed_Cannot_Update
							+ ConstantUtil.ErrorMsg_Authorization_Document_Submission;
				}
				if (newcloseshpind.equalsIgnoreCase("Y")) {
					errorMessage = ConstantUtil.ErrorMsg_Vessel_Status_Changed_Cannot_Update
							+ ConstantUtil.ErrorMsg_Authorization_Document_Submission;
				}
			} // -- Add by FPT.Thai - Sep 30 2009
			String scrmode = "";
			if (criteria.getPredicates().get("scrmode") != null) {
				scrmode = CommonUtility.deNull(criteria.getPredicates().get("scrmode"));
			}
			if (scrmode.equalsIgnoreCase("LIST")) {
				map.put("scrmode1", "UPDATE");
			}

			if (scrmode.equalsIgnoreCase("UPDATE")) {
				String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
				/*
				 * >> Add by FPT.Thai - Sep 30 2009 CR.BPR and WWL Documentation Enhancement
				 * URS_Clarification
				 */
				if (!deleteCmd) {
					String strcustcd = "";
					String docsubtdbcrnbr = "";

					if (criteria.getPredicates().get("docsubtdbcrnbr") != null) {
						docsubtdbcrnbr = (String) criteria.getPredicates().get("docsubtdbcrnbr");
					}
					// System.out.println("docsubtdbcrnbr"+docsubtdbcrnbr);
					strcustcd = docSubAuthurService.getCustomerNbr(docsubtdbcrnbr);
					if (strcustcd.equalsIgnoreCase("")) {
						errorMessage = ConstantUtil.ErrorMsg_Enter_Valid_Authorization_Document
								+ ConstantUtil.ErrorMsg_Submission_TDB_CR_No;
					} else {
						docSubAuthurService.updateADSDetails(strcustcd, userId, vvcd, null, vslnm, invoynbr);
					}
				} else {
					int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
					// -- Delete Auth
					if (sizeSelected != 0) {
//						String[] docsubauthorlist = (String[]) request.getParameterValues("docsubauthorlist");
//						String[] checklist = (String[]) request.getParameterValues("checklist");
//						String[] agtnmlist = (String[]) request.getParameterValues("agtnmlist");// Add by VietND02 - get
																								// parameter to delete
																								// docsubauthorlist
						List<String> docsubauthorlist =  new ArrayList<String>(); 
						String docSubAuthorNo;
						List<String> checklist =  new ArrayList<String>(); 
						String checkListNo;
						List<String> agtnmlist =  new ArrayList<String>(); 
						String agtnmListNo;																		

						for (int i = 0; i < sizeSelected; i++) {
							docSubAuthorNo = (String) criteria.getPredicates().get("docsubauthorlist" + i);
							docsubauthorlist.add(docSubAuthorNo);
							checkListNo = (String) criteria.getPredicates().get("checklist" + i);
							checklist.add(checkListNo);
							agtnmListNo = (String) criteria.getPredicates().get("agtnmlist" + i);
							agtnmlist.add(agtnmListNo);
						}

						List<String> docsubauthorvector = new ArrayList<String>();
						// -- Selected to delete information checking
						int numOfdel = 0;
						if (docsubauthorlist.size() > 0 && checklist.size() > 0) {
							for (int i = 0; i < docsubauthorlist.size(); i++) {
								for (int j = 0; j < checklist.size(); j++) {
									if (docsubauthorlist.get(i).equalsIgnoreCase(checklist.get(j))) {
										log.info("docsubauthorlist[" + i + "]:" + docsubauthorlist.get(i));
										log.info("checklist[" + j + "]:" + checklist.get(j));
										docsubauthorvector.add(agtnmlist.get(i));// Add by VietND02
										numOfdel++;
									}
								}
							}
							// -- End of Selected to delete information checking
							log.info(">> STARTING DELETE >>");
							log.info("Number of docsubauthor to delete = " + numOfdel);
							docSubAuthurService.updateADSDetails(null, userId, vvcd, docsubauthorvector, vslnm,
									invoynbr);
						}
					}

				}
				/*
				 * << Add by FPT.Thai - Sep 30 2009 CR.BPR and WWL Documentation Enhancement
				 * URS_Clarification
				 */
				scrmode = "LIST";
				map.put("scrmode1", "UPDATED");
			}
			if (scrmode.equalsIgnoreCase("LIST")) {
				List<DocSubAuthorValueObject> docsubauthorvector = new ArrayList<DocSubAuthorValueObject>();
				docsubauthorvector = docSubAuthurService.getVesselDetails(vvcd);
				TopsModel topsModel = new TopsModel();
				DocSubAuthorValueObject docSubAuthorValueObject = null;
				for (int i = 0; i < docsubauthorvector.size(); i++) {
					docSubAuthorValueObject = docsubauthorvector.get(i);
					topsModel.put(docSubAuthorValueObject);
				}
				map.put("docSubAuthorValueObject", topsModel.getTopsModel());
			}
			map.put("scrmode", scrmode);
			map.put("screen", "docSubAuthorUpdateSer");
		} catch (BusinessException e) {
			log.info("Exception docSubAuthorUpdate : ", e);
			errorMessage = ConstantUtil.DOCSUB_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
			if (errorMessage == null) {
				errorMessage = CommonUtility.getExceptionMessage(e);
			}
		} catch (Exception e) {
			log.info("Exception docSubAuthorUpdate : ", e);
			errorMessage = ConstantUtil.DOCSUB_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
				map.clear();
				map.put("errorMessage", errorMessage);
				result.setSuccess(false);
				result.setErrors(map);
			} else {
				result.setSuccess(true);
				result.setData(map);
			}
			log.info("docSubAuthorUpdate Result:" + result.toString());
		}
		return ResponseEntityUtil.success(result.toString());
	}
}
