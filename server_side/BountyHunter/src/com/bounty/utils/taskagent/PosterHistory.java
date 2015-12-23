package com.bounty.utils.taskagent;

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
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.bounty.utils.CredentialManager;

public class PosterHistory {
	private static PosterHistory posterStorage;
	
	
	
	private String tableName = "poster_history";
	
	private DynamoDB dynamoDB;
	
	private AmazonDynamoDBClient client;
	
	private Table table;
	
	private PosterHistory() {
		AWSCredentials credentials = CredentialManager.getInstance().getCredential();
		dynamoDB = new DynamoDB((AmazonDynamoDB) new AmazonDynamoDBClient(credentials));
		client = new AmazonDynamoDBClient(credentials);
		table = dynamoDB.getTable(tableName);
	}
	
	public static PosterHistory getInstance() {
		if (posterStorage == null)
			posterStorage = new PosterHistory();
		return posterStorage;
	}
	
	public void createRecord(String taskId, String hunterId, String hunterProfile,
			String taskContent, double posterPrice, double hunterPrice, String status) {
		String searchId = DynamoUtils.getSearchKey(taskId, hunterId);
		String posterId = DynamoUtils.getPosterIdFromTaskId(taskId);
		Item hunterRecord = new Item()
				.withPrimaryKey("posterId", posterId)
				.withString("searchId", searchId)
				.withString("taskId", taskId)
				.withString("taskContent", taskContent)
				.withString("hunterId", hunterId)
				.withString("hunterProfile", hunterProfile)
				.withDouble("posterPrice", posterPrice)
				.withDouble("hunterPrice", hunterPrice)
				.withString("status", status)
				.withString("modifiedAt", DynamoUtils.getCurrentTime());
		table.putItem(hunterRecord);
	}
	
	public void updateRecord(String taskId, String hunterId, Hunter hunter) {
		String posterId = DynamoUtils.getPosterIdFromTaskId(taskId);
		String searchId = DynamoUtils.getSearchKey(taskId, hunterId);
		Double posterPrice = hunter.getPosterPrice();
		Double hunterPrice = hunter.getHunterPrice();
		String status = hunter.getStatus();
		Map<String, String> expressionAttributeNames = new HashMap<String, String>();
		Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
		String atomicCondition = null;
		if (posterPrice != null) {
			atomicCondition = "SET #posterPrice = :posterPrice";
			expressionAttributeNames.put("#posterPrice", "posterPrice");
			expressionAttributeValues.put(":posterPrice", posterPrice);
		} else if (hunterPrice != null) {
			atomicCondition = "SET #hunterPrice = :hunterPrice";
			expressionAttributeNames.put("#hunterPrice", "hunterPrice");
			expressionAttributeValues.put(":hunterPrice", hunterPrice);
		} else if (status != null) {
			atomicCondition = "SET #status = :status";
			expressionAttributeNames.put("#status", "status");
			expressionAttributeValues.put(":status", status);
		}
		
		if (atomicCondition != null) {
			atomicCondition += ", #modifiedAt = :modifiedAt";
			expressionAttributeNames.put("#modifiedAt", "modifiedAt");
			expressionAttributeValues.put(":modifiedAt", DynamoUtils.getCurrentTime());
			UpdateItemOutcome outcome = table.updateItem(
					"posterId", posterId,
					"searchId", searchId,
					atomicCondition,
					expressionAttributeNames,
					expressionAttributeValues);
		}	
	}
	
	public List<Hunter> getAllHunterbyTaskId(String posterId, String taskId) {
		Hunter hunterKey = new Hunter();
		hunterKey.setPosterId(posterId);
		Condition rangeKeyCondition = new Condition()
	            .withComparisonOperator(ComparisonOperator.BEGINS_WITH)
	            .withAttributeValueList(new AttributeValue().withS(taskId));
		DynamoDBQueryExpression<Hunter> queryExpression = new DynamoDBQueryExpression<Hunter>()
				.withHashKeyValues(hunterKey)
				.withRangeKeyCondition("searchId", rangeKeyCondition);
	    List<Hunter> hunters = getSimpleMapper().query(Hunter.class, queryExpression);
	    return hunters;
	}
	
	public List<Hunter> getAllHunterById(String posterId) {
		Hunter hunterKey = new Hunter();
		hunterKey.setPosterId(posterId);
		DynamoDBQueryExpression<Hunter> queryExpression = new DynamoDBQueryExpression<Hunter>()
				.withHashKeyValues(hunterKey);
		List<Hunter> hunters = getSimpleMapper().query(Hunter.class, queryExpression);
		return hunters;
	}
	
	public void deleteHunter(String taskId, String hunterId) {
		String searchId = DynamoUtils.getSearchKey(taskId, hunterId);
		String posterId = DynamoUtils.getPosterIdFromTaskId(taskId);
		table.deleteItem("posterId", posterId, "searchId", searchId);
	}
	
	private DynamoDBMapper getSimpleMapper() {
		return new DynamoDBMapper(client);
	}
	
	@DynamoDBTable(tableName="poster_history")
	public static class Hunter {
		private String posterId;
		private String searchId;
		private String taskId;
		private String taskContent;
		private String hunterId;
		private String hunterProfile;
		private Double posterPrice;
		private Double hunterPrice;
		private String status;
		private String modifiedAt;
		
		@DynamoDBHashKey(attributeName="posterId")
		public String getPosterId() { return posterId; }
		public void setPosterId(String posterId) { this.posterId = posterId; }
		
		@DynamoDBRangeKey(attributeName="searchId")
		public String getSearchId() { return searchId; }
		public void setSearchId(String searchId) { this.searchId = searchId; }
		
		@DynamoDBAttribute(attributeName="taskId")
		public String getTaskId() { return taskId; }
		public void setTaskId(String taskId) { this.taskId = taskId; }
		
		@DynamoDBAttribute(attributeName="taskContent")
		public String getTaskContent() { return taskContent; }
		public void setTaskContent(String taskContent) { this.taskContent = taskContent; }
		
		@DynamoDBAttribute(attributeName="hunterId")
		public String getHunterId() { return hunterId; }
		public void setHunterId(String hunterId) { this.hunterId = hunterId; }
		
		@DynamoDBAttribute(attributeName="hunterProfile")
		public String getHunterProfile() { return hunterProfile; }
		public void setHunterProfile(String hunterProfile) { this.hunterProfile = hunterProfile; }
		
		@DynamoDBAttribute(attributeName="posterPrice")
		public Double getPosterPrice() { return posterPrice; }
		public void setPosterPrice(Double posterPrice) { this.posterPrice = posterPrice; }
		
		@DynamoDBAttribute(attributeName="hunterPrice")
		public Double getHunterPrice() { return hunterPrice; }
		public void setHunterPrice(Double hunterPrice) { this.hunterPrice = hunterPrice; }
		
		@DynamoDBAttribute(attributeName="status")
		public String getStatus() { return status; }
		public void setStatus(String status) { this.status = status; }
		
		@DynamoDBAttribute(attributeName="modifiedAt")
		public String getModifiedAt() { return modifiedAt; }
		public void setModifiedAt(String modifiedAt) { this.modifiedAt = modifiedAt; }
	}
	
	public static void main(String[] args) {
		//PosterHistory.getInstance().createRecord("202015-12-14 23:41:58", "cc", "dd", "dd", 9.0);
		//PosterHistory.getInstance().createRecord("202015-12-14 23:41:58", "dd", "dd", "dd", 9.0);
		//PosterHistory.getInstance().createRecord("202015-12-14 23:41:60", "dd", "dd", "dd", 9.0);
		Hunter hunter = new Hunter();
		hunter.setHunterPrice(8.8);
		
		PosterHistory.getInstance().updateRecord("202015-12-15 12:40:30", "50", hunter);
		
		/*List<Hunter> hunters = PosterHistory.getInstance().getAllHunterbyTaskId("20", "202015-12-14 23:41:58");
		for (Hunter hunter : hunters)
			System.out.println(hunter.hunterId);*/
	}
}
