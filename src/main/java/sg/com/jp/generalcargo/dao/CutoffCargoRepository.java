package sg.com.jp.generalcargo.dao;

import java.util.List;

import sg.com.jp.generalcargo.domain.CutoffValueObject;
import sg.com.jp.generalcargo.domain.CuttoffEdoValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CutoffCargoRepository {

	public List<CuttoffEdoValueObject> getBLDetails(String mftSeqNbr) throws BusinessException;
	
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

	public List<CuttoffEdoValueObject> getEdoBLDetails(String edonumber)  throws BusinessException;

	public List<CutoffValueObject> getEdoNbr(String strblnbr, String strvarnbr)  throws BusinessException;
	
	public List<CutoffValueObject> viewEdoDetails(String blno, String varnbr, String edoasnno, int cutoffno) throws BusinessException;

}
