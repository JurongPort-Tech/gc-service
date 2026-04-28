package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Copyright 2001 Software Design and Consultancy Pte Ltd. All Rights Reserved.
 * System Name          : GBMS (General and Bulk Cargo Management Systems)
 * Module               : containerised - unstuffing
 * Component ID         : UnStuffingCargoValueObject.java
 * Component Description: This class stores Unstuffing Cargo Values.
 *
 * @author      Vani
 * @version     19th Sept 2003
 *
 * Revision History
 * ---------------
 * Author     Request Number    Description of Change     Version     Date Released
 * Vani          -              Creation                    1.1       19 Sept 2003
 */

public class UnStuffingCargoValueObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5391626256477001609L;
	public UnStuffingCargoValueObject() {
	}

	public void setCc_cd(String s) {
		cc_cd = s;
	}

	public String getCc_cd() {
		return cc_cd;
	}

	public void setCicos_cd(String s) {
		cicos_cd = s;
	}

	public String getCicos_cd() {
		return cicos_cd;
	}

	public void setCc_name(String s) {
		cc_name = s;
	}

	public String getCc_name() {
		return cc_name;
	}

	String cc_cd;
	String cicos_cd;
	String cc_name;
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
}
