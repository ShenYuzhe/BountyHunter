package com.bounty.utils.taskagent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.geo.GeoDataManager;
import com.amazonaws.geo.GeoDataManagerConfiguration;
import com.amazonaws.geo.model.DeletePointRequest;
import com.amazonaws.geo.model.DeletePointResult;
import com.amazonaws.geo.model.GeoPoint;
import com.amazonaws.geo.model.PutPointRequest;
import com.amazonaws.geo.model.QueryRadiusRequest;
import com.amazonaws.geo.model.QueryRadiusResult;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.bounty.utils.CredentialManager;

public class TaskPool {
	private GeoDataManagerConfiguration config;
	private GeoDataManager geoDataManager;
	
	private ObjectMapper mapper;
	private JsonFactory factory;
	
	private String tableName = "task_pool";
	
	private static TaskPool dynamoManager = null;
	
	public static TaskPool getInstance() {
		if (dynamoManager == null)
			dynamoManager = new TaskPool();
		return dynamoManager;
	}
	
	private TaskPool() {
		setupGeoDataManager();
		
		mapper = new ObjectMapper();
		factory = mapper.getJsonFactory();
	}
	
	private void setupGeoDataManager() {
		AWSCredentials credentials = CredentialManager.getInstance().getCredential();
		AmazonDynamoDBClient ddb = new AmazonDynamoDBClient(credentials);
		Region region = Region.getRegion(Regions.US_EAST_1);
		ddb.setRegion(region);
		
		config = new GeoDataManagerConfiguration(ddb, tableName);
		geoDataManager = new GeoDataManager(config);
	}
	
	private Date str2TimeStamp(String timeStamp) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = formatter.parse(timeStamp);
		} catch (ParseException e) {
			return null;
		}
		return date;
	}
	
	private String timeToStr(long timeStamp) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        return dateFormatter.format(timeStamp);
	}
	
	private String getCurrentTime() {
		return timeToStr((new Date()).getTime());
	}
	
	public boolean isTimeWithRange(String startTimeStr, String endTimeStr, String timeStampStr) {
		Date startTime = str2TimeStamp(startTimeStr);
		Date endTime = str2TimeStamp(endTimeStr);
		Date timeStamp = str2TimeStamp(timeStampStr);
		return timeStamp.before(endTime) && timeStamp.after(startTime);
	}
	
	public JSONArray queryRadius(JSONObject requestObject,
			String startTime, String endTime) {
		JSONArray records = new JSONArray();
		try {
			double lat = requestObject.getDouble("lat");
			double lng = requestObject.getDouble("lng");
			double radius = requestObject.getDouble("radius");
			GeoPoint center = new GeoPoint(lat, lng);
			
			QueryRadiusRequest queryRadiusRequest = new QueryRadiusRequest(center, radius);
			QueryRadiusResult queryRadiusResult = geoDataManager.queryRadius(queryRadiusRequest);
			
			for (Map<String, AttributeValue> item : queryRadiusResult.getItem()) {
				String postTime = item.get("postAt").getS();
				if (isTimeWithRange(startTime, endTime, postTime))
					records.put(new JSONObject(item.get("taskContentJSON").getS()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return records;
		}
		return records;
	}
	
	public void deletePoint(String taskId, JSONObject geo) throws JSONException {
		double lat = geo.getDouble("lat");
		double lng = geo.getDouble("lng");
		GeoPoint geoPoint = new GeoPoint(lat, lng);
		AttributeValue rangeKeyAttributeValue = new AttributeValue().withS(taskId);
		
		DeletePointRequest deletePointRequest = new DeletePointRequest(geoPoint, rangeKeyAttributeValue);
		DeletePointResult deletePointResult = geoDataManager.deletePoint(deletePointRequest);
	}
	
	public void postTask(double lat, double lng, String taskId,
			JSONObject taskRequest) throws JSONException {
		String postAt = getCurrentTime();
		
		GeoPoint geoPoint = new GeoPoint(lat, lng);
		AttributeValue taskIdAttr = new AttributeValue().withS(taskId);
		AttributeValue postAtAttr = new AttributeValue().withS(postAt);
		AttributeValue taskAttr = new AttributeValue().withS(taskRequest.toString());
		
		PutPointRequest putPointRequest = new PutPointRequest(geoPoint, taskIdAttr);
		putPointRequest.getPutItemRequest().getItem().put("postAt", postAtAttr);
		putPointRequest.getPutItemRequest().getItem().put("taskContentJSON", taskAttr);
		
		geoDataManager.putPoint(putPointRequest);
	}
	
	public static void main(String[] args) throws JSONException {
		
		/*String tags[] = {"coffee", "charge", "borrow", "book", "laptop", "teach", "yelp"};
		int min = 0, max = 6;
		Random random = new Random();
		JSONObject taskRequest = new JSONObject();
		taskRequest.put("lat", 16);
		taskRequest.put("lng", 16);
		for (int i = 0; i < 100; i++) {
			int tagIdx = random.nextInt(max) % (max - min) + min;
			int price = random.nextInt(100) % 90 + 10;
			JSONArray tagArray = new JSONArray();
			tagArray.put(tags[tagIdx]);
			JSONObject taskContent = new JSONObject();
			taskContent.put("tags", tagArray);
			taskContent.put("userId", "3");
			taskContent.put("price", price);
			taskContent.put("content", "test test");
			taskContent.put("taskId", "" + i);
			JSONObject geo = new JSONObject();
			geo.put("lat", 16);
			geo.put("lng", 16);
			taskContent.put("geo", geo);
			taskRequest.put("taskConent", taskContent);
			taskRequest.put("taskId", "" + i);
			TaskPool.getInstance().postTask(taskRequest);
		}*/
	}
}