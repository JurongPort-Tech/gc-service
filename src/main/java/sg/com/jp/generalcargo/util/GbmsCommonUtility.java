package sg.com.jp.generalcargo.util;

import java.text.StringCharacterIterator;


public class GbmsCommonUtility
{

    public GbmsCommonUtility()
    {
    }

    public static String addApostr(String s)
    {
        if (s != null && !s.equals(""))
        {
            boolean flag = false;
            String s1 = new String(s);
            StringBuffer stringbuffer;
            for(int i = 0; i < s1.length(); s1 = stringbuffer.toString())
            {
                stringbuffer = new StringBuffer(s1);
                if(s1.charAt(i) == '\'')
                {
                    stringbuffer.insert(i, "'");
                    i += 2;
                } else
                {
                    i++;
                }
            }
            return s1;
        }
        else{
            return "";
        }
    }
	// Added by Satish on Mar 16 2004 : SL-GBMS-20040306-1
	/*
	 *  This method removes the end of line charters and replace with two spaces.
	 */
	public static String removeCarriageReturn(String s)
	{
		if (s != null && !s.equals(""))
		{
			char[] eol = new char[2];
			eol[0] = (char) 0x0d;
			eol[1] = (char) 0x0a;
			StringBuffer sb = new StringBuffer(s);
		    for(int i = 0; i < sb.length(); i++)
			{
				if( sb.charAt(i) == eol[0] || sb.charAt(i) == eol[1])
		        {
					sb.replace(i,i+1," ");
			    }
			}
			return sb.toString();
		}else return "";
	}
	// End added by Satish.


       /**
        *  SL-JCMS-20050516-1 : To display str contains special characters in HTML(&,",' etc) properly as a form value
        *
	* Replace characters having special meaning <em>inside</em> HTML tags
	* with their escaped equivalents, using character entities such as <tt>'&amp;'</tt>.
	*
	* <P>The escaped characters are :
	* <ul>
	* <li> <
	* <li> >
	* <li> "
	* <li> '
	* <li> \
	* <li> &
	* </ul>
	*
	* <P>This method ensures that arbitrary text appearing inside a tag does not "confuse"
	* the tag. For example, <tt>HREF='Blah.do?Page=1&Sort=ASC'</tt>
	* does not comply with strict HTML because of the ampersand, and should be changed to
	* <tt>HREF='Blah.do?Page=1&amp;Sort=ASC'</tt>. This is commonly seen in building
	* query strings. (In JSTL, the c:url tag performs this task automatically.)
	*
	*
	* @Added by Liu Foong on 17/5/05
	*
	*/
	public static String convertForHTMLTag(String aTagFragment){
		final StringBuffer result = new StringBuffer();

		final StringCharacterIterator iterator = new StringCharacterIterator(aTagFragment);
		char character =  iterator.current();
		while (character != StringCharacterIterator.DONE ){
			if (character == '<') {
				result.append("&lt;");
			}
			else if (character == '>') {
				result.append("&gt;");
			}
			else if (character == '\"') {
				result.append("&quot;");
			}
			else if (character == '\'') {
				result.append("&#039;");
			}
			else if (character == '\\') {
			 	result.append("&#092;");
			}
			else if (character == '&') {
			 	result.append("&amp;");
			}
			else {
			//the char is not a special one
			//add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}

		return result.toString();
	}

	/**
     * Verify one cargo is vehicle RORO type
     *
     * @param type
     * @param subTariffCode
     * @return
     */
    public static boolean isVehicleRORO(String type, String subTariffCode) {
    	boolean result = false;

    	if ((ProcessChargeConst.CARGO_CATEGORY_CODE.PASSENGER_CAR.equalsIgnoreCase(type) || ProcessChargeConst.CARGO_CATEGORY_CODE.STATION_WAGON_VAN.equalsIgnoreCase(type)
        		|| ProcessChargeConst.CARGO_CATEGORY_CODE.BUSES_LORRIES.equalsIgnoreCase(type))
        		&& "RO".equalsIgnoreCase(subTariffCode)){
    		result = true;
    	}

    	return result;
    }


}
