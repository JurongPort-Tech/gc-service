package sg.com.jp.generalcargo.controller;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.iterators.ArrayListIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.com.jp.generalcargo.domain.AdpValueObject;
import sg.com.jp.generalcargo.domain.CashSalesValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EdoJpBilling;
import sg.com.jp.generalcargo.domain.EdoValueObjectCargo;
import sg.com.jp.generalcargo.domain.EdoValueObjectOps;
import sg.com.jp.generalcargo.domain.HsCodeDetails;
import sg.com.jp.generalcargo.domain.ManiFestObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.DnEdoDetailService;
import sg.com.jp.generalcargo.service.InwardCargoEdoService;
import sg.com.jp.generalcargo.service.InwardCargoManifestService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

import static org.reflections.Reflections.log;

@CrossOrigin
@RestController
@RequestMapping(value = InwardCargoEdoController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InwardCargoEdoController {

    public static final String ENDPOINT = "gc/inwardcargo/edo";
    public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
    private String errorMessage = null;
    private static final Log log = LogFactory.getLog(InwardCargoEdoController.class);

    @Autowired
    InwardCargoEdoService edoService;
    @Autowired
    InwardCargoManifestService inwardCargoManifestService;
    @Autowired
    DnEdoDetailService dnEdoDetailService;
    // StartRegion Cargo Edo List

    //delegate.helper.gbms.cargo.edo - EdoVesselVoyageNbrListHandler
    @PostMapping(value = "/edoVesselVoyageNbrList")
    public ResponseEntity<?> edoVesselVoyageNbrList(HttpServletRequest request) {
        Result result = new Result();
        TableResult tableResult = new TableResult();
        TopsModel topsModel = new TopsModel();
        TopsModel topsModel1 = new TopsModel();
        Map<String, Object> map = new HashMap<String, Object>();
        errorMessage = null;
        int size = 0;
        try {
            Criteria criteria = CommonUtil.getCriteria(request);
            log.info("Start edoVesselVoyageNbrList criteria :" + criteria.toString());
            String strmodulecd = ConstantUtil.EDO;
            String modulecd = CommonUtility.deNull(criteria.getPredicates().get("modulecd"));
            String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
            List<EdoValueObjectCargo> edoVesselvoyageList = new ArrayList<>();
            List<EdoValueObjectCargo> vesselvoyageList1 = new ArrayList<>(); // Added by LongDh09
            String PageIndex = ((CommonUtility.deNull(criteria.getPredicates().get("PageIndex"))).equals("") ? "1"
                    : CommonUtility.deNull(criteria.getPredicates().get("PageIndex")));
            String strvarnbr = "";
            if (modulecd != null && !modulecd.isEmpty()) {
                strmodulecd = modulecd;
            }
            log.info("strmodulecd : " + strmodulecd);
            log.info("edoVesselVoyageNbrList::varnbr = " + criteria.getPredicates().get("varnbr"));
            // Added by LongDh09::Start
            String isFetch = CommonUtility.deNull(criteria.getPredicates().get("isFetch"));
            String vesselName = "";
            String voyageNumber = "";
            // HaiTTH1 added on 14/1/2014
            String arrival = "";
            String departure = "";
            String cod_dttm = "";
            String etb_dttm = "";
            String terminal = "";
            if ((criteria.getPredicates().get("vesselName") != null) && !(criteria.getPredicates().get("vesselName").isEmpty())) {
                vesselName = (CommonUtility.deNull(criteria.getPredicates().get("vesselName"))).toUpperCase().trim();
                voyageNumber = (CommonUtility.deNull(criteria.getPredicates().get("voyageNumber"))).toUpperCase()
                        .trim();
                log.info("isFetch.1::vesselName = " + vesselName);
                log.info("isFetch.2::vesselName = " + voyageNumber);
                map.put("vesselName", vesselName);
                map.put("voyageNumber", voyageNumber);
            }
            strvarnbr = CommonUtility.deNull(criteria.getPredicates().get("varnbr"));
            String selectDropDown = CommonUtility.deNull(criteria.getPredicates().get("SelectDropDown"));
            map.put("selectDropDown", selectDropDown);
            log.info("selectDropDown in EDO = " + selectDropDown);

            if (vesselName == null || "".equalsIgnoreCase(vesselName)) {
                log.info("INSIDE SESSION");
                vesselName = CommonUtility.deNull(criteria.getPredicates().get("vslNm"));
                voyageNumber = CommonUtility.deNull(criteria.getPredicates().get("vslVoy"));
                log.info("INSIDE SESSION2: " + vesselName + "/" + voyageNumber);
                map.remove("vslNm");
                map.remove("vslVoy");
            }

            map.put("isFetch", isFetch);
            terminal = CommonUtility.deNull(criteria.getPredicates().get("terminal"));
            map.put("terminal", terminal);

            log.info("EdoVesselVoyageNbrListHandler::isFetch = " + isFetch);
            log.info("EdoVesselVoyageNbrListHandler::vesselName = " + vesselName);
            log.info("EdoVesselVoyageNbrListHandler::vesselName = " + voyageNumber);
            map.put("isFetch", isFetch);
            // Added by LongDh09::End

            if (criteria.getPredicates().get("varnbr") == null || (criteria.getPredicates().get("varnbr").isEmpty())) {
                edoVesselvoyageList = edoService.getVesselVoyageNbrList(coCd, strmodulecd);
                for (int i = 0; i < edoVesselvoyageList.size(); i++) {
                    EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
                    edoValueObject = (EdoValueObjectCargo) edoVesselvoyageList.get(i);
                    topsModel.put(edoValueObject);
                    size = topsModel.getSize();
                }
            } else {
                // ++ 19.10.2009 updated by thanhnv2 for GB CR
                if (!isFetch.equals("FETCH")) {
                    map.put("strvarnbr", strvarnbr);
                    // HaiTTH1 added on 14/1/2014
                    VesselVoyValueObject vessel = edoService.getVesselInfo(strvarnbr);
                    String vesselName1 = vessel.getVslName();
                    String voyageNumber1 = vessel.getVoyNo();
                    List<EdoValueObjectCargo> vt = edoService.getVesselVoyageNbrList(coCd, strmodulecd, vesselName1,
                            voyageNumber1);
                    if (vt.size() > 0) {
                        EdoValueObjectCargo edoObj = (EdoValueObjectCargo) vt.get(0);
                        arrival = edoObj.getArrival();
                        departure = edoObj.getDeparture();
                        cod_dttm = edoObj.getCod_dttm();
                        etb_dttm = edoObj.getEtb_dttm();
                        terminal = edoObj.getTerminal();
                    }
                    map.put("vesselName", vesselName1);
                    map.put("voyageNumber", voyageNumber1);
                }
                // -- 19.10.2009 updated by thanhnv2 for GB CR
                List<String> edovesselvoyList = new ArrayList<>();

                edoVesselvoyageList = edoService.getVesselVoyageNbrList(coCd, strmodulecd);
                for (int i = 0; i < edoVesselvoyageList.size(); i++) {
                    EdoValueObjectCargo edoValueObject1 = new EdoValueObjectCargo();
                    edoValueObject1 = edoVesselvoyageList.get(i);
                    edovesselvoyList.add(edoValueObject1.getVarNbr());
                    edovesselvoyList.add(edoValueObject1.getVslNbr());
                    edovesselvoyList.add(edoValueObject1.getInVoyNbr());
                    edovesselvoyList.add(edoValueObject1.getTerminal());
                }

                // Added by LongDh09::Start
                log.info("EdoVesselVoyageNbrListHandler::vesselvoyagevector1.size() = " + vesselName + " -- "
                        + voyageNumber);
                vesselvoyageList1 = edoService.getVesselVoyageNbrList(coCd, strmodulecd, vesselName, voyageNumber);
                log.info("EdoVesselVoyageNbrListHandler::vesselvoyagevector1.size() = " + vesselvoyageList1.size());
                if (vesselvoyageList1.size() != 0) {
                    for (int i = 0; i < vesselvoyageList1.size(); i++) {
                        EdoValueObjectCargo edoValueObject1 = new EdoValueObjectCargo();
                        edoValueObject1 = (EdoValueObjectCargo) vesselvoyageList1.get(i);
                        // edovesselvoyagevector.addElement(edoValueObject1.getVarNbr());
                        // edovesselvoyagevector.addElement(edoValueObject1.getVslNbr());
                        // edovesselvoyagevector.addElement(edoValueObject1.getInVoyNbr());
                        // if(isFetch.equals("FETCH")) {
                        strvarnbr = edoValueObject1.getVarNbr();
                        // request.setAttribute("strvarnbr", strvarnbr); // add by thanhnv2
                        // }
                        // HaiTTH1 added on 14/1/2014
                        arrival = edoValueObject1.getArrival();
                        departure = edoValueObject1.getDeparture();
                        cod_dttm = edoValueObject1.getCod_dttm();
                        etb_dttm = edoValueObject1.getEtb_dttm();
                        terminal = edoValueObject1.getTerminal();
                    }
                    /*
                     * request.setAttribute("vesselName", vesselName);
                     * request.setAttribute("voyageNumber", voyageNumber);
                     * request.setAttribute("isFetch", isFetch);
                     */
                    log.info("EdoVesselVoyageNbrListHandler::isFetch = " + isFetch);

                } else {
                    isFetch = "";
                    log.info("EdoVesselVoyageNbrListHandler::isFetch = " + isFetch);
                    map.put("isFetch", isFetch);
                    // edovesselvoyagevector.addElement(null);
                    // edovesselvoyagevector.addElement(null);
                    // edovesselvoyagevector.addElement(null);
                }

                map.put("strvarnbr", strvarnbr); // add by thanhnv2
                log.info("=======EdoVesselVoyageNbrListHandler::strvarnbr = " + strvarnbr);

                log.info("EdoVesselVoyageNbrListHandler::vesselName = " + vesselName);
                log.info("EdoVesselVoyageNbrListHandler::vesselName = " + voyageNumber);

                List<String> indicationStatus = edoService.indicationStatus(strvarnbr);
                map.put("indicationStatus", indicationStatus);
                boolean checkVoyNumberStatus = inwardCargoManifestService.chkVslStat(strvarnbr);
                String cVNStatusStr = "FALSE";
                if (checkVoyNumberStatus == true) {
                    cVNStatusStr = "TRUE";
                }
                map.put("cVNStatusStr", cVNStatusStr);

                // Added by LongDh09::End
                map.put("edovesselvoyagevector", edovesselvoyList);
                /*
                 * java.util.Vector edolistvector= new java.util.Vector(); edolistvector=
                 * edoEjb.getEdoList("CUST00001",strvarnbr);
                 * //System.out.println("called edo List ejb method list size is "+edolistvector
                 * .size()); for (int i=0;i<edolistvector.size();i++) { EdoValueObject
                 * edoValueObject = new EdoValueObject();
                 * edoValueObject=(EdoValueObject)edolistvector.elementAt(i);
                 * topsModel.put(edoValueObject); }
                 */

                ////////////// start
                EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
                String changelist = "";
                if (criteria.getPredicates().get("changelist") != null && !(criteria.getPredicates().get("changelist").isEmpty())) {
                    changelist = CommonUtility.deNull(criteria.getPredicates().get("changelist"));
                }

                if ((PageIndex == null) || (PageIndex.equals("null")) || (changelist.equalsIgnoreCase("NEW"))) {
                    // ArrayList bookingList = containerBooking.getBookingList(ucrNo,
                    // vslName,voyOut,slotOpr,cntrOpr,coCd);

                    tableResult = edoService.getEdoList(coCd, strvarnbr, strmodulecd, criteria);
                    size = tableResult.getData().getTotal();
                    int size1 = tableResult.getData().getListData().getTopsModel().size();
                    map.put("total", size);
                    for (int i = 0; i < size1; i++) {
                        edoValueObject = (EdoValueObjectCargo) tableResult.getData().getListData().getTopsModel().get(i);
                        topsModel.put(edoValueObject);
                    }
                }
            }
            // haitth1 added on 14/1/2014
            map.put("arrival", arrival);
            map.put("departure", departure);
            map.put("cod_dttm", cod_dttm);
            map.put("etb_dttm", etb_dttm);
            map.put("terminal", terminal);

            ////////////// end
            map.put("modulecd", strmodulecd);
            map.put("data", topsModel);
            map.put("total", size);


            if (criteria.getPredicates().get("varnbr") == null || (criteria.getPredicates().get("varnbr").isEmpty())) {
                List<String> vector = new ArrayList<String>();
                List<EdoValueObjectCargo> edovesselvoyagevector = new ArrayList<EdoValueObjectCargo>();
                for (int i = 0; i < topsModel.getSize(); i++) {
                    EdoValueObjectCargo edovalueobject = (EdoValueObjectCargo) topsModel.get(i);
                    EdoValueObjectCargo edoobj = new EdoValueObjectCargo();
                    vector.add(edovalueobject.getVarNbr());
                    vector.add(edovalueobject.getVslNbr());
                    vector.add(edovalueobject.getInVoyNbr());
                    vector.add(edovalueobject.getTerminal());
                    edoobj.setVarNbr(edovalueobject.getVarNbr());
                    edoobj.setVslNm(edovalueobject.getVslNbr());
                    edoobj.setInVoyNbr(edovalueobject.getInVoyNbr());
                    edoobj.setTerminal(edovalueobject.getTerminal());
                    edovesselvoyagevector.add(edoobj);
                }
                map.put("edovesselvoyagevector", edovesselvoyagevector);

            } else {
                List<LinkedHashMap<String, Object>> vector1 = new ArrayList<LinkedHashMap<String, Object>>();
                LinkedHashMap<String, Object> item;
                // HaiTTH1 added on 14/1/2014
                int noOfEdoPkg = 0;
                int noOfManPkg = 0;
                int noOfDelPkg = 0;
                int noOfPkgBal = 0;
                int noOfShoLanPkg = 0;
                double totalWg = 0.0;
                double totalVol = 0.0;
                for (int j = 0; j < topsModel.getSize(); j++) {
                    item = new LinkedHashMap<String, Object>();
                    EdoValueObjectCargo edovalueobject1 = (EdoValueObjectCargo) topsModel.get(j);
                    item.put("edoAsnNbr", edovalueobject1.getEdoAsnNbr());
                    item.put("crgDes", edovalueobject1.getCrgDes());
                    item.put("crgTypeNm", edovalueobject1.getCrgTypeNm());
                    item.put("adpNm", edovalueobject1.getAdpNm());
                    item.put("edoNbrPkgs", edovalueobject1.getEdoNbrPkgs());
                    item.put("blNbr", edovalueobject1.getBlNbr());
                    item.put("nbrPkgs", edovalueobject1.getNbrPkgs());
                    item.put("dnNbrPkgs", edovalueobject1.getDnNbrPkgs());
                    item.put("edoStatus", edovalueobject1.getEdoStatus());
                    item.put("nomWeight", edovalueobject1.getNomWeight());
                    item.put("nomVolume", edovalueobject1.getNomVolume());
                    // CR-CIM- 0000108
                    item.put("unstuffInd", edovalueobject1.getUnstuffInd());
                    item.put("crgCategoryCd", edovalueobject1.getCrgCategoryCd());
                    item.put("dgInd", edovalueobject1.getDgInd());
                    item.put("disOprInd", edovalueobject1.getDisOprInd());
                    item.put("whInd", edovalueobject1.getWhInd());
                    item.put("crgCategoryName", edovalueobject1.getCrgCategoryName());
                    // CR-CIM- 0000108
                    // HaiTTH1 added on 10/1/2014
                    item.put("scheme", edovalueobject1.getScheme());
                    int balance = 0;
                    balance = Integer.parseInt(edovalueobject1.getEdoNbrPkgs())
                            - Integer.parseInt(edovalueobject1.getDnNbrPkgs());
                    item.put("balance", balance);
                    item.put("dn_nbr", edovalueobject1.getDn_nbr() == null ? "" : edovalueobject1.getDn_nbr());

                    item.put("crgStatus", edovalueobject1.getCrgStatus());
                    item.put("terminal", edovalueobject1.getTerminal());
                    item.put("gcOperations", edovalueobject1.getGcOperations());
                    item.put("subScheme", edovalueobject1.getSubScheme());

                    vector1.add(item);
                }

                EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
                tableResult = edoService.getEdoListTotal(coCd, strvarnbr, strmodulecd);
                int size1 = tableResult.getData().getListData().getTopsModel().size();
                for (int i = 0; i < size1; i++) {
                    edoValueObject = (EdoValueObjectCargo) tableResult.getData().getListData().getTopsModel().get(i);
                    topsModel1.put(edoValueObject);
                }

                for (int j = 0; j < topsModel1.getSize(); j++) {
                    EdoValueObjectCargo edovalueobject1 = (EdoValueObjectCargo) topsModel1.get(j);
                    int balance = 0;
                    balance = Integer.parseInt(edovalueobject1.getEdoNbrPkgs())
                            - Integer.parseInt(edovalueobject1.getDnNbrPkgs());
                    noOfEdoPkg = noOfEdoPkg + Integer.parseInt(edovalueobject1.getEdoNbrPkgs());
                    noOfDelPkg = noOfDelPkg + Integer.parseInt(edovalueobject1.getDnNbrPkgs());
                    noOfPkgBal = noOfPkgBal + balance;
                    noOfManPkg = noOfManPkg + Integer.parseInt(edovalueobject1.getNbrPkgs());
                    totalWg = totalWg + Double.parseDouble(edovalueobject1.getNomWeight());
                    totalVol = totalVol + Double.parseDouble(edovalueobject1.getNomVolume());
                    if (!StringUtils.isEmpty(edovalueobject1.getShort_landed_pkgs())) {
                        noOfShoLanPkg = noOfShoLanPkg + Integer.parseInt(edovalueobject1.getShort_landed_pkgs());
                    }
                }

                map.put("edolistvector", vector1);

                map.put("noOfEdoPkg", noOfEdoPkg);
                map.put("noOfManPkg", noOfManPkg);
                map.put("noOfDelPkg", noOfDelPkg);
                map.put("noOfPkgBal", noOfPkgBal);
                map.put("noOfShoLanPkg", noOfShoLanPkg);
                map.put("totalWg", totalWg);
                map.put("totalVol", totalVol);
            }
        } catch (BusinessException e) {
            log.info("Exception edoVesselVoyageNbrList : ", e);
            errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
            if (errorMessage == null) {
                errorMessage = e.getMessage();
            }
        } catch (Exception e) {
            log.info("Exception edoVesselVoyageNbrList : ", e);
            errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
        } finally {
            if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
                map.put("errorMessage", errorMessage);
                result = new Result();
                result.setError(errorMessage);
                result.setErrors(map);
                result.setSuccess(false);
            } else {
                result = new Result();
                result.setData(map);
                result.setSuccess(true);
            }
            log.info(" End edoVesselVoyageNbrList result:" + result.toString());
        }
        return ResponseEntityUtil.success(result.toString());
    }

    // end Cargo Edo List

    // start Region EdoDetail
    //delegate.helper.gbms.ops.dnua.dn - dnEdoDetailHandler;
    @PostMapping(value = "/dnEdoDetail")
    public ResponseEntity<?> dnEdoDetail(HttpServletRequest request) {
        Result result = new Result();
        Map<String, Object> map = new HashMap<String, Object>();
        errorMessage = null;
        try {
            Criteria criteria = CommonUtil.getCriteria(request);
            log.info("Start dnEdoDetail criteria :" + criteria.toString());

            String s5 = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
            String s7 = "";
            String searchCrg = "";
            searchCrg = CommonUtility.deNull(criteria.getPredicates().get("searchcrg"));
            String s11 = "";
            s11 = CommonUtility.deNull(criteria.getPredicates().get("tesn_nbr")); // stuff sequence no for stuffing
            s7 = CommonUtility.deNull(criteria.getPredicates().get("flag"));
            String s12 = CommonUtility.deNull(criteria.getPredicates().get("release"));

            List<EdoValueObjectOps> vector = new ArrayList<EdoValueObjectOps>();
            List<EdoValueObjectOps> vector1 = new ArrayList<EdoValueObjectOps>();
            List<EdoValueObjectOps> subAdpVector = new ArrayList<EdoValueObjectOps>();
            // Added by SONLT---------------------------------------------
            boolean chk = dnEdoDetailService.checkESNCntr(CommonUtility.deNull(criteria.getPredicates().get("edo")));
            if (chk) {
                map.put("esncntr", "YES");
            } else {
                map.put("esncntr", "NO");
            }
            // End--------------------------------------------------------
            String userId = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
            map.put("userId", userId);
            if (!s7.equalsIgnoreCase("") && s7.equals("Y")) {
                String cntrNbr = CommonUtility.deNull(criteria.getPredicates().get("cntrNbr"));
                String cntrSeq = "";
                if (!"".equals(CommonUtility.deNull(cntrNbr))) {
                    cntrSeq = dnEdoDetailService.getCntrSeq(cntrNbr);
                }

                // MCConsulting: if the 1st DN is cancelled then update subsequent active DN nbr
                // in the billable events table
                String transactionType = CommonUtility.deNull(criteria.getPredicates().get("transType"));
                boolean canCancelDn = true;
                if ((!transactionType.isEmpty() && transactionType.equals("L"))
                        || (!searchCrg.isEmpty() && searchCrg.equals("SL"))) {
                    // system should not allow to cancel DN after the next day of DN creation

                    String dnNbr = CommonUtility.deNull(criteria.getPredicates().get("dnnbr"));
                    canCancelDn = dnEdoDetailService.checkCancelDN(dnNbr);
                    if (canCancelDn) {
                        log.info("**Updating the first DN in billable events with subsequent Active DN");
                        dnEdoDetailService.checkAndUpdateFirstDN(
                                CommonUtility.deNull(criteria.getPredicates().get("edo")), dnNbr);

                    } else {
                        errorMessage = ConstantUtil.ErrorMsg_Cannot_Cancel_DN;
                    }
                }

                // MCConsulting - check if the DN is allowed to cancel first before calling
                // cancelBillableCharges function
                if (canCancelDn) {
                    if (dnEdoDetailService
                            .cancelBillableCharges(CommonUtility.deNull(criteria.getPredicates().get("dnnbr")), "DN")) {
                        // Begin TungVH
                        String dnFirst = "";
                        if (chk && !"".equals(CommonUtility.deNull(cntrSeq))) {
                            dnFirst = dnEdoDetailService.getDnCntrFirst(cntrSeq, cntrNbr);
                        }
                        // End TungVH

                        // Amend by Zhenguo Deng(harbor) 03/08/2011 : START
                        map.put("searchCrg", searchCrg);
                        log.info("Cancel DN details :searchCrg :" + searchCrg);
                        if (searchCrg.equals("SL")) {
                            dnEdoDetailService.cancelShutoutDN(
                                    CommonUtility.deNull(criteria.getPredicates().get("edo")),
                                    CommonUtility.deNull(criteria.getPredicates().get("dnnbr")), s5);
                        } else {
                            dnEdoDetailService.cancelDN(CommonUtility.deNull(criteria.getPredicates().get("edo")),
                                    CommonUtility.deNull(criteria.getPredicates().get("dnnbr")), s5,
                                    CommonUtility.deNull(criteria.getPredicates().get("transType")), searchCrg, s11);
                            if (searchCrg.trim().equals("T")) {
                                log.info("Cancel DN details :searchCrg :" + searchCrg);
                                // CommonUtility.deNull(criteria.getPredicates().get("tranQty"))
                                int nbrPkg = Integer
                                        .parseInt(CommonUtility.deNull(criteria.getPredicates().get("tranQty")));
                                String transDttm = CommonUtility.deNull(criteria.getPredicates().get("transDttm"));
                                String transType = dnEdoDetailService.checkTransType(s11);
                                String dpNm = CommonUtility.deNull(criteria.getPredicates().get("dpNm"));
                                String dpIcNbr = CommonUtility.deNull(criteria.getPredicates().get("dpIcNbr"));
                                log.info("Cancel DN details :nbrPkg :" + nbrPkg);
                                log.info("Cancel DN details :transType :" + transType);
                                log.info("Cancel DN details :dpNm :" + dpNm);
                                log.info("Cancel DN details :dpIcNbr :" + dpIcNbr);
                                String uaNbr = dnEdoDetailService.getUaNbr(s11, nbrPkg, transDttm, dpNm, dpIcNbr);
                                log.info("Cancel DN details :uaNbr :" + uaNbr);
                                if (uaNbr != null && !uaNbr.equals("")) {
                                    if (dnEdoDetailService.cancelBillableCharges(uaNbr, "UA")) {
                                        dnEdoDetailService.cancelUA(uaNbr, s11, transType, s5,
                                                Integer.toString(nbrPkg));

                                    }
                                }
                            }
                            // Added by Babatunde on Dec., 2013 : End
                            // VietNguyen added to implement logic cancel DN -> should be check Stuff cntr
                            if (chk && !"".equals(CommonUtility.deNull(cntrSeq))) {
                                // check dn balance
                                boolean countBalance = dnEdoDetailService.countDNBalance(cntrNbr);
                                log.info("Cancel DN details :and do check update stuff cntr : ?? :" + countBalance);
                                if (!countBalance) {
                                    dnEdoDetailService.updateCntrStatus(cntrSeq, s5);
                                }
                                // do update cntr status
                            }

                        }
                        // Amend by Zhenguo Deng(harbor) 03/08/2011 : END
                        // Added by SONLT
                        if (chk && !"".equals(CommonUtility.deNull(cntrSeq))) {
                            // Begin TungVH
                            String dnNbr = CommonUtility.deNull(criteria.getPredicates().get("dnnbr"));
                            int remDn = 0;
                            remDn = dnEdoDetailService.checkFirstDN(dnNbr, cntrNbr);
                            if (remDn <= 0) {
                                // need to implement inside other jb call there
                                // String newCatCd = dnEdoDetailService.getNewCatCd(cntrSeq);
                                String newCatCd = "";
                                dnEdoDetailService.changeStatusCntr(cntrSeq, s5, newCatCd);
                            }
                            if (dnNbr.equals(dnFirst)) {
                                // insert USTF into cntr_txn
                                dnEdoDetailService.cancel1stDn(cntrSeq, cntrNbr, s5);
                                // Timestamp dttmSecond = null;
                                // dttmSecond = dnremote.getDnSecond(cntrSeq,  cntrNbr, s5);
                                // if (dttmSecond == null) {
                                // have not any DN
                                // dnremote.changeStatusCntr(cntrSeq, s5);
                                // } else {
                                // At least one DN
                                // dnremote.changeFirstDN(cntrSeq,  cntrNbr, s5, dttmSecond);
                                // }
                            }
                            // End TungVH
                            long declrWt = Long
                                    .parseLong(CommonUtility.deNull(criteria.getPredicates().get("declrWt")));
                            int declrPkgs = Integer
                                    .parseInt(CommonUtility.deNull(criteria.getPredicates().get("declrPkgs")));
                            int tranQty = Integer
                                    .parseInt(CommonUtility.deNull(criteria.getPredicates().get("tranQty")));
                            long weight = (tranQty * declrWt) / declrPkgs;
                            dnEdoDetailService.updateWeight(cntrSeq, weight, s5, "SUB");
                        }
                        // END SONLT
                        // Start added for SMART CR by FPT on 24-Jan-2014
                        log.info("searchCrg=" + searchCrg);
//						String refType = "";
                        String refNbr = "";
                        boolean callSmartInterface = false;
                        if ("LT".equals(searchCrg) || "T".equals(searchCrg)) {
                            if ("T".equals(searchCrg) && StringUtils.isNotBlank(s11)) {
                                refNbr = s11;

                                String chkTesnJpPsa_nbr = dnEdoDetailService.chktesnJpPsa_nbr(s11);

                                if ("Y".equals(chkTesnJpPsa_nbr)) {
                                    // Is this TESN_JP_PSA
//									refType = "TESNJP";
                                    callSmartInterface = true;
                                }
                            } else {
//								refType = "EDO";
                                refNbr = CommonUtility.deNull(criteria.getPredicates().get("edo"));
                                callSmartInterface = true;
                            }
                        }
                        if (callSmartInterface) {
                            String dnNbr = CommonUtility.deNull(criteria.getPredicates().get("dnnbr"));
                            log.info(
                                    "Before SMART interface calling for restore the previous occupancy for the affected number of packages: DN number="
                                            + dnNbr);
                            try {

                                double declrWt = Double
                                        .parseDouble(CommonUtility.deNull(criteria.getPredicates().get("declrWt")));
                                int declrPkgs = Integer
                                        .parseInt(CommonUtility.deNull(criteria.getPredicates().get("declrPkgs")));
                                int tranQty = Integer
                                        .parseInt(CommonUtility.deNull(criteria.getPredicates().get("tranQty")));
                                double weight = (double) (tranQty * declrWt) / declrPkgs;

                                BigDecimal tonnage = BigDecimal.valueOf(weight / 1000);
                                String vvCd = CommonUtility.deNull(criteria.getPredicates().get("vvCd"));

                                String serverIp = null;
                                String serverNm = null;
                                InetAddress jpOnlineAddress = InetAddress.getLocalHost();
                                if (jpOnlineAddress != null) {
                                    serverIp = jpOnlineAddress.getHostAddress();
                                    serverNm = jpOnlineAddress.getHostName();
                                }

                                log.info("DN Cancel serverIp    = " + serverIp);
                                log.info("DN Cancel serverName = " + serverNm);
                                log.info("DN Cancel number of package = " + tranQty);
                                log.info("DN Cancel tonnage = " + tonnage.doubleValue());
                                log.info("DN Cancel vvCd = " + vvCd);
                                log.info("DN Cancel RefNbr = " + refNbr);
                                // need to implement
                                /*
                                 * SmartInterfaceInputVO inputObj = new SmartInterfaceInputVO();
                                 * inputObj.setUserId(s5);
                                 * inputObj.setOccSrcCd(SmartInterfaceConstants.SOURCE_JP);
                                 * inputObj.setServerIp(serverIp); inputObj.setServerNm(serverNm);
                                 * inputObj.setClassNm(this.getClass().getName());
                                 * inputObj.setClassDesc("DN cancel handler class"); inputObj.setRefNbr(refNbr);
                                 * inputObj.setRefType(refType); inputObj.setNbrPkgs(tranQty);
                                 * inputObj.setTonnage(tonnage); inputObj.setVvCd(vvCd);
                                 * inputObj.setTransNbr(dnNbr);
                                 *
                                 * SmartInterfaceUtil.getInstance().restoreStorageOccupancy(inputObj);
                                 */
                            } catch (Exception ex) {
                                log.info("Call SMART Interface restoreStorageOccupancy exception:DN number=" + dnNbr);
                                log.info("Exception: " + ex.getMessage());
                            }
                            log.info("After SMART interface calling");
                        }
                        // End edded for SMART CR by FPT on 24-Jan-2014
                    } else {
                        errorMessage = "Bills raised cannot Cancel DN";
                    }
                } // MCConsulting - End if canCancelDN
                // Amend by Zhenguo Deng(harbor) 03/08/2011 : START
                if (searchCrg.equals("SL")) {

                    // vector =
                    // edoEjb.getShutoutEdoDetail(CommonUtility.deNull(criteria.getPredicates().get("edo")));
                    vector1 = dnEdoDetailService
                            .fetchShutoutDNList(CommonUtility.deNull(criteria.getPredicates().get("edo")));
                } else {
                    vector = dnEdoDetailService
                            .fetchEdoDetails(CommonUtility.deNull(criteria.getPredicates().get("edo")), searchCrg, s11);
                    String listAllDn = CommonUtility.deNull(criteria.getPredicates().get("listAllDn"));
                    if ("TRUE".equalsIgnoreCase(listAllDn)) {
                        vector1 = dnEdoDetailService
                                .fetchDNList(CommonUtility.deNull(criteria.getPredicates().get("edo")), "ALL", s11);
                    } else
                        vector1 = dnEdoDetailService
                                .fetchDNList(CommonUtility.deNull(criteria.getPredicates().get("edo")), searchCrg, s11);
                }
                // Amend by Zhenguo Deng(harbor) 03/08/2011 : END
            } else {
                vector = dnEdoDetailService.fetchEdoDetails(CommonUtility.deNull(criteria.getPredicates().get("edo")),
                        searchCrg, s11);
                String listAllDn = CommonUtility.deNull(criteria.getPredicates().get("listAllDn"));
                if ("TRUE".equalsIgnoreCase(listAllDn)) {
                    vector1 = dnEdoDetailService.fetchDNList(CommonUtility.deNull(criteria.getPredicates().get("edo")),
                            "ALL", s11);
                } else
                    vector1 = dnEdoDetailService.fetchDNList(CommonUtility.deNull(criteria.getPredicates().get("edo")),
                            searchCrg, s11);

                // httpservletrequest.setAttribute("stuffSeqNbr",s11);

            }
            String s8 = dnEdoDetailService.chktesnJpJp_nbr(s11);
            String s9 = dnEdoDetailService.chktesnJpPsa_nbr(s11);
            // added by Vinayak on 05 Feb 2004
            boolean checkEdoStuff = false;
            if (searchCrg.trim().equals("T")) {
                checkEdoStuff = dnEdoDetailService
                        .chkEDOStuffing(CommonUtility.deNull(criteria.getPredicates().get("edo"))); // vinayak added 16
                // jan 2004
            }
            // end added by Vinayak on 05 Feb 2004

            // added by Tatang on 15 Apr 2008 - begin

            // Added by Vietnd02 -- to check size =0
            if (vector.size() == 0)
                errorMessage = "M4500";
            // vietnd02::end
            // CashSalesValueObject csvo = cs.getCashSales(vector1);
            List<CashSalesValueObject> csList;
            if (vector1.size() > 0) {
                csList = dnEdoDetailService.getCashSales(vector1);
            } else {
                csList = new ArrayList<CashSalesValueObject>();
            }
            map.put("csList", csList);
            log.info(vector1.size() + ", " + csList.size());
            // LogManager.instance.logInfo(CommonUtility.deNull(criteria.getPredicates().get("dnnbr")
            // + " - " + csvo.getCashReceiptNbr() + " - " + csvo.getReceiptDttm());
            // added by Tatang on 15 Apr 2008 - end

            // BEGIN FPT Amend to check for TESN JP - JP (existing tesnjpjp check for wrong
            // ESN number, maybe used for different purpose
            String edoTesnJpJp = "N";
            if (dnEdoDetailService.chktesnJpJp(CommonUtility.deNull(criteria.getPredicates().get("edo")))) {
                edoTesnJpJp = "Y";
            }
            log.info("edoTesnJpJp :" + edoTesnJpJp);
            map.put("edoTesnJpJp", edoTesnJpJp);
            // END FPT Amend to check for TESN JP - JP (existing tesnjpjp check for wrong
            // EDO number, maybe used for different purpose

            log.info("checkEdoStuff :" + checkEdoStuff);
            map.put("checkEdoStuff", "" + checkEdoStuff);
            map.put("tesnjpjp", "" + s8);
            map.put("tesnjppsa", "" + s9);
            map.put("edovect", vector);
            map.put("dnList", vector1);

            subAdpVector = dnEdoDetailService
                    .fetchSubAdpDetails(CommonUtility.deNull(criteria.getPredicates().get("edo")));
            map.put("subAdpVector", subAdpVector);

            int spencialPkgs = dnEdoDetailService
                    .getSpencialPackage(CommonUtility.deNull(criteria.getPredicates().get("edo")));
            map.put("spencialPkgs", spencialPkgs + "");
            // Amend by Zhenguo Deng(harbor) 03/08/2011 : START
            if (!s12.equals("") && s12.equals("release")) {
                // nextScreen(httpservletrequest, "ReleaseEdo");
            } else if (searchCrg.equals("SL")) {
                map.put("edo", CommonUtility.deNull(criteria.getPredicates().get("edo")));
                map.put("edoValueObject", vector.get(0));
                map.put("frommode", "SEARCH");
                map.put("funParam", "THREE");
                map.put("status", "L");
                map.put("dnVector", vector1);
                map.put("fromDn", "Y");
                map.put("showTop", "Y");
                // nextScreen(httpservletrequest, "shutoutEdoSearch");
            } else {
                // nextScreen(httpservletrequest, "dnEdoDetail");
            }
            // Amend by Zhenguo Deng(harbor) 03/08/2011 : END

        } catch (BusinessException e) {
            log.info("Exception dnEdoDetail : ", e);
            errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
            if (errorMessage == null) {
                errorMessage = e.getMessage();
            }
        } catch (Exception e) {
            log.info("Exception dnEdoDetail : ", e);
            errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
        } finally {
            if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
                map.put("errorMessage", errorMessage);
                result = new Result();
                result.setError(errorMessage);
                result.setErrors(map);
                result.setSuccess(false);
            } else {
                result.setSuccess(true);
                result.setData(map);
            }
            log.info("END: dnEdoDetail result" + result.toString());
        }

        return ResponseEntityUtil.success(result.toString());
    }

    // start region dnDetail not required
    @PostMapping(value = "/dnDetail")
    public ResponseEntity<?> dnDetail(HttpServletRequest request) {
        errorMessage = null;
        Result result = new Result();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Criteria criteria = CommonUtil.getCriteria(request);
            log.info("START: dnDetail :: criteria: " + criteria.toString());
            String s3 = CommonUtility.deNull(criteria.getPredicates().get("flag"));
            String s4 = "";
            s4 = CommonUtility.deNull(criteria.getPredicates().get("searchcrg"));
            String s5 = "";
            s5 = CommonUtility.deNull(criteria.getPredicates().get("tesn_nbr"));
            List<EdoValueObjectOps> vector = null;
            List<EdoValueObjectOps> vector1 = null;
            //
            boolean select = dnEdoDetailService
                    .chktesnJpJp(CommonUtility.deNull(criteria.getPredicates().get("edo")).toString());
            map.put("select", "" + select);
            // Added by VietNguyen DPE on 20/03/12014 for edo remark link start
            map.put("dnList", CommonUtility.deNull(criteria.getPredicates().get("dnList")));
            // Added by VietNguyen DPE on 20/03/12014 for edo remark link end

            String dd = CommonUtility.deNull(criteria.getPredicates().get("dnNbr"));
            log.info("dnNbr: " + dd);
            //
            if (s3.equals("Y")) {
                // amend by Zhenguo Deng(harbor) 02/08/2011 : START
                if (s4.equals("SL")) {
                    vector = dnEdoDetailService.fetchShutoutDNDetail(
                            CommonUtility.deNull(criteria.getPredicates().get("edo")),
                            CommonUtility.deNull(criteria.getPredicates().get("dnNbr")));
                    vector1 = dnEdoDetailService
                            .getVechDetails(CommonUtility.deNull(criteria.getPredicates().get("dnNbr")));
                } else {
                    vector = dnEdoDetailService.fetchDNDetail(CommonUtility.deNull(criteria.getPredicates().get("edo")),
                            CommonUtility.deNull(criteria.getPredicates().get("dnNbr")),
                            CommonUtility.deNull(criteria.getPredicates().get("status")), s4, s5);
                    vector1 = dnEdoDetailService
                            .getVechDetails(CommonUtility.deNull(criteria.getPredicates().get("dnNbr")));
                }
                // amend by Zhenguo Deng(harbor) 02/08/2011 : END
                // Check EdoESN whether it associate with cntr
                if (dnEdoDetailService.checkESNCntr(CommonUtility.deNull(criteria.getPredicates().get("edo")))) {
                    // get cntr from DN_DETAILS table
                    String cntrNo = dnEdoDetailService
                            .getCntrNo(CommonUtility.deNull(criteria.getPredicates().get("dnNbr")));
                    map.put("cntrNo", cntrNo);
                }
            } else {
                // amend by Zhenguo Deng(harbor) 18/07/2011 :START
                if (!(CommonUtility.deNull(criteria.getPredicates().get("addDnParam"))).equals("")) {
                    map.put("addDnParam", "Y");
                    vector = dnEdoDetailService.fetchShutoutDNCreateDetail(
                            CommonUtility.deNull(criteria.getPredicates().get("edo")),
                            CommonUtility.deNull(criteria.getPredicates().get("status")), s4, s5);
                } else {
                    vector = dnEdoDetailService.fetchDNCreateDetail(
                            CommonUtility.deNull(criteria.getPredicates().get("edo")),
                            CommonUtility.deNull(criteria.getPredicates().get("status")), s4, s5);
                }
                // amend by Zhenguo Deng(harbor) 18/07/2011 :END
            }
            // print need to impplement
            // added by Tatang on 17 Apr 2008 - begin

            CashSalesValueObject csvo = dnEdoDetailService.getCashSales(CommonUtility.deNull(criteria.getPredicates().get("dnNbr")));
            map.put("cashsale", csvo);

            String machineId = dnEdoDetailService.getMachineID(csvo.getCash_receipt_nbr());
            map.put("machineId", machineId);
            String paymentMode = dnEdoDetailService.getCashSalesPaymentCode(csvo.getCsType());
            map.put("paymentMode", paymentMode);
            String nets_refId = dnEdoDetailService.getNETSRefID(csvo.getCash_receipt_nbr());
            map.put("nets_refId", nets_refId);
            // added by Tatang on 17 Apr 2008 - end
            map.put("dnDetail", vector);
            map.put("vechDetail", vector1);

        } catch (BusinessException e) {
            log.info("Exception dnDetail : ", e);
            errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
            if (errorMessage == null) {
                errorMessage = e.getMessage();
            }
        } catch (Exception e) {
            log.info("Exception dnDetail : ", e);
            errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
        } finally {
            if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
                map.put("errorMessage", errorMessage);
                result = new Result();
                result.setError(errorMessage);
                result.setErrors(map);
                result.setSuccess(false);
            } else {
                result.setSuccess(true);
                result.setData(map);
            }

            log.info("END: dnDetail result:" + result.toString());
        }
        return ResponseEntityUtil.success(result.toString());

    }
    // end region

    @PostMapping(value = "/edoAddView")
    public ResponseEntity<?> edoAddView(HttpServletRequest request) {
        return this.edoAdd(request);
    }

    // asn link click
    // start region EdoAdd
    @PostMapping(value = "/edoAdd")
    public ResponseEntity<?> edoAdd(HttpServletRequest request) {
        errorMessage = null;
        TopsModel topsModel = null;
        Result result = new Result();
        Map<String, Object> map = new HashMap<String, Object>();
        Criteria criteria = CommonUtil.getCriteria(request);

        String pVesselName1 = CommonUtility.deNull(criteria.getPredicates().get("vesselName1"));
        String pVesselName2 = CommonUtility.deNull(criteria.getPredicates().get("vesselName2"));
        String pVoyageNumber1 = CommonUtility.deNull(criteria.getPredicates().get("voyageNumber1"));
        String pVoyageNumber2 = CommonUtility.deNull(criteria.getPredicates().get("voyageNumber2"));
        String pfetchView = CommonUtility.deNull(criteria.getPredicates().get("fetchView"));
        String pisDisabled = CommonUtility.deNull(criteria.getPredicates().get("isDisabled"));
        String pvslNm = CommonUtility.deNull(criteria.getPredicates().get("vslNm"));
        String pvslVoy = CommonUtility.deNull(criteria.getPredicates().get("vslVoy"));
        String gross_wt = CommonUtility.deNull(criteria.getPredicates().get("gross_wt"));
        String gross_vol = CommonUtility.deNull(criteria.getPredicates().get("gross_vol"));
        String packages = CommonUtility.deNull(criteria.getPredicates().get("package"));
        String pmodulecd = CommonUtility.deNull(criteria.getPredicates().get("modulecd"));
        String companyCode = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
        companyCode = companyCode.equalsIgnoreCase("") ? "JP" : companyCode;
        String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
        userId = userId.equalsIgnoreCase("") ? "SYSTEM" : userId;
        String pEdoasnnbr = CommonUtility.deNull(criteria.getPredicates().get("edoasnnbr"));
        String strVarNo = CommonUtility.deNull(criteria.getPredicates().get("varNo_in_List"));
        String pvarnbr = CommonUtility.deNull(criteria.getPredicates().get("varnbr"));
        strVarNo = strVarNo.isEmpty() ? null : CommonUtility.deNull(criteria.getPredicates().get("varNo_in_List"));

        try {
            log.info("Start edoAdd criteria :" + criteria.toString());
            String varName = "";
            String varNo = "";
            String fetchView = "";
            // remove condition if not null as the pVesselName2 already have denull method - NS June 2023
            if (!"".equalsIgnoreCase(pVesselName2))
                varName = pVesselName2;
            if (!"".equalsIgnoreCase(pVoyageNumber2))
                varNo = pVoyageNumber2;
            if (!"".equalsIgnoreCase(pfetchView))
                fetchView = pfetchView;

            // HaiTTH1 added on 15/1/2014
            String isDisableJPBillingNo = pisDisabled;
            map.put("isDisable", isDisableJPBillingNo);

            // fixed pvslNm condition - NS June 2023
            if (varName == null && !"".equalsIgnoreCase(pvslNm) && pvslNm != null) {
                varName = (String) pvslNm;
                varNo = (String) pvslVoy;
            }

            log.info("1.EdoAddHandler::isFetch = " + fetchView);
            log.info("2.EdoAddHandler::vesselName = " + varName);
            log.info("3.EdoAddHandler::vesselName = " + varNo);
            map.put("fetchView", fetchView);
            map.put("varName", varName);
            map.put("varNo", varNo);
            // end::vietnd02
            String s = "ONE";
            String s1 = "Add";

            // added by vani start -- 3rd Oct,03
            // TopsModel topsmodel = new TopsModel();
            String moduleCd = "EDO";
            // Added by thanhnv2::Start
            map.put("gross_wt", gross_wt);
            map.put("gross_vol", gross_vol);
            map.put("package", packages);
            // Added by thanhnv2::End

            if (!"".equalsIgnoreCase(pmodulecd) || pmodulecd != null)
                moduleCd = pmodulecd;

            String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
            // Changed by LongDh09::Start
            String strVarNbr = "";
            String isFetch = CommonUtility.deNull(criteria.getPredicates().get("isFetch"));
            log.info("EdoAddHandler::isFetch = " + isFetch);
            // if(!"FETCH".equals(isFetch)) {
            strVarNbr = criteria.getPredicates().get("varnbr");
            // }
            if ((strVarNbr == null || "".equalsIgnoreCase(strVarNbr) || "--SELECT--".equalsIgnoreCase(strVarNbr))
                    && criteria.getPredicates().get("varNo_in_List") != null)
                strVarNbr = (String) criteria.getPredicates().get("varNo_in_List");

            String vesselName = "";
            String voyageNumber = "";
            // remove condition if not null as the pVesselName1 already have denull method - NS June 2023
            if (!"".equalsIgnoreCase(pVesselName1))
                vesselName = pVesselName1.toUpperCase();
            if (!"".equalsIgnoreCase(pVoyageNumber1))
                voyageNumber = pVoyageNumber1.toUpperCase();

            log.info("EdoAddHandler::vesselName = " + vesselName);
            log.info("EdoAddHandler::voyageNumber = " + voyageNumber);

            if (varName != null && !"".equalsIgnoreCase(varName)) {
                vesselName = varName;
                voyageNumber = varNo;
            }

            log.info("EdoAddHandler::strVarNbr = " + strVarNbr);
            log.info("EdoAddHandler::moduleCd = " + moduleCd);
            log.info("EdoAddHandler::vesselName = " + vesselName);
            log.info("EdoAddHandler::voyageNumber = " + voyageNumber);
            String strVslNM = "";
            String strVslVoy = "";
            String vvStatusInd = "";

            // check Sripriya 27 June 2012 to avoid starVarNbr value as '--select--' to
            // store in db
            if (strVarNbr != null && !strVarNbr.equalsIgnoreCase("")) {
                if (strVarNbr.equalsIgnoreCase("--Select--")) {
                    errorMessage = ConstantUtil.ErrorMsg_Invalid_Vessel_Call;
                    throw new BusinessException(errorMessage);
                }
            }
            List<EdoValueObjectCargo> vesselVoyageNbrList1 = dnEdoDetailService.getVesselVoyageNbrList(coCd, moduleCd);
            List<EdoValueObjectCargo> vesselVoyageNbrList2 = dnEdoDetailService.getVesselVoyageNbrList(coCd, moduleCd,
                    vesselName, voyageNumber);
            for (int i = 0; i < vesselVoyageNbrList1.size(); i++) {
                EdoValueObjectCargo edovalueobject = new EdoValueObjectCargo();
                edovalueobject = (EdoValueObjectCargo) vesselVoyageNbrList1.get(i);

                if (edovalueobject.getVarNbr().equals(strVarNbr) && vesselVoyageNbrList2.size() == 0) {
                    strVslNM = edovalueobject.getVslNbr();
                    strVslVoy = edovalueobject.getInVoyNbr();
                }
            }

            if (vesselVoyageNbrList2.size() != 0) {
                for (int i = 0; i < vesselVoyageNbrList2.size(); i++) {
                    EdoValueObjectCargo edovalueobject = new EdoValueObjectCargo();
                    edovalueobject = (EdoValueObjectCargo) vesselVoyageNbrList2.get(i);

                    strVarNbr = edovalueobject.getVarNbr();
                    strVslNM = edovalueobject.getVslNbr();
                    strVslVoy = edovalueobject.getInVoyNbr();

                }
            }
            // Changed by LongDh09::End
            map.put("vslNm", strVslNM);
            map.put("vslVoy", strVslVoy);
            // added by vani end -- 3rd Oct,03
            log.info("========strVslNM/strVslVoy: " + strVslNM + "/" + strVslVoy);

            if (criteria.getPredicates().get("strmode") != null)
                s = criteria.getPredicates().get("strmode");
            if (criteria.getPredicates().get("funmode") != null)
                s1 = criteria.getPredicates().get("funmode");
            if (s1.equalsIgnoreCase("Add"))
                map.put("funmode", "Add");
            if (s1.equalsIgnoreCase("View"))
                map.put("funmode", "View");
            log.info("-------strmode is :" + s);
            if (s.equalsIgnoreCase("SEARCHSCREEN"))
                map.put("strmode", "SEARCHSCREEN");
            if (s.equalsIgnoreCase("OPENWHIND")) {
                String s2 = "";
                if (pEdoasnnbr != null)
                    s2 = pEdoasnnbr;
                List<String> vector = edoService.getWHIndicator(s2);
                String s11 = (String) vector.get(0);
                String s15 = (String) vector.get(1);
                String s21 = (String) vector.get(2);
                String s25 = (String) vector.get(3);
                map.put("edoasnnbr", s2);
                map.put("whind", s11);
                map.put("whappnbr", s15);
                map.put("remarks", s21);
                map.put("nodays", s25);
                map.put("strmode", "OPENWHIND");
            }
            if (s.equalsIgnoreCase("UPDATEWHIND")) {
                String s3 = "";
                String s7 = "";
                String s12 = "";
                String s16 = "";
                String s22 = "";
                if (pEdoasnnbr != null)
                    s3 = pEdoasnnbr;

                String whind = CommonUtility.deNull(criteria.getPredicates().get("whind"));
                String whappnbr = CommonUtility.deNull(criteria.getPredicates().get("whappnbr"));
                String remarks = CommonUtility.deNull(criteria.getPredicates().get("remarks"));
                String nodays = CommonUtility.deNull(criteria.getPredicates().get("nodays"));

                if (!whind.isEmpty())
                    s7 = whind;
                if (!whappnbr.isEmpty())
                    s12 = whappnbr;
                if (!remarks.isEmpty())
                    s16 = remarks;
                if (!nodays.isEmpty())
                    s22 = nodays;

                String s35 = userId;
                edoService.updateWHIndicator(s3, s7, s12, s16, s22, s35);
                s = "SEARCH";
            }
            if (s.equalsIgnoreCase("SEARCH")) {
                // String s17 = accessuservalueobject.getCompanyCode();
                String s17 = companyCode;
                String s23 = "";
                if (pEdoasnnbr != null)
                    s23 = pEdoasnnbr;
                String s26 = edoService.getSearchDetails(s17, s23);
                log.info("strsearchstring" + s26);
                String s28 = "";
                String s31 = "";
                StringTokenizer stringtokenizer = new StringTokenizer(s26, ";");
                s28 = stringtokenizer.nextToken();
                s31 = stringtokenizer.nextToken();
                map.put("varnbr1", s28);

                // Vector vector1 = edoejbremote.getVesselVoyageNbrList(coCd,
                // moduleCd);
                // Changed by LongDh09::Start
                if (!"FETCH".equals(isFetch)) {
                    for (int i = 0; i < vesselVoyageNbrList1.size(); i++) {
                        EdoValueObjectCargo edovalueobject = new EdoValueObjectCargo();
                        edovalueobject = (EdoValueObjectCargo) vesselVoyageNbrList1.get(i);

                        if (edovalueobject.getVarNbr().equals(s28)) {
                            strVslNM = edovalueobject.getVslNbr();
                            strVslVoy = edovalueobject.getInVoyNbr();
                        }
                    }
                } else {
                    for (int i = 0; i < vesselVoyageNbrList2.size(); i++) {
                        EdoValueObjectCargo edovalueobject = new EdoValueObjectCargo();
                        edovalueobject = (EdoValueObjectCargo) vesselVoyageNbrList2.get(i);
                        strVslNM = edovalueobject.getVslNbr();
                        strVslVoy = edovalueobject.getInVoyNbr();

                    }
                }
                // Changed by LongDh09::End

                map.put("vslNm", strVslNM);
                map.put("vslVoy", strVslVoy);

                map.put("vslnmvoynbr1", s31);
                map.put("strmode", "SEARCH");
                s = "VIEWEDO";
            }
            if (s.equalsIgnoreCase("ONE")) {
                // START - #39699 : CR TO VALIDATE CLOSE LCT - NS JUNE 2024
                if (inwardCargoManifestService.checkCloseLCT(CommonUtility.deNull(criteria.getPredicates().get("vslnmvoynbr")))) {
                    throw new BusinessException(ConstantUtil.vesselCloseLCT);
                }
                // END - #39699 : CR TO VALIDATE CLOSE LCT - NS JUNE 2024
                getBLDisplay(criteria, map, "ADD");
                map.put("strmode", "ONE");
                List<AdpValueObject> adpList = new ArrayList<AdpValueObject>();
                String ccode = companyCode;
                map.put("ccode", ccode);

                String taUenNo = CommonUtility.deNull(criteria.getPredicates().get("taUenNo"));
                if (StringUtils.isEmpty(taUenNo)) {
                    taUenNo = "";
                }
                // AdpValueObject adpVO = edoejbremote.getAdpDetails(taUenNo);
                AdpValueObject adpVO = edoService.getTaEndorserNmByUENNo(taUenNo);
                map.put("taUaeNo", taUenNo);
                map.put("taCCode", adpVO.getAdpCustCd() == null ? "" : adpVO.getAdpCustCd());
                map.put("taName", adpVO.getAdpNm() == null ? "" : adpVO.getAdpNm());
                // map.put("taCCode", taNm);
                // map.put("taName", taNm);
                // End thanhpt6
                // added on 13/01/2014 by VietNguyen-----: START
                String adp = CommonUtility.deNull(criteria.getPredicates().get("adp"));
                AdpValueObject adpValueObject = new AdpValueObject();
                if (adp != null && !adp.equals("") && adp.equals("adp")) {
                    String adpIc = CommonUtility.deNull(criteria.getPredicates().get("adpIcParam"));
                    String[] listAdpIc = new String[0];
                    if (criteria.getPredicates().get("adpIc") != null) {
                        listAdpIc = (String[]) (CommonUtil.getRequiredStringParameters(request, "adpIc"));
                    }
                    String[] listAdpNm = new String[0];
                    if (criteria.getPredicates().get("adpNm") != null) {
                        listAdpNm = (String[]) (CommonUtil.getRequiredStringParameters(request, "adpNm"));
                    }
                    String[] listAdpCd = new String[0];
                    if (CommonUtil.getRequiredStringParameters(request, "adpCd") != null) {
                        listAdpCd = (String[]) (CommonUtil.getRequiredStringParameters(request, "adpCd"));
                    }
                    String[] listAdpPkgs = new String[0];
                    if (criteria.getPredicates().get("adpPkgs") != null) {
                        listAdpPkgs = (String[]) (CommonUtil.getRequiredStringParameters(request, "adpPkgs"));
                    }
                    String[] listAdpCntNbr = new String[0];
                    if (criteria.getPredicates().get("adpCntNbr") != null) {
                        listAdpCntNbr = (String[]) (CommonUtil.getRequiredStringParameters(request, "adpCntNbr"));
                    }

                    if (listAdpIc != null) {
                        for (int i = 0; i < listAdpIc.length; i++) {
                            adpValueObject = new AdpValueObject();
                            adpValueObject.setAdpIcTdbcrNbr(listAdpIc[i]);
                            adpValueObject.setAdpNm(listAdpNm[i]);
                            adpValueObject.setAdpCustCd(listAdpCd[i]);
                            adpValueObject.setAdpNbrPkgs(listAdpPkgs[i]);
                            adpValueObject.setAdpContact(listAdpCntNbr[i]);
                            adpList.add(adpValueObject);
                        }
                    }
                    String adpDelete = criteria.getPredicates().get("adpDelete");
                    String ta = criteria.getPredicates().get("taFlag");
                    if (!"ta".equalsIgnoreCase(ta)) {
                        if (!"delete".equalsIgnoreCase(adpDelete)) {
                            String adpRow = criteria.getPredicates().get("adpRow");
                            int row = 0;
                            if (adpRow != null) {
                                row = Integer.parseInt(adpRow);
                            }
                            adpValueObject = edoService.getAdpDetails(adpIc);
                            String adpPkgs = criteria.getPredicates().get("adpPkgsParam");
                            adpValueObject.setAdpNbrPkgs(adpPkgs);
                            if (adpList.size() < row) {
                                for (int i = 0; i < row - 1; i++) {
                                    adpList.add(new AdpValueObject());
                                }
                                adpList.add(adpValueObject);
                            } else {
                                adpList.set(row, adpValueObject);
                            }
                        } else {
                            map.put("adpDelete", adpDelete);
                            String doubleWt = criteria.getPredicates().get("oldWt");
                            map.put("_doubleWt", doubleWt);
                            String doubleVol = criteria.getPredicates().get("oldVol");
                            map.put("_doubleVol", doubleVol);
                            String nbrpkgs = criteria.getPredicates().get("nbrpkgs");
                            map.put("_nbrpkgs", nbrpkgs);
                        }
                    }
                    map.put("adp", "adp");
                } else {
                }
                map.put("adpList", adpList);


            }
            if (s.equalsIgnoreCase("TWO")) {
                getBLDisplay(criteria, map, "ADD");
                String s4 = "";
                String s8 = "";
                String s13 = "";
                String s18 = "";
                String s24 = "";
                String s27 = "";
                if (criteria.getPredicates().get("adpnbr") != null)
                    s4 = criteria.getPredicates().get("adpnbr");
                if (criteria.getPredicates().get("adpnm") != null)
                    s8 = criteria.getPredicates().get("adpnm");
                if (criteria.getPredicates().get("crgagtnbr") != null)
                    s13 = criteria.getPredicates().get("crgagtnbr");
                if (criteria.getPredicates().get("crgagtnm") != null)
                    s18 = criteria.getPredicates().get("crgagtnm");
                if (criteria.getPredicates().get("agtattnbr") != null)
                    s24 = criteria.getPredicates().get("agtattnbr");
                if (criteria.getPredicates().get("agtattnm") != null)
                    s27 = criteria.getPredicates().get("agtattnm");
                String s29 = "false";

                // Begin THANHPT6 JCMS 06/01/2016
                String taUenNo = criteria.getPredicates().get("taUenNo");
                String taCCode = criteria.getPredicates().get("taCCode");
                String taNmByJP = criteria.getPredicates().get("taEndorser");
                // End THANHPT6 JCMS 06/01/2016
                // VietNguyen (FPT) Document Process Enhancement 06-Jan-2014:
                // START

                List<AdpValueObject> adpList = new ArrayList<AdpValueObject>();
                AdpValueObject adpValueObject = null;

                //Add by NS 11112022 : Change request array parameter value.
                int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
                List<String> listAdpIc = new ArrayList<String>();
                String adpIcNo;
                List<String> listAdpNm = new ArrayList<String>();
                String adpNmNo;
                List<String> listAdpCd = new ArrayList<String>();
                String adpCdNo;
                List<String> listAdpPkgs = new ArrayList<String>();
                String adpPkgsNo;
                List<String> listAdpCntNbr = new ArrayList<String>();
                String adpCntNbrNo;

                if (sizeSelected != 0) {
                    for (int i = 0; i < sizeSelected; i++) {
                        adpIcNo = (String) criteria.getPredicates().get("adpIc" + i);
                        listAdpIc.add(adpIcNo);
                        adpNmNo = (String) criteria.getPredicates().get("adpNm" + i);
                        listAdpNm.add(adpNmNo);
                        adpCdNo = (String) criteria.getPredicates().get("adpCd" + i);
                        listAdpCd.add(adpCdNo);
                        adpPkgsNo = (String) criteria.getPredicates().get("adpPkgs" + i);
                        listAdpPkgs.add(adpPkgsNo);
                        adpCntNbrNo = (String) criteria.getPredicates().get("adpCntNbr" + i);
                        listAdpCntNbr.add(adpCntNbrNo);
                    }
                }

                if (listAdpIc != null) {
                    for (int i = 0; i < listAdpIc.size(); i++) {
                        adpValueObject = new AdpValueObject();
                        adpValueObject.setAdpIcTdbcrNbr(listAdpIc.get(i));
                        adpValueObject.setAdpNm(listAdpNm.get(i));
                        adpValueObject.setAdpCustCd(listAdpCd.get(i));
                        adpValueObject.setAdpNbrPkgs(listAdpPkgs.get(i));
                        adpValueObject.setAdpContact(listAdpCntNbr.get(i));
                        adpList.add(adpValueObject);

                    }
                }

                map.put("adpList", adpList);
                if (adpList != null && adpList.size() > 0) {
                    s8 = adpList.get(0).getAdpNm();
                    s4 = adpList.get(0).getAdpIcTdbcrNbr();
                }
                // VietNguyen (FPT) Document Process Enhancement 06-Jan-2014:
                // START

                if (s29.equalsIgnoreCase("false")) {
                    String s32 = edoService.getCompanyName(s4);
                    if (s8.equalsIgnoreCase("") & s32.equalsIgnoreCase("")) {
                        errorMessage = ConstantUtil.ErrorMsg_Edo_001;
                        s29 = "true";
                    } else if (s32.equalsIgnoreCase("")) {
                        map.put("adpnmstatus", "NEW");
                        map.put("adpnm", s8);
                    } else {
                        map.put("adpnmstatus", "OLD");
                        map.put("adpnm", s32);
                    }
                }
                if (s29.equalsIgnoreCase("false") && !s13.equalsIgnoreCase("")) {
                    String s33 = edoService.getCompanyName(s13);
                    if (s18.equalsIgnoreCase("") & s33.equalsIgnoreCase("")) {
                        map.put("errorMessage", ConstantUtil.ErrorMsg_Edo_008);
                        s29 = "true";
                    } else if (s33.equalsIgnoreCase("")) {
                        map.put("crgagtnmstatus", "NEW");
                        map.put("crgagtnm", s18);
                    } else {
                        map.put("crgagtnmstatus", "OLD");
                        map.put("crgagtnm", s33);
                    }
                }
                if (s29.equalsIgnoreCase("false") && !s24.equalsIgnoreCase("")) {
                    String s34 = edoService.getCompanyName(s24);
                    if (s27.equalsIgnoreCase("") & s34.equalsIgnoreCase("")) {
                        errorMessage = ConstantUtil.ErrorMsg_Edo_002;
                        s29 = "true";
                    } else if (s34.equalsIgnoreCase("")) {
                        map.put("agtattnmstatus", "NEW");
                        map.put("agtattnm", s27);
                    } else {
                        map.put("agtattnmstatus", "OLD");
                        map.put("agtattnm", s34);
                    }
                }

                // Start CR FTZ HSCODE - NS JULY 2024
                String mftSeqNbr = criteria.getPredicates().get("blnbr");
                int hsCodeSize = Integer.valueOf(CommonUtility.deNull(criteria.getPredicates().get("hsCodeSize")));

                List<HsCodeDetails> multiHsCodeList = new ArrayList<HsCodeDetails>();
                HsCodeDetails hsCodeDetails = new HsCodeDetails();
                if (hsCodeSize > 0) {
                    for (int x = 0; x < hsCodeSize; x++) {
                        hsCodeDetails = new HsCodeDetails();
                        hsCodeDetails.setHsCode(CommonUtility.deNull(criteria.getPredicates().get("HsCodeArr" + x)));
                        hsCodeDetails.setNbrPkgs(CommonUtility.deNull(criteria.getPredicates().get("NoOfPKgsArr" + x)));
                        hsCodeDetails.setGrossWt(CommonUtility.deNull(criteria.getPredicates().get("gwtArr" + x)));
                        if ((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + x))).indexOf("-") == -1) {
                            hsCodeDetails.setHsSubCodeFr((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + x))));
                            hsCodeDetails.setHsSubCodeTo((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + x))));
                        } else {
                            hsCodeDetails.setHsSubCodeFr((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + x))).split("-")[0]);
                            hsCodeDetails.setHsSubCodeTo((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + x))).split("-")[1]);
                        }
                        hsCodeDetails.setGrossVol(CommonUtility.deNull(criteria.getPredicates().get("mSmtArr" + x)));
                        hsCodeDetails.setHscodeSeqNbr(CommonUtility.deNull(criteria.getPredicates().get("hscodeSeqNbr" + x)));
                        hsCodeDetails.setCustomHsCode(CommonUtility.deNull(criteria.getPredicates().get("customHsCodeArr" + x)));
                        multiHsCodeList.add(hsCodeDetails);
                    }
                }


                // check if already exist mft seq nbr in the edohscode
                boolean ifHsCodeExist = edoService.ifHsCodeExist(mftSeqNbr);

                // check if multiple hscode for the mftSeqNbr
                boolean isMultipleHs = edoService.isMultipleHs(mftSeqNbr);

                // if not exist - give warning message it cannot be separate anymore.
                if (!ifHsCodeExist && isMultipleHs) {
                    boolean isShowRemainder = edoService.isShowRemainder(criteria.getPredicates().get("newnbrpkgs"), multiHsCodeList);
                    log.info("isShowRemainder : " + isShowRemainder);
                    if (isShowRemainder) {
                        map.put("msgHsCode", ConstantUtil.edo_hscode_msg);
                    }
                }
                // if exist mftseqnbr, check if the hscode combined same as added
                // if yes, proceed, if not, throw error, hscode selected not same as previous
                else {
                    boolean correctMultiHsCode = edoService.correctMultiHsCode(mftSeqNbr, multiHsCodeList);
                    if (!correctMultiHsCode) {
                        throw new BusinessException(ConstantUtil.edo_hscode_error_combination);
                    }
                }

                // End CR FTZ HSCODE - NS JULY 2024

                if (s29.equalsIgnoreCase("true")) {
                    map.put("strmode", "TWO");
                } else {
                    List<EdoJpBilling> arraylist = new ArrayList<EdoJpBilling>();
                    if (criteria.getPredicates().get("adpnbr") != null)
                        s4 = criteria.getPredicates().get("adpnbr");
                    map.put("adpnbr", s4);
                    String s36 = "";
                    if (pvarnbr != null)
                        s36 = pvarnbr;
                    String s37 = companyCode;
                    arraylist = edoService.getEdoJpBillingNbr(s4, s37, s36);
                    // Add by Ding Xijia(harbortek) 09-Feb-2011 : START
                    String scheme = edoService.getVesselScheme(s36);
                    // Add by Ding Xijia(harbortek) 09-Feb-2011 : END
                    map.put("jpbnbrarraylist", arraylist);
                    // Add by Ding Xijia(harbortek) 09-Feb-2011 : START
                    map.put("scheme", scheme);
                    // Add by Ding Xijia(harbortek) 09-Feb-2011 : END
                    map.put("strmode", "NEXTTWO");
                    // Begin THANHPT6 JCMS 06/01/2016
                    map.put("taUenNo", taUenNo);
                    map.put("taCCode", taCCode);
                    map.put("taEndorser", taNmByJP);
                    // End ThanhPt6
                }
            }
            if (s.equalsIgnoreCase("THREE")) {
                String s5 = "false";
                String s9 = "-1";
                String s14 = "0";
                // Added by thanhnv2::Start
                String strVol = "0";
                String strWt = "0";
                String inputVol = "0";
                String inputWt = "0";
                double intVol, intWt;
                EdoValueObjectCargo edoValueObject = new EdoValueObjectCargo();
                // Added by thanhnv2::End
                if (criteria.getPredicates().get("newnbrpkgs") != null) {
                    s9 = criteria.getPredicates().get("newnbrpkgs");
                } else {
                    // errorMessage(request, "Enter Valid No Of
                    // Packages");
                    errorMessage = ConstantUtil.ErrorMsg_Edo_003;
                    s5 = "true";
                    getBLDisplay(criteria, map, "ADD");
                    map.put("strmode", "ONE");
                }
                // Added by thanhnv2::Start
                if (criteria.getPredicates().get("newWt") != null) {
                    inputWt = criteria.getPredicates().get("newWt");
                } else {
                    errorMessage = ConstantUtil.ErrorMsg_Edo_004;
                    s5 = "true";
                    getBLDisplay(criteria, map, "ADD");
                    map.put("strmode", "ONE");
                }

                if (criteria.getPredicates().get("newVol") != null) {
                    inputVol = criteria.getPredicates().get("newVol");
                } else {
                    // errorMessage(request, "Enter Valid No Of
                    // Weight");
                    errorMessage = ConstantUtil.ErrorMsg_Edo_005;
                    s5 = "true";
                    getBLDisplay(criteria, map, "ADD");
                    map.put("strmode", "ONE");
                }
                // Added by thanhnv2::End

                if (criteria.getPredicates().get("blnbr") != null) {
                    String s20 = criteria.getPredicates().get("blnbr");
                    s14 = edoService.getEdoNbrPkgs(s20);
                    // Added by thanhnv2::Start
                    log.info("inf1=========" + s20);
                    edoValueObject = edoService.getUsedWeightVolume(s20);
                    strVol = edoValueObject.getNomVolume();
                    strWt = edoValueObject.getNomWeight();
                    // Added by thanhnv2::Start
                } else {

                    errorMessage = ConstantUtil.ErrorMsg_Edo_003;
                    s5 = "true";
                    getBLDisplay(criteria, map, "ADD");
                    map.put("strmode", "ONE");
                }
                int i = Integer.parseInt(s9);
                int j = Integer.parseInt(s14);
                // Added by thanhnv2::Start
                double newVol = Double.parseDouble(inputVol);
                double newWt = Double.parseDouble(inputWt);
                intVol = Double.parseDouble(strVol);
                intWt = Double.parseDouble(strWt);
                log.info("inf2=========" + inputVol + " - " + strVol + " - " + inputWt + " " + strWt);
                if (newVol > intVol) {
                    errorMessage = ConstantUtil.ErrorMsg_Edo_006 + intVol;
                    s5 = "true";
                    getBLDisplay(criteria, map, "ADD");
                    map.put("strmode", "ONE");
                }

                if (newWt > intWt) {
                    errorMessage = ConstantUtil.ErrorMsg_Edo_007 + intWt;
                    s5 = "true";
                    getBLDisplay(criteria, map, "ADD");
                    map.put("strmode", "ONE");
                }
                // Added by thanhnv2::End

                if (j < i) {
                    errorMessage = ConstantUtil.ErrorMsg_Edo_003;
                    s5 = "true";
                    getBLDisplay(criteria, map, "ADD");
                    map.put("strmode", "ONE");
                }
                if (s5.equalsIgnoreCase("false")) {
                    String s30 = "false";
                    // VietNguyen (FPT) Document Process Enhancement
                    // 06-Jan-2014: START
                    List<AdpValueObject> adpList = new ArrayList<AdpValueObject>();
                    AdpValueObject adpValueObject = null;

                    //Add by NS 11112022 : Change request array parameter value.
                    int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
                    List<String> listAdpIc = new ArrayList<String>();
                    String adpIcNo;
                    List<String> listAdpNm = new ArrayList<String>();
                    String adpNmNo;
                    List<String> listAdpCd = new ArrayList<String>();
                    String adpCdNo;
                    List<String> listAdpPkgs = new ArrayList<String>();
                    String adpPkgsNo;
                    List<String> listAdpCntNbr = new ArrayList<String>();
                    String adpCntNbrNo;

                    if (sizeSelected != 0) {
                        for (i = 0; i < sizeSelected; i++) {
                            adpIcNo = (String) criteria.getPredicates().get("adpIc" + i);
                            listAdpIc.add(adpIcNo);
                            adpNmNo = (String) criteria.getPredicates().get("adpNm" + i);
                            listAdpNm.add(adpNmNo);
                            adpCdNo = (String) criteria.getPredicates().get("adpCd" + i);
                            listAdpCd.add(adpCdNo);
                            adpPkgsNo = (String) criteria.getPredicates().get("adpPkgs" + i);
                            listAdpPkgs.add(adpPkgsNo);
                            adpCntNbrNo = (String) criteria.getPredicates().get("adpCntNbr" + i);
                            listAdpCntNbr.add(adpCntNbrNo);
                        }
                    }

                    if (listAdpIc != null) {
                        for (i = 0; i < listAdpIc.size(); i++) {
                            adpValueObject = new AdpValueObject();
                            adpValueObject.setAdpIcTdbcrNbr(listAdpIc.get(i));
                            adpValueObject.setAdpNm(listAdpNm.get(i));
                            adpValueObject.setAdpCustCd(listAdpCd.get(i));
                            adpValueObject.setAdpNbrPkgs(listAdpPkgs.get(i));
                            adpValueObject.setAdpContact(listAdpCntNbr.get(i));
                            adpList.add(adpValueObject);
                        }
                    }

                    // Start CR FTZ HSCODE - NS JULY 2024
                    int hsCodeSize = Integer.valueOf(CommonUtility.deNull(criteria.getPredicates().get("hsCodeSize")));

                    List<HsCodeDetails> multiHsCodeList = new ArrayList<HsCodeDetails>();
                    HsCodeDetails hsCodeDetails = new HsCodeDetails();
                    if (hsCodeSize > 0) {
                        for (int x = 0; x < hsCodeSize; x++) {
                            hsCodeDetails = new HsCodeDetails();
                            hsCodeDetails.setHsCode(CommonUtility.deNull(criteria.getPredicates().get("HsCodeArr" + x)));
                            hsCodeDetails.setNbrPkgs(CommonUtility.deNull(criteria.getPredicates().get("NoOfPKgsArr" + x)));
                            hsCodeDetails.setGrossWt(CommonUtility.deNull(criteria.getPredicates().get("gwtArr" + x)));
                            if ((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + x))).indexOf("-") == -1) {
                                hsCodeDetails.setHsSubCodeFr((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + x))));
                                hsCodeDetails.setHsSubCodeTo((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + x))));
                            } else {
                                hsCodeDetails.setHsSubCodeFr((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + x))).split("-")[0]);
                                hsCodeDetails.setHsSubCodeTo((CommonUtility.deNull(criteria.getPredicates().get("hsSubCodeArr" + x))).split("-")[1]);
                            }
                            hsCodeDetails.setGrossVol(CommonUtility.deNull(criteria.getPredicates().get("mSmtArr" + x)));
                            if (criteria.getPredicates().get("hscodeSeqNbr" + x) == null) {
                                String mftHsCodeSeqNbr = edoService.checkIfExistMultiHsMft(s1);
                                hsCodeDetails.setHscodeSeqNbr(mftHsCodeSeqNbr);
                            } else {
                                hsCodeDetails.setHscodeSeqNbr(CommonUtility.deNull(criteria.getPredicates().get("hscodeSeqNbr" + x)));
                            }
                            hsCodeDetails.setCustomHsCode(CommonUtility.deNull(criteria.getPredicates().get("customHsCodeArr" + x)));
                            multiHsCodeList.add(hsCodeDetails);
                        }
                    }

                    // End CR FTZ HSCODE - NS JULY 2024

                    // VietNguyen (FPT) Document Process Enhancement
                    // 06-Jan-2014: START
                    s30 = insertEdoDetail(criteria, adpList, map, multiHsCodeList);
                    if (s30.equalsIgnoreCase("false")) {
                        getBLDisplay(criteria, map, "ADD");
                        map.put("strmode", "ONE");
                    } else {
                        viewEdoDetails(criteria, s30, map);
                        map.put("strmsg", "Successfully added");
                        map.put("strmode", "VIEWEDO");
                    }
                }
            }
            if (s.equalsIgnoreCase("VIEWEDO")) {
                map.put("jpuser", companyCode.equalsIgnoreCase("JP") ? true : false);
                // Added by thanhnv2::Start
                String vesselCode = (String) criteria.getPredicates().get("vesselCode");
                String vslstat = "";
                boolean bvslstat = dnEdoDetailService.chkVslStat(vesselCode);
                if (bvslstat) {
                    vslstat = "closed";
                } else {
                    vslstat = "notclosed";
                }
                log.info("---------vesselCode=" + vesselCode + "-------------vslstat=" + vslstat);
                map.put("vslstat", vslstat);
                // Added by thanhnv2::End
                String s6 = "", strAmtDel = "";
                vvStatusInd = dnEdoDetailService.getVslStatus(vesselCode);
                map.put("vvStatus", vvStatusInd);
                if (criteria.getPredicates().get("edoasnnbr") != null)
                    s6 = criteria.getPredicates().get("edoasnnbr");
                // converted view adpList edo detail
                topsModel = new TopsModel();
                List<EdoValueObjectCargo> viewEdoDetails = dnEdoDetailService.viewEdoDetails(s6);
                EdoValueObjectCargo edovalueobject = new EdoValueObjectCargo();
                edovalueobject = (EdoValueObjectCargo) viewEdoDetails.get(0);
                List<AdpValueObject> adpList = dnEdoDetailService.getAdpList(s6);
                map.put("adpList", adpList);
                // end view adpList edo detail
                topsModel.put(edovalueobject);
                map.put("model", topsModel);
                map.put("strmode", "VIEWEDO");
                String s10 = "";
                if (criteria.getPredicates().get("modulecd") != null) {
                    s10 = (String) criteria.getPredicates().get("modulecd");
                    map.put("modulecd", s10);
                }

                if (criteria.getPredicates().get("amtdelivered") != null) {
                    strAmtDel = (String) criteria.getPredicates().get("amtdelivered");
                } else {
                    strAmtDel = "0";
                }
                log.info("strAmtDel :" + strAmtDel);
                map.put("amtdelivered", strAmtDel);
                addParametersFromServlet(criteria, topsModel, map);

            }

        } catch (BusinessException e) {
            log.info("Exception edoAdd : ", e);
            errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
            if (errorMessage == null) {
                errorMessage = e.getMessage();
            }
        } catch (Exception e) {
            log.info("Exception edoAdd : ", e);
            errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
        } finally {
            if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
                map.put("errorMessage", errorMessage);
                result = new Result();
                result.setError(errorMessage);
                result.setErrors(map);
                result.setSuccess(false);
            } else {
                result = new Result();
                result.setData(map);
                result.setSuccess(true);
            }
            log.info("End edoAdd result:" + result.toString());
        }
        return ResponseEntityUtil.success(result.toString());
    }

    public String insertEdoDetail(Criteria criteria, List<AdpValueObject> adpList, Map<String, Object> map, List<HsCodeDetails> multiHsCodeList)
            throws BusinessException {
        String s = "false";
        try {
            log.info("Start insertEdoDetail criteria :" + criteria.toString() + "adpList" + adpList + "map" + map);
            String s1 = "";
            String s2 = "";
            String newWt = "";// Added by thanhnv2
            String newVol = "";// Added by thanhnv2
            String s5 = "";
            String s6 = "";
            String s7 = "";
            String s8 = "";
            String s9 = "";
            String s10 = "";
            String s11 = "";
            String s12 = "";
            String s13 = "";
            String s14 = "O";
            String s15 = "";
            String s16 = "";
            String s17 = "A";
            String s18 = "";
            String s19 = "";
            String s20 = "";
            String s21 = "";
            String s22 = "";
            String s23 = "";
            String s24 = "";
            String s25 = "";
            String s26 = "";
            String s27 = "";
            // String stfInd = ""; removed by vinayak 19/12/2003
            String s28 = criteria.getPredicates().get("companyCode");
            s28 = s28.equalsIgnoreCase("") ? "JP" : s28;
            String s29 = criteria.getPredicates().get("userAccount");
            s29 = s29.equalsIgnoreCase("") ? "SYSTEM" : s29;
            s18 = s28;
            s20 = s29;
            // Added by thanhnv2::Start
            if (criteria.getPredicates().get("newWt") != null)
                newWt = criteria.getPredicates().get("newWt");
            if (criteria.getPredicates().get("newVol") != null)
                newVol = criteria.getPredicates().get("newVol");
            // Added by thanhnv2::End
            if (criteria.getPredicates().get("blnbr") != null)
                s1 = criteria.getPredicates().get("blnbr");

            log.info("=======Demo_vietnd VARNBR = "
                    + (CommonUtility.deNull(criteria.getPredicates().get("varnbr"))).toUpperCase().trim());
            if (criteria.getPredicates().get("varnbr") != null)
                s2 = criteria.getPredicates().get("varnbr");
            if ((s2 == null || "".equalsIgnoreCase(s2) || "--SELECT--".equalsIgnoreCase(s2))
                    && criteria.getPredicates().get("varNo_in_List") != null)
                s2 = criteria.getPredicates().get("varNo_in_List");

            //	request.getSession().removeAttribute("varNo_in_List");

            if (criteria.getPredicates().get("adpnbr") != null)
                s5 = criteria.getPredicates().get("adpnbr");
            if (criteria.getPredicates().get("adpnmstatus") != null)
                s15 = criteria.getPredicates().get("adpnmstatus");
            if (s15.equalsIgnoreCase("NEW"))
                s7 = s5;
            if (s15.equalsIgnoreCase("OLD")) {
                s7 = s5;
                s5 = edoService.getCustomerNbr(s5);
                s6 = s5;
            }
            if (criteria.getPredicates().get("adpnm") != null)
                s8 = criteria.getPredicates().get("adpnm");
            if (criteria.getPredicates().get("newnbrpkgs") != null)
                s9 = criteria.getPredicates().get("newnbrpkgs");
            if (criteria.getPredicates().get("crgagtnm") != null)
                s11 = criteria.getPredicates().get("crgagtnm");
            if (criteria.getPredicates().get("agtattnm") != null)
                s13 = criteria.getPredicates().get("agtattnm");
            if (criteria.getPredicates().get("deliveryto") != null)
                s14 = criteria.getPredicates().get("deliveryto");
            if (criteria.getPredicates().get("vslnmvoynbr") != null) {
            }
            if (criteria.getPredicates().get("jpbnbr") != null)
                s16 = criteria.getPredicates().get("jpbnbr");
            if (s16.equalsIgnoreCase("cash"))
                s17 = "C";
            else
                s17 = "A";
            if (criteria.getPredicates().get("edostatus") != null)
                s19 = criteria.getPredicates().get("edostatus");
            if (criteria.getPredicates().get("crgagtnbr") != null)
                s23 = criteria.getPredicates().get("crgagtnbr");
            if (criteria.getPredicates().get("crgagtnmstatus") != null)
                s21 = criteria.getPredicates().get("crgagtnmstatus");
            if (s21.equalsIgnoreCase("NEW"))
                s22 = s23;
            if (s21.equalsIgnoreCase("OLD")) {
                s22 = s23;
                s23 = edoService.getCustomerNbr(s23);
                s10 = s23;
            }
            if (criteria.getPredicates().get("agtattnbr") != null)
                s26 = criteria.getPredicates().get("agtattnbr");
            if (criteria.getPredicates().get("agtattnmstatus") != null)
                s24 = criteria.getPredicates().get("agtattnmstatus");
            if (s24.equalsIgnoreCase("NEW"))
                s25 = s26;
            if (s24.equalsIgnoreCase("OLD")) {
                s25 = s26;
                s26 = edoService.getCustomerNbr(s26);
                s12 = s26;
            }
            if (criteria.getPredicates().get("distype") != null)
                s27 = criteria.getPredicates().get("distype");

            // Begin ThanhPT6 JCMS 06/01/2016
            String taUenNo = criteria.getPredicates().get("taUenNo") == null ? ""
                    : criteria.getPredicates().get("taUenNo");
            String taCCode = criteria.getPredicates().get("taCCode") == null ? ""
                    : criteria.getPredicates().get("taCCode");
            String taNmByJP = criteria.getPredicates().get("taEndorser") == null ? ""
                    : criteria.getPredicates().get("taEndorser");
            // End ThanhPT6
            /*
             * if(RequestUtils.getStringParameter(request,"stuffInd") != null)
             * stfInd = RequestUtils.getStringParameter(request,"stuffInd");
             * removed by vinayak 19/12/2003
             */
            log.info("=======Demo_varNBr = " + s2);
            // Add parameter for TA Endorser by ThanhPT6


            s = edoService.insertEdoDetailsForDPE(s1, s2, s6, s8, s7, s10, s11, s12, s13, s9, s14, s16, s17, s19, s20,
                    s22, s25, s18, s27, newWt, newVol, adpList, taUenNo, taCCode, taNmByJP, multiHsCodeList);
        } catch (BusinessException businessexception) {
            log.info("Exception insertEdoDetail : ", businessexception);
            throw new BusinessException(businessexception.getMessage());
        } catch (Exception exception) {
            log.info("Exception insertEdoDetail : ", exception);
            String s4 = s;
            return s4;
        } finally {
            log.info("End insertEdoDetail , s: " + s);
        }
        return s;
    }

    private void addParametersFromServlet(Criteria criteria, TopsModel topsmodel,
                                          Map<String, Object> map) throws BusinessException {
        try {
            log.info("Start addParametersFromServlet criteria :" + criteria.toString() + "topsmodel" + topsmodel + "map" + map);
            String s1 = "ONE";
            if (criteria.getPredicates().get("strmode") != null)
                s1 = criteria.getPredicates().get("strmode");
            if (s1.equalsIgnoreCase("VIEWEDO")) {
                viewEdoDetails(criteria, topsmodel, map);
            }
        } catch (Exception exception) {
            log.info("Exception addParametersFromServlet : ", exception);
            throw new BusinessException("M4201");
        } finally {
            log.info("End addParametersFromServlet ");
        }
    }

    private void viewEdoDetails(Criteria criteria, TopsModel topsmodel, Map<String, Object> map) throws BusinessException {
        try {
            log.info("Start viewEdoDetails criteria :" + criteria.toString() + "topsmodel" + topsmodel + "map" + map);
            List<EdoValueObjectCargo> vector = new ArrayList<EdoValueObjectCargo>();
            EdoValueObjectCargo edovalueobject = (EdoValueObjectCargo) topsmodel.get(0);
            vector.add(edovalueobject);
            map.put("edoviewlistvector", vector);
            // 19.10.2009 start decentralization, tienlc. GB CR
            List<EdoValueObjectCargo> edoviewlistvector = (ArrayList<EdoValueObjectCargo>) vector;
            String asn = (String) edoviewlistvector.get(0).getEdoAsnNbr();
            String coCd = criteria.getPredicates().get("companyCode");
            boolean userDBVessel = edoService.getUserVesselEDO(coCd, asn);
            String userIdDBVessel = "FALSE";
            if (userDBVessel == true) {
                userIdDBVessel = "TRUE";
            }
            map.put("USERIDEDO", userIdDBVessel);
            // HaiTTH1 added on 20/1/2014
            List<AdpValueObject> adpList = edoService.getAdpList(edovalueobject.getEdoAsnNbr());
            map.put("adpList", adpList);

            // START FTZ - NS JULY 2024
            List<HsCodeDetails> hsCodeDetailList = new ArrayList<HsCodeDetails>();
            hsCodeDetailList = edoService.getEdoHsCodeDetails(edovalueobject.getEdoAsnNbr());
            map.put("hsCodeDetailsList", hsCodeDetailList);
            // END FTZ
        } catch (Exception exception) {
            log.info("Exception viewEdoDetails : ", exception);
            throw new BusinessException("M4201");
        } finally {
            log.info("End viewEdoDetails ");
        }
    }


    @PostMapping(value = "/edoAmend")
    public ResponseEntity<?> edoAmend(HttpServletRequest request) {
        Result result = new Result();
        errorMessage = null;
        Map<String, Object> map = new HashMap<String, Object>();
        int maxiAdp = AdpValueObject.MAX_ADP_TRUCKER;
        List<AdpValueObject> adpList = new ArrayList<AdpValueObject>();
        String s = "ONE";
        try {
            Criteria criteria = CommonUtil.getCriteria(request);
            log.info("** edoAmend Start criteria : " + criteria.toString());
            String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
            String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
            String strmode = CommonUtility.deNull(criteria.getPredicates().get("strmode"));
            strmode = strmode.isEmpty() ? "ONE" : strmode;
            if (strmode != null || !strmode.isEmpty())
                s = strmode;
            // HaiTTH1 added on 20/1/2014

            // HaiTTH1 added on 15/1/2014
            String isDisableJPBillingNo = CommonUtility.deNull(criteria.getPredicates().get("isDisabled"));
            map.put("isDisable", isDisableJPBillingNo);

            if (s.equalsIgnoreCase("ONE")) {
                map.put("jpuser", coCd.equalsIgnoreCase("JP") ? true : false);
                map.put("ccode", coCd);
                //Begin Thanhpt6, JCMS, 06/01/16

                String taUenNo = (String) CommonUtility.deNull(criteria.getPredicates().get("taUenNo"));
                if (StringUtils.isEmpty(taUenNo)) {
                    taUenNo = "";
                }
                String taEndorser = (String) CommonUtility.deNull(criteria.getPredicates().get("taEndorser"));
                //AdpValueObject adpVO = edoAmendService.getAdpDetails(taUenNo);
                AdpValueObject adpVO = edoService.getTaEndorserNmByUENNo(taUenNo);
                map.put("taUaeNo", taUenNo);
                map.put("taCCode", adpVO.getAdpCustCd() == null ? "" : adpVO.getAdpCustCd());
                map.put("taName", adpVO.getAdpNm() == null ? "" : adpVO.getAdpNm());
                //httpservletrequest.setAttribute("taCCode", taNm);
                //httpservletrequest.setAttribute("taName", taNm);
                map.put("taEndorser", taEndorser);
                //End thanhpt6
                String strAmtDel = "";
                getBLDisplay(criteria, map, "AMEND");
                if (!(CommonUtility.deNull(criteria.getPredicates().get("amtdelivered")).equals(""))) {
                    strAmtDel = CommonUtility.deNull(criteria.getPredicates().get("amtdelivered"));
                } else {
                    strAmtDel = "0";
                }
                map.put("strmode", "ONE");
                map.put("amtdelivered", strAmtDel);

                // Added by thanhnv2::Start
                String nom_vol = CommonUtility.deNull(criteria.getPredicates().get("nom_vol"));
                String nom_wt = CommonUtility.deNull(criteria.getPredicates().get("nom_wt"));
                map.put("nom_vol", nom_vol);
                map.put("nom_wt", nom_wt);
                String gross_wt = CommonUtility.deNull(criteria.getPredicates().get("gross_wt"));
                map.put("gross_wt", gross_wt);
                String gross_vol = CommonUtility.deNull(criteria.getPredicates().get("gross_vol"));
                map.put("gross_vol", gross_vol);
                String packages = CommonUtility.deNull(criteria.getPredicates().get("package"));
                map.put("package", packages);
                // Added by thanhnv2::End
                String intdnnbrpkgs = "";
                if (!(CommonUtility.deNull(criteria.getPredicates().get("intdnnbrpkgs")).isEmpty()))
                    intdnnbrpkgs = CommonUtility.deNull(criteria.getPredicates().get("intdnnbrpkgs"));
                map.put("intdnnbrpkgs", intdnnbrpkgs);

                // added on 13/01/2014 by VietNguyen-----: START
                String adp = CommonUtility.deNull(criteria.getPredicates().get("adp"));
                AdpValueObject adpValueObject = new AdpValueObject();
                if (adp != null && !adp.equals("") && adp.equals("adp")) {
                    String adpIc = CommonUtility.deNull(criteria.getPredicates().get("adpIcParam"));
//					String[] listAdpIc = new String[0];
//					if (CommonUtil.getRequiredStringParameters(request,"adpIc")!= null) {
//						listAdpIc = (String[])(CommonUtil.getRequiredStringParameters(request,"adpIc"));
//					}
//					String[] listAdpNm = new String[0];
//					if (CommonUtil.getRequiredStringParameters(request,"adpNm") != null) {
//						listAdpNm = (String[]) (CommonUtil.getRequiredStringParameters(request,"adpNm"));
//					}
//					String[] listAdpCd = new String[0];
//					if (CommonUtil.getRequiredStringParameters(request,"adpCd")!= null) {
//						listAdpCd = (String[]) (CommonUtil.getRequiredStringParameters(request,"adpCd"));
//					}
//					String[] listAdpPkgs = new String[0];
//					if (CommonUtil.getRequiredStringParameters(request,"adpPkgs") != null) {
//						listAdpPkgs = (String[]) (CommonUtil.getRequiredStringParameters(request,"adpPkgs"));
//					}
//
//					String[] listAdpCntNbr = new String[0];
//					if (CommonUtil.getRequiredStringParameters(request,"adpCntNbr")!= null) {
//						listAdpCntNbr = (String[]) (CommonUtil.getRequiredStringParameters(request,"adpCntNbr"));
//					}

                    //Add by NS 11112022 : Change request array parameter value.
                    int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
                    List<String> listAdpIc = new ArrayList<String>();
                    String adpIcNo;
                    List<String> listAdpNm = new ArrayList<String>();
                    String adpNmNo;
                    List<String> listAdpCd = new ArrayList<String>();
                    String adpCdNo;
                    List<String> listAdpPkgs = new ArrayList<String>();
                    String adpPkgsNo;
                    List<String> listAdpCntNbr = new ArrayList<String>();
                    String adpCntNbrNo;

                    if (sizeSelected != 0) {
                        for (int i = 0; i < sizeSelected; i++) {
                            adpIcNo = (String) criteria.getPredicates().get("adpIc" + i);
                            listAdpIc.add(adpIcNo);
                            adpNmNo = (String) criteria.getPredicates().get("adpNm" + i);
                            listAdpNm.add(adpNmNo);
                            adpCdNo = (String) criteria.getPredicates().get("adpCd" + i);
                            listAdpCd.add(adpCdNo);
                            adpPkgsNo = (String) criteria.getPredicates().get("adpPkgs" + i);
                            listAdpPkgs.add(adpPkgsNo);
                            adpCntNbrNo = (String) criteria.getPredicates().get("adpCntNbr" + i);
                            listAdpCntNbr.add(adpCntNbrNo);
                        }
                    }

                    adpList = new ArrayList<AdpValueObject>();
                    if (listAdpIc != null) {
                        for (int i = 0; i < listAdpIc.size(); i++) {
                            adpValueObject = new AdpValueObject();
                            adpValueObject.setAdpIcTdbcrNbr(listAdpIc.get(i));
                            adpValueObject.setAdpNm(listAdpNm.get(i));
                            adpValueObject.setAdpCustCd(listAdpCd.get(i));
                            adpValueObject.setAdpNbrPkgs(listAdpPkgs.get(i));
                            adpValueObject.setAdpContact(listAdpCntNbr.get(i));
                            adpList.add(adpValueObject);
                        }
                    }
                    String adpDelete = CommonUtility.deNull(criteria.getPredicates().get("adpDelete"));
                    String ta = CommonUtility.deNull(criteria.getPredicates().get("taFlag"));
                    if (!"ta".equalsIgnoreCase(ta)) { //added ta endorser flag by ThanhPT6 FPT, 2016-01-15
                        if (!"delete".equalsIgnoreCase(adpDelete)) {
                            String adpRow = CommonUtility.deNull(criteria.getPredicates().get("adpRow"));
                            int row = 0;
                            if (!adpRow.isEmpty()) {
                                row = Integer.parseInt(adpRow);
                            }
                            adpValueObject = edoService.getAdpDetails(adpIc);
                            String adpPkgs = CommonUtility.deNull(criteria.getPredicates().get("adpPkgsParam"));
                            adpValueObject.setAdpNbrPkgs(adpPkgs);
                            if (adpList.size() < row) {
                                for (int i = 0; i < row - 1; i++) {
                                    adpList.add(new AdpValueObject());
                                }
                                adpList.add(adpValueObject);
                            } else {
                                adpList.set(row, adpValueObject);
                            }
                        } else {
                            String isEditableEDOPkgs = CommonUtility.deNull(criteria.getPredicates().get("isEditableEDOPkgs"));
                            map.put("isEditableEDOPkgs", isEditableEDOPkgs);
                            String doubleWt = CommonUtility.deNull(criteria.getPredicates().get("doubleWt"));
                            map.put("_doubleWt", doubleWt);
                            String doubleVol = CommonUtility.deNull(criteria.getPredicates().get("doubleVol"));
                            map.put("_doubleVol", doubleVol);
                            String nbrpkgs = CommonUtility.deNull(criteria.getPredicates().get("nbrpkgs"));
                            map.put("_nbrpkgs", nbrpkgs);
                        }
                    }
                    map.put("adpDelete", adpDelete);
                    map.put("adp", "adp");
                } else {
                    String edoasnnbr = CommonUtility.deNull(criteria.getPredicates().get("edoasnnbr"));
                    adpList = edoService.getAdpList(edoasnnbr);
                }
                map.put("adpList", adpList);
                // HaiTTH1 added on 3/3/2014
                String adpNbr = "";
                String adpNm = "";
                String appointedAdpNbr = "";
                String appointedAdpNm = "";
                if (criteria.getPredicates().get("adpNbr_") != null)
                    adpNbr = CommonUtility.deNull(criteria.getPredicates().get("adpNbr_"));
                if (criteria.getPredicates().get("adpNm_") != null)
                    adpNm = CommonUtility.deNull(criteria.getPredicates().get("adpNm_"));
                if (criteria.getPredicates().get("appointedAdpNbr_") != null)
                    appointedAdpNbr = CommonUtility.deNull(criteria.getPredicates().get("appointedAdpNbr_"));
                if (criteria.getPredicates().get("appointedAdpNm_") != null)
                    appointedAdpNm = CommonUtility.deNull(criteria.getPredicates().get("appointedAdpNm_"));
                map.put("adpNbr_", adpNbr);
                map.put("adpNm_", adpNm);
                map.put("appointedAdpNbr_", appointedAdpNbr);
                map.put("appointedAdpNm_", appointedAdpNm);
                // HaiTTH1 ended on 3/3/2014

            }
            if (s.equalsIgnoreCase("TWO")) {
                getBLDisplay(criteria, map, "AMEND");
                String s1 = "";
                String s4 = "";
                String s6 = "";
                String s9 = "";
                String s12 = "";
                String s16 = "";
                //Begin THANHPT6 JCMS 06/01/2016
                String taUenNo = CommonUtility.deNull(criteria.getPredicates().get("taUenNo"));
                String taCCode = CommonUtility.deNull(criteria.getPredicates().get("taCCode"));
                String taNmByJP = CommonUtility.deNull(criteria.getPredicates().get("taEndorser"));
                //End THANHPT6 JCMS 06/01/2016
                //VietNguyen (FPT) Document Process Enhancement 06-Jan-2014: START
                adpList = new ArrayList<AdpValueObject>();
                AdpValueObject adpValueObject = null;
//				String[] listAdpIc = new String[0];
//				if (CommonUtil.getRequiredStringParameters(request,"adpIc") != null) {
//					listAdpIc = (String[]) (CommonUtil.getRequiredStringParameters(request,"adpIc"));
//				}
//				String[] listAdpNm = new String[0];
//				if (CommonUtil.getRequiredStringParameters(request,"adpNm") != null) {
//					listAdpNm = (String[]) (CommonUtil.getRequiredStringParameters(request,"adpNm"));
//				}
//				String[] listAdpCd = new String[0];
//				if (CommonUtil.getRequiredStringParameters(request,"adpCd") != null) {
//					listAdpCd = (String[]) (CommonUtil.getRequiredStringParameters(request,"adpCd"));
//				}
//				String[] listAdpPkgs = new String[0];
//				if (CommonUtil.getRequiredStringParameters(request,"adpPkgs") != null) {
//					listAdpPkgs = (String[]) (CommonUtil.getRequiredStringParameters(request,"adpPkgs"));
//				}
//				String[] listAdpCntNbr = new String[0];
//				if (CommonUtil.getRequiredStringParameters(request,"adpCntNbr") != null) {
//					listAdpCntNbr = (String[]) (CommonUtil.getRequiredStringParameters(request,"adpCntNbr"));
//				}
                //     			String invalidApd = "";

                //Add by NS 11112022 : Change request array parameter value.
                int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
                List<String> listAdpIc = new ArrayList<String>();
                String adpIcNo;
                List<String> listAdpNm = new ArrayList<String>();
                String adpNmNo;
                List<String> listAdpCd = new ArrayList<String>();
                String adpCdNo;
                List<String> listAdpPkgs = new ArrayList<String>();
                String adpPkgsNo;
                List<String> listAdpCntNbr = new ArrayList<String>();
                String adpCntNbrNo;

                if (sizeSelected != 0) {
                    for (int i = 0; i < sizeSelected; i++) {
                        adpIcNo = (String) criteria.getPredicates().get("adpIc" + i);
                        listAdpIc.add(adpIcNo);
                        adpNmNo = (String) criteria.getPredicates().get("adpNm" + i);
                        listAdpNm.add(adpNmNo);
                        adpCdNo = (String) criteria.getPredicates().get("adpCd" + i);
                        listAdpCd.add(adpCdNo);
                        adpPkgsNo = (String) criteria.getPredicates().get("adpPkgs" + i);
                        listAdpPkgs.add(adpPkgsNo);
                        adpCntNbrNo = (String) criteria.getPredicates().get("adpCntNbr" + i);
                        listAdpCntNbr.add(adpCntNbrNo);
                    }
                }

                if (listAdpIc != null) {
                    for (int i = 0; i < listAdpIc.size(); i++) {
                        adpValueObject = new AdpValueObject();
                        adpValueObject.setAdpIcTdbcrNbr(listAdpIc.get(i).trim());
                        adpValueObject.setAdpNm(listAdpNm.get(i));
                        adpValueObject.setAdpCustCd(listAdpCd.get(i));
                        adpValueObject.setAdpNbrPkgs(listAdpPkgs.get(i));
                        adpValueObject.setAdpContact(listAdpCntNbr.get(i));
                        adpList.add(adpValueObject);
                    }
                }

                if (adpList != null && adpList.size() > 0) {
                    s1 = adpList.get(0).getAdpIcTdbcrNbr();
                    s4 = adpList.get(0).getAdpNm();
                }
                map.put("adpList", adpList);
                //VietNguyen (FPT) Document Process Enhancement 06-Jan-2014: START
                if (criteria.getPredicates().get("crgagtnbr") != null)
                    s6 = CommonUtility.deNull(criteria.getPredicates().get("crgagtnbr"));
                if (criteria.getPredicates().get("crgagtnm") != null)
                    s9 = CommonUtility.deNull(criteria.getPredicates().get("crgagtnm"));
                if (criteria.getPredicates().get("agtattnbr") != null)
                    s12 = CommonUtility.deNull(criteria.getPredicates().get("agtattnbr"));
                if (criteria.getPredicates().get("agtattnm") != null)
                    s16 = CommonUtility.deNull(criteria.getPredicates().get("agtattnm"));
                String s17 = "false";
                if (s17.equalsIgnoreCase("false")) {
                    String s18 = edoService.getCompanyName(s1); // s18: comp name
                    if (s4.equalsIgnoreCase("") & s18.equalsIgnoreCase("")) {
                        errorMessage = ConstantUtil.ErrorMsg_Added_Atleast_One_Adp;
                        s17 = "true";
                    } else if (s18.equalsIgnoreCase("")) {
                        map.put("adpnmstatus", "NEW");
                        map.put("adpnm", s4);
                    } else // vao day
                    {
                        map.put("adpnmstatus", "OLD");
                        map.put("adpnm", s18);
                        map.put("adpnbr", s1);
                    }
                }
                if (s17.equalsIgnoreCase("false") && !s6.equalsIgnoreCase("")) {
                    String s19 = edoService.getCompanyName(s6);
                    if (s9.equalsIgnoreCase("") & s19.equalsIgnoreCase("")) {
                        errorMessage = ConstantUtil.ErrorMsg_Enter_Cargo_Agent_Name;
                        s17 = "true";
                    } else if (s19.equalsIgnoreCase("")) {
                        map.put("crgagtnmstatus", "NEW");
                        map.put("crgagtnm", s9);
                    } else {
                        map.put("crgagtnmstatus", "OLD");
                        map.put("crgagtnm", s19);
                    }
                }
                if (s17.equalsIgnoreCase("false") && !s12.equalsIgnoreCase("")) {
                    String s20 = edoService.getCompanyName(s12);
                    if (s16.equalsIgnoreCase("") & s20.equalsIgnoreCase("")) {
                        errorMessage = ConstantUtil.ErrorMsg_Enter_Attendance_Agent_Name;
                        s17 = "true";
                    } else if (s20.equalsIgnoreCase("")) {
                        map.put("agtattnmstatus", "NEW");
                        map.put("agtattnm", s16);
                    } else {
                        map.put("agtattnmstatus", "OLD");
                        map.put("agtattnm", s20);
                    }
                }
                String s21 = "EDO";
                if (criteria.getPredicates().get("modulecd") != null && !(criteria.getPredicates().get("modulecd").isEmpty()))
                    s21 = CommonUtility.deNull(criteria.getPredicates().get("modulecd"));
                if (s17.equalsIgnoreCase("false") && s21.equalsIgnoreCase("ADPRENOM"))
                    s = "THREE";
                else if (s17.equalsIgnoreCase("true")) {
                    map.put("strmode", "TWO");
                } else {
                    List<EdoJpBilling> arraylist = new ArrayList<EdoJpBilling>();
                    if (criteria.getPredicates().get("adpnbr") != null)
                        s1 = CommonUtility.deNull(criteria.getPredicates().get("adpnbr"));
                    String s22 = "";
                    if (criteria.getPredicates().get("varnbr") != null)
                        s22 = CommonUtility.deNull(criteria.getPredicates().get("varnbr"));

                    String s24 = coCd;
                    arraylist = edoService.getEdoJpBillingNbr(s1, s24, s22);
                    String amtDel = "";
                    if (criteria.getPredicates().get("amtdelivered") != null) {
                        amtDel = CommonUtility.deNull(criteria.getPredicates().get("amtdelivered"));
                    } else {
                        amtDel = "0";
                    }
                    map.put("jpbnbrarraylist", arraylist);
                    // Add by Ding Xijia(harbortek) 09-Feb-2011 : START
                    String scheme = edoService.getVesselScheme(s22);
                    // Add by Ding Xijia(harbortek) 09-Feb-2011 : END
                    map.put("strmode", "NEXTTWO");
                    // Add by Ding Xijia(harbortek) 09-Feb-2011 : START
                    map.put("scheme", scheme);
                    // Add by Ding Xijia(harbortek) 09-Feb-2011 : END
                    map.put("amtdelivered", amtDel);
                    //Begin THANHPT6 JCMS 06/01/2016
                    map.put("taUenNo", taUenNo);
                    map.put("taCCode", taCCode);
                    map.put("taEndorser", taNmByJP);
                    //End ThanhPt6
                }
            }
            if (s.equalsIgnoreCase("THREE")) {
                String s2 = "false";
                String s5 = "0";
                String s7 = "0";
                String s10 = "0";

                String varName = "";
                String varNo = "";

                if (criteria.getPredicates().get("vslNm") != null) {
                    varName = CommonUtility.deNull(criteria.getPredicates().get("vslNm"));
                    varNo = CommonUtility.deNull(criteria.getPredicates().get("vslVoy"));
                }

                log.info("2.EdoAmendHandler::vesselName = " + varName);
                log.info("3.EdoAmendHandler::vesselName = " + varNo);

                map.put("varName", varName);
                map.put("varNo", varNo);

                if (criteria.getPredicates().get("existnbrpkgs") != null) {
                    s7 = CommonUtility.deNull(criteria.getPredicates().get("existnbrpkgs"));
                } else {
                    errorMessage = ConstantUtil.ErrorMsg_Enter_Valid_Edo_Pkgs;
                    s2 = "true";
                    getBLDisplay(criteria, map, "AMEND");
                    map.put("strmode", "ONE");
                }
                if (criteria.getPredicates().get("newnbrpkgs") != null) {
                    s5 = CommonUtility.deNull(criteria.getPredicates().get("newnbrpkgs"));
                } else {
                    errorMessage = ConstantUtil.ErrorMsg_Enter_Valid_Edo_Pkgs;
                    s2 = "true";
                    getBLDisplay(criteria, map, "AMEND");
                    map.put("strmode", "ONE");
                }
                if (s5.equalsIgnoreCase(""))
                    s5 = "0";
                if (s7.equalsIgnoreCase(""))
                    s7 = "0";
                int i = Integer.parseInt(s5);
                int j = Integer.parseInt(s7);
                int k = i - j;
                if (k > 0)
                    s5 = String.valueOf(k);
                else
                    s5 = "0";
                if (criteria.getPredicates().get("blnbr") != null) {
                    String s14 = CommonUtility.deNull(criteria.getPredicates().get("blnbr"));
                    s10 = edoService.getEdoNbrPkgs(s14);
                } else {
                    errorMessage = ConstantUtil.ErrorMsg_Enter_Valid_Edo_Pkgs;
                    s2 = "true";
                    getBLDisplay(criteria, map, "AMEND");
                    map.put("strmode", "ONE");
                }
                if (s10.equalsIgnoreCase(""))
                    s10 = "0";
                int l = Integer.parseInt(s5);
                int i1 = Integer.parseInt(s10);
                if (i1 < l) {
                    errorMessage = ConstantUtil.ErrorMsg_Enter_Valid_Edo_Pkgs;
                    s2 = "true";
                    getBLDisplay(criteria, map, "AMEND");
                    map.put("strmode", "ONE");
                }
                if (s2.equalsIgnoreCase("false")) {
                    adpList = new ArrayList<AdpValueObject>();
                    AdpValueObject adpValueObject = null;
//					String[] listAdpIc = new String[0];
//					if (criteria.getPredicates().get("adpIc") != null) {
//						listAdpIc = (String[])  (CommonUtil.getRequiredStringParameters(request,"adpIc"));
//					}
//					String[] listAdpNm = new String[0];
//					if (criteria.getPredicates().get("adpNm") != null) {
//						listAdpNm = (String[])  (CommonUtil.getRequiredStringParameters(request,"adpNm"));
//					}
//					String[] listAdpCd = new String[0];
//					if (CommonUtil.getRequiredStringParameters(request,"adpCd") != null) {
//						listAdpCd = (String[])  (CommonUtil.getRequiredStringParameters(request,"adpCd"));
//					}
//					String[] listAdpPkgs = new String[0];
//					if (criteria.getPredicates().get("adpPkgs") != null) {
//						listAdpPkgs = (String[])  (CommonUtil.getRequiredStringParameters(request,"adpPkgs"));
//					}
//					String[] listAdpCntNbr = new String[0];
//					if (criteria.getPredicates().get("adpCntNbr") != null) {
//						listAdpCntNbr = (String[])  (CommonUtil.getRequiredStringParameters(request,"adpCntNbr"));
//					}

                    //Add by NS 11112022 : Change request array parameter value.
                    int sizeSelected = Integer.parseInt(CommonUtility.deNull(criteria.getPredicates().get("size")));
                    List<String> listAdpIc = new ArrayList<String>();
                    String adpIcNo;
                    List<String> listAdpNm = new ArrayList<String>();
                    String adpNmNo;
                    List<String> listAdpCd = new ArrayList<String>();
                    String adpCdNo;
                    List<String> listAdpPkgs = new ArrayList<String>();
                    String adpPkgsNo;
                    List<String> listAdpCntNbr = new ArrayList<String>();
                    String adpCntNbrNo;

                    if (sizeSelected != 0) {
                        for (i = 0; i < sizeSelected; i++) {
                            adpIcNo = (String) criteria.getPredicates().get("adpIc" + i);
                            listAdpIc.add(adpIcNo.trim());
                            adpNmNo = (String) criteria.getPredicates().get("adpNm" + i);
                            listAdpNm.add(adpNmNo.trim());
                            adpCdNo = (String) criteria.getPredicates().get("adpCd" + i);
                            listAdpCd.add(adpCdNo);
                            adpPkgsNo = (String) criteria.getPredicates().get("adpPkgs" + i);
                            listAdpPkgs.add(adpPkgsNo);
                            adpCntNbrNo = (String) criteria.getPredicates().get("adpCntNbr" + i);
                            listAdpCntNbr.add(adpCntNbrNo);
                        }
                    }

                    if (listAdpIc != null) {
                        for (i = 0; i < listAdpIc.size(); i++) {
                            adpValueObject = new AdpValueObject();
                            adpValueObject.setAdpIcTdbcrNbr(listAdpIc.get(i));
                            adpValueObject.setAdpNm(listAdpNm.get(i));
                            adpValueObject.setAdpCustCd(listAdpCd.get(i));
                            adpValueObject.setAdpNbrPkgs(listAdpPkgs.get(i));
                            adpValueObject.setAdpContact(listAdpCntNbr.get(i));
                            adpList.add(adpValueObject);
                        }
                    }

                    String s23 = "false";
                    String stramtdel = "";
                    s23 = amendEdoDetails(criteria, adpList, map);
                    if (s23.equalsIgnoreCase("false")) {
                        getBLDisplay(criteria, map, "AMEND");
                        map.put("strmode", "ONE");
                    } else {
                        if (criteria.getPredicates().get("amtdelivered") != null) {
                            stramtdel = CommonUtility.deNull(criteria.getPredicates().get("amtdelivered"));
                        } else {
                            stramtdel = "0";
                        }
                        viewEdoDetails(criteria, s23, map);
                        map.put("strmsg", "Successfully Amended");
                        map.put("strmode", "VIEWEDO");
                        map.put("amtdelivered", stramtdel);
                    }
                }
            }
            if (s.equalsIgnoreCase("VETT")) {

                String s8 = userId;
                String s11 = "";
                String s15 = "";
                if (criteria.getPredicates().get("edoasnnbr") != null)
                    s11 = CommonUtility.deNull(criteria.getPredicates().get("edoasnnbr"));
                if (criteria.getPredicates().get("edostatus") != null)
                    s15 = CommonUtility.deNull(criteria.getPredicates().get("edostatus"));
                edoService.updateVettedEdo(s11, s15, s8);
                s = "VIEWEDO";
            }
            if (s.equalsIgnoreCase("VIEWEDO")) {
                String s3 = "";
                if (criteria.getPredicates().get("edoasnnbr") != null)
                    s3 = CommonUtility.deNull(criteria.getPredicates().get("edoasnnbr"));
                viewEdoDetails(criteria, s3, map);
                map.put("strmode", "VIEWEDO");
            }
            // HaiTTH1 added on 28/2/2014
            String edoasnnbr = CommonUtility.deNull(criteria.getPredicates().get("edoasnnbr"));
            List<AdpValueObject> vewAdpList = edoService.getAdpList(edoasnnbr);
            map.put("viewAdpList", vewAdpList);
            map.put("maxiAdp", maxiAdp);
            // HaiTTH1 ended on 28/2/2014
            map.put("httpservletrequest", "gbmsEdoAmendSer");
        } catch (BusinessException e) {
            log.info("Exception edoAmend : ", e);
            errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
            if (errorMessage == null) {
                errorMessage = e.getMessage();
            }
        } catch (Exception e) {
            log.info("Exception edoAmend : ", e);
            errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
        } finally {
            if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
                map.put("errorMessage", errorMessage);
                result = new Result();
                result.setError(errorMessage);
                result.setErrors(map);
                result.setSuccess(false);
            } else {
                result = new Result();
                result.setData(map);
                result.setSuccess(true);
            }
            log.info("End edoAmend result:" + result.toString());
        }
        return ResponseEntityUtil.success(result.toString());
    }


    //delegate.helper.gbms.cargo.edo -->EdoAddHandler -->getBLDisplay()
    private void getBLDisplay(Criteria criteria, Map<String, Object> map, String action) throws BusinessException {
        try {
            log.info("** getBLDisplay Start criteria: " + criteria.toString());
            TopsModel topsmodel = new TopsModel();
            List<EdoValueObjectCargo> vector = new ArrayList<EdoValueObjectCargo>();
            List<ArrayList<String>> vector1 = new ArrayList<ArrayList<String>>();
            String s = "";
            String s1 = "";
            s1 = CommonUtility.deNull(criteria.getPredicates().get("varnbr"));
            if ("--Select--".equalsIgnoreCase(s1) || "".equalsIgnoreCase(s1) || s1 == null)
                s1 = CommonUtility.deNull(criteria.getPredicates().get("vslnmvoynbr"));

            log.info("====INSIDE getBLDisplay: varnbr = " + s1);

            //  16/06/2011 PCYAP To add/update only own EDO
            String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
            vector = edoService.getBLNbrList(s1, action, coCd);
            if (criteria.getPredicates().get("blnbr") == null) {
                EdoValueObjectCargo edovalueobject = new EdoValueObjectCargo();
                edovalueobject = (EdoValueObjectCargo) vector.get(0);
                s = edovalueobject.getMftSeqNbr();
            } else {
                s = CommonUtility.deNull(criteria.getPredicates().get("blnbr"));
            }
            for (int i = 0; i < vector.size(); i++) {
                EdoValueObjectCargo edovalueobject1 = null;
                edovalueobject1 = (EdoValueObjectCargo) vector.get(i);
                ArrayList<String> arraylist = new ArrayList<String>();
                arraylist.add(edovalueobject1.getBlNbr());
                arraylist.add(edovalueobject1.getMftSeqNbr());
                vector1.add(arraylist);
            }
            map.put("blnbrvector", vector1);
            List<EdoValueObjectCargo> vector2 = new ArrayList<EdoValueObjectCargo>();
            vector2 = edoService.getBLDetails(s);
            for (int j = 0; j < vector2.size(); j++) {
                EdoValueObjectCargo edovalueobject2 = new EdoValueObjectCargo();
                edovalueobject2 = (EdoValueObjectCargo) vector2.get(j);
                topsmodel.put(edovalueobject2);
            }
            map.put("model", topsmodel);

            // START CR FTZ HSCODE - NS JULY 2024
            List<HsCodeDetails> hsCodeDetailList = new ArrayList<HsCodeDetails>();
            List<HsCodeDetails> hsCodeEdoDetailList = new ArrayList<HsCodeDetails>();
            List<Map<String, String>> hsCodeAllEdoDetailList = new ArrayList<Map<String, String>>();

            if (action.equalsIgnoreCase("add")) {
                hsCodeDetailList = edoService.getHsCodeDetails(s);
                map.put("hsCodeDetailsList", hsCodeDetailList);

                // if mft seq nbr already exist in edo hscode detail table,
                boolean ifHsCodeExist = edoService.ifHsCodeExist(s);
                if (ifHsCodeExist) {
                    map.put("ifHsCodeExist", true);
                    // check if its combined as all hscode

                    List<Map<String, String>> optionHscodeExistingList = edoService.getOptionHscodeExisting(s);
                    map.put("optionHscodeExistingList", optionHscodeExistingList);

                }


            } else {
                hsCodeDetailList = edoService.getHsCodeDetails(s); // manifest
                map.put("hsCodeDetailsList", hsCodeDetailList);
                hsCodeEdoDetailList = edoService.getEdoHsCodeDetails(criteria.getPredicates().get("edoasnnbr")); //edo
                map.put("hsCodeEdoDetailList", hsCodeEdoDetailList);
                hsCodeAllEdoDetailList = edoService.getAllEdoHsCodeDetails(s, criteria.getPredicates().get("edoasnnbr")); // all edo with mft
                map.put("hsCodeAllEdoDetailList", hsCodeAllEdoDetailList);
            }

            // END CR FTZ HSCODE - NS JULY 2024

            getBLDisplayList(criteria, map, topsmodel);
        } catch (BusinessException e) {
            log.info("Exception getBLDisplay : ", e);
            throw new BusinessException(e.getMessage());
        } catch (Exception e) {
            log.info("Exception getBLDisplay : ", e);
        } finally {
            log.info("End getBLDisplay()");
        }
    }


    //delegate.helper.gbms.cargo.edo -->EdoAmendHandler -->viewEdoDetails()
    public void viewEdoDetails(Criteria criteria, String s, Map<String, Object> map) throws BusinessException {
        try {
            log.info("** viewEdoDetails Start criteria: " + criteria.toString());
            TopsModel topsmodel = new TopsModel();

            List<EdoValueObjectCargo> vector = edoService.viewEdoDetails(s); // EdoAmendService
            EdoValueObjectCargo edovalueobject = new EdoValueObjectCargo();
            edovalueobject = (EdoValueObjectCargo) vector.get(0);
            topsmodel.put(edovalueobject);
            map.put("model", topsmodel);

            // Added by VietNguyen 20/01/2014
            List<AdpValueObject> adpList = edoService.getAdpList(s); // EdoAmendService
            map.put("adpList", adpList);
            // haiTTH1 added on 3/3/2014 for adp renom
            map.put("viewAdpList", adpList);


            // START FTZ - NS JULY 2024
            List<HsCodeDetails> hsCodeDetailList = new ArrayList<HsCodeDetails>();
            hsCodeDetailList = edoService.getEdoHsCodeDetails(edovalueobject.getEdoAsnNbr());
            map.put("hsCodeDetailsList", hsCodeDetailList);
            // END FTZ
        } catch (BusinessException e) {
            log.info("Exception viewEdoDetails : ", e);
            throw new BusinessException(e.getMessage());
        } catch (Exception e) {
            log.info("Exception viewEdoDetails : ", e);
            throw new BusinessException("M4201");
        } finally {
            log.info("End viewEdoDetails()");
        }
    }

    //uiServlet.gbms.cargo.edo; -->EdoAddServlet -->getBLDisplayList()
    public void getBLDisplayList(Criteria criteria, Map<String, Object> map, TopsModel topsmodel) throws BusinessException {
        try {
            log.info("getBLDisplayList Start criteria:" + criteria.toString());
            String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
            if (topsmodel != null && topsmodel.getSize() != 0) {
                EdoValueObjectCargo edovalueobject = (EdoValueObjectCargo) topsmodel.get(0);
                map.put("blnbrlistvector", edovalueobject);
                // START - CR To remove validation for volume - NS MAY 2024
                criteria.addPredicate("checkType", "All");
                criteria.addPredicate("hsCd", edovalueobject.getHsCode());
                criteria.addPredicate("hsSubCd",
                        edovalueobject.getHsCodeFrom()
                                + (!CommonUtil.deNull(edovalueobject.getHsCodeTo()).isEmpty()
                                ? ("-" + edovalueobject.getHsCodeTo())
                                : ""));
                criteria.addPredicate("scheme", inwardCargoManifestService.getSchemeName(CommonUtil.deNull(criteria.getPredicates().get("varnbr"))));
                criteria.addPredicate("status", edovalueobject.getCrgStatus().equalsIgnoreCase("L") ? "Local" : "Transhipment");
                criteria.addPredicate("consigneeCode", edovalueobject.getConsignee());
                criteria.addPredicate("consigneeOthers", edovalueobject.getConsNm());
                map.put("disabledVolume", inwardCargoManifestService.isDisabledVolume(criteria));
                // END - CR To remove validation for volume - NS MAY 2024
            }
            // HaiTTH1 added on 14/1/2014
            map.put("custCd", coCd);
        } catch (Exception e) {
            log.info("Exception getBLDisplayList : ", e);
            throw new BusinessException("M4201");
        } finally {
            log.info("End getBLDisplayList()");
        }
    }

    //delegate.helper.gbms.cargo.edo -->EdoAmendHandler -->amendEdoDetails()
    public String amendEdoDetails(Criteria criteria, List<AdpValueObject> adpList, Map<String, Object> map) throws BusinessException {
        String s = "false";
        try {
            log.info("** amendEdoDetails Start criteria: " + criteria.toString());
            String s1 = "";
            String s2 = "";
            String s5 = "";
            String s6 = "";
            String s7 = "";
            String s8 = "";
            String s9 = "";
            String s10 = "";
            String s11 = "";
            String s12 = "";
            String s13 = "";
            String s14 = "O";
            String s15 = "";
            String s16 = "";
            String s17 = "A";
            String s19 = "";
            String s20 = "";
            String s21 = "";
            String s22 = "";
            String s23 = "";
            String s24 = "";
            String s25 = "";
            String s26 = "";
            String s27 = "";
            String s28 = "";
            //String strStfInd = "";   removed by vinayak 19/12/2003
            String userId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
            String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
            String s29 = coCd;
            String s30 = userId;
            s20 = s30;
            String s31 = "EDO";
            if (criteria.getPredicates().get("modulecd") != null)
                s31 = CommonUtility.deNull(criteria.getPredicates().get("modulecd"));
            if (criteria.getPredicates().get("blnbr") != null)
                s1 = CommonUtility.deNull(criteria.getPredicates().get("blnbr"));
            if (criteria.getPredicates().get("varnbr") != null)
                s2 = CommonUtility.deNull(criteria.getPredicates().get("varnbr"));
            // if(httpservletrequest.getParameter("adpnm") != null)
            // s8 = httpservletrequest.getParameter("adpnm");
            if (adpList.size() > 0) {
                AdpValueObject obj = (AdpValueObject) adpList.get(0);
                s5 = obj.getAdpIcTdbcrNbr();
                s8 = obj.getAdpNm();
            }

            if (criteria.getPredicates().get("newnbrpkgs") != null)
                s9 = CommonUtility.deNull(criteria.getPredicates().get("newnbrpkgs"));
            if (criteria.getPredicates().get("crgagtnm") != null)
                s11 = CommonUtility.deNull(criteria.getPredicates().get("crgagtnm"));
            if (criteria.getPredicates().get("agtattnm") != null)
                s13 = CommonUtility.deNull(criteria.getPredicates().get("agtattnm"));
            if (criteria.getPredicates().get("deliveryto") != null)
                s14 = CommonUtility.deNull(criteria.getPredicates().get("deliveryto"));
            if (criteria.getPredicates().get("jpbnbr") != null)
                s16 = CommonUtility.deNull(criteria.getPredicates().get("jpbnbr"));

            if (s16.equalsIgnoreCase("cash"))
                s17 = "C";
            else
                s17 = "A";
            if (criteria.getPredicates().get("edoasnnbr") != null)
                s21 = CommonUtility.deNull(criteria.getPredicates().get("edoasnnbr"));
            if (criteria.getPredicates().get("edostatus") != null)
                s19 = CommonUtility.deNull(criteria.getPredicates().get("edostatus"));

            // if(httpservletrequest.getParameter("adpnbr") != null)
            // s5 = httpservletrequest.getParameter("adpnbr");

            if (criteria.getPredicates().get("adpnmstatus") != null)
                s15 = CommonUtility.deNull(criteria.getPredicates().get("adpnmstatus"));
            if (s31.equalsIgnoreCase("ADPRENOM")) {
                if (criteria.getPredicates().get("adpnmstatus") != null)
                    s15 = (String) CommonUtility.deNull(criteria.getPredicates().get("adpnmstatus"));
                if (adpList.size() > 0) {
                    AdpValueObject obj = (AdpValueObject) adpList.get(0);
                    s6 = obj.getAdpIcTdbcrNbr();
                    s8 = obj.getAdpNm();
                }
            }
            if (s15.equalsIgnoreCase("NEW"))
                s7 = s5;
            if (s15.equalsIgnoreCase("OLD")) {
                s7 = s5;
                s5 = edoService.getCustomerNbr(s5);
                s6 = s5;
            }
            if (criteria.getPredicates().get("crgagtnbr") != null)
                s24 = CommonUtility.deNull(criteria.getPredicates().get("crgagtnbr"));
            if (criteria.getPredicates().get("crgagtnmstatus") != null)
                s22 = CommonUtility.deNull(criteria.getPredicates().get("crgagtnmstatus"));
            if (s22.equalsIgnoreCase("NEW"))
                s23 = s24;
            if (s22.equalsIgnoreCase("OLD")) {
                s23 = s24;
                s24 = edoService.getCustomerNbr(s24);
                s10 = s24;
            }
            if (criteria.getPredicates().get("agtattnbr") != null)
                s27 = CommonUtility.deNull(criteria.getPredicates().get("agtattnbr"));
            if (criteria.getPredicates().get("agtattnmstatus") != null)
                s25 = CommonUtility.deNull(criteria.getPredicates().get("agtattnmstatus"));
            if (s25.equalsIgnoreCase("NEW"))
                s26 = s27;
            if (s25.equalsIgnoreCase("OLD")) {
                s26 = s27;
                s27 = edoService.getCustomerNbr(s27);  //ShutOutCargoRepository
                s12 = s27;
            }
            if (criteria.getPredicates().get("distype") != null)
                s28 = CommonUtility.deNull(criteria.getPredicates().get("distype"));

			/*if(httpservletrequest.getParameter("stfInd") != null)
	                strStfInd = httpservletrequest.getParameter("stfInd"); removed by vinayak 19/12/2003*/
            String wt = CommonUtility.deNull(criteria.getPredicates().get("upWeight"));// Added by thanhnv2
            String vol = CommonUtility.deNull(criteria.getPredicates().get("upVolume"));// Added by thanhnv2
            //Begin ThanhPT6 JCMS 06/01/2016
            String taUenNo = CommonUtility.deNull(criteria.getPredicates().get("taUenNo")) == null ? "" : CommonUtility.deNull(criteria.getPredicates().get("taUenNo"));
            String taCCode = CommonUtility.deNull(criteria.getPredicates().get("taCCode")) == null ? "" : CommonUtility.deNull(criteria.getPredicates().get("taCCode"));
            String taNmByJP = CommonUtility.deNull(criteria.getPredicates().get("taEndorser")) == null ? "" : CommonUtility.deNull(criteria.getPredicates().get("taEndorser"));
            //End ThanhPT6
            log.info("=====wt+vol: " + wt + " - " + vol + " - " + s1);
            //++ updated by thanhnv2: add 2 more paramenters
            //Add parameter for TA Endorser by ThanhPT6

            //GeneralCargoEdoJdbcRepository
            s = edoService.updateEdoDetailsForDPE(s1, s2, s6, s8, s7, s10, s11, s12, s13, s9, s14, s16, s17, s19, s20, s21, s23, s26, s29, s31, s28, wt, vol, adpList, taUenNo, taCCode, taNmByJP);//added 'strStfInd' by vani -- 2nd Oct,03 ,strStfInd
            //-- updated by thanhnv2: add 2 more paramenters
        } catch (BusinessException e) {
            log.info("Exception amendEdoDetails : ", e);
            throw new BusinessException(e.getMessage());
        } catch (Exception e) {
            log.info("Exception amendEdoDetails : ", e);
            throw new BusinessException("M4201");
        } finally {
            log.info("End amendEdoDetails()");
        }
        return s;
    }
    //EndRegion EdoAmend

    // CH - 3 --> Winstar Changes Start
    @PostMapping(value = "/edoByVessel")
    public ResponseEntity<?> edoByVessel(HttpServletRequest request) {
        Result result = new Result();
        Map<String, Object> responseMap = new HashMap<>();
        Criteria criteria = CommonUtil.getCriteria(request);

        String companyCode = resolveCompanyCode(criteria);
        String userId = resolveUserId(criteria);
        String varnbr = CommonUtility.deNull(criteria.getPredicates().get("varnbr"));
        String action = CommonUtility.deNull(criteria.getPredicates().get("action"));

        try {
            log.info("Start edoByVessel criteria: {}" + criteria);

            validateVesselCallNumber(varnbr);
            populateBaseRequestMap(responseMap, action, companyCode, userId, varnbr);

            VesselVoyValueObject vessel = edoService.getVesselInfo(varnbr);
            log.info("Resolved vessel: name={}, voyage={}"+ vessel.getVslName());

            enrichMapWithVesselSchedule(responseMap, companyCode, vessel);

            if ("add".equalsIgnoreCase(action)) {
                getEdoByVessel(criteria, responseMap, "ADD");
            }

            result.setData(responseMap);
            result.setSuccess(true);

        } catch (BusinessException e) {
            log.warn("Business exception in edoByVessel: {}" + e.getMessage());
            handleError(result, responseMap, resolveBusinessError(e));

        } catch (Exception e) {
            log.error("Unexpected exception in edoByVessel", e);
            handleError(result, responseMap, ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201"));
        }

        log.info("End edoByVessel result: {}" +  result);
        return ResponseEntityUtil.success(result.toString());
    }

    private void getEdoByVessel(Criteria criteria, Map<String, Object> map, String action)
            throws BusinessException {
        log.info("** getEdoByVessel Start criteria: {}"+ criteria);

        try {
            // Extract parameters safely
            String varNbr = CommonUtility.deNull(criteria.getPredicates().get("varnbr"));
            String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

            // Fetch initial cargo list (only for "add" action with non-empty varnbr)
            List<EdoValueObjectCargo> cargoList;
            if (!varNbr.isEmpty() && "add".equalsIgnoreCase(action)) {
                // FIXED: s1 was undefined – replaced with varNbr. Verify actual intended value!
                cargoList = edoService.getBLNbrList(varNbr, action, coCd);
            } else {
                cargoList = Collections.emptyList();
            }

            // Build both blList and mftSeqNbrList in a single stream operation
            List<List<String>> blList = new ArrayList<>();
            List<String> mftSeqNbrList = new ArrayList<>();

            for (EdoValueObjectCargo cargo : cargoList) {
                // Build blList entry
                blList.add(Arrays.asList(cargo.getBlNbr(), cargo.getMftSeqNbr()));

                // Collect non-blank mftSeqNbr for later use
                String mftSeq = cargo.getMftSeqNbr();
                if (mftSeq != null && !mftSeq.isEmpty()) {
                    mftSeqNbrList.add(mftSeq);
                }
            }

            map.put("blList", blList);

            // Fetch EDO details – CRITICAL PERFORMANCE FIX: batch query
            List<EdoValueObjectCargo> edoDetailsList = new ArrayList<>();
            List<EdoValueObjectCargo> edoDetails;
            if (!mftSeqNbrList.isEmpty()) {
                for (String seqNbr : mftSeqNbrList) {
                    edoDetails = edoService.getEdoByVessel(seqNbr);
                    if (edoDetails != null) {
                        edoDetailsList.addAll(edoDetails);
                    }
                }
            }
            // Log each key for debugging (optional, keep if needed)
            mftSeqNbrList.forEach(key -> log.info("====mftSeqNbrList data {}" + key));

            map.put("edoDetails", edoDetailsList);

        } catch (BusinessException e) {
            log.error("BusinessException in getEdoByVessel: ", e);
        } catch (Exception e) {
            log.error("Unexpected exception in getEdoByVessel: ", e);
        } finally {
            log.info("End getEdoByVessel()");
        }
    }

    @PostMapping(value = "/edoManifest")
    public ResponseEntity<?> edoManifest(@RequestBody ManiFestObject request) {
        errorMessage = null;
        Result result = new Result();
        try {
            log.info("Start of Edo Manifest");
            result = edoService.processEdoManifestEntries(request);
        } catch (Exception e) {
            log.info("Exception edoManifest : ", e);
            errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
        } finally {
            if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
                result = new Result();
                result.setError(errorMessage);
                result.setSuccess(false);
            }
            log.info("End edoManifest result:" + result.toString());
        }

        return ResponseEntityUtil.success(result.toString());
    }

    @GetMapping("/adp/{adpIc}")

    public ResponseEntity<?> getAdpList(

            @PathVariable String adpIc) {

        Result result = new Result();

        Map<String, Object> map = new HashMap<String, Object>();

        try {

            AdpValueObject adpObject = edoService.getAdpDetails(adpIc);

            map.put("adpObject", adpObject);

        } catch (BusinessException e) {

            log.info("Exception edoAdd : ", e);

            errorMessage = CommonUtility.getExceptionMessage(e);

            if (errorMessage == null) {

                errorMessage = e.getMessage();

            }

        } catch (Exception e) {

            log.info("Exception edoAdd : ", e);

            errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");

        } finally {

            if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {

                map.put("errorMessage", errorMessage);

                result = new Result();

                result.setError(errorMessage);

                result.setErrors(map);

                result.setSuccess(false);

            } else {

                result = new Result();

                result.setData(map);

                result.setSuccess(true);

            }

        }

        return ResponseEntityUtil.success(result.toString());

    }

    @GetMapping("/deliveryNote/{taUaeNo}")
    public ResponseEntity<?> getDNDetails(@PathVariable String taUaeNo) {
        Result result = new Result();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            AdpValueObject adpVO = edoService.getTaEndorserNmByUENNo(taUaeNo);

            map.put("taUaeNo", taUaeNo);

            map.put("taCCode", adpVO.getAdpCustCd() == null ? "" : adpVO.getAdpCustCd());

            map.put("taName", adpVO.getAdpNm() == null ? "" : adpVO.getAdpNm());
        } catch (BusinessException e) {
            log.info("Exception edoAdd : ", e);
            errorMessage = CommonUtility.getExceptionMessage(e);
            if (errorMessage == null) {
                errorMessage = e.getMessage();
            }
        } catch (Exception e) {
            log.info("Exception edoAdd : ", e);
            errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
        } finally {
            if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
                map.put("errorMessage", errorMessage);
                result = new Result();
                result.setError(errorMessage);
                result.setErrors(map);
                result.setSuccess(false);
            } else {
                result = new Result();
                result.setData(map);
                result.setSuccess(true);
            }
        }
        return ResponseEntityUtil.success(result.toString());
    }

    @GetMapping("/jpBill/{vslnmvoynbr}/{coCd}/{adpNbr}")
    public ResponseEntity<?> getJPBillDetails(@PathVariable String vslnmvoynbr,
                                              @PathVariable String coCd,

                                              @PathVariable String adpNbr) {
        Result result = new Result();
        Map<String, Object> map = new HashMap<String, Object>();

        List<EdoJpBilling> arraylist = new ArrayList<EdoJpBilling>();
        try {
            arraylist = edoService.getEdoJpBillingNbr(adpNbr, coCd, vslnmvoynbr);

            map.put("jpbnbrarraylist", arraylist);

            String adpnm = edoService.getCompanyName(adpNbr);

            if (adpnm.equalsIgnoreCase("")) {
                map.put("adpnmstatus", "NEW");
                map.put("adpnm", adpNbr);
            } else {
                map.put("adpnmstatus", "OLD");
                map.put("adpnm", adpnm);
            }
        } catch (BusinessException e) {
            log.info("Exception edoAdd : ", e);
            errorMessage = CommonUtility.getExceptionMessage(e);
            if (errorMessage == null) {
                errorMessage = e.getMessage();
            }
        } catch (Exception e) {
            log.info("Exception edoAdd : ", e);
            errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
        } finally {
            if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
                map.put("errorMessage", errorMessage);
                result = new Result();
                result.setError(errorMessage);
                result.setErrors(map);
                result.setSuccess(false);
            } else {
                result = new Result();
                result.setData(map);
                result.setSuccess(true);
            }
        }
        return ResponseEntityUtil.success(result.toString());
    }

    // CH - 3 --> Winstar Changes End

    @PostMapping(value = "/edoDelete")
    public ResponseEntity<?> edoDelete(HttpServletRequest request) {
        Result result = new Result();
        errorMessage = null;
        Map<String, Object> map = new HashMap<String, Object>();
        Criteria criteria = CommonUtil.getCriteria(request);
        try {
            log.info("Start edoDelete criteria :" + criteria.toString());
            String edoasnnbr = CommonUtility.deNull(criteria.getPredicates().get("edoasnnbr"));
            String stredoasnnbr = "";
            if (edoasnnbr != null) {
                stredoasnnbr = (String) edoasnnbr;
            }
            boolean isTesnExist = edoService.checkTesnExist(stredoasnnbr);
            if (isTesnExist) {
                throw new BusinessException("M20815");
            }

            boolean isDelete = edoService.checkDeleteEdo(stredoasnnbr);
            if (!isDelete) {
                // errorMessage(request, "This shipment (ASN) has been tagged
                // with Warehouse Application, should you require assistance,
                // please contact SSPAT and D&C officers");
                errorMessage = ConstantUtil.ErrorMsg_Shipment_Has_Been_Tagged;
                throw new BusinessException(errorMessage);
            }
            String UserId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
            String deletestatus = "";
            deletestatus = edoService.deleteEdoDetails(stredoasnnbr, UserId);
            //	result.fromBean(deletestatus);
            map.put("canedo", stredoasnnbr);
            map.put("Screen", "gbmsEdoDeleteSer");
            map.put("deletestatus", deletestatus);

        } catch (BusinessException e) {
            log.info("Exception edoDelete : ", e);
            errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(e));
            if (errorMessage == null) {
                errorMessage = e.getMessage();
            }
        } catch (Exception e) {
            log.info("Exception edoDelete : ", e);
            errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
        } finally {
            if (errorMessage != null && !errorMessage.equalsIgnoreCase("")) {
                map.put("errorMessage", errorMessage);
                result = new Result();
                result.setError(errorMessage);
                result.setErrors(map);
                result.setSuccess(false);
            } else {
                result = new Result();
                result.setData(map);
                result.setSuccess(true);
            }
            log.info("End edoDelete result:" + result.toString());
        }
        return ResponseEntityUtil.success(result.toString());
    }

    // ─── Private Helpers ────────────────────────────────────────────────────────

    private String resolveCompanyCode(Criteria criteria) {
        String value = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
        return value.isEmpty() ? "JP" : value;
    }

    private String resolveUserId(Criteria criteria) {
        String value = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
        return value.isEmpty() ? "SYSTEM" : value;
    }

    private void validateVesselCallNumber(String varnbr) throws BusinessException {
        if ("--Select--".equalsIgnoreCase(varnbr)) {
            throw new BusinessException(ConstantUtil.ErrorMsg_Invalid_Vessel_Call);
        }
    }

    private void populateBaseRequestMap(Map<String, Object> map,
                                        String action,
                                        String companyCode,
                                        String userId,
                                        String varnbr) {
        map.put("moduleCd", "EDO");
        map.put("ccode", companyCode);
        map.put("companyCode", companyCode);
        map.put("userAccount", userId);

        if (!action.isEmpty()) map.put("action", action);
        if (!varnbr.isEmpty()) map.put("varnbr", varnbr);
    }

    private void enrichMapWithVesselSchedule(Map<String, Object> map,
                                             String companyCode,
                                             VesselVoyValueObject vessel) throws BusinessException {
        String vesselName = vessel.getVslName();
        String voyageNumber = vessel.getVoyNo();

        map.put("vslNm", vesselName);
        map.put("vslVoy", voyageNumber);

        List<EdoValueObjectCargo> scheduleList =
                edoService.getVesselVoyageNbrList(companyCode, "EDO", vesselName, voyageNumber);

        if (!scheduleList.isEmpty()) {
            EdoValueObjectCargo schedule = scheduleList.get(0);
            log.info("Vessel schedule found: arrival={}, departure={}"+  schedule.getArrival()+ " " + schedule.getDeparture());

            map.put("arrival", schedule.getArrival());
            map.put("departure", schedule.getDeparture());
            map.put("cod_dttm", schedule.getCod_dttm());
            map.put("etb_dttm", schedule.getEtb_dttm());
            map.put("terminal", schedule.getTerminal());
        } else {
            map.put("arrival", "");
            map.put("departure", "");
            map.put("cod_dttm", "");
            map.put("etb_dttm", "");
            map.put("terminal", "");
        }
    }

    private String resolveBusinessError(BusinessException e) {
        String mapped = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP
                .get(CommonUtility.getExceptionMessage(e));
        return mapped != null ? mapped : e.getMessage();
    }

    private void handleError(Result result, Map<String, Object> map, String errorMessage) {
        map.put("errorMessage", errorMessage);
        result.setError(errorMessage);
        result.setErrors(map);
        result.setSuccess(false);
    }

}
