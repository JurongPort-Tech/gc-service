package sg.com.jp.generalcargo.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import sg.com.jp.generalcargo.domain.Page;
import sg.com.jp.generalcargo.domain.Result;

/*
 * Revision History
 * ------------------------------------------------------------------------------------------------------
 * Author			Description												Version			Date
 * ------------------------------------------------------------------------------------------------------
 * MC Consulting	First Version											1.0				27-Mar-2019
 */

public class ResponseEntityUtil {

	public static ResponseEntity<?> created(UriComponentsBuilder ucb, String url, String id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucb.path(url + "/{id}").buildAndExpand(id).toUri());
		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	public static ResponseEntity<?> databaseError(String operation, String tableName) {
		String message = "Unable to " + operation + " in " + tableName + " table, please contact System Administrator.";
		return new ResponseEntity<Error>(new Error(message), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public static ResponseEntity<?> duplicate(String message) {
		return new ResponseEntity<Error>(new Error(message), HttpStatus.CONFLICT);
	}

	public static ResponseEntity<?> recordNotFound() {
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	public static ResponseEntity<?> success() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

	public static ResponseEntity<?> success(Page page) {
		return new ResponseEntity<Page>(page, HttpStatus.OK);
	}

	public static ResponseEntity<?> success(Object object) {
		Result result = checkError(object);
		boolean success = result.getSuccess();
		if (!success) {
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(object, HttpStatus.OK);
		}
	}

	public static ResponseEntity<?> ok(Object object) {
		Result result = checkError(object);
		boolean success = result.getSuccess();
		if (!success) {
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(object, HttpStatus.OK);
		}
	}

	public static ResponseEntity<?> oK(Object object, Integer integer) {
		Result result = checkError(object);
		boolean success = result.getSuccess();
		if (!success) {
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(object, HttpStatus.OK);
		}
	}

	public static ResponseEntity<?> updated() {
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	public static ResponseEntity<?> validationError(List<Map<String, String>> errors) {
		return new ResponseEntity<Error>(new Error(), HttpStatus.BAD_REQUEST);
	}

	public static ResponseEntity<?> validationError(String message) {
		return new ResponseEntity<Error>(new Error(message), HttpStatus.BAD_REQUEST);
	}

	public static ResponseEntity<?> badRequest(Object object) {
		return new ResponseEntity<>(object, HttpStatus.BAD_REQUEST);
	}

	public static boolean containsWords(String inputString, String[] items) {
		boolean found = false;
		for (String item : items) {
			if (inputString.toLowerCase().contains(item.toLowerCase())) {
				found = true;
				break;
			}
		}
		return found;
	}

	private static HashMap<String, Object> objToMap(Object object) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();

		Object listObject = object;
		if (listObject instanceof HashMap) {
			for (Object i : ((Map<?, ?>) listObject).keySet()) {
				hashMap.put((String) i, ((Map<?, ?>) listObject).get(i));
			}
		}

		return hashMap;
	}

	public static Result checkError(Object object) {

		Result result = new Result();
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject obj = new JSONObject();

		if (object instanceof HashMap) {
			HashMap<String, Object> text = objToMap(object);
			obj = new JSONObject(text);
		} else if (object instanceof List<?>) {
			// already in jsonArray
		} else {
			String text = object.toString();
			obj = new JSONObject(text);
		}

		if (obj.toMap().get("data") != null) {
			JSONObject dataobj = new JSONObject();
			JSONArray dataArr = new JSONArray();
			String dataStr = new String();
			if (obj.toMap().get("data") instanceof String) {
				dataStr = obj.toMap().get("data").toString();
				dataobj.put("data", dataStr);
				result.setData(dataobj.toMap());
			} else {
				try {
					dataobj = obj.getJSONObject("data");
					result.setData(dataobj.toMap());
				} catch (Exception e) {
					dataArr = obj.getJSONArray("data");
					dataobj.put("data", dataArr);
					result.setData(dataobj.toMap());
				}
			}

		}

		// if success false
		if (obj.toMap().get("success") != null && obj.get("success").equals(false)) {
			result.setSuccess(false);
			String errorMessage = ConstantUtil.Error_M4201; // default errorMessage
			String[] words = { "java.", "Exception", "PreparedStatementCallback", "JSON" }; // if contains this, return
																							// default error

			// if has errors
			if (obj.has("errors")) {
				JSONObject errors = (JSONObject) obj.get("errors");
				if (containsWords(errors.get("errorMessage").toString(), words)) {
					map.put("errorMessage", errorMessage);
				} else {
					map.put("errorMessage", errors.get("errorMessage"));
				}
				result.setErrors(map);

			} else {
				if (containsWords(obj.get("error").toString(), words)) {
					result.setError(errorMessage);
				} else {
					result.setError(obj.get("error").toString());
				}

			}
		} else {
			result.setSuccess(true);
		}

		return result;

	}

}
