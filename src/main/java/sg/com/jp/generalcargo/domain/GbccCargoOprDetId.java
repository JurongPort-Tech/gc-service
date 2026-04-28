package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccCargoOprDetId  implements Serializable
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
    private Date createDttm;
    private Integer hatchNbr;

    public GbccCargoOprDetId() {
    }

    public GbccCargoOprDetId(String vvCd, String stevCoCd, Date createDttm, Integer hatchNbr) {
        this.vvCd = vvCd;
        this.stevCoCd = stevCoCd;
        this.createDttm = createDttm;
        this.hatchNbr = hatchNbr;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GbccCargoOprDetId)) {
            return false;
        }
        GbccCargoOprDetId other = ((GbccCargoOprDetId) o);
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
        if (this.createDttm == null) {
            if (other.createDttm!= null) {
                return false;
            }
        } else {
            if (!this.createDttm.equals(other.createDttm)) {
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
        if (this.createDttm!= null) {
            rtn = (rtn + this.createDttm.hashCode());
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

    public Date getCreateDttm() {
        return createDttm;
    }

    public void setCreateDttm(Date createDttm) {
        this.createDttm = createDttm;
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
