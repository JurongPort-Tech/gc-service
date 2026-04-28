package sg.com.jp.generalcargo.service;

import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.CutoffValueObject;
import sg.com.jp.generalcargo.domain.CuttoffEdoValueObject;
import sg.com.jp.generalcargo.domain.EdoValueObjectCargo;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.vesselVoyObjectValue;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CutoffService {

	public String deleteBlCutoff(String mftnbr, String cutoffno) throws BusinessException;

	public String deleteEdoCutoff(String edoasnnbr, String cutoffnbr) throws BusinessException;

	public String saveCutoffDetails(String varnbr, String vesselname, String invoyage, String blnbr, String cargotype,
			String hscode, String cargodesc, String cargomarking, String cargostatus, String edonumber,
			String storageind, String cutofftype, String pkgtype, String newnbrpkgs1, String struserid) throws BusinessException;

	public String saveEdoCutoffDetails(String varnbr, String vesselname, String invoyage, String blnbr,
			String cargotype, String hscode, String cargodesc, String cargomarking, String cargostatus,
			String edonumber, String storageind, String cutofftype, String pkgtype, String newnbrpkgs1,
			String struserid) throws BusinessException;

	public List<CuttoffEdoValueObject> getBLNbrList(String strvarnbr, String string) throws BusinessException;

	public List<CuttoffEdoValueObject> getBLDetails(String strblnbr) throws BusinessException;

	public List<CuttoffEdoValueObject> getEdoBLDetails(String edonumber)  throws BusinessException;

	public List<CutoffValueObject> getEdoNbr(String strblnbr, String strvarnbr)  throws BusinessException;

	public List<CutoffValueObject> viewEdoDetails(String blno, String varnbr, String edoasnno, int cutoffno) throws BusinessException;
	
	public List<EdoValueObjectCargo> getVesselVoyageNbrList(String coCd, String strmodulecd) throws BusinessException;

	public TableResult getEdoLst(String coCd, String strvarnbr, String strmodulecd, Criteria criteria) throws BusinessException;

	public List<vesselVoyObjectValue> getVslVoyNbrList(String coCd, String strmodulecd, String search) throws BusinessException;

}
