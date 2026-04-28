package sg.com.jp.generalcargo.service;

import java.util.List;
import java.util.Map;

import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TesnEsnListValueObject;
import sg.com.jp.generalcargo.domain.TesnJpJpValueObject;
import sg.com.jp.generalcargo.domain.TesnVesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface TesnJpJpService {

	public List<TesnVesselVoyValueObject> getVslList(String coCd) throws BusinessException;

	public TesnVesselVoyValueObject getVessel(String fetchVesselName, String fetchVoyageNbr, String coCd) throws BusinessException;

	public TableResult getTesnJpJpList(String strVoyno, Criteria criteria) throws BusinessException;

	public int tesnjpjpValidCheck(String s6, String s7) throws BusinessException;

	public int tesnjpjpValidCheck_status(String s6, String s7) throws BusinessException;

	public int tesnjpjpValidCheck_DN(String s6, String s7) throws BusinessException;

	public TesnJpJpValueObject tesnjpjpView(String s7, String s6, String s13) throws BusinessException;

	public List<String> getSAacctno(String s7) throws BusinessException;

	public List<TesnEsnListValueObject> getABacctno(String s7) throws BusinessException;

	public List<TesnEsnListValueObject> getABacctnoForSA(String s7) throws BusinessException;

	public String getBPacctnbr(String s6, String s7) throws BusinessException;

	public String getScheme(String s7) throws BusinessException;

	public String getSchemeInd(String s7) throws BusinessException;

	public String getClsShipInd(String s7) throws BusinessException;

	public String getClsShipInd_bkr(String s9) throws BusinessException;

	public String getSchemeName(String s7) throws BusinessException;

	public String getVCactnbr(String s7) throws BusinessException;

	public String getABactnbr(String s7) throws BusinessException;

	public void EsnAssignBillUpdate(String s13, String s6, String s) throws BusinessException;

	public void EsnAssignVslUpdate(String s7, String string, String s) throws BusinessException;

	public void AssignCrgvalUpdate(String s14, String s6, String s) throws BusinessException;

	public String AssignCrgvalCheck(String s6) throws BusinessException;

	public List<TesnEsnListValueObject> getAssignCargo() throws BusinessException;

	public List<Map<String, Object>> getCategoryList() throws BusinessException;

	public String getCategoryValue(String category) throws BusinessException;

	public void tesnVerify_accno(String s12) throws BusinessException;

	public void tesnjpjpAmendForDPE(String s5, String s2, String s7, String s9, String s11, String s12, String s14,
			String s13, String s, String s16, String s17, String stuffind, String category, String nomWt,
			String nomVol) throws BusinessException;

	public void tesnVerify_amend(String s6, String s3) throws BusinessException;

	public void tesnVerify_amend_status(String s6, String s3) throws BusinessException;

	public String getVesselType(String brno) throws BusinessException;

	public boolean chkDttmOfSecondCarrierVsl(String brno) throws BusinessException;

	public String getEdoAcct(String edo_asn_nbr, String bk_ref_nbr, String s1) throws BusinessException;

	public List<String> tesnjpjpGetAccNo(String s1, String s4, String s8) throws BusinessException;

	public void tesnVerify_delete(String tesn_nbr, String vesselvoyageno) throws BusinessException;

	public void tesnVerify_delete_status(String tesn_nbr, String vesselvoyageno) throws BusinessException;

	public void tesnVerify_delete_DN(String tesn_nbr, String vesselvoyageno) throws BusinessException;

	public void tesnjpjpDelete(String vesselvoyageno, String tesn_nbr, String nbr_pkgs, String edo_asn_nbr,
			String bk_ref_nbr, String userID) throws BusinessException;

	public String tesnjpjpAddForDPE(String s2, String s4, String s7, String s9, String s12, String s13, String s15,
			String s11, String s10, String s1, String s, String s14, String stuffind, String category, String nomWt,
			String nomVol) throws BusinessException;

	public void tesnVerify_edo_bk(String s3, String s5, String s1) throws BusinessException;

	public TesnJpJpValueObject tesnjpjpAddView(String s3, String s5) throws BusinessException;

	public List<BookingReferenceValueObject> fetchBKDetails(String s5) throws BusinessException;

	public void checkEdoPackage(String s2) throws BusinessException;

}
