package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.EdoValueObjectContainerised;
import sg.com.jp.generalcargo.domain.EdoValueObjectOps;
import sg.com.jp.generalcargo.util.BusinessException;

public interface DnEdoDetailRepository {

	// StartRegion DnEdoDetailRepository
	public boolean checkESNCntr(String edoasn) throws BusinessException;

	public String getCntrSeq(String cntrNo) throws BusinessException;

	public boolean checkCancelDN(String dnNbr) throws BusinessException;

	public String getDnCntrFirst(String cntrSeq, String cntrNbr) throws BusinessException;

	public String cancelShutoutDN(String edoNbr, String dnNbr, String userid) throws BusinessException;

	public String getUaNbr(String esnNbr, int nbrPkgs, String transDttm, String dpNm, String dpIcNbr)
			throws BusinessException;

	public boolean countDNBalance(String cntrNbr) throws BusinessException;

	public String updateCntrStatus(String cntrSeq, String userID) throws BusinessException;

	public int checkFirstDN(String edoNbr, String cntrNo) throws BusinessException;

	public void changeStatusCntr(String cntrSeq, String user, String newCatCode) throws BusinessException;

	public void cancel1stDn(String cntrSeq, String cntrNbr, String user) throws BusinessException;

	public void updateWeight(String cntrSeq, long weight, String user, String times) throws BusinessException;

	public String chktesnJpPsa_nbr(String esn_asnNbr) throws BusinessException;

	public List<EdoValueObjectOps> fetchShutoutDNList(String edoNbr) throws BusinessException;

	public List<EdoValueObjectOps> fetchEdoDetails(String edoNbr, String searchcrg, String tesnnbr) throws BusinessException;

	public List<EdoValueObjectOps> fetchDNList(String edoNbr, String searchcrg, String tesn_nbr) throws BusinessException;

	public boolean chkEDOStuffing(String edoNbr) throws BusinessException;

	public boolean chktesnJpJp(String edoNbr) throws BusinessException;

	public List<EdoValueObjectOps> fetchSubAdpDetails(String edoNbr) throws BusinessException;

	public int getSpencialPackage(String edoNbr) throws BusinessException;

	public List<EdoValueObjectOps> fetchShutoutDNDetail(String strEdoNo, String dnNo) throws BusinessException;

	public List<EdoValueObjectOps> getVechDetails(String dnNbr) throws BusinessException;

	public List<EdoValueObjectContainerised> fetchDNDetail(String edoNbr, String status, String searchcrg, String tesn_nbr)
			throws BusinessException;

	public String getCntrNo(String dnNbr) throws BusinessException;

	public List<EdoValueObjectOps> fetchDNDetail(String strEdoNo, String edoNbr, String status, String searchcrg,
			String tesn_nbr) throws BusinessException;

	public List<EdoValueObjectOps> fetchShutoutDNCreateDetail(String edoNbr, String transType, String searchcrg,
			String tesn_nbr) throws BusinessException;

	public boolean chkVVStatus(String esnNbrR) throws BusinessException;

	public List<EdoValueObjectOps> fetchDNCreateDetail(String edoNbr, String transType, String searchcrg,
			String tesn_nbr) throws BusinessException;

	public String cancelDN(String edoNbr, String dnNbr, String userid, String transtype, String searchcrg,
			String tesn_nbr) throws BusinessException;

	public String chktesnJpJp_nbr(String esn_asnNbr) throws BusinessException;
	// EndRegion DnEdoDetailRepository

}
