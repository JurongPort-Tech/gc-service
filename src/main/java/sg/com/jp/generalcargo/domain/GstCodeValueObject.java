package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
public class GstCodeValueObject extends CodeVO implements TopsIObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double gstCharge;
    private Timestamp startDate;
    private Timestamp endDate;
    
    /** Creates new GstCodeValueObject */
    public GstCodeValueObject() {
    }

    public void setGstCharge (double gstCharge) {
        this.gstCharge = gstCharge;
    }
    
    public void setStartDate (Timestamp startDate) {
        this.startDate = startDate;
    }
    
    public void setEndDate (Timestamp endDate) {
        this.endDate = endDate;
    }
    
    public double getGstCharge () {
        return gstCharge;
    }
    
    public Timestamp getStartDate () {
        return startDate;
    }
    
    public Timestamp getEndDate () {
        return endDate;
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
