package org.example.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
	public static boolean matchesFormat(String inputData, String formatPattern) {
        Pattern pattern = Pattern.compile(formatPattern);
        Matcher matcher = pattern.matcher(inputData);
        return matcher.matches();
    }
}