package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TesnEsnListValueObject;
import sg.com.jp.generalcargo.domain.TesnJpJpValueObject;
import sg.com.jp.generalcargo.domain.TesnVesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface TesnjpjpRepository {
	
	public String tesnjpjpAddForDPE(String edo_asn_nbr, String bk_ref_nbr,
			String ld_ind, String nbr_pkgs, String pay_mode,
			String acc_num, String val_pkgs,
			String out_voy_var_nbr, String in_voy_var_nbr,
			String coCd, String UserID, String edo_nbr_pkgs,
			String stuffind,String category, String nomWt, String nomVol) throws BusinessException;
	
	public void tesnVerify_edo_bk(String edo_asn_nbr, String bk_ref_nbr,String comp_code) throws BusinessException ;
	
	public TesnJpJpValueObject tesnjpjpAddView(String edo_asn_nbr,String bk_ref_nbr) throws BusinessException;
	
	public void tesnjpjpDelete(String vesselvoyageno, String tesn_nbr,
			String nbr_pkgs, String edo_asn_nbr, String bk_ref_nbr, String UserID) throws BusinessException ;
	
	public void tesnVerify_delete_DN(String tesn_nbr, String out_voy_var_nbr) throws BusinessException ;
	
	 public void tesnVerify_delete_status(String tesn_nbr, String out_voy_var_nbr) throws BusinessException;
	
	public void tesnVerify_delete(String tesn_nbr, String out_voy_var_nbr) throws BusinessException;
	
	public List<String> tesnjpjpGetAccNo(String comp_code, String vsl_cd, String bk_ref_nbr) throws BusinessException;
	
	public String getEdoAcct(String txt_edo_asn_nbr, String bk_ref_nbr, String comp_code) throws BusinessException;
	
	public boolean chkDttmOfSecondCarrierVsl(String bk_ref_nbr) throws BusinessException ;
	
	public void tesnVerify_amend_status(String tesn_nbr, String out_voy_var_nbr) throws BusinessException ;
	
	public void tesnVerify_amend(String tesn_nbr, String out_voy_var_nbr) throws BusinessException;
	
	public void tesnjpjpAmendForDPE(String vesselvoyageno, String tesn_nbr,
			String ld_ind, String nbr_pkgs, String pay_mode,
			String acc_num, String val_pkgs, String edo_asn_nbr,
			String userid, String edo_nbr_pkgs,
			String prev_nbr_pkgs,String stuffind,String category,
			String nomWt, String nomVol) throws BusinessException ;
	
	public void tesnVerify_accno(String acc_num) throws BusinessException;
	
	public List<TesnEsnListValueObject> getAssignCargo() throws BusinessException;
	
	public String AssignCrgvalCheck(String esnnbr) throws BusinessException;
	
	public void AssignCrgvalUpdate(String crgval, String esnnbr, String userId) throws BusinessException;
	
	public void EsnAssignVslUpdate(String vv_cd, String status, String userId) throws BusinessException;
	
	public void EsnAssignBillUpdate(String acctnbr, String esno, String userid) throws BusinessException;
	
	public String getClsShipInd_bkr(String bkrNbr) throws BusinessException;
	
	public String getClsShipInd(String varNo) throws BusinessException;
	
	public String getSchemeInd(String out_voyno) throws BusinessException ;
	
	public String getABactnbr(String voy_nbr) throws BusinessException;
	
	public String getVCactnbr(String voy_nbr) throws BusinessException;
	
	public String getSchemeName(String voy_nbr) throws BusinessException ;
	
	public String getBPacctnbr(String esno, String voy_nbr) throws BusinessException;
	
	public List<TesnEsnListValueObject> getABacctnoForSA(String out_voyno) throws BusinessException;
	
	public List<TesnEsnListValueObject> getABacctno(String out_voyno) throws BusinessException ;
	
	public List<String> getSAacctno(String vv_cd) throws BusinessException;
	
	public TesnJpJpValueObject tesnjpjpView(String vesselvoyageno,String tesn_nbr, String edoAsn) throws BusinessException;
	
	public int tesnjpjpValidCheck_DN(String tesn_nbr, String out_voy_var_nbr) throws BusinessException;
	
	public int tesnjpjpValidCheck_status(String tesn_nbr, String out_voy_var_nbr) throws BusinessException;
	
	public int tesnjpjpValidCheck(String tesn_nbr, String out_voy_var_nbr) throws BusinessException ;
	
	public TableResult getTesnJpJpList(String vvcode, Criteria criteria) throws BusinessException;
	
	public TesnVesselVoyValueObject getVessel(String vesselName, String outvoyNbr, String coCd) throws BusinessException;
	
	public List<TesnVesselVoyValueObject> getVslList(String coCd) throws BusinessException ;

	public void checkEdoPackage(String edo_asn_nbr) throws BusinessException;

}
