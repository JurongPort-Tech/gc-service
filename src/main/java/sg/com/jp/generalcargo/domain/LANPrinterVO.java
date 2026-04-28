package sg.com.jp.generalcargo.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
 
public class LANPrinterVO {

	private String filePath;
    private String encodedString;
    private String printerPath;

    private static final Log log = LogFactory.getLog(LANPrinterVO.class);

    public LANPrinterVO() {}
 
    public String getFilePath() {
        return filePath;
    }
 
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
 
    public String getEncodedString() {
        return encodedString;
    }
 
    public void setEncodedString(String encodedString) {
        this.encodedString = encodedString;
    }
 
    public String getPrinterPath() {
        return printerPath;
    }
 
    public void setPrinterPath(String printerPath) {
        this.printerPath = printerPath;
    }

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(new ObjectMapper().writeValueAsString(this));
        } catch (JsonProcessingException j) {
            log.info("Printing j exception in toString" + j);
        	log.info("Exception toString JsonProcessingException: ", j);
            return "";
        } catch (Exception e) {
        	log.info("Exception toString : ", e);
        }
        return sb.toString();
    }
}
 