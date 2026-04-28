package sg.com.jp.generalcargo.service;

import java.util.Date;
import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.GbccCargoTallysheetVO;
import sg.com.jp.generalcargo.domain.MiscTypeCode;
import sg.com.jp.generalcargo.domain.StevedoreCompany;
import sg.com.jp.generalcargo.domain.VesselCall;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CargoTallySheetService {

	public List<MiscTypeCode> cc_getMiscTypeCode(String catCd) throws BusinessException;

	public GbccCargoTallysheetVO cc_getCargoTallySheetById(String custCd, String vvCd, Date crDttm, String oprType,
			Integer hatchNo, String stevCd) throws BusinessException;

	public boolean cc_persistCargoTallySheet(GbccCargoTallysheetVO transientObject) throws BusinessException;

	public List<VesselCall> cc_getCargoOprVesselCall(String custCd) throws BusinessException;

	public List<StevedoreCompany> cc_getCargoOprStevedore() throws BusinessException;

	public List<String> cc_getCargoOprBerth(String custCd) throws BusinessException;

	public List<GbccCargoTallysheetVO> cc_getCargoTallySheet(String CustCode, String vvCd, String berthNo, String oprType, Integer hatchNo,
			String sortBy, Criteria criteria, Boolean needAllData) throws BusinessException;

	public String cc_checkCargoTallysheetAdd(String vvCd, String stevCd, String coCd) throws BusinessException;

}
