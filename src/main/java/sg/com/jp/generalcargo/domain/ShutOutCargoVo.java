package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShutOutCargoVo implements Serializable {
	public static final long serialVersionUID = 1L;
	private String terminal;
	private String subScheme;
	private String gcOperations;
	private String vesselName;
	private String outVoyNbr;
	private String vesselCallCode;
	private String bkgRefNbr;
	private String esnAsnNbr;
	private String nbrOfPkgs;
	private String actNbrOfPkgsShipped;
	private String shutoutPkgs;
	private String shutoutEsnWeight;
	private String shutoutEsnVolume;
	private String shutoutDeliveryInd;
	private String shutoutDeliveryRemarks;
	private String shutoutUserId;
	private String shutoutUserName;
	private String transferDTTM;
	private String transVesselName;
	private String transOutVoyNbr;
	private String transVesselCallCode;
	private String transBkgRefNbr;
	private String transEsnAsnNbr;
	private String cargoType;
	private String serialNbr;
	private String shutoutDeliveredPkgs;
	private String balancePkgsToShutout;
	private String totalShutoutPkgs;
	private String transferredShutoutPkgs;

	// DPE TUngnm3 Add
	private String truckerName;
	private String cargoStatus;
	private String scheme;
	private String adp;
	private String balancToLoadPkgs;
	private String transferredPkgs;
	private String lyingPkgs;
	private String receivedPkgs;
	private String shutOutEdoPkgs;
	private String declarePkgs;
	private String dwellDays;

	private String shutOutInd;
	private String dnNbrPkgs;
	private boolean updatedByJp;

	private String closeShutOutLoop ="";

	public String getCloseShutOutLoop() {
        return closeShutOutLoop;
    }
    public void setCloseShutOutLoop(String closeShutOutLoop) {
        this.closeShutOutLoop = closeShutOutLoop;
    }

	public boolean getUpdatedByJp() {
        return updatedByJp;
    }
    public void setUpdatedByJp(boolean updatedByJp) {
        this.updatedByJp = updatedByJp;
    }

	public String getTerminal() {
		return terminal;
	}
	public void setTerminal(String terminal) {
		this.terminal = terminal;
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
	
	public String getSerialNbr() {return serialNbr;}
	public void setSerialNbr(String serialNbr) {this.serialNbr = serialNbr;}
	
	public String getCargoType() {return cargoType;}
	public void setCargoType(String cargoType) {this.cargoType = cargoType;}
	
	public String getActNbrOfPkgsShipped() {return actNbrOfPkgsShipped;}
	public void setActNbrOfPkgsShipped(String actNbrOfPkgsShipped) {this.actNbrOfPkgsShipped = actNbrOfPkgsShipped;}

	public String getBkgRefNbr() {return bkgRefNbr;}
	public void setBkgRefNbr(String bkgRefNbr) {this.bkgRefNbr = bkgRefNbr;}
	
	public String getEsnAsnNbr() {return esnAsnNbr;}
	public void setEsnAsnNbr(String esnAsnNbr) {this.esnAsnNbr = esnAsnNbr;}
	
	public String getOutVoyNbr() {return outVoyNbr;}
	public void setOutVoyNbr(String outVoyNbr) {this.outVoyNbr = outVoyNbr;}
	
	public String getShutoutDeliveryInd() {return shutoutDeliveryInd;}
	public void setShutoutDeliveryInd(String shutoutDeliveryInd) {this.shutoutDeliveryInd = shutoutDeliveryInd;}
	
	public String getShutoutDeliveryRemarks() {return shutoutDeliveryRemarks;}
	public void setShutoutDeliveryRemarks(String shutoutDeliveryRemarks) {this.shutoutDeliveryRemarks = shutoutDeliveryRemarks;}
	
	public String getShutoutPkgs() {return shutoutPkgs;}
	public void setShutoutPkgs(String shutoutPkgs) {this.shutoutPkgs = shutoutPkgs;}
	
	public String getTransBkgRefNbr() {return transBkgRefNbr;}
	public void setTransBkgRefNbr(String transBkgRefNbr) {this.transBkgRefNbr = transBkgRefNbr;}
	
	public String getTransEsnAsnNbr() {return transEsnAsnNbr;}
	public void setTransEsnAsnNbr(String transEsnAsnNbr) {this.transEsnAsnNbr = transEsnAsnNbr;}
	
	public String getTransferDTTM() {return transferDTTM;}
	public void setTransferDTTM(String transferDTTM) {this.transferDTTM = transferDTTM;}
	
	public String getTransOutVoyNbr() {return transOutVoyNbr;}
	public void setTransOutVoyNbr(String transOutVoyNbr) {this.transOutVoyNbr = transOutVoyNbr;}
	
	public String getTransVesselCallCode() {return transVesselCallCode;}
	public void setTransVesselCallCode(String transVesselCallCode) {this.transVesselCallCode = transVesselCallCode;}
	
	public String getTransVesselName() {return transVesselName;}
	public void setTransVesselName(String transVesselName) {this.transVesselName = transVesselName;}
	
	public String getNbrOfPkgs() {return nbrOfPkgs;}
	public void setNbrOfPkgs(String nbrOfPkgs) {this.nbrOfPkgs = nbrOfPkgs;}
	
	public String getVesselCallCode() {return vesselCallCode;}
	public void setVesselCallCode(String vesselCallCode) {this.vesselCallCode = vesselCallCode;}
	
	public String getVesselName() {return vesselName;}
	public void setVesselName(String vesselName) {this.vesselName = vesselName;}
	
	public String getShutoutEsnVolume() {return shutoutEsnVolume;}
	public void setShutoutEsnVolume(String shutoutEsnVolume) {this.shutoutEsnVolume = shutoutEsnVolume;}
	
	public String getShutoutEsnWeight() {return shutoutEsnWeight;}
	public void setShutoutEsnWeight(String shutoutEsnWeight) {this.shutoutEsnWeight = shutoutEsnWeight;}
	
	public String getShutoutUserId() {return shutoutUserId;}
	public void setShutoutUserId(String shutoutUserId) {this.shutoutUserId = shutoutUserId;}
	
	public String getShutoutUserName() {return shutoutUserName;}
	public void setShutoutUserName(String shutoutUserName) {this.shutoutUserName = shutoutUserName;}
	
	public String getShutoutDeliveredPkgs() {return shutoutDeliveredPkgs;}
	public void setShutoutDeliveredPkgs(String shutoutDeliveredPkgs) {this.shutoutDeliveredPkgs = shutoutDeliveredPkgs;}
	
	public String getBalancePkgsToShutout() {return balancePkgsToShutout;}
	public void setBalancePkgsToShutout(String balancePkgsToShutout) {this.balancePkgsToShutout = balancePkgsToShutout;}
	
	public static long getSerialVersionUID() {return serialVersionUID;}
	
	public String getTotalShutoutPkgs() {return totalShutoutPkgs;}
	public void setTotalShutoutPkgs(String totalShutoutPkgs) {this.totalShutoutPkgs = totalShutoutPkgs;}
	
	public String getTransferredShutoutPkgs() {return transferredShutoutPkgs;}
	public void setTransferredShutoutPkgs(String transferredShutoutPkgs) {this.transferredShutoutPkgs = transferredShutoutPkgs;}
	public String getTruckerName() {
		return truckerName;
	}
	public void setTruckerName(String truckerName) {
		this.truckerName = truckerName;
	}
	public String getCargoStatus() {
		return cargoStatus;
	}
	public void setCargoStatus(String cargoStatus) {
		this.cargoStatus = cargoStatus;
	}
	public String getScheme() {
		return scheme;
	}
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	public String getAdp() {
		return adp;
	}
	public void setAdp(String adp) {
		this.adp = adp;
	}
	public String getBalancToLoadPkgs() {
		return balancToLoadPkgs;
	}
	public void setBalancToLoadPkgs(String balancToLoadPkgs) {
		this.balancToLoadPkgs = balancToLoadPkgs;
	}
	public String getTransferredPkgs() {
		return transferredPkgs;
	}
	public void setTransferredPkgs(String transferredPkgs) {
		this.transferredPkgs = transferredPkgs;
	}
	public String getLyingPkgs() {
		return lyingPkgs;
	}
	public void setLyingPkgs(String lyingPkgs) {
		this.lyingPkgs = lyingPkgs;
	}
	public String getReceivedPkgs() {
		return receivedPkgs;
	}
	public void setReceivedPkgs(String receivedPkgs) {
		this.receivedPkgs = receivedPkgs;
	}
	public String getShutOutEdoPkgs() {
		return shutOutEdoPkgs;
	}
	public void setShutOutEdoPkgs(String shutOutEdoPkgs) {
		this.shutOutEdoPkgs = shutOutEdoPkgs;
	}
	public String getShutOutInd() {
        return shutOutInd;
    }
    public void setShutOutInd(String shutOutInd) {
        this.shutOutInd = shutOutInd;
    }
    public String getDnNbrPkgs() {
        return dnNbrPkgs;
    }
    public void setDnNbrPkgs(String dnNbrPkgs) {
        this.dnNbrPkgs = dnNbrPkgs;
    }
	public String getDeclarePkgs() {
		return declarePkgs;
	}
	public void setDeclarePkgs(String declarePkgs) {
		this.declarePkgs = declarePkgs;
	}
	public String getDwellDays() {
		return dwellDays;
	}
	public void setDwellDays(String dwellDays) {
		this.dwellDays = dwellDays;
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
