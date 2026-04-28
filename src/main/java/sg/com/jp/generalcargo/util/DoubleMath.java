package sg.com.jp.generalcargo.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DoubleMath{

	private static final Log log = LogFactory.getLog(DoubleMath.class);
	private int scale;
	private int roundingMode;
	public DoubleMath(){
		scale = -1;
		roundingMode = BigDecimal.ROUND_HALF_UP;
	}

	public double add(double d1, double d2){
		return add(Double.toString(d1), Double.toString(d2));
	}
	public double subtract(double d1, double d2){
		return subtract(Double.toString(d1), Double.toString(d2));
	}
	public double multiply(double d1, double d2){
		return multiply(Double.toString(d1), Double.toString(d2));
	}
	public double divide(double d1, double d2){
		DecimalFormat df = new DecimalFormat("#.00000#####");
		String s1 = Double.toString(d1);
		String s2 = Double.toString(d2);
		try {
			s1 = df.format(d1);
			s2 = df.format(d2);
		} catch (Exception e) {
			log.error("Exception divide : ", e);
		}
		return divide(s1, s2);
	}
	
	public double add(String s1, String s2){
		BigDecimal bd1 = new BigDecimal(s1);
		BigDecimal bd2 = new BigDecimal(s2);
		if (scale >= 0){
			bd1.setScale(scale, roundingMode);
			bd2.setScale(scale, roundingMode);
		}
		BigDecimal res = bd1.add(bd2);
		return res.doubleValue();
	}

	public double subtract(String s1, String s2){
		BigDecimal bd1 = new BigDecimal(s1);
		BigDecimal bd2 = new BigDecimal(s2);
		if (scale >= 0){
			bd1.setScale(scale, roundingMode);
			bd2.setScale(scale, roundingMode);
		}
		BigDecimal res = bd1.subtract(bd2);
		return res.doubleValue();
	}

	public double multiply(String s1, String s2){
		BigDecimal bd1 = new BigDecimal(s1);
		BigDecimal bd2 = new BigDecimal(s2);
		if (scale >= 0){
			bd1.setScale(scale, roundingMode);
			bd2.setScale(scale, roundingMode);
		}
		BigDecimal res = bd1.multiply(bd2);
		return res.doubleValue();
	}

	public double divide(String s1, String s2){
		BigDecimal bd1 = new BigDecimal(s1);
		BigDecimal bd2 = new BigDecimal(s2);
		BigDecimal res = null;
		if (scale >= 0){
			bd1.setScale(scale, roundingMode);
			bd2.setScale(scale, roundingMode);
			res = bd1.divide(bd2, scale, roundingMode);
		}else{
			res = bd1.divide(bd2, roundingMode);
		}
		return res.doubleValue();
	}

	public double abs(double d1){
		return abs(Double.toString(d1));
	}
	public double abs(String s1){
		return abs(new BigDecimal(s1));
	}
	public double abs(BigDecimal bd){
		return bd.abs().doubleValue();
	}
	public boolean equals(double d1, double d2){
		return equals(Double.toString(d1), Double.toString(d2));
	}
	public boolean equals(String s1, String s2){
		BigDecimal bd1 = new BigDecimal(s1);
		BigDecimal bd2 = new BigDecimal(s2);
		return bd1.equals(bd2);
	}

		
	public void setScale(int scale) { this.scale = scale; }
	public void setRoundingMode(int roundingMode) { this.roundingMode = roundingMode; }
	public int getScale() { return (this.scale); }
	public int getRoundingMode() { return (this.roundingMode); }
}