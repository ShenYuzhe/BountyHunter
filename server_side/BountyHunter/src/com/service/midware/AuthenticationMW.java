package com.service.midware;

import com.bounty.utils.facebookRest;

import redis.clients.jedis.Jedis;

public class AuthenticationMW {
	
	private Jedis jedis;
	
	private static AuthenticationMW authentication = null;
	
	private String endPoint = "localhost";
	
	public static void main(String[] args) {
		
		Jedis jedis = new Jedis("localhost");
		System.out.println(jedis.ping());
	}
	
	private AuthenticationMW() {
		jedis = new Jedis(endPoint);
	}
	
	public static AuthenticationMW getInstance() {
		if (authentication == null)
			authentication = new AuthenticationMW();
		return authentication;
	}
	
	public boolean checkAndSetValidity(String userId, String accessToken) {
		if (accessToken.equals(jedis.get(userId)))
			return true;
		boolean isValid = facebookRest.verifyToken(userId, accessToken);
		if (isValid)
			jedis.set(userId, accessToken);
		return isValid;
	}
}
