package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class TesnVesselVoyValueObject implements TopsIObject
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TesnVesselVoyValueObject()
    {
    }

    public void setVslName(String s)
    {
        vslName = s;
    }

    public String getVslName()
    {
        return vslName;
    }

    public void setVoyNo(String s)
    {
        voyNo = s;
    }

    public String getVoyNo()
    {
        return voyNo;
    }

    public void setVvVoy(String s)
    {
        vvVoy = s;
    }

    public String getVvVoy()
    {
        return vvVoy;
    }

    public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	String vslName;
    String voyNo;
    String vvVoy;
    String terminal;
    
    @Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	
}
