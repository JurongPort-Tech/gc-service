package sg.com.jp.generalcargo.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.EsnRepository;
import sg.com.jp.generalcargo.dao.TesnRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TesnValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.service.TesnJpPsaService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service
public class TesnJpPsaServiceImpl implements TesnJpPsaService{

	@Autowired
	private TesnRepository tesnRepo;
	
	@Autowired
	private EsnRepository esnRepo;
	
	
	@Override
	public TableResult getTesnJpPsaList(String vvcode, String coCode, Criteria criteria) throws BusinessException {
		
		return tesnRepo.getTesnJpPsaList(vvcode, coCode, criteria);
	}

	@Override
	public VesselVoyValueObject getVessel(String vesselName, String invoyNbr, String coCd) throws BusinessException {
		
		return tesnRepo.getVessel(vesselName, invoyNbr, coCd);
	}

	@Override
	public List<VesselVoyValueObject> getVesselVoy(String coCode) throws BusinessException {
		
		return tesnRepo.getVesselVoy(coCode);
	}

	@Override
	public String getTdbCrno(String edo) throws BusinessException {
		
		return tesnRepo.getTdbCrno(edo);
	}

	@Override
	public String getBLNo(String tesn) throws BusinessException {
		
		return tesnRepo.getBLNo(tesn);
	}

	@Override
	public String getSysdate() throws BusinessException {
		
		return tesnRepo.getSysdate();
	}

	@Override
	public List<TesnValueObject> getDisplayCargoDetails(String asnNo, String vvCd, String secCarNm) throws BusinessException {
		
		return tesnRepo.getDisplayCargoDetails(asnNo, vvCd, secCarNm);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String createNomVesselJPPsa(String vslName, String voyNbr, String userid) throws BusinessException {
		
		return tesnRepo.createNomVesselJPPsa(vslName, voyNbr, userid);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public List<TesnValueObject> addRecordForDPE(String uid, String ps, String secCarves, String secCarvoy, int noOfPkgs,
			String shipper, String edoAsnNbrG, String bkref, String acctNbr, String category, String nomWt, String nomVol) throws BusinessException {
		
		return tesnRepo.addRecordForDPE(uid, ps, secCarves, secCarvoy, noOfPkgs, shipper, edoAsnNbrG, bkref, acctNbr, category, nomWt, nomVol);
	}

	@Override
	public boolean chkDttmOfSecondCarrierVsl(String vslNm, String outVoyNbr) throws BusinessException {
		
		return tesnRepo.chkDttmOfSecondCarrierVsl(vslNm, outVoyNbr);
	}

	@Override
	public boolean chkSecondCarrierVsl(String vslNm, String outVoyNbr) throws BusinessException {
		
		return tesnRepo.chkSecondCarrierVsl(vslNm, outVoyNbr);
	}

	@Override
	public String getCategoryValue(String ccCd) throws BusinessException {
		
		return esnRepo.getCategoryValue(ccCd);
	}

	@Override
	public List<Map<String, Object>> getCategoryList() throws BusinessException {
		
		return esnRepo.getCategoryList();
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String deleteRecord(String tesnNbr, String edoAsnNbrG, String uid) throws BusinessException {
		
		return tesnRepo.deleteRecord(tesnNbr, edoAsnNbrG, uid);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String updateRecord(String uid, String ps, String secCarves, String secCarvoy, int noOfPkgs, String shipper,
			String tesnNbr, String edoAsnNbrG, String bkref, String acctNbr, String category, String nomWtStr, String nomVolStr) throws BusinessException {
		
		return tesnRepo.updateRecord(uid, ps, secCarves, secCarvoy, noOfPkgs, shipper, tesnNbr, edoAsnNbrG, bkref, acctNbr, category, nomWtStr, nomVolStr);
	}

	@Override
	public String getTesnWtVol(String tesnNbr) throws BusinessException {
		
		return tesnRepo.getTesnWtVol(tesnNbr);
	}

	@Override
	public String getVslTypeByAsnNo(String asnNo) throws BusinessException {
		
		return tesnRepo.getVslTypeByAsnNo(asnNo);
	}

	@Override
	public List<TesnValueObject> getPortList() throws BusinessException {
		
		return tesnRepo.getPortList();
	}

	@Override
	public List<TesnValueObject> getCargoDetails(String asnNo) throws BusinessException {
		
		return tesnRepo.getCargoDetails(asnNo);
	}

	@Override
	public boolean chkCrgAgtAdpEdo(String edoNbr, String compCode) throws BusinessException {
		
		return tesnRepo.chkCrgAgtAdpEdo(edoNbr, compCode);
	}

}
