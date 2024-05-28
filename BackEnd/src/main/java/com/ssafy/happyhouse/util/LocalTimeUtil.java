package com.ssafy.happyhouse.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeUtil {
	
	public static String getDateTime() {
		LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = now.format(format);
        return date;
	}
}
