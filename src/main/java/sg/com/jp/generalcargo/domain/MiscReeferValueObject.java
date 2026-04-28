package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MiscReeferValueObject implements TopsIObject{

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] cntrNo;
	    private String[] cntrSize;
	    private String[] cntrStatus;
	    private String[] plugInDt;
	    private String[] plugInTime;
	    private String[] plugOutDt;
	    private String[] plugOutTime;
	    private String[] deliveryDttm;
	    private String[] dnPoNbr;
	    private String[] remarks;
	    
	    /**
	     * @return Returns the cntrNo.
	     */
	    public String[] getCntrNo() {
	        return cntrNo;
	    }
	    /**
	     * @param cntrNo The cntrNo to set.
	     */
	    public void setCntrNo(String[] cntrNo) {
	        this.cntrNo = cntrNo;
	    }
	    /**
	     * @return Returns the cntrSize.
	     */
	    public String[] getCntrSize() {
	        return cntrSize;
	    }
	    /**
	     * @param cntrSize The cntrSize to set.
	     */
	    public void setCntrSize(String[] cntrSize) {
	        this.cntrSize = cntrSize;
	    }
	    /**
	     * @return Returns the cntrStatus.
	     */
	    public String[] getCntrStatus() {
	        return cntrStatus;
	    }
	    /**
	     * @param cntrStatus The cntrStatus to set.
	     */
	    public void setCntrStatus(String[] cntrStatus) {
	        this.cntrStatus = cntrStatus;
	    }
	    
	    /**
	     * @return Returns the deliveryDttm.
	     */
	    public String[] getDeliveryDttm() {
	        return deliveryDttm;
	    }
	    /**
	     * @param deliveryDttm The deliveryDttm to set.
	     */
	    public void setDeliveryDttm(String[] deliveryDttm) {
	        this.deliveryDttm = deliveryDttm;
	    }
	    /**
	     * @return Returns the dnPoNbr.
	     */
	    public String[] getDnPoNbr() {
	        return dnPoNbr;
	    }
	    /**
	     * @param dnPoNbr The dnPoNbr to set.
	     */
	    public void setDnPoNbr(String[] dnPoNbr) {
	        this.dnPoNbr = dnPoNbr;
	    }
	 
	    /**
	     * @return Returns the plugInDt.
	     */
	    public String[] getPlugInDt() {
	        return plugInDt;
	    }
	    /**
	     * @param plugInDt The plugInDt to set.
	     */
	    public void setPlugInDt(String[] plugInDt) {
	        this.plugInDt = plugInDt;
	    }
	    /**
	     * @return Returns the plugOutDt.
	     */
	    public String[] getPlugOutDt() {
	        return plugOutDt;
	    }
	    /**
	     * @param plugOutDt The plugOutDt to set.
	     */
	    public void setPlugOutDt(String[] plugOutDt) {
	        this.plugOutDt = plugOutDt;
	    }
	    /**
	     * @return Returns the remarks.
	     */
	    public String[] getRemarks() {
	        return remarks;
	    }
	    /**
	     * @param remarks The remarks to set.
	     */
	    public void setRemarks(String[] remarks) {
	        this.remarks = remarks;
	    }
	    /**
	     * @return Returns the plugInTime.
	     */
	    public String[] getPlugInTime() {
	        return plugInTime;
	    }
	    /**
	     * @param plugInTime The plugInTime to set.
	     */
	    public void setPlugInTime(String[] plugInTime) {
	        this.plugInTime = plugInTime;
	    }
	    /**
	     * @return Returns the plugOutTime.
	     */
	    public String[] getPlugOutTime() {
	        return plugOutTime;
	    }
	    /**
	     * @param plugOutTime The plugOutTime to set.
	     */
	    public void setPlugOutTime(String[] plugOutTime) {
	        this.plugOutTime = plugOutTime;
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
