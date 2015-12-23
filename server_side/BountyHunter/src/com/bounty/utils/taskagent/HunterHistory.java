package com.bounty.utils.taskagent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.bounty.utils.CredentialManager;

public class HunterHistory {
	private static HunterHistory hunterHistory;
	
	private String tableName = "hunter_history";
	
	private DynamoDB dynamoDB;
	
	private AmazonDynamoDBClient client;
	
	private Table table;
	
	private HunterHistory() {
		AWSCredentials credentials = CredentialManager.getInstance().getCredential();
		dynamoDB = new DynamoDB((AmazonDynamoDB) new AmazonDynamoDBClient(credentials));
		client = new AmazonDynamoDBClient(credentials);
		table = dynamoDB.getTable(tableName);
	}
	
	public static HunterHistory getInstance() {
		if (hunterHistory == null)
			hunterHistory = new HunterHistory();
		return hunterHistory;
	}
	
	public void createRecord(String hunterId, String taskId,
			String posterProfile,
			String taskContent, double posterPrice, double hunterPrice) {
		String posterId = DynamoUtils.getPosterIdFromTaskId(taskId);
		Item hunterRecord = new Item()
				.withPrimaryKey("hunterId", hunterId)
				.withString("taskId", taskId)
				.withString("taskContent", taskContent)
				.withString("posterId", posterId)
				.withString("posterProfile", posterProfile)
				.withDouble("posterPrice", posterPrice)
				.withDouble("hunterPrice", hunterPrice)
				.withString("status", HistoryStatus.CANDIDATE.toString())
				.withString("modifiedAt", DynamoUtils.getCurrentTime());
		table.putItem(hunterRecord);
	}
	
	public void updateRecord(String taskId, String hunterId, TaskRecord taskRecord) {
		Double posterPrice = taskRecord.getPosterPrice();
		Double hunterPrice = taskRecord.getHunterPrice();
		String status = taskRecord.getStatus();
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
					"hunterId", hunterId,
					"taskId", taskId,
					atomicCondition,
					expressionAttributeNames,
					expressionAttributeValues);
		}
			
	}
	
	public List<TaskRecord> getAllTaskRecordById(String hunterId) {
		TaskRecord task_key = new TaskRecord();
		task_key.setHunterId(hunterId);
		DynamoDBQueryExpression<TaskRecord> queryExpression = new DynamoDBQueryExpression<TaskRecord>()
				.withHashKeyValues(task_key);
		List<TaskRecord> hunters = getSimpleMapper().query(TaskRecord.class, queryExpression);
		return hunters;
	}
	
	public void deleteTask(String taskId, String hunterId) {
		table.deleteItem("hunterId", hunterId, "taskId", taskId);
	}
	
	private DynamoDBMapper getSimpleMapper() {
		return new DynamoDBMapper(client);
	}
	
	@DynamoDBTable(tableName="hunter_history")
	public static class TaskRecord {
		private String hunterId;
		
		private String posterId;
		private String posterProfile;
		
		private String taskId;
		private String taskContent;
		
		private Double posterPrice;
		private Double hunterPrice;
		private String status;
		private String modifiedAt;
		
		
		@DynamoDBHashKey(attributeName="hunterId")
		public String getHunterId() { return hunterId; }
		public void setHunterId(String hunterId) { this.hunterId = hunterId; }
		
		@DynamoDBAttribute(attributeName="taskId")
		public String getTaskId() { return taskId; }
		public void setTaskId(String taskId) { this.taskId = taskId; }
		
		@DynamoDBAttribute(attributeName="taskContent")
		public String getTaskContent() { return taskContent; }
		public void setTaskContent(String taskContent) { this.taskContent = taskContent; }
		
		@DynamoDBAttribute(attributeName="posterId")
		public String getPosterId() { return posterId; }
		public void setPosterId(String posterId) { this.posterId = posterId; }
		
		@DynamoDBAttribute(attributeName="posterProfile")
		public String getPosterProfile() { return posterProfile; }
		public void setPosterProfile(String posterProfile) { this.posterProfile = posterProfile; }
		
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
	
	public static void main(String[] args) throws JSONException {
		//TaskRecord taskRecord = new TaskRecord();
		//taskRecord.setPrice(89.0);
		//HunterHistory.getInstance().deleteTask("202015-12-13 13:38:42", "99");
		
	}
}
