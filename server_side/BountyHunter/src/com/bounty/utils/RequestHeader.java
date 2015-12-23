package com.bounty.utils;

import java.net.URLConnection;

public class RequestHeader {
	private String userAgent = null;
	private String authorization = null;
	private String contentType = null;
	private String contentLength = null;
	private String acceptEncoding = null;
	private String grantType = null;
	
	public void setConnectionHeader(URLConnection connection) {
		if (authorization != null) {
			System.out.println(authorization);
			connection.setRequestProperty("Authorization", authorization);
		}
		if (contentType != null) {
			System.out.println(contentType);
			connection.setRequestProperty("Content-Type", contentType);
		}
		if (contentLength != null) {
			System.out.println(contentLength);
			connection.setRequestProperty("Content-Length", acceptEncoding);
		}
		if (acceptEncoding != null) {
			System.out.println(acceptEncoding);
			connection.setRequestProperty("Accept-Encoding", acceptEncoding);
		}
		if (userAgent != null) {
			System.out.println(userAgent);
			connection.setRequestProperty("user-agent", userAgent);
		}
	}
	
	public RequestHeader setUserAgent(String _userAgent) {
		userAgent = _userAgent;
		return this;
	}
	
	public String getUserAgent() {
		return this.userAgent;
	} 
	
	public RequestHeader setAuthorization(String _authorization) {
		authorization = _authorization;
		return this;
	}
	
	public String getAuthorization() {
		return this.authorization;
	}
	
	public RequestHeader setContentType(String _contentType) {
		contentType = _contentType;
		return this;
	}
	
	public String getContentType() {
		return this.contentType;
	}
	
	public RequestHeader setContentLength(String _contentLength) {
		contentLength = _contentLength;
		return this;
	}
	
	public String getContentLength() {
		return this.contentLength;
	}
	
	public RequestHeader setAcceptEncoding(String _acceptEncoding) {
		acceptEncoding = _acceptEncoding;
		return this;
	}
	
	public String getAcceptEncodingh() {
		return this.acceptEncoding;
	}
	
	public RequestHeader setGrantType(String _grantType) {
		grantType = _grantType;
		return this;
	}
	
	public String getGrandType() {
		return this.grantType;
	}
}
