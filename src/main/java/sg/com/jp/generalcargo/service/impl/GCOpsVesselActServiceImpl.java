package sg.com.jp.generalcargo.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import sg.com.jp.generalcargo.dao.VesselActRepo;
import sg.com.jp.generalcargo.domain.BillingCodesVO;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EmailValueObject;
import sg.com.jp.generalcargo.domain.GroupVO;
import sg.com.jp.generalcargo.domain.IMessageValueObject;
import sg.com.jp.generalcargo.domain.LineTowedVesselValueObject;
import sg.com.jp.generalcargo.domain.OSDExemptionClauses;
import sg.com.jp.generalcargo.domain.OSDReviewObject;
import sg.com.jp.generalcargo.domain.OpsValueObject;
import sg.com.jp.generalcargo.domain.OverStayDockageValueObject;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselActValueObject;
import sg.com.jp.generalcargo.domain.WaiverCodesVO;
import sg.com.jp.generalcargo.service.GCOpsVesselActService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.dao.ClvsOpsRepo;
import sg.com.jp.generalcargo.dao.LineTowedVesselRepo;
import sg.com.jp.generalcargo.dao.MaintWaiverRangeRepo;
import sg.com.jp.generalcargo.dao.OvrstyWavrOpsRepo;
import sg.com.jp.generalcargo.dao.ProcessMarRepo;
import sg.com.jp.generalcargo.dao.VslProdlistRepo;

@Service
public class GCOpsVesselActServiceImpl implements GCOpsVesselActService {

	@Autowired
	private VesselActRepo vesselActRepo;

	@Autowired
	private VslProdlistRepo vslProdlistRepo;

	@Autowired
	private LineTowedVesselRepo lineTowedOpsRepo;

	@Autowired
	private ClvsOpsRepo clvsOpsRepo;

	@Autowired
	private OvrstyWavrOpsRepo ovrstyWavrOpsRepo;

	@Autowired
	private ProcessMarRepo processMarRepo;

	@Autowired
	private MaintWaiverRangeRepo maintWaiverRangeRepo;

	@Override
	public void addDockage(String vvcd, List<LineTowedVesselValueObject> co) throws BusinessException {
		lineTowedOpsRepo.addDockage(vvcd, co);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public OpsValueObject getVesselInfo(String vvCode, OpsValueObject opsValueObject) throws BusinessException {
		return clvsOpsRepo.getVesselInfo(vvCode, opsValueObject);
	}

	@Override
	public List<VesselActValueObject> getVesselActShiftList(String strCustCode, String strvvcd)
			throws BusinessException {
		return vesselActRepo.getVesselActShiftList(strCustCode, strvvcd);
	}

	@Override
	public List<LineTowedVesselValueObject> getDockageList(String vvcd) throws BusinessException {
		return lineTowedOpsRepo.getDockageList(vvcd);
	}

	@Override
	public List<String> getWaiverBillingList(String strvvcd) throws BusinessException {
		return vesselActRepo.getWaiverBillingList(strvvcd);
	}

	@Override
	public List<VesselActValueObject> getVesselList(String name) throws BusinessException {
		return vesselActRepo.getVesselList(name);
	}

	@Override
	public Map<String, Object> getVesselScheme() throws BusinessException {
		return vslProdlistRepo.getVesselScheme();
	}

	@Override
	public TableResult getVesselActList(String coCd, String vslnm, String vvcd, String atbFrom, String atbTo,
			String atuFrom, String atuTo, String schemdCd, Criteria criteria) throws BusinessException {
		return vesselActRepo.getVesselActList(coCd, vslnm, vvcd, atbFrom, atbTo, atuFrom, atuTo, schemdCd, criteria);
	}

	@Override
	public void updateWaiverDetails(String strwaivercd, String strwaiverreason, String strwaiverstatus,
			String struserids, String vvcd) throws BusinessException {
		vesselActRepo.updateWaiverDetails(strwaivercd, strwaiverreason, strwaiverstatus, struserids, vvcd);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateBillDetails(String strbillcd, String struserids, String vvcd) throws BusinessException {
		vesselActRepo.updateBillDetails(strbillcd, struserids, vvcd);
	}

	@Override
	public OverStayDockageValueObject getWaiverStatus(String vvcd) throws BusinessException {
		return ovrstyWavrOpsRepo.getWaiverStatus(vvcd);
	}

	@Override
	public Map<String, Object> determineOverStayAndAmount(String vvcd) throws BusinessException {
		return processMarRepo.determineOverStayAndAmount(vvcd);
	}

	@Override
	public GroupVO getLevelId(String strAmount) throws BusinessException {
		return maintWaiverRangeRepo.getLevelId(strAmount);
	}

	@Override
	public boolean hasAccesstoOSD(String userID) throws BusinessException {
		return ovrstyWavrOpsRepo.hasAccesstoOSD(userID);
	}

	@Override
	public OpsValueObject getVessels(OpsValueObject opsValueObject) throws BusinessException {
		return clvsOpsRepo.getVessels(opsValueObject);
	}

	@Override
	public List<String> getWaiverList(String vvcd, String strwaiverstatus) throws BusinessException {
		return vesselActRepo.getWaiverList(vvcd, strwaiverstatus);
	}

	@Override
	public String getCodColStatus(String vvcd) throws BusinessException {
		return vesselActRepo.getCodColStatus(vvcd);
	}

	@Override
	public String getBillList(String vvcd) throws BusinessException {
		return vesselActRepo.getBillList(vvcd);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void updateVesselActivityShift(String strvvstatus,
			String[] stratbdttm, String[] stratudttm, 
			String strcoddttm, String strcoldttm, 
			String strbcoddttm, String strbcoldttm, 
			String strdiscdttm, String strloaddttm, 
			String struserid,	String strvvcd,
			int intarrsize, String strfgcdttm,
			String strtotgencargoactivity)
			throws BusinessException {
		vesselActRepo.updateVesselActivityShift(strvvstatus,
				stratbdttm, stratudttm, 
				strcoddttm, strcoldttm, 
				strbcoddttm, strbcoldttm, 
				strdiscdttm, strloaddttm, 
				struserid,	strvvcd,
				intarrsize, strfgcdttm,
				strtotgencargoactivity);
	}

	@Override
	public void updateVesselActivity(String strvvstatus,String stratbdttm,
			String stratudttm, String strcoddttm,
			String strcoldttm, String strbcoddttm,
            String strbcoldttm, String strdiscdttm,
			String strloaddttm, String struserid,
            String strvvcd, String strfgcdttm,
            String strtotgencargoactivity) throws BusinessException {
		vesselActRepo.updateVesselActivity(strvvstatus, stratbdttm,
				 stratudttm,  strcoddttm,
				 strcoldttm,  strbcoddttm,
                 strbcoldttm,  strdiscdttm,
				 strloaddttm,  struserid,
                 strvvcd,  strfgcdttm,
                 strtotgencargoactivity);
	}

	@Override
	public void updateVesselActStatus(String strvvstatus, String vvcd, String struserid) throws BusinessException {
		vesselActRepo.updateVesselActStatus(strvvstatus, vvcd, struserid);
	}

	@Override
	public String checkShipStore(String vvcd) throws BusinessException {
		return vesselActRepo.checkShipStore(vvcd);
	}

	@Override
	public int checkImportCntr(String vvcd) throws BusinessException {
		return vesselActRepo.checkImportCntr(vvcd);
	}

	@Override
	public int checkExportCntr(String vvcd) throws BusinessException {
		return vesselActRepo.checkExportCntr(vvcd);
	}

	@Override
	public List<WaiverCodesVO> getWaiverCodes(WaiverCodesVO waiverCodesVO) throws BusinessException {
		return ovrstyWavrOpsRepo.getWaiverCodes(waiverCodesVO);
	}

	@Override
	public List<BillingCodesVO> getBillingReasons(BillingCodesVO billingCodesVO) throws BusinessException {
		return ovrstyWavrOpsRepo.getBillingReasons(billingCodesVO);
	}

	@Override
	public Map<String, String> getVesselDataForKmf(String vvcd) throws BusinessException {
		return vesselActRepo.getVesselDataForKmf(vvcd);
	}

	@Override
	public Timestamp getSysDate() throws BusinessException {
		return vesselActRepo.getSysDate();
	}
	@Override
	public List<OSDReviewObject> getOsdReviewList(String vvCd) throws BusinessException{
		return vesselActRepo.getOsdReviewList(vvCd);
	}
	@Override
	public int updateOsdReview(
	        String vvcd,String osdExemptionCodeList,String lateArrivalExemptionList,String osdReviewOption,String lateArrivalReviewOption,
	        String useraccount,String submitInd,String actualOsdFilesName,String actuallateFilesName, Map<String, String> encryptedfiles
	) throws BusinessException{
		return vesselActRepo.updateOsdReview( vvcd, osdExemptionCodeList, lateArrivalExemptionList,     
		         osdReviewOption, lateArrivalReviewOption,
		         useraccount, submitInd,actualOsdFilesName, actuallateFilesName, encryptedfiles );
	}
	@Override
	public Long queryOsd(String vvcd, boolean lateQ, boolean osdQ, String queryRemarks, String useraccount) throws BusinessException {
		return vesselActRepo.queryOsd( vvcd,  lateQ,  osdQ,  queryRemarks,  useraccount);
	}
	@Override
	public List<OSDExemptionClauses> getLateArrivalWaiverList() throws BusinessException {
		return vesselActRepo.getLateArrivalWaiverList();
	}
	
	@Override
	public List<OSDExemptionClauses> getOsdExemptionList() throws BusinessException{
		return vesselActRepo.getOsdExemptionList();
	}
	@Override
	public Map<String, String> uploadOsdFiles(
	        MultipartHttpServletRequest request
	) throws BusinessException,IOException {
		return vesselActRepo.uploadOsdFiles(request);
	}
	@Override
	public Map<String, String> getLatestOsdFile(String vvcd,String actualFileName) throws BusinessException{
		return vesselActRepo.getLatestOsdFile( vvcd, actualFileName);
	}
	
	@Override
	public boolean sendMessage(IMessageValueObject mVO) throws BusinessException{
		return vesselActRepo.sendMessage(mVO);

	}
	
	@Override
	public boolean schemeAndVesselIndicator(String vvcd) throws BusinessException{
		return vesselActRepo.schemeAndVesselIndicator(vvcd);
	}

	@Override
	public Long approveOsd(String vvcd, String userAccount) throws BusinessException {
		return vesselActRepo.approveOsd(vvcd,userAccount);
	}

	@Override
	public boolean osdSubmitindicator(String vvcd, boolean beforeApprove) throws BusinessException {
		return vesselActRepo.osdSubmitindicator(vvcd,beforeApprove);
	}

	@Override
	public int getSumOfExemptionMinutes(String vvcd, String ExemptionType) throws BusinessException {
		return vesselActRepo.getSumOfExemptionMinutes(vvcd, ExemptionType);
	}
	
	//CH-7 trigger email functionality
	@Override
	public EmailValueObject getEmailContentForQueryOsd(String vvcd, boolean lateQ, boolean osdQ, String queryRemarks) throws BusinessException {
		return vesselActRepo.getEmailContentForQueryOsd(vvcd, lateQ, osdQ, queryRemarks);
	}

}
