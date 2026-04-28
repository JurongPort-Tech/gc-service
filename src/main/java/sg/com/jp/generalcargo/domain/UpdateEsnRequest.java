package sg.com.jp.generalcargo.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.com.jp.generalcargo.util.CommonUtility;

public class UpdateEsnRequest {
	private String usrid;
	private String companyCode;
	private String bookingRefNo;
	private String hscd;
	private String pkgsType;
	private String mark;
	private String truckerNo;
	private String truckerName;
	private String lopInd;
	private String dgInd;
	private String stgInd;
	private String loadingFrom;
	private String poD;
	private String dutiGI;
	private String truckerCNo;
	private String payMode;
	private String accNo;
	private String esn_asn_nbr;
	private String cargoDes;
	private String stfInd;
	private String strUAFlag;
	private String category;
	private String hsSubCodeFr;
	private String hsSubCodeTo;
	private String asn_nbr;
	private String crgType;
	private String cntr1;
	private String cntr2;
	private String cntr3;
	private String cntr4;
	private String add_l1;
	private String co_nm;
	private String uen_nbr;
	private String tdb_cr_nbr;
	private String otherAcct;
	private String varNbr;
	private String isEditShipper;
	private String shipperAddress;
	private String shipperNm;
	private double weight;
	private double volume;
	private int noOfPkgs;
	
	// FTZ - NS NOV 2024
	private String customHsCode;
	private String conNm;
	private String conAddr;
	private String shipperAddr;
	private String notifyParty;
	private String notifyPartyAddr;
	private String placeofDelivery;
	private String placeofReceipt;
	private String blNbr;
	private int hsCodeSize;
	private List<HsCodeDetails> multiHsCodeList;


	private List<TruckerValueObject> truckerItems;

	public String getUsrid() {
		return usrid;
	}

	public void setUsrid(String usrid) {
		this.usrid = usrid;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getBookingRefNo() {
		return bookingRefNo;
	}

	public void setBookingRefNo(String bookingRefNo) {
		this.bookingRefNo = bookingRefNo;
	}

	public String getHscd() {
		return hscd;
	}

	public void setHscd(String hscd) {
		this.hscd = hscd;
	}

	public String getPkgsType() {
		return pkgsType;
	}

	public void setPkgsType(String pkgsType) {
		this.pkgsType = pkgsType;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getTruckerNo() {
		return truckerNo;
	}

	public void setTruckerNo(String truckerNo) {
		this.truckerNo = truckerNo;
	}

	public String getTruckerName() {
		return truckerName;
	}

	public void setTruckerName(String truckerName) {
		this.truckerName = truckerName;
	}

	public String getLopInd() {
		return lopInd;
	}

	public void setLopInd(String lopInd) {
		this.lopInd = lopInd;
	}

	public String getDgInd() {
		return dgInd;
	}

	public void setDgInd(String dgInd) {
		this.dgInd = dgInd;
	}

	public String getStgInd() {
		return stgInd;
	}

	public void setStgInd(String stgInd) {
		this.stgInd = stgInd;
	}

	public String getLoadingFrom() {
		return loadingFrom;
	}

	public void setLoadingFrom(String loadingFrom) {
		this.loadingFrom = loadingFrom;
	}

	public String getPoD() {
		return poD;
	}

	public void setPoD(String poD) {
		this.poD = poD;
	}

	public String getDutiGI() {
		return dutiGI;
	}

	public void setDutiGI(String dutiGI) {
		this.dutiGI = dutiGI;
	}

	public String getTruckerCNo() {
		return truckerCNo;
	}

	public void setTruckerCNo(String truckerCNo) {
		this.truckerCNo = truckerCNo;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getEsn_asn_nbr() {
		return esn_asn_nbr;
	}

	public void setEsn_asn_nbr(String esn_asn_nbr) {
		this.esn_asn_nbr = esn_asn_nbr;
	}

	public String getCargoDes() {
		return cargoDes;
	}

	public void setCargoDes(String cargoDes) {
		this.cargoDes = cargoDes;
	}

	public String getStfInd() {
		return stfInd;
	}

	public void setStfInd(String stfInd) {
		this.stfInd = stfInd;
	}

	public String getStrUAFlag() {
		return strUAFlag;
	}

	public void setStrUAFlag(String strUAFlag) {
		this.strUAFlag = strUAFlag;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getHsSubCodeFr() {
		return hsSubCodeFr;
	}

	public void setHsSubCodeFr(String hsSubCodeFr) {
		this.hsSubCodeFr = hsSubCodeFr;
	}

	public String getHsSubCodeTo() {
		return hsSubCodeTo;
	}

	public void setHsSubCodeTo(String hsSubCodeTo) {
		this.hsSubCodeTo = hsSubCodeTo;
	}

	public String getAsn_nbr() {
		return asn_nbr;
	}

	public void setAsn_nbr(String asn_nbr) {
		this.asn_nbr = asn_nbr;
	}

	public String getCrgType() {
		return crgType;
	}

	public void setCrgType(String crgType) {
		this.crgType = crgType;
	}

	public String getCntr1() {
		return cntr1;
	}

	public void setCntr1(String cntr1) {
		this.cntr1 = cntr1;
	}

	public String getCntr2() {
		return cntr2;
	}

	public void setCntr2(String cntr2) {
		this.cntr2 = cntr2;
	}

	public String getCntr3() {
		return cntr3;
	}

	public void setCntr3(String cntr3) {
		this.cntr3 = cntr3;
	}

	public String getCntr4() {
		return cntr4;
	}

	public void setCntr4(String cntr4) {
		this.cntr4 = cntr4;
	}

	public String getAdd_l1() {
		return add_l1;
	}

	public void setAdd_l1(String add_l1) {
		this.add_l1 = add_l1;
	}

	public String getCo_nm() {
		return co_nm;
	}

	public void setCo_nm(String co_nm) {
		this.co_nm = co_nm;
	}

	public String getUen_nbr() {
		return uen_nbr;
	}

	public void setUen_nbr(String uen_nbr) {
		this.uen_nbr = uen_nbr;
	}

	public String getTdb_cr_nbr() {
		return tdb_cr_nbr;
	}

	public void setTdb_cr_nbr(String tdb_cr_nbr) {
		this.tdb_cr_nbr = tdb_cr_nbr;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public int getNoOfPkgs() {
		return noOfPkgs;
	}

	public void setNoOfPkgs(int noOfPkgs) {
		this.noOfPkgs = noOfPkgs;
	}

	public List<TruckerValueObject> getTruckerItems() {
		return truckerItems;
	}

	public void setTruckerItems(List<TruckerValueObject> truckerItems) {
		this.truckerItems = truckerItems;
	}

	public String getOtherAcct() {
		return otherAcct;
	}

	public void setOtherAcct(String otherAcct) {
		this.otherAcct = otherAcct;
	}

	public String getVarNbr() {
		return varNbr;
	}

	public void setVarNbr(String varNbr) {
		this.varNbr = varNbr;
	}

	public String getIsEditShipper() {
		return isEditShipper;
	}

	public void setIsEditShipper(String isEditShipper) {
		this.isEditShipper = isEditShipper;
	}

	public String getShipperAddress() {
		return shipperAddress;
	}

	public void setShipperAddress(String shipperAddress) {
		this.shipperAddress = shipperAddress;
	}

	public String getShipperNm() {
		return shipperNm;
	}

	public void setShipperNm(String shipperNm) {
		this.shipperNm = shipperNm;
	}
	
	public String getCustomHsCode() {
		return customHsCode;
	}

	public void setCustomHsCode(String customHsCode) {
		this.customHsCode = customHsCode;
	}

	public String getConNm() {
		return conNm;
	}

	public void setConNm(String conNm) {
		this.conNm = conNm;
	}

	public String getConAddr() {
		return conAddr;
	}

	public void setConAddr(String conAddr) {
		this.conAddr = conAddr;
	}

	public String getShipperAddr() {
		return shipperAddr;
	}

	public void setShipperAddr(String shipperAddr) {
		this.shipperAddr = shipperAddr;
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

	public String getBlNbr() {
		return blNbr;
	}

	public void setBlNbr(String blNbr) {
		this.blNbr = blNbr;
	}

	public int getHsCodeSize() {
		return hsCodeSize;
	}

	public void setHsCodeSize(String hsCodeSize) {
		if(hsCodeSize == null) { hsCodeSize = "0"; }
		this.hsCodeSize = Integer.valueOf(hsCodeSize);
	}

	public List<HsCodeDetails> getMultiHsCodeList() {
		return multiHsCodeList == null ? new ArrayList<HsCodeDetails>()  :multiHsCodeList;
	}

	public void setMultiHsCodeList(List<HashMap<String, String>> hsCodeList) {
		List<HsCodeDetails> multiHsList = new ArrayList<>();
		HsCodeDetails hsCodeDetails = new HsCodeDetails();
		if (hsCodeList.size() > 0) {
			for (int X = 0; X < hsCodeList.size(); X++) {
				hsCodeDetails = new HsCodeDetails();
				hsCodeDetails.setCrgDes(CommonUtility.deNull(hsCodeList.get(X).get("CrgDescArr" + X)));
				hsCodeDetails.setHsCode(CommonUtility.deNull(hsCodeList.get(X).get("HsCodeArr" + X)));
				hsCodeDetails.setNbrPkgs(CommonUtility.deNull(hsCodeList.get(X).get("NoOfPKgsArr" + X)));
				hsCodeDetails.setCustomHsCode(CommonUtility.deNull(hsCodeList.get(X).get("customHsCodeArr" + X)));
				hsCodeDetails.setGrossWt(CommonUtility.deNull(hsCodeList.get(X).get("gwtArr" + X)));
				if((CommonUtility.deNull(hsCodeList.get(X).get("hsSubCodeArr" + X))).indexOf("-") == -1) {
					hsCodeDetails.setHsSubCodeFr((CommonUtility.deNull(hsCodeList.get(X).get("hsSubCodeArr" + X))));	
					hsCodeDetails.setHsSubCodeTo((CommonUtility.deNull(hsCodeList.get(X).get("hsSubCodeArr" + X))));	
				} else {
					hsCodeDetails.setHsSubCodeFr((CommonUtility.deNull(hsCodeList.get(X).get("hsSubCodeArr" + X))).split("-")[0]);
					hsCodeDetails.setHsSubCodeTo((CommonUtility.deNull(hsCodeList.get(X).get("hsSubCodeArr" + X))).split("-")[1]);
				}
				hsCodeDetails.setGrossVol(CommonUtility.deNull(hsCodeList.get(X).get("mSmtArr" + X)));
				hsCodeDetails.setHsSubCodeDesc( CommonUtility.deNull(hsCodeList.get(X).get("hsSubCodeDescArr" + X)));
				hsCodeDetails.setIsHsCodeChange(CommonUtility.deNull(hsCodeList.get(X).get("isHsCodeChange" + X)));
				hsCodeDetails.setHscodeSeqNbr(CommonUtility.deNull(hsCodeList.get(X).get("hscodeSeqNbr" + X)));
				multiHsList.add(hsCodeDetails);
			}
			this.multiHsCodeList = multiHsList;
			
		}
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
