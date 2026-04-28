package sg.com.jp.generalcargo.domain;
import java.sql.Timestamp;

public class UserTimestampVO implements TopsIValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String LMU = "last_modify_user_id";
	public static final String LMDT = "last_modify_dttm";

	protected String lastModifyUserId;
	protected Timestamp lastModifyTimestamp;
	
	public UserTimestampVO(){
		this.lastModifyTimestamp = null;
		this.lastModifyUserId = null;
	}

	public void doGet(Object object) {}
	public void doSet(Object object){}
	
	public String getUser(){return lastModifyUserId;}
	public void setUser(String value){lastModifyUserId = value;}
	
	public Timestamp getTimestamp(){return lastModifyTimestamp;}
	public void setTimestamp(Timestamp value){lastModifyTimestamp = value;}
	
	public boolean isSame(UserTimestampVO vo){
		return this.isSame(vo.getUser(), vo.getTimestamp());
	}
	
	public boolean isSame(String user, Timestamp ts){
		if (user != null && user.equals(this.getUser()) &&
			ts != null && ts.equals(this.getTimestamp())){
			return true;
		}else{
			return false;
		}
	}
	
	static public Timestamp getCurrentTimestamp(){
		return new Timestamp(System.currentTimeMillis());
		//return new Timestamp(Calendar.getInstance().getTime().getTime());
	}
}
