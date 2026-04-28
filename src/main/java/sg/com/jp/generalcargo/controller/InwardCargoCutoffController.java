package sg.com.jp.generalcargo.controller;

import java.util.ArrayList;
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
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.CutoffValueObject;
import sg.com.jp.generalcargo.domain.CuttoffEdoValueObject;
import sg.com.jp.generalcargo.domain.Result;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TopsModel;
import sg.com.jp.generalcargo.domain.vesselVoyObjectValue;
import sg.com.jp.generalcargo.service.CutoffService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtil;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.ConstantUtil;
import sg.com.jp.generalcargo.util.ResponseEntityUtil;

@CrossOrigin
@RestController
@RequestMapping(value = InwardCargoCutoffController.ENDPOINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InwardCargoCutoffController {

	public static final String ENDPOINT = "gc/inwardcargo/cutoff";
	public static final String API_VERSION_1 = "application/vnd.jurong.v1+json;charset=UTF-8";
	private String errorMessage = null;
	private static final Log log = LogFactory.getLog(InwardCargoCutoffController.class);

	@Autowired
	private CutoffService CutoffService;

	//delegate.helper.gbms.cargo.cutoff -->CutoffAddHandler
	
	@ApiOperation(value = "cutoffAdd", response = String.class)
	@PostMapping(value = "/cutoffAdd")
	public ResponseEntity<?> cutoffAdd(HttpServletRequest request) {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		errorMessage = null;
		try {
			log.info("** cutoffAdd Start criteria :" + criteria.toString());
			TopsModel topsModel=new TopsModel();

			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			// String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

			String strmode= "";
			// String funmode= "Add";

			if (criteria.getPredicates().get("strmode")!=null){
				strmode=  CommonUtility.deNull(criteria.getPredicates().get("strmode"));
			}


			//Cutoff EJB calling -------- Ramesh
			String varnbr="";
			String vesselname = "";
			String invoyage="";
			String blnbr="";
			String cargotype="";
			String hscode="";
			String cargodesc="";
			String cargomarking="";
			String cargostatus="";
			String edonumber="";
			String storageind="";
			String newnbrpkgs1="";
			String cutofftype="";
			String pkgtype="";
			
//			String consignee="";
//			String deliveryto="";
//			String distype="";
//			String disoperind="";

			if(strmode.equalsIgnoreCase("BLCUTOFFDELETE")){

				String mftnbr =  CommonUtility.deNull(criteria.getPredicates().get("mftnbr"));
				String cutoffno =  CommonUtility.deNull(criteria.getPredicates().get("cutoffno"));


				String deleted = CutoffService.deleteBlCutoff(mftnbr,cutoffno);
				map.put("strmode","BLCUTOFFDELETE");
				map.put("BLCutoffdeleted",deleted);
				//nextScreen(request, "cutoffAddSer");
			}

			if(strmode.equalsIgnoreCase("EDOCUTOFFDELETE")){

				String edoasnnbr =  CommonUtility.deNull(criteria.getPredicates().get("edoasnno"));
				String cutoffnbr =  CommonUtility.deNull(criteria.getPredicates().get("cutoffno"));


				String deleted = CutoffService.deleteEdoCutoff(edoasnnbr,cutoffnbr);
				map.put("strmode","EDOCUTOFFDELETE");
				map.put("EDOCutoffdeleted",deleted);
				//nextScreen(request, "cutoffAddSer");
			}

			if(strmode.equalsIgnoreCase("CUTOFFSAVE")){

				String struserid=coCd;

				varnbr= CommonUtility.deNull(criteria.getPredicates().get("varnbr"));
				vesselname= CommonUtility.deNull(criteria.getPredicates().get("vesselname"));
				invoyage= CommonUtility.deNull(criteria.getPredicates().get("invoyage"));
				blnbr= CommonUtility.deNull(criteria.getPredicates().get("blnbr"));
				cargotype= CommonUtility.deNull(criteria.getPredicates().get("cargotype"));
				hscode= CommonUtility.deNull(criteria.getPredicates().get("hscode"));
				cargodesc= CommonUtility.deNull(criteria.getPredicates().get("cargodesc"));
				cargomarking= CommonUtility.deNull(criteria.getPredicates().get("cargomarking"));
				cargostatus= CommonUtility.deNull(criteria.getPredicates().get("cargostatus"));
				edonumber= CommonUtility.deNull(criteria.getPredicates().get("edonumber"));
				storageind= CommonUtility.deNull(criteria.getPredicates().get("storageind"));
				cutofftype= CommonUtility.deNull(criteria.getPredicates().get("cutofftype"));
				pkgtype= CommonUtility.deNull(criteria.getPredicates().get("pkgtype"));
//				consignee= CommonUtility.deNull(criteria.getPredicates().get("consignee"));
				newnbrpkgs1= CommonUtility.deNull(criteria.getPredicates().get("newnbrpkgs"));


				String cutoffValue = CutoffService.saveCutoffDetails(varnbr, vesselname, invoyage, blnbr,
						cargotype, hscode, cargodesc, cargomarking, cargostatus,
						edonumber, storageind,cutofftype,pkgtype,newnbrpkgs1,struserid);

				map.put("cutoffvalue",cutoffValue);
				map.put("strmode","CUTOFFSAVE");
				//nextScreen(request, "cutoffAddSer");
			}


			if(strmode.equalsIgnoreCase("EDOCUTOFFSAVE")){

				String struserid= coCd;

				varnbr= CommonUtility.deNull(criteria.getPredicates().get("varnbr"));
				vesselname= CommonUtility.deNull(criteria.getPredicates().get("vesselname"));
				invoyage= CommonUtility.deNull(criteria.getPredicates().get("invoyage"));
				blnbr= CommonUtility.deNull(criteria.getPredicates().get("blnbr"));
				cargotype= CommonUtility.deNull(criteria.getPredicates().get("cargotype"));
				hscode= CommonUtility.deNull(criteria.getPredicates().get("hscode"));
				cargodesc= CommonUtility.deNull(criteria.getPredicates().get("cargodesc"));
				cargomarking= CommonUtility.deNull(criteria.getPredicates().get("cargomarking"));
				cargostatus= CommonUtility.deNull(criteria.getPredicates().get("cargostatus"));
				edonumber= CommonUtility.deNull(criteria.getPredicates().get("edonr"));
				storageind= CommonUtility.deNull(criteria.getPredicates().get("storageind"));
				cutofftype= CommonUtility.deNull(criteria.getPredicates().get("cutofftype"));
				pkgtype= CommonUtility.deNull(criteria.getPredicates().get("pkgtype"));
//				consignee= CommonUtility.deNull(criteria.getPredicates().get("consignee"));
				newnbrpkgs1= CommonUtility.deNull(criteria.getPredicates().get("newnbrpkgs"));


				String cutoffValue = CutoffService.saveEdoCutoffDetails(varnbr, vesselname, invoyage, blnbr,
						cargotype, hscode, cargodesc, cargomarking, cargostatus,
						edonumber, storageind,cutofftype,pkgtype,newnbrpkgs1,struserid);

				map.put("edocutoffvalue",cutoffValue);
				map.put("strmode","EDOCUTOFFSAVE");
				//nextScreen(request, "cutoffAddSer");
			}


			//Cutoff EJB calling -------- Ramesh


			//Get BL Cutoff Details ----- Ramesh
			if (strmode.equalsIgnoreCase("ONE")){
				//getBLDisplay(request,map);
				
				List<CuttoffEdoValueObject> blnbrvector1 = new ArrayList<CuttoffEdoValueObject>();
				List<List<String>> blnbrvector = new ArrayList<List<String>>();
				String strblnbr= "";
				String strvarnbr= "";
				String mode = "N";
				strvarnbr= CommonUtility.deNull(criteria.getPredicates().get("varnbr"));

				blnbrvector1=CutoffService.getBLNbrList(strvarnbr,"ADD");

				if(blnbrvector1.size() > 0) {
					if (criteria.getPredicates().get("blnbr") ==null ){
						CuttoffEdoValueObject edoValueObject1 =new CuttoffEdoValueObject();
						edoValueObject1=(CuttoffEdoValueObject)blnbrvector1.get(0);
						strblnbr=edoValueObject1.getMftSeqNbr();
						mode = "Y";
					}else{
						strblnbr= CommonUtility.deNull(criteria.getPredicates().get("blnbr"));
					}
				}
				for (int i=0;i<blnbrvector1.size();i++)
				{
					CuttoffEdoValueObject edoValueObject1 =new CuttoffEdoValueObject();
					edoValueObject1=blnbrvector1.get(i);
					List<String> blnbrarraylist= new ArrayList<String>();
					blnbrarraylist.add(edoValueObject1.getBlNbr());
					blnbrarraylist.add(edoValueObject1.getMftSeqNbr());
					blnbrvector.add(blnbrarraylist);
				}
				map.put("blnbrvector",blnbrvector);

				List<CuttoffEdoValueObject> blnbrlistvector=new ArrayList<CuttoffEdoValueObject>();
				blnbrlistvector= CutoffService.getBLDetails(strblnbr);

				CuttoffEdoValueObject edoValueObject = new CuttoffEdoValueObject();
				for (int i=0;i<blnbrlistvector.size();i++)
				{
					
					edoValueObject=blnbrlistvector.get(i);
					if(mode.equals("Y"))
						edoValueObject.setNbrPkgs("0");
					
				}
				topsModel.put(edoValueObject);
				map.put("listData", topsModel);
				map.put("strmode","ONE");
			}
			//Get BL Cutoff Details ----- Ramesh

			//Get Edo Cutoff Details ----- Ramesh
			if (strmode.equalsIgnoreCase("EDOCUTOFF")){
				getEdoCutoffDisplay(request,map);
				map.put("strmode","EDOCUTOFF");
			}

			if (strmode.equalsIgnoreCase("VIEWEDO"))
			{
				String edoasnnbr="";
				if (criteria.getPredicates().get("edoasnnbr")!=null)
				{
					edoasnnbr= CommonUtility.deNull(criteria.getPredicates().get("edoasnnbr"));
				}
				viewEdoDetails(request,edoasnnbr,map);
				map.put("strmode","VIEWEDO");
			}


		} catch (BusinessException be) {
			log.info("Exception cutoffAdd: ", be);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception cutoffAdd : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
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
				log.info("END: cutoffAdd result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	public ResponseEntity<?> getBLDisplay(HttpServletRequest request, Map<String, Object> map) throws BusinessException {

		TopsModel topsModel = new TopsModel();
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		
		try{	
			log.info("*******getBLDisplay Start*****"+criteria.toString());
			List<CuttoffEdoValueObject> blnbrvector1 = new ArrayList<CuttoffEdoValueObject>();
			List<List<String>> blnbrvector = new ArrayList<List<String>>();
			String strblnbr= "";
			String strvarnbr= "";
			String mode = "N";
			strvarnbr= CommonUtility.deNull(criteria.getPredicates().get("varnbr"));

			blnbrvector1=CutoffService.getBLNbrList(strvarnbr,"ADD");

			if (criteria.getPredicates().get("blnbr") ==null ){
				CuttoffEdoValueObject edoValueObject1 =new CuttoffEdoValueObject();
				edoValueObject1=(CuttoffEdoValueObject)blnbrvector1.get(0);
				strblnbr=edoValueObject1.getMftSeqNbr();
				mode = "Y";
			}else{
				strblnbr= CommonUtility.deNull(criteria.getPredicates().get("blnbr"));
			}
			for (int i=0;i<blnbrvector1.size();i++)
			{
				CuttoffEdoValueObject edoValueObject1 =new CuttoffEdoValueObject();
				edoValueObject1=(CuttoffEdoValueObject)blnbrvector1.get(i);
				List<String> blnbrarraylist= new ArrayList<String>();
				blnbrarraylist.add(edoValueObject1.getBlNbr());
				blnbrarraylist.add(edoValueObject1.getMftSeqNbr());
				blnbrvector.add(blnbrarraylist);
			}
			map.put("blnbrvector",blnbrvector);

			List<CuttoffEdoValueObject> blnbrlistvector=new ArrayList<CuttoffEdoValueObject>();
			blnbrlistvector= CutoffService.getBLDetails(strblnbr);

			CuttoffEdoValueObject edoValueObject = new CuttoffEdoValueObject();
			for (int i=0;i<blnbrlistvector.size();i++)
			{
				
				edoValueObject=blnbrlistvector.get(i);
				if(mode.equals("Y"))
					edoValueObject.setNbrPkgs("0");
				
			}
			topsModel.put(edoValueObject);
			map.put("listData",edoValueObject);
			map.put("listData", topsModel);
		} catch (BusinessException be) {
			log.info("Exception getBLDisplay: ", be);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception getBLDisplay : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
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
				log.info("END: getBLDisplay result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	
	}

	public ResponseEntity<?> getEdoCutoffDisplay(HttpServletRequest request, Map<String, Object> map) throws BusinessException {

		TopsModel topsModel = new TopsModel();
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();

		try{
			log.info("*******getEdoCutoffDisplay Start*****"+criteria.toString());

			List<CuttoffEdoValueObject> blnbrvector1 = new ArrayList<CuttoffEdoValueObject>();
			List<List<String>> blnbrvector = new ArrayList<List<String>>();
			String strblnbr= "";
			String strvarnbr= "";
			String mode = "N";
			strvarnbr= CommonUtility.deNull(criteria.getPredicates().get("varnbr"));


			blnbrvector1=CutoffService.getBLNbrList(strvarnbr,"ADD");
			if(blnbrvector1.size() > 0) {
				if (criteria.getPredicates().get("blnbr") ==null ){
					CuttoffEdoValueObject edoValueObject1 =new CuttoffEdoValueObject();
					edoValueObject1=blnbrvector1.get(0);
					strblnbr=edoValueObject1.getMftSeqNbr();
					mode = "Y";
				}else{
					strblnbr= CommonUtility.deNull(criteria.getPredicates().get("blnbr"));
				}
			}

			//the variable strblnbr stores the mft seq nbr - exceptional case

			for (int i=0;i<blnbrvector1.size();i++)
			{
				CuttoffEdoValueObject edoValueObject1 =new CuttoffEdoValueObject();
				edoValueObject1=(CuttoffEdoValueObject)blnbrvector1.get(i);
				List<String> blnbrarraylist= new ArrayList<String>();
				blnbrarraylist.add(edoValueObject1.getBlNbr());
				blnbrarraylist.add(edoValueObject1.getMftSeqNbr());
				blnbrvector.add(blnbrarraylist);
			}
			map.put("blnbrvector",blnbrvector);

			String edonumber="";
			String mode_edonbr ="Y";
			edonumber= CommonUtility.deNull(criteria.getPredicates().get("edonr"));
			List<CuttoffEdoValueObject> blnbrlistvector=new ArrayList<CuttoffEdoValueObject>();
			List<CuttoffEdoValueObject> blnbrlistvector_nbr = new ArrayList<CuttoffEdoValueObject>();
			if(edonumber != null && !edonumber.equals("") && !edonumber.equals("select") && !edonumber.equals("NA")){
				blnbrlistvector_nbr = CutoffService.getEdoBLDetails(edonumber);
				mode_edonbr = "N";
			}
			blnbrlistvector= CutoffService.getBLDetails(strblnbr);
			if(blnbrlistvector.size()>0){
				for (int i=0;i<blnbrlistvector.size();i++)
				{
					CuttoffEdoValueObject edoValueObject = new CuttoffEdoValueObject();
					edoValueObject=  blnbrlistvector.get(i);
					if(blnbrlistvector_nbr.size() > 0){
						edoValueObject=blnbrlistvector_nbr.get(0);
						mode = "N";
					}

					if(mode.equals("Y"))
						edoValueObject.setNbrPkgs("0");

					if(mode_edonbr.equals("Y"))
						edoValueObject.setNbrPkgs("0");
					topsModel.put(edoValueObject);
				}
			}
			map.put("listData", topsModel);

			//edo cutoff details query retrive from cutoff cargo ejb
			List<CutoffValueObject> edoasn = new ArrayList<CutoffValueObject>();
			//if(strblnbr != null && !strblnbr.equals("select"))
			if(mode.equals("N"))
				edoasn = CutoffService.getEdoNbr(strblnbr, strvarnbr); //This will fetch edo-asn no. value from gb_edo table
			//the variable strblnbr stores the mft seq nbr - exceptional case
			map.put("strmode","EDOCUTOFF");
			map.put("edonbrs",edoasn);
			//edo cutoff details query retrive from cutoff cargo ejb
		} catch (BusinessException be) {
			log.info("Exception getEdoCutoffDisplay: ", be);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception getEdoCutoffDisplay : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
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
				log.info("END: getEdoCutoffDisplay result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}

	public ResponseEntity<?> viewEdoDetails(HttpServletRequest request, String edoasnnbr, Map<String, Object> map) throws BusinessException {

		TopsModel topsModel = new TopsModel();
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		try{
			log.info("*******viewEdoDetails Start*****"+criteria.toString()+" edoasnnbr:"+CommonUtility.deNull(edoasnnbr)+" map:"+map );

			List<CutoffValueObject> edoviewlistvector=new ArrayList<CutoffValueObject>();
			int index = Integer.parseInt(criteria.getPredicates().get("VarcodeIndex"));
			int cutoffno=Integer.parseInt(criteria.getPredicates().get("cutoffno"+index));
		
			String cutofftype= CommonUtility.deNull(criteria.getPredicates().get("cutofftype"+index));
			String cutoffqty= CommonUtility.deNull(criteria.getPredicates().get("cutoffqty"+index));
			String blno= CommonUtility.deNull(criteria.getPredicates().get("blno"+index));
//			String totalpkgs= CommonUtility.deNull(criteria.getPredicates().get("totalpkgs"+index));
			String varnbr= CommonUtility.deNull(criteria.getPredicates().get("varnbr"));
//			String vesselname= CommonUtility.deNull(criteria.getPredicates().get("vesselname"));

			map.put("cutoffno",Integer.toString(cutoffno));
			map.put("blno",blno);
		
			map.put("cutofftype",cutofftype);
			map.put("cutoffqty",cutoffqty);
			
			String edoasnno = "";
			if (criteria.getPredicates().get("edoasnno"+index)!=null && !criteria.getPredicates().get("edoasnno"+index).equalsIgnoreCase("-NA-")){
				edoasnno= CommonUtility.deNull(criteria.getPredicates().get("edoasnno"+index));
			}
			map.put("edoasnno",edoasnno);
			
			edoviewlistvector=CutoffService.viewEdoDetails(blno, varnbr, edoasnno, cutoffno);
			CutoffValueObject cutoffValueObject = new CutoffValueObject();
			cutoffValueObject = edoviewlistvector.get(0);
			map.put("cutoffno",Integer.toString(cutoffno));
			topsModel.put(cutoffValueObject);
			map.put("listData", topsModel);
		
		} catch (BusinessException be) {
			log.info("Exception viewEdoDetails: ", be);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception viewEdoDetails : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
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
				log.info("END: viewEdoDetails result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}
	
	@ApiOperation(value = "cutoffVesselVoyage", response = String.class)
	@PostMapping(value = "/cutoffVesselVoyage")
	public ResponseEntity<?> cutoffVesselVoyage(HttpServletRequest request) {
		Criteria criteria = CommonUtil.getCriteria(request);
		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		TableResult tableResult = new TableResult();
		errorMessage = null;
		try {
			log.info("** perform Start criteria :" + criteria.toString());
			TopsModel topsModel=new TopsModel();

			String strmodulecd="EDO";
			if (request.getParameter("modulecd")!=null)
			{
				strmodulecd= CommonUtility.deNull(criteria.getPredicates().get("modulecd"));
			}
			String coCd = CommonUtility.deNull(criteria.getPredicates().get("companyCode"));
			String search = CommonUtility.deNull(criteria.getPredicates().get("search")).toUpperCase();
			// String UserID = CommonUtility.deNull(criteria.getPredicates().get("userAccount"));

	
			List<vesselVoyObjectValue> vesselvoyagevector = new ArrayList<vesselVoyObjectValue>();
//			vesselvoyagevector=CutoffService.getVslVoyNbrList(coCd,strmodulecd);
			
	
			String strvarnbr= "";

			if ( criteria.getPredicates().get("varnbr") == null ){
				
				vesselvoyagevector=CutoffService.getVslVoyNbrList(coCd,strmodulecd, search);
				
				for (int i=0;i<vesselvoyagevector.size();i++)
				{
					vesselVoyObjectValue vesselVoyObjectValue = new vesselVoyObjectValue();
					vesselVoyObjectValue=vesselvoyagevector.get(i);
					topsModel.put(vesselVoyObjectValue);
				}
				
				map.put("listData", topsModel);
				
			}else{
				strvarnbr=CommonUtility.deNull(criteria.getPredicates().get("varnbr"));
				//retrive  cutoff values from cutoff table ---- Ramesh


				//retrive  cutoff values from cutoff table ---- Ramesh


//				Vector edovesselvoyagevector=new Vector();
//				
//				vesselvoyagevector=CutoffService.getVslVoyNbrList(coCd,strmodulecd);
//				
//				for (int i=0;i<vesselvoyagevector.size();i++ )
//				{
//					vesselVoyObjectValue vesselVoyObjectValue1 =new vesselVoyObjectValue();
//					vesselVoyObjectValue1=(vesselVoyObjectValue)vesselvoyagevector.get(i);
//					edovesselvoyagevector.addElement(vesselVoyObjectValue1.getVarNbr());
//					edovesselvoyagevector.addElement(vesselVoyObjectValue1.getVslNbr());
//					edovesselvoyagevector.addElement(vesselVoyObjectValue1.getInVoyNbr());
//					edovesselvoyagevector.addElement(vesselVoyObjectValue1.getTerminal());
//				}
//
//				map.put("edovesselvoyagevector",edovesselvoyagevector);
//
//				//////////////start
//				vesselVoyObjectValue vesselVoyObjectValue = new vesselVoyObjectValue();
				int total = 0;
				List<CutoffValueObject> edolistvector = new ArrayList<CutoffValueObject>();
				
				String changelist="";
				if ( criteria.getPredicates().get("changelist") !=null ){
					changelist= CommonUtility.deNull(criteria.getPredicates().get("changelist"));
				}
				
				if ((changelist.equalsIgnoreCase("NEW"))) {
					tableResult =CutoffService.getEdoLst(coCd,strvarnbr,strmodulecd, criteria); //value from cutoff table ***** Ramesh
//					edolistvector = tableResult.getData().getListData().getTopsModel();
//					total = tableResult.getData().getTotal();
					map.put("listData", tableResult.getData().getListData().getTopsModel());
					map.put("total", tableResult.getData().getTotal());
				} else {
					map.put("listData", edolistvector);
					map.put("total", total);
				}
			}
	
		} catch (BusinessException be) {
			log.info("Exception cutoffVesselVoyage: ", be);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get(CommonUtility.getExceptionMessage(be));
			if(errorMessage == null) {
				errorMessage = be.getMessage();
			}		
		} catch (Exception e) {
			log.info("Exception cutoffVesselVoyage : ", e);
			errorMessage = ConstantUtil.INWARD_CARGO_ERROR_CONSTANT_MAP.get("M4201");
		} finally {
			if (errorMessage != null) {
				map.put("errorMessage", errorMessage);
				result = new Result();
				result.setErrors(map);
				result.setError(errorMessage);
				result.setSuccess(false);

			} else {
				result.setData(map);
				result.setSuccess(true);
				log.info("END: cutoffVesselVoyage result: " + result.toString());
			}
		}
		return ResponseEntityUtil.success(result.toString());
	}
}
