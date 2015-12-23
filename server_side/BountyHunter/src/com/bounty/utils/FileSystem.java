package com.bounty.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.HashMap;

public class FileSystem {
	
	private static FileSystem fileUtils = null;
	
	private final String rootDir;
	
	private String[][] extendList = {
			{"jpg", "image/jpeg"},
			{"jpeg", "image/jpeg"},
			{"png", "image/png"}
	};
	
	public String defaultName = "default.jpeg";
	
	private HashMap<String, String> extendMap = new HashMap<String, String>();
	
	private void loadExtendMap() {
		for (String[] pair : extendList)
			extendMap.put(pair[0], pair[1]);
	}
	
	private FileSystem() {
		rootDir = "/tmp/BountyHunter";
		checkAndCreateDir(rootDir);
		loadExtendMap();
	}
	
	public static FileSystem getInstance() {
		if (fileUtils == null)
			fileUtils = new FileSystem();
		return fileUtils;
	}
	
	private void inputStreamToFile(String filePath, InputStream ifStream) throws IOException {
		S3Manager.getInstance().uploadFile(filePath, ifStream, getFileType(filePath));
		/*OutputStream outputStream = new FileOutputStream(new File(filePath));
		int read = 0;
		byte[] bytes = new byte[1024];
		while ((read = ifStream.read(bytes)) != -1)
			outputStream.write(bytes, 0, read);*/
	}
	
	public void checkAndCreateDir(String path) {
		File folder = new File(path);
		if (folder.isDirectory())
			return;
		folder.mkdir();
	}
	
	public void saveUserImage(String userId, String fileName, InputStream ifStream) throws IOException {
		/*System.out.println(rootDir);
		String userPath = rootDir + "/" + userId;
		checkAndCreateDir(userPath);
		String savePath = userPath + "/" + fileName;
		System.out.println(savePath);*/
		String savePath = userId + "/" + fileName;
		inputStreamToFile(savePath, ifStream);
	}
	
	private void saveImage(String savePath, byte[] bytes) {
		try {
			FileOutputStream ofstream = new FileOutputStream(savePath);
			ofstream.write(bytes);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {}
	}
	
	public void createDefaultIcon(String userId) throws IOException {
		
		String defaultPath = rootDir + "/" + defaultName;
		FileInputStream ifStream = new FileInputStream(new File(defaultPath));
		saveUserImage(userId, "default.jpeg", ifStream);
	}
	
	public String getFileType(String fileName) {
		String[] tokens = fileName.split("\\.");
		if (tokens.length < 2)
			return null;
		return extendMap.get(tokens[tokens.length - 1]);
	}
	
	public void sendFile(String userId, String fileName, OutputStream out) throws IOException {
		String filePath = rootDir + "/" + userId + "/" + fileName;
		sendFile(filePath, out);
	}
	
	private void sendFile(String filePath, OutputStream out) throws IOException {
		FileInputStream in = new FileInputStream(filePath);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = in.read(buffer)) > 0)
           out.write(buffer, 0, length);
        in.close();
        out.flush();
	}
	
	public String getUserFilePath(String userId, String fileName) {
		return S3Manager.getInstance().getUserFileUrl(userId + "/" + fileName);
	}
	
	public String getDefaultPath(String userId) throws UnknownHostException {
		return S3Manager.getInstance().getDefaultUrl();//getUserFilePath(userId, defaultName);
	}
	
	public static void main(String[] args) throws UnknownHostException {
		System.out.println(Inet4Address.getLocalHost().getHostAddress());
		HashMap<String, String> a = new HashMap<String, String>();
		String b = "aa.jpg";
		String[] tokens = b.split("\\.");
		System.out.println(tokens[0]);
		System.out.println(a.get("ccc"));
	}
}
