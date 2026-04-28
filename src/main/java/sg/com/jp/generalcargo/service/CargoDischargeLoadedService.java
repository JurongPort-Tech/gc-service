package sg.com.jp.generalcargo.service;

import java.util.Date;
import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.GbccCargoOprVO;
import sg.com.jp.generalcargo.domain.GbccVslProd;
import sg.com.jp.generalcargo.domain.MiscTypeCode;
import sg.com.jp.generalcargo.domain.StevedoreCompany;
import sg.com.jp.generalcargo.domain.VesselCall;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CargoDischargeLoadedService {

	public List<VesselCall> cc_getCargoOprVesselCall(String custCd) throws BusinessException;

	public List<StevedoreCompany> cc_getCargoOprStevedore() throws BusinessException;

	public List<String> cc_getCargoOprBerth(String custCd) throws BusinessException;

	public List<MiscTypeCode> cc_getMiscTypeCode(String catCd) throws BusinessException;

	public String cc_checkCargoOprAdd(String vvCd, String stevCd, String coCd, String shiftCd, Date shiftDttm)
			throws BusinessException;

	public GbccCargoOprVO cc_getCargoOprById(String vvCd, String stevCd, Date crDttm, String shiftCd, Date shiftDttm,
			String custCd) throws BusinessException;

	public List<GbccCargoOprVO> cc_getCargoOpr(String vvCd, String berthNo, String CustCode, String shiftCd,
			Date shiftDttm, String sortBy, Criteria criteria, Boolean needAllData) throws BusinessException;

	public GbccCargoOprVO cc_getCargoOprById(String vvCd, String stevCd, Date crDttm) throws BusinessException;

	public GbccCargoOprVO cc_getCargoOprByVvCdCustCd(String vvCd, String custCd) throws BusinessException;

	public GbccVslProd cc_getVslProdById(String vvCd, String stevCd, Date crDttm) throws BusinessException;

	public boolean cc_persistCargoOpr(GbccCargoOprVO transientObject) throws BusinessException;

	public GbccVslProd cc_getVslProdById(String vvCd, String stevCd, String shiftCd, Date shiftDttm)
			throws BusinessException;
	
	public boolean cc_persistVslProd(GbccVslProd transientObject) throws BusinessException;

}
