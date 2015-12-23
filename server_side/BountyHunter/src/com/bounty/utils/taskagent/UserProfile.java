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
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.bounty.utils.CredentialManager;

public class UserProfile {
	
private static UserProfile userProfile = null;
	
	private String tableName = "user_profile";
	
	private DynamoDB dynamoDB;
	
	private AmazonDynamoDBClient client;
	
	private Table table;
	
	public static void main(String[] args) throws JSONException {
		UserProfile.getInstance().updateGrade("18", 23);
	}
	
	public static UserProfile getInstance() {
		if (userProfile == null)
			userProfile = new UserProfile();
		return userProfile;
	}
	
	private UserProfile() {
		AWSCredentials credentials = CredentialManager.getInstance().getCredential();
		dynamoDB = new DynamoDB((AmazonDynamoDB) new AmazonDynamoDBClient(credentials));
		client = new AmazonDynamoDBClient(credentials);
		table = dynamoDB.getTable(tableName);
	}
	
	public void createProfile(String userId, Profile profile) {
		Item item = new Item();
		item.withPrimaryKey("userId", userId)
			.withString("userName", profile.userName)
			.withString("iconUrl", profile.iconUrl);
		if (profile.sex != null)
			item.withString("sex", profile.sex);
		if (profile.email != null)
			item.withString("email", profile.email);
		if (profile.birthday != null)
			item.withString("birthday", profile.birthday);
		table.putItem(item);
	}
	
	public void updateGrade(String userId, double increment) {
		Map<String, String> expressionAttributeNames = new HashMap<String, String>();
		Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
		String atomicCondition = null;
		atomicCondition = "ADD #grade :increment";
		expressionAttributeNames.put("#grade", "grade");
		expressionAttributeValues.put(":increment", increment);
		if (atomicCondition != null) {
			atomicCondition += " SET #modifiedAt = :modifiedAt";
			expressionAttributeNames.put("#modifiedAt", "modifiedAt");
			expressionAttributeValues.put(":modifiedAt", DynamoUtils.getCurrentTime());
			UpdateItemOutcome outcome = table.updateItem(
					"userId", userId,
					atomicCondition,
					expressionAttributeNames,
					expressionAttributeValues);
		}
	}
	
	public void updateProfile(String userId, JSONObject profileRequest) throws JSONException {
		Map<String, String> expressionAttributeNames = new HashMap<String, String>();
		Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
		String atomicCondition = null;
		//Item profile = new Item().withPrimaryKey("userId", userId);
		if (profileRequest.has("userName")) {
			atomicCondition = "SET #userName = :userName";
			expressionAttributeNames.put("#userName", "userName");
			expressionAttributeValues.put(":userName", profileRequest.getString("userName"));
		}
		if (profileRequest.has("iconUrl")) {
			if (atomicCondition == null)
				atomicCondition = "SET #iconUrl = :iconUrl";
			else
				atomicCondition += ", #iconUrl = :iconUrl";
			expressionAttributeNames.put("#iconUrl", "iconUrl");
			expressionAttributeValues.put(":iconUrl", profileRequest.getString("iconUrl"));
		}
		if (profileRequest.has("sex")) {
			if (atomicCondition == null)
				atomicCondition = "SET #sex = :sex";
			else
				atomicCondition += ", #sex = :sex";
			expressionAttributeNames.put("#sex", "sex");
			expressionAttributeValues.put(":sex", profileRequest.getString("sex"));
		}
		if (profileRequest.has("email")) {
			System.out.println("in email");;
			if (atomicCondition == null)
				atomicCondition = "SET #email = :email";
			else
				atomicCondition += ", #email = :email";
			expressionAttributeNames.put("#email", "email");
			expressionAttributeValues.put(":email", profileRequest.getString("email"));
		}
		if (profileRequest.has("birthday")) {
			if (atomicCondition == null)
				atomicCondition = "SET #birthday = :birthday";
			else
				atomicCondition += ", #birthday = :birthday";
			expressionAttributeNames.put("#birthday", "birthday");
			expressionAttributeValues.put(":birthday", profileRequest.getString("birthday"));
		}
		if (atomicCondition != null) {
			atomicCondition += ", #modifiedAt = :modifiedAt";
			expressionAttributeNames.put("#modifiedAt", "modifiedAt");
			expressionAttributeValues.put(":modifiedAt", DynamoUtils.getCurrentTime());
			UpdateItemOutcome outcome = table.updateItem(
					"userId", userId,
					atomicCondition,
					expressionAttributeNames,
					expressionAttributeValues);
		}
	}
	
	private DynamoDBMapper getSimpleMapper() {
		return new DynamoDBMapper(client);
	}
	
	public JSONObject getProfile(String userId) throws JSONException {
		Profile profile_key = new Profile();
		profile_key.setUserId(userId);
		
		DynamoDBQueryExpression<Profile> queryExpression = new DynamoDBQueryExpression<Profile>()
				.withHashKeyValues(profile_key);
		
		List<Profile> profiles = getSimpleMapper().query(Profile.class, queryExpression);
		if (profiles.size() == 0)
			return null;
		Profile profile = profiles.get(0);
		
		String userName = profile.getUserName();
		String iconUrl = profile.getIconUrl();
		String sex = profile.getSex();
		String email = profile.getEmail();
		String birthday = profile.getBirthday();
		Double grade = profile.getGrade();
		
		JSONObject profileObject = new JSONObject();
		profileObject.put("userName", userName);
		profileObject.put("iconUrl", iconUrl);
		if (sex != null)
			profileObject.put("sex", sex);
		else
			profileObject.put("sex", "");
		if (email != null)
			profileObject.put("email", email);
		else
			profileObject.put("email", "");
		if (birthday != null)
			profileObject.put("birthday", birthday);
		else
			profileObject.put("birthday", "");
		if (grade != null)
			profileObject.put("grade", grade);
		else
			profileObject.put("grade", 0.0);
		return profileObject;
	}
	
	@DynamoDBTable(tableName="user_profile")
	public static class Profile {
		private String userId;
		private String userName;
		private String iconUrl;
		private String sex;
		private String email;
		private String birthday;
		private Double grade;
		
		@DynamoDBHashKey(attributeName="userId")
		public String getUserId() { return userId; }
		public void setUserId(String userId) { this.userId = userId; }
		
		@DynamoDBAttribute(attributeName="userName")
		public String getUserName() { return userName; }
		public void setUserName(String userName) { this.userName = userName; }
		
		@DynamoDBAttribute(attributeName="iconUrl")
		public String getIconUrl() { return iconUrl; }
		public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
		
		@DynamoDBAttribute(attributeName="sex")
		public String getSex() { return sex; }
		public void setSex(String sex) { this.sex = sex; }
		
		@DynamoDBAttribute(attributeName="email")
		public String getEmail() { return email; }
		public void setEmail(String email) { this.email = email; }
		
		@DynamoDBAttribute(attributeName="birthday")
		public String getBirthday() { return birthday; }
		public void setBirthday(String birthday) { this.birthday = birthday; }
		
		@DynamoDBAttribute(attributeName="grade")
		public Double getGrade() { return grade; }
		public void setGrade(Double grade) { this.grade = grade; }
	}
}
