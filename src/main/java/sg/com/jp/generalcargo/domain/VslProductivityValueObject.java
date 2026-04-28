package sg.com.jp.generalcargo.domain;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VslProductivityValueObject implements TopsIObject {

	private static final long serialVersionUID = 1L;

	private String vvCode;

	private String vesselName;

	private String agent;

	private String scheme;

	private String outVoyageNumber;

	private double cargoDischargeTonnage;

	private double cargoLoadTonnage;

	private double totalTonnage;

	private Date ATB;

	private Date ATU;

	private String timeatBerth;

	private int gangsSupplied;

	private int noofHatchesWorked;

	private double dailyRateGeneralCargo;

	private Date workCommence;

	private Date workCompleted;

	private double workHours;

	private String workHoursDHHMI;

	private double tonsPerHour;

	private double tonsPerGangHour;

	private Integer LOA;

	private double dailyRateMtrLOA;

	private String berth;

	private String stevedore;

	private String cargoCategory;

	private int benchmark;

	private double uniSteelTonsPerHour;

	private double uniSteelTonsPerHourPerVsl;

	private double mixedSteelTonsPerHour;

	private double mixSteelTonsPerHourPerVsl;

	private double totalTonsPerHour;
	// Added by Punitha on 04/09/2008.
	private String liner;

	private double cntrTonnage;

	private double cargoTonnage;

	private double bulkTonnage;

	private double bulkDischargeTonnage;

	private double bulkLoadTonnage;

	private int hatchNbr;

	private double berthHr;

	private double actualWorkHr;

	private Date lastCargoDttm;

	private Date lastCargoGraceDttm;

	private String prodSurcharge;

	private String cargoType;

	private Date actualCargoDttm;

	// Added by Punitha on 21/04/2009
	private double grossTonsPerHour;

	private Date gbFirstActDttm;

	private Date gbLastActDttm;

	private double timeExceeded;

	private double genCargoLoadTonnage;

	// Added by Punitha on 11/01/2010

	private String floatCraneInd;

	private String heavyLiftOverside;

	private String vesselType;

	private double timeAtWork;

	private double discCntrTonnage;

	private double loadCntrTonnage;

	// End

	// Jacky SL-OPS-20100713-01 Rain hours be included 13/07/2010
	private double rainHr;
	// End

	// Jacky SL-OPS-20100713-01 Rain hours be included 13/07/2010
	public void setRainHours(double rainHr) {
		this.rainHr = rainHr;
	}

	public double getRainHours() {
		return rainHr;
	}
	// End

	public double getGenCargoLoadTonnage() {
		return genCargoLoadTonnage;
	}

	public void setGenCargoLoadTonnage(double genCargoLoadTonnage) {
		this.genCargoLoadTonnage = genCargoLoadTonnage;
	}

	public double getTimeExceeded() {
		return timeExceeded;
	}

	public void setTimeExceeded(double timeExceeded) {
		this.timeExceeded = timeExceeded;
	}

	public Date getGbFirstActDttm() {
		return gbFirstActDttm;
	}

	public void setGbFirstActDttm(Date gbFirstActDttm) {
		this.gbFirstActDttm = gbFirstActDttm;
	}

	public Date getGbLastActDttm() {
		return gbLastActDttm;
	}

	public void setGbLastActDttm(Date gbLastActDttm) {
		this.gbLastActDttm = gbLastActDttm;
	}

	/**
	 * @return the Actual Time of Berth
	 */
	public Date getATB() {
		return ATB;
	}

	/**
	 * @param ATB to be set
	 */

	public void setATB(Date ATB) {
		this.ATB = ATB;
	}

	/**
	 * @return the ATU
	 */
	public Date getATU() {
		return ATU;
	}

	/**
	 * @param ATU to be set
	 */
	public void setATU(Date ATU) {
		this.ATU = ATU;
	}

	/**
	 * @return the Benchmark
	 */
	public int getBenchmark() {
		return benchmark;
	}

	/**
	 * @param set the Benchmark
	 */
	public void setBenchmark(int benchmark) {
		this.benchmark = benchmark;
	}

	/**
	 * @return the Berth
	 */
	public String getBerth() {
		return berth;
	}

	/**
	 * @param set the berth
	 */
	public void setBerth(String berth) {
		this.berth = berth;
	}

	/**
	 * @return the CargoCategory
	 */

	public String getCargoCategory() {
		return cargoCategory;
	}

	/**
	 * @param set the cargoCategory
	 */
	public void setCargoCategory(String cargoCategory) {
		this.cargoCategory = cargoCategory;
	}

	/**
	 * @return the CargoDischargeTonnage
	 */
	public double getCargoDischargeTonnage() {
		return cargoDischargeTonnage;
	}

	public void setCargoDischargeTonnage(double cargoDischargeTonnage) {
		this.cargoDischargeTonnage = cargoDischargeTonnage;
	}

	/**
	 * @return the CargoLoad Tonnage
	 */
	public double getCargoLoadTonnage() {
		return cargoLoadTonnage;
	}

	public void setCargoLoadTonnage(double cargoLoadTonnage) {
		this.cargoLoadTonnage = cargoLoadTonnage;
	}

	/**
	 * @return the DailyRateGeneralCargo
	 */
	public double getDailyRateGeneralCargo() {
		return dailyRateGeneralCargo;
	}

	/**
	 * @param set the Daily Rate General Cargo
	 */
	public void setDailyRateGeneralCargo(double dailyRateGeneralCargo) {
		this.dailyRateGeneralCargo = dailyRateGeneralCargo;
	}

	/**
	 * @return the DailyRateMtrLoa
	 */
	public double getDailyRateMtrLOA() {
		return dailyRateMtrLOA;
	}

	/**
	 * @param set the Daily Rate Mtr LOA
	 */
	public void setDailyRateMtrLOA(double dailyRateMtrLOA) {
		this.dailyRateMtrLOA = dailyRateMtrLOA;
	}

	/**
	 * @return the Gangs Supplied
	 */
	public int getGangsSupplied() {
		return gangsSupplied;
	}

	public void setGangsSupplied(int gangsSupplied) {
		this.gangsSupplied = gangsSupplied;
	}

	/**
	 * @return the LOA
	 */
	public Integer getLOA() {
		return LOA;
	}

	/**
	 * @param set the LOA
	 */
	public void setLOA(Integer LOA) {
		this.LOA = LOA;
	}

	/**
	 * @return the Mixed Steel Tons per hour
	 */
	public double getMixedSteelTonsPerHour() {
		return mixedSteelTonsPerHour;
	}

	public void setMixedSteelTonsPerHour(double mixedSteelTonsPerHour) {
		this.mixedSteelTonsPerHour = mixedSteelTonsPerHour;
	}

	/**
	 * @return the Mixed Steel Tons per hour
	 */
	public double getMixSteelTonsPerHourPerVsl() {
		return mixSteelTonsPerHourPerVsl;
	}

	/**
	 * @param set the MixSteel Tons PerHour Vessel
	 */
	public void setMixSteelTonsPerHourPerVsl(double mixSteelTonsPerHourPerVsl) {
		this.mixSteelTonsPerHourPerVsl = mixSteelTonsPerHourPerVsl;
	}

	/**
	 * @return the NO. of hatches worked
	 */
	public int getNoofHatchesWorked() {
		return noofHatchesWorked;
	}

	/**
	 * @param set the No of hatches worked
	 */
	public void setNoofHatchesWorked(int noofHatchesWorked) {
		this.noofHatchesWorked = noofHatchesWorked;
	}

	/**
	 * @return the Benchmark
	 */
	public String getOutVoyageNumber() {
		return outVoyageNumber;
	}

	public void setOutVoyageNumber(String outVoyageNumber) {
		this.outVoyageNumber = outVoyageNumber;
	}

	/**
	 * @return the Stevedore
	 */
	public String getStevedore() {
		return stevedore;
	}

	/**
	 * @param set the stevedore
	 */

	public void setStevedore(String stevedore) {
		this.stevedore = stevedore;
	}

	/**
	 * @return the Time at berth
	 */
	public String getTimeatBerth() {
		return timeatBerth;
	}

	/**
	 * @param set the time at berth
	 */
	public void setTimeatBerth(String timeatBerth) {
		this.timeatBerth = timeatBerth;
	}

	/**
	 * @return tons per GangHour
	 */
	public double getTonsPerGangHour() {
		return tonsPerGangHour;
	}

	/**
	 * @param set the tons per gang hour
	 */
	public void setTonsPerGangHour(double tonsPerGangHour) {
		this.tonsPerGangHour = tonsPerGangHour;
	}

	/**
	 * @return tons per GangHour
	 */
	public double getTonsPerHour() {
		return tonsPerHour;
	}

	/**
	 * @param set the tons per hour
	 */
	public void setTonsPerHour(double tonsPerHour) {
		this.tonsPerHour = tonsPerHour;
	}

	/**
	 * @return the Total Tonnage
	 */
	public double getTotalTonnage() {
		return totalTonnage;
	}

	/**
	 * @param set Total Tonnage
	 */

	public void setTotalTonnage(double totalTonnage) {
		this.totalTonnage = totalTonnage;
	}

	/**
	 * @return tons per GangHour
	 */
	public double getTotalTonsPerHour() {
		return totalTonsPerHour;
	}

	/**
	 * @param set the Total Tons per hour
	 */
	public void setTotalTonsPerHour(double totalTonsPerHour) {
		this.totalTonsPerHour = totalTonsPerHour;
	}

	/**
	 * @return tons per uniformed steel products tons per hour
	 */
	public double getUniSteelTonsPerHour() {
		return uniSteelTonsPerHour;
	}

	/**
	 * @param set the UniSteel Tons per Hour
	 */
	public void setUniSteelTonsPerHour(double uniSteelTonsPerHour) {
		this.uniSteelTonsPerHour = uniSteelTonsPerHour;
	}

	/**
	 * @return tons per GangHour
	 */
	public double getUniSteelTonsPerHourPerVsl() {
		return uniSteelTonsPerHourPerVsl;
	}

	/**
	 * @param set the unisteel tons per hour vessel
	 */
	public void setUniSteelTonsPerHourPerVsl(double uniSteelTonsPerHourPerVsl) {
		this.uniSteelTonsPerHourPerVsl = uniSteelTonsPerHourPerVsl;
	}

	/**
	 * @return the vessel Name
	 */

	public String getVesselName() {
		return vesselName;
	}

	/**
	 * @param set the time vessel name
	 */
	public void setVesselName(String vesselName) {
		this.vesselName = vesselName;
	}

	/**
	 * @return the agent
	 */

	public String getAgent() {
		return agent;
	}

	/**
	 * @param set the agent
	 */
	public void setAgent(String agent) {
		this.agent = agent;
	}

	/**
	 * @return the scheme
	 */

	public String getScheme() {
		return scheme;
	}

	/**
	 * @param set the scheme
	 */
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	/**
	 * @return the vessel code
	 */
	public String getVvCode() {
		return vvCode;
	}

	/**
	 * @param set the vvcdCode
	 */
	public void setVvCode(String vvCode) {
		this.vvCode = vvCode;
	}

	/**
	 * @return the workCommence date
	 */
	public Date getWorkCommence() {
		return workCommence;
	}

	/**
	 * @param set the work commence
	 */
	public void setWorkCommence(Date workCommence) {
		this.workCommence = workCommence;
	}

	/**
	 * @return the date work completed
	 */
	public Date getWorkCompleted() {
		return workCompleted;
	}

	/**
	 * @param set the workCompleted
	 */
	public void setWorkCompleted(Date workCompleted) {
		this.workCompleted = workCompleted;
	}

	/**
	 * return the get work hours
	 */
	public double getWorkHours() {
		return workHours;
	}

	public void setWorkHours(double workHours) {
		this.workHours = workHours;
	}

	/**
	 * @return the work hours
	 */
	public String getWorkHoursDHHMI() {
		return workHoursDHHMI;
	}

	/**
	 * @param set the work hours in day hours and minutes
	 */
	public void setWorkHoursDHHMI(String workHoursDHHMI) {
		this.workHoursDHHMI = workHoursDHHMI;
	}

	public String getLiner() {
		return liner;
	}

	public void setLiner(String liner) {
		this.liner = liner;
	}

	public int getHatchNbr() {
		return hatchNbr;
	}

	public void setHatchNbr(int hatchNbr) {
		this.hatchNbr = hatchNbr;
	}

	public double getBerthHr() {
		return berthHr;
	}

	public void setBerthHr(double berthHr) {
		this.berthHr = berthHr;
	}

	public double getCargoTonnage() {
		return cargoTonnage;
	}

	public void setCargoTonnage(double cargoTonnage) {
		this.cargoTonnage = cargoTonnage;
	}

	public double getCntrTonnage() {
		return cntrTonnage;
	}

	public void setCntrTonnage(double cntrTonnage) {
		this.cntrTonnage = cntrTonnage;
	}

	public double getBulkDischargeTonnage() {
		return bulkDischargeTonnage;
	}

	public void setBulkDischargeTonnage(double bulkDischargeTonnage) {
		this.bulkDischargeTonnage = bulkDischargeTonnage;
	}

	public double getBulkLoadTonnage() {
		return bulkLoadTonnage;
	}

	public void setBulkLoadTonnage(double bulkLoadTonnage) {
		this.bulkLoadTonnage = bulkLoadTonnage;
	}

	public double getBulkTonnage() {
		return bulkTonnage;
	}

	public void setBulkTonnage(double bulkTonnage) {
		this.bulkTonnage = bulkTonnage;
	}

	public double getActualWorkHr() {
		return actualWorkHr;
	}

	public void setActualWorkHr(double actualWorkHr) {
		this.actualWorkHr = actualWorkHr;
	}

	public Date getLastCargoDttm() {
		return lastCargoDttm;
	}

	public void setLastCargoDttm(Date lastCargoDttm) {
		this.lastCargoDttm = lastCargoDttm;
	}

	public Date getLastCargoGraceDttm() {
		return lastCargoGraceDttm;
	}

	public void setLastCargoGraceDttm(Date lastCargoGraceDttm) {
		this.lastCargoGraceDttm = lastCargoGraceDttm;
	}

	public String getProdSurcharge() {
		return prodSurcharge;
	}

	public void setProdSurcharge(String prodSurcharge) {
		this.prodSurcharge = prodSurcharge;
	}

	public String getCargoType() {
		return cargoType;
	}

	public void setCargoType(String cargoType) {
		this.cargoType = cargoType;
	}

	public Date getActualCargoDttm() {
		return actualCargoDttm;
	}

	public void setActualCargoDttm(Date actualCargoDttm) {
		this.actualCargoDttm = actualCargoDttm;
	}

	public double getGrossTonsPerHour() {
		return grossTonsPerHour;
	}

	public void setGrossTonsPerHour(double grossTonsPerHour) {
		this.grossTonsPerHour = grossTonsPerHour;
	}

	// Added by Punitha on 11/01/2010
	public String getHeavyLiftOverside() {
		return heavyLiftOverside;
	}

	public void setHeavyLiftOverside(String heavyLiftOverside) {
		this.heavyLiftOverside = heavyLiftOverside;
	}

	public String getVesselType() {
		return vesselType;
	}

	public void setVesselType(String vesselType) {
		this.vesselType = vesselType;
	}

	public String getFloatCraneInd() {
		return floatCraneInd;
	}

	public void setFloatCraneInd(String floatCraneInd) {
		this.floatCraneInd = floatCraneInd;
	}

	public double getTimeAtWork() {
		return timeAtWork;
	}

	public void setTimeAtWork(double timeAtWork) {
		this.timeAtWork = timeAtWork;
	}

	public double getDiscCntrTonnage() {
		return discCntrTonnage;
	}

	public void setDiscCntrTonnage(double discCntrTonnage) {
		this.discCntrTonnage = discCntrTonnage;
	}

	public double getLoadCntrTonnage() {
		return loadCntrTonnage;
	}

	public void setLoadCntrTonnage(double loadCntrTonnage) {
		this.loadCntrTonnage = loadCntrTonnage;
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
