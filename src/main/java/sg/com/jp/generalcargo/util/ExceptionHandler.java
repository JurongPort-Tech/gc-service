package sg.com.jp.generalcargo.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import sg.com.jp.generalcargo.domain.Result;
 
@ControllerAdvice
public class ExceptionHandler {
 
    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ResponseEntity<?>  showCustomMessage(Exception e){       
        
    	Result result = new Result();
    	result.setError(ConstantUtil.Error_M4201);
    	result.setSuccess(false);
        return ResponseEntityUtil.badRequest(result);
    }
}