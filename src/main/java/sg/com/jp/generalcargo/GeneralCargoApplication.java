package sg.com.jp.generalcargo;

import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;



@SpringBootApplication
@EnableFeignClients
public class GeneralCargoApplication {

	
	private static final Log log = LogFactory.getLog(GeneralCargoApplication.class);

	@PostConstruct
	void started() {
		
		log.info(TimeZone.getDefault());
	}

	
	
	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Singapore"));
		SpringApplication.run(GeneralCargoApplication.class, args);
	}
	
	


}
