package com.service.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

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
import com.bounty.utils.ServletUtils;
import com.service.rest.imp.ProfileRestImp;

@MultipartConfig
public class ProfileUpdater extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private ProfileRestImp profileRestImp = new ProfileRestImp();
	
    public ProfileUpdater() {
        super();
        // TODO Auto-generated constructor stub
    }


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter writer = new PrintWriter(response.getWriter());
		JSONObject responseObject = new JSONObject();
		
		String userId = request.getParameter("userId");
		String userName = request.getParameter("userName");
		String email = request.getParameter("email");
		String sex = request.getParameter("sex");
		String birthday = request.getParameter("birthday");
		String fileType = null;
		String url = null;
		
		String responseMsg;
		
		if (userId == null) {
			responseObject = ServletUtils.composeResponse(Status.BAD_REQUEST.getStatusCode(),
						"missing userid");
			ServletUtils.doResponse(responseObject.toString(), writer);
			return;
		}
		
		Part filePart = request.getPart("file");
		
		if (filePart != null) {
			String fileName = ServletUtils.getSubmittedFileName(filePart);
			// verify file type
			if (fileName != null) {
				fileType = FileSystem.getInstance().getFileType(fileName);
				if (fileType == null) {
					responseObject = ServletUtils.composeResponse(Status.BAD_REQUEST.getStatusCode(),
							"only support .jpg .jpeg .png");
					writer.write(responseObject.toString());
					writer.flush();
					return;
				}
			}
			InputStream ifStream = filePart.getInputStream();
			FileSystem.getInstance().saveUserImage(userId, fileName, ifStream);
			url = FileSystem.getInstance().getUserFilePath(userId, fileName);
			System.out.println(url);
		}

		JSONObject profileRequest = new JSONObject();
		try {
			if (userName != null && userName != "")
				profileRequest.put("userName", userName);
			if (url != null)
				profileRequest.put("iconUrl", url);
			if (sex != null && sex != "")
				profileRequest.put("sex", sex);
			if (email != null && email != "")
				profileRequest.put("email", email);
			if (birthday != null && birthday != "")
				profileRequest.put("birthday", birthday);
			profileRestImp.updateProfile(userId, profileRequest.toString());
			//profileRestImp.createProfile(userId, profileRequest.toString());
		} catch (JSONException e) {e.printStackTrace();}
		
		responseMsg = ServletUtils.composeResponse(Status.OK.getStatusCode(), "").toString();
		ServletUtils.doResponse(responseMsg, writer);
	}

}
