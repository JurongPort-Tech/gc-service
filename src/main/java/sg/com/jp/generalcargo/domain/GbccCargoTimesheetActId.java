package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccCargoTimesheetActId implements Serializable
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
    private Integer recSeqNbr;

    public GbccCargoTimesheetActId() {
    }

    public GbccCargoTimesheetActId(String vvCd, Date createDttm, Integer recSeqNbr) {
        this.vvCd = vvCd;
        this.createDttm = createDttm;
        this.recSeqNbr = recSeqNbr;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GbccCargoTimesheetActId)) {
            return false;
        }
        GbccCargoTimesheetActId other = ((GbccCargoTimesheetActId) o);
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
            if (other.createDttm != null) {
                return false;
            }
        } else {
        	if (other.createDttm == null) {
        		return false;
        	}
            if (!(this.createDttm.getTime() == other.createDttm.getTime())) {
                return false;
            }
        }
        if (this.recSeqNbr == null) {
            if (other.recSeqNbr!= null) {
                return false;
            }
        } else {
            if (!this.recSeqNbr.equals(other.recSeqNbr)) {
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
        if (this.recSeqNbr!= null) {
            rtn = (rtn + this.recSeqNbr.hashCode());
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

    public Integer getRecSeqNbr() {
        return recSeqNbr;
    }

    public void setRecSeqNbr(Integer recSeqNbr) {
        this.recSeqNbr = recSeqNbr;
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
