package com.bounty.utils.taskagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.bounty.utils.CredentialManager;

public class UserStatistic {
	
	private static UserStatistic userStat;
	
	private String tableName = "user_stat";
	
	private DynamoDB dynamoDB;
	
	private AmazonDynamoDBClient client;
	
	private Table table;
	
	private UserStatistic() {
		AWSCredentials credentials = CredentialManager.getInstance().getCredential();
		dynamoDB = new DynamoDB((AmazonDynamoDB) new AmazonDynamoDBClient(credentials));
		client = new AmazonDynamoDBClient(credentials);
		table = dynamoDB.getTable(tableName);
	}
	
	public static UserStatistic getInstance() {
		if (userStat == null)
			userStat = new UserStatistic();
		return userStat;
	}
	
	public void updateStat(String userId, List<String> tags) {
		Map<String, String> expressionAttributeNames = new HashMap<String, String>();
		for (String tag : tags) {
			String atomicCondition = "ADD #count :val";   
			expressionAttributeNames.put("#count", "count");
			Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
			expressionAttributeValues.put(":val", 1);
			UpdateItemOutcome outcome = table.updateItem(
				"userId", userId,
				"tag", tag,
				atomicCondition,
				expressionAttributeNames,
				expressionAttributeValues);
		}
	}
	
	public List<Statistic> getAllUserStat() {
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		List<Statistic> scanResult = getSimpleMapper().scan(Statistic.class, scanExpression);
		return scanResult;
	}
	
	private DynamoDBMapper getSimpleMapper() {
		return new DynamoDBMapper(client);
	}
	
	public boolean isUserRecordExist(String userId) {
		Statistic userIdKey = new Statistic();
		userIdKey.setUserId(userId);
		DynamoDBQueryExpression<Statistic> queryExpression = new DynamoDBQueryExpression<Statistic>()
				.withHashKeyValues(userIdKey);
		List<Statistic> userRecords = getSimpleMapper().query(Statistic.class, queryExpression);
		return userRecords.size() > 0;
	}
	
	public void createUserStat(String userId) {
		Item userStat = new Item().withPrimaryKey("userId", userId);
		table.putItem(userStat);
	}
	
	@DynamoDBTable(tableName="user_stat")
	public static class Statistic {
		private String userId;
		private String tag;
		private int count;
		
		@DynamoDBHashKey(attributeName="userId")
		public String getUserId() { return userId; }
		public void setUserId(String userId) { this.userId = userId; }
		
		@DynamoDBAttribute(attributeName="tag")
		public String getTag() { return tag; }
		public void setTag(String tag) { this.tag = tag; }
		
		@DynamoDBAttribute(attributeName="count")
		public int getCount() { return count; }
		public void setCount(int count) { this.count = count; }
		
	}
	
	public static void main(String[] args) {
		List<String> tags = new ArrayList<String>();
		/*tags.add("coffee");
		tags.add("yelp");
		UserStatistic.getInstance().updateStat("qwww", tags);
		
		tags.add("coffee");
		tags.add("yelp");
		UserStatistic.getInstance().updateStat("fififi", tags);*/
		
		tags.add("borrow");
		tags.add("map");
		UserStatistic.getInstance().updateStat("ccc", tags);
		
		
		
		List<Statistic> results = UserStatistic.getInstance().getAllUserStat();
		for (Statistic record : results)
			System.out.println(record.getUserId() + "\t" + record.getTag() + "\t" + record.getCount());
	}
}
