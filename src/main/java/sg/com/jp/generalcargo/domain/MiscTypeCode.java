package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MiscTypeCode implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MiscTypeCodeId id;
    private String catCd;
    private String miscTypeCd;
    private String miscTypeNm;
    private String recStatus;
    private String lastModifyUserId;
    private Date lastModifyDttm;
   
    
    
    public MiscTypeCode() {
    }

    public MiscTypeCode(String miscTypeNm, String recStatus, String lastModifyUserId, Date lastModifyDttm) {
        this.miscTypeNm = miscTypeNm;
        this.recStatus = recStatus;
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
    }

    public MiscTypeCode(MiscTypeCodeId id, String miscTypeNm, String recStatus, String lastModifyUserId, Date lastModifyDttm) {
        this.id = id;
        this.miscTypeNm = miscTypeNm;
        this.recStatus = recStatus;
        this.lastModifyUserId = lastModifyUserId;
        this.lastModifyDttm = lastModifyDttm;
    }
    
    public MiscTypeCode(String miscTypeCd, String miscTypeNm) {
    	this.id = new MiscTypeCodeId(null, miscTypeCd);
    	this.miscTypeNm = miscTypeNm;
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

	public MiscTypeCodeId getId() {
        return id;
    }

    public void setId(MiscTypeCodeId id) {
        this.id = id;
    }

    public String getMiscTypeNm() {
        return miscTypeNm;
    }

    public void setMiscTypeNm(String miscTypeNm) {
        this.miscTypeNm = miscTypeNm;
    }

    public String getRecStatus() {
        return recStatus;
    }

    public void setRecStatus(String recStatus) {
        this.recStatus = recStatus;
    }

    public String getLastModifyUserId() {
        return lastModifyUserId;
    }

    public void setLastModifyUserId(String lastModifyUserId) {
        this.lastModifyUserId = lastModifyUserId;
    }

    public Date getLastModifyDttm() {
        return lastModifyDttm;
    }

    public void setLastModifyDttm(Date lastModifyDttm) {
        this.lastModifyDttm = lastModifyDttm;
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
