package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import sg.com.jp.generalcargo.util.ProcessChargeConst;

public abstract class CabDataFetch {
	
	/**
	 * Only apply for RORO
	 * 
	 * @param xszParam
	 * @param xGbmsCargoBillingValueObject
	 * @return
	 */
	public String processMvmtForRORO(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {

		if ("L".equalsIgnoreCase(xGbmsCargoBillingValueObject.getCargoStatus()))
			xszParam = "LL";
		else if ("T".equalsIgnoreCase(xGbmsCargoBillingValueObject.getCargoStatus())
				|| "R".equalsIgnoreCase(xGbmsCargoBillingValueObject.getCargoStatus())) {
			if ("TS".equalsIgnoreCase(xGbmsCargoBillingValueObject.getTransStatus())) {
				xszParam = "TS";
			} else if ("IT".equalsIgnoreCase(xGbmsCargoBillingValueObject.getTransStatus())) {
				xszParam = "IT";
			}
		}
		return xszParam;
	}
	
	protected String getVesselScheme(String vesselScheme, String AbCd) {
		if (!(AbCd.equalsIgnoreCase(""))) {
			vesselScheme = "JLR";
		}
		/*
		 * if (vesselScheme.equalsIgnoreCase("JBT")) { vesselScheme="JNL"; }
		 */ // 29082002
		return vesselScheme;
	}

	protected String getCargoAcct(String actnbr) {
		if (actnbr != null && !actnbr.equals("") && actnbr.equalsIgnoreCase("CA"))
			actnbr = "CASH";
		return actnbr;
	}

	protected String getVesselType(String cargoType) {
		if (cargoType.equals("01") || cargoType.equals("02") || cargoType.equals("03")) {
			cargoType = "RO";
		} else {
			cargoType = "GL";
		}
		return cargoType;
	}

	protected String getSubCat(String cargoCategory) {
		String subCat = "";
		if (cargoCategory.equalsIgnoreCase("00") || cargoCategory.equalsIgnoreCase("MF")
				|| cargoCategory.equalsIgnoreCase("PG") || cargoCategory.equalsIgnoreCase("P1")
				|| cargoCategory.equalsIgnoreCase("P2") || cargoCategory.equalsIgnoreCase("P3")) {
			subCat = "GL";
		}
		if (cargoCategory.equalsIgnoreCase("01") || cargoCategory.equalsIgnoreCase("02")
				|| cargoCategory.equalsIgnoreCase("03")) {
			subCat = "RO";
		}
		if (cargoCategory.equalsIgnoreCase("WA") || cargoCategory.equalsIgnoreCase("LS")) {
			subCat = "AN";
		}
		if (cargoCategory.equalsIgnoreCase("EX")) {
			subCat = "EX";
		}
		return subCat;
	}

	protected Timestamp getPrintDttm(Object OBJprintDttm) {
		Timestamp printDttm = null;
		if (OBJprintDttm != null) {
			printDttm = (Timestamp) OBJprintDttm;
		}
		return printDttm;
	}

	protected Timestamp getLastModifyDttm(Object dttm) {
		Timestamp lastModifyDttm = (Timestamp) dttm;
		return lastModifyDttm;
	}

	protected double getBillTonBl(String billTonBl) {
		if (billTonBl.equalsIgnoreCase("")) {
			billTonBl = "0";
		}
		double DBbillTonBl = Double.parseDouble(billTonBl);
		return DBbillTonBl;
	}

	protected double getBillTonEdo(String billTonEdo) {
		if (billTonEdo.equalsIgnoreCase("")) {
			billTonEdo = "0";
		}
		double DBbillTonEdo = Double.parseDouble(billTonEdo);
		return DBbillTonEdo;
	}

	protected double getBillTonDn(String billTonDn) {
		if (billTonDn.equalsIgnoreCase("")) {
			billTonDn = "0";
		}
		double DBbillTonDn = Double.parseDouble(billTonDn);
		return DBbillTonDn;
	}

	protected double getBillTonEsn(String billTonEsn) {
		if (billTonEsn.equalsIgnoreCase("")) {
			billTonEsn = "0";
		}
		double DBbillTonEsn = Double.parseDouble(billTonEsn);
		return DBbillTonEsn;
	}

	protected double getBillTonBkg(String billTonBkg) {
		if (billTonBkg.equalsIgnoreCase("")) {
			billTonBkg = "0";
		}
		double DBbillTonBkg = Double.parseDouble(billTonBkg);
		return DBbillTonBkg;
	}

	protected double getLoadTonCs(String loadTonCs) {
		if (loadTonCs.equalsIgnoreCase("")) {
			loadTonCs = "0";
		}
		double DBloadTonCs = Double.parseDouble(loadTonCs);
		return DBloadTonCs;
	}

	protected double getShutoutTonCs(String shutoutTonCs) {
		if (shutoutTonCs.equalsIgnoreCase("")) {
			shutoutTonCs = "0";
		}
		double DBshutoutTonCs = Double.parseDouble(shutoutTonCs);
		return DBshutoutTonCs;
	}

	protected int getCountUnit(String countUnit) {
		if (countUnit.equalsIgnoreCase("")) {
			countUnit = "0";
		}
		int INTcountUnit = Integer.parseInt(countUnit);
		return INTcountUnit;
	}

	protected int getTotalPackEdo(String totalPackEdo) {
		if (totalPackEdo.equalsIgnoreCase("")) {
			totalPackEdo = "0";
		}
		int INTtotalPackEdo = Integer.parseInt(totalPackEdo);
		return INTtotalPackEdo;
	}

	protected int getTotalPackDn(String totalPackDn) {
		if (totalPackDn.equalsIgnoreCase("")) {
			totalPackDn = "0";
		}
		int INTtotalPackDn = Integer.parseInt(totalPackDn);
		return INTtotalPackDn;
	}

//[Spr001
	public String processDiscVVcd(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xszParam.equalsIgnoreCase("disc")) {
			/*
			 * if(xGbmsCargoBillingValueObject.getCargoStatus().equalsIgnoreCase("T") ||
			 * xGbmsCargoBillingValueObject.getCargoStatus().equalsIgnoreCase("R")) // T|R -
			 * transhipment/re-export xszParam = xGbmsCargoBillingValueObject.getDiscVvCd();
			 * } if(xszParam.equalsIgnoreCase("L")) {
			 * xszParam=xGbmsCargoBillingValueObject.getDiscVvCd(); }
			 */
			xszParam = xGbmsCargoBillingValueObject.getDiscVvCd();
		} else {
			xszParam = "";
		}
		return xszParam;
	}

	public String processLoadVVcd(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xszParam.equalsIgnoreCase("load")) {
			/*
			 * if(xGbmsCargoBillingValueObject.getCargoStatus().equalsIgnoreCase("T") ||
			 * xGbmsCargoBillingValueObject.getCargoStatus().equalsIgnoreCase("R")) // T|R -
			 * transhipment/re-export xszParam=xGbmsCargoBillingValueObject.getLoadVvCd(); }
			 * if(xszParam.equalsIgnoreCase("L")) {
			 * xszParam=xGbmsCargoBillingValueObject.getLoadVvCd(); }
			 */
			xszParam = xGbmsCargoBillingValueObject.getLoadVvCd();
		} else {
			xszParam = "";
		}
		return xszParam;
	}

	public String processVvInd(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		return xszParam;
	}

	public String processBusinessType(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		return xszParam;
	}

	public String processSchemeCd(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {

		return xGbmsCargoBillingValueObject.getVesselScheme();
	}

	public String processTariffMainCatCd(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		return xszParam;
	}

	public String processTariffSubCatCd(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		return xszParam;
	}

	public String processMvmt(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xszParam.equalsIgnoreCase("SE")) {
			if (xGbmsCargoBillingValueObject.getCargoStatus().equalsIgnoreCase("L"))
				xszParam = new String("LL");
			else if (xGbmsCargoBillingValueObject.getCargoStatus().equalsIgnoreCase("T")
					|| xGbmsCargoBillingValueObject.getCargoStatus().equalsIgnoreCase("R"))
				xszParam = new String(xGbmsCargoBillingValueObject.getTransStatus());
		}
		return xszParam;
	}

	public String processType(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		xszParam = new String(xGbmsCargoBillingValueObject.getType());
		return xszParam;
	}

	public String processCargoType(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		return xszParam;
	}

	public String processLocalLeg(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		return xszParam;
	}

	public String processDiscGateway(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		return xszParam;
	}

	public String processDiscGateWay(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xszParam.equalsIgnoreCase("E"))
			if (xGbmsCargoBillingValueObject.getCargoStatus().equalsIgnoreCase("T")
					|| xGbmsCargoBillingValueObject.getCargoStatus().equalsIgnoreCase("R")) {
				if (xGbmsCargoBillingValueObject.getTransStatus().equalsIgnoreCase("IT"))
					xszParam = "P";
				else if (xGbmsCargoBillingValueObject.getTransStatus().equalsIgnoreCase("TS"))
					xszParam = "J";
			}
		return xszParam;
	}

	public String processRefInd(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		return xszParam;
	}

	public String processBlNbr(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xszParam.equalsIgnoreCase("x"))
			xszParam = new String(xGbmsCargoBillingValueObject.getBlNbr());
		return xszParam;
	}

	public String processEdoAsnNbr(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xszParam.equalsIgnoreCase("x"))
			xszParam = new String(xGbmsCargoBillingValueObject.getEdoAsnNbr());
		return xszParam;
	}

	public String processBkRefNbr(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xszParam.equalsIgnoreCase("x"))
			xszParam = new String(xGbmsCargoBillingValueObject.getBkRefNbr());
		return xszParam;
	}

	public String processEsnAsnNbr(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xszParam.equalsIgnoreCase("x"))
			xszParam = new String(xGbmsCargoBillingValueObject.getEsnAsnNbr());
		return xszParam;
	}

	public String processDnNbr(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xszParam.equalsIgnoreCase("x"))
			xszParam = new String(xGbmsCargoBillingValueObject.getDnNbr());
		return xszParam;
	}

	public String processUaNbr(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xszParam.equalsIgnoreCase("x"))
			xszParam = new String(xGbmsCargoBillingValueObject.getUaNbr());
		return xszParam;
	}

	public double processBillTonBl(double xdParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xdParam == -1)
			xdParam = xGbmsCargoBillingValueObject.getBillTonEdo();
		return xdParam;
	}

	public double processBillTonEdo(double xdParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xdParam == -1)
			xdParam = xGbmsCargoBillingValueObject.getBillTonEdo();
		return xdParam;
	}

	public double processBillTonDn(double xdParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xdParam == -1)
			xdParam = xGbmsCargoBillingValueObject.getBillTonDn();
		return xdParam;
	}

	public double processBillTonEsn(double xdParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xdParam == -1)
			xdParam = xGbmsCargoBillingValueObject.getBillTonEsn();
		return xdParam;
	}

	public double processBillTonBkg(double xdParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xdParam == -1)
			xdParam = xGbmsCargoBillingValueObject.getBillTonBkg();
		return xdParam;
	}

	public double processLoadTonCs(double xdParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xdParam == -1)
			xdParam = xGbmsCargoBillingValueObject.getLoadTonCs();
		return xdParam;
	}

	public double processShutoutTonCs(double xdParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xdParam == -1)
			xdParam = xGbmsCargoBillingValueObject.getShutoutTonCs();
		return xdParam;
	}

	public int processCountUnit(int xdParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xdParam == -1)
			xdParam = xGbmsCargoBillingValueObject.getCountUnit();
		return xdParam;
	}

	public int processTotalPackEdo(int xdParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xdParam == -1)
			xdParam = xGbmsCargoBillingValueObject.getTotalPackEdo();
		return xdParam;
	}

	public int processTotalPackDn(int xdParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		if (xdParam == -1)
			xdParam = xGbmsCargoBillingValueObject.getTotalPackDn();
		return xdParam;
	}

	public String processBillAcctNbr(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		String mschactnbr = new String(xGbmsCargoBillingValueObject.getMixedSchemeAcct());

		if (xszParam.equalsIgnoreCase("SA")) {
			if (xGbmsCargoBillingValueObject.getMixedSchemeAcct().trim().length() != 0 && mschactnbr != null
					&& !mschactnbr.equals("") && !mschactnbr.equals("null"))
				xszParam = new String(xGbmsCargoBillingValueObject.getMixedSchemeAcct());
			else if (xGbmsCargoBillingValueObject.getAbCd().trim().length() != 0)
				xszParam = new String(xGbmsCargoBillingValueObject.getAbCd());
			else
				xszParam = new String(xGbmsCargoBillingValueObject.getSaAcct());
		}
		if (xszParam.equalsIgnoreCase("CA"))
			xszParam = new String(xGbmsCargoBillingValueObject.getCargoAcct());
		if (xszParam.equalsIgnoreCase("TR")) {
			String strCargoStatus = xGbmsCargoBillingValueObject.getCargoStatus();
			String strTransStatus = xGbmsCargoBillingValueObject.getTransStatus();
			if ((strCargoStatus.equalsIgnoreCase("L"))
					|| ((strCargoStatus.equalsIgnoreCase("R")) && (strTransStatus.equalsIgnoreCase("TH")))
					|| ((strCargoStatus.equalsIgnoreCase("T")) && (strTransStatus.equalsIgnoreCase("TH")))) {
				xszParam = new String(xGbmsCargoBillingValueObject.getCargoAcct());
			}
			if (((strCargoStatus.equalsIgnoreCase("R")) && (strTransStatus.equalsIgnoreCase("IT")))
					|| ((strCargoStatus.equalsIgnoreCase("T")) && (strTransStatus.equalsIgnoreCase("IT")))) {
				xszParam = new String(xGbmsCargoBillingValueObject.getSaAcct());
			}

		}
		return xszParam;
	}

	public Timestamp processPrintDttm(String xszParam, GbmsCargoBillingValueObject xGbmsCargoBillingValueObject) {
		Timestamp zTime = null;
		if (xszParam.equalsIgnoreCase("x"))
			zTime = xGbmsCargoBillingValueObject.getPrintDttm();
		return zTime;
	}

//Spr001]

	// 06/06/2013 PCYAP To waive Empty Mafi wharfage charge
	protected String deriveType(String cargoCategoryCode, String defaultType) {
		String type = null;

		if (ProcessChargeConst.CARGO_CATEGORY_CODE.EMPTY_MAFI.equalsIgnoreCase(cargoCategoryCode)) {
			type = ProcessChargeConst.CARGO_CATEGORY_CODE.EMPTY_MAFI;

		} else if (ProcessChargeConst.CARGO_CATEGORY_CODE.PDC_CARGO_GENERAL.equalsIgnoreCase(cargoCategoryCode)) {
			type = ProcessChargeConst.CARGO_CATEGORY_CODE.PDC_CARGO_GENERAL;

		} else if (ProcessChargeConst.CARGO_CATEGORY_CODE.PDC_CARGO_PASSENGER_CAR.equalsIgnoreCase(cargoCategoryCode)) {
			type = ProcessChargeConst.CARGO_CATEGORY_CODE.PDC_CARGO_PASSENGER_CAR;

		} else if (ProcessChargeConst.CARGO_CATEGORY_CODE.PDC_CARGO_STATION_WAGON.equalsIgnoreCase(cargoCategoryCode)) {
			type = ProcessChargeConst.CARGO_CATEGORY_CODE.PDC_CARGO_STATION_WAGON;

		} else if (ProcessChargeConst.CARGO_CATEGORY_CODE.PDC_CARGO_LORRY.equalsIgnoreCase(cargoCategoryCode)) {
			type = ProcessChargeConst.CARGO_CATEGORY_CODE.PDC_CARGO_LORRY;

		} else {
			type = defaultType;

		}

		return type;
	}
	
}
