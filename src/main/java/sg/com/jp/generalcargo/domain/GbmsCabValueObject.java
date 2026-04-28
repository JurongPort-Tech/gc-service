package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *System Name: GBMS (General Bulk Cargo Management System)
*Component ID: GbmsCabValueObject.java
*Component Description: Stores  Gbms Cab Value Objects
* 
*@author      Balaji R.k.
*@version     17 July 2002
 */

/*
 Revision History
================
* Author   Request Number  Description of Change   Version     Date Released
* Balaji                      Creation				1.0         17 July 2002
 */

public class GbmsCabValueObject implements TopsIObject
{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Timestamp txn_Dttm;
	private String scheme;
	private String bill_acct;

	private String status;
	private String bill_wharf_triggered_ind;
	private String bill_service_triggered_ind;
	private String bill_storerent_triggered_ind;	        
      
	public GbmsCabValueObject(){
	}
	
	public Timestamp getTxnDttm(){
		return txn_Dttm; 
	}
	public void setTxnDttm(Timestamp txnDttm){
		txnDttm=txnDttm; 
	}
	public String getScheme(){
		return scheme; 
	}
	public void setScheme(String Scheme){
		scheme=Scheme; 
	}
	public String getBillAcct(){
		return bill_acct; 
	}
	public void setBillAcct(String billAcct){
		bill_acct=billAcct; 
	}

	public String getBillWharfTriggeredInd(){
		return bill_wharf_triggered_ind; 
	}
	public void setBillWharfTriggeredInd(String billWharfTriggeredInd){
		bill_wharf_triggered_ind=billWharfTriggeredInd; 
	}
	public String getBillServiceTriggeredInd(){
		return bill_service_triggered_ind; 
	}
	public void setBillServiceTriggeredInd(String billServiceTriggeredInd){
		bill_service_triggered_ind=billServiceTriggeredInd; 
	}

	public String getBill_storerent_triggered_ind(){
		return bill_storerent_triggered_ind; 
	}
	public void setBill_storerent_triggered_ind(String bill_storerent_triggered_ind){
		this.bill_storerent_triggered_ind=bill_storerent_triggered_ind; 
	}

	public String getStatus(){
		return status; 
	}
	public void setStatus(String Status){
		status=Status; 
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

