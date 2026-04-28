package sg.com.jp.generalcargo.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbArrivalWaiver implements TopsIObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String gbArrivalWaiverInd;
    private BigDecimal gbArrivalWaiverAmount;


    public String getGbArrivalWaiverInd() {
        return gbArrivalWaiverInd;
    }

    public void setGbArrivalWaiverInd(String gbArrivalWaiverInd) {
        this.gbArrivalWaiverInd = gbArrivalWaiverInd;
    }

    public BigDecimal getGbArrivalWaiverAmount() {
        return gbArrivalWaiverAmount;
    }

    public void setGbArrivalWaiverAmount(BigDecimal gbArrivalWaiverAmount) {
        this.gbArrivalWaiverAmount = gbArrivalWaiverAmount;
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
