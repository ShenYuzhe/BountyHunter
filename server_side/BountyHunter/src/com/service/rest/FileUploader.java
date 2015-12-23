package com.service.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.ws.rs.core.Response.Status;

import org.json.JSONException;
import org.json.JSONObject;

import com.bounty.utils.FileSystem;

@MultipartConfig
public class FileUploader extends HttpServlet {
	private static final long serialVersionUID = 1L;
	

       
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = new PrintWriter(response.getWriter());
		JSONObject responseObject = new JSONObject();
		
		String fileName = request.getParameter("fileName");
		String userId = request.getParameter("userId");
		String fileType = FileSystem.getInstance().getFileType(fileName);
		if (fileType == null) {
			try {
				responseObject.put("responseCode", Status.BAD_REQUEST.getStatusCode());
				responseObject.put("message", "only support .jpg .jpeg .png");
			} catch (JSONException e) {}
			writer.write(responseObject.toString());
			writer.flush();
			return;
		}
		
		Part filePart = request.getPart("file");
		InputStream ifStream = filePart.getInputStream();
		FileSystem.getInstance().saveUserImage(userId, fileName, ifStream);
		
		String url = FileSystem.getInstance().getUserFilePath(userId, fileName);

		try {
			responseObject.put("url", url);
			responseObject.put("responseCode", Status.OK.getStatusCode());
			writer.write(responseObject.toString());
			writer.flush();
		} catch (JSONException e) {}
	}

}
