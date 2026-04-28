package sg.com.jp.generalcargo.restclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import sg.com.jp.generalcargo.domain.SmartInterfaceInputVO;
import sg.com.jp.generalcargo.domain.SmartInterfaceOutputVO;

@FeignClient(name = "smart-client",url = "${smart.rest.client.url}")
public interface SmartServiceRestClient {

	
	@GetMapping(value = "/getJpAppStgLocByRefNbr")
	public List<SmartInterfaceOutputVO> getJpAppStgLocByRefNbr(@RequestParam String planType,@RequestParam String appRefNbr );

	@GetMapping(value = "/checkValidUseOfSpacePlan")
	public boolean checkValidUseOfSpacePlan(@RequestParam String userId,@RequestParam String refNbr,@RequestParam boolean isInactivate);

	@GetMapping(value = "/getLocListBasedOnLocType")
	public List<?> getLocListBasedOnLocType(@RequestParam String locType);

	@GetMapping(value = "/getLocListBasedOnStorageZone")
	public List<?> getLocListBasedOnStorageZone(@RequestParam String stgType, @RequestParam String stgZone);	

	@PostMapping(value = "/releaseStorageOccupancy")
	public void releaseStorageOccupancy(@RequestBody SmartInterfaceInputVO inputObj);

	@PostMapping(value = "/markStorageOccupancy")
	public void markStorageOccupancy(@RequestBody SmartInterfaceInputVO inputObj);
}
