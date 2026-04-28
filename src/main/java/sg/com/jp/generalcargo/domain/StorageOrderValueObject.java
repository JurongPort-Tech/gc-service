package sg.com.jp.generalcargo.domain;

/**
 *	System Name: TOPs (Terminal Operation and Planning System)
 *	Component ID: StorageOrderValueObject.java (VCD - Storage Order)
 *	Component Description: This is the Container Storage Order value object class
 *
 *	@author      Nguyen Manh Hien
 *	@version     22 September 2009
 *
 *	Revision History
 *	================
 * 	Author   Request Number  Description of Change   Version     Date Released
 * 	HienNM2                	 Creation                1.0       22 September 2009
 * Cally				CR-OPS-20100923-009 Use of Space & Storing Order  					23 Sep 10
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author HienNM2
 *
 */
public class StorageOrderValueObject implements TopsIObject {

	// private CntrEnqJdbcRepository cntrEnqJdbcRepository;

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor
	 */
	public StorageOrderValueObject() {
	}

	private String seqNo;

	private String cntrSeqNo;

	private String cntrNo;

	private String dateCreate;

	private String dateModify;

	private String status;

	private String iso;

	private String length;

	private String height;

	private String weight;

	private String pDisc1;

	private String pDisc2;

	private String pDisc3;

	private String pDest;

	private String impHaulier;

	private String expHaulier;

	// Cally CR-OPS-20100923-009 Use of Space & Storing Order 23 Sep 10
	private String impTruckNbr;

	private String expTruckNbr;

	private String impGateOut;

	private String expGateIn;

	private String total;

	private String remarks;

	private String dgInd;

	private String strImdgClCd;

	private String arrvStat;

	private String miscAppNo;

	private String tenant;

	private String type;

	private String esnEdoAsn;

	private String vvCode;

	private String terminal;

	private String scheme;

	private String voyIn;

	private String voyOut;

	private String tesnIndicator;

	public String getTesnIndicator() {
		return tesnIndicator;
	}

	public void setTesnIndicator(String tesnIndicator) {
		this.tesnIndicator = tesnIndicator;
	}

	public String getEsnEdoAsn() {
		return esnEdoAsn;
	}

	public void setEsnEdoAsn(String esnEdoAsn) {
		this.esnEdoAsn = esnEdoAsn;
	}

	public String getVvCode() {
		return vvCode;
	}

	public void setVvCode(String vvCode) {
		this.vvCode = vvCode;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getVoyIn() {
		return voyIn;
	}

	public void setVoyIn(String voyIn) {
		this.voyIn = voyIn;
	}

	public String getVoyOut() {
		return voyOut;
	}

	public void setVoyOut(String voyOut) {
		this.voyOut = voyOut;
	}

	/**
	 * @return the seqNo
	 */
	public String getSeqNo() {
		return seqNo;
	}

	/**
	 * @param seqNo the seqNo to set
	 */
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	/**
	 * @return the cntrSeqNo
	 */
	public String getCntrSeqNo() {
		return cntrSeqNo;
	}

	/**
	 * @param cntrSeqNo the cntrSeqNo to set
	 */
	public void setCntrSeqNo(String cntrSeqNo) {
		this.cntrSeqNo = cntrSeqNo;
	}

	/**
	 * @return the cntrNo
	 */
	public String getCntrNo() {
		return cntrNo;
	}

	/**
	 * @param cntrNo the cntrNo to set
	 */
	public void setCntrNo(String cntrNo) {
		this.cntrNo = cntrNo;
	}

	/**
	 * @return the dateCreate
	 */
	public String getDateCreate() {
		return dateCreate;
	}

	/**
	 * @param dateCreate the dateCreate to set
	 */
	public void setDateCreate(String dateCreate) {
		this.dateCreate = dateCreate;
	}

	/**
	 * @return the dateModify
	 */
	public String getDateModify() {
		return dateModify;
	}

	/**
	 * @param dateModify the dateModify to set
	 */
	public void setDateModify(String dateModify) {
		this.dateModify = dateModify;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the iso
	 */
	public String getIso() {
		return iso;
	}

	/**
	 * @param iso the iso to set
	 */
	public void setIso(String iso) {
		this.iso = iso;
	}

	/**
	 * @return the length
	 */
	public String getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(String length) {
		this.length = length;
	}

	/**
	 * @return the height
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * @return the weight
	 */
	public String getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(String weight) {
		this.weight = weight;
	}

	/**
	 * @return the pDisc1
	 */
	public String getPDisc1() {
		return pDisc1;
	}

	/**
	 * @param disc1 the pDisc1 to set
	 */
	public void setPDisc1(String disc1) {
		pDisc1 = disc1;
	}

	/**
	 * @return the pDisc2
	 */
	public String getPDisc2() {
		return pDisc2;
	}

	/**
	 * @param disc2 the pDisc2 to set
	 */
	public void setPDisc2(String disc2) {
		pDisc2 = disc2;
	}

	/**
	 * @return the pDisc3
	 */
	public String getPDisc3() {
		return pDisc3;
	}

	/**
	 * @param disc3 the pDisc3 to set
	 */
	public void setPDisc3(String disc3) {
		pDisc3 = disc3;
	}

	/**
	 * @return the pDest
	 */
	public String getPDest() {
		return pDest;
	}

	/**
	 * @param dest the pDest to set
	 */
	public void setPDest(String dest) {
		pDest = dest;
	}

	/**
	 * @return the impHaulier
	 */
	public String getImpHaulier() {
		return impHaulier;
	}

	/**
	 * @param impHaulier the impHaulier to set
	 */
	public void setImpHaulier(String impHaulier) {
		this.impHaulier = impHaulier;
	}

	/**
	 * @return the expHaulier
	 */
	public String getExpHaulier() {
		return expHaulier;
	}

	/**
	 * @param expHaulier the expHaulier to set
	 */
	public void setExpHaulier(String expHaulier) {
		this.expHaulier = expHaulier;
	}

	// Cally CR-OPS-20100923-009 Use of Space & Storing Order 23 Sep 10
	public String getImpTruckNbr() {
		return impTruckNbr;
	}

	public void setImpTruckNbr(String impTruckNbr) {
		this.impTruckNbr = impTruckNbr;
	}

	public String getExpTruckNbr() {
		return expTruckNbr;
	}

	public void setExpTruckNbr(String expTruckNbr) {
		this.expTruckNbr = expTruckNbr;
	}

	public String getImpGateOut() {
		return impGateOut;
	}

	public void setImpGateOut(String impGateOut) {
		this.impGateOut = impGateOut;
	}

	public String getExpGateIn() {
		return expGateIn;
	}

	public void setExpGateIn(String expGateIn) {
		this.expGateIn = expGateIn;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the dgInd
	 */
	public String getDgInd() {
		return dgInd;
	}

	/**
	 * @param dgInd the dgInd to set
	 */
	public void setDgInd(String dgInd) {
		this.dgInd = dgInd;
	}

	/**
	 * @return the strImdgClCd
	 */
	public String getStrImdgClCd() {
		return strImdgClCd;
	}

	/**
	 * @param strImdgClCd the strImdgClCd to set
	 */
	public void setStrImdgClCd(String strImdgClCd) {
		this.strImdgClCd = strImdgClCd;
	}

	/**
	 * @return the serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public void setArrvStat(String arrvStat) {
		this.arrvStat = arrvStat;
	}

	public String getArrvStat() {
		return arrvStat;
	}

	/**
	 * Set container remarks
	 *
	 * @param reeferTemp
	 * @param imoClass
	 * @param oogUnit
	 * @param oogHeight
	 * @param oogFront
	 * @param oogBack
	 * @param oogRight
	 * @param oogLeft
	 * @param height
	 * @param catCd
	 */
	public void setRemarks(String reeferTemp, String imoClass, String oogUnit, int oogHeight, int oogFront, int oogBack,
			int oogRight, int oogLeft, String height, String catCd) {
		//remarks = cntrEnqJdbcRepository.getCntrRemrks(reeferTemp, imoClass, oogUnit, oogHeight, oogFront, oogBack,
		//		oogRight, oogLeft, height, catCd);
		return;
	}

	/**
	 * @return the miscAppNo
	 */
	public String getMiscAppNo() {
		return miscAppNo;
	}

	/**
	 * @param miscAppNo the miscAppNo to set
	 */
	public void setMiscAppNo(String miscAppNo) {
		this.miscAppNo = miscAppNo;
	}

	/**
	 * @param tenant
	 */
	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	/**
	 * @return tenant
	 */
	public String getTenant() {
		return tenant;
	}

	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return type
	 */
	public String getType() {
		return type;
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
