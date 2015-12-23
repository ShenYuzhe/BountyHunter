package com.bounty.taskroom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bounty.utils.taskagent.DynamoUtils;
import com.bounty.utils.taskagent.HistoryStatus;
import com.bounty.utils.taskagent.HunterHistory;
import com.bounty.utils.taskagent.HunterHistory.TaskRecord;
import com.bounty.utils.taskagent.PosterHistory;
import com.bounty.utils.taskagent.PosterHistory.Hunter;
import com.bounty.utils.taskagent.TaskStorage;
import com.bounty.utils.taskagent.TaskStorage.Task;
import com.bounty.utils.taskagent.UserProfile;
import com.bounty.utils.taskagent.UserProfile.Profile;

public class TaskManager {
	
	private static TaskManager taskManager = null;
	
	public static TaskManager getInstance() {
		if (taskManager == null)
			taskManager = new TaskManager();
		return taskManager;
	}
	
	private TaskManager() {}
	
	public void postTask(String taskId, String posterId, JSONObject taskContent) {
		TaskStorage.getInstance().insertTask(taskId, taskContent);
		double posterPrice;
		try {
			posterPrice = taskContent.getDouble("price");
			JSONObject posterProfile = UserProfile.getInstance().getProfile(posterId);
			PosterHistory.getInstance().createRecord(taskId, posterId, posterProfile.toString(),
					taskContent.toString(), posterPrice, 0.0, HistoryStatus.POSTED.toString());
		} catch (JSONException e) {}
		
	}
	
	public void acceptTask(String taskId, String hunterId, double hunterPrice) throws JSONException {
		String posterId = DynamoUtils.getPosterIdFromTaskId(taskId);
		Task task = TaskStorage.getInstance().getTaskById(taskId);
		JSONObject taskContent;
		double posterPrice;
		try {
			taskContent = new JSONObject(task.getTaskContent());
			posterPrice = taskContent.getDouble("price");
		} catch (JSONException e) {
			return;
		}
		JSONObject posterProfile = UserProfile.getInstance().getProfile(posterId);
		JSONObject hunterProfile = UserProfile.getInstance().getProfile(hunterId);
		System.out.println(hunterProfile);
		System.out.println(posterProfile);
		PosterHistory.getInstance().createRecord(taskId, hunterId, hunterProfile.toString(),
				task.getTaskContent(), posterPrice, hunterPrice, HistoryStatus.CANDIDATE.toString());
		HunterHistory.getInstance().createRecord(hunterId, taskId, posterProfile.toString(), task.getTaskContent(), posterPrice, hunterPrice);
	}
	
	private void changeHunterStatus(String hunterId, String taskId, String status) {
		Hunter hunter = new Hunter();
		hunter.setStatus(status);
		PosterHistory.getInstance().updateRecord(taskId, hunterId, hunter);
		TaskRecord taskRecord = new TaskRecord();
		taskRecord.setStatus(status);
		HunterHistory.getInstance().updateRecord(taskId, hunterId, taskRecord);
	}
	
	public void confirmTask(String posterId, String taskId, String hunterId) {
		List<Hunter> hunters = PosterHistory.getInstance().getAllHunterbyTaskId(posterId, taskId);
		for (Hunter hunter : hunters) {
			String otherHunterId = hunter.getHunterId();
			String status = HistoryStatus.REJECTED.toString();
			if (otherHunterId.equals(hunterId))
				status = HistoryStatus.SELECTED.toString();
			if (otherHunterId.equals(posterId))
				continue;
			changeHunterStatus(otherHunterId, taskId, status);
		}
		Task task = new Task();
		task.setSelectHunter(hunterId);
		TaskStorage.getInstance().updateTask(taskId, task);
	}
	
	public void rejectHunter(String posterId, String taskId, String hunterId) {
		changeHunterStatus(hunterId, taskId, HistoryStatus.REJECTED.toString());
	}
	
	public void cancelTaskByPoster(String posterId, String taskId) {
		List<Hunter> hunters = PosterHistory.getInstance().getAllHunterById(posterId);
		for (Hunter hunter : hunters) {
			String hunterId = hunter.getHunterId();
			String status = HistoryStatus.CANCELED_POSTER.toString();
			changeHunterStatus(hunterId, taskId, status);
		}
	}
	
	public void cancelTaskByHunter(String hunterId, String taskId) {
		String status = HistoryStatus.CANCELED_HUNTER.toString();
		changeHunterStatus(hunterId, taskId, status);
	}
	
	public void finishTask(String posterId, String taskId, double grade) {
		Task task = TaskStorage.getInstance().getTaskById(taskId);
		String hunterId = task.getSelectHunter();
		String status = HistoryStatus.FINISHED.toString();
		changeHunterStatus(hunterId, taskId, status);
		UserProfile.getInstance().updateGrade(hunterId, grade);
	}
	
	public void changePosterPrice(String hunterId, String taskId, double price) {
		TaskRecord taskRecord = new TaskRecord();
		taskRecord.setPosterPrice(price);
		HunterHistory.getInstance().updateRecord(taskId, hunterId, taskRecord);
		Hunter hunter = new Hunter();
		hunter.setPosterPrice(price);
		PosterHistory.getInstance().updateRecord(taskId, hunterId, hunter);
	}
	
	public void updatePriceByPoster(String posterId, String taskId, double price) {
		List<Hunter> hunters = PosterHistory.getInstance().getAllHunterbyTaskId(posterId, taskId);
		for (Hunter hunter : hunters) {
			String hunterId = hunter.getHunterId();
			TaskRecord taskRecord = new TaskRecord();
			taskRecord.setPosterPrice(price);
			changePosterPrice(hunterId, taskId, price);
		}	
	}
		
	public void updatePriceByHunter(String taskId, String hunterId, double price) {
		Hunter hunter = new Hunter();
		hunter.setHunterPrice(price);
		PosterHistory.getInstance().updateRecord(taskId, hunterId, hunter);
		TaskRecord taskRecord = new TaskRecord();
		taskRecord.setHunterPrice(price);
		HunterHistory.getInstance().updateRecord(taskId, hunterId, taskRecord);
	}
	
	public JSONArray getHunterList(String posterId) {
		List<Hunter> hunterList = PosterHistory.getInstance().getAllHunterById(posterId);
		JSONArray hunterArray = new JSONArray();
		for (Hunter hunter : hunterList) {
			JSONObject hunterObject = new JSONObject();
			try {
				hunterObject.put("taskId", hunter.getTaskId());
				hunterObject.put("taskContent", new JSONObject(hunter.getTaskContent()));
				hunterObject.put("hunterId", hunter.getHunterId());
				hunterObject.put("hunterProfile", UserProfile.getInstance().getProfile(hunter.getHunterId()));//new JSONObject(hunter.getHunterProfile()));
				hunterObject.put("posterPrice", hunter.getPosterPrice());
				hunterObject.put("hunterPrice", hunter.getHunterPrice());
				hunterObject.put("status", hunter.getStatus());
				hunterObject.put("modifiedAt", hunter.getModifiedAt());
				hunterArray.put(hunterObject);
			} catch (JSONException e) {}
		}
		return hunterArray;
	}
	
	public JSONArray getTaskRecord(String hunterId) {
		List<TaskRecord> taskRecordList = HunterHistory.getInstance().getAllTaskRecordById(hunterId);
		JSONArray taskRecordArray = new JSONArray();
		for (TaskRecord taskRecord : taskRecordList) {
			JSONObject taskRecordObject = new JSONObject();
			try {
				taskRecordObject.put("posterId", taskRecord.getPosterId());
				taskRecordObject.put("posterProfile", UserProfile.getInstance().getProfile(taskRecord.getPosterId()));//new JSONObject(taskRecord.getPosterProfile()));
				taskRecordObject.put("taskContent", new JSONObject(taskRecord.getTaskContent()));
				taskRecordObject.put("posterPrice", taskRecord.getPosterPrice());
				taskRecordObject.put("hunterPrice", taskRecord.getHunterPrice());
				taskRecordObject.put("status", taskRecord.getStatus());
				taskRecordObject.put("modifiedAt", taskRecord.getModifiedAt());
				taskRecordArray.put(taskRecordObject);
			} catch (JSONException e) {}
		}
		return taskRecordArray;
	}
	
	public JSONObject getGeoByTaskId(String taskId) {
		JSONObject geo = new JSONObject();
		Task task = TaskStorage.getInstance().getTaskById(taskId);
		try {
			JSONObject taskContent = new JSONObject(task.getTaskContent());
			geo.put("lat", taskContent.getDouble("lat"));
			geo.put("lng", taskContent.getDouble("lng"));
		} catch (JSONException e) {}
		return geo;
	}
	
	public List<String> getTagsByTaskId(String taskId) {
		List<String> tagList = new ArrayList<String>();
		Task task = TaskStorage.getInstance().getTaskById(taskId);
		try {
			JSONObject taskContent = new JSONObject(task.getTaskContent());
			JSONArray tags = taskContent.getJSONArray("tags");
			for (int i = 0; i < tags.length(); i++)
				tagList.add(tags.getString(i));
		} catch (JSONException e) {}
		return tagList;
	}
	
	public String getSelectedHunter(String taskId) {
		Task task = TaskStorage.getInstance().getTaskById(taskId);
		return task.getSelectHunter();
	}
	
	/*public boolean postTask(JSONObject postRequest) {
		try {
			String taskId = postRequest.getString("taskId");
			String postId = postRequest.getString("userId");
			Double price = postRequest.getDouble("price");
			JSONArray tags = postRequest.getJSONArray("tags");
			JSONObject geo = new JSONObject();//postRequest.getJSONObject("geo");
			geo.put("lat", postRequest.getDouble("lat"));
			geo.put("lng", postRequest.getDouble("lng"));
			taskRoomMap.put(taskId, new TaskRoom(postId, price, geo, tags));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public JSONObject getGeoByTaskId(String taskId) {
		TaskRoom taskRoom = taskRoomMap.get(taskId);
		if (taskRoom == null)
			return null;
		return taskRoom.geo;
	}
	
	public List<String> getTagsByTaskId(String taskId) {
		TaskRoom taskRoom = taskRoomMap.get(taskId);
		if (taskRoom == null)
			return null;
		return taskRoom.tags;
	}
	
	public String getSelectUser(String taskId) {
		TaskRoom taskRoom = taskRoomMap.get(taskId);
		if (taskRoom == null)
			return null;
		return taskRoom.selectHunter;
	}
	
	public boolean confirmTask(JSONObject confirmRequest) {
		String taskId, hunterId;
		try {
			taskId = confirmRequest.getString("taskId");
			hunterId = confirmRequest.getString("hunterId");
		} catch (JSONException e) {
			return false;
		}
		TaskRoom taskRoom = taskRoomMap.get(taskId);
		if (taskRoom == null)
			return false;
		taskRoom.selectHunter(hunterId);	
		return true;
	}
	
	public boolean acceptTask(JSONObject acceptRequest) {
		String hunterId, taskId;
		try {
			taskId = acceptRequest.getString("taskId");
			hunterId = acceptRequest.getString("userId");
		} catch (JSONException e) {
			return false;
		}
		TaskRoom taskRoom = taskRoomMap.get(taskId);
		if (taskRoom == null)
			return false;
		taskRoom.updateHunterPrice(hunterId, acceptRequest);
		return true;
	}
	
	public JSONArray getHunterList(String taskId) throws JSONException {
		JSONArray hunterList;
		TaskRoom taskRoom = taskRoomMap.get(taskId);
		hunterList = taskRoom.getHunterList();
		return hunterList;
	}
	
	public String getTaskStatus(String taskId, String hunterId) {
		TaskRoom taskRoom = taskRoomMap.get(taskId);
		if (taskRoom == null)
			return "TASK_NOT_EXIST";
		Status taskStatus = taskRoom.getTaskStatus(hunterId);
		return taskStatus.toString();
	}
	
	public void deletTaskRoom(String taskId) {
		if (taskRoomMap.containsKey(taskId))
			taskRoomMap.remove(taskId);
	}*/
	
	public static void main(String[] args) {
		HashMap<String, String> test = new HashMap<String, String>();
		String val = test.get("nnn");
		System.out.println(val);
	}
}
