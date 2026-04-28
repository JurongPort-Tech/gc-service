package sg.com.jp.generalcargo.service;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;
import sg.com.jp.generalcargo.domain.StoreRentCrReport;
import sg.com.jp.generalcargo.util.BusinessException;

public interface InterTerminalStoreRentService {
	
	public JasperPrint getJasperPrint(String jasperName, Map<String, Object> parameters, String billmonth, List<?> Record) throws BusinessException, Exception;

	public List<StoreRentCrReport> getStoreRentReports(String billmonth, String tsdirection) throws BusinessException;

}
