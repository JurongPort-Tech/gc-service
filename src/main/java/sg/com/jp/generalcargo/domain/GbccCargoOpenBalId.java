package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccCargoOpenBalId implements Serializable
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String vvCd;
    private String stevCoCd;

    public GbccCargoOpenBalId() {
    }

    public GbccCargoOpenBalId(String vvCd, String stevCoCd) {
        this.vvCd = vvCd;
        this.stevCoCd = stevCoCd;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GbccCargoOpenBalId)) {
            return false;
        }
        GbccCargoOpenBalId other = ((GbccCargoOpenBalId) o);
        if (this.vvCd == null) {
            if (other.vvCd!= null) {
                return false;
            }
        } else {
            if (!this.vvCd.equals(other.vvCd)) {
                return false;
            }
        }
        if (this.stevCoCd == null) {
            if (other.stevCoCd!= null) {
                return false;
            }
        } else {
            if (!this.stevCoCd.equals(other.stevCoCd)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int rtn = 17;
        rtn = (rtn* 37);
        if (this.vvCd!= null) {
            rtn = (rtn + this.vvCd.hashCode());
        }
        rtn = (rtn* 37);
        if (this.stevCoCd!= null) {
            rtn = (rtn + this.stevCoCd.hashCode());
        }
        return rtn;
    }

    public String getVvCd() {
        return vvCd;
    }

    public void setVvCd(String vvCd) {
        this.vvCd = vvCd;
    }

    public String getStevCoCd() {
        return stevCoCd;
    }

    public void setStevCoCd(String stevCoCd) {
        this.stevCoCd = stevCoCd;
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
