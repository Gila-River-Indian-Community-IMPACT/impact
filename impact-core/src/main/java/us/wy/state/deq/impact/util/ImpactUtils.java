package us.wy.state.deq.impact.util;

import org.apache.commons.lang.StringUtils;

public class ImpactUtils {


	public static String toUri(String s) {
		if (!StringUtils.isBlank(s)) {
			if (!s.toLowerCase().startsWith("http:") &&
					!s.toLowerCase().startsWith("https:") &&
					!s.startsWith("/") && 
					!s.startsWith("//")) 
			{
				s = "http://" + s;
			}
		}
		return s;
	}

}
