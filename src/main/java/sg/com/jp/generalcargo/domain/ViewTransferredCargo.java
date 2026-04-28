package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class ViewTransferredCargo extends BaseModel{

	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private String vslName;
	    private String fromASN ;
	    private String toASN ;
	    private String cargoStatus;
	    private String noOfPkgsTerminated; 
	    private String transferredPkgs;
	    private String transferType;
	    private String firstTxnDttm;
	    private String noOfPkgs;
	    private String fromVoyageOut;
	    private String toVoyageOut;
	    private String modifyBy;
	    private String fromVslNum;
	    private String fromVslNumPkgs;
	    private String toVslNm;
	    private String modifyDttm;
	    private String noOfShutOutPkgs;
	    private String fromTerminal;
	    private String toTerminal;
	    private String custCode;
	    private String vslIndicator;
	    private String vvCd;
	    private String outVoyNo;
	    private String refNum;
	    private String esnFirstTrans;
	    private String tesnjpjpFirstTrans;
	    private String tesnpsajpFirstTrans;
	    
		public String getFromVslNumPkgs() {
			return fromVslNumPkgs;
		}
		public void setFromVslNumPkgs(String fromVslNumPkgs) {
			this.fromVslNumPkgs = fromVslNumPkgs;
		}
		public String getEsnFirstTrans() {
			return esnFirstTrans;
		}
		public void setEsnFirstTrans(String esnFirstTrans) {
			this.esnFirstTrans = esnFirstTrans;
		}
		public String getTesnjpjpFirstTrans() {
			return tesnjpjpFirstTrans;
		}
		public void setTesnjpjpFirstTrans(String tesnjpjpFirstTrans) {
			this.tesnjpjpFirstTrans = tesnjpjpFirstTrans;
		}
		public String getTesnpsajpFirstTrans() {
			return tesnpsajpFirstTrans;
		}
		public void setTesnpsajpFirstTrans(String tesnpsajpFirstTrans) {
			this.tesnpsajpFirstTrans = tesnpsajpFirstTrans;
		}
		public String getTransferredPkgs() {
			return transferredPkgs;
		}
		public void setTransferredPkgs(String transferredPkgs) {
			this.transferredPkgs = transferredPkgs;
		}
		public String getRefNum() {
			return refNum;
		}
		public void setRefNum(String refNum) {
			this.refNum = refNum;
		}
		public String getOutVoyNo() {
			return outVoyNo;
		}
		public void setOutVoyNo(String outVoyNo) {
			this.outVoyNo = outVoyNo;
		}
		public String getVvCd() {
			return vvCd;
		}
		public void setVvCd(String vvCd) {
			this.vvCd = vvCd;
		}
		
		public String getVslName() {
			return vslName;
		}
		public void setVslName(String vslName) {
			this.vslName = vslName;
		}
		public String getFromASN() {
			return fromASN;
		}
		public void setFromASN(String fromASN) {
			this.fromASN = fromASN;
		}
		public String getToASN() {
			return toASN;
		}
		public void setToASN(String toASN) {
			this.toASN = toASN;
		}
		public String getCargoStatus() {
			return cargoStatus;
		}
		public void setCargoStatus(String cargoStatus) {
			this.cargoStatus = cargoStatus;
		}
		public String getNoOfPkgsTerminated() {
			return noOfPkgsTerminated;
		}
		public void setNoOfPkgsTerminated(String noOfPkgsTerminated) {
			this.noOfPkgsTerminated = noOfPkgsTerminated;
		}
		public String getTransferType() {
			return transferType;
		}
		public void setTransferType(String transferType) {
			this.transferType = transferType;
		}
		public String getFirstTxnDttm() {
			return firstTxnDttm;
		}
		public void setFirstTxnDttm(String firstTxnDttm) {
			this.firstTxnDttm = firstTxnDttm;
		}
		public String getNoOfPkgs() {
			return noOfPkgs;
		}
		public void setNoOfPkgs(String noOfPkgs) {
			this.noOfPkgs = noOfPkgs;
		}
		public String getFromVoyageOut() {
			return fromVoyageOut;
		}
		public void setFromVoyageOut(String fromVoyageOut) {
			this.fromVoyageOut = fromVoyageOut;
		}
		public String getToVoyageOut() {
			return toVoyageOut;
		}
		public void setToVoyageOut(String toVoyageOut) {
			this.toVoyageOut = toVoyageOut;
		}
		public String getModifyBy() {
			return modifyBy;
		}
		public void setModifyBy(String modifyBy) {
			this.modifyBy = modifyBy;
		}
		
		public String getFromVslNum() {
			return fromVslNum;
		}
		public void setFromVslNum(String fromVslNum) {
			this.fromVslNum = fromVslNum;
		}
		public String getToVslNm() {
			return toVslNm;
		}
		public void setToVslNm(String toVslNm) {
			this.toVslNm = toVslNm;
		}
		public String getModifyDttm() {
			return modifyDttm;
		}
		public void setModifyDttm(String modifyDttm) {
			this.modifyDttm = modifyDttm;
		}
		public String getNoOfShutOutPkgs() {
			return noOfShutOutPkgs;
		}
		public void setNoOfShutOutPkgs(String noOfShutOutPkgs) {
			this.noOfShutOutPkgs = noOfShutOutPkgs;
		}
		public String getFromTerminal() {
			return fromTerminal;
		}
		public void setFromTerminal(String fromTerminal) {
			this.fromTerminal = fromTerminal;
		}
		public String getToTerminal() {
			return toTerminal;
		}
		public void setToTerminal(String toTerminal) {
			this.toTerminal = toTerminal;
		}
		public String getCustCode() {
			return custCode;
		}
		public void setCustCode(String custCode) {
			this.custCode = custCode;
		}
		public String getVslIndicator() {
			return vslIndicator;
		}
		public void setVslIndicator(String vslIndicator) {
			this.vslIndicator = vslIndicator;
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
