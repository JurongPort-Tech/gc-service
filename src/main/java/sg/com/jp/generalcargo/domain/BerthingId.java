package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BerthingId implements Serializable
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String vvCd;
    private Integer shiftInd;

    public BerthingId() {
    }

    public BerthingId(String vvCd, Integer shiftInd) {
        this.vvCd = vvCd;
        this.shiftInd = shiftInd;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BerthingId)) {
            return false;
        }
        BerthingId other = ((BerthingId) o);
        if (this.vvCd == null) {
            if (other.vvCd!= null) {
                return false;
            }
        } else {
            if (!this.vvCd.equals(other.vvCd)) {
                return false;
            }
        }
        if (this.shiftInd == null) {
            if (other.shiftInd!= null) {
                return false;
            }
        } else {
            if (!this.shiftInd.equals(other.shiftInd)) {
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
        if (this.shiftInd!= null) {
            rtn = (rtn + this.shiftInd.hashCode());
        }
        return rtn;
    }

    public String getVvCd() {
        return vvCd;
    }

    public void setVvCd(String vvCd) {
        this.vvCd = vvCd;
    }

    public Integer getShiftInd() {
        return shiftInd;
    }

    public void setShiftInd(Integer shiftInd) {
        this.shiftInd = shiftInd;
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
