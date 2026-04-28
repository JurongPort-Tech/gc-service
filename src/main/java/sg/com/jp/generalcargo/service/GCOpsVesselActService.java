package sg.com.jp.generalcargo.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartHttpServletRequest;

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
import sg.com.jp.generalcargo.util.BusinessException;

public interface GCOpsVesselActService {
	
	public List<VesselActValueObject> getVesselList(String name) throws BusinessException;

	public Map<String, Object> getVesselScheme() throws BusinessException;

	public TableResult getVesselActList(String coCd, String vslnm, String vvcd, String atbFrom, String atbTo, String atuFrom,
			String atuTo, String schemdCd,Criteria criteria) throws BusinessException;
	
	public void updateWaiverDetails(String strwaivercd, String strwaiverreason, String strwaiverstatus, String struserids,
			String vvcd) throws BusinessException;

	public void updateBillDetails(String strbillcd, String struserids, String vvcd) throws BusinessException;

	public OverStayDockageValueObject getWaiverStatus(String vvcd) throws BusinessException;

	public  Map<String, Object> determineOverStayAndAmount(String vvcd) throws BusinessException;

	public GroupVO getLevelId(String strAmount) throws BusinessException;

	public boolean hasAccesstoOSD(String userID) throws BusinessException;

	public OpsValueObject getVessels(OpsValueObject opsValueObject) throws BusinessException;

	public List<String> getWaiverList(String vvcd, String strwaiverstatus) throws BusinessException;

	public String getCodColStatus(String vvcd) throws BusinessException;

	public String getBillList(String vvcd) throws BusinessException;

	public  List<LineTowedVesselValueObject> getDockageList(String vvcd) throws BusinessException;

	public void updateVesselActivityShift(String strvvstatus, String[] stratbdttm2, String[] stratudttm2,
			String strcoddttm, String strcoldttm, String strbcoddttm, String strbcoldttm, String strdiscdttm,
			String strloaddttm, String struserid, String vvcd, int i, String strfgcdttm, String totalGenCargoAct) throws BusinessException;

	public void updateVesselActivity(String strvvstatus, String stratbdttm, String stratudttm, String strcoddttm,
			String strcoldttm, String strbcoddttm, String strbcoldttm, String strdiscdttm, String strloaddttm,
			String struserid, String vvcd, String strfgcdttm, String totalGenCargoAct) throws BusinessException;

	public void updateVesselActStatus(String strvvstatus, String vvcd, String struserid) throws BusinessException;

	public List<String> getWaiverBillingList(String vvcd) throws BusinessException;

	public List<VesselActValueObject> getVesselActShiftList(String coCd, String vvcd) throws BusinessException;

	public String checkShipStore(String vvcd) throws BusinessException;

	public int checkImportCntr(String vvcd) throws BusinessException;

	public int checkExportCntr(String vvcd) throws BusinessException;

	public  List<WaiverCodesVO> getWaiverCodes(WaiverCodesVO waiverCodesVO) throws BusinessException;

	public List<BillingCodesVO>  getBillingReasons(BillingCodesVO billingCodesVO) throws BusinessException;
	
    public void addDockage(String vvcd,  List<LineTowedVesselValueObject>  co) throws BusinessException;
	
	public OpsValueObject getVesselInfo(String vvCode, OpsValueObject opsValueObject) throws BusinessException;

	public Map<String, String> getVesselDataForKmf(String vvcd) throws BusinessException;

	public Timestamp getSysDate() throws BusinessException;
	
	public List<OSDReviewObject> getOsdReviewList(String vvCd) throws BusinessException;
	
	
	public int updateOsdReview(
	        String vvcd,String osdExemptionCodeList,String lateArrivalExemptionList,String osdReviewOption,String lateArrivalReviewOption,
	        String useraccount,String submitInd,String actualOsdFilesName,String actuallateFilesName, Map<String, String> encryptedfiles
	) throws BusinessException;

	public Long queryOsd(String vvcd, boolean lateQ, boolean osdQ, String queryRemarks, String useraccount) throws BusinessException;
	
	
	public List<OSDExemptionClauses> getOsdExemptionList() throws BusinessException;
	
	public List<OSDExemptionClauses> getLateArrivalWaiverList() throws BusinessException;

	public Map<String, String> uploadOsdFiles(MultipartHttpServletRequest request) throws BusinessException, IOException;
	
	public Map<String, String> getLatestOsdFile(String vvcd,String actualFileName) throws BusinessException;
	
	public boolean sendMessage(IMessageValueObject mVO) throws BusinessException;

	public boolean schemeAndVesselIndicator(String vvcd) throws BusinessException;

	public Long approveOsd(String vvcd, String userAccount) throws BusinessException;

	public boolean osdSubmitindicator(String vvcd, boolean beforeApprove) throws BusinessException;

	public int getSumOfExemptionMinutes(String vvcd, String ExemptionType) throws BusinessException;

	public EmailValueObject getEmailContentForQueryOsd(String vvcd, boolean lateQ, boolean osdQ, String queryRemarks) throws BusinessException;

}
