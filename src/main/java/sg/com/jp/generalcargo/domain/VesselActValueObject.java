package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VesselActValueObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private java.lang.String var_nbr;

	private java.lang.String terminal;

	private java.lang.String vsl_nm;

	private java.lang.String in_voy_nbr;

	private java.lang.String out_voy_nbr;

	private java.lang.String atb_dttm;

	private java.lang.String atu_dttm;

	private java.lang.String cod_dttm;

	private java.lang.String col_dttm;

	private java.lang.String bcod_dttm;

	private java.lang.String bcol_dttm;

	private java.lang.String first_act_dttm;

	private java.lang.String last_act_dttm;

	/* Start fix Santosh */
	private java.lang.String first_cargo_act_dttm;

	/* End fix Santosh */
	private java.lang.String gb_close_vsl_ind;

	private java.lang.String vv_status_ind;

	private java.lang.String scheme;

	// modifed after may 02
	private java.lang.String shift_ind;

	private java.lang.String mvt_ind;

	private java.lang.String etb_dttm;

	private java.lang.String etu_dttm;

	private java.lang.String today_dttm;

	private String lineTowedVessel; // by Yan Jun

	// START Code added by Tirumal for imlementing VesselActivity CR, dated
	// 04-12-2007
	private String noOfGangsSupplied;

	private String noOfWorkableHatches;

	private String reasonForDelay;

	private String remarks;

	private List<String> tempList;

	// END Code added by Tirumal for imlementing VesselActivity CR, dated
	// 04-12-2007
	// START Code added by Madhu(Zensar) for VesselProductivty Billing CR, dated
	// 25-09-2008
	private String totGenCargoActivity;
	// END Code added by Madhu(Zensar) for VesselProductivty Billing CR, dated
	// 25-09-2008

	private String gbArrivalWaiverInd;
	private BigDecimal gbArrivalWaiverAmount;

	// 20190328 koktsing SSL-OPS-0000623
	// determine GB/CT last activity date based on later date among COD_DTTM,
	// COL_DTTM, GB_LAST_ACT_DTTM
	private java.lang.String cntrCodDttm;

	private java.lang.String cntrColDttm;

	private java.lang.String combiGcOpsInd;

	public String getLineTowedVessel() {
		return lineTowedVessel;
	}

	public void setLineTowedVessel(String lineTowedVessel) {
		this.lineTowedVessel = lineTowedVessel;
	}

	public VesselActValueObject() {
	}

	public java.lang.String getVarNbr() {
		return var_nbr;
	}

	public void setVarNbr(java.lang.String varNbr) {
		var_nbr = varNbr;
	}

	public java.lang.String getTerminal() {
		return terminal;
	}

	public void setTerminal(java.lang.String terminal) {
		this.terminal = terminal;
	}

	public java.lang.String getVslNm() {
		return vsl_nm;
	}

	public void setVslNm(java.lang.String vslNm) {
		vsl_nm = vslNm;
	}

	public java.lang.String getInVoyNbr() {
		return in_voy_nbr;
	}

	public void setInVoyNbr(java.lang.String inVoyNbr) {
		in_voy_nbr = inVoyNbr;
	}

	public java.lang.String getOutVoyNbr() {
		return out_voy_nbr;
	}

	public void setOutVoyNbr(java.lang.String outVoyNbr) {
		out_voy_nbr = outVoyNbr;
	}

	public java.lang.String getAtbDttm() {
		return atb_dttm;
	}

	public void setAtbDttm(java.lang.String atbDttm) {
		atb_dttm = atbDttm;
	}

	public java.lang.String getAtuDttm() {
		return atu_dttm;
	}

	public void setAtuDttm(java.lang.String atuDttm) {
		atu_dttm = atuDttm;
	}

	public java.lang.String getCodDttm() {
		return cod_dttm;
	}

	public void setCodDttm(java.lang.String codDttm) {
		cod_dttm = codDttm;
	}

	public java.lang.String getColDttm() {
		return col_dttm;
	}

	public void setColDttm(java.lang.String colDttm) {
		col_dttm = colDttm;
	}

	public java.lang.String getBcodDttm() {
		return bcod_dttm;
	}

	public void setBcodDttm(java.lang.String bcodDttm) {
		bcod_dttm = bcodDttm;
	}

	public java.lang.String getBcolDttm() {
		return bcol_dttm;
	}

	public void setBcolDttm(java.lang.String bcolDttm) {
		bcol_dttm = bcolDttm;
	}

	public java.lang.String getFirstActDttm() {
		return first_act_dttm;
	}

	public void setFirstActDttm(java.lang.String firstActDttm) {
		first_act_dttm = firstActDttm;
	}

	public java.lang.String getLastActDttm() {
		return last_act_dttm;
	}

	public void setLastActDttm(java.lang.String lastActDttm) {
		last_act_dttm = lastActDttm;
	}

	/* Start fix Santosh */
	public java.lang.String getFirstCargoActDttm() {
		return first_cargo_act_dttm;
	}

	public void setFirstCargoActDttm(java.lang.String firstCargoActDttm) {
		first_cargo_act_dttm = firstCargoActDttm;
	}

	/* End fix Santosh */
	public java.lang.String getGbCloseVslInd() {
		return gb_close_vsl_ind;
	}

	public void setGbCloseVslInd(java.lang.String gbCloseVslInd) {
		gb_close_vsl_ind = gbCloseVslInd;
	}

	public java.lang.String getVvStatusInd() {
		return vv_status_ind;
	}

	public void setVvStatusInd(java.lang.String vvStatusInd) {
		vv_status_ind = vvStatusInd;
	}

	public java.lang.String getScheme() {
		return scheme;
	}

	public void setScheme(java.lang.String Scheme) {
		scheme = Scheme;
	}

	// modifed after may 02
	public java.lang.String getShiftInd() {
		return shift_ind;
	}

	public void setShiftInd(java.lang.String shiftInd) {
		shift_ind = shiftInd;
	}

	public java.lang.String getMvtInd() {
		return mvt_ind;
	}

	public void setMvtInd(java.lang.String mvtInd) {
		mvt_ind = mvtInd;
	}

	public java.lang.String getEtbDttm() {
		return etb_dttm;
	}

	public void setEtbDttm(java.lang.String etbDttm) {
		etb_dttm = etbDttm;
	}

	public java.lang.String getEtuDttm() {
		return etu_dttm;
	}

	public void setEtuDttm(java.lang.String etuDttm) {
		etu_dttm = etuDttm;
	}

	public java.lang.String getTodayDttm() {
		return today_dttm;
	}

	public void setTodayDttm(java.lang.String todayDttm) {
		today_dttm = todayDttm;
	}

	// START Code added by Tirumal for imlementing VesselActivity CR, dated
	// 04-12-2007

	public String getNoOfGangsSupplied() {
		return noOfGangsSupplied;
	}

	public void setNoOfGangsSupplied(String noOfGangsSupplied) {
		this.noOfGangsSupplied = noOfGangsSupplied;
	}

	public String getNoOfWorkableHatches() {
		return noOfWorkableHatches;
	}

	public void setNoOfWorkableHatches(String noOfWorkableHatches) {
		this.noOfWorkableHatches = noOfWorkableHatches;
	}

	public String getReasonForDelay() {
		return reasonForDelay;
	}

	public void setReasonForDelay(String reasonForDelay) {
		this.reasonForDelay = reasonForDelay;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the tempList
	 */
	public List<String> getTempList() {
		return tempList;
	}

	/**
	 * @param tempList the tempList to set
	 */
	public void setTempList(List<String> tempList) {
		this.tempList = tempList;
	}

	/**
	 * @return the totGenCargoActivity
	 */
	public String getTotGenCargoActivity() {
		return totGenCargoActivity;
	}

	/**
	 * @param totGenCargoActivity the totGenCargoActivity to set
	 */
	public void setTotGenCargoActivity(String totGenCargoActivity) {
		this.totGenCargoActivity = totGenCargoActivity;
	}

	// END Code added by Tirumal for imlementing VesselActivity CR, dated 04-12-2007

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

	public java.lang.String getCntrCodDttm() {
		return cntrCodDttm;
	}

	public void setCntrCodDttm(java.lang.String cntrCodDttm) {
		this.cntrCodDttm = cntrCodDttm;
	}

	public java.lang.String getCntrColDttm() {
		return cntrColDttm;
	}

	public void setCntrColDttm(java.lang.String cntrColDttm) {
		this.cntrColDttm = cntrColDttm;
	}

	public java.lang.String getCombiGcOpsInd() {
		return combiGcOpsInd;
	}

	public void setCombiGcOpsInd(java.lang.String combiGcOpsInd) {
		this.combiGcOpsInd = combiGcOpsInd;
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
