/**
 * System Name:
 * Component ID:
 * Component Description: This is the ValueObject class for Manifest
 *
 * @author      Lakshmi
 * @version     10 January 2002
 */

/*
 * Revision History
 * ----------------
 * Author   Request Number  Description of Change   Version     Date Released
 * Lakshmi                   Creation                1.0         10 January 2002
 */

package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ManifestCargoValueObject  
{
	String cc_cd;
	String cicos_cd;
	String cc_name;
	
	public ManifestCargoValueObject()
	{
	}

	public void setCc_cd(String s)
	{
		cc_cd = s;
	}

	public String getCc_cd()
	{
		return cc_cd;
	}

	public void setCicos_cd(String s)
	{
		cicos_cd = s;
	}

	public String getCicos_cd()
	{
		return cicos_cd;
	}

	public void setCc_name(String s)
	{
		cc_name = s;
	}

	public String getCc_name()
	{
		return cc_name;
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
