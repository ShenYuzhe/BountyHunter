package com.bounty.recommend;

import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bounty.utils.taskagent.TaskPool;
import com.bounty.utils.taskagent.UserStatistic;
import com.bounty.utils.taskagent.UserStatistic.Statistic;

public class TaskRecommender {
	private final FastByIDMap<PreferenceArray> preferences = 
			  new FastByIDMap<PreferenceArray>();
	int max_num_of_tag = 8;
	
	public TaskRecommender() {
		initialize();
	}
	
	private void initialize(){
		List<Statistic> results = UserStatistic.getInstance().getAllUserStat();
		for (Statistic record : results){
			List<String> tag_list = new ArrayList<String>();
			tag_list.add(record.getTag());
			update_local(record.getUserId(), tag_list, record.getCount());
		}
	}
	
	private void update_local(String userid, List<String> itemid_list, int value){
		long userid_long = String2hash(userid);
		List<String> list = new ArrayList<String>();
		if(!preferences.containsKey(userid_long)){
			PreferenceArray new_preference = new GenericUserPreferenceArray(max_num_of_tag);
			new_preference.setUserID(0, userid_long);
			preferences.put(userid_long, new_preference);
		}
		PreferenceArray new_preference = preferences.get(userid_long);
		for (int i=0; i<itemid_list.size(); i++){
			int itemid_num = item2int(itemid_list.get(i));
			if(!new_preference.hasPrefWithItemID(itemid_num)){
				new_preference.setItemID((int)itemid_num, itemid_num);
			}
			new_preference.setValue((int) itemid_num, new_preference.getValue((int)itemid_num)+value);
		}
		preferences.remove(userid_long);
		preferences.put(userid_long, new_preference);
	}
	
	private void update_db(String userid, List<String> itemid_list, int value){
		UserStatistic.getInstance().updateStat(userid, itemid_list);
	}
	
	
	public void update(String userid, List<String> itemid_list, int value){
		update_local(userid, itemid_list, value);
		update_db(userid, itemid_list, value);
	}
	
	private int item2int(String itemid){
		if(itemid.equals("coffee")) return 1;
		if(itemid.equals("charge")) return 2;
		if(itemid.equals("borrow")) return 3;
		if(itemid.equals("book")) return 4;
		if(itemid.equals("laptop")) return 5;
		if(itemid.equals("teach")) return 6;
		if(itemid.equals("yelp")) return 7;
		return 0;
	}
	
	private long String2hash(String userid){
		long hash_value = 0;
		for (int i=0; i<userid.length(); i++){
			hash_value*=100;
			hash_value += (int)userid.charAt(i)-(int)'a'+1;
		}
		return hash_value;
	}
	
	private void print_preference(){
		System.out.println(preferences.toString());
	}
	
	public List<RecommendedItem> recommend_tag(String userid, int num) throws TasteException{
		DataModel model = new GenericDataModel(preferences);
		
		ItemSimilarity item_similarity = new EuclideanDistanceSimilarity(model);
    	ItemBasedRecommender recommender = new GenericItemBasedRecommender(model, item_similarity);
        
    	List<RecommendedItem> recommendations = recommender.recommend(String2hash(userid), num);
        for (RecommendedItem recommendation:recommendations){
        	System.out.println(recommendation);
        }
        return recommendations;
	}
	
	
	private JSONArray queryRadius(double lat, double lng, double radius, String startTime, String endTime) throws JSONException{
		JSONObject requestObject = new JSONObject();  
		requestObject.put("lat", lat);  
		requestObject.put("lng", lng);  
		requestObject.put("radius", radius); 	
		JSONArray tasksNearby = TaskPool.getInstance().queryRadius(requestObject, startTime, endTime);
		return tasksNearby;
	}
	
	public JSONArray recommend_task(String userid, int num, double lat, double lng, double radius,
			String startTime, String endTime) throws JSONException, TasteException {
		
		if(!preferences.containsKey(String2hash(userid)))
			return new JSONArray();
		
		List<RecommendedItem> recommendations = recommend_tag(userid, 10);
		
		
		JSONArray tasksNearby = queryRadius(lat, lng, radius, startTime, endTime);
		if(tasksNearby.length()<=num) return tasksNearby;
		
		JSONArray result_tasks = new JSONArray();
		JSONArray back_tasks = new JSONArray();
		
		for (int i = 0; i < tasksNearby.length(); i++) {
			JSONObject task = tasksNearby.getJSONObject(i);
			JSONArray tags = task.getJSONArray("tags");
			
			for(int j=0; j< tags.length(); j++){
				int tagid_int = item2int(tags.getString(j));
				boolean needBreak = false;
				for (int k=0; k < recommendations.size(); k++){
					System.out.println(recommendations.get(k).getItemID());
					if(tagid_int==recommendations.get(k).getItemID()){
						result_tasks.put(task);
						if (result_tasks.length()>=num) return result_tasks;
						else needBreak = true;
						break;
					}
				}
				if (needBreak)
					break;
				else back_tasks.put(task);
			}
		}
		if(result_tasks.length()<num){
			for (int i=0; i<num-result_tasks.length(); i++){
				result_tasks.put(back_tasks.getJSONObject(i));
			}
			return result_tasks;
		}
		return result_tasks;
	}
	
	public static void main(String[] args) throws TasteException, JSONException{
		TaskRecommender recommendation = new TaskRecommender();
		
		System.out.println("Original preference");
		
		recommendation.initialize();
		recommendation.print_preference();
		
		/*
		System.out.println("update1");
		List<String> itemid_list = new ArrayList<String>();
		itemid_list.add("coffee");
		itemid_list.add("charge");
		recommendation.update("hi", itemid_list,1);
		recommendation.print_preference(); 
		
		System.out.println("update2");
		List<String> itemid_list2 = new ArrayList<String>();
		itemid_list2.add("coffee");
		recommendation.update("90", itemid_list2, 1);
		recommendation.print_preference();
		
		System.out.println("update3");
		List<String> itemid_list3 = new ArrayList<String>();
		recommendation.update("100", itemid_list3, 1);
		recommendation.print_preference(); */
		
		//recommendation.recommend_tag("10", 10);
		JSONArray tasks = recommendation.recommend_task("10", 10, 50, 50, 20000, "2015-12-18 18:00:45", "2015-12-18 19:45:45");
		for (int i = 0; i < tasks.length(); i++)
			System.out.println(tasks.getJSONObject(i));
		//for(JSONObject task:tasks){
		//	System.out.println(task);
		//}
	}
	
}
