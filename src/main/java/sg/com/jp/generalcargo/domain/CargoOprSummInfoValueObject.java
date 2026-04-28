package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.jp.generalcargo.util.CommonUtility;

public class CargoOprSummInfoValueObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	private static final Log log = LogFactory.getLog(CargoOprSummInfoValueObject.class);
	
	
	
	private String vvCd;
	private Integer totalComp;
	private Integer totalOpen;
	private Integer totalOpenDisc;
	private Integer totalOpenLoad;
	private Integer totalCompDisc;
	private Integer totalCompLoad;
	private Date commencementDttm;
	private Date completedDttm;
	private Date colDttm;
	private Date codDttm;
	
	private Berthing firstBerthing;
	private Berthing lastBerthing;
	
	private Timestamp max_shift_dttm;
	private Timestamp max_col;
	private Timestamp max_cod;
	private Integer total_disc_completed;
	private Integer total_load_completed;
	private Integer total_disc_openbal;
	private Integer total_load_openbal;
	private Integer total_completed;
	private Integer total_openbal;
	private Timestamp workstartdttm;
	
	
	
	
	
	public Timestamp getMax_shift_dttm() {
		return max_shift_dttm;
	}
	public void setMax_shift_dttm(Timestamp max_shift_dttm) {
		this.max_shift_dttm = max_shift_dttm;
	}
	public Timestamp getMax_col() {
		return max_col;
	}
	public void setMax_col(Timestamp max_col) {
		this.max_col = max_col;
	}
	public Timestamp getMax_cod() {
		return max_cod;
	}
	public void setMax_cod(Timestamp max_cod) {
		this.max_cod = max_cod;
	}
	public Integer getTotal_disc_completed() {
		return total_disc_completed;
	}
	public void setTotal_disc_completed(Integer total_disc_completed) {
		this.total_disc_completed = total_disc_completed;
	}
	public Integer getTotal_load_completed() {
		return total_load_completed;
	}
	public void setTotal_load_completed(Integer total_load_completed) {
		this.total_load_completed = total_load_completed;
	}
	public Integer getTotal_disc_openbal() {
		return total_disc_openbal;
	}
	public void setTotal_disc_openbal(Integer total_disc_openbal) {
		this.total_disc_openbal = total_disc_openbal;
	}
	public Integer getTotal_load_openbal() {
		return total_load_openbal;
	}
	public void setTotal_load_openbal(Integer total_load_openbal) {
		this.total_load_openbal = total_load_openbal;
	}
	public Integer getTotal_completed() {
		return total_completed;
	}
	public void setTotal_completed(Integer total_completed) {
		this.total_completed = total_completed;
	}
	public Integer getTotal_openbal() {
		return total_openbal;
	}
	public void setTotal_openbal(Integer total_openbal) {
		this.total_openbal = total_openbal;
	}
	public Timestamp getWorkstartdttm() {
		return workstartdttm;
	}
	public void setWorkstartdttm(Timestamp workstartdttm) {
		this.workstartdttm = workstartdttm;
	}
	
	public String getVvCd() {
    	return vvCd;
    }
    public void setVvCd(String vvCd) {
    	this.vvCd = vvCd;
    }
    public Integer getTotalComp() {
    	return totalComp;
    }
    public void setTotalComp(Integer totalComp) {
    	this.totalComp = totalComp;
    }
    public Integer getTotalOpen() {
    	return totalOpen;
    }
    public void setTotalOpen(Integer totalOpen) {
    	this.totalOpen = totalOpen;
    }
    public Integer getTotalOpenDisc() {
    	return totalOpenDisc;
    }
    public void setTotalOpenDisc(Integer totalOpenDisc) {
    	this.totalOpenDisc = totalOpenDisc;
    }
    public Integer getTotalOpenLoad() {
    	return totalOpenLoad;
    }
    public void setTotalOpenLoad(Integer totalOpenLoad) {
    	this.totalOpenLoad = totalOpenLoad;
    }
    public Integer getTotalCompDisc() {
    	return totalCompDisc;
    }
    public void setTotalCompDisc(Integer totalCompDisc) {
    	this.totalCompDisc = totalCompDisc;
    }
    public Integer getTotalCompLoad() {
    	return totalCompLoad;
    }
    public void setTotalCompLoad(Integer totalCompLoad) {
    	this.totalCompLoad = totalCompLoad;
    }
    
    public Date getCommencementDttm() {
    	return commencementDttm;
    }
    public void setCommencementDttm(Date commencementDttm) {
    	this.commencementDttm = commencementDttm;
    }
    
    public Date getCompletedDttm() {
    	return completedDttm;
    }
    public void setCompletedDttm(Date completedDttm) {
    	this.completedDttm = completedDttm;
    }
    public Date getColDttm() {
    	return colDttm;
    }
    public void setColDttm(Date colDttm) {
    	this.colDttm = colDttm;
    }
    public Date getCodDttm() {
    	return codDttm;
    }
    public void setCodDttm(Date codDttm) {
    	this.codDttm = codDttm;
    }
    
    public Berthing getFirstBerthing() {
    	return firstBerthing;
    }
    public void setFirstBerthing(Berthing firstBerthing) {
    	this.firstBerthing = firstBerthing;
    }
    public Berthing getLastBerthing() {
    	return lastBerthing;
    }
    public void setLastBerthing(Berthing lastBerthing) {
    	this.lastBerthing = lastBerthing;
    }
    
    public Date getFirstActDttm() {
    	Date startDttm;
    	if (firstBerthing == null)
    		startDttm = commencementDttm;
    	else {
    		if (firstBerthing.getGbFirstActDttm() == null)
    			startDttm = commencementDttm;
    		else
    			startDttm = firstBerthing.getGbFirstActDttm();
    	}
    	return startDttm;
    }
    
    public Date getLastActDttm() {
    	Date endDttm;
    	if (lastBerthing == null)
    		endDttm = completedDttm;
    	else {
    		if (lastBerthing.getGbLastActDttm() == null)
    			endDttm = completedDttm;
    		else
    			endDttm = lastBerthing.getGbLastActDttm();
    		
    	}
    	return endDttm;
    }
    
    public Long getProdRate() {
    	Long prodRate = new Long(0);
    	
    	Date startDttm = getFirstActDttm();
    	Date endDttm = getLastActDttm();
    	
    	Date colDttmAct = lastBerthing.getGbColDttm();
    	Date codDttmAct = lastBerthing.getGbCodDttm();
    	Date actualCompletionDttm = endDttm;
    	
    	if(codDttmAct != null && colDttmAct != null){
     		if(CommonUtility.isStartBeforeEndDate(colDttmAct, codDttmAct)){
     			actualCompletionDttm = codDttmAct;
     		} else {
     			actualCompletionDttm = colDttmAct;
     		}     		
    	} else 	if(colDttmAct != null && codDttmAct == null){
    		actualCompletionDttm = colDttmAct;
    	} else if(codDttmAct != null && colDttmAct == null){
    		actualCompletionDttm = codDttmAct;
    	}   	
    	if (endDttm == null)
    		return prodRate;
    	
    	if(actualCompletionDttm == null)
    		return prodRate;
    	
    	if (startDttm == null)
    		return prodRate;
    	
    	//long totalWorkingHr = (((endDttm.getTime() - startDttm.getTime())/1000)/60)/60; Bhuvana    	
    	//double totalWorkingHr = (double)(endDttm.getTime() - startDttm.getTime())/(1000*60*60);
    	double totalWorkingHr = (double)(actualCompletionDttm.getTime() - startDttm.getTime())/(1000*60*60);
    	if (totalWorkingHr == 0)
    		return prodRate;
    	
    	//long perHrProd = (long) (totalComp.intValue()/totalWorkingHr); Bhuvana    	
    	double perHrProdDouble = (double)(totalComp.intValue()/CommonUtility.formatNumToScale(totalWorkingHr,2));
    	long perHrProd = (long)perHrProdDouble;
    	
    	log.info("NettProd->STime=" + startDttm + ";ETime=" + actualCompletionDttm + " PerHrProdDoub=" + perHrProdDouble + ";PerHrProd="
				+ perHrProd);
    	
    	return new Long(perHrProd);
    }
    public Long getProdRateGross() {
    	if (firstBerthing == null)
    		return new Long(0);
    	if (lastBerthing == null)
    		return new Long(0);
    	
    	Date startDttm = firstBerthing.getAtbDttm();
    	if (startDttm == null)
    		return new Long(0);
    	Date endDttm = lastBerthing.getAtuDttm();
    	if (endDttm == null)
    		endDttm = getLastActDttm();
    	
    	//long totalWorkingHr = (((endDttm.getTime() - startDttm.getTime())/1000)/60)/60; Bhuvana    	
    	double totalWorkingHr = (double)(endDttm.getTime() - startDttm.getTime())/(1000*60*60);
    	
    	if (totalWorkingHr == 0)
    		return new Long(0);
    	
    	//long perHrProd = (long) (totalComp.intValue()/totalWorkingHr); Bhuvana
    	double perHrProdDouble = (double) (totalComp.intValue()/CommonUtility.formatNumToScale(totalWorkingHr,2));
    	long perHrProd = (long)perHrProdDouble;
    	log.info("GrossProd->STime=" + startDttm + ";ETime=" + endDttm + " PerHrProdDoub=" + perHrProdDouble + ";PerHrProd="
				+ perHrProd);
    	return new Long(perHrProd);
    	
    	
    	
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
