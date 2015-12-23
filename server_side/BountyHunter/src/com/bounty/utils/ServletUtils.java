package com.bounty.utils;

import java.io.PrintWriter;

import javax.servlet.http.Part;

import org.json.JSONException;
import org.json.JSONObject;

public class ServletUtils {
	public static JSONObject composeResponse(int responseCode, String message) {
		JSONObject responseObject = new JSONObject();
		try {
			responseObject.put("responseCode", responseCode);
			responseObject.put("message", message);
		} catch (JSONException e) {}
		return responseObject;
	}
	
	public static void doResponse(String message, PrintWriter writer) {
		writer.write(message);
		writer.flush();
	}
	
	public static String getSubmittedFileName(Part part) {
	    for (String cd : part.getHeader("content-disposition").split(";")) {
	        if (cd.trim().startsWith("filename")) {
	            String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
	            return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1); // MSIE fix.
	        }
	    }
	    return null;
	}

}
