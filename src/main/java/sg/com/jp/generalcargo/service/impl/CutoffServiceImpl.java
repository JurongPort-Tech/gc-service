package sg.com.jp.generalcargo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.com.jp.generalcargo.dao.CutoffCargoRepository;
import sg.com.jp.generalcargo.dao.impl.EdoJdbcRepository;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.CutoffValueObject;
import sg.com.jp.generalcargo.domain.CuttoffEdoValueObject;
import sg.com.jp.generalcargo.domain.EdoValueObjectCargo;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.vesselVoyObjectValue;
import sg.com.jp.generalcargo.service.CutoffService;
import sg.com.jp.generalcargo.util.BusinessException;

@Service("cutoffAddService")
public class CutoffServiceImpl implements CutoffService{

	@Autowired
	private CutoffCargoRepository cutoffCargoRepo;
	
	@Autowired
	private EdoJdbcRepository edoJdbcRepo;

	
	
	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String deleteBlCutoff(String mftnbr, String cutoffno) throws BusinessException {
		
		return cutoffCargoRepo.deleteBlCutoff(mftnbr, cutoffno);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String deleteEdoCutoff(String edoasnnbr, String cutoffnbr) throws BusinessException {
		
		return cutoffCargoRepo.deleteEdoCutoff(edoasnnbr, cutoffnbr);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String saveCutoffDetails(String varnbr, String vesselname, String invoyage, String blnbr, String cargotype,
			String hscode, String cargodesc, String cargomarking, String cargostatus, String edonumber,
			String storageind, String cutofftype, String pkgtype, String newnbrpkgs1, String struserid)
			throws BusinessException {
		
		return cutoffCargoRepo.saveCutoffDetails(varnbr, vesselname, invoyage, blnbr, cargotype, hscode, cargodesc, cargomarking, cargostatus, edonumber, storageind, cutofftype, pkgtype, newnbrpkgs1, struserid);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public String saveEdoCutoffDetails(String varnbr, String vesselname, String invoyage, String blnbr,
			String cargotype, String hscode, String cargodesc, String cargomarking, String cargostatus,
			String edonumber, String storageind, String cutofftype, String pkgtype, String newnbrpkgs1,
			String struserid) throws BusinessException {
		
		return cutoffCargoRepo.saveEdoCutoffDetails(varnbr, vesselname, invoyage, blnbr, cargotype, hscode, cargodesc, cargomarking, cargostatus, edonumber, storageind, cutofftype, pkgtype, newnbrpkgs1, struserid);
	}

	@Override
	public List<CuttoffEdoValueObject> getBLNbrList(String strvarnbr, String string) throws BusinessException {
		
		return cutoffCargoRepo.getBLNbrList(strvarnbr, string);
	}

	@Override
	public List<CuttoffEdoValueObject> getBLDetails(String strblnbr) throws BusinessException {
		
		return cutoffCargoRepo.getBLDetails(strblnbr);
	}

	@Override
	public List<CuttoffEdoValueObject> getEdoBLDetails(String edonumber) throws BusinessException {
		
		return cutoffCargoRepo.getEdoBLDetails(edonumber);
	}

	@Override
	public List<CutoffValueObject> getEdoNbr(String strblnbr, String strvarnbr) throws BusinessException {
		
		return cutoffCargoRepo.getEdoNbr(strblnbr, strvarnbr);
	}

	@Override
	public List<CutoffValueObject> viewEdoDetails(String blno, String varnbr, String edoasnno, int cutoffno) throws BusinessException {
		
		return cutoffCargoRepo.viewEdoDetails(blno, varnbr, edoasnno, cutoffno);
	}

	
	@Override
	public List<EdoValueObjectCargo> getVesselVoyageNbrList(String coCd, String strmodulecd) throws BusinessException {
		
		return edoJdbcRepo.getVesselVoyageNbrList(coCd, strmodulecd);
	}
	
	@Override
	public List<vesselVoyObjectValue> getVslVoyNbrList(String coCd, String strmodulecd, String search) throws BusinessException{
		return edoJdbcRepo.getVslVoyNbrList(coCd, strmodulecd, search);
	}
	
	@Override
	public TableResult getEdoLst(String coCd, String strvarnbr, String strmodulecd, Criteria criteria) throws BusinessException {
		return edoJdbcRepo.getEdoLst(coCd, strvarnbr, strmodulecd, criteria);
	}
	

}
