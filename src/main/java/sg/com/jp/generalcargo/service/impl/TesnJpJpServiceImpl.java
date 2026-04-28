package sg.com.jp.generalcargo.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.BookingRefRepository;
import sg.com.jp.generalcargo.dao.EsnRepository;
import sg.com.jp.generalcargo.dao.TesnjpjpRepository;
import sg.com.jp.generalcargo.domain.BookingReferenceValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TesnEsnListValueObject;
import sg.com.jp.generalcargo.domain.TesnJpJpValueObject;
import sg.com.jp.generalcargo.domain.TesnVesselVoyValueObject;
import sg.com.jp.generalcargo.service.TesnJpJpService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service("tesnJpJpService")
public class TesnJpJpServiceImpl implements TesnJpJpService{

	@Autowired
	private TesnjpjpRepository tesnjpjpRepo;
	
	@Autowired
	private EsnRepository esnRepo;
	
	@Autowired
	private BookingRefRepository bookingRefRepo;
	
	@Override
	public List<TesnVesselVoyValueObject> getVslList(String coCd) throws BusinessException {
		
		return tesnjpjpRepo.getVslList(coCd);
	}

	@Override
	public TesnVesselVoyValueObject getVessel(String fetchVesselName, String fetchVoyageNbr, String coCd)
			throws BusinessException {
		
		return tesnjpjpRepo.getVessel(fetchVesselName, fetchVoyageNbr, coCd);
	}

	@Override
	public TableResult getTesnJpJpList(String strVoyno, Criteria criteria) throws BusinessException {
		
		return tesnjpjpRepo.getTesnJpJpList(strVoyno, criteria);
	}

	@Override
	public int tesnjpjpValidCheck(String s6, String s7) throws BusinessException {
		
		return tesnjpjpRepo.tesnjpjpValidCheck(s6, s7);
	}

	@Override
	public int tesnjpjpValidCheck_status(String s6, String s7) throws BusinessException {
		
		return tesnjpjpRepo.tesnjpjpValidCheck_status(s6, s7);
	}

	@Override
	public int tesnjpjpValidCheck_DN(String s6, String s7) throws BusinessException {
		
		return tesnjpjpRepo.tesnjpjpValidCheck_DN(s6, s7);
	}

	@Override
	public TesnJpJpValueObject tesnjpjpView(String s7, String s6, String s13) throws BusinessException {
		
		return tesnjpjpRepo.tesnjpjpView(s7, s6, s13);
	}

	@Override
	public List<String> getSAacctno(String s7) throws BusinessException {
		
		return tesnjpjpRepo.getSAacctno(s7);
	}

	@Override
	public List<TesnEsnListValueObject> getABacctno(String s7) throws BusinessException {
		
		return tesnjpjpRepo.getABacctno(s7);
	}

	@Override
	public List<TesnEsnListValueObject> getABacctnoForSA(String s7) throws BusinessException {
		
		return tesnjpjpRepo.getABacctnoForSA(s7);
	}

	@Override
	public String getBPacctnbr(String s6, String s7) throws BusinessException {
		
		return tesnjpjpRepo.getBPacctnbr(s6, s7);
	}

	@Override
	public String getScheme(String s7) throws BusinessException {
		
		return tesnjpjpRepo.getSchemeName(s7);
	}

	@Override
	public String getSchemeInd(String s7) throws BusinessException {
		
		return tesnjpjpRepo.getSchemeInd(s7);
	}

	@Override
	public String getClsShipInd(String s7) throws BusinessException {
		
		return tesnjpjpRepo.getClsShipInd(s7);
	}

	@Override
	public String getClsShipInd_bkr(String s9) throws BusinessException {
		
		return tesnjpjpRepo.getClsShipInd_bkr(s9);
	}

	@Override
	public String getSchemeName(String s7) throws BusinessException {
		
		return tesnjpjpRepo.getSchemeName(s7);
	}

	@Override
	public String getVCactnbr(String s7) throws BusinessException {
		
		return tesnjpjpRepo.getVCactnbr(s7);
	}

	@Override
	public String getABactnbr(String s7) throws BusinessException {
		
		return tesnjpjpRepo.getABactnbr(s7);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void EsnAssignBillUpdate(String s13, String s6, String s) throws BusinessException {
		
		tesnjpjpRepo.EsnAssignBillUpdate(s13, s6, s);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void EsnAssignVslUpdate(String s7, String string, String s) throws BusinessException {
		
		tesnjpjpRepo.EsnAssignVslUpdate(s7, string, s);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void AssignCrgvalUpdate(String s14, String s6, String s) throws BusinessException {
		
		tesnjpjpRepo.AssignCrgvalUpdate(s14, s6, s);
	}

	@Override
	public String AssignCrgvalCheck(String s6) throws BusinessException {
		
		return tesnjpjpRepo.AssignCrgvalCheck(s6);
	}

	@Override
	public List<TesnEsnListValueObject> getAssignCargo() throws BusinessException {
		
		return tesnjpjpRepo.getAssignCargo();
	}

	@Override
	public List<Map<String, Object>> getCategoryList() throws BusinessException {
		
		return esnRepo.getCategoryList();
	}

	@Override
	public String getCategoryValue(String category) throws BusinessException {
		
		return esnRepo.getCategoryValue(category);
	}

	@Override
	public void tesnVerify_accno(String s12) throws BusinessException {
		
		tesnjpjpRepo.tesnVerify_accno(s12);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void tesnjpjpAmendForDPE(String s5, String s2, String s7, String s9, String s11, String s12, String s14,
			String s13, String s, String s16, String s17, String stuffind, String category, String nomWt, String nomVol)
			throws BusinessException {
		
		tesnjpjpRepo.tesnjpjpAmendForDPE( s5,  s2,  s7,  s9,  s11,  s12,  s14, s13,  s,  s16,  s17,  stuffind,  category,  nomWt,  nomVol);
	}

	@Override
	public void tesnVerify_amend(String s6, String s3) throws BusinessException {
		
		tesnjpjpRepo.tesnVerify_amend(s6, s3);
	}

	@Override
	public void tesnVerify_amend_status(String s6, String s3) throws BusinessException {
		
		tesnjpjpRepo.tesnVerify_amend_status(s6, s3);
	}

	@Override
	public String getVesselType(String brno) throws BusinessException {
		
		return esnRepo.getVesselType(brno);
	}

	@Override
	public boolean chkDttmOfSecondCarrierVsl(String brno) throws BusinessException {
		
		return tesnjpjpRepo.chkDttmOfSecondCarrierVsl(brno);
	}

	@Override
	public String getEdoAcct(String edo_asn_nbr, String bk_ref_nbr, String s1) throws BusinessException {
		
		return tesnjpjpRepo.getEdoAcct(edo_asn_nbr, bk_ref_nbr, s1);
	}

	@Override
	public List<String> tesnjpjpGetAccNo(String s1, String s4, String s8) throws BusinessException {
		
		return tesnjpjpRepo.tesnjpjpGetAccNo(s1, s4, s8);
	}

	@Override
	public void tesnVerify_delete(String tesn_nbr, String vesselvoyageno) throws BusinessException {
		
		tesnjpjpRepo.tesnVerify_delete(tesn_nbr, vesselvoyageno);
	}

	@Override
	public void tesnVerify_delete_status(String tesn_nbr, String vesselvoyageno) throws BusinessException {
		
		tesnjpjpRepo.tesnVerify_delete_status(tesn_nbr, vesselvoyageno);
	}

	@Override
	public void tesnVerify_delete_DN(String tesn_nbr, String vesselvoyageno) throws BusinessException {
		
		tesnjpjpRepo.tesnVerify_delete_DN(tesn_nbr, vesselvoyageno);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void tesnjpjpDelete(String vesselvoyageno, String tesn_nbr, String nbr_pkgs, String edo_asn_nbr,
			String bk_ref_nbr, String userID) throws BusinessException {
		
		tesnjpjpRepo.tesnjpjpDelete(vesselvoyageno, tesn_nbr, nbr_pkgs, edo_asn_nbr, bk_ref_nbr, userID);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String tesnjpjpAddForDPE(String s2, String s4, String s7, String s9, String s12, String s13, String s15,
			String s11, String s10, String s1, String s, String s14, String stuffind, String category, String nomWt,
			String nomVol) throws BusinessException {
		
		return tesnjpjpRepo.tesnjpjpAddForDPE( s2,  s4,  s7,  s9,  s12,  s13,  s15, s11,  s10,  s1,  s,  s14,  stuffind,  category,  nomWt,
				 nomVol);
	}

	@Override
	public void tesnVerify_edo_bk(String s3, String s5, String s1) throws BusinessException {
		
		tesnjpjpRepo.tesnVerify_edo_bk(s3, s5, s1);
	}

	@Override
	public TesnJpJpValueObject tesnjpjpAddView(String s3, String s5) throws BusinessException {
		
		return tesnjpjpRepo.tesnjpjpAddView(s3, s5);
	}

	@Override
	public List<BookingReferenceValueObject> fetchBKDetails(String s5) throws BusinessException {
		
		return bookingRefRepo.fetchBKDetails(s5);
	}

	@Override
	public void checkEdoPackage(String edo_asn_nbr) throws BusinessException {
		tesnjpjpRepo.checkEdoPackage(edo_asn_nbr);
	}

}
