package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GbccViewVvStevedore implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GbccViewVvStevedoreId id;
	private String vvCd;
    private String stevCoCd;
    private BigDecimal lineno;
    private String stevContact;
    private String stevRemarks;
    private String stevRep;
    private String lastModifyUserId;
    private String stevedoreCompanyName;
    private String  stev_co_nm;
    public GbccViewVvStevedore() {
    }

    public GbccViewVvStevedore(GbccViewVvStevedoreId id,String stevContact, String stevRemarks, String stevRep, String lastModifyUserId) {
        this.id = id;
        this.stevContact = stevContact;
        this.stevRemarks = stevRemarks;
        this.stevRep = stevRep;
        this.lastModifyUserId = lastModifyUserId;
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

	public String getStev_co_nm() {
		return stev_co_nm;
	}

	public void setStev_co_nm(String stev_co_nm) {
		this.stev_co_nm = stev_co_nm;
	}

	public GbccViewVvStevedoreId getId() {
        return id;
    }

    public void setId(GbccViewVvStevedoreId id) {
        this.id = id;
    }

    public String getStevContact() {
        return stevContact;
    }

    public void setStevContact(String stevContact) {
        this.stevContact = stevContact;
    }

    public String getStevRemarks() {
        return stevRemarks;
    }

    public void setStevRemarks(String stevRemarks) {
        this.stevRemarks = stevRemarks;
    }

    public String getStevRep() {
        return stevRep;
    }

    public void setStevRep(String stevRep) {
        this.stevRep = stevRep;
    }
    
    public String getLastModifyUserId() {
        return lastModifyUserId;
    }

    public void setLastModifyUserId(String lastModifyUserId) {
        this.lastModifyUserId = lastModifyUserId;
    }
    
    public String getStevedoreCompanyName() {
        return stevedoreCompanyName;
    }

    public void setStevedoreCompanyName(String stevedoreCompanyName) {
        this.stevedoreCompanyName = stevedoreCompanyName;
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
