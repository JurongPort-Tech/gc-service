package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Copyright 2001 Software Design and Consultancy Pte Ltd. All Rights Reserved.
 * System Name          : GBMS (General and Bulk Cargo Management Systems)
 * Module               : cargo - manifest
 * Component ID         : ManifestValueObject.java
 * Component Description:
 *
 * @author
 * @version
 */

/*
 * Revision History
 * ----------------
 * Author    Request Number      Description of Change                Version   Date Released
 *           JPPL/IT/001/2001    Creation                             1.0
 *           - Phase 1
 *
 * Vani      JPPL/IT/001/2001    Changed to add UnStuff               1.3       30 Oct 2003
 *           - Phase 2           Indicator
 *
 * TVS       JPPL/IT/001/2001    Added new variable
 *           - Phase 2           unstuffclosestatus and               1.4       27 Nov 2003
 *                               set and get method for accessing it
 *
 * Irene Tan GSL-2003-000084     To disallow deletion of bulk         1.5       19 Dec 2003
 *                               manifest after EDO created
 * MCC             20 Feb 2015 Added EPC_IND in ESN
 */

public class CcStuffingVo implements TopsIObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3394986344682532670L;
	String seqNo;
    String vvCode;
    String cntrNo;
    String cntrSeqNo;
    String userId;
    String stuffdttm;
    String waivecharge;
    
	public String getSeqNo() {
		return seqNo;
	}
	
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	
	public String getVvCode() {
		return vvCode;
	}
	
	public void setVvCode(String vvCode) {
		this.vvCode = vvCode;
	}
	
	public String getCntrNo() {
		return cntrNo;
	}
	
	public void setCntrNo(String cntrNo) {
		this.cntrNo = cntrNo;
	}
	
	public String getCntrSeqNo() {
		return cntrSeqNo;
	}
	
	public void setCntrSeqNo(String cntrSeqNo) {
		this.cntrSeqNo = cntrSeqNo;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStuffdttm() {
		return stuffdttm;
	}

	public void setStuffdttm(String stuffdttm) {
		this.stuffdttm = stuffdttm;
	}

	public String getWaivecharge() {
		return waivecharge;
	}

	public void setWaivecharge(String waivecharge) {
		this.waivecharge = waivecharge;
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

