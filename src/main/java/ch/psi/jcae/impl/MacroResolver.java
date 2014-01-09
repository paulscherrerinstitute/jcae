package ch.psi.jcae.impl;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacroResolver {

    private static final String fieldStart = "\\$\\{";
    private static final String fieldEnd = "\\}";

    private static final String regex = fieldStart + "([^}]+)" + fieldEnd;
    private static final Pattern pattern = Pattern.compile(regex);

    public static String format(String format, Map<String, String> macros) {
        Matcher m = pattern.matcher(format);
        String result = format;
        while (m.find()) {
        	String ma = m.group(1);
        	String replacement = macros.get(ma);
        	if(replacement==null){ // use of an non existing macro
        		replacement=fieldStart+ma+fieldEnd;
        	}
            result = result.replaceFirst(fieldStart+ma+fieldEnd, replacement);
        }
        return result;
    }
}