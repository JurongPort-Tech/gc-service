package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccViewVvStevedoreId  implements Serializable
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
    private BigDecimal lineno;
    

    public GbccViewVvStevedoreId() {
    }

    public GbccViewVvStevedoreId(String vvCd, String stevCoCd, BigDecimal lineno) {
        this.vvCd = vvCd;
        this.stevCoCd = stevCoCd;
        
        this.lineno = lineno;
        
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GbccViewVvStevedoreId)) {
            return false;
        }
        GbccViewVvStevedoreId other = ((GbccViewVvStevedoreId) o);
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
        
        if (this.lineno == null) {
            if (other.lineno!= null) {
                return false;
            }
        } else {
            if (!this.lineno.equals(other.lineno)) {
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
        if (this.lineno!= null) {
            rtn = (rtn + this.lineno.hashCode());
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

    

    public BigDecimal getLineno() {
        return lineno;
    }

    public void setLineno(BigDecimal lineno) {
        this.lineno = lineno;
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
