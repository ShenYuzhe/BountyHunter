package com.service.rest.imp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bounty.recommend.TaskRecommender;
import com.bounty.taskroom.TaskManager;
import com.bounty.utils.GeoUtil;
import com.bounty.utils.taskagent.TaskPool;
import com.bounty.utils.taskagent.UserProfile;

public class TaskRestImp {
	
	private final long timePeriod = 10L*24L*60L*60L*1000L;
	
	private TaskRecommender taskRecommender = new TaskRecommender();
	
	private String timeToStr(long timeStamp) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        return dateFormatter.format(timeStamp);
	}
	
	private String getCurrentTime() {
		return timeToStr((new Date()).getTime());
	}
	
	private String getStartTime() {
		long startTime = (new Date()).getTime() - timePeriod;
		return timeToStr(startTime);
	}
	
	private void markDist(double lat1, double lng1, JSONArray taskList) throws JSONException {
		for (int i = 0; i < taskList.length(); i++) {
			JSONObject task = taskList.getJSONObject(i);
			Double lat2 = task.getDouble("lat"),
					lng2 = task.getDouble("lng");
			double distance = GeoUtil.GetDistance(lat1, lng1, lat2, lng2);
			task.put("distance", distance);
		}
	}
	
	private void addUserProfile(JSONArray taskList) throws JSONException {
		for (int i = 0; i < taskList.length(); i++) {
			JSONObject task = taskList.getJSONObject(i);
			String userId = task.getString("userId");
			JSONObject profile = UserProfile.getInstance().getProfile(userId);
			if (profile != null) {
				String userName = profile.getString("userName");
				String iconUrl = profile.getString("iconUrl");
				task.put("userName", userName);
				task.put("iconUrl", iconUrl);
				if (profile.has("sex"))
					task.put("sex", profile.getString("sex"));
				if (profile.has("email"))
					task.put("email", profile.getString("email"));
				if (profile.has("birthday"))
					task.put("birthday", profile.getString("birthday"));
			}
		}
	}
	
	public Response taskNearby(String lat, String lng, String radius) throws JSONException {
		String startTime = getStartTime(),
				endTime = getCurrentTime();
		JSONObject nearbyRequest;
		JSONArray nearbyResult;
		JSONObject responseObject = new JSONObject();
		try {
			nearbyRequest = new JSONObject();
			double latVal = Double.parseDouble(lat),
					lngVal = Double.parseDouble(lng);
			nearbyRequest.put("lat", latVal);
			nearbyRequest.put("lng", lngVal);
			nearbyRequest.put("radius", Double.parseDouble(radius));
			nearbyResult = TaskPool.getInstance().queryRadius(nearbyRequest, startTime, endTime);
			markDist(latVal, lngVal, nearbyResult);
			addUserProfile(nearbyResult);
			responseObject.put("tasks", nearbyResult);
			responseObject.put("responseCode", Status.OK.getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
			responseObject.put("responseCode", Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseObject.toString()).build();
		}
		return Response.ok().entity(responseObject.toString()).build();
	}
	
	public Response recommendTask(String userId, String latStr, String lngStr, String radiusStr, String taskNum) throws JSONException {
		JSONObject responseObject = new JSONObject();
		try {
			int num = Integer.parseInt(taskNum);
			double lat = Double.parseDouble(latStr);
			double lng = Double.parseDouble(lngStr);
			double radius = Double.parseDouble(radiusStr);
			String startTime = getStartTime(),
					endTime = getCurrentTime();
			JSONArray tasks = taskRecommender.recommend_task(userId, num, lat, lng, radius, startTime, endTime);
			markDist(lat, lng, tasks);
			addUserProfile(tasks);
			responseObject.put("responseCode", Status.OK.getStatusCode());
			responseObject.put("tasks", tasks);
		} catch (NumberFormatException eNum) {
			responseObject.put("responseCode", Status.BAD_REQUEST.getStatusCode());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		} catch (Exception e) {
			e.printStackTrace();
			responseObject.put("responseCode", Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseObject.toString()).build();
		}
		return Response.ok().entity(responseObject.toString()).build();
	}
	
	private String genTaskId(String posterId) {
		return posterId + getCurrentTime();
	}
	
	public Response postTask(String requestStr) throws JSONException {
		JSONObject postRequest;
		JSONObject responseObject = new JSONObject();
		String posterId;
		double lat, lng;
		try {
			postRequest = new JSONObject(requestStr);
			posterId = postRequest.getString("userId");
			lat = postRequest.getDouble("lat");
			lng = postRequest.getDouble("lng");
		} catch (JSONException e) {
			responseObject.put("responseCode", Status.BAD_REQUEST.getStatusCode());
			return Response.status(Status.BAD_REQUEST).entity(responseObject.toString()).build();
		}
		 
		String taskId = genTaskId(posterId);
		postRequest.put("taskId", taskId);
		TaskPool.getInstance().postTask(lat, lng, taskId, postRequest);
		TaskManager.getInstance().postTask(taskId, posterId, postRequest);
		responseObject.put("responseCode", Status.OK.getStatusCode());
		responseObject.put("taskId", taskId);
		return Response.ok().entity(responseObject.toString()).build();
	}
	
	public Response acceptTask(String acceptRequestStr) throws JSONException {
		JSONObject responseObject = new JSONObject();
		JSONObject acceptRequest;
		String taskId, hunterId;
		double price;
		try {
			acceptRequest = new JSONObject(acceptRequestStr);
			taskId = acceptRequest.getString("taskId");
			hunterId = acceptRequest.getString("userId");
			price = acceptRequest.getDouble("price");
		} catch (JSONException e) {
			responseObject.put("responseCode", Status.BAD_REQUEST.getStatusCode());
			return Response.status(Status.BAD_REQUEST).entity(responseObject.toString()).build();
		}
		TaskManager.getInstance().acceptTask(taskId, hunterId, price);//acceptTask(acceptRequest);
		responseObject.put("responseCode", Status.OK.getStatusCode());
		return Response.ok().entity(responseObject.toString()).build();
	}
	
	public Response confirmTask(String confirmRequestStr) throws JSONException {
		JSONObject responseObject = new JSONObject();
		JSONObject confirmRequest;
		String taskId, posterId, hunterId;
		try {
			confirmRequest = new JSONObject(confirmRequestStr);
			taskId = confirmRequest.getString("taskId");
			posterId = confirmRequest.getString("posterId");
			hunterId = confirmRequest.getString("hunterId");
		} catch (JSONException e) {
			responseObject.put("resposneCode", Status.BAD_REQUEST.getStatusCode());
			return Response.status(Status.BAD_REQUEST).entity(responseObject.toString()).build();
		}
		TaskManager.getInstance().confirmTask(posterId, taskId, hunterId);//.confirmTask(confirmRequest);
		responseObject.put("responseCode", Status.OK.getStatusCode());
		return Response.ok().entity(responseObject.toString()).build();
	}
	
	public Response rejectHunter(String rejectRequestStr) throws JSONException {
		JSONObject responseObject = new JSONObject();
		JSONObject confirmRequest;
		String taskId, posterId, hunterId;
		try {
			confirmRequest = new JSONObject(rejectRequestStr);
			taskId = confirmRequest.getString("taskId");
			posterId = confirmRequest.getString("posterId");
			hunterId = confirmRequest.getString("hunterId");
		} catch (JSONException e) {
			responseObject.put("resposneCode", Status.BAD_REQUEST.getStatusCode());
			return Response.status(Status.BAD_REQUEST).entity(responseObject.toString()).build();
		}
		TaskManager.getInstance().rejectHunter(posterId, taskId, hunterId);//.confirmTask(confirmRequest);
		responseObject.put("responseCode", Status.OK.getStatusCode());
		return Response.ok().entity(responseObject.toString()).build();
	}
	
	public Response getHunterList(String posterId) throws JSONException {
		JSONObject responseObject = new JSONObject();
		try {
			JSONArray hunterList = TaskManager.getInstance().getHunterList(posterId);
			responseObject.put("responseCode", Status.OK.getStatusCode());
			responseObject.put("hunters", hunterList);
		} catch (JSONException e) {
			responseObject.put("responseCode", Status.BAD_REQUEST.getStatusCode());
			return Response.status(Status.BAD_REQUEST).entity(responseObject.toString()).build();
		}
		return Response.ok().entity(responseObject.toString()).build();
	}
	
	public Response getTaskStatus(String hunterId) throws JSONException {
		JSONObject responseObject = new JSONObject();
		JSONArray taskRecords = TaskManager.getInstance().getTaskRecord(hunterId);
		responseObject.put("resposneCode", Status.OK.getStatusCode());
		responseObject.put("taskRecords", taskRecords);
		return Response.ok().entity(responseObject.toString()).build();
	}
	
	public Response finishTask(String taskStr) throws JSONException {
		JSONObject responseObject = new JSONObject();
		JSONObject taskObject;
		String taskId, posterId;
		double grade = 1.0;
		try {
			taskObject = new JSONObject(taskStr);
			taskId = taskObject.getString("taskId");
			posterId = taskObject.getString("posterId");
			if (taskObject.has("grade"))
				grade *= taskObject.getDouble("grade");
			JSONObject taskGeo = TaskManager.getInstance().getGeoByTaskId(taskId);
			TaskPool.getInstance().deletePoint(taskId, taskGeo);
		} catch (JSONException e) {
			responseObject.put("responseCode", Status.BAD_REQUEST.getStatusCode());
			return Response.status(Status.BAD_REQUEST).entity(responseObject.toString()).build();
		}
		List<String> tags = TaskManager.getInstance().getTagsByTaskId(taskId);
		TaskManager.getInstance().finishTask(posterId, taskId, grade);
		String hunterId = TaskManager.getInstance().getSelectedHunter(taskId);
		if (tags != null && hunterId != null)
			taskRecommender.update(hunterId, tags, 1);
		responseObject.put("responseCode", 200);
		return Response.ok().entity(responseObject.toString()).build();
	}
	
	public Response cancelTaskPoster(String taskStr) throws JSONException {
		String posterId, taskId;
		JSONObject taskObject, responseObject = new JSONObject();
		try {
			taskObject = new JSONObject(taskStr);
			posterId = taskObject.getString("posterId");
			taskId = taskObject.getString("taskId");
			TaskManager.getInstance().cancelTaskByPoster(posterId, taskId);
		} catch (JSONException e) {
			responseObject.put("responseCode", Status.BAD_REQUEST.getStatusCode());
			return Response.status(Status.BAD_REQUEST.getStatusCode()).entity(responseObject.toString()).build();
		}
		responseObject.put("responseCode", Status.OK.getStatusCode());
		return Response.ok().entity(responseObject.toString()).build();
	}
	
	public Response cancelTaskHunter(String taskStr) throws JSONException {
		String hunterId, taskId;
		JSONObject taskObject, responseObject = new JSONObject();
		try {
			taskObject = new JSONObject(taskStr);
			hunterId = taskObject.getString("hunterId");
			taskId = taskObject.getString("taskId");
			TaskManager.getInstance().cancelTaskByHunter(hunterId, taskId);
		} catch (JSONException e) {
			responseObject.put("responseCode", Status.BAD_REQUEST.getStatusCode());
			return Response.status(Status.BAD_REQUEST.getStatusCode()).entity(responseObject.toString()).build();
		}
		responseObject.put("responseCode", Status.OK.getStatusCode());
		return Response.ok().entity(responseObject.toString()).build();
	}
	
	public Response updatePricePoster(String updateRequest) throws JSONException {
		JSONObject updateObject, responseObject = new JSONObject();
		String posterId, taskId;
		Double price;
		try {
			updateObject = new JSONObject(updateRequest);
			posterId = updateObject.getString("posterId");
			taskId = updateObject.getString("taskId");
			price = updateObject.getDouble("price");
			TaskManager.getInstance().updatePriceByPoster(posterId, taskId, price);
		} catch (JSONException e) {
			responseObject.put("responseCode", Status.BAD_REQUEST.getStatusCode());
			return Response.status(Status.BAD_REQUEST.getStatusCode()).entity(responseObject.toString()).build();
		}
		responseObject.put("responseCode", Status.OK.getStatusCode());
		return Response.ok().entity(responseObject.toString()).build();
	}
	
	public Response updatePriceHunter(String updateRequest) throws JSONException {
		JSONObject updateObject, responseObject = new JSONObject();
		String hunterId, taskId;
		Double price;
		try {
			updateObject = new JSONObject(updateRequest);
			hunterId = updateObject.getString("hunterId");
			taskId = updateObject.getString("taskId");
			price = updateObject.getDouble("price");
			TaskManager.getInstance().updatePriceByHunter(taskId, hunterId, price);
		} catch (JSONException e) {
			responseObject.put("responseCode", Status.BAD_REQUEST.getStatusCode());
			return Response.status(Status.BAD_REQUEST.getStatusCode()).entity(responseObject.toString()).build();
		}
		responseObject.put("responseCode", Status.OK.getStatusCode());
		return Response.ok().entity(responseObject.toString()).build();
	}
	
	public static void main(String[] args) throws JSONException {
		JSONArray array = new JSONArray();
		array.put(new JSONObject());
		array.put(new JSONObject());
		for (int i = 0; i < array.length(); i++)
			array.getJSONObject(i).put("aa", "bb");
		for (int i = 0; i < array.length(); i++)
			System.out.println(array.getJSONObject(i));
	}
}












