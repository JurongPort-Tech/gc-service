package sg.com.jp.generalcargo.service;

import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.TesnJpJpValueObject;
import sg.com.jp.generalcargo.domain.TesnPsaJpEsnListValueObject;
import sg.com.jp.generalcargo.domain.TesnValueObject;
import sg.com.jp.generalcargo.domain.TesnVesselVoyValueObject;
import sg.com.jp.generalcargo.domain.VesselVoyValueObject;
import sg.com.jp.generalcargo.util.BusinessException;

public interface TesnSearchService {

	public List<TesnPsaJpEsnListValueObject> getCntrDetails(String esnNbr) throws BusinessException;

	public List<TesnPsaJpEsnListValueObject> getEsnDetails(String esnNbr, String custId) throws BusinessException;

	public List<VesselVoyValueObject> getVesselList(String custId) throws BusinessException;

	public List<TesnValueObject> getTesnSearchJpPsaList(String tesnNo, String coCode) throws BusinessException;

	public List<TesnValueObject> getDisplayCargoDetails(String asnNo, String vvCd, String secCarNm)
			throws BusinessException;

	public List<VesselVoyValueObject> getVesselVoy(String coCode) throws BusinessException;

	public TesnJpJpValueObject tesnjpjpView(String vesselvoyageno, String tesn_nbr, String edoAsn) throws BusinessException;

	public int tesnjpjpValidCheck_DN(String tesn_nbr, String out_voy_var_nbr) throws BusinessException;

	public int tesnjpjpValidCheck_status(String tesn_nbr, String out_voy_var_nbr) throws BusinessException;

	public int tesnjpjpValidCheck(String tesn_nbr, String out_voy_var_nbr) throws BusinessException;

	public List<TesnVesselVoyValueObject> getVslList(String coCd) throws BusinessException;

	public TableResult getTesnJpJpList(String vvcode, Criteria criteria) throws BusinessException;

	public String tesnSearch(String tesnNo) throws BusinessException;

}
