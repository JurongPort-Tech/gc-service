package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccCargoOpenBalDetId  implements Serializable
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
    private Integer hatchNbr;

    public GbccCargoOpenBalDetId() {
    }

    public GbccCargoOpenBalDetId(String vvCd, String stevCoCd, Integer hatchNbr) {
        this.vvCd = vvCd;
        this.stevCoCd = stevCoCd;
        this.hatchNbr = hatchNbr;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GbccCargoOpenBalDetId)) {
            return false;
        }
        GbccCargoOpenBalDetId other = ((GbccCargoOpenBalDetId) o);
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
        if (this.hatchNbr == null) {
            if (other.hatchNbr!= null) {
                return false;
            }
        } else {
            if (!this.hatchNbr.equals(other.hatchNbr)) {
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
        rtn = (rtn* 37);
        if (this.hatchNbr!= null) {
            rtn = (rtn + this.hatchNbr.hashCode());
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

    public Integer getHatchNbr() {
        return hatchNbr;
    }

    public void setHatchNbr(Integer hatchNbr) {
        this.hatchNbr = hatchNbr;
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
