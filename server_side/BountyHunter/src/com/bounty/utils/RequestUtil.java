package com.bounty.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

public class RequestUtil {

	public RequestUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static String sendGet(String url, String param, RequestHeader header) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url;
			if (param != null)
				urlNameString = urlNameString + "?" + param;
			URL realUrl = new URL(urlNameString);
			URLConnection connection = realUrl.openConnection();
			
			header.setConnectionHeader(connection);
			
			connection.connect();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
				result += line;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
	
	public static String sendPost(String url, String param, RequestHeader header) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			
			URL realUrl = new URL(url);
			URLConnection connection = realUrl.openConnection();
			
			header.setConnectionHeader(connection);
			
			connection.setDoInput(true);
			connection.setDoOutput(true);
            out = new PrintWriter(connection.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
            	result += line;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
	
	
	public static void main(String[] args) {
		//String result = sendGet("https://api.twitter.com/1.1/trends/place.json", "id=1");
		//System.out.println(result);
	}
}
