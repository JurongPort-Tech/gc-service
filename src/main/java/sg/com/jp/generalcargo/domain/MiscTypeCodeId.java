package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MiscTypeCodeId implements Serializable
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String catCd;
    private String miscTypeCd;

    public MiscTypeCodeId() {
    }

    public MiscTypeCodeId(String catCd, String miscTypeCd) {
        this.catCd = catCd;
        this.miscTypeCd = miscTypeCd;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MiscTypeCodeId)) {
            return false;
        }
        MiscTypeCodeId other = ((MiscTypeCodeId) o);
        if (this.catCd == null) {
            if (other.catCd!= null) {
                return false;
            }
        } else {
            if (!this.catCd.equals(other.catCd)) {
                return false;
            }
        }
        if (this.miscTypeCd == null) {
            if (other.miscTypeCd!= null) {
                return false;
            }
        } else {
            if (!this.miscTypeCd.equals(other.miscTypeCd)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int rtn = 17;
        rtn = (rtn* 37);
        if (this.catCd!= null) {
            rtn = (rtn + this.catCd.hashCode());
        }
        rtn = (rtn* 37);
        if (this.miscTypeCd!= null) {
            rtn = (rtn + this.miscTypeCd.hashCode());
        }
        return rtn;
    }

    public String getCatCd() {
        return catCd;
    }

    public void setCatCd(String catCd) {
        this.catCd = catCd;
    }

    public String getMiscTypeCd() {
        return miscTypeCd;
    }

    public void setMiscTypeCd(String miscTypeCd) {
        this.miscTypeCd = miscTypeCd;
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
