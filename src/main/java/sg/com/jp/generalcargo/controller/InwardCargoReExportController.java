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
import sg.com.jp.generalcargo.domain.ReExportValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.service.ReExportService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = InwardCargoReExportController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InwardCargoReExportController {

	public static final String ENDPOINT = "gc/inwardcargo/reexport";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(InwardCargoReExportController.class);

	@Autowired
	private ReExportService reExportService;

	
	//delegate.helper.gbms.cargo.reexport --> ReExportListHandler
		@PostMapping(value = "/ReExportList")
		public ResponseEntity<?> ReExportList(HttpServletRequest request) throws BusinessException {

			Criteria criteria = CommonUtil.getCriteria(request);
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> mapError = new HashMap<String, Object>();
			Result result = new Result();
			errorMessage = null;
			try {
				log.info("START: ReExportList criteria:" + criteria.toString());

				TopsModel topsModel = new TopsModel();

				//String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
				String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

				String selVoyno = "";
				int inop = 0; double dgwt = 0.0; double dvol = 0.0;

				try {
					selVoyno = CommonUtility.deNull(criteria.getPredicates().get("vslName"));
					//String vslnew = CommonUtility.deNull(criteria.getPredicates().get("vslnew"));
				}
				catch(Exception e){
					log.info("Exception ReExportList: ", e);
					throw new BusinessException("M4201");
				}

				List<ReExportValueObject> mftlist = new ArrayList<ReExportValueObject>(); 
				List<ReExportValueObject> mftlis = new ArrayList<ReExportValueObject>();
				List<String> blnolist = new ArrayList<String>(); 
				List<String> crglist = new ArrayList<String>(); 
				List<String> npkglist = new ArrayList<String>(); 
				List<String> gwtlist = new ArrayList<String>(); 
				List<String> gvolist = new ArrayList<String>(); 
				List<String> seqnolist = new ArrayList<String>(); 
				List<String> crgstatlist = new ArrayList<String>(); 
				List<String> reexpnbrlist = new ArrayList<String>(); 
				List<String> edonbrpkgslist = new ArrayList<String>(); 
				List<String> terminalList = new ArrayList<String>(); 
				List<String> schemeList = new ArrayList<String>(); 
				List<String> subSchemeList = new ArrayList<String>(); 
				List<String> gcOperationsList = new ArrayList<String>(); 

				List<ReExportValueObject> vesselCallList = reExportService.getVesselVoy(coCd);


				for(int i=0;i<vesselCallList.size();i++) {
					ReExportValueObject vvvObj = new ReExportValueObject();
					vvvObj =  vesselCallList.get(i);
					topsModel.put(vvvObj);
				}
				map.put("selVoyno",selVoyno);

				

				if(selVoyno!=null && !selVoyno.equals("")) {
					// changed by Irene Tan on 14 July 2004 :
					//mftlis = mftrem.getManifestList(selVoyno,UserID);
					mftlis = reExportService.getManifestList(selVoyno,coCd,criteria);
					// end changed by Irene Tan on 14 July 2004
					for (int i=0;i<mftlis.size();i++) {
						ReExportValueObject mftvObj = new ReExportValueObject();
						mftvObj =  mftlis.get(i);

						inop = inop + Integer.valueOf(mftvObj.getNoofPkgs()).intValue();
						dgwt = dgwt + Double.valueOf(mftvObj.getGrWt()).doubleValue();
						dvol = dvol + Double.valueOf(mftvObj.getGrMsmt()).doubleValue();
					}//for
				}//if


			if (selVoyno != null && !selVoyno.equals("")) {

				mftlist = reExportService.getManifestList(selVoyno, coCd, criteria);
				for (int i = 0; i < mftlist.size(); i++) {
					ReExportValueObject mftvObj = new ReExportValueObject();
					mftvObj =  mftlist.get(i);
					blnolist.add((String) mftvObj.getBlNo());
					crglist.add((String) mftvObj.getCrgDesc());
					npkglist.add((String) mftvObj.getNoofPkgs());
					gwtlist.add((String) mftvObj.getGrWt());
					gvolist.add((String) mftvObj.getGrMsmt());
					seqnolist.add((String) mftvObj.getSeqNo());
					crgstatlist.add((String) mftvObj.getCrgStatus());
					reexpnbrlist.add((String) mftvObj.getReExpNbr());
					edonbrpkgslist.add((String) mftvObj.getEdoNbrPkgs());
					terminalList.add((String) mftvObj.getTerminal());
					schemeList.add((String) mftvObj.getScheme());
					subSchemeList.add((String) mftvObj.getSubScheme());
					gcOperationsList.add((String) mftvObj.getGcOperations());

				}
			}

			   int totalCount = reExportService.getManifestListCount(selVoyno, coCd, criteria);
				map.put("blnolist",blnolist);
				map.put("crglist",crglist);
				map.put("npkglist",npkglist);
				map.put("gwtlist",gwtlist);
				map.put("gvolist",gvolist);
				map.put("seqnolist",seqnolist);
				map.put("crgstatlist",crgstatlist);
				map.put("reexpnbrlist",reexpnbrlist);
				map.put("edonbrpkgslist",edonbrpkgslist);
				map.put("terminalList",terminalList);
				map.put("schemeList",schemeList);
				map.put("subSchemeList",subSchemeList);
				map.put("gcOperationsList",gcOperationsList);

				map.put("usrtyp",coCd);
				// log.info("coCd**22222******"+coCd);
				map.put("tnop",""+inop);
				map.put("tgwt",""+dgwt);
				map.put("tvol",""+dvol);

				map.put("ListData", topsModel);
				map.put("total", totalCount);

			} catch (BusinessException be) {
				log.info("Exception ReExportList: ", be);
				errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
				if(errorMessage == null) {
					errorMessage = be.getMessage();
				}		
			} catch (Exception e) {
				log.info("Exception ReExportList : ", e);
				errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
			} finally {
				if (errorMessage != null) {
					mapError.put("errorMessage", errorMessage);
					result = new Result();
					result.setErrors(mapError);
					result.setSuccess(false);
					result.setData(map);

				} else {
					result.setData(map);
					result.setSuccess(true);
					log.info("END: ReExportList result: " + result.toString());
				}
			}
			return ResponseEntityUtil.success(result.toString());
		}



	//delegate.helper.gbms.cargo.reexport --> ReExportUpdateHandler
	@PostMapping(value = "/ReExportUpdate")
	public ResponseEntity<?> ReExportUpdate(HttpServletRequest request) throws BusinessException {

		Criteria criteria = CommonUtil.getCriteria(request);
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapError = new HashMap<String, Object>();
		Result result = new Result();
		errorMessage = null;
		try {
			log.info("START: ReExportUpdate criteria:" + criteria.toString());

			TopsModel topsModel = new TopsModel();

			//			String UserId = "JPUSER";
			//			String coCd = "JP";

			String UserId = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));

			String scrmode="";

			if (criteria.getPredicates().get("scrmode")!=null)
			{
				scrmode=(String)criteria.getPredicates().get("scrmode");
			}
			if (scrmode.equalsIgnoreCase("LIST"))
			{
				String mftSeqNo="";
				if (criteria.getPredicates().get("SeqNo")!=null)
				{
					mftSeqNo=(String)criteria.getPredicates().get("SeqNo");
				}
				// log.info("mftSeqNo :"+mftSeqNo);
				String newmftseqnbr=reExportService.checkReExportStatus(mftSeqNo,coCd);
				StringTokenizer st1 = new StringTokenizer (newmftseqnbr,"|");
				newmftseqnbr=st1.nextToken();
				String crgstatus=st1.nextToken();
				// log.info("newmftseqnbr :"+newmftseqnbr);
				if (newmftseqnbr.length()==7){
					newmftseqnbr="0".concat(newmftseqnbr);
				}
				if (newmftseqnbr.length()==6){
					newmftseqnbr="00".concat(newmftseqnbr);
				}
				if (newmftseqnbr.length()==5){
					newmftseqnbr="000".concat(newmftseqnbr);
				}
				if (newmftseqnbr.length()==4){
					newmftseqnbr="0000".concat(newmftseqnbr);
				}
				if (newmftseqnbr.length()==3){
					newmftseqnbr="00000".concat(newmftseqnbr);
				}
				if (newmftseqnbr.length()==2){
					newmftseqnbr="000000".concat(newmftseqnbr);
				}
				if (newmftseqnbr.length()==1){
					newmftseqnbr="0000000".concat(newmftseqnbr);
				}
				if (!(newmftseqnbr.equalsIgnoreCase(mftSeqNo)))
				{
					errorMessage="Cannot apply for Re-Export";
				}
				map.put("crgstatus",crgstatus);
				if (crgstatus.equalsIgnoreCase("R"))
				{
					String reexpnbr=st1.nextToken();
					String reexpdttm=st1.nextToken();
					String reexportdetails=reexpnbr+"|"+reexpdttm;
					map.put("reexportdetails",reexportdetails);
				}

			}
			if (scrmode.equalsIgnoreCase("UPDATE"))
			{		
				String mftSeqNo="";
				if (criteria.getPredicates().get("SeqNo")!=null)
				{
					mftSeqNo=(String)criteria.getPredicates().get("SeqNo");
				}
				String PortL="";
				if (criteria.getPredicates().get("PortL")!=null)
				{
					PortL=(String)criteria.getPredicates().get("PortL");
				}

				// log.info("mftSeqNo : "+mftSeqNo);
				boolean portcdl = reExportService.chkPortCode(PortL);
				if (!portcdl)
				{
					log.info("Writing from ReExportEjb.updateReExportDetails");
					log.info("Invalid Port Code " + PortL);
					throw new BusinessException("Invalid Loading Port.");
				}
				String reexportdetails=reExportService.updateReExportDetails(mftSeqNo,PortL,UserId,coCd);
				map.put("reexportdetails",reexportdetails);
			}
			map.put("scrmode",scrmode);

			map.put("ListData", topsModel);

		} catch (BusinessException be) {
			log.info("Exception ReExportUpdate: ", be);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception ReExportUpdate : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				mapError.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(mapError);
				result.setSuccess(false);
				result.setData(map);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: ReExportUpdate result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}
}
