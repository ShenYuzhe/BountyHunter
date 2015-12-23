package com.bounty.utils.taskagent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.bounty.utils.taskagent.UserStatistic.Statistic;

public class TaskStorage {
	private static TaskStorage taskStorage;
	
	private String tableName = "task_storage";
	
	private DynamoDB dynamoDB;
	
	private AmazonDynamoDBClient client;
	
	private Table table;
	
	private TaskStorage() {
		AWSCredentials credentials = CredentialManager.getInstance().getCredential();
		dynamoDB = new DynamoDB((AmazonDynamoDB) new AmazonDynamoDBClient(credentials));
		client = new AmazonDynamoDBClient(credentials);
		table = dynamoDB.getTable(tableName);
	}
	
	public static TaskStorage getInstance() {
		if (taskStorage == null)
			taskStorage = new TaskStorage();
		return taskStorage;
	}
	
	public void insertTask(String taskId, JSONObject taskContent) {
		Item task = new Item();
		task.withPrimaryKey("taskId", taskId)
			.withString("taskContent", taskContent.toString())
			.withString("selectedHunter", "TBD")
			.withDouble("settlePrice", 0.0);
		table.putItem(task);
	}
	
	public Task getTaskById(String taskId) {
		Task taskKey = new Task();
		taskKey.setTaskId(taskId);
		DynamoDBQueryExpression<Task> queryExpression = new DynamoDBQueryExpression<Task>()
				.withHashKeyValues(taskKey);
		List<Task> tasks = getSimpleMapper().query(Task.class, queryExpression);
		Task task =  tasks.get(0);
		return task;
	}
	
	public List<Statistic> getAllUserStat() {
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		List<Statistic> scanResult = getSimpleMapper().scan(Statistic.class, scanExpression);
		return scanResult;
	}
	
	private DynamoDBMapper getSimpleMapper() {
		return new DynamoDBMapper(client);
	}
	
	public void updateTask(String taskId, Task task) {
		Double settlePrice = task.getSettlePrice();
		String selectedHunter = task.getSelectHunter();
		Map<String, String> expressionAttributeNames = new HashMap<String, String>();
		Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
		String atomicCondition = null;
		if (settlePrice != null) {
			atomicCondition = "SET #settlePrice = :settlePrice";
			expressionAttributeNames.put("#settlePrice", "settlePrice");
			expressionAttributeValues.put(":settlePrice", settlePrice);
		}
		if (selectedHunter != null) {
			atomicCondition = "SET #selectedHunter = :selectedHunter";
			expressionAttributeNames.put("#selectedHunter", "selectedHunter");
			expressionAttributeValues.put(":selectedHunter", selectedHunter);
		}
		if (atomicCondition != null) {
			UpdateItemOutcome outcome = table.updateItem(
					"taskId", taskId,
					atomicCondition,
					expressionAttributeNames,
					expressionAttributeValues);
		}	
	}
	
	@DynamoDBTable(tableName="task_storage")
	public static class Task {
		private String taskId;
		private String taskContent;
		private String selectedHunter;
		private Double settlePrice;
		
		@DynamoDBHashKey(attributeName="taskId")
		public String getTaskId() { return taskId; }
		public void setTaskId(String taskId) { this.taskId = taskId; }
		
		@DynamoDBAttribute(attributeName="taskContent")
		public String getTaskContent() { return taskContent; }
		public void setTaskContent(String taskContent) { this.taskContent = taskContent; }
		
		@DynamoDBAttribute(attributeName="selectedHunter")
		public String getSelectHunter() { return selectedHunter; }
		public void setSelectHunter(String selectedHunter) { this.selectedHunter = selectedHunter; }
		
		@DynamoDBAttribute(attributeName="settlePrice")
		public Double getSettlePrice() { return settlePrice; }
		public void setSettlePrice(Double settlePrice) { this.settlePrice = settlePrice; }
	}
	
	public static void main(String[] args) throws JSONException {
		Task task = TaskStorage.getInstance().getTaskById("202015-12-14 23:41:58");
	}
}
