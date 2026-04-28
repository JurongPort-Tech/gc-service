package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccCargoTimesheetId implements Serializable
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

    public GbccCargoTimesheetId() {
    }

    public GbccCargoTimesheetId(String vvCd, Date createDttm) {
        this.vvCd = vvCd;
        this.createDttm = createDttm;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GbccCargoTimesheetId)) {
            return false;
        }
        GbccCargoTimesheetId other = ((GbccCargoTimesheetId) o);
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
    
    
    @Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
	
}
