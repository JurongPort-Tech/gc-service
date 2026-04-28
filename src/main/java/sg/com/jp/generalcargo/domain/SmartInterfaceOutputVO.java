package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SmartInterfaceOutputVO implements Serializable
{
    private static final long serialVersionUID = -2073010144960983811L;
    private String stgType;
    private String stgZone;
    private String stgZoneCd;
    private String stgName;
    private String stgDesc;
    private String gridNm;
    private BigDecimal area;
    private String opsStartDttm;
    private String opsEndDttm;
    
    public String getStgType() {
        return this.stgType;
    }
    
    public void setStgType(final String stgType) {
        this.stgType = stgType;
    }
    
    public String getStgZone() {
        return this.stgZone;
    }
    
    public void setStgZone(final String stgZone) {
        this.stgZone = stgZone;
    }
    
    public String getStgDesc() {
        return this.stgDesc;
    }
    
    public void setStgDesc(final String stgDesc) {
        this.stgDesc = stgDesc;
    }
    
    public String getStgName() {
        return this.stgName;
    }
    
    public void setStgName(final String stgName) {
        this.stgName = stgName;
    }
    
    public String getGridNm() {
        return this.gridNm;
    }
    
    public void setGridNm(final String gridNm) {
        this.gridNm = gridNm;
    }
    
    public String getStgZoneCd() {
        return this.stgZoneCd;
    }
    
    public void setStgZoneCd(final String stgZoneCd) {
        this.stgZoneCd = stgZoneCd;
    }
    
    public BigDecimal getArea() {
        return this.area;
    }
    
    public void setArea(final BigDecimal area) {
        this.area = area;
    }
    
    public String getOpsStartDttm() {
        return this.opsStartDttm;
    }
    
    public void setOpsStartDttm(final String opsStartDttm) {
        this.opsStartDttm = opsStartDttm;
    }
    
    public String getOpsEndDttm() {
        return this.opsEndDttm;
    }
    
    public void setOpsEndDttm(final String opsEndDttm) {
        this.opsEndDttm = opsEndDttm;
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