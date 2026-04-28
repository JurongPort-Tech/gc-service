package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UaListObject implements TopsIObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String ua_nbr=null;
	String bill_tonn=null;
	String bill_status=null;
	String ua_nbr_pkgs=null;
	String trans_time=null;
	String ua_status=null;
	//Added by SONLT
	String cntrNo=null;

	String last_modify_user_id;
	String lane_nbr;
	String truck_nbr;

	public String getCntrNo() {
        return cntrNo;
    }

    public void setCntrNo(String cntrNo) {
        this.cntrNo = cntrNo;
    }

    public String getTruck_nbr() {
        return truck_nbr;
    }

    public void setTruck_nbr(String truckNbr) {
        truck_nbr = truckNbr;
    }

    public UaListObject()
	{
	}

	public void setUa_nbr(String s)
	{
		this.ua_nbr = s;
	}

	public String getUa_nbr()
	{
		return ua_nbr ;
	}

	public void setBill_tonn(String s)
	{
		this.bill_tonn = s;
	}

	public String getBill_tonn()
	{
		return bill_tonn ;
	}

	public void setBill_status(String s)
	{
		this.bill_status = s;
	}

	public String getBill_status()
	{
		return bill_status ;
	}

	public void setUa_nbr_pkgs(String s)
	{
		this.ua_nbr_pkgs = s;
	}

	public String getUa_nbr_pkgs()
	{
		return ua_nbr_pkgs ;
	}

	public void setTrans_time(String s)
	{
		this.trans_time = s;
	}

	public String getTrans_time()
	{
		return trans_time ;
	}

	public void setUa_status(String s)
	{
		this.ua_status = s;
	}

	public String getUa_status()
	{
		return ua_status ;
	}
	public void setUa_cntr(String cntrNo)
	{
		this.cntrNo = cntrNo;
	}
	public String getUa_cntr()
	{
		return cntrNo ;
	}
	public String getLast_modify_user_id() {
		return last_modify_user_id;
	}
	public void setLast_modify_user_id(String last_modify_user_id) {
		this.last_modify_user_id = last_modify_user_id;
	}
	public String getLane_nbr() {
		return lane_nbr;
	}
	public void setLane_nbr(String lane_nbr) {
		this.lane_nbr = lane_nbr;
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
