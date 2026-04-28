package sg.com.jp.generalcargo.service;

import java.util.Date;
import java.util.List;

import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.GbccCargoTimesheetVO;
import sg.com.jp.generalcargo.domain.MiscTypeCode;
import sg.com.jp.generalcargo.domain.StevedoreCompany;
import sg.com.jp.generalcargo.domain.VesselCall;
import sg.com.jp.generalcargo.util.BusinessException;

public interface CargoTimeSheetService {

	public List<MiscTypeCode> cc_getMiscTypeCode(String catCd) throws BusinessException;

	public GbccCargoTimesheetVO cc_getCargoTimeSheetById(String vvCd, String stevCoCd, Date crDttm)
			throws BusinessException;

	public boolean cc_persistCargoTimeSheet(GbccCargoTimesheetVO transientObject) throws BusinessException;

	public List<VesselCall> cc_getCargoVesselCall(String custCd) throws BusinessException;

	public List<StevedoreCompany> cc_getCargoStevedore() throws BusinessException;

	public List<String> cc_getCargoBerth(String custCd) throws BusinessException;

	public List<GbccCargoTimesheetVO> cc_getCargoTimeSheet(String CustCode, String vvCd, String stevCoCd, String berthNo, String sortBy,
			Criteria criteria, Boolean needAllData) throws BusinessException;

}
