package com.bounty.taskroom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bounty.utils.taskagent.UserProfile;

public class TaskRoom {
	String postId;
	double price;
	String selectHunter = null;
	JSONObject geo;
	List<String> tags = new ArrayList<String>();
	
	public static enum Status {
		NOT_CONFIRMED, CONFIRMED, SUCCESS 
	};
	
	private HashMap<String, JSONObject> taskHunters = new HashMap<String, JSONObject>();
	
	public TaskRoom(String postId, double price, JSONObject geo, JSONArray jsonTags) throws JSONException {
		this.postId = postId;
		this.price = price;
		this.geo = geo;
		for (int i = 0; i < jsonTags.length(); i++)
			tags.add(jsonTags.getString(i));
	}
	
	public void updateHunterPrice(String hunterId, JSONObject requestObject) {
		taskHunters.put(hunterId, requestObject);
	}
	
	public void selectHunter (String hunterId) {
		selectHunter = hunterId;
	}
	
	public JSONArray getHunterList() throws JSONException {
		JSONArray hunterList = new JSONArray();
		for (Entry<String, JSONObject> hunter : taskHunters.entrySet()) {
			String hunterId = hunter.getValue().getString("userId");
			Double price = hunter.getValue().getDouble("price");
			JSONObject profile = UserProfile.getInstance().getProfile(hunterId);
			JSONObject hunterDetail = new JSONObject();
			hunterDetail.put("userId", hunterId);
			hunterDetail.put("userName", profile.getString("userName"));
			hunterDetail.put("iconName", profile.getString("iconUrl"));
			hunterDetail.put("price", price);
			hunterList.put(hunterDetail);
		}
		return hunterList;
			
	}
	
	public Status getTaskStatus(String hunterId) {
		if (selectHunter == null)
			return Status.NOT_CONFIRMED;
		if (hunterId.equals(selectHunter))
			return Status.SUCCESS;
		return Status.CONFIRMED;
	}
	
	public static void main(String[] args) {
		System.out.println(TaskRoom.Status.CONFIRMED);
	}
}
