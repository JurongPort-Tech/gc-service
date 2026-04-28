package sg.com.jp.generalcargo.service.impl;

import java.util.List;
import java.util.Map;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperPrint;
import sg.com.jp.generalcargo.dao.ShipStoreRepository;
import sg.com.jp.generalcargo.domain.StoreRentCrReport;
import sg.com.jp.generalcargo.service.InterTerminalStoreRentService;
import sg.com.jp.generalcargo.util.BusinessException;
import sg.com.jp.generalcargo.util.CommonUtility;
import sg.com.jp.generalcargo.util.JasperUtil;

@Service("InterTerminalStoreRentService")
public class InterTerminalStoreRentServiceImpl implements InterTerminalStoreRentService{

	private static final Log log = LogFactory.getLog(InterTerminalStoreRentServiceImpl.class);
	@Autowired
	private ShipStoreRepository shipStoreRepository;
	
	
	@Override
	public JasperPrint getJasperPrint(String jasperName, Map<String, Object> parameters,String billmonth, List<?> records) throws Exception {
		JasperPrint jasperPrint = null;
		try {
			log.info("START: getJasperPrint "+" jasperName:"+CommonUtility.deNull(jasperName) +" parameters:"+ parameters
					+" billmonth:"+ CommonUtility.deNull(billmonth) +" records:"+records.size());
			String billMonth = CommonUtility.deNull(billmonth);
			DateTimeFormatter inputFmt = DateTimeFormatter.ofPattern("MM/yyyy");
			DateTimeFormatter outputFmt = DateTimeFormatter.ofPattern("MMM yyyy");
			YearMonth ym = YearMonth.parse(billMonth, inputFmt);
			String reportMonth = ym.format(outputFmt); 
			log.info("reportMonth in getJasperPrint" + reportMonth);
			parameters.put("P_REPORT_MONTH", reportMonth);
			jasperPrint = JasperUtil.jasperPrint(parameters, jasperName, billmonth, records);
			
			log.info("END: *** getJasperPrint Result *****" + jasperPrint.toString());

		
		} catch (Exception e) {
			log.info("Exception getJasperPrint : ", e);
			throw new Exception(e.getMessage());
		}
		return jasperPrint;
	}


	@Override
	public List<StoreRentCrReport> getStoreRentReports(String billmonth, String tsdirection) throws BusinessException {
		return shipStoreRepository.getStoreRentReports( billmonth, tsdirection);
	}

}
