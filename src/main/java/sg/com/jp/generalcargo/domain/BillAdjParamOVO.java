package sg.com.jp.generalcargo.domain;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BillAdjParamOVO extends BillAbstractAdjParamVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(BillAdjParamOVO.class);
	public BillAdjParamOVO() {}
	
	public double getTotalAmount(){
		BigDecimal rate = new BigDecimal("" + this.getUnitRate());
		BigDecimal time = new BigDecimal("" + this.getTotalTime());
		BigDecimal other = new BigDecimal("" + this.getTotalOtherUnit());
		BigDecimal cntr = new BigDecimal("" + this.getTotalContainer());
		BigDecimal result = rate.multiply(other);  
		log.info("@@@@@@@@before..bigdecimal...result = " + result);
		log.info("@@@@@@@@after..set scale...result = " + result.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
		double retVal = result.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		log.info("@@@@@@@@after..double...retVal = " + retVal); 
		if (DEBUG) log.info(result.toString() + " " + retVal);

		return retVal;
		//return (new BigDecimal(""+this.getUnitRate()).multiply(new BigDecimal(""+this.getTotalOtherUnit()))).doubleValue();
	}
	
	public String writeJS(){
		sb.setLength(0);
		sb.append("var ");
		sb.append(TOTAL_AMT);
		sb.append(" = ");
		sb.append(TOTAL_OTHER);
		sb.append(" * ");
		sb.append(UNIT_RATE);
		sb.append(";\n");
		return sb.toString();
	}
}