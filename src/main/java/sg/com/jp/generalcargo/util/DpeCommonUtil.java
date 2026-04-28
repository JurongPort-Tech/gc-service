package sg.com.jp.generalcargo.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class DpeCommonUtil {
	
	/**
	 * Generate a random number for the last digits of ASN.
	 *
	 * @author HaiVM
	 * @param maxlength Length of random digit
	 * @return a random number
	 */
	public static String generateRandomNumber(int length, boolean event) {
		String result;
		Random rnd = new Random();
		int upperNbr;
		if (length == 3) {
			// Bulk GB EDO & Bulk ESN case
			upperNbr = 1000;
		} else {
			upperNbr = 100000;
		}
		int rndNumber = 0;
		if (event) {
			//For Esn, Tesn
			rndNumber = rnd.nextInt(upperNbr/2);
			rndNumber = rndNumber * 2;
		} else {
			//For EDO
			rndNumber = rnd.nextInt((upperNbr - 1) / 2 - 1 /2);
			rndNumber = rndNumber * 2 + 1;
		}
		int nbrLength = String.valueOf(rndNumber).length();
		switch (nbrLength) {
		case 1:
			result = "0000" + rndNumber;
			break;
		case 2:
			result = "000" + rndNumber;
			break;
		case 3:
			result = "00" + rndNumber;
			break;
		case 4:
			result = "0" + rndNumber;
			break;
		case 5:
			result = "" + rndNumber;
			break;
		default:
			result = "00000";
			break;
		}
		return result.substring(result.length() - length, result.length());
	}

	/**
	 * Add day to the date.
	 *
	 * @param date the date need to add
	 * @param days the number of day
	 * @return the date
	 */
	public static Date addDayToDate(Date date, int days) {
		if (date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, days);
		return cal.getTime();
	}

	/**
     * Format date to string.
     *
     * @param date
     * @param pattern
     * @return String date after formatted
     */
    public static String formatDate(Date date, String pattern) {
    	if (date == null) {
    		return "";
    	}

        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    public static String formatDateTime(Date dt) {
		if (dt == null) {
			return "";
		}
		try {
			SimpleDateFormat fm = new SimpleDateFormat(DpeConstants.DATE_TIME_24_FORMAT);
			return fm.format(dt);
		} catch (Exception e) {
			return "";
		}
    }



//NOt imported
//    /**
//     * This function is used to get product list from a JSON string.
//     * @param jsonStr JSON String
//     * @return job detail list
//     */
//    public static TruckerValueObject[] getTruckerListFromJson(String jsonStr) {
//    	GsonBuilder builder = new GsonBuilder();
//        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
//        	public Date deserialize(JsonElement arg0, Type arg1,
//					JsonDeserializationContext arg2)
//					throws JsonParseException {
//				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//                String date = arg0.getAsJsonPrimitive().getAsString();
//                try {
//					return format.parse(date);
//				} catch (java.text.ParseException e) {
//					return null;
//				}
//			}
//
//			@Override
//			public Date deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
//					JsonDeserializationContext context) throws JsonParseException {
//				// TODO Auto-generated method stub
//				return null;
//			}
//        });
//        Gson gson = builder.create();
//
//        TruckerValueObject[] data = gson.fromJson(jsonStr, TruckerValueObject[].class);
//		return data;
//    }

}
