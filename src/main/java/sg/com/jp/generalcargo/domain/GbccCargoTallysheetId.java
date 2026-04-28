package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccCargoTallysheetId  implements Serializable
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String vvCd;
    private Date createDttm;
    private String oprType;
    private Integer hatchNbr;

    public GbccCargoTallysheetId() {
    }

    public GbccCargoTallysheetId(String vvCd, Date createDttm, String oprType, Integer hatchNbr) {
        this.vvCd = vvCd;
        this.createDttm = createDttm;
        this.oprType = oprType;
        this.hatchNbr = hatchNbr;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GbccCargoTallysheetId)) {
            return false;
        }
        GbccCargoTallysheetId other = ((GbccCargoTallysheetId) o);
        if (this.vvCd == null) {
            if (other.vvCd!= null) {
                return false;
            }
        } else {
            if (!this.vvCd.equals(other.vvCd)) {
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
        if (this.oprType == null) {
            if (other.oprType!= null) {
                return false;
            }
        } else {
            if (!this.oprType.equals(other.oprType)) {
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
        if (this.createDttm!= null) {
            rtn = (rtn + this.createDttm.hashCode());
        }
        rtn = (rtn* 37);
        if (this.oprType!= null) {
            rtn = (rtn + this.oprType.hashCode());
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

    public Date getCreateDttm() {
        return createDttm;
    }

    public void setCreateDttm(Date createDttm) {
        this.createDttm = createDttm;
    }

    public String getOprType() {
        return oprType;
    }

    public void setOprType(String oprType) {
        this.oprType = oprType;
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
