package com.bounty.utils.taskagent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DynamoUtils {
	private static String dateFormatStr = "yyyy-mm-dd hh:mm:ss";;
	
	static public String getSearchKey(String taskId, String userId) {
		return taskId + ";" + userId;
	}
	
	static public String getPosterIdFromTaskId(String taskId) {
		return taskId.substring(0, taskId.length() - dateFormatStr.length());
	}
	
	private static String timeToStr(long timeStamp) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        return dateFormatter.format(timeStamp);
	}
	
	public static String getCurrentTime() {
		return timeToStr((new Date()).getTime());
	}
}
