package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccVslProdId implements Serializable
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date createDttm;
    private String vvCd;
    private String stevCoCd;

    public GbccVslProdId() {
    }

    public GbccVslProdId(Date createDttm, String vvCd, String stevCoCd) {
        this.createDttm = createDttm;
        this.vvCd = vvCd;
        this.stevCoCd = stevCoCd;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GbccVslProdId)) {
            return false;
        }
        GbccVslProdId other = ((GbccVslProdId) o);
        if (this.createDttm == null) {
            if (other.createDttm!= null) {
                return false;
            }
        } else {
            if (!this.createDttm.equals(other.createDttm)) {
                return false;
            }
        }
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
        if (this.createDttm!= null) {
            rtn = (rtn + this.createDttm.hashCode());
        }
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

    public Date getCreateDttm() {
        return createDttm;
    }

    public void setCreateDttm(Date createDttm) {
        this.createDttm = createDttm;
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
