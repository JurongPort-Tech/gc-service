package sg.com.jp.generalcargo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Component
@ConfigurationProperties(prefix = "printers")
public class PrinterProperties {
	
	private static final Log log = LogFactory.getLog(PrinterProperties.class);
	
    private Map<String, String> mappings;
 
    public Map<String, String> getMappings() {
        return mappings;
    }
 
    public void setMappings(Map<String, String> mappings) {
        this.mappings = mappings;
    }
    
    public String getMappedPrinter(String key) {
        if (mappings == null) {
        	log.info("Warning: Unknown printer key: " + key);
            return "NO_PRINTER_MAPPED";
        }
        return mappings.getOrDefault(key, "NO_PRINTER_MAPPED");
    }
}