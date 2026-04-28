/**
 * System Name:	GBMS (General Bulk Cargo Management System)
 * Component ID:DocSubAuthorValueObject.java
 * Component Description: This is the ValueObject class for Document Submission Authorization
 *
 * @author      Balaji R.k.  
 * @version     12 June 2002
 */

/*
 * Revision History
 * ----------------
 * Author   Request Number  Description of Change   Version     Date Released
 * Balaji R.k.                Creation				1.0         12 June 2002
 */

package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DocSubAuthorValueObject implements Serializable
     
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DocSubAuthorValueObject()
    {
    }

    public void setVslName(String s)
    {
        vsl_name = s;
    }

    public String getVslName()
    {
        return vsl_name;
    }

    public void setVvCd(String s)
    {
        vv_cd = s;
    }

    public String getVvCd()
    {
        return vv_cd;
    }

    public void setInVoyNbr(String s)
    {
        in_voy_nbr = s;
    }

    public String getInVoyNbr()
    {
        return in_voy_nbr;
    }

    public void setOutVoyNbr(String s)
    {
        out_voy_nbr = s;
    }

    public String getOutVoyNbr()
    {
        return out_voy_nbr;
    }

    public void setEtuDttm(String s)
    {
        etu_dttm = s;
    }

    public String getEtuDttm()
    {
        return etu_dttm;
    }

    public void setBtrDttm(String s)
    {
        btr_dttm = s;
    }

    public String getBtrDttm()
    {
        return btr_dttm;
    }

    public void setAgtNm(String s)
    {
        agt_nm = s;
    }

    public String getAgtNm()
    {
        return agt_nm;
    }

    public void setAuthorNm(String s)
    {
        author_nm = s;
    }

    public String getAuthorNm()
    {
        return author_nm;
    }

    public void setAuthorNbr(String s)
    {
        author_nbr = s;
    }

    public String getAuthorNbr()
    {
        return author_nbr;
    }

    public void setDocSubAuthor(String s)
    {
        doc_sub_author = s;
    }

    public String getDocSubAuthor()
    {
        return doc_sub_author;
    }

    public void setDocSubAuthorNbr(String s)
    {
        doc_sub_author_nbr = s;
    }

    public String getDocSubAuthorNbr()
    {
        return doc_sub_author_nbr;
    }

    public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getSubScheme() {
		return subScheme;
	}

	public void setSubScheme(String subScheme) {
		this.subScheme = subScheme;
	}

	public String getGcOperations() {
		return gcOperations;
	}

	public void setGcOperations(String gcOperations) {
		this.gcOperations = gcOperations;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAgentFullName() {
		return agentFullName;
	}

	public void setAgentFullName(String agentFullName) {
		this.agentFullName = agentFullName;
	}

	public String getCreateDttm() {
		return createDttm;
	}

	public void setCreateDttm(String createDttm) {
		this.createDttm = createDttm;
	}

	private String vv_cd;
    private String vsl_name;
    private String in_voy_nbr;
    private String out_voy_nbr;
    private String etu_dttm;
    private String btr_dttm;
    private String agt_nm;
    private String author_nm;
    private String author_nbr;
    private String doc_sub_author;
    private String doc_sub_author_nbr;
    private String terminal;
    private String scheme;
    private String subScheme;
    private String gcOperations;
    private String status;
    private String userId;
    private String createDttm;
    private String agentFullName;
    
    @Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}
}
