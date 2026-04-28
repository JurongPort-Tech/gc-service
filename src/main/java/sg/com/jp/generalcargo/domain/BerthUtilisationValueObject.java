package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BerthUtilisationValueObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private String berthNumber;
	private String berthLength;
	private String VVcode;
	private double tonnageGC;
	private double tonnageBC;
	private double tonnageCNTR;
	private double totalTonnage;
	private int vesselCount;
	private double totalDurationOfVesselStay;
	private double averageTonnageHandledPerHour;
	private double averageTonnageHandledPerDay;
	private double averageTonnageHandledPerVessel;

	public double getAverageTonnageHandledPerDay() {
		return averageTonnageHandledPerDay;
	}

	public void setAverageTonnageHandledPerDay(double averageTonnageHandledPerDay) {
		this.averageTonnageHandledPerDay = averageTonnageHandledPerDay;
	}

	public double getAverageTonnageHandledPerHour() {
		return averageTonnageHandledPerHour;
	}

	public void setAverageTonnageHandledPerHour(double averageTonnageHandledPerHour) {
		this.averageTonnageHandledPerHour = averageTonnageHandledPerHour;
	}

	public double getAverageTonnageHandledPerVessel() {
		return averageTonnageHandledPerVessel;
	}

	public void setAverageTonnageHandledPerVessel(double averageTonnageHandledPerVessel) {
		this.averageTonnageHandledPerVessel = averageTonnageHandledPerVessel;
	}

	public String getBerthLength() {
		return berthLength;
	}

	public void setBerthLength(String berthLength) {
		this.berthLength = berthLength;
	}

	public String getBerthNumber() {
		return berthNumber;
	}

	public void setBerthNumber(String berthNumber) {
		this.berthNumber = berthNumber;
	}

	public double getTonnageBC() {
		return tonnageBC;
	}

	public void setTonnageBC(double tonnageBC) {
		this.tonnageBC = tonnageBC;
	}

	public double getTonnageCNTR() {
		return tonnageCNTR;
	}

	public void setTonnageCNTR(double tonnageCNTR) {
		this.tonnageCNTR = tonnageCNTR;
	}

	public double getTonnageGC() {
		return tonnageGC;
	}

	public void setTonnageGC(double tonnageGC) {
		this.tonnageGC = tonnageGC;
	}

	public double getTotalDurationOfVesselStay() {
		return totalDurationOfVesselStay;
	}

	public void setTotalDurationOfVesselStay(double totalDurationOfVesselStay) {
		this.totalDurationOfVesselStay = totalDurationOfVesselStay;
	}

	public double getTotalTonnage() {
		return totalTonnage;
	}

	public void setTotalTonnage(double totalTonnage) {
		this.totalTonnage = totalTonnage;
	}

	public int getVesselCount() {
		return vesselCount;
	}

	public void setVesselCount(int vesselCount) {
		this.vesselCount = vesselCount;
	}

	public String getVVcode() {
		return VVcode;
	}

	public void setVVcode(String vcode) {
		VVcode = vcode;
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
