package com.bounty.utils;

import org.json.JSONObject;
import org.json.JSONException;

public class facebookRest {
	static String appId = "";
	static String appSecret = "";
	
	static public String getAppToken() {
		String response = RequestUtil.sendGet("https://graph.facebook.com/oauth/access_token",
				"client_id=" + appId + "&client_secret=" + appSecret + "&grant_type=client_credentials",
				new RequestHeader());
		String accessToken = null;
		try {
			String[] tokens = response.split("=");
			accessToken = tokens[1];
		} catch (Exception e) {
			return null;
		}
		return accessToken;
	}
	
	static public boolean verifyToken(String userId, String accessToken) {
		String appToken = getAppToken();
		String strResponse = RequestUtil.sendGet("https://graph.facebook.com/debug_token",
				"input_token=" + accessToken + "&access_token=" + appToken,
				new RequestHeader());
		try {
			JSONObject jsonResponse = new JSONObject(strResponse);
			JSONObject jsonData = jsonResponse.getJSONObject("data");
			Boolean isValid = jsonData.getBoolean("is_valid");
			String actualUserId = jsonData.getString("user_id");
			return isValid && (userId.equals(actualUserId));
		} catch (JSONException e) {
			return false;
		}
	}
	
	static public void main(String[] args) {
		System.out.println(verifyToken("", ""));
	}
}
