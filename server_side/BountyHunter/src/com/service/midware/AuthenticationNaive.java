package com.service.midware;

import redis.clients.jedis.Jedis;

public class AuthenticationNaive {
	
private Jedis jedis;
	
	private static AuthenticationNaive authentication = null;
	
	private String endPoint = "bountyhunter.pqgjsr.0001.use1.cache.amazonaws.com";
	
	public static void main(String[] args) {
		
		Jedis jedis = new Jedis("localhost");
		System.out.println(jedis.ping());
	}
	
	private AuthenticationNaive() {
		jedis = new Jedis(endPoint);
	}
	
	public static AuthenticationNaive getInstance() {
		if (authentication == null)
			authentication = new AuthenticationNaive();
		return authentication;
	}
	
	public boolean register(String userId, String password) {
		if (userId == null || password == null)
			return false;
		if (jedis.exists(userId))
			return false;
		jedis.set(userId, password);
		return true;
	}
	
	public void deleteUser(String userId) {
		jedis.del(userId);
	}
	
	public boolean isValid(String userId, String password) {
		return password.equals(jedis.get(userId));
	}
}
