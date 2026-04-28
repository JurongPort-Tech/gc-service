package sg.com.jp.generalcargo.domain;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class SystemConfigList extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(SystemConfigList.class);
	private List<ConfigMsg> remarksDet;
	private String remarks;
	private String  inputTime;
	
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) { 
        ObjectMapper mapper = null; 

        List<ConfigMsg> msg = null; 

        try { 
        	
            if (remarks!=null && !remarks.equalsIgnoreCase("")) { 

                mapper = new ObjectMapper(); 
              msg= Arrays.asList(mapper.readValue(remarks, ConfigMsg.class)); 

            } 

        } catch (Exception e) { 
        	log.info("Exception functionName : ", e);
        } 

        this.remarksDet = msg; 

    } 

	public List<ConfigMsg> getRemarksDet() {
		return remarksDet;
	}

	public void setRemarksDet(List<ConfigMsg> remarksDet) {
		this.remarksDet = remarksDet;
	}

	public String getInputTime() {
		return inputTime;
	}

	public void setInputTime(String inputTime) {
		this.inputTime = inputTime;
	}


	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	 
}
