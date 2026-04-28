package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UACntrJasperReport implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ATB;
	private String COD;
	private String STUFF_DTTM;
	private String TRANSREFNO;
	private String DATETIME;
	private String VSLNM;
	private String VOYNO;
	private String CONTSIZE;
	private String CONTTYPE;
	private String TRANSTYPE;
	private String CRGREF;
	private String ASNNO;
	private String NRICPASSPORTNO;
	private String MARKING;
	private String CRG_DESC;
	private BigDecimal WT;
	private BigDecimal VOL;
	private BigDecimal DECLQTY;
	private BigDecimal TRANSQTY;
	private BigDecimal BALQTY;
	
	private String VEH1;
	private String VEH2;
	private String VEH3;
	private String VEH4;
	private String VEH5;

	private String TARRIF_CD_SER_CHRG;
	private String TARRIF_DESC_SER_CHRG;
	private BigDecimal BILLABLE_TON_SER_CHRG;
	private BigDecimal UNIT_RATE_SER_CHRG;
	private BigDecimal TOTAL_AMT_SER_CHRG;
	
	private String TARRIF_CD_WHARF_CHRG;
	private String TARRIF_DESC_WHARF_CHRG;
	private BigDecimal BILLABLE_TON_WHARF_CHRG;
	private BigDecimal UNIT_RATE_WHARF_CHRG;
	private BigDecimal TOTAL_AMT_WHARF_CHRG;
	
	private String TARRIF_CD_STORE_CHRG;
	private String TARRIF_DESC_STORE_CHRG;
	private BigDecimal BILLABLE_TON_STORE_CHRG;
	private BigDecimal UNIT_RATE_STORE_CHRG;
	private BigDecimal TOTAL_AMT_STORE_CHRG;
	
	private String TARRIF_CD_SR_CHRG;
	private String TARRIF_DESC_SR_CHRG;
	private BigDecimal BILLABLE_TON_SR_CHRG;
	private BigDecimal UNIT_RATE_SR_CHRG;
	private BigDecimal TOTAL_AMT_SER_WHARF_CHRG;
	
	private String UNIT_RATE_SER_WHARF_CHRG;
	private String BILLABLE_TON_SER_WHARF_CHRG;
	private BigDecimal TARRIF_DESC_SER_WHARF_CHRG;
	private BigDecimal TARRIF_CD_SER_WHARF_CHRG;
	private BigDecimal TOTAL_AMT_SR_CHRG;
	
	private BigDecimal TIME_UNIT_SER;
	private BigDecimal TIME_UNIT_WHF;
	private BigDecimal TIME_UNIT_SR;
	private BigDecimal TIME_UNIT_SER_WHF;
	private BigDecimal TIME_UNIT_STORE;

	private String CNTR_NBR;
	private String ACCT_NBR_SER_CHRG;
	private String EDO_ACCT_NBR;
	
	public String getATB() {
		return ATB;
	}

	public void setATB(String aTB) {
		ATB = aTB;
	}

	public String getCOD() {
		return COD;
	}

	public void setCOD(String cOD) {
		COD = cOD;
	}
	
	public String getSTUFF_DTTM() {
		return STUFF_DTTM;
	}

	public void setSTUFF_DTTM(String sTUFF_DTTM) {
		STUFF_DTTM = sTUFF_DTTM;
	}

	public String getTRANSREFNO() {
		return TRANSREFNO;
	}

	public void setTRANSREFNO(String tRANSREFNO) {
		TRANSREFNO = tRANSREFNO;
	}

	public String getDATETIME() {
		return DATETIME;
	}

	public void setDATETIME(String dATETIME) {
		DATETIME = dATETIME;
	}

	public String getVSLNM() {
		return VSLNM;
	}

	public void setVSLNM(String vSLNM) {
		VSLNM = vSLNM;
	}

	public String getVOYNO() {
		return VOYNO;
	}

	public void setVOYNO(String vOYNO) {
		VOYNO = vOYNO;
	}

	public String getCONTSIZE() {
		return CONTSIZE;
	}

	public void setCONTSIZE(String cONTSIZE) {
		CONTSIZE = cONTSIZE;
	}

	public String getCONTTYPE() {
		return CONTTYPE;
	}

	public void setCONTTYPE(String cONTTYPE) {
		CONTTYPE = cONTTYPE;
	}

	public String getTRANSTYPE() {
		return TRANSTYPE;
	}

	public void setTRANSTYPE(String tRANSTYPE) {
		TRANSTYPE = tRANSTYPE;
	}

	public String getCRGREF() {
		return CRGREF;
	}

	public void setCRGREF(String cRGFER) {
		CRGREF = cRGFER;
	}

	public String getASNNO() {
		return ASNNO;
	}

	public void setASNNO(String aSNNO) {
		ASNNO = aSNNO;
	}

	public String getNRICPASSPORTNO() {
		return NRICPASSPORTNO;
	}

	public void setNRICPASSPORTNO(String nRICPASSPORTNO) {
		NRICPASSPORTNO = nRICPASSPORTNO;
	}

	public String getMARKING() {
		return MARKING;
	}

	public void setMARKING(String mARKING) {
		MARKING = mARKING;
	}

	public String getCRG_DESC() {
		return CRG_DESC;
	}

	public void setCRG_DESC(String cARGO_DESC) {
		CRG_DESC = cARGO_DESC;
	}

	public BigDecimal getWT() {
		return WT;
	}

	public void setWT(BigDecimal wT) {
		WT = wT;
	}

	public BigDecimal getVOL() {
		return VOL;
	}

	public void setVOL(BigDecimal vOL) {
		VOL = vOL;
	}

	public BigDecimal getDECLQTY() {
		return DECLQTY;
	}

	public void setDECLQTY(BigDecimal dECLQTY) {
		DECLQTY = dECLQTY;
	}

	public BigDecimal getTRANSQTY() {
		return TRANSQTY;
	}

	public void setTRANSQTY(BigDecimal tRANSQTY) {
		TRANSQTY = tRANSQTY;
	}

	public BigDecimal getBALQTY() {
		return BALQTY;
	}

	public void setBALQTY(BigDecimal bALQTY) {
		BALQTY = bALQTY;
	}

	public String getTARRIF_CD_SER_CHRG() {
		return TARRIF_CD_SER_CHRG;
	}

	public void setTARRIF_CD_SER_CHRG(String tARRIF_CD_SER_CHRG) {
		TARRIF_CD_SER_CHRG = tARRIF_CD_SER_CHRG;
	}

	public String getTARRIF_DESC_SER_CHRG() {
		return TARRIF_DESC_SER_CHRG;
	}

	public void setTARRIF_DESC_SER_CHRG(String tARRIF_DESC_SER_CHRG) {
		TARRIF_DESC_SER_CHRG = tARRIF_DESC_SER_CHRG;
	}

	public BigDecimal getBILLABLE_TON_SER_CHRG() {
		return BILLABLE_TON_SER_CHRG;
	}

	public void setBILLABLE_TON_SER_CHRG(BigDecimal bILLABLE_TON_SER_CHRG) {
		BILLABLE_TON_SER_CHRG = bILLABLE_TON_SER_CHRG;
	}

	public BigDecimal getUNIT_RATE_SER_CHRG() {
		return UNIT_RATE_SER_CHRG;
	}

	public void setUNIT_RATE_SER_CHRG(BigDecimal uNIT_RATE_SER_CHRG) {
		UNIT_RATE_SER_CHRG = uNIT_RATE_SER_CHRG;
	}

	public BigDecimal getTOTAL_AMT_SER_CHRG() {
		return TOTAL_AMT_SER_CHRG;
	}

	public void setTOTAL_AMT_SER_CHRG(BigDecimal tOTAL_AMT_SER_CHRG) {
		TOTAL_AMT_SER_CHRG = tOTAL_AMT_SER_CHRG;
	}

	public String getTARRIF_CD_WHARF_CHRG() {
		return TARRIF_CD_WHARF_CHRG;
	}

	public void setTARRIF_CD_WHARF_CHRG(String tARRIF_CD_WHARF_CHRG) {
		TARRIF_CD_WHARF_CHRG = tARRIF_CD_WHARF_CHRG;
	}

	public String getTARRIF_DESC_WHARF_CHRG() {
		return TARRIF_DESC_WHARF_CHRG;
	}

	public void setTARRIF_DESC_WHARF_CHRG(String tARRIF_DESC_WHARF_CHRG) {
		TARRIF_DESC_WHARF_CHRG = tARRIF_DESC_WHARF_CHRG;
	}

	public BigDecimal getBILLABLE_TON_WHARF_CHRG() {
		return BILLABLE_TON_WHARF_CHRG;
	}

	public void setBILLABLE_TON_WHARF_CHRG(BigDecimal bILLABLE_TON_WHARF_CHRG) {
		BILLABLE_TON_WHARF_CHRG = bILLABLE_TON_WHARF_CHRG;
	}

	public BigDecimal getUNIT_RATE_WHARF_CHRG() {
		return UNIT_RATE_WHARF_CHRG;
	}

	public void setUNIT_RATE_WHARF_CHRG(BigDecimal uNIT_RATE_WHARF_CHRG) {
		UNIT_RATE_WHARF_CHRG = uNIT_RATE_WHARF_CHRG;
	}

	public BigDecimal getTOTAL_AMT_WHARF_CHRG() {
		return TOTAL_AMT_WHARF_CHRG;
	}

	public void setTOTAL_AMT_WHARF_CHRG(BigDecimal tOTAL_AMT_WHARF_CHRG) {
		TOTAL_AMT_WHARF_CHRG = tOTAL_AMT_WHARF_CHRG;
	}

	public String getTARRIF_CD_STORE_CHRG() {
		return TARRIF_CD_STORE_CHRG;
	}

	public void setTARRIF_CD_STORE_CHRG(String tARRIF_CD_STORE_CHRG) {
		TARRIF_CD_STORE_CHRG = tARRIF_CD_STORE_CHRG;
	}

	public String getTARRIF_DESC_STORE_CHRG() {
		return TARRIF_DESC_STORE_CHRG;
	}

	public void setTARRIF_DESC_STORE_CHRG(String tARRIF_DESC_STORE_CHRG) {
		TARRIF_DESC_STORE_CHRG = tARRIF_DESC_STORE_CHRG;
	}

	public BigDecimal getBILLABLE_TON_STORE_CHRG() {
		return BILLABLE_TON_STORE_CHRG;
	}

	public void setBILLABLE_TON_STORE_CHRG(BigDecimal bILLABLE_TON_STORE_CHRG) {
		BILLABLE_TON_STORE_CHRG = bILLABLE_TON_STORE_CHRG;
	}

	public BigDecimal getUNIT_RATE_STORE_CHRG() {
		return UNIT_RATE_STORE_CHRG;
	}

	public void setUNIT_RATE_STORE_CHRG(BigDecimal uNIT_RATE_STORE_CHRG) {
		UNIT_RATE_STORE_CHRG = uNIT_RATE_STORE_CHRG;
	}

	public BigDecimal getTOTAL_AMT_STORE_CHRG() {
		return TOTAL_AMT_STORE_CHRG;
	}

	public void setTOTAL_AMT_STORE_CHRG(BigDecimal tOTAL_AMT_STORE_CHRG) {
		TOTAL_AMT_STORE_CHRG = tOTAL_AMT_STORE_CHRG;
	}

	public String getTARRIF_CD_SR_CHRG() {
		return TARRIF_CD_SR_CHRG;
	}

	public void setTARRIF_CD_SR_CHRG(String tARRIF_CD_SR_CHRG) {
		TARRIF_CD_SR_CHRG = tARRIF_CD_SR_CHRG;
	}

	public String getTARRIF_DESC_SR_CHRG() {
		return TARRIF_DESC_SR_CHRG;
	}

	public void setTARRIF_DESC_SR_CHRG(String tARRIF_DESC_SR_CHRG) {
		TARRIF_DESC_SR_CHRG = tARRIF_DESC_SR_CHRG;
	}

	public BigDecimal getBILLABLE_TON_SR_CHRG() {
		return BILLABLE_TON_SR_CHRG;
	}

	public void setBILLABLE_TON_SR_CHRG(BigDecimal bILLABLE_TON_SR_CHRG) {
		BILLABLE_TON_SR_CHRG = bILLABLE_TON_SR_CHRG;
	}

	public BigDecimal getUNIT_RATE_SR_CHRG() {
		return UNIT_RATE_SR_CHRG;
	}

	public void setUNIT_RATE_SR_CHRG(BigDecimal uNIT_RATE_SR_CHRG) {
		UNIT_RATE_SR_CHRG = uNIT_RATE_SR_CHRG;
	}

	public BigDecimal getTOTAL_AMT_SER_WHARF_CHRG() {
		return TOTAL_AMT_SER_WHARF_CHRG;
	}

	public void setTOTAL_AMT_SER_WHARF_CHRG(BigDecimal tOTAL_AMT_SER_WHARF_CHRG) {
		TOTAL_AMT_SER_WHARF_CHRG = tOTAL_AMT_SER_WHARF_CHRG;
	}

	public String getUNIT_RATE_SER_WHARF_CHRG() {
		return UNIT_RATE_SER_WHARF_CHRG;
	}

	public void setUNIT_RATE_SER_WHARF_CHRG(String uNIT_RATE_SER_WHARF_CHRG) {
		UNIT_RATE_SER_WHARF_CHRG = uNIT_RATE_SER_WHARF_CHRG;
	}

	public String getBILLABLE_TON_SER_WHARF_CHRG() {
		return BILLABLE_TON_SER_WHARF_CHRG;
	}

	public void setBILLABLE_TON_SER_WHARF_CHRG(String bILLABLE_TON_SER_WHARF_CHRG) {
		BILLABLE_TON_SER_WHARF_CHRG = bILLABLE_TON_SER_WHARF_CHRG;
	}

	public BigDecimal getTARRIF_DESC_SER_WHARF_CHRG() {
		return TARRIF_DESC_SER_WHARF_CHRG;
	}

	public void setTARRIF_DESC_SER_WHARF_CHRG(BigDecimal tARRIF_DESC_SER_WHARF_CHRG) {
		TARRIF_DESC_SER_WHARF_CHRG = tARRIF_DESC_SER_WHARF_CHRG;
	}

	public BigDecimal getTARRIF_CD_SER_WHARF_CHRG() {
		return TARRIF_CD_SER_WHARF_CHRG;
	}

	public void setTARRIF_CD_SER_WHARF_CHRG(BigDecimal tARRIF_CD_SER_WHARF_CHRG) {
		TARRIF_CD_SER_WHARF_CHRG = tARRIF_CD_SER_WHARF_CHRG;
	}

	public BigDecimal getTOTAL_AMT_SR_CHRG() {
		return TOTAL_AMT_SR_CHRG;
	}

	public void setTOTAL_AMT_SR_CHRG(BigDecimal tOTAL_AMT_SR_CHRG) {
		TOTAL_AMT_SR_CHRG = tOTAL_AMT_SR_CHRG;
	}

	public BigDecimal getTIME_UNIT_SER() {
		return TIME_UNIT_SER;
	}

	public void setTIME_UNIT_SER(BigDecimal tIME_UNIT_SER) {
		TIME_UNIT_SER = tIME_UNIT_SER;
	}

	public BigDecimal getTIME_UNIT_WHF() {
		return TIME_UNIT_WHF;
	}

	public void setTIME_UNIT_WHF(BigDecimal tIME_UNIT_WHF) {
		TIME_UNIT_WHF = tIME_UNIT_WHF;
	}

	public BigDecimal getTIME_UNIT_SR() {
		return TIME_UNIT_SR;
	}

	public void setTIME_UNIT_SR(BigDecimal tIME_UNIT_SR) {
		TIME_UNIT_SR = tIME_UNIT_SR;
	}

	public BigDecimal getTIME_UNIT_SER_WHF() {
		return TIME_UNIT_SER_WHF;
	}

	public void setTIME_UNIT_SER_WHF(BigDecimal tIME_UNIT_SER_WHF) {
		TIME_UNIT_SER_WHF = tIME_UNIT_SER_WHF;
	}

	public BigDecimal getTIME_UNIT_STORE() {
		return TIME_UNIT_STORE;
	}

	public void setTIME_UNIT_STORE(BigDecimal tIME_UNIT_STORE) {
		TIME_UNIT_STORE = tIME_UNIT_STORE;
	}

	public String getCNTR_NBR() {
		return CNTR_NBR;
	}

	public void setCNTR_NBR(String cNTR_NBR) {
		CNTR_NBR = cNTR_NBR;
	}

	public String getACCT_NBR_SER_CHRG() {
		return ACCT_NBR_SER_CHRG;
	}

	public void setACCT_NBR_SER_CHRG(String aCCT_NBR_SER_CHRG) {
		ACCT_NBR_SER_CHRG = aCCT_NBR_SER_CHRG;
	}

	public String getEDO_ACCT_NBR() {
		return EDO_ACCT_NBR;
	}

	public void setEDO_ACCT_NBR(String eDO_ACCT_NBR) {
		EDO_ACCT_NBR = eDO_ACCT_NBR;
	}

	public String getVEH1() {
		return VEH1;
	}

	public void setVEH1(String vEH1) {
		VEH1 = vEH1;
	}

	public String getVEH2() {
		return VEH2;
	}

	public void setVEH2(String vEH2) {
		VEH2 = vEH2;
	}

	public String getVEH3() {
		return VEH3;
	}

	public void setVEH3(String vEH3) {
		VEH3 = vEH3;
	}

	public String getVEH4() {
		return VEH4;
	}

	public void setVEH4(String vEH4) {
		VEH4 = vEH4;
	}

	public String getVEH5() {
		return VEH5;
	}

	public void setVEH5(String vEH5) {
		VEH5 = vEH5;
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
