package sg.com.jp.generalcargo.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder=true)
@JsonInclude(Include.NON_NULL)
public class StatusBean {
	private String status;
	private String msg;
	private int code;
	private boolean ok;
	private String payload;

	@Override
	public String toString() {
		String retVal = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			retVal = mapper.writeValueAsString(this);
		} catch (Exception e) {
		}
		return retVal;
	}
}
