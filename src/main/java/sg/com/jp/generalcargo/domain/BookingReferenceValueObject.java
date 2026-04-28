package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BookingReferenceValueObject implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * System Name : TOPs (Terminal Operation and Planning System) Component ID :
	 * CntrEnqCntrNoListValueObject.java (CIM - Container Enquiry) Component
	 * Description: This is the valueObject used for displaying the container list.
	 *
	 * @author Irene Tan
	 * @version 12 November 2001
	 *
	 *          Change Revision --------------- Author Request Number Description of
	 *          Change Version Date Released TVS - Creation 1.1 12 Jan 2002
	 *
	 *          Irene Tan GBMS Phase 1 - To include abbreviated 1.2 07 Mar 2005
	 *          Improvement vessel name
	 */

	private String br_No;
	private String vessel_Name;
	private String voyage_No;
	private String shipper_Name;
	private String shipper_Cr_No;
	private String shipperCoyCode;
	private String cargo_Type;
	private String cargo_Category; // added by Maksym
	private String package_Nos;
	private String weight;
	private String volume;
	private String variance;
	private String port_Of_Discharge;
	private String esn_Declarant_No;
	private String esn_Declarant_Name;
	private String date_Of_Creation;
	private String volume_Variance;
	private String weight_Variance;
	private String package_Variance;
	private String shipper_Add;
	private String shipper_Contact;
	private String container_Size;
	private String container_Type;
	private String no_Of_Containers;
	private String port_Name;
	private String bkCreateCd; // added by thanhnv2
	private String bkShipperCd;
	private String varNbr;
	private String vvIdnStatus;
	private String vslCloseShipInd;

	private String terminal;
	private String scheme;
	private String subScheme;
	private String gcOperations;
	private String bkStatus;
	
	// START - CR FTZ - NS JUNE 2024
	private String blNbr;
	private String notifyParty;
	private String notifyPartyAddr;
	private String placeofDelivery;
	private String placeofReceipt;
	private String conName;
	private String consigneeAddr;

	public String getBlNbr() {
		return blNbr;
	}

	public void setBlNbr(String blNbr) {
		this.blNbr = blNbr;
	}

	public String getNotifyParty() {
		return notifyParty;
	}

	public void setNotifyParty(String notifyParty) {
		this.notifyParty = notifyParty;
	}

	public String getNotifyPartyAddr() {
		return notifyPartyAddr;
	}

	public void setNotifyPartyAddr(String notifyPartyAddr) {
		this.notifyPartyAddr = notifyPartyAddr;
	}

	public String getPlaceofDelivery() {
		return placeofDelivery;
	}

	public void setPlaceofDelivery(String placeofDelivery) {
		this.placeofDelivery = placeofDelivery;
	}

	public String getPlaceofReceipt() {
		return placeofReceipt;
	}

	public void setPlaceofReceipt(String placeofReceipt) {
		this.placeofReceipt = placeofReceipt;
	}

	public String getConName() {
		return conName;
	}

	public void setConName(String conName) {
		this.conName = conName;
	}

	public String getConsigneeAddr() {
		return consigneeAddr;
	}

	public void setConsigneeAddr(String consigneeAddr) {
		this.consigneeAddr = consigneeAddr;
	}
	// END - CR FTZ - NS JUNE 2024
		
	public String getBkStatus() {
		return bkStatus;
	}

	public void setBkStatus(String bkStatus) {
		this.bkStatus = bkStatus;
	}

	public String getBkShipperCd() {
		return bkShipperCd;
	}

	public void setBkShipperCd(String bkShipperCd) {
		this.bkShipperCd = bkShipperCd;
	}

	public String getBkCreateCd() {
		return bkCreateCd;
	}

	public void setBkCreateCd(String bkCreateCd) {
		this.bkCreateCd = bkCreateCd;
	}

	private List<BookRefvoyageOutwardValueObject> voyage_outward;
	// Added by Irene Tan on 07 Mar 2005 : GBMS Phase 1 Improvement
	private String abbrVslName;
	// End Added by Irene Tan on 07 Mar 2005 : GBMS Phase 1 Improvement

	public BookingReferenceValueObject() {
		voyage_outward = new Vector<>();
	}

	public String getBrNo() {
		return br_No;
	}

	public String getVesselName() {
		return vessel_Name;
	}

	public String getVoyageNo() {
		return voyage_No;
	}

	public String getShipperName() {
		return shipper_Name;
	}

	public String getShipperCrNo() {
		return shipper_Cr_No;
	}

	public String getShipperAddress() {
		return shipper_Add;
	}

	public String getShipperContactNo() {
		return shipper_Contact;
	}

	public String getCargoType() {
		return cargo_Type;
	}

	public String getPackageNos() {
		return package_Nos;
	}

	public String getWeight() {
		return weight;
	}

	public String getVolume() {
		return volume;
	}

	public String getVariance() {
		return variance;
	}

	public String getPortOfDischarge() {
		return port_Of_Discharge;
	}

	public String getEsnDeclarantNo() {
		return esn_Declarant_No;
	}

	public String getEsnDeclarantName() {
		return esn_Declarant_Name;
	}

	public String getDateOfCreation() {
		return date_Of_Creation;
	}

	public List<BookRefvoyageOutwardValueObject> getVoyageoutward() {
		return voyage_outward;
	}

	public String getPackageVariance() {
		return package_Variance;
	}

	public String getWeightVariance() {
		return weight_Variance;
	}

	public String getVolumeVariance() {
		return volume_Variance;
	}

	public String getContainerSize() {
		return container_Size;
	}

	public String getContainerType() {
		return container_Type;
	}

	public String getNoContainer() {
		return no_Of_Containers;
	}

	public String getPortName() {
		return port_Name;
	}

	// Added by Irene Tan on 07 Mar 2005 : GBMS Phase 1 Improvement
	public String getAbbrVslName() {
		return abbrVslName;
	}
	// End Added by Irene Tan on 07 Mar 2005 : GBMS Phase 1 Improvement

	public void setVoyageoutward(List<BookRefvoyageOutwardValueObject> vector) {
		voyage_outward = vector;
	}

	public void setBrNo(String s) {
		br_No = s;
	}

	public void setVesselName(String s) {
		vessel_Name = s;
	}

	public void setVoyageNo(String s) {
		voyage_No = s;
	}

	public void setShipperName(String s) {
		shipper_Name = s;
	}

	public void setShipperCrNo(String s) {
		shipper_Cr_No = s;
	}

	public void setShipperAddress(String s) {
		shipper_Add = s;
	}

	public void setShipperContact(String s) {
		shipper_Contact = s;
	}

	public void setCargoType(String s) {
		cargo_Type = s;
	}

	public void setPackageNos(String s) {
		package_Nos = s;
	}

	public void setWeight(String s) {
		weight = s;
	}

	public void setVolume(String s) {
		volume = s;
	}

	public void setPackageVariance(String s) {
		package_Variance = s;
	}

	public void setWeightVariance(String s) {
		weight_Variance = s;
	}

	public void setVolumeVariance(String s) {
		volume_Variance = s;
	}

	public void setPortOfDischarge(String s) {
		port_Of_Discharge = s;
	}

	public void setPortName(String s) {
		port_Name = s;
	}

	public void setEsnDeclarantNo(String s) {
		esn_Declarant_No = s;
	}

	public void setEsnDeclarantName(String s) {
		esn_Declarant_Name = s;
	}

	public void setDateOfCreation(String s) {
		date_Of_Creation = s;
	}

	public void setContainerSize(String s) {
		container_Size = s;
	}

	public void setContainerType(String s) {
		container_Type = s;
	}

	public void setNoContainer(String s) {
		no_Of_Containers = s;
	}

	// Added by Irene Tan on 07 Mar 2005 : GBMS Phase 1 Improvement
	public void setAbbrVslName(String abbrVslName) {
		this.abbrVslName = abbrVslName;
	}

	// End Added by Irene Tan on 07 Mar 2005 : GBMS Phase 1 Improvement
	public String getShipperCoyCode() {
		return shipperCoyCode;
	}

	public void setShipperCoyCode(String shipperCoyCode) {
		this.shipperCoyCode = shipperCoyCode;
	}

	public String getVarNbr() {
		return varNbr;
	}

	public void setVarNbr(String varNbr) {
		this.varNbr = varNbr;
	}

	public String getVvIdnStatus() {
		return vvIdnStatus;
	}

	public void setVvIdnStatus(String vvIdnStatus) {
		this.vvIdnStatus = vvIdnStatus;
	}

	public String getVslCloseShipInd() {
		return vslCloseShipInd;
	}

	public void setVslCloseShipInd(String vslCloseShipInd) {
		this.vslCloseShipInd = vslCloseShipInd;
	}

	// BEGIN added by Maksym JCMS Smart CR 6.10
	public String getCargoCategory() {
		return cargo_Category;
	}

	public void setCargoCategory(String cargo_Category) {
		this.cargo_Category = cargo_Category;
	}
	// END added by Maksym JCMS Smart CR 6.10

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

	public String getSubScheme() {
		return subScheme;
	}

	public void setSubScheme(String subScheme) {
		this.subScheme = subScheme;
	}

	public String getGcOperations() {
		return gcOperations;
	}

	public void setGcOperations(String gcOperations) {
		this.gcOperations = gcOperations;
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
