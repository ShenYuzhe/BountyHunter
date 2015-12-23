package com.service.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bounty.utils.FileSystem;

/**
 * Servlet implementation class FileDownLoader
 */
public class FileDownLoader extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String userId = request.getParameter("userId");
        String fileName = request.getParameter("fileName");
        String fileType = request.getParameter("fileType");
        
        System.out.println(userId + "\t" + fileName + "\t" + fileType);

        response.setContentType("image/jpeg");
        response.setHeader("Content-disposition","attachment; filename=" + fileName);  

        FileSystem.getInstance().sendFile(userId, fileName, response.getOutputStream());
   }

}
