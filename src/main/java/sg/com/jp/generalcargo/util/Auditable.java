package sg.com.jp.generalcargo.util;

public interface Auditable {

	public boolean isFieldAuditable(String fieldname);
	public String formatFieldValue(String fieldname, String fieldvalue);

}
