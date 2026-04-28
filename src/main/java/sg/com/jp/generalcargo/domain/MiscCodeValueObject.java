package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//package: valueObject.codes.MiscCode;


/**
 * MiscCodeValueObject.java
 *
 * @author Low Mui Yong
 * @date December 3, 2001
 * Modifications:
 *      Date(By)                       Changes
 *
 */

public class MiscCodeValueObject implements TopsIObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Attributes of the Miscellaneous Type Code table
    private String misc_cat_cd;          // Misc Cat Code
    private String misc_type_cd;         // Misc Type Code
    private String misc_type_nm;         // Misc Type Name
    private String misc_status;          // Record Status
    private String last_modify_user_id;  // Last Modified By
    private String last_modify_dttm;     // Last Modified Date

    /** Creates new MiscCodeValueObject */
    public MiscCodeValueObject()
    {
//      System.out.println("MiscCodeValueObject");
    }

     /**
     * Set the Misc Cat Code field
     *
     * @param   String     Misc Code
     *
     */
    public void setCatCode(String catCode) {
        misc_cat_cd = catCode;
    }

    /**
     * Returns the Misc Cat Code value
     */
    public String getCatCode() {
        return misc_cat_cd;
    }

    /**
     * Set the Misc Type Code field
     *
     * @param   String     Misc Code
     *
     */
    public void setTypeCode(String typeCode) {
        misc_type_cd = typeCode;
    }

    /**
     * Returns the Misc Type Code value
     */
    public String getTypeCode() {
        return misc_type_cd;
    }

    /**
     * Set the Misc Type Name field
     *
     * @param   String     Misc Type Name
     *
     */
    public void setTypeName(String name) {
        misc_type_nm = name;
    }

    /**
     * Returns the Misc Type Name value
     */
    public String getTypeName() {
        return misc_type_nm;
    }

    /**
     * Set the Status field
     *
     * @param   String     Status
     *
     */
    public void setStatus(String status) {
        misc_status = status;
    }

    /**
     * Returns the Status value
     */
    public String getStatus() {
        return misc_status;
    }

    /**
     * Set the Last Modified By field
     *
     * @param   String     Last Modified By
     *
     */
    public void setLastModifiedBy(String lastModifiedBy) {
        last_modify_user_id = lastModifiedBy;
    }

    /**
     * Returns the Last Modified By value
     */
    public String getLastModifiedBy() {
        return last_modify_user_id;
    }

    /**
     * Set the Last Modified Date field
     *
     * @param   String     Last Modified Date
     *
     */
    public void setLastModifiedDate(String lastModifiedDate) {
        last_modify_dttm = lastModifiedDate;
    }

    /**
     * Returns the Last Modified Date value
     */
    public String getLastModifiedDate() {
        return last_modify_dttm;
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

