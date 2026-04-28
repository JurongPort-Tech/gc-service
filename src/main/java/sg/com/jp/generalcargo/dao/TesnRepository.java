package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TesnValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface TesnRepository {

	public List<TesnValueObject> getTesnSearchJpPsaList(String tesnNo, String coCode) throws BusinessException;

	public List<TesnValueObject> getCargoDetails(String asnNo) throws BusinessException;

	public boolean chkCrgAgtAdpEdo(String edoNbr, String compCode) throws BusinessException;

	public List<TesnValueObject> getPortList() throws BusinessException;

	public String deleteRecord(String tesnNbr, String edoAsnNbrG, String uid) throws BusinessException;

	public String updateRecord(String uid, String ps, String secCarves, String secCarvoy, int noOfPkgs, String shipper,
			String tesnNbr, String edoAsnNbrG, String bkref, String acctNbr, String category, String nomWtStr, String nomVolStr) throws BusinessException;

	public String getTesnWtVol(String tesnNbr) throws BusinessException;

	public String getVslTypeByAsnNo(String asnNo) throws BusinessException;

	public String getTdbCrno(String edo) throws BusinessException;

	public String getBLNo(String tesn) throws BusinessException;

	public String getSysdate() throws BusinessException;

	public List<TesnValueObject> getDisplayCargoDetails(String asnNo, String vvCd, String secCarNm)
			throws BusinessException;

	public String createNomVesselJPPsa(String vslName, String voyNbr, String userid) throws BusinessException;

	public List<TesnValueObject> addRecordForDPE(String uid, String ps, String secCarves, String secCarvoy,
			int noOfPkgs, String shipper, String edoAsnNbrG, String bkref, String acctNbr, String category, String nomWt, String nomVol)
			throws BusinessException;

	public boolean chkDttmOfSecondCarrierVsl(String vslNm, String outVoyNbr) throws BusinessException;

	public boolean chkSecondCarrierVsl(String vslNm, String outVoyNbr) throws BusinessException;

	public TableResult getTesnJpPsaList(String vvcode, String coCode, Criteria criteria) throws BusinessException;

	public VesselVoyValueObject getVessel(String vesselName, String invoyNbr, String coCd) throws BusinessException;

	public List<VesselVoyValueObject> getVesselVoy(String coCode) throws BusinessException;

}
