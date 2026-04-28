package sg.com.jp.generalcargo.domain;

public class Option {

	
	public Option() {
		super();
	}
	public Option(String value) {
		super();
		this.value = value;
		this.label = value;
	}
	private String value;
	private String label;
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	
}
