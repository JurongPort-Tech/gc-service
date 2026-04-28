package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.ContainerDetailObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.SchemeAccountObject;
import sg.com.jp.generalcargo.domain.StuffingDetailObject;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface StuffingRepository {

	public TableResult getContainerDetails(String vvcode, String containerno, Criteria criteria) throws BusinessException;

	public List<VesselVoyValueObject> getVesselVoyage(String companycode) throws BusinessException;

	public List<String> getContainerNos(String vvcode) throws BusinessException;

	public List<String> checkEdoNoPkgs(List<String> edonos, List<String> edopkgs, String vvcode, String seqno, boolean insert)
			throws BusinessException;

	public List<ContainerDetailObject> chkESNStuffInd(List<String> esnNbr) throws BusinessException;

	public List<String> checkEsnNoPkgs(List<String> esnnos, List<String> esnpkgs, String vvcode, String seqno, boolean insert)
			throws BusinessException;

	public boolean isClosed(String vvcode, String contno, String cntrseqno, String seqno) throws BusinessException;

	public String insertStuffing(List<String> edonos, List<String> edopkgs, List<String> esnnos, List<String> esnpkgs, String seqno,
			String vvcode, String cntrno, String cntrseqno, String userid) throws BusinessException;

	public List<StuffingDetailObject> getStuffingDetails(String vvcode, String contno, String cntrseqno, String seqno)
			throws BusinessException;

	public String updateStuffing(List<String> edonos, List<String> edopkgs, List<String> esnnos, List<String> esnpkgs, String seqno,
			String vvcode, String cntrno, String cntrseqno, String userid) throws BusinessException;

	public List<StuffingDetailObject> getStuffingDetailsToAmend(String vvcode, String contno, String cntrseqno, String seqno)
			throws BusinessException;

	public List<String> getBillAccountNos(String vvcode) throws BusinessException;

	public List<SchemeAccountObject> getSchemeAccountNos() throws BusinessException;

	public void assignBillableParty(String vvcode, String contno, String cntrseqno, String seqno, String acctno,
			String userid) throws BusinessException;

	public boolean isChkDNCreated(String seqno) throws BusinessException;

	public boolean cancelGbEdoUpd(String seqno) throws BusinessException;

	public void cancelStuffing(String vvcode, String cntrno, String cntrseqno, String seqno, String userid)
			throws BusinessException;

	public boolean isStuffingDttmLesser(String stuffdttm) throws BusinessException;

	public boolean isGbEdoUpd(String seqno) throws BusinessException;

	public void closeStuffing(String vvcode, String contno, String cntrseqno, String seqno, String stuffdttm,
			String waivecharge, String userid) throws Exception;

	public void updateWaiverStatus(String vvcode, String contno, String cntrseqno, String seqno, String waivecharge,
			String userid) throws BusinessException;
	
	public boolean isTesnNbr(String esnno) throws BusinessException;

}
