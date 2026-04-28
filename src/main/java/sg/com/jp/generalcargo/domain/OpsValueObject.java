package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * System Name: TOPs (Terminal Operation and Planning System)
 * Component ID: YardOpsValueObject.java (OPS - Yard Operations)
 * Component Description: This is the ValueObject class for Yard Operations.
 *
 * @author      JHD
 * @version     23 October 2001
 */

/*
 * Revision History
 * ----------------
 * Author   Request Number  Description of Change   Version     Date Released
 * JHD                      Creation                1.0         23 October 2001
 */

public class OpsValueObject implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Holds value of property value. */
	private String[][] tableData;
	private List<String> arrayData;
	private String[][] secondTableData;
	private String[][] thirdTableData;
	private String[][][] table3DData;
	private List<Object> dateVector;
	private int totalCnt[];

	/** Constructor */
	public OpsValueObject() {
	}

	public void setTableData(String[][] tableData) {
		this.tableData = tableData;
	}

	public void set3DTableData(String[][][] table3DData) {
		this.table3DData = table3DData;
	}

	public void setArrayData(List<String> arrayData) {
		this.arrayData = arrayData;
	}

	public void setSecondTableData(String[][] secondTableData) {
		this.secondTableData = secondTableData;
	}

	public void setThirdTableData(String[][] thirdTableData) {
		this.thirdTableData = thirdTableData;
	}

	public String[][] getTableData() {
		return tableData;
	}

	public String[][][] get3DTableData() {
		return table3DData;
	}

	public List<String> getArrayData() {
		return arrayData;
	}

	public String[][] getSecondTableData() {
		return secondTableData;
	}

	public String[][] getThirdTableData() {
		return thirdTableData;
	}

	public void setDateVector(List<Object> dateVector) {
		this.dateVector = dateVector;
	}

	public List<Object> getDateVector() {
		return dateVector;
	}

	public void setTotalCnt(int totalCnt[]) {
		this.totalCnt = totalCnt;
	}

	public int[] getTotalCnt() {
		return totalCnt;
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
