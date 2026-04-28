package sg.com.jp.generalcargo.dao;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.EmailValueObject;
import sg.com.jp.generalcargo.domain.IMessageValueObject;
import sg.com.jp.generalcargo.domain.OSDExemptionClauses;
import sg.com.jp.generalcargo.domain.OSDReviewObject;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselActValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface VesselActRepo {

	public List<VesselActValueObject> getVesselActShiftList(String strCustCode, String strvvcd)
			throws BusinessException;

	public List<String> getWaiverBillingList(String strvvcd) throws BusinessException;

	public List<VesselActValueObject> getVesselList(String name) throws BusinessException;

	public TableResult getVesselActList(String strCustCode, String strvslnm, String strvvcd, String atbFromTime,
			String atbToTime, String atuFromTime, String atuToTime, String schemdCd, Criteria criteria)
			throws BusinessException;

	public int checkExportCntr(String outVoyNbr) throws BusinessException;

	public int checkImportCntr(String outVoyNbr) throws BusinessException;

	public String checkShipStore(String outVoyNbr) throws BusinessException;

	public void updateVesselActStatus(String strvvstatus, String strvvcd, String struserid) throws BusinessException;

	public void updateVesselActivity(String strvvstatus, String stratbdttm, String stratudttm, String strcoddttm,
			String strcoldttm, String strbcoddttm, String strbcoldttm, String strdiscdttm, String strloaddttm,
			String struserid, String strvvcd, String strfgcdttm, String strtotgencargoactivity)
			throws BusinessException;

	public void updateVesselActivityShift(String strvvstatus, String[] stratbdttm, String[] stratudttm,
			String strcoddttm, String strcoldttm, String strbcoddttm, String strbcoldttm, String strdiscdttm,
			String strloaddttm, String struserid, String strvvcd, int intarrsize, String strfgcdttm,
			String strtotgencargoactivity) throws BusinessException;

	public String getBillList(String strvvcd) throws BusinessException;

	public String getCodColStatus(String strvvcd) throws BusinessException;

	public List<String> getWaiverList(String strvvcd, String strwaiverstatus) throws BusinessException;

	public void updateBillDetails(String strbillcd, String struserid, String strvvcd) throws BusinessException;

	public void updateWaiverDetails(String strWaiverCd, String strWaiverReason, String strwaiverstatus,
			String struserid, String strvvcd) throws BusinessException;

	public Map<String, String> getVesselDataForKmf(String vvcd) throws BusinessException;

	public Timestamp getSysDate() throws BusinessException;
	
	public List<OSDReviewObject> getOsdReviewList(String vvCd) throws BusinessException;
	
	public Long queryOsd(String vvcd, boolean lateQ, boolean osdQ, String queryRemarks, String useraccount) throws BusinessException;
	
	
	public List<OSDExemptionClauses> getLateArrivalWaiverList() throws BusinessException;
	
	
	public List<OSDExemptionClauses> getOsdExemptionList() throws BusinessException;
	
	public Map<String, String> uploadOsdFiles(
	        MultipartHttpServletRequest request
	) throws BusinessException,IOException;

	public int updateOsdReview(String vvcd, String osdExemptionCodeList, String lateArrivalExemptionList,
			String osdReviewOption, String lateArrivalReviewOption, String useraccount, String submitInd,
			String actualOsdFilesName, String actuallateFilesName, Map<String, String> encryptedfiles) throws BusinessException;
	
	public Map<String, String> getLatestOsdFile(String vvcd,String actualFileName) throws BusinessException;

	public boolean sendMessage(IMessageValueObject mVO) throws BusinessException;

	public boolean schemeAndVesselIndicator(String vvcd) throws BusinessException;

	public boolean osdSubmitindicator(String vvcd, boolean beforeApprove) throws BusinessException;

	public Long approveOsd(String vesselCode, String userAccount) throws BusinessException;

	public int getSumOfExemptionMinutes(String vvcd, String exemptionType) throws BusinessException;

	public EmailValueObject getEmailContentForQueryOsd(String vvcd, boolean lateQ, boolean osdQ, String queryRemarks) throws BusinessException;


}
