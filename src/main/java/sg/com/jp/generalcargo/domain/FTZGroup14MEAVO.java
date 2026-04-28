package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Group 14 MEA MEASUREMENT
public class FTZGroup14MEAVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// measurement application qualifier
	private String measure_appl_qual;
	
	// measurement details
	private String measure_dim_cdd;
	private String measure_sign_cdd;
	private String measure_attr_cdd;
	private String measure_attr;
	
	// value/range
	private String measure_unit_qual;
	private double measure_val;
	private double range_min;
	private double range_max;
	private int sign_digit;
	
	// surface/layer indicator, coded
	private String surface_layer_ind_cdd;

	public String getMeasure_appl_qual() {
		return measure_appl_qual;
	}

	public void setMeasure_appl_qual(String measure_appl_qual) {
		this.measure_appl_qual = measure_appl_qual;
	}

	public String getMeasure_dim_cdd() {
		return measure_dim_cdd;
	}

	public void setMeasure_dim_cdd(String measure_dim_cdd) {
		this.measure_dim_cdd = measure_dim_cdd;
	}

	public String getMeasure_sign_cdd() {
		return measure_sign_cdd;
	}

	public void setMeasure_sign_cdd(String measure_sign_cdd) {
		this.measure_sign_cdd = measure_sign_cdd;
	}

	public String getMeasure_attr_cdd() {
		return measure_attr_cdd;
	}

	public void setMeasure_attr_cdd(String measure_attr_cdd) {
		this.measure_attr_cdd = measure_attr_cdd;
	}

	public String getMeasure_attr() {
		return measure_attr;
	}

	public void setMeasure_attr(String measure_attr) {
		this.measure_attr = measure_attr;
	}

	public String getMeasure_unit_qual() {
		return measure_unit_qual;
	}

	public void setMeasure_unit_qual(String measure_unit_qual) {
		this.measure_unit_qual = measure_unit_qual;
	}

	public double getMeasure_val() {
		return measure_val;
	}

	public void setMeasure_val(double measure_val) {
		this.measure_val = measure_val;
	}

	public double getRange_min() {
		return range_min;
	}

	public void setRange_min(double range_min) {
		this.range_min = range_min;
	}

	public double getRange_max() {
		return range_max;
	}

	public void setRange_max(double range_max) {
		this.range_max = range_max;
	}

	public int getSign_digit() {
		return sign_digit;
	}

	public void setSign_digit(int sign_digit) {
		this.sign_digit = sign_digit;
	}

	public String getSurface_layer_ind_cdd() {
		return surface_layer_ind_cdd;
	}

	public void setSurface_layer_ind_cdd(String surface_layer_ind_cdd) {
		this.surface_layer_ind_cdd = surface_layer_ind_cdd;
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
