package sg.com.jp.generalcargo.domain;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class ReportValueObject implements TopsIValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String PARAM_DATE = "DATE";
	public static final String PARAM_RANDOM = "RANDOM";
	public static final String PARAM_EXTENSION = "EXTENSION";
	public static final String PARAM_PAGE_SIZE = "PAGE_SIZE";
	public static final String PARAM_PAGE_ORIENTATION = "PAGE_ORIENTATION";
	public static final String PARAM_PRINTER = "PRINTER";
	public static final String PARAM_REPORT = "REPORT";
	public static final String PARAM_OTHER = "OTHER";

	private static final String promptVar = "prompt";
	public static final String PAPER_SIZE_A3 = "a3";
	public static final String PAPER_SIZE_A4 = "a4";
	public static final String PAPER_SIZE_A5 = "a5";
	public static final String PAPER_SIZE_B4 = "b4";
	public static final String PAPER_SIZE_B5 = "b5";
	public static final String PAPER_SIZE_LETTER = "letter";
	
	public static final String ORIENTATION_POTRAIT = null;
	public static final String ORIENTATION_LANDSCAPE = "-landscape";
	
	public static final String EXPORT_RTF = "u2frtf";
	public static final String EXPORT_HTML = "u2fhtm";
	public static final String EXPORT_PDF = "u2fpdf";
	//TuanTA10 start at 16/08/2007
	public static final String EXPORT_XLS ="u2fxls";
	
	private Properties prop;
	private Map<String, String> params;
	private int promptPos = 1;
	private String reportFileName = null;
	private String reportQueryFileName = null;
	private String reportPageSize = PAPER_SIZE_A4;
	private String printerName = null;
	private String printOrientation = "";
	private int numOfPages = 0;
	private boolean spool = true;
	private String option = null;
	private String filenameAppend = null;
	private String outputDirectory = null;
	private String exportFormat = null;
	
	public ReportValueObject() {
		prop = new Properties();
		prop.setProperty("cmd", "rfsh");

		//Set number of pages prompt field to 0
		prop.setProperty(promptVar + "0", "0");

		spool = true;
		option = "mtypePreprinted";
		filenameAppend = null;
		this.outputDirectory = null;
		this.exportFormat = EXPORT_PDF;
	}

	public void doGet(Object object) {}	
	public void doSet(Object object) {}
	
	/** Getter for property value.
	 * @return Value of property value.
	 */
	public Properties getProperties() {
		return prop;
	}
	
	public void addStringPrompt(String strValue) {
		setPrompt(strValue);
	}
	
	private synchronized void setPrompt(String promptVal) {
		prop.setProperty(promptVar + promptPos, promptVal);
		promptPos++;
	}
	
	public void setReportFileName(String reportFileName) { this.reportFileName = reportFileName; }
	public String getReportFileName() { return reportFileName; }
	
	public void setReportQueryFileName(String reportQueryFileName) { this.reportQueryFileName = reportQueryFileName; }
	public String getReportQueryFileName() { return reportQueryFileName; }

	public void setReportPageSize(String reportPageSize) { this.reportPageSize = reportPageSize; }
	public String getReportPageSize() { return reportPageSize; }
	
	public void setPrinterName(String printerName) { this.printerName = printerName; }
	public String getPrinterName() { return printerName; }

	public void setPrintOrientation(String printOrientation) { this.printOrientation = printOrientation; }
	public String getPrintOrientation() { return printOrientation; }
	
	public void setNumOfPages(int numOfPages) {
		this.numOfPages = numOfPages;
		prop.setProperty(promptVar + "0", Integer.toString(numOfPages));
	}
	public int getNumOfPages() { return numOfPages; }
	
	public boolean isSpool() { return this.spool; }
	public void setSpool(boolean spool) { this.spool = spool; }

	public String getOption() { return this.option; }
	public void setOption(String option) { this.option = option; }

	public String getFilenameAppend() { return this.filenameAppend; }
	public void setFilenameAppend(String filenameAppend) { this.filenameAppend = filenameAppend; }
	
	public String getOutputDirectory() { return this.outputDirectory; }
	public void setOutputDirectory(String outputDirectory) { this.outputDirectory = outputDirectory; }
	
	public String getExportFormat() { return this.exportFormat; }
	public void setExportFormat(String exportFormat) { this.exportFormat = exportFormat; }
	
	
	
	public String getParam(String key){
		if(params != null && params.containsKey(key))
			return (String)params.get(key);
		
		return "";
	}
	//TuanTA10 end at 16/08/2007
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ReportFileName: ");
		sb.append(this.reportFileName);
		sb.append(" ReportQueryFileName: ");
		sb.append(this.reportQueryFileName);
		sb.append(" IsSpool: ");
		sb.append(this.spool);
		sb.append(" OutputDirectoy: ");
		sb.append(this.outputDirectory);
		sb.append(" Option: ");
		sb.append(this.option);
		sb.append(" FilenameAppend: ");
		sb.append(this.filenameAppend);
		sb.append(" ExportFormat: ");
		sb.append(this.exportFormat);
		Iterator<?> iter = this.prop.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry)iter.next();
			sb.append(" ");
			sb.append(entry.getKey());
			sb.append(": ");
			sb.append(entry.getValue());
		}
		
		//TuanTA10 start at 16/08/2007
		//iter = this.params.entrySet().iterator();
		//while (iter.hasNext()) {
		//	Map.Entry entry = (Map.Entry)iter.next();
		//	sb.append(" ");
		//	sb.append(entry.getKey());
		//	sb.append(": ");
		//	sb.append(entry.getValue());
		//}
		//TuanTA10 end at 16/08/2007		
		
		return sb.toString();
	}
	
	//CuongTM add on 18 Feb 2008
	public Map<String, String> getParams() {
		return params;
	}
	
    // Added by MC Consulting for E-Invoice enhancements
    private boolean isTaxInvoice;
    private boolean isSupportDocument;
    private boolean isDnCn;

    public boolean isTaxInvoice() {
        return isTaxInvoice;
    }

    public boolean isSupportDocument() {
        return isSupportDocument;
    }

    public boolean isDnCn() {
        return isDnCn;
    }
    // End of addition by MC Consulting for E-Invoice enhancements
    
//	@Override
//	public String toString() {
//		try {
//			return new ObjectMapper().writeValueAsString(this);
//		} catch (JsonProcessingException e) {
//			return "";
//		}
//	}
}
