package sg.com.jp.generalcargo.domain;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BillAdjParamCTOVO extends BillAbstractAdjParamVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public BillAdjParamCTOVO() {}
	private static final Log log = LogFactory.getLog(BillAdjParamCTOVO.class);
	public double getTotalAmount(){
		BigDecimal rate = new BigDecimal("" + this.getUnitRate());
		BigDecimal time = new BigDecimal("" + this.getTotalTime());
		BigDecimal other = new BigDecimal("" + this.getTotalOtherUnit());
		BigDecimal cntr = new BigDecimal("" + this.getTotalContainer());
		BigDecimal result = rate.multiply(cntr).multiply(time).multiply(other);
		double retVal = result.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if (DEBUG) log.info(result.toString() + " " + retVal);
		return retVal;
		//return (new BigDecimal(""+this.getUnitRate()).multiply(new BigDecimal(""+this.getTotalContainer())).multiply(new BigDecimal(""+this.getTotalTime())).multiply(new BigDecimal(""+this.getTotalOtherUnit()))).doubleValue();
	}
	
	public String writeJS(){
		sb.setLength(0);
		sb.append("var ");
		sb.append(TOTAL_AMT);
		sb.append(" = ");
		sb.append(TOTAL_CNTR);
		sb.append(" * ");
		sb.append(TOTAL_TIME);
		sb.append(" * ");
		sb.append(TOTAL_OTHER);
		sb.append(" * ");
		sb.append(UNIT_RATE);
		sb.append(";\n");
		return sb.toString();
	}
}
