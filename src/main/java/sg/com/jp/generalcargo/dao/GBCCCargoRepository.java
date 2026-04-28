package sg.com.jp.generalcargo.dao;

import java.util.Date;
import java.util.List;

import sg.com.jp.generalcargo.domain.Berthing;
import sg.com.jp.generalcargo.domain.CargoOprSummInfoValueObject;
import sg.com.jp.generalcargo.domain.Criteria;
import sg.com.jp.generalcargo.domain.GbccCargoOpenBalVO;
import sg.com.jp.generalcargo.domain.GbccCargoOprPlanDet;
import sg.com.jp.generalcargo.domain.GbccCargoOprPlanVO;
import sg.com.jp.generalcargo.domain.GbccCargoOprVO;
import sg.com.jp.generalcargo.domain.GbccCargoTallysheetVO;
import sg.com.jp.generalcargo.domain.GbccCargoTimesheetVO;
import sg.com.jp.generalcargo.domain.GbccRulePara;
import sg.com.jp.generalcargo.domain.GbccViewVvStevedore;
import sg.com.jp.generalcargo.domain.GbccVslProd;
import sg.com.jp.generalcargo.domain.MiscTypeCode;
import sg.com.jp.generalcargo.domain.StevedoreCompany;
import sg.com.jp.generalcargo.domain.TableResult;
import sg.com.jp.generalcargo.domain.VesselCall;
import sg.com.jp.generalcargo.util.BusinessException;

public interface GBCCCargoRepository {

	public GbccCargoOprPlanVO getCargoOprPlanById(String vvCd, String stevCd, Date crDttm) throws BusinessException;

	public boolean saveCargoOprPlan(GbccCargoOprPlanVO transientObject) throws BusinessException;

	public void addCargoOprPlanDet(GbccCargoOprPlanDet transientObject) throws BusinessException;

	public TableResult getCargoOprPlan(String CustCode, String sortBy, Criteria criteria, Boolean needAllData)
			throws BusinessException;

	public TableResult getCargoOprPlan(String CustCode, String sortBy, Criteria criteria, Boolean needAllData,
			String ETBFrom, String ETBTo, String listAllChk) throws BusinessException;

	public String getCompanyName(String coCd) throws BusinessException;

	public List<MiscTypeCode> getMiscTypeCode(String catCd) throws BusinessException;

	public GbccCargoOpenBalVO getCargoOpenBalById(String vvCd, String stevCd) throws BusinessException;

	public boolean saveCargoOpenBal(GbccCargoOpenBalVO transientObject) throws BusinessException;

	public List<GbccCargoOpenBalVO> getCargoOpenBal(String vvCd, String CustCode, String sortBy, Criteria criteria,
			Boolean needAllData, String ATBFrom, String ATBTo, String listAllChk) throws BusinessException;

	public List<VesselCall> getCargoOprVesselCall(String custCd) throws BusinessException;

	public List<StevedoreCompany> getCargoOprStevedore() throws BusinessException;

	public List<String> getCargoOprBerth(String custCd) throws BusinessException;

	public List<GbccViewVvStevedore> getViewVvStevedoreByVvCd(String vvCd, String coCd) throws BusinessException;

	public Berthing getFirstBerthing(String vvCd) throws BusinessException;

	public List<GbccCargoOprVO> getCargoOprByShiftDate(String vvCd, String stevCoCd, Date crDttm)
			throws BusinessException;

	public List<GbccCargoOprVO> getCargoOpr(String vvCd, String berthNo, String CustCode, String shiftCd,
			Date shiftDttm, String sortBy, Criteria criteria, Boolean needAllData) throws BusinessException;

	public GbccCargoOprVO getCargoOprById(String vvCd, String stevCd, Date crDttm, String shiftCd, Date shiftDttm,
			String custCd) throws BusinessException;

	public GbccRulePara getGbccRuleParaById(String paraCd) throws BusinessException;

	public GbccCargoOprVO getCargoOprById(String vvCd, String stevCd, Date crDttm) throws BusinessException;

	public GbccCargoOprVO getCargoOprByVvCdCustCd(String vvCd, String custCd) throws BusinessException;

	public GbccVslProd getVslProdById(String vvCd, String stevCd, Date crDttm) throws BusinessException;

	public boolean saveCargoOpr(GbccCargoOprVO transientObject) throws BusinessException;

	public Berthing getLastBerthing(String vvCd) throws BusinessException;

	public CargoOprSummInfoValueObject getCargoOprSumm(String vvCd) throws BusinessException;

	public CargoOprSummInfoValueObject getCargoOprPrevCompleted(String vvCd, String stevCd, String shiftCd,
			Date shiftDttm) throws BusinessException;

	public boolean saveVslProd(GbccVslProd transientObject) throws BusinessException;

	public GbccVslProd getVslProdById(String vvCd, String stevCd, String shiftCd, Date shiftDttm)
			throws BusinessException;

	public GbccCargoTallysheetVO getCargoTallySheetById(String custCd, String vvCd, Date crDttm, String oprType,
			Integer hatchNo, String stevCd) throws BusinessException;

	public boolean saveCargoTallySheet(GbccCargoTallysheetVO transientObject) throws BusinessException;

	public List<GbccCargoTallysheetVO> getCargoTallySheet(String CustCode, String vvCd, String berthNo, String oprType,
			Integer hatchNo, String sortBy, Criteria criteria, Boolean needAllData) throws BusinessException;

	public GbccCargoTimesheetVO getCargoTimeSheetById(String vvCd, String stevCoCd, Date crDttm)
			throws BusinessException;

	public boolean saveCargoTimeSheet(GbccCargoTimesheetVO transientObject) throws BusinessException;

	public List<VesselCall> getCargoVesselCall(String custCd) throws BusinessException;

	public List<StevedoreCompany> getCargoStevedore() throws BusinessException;

	public List<String> getCargoBerth(String custCd) throws BusinessException;

	public List<GbccCargoTimesheetVO> getCargoTimeSheet(String CustCode, String vvCd, String stevCoCd, String berthNo,
			String sortBy, Criteria criteria, Boolean needAllData) throws BusinessException;
}
