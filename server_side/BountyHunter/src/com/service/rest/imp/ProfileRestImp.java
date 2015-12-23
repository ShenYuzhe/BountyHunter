package com.service.rest.imp;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONException;
import org.json.JSONObject;

import com.bounty.utils.FileSystem;
import com.bounty.utils.taskagent.UserProfile;
import com.bounty.utils.taskagent.UserProfile.Profile;
import com.service.midware.AuthenticationNaive;

public class ProfileRestImp {
	public Response deleteUser(String userId) {
		AuthenticationNaive.getInstance().deleteUser(userId);
		JSONObject resposneObject = new JSONObject();
		try {
			resposneObject.put("responseCode", 200);
		} catch (JSONException e) {}
		return Response.ok().entity(resposneObject.toString()).build();
	}
	
	
	public Response createProfile(String userId, String requestStr) throws JSONException {
		JSONObject responseObject = new JSONObject();
		String userName, iconUrl;
		try {
			JSONObject requestObject = new JSONObject(requestStr);
			userName = requestObject.getString("userName");
			if (!requestObject.has("iconUrl")) {
				String defaultPicture = FileSystem.getInstance().getDefaultPath(userId);
				iconUrl = defaultPicture;
				responseObject.put("iconUrl", defaultPicture);
			} else
				iconUrl = requestObject.getString("iconUrl");
			Profile profile = new Profile();
			profile.setUserName(userName);
			profile.setIconUrl(iconUrl);
			if (requestObject.has("sex"))
				profile.setSex(requestObject.getString("sex"));
			if (requestObject.has("email"))
				profile.setEmail(requestObject.getString("email"));
			if (requestObject.has("birthday"))
				profile.setBirthday(requestObject.getString("birthday"));
			UserProfile.getInstance().createProfile(userId, profile);
		} catch (Exception e) {
			e.printStackTrace();
			responseObject.put("responseCode", Status.BAD_REQUEST.getStatusCode());
			return Response.status(Status.BAD_REQUEST).entity(responseObject.toString()).build();
		}
		responseObject.put("responseCode", Status.OK.getStatusCode());
		return Response.ok().entity(responseObject.toString()).build();
	}
	
	public Response updateProfile(String userId, String requestStr) throws JSONException {
		JSONObject responseObject = new JSONObject();
		try {
			JSONObject requestObject = new JSONObject(requestStr);
			UserProfile.getInstance().updateProfile(userId, requestObject);
		} catch (Exception e) {
			responseObject.put("responseCode", Status.BAD_REQUEST.getStatusCode());
			return Response.status(Status.BAD_REQUEST).entity(responseObject.toString()).build();
		}
		responseObject.put("responseCode", Status.OK.getStatusCode());
		return Response.ok().entity(responseObject.toString()).build();
	}
	
	public Response getProfile(String userId) throws JSONException {
		JSONObject profileObject = null;
		JSONObject responseObject = new JSONObject();
		try {
			profileObject = UserProfile.getInstance().getProfile(userId);
		} catch (JSONException e) {
			responseObject.put("responseCode", Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObject.toString()).build();
		}
		responseObject.put("profile", profileObject);
		return Response.ok().entity(responseObject.toString()).build();
	}
	
	public Response register(String userId, String password) throws JSONException {
		JSONObject responseObject = new JSONObject();
		boolean isValid = AuthenticationNaive.getInstance().register(userId, password);
		if (!isValid) {
			responseObject.put("responseCode", Status.UNAUTHORIZED.getStatusCode());
			return Response.status(Status.UNAUTHORIZED.getStatusCode()).entity(responseObject.toString()).build();
		}
		responseObject.put("responseCode", Status.OK.getStatusCode());
		return Response.ok().entity(responseObject.toString()).build();
	}
	
	public Response login(String userId, String password) throws JSONException {
		JSONObject responseObject = new JSONObject();
		boolean isValid = AuthenticationNaive.getInstance().isValid(userId, password);//AuthenticationMW.getInstance().checkAndSetValidity(userId, accessToken);
		try {
			if (isValid) {
				responseObject.put("responseCode", Status.OK.getStatusCode());
				return Response.ok(responseObject.toString()).build();
			}
			responseObject.put("responseCode", Status.UNAUTHORIZED.getStatusCode());
		} catch (JSONException e) {
			responseObject.put("responseCode", Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseObject.toString()).build();
		}
		return Response.status(Status.UNAUTHORIZED).entity(responseObject.toString()).build();
	}
	
	
	public static void main(String[] args) {
		JSONObject obj = new JSONObject();
	}
}
